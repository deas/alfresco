package org.alfresco.share.site.document;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorCloudImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;

/**
 * @author Roman.Chul
 */

@Listeners(FailedTestListener.class)
public class ManageFoldersTest extends AbstractTests
{
    private static Log logger = LogFactory.getLog(ManageFoldersTest.class);
    protected String testUser;
    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13821() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13821()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            FolderDetailsPage folderDetailsPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();
            List<String> folderActionsList = folderDetailsPage.getFolderActionList();
            HashSet<String> actualActionSet = new HashSet<String>(folderActionsList);

            HashSet<String> expectedActionSet = new HashSet<String>();
            if (alfrescoVersion.equals(AlfrescoVersion.Enterprise42))
            {
                expectedActionSet.add("Download as Zip");
            }
            expectedActionSet.add("Edit Properties");
            expectedActionSet.add("Copy to...");
            expectedActionSet.add("Move to...");
            expectedActionSet.add("Manage Rules");
            expectedActionSet.add("Delete Folder");
            expectedActionSet.add("Manage Permissions");
            if (!alfrescoVersion.isCloud())
            {
                expectedActionSet.add("Manage Aspects");
            }

            Assert.assertTrue(actualActionSet.containsAll(expectedActionSet));

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

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13819() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13819()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName).isRuleIconPresent(5000));

            FolderRulesPage rulesPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectManageRules().render();
            Assert.assertTrue(rulesPage.isPageCorrect(folderName));

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

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13820() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            //CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            //ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            //ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

            // Create Rule
            FolderRulesPage rulesPage = documentLibraryPage.render(maxWaitTime).getFileDirectoryInfo(folderName).selectManageRules().render();
            CreateRulePage createRulePage = rulesPage.openCreateRulePage().render();
            createRulePage.fillNameField(folderName);
            createRulePage.fillDescriptionField(folderName);

            if (!alfrescoVersion.isCloud())
            {
                ActionSelectorEnterpImpl actionSelector = createRulePage.getActionOptionsObj();
                actionSelector.selectExtractMetadata();
            }
            else
            {
                ActionSelectorCloudImpl actionSelectorCloud = createRulePage.getActionOptionsObj();
                actionSelectorCloud.selectCopy(siteName, "Documents");
            }

            createRulePage.clickCreate();

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13820()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName).isRuleIconPresent(5000));

            FolderRulesPageWithRules rulesPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectManageRules().render();
            rulesPage.render(maxWaitTime);
            Assert.assertTrue(rulesPage.isPageCorrect(folderName));

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

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_8958() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_8958()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String tagName = "testTag";

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            documentLibraryPage.getFileDirectoryInfo(folderName).addTag(tagName);

            // Clicking the tagName link present under folder name
            documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTagNameLink(tagName).render();

            // Check that the folder is listed
            Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(drone, folderName, true));

            // Clicking the tagName present under Tags menu tree on Document Library page.
            documentLibraryPage = documentLibraryPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(tagName).render();

            Assert.assertTrue(documentLibraryPage.isFileVisible(folderName));

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

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13823() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13823()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String tagName = "testtag";
        DocumentLibraryPage documentLibraryPage;

        try
        {

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Enter tag and click cancel
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.enterTagString(tagName);
            info.clickOnTagCancelButton();

            Assert.assertEquals(info.getTags().size(), 0);
            // Enter tag and click save
            info.addTag(tagName);

            Assert.assertEquals(info.getTags().size(), 1);

            // Remove tag and click cancel
            info.clickOnAddTag();
            info.clickOnTagRemoveButton(tagName);
            info.clickOnTagCancelButton();

            // Clicking the tagName link present under folder name
            documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTagNameLink(tagName).render();

            // Check that the folder is listed
            Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(drone, folderName, true));
            // Clicking the tagName present under Tags menu tree on Document Library page.
            documentLibraryPage = documentLibraryPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(tagName).render();

            Assert.assertTrue(documentLibraryPage.isFileVisible(folderName));

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            // Remove tag
            try
            {
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
                FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
                info.clickOnAddTag();
                info.clickOnTagRemoveButton(tagName);
                info.clickOnTagSaveButton();

            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13824() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13824()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String tagName = "testtag";
        DocumentLibraryPage documentLibraryPage = null;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Add tag
            documentLibraryPage.getFileDirectoryInfo(folderName).addTag(tagName);

            // Open folder details page
            FolderDetailsPage detailsPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

            List<String> tagList = detailsPage.getTagList();
            Assert.assertTrue(tagList.size() == 1 && tagList.get(0).equals(tagName));

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            // Remove tag
            try
            {
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
                FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
                info.clickOnAddTag();
                info.clickOnTagRemoveButton(tagName);
                info.clickOnTagSaveButton();

            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13826() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13826()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        DocumentLibraryPage documentLibraryPage = null;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Try to add empty tag
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.enterTagString("");
            info.clickOnTagSaveButton();

            // Verify that tag is not added
            Assert.assertEquals(info.getTags().size(), 0);

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

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13827() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13827()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String[] tagList = new String[] { "caf\u00E9", "fr\u00FChst\u00FCck", "propriet\u00E0", "\u30D7\u30ED\u30D1\u30C6\u30A3", "ma\u00F1ana" };
        DocumentLibraryPage documentLibraryPage = null;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Try to add empty tag
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            for (String tag : tagList)
            {
                info.clickOnAddTag();
                info.enterTagString(tag);
                info.clickOnTagSaveButton();
            }

            // Open folder details page
            FolderDetailsPage detailsPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

            List<String> actualTagList = detailsPage.getTagList();
            Assert.assertEquals(actualTagList.size(), tagList.length);

            for (String tag : tagList)
            {
                Assert.assertTrue(actualTagList.contains(tag));
            }

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            // Remove tag
            try
            {
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
                FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
                info.clickOnAddTag();
                for (String tag : tagList)
                {
                    info.clickOnTagRemoveButton(tag);
                }
                info.clickOnTagSaveButton();

            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13828() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13828()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String longTag = ShareUser.getRandomStringWithNumders(256);

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Add tag more than 255 symbols
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.addTag(longTag);

            // Open folder details page
            FolderDetailsPage detailsPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();
            detailsPage.getTagList();

            List<String> actualTagList = detailsPage.getTagList();
            Assert.assertEquals(actualTagList.size(), 1);
            Assert.assertTrue(actualTagList.contains(longTag.substring(0, 255)));

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            // Remove tag
            try
            {
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
                FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
                info.clickOnAddTag();
                info.clickOnTagRemoveButton(longTag.substring(0, 255));
                info.clickOnTagSaveButton();

            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13829() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13829()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String tag1 = "dsf!@#$sd%^sdf&sdf*(sd)_+|s d f\\/?.,<>:;\"'`=-{}[]";
        String tag2 = "!@#$%^&()_+.,;'`=-{}[]";
        String tag3 = "test tag";

        DocumentLibraryPage documentLibraryPage = null;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Try to add tag with disallowed wildcards
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.enterTagString(tag1);
            info.clickOnTagSaveButton();
            // tag is not added
            Assert.assertFalse(documentLibraryPage.getAllTagNames().contains(tag1));
            info.clickOnTagCancelButton();

            // Try to add tag with valid wildcards
            info.clickOnAddTag();
            info.enterTagString(tag2);
            info.clickOnTagSaveButton();
            while (!documentLibraryPage.getAllTagNames().contains(tag2))
            {
                try
                {
                    info.clickOnTagSaveButton();
                }
                catch (Throwable ex)
                {
                    logger.info(ex.getMessage());
                }
            }
            // tag is added
            Assert.assertTrue(documentLibraryPage.getAllTagNames().contains(tag2));

            // Open folder details page
            FolderDetailsPage detailsPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();
            detailsPage.getTagList();

            List<String> actualTagList = detailsPage.getTagList();
            Assert.assertEquals(actualTagList.size(), 1);
            Assert.assertTrue(actualTagList.contains(tag2));

            documentLibraryPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
            // Try to add tag with spaces
            info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.addTag(tag3);

            while (!documentLibraryPage.getAllTagNames().contains(tag3))
            {
                try
                {
                    info.clickOnTagSaveButton();
                }
                catch (Throwable ex)
                {
                    logger.info(ex.getMessage());
                }
            }
            // tag is added
            Assert.assertTrue(documentLibraryPage.getAllTagNames().contains(tag3));

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            // Remove tag
            try
            {
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
                FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
                info.clickOnAddTag();
                info.clickOnTagRemoveButton(tag2);
                info.clickOnTagRemoveButton(tag3);
                info.clickOnTagSaveButton();

            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13830() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13830()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String tag = "testtag13830";

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Add tag
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.addTag(tag);

            // Open folder details page
            FolderDetailsPage detailsPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();
            List<String> actualTagList = detailsPage.getTagList();
            Assert.assertEquals(actualTagList.size(), 1);
            Assert.assertTrue(actualTagList.contains(tag));

            documentLibraryPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
            info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.sendKeysToTagInput(Keys.BACK_SPACE);
            // Verify is tag highlighted
            Assert.assertTrue(info.isTagHighlightedOnEdit(tag));
            info.sendKeysToTagInput(Keys.BACK_SPACE);
            info.clickOnTagSaveButton();

            Assert.assertEquals(info.getTags().size(), 0);

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

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13831() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);
            String tagName = getTestName().toLowerCase();

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
            documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.enterTagString(tagName);
            while (!documentLibraryPage.getAllTagNames().contains(tagName))
            {
                try
                {
                    info.clickOnTagSaveButton();
                }
                catch (Throwable ex)
                {
                    logger.info(ex.getMessage());
                }
            }

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13831()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String tag = testName.toLowerCase();

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Click on tag
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.clickTagOnEdit(tag);
            info.sendKeysToTagInput(tag + "edited");
            info.clickOnTagCancelButton();

            documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
            info = documentLibraryPage.getFileDirectoryInfo(folderName);

            Assert.assertTrue(info.getTags().contains(tag));
            Assert.assertTrue(documentLibraryPage.getAllTagNames().contains(tag));

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

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13832() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);
            String tagName = getTestName().toLowerCase();

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.enterTagString(tagName);
            while (!documentLibraryPage.getAllTagNames().contains(tagName))
            {
                try
                {
                    info.clickOnTagSaveButton();
                }
                catch (Throwable ex)
                {
                    logger.info(ex.getMessage());
                }
            }

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13832()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String tag = testName.toLowerCase();

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Click on tag
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.clickTagOnEdit(tag);
            info.sendKeysToTagInput(tag + "edited");
            info.clickOnTagSaveButton();

            Assert.assertTrue(info.getTags().contains(tag + "edited"));

            // Open folder details page
            FolderDetailsPage detailsPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

            List<String> actualTagList = detailsPage.getTagList();
            Assert.assertEquals(actualTagList.size(), 1);
            Assert.assertFalse(actualTagList.contains(tag));
            Assert.assertTrue(actualTagList.contains(tag + "edited"));

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            // Revert tag
            try
            {
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
                FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
                info.clickOnAddTag();
                info.clickTagOnEdit(tag + "edited");
                info.enterTagString(tag);
                while (!documentLibraryPage.getAllTagNames().contains(tag))
                {
                    try
                    {
                        info.clickOnTagSaveButton();
                    }
                    catch (Throwable ex)
                    {
                        logger.info(ex.getMessage());
                    }
                }

            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13834() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);
            String tagName = getTestName().toLowerCase();

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.enterTagString(tagName);
            while (!documentLibraryPage.getAllTagNames().contains(tagName))
            {
                try
                {
                    info.clickOnTagSaveButton();
                }
                catch (Throwable ex)
                {
                    logger.info(ex.getMessage());
                }
            }
            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13834()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String tag = testName.toLowerCase();

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Click on tag
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.clickTagOnEdit(tag);
            info.clickOnTagSaveButton();

            Assert.assertTrue(info.getTags().contains(tag));
            Assert.assertTrue(documentLibraryPage.getAllTagNames().contains(tag));

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

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13835() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);
            String tagName = getTestName().toLowerCase();

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.enterTagString(tagName);
            while (!documentLibraryPage.getAllTagNames().contains(tagName))
            {
                try
                {
                    info.clickOnTagSaveButton();
                }
                catch (Throwable ex)
                {
                    logger.info(ex.getMessage());
                }
            }
            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13835()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String tag = testName.toLowerCase();
        String[] tagList = new String[] { "caf\u00E9", "fr\u00FChst\u00FCck", "propriet\u00E0", "\u30D7\u30ED\u30D1\u30C6\u30A3", "ma\u00F1ana" };
        DocumentLibraryPage documentLibraryPage;
        String tempTag = tag;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Click on tag
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);

            for (String tagName : tagList)
            {
                info.clickOnAddTag();
                info.clickTagOnEdit(tempTag);
                info.sendKeysToTagInput(tagName);
                info.clickOnTagSaveButton();
                tempTag = tagName;

                documentLibraryPage = ShareUser.openDocumentLibrary(drone).render(maxWaitTime);
                info = documentLibraryPage.getFileDirectoryInfo(folderName);

                Assert.assertTrue(info.getTags().contains(tagName));
                Assert.assertTrue(documentLibraryPage.getAllTagNames().contains(tagName));
            }

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            // Revert tag
            try
            {
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
                FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
                info.clickOnAddTag();
                info.clickTagOnEdit(tempTag);
                info.enterTagString(tag);
                while (!documentLibraryPage.getAllTagNames().contains(tag))
                {
                    try
                    {
                        info.clickOnTagSaveButton();
                    }
                    catch (Throwable ex)
                    {
                        logger.info(ex.getMessage());
                    }
                }

            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13836() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);
            String tagName = getTestName().toLowerCase();

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.enterTagString(tagName);
            while (!documentLibraryPage.getAllTagNames().contains(tagName))
            {
                try
                {
                    info.clickOnTagSaveButton();
                }
                catch (Throwable ex)
                {
                    logger.info(ex.getMessage());
                }
            }
            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13836()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String tag = testName.toLowerCase();
        DocumentLibraryPage documentLibraryPage;
        String longTag = ShareUser.getRandomStringWithNumders(256);

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Click on tag
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);

            info.clickOnAddTag();
            info.clickTagOnEdit(tag);
            info.sendKeysToTagInput(longTag);
            info.clickOnTagSaveButton();

            documentLibraryPage = ShareUser.openDocumentLibrary(drone).render(maxWaitTime);
            info = documentLibraryPage.getFileDirectoryInfo(folderName);

            Assert.assertTrue(info.getTags().contains(longTag.substring(0, 255)));
            Assert.assertTrue(documentLibraryPage.getAllTagNames().contains(longTag.substring(0, 255)));

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            // Revert tag
            try
            {
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
                FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
                info.clickOnAddTag();
                info.clickTagOnEdit(longTag.substring(0, 255));
                info.enterTagString(tag);
                while (!documentLibraryPage.getAllTagNames().contains(tag))
                {
                    try
                    {
                        info.clickOnTagSaveButton();
                    }
                    catch (Throwable ex)
                    {
                        logger.info(ex.getMessage());
                    }
                }

            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13837() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);
            String tagName = getTestName().toLowerCase();

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.enterTagString(tagName);
            while (!documentLibraryPage.getAllTagNames().contains(tagName))
            {
                try
                {
                    info.clickOnTagSaveButton();
                }
                catch (Throwable ex)
                {
                    logger.info(ex.getMessage());
                }
            }
            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13837()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String tag = testName.toLowerCase();
        DocumentLibraryPage documentLibraryPage;
        String tag1 = "dsf!@#$sd%^sdf&sdf*(sd)_+|s d f\\/?.,<>:;\"'`=-{}[]";
        String tag2 = "!@#$%^&()_+.,;'`=-{}[]";
        String tag3 = tag + " edited";

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Try to edit tag with disallowed wildcards
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.clickTagOnEdit(tag);
            info.sendKeysToTagInput(tag1);
            info.clickOnTagSaveButton();
            // tag is not added
            documentLibraryPage = ShareUser.openDocumentLibrary(drone).render(maxWaitTime);
            info = documentLibraryPage.getFileDirectoryInfo(folderName);

            Assert.assertFalse(info.getTags().contains(tag1));
            Assert.assertFalse(documentLibraryPage.getAllTagNames().contains(tag1));

            // Try to edit tag with valid wildcards
            info.clickOnAddTag();
            info.clickTagOnEdit(tag);
            info.sendKeysToTagInput(tag2);
            while (!documentLibraryPage.getAllTagNames().contains(tag2))
            {
                try
                {
                    info.clickOnTagSaveButton();
                }
                catch (Throwable ex)
                {
                    logger.info(ex.getMessage());
                }
            }
            // tag is edited
            Assert.assertTrue(info.getTags().contains(tag2));
            Assert.assertTrue(documentLibraryPage.getAllTagNames().contains(tag2));

            // Open folder details page
            FolderDetailsPage detailsPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();
            detailsPage.getTagList();

            List<String> actualTagList = detailsPage.getTagList();
            Assert.assertEquals(actualTagList.size(), 1);
            Assert.assertTrue(actualTagList.contains(tag2));

            documentLibraryPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
            // Try to edit tag with spaces
            info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.clickTagOnEdit(tag2);
            info.sendKeysToTagInput(tag3);
            info.clickOnTagSaveButton();

            while (!documentLibraryPage.getAllTagNames().contains(tag3))
            {
                try
                {
                    info.clickOnTagSaveButton();
                }
                catch (Throwable ex)
                {
                    logger.info(ex.getMessage());
                }
            }
            // tag is edited
            Assert.assertTrue(documentLibraryPage.getAllTagNames().contains(tag3));

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            // Revert tag
            try
            {
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
                FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
                List<String> addedTags = info.getTags();
                info.clickOnAddTag();
                for (String addedTag : addedTags)
                {
                    info.clickOnTagRemoveButton(addedTag);
                }
                info.enterTagString(tag);
                while (!documentLibraryPage.getAllTagNames().contains(tag))
                {
                    try
                    {
                        info.clickOnTagSaveButton();
                    }
                    catch (Throwable ex)
                    {
                        logger.info(ex.getMessage());
                    }
                }

            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13838() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);
            String tagName = getTestName().toLowerCase();

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.enterTagString(tagName);
            while (!documentLibraryPage.getAllTagNames().contains(tagName))
            {
                try
                {
                    info.clickOnTagSaveButton();
                }
                catch (Throwable ex)
                {
                    logger.info(ex.getMessage());
                }
            }
            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13838()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String tag = testName.toLowerCase();
        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Try to add tag with disallowed wildcards
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            info.clickOnAddTag();
            info.clickTagOnEdit(tag);
            info.sendKeysToTagInput(Keys.BACK_SPACE);
            info.clickOnTagSaveButton();
            // tag is not removed
            documentLibraryPage = ShareUser.openDocumentLibrary(drone).render(maxWaitTime);
            info = documentLibraryPage.getFileDirectoryInfo(folderName);

            Assert.assertTrue(info.getTags().contains(tag));
            Assert.assertTrue(documentLibraryPage.getAllTagNames().contains(tag));

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

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13839() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);
            String[] tagList = new String[] { "!@#$%^&()_+.,;'`=-{}[]", "ma\u00F1ana", ShareUser.getRandomStringWithNumders(255) };
            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            for (String tagName : tagList)
            {
                info.clickOnAddTag();
                info.enterTagString(tagName);
                while (!documentLibraryPage.getAllTagNames().contains(tagName))
                {
                    try
                    {
                        info.clickOnTagSaveButton();
                    }
                    catch (Throwable ex)
                    {
                        logger.info(ex.getMessage());
                    }
                }
            }
            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13839()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        DocumentLibraryPage documentLibraryPage;
        String[] tagList = new String[] { "!@#$%^&()_+.,;'`=-{}[]", "ma\u00F1ana", ShareUser.getRandomStringWithNumders(255) };

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);

            List<String> actualTags = info.getTags();
            for (String actualTag : actualTags)
            {
                info.clickOnAddTag();
                info.clickOnTagRemoveButton(actualTag);
                info.clickOnTagCancelButton();

                // tag is not removed
                Assert.assertTrue(info.getTags().contains(actualTag));

                info.clickOnAddTag();
                info.clickOnTagRemoveButton(actualTag);
                info.clickOnTagSaveButton();
                // tag is removed
                documentLibraryPage = ShareUser.openDocumentLibrary(drone).render(maxWaitTime);
                info = documentLibraryPage.getFileDirectoryInfo(folderName);

                Assert.assertFalse(info.getTags().contains(actualTag));
                Assert.assertFalse(documentLibraryPage.getAllTagNames().contains(actualTag));
            }

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            // Revert tags
            try
            {
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
                FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
                for (String tagName : tagList)
                {
                    info.clickOnAddTag();
                    info.enterTagString(tagName);
                    while (!documentLibraryPage.getAllTagNames().contains(tagName))
                    {
                        try
                        {
                            info.clickOnTagSaveButton();
                        }
                        catch (Throwable ex)
                        {
                            logger.info(ex.getMessage());
                        }
                    }
                }

            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13840() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);
            String[] tagList = new String[] { "!@#$%^&()_+.,;'`=-{}[]", "ma\u00F1ana", ShareUser.getRandomStringWithNumders(255) };
            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
            for (String tagName : tagList)
            {
                info.clickOnAddTag();
                info.enterTagString(tagName);
                while (!documentLibraryPage.getAllTagNames().contains(tagName))
                {
                    try
                    {
                        info.clickOnTagSaveButton();
                    }
                    catch (Throwable ex)
                    {
                        logger.info(ex.getMessage());
                    }
                }
            }
            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "ManageFolders")
    public void Enterprise40x_13840()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        DocumentLibraryPage documentLibraryPage;
        String[] tagList = new String[] { "!@#$%^&()_+.,;'`=-{}[]", "ma\u00F1ana", ShareUser.getRandomStringWithNumders(255) };

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);

            List<String> actualTags = info.getTags();
            for (String actualTag : actualTags)
            {
                info.clickOnAddTag();
                info.clickOnTagRemoveButton(actualTag);
                info.clickOnTagSaveButton();
                // tag is removed
                documentLibraryPage = ShareUser.openDocumentLibrary(drone).render(maxWaitTime);
                info = documentLibraryPage.getFileDirectoryInfo(folderName);

                Assert.assertFalse(info.getTags().contains(actualTag));
                Assert.assertFalse(documentLibraryPage.getAllTagNames().contains(actualTag));
            }

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            // Revert tags
            try
            {
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
                FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(folderName);
                for (String tagName : tagList)
                {
                    info.clickOnAddTag();
                    info.enterTagString(tagName);
                    while (!documentLibraryPage.getAllTagNames().contains(tagName))
                    {
                        try
                        {
                            info.clickOnTagSaveButton();
                        }
                        catch (Throwable ex)
                        {
                            logger.info(ex.getMessage());
                        }
                    }
                }

            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
            testCleanup(drone, testName);
        }
    }

}
