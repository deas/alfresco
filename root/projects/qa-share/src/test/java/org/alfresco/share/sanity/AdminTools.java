package org.alfresco.share.sanity;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.adminconsole.CategoryManagerPage;
import org.alfresco.po.share.adminconsole.NodeBrowserPage;
import org.alfresco.po.share.adminconsole.TagManagerPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.share.site.blog.PostViewPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.InformationEventForm;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.site.discussions.TopicViewPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.site.links.LinksDetailsPage;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.po.share.user.TrashCanPage;
import org.alfresco.share.site.document.DocumentDetailsActionsTest;
import org.alfresco.share.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.alfresco.po.share.site.document.Categories.CATEGORY_ROOT;
import static org.alfresco.po.share.site.document.DocumentAspect.CLASSIFIABLE;
import static org.testng.Assert.*;

/**
 * This class contains the sanity tests for admin tools
 * 
 * @author Antonik Olga
 */
public class AdminTools extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(DocumentDetailsActionsTest.class);
    protected String testUser;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void ALF_3088() throws IOException, TimeoutException, InterruptedException
    {

        // start test
        testName = getTestName();

        // test data setup
        String siteName = getSiteName(testName) + getRandomString(3);
        String picture = "channel-test-jpg.jpg";
        String defaultLogo = "app-logo-48.png";
        try
        {

            // admin logs in
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // create Site
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // get all themes
            AdminConsolePage.ThemeType[] themes = AdminConsolePage.ThemeType.values();

            for (AdminConsolePage.ThemeType theme : themes)
            {
                // navigate to Admin Console page (Application)
                AdminConsolePage adminConsolePage = ApplicationPageUtil.openApplicationPage(drone);

                // change the Theme
                adminConsolePage.selectTheme(theme).render();
                assertTrue(adminConsolePage.isThemeSelected(theme));

                // go to My Dashboard and verify changes are applied (verify color of link on Welcome dashboard: Create a site)
                DashBoardPage dashBoardPage = ShareUser.openUserDashboard(drone).render();
                String color = dashBoardPage.getColor(ShareUserDashboard.CREATE_SITE_BUTTON, false);
                assertTrue(color.equals(theme.hexTextColor));

                // go to Site Dashboard and verify changes are applied
                SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
                color = siteDashboardPage.getColor(ShareUserSitePage.INVITE_TO_SITE, false);
                assertTrue(color.equals(theme.hexTextColor));

            }

            // navigate to Admin Console page (Application)
            AdminConsolePage adminConsolePage = ApplicationPageUtil.openApplicationPage(drone);

            // upload any picture file to Logo (not more than recommended). Apply the changes
            File pic = new File(DATA_FOLDER + SLASH + picture);
            String srcBeforeUpload = drone.find(AdminConsolePage.LOGO_PICTURE).getAttribute("src");
            adminConsolePage.uploadPicture(pic.getCanonicalPath()).render();
            String srcAfterUpload = drone.find(AdminConsolePage.LOGO_PICTURE).getAttribute("src");

            assertNotEquals(srcBeforeUpload, srcAfterUpload, "New picture wasn't  uploaded or picture for upload was already uploaded earlier");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // return Light Theme (by default) back
            // admin logs in
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // navigate to Admin Console page (Application)
            AdminConsolePage adminConsolePage = ApplicationPageUtil.openApplicationPage(drone);

            // change the Theme
            adminConsolePage.selectTheme(AdminConsolePage.ThemeType.light).render();

            // return default logo picture back
            File defaultPic = new File(DATA_FOLDER + SLASH + defaultLogo);
            adminConsolePage.uploadPicture(defaultPic.getCanonicalPath()).render();

            ShareUser.logout(drone);
        }
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void ALF_3089() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String subCategory = getRandomString(5);

        List<DocumentAspect> aspects = new ArrayList<>();
        aspects.add(CLASSIFIABLE);

        /**
         * Precondition:
         * 1. Any Site is created
         * 2. Any documnent is created in DocLib
         */

        // admin logs in
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // admin creates site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);
        new File(DATA_FOLDER + SLASH + fileName).delete();

        // Test

        // as admin user go to Admin Console > Category Manager page
        CategoryManagerPage categoryManagerPage = CategoryManagerPageUtil.openCategoryManagerPage(drone);

        // expand root category
        categoryManagerPage.expandCategoryRootTree();

        assertTrue(categoryManagerPage.isCategoryRootTreeExpanded());

        // add any new sub-category using Add Category icon
        categoryManagerPage.addNewCategory(CATEGORY_ROOT.getValue(), subCategory);

        // verify that new category was added
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        categoryManagerPage = CategoryManagerPageUtil.openCategoryManagerPage(drone).render();
        if(!categoryManagerPage.isCategoryPresent(subCategory))
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            categoryManagerPage = CategoryManagerPageUtil.openCategoryManagerPage(drone).render();

        }
        assertTrue(categoryManagerPage.isCategoryPresent(subCategory));

        // add Classifiable aspect to any document
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName);
        SelectAspectsPage selectAspectsPage = documentDetailsPage.selectManageAspects();
        selectAspectsPage.add(aspects);
        documentDetailsPage = selectAspectsPage.clickApplyChanges().render();

        Map<String, Object> props = documentDetailsPage.getProperties();
        props.put("Categories", subCategory);

        // add newly created category to the document
        EditDocumentPropertiesPage editDocumentProperties = documentDetailsPage.selectEditProperties().render();
        CategoryPage categoryPage = editDocumentProperties.getCategory().render();
        categoryPage.addCategories(Arrays.asList(subCategory)).render();
        categoryPage.clickOk();
        editDocumentProperties.clickSave();

        documentDetailsPage = ShareUser.getSharePage(drone).render();
        assertEquals(documentDetailsPage.getElementText(By.xpath("//div[@class='itemtype-cm:category']")), subCategory);

        // edit any sub-category using Edit Category icon
        categoryManagerPage = CategoryManagerPageUtil.openCategoryManagerPage(drone);
        categoryManagerPage.editCategory(subCategory, subCategory + 1);

        assertTrue(categoryManagerPage.isCategoryPresent(subCategory + 1));

        // navigate to DocLib, expand Categories, click on the category
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        TreeMenuNavigation treeMenuNavigation = documentLibraryPage.getLeftMenus();
        documentLibraryPage = treeMenuNavigation.selectNode(TreeMenuNavigation.TreeMenu.CATEGORIES, drone.getValue(TreeMenuNavigation.CATEGORY_ROOT_PROPERTY),
                subCategory + 1).render();

        assertTrue(documentLibraryPage.isFileVisible(fileName));

        // delete any sub-category using Delete Category icon
        categoryManagerPage = CategoryManagerPageUtil.openCategoryManagerPage(drone);
        categoryManagerPage.deleteCategory(subCategory + 1);

        assertFalse(categoryManagerPage.isCategoryPresent(subCategory + 1));

        // navigate to DocLib, expand Categories, look for the category
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertFalse(documentLibraryPage.getAllCategoriesNames().contains(subCategory + 1));

        ShareUser.logout(drone);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void ALF_3090() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String fileName = "testSearch.xml";
        String text = "testing";

        /**
         * Precondition
         * 1. A TestSite site created
         * 2. testSearch.xml is added to test site (with testing content inside it)
         */

        // admin logs in
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // admin creates site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // upload xml File
        File file = new File(DATA_FOLDER + SLASH + fileName);
        ShareUserSitePage.uploadFile(drone, file);

        // get node ref of document
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        String nodeRef = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).getContentNodeRef();

        nodeRef = nodeRef.substring(nodeRef.indexOf("workspace"));
        nodeRef = nodeRef.replaceFirst("/", "://");

        // as admin go to Admin Console - Node Browser page
        NodeBrowserPage nodeBrowserPage = NodeBrowserPageUtil.openNodeBrowserPage(drone).render();

        // select "nodref" search, select store: workspace://SpacesStore and enter the noderef of TestDoc and click Search button
        nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, nodeRef, NodeBrowserPage.QueryType.NODE_REF, NodeBrowserPage.Store.WORKSPACE_SPACE_STORE)
                .render();

        // verify that testSearch.xml displays in search results
        assertTrue(nodeBrowserPage.isInResultsByName(fileName), "Nothing was found or there was found incorrect file by node ref");

        // elect "xpath" search, enter any xpath search (e.g. /app:company_home/st:sites/cm:testsite/cm:documentLibrary/cm:testSearch.xml) and click Search
        // button
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        nodeBrowserPage = NodeBrowserPageUtil.openNodeBrowserPage(drone).render();
        nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone,
                "/app:company_home/st:sites/cm:" + siteName.toLowerCase() + "/cm:documentLibrary/cm:" + fileName, NodeBrowserPage.QueryType.XPATH).render();

        // verify that testSearch.xml displays in search results
        assertTrue(nodeBrowserPage.isInResultsByName(fileName) && nodeBrowserPage.isInResultsByNodeRef(nodeRef),
                "Nothing was found or there was found incorrect file by xpath");

        // select "lucene" search, enter testing and click Search button
        nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, text, NodeBrowserPage.QueryType.LUCENE).render();

        assertTrue(nodeBrowserPage.isInResultsByName(fileName) && nodeBrowserPage.isInResultsByNodeRef(nodeRef),
                "Nothing was found or there was found incorrect file by text of document (lucene)");

        // select "fts-alfresco" search, enter "cm:name:testSearch" and click Search button
        nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, "cm:name:" + fileName, NodeBrowserPage.QueryType.FTS_ALFRECO).render();

        assertTrue(nodeBrowserPage.isInResultsByName(fileName) && nodeBrowserPage.isInResultsByNodeRef(nodeRef),
                "Nothing was found or there was found incorrect file by fts-alfresco (cm:name:testSearch.xml)");

        // select "cmis-strict" search, enter "SELECT * from cmis:document where cmis:name =  'testSearch.xml'" and click Search button
        String query = "SELECT * from cmis:document where cmis:name ='" + fileName + "'";
        nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, query, NodeBrowserPage.QueryType.CMIS_STRICT).render();

        assertTrue(nodeBrowserPage.isInResultsByName(fileName) && nodeBrowserPage.isInResultsByNodeRef(nodeRef),
                "Nothing was found or there was found incorrect file by cmis-strict " + "('SELECT * from cmis:document where cmis:name='testSearch.xml'')");

        // select "cmis-alfresco" search, enter "SELECT * from cmis:document where cmis:name ='testSearch.xml'" and click Search button
        nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, query, NodeBrowserPage.QueryType.CMIS_ALFRESCO).render();

        assertTrue(nodeBrowserPage.isInResultsByName(fileName) && nodeBrowserPage.isInResultsByNodeRef(nodeRef),
                "Nothing was found or there was found incorrect file by cmis-alfresco " + "('SELECT * from cmis:document where cmis:name='testSearch.xml'')");

        ShareUser.logout(drone);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void ALF_3091() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String tagName = getTagName(testName);
        String fileName = getFileName(testName);
        String folderName = getFolderName(testName);
        String wiki = "wiki_" + testName;
        String blog = "blog" + testName;
        String link = "link_" + testName;
        String event = "event_" + testName;
        String topic = "topic_" + testName;
        String none_tag = "(None)";
        String TAG_NODE = "//a[text()='%s']";

        /**
         * Precondition:
         * 1. A TestSite site created
         * 2. New document, folder, wiki page, blog post, link, calendar event, topic are created and TestTag tag added to them
         */
        // admin logs in
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // admin creates site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        documentLibraryPage = documentLibraryPage.getNavigation().selectDetailedView().render();

        // upload File
        String[] info = { fileName, DOCLIB };
        documentLibraryPage = ShareUser.uploadFileInFolder(drone, info).render();
        new File(DATA_FOLDER + SLASH + fileName).delete();
        // create folder
        documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, folderName);

        // add tag to the uploaded file
        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(fileName);
        fileInfo.addTag(tagName);

        // add tag to the created folder
        fileInfo = documentLibraryPage.getFileDirectoryInfo(folderName);
        fileInfo.addTag(tagName);

        // create wiki page with tag
        SitePageType[] pages = new SitePageType[] { SitePageType.WIKI, SitePageType.BLOG, SitePageType.CALENDER, SitePageType.DISCUSSIONS, SitePageType.LINKS};
        ShareUser.openSiteDashboard(drone, siteName).render();
        ShareUserDashboard.addPageToSite(drone, siteName, pages);

        // create link with tag
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        LinksPage linksPage = siteDashboardPage.getSiteNav().selectLinksPage().render();
        linksPage.clickNewLink();
        linksPage.createLink(link, link, tagName).render();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        WikiUtils.createWikiPage(drone, siteName, wiki, wiki, tagName);

        // create blog post with tag
        // open blog page and create internal post
        BlogPage blogPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectBlogPage().render();
        PostViewPage postViewPage = blogPage.createPostInternally(blog, blog, tagName);

        assertNotNull(postViewPage);
        assertTrue(postViewPage.verifyPostExists(blog));


        // create event with tag
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        CalendarPage calendarPage = siteDashboardPage.getSiteNav().selectCalendarPage().render();
        calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event, event, event, null, null, null, null, tagName, false);

        // create topic with tag
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        DiscussionsPage discussionsPage = siteDashboardPage.getSiteNav().selectDiscussionsPage().render();
        discussionsPage.createTopic(topic, topic, tagName);

        // Test

        // admin logs in, as admin go to Admin Console > Tag Management page
        TagManagerPage tagManagerPage = TagManagerPageUtil.openTagManagerPage(drone);

        // search for tag
        tagManagerPage = TagManagerPageUtil.findTag(drone, tagName);

        assertTrue(tagManagerPage.isInResults(tagName));

        // edit the tag (from testTag to Tag)
        assertTrue(TagManagerPageUtil.editTagAndVerify(drone, tagName, tagName + 1));

        tagName += 1;

        for (int i = 0; i < 2; i++)
        {
            // navigate to the Document Library page
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

            if (i == 0)
            {
                // verify that document, folder are tagged with "Tag" tag
                TreeMenuNavigation treeMenuNavigation = documentLibraryPage.getLeftMenus().render();
                treeMenuNavigation.selectTagNode(tagName).render();
                assertTrue(documentLibraryPage.isFileVisible(fileName));
                assertTrue(documentLibraryPage.isItemVisble(folderName));

            }
            else
            {
                // verify that deleted tag doesn't displayed oin Document Library
                assertFalse(drone.isElementDisplayed(By.xpath(TreeMenuNavigation.TreeMenu.TAGS.getXpath() + String.format(TAG_NODE, tagName))));
            }

            // navigate to the Wiki page and get tag
            String tag = WikiUtils.getWikiTag(drone, siteName, wiki);

            if (i == 0)
            {
                // verify that wiki page is tagged with "Tag" tag
                assertEquals(tagName, tag);
            }
            else
            {
                // verify that wiki page is not tagged with "Tag" tag already
                assertEquals(none_tag, tag);
            }

            // navigate to the Blog page
            blogPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectBlogPage().render();

            // open details page of created blog
            postViewPage = blogPage.openBlogPost(blog).render();

            // get tag
            tag = postViewPage.getTagName();

            if (i == 0)
            {
                // verify that blog post is tagged with "Tag" tag
                assertEquals(tagName, tag);
            }
            else
            {
                // verify that blog post is not tagged with "Tag" tag already
                assertEquals(none_tag, tag);
            }

            // navigate to the Links page
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
            linksPage = siteDashboardPage.getSiteNav().selectLinksPage().render();

            // open details page of the link
            LinksDetailsPage linksDetailsPage = linksPage.clickLink(link).render();

            // get tag
            tag = linksDetailsPage.getTagName();

            if (i == 0)
            {
                // verify that link is tagged with "Tag" tag
                assertEquals(tagName, tag);
            }
            else
            {
                // verify that link is not tagged with "Tag" tag already
                assertEquals(none_tag, tag);
            }

            // navigate to the Calendar page
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
            calendarPage = siteDashboardPage.getSiteNav().selectCalendarPage().render();

            // select created event
            InformationEventForm informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event).render();

            // get tag
            tag = informationEventForm.getTagName();

            if (i == 0)
            {
                // verify that calendar event is tagged with "Tag" tag
                assertEquals(tagName, tag);
            }
            else
            {
                // verify that calendar event is not tagged with "Tag" tag already
                assertEquals(none_tag, tag);
            }

            // navigate to the Discussion page
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
            discussionsPage = siteDashboardPage.getSiteNav().selectDiscussionsPage().render();

            // select topic
            TopicViewPage viewPage = discussionsPage.viewTopic(topic).render();

            // get tag
            tag = viewPage.getTagName();

            if (i == 0)
            {
                // verify that calendar event is tagged with "Tag" tag
                assertEquals(tagName, tag);
            }
            else
            {
                // verify that calendar event is not tagged with "Tag" tag already
                assertEquals(none_tag, tag);
            }

            if (i == 0)
            {
                // delete the tag
                assertTrue(TagManagerPageUtil.deleteTagAndVerify(drone, tagName));

            }
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void ALF_3104() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "1.txt";
        String fileName2 = getFileName(testName) + "2.txt";
        String fileName3 = getFileName(testName) + "3.txt";

        /**
         * Precondition:
         * 1. A TestSite site created
         * 2. Some items are created in TestSite and then deleted
         */
        // admin logs in
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // clean all trashcan
        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone);

        ShareUserProfile.emptyTrashCan(drone, SyncInfoPage.ButtonType.REMOVE);

        // admin creates site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        documentLibraryPage = documentLibraryPage.getNavigation().selectDetailedView().render();

        // upload File
        String[] info = { fileName1, DOCLIB };
        documentLibraryPage = ShareUser.uploadFileInFolder(drone, info).render();
        info = new String[] { fileName2, DOCLIB };
        documentLibraryPage = ShareUser.uploadFileInFolder(drone, info).render();
        info = new String[] { fileName3, DOCLIB };
        documentLibraryPage = ShareUser.uploadFileInFolder(drone, info).render();

        new File(DATA_FOLDER + SLASH + fileName1).delete();
        new File(DATA_FOLDER + SLASH + fileName2).delete();
        new File(DATA_FOLDER + SLASH + fileName3).delete();

        // deleted created items
        documentLibraryPage = drone.getCurrentPage().render();
        FileDirectoryInfo fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1);
        fileDirectoryInfo.delete().render();
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName2);
        fileDirectoryInfo.delete().render();
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName3);
        fileDirectoryInfo.delete().render();

        // Test
        // as admin go to My Profile -> Trashcan pag
        trashCanPage = ShareUserProfile.navigateToTrashCan(drone);
        assertTrue(trashCanPage.getTitle().contains("User Trashcan"));
        assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, fileName1));
        assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, fileName2));
        assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, fileName3));

        // Recover any item by using Recover button and verify the item is recovered
        trashCanPage = ShareUserProfile.recoverTrashCanItem(drone, fileName1).render();

        // verify that item was recovered
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertTrue(documentLibraryPage.isFileVisible(fileName1));

        // delete any item by using Delete button and verify the item is deleted
        trashCanPage = ShareUserProfile.navigateToTrashCan(drone);
        trashCanPage = ShareUserProfile.deleteTrashCanItem(drone, fileName2);
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, fileName2));

        // empty trashcan list by using Empty button
        trashCanPage = ShareUserProfile.emptyTrashCan(drone, SyncInfoPage.ButtonType.REMOVE).render();
        assertFalse(trashCanPage.hasTrashCanItems());

        ShareUser.logout(drone);
    }

}
