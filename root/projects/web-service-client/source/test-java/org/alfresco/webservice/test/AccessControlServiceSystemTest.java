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

import org.alfresco.webservice.accesscontrol.ACE;
import org.alfresco.webservice.accesscontrol.ACL;
import org.alfresco.webservice.accesscontrol.AccessControlServiceSoapBindingStub;
import org.alfresco.webservice.accesscontrol.AccessStatus;
import org.alfresco.webservice.accesscontrol.AuthorityFilter;
import org.alfresco.webservice.accesscontrol.GetClassPermissionsResult;
import org.alfresco.webservice.accesscontrol.GetPermissionsResult;
import org.alfresco.webservice.accesscontrol.HasPermissionsResult;
import org.alfresco.webservice.accesscontrol.NewAuthority;
import org.alfresco.webservice.accesscontrol.OwnerResult;
import org.alfresco.webservice.accesscontrol.SiblingAuthorityFilter;
import org.alfresco.webservice.administration.NewUserDetails;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/** 
 * @author Roy Wetherall
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccessControlServiceSystemTest extends BaseWebServiceSystemTest
{
    private static Log logger = LogFactory.getLog(AccessControlServiceSystemTest.class);
    
    private String userName1 = null;
    private String userName2 = null;
    
    private AccessControlServiceSoapBindingStub accessControlService = WebServiceFactory.getAccessControlService();
    
    private void createUsers() throws Exception
    {
        this.userName1 = "user1" + System.currentTimeMillis();
        this.userName2 = "user2" + System.currentTimeMillis();
        
        // Create some users we can user in the tests
        String homeFolder = store.getScheme() + "://" + store.getAddress() + "/" + folderReference.getUuid();      
        NewUserDetails[] newUsers = new NewUserDetails[] {
                new NewUserDetails(
                        this.userName1, 
                        "password",
                        createPersonProperties(homeFolder, "first", "middle", "last", "email", "org")),
                new NewUserDetails(
                        this.userName2, 
                        "password",
                        createPersonProperties(homeFolder, "first", "middle", "last", "email", "org")) };

        // Create the new users
        WebServiceFactory.getAdministrationService().createUsers(newUsers);
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
                new NamedValue(Constants.PROP_USER_ORGID, false, orgId, null) };
    }
    
    private void removeUsers() throws Exception
    {
        String[] userNames = new String[]{this.userName1, this.userName2};
        WebServiceFactory.getAdministrationService().deleteUsers(userNames);       
    }
    
    /**
     * Test getting, setting and removing permissions
     */
    public void test1GetSetRemoveACEs() throws Exception
    {
        // Resolve the predicate and create the test users
        Predicate predicate = new Predicate(new Reference[]{BaseWebServiceSystemTest.contentReference}, null, null);
        createUsers();
        
        // Get the ACL for the content node
        ACL[] acls = this.accessControlService.getACLs(predicate, null);
        assertNotNull(acls);
        assertEquals(1, acls.length);
        
        // Check the details of the ace returned
        ACL acl = acls[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), acl.getReference().getUuid());
        assertEquals(true, acl.isInheritPermissions());
        assertNull(acl.getAces());
        
        // Add some acls to the content
        ACE[] aces1 = new ACE[]
        {
           new ACE(this.userName1, Constants.READ, AccessStatus.acepted),
           new ACE(this.userName2, Constants.WRITE, AccessStatus.acepted)
        };
        ACL[] acls1 = this.accessControlService.addACEs(predicate, aces1);
        
        // Check the details of the addACE result
        assertNotNull(acls1);
        assertEquals(1, acls1.length);
        ACL acl1 = acls1[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), acl1.getReference().getUuid());
        assertEquals(true, acl1.isInheritPermissions());
        assertNotNull(acl1.getAces());
        assertEquals(2, acl1.getAces().length);
        for (ACE ace1 : acl1.getAces())
        {
            if (ace1.getAuthority().equals(this.userName1) == true)
            {
                assertEquals(Constants.READ, ace1.getPermission());
                assertEquals(AccessStatus.acepted, ace1.getAccessStatus());
            }
            else if (ace1.getAuthority().equals(this.userName2) == true)
            {
                assertEquals(Constants.WRITE, ace1.getPermission());
                assertEquals(AccessStatus.acepted, ace1.getAccessStatus());
            }
            else
            {
                fail("I wasn't expecting anything else here");
            }
        }
        
        // Double check the get works
        ACL[] acls3 = this.accessControlService.getACLs(predicate, null);
        assertNotNull(acls3);
        assertEquals(1, acls3.length);
        assertNotNull(acls3[0].getAces());
        assertEquals(2, acls3[0].getAces().length);
        
        // Remove an ACE
        ACE[] aces2 = new ACE[]
          {
             new ACE(this.userName1, Constants.READ, AccessStatus.acepted)
          };
        ACL[] acls4 = this.accessControlService.removeACEs(predicate, aces2);
        assertNotNull(acls4);
        assertEquals(1, acls4.length);
        assertNotNull(acls4[0].getAces());
        assertEquals(1, acls4[0].getAces().length);
        
        // Double check get
        ACL[] acls5 = this.accessControlService.getACLs(predicate, null);
        assertNotNull(acls5);
        assertEquals(1, acls5.length);
        assertNotNull(acls5[0].getAces());
        assertEquals(1, acls5[0].getAces().length);
        
        // Remove all
        ACL[] acls6 = this.accessControlService.removeACEs(predicate, null);
        assertNotNull(acls6);
        assertEquals(1, acls6.length);
        assertNull(acls6[0].getAces());
        
        // Remove the users added
        removeUsers();
    }
    
    /**
     * Test getPermissions
     * 
     * @throws Exception
     */
    public void test2GetPermissions() throws Exception
    {
        // Create predicate
        Predicate predicate = new Predicate(new Reference[]{BaseWebServiceSystemTest.contentReference}, null, null);
        
        // Get the permissions that can be set
        GetPermissionsResult[] results = this.accessControlService.getPermissions(predicate);
        
        // Check the result
        assertNotNull(results);
        assertEquals(1, results.length);
        GetPermissionsResult result = results[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), result.getReference().getUuid());
        assertNotNull(result.getPermissions());
               
        if (logger.isDebugEnabled() == true)
        {
            System.out.println("Node permissions:");
            for (String permission : result.getPermissions())
            {
                System.out.println(permission);
            }
            System.out.println("\n");
        }
    }
    
    /**
     * Test getClassPermissions
     * 
     * @throws Exception
     */
    public void test3GetClassPermissions() throws Exception
    {
        // Get the permissions that can be set
        GetClassPermissionsResult[] results = this.accessControlService.getClassPermissions(new String[]{Constants.TYPE_FOLDER});
        
        // Check the result
        assertNotNull(results);
        assertEquals(1, results.length);
        GetClassPermissionsResult result = results[0];
        assertEquals(Constants.TYPE_FOLDER, result.getClassName());
        assertNotNull(result.getPermissions());
               
        if (logger.isDebugEnabled() == true)
        {
            System.out.println("Class permissions:");
            for (String permission : result.getPermissions())
            {
                System.out.println(permission);
            }
            System.out.println("\n");
        }
        
    }
    
    /**
     * Test hasPermissions
     * 
     * @throws Exception
     */
    public void test4HasPermissions() throws Exception
    {
        Predicate predicate = convertToPredicate(BaseWebServiceSystemTest.contentReference);
        
        HasPermissionsResult[] results = this.accessControlService.hasPermissions(predicate, new String[]{Constants.WRITE});
        assertNotNull(results);
        assertEquals(1, results.length);
        
        HasPermissionsResult result = results[0];
        assertEquals(Constants.WRITE, result.getPermission());
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), result.getReference().getUuid());
        assertEquals(AccessStatus.acepted, result.getAccessStatus());
    }
    
    /**
     * Test setInheritPermissions
     * 
     * @throws Exception
     */
    public void test5SetInheritPermissions() throws Exception
    {
        ACL[] acls = this.accessControlService.setInheritPermission(convertToPredicate(BaseWebServiceSystemTest.contentReference), false);
        assertNotNull(acls);
        assertEquals(1, acls.length);
        ACL acl = acls[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), acl.getReference().getUuid());
        assertFalse(acl.isInheritPermissions());
    }
    
    /**
     * Test setOwnable and getOwnable
     * @throws Exception
     */
    public void test6SetGetOwnable() throws Exception
    {
        // Create a couple of users
        createUsers();
        
        // Check the current owner
        OwnerResult[] results = this.accessControlService.getOwners(convertToPredicate(BaseWebServiceSystemTest.contentReference));
        assertNotNull(results);
        assertEquals(1, results.length);
        OwnerResult result = results[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), result.getReference().getUuid());
        assertEquals("admin", result.getOwner());
        
        // Reset the owner
        OwnerResult[] results2 = this.accessControlService.setOwners(convertToPredicate(BaseWebServiceSystemTest.contentReference), this.userName1);
        assertNotNull(results2);
        assertEquals(1, results2.length);
        OwnerResult result2 = results2[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), result2.getReference().getUuid());
        assertEquals(this.userName1, result2.getOwner());        
        
        // Remove the created users
        removeUsers();      
    }
    
    // Authorities unit test summary ...
    // -> create root groups
    // -> create sub-groups
    // -> getAll (sub-groups and all)
    // -> addAuthroity (groups and users)
    // -> getAuthorities for a the current user
    // -> getParentAuthorites
    // -> getChildAuthorities
    // -> remove Authorities (groups and users)
    // -> delete groups
    
    public void test7CreateAuthorities()
    	throws Exception
    {
    	// Create two root authorities ...
    	//
    	//  -> rootGroupOne
    	//  -> rootGroupTwo
    	
    	String shortName1 = getShortName("rootGroupOne");
    	String shortName2 = getShortName("rootGroupTwo");
    	NewAuthority newAuthOne = new NewAuthority("GROUP", shortName1);
    	NewAuthority newAuthTwo = new NewAuthority("GROUP", shortName2);
    	NewAuthority[] newAuthorities = new NewAuthority[]{newAuthOne, newAuthTwo};
    	
    	String[] result = this.accessControlService.createAuthorities(null, newAuthorities);
    	
    	assertNotNull(result);
    	assertEquals(2, result.length);
    	String rootGroupOne = result[0];
    	String rootGroupTwo = result[1];
    	assertEquals("GROUP_" + shortName1, rootGroupOne);
    	assertEquals("GROUP_" + shortName2, rootGroupTwo);
    	
    	// Create sub-groups under rootGroupOne ....
    	//
    	//  -> groupOneA
    	//  -> groupOneB
    	
    	String shortName3 = getShortName("groupOneA");
    	String shortName4 = getShortName("groupOneB");
    	NewAuthority newAuthOneA = new NewAuthority("GROUP", shortName3);
    	NewAuthority newAuthOneB = new NewAuthority("GROUP", shortName4);
    	NewAuthority[] newAuthorities2 = new NewAuthority[]{newAuthOneA, newAuthOneB};
    	
    	String[] result2 = this.accessControlService.createAuthorities(rootGroupOne, newAuthorities2);
    	
    	assertNotNull(result2);
    	assertEquals(2, result2.length);
    	String groupOneA = result2[0];
    	String groupOneB = result2[1];
    	assertEquals("GROUP_" + shortName3, groupOneA);
    	assertEquals("GROUP_" + shortName4, groupOneB);
    	
    	// Create sub-groups under rootGroupTwo ...
    	//
    	//  -> groupTwoA
    	//  -> groupTwoB
    	
    	String shortName5 = getShortName("groupTwoA");
    	String shortName6 = getShortName("groupTwoB");
    	NewAuthority newAuthTwoA = new NewAuthority("GROUP", shortName5);
    	NewAuthority newAuthTwoB = new NewAuthority("GROUP", shortName6);
    	NewAuthority[] newAuthorities3 = new NewAuthority[]{newAuthTwoA, newAuthTwoB};
    	
    	String[] result3 = this.accessControlService.createAuthorities(rootGroupTwo, newAuthorities3);
    	
    	assertNotNull(result3);
    	assertEquals(2, result3.length);
    	String groupTwoA = result3[0];
    	String groupTwoB = result3[1];
    	assertEquals("GROUP_" + shortName5, groupTwoA);
    	assertEquals("GROUP_" + shortName6, groupTwoB);    	
    
    	// Get all groups (any level)
    	
    	AuthorityFilter filter = new AuthorityFilter("GROUP", false);
    	String[] result4 = this.accessControlService.getAllAuthorities(filter);
    	assertNotNull(result4);
    	if (result4.length < 6)
    	{
    		fail("We where expecting at least 6 groups to be returned from the getAllAuthorities search.");
    	}
    	assertTrue(arrayContains(result4, rootGroupOne));
    	assertTrue(arrayContains(result4, rootGroupTwo));
    	assertTrue(arrayContains(result4, groupOneA));
    	assertTrue(arrayContains(result4, groupOneB));
    	assertTrue(arrayContains(result4, groupTwoA));
    	assertTrue(arrayContains(result4, groupTwoB));
    	
    	// Get all groups root level
    	
    	AuthorityFilter filter2 = new AuthorityFilter("GROUP", true);
    	String[] result5 = this.accessControlService.getAllAuthorities(filter2);
    	assertNotNull(result5);
    	if (result5.length < 2)
    	{
    		fail("We where expecting at least 2 groups to be returned from the getAllAuthorities search.");
    	}
    	assertTrue(arrayContains(result5, rootGroupOne));
    	assertTrue(arrayContains(result5, rootGroupTwo));
    	assertFalse(arrayContains(result5, groupOneA));
    	assertFalse(arrayContains(result5, groupOneB));
    	assertFalse(arrayContains(result5, groupTwoA));
    	assertFalse(arrayContains(result5, groupTwoB));
    	
    	// Add the users as children of ...
    	//
    	// -> groupOneA
    	// -> rootGroupTwo
    	
    	createUsers();
    	assertNotNull(this.userName1);
    	assertNotNull(this.userName2);
    	String[] users = new String[]{this.userName1, this.userName2};
    	String[] result6 = this.accessControlService.addChildAuthorities(groupOneA, users);
    	assertNotNull(result6);
    	assertEquals(2, result6.length);
    	assertEquals(this.userName1, result6[0]);
    	assertEquals(this.userName2, result6[1]);
    	
    	String[] result7 = this.accessControlService.addChildAuthorities(rootGroupTwo, users);
    	assertNotNull(result7);
    	assertEquals(2, result7.length);
    	assertEquals(this.userName1, result7[0]);
    	assertEquals(this.userName2, result7[1]);
    	
    	// Switch authentication to userName1 and check which authorities they belong to
    	
    	AuthenticationUtils.startSession(this.userName1, "password");
    	String[] result8 = this.accessControlService.getAuthorities();
    	assertNotNull(result8);
    	if (result8.length < 3)
    	{
    		fail("We where expecting the user to be in at least 3 gropus");
    	}
    	assertTrue(arrayContains(result8, rootGroupOne));
    	assertTrue(arrayContains(result8, rootGroupTwo));
    	assertTrue(arrayContains(result8, groupOneA));
    	assertFalse(arrayContains(result8, groupOneB));
    	assertFalse(arrayContains(result8, groupTwoA));
    	assertFalse(arrayContains(result8, groupTwoB));
    	AuthenticationUtils.startSession(USERNAME, PASSWORD);    	
    	
    	// Get the parent authorities of ...
    	//
    	// -> groupOneA	(immediate = false)
    	// -> userOne 	(immediate = true)
    	
    	SiblingAuthorityFilter filter3 = new SiblingAuthorityFilter("GROUP", true);
    	String[] result9 = this.accessControlService.getParentAuthorities(groupOneA, filter3);
    	assertNotNull(result9);
    	assertEquals(1, result9.length);
    	assertEquals(rootGroupOne, result9[0]);
    	
    	SiblingAuthorityFilter filter4 = new SiblingAuthorityFilter("GROUP", false);
    	String[] result10 = this.accessControlService.getParentAuthorities(this.userName1, filter4);
    	assertNotNull(result10);

    	if (result8.length < 3)
    	{
    		fail("We where expecting the user to have at least 3 parent groups");
    	}
    	assertTrue(arrayContains(result8, rootGroupOne));
    	assertTrue(arrayContains(result8, rootGroupTwo));
    	assertTrue(arrayContains(result8, groupOneA));
    	assertFalse(arrayContains(result8, groupOneB));
    	assertFalse(arrayContains(result8, groupTwoA));
    	assertFalse(arrayContains(result8, groupTwoB));
    	
    	// Get the child authorities of ...
    	// 
    	// -> groupOneA (immediate = true, type = "USER")
    	// -> rootGroupOne (immediate = false, type= "GROUP")
    	
    	SiblingAuthorityFilter filter5 = new SiblingAuthorityFilter("USER", true);
    	String[] result11 = this.accessControlService.getChildAuthorities(groupOneA, filter5);
    	assertNotNull(result11);
    	assertEquals(2, result11.length);
    	assertTrue(arrayContains(result11, this.userName1));
    	assertTrue(arrayContains(result11, this.userName2));
    	
    	SiblingAuthorityFilter filter6 = new SiblingAuthorityFilter("GROUP", false);
    	String[] result12 = this.accessControlService.getChildAuthorities(rootGroupOne, filter6);
    	assertNotNull(result12);
    	assertEquals(2, result12.length);
    	assertTrue(arrayContains(result12, groupOneA));
    	assertTrue(arrayContains(result12, groupOneB));
    	
    	// Get the children of a group that has no children
    	
    	SiblingAuthorityFilter filter7 = new SiblingAuthorityFilter("GROUP", false);
    	String[] result13 = this.accessControlService.getChildAuthorities(groupOneA, filter7);
    	assertNull(result13);
    	
    	// Remove the users from groupOneA
    	
    	this.accessControlService.removeChildAuthorities(groupOneA, users);
    	String[] result14 = this.accessControlService.getChildAuthorities(groupOneA, filter5);
    	assertNull(result14);  
    	
    	// Delete a group
    	
    	String[] toDelete = new String[]{groupOneA, groupOneB};
    	this.accessControlService.deleteAuthorities(toDelete);
    	String[] result15 = this.accessControlService.getChildAuthorities(rootGroupOne, filter6);
    	assertNull(result15);    	
    	
    }
    
    // NOTE: not the best way to check an array (especially since this will get called over and over again, but
    //       as we are in a unit test will do as its quick to implement and reliable
    private boolean arrayContains(String[] array, String value)
    {
    	boolean result = false;
    	for (String string : array) 
    	{
    		if (string.equals(value) == true)
    		{
    			result = true;
    			break;
    		}
		}    	
    	return result;
    }
    
    // Calculate the short name that is near enought unique
    private int counter = 0;
    private String getShortName(String baseName)
    {
    	this.counter++;
    	return baseName + System.nanoTime() + this.counter;    	
    }
}
