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
package org.alfresco.util;

import java.util.Date;
import java.util.TimeZone;

import org.alfresco.error.AlfrescoRuntimeException;

import junit.framework.TestCase;

public class ISO8601DateFormatTest extends TestCase
{
    public void testConversion()
    {
        String test = "2005-09-16T17:01:03.456+01:00";
        // convert to a date
        Date date = ISO8601DateFormat.parse(test);
        // get the string form
        String strDate = ISO8601DateFormat.format(date);
        // convert back to a date from the converted string
        Date dateAfter = ISO8601DateFormat.parse(strDate);
        // make sure the date objects match, test this instead of the
        // string as the string form will be different in different
        // locales
        assertEquals(date, dateAfter);
    }
    
    public void testMiliseconds()
    {
       // ALF-3803 bug fix, milliseconds are optional
       String testA   = "2005-09-16T17:01:03.456+01:00";
       String testB   = "2005-09-16T17:01:03+01:00";
       String testBms = "2005-09-16T17:01:03.000+01:00";
       
       Date dateA = ISO8601DateFormat.parse(testA);
       Date dateB = ISO8601DateFormat.parse(testB);
       
       assertEquals(testA, ISO8601DateFormat.format(dateA));
       assertEquals(testBms, ISO8601DateFormat.format(dateB));
    }
    
    public void testTimezones()
    {
       Date date = null;
       
       // A timezone is required by default
       date = ISO8601DateFormat.parse("2011-02-04T12:13:14Z");
       try
       {
          ISO8601DateFormat.parse("2011-02-04T12:13:14");
          fail("TimeZones are required");
       }
       catch(AlfrescoRuntimeException e) {}
       
       // You can specify one explicitly though
       TimeZone tz = TimeZone.getTimeZone("Europe/London");
       date = ISO8601DateFormat.parse("2011-02-04T12:13:14", tz);
       
       // The timezone will be used
       // Sydney is 9 hours ahead of UTC at that point
       tz = TimeZone.getTimeZone("Australia/Sydney");
       String testSydney = "2011-02-04T16:13:14";
       String testUTC    = "2011-02-04T05:13:14.000Z";
       
       date = ISO8601DateFormat.parse(testSydney, tz);
       assertEquals(testUTC, ISO8601DateFormat.format(date));
       
       // Check with ms too
       date = ISO8601DateFormat.parse(testSydney + ".000", tz);
       assertEquals(testUTC, ISO8601DateFormat.format(date));
    }
}
