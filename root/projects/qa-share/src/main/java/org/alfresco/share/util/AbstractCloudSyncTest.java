/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

package org.alfresco.share.util;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.CreateNewFolderInCloudPage;
import org.alfresco.po.share.site.DestinationAndAssigneeBean;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryNavigation;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

/**
 * This class is responsible for holding all common methods related hybrid
 * functionalities.
 * 
 * @author Abhijeet Bharade
 */
public abstract class AbstractCloudSyncTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(AbstractCloudSyncTest.class);
    protected String onPremUser;
    protected String cloudUser = DEFAULT_PREMIUMNET_USER;
    protected String siteName;
    protected String cloudUserSiteName;
    protected static final String DEFAULT_FOLDER_NAME = "Documents";
    protected static final String FORGOT_PASSWORD_LINK_URL = "https://my.alfresco.com/share/page/forgot-password";
    protected String hybridDomainFree;
    protected String hybridDomainPremium;
    protected String adminUserFree;
    protected String adminUserPrem;

    @Override
    public void setup() throws Exception
    {
        super.setup();

        hybridDomainFree = DOMAIN_FREE;
        hybridDomainPremium = DOMAIN_PREMIUM;
        adminUserFree = getUserNameForDomain("admin", hybridDomainFree);
        adminUserPrem = getUserNameForDomain("admin", hybridDomainPremium);

        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, adminUserFree);
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, adminUserPrem);
        
        if (hybridEnabled)
        {
            setupHybridDrone();
            CreateUserAPI.createActivateUserAsTenantAdmin(hybridDrone, ADMIN_USERNAME, adminUserFree);
            CreateUserAPI.createActivateUserAsTenantAdmin(hybridDrone, ADMIN_USERNAME, adminUserPrem);
            CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, hybridDomainPremium, "1000");
        }
    }

    @Override
    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        super.tearDown();
        if (logger.isTraceEnabled())
        {
            logger.trace("shutting cloud web drone");
        }
    }

    /**
     * Method to Set Up Cloud sync by admin and returns CloudSyncPage
     * 
     * @param drone
     * @param cloudUserName
     * @param password
     * @return CloudSyncPage
     */
    public CloudSyncPage signInToAlfrescoInTheCloud(WebDrone drone, String cloudUserName, String password)
    {
        CloudSyncPage cloudSyncPage = navigateToCloudSync(drone);

        if (cloudSyncPage.isDisconnectButtonDisplayed())
        {
            cloudSyncPage = cloudSyncPage.disconnectCloudAccount().render();
        }

        CloudSignInPage cloudSignInPage = cloudSyncPage.selectCloudSign().render();
        cloudSyncPage = cloudSignInPage.loginAs(cloudUserName, password).render();

        return cloudSyncPage;
    }

    /**
     * Method to login and configure cloud sync (Logs out the user afterwords)
     * 
     * @param drone
     * @param onPremUserName
     * @param onPremPassword
     * @param cloudUserName
     * @param cloudUserPassword
     */
    // TODO: Abhijit: remove unused. Avoid login, logout from helper methods to
    // enhance visibility of steps within the tests
    public void loginAndConfigureCloudSync(WebDrone drone, String onPremUserName, String onPremPassword, String cloudUserName, String cloudUserPassword)
    {
        ShareUser.login(drone, onPremUserName, onPremPassword);
        signInToAlfrescoInTheCloud(drone, cloudUserName, cloudUserPassword);
        ShareUser.logout(drone);
    }

    /**
     * Method to select the sitename of destination and clicks on sync button
     * 
     * @param fileName
     * @param siteName
     * @param drone
     * @return {@link DocumentDetailsPage}
     */
    protected DocumentDetailsPage selectDestinationAndSync(WebDrone drone, String fileName, String siteName)
    {
        DestinationAndAssigneePage assigneePage = (DestinationAndAssigneePage) getSharePage(drone);
        assigneePage.selectSite(siteName);
        assigneePage.selectFolder(fileName);

        return assigneePage.selectSubmitButtonToSync().render();
    }

    /**
     * Method to select the cloudToSync option on document details Page
     * 
     * @param drone
     * @param fileName
     * @return {@link DestinationAndAssigneePage}
     */
    public static DestinationAndAssigneePage selectSyncToCloudDocLib(WebDrone drone, String fileName)
    {
        DocumentLibraryPage docLibPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);
        FileDirectoryInfo contentRow = docLibPage.getFileDirectoryInfo(fileName);
        DestinationAndAssigneePage assigneePage = (DestinationAndAssigneePage) contentRow.selectSyncToCloud();
        assigneePage.render();
        return assigneePage;
    }

    /**
     * Method to select the cloudToSync option on document details Page
     * 
     * @param drone
     * @return {@link DestinationAndAssigneePage}
     */
    public static DestinationAndAssigneePage selectSyncToCloud(WebDrone drone)
    {
        DocumentDetailsPage docsPage = (DocumentDetailsPage) getSharePage(drone);
        DestinationAndAssigneePage assigneePage = (DestinationAndAssigneePage) docsPage.selectSyncToCloud();
        assigneePage.render();
        return assigneePage;
    }

    /**
     * Helper method to sync on-premise content to cloud. It assumes the right
     * user is logged in and on the DocumentLibraryPage for the selected Site
     * 
     * @param drone
     * @param siteName
     * @param contentName
     * @return DocumentLibraryPage
     */
    public static DocumentLibraryPage requestSyncToCloud(WebDrone drone, String siteName, String contentName)
    {
        DocumentLibraryPage docLibPage = (DocumentLibraryPage) getSharePage(drone);
        try
        {
            if (!isAlfrescoVersionCloud(drone))
            {
                docLibPage = docLibPage.getFileDirectoryInfo(contentName).selectRequestSync().render();
            }
            else
            {
                throw new UnsupportedOperationException("This operation is not supported for Cloud: Request Sync To Cloud");
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
        return docLibPage;
    }

    /**
     * Sync all content.
     * 
     * @param drone
     * @param desAndAssBean
     * @return
     */
    // TODO@ naved: update JavaDocs to say, if the content is expected to be
    // selected before this util is called
    public static HtmlPage syncAllContentToCloud(WebDrone drone, DestinationAndAssigneeBean desAndAssBean)
    {
        try
        {
            DocumentLibraryPage docLibPage = ((DocumentLibraryPage) getSharePage(drone));

            DocumentLibraryNavigation docLibNav = docLibPage.getNavigation().render();
            DestinationAndAssigneePage desAndAssPage = ((DestinationAndAssigneePage) docLibNav.selectSyncToCloud()).render();

            desAndAssPage.selectNetwork(desAndAssBean.getNetwork());
            desAndAssPage.selectSite(desAndAssBean.getSiteName());
            desAndAssPage.selectFolder(desAndAssBean.getSyncToPath());

            if (desAndAssBean.isLockOnPrem())
                desAndAssPage.selectLockOnPremCopy();
            if (desAndAssBean.isExcludeSubFolder())
                desAndAssPage.unSelectIncludeSubFolders();

            // Sync content by clicking and submitting all the details.
            return desAndAssPage.selectSubmitButtonToSync();
        }
        catch (IllegalArgumentException excp)
        {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Select content from Document Library content row and sync. Note: Returns
     * HtmlPage, which needs rendering as appropriate Page Object
     * 
     * @param drone
     * @param contentName
     *            String name of the content to be synced
     * @param desAndAssBean
     *            {@link DestinationAndAssigneeBean}
     * @return DocumentLibraryPage
     */
    public static DocumentLibraryPage syncContentToCloud(WebDrone drone, String contentName, DestinationAndAssigneeBean desAndAssBean)
    {
        try
        {
            DocumentLibraryPage docLibpage = (DocumentLibraryPage) getSharePage(drone);
            DestinationAndAssigneePage desAndAssPage = null;

            if (!(contentName == null || contentName.isEmpty()))
            {
                desAndAssPage = ((DestinationAndAssigneePage) (docLibpage.getFileDirectoryInfo(contentName).selectSyncToCloud())).render();
            }
            else
            {
                desAndAssPage = ((DestinationAndAssigneePage) docLibpage.getNavigation().render().selectSyncToCloud()).render();
            }
            desAndAssPage.selectNetwork(desAndAssBean.getNetwork());
            desAndAssPage.selectSite(desAndAssBean.getSiteName());
            desAndAssPage.selectFolder(desAndAssBean.getSyncToPath());

            if (desAndAssBean.isLockOnPrem())
                desAndAssPage.selectLockOnPremCopy();

            if (desAndAssBean.isExcludeSubFolder())
                desAndAssPage.unSelectIncludeSubFolders();

            // Sync content by clicking and submitting all the details.
            return (DocumentLibraryPage) desAndAssPage.selectSubmitButtonToSync().render();
        }
        catch (IllegalArgumentException excp)
        {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * This method unsyncs the given site's document from cloud. User should be
     * logged in and on DocumentLibraryPage for the selected Site
     * 
     * @param driver
     * @param fileName
     */
    // TODO: Naved: Is this method specific to Ent / cloud, throw
    // unsupportedOperationException, if alfrescoVersion is not appropriate
    public static void unSyncFromCloud(WebDrone driver, String fileName)
    {
        DocumentLibraryPage documentLibraryPage = (DocumentLibraryPage) getSharePage(driver);
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName).render();
        if (documentDetailsPage.isFileSyncSetUp())
        {
            documentDetailsPage.selectUnSyncFromCloud().render();
        }
        else
        {
            logger.info("File sync is not setup on file : " + fileName);
        }
    }

    /**
     * This method is used to disconnects the cloud sync in the cloud sync page.
     * User should be logged in already.
     * 
     * @param driver
     *            WebDrone
     * @return CloudSyncPage
     */
    public static CloudSyncPage disconnectCloudSync(WebDrone driver)
    {
        MyProfilePage myProfilePage = ShareUser.getSharePage(driver).getNav().selectMyProfile().render();

        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();

        if (cloudSyncPage.isDisconnectButtonDisplayed())
        {
            cloudSyncPage.disconnectCloudAccount().render();
        }

        return cloudSyncPage;
    }

    /**
     * Method to Navigate to CloudSync Sign up Page
     * 
     * @param drone
     * @return CloudSyncPage
     */
    public static CloudSyncPage navigateToCloudSync(WebDrone drone)
    {
        SharePage sharePage = ShareUser.getSharePage(drone);

        MyProfilePage myProfilePage = sharePage.getNav().selectMyProfile().render();
        return myProfilePage.getProfileNav().selectCloudSyncPage().render();
    }

    /**
     * This method is used to get sync status (with retry) for a content from
     * document library page and returns true if the content synced otherwise
     * false. Since cloud sync is not instantaneous, the method keeps retrying
     * until maxWaitTime_CloudSync is reached This method could be invoked after
     * syncToCloud is initiated from document library page.
     * 
     * @param driver
     * @param fileName
     * @return boolean
     */
    public static boolean checkIfContentIsSynced(WebDrone driver, String fileName)
    {
        DocumentLibraryPage docLibPage = (DocumentLibraryPage) getSharePage(driver);
        docLibPage = docLibPage.renderItem(maxWaitTime, fileName);

        String status = "";
        SyncInfoPage syncInfoPage;

        try
        {
            RenderTime t = new RenderTime(maxWaitTime_CloudSync);
            while (true)
            {
                t.start();
                try
                {
                    syncInfoPage = docLibPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();
                    status = syncInfoPage.getCloudSyncStatus();
                    syncInfoPage.clickOnCloseButton();

                    // if
                    // (status.contains(driver.getLanguageValue("sync.status.pending.text")))
                    if (status.contains("Pending"))
                    {
                        webDriverWait(driver, 1000);
                        // Changing to drone.refresh so that it runs from
                        // RepositoryPage too
                        // docLibPage = ShareUser.openDocumentLibrary(driver);
                        driver.refresh();
                        docLibPage = driver.getCurrentPage().render();
                        docLibPage = docLibPage.renderItem(maxWaitTime, fileName).render();
                    }
                    else
                    {
                        // return
                        // status.contains(driver.getLanguageValue("sync.status.synced.text"));
                        return status.contains("Synced");
                    }
                }
                finally
                {
                    t.end();
                }
            }
        }
        catch (PageException e)
        {
        }
        catch (PageRenderTimeException exception)
        {
        }

        return false;
    }

    /**
     * Navigate to Sync Info Page.
     * 
     * @param drone
     * @param content
     * @return SyncInfoPage
     */
    public static SyncInfoPage navigateToSyncInfoPage(WebDrone drone, String content)
    {
        DocumentLibraryPage doclibPage = (DocumentLibraryPage) getSharePage(drone);
        return doclibPage.getFileDirectoryInfo(content).clickOnViewCloudSyncInfo().render();

    }

    /**
     * Sync to cloud by creating a new folder
     * 
     * @param - drone
     * @param - {@link DestinationAndAssigneeBean}
     * @param - ContentName
     * @param - FolderName
     * @retrun - DocumentLibraryPage
     * @throw - IllegalArgumentException
     */
    public static DocumentLibraryPage createNewFolderAndSyncContent(WebDrone drone, String contentName, DestinationAndAssigneeBean desAndAssBean,
        String folderName)
    {
        try
        {
            DocumentLibraryPage docLibpage = (DocumentLibraryPage) getSharePage(drone);
            DestinationAndAssigneePage desAndAssPage = null;

            if (!(contentName == null || contentName.isEmpty()))
            {
                desAndAssPage = ((DestinationAndAssigneePage) (docLibpage.getFileDirectoryInfo(contentName).selectSyncToCloud())).render();
            }
            else
            {
                desAndAssPage = ((DestinationAndAssigneePage) docLibpage.getNavigation().render().selectSyncToCloud()).render();
            }
            desAndAssPage.selectNetwork(desAndAssBean.getNetwork());
            desAndAssPage.selectSite(desAndAssBean.getSiteName());
            desAndAssPage.selectFolder(desAndAssBean.getSyncToPath());

            // Create a new folder within site document library
            CreateNewFolderInCloudPage createNewFolderInCloudPage = desAndAssPage.selectCreateNewFolder().render();
            desAndAssPage = createNewFolderInCloudPage.createNewFolder(folderName).render();
            // Select the created folder and submit Sync
            Assert.assertTrue(desAndAssPage.isFolderDisplayed(folderName), "Verify created folder is displayed");
            desAndAssPage.selectFolder(folderName);

            if (desAndAssBean.isLockOnPrem())
            {
                desAndAssPage.selectLockOnPremCopy();
            }

            if (desAndAssBean.isExcludeSubFolder())
            {
                desAndAssPage.unSelectIncludeSubFolders();
            }
            // Sync content by clicking and submitting all the details.
            return (DocumentLibraryPage) desAndAssPage.selectSubmitButtonToSync().render();
        }
        catch (IllegalArgumentException excp)
        {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Method to UnSync a document and remove it from the Cloud.
     * 
     * @param drone
     * @param fileName
     * @return {@link DocumentLibraryPage}
     */
    protected DocumentLibraryPage selectUnSyncAndRemoveContentFromCloud(WebDrone drone, String fileName)
    {
        DocumentLibraryPage docLibPage = drone.getCurrentPage().render();

        return docLibPage.getFileDirectoryInfo(fileName).selectUnSyncAndRemoveContentFromCloud(true).render();
    }

    /**
     * Method to check if the cloud sync icon is displayed for a given file in
     * Document Library Page
     * 
     * @param drone
     * @param fileName
     * @return True if Cloud Sync icon is displayed
     */
    protected boolean isCloudSynced(WebDrone drone, String fileName)
    {
        DocumentLibraryPage docLibPage = drone.getCurrentPage().render();
        return docLibPage.getFileDirectoryInfo(fileName).isCloudSynced();
    }

    /**
     * This method is used wait for Version number increment in document detail
     * page
     * 
     * @param driver
     * @param expectedVersion
     * @return boolean
     */
    public static boolean checkForNewVersion(WebDrone driver, String expectedVersion)
    {
        DocumentDetailsPage docDetailPage = (DocumentDetailsPage) getSharePage(driver);
        docDetailPage.render();
        String newVersion;
        boolean results = false;
        try
        {
            RenderTime t = new RenderTime(maxWaitTime_CloudSync);
            while (true)
            {
                t.start();
                try
                {

                    newVersion = docDetailPage.getDocumentVersion();
                    if (!newVersion.equals(expectedVersion))
                    {
                        webDriverWait(driver, 1000);
                        driver.refresh();
                        docDetailPage.render();
                        results = false;

                    }
                    else
                    {
                        return results = true;
                    }
                }
                finally
                {
                    t.end();
                }
            }
        }
        catch (PageException e)
        {
        }
        catch (PageRenderTimeException exception)
        {
        }

        return results;
    }

    /**
     * This method is used to check if the description has been updated.
     * If the description hasn't been updated, it refreshes the page and verifies
     * the condition until given period of time
     *
     * @param driver
     * @param fileName
     * @param expectedDescription
     * @return boolean
     */
    public static boolean checkIfDescriptionIsUpdated(WebDrone driver, String fileName, String expectedDescription)
    {
        DocumentLibraryPage docLibPage = (DocumentLibraryPage) getSharePage(driver);
        docLibPage.render();

        String actualDescription = "";
        try
        {
            RenderTime t = new RenderTime(maxWaitTime_CloudSync);
            while (true)
            {
                t.start();
                try
                {
                    actualDescription = docLibPage.getFileDirectoryInfo(fileName).getDescription();
                    if (!expectedDescription.equals(actualDescription))
                    {
                        webDriverWait(driver, 1000);
                        driver.refresh();
                        docLibPage.render();
                    }
                    else
                    {
                        return true;
                    }
                }
                finally
                {
                    t.end();
                }
            }
        }
        catch (PageException e)
        {
        }
        catch (PageRenderTimeException exception)
        {
        }

        return false;
    }

}
