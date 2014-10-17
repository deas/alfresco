package org.alfresco.share.sharepoint.office2010;

import java.util.Date;
import java.util.List;





import java.util.Properties;

import org.alfresco.application.util.Application;
import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.MyCalendarDashlet;
import org.alfresco.po.share.dashlet.SiteCalendarDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.CalendarPage.ActionEventVia;
import org.alfresco.po.share.site.calendar.InformationEventForm;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.windows.application.MicorsoftOffice2010;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

/**
 * @author bogdan.bocancea
 */

@Listeners(FailedTestListener.class)
public class MSOutlook2010EventsTests extends AbstractUtils
{
    MicorsoftOffice2010 outlook = new MicorsoftOffice2010(Application.OUTLOOK, "2010");
    
    private String next_site;
    private CustomiseUserDashboardPage customizeUserDash;
    private DashBoardPage dashBoard;
    private String sharePointPath;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        
        Runtime.getRuntime().exec("taskkill /F /IM OUTLOOK.EXE");
        
        sharePointPath = outlook.getSharePointPath();
    }

    @AfterMethod(alwaysRun = true)
    public void teardownMethod() throws Exception
    {
        Runtime.getRuntime().exec("taskkill /F /IM OUTLOOK.EXE");
        Runtime.getRuntime().exec("taskkill /F /IM CobraWinLDTP.EXE");
    }

    /**
     * AONE-9704:Event info window
     */
    @Test(groups = "alfresco-one")
    public void AONE_9704() throws Exception
    {

        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // create new meeting workspace
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // navigate to site
        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName));

        // ---- Step 1 ----
        // ---- Step Action -----
        // Click the event's name link;
        // Expected Result
        // Event Information window is opened;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName).render();

        // ---- Step 2 ----
        // ---- Step Action -----
        // Verify event's information;
        // Expected Result
        // There are next fields are present:
        /**
         * Details part
         * - What field (marked as mandatory);
         * - Where text field;
         * - Description test field;
         * - Tags associated with the document;
         * Time part:
         * - Start Date ;
         * - End Date;
         * - Recurrence;
         */
        Assert.assertEquals(eventInfo.getWhatDetail(), siteName);
        Assert.assertEquals(eventInfo.getWhereDetail(), location);
        Assert.assertTrue(eventInfo.getDescriptionDetail().isEmpty());
        Assert.assertEquals(eventInfo.getTagName(), "(None)");
        Assert.assertFalse(eventInfo.getStartDateTime().isEmpty());
        Assert.assertFalse(eventInfo.getEndDateTime().isEmpty());

    }

    /**
     * AONE-9705:Deleting event
     */
    @Test(groups = "alfresco-one")
    public void AONE_9705() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName_meeting = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);

        // ---- Pre-conditions: -----
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        Ldtp security = new Ldtp("Windows Security");

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        outlook.getAbstractUtil().clickOnObject(l, "btnMeeting");

        Ldtp l2 = outlook.getAbstractUtil().setOnWindow("Untitled");

        outlook.getAbstractUtil().clickOnObject(l2, "btnMeetingWorkspace");
        outlook.getAbstractUtil().clickOnObject(l2, "hlnkChangesettings");

        l2.selectItem("cboWebsiteDropdown", "Other...");

        Ldtp l3 = outlook.getAbstractUtil().setOnWindow("Other Workspace Server");

        l3.deleteText("txtServerTextbox", 0);
        l3.enterString("txtServerTextbox", sharePointPath);
        l3.click("btnOK");

        outlook.operateOnSecurity(security, testUser, DEFAULT_PASSWORD);

        l2.click("chkAlldayevent");

        Ldtp l4 = outlook.getAbstractUtil().setOnWindow("Untitled");
        l4.click("btnOK");
        l4.enterString("txtLocation", location);
        l4.enterString("txtSubject", siteName_meeting);

        // Click "create" button;
        l4.click("btnCreate");

        outlook.operateOnSecurity(security, testUser, DEFAULT_PASSWORD);

        outlook.operateOnSecurity(security, testUser, DEFAULT_PASSWORD);

        // login to share
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // navigate to site
        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName_meeting);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName_meeting));

        // ---- Step 1 ----
        // ---- Step Action -----
        // Click the event's name;
        // ---- Expected result ----
        // Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName_meeting).render();

        // ---- Step 2 -----
        // ---- Step Action -----
        // Click Delete button;
        // ---- Expected result ----
        // Delete button is disabled; Event is not deleted;
        Assert.assertFalse(eventInfo.isDeleteButtonEnabled());
        eventInfo.clickClose();

        // ---- Step 3 -----
        // ---- Step Action -----
        // Delete Appointment via Outlook;
        // ---- Expected result ----
        // Appointment is deleted successfully;
        Ldtp remove = outlook.getAbstractUtil().setOnWindow(siteName_meeting);
        remove.doubleClick("btnRemove");
        Ldtp l_error = outlook.getAbstractUtil().setOnWindow("Microsoft Outlook");
        l_error.click("btnYes");

        // login to share
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // ---- Step 4 -----
        // ---- Step Action -----
        // Open Calendar in Share client for the workspace;
        // ---- Expected result ----
        // Calendar is opened in Share;
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName_meeting);
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();
        calendarPage.chooseMonthTab().render();

        // ---- Step 5 -----
        // ---- Step Action -----
        // Verify the presence of recently deleted event ;
        // ---- Expected result ----
        // Event is absent in Calendar;
        Assert.assertFalse(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName_meeting));
    }

    /**
     * AONE-9706:Editing event via Outlook. Send updates
     */
    @Test(groups = "alfresco-one")
    public void AONE_9706() throws Exception
    {

        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l3 = outlook.openOfficeApplication();


        // create new meeting workspace
        outlook.operateOnCreateNewMeetingWorkspace(l3, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        // login to share
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // navigate to site
        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName));

        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName).render();
        String end_date = eventInfo.getEndDateTime();
        eventInfo.closeInformationForm();

        // ---- Step 1 -----
        // ---- Step Action -----
        // Expand\collapse event's duration;
        // ---- Expected result ----
        // Event's duration and time boundaries are changed; New value is set; Microsoft Outlook window pops up; It notifies about time of the meeteing has
        // changed and and offers sending updates or no.
        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.enterString("txtTo", testUser);

        // add 1 day to current date
        SimpleDateFormat FormattedDATE = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        String tommorrow = (String) (FormattedDATE.format(c.getTime()));

        l1.deleteText("txtEnddate", 0);
        l1.enterString("txtEnddate", tommorrow);

        outlook.exitOfficeApplication(l1);

        // ---- Step 2,3,4 -----
        // ---- Step Action -----
        // Select Save Changes and Send update radio button and click OK button;
        // ---- Expected result ----
        // Save Changes and Save update radio button is selected; Microsoft Outlook windows closed;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow("Microsoft Outlook");
        l2.click("rbtnSavechangesandsendmeeting");
        l2.click("btnOK");

        // ---- Step 5, 6 -----
        // ---- Step Action -----
        // Log in Share as any user;Open Calendar tab for the workspace;
        // ---- Expected result ----
        // Calendar tab is opened for the workspace;
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();

        // ---- Step 7 -----
        // ---- Step Action ----
        // Click the event's name and verify time and name were changed;
        // ---- Expected result ----
        // Event Info window is displayed; Event's details have changed;
        eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName).render();
        String updated_endDate = eventInfo.getEndDateTime();
        Assert.assertNotEquals(updated_endDate, end_date);
    }

    /**
     * AONE-9707:Editing event to recurrence via Outlook
     */
    @Test(groups = "alfresco-one")
    public void AONE_9707() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // create new meeting workspace
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);
        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // login to share
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // navigate to site
        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName));

        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName).render();
        Assert.assertFalse(eventInfo.isRecurrencePresent());
        eventInfo.closeInformationForm();

        calendarPage.chooseAgendaTab().render(maxWaitTime);
        int event_number = calendarPage.getTheNumOfEvents(ActionEventVia.AGENDA_TAB);
        Assert.assertEquals(1, event_number);

        // ---- Step 1 -----
        // ---- Step Action ----
        // Click Recurrence button in Ms Outlook;
        // ---- Expected result ----
        // Appointment recurrence window is opened;
        Ldtp site = outlook.getAbstractUtil().setOnWindow(siteName);
        site.doubleClick("btnRecurrence");

        // ---- Step 2 -----
        // ---- Step Action ----
        // Select any start and end time (or set any duration);
        // ---- Expected result ----
        // Time and duration are set;
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Set any recurrence pattern;
        // ---- Expected result ----
        // Recurrence pattern is set;
        recurrence.click("rbtnDaily");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Set any range of recurrence;
        // ---- Expected result ----
        // Range of recurrence is set;
        recurrence.click("rbtnEndafter");
        recurrence.click("btnOK");

        // ---- Step 5 -----
        // ---- Step Action ----
        // // Select Save Changes (that is not matter with Send updates or no) and click OK button;
        // ---- Expected result ----
        // Changes are saved in Outlook;
        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 6, 7 -----
        // ---- Step Action ----
        // Log in Share;
        // Verify the created event is edited successfully;
        // ---- Expected result ----
        // My Calendar dashlet event is marked as recurring, recurring events are also displayed in calendar tab of the meeting workspace with correct start
        // time and duration, recurrence pattern and the range of recurrence;
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();
        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        String eventName = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventName.contains("Repeating"));

        // open site dashboard
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // navigate to Calendar
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.isRecurrencePresent());
        eventInfo.closeInformationForm();

        // recurring events are also displayed in calendar tab
        calendarPage.chooseAgendaTab().render(maxWaitTime);
        int event_reccurence = calendarPage.getTheNumOfEvents(ActionEventVia.AGENDA_TAB);
        Assert.assertEquals(10, event_reccurence);

    }

    /**
     * AONE-9708:Editing recurrence event via Outlook
     */
    @Test(groups = "alfresco-one")
    public void AONE_9708() throws Exception
    {

        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // ---- Pre-conditions: ----
        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // create new meeting workspace
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);
        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");
        recurrence.click("rbtnDaily");
        recurrence.click("rbtnEndafter");
        recurrence.deleteText("txtEndafterEditableTextoccurences", 0);
        recurrence.enterString("txtEndafterEditableTextoccurences", "10");
        recurrence.click("btnOK");

        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName));
        calendarPage.chooseAgendaTab().render(maxWaitTime);
        int event_number = calendarPage.getTheNumOfEvents(ActionEventVia.AGENDA_TAB);
        Assert.assertEquals(10, event_number);

        // ---- Step 1 -----
        // ---- Step Action ----
        // Click Recurrence button in Ms Outlook;
        // ---- Expected result ----
        // Appointment recurrence window is opened;
        Ldtp site = outlook.getAbstractUtil().setOnWindow(siteName);
        site.doubleClick("btnRecurrence");

        // ---- Step 2 -----
        // ---- Step Action ----
        // Select another start and end time ;
        // ---- Expected result ----
        // Time and duration are set;
        recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "6 hours");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Set any recurrence pattern;
        // ---- Expected result ----
        // Recurrence pattern is set;
        recurrence.click("rbtnDaily");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Set another range of recurrence;
        // ---- Expected result ----
        // Range of recurrence is set;
        recurrence.click("rbtnEndafter");
        recurrence.deleteText("txtEndafterEditableTextoccurences", 0);
        recurrence.enterString("txtEndafterEditableTextoccurences", "15");
        recurrence.click("btnOK");

        // ---- Step 5 -----
        // ---- Step Action ----
        // Select Save Changes (that is not matter with Send updates or no) and click OK button;
        // ---- Expected result ----
        // Changes are saved in Outlook;
        after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 6, 7 -----
        // ---- Step Action ----
        // Verify the event is edited successfully;
        // ---- Expected result ----
        // My Calendar dashlet event is marked as recurring, recurring events are also displayed in calendar tab of the meeting workspace with chached start
        // time and duration, recurrence pattern and the range of recurrence;
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();
        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        String eventName = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventName.contains("Repeating"));

        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        calendarPage.chooseAgendaTab().render();
        int event_reccurence = calendarPage.getTheNumOfEvents(ActionEventVia.AGENDA_TAB);
        Assert.assertEquals(event_reccurence, 15);

        calendarPage.chooseMonthTab().render();
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.isRecurrencePresent());
        eventInfo.closeInformationForm();

    }

    /**
     * AONE-9709:Editing event via Outlook. Don't save changes
     */
    @Test(groups = "alfresco-one")
    public void AONE_9709() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // ---- Pre-conditions: ----
        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();


        // create new meeting workspace
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        // login to share
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // navigate to site
        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName));

        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName).render();
        String end_date = eventInfo.getEndDateTime();
        eventInfo.closeInformationForm();

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.enterString("txtTo", testUser);

        // ---- Step 1 -----
        // ---- Step Action ----
        // Expand\collapse event's duration;
        // ---- Expected result ----
        // Event's duration and time boundaries are changed; New value is set; Microsoft Outlook window pops up; It notifies about time of the meeteing has
        // changed and and offers sending updates or no.
        SimpleDateFormat FormattedDATE = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        String tommorrow = (String) (FormattedDATE.format(c.getTime()));
        l1.click("btnSave");
        l1.deleteText("txtEnddate", 0);
        l1.enterString("txtEnddate", tommorrow);

        // ---- Step 2, 3, 4 -----
        // ---- Step Action ----
        // Select Dont't save changes radio button and click OK button;
        // ---- Expected result ----
        // Dont't save changes radio button is selected; Microsoft Outlook window is closed;
        outlook.exitOfficeApplication(l1);
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow("Microsoft Outlook");
        l2.click("rbtnDon'tsavechanges");
        l2.click("btnOK");

        // ---- Step 5, 6 -----
        // ---- Step Action ----
        // Log in Share as any user;
        // Open Calendar tab for the workspace;
        // ---- Expected result ----
        // Calendar tab is opened for the workspace;
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();

        // ---- Step 7 -----
        // ---- Step Action ----
        // Log in Share as any user;
        // Open Calendar tab for the workspace;
        // ---- Expected result ----
        // Event Info window is displayed; Event's details have not changed;
        eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName).render();
        String updated_endDate = eventInfo.getEndDateTime();
        Assert.assertEquals(updated_endDate, end_date);

    }

    /**
     * AONE-9710:Verify the events are founds without errors
     */
    @Test(groups = "alfresco-one")
    public void AONE_9710() throws Exception
    {

        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // create new meeting workspace
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 -----
        // ---- Step Action ----
        // Enter a name of the event created via MS Outlook;
        // ---- Expected result ----
        // Query is entered;
        SiteFinderPage siteFinderPage = SiteUtil.searchSiteWithRetry(drone, siteName, true);

        // ---- Step 2 -----
        // ---- Step Action ----
        // Click Search button or press Enter key;
        // ---- Expected result ----
        // Event is found and displayed at the Search result page without any errors;
        List<String> theSite = siteFinderPage.getSiteList();
        Assert.assertTrue(theSite.contains(siteName));
    }

    /**
     * AONE-9711:All day events created in Outlook are appearing in Calendar
     */
    @Test(groups = "alfresco-one")
    public void AONE_9711() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();


        // create new meeting workspace
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        // login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // open site dashboard
        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // All day event is displayed in Calendar;
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName));
    }

    /**
     * AONE-9712:Creating a recurrence event. Times and duration.
     */
    @Test(groups = "alfresco-one")
    public void AONE_9712() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // / ---- Step 1, 2 -----
        // ---- Step Action ----
        // 1. Click on "New Appointment" link in "New" drop-down menu;
        // 2. Fill in Subject and location;
        // ---- Expected result ----
        // 1. Appointment form is opened;
        // 2. Information is entered successfully;
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Click Recurrence button;
        // ---- Expected result ----
        // Appointment recurrence form is opened;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Select any Start time and End time (or set any duration);
        // ---- Expected result ----
        // Time and duration are set;
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "6 hours");
        recurrence.click("rbtnDaily");

        String startTime = convertHour(recurrence.getTextValue("txtStart"));
        String endTime = convertHour(recurrence.getTextValue("txtEnd"));

        // select OK
        recurrence.click("btnOK");

        // Select Save Changes
        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 9 -----
        // ---- Step Action ----
        // Go to Alfresco Share;
        // ---- Expected result ----
        // Alfresco Share is opened;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // ---- Step 10 -----
        // ---- Step Action ----
        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        // ---- Expected result ----
        // New Appointment is created and displayed in the calendar of selected Meeting Workspace;
        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName));

        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.getStartDateTime().contains(startTime));
        Assert.assertTrue(eventInfo.getEndDateTime().contains(endTime));
        eventInfo.closeInformationForm();

        // ---- Step 11 -----
        // ---- Step Action ----
        // Verify start and end date, duration of the event ar My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place;
        // ---- Expected result ----
        // The start and end date, duration of the event ar My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place are correctly
        // displayed;
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // The created appointment is displayed with correct date on the dashlet;
        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName));

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();
        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        String theTime = startTime + " - " + endTime;

        // The start and end date, duration of the event at My calendar dashlet
        String eventDetails = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventDetails.contains(theTime));

    }

    /**
     * AONE-9713:Creating a recurrence event. Daily Every () day
     */
    @Test(groups = "alfresco-one")
    public void AONE_9713() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();


        // ---- Step 1, 2 -----
        // ---- Step Action ----
        // 1. Click on "New Appointment" link in "New" drop-down menu;
        // 2. Fill in Subject and location;
        // ---- Expected result ----
        // 1. Appointment form is opened;
        // 2. Information is entered successfully;
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // ---- Step 3 ----
        // ---- Step Action ----
        // Click Recurrence button;
        // ---- Expected result ----
        // Appointment recurrence form is opened;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Select any Start time and End time (or set any duration);
        // ---- Expected result ----
        // Time and duration are set;
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");

        // ---- Step 5 -----
        // ---- Step Action ----
        // In Recurrence pattern set Daily radio button,
        // ---- Expected result ----
        // Any number is entered; Settings are saved;
        recurrence.click("rbtnDaily");
        recurrence.click("rbtnEvery");

        // ---- Step 6 -----
        // ---- Step Action ----
        // Fill Every day(s) with any day number and click ok button;
        // ---- Expected result ----
        // Any number is entered; Settings are saved;
        recurrence.deleteText("txtEveryEditableTextday(s)", 0);
        recurrence.enterString("txtEveryEditableTextday(s)", "5");
        String startTime = convertHour(recurrence.getTextValue("txtStart"));
        String endTime = convertHour(recurrence.getTextValue("txtEnd"));
        recurrence.click("btnOK");

        // Select Save Changes
        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 11 -----
        // ---- Step Action ----
        // Go to Alfresco Share;
        // ---- Expected result ----
        // Alfresco Share is opened;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // ---- Step 12 -----
        // ---- Step Action ----
        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        // ---- Expected result ----
        // New recurrence appointment is created and displayed in the calendar of selected Meeting Workspace;
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName));

        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.getStartDateTime().contains(startTime));
        Assert.assertTrue(eventInfo.getEndDateTime().contains(endTime));

        String recDetail = eventInfo.getRecurrenceDetail();
        Assert.assertTrue(recDetail.contains("Occurs every 5 days effective"));

        eventInfo.closeInformationForm();

        // ---- Step 13 -----
        // ---- Step Action ----
        // Verify start and end date, duration of the event ar My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place;
        // ---- Expected result ----
        // Event at My calendar dashlet, Site calendar dashlet is marked as recurrence; Calendar tab of the meeting place are correctly dispalyed (it occurs
        // every <entered number> days effective start date from start time to end time);
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // Site calendar dashlet is marked as recurrence
        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName));

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();
        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        String theTime = startTime + " - " + endTime;

        // The start and end date, duration of the event at My calendar dashlet
        String eventDetails = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventDetails.contains(theTime));
    }

    /**
     * AONE-9714:Creating a recurrence event. Daily. Every Weekday
     */
    @Test(groups = "alfresco-one")
    public void AONE_9714() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // ---- Step 1, 2 -----
        // ---- Step Action ----
        // 1. Click on "New Appointment" link in "New" drop-down menu;
        // 2. Fill in Subject and location;
        // ---- Expected result ----
        // 1. Appointment form is opened;
        // 2. Information is entered successfully;
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Click Recurrence button;
        // ---- Expected result ----
        // Appointment recurrence form is opened;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Select any Start time and End time (or set any duration);
        // ---- Expected result ----
        // Time and duration are set;
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");

        // ---- Step 5 -----
        // ---- Step Action ----
        // In Recurrence pattern set Daily radio button,
        // ---- Expected result ----
        // Radio button is selected; Settings are saved;
        recurrence.click("rbtnDaily");

        // ---- Step 6 -----
        // ---- Step Action ----
        // Select every weekday and click Ok button;
        // ---- Expected result ----
        // Radio button is selected; Settings are saved;
        recurrence.click("rbtnEveryweekday");

        String startTime = convertHour(recurrence.getTextValue("txtStart"));
        String endTime = convertHour(recurrence.getTextValue("txtEnd"));

        // select OK
        recurrence.click("btnOK");

        // Select Save Changes
        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 11 -----
        // ---- Step Action ----
        // Go to Alfresco Share;
        // ---- Expected result ----
        // Alfresco Share is opened;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // ---- Step 12 -----
        // ---- Step Action ----
        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        // ---- Expected result ----
        // New recurrence appointment is created and displayed in the calendar of selected Meeting Workspace;
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName));

        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.getStartDateTime().contains(startTime));
        Assert.assertTrue(eventInfo.getEndDateTime().contains(endTime));

        String recDetail = eventInfo.getRecurrenceDetail();
        Assert.assertTrue(recDetail.contains("Occurs each week on"));

        eventInfo.closeInformationForm();

        // ---- Step 13 -----
        // ---- Step Action ----
        // Verify start and end date, duration of the event ar My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place;
        // ---- Expected result ----
        // Event at My calendar dashlet, Site calendar dashlet is marked as recurrence; Calendar tab of the meeting place are correctly dispalyed (it occurs
        // every weekday (from Monday till Friday) effective start date from start time to end time);
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // Site calendar dashlet is marked as recurrence
        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName));

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();
        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        String theTime = startTime + " - " + endTime;

        // The start and end date, duration of the event at My calendar dashlet
        String eventDetails = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventDetails.contains(theTime));
    }

    /**
     * AONE-9715: Creating a recurrence event. Weekly. Every () week(s)
     */
    @Test(groups = "alfresco-one")
    public void AONE_9715() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // ---- Step 1, 2 -----
        // ---- Step Action ----
        // 1. Click on "New Appointment" link in "New" drop-down menu;
        // 2. Fill in Subject and location;
        // ---- Expected result ----
        // 1. Appointment form is opened;
        // 2. Information is entered successfully;
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Click Recurrence button;
        // ---- Expected result ----
        // Appointment recurrence form is opened;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Select any Start time and End time (or set any duration);
        // ---- Expected result ----
        // Time and duration are set;
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");

        // ---- Step 5 -----
        // ---- Step Action ----
        // In Recurrence pattern set Weekly radio button,
        // ---- Expected result ----
        // Weekly radio button is selected;
        recurrence.click("rbtnWeekly");

        // ---- Step 6 -----
        // ---- Step Action ----
        // Enter any number into Recur every week(s) on, select any day(s) and click Ok button;
        // ---- Expected result ----
        // Any number is entered, day (s) are selected; Settings are saved;
        recurrence.deleteText("txtRecureveryEditableTextweek(s)", 0);
        recurrence.enterString("txtRecureveryEditableTextweek(s)", "3");
        recurrence.click("chkMonday");
        recurrence.click("chkTuesday");
        recurrence.click("chkWednesday");
        recurrence.click("chkThursday");

        String startTime = convertHour(recurrence.getTextValue("txtStart"));
        String endTime = convertHour(recurrence.getTextValue("txtEnd"));
        recurrence.click("btnOK");
        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 11 -----
        // ---- Step Action ----
        // Go to Alfresco Share;
        // ---- Expected result ----
        // Alfresco Share is opened;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // ---- Step 12 -----
        // ---- Step Action ----
        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        // ---- Expected result ----
        // New recurrence appointment is created and displayed in the calendar of selected Meeting Workspace;
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName));

        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.getStartDateTime().contains(startTime));
        Assert.assertTrue(eventInfo.getEndDateTime().contains(endTime));

        String recDetail = eventInfo.getRecurrenceDetail();
        Assert.assertTrue(recDetail.contains("Occurs every 3 weeks on"));

        eventInfo.closeInformationForm();

        // ---- Step 13 -----
        // ---- Step Action ----
        // Verify the event at My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place;
        // ---- Expected result ----
        // Event at My calendar dashlet, Site calendar dashlet is marked as recurrence; Calendar tab of the meeting place are correctly dispalyed (Occurs every
        // <entered number> weeks on <selected day(s)> effective start date from start time to end time);
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // Site calendar dashlet is marked as recurrence
        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName));

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();
        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        // The start and end date, duration of the event at My calendar dashlet
        String theTime = startTime + " - " + endTime;
        String eventDetails = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventDetails.contains(theTime));

    }

    /**
     * AONE-9716:Creating a recurrence event. Monthly. Every () of () month
     */
    @Test(groups = "alfresco-one")
    public void AONE_9716() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();


        // ---- Step 1, 2 -----
        // ---- Step Action ----
        // 1. Click on "New Appointment" link in "New" drop-down menu;
        // 2. Fill in Subject and location;
        // ---- Expected result ----
        // 1. Appointment form is opened;
        // 2. Information is entered successfully;
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Click Recurrence button;
        // ---- Expected result ----
        // Appointment recurrence form is opened;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Select any Start time and End time (or set any duration);
        // ---- Expected result ----
        // Time and duration are set;
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");

        // ---- Step 5 -----
        // ---- Step Action ----
        // In Recurrence pattern set Monthly radio button
        // ---- Expected result ----
        // Monthly radio button is seleceted;
        recurrence.click("rbtnMonthly");

        // ---- Step 6 -----
        // ---- Step Action ----
        // Select Day radio button, enter any number of day, enter a number of month(s) click Ok button;
        // ---- Expected result ----
        // Any numbers are entered, Day is selected; Settings are saved;
        recurrence.click("rbtnDay");
        recurrence.deleteText("txtDayEditableTextofeveryEditableTextmonth(s)1", 0);
        recurrence.enterString("txtDayEditableTextofeveryEditableTextmonth(s)1", "2");

        String day = recurrence.getTextValue("txtDayEditableTextofeveryEditableTextmonth(s)");
        String month = recurrence.getTextValue("txtDayEditableTextofeveryEditableTextmonth(s)1");
        String startTime = convertHour(recurrence.getTextValue("txtStart"));
        String endTime = convertHour(recurrence.getTextValue("txtEnd"));

        recurrence.click("btnOK");
        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 11 -----
        // ---- Step Action ----
        // Go to Alfresco Share;
        // ---- Expected result ----
        // Alfresco Share is opened;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // ---- Step 12 -----
        // ---- Step Action ----
        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        // ---- Expected result ----
        // New recurrence appointment is created and displayed in the calendar of selected Meeting Workspace;
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName));

        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.getStartDateTime().contains(startTime));
        Assert.assertTrue(eventInfo.getEndDateTime().contains(endTime));

        // Calendar tab of the meeting place are correctly displayed (Occurs every <entered number> weeks on <selected day(s)> effective start date from start
        // time to end time);
        String recDetail = eventInfo.getRecurrenceDetail();
        String compareRecDetail = "Occurs day " + day + " of every " + month + " month(s)";
        Assert.assertTrue(recDetail.contains(compareRecDetail));

        eventInfo.closeInformationForm();

        // ---- Step 13 -----
        // ---- Step Action ----
        // Verify the event at My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place;
        // ---- Expected result ----
        // Event at My calendar dashlet, Site calendar dashlet is marked as recurrence; Calendar tab of the meeting place are correctly dispalyed (Occurs day
        // <number> of every <number> months effective start date from start time to end time);
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // Site calendar dashlet is marked as recurrence
        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName));

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();
        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        String theTime = startTime + " - " + endTime;

        // The start and end date, duration of the event at My calendar dashlet
        String eventDetails = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventDetails.contains(theTime));

    }

    /**
     * AONE-9717: Creating a recurrence event. Monthly
     */
    @Test(groups = "alfresco-one")
    public void AONE_9717() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();


        // ---- Step 1, 2 -----
        // ---- Step Action ----
        // 1. Click on "New Appointment" link in "New" drop-down menu;
        // 2. Fill in Subject and location;
        // ---- Expected result ----
        // 1. Appointment form is opened;
        // 2. Information is entered successfully;
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Click Recurrence button;
        // ---- Expected result ----
        // Appointment recurrence form is opened;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Select any Start time and End time (or set any duration);
        // ---- Expected result ----
        // Time and duration are set;
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");

        // ---- Step 5 -----
        // ---- Step Action ----
        // In Recurrence pattern set Monthly radio button
        // ---- Expected result ----
        // Monthly radio button is selected;
        recurrence.click("rbtnMonthly");

        // ---- Step 6 -----
        // ---- Step Action ----
        // Select The radio button, select values form combo boxes, enter any number of month(s) click Ok button;
        // ---- Expected result ----
        // Any number is entered, some values are selected from combo boxes; Settings are saved;
        recurrence.click("rbtnThe");
        recurrence.selectItem("cboTheEditableTextEditableTextofeveryEditableTextmonth(s)", "last");
        recurrence.selectItem("cboTheEditableTextEditableTextofeveryEditableTextmonth(s)1", "Friday");
        recurrence.deleteText("txtTheEditableTextEditableTextofeveryEditableTextmonth(s)", 0);
        recurrence.enterString("txtTheEditableTextEditableTextofeveryEditableTextmonth(s)", "3");

        String startTime = convertHour(recurrence.getTextValue("txtStart"));
        String endTime = convertHour(recurrence.getTextValue("txtEnd"));
        recurrence.click("btnOK");

        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 11 -----
        // ---- Step Action ----
        // Go to Alfresco Share;
        // ---- Expected result ----
        // Alfresco Share is opened;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // ---- Step 12 -----
        // ---- Step Action ----
        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        // ---- Expected result ----
        // New recurrence appointment is created and displayed in the calendar of selected Meeting Workspace;
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName));

        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.getStartDateTime().contains(startTime));
        Assert.assertTrue(eventInfo.getEndDateTime().contains(endTime));

        // Calendar tab of the meeting place are correctly displayed (Occurs every <entered number> weeks on <selected day(s)> effective start date from start
        // time to end time);
        String recDetail = eventInfo.getRecurrenceDetail();
        String compareRecDetail = "Occurs the last Friday of every 3 month(s)";
        Assert.assertTrue(recDetail.contains(compareRecDetail));

        eventInfo.closeInformationForm();

        // ---- Step 13 -----
        // ---- Step Action ----
        // Verify the event at My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place;
        // ---- Expected result ----
        // Event at My calendar dashlet, Site calendar dashlet is marked as recurrence; Calendar tab of the meeting place are correctly dispalyed (e.g. Occurs
        // the fourth Friday of every 2 months effective 11/26/2010 from 4:30 PM to 5:00 PM);
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // Site calendar dashlet is marked as recurrence
        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName));

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();

        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        String theTime = startTime + " - " + endTime;

        // The start and end date, duration of the event at My calendar dashlet
        String eventDetails = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventDetails.contains(theTime));

    }

    /**
     * AONE-9718:Creating a recurrence event. Yearly. On the month
     */
    @Test(groups = "alfresco-one")
    public void AONE_9718() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();


        // ---- Step 1, 2 -----
        // ---- Step Action ----
        // 1. Click on "New Appointment" link in "New" drop-down menu;
        // 2. Fill in Subject and location;
        // ---- Expected result ----
        // 1. Appointment form is opened;
        // 2. Information is entered successfully;
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Click Recurrence button;
        // ---- Expected result ----
        // Appointment recurrence form is opened;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Select any Start time and End time (or set any duration);
        // ---- Expected result ----
        // Time and duration are set;
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");

        // ---- Step 5 -----
        // ---- Step Action ----
        // In Recurrence pattern set Yearly radio button
        // ---- Expected result ----
        // Yearly radio button is selected;
        recurrence.click("rbtnYearly");

        // ---- Step 6 -----
        // ---- Step Action ----
        // Fill Recur every with any correct value, select On radio button, select any month from combo box, enter any date and click Ok button;
        // ---- Expected result ----
        // Recur every field is filled, On radio button is selected, month is chosen, date is entered; Settings are saved;
        recurrence.deleteText("txtRecurevery", 0);
        recurrence.enterString("txtRecurevery", "3");
        recurrence.click("rbtnOn");
        String day = recurrence.getTextValue("txtEveryEditableTextEditableText");

        String startTime = convertHour(recurrence.getTextValue("txtStart"));
        String endTime = convertHour(recurrence.getTextValue("txtEnd"));
        recurrence.click("btnOK");

        // Select Save Changes
        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 11 -----
        // ---- Step Action ----
        // Go to Alfresco Share;
        // ---- Expected result ----
        // Alfresco Share is opened;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // ---- Step 12 -----
        // ---- Step Action ----
        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        // ---- Expected result ----
        // New recurrence appointment is created and displayed in the calendar of selected Meeting Workspace;
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName));

        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.getStartDateTime().contains(startTime));
        Assert.assertTrue(eventInfo.getEndDateTime().contains(endTime));

        // Calendar tab of the meeting place are correctly displayed (Occurs every <entered number> weeks on <selected day(s)> effective start date from start
        // time to end time);
        String recDetail = eventInfo.getRecurrenceDetail();
        String compareRecDetail = "Occurs day " + day + " of every 36 month(s)";
        Assert.assertTrue(recDetail.contains(compareRecDetail));

        eventInfo.closeInformationForm();

        // ---- Step 13 -----
        // ---- Step Action ----
        // Verify the event at My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place;
        // ---- Expected result ----
        // Event at My calendar dashlet, Site calendar dashlet is marked as recurrence; Calendar tab of the meeting place are correctly dispalyed (it occurs
        // every <entered year frequency> years on <selected month> <selected date> effective <start date> from <start time> to <end time>);
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // Site calendar dashlet is marked as recurrence
        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName));

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();

        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        String theTime = startTime + " - " + endTime;

        // The start and end date, duration of the event at My calendar dashlet
        String eventDetails = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventDetails.contains(theTime));

    }

    /**
     * AONE-9719:Creating a recurrence event. Yearly. Frequency
     */
    @Test(groups = "alfresco-one")
    public void AONE_9719() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();


        // ---- Step 1, 2 -----
        // ---- Step Action ----
        // 1. Click on "New Appointment" link in "New" drop-down menu;
        // 2. Fill in Subject and location;
        // ---- Expected result ----
        // 1. Appointment form is opened;
        // 2. Information is entered successfully;
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Click Recurrence button;
        // ---- Expected result ----
        // Appointment recurrence form is opened;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Select any Start time and End time (or set any duration);
        // ---- Expected result ----
        // Time and duration are set;
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");

        // ---- Step 5 -----
        // ---- Step Action ----
        // In Recurrence pattern set Yearly radio button
        // ---- Expected result ----
        // Yearly radio button is selected;
        recurrence.click("rbtnYearly");

        // ---- Step 6 -----
        // ---- Step Action ----
        // Fill Recur every with any correct value, select On the radio button, select any frequency, select any day from combobox, select any month from combox
        // and click Ok button;
        // ---- Expected result ----
        // Recur every field is filled, On radio button is selected, month is chosen, date is entered; Settings are saved;
        recurrence.deleteText("txtRecurevery", 0);
        recurrence.enterString("txtRecurevery", "3");
        recurrence.click("rbtnOnthe");

        String period = recurrence.getTextValue("cboTheEditableTextEditableTextofEditableText");
        String week_day = recurrence.getTextValue("cboTheEditableTextEditableTextofEditableText1");

        String startTime = convertHour(recurrence.getTextValue("txtStart"));
        String endTime = convertHour(recurrence.getTextValue("txtEnd"));
        recurrence.click("btnOK");

        // Select Save Changes
        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 11 -----
        // ---- Step Action ----
        // Go to Alfresco Share;
        // ---- Expected result ----
        // Alfresco Share is opened;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // ---- Step 12 -----
        // ---- Step Action ----
        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        // ---- Expected result ----
        // New recurrence appointment is created and displayed in the calendar of selected Meeting Workspace;
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName));

        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.getStartDateTime().contains(startTime));
        Assert.assertTrue(eventInfo.getEndDateTime().contains(endTime));

        String recDetail = eventInfo.getRecurrenceDetail();
        String compareRecDetail = "Occurs the " + period + " " + week_day + " of every 36 month(s)";
        Assert.assertTrue(recDetail.contains(compareRecDetail));

        eventInfo.closeInformationForm();

        // ---- Step 13 -----
        // ---- Step Action ----
        // Verify the event at My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place;
        // ---- Expected result ----
        // Event at My calendar dashlet, Site calendar dashlet is marked as recurrence; Calendar tab of the meeting place are correctly displayed (it occurs
        // every <entered year frequency> years on the <selected frequency> <selected day> of <selected month> effective <start date> from <start time> to <end
        // time>);
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // Site calendar dashlet is marked as recurrence
        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName));

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();

        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        String theTime = startTime + " - " + endTime;

        // The start and end date, duration of the event at My calendar dashlet
        String eventDetails = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventDetails.contains(theTime));

    }

    /**
     * AONE-9720:Creating a recurrence event. No end date
     */
    @Test(groups = "alfresco-one")
    public void AONE_9720() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();


        // ---- Step 1, 2 -----
        // ---- Step Action ----
        // 1. Click on "New Appointment" link in "New" drop-down menu;
        // 2. Fill in Subject and location;
        // ---- Expected result ----
        // 1. Appointment form is opened;
        // 2. Information is entered successfully;
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Click Recurrence button;
        // ---- Expected result ----
        // Appointment recurrence form is opened;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Select any Start time and End time (or set any duration);
        // ---- Expected result ----
        // Time and duration are set;
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");

        // ---- Step 5 -----
        // ---- Step Action ----
        // Set any recurrence pattern;
        // ---- Expected result ----
        // Any recurrence pattern is chosen;
        recurrence.click("rbtnDaily");
        recurrence.click("rbtnEveryweekday");

        // ---- Step 6 -----
        // ---- Step Action ----
        // In the Range of recurrence part select any start date and No end date radio button and click Ok button;
        // ---- Expected result ----
        // Start date is set,No end date radio button is selected; Settings are saved;
        recurrence.click("rbtnNoenddate");
        String startTime = convertHour(recurrence.getTextValue("txtStart"));
        String endTime = convertHour(recurrence.getTextValue("txtEnd"));
        recurrence.click("btnOK");

        // Select Save Changes
        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 11 -----
        // ---- Step Action ----
        // Go to Alfresco Share;
        // ---- Expected result ----
        // Alfresco Share is opened;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // ---- Step 12 -----
        // ---- Step Action ----
        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        // ---- Expected result ----
        // New recurrence appointment is created and displayed in the calendar of selected Meeting Workspace;
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName));
        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.getStartDateTime().contains(startTime));
        Assert.assertTrue(eventInfo.getEndDateTime().contains(endTime));

        String recDetail = eventInfo.getRecurrenceDetail();
        String compareRecDetail = "Occurs each week on";
        Assert.assertTrue(recDetail.contains(compareRecDetail));
        eventInfo.closeInformationForm();
        calendarPage.chooseAgendaTab().render();
        int event_reccurence = calendarPage.getTheNumOfEvents(ActionEventVia.AGENDA_TAB);
        Assert.assertTrue(event_reccurence > 15);

        // ---- Step 13 -----
        // ---- Step Action ----
        // Verify the event at My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place;
        // ---- Expected result ----
        // Event at My calendar dashlet, Site calendar dashlet is marked as recurrence; Calendar tab of the meeting place are correctly dispalyed (start date is
        // correct, event repeats with no end date );
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // Site calendar dashlet is marked as recurrence
        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName));

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();

        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        String theTime = startTime + " - " + endTime;

        // The start and end date, duration of the event at My calendar dashlet
        String eventDetails = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventDetails.contains(theTime));

    }

    /**
     * AONE-9721:Creating a recurrence event. End after several occurrences
     */
    @Test(groups = "alfresco-one")
    public void AONE_9721() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();



        // ---- Step 1, 2 -----
        // ---- Step Action ----
        // 1. Click on "New Appointment" link in "New" drop-down menu;
        // 2. Fill in Subject and location;
        // ---- Expected result ----
        // 1. Appointment form is opened;
        // 2. Information is entered successfully;
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Click Recurrence button;
        // ---- Expected result ----
        // Appointment recurrence form is opened;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Select any Start time and End time (or set any duration);
        // ---- Expected result ----
        // Time and duration are set;
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");

        // ---- Step 5 -----
        // ---- Step Action ----
        // Set any recurrence pattern;
        // ---- Expected result ----
        // Any recurrence pattern is chosen;
        recurrence.click("rbtnDaily");
        recurrence.click("rbtnEveryweekday");

        // ---- Step 6 -----
        // ---- Step Action ----
        // In the Range of recurrence part select any start date, select End after, enter any value of occurrences and click Ok button;
        // ---- Expected result ----
        // Start date is set, End after radio button is selected, any occurrences nomber is entered; Settings are saved;s
        recurrence.click("rbtnEndafter");
        recurrence.deleteText("txtEndafterEditableTextoccurences", 0);
        recurrence.enterString("txtEndafterEditableTextoccurences", "9");

        String startTime = convertHour(recurrence.getTextValue("txtStart"));
        String endTime = convertHour(recurrence.getTextValue("txtEnd"));
        recurrence.click("btnOK");

        // Select Save Changes
        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 11 -----
        // ---- Step Action ----
        // Go to Alfresco Share;
        // ---- Expected result ----
        // Alfresco Share is opened;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // ---- Step 12 -----
        // ---- Step Action ----
        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        // ---- Expected result ----
        // New recurrence appointment is created and displayed in the calendar of selected Meeting Workspace;
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName));
        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.getStartDateTime().contains(startTime));
        Assert.assertTrue(eventInfo.getEndDateTime().contains(endTime));
        Assert.assertTrue(eventInfo.isRecurrencePresent());

        eventInfo.closeInformationForm();
        calendarPage.chooseAgendaTab().render();
        int event_reccurence = calendarPage.getTheNumOfEvents(ActionEventVia.AGENDA_TAB);
        Assert.assertEquals(event_reccurence, 9);

        // ---- Step 13 -----
        // ---- Step Action ----
        // Verify the event at My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place;
        // ---- Expected result ----
        // Event at My calendar dashlet, Site calendar dashlet is marked as recurrence; Calendar tab of the meeting place are correctly dispalyed (start date is
        // correct, event repeats <enter number of occurences in the 6 step> times );
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // Site calendar dashlet is marked as recurrence
        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName));

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();

        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        String theTime = startTime + " - " + endTime;

        // The start and end date, duration of the event at My calendar dashlet
        String eventDetails = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventDetails.contains(theTime));

    }

    /**
     * AONE-9722:Creating a recurrence event. End by date
     */
    @Test(groups = "alfresco-one")
    public void AONE_9722() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();


        // ---- Step 1, 2 -----
        // ---- Step Action ----
        // 1. Click on "New Appointment" link in "New" drop-down menu;
        // 2. Fill in Subject and location;
        // ---- Expected result ----
        // 1. Appointment form is opened;
        // 2. Information is entered successfully;
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Click Recurrence button;
        // ---- Expected result ----
        // Appointment recurrence form is opened;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        // ---- Step 4 -----
        // ---- Step Action ----
        // Select any Start time and End time (or set any duration);
        // ---- Expected result ----
        // Time and duration are set;
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");

        // ---- Step 5 -----
        // ---- Step Action ----
        // Set any recurrence pattern;
        // ---- Expected result ----
        // Any recurrence pattern is chosen;
        recurrence.click("rbtnDaily");
        recurrence.click("rbtnEvery");

        // ---- Step 6 -----
        // ---- Step Action ----
        // In the Range of recurrnce part select any start date, select End by radio button and select any date, click Ok button;
        // ---- Expected result ----
        // Start date is set, End byr radio button is selected, any date is selected; Settings are saved;
        recurrence.click("rbtnEndby");
        recurrence.deleteText("txtEndby", 0);

        SimpleDateFormat FormattedDATE = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 2);
        String nextDays = (String) (FormattedDATE.format(c.getTime()));

        recurrence.enterString("txtEndby", nextDays);

        String startTime = convertHour(recurrence.getTextValue("txtStart"));
        String endTime = convertHour(recurrence.getTextValue("txtEnd"));
        recurrence.click("btnOK");

        // Select Save Changes
        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // ---- Step 11 -----
        // ---- Step Action ----
        // Go to Alfresco Share;
        // ---- Expected result ----
        // Alfresco Share is opened;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // ---- Step 12 -----
        // ---- Step Action ----
        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        // ---- Expected result ----
        // New recurrence appointment is created and displayed in the calendar of selected Meeting Workspace;
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName));
        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.getStartDateTime().contains(startTime));
        Assert.assertTrue(eventInfo.getEndDateTime().contains(endTime));
        Assert.assertTrue(eventInfo.isRecurrencePresent());

        eventInfo.closeInformationForm();
        calendarPage.chooseAgendaTab().render();
        int event_reccurence = calendarPage.getTheNumOfEvents(ActionEventVia.AGENDA_TAB);
        Assert.assertEquals(event_reccurence, 3);

        // ---- Step 13 -----
        // ---- Step Action ----
        // Verify the event at My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place;
        // ---- Expected result ----
        // Event at My calendar dashlet, Site calendar dashlet is marked as recurrence; Calendar tab of the meeting place are correctly dispalyed (start date is
        // correct, event repeats till the end date selected in the 6 step);
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // Site calendar dashlet is marked as recurrence
        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName));

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();

        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertTrue(repeating);

        String theTime = startTime + " - " + endTime;

        // The start and end date, duration of the event at My calendar dashlet
        String eventDetails = myCalendar.getEventDetails(siteName);
        Assert.assertTrue(eventDetails.contains(theTime));

    }

    /**
     * AONE-9723:Remove recurrence
     */
    @Test(groups = "alfresco-one")
    public void AONE_9723() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String location = testName + " - Room";
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);

        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();


        // Precondition: any recurrence event is created via MS Outlook;
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");

        // Click Recurrence button;
        Ldtp l2 = outlook.getAbstractUtil().setOnWindow(siteName);
        l2.doubleClick("btnRecurrence");

        // Select any start and end time (or set any duration);
        Ldtp recurrence = outlook.getAbstractUtil().setOnWindow("Appointment Recurrence");
        recurrence.deleteText("txtDuration", 0);
        recurrence.enterString("txtDuration", "4 hours");

        // Set any recurrence pattern;
        recurrence.click("rbtnDaily");
        recurrence.click("rbtnEvery");
        String startTime = convertHour(recurrence.getTextValue("txtStart"));
        String endTime = convertHour(recurrence.getTextValue("txtEnd"));
        recurrence.click("btnOK");

        // Select Save Changes
        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // Go to Alfresco Share;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        SiteDashboardPage siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName));

        // Click the event's name -> Event Info window pops up;
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName).render();
        Assert.assertTrue(eventInfo.isRecurrencePresent());
        eventInfo.closeInformationForm();

        // Verify the event at My calendar dashlet, Site calendar dashlet;
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // Site calendar dashlet is marked as recurrence
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName));

        // ---- Step 1 -----
        // ---- Step Action ----
        // Click Recurence button;
        // ---- Expected result ----
        // Apointment recurrence form is opened;
        after_rec.doubleClick("btnRecurrence");

        // ---- Step 2 -----
        // ---- Step Action ----
        // CLick Remove recurrence button;
        // ---- Expected result ----
        // Apointment recurrence form is closed; Reccurence rule is removed;
        recurrence.click("btnRemoveRecurrence");
        l2.click("btnSave");

        // ---- Step 3 -----
        // ---- Step Action ----
        // Go to Alfresco Share;
        // ---- Expected result ----
        // User logs in successfully;
        siteDashBoard = SiteUtil.openSiteFromSearch(drone, siteName);

        // ---- Step 4 -----
        // ---- Step Action ----
        // Verify the created event is displayed in the calendar of selected Meeting Workspace;
        // ---- Expected result ----
        // Event is not recurrent; It correctly displayed in the calendar of selected Meeting Workspace;
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();

        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName));
        eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, siteName).render();
        Assert.assertFalse(eventInfo.isRecurrencePresent());
        Assert.assertTrue(eventInfo.getStartDateTime().contains(startTime));
        Assert.assertTrue(eventInfo.getEndDateTime().contains(endTime));
        eventInfo.closeInformationForm();

        // ---- Step 5 -----
        // ---- Step Action ----
        // Verify the event at My calendar dashlet, Site calendar dashlet and Calendar tab of the meeting place;
        // ---- Expected result ----
        // Event at My calendar dashlet, Site calendar dashlet is not marked as recurrence; Calendar tab of the meeting place are correctly dispalyed (start
        // date is correct, duration is correct); There is not event's reccurrences in future;
        SiteUtil.openSiteFromSearch(drone, siteName);
        siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        // Site calendar dashlet is marked as recurrence
        Assert.assertFalse(siteCalendarDashlet.isRepeating(siteName));

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_CALENDAR, 1).render();

        MyCalendarDashlet myCalendar = dashBoard.getDashlet("my-calendar").render();

        // My Calendar dashlet event is marked as recurring
        Boolean repeating = myCalendar.isRepeating(siteName);
        Assert.assertFalse(repeating);

    }

    private String convertHour(String hour)
    {
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
        String convertedHour = "";
        try
        {
            Date _24HourDt;
            _24HourDt = _24HourSDF.parse(hour);
            convertedHour = _12HourSDF.format(_24HourDt);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String hours = convertedHour.substring(0, 2);
        int hours_int = Integer.parseInt(hours);
        if (hours_int <= 10)
        {
            convertedHour = convertedHour.substring(1);
        }

        return convertedHour;
    }
}
