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
package org.alfresco.webservice.test;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.alfresco.webservice.authentication.AuthenticationFault;
import org.alfresco.webservice.authentication.AuthenticationResult;
import org.alfresco.webservice.authentication.AuthenticationServiceSoapBindingStub;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * Tests the AuthenticationService by trying to login as admin/admin and  
 * attempting to login with incorrect credentials.
 * 
 * @author gavinc
 */
public class AuthenticationServiceSystemTest extends TestCase 
{
   /**
    * Tests whether the authentication service is working correctly
    * 
    * @throws Exception
    */
   public void testSuccessfulLogin() throws Exception 
   {
      try 
      {
         AuthenticationResult value = WebServiceFactory.getAuthenticationService().startSession("admin", "admin");
         assertNotNull("result must not be null", value);
         System.out.println("ticket = " + value.getTicket());
      }
      catch (AuthenticationFault error) 
      {
         throw new AssertionFailedError("AuthenticationFault Exception caught: " + error);
      }
   }
   
   /**
    * Tests that a failed authentication attempt fails as expected
    * 
    * @throws Exception
    */
   public void testFailedLogin() throws Exception
   {
      try
      {
          WebServiceFactory.getAuthenticationService().startSession("wrong", "credentials");
         fail("The credentials are incorrect so an AuthenticationFault should have been thrown");
      }
      catch (AuthenticationFault error) 
      {
         // we expected this
      }
   }
   
   /**
    * Tests endSession
    * 
    * @throws Exception
    */
   public void testEndSession() throws Exception
   {
       AuthenticationServiceSoapBindingStub authenticationService = WebServiceFactory.getAuthenticationService();
       
       // Create and end a session
       AuthenticationResult result = authenticationService.startSession("admin", "admin");
       authenticationService.endSession(result.getTicket());
       
       try
       {
           // Try and end an invalid session
           authenticationService.endSession("badSessionId");
           fail("An exception should have been thrown since we are trying to end an invalid session");
       }
       catch (Throwable exception)
       {
           // Web are expecting this exception
       }
   }
}
