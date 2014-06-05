package org.alfresco.share.util;

import org.alfresco.po.share.util.PageUtils;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergey Kardash
 */
public class ShareUserCalendar extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(ShareUserCalendar.class);

    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    public ShareUserCalendar()
    {
        if (logger.isTraceEnabled())
        {
            logger.debug(this.getClass().getSimpleName() + " instantiated");
        }

    }

    /**
     * @param in
     * @return IP from string by format in accordance with pattern
     */
    private static String findIP(String in)
    {
        Matcher m = Pattern.compile(regexUrl).matcher(in);
        if (m.find())
        {
            return m.group(0);
        }
        return null;
    }
}
