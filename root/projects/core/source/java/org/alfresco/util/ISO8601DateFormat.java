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
import java.util.TimeZone;

import org.alfresco.error.AlfrescoRuntimeException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


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
   
    /**
     * Format date into ISO format (UCT0 / Zulu)
     * 
     * @param isoDate  the date to format
     * @return  the ISO Zulu timezone formatted string
     */
    public static String format(Date isoDate)
    {      	
    	DateTime dt = new DateTime(isoDate, DateTimeZone.UTC);
    	return dt.toString();
    }
    
    /**
     * Normalise isoDate time to Zulu(UTC0) time-zone, removing any UTC offset.
     * @param isoDate
     * @return the ISO Zulu timezone formatted string 
     * 			e.g 2011-02-04T17:13:14.000+01:00 -> 2011-02-04T16:13:14.000Z
     */
    public static String formatToZulu(String isoDate){
		try 
		{
			DateTime dt = new DateTime(isoDate, DateTimeZone.UTC);
			return dt.toString();
		} catch (IllegalArgumentException e) 
		{
			throw new AlfrescoRuntimeException("Failed to parse date " + isoDate, e);
		}
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
     * @param timezone The time zone, null means default time zone
     * @return  the date
     * @throws AlfrescoRuntimeException         if the parse failed
     */
    public static Date parseInternal(String isoDate, TimeZone timezone)
    {
		try 
		{
			// null time-zone defaults to the local time-zone
			DateTimeZone dtz = DateTimeZone.forTimeZone(timezone);
			DateTime dateTime = new DateTime(isoDate, dtz);
			Date date = dateTime.toDate();
			return date;
		} 
		catch (IllegalArgumentException e) 
		{
			throw new AlfrescoRuntimeException("Failed to parse date " + isoDate, e);
		}
    }
    
    /**
     * Checks whether or not the given ISO8601-formatted date-string contains a time-component
     * instead of only the actual date.
     * 
     * @param isoDate
     * @return true, if time is present.
     */
    public static boolean isTimeComponentDefined(String isoDate)
    {
    	boolean defined = false;
    	
    	if(isoDate != null && isoDate.length() > 11) 
    	{
    		// Find occurrence of T (sYYYY-MM-DDT..), sign is optional
    		int expectedLocation = 10;
    		if(isoDate.charAt(0) == '-' || isoDate.charAt(0) == '+') {
    			// Sign is included before year
    			expectedLocation++;
    		}
    		
    		defined = isoDate.length() >= expectedLocation && isoDate.charAt(expectedLocation) == 'T';
    	}
    	
    	return defined;
    }
    
    /**
     * Parses the given ISO8601-formatted date-string, not taking into account the time-component.
     * The time-information for the will be reset to zero.
     * 
     * @param isoDate the day (formatted sYYYY-MM-DD) or a full date (sYYYY-MM-DDThh:mm:ss.sssTZD)
     * @param timeZone the timezone to use
     * @return the parsed date
     * 
     * @throws AlfrescoRuntimeException if the parsing failed.
     */
    public static Date parseDayOnly(String isoDate, TimeZone timezone)
    {
    	try
    	{
    		if(isoDate != null && isoDate.length() >= 10) 
        	{   
        		int offset = 0;
        		
        		// Sign can be included before year
        		boolean bc = false;
        		if(isoDate.charAt(0) == '-')
        		{
        			bc = true;
        			offset++;
        		}
        		else if(isoDate.charAt(0) == '+')
        		{
        			offset++;
        		}
        		
        		// Extract year
                int year = Integer.parseInt(isoDate.substring(offset, offset += 4));
                if (isoDate.charAt(offset) != '-')
                {
                    throw new IndexOutOfBoundsException("Expected - character but found " + isoDate.charAt(offset));
                }
                
                // Extract month
                int month = Integer.parseInt(isoDate.substring(offset += 1, offset += 2));
                if (isoDate.charAt(offset) != '-')
                {
                    throw new IndexOutOfBoundsException("Expected - character but found " + isoDate.charAt(offset));
                }

                // Extract day
                int day = Integer.parseInt(isoDate.substring(offset += 1, offset += 2));
                
                Calendar calendar = new GregorianCalendar(timezone);
                calendar.setLenient(false);
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                if(bc)
                {
                	calendar.set(Calendar.ERA, GregorianCalendar.BC);
                }
                
                return calendar.getTime();
        	}
    		else
    		{
    			throw new AlfrescoRuntimeException("String passed is too short " + isoDate);
    		}
    	}
    	catch(IndexOutOfBoundsException e)
        {
            throw new AlfrescoRuntimeException("Failed to parse date " + isoDate, e);
        }
        catch(NumberFormatException e)
        {
            throw new AlfrescoRuntimeException("Failed to parse date " + isoDate, e);
        }
    }
        
    
}
