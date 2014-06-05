package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.alfresco.po.share.site.blog.ConfigureBlogPage.TypeOptions.WORDPRESS;
import static org.testng.Assert.*;

/**
 * Holds tests for Blog page web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class BlogPageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    BlogPage blogPage = null;
    PostViewPage postViewPage = null;
    String text = getClass().getSimpleName();
    String editedText = text + " edited";
    String externalTitle = "Hi, everyone! Today is " + Calendar.getInstance().getTime();
    String externalMessage = "Today is " + Calendar.getInstance().getTime();

    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "blog" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void addBlogPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.BLOG);
        customizeSitePage.addPages(addPageTypes).render();
        blogPage = siteDashBoard.getSiteNav().selectBlogPage().render();
        assertNotNull(blogPage);
    }

    @Test(dependsOnMethods = "addBlogPage", priority = 3)
    public void createBlogPostInternally()
    {
        assertTrue(blogPage.isNewPostEnabled());
        postViewPage = blogPage.createPostInternally(text, text).render();
        assertNotNull(postViewPage);
        assertTrue(postViewPage.verifyPostExists(text));
    }

    @Test(dependsOnMethods = "configureExternalWordpressBlog")
    public void createBlogPostExternally()
    {
        assertTrue(blogPage.isNewPostEnabled());
        postViewPage = blogPage.createPostExternally(externalTitle, externalMessage).render();
        assertNotNull(postViewPage);
        assertTrue(postViewPage.verifyPostExists(externalTitle));
    }

    @Test(dependsOnMethods = "addBlogPage", priority = 2)
    public void saveAsDraft()
    {
        assertTrue(blogPage.isNewPostEnabled());
        postViewPage = blogPage.saveAsDraft(text, text).render();
        assertNotNull(postViewPage);
        assertTrue(postViewPage.verifyPostExists(text));
    }

    @Test(dependsOnMethods = "createBlogPostInternally", enabled=false)
    public void openPost()
    {
        blogPage = siteDashBoard.getSiteNav().selectBlogPage().render();
        postViewPage = blogPage.openBlogPost(text).render();
        assertNotNull(postViewPage);
        assertTrue(postViewPage.verifyPostExists(text));
    }

    @Test(dependsOnMethods = "addBlogPage", priority = 1)
    public void configureExternalWordpressBlog()
    {
        assertTrue(blogPage.isConfigureBlogDisplayed());
        blogPage.configureExternalBlog(WORDPRESS, text, text, blogUrl, blogUsername, blogPassword);
        assertNotNull(blogPage);
    }

    @Test(dependsOnMethods = "openPost", enabled=false)
    public void createPostComment()
    {
        assertTrue(postViewPage.isAddCommentDisplayed());
        postViewPage.createBlogComment(text);
    }

    @Test (dependsOnMethods = "openPost", enabled=false)
    public void editBlogPostAndSaveAsDraft ()
    {
        postViewPage.editBlogPostAndSaveAsDraft(editedText, editedText);
    }

    @Test (dependsOnMethods = "createPostComment", enabled=false)
    public void editPostComment ()
    {
        postViewPage.editBlogComment(text, editedText);
        assertNotNull(postViewPage);
    }

    @Test (dependsOnMethods = "editPostComment", enabled=false)
    public void deletePostComment ()
    {
        int expCount = postViewPage.getCommentCount();
        postViewPage.deleteCommentWithConfirm(editedText);
        assertEquals(postViewPage.getCommentCount(), expCount-1);
        assertNotNull(postViewPage);
    }

    @Test (dependsOnMethods = "deletePostComment", enabled=false)
    public void deletePostWithConfirm ()
    {
        blogPage = postViewPage.clickBackLink().render();
        int expCount = blogPage.getPostsCount();
        blogPage.openBlogPost(editedText);
        blogPage = postViewPage.deleteBlogPostWithConfirm().render();
        assertEquals(blogPage.getPostsCount(), expCount-1);
    }
}
