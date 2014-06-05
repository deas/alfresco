package org.alfresco.share.sanity.repository.bulkimport;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.bulkimport.BulkImportPage;
import org.alfresco.po.share.bulkimport.InPlaceBulkImportPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.*;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Calendar;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class BulkImportTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(BulkImportTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setupCustomDrone(WebDroneType.DownLoadDrone);
        testName = this.getClass().getSimpleName();
        logger.info("[Suite ] : Start Tests in: " + testName);
    }

    /**
     * Test - ALF-3127: Streaming import
     * <ul>
     * <li>Login</li>
     * <li>navigate to bulkfsimport</li>
     * <li>Fill mandatory parameters</li>
     * <li>Click Initiate Bulk Import button</li>
     * <li>After completion go to /Company Home/Bulk Import</li>
     * <li>Bulk Import dir contains all files from filesystem that were specified</li>
     * </ul>
     */
    @Test(groups = { "NonGrid", "EnterpriseOnly" }, timeOut = 300000)
    public void ALF_3127() throws Exception
    {
        /** Start Test */
        testName = getTestName();
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName1 = getRandomString(10) + ".txt";

        ShareUser.login(customDrone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

        DocumentLibraryPage docLibPage;
        ShareUser.openSitesDocumentLibrary(customDrone, siteName).render(maxWaitTime);

        // Create File for bulk import
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileName1);
        ShareUser.createContent(customDrone, contentDetails, ContentType.PLAINTEXT);
        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);
        thisRow.selectDownload();
        docLibPage.waitForFile(downloadDirectory + fileName1);

        // Thread needs to be stopped for some time to make sure the file download stream has been closed properly
        // otherwise download part will not be closed and will throws an exception.
        webDriverWait(customDrone, maxDownloadWaitTime);

        // Verify the file is downloaded or not.
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(fileName1), "File " + fileName1
                + " isn't downloaded");

        // Creating folder for bulk import
        docLibPage = ShareUserSitePage.createFolder(customDrone, folderName, null);
        String siteUrl = customDrone.getCurrentUrl();

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Get nodeRef for bulk import
        String nodeRef = docLibPage.getFileDirectoryInfo(folderName).getNodeRef();

        // navigate to http://host:port/alfresco/service/bulkfsimport
        ShareUtil.navigateToBulkImport(customDrone, false, ADMIN_USERNAME, ADMIN_PASSWORD);
        BulkImportPage bulkImportPage = customDrone.getCurrentPage().render();

        // Fill mandatory parameters / Click Initiate Bulk Import button
        // Bulk Import is initiated
        bulkImportPage.createImport(downloadDirectory, null, nodeRef, false, false);

        // After completion go to /Company Home/Bulk Import
        customDrone.navigateTo(siteUrl);
        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Navigating to folder
        docLibPage.selectFolder(folderName).render();

        // Verify Bulk Import dir contains all files from filesystem that were specified
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible. Bulk Import isn't created");
    }

    /**
     * Test - ALF-3128: In-Place import
     * <ul>
     * <li>Login</li>
     * <li>navigate to bulkfsimport/inplace</li>
     * <li>Fill mandatory parameters</li>
     * <li>Click Initiate Bulk Import button</li>
     * <li>After completion go to /Company Home/In-Place Bulk</li>
     * <li>Bulk Import dir contains all files from filesystem that were specified</li>
     * </ul>
     */
    @Test(groups = { "NonGrid", "EnterpriseOnly" }, timeOut = 300000)
    public void ALF_3128() throws Exception
    {
        /** Start Test */
        testName = getTestName();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String targetPath = "/Company Home/" + getFolderName(testName) + System.currentTimeMillis();

        // Login
        ShareUser.login(customDrone, ADMIN_USERNAME, ADMIN_PASSWORD);

        RepositoryPage repositoryPage;

        // go to Repository Browser,
        String folderPath = REPO;
        ShareUserRepositoryPage.navigateToFolderInRepository(customDrone, folderPath);

        // create Folder1 for In-Place import
        repositoryPage = ShareUserRepositoryPage.createFolderInRepository(customDrone, folderName, folderName, folderName);

        Assert.assertTrue(repositoryPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");
        String repoUrl = customDrone.getCurrentUrl();

        // navigate to http://host:port/alfresco/service/bulkfsimport/inplace
        ShareUtil.navigateToBulkImport(customDrone, true, ADMIN_USERNAME, ADMIN_PASSWORD);
        InPlaceBulkImportPage inPlaceBulkImportPage = customDrone.getCurrentPage().render();

        // Fill mandatory parameters / Click Initiate Bulk Import button
        // Bulk Import is initiated
        String importDirectory = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        inPlaceBulkImportPage.createImportInPlace(importDirectory, null, targetPath, false);

        // After completion go to /Company Home/Bulk Import
        customDrone.navigateTo(repoUrl);
        repositoryPage = customDrone.getCurrentPage().render();
        Assert.assertTrue(repositoryPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Navigating to folder
        repositoryPage = repositoryPage.selectFolder(folderName).render();

        // Verify Bulk Import dir contains all files from filesystem that were specified
        String currentMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
        Assert.assertTrue(repositoryPage.isFileVisible(currentMonth), "File/Folder " + currentMonth + " isn't visible. Bulk Import isn't created");
    }

    /**
     * Test - ALF-3129: Import. Replace existing files
     * <ul>
     * <li>At least one Bulk Import initiated and completed successfully</li>
     * <li>Change any file on filesystem from import that was run in pre-condition</li>
     * <li>initiate the same bulk import with "Replace Existing Files" set to false</li>
     * <li>verify the file has not been replaced</li>
     * <li>initiate the same bulk import with "Replace Existing Files" set to true</li>
     * <li>verify the file has been replaced</li>
     * </ul>
     */
    @Test(groups = { "NonGrid", "EnterpriseOnly" }, timeOut = 300000)
    public void ALF_3129() throws Exception
    {
        /** Start Test */
        testName = getTestName();
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName1 = getRandomString(10) + ".txt";
        String newContent = getRandomString(15);

        ShareUser.login(customDrone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

        DocumentLibraryPage docLibPage;
        ShareUser.openSitesDocumentLibrary(customDrone, siteName).render(maxWaitTime);

        // Create File for bulk import
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileName1);
        ShareUser.createContent(customDrone, contentDetails, ContentType.PLAINTEXT);
        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);
        thisRow.selectDownload();
        docLibPage.waitForFile(downloadDirectory + fileName1);

        // Thread needs to be stopped for some time to make sure the file download stream has been closed properly
        // otherwise download part will not be closed and will throws an exception.
        webDriverWait(customDrone, maxDownloadWaitTime);

        // Verify the file is downloaded or not.
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(fileName1), "File " + fileName1
                + " isn't downloaded");

        // Creating folder for bulk import
        docLibPage = ShareUserSitePage.createFolder(customDrone, folderName, null);
        String siteUrl = customDrone.getCurrentUrl();

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Get nodeRef for bulk import
        String nodeRef = docLibPage.getFileDirectoryInfo(folderName).getNodeRef();

        // navigate to http://host:port/alfresco/service/bulkfsimport
        ShareUtil.navigateToBulkImport(customDrone, false, ADMIN_USERNAME, ADMIN_PASSWORD);
        BulkImportPage bulkImportPage = customDrone.getCurrentPage().render();

        // Fill mandatory parameters / Click Initiate Bulk Import button
        // Bulk Import is initiated
        bulkImportPage.createImport(downloadDirectory, null, nodeRef, false, false);

        // After completion go to /Company Home/Bulk Import
        customDrone.navigateTo(siteUrl);
        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Navigating to folder
        docLibPage.selectFolder(folderName).render();

        // Verify Bulk Import dir contains all files from filesystem that were specified
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible. Bulk Import isn't created");

        // Change any file on filesystem from import that was run in pre-condition
        changeTextForDownloadDirectoryFile(fileName1, newContent);

        // initiate the same bulk import as was run in pre-condition with "Replace Existing Files" set to false
        ShareUtil.navigateToBulkImport(customDrone, false, ADMIN_USERNAME, ADMIN_PASSWORD);
        bulkImportPage = customDrone.getCurrentPage().render();

        // Fill mandatory parameters / Click Initiate Bulk Import button
        // Bulk Import is initiated
        bulkImportPage.createImport(downloadDirectory, null, nodeRef, false, false);

        customDrone.navigateTo(siteUrl);
        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Navigating to folder
        docLibPage.selectFolder(folderName).render();

        // Verify Bulk Import dir contains all files from filesystem that were specified
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible. Bulk Import isn't created");

        DocumentDetailsPage documentDetailsPage = docLibPage.selectFile(fileName1).render();

        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        contentDetails = inlineEditPage.getDetails();

        // verify the file has not been replaced
        Assert.assertEquals(contentDetails.getContent(), fileName1, "Content for file " + fileName1 + " was replaced");

        // initiate the same bulk import as was run in pre-condition with "Replace Existing Files" set to true
        ShareUtil.navigateToBulkImport(customDrone, false, ADMIN_USERNAME, ADMIN_PASSWORD);
        bulkImportPage = customDrone.getCurrentPage().render();

        // Fill mandatory parameters / Click Initiate Bulk Import button
        // Bulk Import is initiated
        bulkImportPage.createImport(downloadDirectory, null, nodeRef, false, true);

        customDrone.navigateTo(siteUrl);
        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Navigating to folder
        docLibPage.selectFolder(folderName).render();

        // Verify Bulk Import dir contains all files from filesystem that were specified
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible. Bulk Import isn't created");

        documentDetailsPage = docLibPage.selectFile(fileName1).render();

        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        contentDetails = inlineEditPage.getDetails();

        // verify the file has been replaced
        Assert.assertEquals(contentDetails.getContent(), newContent, "Content for file " + fileName1 + " wasn't replaced. The file isn't changed");

    }

    /**
     * Test - ALF-3130: Import. Disable rules
     * <ul>
     * <li>Login</li>
     * <li>Any rule created for any folder</li>
     * <li>initiate the bulk import with "Disable Rules" set to true</li>
     * <li>The rule isn't allied</li>
     * <li>initiate the bulk import with "Disable Rules" set to false</li>
     * <li>verify the file are imported and the rule is allied to them</li>
     * </ul>
     */
    @Test(groups = { "NonGrid", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_3130() throws Exception
    {
        /** Start Test */
        testName = getTestName();
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName1 = getRandomString(10) + ".txt";
        String folderName = getFolderName(testName);
        String ruleName = "Add Aspect Rule Name";
        String ruleDescription = "Add Aspect Rule Description";
        DocumentLibraryPage docLibPage;

        ShareUser.login(customDrone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

        // Creating folder for bulk import
        docLibPage = ShareUserSitePage.createFolder(customDrone, folderName, null);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // On server and create rule "testRule"
        docLibPage = (docLibPage.getNavigation().selectDetailedView()).render();
        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        docLibPage.getFileDirectoryInfo(folderName).selectMoreLink();
        FolderRulesPage folderRulesPage = folderInfo.selectManageRules().render(maxWaitTime);
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);
        createRulePage.fillDescriptionField(ruleDescription);

        // Select "Inbound" value from "When" drop-down select control
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select Add aspect" from "Perform Action" drop-down select control
        // Select 'Classifiable' from drop-down select control
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectAddAspect(DocumentAspect.CLASSIFIABLE.getValue());

        // Click "Create" button
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();

        // The rule is created successfully
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        ShareUser.openSitesDocumentLibrary(customDrone, siteName).render(maxWaitTime);

        // Create File for bulk import
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileName1);
        ShareUser.createContent(customDrone, contentDetails, ContentType.PLAINTEXT);
        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);
        thisRow.selectDownload();
        docLibPage.waitForFile(downloadDirectory + fileName1);

        // Thread needs to be stopped for some time to make sure the file download stream has been closed properly
        // otherwise download part will not be closed and will throws an exception.
        webDriverWait(customDrone, maxDownloadWaitTime);

        // Verify the file is downloaded or not.
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(fileName1), "File " + fileName1
                + " isn't downloaded");

        // Get nodeRef for bulk import
        String siteUrl = customDrone.getCurrentUrl();
        String nodeRef = docLibPage.getFileDirectoryInfo(folderName).getNodeRef();

        ShareUtil.navigateToBulkImport(customDrone, false, ADMIN_USERNAME, ADMIN_PASSWORD);
        BulkImportPage bulkImportPage = customDrone.getCurrentPage().render();

        // initiate the bulk import with "Disable Rules" set to true
        bulkImportPage.createImport(downloadDirectory, null, nodeRef, true, false);

        customDrone.navigateTo(siteUrl);
        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");
        // Navigating to folder
        docLibPage.selectFolder(folderName).render();

        // Verify Bulk Import dir contains all files from filesystem that were specified
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible. Bulk Import isn't created");

        DocumentDetailsPage detailsPage = docLibPage.selectFile(fileName1).render();

        // View 'Manage Aspects' page for the added item
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();

        // Verify the rule isn't applied
        Assert.assertFalse(aspectsPage.getSelectedAspects().contains(DocumentAspect.CLASSIFIABLE), "'Classifiable'' aspect is applied");

        aspectsPage.clickCancel();

        // remove all imported files before another import
        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");
        // Navigating to folder
        docLibPage.selectFolder(folderName).render();

        // Verify Bulk Import dir contains all files from filesystem that were specified
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible.");

        // Several folders are selected
        docLibPage = docLibPage.getNavigation().selectAll().render();

        // Click Delete from Selected items drop down
        ConfirmDeletePage confirmDeletePage = docLibPage.getNavigation().selectDelete().render();
        // Select the folders again and repeat step1. Click Delete
        docLibPage = confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render();

        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible. Files were not removed");

        // initiate the bulk import with "Disable Rules" set to false
        ShareUtil.navigateToBulkImport(customDrone, false, ADMIN_USERNAME, ADMIN_PASSWORD);
        bulkImportPage = customDrone.getCurrentPage().render();

        // Fill mandatory parameters / Click Initiate Bulk Import button
        bulkImportPage.createImport(downloadDirectory, null, nodeRef, false, false);

        customDrone.navigateTo(siteUrl);
        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");
        // Navigating to folder
        docLibPage.selectFolder(folderName).render();

        // verify the file are imported and the rule is applied to them
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible. Bulk Import isn't created");

        detailsPage = docLibPage.selectFile(fileName1).render();

        aspectsPage = detailsPage.selectManageAspects().render();

        Assert.assertTrue(aspectsPage.getSelectedAspects().contains(DocumentAspect.CLASSIFIABLE),
                "'Classifiable'' aspect isn't appeared. The rule isn't applied to file");
    }

}