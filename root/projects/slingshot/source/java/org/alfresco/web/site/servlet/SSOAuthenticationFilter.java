/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.sasl.RealmCallback;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.jlan.server.auth.kerberos.KerberosDetails;
import org.alfresco.jlan.server.auth.ntlm.NTLM;
import org.alfresco.jlan.server.auth.ntlm.NTLMLogonDetails;
import org.alfresco.jlan.server.auth.ntlm.NTLMMessage;
import org.alfresco.jlan.server.auth.ntlm.Type1NTLMMessage;
import org.alfresco.jlan.server.auth.ntlm.Type2NTLMMessage;
import org.alfresco.jlan.server.auth.ntlm.Type3NTLMMessage;
import org.alfresco.jlan.server.auth.spnego.NegTokenInit;
import org.alfresco.jlan.server.auth.spnego.NegTokenTarg;
import org.alfresco.jlan.server.auth.spnego.OID;
import org.alfresco.jlan.server.auth.spnego.SPNEGO;
import org.alfresco.util.Pair;
import org.alfresco.util.log.NDC;
import org.alfresco.web.site.servlet.config.KerberosConfigElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.util.Base64;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.web.context.support.WebApplicationContextUtils;

import static org.alfresco.web.site.SlingshotPageView.REDIRECT_URI;
import static org.alfresco.web.site.SlingshotPageView.REDIRECT_QUERY;

/**
 * SSO Authentication Filter Class for web-tier, supporting NTLM and Kerberos challenges from the repository tier.
 * Thanks to Sylvain Chambon for contributing the Kerberos delegation code.
 * 
 * @author Kevin Roast
 * @author gkspencer
 * @author Sylvain Chambon
 * @author dward
 */
public class SSOAuthenticationFilter implements Filter, CallbackHandler
{
    private static Log logger = LogFactory.getLog(SSOAuthenticationFilter.class);
    
    // Authentication request/response headers
    private static final String AUTH_NTLM = "NTLM";
    private static final String AUTH_SPNEGO = "Negotiate";
    private static final String HEADER_WWWAUTHENTICATE = "WWW-Authenticate";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    
    // NTLM authentication session object names
    private static final String NTLM_AUTH_DETAILS = "_alfwfNTLMDetails";
    
    private static final String MIME_HTML_TEXT = "text/html";

    private static final String PAGE_SERVLET_PATH = "/page";
    private static final String LOGIN_PATH_INFORMATION = "/dologin";
    private static final String LOGIN_PARAMETER = "login";
    private static final String IGNORE_LINK = "/accept-invite";
    private static final String UNAUTHENTICATED_ACCESS_PROXY = "/proxy/alfresco-noauth";
    
    private ConnectorService connectorService;
    private String endpoint;
    private ServletContext servletContext;
    private String userHeader;
    private SlingshotLoginController loginController;
    
    // Kerberos settings
    //
    // Account name and password for server ticket
    //
    // The account name *must* be built from the HTTP server name, in the format :
    //
    //      HTTP/<server_name>@<realm>
    //
    // (NB this is because the web browser requests an ST for the
    // HTTP/<server_name> principal in the current realm, so if we're to decode
    // that ST, it has to match.)
    
    private String krbAccountName;
    private String krbPassword;
    
    // Kerberos realm and KDC address
    
    private String krbRealm;
    
    // Service Principal Name to use on the endpoint
    // This must be like: HTTP/host.name@REALM
    
    private String krbEndpointSPN;
    
    // Login configuration entry name
    private String jaasLoginEntryName; 

    // Server login context
    private LoginContext jaasLoginContext;
    
    // A Boolean which when true strips the @domain sufix from Kerberos authenticated usernames. Default is <tt>true</tt>.
    private boolean stripUserNameSuffix;

    /**
     * Initialize the filter
     */
    public void init(FilterConfig args) throws ServletException
    {
        if (logger.isDebugEnabled())
            logger.debug("Initializing the SSOAuthenticationFilter.");
        // get reference to our ServletContext
        this.servletContext = args.getServletContext();
        
        ApplicationContext context = getApplicationContext();

        this.loginController = (SlingshotLoginController)context.getBean("loginController");
        
        // retrieve the connector service
        this.connectorService = (ConnectorService)context.getBean("connector.service");

        ConfigService configService = (ConfigService)context.getBean("web.config");
        
        // Retrieve the remote configuration
        RemoteConfigElement remoteConfig = (RemoteConfigElement) configService.getConfig("Remote").getConfigElement(
                "remote");
        if (remoteConfig == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("There is no remote configuration.");
            return;
        }

        // get the endpoint id to use
        String endpoint = args.getInitParameter("endpoint");
        if (endpoint == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("There is no endpoint id in the configuration.");
            return;
        }
        
        // Get the endpoint descriptor and check if external auth is enabled
        EndpointDescriptor endpointDescriptor = remoteConfig.getEndpointDescriptor(endpoint);
        if (endpointDescriptor == null || !endpointDescriptor.getExternalAuth())
        {
            if (logger.isDebugEnabled())
                logger.debug("Could not get endpoint descriptor for " + endpoint);
            return;
        }

        // Save the endpoint, activating the filter
        this.endpoint = endpoint;
        if (logger.isDebugEnabled())
            logger.debug("Endpoint is " + endpoint);
        
        // Obtain the userHeader (if configured) from the alfresco connector
        try
        {
            userHeader = connectorService.getConnector(endpoint).getConnectorSession().getParameter(SlingshotAlfrescoConnector.CS_PARAM_USER_HEADER);
            if (logger.isDebugEnabled())
                logger.debug("userHeader is " + userHeader);
        }
        catch (ConnectorServiceException e)
        {
            logger.error("Expected to find a connector for the endpoint "+endpoint, e);
        }

        // retrieve the optional kerberos configuration
        KerberosConfigElement krbConfig = (KerberosConfigElement) configService.getConfig("Kerberos").getConfigElement(
                "kerberos");
        if (krbConfig != null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Found configuration for Kerberos authentication.");
            // Get the Kerberos realm
            
            String krbRealm = krbConfig.getRealm();
            if ( krbRealm != null && krbRealm.length() > 0)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Found Kerberos realm: " + krbRealm);
                // Set the Kerberos realm
                
                this.krbRealm = krbRealm;
            }
            else
                throw new ServletException("Kerberos realm not specified");
            
            // Get the HTTP service account password
            
            String srvPassword = krbConfig.getPassword();
            if ( srvPassword != null && srvPassword.length() > 0)
            {
                // Set the HTTP service account password
                
                this.krbPassword = srvPassword;
            }
            else
                throw new ServletException("HTTP service account password not specified");
            
    
            String krbEndpointSPN = krbConfig.getEndpointSPN();
            if ( krbEndpointSPN != null && krbEndpointSPN.length() > 0)
            {
                // Set the Service Principal Name to use on the endpoint
                if (logger.isDebugEnabled())
                    logger.debug("The Service Principal Name to use on the endpoint: " + krbEndpointSPN);
                this.krbEndpointSPN = krbEndpointSPN;
            }
            else
                throw new ServletException("endpoint service principal name not specified");
            
            // Get the login configuration entry name
            
            String loginEntry = krbConfig.getLoginEntryName();
            
            if ( loginEntry != null)
            {
                if ( loginEntry.length() > 0)
                {
                    // Set the login configuration entry name to use
                    if (logger.isDebugEnabled())
                        logger.debug("The login configuration entry name to use: " + loginEntry);
                    jaasLoginEntryName = loginEntry;
                }
                else
                    throw new ServletException("Invalid login entry specified");
            }
            
            // Get the login stripUserNameSuffix property

            boolean stripUserNameSuffix = krbConfig.getStripUserNameSuffix();

            // Set the login configuration entry name to use
            if (logger.isDebugEnabled())
                logger.debug("The stripUserNameSuffix property is set to: " + stripUserNameSuffix);
            this.stripUserNameSuffix = stripUserNameSuffix;

            // Create a login context for the HTTP server service
            
            try
            {
                // Login the HTTP server service
                
                jaasLoginContext = new LoginContext( jaasLoginEntryName, this);
                jaasLoginContext.login();
                
                // DEBUG
                
                if ( logger.isDebugEnabled())
                    logger.debug( "HTTP Kerberos login successful");
            }
            catch ( LoginException ex)
            {
                // Debug
                
                if ( logger.isErrorEnabled())
                    logger.error("HTTP Kerberos web filter error", ex);
                
                throw new ServletException("Failed to login HTTP server service");
            }
            
            // Get the HTTP service account name from the subject
            
            Subject subj = jaasLoginContext.getSubject();
            Principal princ = subj.getPrincipals().iterator().next();
            
            krbAccountName = princ.getName();
            
            // DEBUG
            
            if ( logger.isDebugEnabled())
                logger.debug("Logged on using principal " + krbAccountName);        
        }

        if (logger.isInfoEnabled())
            logger.info("SSOAuthenticationFilter initialised.");
    }
    
    /**
     * Wraps an {@link HttpServletRequest} if an HTTP header has been configured for
     * use by an external SSO system to provide the name of an authenticated user.
     * The wrapper's {@link #getRemoteUser} returns the value of the header but will
     * defaults to the wrapped method's value if the header is not set.
     * @param sreq original {@code ServletRequest}
     * @return either the original {@code sreq} or a wrapped {@code HttpServletRequest}
     */
    private ServletRequest wrapHeaderAuthenticatedRequest(ServletRequest sreq)
    {
        if (userHeader != null &&
            sreq instanceof HttpServletRequest)
        {
            final HttpServletRequest req = (HttpServletRequest)sreq;
            sreq = new HttpServletRequestWrapper(req)
            {
                @Override
                public String getRemoteUser()
                {
                    String remoteUser = req.getHeader(userHeader);
                    if (remoteUser == null)
                    {
                        remoteUser = super.getRemoteUser();
                    }
                    return remoteUser;
                }
            };
        }
        return sreq;
    }

    /**
     * Run the filter
     * 
     * @param sreq ServletRequest
     * @param sresp ServletResponse
     * @param chain FilterChain
     * 
     * @exception IOException
     * @exception ServletException
     */
    public void doFilter(ServletRequest sreq, ServletResponse sresp, FilterChain chain)
        throws IOException, ServletException
    {
        NDC.remove();
        NDC.push(Thread.currentThread().getName());
        final boolean debug = logger.isDebugEnabled();
        
        // Wrap externally authenticated requests that provide the user in an HTTP header
        // with one that returns the correct name from getRemoteUser(). For use in our own
        // calls to this method and any chained filters.
        sreq = wrapHeaderAuthenticatedRequest(sreq);
        
        // Bypass the filter if we don't have an endpoint with external auth enabled
        if (this.endpoint == null)
        {
            if (debug)
                logger.debug("There is no endpoint with external auth enabled.");
            chain.doFilter(sreq, sresp);
            return;
        }

        // Get the HTTP request/response/session
        HttpServletRequest req = (HttpServletRequest)sreq;
        HttpServletResponse res = (HttpServletResponse)sresp;
        HttpSession session = req.getSession();
        
        if (req.getServletPath() != null && req.getServletPath().startsWith(UNAUTHENTICATED_ACCESS_PROXY))
        {
            if (debug)
                logger.debug("SSO is by-passed for unauthenticated access endpoint.");
            chain.doFilter(sreq, sresp);
            return;
        }
        
        // external invitation link should not trigger any SSO
        if (PAGE_SERVLET_PATH.equals(req.getServletPath()) && IGNORE_LINK.equals(req.getPathInfo()))
        {
            if (debug)
                logger.debug("SSO is by-passed for external invitation link.");
            chain.doFilter(sreq, sresp);
            return;
        }
        
        if (debug) logger.debug("Processing request " + req.getRequestURI() + " SID:" + session.getId());
        
        // Login page or login submission
        String pathInfo;
        if (PAGE_SERVLET_PATH.equals(req.getServletPath())
                && (LOGIN_PATH_INFORMATION.equals(pathInfo = req.getPathInfo()) || pathInfo == null
                        && LOGIN_PARAMETER.equals(req.getParameter("pt"))))
        {
            if (debug)
                logger.debug("Login page requested, chaining ...");

            // Chain to the next filter
            chain.doFilter(sreq, sresp);
            return;
        }

        // initialize a new request context
        RequestContext context = null;
        try
        {
            // perform a "silent" init - i.e. no user creation or remote connections
            context = RequestContextUtil.initRequestContext(getApplicationContext(), req, true);
        }
        catch (Exception ex)
        {
            logger.error("Error calling initRequestContext", ex);
            throw new ServletException(ex);
        }

        // get the page from the model if any - it may not require authentication
        Page page = context.getPage();
        if (page != null && page.getAuthentication() == RequiredAuthentication.none)
        {
            if (logger.isDebugEnabled())
                logger.debug("Unauthenticated page requested - skipping auth filter...");
            chain.doFilter(sreq, sresp);
            return;
        }
        
        // Check if the browser is Opera, if so then display the login page as Opera does not
        // support NTLM and displays an error page if a request to use NTLM is sent to it
        String userAgent = req.getHeader("user-agent");
        if (userAgent != null && userAgent.indexOf("Opera ") != -1)
        {
            if (debug) logger.debug("Opera detected, redirecting to login page");

            redirectToLoginPage(req, res);
            return;
        }
        
        // Check if there is an authorization header with a challenge response
        String authHdr = req.getHeader(HEADER_AUTHORIZATION);

        // We are not passing on a challenge response and we have sufficient client session information
        if (authHdr == null && AuthenticationUtil.isAuthenticated(req))        
        {
            if (debug)
                logger.debug("Touching the repo to ensure we still have an authenticated session.");
            challengeOrPassThrough(chain, req, res, session);
            return;
        }
        
        // Check the authorization header
        if (authHdr == null)
        {
            if (debug) logger.debug("New auth request from " + req.getRemoteHost() + " (" +
                                    req.getRemoteAddr() + ":" + req.getRemotePort() + ")");
            challengeOrPassThrough(chain, req, res, session);
            return;
        }
        // SPNEGO / Kerberos authentication
        else if (authHdr.startsWith(AUTH_SPNEGO) && this.krbRealm != null)
        {            
            if (debug)
                logger.debug("Processing SPNEGO / Kerberos authentication.");
            // Decode the received SPNEGO blob and validate
            
            final byte[] spnegoByts = Base64.decode( authHdr.substring(10).getBytes());
         
            // Check if the client sent an NTLMSSP blob
            
            if ( isNTLMSSPBlob( spnegoByts, 0))
            {
                if ( logger.isDebugEnabled())
                    logger.debug( "Client sent an NTLMSSP security blob");
                
                // Restart the authentication
                
                restartAuthProcess(session, req, res, AUTH_SPNEGO);
                return;
            }
                
            //  Check the received SPNEGO token type

            int tokType = -1;
            
            try
            {
                tokType = SPNEGO.checkTokenType( spnegoByts, 0, spnegoByts.length);
            }
            catch ( IOException ex)
            {
            }

            // Check for a NegTokenInit blob
            
            if ( tokType == SPNEGO.NegTokenInit)
            {
                if (debug)
                    logger.debug("Parsing the SPNEGO security blob to get the Kerberos ticket.");
                
                NegTokenInit negToken = new NegTokenInit();
                
                try
                {
                    // Decode the security blob
                    
                    negToken.decode( spnegoByts, 0, spnegoByts.length);

                    //  Determine the authentication mechanism the client is using and logon
                    
                    String oidStr = null;
                    if ( negToken.numberOfOids() > 0)
                        oidStr = negToken.getOidAt( 0).toString();
                    
                    if (  oidStr != null && (oidStr.equals( OID.ID_MSKERBEROS5) || oidStr.equals(OID.ID_KERBEROS5)))
                    {
                        if (debug)
                            logger.debug("Kerberos logon.");
                        //  Kerberos logon
                        
                        if ( doKerberosLogon( negToken, req, res, session) != null)
                        {
                            // Allow the user to access the requested page
                                
                            chain.doFilter( req, res);
                            if ( logger.isDebugEnabled())
                                logger.debug("Request processing ended");
                        }
                        else
                        {
                            // Send back a request for SPNEGO authentication
                            
                            restartAuthProcess(session, req, res, AUTH_SPNEGO);
                        }   
                    }
                    else
                    {
                        //  Unsupported mechanism, e.g. NegoEx
                        
                        if ( logger.isDebugEnabled())
                            logger.debug( "Unsupported SPNEGO mechanism " + oidStr);
    
                        // Try again!
                        
                        restartAuthProcess(session, req, res, AUTH_SPNEGO);
                    }
                }
                catch ( IOException ex)
                {
                    // Log the error
                    
                    if ( logger.isDebugEnabled())
                        logger.debug(ex);
                }
            }
            else
            {
                //  Unknown SPNEGO token type
                
                if ( logger.isDebugEnabled())
                    logger.debug( "Unknown SPNEGO token type");

                // Send back a request for SPNEGO authentication
                
                restartAuthProcess(session, req, res, AUTH_SPNEGO);
            }
        }
        // NTLM authentication
        else if (authHdr.startsWith(AUTH_NTLM))
        {
            if (debug)
                logger.debug("Processing NTLM authentication.");
            // Decode the received NTLM blob and validate
            final byte[] authHdrByts = authHdr.substring(5).getBytes();
            final byte[] ntlmByts = Base64.decode(authHdrByts);
            int ntlmTyp = NTLMMessage.isNTLMType(ntlmByts);
            
            if (ntlmTyp == NTLM.Type1)
            {
                if (debug)
                    logger.debug("Process the type 1 NTLM message.");
                Type1NTLMMessage type1Msg = new Type1NTLMMessage(ntlmByts);
                
                // Start with a fresh session
                clearSession(session);
                session = req.getSession();
                processType1(type1Msg, req, res, session);
            }
            else if (ntlmTyp == NTLM.Type3)
            {
                if (debug)
                    logger.debug("Process the type 3 NTLM message.");
                Type3NTLMMessage type3Msg = new Type3NTLMMessage(ntlmByts);
                processType3(type3Msg, req, res, session, chain);
            }
            else
            {
                if (debug) logger.debug("NTLM not handled, redirecting to login page");
                
                redirectToLoginPage(req, res);
            }
        }
        // Possibly basic auth - allow through
        else
        {
            if (debug)
                logger.debug("Processing Basic Authentication.");
            if (AuthenticationUtil.isAuthenticated(req))
            {
                if (debug)
                    logger.debug("Ensuring the session is still valid.");
                challengeOrPassThrough(chain, req, res, session);
            }
            else
            {
                if (debug)
                    logger.debug("Establish a new session or bring up the login page.");
                chain.doFilter(req, res);
            }
        }
    }
    
    /**
     * Removes all attributes stored in session
     * 
     * @param session Session
     */
    @SuppressWarnings("unchecked")
    private void clearSession(HttpSession session)
    {
        if (logger.isDebugEnabled())
            logger.debug("Clearing the session.");
        Enumeration<String> names = (Enumeration<String>) session.getAttributeNames();
        while (names.hasMoreElements())
        {
            session.removeAttribute(names.nextElement());
        }
    }

    /**
     * JAAS callback handler
     * 
     * @param callbacks Callback[]
     * @exception IOException
     * @exception UnsupportedCallbackException
     */
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
    {
        if (logger.isDebugEnabled())
            logger.debug("Processing the JAAS callback list of " + callbacks.length + " items.");
        for (int i = 0; i < callbacks.length; i++)
        {
            // Request for user name
            
            if (callbacks[i] instanceof NameCallback)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Request for user name.");
                NameCallback cb = (NameCallback) callbacks[i];
                cb.setName(krbAccountName);
            }
            
            // Request for password
            else if (callbacks[i] instanceof PasswordCallback)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Request for password.");
                PasswordCallback cb = (PasswordCallback) callbacks[i];
                cb.setPassword(krbPassword.toCharArray());
            }
            
            // Request for realm
            
            else if (callbacks[i] instanceof RealmCallback)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Request for realm.");
                RealmCallback cb = (RealmCallback) callbacks[i];
                cb.setText(krbRealm);
            }
            else
            {
                throw new UnsupportedCallbackException(callbacks[i]);
            }
        }
    }

    private void challengeOrPassThrough(FilterChain chain, HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException, ServletException
    {
        try
        {
            // In this mode we can only use vaulted credentials. Do not proxy any request headers.
            String userId = AuthenticationUtil.getUserId(req);
            
            // If we are as yet unauthenticated but have external authentication, do the ping check as the external user.
            // This will either establish the session or throw us out to log in as someone else!
            if (userId == null)
            {
                userId = req.getRemoteUser();
                if (userId != null && logger.isDebugEnabled())
                    logger.debug("Initial login from externally authenticated user " + userId);
                
            }
            else if (logger.isDebugEnabled())
                logger.debug("Validating repository session for  " + userId);

            Connector conn = connectorService.getConnector(this.endpoint, userId, session);

            // ALF-10785: We must pass through the language header to set up the session in the correct locale
            ConnectorContext ctx;
            if (req.getHeader(HEADER_ACCEPT_LANGUAGE) != null)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Accept-Language header present: " + req.getHeader(HEADER_ACCEPT_LANGUAGE));
                ctx = new ConnectorContext(null, Collections.singletonMap(HEADER_ACCEPT_LANGUAGE, req.getHeader(HEADER_ACCEPT_LANGUAGE)));
            }
            else
            {
                ctx = new ConnectorContext();
            }

            Response remoteRes = conn.call("/touch", ctx);
            if (Status.STATUS_UNAUTHORIZED == remoteRes.getStatus().getCode())
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Repository session timed out - restarting auth process...");
                }
                
                String authHdr = remoteRes.getStatus().getHeaders().get(HEADER_WWWAUTHENTICATE);
                if (authHdr != null)
                {
                    // restart SSO login as the repo has timed us out
                    restartAuthProcess(session, req, res, authHdr);
                }
                else
                {
                    // Don't invalidate the session if we've already got external authentication - it may result in us
                    // having to reauthenticate externally too!
                    if (req.getRemoteUser() == null)
                    {
                        session.invalidate();
                    }
                    // restart manual login
                    redirectToLoginPage(req, res);
                }
                return;
            }
            else
            {
                onSuccess(req, res, session, userId);
                
                // we have local auth in the session and the repo session is also valid
                // this means we do not need to perform any further auth handshake
                if (logger.isDebugEnabled())
                {
                    logger.debug("Authentication not required, chaining ...");
                }

                chain.doFilter(req, res);
                return;
            }
        }
        catch (ConnectorServiceException cse)
        {
            throw new PlatformRuntimeException("Incorrectly configured endpoint ID: " + this.endpoint);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy()
    {
    }
    
    /**
     * Return the non-proxied headers for an NTLM /touch request
     * 
     * @param conn      Connector
     * 
     * @return the headers required for the request - if any
     */
    private Map<String, String> getConnectionHeaders(Connector conn)
    {
        Map<String, String> headers = new HashMap<String, String>(4);
        headers.put("user-agent", "");
        if (conn.getConnectorSession().getCookie("JSESSIONID") == null)
        {
            // Ensure we do not proxy over the Session ID from the browser request:
            // If Alfresco and SURF app are deployed into the same app-server and user is
            // user same browser instance to access both apps then we could get wrong session ID!
            headers.put("Cookie", null);
        }

        // ALF-12278: Prevent the copying over of headers specific to a POST request on to the touch GET request
        headers.put("Content-Type", null);
        headers.put("Content-Length", null);
        return headers;
    }
    
    /**
     * Restart the authentication process for NTLM or Kerberos - clear current security details
     */
    private void restartAuthProcess(HttpSession session, HttpServletRequest req, HttpServletResponse res, String authHdr) throws IOException
    {
        if (logger.isDebugEnabled())
            logger.debug("Restarting " + authHdr + " authentication.");

        // Clear any cached logon details from the sessiom
        clearSession(session);
        setRedirectUrl(req);
        
        // restart the authentication process for NTLM
        res.setHeader(HEADER_WWWAUTHENTICATE, authHdr);
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType(MIME_HTML_TEXT);
        
        final PrintWriter out = res.getWriter();
        out.println("<html><head>");
        out.println("<meta http-equiv=\"Refresh\" content=\"0; url=" + 
                req.getContextPath() + "/page?pt=login" + "\">"); 
        out.println("</head><body><p>Please <a href=\"" +
                req.getContextPath() + "/page?pt=login" + "\">log in</a>.</p>");
        out.println("</body></html>");
        out.close();
        
        res.flushBuffer();
    }

    /**
     * Process a type 1 NTLM message
     * 
     * @param type1Msg Type1NTLMMessage
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     * @param session HttpSession
     * 
     * @exception IOException
     */
    private void processType1(Type1NTLMMessage type1Msg, HttpServletRequest req, HttpServletResponse res,
            HttpSession session) throws IOException
    {
        if (logger.isDebugEnabled())
            logger.debug("Received type1 " + type1Msg);
        
        // Get the existing NTLM details
        NTLMLogonDetails ntlmDetails = (NTLMLogonDetails)session.getAttribute(NTLM_AUTH_DETAILS);
        
        // Check if cached logon details are available
        if (ntlmDetails != null && ntlmDetails.hasType2Message() && ntlmDetails.hasNTLMHashedPassword())
        {
            // Get the authentication server type2 response
            Type2NTLMMessage cachedType2 = ntlmDetails.getType2Message();
            
            byte[] type2Bytes = cachedType2.getBytes();
            String ntlmBlob = "NTLM " + new String(Base64.encodeBytes(type2Bytes, Base64.DONT_BREAK_LINES));
            
            if (logger.isDebugEnabled())
                logger.debug("Sending cached NTLM type2 to client - " + cachedType2);
            
            // Send back a request for NTLM authentication
            res.setHeader(HEADER_WWWAUTHENTICATE, ntlmBlob);
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.flushBuffer();
        }
        else
        {
            // Clear any cached logon details
            session.removeAttribute(NTLM_AUTH_DETAILS);
            
            try
            {
                Connector conn = this.connectorService.getConnector(this.endpoint, session);
                ConnectorContext ctx = new ConnectorContext(null, getConnectionHeaders(conn));
                Response remoteRes = conn.call("/touch", ctx, req, null);
                if (Status.STATUS_UNAUTHORIZED == remoteRes.getStatus().getCode())
                {
                    String authHdr = remoteRes.getStatus().getHeaders().get(HEADER_WWWAUTHENTICATE);
                    if (authHdr.startsWith(AUTH_NTLM) && authHdr.length() > 4)
                    {
                        // Decode the received NTLM blob and validate
                        final byte[] authHdrByts = authHdr.substring(5).getBytes();
                        final byte[] ntlmByts = Base64.decode(authHdrByts);
                        int ntlmType = NTLMMessage.isNTLMType(ntlmByts);
                        if (ntlmType == NTLM.Type2)
                        {
                            // Retrieve the type2 NTLM message
                            Type2NTLMMessage type2Msg = new Type2NTLMMessage(ntlmByts);
                            
                            // Store the NTLM logon details, cache the type2 message, and token if using passthru
                            ntlmDetails = new NTLMLogonDetails();
                            ntlmDetails.setType2Message(type2Msg);
                            session.setAttribute(NTLM_AUTH_DETAILS, ntlmDetails);
                            
                            if (logger.isDebugEnabled())
                                logger.debug("Sending NTLM type2 to client - " + type2Msg);
                            
                            // Send back a request for NTLM authentication
                            byte[] type2Bytes = type2Msg.getBytes();
                            String ntlmBlob = "NTLM " + new String(Base64.encodeBytes(type2Bytes, Base64.DONT_BREAK_LINES));
                            
                            res.setHeader(HEADER_WWWAUTHENTICATE, ntlmBlob);
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.flushBuffer();
                        }
                        else
                        {
                            if (logger.isDebugEnabled())
                                logger.debug("Unexpected NTLM message type from repository: NTLMType" + ntlmType);
                            redirectToLoginPage(req, res);
                        }
                    }
                    else
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("Unexpected response from repository: WWW-Authenticate:" + authHdr);
                        redirectToLoginPage(req, res);
                    }
                }
                else
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Unexpected response from repository: " + remoteRes.getStatus().getMessage());
                    redirectToLoginPage(req, res);
                }
            }
            catch (ConnectorServiceException cse)
            {
                throw new PlatformRuntimeException("Incorrectly configured endpoint ID: " + this.endpoint);
            }
        }
    }
    
    /**
     * Process a type 3 NTLM message
     * 
     * @param type3Msg Type3NTLMMessage
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     * @param session HttpSession
     * @param chain FilterChain
     * @exception IOException
     * @exception ServletException
     */
    private void processType3(Type3NTLMMessage type3Msg, HttpServletRequest req, HttpServletResponse res,
            HttpSession session, FilterChain chain) throws IOException, ServletException
    {
        if (logger.isDebugEnabled())
            logger.debug("Received type3 " + type3Msg);
        
        // Get the existing NTLM details
        NTLMLogonDetails ntlmDetails = (NTLMLogonDetails) session.getAttribute(NTLM_AUTH_DETAILS);
        String userId = AuthenticationUtil.getUserId(req);
        
        // Get the NTLM logon details
        String userName = type3Msg.getUserName();
        String workstation = type3Msg.getWorkstation();
        String domain = type3Msg.getDomain();
        
        boolean authenticated = false;
        
        // Check if we are using cached details for the authentication
        if (userId != null && ntlmDetails != null && ntlmDetails.hasNTLMHashedPassword())
        {
            // Check if the received NTLM hashed password matches the cached password
            byte[] ntlmPwd = type3Msg.getNTLMHash();
            byte[] cachedPwd = ntlmDetails.getNTLMHashedPassword();
            
            if (ntlmPwd != null)
            {
                if (ntlmPwd.length == cachedPwd.length)
                {
                    authenticated = true;
                    for (int i = 0; i < ntlmPwd.length; i++)
                    {
                        if (ntlmPwd[i] != cachedPwd[i])
                        {
                            authenticated = false;
                            break;
                        }
                    }
                }
            }
            
            if (logger.isDebugEnabled())
                logger.debug("Using cached NTLM hash, authenticated = " + authenticated);
            
            if (!authenticated)
            {
                restartAuthProcess(session, req, res, AUTH_NTLM);
            }
            else
            {
                // Allow the user to access the requested page
                chain.doFilter(req, res);
            }
        }
        else
        {
            try
            {
                Connector conn = this.connectorService.getConnector(this.endpoint, session);
                ConnectorContext ctx = new ConnectorContext(null, getConnectionHeaders(conn));
                Response remoteRes = conn.call("/touch", ctx, req, null);
                if (Status.STATUS_UNAUTHORIZED == remoteRes.getStatus().getCode())
                {
                    String authHdr = remoteRes.getStatus().getHeaders().get(HEADER_WWWAUTHENTICATE);
                    if (authHdr.equals(AUTH_NTLM))
                    {
                        // authentication failed on repo side - being login process again
                        String userAgent = req.getHeader("user-agent");
                        if (userAgent != null && userAgent.indexOf("Safari") != -1)
                        {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            final PrintWriter out = res.getWriter();
                            out.println("<html><head></head>");
                            out.println("<body><p>Login authentication failed. Please close and re-open Safari to try again.</p>");
                            out.println("</body></html>");
                            out.close();
                        }
                        else
                        {
                            restartAuthProcess(session, req, res, authHdr);
                        }
                        res.flushBuffer();
                    }
                    else
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("Unexpected response from repository: WWW-Authenticate:" + authHdr);
                        redirectToLoginPage(req, res);
                    }
                }
                else if (Status.STATUS_OK == remoteRes.getStatus().getCode() ||
                         Status.STATUS_TEMPORARY_REDIRECT == remoteRes.getStatus().getCode())
                {
                    //
                    // NTLM login successful - Update the NTLM logon details in the session
                    //
                    if (ntlmDetails == null)
                    {
                        // No cached NTLM details
                        ntlmDetails = new NTLMLogonDetails(userName, workstation, domain, false, null);
                        ntlmDetails.setNTLMHashedPassword(type3Msg.getNTLMHash());
                        session.setAttribute(NTLM_AUTH_DETAILS, ntlmDetails);
                        
                        if (logger.isDebugEnabled())
                            logger.debug("No cached NTLM details, created");
                    }
                    else
                    {
                        // Update the cached NTLM details
                        ntlmDetails.setDetails(userName, workstation, domain, false, null);
                        ntlmDetails.setNTLMHashedPassword(type3Msg.getNTLMHash());
                        
                        if (logger.isDebugEnabled())
                            logger.debug("Updated cached NTLM details");
                    }
                    
                    if (logger.isDebugEnabled())
                        logger.debug("User logged on via NTLM, " + ntlmDetails);
                    
                    // Set the external auth flag so the UI knows we are using SSO etc.
                    session.setAttribute(UserFactory.SESSION_ATTRIBUTE_EXTERNAL_AUTH, Boolean.TRUE);
                    
                    onSuccess(req, res, session, userName);
                    
                    // Allow the user to access the requested page
                    chain.doFilter(req, res);
                }
                else
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Unexpected response from repository: " + remoteRes.getStatus().getMessage());
                    redirectToLoginPage(req, res);
                }
            }
            catch (ConnectorServiceException cse)
            {
                throw new PlatformRuntimeException("Incorrectly configured endpoint: " + this.endpoint);
            }
        }
    }
    
    /**
     * Redirect to the root of the website - ignore further SSO auth requests
     */
    private void redirectToLoginPage(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        if (logger.isDebugEnabled())
            logger.debug("Redirecting to the login page.");
        setRedirectUrl(req);
        res.sendRedirect(req.getContextPath() + "/page?pt=login");
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
     * Check if a security blob starts with the NTLMSSP signature
     * 
     * @param byts byte[]
     * @param offset int
     * @return boolean
     */
    private boolean isNTLMSSPBlob( byte[] byts, int offset)
    {
        // Check if the blob has the NTLMSSP signature

        boolean isNTLMSSP = false;
        
        if (( byts.length - offset) >= NTLM.Signature.length) {
          
          if (logger.isDebugEnabled())
              logger.debug("Checking if the blob has the NTLMSSP signature.");
          // Check for the NTLMSSP signature
          
          int idx = 0;
          while ( idx < NTLM.Signature.length && byts[offset + idx] == NTLM.Signature[ idx])
            idx++;
          
          if ( idx == NTLM.Signature.length)
            isNTLMSSP = true;
        }
        
        return isNTLMSSP;
    }
    
    /**
     * Perform a Kerberos login and return an SPNEGO response
     * 
     * @param negToken NegTokenInit
     * @param req HttpServletRequest
     * @param resp HttpServletResponse
     * @param httpSess HttpSession
     * @return NegTokenTarg
     */
    @SuppressWarnings("unchecked")
    private NegTokenTarg doKerberosLogon( NegTokenInit negToken, HttpServletRequest req, HttpServletResponse resp, HttpSession httpSess)
    {
        //  Authenticate the user
        
        KerberosDetails krbDetails = null;
        NegTokenTarg negTokenTarg = null;
        
        try
        {
            //  Run the session setup as a privileged action
            
            KerberosSessionSetupPrivilegedAction sessSetupAction = new KerberosSessionSetupPrivilegedAction(
                    krbAccountName, negToken.getMechtoken(), krbEndpointSPN);
            
            Object result = Subject.doAs( jaasLoginContext.getSubject(), sessSetupAction);
    
            if ( result != null)
            {
                // Access the Kerberos response
                Pair<KerberosDetails, String> resultPair = (Pair<KerberosDetails, String>)result;
                
                krbDetails = resultPair.getFirst();
                String tokenForEndpoint = resultPair.getSecond();
                
                // Create the NegTokenTarg response blob
                
                negTokenTarg = new NegTokenTarg( SPNEGO.AcceptCompleted, OID.KERBEROS5, krbDetails.getResponseToken());
                
                // Check if the user has been authenticated, if so then setup the user environment
                
                if ( negTokenTarg != null)
                {
                    String userName = stripUserNameSuffix ? krbDetails.getUserName() : krbDetails.getSourceName();

                    // Debug
                    if ( logger.isDebugEnabled())
                        logger.debug("User " + userName + " logged on via Kerberos; attempting to log on to Alfresco then");

                    boolean authenticated = doKerberosDelegateLogin(req, resp, httpSess, userName, tokenForEndpoint);
                    if (!authenticated) {
                        return null;
                    }
                }
            }
            else
            {
                // Debug
                
                if ( logger.isDebugEnabled())
                    logger.debug( "No SPNEGO response, Kerberos logon failed");
            }
        }
        catch (Exception ex)
        {
            // Log the error

            if ( logger.isDebugEnabled())
                logger.debug("Kerberos logon error", ex);
        }
    
        // Return the response SPNEGO blob
        
        return negTokenTarg;
    }
    
    private boolean doKerberosDelegateLogin(HttpServletRequest req, HttpServletResponse res, HttpSession session, String userName, String tokenForEndpoint) throws IOException {
        
        try
        {
            Connector conn = connectorService.getConnector(this.endpoint, session);
            ConnectorContext ctx;
            
            // ALF-10785: We must pass through the language header to set up the session in the correct locale            
            if (req.getHeader(HEADER_ACCEPT_LANGUAGE) != null)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Accept-Language header present: " + req.getHeader(HEADER_ACCEPT_LANGUAGE));
                Map<String, String> headers = new HashMap(7);
                headers.put(HEADER_ACCEPT_LANGUAGE, req.getHeader(HEADER_ACCEPT_LANGUAGE));

                ctx = new ConnectorContext(null, headers);
            }
            else
            {
                ctx = new ConnectorContext();
            }

            Response remoteRes = conn.call("/touch", ctx);
            if (Status.STATUS_UNAUTHORIZED == remoteRes.getStatus().getCode())
            {
                String authHdr = remoteRes.getStatus().getHeaders().get(HEADER_WWWAUTHENTICATE);
                if (authHdr.equals(AUTH_SPNEGO))
                {
                    Map<String, String> headers = new HashMap(7);
                    headers.put(HEADER_AUTHORIZATION, AUTH_SPNEGO + ' ' + tokenForEndpoint);
                    
                    if (req.getHeader(HEADER_ACCEPT_LANGUAGE) != null)
                    {
                        headers.put(HEADER_ACCEPT_LANGUAGE, req.getHeader(HEADER_ACCEPT_LANGUAGE));
                    }

                    ctx = new ConnectorContext(null, headers);
                    remoteRes = conn.call("/touch", ctx);
                    
                    if (Status.STATUS_OK == remoteRes.getStatus().getCode() ||
                            Status.STATUS_TEMPORARY_REDIRECT == remoteRes.getStatus().getCode())
                   {
                       if (logger.isDebugEnabled())
                           logger.debug("Authentication succeded on the repo side.");
                       // Create User ID in session so the web-framework dispatcher knows we have logged in
                       session.setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, userName);
                       
                       // Set the external auth flag so the UI knows we are using SSO etc.
                       session.setAttribute(UserFactory.SESSION_ATTRIBUTE_EXTERNAL_AUTH, Boolean.TRUE);
                       

                   } else if (Status.STATUS_UNAUTHORIZED == remoteRes.getStatus().getCode()) {
                       if (logger.isDebugEnabled())
                           logger.debug("Authentication failed on repo side - beging login process again.");
                       res.setHeader(HEADER_WWWAUTHENTICATE, authHdr);
                       res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                       res.flushBuffer();
                   }
                }
                else
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Unexpected response from repository: WWW-Authenticate:" + authHdr);
                    return false;
                }
            }
            else if (Status.STATUS_OK == remoteRes.getStatus().getCode() ||
                     Status.STATUS_TEMPORARY_REDIRECT == remoteRes.getStatus().getCode())
            {
                if (logger.isDebugEnabled())
                    logger.debug("Authentication succeded on the repo side.");
                
                // Set the external auth flag so the UI knows we are using SSO etc.
                session.setAttribute(UserFactory.SESSION_ATTRIBUTE_EXTERNAL_AUTH, Boolean.TRUE);

                onSuccess(req, res, session, userName);
            }
            else
            {
                if (logger.isDebugEnabled())
                    logger.debug("Unexpected response from repository: " + remoteRes.getStatus().getMessage());
                return false;
            }
        }
        catch (ConnectorServiceException cse)
        {
            throw new AlfrescoRuntimeException("Incorrectly configured endpoint: " + this.endpoint);
        }

        return true;
    }

    /**
     * Set the {@link org.alfresco.web.site.SlingshotPageView#REDIRECT_URI}
     * <br> and {@link org.alfresco.web.site.SlingshotPageView#REDIRECT_QUERY}
     * <br> parameters to the session.
     * 
     * @param req
     */
    private void setRedirectUrl(HttpServletRequest req)
    {
        HttpSession session = req.getSession();
        session.setAttribute(REDIRECT_URI, req.getRequestURI());
        if (req.getQueryString() != null)
        {
            session.setAttribute(REDIRECT_QUERY, req.getQueryString());
        }
    }
    
    /**
     * Success login method handler, uses logic of {@link SlingshotLoginController} to retrieve all of the user's groups
     * 
     * @param req current http request
     * @param res current http response
     * @param session current session
     * @param username logged in user name
     * 
     */
    private void onSuccess(HttpServletRequest req, HttpServletResponse res, HttpSession session, String username)
    {
        try
        {
            if (session.getAttribute(SlingshotLoginController.SESSION_ATTRIBUTE_KEY_USER_GROUPS) == null)
            {
                // Create User ID in session so the web-framework dispatcher knows we have logged in
                session.setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, username);
    
                // ACE-3257 fix, retrieve all of the groups that the user is a member of...
                loginController.onSuccess(req, res);
            }
        }
        catch (Exception e)
        {
            throw new AlfrescoRuntimeException("Unable to retrieve groups for currently logged in user!", e);
        }
    }
}
