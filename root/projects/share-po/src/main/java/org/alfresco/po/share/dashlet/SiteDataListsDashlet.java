package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.datalist.NewListForm;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

/**
 * Page object to hold data list dashlet
 *
 * @author Marina.Nenadovets
 */
public class SiteDataListsDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.site-data-lists");
    private static final By CREATE_DATA_LIST = By.cssSelector("a[href='data-lists#new']");

    /**
     * Constructor.
     */
    protected SiteDataListsDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector("div.dashlet.site-data-lists .yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized SiteDataListsDashlet render(RenderTime timer)
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
    public SiteDataListsDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteDataListsDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method gets the focus by placing mouse over on Site Content Dashlet.
     */
    private void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Method to verify whether create data list link is available
     *
     * @return boolean
     */
    public boolean isCreateDataListDisplayed()
    {
        try
        {
            getFocus();
            return drone.isElementDisplayed(CREATE_DATA_LIST);
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to click Create Data List button
     *
     * @return NewListForm page object
     */

    public NewListForm clickCreateDataList()
    {
        try
        {
            getFocus();
            drone.findAndWait(CREATE_DATA_LIST).click();
            return new NewListForm(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + CREATE_DATA_LIST);
        }
    }
}
