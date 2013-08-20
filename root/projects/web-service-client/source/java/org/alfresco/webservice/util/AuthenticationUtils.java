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
package org.alfresco.webservice.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.alfresco.webservice.authentication.AuthenticationFault;
import org.alfresco.webservice.authentication.AuthenticationResult;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.ws.security.WSPasswordCallback;

/**
 * @author Roy Wetherall
 */
public class AuthenticationUtils implements CallbackHandler
{
    /** WS security information */
    private static final String WS_SECURITY_INFO = 
         "<deployment xmlns='http://xml.apache.org/axis/wsdd/' xmlns:java='http://xml.apache.org/axis/wsdd/providers/java'>" +
         "   <transport name='http' pivot='java:org.apache.axis.transport.http.HTTPSender'/>" +
         "   <globalConfiguration >" +
         "     <requestFlow >" +
         "       <handler type='java:org.apache.ws.axis.security.WSDoAllSender' >" +
         "               <parameter name='action' value='UsernameToken Timestamp'/>" +
         "               <parameter name='user' value='ticket'/>" +
         "               <parameter name='passwordCallbackClass' value='org.alfresco.webservice.util.AuthenticationUtils'/>" +
         "               <parameter name='passwordType' value='PasswordText'/>" +
         "           </handler>" +
         "       <handler name='cookieHandler' type='java:org.alfresco.webservice.util.CookieHandler' />" +
         "     </requestFlow >" +
         "   </globalConfiguration>" +
         "</deployment>";

    /** WS simple information */
    private static final String WS_SIMPLE_INFO = 
         "<deployment xmlns='http://xml.apache.org/axis/wsdd/' xmlns:java='http://xml.apache.org/axis/wsdd/providers/java'>" +
         "   <transport name='http' pivot='java:org.apache.axis.transport.http.HTTPSender'/>" +
         "   <globalConfiguration >" +
         "     <requestFlow >" +
         "       <handler name='cookieHandler' type='java:org.alfresco.webservice.util.CookieHandler' />" +
         "     </requestFlow >" +
         "   </globalConfiguration>" +
         "</deployment>";

    /** Thread local containing the current authentication details */
    private static ThreadLocal<AuthenticationDetails> authenticationDetails = new ThreadLocal<AuthenticationDetails>();
    
    /**
     * Start a session
     * 
     * @param username
     * @param password
     * @throws AuthenticationFault
     */
    public static void startSession(String username, String password)
        throws AuthenticationFault
    {
        try
        {
            // Start the session
            AuthenticationResult result = WebServiceFactory.getAuthenticationService().startSession(username, password);           
            
            // Store the ticket for use later
            authenticationDetails.set(new AuthenticationDetails(result.getUsername(), result.getTicket(), result.getSessionid()));
        }
        catch (RemoteException exception)
        {
            if (exception instanceof AuthenticationFault)
            {
                // Rethrow the authentication exception
                throw (AuthenticationFault)exception;
            }
            else
            {
                // Throw the exception as a wrapped runtime exception
                throw new WebServiceException("Error starting session.", exception);
            }
        }             
    }
    
    /**
     * Start a session
     * 
     * @param username
     * @param password
     * @param timeoutInterval timeout interval
     * @throws AuthenticationFault
     */
    public static void startSession(String username, String password, long timeoutInterval)
    	throws AuthenticationFault
	{
		startSession(username, password);
	
		AuthenticationDetails ad = getAuthenticationDetails();
		ad.setTimeoutInterval(timeoutInterval);
	}
    
    public static void setAuthenticationDetails(AuthenticationDetails authenticationDetails)
    {
    	AuthenticationUtils.authenticationDetails.set(authenticationDetails);
    }
    
    /**
	 * @return if timeoutInterval is not set return false.
	 */
    public static boolean isCurrentTicketTimedOut()
    {
    	boolean to = getAuthenticationDetails().isTimedOut();
    	
    	if (to)
    		endSession();
    	
    	return to;
    }


    /**
     * Ends the current session
     */
    public static void endSession()
    {
        AuthenticationDetails authenticationDetails = AuthenticationUtils.authenticationDetails.get();
        if (authenticationDetails != null)
        {
            try
            {
                WebServiceFactory.getAuthenticationService().endSession(authenticationDetails.getTicket());
                AuthenticationUtils.authenticationDetails.remove();
            }
            catch (RemoteException exception)
            {
                exception.printStackTrace();
                throw new WebServiceException("Error ending session.", exception);
            }
        }
    }
    
    /**
     * Get the ticket for the current authentication details on the current thread
     * 
     * @return  String  the ticket
     */
    public static String getTicket()
    {
        String result = null;
        AuthenticationDetails authDetails = AuthenticationUtils.authenticationDetails.get();
        if (authDetails != null)
        {
            result = authDetails.getTicket();
        }
        return result;
    }
    
    /**
     * Get the authentication details for the current thread
     * 
     * @return  the authentication details
     */
    public static AuthenticationDetails getAuthenticationDetails()
    {
        return AuthenticationUtils.authenticationDetails.get();
    }
    
    /**
     * The implementation of the passwrod call back used by the WS Security
     * 
     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
    {
       for (int i = 0; i < callbacks.length; i++) 
       {
          if (callbacks[i] instanceof WSPasswordCallback) 
          {
             WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
             String ticket = AuthenticationUtils.getTicket();
             if (ticket == null)
             {
                 throw new WebServiceException("Ticket could not be found when calling callback handler.");
             }
             pc.setPassword(ticket);
          }
          else 
          {
             throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
          }
       }
    }
    
    /**
     * Gets the engine configuration used to create the web service references
     * 
     * @return  EngineConfiguration     the engine configuration
     */
    public static EngineConfiguration getEngineConfiguration()
    {
        return new FileProvider(new ByteArrayInputStream(WS_SECURITY_INFO.getBytes()));
    }    

    /**
     * Gets the simple engine configuration used to create the authentication service references
     * 
     * @return {@link EngineConfiguration} the simple engine configuration
     */
    public static EngineConfiguration getSimpleConfiguration()
    {
        return new FileProvider(new ByteArrayInputStream(WS_SIMPLE_INFO.getBytes()));
    }
}
