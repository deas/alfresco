package org.alfresco.share.cloudconsole;

import org.alfresco.po.share.console.CloudConsolePage;
import org.alfresco.po.share.console.CloudConsoleSearchResultPage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

/**
 * Class to include: Tests for Cloud Console
 * 
 * @author Dmitry Yukhnovets
 */

// TODO: Move CloudConsole in cloud package: This contains cloud specific tests (as ComAlfOne)
@Listeners(FailedTestListener.class)
public class CloudConsoleTest extends AbstractTests
{

    private static Log logger = LogFactory.getLog(CloudConsoleTest.class);
    private CloudConsolePage cloudConsolePage;
    protected String testUser;
    
    //TODO: Consider moving this to test properties file, specially if cloudConsole credentials are diff for diff env e.g. Staging
    private final String USERNAME = "automationteam";
    private final String PASSWORD = "wR5qiqNY";

    @Override
    // TODO: Group=MyAlfresco?
    @BeforeClass(alwaysRun = true, groups = { "Cloud2" })
    public void setup() throws Exception
    {
        super.setup();
        cloudConsolePage = new CloudConsolePage(drone);
    }

    @BeforeMethod(groups = { "Cloud2" })
    public void beforeTest() throws Exception
    {
        String cloudConsoleUrl = ShareUser.getCloudConsoleURL(drone);
        cloudConsolePage.openCloudConsole(cloudConsoleUrl).render();
        if (cloudConsolePage.isLoggedToCloudConsole())
        {
            cloudConsolePage = cloudConsolePage.logOutFromCloudConsole().render();
        }
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "DataPrepConsole" })
    public void dataPrep_ALF_2230() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

    }

    /**
     * Check the search options in testmy environment
     * a) enter an account id, tenant or e-mail address to find an account
     * entered my e-mail address in the search option
     * enter an e-mail address, after that it should show the account details
     */

    @Test(groups = { "Cloud2" })
    public void ALF_2230()
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        
        // Login to Cloud Console
        CloudConsolePage consolePage = cloudConsolePage.loginAs(USERNAME, PASSWORD).render();
        
        // Search
        CloudConsoleSearchResultPage resultPage = consolePage.executeSearch(testUser).render();
        
        Assert.assertTrue(resultPage.isVisibleResults());
    }

    /**
     * add user e-mail addresses one by one in the option column box
     * click bulk upload option
     * Users should be invited successfully
     */
    @Test(groups = { "Cloud2" })
    public void ALF_2246()
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + 1);
        String user2 = getUserNameFreeDomain(testName + 2);

        String[] usersForInvitation = { user1, user2 };
        
        // TODO: Avoid webdrone / share-po code (render) in the tests, to keep tests simpler and easy maintainable
        // TODO: Avoid page chains
        Map<String, Boolean> results = cloudConsolePage.loginAs(USERNAME, PASSWORD).render().openDashboardPage().render().openInviteUsersTab().render()
                .executeCorrectBulkImport(usersForInvitation);

        Assert.assertTrue(results.containsKey(user1) && results.get(user1));
        Assert.assertTrue(results.containsKey(user2) && results.get(user2));
    }

    /**
     * upload any allowed format with the username e-mail addresses
     * click bulk upload option
     * all the users in the file should be uploaded successfully
     */
    @Test(groups = { "Cloud2" })
    public void ALF_2247()
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + 1);
        String user2 = getUserNameFreeDomain(testName + 2);
        String usersForInvitation = user1 + "\r\n" + user2;
        File fileForBulkImport = SiteUtil.newFile(DATA_FOLDER + testName + ".txt", usersForInvitation).getAbsoluteFile();
        Map<String, Boolean> results = cloudConsolePage.loginAs(USERNAME, PASSWORD).render().openDashboardPage().render().openInviteUsersTab().render()
                .executeCorrectBulkImport(fileForBulkImport);

        Assert.assertTrue(results.containsKey(user1) && results.get(user1));
        Assert.assertTrue(results.containsKey(user2) && results.get(user2));
    }

}
