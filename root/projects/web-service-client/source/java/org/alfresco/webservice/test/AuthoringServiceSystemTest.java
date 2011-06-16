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


import org.alfresco.webservice.authoring.AuthoringServiceSoapBindingStub;
import org.alfresco.webservice.authoring.CancelCheckoutResult;
import org.alfresco.webservice.authoring.CheckinResult;
import org.alfresco.webservice.authoring.CheckoutResult;
import org.alfresco.webservice.authoring.LockStatus;
import org.alfresco.webservice.authoring.LockTypeEnum;
import org.alfresco.webservice.authoring.VersionResult;
import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Version;
import org.alfresco.webservice.types.VersionHistory;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuthoringServiceSystemTest extends BaseWebServiceSystemTest
{
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(AuthoringServiceSystemTest.class);

    private static final String INITIAL_VERSION_CONTENT = "Content of the initial version";
    private static final String SECOND_VERSION_CONTENT = "The content for the second version is completely different";
    
    private static final String VALUE_DESCRIPTION = "description";

    private AuthoringServiceSoapBindingStub authoringService;

    public AuthoringServiceSystemTest()
    {
        this.authoringService = WebServiceFactory.getAuthoringService();
    }
    
    /**
     * Tests the checkout service method
     * 
     * @throws Exception
     */
    public void testCheckout() throws Exception
    {
        doCheckOut();
        
        // TODO test multiple checkout
    }
    
    /**
     * Reusable method to do a standard checkout
     * 
     * @return
     * @throws Exception
     */
    private Reference doCheckOut() throws Exception
    {
        // Use the helper to create the verionable node
        Reference reference = createContentAtRoot("version_test.txt", INITIAL_VERSION_CONTENT);        
        Predicate predicate = convertToPredicate(reference);

        // Check the content out (to the same detination)
        CheckoutResult result = this.authoringService.checkout(predicate, null);
        assertNotNull(result);
        assertEquals(1, result.getOriginals().length);
        assertEquals(1, result.getWorkingCopies().length);
        
        // TODO need to check that the working copy and the origional are in the correct states ...
        
        return result.getWorkingCopies()[0];
    }

    /**
     * Tests the checkout service method passing a destination for the working
     * copy
     * 
     * @throws Exception
     */
    public void xtestCheckoutWithDestination() throws Exception
    {
        Reference reference = createContentAtRoot("version_test.txt", INITIAL_VERSION_CONTENT);
        Predicate predicate = convertToPredicate(reference);        
        ParentReference parentReference = getFolderParentReference("{" + Constants.NAMESPACE_CONTENT_MODEL + "}workingCopy");
        
        // Checkout the content to the folder
        CheckoutResult result = this.authoringService.checkout(predicate, parentReference);
        assertNotNull(result);
        assertEquals(1, result.getOriginals().length);
        assertEquals(1, result.getWorkingCopies().length);
        
        // TODO need to check that the working copy and the origional are in the correct states
    }

    /**
     * Tests the checkin service method
     * 
     * @throws Exception
     */
    public void testCheckin() throws Exception
    {
        // First we need to check a document out
        Reference workingCopy = doCheckOut();
        
        // Check in but keep checked out
        Predicate predicate = convertToPredicate(workingCopy);
        NamedValue[] comments = getVersionComments();
        CheckinResult checkinResult = this.authoringService.checkin(predicate, comments, true);
        
        // Check the result
        assertNotNull(checkinResult);
        assertEquals(1, checkinResult.getCheckedIn().length);
        assertEquals(1, checkinResult.getWorkingCopies().length);
        // TODO check that state of the orig and working copies
        
        // Checkin but don't keep checked out
        Predicate predicate2 = convertToPredicate(checkinResult.getWorkingCopies()[0]);
        CheckinResult checkinResult2 = this.authoringService.checkin(predicate2, comments, false);
        
        // Check the result
        assertNotNull(checkinResult2);
        assertEquals(1, checkinResult2.getCheckedIn().length);
        assertNull(checkinResult2.getWorkingCopies());
        // TODO check the above behaviour ...
        // TODO check that the state of the org and working copies
        
        // TODO check multiple checkin
    }

    /**
     * Helper method to get a list of version comments
     * 
     * @return
     */
    private NamedValue[] getVersionComments()
    {
        NamedValue[] comments = new NamedValue[1];
        comments[0] = new NamedValue("description", false, VALUE_DESCRIPTION, null);
        return comments;
    }

    /**
     * Tests the checkinExternal service method
     * 
     * @throws Exception
     */
    public void testCheckinExternal() throws Exception
    {
        // First we need to check a document out
        Reference workingCopy = doCheckOut();
        
        // Check in with external content
        NamedValue[] comments = getVersionComments();
        ContentFormat contentFormat = new ContentFormat(Constants.MIMETYPE_TEXT_PLAIN, "UTF-8");
        Reference origionalNode = this.authoringService.checkinExternal(workingCopy, comments, false, contentFormat, SECOND_VERSION_CONTENT.getBytes());
        
        // Check the origianl Node
        assertNotNull(origionalNode);
        Content[] contents = this.contentService.read(new Predicate(new Reference[]{origionalNode}, BaseWebServiceSystemTest.store, null), Constants.PROP_CONTENT.toString());
        Content readResult = contents[0];
        assertNotNull(readResult);
        String checkedInContent = ContentUtils.getContentAsString(readResult);
        assertNotNull(checkedInContent);
        assertEquals(SECOND_VERSION_CONTENT, checkedInContent);
    }

    /**
     * Tests the cancelCheckout service method
     * 
     * @throws Exception
     */
    public void testCancelCheckout() throws Exception
    {
        // Check out a node
        Reference workingCopy = doCheckOut();
        
        // Cancel the check out
        Predicate predicate = convertToPredicate(workingCopy);
        CancelCheckoutResult result = this.authoringService.cancelCheckout(predicate);
        
        // Check the result
        assertNotNull(result);
        assertEquals(1, result.getOriginals().length);
        // TODO check that state of the orig and that the working copy has been deleted
        
        // TODO I don't think that the working copies should be returned in the result since they have been deleted !!
    }

    /**
     * Tests the lock service methods, lock, unlock and getStaus
     * 
     * @throws Exception
     */
    public void testLockUnLockGetStatus() throws Exception
    {
        Reference reference = createContentAtRoot("lock_test1.txt", INITIAL_VERSION_CONTENT);        
        Predicate predicate = convertToPredicate(reference);
        
        // Get the status 
        checkLockStatus(predicate, null, null);
        
        // Lock with a write lock
        Reference[] lockedRefs = this.authoringService.lock(predicate, false, LockTypeEnum.write);
        assertNotNull(lockedRefs);
        assertEquals(1, lockedRefs.length);        
        // TODO check in more detail
        
        // Get the status
        checkLockStatus(predicate, USERNAME, LockTypeEnum.write);
        
        // Unlock (bad)
//        try
//        {
//            this.authoringService.unlock(predicate, "bad", false);
//            fail("This should have thrown an exception.");
//        }
//        catch (Throwable exception)
//        {
//            // Good .. we where expceting this
//        }
        
        // Unlock (good)
        Reference[] unlocked = this.authoringService.unlock(predicate, false);
        assertNotNull(unlocked);
        assertEquals(1, unlocked.length);
        
        // Get the status
        checkLockStatus(predicate, null, null);
        
        // Read lock
        Reference[] lockedRefs2 = this.authoringService.lock(predicate, false, LockTypeEnum.read);
        
        assertNotNull(lockedRefs2);
        assertEquals(1, lockedRefs2.length);
        // TODO check in more detail
        
        // Get the status
        checkLockStatus(predicate, USERNAME, LockTypeEnum.read);
    }
    
    private void checkLockStatus(Predicate predicate, String lockOwner, LockTypeEnum lockType)
        throws Exception
    {
        LockStatus[] lockStatus1 = this.authoringService.getLockStatus(predicate);
        assertNotNull(lockStatus1);
        assertEquals(1, lockStatus1.length);
        LockStatus ls1 = lockStatus1[0];
        assertNotNull(ls1);
        assertEquals(lockOwner, ls1.getLockOwner());
        assertEquals(lockType, ls1.getLockType());
    }

    /**
     * Tests the createVersion service method
     * 
     * @throws Exception
     */
    public void testVersionMethods() throws Exception
    {
        Reference reference = createContentAtRoot("create_version_test.txt", INITIAL_VERSION_CONTENT);        
        Predicate predicate = convertToPredicate(reference);
        
        // Get the version history (before its been versioned)
        VersionHistory emptyVersionHistory = this.authoringService.getVersionHistory(reference);
        assertNotNull(emptyVersionHistory);
        assertNull(emptyVersionHistory.getVersions());
        
        // Create the minor version 0.1
        VersionResult result = this.authoringService.createVersion(predicate, getVersionComments(), false);        
        assertNotNull(result);
        assertEquals(1, result.getNodes().length);
        assertEquals(1, result.getVersions().length);
        Version version = result.getVersions()[0];
        assertEquals("0.1", version.getLabel());
        // TODO check commentaries
        // TODO check creator
        
        // Get the version history create minor version 0.2
        this.authoringService.createVersion(predicate, getVersionComments(), false);
        VersionHistory versionHistory = this.authoringService.getVersionHistory(reference);
        assertNotNull(versionHistory);
        assertEquals(2, versionHistory.getVersions().length);
        // TODO some more tests ...
        
        // Update the content
        this.contentService.write(reference, Constants.PROP_CONTENT, SECOND_VERSION_CONTENT.getBytes(), null);
        
        // Create another version 0.4
        VersionResult versionResult2 = this.authoringService.createVersion(predicate, getVersionComments(), false);
        assertNotNull(versionResult2);
        assertEquals(1, versionResult2.getNodes().length);
        assertEquals(1, versionResult2.getVersions().length);
        Version version2 = versionResult2.getVersions()[0];
        assertEquals("0.4", version2.getLabel());
        // TODO check commentaries
        // TODO check creator
        
        // Check the version history
        VersionHistory versionHistory2 = this.authoringService.getVersionHistory(reference);
        assertNotNull(versionHistory2);
        assertEquals(4, versionHistory2.getVersions().length);
        // TODO some more tests ...
        
        // Create a major version (1.0)
        NamedValue versionVal = Utils.createNamedValue("versionType","MAJOR");
        NamedValue descriptionVal = Utils.createNamedValue("description", "new description");
        NamedValue[] comments = new NamedValue[]{versionVal,descriptionVal};
        VersionResult result5 = this.authoringService.createVersion(predicate, comments, false);        
        assertNotNull(result5);
        assertEquals(1, result5.getNodes().length);
        assertEquals(1, result5.getVersions().length);
        Version version5 = result5.getVersions()[0];
        assertEquals("1.0", version5.getLabel());
        
        // Confirm the current content of the node
        Content[] contents = this.contentService.read(new Predicate(new Reference[]{reference}, BaseWebServiceSystemTest.store, null), Constants.PROP_CONTENT.toString());
        Content readResult1 = contents[0];
        String content1 = ContentUtils.getContentAsString(readResult1);
        assertEquals(SECOND_VERSION_CONTENT, content1);
        
        // Revert the node to the first version
        this.authoringService.revertVersion(reference, "0.1");
        
        // Confirm that the state of the node has been reverted
        Content[] contents2 = this.contentService.read(new Predicate(new Reference[]{reference}, BaseWebServiceSystemTest.store, null), Constants.PROP_CONTENT.toString());
        Content readResult2 = contents2[0];
        String content2 = ContentUtils.getContentAsString(readResult2);
        assertEquals(INITIAL_VERSION_CONTENT, content2);
        
        // Now delete the version history
        VersionHistory deletedVersionHistory = this.authoringService.deleteAllVersions(reference);
        assertNotNull(deletedVersionHistory);
        assertNull(deletedVersionHistory.getVersions());
        
        // Check the version history
        VersionHistory versionHistory3 = this.authoringService.getVersionHistory(reference);
        assertNotNull(versionHistory3);
        assertNull(versionHistory3.getVersions());    
        
       
    }
}
