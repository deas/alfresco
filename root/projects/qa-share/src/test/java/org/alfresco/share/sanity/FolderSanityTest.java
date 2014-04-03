package org.alfresco.share.sanity;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPopup;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage.ButtonType;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This class contains the sanity tests for folder related functionality.
 * 
 * @author Chiran
 */
@Listeners(FailedTestListener.class)
public class FolderSanityTest extends AbstractTests
{
    private static Log logger = LogFactory.getLog(FolderSanityTest.class);

    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        logger.info("Starting Tests: " + testName);
    }

    /**
     * <ul>
     * <li>Login as testUser</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>open site document library page</li>
     * <li>create test Folder and 2 extra folders for copy and move locations</li>
     * <li>Verify Folder is created</li>
     * <li>Edit Metadata (Properties)</li>
     * <li>Verify Edited successfully</li>
     * <li>Add a tag to Folder</li>
     * <li>Verify Tag added successfully</li>
     * <li>Add Folder to Category</li>
     * <li>Verify added to category successfully</li>
      * <li>Click on tag present on left hand tree</li>
     * <li>Verify only Folder displayed which has that tag</li>
     * <li>Click on Category on left hand tree</li>
     * <li>Only Folder with this category has displayed.</li>
     * <li>Mark the folder as Favourite</li>
     * <li>Verify added as favourite</li>
     * <li>Copy the folder into another folder</li>
     * <li>Verify that folder has copied successfully</li>
     * <li>Move the folder into another folder</li>
     * <li>Verify the folder has moved successfully.</li>
     * <li>Manage Rules for folder: Create any Inbound rule and verify it works</li>
     * <li>Inbound rule is created and works correctly</li>
     * <li>Manage Rules for folder: Create any Updated rule and verify it works</li>
     * <li>Update rule is created and works correctly</li>
     * <li>Manage Rules for folder: Create any Outbound rule and verify it works</li>
     * <li>Outbound rule is created and works correctly</li>
     * <li>Manage Rules for folder: Link to Rule Set and verify it works</li>
     * <li>Folder is linked to Rule set and works correctly</li>
     * <li>Manage Permissions for folder</li>
     * <li>Permissions are changed for folder</li>
     * <li>Manage Aspects for folder</li>
     * <li>Aspects are added for folder</li>
     * <li>View the folder in Alfresco Explorer</li>
     * <li>Folder is opened in Alfresco Explorer</li>
     * <li>Add comment to the folder</li>
     * <li>Comment is added. Comment counter is increased to 1.</li>
     * <li>Edit comment</li>
     * <li>Comment is edited</li>
     * <li>Delete comment</li>
     * <li>Comment is deleted. Comment counter is decreased on 1.</li>
     * <li>Delete the folder</li>
     * <li>Folder is deleted</li>
     * <li>Go to My Dashboard and Site Dashboard activities and ensure all activities are displayed</li>
     * <li>Activities list reflects all changes: Create, Delete folder; Create, Edit, Delete comment</li>
     * </ul>
     */
    @Test(groups = "Sanity")
    public void enterprise40X_6541() throws Exception
    {
        String testName = getTestName();
        
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String testUser = getUserNamePremiumDomain(testName) + System.currentTimeMillis();
        
        String testFolderName = getFolderName(testName);
        String testFolderName1 = getFolderName(testName + "1");
        String copyFolderName = getFolderName(testName + "copy" );
        String moveFolderName = getFolderName(testName + "move" );
        String tempFolderName = getFolderName(testName + "temp" );
        
        String description = "sample description";
        String tagName = "testtag";
        String comment = "comment";
        
        String[] testUserInfo = new String[] { testUser };

        // Create Users
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login as User
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        
        // Create test folder , Copy and Move folders
        DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, testFolderName, "");
        docLibPage = ShareUserSitePage.createFolder(drone, copyFolderName, "");
        docLibPage = ShareUserSitePage.createFolder(drone, moveFolderName, "");
        docLibPage = ShareUserSitePage.createFolder(drone, tempFolderName, "");
        docLibPage = ShareUserSitePage.createFolder(drone, testFolderName1, "");
        
        Assert.assertTrue(docLibPage.isFileVisible(testFolderName));
        
        // TODO: Chiran: Do not get FileDirectoryInfo element as a separate step. Get it everytime the page is refreshed to avoid WD errors
        FileDirectoryInfo contentRow = docLibPage.getFileDirectoryInfo(testFolderName);

        //Edit Properties (Edit metadata)
        //TODO : Chiran: Missing step to add category. 
        // TODO: Chiran: Need to make the below functionality as util method
        EditDocumentPropertiesPopup editDocPropertiesPage = contentRow.selectEditProperties().render();
        editDocPropertiesPage.setDescription(description);
        docLibPage = editDocPropertiesPage.selectSave().render();

        //TODO: Chiran: Need to make the below functionality as util method
        editDocPropertiesPage = contentRow.selectEditProperties().render();
        
        Assert.assertEquals(description, editDocPropertiesPage.getDescription());
        
        editDocPropertiesPage.clickOnCancel();
        
        //Add a tag
        contentRow.addTag(tagName);
        
        // TODO: Please Remove: ALF-19161 Once Defect fixed then will remove the below 2 lines.
        webDriverWait(drone, refreshDuration);
        drone.refresh();
        
        docLibPage = ShareUser.openDocumentLibrary(drone);
        
        // TODO: Chiran: Do not get FileDirectoryInfo element as a separate step.
        contentRow = docLibPage.getFileDirectoryInfo(testFolderName);
        
        List<String> tags = contentRow.getTags();
        Assert.assertTrue(tags.size()>0);
        Assert.assertTrue(tags.contains(tagName));
        
        docLibPage = docLibPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(tagName).render();
        Assert.assertTrue(docLibPage.isFileVisible(testFolderName));
        
        docLibPage = ShareUser.openDocumentLibrary(drone);
        Assert.assertEquals(docLibPage.getFiles().size(), 5);
        
        //TODO:Adding folder to Category
        //TODO:Verify the folder should get displayed when clicked on category 
        
        // Copy the testFolder to copyFolder
        String[] folderPath = {copyFolderName};

        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName, testFolderName, folderPath, true);

        docLibPage = ShareUser.openDocumentLibrary(drone);
        
        Assert.assertTrue(docLibPage.getFiles().size() == 5);

        docLibPage = docLibPage.selectFolder(copyFolderName).render();

        Assert.assertTrue(docLibPage.isFileVisible(testFolderName));
        
        docLibPage = ShareUser.openDocumentLibrary(drone);

        // move the testFolder into moveFolder
        String[] folders = {moveFolderName};

        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName, tempFolderName, folders, false);

        Assert.assertTrue(docLibPage.getFiles().size() == 4);

        docLibPage = docLibPage.selectFolder(moveFolderName).render();

        Assert.assertTrue(docLibPage.isFileVisible(tempFolderName));
        
        docLibPage = ShareUser.openDocumentLibrary(drone);
        
        //Note : Need clarity
        //Create inbound rule 
        // Verify inbound rule is created successfully
        // Verify the rule is working as expected

        //Note : Need clarity
        //Create updated rule 
        // Verify updated rule is created successfully
        // Verify the rule is working as expected
        
        //Note : Need clarity
        //Create outbound rule 
        // Verify outbound rule is created successfully
        // Verify the rule is working as expected
        
        //Note : Need clarity
        // link folder to ruleset
        // Verify link is successful
        // Verify the link is working properly.
        
        // Change Permissions
        contentRow = docLibPage.getFileDirectoryInfo(testFolderName1);
        ManagePermissionsPage managePermissionsPage = contentRow.selectManagePermission().render();
        managePermissionsPage = managePermissionsPage.toggleInheritPermission(false, ButtonType.Yes).render();
        docLibPage = (DocumentLibraryPage) managePermissionsPage.selectSave();
        docLibPage.render();
        managePermissionsPage = contentRow.selectManagePermission().render();
        Assert.assertFalse(managePermissionsPage.isInheritPermissionEnabled());
        
       //Open Doc Lib
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        
        if(!alfrescoVersion.isCloud())
        {
            //Manage Aspects from DetailsPage
            FolderDetailsPage folderDetailsPage = contentRow.selectViewFolderDetails().render();
            List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
            aspects.add(DocumentAspect.ALIASABLE_EMAIL);
            folderDetailsPage = (FolderDetailsPage) ShareUser.addAspects(drone, aspects);
            folderDetailsPage .render();
            
            Assert.assertTrue(folderDetailsPage.getProperties().containsKey("Alias"));
            
            docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        }
        
        //TODO: Webdrone changes are needed.
        //View the folder in alfresco explorer .
        //Verify folder is opened in alfresco explorer.
        
        // Adding comment
        Assert.assertEquals(Integer.valueOf(0), docLibPage.getCommentCount());

        DetailsPage detailsPage = contentRow.selectViewFolderDetails().render();

        detailsPage = detailsPage.addComment(comment).render();

        docLibPage = ShareUser.openDocumentLibrary(drone);
        
        Assert.assertEquals(Integer.valueOf(1), docLibPage.getCommentCount());
        
        detailsPage = contentRow.selectViewFolderDetails().render();

        Assert.assertEquals(1, detailsPage.getCommentCount());

        // Remove comment
        detailsPage.removeComment(comment);

        docLibPage = ShareUser.openDocumentLibrary(drone);
        
        Assert.assertEquals(Integer.valueOf(0), docLibPage.getCommentCount());
        
        //Removing folder from doclib
        docLibPage.deleteItem(testFolderName);
        
        docLibPage = ShareUser.openDocumentLibrary(drone);
        
        Assert.assertFalse(docLibPage.isFileVisible(testFolderName));
        
        //DashBoard Activities
        ShareUser.openUserDashboard(drone);

        String activityEntry = testUser + " LName" + FEED_CONTENT_ADDED + FEED_FOR_FOLDER + testFolderName1 + FEED_LOCATION + siteName;

        Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true));
        
        activityEntry = testUser + " LName" + FEED_CONTENT_DELETED + FEED_FOR_FOLDER + testFolderName + FEED_LOCATION + siteName;

        Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true));

        activityEntry = testUser + " LName" + FEED_COMMENTED_ON + " " + testFolderName1 + FEED_LOCATION + siteName;

        Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true));

        activityEntry = testUser + " LName" + FEED_COMMENT_DELETED + FEED_COMMENTED_FROM + testFolderName1 + FEED_LOCATION + siteName;
        
        Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true));
    }
}