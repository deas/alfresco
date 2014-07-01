package org.alfresco.po.share.site.calendar;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;

/**
 * Holds tests for Calendar page web elements
 *
 * @author Sergey Kardash
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" }, enabled=false)
public class CalendarPageTest  extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    CalendarPage calendarPage = null;
    protected String folderName;
    protected String folderDescription;
    protected String event1 = "single_day_event1";
    protected String tag1 = "tag1";
    String edit_event1_what = "single_day_event1_edit_what";
    String edit_event1_where = "single_day_event1_edit_where";
    String edit_event1_description = "single_day_event1_edit_description";

    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "calendar" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void addCalendarPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.CALENDER);
        customizeSitePage.addPages(addPageTypes);
        try {
            saveScreenShot("addCalendarPage_after_add_pages_screen");
            savePageSource("addCalendarPage_after_add_pages_page_source");
        } catch (IOException e) {
            e.printStackTrace();
        }
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();
        assertNotNull(calendarPage);
    }

    /**
     * Method for event creation
     *
     * @return CalendarPage
     */
    @Test(dependsOnMethods = "addCalendarPage", timeOut = 60000)
    public void testCreateEvent()
    {
        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event1, event1, event1, null, null, null, null, tag1, false);
    }

    @Test(dependsOnMethods = "testCreateEvent", timeOut = 60000)
    public void testIsEventPresent()
    {
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
    }

    @Test(dependsOnMethods = "testIsEventPresent", timeOut = 60000)
    public void testChooseDayTab()
    {
        calendarPage = calendarPage.chooseDayTab().render();
        assertNotNull(calendarPage, "Calendar page day tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the day tab");
    }

    @Test(dependsOnMethods = "testChooseDayTab", timeOut = 60000)
    public void testChooseWeekTab()
    {
        calendarPage = calendarPage.chooseWeekTab().render();
        assertNotNull(calendarPage, "Calendar page week tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the week tab");
    }

    @Test(dependsOnMethods = "testChooseWeekTab", timeOut = 60000)
    public void testChooseMonthTab()
    {
        calendarPage = calendarPage.chooseMonthTab().render();
        assertNotNull(calendarPage, "Calendar page month tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
    }

    @Test(dependsOnMethods = "testChooseMonthTab", timeOut = 60000)
    public void testChooseAgendaTab()
    {
        calendarPage = calendarPage.chooseAgendaTab().render();
        assertNotNull(calendarPage, "Calendar page agenda tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the agenda tab");
    }

    @Test(dependsOnMethods = "testChooseAgendaTab", timeOut = 60000)
    public void testShowWorkingHours()
    {
        calendarPage = calendarPage.chooseWeekTab().render();
        assertNotNull(calendarPage, "Calendar page week tab isn't opened");

        calendarPage = calendarPage.showWorkingHours().render();
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.FIRST_WORKING_HOUR), "Not only working hours (7.00 - 18.00) are shown");
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.LAST_WORKING_HOUR), "Not only working hours (7.00 - 18.00) are shown");
    }

    @Test(dependsOnMethods = "testShowWorkingHours", timeOut = 60000)
    public void testShowAllHours()
    {
        calendarPage = calendarPage.showAllHours().render();
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.FIRST_ALL_HOUR), "Not all hours are shown");
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.LAST_ALL_HOUR), "Not all hours are shown");
    }

    @Test(dependsOnMethods = "testShowAllHours", timeOut = 60000)
    public void testIsShowAllItemsPresent()
    {
        Assert.assertTrue(calendarPage.isShowAllItemsPresent(), "Link 'Show All items' isn't displayed");
    }

    @Test(dependsOnMethods = "testIsShowAllItemsPresent", timeOut = 60000)
    public void testIsTagPresent()
    {
        Assert.assertTrue(calendarPage.isTagPresent(tag1), "Tag Link '" + tag1 + "' isn't displayed");
    }

    @Test(dependsOnMethods = "testIsTagPresent", timeOut = 60000)
    public void testEditEvent()
    {
        calendarPage = calendarPage.chooseMonthTab().render();
        assertNotNull(calendarPage, "Calendar page month tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
        calendarPage = calendarPage.editEvent(event1, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, CalendarPage.ActionEventVia.MONTH_TAB, edit_event1_what,
                edit_event1_where, edit_event1_description, null, null, null, null, null, false, null).render();
    }

    @Test(dependsOnMethods = "testEditEvent", timeOut = 60000)
    public void testCheckInformationEventForm()
    {
        InformationEventForm informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, edit_event1_what).render();
        Assert.assertTrue(informationEventForm.getWhatDetail().contains(edit_event1_what), "The " + edit_event1_what
                + " isn't correctly displayed on the information form what field. Server B");
        Assert.assertTrue(informationEventForm.getWhereDetail().contains(edit_event1_where), "The " + edit_event1_where
                + " isn't correctly displayed on the information form where field. Server B");
        Assert.assertTrue(informationEventForm.getDescriptionDetail().contains(edit_event1_description), "The " + edit_event1_description
                + " isn't correctly displayed on the information form description field. Server B");
        calendarPage = informationEventForm.closeInformationForm();
        assertNotNull(calendarPage, "Calendar page month tab isn't opened");
    }

}