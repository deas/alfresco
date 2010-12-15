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

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Provides <b>thread safe</b> means of obtaining a cached date formatter.
 * <p>
 * The cached string-date mappings are stored in a <tt>WeakHashMap</tt>.
 * 
 * @see java.text.DateFormat#setLenient(boolean)
 * 
 * @author Derek Hulley
 */
public class CachingDateFormat extends SimpleDateFormat
{
    private static final long serialVersionUID = 3258415049197565235L;

    /** <pre> yyyy-MM-dd'T'HH:mm:ss </pre> */
    public static final String FORMAT_FULL_GENERIC = "yyyy-MM-dd'T'HH:mm:ss";

    /** <pre> yyyy-MM-dd'T'HH:mm:ss </pre> */
    public static final String FORMAT_CMIS_SQL = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final String[] LENIENT_FORMATS = new String[] {"yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd'T'HH", "yyyy-MM-dd'T'",
        "yyyy-MMM-dd'T'HH:mm:ss.SSS", "yyyy-MMM-dd", "yyyy-MMM-dd'T'HH:mm:ss.SSSZ", "yyyy-MMM-dd'T'HH:mm:ss", "yyyy-MMM-dd'T'HH:mm", "yyyy-MMM-dd'T'HH", "yyyy-MMM-dd'T'"
    };
    
    /** <pre> yyyy-MM-dd </pre> */
    public static final String FORMAT_DATE_GENERIC = "yyyy-MM-dd";

    /** <pre> HH:mm:ss </pre> */
    public static final String FORMAT_TIME_GENERIC = "HH:mm:ss";

    private static ThreadLocal<SimpleDateFormat> s_localDateFormat = new ThreadLocal<SimpleDateFormat>();
    
    private static ThreadLocal<SimpleDateFormat> s_localDateOnlyFormat = new ThreadLocal<SimpleDateFormat>();

    private static ThreadLocal<SimpleDateFormat> s_localTimeOnlyFormat = new ThreadLocal<SimpleDateFormat>();
    
    private static ThreadLocal<SimpleDateFormat> s_localCmisSqlDatetime = new ThreadLocal<SimpleDateFormat>();
    
    private static ThreadLocal<SimpleDateFormat[]> s_lenientParsers = new ThreadLocal<SimpleDateFormat[]>();

    transient private Map<String, Date> cacheDates = new WeakHashMap<String, Date>(89);

    private CachingDateFormat(String format)
    {
        super(format);
    }

    public String toString()
    {
        return this.toPattern();
    }

    /**
     * @param length
     *            the type of date format, e.g. {@link CachingDateFormat#LONG }
     * @param locale
     *            the <code>Locale</code> that will be used to determine the
     *            date pattern
     * 
     * @see #getDateFormat(String, boolean)
     * @see CachingDateFormat#SHORT
     * @see CachingDateFormat#MEDIUM
     * @see CachingDateFormat#LONG
     * @see CachingDateFormat#FULL
     */
    public static SimpleDateFormat getDateFormat(int length, Locale locale, boolean lenient)
    {
        SimpleDateFormat dateFormat = (SimpleDateFormat) CachingDateFormat.getDateInstance(length, locale);
        // extract the format string
        String pattern = dateFormat.toPattern();
        // we have a pattern to use
        return getDateFormat(pattern, lenient);
    }

    /**
     * @param dateLength
     *            the type of date format, e.g. {@link CachingDateFormat#LONG }
     * @param timeLength
     *            the type of time format, e.g. {@link CachingDateFormat#LONG }
     * @param locale
     *            the <code>Locale</code> that will be used to determine the
     *            date pattern
     * 
     * @see #getDateFormat(String, boolean)
     * @see CachingDateFormat#SHORT
     * @see CachingDateFormat#MEDIUM
     * @see CachingDateFormat#LONG
     * @see CachingDateFormat#FULL
     */
    public static SimpleDateFormat getDateTimeFormat(int dateLength, int timeLength, Locale locale, boolean lenient)
    {
        SimpleDateFormat dateFormat = (SimpleDateFormat) CachingDateFormat.getDateTimeInstance(dateLength, timeLength, locale);
        // extract the format string
        String pattern = dateFormat.toPattern();
        // we have a pattern to use
        return getDateFormat(pattern, lenient);
    }

    /**
     * @param pattern
     *            the conversion pattern to use
     * @param lenient
     *            true to allow the parser to extract the date in conceivable
     *            manner
     * @return Returns a conversion-cacheing formatter for the given pattern,
     *         but the instance itself is not cached
     */
    public static SimpleDateFormat getDateFormat(String pattern, boolean lenient)
    {
        // create an alfrescoDateFormat for cacheing purposes
        SimpleDateFormat dateFormat = new CachingDateFormat(pattern);
        // set leniency
        dateFormat.setLenient(lenient);
        // done
        return dateFormat;
    }

    /**
     * @return Returns a thread-safe formatter for the generic date/time format
     * 
     * @see #FORMAT_FULL_GENERIC
     */
    public static SimpleDateFormat getDateFormat()
    {
        if (s_localDateFormat.get() != null)
        {
            return s_localDateFormat.get();
        }

        CachingDateFormat formatter = new CachingDateFormat(FORMAT_FULL_GENERIC);
        // it must be strict
        formatter.setLenient(false);
        // put this into the threadlocal object
        s_localDateFormat.set(formatter);
        // done
        return s_localDateFormat.get();
    }
    
    /**
     * @return Returns a thread-safe formatter for the cmis sql datetime format
     */
    public static SimpleDateFormat getCmisSqlDatetimeFormat()
    {
        if (s_localCmisSqlDatetime.get() != null)
        {
            return s_localCmisSqlDatetime.get();
        }

        CachingDateFormat formatter = new CachingDateFormat(FORMAT_CMIS_SQL);
        // it must be strict
        formatter.setLenient(false);
        // put this into the threadlocal object
        s_localCmisSqlDatetime.set(formatter);
        // done
        return s_localCmisSqlDatetime.get();
    }

    /**
     * @return Returns a thread-safe formatter for the generic date format
     * 
     * @see #FORMAT_DATE_GENERIC
     */
    public static SimpleDateFormat getDateOnlyFormat()
    {
        if (s_localDateOnlyFormat.get() != null)
        {
            return s_localDateOnlyFormat.get();
        }

        CachingDateFormat formatter = new CachingDateFormat(FORMAT_DATE_GENERIC);
        // it must be strict
        formatter.setLenient(false);
        // put this into the threadlocal object
        s_localDateOnlyFormat.set(formatter);
        // done
        return s_localDateOnlyFormat.get();
    }

    /**
     * @return Returns a thread-safe formatter for the generic time format
     * 
     * @see #FORMAT_TIME_GENERIC
     */
    public static SimpleDateFormat getTimeOnlyFormat()
    {
        if (s_localTimeOnlyFormat.get() != null)
        {
            return s_localTimeOnlyFormat.get();
        }

        CachingDateFormat formatter = new CachingDateFormat(FORMAT_TIME_GENERIC);
        // it must be strict
        formatter.setLenient(false);
        // put this into the threadlocal object
        s_localTimeOnlyFormat.set(formatter);
        // done
        return s_localTimeOnlyFormat.get();
    }

    /**
     * Parses and caches date strings.
     * 
     * @see java.text.DateFormat#parse(java.lang.String,
     *      java.text.ParsePosition)
     */
    public Date parse(String text, ParsePosition pos)
    {
        Date cached = cacheDates.get(text);
        if (cached == null)
        {
            Date date = super.parse(text, pos);
            if ((date != null) && (pos.getIndex() == text.length()))
            {
                cacheDates.put(text, date);
                Date clonedDate = (Date) date.clone();
                return clonedDate;
            }
            else
            {
                return date;
            }
        }
        else
        {
            pos.setIndex(text.length());
            Date clonedDate = (Date) cached.clone();
            return clonedDate;
        }
    }
    
    public static Date lenientParse(String text) throws ParseException
    {
        SimpleDateFormat[] formatters = getLenientFormatters();
        for(SimpleDateFormat formatter : formatters)
        {
            try
            {
                return formatter.parse(text);
            }
            catch (ParseException e)
            {
                continue;
            }
        }
        throw new ParseException("Unknown date format", 0);
    }
    
    public static SimpleDateFormat[] getLenientFormatters()
    {
        if (s_lenientParsers.get() != null)
        {
            return s_lenientParsers.get();
        }

        int i = 0;
        SimpleDateFormat[] formatters = new SimpleDateFormat[LENIENT_FORMATS.length];
        for(String format : LENIENT_FORMATS)
        {
            CachingDateFormat formatter = new CachingDateFormat(format);
            // it must be strict
            formatter.setLenient(false);
            formatters[i++] = formatter;
        }
       
        // put this into the threadlocal object
        s_lenientParsers.set(formatters);
        // done
        return s_lenientParsers.get();
    }
    
}
