package org.alfresco.po.share.site.datalist;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

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

/**
 * New List form page object
 *
 * @author Marina.Nenadovets
 */

public class NewListForm extends ShareDialogue
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By SAVE_BTN = By.cssSelector("button[id$=submit-button]");
    private static final By CANCEL_BTN = By.cssSelector("button[id$=cancel-button]");
    @SuppressWarnings("unused")
    private static final By LISTS_TYPES_CONTAINER = By.cssSelector("div[id$='itemTypesContainer']>div");
    private static final By TITLE_FIELD = By.cssSelector("input[id$='cm_title']");
    private static final By DESCRIPTION_FIELD = By.cssSelector("textarea[id$='cm_description']");

    public NewListForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewListForm render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(SAVE_BTN),
            getVisibleRenderElement(CANCEL_BTN));

        return this;
    }

    @SuppressWarnings("unchecked")
    public NewListForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewListForm render(long time)
    {
        return render(new RenderTime(time));
    }

    public static enum TypeOptions
    {
        CONTACT_LIST(0),
        EVENT_LIST(1),
        ISSUE_LIST(2),
        LOCATION_LIST(3),
        MEETING_AGENDA(4),
        TASK_LIST_ADV(5),
        TASK_LIST_SPL(6),
        TO_DO_LIST(7);

        public final int numberPosition;

        TypeOptions(int numberPosition)
        {
            this.numberPosition = numberPosition;
        }
    }

    /**
     * Method to set input in the fields of New List Form
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
            logger.debug("Unable to find " + input);
        }
    }

    /**
     * Method to set input into the Title field
     *
     * @param name
     */
    public void inputTitleField(String name)
    {
        setInput(drone.findAndWait(TITLE_FIELD), name);
    }

    /**
     * Method to set input into the Description field
     *
     * @param description
     */
    public void inputDescriptionField(String description)
    {
        setInput(drone.findAndWait(DESCRIPTION_FIELD), description);
    }

    /**
     * Method for clicking Save button
     *
     * @return Data List Page object
     */
    public DataListPage clickSave()
    {
        try
        {
            drone.findAndWait(SAVE_BTN).click();
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new DataListPage(drone);
    }

    /**
     * Method for clickin cancel button
     *
     * @return DataListPage
     */
    public DataListPage clickCancel()
    {
        try
        {
            drone.findAndWait(CANCEL_BTN).click();
            return new DataListPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("unable to find " + CANCEL_BTN);
        }
    }
}
