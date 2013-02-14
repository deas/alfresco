/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.site.servlet;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.surf.util.Base64;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * A CSRF Filter class for the web-tier checking that certain requests supply a secret token that is compared
 * to the token existing in the user's session to mitigate CSRF attacks. It is also possible to check the referer or
 * origin headers.
 *
 * The logic is configurable making it possible to: disable the filter, use 1 and same token per session, refresh the
 * token when certain urls are requested (i.e. on a new page visit, which is recommended) OR refresh the token on
 * every request made to the server (which is not recommended since multiple requests might span over each other making
 * some tokens stale and therefor get treated as a CSRF attack).
 *
 * It is recommended to run the filter with a filter-mapping that NOT includes client side resources since that
 * is pointless and unnecessarily would decrease the performance of the webapp (even though the filter still would work).
 *
 * @author Erik Winlof
 */
public class CSRFFilter implements Filter
{
    private static Log logger = LogFactory.getLog(CSRFFilter.class);
    
    private ServletContext servletContext = null;
    
    private Boolean enabled = true;
    private List<Rule> rules = null;

    /**
     * Parses the filter rule config.
     *
     * @param config The filter config
     * @throws ServletException if the rule filter config is invalid
     */
    @Override
    public void init(FilterConfig config) throws ServletException
    {
        servletContext = config.getServletContext();
        
        ApplicationContext context = getApplicationContext();
        
        ConfigService configService = (ConfigService)context.getBean("web.config");
        
        // Retrieve the remote configuration
        Config csrfConfig = (Config) configService.getConfig("CSRFPolicy");
        if (csrfConfig == null)
        {
            enabled = false;
            if (logger.isDebugEnabled())
                logger.debug("There is no 'CSRFPolicy' config, filter will allow all requests.");
        }
        else
        {
            ConfigElement filterConfig = csrfConfig.getConfigElement("filter");
            if (filterConfig == null)
            {
                enabled = false;
                if (logger.isDebugEnabled())
                    logger.debug("The 'CSRFPolicy' config had no filter, filter will allow all requests.");
            }
            else
            {
                List<ConfigElement> rulesConfigList = filterConfig.getChildren("rule");
                if (rulesConfigList == null || rulesConfigList.size() == 0)
                {
                    enabled = false;
                    if (logger.isDebugEnabled())
                        logger.debug("The 'CSRFPolicy' filter config was empty, filter will allow all requests.");
                }
                else
                {
                    rules = new LinkedList<Rule>();
                    for (ConfigElement ruleConfig : rulesConfigList)
                    {
                        rules.add(createRule(ruleConfig));
                    }
                }
            }
        }
    }

    /**
     * Creates a rule object based on the config.
     *
     * @param ruleConfig The rule config element
     * @return A rul eobject created form the config
     * @throws ServletException if the config is invalid
     */
    protected Rule createRule(final ConfigElement ruleConfig) throws ServletException
    {
        final Rule rule = new Rule();
        
        // Request
        ConfigElement requestConfig = ruleConfig.getChild("request");
        if (requestConfig != null)
        {
            // Method
            rule.setMethod(requestConfig.getChildValue("method"));
            
            // Path
            rule.setPath(requestConfig.getChildValue("path"));
            
            // Headers
            List<ConfigElement> headerConfigs = requestConfig.getChildren("header");
            if (headerConfigs != null && headerConfigs.size() > 0)
            {
                Map<String, String> headers = new HashMap<String, String>();
                String value;
                for (ConfigElement headerConfig : headerConfigs)
                {
                    value = headerConfig.getValue();
                    headers.put(headerConfig.getAttribute("name"), value);
                }
                rule.setHeaders(headers);
            }
            
            // Session
            ConfigElement sessionConfig = requestConfig.getChild("session");
            if (sessionConfig != null)
            {
                // Session attributes
                List<ConfigElement> attributeConfigs = sessionConfig.getChildren("attribute");
                if (attributeConfigs != null && attributeConfigs.size() > 0)
                {
                    Map<String, String> sessionAttributes = new HashMap<String, String>();
                    String value;
                    for (ConfigElement attributeConfig : attributeConfigs)
                    {
                        value = attributeConfig.getValue();
                        sessionAttributes.put(attributeConfig.getAttribute("name"), value);
                    }
                    rule.setSessionAttributes(sessionAttributes);
                }
            }
        }
        
        // Actions
        List<ConfigElement> actionConfigs = ruleConfig.getChildren("action");
        if (actionConfigs != null && actionConfigs.size() > 0)
        {
            List<Action> actions = new LinkedList<Action>();
            String actionName;
            Action action;
            Map<String, String> parameters;
            List<ConfigElement> actionParameterConfigs;
            for (ConfigElement actionConfig : actionConfigs)
            {
                actionName = actionConfig.getAttribute("name");
                action = createAction(actionName);
                if (action == null)
                {
                    String message = "There is no action named '" + actionName + "'";
                    if (logger.isErrorEnabled())
                        logger.error(message);
                    throw new ServletException(message);
                }
                action.setName(actionName);
                parameters = new HashMap<String, String>();
                
                // Action parameters
                actionParameterConfigs = actionConfig.getChildren("param");
                if (actionParameterConfigs != null)
                {
                    for (ConfigElement actionParameterConfig : actionParameterConfigs)
                    {
                        parameters.put(actionParameterConfig.getAttribute("name"), actionParameterConfig.getValue());
                    }
                }
                action.init(parameters);
                actions.add(action);
            }
            rule.setActions(actions);
        }
        return rule;
    }

    /**
     * Creates a rule action based on a name
     *
     * @param name The name of the action, can be "generateToken", "assertToken" and "clearToken"
     * @return An action object
     * @throws ServletException if there is no action for name
     */
    protected Action createAction(String name) throws ServletException
    {
        if (name.equals("generateToken"))
        {
            return new GenerateTokenAction();
        }
        else if (name.equals("assertToken"))
        {
            return new AssertTokenAction();
        }
        else if (name.equals("clearToken"))
        {
            return new ClearTokenAction();
        }
        else if (name.equals("assertReferer"))
        {
            return new AssertRefererAction();
        }
        else if (name.equals("assertOrigin"))
        {
            return new AssertOriginAction();
        }
        return null;
    }

    /**
     * Will check the requests method, path, request headers & the session's attributes against the rule config
     * to see which rule actions that should be used, will either generate a new token, assert that the request's token
     * equals the session's token, remove the token fmor the cookie and session OR simply do nothing.
     *
     * @param servletRequest The servlet request
     * @param servletResponse The servlet response
     * @param filterChain The filter chain
     * @throws IOException
     * @throws ServletException if the request requires a CSRF token but there is no such token in the request matching
     * the token in the user's session.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException
    {
        if (enabled && servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse)
        {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            HttpSession session = request.getSession(false);
            
            for (Rule rule : rules)
            {
                if (matchRequest(rule, request, session))
                {
                    List<Action> actions = rule.getActions();
                    if (actions != null)
                    {
                        for (Action action : actions)
                        {
                            action.run(request, response, session);
                        }
                    }
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
            }
        }
        
        // Proceed as usual
        filterChain.doFilter(servletRequest, servletResponse);
    }
    
    @Override
    public void destroy()
    {
    }

    /**
     * Compare the requets against the configured rules.
     *
     * @param rule The rule to match against the request and session
     * @param request The http request
     * @param session The user's session
     * @return The first rule that matches the request and sessions or null if there is no such rule.
     * @throws ServletException
     */
    protected boolean matchRequest(Rule rule, HttpServletRequest request, HttpSession session) throws ServletException
    {
        // Match method
        if (rule.getMethod() != null && !matchString(request.getMethod(), rule.getMethod()))
        {
            return false;
        }
        
        // Match path
        if (rule.getPath() != null && !matchString(request.getRequestURI(). substring(request.getContextPath().length()), rule.getPath()))
        {
            return false;
        }
        
        // Match headers (if specified)
        Map<String, String> headers = rule.getHeaders();
        if (headers != null)
        {
            for (final String headerName: headers.keySet())
            {
                if (!matchString(request.getHeader(headerName), headers.get(headerName)))
                {
                    return false;
                }
            }
        }
        
        // Match session attributes (if specified)
        boolean matched = true;
        Map<String, String> sessionAttributes = rule.getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.size() != 0)
        {
            if (session == null)
            {
                matched = false;
            }
            else
            {
                for (final String name: sessionAttributes.keySet())
                {
                    final Object value = session.getAttribute(name);
                    if (value != null && !(value instanceof String))
                    {
                        // We can't match a non string and non null value with a string, so return false
                        matched = false;
                        break;
                    }
                    if (!matchString((String) value, sessionAttributes.get(name)))
                    {
                        matched = false;
                        break;
                    }
                }
            }
        }
        return matched;
    }

    /**
     * Checks if str matches the regular expression defined in regexp.
     *
     * @param str The value to match
     * @param regexp The regular expression to match against str
     * @return true if str matches regexp
     */
    protected boolean matchString(String str, String regexp)
    {
        if (regexp == null && str == null)
        {
            return true;
        }
        
        if ((regexp != null && str == null) || (regexp == null && str != null))
        {
            return false;
        }
        
        // There was a condition and a value, lets see if they match
        return str.matches(regexp);
    }

    /**
     * Retrieves the root application context
     *
     * @return application context
     */
    private ApplicationContext getApplicationContext()
    {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }

    /**
     * Internal representation of a rule.
     */
    private class Rule
    {
        protected String method;
        protected String path;
        protected Map<String, String> headers;
        protected Map<String, String> sessionAttributes;
        protected List<Action> actions;
        
        public String getMethod()
        {
            return method;
        }
        
        public void setMethod(String method)
        {
            this.method = method;
        }
        
        public String getPath()
        {
            return path;
        }
        
        public void setPath(String path)
        {
            this.path = path;
        }
        
        public Map<String, String> getHeaders()
        {
            return headers;
        }
        
        public void setHeaders(Map<String, String> headers)
        {
            this.headers = headers;
        }
        
        public Map<String, String> getSessionAttributes()
        {
            return sessionAttributes;
        }
        
        public void setSessionAttributes(Map<String, String> sessionAttributes)
        {
            this.sessionAttributes = sessionAttributes;
        }
        
        public List<Action> getActions()
        {
            return actions;
        }
        
        public void setActions(List<Action> actions)
        {
            this.actions = actions;
        }
    }

    /**
     * Returns the current server's scheme, name & port
     *
     * @param request The http request
     * @return the current server's scheme, name & port
     */
    private String getServerString(HttpServletRequest request)
    {
        String currentServerContext = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80)
        {
            currentServerContext += ":" + request.getServerPort();
        }
        return currentServerContext;
    }


    /**
     * Abstract base class representing a rule action.
     */
    private abstract class Action
    {
        protected String name;
        protected Map<String, String> params = new HashMap<String, String>();

        public void setName(String name)
        {
            this.name = name;
        }
        public void init(Map<String, String> params) throws ServletException
        {
            this.params = params;
        }
        
        public abstract void run(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException;
    }

    /**
     * Action that will generate a token in the session adn set it in the cookie.
     */
    private class GenerateTokenAction extends Action
    {
        public static final String PARAM_SESSION = "session";
        public static final String PARAM_COOKIE = "cookie";
        private final SecureRandom random = new SecureRandom();

        /**
         * Requires the following params; the name of the cookie to set the token in and the name of the session
         * attribute to place the token in. Defined in params with key "cookie" and "session".
         *
         * @param params Action paremeters
         * @throws ServletException
         */
        public void init(Map<String, String> params) throws ServletException
        {
            super.init(params);
            
            // Check for mandatory parameters
            if (params == null || params.size() == 0)
            {
                String message = "Parameter '" + PARAM_SESSION + "' or '" + PARAM_COOKIE + "' must be defined.";
                if (logger.isErrorEnabled())
                    logger.error(message);
                throw new ServletException(message);
            }
        }
        
        public void run(HttpServletRequest request, HttpServletResponse response, HttpSession session)
        {
            final byte[] bytes = new byte[32];
            random.nextBytes(bytes);
            final String newToken = Base64.encodeBytes(bytes);
            
            if (logger.isDebugEnabled())
                logger.debug("Generate token " + request.getMethod() + " " + request.getRequestURI() + " :: '" + newToken + "'");
            
            // Set in session
            if (params.get(PARAM_SESSION) != null && session != null)
            {
                session.setAttribute(params.get(PARAM_SESSION), newToken);
            }
            
            // Set in cookie
            if (params.get(PARAM_COOKIE) != null)
            {
                // Expose token as a cookie to the client
                int TIMEOUT = 60*60*24*7;
                Cookie userCookie = new Cookie(params.get(PARAM_COOKIE), URLEncoder.encode(newToken));
                userCookie.setPath(request.getContextPath());
                userCookie.setMaxAge(TIMEOUT);
                response.addCookie(userCookie);
            }
        }
    }

    /**
     * An action that asserts the request contains the token (either in the requets header or as a url parameter) and
     * that the token has the same value as the token in the user's session.
     */
    private class AssertTokenAction extends Action
    {
        public static final String PARAM_SESSION = "session";
        public static final String PARAM_HEADER = "header";
        public static final String PARAM_PARAMETER = "parameter";

        /**
         * Requires the following params; the name of the request header to look for the token in, the name of the url
         * parameter to look for the token and the name of the session attribute that holds the user's session.
         * Defined in params with key "header", "parameter" and "session".
         *
         * @param params The action parameters
         * @throws ServletException
         */
        public void init(Map<String, String> params) throws ServletException
        {
            super.init(params);

            // Check for mandatory parameters
            if (params == null || !params.containsKey(PARAM_SESSION))
            {
                String message = "Parameter '" + PARAM_SESSION + "' must be defined.";
                if (logger.isErrorEnabled())
                    logger.error(message);
                throw new ServletException(message);
            }
            if (!params.containsKey(PARAM_HEADER) && !params.containsKey(PARAM_PARAMETER))
            {
                String message = "Parameter '" + PARAM_HEADER + "' or '" + PARAM_PARAMETER + "' must be defined.";
                if (logger.isErrorEnabled())
                    logger.error(message);
                throw new ServletException(message);
            }
        }
        
        public void run(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException
        {
            String sessionToken = null;
            if (session != null)
            {
                sessionToken = (String) session.getAttribute(params.get(PARAM_SESSION));
            }
            if (params.containsKey(PARAM_HEADER))
            {
                String headerToken = request.getHeader(params.get(PARAM_HEADER));
                
                if (logger.isDebugEnabled())
                    logger.debug("Assert token " + request.getMethod() + " " + request.getRequestURI() + " :: session: '"
                            + sessionToken + "' vs header: '" + headerToken + "'");
                
                if (headerToken == null || sessionToken == null || !headerToken.equals(sessionToken))
                {
                    String message = "Possible CSRF attack noted when comparing token in session and request header. Request: "
                            + request.getMethod() + " " + request.getRequestURI();
                    if (logger.isInfoEnabled())
                        logger.info(message);

                    throw new ServletException(message);
                }
            }
            else if (params.containsKey(PARAM_PARAMETER))
            {
                String parameterToken = request.getParameter(params.get(PARAM_PARAMETER));
                
                if (logger.isDebugEnabled())
                    logger.debug("Assert token " + request.getMethod() + " " + request.getRequestURI() + " :: session: '"
                            + sessionToken + "' vs parameter: '" + parameterToken + "'");
                
                if (parameterToken == null || sessionToken == null || !parameterToken.equals(sessionToken))
                {
                    String message = "Possible CSRF attack noted when comparing token in session and request parameter. Request: "
                            + request.getMethod() + " " + request.getRequestURI();
                    if (logger.isInfoEnabled())
                        logger.info(message);

                    throw new ServletException(message);
                }
            }
        }
    }

    /**
     * Action that clears the token from the user's session and the also clears the token set in the cookie from the browser.
     */
    private class ClearTokenAction extends Action
    {
        public static final String PARAM_SESSION = "session";
        public static final String PARAM_COOKIE = "cookie";

        /**
         * Requires the following params; the name of the cookie to clear the value of and the name of the session
         * attribute to clear the value of . Defined in params with key "cookie" and "session".
         *
         * @param params
         * @throws ServletException
         */
        public void init(Map<String, String> params) throws ServletException
        {
            super.init(params);

            // Check for mandatory parameters
            if (params == null || params.size() == 0)
            {
                String message = "Parameter '" + PARAM_SESSION + "' or '" + PARAM_COOKIE + "' must be defined.";
                if (logger.isErrorEnabled())
                    logger.error(message);
                throw new ServletException(message);
            }
        }
        
        public void run(HttpServletRequest request, HttpServletResponse response, HttpSession session)
        {
            if (logger.isDebugEnabled())
                logger.debug("Clear token " + request.getMethod() + " " + request.getRequestURI());
            
            // Remove token from session
            if (params.get(PARAM_SESSION) != null && session != null)
            {
                session.setAttribute(params.get(PARAM_SESSION), null);
            }
            
            // Clear token cookie
            if (params.get(PARAM_COOKIE) != null)
            {
                // Expose token as a cookie to the client
                Cookie userCookie = new Cookie(params.get(PARAM_COOKIE), "");
                userCookie.setPath(request.getContextPath());
                userCookie.setMaxAge(0);
                response.addCookie(userCookie);
            }
        }
    }

    /**
     * An action that asserts the request's 'Referer' header starts with the current server name or the "referer" param.
     *
     * Note that the word “referrer” is misspelled in the RFC as well as in most implementations.
     */
    private class AssertRefererAction extends Action
    {
        public static final String PARAM_ALWAYS = "always";
        public static final String PARAM_REFERER = "referer";
        public static final String HEADER_REFERER = "Referer";

        /**
         * Requires the following params; a boolean deciding if the referer header MUST be present when validated.
         * Defined in a param with key "always".
         *
         * @param params The action parameters
         * @throws ServletException
         */
        public void init(Map<String, String> params) throws ServletException
        {
            super.init(params);

            // Check for mandatory parameters
            if (params == null || !params.containsKey(PARAM_ALWAYS))
            {
                String message = "Parameter '" + PARAM_ALWAYS + "' must be defined.";
                if (logger.isErrorEnabled())
                    logger.error(message);
                throw new ServletException(message);
            }
            if (!params.get(PARAM_ALWAYS).equals("true") && !params.get(PARAM_ALWAYS).equals("false"))
            {
                String message = "Parameter '" + PARAM_ALWAYS + "' must be a boolean and be set to true or false.";
                if (logger.isErrorEnabled())
                    logger.error(message);
                throw new ServletException(message);
            }
        }

        public void run(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException
        {
            String refererHeader = request.getHeader(HEADER_REFERER);
            if (refererHeader == null)
            {
                refererHeader = "";
            }
            else if (!refererHeader.endsWith("/"))
            {
                refererHeader += "/";
            }

            String currentServer = params.containsKey(PARAM_REFERER) ? params.get(PARAM_REFERER) : getServerString(request);

            if (logger.isDebugEnabled())
                logger.debug("Assert referer " + request.getMethod() + " " + request.getRequestURI() + " :: referer: '"
                        + request.getHeader(HEADER_REFERER) + "' vs server & context: '" + currentServer + "'");

            // Note! Add slashes at the end to avoid missing when the victim's domain is "site.com"
            // and the attacker site "site.com.attacker.com"
            if (!currentServer.endsWith("/"))
            {
                currentServer += "/";
            }

            if (refererHeader.isEmpty() && params.get(PARAM_ALWAYS).equals("false"))
            {
                // The referrer header might be blank or no existing due to a variety of "valid" reasons, i.e:
                // * If a website is accessed from a HTTP Secure (HTTPS) connection and a link points to anywhere except
                //   another secure location, then the referrer field is not sent.
                // * A proxy or other system might have blanked the header due to privacy concerns sending the entire
                //   url including the full path.
                // * The user agent might have been instructed to not send the referrer header using "noreferrer".
            }
            else
            {
                if (!refererHeader.startsWith(currentServer))
                {
                    String message = "Possible CSRF attack noted when asserting referer header '"
                            + request.getHeader(HEADER_REFERER) + "'. Request: " + request.getMethod() + " "
                            + request.getRequestURI();
                    if (logger.isInfoEnabled())
                        logger.info(message);

                    throw new ServletException(message);
                }
            }
        }
    }


    /**
     * An action that asserts the request's 'Origin' header matches the current server name or the "origin" param .
     */
    private class AssertOriginAction extends Action
    {
        public static final String PARAM_ALWAYS = "always";
        public static final String PARAM_ORIGIN = "origin";
        public static final String HEADER_ORIGIN = "Origin";

        /**
         * Requires the following params; a boolean deciding if the origin header MUST be present when validated.
         * Defined in a param with key "always".
         *
         * @param params The action parameters
         * @throws ServletException
         */
        public void init(Map<String, String> params) throws ServletException
        {
            super.init(params);

            // Check for mandatory parameters
            if (params == null || !params.containsKey(PARAM_ALWAYS))
            {
                String message = "Parameter '" + PARAM_ALWAYS + "' must be defined.";
                if (logger.isErrorEnabled())
                    logger.error(message);
                throw new ServletException(message);
            }
            if (!params.get(PARAM_ALWAYS).equals("true") && !params.get(PARAM_ALWAYS).equals("false"))
            {
                String message = "Parameter '" + PARAM_ALWAYS + "' must be a boolean and be set to true or false.";
                if (logger.isErrorEnabled())
                    logger.error(message);
                throw new ServletException(message);
            }
        }

        public void run(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException
        {
            String originHeader = request.getHeader(HEADER_ORIGIN);
            if (originHeader == null)
            {
                originHeader = "";
            }

            String currentServer = params.containsKey(PARAM_ORIGIN) ? params.get(PARAM_ORIGIN) : getServerString(request);

            if (logger.isDebugEnabled())
                logger.debug("Assert origin " + request.getMethod() + " " + request.getRequestURI() + " :: origin: '" + request.getHeader(HEADER_ORIGIN) + "' vs server: '" + currentServer + "'");

            if (originHeader.isEmpty() && params.get(PARAM_ALWAYS).equals("false"))
            {
                // Only valid reason for the Origin header not being sent should be due to an old browser NOT supporting it.
            }
            else
            {
                if (!originHeader.startsWith(currentServer))
                {
                    String message = "Possible CSRF attack noted when asserting origin header '" + request.getHeader(HEADER_ORIGIN) + "'. Request: " + request.getMethod() + " " + request.getRequestURI();
                    if (logger.isInfoEnabled())
                        logger.info(message);

                    throw new ServletException(message);
                }
            }
        }
    }
}
