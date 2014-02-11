/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.vti.handler.alfresco;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.module.vti.handler.alfresco.v3.AlfrescoMeetingServiceHandler;
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.alfresco.repo.calendar.CalendarModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.calendar.CalendarEntry;
import org.alfresco.service.cmr.calendar.CalendarService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * Test cases for {@link AlfrescoMeetingServiceHandler}. Based on MNT-9500.
 * 
 * @author Alex Bykov
 * @since 4.1.7
 */
public class AlfrescoMeetingServiceHandlerTest
{
    private static final String TEST_SITE_PREFIX = "CalendarSiteTest";

    private static final ApplicationContext appContext = ApplicationContextHelper.getApplicationContext();

    // injected services
    private static CalendarService calendarService;
    private static NodeService nodeService;
    private static RetryingTransactionHelper transactionHelper;
    private static SiteService siteService;

    private static AlfrescoMeetingServiceHandler meetingServiceHandler;

    private static final String ADMIN_USER = AuthenticationUtil.getAdminUserName();

    private static SiteInfo CALENDAR_SITE;
    private static CalendarEntry RECURRENCE;

    // the new end and start of occurrence
    private static final Date NEW_START = new Date(1380261600000L); // 09/27/2013 09:00:00
    private static final Date NEW_END = new Date(1380279600000L); // 09/27/2013 14:00:00

    // the new location and subject of occurrence
    private static final String NEW_LOC = "new location";
    private static final String NEW_SUB = "new subject";

    @BeforeClass
    public static void initTestsContext() throws Exception
    {
    	calendarService = (CalendarService) appContext.getBean("CalendarService");
    	nodeService = (NodeService) appContext.getBean("nodeService");
    	transactionHelper = (RetryingTransactionHelper) appContext.getBean("retryingTransactionHelper");
    	siteService = (SiteService) appContext.getBean("SiteService");

    	meetingServiceHandler = new AlfrescoMeetingServiceHandler();
    	meetingServiceHandler.setCalendarService(calendarService);
    	meetingServiceHandler.setNodeService(nodeService);
    	meetingServiceHandler.setSiteService(siteService);
    	meetingServiceHandler.setTransactionService((TransactionService) appContext.getBean("transactionService"));
    	meetingServiceHandler.setNamespaceService((NamespaceService) appContext.getBean("namespaceService"));
    	meetingServiceHandler.setSearchService((SearchService) appContext.getBean("searchService"));
    	meetingServiceHandler.setPersonService((PersonService) appContext.getBean("personService"));

        // Do the setup as admin
        AuthenticationUtil.setFullyAuthenticatedUser(ADMIN_USER);

        createTestSite();
        createRecurrence();
    }

    @Test
    public void testUpdateMeeting()
    {
        updateOccurrence();

        // check that occurrence has been updated successfully
        Set<QName> childNodeTypeQNames = new HashSet<QName>();
        childNodeTypeQNames = new HashSet<QName>();
        childNodeTypeQNames.add(CalendarModel.TYPE_UPDATED_EVENT);
        List<ChildAssociationRef> updatedEventList = nodeService.getChildAssocs(RECURRENCE.getNodeRef(), childNodeTypeQNames);

        // get properties through node service
        Date start = null;
        Date end = null;
        String loc = null;
        String sub = null;

        for (ChildAssociationRef updatedEvent : updatedEventList)
        {
            NodeRef occurenceRef = updatedEvent.getChildRef();
            start = (Date) nodeService.getProperty(occurenceRef, CalendarModel.PROP_UPDATED_START);
            end = (Date) nodeService.getProperty(occurenceRef, CalendarModel.PROP_UPDATED_END);
            loc = (String) nodeService.getProperty(occurenceRef, CalendarModel.PROP_UPDATED_WHERE);
            sub = (String) nodeService.getProperty(occurenceRef, CalendarModel.PROP_UPDATED_WHAT);
        }

        // check properties
        assertTrue(NEW_START.equals(start));
        assertTrue(NEW_END.equals(end));
        assertTrue(NEW_LOC.equals(loc));
        assertTrue(NEW_SUB.equals(sub));
    }

    @Test
    public void testUpdateSeries()
    {
        updateOccurrence();

        MeetingBean updateSeries = new MeetingBean();
        List<String> attendees = new ArrayList<String>(1);
        attendees.add("admin@alfresco.com");
        updateSeries.setAttendees(attendees);
        updateSeries.setRecurrenceRule("FREQ=WEEKLY;COUNT=10;BYDAY=FR;INTERVAL=1");
        updateSeries.setStart(new Date(1378709100000L)); // 09/09/2013 9:45
        updateSeries.setEnd(new Date(1378716300000L)); // 09/09/2013 11:45
        updateSeries.setLastRecurrence(new Date(1384497900000L)); // 11/15/2013 9:45
        updateSeries.setOutlook(true);
        updateSeries.setOrganizer("admin@alfresco.com");
        updateSeries.setOutlookUID("OutLookUID!");

        // change location and subject
        updateSeries.setLocation("new series location");
        updateSeries.setTitle("new series subject");

        // update event
        updateMeeting(CALENDAR_SITE.getShortName(), updateSeries);

        // updated occurrence should remain
        Set<QName> childNodeTypeQNames = new HashSet<QName>();
        childNodeTypeQNames = new HashSet<QName>();
        childNodeTypeQNames.add(CalendarModel.TYPE_UPDATED_EVENT);
        childNodeTypeQNames.add(CalendarModel.TYPE_IGNORE_EVENT);
        List<ChildAssociationRef> updatedOccurrencesList = nodeService.getChildAssocs(RECURRENCE.getNodeRef(), childNodeTypeQNames);
        assertTrue(updatedOccurrencesList.size() > 0);

        // remove occurrence 09/16/2013
        removeMeeting(20130916);

        // change the end date of the event
        updateSeries.setEnd(new Date(1378728000000L));
        updateMeeting(CALENDAR_SITE.getShortName(), updateSeries);

        // updated removed occurrences should disappear
        updatedOccurrencesList = nodeService.getChildAssocs(RECURRENCE.getNodeRef(), childNodeTypeQNames);
        assertTrue(updatedOccurrencesList.size() == 0);

        // check updated properties of the event
        Map<QName, Serializable> eventProperties = nodeService.getProperties(RECURRENCE.getNodeRef());

        assertTrue(eventProperties.get(CalendarModel.PROP_WHERE).toString().equals("new series location"));
        assertTrue(eventProperties.get(CalendarModel.PROP_WHAT).toString().equals("new series subject"));
    }

    @Test
    public void testRemoveMeeting()
    {
        // remove occurrence 09/09/2013
        removeMeeting(20130909);

        // remove updated occurrence 27/09/2013
        updateOccurrence();
        removeMeeting(20130927);

        Set<QName> childNodeTypeQNames = new HashSet<QName>();
        childNodeTypeQNames = new HashSet<QName>();
        childNodeTypeQNames.add(CalendarModel.TYPE_UPDATED_EVENT);
        List<ChildAssociationRef> updatedOccurrencesList = nodeService.getChildAssocs(RECURRENCE.getNodeRef(), childNodeTypeQNames);

        // updated occurrence should be removed
        assertTrue(updatedOccurrencesList.size() == 0);
    }

    private void removeMeeting(int day)
    {
        meetingServiceHandler.removeMeeting(CALENDAR_SITE.getShortName(), day, "OutLookUID!", 0, null, true);

        Set<QName> childNodeTypeQNames = new HashSet<QName>();
        childNodeTypeQNames.add(CalendarModel.TYPE_IGNORE_EVENT);
        List<ChildAssociationRef> ignoreEventList = nodeService.getChildAssocs(RECURRENCE.getNodeRef(), childNodeTypeQNames);
        Date ignoredDate = null;
        for (ChildAssociationRef ignoreEvent : ignoreEventList)
        {
            ignoredDate = (Date) nodeService.getProperty(ignoreEvent.getChildRef(), CalendarModel.PROP_IGNORE_EVENT_DATE);
        }

        SimpleDateFormat sdt = new SimpleDateFormat("yyyyMMdd");

        // check a date of removed occurrence
        assertNotNull(ignoredDate);
        assertTrue(sdt.format(ignoredDate).equals(String.valueOf(day)));
    }

    private static void createTestSite() throws Exception
    {
        CALENDAR_SITE = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<SiteInfo>()
        {
            @Override
            public SiteInfo execute() throws Throwable
            {
                SiteInfo site = siteService.createSite(TEST_SITE_PREFIX, AlfrescoMeetingServiceHandlerTest.class.getSimpleName() + "_testRecSite" + System.currentTimeMillis(),
                        "recurrence event on site - title", "test occurrence operations - description", SiteVisibility.PUBLIC);
                return site;
            }
        });
    }

    private static void createRecurrence()
    {
        MeetingBean meetingBean = new MeetingBean();
        List<String> attendees = new ArrayList<String>(1);
        attendees.add("admin@alfresco.com");
        meetingBean.setAttendees(attendees);
        meetingBean.setLocation("location");
        meetingBean.setRecurrenceRule("FREQ=WEEKLY;COUNT=10;BYDAY=FR;INTERVAL=1");
        meetingBean.setStart(new Date(1378709100000L)); // 09/09/2013 9:45
        meetingBean.setEnd(new Date(1378716300000L)); // 09/09/2013 11:45
        meetingBean.setLastRecurrence(new Date(1384497900000L)); // 11/15/2013 9:45
        meetingBean.setOutlook(true);
        meetingBean.setTitle(CALENDAR_SITE.getShortName());
        meetingBean.setOrganizer("admin@alfresco.com");
        meetingBean.setOutlookUID("OutLookUID!");

        // create recurrence
        RECURRENCE = calendarService.createCalendarEntry(CALENDAR_SITE.getShortName(), meetingBean);

        // Ensure it got a noderef
        assertNotNull(RECURRENCE.getNodeRef());
        assertNotNull(RECURRENCE.getSystemName());
    }

    private void updateOccurrence()
    {
        MeetingBean updateMeeting = new MeetingBean();
        List<String> attendees = new ArrayList<String>(1);
        attendees.add("admin@alfresco.com");
        updateMeeting.setAttendees(attendees);
        updateMeeting.setOrganizer("admin@alfresco.com");
        updateMeeting.setOutlookUID("OutLookUID!");
        updateMeeting.setOutlook(true);
        updateMeeting.setReccurenceIdDate(new Date(1380258000000L)); // 09/27/2013 08:00:00
        updateMeeting.setStart(NEW_START);
        updateMeeting.setEnd(NEW_END);
        updateMeeting.setLocation(NEW_LOC);
        updateMeeting.setTitle(NEW_SUB);

        // update occurrence
        updateMeeting(CALENDAR_SITE.getShortName(), updateMeeting);
    }

    private void updateMeeting(final String siteName, final MeetingBean updateProperties)
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                meetingServiceHandler.updateMeetingFromICal(siteName, updateProperties, false);
                return null;
            }
        });
    }
}
