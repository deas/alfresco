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

import org.alfresco.service.cmr.calendar.CalendarTimezoneHelper;

/**
 * Information on a TimeZone to be used by an Event or a Meeting Workspace.
 * This is simple and POJO based, to facilitate easy creation from
 *  Web Service requests.
 * When working with iCal feeds, you would more usually work with 
 *  {@link CalendarTimezoneHelper}, which is able to generate
 *  Java TimeZone objects.
 * TODO When fully implemented on the WS side, offer to create
 *  Java TimeZone objects too
 */
public class TimeZoneInformation
{
    private String id;
    
    private int bias;

    private TimeZoneInformationDate standardDate;

    private int standardBias;

    private TimeZoneInformationDate daylightDate;

    private int daylightBias;
    
    /**
     * @return The TimeZone ID, eg "Canberra, Melbourne, Sydney"
     */
    public String getID()
    {
        return id;
    }
    
    public void setID(String id)
    {
        this.id = id;
    }

    public int getBias()
    {
        return bias;
    }

    public void setBias(int bias)
    {
        this.bias = bias;
    }

    public TimeZoneInformationDate getStandardDate()
    {
        return standardDate;
    }

    public void setStandardDate(TimeZoneInformationDate standardDate)
    {
        this.standardDate = standardDate;
    }

    public int getStandardBias()
    {
        return standardBias;
    }

    public void setStandardBias(int standardBias)
    {
        this.standardBias = standardBias;
    }

    public TimeZoneInformationDate getDaylightDate()
    {
        return daylightDate;
    }

    public void setDaylightDate(TimeZoneInformationDate daylightDate)
    {
        this.daylightDate = daylightDate;
    }

    public int getDaylightBias()
    {
        return daylightBias;
    }

    public void setDaylightBias(int daylightBias)
    {
        this.daylightBias = daylightBias;
    }
}