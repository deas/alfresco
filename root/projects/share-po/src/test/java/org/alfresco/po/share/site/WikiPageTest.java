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
package org.alfresco.po.share.site;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.po.share.site.document.TinyMceEditor.FormatType;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.po.share.AlfrescoVersion.Enterprise41;
import static org.alfresco.po.share.AlfrescoVersion.Enterprise42;

/**
 * @author nshah
 *
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class WikiPageTest extends AbstractSiteDashletTest
{

    DashBoardPage dashBoard;
    CustomiseSiteDashboardPage customizeSiteDashboardPage;
    SiteDashboardPage siteDashboardPage;
    CustomizeSitePage customizeSitePage;
    WikiPage wikiPage;
    List<String> textLines = new ArrayList<String>();
    
    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "wiki" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }
    @Test
    public void selectCustomizeDashboard() throws Exception
    {
    	AlfrescoVersion version = drone.getProperties().getVersion();
        if(Enterprise42.equals(version))
        {
            siteDashBoard.getSiteNav().selectConfigure();
        }
        else if(Enterprise41.equals(version))
        {
            siteDashBoard.getSiteNav().selectMore();
        }
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.WIKI);
        siteDashBoard = customizeSitePage.addPages(addPageTypes).render();
        wikiPage = siteDashBoard.getSiteNav().selectSiteWikiPage().render();
        Assert.assertTrue(wikiPage.getTitle().contains("Wiki"));
    }

    @Test(dependsOnMethods="selectCustomizeDashboard")
    public void testWikiPageDisplay() throws Exception
    {
    	wikiPage.clickOnNewPage();
    	Assert.assertTrue(wikiPage.isWikiPageDisplayed());
    }
    
    @Test(dependsOnMethods="testWikiPageDisplay")
    public void testBulletListOfWikiPage() throws Exception
    {
    	wikiPage.createWikiPageTitle("Wiki Page 1");
    	textLines.add("This is a new Wiki text!");
    	wikiPage.insertText(textLines);
    	TinyMceEditor tinyMceEditor = wikiPage.getTinyMCEEditor();    	
    	tinyMceEditor.clickTextFormatter(FormatType.BULLET);
    	Assert.assertEquals(textLines.get(0), tinyMceEditor.getText());       
        Assert.assertTrue(tinyMceEditor.getContent().contains("<li>" + textLines.get(0) + "</li>"));
        wikiPage.clickSaveButton().render();
    }
    
    @Test(dependsOnMethods = "testBulletListOfWikiPage", enabled = false)
    public void testFontStyle() throws Exception
    {
    	wikiPage.clickFontStyle();
    	Assert.assertEquals(textLines.get(0),wikiPage.retrieveWikiText("FONT"));
    	wikiPage.clickOnRemoveFormatting();
    	Assert.assertEquals(textLines.get(0),wikiPage.retrieveWikiText(""));
    }
    
    @Test(dependsOnMethods = "testFontStyle", enabled = false)
    public void testFontSize() throws Exception{
    	wikiPage.clickFontSize();
    	Assert.assertEquals(textLines.get(0), wikiPage.retrieveWikiText("FONT"));
    	wikiPage.clickOnRemoveFormatting();
    	Assert.assertEquals(textLines.get(0),wikiPage.retrieveWikiText(""));
    	wikiPage.clickSaveButton();
    }
}
