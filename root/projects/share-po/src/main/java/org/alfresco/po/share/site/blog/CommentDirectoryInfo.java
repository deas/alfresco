package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Holds html elements related to Blog Comment Directory info
 *
 * @author Marina.Nenadovets
 */
public class CommentDirectoryInfo extends HtmlElement
{
    private static final By EDIT_LINK = By.cssSelector("a[class$='edit-comment']");
    private static final By DELETE_LINK = By.cssSelector("a[class$='delete-comment']");

    /**
     * Constructor
     */
    protected CommentDirectoryInfo(WebDrone drone, WebElement webElement)
    {
        super(webElement, drone);
    }

    /**
     * Method to click Edit
     */
    public BlogCommentForm clickEdit()
    {
        try
        {
            findAndWait(EDIT_LINK).click();
            return new BlogCommentForm(drone);
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + EDIT_LINK);
        }
    }

    /**
     * Method to click Delete
     */
    public void clickDelete()
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
