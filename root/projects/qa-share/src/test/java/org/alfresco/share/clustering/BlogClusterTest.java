/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.clustering;

import static org.alfresco.po.share.enums.BlogPostStatus.DRAFT;
import static org.alfresco.po.share.enums.BlogPostStatus.UPDATED;
import static org.alfresco.po.share.site.blog.BlogTreeMenuNavigation.PostsMenu.ALL;
import static org.alfresco.po.share.site.blog.BlogTreeMenuNavigation.PostsMenu.MY_DRAFTS;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.share.site.blog.BlogTreeMenuNavigation;
import org.alfresco.po.share.site.blog.PostViewPage;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class BlogClusterTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(BlogClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    private String testUser;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName);
        logger.info("Starting Tests: " + testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        /*
         * String shareJmxPort = getAlfrescoServerProperty("Alfresco:Type=Configuration,Category=sysAdmin,id1=default", "share.port").toString();
         * boolean clustering_enabled_jmx = (boolean) getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusteringEnabled");
         * if (clustering_enabled_jmx)
         * {
         * Object clustering_url = getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusterMembers");
         * try
         * {
         * CompositeDataSupport compData = (CompositeDataSupport) ((TabularDataSupport) clustering_url).values().toArray()[0];
         * String clusterIP = compData.values().toArray()[0] + ":" + shareJmxPort;
         * CompositeDataSupport compData2 = (CompositeDataSupport) ((TabularDataSupport) clustering_url).values().toArray()[1];
         * String clusterIP2 = compData2.values().toArray()[0] + ":" + shareJmxPort;
         * node1Url = shareUrl.replace(shareIP, clusterIP);
         * node2Url = shareUrl.replace(shareIP, clusterIP2);
         * }
         * catch (Throwable ex)
         * {
         * throw new SkipException("Skipping as pre-condition step(s) fail");
         * }
         * }
         */
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        if (clusterMembers.size() >= 2)
        {
            node1Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(0) + ":" + nodePort);
            node2Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(1) + ":" + nodePort);
        }
        else
        {
            throw new PageOperationException("Number of cluster members is less than two");
        }
    }

    /**
     * Test - AONE_15906:Creating a Published Internally blog post
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Blog page for site is opened</li>
     * <li>Click "New Post" button</li>
     * <li>Type a Title for the post</li>
     * <li>Type the post Content in the Text box</li>
     * <li>Type a tag in the box provided and click "Add" button</li>
     * <li>Click "Publish internally" button</li>
     * <li>The new post appears and is published to the internal blog</li>
     * <li>Go to server B to the current site and verify presence of blog post created</li>
     * <li>The new created blog post is displayed correctly on server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9206() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String tagName = getTagName(testName);
        String blogName = "blog" + testName;
        String postText = "postText" + getRandomString(10);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Blog page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.BLOG);

        // create blog post with tag
        // open blog page and create internal post
        BlogPage blogPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectBlogPage().render();
        PostViewPage postViewPage = blogPage.createPostInternally(blogName, postText, tagName);

        // The new post appears and is published to the internal blog
        assertTrue(postViewPage.verifyPostExists(blogName), "Post name '" + blogName + "' is not displayed at blog detail page. Server A.");
        assertTrue(postViewPage.getTagName().contains(tagName), "Post tag '" + tagName + "' is not displayed at blog detail page. Server A.");
        assertTrue(postViewPage.getPromptText().contains(postText), "Post text is not displayed at blog detail page. Server A.");

        // Go to blog post list page
        blogPage = postViewPage.clickBackLink();

        // Verify The new created blog post is displayed correctly on server A
        assertTrue(blogPage.isPostPresented(blogName), "Post with name '" + blogName + "' is not presented. Server A");
        assertTrue(blogPage.checkTags(blogName, tagName), "Tag '" + tagName + "'  is not presented for post with name '" + blogName + "'. Server A");

        ShareUser.logout(drone);

        // verify that created blog post at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        blogPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectBlogPage().render();

        // Verify The new created blog post is displayed correctly on server B
        assertTrue(blogPage.isPostPresented(blogName), "Post with name '" + blogName + "' is not presented. Server A");
        assertTrue(blogPage.checkTags(blogName, tagName), "Tag '" + tagName + "'  is not presented for post with name '" + blogName + "'. Server A");

        // Open post detail page
        postViewPage = blogPage.openBlogPost(blogName);

        // Verify The new created blog post is displayed correctly on server B
        assertTrue(postViewPage.verifyPostExists(blogName), "Post name '" + blogName + "' is not displayed at blog detail page. Server B.");
        assertTrue(postViewPage.getTagName().contains(tagName), "Post tag '" + tagName + "' is not displayed at blog detail page. Server B.");
        assertTrue(postViewPage.getPromptText().contains(postText), "Post text is not displayed at blog detail page. Server B.");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15907:Creating a Draft blog post
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Blog page for site is opened</li>
     * <li>Click "New Post" button</li>
     * <li>Type a Title for the post</li>
     * <li>Type the post Content in the Text box</li>
     * <li>Type a tag in the box provided and click "Add" button</li>
     * <li>Click "Save as Draft" button</li>
     * <li>Verify the new post appears. The text (Draft) appears</li>
     * <li>Go to server B to the current site</li>
     * <li>Click 'My Drafts' in 'Posts' section of Blog page</li>
     * <li>The new created blog post is displayed correctly on server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9207() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String tagName = getTagName(testName);
        String blogName = "blog" + testName;
        String postText = "postText" + getRandomString(10);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Blog page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.BLOG);

        // create blog post with tag
        // open blog page and Click "Save as Draft" button
        BlogPage blogPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectBlogPage().render();
        List<String> tagNameList = new ArrayList<>();
        tagNameList.add(tagName);
        PostViewPage postViewPage = blogPage.saveAsDraft(blogName, postText, tagNameList).render();

        // The new post appears as draft
        assertTrue(postViewPage.verifyPostExists(blogName), "Post name '" + blogName + "' is not displayed at blog detail page. Server A.");
        assertTrue(postViewPage.getTagName().contains(tagName), "Post tag '" + tagName + "' is not displayed at blog detail page. Server A.");
        assertTrue(postViewPage.getPromptText().contains(postText), "Post text is not displayed at blog detail page. Server A.");
        List<String> postStatus = postViewPage.getPostStatus();
        assertTrue(postStatus.contains(DRAFT.getPostStatus()), "The post status is incorrect");

        // Go to blog post list page
        blogPage = postViewPage.clickBackLink();

        // Click 'My Drafts' in 'Posts' section of Blog page
        BlogTreeMenuNavigation blogTreeMenuNavigation = blogPage.getLeftMenus().render();
        blogPage = blogTreeMenuNavigation.selectListNode(MY_DRAFTS).render();

        // Verify The new created blog post is displayed correctly on server A
        assertTrue(blogPage.isPostPresented(blogName), "Post with name '" + blogName + "' is not presented. Server A");
        assertTrue(blogPage.checkTags(blogName, tagName), "Tag '" + tagName + "'  is not presented for post with name '" + blogName + "'. Server A");

        ShareUser.logout(drone);

        // verify that created blog post at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectBlogPage().render();

        // Click 'My Drafts' in 'Posts' section of Blog page
        blogPage = blogTreeMenuNavigation.selectListNode(MY_DRAFTS).render();

        // Verify The new created blog post is displayed correctly on server B
        assertTrue(blogPage.isPostPresented(blogName), "Post with name '" + blogName + "' is not presented. Server A");
        assertTrue(blogPage.checkTags(blogName, tagName), "Tag '" + tagName + "'  is not presented for post with name '" + blogName + "'. Server A");

        // Open post detail page
        postViewPage = blogPage.openBlogPost(blogName);

        // Verify The new created blog post is displayed correctly on server B
        assertTrue(postViewPage.verifyPostExists(blogName), "Post name '" + blogName + "' is not displayed at blog detail page. Server B.");
        assertTrue(postViewPage.getTagName().contains(tagName), "Post tag '" + tagName + "' is not displayed at blog detail page. Server B.");
        assertTrue(postViewPage.getPromptText().contains(postText), "Post text is not displayed at blog detail page. Server B.");
        postStatus = postViewPage.getPostStatus();
        assertTrue(postStatus.contains(DRAFT.getPostStatus()), "The post status is incorrect");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15908:Editing blog post. Simple update
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Blog page for site is opened</li>
     * <li>At least one blog post is created</li>
     * <li>Click "Edit" to the right of the post</li>
     * <li>Type a new Title for the post</li>
     * <li>Type a new post content in the Text box</li>
     * <li>Remove an existing tag(s) from the set beneath the Text box clicking the tag(s)</li>
     * <li>Click "Update" button</li>
     * <li>Go to server B to the current site and verify updated blog post</li>
     * <li>Verify all changes are applied for Blog post on server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9208() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String tagName = getTagName(testName);
        String blogName = "blog" + testName;
        String newPostName = "blog" + getRandomString(10);
        String postText = "postText" + getRandomString(10);
        String newPostText = "postText" + getRandomString(10);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Blog page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.BLOG);

        // create blog post with tag
        // open blog page and create internal post
        BlogPage blogPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectBlogPage().render();
        PostViewPage postViewPage = blogPage.createPostInternally(blogName, postText, tagName);

        // The new post appears and is published to the internal blog
        assertTrue(postViewPage.verifyPostExists(blogName), "Post name '" + blogName + "' is not displayed at blog detail page. Server A.");
        assertTrue(postViewPage.getTagName().contains(tagName), "Post tag '" + tagName + "' is not displayed at blog detail page. Server A.");
        assertTrue(postViewPage.getPromptText().contains(postText), "Post text is not displayed at blog detail page. Server A.");

        // Go to blog post list page
        blogPage = postViewPage.clickBackLink();

        // Verify The new created blog post is displayed correctly on server A
        assertTrue(blogPage.isPostPresented(blogName), "Post with name '" + blogName + "' is not presented. Server A");
        assertTrue(blogPage.checkTags(blogName, tagName), "Tag '" + tagName + "'  is not presented for post with name '" + blogName + "'. Server A");

        // Edit post
        blogPage.editPost(blogName, newPostName, newPostText, tagName, true).render();

        // Verify all changes are applied for Blog post on server A
        assertTrue(postViewPage.verifyPostExists(newPostName), "New post name '" + newPostName
                + "' is not displayed at blog detail page (after update). Server A.");
        assertTrue(postViewPage.getPromptText().contains(newPostText), "New post text is not displayed at blog detail page. Server A.");
        assertTrue(postViewPage.getPostStatus().contains(UPDATED.getPostStatus()), "The post status is incorrect. Server A.");

        postViewPage.clickBackLink().render();

        // Verify all changes are applied for Blog post on server A
        assertTrue(blogPage.isPostPresented(newPostName), "New post with name '" + newPostName + "' is not presented. Server A");
        assertTrue(blogPage.checkTags(newPostName, null), "Tag is presented for post with name '" + newPostName + "'. Server A");

        ShareUser.logout(drone);

        // verify that created blog post at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        blogPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectBlogPage().render();

        // Verify all changes are applied for Blog post on server B
        assertTrue(blogPage.isPostPresented(newPostName), "New post with name '" + newPostName + "' is not presented. Server B");
        assertTrue(blogPage.checkTags(newPostName, null), "Tag is presented for post with name '" + newPostName + "'. Server B");

        // Open post detail page
        postViewPage = blogPage.openBlogPost(newPostName);

        // Verify all changes are applied for Blog post on server B
        assertTrue(postViewPage.verifyPostExists(newPostName), "Post name '" + newPostName + "' is not displayed at blog detail page (after update). Server B.");
        assertTrue(postViewPage.getPromptText().contains(newPostText), "Post text is not displayed at blog detail page. Server B.");
        assertTrue(postViewPage.getPostStatus().contains(UPDATED.getPostStatus()), "The post status is incorrect. Server B.");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15909:Deletion a blog posts
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>At least one Draft post</li>
     * <li>At least one Published Internally post</li>
     * <li>At least one Published Externally post</li>
     * <li>Blog page for site is opened</li>
     * <li>Blog component page is opened at "All" view</li>
     * <li>In the post list, locate any Draft post you want to delete and click "Delete" to the right of the post</li>
     * <li>The post is not displayed in the list</li>
     * <li>In the post list, locate any Published Internally post you want to delete and click "Delete" to the right of the post</li>
     * <li>The post is not displayed in the list</li>
     * <li>In the post list, locate any Published Externally post you want to delete and click "Delete" to the right of the post</li>
     * <li>The post is not displayed in the list</li>
     * <li>Go to server B and verify deletion of blog posts</li>
     * <li>All posts were deleted on server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9209() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String tagName = getTagName(testName);
        String blogNameDraft = "blogNameDraft" + testName;
        String blogNameInternal = "blogNameInternal" + testName;
        String blogNameExternal = "blogNameExternal" + testName;
        String postText = "postText" + getRandomString(10);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Blog page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.BLOG);

        // create blog post with tag
        // open blog page and create internal post
        BlogPage blogPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectBlogPage().render();
        PostViewPage postViewPage = blogPage.createPostInternally(blogNameInternal, postText, tagName);
        postViewPage.clickBackLink().render();
        //blogPage.configureExternalBlog(WORDPRESS, blogNameExternal, blogNameExternal, blogUrl, blogUsername, blogPassword);

        // create at least one Published Externally post
        //postViewPage = blogPage.createPostExternally(blogNameExternal, postText, null);
        postViewPage.clickBackLink().render();

        // create At least one Draft post
        postViewPage = blogPage.saveAsDraft(blogNameDraft, postText, null).render();
        postViewPage.clickBackLink().render();

        BlogTreeMenuNavigation blogTreeMenuNavigation = blogPage.getLeftMenus().render();
        // Blog component page is opened at "All" view
        blogPage = blogTreeMenuNavigation.selectListNode(ALL).render();
        // Check that all posts presented
        assertTrue(blogPage.isPostPresented(blogNameDraft), "Post (Draft) with name '" + blogNameDraft + "' is not presented. Server A");
        assertTrue(blogPage.isPostPresented(blogNameInternal), "Post (Internal) with name '" + blogNameInternal + "' is not presented. Server A");
        assertTrue(blogPage.isPostPresented(blogNameExternal), "Post (External) with name '" + blogNameExternal + "' is not presented. Server A");

        // Delete Draft post
        blogPage.openBlogPost(blogNameDraft);
        blogPage = postViewPage.deleteBlogPostWithConfirm().render();
        blogTreeMenuNavigation.selectListNode(ALL).render();

        // Check deletion
        assertFalse(blogPage.isPostPresented(blogNameDraft), "Post (Draft) with name '" + blogNameDraft + "' is presented. Server A");

        // Delete Internal post
        blogPage.openBlogPost(blogNameInternal);
        blogPage = postViewPage.deleteBlogPostWithConfirm().render();
        blogTreeMenuNavigation.selectListNode(ALL).render();

        // Check deletion
        assertFalse(blogPage.isPostPresented(blogNameInternal), "Post (Internal) with name '" + blogNameInternal + "' is presented. Server A");

        // Delete External post
        blogPage.openBlogPost(blogNameExternal);
        blogPage = postViewPage.deleteBlogPostWithConfirm().render();
        blogTreeMenuNavigation.selectListNode(ALL).render();

        // Check deletion
        assertFalse(blogPage.isPostPresented(blogNameExternal), "Post (External) with name '" + blogNameExternal + "' is presented. Server A");

        ShareUser.logout(drone);

        // verify that blog posts were deleted at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        blogPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectBlogPage().render();

        // Blog component page is opened at "All" view
        blogTreeMenuNavigation.selectListNode(ALL).render();

        // All blog posts created in previously are missing on server B
        assertFalse(blogPage.isPostPresented(blogNameDraft), "Post (Draft) with name '" + blogNameDraft + "' is presented. Server B");
        assertFalse(blogPage.isPostPresented(blogNameInternal), "Post (Internal) with name '" + blogNameInternal + "' is presented. Server B");
        assertFalse(blogPage.isPostPresented(blogNameExternal), "Post (External) with name '" + blogNameExternal + "' is presented. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15910:Adding comment
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>At least one blog post is created</li>
     * <li>Blog's details page for created blog is opened</li>
     * <li>Enter any text in 'Add comment' section</li>
     * <li>Click 'Create Comment'  button</li>
     * <li>Comment is added</li>
     * <li>Go to server B to the current site</li>
     * <li>verify Blog's details page for created blog</li>
     * <li>New comment is displayed on Details page</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9210() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String tagName = getTagName(testName);
        String blogName = "blog" + testName;
        String postText = "postText" + getRandomString(10);
        String commentText = "commentText" + getRandomString(10);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Blog page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.BLOG);

        // create blog post with tag
        // open blog page and create internal post
        BlogPage blogPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectBlogPage().render();
        PostViewPage postViewPage = blogPage.createPostInternally(blogName, postText, tagName);

        // The new post appears and is published to the internal blog
        assertTrue(postViewPage.verifyPostExists(blogName), "Post name '" + blogName + "' is not displayed at blog detail page. Server A.");

        //Click 'Create Comment'  button
        assertTrue(postViewPage.isAddCommentDisplayed());
        postViewPage.createBlogComment(commentText).render();
        assertTrue(postViewPage.isCommentCorrect(commentText),"Comment isn't added. Server A");

        ShareUser.logout(drone);

        // verify that created blog post at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        blogPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectBlogPage().render();

        // Verify The new created blog post is displayed correctly on server B
        assertTrue(blogPage.isPostPresented(blogName), "Post with name '" + blogName + "' is not presented. Server B");

        // Open post detail page
        postViewPage = blogPage.openBlogPost(blogName);

        // Verify New comment is displayed on Details page on server B
        assertTrue(postViewPage.verifyPostExists(blogName), "Post name '" + blogName + "' is not displayed at blog detail page. Server B.");
        assertTrue(postViewPage.isCommentCorrect(commentText),"Comment isn't added. Server B");

        ShareUser.logout(drone);
    }
}