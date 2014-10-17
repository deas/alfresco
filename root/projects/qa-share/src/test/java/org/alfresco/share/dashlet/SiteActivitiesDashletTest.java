package org.alfresco.share.dashlet;

import static org.alfresco.po.share.dashlet.SiteActivitiesHistoryFilter.FOURTEEN_DAYS;
import static org.alfresco.po.share.dashlet.SiteActivitiesHistoryFilter.SEVEN_DAYS;
import static org.alfresco.po.share.dashlet.SiteActivitiesHistoryFilter.TODAY;
import static org.alfresco.po.share.dashlet.SiteActivitiesHistoryFilter.TWENTY_EIGHT_DAYS;
import static org.alfresco.po.share.dashlet.SiteActivitiesTypeFilter.ALL_ITEMS;
import static org.alfresco.po.share.dashlet.SiteActivitiesTypeFilter.COMMENTS;
import static org.alfresco.po.share.dashlet.SiteActivitiesTypeFilter.CONTENT;
import static org.alfresco.po.share.dashlet.SiteActivitiesTypeFilter.MEMBERSHIPS;
import static org.alfresco.po.share.dashlet.SiteActivitiesTypeFilter.STATUS_UPDATES;
import static org.alfresco.po.share.dashlet.SiteActivitiesUserFilter.EVERYONES_ACTIVITIES;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.ActivityShareLink;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet.LinkType;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.AddCommentForm;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CalendarUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Bogdan.Bocancea
 */

@Listeners(FailedTestListener.class)
public class SiteActivitiesDashletTest extends AbstractUtils
{

    String testUser;
    protected String siteName = "";
    DocumentLibraryPage documentLibraryPage;
    SiteDashboardPage siteDashBoard;
    private static final String SITE_ACTIVITY = "site-activities";

    private String newDate;

    // private String cmdChangeNewDateUnix = "date +%Y%m%d -s ";
    private String cmdChangeNewDateUnix = "date +%d-%m-%Y -s ";
    private String cmdChangeDefaultDateUnix = "ntpdate ntp.ubuntu.com";

    private String cmdChangeCurrentDate1 = "cmd /C w32tm /config /update";
    private String cmdChangeCurrentDate2 = "cmd /C w32tm /resync";
    private String cmdChangeNewDate = "cmd /C date ";

    private String cmdDefaultDateWin1;
    private String cmdDefaultDateWin2;
    private String cmdDefaultDateUnix;
    private String cmdNewDate;

    private static Log logger = LogFactory.getLog(SiteSearchDashletTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName()+2;
        testUser = testName + "@" + DOMAIN_FREE;

        logger.info("Start Tests in: " + testName);

        if (System.getProperty("os.name").contains("Windows"))
        {
            cmdDefaultDateWin1 = cmdChangeCurrentDate1;
            cmdDefaultDateWin2 = cmdChangeCurrentDate2;
            cmdNewDate = cmdChangeNewDate;
        }
        else
        {
            cmdDefaultDateUnix = cmdChangeDefaultDateUnix;
            cmdNewDate = cmdChangeNewDateUnix;
        }

    }

    @AfterClass(alwaysRun = true)
    public void teardown() throws Exception
    {
        if (System.getProperty("os.name").contains("Windows"))
        {
            Runtime.getRuntime().exec(cmdDefaultDateWin1);
            Runtime.getRuntime().exec(cmdDefaultDateWin2);
        }
        else
        {
            Runtime.getRuntime().exec(cmdDefaultDateUnix);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void teardownMethod() throws Exception
    {
        if (System.getProperty("os.name").contains("Windows"))
        {
            Runtime.getRuntime().exec(cmdDefaultDateWin1);
            Runtime.getRuntime().exec(cmdDefaultDateWin2);
        }
        else
        {
            Runtime.getRuntime().exec(cmdDefaultDateUnix);
        }
    }

    @Test(groups = { "DataPrepSiteActivitiesDashlet", "CloudOnly" })
    public void dataPrep_AONE_12114() throws Exception
    {

        String testName = getTestName();
        String fileName = getFileName(testName) + ".txt";
        String siteName = getSiteName(testName);

        // User
        String testUser = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        documentLibraryPage = drone.getCurrentPage().render();

        // select the file
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);

        // like document
        detailsPage.selectLike();

    }

    @Test(groups = { "CloudOnly" })
    public void AONE_12114() throws Exception
    {

        testName = getTestName();
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String testUser = getUserNameFreeDomain(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render();

        // get dashlet
        SiteActivitiesDashlet dashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SITE_ACTIVITIES).render().render();
        siteDashBoard = dashlet.selectUserFilter(EVERYONES_ACTIVITIES).render();
        siteDashBoard = dashlet.selectTypeFilter(ALL_ITEMS).render();
        siteDashBoard = dashlet.selectHistoryFilter(TODAY).render();

        // 1. Select today value from drop-down menu
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        assertEquals(dashlet.getCurrentHistoryFilter(), TODAY);
        // 1. Created activities are displayed
        List<ShareLink> documents = dashlet.getSiteActivities(LinkType.Document);
        String documentsToString = documents.toString();
        Assert.assertNotNull(documents);
        Assert.assertTrue(documentsToString.contains(fileName));

        // 2. Select in the last 7 days value from drop-down menu
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        siteDashBoard = dashlet.selectHistoryFilter(SEVEN_DAYS).render();
        // 2. Created activities are displayed;
        documents = dashlet.getSiteActivities(LinkType.Document);
        documentsToString = documents.toString();
        Assert.assertNotNull(documents);
        Assert.assertTrue(documentsToString.contains(fileName));

        // 3. Change time to 8 days forward on the server and verify filter
        newDate = addDaysToCurrentDate(8);
        Runtime.getRuntime().exec(cmdNewDate + newDate);

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render();
        dashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SITE_ACTIVITIES).render().render();
        siteDashBoard = dashlet.selectHistoryFilter(SEVEN_DAYS).render();
        // 3. Created activities aren't displayed;
        documents = dashlet.getSiteActivities(LinkType.Document);
        documentsToString = documents.toString();
        Assert.assertTrue(documents.isEmpty());
        Assert.assertFalse(documentsToString.contains(fileName));

        // 4. Select in the last 14 days value from drop-down menu
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        siteDashBoard = dashlet.selectHistoryFilter(FOURTEEN_DAYS).render();
        // 4. Created activities are displayed;
        documents = dashlet.getSiteActivities(LinkType.Document);
        documentsToString = documents.toString();
        Assert.assertNotNull(documents);
        Assert.assertTrue(documentsToString.contains(fileName));

        // 5. Change time to 15 days forward on the server and verify filter
        newDate = addDaysToCurrentDate(15);
        Runtime.getRuntime().exec(cmdNewDate + newDate);

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render();
        dashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SITE_ACTIVITIES).render().render();
        siteDashBoard = dashlet.selectHistoryFilter(FOURTEEN_DAYS).render();
        // 5. Created activities aren't displayed;
        documents = dashlet.getSiteActivities(LinkType.Document);
        documentsToString = documents.toString();
        Assert.assertTrue(documents.isEmpty());
        Assert.assertFalse(documentsToString.contains(fileName));

        // 6. Select in the last 28 days value from drop-down menu
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        siteDashBoard = dashlet.selectHistoryFilter(TWENTY_EIGHT_DAYS).render();
        // 6. Created activities are displayed;
        documents = dashlet.getSiteActivities(LinkType.Document);
        documentsToString = documents.toString();
        Assert.assertNotNull(documents);
        Assert.assertTrue(documentsToString.contains(fileName));

        // 7. Change time to 29 days forward on the server and verify filter
        newDate = addDaysToCurrentDate(29);
        Runtime.getRuntime().exec(cmdNewDate + newDate);

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render();
        dashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SITE_ACTIVITIES).render().render();
        siteDashBoard = dashlet.selectHistoryFilter(TWENTY_EIGHT_DAYS).render();
        // 7. Created activities aren't displayed;
        documents = dashlet.getSiteActivities(LinkType.Document);
        documentsToString = documents.toString();
        Assert.assertTrue(documents.isEmpty());
        Assert.assertFalse(documentsToString.contains(fileName));

    }

    @Test(groups = { "DataPrepSiteActivitiesDashlet", "CloudOnly" })
    public void dataPrep_AONE_12115() throws Exception
    {
        String text = "Comment Text";
        String testName = getTestName();
        String fileName = getFileName(testName) + ".txt";
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";

        // User1
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User2
        String[] testUserInfo1 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        documentLibraryPage = drone.getCurrentPage().render();

        // select the file
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);

        // like document
        detailsPage.selectLike();

        // click add comment
        AddCommentForm addCommentForm = detailsPage.clickAddCommentButton();
        // Type any text
        TinyMceEditor tinyMceEditor = addCommentForm.getTinyMceEditor();
        tinyMceEditor.setText(text);
        addCommentForm.clickAddCommentButton();
        assertTrue(detailsPage.isCommentCorrect(text), "Comment didn't create");

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.CONSUMER);

    }

    @Test(groups = { "CloudOnly" })
    public void AONE_12115()
    {
        testName = getTestName();
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String testUser = getUserNameFreeDomain(testName) + "1";
        String testUser2 = getUserNameFreeDomain(testName) + "2";

        // login with user1
        ShareUser.login(drone, testUser);
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);

        // get dashlet
        SiteActivitiesDashlet dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();

        siteDashBoard = dashlet.selectUserFilter(EVERYONES_ACTIVITIES).render();

        siteDashBoard = dashlet.selectHistoryFilter(TODAY).render();

        // 1. Select All items value from drop-down menu
        siteDashBoard = dashlet.selectTypeFilter(ALL_ITEMS).render();
        // 1. All activities are displayed
        List<ShareLink> documents = dashlet.getSiteActivities(LinkType.Document);
        String documentsToString = documents.toString();
        Assert.assertNotNull(documents);
        Assert.assertTrue(documentsToString.contains(fileName));

        List<ShareLink> users = dashlet.getSiteActivities(LinkType.User);
        String userToString = users.toString();
        Assert.assertNotNull(users);
        Assert.assertTrue(userToString.contains(testUser2));

        List<String> activities = dashlet.getSiteActivityDescriptions();
        String activitiesToString = activities.toString();
        String commented = testUser + " " + DEFAULT_LASTNAME + " commented" + " on " + fileName;
        String added = testUser + " " + DEFAULT_LASTNAME + " added document " + fileName;
        String like = testUser + " " + DEFAULT_LASTNAME + " liked document " + fileName;
        String joined = testUser2 + " " + DEFAULT_LASTNAME + " joined site " + siteName;
        Assert.assertTrue(activitiesToString.contains(commented));
        Assert.assertTrue(activitiesToString.contains(added));
        Assert.assertTrue(activitiesToString.contains(like));
        Assert.assertTrue(activitiesToString.contains(joined));

        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        // 2. Select Status updates value from drop-down menu
        siteDashBoard = dashlet.selectTypeFilter(STATUS_UPDATES).render();
        // 2. No activities are displayed
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        activities = dashlet.getSiteActivityDescriptions();
        Assert.assertTrue(activities.isEmpty());

        // 3. Select Comments value from drop-down menu
        siteDashBoard = dashlet.selectTypeFilter(COMMENTS).render();
        // 3. Only comments activities are displayed
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        activities = dashlet.getSiteActivityDescriptions();
        activitiesToString = activities.toString();
        commented = testUser + " " + DEFAULT_LASTNAME + " commented" + " on " + fileName;
        Assert.assertTrue(activitiesToString.contains(commented));
        Assert.assertFalse(activitiesToString.contains(added));
        Assert.assertFalse(activitiesToString.contains(joined));

        // 4. Select Content value from drop-down menu
        siteDashBoard = dashlet.selectTypeFilter(CONTENT).render();
        // 4. Only content items activities are displayed
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        activities = dashlet.getSiteActivityDescriptions();
        activitiesToString = activities.toString();
        added = testUser + " " + DEFAULT_LASTNAME + " added document " + fileName;
        Assert.assertTrue(activitiesToString.contains(added));
        Assert.assertFalse(activitiesToString.contains(commented));
        Assert.assertFalse(activitiesToString.contains(joined));

        // 5. Select Memberships value from drop-down menu
        siteDashBoard = dashlet.selectTypeFilter(MEMBERSHIPS).render();
        // 5. Only membership activities are displayed
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        activities = dashlet.getSiteActivityDescriptions();
        activitiesToString = activities.toString();
        joined = testUser2 + " " + DEFAULT_LASTNAME + " joined site " + siteName;
        Assert.assertTrue(activitiesToString.contains(joined));
        Assert.assertFalse(activitiesToString.contains(commented));
        Assert.assertFalse(activitiesToString.contains(added));

    }

    @Test(groups = { "DataPrepSiteActivitiesDashlet", "CloudOnly" })
    public void dataPrep_AONE_3315() throws Exception
    {

        String testName = getTestName();
        String fileName = getFileName(testName) + ".txt";
        String siteName = getSiteName(testName);

        // User
        String testUser = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        documentLibraryPage = drone.getCurrentPage().render();

        // select the file
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);

        // like document
        detailsPage.selectLike();

    }

    /**
     * AONE-3315:Site Activities dashlet. Date filter
     */
    @Test(groups = { "CloudOnly" })
    public void AONE_3315() throws Exception
    {

        testName = getTestName();
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String testUser = getUserNameFreeDomain(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render();

        // get dashlet
        SiteActivitiesDashlet dashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SITE_ACTIVITIES).render().render();
        siteDashBoard = dashlet.selectUserFilter(EVERYONES_ACTIVITIES).render();
        siteDashBoard = dashlet.selectTypeFilter(ALL_ITEMS).render();
        siteDashBoard = dashlet.selectHistoryFilter(TODAY).render();

        // 1. Select today value from drop-down menu
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        assertEquals(dashlet.getCurrentHistoryFilter(), TODAY);
        // 1. Created activities are displayed
        List<ShareLink> documents = dashlet.getSiteActivities(LinkType.Document);
        String documentsToString = documents.toString();
        Assert.assertNotNull(documents);
        Assert.assertTrue(documentsToString.contains(fileName));

        // 2. Select in the last 7 days value from drop-down menu
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        siteDashBoard = dashlet.selectHistoryFilter(SEVEN_DAYS).render();
        // 2. Created activities are displayed;
        documents = dashlet.getSiteActivities(LinkType.Document);
        documentsToString = documents.toString();
        Assert.assertNotNull(documents);
        Assert.assertTrue(documentsToString.contains(fileName));

        // 3. Change time to 8 days forward on the server and verify filter
        newDate = addDaysToCurrentDate(8);
        Runtime.getRuntime().exec(cmdNewDate + newDate);

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render();
        dashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SITE_ACTIVITIES).render().render();
        siteDashBoard = dashlet.selectHistoryFilter(SEVEN_DAYS).render();
        // 3. Created activities aren't displayed;
        documents = dashlet.getSiteActivities(LinkType.Document);
        documentsToString = documents.toString();
        Assert.assertTrue(documents.isEmpty());
        Assert.assertFalse(documentsToString.contains(fileName));

        // 4. Select in the last 14 days value from drop-down menu
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        siteDashBoard = dashlet.selectHistoryFilter(FOURTEEN_DAYS).render();
        // 4. Created activities are displayed;
        documents = dashlet.getSiteActivities(LinkType.Document);
        documentsToString = documents.toString();
        Assert.assertNotNull(documents);
        Assert.assertTrue(documentsToString.contains(fileName));

        // 5. Change time to 15 days forward on the server and verify filter
        newDate = addDaysToCurrentDate(15);
        Runtime.getRuntime().exec(cmdNewDate + newDate);

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render();
        dashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SITE_ACTIVITIES).render().render();
        siteDashBoard = dashlet.selectHistoryFilter(FOURTEEN_DAYS).render();
        // 5. Created activities aren't displayed;
        documents = dashlet.getSiteActivities(LinkType.Document);
        documentsToString = documents.toString();
        Assert.assertTrue(documents.isEmpty());
        Assert.assertFalse(documentsToString.contains(fileName));

        // 6. Select in the last 28 days value from drop-down menu
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        siteDashBoard = dashlet.selectHistoryFilter(TWENTY_EIGHT_DAYS).render();
        // 6. Created activities are displayed;
        documents = dashlet.getSiteActivities(LinkType.Document);
        documentsToString = documents.toString();
        Assert.assertNotNull(documents);
        Assert.assertTrue(documentsToString.contains(fileName));

        // 7. Change time to 29 days forward on the server and verify filter
        newDate = addDaysToCurrentDate(29);
        Runtime.getRuntime().exec(cmdNewDate + newDate);

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render();
        dashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SITE_ACTIVITIES).render().render();
        siteDashBoard = dashlet.selectHistoryFilter(TWENTY_EIGHT_DAYS).render();
        // 7. Created activities aren't displayed;
        documents = dashlet.getSiteActivities(LinkType.Document);
        documentsToString = documents.toString();
        Assert.assertTrue(documents.isEmpty());
        Assert.assertFalse(documentsToString.contains(fileName));

    }

    @Test(groups = { "DataPrepSiteActivitiesDashlet", "AlfrescoOne" })
    public void dataPrep_AONE_2828() throws Exception
    {

        String testName = getTestName();
        String fileName = getFileName(testName) + ".txt";
        String siteName = getSiteName(testName);

        // User
        String testUser = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // create a file with user1
        ContentDetails contentDetails1 = new ContentDetails();
        contentDetails1.setName(fileName);
        contentDetails1.setContent("file content for test 2828");
        documentLibraryPage = ShareUser.createContent(drone, contentDetails1, ContentType.PLAINTEXT);

        documentLibraryPage = drone.getCurrentPage().render();

        // select the file
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);

        // like document
        detailsPage.selectLike();

    }

    /**
     * AONE-2828:My Activities dashlet. Date filter -in Enterprise
     * AONE-12027:My Activities dashlet. Date filter - in Cloud
     */

    @Test(groups = { "AlfrescoOne" })
    public void AONE_2828() throws Exception
    {

        testName = getTestName();
        String fileName = getFileName(testName) + ".txt";
        String testUser = getUserNameFreeDomain(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        DashBoardPage dashBoard = ShareUser.openUserDashboard(drone).render(maxWaitTime);

        // 1. Select today value from drop-down menu
        MyActivitiesDashlet activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromUserActivities("Everyone\'s activities").render();
        activitiesDashlet.selectOptionFromHistoryFilter(TODAY).render();
        Assert.assertTrue(activitiesDashlet.isHistoryOptionSelected(TODAY), "Today option is not selected");
        // 1. Created activities are displayed
        List<ActivityShareLink> activities = activitiesDashlet.getActivities();
        String documentsToString = activities.toString();
        Assert.assertFalse(activities.isEmpty());
        Assert.assertTrue(documentsToString.contains(fileName));

        // 2. Select in the last 7 days value from drop-down menu
        activitiesDashlet.selectOptionFromHistoryFilter(SEVEN_DAYS).render();
        Assert.assertTrue(activitiesDashlet.isHistoryOptionSelected(SEVEN_DAYS), "Last 7 days option is not selected");
        // 2. Created activities are displayed;
        activities = activitiesDashlet.getActivities();
        documentsToString = activities.toString();
        Assert.assertFalse(activities.isEmpty());
        Assert.assertTrue(documentsToString.contains(fileName));

        // 3. Change time to 8 days forward on the server and verify filter
        newDate = addDaysToCurrentDate(8);
        Runtime.getRuntime().exec(cmdNewDate + newDate);

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        dashBoard = ShareUser.openUserDashboard(drone).render();
        activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromHistoryFilter(SEVEN_DAYS).render();
        // 3. Created activities aren't displayed;
        activities = activitiesDashlet.getActivities();
        documentsToString = activities.toString();
        Assert.assertTrue(activities.isEmpty());
        Assert.assertFalse(documentsToString.contains(fileName));

        // 4. Select in the last 14 days value from drop-down menu
        activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromHistoryFilter(FOURTEEN_DAYS).render();
        Assert.assertTrue(activitiesDashlet.isHistoryOptionSelected(FOURTEEN_DAYS), "Last 14 days option is not selected");
        // 4. Created activities are displayed;
        activities = activitiesDashlet.getActivities();
        documentsToString = activities.toString();
        Assert.assertFalse(activities.isEmpty());
        Assert.assertTrue(documentsToString.contains(fileName));

        // 5. Change time to 15 days forward on the server and verify filter
        newDate = addDaysToCurrentDate(15);
        Runtime.getRuntime().exec(cmdNewDate + newDate);

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        dashBoard = ShareUser.openUserDashboard(drone).render();
        activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromHistoryFilter(FOURTEEN_DAYS).render();
        // 5. Created activities aren't displayed;
        activities = activitiesDashlet.getActivities();
        documentsToString = activities.toString();
        Assert.assertTrue(activities.isEmpty());
        Assert.assertFalse(documentsToString.contains(fileName));

        // 6. Select in the last 28 days value from drop-down menu
        activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromHistoryFilter(TWENTY_EIGHT_DAYS).render();
        Assert.assertTrue(activitiesDashlet.isHistoryOptionSelected(TWENTY_EIGHT_DAYS), "Last 28 days option is not selected");
        // 6. Created activities are displayed;
        activities = activitiesDashlet.getActivities();
        documentsToString = activities.toString();
        Assert.assertFalse(activities.isEmpty());
        Assert.assertTrue(documentsToString.contains(fileName));

        // TODO: 7. Change time to 29 days forward on the server and verify filter
        newDate = addDaysToCurrentDate(29);
        Runtime.getRuntime().exec(cmdNewDate + newDate);

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        dashBoard = ShareUser.openUserDashboard(drone).render();
        activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromHistoryFilter(TWENTY_EIGHT_DAYS).render();
        // 7. Created activities aren't displayed;
        activities = activitiesDashlet.getActivities();
        documentsToString = activities.toString();
        Assert.assertTrue(activities.isEmpty());
        Assert.assertFalse(documentsToString.contains(fileName));

    }

    private String addDaysToCurrentDate(int noOfDays) throws Exception
    {
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(0, noOfDays, "1:00 AM", "2:00 AM", true);
        String newAllDate = timeValues.get("endDateValue");

        String newDateFormatted = CalendarUtil.getDateInFormat(newAllDate, "dd-MM-yyyy", true);
        return newDateFormatted;
    }
}
