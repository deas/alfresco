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

/**
 * @author PavelYur
 */
public class TimeZoneInformation
{
    private int bias;

    private TimeZoneInformationDate standardDate;

    private int standardBias;

    private TimeZoneInformationDate daylightDate;

    private int daylightBias;

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