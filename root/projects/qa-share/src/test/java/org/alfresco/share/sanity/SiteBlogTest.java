package org.alfresco.share.sanity;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.share.site.blog.BlogTreeMenuNavigation;
import org.alfresco.po.share.site.blog.PostViewPage;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.po.share.enums.BlogPostStatus.OUT_OF_SYNC;
import static org.alfresco.po.share.enums.BlogPostStatus.UPDATED;
import static org.alfresco.po.share.site.blog.BlogTreeMenuNavigation.PostsMenu.*;
import static org.alfresco.po.share.site.blog.ConfigureBlogPage.TypeOptions.WORDPRESS;
import static org.testng.Assert.*;

/**
 * This class includes Site Blog Sanity tests
 *
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
public class SiteBlogTest extends AbstractUtils
{
    private static final Log logger = LogFactory.getLog(SiteBlogTest.class);
    private final static String BLOG_NAME = "alfrescoqacloud";
    private final static String BLOG_TYPE = ".wordpress.com";

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_AONE_8232() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        //Any user is created
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        //Any site is created
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        //The user is a manager in the Site
        ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, testUser, siteName, UserRole.MANAGER);

        //The user is logged in
        ShareUser.openSiteDashboard(drone, siteName);
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();

        //Add Blog page
        List<SitePageType> pagesToAdd = new ArrayList<>();
        pagesToAdd.add(SitePageType.BLOG);
        customizeSitePage.addPages(pagesToAdd);
    }

    /**
     * Check Blog activities on site
     */
    @Test(groups = "Sanity")
    public void AONE_8232() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String editedItem = "_edited";
        String tagName = "tag1";
        String activityEntry;
        List<String> tagsToAdd = new ArrayList<>();
        String[] postNames = { "draft_post", "internal_post", "external_post" + System.currentTimeMillis() };

        //The user is logged in
        ShareUser.login(drone, testUser);
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName);

        //Blog page is opened
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();

        //Click New Post, fill in the fields and Save as Draft
        PostViewPage postViewPage = blogPage.saveAsDraft(postNames[0], testName, null).render();

        //Verify draft blog post is created. Details of the post are displayed
        assertTrue(postViewPage.isPostCorrect(postNames[0], testName, null), "The post isn't correct");

        //Create internal post
        postViewPage.createPostInternally(postNames[1], testName).render();

        //Edit any post and add a tag to it. Click Update
        tagsToAdd.add(tagName);
        postViewPage.editBlogPostAndUpdate(postNames[1] + editedItem, testName + editedItem, tagsToAdd);

        //Blog post is edited and new tag is added to it
        assertTrue(postViewPage.isPostCorrect(postNames[1] + editedItem, testName + editedItem, tagsToAdd), "The post isn't correct");

        //Click on the tag
        blogPage = postViewPage.clickOnTheTag(tagsToAdd.get(0)).render();

        //The user is redirected to blog post list. Only the posts tagged with selected tag are listed
        assertTrue(drone.getCurrentPage() instanceof BlogPage);
        int postsCount = blogPage.getPostsCount();
        if (postsCount == 0)
        {
            for (int refreshCount = 1; refreshCount < retrySearchCount; refreshCount++)
            {
                webDriverWait(drone, 5000);
                refreshSharePage(drone);
                postsCount = blogPage.getPostsCount();

                if (postsCount >= 1)
                    break;
            }
        }
        assertEquals(postsCount, 1, "The post isn't displayed");
        assertTrue(blogPage.isPostPresented(postNames[1] + editedItem), "The post isn't displayed in the list");

        //Navigate back to the post details and add a comment to the post
        postViewPage = blogPage.openBlogPost(postNames[1] + editedItem).render();
        postViewPage.createBlogComment(testName).render();

        //The comment is added
        assertTrue(postViewPage.isCommentCorrect(testName), "The comment wasn't added");
        //Making driver wait so that activities get indexed
        webDriverWait(drone, -1);
        blogPage = postViewPage.clickBackLink().render();
        postViewPage = blogPage.openBlogPost(postNames[1] + editedItem).render();

        //Edit the comment
        postViewPage.editBlogComment(testName, testName + editedItem).render();
        assertTrue(postViewPage.isCommentCorrect(testName + editedItem), "The comment wasn't edited");
        //Making driver wait so that activities get indexed
        webDriverWait(drone, -1);
        blogPage = postViewPage.clickBackLink().render();
        postViewPage = blogPage.openBlogPost(postNames[1] + editedItem).render();

        //Delete the comment
        postViewPage.deleteCommentWithConfirm(testName + editedItem).render();
        assertFalse(postViewPage.isCommentCorrect(testName + editedItem), "Comment wasn't deleted");

        //Edit the post, Publish Internally (this is for draft post)
        blogPage = postViewPage.clickBackLink().render();
        BlogTreeMenuNavigation blogTreeMenuNavigation = blogPage.getLeftMenus();
        blogTreeMenuNavigation.selectListNode(ALL).render();
        blogPage.openBlogPost(postNames[0]).render();
        postViewPage.editBlogPostAndPublishInternally(postNames[2], testName, tagsToAdd);

        //The post is edited and published internally (verify the post is listed in My Published posts)
        postViewPage.isPostCorrect(postNames[2], testName, tagsToAdd);

        //Verify the post is listed in My Published posts;
        blogPage = postViewPage.clickBackLink().render();
        blogTreeMenuNavigation.selectListNode(MY_PUBLISHED);
        assertTrue(blogPage.isPostPresented(postNames[2]), "The post isn't displayed");

        //Subscribe to RSS feed and open post from RSS list
        RssFeedPage rssFeedPage = postViewPage.clickRssFeedBtn(testUser, DEFAULT_PASSWORD).render();
        assertTrue(rssFeedPage.isSubscribePanelDisplay(), "Subscribe panel isn't available");
        rssFeedPage.clickOnFeedContent(postNames[2]).render();

        //The user is subscribed to RSS feed. The post is opened and displays correctly
        assertTrue(postViewPage.isPostCorrect(postNames[2], testName, tagsToAdd), "The post isn't displayed");

        //Edit the post, Update Internally and Publish Externally
        postViewPage.editBlogPostAndPublishExternally(postNames[2] + editedItem, testName + editedItem, null);
        assertTrue(postViewPage.isPostCorrect(postNames[2] + editedItem, testName + editedItem, tagsToAdd));

        //Verify the post is not listed in Published Externally
        blogPage = postViewPage.clickBackLink().render();
        blogTreeMenuNavigation.selectListNode(PUBLISHED_EXTERNALLY);
        assertFalse(blogPage.isPostPresented(postNames[2] + editedItem), "The post is displayed");

        //Configure External Blog. Provide the valid information
        //blogPage.configureExternalBlog(WORDPRESS, testName, testName, BLOG_NAME + BLOG_TYPE, blogUsername, blogPassword);

        //Edit the post, Update Internally and Publish Externally
        blogTreeMenuNavigation.selectListNode(ALL);
        postViewPage = blogPage.openBlogPost(postNames[2] + editedItem).render();
        postViewPage.editBlogPostAndPublishExternally(postNames[2], testName, null).render();

        //The post is edited and published to External Blog (login to wordpress and ensure the blog is published)
        boolean isPublished = BlogUtil.isPostPublishedToExternalWordpressBlog(drone, postNames[2], BLOG_NAME, blogUsername, blogPassword);

        assertTrue(isPublished, "The post wasn't published to External Blog");

        //Edit the post, Update
        postViewPage.editBlogPostAndUpdate(postNames[2] + editedItem, testName + editedItem, null);

        //The post updated only internally (verify in wordpress). It is Out of sync
        assertTrue(postViewPage.isPostCorrect(postNames[2] + editedItem, testName + editedItem, tagsToAdd), "Post isn't updated");

        isPublished = BlogUtil.isPostPublishedToExternalWordpressBlog(drone, postNames[2] + editedItem, BLOG_NAME, blogUsername, blogPassword);

        assertFalse(isPublished);
        assertTrue(postViewPage.getPostStatus().contains(OUT_OF_SYNC.getPostStatus()), "The post's status is incorrect");

        //Click Update Externally
        postViewPage.clickUpdateExternally().render();

        //The post is updated externally (verify in wordpress). It is no longer Out of sync
        assertTrue(BlogUtil.isPostPublishedToExternalWordpressBlog(drone, postNames[2] + editedItem, BLOG_NAME, blogUsername, blogPassword));
        assertTrue(postViewPage.getPostStatus().contains(UPDATED.getPostStatus()), "The post's status is incorrect");

        //Click Remove Externally
        postViewPage.clickRemoveExternally().render();
        assertTrue(BlogUtil.isPostRemovedFromExternalWordpressBlog(drone, postNames[2] + editedItem, BLOG_NAME, blogUsername, blogPassword));

        //Click Delete. Confirm deletion
        blogPage = postViewPage.deleteBlogPostWithConfirm().render();

        //The blog is deleted
        assertFalse(blogPage.isPostPresented(postNames[2] + editedItem), "The post wasn't deleted");

        //Go to Site Dashboard activities and ensure all activities are displayed
        ShareUser.openSiteDashboard(drone, siteName);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_CREATED + FEED_FOR_BLOG_POST + postNames[1];
        Boolean entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_COMMENTED_ON + " " + postNames[1] + editedItem;
        entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_UPDATED_COMMENT_ON + " " + postNames[1] + editedItem;
        entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_COMMENT_DELETED + FEED_COMMENTED_FROM + postNames[1] + editedItem;
        entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_UPDATED + FEED_FOR_BLOG_POST + postNames[2];
        entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_UPDATED + FEED_FOR_BLOG_POST + postNames[2] + editedItem;
        entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_UPDATED + FEED_FOR_BLOG_POST + postNames[2];
        entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_UPDATED + FEED_FOR_BLOG_POST + postNames[2] + editedItem;
        entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_DELETED + FEED_FOR_BLOG_POST + postNames[2] + editedItem;
        entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        //Go to My Dashboard activities and ensure all activities are displayed

        ShareUser.openUserDashboard(drone).render();

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_CREATED + FEED_FOR_BLOG_POST + postNames[1] + FEED_LOCATION + siteName;
        entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_COMMENTED_ON + " " + postNames[1] + editedItem + FEED_LOCATION + siteName;
        entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_UPDATED_COMMENT_ON + " " + postNames[1] + editedItem + FEED_LOCATION + siteName;
        entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_COMMENT_DELETED + FEED_COMMENTED_FROM + postNames[1] + editedItem + FEED_LOCATION + siteName;
        entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_UPDATED + FEED_FOR_BLOG_POST + postNames[2] + FEED_LOCATION + siteName;
        entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_UPDATED + FEED_FOR_BLOG_POST + postNames[2] + editedItem + FEED_LOCATION + siteName;
        entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_UPDATED + FEED_FOR_BLOG_POST + postNames[2] + FEED_LOCATION + siteName;
        entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_UPDATED + FEED_FOR_BLOG_POST + postNames[2] + editedItem + FEED_LOCATION + siteName;
        entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);
        assertTrue(entryFound, "Unable to find " + activityEntry);

        activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_DELETED + FEED_FOR_BLOG_POST + postNames[2] + editedItem + FEED_LOCATION + siteName;
        entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);
        assertTrue(entryFound, "Unable to find " + activityEntry);
    }
}
