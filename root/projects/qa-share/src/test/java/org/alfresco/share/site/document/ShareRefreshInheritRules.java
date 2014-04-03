package org.alfresco.share.site.document;

import java.io.File;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorCloudImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class ShareRefreshInheritRules extends AbstractUtils
{

    private static final Logger logger = Logger.getLogger(ShareRefreshInheritRules.class);

    private String testUser;
    private String testUser1;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        // create a single user
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        String[] testUserInfo = new String[] { testUser };
        testUser1 = testName + "InvitedUser" + "@" + DOMAIN_FREE;
        String[] testUser1Info = new String[] { testUser1 };

        try
        {
            // create users
        	CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
            logger.info("ShareRefresh users created. " + testUser);

            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1Info);
            logger.info("ShareRefresh users created. " + testUser1);

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }

    }

    @BeforeMethod(groups = { "ShareRefreshInheritRules" })
    public void prepare() throws Exception
    {
        // login as created user
        try
        {
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            logger.info("ShareRefresh user logged in - drone." + testUser);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }

    }

    @AfterMethod(groups = { "ShareRefreshInheritRules" })
    public void quit() throws Exception
    {
        // logout as created user
        try
        {
            ShareUser.logout(drone);
            logger.info("ShareRefresh user logged out - drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
           
    }

    /**
     * Inherit/Don't Inherit Rules toggle
     * 
     * @throws Exception
     */

    @Test(groups = { "ShareRefreshInheritRules" })
    public void ALF_10201() throws Exception
    {
    	// create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        String parentFolderName = getFolderName(testName) + System.currentTimeMillis() + "-Parent";
        String subFolderName = getFolderName(testName) + System.currentTimeMillis() + "-Subfolder";

        DocumentLibraryPage docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // create parent folder in sites document library
        docPage = ShareUserSitePage.createFolder(drone, parentFolderName, "parent folder").render();
        docPage.selectFolder(parentFolderName);

        // create subfolder in parent folder
        ShareUserSitePage.createFolder(drone, subFolderName, "subfolder").render();

        // create the rule for parent folder applicable to subfolders
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        FolderRulesPage folderRulesPage = docPage.getFileDirectoryInfo(parentFolderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(parentFolderName));

        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField("New Copy Rule Name");
        createRulePage.fillDescriptionField("New Copy Rule Description");

        if (isAlfrescoVersionCloud(drone))
        {
        	ActionSelectorCloudImpl actionSelectorCloudImpl = createRulePage.getActionOptionsObj();
            actionSelectorCloudImpl.selectCopy(siteName, "Documents");
        }
        else
        {
            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
            actionSelectorEnterpImpl.selectCopy(siteName, "Documents");
        }

        createRulePage.selectApplyToSubfolderCheckbox();
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(parentFolderName));

        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();

        FolderRulesPage subFolderRulesPage = docPage.getFileDirectoryInfo(subFolderName).selectManageRules().render();
        Assert.assertTrue(subFolderRulesPage.isPageCorrect(subFolderName));

        // verify Inherit Rules button is present
        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());

        // verify the text is "Inherit Rules"
        Assert.assertEquals(subFolderRulesPage.getInheritRulesText(), "Inherit Rules");

        // verify the message
        Assert.assertTrue(subFolderRulesPage.isInheritRulesMessageDisplayed());

        // toggle
        subFolderRulesPage = subFolderRulesPage.toggleInheritRules().render();

        // check it changed
        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());
        Assert.assertEquals(subFolderRulesPage.getInheritRulesText(), "Don't Inherit Rules");

        // toggle again
        subFolderRulesPage = subFolderRulesPage.toggleInheritRules().render();

        // check it changed
        Assert.assertEquals(subFolderRulesPage.getInheritRulesText(), "Inherit Rules");
    }

    /**
     * Inherit Rules - no rules are applied to subfolder
     * 
     * @throws Exception
     */

    @Test(groups = { "ShareRefreshInheritRules" })
    public void ALF_10202() throws Exception
    {
    	// create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        String parentFolderName = getFolderName(testName) + System.currentTimeMillis() + "-Parent";
        String subFolderName = getFolderName(testName) + System.currentTimeMillis() + "-Subfolder";

        DocumentLibraryPage docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // create parent folder in sites document library
        docPage = ShareUserSitePage.createFolder(drone, parentFolderName, "parent folder").render();
        docPage.selectFolder(parentFolderName);

        // create subfolder in parent folder
        ShareUserSitePage.createFolder(drone, subFolderName, "subfolder").render();

        // create the rule for parent folder not applicable to subfolders
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        FolderRulesPage folderRulesPage = docPage.getFileDirectoryInfo(parentFolderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(parentFolderName));

        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField("New Copy Rule Name");
        createRulePage.fillDescriptionField("New Copy Rule Description");

        if (isAlfrescoVersionCloud(drone))
        {
        	ActionSelectorCloudImpl actionSelectorCloudImpl = createRulePage.getActionOptionsObj();
            actionSelectorCloudImpl.selectCopy(siteName, "Documents");
        }
        else
        {
            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
            actionSelectorEnterpImpl.selectCopy(siteName, "Documents");
        }

        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(parentFolderName));

        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();

        FolderRulesPage subFolderRulesPage = docPage.getFileDirectoryInfo(subFolderName).selectManageRules().render();
        Assert.assertTrue(subFolderRulesPage.isPageCorrect(subFolderName));

        // verify Inherit Rules button is present
        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());

        // toggle
        subFolderRulesPage = subFolderRulesPage.toggleInheritRules().render();

        // verify the text is "Don't Inherit Rules"
        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());
        Assert.assertEquals(subFolderRulesPage.getInheritRulesText(), "Don't Inherit Rules");

        // verify the message
        Assert.assertFalse(subFolderRulesPage.isInheritRulesMessageDisplayed());

        // upload any document to subfolder
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();
        docPage.selectFolder(subFolderName).render();
        File sampleFile = SiteUtil.prepareFile();
        ShareUserSitePage.uploadFile(drone, sampleFile);

        // verify the rule is not applied to subfolder - file is not copied to copySite1
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // docPage = siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
        Assert.assertFalse(docPage.isFileVisible(sampleFile.getName()));

        // click on "Don't Inherit Rules"
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();

        subFolderRulesPage = docPage.getFileDirectoryInfo(subFolderName).selectManageRules().render();
        Assert.assertTrue(subFolderRulesPage.isPageCorrect(subFolderName));

        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());

        // toggle
        subFolderRulesPage = subFolderRulesPage.toggleInheritRules().render();
        Assert.assertEquals(subFolderRulesPage.getInheritRulesText(), "Inherit Rules");

        // upload file again
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();
        docPage.selectFolder(subFolderName).render();
        File anotherFile = SiteUtil.prepareFile();
        ShareUserSitePage.uploadFile(drone, anotherFile);

        // verify the rule is not applied to subfolder - file is not copied to copySite1
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertFalse(docPage.isFileVisible(anotherFile.getName()));
    }

    /**
     * Inherit Rules - rule is applied to subfolder
     * 
     * @throws Exception
     */

    @Test(groups = { "ShareRefreshInheritRules" })
    public void ALF_10203() throws Exception
    {
    	// create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        String parentFolderName = getFolderName(testName) + System.currentTimeMillis() + "-Parent";
        String subFolderName = getFolderName(testName) + System.currentTimeMillis() + "-Subfolder";

        DocumentLibraryPage docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // create parent folder in sites document library
        docPage = ShareUserSitePage.createFolder(drone, parentFolderName, "parent folder").render();
        docPage.selectFolder(parentFolderName);

        // create subfolder in parent folder
        ShareUserSitePage.createFolder(drone, subFolderName, "subfolder").render();

        // create the rule for parent folder not applicable to subfolders
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        FolderRulesPage folderRulesPage = docPage.getFileDirectoryInfo(parentFolderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(parentFolderName));

        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField("New Copy Rule Name");
        createRulePage.fillDescriptionField("New Copy Rule Description");

        if (isAlfrescoVersionCloud(drone))
        {
        	ActionSelectorCloudImpl actionSelectorCloudImpl = createRulePage.getActionOptionsObj();
            actionSelectorCloudImpl.selectCopy(siteName, "Documents");
        }
        else
        {
            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
            actionSelectorEnterpImpl.selectCopy(siteName, "Documents");
        }

        createRulePage.selectApplyToSubfolderCheckbox();
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(parentFolderName));

        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();

        FolderRulesPage subFolderRulesPage = docPage.getFileDirectoryInfo(subFolderName).selectManageRules().render();
        Assert.assertTrue(subFolderRulesPage.isPageCorrect(subFolderName));

        // verify the message
        Assert.assertTrue(subFolderRulesPage.isInheritRulesMessageDisplayed());

        // verify Inherit Rules button is present
        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());

        // toggle
        subFolderRulesPage = subFolderRulesPage.toggleInheritRules().render();

        // verify the text is "Don't Inherit Rules"
        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());
        Assert.assertEquals(subFolderRulesPage.getInheritRulesText(), "Don't Inherit Rules");

        // upload any document to subfolder
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();
        // docPage = docPage.renderItem(maxWaitTime, subFolderName); ?????
        docPage.selectFolder(subFolderName).render();
        File sampleFile = SiteUtil.prepareFile();
        ShareUserSitePage.uploadFile(drone, sampleFile);

        // verify the rule is not applied to subfolder
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertFalse(docPage.isFileVisible(sampleFile.getName()));

        // click on "Don't Inherit Rules"
        docPage.selectFolder(parentFolderName).render();

        subFolderRulesPage = docPage.getFileDirectoryInfo(subFolderName).selectManageRules().render();
        Assert.assertTrue(subFolderRulesPage.isPageCorrect(subFolderName));

        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());

        // toggle
        subFolderRulesPage = subFolderRulesPage.toggleInheritRules().render();
        Assert.assertEquals(subFolderRulesPage.getInheritRulesText(), "Inherit Rules");

        // upload file again
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();
        docPage.selectFolder(subFolderName).render();
        File anotherFile = SiteUtil.prepareFile();
        ShareUserSitePage.uploadFile(drone, anotherFile);

        // verify the rule is applied to subfolder
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertTrue(docPage.isFileVisible(anotherFile.getName()));
    }

    /**
     * Inherit Rules - no permissions to parent folder - Contributor
     * 
     * @throws Exception
     */

    @Test(groups = { "ShareRefreshInheritRules" })
    public void ALF_10204() throws Exception
    {
    	// create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        String parentFolderName = getFolderName(testName) + System.currentTimeMillis() + "-Parent";
        String subFolderName = getFolderName(testName) + System.currentTimeMillis() + "-Subfolder";

        DocumentLibraryPage docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // create parent folder in sites document library
        docPage = ShareUserSitePage.createFolder(drone, parentFolderName, "parent folder").render();

        // add user with write permissions to the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser1, siteName, UserRole.CONTRIBUTOR);

        // Inviting user logs out
        ShareUser.logout(drone);

        // Invited User logs in
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // create subfolder in parent folder as invited user
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();
        ShareUserSitePage.createFolder(drone, subFolderName, "subfolder");

        // Invited user logs out
        ShareUser.logout(drone);

        // Site creator logs in
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create the rule for parent folder applicable to subfolders
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        FolderRulesPage folderRulesPage = docPage.getFileDirectoryInfo(parentFolderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(parentFolderName));

        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField("New Copy Rule Name");
        createRulePage.fillDescriptionField("New Copy Rule Description");

        if (isAlfrescoVersionCloud(drone))
        {
        	ActionSelectorCloudImpl actionSelectorCloudImpl = createRulePage.getActionOptionsObj();
            actionSelectorCloudImpl.selectCopy(siteName, "Documents");
        }
        else
        {
        	ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
            actionSelectorEnterpImpl.selectCopy(siteName, "Documents");
        }

        createRulePage.selectApplyToSubfolderCheckbox();
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(parentFolderName));

        // site creator logs out
        ShareUser.logout(drone);

        // invited user logs in
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();

        // open manage rules page for subfolder
        FolderRulesPage subFolderRulesPage = docPage.getFileDirectoryInfo(subFolderName).selectManageRules().render();
        Assert.assertTrue(subFolderRulesPage.isPageCorrect(subFolderName));

        // verify the message
        Assert.assertTrue(subFolderRulesPage.isInheritRulesMessageDisplayed());

        // verify Inherit Rules button is present
        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());

        // toggle
        subFolderRulesPage = subFolderRulesPage.toggleInheritRules().render();

        // verify the text is "Don't Inherit Rules"
        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());
        Assert.assertEquals(subFolderRulesPage.getInheritRulesText(), "Don't Inherit Rules");

        // upload any document to subfolder
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();
        docPage.selectFolder(subFolderName).render();
        File sampleFile = SiteUtil.prepareFile();
        ShareUserSitePage.uploadFile(drone, sampleFile);

        // verify the rule is not applied to subfolder
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertFalse(docPage.isFileVisible(sampleFile.getName()));

        // click on "Don't Inherit Rules"
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();

        subFolderRulesPage = docPage.getFileDirectoryInfo(subFolderName).selectManageRules().render();
        Assert.assertTrue(subFolderRulesPage.isPageCorrect(subFolderName));

        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());

        // toggle
        subFolderRulesPage = subFolderRulesPage.toggleInheritRules().render();
        Assert.assertEquals(subFolderRulesPage.getInheritRulesText(), "Inherit Rules");

        // upload file again
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();
        docPage.selectFolder(subFolderName).render();
        File anotherFile = SiteUtil.prepareFile();
        ShareUserSitePage.uploadFile(drone, anotherFile);

        // verify the rule is applied to subfolder
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertTrue(docPage.isFileVisible(anotherFile.getName()));

    }

    /**
     * Inherit Rules - no permissions to parent folder - Consumer
     * 
     * @throws Exception
     */
    @Test(groups = { "ShareRefreshInheritRules" })
    public void ALF_2546() throws Exception
    {
    	// create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        String parentFolderName = getFolderName(testName) + System.currentTimeMillis() + "-Parent";
        String subFolderName = getFolderName(testName) + System.currentTimeMillis() + "-Subfolder";

        DocumentLibraryPage docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // create parent folder in sites document library
        docPage = ShareUserSitePage.createFolder(drone, parentFolderName, "parent folder");

        // add user with write permissions to the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser1, siteName, UserRole.CONTRIBUTOR);

        // Inviting user logs out
        ShareUser.logout(drone);

        // Invited User logs in
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
            
        // create subfolder in parent folder as invited user
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();
        ShareUserSitePage.createFolder(drone, subFolderName, "subfolder");

        // Invited user logs out
        ShareUser.logout(drone);

        // Site creator logs in
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create the rule for parent folder applicable to subfolders
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        FolderRulesPage folderRulesPage = docPage.getFileDirectoryInfo(parentFolderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(parentFolderName));

        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField("New Copy Rule Name");
        createRulePage.fillDescriptionField("New Copy Rule Description");

        if (isAlfrescoVersionCloud(drone))
        {
        	ActionSelectorCloudImpl actionSelectorCloudImpl = createRulePage.getActionOptionsObj();
            actionSelectorCloudImpl.selectCopy(siteName, "Documents", parentFolderName, subFolderName);
        }
        else
        {
            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
            actionSelectorEnterpImpl.selectCopy(siteName, "Documents", parentFolderName, subFolderName);
        }

        createRulePage.selectApplyToSubfolderCheckbox();
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(parentFolderName));

        // change the role of invited user from contributor to consumer
        ShareUserMembers.assignRoleToSiteMember(drone, testUser1, siteName, UserRole.CONSUMER);

        // site creator logs out
        ShareUser.logout(drone);

        // invited user logs in
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();
        docPage.renderItem(maxWaitTime, subFolderName);

        // open manage rules page for subfolder
        FolderRulesPage subFolderRulesPage = docPage.getFileDirectoryInfo(subFolderName).selectManageRules().render();
        Assert.assertTrue(subFolderRulesPage.isPageCorrect(subFolderName));

        // verify the message
        Assert.assertTrue(subFolderRulesPage.isInheritRulesMessageDisplayed());

        // verify Inherit Rules button is present
        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());

        // toggle
        subFolderRulesPage = subFolderRulesPage.toggleInheritRules().render();

        // verify the text is "Don't Inherit Rules"
        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());
        Assert.assertEquals(subFolderRulesPage.getInheritRulesText(), "Don't Inherit Rules");

        // upload any document to subfolder
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();
        docPage.selectFolder(subFolderName).render();
        File sampleFile = SiteUtil.prepareFile();
        ShareUserSitePage.uploadFile(drone, sampleFile);

        // verify the rule is not applied to subfolder
        Assert.assertFalse(docPage.isFileVisible("Copy of " + sampleFile.getName()));

        // click on "Don't Inherit Rules"
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();

        subFolderRulesPage = docPage.getFileDirectoryInfo(subFolderName).selectManageRules().render();
        Assert.assertTrue(subFolderRulesPage.isPageCorrect(subFolderName));

        Assert.assertTrue(subFolderRulesPage.isInheritRuleToggleAvailable());

        // toggle
        subFolderRulesPage = subFolderRulesPage.toggleInheritRules().render();
        Assert.assertEquals(subFolderRulesPage.getInheritRulesText(), "Inherit Rules");

        // upload file again
        docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docPage.selectFolder(parentFolderName).render();
        docPage.selectFolder(subFolderName).render();
        File anotherFile = SiteUtil.prepareFile();
        ShareUserSitePage.uploadFile(drone, anotherFile);

        // verify the rule is applied to subfolder
        Assert.assertTrue(docPage.isFileVisible("Copy of " + anotherFile.getName()));
    }

}
