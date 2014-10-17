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
package org.alfresco.po.share.site;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPageList;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class WikiPageListTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard = null;
    CustomizeSitePage customizeSitePage = null;
    WikiPage wikiPage = null;
    WikiPageList wikiPageList = null;
    String wikiTitle = getClass().getSimpleName();
    String editedTitle = wikiTitle + "edited";
    List<String> textLines = new ArrayList<>();
    List<String> tagList = new ArrayList<>();
    String wikiPageText = "This is a wiki!";
    String tagName = "tag_wiki";

    String editedTxtLines = "Edited wiki text";
    Double toVersion = 1.0;

    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "wikiList" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        List<SitePageType> addPageTypes = new ArrayList<>();
        addPageTypes.add(SitePageType.WIKI);
        customizeSitePage.addPages(addPageTypes);
        wikiPageList = siteDashBoard.getSiteNav().selectWikiPage().clickWikiPageListBtn();

    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void createWikiPage()
    {
        textLines.add(wikiPageText);
        tagList.add(tagName);
        wikiPage = wikiPageList.createWikiPage(wikiTitle, textLines, tagList).render();
        assertNotNull(wikiPage);
        wikiPageList = wikiPage.clickWikiPageListBtn().render();
    }

    @Test(dependsOnMethods = "createWikiPage")
    public void isWikiPagePresent()
    {
        Assert.assertTrue(wikiPageList.isWikiPagePresent(wikiTitle));
    }

    @Test(dependsOnMethods = "isWikiPagePresent")
    public void getWikiPageTextFromPageList()
    {
        Assert.assertTrue((wikiPageList.getWikiPageTextFromPageList(wikiTitle)).contains(wikiPageText));
    }

    @Test(dependsOnMethods = "getWikiPageTextFromPageList")
    public void checkTags()
    {
        Assert.assertTrue(wikiPageList.checkTags(wikiTitle, tagName));
    }

    @Test(dependsOnMethods = "checkTags")
    public void editWikiPage()
    {
        wikiPageList.editWikiPage(wikiTitle, editedTxtLines);
        assertNotNull(wikiPage);
        assertEquals(wikiPage.getWikiText(), editedTxtLines);
    }

    @Test(dependsOnMethods = "editWikiPage")
    public void renameWikiPage()
    {
        wikiPage.renameWikiPage(editedTitle);
        assertEquals(wikiPage.getWikiTitle(), editedTitle);
    }

    @Test(dependsOnMethods = "renameWikiPage")
    public void revertToVersion()
    {
        wikiPage.clickDetailsLink();
        Double expVersion = wikiPage.getCurrentWikiVersion() + 0.1;
        wikiPage.revertToVersion(toVersion);
        assertEquals(wikiPage.getCurrentWikiVersion(), expVersion);
    }

    @Test(dependsOnMethods = "revertToVersion")
    public void removeTag()
    {
        wikiPageList = wikiPage.clickWikiPageListBtn().render();
        String[] removeTags = { tagName };
        wikiPage = wikiPageList.removeTag(editedTitle, removeTags);
        wikiPageList = wikiPage.clickWikiPageListBtn().render();
        Assert.assertTrue(wikiPageList.checkTags(editedTitle, null));
    }

    @Test(dependsOnMethods = "removeTag")
    public void clickWikiPage()
    {
        Assert.assertTrue(wikiPageList.isWikiPagePresent(editedTitle));
        wikiPage = wikiPageList.clickWikiPage(editedTitle);
        assertNotNull(wikiPage);
        assertEquals(wikiPage.getWikiText(), wikiPageText);
    }

    @Test(dependsOnMethods = "clickWikiPage")
    public void clickWikiPageDetails()
    {
        wikiPageList = wikiPage.clickWikiPageListBtn().render();
        Assert.assertTrue(wikiPageList.isWikiPagePresent(editedTitle));
        wikiPage = wikiPageList.clickWikiPageDetails(editedTitle);
        assertEquals(wikiPage.getWikiText(), wikiPageText);
    }

    @Test(dependsOnMethods = "clickWikiPageDetails")
    public void deleteWikiPage()
    {
        wikiPageList = wikiPage.clickWikiPageListBtn();
        int expCount = wikiPageList.getWikiCount() - 1;
        wikiPageList = wikiPageList.deleteWikiWithConfirm(editedTitle);
        assertEquals(wikiPageList.getWikiCount(), expCount);
    }

    @Test(dependsOnMethods = "deleteWikiPage")
    public void isNoWikiPagesDisplayed()
    {
        wikiPageList = wikiPage.clickWikiPageListBtn();
        wikiPageList = wikiPageList.deleteWikiWithConfirm(wikiTitle);
        Assert.assertTrue(wikiPageList.isNoWikiPagesDisplayed());
    }
}
