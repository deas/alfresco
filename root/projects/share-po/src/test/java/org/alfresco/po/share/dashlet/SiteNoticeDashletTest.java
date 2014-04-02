/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.document.TinyMceEditor.FormatType;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test site content dashlet page elements.
 * 
 * @author Chiran
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2", "Cloud2" })
public class SiteNoticeDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_NOTICE = "site-notice";
    
    private SiteNoticeDashlet noticeDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private ConfigureSiteNoticeDialogBoxPage configureSiteNoticeDialog = null;
    private ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = null;
    private String titleAndText = null;
    private static final String expectedHelpBallonMsg = "This dashlet displays a custom message on the dashboard, specified by the site manager";
    private String fontAttForCloud = "<font style=\"color: rgb(0, 0, 255);\">";
    private String fontAtt = "<font color=\"#0000FF\">";
    private String fontBackColorAttr = "<font style=\"background-color: rgb(255, 255, 0);\">";
    
    @BeforeTest
    public void prepare()
    {
        siteName = "sitenoticedashlettest" + System.currentTimeMillis();
     }
    
    @BeforeClass
    public void loadFile() throws Exception
    {
        uploadDocument();
        navigateToSiteDashboard();
    }
    
    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlet.SITE_NOTICE, 1).render();
        noticeDashlet = siteDashBoard.getDashlet(SITE_NOTICE).render();
        Assert.assertNotNull(noticeDashlet);
    }

    @Test(dependsOnMethods="instantiateDashlet")
    public void verifyHelpAndConfigureIcons()
    {
        Assert.assertTrue(noticeDashlet.isHelpIconDisplayed());
        Assert.assertTrue(noticeDashlet.isConfigureIconDisplayed());
    }

    @Test(dependsOnMethods="verifyHelpAndConfigureIcons")
    public void selectHelpIcon() 
    {
        noticeDashlet.clickOnHelpIcon();
        Assert.assertTrue(noticeDashlet.isBalloonDisplayed());

        String actualHelpBallonMsg = noticeDashlet.getHelpBalloonMessage();
        Assert.assertEquals(actualHelpBallonMsg, expectedHelpBallonMsg);
    }

    @Test(dependsOnMethods = "selectHelpIcon")
    public void selectConfigureIcon() 
    {
        noticeDashlet.closeHelpBallon();
        configureSiteNoticeDialog = noticeDashlet.clickOnConfigureIcon().render();
        
        Assert.assertNotNull(configureSiteNoticeDialog);
    }

    @Test(dependsOnMethods="selectConfigureIcon")
    public void configureWithDetailsAndClickOK() 
    {
        titleAndText = siteName + System.currentTimeMillis();
        
        configureSiteNoticeDialog.setTitle(titleAndText);
        configureSiteNoticeDialog.setText(titleAndText);
        siteDashBoard = configureSiteNoticeDialog.clickOnOKButton().render();
        noticeDashlet = siteDashBoard.getDashlet(SITE_NOTICE).render();
        
        Assert.assertEquals(noticeDashlet.getTitle(), titleAndText);
        Assert.assertEquals(noticeDashlet.getContent(), titleAndText); 
    }
    
    @Test(dependsOnMethods="configureWithDetailsAndClickOK")
    public void configureWithDetailsAndClickCancel()
    {
        titleAndText = siteName + System.currentTimeMillis();
        
        configureSiteNoticeDialog = noticeDashlet.clickOnConfigureIcon().render();
        configureSiteNoticeDialog.setTitle(titleAndText);
        configureSiteNoticeDialog.setText(titleAndText);
        configureSiteNoticeDialog.clickOnCancelButton();
        noticeDashlet = siteDashBoard.getDashlet(SITE_NOTICE).render();
        
        Assert.assertNotEquals(noticeDashlet.getTitle(), titleAndText);
        Assert.assertNotEquals(noticeDashlet.getContent(), titleAndText); 
    }
    
    @Test(dependsOnMethods="configureWithDetailsAndClickCancel")
    public void configureWithDetailsAndClickClose()
    {
        titleAndText = siteName + System.currentTimeMillis();
        configureSiteNoticeDialog = noticeDashlet.clickOnConfigureIcon().render();
        configureSiteNoticeDialog.setTitle(titleAndText);
        configureSiteNoticeDialog.setText(titleAndText);
        configureSiteNoticeDialog.clickOnCloseButton();
        noticeDashlet = siteDashBoard.getDashlet(SITE_NOTICE).render();
        
        Assert.assertNotEquals(noticeDashlet.getTitle(), titleAndText);
        Assert.assertNotEquals(noticeDashlet.getContent(), titleAndText); 
    }
    
    @Test(dependsOnMethods="configureWithDetailsAndClickClose")
    public void getTextFromEditor()
    {
        configureSiteNoticeDialog = noticeDashlet.clickOnConfigureIcon().render();
        siteNoticeEditor = configureSiteNoticeDialog.getContentTinyMceEditor();
        
        // test text as blue color
        siteNoticeEditor.clickColorCode();
        Assert.assertEquals(titleAndText, siteNoticeEditor.getTextFromConfigureTextEditor(siteName));
        AlfrescoVersion version = drone.getProperties().getVersion();
       
        if (AlfrescoVersion.Cloud2.equals(version))
        {
            fontAtt = fontAttForCloud ;
        }
        Assert.assertTrue(siteNoticeEditor.getContent().contains(fontAtt +titleAndText+"</font>"));
    }
    
    @Test(dependsOnMethods="getTextFromEditor", groups="BambooBug")
    public void getBackColorFromEditor()
    {
        // test text as yellow back ground color
        siteNoticeEditor.setTinyMceOfConfigureDialogBox(siteName);
        siteNoticeEditor.clickTextFormatterFromConfigureDialog(FormatType.BOLD);
        siteNoticeEditor.removeFormatting();
        siteNoticeEditor.clickBackgroundColorCode();
        Assert.assertEquals(titleAndText, siteNoticeEditor.getTextFromConfigureTextEditor(siteName));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(fontBackColorAttr));
        configureSiteNoticeDialog.clickOnCloseButton();
    }
}