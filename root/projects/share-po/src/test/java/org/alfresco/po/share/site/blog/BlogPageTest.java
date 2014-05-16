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
import java.util.List;

import static org.alfresco.po.share.site.blog.ConfigureBlogPage.TypeOptions.WORDPRESS;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Holds tests for Blog page web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2", "TestBug"})
public class BlogPageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    BlogPage blogPage = null;
    PostViewPage postViewPage = null;
    String text = getClass().getSimpleName();
    String editedText = text + " edited";
    String url = "qaalfresco.wordpress.com";
    String blogUsername = "qaalfresco";
    String blogPassword = "parkh0use";

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

    @Test(groups = "Enterprise-only")
    public void addBlogPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.BLOG);
        customizeSitePage.addPages(addPageTypes);
        blogPage = siteDashBoard.getSiteNav().selectBlogPage();
        assertNotNull(blogPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "addBlogPage")
    public void createBlogPostInternally()
    {
        assertTrue(blogPage.isNewPostEnabled());
        postViewPage = blogPage.createPostInternally(text, text);
        assertNotNull(postViewPage);
        assertTrue(postViewPage.verifyPostExists(text));
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "configureExternalWordpressBlog")
    public void createBlogPostExternally()
    {
        assertTrue(blogPage.isNewPostEnabled());
        postViewPage = blogPage.createPostExternally(text, text);
        assertNotNull(postViewPage);
        assertTrue(postViewPage.verifyPostExists(text));
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "addBlogPage")
    public void saveAsDraft()
    {
        assertTrue(blogPage.isNewPostEnabled());
        postViewPage = blogPage.saveAsDraft(text, text);
        assertNotNull(postViewPage);
        assertTrue(postViewPage.verifyPostExists(text));
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "createBlogPostInternally")
    public void openPost()
    {
        blogPage = postViewPage.clickBackLink();
        blogPage.openBlogPost(text);
        assertNotNull(postViewPage);
        assertTrue(postViewPage.verifyPostExists(text));
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "addBlogPage")
    public void configureExternalWordpressBlog()
    {
        assertTrue(blogPage.isConfigureBlogDisplayed());
        blogPage.configureExternalBlog(WORDPRESS, text, text, url, blogUsername, blogPassword);
        assertNotNull(blogPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "openPost")
    public void createPostComment()
    {
        assertTrue(postViewPage.isAddCommentDisplayed());
        postViewPage.createBlogComment(text);
    }
}
