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
package org.alfresco.module.vti.handler.alfresco;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * Utils class for alfresco handlers that implement frontpage protocol
 * 
 * @author Dmitry Lazurkin
 */
public class VtiUtils
{
    public final static String HEADER_USER_AGENT = "User-Agent";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    private static final SimpleDateFormat propfindDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
    private static final SimpleDateFormat versionDateFormat = new SimpleDateFormat("M/d/yyyy h:mm a", Locale.ENGLISH);
    private static final SimpleDateFormat browserDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
    
    private static Pattern macClientPattern = Pattern.compile(".*Microsoft Document Connection.*");
    private static Pattern validNamePattern = Pattern.compile("[^#]+");
    
    static
    {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        propfindDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        browserDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Convert FrontPageExtension version string to alfresco version label. For FrontPageExtension version string minor number is optional, but for alfresco version label it's
     * required
     * 
     * @param docVersion FrontPageExtension version string
     * @return alfresco version label
     */
    public static String toAlfrescoVersionLabel(String docVersion)
    {
        if (docVersion.indexOf(".") == -1)
        {
            docVersion += ".0"; // add minor number to version label
        }

        return docVersion;
    }

    /**
     * Convert FrontPageExtension lock timeout to Alfresco lock timeout. FrontPageExtension timeout is number of minutes, but Alfresco timeout is number of seconds.
     * 
     * @param timeout FrontPageExtension lock timeout
     * @return Alfresco lock timeout
     */
    public static int toAlfrescoLockTimeout(int timeout)
    {
        return timeout * 60;
    }

    /**
     * Format date
     * 
     * @param date input date
     * @return String formated date
     */
    public static String formatDate(Date date)
    {
        return dateFormat.format(date);
    }

    /**
     * Format version date
     * 
     * @param date input date
     * @return String formated version date
     */
    public static String formatVersionDate(Date date)
    {
        return versionDateFormat.format(date);
    }

    /**
     * Format propfind date
     * 
     * @param date input date
     * @return String formated propfind date
     */
    public static String formatPropfindDate(Date date)
    {
        return propfindDateFormat.format(date);
    }
    
    /**
     * Format browser date
     * 
     * @param date input date
     * @return String formated browser date
     */
    public static String formatBrowserDate(Date date)
    {
        return browserDateFormat.format(date);
    }

    /**
     * Compare dates
     * 
     * @param date input date
     * @param dateString input date in string
     * @return <b>true</b> if date and dateString equals, <b>false</b> otherwise
     */
    public static boolean compare(Date date, String dateString)
    {
        return dateString.replaceAll("-0000", "+0000").equals(dateFormat.format(date));
    }
    
    public static boolean hasIllegalCharacter(String value)
    {
        Matcher matcher = validNamePattern.matcher(value);
        return !matcher.matches();
    }
    
    public static String convertToPropfindFormat(String dateInVtiFormat)
    {
        try
        {
            return propfindDateFormat.format(dateFormat.parse(dateInVtiFormat));
        }
        catch (ParseException e)
        {
            return dateInVtiFormat;
        }
    }
    
    /**
     * 
     * @return the amount of time in minutes to deduct from local time to get UTC.
     */
    public static int getServerOffset()
    {
        int offset = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 60000;
        
        return -offset;
    }
    
    public static String convertDateToVersion(Date date)
    {
        return (Long.toString(date.getTime())).substring(0, 11);
    }

    /**
     * Generates a SharePoint style ETag for the resource,
     *  based on its GUID and last modified date
     */
    public static String constructETag(String guid, Date lastModified)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("\"{");
        sb.append(guid);
        sb.append("},");
        sb.append(VtiUtils.convertDateToVersion(lastModified));
        sb.append("\"");

        return sb.toString();
    }

    /**
     * Generate a SharePoint style Resource Tag (RTag) for
     *  the resource, based on its GUID and last modified date.
     * This contains the same information as the ETag, but
     *  is formatted differently. 
     */
    public static String constructResourceTag(String guid, Date lastModified)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("rt:");
        sb.append(guid);
        sb.append("@");
        sb.append(VtiUtils.convertDateToVersion(lastModified));

        return sb.toString();
    }
    
    public static String constructRid(String guid)
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("rid:{");
        sb.append(guid);
        sb.append("}");

        return sb.toString();
    }
    
    public static boolean isMacClientRequest(HttpServletRequest request)
    {
        String userAgent = request.getHeader(HEADER_USER_AGENT);
        
        // TODO Should we do this check only once, and cache it?
        return (userAgent != null && macClientPattern.matcher(userAgent).matches());
    }
}
