package org.alfresco.po.share.site.links;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * An abstract of Link form
 *
 * @author Marina.Nenadovets
 */
public abstract class AbstractLinkForm extends SharePage
{
    @SuppressWarnings("unused")
    private Log logger = LogFactory.getLog(this.getClass());

    protected static final By TITLE_FIELD = By.cssSelector("input[id$='default-title']");
    protected static final By URL_FIELD = By.cssSelector("input[id$='default-url']");
    protected static final By DESCRIPTION_FIELD = By.cssSelector("textarea[id$='default-description']");
    protected static final By INTERNAL_CHKBOX = By.cssSelector("input[id$='default-internal']");
    protected static final By CANCEL_BTN = By.cssSelector("button[id$='default-cancel-button']");

    /**
     * Constructor
     *
     * @param drone
     */
    protected AbstractLinkForm(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Method for setting an input into the field
     *
     * @param input
     * @param value
     */
    private void setInput(final WebElement input, final String value)
    {
        try
        {
            input.clear();
            input.sendKeys(value);
        }
        catch (NoSuchElementException e)
        {
            throw new ShareException("Unable to find " + input);
        }
    }

    protected void setTitleField(final String title)
    {
        setInput(drone.findAndWait(TITLE_FIELD), title);
    }

    protected void setUrlField(final String title)
    {
        setInput(drone.findAndWait(URL_FIELD), title);
    }

    protected void setDescriptionField(final String title)
    {
        setInput(drone.findAndWait(DESCRIPTION_FIELD), title);
    }

    protected void setInternalChkbox()
    {
        drone.findAndWait(INTERNAL_CHKBOX).click();
    }

    /**
     * Method for clicking Cancel button
     *
     * @param title
     */
    protected void clickCancelBtn(final String title)
    {
        try
        {
            drone.findAndWait(CANCEL_BTN).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find " + CANCEL_BTN);
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Timed out finding " + CANCEL_BTN);
        }
    }

}
