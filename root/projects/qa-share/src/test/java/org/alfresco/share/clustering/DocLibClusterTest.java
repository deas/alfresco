package org.alfresco.share.clustering;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.search.SearchKeys;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSearchPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

// import javax.management.openmbean.CompositeDataSupport;
// import javax.management.openmbean.TabularDataSupport;

/**
 * TODO: Add info, Author
 */
public class DocLibClusterTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(DocLibClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    private String testUser;
    // private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName);
        logger.info("Starting Tests: " + testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        /*
         * String shareJmxPort = getAlfrescoServerProperty("Alfresco:Type=Configuration,Category=sysAdmin,id1=default", "share.port").toString();
         * boolean clustering_enabled_jmx = (boolean) getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusteringEnabled");
         * if (clustering_enabled_jmx)
         * {
         * Object clustering_url = getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusterMembers");
         * try
         * {
         * CompositeDataSupport compData = (CompositeDataSupport) ((TabularDataSupport) clustering_url).values().toArray()[0];
         * String clusterIP = compData.values().toArray()[0] + ":" + shareJmxPort;
         * CompositeDataSupport compData2 = (CompositeDataSupport) ((TabularDataSupport) clustering_url).values().toArray()[1];
         * String clusterIP2 = compData2.values().toArray()[0] + ":" + shareJmxPort;
         * node1Url = shareUrl.replace(shareIP, clusterIP);
         * node2Url = shareUrl.replace(shareIP, clusterIP2);
         * }
         * catch (Throwable ex)
         * {
         * throw new SkipException("Skipping as pre-condition step(s) fail");
         * }
         * }
         */
        // TODO: Do not cast, use render to render appropriate page object if util returns HtmlPage
        SystemSummaryPage sysSummaryPage = (SystemSummaryPage) ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);

        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering);

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        // TODO: Suggested usage: Accept node urls via properties file ie. share.harget, hybrid.target with both versions = Share
        List<String> clusterMembers = clusteringPage.getClusterMembers();
        if (clusterMembers.size() >= 2)
        {
            node1Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(0) + ":" + nodePort);
            node2Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(1) + ":" + nodePort);
        }
        else
        {
            throw new PageOperationException("Number of cluster members is less than two");
        }
    }

    // Check on server B the file content created on server A
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3041() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName1 = getFileName(getTestName()) + ".txt";
        String fileContent1 = "testText";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any text document on server A (e.g. "testFile") with text (e.g. "testText")
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileContent1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        ShareUser.logout(drone);

        // Verify that file is presented on server B and check the file content.
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // The document ("testFile") is presented on server B, opened successfully, it contains text ("testText");
        DocumentDetailsPage documentDetailsPage = docLibPage.selectFile(fileName1).render();

        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        contentDetails = inlineEditPage.getDetails();
        Assert.assertEquals(contentDetails.getContent(), fileContent1, "Content for file " + fileName1 + " isn't visible");

        ShareUser.logout(drone);
    }

    // Verify that the folder created on server A is presented on server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3042() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String folderName = "The first folder";
        String folderDescription = String.format("Description of %s", folderName);
        String testTagName = "testTag";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any folder with tag (e.g. "testTag") on server A.
        ShareUserSitePage.createFolder(drone, folderName, folderDescription).render();

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Adding test tag to folder
        ShareUserSitePage.addTagsFromDocLib(drone, folderName, Arrays.asList(testTagName));

        ShareUser.logout(drone);

        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Clicking the tagName link present under folder name
        docLibPage.getFileDirectoryInfo(folderName).clickOnTagNameLink(testTagName).render();

        // Check that the folder is listed
        Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(drone, folderName, true));

        // Clicking the tagName present under Tags menu tree on Document
        // Library page.
        docLibPage = docLibPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(testTagName).render();

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        ShareUser.logout(drone);
    }

    // Verify that the file edited on server A is presented in "I'm Editing" on server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3043() throws Exception
    {

        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName1 = getFileName(getTestName()) + ".txt";
        String fileContent1 = "testText";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Click "Edit offline" action for any content on server A.
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileContent1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // Click Edit Offline button;
        if (docLibPage.getFileDirectoryInfo(fileName1).isEditOfflineLinkPresent())
        {
            docLibPage = docLibPage.getFileDirectoryInfo(fileName1).selectEditOffline().render();

            assertEquals(docLibPage.getFileDirectoryInfo(fileName1).getContentInfo(), "This document is locked by you for offline editing.", "File "
                    + fileName1 + " isn't locked");
        }

        ShareUser.logout(drone);

        // Verify that file is presented on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // Check "I'm editing" on server B
        TreeMenuNavigation treeMenu = docLibPage.getLeftMenus();

        // The file edited in pre-condition is presented in "I'm editing" on server B
        docLibPage = treeMenu.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.IM_EDITING).render();
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1
                + " isn't visible. The file edited in pre-condition isn't presented in \"I'm editing\" on server B.");

        ShareUser.logout(drone);
    }

    // Verify that changes made on server A are presented on server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3044() throws Exception
    {

        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName1 = getFileName(getTestName()) + ".txt";
        String fileNewName1 = getFileName(getTestName()) + "_new_name.txt";
        String fileContent1 = "testText";
        String folderName = "The first folder";
        String folderNewName = getFileName(getTestName()) + "_new_name_folder";
        String folderDescription = String.format("Description of %s", folderName);
        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any content and any folder on server A
        // Create any text document on server A (e.g. "testFile") with text (e.g. "testText")
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileContent1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // Create any folder
        ShareUserSitePage.createFolder(drone, folderName, folderDescription).render();

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        ShareUser.logout(drone);

        // Verify that the file and the folder created in pre-condition is presented on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        ShareUser.logout(drone);

        // Change file name, title and description on server A
        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Changes made successfully
        docLibPage = ShareUser.editProperties(drone, fileName1, fileNewName1, null, null);
        Assert.assertTrue(docLibPage.isFileVisible(fileNewName1), "File " + fileNewName1 + " isn't visible");
        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible");

        docLibPage = ShareUser.editProperties(drone, folderName, folderNewName, null, null);
        Assert.assertTrue(docLibPage.isFileVisible(folderNewName), "File " + folderNewName + " isn't visible");
        Assert.assertFalse(docLibPage.isFileVisible(folderName), "File " + folderName + " is visible");

        ShareUser.logout(drone);

        // Check that all changes made in step 1 are presented on server B correctly
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // All changes are presented correctly
        Assert.assertTrue(docLibPage.isFileVisible(fileNewName1), "File " + fileNewName1 + " isn't visible");
        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible");

        Assert.assertTrue(docLibPage.isFileVisible(folderNewName), "File " + folderNewName + " isn't visible");
        Assert.assertFalse(docLibPage.isFileVisible(folderName), "File " + folderName + " is visible");

        ShareUser.logout(drone);
    }

    // Verify if a rule created on server A can be observed on server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3045() throws Exception
    {

        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String folderName = "The first folder";
        String folderDescription = String.format("Description of %s", folderName);
        String ruleName = "Add Aspect Rule Name";
        String ruleDescription = "Add Aspect Rule Description";
        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any folder on servers A
        ShareUserSitePage.createFolder(drone, folderName, folderDescription).render();

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // On server A create rule "testRule"
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
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

        ShareUser.logout(drone);

        // Verify that "testRule" rule for folder created in pre-condition has appeared on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isRuleIconPresent(5000), "Rule icon for " + folderName + " isn't presented");

        FolderRulesPageWithRules rulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        rulesPage.render(maxWaitTime);

        // "testRule" rule for folder created in pre-condition has appeared on server B
        Assert.assertTrue(rulesPage.isPageCorrect(folderName), "Rule page isn't correct");
        Assert.assertTrue(rulesPage.isRuleNameDisplayed(ruleName), "Rule with name " + ruleName + " isn't displayed");

        ShareUser.logout(drone);
    }

    // Verify if rule deleted on server A is also deleted from server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3046() throws Exception
    {

        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String folderName = "The first folder";
        String folderDescription = String.format("Description of %s", folderName);
        String ruleName = "Add Aspect Rule Name";
        String ruleDescription = "Add Aspect Rule Description";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any folder on servers A
        ShareUserSitePage.createFolder(drone, folderName, folderDescription);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // On server A create rule "testRule"
        FolderRulesPage folderRulesPage;
        folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage();
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

        ShareUser.logout(drone);

        // Verify that "testRule" rule for folder created in pre-condition has appeared on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isRuleIconPresent(5000), "Rule icon for " + folderName + " isn't presented");

        FolderRulesPageWithRules rulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();

        // "testRule" rule for folder created in pre-condition has appeared on server B
        Assert.assertTrue(rulesPage.isRuleNameDisplayed(ruleName), "Rule with name " + ruleName + " isn't displayed");

        ShareUser.logout(drone);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        rulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();

        // Delete rule "testRule" on server A
        rulesPage.deleteRule(ruleName);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // The rule is deleted successfully
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isRuleIconPresent(5000), "Rule icon for " + folderName + " is presented");

        ShareUser.logout(drone);

        // Verify that "testRule" rule is deleted from server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // "testRule" rule is deleted from server B
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isRuleIconPresent(5000), "Rule icon for " + folderName + " is presented");

        folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();

        // "testRule" rule is deleted from server B
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        ShareUser.logout(drone);

    }

    // Check if deleted from server A file and folder will be deleted from server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3047() throws Exception
    {

        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName1 = getFileName(getTestName()) + ".txt";
        String fileContent1 = "testText";
        String folderName = "The first folder";
        String folderDescription = String.format("Description of %s", folderName);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any content and any folder on server A
        // Create any text document on server A (e.g. "testFile") with text (e.g. "testText")
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileContent1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // Create any folder
        ShareUserSitePage.createFolder(drone, folderName, folderDescription).render();

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        ShareUser.logout(drone);

        // Verify that the file and the folder created in pre-condition is presented on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // The file and the folder are presented
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        ShareUser.logout(drone);

        // Delete this file and folder from server A
        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        docLibPage = docLibPage.getNavigation().selectAll().render();

        // Click Delete from Selected items drop down
        ConfirmDeletePage confirmDeletePage = docLibPage.getNavigation().selectDelete();

        // Select the folders again and repeat step1. Click Delete
        confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render();

        // The file and the folder are deleted successfully from server A
        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't deleted from server A");
        Assert.assertFalse(docLibPage.isFileVisible(folderName), "File " + folderName + " isn't deleted from server A");

        ShareUser.logout(drone);

        // Verify that the file and the folder are deleted from server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // The file and the folder are deleted successfully from server B
        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't deleted from server B");
        Assert.assertFalse(docLibPage.isFileVisible(folderName), "File " + folderName + " isn't deleted from server B");

        ShareUser.logout(drone);
    }

    // Verify possibility to upload different files on servers A and B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3048() throws Exception
    {

        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName1 = "First_" + getFileName(testName) + ".txt";
        String fileName2 = "Second_" + getFileName(testName) + ".txt";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Upload any files on server A
        String[] fileInfo = { fileName1, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Files are uploaded successfully on server A
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        ShareUser.logout(drone);

        // Upload any files on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        String[] fileInfo2 = { fileName2, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo2);

        // Files are uploaded successfully on server B
        Assert.assertTrue(docLibPage.isFileVisible(fileName2), "File " + fileName2 + " isn't visible");

        // Verify that files are presented on each server
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        ShareUser.logout(drone);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify that files are presented on each server
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(fileName2), "File " + fileName2 + " isn't visible");

    }

    // Check if the created on server A file can be found with simple search on server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3049() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName1 = "testFile" + System.currentTimeMillis() + ".txt";
        String fileContent1 = "testText" + System.currentTimeMillis();

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any content (e.g. "testFile") on server A
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileContent1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        ShareUser.logout(drone);

        // Verify that file is presented on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // "testFile" is presented in Document Library on server B
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // Try to search "testFile" on server B using simple search
        Assert.assertTrue(CollectionUtils.hasElements(ShareUserSearchPage.basicSearch(drone, fileName1, false)), "The file " + fileName1
                + " isn't found successfully");

        Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, fileName1), "Not Found " + fileName1);

        ShareUser.logout(drone);
    }

    // Check if the created on server A file can be found with advanced search on server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3050() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName1 = "testFile" + System.currentTimeMillis() + ".txt";
        String fileContent1 = "testText" + System.currentTimeMillis();

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any content (e.g. "testFile") on server A
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileContent1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        ShareUser.logout(drone);

        // Verify that file is presented on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // "testFile" is presented in Document Library on server B
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // Try to search "testFile" on server B using advanced search

        // Searching for valid Name string
        keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), fileName1);
        ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);
        Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, fileName1), "Not Found " + fileName1);

        ShareUser.logout(drone);
    }

    // Verify if add comment to the file and to the folder on server A is also presented on server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3051() throws Exception
    {

        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName1 = getFileName(getTestName()) + ".txt";
        String fileContent1 = "testText";
        String folderName = "The first folder";
        String folderDescription = String.format("Description of %s", folderName);
        String fileComment = getRandomString(10);
        String folderComment = getRandomString(10);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any content and any folder on server A
        // Create any text document on server A
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileContent1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // Add comments to file
        DocumentDetailsPage detailsPage = docLibPage.selectFile(fileName1);
        AddCommentForm addCommentForm = detailsPage.clickAddCommentButton();

        // Type any text
        TinyMceEditor tinyMceEditor = addCommentForm.getTinyMceEditor();
        tinyMceEditor.setText(fileComment);

        addCommentForm.clickAddCommentButton();
        assertTrue(detailsPage.isCommentCorrect(fileComment), "Comment didn't created for content");

        docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any folder
        ShareUserSitePage.createFolder(drone, folderName, folderDescription).render();

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Add comments to file and to folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);
        folderDetailsPage.addComment(folderComment);
        assertTrue(folderDetailsPage.isCommentCorrect(folderComment), "Comment didn't added for folder");

        ShareUser.logout(drone);

        // Verify that comments are presented on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // The file and the folder are presented
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Verify that comments are presented on server B
        detailsPage = docLibPage.selectFile(fileName1);
        assertTrue(detailsPage.isCommentCorrect(fileComment), "The comment for content wasn't displayed successfully");

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);

        assertTrue(folderDetailsPage.isCommentCorrect(folderComment), "The comment for folder wasn't displayed successfully");

        ShareUser.logout(drone);

    }

    // Verify if edit content on server A is also changed on server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3052() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName1 = getFileName(getTestName()) + ".txt";
        String fileContent1 = "testText";
        String fileNewName = getFileName(getTestName()) + "_new_name.txt";
        String fileNewContent = getFileName(getTestName()) + "_new_content";
        String fileNewDescription = getFileName(getTestName()) + "_new_description";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any content on server A
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileContent1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Edit content created in pre-condition on server A
        docLibPage.selectFile(fileName1).render();
        ShareUser.editTextDocument(drone, fileNewName, fileNewDescription, fileNewContent);

        docLibPage = ShareUser.openDocumentLibrary(drone);

        Assert.assertTrue(docLibPage.isFileVisible(fileNewName), "File " + fileNewName + " isn't visible");
        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible");

        ShareUser.logout(drone);

        // Verify that all changes are presented on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(fileNewName), "File " + fileNewName + " isn't visible");
        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible");

        // All changes are presented correctly on server B
        DocumentDetailsPage documentDetailsPage = docLibPage.selectFile(fileNewName).render();

        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        contentDetails = inlineEditPage.getDetails();
        Assert.assertEquals(contentDetails.getContent(), fileNewContent, "Content for file " + fileNewName + " isn't visible on server B");
        Assert.assertEquals(contentDetails.getDescription(), fileNewDescription, "Description for file " + fileNewName + " isn't visible on server B");
        Assert.assertEquals(contentDetails.getName(), fileNewName, "Name for file " + fileNewName + " isn't visible on server B");

        ShareUser.logout(drone);
    }

    // Verify if edit comment to the file or to the folder on server A is also presented on server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3053() throws Exception
    {

        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName1 = getFileName(getTestName()) + ".txt";
        String fileContent1 = "testText";
        String folderName = "The first folder";
        String folderDescription = String.format("Description of %s", folderName);
        String fileComment = getRandomString(10);
        String newFileComment = "new_" + getRandomString(10);
        String folderComment = getRandomString(10);
        String newFolderComment = "new_" + getRandomString(10);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any content and any folder on server A
        // Create any text document on server A
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileContent1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // Add comments to file
        DocumentDetailsPage detailsPage = docLibPage.selectFile(fileName1);
        AddCommentForm addCommentForm = detailsPage.clickAddCommentButton();

        // Type any text
        TinyMceEditor tinyMceEditor = addCommentForm.getTinyMceEditor();
        tinyMceEditor.setText(fileComment);

        addCommentForm.clickAddCommentButton();
        assertTrue(detailsPage.isCommentCorrect(fileComment), "Comment didn't created for content");

        docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any folder
        ShareUserSitePage.createFolder(drone, folderName, folderDescription).render();

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Add comments to file and to folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);
        folderDetailsPage.addComment(folderComment);
        assertTrue(folderDetailsPage.isCommentCorrect(folderComment), "Comment didn't added for folder");

        ShareUser.logout(drone);

        // Verify that comments are presented on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // The file and the folder are presented
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Verify that comments are presented on server B
        detailsPage = docLibPage.selectFile(fileName1);
        assertTrue(detailsPage.isCommentCorrect(fileComment), "The comment for content wasn't displayed successfully");

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);

        assertTrue(folderDetailsPage.isCommentCorrect(folderComment), "The comment for folder wasn't displayed successfully");

        ShareUser.logout(drone);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // The file and the folder are presented
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);

        assertTrue(folderDetailsPage.isCommentCorrect(folderComment), "Comment didn't add.");

        // Edit the comment on server A
        folderDetailsPage.editComment(folderComment, newFolderComment);
        folderDetailsPage.saveEditComments();

        assertTrue(folderDetailsPage.isCommentCorrect(newFolderComment), "Changed comment didn't correct.");
        assertFalse(folderDetailsPage.isCommentCorrect(folderComment), "Changed comment didn't correct.");

        docLibPage = ShareUser.openDocumentLibrary(drone);

        // The file and the folder are presented
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName1);

        assertTrue(documentDetailsPage.isCommentCorrect(fileComment), "Comment didn't add.");

        // Edit the comment on server A
        folderDetailsPage.editComment(fileComment, newFileComment);
        folderDetailsPage.saveEditComments();

        assertTrue(folderDetailsPage.isCommentCorrect(newFileComment), "Changed comment didn't correct for content.");
        assertFalse(folderDetailsPage.isCommentCorrect(fileComment), "Changed comment didn't correct for folder.");

        ShareUser.logout(drone);

        // Verify that comments are presented on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // The file and the folder are presented
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Verify that comments are presented on server B
        detailsPage = docLibPage.selectFile(fileName1);
        assertTrue(detailsPage.isCommentCorrect(newFileComment), "The comment for content wasn't displayed correctly");

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);

        assertTrue(folderDetailsPage.isCommentCorrect(newFolderComment), "The comment for folder wasn't displayed correctly");

        ShareUser.logout(drone);
    }

    // Check if deleted a comment to the file or to the folder from server A is also deleted from server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3054() throws Exception
    {

        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName1 = getFileName(getTestName()) + ".txt";
        String fileContent1 = "testText";
        String folderName = "The first folder";
        String folderDescription = String.format("Description of %s", folderName);
        String fileComment = getRandomString(10);
        String folderComment = getRandomString(10);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any content and any folder on server A
        // Create any text document on server A
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(fileContent1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // Add comments to file
        DocumentDetailsPage detailsPage = docLibPage.selectFile(fileName1);
        AddCommentForm addCommentForm = detailsPage.clickAddCommentButton();

        // Type any text
        TinyMceEditor tinyMceEditor = addCommentForm.getTinyMceEditor();
        tinyMceEditor.setText(fileComment);

        addCommentForm.clickAddCommentButton();
        assertTrue(detailsPage.isCommentCorrect(fileComment), "Comment didn't created for content");

        docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any folder
        ShareUserSitePage.createFolder(drone, folderName, folderDescription).render();

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Add comments to folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);
        folderDetailsPage.addComment(folderComment);
        assertTrue(folderDetailsPage.isCommentCorrect(folderComment), "Comment didn't added for folder");

        ShareUser.logout(drone);

        // Verify that comments are presented on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // The file and the folder are presented
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Verify that comments are presented on server B
        detailsPage = docLibPage.selectFile(fileName1);
        assertTrue(detailsPage.isCommentCorrect(fileComment), "The comment for content wasn't displayed successfully");

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);

        assertTrue(folderDetailsPage.isCommentCorrect(folderComment), "The comment for folder wasn't displayed successfully");

        ShareUser.logout(drone);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // The file and the folder are presented
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);

        assertTrue(folderDetailsPage.isCommentCorrect(folderComment), "Comment for folder isn't correct.");

        // Delete the comment on server A
        detailsPage.removeComment(folderComment).render();

        // Go to document library and verify comment counter
        docLibPage = ShareUser.openDocumentLibrary(drone);
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(folderName).getCommentsCount(), 0);

        // The file and the folder are presented
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName1);

        assertTrue(documentDetailsPage.isCommentCorrect(fileComment), "Comment for content isn't correct.");

        // Delete the comment on server A
        detailsPage.removeComment(fileComment).render();

        docLibPage = ShareUser.openDocumentLibrary(drone);
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(fileName1).getCommentsCount(), 0);

        ShareUser.logout(drone);

        // Verify that the comment is deleted from server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // The file and the folder are presented
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // The comment is deleted successfully from server B
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(fileName1).getCommentsCount(), 0);
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(folderName).getCommentsCount(), 0);

        ShareUser.logout(drone);
    }

    // Verify if a rule edited on server A is also edited on server B
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3055() throws Exception
    {

        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String folderName = "The first folder";
        String folderDescription = String.format("Description of %s", folderName);
        String ruleName = "Add Aspect Rule Name";
        String newRuleName = "new Rule Name";
        String ruleDescription = "Add Aspect Rule Description";
        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create any folder on servers A
        ShareUserSitePage.createFolder(drone, folderName, folderDescription).render();

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // On server A create rule "testRule"
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
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

        ShareUser.logout(drone);

        // Verify that "testRule" rule for folder created in pre-condition has appeared on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isRuleIconPresent(5000), "Rule icon for " + folderName + " isn't presented");

        FolderRulesPageWithRules rulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();

        // "testRule" rule for folder created in pre-condition has appeared on server B
        Assert.assertTrue(rulesPage.isPageCorrect(folderName), "Rule page isn't correct");
        Assert.assertTrue(rulesPage.isRuleNameDisplayed(ruleName), "Rule with name " + ruleName + " isn't displayed");

        ShareUser.logout(drone);

        // Edit rule "testRule" on server A
        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        rulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        // TODO: Remove redundant render, since the page is rendered above already
        rulesPage.render(maxWaitTime);

        createRulePage = rulesPage.clickEditButton().render();

        createRulePage.fillNameField(newRuleName);

        // Click "Save" button
        rulesPage = createRulePage.clickSave().render();

        // The rule is edited successfully
        Assert.assertTrue(rulesPage.isPageCorrect(folderName), "Rule page isn't correct");
        Assert.assertTrue(rulesPage.isRuleNameDisplayed(newRuleName), "Rule with name " + newRuleName + " isn't displayed");

        ShareUser.logout(drone);

        // Verify that "testRule" rule is edited from server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        rulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        // TODO: Remove redundant render, since the page is rendered above already
        rulesPage.render(maxWaitTime);

        // "testRule" rule is edited from server B
        Assert.assertTrue(rulesPage.isPageCorrect(folderName), "Rule page isn't correct");
        Assert.assertFalse(rulesPage.isRuleNameDisplayed(ruleName), "Rule with name " + ruleName + " is displayed");
        Assert.assertTrue(rulesPage.isRuleNameDisplayed(newRuleName), "Rule with name " + newRuleName + " isn't displayed");
    }
}
