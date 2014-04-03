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
package org.alfresco.po.share.site.document;

import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.TinyMceEditor.FormatType;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class ContentFormatPageTest extends AbstractTest
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static String siteName;
    private static String folderName;
    private static String folderDescription;
    private static DocumentLibraryPage documentLibPage;
    private FolderDetailsPage folderDetailsPage;
    private TinyMceEditor textEditor;
    private String commentText;
    private String fontAttForCloud = "<font style=\"color: rgb(0, 0, 255);\">";
    private String fontAtt = "<font color=\"#0000FF\">";
     
    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="alfresco-one")
    public void prepare() throws Exception
    {
        if (logger.isTraceEnabled()) logger.trace("====prepare====");
        commentText = "This is Comment";
        siteName = "site" + System.currentTimeMillis();
        folderName = "The first folder";
        folderDescription = String.format("Description of %s", folderName);
        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");
    }

    @AfterClass(groups="alfresco-one")
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     * 
     * @throws Exception
     */
    @Test(groups="alfresco-one")
    public void createData() throws Exception
    {
        if (logger.isTraceEnabled()) logger.trace("====createData====");
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
        FileDirectoryInfo content = getFolder();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(content.getName());
        folderDetailsPage = thisRow.selectViewFolderDetails().render();
        folderDetailsPage.addComment(commentText);  
        textEditor = folderDetailsPage.getContentPage();
        textEditor.setTinyMce(TinyMceEditor.FRAME_ID);
        textEditor.addContent(commentText);        
    }

    // 1) Enter text in the Rich Text Editor
    // 2) click on BOLD button
    @Test(dependsOnMethods = "createData",groups="alfresco-one")
    public void testBoldFontOfRichTextFormatter()
    {
        if (logger.isTraceEnabled()) logger.trace("====testBoldFontOfRichTextFormatter====");
        textEditor.clickTextFormatter(FormatType.BOLD);
        Assert.assertEquals(commentText, textEditor.getText());       
        Assert.assertTrue(textEditor.getContent().contains("<b>"+commentText+"</b>"));
        textEditor.removeFormatting();

    }

    // 1) Enter text in the Rich Text Editor
    // 2) click on ITALIC button
    @Test(dependsOnMethods = "testBoldFontOfRichTextFormatter",groups="alfresco-one")
    public void testItalicFontOfRichTextFormatter()
    {
        if (logger.isTraceEnabled()) logger.trace("====testItalicFontOfRichTextFormatter====");
        textEditor.clickTextFormatter(FormatType.ITALIC);
        Assert.assertEquals(commentText, textEditor.getText());       
        Assert.assertTrue(textEditor.getContent().contains("<i>"+commentText+"</i>"));
        textEditor.removeFormatting();
    }
    // 1) Enter text in the Rich Text Editor
    // 2) click on ITALIC button
    @Test(dependsOnMethods = "testItalicFontOfRichTextFormatter",groups="alfresco-one")
    public void testUnderLinedFontOfRichTextFormatter()
    {
        if (logger.isTraceEnabled()) logger.trace("====testUnderLinedFontOfRichTextFormatter====");
        textEditor.clickTextFormatter(FormatType.UNDERLINED);
        Assert.assertEquals(commentText, textEditor.getText());       
        Assert.assertTrue(textEditor.getContent().contains("<u>"+commentText+"</u>"));
        textEditor.removeFormatting();
    }

    // 1) Enter text in the Rich Text Editor
    // 2) click on BULLET paragraph
    @Test(dependsOnMethods = "testUnderLinedFontOfRichTextFormatter",groups="alfresco-one")
    public void testBulletPointInsertionInRichTextFormatter()
    {
        if (logger.isTraceEnabled()) logger.trace("====testBulletPointInsertionInRichTextFormatter====");
        textEditor.clickTextFormatter(FormatType.BULLET);
        Assert.assertEquals(commentText, textEditor.getText());       
        Assert.assertTrue(textEditor.getContent().contains("<li>"+commentText+"</li>"));
        textEditor.clickTextFormatter(FormatType.BULLET);
        Assert.assertEquals(commentText, textEditor.getText());       
        Assert.assertTrue(textEditor.getContent().contains("<p>"+commentText+"</p>"));
       
    }

    // 1) Enter text in the Rich Text Editor
    // 2) click on NUMBERED paragraph
    @Test(dependsOnMethods = "testBulletPointInsertionInRichTextFormatter",groups="alfresco-one")
    public void testNumberInsertionInRichTextFormatter()
    {
        if (logger.isTraceEnabled())
            logger.trace("====testNumberInsertionInRichTextFormatter====");
        textEditor.clickTextFormatter(FormatType.NUMBER);
        Assert.assertEquals(commentText, textEditor.getText());       
        Assert.assertTrue(textEditor.getContent().contains("<ol><li>"+commentText+"</li></ol>"));
        textEditor.clickTextFormatter(FormatType.NUMBER);
        Assert.assertTrue(textEditor.getContent().contains("<p>"+commentText+"</p>"));
     }

    // 1) Enter text in the Rich Text Editor
    // 2) click on [A] button colour change of the text
    // 3) select colour from drop down and click on desired colour
    @Test(dependsOnMethods = "testNumberInsertionInRichTextFormatter", groups = "alfresco-one" )
    public void testColourCodeInsertionInRichTextFormatter()
    {
        if (logger.isTraceEnabled())
            logger.trace("====testColourCodeInsertionInRichTextFormatter====");
        textEditor.clickColorCode();
        Assert.assertEquals(commentText, textEditor.getText());
        AlfrescoVersion version = drone.getProperties().getVersion();
        if (AlfrescoVersion.Cloud2.equals(version))
        {
            fontAtt = fontAttForCloud ;
        }
        Assert.assertTrue(textEditor.getContent().contains(fontAtt +commentText+"</font>"));
        textEditor.removeFormatting();
        
    }

    // 1) Enter text in the Rich Text Editor
    // 2) click on [A] button colour change of the text
    // 3) click on UNDO symbol.
    // 4) click on REDO symbol.
    @Test(dependsOnMethods = "testColourCodeInsertionInRichTextFormatter",groups = "alfresco-one")
    public void testUndoButtonOfRichTextFormatter()
    {
        if (logger.isTraceEnabled())
            logger.trace("====testUndoAndRedoButtonOfRichTextFormatter====");
        textEditor.clickColorCode();
        Assert.assertEquals(commentText, textEditor.getText());       
        AlfrescoVersion version = drone.getProperties().getVersion();
        if (AlfrescoVersion.Cloud2.equals(version))
        {
            fontAtt = fontAttForCloud;
        }
        Assert.assertTrue(textEditor.getContent().contains(fontAtt +commentText+"</font>"));
        textEditor.clickUndo();        
        Assert.assertTrue(textEditor.getContent().contains("<p>"+commentText+"</p>"));
    }
    
    // 1) Enter text in the Rich Text Editor
    // 2) click on [A] button colour change of the text
    // 3) click on UNDO symbol.
    // 4) click on REDO symbol.
    @Test(dependsOnMethods = "testUndoButtonOfRichTextFormatter",groups = "alfresco-one")
    public void testRedoButtonOfRichTextFormatter()
    {
        if (logger.isTraceEnabled())
            logger.trace("====testRedoButtonOfRichTextFormatter====");
      
        textEditor.clickColorCode();
        Assert.assertEquals(commentText, textEditor.getText());  
        AlfrescoVersion version = drone.getProperties().getVersion();
        if (AlfrescoVersion.Cloud2.equals(version))
        {
            fontAtt = fontAttForCloud;
        }
        Assert.assertTrue(textEditor.getContent().contains(fontAtt +commentText+"</font>"));
        textEditor.clickUndo();        
        Assert.assertTrue(textEditor.getContent().contains("<p>"+commentText+"</p>"));
        textEditor.clickRedo();       
        if (AlfrescoVersion.Cloud2.equals(version))
        {
            fontAtt = fontAttForCloud;
        }
        Assert.assertTrue(textEditor.getContent().contains(fontAtt +commentText+"</font>"));
        textEditor.removeFormatting();
    }

 

    /**
     * Method renders the documentlibrary page and returns the folder as FileDirectoryInfo
     * 
     * @return FileDirectoryInfo element for folder / content at index 0
     * @throws Exception
     */
    private FileDirectoryInfo getFolder() throws Exception
    {
        if (logger.isTraceEnabled())
            logger.trace("====getFolder====");
        documentLibPage = drone.getCurrentPage().render();
        List<FileDirectoryInfo> results = documentLibPage.getFiles();
        if (results.isEmpty())
        {
            throw new Exception("Error getting folder");
        }
        else
        {
            // Get folder
            return results.get(0);
        }
    }

}
