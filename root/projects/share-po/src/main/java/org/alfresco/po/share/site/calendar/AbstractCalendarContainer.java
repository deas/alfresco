package org.alfresco.po.share.site.calendar;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Abstract of Calendar Container for Calendar Event form
 * 
 * @author Sergey Kardash
 */
public class AbstractCalendarContainer extends AbstractEventForm
{
    private Log logger = LogFactory.getLog(this.getClass());

    private final static String DATE_BUTTON = "//table[@id='buttoncalendar']//a[text()='%s']";

    protected AbstractCalendarContainer(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractCalendarContainer render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    public AbstractCalendarContainer render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractCalendarContainer render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to click on any element by its locator
     * 
     * @param locator
     */
    public void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    /**
     * Method to check if the element is displayed
     * 
     * @param locator
     * @return boolean
     */
    public boolean isDisplayed(By locator)
    {
        try
        {
            return drone.findAndWait(locator, 2000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to set date
     * 
     * @param date
     */
    public void setDate(String date)
    {
        if (date == null)
        {
            throw new IllegalArgumentException("Date is required");
        }

        try
        {
            String dateXpath = String.format(DATE_BUTTON, date);
            WebElement element = drone.findAndWait(By.xpath(dateXpath));
            element.click();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find the date button ", te);
            }
        }
    }

}
