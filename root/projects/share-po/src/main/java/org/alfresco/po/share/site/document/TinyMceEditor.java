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

import java.util.NoSuchElementException;

import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author nshah
 */
public class TinyMceEditor extends HtmlElement
{
    private Log logger = LogFactory.getLog(TinyMceEditor.class);

    private static final String TINY_MCE_SELECT_ALL_COMMAND = "tinyMCE.activeEditor.selection.select(tinyMCE.activeEditor.getBody(),true);";
    private static final String CSS_COLOUR_FONT = "#tinymce>p>font";
    private static final String CSS_REMOVE_FORMAT = ".mceIcon.mce_removeformat";
    private static final String CSS_COLOR_ATT = "rich.txt.editor.color.code";
    private static final String CSS_STR_BOLD = ".mceIcon.mce_bold";
    public static final String FRAME_ID = "template_x002e_comments_x002e_folder-details_x0023_default-add-content_ifr";
    public static final String TINYMCE_CONTENT = "body[id$='tinymce']";
    
   
    private static final String CSS_STR_ITALIC = ".mceIcon.mce_italic";
    private static final String CSS_STR_UNDER_LINED = ".mceIcon.mce_underline";
    private static final String CSS_STR_BULLETS = ".mceIcon.mce_bullist";
    private static final String CSS_STR_NUMBERS = ".mceIcon.mce_numlist";
    private static final String CSS_STR_BOLD_FMT_TXT = "#tinymce>p>b";
    private static final String CSS_STR_ITALIC_FMT_TXT = "#tinymce>p>i";
    private static final String CSS_STR_UNDER_LINED_FMT_TXT = "#tinymce>p>u";
    private static final String CSS_STR_BULLET_FMT_TXT = "#tinymce>ul>li";
    private static final String CSS_STR_NUMBER_FMT_TXT = "#tinymce>ol>li";
    private static final String CSS_STR_TEXT_TAG = "#tinymce>p";
    private static final String CSS_STR_FORE_COLOUR = "#template_x002e_comments_x002e_folder-details_x0023_default-add-content_forecolor_open";
    private static final String CSS_BLUE_COLOUR_CODE = "a[title=Blue]";
    private static final String CSS_BLACK_COLOUR_CODE = "div.mce_forecolor td>a[title='Black']";
    private static final String CSS_COLOR_FONT = "#tinymce>p>font";
    private static final String CSS_UNDO = ".mceIcon.mce_undo";
    private static final String CSS_REDO = ".mceIcon.mce_redo";    
    private static final String CSS_BULLET_TEXT = "#tinymce>ul>li";
    private String frameId;
    private FormatType formatType;    
   

    public enum FormatType
    {
        BOLD,
        ITALIC,
        UNDERLINED,
        NUMBER,
        BULLET,
        BOLD_FMT_TXT, 
        ITALIC_FMT_TXT,
        UNDER_LINED_FMT_TXT,
        BULLET_FMT_TXT,
        NUMBER_FMT_TXT,
        COLOR,
        COLOR_CODE,
        BLACK_COLOR_CODE,
        UNDO,
        REDO, 
        DEFAULT,
        COLOR_FONT,
        BULLET_TEXT;
    }
    
    public String getFrameId()
    {
        return frameId;
    }
    public void setFrameId(String frameId)
    {
        this.frameId = frameId;
    }
    
    public void setFormatType(FormatType formatType)
    {
        this.formatType = formatType;
    }
    
    public String getCSSOfFormatType()
    {        
        switch (formatType)
        {
            case BOLD:
                return CSS_STR_BOLD;               
            case ITALIC:
                return CSS_STR_ITALIC;                
            case UNDERLINED:
                return CSS_STR_UNDER_LINED;                
            case BULLET:
                return CSS_STR_BULLETS;               
            case NUMBER:
                return CSS_STR_NUMBERS; 
            case COLOR:
                return CSS_STR_FORE_COLOUR;
            case COLOR_CODE:
                return CSS_BLUE_COLOUR_CODE;
            case BLACK_COLOR_CODE:
                return CSS_BLACK_COLOUR_CODE;
            case UNDO:
                return CSS_UNDO;
            case REDO:
                return CSS_REDO;
            default:
                throw new PageException();
             
        }
    }
    
    public String getCSSOfText(FormatType formatType)
    {        
        switch (formatType)
        {
            case BOLD_FMT_TXT:
                return CSS_STR_BOLD_FMT_TXT;
            case ITALIC_FMT_TXT:
                return CSS_STR_ITALIC_FMT_TXT;
            case UNDER_LINED_FMT_TXT:
                return CSS_STR_UNDER_LINED_FMT_TXT;
            case BULLET_FMT_TXT:
                return CSS_STR_BULLET_FMT_TXT;
            case NUMBER_FMT_TXT:
                return CSS_STR_NUMBER_FMT_TXT;
            case COLOR_FONT:
                return CSS_COLOR_FONT;
            case BULLET_TEXT:
                return CSS_BULLET_TEXT;
            default:
                return CSS_STR_TEXT_TAG;
        }
    }


    public void setTinyMce(String frameId)
    {        
        setFrameId(frameId);        
    }
    /**
     * Constructor
     */
    public TinyMceEditor(WebDrone drone)
    {
        super(drone);      
    }

    /**
     * @param txt
     */
    public void addContent(String txt)
    {
        try
        {
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", txt);
            drone.executeJavaScript(setCommentJs);
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            logger.error("Element : " + txt + " is not present");
        }
    }
    
    /**
     * This method sets the given text into Site Content Configure text editor.
     * 
     * @param text
     */
   
    public void setText(String text)
    {   
        if(text == null)
        {
            throw new IllegalArgumentException("Text is required");
        }
        
        try
        {   
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", "");
            drone.executeJavaScript(setCommentJs);
            setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", text);
            drone.executeJavaScript(setCommentJs);
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            throw new PageException("Unable to find text css in tinyMCE editor." , noSuchElementExp);
        }
    }
    

    /**
     * Click on TinyMCE editor's format option.    
     */
    public void clickTextFormatter(FormatType formatType)
    {     
     
        setFormatType(formatType);
        selectTextFromEditor();
        clickElementOnRichTextFormatter(getCSSOfFormatType());      
    }
 
    /**
     * Click to select color code on text.
     */
    public void clickColorCode()
    {        
        selectTextFromEditor();
        setFormatType(FormatType.COLOR);
        clickElementOnRichTextFormatter(getCSSOfFormatType());
        setFormatType(FormatType.COLOR_CODE);
        clickElementOnRichTextFormatter(getCSSOfFormatType());       
    }
    /**
     * click to undo to default format.
     */
    public void clickUndo()
    {
        setFormatType(FormatType.UNDO);
        clickElementOnRichTextFormatter(getCSSOfFormatType());           
    }

    /**
     * Click to Redo the undo operation.
     */
    public void clickRedo()
    {
        setFormatType(FormatType.REDO);
        clickElementOnRichTextFormatter(getCSSOfFormatType());
    }

 
    /**
     * @param cssString
     * @return
     */
    public String getColourAttribute()
    {
        try
        {
            drone.switchToFrame(getFrameId());
            WebElement element = drone.findAndWait(By.cssSelector(CSS_COLOUR_FONT));
            if (!CSS_COLOR_ATT.equals(element.getAttribute("color")) || CSS_COLOR_ATT.equals(element.getAttribute("style")))
            {
                drone.switchToDefaultContent();
                return "BLUE";
            }

        }
        catch (NoSuchElementException noSuchElementExp)
        {
            logger.error("Element :" + CSS_COLOUR_FONT + " does not exist");
        }
        return "";
    }

    /**
     * Click to remove formatting from text. 
     */
    public void removeFormatting()
    {
        try
        {
            drone.findAndWait(By.cssSelector(CSS_REMOVE_FORMAT)).click();
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            logger.error("Element :" + CSS_REMOVE_FORMAT + " does not exist");
        }
    }

    /**
     * @param cssString
     */
    public void selectTextFromEditor()
    {
        //This select all in the edit pane
        /**
         * @author Michael Suzuki
         * Changed to use tinymce directly as its 
         * faster to edit with tinymce object instead
         * of using the ui.
         * 
         * The script below will select every thing
         * inside the editing pane. 
         */
        drone.executeJavaScript(TINY_MCE_SELECT_ALL_COMMAND);
    }

    /**
     * 
     * @param cssString
     */
    protected void clickElementOnRichTextFormatter(String cssString)
    {
        try
        {
            drone.switchToDefaultContent();
            drone.findAndWait(By.cssSelector(cssString)).click();
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            logger.error("Element :" + cssString + " does not exist");
        }
    }

    /**
     * Get text from TinyMCE editor.
     * @param cssString
     * @return
     */
    public String getText()
    {      
        try
        {          
            drone.switchToFrame(getFrameId());
            String text = drone.find(By.cssSelector(TINYMCE_CONTENT)).getText();
            drone.switchToDefaultContent();
            return text;
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            logger.error("Element : does not exist");
            throw new PageException("Unable to find text in tinyMCE editor." , noSuchElementExp);
        }
    }

    /**
     * Get HTML source for from TinyMCE editor.
     * @param cssString
     * @return
     */
    public String getContent()
    {       
        try
        {          
            drone.switchToFrame(getFrameId());           
            WebElement element = drone.findAndWait(By.cssSelector(TINYMCE_CONTENT));
            String contents = (String)drone.executeJavaScript("return arguments[0].innerHTML;", element);           
            drone.switchToDefaultContent();           
            return contents;
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            logger.error("Element :body[id$='tinymce'] does not exist");
            throw new PageException("Unable to find content in tinyMCE editor.", noSuchElementExp);
        }
    }
}
