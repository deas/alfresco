package org.alfresco.po.share.site.discussions;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Topic View page object
 * relating to Share topic view page
 *
 * @author Marina Nenadovets
 */
public class TopicViewPage extends DiscussionsPage
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By REPLY_LINK = By.cssSelector(".onAddReply>a");
    private static final By BACK_LINK = By.cssSelector(".backLink>a");
    private static final By REPLY_CONTAINER = By.cssSelector(".reply");

    /**
     * Constructor
     *
     * @param drone
     */
    public TopicViewPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TopicViewPage render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(BACK_LINK));
        return this;
    }

    @SuppressWarnings("unchecked")
    public TopicViewPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TopicViewPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for clicking Back button from Topic View page
     *
     * @return Discussions Page object
     */
    public DiscussionsPage clickBack()
    {
        try
        {
            drone.findAndWait(BACK_LINK).click();
            waitUntilAlert();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to find Back Link");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new DiscussionsPage(drone);
    }

    /**
     * Method for clicking Reply button on topic view page
     *
     * @return AddReplyForm page object
     */
    private AddReplyForm clickReply()
    {
        try
        {
            drone.findAndWait(REPLY_LINK).click();
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new AddReplyForm(drone);
    }

    /**
     * Method for creating a reply
     *
     * @param replyText
     * @return
     */
    public TopicViewPage createReply(String replyText)
    {
        try
        {
            clickReply();
            waitUntilAlert();
            AddReplyForm addReplyForm = new AddReplyForm(drone);
            addReplyForm.insertText(replyText);
            addReplyForm.clickSubmit().render();
            waitUntilAlert();
            return new TopicViewPage(drone);
        }
        catch (TimeoutException te)
        {
            throw new ShareException("The operation has timed out");
        }
    }

    /**
     * Method to verify whether reply link is available
     *
     * @return true if displayed
     */
    public boolean isReplyLinkDisplayed()
    {
        try
        {
            return drone.findAndWait(REPLY_LINK, 2000).isDisplayed();
        }
        catch (TimeoutException nse)
        {
            return false;
        }
    }

    private ReplyDirectoryInfo getReplyDirectoryInfo(final String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = drone.findAndWait(By.xpath(String.format("//div[@class='nodeContent']/div[2]/p[text()='%s']/../../..", title)), WAIT_TIME_3000);
        }
        catch (TimeoutException te)
        {
            throw new ShareException(String.format("File directory info with title %s was not found", title), te);
        }
        return new ReplyDirectoryInfo(drone, row);
    }

    /**
     * Method to edit a reply
     *
     * @param title
     * @param replyText
     * @return Topic View Page
     */
    public TopicViewPage editReply (String title, String replyText)
    {
        getReplyDirectoryInfo(title).clickEdit();
        AddReplyForm addReplyForm = new AddReplyForm(drone);
        addReplyForm.insertText(replyText);
        addReplyForm.clickSubmit().render();
        return new TopicViewPage(drone);
    }

    /**
     * Method to get the number of replies
     *
     * @return number of replies
     */
    public int getReplyCount ()
    {
        try
        {
            if(!drone.isElementDisplayed(REPLY_CONTAINER))
            {
                return 0;
            }
            return drone.findAll(REPLY_CONTAINER).size();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + REPLY_CONTAINER);
        }
    }
}
