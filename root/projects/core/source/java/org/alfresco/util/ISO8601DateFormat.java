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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.alfresco.error.AlfrescoRuntimeException;


/**
 * Formatting support for ISO 8601 dates
 * <pre>
 *    sYYYY-MM-DDThh:mm:ss.sssTZD
 * </pre>
 * where:
 * <ul>
 *   <li>sYYYY  Four-digit year with optional leading positive (<b>+</b>) or negative (<b>-</b>) sign. 
 *          A negative sign indicates a year BCE. The absence of a sign or the presence of a 
 *          positive sign indicates a year CE (for example, -0055 would indicate the year 55 BCE, 
 *          while +1969 and 1969 indicate the year 1969 CE).</li>
 *   <li>MM     Two-digit month (01 = January, etc.)</li>
 *   <li>DD     Two-digit day of month (01 through 31)</li>
 *   <li>hh     Two digits of hour (00 through 23)</li>
 *   <li>mm     Two digits of minute (00 through 59)</li>
 *   <li>ss.sss Seconds, to three decimal places (00.000 through 59.999)</li>
 *   <li>TZD    Time zone designator (either Z for Zulu, i.e. UTC, or +hh:mm or -hh:mm, i.e. an offset from UTC)</li>
 * </ul>
 */
public class ISO8601DateFormat
{
    private static final ThreadLocal<Map<String, TimeZone>> timezones;
    static
    {
        timezones = new ThreadLocal<Map<String,TimeZone>>();
    }
    
    /**
     * Format date into ISO format
     * 
     * @param isoDate  the date to format
     * @return  the ISO formatted string
     */
    public static String format(Date isoDate)
    {
        // Note: always serialise to Gregorian Calendar
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(isoDate);
        
        StringBuilder formatted = new StringBuilder(28);
        padInt(formatted, calendar.get(Calendar.YEAR), 4);
        formatted.append('-');
        padInt(formatted, calendar.get(Calendar.MONTH) + 1, 2);
        formatted.append('-');
        padInt(formatted, calendar.get(Calendar.DAY_OF_MONTH), 2);
        formatted.append('T');
        padInt(formatted, calendar.get(Calendar.HOUR_OF_DAY), 2);
        formatted.append(':');
        padInt(formatted, calendar.get(Calendar.MINUTE), 2);
        formatted.append(':');
        padInt(formatted, calendar.get(Calendar.SECOND), 2);
        formatted.append('.');
        padInt(formatted, calendar.get(Calendar.MILLISECOND), 3);
        
        TimeZone tz = calendar.getTimeZone();
        int offset = tz.getOffset(calendar.getTimeInMillis());
        if (offset != 0)
        {
            int hours = Math.abs((offset / (60 * 1000)) / 60);
            int minutes = Math.abs((offset / (60 * 1000)) % 60);
            formatted.append(offset < 0 ? '-' : '+');
            padInt(formatted, hours, 2);
            formatted.append(':');
            padInt(formatted, minutes, 2);
        } 
        else
        {
            formatted.append('Z');
        }
        
        return formatted.toString();
    }
    
    
    /**
     * Parse date from ISO formatted string. 
     * The ISO8601 date must include TimeZone offset information
     * 
     * @param isoDate  ISO string to parse
     * @return  the date
     * @throws AlfrescoRuntimeException         if the parse failed
     */
    public static Date parse(String isoDate)
    {
       return parseInternal(isoDate, null);
    }

    /**
     * Parse date from ISO formatted string, with an
     *  explicit timezone specified
     * 
     * @param isoDate  ISO string to parse
     * @param timezone The TimeZone the date is in
     * @return  the date
     * @throws AlfrescoRuntimeException         if the parse failed
     */
    public static Date parse(String isoDate, TimeZone timezone)
    {
       return parseInternal(isoDate, timezone);
    }
    
    /**
     * Parse date from ISO formatted string, either in the specified
     *  TimeZone, or with TimeZone information taken from the date
     * 
     * @param isoDate  ISO string to parse
     * @return  the date
     * @throws AlfrescoRuntimeException         if the parse failed
     */
    public static Date parseInternal(String isoDate, TimeZone timezone)
    {
        Date parsed = null;
        
        try
        {
            int offset = 0;
        
            // extract year
            int year = Integer.parseInt(isoDate.substring(offset, offset += 4));
            if (isoDate.charAt(offset) != '-')
            {
                throw new IndexOutOfBoundsException("Expected - character but found " + isoDate.charAt(offset));
            }
            
            // extract month
            int month = Integer.parseInt(isoDate.substring(offset += 1, offset += 2));
            if (isoDate.charAt(offset) != '-')
            {
                throw new IndexOutOfBoundsException("Expected - character but found " + isoDate.charAt(offset));
            }

            // extract day
            int day = Integer.parseInt(isoDate.substring(offset += 1, offset += 2));
            if (isoDate.charAt(offset) != 'T')
            {
                throw new IndexOutOfBoundsException("Expected T character but found " + isoDate.charAt(offset));
            }

            // extract hours, minutes, seconds and milliseconds
            int hour = Integer.parseInt(isoDate.substring(offset += 1, offset += 2));
            if (isoDate.charAt(offset) != ':')
            {
                throw new IndexOutOfBoundsException("Expected : character but found " + isoDate.charAt(offset));
            }
            int minutes = Integer.parseInt(isoDate.substring(offset += 1, offset += 2));
            if (isoDate.charAt(offset) != ':')
            {
                throw new IndexOutOfBoundsException("Expected : character but found " + isoDate.charAt(offset));
            }
            int seconds = Integer.parseInt(isoDate.substring(offset += 1 , offset += 2));
            int milliseconds = 0;
            if (isoDate.length() > offset && isoDate.charAt(offset) == '.')
            {
                // ALF-3803 bug fix, milliseconds are optional
                milliseconds = Integer.parseInt(isoDate.substring(offset += 1, offset += 3));
            }
            
            // Do we need to extract the timezone, or was it given?
            if(timezone == null)
            {
               // Extract timezone
               String timezoneId;
               char timezoneIndicator = isoDate.charAt(offset);
               if (timezoneIndicator == '+' || timezoneIndicator == '-')
               {
                   timezoneId = "GMT" + isoDate.substring(offset);
               }
               else if (timezoneIndicator == 'Z')
               {
                   timezoneId = "GMT";
               }
               else
               {
                   throw new IndexOutOfBoundsException("Invalid time zone indicator " + timezoneIndicator);
               }
               
               // Get the timezone
               Map<String, TimeZone> timezoneMap = timezones.get();
               if (timezoneMap == null)
               {
                   timezoneMap = new HashMap<String, TimeZone>(3);
                   timezones.set(timezoneMap);
               }
               timezone = timezoneMap.get(timezoneId);
               if (timezone == null)
               {
                   timezone = TimeZone.getTimeZone(timezoneId);
                   timezoneMap.put(timezoneId, timezone);
               }
               if (!timezone.getID().equals(timezoneId))
               {
                   throw new IndexOutOfBoundsException();
               }
            }

            // initialize Calendar object#
            // Note: always de-serialise from Gregorian Calendar
            Calendar calendar = new GregorianCalendar(timezone);
            calendar.setLenient(false);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, seconds);
            calendar.set(Calendar.MILLISECOND, milliseconds);

            // extract the date
            parsed = calendar.getTime();
        }
        catch(IndexOutOfBoundsException e)
        {
            throw new AlfrescoRuntimeException("Failed to parse date " + isoDate, e);
        }
        catch(NumberFormatException e)
        {
            throw new AlfrescoRuntimeException("Failed to parse date " + isoDate, e);
        }
        catch(IllegalArgumentException e)
        {
            throw new AlfrescoRuntimeException("Failed to parse date " + isoDate, e);
        }
        
        return parsed;
    }
    
    /**
     * Helper to zero pad a number to specified length 
     */
    private static void padInt(StringBuilder buffer, int value, int length)
    {
        String strValue = Integer.toString(value);
        for (int i = length - strValue.length(); i > 0; i--)
        {
            buffer.append('0');
        }
        buffer.append(strValue);
    }
}
