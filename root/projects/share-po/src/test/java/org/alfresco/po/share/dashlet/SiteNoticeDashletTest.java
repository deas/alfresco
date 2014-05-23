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
import org.alfresco.po.share.dashlet.InsertOrEditImagePage.ImageAlignment;
import org.alfresco.po.share.dashlet.InsertOrEditLinkPage.InsertLinkPageTargetItems;
import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.po.share.enums.TinyMceColourCode;
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
    private HtmlSourceEditorPage htmlSourcePage = null;
    private String titleAndText = null;
    private String anchorName = "testAnchor";
    private static final String expectedHelpBallonMsg = "This dashlet displays a custom message on the dashboard, specified by the site manager";
    private String fontAttForCloud = "<font style=\"color: rgb(0, 0, 255);\">";
    private String fontAtt = "<span style=\"color: rgb(0, 0, 255);\">";
    private String fontBackColorAttr = "<span style=\"background-color: rgb(0, 0, 0);\">";
    private InsertOrEditLinkPage editLinkPage  = null;
    private InsertOrEditImagePage insertOrEditImage = null;
    private final String linkContent = "<a target=\"_blank\" title=\"Test\" href=\"https://google.co.uk\" data-mce-href=\"https://google.co.uk\">%s</a>";
    private final String anchorContent = "<a class=\"mceItemAnchor\" name=\"%s\"";
    private String imageURL = "http://cdn2.business2community.com/wp-content/uploads/2013/04/google-.jpg";
    private final String imageDescription = "Alfresco Business Image";
    private long imageWidth = 100;
    private long imageHeight = 100;
    String image_src_content = "src=\"%s\"";
    String image_data_src_content = "data-mce-src=\"%s\"";
    String image_align_content = "align=\"bottom\"";
    String image_height_content = "height=\"%s\"";
    String image_width_content = "width=\"%s\"";
    String image_alt_content = "alt=\"%s\"";
    
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
        siteNoticeEditor.clickColorCode(TinyMceColourCode.BLUE);
        Assert.assertEquals(titleAndText, siteNoticeEditor.getText());
        AlfrescoVersion version = drone.getProperties().getVersion();
       
        if (AlfrescoVersion.Cloud2.equals(version))
        {
            fontAtt = fontAttForCloud ;
        }
        Assert.assertTrue(siteNoticeEditor.getContent().contains(fontAtt +titleAndText+"</span>"));
    }
    
    @Test(dependsOnMethods="getTextFromEditor")
    public void getBackColorFromEditor()
    {
        siteNoticeEditor.clickTextFormatterWithOutSelectingText(FormatType.BOLD);
        siteNoticeEditor.removeFormatting();
        siteNoticeEditor.clickBackgroundColorCode(TinyMceColourCode.BLACK);
        Assert.assertEquals(titleAndText, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains(fontBackColorAttr));
    }
    
    @Test(dependsOnMethods="getBackColorFromEditor")
    public void testInsertOrEditLink()
    {
        siteNoticeEditor.removeFormatting();
        editLinkPage = siteNoticeEditor.clickInsertOrEditLink().render();
        Assert.assertNotNull(editLinkPage);
    }
    
    @Test(dependsOnMethods="testInsertOrEditLink",expectedExceptions = IllegalArgumentException.class)
    public void testLinkUrlWithNull()
    {
        editLinkPage.setLinkUrl(null);
    }
    
    @Test(dependsOnMethods="testLinkUrlWithNull")
    public void testLinkCancel()
    {
        editLinkPage.setTarget(InsertLinkPageTargetItems.OPEN_LINK_IN_NEW_WINDOW);
        editLinkPage.setTitle("Test");
        editLinkPage.setLinkUrl("https://google.co.uk");
        configureSiteNoticeDialog = editLinkPage.clickOnCancelButton().render();
        Assert.assertFalse(configureSiteNoticeDialog.getContentTinyMceEditor().getContent().contains(String.format(linkContent, titleAndText)));
    }
    
    @Test(dependsOnMethods="testLinkCancel")
    public void testLinkUrl()
    {
        editLinkPage = siteNoticeEditor.clickInsertOrEditLink().render();
        
        editLinkPage.setTarget(InsertLinkPageTargetItems.OPEN_LINK_IN_NEW_WINDOW);
        editLinkPage.setTitle("Test");
        editLinkPage.setLinkUrl("https://google.co.uk");
        configureSiteNoticeDialog = editLinkPage.clickInsertOrUpdateButton().render();
        siteNoticeEditor = configureSiteNoticeDialog.getContentTinyMceEditor();
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(linkContent, titleAndText)));
        siteDashBoard = configureSiteNoticeDialog.clickOnOKButton().render();
    }  
    
    @Test(dependsOnMethods="testLinkUrl")
    public void testLinkOnSiteNoticeDashlet()
    {
        noticeDashlet = siteDashBoard.getDashlet(SITE_NOTICE).render();
        String mainDrone = drone.getWindowHandle();
        noticeDashlet.selectLink(titleAndText);
        Assert.assertTrue(switchDrone(drone.getValue("page.google.title")));
        drone.closeWindow();
       drone.switchToWindow(mainDrone);
       Assert.assertTrue(drone.getTitle().contains(drone.getValue("page.site.dashboard.title")));
    }
    
    @Test(dependsOnMethods="testLinkOnSiteNoticeDashlet")
    public void testUpdateLink()
    {
        configureSiteNoticeDialog = noticeDashlet.clickOnConfigureIcon().render();
        siteNoticeEditor = configureSiteNoticeDialog.getContentTinyMceEditor();
        siteNoticeEditor.selectTextFromEditor();
        editLinkPage = siteNoticeEditor.clickInsertOrEditLink().render();
        editLinkPage.setTitle("TestUpdate");
        configureSiteNoticeDialog = editLinkPage.clickInsertOrUpdateButton().render();
       Assert.assertNotNull(configureSiteNoticeDialog);
    }
    
    @Test(dependsOnMethods="testUpdateLink")
    public void testUnLink()
    {
        siteNoticeEditor = configureSiteNoticeDialog.getContentTinyMceEditor();
        siteNoticeEditor.selectTextFromEditor();
        siteNoticeEditor.clickUnLink();
        Assert.assertFalse(siteNoticeEditor.getContent().contains(String.format(linkContent, titleAndText)));
    }
    
    @Test(dependsOnMethods="testUnLink")
    public void testInsertEditAnchor()
    {
        InsertOrEditAnchorPage insertOrEditAnchor = siteNoticeEditor.selectInsertOrEditAnchor().render();
        insertOrEditAnchor.setName(anchorName);
        configureSiteNoticeDialog = insertOrEditAnchor.clickInsertOrUpdateButton().render();
        siteNoticeEditor = configureSiteNoticeDialog.getContentTinyMceEditor();
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(anchorContent, anchorName)));
    }
    
    @Test(dependsOnMethods="testInsertEditAnchor")
    public void testInsertEditAnchorCancelChanges()
    {
        String newAnchorName = anchorName + "updated";
        InsertOrEditAnchorPage insertOrEditAnchor = siteNoticeEditor.selectInsertOrEditAnchor().render();
        insertOrEditAnchor.setName(newAnchorName);
        configureSiteNoticeDialog = insertOrEditAnchor.clickOnCancelButton().render();
        siteNoticeEditor = configureSiteNoticeDialog.getContentTinyMceEditor();
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(anchorContent, anchorName)));
        Assert.assertFalse(siteNoticeEditor.getContent().contains(String.format(anchorContent, newAnchorName)));
    }
    
    @Test(dependsOnMethods="testInsertEditAnchorCancelChanges")
    public void testClickOnImageLink()
    {
        insertOrEditImage = siteNoticeEditor.selectInsertOrEditImage().render();
        Assert.assertNotNull(insertOrEditImage);
        Assert.assertTrue(insertOrEditImage.getTitle().equalsIgnoreCase(drone.getValue("page.insert.edit.image.title")));
    }
    
    @Test(dependsOnMethods="testClickOnImageLink",expectedExceptions = IllegalArgumentException.class)
    public void testImageUrlWithNull()
    {
        insertOrEditImage.setImageUrl(null);
    }
    
    @Test(dependsOnMethods="testImageUrlWithNull",expectedExceptions = IllegalArgumentException.class)
    public void testImageDescWithNull()
    {
        insertOrEditImage.setDescription(null);
    }
    
    @Test(dependsOnMethods="testImageDescWithNull",expectedExceptions = IllegalArgumentException.class)
    public void testDimentionsWidthWithNegitiveValues()
    {
        insertOrEditImage.setDimensions(-1, -200);
    }
    
    @Test(dependsOnMethods="testDimentionsWidthWithNegitiveValues")
    public void testImage()
    {
        insertOrEditImage.setImageUrl(imageURL);
        insertOrEditImage.setDescription("Alfresco Business Image");
        insertOrEditImage.setAlignment(ImageAlignment.BOTTOM);
        insertOrEditImage.setDimensions(imageWidth,imageHeight);
        configureSiteNoticeDialog = insertOrEditImage.clickInsertOrUpdateButton().render();
        siteNoticeEditor = configureSiteNoticeDialog.getContentTinyMceEditor();
        
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_src_content,imageURL)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_data_src_content,imageURL)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_alt_content, imageDescription)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_height_content, imageHeight)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_width_content, imageWidth)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(image_align_content));
    }
    
    @Test(dependsOnMethods="testImage")
    public void testImageCancelChanges()
    {
        insertOrEditImage = siteNoticeEditor.selectInsertOrEditImage().render();
        String newDescription = "Updated Description";
        insertOrEditImage.setImageUrl(imageURL);
        insertOrEditImage.setDescription(imageDescription);
        insertOrEditImage.setAlignment(ImageAlignment.BOTTOM);
        insertOrEditImage.setDimensions(imageWidth,imageHeight);
        configureSiteNoticeDialog = insertOrEditImage.clickOnCancelButton().render();
        siteNoticeEditor = configureSiteNoticeDialog.getContentTinyMceEditor();

        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_src_content,imageURL)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_data_src_content,imageURL)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_alt_content, imageDescription)));
        Assert.assertFalse(siteNoticeEditor.getContent().contains(String.format(image_alt_content, newDescription)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_height_content, imageHeight)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_width_content, imageWidth)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(image_align_content));
    }

    @Test(dependsOnMethods="testImageCancelChanges", expectedExceptions = IllegalArgumentException.class)
    public void testHtmlSourceEditorWithNull()
    {
        siteNoticeEditor.clearAll();
        htmlSourcePage = siteNoticeEditor.selectHtmlSourceEditor().render();
        htmlSourcePage.setHTMLSource(null);
    }
    
    @Test(dependsOnMethods="testHtmlSourceEditorWithNull")
    public void testHtmlSourceEditor()
    {
        String newHtmlSource = "<p>hello</p>";
        htmlSourcePage.setHTMLSource(newHtmlSource);
        configureSiteNoticeDialog = htmlSourcePage.clickInsertOrUpdateButton().render();
        siteNoticeEditor = configureSiteNoticeDialog.getContentTinyMceEditor();
        Assert.assertTrue(siteNoticeEditor.getContent().contains(newHtmlSource));
    }
    
    @Test(dependsOnMethods="testHtmlSourceEditor")
    public void testHtmlSourceEditorCancelChanges()
    {
        String newHtmlSource = "<p>hello2</p>";
        HtmlSourceEditorPage htmlSourcePage = siteNoticeEditor.selectHtmlSourceEditor().render();
        htmlSourcePage.setHTMLSource(newHtmlSource);
        configureSiteNoticeDialog = htmlSourcePage.clickOnCancelButton().render();
        siteNoticeEditor = configureSiteNoticeDialog.getContentTinyMceEditor();
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<p>hello</p>"));
        Assert.assertFalse(siteNoticeEditor.getContent().contains(newHtmlSource));
    }
    
    private boolean switchDrone(String windowName)
    {
        for (String windowHandle : drone.getWindowHandles())
        {
            drone.switchToWindow(windowHandle);
            if (drone.getTitle().equals(windowName))
            {
                return true;
            }
        }

        return false;
    }
}