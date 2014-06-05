package org.alfresco.share.sanity;

import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * @author Sergey Kardash
 */
public class SiteCalendarSanityTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(SiteCalendarSanityTest.class);

    protected String testUser;

    protected String siteName = "";

    /**
     * Class includes: Tests from TestLink in Area: Sanity Solr Site Calendar
     * <ul>
     * <li>Test covered work with calendar</li>
     * </ul>
     */
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
    }

    // Day Tab
    @Test(groups = { "Sanity", "EnterpriseOnly" }, timeOut = 300000)
    public void ALF_3080() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());

        String event1 = "single_day_event1_" + getRandomString(5);
        String edit_event1 = "single_day_event1_edit_" + getRandomString(5);
        String event2 = "all_day_event2_" + getRandomString(5);
        String edit_event2 = "all_day_event2_edit_" + getRandomString(5);
        String event3 = "mul_day_event3_" + getRandomString(5);
        String event4 = "event4_" + getRandomString(5);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Calendar > Day tab is opened
//        CustomizeSitePage customizeSizePage = ShareUser.customizeSite(drone, siteName);
//        List<SitePageType> pageTypes = new ArrayList<SitePageType>();
//        pageTypes.add(SitePageType.CALENDER);
//        customizeSizePage.addPages(pageTypes);
        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName);
        savePageSource(getTestName() + "_add_calendar");
        saveOsScreenShot(getTestName() + "_add_calendar1");
        saveScreenShot(getTestName() + "_add_calendar2");
        saveScreenShot(drone,getTestName() + "_add_calendar3");
        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, null, null, null, null, null, false);

        // Create any all day event, e.g. event2
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event2, event2, event2, null, null, null, null, null, true);

        // Create any multiply day event, e.g. event3
        Calendar calendar = Calendar.getInstance();
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        int todayDate = calendar.get(Calendar.DATE);

        int anotherDate;
        if (lastDate == todayDate)
        {
            anotherDate = todayDate - 1;
            calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event3, event3, event3, String.valueOf(anotherDate), null,
                    String.valueOf(todayDate), null, null, false);
        }
        else
        {
            anotherDate = todayDate + 1;
            calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event3, event3, event3, String.valueOf(todayDate), null,
                    String.valueOf(anotherDate), null, null, false);
        }

        // Create event using Add event button, e.g. event4
        calendarPage = calendarPage.createEvent(event4, event4, event4, false);

        // The event is created and displayed correctly on all the tabs: Day, Week, Month, Agenda
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseWeekTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the week tab");

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseAgendaTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the agenda tab");

        calendarPage = calendarPage.chooseDayTab().render();

        // Click Show working hours
        calendarPage = calendarPage.showWorkingHours().render();
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.FIRST_WORKING_HOUR), "Not only working hours (7.00 - 18.00) are shown");
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.LAST_WORKING_HOUR), "Not only working hours (7.00 - 18.00) are shown");

        // Click Show all hours
        calendarPage = calendarPage.showAllHours().render();
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.FIRST_ALL_HOUR), "Not all hours are shown");
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.LAST_ALL_HOUR), "Not all hours are shown");

        // Edit any not all-day event, e.g. event1
        calendarPage = calendarPage.editEvent(event1, CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, CalendarPage.ActionEventVia.DAY_TAB, edit_event1, null,
                edit_event1, null, null, null, null, null, false, null).render();

        calendarPage = calendarPage.editEvent(event2, CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, CalendarPage.ActionEventVia.DAY_TAB, edit_event2, null,
                edit_event2, null, null, null, null, null, false, null).render();

        // The event is edited and displayed correctly on all the tabs: Day, Week, Month, Agenda

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseWeekTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the week tab");

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseAgendaTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the agenda tab");

        calendarPage = calendarPage.chooseDayTab().render();

        // Delete any event, e.g. event3
        calendarPage = calendarPage.deleteEvent(event3, CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, CalendarPage.ActionEventVia.DAY_TAB).render();

        // The event is deleted and is not displayed on all the tabs: Day, Week, Month, Agenda
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseWeekTab().render();

        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the week tab");

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseAgendaTab().render();

        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the agenda tab");

    }

    // Week Tab
    @Test(groups = { "Sanity", "EnterpriseOnly" }, timeOut = 300000)
    public void ALF_3081() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());

        String event1 = "single_day_event1_" + getRandomString(5);
        String edit_event1 = "single_day_event1_edit_" + getRandomString(5);
        String event2 = "all_day_event2_" + getRandomString(5);
        String edit_event2 = "all_day_event2_edit_" + getRandomString(5);
        String event3 = "mul_day_event3_" + getRandomString(5);
        String event4 = "event4_" + getRandomString(5);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Calendar > Week tab is opened
        CustomizeSitePage customizeSizePage = ShareUser.customizeSite(drone, siteName);
        List<SitePageType> pageTypes = new ArrayList<SitePageType>();
        pageTypes.add(SitePageType.CALENDER);
        customizeSizePage.addPages(pageTypes);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        Calendar calendar = Calendar.getInstance();
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        int todayDate = calendar.get(Calendar.DATE);

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.WEEK_TAB, event1, event1, event1, String.valueOf(todayDate), null,
                String.valueOf(todayDate), null, null, false);

        // Create any all day event, e.g. event2
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.WEEK_TAB, event2, event2, event2, String.valueOf(todayDate), null,
                String.valueOf(todayDate), null, null, true);

        // Create any multiply day event, e.g. event3
        int anotherDate;
        if (lastDate == todayDate)
        {
            anotherDate = todayDate - 1;
            calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.WEEK_TAB, event3, event3, event3, String.valueOf(anotherDate), null,
                    String.valueOf(todayDate), null, null, false);
        }
        else
        {
            anotherDate = todayDate + 1;
            calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.WEEK_TAB, event3, event3, event3, String.valueOf(todayDate), null,
                    String.valueOf(anotherDate), null, null, false);
        }

        // Create event using Add event button, e.g. event4
        calendarPage = calendarPage.createEvent(event4, event4, event4, false);

        // The event is created and displayed correctly on all the tabs: Day, Week, Month, Agenda
        calendarPage = calendarPage.chooseDayTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseAgendaTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the agenda tab");

        calendarPage = calendarPage.chooseWeekTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the week tab");

        // Click Show working hours
        calendarPage = calendarPage.showWorkingHours().render();
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.FIRST_WORKING_HOUR), "Not only working hours (7.00 - 18.00) are shown");
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.LAST_WORKING_HOUR), "Not only working hours (7.00 - 18.00) are shown");

        // Click Show all hours
        calendarPage = calendarPage.showAllHours().render();
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.FIRST_ALL_HOUR), "Not all hours are shown");
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.LAST_ALL_HOUR), "Not all hours are shown");

        // Edit any not all-day event, e.g. event1
        calendarPage = calendarPage.editEvent(event1, CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, CalendarPage.ActionEventVia.WEEK_TAB, edit_event1, null,
                edit_event1, null, null, null, null, null, false, null).render();

        calendarPage = calendarPage.editEvent(event2, CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, CalendarPage.ActionEventVia.WEEK_TAB, edit_event2, null,
                edit_event2, null, null, null, null, null, false, null).render();

        // The event is edited and displayed correctly on all the tabs: Day, Week, Month, Agenda

        calendarPage = calendarPage.chooseDayTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseWeekTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the week tab");

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseAgendaTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the agenda tab");

        calendarPage = calendarPage.chooseWeekTab().render();

        // Delete any event, e.g. event3
        calendarPage = calendarPage.deleteEvent(event3, CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, CalendarPage.ActionEventVia.WEEK_TAB).render();

        // The event is deleted and is not displayed on all the tabs: Day, Week, Month, Agenda
        calendarPage = calendarPage.chooseDayTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseWeekTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the week tab");

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseAgendaTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the agenda tab");

        ShareUser.logout(drone);
    }

    // Month Tab
    @Test(groups = { "Sanity", "EnterpriseOnly" }, timeOut = 300000)
    public void ALF_3082() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());

        String event1 = "single_day_event1_" + getRandomString(5);
        String edit_event1 = "single_day_event1_edit_" + getRandomString(5);
        String event2 = "all_day_event2_" + getRandomString(5);
        String edit_event2 = "all_day_event2_edit_" + getRandomString(5);
        String event3 = "mul_day_event3_" + getRandomString(5);
        String event4 = "event4_" + getRandomString(5);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Calendar > Month tab is opened
        CustomizeSitePage customizeSizePage = ShareUser.customizeSite(drone, siteName);
        List<SitePageType> pageTypes = new ArrayList<SitePageType>();
        pageTypes.add(SitePageType.CALENDER);
        customizeSizePage.addPages(pageTypes);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event1, event1, event1, null, null, null, null, null, false);

        // Create any all day event, e.g. event2
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event2, event2, event2, null, null, null, null, null, true);

        // Create any multiply day event, e.g. event3
        Calendar calendar = Calendar.getInstance();
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        int todayDate = calendar.get(Calendar.DATE);

        int anotherDate;
        if (lastDate == todayDate)
        {
            anotherDate = todayDate - 1;
            calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event3, event3, event3, String.valueOf(anotherDate), null,
                    String.valueOf(todayDate), null, null, false);
        }
        else
        {
            anotherDate = todayDate + 1;
            calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event3, event3, event3, String.valueOf(todayDate), null,
                    String.valueOf(anotherDate), null, null, false);
        }

        // Create event using Add event button, e.g. event4
        calendarPage = calendarPage.createEvent(event4, event4, event4, false);

        calendarPage = calendarPage.chooseDayTab().render();
        // The event is created and displayed correctly on all the tabs: Day, Week, Month, Agenda
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseWeekTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the week tab");

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseAgendaTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the agenda tab");

        calendarPage = calendarPage.chooseMonthTab().render();
        // Edit any not all-day event, e.g. event1
        calendarPage = calendarPage.editEvent(event1, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, CalendarPage.ActionEventVia.MONTH_TAB, edit_event1, null,
                edit_event1, null, null, null, null, null, false, null).render();

        calendarPage = calendarPage.editEvent(event2, CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, CalendarPage.ActionEventVia.MONTH_TAB, edit_event2, null,
                edit_event2, null, null, null, null, null, false, null).render();

        // The event is edited and displayed correctly on all the tabs: Day, Week, Month, Agenda

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseWeekTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the week tab");

        calendarPage = calendarPage.chooseDayTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseAgendaTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the agenda tab");

        calendarPage = calendarPage.chooseMonthTab().render();
        // Delete any event, e.g. event3
        calendarPage = calendarPage.deleteEvent(event3, CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, CalendarPage.ActionEventVia.MONTH_TAB).render();

        // The event is deleted and is not displayed on all the tabs: Day, Week, Month, Agenda
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseDayTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseWeekTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the week tab");

        calendarPage = calendarPage.chooseAgendaTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the agenda tab");
    }

    // Agenda Tab
    @Test(groups = { "Sanity", "EnterpriseOnly" }, timeOut = 300000)
    public void ALF_3083() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());

        String event1 = "single_day_event1_" + getRandomString(5);
        String edit_event1 = "single_day_event1_edit_" + getRandomString(5);
        String event2 = "all_day_event2_" + getRandomString(5);
        String edit_event2 = "all_day_event2_edit_" + getRandomString(5);
        String event3 = "mul_day_event3_" + getRandomString(5);
        String event4 = "event4_" + getRandomString(5);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Calendar > Agenda tab is opened
        CustomizeSitePage customizeSizePage = ShareUser.customizeSite(drone, siteName);
        List<SitePageType> pageTypes = new ArrayList<SitePageType>();
        pageTypes.add(SitePageType.CALENDER);
        customizeSizePage.addPages(pageTypes);
        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.AGENDA_TAB, event1, event1, event1, null, null, null, null, null, false);

        // Create any all day event, e.g. event2
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.AGENDA_TAB, event2, event2, event2, null, null, null, null, null, true);

        // Create any multiply day event, e.g. event3
        Calendar calendar = Calendar.getInstance();
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        int todayDate = calendar.get(Calendar.DATE);

        int anotherDate;
        if (lastDate == todayDate)
        {
            anotherDate = todayDate - 1;
            calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.AGENDA_TAB, event3, event3, event3, String.valueOf(anotherDate), null,
                    String.valueOf(todayDate), null, null, false);
        }
        else
        {
            anotherDate = todayDate + 1;
            calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.AGENDA_TAB, event3, event3, event3, String.valueOf(todayDate), null,
                    String.valueOf(anotherDate), null, null, false);
        }

        // Create event using Add event button, e.g. event4
        calendarPage = calendarPage.createEvent(event4, event4, event4, false);

        calendarPage = calendarPage.chooseDayTab().render();
        // The event is created and displayed correctly on all the tabs: Day, Week, Month, Agenda
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseWeekTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the week tab");

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseAgendaTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event4), "The " + event4
                + " isn't correctly displayed on the agenda tab");

        calendarPage = calendarPage.chooseMonthTab().render();
        // Edit any not all-day event, e.g. event1
        calendarPage = calendarPage.editEvent(event1, CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, CalendarPage.ActionEventVia.AGENDA_TAB, edit_event1,
                null, edit_event1, null, null, null, null, null, false, null).render();

        calendarPage = calendarPage.editEvent(event2, CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, CalendarPage.ActionEventVia.AGENDA_TAB, edit_event2,
                null, edit_event2, null, null, null, null, null, false, null).render();

        // The event is edited and displayed correctly on all the tabs: Day, Week, Month, Agenda
        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseWeekTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the week tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the week tab");

        calendarPage = calendarPage.chooseDayTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the day tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseAgendaTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, edit_event1), "The " + edit_event1
                + " isn't correctly displayed on the agenda tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, edit_event2), "The " + edit_event2
                + " isn't correctly displayed on the agenda tab");

        // Delete any event, e.g. event3
        calendarPage = calendarPage.deleteEvent(event3, CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, CalendarPage.ActionEventVia.AGENDA_TAB).render();

        // The event is deleted and is not displayed on all the tabs: Day, Week, Month, Agenda
        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseDayTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseWeekTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the week tab");

        calendarPage = calendarPage.chooseAgendaTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event3), "The " + event3
                + " isn't correctly displayed on the agenda tab");
    }

    // Tags and Activities
    @Test(groups = { "Sanity", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_3084() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());

        String event1 = "single_day_event1_" + getRandomString(5);
        String event2 = "all_day_event2_" + getRandomString(5);
        String tag1 = "tag1_" + getRandomString(5);
        String tag2 = "tag2_" + getRandomString(5);
        String tag3 = "tag3_" + getRandomString(5);
        String tags = tag2 + " " + tag3;
        String[] removeTags = { tag2 };

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Calendar > Month tab is opened
        CustomizeSitePage customizeSizePage = ShareUser.customizeSite(drone, siteName);
        List<SitePageType> pageTypes = new ArrayList<SitePageType>();
        pageTypes.add(SitePageType.CALENDER);
        customizeSizePage.addPages(pageTypes);
        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create one event without tags, e.g. event1, and one event with tags, e.g. event2
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event1, event1, event1, null, null, null, null, null, false);
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event2, event2, event2, null, null, null, null, tags, false);

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the month tab");

        Assert.assertTrue(calendarPage.isShowAllItemsPresent(), "Link 'Show All items' isn't displayed on the month tab");
        Assert.assertTrue(calendarPage.isTagPresent(tag2), "Tag Link '" + tag2 + "' isn't displayed on the month tab");
        Assert.assertTrue(calendarPage.isTagPresent(tag3), "Tag Link '" + tag3 + "' isn't displayed on the month tab");

        // Verify tag filtering is working: click on the tag in Tags section
        calendarPage = calendarPage.clickTagLink(tag2);
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " is displayed on the month tab. Tag filtering isn't working correctly");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the month tab. Tag filtering isn't working correctly");

        // Click Show all items
        calendarPage = calendarPage.clickShowAllItems();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab. After click 'Show all items'");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the month tab. After click 'Show all items'");

        // Previous steps on all the tabs: Day
        calendarPage = calendarPage.chooseDayTab().render();
        calendarPage = calendarPage.clickTagLink(tag2);
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " is displayed on the day tab. Tag filtering isn't working correctly");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the day tab. Tag filtering isn't working correctly");

        // Click Show all items
        calendarPage = calendarPage.clickShowAllItems();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the day tab. After click 'Show all items'");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the day tab. After click 'Show all items'");

        // Previous steps on all the tabs: Week
        calendarPage = calendarPage.chooseWeekTab().render();
        calendarPage = calendarPage.clickTagLink(tag2);
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " is displayed on the week tab. Tag filtering isn't working correctly");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the week tab. Tag filtering isn't working correctly");

        // Click Show all items
        calendarPage = calendarPage.clickShowAllItems();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the week tab. After click 'Show all items'");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the week tab. After click 'Show all items'");

        // Previous steps on all the tabs: Agenda
        calendarPage = calendarPage.chooseAgendaTab().render();
        calendarPage = calendarPage.clickTagLink(tag2);
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " is displayed on the agenda tab. Tag filtering isn't working correctly");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the agenda tab. Tag filtering isn't working correctly");

        // Click Show all items
        calendarPage = calendarPage.clickShowAllItems();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the agenda tab. After click 'Show all items'");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the agenda tab. After click 'Show all items'");

        // Edit event1: add a tag to it. Verify filtering
        calendarPage = calendarPage.chooseMonthTab().render();
        calendarPage = calendarPage.editEvent(event1, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, CalendarPage.ActionEventVia.MONTH_TAB, null, null, null,
                null, null, null, null, tag1, false, null).render();

        Assert.assertTrue(calendarPage.isTagPresent(tag1), "Tag Link '" + tag1 + "' isn't displayed on the month tab");

        // Verify tag filtering is working: click on the tag in Tags section
        calendarPage = calendarPage.clickTagLink(tag1);
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't displayed on the month tab. Tag filtering isn't working correctly");
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn correctly displayed on the month tab. Tag filtering isn't working correctly");

        // Click Show all items
        calendarPage = calendarPage.clickShowAllItems();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab. After edit event1 and click 'Show all items'");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the month tab. After edit event1 and click 'Show all items'");

        // Previous steps on all the tabs: Day
        calendarPage = calendarPage.chooseDayTab().render();
        calendarPage = calendarPage.clickTagLink(tag1);
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't displayed on the day tab. Tag filtering isn't working correctly");
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event2), "The " + event2
                + " is correctly displayed on the day tab. Tag filtering isn't working correctly");

        // Click Show all items
        calendarPage = calendarPage.clickShowAllItems();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the day tab. After click 'Show all items'");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the day tab. After click 'Show all items'");

        // Previous steps on all the tabs: Week
        calendarPage = calendarPage.chooseWeekTab().render();
        calendarPage = calendarPage.clickTagLink(tag1);
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't displayed on the week tab. Tag filtering isn't working correctly");
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event2), "The " + event2
                + " is correctly displayed on the week tab. Tag filtering isn't working correctly");

        // Click Show all items
        calendarPage = calendarPage.clickShowAllItems();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the week tab. After click 'Show all items'");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the week tab. After click 'Show all items'");

        // Previous steps on all the tabs: Agenda
        calendarPage = calendarPage.chooseAgendaTab().render();
        calendarPage = calendarPage.clickTagLink(tag1);
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't displayed on the agenda tab. Tag filtering isn't working correctly");
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event2), "The " + event2
                + " is correctly displayed on the agenda tab. Tag filtering isn't working correctly");

        // Click Show all items
        calendarPage = calendarPage.clickShowAllItems();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the agenda tab. After click 'Show all items'");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the agenda tab. After click 'Show all items'");

        // Edit event2: remove the tag. Verify filtering
        calendarPage = calendarPage.chooseMonthTab().render();
        calendarPage = calendarPage.editEvent(event2, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, CalendarPage.ActionEventVia.MONTH_TAB, null, null, null,
                null, null, null, null, null, false, removeTags).render();

        Assert.assertTrue(calendarPage.isTagPresent(tag1), "Tag Link '" + tag1 + "' isn't displayed on the month tab");
        Assert.assertTrue(calendarPage.isTagPresent(tag3), "Tag Link '" + tag3 + "' isn't displayed on the month tab");
        Assert.assertFalse(calendarPage.isTagPresent(tag2), "Tag Link '" + tag2 + "' is displayed on the month tab");

        // Verify tag filtering is working: click on the tag in Tags section
        calendarPage = calendarPage.clickTagLink(tag3);
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " is displayed on the month tab. Tag filtering isn't working correctly");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the month tab. Tag filtering isn't working correctly");

        // Click Show all items
        calendarPage = calendarPage.clickShowAllItems();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab. After click 'Show all items'");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the month tab. After click 'Show all items'");

        // Previous steps on all the tabs: Day
        calendarPage = calendarPage.chooseDayTab().render();
        calendarPage = calendarPage.clickTagLink(tag3);
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " is displayed on the day tab. Tag filtering isn't working correctly");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the day tab. Tag filtering isn't working correctly");

        // Click Show all items
        calendarPage = calendarPage.clickShowAllItems();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the day tab. After click 'Show all items'");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the day tab. After click 'Show all items'");

        // Previous steps on all the tabs: Week
        calendarPage = calendarPage.chooseWeekTab().render();
        calendarPage = calendarPage.clickTagLink(tag3);
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " is displayed on the week tab. Tag filtering isn't working correctly");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the week tab. Tag filtering isn't working correctly");

        // Click Show all items
        calendarPage = calendarPage.clickShowAllItems();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the week tab. After click 'Show all items'");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the week tab. After click 'Show all items'");

        // Previous steps on all the tabs: Agenda
        calendarPage = calendarPage.chooseAgendaTab().render();
        calendarPage = calendarPage.clickTagLink(tag3);
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " is displayed on the agenda tab. Tag filtering isn't working correctly");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the agenda tab. Tag filtering isn't working correctly");

        // Click Show all items
        calendarPage = calendarPage.clickShowAllItems();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the agenda tab. After click 'Show all items'");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the agenda tab. After click 'Show all items'");

        // Delete event1
        calendarPage = calendarPage.chooseMonthTab().render();
        calendarPage = calendarPage.deleteEvent(event1, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, CalendarPage.ActionEventVia.MONTH_TAB).render();

        // The event is deleted and is not displayed on all the tabs: Day, Week, Month, Agenda
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");

        calendarPage = calendarPage.chooseDayTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_MULTIPLY_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the day tab");

        calendarPage = calendarPage.chooseWeekTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_MULTIPLY_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the week tab");

        calendarPage = calendarPage.chooseAgendaTab().render();
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_MULTIPLY_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the agenda tab");

        // Go to My Dashboard activities and ensure all activities are displayed
        // User DashBoard Activities
        ShareUser.openUserDashboard(drone);

        // Check activity feed on: User DashBoard: event created
        String activityEntry = testUser + " LName" + FEED_CONTENT_CREATED + FEED_FOR_CALENDAR_EVENT + event1 + FEED_LOCATION + siteName;
        Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity feed on: User DashBoard: that event "
                + event1 + " created is absent");

        // Check activity feed on: User DashBoard: Content Updated
        activityEntry = testUser + " LName" + FEED_CONTENT_CREATED + FEED_FOR_CALENDAR_EVENT + event2 + FEED_LOCATION + siteName;
        Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity feed on: User DashBoard: that event "
                + event2 + " created is absent");

        activityEntry = testUser + " LName" + FEED_CONTENT_UPDATED + FEED_FOR_CALENDAR_EVENT + event1 + FEED_LOCATION + siteName;
        Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity feed on: User DashBoard: that event "
                + event1 + " updated is absent");

        activityEntry = testUser + " LName" + FEED_CONTENT_UPDATED + FEED_FOR_CALENDAR_EVENT + event2 + FEED_LOCATION + siteName;
        Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity feed on: User DashBoard: that event "
                + event2 + " updated is absent");

        activityEntry = testUser + " LName" + FEED_CONTENT_DELETED + FEED_FOR_CALENDAR_EVENT + event1 + FEED_LOCATION + siteName;
        Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity feed on: User DashBoard: that event "
                + event1 + " deleted is absent");

        // Go to Site Dashboard activities and ensure all activities are displayed
        ShareUser.openSiteDashboard(drone, siteName);

        // Check activity feed on: Site DashBoard: event created
        activityEntry = testUser + " LName" + FEED_CONTENT_CREATED + FEED_FOR_CALENDAR_EVENT + event1;
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION),
                "Activity feed on: Site DashBoard: that event " + event1 + " created is absent");

        // Check activity feed on: Site DashBoard: event created
        activityEntry = testUser + " LName" + FEED_CONTENT_CREATED + FEED_FOR_CALENDAR_EVENT + event2;
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION),
                "Activity feed on: Site DashBoard: that event " + event2 + " created is absent");

        // Check activity feed on: Site DashBoard: Content Updated
        activityEntry = testUser + " LName" + FEED_CONTENT_UPDATED + FEED_FOR_CALENDAR_EVENT + event1;
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION),
                "Activity feed on: Site DashBoard: that event " + event1 + " updated is absent");

        // Check activity feed on: Site DashBoard: Content Updated
        activityEntry = testUser + " LName" + FEED_CONTENT_UPDATED + FEED_FOR_CALENDAR_EVENT + event2;
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION),
                "Activity feed on: Site DashBoard: that event " + event2 + " updated is absent");

        // Check activity feed on: Site DashBoard: Content Deleted
        activityEntry = testUser + " LName" + FEED_CONTENT_DELETED + FEED_FOR_CALENDAR_EVENT + event1;
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION),
                "Activity feed on: Site DashBoard: that event " + event1 + " deleted is absent");

    }
}
