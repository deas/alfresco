package org.alfresco.share.sanity;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.search.*;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.links.LinksDetailsPage;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.share.search.SearchKeys;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.alfresco.po.share.site.SitePageType.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This class includes Search Sanity tests
 * 
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
public class SearchTest extends AbstractUtils
{
    private static final Log logger = LogFactory.getLog(SearchTest.class);
    String test = "TestSimpleSearch";
    String text = "TextSimpleSearch";
    String tag = "TagSimpleSearch";

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_AONE_8279() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName1 = getSiteName(testName) + "1";
        String siteName2 = getSiteName(testName) + "2";

        List<String> wikiLines = new ArrayList<>();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);

        // A wiki page, a blog post, a library document, a link, a topic with title "Test" are created in any of the sites
        // A wiki page, a blog post, a library document, a link, a topic with any title but containing "test" in 'Text' or 'Description' fields are created
        // in any of the sites
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName1).render();
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> pageTypes = new ArrayList<>();
        pageTypes.add(SitePageType.WIKI);
        pageTypes.add(BLOG);
        pageTypes.add(LINKS);
        pageTypes.add(DISCUSSIONS);
        customizeSitePage.addPages(pageTypes).render();

        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();
        wikiLines.add(getRandomString(5));
        wikiPage.createWikiPage(test, wikiLines);
        wikiLines.clear();
        wikiLines.add(test);
        wikiPage.createWikiPage(text, wikiLines);

        BlogPage blogPage = siteDashboardPage.getSiteNav().selectBlogPage().render();
        String blogLines = (getRandomString(5));
        blogPage.createPostInternally(test, blogLines).render();
        blogPage.createPostInternally(text, test).render();

        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
        String randText = (getRandomString(5));
        ContentDetails contentDetails = new ContentDetails(test, null, null, randText);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT).render(maxWaitTime);
        contentDetails = new ContentDetails(text, null, null, test);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT).render(maxWaitTime);

        LinksPage linksPage = siteDashboardPage.getSiteNav().selectLinksPage().render();
        String randLink = (getRandomString(5));
        LinksDetailsPage linksDetailsPage = linksPage.createLink(test, randLink).render();
        linksDetailsPage.browseToLinksList().render();
        linksPage.createLink(text, randLink, test, null);

        DiscussionsPage discussionsPage = siteDashboardPage.getSiteNav().selectDiscussionsPage().render();
        String randTopicLines = (getRandomString(5));
        discussionsPage.createTopic(test, randTopicLines).render();
        discussionsPage.createTopic(text, test).render();

        // A wiki page, a blog post, a library document, a link, a topic with tag "Test" are created in any of the sites
        SiteDashboardPage siteDashboardPage1 = ShareUser.openSiteDashboard(drone, siteName2).render();

        CustomizeSitePage customizeSitePage1 = siteDashboardPage.getSiteNav().selectCustomizeSite().render();
        customizeSitePage1.addPages(pageTypes).render();

        WikiPage wikiPage1 = siteDashboardPage1.getSiteNav().selectWikiPage().render();
        String randWiki = getRandomString(5);
        wikiLines.clear();
        wikiLines.add(randWiki);
        List<String> tagsToAdd = new ArrayList<>();
        tagsToAdd.add(test);
        wikiPage1.createWikiPage(tag, wikiLines, tagsToAdd).render();

        BlogPage blogPage1 = siteDashboardPage1.getSiteNav().selectBlogPage().render();
        String randBlog = getRandomString(5);
        blogPage1.createPostInternally(tag, randBlog, test).render();

        DocumentLibraryPage documentLibraryPage = siteDashboardPage1.getSiteNav().selectSiteDocumentLibrary().render();
        String randDoc = getRandomString(5);
        contentDetails = new ContentDetails(tag, randDoc, randDoc, randDoc);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT).render(maxWaitTime);
        documentLibraryPage.getFileDirectoryInfo(tag).addTag(test);

        ShareUser.login(drone, testUser);
        siteDashboardPage1 = ShareUser.openSiteDashboard(drone, siteName2).render();

        LinksPage linksPage1 = siteDashboardPage1.getSiteNav().selectLinksPage().render();
        randLink = getRandomString(5);
        linksPage1.createLink(tag, randLink, randLink, test).render();

        DiscussionsPage discussionsPage1 = siteDashboardPage1.getSiteNav().selectDiscussionsPage().render();
        String randTopic = getRandomString(5);
        discussionsPage1.createTopic(tag, randTopic, test).render();
    }

    /**
     * Check Simple Search on Site
     */
    @Test(groups = "Sanity")
    public void AONE_8279() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName1 = getSiteName(testName) + "1";

        ShareUser.login(drone, testUser);

        // Type any request in search field and press enter;
        FacetedSearchPage facetedSearchPage = ShareUser.openSiteDashboard(drone, siteName1).getNav().performSearch(test);

        if (!facetedSearchPage.isItemPresentInResultsList(SitePageType.WIKI, test))
        {
            drone.deleteCookies();
            drone.refresh();
            drone.getCurrentPage().render();
            ShareUser.login(drone, testUser);

            facetedSearchPage = ShareUser.openSiteDashboard(drone, siteName1).getNav().performSearch(test);

        }

        // The search result is presented on the page;
        assertTrue(
                facetedSearchPage.isItemPresentInResultsList(SitePageType.WIKI, test) && facetedSearchPage.isItemPresentInResultsList(SitePageType.WIKI, text)
                        && facetedSearchPage.isItemPresentInResultsList(SitePageType.WIKI, tag), "Not all Wikis are retrieved");

        assertTrue(
                facetedSearchPage.isItemPresentInResultsList(BLOG, test) && facetedSearchPage.isItemPresentInResultsList(BLOG, text)
                        && facetedSearchPage.isItemPresentInResultsList(BLOG, tag), "Not all Posts are retrieved");

        assertTrue(facetedSearchPage.isItemPresentInResultsList(DOCUMENT_LIBRARY, test) && facetedSearchPage.isItemPresentInResultsList(DOCUMENT_LIBRARY, text)
                && facetedSearchPage.isItemPresentInResultsList(DOCUMENT_LIBRARY, tag), "Not all docs are retrieved");

        assertTrue(
                facetedSearchPage.isItemPresentInResultsList(LINKS, test) && facetedSearchPage.isItemPresentInResultsList(LINKS, text)
                        && facetedSearchPage.isItemPresentInResultsList(LINKS, tag), "Not all Links are retrieved");

        assertTrue(facetedSearchPage.isItemPresentInResultsList(DISCUSSIONS, test) && facetedSearchPage.isItemPresentInResultsList(DISCUSSIONS, text)
                && facetedSearchPage.isItemPresentInResultsList(DISCUSSIONS, tag), "Not all topics are retrieved");

        // Choose "Search all Sites" item;
        FacetedSearchScopeMenu scopeMenu = facetedSearchPage.getScopeMenu();
        scopeMenu.scopeByLabel("all Sites").render();

        // The item is chosen successfully;
        assertTrue(scopeMenu.getCurrentSelection().equalsIgnoreCase("all sites"));

        // Verify the all the items are listed
        assertTrue(facetedSearchPage.getResults().size() == 15, "Incorrect number of results");

        // Type any request "Conference" and press Enter;
        facetedSearchPage.getSearchForm().search(test).render();

        // User does to "Search Result" page;
        // The search result is presented on the page;
        assertTrue(
                facetedSearchPage.isItemPresentInResultsList(SitePageType.WIKI, test) && facetedSearchPage.isItemPresentInResultsList(SitePageType.WIKI, text)
                        && facetedSearchPage.isItemPresentInResultsList(SitePageType.WIKI, tag), "Not all Wikis are retrieved");

        assertTrue(
                facetedSearchPage.isItemPresentInResultsList(BLOG, test) && facetedSearchPage.isItemPresentInResultsList(BLOG, text)
                        && facetedSearchPage.isItemPresentInResultsList(BLOG, tag), "Not all Posts are retrieved");

        assertTrue(facetedSearchPage.isItemPresentInResultsList(DOCUMENT_LIBRARY, test) && facetedSearchPage.isItemPresentInResultsList(DOCUMENT_LIBRARY, text)
                && facetedSearchPage.isItemPresentInResultsList(DOCUMENT_LIBRARY, tag), "Not all docs are retrieved");

        assertTrue(
                facetedSearchPage.isItemPresentInResultsList(LINKS, test) && facetedSearchPage.isItemPresentInResultsList(LINKS, text)
                        && facetedSearchPage.isItemPresentInResultsList(LINKS, tag), "Not all Links are retrieved");

        assertTrue(facetedSearchPage.isItemPresentInResultsList(DISCUSSIONS, test) && facetedSearchPage.isItemPresentInResultsList(DISCUSSIONS, text)
                && facetedSearchPage.isItemPresentInResultsList(DISCUSSIONS, tag), "Not all topicss are retrieved");

        // The following information presents: - "Search for "Test " in "all sites" returned N results" line; - "Search "Test" Site only" link;
        int actResultsDisplayed = facetedSearchPage.getResults().size();
        assertTrue(facetedSearchPage.getSort().getResults().equals(Integer.toString(actResultsDisplayed)),
                "Incorrect number of results is displayed in Results menu: ISSUE ACE-2372");
        assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel("Repository"));
        assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel(siteName1));

        // Click "Search Test site only" link;
        scopeMenu.scopeByLabel(siteName1).render();
        facetedSearchPage.render();

        // The list of items found within this site is shown;
        assertTrue(facetedSearchPage.getResults().size() == 10, "Incorrect number of results");

        assertTrue(
                facetedSearchPage.isItemPresentInResultsList(SitePageType.WIKI, test) && facetedSearchPage.isItemPresentInResultsList(SitePageType.WIKI, text),
                "Not all Wikis are retrieved");

        assertTrue(facetedSearchPage.isItemPresentInResultsList(BLOG, test) && facetedSearchPage.isItemPresentInResultsList(BLOG, text),
                "Not all Posts are retrieved");

        assertTrue(
                facetedSearchPage.isItemPresentInResultsList(DOCUMENT_LIBRARY, test) && facetedSearchPage.isItemPresentInResultsList(DOCUMENT_LIBRARY, text),
                "Not all docs are retrieved");

        assertTrue(facetedSearchPage.isItemPresentInResultsList(LINKS, test) && facetedSearchPage.isItemPresentInResultsList(LINKS, text),
                "Not all Links are retrieved");

        assertTrue(facetedSearchPage.isItemPresentInResultsList(DISCUSSIONS, test) && facetedSearchPage.isItemPresentInResultsList(DISCUSSIONS, text),
                "Not all topics are retrieved");
    }

    @Test(groups = { "DataPrepSanity", "NonGrid" })
    public void dataPrep_AONE_8278() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String folderName = getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser + 1);
        ShareUser.login(drone, testUser);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Creating two documents
        ShareUser.openDocumentLibrary(drone);

        ContentDetails content = new ContentDetails(fileName + "name" + 1, fileName + "title" + 1, fileName + "desc" + 1, fileName + "content" + 1);
        ShareUser.createContent(drone, content, ContentType.PLAINTEXT).render();

        content = new ContentDetails(fileName + "name" + 2, fileName + "title" + 2, fileName + "desc" + 2, fileName + "content" + 2);
        ShareUser.createContent(drone, content, ContentType.HTML).render();

        // creating two folders
        for (int i = 1; i < 3; i++)
        {
            ShareUserSitePage.createFolder(drone, folderName + "name" + i, folderName + "title" + i, folderName + "desc" + i);
        }
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser + 1, siteName, UserRole.COLLABORATOR);
        ShareUser.login(drone, testUser + 1);
        ShareUser.openSiteDashboard(drone, siteName);
        content = new ContentDetails(fileName + "user2", null, null, fileName);
        ShareUser.createContent(drone, content, ContentType.PLAINTEXT).render();
    }

    /**
     * Check Adv Search on Site
     */
    @Test(groups = { "Sanity", "NonGrid" })
    public void AONE_8278() throws Exception
    {
        try
        {
            String testName = getTestName();
            String testUser = getUserNameFreeDomain(testName);
            String fileName = getFileName(testName);
            String folderName = getFolderName(testName);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            String todaysDate = dateFormat.format(calendar.getTime());

            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String tommsDate = dateFormat.format(calendar.getTime());

            setupCustomDrone(WebDroneType.DownLoadDrone);
            ShareUser.login(customDrone, testUser);

            // Search for Content using Keywords search criteria
            List<String> info = Arrays.asList(SearchKeys.CONTENT.getSearchKeys());
            Map<String, String> keywordSearchText = new HashMap<>();
            keywordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), testName);
            List<SearchResult> theResults = ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);

            // All contents with specified Keywords are found
            assertTrue(theResults.size() > 0, "No items are retrieved");
            boolean isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 1);
            assertTrue(isAvailable, "The item " + fileName + "name" + 1 + "isn't retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 2);
            assertTrue(isAvailable, "The item " + fileName + "name" + 2 + "isn't retrieved");

            // Click Go to Advanced Search. Search for Content using Name search criteria
            RepositoryResultsPage resultsPage = new RepositoryResultsPage(customDrone);
            resultsPage.goToAdvancedSearch();
            keywordSearchText.clear();
            keywordSearchText.put(SearchKeys.NAME.getSearchKeys(), fileName + "name" + 1);
            theResults = ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);

            // All contents with specified Name are found
            assertTrue(theResults.size() > 0, "No items are retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 1);
            assertFalse(ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 2));
            assertTrue(isAvailable, "The item " + fileName + "name" + 1 + "isn't retrieved");

            // Search for Content using Title search criteria
            keywordSearchText.clear();
            keywordSearchText.put(SearchKeys.TITLE.getSearchKeys(), fileName + "title" + 1);
            theResults = ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);

            // All contents with specified title are found
            assertTrue(theResults.size() > 0, "No items are retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 1);
            assertFalse(ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 2));
            assertTrue(isAvailable, "The item " + fileName + "name" + 1 + "isn't retrieved");

            // Search for Content using Description search criteria
            keywordSearchText.clear();
            keywordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), fileName + "desc" + 1);
            theResults = ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);

            // All contents with specified description are found
            assertTrue(theResults.size() > 0, "No items are retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 1);
            assertFalse(ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 2));
            assertTrue(isAvailable, "The item " + fileName + "name" + 1 + "isn't retrieved");

            // Search for Content using Mimetype search criteria
            keywordSearchText.clear();
            keywordSearchText.put(SearchKeys.MIME.getSearchKeys(), "HTML");
            theResults = ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);

            // All contents with specified mimetype are found
            assertTrue(theResults.size() > 0, "No items are retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 2);
            assertFalse(ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 1));
            assertTrue(isAvailable, "The item " + fileName + "name" + 2 + "isn't retrieved");

            // Search for Content using Modified Date search criteria
            keywordSearchText.clear();
            keywordSearchText.put(SearchKeys.MODIFIERFROMDATE.getSearchKeys(), todaysDate);
            keywordSearchText.put(SearchKeys.MODIFIERTODATE.getSearchKeys(), tommsDate);
            theResults = ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);

            if (theResults.size() == 0)
            {
                webDriverWait(drone, 5000);
                SiteResultsPage siteResultsPage = new SiteResultsPage(drone);
                theResults = siteResultsPage.getResults();
            }

            // All contents modified during the specified period are found
            assertTrue(theResults.size() > 0, "Nothing is retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 2);
            assertTrue(ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 1) && isAvailable, "Some items aren't retrieved");

            // Search for Content using Modifier search criteria
            keywordSearchText.clear();
            keywordSearchText.put(SearchKeys.MODIFIER.getSearchKeys(), testUser + 1);
            theResults = ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);

            // All contents with specified Modifier are found
            assertTrue(theResults.size() > 0, "No items are retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "user2");
            assertTrue(isAvailable, "The item isn't retrieved");
            assertFalse(ShareUserSearchPage.isSearchItemAvailable(customDrone, fileName + "name" + 1), "The item shouldn't be available");

            // Click View in Browser icon
            String theUrl = theResults.get(0).clickOnViewInBrowserIcon();
            Assert.assertNotNull(theUrl);
            Assert.assertTrue(theUrl.contains(fileName + "user2"));

            // Click Download icon
            theResults = ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);
            theResults.get(0).clickOnDownloadIcon();
            getSharePage(customDrone).waitForFile(downloadDirectory + fileName + "user2");
            List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
            assertTrue(extractedChildFilesOrFolders.contains(fileName + "user2"));

            // Search for Folder using Keywords search criteria
            info = Arrays.asList(SearchKeys.FOLDERS.getSearchKeys());
            keywordSearchText.clear();
            keywordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), testName);
            theResults = ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);

            // All folders with specified Keywords are found
            assertTrue(theResults.size() > 0, "No items are retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, folderName + "name" + 1);
            assertTrue(isAvailable, "The item " + folderName + "name" + 1 + "isn't retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, folderName + "name" + 2);
            assertTrue(isAvailable, "The item " + folderName + "name" + 2 + "isn't retrieved");

            // Search for Folder using Name search criteria
            keywordSearchText.clear();
            keywordSearchText.put(SearchKeys.NAME.getSearchKeys(), folderName + "name" + 1);
            theResults = ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);

            // All folders with specified Name are found
            assertTrue(theResults.size() > 0, "No items are retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, folderName + "name" + 1);
            assertTrue(isAvailable, "The item " + folderName + "name" + 1 + "isn't retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, folderName + "name" + 2);
            assertFalse(isAvailable, "The item " + folderName + "name" + 2 + "isn't retrieved");

            // Search for Folder using Title search criteria
            keywordSearchText.clear();
            keywordSearchText.put(SearchKeys.TITLE.getSearchKeys(), folderName + "title" + 1);
            theResults = ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);

            // All folders with specified titles are found
            assertTrue(theResults.size() > 0, "No items are retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, folderName + "name" + 1);
            assertTrue(isAvailable, "The item " + folderName + "name" + 1 + "isn't retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, folderName + "name" + 2);
            assertFalse(isAvailable, "The item " + folderName + "name" + 2 + "isn't retrieved");

            // Search for Folder using Description search criteria
            keywordSearchText.clear();
            keywordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), folderName + "desc" + 1);
            theResults = ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);

            // All folders with specified descriptions are found
            assertTrue(theResults.size() > 0, "No items are retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, folderName + "name" + 1);
            assertTrue(isAvailable, "The item " + folderName + "name" + 1 + "isn't retrieved");
            isAvailable = ShareUserSearchPage.isSearchItemAvailable(customDrone, folderName + "name" + 2);
            assertFalse(isAvailable, "The item " + folderName + "name" + 2 + "isn't retrieved");

            // Click on folder's path
            SharePage thePage = theResults.get(0).clickContentPath();
            assertTrue(thePage instanceof DocumentLibraryPage);

            // Sort the found items by any criteria
            ShareUserSearchPage.advanceSearch(customDrone, info, keywordSearchText);

            List<SearchResult> sortedByName = ShareUserSearchPage.sortSearchResults(customDrone, SortType.NAME);

            List<SearchResult> expectedResultsSortedByName = new ArrayList<>(sortedByName);
            Collections.sort(expectedResultsSortedByName, new SortedSearchResultItemByName());

            // Check Sort order
            for (int i = 0; (i < 5 && i < expectedResultsSortedByName.size()); i++)
            {
                assertTrue(expectedResultsSortedByName.get(i).getTitle().equalsIgnoreCase(sortedByName.get(i).getTitle()),
                        "The results are not sorted as expected - " + expectedResultsSortedByName.get(i).getTitle() + " - " + sortedByName.get(i).getTitle());
            }

            ShareUser.logout(customDrone);
            customDrone.quit();
        }
        catch (Exception e)
        {
            ShareUser.logout(customDrone);
            customDrone.quit();
        }

    }

    private class SortedSearchResultItemByName implements Comparator<SearchResult>
    {
        public int compare(SearchResult item1, SearchResult item2)
        {
            return StringUtils.substringBefore(item1.getTitle(), ".").compareTo(StringUtils.substringBefore(item2.getTitle(), "."));
        }
    }
}