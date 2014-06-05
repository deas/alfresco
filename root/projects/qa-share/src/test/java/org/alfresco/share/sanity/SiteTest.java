/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.sanity;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ServerErrorPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.*;
import org.alfresco.po.share.dashlet.mydiscussions.CreateNewTopicPage;
import org.alfresco.po.share.dashlet.mydiscussions.TopicDetailsPage;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.*;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.datalist.NewListForm;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.site.document.DocumentLibraryNavigation;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.alfresco.po.share.dashlet.MyDiscussionsDashlet.LinkType.Topic;
import static org.alfresco.po.share.dashlet.MyDiscussionsTopicsFilter.ALL_TOPICS;
import static org.alfresco.po.share.dashlet.MyDiscussionsTopicsFilter.MY_TOPICS;
import static org.alfresco.po.share.dashlet.SearchLimit.TEN;
import static org.alfresco.po.share.dashlet.SiteContentFilter.*;
import static org.alfresco.po.share.enums.UserRole.CONTRIBUTOR;
import static org.alfresco.po.share.enums.UserRole.MANAGER;
import static org.alfresco.po.share.site.SiteFinderPage.ButtonType.*;
import static org.alfresco.po.share.site.datalist.NewListForm.TypeOptions.CONTACT_LIST;
import static org.alfresco.share.util.SiteUtil.prepareJpg;
import static org.testng.Assert.*;

/**
 * This class contains the sanity tests for site.
 *
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class SiteTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SiteTest.class);

    private static final SitePageType[] PAGE_TYPES = {
            SitePageType.BLOG,
            SitePageType.CALENDER,
            SitePageType.DATA_LISTS,
            SitePageType.DISCUSSIONS,
            SitePageType.LINKS,
            SitePageType.WIKI
    };

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_ALF_3086() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName);
        String testUser2 = testUser1 + "2";
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);
    }

    @Test(groups = "Sanity")
    public void ALF_3086() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName);
        String testUser2 = testUser1 + "2";
        String siteNamePublic = getSiteName(testName);
        String siteNameModerated = siteNamePublic + "Mod";
        String siteNamePrivate = siteNamePublic + "Pr";

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, siteNamePublic, SITE_VISIBILITY_PUBLIC);
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(PAGE_TYPES));

        ShareUser.createSite(drone, siteNameModerated, SITE_VISIBILITY_MODERATED);
        customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(PAGE_TYPES));

        ShareUser.createSite(drone, siteNamePrivate, SITE_VISIBILITY_PRIVATE);
        customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(PAGE_TYPES));

        ShareUser.openUserDashboard(drone);
        MySitesDashlet mySitesDashlet = ShareUser.getDashlet(drone, "my-sites").render();
        String errorMessage = "Site[%s] not added to favourite";
        assertTrue(mySitesDashlet.isSiteFavourite(siteNamePublic), String.format(errorMessage, siteNamePublic));
        assertTrue(mySitesDashlet.isSiteFavourite(siteNameModerated), String.format(errorMessage, siteNameModerated));
        assertTrue(mySitesDashlet.isSiteFavourite(siteNamePrivate), String.format(errorMessage, siteNamePrivate));

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        SiteFinderPage siteFinderPage = SiteUtil.searchSite(drone, siteNamePublic);
        assertTrue(siteFinderPage.hasResults(), format("Search [%s] didn't return results", siteNamePublic));
        List<String> resultsList = siteFinderPage.getSiteList();
        assertEquals(resultsList.size(), 2, format("Search [%s] return wrong results count", siteNamePublic));
        assertEquals(resultsList.get(0), siteNamePublic, format("%s not found or wrong sorting.", siteNamePublic));
        assertEquals(resultsList.get(1), siteNameModerated, format("%s not found or wrong sorting.", siteNameModerated));

        assertTrue(siteFinderPage.isButtonForSitePresent(siteNamePublic, Join), format("Join button didn't displayed for site %s", siteNamePublic));
        assertFalse(siteFinderPage.isButtonForSitePresent(siteNamePublic, RequestToJoin), format("RequestToJoin button didn't displayed for site %s", siteNamePublic));
        assertFalse(siteFinderPage.isButtonForSitePresent(siteNamePublic, Leave), format("Leave button didn't displayed for site %s", siteNamePublic));
        assertFalse(siteFinderPage.isButtonForSitePresent(siteNamePublic, Delete), format("Delete button didn't displayed for site %s", siteNamePublic));

        assertTrue(siteFinderPage.isButtonForSitePresent(siteNameModerated, RequestToJoin), format("Join button didn't displayed for site %s", siteNameModerated));
        assertFalse(siteFinderPage.isButtonForSitePresent(siteNameModerated, Join), format("Join button didn't displayed for site %s", siteNameModerated));
        assertFalse(siteFinderPage.isButtonForSitePresent(siteNameModerated, Leave), format("Leave button didn't displayed for site %s", siteNameModerated));
        assertFalse(siteFinderPage.isButtonForSitePresent(siteNameModerated, Delete), format("Delete button didn't displayed for site %s", siteNameModerated));

        siteDashboardPage = siteFinderPage.selectSite(siteNamePublic);
        SiteNavigation siteNav = siteDashboardPage.getSiteNav();
        siteNav.selectSiteDocumentLibrary();
        assertTrue(drone.getCurrentPage().render() instanceof DocumentLibraryPage, "Site page links work incorrect.(DocumentLibraryPage)");
        siteNav.selectBlogPage();
        assertTrue(drone.getCurrentPage().render() instanceof BlogPage, "Site page links work incorrect.(BlogPage)");
        siteNav.selectDataListPage();
        assertTrue(drone.getCurrentPage().render() instanceof DataListPage, "Site page links work incorrect.(DataListPage)");
        siteNav.selectCalendarPage();
        assertTrue(drone.getCurrentPage().render() instanceof CalendarPage, "Site page links work incorrect.(CalendarPage)");
        siteNav.selectLinksPage();
        assertTrue(drone.getCurrentPage().render() instanceof LinksPage, "Site page links work incorrect.(LinksPage)");
        siteNav.selectWikiPage();
        assertTrue(drone.getCurrentPage().render() instanceof WikiPage, "Site page links work incorrect.(WikiPage)");
        siteNav.selectDiscussionsPage();
        assertTrue(drone.getCurrentPage().render() instanceof DiscussionsPage, "Site page links work incorrect.(DiscussionsPage)");

        siteFinderPage = SiteUtil.searchSite(drone, siteNameModerated);
        boolean accessNotDenied = false;
        try
        {
            siteFinderPage.selectSite(siteNameModerated);
        }
        catch (PageRenderTimeException e)
        {
            assertTrue(drone.getCurrentPage() instanceof SiteDashboardPage, "Something wrong with moderated site.");
            accessNotDenied = true;
        }
        assertTrue(accessNotDenied, "User can see moderated site data");

        String privateSiteUrl = format("/page/site/%s/dashboard", siteNamePrivate);
        String currentUrl = drone.getCurrentUrl();
        String url = currentUrl.replaceFirst("^*/page.*", privateSiteUrl);
        drone.navigateTo(url);

        try
        {
            drone.getCurrentPage().render();
        }
        catch (PageRenderTimeException e)
        {
            //if Server Error page didn't displayed - method render throw PageRenderTimeException
            new ServerErrorPage(drone).render();
        }

    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_ALF_3087() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser + 2);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "Sanity")
    public void ALF_3087()
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        siteDashboardPage.getSiteNav().selectInvite().render();
        assertTrue(drone.getCurrentPage().render() instanceof InviteMembersPage, "Invite Members page didn't open.");
        ShareUser.openSiteDashboard(drone, siteName);

        CustomiseSiteDashboardPage customiseSiteDashboardPage = siteDashboardPage.getSiteNav().selectCustomizeDashboard().render();
        customiseSiteDashboardPage.addDashlet(Dashlets.SITE_NOTICE, 1);
        customiseSiteDashboardPage = siteDashboardPage.getSiteNav().selectCustomizeDashboard().render();
        assertTrue(customiseSiteDashboardPage.isDashletInColumn(Dashlets.SITE_NOTICE, 1), "Added dashlet not present in column.");
        customiseSiteDashboardPage.remove(Dashlets.SITE_NOTICE);
        customiseSiteDashboardPage = siteDashboardPage.getSiteNav().selectCustomizeDashboard().render();
        assertFalse(customiseSiteDashboardPage.isDashletInColumn(Dashlets.SITE_NOTICE, 1), "Information about removing dashlet not present.");

        EditSitePage editSitePage = siteDashboardPage.getSiteNav().selectEditSite().render();
        editSitePage.selectSiteVisibility(true, false);
        editSitePage.selectOk();

        editSitePage = siteDashboardPage.getSiteNav().selectEditSite().render();
        assertTrue(editSitePage.isPrivate(), "Information about visibility site in EditSitePage is wrong.");
        editSitePage.cancel();

        assertFalse(siteDashboardPage.isPagesMoreButtonDisplayed(), "More button displayed by default.");
        assertEquals(siteDashboardPage.getPagesLinkCount(), 3, "Wrong pages count by default.");
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary();
        siteDashboardPage.getSiteNav().selectSiteDashBoard();

        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> pageTypes = new ArrayList<SitePageType>();
        pageTypes.add(SitePageType.BLOG);
        pageTypes.add(SitePageType.CALENDER);
        pageTypes.add(SitePageType.LINKS);
        customizeSitePage.addPages(pageTypes);
        assertFalse(siteDashboardPage.isPagesMoreButtonDisplayed(), "More button displayed after pages added.");
        assertEquals(siteDashboardPage.getPagesLinkCount(), 5, "Wrong pages links displayed after pages added.");

        HtmlPage htmlPage = siteDashboardPage.getSiteNav().leaveSite();
        assertTrue(htmlPage instanceof SiteDashboardPage, "User was able to leave the site");
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser + "2", siteName, MANAGER);
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        htmlPage = siteDashboardPage.getSiteNav().leaveSite();
        assertTrue(htmlPage instanceof DashBoardPage, "The user can not leave the site");
    }


    @Test(groups = "DataPrepSanity")
    public void dataPrep_ALF_3110() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String testUser2 = testUser + "2";
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);

        Map<String, File> foldersAndJpgs = new HashMap<>();

        foldersAndJpgs.put(folderName + 1, prepareJpg(testName));
        foldersAndJpgs.put(folderName + 2, prepareJpg(testName));
        foldersAndJpgs.put(folderName + 3, prepareJpg(testName));

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created by User1
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // All the dashlets are added to the site dashboard
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        CustomiseSiteDashboardPage customiseSiteDashboardPage = siteDashboardPage.getSiteNav().selectCustomizeDashboard().render();
        customiseSiteDashboardPage.selectChangeLayout();
        customiseSiteDashboardPage.selectNewLayout(4);
        customiseSiteDashboardPage.addAllDashlets();
        // All the pages are added to the site
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(PAGE_TYPES));
        // Several images are placed in several folders of the site
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentLibraryNavigation documentLibraryNavigation = documentLibraryPage.getNavigation();
        for (String folder : foldersAndJpgs.keySet())
        {
            ShareUserSitePage.createFolder(drone, folder, folder);
            ShareUserSitePage.navigateToFolder(drone, folder);
            File uploadedFile = foldersAndJpgs.get(folder);
            ShareUserSitePage.uploadFile(drone, uploadedFile);
            ShareUserSitePage.editContentNameInline(drone, uploadedFile.getName(), folder + ".jpg", true);
            documentLibraryNavigation.selectFolderInNavBar("Documents");
        }
        // User2 is at least contributor on the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, CONTRIBUTOR);
        // At least one wiki page is created
        SiteNavigation siteNav = SiteUtil.openSiteDashboard(drone, siteName).getSiteNav();
        WikiPage wikiPage = siteNav.selectWikiPage();
        wikiPage.createWikiPage(testName, asList(testName));
        // At least one link is created
        LinksPage linksPage = siteNav.selectLinksPage();
        linksPage.clickNewLink();
        linksPage.createLink(testName, "http://electrictower.ru");
        // At least one Data list is created
        SharePage somePage = siteNav.selectDataListPage().render();
        try
        {
            NewListForm newListForm = (NewListForm) somePage;
            newListForm.selectListType(CONTACT_LIST);
            newListForm.inputTitleField(testUser);
            newListForm.inputDescriptionField(testName);
            newListForm.clickSave();
        }
        catch (ClassCastException e)
        {
            //Error in correct resolving pages by webdrone.
            DataListPage dataListPage = (DataListPage) somePage;
            dataListPage.createDataList(CONTACT_LIST, testUser, testName);
        }
        // Several events are created: at least one event is passed, three events are upcoming - simple one day event, all day event, multy-day event
        CalendarPage calendarPage = siteNav.selectCalendarPage();
        calendarPage.createEvent("All day Event", testName, testName, true);
        calendarPage.createEvent("Just event", testUser, testUser, false);
        Calendar calendar = Calendar.getInstance();
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        int todayDate = calendar.get(Calendar.DATE);
        int anotherDate;
        if (lastDate == todayDate)
        {
            anotherDate = todayDate - 1;
            calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, "TwoDay", "TwoDay", "TwoDay", String.valueOf(anotherDate), null,
                    String.valueOf(todayDate), null, null, false);
        }
        else
        {
            anotherDate = todayDate + 1;
            calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, "TwoDay", "TwoDay", "TwoDay", String.valueOf(todayDate), null,
                    String.valueOf(anotherDate), null, null, false);
        }
        // At least one discussion is created by each user
        // At least one content is marked as favourite by each user
        // At least one content is being edited by each user
        DiscussionsPage discussionsPage = siteNav.selectDiscussionsPage();
        discussionsPage.createTopic(testUser, testName);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName + 1);
        FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(folderName + "1.jpg");
        fileDirectoryInfo.selectFavourite();
        fileDirectoryInfo.selectEditOffline();
        // Second user
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        siteNav = SiteUtil.openSiteDashboard(drone, siteName).getSiteNav();
        discussionsPage = siteNav.selectDiscussionsPage();
        discussionsPage.createTopic(testUser2, testName);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName + 2);
        fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(folderName + "2.jpg");
        fileDirectoryInfo.selectFavourite();
        File user2File = prepareJpg(testName);
        String fileName = testUser2 + ".jpg";
        documentLibraryPage = ShareUserSitePage.uploadFile(drone, user2File);
        ShareUserSitePage.editContentNameInline(drone, user2File.getName(), fileName, true);
        fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(fileName);
        fileDirectoryInfo.selectEditOffline();
    }

    @Test(groups = "Sanity")
    public void ALF_3110()
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String testUser2 = testUser + "2";
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        // Verify Site Calendar
        SiteCalendarDashlet siteCalendarDashlet = siteDashboardPage.getDashlet("site-calendar").render();
        assertEquals(siteCalendarDashlet.getEventsCount(), 3, "Wrong events count in Calendar Dashlet Displayed.");
        assertTrue(siteCalendarDashlet.isEventsDisplayed("All day Event"), "All day Event didn't displayed in dashlet.");
        assertTrue(siteCalendarDashlet.isEventsDisplayed("TwoDay"), "TwoDay event didn't displayed in dashlet.");
        assertTrue(siteCalendarDashlet.isEventsDisplayed("Just event"), "Just event didn't displayed in dashlet.");
        // Verify Image Preview. Configure the dashlet
        ImagePreviewDashlet imagePreviewDashlet = siteDashboardPage.getDashlet("image-preview").render();
        assertEquals(imagePreviewDashlet.getImagesCount(), 6, "If wrong please check ACE-1660. And correct image nuber.");
        for (int i = 1; i < 4; i++)
        {
            String imageName = String.format("%s%d.jpg", folderName, i);
            assertTrue(imagePreviewDashlet.isImageDisplayed(imageName), String.format("Image[%s] didn't display in dashlet", imageName));
        }
        assertTrue(imagePreviewDashlet.isImageDisplayed(testUser2 + ".jpg"));
        SelectImageFolderBoxPage selectImageFolderBoxPage = imagePreviewDashlet.clickOnConfigure();
        try
        {
            selectImageFolderBoxPage.render();
        }
        catch (Exception e)
        {
            fail("Can't config ImageDashlet");
        }
        selectImageFolderBoxPage.clickCancel();
        // Verify Site Links. Create a link form the dashlet
        SiteLinksDashlet siteLinksDashlet = siteDashboardPage.getDashlet("site-links").render();
        assertEquals(siteLinksDashlet.getLinksCount(), 1, "Wrong links count displayed.");
        assertTrue(siteLinksDashlet.isLinkDisplayed(testName), String.format("Link [%s] didn't displayed in dashlet", testName));
        siteLinksDashlet.createLink("alfresco", "http://www.alfresco.com/");
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        siteLinksDashlet = siteDashboardPage.getDashlet("site-links").render();
        assertEquals(siteLinksDashlet.getLinksCount(), 2, "Wrong links count displayed.");
        assertTrue(siteLinksDashlet.isLinkDisplayed(testName), String.format("Link [%s] didn't displayed in dashlet", testName));
        assertTrue(siteLinksDashlet.isLinkDisplayed("alfresco"), "Link [alfresco] didn't displayed in dashlet");
        // Verify My Discussions: My Topics/ All Topics. Click New Topic
        MyDiscussionsDashlet myDiscussionsDashlet = siteDashboardPage.getDashlet("my-discussions").render();
        List<ShareLink> shareLinks = myDiscussionsDashlet.getTopics(Topic);
        assertEquals(shareLinks.size(), 2, "If wrong please check ACE-374. And correct expected result.");
        assertEquals(shareLinks.get(0).getDescription(), testUser2, "Wrong first topic in dashlet.");
        assertEquals(shareLinks.get(1).getDescription(), testUser, "Wrong second topic in dashlet.");
        myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS);
        shareLinks = myDiscussionsDashlet.getTopics(Topic);
        assertEquals(shareLinks.size(), 2, "Wrong topics count.");
        assertEquals(shareLinks.get(0).getDescription(), testUser2, "Wrong first topic in dashlet.");
        assertEquals(shareLinks.get(1).getDescription(), testUser, "Wrong second topic in dashlet.");
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton();
        createNewTopicPage.enterTopicTitle(testName);
        createNewTopicPage.getTinyMCEEditor().setText(testName);
        TopicDetailsPage topicDetailsPage = createNewTopicPage.saveTopic();
        siteDashboardPage = topicDetailsPage.getSiteNav().selectSiteDashBoard();
        myDiscussionsDashlet = siteDashboardPage.getDashlet("my-discussions").render();
        //Solr wait
        for (int i = 0; i < 20; i++)
        {
            myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS);
            if (shareLinks.size() != myDiscussionsDashlet.getTopics(Topic).size())
            {
                break;
            }
            myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS);
        }
        shareLinks = myDiscussionsDashlet.getTopics(Topic);
        assertEquals(shareLinks.size(), 3, "If wrong please check ACE-374. And correct expected result.");
        // Verify Site Search. Search for any item
        SiteSearchDashlet siteSearchDashlet = siteDashboardPage.getDashlet("site-search").render();
        siteSearchDashlet.search(testUser + "2.jpg");
        List<SiteSearchItem> searchResults = siteSearchDashlet.getSearchItems();
        assertEquals(searchResults.size(), 1, "Wrong items count found.");
        assertEquals(searchResults.get(0).getItemName().getDescription(), testUser + "2.jpg", "Found not what we expect.");
        // Verify Site Members: click All Members/ click on any member.
        SiteMembersDashlet siteMembersDashlet = siteDashboardPage.getDashlet("site-members").render();
        siteMembersDashlet.clickAllMembers();
        assertTrue(drone.getCurrentPage() instanceof SiteMembersPage, "After click on 'All Members' Site Members page don't open.");
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        siteMembersDashlet = siteDashboardPage.getDashlet("site-members").render();
        siteMembersDashlet.clickOnUser(testUser);
        assertTrue(drone.getCurrentPage() instanceof MyProfilePage, "After click on user link user profile page don't open.");
        // Verify Site Activities. Subcribe to RSS and open any page from RSS list
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        SiteActivitiesDashlet siteActivitiesDashlet = siteDashboardPage.getDashlet("site-activities").render();
        assertTrue(siteActivitiesDashlet.isRssBtnDisplayed(), "Rss button missing");
        RssFeedPage rssFeedPage = siteActivitiesDashlet.selectRssFeed(testUser, DEFAULT_PASSWORD);
        assertEquals(rssFeedPage.getTitle(), String.format("Alfresco Activities Site Feed for %s", siteName.toLowerCase()), "Rss from activity dashlet didn't opened.");
        assertTrue(rssFeedPage.isSubscribePanelDisplay(), "Subscribe panel in feed don't display.");
        assertTrue(rssFeedPage.isDisplayedInFeed(testUser), String.format("Information about activity by user[%s] don't display.", testUser));
        SharePage someSharePage = rssFeedPage.clickOnFeedContent(testUser);
        assertTrue(someSharePage.isLoggedIn(), "Page from rss don't open correct");
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        // Verify Site Notice. Configure Site Notice
        SiteNoticeDashlet siteNoticeDashlet = siteDashboardPage.getDashlet("site-notice").render();
        assertEquals(siteNoticeDashlet.getContent(), "No text has been configured", "Default text in dashlet don't display");
        ConfigureSiteNoticeDialogBoxPage configureSiteNoticeDialogBoxPage = siteNoticeDashlet.clickOnConfigureIcon();
        configureSiteNoticeDialogBoxPage.setText("ETO NORMA!");
        configureSiteNoticeDialogBoxPage.clickOnOKButton();
        assertEquals(siteNoticeDashlet.getContent(), "ETO NORMA!", "The Notice dashlet don't reflects the changes");
        // Verify Wiki. Configure Wiki
        WikiDashlet wikiDashlet = siteDashboardPage.getDashlet("wiki").render();
        String wikiDashletText = wikiDashlet.getContent();
        assertEquals(wikiDashletText, "No page is configured", "Default text in wiki dashlet don't displayed.");
        SelectWikiDialogueBoxPage selectWikiDialogueBoxPage = wikiDashlet.clickConfigure();
        selectWikiDialogueBoxPage.selectWikiPageBy(testName);
        for (int i = 0; i < 20; i++)
        {
            if (!wikiDashletText.equals(wikiDashlet.getContent()))
            {
                break;
            }
        }
        assertEquals(wikiDashlet.getContent(), testName, "Wiki dashlet don't reflect the changes.");
        // Verify Web View. Configure the dashlet
        WebViewDashlet webViewDashlet = siteDashboardPage.getDashlet("web-view").render();
        assertEquals(webViewDashlet.getDefaultMessage(), "No web page to display.", "Default message in WebViewDashlet incorrect.");
        ConfigureWebViewDashletBoxPage configureWebViewDashletBoxPage = webViewDashlet.clickConfigure();
        String siteUrl = "http://electrictower.ru/";
        configureWebViewDashletBoxPage.config("electric tower", siteUrl);
        for (int i = 0; i < 20; i++)
        {
            if (webViewDashlet.isFrameShow(siteUrl))
            {
                break;
            }
        }
        assertTrue(webViewDashlet.isFrameShow(siteUrl), "The WebView dashlet don't reflects the changes.");
        // Verify Site Content: I've Recently Modified, I'm Editing, My Favourites
        SiteContentDashlet siteContentDashlet = siteDashboardPage.getDashlet("site-contents").render();
        siteContentDashlet.selectFilter(I_AM_EDITING);
        List<ShareLink> contents = siteContentDashlet.getSiteContents();
        assertEquals(contents.size(), 1, "'I'am editing' filter return wrong contents count.");
        assertEquals(contents.get(0).getDescription(), folderName + 1 + ".jpg", "Wrong content display in ContentDashlet.");
        siteContentDashlet.selectFilter(I_HAVE_RECENTLY_MODIFIED);
        for (int i = 0; i < 20; i++)
        {
            if (contents != siteContentDashlet.getSiteContents())
            {
                break;
            }
        }
        contents = siteContentDashlet.getSiteContents();
        assertEquals(contents.size(), 4, "'I've Recently Modified' filter return wrong contents count.");
        assertEquals(contents.get(0).getDescription(), testUser2 + ".jpg", "Wrong content display in ContentDashlet.");
        siteContentDashlet.selectFilter(MY_FAVOURITES);
        for (int i = 0; i < 20; i++)
        {
            if (contents != siteContentDashlet.getSiteContents())
            {
                break;
            }
        }
        contents = siteContentDashlet.getSiteContents();
        assertEquals(contents.size(), 1, "'My Favorites' filter return wrong contents count.");
        assertEquals(contents.get(0).getDescription(), folderName + 1 + ".jpg", "Wrong content display in ContentDashlet.");
        // Verify Site Data Lists. Create Data List
        SiteDataListsDashlet siteDataListsDashlet = siteDashboardPage.getDashlet("data-lists").render();
        assertEquals(siteDataListsDashlet.getListsCount(), 1, "Wrong data-lists count in dashlet present.");
        assertTrue(siteDataListsDashlet.isDataListDisplayed(testName), "Information about created data-list didn't displayed in dashlet.");
        NewListForm newListForm = siteDataListsDashlet.clickCreateDataList();
        newListForm.inputTitleField(testUser);
        newListForm.inputDescriptionField(testUser);
        newListForm.selectListType(CONTACT_LIST);
        newListForm.clickSave();
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        siteDataListsDashlet = siteDashboardPage.getDashlet("data-lists").render();
        assertEquals(siteDataListsDashlet.getListsCount(), 2, "Wrong data-lists count in dashlet present.");
        assertTrue(siteDataListsDashlet.isDataListDisplayed(testUser), "Information about new data-list didn't displayed in dashlet.");
        // Verify Site Profile
        SiteProfileDashlet siteProfileDashlet = siteDashboardPage.getDashlet("site-profile").render();
        String content = siteProfileDashlet.getContent();
        assertTrue(content.contains("Site Manager(s): " + testUser), "Information about site manager don't display.");
        assertTrue(content.contains("Visibility: Public"), "Information about site visibility don't display.");
        // Verify Saved Search. Configure the dashlet
        SavedSearchDashlet savedSearchDashlet = siteDashboardPage.getDashlet("saved-search").render();
        content = savedSearchDashlet.getContent();
        assertEquals(content, "No results found.", "Information in saved search dashlet wrong by default.");
        ConfigureSavedSearchDialogBoxPage configureSavedSearchDialogBoxPage = savedSearchDashlet.clickOnEditButton().render();
        configureSavedSearchDialogBoxPage.setSearchLimit(TEN);
        configureSavedSearchDialogBoxPage.setSearchTerm(testUser2 + ".jpg");
        configureSavedSearchDialogBoxPage.clickOnOKButton();
        assertEquals(savedSearchDashlet.getSearchItems().size(), 1, "Wrong items count found in saved search site dashlet");
        assertTrue(savedSearchDashlet.isItemFound(testUser2 + ".jpg"), "Content not found in saved search dashlet.");
        // Verify RSS Feed. Configure the dashlet
        RssFeedDashlet rssFeedDashlet = siteDashboardPage.getDashlet("rss-feed").render();
        for (int i = 0; i < 1000; i++)
        {
            if (!"Rss Feed".equals(rssFeedDashlet.getTitle()))
            {
                break;
            }
        }
        String defaultTitle = rssFeedDashlet.getTitle();
        assertEquals(defaultTitle, "Alfresco Blog", "Rss dashlet don't show Alfresco rss by default.");
        RssFeedUrlBoxPage rssFeedUrlBoxPage = rssFeedDashlet.clickConfigure();
        rssFeedUrlBoxPage.fillURL("http://projects.apache.org/feeds/atom.xml");
        rssFeedUrlBoxPage.clickOk();
        for (int i = 0; i < 1000; i++)
        {
            if (!defaultTitle.equals(rssFeedDashlet.getTitle()))
            {
                break;
            }
        }
        assertEquals(rssFeedDashlet.getTitle(), "Apache Software Foundation Project Releases", "Rss Feed don't reflect are changes.");
        // Verify Alfresco Add-Ons RSS Feed. Configure the dashlet
        AddOnsRssFeedDashlet addOnsRssFeedDashlet = siteDashboardPage.getDashlet("addOns-rss").render();
        defaultTitle = addOnsRssFeedDashlet.getTitle();
        assertEquals(defaultTitle, "Alfresco Add-ons RSS Feed", "Rss dashlet don't show Alfresco rss by default.");
        rssFeedUrlBoxPage = addOnsRssFeedDashlet.clickConfigure();
        rssFeedUrlBoxPage.fillURL("http://projects.apache.org/feeds/atom.xml");
        rssFeedUrlBoxPage.clickOk();
        for (int i = 0; i < 1000; i++)
        {
            if (!defaultTitle.equals(addOnsRssFeedDashlet.getTitle()))
            {
                break;
            }
        }
        assertEquals(addOnsRssFeedDashlet.getTitle(), "Apache Software Foundation Project Releases", "AddOns Rss Feed don't reflect are changes.");
    }

}
