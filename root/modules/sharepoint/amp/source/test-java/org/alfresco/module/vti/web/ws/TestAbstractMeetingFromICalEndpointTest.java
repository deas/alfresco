/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.module.vti.web.ws;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.TimeZone;

import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.alfresco.util.ISO8601DateFormat;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link AbstractMeetingFromICalEndpoint} class.
 * 
 * @author Alex Bykov
 */
public class TestAbstractMeetingFromICalEndpointTest
{
    private AbstractMeetingFromICalEndpoint meetingEndpoint;

    @Before
    public void setUp() throws Exception
    {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        meetingEndpoint = new AbstractMeetingFromICalEndpoint(null)
        {
            @Override
            protected void executeMeetingAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName, MeetingBean meetingBean, int sequence, int recurrenceId,
                    boolean ignoreAttendees, boolean cancelMeeting) throws Exception
            {
            }
        };
    }

    @Test
    public void testLastMeeting()
    {
        // Test last meeting day for yearly recurrence events
        // Occurs every January 14 effective 14/01/2014 until 14/01/2023 from 12:00 to 12:30
        String icalText = "BEGIN:VCALENDAR\r\nPRODID:-//Microsoft Corporation//Outlook 14.0 MIMEDIR//EN\r\nVERSION:2.0\r\nMETHOD:REQUEST\r\nX-MS-OLK-FORCEINSPECTOROPEN:TRUE\r\nBEGIN:VTIMEZONE\r\nTZID:Kaliningrad Standard Time\r\nBEGIN:STANDARD\r\nDTSTART:16010101T000000\r\nTZOFFSETFROM:+0400\r\nTZOFFSETTO:+0300\r\nEND:STANDARD\r\nEND:VTIMEZONE\r\nBEGIN:VEVENT\r\nCLASS:PUBLIC\r\nCREATED:20140114T120326Z\r\nDTEND;TZID=\"Kaliningrad Standard Time\":20140114T153000\r\nDTSTAMP:20140114T120327Z\r\nDTSTART;TZID=\"Kaliningrad Standard Time\":20140114T150000\r\nLAST-MODIFIED:20140114T120326Z\r\nLOCATION:location\r\nPRIORITY:5\r\nRRULE:FREQ=YEARLY;COUNT=10;BYMONTHDAY=14;BYMONTH=1\r\nSEQUENCE:0\r\nSUMMARY;LANGUAGE=en-gb:testSubject\r\nTRANSP:OPAQUE\r\nUID:040000008200E00074C5B7101A82E0080000000090E61A733711CF01000000000000000010000000523CF26A92380A4197215B5F05BBC150\r\nX-MICROSOFT-CDO-BUSYSTATUS:BUSY\r\nX-MICROSOFT-CDO-IMPORTANCE:1\r\nX-MICROSOFT-DISALLOW-COUNTER:FALSE\r\nX-MS-OLK-AUTOFILLLOCATION:FALSE\r\nX-MS-OLK-CONFTYPE:0\r\nBEGIN:VALARM\r\nTRIGGER:-PT15M\r\nACTION:DISPLAY\r\nDESCRIPTION:Reminder\r\nEND:VALARM\r\nEND:VEVENT\r\nEND:VCALENDAR\r\n";
        MeetingBean meeting = meetingEndpoint.getMeeting(icalText);
        // Last day is Sat Jan 14 12:00:00 GMT 2023
        Date expectedDate = ISO8601DateFormat.parse("2023-01-14T12:00:00.000Z");
        assertEquals("End date is not correctly counted for yearly recurrence events", expectedDate, meeting.getLastRecurrence());

        // Test last meeting day for monthly recurrence events
        // Occurs day 14 of every 1 month effective 14/01/2014 until 14/10/2014 from 12:00 to 12:30
        icalText = "BEGIN:VCALENDAR\r\nPRODID:-//Microsoft Corporation//Outlook 14.0 MIMEDIR//EN\r\nVERSION:2.0\r\nMETHOD:REQUEST\r\nX-MS-OLK-FORCEINSPECTOROPEN:TRUE\r\nBEGIN:VTIMEZONE\r\nTZID:Kaliningrad Standard Time\r\nBEGIN:STANDARD\r\nDTSTART:16010101T000000\r\nTZOFFSETFROM:+0400\r\nTZOFFSETTO:+0300\r\nEND:STANDARD\r\nEND:VTIMEZONE\r\nBEGIN:VEVENT\r\nCLASS:PUBLIC\r\nCREATED:20140114T144351Z\r\nDTEND;TZID=\"Kaliningrad Standard Time\":20140114T153000\r\nDTSTAMP:20140114T144351Z\r\nDTSTART;TZID=\"Kaliningrad Standard Time\":20140114T150000\r\nLAST-MODIFIED:20140114T144351Z\r\nLOCATION:location\r\nPRIORITY:5\r\nRRULE:FREQ=MONTHLY;COUNT=10;BYMONTHDAY=14\r\nSEQUENCE:0\r\nSUMMARY;LANGUAGE=en-gb:testSublect\r\nTRANSP:OPAQUE\r\nUID:040000008200E00074C5B7101A82E0080000000090E61A733711CF01000000000000000\r\n\t010000000523CF26A92380A4197215B5F05BBC150\r\nX-MICROSOFT-CDO-BUSYSTATUS:BUSY\r\nX-MICROSOFT-CDO-IMPORTANCE:1\r\nX-MICROSOFT-DISALLOW-COUNTER:FALSE\r\nX-MS-OLK-AUTOFILLLOCATION:FALSE\r\nX-MS-OLK-CONFTYPE:0\r\nBEGIN:VALARM\r\nTRIGGER:-PT15M\r\nACTION:DISPLAY\r\nDESCRIPTION:Reminder\r\nEND:VALARM\r\nEND:VEVENT\r\nEND:VCALENDAR\r\n";
        meeting = meetingEndpoint.getMeeting(icalText);
        // Last day is Tue Oct 14 12:00:00 GMT 2014
        expectedDate = ISO8601DateFormat.parse("2014-10-14T12:00:00.000Z");
        assertEquals("End date is not correctly counted for monthly recurrence events", expectedDate, meeting.getLastRecurrence());

        // Test last meeting day for weekly recurrence events
        // Occurs every Tuesday effective 14/01/2014 until 18/03/2014 from 12:00 to 12:30
        icalText = "BEGIN:VCALENDAR\r\nPRODID:-//Microsoft Corporation//Outlook 14.0 MIMEDIR//EN\r\nVERSION:2.0\r\nMETHOD:REQUEST\r\nX-MS-OLK-FORCEINSPECTOROPEN:TRUE\r\nBEGIN:VTIMEZONE\r\nTZID:Kaliningrad Standard Time\r\nBEGIN:STANDARD\r\nDTSTART:16010101T000000\r\nTZOFFSETFROM:+0400\r\nTZOFFSETTO:+0300\r\nEND:STANDARD\r\nEND:VTIMEZONE\r\nBEGIN:VEVENT\r\nCLASS:PUBLIC\r\nCREATED:20140114T145233Z\r\nDTEND;TZID=\"Kaliningrad Standard Time\":20140114T153000\r\nDTSTAMP:20140114T145233Z\r\nDTSTART;TZID=\"Kaliningrad Standard Time\":20140114T150000\r\nLAST-MODIFIED:20140114T145233Z\r\nLOCATION:location\r\nPRIORITY:5\r\nRRULE:FREQ=WEEKLY;COUNT=10;BYDAY=TU\r\nSEQUENCE:0\r\nSUMMARY;LANGUAGE=en-gb:testSublect\r\nTRANSP:OPAQUE\r\nUID:040000008200E00074C5B7101A82E0080000000090E61A733711CF01000000000000000\r\n\t010000000523CF26A92380A4197215B5F05BBC150\r\nX-MICROSOFT-CDO-BUSYSTATUS:BUSY\r\nX-MICROSOFT-CDO-IMPORTANCE:1\r\nX-MICROSOFT-DISALLOW-COUNTER:FALSE\r\nX-MS-OLK-AUTOFILLLOCATION:FALSE\r\nX-MS-OLK-CONFTYPE:0\r\nBEGIN:VALARM\r\nTRIGGER:-PT15M\r\nACTION:DISPLAY\r\nDESCRIPTION:Reminder\r\nEND:VALARM\r\nEND:VEVENT\r\nEND:VCALENDAR\r\n";
        meeting = meetingEndpoint.getMeeting(icalText);
        // Last day is Tue Mar 18 12:00:00 GMT 2014
        expectedDate = ISO8601DateFormat.parse("2014-03-18T12:00:00.000Z");
        assertEquals("End date is not correctly counted for weekly recurrence events", expectedDate, meeting.getLastRecurrence());

        // Test last meeting day for daily recurrence events
        // Occurs every day effective 14/01/2014 until 23/01/2014 from 12:00 to 12:30
        icalText = "BEGIN:VCALENDAR\r\nPRODID:-//Microsoft Corporation//Outlook 14.0 MIMEDIR//EN\r\nVERSION:2.0\r\nMETHOD:REQUEST\r\nX-MS-OLK-FORCEINSPECTOROPEN:TRUE\r\nBEGIN:VTIMEZONE\r\nTZID:Kaliningrad Standard Time\r\nBEGIN:STANDARD\r\nDTSTART:16010101T000000\r\nTZOFFSETFROM:+0400\r\nTZOFFSETTO:+0300\r\nEND:STANDARD\r\nEND:VTIMEZONE\r\nBEGIN:VEVENT\r\nCLASS:PUBLIC\r\nCREATED:20140114T145949Z\r\nDTEND;TZID=\"Kaliningrad Standard Time\":20140114T153000\r\nDTSTAMP:20140114T145949Z\r\nDTSTART;TZID=\"Kaliningrad Standard Time\":20140114T150000\r\nLAST-MODIFIED:20140114T145949Z\r\nLOCATION:location\r\nPRIORITY:5\r\nRRULE:FREQ=DAILY;COUNT=10\r\nSEQUENCE:0\r\nSUMMARY;LANGUAGE=en-gb:testSublect\r\nTRANSP:OPAQUE\r\nUID:040000008200E00074C5B7101A82E0080000000090E61A733711CF01000000000000000\r\n\t010000000523CF26A92380A4197215B5F05BBC150\r\nX-MICROSOFT-CDO-BUSYSTATUS:BUSY\r\nX-MICROSOFT-CDO-IMPORTANCE:1\r\nX-MICROSOFT-DISALLOW-COUNTER:FALSE\r\nX-MS-OLK-AUTOFILLLOCATION:FALSE\r\nX-MS-OLK-CONFTYPE:0\r\nBEGIN:VALARM\r\nTRIGGER:-PT15M\r\nACTION:DISPLAY\r\nDESCRIPTION:Reminder\r\nEND:VALARM\r\nEND:VEVENT\r\nEND:VCALENDAR\r\n";
        meeting = meetingEndpoint.getMeeting(icalText);
        // Last day is Thu Jan 23 12:00:00 GMT 2014
        expectedDate = ISO8601DateFormat.parse("2014-01-23T12:00:00.000Z");
        assertEquals("End date is not correctly counted for daily recurrence events", expectedDate, meeting.getLastRecurrence());
    }
}