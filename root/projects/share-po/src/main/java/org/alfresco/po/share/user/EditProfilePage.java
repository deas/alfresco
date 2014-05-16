package org.alfresco.po.share.user;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * Page object to reflect Edit user profile page
 *
 * @author Marina.Nenadovets
 */
public class EditProfilePage extends SharePage
{
    private static final By SAVE_CHANGES = By.cssSelector("button[id$=default-button-save-button]");

    /**
     * Constructor
     */
    public EditProfilePage(WebDrone drone)
    {
        super(drone);
    }

    /*
     * Render logic
     */
    @SuppressWarnings("unchecked")
    public EditProfilePage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(SAVE_CHANGES));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditProfilePage render(long time)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditProfilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
