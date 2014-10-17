package org.alfresco.share.user.trashcan;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.user.*;
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
 * @author Maryia Zaichanka
 */

@Listeners(FailedTestListener.class)
public class TrashcanTest2 extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(TrashcanTest2.class);

    private String format = "EEE d MMM YYYY";

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


    @AfterMethod(groups = { "AlfrescoOne" })
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

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14193() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);
        String trashcanUser = getUserNameFreeDomain(testName);

        String fileName1 = getFileName(testName);
        String folderName1 = getFolderName(testName);

        for (int i = 1; i <= 2; i++)
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser + i);
        }

        ShareUser.login(drone, trashcanUser + 1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14193() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String fileName1 = getFileName(testName);
        String folderName1 = getFolderName(testName);

        // Log in as User2
        ShareUser.login(drone, trashcanUser + 2, DEFAULT_PASSWORD);

        // Navigate to My Profile
        // Click on Trashcan
        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone).render();

        // Verify the trashcan
        Assert.assertFalse(trashCanPage.hasTrashCanItems(), "A trashcan isn't empty");

        // Try to search for deleted items
        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");

        Assert.assertFalse(nameOfItems.contains(fileName1), "The deleted item is present in other user's trashcan");
        Assert.assertFalse(nameOfItems.contains(folderName1), "The deleted item is present in other user's trashcan");

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14185() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String searchText = getRandomString(5);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser);

        // Log in as created user
        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        // Trashcan is opened
        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone).render();

        // Fill in 'Search' field with any string
        ShareUserProfile.searchInput(drone).sendKeys(searchText);
        Assert.assertFalse(ShareUserProfile.getInputText(drone).contains(searchText), "Search field isn't filled with search text");

        // Click 'Clear' button
        trashCanPage.clearSearch().render();
        Assert.assertTrue(ShareUserProfile.getInputText(drone).isEmpty(), "Search field isn't cleared");

        // Do no fill in any text and click 'Clear' button
        trashCanPage.clearSearch().render();
        Assert.assertTrue(ShareUserProfile.getInputText(drone).isEmpty(), "Search field isn't cleared");

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14173() throws Exception
    {
        String testName = getTestName();

        String trashcanUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14173() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        String fileName1 = getFileName(testName) + "-1" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-2" + System.currentTimeMillis();
        String folderName1 = getFolderName(testName) + "-3" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-4" + System.currentTimeMillis();

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);

        DocumentLibraryPage docLibPage = ShareUser.deleteSelectedContent(drone);

        Assert.assertFalse(docLibPage.isFileVisible(fileName1), fileName1 + "is deleted");
        Assert.assertFalse(docLibPage.isFileVisible(fileName2), fileName2 + "is deleted");
        Assert.assertFalse(docLibPage.isFileVisible(folderName1), folderName1 + "is deleted");
        Assert.assertFalse(docLibPage.isFileVisible(folderName2), folderName2 + "is deleted");

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, fileName1).contains(fileName1));
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, fileName2).contains(fileName2));
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, folderName1).contains(folderName1));
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, folderName2).contains(folderName2));

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14175() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        for (int i = 0; i < 4; i++)
        {
            ShareUser.uploadFileInFolder(drone, new String[] { i + "-" + fileName, DOCLIB }).render();
            ShareUserSitePage.createFolder(drone, i + "-" + folderName, i + "-" + folderName);
        }
        for (int i = 0; i < 3; i++)
        {
            ShareUser.createCopyOfAllContent(drone);
        }

        ShareUser.deleteAllContentFromDocumentLibrary(drone);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14175() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone);

        // TODO: Naved: Missing Step 3: Click on Page 2

        // TODO: Naved: Missing Step 4: Click on Page 1

        Assert.assertTrue(trashCanPage.hasNextPage());

        trashCanPage.selectNextPage();

        Assert.assertTrue(trashCanPage.render().hasPreviousPage());

        trashCanPage.render().selectPreviousPage();

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14174() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14174() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        String fileName1 = getFileName(testName) + "-1" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-2" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-1" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-2" + System.currentTimeMillis();

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);

        ShareUser.deleteSelectedContent(drone);
        String dateOfContentDeletion = ShareUser.getDate(format);

        ShareUserProfile.navigateToTrashCan(drone);

        TrashCanItem itemInfo = ShareUserProfile.getTrashCanItem(drone, folderName1);

        Assert.assertEquals(itemInfo.getFileName(), folderName1);
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion), String.format("Date displayed: %s, Date Expected: %s", itemInfo.getDate(), dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith(DOCLIB_CONTAINER));
        Assert.assertTrue(itemInfo.getUserFullName().startsWith(trashcanUser));

        itemInfo = ShareUserProfile.getTrashCanItem(drone, folderName2);

        Assert.assertEquals(itemInfo.getFileName(), folderName2);
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith(DOCLIB_CONTAINER));
        Assert.assertTrue(itemInfo.getUserFullName().startsWith(trashcanUser));

        itemInfo = ShareUserProfile.getTrashCanItem(drone, fileName1);

        Assert.assertEquals(itemInfo.getFileName(), fileName1);
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith(DOCLIB_CONTAINER));
        Assert.assertTrue(itemInfo.getUserFullName().startsWith(trashcanUser));

        itemInfo = ShareUserProfile.getTrashCanItem(drone, fileName2);

        Assert.assertEquals(itemInfo.getFileName(), fileName2);
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith(DOCLIB_CONTAINER));
        Assert.assertTrue(itemInfo.getUserFullName().startsWith(trashcanUser));
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14176() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    /**
     * AONE_14176
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_14176() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);
        String siteName1 = getSiteName(testName) + "-1" + System.currentTimeMillis();
        String siteName2 = getSiteName(testName) + "-2" + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-1" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-2" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-3" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-4" + System.currentTimeMillis();

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Sites
        SiteUtil.createSite(drone, siteName1, siteName1, SITE_VISIBILITY_PUBLIC, true);
        SiteUtil.createSite(drone, siteName2, siteName2, SITE_VISIBILITY_PUBLIC, true);

        // Delete Sites
        SiteUtil.deleteSite(drone, siteName1);
        SiteUtil.deleteSite(drone, siteName2);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        // Select Files and Folders created above
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);

        ShareUser.selectContentCheckBox(drone, folderName1);
        DocumentLibraryPage docLibPage = ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        ShareUserProfile.navigateToTrashCan(drone);

        // Recover site, file and folder
        ShareUserProfile.recoverTrashCanItem(drone, siteName1.toLowerCase());

        ShareUserProfile.recoverTrashCanItem(drone, fileName1);

        ShareUserProfile.recoverTrashCanItem(drone, folderName1);

        Assert.assertTrue(ShareUser.openSiteDashboard(drone, siteName1).isSite(siteName1));

        // Confirm right files are recovered
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertTrue(docLibPage.isFileVisible(fileName1));
        Assert.assertFalse(docLibPage.isFileVisible(fileName2));

        // Confirm right folders are recovered
        Assert.assertTrue(docLibPage.isFileVisible(folderName1));
        Assert.assertFalse(docLibPage.isFileVisible(folderName2));

        // Confirm certain files and right folders are still in trashcan as expected
        ShareUserProfile.navigateToTrashCan(drone);

        // Additional Step to check that the items not recovered are present in trashcan
        TrashCanItem trashCanItem = ShareUserProfile.getTrashCanItem(drone, siteName2);
        Assert.assertNotNull(trashCanItem);

        trashCanItem = ShareUserProfile.getTrashCanItem(drone, fileName2);
        Assert.assertNotNull(trashCanItem);

        trashCanItem = ShareUserProfile.getTrashCanItem(drone, folderName2);
        Assert.assertNotNull(trashCanItem);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14177() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    /**
     * AONE_14177
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_14177() throws Exception
    {
        // dataPrep_TrashCan_AONE_14177(drone);
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);
        String siteName1 = getSiteName(testName) + System.currentTimeMillis() + "-1";
        String siteName2 = getSiteName(testName) + System.currentTimeMillis() + "-2";

        String fileName1 = getFileName(testName) + "-1" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-2" + System.currentTimeMillis();
        String fileName3 = getFileName(testName) + "-5" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-3" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-4" + System.currentTimeMillis();
        String folderName3 = getFolderName(testName) + "-6" + System.currentTimeMillis();

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName3, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, folderName3, folderName3);

        // Select Content
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, fileName3);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);
        ShareUser.selectContentCheckBox(drone, folderName3);

        ShareUser.deleteSelectedContent(drone);
        SiteUtil.deleteSite(drone, siteName1);
        SiteUtil.deleteSite(drone, siteName2);

        ShareUserProfile.navigateToTrashCan(drone);

        ShareUserProfile.deleteTrashCanItem(drone, fileName1);

        ShareUserProfile.deleteTrashCanItem(drone, fileName2);

        ShareUserProfile.deleteTrashCanItem(drone, folderName1);

        ShareUserProfile.deleteTrashCanItem(drone, folderName2);

        ShareUserProfile.deleteTrashCanItem(drone, siteName1);

        ShareUserProfile.deleteTrashCanItem(drone, siteName2);

        Assert.assertFalse(SiteUtil.getSiteFinder(drone).getSiteList().contains(ShareUser.getSiteShortname(siteName1)));
        Assert.assertFalse(SiteUtil.getSiteFinder(drone).getSiteList().contains(ShareUser.getSiteShortname(siteName2)));

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertFalse(docLibPage.isFileVisible(fileName1));
        Assert.assertFalse(docLibPage.isFileVisible(fileName2));

        Assert.assertFalse(docLibPage.isFileVisible(fileName3));

        Assert.assertFalse(docLibPage.isFileVisible(folderName1));
        Assert.assertFalse(docLibPage.isFileVisible(folderName2));

        Assert.assertFalse(docLibPage.isFileVisible(folderName3));

        ShareUserProfile.navigateToTrashCan(drone);

        TrashCanItem trashCanItem = ShareUserProfile.getTrashCanItem(drone, fileName3);
        Assert.assertTrue(trashCanItem.getFileName().contains(fileName3));

        trashCanItem = ShareUserProfile.getTrashCanItem(drone, folderName3);
        Assert.assertTrue(trashCanItem.getFileName().contains(folderName3));

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14178() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser });

    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14178() throws Exception
    {
        // dataPrep_TrashCan_AONE_14178(drone);
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);

        String siteName1 = getSiteName(testName) + "-1" + System.currentTimeMillis();
        String siteName2 = getSiteName(testName) + "-2" + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-3" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-4" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-5" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-6" + System.currentTimeMillis();

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        // Select Files
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);

        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        SiteUtil.deleteSite(drone, siteName1);
        SiteUtil.deleteSite(drone, siteName2);

        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone);

        ShareUserProfile.emptyTrashCan(drone, SyncInfoPage.ButtonType.CANCEL);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, siteName1));
        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, folderName1));
        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, fileName1));

        ShareUserProfile.emptyTrashCan(drone, SyncInfoPage.ButtonType.REMOVE);

        Assert.assertFalse(trashCanPage.hasTrashCanItems());

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14179() throws Exception
    {
        String testName = getTestName();
        String siteName1 = getSiteName(testName) + "-s1";

        String trashcanUser1 = getUserNameFreeDomain(testName + "1");
        String trashcanUser2 = getUserNameFreeDomain(testName + "2");

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser2 });

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14179() throws Exception
    {
        // dataPrep_TrashCan_AONE_14179(drone);
        String testName = getTestName();
        String trashcanUser1 = getUserNameFreeDomain(testName + "1");
        String trashcanUser2 = getUserNameFreeDomain(testName + "2");

        String siteName1 = getSiteName(testName) + "-s1";

        String fileName1 = getFileName(testName) + "-fi1" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-fo2" + System.currentTimeMillis();

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        // Select File and Folder
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, folderName1);

        ShareUser.deleteSelectedContent(drone);

        SiteUtil.deleteSite(drone, siteName1);

        ShareUser.logout(drone);

        ShareUser.login(drone, trashcanUser2, DEFAULT_PASSWORD);

        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertFalse(trashCanPage.hasTrashCanItems());

        Assert.assertFalse(ShareUserProfile.getTrashCanItems(drone, siteName1).contains(siteName1));

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, fileName1));

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName1));

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14180() throws Exception
    {
        String testName = getTestName();

        String siteName1 = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName + "1");

        String fileName1 = "f1-" + getFileName(testName) + ".txt";
        String fileName2 = "f2-" + getFileName(testName) + ".txt";

        String folderName1 = "f3-" + getFolderName(testName);
        String folderName2 = "f4-" + getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

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
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14180() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName + "1");

        String fileName1 = "f1-" + getFileName(testName) + ".txt";
        String fileName2 = "f2-" + getFileName(testName) + ".txt";

        String folderName1 = "f3-" + getFolderName(testName);
        String folderName2 = "f4-" + getFolderName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone);

        // Search for file includes file in the results
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, fileName1).contains(fileName1));

        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, fileName2).contains(fileName2));
        Assert.assertTrue(trashCanPage.getTrashCanItemForContent(TrashCanValues.FILE, fileName2, "documentLibrary").size() > 0);

        // Search for folder includes folder in the results
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, folderName1).contains(folderName1));

        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, folderName2).contains(folderName2));
        Assert.assertTrue(trashCanPage.getTrashCanItemForContent(TrashCanValues.FOLDER, folderName2, "documentLibrary").size() > 0);

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14181() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName + "1");

        String fileName1 = "fi1-" + getFileName(testName);
        String fileName2 = "fi2-" + getFileName(testName);

        String folderName1 = "fo3-" + getFolderName(testName);
        String folderName2 = "fo4-" + getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

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
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14181() throws Exception
    {
        // dataPrep_TrashCan_AONE_14181(drone);
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1");

        String fileName1 = "fi1-" + getFileName(testName);
        String fileName2 = "fi2-" + getFileName(testName);

        String folderName1 = "fo3-" + getFolderName(testName);
        String folderName2 = "fo4-" + getFolderName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        // Empty String search
        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");

        Assert.assertTrue(nameOfItems.contains(fileName1));
        Assert.assertTrue(nameOfItems.contains(fileName2));
        Assert.assertTrue(nameOfItems.contains(folderName1));
        Assert.assertTrue(nameOfItems.contains(folderName2));

        // Search: ends with common term: *testName
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "*" + testName);

        Assert.assertTrue(nameOfItems.contains(fileName1));
        Assert.assertTrue(nameOfItems.contains(fileName2));
        Assert.assertTrue(nameOfItems.contains(folderName1));
        Assert.assertTrue(nameOfItems.contains(folderName2));

        // Search: starts with common term: fo*
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "fo*");
        Assert.assertFalse(nameOfItems.contains(fileName1));
        Assert.assertFalse(nameOfItems.contains(fileName2));
        Assert.assertTrue(nameOfItems.contains(folderName1));
        Assert.assertTrue(nameOfItems.contains(folderName2));

        // Search: starts with common term: fi*
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "fi*");
        Assert.assertTrue(nameOfItems.contains(fileName1));
        Assert.assertTrue(nameOfItems.contains(fileName2));
        Assert.assertFalse(nameOfItems.contains(folderName1));
        Assert.assertFalse(nameOfItems.contains(folderName2));

        nameOfItems = ShareUserProfile.getTrashCanItems(drone, fileName1 + "*");
        Assert.assertTrue(nameOfItems.contains(fileName1));
        Assert.assertFalse(nameOfItems.contains(fileName2));
        Assert.assertFalse(nameOfItems.contains(folderName1));
        Assert.assertFalse(nameOfItems.contains(folderName2));

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14182() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName + "1");

        String fileName1 = "fi1-" + getFileName(testName);
        String fileName2 = "fi2-" + getFileName(testName);

        String folderName1 = "fo3-" + getFolderName(testName);
        String folderName2 = "fo4-" + getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
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
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14182() throws Exception
    {

        String testName = getTestName();
        String trashcanUser1 = getUserNameFreeDomain(testName + "1");
        String fileName1 = "fi1-" + getFileName(testName);
        String fileName2 = "fi2-" + getFileName(testName);
        String folderName1 = "fo3-" + getFolderName(testName);
        String folderName2 = "fo4-" + getFolderName(testName);

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");

        Assert.assertTrue(nameOfItems.contains(fileName1));
        Assert.assertTrue(nameOfItems.contains(fileName2));
        Assert.assertTrue(nameOfItems.contains(folderName1));
        Assert.assertTrue(nameOfItems.contains(folderName2));

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14184() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);
        String trashcanUser1 = getUserNameFreeDomain(testName + "1");
        String trashcanUser2 = getUserNameFreeDomain(testName + "2");

        String fileName1 = getFileName(testName);
        String folderName1 = getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser2 });

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        ShareUser.selectContentCheckBox(drone, fileName1);

        ShareUser.selectContentCheckBox(drone, folderName1);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14184() throws Exception
    {
        String testName = getTestName();

        String trashcanUser1 = getUserNameFreeDomain(testName + "1");
        String trashcanUser2 = getUserNameFreeDomain(testName + "2");

        String fileName1 = getFileName(testName);

        String folderName1 = getFolderName(testName);

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        String url = getShareUrl();
        if (alfrescoVersion.isCloud())
        {
            url = url + "/" + getUserDomain(trashcanUser2);
        }

        drone.navigateTo(url + "/page/user/" + trashcanUser2 + "/user-trashcan");

        ((TrashCanPage) drone.getCurrentPage()).render();
        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertTrue(nameOfItems.contains(fileName1));

        Assert.assertTrue(nameOfItems.contains(folderName1));

        Assert.assertTrue(drone.getCurrentUrl().contains(trashcanUser2));
        Assert.assertFalse(drone.getCurrentUrl().contains(trashcanUser1));

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14171() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser);

        // Log in as created user
        SharePage sharePage = ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        //Navigate to My Profile
        MyProfilePage myProfilePage = sharePage.getNav().selectMyProfile().render();
        Assert.assertTrue(myProfilePage.isTrashcanLinkDisplayed(), "Trashcan isn't present");
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14172() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser);

        // Log in as created user
        SharePage sharePage = ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        // Navigate to My Profile
        // Click on Trashcan
        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone).render();

        //Verify Trashcan page
        Assert.assertTrue (trashCanPage.isPageCorrect());

    }


}
