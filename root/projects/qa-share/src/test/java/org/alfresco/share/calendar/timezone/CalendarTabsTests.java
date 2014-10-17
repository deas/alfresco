package org.alfresco.share.calendar.timezone;

/**
 * Tests for Calendar->TimeZone->CalendarTabs
 * 
 * @author Corina.Nechifor
 */

import java.util.Map;
import java.util.TimeZone;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.InformationEventForm;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CalendarUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class CalendarTabsTests extends AbstractUtils
{

    private static final Logger logger = Logger.getLogger(CalendarTabsTests.class);
    private String testName;
    private String testUser;

    private String defaultTZ = "London";
    private String newTZ = "Bucharest";
    
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);

        logger.info("Start Tests in: " + testName);

        CalendarUtil.changeTimeZone(defaultTZ);
    }


    @AfterMethod(alwaysRun = true)
    public void teardownMEthod() throws Exception
    {
        CalendarUtil.changeTimeZone(defaultTZ);
    }

    @Test(groups = { "DataPrepCalendar" })
    public void dataPrep_AONE_CalendarTabs() throws Exception
    {

        // Create normal User
       String[] testUser2 = new String[] { testUser };
       CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);
    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_15911() throws Exception
    {
        boolean allDay = false;
        ShareUser.login(drone, testUser);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

        // Calendar component is added to the site
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.setTimeForSingleDay("3:40 AM", "7:30 AM", false);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "single_day_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay).render();

        // verify event was added
        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1, expectedStartDate, expectedEndDate);

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        expectedStartDate = CalendarUtil.convertDateToNewTimeZone(expectedStartDate, defaultTZ, newTZ, allDay);
        expectedEndDate = CalendarUtil.convertDateToNewTimeZone(expectedEndDate, defaultTZ, newTZ, allDay);

        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1, expectedStartDate, expectedEndDate);

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_15912() throws Exception
    {
        boolean allDay = true;
        ShareUser.login(drone, testUser);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Calendar component is added to the site
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.setTimeForSingleDay("3:00 AM", "7:00 AM", true);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "single_day_allDay_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        // verify event was added
        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_15915() throws Exception
    {
        boolean allDay = false;
        ShareUser.login(drone, testUser);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Calendar component is added to the site
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(0, 14, "7:00 AM", "9:00 AM", allDay);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_weeks_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        expectedStartDate = CalendarUtil.convertDateToNewTimeZone(expectedStartDate, defaultTZ, newTZ, allDay);
        expectedEndDate = CalendarUtil.convertDateToNewTimeZone(expectedEndDate, defaultTZ, newTZ, allDay);

        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_15916() throws Exception
    {
        boolean allDay = true;
        ShareUser.login(drone, testUser);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Calendar component is added to the site
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(0, 14, "7:00 AM", "9:00 AM", allDay);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_weeks_allDay_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        // verify event was added
        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_15913() throws Exception
    {
        boolean allDay = false;
        ShareUser.login(drone, testUser);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Calendar component is added to the site
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(0, 2, "7:00 AM", "9:00 AM", allDay);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_days_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        // verify event was added
        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        expectedStartDate = CalendarUtil.convertDateToNewTimeZone(expectedStartDate, defaultTZ, newTZ, allDay);
        expectedEndDate = CalendarUtil.convertDateToNewTimeZone(expectedEndDate, defaultTZ, newTZ, allDay);

        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_15914() throws Exception
    {
        boolean allDay = true;
        ShareUser.login(drone, testUser);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Calendar component is added to the site
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(0, 3, "7:00 AM", "9:00 AM", allDay);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_days_allDay_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        // verify event was added
        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_15917() throws Exception
    {
        boolean allDay = false;
        ShareUser.login(drone, testUser);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Calendar component is added to the site
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(2, 2, "7:00 AM", "9:00 AM", allDay);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_months_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        // verify event was added
        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        expectedStartDate = CalendarUtil.convertDateToNewTimeZone(expectedStartDate, defaultTZ, newTZ, allDay);
        expectedEndDate = CalendarUtil.convertDateToNewTimeZone(expectedEndDate, defaultTZ, newTZ, allDay);

        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_15918() throws Exception
    {
        boolean allDay = true;
        ShareUser.login(drone, testUser);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Calendar component is added to the site
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(3, 1, "7:00 AM", "9:00 AM", allDay);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_months_allDay_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        // verify event was added

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, event1, expectedStartDate, expectedEndDate);

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_15919() throws Exception
    {
        boolean allDay = false;
        ShareUser.login(drone, testUser);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Calendar component is added to the site
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(0, 1, "2:30 PM", "1:00 AM", allDay);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_days_specific_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        // verify event was added
        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        expectedStartDate = CalendarUtil.convertDateToNewTimeZone(expectedStartDate, defaultTZ, newTZ, allDay);
        expectedEndDate = CalendarUtil.convertDateToNewTimeZone(expectedEndDate, defaultTZ, newTZ, allDay);

        clickOnEventAndVerifyDate(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

        clickOnEventAndVerifyDate(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event1, expectedStartDate, expectedEndDate);

    }

    /***
     * @param eventType
     * @param eventName
     * @param expectedStartDate
     * @param expectedEndDate
     */
    private void clickOnEventAndVerifyDate(CalendarPage.EventType eventType, String eventName, String expectedStartDate, String expectedEndDate)
    {
        CalendarPage calendarPage = drone.getCurrentPage().render();

        if (eventType.name().startsWith("AGENDA"))
            calendarPage.chooseAgendaTab().render(maxWaitTime);
        else if (eventType.name().startsWith("DAY"))
            calendarPage.chooseDayTab().render(maxWaitTime);
        else if (eventType.name().startsWith("MONTH"))
            calendarPage.chooseMonthTab().render(maxWaitTime);
        else if (eventType.name().startsWith("WEEK"))
            calendarPage.chooseWeekTab().render(maxWaitTime);

        // verify the event is present in tab
        Assert.assertTrue(calendarPage.isEventPresent(eventType, eventName), "The " + eventName + " isn't correctly displayed on the tab " + eventType.name());

        InformationEventForm eventInfo = calendarPage.clickOnEvent(eventType, eventName).render(maxWaitTime);

        // get start and end date time from event information page
        String actualStartDate = eventInfo.getStartDateTime().replace("at ", "");
        String actualEndDate = eventInfo.getEndDateTime().replace("at ", "");

        calendarPage = eventInfo.closeInformationForm().render();

        Assert.assertEquals(actualStartDate, expectedStartDate);
        Assert.assertEquals(actualEndDate, expectedEndDate);
    }

}