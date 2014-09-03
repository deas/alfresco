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
package org.alfresco.po.share.dashlet;

import static org.alfresco.po.share.enums.Dashlets.ALFRESCO_ADDONS_RSS_FEED;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class SiteAddOnsRssFeedDashletTest extends AbstractSiteDashletTest
{
    private static final String DASHLET_NAME = "addOns-rss";
    private AddOnsRssFeedDashlet rssFeedDashlet = null;
    private String defaultTitle = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    RssFeedUrlBoxPage rssFeedUrlBoxPage = null;

    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows the latest news from Alfresco Add-ons. Click the edit icon on the dashlet to configure the feed.";
    private static final String EXP_TITLE_BY_DEFAULT = "Alfresco Add-ons RSS Feed";
    private static final String CUSTOM_RSS_URL = "http://projects.apache.org/feeds/atom.xml";
    private static final String EXP_CUSTOM_RSS_TITLE = "Apache Software Foundation Project Releases";

    @BeforeClass
    public void setUp() throws Exception
    {
        siteName = "AddOnsRssDashletTest" + System.currentTimeMillis();
        loginAs("admin", "admin");
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(ALFRESCO_ADDONS_RSS_FEED, 1).render();
        rssFeedDashlet = siteDashBoard.getDashlet(DASHLET_NAME).render();
        assertNotNull(rssFeedDashlet);
    }

    @Test(dependsOnMethods = "instantiateDashlet")
    public void getDefaultTitle()
    {
        defaultTitle = rssFeedDashlet.getTitle();
        assertNotNull(defaultTitle);
        assertEquals(defaultTitle, EXP_TITLE_BY_DEFAULT);
    }

    @Test(dependsOnMethods = "getDefaultTitle")
    public void verifyHelpIcon()
    {
        rssFeedDashlet.clickOnHelpIcon();
        assertTrue(rssFeedDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = rssFeedDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        rssFeedDashlet.closeHelpBallon();
        assertFalse(rssFeedDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void clickConfigureButton()
    {
        rssFeedUrlBoxPage = rssFeedDashlet.clickConfigure().render();
        assertNotNull(rssFeedUrlBoxPage);
    }

    @Test(dependsOnMethods = "clickConfigureButton")
    public void configExternalRss()
    {
        rssFeedUrlBoxPage.fillURL(CUSTOM_RSS_URL);
        rssFeedUrlBoxPage.clickOk();
        for (int i = 0; i < 1000; i++)
        {
            if (!defaultTitle.equals(rssFeedDashlet.getTitle()))
            {
                break;
            }
        }
        assertEquals(rssFeedDashlet.getTitle(), EXP_CUSTOM_RSS_TITLE);
    }

}
