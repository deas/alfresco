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

import org.alfresco.webservice.administration.NewUserDetails;
import org.alfresco.webservice.administration.UserDetails;
import org.alfresco.webservice.administration.UserFilter;
import org.alfresco.webservice.administration.UserQueryResults;
import org.alfresco.webservice.repository.RepositoryServiceLocator;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.QueryConfiguration;
import org.alfresco.webservice.util.AuthenticationDetails;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Administration service system test
 * 
 * @author Roy Wetherall
 */
public class AdministrationServiceSystemTest extends BaseWebServiceSystemTest
{
    private static Log logger = LogFactory.getLog(AdministrationServiceSystemTest.class);
    
    public void testGetUsersBatching() throws Exception
    {
        int batchSize = 1;
        QueryConfiguration queryCfg = new QueryConfiguration();
        queryCfg.setFetchSize(batchSize);
        WebServiceFactory.getAdministrationService().setHeader(
                new RepositoryServiceLocator().getServiceName().getNamespaceURI(), 
                "QueryHeader", 
                queryCfg);
        
        // Get the details of the new users
        String homeFolder = store.getScheme() + "://" + store.getAddress() + "/" + folderReference.getUuid();
        String one = Long.toString(System.currentTimeMillis());
        String two = one + "2";        
        NewUserDetails[] newUsers = new NewUserDetails[] {
                new NewUserDetails(
                        "user" + one, 
                        "password" + one,
                        createPersonProperties(homeFolder, "first" + one, "middle" + one, "last" + one, "email" + one, "org" + one)),
                new NewUserDetails(
                        "user" + two, 
                        "password2" + two,
                        createPersonProperties(homeFolder, "first" + two, "middle" + two, "last" + two, "email" + two, "org" + two)) };

        // Create the new users
        WebServiceFactory.getAdministrationService().createUsers(newUsers);
        
        UserQueryResults results = WebServiceFactory.getAdministrationService().queryUsers(null);
        assertNotNull(results);
        
        if (logger.isDebugEnabled() == true)
        {
            while(true)
            {
                System.out.println("Next batch");
                System.out.println("Session Id: " + results.getQuerySession());
                
                for (UserDetails details : results.getUserDetails())
                {
                    System.out.println("User name: " + details.getUserName());
                }
                
                if (results.getQuerySession() == null)
                {
                    break;
                }
                results = WebServiceFactory.getAdministrationService().fetchMoreUsers(results.getQuerySession());
            }
        }
        

        // Delete the created users
        String[] userNames = new String[]{"user" + one, "user" + two};
        WebServiceFactory.getAdministrationService().deleteUsers(userNames);
    }
    
    /**
     * Test the general user CRUD methods
     */
    public void testCreateGetDeleteUser() throws Exception
    {
        // Try and get a user that does not exist
        try
        {
            WebServiceFactory.getAdministrationService().getUser("badUser");
            fail("An exception should have been raised since we are trying to get hold of a user that does not exist.");
        }
        catch (Exception exception)
        {
            // Ignore since this is what we would expect to happen
        }
        
        // Get the details of the new users
        String homeFolder = store.getScheme() + "://" + store.getAddress() + "/" + folderReference.getUuid();
        String one = Long.toString(System.currentTimeMillis());
        String two = one + "2";        
        NewUserDetails[] newUsers = new NewUserDetails[] {
                new NewUserDetails(
                        "user" + one, 
                        "password" + one,
                        createPersonProperties(homeFolder, "first" + one, "middle" + one, "last" + one, "email" + one, "org" + one)),
                new NewUserDetails(
                        "user" + two, 
                        "password2" + two,
                        createPersonProperties(homeFolder, "first" + two, "middle" + two, "last" + two, "email" + two, "org" + two)) };

        // Create the new users
        UserDetails[] userDetails = WebServiceFactory.getAdministrationService().createUsers(newUsers);

        // Check the details of the created users
        assertNotNull(userDetails);
        assertEquals(2, userDetails.length);
        String name = one;
        for (UserDetails result : userDetails)
        {
            NamedValue[] properties = result.getProperties();
            for (NamedValue value : properties)
            {
               if (value.getName().equals(Constants.PROP_USER_FIRSTNAME) == true)
               {
                   assertEquals("first" + name, value.getValue());
               }
               else if (value.getName().equals(Constants.PROP_USER_MIDDLENAME) == true)
               {
                   assertEquals("middle" + name, value.getValue());
               }
               else if (value.getName().equals(Constants.PROP_USER_LASTNAME) == true)
               {
                   assertEquals("last" + name, value.getValue());
               }
               else if (value.getName().equals(Constants.PROP_USER_HOMEFOLDER) == true)
               {
                   assertEquals(homeFolder, value.getValue());
               }
               else if (value.getName().equals(Constants.PROP_USER_EMAIL) == true)
               {
                   assertEquals("email" + name, value.getValue());
               }
               else if (value.getName().equals(Constants.PROP_USER_ORGID) == true)
               {
                   assertEquals("org" + name, value.getValue());
               }
               else if (value.getName().equals(Constants.PROP_USERNAME) == true)
               {
                   assertEquals("user" + name, value.getValue());
               }
            }
            name = two;
        }
        
        // Try and get one of the created users
        UserDetails userDetails2 = WebServiceFactory.getAdministrationService().getUser("user" + one);
        
        // Check the user details
        assertNotNull(userDetails2);
        assertEquals("user" + one, userDetails2.getUserName());
        NamedValue[] properties = userDetails2.getProperties();
        for (NamedValue value : properties)
        {
           if (value.getName().equals(Constants.PROP_USER_FIRSTNAME) == true)
           {
               assertEquals("first" + one, value.getValue());
           }
           else if (value.getName().equals(Constants.PROP_USER_MIDDLENAME) == true)
           {
               assertEquals("middle" + one, value.getValue());
           }
           else if (value.getName().equals(Constants.PROP_USER_LASTNAME) == true)
           {
               assertEquals("last" + one, value.getValue());
           }
           else if (value.getName().equals(Constants.PROP_USER_HOMEFOLDER) == true)
           {
               assertEquals(homeFolder, value.getValue());
           }
           else if (value.getName().equals(Constants.PROP_USER_EMAIL) == true)
           {
               assertEquals("email" + one, value.getValue());
           }
           else if (value.getName().equals(Constants.PROP_USER_ORGID) == true)
           {
               assertEquals("org" + one, value.getValue());
           }
           else if (value.getName().equals(Constants.PROP_USERNAME) == true)
           {
               assertEquals("user" + one, value.getValue());
           }
        }

        // Delete the created users
        String[] userNames = new String[]{"user" + one, "user" + two};
        WebServiceFactory.getAdministrationService().deleteUsers(userNames);
        
        // Ensure that the users have been deleted
        try
        {
            WebServiceFactory.getAdministrationService().getUser("user" + two);
            fail("An exception should have been raised since we are trying to get hold of a user that has previously been deleted.");
        }
        catch (Exception exception)
        {
            // Ignore since this is what we would expect to happen
        }
    }

    public void testUserFilter()
        throws Exception
    {
        UserFilter userFilter = new UserFilter("^user.*");

        UserQueryResults results = WebServiceFactory.getAdministrationService().queryUsers(userFilter);
        assertNotNull(results);
        if (results.getUserDetails() != null)
        {
        	assertTrue(results.getUserDetails().length != 0);
        }
        
        UserFilter userFilter2 = new UserFilter("^bob.*");

        UserQueryResults results2 = WebServiceFactory.getAdministrationService().queryUsers(userFilter2);
        assertNotNull(results2);
        assertNull(results2.getUserDetails());
        
        UserFilter userFilter3 = new UserFilter("^ad.*");

        UserQueryResults results3 = WebServiceFactory.getAdministrationService().queryUsers(userFilter3);
        assertNotNull(results3);
        assertTrue(results3.getUserDetails().length == 1);
        
        UserFilter userFilter4 = new UserFilter("admin");

        UserQueryResults results4 = WebServiceFactory.getAdministrationService().queryUsers(userFilter4);
        assertNotNull(results4);
        assertTrue(results4.getUserDetails().length == 1);
    }
    
    /**
     * Test being able to create a new user, log in as that user, change that users password
     */
    public void testCreateAndAuthenticateNewUser() throws Exception
    {
        // Get the details of the new user
        String homeFolder = store.getScheme() + "://" + store.getAddress() + "/" + folderReference.getUuid();
        String one = Long.toString(System.currentTimeMillis());
        NewUserDetails[] newUsers = new NewUserDetails[] 
        {
                new NewUserDetails(
                        "user" + one, 
                        "password" + one,
                        createPersonProperties(homeFolder, "first" + one, "middle" + one, "last" + one, "email" + one, "org" + one))
        };

        // Create the new users
        UserDetails[] userDetails = WebServiceFactory.getAdministrationService().createUsers(newUsers);
        assertNotNull(userDetails);
        assertEquals(1, userDetails.length);
        
        // End the current session
        AuthenticationUtils.endSession();
        
        // Try and start a session as the newly create user
        AuthenticationUtils.startSession("user" + one, "password" + one);
        
        // Re-login as the admin user
        AuthenticationUtils.endSession();
        AuthenticationUtils.startSession(USERNAME, PASSWORD);
        
        // Lets try and change the password
        ///try
        //{
        //    WebServiceFactory.getAdministrationService().changePassword("user" + one, "badPassword", "newPassword");
        //    fail("This should throw an exception since we have not specified the old password correctly.");
       // }
       // catch (Exception exception)
       // {
       //     // Ignore since we where expecting the exception
       // }
        // "password" + one
        WebServiceFactory.getAdministrationService().changePassword("user" + one, null, "newPassword");
        
        // Now we should try and start a session with the new password
        AuthenticationUtils.endSession();
        AuthenticationUtils.startSession("user" + one, "newPassword");        
    }

    private NamedValue[] createPersonProperties(
            String homeFolder,
            String firstName, 
            String middleName, 
            String lastName, 
            String email,
            String orgId)
    {
        // Create the new user objects
        return new NamedValue[] {
                new NamedValue(Constants.PROP_USER_HOMEFOLDER, false, homeFolder, null),
                new NamedValue(Constants.PROP_USER_FIRSTNAME, false, firstName, null),
                new NamedValue(Constants.PROP_USER_MIDDLENAME, false, middleName, null),
                new NamedValue(Constants.PROP_USER_LASTNAME, false, lastName, null),
                new NamedValue(Constants.PROP_USER_EMAIL, false, email, null),
                new NamedValue(Constants.PROP_USER_ORGID, false, orgId, null)};
    }
    
    public void testCreateUsersWithSameName()
        throws Exception
    {
        // Get the details of the new user
        String homeFolder = store.getScheme() + "://" + store.getAddress() + "/" + folderReference.getUuid();
        String one = Long.toString(System.currentTimeMillis());
        NewUserDetails[] newUsers = new NewUserDetails[] 
        {
                new NewUserDetails(
                        "user" + one, 
                        "password" + one,
                        createPersonProperties(homeFolder, "first" + one, "middle" + one, "last" + one, "email" + one, "org" + one)),
                new NewUserDetails(
                        "user" + one, 
                        "password" + one,
                        createPersonProperties(homeFolder, "first" + one, "middle" + one, "last" + one, "email" + one, "org" + one))
                        
        };
        
        try
        {
            // Create the new users
            WebServiceFactory.getAdministrationService().createUsers(newUsers);
            fail("This should have failed as we attempted to create the same user twice");
        }
        catch (Throwable exception)
        {
            // Ignore as we where expecting an exception
        }        
    }

    public void testCreateUsersMultiThreaded()
    {
        String userName = "user" + Long.toString(System.currentTimeMillis());
        
        CreateUser createUser1 = new CreateUser(userName, AuthenticationUtils.getAuthenticationDetails());
        CreateUser createUser2 = new CreateUser(userName, AuthenticationUtils.getAuthenticationDetails());
        CreateUser createUser3 = new CreateUser(userName, AuthenticationUtils.getAuthenticationDetails());
        
        createUser1.start();
        createUser2.start();
        createUser3.start();
        
       // try {createUser1.join();} catch (InterruptedException e) {}
        
        
        
        System.out.println("testCreateUsersMultiThreaded: " + userName);
    }
    
    private class CreateUser extends Thread
    {
        private String userName;
        private AuthenticationDetails authenticationDetails;
        
        public CreateUser(String userName, AuthenticationDetails authenticationDetails)
        {
            this.userName = userName;
            this.authenticationDetails = authenticationDetails;
        }
        
        public void run()
        {
            try
            {
                // Set the ticket up for this thread
                AuthenticationUtils.setAuthenticationDetails(this.authenticationDetails);
                
                // Get the details of the new user
                String homeFolder = store.getScheme() + "://" + store.getAddress() + "/" + folderReference.getUuid();
                NewUserDetails[] newUsers = new NewUserDetails[] 
                {
                        new NewUserDetails(
                                this.userName, 
                                "password",
                                createPersonProperties( homeFolder, 
                                                        "first" + this.userName, 
                                                        "middle" + this.userName, 
                                                        "last" + this.userName, 
                                                        "email" + this.userName, 
                                                        "org" + this.userName))
                };
                
                // Create the new users
                WebServiceFactory.getAdministrationService().createUsers(newUsers);
                
                System.out.println("Creating user in thread: " + this.userName);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
                throw new RuntimeException("Unable to creat user in thread", exception);
            }
        }
    }
    
}
