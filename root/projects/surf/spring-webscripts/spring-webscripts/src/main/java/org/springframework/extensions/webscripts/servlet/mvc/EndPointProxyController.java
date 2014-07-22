/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts.servlet.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.config.RemoteConfigElement.IdentityType;
import org.springframework.extensions.surf.exception.WebScriptsPlatformException;
import org.springframework.extensions.surf.util.Base64;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.CredentialsImpl;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.RemoteClient;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * EndPoint HTTP Proxy Controller for Spring MVC.
 * <p>
 * Provides the ability to submit a URL request via a configured end-point such as a
 * remote Alfresco Server or any remote machine that supports an HTTP REST interface.
 * Makes use of the Connector framework so that appropriate authentication is automatically
 * applied to the proxied request as applicable.
 * <p>
 * This servlet accepts URIs of the following format:
 * <pre>/proxy/&lt;endpointid&gt;[/&lt;uri&gt;]*[?[&lt;argName&gt;=&lt;argValue&gt;]*]</pre>
 * Where:
 * <p>
 * - endpointid is the ID of a configured EndPoint model object to make a request against<br>
 * - url is the uri to call on the EndPoint URL e.g. /api/sites<br>
 * - argName is the name of a URL argument to append to the request<br>
 * - argValue is the value of URL argument<br>
 * <p>
 * E.g.
 * <pre>/proxy/alfresco/api/sites?name=mysite&amp;desc=description</pre>
 * The proxy supports all valid HTTP methods supported by the RemoteClient.
 * 
 * @see RemoteClient
 * 
 * @author Kevin Roast
 */
public class EndPointProxyController extends AbstractController
{
    private static Log logger = LogFactory.getLog(EndPointProxyController.class);
    
    private static final long serialVersionUID = -176412355613122789L;
    
    private static final String JSESSIONID = ";jsessionid=";
    private static final String USER_ID = "_alf_USER_ID";
    
    // Spring bean references
    protected ConfigService configService;
    protected ConnectorService connectorService;
    protected ProxyControllerInterceptor proxyControllerInterceptor = new ProxyControllerInterceptor()
    {
        public boolean exceptionOnError(EndpointDescriptor endpoint, String uri)
        {
            return false;
        }
        
        public boolean allowHttpBasicAuthentication(EndpointDescriptor endpoint, String uri)
        {
            return false;
        }
    };
    
    // Service cached values
    protected RemoteConfigElement config;
    
    
    /**
     * Sets the config service.
     * 
     * @param configService the new config service
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /**
     * Sets the connector service.
     * 
     * @param connectorService the new connector service
     */
    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }
    
    /**
     * Set if to present a Basic HTTP Auth challenge with a 401 error, otherwise a plain 401 response
     * will be sent when no authorised user is available in the session and no other auth is available.
     * Note: deprecated. @see ProxyControllerInterceptor 
     * 
     * @param basicHttpAuthChallenge true to present a Basic HTTP Auth challenge with a 401 error, false otherwise
     */
    @Deprecated
    public void setBasicHttpAuthChallenge(boolean basicHttpAuthChallenge)
    {
    }
    
    /**
     * Set the ProxyControllerInterceptor reference
     * 
     * @param proxyControllerInterceptor the ProxyControllerInterceptor
     */
    public void setProxyControllerInterceptor(ProxyControllerInterceptor proxyControllerInterceptor)
    {
        if (proxyControllerInterceptor == null)
        {
            throw new IllegalArgumentException("ProxyControllerInterceptor is mandatory");
        }
        this.proxyControllerInterceptor = proxyControllerInterceptor;
    }
    
    /**
     * Gets the remote config.
     * 
     * @return the remote config
     */
    public RemoteConfigElement getRemoteConfig()
    {
        if (this.config == null)
        {
            // retrieve the remote configuration
            this.config = (RemoteConfigElement) configService.getConfig("Remote").getConfigElement("remote");
        }
        
        return this.config;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.mvc.AbstractController#createModelAndView(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws Exception
    {
        // get the portion of the uri beyond the handler mapping (resolved by Spring)
        String uri = (String) req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        
        // handle Flash uploader specific jsession parameter for conforming to servlet spec on later TomCat 6/7 versions
        int jsessionid;
        if ((jsessionid = uri.indexOf(JSESSIONID)) != -1)
        {
            uri = uri.substring(0, jsessionid);
        }
        
        // validate and return the endpoint id from the URI path
        StringTokenizer t = new StringTokenizer(URLEncoder.encodeUri(uri), "/");
        if (!t.hasMoreTokens())
        {
            throw new IllegalArgumentException("Proxy URL did not specify endpoint id.");
        }
        String endpointId = t.nextToken();
        
        // rebuild rest of the URL for the proxy request
        StringBuilder buf = new StringBuilder(64);
        if (t.hasMoreTokens())
        {
            do
            {
                buf.append('/');
                buf.append(t.nextToken());
            } while (t.hasMoreTokens());
        }
        else
        {
            // allow for an empty uri to be passed in
            // this could therefore refer to the root of a service i.e. /webapp/axis
            buf.append('/');
        }
        
        try
        {
            // retrieve the endpoint descriptor - do not allow proxy access to unsecure endpoints
            EndpointDescriptor descriptor = getRemoteConfig().getEndpointDescriptor(endpointId);
            if (descriptor == null || descriptor.getUnsecure())
            {
                // throw an exception if endpoint ID is does not exist or invalid
                throw new WebScriptsPlatformException("Invalid EndPoint Id: " + endpointId);
            }
            
            // user id from session NOTE: @see org.springframework.extensions.surf.UserFactory
            Connector connector;
            String userId = null;
            HttpSession session = req.getSession(false);
            if (session != null)
            {
                userId = (String)session.getAttribute(USER_ID);
            }
            if (userId != null && this.connectorService.getCredentialVault(req.getSession(), userId).hasCredentials(endpointId))
            {
                // build an authenticated connector - as we have a userId
                connector = this.connectorService.getConnector(endpointId, userId, req.getSession());
            }
            else if (descriptor.getIdentity() == IdentityType.NONE ||
                     descriptor.getIdentity() == IdentityType.DECLARED ||
                     descriptor.getExternalAuth())
            {
                // the authentication for this endpoint is either not required, declared in config or
                // managed "externally" (i.e. by a servlet filter such as NTLM) - this means we should
                // proceed on the assumption it will be dealt with later
                connector = this.connectorService.getConnector(endpointId, req.getSession());
            }
            else if (descriptor.getBasicAuth() || this.proxyControllerInterceptor.allowHttpBasicAuthentication(descriptor, uri))
            {
                // check for HTTP authorisation request (i.e. RSS feeds, direct links etc.)
                String authorization = req.getHeader("Authorization");
                if (authorization == null || authorization.length() == 0)
                {
                    authorizedResponseStatus(res);
                    
                    // no further processing as authentication is required but not provided
                    // the browser will now prompt the user for appropriate credentials
                    return null;
                }
                else
                {
                    // user has provided authentication details with the request
                    String[] authParts = authorization.split(" ");
                    // test for a "negotiate" header - we will then suggest "basic" as the auth mechanism
                    if (authParts[0].equalsIgnoreCase("negotiate"))
                    {
                       authorizedResponseStatus(res);
                       
                       // no further processing as authentication is required but not provided
                       // the browser will now prompt the user for appropriate credentials
                       return null;
                    }
                    if (!authParts[0].equalsIgnoreCase("basic"))
                    {
                        throw new WebScriptsPlatformException("Authorization '" + authParts[0] + "' not supported.");
                    }
                    
                    String[] values = new String(Base64.decode(authParts[1])).split(":");
                    if (values.length == 2)
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("Authenticating (BASIC HTTP) user " + values[0]);
                        
                        // assume username and password passed as the parts and
                        // build an unauthenticated authentication connector then
                        // apply the supplied credentials to it
                        connector = this.connectorService.getConnector(endpointId, values[0], req.getSession());
                        Credentials credentials = new CredentialsImpl(endpointId);
                        credentials.setProperty(Credentials.CREDENTIAL_USERNAME, values[0]);
                        credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, values[1]);
                        connector.setCredentials(credentials);
                    }
                    else
                    {
                        authorizedResponseStatus(res);
                        
                        // no further processing as authentication is required but not provided
                        // the browser will now prompt the user for appropriate credentials
                        return null;
                    }
                }
            }
            else
            {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED,
                        "No USER_ID found in session and requested endpoint requires authentication.");
                
                // no further processing as authentication is required but not provided
                return null;
            }
            
            // build a connector context, stores information about how we will drive the remote client
            // ensure we don't proxy over any browser to web-tier Authorization headers over to the endpoint
            Map<String, String> headers = new HashMap<String, String>(1, 1.0f);
            headers.put("Authorization", null);
            ConnectorContext context = new ConnectorContext(
                  HttpMethod.valueOf(req.getMethod().toUpperCase()), null, headers);
            context.setExceptionOnError(this.proxyControllerInterceptor.exceptionOnError(descriptor, uri));
            context.setContentType(req.getContentType());
            
            // build proxy URL referencing the endpoint
            final String q = req.getQueryString();
            final String url = buf.toString() + (q != null && q.length() != 0 ? "?" + q : "");
            
            if (logger.isDebugEnabled())
            {
                logger.debug("EndPointProxyController preparing to proxy:");
                logger.debug(" - endpointId: " + endpointId);
                logger.debug(" - userId: " + userId);
                logger.debug(" - connector: " + connector);
                logger.debug(" - method: " + context.getMethod());
                logger.debug(" - url: " + url);
            }
            
            // call through using our connector to proxy
            Response response = connector.call(url, context, req, res);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Return code: " + response.getStatus().getCode());
                if (response.getStatus().getCode() == 500)
                {
                    logger.debug("Error detected: " + response.getStatus().getMessage() + "\n" +
                            response.getStatus().getException().toString());
                }
            }
        }
        catch (Throwable err)
        {
            // TODO: trap and handle errors!
            throw new WebScriptsPlatformException("Error during endpoint proxy processing: " + err.getMessage(), err);
        }
        
        return null;
    }

    private void authorizedResponseStatus(HttpServletResponse res)
    {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED,
                "No USER_ID found in session and requested endpoint requires authentication.");
        res.setHeader("WWW-Authenticate", "Basic realm=\"Alfresco\"");
    }
}