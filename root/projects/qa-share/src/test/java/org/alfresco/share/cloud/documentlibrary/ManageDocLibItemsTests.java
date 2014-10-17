package org.alfresco.share.cloud.documentlibrary;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.TreeMenuNavigation;
import org.alfresco.share.site.document.DocumentDetailsActionsTest;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author bogdan.bocancea
 */

@Listeners(FailedTestListener.class)
public class ManageDocLibItemsTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(DocumentDetailsActionsTest.class);
    protected String testUser;
    protected String siteName = "";
    private static DocumentLibraryPage documentLibPage;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepDocumentLibrary", "AlfrescoOne" })
    public void dataPrep_12567() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);

        // User 1
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User 2
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

        // invite user2 to site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.COLLABORATOR);

        ShareUser.openSiteDashboard(drone, siteName).render();

        // Upload File
        String fileName1 = getFileName(testName) + "1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo1).render();

        String fileName2 = getFileName(testName) + "2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo2).render();

        String fileName3 = getFileName(testName) + "3.txt";
        String[] fileInfo3 = { fileName3, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo3).render();

        FileDirectoryInfo fileInfoDir = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1);

        // select favorite for file1
        fileInfoDir.selectFavourite();

        // edit offline first document
        fileInfoDir.selectEditOfflineAndCloseFileWindow().render();
        
        // select favorite for File2
        fileInfoDir = ShareUserSitePage.getFileDirectoryInfo(drone, fileName2);
        fileInfoDir.selectFavourite();
        
        ShareUser.logout(drone);

        // Login user 2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUser.selectHomeNetwork(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // edit offline
        fileInfoDir = ShareUserSitePage.getFileDirectoryInfo(drone, fileName3);
        fileInfoDir.selectEditOfflineAndCloseFileWindow().render();
        fileInfoDir = ShareUserSitePage.getFileDirectoryInfo(drone, fileName2);
        fileInfoDir.selectFavourite();
        
        ShareUser.logout(drone);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_12567() throws Exception
    {
        String testName = getTestName();
        String fileName1 = getFileName(testName) + "1.txt";
        String fileName2 = getFileName(testName) + "2.txt";
        String fileName3 = getFileName(testName) + "3.txt";
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // login user 1
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();

        // navigate to document library
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        TreeMenuNavigation treeMenuNavigation = documentLibPage.getLeftMenus().render();

        // 1. Click the All documents view
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS).render();

        // 1. All the items in the Document Library are displayed;
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName2, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, true), fileName2
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName3, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, true), fileName3
                + " cannot be found.");

        // 2. Click Locate File action from More+ menu for one of documents;
        FileDirectoryInfo fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1);
        fileInfoDir1.selectLocateFile();

        // 2. The folder where the file is located is opened. The file is displayed;
        assertTrue(documentLibPage.isFileVisible(fileName1));

        // 3. Click the I'm Editing view;
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.IM_EDITING).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.IM_EDITING, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName2, TreeMenuNavigation.DocumentsMenu.IM_EDITING, false), fileName2
                + " cannot be found.");

        // 4. Click Locate File action from More+ menu for one of documents;
        fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1);
        fileInfoDir1.selectLocateFile();

        // 4. The folder where the file is located is opened. The file is displayed;
        assertTrue(documentLibPage.isFileVisible(fileName1));

        // 5. Click the Others are Editing view
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.OTHERS_EDITING).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.OTHERS_EDITING, false), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName3, TreeMenuNavigation.DocumentsMenu.OTHERS_EDITING, true), fileName3
                + " cannot be found.");

        // 6. Click Locate File action from More+ menu for one of documents;
        fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName3);
        fileInfoDir1.selectLocateFile();
        assertTrue(documentLibPage.isFileVisible(fileName3));

        // 7. Click the Recently Modified view;
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.RECENTLY_MODIFIED).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.RECENTLY_MODIFIED, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName3, TreeMenuNavigation.DocumentsMenu.RECENTLY_MODIFIED, true), fileName3
                + " cannot be found.");

        // 8. Click Locate File action from More+ menu for one of documents;
        fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName3);
        fileInfoDir1.selectLocateFile();
        assertTrue(documentLibPage.isFileVisible(fileName3));

        // 9. Click the Recently Added view;
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.RECENTLY_ADDED).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.RECENTLY_ADDED, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName2, TreeMenuNavigation.DocumentsMenu.RECENTLY_ADDED, true), fileName2
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName3, TreeMenuNavigation.DocumentsMenu.RECENTLY_ADDED, true), fileName3
                + " cannot be found.");

        // 10. Click Locate File action from More+ menu for one of documents;
        fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName3);
        fileInfoDir1.selectLocateFile();
        assertTrue(documentLibPage.isFileVisible(fileName3));

        // 11. Click the My Favorites view;
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.MY_FAVORITES).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.MY_FAVORITES, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName2, TreeMenuNavigation.DocumentsMenu.MY_FAVORITES, true), fileName2
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName3, TreeMenuNavigation.DocumentsMenu.MY_FAVORITES, false), fileName3
                + " cannot be found.");

        // 12. Click Locate File action from More+ menu for one of documents;
        fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName2);
        fileInfoDir1.selectLocateFile();
        assertTrue(documentLibPage.isFileVisible(fileName2));
        
        // 13. Click the Synced content view
        // 14. Click Locate File action from More+ menu for one of folders.
        // TODO: it is necessary to have the hybrid workflow enabled, but this test is executed only for cloud.
    }

}
