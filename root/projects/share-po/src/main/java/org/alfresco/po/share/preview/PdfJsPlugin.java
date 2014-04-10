package org.alfresco.po.share.preview;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class PdfJsPlugin extends SharePage
{
    private static Log logger = LogFactory.getLog(PdfJsPlugin.class);

    // Controls container
    private static final String CLASS_CONTROLS_DIV = "controls";

    // Sidebar container
    private static final String SIDEBAR_DIV = ".sidebar";

    // Main document container
    private static final String VIEWER_MAIN_DIV = ".viewer.documentView";

    // Main document container
    private static final String VIEWER_MAIN_PAGES = ".viewer.documentView .page";

    public PdfJsPlugin(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PdfJsPlugin render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public PdfJsPlugin render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.cssSelector(VIEWER_MAIN_DIV)),
                getVisibleRenderElement(By.cssSelector(SIDEBAR_DIV)),
                getVisibleRenderElement(By.className(CLASS_CONTROLS_DIV)));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PdfJsPlugin render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Checks if sidebar is displayed
     * 
     * @return
     */
    public boolean isSidebarVisible()
    {
        try
        {
            WebElement sidebar = drone.find(By.cssSelector(SIDEBAR_DIV));
            return sidebar.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No sidebar container " + nse);
            throw new PageException("Unable to find sidebar container.", nse);
        }
    }

    /**
     * Return the number of pages present within the main view
     * 
     * @return
     */
    public int getMainViewNumDisplayedPages()
    {
        try
        {
            List<WebElement> pages = drone.findDisplayedElements(By.cssSelector(VIEWER_MAIN_PAGES));
            return pages.size();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No pages found " + nse);
            throw new PageException("Unable to find any pages.", nse);
        }
    }

}
