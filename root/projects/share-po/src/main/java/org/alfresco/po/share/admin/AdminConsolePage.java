package org.alfresco.po.share.admin;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

/**
 * The Class AdminConsolePage.
 */
@SuppressWarnings("unchecked")
public class AdminConsolePage extends SharePage implements HtmlPage
{

    /**
     * Instantiates a new admin console page.
     * 
     * @param drone WebDriver browser client
     */
    public AdminConsolePage(WebDrone drone)
    {
        super(drone);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render()
     */
    @Override
    public AdminConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @Override
    public AdminConsolePage render(RenderTime maxPageLoadingTime)
    {
        basicRender(maxPageLoadingTime);
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @Override
    public AdminConsolePage render(long maxPageLoadingTime)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

}
