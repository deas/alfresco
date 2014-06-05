package org.alfresco.po.share.site.links;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Marina.Nenadovets
 *
 */
public class LinkDirectoryInfo extends HtmlElement
{
    private static final By EDIT_LINK = By.cssSelector(".edit-link>a");
    private static final By DELETE_LINK = By.cssSelector(".delete-link>a");
    /**
     * Constructor
     */
    protected LinkDirectoryInfo(WebDrone drone, WebElement webElement)
    {
        super(webElement,drone);
    }

    /**
     * Method to click Edit
     *
     * @return AddLink form
     */
    public AddLinkForm clickEdit ()
    {
        findAndWait(EDIT_LINK).click();
        return new AddLinkForm(drone).render();
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

    /**
     * Method to verify whether edit link is displayed
     *
     * @return boolean
     */
    public boolean isEditDisplayed()
    {
        return findElement(EDIT_LINK).isDisplayed();
    }

    /**
     * Method to verify whether delete link is displayed
     *
     * @return boolean
     */
    public boolean isDeleteDisplayed()
    {
        return findElement(DELETE_LINK).isDisplayed();
    }
}
