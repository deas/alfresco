/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.share.adminconsole;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.adminconsole.NodeBrowserPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.NodeBrowserPageUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.annotations.*;

import java.util.Locale;


import static org.alfresco.po.share.adminconsole.NodeBrowserPage.QueryType.*;
import static org.alfresco.po.share.adminconsole.NodeBrowserPage.Store.*;
import static org.testng.Assert.*;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class NodeBrowserTests extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(NodeBrowserTests.class);

    private enum LocalizationTestData
    {
        FRENCH(Locale.FRENCH, "la recherche a pris [0-9]+ms"),
        GERMANY(Locale.GERMANY, "Suche dauerte [0-9]+ms"),
        SPANISH(new Locale("es", "SP"), "la búsqueda ha tardado [0-9]+ms"),
        ITALIAN(Locale.ITALIAN, "la ricerca ha richiesto [0-9]+ms"),
        JAPANESE(Locale.JAPANESE, "検索で[0-9]+msを取得済み"),
        DUTCH(new Locale("nl", "DU"), "zoeken duurde [0-9]+ms"),
        RUSSIAN(new Locale("ru", "RUS"), "поиск длился [0-9]+мс"),
        CHINES(new Locale("zh_cn", "cn"), "搜索花费 [0-9]+ms");

        public final Locale locale;
        public final String text;

        LocalizationTestData(Locale locale, String text)
        {
            this.locale = locale;
            this.text = text;
        }
    }

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
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        NodeBrowserPageUtil.openNodeBrowserPage(drone);
    }

    @Test(groups = { "Share", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_13876()
    {
        NodeBrowserPage nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, STORE_ROOT, WORKSPACE_SPACE_STORE);
        assertTrue(nodeBrowserPage.isSearchResults(), "Query return Nothing");
        assertEquals(nodeBrowserPage.getSearchResultsCount(), 1, "Query returned more then one root node.");
        assertTrue(nodeBrowserPage.isInResultsByNodeRef("workspace://SpacesStore/\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}"));
        assertTrue(nodeBrowserPage.isOnSearchBar("search took [0-9]+ms"));

        nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, ARCHIVE_SPACE_STORE);
        assertTrue(nodeBrowserPage.isSearchResults(), "Query return Nothing");
        assertEquals(nodeBrowserPage.getSearchResultsCount(), 1, "Query returned more then one root node.");
        assertTrue(nodeBrowserPage.isInResultsByNodeRef("archive://SpacesStore/\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}"));
        assertTrue(nodeBrowserPage.isOnSearchBar("search took [0-9]+ms"));
    }

    @Test(groups = { "Share", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_13886()
    {
        NodeBrowserPage nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, "aaa", LUCENE, WORKSPACE_SPACE_STORE);
        assertFalse(nodeBrowserPage.isSearchResults(), "Lucene alfresco query:'aaa' return results.");
        assertTrue(nodeBrowserPage.isOnSearchBar("search took [0-9]+ms"));

    }

    @Test(groups = { "Localization", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_13888()
    {
        executeLocalizationTest(LocalizationTestData.FRENCH);
    }

    @Test(groups = { "Localization", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_13889()
    {
        executeLocalizationTest(LocalizationTestData.GERMANY);
    }

    @Test(groups = { "Localization", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_13890()
    {
        executeLocalizationTest(LocalizationTestData.SPANISH);
    }

    @Test(groups = { "Localization", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_13891()
    {
        executeLocalizationTest(LocalizationTestData.ITALIAN);
    }

    @Test(groups = { "Localization", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_13892()
    {
        executeLocalizationTest(LocalizationTestData.JAPANESE);
    }

    @Test(groups = { "Localization", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_13893()
    {
        executeLocalizationTest(LocalizationTestData.DUTCH);
    }

    @Test(groups = { "Localization", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_13894()
    {
        executeLocalizationTest(LocalizationTestData.RUSSIAN);
    }

    @Test(groups = { "Localization", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_13895()
    {
        executeLocalizationTest(LocalizationTestData.CHINES);
    }

    /**
     * Tests logic for localization
     *
     * @param localizationTestData
     */
    public void executeLocalizationTest(LocalizationTestData localizationTestData)
    {
        try
        {
            createCustomDroneAndLogin(localizationTestData.locale);
            NodeBrowserPageUtil.openNodeBrowserPage(customDrone);
            NodeBrowserPage nodeBrowserPage = NodeBrowserPageUtil.executeQuery(customDrone, "TEXT:alfresco");
            assertTrue(nodeBrowserPage.isSearchResults(), "FTS alfresco query:'TEXT:alfresco' return nothing.");
            assertTrue(nodeBrowserPage.isOnSearchBar(localizationTestData.text), "Correct localization text on status bar didn't present.");

            nodeBrowserPage = NodeBrowserPageUtil.executeQuery(customDrone, "aaa", LUCENE);
            assertFalse(nodeBrowserPage.isSearchResults(), "Lucene alfresco query:'aaa' return results.");
            assertTrue(nodeBrowserPage.isOnSearchBar(localizationTestData.text), "Correct localization text on status bar didn't present.");
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

    /**
     * User logs out after group Share is executed
     */
    @AfterGroups("Share")
    public void quitDrone()
    {
        quit(drone);
    }

    private void quit(WebDrone drone)
    {
        ShareUser.logout(drone);
        logger.info("User logged out - drone.");
        drone.quit();
        logger.info("Close browser");
    }

    @Override
    public void tearDown()
    {
        //For didn't logout twice.
        logger.info("[Suite ] : End of Tests in: " + this.getClass().getSimpleName());
    }
}
