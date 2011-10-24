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
import org.alfresco.service.cmr.calendar.CalendarEntryDTO;

/**
 * The meeting bean, which is an extension of the regular
 *  {@link CalendarEntry} object with extra information on
 *  the organiser and attendees
 */
public class MeetingBean extends CalendarEntryDTO
{
    private String organizer;
    private List<String> attendees;
    
    public MeetingBean()
    {
        super();
        
        // We're always Outlook based
        setOutlook(true);
    }

    /**
     * Returns the Subject (title)
     */
    public String getSubject()
    {
        return getTitle();
    }

    /**
     * Sets the Subject (title)
     */
    public void setSubject(String subject)
    {
        setTitle(subject);
    }
    
    public Date getStartDate()
    {
        return getStart(); 
    }
    public Date getEndDate()
    {
        return getEnd();
    }

    public String getOrganizer()
    {
        return organizer;
    }

    public void setOrganizer(String organizer)
    {
        this.organizer = organizer;
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
        return getOutlookUID();
    }

    public void setId(String id)
    {
        setOutlookUID(id);
    }
}