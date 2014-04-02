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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.webdrone.HtmlElement;
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
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the HTML page relating to the document library
 * sub navigation bar that appears on the document library site page.
 *
 * @author Michael Suzuki
 * @author Shan Nagarajan
 *
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
    private static final By DELETE = By.cssSelector(".onActionDelete");
    private static final By COPY_TO = By.cssSelector(".onActionCopyTo");
    private static final By MOVE_TO = By.cssSelector(".onActionMoveTo");
    private static final By DESELECT_ALL = By.cssSelector(".onActionDeselectAll");
    private static final By START_WORKFLOW = By.cssSelector(".onActionAssignWorkflow");
    private static final By SET_DEFAULT_VIEW = By.cssSelector(".setDefaultView");
    private static final By REMOVE_DEFAULT_VIEW = By.cssSelector(".removeDefaultView");
    
    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Constructor.
     */
    public DocumentLibraryNavigation(WebDrone drone)
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
    public HtmlPage selectFileUpload()
    {
        if(!alfrescoVersion.isFileUploadHtml5())
        {
            setSingleMode();
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
    public HtmlPage getFileUpload(WebDrone drone)
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
                //Find by unique folder icon that appears in the dialog
                element = drone.findAndWait(By.cssSelector("img.title-folder"));
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
    public HtmlPage clickSelectedItems()
    {
        try
        {
            WebElement selectedItemsElement = drone.findAndWait(SELECTED_ITEMS);

            if(selectedItemsElement.isEnabled())
            {
                selectedItemsElement.click();
                return FactorySharePage.resolvePage(drone);
            }
            throw new PageException("Selected Items Button found, but is not enabled please select one or more item");
        }
        catch (TimeoutException e)
        {
            logger.error("Selected Item not available : " + SELECTED_ITEMS.toString());
            throw new PageException("Not able to find the Selected Items Button.", e);
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
        if(AlfrescoVersion.Enterprise41.equals(alfrescoVersion) || alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("Download as Zip option si not available on this version " + alfrescoVersion.toString());
        }
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
            throw new PageException(expectionMessage, e);
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
            throw new PageException(exceptionMessage, e);
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
    public HtmlPage selectAll()
    {
        try
        {
            clickSelectDropDown();
            if(isSelectMenuVisible())
            {
                drone.findAndWait(SELECT_ALL).click();
                return FactorySharePage.resolvePage(drone);
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
            throw new PageException(exceptionMessage, e);
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
                drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
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
            throw new PageOperationException(exceptionMessage, e);
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
                drone.waitUntilElementDeletedFromDom(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
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
            throw new PageOperationException(exceptionMessage, e);
        }
    }

    /**
     * Click on delete item of selected items drop down.
     * @return
     */
    public ConfirmDeletePage selectDelete()
    {
        try
        {
            clickSelectedItems();
            if(isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(DELETE);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return new ConfirmDeletePage(drone);
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Delete\" Link";
            logger.error(exceptionMessage + e.getMessage());
            throw new PageOperationException(exceptionMessage, e);
        }
    }


    /**
     * Click on copy to item of selected items drop down.
     * @return
     */
    public CopyOrMoveContentPage selectCopyTo()
    {
        try
        {
            clickSelectedItems();
            if(isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(COPY_TO);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return new CopyOrMoveContentPage(drone);
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Copy To\" Link";
            logger.error(exceptionMessage + e.getMessage());
            throw new PageOperationException(exceptionMessage, e);
        }
    }

    /**
     * Click on Move to item of selected items drop down.
     *
     * @return
     */
    public CopyOrMoveContentPage selectMoveTo()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(MOVE_TO);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return new CopyOrMoveContentPage(drone);
            } else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        } catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Move To\" Link";
            logger.error(exceptionMessage + e.getMessage());
            throw new PageOperationException(exceptionMessage, e);
        }
    }

    /**
     * Click on DeselectAll item of selected items drop down.
     *
     * @return
     */
    public HtmlPage selectDesellectAll()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(DESELECT_ALL);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return FactorySharePage.resolvePage(drone);
            } else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        } catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Deselect All\" Link";
            logger.error(exceptionMessage + e.getMessage());
            throw new PageOperationException(exceptionMessage, e);
        }
    }

    /**
     * Click on Start Work Flow item of selected items drop down.
     * @return StartWorkFlowPage
     */
    public StartWorkFlowPage selectStartWorkFlow()
    {
        try
        {
            clickSelectedItems();
            if(isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(START_WORKFLOW);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDeletedFromDom(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return new StartWorkFlowPage(drone);
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"StartWorkFlow\" Link";
            logger.error(exceptionMessage + e.getMessage());
            throw new PageOperationException(exceptionMessage, e);
        }
    }

    private void clickOptionDropDown()
    {
        WebElement btn = drone.find(By.cssSelector("button[id$='default-options-button-button']"));
        HtmlElement dropdownButton = new HtmlElement(btn, drone);
        dropdownButton.click();
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
                clickOptionDropDown();
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
    public HtmlPage selectDetailedView()
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
            return drone.getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
            throw new PageException("Exceeded the time to find css.", e);
        }
    }

    /**
     * Selects the Filmstrip View of the Document Library.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectFilmstripView()
    {
        try
        {
            selectItemInOptionsDropDown(By.cssSelector("span.view.filmstrip"));
            return drone.getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
            throw new PageException("Exceeded the time to find css.");
        }
    }

    /**
     * Selects the Simple View of the Document Library.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectSimpleView()
    {
        try
        {
            switch (alfrescoVersion)
            {
                case Enterprise41:
                    drone.findAndWait(By.cssSelector("button[title='Simple View']")).click();
                    break;
                case Cloud:
                    drone.findAndWait(By.cssSelector("button[id$='default-simpleView-button']")).click();
                    break;
                default:
                    selectItemInOptionsDropDown(By.cssSelector("span.view.simple"));
                    break;
            }
            return drone.getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
            throw new PageException("Exceeded the time to find css.", e);
        }
    }

    /**
     * Selects the Table View of the Document Library.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectTableView()
    {
        try
        {
            switch (alfrescoVersion) 
            {
                case Enterprise42:
                case MyAlfresco:
                    selectItemInOptionsDropDown(By.cssSelector("span.view.table"));
                    break;
                    
                case Cloud:
                    drone.findAndWait(By.cssSelector("button[id$='default-tableView-button']")).click();
                    break;
                    
                default:
                    drone.findAndWait(By.cssSelector("button[title='Table View']")).click();
                    break;
            }
            return drone.getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
            throw new PageException("Exceeded the time to find css.");
        }
    }
    
    /**
     * Mimics the action of selecting the Hide Folders in Option Menu.
     *
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectHideFolders()
    {
        try
        {
            selectItemInOptionsDropDown(By.cssSelector(".hideFolders"));
            return FactorySharePage.resolvePage(drone);
        }
        catch(NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css." + nse.getMessage());
        }
        catch(TimeoutException te)
        {
            logger.error("Exceeded the time to find css." + te.getMessage());
        }
        closeOptionMenu();
        throw new PageOperationException("Not able to find the Hide Folder Option");
    }

    /**
     * Mimics the action of selecting the Show Folders in Option Menu.
     *
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectShowFolders()
    {
        try
        {
            if(AlfrescoVersion.Enterprise41.equals(alfrescoVersion))
            {
                drone.find(By.cssSelector("button[id$='howFolders-button-button']")).click();
            }
            else
            {
                selectItemInOptionsDropDown(By.cssSelector(".showFolders"));
            }
            return FactorySharePage.resolvePage(drone);
        }
        catch(NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css." + nse.getMessage());
        }
        catch(TimeoutException te)
        {
            logger.error("Exceeded the time to find css." + te.getMessage());
        }
        closeOptionMenu();
        throw new PageOperationException("Not able to find the Show Folder Option");
    }

    /**
     * Closes the Option Menu if it is opened.
     */
    private void closeOptionMenu()
    {
        if(drone.find(By.cssSelector("div[id$='_default-options-menu']")).isDisplayed())
        {
            WebElement btn = drone.find(By.cssSelector("button[id$='default-options-button-button']"));
            HtmlElement dropdownButton = new HtmlElement(btn, drone);
            dropdownButton.click();
        }
    }

    /**
     * Mimics the action of selecting the Hide Breadcrump in Option Menu.
     *
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectHideBreadcrump()
    {
        try
        {
            selectItemInOptionsDropDown(By.cssSelector(".hidePath"));
            return FactorySharePage.resolvePage(drone);
        }
        catch(NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css." + nse.getMessage());
        }
        catch(TimeoutException te)
        {
            logger.error("Exceeded the time to find css." + te.getMessage());
        }
        closeOptionMenu();
        throw new PageOperationException("Not able to find the Hide Breadcrump Option");
    }

    /**
     * Mimics the action of selecting the Show Folders in Option Menu.
     *
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectShowBreadcrump()
    {
        try
        {
            selectItemInOptionsDropDown(By.cssSelector(".showPath"));
            return FactorySharePage.resolvePage(drone);
        }
        catch(NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css." + nse.getMessage());
        }
        catch(TimeoutException te)
        {
            logger.error("Exceeded the time to find css." + te.getMessage());
        }
        closeOptionMenu();
        throw new PageOperationException("Not able to find the Show Breadcrump Option");
    }

    /**
     * Method to check the visibility of navigation bar.
     *
     * @return true if navigation bar visible else false.
     */
    public boolean isNavigationBarVisible()
    {
        try
        {
            return drone.find(By.cssSelector("div[id$='_default-navBar']")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /**
     * Mimcis the action of click the folder up button in Navigation bar.
     *
     * @return {@link HtmlPage}
     */
    public HtmlPage clickFolderUp()
    {
        if(isNavigationBarVisible())
        {
            WebElement folderUpElement = drone.find(By.cssSelector("button[id$='folderUp-button-button’]"));
          if(folderUpElement.isEnabled())
          {
              folderUpElement.click();
              return FactorySharePage.resolvePage(drone);
          }
          else
          {
              throw new PageOperationException("You may be in the root folder, please check path and use folder up whenever required.");
          }
        }
        else
        {
           throw new PageOperationException("Navigation might be hidden, please click show breadcrump from option menu.");
        }
    }

    /**
     * Returns the {@link List} for Folders as {@link ShareLink}.
     *
     * @return {@link List} of {@link ShareLink} Folders.
     */
    public List<ShareLink> getFoldersInNavBar()
    {
        if(isNavigationBarVisible())
        {
            List<ShareLink> folderLinks = new ArrayList<ShareLink>();
            try
            {
                List<WebElement> folders = drone.findAll(By.cssSelector("a.folder"));
                folders.add(drone.find(By.cssSelector(".label>a")));
                for (WebElement folder : folders)
                {
                    folderLinks.add(new ShareLink(folder, drone));
                }
            }
            catch (NoSuchElementException e){}
            return folderLinks;
        }
        else
        {
           throw new PageOperationException("Navigation might be hidden, please click show breadcrump from option menu.");
        }
    }

    /**
     * Mimics the action selecting the folder in the navigation bar.
     *
     *  @param folderName - Folder Name to be selected in navigation bar.
     *
     * @return {@link HtmlPage}
     */
    public HtmlPage selectFolderInNavBar(String folderName)
    {
        if(isNavigationBarVisible())
        {
            List<ShareLink> folderLinks = getFoldersInNavBar();
            for (ShareLink shareLink : folderLinks)
            {
                if(shareLink.getDescription().trim().equals(folderName))
                {
                    return shareLink.click();
                }
            }
            throw new PageOperationException("Not able to find the folder named: " + folderName);
        }
        else
        {
           throw new PageOperationException("Navigation might be hidden, please click show breadcrump from option menu.");
        }
    }

    /**
     * Selects the Gallery View of the Document Library.
     *
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectGalleryView()
    {
        try
        {
            selectItemInOptionsDropDown(By.cssSelector("span.view.gallery"));
            return drone.getCurrentPage();
        }
        catch(NoSuchElementException nse)
        {
            logger.error("Unable to find css." + nse.getMessage());
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
        }
        throw new PageException("Unable to select the Gallery view.");
    }

    /**
     * This method is used to find the view type.
     *
     * @param By selector location of button in dropdown to select
     */
    public ViewType getViewType()
    {
        // Note: This is temporary fix to find out the view type.
        String text = (String) drone.executeJavaScript("return Alfresco.util.ComponentManager.findFirst('Alfresco.DocumentList').options.viewRendererName;");
        ViewType type = ViewType.getViewType(text);
        return type;
    }
    
    private boolean isDefaultViewVisible(By view)
    {
        boolean visible = false;
        try
        {
            clickOptionDropDown();
            if(drone.findAndWait(By.cssSelector("div[id$='default-options-menu']")).isDisplayed())
            {
                visible = drone.find(view).isDisplayed();
            }
        }
        catch (TimeoutException e)
        {
        }
        catch (NoSuchElementException e)
        {
        }
        
        closeOptionMenu();
        
        return visible;
    }
    
    /**
     * Returns true if the Set current view to default is visible
     * 
     * @return
     */
    public boolean isSetDefaultViewVisible()
    {
        return isDefaultViewVisible(SET_DEFAULT_VIEW);
    }
    
    /**
     * Returns true if the Remove current default view is present
     * 
     * @return
     */
    public boolean isRemoveDefaultViewVisible()
    {
        return isDefaultViewVisible(REMOVE_DEFAULT_VIEW);
    }
    
    /**
     * Clicks on the 'Set "<current view>" as default for this folder' button in the options menu.
     * 
     * @return
     */
    public HtmlPage selectSetCurrentViewToDefault()
    {
        try
        {
            selectItemInOptionsDropDown(SET_DEFAULT_VIEW);
            return FactorySharePage.resolvePage(drone);
        }
        catch(NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css." + nse.getMessage());
        }
        catch(TimeoutException te)
        {
            logger.error("Exceeded the time to find css." + te.getMessage());
        }
        closeOptionMenu();
        throw new PageOperationException("Not able to find the Set Default View Option");
    }
    
    /**
     * Clicks on the 'Remove "<current view>" as default for this folder' button in the options menu.
     * 
     * @return
     */
    public HtmlPage selectRemoveCurrentViewFromDefault()
    {
        try
        {
            selectItemInOptionsDropDown(REMOVE_DEFAULT_VIEW);
            return FactorySharePage.resolvePage(drone);
        }
        catch(NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css." + nse.getMessage());
        }
        catch(TimeoutException te)
        {
            logger.error("Exceeded the time to find css." + te.getMessage());
        }
        closeOptionMenu();
        throw new PageOperationException("Not able to find the Remove Default View Option");
    }

    private void clickSortDropDown()
    {
        WebElement btn = drone.find(By.cssSelector("button[id$='default-sortField-button-button']"));
        HtmlElement sortButton = new HtmlElement(btn, drone);
        sortButton.click();
    }
    
    /**
     * Select the sort drop down and clicks on the button in 
     * the dropdown.
     * @param sortField enum value of the button in dropdown to select
     */
    public HtmlPage selectSortFieldFromDropDown(SortField sortField)
    {
        try
        {
            RenderTime timer = new RenderTime(WAIT_TIME_3000);
            while(true)
            {
                timer.start();
                try
                {
                	clickSortDropDown();
                	
                    WebElement dropdown = drone.findAndWait(By.cssSelector("div[class$='sort-field'] > div"));
                    if(dropdown.isDisplayed())
                    {
                        new HtmlElement(drone.find(sortField.getSortLocator()), drone).click();
                        break;
                    }            
                }
                catch (StaleElementReferenceException stale) { }
                finally { timer.end(); }
            }

            return FactorySharePage.resolvePage(drone);
        }
        catch(NoSuchElementException nse)
        {
            logger.error("Unable to find css." + nse.getMessage());
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
        }
        throw new PageException("Unable to select the sort field " + sortField);
    }
    
    /**
     * Get the current sort field.
     * 
     * @return	The SortField enum.
     */
    public SortField getCurrentSortField()
    {
        try
        {
            RenderTime timer = new RenderTime(WAIT_TIME_3000);
            while(true)
            {
                timer.start();
                try
                {
                	
                    WebElement button = drone.findAndWait(By.cssSelector("div[class$='sort-field'] button"));
                   
                    return SortField.getEnum(button.getText());
         
                }
                catch (StaleElementReferenceException stale) { }
                finally { timer.end(); }
            }
        }
        catch(NoSuchElementException nse)
        {
            logger.error("Unable to find css." + nse.getMessage());
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
        }
        throw new PageException("Unable to find the current sort field ");
    }

    /**
     * Determines the sort direction.
     * 
     * @return	<code>true</code> if the page is sorted ascending, otherwise <code>false</code>
     */
    public boolean isSortAscending()
    {
    	try
    	{
	    	WebElement sortEl = drone.findAndWait(By.cssSelector("span[id$='default-sortAscending-button']"));
	    	String classValue = sortEl.getAttribute("class");
	    	
	    	if(classValue == null)
	    	{
	    		return true;
	    	}
	    	
	    	//if class sort-descending exists then ascending is true.
	    	return !classValue.contains("sort-descending");    		
    	}
        catch(NoSuchElementException nse)
        {
            logger.error("Unable to find css." + nse.getMessage());
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
        }
        throw new PageException("Unable to find the sort button.");
    }
    
    private void clickSortOrder()
    {
        WebElement btn = drone.find(By.cssSelector("button[id$='default-sortAscending-button-button']"));
        HtmlElement dropdownButton = new HtmlElement(btn, drone);
        dropdownButton.click();
    }
    
    /**
     * Set the sort direction to ascending.
     * 
     * @return	The refreshed HtmlPage.
     */
    public HtmlPage sortAscending()
    {
    	if(!isSortAscending())
    	{
    		clickSortOrder();
    	}
    	
    	return FactorySharePage.resolvePage(drone);
    }
    
    /**
     * Set the sort direction to descending.
     * 
     * @return	The refreshed HtmlPage.
     */
    public HtmlPage sortDescending()
    {
    	if(isSortAscending())
    	{
    		clickSortOrder();
    	}
    	
    	return FactorySharePage.resolvePage(drone);
    }
}