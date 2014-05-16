package org.alfresco.po.share.site.links;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Page object to reflect Links Details page
 *
 * @author Marina.Nenadovets
 */
@SuppressWarnings("unused")
public class LinksDetailsPage extends SharePage
{
    private static final By LINKS_LIST_LINK = By.cssSelector(".forward-link>a");
    private static final By COMMENT_LINK = By.cssSelector(".onAddCommentClick");
    private static final By EDIT_LINK = By.cssSelector(".onEditLink>a");
    private static final By DELETE_LINK = By.cssSelector(".onDeleteLink>a");

    /**
     * Constructor
     *
     * @param drone
     */
    public LinksDetailsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    public LinksDetailsPage render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(LINKS_LIST_LINK),
            getVisibleRenderElement(COMMENT_LINK));

        return this;
    }

    @SuppressWarnings("unchecked")
    public LinksDetailsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    public LinksDetailsPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to browse to links list
     *
     * @return Links Page
     */
    public LinksPage browseToLinksList ()
    {
        try
        {
            drone.findAndWait(LINKS_LIST_LINK).click();
            return new LinksPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + LINKS_LIST_LINK);
        }
    }
}
