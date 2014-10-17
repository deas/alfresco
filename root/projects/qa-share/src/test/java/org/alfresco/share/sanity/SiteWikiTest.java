package org.alfresco.share.sanity;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPageList;
import org.alfresco.po.share.site.wiki.WikiTreeMenuNavigation;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ActivityType;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.po.share.site.wiki.WikiTreeMenuNavigation.PagesMenu;
import static org.testng.Assert.*;

/**
 * This class includes Site Wiki Sanity tests
 *
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
public class SiteWikiTest extends AbstractUtils
{
    private static final Log logger = LogFactory.getLog(SiteWikiTest.class);

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_AONE_8228() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        //Any user is created
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        //Any site is created
        ShareUser.login(drone, testUser);
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        //The user is logged in
        ShareUser.openSiteDashboard(drone, siteName);
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();

        //Add Wiki page
        List<SitePageType> pagesToAdd = new ArrayList<>();
        pagesToAdd.add(SitePageType.WIKI);
        customizeSitePage.addPages(pagesToAdd);
    }

    /**
     * Check Wiki activities on site
     */
    @Test(groups = "Sanity")
    public void AONE_8228() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String editedItem = testName + "edited";
        String activityEntry = "";

        //The user is logged in
        ShareUser.login(drone, testUser);
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName);

        //Wiki page is opened
        WikiPage wikiPage = dashboard.getSiteNav().selectWikiPage().render();

        //Create any new wiki page
        List<String> txtLines = new ArrayList<>();
        txtLines.add(0, testName);
        wikiPage.createWikiPage(testName, txtLines).render();

        //The newly created wiki page is displayed
        assertTrue(wikiPage.getWikiTitle().equals(testName) && wikiPage.getWikiText().equals(testName), "The wiki isn't correct");

        //Edit the page and add a tag to it
        List<String> tagsToAdd = new ArrayList<>();
        tagsToAdd.add(0, testName);
        wikiPage.editWikiPage(editedItem, tagsToAdd).render();

        //Verify the page is edited. The tag is added
        wikiPage.clickDetailsLink();
        assertTrue(wikiPage.getWikiText().contentEquals(editedItem) && wikiPage.getTagName().equalsIgnoreCase(testName));

        //Go to Details page and rename the page
        wikiPage.renameWikiPage(editedItem);

        //Verify the title was changed
        assertTrue(wikiPage.getWikiTitle().contentEquals(editedItem), "Title wasn't edited");

        //Go to Details and View versions (verify all the versions)
        wikiPage.clickDetailsLink();
        wikiPage.viewVersion(new Double(1.2));
        assertTrue(wikiPage.getWikiTitle().contentEquals(editedItem) && wikiPage.getWikiText().contentEquals(editedItem),
            "Incorrect title or text of wiki version " + 1.2);

        wikiPage.viewVersion(new Double(1.1));
        assertTrue(wikiPage.getWikiTitle().contentEquals(editedItem) && wikiPage.getWikiText().contentEquals(editedItem),
            "Incorrect title or text of wiki version " + 1.1);

        wikiPage.viewVersion(new Double(1.0));
        assertTrue(wikiPage.getWikiTitle().contentEquals(editedItem) && wikiPage.getWikiText().contentEquals(testName),
            "Incorrect title or text of wiki version " + 1.0);

        //Revert any version
        wikiPage.revertToVersion(new Double(1.0));

        //Verify the version of the wiki page is reverted
        //AONE-15630 - tags aren't reverted
        //ALF-11020 - name isn't reverted
        assertTrue(wikiPage.getWikiTitle().contentEquals(testName) && wikiPage.getWikiText().contentEquals(testName)
            && wikiPage.getTagName().contentEquals(null), "AONE-15630, ALF-11020: tags and names are not reverted");

        //Subscribe to RSS feed and open wiki page from RSS list
        RssFeedPage rssFeedPage = wikiPage.clickRssFeedBtn(testName, DEFAULT_PASSWORD).render();
        assertTrue(rssFeedPage.isSubscribePanelDisplay(), "Subscribe panel isn't available");
        wikiPage = (WikiPage) rssFeedPage.clickOnFeedContent(editedItem).render();
        assertTrue(drone.getCurrentPage() instanceof WikiPage, "Couldn't open Wiki page");

        //Delete the wiki page
        wikiPage.deleteWiki();
        assertTrue(drone.getCurrentPage() instanceof WikiPageList, "User isn't redirected to Wiki List.");

        //Verify the page was deleted
        WikiPageList wikiPageList = new WikiPageList(drone).render();
        assertFalse(wikiPageList.isWikiPagePresent(editedItem), "Wiki page wasn't deleted.");

        //Go to Site Dashboard activities and ensure all activities are displayed
        ShareUser.openSiteDashboard(drone, siteName);

        String[] entries = { FEED_CONTENT_CREATED, FEED_CONTENT_UPDATED, FEED_CONTENT_RENAMED, FEED_CONTENT_UPDATED, FEED_CONTENT_DELETED };

        for (int i = 0; i < entries.length; i++)
        {
            if(i == 2)
            {
                activityEntry = testUser + " " + DEFAULT_LASTNAME + entries[i] + FEED_FOR_WIKI_PAGE +
                    FEED_COMMENTED_FROM + testName + FEED_COMMENTED_TO + editedItem;
            }
            else
            {
                activityEntry = testUser + " " + DEFAULT_LASTNAME + entries[i] + FEED_FOR_WIKI_PAGE + testName;
            }
            Boolean entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION);
            assertTrue(entryFound, "Unable to find " + activityEntry);
        }

        //Go to My Dashboard activities and ensure all activities are displayed
        ShareUser.openUserDashboard(drone).render();
        for (int i = 0; i < entries.length; i++)
        {
            if(i == 2)
            {
                activityEntry = testUser + " " + DEFAULT_LASTNAME + entries[i] + FEED_FOR_WIKI_PAGE +
                    FEED_COMMENTED_FROM + testName + FEED_COMMENTED_TO + editedItem;
            }
            else
            {
                activityEntry = testUser + " " + DEFAULT_LASTNAME + entries[i] + FEED_FOR_WIKI_PAGE + testName + FEED_LOCATION + siteName;
            }
            Boolean entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);
            assertTrue(entryFound, "Unable to find " + activityEntry);
        }
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_AONE_8229() throws Exception
    {
        String testName = getTestName();
        String testUser1 = username+1;
        String testUser2 = username+2;
        String siteName = getSiteName(testName);
        List<String> wikiText = new ArrayList<>();
        List<String> wikiTags = new ArrayList<>();

        //2 users are created
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        //Any site is created
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        //The user is a manager in the Site
        ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, testUser1, siteName, UserRole.CONTRIBUTOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, testUser2, siteName, UserRole.CONTRIBUTOR);

        //The user is logged in
        ShareUser.login(drone, ADMIN_USERNAME);
        ShareUser.openSiteDashboard(drone, siteName);
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();

        //Add Wiki page
        List<SitePageType> pagesToAdd = new ArrayList<>();
        pagesToAdd.add(SitePageType.WIKI);
        customizeSitePage.addPages(pagesToAdd);

        //Wiki pages are created by both users
        ShareUser.login(drone, testUser1);
        ShareUser.openSiteDashboard(drone, siteName);
        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();

        //User1 created wiki page
        String[] wikis = { "user1 wiki", "user2 wiki", "wiki with tag1", "wiki with tag2", "wiki with no tags" };

        wikiText.add("This is wiki_text");
        wikiPage.createWikiPage(wikis[0], wikiText);
        ShareUser.logout(drone);

        //User2 logs in and creates wiki page
        ShareUser.login(drone, testUser2);
        ShareUser.openSiteDashboard(drone, siteName);
        wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();
        wikiPage.createWikiPage(wikis[1], wikiText).render();

        //One wiki page is created with tag1, one with tag2, without tags
        wikiTags.add(0, "tag1");

        wikiPage.createWikiPage(wikis[2], wikiText, wikiTags).render();

        wikiTags.clear();
        wikiTags.add(0, "tag2");
        wikiPage.createWikiPage(wikis[3], wikiText, wikiTags).render();
        wikiPage.createWikiPage(wikis[4], wikiText).render();

        //Wiki Page List is opened
        wikiPage.clickWikiPageListBtn().render();
    }

    /**
     * Check Wiki Page List actions in the site
     */
    @Test(groups = "Sanity")
    public void AONE_8229() throws Exception
    {
        String testName = getTestName();
        String editedItem = testName + "edited";
        String testUser1 = username+1;
        String siteName = getSiteName(testName);

        String[] wikis = { "user1 wiki", "user2 wiki", "wiki with tag1", "wiki with tag2", "wiki with no tags" };
        String[] tags = { "tag1", "tag2" };

        //User1 logs in and navigates to wiki page
        ShareUser.login(drone, testUser1);
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();

        //Click My Pages
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();
        WikiTreeMenuNavigation wikiTreeMenuNavigation = wikiPageList.getLeftMenus().render();
        wikiTreeMenuNavigation.selectPageNode(PagesMenu.MY_PAGES).render();

        //Only page, created by the current user are displayed
        assertEquals(wikiPageList.getWikiCount(), 1, "Incorrect number of pages");
        assertTrue(wikiPageList.isWikiPagePresent(wikis[0]), wikis[0] + " isn't present");

        //Click Recently Modified, All, Recently Added and verify all the pages are correctly displayed
        List<PagesMenu> allWikisToDisplay = new ArrayList<>();
        allWikisToDisplay.add(PagesMenu.RECENTLY_MODIFIED);
        allWikisToDisplay.add(PagesMenu.ALL);
        allWikisToDisplay.add(PagesMenu.RECENTLY_ADDED);

        for (PagesMenu allTheWikisToDisplay : allWikisToDisplay)
        {
            wikiTreeMenuNavigation.selectPageNode(allTheWikisToDisplay).render();
            assertEquals(wikiPageList.getWikiCount(), wikis.length, "Not all pages are displayed");
            for (String allWikis : wikis)
            {
                assertTrue(wikiPageList.isWikiPagePresent(allWikis), allWikis + " isn't present in the list");
            }

        }

        //Click on the tag1 in Tags section
        wikiTreeMenuNavigation.selectTagNode(tags[0]).render();

        //Only pages tagged with tag1 are displayed
        assertEquals(wikiPageList.getWikiCount(), 1, "Incorrect number of pages");
        assertTrue(wikiPageList.isWikiPagePresent(wikis[2]));

        //Click Show all items in Tags section
        wikiTreeMenuNavigation.selectShowAllItems().render();
        assertEquals(wikiPageList.getWikiCount(), wikis.length, "Incorrect number of pages");
        for (String allWikis : wikis)
        {
            assertTrue(wikiPageList.isWikiPagePresent(allWikis), allWikis + " isn't present in the list");
        }

        //Click Main Page
        boolean isMainPageOpen = wikiPageList.openMainPage();
        assertTrue(isMainPageOpen, "The main page isn't open");

        //Click Wiki Page List
        wikiPageList = wikiPage.clickWikiPageListBtn().render();
        assertNotNull(wikiPageList, "The page isn't opened");

        //Create new wiki page from Wiki Page List
        List<String> txtLines = new ArrayList<>();
        txtLines.add(testName);
        wikiPageList.createWikiPage(testName, txtLines, txtLines).render();
        refreshSharePage(drone).render();

        //Wiki page is created
        wikiPageList = wikiPage.clickWikiPageListBtn().render();
        if(!(drone.getCurrentPage() instanceof WikiPageList))
        {
            wikiPageList = wikiPage.clickWikiPageListBtn().render();
        }
        assertTrue(wikiPageList.isWikiPagePresent(testName), "Wiki page isn't displayed");

        //Edit any wiki
        wikiPage = wikiPageList.editWikiPage(testName, editedItem);
        assertTrue(wikiPage.getWikiTitle().contentEquals(testName) && wikiPage.getWikiText().contentEquals(editedItem), "Wiki wasn't edited");

        //Navigate to Wiki Page List and click Details
        wikiPageList = wikiPage.clickWikiPageListBtn().render();
        wikiPage = wikiPageList.getWikiPageDirectoryInfo(testName).clickDetails();

        //Verify the details af the wiki page is opened and are correct
        assertTrue(wikiPage.isWikiDetailsCorrect(testName, editedItem), "Wiki details isn't incorrect");

        //Navigate to Wiki Page List and click Delete. Confirm delete
        wikiPageList = wikiPage.clickWikiPageListBtn().render();
        wikiPageList.deleteWikiWithConfirm(testName).render();

        //Verify the wiki page is deleted
        assertFalse(wikiPageList.isWikiPagePresent(testName), "Wiki page wasn't deleted");
    }
}
