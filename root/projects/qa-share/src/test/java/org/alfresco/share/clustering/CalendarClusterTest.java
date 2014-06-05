package org.alfresco.share.clustering;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.InformationEventForm;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.List;

/**
 * @author Sergey Kardash
 */
public class CalendarClusterTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(DocLibClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    private String testUser;
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
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering);

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

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

    /**
     * Test - Enterprise40x_10115: Creating event
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Admin is logged in to the Share at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Calendar page is opened at the server A</li>
     * <li>Create event</li>
     * <li>At the server B verify that created event is displayed</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3155() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String event = "event_" + getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Calendar > Month tab is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create event using Add event button, e.g. event
        calendarPage = calendarPage.createEvent(event, event, event, false);

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event), "The " + event
                + " isn't correctly displayed on the month tab. Server A");

        ShareUser.logout(drone);

        // verify that created event is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event), "The " + event
                + " isn't correctly displayed on the month tab. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - Enterprise40x_10116: Editing event
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Admin is logged in to the Share at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Calendar page is opened at the server A</li>
     * <li>Create event</li>
     * <li>Change name, place and description of the event</li>
     * <li>Verify that all changes made on the server A are available from the server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3156() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String event = "event_" + getRandomString(5);
        String edit_event1_what = "single_day_event1_edit_what";
        String edit_event1_where = "single_day_event1_edit_where";
        String edit_event1_description = "single_day_event1_edit_description";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Calendar > Month tab is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create event using Add event button, e.g. event
        calendarPage = calendarPage.createEvent(event, event, event, false);

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event), "The " + event
                + " isn't correctly displayed on the month tab. Server A");

        // Change name, place and description of the event
        calendarPage = calendarPage.editEvent(event, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, CalendarPage.ActionEventVia.MONTH_TAB, edit_event1_what,
                edit_event1_where, edit_event1_description, null, null, null, null, null, false, null).render();

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, edit_event1_what), "The " + edit_event1_what
                + " isn't correctly displayed on the month tab (after edition). Server A");
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event), "The " + event
                + " is displayed on the month tab. Server A");

        ShareUser.logout(drone);

        // verify that created event is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Verify that all changes made on the server A are available from the server B
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, edit_event1_what), "The " + edit_event1_what
                + " isn't correctly displayed on the month tab. Server B");
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event), "The " + event
                + " is displayed on the month tab. Server B");

        InformationEventForm informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, edit_event1_what).render();
        Assert.assertTrue(informationEventForm.getWhatDetail().contains(edit_event1_what), "The " + edit_event1_what
                + " isn't correctly displayed on the information form. Server B");
        Assert.assertTrue(informationEventForm.getWhereDetail().contains(edit_event1_where), "The " + edit_event1_where
                + " isn't correctly displayed on the information form. Server B");
        Assert.assertTrue(informationEventForm.getDescriptionDetail().contains(edit_event1_description), "The " + edit_event1_description
                + " isn't correctly displayed on the information form. Server B");

        informationEventForm.closeInformationForm();

        ShareUser.logout(drone);
    }

    /**
     * Test - Enterprise40x_10117: Deleting event
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Admin is logged in to the Share at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Calendar page is opened at the server A</li>
     * <li>Create event</li>
     * <li>Deleting event</li>
     * <li>Verify that event was deleted successfully. Server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3157() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String event = "event_" + getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Calendar > Month tab is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create event using Add event button, e.g. event
        calendarPage = calendarPage.createEvent(event, event, event, false);

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event), "The " + event
                + " isn't correctly displayed on the month tab. Server A");

        // Delete any event, e.g. event
        calendarPage = calendarPage.deleteEvent(event, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, CalendarPage.ActionEventVia.MONTH_TAB).render();

        // The event is deleted and is not displayed on all the tab Month
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event), "The " + event
                + " is displayed on the month tab. Server A");

        ShareUser.logout(drone);

        // verify that deleted and is not displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // The event is deleted and is not displayed on all the tab Month
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event), "The " + event
                + " is displayed on the month tab. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - Enterprise40x_10118: Create all-day event
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Admin is logged in to the Share at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Calendar page is opened at the server A</li>
     * <li>Create all-day event</li>
     * <li>At the server B verify that Created all-day event is displayed</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3158() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String event = "event_" + getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Calendar > Month tab is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create any all day event, e.g. event
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.ADD_EVENT_BUTTON, event, event, event, null, null, null, null, null, true);

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event), "The all day " + event
                + " isn't correctly displayed on the month tab. Server A");

        ShareUser.logout(drone);

        // verify that created event is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event), "The all day " + event
                + " isn't correctly displayed on the month tab. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - Enterprise40x_10119: Create several days duration event
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Admin is logged in to the Share at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Calendar page is opened at the server A</li>
     * <li>Create several days duration event</li>
     * <li>At the server B verify that Created several days duration event is displayed</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void ALF_3159() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String event = "event_" + getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Calendar > Month tab is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create any multiply day event, e.g. event
        Calendar calendar = Calendar.getInstance();
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        int todayDate = calendar.get(Calendar.DATE);

        int anotherDate;
        if (lastDate == todayDate)
        {
            anotherDate = todayDate - 1;
            calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event, event, event, String.valueOf(anotherDate), null,
                    String.valueOf(todayDate), null, null, false);
        }
        else
        {
            anotherDate = todayDate + 1;
            calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event, event, event, String.valueOf(todayDate), null,
                    String.valueOf(anotherDate), null, null, false);
        }

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event), "The several days duration event " + event
                + " isn't correctly displayed on the month tab. Server A");

        ShareUser.logout(drone);

        // verify that created several days duration event is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event), "The several days duration event " + event
                + " isn't correctly displayed on the month tab. Server B");

        ShareUser.logout(drone);
    }
}
