package org.alfresco.po.share.util;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class PageUtils.
 */
public class PageUtils
{
    private static final String regexProtocol = "\\w+\\:\\W+";
    private static final String regexShareUrl = "\\w+\\W?\\w+\\W?\\w+\\W?\\w+\\:?\\w+?\\/(share)";
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    /**
     * Is a WebElement usable?
     * 
     * @param element the WebElement
     * @return boolean
     */
    public static boolean usableElement(WebElement element)
    {
        if (element != null && element.isDisplayed() && element.isEnabled() 
                && !StringUtils.contains(element.getAttribute("class"), "dijitDisabled")
                && !StringUtils.contains(element.getAttribute("aria-disabled"), "true"))
        {
            return true;
        }
        return false;
    }

    /**
     * Method to return current protocol from the url
     *
     * @param shareUrl
     * @return String
     */
    public static String getProtocol(String shareUrl)
    {

        Pattern p1 = Pattern.compile(regexProtocol);
        Matcher m1 = p1.matcher(shareUrl);
        if (m1.find())
            return m1.group();
        else
            return new String("Cannot find protocol");
    }

    /**
     * Method to return Share Url without the protocol string (i.e pbld01.alfresco.com/share)
     *
     * @param shareUrl
     * @return String
     */
    public static String getShareUrl(String shareUrl)
    {

        Pattern p1 = Pattern.compile(regexShareUrl);
        Matcher m1 = p1.matcher(shareUrl);
        if (m1.find())
            return m1.group();
        else
            return new String("Cannot find Share url");
    }

    /**
     * Method to return Share Url without the protocol string (i.e pbld01.alfresco.com/share)
     * 
     * @param shareUrl
     * @return String
     */
    public static String getUrl(String shareUrl)
    {

        Pattern p1 = Pattern.compile(regexUrl);
        Matcher m1 = p1.matcher(shareUrl);
        if (m1.find())
            return m1.group();
        else
            return new String("Cannot find expected url");
    }
}