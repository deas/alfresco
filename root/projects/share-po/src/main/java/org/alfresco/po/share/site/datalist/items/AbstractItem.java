package org.alfresco.po.share.site.datalist.items;

import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * An abstract of data list item
 *
 * @author Marina.Nenadovets
 */

public abstract class AbstractItem extends ShareDialogue
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By SAVE_BTN = By.cssSelector("button[id$='form-submit-button']");
    private static final By FORM_FIELDS = By.cssSelector(".form-field>input");


    protected AbstractItem(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractItem render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractItem render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractItem render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to set input in the field
     *
     * @param input
     * @param value
     */
    public void setInput(final WebElement input, final String value)
    {
        try
        {
            input.clear();
            input.sendKeys(value);
        }
        catch (NoSuchElementException e)
        {
            throw new ShareException("Unable to find the input fields");
        }
    }

    /**
     * Method to fill all the fields of an item
     *
     * @param data
     */
    public void fillItemFields(String data)
    {
        try
        {
            List<WebElement> formFields = drone.findAndWaitForElements(FORM_FIELDS);
            for (int i = 0; i < formFields.size(); i++)
            {
                setInput(formFields.get(i), data);
            }
        }
        catch (TimeoutException te)
        {
            logger.error("smth wrong with item fields method");
        }
    }

    /**
     * Method for clicking Save button
     */
    public void clickSave()
    {
        try
        {
            drone.findAndWait(SAVE_BTN).click();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Save button isn't displayed!");
        }
    }
    /**
     * Method for editing an item
     *
     * @param newTitle
     */
    public void editAnItem (String newTitle)
    {
        fillItemFields(newTitle);
        clickSave();
        waitUntilAlert();
    }
}
