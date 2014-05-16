package org.alfresco.po.share.systemsummary;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sergey.kardash on 4/12/14.
 */
public class RepositoryServerClusteringPage extends AdvancedAdminConsolePage
{
    // Cluster Enabled
    private static final By CLUSTER_ENABLED = By.cssSelector("div[class$='control status'] span[class$='value'] img[title$='Enabled']");

    // Cluster Members
    private static final By CLUSTER_MEMBERS_IP = By.cssSelector("table[id$='rc-membertable'] tbody tr td:nth-of-type(2)");

    // Cluster Members
    private static final By CLUSTER_MEMBERS_NUMBER = By.cssSelector("div[class$='column-full'] div[class$='control field'] span[class$='value']");

    public RepositoryServerClusteringPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryServerClusteringPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryServerClusteringPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryServerClusteringPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * @return true if cluster enabled, return false if cluster disabled
     */
    public boolean isClusterEnabled()
    {
        try
        {
            WebElement clusterEnabled = drone.find(CLUSTER_ENABLED);
            return clusterEnabled.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            return false;
        }
    }

    /**
     * @return number of cluster members
     */
    public int getClusterMembersNumber()
    {
        try
        {
            WebElement clusterMembersNumber = drone.findAndWait(CLUSTER_MEMBERS_NUMBER);
            return Integer.parseInt(clusterMembersNumber.getText());
        }
        catch (NumberFormatException e)
        {
            throw new PageOperationException("Unable to parse Cluster members number");
        }
    }

    /**
     * @return list of ip address for cluster members
     */
    public List<String> getClusterMembers()
    {

        try
        {
            List<String> clusterMembers = new ArrayList<>();
            List<WebElement> elements = drone.findAndWaitForElements(CLUSTER_MEMBERS_IP);
            for (WebElement webElement : elements)
            {
                if (webElement.isDisplayed())
                {
                    clusterMembers.add(webElement.getText());
                }
            }
            return clusterMembers;
        }
        catch (StaleElementReferenceException e)
        {
            return getClusterMembers();
        }

    }

}
