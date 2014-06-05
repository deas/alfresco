package org.alfresco.po.share.site.discussions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.dashlet.mydiscussions.TopicsListPage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Site Discussions Page object
 * relating to Share site Discussions page
 *
 * @author Marina Nenadovets
 */
public class DiscussionsPage extends TopicsListPage
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By NEW_TOPIC_BTN = By.cssSelector("#template_x002e_toolbar_x002e_discussions-topiclist_x0023_default-create-button-button");
    @SuppressWarnings("unused")
    private static final By TOPIC_TITLE = By.cssSelector(".nodeTitle>a");
    private static final By TOPIC_CONTAINER = By.cssSelector("tbody[class='yui-dt-data']>tr");


    public DiscussionsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DiscussionsPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(NEW_TOPIC_BTN));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DiscussionsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DiscussionsPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to click New Topic button
     *
     * @return NewTopicForm page
     */
    private NewTopicForm clickNewTopic()
    {
        try
        {
            drone.findAndWait(NEW_TOPIC_BTN).click();
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to locate New Topic button");
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        return new NewTopicForm(drone);
    }

    /**
     * Method to create new topic
     *
     * @param titleField
     * @param textLines
     * @return
     */

    public TopicViewPage createTopic(String titleField, String textLines)
    {
        try
        {
            DiscussionsPage discussionsPage = new DiscussionsPage(drone);
            NewTopicForm newTopicForm = discussionsPage.clickNewTopic();

            newTopicForm.setTitleField(titleField);
            try
            {
                checkNotNull(textLines);
            }
            catch (NullPointerException e)
            {
                throw new ShareException("The lines are null!");
            }
            newTopicForm.insertText(textLines);
            newTopicForm.clickSave();
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        catch (NoSuchElementException nse)
        {
            logger.debug("Unable to find the elements");
        }
        return new TopicViewPage(drone).render();
    }

    /**
     * Method to create new topic without text field
     *
     * @param titleField
     * @return
     */

    public TopicViewPage createTopic(String titleField)
    {
        try
        {
            DiscussionsPage discussionsPage = new DiscussionsPage(drone);
            NewTopicForm newTopicForm = discussionsPage.clickNewTopic();

            newTopicForm.setTitleField(titleField);
            newTopicForm.clickSave();
            waitUntilAlert();
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        catch (NoSuchElementException nse)
        {
            logger.debug("Unable to find the elements");
        }
        return new TopicViewPage(drone).render();
    }

    /**
     * Method to create new topic with tag
     *
     * @param titleField
     * @param textLines
     * @param tag
     * @return
     */

    public TopicViewPage createTopic(String titleField, String textLines, String tag)
    {
        try
        {
            DiscussionsPage discussionsPage = new DiscussionsPage(drone);
            NewTopicForm newTopicForm = discussionsPage.clickNewTopic();

            newTopicForm.setTitleField(titleField);
            try
            {
                checkNotNull(textLines);
            }
            catch (NullPointerException e)
            {
                throw new ShareException("The lines are null!");
            }
            newTopicForm.insertText(textLines);
            newTopicForm.addTag(tag);

            newTopicForm.clickSave();
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        catch (NoSuchElementException nse)
        {
            logger.debug("Unable to find the elements");
        }
        return new TopicViewPage(drone).render();
    }

    public TopicDirectoryInfo getTopicDirectoryInfo(final String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = drone.findAndWait(By.xpath(String.format("//a[text()='%s']/../../..", title)), WAIT_TIME_3000);
            drone.mouseOverOnElement(row);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        catch (TimeoutException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        return new TopicDirectoryInfoImpl (drone, row);
    }

    /**
     * Method to verify whether New Topic Link is available
     *
     * @return true if enabled
     */
    public boolean isNewTopicEnabled ()
    {
        try
        {
            return drone.findAndWait(NEW_TOPIC_BTN).isEnabled();
        }
        catch (TimeoutException nse)
        {
            return false;
        }
    }
    /**
     * Method to view topic
     *
     * @return TopicViewPage
     */
    public TopicViewPage viewTopic (String title)
    {
        try
        {
            getTopicDirectoryInfo(title).viewTopic();
            return new TopicViewPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to view the topic");
        }
    }
    /**
     * Method to edit topic
     *
     * @param oldTitle
     * @param newTitle
     * @return
     */
    public TopicViewPage editTopic (String oldTitle, String newTitle, String txtLines)
    {
        try
        {
            NewTopicForm newTopicForm = getTopicDirectoryInfo(oldTitle).editTopic();
            newTopicForm.setTitleField(newTitle);
            newTopicForm.insertText(txtLines);
            newTopicForm.clickSave();
            waitUntilAlert();
            logger.info("Edited topic " + oldTitle);
            return new TopicViewPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Timed out finding buttons");
        }
    }

    /**
     * Method to delete topic with confirmation
     * @param title
     * @return Discussions Page
     */
    public DiscussionsPage deleteTopicWithConfirm (String title)
    {
        try
        {
            getTopicDirectoryInfo(title).deleteTopic();
            if (!drone.isElementDisplayed(PROMPT_PANEL_ID))
            {
                throw new ShareException("The prompt dialogue isn't popped up");
            }
            drone.findAndWait(CONFIRM_DELETE).click();
            waitUntilAlert();
            logger.info("Topic " + title + "was deleted");
            return new DiscussionsPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to delete a topic");
        }
    }

    /**
     * Method to get topic count
     * @return number of topics
     */
    public int getTopicCount ()
    {
        try
        {
            if (!drone.isElementDisplayed(TOPIC_CONTAINER))
            {
                return 0;
            }
            return drone.findAll(TOPIC_CONTAINER).size();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + TOPIC_CONTAINER);
        }
    }

    /**
     * Method to verify whether edit topic is displayed
     *
     * @param title
     * @return true if displayed
     */
    public boolean isEditTopicDisplayed(String title)
    {
        return getTopicDirectoryInfo(title).isEditTopicDisplayed();
    }

    /**
     * Method to verify whether delete topic is displayed
     *
     * @param title
     * @return true if displayed
     */
    public boolean isDeleteTopicDisplayed(String title)
    {
        return getTopicDirectoryInfo(title).isDeleteTopicDisplayed();
    }
}
