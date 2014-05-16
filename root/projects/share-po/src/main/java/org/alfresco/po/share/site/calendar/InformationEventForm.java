package org.alfresco.po.share.site.calendar;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * @author Sergey Kardash
 */
public class InformationEventForm extends AbstractEventForm
{
    private Log logger = LogFactory.getLog(this.getClass());

    private final static By EDIT_BUTTON = By.cssSelector("button[id$='edit-button-button']");
    private final static By DELETE_BUTTON = By.cssSelector("button[id$='delete-button-button']");

    public InformationEventForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public InformationEventForm render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    public InformationEventForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public InformationEventForm render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for click on 'Edit' on information event form
     * 
     * @return
     */
    public EditEventForm clickOnEditEvent()
    {
        try
        {
            drone.findAndWait(EDIT_BUTTON).click();
            logger.info("Click edit event button");
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to locate edit Event button");
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        return new EditEventForm(drone);
    }

    /**
     * Method for click on 'Delete' on information event form
     * 
     * @return
     */
    public DeleteEventForm clickOnDeleteEvent()
    {
        try
        {
            drone.findAndWait(DELETE_BUTTON).click();
            logger.info("Click delete event button");
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to locate delete Event button");
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        return new DeleteEventForm(drone);
    }
}
