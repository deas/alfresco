package org.alfresco.po.share.site.discussions;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Holds html elements related to Reply Directory info
 *
 * @author Marina.Nenadovets
 *
 */
public class ReplyDirectoryInfo extends HtmlElement
{
    private static final By EDIT_LINK = By.cssSelector(".onEditReply>a");
    private static final By DELETE_LINK = By.cssSelector(".onDeleteReply>a");
    /**
     * Constructor
     */
    protected ReplyDirectoryInfo(WebDrone drone, WebElement webElement)
    {
        super(webElement,drone);
    }

    /**
     * Method to click Edit
     */
    public void clickEdit ()
    {
        try
        {
            findAndWait(EDIT_LINK).click();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + EDIT_LINK);
        }
    }

    /**
     * Method to click Delete
     */
    public void clickDelete ()
    {
        try
        {
            findAndWait(DELETE_LINK).click();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + DELETE_LINK);
        }
    }
}
