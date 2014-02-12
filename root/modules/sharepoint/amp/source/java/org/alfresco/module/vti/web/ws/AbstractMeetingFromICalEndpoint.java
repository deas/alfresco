/*
* Copyright (C) 2005-2010 Alfresco Software Limited.
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.alfresco.service.cmr.calendar.CalendarTimezoneHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Parent class of Meeting Endpoints which work on iCal files.
 * 
 * @author Nick Burch
 */
public abstract class AbstractMeetingFromICalEndpoint extends AbstractMeetingEndpoint
{
    private static final String DATE_FROMAT = "yyyyMMddkkmmss";

    private static final String ALL_DAY_DATE_FROMAT = "yyyyMMdd";
    
    private static final String PREFIX_MAILTO = "mailto:";
    
    private static Log logger = LogFactory.getLog(AddMeetingFromICalEndpoint.class);

    private static long DAY = 24 * 60 * 60 * 1000;

    private static Map<String, Integer> daysMap = new HashMap<String, Integer>();

    {
        daysMap.put("SU", new Integer(0));
        daysMap.put("MO", new Integer(1));
        daysMap.put("TU", new Integer(2));
        daysMap.put("WE", new Integer(3));
        daysMap.put("TH", new Integer(4));
        daysMap.put("FR", new Integer(5));
        daysMap.put("SA", new Integer(6));
    }

    public AbstractMeetingFromICalEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }

    /**
     * Add new meeting to Meeting Workspace
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse})
     */
    @Override
    protected void executeRequest(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            Element requestElement, SimpleNamespaceContext nc) throws Exception
    {
        // getting organizerEmail parameter from request
        XPath organizerEmailPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/organizerEmail"));
        organizerEmailPath.setNamespaceContext(nc);
        Element organizerEmailE = (Element) organizerEmailPath.selectSingleNode(requestElement);
        String organizerEmail = null;
        if (organizerEmailE != null && organizerEmailE.getText() != null)
        {
            organizerEmail = organizerEmailE.getText();
        }
        if (logger.isDebugEnabled())
            logger.debug("Getting organizerEmail from request: " + organizerEmail);

        // getting icalText parameter from request
        XPath icalTextPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/icalText"));
        icalTextPath.setNamespaceContext(nc);
        Element icalTextE = (Element) icalTextPath.selectSingleNode(requestElement);
        String icalText = null;
        if (icalTextE != null && icalTextE.getText() != null)
        {
            icalText = icalTextE.getText();
        }
        if (logger.isDebugEnabled())
            logger.debug("Getting icalText from request: " + icalText);
        
        // Get the ignoreAttendees parameter from request
        // Controls if this is a scheduling only update, or if attendees are affected too
        if (logger.isDebugEnabled())
            logger.debug("Getting ignoreAttendees from request.");
        XPath ignoreAttendeesPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/ignoreAttendees"));
        ignoreAttendeesPath.setNamespaceContext(nc);
        Element ignoreAttendeesE = (Element) ignoreAttendeesPath.selectSingleNode(requestElement);
        boolean ignoreAttendees = false;
        if (ignoreAttendeesE != null && ignoreAttendeesE.getText() != null)
        {
            ignoreAttendees = Boolean.parseBoolean(ignoreAttendeesE.getText());
        }

        // Turn the iCal text into an object we can use/store
        MeetingBean meetingBean = getMeeting(icalText);

        // Have the action done
        executeMeetingAction(soapRequest, soapResponse, siteName, meetingBean, -1, -1, ignoreAttendees, false);
    }

    /**
     * Create meeting bean from iCalText
     * 
     * @param icalText iCal
     */
    protected MeetingBean getMeeting(String icalText)
    {
        // iCal text is required
        if (icalText == null)
        {
            throw new VtiSoapException("iCal Text is required", 5);
        }
        
        // Strip and prepare
        icalText = icalText.replaceAll("\r\n\t", "");
        icalText = icalText.replaceAll("\r\n ", "");
        
        if (icalText.length() == 0)
        {
            throw new VtiSoapException("iCal Text must not be empty", 5);
        }
        
        // Delegate the parsing work (for now) to CalendarTimezoneHelper
        // In future, we should have something build the MeetingBean directly
        Map<String, String> icalParams = ICalHelper.getICalParams(icalText);
        return getMeeting(icalParams);
    }
    private static class ICalHelper extends CalendarTimezoneHelper
    {
        protected static Map<String,String> getICalParams(String icalText)
        {
           return CalendarTimezoneHelper.getICalParams(icalText);
        }
        protected static SimpleTimeZone buildTimeZone(Map<String,String> icalParams)
        {
           return CalendarTimezoneHelper.buildTimeZone(icalParams);
        }
    }

    /**
     * Create meeting bean from iCal parameters
     * 
     * @param params iCal params
     */
    private MeetingBean getMeeting(Map<String, String> params)
    {
        MeetingBean meeting = new MeetingBean();
        meeting.setLocation(params.get("LOCATION"));
        meeting.setTitle(params.get("SUMMARY"));
        meeting.setOrganizer(params.get("ORGANIZER"));
        meeting.setId(params.get("UID"));
        
        // Start Date is required. If no end is given, the iCal spec
        //  says that it is treated as ending and the start time
        meeting.setStart(parseDate("DTSTART", params));
        if (params.containsKey("DTEND"))
        {
            meeting.setEnd(parseDate("DTEND", params));
        }
        else
        {
            meeting.setEnd(meeting.getStart());
        }
        
        if (params.get("RRULE") != null)
        {
            meeting.setRecurrenceRule(params.get("RRULE"));
            meeting.setLastRecurrence(getLastMeeting(meeting));
            if (logger.isDebugEnabled())
            {
                logger.debug("RRULE: " + meeting.getRecurrenceRule());
                logger.debug("Last meeting: " + meeting.getLastRecurrence());
            }
        }
        else if (params.containsKey("RECURRENCE-ID"))
        {
            meeting.setReccurenceIdDate(parseDate("RECURRENCE-ID", params));
        }
        List<String> attendees = new ArrayList<String>();
        String currentAttendee = null;
        for (int i = 0; (currentAttendee = params.get("ATTENDEE" + i)) != null; i++)
        {
            if (currentAttendee.startsWith(PREFIX_MAILTO))
            {
                currentAttendee = currentAttendee.substring(PREFIX_MAILTO.length());
            }
            attendees.add(currentAttendee);
        }
        meeting.setAttendees(attendees);
        if (logger.isDebugEnabled())
		{
            logger.debug("Attendees are: " + meeting.getAttendees());
        }
        return meeting;
    }

    /**
     * Parse date from specific iCal format
     * 
     * @param stringDate iCal date value
     */
    private Date parseDate(String dateType, Map<String,String> params)
    {
        DateFormat dateFormat;
        
        // Is this a whole-day date, or a date+time?
        String stringDate = params.get(dateType);
        if (stringDate.indexOf("T") == -1)
        {
            dateFormat = new SimpleDateFormat(ALL_DAY_DATE_FROMAT);
        }
        else
        {
            dateFormat = new SimpleDateFormat(DATE_FROMAT);
        }
        
        // Try to work out the timezone
        TimeZone timeZone = getTimeZone(dateType, params);
        dateFormat.setTimeZone(timeZone);

        // Change from iCal to Java format
        stringDate = prepareDate(stringDate);

        // Try to parse
        Date date = null;
        try
        {
            date = dateFormat.parse(stringDate);
        }
        catch (ParseException e)
        {
            throw new AlfrescoRuntimeException("Date '" + stringDate + "' + cannot be parsed", e);
        }

        return date;
    }

    /**
     * Retrieve TimeZone from specific iCal format
     * 
     * @param stringDate iCal date value
     * @param params the full iCal parameters (used to find full TZ info from)
     */
    private TimeZone getTimeZone(String dateType, Map<String,String> params)
    {
        String stringDate = params.get(dateType);
        String dateTypeTZID = dateType+"-TZID";
        
        // If there's a VTIMEZONE block, use that
        TimeZone timeZone = ICalHelper.buildTimeZone(params);
        if (timeZone != null)
        {
           return timeZone;
        }
        
        // Try other ways to find it
        if (stringDate.endsWith("Z"))
        {
            timeZone = TimeZone.getTimeZone("GMT");
        }
        else if (params.containsKey(dateTypeTZID))
        {
            // Let's hope it's in the Java format!
            String timezoneId = params.get(dateTypeTZID);
            timeZone = TimeZone.getTimeZone(timezoneId);
        }
        else
        {
            // No useful timezone info given
            // Fall back on the system default as our best hope
            timeZone = TimeZone.getDefault();
        }
        
        return timeZone;
    }

    /**
     * Prepare iCal date to parsing, remove all unparsable parts
     * 
     * @param stringDate iCal date value
     */
    private String prepareDate(String stringDate)
    {
        String preparedDate = stringDate;
        if (preparedDate.contains(":"))
        {
            preparedDate = preparedDate.substring(stringDate.indexOf(":") + 1);
        }
        if (preparedDate.contains("T"))
        {
            preparedDate = preparedDate.replace("T", "");
        }
        if (preparedDate.contains("Z"))
        {
            preparedDate = preparedDate.replace("Z", "");
        }
        return preparedDate;
    }

    /**
     * Get last date of meeting
     * 
     * @param meeting The meeting bean ({@link MeetingBean})
     * @return date of last meeting
     */
    private Date getLastMeeting(MeetingBean meeting)
    {
        String[] ruleParams = meeting.getRecurrenceRule().split(";");

        Map<String, String> eventParam = new HashMap<String, String>();

        for (int i = 0; i < ruleParams.length; i++)
        {
            String[] part = ruleParams[i].split("=");
            eventParam.put(part[0], part[1]);
        }

        boolean reloadEventParam = false;

        long interval = 1;
        if (eventParam.get("INTERVAL") != null)
        {
            interval = Long.parseLong(eventParam.get("INTERVAL"));
        }
        else
        {
            meeting.setRecurrenceRule(meeting.getRecurrenceRule() + ";INTERVAL=1");
            reloadEventParam = true;
        }

        if (eventParam.get("FREQ").equals("YEARLY"))
        {
            // Yearly is the same as monthly, we just increase interval
            String recRule = meeting.getRecurrenceRule().replace("YEARLY", "MONTHLY");
            recRule = recRule.replace("INTERVAL=" + interval, "INTERVAL=" + interval*12);

            meeting.setRecurrenceRule(recRule);
            reloadEventParam = true;
        }

        if (eventParam.get("FREQ").equals("DAILY") && eventParam.get("BYDAY") != null)
        {
            String recRule = meeting.getRecurrenceRule().replace("DAILY", "WEEKLY");
            meeting.setRecurrenceRule(recRule);
            reloadEventParam = true;
        }

        if (reloadEventParam)
        {
            eventParam.clear();
            ruleParams = meeting.getRecurrenceRule().split(";");

            for (int i = 0; i < ruleParams.length; i++)
            {
                String[] part = ruleParams[i].split("=");
                eventParam.put(part[0], part[1]);
            }
        }

        if (eventParam.get("COUNT") != null)
        {
            Long eventOccurrs = Long.parseLong(eventParam.get("COUNT"));
            if (eventParam.get("FREQ").equals("MONTHLY"))
            {
                return getLastMeetingMonthly(meeting.getStartDate(), eventParam, eventOccurrs, interval);
            }

            if (eventParam.get("FREQ").equals("DAILY"))
            {
                return getLastMeetingDaily(meeting.getStartDate(), eventParam, eventOccurrs, interval);
            }

            if (eventParam.get("FREQ").equals("WEEKLY"))
            {
                return getLastMeetingWeekly(meeting.getStartDate(), eventParam, eventOccurrs, interval);
            }
        }
        else if (eventParam.get("UNTIL") != null)
        {
            return parseDate("UNTIL", eventParam);
        }

        return null;
    }

    /**
     * Get last date of recurrence meeting for daily recurrence meeting
     * 
     * @param meetingFirstDay first date of meeting
     * @param eventParam event parameters from meeting RRULE
     * @param eventOccurrs count of meetings
     * @param interval interval between meetings
     * @return last date of recurrence meeting
     */
    private Date getLastMeetingDaily(Date meetingFirstDay, Map<String, String> eventParam, long eventOccurrs, long interval)
    {
        eventOccurrs--;
        return new Date(meetingFirstDay.getTime() + eventOccurrs * interval * DAY);
    }

    /**
     * Get last date of recurrence meeting for weekly recurrence meeting
     * 
     * @param meetingFirstDay first date of meeting
     * @param eventParam event parameters from meeting RRULE
     * @param eventOccurrs count of meetings
     * @param interval interval between meetings
     * @return last date of recurrence meeting
     */
    private Date getLastMeetingWeekly(Date meetingFirstDay, Map<String, String> eventParam, long eventOccurrs, long interval)
    {
        String[] ruleDays = eventParam.get("BYDAY").split(",");

        long fullWeeks = eventOccurrs / ruleDays.length;
        int occurrsToAdd = new Long(eventOccurrs % ruleDays.length).intValue() - 1;
        Date lastMeeting = new Date();

        lastMeeting.setTime(meetingFirstDay.getTime() + fullWeeks * 7 * DAY * interval);

        if (occurrsToAdd == -1)
        {
            lastMeeting.setTime(lastMeeting.getTime() - 7 * DAY * interval);
            occurrsToAdd += ruleDays.length;
        }
        if (occurrsToAdd > 0)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastMeeting);
            long eventStartDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;

            long eventLastDay = eventStartDayOfWeek;

            // Add days that are after rule start day.
            for (int i = 0; i < ruleDays.length; i++)
            {
                if (occurrsToAdd > 0)
                {
                    long dayFromRule = daysMap.get(ruleDays[i]);

                    if (dayFromRule > eventLastDay)
                    {
                        long dayOffset = dayFromRule - eventLastDay;
                        lastMeeting.setTime(lastMeeting.getTime() + dayOffset * DAY);
                        eventLastDay = dayFromRule;
                        occurrsToAdd--;
                    }
                }
                else
                {
                    break;
                }
            }

            if (occurrsToAdd > 0)
            {
                lastMeeting.setTime(lastMeeting.getTime() + 7 * DAY * interval);

                cal.setTime(lastMeeting);
                cal.set(Calendar.DAY_OF_WEEK, 2);
                cal.getTime();
                cal.add(Calendar.DAY_OF_YEAR, -1);
                lastMeeting.setTime(cal.getTimeInMillis());

                // Find the first day before rule start day.
                long dayOffset = 0;
                for (int i = 0; i < ruleDays.length; i++)
                {
                    if (occurrsToAdd > 0)
                    {
                        long dayFromRule = daysMap.get(ruleDays[i]);

                        if (dayFromRule < eventStartDayOfWeek)
                        {
                            // we doesn't include Monday because we've already include it.
                            if (dayFromRule > daysMap.get("SU"))
                            {
                                dayOffset = dayFromRule;
                            }
                            occurrsToAdd--;
                        }
                        else if (dayFromRule == eventStartDayOfWeek)
                        {
                            occurrsToAdd--;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
                lastMeeting.setTime(lastMeeting.getTime() + DAY * dayOffset);
            }
        }
        return lastMeeting;
    }

    /**
     * Get last date of recurrence meeting for monthly recurrence meeting
     * 
     * @param meetingFirstDay first date of meeting
     * @param eventParam event parameters from meeting RRULE
     * @param eventOccurrs count of meetings
     * @param interval interval between meetings
     * @return last date of recurrence meeting
     */
    private Date getLastMeetingMonthly(Date meetingFirstDay, Map<String, String> eventParam, long eventOccurrs, long interval)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(meetingFirstDay);
        // set last meeting month
        cal.add(Calendar.MONTH, new Long((eventOccurrs - 1) * interval).intValue());
        // refresh calendar
        cal.getTime();

        if (eventParam.get("BYDAY") != null)
        {
            String[] allowedDays3 = eventParam.get("BYDAY").split(",");
            Set<Integer> allowedDays = new HashSet<Integer>();
            for (String day : allowedDays3)
            {
                allowedDays.add(daysMap.get(day).intValue());
            }

            int dayInWeek = Integer.parseInt(eventParam.get("BYSETPOS"));
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.getTime();

            while (dayInWeek > 0)
            {
                if (allowedDays.contains(new Integer(cal.get(Calendar.DAY_OF_WEEK) - 1)))
                {
                    dayInWeek--;
                }
                if (dayInWeek > 0)
                {
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    cal.getTime();
                }
            }

            if (dayInWeek == -1)
            {
                cal.add(Calendar.MONTH, 1);
                cal.getTime();
                cal.add(Calendar.DAY_OF_YEAR, -1);
                cal.getTime();
                while ((!allowedDays.contains(new Integer(cal.get(Calendar.DAY_OF_WEEK) - 1))))
                {
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    cal.getTime();
                }
            }
        }
        return new Date(cal.getTimeInMillis());
    }
}