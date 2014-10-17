package org.alfresco.share.peoplefinder;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Class includes: One Tests from TestLink in Area:
 * Alfresco Share / People Finder / Search by /
 * 
 * @author Cristina Axinte
 */
@Listeners(FailedTestListener.class)
public class SearchByTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SearchByTests.class);

    protected String testUser;

    protected String siteName = "";

    private String userName;
    private String firstName;
    private String lastName;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;

        userName = "SearchUser SearchName1";
        firstName = "SearchFirstName1";
        lastName = "SearchLastName1";

        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "DataPrepPeopleFinder" })
    public void dataPrep_AONE_3001() throws Exception
    {
        String testName = getTestName();
        String[] testUserInfo = new String[] {userName, firstName, lastName};
        try
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.logout(drone);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }

        logger.info("Start Tests in: " + testName);
    }

    /**
     * AONE-3001:Search by double-part user name
     */
    @Test(groups = { "PeopleFinder", "EnterpriseOnly" })
    public void AONE_3001() throws Exception
    {
        // Login with admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Step prec. People Finder page is opened;

        DashBoardPage dashBoard = ShareUser.openUserDashboard(drone).render(maxWaitTime);
        PeopleFinderPage peopleFinderPage = dashBoard.getNav().selectPeople().render();
        // Step 1. Enter first part of username (e.g. User) into Search field and click "Search" button;
        PeopleFinderPage resultPage = peopleFinderPage.searchFor("SearchUser").render();
        // Created user was found;
        List<ShareLink> searchLinks = resultPage.getResults();
        if (!searchLinks.isEmpty())
        {
            Assert.assertTrue(isUserLinkFound(firstName + " " + lastName, searchLinks), userName + " is not in results list.");
        }
        else
        {
            Assert.fail("Search results list for user " + userName + "  is empty");
        }

        // Step 2. Enter last part of username (e.g. Name) into Search field and click "Search" button;
        resultPage = peopleFinderPage.clearAndSearchFor("SearchName1").render();
        // Created user was found;
        searchLinks = resultPage.getResults();
        if (!searchLinks.isEmpty())
        {
            Assert.assertTrue(isUserLinkFound(firstName + " " + lastName, searchLinks), userName + " is not in results list.");
        }
        else
        {
            Assert.fail("Search results list for user " + userName + "  is empty");
        }

        // Step 3. Enter whole username (e.g. User Name) into Search field and click "Search" button;
        resultPage = peopleFinderPage.clearAndSearchFor(userName).render();
        // Created user was found;
        searchLinks = resultPage.getResults();
        if (!searchLinks.isEmpty())
        {
            Assert.assertTrue(isUserLinkFound(firstName + " " + lastName, searchLinks), userName + " is not in results list.");
        }
        else
        {
            Assert.fail("Search results list for user " + userName + "  is empty: ACE-2279");
        }

    }

    private boolean isUserLinkFound(String testUser, List<ShareLink> searchLinks)
    {
        for (ShareLink shareLink : searchLinks)
        {
            if (shareLink.getDescription().contains(testUser))
            {
                return true;
            }
        }
        return false;
    }

}
