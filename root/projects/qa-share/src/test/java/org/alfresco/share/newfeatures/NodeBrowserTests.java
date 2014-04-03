// TODO: Add Copyright info

// TODO: Rename package NewFeatures to appropriate share hierarchy (or Enterprise if enterprise specific)
package org.alfresco.share.newfeatures;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.adminconsole.NodeBrowserPage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.annotations.*;

import java.util.Locale;

import static org.testng.Assert.*;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class NodeBrowserTests extends AbstractTests
{
        private static final Logger logger = Logger.getLogger(NodeBrowserTests.class);
        private NodeBrowserPage nodeBrowserPage;

        @Override
        @BeforeClass(alwaysRun = true)
        public void setup() throws Exception
        {
                testName = this.getClass().getSimpleName();
                logger.info("[Suite ] : Start Tests in: " + testName);
        }


        // TODO: Use from WebDrone project, Move out of qa-share  
        private SharePage createCustomDroneAndLogin(Locale locale)
        {
                WebDriver webDriver = new FirefoxDriver(createFirefoxProfile(locale));
                super.setupCustomDrone(webDriver);
                return ShareUser.login(customDrone, ADMIN_USERNAME, ADMIN_PASSWORD);
        }

        /**
         * Create custom FireFox profile with specific locale.
         *
         * @param locale
         * @return
         */
        // TODO: Use from WebDrone project, Move out of qa-share  
        public FirefoxProfile createFirefoxProfile(Locale locale)
        {  
            FirefoxProfile firefoxProfile = new FirefoxProfile();
                firefoxProfile.setPreference("intl.accept_languages", locale.getLanguage());
                return firefoxProfile;
        }

        /**
         * Executed before Share group tests. Create simple drone with login as administrator.
         *
         * @throws Exception
         */
        @BeforeGroups("Share")
        public void setupDroneAndLogin() throws Exception
        {
                super.setup();
                SharePage page = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                nodeBrowserPage = page.getNav().getNodeBrowserPage();
        }

        @Test(groups = { "Share" }, timeOut = 400000)
        public void enterprise40x_13876()
        {
        // TODO: Create and Use Enums for selectable store, Query etc
            // TODO: Create Util to search specifying store, query, search val and click search, rather than Page Object specific code duplicated
                nodeBrowserPage.selectStore("workspace://SpacesStore");
                nodeBrowserPage.selectQueryType("storeroot");
                nodeBrowserPage.clickSearchButton();
                assertTrue(nodeBrowserPage.isSearchResults(), "Query return Nothing");
                assertEquals(nodeBrowserPage.getSearchResultsCount(), 1, "Query returned more then one root node.");
                assertTrue(nodeBrowserPage.isInResults("workspace://SpacesStore/\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}"));
                assertTrue(nodeBrowserPage.isOnSearchBar("search took [0-9]+ms"));

                nodeBrowserPage.selectStore("archive://SpacesStore");
                nodeBrowserPage.clickSearchButton();
                assertTrue(nodeBrowserPage.isSearchResults(), "Query return Nothing");
                assertEquals(nodeBrowserPage.getSearchResultsCount(), 1, "Query returned more then one root node.");
                assertTrue(nodeBrowserPage.isInResults("archive://SpacesStore/\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}"));
                assertTrue(nodeBrowserPage.isOnSearchBar("search took [0-9]+ms"));
        }

        @Test(groups = { "Share" }, timeOut = 400000)
        public void enterprise40x_13886()
        {
                nodeBrowserPage.selectStore("workspace://SpacesStore");
                nodeBrowserPage.selectQueryType("lucene");
                nodeBrowserPage.fillQueryField("aaa");
                nodeBrowserPage.clickSearchButton();
                assertFalse(nodeBrowserPage.isSearchResults(), "Lucene alfresco query:'aaa' return results.");
                assertTrue(nodeBrowserPage.isOnSearchBar("search took [0-9]+ms"));

        }

        @Test(groups = { "Localization" }, timeOut = 400000)
        public void enterprise40x_13888()
        {
                executeLocalizationTest(Locale.FRENCH, "la recherche a pris [0-9]+ms");
        }

        @Test(groups = { "Localization" }, timeOut = 400000)
        public void enterprise40x_13889()
        {
                executeLocalizationTest(Locale.GERMANY, "Suche dauerte [0-9]+ms");
        }

        @Test(groups = { "Localization" }, timeOut = 400000)
        public void enterprise40x_13890()
        {
                Locale spanish = new Locale("es", "SP");
                executeLocalizationTest(spanish, "la búsqueda ha tardado [0-9]+ms");
        }

        @Test(groups = { "Localization" }, timeOut = 400000)
        public void enterprise40x_13891()
        {
                executeLocalizationTest(Locale.ITALIAN, "la ricerca ha richiesto [0-9]+ms");
        }

        @Test(groups = { "Localization" }, timeOut = 400000)
        public void enterprise40x_13892()
        {
                executeLocalizationTest(Locale.JAPANESE, "検索で[0-9]+msを取得済み");
        }

        @Test(groups = { "Localization" }, timeOut = 400000)
        public void enterprise40x_13893()
        {
                Locale dutch = new Locale("nl", "DU");
                executeLocalizationTest(dutch, "zoeken duurde [0-9]+ms");
        }

        @Test(groups = { "Localization" }, timeOut = 400000)
        public void enterprise40x_13894()
        {
                Locale russian = new Locale("ru", "RUS");
                executeLocalizationTest(russian, "поиск длился [0-9]+мс");
        }

        @Test(groups = { "Localization" }, timeOut = 400000)
        public void enterprise40x_13895()
        {
                Locale chinese = new Locale("zh_cn", "cn");
                executeLocalizationTest(chinese, "搜索花费 [0-9]+ms");
        }

        /**
         * Tests logic for localization
         *
         * @param locale
         * @param text
         */
        public void executeLocalizationTest(Locale locale, String text)
        {
                try
                {
                        SharePage page = createCustomDroneAndLogin(locale);
                        NodeBrowserPage nodeBrowserPage = page.getNav().getNodeBrowserPage();

                        nodeBrowserPage.fillQueryField("TEXT:alfresco");
                        nodeBrowserPage.clickSearchButton();
                        assertTrue(nodeBrowserPage.isSearchResults(), "FTS alfresco query:'TEXT:alfresco' return nothing.");
                        assertTrue(nodeBrowserPage.isOnSearchBar(text), "Correct localization text on status bar didn't present.");

                        nodeBrowserPage.selectQueryType("lucene");
                        nodeBrowserPage.fillQueryField("aaa");
                        nodeBrowserPage.clickSearchButton();
                        assertFalse(nodeBrowserPage.isSearchResults(), "Lucene alfresco query:'aaa' return results.");
                        assertTrue(nodeBrowserPage.isOnSearchBar(text), "Correct localization text on status bar didn't present.");
                }
                finally
                {
                        quitCustomDrone();
                }
        }

        /**
         * User logs out after test is executed from groups Localization
         */
        public void quitCustomDrone()
        {
                quit(customDrone);
        }

        // TODO: Redundant pl remove. AbstractTests > AfterClass does this
        /**
         * User logs out after group Share is executed
         */
        @AfterGroups("Share")
        public void quitDrone()
        {
                quit(drone);
        }

        // TODO: Redundant pl remove. AbstractTests > AfterClass does this
        private void quit(WebDrone drone)
        {
                ShareUser.logout(drone);
                logger.info("User logged out - drone.");
                drone.quit();
                logger.info("Close browser");
        }

        // TODO: Redundant pl remove. AbstractTests > AfterClass does this
        @Override
        public void tearDown()
        {
                //For didn't logout twice.
                logger.info("[Suite ] : End of Tests in: " + this.getClass().getSimpleName());
        }
}
