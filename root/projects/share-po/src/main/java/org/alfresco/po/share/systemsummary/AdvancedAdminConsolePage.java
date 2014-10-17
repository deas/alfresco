package org.alfresco.po.share.systemsummary;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.NoSuchElementException;


/**
 * @author sergey.kardash on 4/11/14.
 */
public abstract class AdvancedAdminConsolePage extends SharePage
{

    // private Log logger = LogFactory.getLog(this.getClass());

    public AdvancedAdminConsolePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedAdminConsolePage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedAdminConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedAdminConsolePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public SharePage openConsolePage(AdminConsoleLink adminConsoleLink)
    {
        drone.findAndWait(adminConsoleLink.contentLocator).click();
        return drone.getCurrentPage().render();
    }

    /**
     * Checks if tab present at a left column af tabs' list
     * @param adminConsoleLink
     * @return
     */
    public boolean isConsoleLinkPresent(AdminConsoleLink adminConsoleLink)
    {
          try
          {
              return drone.findAndWait(adminConsoleLink.contentLocator).isDisplayed();
          }
          catch (NoSuchElementException nse)
          {
              return false;
          }
    }

}
