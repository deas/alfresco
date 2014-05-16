package org.alfresco.share.site.document;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskInfo;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author sergey.kardash on 3/10/14.
 */
@Listeners(FailedTestListener.class)
public class ManageSelectedItemsTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(ManageSelectedItemsTests.class);
    protected String testUser;
    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_Enterprise40x_13842() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

    }

    // Folders - View the actions
    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_13842() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderName1 = "First_" + getFolderName(testName);
        String folderName2 = "Second_" + getFolderName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.openDocumentLibrary(drone);

        // Creating folder
        ShareUserSitePage.createFolder(drone, folderName1, null);

        // Creating folder
        ShareUserSitePage.createFolder(drone, folderName2, null);

        // Select one folder. Verify the Selected Items drop-down
        DocumentLibraryPage docLibPage = ShareUser.selectContentCheckBox(drone, folderName1);

        // The following actions are available: Copy to, Move to, Delete, Deselect All, For 4.2 Download as ZIp
        Assert.assertTrue(docLibPage.getNavigation().isSelectedItemMenuCorrectForFolder(), "The following actions aren't available:"
                + " Copy to, Move to, Delete, Deselect All, For 4.2 Download as ZIp");

        // Select several folder. Verify the Selected Items drop-down
        docLibPage = ShareUser.selectContentCheckBox(drone, folderName2);

        // The following actions are available: Copy to, Move to, Delete, Deselect All, For 4.2 Download as ZIp
        Assert.assertTrue(docLibPage.getNavigation().isSelectedItemMenuCorrectForFolder(), "The following actions aren't available: "
                + "Copy to, Move to, Delete, Deselect All, For 4.2 Download as ZIp");

    }

    // Folders - Copy to
    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_13844() throws Exception
    {

        String testName = getTestName();
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderParent = "parent_" + getFolderName(testName + System.currentTimeMillis());
        String copyFolder = "copy_" + getFolderName(testName + System.currentTimeMillis());
        String folderName1 = "First_" + getFolderName(testName + System.currentTimeMillis());
        String folderName2 = "Second_" + getFolderName(testName + System.currentTimeMillis());
        String[] copyFolderPath = { copyFolder };

        // Login
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Any site was created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.openDocumentLibrary(drone);

        // Creating folder
        ShareUserSitePage.createFolder(drone, copyFolder, null);
        ShareUserSitePage.createFolder(drone, folderParent, null);

        DocumentLibraryPage documentLibPage;
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        // At least 2 folders a were created within the site Document Library
        ShareUser.createFolderInFolder(drone, folderName1, null, DOCLIB_CONTAINER + SLASH + folderParent);
        ShareUser.createFolderInFolder(drone, folderName2, null, DOCLIB_CONTAINER + SLASH + folderParent);

        // Several folders are selected
        // Click Copy to from Selected items drop down
        // Select the destination (e.g. Sites) and click Cancel
        ShareUserSitePage.copyOrMoveToFolderAllSelectedItems(drone, siteName, copyFolderPath, true, false);

        // The dialog closes. The folders are not copied (verify the destination folder)
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        Assert.assertTrue(documentLibPage.isFileVisible(folderParent), "Verifying the parent folder is present in site doclib");

        // Navigating to path folder
        documentLibPage.selectFolder(copyFolder).render();
        Assert.assertFalse(documentLibPage.isFileVisible(folderName1), "Verifying copied folder1 isn't present in site doclib/copyFolder_path");
        Assert.assertFalse(documentLibPage.isFileVisible(folderName2), "Verifying copied folder2 isn't present in site doclib/copyFolder_path");

        // Navigating to parent folder
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        documentLibPage.selectFolder(folderParent).render();

        // Select several folders again and click Copy to from Selected Items drop-down
        Assert.assertTrue(documentLibPage.isFileVisible(folderName1), "Verifying folder1 is present in site doclib / parent folder");
        Assert.assertTrue(documentLibPage.isFileVisible(folderName2), "Verifying folder2 is present in site doclib / parent folder");

        // Select the destination (e.g. Sites) and click Copy
        ShareUserSitePage.copyOrMoveToFolderAllSelectedItems(drone, siteName, copyFolderPath, true, true);

        // Navigate to the destination and verify the folders are copied
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        documentLibPage = documentLibPage.selectFolder(copyFolder).render();
        // The folders are copied
        Assert.assertTrue(documentLibPage.isFileVisible(folderName1), "Verifying copied folder1 is present in site doclib/copyPath");
        Assert.assertTrue(documentLibPage.isFileVisible(folderName2), "Verifying copied folder2 is present in site doclib/copyPath");
    }

    // Folders - Move to
    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_13845() throws Exception
    {

        String testName = getTestName();
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderParent = "parent_" + getFolderName(testName + System.currentTimeMillis());
        String moveFolder = "move_" + getFolderName(testName + System.currentTimeMillis());
        String folderName1 = "First_" + getFolderName(testName + System.currentTimeMillis());
        String folderName2 = "Second_" + getFolderName(testName + System.currentTimeMillis());
        String[] moveFolderPath = { moveFolder };

        // Login
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Any site was created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.openDocumentLibrary(drone);

        // Creating folder
        ShareUserSitePage.createFolder(drone, moveFolder, null);
        ShareUserSitePage.createFolder(drone, folderParent, null);

        DocumentLibraryPage documentLibPage;
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        // At least 2 folders a were created within the site Document Library
        ShareUser.createFolderInFolder(drone, folderName1, null, DOCLIB_CONTAINER + SLASH + folderParent);
        ShareUser.createFolderInFolder(drone, folderName2, null, DOCLIB_CONTAINER + SLASH + folderParent);

        // Several folders are selected
        // Click Move to from Selected items drop down
        // Select the destination (e.g. Sites) and click Cancel
        ShareUserSitePage.copyOrMoveToFolderAllSelectedItems(drone, siteName, moveFolderPath, false, false);

        // The dialog closes. The folders aren't moved (verify the destination)
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        Assert.assertTrue(documentLibPage.isFileVisible(folderParent), "Verifying the parent folder is present in site doclib");

        documentLibPage.selectFolder(moveFolder).render();
        Assert.assertFalse(documentLibPage.isFileVisible(folderName1), "Verifying moved folder1 isn't present in site doclib/movePath");
        Assert.assertFalse(documentLibPage.isFileVisible(folderName2), "Verifying moved folder2 isn't present in site doclib/movePath");

        // Navigating to parent folder
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        documentLibPage.selectFolder(folderParent).render();

        // Select several folders again and click Move to from Selected Items drop-down
        Assert.assertTrue(documentLibPage.isFileVisible(folderName1), "Verifying folder1 is present in site doclib / parent folder");
        Assert.assertTrue(documentLibPage.isFileVisible(folderName2), "Verifying folder2 is present in site doclib / parent folder");

        // Select the destination (e.g. Sites) and click Move
        ShareUserSitePage.copyOrMoveToFolderAllSelectedItems(drone, siteName, moveFolderPath, false, true);

        // Navigate to the destination and verify the folders are moved
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        documentLibPage = documentLibPage.selectFolder(moveFolder).render();
        // The folders are moved
        Assert.assertTrue(documentLibPage.isFileVisible(folderName1), "Verifying moved folder1 is present in site doclib/movePath");
        Assert.assertTrue(documentLibPage.isFileVisible(folderName2), "Verifying moved folder2 is present in site doclib/movePath");

        // Navigating to parent folder
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        documentLibPage.selectFolder(folderParent).render();

        Assert.assertFalse(documentLibPage.isFileVisible(folderName1), "Verifying moved folder1 isn't present in site doclib / parent folder");
        Assert.assertFalse(documentLibPage.isFileVisible(folderName2), "Verifying moved folder2 isn't present in site doclib / parent folder");
    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_Enterprise40x_13846() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    // Folders - Delete
    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_13846() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderParent = "parent_" + getFolderName(testName + System.currentTimeMillis());
        String folderName1 = "First_" + getFolderName(testName + System.currentTimeMillis());
        String folderName2 = "Second_" + getFolderName(testName + System.currentTimeMillis());

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site was created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        DocumentLibraryPage documentLibPage = ShareUser.openDocumentLibrary(drone);

        // Creating folder
        ShareUserSitePage.createFolder(drone, folderParent, null);
        // At least 2 folders a were created within the site Document Library
        ShareUser.createFolderInFolder(drone, folderName1, null, DOCLIB_CONTAINER + SLASH + folderParent);
        ShareUser.createFolderInFolder(drone, folderName2, null, DOCLIB_CONTAINER + SLASH + folderParent);

        // Several folders are selected
        documentLibPage = documentLibPage.getNavigation().selectAll().render();

        // Click Delete from Selected items drop down
        ConfirmDeletePage confirmDeletePage = documentLibPage.getNavigation().selectDelete().render();

        // Click Cancel
        confirmDeletePage.selectAction(ConfirmDeletePage.Action.Cancel).render();

        // The message is closed. The folders are not deleted
        Assert.assertTrue(documentLibPage.isFileVisible(folderName1), "Verifying folder1 isn't present in site doclib / parent folder (deleted)");
        Assert.assertTrue(documentLibPage.isFileVisible(folderName2), "Verifying folder2 isn't present in site doclib / parent folder (deleted)");

        // Select the folders again and repeat step1. Click Delete
        confirmDeletePage = documentLibPage.getNavigation().selectDelete().render();
        confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render();

        // The folders are deleted
        Assert.assertFalse(documentLibPage.isFileVisible(folderName1), "Verifying moved folder1 is present in site doclib / parent folder (not deleted)");
        Assert.assertFalse(documentLibPage.isFileVisible(folderName2), "Verifying moved folder2 is present in site doclib / parent folder (not deleted)");
    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_Enterprise40x_13849() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

    }

    // Documents - View the actions
    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_13849() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = "First_" + getFolderName(testName) + ".txt";
        String fileName2 = "Second_" + getFolderName(testName) + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.openDocumentLibrary(drone);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Create File
        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Select one document. Verify the Selected Items drop-down
        DocumentLibraryPage docLibPage = ShareUser.selectContentCheckBox(drone, fileName1);

        // The following actions are available: Copy to, Move to, Delete, Deselect All, Start Workflow, For 4.2 Download as ZIp
        Assert.assertTrue(docLibPage.getNavigation().isSelectedItemMenuCorrectForDocument(), "The following actions aren't available: "
                + "Copy to, Move to, Delete, Deselect All, Start Workflow, For 4.2 Download as ZIp");

        // Select several documents. Verify the Selected Items drop-down
        docLibPage = ShareUser.selectContentCheckBox(drone, fileName2);

        // The following actions are available: Copy to, Move to, Delete, Deselect All, Start Workflow, For 4.2 Download as ZIp
        Assert.assertTrue(docLibPage.getNavigation().isSelectedItemMenuCorrectForDocument(), "The following actions are available: "
                + "Copy to, Move to, Delete, Deselect All, Start Workflow, For 4.2 Download as ZIp");

    }

    // Documents - Copy to
    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_13850() throws Exception
    {

        String testName = getTestName();
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderParent = "parent_" + getFolderName(testName + System.currentTimeMillis());
        String copyFolder = "copy_" + getFolderName(testName + System.currentTimeMillis());
        String fileName1 = "First_" + getFileName(testName) + ".txt";
        String fileName2 = "Second_" + getFileName(testName) + ".txt";
        String[] copyFolderPath = { copyFolder };

        // Login
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Any site was created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.openDocumentLibrary(drone);

        // Creating folder
        DocumentLibraryPage documentLibPage = ShareUserSitePage.createFolder(drone, folderParent, null);
        ShareUserSitePage.createFolder(drone, copyFolder, null);

        documentLibPage.selectFolder(folderParent).render();

        // At least 2 documents a were created within the site Document Library
        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileName1);
        documentLibPage = ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibPage);

        documentLibPage.selectFolder(folderParent).render();
        // Create File
        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setContent(fileName2);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibPage);

        documentLibPage.selectFolder(folderParent).render();
        // Several documents are selected
        // Click Copy to from Selected items drop down
        // Select the destination (e.g. Sites) and click Cancel
        ShareUserSitePage.copyOrMoveToFolderAllSelectedItems(drone, siteName, copyFolderPath, true, false);

        // The dialog closes. The documents are not copied (verify the destination document)
        documentLibPage = ShareUser.openDocumentLibrary(drone);

        // Navigating to path folder
        documentLibPage.selectFolder(copyFolder).render();

        Assert.assertFalse(documentLibPage.isFileVisible(fileName1), "Verifying copied document1 isn't present in site doclib/copyFolder_path");
        Assert.assertFalse(documentLibPage.isFileVisible(fileName2), "Verifying copied document2 isn't present in site doclib/copyFolder_path");

        // Navigating to parent folder
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        documentLibPage.selectFolder(folderParent).render();

        // Select several folders again and click Copy to from Selected Items drop-down
        Assert.assertTrue(documentLibPage.isFileVisible(fileName1), "Verifying document1 is present in site doclib / parent folder");
        Assert.assertTrue(documentLibPage.isFileVisible(fileName2), "Verifying document2 is present in site doclib / parent folder");

        // Select the destination (e.g. Sites) and click Copy
        ShareUserSitePage.copyOrMoveToFolderAllSelectedItems(drone, siteName, copyFolderPath, true, true);

        // Navigate to the destination and verify the documents are copied
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        documentLibPage.selectFolder(copyFolder).render();
        // The documents are copied
        Assert.assertTrue(documentLibPage.isFileVisible(fileName1), "Verifying copied document1 is present in site doclib/copyFolder_path");
        Assert.assertTrue(documentLibPage.isFileVisible(fileName2), "Verifying copied document2 is present in site doclib/copyFolder_path");
    }

    // Documents - Move to
    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_13851() throws Exception
    {

        String testName = getTestName();
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String moveFolder = "move_" + getFolderName(testName + System.currentTimeMillis());
        String folderParent = "parent_" + getFolderName(testName + System.currentTimeMillis());
        String fileName1 = "First_" + getFileName(testName) + ".txt";
        String fileName2 = "Second_" + getFileName(testName) + ".txt";
        String[] moveFolderPath = { moveFolder };

        // Login
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Any site was created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        DocumentLibraryPage documentLibPage;
        ShareUser.openDocumentLibrary(drone);

        // Creating folder
        ShareUserSitePage.createFolder(drone, moveFolder, null);
        documentLibPage = ShareUserSitePage.createFolder(drone, folderParent, null);

        documentLibPage.selectFolder(folderParent).render();

        // At least 2 documents a were created within the site Document Library
        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileName1);
        documentLibPage = ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibPage);

        documentLibPage.selectFolder(folderParent).render();
        // Create File
        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setContent(fileName2);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibPage);

        documentLibPage.selectFolder(folderParent).render();
        // Several documents are selected
        // Click Move to from Selected items drop down
        // Select the destination (e.g. Sites) and click Cancel
        ShareUserSitePage.copyOrMoveToFolderAllSelectedItems(drone, siteName, moveFolderPath, false, false);

        // The dialog closes. The documents aren't moved (verify the destination)
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        documentLibPage = documentLibPage.selectFolder(moveFolder).render();
        Assert.assertFalse(documentLibPage.isFileVisible(fileName1), "Verifying moved document1 isn't present in site doclib/movePath");
        Assert.assertFalse(documentLibPage.isFileVisible(fileName2), "Verifying moved document2 isn't present in site doclib/movePath");

        // Navigating to parent folder
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        documentLibPage.selectFolder(folderParent).render();

        // Select several folders again and click Move to from Selected Items drop-down
        Assert.assertTrue(documentLibPage.isFileVisible(fileName1), "Verifying document1 is present in site doclib / parent folder");
        Assert.assertTrue(documentLibPage.isFileVisible(fileName2), "Verifying document2 is present in site doclib / parent folder");

        // Select the destination (e.g. Sites) and click Move
        ShareUserSitePage.copyOrMoveToFolderAllSelectedItems(drone, siteName, moveFolderPath, false, true);

        // Navigate to the destination and verify the documents are moved
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        documentLibPage.selectFolder(moveFolder).render();

        // The documents are moved
        Assert.assertTrue(documentLibPage.isFileVisible(fileName1), "Verifying moved document1 is present in site doclib/movePath");
        Assert.assertTrue(documentLibPage.isFileVisible(fileName2), "Verifying moved document2 is present in site doclib/movePath");

        // Navigating to parent folder
        documentLibPage = ShareUser.openDocumentLibrary(drone);
        documentLibPage.selectFolder(folderParent).render();

        // verify the documents are moved
        Assert.assertFalse(documentLibPage.isFileVisible(fileName1), "Verifying moved document1 isn't present in site doclib / parent folder");
        Assert.assertFalse(documentLibPage.isFileVisible(fileName2), "Verifying moved document2 isn't present in site doclib / parent folder");
    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_Enterprise40x_13852() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    // Documents - Delete
    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_13852() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderParent = "parent_" + getFolderName(testName + System.currentTimeMillis());
        String fileName1 = "First_" + getFileName(testName) + ".txt";
        String fileName2 = "Second_" + getFileName(testName) + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site was created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.openDocumentLibrary(drone);

        // Creating folder
        DocumentLibraryPage documentLibPage = ShareUserSitePage.createFolder(drone, folderParent, null);

        documentLibPage.selectFolder(folderParent).render();

        // At least 2 documents a were created within the site Document Library
        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileName1);
        documentLibPage = ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibPage);

        documentLibPage.selectFolder(folderParent).render();
        // Create File
        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setContent(fileName2);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibPage);

        documentLibPage.selectFolder(folderParent).render();
        // Several documents are selected
        documentLibPage = documentLibPage.getNavigation().selectAll().render();

        // Click Delete from Selected items drop down
        ConfirmDeletePage confirmDeletePage = documentLibPage.getNavigation().selectDelete().render();

        // Click Cancel
        confirmDeletePage.selectAction(ConfirmDeletePage.Action.Cancel).render();

        // The message is closed. The documents are not deleted
        Assert.assertTrue(documentLibPage.isFileVisible(fileName1), "Verifying document1 isn't present in site doclib / parent folder (deleted)");
        Assert.assertTrue(documentLibPage.isFileVisible(fileName2), "Verifying document2 isn't present in site doclib / parent folder (deleted)");

        // Select the documents again and repeat step1. Click Delete
        confirmDeletePage = documentLibPage.getNavigation().selectDelete().render();
        confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render();

        // The documents are deleted
        Assert.assertFalse(documentLibPage.isFileVisible(fileName1), "Verifying moved document1 is present in site doclib / parent folder (not deleted)");
        Assert.assertFalse(documentLibPage.isFileVisible(fileName2), "Verifying moved document2 is present in site doclib / parent folder (not deleted)");
    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_Enterprise40x_13853() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

    }

    // Documents - Start Workflow
    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_13853()
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = "First_" + getFileName(testName) + ".txt";
        String fileName2 = "Second_" + getFileName(testName) + ".txt";
        String START_WORKFLOW = "Start Workflow";

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openSiteDashboard(drone, siteName);

            // Upload Files
            String[] fileInfo = { fileName1, DOCLIB };
            ShareUser.uploadFileInFolder(drone, fileInfo);

            String[] fileInfo2 = { fileName2, DOCLIB };
            ShareUser.uploadFileInFolder(drone, fileInfo2);

            // "Document Library" page has been opened
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Several documents are selected
            documentLibraryPage = documentLibraryPage.getNavigation().selectAll().render();

            // Click Start Workflow from Selected items drop down
            StartWorkFlowPage startWorkFlowPage = documentLibraryPage.getNavigation().selectStartWorkFlow().render();

            // Start Workflow page opens. The selected files are displayed in the Items section
            Assert.assertTrue(startWorkFlowPage.getTitle().contains(START_WORKFLOW), "Start Workflow page isn't opens");

            // Fill in the required fields
            NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW));
            String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
            String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
            List<String> reviewers = new ArrayList<>();
            reviewers.add(username);
            WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
            formDetails.setMessage(workFlowName1);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);

            // click Cancel
            documentLibraryPage = newWorkflowPage.cancelCreateWorkflow(formDetails).render();

            // check the document isn't marked with icon
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the file isn't part of a workflow");
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the file isn't part of a workflow");

            // The workflow wasn't started (log in as assignee and verify that the task wasn't created)
            // site creator logs out and assignee user logs in
            ShareUser.logout(drone);
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // check the task is not in MyTasks for site creator
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
            assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            ShareUser.logout(drone);

            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Several documents are selected
            documentLibraryPage = documentLibraryPage.getNavigation().selectAll().render();
            // Select StartWorkflow from Document Library Navigation
            startWorkFlowPage = documentLibraryPage.getNavigation().selectStartWorkFlow().render();

            Assert.assertTrue(startWorkFlowPage.getTitle().contains(START_WORKFLOW), "Start Workflow page isn't opens");

            // Fill in the required fields and click Start Workflow
            newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW));

            // Start workflow
            documentLibraryPage = newWorkflowPage.startWorkflow(formDetails).render();
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName1), "File isn't visible " + fileName1);

            // The icon indicating that the document belongs to workflow is displayed near both documents
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the file is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the file is part of a workflow");

            ShareUser.logout(drone);

            // The workflow is started (log in as assignee and verify that the task was created
            // and contains both documents in Items section)
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // check the task is not in MyTasks for site creator
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
            assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            // press Task Done
            TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName1);

            TaskInfo taskDetailsInfo = taskDetailsPage.getTaskDetailsInfo();
            assertEquals(taskDetailsInfo.getMessage(), workFlowName1);

            // Verify Item details in Task Details Page
            List<TaskItem> taskItems = taskDetailsPage.getTaskItems();
            assertEquals(taskItems.size(), 2);

            if (taskItems.get(0).getItemName().equals(fileName1))
            {
                assertEquals(taskItems.get(0).getItemName(), fileName1);
                assertEquals(taskItems.get(1).getItemName(), fileName2);
            }
            else
            {
                assertEquals(taskItems.get(1).getItemName(), fileName1);
                assertEquals(taskItems.get(0).getItemName(), fileName2);
            }

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

}
