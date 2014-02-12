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
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the HTML page relating to the document library
 * sub navigation bar that appears on the document library site page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class DocumentLibraryNavigation extends SharePage
{
    public static final String FILE_UPLOAD_BUTTON = "button[id$='fileUpload-button-button']";
    public static final String CREATE_NEW_FOLDER_BUTTON = "button[id$='newFolder-button-button']";
    private static final By CREATE_CONTENT_BUTTON = By.cssSelector("button[id*='createContent-button']");
    private static final By SELECTED_ITEMS = By.cssSelector("button[id$='selectedItems-button-button']");
    private static final By SELECTED_ITEMS_MENU = By.cssSelector("div[id$='selectedItems-menu']");
    private static final By DOWNLOAD_AS_ZIP = By.cssSelector(".onActionDownload");
    private static final By SELECT_DROPDOWN = By.cssSelector("button[id$='default-fileSelect-button-button']");
    private static final By SELECT_DROPDOWN_MENU = By.cssSelector("div[id$='default-fileSelect-menu']");
    private static final By SELECT_ALL = By.cssSelector(".selectAll");
    private static final By SYNC_TO_CLOUD = By.cssSelector(".onActionCloudSync");
    private static final By REQUEST_SYNC = By.cssSelector(".onActionCloudSyncRequest");
    private static final String FILE_UPLOAD_ERROR_MESSAGE = "Unable to create file upload page";
    private static final String CREATE_FOLDER_ERROR_MESSAGE = "Unable to create new folder page";
    private Log logger = LogFactory.getLog(this.getClass());
    
    /**
     * Constructor.
     */
    public DocumentLibraryNavigation(WebDrone drone, String fileUploadButtonId, String createNewFolderButtonId)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentLibraryNavigation render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentLibraryNavigation render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentLibraryNavigation render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * @return      <tt>true</tt> if the <b>Upload</b> link is available
     * 
     * @since 1.5.1
     */
    public boolean hasFileUploadLink()
    {
        try
        {
            By criteria = By.cssSelector(FILE_UPLOAD_BUTTON);
            return drone.findAndWait(criteria, 100).isEnabled();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Mimics the action of selecting the file upload button.
     * 
     * @return HtmlPage response page object
     */
    public UploadFilePage selectFileUpload()
    {
        if(!alfrescoVersion.isFileUploadHtml5())
        {
            disbaleFileUploadFlash();   
        }
        WebElement button = drone.findAndWait(By.cssSelector(FILE_UPLOAD_BUTTON));
        button.click();
        
        return getFileUpload(drone);
    }
    /**
     * Get file upload page pop up object.
     * 
     * @param drone WebDrone browser client
     * @param repositoryBrowsing        <tt>true</tt> if we are doing repository browsing
     * @return SharePage page object response
     */
    public UploadFilePage getFileUpload(WebDrone drone)
    {

        // Verify if it is really file upload page, and then create the page.
        try
        {
            WebElement element;
            switch (alfrescoVersion) {
            case Cloud:
                element = drone.findAndWaitById("upload.file.dialog.id");
                break;
            case Enterprise41:
                element = drone.findAndWait(By.cssSelector("form[id$='_default-htmlupload-form']"));
                break;
            default:
                element = drone.findAndWaitById("upload.file.dialog.id");
                break;
            }

            if (element.isDisplayed())
            {
                return new UploadFilePage(drone);
            }
        }
        catch (TimeoutException te)
        {
            throw new PageException(FILE_UPLOAD_ERROR_MESSAGE, te);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(FILE_UPLOAD_ERROR_MESSAGE, e);
        }
        throw new PageException(FILE_UPLOAD_ERROR_MESSAGE);
    }
    
    /**
     * @return      <tt>true</tt> if the <b>New Folder</b> link is available
     * 
     * @since 1.5.1
     */
    public boolean hasNewFolderLink()
    {
        try
        {
            By criteria = By.cssSelector(CREATE_NEW_FOLDER_BUTTON);
            return drone.findAndWait(criteria, 250).isEnabled();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Introduced in Enterprise 4.2 dropdown with actions
     * to create different content such as folders and files.
     */
    private void selectCreateContentDropdown()
    {
        WebElement dropdown = drone.findAndWait(By.cssSelector("button[id$='createContent-button-button']"));
        dropdown.click();
    }
    /**
     * Mimics the action of selecting the create new folder button.
     * @return {@link NewFolderPage} page response
     */
    public NewFolderPage selectCreateNewFolder()
    {
        WebElement button;
        switch (alfrescoVersion)
        {
            case Enterprise41:
                button = drone.findAndWait(By.cssSelector(CREATE_NEW_FOLDER_BUTTON));
                break;
            default:
                selectCreateContentDropdown();
                button = drone.findAndWait(By.cssSelector("span.folder-file"));
                break;
        }
        button.click();
        return getNewFolderPage(drone);
    }
    
    /**
     * Mimics the action of selecting Create Plain Text.
     * 
     * @return {@link SharePage}
     * @throws Exception 
     */
    public  HtmlPage selectCreateContent(ContentType content)
    {
       
        if(alfrescoVersion.isCloud())
        {
        switch (content)
        {
            case GOOGLEDOCS:
            case GOOGLEPRESENTATION:
            case GOOGLESPREADSHEET:
                break;
            case PLAINTEXT:
            case HTML:
            case XML:
            default:
                throw new UnsupportedOperationException("Create Plain Text not Available for Cloud");
        }
        }    
       try
        {
           drone.findAndWait(CREATE_CONTENT_BUTTON).click();
           drone.findAndWait(content.getContentLocator()).click();
            
          }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element");
            throw new PageException("Unable to fine the Create Plain Text Link.", exception);
        }
           return content.getContentCreationPage(drone);
    }
    
    
    /**
     * Mimics the action of Selected Items.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage clickSelectedItems()
    {
        try
        {
            WebElement selectedItemsElement = drone.findAndWait(SELECTED_ITEMS);

            if(selectedItemsElement.isEnabled())
            {
                selectedItemsElement.click();
                return new DocumentLibraryPage(drone);
            }
            throw new PageException("Selected Items Button found, but is not enabled please select one or more item");
        }
        catch (TimeoutException e)
        {
            logger.error("Selected Item not available : " + SELECTED_ITEMS.toString());
            throw new PageException("Not able to find the Selected Items Button.");
        }
        
    }
    
    /**
     * 
     * @return true is Selected Item Menu Visible, else false.
     */
    public boolean isSelectedItemMenuVisible()
    {
        try
        {
            return drone.findAndWait(SELECTED_ITEMS_MENU).isDisplayed();
        }
        catch (TimeoutException e)
        {
        }
        catch (Exception e)
        {
        }
        return false;
    }
    
    /**
     * Mimics the action select download as Zip from selected Item.
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage selectDownloadAsZip()
    {
        try
        {
            if(isSelectedItemMenuVisible())
            {
                drone.findAndWait(DOWNLOAD_AS_ZIP).click();
                return new DocumentLibraryPage(drone);
            }
            else
            {
                throw new PageException("Selected Items menu not visible please click selected items before download as zip");
            }
        }
        catch (TimeoutException e)
        {
            String expectionMessage = "Not able to find the download as zip Link";
            logger.error(expectionMessage + e.getMessage());
            throw new PageException(expectionMessage);
        }
    }

    /**
     *
     */
    private void clickSelectDropDown()
    {
        try
        {
            drone.findAndWait(SELECT_DROPDOWN).click();
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the Select Dropdown";
            logger.error(exceptionMessage + e.getMessage());
            throw new PageException(exceptionMessage);
        }
    }

    /**
     *
     * @return true is Select Menu Visible, else false.
     */
    public boolean isSelectMenuVisible()
    {
        try
        {
            return drone.findAndWait(SELECT_DROPDOWN_MENU).isDisplayed();
        }
        catch (TimeoutException e)
        {
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * Mimics the action select All select dropdown.
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage selectAll()
    {
        try
        {
            clickSelectDropDown();
            if(isSelectMenuVisible())
            {
                drone.findAndWait(SELECT_ALL).click();
                return new DocumentLibraryPage(drone);
            }
            else
            {
                throw new PageException("Select dropdown menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find select All option";
            logger.error(exceptionMessage + e.getMessage());
            throw new PageException(exceptionMessage);
        }
    }

    /**
     * Mimics the action select "Sync to Cloud" from selected Item.
     * Assumes Cloud sync is already set-up
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectSyncToCloud()
    {
        try
        {
            clickSelectedItems();
            if(isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(SYNC_TO_CLOUD);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDisappears(By.id(id), maxPageLoadingTime);
                if(isSignUpDialogVisible())
                {
                    return new CloudSignInPage(getDrone());
                }
                else
                {
                    return new DestinationAndAssigneePage(getDrone());
                }
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Sync to Cloud\" Link";
            logger.error(exceptionMessage + e.getMessage());
            throw new PageOperationException(exceptionMessage);
        }
    }

    /**
     * Returns true if Sign In To Alfresco Cloud popup opens (User haven't set up CloudSync)
     *
     * @return boolean
     */
    public boolean isSignUpDialogVisible()
    {
        RenderTime time = new RenderTime(maxPageLoadingTime);
        time.start();
        try
        {
            while (true)
            {
                try
                {
                    return drone.find(By.cssSelector("form.cloud-auth-form")).isDisplayed();
                }
                catch (NoSuchElementException e)
                {
                    try
                    {
                        return !drone.find(By.cssSelector("div[id$='default-cloud-folder-title']")).isDisplayed();
                    }
                    catch (NoSuchElementException nse)
                    {
                        time.end();
                        continue;
                    }
                }
            }
        }
        catch (PageRenderTimeException prte)
        {

        }
        return false;
    }
    /**
     * New folder page pop up object.
     * 
     * @param drone WebDrone browser client
     * @param repositoryBrowsing        <tt>true</tt> if we are creating a folder in the Repository browser
     * @return NewFolderPage page object response
     */
    public NewFolderPage getNewFolderPage(WebDrone drone)
    {
        // Verify if it is right page, and then create the page.
        try
        {
            WebElement element = drone.findAndWait(By.cssSelector("div[id$='default-createFolder-dialog']"));
            if (element.isDisplayed())
            {
                return new NewFolderPage(drone);
            }
        }
        catch (TimeoutException te)
        {
            throw new PageException(CREATE_FOLDER_ERROR_MESSAGE, te);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(FILE_UPLOAD_ERROR_MESSAGE, e);
        }
        throw new PageException(CREATE_FOLDER_ERROR_MESSAGE);
    }

    /**
     * Mimics the action select "Request Sync" from selected Item.
     * Assumes Cloud sync is already set-up
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage selectRequestSync()
    {
        try
        {
            clickSelectedItems();
            if(isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(REQUEST_SYNC);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDeletedFromDom(By.id(id), maxPageLoadingTime);
                return new DocumentLibraryPage(drone);
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Request Sync\" Link";
            logger.error(exceptionMessage + e.getMessage());
            throw new PageOperationException(exceptionMessage);
        }
    }
}