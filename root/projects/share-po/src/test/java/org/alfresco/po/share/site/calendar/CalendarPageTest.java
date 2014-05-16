package org.alfresco.po.share.site.calendar;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class CalendarPageTest extends AbstractSiteDashletTest
{

    private Log logger = LogFactory.getLog(this.getClass());

    protected String siteName = "calendar" + System.currentTimeMillis();
    protected String folderName;
    protected String folderDescription;
    protected String userName = "user" + System.currentTimeMillis() + "@test.com";
    protected String firstName = userName;
    protected String event1 = "single_day_event1";
    protected String tag1 = "tag1";

    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    CalendarPage calendarPage = null;

    @BeforeClass(groups = "Enterprise-only")
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    protected void navigateToSiteDashboard()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("navigate to " + shareUrl);
        }
        drone.navigateTo(shareUrl);
        DashBoardPage boardPage = drone.getCurrentPage().render();
        SiteFinderPage finderPage = boardPage.getNav().selectSearchForSites().render();
        finderPage = finderPage.searchForSite(siteName).render();
        finderPage = siteSearchRetry(finderPage, siteName);
        siteDashBoard = finderPage.selectSite(siteName).render();
    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test(groups = "Enterprise-only")
    public void addCalendarPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        List<SitePageType> addPageTypes = new ArrayList<>();
        addPageTypes.add(SitePageType.CALENDER);
        customizeSitePage.addPages(addPageTypes);
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();
        assertNotNull(calendarPage, "Calendar page isn't opened");
    }

    /**
     * Method for event creation
     * 
     * @return CalendarPage
     */
    @Test(groups = "Enterprise-only", dependsOnMethods = "addCalendarPage")
    public void testCreateEvent()
    {
        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event1, event1, event1, null, null, null, null, tag1, false);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testCreateEvent")
    public void testIsEventPresent()
    {
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testIsEventPresent")
    public void testChooseDayTab()
    {
        calendarPage = calendarPage.chooseDayTab().render();
        assertNotNull(calendarPage, "Calendar page day tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the day tab");
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testChooseDayTab")
    public void testChooseWeekTab()
    {
        calendarPage = calendarPage.chooseWeekTab().render();
        assertNotNull(calendarPage, "Calendar page week tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the week tab");
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testChooseWeekTab")
    public void testChooseMonthTab()
    {
        calendarPage = calendarPage.chooseMonthTab().render();
        assertNotNull(calendarPage, "Calendar page month tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testChooseMonthTab")
    public void testChooseAgendaTab()
    {
        calendarPage = calendarPage.chooseAgendaTab().render();
        assertNotNull(calendarPage, "Calendar page agenda tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the agenda tab");
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testChooseAgendaTab")
    public void testShowWorkingHours()
    {
        calendarPage = calendarPage.chooseWeekTab().render();
        assertNotNull(calendarPage, "Calendar page week tab isn't opened");

        calendarPage = calendarPage.showWorkingHours().render();
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.FIRST_WORKING_HOUR), "Not only working hours (7.00 - 18.00) are shown");
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.LAST_WORKING_HOUR), "Not only working hours (7.00 - 18.00) are shown");
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testShowWorkingHours")
    public void testShowAllHours()
    {
        calendarPage = calendarPage.showAllHours().render();
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.FIRST_ALL_HOUR), "Not all hours are shown");
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.LAST_ALL_HOUR), "Not all hours are shown");
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testShowAllHours")
    public void testIsShowAllItemsPresent()
    {
        Assert.assertTrue(calendarPage.isShowAllItemsPresent(), "Link 'Show All items' isn't displayed");
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testIsShowAllItemsPresent")
    public void testIsTagPresent()
    {
        Assert.assertTrue(calendarPage.isTagPresent(tag1), "Tag Link '" + tag1 + "' isn't displayed");
    }

}
