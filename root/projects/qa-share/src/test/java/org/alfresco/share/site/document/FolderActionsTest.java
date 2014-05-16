package org.alfresco.share.site.document;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.*;

import static org.alfresco.po.share.site.document.DocumentAspect.*;
import org.alfresco.share.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author maryia.zaichanka
 */
@Listeners(FailedTestListener.class)
public class FolderActionsTest extends AbstractAspectTests
{
    private static Log logger = LogFactory.getLog(FolderActionsTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2921() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2921()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(CLASSIFIABLE);
        proptery.setExpectedProprtyKey(getClassifiableAspectKey());

        addAspectFolderKey(proptery);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2929() throws Exception
    {
        removeAspectDataPrepFolder(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2929()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(CLASSIFIABLE);
        proptery.setExpectedProprtyKey(getClassifiableAspectKey());

        removeAspectFolderKey(proptery);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2923() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2923()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(VERSIONABLE);

        addAspectFolder(proptery);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2924() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2924()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(INLINE_EDITABLE);

        addAspectFolder(proptery);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2926() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2926()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(EXIF);

        addAspectFolder(proptery);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2927() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2927()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(AUDIO);

        addAspectFolder(proptery);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2925() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2925()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(INDEX_CONTROL);

        addAspectFolder(proptery);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2928() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2928()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(RESTRICTABLE);
        proptery.setExpectedProprtyKey(getRestrictableAspectKey());

        addAspectFolderKey(proptery);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2922() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2922()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(GEOGRAPHIC);

        addAspectLinkCheck(proptery, false);

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2933() throws Exception
    {
        removeAspectDataPrepFolder(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2933()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(GEOGRAPHIC);

        addAspectLinkCheck(proptery, true);

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2931() throws Exception
    {
        removeAspectDataPrepFolder(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2931()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(ALIASABLE_EMAIL);
        proptery.setExpectedProprtyKey(getAliasAbleAspectKey());

        removeAspectFolderKey(proptery);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2935() throws Exception
    {
        removeAspectDataPrepFolder(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2935()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(RESTRICTABLE);
        proptery.setExpectedProprtyKey(getRestrictableAspectKey());

        removeAspectFolderKey(proptery);
    }

    /*
     * the following test is commented because visibility of the aspect isn't set by default in share-form.config file
     */

    // @Test(groups={"DataPrepDocumentLibrary"})
    // public void dataPrep_ALF_2930() throws Exception
    // {
    // removeAspectDataPrepFolder(getTestName());
    //
    // }
    //
    // @Test(groups="EnterpriseOnly")
    // public void ALF_2930()
    // {
    // AspectTestProptery proptery = new AspectTestProptery();
    // proptery.setTestName(getTestName());
    // proptery.setAspect(DUBLIN_CORE);
    // proptery.setExpectedProprtyKey(getDublinCoreAspectKey());
    //
    // removeAspectFolderKey(proptery);
    // }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2932() throws Exception
    {
        removeAspectDataPrepFolder(getTestName());

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2932()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(SUMMARIZABLE);
        proptery.setExpectedProprtyKey(getSummarisableAspectKey());

        removeAspectFolder(proptery);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2936() throws Exception
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

    @Test(groups = "EnterpriseOnly")
    public void ALF_2936()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        DocumentLibraryPage documentLibraryPage = null;
        AspectTestProptery proptery = new AspectTestProptery();

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

        // Open folder details page
        FolderDetailsPage detailsPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        Map<String, Object> properties = detailsPage.getProperties();

        // Check and Set property size before adding aspect
        proptery.setSizeBeforeAspectAdded(properties.size());

        // Click 'Change Type' action
        ChangeTypePage changeTypePage = detailsPage.selectChangeType().render();
        assertTrue(changeTypePage.isChangeTypeDisplayed());

        List<String> types = changeTypePage.getTypes();
        assertTrue(types.contains("Select type..."));

        // Select any type if present and click Cancel
        int typeCount = types.size();

        if (typeCount > 1)
        {
            for (int i = 0; i <= 1; i++)
            {
                String randomType = types.get(1);
                changeTypePage.selectChangeType(randomType);
                if (i == 0)
                {
                    changeTypePage.selectCancel().render();
                    properties = detailsPage.getProperties();
                    assertEquals(properties.size(), proptery.getSizeBeforeAspectAdded());
                    changeTypePage = detailsPage.selectChangeType().render();
                }
                // Click Change Type again, select any type and click OK
                if (i == 1)
                {
                    changeTypePage.selectSave().render();
                    properties = detailsPage.getProperties();
                    Assert.assertNotSame(properties.size(), proptery.getSizeBeforeAspectAdded());
                }

            }

        }
        else
        {
            changeTypePage.selectCancel();
            properties = detailsPage.getProperties();
            assertEquals(properties.size(), proptery.getSizeBeforeAspectAdded());
        }
        ShareUser.logout(drone);

    }

    /*
     * the following test is commented because visibility of the aspect isn't set by default in share-form.config file
     */
    // @Test(groups={"DataPrepDocumentLibrary"})
    // public void dataPrep_ALF_2934() throws Exception
    // {
    // removeAspectDataPrepFolder(getTestName());
    //
    // }
    //
    // @Test(groups="EnterpriseOnly")
    // public void ALF_2934()
    // {
    // AspectTestProptery proptery = new AspectTestProptery();
    // proptery.setTestName(getTestName());
    // proptery.setAspect(EMAILED);
    // proptery.setExpectedProprtyKey(getEmailedAspectKey());
    //
    // removeAspectFolderKey(proptery);
    // }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_14015() throws Exception
    {

        String testName = getTestName();
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);

        // Create 2 users
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        String testUser2 = getUserNameFreeDomain(testName + "1");
        String[] testUserInfo2 = new String[] { testUser2 };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login with the first user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Invite user2 to the created site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.MANAGER);
        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly" })
    public void Enterprise40x_14015()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser2 = getUserNameFreeDomain(testName + "1");

        // Login
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Modify the created by site creator folder
        EditDocumentPropertiesPage folderDetailsPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectEditProperties().render();
        folderDetailsPage.setName(folderName + "new");
        documentLibraryPage = folderDetailsPage.selectSave().render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(folderName + "new"), "The folder isn't modified");

        // Open Document Library page and verify modified by field
        FileDirectoryInfo folderInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName + "new");
        assertTrue(folderInfo.getContentEditInfo().contains(testUser2));

        // Verify modifier of the folder on Details page
        FolderDetailsPage detailsPage = folderInfo.selectViewFolderDetails().render();
        assertTrue(detailsPage.IsModifierDisplayed(drone, testUser2), "Modifier of the folder isn't displayed");
        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_14016() throws Exception
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
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Create Subfolder
        ShareUser.createFolderInFolder(drone, folderName + 1, folderName + 1, folderName);

        ShareUser.logout(drone);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void Enterprise40x_14016()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Open folder
        documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();

        // Open details page of the subfolder
        FolderDetailsPage folderDetailsPage = documentLibraryPage.getFileDirectoryInfo(folderName + 1).selectViewFolderDetails().render();

        // Click on parent folder link in the Path
        documentLibraryPage = folderDetailsPage.navigateToParentFolder().render();

        assertTrue(documentLibraryPage.isFileVisible(folderName + 1), "The subfolder isn't displayed");

        ShareUser.logout(drone);

    }



}