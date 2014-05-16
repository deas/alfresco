package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Page object holds the elements of Select Wiki box page
 *
 * @author Marina.Nenadovets
 */

public class SelectWikiDialogueBoxPage extends SharePage
{
    @SuppressWarnings("unused")
    private static final By SELECT_DROP_DOWN = By.cssSelector("select[name='wikipage']");
    private static final By OK_BUTTON = By.cssSelector("button[id$='configDialog-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='configDialog-cancel-button']");
    private static final By CLOSE_BUTTON = By.cssSelector(".container-close");

    /**
     * Constructor.
     */
    protected SelectWikiDialogueBoxPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectWikiDialogueBoxPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(OK_BUTTON), getVisibleRenderElement(CANCEL_BUTTON),
            getVisibleRenderElement(CLOSE_BUTTON));
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectWikiDialogueBoxPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectWikiDialogueBoxPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
