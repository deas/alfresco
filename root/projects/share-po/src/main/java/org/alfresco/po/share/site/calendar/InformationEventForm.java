package org.alfresco.po.share.site.calendar;

import org.alfresco.po.share.exception.ShareException;
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
    private final static By TAG = By.xpath("//div[text()='Tags:']/following-sibling::div");
    private final static By WHAT_DETAIL = By.xpath("//div[contains(text(),'What:')]/following-sibling::div");
    private final static By WHERE_DETAIL = By.xpath("//div[contains(text(),'Where:')]/following-sibling::div");
    private final static By DESCRIPTION_DETAIL = By.xpath("//div[contains(text(),'Description:')]/following-sibling::div");
    private final static By OK_BUTTON = By.cssSelector("button[id$='_defaultContainer-cancel-button-button']");

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

    @SuppressWarnings("unchecked")
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

    /**
     * Method to verify whether Edit button is present
     *
     * @return boolean
     */
    public boolean isEditButtonPresent ()
    {
        boolean isPresent = false;
        try
        {
            isPresent = drone.isElementDisplayed(EDIT_BUTTON);
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        return isPresent;
    }

    /**
     * Method to verify whether Delete button is present
     *
     * @return boolean
     */
    public boolean isDeleteButtonPresent ()
    {
        boolean isPresent = false;
        try
        {
            isPresent = drone.isElementDisplayed(DELETE_BUTTON);
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        return isPresent;
    }

    /**
     * Method to retrieve tag added to Calendar Event
     * 
     * @return String
     */
    public String getTagName()
    {
        try
        {
            String tagName = drone.findAndWait(TAG).getText();
            if (!tagName.isEmpty())
                return tagName;
            else
                throw new IllegalArgumentException("Cannot find tag");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the tag");
        }
    }

    /**
     * Method to retrieve what Detail added to Calendar Event
     *
     * @return String
     */
    public String getWhatDetail()
    {
        try
        {
            String whatDetail = drone.findAndWait(WHAT_DETAIL).getText();
            if (!whatDetail.isEmpty())
                return whatDetail;
            else
                throw new IllegalArgumentException("Cannot find what Detail");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the what Detail");
        }
    }

    /**
     * Method to retrieve where Detail added to Calendar Event
     *
     * @return String
     */
    public String getWhereDetail()
    {
        try
        {
            String whatDetail = drone.findAndWait(WHERE_DETAIL).getText();
            if (!whatDetail.isEmpty())
                return whatDetail;
            else
                throw new IllegalArgumentException("Cannot find where Detail");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the where Detail");
        }
    }

    /**
     * Method to retrieve description Detail added to Calendar Event
     *
     * @return String
     */
    public String getDescriptionDetail()
    {
        try
        {
            String whatDetail = drone.findAndWait(DESCRIPTION_DETAIL).getText();
            if (!whatDetail.isEmpty())
                return whatDetail;
            else
                throw new IllegalArgumentException("Cannot find description Detail");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the description Detail");
        }
    }

    /**
     * Method for close information event form
     *
     * @return
     */
    public CalendarPage closeInformationForm()
    {
        try
        {
            drone.findAndWait(OK_BUTTON).click();
            logger.info("Click ok event button");
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to locate ok button information event form");
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        return drone.getCurrentPage().render();
    }
}
