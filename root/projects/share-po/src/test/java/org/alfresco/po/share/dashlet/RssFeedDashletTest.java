/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests for RSS Feed dashlet web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class RssFeedDashletTest extends AbstractSiteDashletTest
{
    private static final String RSS_FEED_DASHLET = "rss-feed";
    private RssFeedDashlet rssFeedDashlet = null;
    private String defaultTitle = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    RssFeedUrlBoxPage rssFeedUrlBoxPage = null;

    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows the RSS feed of your choice. Click the edit icon on the dashlet to change the feed.";
    private static final String EXP_TITLE_BY_DEFAULT = "Alfresco Blog";
    private static final String CUSTOM_RSS_URL = "http://projects.apache.org/feeds/atom.xml";
    private static final String EXP_CUSTOM_RSS_TITLE = "Apache Software Foundation Project Releases";

    @BeforeClass
    public void setUp() throws Exception
    {
        loginAs("admin", "admin");
        siteName = "rssFeedDashletTest" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @Test(groups = "Enterprise-only")
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.RSS_FEED, 1).render();
        rssFeedDashlet = siteDashBoard.getDashlet(RSS_FEED_DASHLET).render();
        assertNotNull(rssFeedDashlet);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "instantiateDashlet")
    public void verifyTitle()
    {
        defaultTitle = rssFeedDashlet.getTitle();
        assertNotNull(defaultTitle);
        assertEquals(defaultTitle, EXP_TITLE_BY_DEFAULT);
    }


    @Test(groups = "Enterprise-only", dependsOnMethods = "verifyTitle")
    public void verifyHelpIcon()
    {
        rssFeedDashlet.clickOnHelpIcon();
        assertTrue(rssFeedDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = rssFeedDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        rssFeedDashlet.closeHelpBallon();
        assertFalse(rssFeedDashlet.isBalloonDisplayed());
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "verifyHelpIcon")
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
