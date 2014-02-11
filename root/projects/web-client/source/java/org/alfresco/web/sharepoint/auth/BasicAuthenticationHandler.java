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
package org.alfresco.web.sharepoint.auth;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.repo.SessionUser;
import org.alfresco.repo.management.subsystems.ActivateableBean;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.web.auth.AuthenticationListener;
import org.alfresco.repo.web.auth.BasicAuthCredentials;
import org.alfresco.repo.webdav.auth.SharepointConstants;
import org.alfresco.web.bean.repository.User;
import org.apache.commons.codec.binary.Base64;

/**
 * <p>
 * BASIC web authentication implementation.
 * </p>
 * 
 * @author PavelYur
 */
public class BasicAuthenticationHandler extends AbstractAuthenticationHandler implements SharepointConstants
{
    private final static String HEADER_AUTHORIZATION = "Authorization";

    private final static String BASIC_START = "Basic";
    
    private AuthenticationListener authenticationListener;
    protected RemoteUserMapper remoteUserMapper;
    protected AuthenticationComponent authenticationComponent;
    
    /**
     * Set the authentication listener
     */
    public void setAuthenticationListener(AuthenticationListener authenticationListener)
    {
        this.authenticationListener = authenticationListener;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.alfresco.repo.webdav.auth.SharepointAuthenticationHandler#authenticateRequest(javax.servlet.ServletContext,
     * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public boolean authenticateRequest(ServletContext context, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
        if (isUserAuthenticated(context, request))
        {
            return true;
        }
        else
        {
            // Unlike multi-stage authentication protocols like Kerberos / NTLM we have only one possible response to an
            // unauthenticated user
            restartLoginChallenge(context, request, response);
            return false;
        }
    }

    /**
     * Returns <code>true</code> if the user is authenticated and their details are cached in the session
     * 
     * @param context
     *            the servlet context
     * @param request
     *            the servlet request
     * @return <code>true</code>, if the user is authenticated
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             On other errors.
     */
    public boolean isUserAuthenticated(ServletContext context, HttpServletRequest request) throws IOException,
            ServletException
    {
        String authHdr = request.getHeader(HEADER_AUTHORIZATION);
        HttpSession session = request.getSession(false);
        SessionUser sessionUser = session == null ? null : (SessionUser) session.getAttribute(USER_SESSION_ATTRIBUTE);
        if (sessionUser == null)
        {
            if (authHdr != null && authHdr.length() > 5 && authHdr.substring(0, 5).equalsIgnoreCase(BASIC_START))
            {
                String basicAuth = new String(Base64.decodeBase64(authHdr.substring(5).getBytes()));
                String username = null;
                String password = null;
    
                int pos = basicAuth.indexOf(":");
                if (pos != -1)
                {
                    username = basicAuth.substring(0, pos);
                    password = basicAuth.substring(pos + 1);
                }
                else
                {
                    username = basicAuth;
                    password = "";
                }
    
                try
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Authenticating user '" + username + "'");
    
                    authenticationService.authenticate(username, password.toCharArray());
    
                    // Normalize the user ID taking into account case sensitivity settings
                    username = authenticationService.getCurrentUserName();
    
                    if (logger.isDebugEnabled())
                        logger.debug("Authenticated user '" + username + "'");
                    
                    authenticationListener.userAuthenticated(new BasicAuthCredentials(username, password));
    
                    request.getSession()
                            .setAttribute(
                                    USER_SESSION_ATTRIBUTE,
                                    new User(username, authenticationService.getCurrentTicket(), personService
                                            .getPerson(username)));
    
                    return true;
                }
                catch (AuthenticationException ex)
                {
                    authenticationListener.authenticationFailed(new BasicAuthCredentials(username, password), ex);
                }
            }
            else
            {
                if (remoteUserMapper != null && (!(remoteUserMapper instanceof ActivateableBean) || ((ActivateableBean) remoteUserMapper).isActive()))
                {
                    String userId = remoteUserMapper.getRemoteUser(request);
                    if (userId != null)
                    {
                        // authenticated by other
                        authenticationComponent.setCurrentUser(userId);

                        request.getSession().setAttribute(USER_SESSION_ATTRIBUTE, new User(userId, authenticationService.getCurrentTicket(), personService.getPerson(userId)));
                        return true;
                    }
                }
            }
        }
        else
        {
            try
            {
                authenticationService.validate(sessionUser.getTicket());
                authenticationListener.userAuthenticated(new TicketCredentials(sessionUser.getTicket()));
                return true;
            }
            catch (AuthenticationException ex)
            {
                authenticationListener.authenticationFailed(new TicketCredentials(sessionUser.getTicket()), ex);
                session.invalidate();
            }
        }

        return false;
    }

    @Override
    public String getWWWAuthenticate()
    {
        return "Basic realm=\"Alfresco Server\"";
    }

    public void setRemoteUserMapper(RemoteUserMapper remoteUserMapper)
    {
        this.remoteUserMapper = remoteUserMapper;
    }

    public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }
    
    
}
