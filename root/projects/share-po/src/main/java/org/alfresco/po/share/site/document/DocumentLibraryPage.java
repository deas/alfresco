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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.Pagination;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Site document library page object, holds all element of the HTML page
 * relating to share's site document library page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class DocumentLibraryPage extends SitePage
{
    private static final String JS_SCRIPT_CHECK_DOCLIST = "return Alfresco.util.ComponentManager.findFirst('Alfresco.DocumentList').widgets.dataTable._getViewRecords();";
    private static Log logger = LogFactory.getLog(DocumentLibraryPage.class);
    private static final String SITE_FILE_UPLOAD_BUTTON = "site.document.library.nav.upload.id";
    private static final String SITE_CREATE_NEW_FOLDER_BUTTON = "site.document.library.nav.create.folder.id";
    private static final String PAGINATION_BUTTON_NEXT = "a.yui-pg-next";
    private static final String PAGINATION_BUTTON_PREVIOUS = "a.yui-pg-previous";
    public static final String FILES_AND_DOCUMENTS_TABLE_CSS = "table#yuievtautoid-0 > tbody.yui-dt-data > tr";
    private static final String ALL_TAGS_PRESENT_ON_DOC_LIB = "div.filter>ul>li>span.tag>a.tag-link";
    private static final String NODEREF_LOCATOR = "input[id^='checkbox-yui']";
    private static final String FILE_UPLOAD_INSTRUCTION = "div.docListInstructionsWithDND";
    private static final String BOTTOM_PAGINATOR_LOCATION = "div[id$='_default-paginatorBottom']";
    private static final By THUMBNAIL_IMAGE = By.cssSelector("td[class$='yui-dt-col-thumbnail'] img");
    private final String subfolderName;
    private boolean shouldHaveFiles;
    private final String hasTags;
    private String contentName;
    public enum Optype
    {
        REQ_TO_SYNC,  
        SYNC,
        UNSYNC;
    }

    /**
     * Constructor.
     */
    public DocumentLibraryPage(WebDrone drone, final String subfolderName)
    {
        super(drone);
        this.subfolderName = subfolderName;
        this.hasTags = "";
        this.contentName = null;
    }
    
    public DocumentLibraryPage(WebDrone drone)
    {
        super(drone);
        subfolderName = null;
        this.hasTags = "";
        this.contentName = null;
    }

    public DocumentLibraryPage(WebDrone drone, final String contentName, final String hasTag)
    {
        super(drone);
        subfolderName = null;
        this.shouldHaveFiles = true;
        this.contentName = contentName;
        this.hasTags = hasTag;
    }
    /**
     * Getter method  
     * 
     */
    public String getContentName()
    {
        return contentName;
    }

    public void setContentName(String contentName)
    {
        this.contentName = contentName;
    }

    /**
     * Check if javascript message is displayed.
     * The message details are loading document library message. 
     * @return if message displayed
     */
    private boolean isDocumentLibLoading()
    {
        //Check url to see if the document lib page is present.
        return (isJSMessageDisplayed() || pageLoadingMessageDisplayed());
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized DocumentLibraryPage render(RenderTime timer)
    {
        while(true)
        {
            try
            {
                timer.start();
                synchronized (this)
                {
                    try{ this.wait(100L); } catch (InterruptedException e) {}
                }
                
                //Case 1: When we have no files we check upload instruction appears and we are not expecting files
                if(isFileUploadInstructionDisplayed() && !shouldHaveFiles)
                {
                    if(paginatorRendered())
                    {
                        boolean hasNoFiles = !hasFiles();
                        if(hasNoFiles)
                        {
                            if(logger.isTraceEnabled())
                            {
                                logger.trace("upload message appears and has no files is: " + hasNoFiles);
                            }
                            break;
                        }
                    }
                }
                //Case 2: When we have some files or expecting to see a file. 
                //Check if loading document library message is gone
                if(!isDocumentLibLoading())
                {
                    //Check if were in correct folder
                    if(subfolderName != null)
                    {
                        if(isSubFolderDocLib(subfolderName))
                        {
                            //Give time to reload the document library page
                            synchronized (this)
                            {
                                try{ this.wait(100L); } catch (InterruptedException e) {}
                            }
                            if(dataRendered()) break;
                        }
                    } 
                    else
                    {
                        if(dataRendered()) break;
                    }
                } 
            }
            catch (StaleElementReferenceException ste)
            {
                //DOM has changed therefore page should render once change is completed
            }
            finally
            {
                timer.end();
            }
        }
        return this;
    }
    /**
     * Checks to see if document list bottom 
     * paginator is displayed
     * @return true if displayed
     */
    public boolean paginatorRendered()
    {
        try
        {
            return drone.find(By.cssSelector(BOTTOM_PAGINATOR_LOCATION)).isDisplayed();
        }
        catch (NoSuchElementException e) { }
        return false;
    }

    private boolean pageLoadingMessageDisplayed()
    {
        try
        {
           return(drone.find(By.cssSelector("table>tbody>tr>td.yui-dt-empty>div")).isDisplayed());
           
        }
        catch (NoSuchElementException e) { }
       return false;
    }
    
    /**
     * Render logic to check if files or folders
     * are present on the document library page or 
     * if empty file folder help message is displayed.
     * @return true if page has rendered
     */
    private boolean dataRendered()
    {
        //First check bottom pagination is rendered
        if(paginatorRendered())
        {
            //If result is expected
            if (shouldHaveFiles)
            {
                if(logger.isTraceEnabled())
                {
                    logger.trace("dataRendered check with shouldHaveFiles");
                }
                return hasData();
            }
            //otherwise let it be true
            return true;
        }
        return false;
    }
    /**
     * Checks if file upload message appears, this indicates
     * there are no files or folders in the document library page
     * @return true if message is displayed
     */
    private boolean hasNoData()
    {
        try
        {                                                              
            WebElement td = drone.find(By.cssSelector("tbody.yui-dt-message tr td"));
            boolean visible = td.isDisplayed();
            return visible;
        }
        catch (NoSuchElementException e) {}
        return false;

    }
    /**
     * Check to see if document library contains files or folders with tags.
     * @return true if files or folders are displayed
     */
    private boolean hasData()
    {
        // Look for results
        try
        {
            if(hasNoData())
            {
                return false;
            }
            boolean hasContentRows = hasFiles();
            if(hasContentRows)
            {
                if(hasTags != null)
                {
                    if(!(hasTags.isEmpty()))
                    {
                        hasContentRows = hasTag();
                    }
                }
            }
            return hasContentRows;

        }
        catch (NoSuchElementException e) { }
        return false; 
    }

    /**
     * Check to see if document library contains files or folders.
     * @return true if files or folders are displayed
     */
    private boolean hasTag()
    {
        // Look for Tags
        try
        {
            return getContentRow(contentName).getTags().contains(hasTags.toLowerCase());
        }
        catch (NoSuchElementException e) { }
        return false; 
    }

    /**
     * Checks to verify if we are in the correct sub folder document library page.
     * @return true if bread crumb match location of the sub folder name.
     */
    public synchronized boolean isSubFolderDocLib(final String name)
    {
        //If we are expected to be in sub folder assert by checking the bread crumb
        try
        {
            List<WebElement> list = drone.findAll(By.cssSelector("div[id$='default-breadcrumb']>div.crumb>span.label>a"));
            WebElement element = list.get(list.size()-1);
            String folderName = element.getText();
            if(name.equalsIgnoreCase(folderName))
            {
                return true;
            }
        }
        catch (Exception e) { }

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentLibraryPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public DocumentLibraryPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if the page viewed is the document library page.
     */
    public boolean isDocumentLibrary()
    {
        try
        {
            return drone.find(By.cssSelector("#alfresco-documentlibrary")).isDisplayed();
        }
        catch (NoSuchElementException e) { }
        return false;
    }
    /**
     * Get document library sub navigation.
     * 
     * @return {@link DocumentLibraryNavigation} object.
     */
    public DocumentLibraryNavigation getNavigation()
    {
        return new DocumentLibraryNavigation(drone, SITE_FILE_UPLOAD_BUTTON, SITE_CREATE_NEW_FOLDER_BUTTON);
    }
    
    /**
     * Check is instruction message is displayed.
     * If it is displayed then it is a good indication
     * that there are no files or folder on the page.
     * @return if displayed
     */
    public boolean isFileUploadInstructionDisplayed()
    {
        try
        {
            boolean displayed = drone.find(By.cssSelector(FILE_UPLOAD_INSTRUCTION)).isDisplayed();
            if(logger.isTraceEnabled())
            {
                logger.trace(String.format("** File upload instruction is displayed: %s", displayed)); 
            }
            return displayed;
        }
        catch (NoSuchElementException e) { }
        return false;
    }

    /**
     * Checks document list is populated by injecting 
     * a javascript in to an alfresco component that
     * renders the document list.
     * @return true if collection of documents exists
     */
    public boolean hasFiles()
    {
        try
        {
            ArrayList<?> objs = (ArrayList<?>) drone.executeJavaScript(JS_SCRIPT_CHECK_DOCLIST);
            if(!objs.isEmpty())
            {
                return true;
            }
        }
        catch (Exception e) { }
        return false;
    }
    
    /**
     * Extracts the results from result table that matches the file name.
     * 
     * @return Collection of {@link FileDirectoryInfo} relating to result
     */
    public List<FileDirectoryInfo> getFiles()
    {
        try
        {
            boolean noFiles = !hasFiles();
            if(logger.isTraceEnabled()) logger.trace(String.format("Documet list has no files: %s", noFiles));
            
            if(noFiles)
            {
                return Collections.emptyList();
            }
            
            List<WebElement> results = drone.findAll(By.cssSelector(NODEREF_LOCATOR));
            if(logger.isTraceEnabled())
            {
                logger.trace(String.format("getFiles list is empty: %s file size %d",
                        results.isEmpty(),
                        results.size()));
            }
            
            if (!results.isEmpty())
            {
                List<FileDirectoryInfo> fileDirectoryList = new ArrayList<FileDirectoryInfo>();
                for (WebElement result : results)
                {
                    FileDirectoryInfo file = new FileDirectoryInfo(result.getAttribute("value"), result, drone);
                    if(logger.isTraceEnabled())
                    {
                        logger.trace("adding file" + file.getName());
                    }
                	fileDirectoryList.add(file);
                }
                return fileDirectoryList;
            }
            //Try again as we are expecting results.
            return getFiles();
        }
        catch (NoSuchElementException e) { }
        catch (StaleElementReferenceException e)
        {
            if(logger.isTraceEnabled())
            {
                logger.debug("found stale element retrying get files");
            }
        }
        //Try again as we should have results else upload instructions view would be piked up.
        return getFiles();
    }
    
    /**
     * Selects the title of the document link.
     * 
     * @param title String file title
     * @return DocumentDetailsPage page response object
     */
    public DocumentDetailsPage selectFile(final String title)
    {
        selectEntry(title).click();
        return new DocumentDetailsPage(drone);
    }

    /**
     * Selects the title of the folder link.
     * 
     * @param title String folder title
     * @return DocumentLibraryPage page response object
     */
    public DocumentLibraryPage selectFolder(final String title)
    {
        selectEntry(title).click();
        return new DocumentLibraryPage(drone, title);
    }

    /**
     * Selects an entry regardless of type (file or folder)
     * @return 
     */
    protected WebElement selectEntry(final String title)
    {
        if(title == null || title.isEmpty()) throw new IllegalArgumentException("Title is required");
        String search = String.format("//h3/span/a[text()='%s']",title);
        return drone.find(By.xpath(search));
    }

    /**
     * Selects file or folder from page.
     * @param name identifier
     * @return true if selected
     */
    private synchronized FileDirectoryInfo findFileOrFolder(final String name)
    {
        if(name == null || name.isEmpty()) throw new IllegalArgumentException("Name is required");
        if(logger.isTraceEnabled())
        {
            logger.trace("Look in collection for: " + name);
        }
        List<FileDirectoryInfo> files = getFiles();
        for (FileDirectoryInfo file : files)
        {
            String fileName = file.getName();
            if(logger.isTraceEnabled())
            {
                logger.trace(String.format("matching %s against %s",name, fileName));
            }
            if (name.equalsIgnoreCase(fileName))
            {
                return file;
            }
        }
        if(logger.isTraceEnabled())
        {
            logger.trace(String.format("content: %s not found", name));
        }
        throw new PageOperationException("Unable to locate fileName: " + name);
    }
    /**
     * Checks to see if file is visible on the page.
     * 
     * @param fileName String title
     * @return true if file exists on the page
     */
    public synchronized boolean isFileVisible(final String fileName)
    { 
        if(fileName == null || fileName.isEmpty()) 
        {
            throw new UnsupportedOperationException("File name required");
        }
        try
        {
            for (FileDirectoryInfo file : getFiles())
            {
                if(fileName.equalsIgnoreCase(file.getName()))
                {
                    return true;
                }
            }
        }
        catch (PageOperationException e){ }
        return false;
    }
    
    /**
     * Checks to see if files is on the page.
     * 
     * @return true if file exists on the page
     */
    public boolean isFilesVisible()
    {
    	try
    	{
    		WebElement content = drone.find(By.cssSelector("div[id$='default-documents']"));
    		return content.isDisplayed();
    	}
    	catch (NoSuchElementException nse)
    	{
    		return false;
		}
    }
    /**
     * The number of comments value displayed
     * on the span comment count.
     * @return {@link Integer} total number of comments
     */
    public Integer getCommentCount()
    {
    	try
    	{
    		WebElement span = drone.find(By.cssSelector("span.comment-count"));
    		return Integer.valueOf(span.getText());
    	}
    	catch (NoSuchElementException nse)
    	{
    		return 0;
		}
    }

    /**
     * Checks if pagination next button is active.
     * @return true if next page exists
     */
    public boolean hasNextPage()
    {
        return Pagination.hasPaginationButton(drone, PAGINATION_BUTTON_NEXT);
    }

    /**
     * Checks if pagination previous button is active.
     * @return true if next page exists
     */
    public boolean hasPreviousPage()
    {
        return Pagination.hasPaginationButton(drone, PAGINATION_BUTTON_PREVIOUS);
    }

    /**
     * Selects the button next on the pagination bar.
     */
    public void selectNextPage()
    {
        Pagination.selectPagiantionButton(drone, PAGINATION_BUTTON_NEXT);
    }
    
    /**
     * Selects the button previous on the pagination bar.
     */
    public void selectPreviousPage()
    {
        Pagination.selectPagiantionButton(drone, PAGINATION_BUTTON_PREVIOUS);
    }


    /**
     * Locates the file or folder and deletes it.
     * 
     * @param name
     *            String identifier
     * @return page response
     */
    public HtmlPage deleteItem(final String name)
    {
        if (name == null) throw new IllegalArgumentException("require name value");
        FileDirectoryInfo item = getFileDirectoryInfo(name);
        item.selectDelete();
        confirmDelete();
        return new DocumentLibraryPage(drone);
    }
    
    /**
     * Locates the file or folder and deletes it.
     * 
     * @param name
     *            String identifier
     * @return page response
     */
    public HtmlPage deleteItem(final int number)
    {
        FileDirectoryInfo item = getFileDirectoryInfo(number);
        item.selectDelete();
        confirmDelete();
        return new DocumentLibraryPage(drone);
    }
    
    /**
     * Action of selecting ok on confirm delete pop up dialog.
     */
    private void confirmDelete()
    {
        WebElement confirmDelete = drone.find(By.cssSelector("div#prompt div.ft span span button"));
        confirmDelete.click();
        if (logger.isTraceEnabled())
        {
            logger.trace("deleting");
        }
    }

    /**
     * Returns the ShareContentRow for the selected contentName.
     * @param WebDrone drone
     * @param name String content name identifier
     * @return {@link ShareContentRow}
     * @deprecated use getFileDirectoryInfo
     */
    public synchronized FileDirectoryInfo getContentRow(final String name)
    {
        if(name == null || name.isEmpty()) throw new UnsupportedOperationException("Name input value is required");
        return findFileOrFolder(name);
    }
    
    /**
     * This method does the clicking on given tag name, which presents under the Tags Tree menu on Document Library page.
     * 
     * @param tagName
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(String tagName)
    {
        if(tagName == null)
        {
            throw new UnsupportedOperationException("TagName is required.");
        }

        String text = null;
        List<WebElement> tags = getAllTags(); 
        
        for(WebElement tag : tags)
        {
            text = tag.getText();
            
            if(text != null && text.equalsIgnoreCase(tagName))
            {
                tag.click();
                return new DocumentLibraryPage(drone);
            }
        }
        
        throw new PageException("Not able to find the given tag : " + tagName);
        
    }
    
    /**
     * This method gets the all tag elements present on document library Tags tree menu.
     * 
     * @return List<WebElement> 
     */
    private List<WebElement> getAllTags()
    {
        try
        {
            return drone.findAndWaitForElements(By.cssSelector(ALL_TAGS_PRESENT_ON_DOC_LIB));
        }
        catch(TimeoutException e)
        {
            logger.error("Exceeded the time to find the All Tags css." + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * This method gets the list of tag names present on document library Tags tree menu.
     * 
     * @return List<String>
     */
    public List<String> getAllTagNames()
    {
        List<String> tagNames = new ArrayList<String>();
        String text = null;
        
        List<WebElement> tags = getAllTags(); 
        
        for(WebElement tag : tags)
        {
            text = tag.getText();
            
            if(text != null)
            {
                tagNames.add(text);
            }
        }
         return tagNames;
    }

    /**
     * Select the option drop down, introduced in
     * Alfresco enterprise 4.2 and clicks on the button in 
     * the dropdown.
     * @param By selector location of button in dropdown to select
     */
    private void selectItemInOptionsDropDown(By button)
    {
        RenderTime timer = new RenderTime(WAIT_TIME_3000);
        while(true)
        {
            timer.start();
            try
            {
                WebElement btn = drone.find(By.cssSelector("button[id$='default-options-button-button']"));
                HtmlElement dropdownButton = new HtmlElement(btn, drone);
                dropdownButton.click();
                
                WebElement dropdown = drone.findAndWait(By.cssSelector("div[id$='default-options-menu']"));
                if(dropdown.isDisplayed())
                {
                    new HtmlElement(drone.find(button), drone).click();
                    break;
                }            
            }
            catch (StaleElementReferenceException stale) { }
            finally { timer.end(); }
        }
    }
    
    /**
     * Selects the Detailed View of the Document Library.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage selectDetailedView()
    {
        try
        {
            switch (alfrescoVersion) 
            {
                case Enterprise41:
                    drone.findAndWait(By.cssSelector("button[title='Detailed View']")).click();
                    break;
                case Cloud:
                    drone.findAndWait(By.cssSelector("button[id$='default-detailedView-button']")).click();
                    break;
                default:
                    selectItemInOptionsDropDown(By.cssSelector("span.view.detailed"));
                    break;
            }
            return new DocumentLibraryPage(drone);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
            throw new PageException("Exceeded the time to find css.");
        }
    }
    
    /**
     * Check the uploaded content has uploaded successfully
     * @param - String
     * @return - Boolean
     */
    public synchronized boolean isContentUploadedSucessful(String contentName)
    {
        //If we are expected content to be uploaded successful check whether it is uploaded
        try
        {
            DocumentLibraryPage docPage = FactorySharePage.resolvePage(drone).render();
            List<FileDirectoryInfo> results = docPage.getFiles();
            for (FileDirectoryInfo filenames : results)
            {
                if(filenames.getName().equalsIgnoreCase(contentName))
                {
                    return true;
                }
            }
        }
        catch (Exception e) { }

        return false;
    }

    /**
     * Select a particular file directory info row
     * based on the count, the accepted range is 1-50.
     * @param number Integer item row
     * @return {@link FileDirectoryInfo} page response
     */
    public FileDirectoryInfo getFileDirectoryInfo(final Integer number)
    {
        if(number == null || !((number > 0) && (number < 50)))
        {
            throw new IllegalArgumentException("A valid number range of 1 to 50 is required");
        }
        try
        {
            WebElement row = drone.find(By.cssSelector(String.format("tbody.yui-dt-data tr:nth-of-type(%d)", number)));
            String nodeRef = row.findElement(THUMBNAIL_IMAGE).getAttribute("id");
            return new FileDirectoryInfo(nodeRef, row, drone);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info row %d was not found",number), e);
        }
    }
    
    /**
     * Select a particular file directory info row based on the title.
     * @param title String item title
     * @return {@link FileDirectoryInfo} page response
     */
    public FileDirectoryInfo getFileDirectoryInfo(final String title)
    {
        if(title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }
        try
        {
            WebElement row = drone.find(By.xpath(String.format("//h3/span/a[text()='%s']/../../../../..",title)));
            String nodeRef = row.findElement(THUMBNAIL_IMAGE).getAttribute("id");
            return new FileDirectoryInfo(nodeRef, row, drone);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found",title), e);
        }
    }
    
    public boolean isShouldHaveFiles()
    {
        return shouldHaveFiles;
    }

    public void setShouldHaveFiles(boolean shouldHaveFiles)
    {
        this.shouldHaveFiles = shouldHaveFiles;
    }  

    /**
     * @param optype
     * @return
     */
    public boolean isMessagePresent(Optype optype)
    {       

      try
        {
          String message = drone.find(By.cssSelector(".message")).getText();
            switch(optype)
            {
                case SYNC:
                    return "Sync was created".equals(message) ? true:false;
                case UNSYNC:
                    return "Sync has been removed".equals(message) ? true:false;  
                case REQ_TO_SYNC:
                    return "Successfully requested Sync".equalsIgnoreCase(message) ? true:false;
                default:
                     throw new PageOperationException(message);
            }       
        }
        catch(TimeoutException toe)
        {
            logger.error("Message element not found!!");
        }
        throw new PageOperationException("Message element not found!!");
    }   
}