package org.alfresco.po.share.admin;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

/**
 * The Class ManageSitesPage.
 */
@SuppressWarnings("unchecked")
public class ManageSitesPage extends SharePage
{

    /**
     * Instantiates a new manage sites page.
     *
     * @param drone WebDriver browser client
     */
    public ManageSitesPage(WebDrone drone)
    {
        super(drone);
    }

    /* (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render()
     */
    @Override
    public ManageSitesPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /* (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @Override
    public ManageSitesPage render(RenderTime maxPageLoadingTime)
    {
        basicRender(maxPageLoadingTime);
        return this;
    }

    /* (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @Override
    public ManageSitesPage render(long maxPageLoadingTime)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}