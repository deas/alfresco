package org.alfresco.po.share.site.discussions;

import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Marina.Nenadovets
 */
public class TopicDirectoryInfoImpl extends HtmlElement implements TopicDirectoryInfo
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By VIEW_LINK = By.cssSelector(".onViewTopic>a");
    private static final By EDIT_TOPIC = By.cssSelector(".onEditTopic>a");
    private static final By DELETE_TOPIC = By.cssSelector(".onDeleteTopic>a");

    /**
     * Constructor
     */
    protected TopicDirectoryInfoImpl(WebDrone drone, WebElement webElement)
    {
        super(webElement,drone);
    }

    /**
     * Method to view the topic from Discussion list page
     *
     * @return TopicViewPage
     */
    public TopicViewPage viewTopic()
    {
        try
        {

            findAndWait(VIEW_LINK).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find View button");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new TopicViewPage(drone);
    }

    /**
     * Method to view the topic from Discussion list page
     *
     * @return TopicViewPage
     */
    public NewTopicForm editTopic()
    {
        try
        {
            findAndWait(EDIT_TOPIC).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find View button");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new NewTopicForm(drone);
    }

    /**
     * Method to delete a topic
     *
     * @return Discussions Page
     */
    public DiscussionsPage deleteTopic()
    {
        try
        {
            findAndWait(DELETE_TOPIC).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find View button");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new DiscussionsPage(drone);
    }
}
