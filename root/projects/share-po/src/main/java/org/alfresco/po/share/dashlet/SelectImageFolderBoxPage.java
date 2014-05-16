package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Page object holds the elements of Select Image box
 *
 * @author Marina.Nenadovets
 */
public class SelectImageFolderBoxPage extends SharePage
{
    private static final By DESTINATION_CONTAINER = By.cssSelector("div[id$='default-rulesPicker-modeGroup']");
    private static final By SITES_CONTAINER = By.cssSelector("div[id$='default-rulesPicker-sitePicker']");
    private static final By PATH_CONTAINER = By.cssSelector("div[id$='default-rulesPicker-treeview']");
    private static final By OK_BUTTON = By.cssSelector("button[id$='default-rulesPicker-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='default-rulesPicker-cancel-button']");
    private static final By CLOSE_BUTTON = By.cssSelector(".container-close");

    /**
     * Constructor.
     */
    protected SelectImageFolderBoxPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectImageFolderBoxPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(DESTINATION_CONTAINER), getVisibleRenderElement(SITES_CONTAINER),
            getVisibleRenderElement(PATH_CONTAINER), getVisibleRenderElement(OK_BUTTON), getVisibleRenderElement(CANCEL_BUTTON), getVisibleRenderElement(CLOSE_BUTTON));
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectImageFolderBoxPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectImageFolderBoxPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
