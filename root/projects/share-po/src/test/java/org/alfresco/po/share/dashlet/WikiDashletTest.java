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

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.wiki.WikiPageList;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.alfresco.po.share.site.SitePageType.WIKI;
import static org.testng.Assert.*;

/**
 * Tests for Wiki dashlet web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2", "Enterprise-only" })
public class WikiDashletTest extends AbstractSiteDashletTest
{
    private static final String WIKI_DASHLET = "wiki";
    private WikiDashlet wikiDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private SelectWikiDialogueBoxPage selectWikiDialogueBoxPage = null;
    private String wikiTitle = getClass().getSimpleName();
    private List<String> textLines = Arrays.asList(wikiTitle);

    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows a page selected from the site's wiki.\n"
            + "Navigate to the wiki to see all related content.";
    private static final String EXP_CONTENT_BY_DEFAULT = "No page is configured";

    @BeforeClass
    public void setUp() throws Exception
    {
        loginAs("admin", "admin");
        siteName = "wikiDashletTest" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
        CustomizeSitePage customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(Arrays.asList(WIKI));
        WikiPageList wikiPageList = siteDashBoard.getSiteNav().selectWikiPage().clickWikiPageListBtn();
        wikiPageList.createWikiPage(wikiTitle, textLines);
    }

    @Test
    public void instantiateDashlet()
    {
        navigateToSiteDashboard();
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.WIKI, 1).render();
        wikiDashlet = siteDashBoard.getDashlet(WIKI_DASHLET).render();
        assertNotNull(wikiDashlet);
    }


    @Test(dependsOnMethods = "instantiateDashlet")
    public void verifyContent()
    {
        String wikiDashletText = wikiDashlet.getContent();
        assertEquals(wikiDashletText, EXP_CONTENT_BY_DEFAULT);
    }

    @Test(dependsOnMethods = "verifyContent")
    public void verifyHelpIcon()
    {
        wikiDashlet.clickOnHelpIcon();
        assertTrue(wikiDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = wikiDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        wikiDashlet.closeHelpBallon();
        assertFalse(wikiDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void verifyConfigureIcon()
    {
        selectWikiDialogueBoxPage = wikiDashlet.clickConfigure();
        assertNotNull(selectWikiDialogueBoxPage);
    }

    @Test(dependsOnMethods = "verifyConfigureIcon")
    public void verifySelectWikiPage()
    {
        selectWikiDialogueBoxPage.selectWikiPageBy(wikiTitle);
        assertEquals(wikiDashlet.getContent(), wikiTitle);
    }
}
