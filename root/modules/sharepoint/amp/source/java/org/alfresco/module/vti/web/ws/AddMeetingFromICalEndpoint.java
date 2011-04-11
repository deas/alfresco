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
import java.util.TimeZone;
import java.util.Stack;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling AddMeetingFromICal soap method
 * 
 * @author PavelYur
 */
public class AddMeetingFromICalEndpoint extends AbstractEndpoint
{

    // handler that provides methods for operating with meetings
    private MeetingServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "mt";

    private static final String DATE_FROMAT = "yyyyMMddkkmmss";

    private static final String ALL_DAY_DATE_FROMAT = "yyyyMMdd";
    
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

    public AddMeetingFromICalEndpoint(MeetingServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Add new meeting to Meeting Workspace
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse})
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("Soap Method with name " + getName() + " is started.");
        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        Element requestElement = soapRequest.getDocument().getRootElement();

        // getting organizerEmail parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting organizerEmail from request.");
        XPath organizerEmailPath = new Dom4jXPath(buildXPath(prefix, "/AddMeetingFromICal/organizerEmail"));
        organizerEmailPath.setNamespaceContext(nc);

        // getting icalText parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting icalText from request.");
        XPath icalTextPath = new Dom4jXPath(buildXPath(prefix, "/AddMeetingFromICal/icalText"));
        icalTextPath.setNamespaceContext(nc);
        Element icalText = (Element) icalTextPath.selectSingleNode(requestElement);
        MeetingBean meetingBean = getMeeting(icalText.getText());
        String siteName = getDwsFromUri(soapRequest).substring(1);

        handler.addMeetingFromICal(siteName, meetingBean);

        Element root = soapResponse.getDocument().addElement("AddMeetingFromICalResponse", namespace);
        Element result = root.addElement("AddMeetingFromICalResult");
        result.addElement("AddMeetingFromICal").addAttribute("Url", getHost(soapRequest) + soapRequest.getAlfrescoContextName() + "/" + siteName + "?calendar=calendar")
                .addAttribute("HostTitle", meetingBean.getSubject()).addAttribute("UniquePermissions", "true").addAttribute("MeetingCount", "1").addAttribute("AnonymousAccess",
                        "false").addAttribute("AllowAuthenticatedUsers", "false");
        result.addElement("AttendeeUpdateStatus").addAttribute("Code", "0").addAttribute("Detail", "").addAttribute("ManageUserPage", "");

        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is finished.");
        }
    }

    /**
     * Create meeting bean from iCalText
     * 
     * @param icalText iCal
     */
    protected MeetingBean getMeeting(String icalText)
    {
        icalText = icalText.replaceAll("\r\n\t", "");
        icalText = icalText.replaceAll("\r\n ", "");
        Map<String, String> icalParams = getICalParams(icalText);
        return getMeeting(icalParams);
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
        meeting.setSubject(params.get("SUMMARY"));
        meeting.setOrganizer(params.get("ORGANIZER"));
        meeting.setId(params.get("UID"));
        meeting.setStartDate(parseDate(params.get("DTSTART")));
        meeting.setEndDate(parseDate(params.get("DTEND")));
        if (params.get("RRULE") != null)
        {
            meeting.setReccurenceRule(params.get("RRULE"));
            meeting.setLastMeetingDate(getLastMeeting(meeting));
            if (logger.isDebugEnabled())
            {
                logger.debug("RRULE: " + meeting.getReccurenceRule());
                logger.debug("Last meeting: " + meeting.getLastMeetingDate());
            }
        }
        List<String> attendees = new ArrayList<String>();
        String currentAttendee = null;
        for (int i = 0; (currentAttendee = params.get("ATTENDEE" + i)) != null; i++)
        {
            attendees.add(currentAttendee);
        }
        meeting.setAttendees(attendees);
        System.out.println(meeting.getAttendees());
        return meeting;
    }

    /**
     * Parse date from specific iCal format
     * 
     * @param stringDate iCal date value
     */
    private Date parseDate(String stringDate)
    {
        DateFormat dateFormat;
        
        if (stringDate.indexOf("T") == -1)
        {
            dateFormat = new SimpleDateFormat(ALL_DAY_DATE_FROMAT);
        }
        else
        {
            dateFormat = new SimpleDateFormat(DATE_FROMAT);
        }
        
        TimeZone timeZone = getTimeZone(stringDate);
        dateFormat.setTimeZone(timeZone);

        stringDate = prepareDate(stringDate);

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
     */
    private TimeZone getTimeZone(String stringDate)
    {
        TimeZone timeZone = null;
        if (stringDate.startsWith("TZID"))
        {
            String timeZoneId = stringDate.substring(5, stringDate.indexOf(":"));
            timeZone = TimeZone.getTimeZone(timeZoneId);
        }
        else if (stringDate.endsWith("Z"))
        {
            timeZone = TimeZone.getTimeZone("GMT");
        }
        else
        {
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
     * Retrieve params from iCal text
     * 
     * @param params iCal params
     */
    private Map<String, String> getICalParams(String icalText)
    {
        Map<String, String> result = new HashMap<String, String>();
        String[] segregatedLines = icalText.split("\r\n");
        int attendeeNum = 0;
        Stack<String> stack = new Stack<String>();
        for (String line : segregatedLines)
        {
            String[] keyValue = line.split(":");
            if (keyValue.length >= 2)
            {
                if (keyValue[0].equals("BEGIN"))
                {
                    stack.push(keyValue[1]);
                    continue;
                }
                if (keyValue[0].equals("END"))
                {
                    stack.pop();
                    continue;
                }
                
                if (!stack.isEmpty() && stack.peek().equals("VEVENT"))
                {
                    if (keyValue[0].contains(";"))
                    {
                        keyValue[0] = keyValue[0].substring(0, keyValue[0].indexOf(";"));
                    }
                    if (keyValue[0].equals("ATTENDEE"))
                    {
                        keyValue[0] = keyValue[0] + attendeeNum;
                        attendeeNum++;
                    }
                    result.put(keyValue[0], keyValue[keyValue.length - 1]);
                }
            }
        }
        return result;
    }

    /**
     * Get last date of meeting
     * 
     * @param meeting The meeting bean ({@link MeetingBean})
     * @return date of last meeting
     */
    private Date getLastMeeting(MeetingBean meeting)
    {
        String[] ruleParams = meeting.getReccurenceRule().split(";");

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
            meeting.setReccurenceRule(meeting.getReccurenceRule() + ";INTERVAL=1");
            reloadEventParam = true;
        }

        if (eventParam.get("FREQ").equals("YEARLY"))
        {
            // Yearly is the same as monthly, we just increase interval
            String recRule = meeting.getReccurenceRule().replace("YEARLY", "MONTHLY");
            interval *= 12;
            recRule = recRule.replace("INTERVAL=" + eventParam.get("INTERVAL"), "INTERVAL=" + interval);

            if (eventParam.get("BYMONTHDAY") != null)
            {
                recRule = recRule.replace("BYMONTHDAY=" + eventParam.get("BYMONTHDAY") + ";", "");
                recRule = recRule + ";BYSETPOS=" + eventParam.get("BYMONTHDAY") + ";";
                recRule = recRule + "BYDAY=SU,MO,TU,WE,TH,FR,SA;";
            }
            meeting.setReccurenceRule(recRule);
            reloadEventParam = true;
        }

        if (eventParam.get("FREQ").equals("DAILY") && eventParam.get("BYDAY") != null)
        {
            String recRule = meeting.getReccurenceRule().replace("DAILY", "WEEKLY");
            meeting.setReccurenceRule(recRule);
            reloadEventParam = true;
        }

        if (reloadEventParam)
        {
            eventParam.clear();
            ruleParams = meeting.getReccurenceRule().split(";");

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
            return parseDate(eventParam.get("UNTIL"));
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