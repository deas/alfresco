package org.alfresco.po.share.site.blog;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Site Blog Page object
 * relating to Share site Blog page
 *
 * @author Marina.Nenadovets
 */
@SuppressWarnings("unused")
public class BlogPage extends SitePage
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By NEW_POST_BTN = By.cssSelector(".new-blog>span");
    private static final By CONFIGURE_BLOG = By.cssSelector(".configure-blog>span");
    private static final By POSTS_CONTAINER = By.cssSelector("td[class*='blogposts']");
    private static final By EMPTY_POST_CONTAINER = By.cssSelector("td[class*='empty']");
    private static final By BACK_LINK = By.cssSelector("span.backLink>a");

    /**
     * Constructor
     */
    public BlogPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlogPage render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(NEW_POST_BTN));
        return this;
    }

    @SuppressWarnings("unchecked")
    public BlogPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlogPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to click New Topic button
     *
     * @return NewTopicForm page
     */
    private NewPostForm clickNewPost()
    {
        try
        {
            drone.findAndWait(NEW_POST_BTN).click();
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to locate New Post button");
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        return new NewPostForm(drone);
    }

    /**
     * Method to create new topic without text field
     *
     * @param titleField
     * @return
     */
    public PostViewPage createPostInternally(String titleField)
    {
        try
        {
            BlogPage blogPage = new BlogPage(drone);
            NewPostForm newPostForm = blogPage.clickNewPost();

            newPostForm.setTitleField(titleField);
            newPostForm.clickPublishInternally();
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        catch (NoSuchElementException nse)
        {
            logger.debug("Unable to find the elements");
        }
        return new PostViewPage(drone).render();
    }

    /**
     * Method to create new topic with text field
     *
     * @param titleField
     * @return
     */
    public PostViewPage createPostInternally(String titleField, String txtLines)
    {
        try
        {
            BlogPage blogPage = new BlogPage(drone);
            NewPostForm newPostForm = blogPage.clickNewPost();
            waitUntilAlert();
            newPostForm.setTitleField(titleField);
            newPostForm.insertText(txtLines);
            newPostForm.clickPublishInternally();
            waitUntilAlert(7);
            return new PostViewPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Post wasn't created");
        }
    }

    /**
     * Method to create new topic with text field and tag
     *
     * @param titleField
     * @return PostViewPage
     */
    public PostViewPage createPostInternally(String titleField, String txtLines, String tagName)
    {
        try
        {
            BlogPage blogPage = new BlogPage(drone);
            NewPostForm newPostForm = blogPage.clickNewPost();
            waitUntilAlert();
            newPostForm.setTitleField(titleField);
            newPostForm.insertText(txtLines);
            newPostForm.addTag(tagName);
            newPostForm.clickPublishInternally();
            waitUntilAlert(5);
            return new PostViewPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Post wasn't created");
        }
    }


    /**
     * Method to create new topic with text field and save as draft
     *
     * @param titleField
     * @return
     */
    public PostViewPage saveAsDraft(String titleField, String txtLines)
    {
        try
        {
            BlogPage blogPage = new BlogPage(drone);
            NewPostForm newPostForm = blogPage.clickNewPost();

            newPostForm.setTitleField(titleField);
            newPostForm.insertText(txtLines);
            return newPostForm.clickSaveAsDraft().render(3000);

        }
        catch (TimeoutException te)
        {
            throw new ShareException("the operation has timed out");
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find the button");
        }
    }
       
    /**
     * Method to Configure External Blog (wordpress, typepad)
     *
     * @param option
     * @param name
     * @param desc
     * @param url
     * @param userName
     * @param password
     * @return Blog page
     *//*
    public void configureExternalBlog(ConfigureBlogPage.TypeOptions option, String name, String desc, String url,
        String userName, String password)
    {
        ConfigureBlogPage configureBlogPage = clickConfigureBlog();
        configureBlogPage.selectTypeOption(option);
        configureBlogPage.inputNameField(name);
        configureBlogPage.inputDescriptionField(desc);
        configureBlogPage.inputURL(url);
        configureBlogPage.inputUserName(userName);
        configureBlogPage.inputPassword(password);
        configureBlogPage.clickOk();
        waitUntilAlert(7);
    }

    /**
     * Method to verify whether configure External Blog is enabled
     *
     * @return true if enabled
     */
    public boolean isNewPostEnabled()
    {
        String someButton = drone.findAndWait(NEW_POST_BTN).getAttribute("class");
        if (someButton.contains("yui-button-disabled"))
        {
            return false;
        }
        else return true;
    }

    /**
     * Method to open a post
     *
     * @param title
     * @return Post View Page
     */
    public PostViewPage openBlogPost(String title)
    {
        try
        {
            WebElement thePost = drone.findAndWait(By.xpath(String.format("//a[text()='%s']", title)));
            drone.mouseOver(thePost);
            thePost.click();
            waitUntilAlert();
        }
        catch (TimeoutException e)
        {
            throw new ShareException("Unable to click the link");
        }
        return new PostViewPage(drone);
    }   

    /**
     * Method to retrieve the posts count
     *
     * @return number of posts
     */
    public int getPostsCount ()
    {
        try
        {
                List <WebElement> numOfPosts = drone.findAndWaitForElements(POSTS_CONTAINER);
                return numOfPosts.size();
        }
        catch (TimeoutException te)
        {
            return 0;
        }
        catch (NoSuchElementException nse)
        {
            return 0;
        }
    }
}