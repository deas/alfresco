/*
* Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.repo.lotus.ws.impl.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.acegisecurity.Authentication;

import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

/**
 * Basic authenticator based on username/password retrieved from Authorization http header
 * 
 * @author PavelYur
 */
public class BasicAuthenticator implements Authenticator
{
    private Log logger = LogFactory.getLog(BasicAuthenticator.class);

    private final static String AUTHENTICATION_USER = "_alfAuthUser";

    private TransactionService transactionService;
    private AuthenticationService authenticationService;
    private AuthenticationComponent authenticationComponent;

    /**
     * Sets transactionService
     * 
     * @param transactionService the transactionService to set
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    /**
     * Sets authenticationService
     * 
     * @param authenticationService the authenticationService to set
     */
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }

    @Override
    public boolean authenticate(Message message)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Start BASIC authentication for incoming request.");
        }

        final AuthorizationPolicy policy = message.get(AuthorizationPolicy.class);

        boolean success = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Boolean>()
        {
            public Boolean execute() throws Throwable
            {
                try
                {
                    if (policy == null)
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("BASIC authentication was not performed. Credentials was not provided.");
                        }
                        return Boolean.FALSE;
                    }

                    authenticationService.authenticate(policy.getUserName(), policy.getPassword().toCharArray());
                }
                catch (AuthenticationException e)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("BASIC authentication failed with message: " + e.getMessage());
                    }
                    return Boolean.FALSE;
                }

                if (logger.isDebugEnabled())
                {
                    logger.debug("BASIC authentication successfully finished. User: " + authenticationService.getCurrentUserName());
                }
                return Boolean.TRUE;
            }
        });

        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        HttpSession session = request.getSession();

        if (success)
        {
            session.setAttribute(AUTHENTICATION_USER, policy.getUserName());
        }
        else
        {
            String userId = (String) session.getAttribute(AUTHENTICATION_USER);

            if (userId != null)
            {
                Authentication auth = authenticationComponent.setCurrentUser(userId);
                if (auth.isAuthenticated())
                {
                    return Boolean.TRUE;
                }
                else
                {
                    session.removeAttribute(AUTHENTICATION_USER);
                }
            }
        }

        return success;
    }

    @Override
    public boolean isActive()
    {
        return true;
    }

}
