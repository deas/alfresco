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
package org.alfresco.po.share.site.wiki;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Site wiki main page object, holds all element of the HTML page relating to
 * share's site wiki page.
 * 
 * @author Michael Suzuki
 * @since 1.2
 */
public class WikiPage extends SitePage
{

    private Log logger = LogFactory.getLog(this.getClass());

    private static final String WIKI_FORMAT_IFRAME = "template_x002e_createform_x002e_wiki-create_x0023_default-content_ifr";
    private static final String WIKI_EDIT_IFRAME = "template_x002e_wikipage_x002e_wiki-page_x0023_default-content_ifr";
    private static final By CANCEL_BUTTON = By.cssSelector("a[id$='default-cancel-button-button']");
    private static final By DEFAULT_CONTENT_TOOLBAR = By.cssSelector("div[id$='default-content_toolbargroup']>span");
    private static final By BUTTON_CREATE = By.cssSelector("button[id$='default-create-button-button']");
    private static final By CREATE_WIKI_TITLE = By.cssSelector("input[id$='createform_x002e_wiki-create_x0023_default-title']");   
    private static final By FONT_STYLE_SELECT = By.cssSelector("a[id$='default-content_fontselect_open']");
    private static final By FONT_SIZE_SELECT = By.cssSelector("a[id$='default-content_fontsizeselect_open']");
    private static final By IMAGE_LIB = By.cssSelector(".mceIcon.mce_alfresco-imagelibrary");
    private static final By IMAGE_RSLT = By.cssSelector("#image_results");
    private static final By BUTTON_SAVE = By.cssSelector("button[id$='default-save-button-button']");
    private static final By REMOVE_FORMAT = By.cssSelector(".mceIcon.mce_removeformat");
    private static final By DELETE_WIKI = By.cssSelector("button[id$='default-delete-button-button']");
    private static final By EDIT_WIKI =By.cssSelector("a[href*='action=edit']");

    private TinyMceEditor tinyMCEEditor = new TinyMceEditor(drone);

    public enum ImageType
    {
        JPG,
        PNG,
        BMP;
    }
    
    public enum Mode
    {
        ADD,
        EDIT;        
    }
    
    public enum FONT_ATTR
    {
        face,
        size;        
    }
    
    public WikiPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiPage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(BUTTON_CREATE));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Check if wiki page is displayed or not.
     * 
     * @return
     */
    public boolean isWikiPageDisplayed()
    {
        try
        {
            return drone.findAndWait(DEFAULT_CONTENT_TOOLBAR).isDisplayed();
        }
        catch (TimeoutException toe)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Time out finding " + CANCEL_BUTTON.toString(), toe);
            }
        }
        catch (ElementNotVisibleException visibleException)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Element Not Visible: " + CANCEL_BUTTON.toString(), visibleException);
            }
        }
        return false;
    }

    /**
     * Check content tool bar is displayed.
     * 
     * @return
     */
    public boolean isTinyMCEDisplayed()
    {
        try
        {
            return drone.findAndWait(DEFAULT_CONTENT_TOOLBAR).isDisplayed();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + DEFAULT_CONTENT_TOOLBAR.toString(), toe);
        }
        throw new PageException("Page is not rendered");
    }

    /**
     * click on new wiki page.
     */
    public void clickOnNewPage()
    {
        try
        {
            drone.findAndWait(BUTTON_CREATE).click();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + BUTTON_CREATE.toString(), toe);
        }

    }

    /**
     * Create wiki page title.
     * 
     * @param wikiTitle
     */
    public void createWikiPageTitle(String wikiTitle)
    {
        try
        {
            drone.findAndWait(CREATE_WIKI_TITLE).sendKeys(wikiTitle);
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + CREATE_WIKI_TITLE.toString(), toe);
        }
    }

    /**
     * Insert text in wiki text area.
     * 
     * @param txtLines
     */
    public void insertText(List<String> txtLines)
    {
        try
        {
            drone.executeJavaScript(String.format("tinyMCE.activeEditor.setContent('%s');", txtLines.get(0)));
            drone.switchToFrame(WIKI_FORMAT_IFRAME);
            WebElement element = drone.findAndWait(By.cssSelector("#tinymce"));
            if (!element.getText().isEmpty())
            {
                element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            }
            drone.switchToDefaultContent();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding #tinymce", toe);
        }
    }

 /*   *//**
     * Click bullet list button on wiki text formatter.
     *//*
    public void clickBulletList()
    {
        try
        {
            drone.findAndWait(BULLET_LIST).click();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + BULLET_LIST.toString());
        }

    }
*/
    /**
     * Click Font Style button on wiki text formatter.
     */
    public void clickFontStyle()
    {
        try
        {
            drone.findAndWait(FONT_STYLE_SELECT).click();
            drone.findAndWait(By.cssSelector("#mce_22")).click();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + FONT_STYLE_SELECT.toString() + " OR #mce_22");
        }
    }

    /**
     * Click on Font size button on wiki text formatter.
     */
    public void clickFontSize()
    {
        try
        {
            drone.findAndWait(FONT_SIZE_SELECT).click();
            List<WebElement> elements = drone.findAll(By.cssSelector(".mceText"));
            for (WebElement webElement : elements)
            {
                if ("font-size: 12pt;".equals(webElement.getAttribute("style")))
                {
                    webElement.click();
                    break;
                }
            }
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + FONT_SIZE_SELECT.toString() + " OR #mce_22", toe);
        }
    }



    /**
     * Retrieve formatted wiki text. 
     * @param type
     * @return
     */
    public String retrieveWikiText(String type)
    {
        try
        {
            drone.switchToFrame(WIKI_FORMAT_IFRAME);
            String richText = drone.findAndWait(getCSSToRetrieveText(type)).getText();
            drone.switchToDefaultContent();
            return richText;
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + type, toe);
        }
        throw new PageException("Wiki Page has no such element");
    }

    /**
     * Check for image library is displayed.
     * @return
     */
    public boolean isImageLibraryDisplayed()
    {
        try
        {
            drone.findAndWait(IMAGE_LIB).click();
            return drone.findAndWait(IMAGE_RSLT).isDisplayed();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + IMAGE_LIB+" or "+IMAGE_RSLT, toe);
        }
        return false;
    }

    /**
     * click on save button to save wiki text.
     */
    public WikiPage clickSaveButton()
    {
        try
        {
            drone.waitUntilElementClickable(BUTTON_SAVE, TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
            WebElement saveButton = drone.findAndWait(BUTTON_SAVE);
            if (saveButton.isEnabled())
            {
                saveButton.click();
                drone.waitUntilElementDeletedFromDom(DEFAULT_CONTENT_TOOLBAR, maxPageLoadingTime);
                return new WikiPage(drone);
            }
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + BUTTON_SAVE, toe);
        }
        throw new PageException("Not able find the Save Button");
    }

    /**
     * Click on remove format button. 
     */
    public void clickOnRemoveFormatting()
    {
        try
        {
            drone.findAndWait(REMOVE_FORMAT).click();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + REMOVE_FORMAT, toe);
        }
    }
    
    /**
     * Click to view images library.
     */
    public void clickImageOfLibrary()
    {
        try
        {
            drone.findAndWaitForElements(By.cssSelector("#image_results>img")).get(0).click();
        }
        catch(TimeoutException toe)
        {
            logger.error("Time out finding image", toe);
        }
    }
    
    /**
     * Check if image is rendered in wiki text area.
     * @return
     */
    public int imageCount(Mode mode)
    {        
        String frameId;
       try
       {
            switch(mode)
            {
            case EDIT:
                   frameId= WIKI_EDIT_IFRAME;
                   break;
            default:
                   frameId =WIKI_FORMAT_IFRAME;
            }
            drone.switchToFrame(frameId);
            int totalImage =  drone.findAndWaitForElements(By.cssSelector("#tinymce>p>img")).size();
            drone.switchToDefaultContent();                   
            return totalImage;
             
        }
        catch(TimeoutException toe)
        {
            logger.error("Time out rendering image", toe);
        }
        throw new PageException("Image is not rendered");
    }

    /**
     * Delete wiki page created.
     */
    public void deleteWiki(){
        
        try
        {
            By popupDeleteButton = By.cssSelector(drone.getElement("delete.wiki.popup"));
            drone.findAndWait(DELETE_WIKI).click();   
            drone.findAndWait(popupDeleteButton).click(); 
        }
        catch(TimeoutException toe)
        {
            logger.error("Unable to find delete wiki button", toe);
        }
    }
    
    
    /**
     * 
     * @param type
     * @return
     */
    private By getCSSToRetrieveText(String type)
    {

        if ("BULLET".equals(type))
        {
            return By.cssSelector("#tinymce>ul>li");
        }
        else if ("NUMBER".equals(type))
        {
            return By.cssSelector("#tinymce>ol>li");
        }
        else if ("FONT".equals(type))
        {
            String selector = AlfrescoVersion.Enterprise41 == alfrescoVersion ? "#tinymce>ul>li>font" : "#tinymce>ul>li>span>font";
            return By.cssSelector(selector);
        }
        else if("IMG".equals(type))
        {
            return By.cssSelector("#tinymce>p>img");
        }
        else
        {
            return By.cssSelector("#tinymce");
        }
    }
    
    
    /**
     * Get TinyMCEEditor object to navigate TinyMCE functions.
     * 
     * @return
     */
    public TinyMceEditor getTinyMCEEditor()
    {       
        tinyMCEEditor.setTinyMce(WIKI_FORMAT_IFRAME);
        return tinyMCEEditor;
    }
    
    /**
     * Copy Image using CTRL+C
     */
    public void copyImageFromLib()
    {
        try
        {
            drone.switchToDefaultContent(); 
            drone.switchToFrame(WIKI_FORMAT_IFRAME);
            WebElement element = drone.findAndWait(By.cssSelector("#tinymce>p>img"));            
            element.sendKeys(Keys.chord(Keys.CONTROL, "a")); 
            element.sendKeys(Keys.chord(Keys.CONTROL, "c"));            
            drone.switchToDefaultContent();
        }
        catch(TimeoutException toe)
        {
            logger.error("Time out finding image", toe);
        }
    }
    
    /**
     * Pasting image using CTRL+V
     */
    public void pasteImageOnEditor()
    {
        try
        {
            drone.switchToDefaultContent(); 
            drone.switchToFrame(WIKI_FORMAT_IFRAME);
            WebElement element = drone.findAndWait(By.cssSelector("#tinymce"));      
            element.sendKeys(Keys.chord(Keys.CONTROL, "v"));       
            element.sendKeys(Keys.chord(Keys.CONTROL, "v"));      
            drone.switchToDefaultContent();        
        }
        catch(TimeoutException toe)
        {
            logger.error("Time out finding image", toe);
        }
    }
    
    /**
     * Edit Wiki Page.
     * @return
     */
    public WikiPage editWikiPage()
    {
        try
        {            
            drone.findAndWait(EDIT_WIKI).click();
            drone.waitUntilElementClickable(DEFAULT_CONTENT_TOOLBAR, maxPageLoadingTime);
            return new WikiPage(drone);
        }
        catch(TimeoutException toe)
        {
            logger.error("Time out finding image", toe);
        }
        throw new PageException();
     }
    
    
    /**
     * @param type
     * @return
     */
    public String verifyEditText(String type)
    {
        try
        {            
            drone.switchToFrame(WIKI_EDIT_IFRAME);
            String richText = drone.findAndWait(getCSSToRetrieveText(type)).getText();
            drone.switchToDefaultContent();
            return richText;  
        }
        catch(TimeoutException toe)
        {
            logger.error("Time out finding image", toe);
        }
        throw new PageException();
    }
        
    
    /**
     * @param type
     * @return
     */
    public String getFontAttributeValue(FONT_ATTR type)
    {
        try
        {            
            drone.switchToFrame(WIKI_EDIT_IFRAME);
            String attrValue = drone.findAndWait(By.cssSelector("#tinymce>ul>li>font")).getAttribute(type.name());

            drone.switchToDefaultContent();
            return attrValue;  
        }
        catch(TimeoutException toe)
        {
            logger.error("Time out finding attribute of font element", toe);
        }
        throw new PageException("Font element not found!");
    }
}
