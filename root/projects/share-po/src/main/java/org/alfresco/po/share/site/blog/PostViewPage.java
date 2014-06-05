package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.SharePage;
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

import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Page object to represent Post View page
 *
 * @author Marina.Nenadovets
 */

public class PostViewPage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final By ADD_COMMENT_BTN = By.cssSelector(".onAddCommentClick>span>button");
    private static final By BACK_LINK = By.cssSelector("span.backLink>a");
    private static final By NEW_POST_BTN = By.cssSelector(".new-blog>span");
    private static final By POST_TITLE = By.cssSelector(".nodeTitle>a");
    private static final By COMMENT_CONTENT = By.cssSelector(".comment-content>p");
    private static final By EDIT_LINK = By.cssSelector(".onEditBlogPost>a");
    private static final By DELETE_LINK = By.cssSelector(".onDeleteBlogPost>a");
    private static final By TAG = By.cssSelector("span.tag > a");
    private static final By TAG_NONE = By.xpath("//span[@class='nodeAttrValue' and text()='(None)']");

    public PostViewPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PostViewPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(NEW_POST_BTN),
            getVisibleRenderElement(BACK_LINK));
        return this;
    }

    @SuppressWarnings("unchecked")
    public PostViewPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public PostViewPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for clicking Add comment button
     *
     * @param comment
     * @return
     */
    private BlogCommentForm clickAddCommentBtn(String comment)
    {
        try
        {
            drone.findAndWait(ADD_COMMENT_BTN).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find comment button");
        }
        return new BlogCommentForm(drone);
    }

    /**
     * Method for creating blog comment
     *
     * @param comment
     * @return Post View page object
     */
    public PostViewPage createBlogComment(String comment)
    {
        PostViewPage postViewPage = new PostViewPage(drone);
        BlogCommentForm blogCommentForm = postViewPage.clickAddCommentBtn(comment);
        blogCommentForm.insertText(comment);
        blogCommentForm.clickAddComment();
        waitUntilAlert();
        if (isCommentPresent(comment))
        {
            return new PostViewPage(drone).render();
        }
        throw new ShareException("Comment wasn't added");
    }

    private boolean isCommentPresent(String comment)
    {
        boolean isPresent = false;
        List<WebElement> allComments = drone.findAll(COMMENT_CONTENT);
        for (WebElement allTheComments: allComments)
        {
           isPresent = allTheComments.getText().equalsIgnoreCase(comment);
            if (isPresent)
                return isPresent;
        }
        return isPresent;
    }

    public int getCommentCount ()
    {
        try
        {
            List<WebElement> span = drone.findAll(COMMENT_CONTENT);
            return span.size();
        }
        catch (NoSuchElementException nse)
        {
            return 0;
        }
    }

    private boolean isEnabled(By locator)
    {
        try
        {
            return drone.findAndWait(locator, 2000).isEnabled();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to verify whether add comment button is displayed
     *
     * @return true if visible
     */
    public boolean isAddCommentDisplayed()
    {
        return isEnabled(ADD_COMMENT_BTN);
    }

    /**
     * Method to go back to Blog page list
     *
     * @return BlogPage
     */
    public BlogPage clickBackLink()
    {
        try
        {
            WebElement backLink = drone.findAndWait(BACK_LINK);
            backLink.click();
            waitUntilAlert(7);
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + BACK_LINK);
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find " + BACK_LINK);
        }
        return drone.getCurrentPage().render();
    }

    /**
     * Method to verify whether post was created
     *
     * @return boolean
     */
    public boolean verifyPostExists(String title)
    {
        try
        {
            String actualTitle = drone.find(POST_TITLE).getText();
            if (title.equals(actualTitle))
                return true;
            else
                return false;
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Timed out finding the post");
        }
    }

    private EditPostForm clickEdit()
    {
        try
        {
            drone.findAndWait(EDIT_LINK).click();
            return new EditPostForm(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + EDIT_LINK);
        }
    }

    private CommentDirectoryInfo getCommentDirectoryInfo (String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = drone.findAndWait(By.xpath(String.format("//div[@class='comment-content']/p[text()='%s']/../..", title)), WAIT_TIME_3000);
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
        return new CommentDirectoryInfo(drone, row);
    }

    /**
     * Method to edit post comment
     *
     * @param oldComment
     * @param newComment
     *
     * @return PostViewPage
     */
    public PostViewPage editBlogComment (String oldComment, String newComment)
    {
        try
        {
            getCommentDirectoryInfo(oldComment).clickEdit();
            BlogCommentForm blogCommentForm = new BlogCommentForm(drone);
            blogCommentForm.insertText(newComment);
            blogCommentForm.clickAddComment();
            waitUntilAlert();
            if(isCommentPresent(newComment))
            {
                return new PostViewPage(drone).render();
            }
            throw new ShareException("Comment wasn't be edited");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to edit the comment");
        }
    }

    private void clickDelete()
    {
        try
        {
            drone.findAndWait(DELETE_LINK).click();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + DELETE_LINK);
        }
    }

    /**
     * Method to edit a blog post and save it as draft
     *
     * @param newTitle
     * @param newLines
     *
     * @return PostViewPage
     */
    public PostViewPage editBlogPostAndSaveAsDraft (String newTitle, String newLines)
    {
        EditPostForm editPostForm = clickEdit();
        editPostForm.setTitleField(newTitle);
        editPostForm.insertText(newLines);
        editPostForm.clickSaveAsDraft();
        return new PostViewPage(drone).render();
    }

    /**
     * Method to delete a post and confirm
     *
     * @return BlogPage
     */
    public BlogPage deleteBlogPostWithConfirm ()
    {
        try
        {
            clickDelete();
            drone.findAndWait(CONFIRM_DELETE).click();
            waitUntilAlert();
            return new BlogPage(drone);
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to delete a post");
        }
    }

    /**
     * Method to delete a comment and confirm
     *
     * @param commentTitle
     * @return PostViewPage
     */
    public PostViewPage deleteCommentWithConfirm (String commentTitle)
    {
        try
        {
            getCommentDirectoryInfo(commentTitle).clickDelete();
            drone.findAndWait(CONFIRM_DELETE).click();
            waitUntilAlert();
            return new PostViewPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to delete the comment");
        }
    }

    /**
     * Method to verify whether edit post is displayed
     *
     * @return true if displayed
     */
    public boolean isEditPostDisplayed ()
    {
        return drone.isElementDisplayed(EDIT_LINK);
    }

    /**
     * Method to verify whether delete post is displayed
     *
     * @return true if displayed
     */
    public boolean isDeletePostDisplayed ()
    {
        return drone.isElementDisplayed(DELETE_LINK);
    }

    /**
     * Method to verify whether edit comment is displayed
     * @param comment
     * @return true if displayed
     */
    public boolean isEditCommentDisplayed (String comment)
    {
        return getCommentDirectoryInfo(comment).isEditDisplayed();
    }

    /**
     * Method to verify whether delete comment is displayed
     * @param comment
     * @return true if displayed
     */
    public boolean isDeleteCommentDisplayed (String comment)
    {
        return getCommentDirectoryInfo(comment).isDeleteDisplayed();
    }

    /**
     * Method to retrieve tag added to Blog
     * 
     * @return String
     */
    public String getTagName()
    {
        try
        {
            if (!drone.isElementDisplayed(TAG_NONE))
            {
                String tagName = drone.findAndWait(TAG).getText();
                if (!tagName.isEmpty())
                    return tagName;
                else
                    throw new IllegalArgumentException("Cannot find tag");

            }
            else
                return drone.find(TAG_NONE).getText();

        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the tag");
        }
    }
}
