package org.alfresco.po.alfresco;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * Created by olga.lokhach on 6/17/2014.
 */
public class RepositoryAdminConsolePage extends AbstractAdminConsole
{

    public RepositoryAdminConsolePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryAdminConsolePage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryAdminConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryAdminConsolePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to verify console is opened
     *
     * @return boolean
     */

    public boolean isOpened()
    {
        return drone.findAndWait(By.xpath("//span[@id='RepoAdmin-console-title:titleRepoAdminConsole']")).isDisplayed();
    }

}
