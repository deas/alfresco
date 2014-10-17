package org.alfresco.po.share.dashlet;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.*;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Page object to hold Site Calendar dashlet
 * 
 * @author Marina.Nenadovets
 */
public class SiteCalendarDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.calendar");
    private static final By EVENTS_LINKS = By.cssSelector(".details2>div>span>a");
    private static final By EVENTS_DETAILS = By.cssSelector(".details2>div>span");

    /**
     * Constructor.
     */
    protected SiteCalendarDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector("div.dashlet.calendar .yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized SiteCalendarDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                timer.start();
                synchronized (this)
                {
                    try
                    {
                        this.wait(50L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                try
                {
                    scrollDownToDashlet();
                    getFocus();
                    this.dashlet = drone.findAndWait((DASHLET_CONTAINER_PLACEHOLDER), 100L, 10L);
                    break;
                }
                catch (NoSuchElementException e)
                {

                }
                catch (StaleElementReferenceException ste)
                {
                    // DOM has changed therefore page should render once change
                    // is completed
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find site notice dashlet", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteCalendarDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteCalendarDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method gets the focus by placing mouse over on Site Calendar Dashlet.
     */
    protected void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    private List<WebElement> getEventLinksElem()
    {
        try
        {
            return dashlet.findElements(EVENTS_LINKS);
        }
        catch (StaleElementReferenceException e)
        {
            return getEventLinksElem();
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }

    /**
     * Gets count of Events displayed in Dashlet
     * 
     * @return
     */
    public int getEventsCount()
    {
        return getEventLinksElem().size();
    }

    /**
     * Return true if link with eventName Displayed.
     * 
     * @param eventName
     * @return
     */
    public boolean isEventsDisplayed(String eventName)
    {
        checkNotNull(eventName);
        List<WebElement> eventLinks = getEventLinksElem();
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.contains(eventName))
            {
                return eventLink.isDisplayed();
            }
        }
        return false;
    }

    /**
     * @param eventDetail
     * @return boolean
     */
    public boolean isEventsWithDetailDisplayed(String eventDetail)
    {
        checkNotNull(eventDetail);
        List<WebElement> eventLinks = dashlet.findElements(EVENTS_DETAILS);
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.contains(eventDetail))
            {
                return eventLink.isDisplayed();
            }
        }
        return false;
    }
    
    /**
     * Return the name of the event.
     * 
     * @param eventName
     * @return boolean
     */
    public boolean isRepeating(String event)
    {
        checkNotNull(event);
        List<WebElement> eventLinks = getEventLinksElem();
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.equalsIgnoreCase(event))
                ;
            Boolean repeating = eventLink.getText().contains("Repeating");
            return repeating;
        }

        return false;
    }
}