package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Page object to hold Web View dashlet
 *
 * @author Marina.Nenadovets
 */
public class WebViewDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.webview");
    private static final By IF_FRAME_WITH_SITE = By.cssSelector("iframe[class='iframe-body']");
    private static final By DEFAULT_MESSAGE = By.cssSelector("h3[class$='default-body']");

    /**
     * Constructor.
     */
    protected WebViewDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector(".yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized WebViewDashlet render(RenderTime timer)
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
    public WebViewDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WebViewDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method gets the focus by placing mouse over on Site Web View Dashlet.
     */
    protected void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Method to click Configure icon
     *
     * @return ConfigureWebViewDashletBox page object
     */
    public ConfigureWebViewDashletBoxPage clickConfigure()
    {
        try
        {
            getFocus();
            dashlet.findElement(CONFIGURE_DASHLET_ICON).click();
            return new ConfigureWebViewDashletBoxPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("The operation has timed out");
        }
    }

    /**
     * return default text from dashlet. or throw Exception.
     *
     * @return
     */
    public String getDefaultMessage()
    {
        try
        {
            return dashlet.findElement(DEFAULT_MESSAGE).getText();
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Default message in web view dashlet missing or don't display.");
        }
    }

    /**
     * return true if frame with url displayed.
     *
     * @param url
     * @return
     */
    public boolean isFrameShow(String url)
    {
        checkNotNull(url);
        try
        {
            WebElement element = dashlet.findElement(IF_FRAME_WITH_SITE);
            return element.getAttribute("src").equals(url);
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }


}
