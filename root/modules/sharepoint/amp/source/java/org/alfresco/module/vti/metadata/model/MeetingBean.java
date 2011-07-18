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

package org.alfresco.module.vti.metadata.model;

import java.util.Date;
import java.util.List;

import org.alfresco.service.cmr.calendar.CalendarEntry;

/**
 * The meeting bean
 * 
 * TODO Tie this in with the new {@link CalendarEntry} code
 * 
 * @author StasSo
 */
public class MeetingBean
{
    private String location;
    private String subject;
    private Date startDate;
    private Date endDate;
    private Date lastMeetingDate;
    private String organizer;
    private String reccurenceRule;
    private List<String> attendees;
    private String id;

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public Date getLastMeetingDate()
    {
        return lastMeetingDate;
    }

    public void setLastMeetingDate(Date lastMeetingDate)
    {
        this.lastMeetingDate = lastMeetingDate;
    }

    public String getOrganizer()
    {
        return organizer;
    }

    public void setOrganizer(String organizer)
    {
        this.organizer = organizer;
    }

    public String getReccurenceRule()
    {
        return reccurenceRule;
    }

    public void setReccurenceRule(String reccurenceRule)
    {
        this.reccurenceRule = reccurenceRule;
    }

    public List<String> getAttendees()
    {
        return attendees;
    }

    public void setAttendees(List<String> attendees)
    {
        this.attendees = attendees;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}