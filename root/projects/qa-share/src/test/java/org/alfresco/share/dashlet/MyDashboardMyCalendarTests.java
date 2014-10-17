/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.dashlet;

import org.alfresco.po.share.dashlet.MyCalendarDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.testng.Assert.*;

/**
 * @author jcule
 */
@Listeners(FailedTestListener.class)
public class MyDashboardMyCalendarTests extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(MyDashboardMyCalendarTests.class);

    private static final String EMPTY_DASHLET_MESSAGE = "No upcoming events";
    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows the upcoming events in sites you belong to.";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);

    }

    /**
     * My Calendar dashlet (no events created)
     * 1) Login as created user
     * 2) Create a site
     * 3) Add My Calendar dashlet to the User Dashboard
     * 4) Open My Calendar dashlet
     * 5) Verify "No upcoming events" message is displayed
     * 6) Verify ? icon is present and click on the icon
     * 7) Verify baloon popup with Calendar dashlet is displayed
     * 8) This dashlet shows the upcoming events in sites you belong to. baloon pop-up is displayed
     * 9) Click X icon on baloon popup and check popup is hidden
     * 
     * @throws Exception
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_2931() throws Exception
    {
        logger.info("Starting Test: " + getTestName());
        // create user
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.addDashlet(drone, Dashlets.MY_CALENDAR);

        MyCalendarDashlet myCalendarDashlet = ShareUserDashboard.getMyCalendarDashlet(drone);

        // verify empty dashlet message - "No upcoming events" is displayed
        String emptyDashletMessage = myCalendarDashlet.getEmptyDashletMessage();
        Assert.assertEquals(emptyDashletMessage, EMPTY_DASHLET_MESSAGE, "Expected message '" + EMPTY_DASHLET_MESSAGE + "' isn't displayed");

        // Click on ? icon
        myCalendarDashlet.clickOnHelpIcon();

        // Verify baloon popup with Calendar dashlet is displayed
        assertTrue(myCalendarDashlet.isBalloonDisplayed(), "'Expected balloon isn't displayed");
        String actualHelpBalloonMsg = myCalendarDashlet.getHelpBalloonMessage();

        // This dashlet shows the upcoming events in sites you belong to. baloon pop-up is displayed
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG, "Message '" + EXP_HELP_BALLOON_MSG + "' isn't displayed");
        myCalendarDashlet.closeHelpBallon();
        assertFalse(myCalendarDashlet.isBalloonDisplayed(), "'Expected balloon isn't hidden");

        // logout
        ShareUser.logout(drone);
        logger.info("End Test: " + getTestName());
    }

    /**
     * My Calendar dashlet (some events created)
     * 1) Login as created user
     * 2) Create a site
     * 3) Any site is created by user
     * 4) Any event created by user for created site
     * 5) My Calendar dashlet is added to My Dashboard
     * 6) Verify list of events
     * 7) Verify information for event
     * 
     * @throws Exception
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_2932() throws Exception
    {
        logger.info("Starting Test: " + getTestName());
        // create user
        String testName = getTestName();
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String event1 = "single_day_event1_" + getRandomString(5);

        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // example:'30 September, 2014'
        DateFormat sdfDayFormat = new SimpleDateFormat("d MMMM, Y");
        Date currentDate = new Date();
        String date = sdfDayFormat.format(currentDate);
        String eventDate = date + " 12:00 PM - 1:00 PM";

        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.addDashlet(drone, Dashlets.MY_CALENDAR);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);
        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event1, event1, event1, null, null, null, null, null, false);

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");

        ShareUser.openUserDashboard(drone);

        MyCalendarDashlet myCalendarDashlet = ShareUserDashboard.getMyCalendarDashlet(drone);

        // Verify list of events
        assertEquals(myCalendarDashlet.getEventsCount(), 1, "Expected event isn't displayed");
        // Verify information for event
        Assert.assertTrue(myCalendarDashlet.isEventDisplayed(event1, eventDate, siteName), event1 + " isn't found or information for event isn't correct");

        // logout
        ShareUser.logout(drone);

        logger.info("End Test: " + getTestName());
    }

    /**
     * My Calendar dashlet. Available actions
     * 1) Login as created user
     * 2) Create a site
     * 3) Any site is created by user
     * 4) Any event created by user for created site
     * 5) My Calendar dashlet is added to My Dashboard
     * 6) Verify list of available events
     * 7) Click at the event's name link
     * 8) Navigate to My Dashboard page
     * 9) Click site link on which event was created
     * 10) Site Dashboard page for selected site is opened
     * 
     * @throws Exception
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_2933() throws Exception
    {
        logger.info("Starting Test: " + getTestName());
        // create user
        String testName = getTestName();
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String event1 = "single_day_event1_" + getRandomString(5);

        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // example:'30 September, 2014'
        DateFormat sdfDayFormat = new SimpleDateFormat("d MMMM, Y");
        Date currentDate = new Date();
        String date = sdfDayFormat.format(currentDate);
        String eventDate = date + " 12:00 PM - 1:00 PM";

        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.addDashlet(drone, Dashlets.MY_CALENDAR);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);
        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event1, event1, event1, null, null, null, null, null, false);

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");

        ShareUser.openUserDashboard(drone);

        MyCalendarDashlet myCalendarDashlet = ShareUserDashboard.getMyCalendarDashlet(drone);

        // Verify list of events
        assertEquals(myCalendarDashlet.getEventsCount(), 1, "Expected event isn't displayed");
        // Verify information for event
        Assert.assertTrue(myCalendarDashlet.isEventDisplayed(event1, eventDate, siteName), event1 + " isn't found or information for event isn't correct");

        // Click at the event's name link
        calendarPage = myCalendarDashlet.clickEvent(event1);

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");

        // Navigate to My Dashboard page
        ShareUser.openUserDashboard(drone);
        myCalendarDashlet = ShareUserDashboard.getMyCalendarDashlet(drone);

        // Click site link on which event was created
        SiteDashboardPage siteDash = myCalendarDashlet.clickEventSiteName(event1, siteName);

        // Site Dashboard page for selected site is opened
        Assert.assertTrue(siteDash.isSiteTitle(siteName), "Site Dashboard page for selected site " + siteName + " isn't opened");

        // logout
        ShareUser.logout(drone);

        logger.info("End Test: " + getTestName());
    }

    /**
     * My Calendar dashlet. Different sites
     * 1) Login as created user
     * 2) The user is member of 2 sites
     * 3) first site is created by him
     * 4) the second site is created by other user
     * 5) For at least 1 event is created by the user in each site
     * 6) My Calendar dashlet is added to My Dashboard
     * 7) Verify listed events on the dashlet
     * 8) All created by user events are displayed on dashlet
     * 
     * @throws Exception
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_2934() throws Exception
    {
        logger.info("Starting Test: " + getTestName());
        // create user
        String testName = getTestName();
        String event1 = "single_day_event1_" + getRandomString(5);
        String event2 = "single_day_event2_" + getRandomString(5);

        // example:'30 September, 2014'
        DateFormat sdfDayFormat = new SimpleDateFormat("d MMMM, Y");
        Date currentDate = new Date();
        String date = sdfDayFormat.format(currentDate);
        String eventDate = date + " 12:00 PM - 1:00 PM";
        String siteName1 = getSiteName(testName + "-1" + System.currentTimeMillis());
        String siteName2 = getSiteName(testName + "-2" + System.currentTimeMillis());

        String testUser1 = getUserNameFreeDomain(testName + "-1" + System.currentTimeMillis());
        String testUser2 = getUserNameFreeDomain(testName + "-2" + System.currentTimeMillis());

        // Create users
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        // Login through created user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to User DashBoard
        ShareUser.openUserDashboard(drone);

        // Create a different new site.
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC).render();
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.COLLABORATOR);

        ShareUser.openSiteDashboard(drone, siteName1);

        ShareUserDashboard.addPageToSite(drone, siteName1, SitePageType.CALENDER);

        // login as created user
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        ShareUserDashboard.addDashlet(drone, Dashlets.MY_CALENDAR);

        ShareUser.openSiteDashboard(drone, siteName1);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName1);
        CalendarPage calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event1, event1, event1, null, null, null, null, null, false);

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");

        ShareUser.openUserDashboard(drone);

        // Any site is created
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSiteDashboard(drone, siteName2);

        ShareUserDashboard.addPageToSite(drone, siteName2, SitePageType.CALENDER);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName2);
        calendarPage = siteDashPage.getSiteNav().selectCalendarPage();

        // Create any single day event, e.g. event2
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event2, event2, event2, null, null, null, null, null, false);

        calendarPage = calendarPage.chooseMonthTab().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event2), "The " + event2
                + " isn't correctly displayed on the month tab");

        ShareUser.openUserDashboard(drone);

        MyCalendarDashlet myCalendarDashlet = ShareUserDashboard.getMyCalendarDashlet(drone);

        // All created by user events are displayed on dashlet (from his site and from site he is member of)
        // Verify list of events
        assertEquals(myCalendarDashlet.getEventsCount(), 2, "Expected event isn't displayed");
        // Verify information for event
        Assert.assertTrue(myCalendarDashlet.isEventDisplayed(event2, eventDate, siteName2), event2 + " isn't found or information for event isn't correct");
        Assert.assertTrue(myCalendarDashlet.isEventDisplayed(event1, eventDate, siteName1), event1 + " isn't found or information for event isn't correct");

        // logout
        ShareUser.logout(drone);

        logger.info("End Test: " + getTestName());
    }

}
