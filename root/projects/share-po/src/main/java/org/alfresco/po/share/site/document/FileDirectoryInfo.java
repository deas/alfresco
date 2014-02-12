/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of
 * Alfresco Alfresco is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. Alfresco is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Alfresco. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.exception.AlfrescoVersionException;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Entity that models the list of file or directories as it appears on the
 * {@link DocumentLibraryPage}. The list models the HTML element representing
 * the file or directory.
 * 
 * @author Michael Suzuki
 */
public class FileDirectoryInfo extends HtmlElement
{
    private static Log logger = LogFactory.getLog(FileDirectoryInfo.class);

    private static final String CLOUD_SYNC_ICON = "a[data-action='onCloudSyncIndicatorAction']";
    private static final String WORKFLOW_ICON = "img[alt='active-workflows']";
    private static final String FILENAME_IDENTIFIER = "h3.filename a";
    private static final String FILE_EDIT_INFO = "div.yui-dt-liner div:nth-of-type(1)";
    private static final String FILE_DESC_IDENTIFIER = " td.yui-dt-col-fileName div.yui-dt-liner div:nth-of-type(2)";
    private static final String TAG_INFO = "span[title='Tag'] + form + span.item";
    private static final String TAG_COLLECTION = TAG_INFO + " > span.tag > a";
    private static final String ADD_TAG = "span[title='Tag']";
    private static final String INPUT_TAG_NAME = "div.inlineTagEdit input";
    private static final String TAG_NAME = "a.tag-link";
    private static final String IMG_FOLDER = "/documentlibrary/images/folder";
    private static final String FAVOURITE_CONTENT = "a.favourite-action";
    private static final String LIKE_CONTENT = "a.like-action";
    private static final String LIKE_COUNT = "span.likes-count";
    private static final String THUMBNAIL = "td.yui-dt-col-thumbnail>div>span>a";
    private static final String THUMBNAIL_TYPE = "td.yui-dt-col-thumbnail>div>span";
    private static final String CONTENT_NODEREF = "h3.filename form";
    private static final String ACTIONS_MENU = "td:nth-of-type(5)";
    private static final String ACTIONS_LIST = "div.action-set>div";
    private static final String TITLE = "span.title";
    private static final String INLINE_TAGS = "div.inlineTagEdit>span>span.inlineTagEditTag";
    private static final String CLOUD_REMOVE_TAG = "img[src$='delete-item-off.png']";
    private static final String ENTERPRISE_REMOVE_TAG = "img[src$='delete-tag-off.png']";
    private static final String SELECT_CHECKBOX = "input[id^='checkbox-yui']";
    private static final String CONTENT_ACTIONS = "td:nth-of-type(5)";
    private static final String DOWNLOAD_DOCUMENT = "div.document-download>a";
    private static final String DOWNLOAD_FOLDER = "div.folder-download>a";
    private static final By TAG_LINK_LOCATOR = By.cssSelector("div.yui-dt-liner>div>span>span>a.tag-link");
    private static final By SYNC_INFO_PAGE = By.cssSelector("img[title*='Click to view'] ");
    private static final By INFO_BANNER = By.cssSelector("div.info-banner");
    private static final By LOCK_ICON = By.cssSelector("img[alt='lock-owner']");
    private static final By INLINE_EDIT_LINK = By.cssSelector("div.document-inline-edit>a[title='Inline Edit']>span");
    private static final By EDIT_OFFLINE_LINK = By.cssSelector("div.document-edit-offline>a[title='Edit Offline']>span");
    private static final By MORE_ACTIONS_MENU = By.cssSelector("div.more-actions");
    private static final By SYNC_FAILED_ICON = By.cssSelector("img[alt='cloud-sync-failed']");
    private final String GOOGLE_DOCS_URL = "googledocsEditor?";
    private final By REQUEST_TO_SYNC = By.cssSelector("div#onActionCloudSyncRequest>a[title='Request Sync']");
    private final String moreActions;
    private final String nodeRef;
    private final AlfrescoVersion alfrescoVersion;

    public FileDirectoryInfo(String nodeRef, WebElement webElement, WebDrone drone)
    {
        super(webElement, drone);
        if(nodeRef == null)
        {
            throw new IllegalArgumentException("NodeRef is required");
        }
        this.nodeRef = nodeRef;
        moreActions = drone.getElement("more.actions");
        alfrescoVersion = drone.getProperties().getVersion();
        resolveStaleness();
    }

    /**
     * Gets the name of the file or directory, if none then empty string is
     * returned.
     * 
     * @return String title
     */
    public String getName()
    {
        String title = "";
        try
        {
            title = findAndWait(By.cssSelector(FILENAME_IDENTIFIER)).getText();
            
        }
        catch (TimeoutException te)
        {
            logger.error("Timeout Reached", te);
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            getName();
        }
        return title;
    }


    /**
     * Click on title.
     */
    public void clickOnTitle()
    {
        findAndWait(By.cssSelector(FILENAME_IDENTIFIER)).click();
        domEventCompleted();
    }


    /**
     * Checks if the FileDirectory is of a folder type.
     * 
     * @return true if folder
     */
    public boolean isTypeFolder()
    {
        boolean isFolder = false;
        try
        {
            WebElement img = findElement(By.tagName("img"));
            String path = img.getAttribute("src");
            if(path != null && path.contains(IMG_FOLDER))
            {
                isFolder = true;
            }
        }
        catch (NoSuchElementException e){ }
        return isFolder;
    }

    /**
     * Gets the description of the file or directory, if none then empty string
     * is returned.
     * 
     * @return String Content description
     */
    public String getDescription()
    {
        try
        {
            return findAndWait(By.cssSelector(FILE_DESC_IDENTIFIER)).getText();
        }
        catch (NoSuchElementException nse) { }
        return "";
    }

    /**
     * Gets the Create / Edit Information of the file or directory, if none then
     * empty string is returned.
     * 
     * @return String Content Edit Information
     */
    public String getContentEditInfo()
    {
        return findAndWait(By.cssSelector(FILE_EDIT_INFO)).getText();
    }

    /**
     * Gets the Tag Information of the file or directory, if none then 'No Tags'
     * string is returned.
     * 
     * @return List<String> List of tags added to the content
     */
    public List<String> getTags()
    {
        List<String> tagsList = new ArrayList<String>();
        try
        {
            // Find if multiple tags are present
            List<WebElement> tagList = findAllWithWait(By.cssSelector(TAG_COLLECTION));
            for (WebElement tag : tagList)
            {
                tagsList.add(tag.getText());
            }
        }
        catch (TimeoutException te)
        {
            if(logger.isDebugEnabled()) 
            {
                    logger.debug("Timed out while waiting for Tag Information", te);
            }
        }
        return tagsList;
    }


    /**
     * Select the delete button on the item.
     * @return boolean <tt>true</tt> if delete option is available and clicked
     */
    public void selectDelete()
    {
        try
        {
            WebElement actions = findElement(By.cssSelector("td:nth-of-type(5)"));
            getDrone().mouseOverOnElement(actions);
            WebElement moreLink = findElement(By.cssSelector("a.show-more"));
            moreLink.click();
            WebElement deleteLink = findElement(By.cssSelector("div[class$='delete'] a"));
            deleteLink.click();
        }
        catch (NoSuchElementException e) { }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectDelete();
        }
    }

    /**
     * Selects the edit properties link on the select data row on
     * DocumentLibrary Page.
     * 
     * @return {@link EditDocumentPropertiesPopup} response
     */
    public EditDocumentPropertiesPopup selectEditProperties()
    {
        WebElement actions = findElement(By.cssSelector("td:nth-of-type(5)"));
        getDrone().mouseOverOnElement(actions);
        WebElement editProperties = findElement(By.cssSelector("div.document-edit-properties>a"));
        editProperties.click();
        return new EditDocumentPropertiesPopup(getDrone());
    }

    /**
     * Selects the 'Actions' menu link on the select data row on DocumentLibrary
     * Page.
     * 
     * @return {@link WebElement} WebElement that allows access to Actions menu for the selected Content
     */
    public WebElement selectContentActions()
    {
        return findElement(By.cssSelector(ACTIONS_MENU));
    }
    
    /**
     * Selects the 'Actions' menu link on the select data row on DocumentLibrary Page.
     * 
     * @return List of {@link WebElement} available for the selected Content
     */
    public List<WebElement> getContentActions()
    {
        try
        { 
            return selectContentActions().findElements(By.cssSelector(ACTIONS_LIST));
        }
        catch (Exception e)
        {
            logger.error("Error getting Actions" + e.toString());
        }
        return Collections.emptyList();
    }

    /**
     * Selects or de-selects the favorite option on the select data row on DocumentLibrary Page.
     */
    public void selectFavourite()
    {
        try
        {
            findElement(By.cssSelector(FAVOURITE_CONTENT)).click();
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            selectFavourite();
        }
    }

    /**
     * Selects or de selects the Like option on the select data row on
     * DocumentLibrary Page.
     */
    public void selectLike()
    {
        findElement(By.cssSelector(LIKE_CONTENT)).click();
        domEventCompleted();
    }

    /**
     * Checks if the Like option is selected on the selected data row on
     * DocumentLibrary Page
     * 
     * @return {boolean} true if the content is liked
     */
    public boolean isLiked()
    {
        try
        {
            WebElement likeContent = findElement(By.cssSelector(LIKE_CONTENT));
            String status = likeContent.getAttribute("class");
            if(status != null)
            {
                boolean liked = status.contains("like-action enabled");
                return liked;
            }
        }
        catch(NoSuchElementException nse){ }     
        catch(StaleElementReferenceException stale)
        {
            resolveStaleness();
            return isLiked(); 
        }
        return false;  
    }

    /**
     * Checks if the Favourite option is selected on the selected data row on
     * DocumentLibrary Page
     * 
     * @return {Boolean} true if the content is marked as Favourite
     */
    public boolean isFavourite()
    {
        try
        {
            WebElement favouriteContent = findElement(By.cssSelector(FAVOURITE_CONTENT));
            String status = favouriteContent.getAttribute("class");
            if (status != null)
            {
                return status.contains("favourite-action enabled");
            }
        }
        catch(NoSuchElementException nse){ }
        catch(StaleElementReferenceException stale)
        {
            resolveStaleness();
            return isFavourite(); 
        }
        return false;
    }

    /**
     * Gets the like count for the selected data row on DocumentLibrary Page
     * 
     * @return {String} Like Count
     */
    public String getLikeCount()
    {
        try
        {
            return findElement(By.cssSelector(LIKE_COUNT)).getText();
        }
        catch (StaleElementReferenceException e)
        {
            return getLikeCount();
        }
    }

    /**
     * Check if tags are attached to the selected content.
     * 
     * @return boolean <tt>true</tt> if content has one or more Tags
     */
    public boolean hasTags()
    {
        try
        {
            WebElement contentTag = find(By.cssSelector(TAG_NAME));
            return contentTag.isDisplayed();
        }
        catch (NoSuchElementException nse) { }
        catch (TimeoutException te) { }
        return false;
    }

    /**
     * Adds the specified Tag to the file or directory.
     * 
     * @param tagName String tag to be added
     */
    public void addTag(final String tagName)
    {
        if(tagName == null || tagName.isEmpty())
        {
            throw new IllegalArgumentException("Tag Name is required");
        }   
        try
        {
            clickOnAddTag();
            WebElement inputTagName = findAndWait(By.cssSelector(INPUT_TAG_NAME));
            inputTagName.clear();
            inputTagName.sendKeys(tagName + "\n");
            findAndWait(By.linkText("Save")).click();
            domEventCompleted();
         }
         catch (TimeoutException te)
         {
             logger.error("Error adding tag: ", te);
             throw new PageException("Error While adding tag: " + tagName);
         }
    }

    /**
     * Get NodeRef for the content on the selected data row on DocumentLibrary
     * Page.
     * 
     * @return {String} Node Ref / GUID
     */
    public String getContentNodeRef()
    {
        try
        {
            WebElement nodeRef = findElement(By.cssSelector(CONTENT_NODEREF));
            String nodeRefStr = nodeRef.getAttribute("action");
            if (nodeRefStr != null)
            {
                nodeRefStr = nodeRefStr.replace("/formprocessor", "");
                String nodeRefVal = nodeRefStr.substring(nodeRefStr.indexOf("/") + 1);
                return nodeRefVal;
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find content node ref value", nse);
        }
        throw new PageOperationException("The node ref value was invalid");        
    }

    @Override
    public String toString()
    {
        return "FileDirectoryInfo [getName()=" + getName() + "]";
    }

    
    /**
     * Gets the Title of the file or directory, if none then empty string
     * is returned.
     * 
     * @return String Content description
     */
    public String getTitle()
    {
        try
        {
            return findAndWait(By.cssSelector(TITLE)).getText();
        }
        catch (TimeoutException te) { }
        throw new PageOperationException("Unable to find content row title");
    }

    /**
     * This method gets the list of in line tags after clicking on tag info icon.
     * 
     * @return List<WebElement> collection of tags
     */
    private List<WebElement> getInlineTagList()
    {
        try
        {
            return findAllWithWait(By.cssSelector(INLINE_TAGS));
        }
        catch (TimeoutException e)
        {
                logger.error("Exceeded the time to find css." + e.getMessage());
                throw new PageException("Exceeded the time to find css." + e.getMessage());
        }

    }

    /**
     * Mimics the action of hovering over a tag until edit tag icon appears.
     */
    public void clickOnAddTag()
    {
        //hover over tag area
        RenderTime timer = new RenderTime(((WebDroneImpl) getDrone()).getMaxPageRenderWaitTime() * 2);
        while(true)
        {
            try
            {
                timer.start();
                WebElement tagInfo = findAndWait(By.cssSelector(TAG_INFO));
                getDrone().mouseOver(tagInfo);
                //Wait till pencil icon appears
                WebElement addTagBtn = findElement(By.cssSelector(ADD_TAG));
                //Select to get focus
                addTagBtn.click();
                if(findElement(By.cssSelector(INPUT_TAG_NAME)).isDisplayed()) break;
            }
            catch (NoSuchElementException e){}
            catch (ElementNotVisibleException e2){}
            catch (StaleElementReferenceException stale){}
            finally { timer.end(); }
        }
    }

    /**
     * This method gets the status whether given tagname remove button
     * has found or not.
     * @return boolean if icon is displayed
     */
    public boolean removeTagButtonIsDisplayed(String tagName)
    {
        if (tagName == null) throw new IllegalArgumentException("tagName is required.");
        try
        {
            return getRemoveTagButton(tagName).isDisplayed();
        }
        catch (Exception e)
        { 
        	logger.error(String.format("Unable to find remove tag for %s cause: %s", tagName, e.getMessage()));
    	}
        return false;
    }

    /**
     * This method clicks on given tag name remove button.
     * @param tagName String tag name
     */
    public void clickOnTagRemoveButton(String tagName)
    {
        if (tagName == null)
        {
            throw new IllegalArgumentException("tagName is required.");
        }
        
        try
        {
            getRemoveTagButton(tagName).click();
        }
        catch (Exception e)
        {
            throw new PageException("Unable to find the remove tag button.");
        }
    }

    /**
     * This method finds the remove button on tag element and returns button 
     * @param tagName
     * @return WebElement
     */
    private WebElement getRemoveTagButton(String tagName)
    {
        for (WebElement tag : getInlineTagList())
        {
            String text = tag.getText();
            if (text != null && text.equalsIgnoreCase(tagName))
            {
                try
                {
                    String selector = alfrescoVersion.isDojoSupported()  ? CLOUD_REMOVE_TAG : ENTERPRISE_REMOVE_TAG;
                    return tag.findElement(By.cssSelector(selector));
                }
                catch (NoSuchElementException e)
                {
                    logger.error("Unable to find the remove tag button." + e.getMessage());
                }
            }
        }
        throw new PageException("Unable to find the remove tag button.");
    }

    /**
     * This method is used to click on save button when editing a tag. 
     */
    public void clickOnTagSaveButton()
    {
        try
        {
            findAndWait(By.linkText("Save")).click();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Save button css." + ex.getMessage());
            throw new PageException("Exceeded time to find the Save button css.");
        }
    }

    /**
     * This method is used to click on cancel button when editing a tag. 
     */
    public void clickOnTagCancelButton()
    {
        try
        {
            findAndWait(By.linkText("Cancel")).click();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Save button css." + ex.getMessage());
            throw new PageException("Exceeded time to find the Save button css.");
        }
    }
    
    /**
     * Selects checkbox next to the contentRow.
     */
    public void selectCheckbox()
    {
        findElement(By.cssSelector(SELECT_CHECKBOX)).click();
        domEventCompleted();
    }
    
    /**
     * Verify if checkbox next to the contentRow is selected.
     * @return true if selected
     */
    public boolean isCheckboxSelected()
    {
        try
        {
            return findElement(By.cssSelector(SELECT_CHECKBOX)).isSelected();
        }
        catch(NoSuchElementException nse){ }
        return false;
    }
    
    /**
     * Clicks on the thumbnail next to the contentRow.
     * 
     * @return {Link SitePage} Instance of SitePage page object
     */
    public SitePage selectThumbnail()
    {
        WebElement contentThumbnail = findElement(By.cssSelector(THUMBNAIL));
        String href = contentThumbnail.getAttribute("href");
        contentThumbnail.click();
        if(href != null && href.contains("document-details"))
        {
            return new DocumentDetailsPage(getDrone());
        }
        return new DocumentLibraryPage(getDrone());
    }
    
    /**
     * Returns the WebElement for Actions in the selected row.
     * 
     * @return {Link WebElement} from where the set of Actions available for the
     *         selected content can be accessed
     */
    private WebElement selectAction()
    {
        return findElement(By.cssSelector(CONTENT_ACTIONS));
    }
    
    /**
     * Returns the WebElement for <More> Actions the in the selected row.
     */
    public WebElement selectMoreAction()
    {
        WebElement actions = findElement(By.cssSelector(CONTENT_ACTIONS));
        getDrone().mouseOverOnElement(actions);
        WebElement contentActions = selectAction();
        return contentActions.findElement(By.cssSelector(moreActions));
    }
    
    /**
     * Returns true if content in the selected data row on DocumentLibrary is
     * folder Page.
     * 
     * @return {boolean} <tt>true</tt> if the content is of type folder.
     */
    public boolean isFolder()
    {
        try
        {
            WebElement thumbnailType = findElement(By.cssSelector(THUMBNAIL_TYPE));
            return thumbnailType.getAttribute("class").equals("folder");
        }
        catch (Exception e){ }
        return false;
    }

    /**
     * Returns whether the file / dir is cloud synced.
     * 
     * @return
     */
    public boolean isCloudSynced()
    {
        try
        {
            WebElement thumbnailType = findAndWait(By.cssSelector(CLOUD_SYNC_ICON));
            return thumbnailType.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Returns whether the file / dir is part of workflow.
     * 
     * @return
     */
    public boolean isPartOfWorkflow()
    {
        try
        {
            WebElement thumbnailType = findAndWait(By.cssSelector(WORKFLOW_ICON));
            return thumbnailType.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
        catch (TimeoutException e)
        {
            return false;
        }

    }
    /**
     * Selects the <Download as zip> link on the select data row on DocumentLibrary
     * Page. Only available for content type = Folder.
     */
    public void selectDownloadFolderAsZip()
    {
        if(!isFolder())
        {
            throw new PageOperationException("Option Download Folder is not possible against a file, must be folder to workFileDirectoryInfoTest");
        }
        if(!(AlfrescoVersion.Enterprise42.equals(alfrescoVersion) || AlfrescoVersion.Enterprise43.equals(alfrescoVersion)))
        {
            throw new AlfrescoVersionException("Option Download Folder as Zip is not available for this version of Alfresco");
        }

        WebElement contentActions = selectAction();
        downloadFolderAsZip(contentActions);
        /*
         *  Assumes driver capability settings to save file in a specific location when
         *  <Download> option is selected via Browser
         */
    }
    /**
     * Clicks on the download folder as a zip button from the action menu
     * @param contentActions drop down menu web element
     * @param retry limits the number of tries
     */
    private void downloadFolderAsZip(WebElement contentActions, String ... retry)
    {
        try
        {
            getDrone().mouseOver(contentActions);
            WebElement menuOption = findElement(By.cssSelector(DOWNLOAD_FOLDER));
            menuOption.click();
        }
        catch (NoSuchElementException nse)
        {
            if(retry.length < 1)
            {
                downloadFolderAsZip(contentActions,"retry");
            }
            throw new PageException("Unable to click download folder as a zip");
        }
    }
    
    /**
     * Selects the <Download> link on the select data row on DocumentLibrary Page.
     */
    public void selectDownload()
    {
        if (isFolder())
        {
            throw new UnsupportedOperationException("Option View Details is only available to Content of type Document");
        }

        WebElement contentActions = selectAction();
        getDrone().mouseOverOnElement(contentActions);
        WebElement menuOption = findElement(By.cssSelector(DOWNLOAD_DOCUMENT));
        menuOption.click();
        // Assumes driver capability settings to save file in a specific location when
        // <Download> option is selected via Browser
    }
    /**
     * Gets the node ref id of the content.
     * @return String node identifier
     */
    public String getNodeRef()
    {
        try
        {
            return super.findAndWait(By.cssSelector(SELECT_CHECKBOX)).getAttribute("value");
        }
        catch (StaleElementReferenceException e)
        { 
            throw new PageException("Unable to obtain nodeRef id required for FileDirectoryInfo", e);
        }
    }

    /**
     * Refresh web element mechanism.
     * As the page changes every id on every action or event 
     * that takes place on the page, we refresh the web element
     * we were working with by re-finding it on the page 
     * and updating the page object.
     */
    private void resolveStaleness() 
    {
        if(nodeRef == null || nodeRef.isEmpty())
        {
            throw new UnsupportedOperationException(String.format("Content noderef is required: %s",nodeRef));
        }
        
        List<WebElement> elements = getDrone().findAll(By.cssSelector(String.format("input[value='%s']",nodeRef)));
        if(elements == null || elements.isEmpty())
        {
            throw new UnsupportedOperationException("there are no elements matching the node ref");
        }
        
        for(WebElement element : elements)
        {
            if(element.getText() != null && element.getAttribute("value").equalsIgnoreCase(nodeRef))
            {
                WebElement row = element.findElement(By.xpath("../../.."));
                setWebElement(row);
            }
        }
    }
    
    /**
     * Performs the find and wait given amount of time
     * with an added resolveStaleness.
     * If we encounter the staleness exception we refresh the web
     * element we are working with and re-do the search.
     * @param By css selector
     * @return {@link WebElement}
     */
    @Override
    public WebElement findAndWait(By cssSelector)
    {
        try
        {
            return super.findAndWait(cssSelector);
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            return findAndWait(cssSelector);
        }
    }
    
    /**
     * Performs the find with an added resolveStaleness.
     * If we encounter the staleness exception we refresh the web
     * element we are working with and re-do the search.
     * @param By css selector
     * @return {@link WebElement}
     */

    @Override
    public WebElement findElement(By cssSelector)
    {
        try
        {
            return super.findElement(cssSelector);
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            return findElement(cssSelector);
        }
    }
    /**
     * Performs the find with an added resolveStaleness.
     * If we encounter the staleness exception we refresh the web
     * element we are working with and re-do the search.
     * @param By css selector
     * @return colelction {@link WebElement}
     */

    @Override
    public List<WebElement> findAllWithWait(By cssSelector) 
    {
        try
        {
            return super.findAllWithWait(cssSelector);
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            return findAllWithWait(cssSelector);
        }
    }
    
    /**
     * Selects the <View Details> link on the select data row on DocumentLibrary Page. 
     * Only available for content type = Folder.
     * 
     * @return {@link DocumentLibraryPage} response
     */
    public FolderDetailsPage selectViewFolderDetails()
    {
        WebElement contentActions = findElement(By.cssSelector(CONTENT_ACTIONS));
        getDrone().mouseOver(contentActions);
        WebElement menuOption = findAndWait(By.cssSelector("div.folder-view-details>a"));
        menuOption.click();

        return new FolderDetailsPage(getDrone());
    }
    
    /**
     * This method clicks on tag Name link.
     * 
     * @param tagName
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage clickOnTagNameLink(String tagName)
    {
        if (tagName == null)
        {
            throw new UnsupportedOperationException("Drone and TagName is required.");
        }

        try
        {
            List<WebElement> tagList = getDrone().findAndWaitForElements(TAG_LINK_LOCATOR);
            if (tagList != null)
            {
                for (WebElement tag : tagList)
                {
                    String tagText = tag.getText();
                    if (tagName.equalsIgnoreCase(tagText))
                    {
                        tag.click();
                        return new DocumentLibraryPage(getDrone());
                    }
                }
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
            throw new PageException("Exceeded the time to find css.");
        }
        throw new PageException("Not able to tag name: " + tagName);
    }

    /**
     * Selects the "Sync to Cloud" link on the select data row on
     * DocumentLibrary Page.
     *
     * @return {@link DestinationAndAssigneePage} response
     */
    public HtmlPage selectSyncToCloud()
    {
        try
        {
            WebElement actions = findElement(By.cssSelector(CONTENT_ACTIONS));
            getDrone().mouseOverOnElement(actions);
            selectMoreAction().click();
            WebElement syncToCloud = findAndWait(By.cssSelector("div#onActionCloudSync a"));
            syncToCloud.click();
            if(isSignUpDialogVisible())
            {
                return new CloudSignInPage(getDrone());
            }
            else
            {
                return new DestinationAndAssigneePage(getDrone());
            }
        }
        catch (NoSuchElementException nse)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("No Such Element exception" + nse);
            }
        }
        catch (TimeoutException te)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Timeout exception" + te);
            }
        }
        throw  new PageException("Unable to select SyncToCloud option");

    }

    /**
     * Selects the edit in google docs link
     * 
     * @return {@link DestinationAndAssigneePage} response
     */
    public HtmlPage selectEditInGoogleDocs()
    {
        WebElement actions = findElement(By.cssSelector(CONTENT_ACTIONS));
        getDrone().mouseOverOnElement(actions);
        selectMoreAction().click();
        WebElement editLink = findAndWait(By.cssSelector("div#onGoogledocsActionEdit a"));
        editLink.click();
        String text = "Editing in Google Docs";
        drone.waitUntilVisible(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxTime, MILLISECONDS));
        drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxTime, MILLISECONDS));
        if (!drone.getCurrentUrl().contains(GOOGLE_DOCS_URL))
        {
            return new GoogleDocsAuthorisation(drone, null, false);
        }
        else
        {
            String errorMessage = "";
            try
            {
                errorMessage = drone.find(By.cssSelector("div.bd>span.message")).getText();
            }
            catch (NoSuchElementException e)
            {
                return new EditInGoogleDocsPage(drone, null, false);
            }
            throw new PageException(errorMessage);
        }
    }
    /**
     * Returns true if Sign In To Alfresco Cloud popup opens (User haven't set up CloudSync)
     *
     * @return boolean
     */
    public boolean isSignUpDialogVisible()
    {
        RenderTime time = new RenderTime(maxTime);

        time.start();

        try
        {
            while (true)
            {
                try
                {
                    return !drone.find(By.cssSelector("div[id$='default-cloud-folder-title']")).isDisplayed();
                }
                catch (NoSuchElementException e)
                {
                    try
                    {
                        return drone.find(By.cssSelector("form.cloud-auth-form")).isDisplayed();
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
     * Selects the "unSync to Cloud" link on the select data row on
     * DocumentLibrary Page.
     *
     * @return {@link DestinationAndAssigneePage} response
     */
    public DocumentLibraryPage selectUnSyncAndRemoveContentFromCloud(boolean doRemoveContentOnCloud)
    {
        selectUnSyncFromCloud();
        if(doRemoveContentOnCloud)
            getDrone().findAndWait(By.cssSelector(".requestDeleteRemote-checkBox")).click();
        List<WebElement> buttonElements = getDrone().findAndWaitForElements(By.cssSelector("div>span.button-group>span>span.first-child"));
        for (WebElement webElement : buttonElements)
        {
            if("Remove sync".equals(webElement.getText()))
            {
                webElement.click();
                break;
            }
        }
        return new DocumentLibraryPage(getDrone());
    }
    
    /**
     * Selects the "Sync to Cloud" link on the select data row on
     * DocumentLibrary Page.
     *
     * @return {@link DestinationAndAssigneePage} response
     */
    public void selectUnSyncFromCloud()
    {
        selectMoreAction().click();
        WebElement unSyncToCloud = findAndWait(By.cssSelector("div#onActionCloudUnsync>a[title='Unsync from Cloud']"));
        unSyncToCloud.click();        
    }
    
    /**
     * This method verifies the viewCloudSyncInfo link is present or not.
     * @return boolean
     */
    public boolean isViewCloudSyncInfoLinkPresent()
    {
        try
        {
            WebElement viewCloudSync = findAndWait(By.cssSelector("img[title='Click to view sync info']"));
            return viewCloudSync.isDisplayed();
        }
        catch(TimeoutException e) { }
        
        return false;
    }
    
    /**
     * This method clicks on the viewCloudSyncInfo link.
     * @return SyncInfoPage
     */
    public SyncInfoPage clickOnViewCloudSyncInfo()
    {
        try
        {
            findAndWait(SYNC_INFO_PAGE).click();
            return new SyncInfoPage(getDrone());
        }
        catch(TimeoutException e) 
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
        }
        throw new PageException("Not able to click on view cloud sync info link.");
    }

    /**
     * Selects the "Inline Edit" link on the select data row on
     * DocumentLibrary Page.
     *
     * @return {@link InlineEditPage} response
     */
    public HtmlPage selectInlineEdit()
    {
        selectMoreAction().click();
        WebElement inLineEdit = findAndWait(INLINE_EDIT_LINK);
        inLineEdit.click();
        return new InlineEditPage(drone);
    }
    
    /**
     * This method clicks on the viewCloudSyncInfo link.
     * @return SyncInfoPage
     */
    public String getCloudSyncType()
    {
        try
        {
            return findAndWait(SYNC_INFO_PAGE).getAttribute("title");           
        }
        catch(TimeoutException e) 
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
        }
        throw new PageException("Not able to click on view cloud sync info link.");
    }
    
  
    /**
     * Retrieve content info (This document is locked by you., This document is locked by you for offline editing., Last sync failed.)
     * @return
     */
    public String getContentInfo()
    {
        try
        {
            return findAndWait(INFO_BANNER).getText();
        }
        catch(TimeoutException e) 
        {
            logger.error("Exceeded the time to find Info banner.");
        }
        return "";
    }

    /**
     * Method to check if the content is locked or not
     * @return
     */
    public boolean isLocked()
    {
        try
        {
            return findAndWait(LOCK_ICON).isDisplayed();
        }
        catch (TimeoutException te)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Lock icon is not displayed");
            }
        }
        return false;
    }

    /**
     * Method to check if Inline Edit Link is displayed or not
     *
     * @return true if visible on the page
     */
    public boolean isInlineEditLinkPresent()
    {
        try
        {
            selectMoreAction().click();
            findAndWait(MORE_ACTIONS_MENU);
            return find(INLINE_EDIT_LINK).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Inline Edit link is not displayed");
            }
        }
        catch (TimeoutException e) {}
        return false;
    }

    /**
     * Method to check if Edit Offline Link is displayed or not
     *
     * @return true if visible on the page
     */
    public boolean isEditOfflineLinkPresent()
    {
        try
        {
            selectMoreAction().click();
            findAndWait(MORE_ACTIONS_MENU);
            return find(EDIT_OFFLINE_LINK).isDisplayed();
        }
         catch (NoSuchElementException te)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Edit Offline link is not displayed");
            }
        }
        catch (TimeoutException e) {}

        return false;
    }
   
    /**
     * This method verifies the editInGoogleDocs link is present or not.
     * @return boolean
     */
    public boolean isEditInGoogleDocsPresent()
    {
        try
        {
            selectMoreAction().click();
            WebElement editInGoogleDocs = findAndWait(By.cssSelector("div.google-docs-edit-action-link a"));
            return editInGoogleDocs.isDisplayed();
        }
        catch (NoSuchElementException e) {}
        catch (TimeoutException e) {}

        return false;
    }
    
    /**
     * This method verifies the delete link is present or not.
     * @return boolean
     */
    public boolean isDeletePresent()
    {
        try
        {
            selectMoreAction().click();
            WebElement deleteLink = findAndWait(By.cssSelector("div[class$='delete'] a"));
            return deleteLink.isDisplayed();
        }
        catch (NoSuchElementException e) {}
        catch (TimeoutException e) {}
        
        return false;
    }
    
    /**
     * Select the link manage rules from
     * the actions drop down.
     */
    public HtmlPage selectManageRules()
    {
        WebElement contentActions = selectMoreAction();
        contentActions.click();
        WebElement btn = drone.find(By.cssSelector("div.folder-manage-rules > a"));
        btn.click();
        return FactorySharePage.resolvePage(drone);
    }
        
    /**
     * Check "UnSync to Cloud" link on the select data row.
     * DocumentLibrary Page.
     * @author nshah
     * @return {@link DestinationAndAssigneePage} response
     */
    public boolean isUnSyncFromCloudLinkPresent()
    {
        selectMoreAction().click();
        return drone.isElementDisplayed(By.cssSelector("div#onActionCloudUnsync>a[title='Unsync from Cloud']"));
    }

    /**
     * Verify if the Sync failed icon is displayed or not
     * @param waitTime
     * @return
     */
    public boolean isSyncFailedIconPresent(long waitTime)
    {
        try
        {
            return drone.findAndWaitWithRefresh(SYNC_FAILED_ICON, waitTime).isDisplayed();
        }
        catch (TimeoutException e)
        {
            if(logger.isInfoEnabled())
            {
                logger.info("Sync failed icon is not displayed");
            }
        }
        return false;
    }

    /**
     * Select the link Request Sync from
     * the actions drop down.
     */
    public DocumentLibraryPage selectRequestSync()
    {
        try
        {
            WebElement contentActions = selectMoreAction();
            contentActions.click();
            WebElement btn = drone.find(REQUEST_TO_SYNC);
            btn.click();
            return new DocumentLibraryPage(drone);
        }
        catch (NoSuchElementException e)
        {
            if(logger.isInfoEnabled())
            {
                logger.info("Request Sync link is not displayed");
            }
        }
        throw  new PageException("Unable to select Request Sync option");
    }

    
    /**
     * Request to sync is present or not.
     * the actions drop down.
     */
    public boolean isRequestToSyncLinkPresent()
    {
        try
        {
            WebElement contentActions = selectMoreAction();
            contentActions.click();
            return drone.isElementDisplayed(REQUEST_TO_SYNC) ? true: false;         
        }
        catch (NoSuchElementException e)
        {
            if(logger.isInfoEnabled())
            {
                logger.info("Request Sync link element is not present");
            }
        }
        throw  new PageException("Request Sync link element is not present");
    }
    /**
     * Check "Sync to Cloud" link on the select data row.
     * DocumentLibrary Page.
     * @author rmanyam
     * @return
     */
    public boolean isSyncToCloudLinkPresent()
    {
        try
        {
            WebElement actions = findElement(By.cssSelector(CONTENT_ACTIONS));
            getDrone().mouseOverOnElement(actions);
            selectMoreAction().click();
            return drone.findAndWait(By.cssSelector("div#onActionCloudSync a")).isDisplayed();
        }
        catch (NoSuchElementException nse) { }
        catch (TimeoutException nse) { }
        return false;
    }
        
    /**
     * select Manage permission link from more option of document library.
     * @return
     */
    public ManagePermissionsPage selectManagePermission()
    {
        try
        {
            selectMoreAction().click();
            WebElement managePermissionLink = drone.findAndWait(By.cssSelector("div.document-manage-granular-permissions > a"));         
            managePermissionLink.click();
            return new ManagePermissionsPage(drone);
        }
        catch (NoSuchElementException nse) {}
        catch (TimeoutException exception)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Manage permission link is not displayed for selected data row");
            }
        }
        throw new PageOperationException("Manage permission link is not displayed for selected data row");
    }
    
    /**
     * select Copy to... link from more option of document library.
     * 
     * @return CopyOrMoveContentPage
     */
    public CopyOrMoveContentPage selectCopyTo()
    {
        try
        {
            selectMoreAction().click();
            WebElement copyToLink = drone.findAndWait(By.linkText("Copy to..."));
            copyToLink.click();
            return new CopyOrMoveContentPage(drone);
        }
        catch (NoSuchElementException nse) {}
        catch (TimeoutException exception)
        {
            logger.error("Copy to link is not displayed for selected data row");
        }
        throw new PageOperationException("Copy to link is not displayed for selected data row");
    }
    
    /**
     * select Move to... link from more option of document library.
     * 
     * @return CopyOrMoveContentPage
     */
    public CopyOrMoveContentPage selectMoveTo()
    {
        try
        {
            selectMoreAction().click();
            WebElement copyToLink = drone.findAndWait(By.linkText("Move to..."));
            copyToLink.click();
            return new CopyOrMoveContentPage(drone);
        }
        catch (NoSuchElementException nse) {}
        catch (TimeoutException exception)
        {
            logger.error("Move to link is not displayed for selected data row");
        }
        throw new PageOperationException("Move to link is not displayed for selected data row");
    }

    /**
     * Select delete action
     * @return page response
     */
    public HtmlPage delete()
    {
        selectDelete();
        confirmDelete();
        return FactorySharePage.resolvePage(drone);
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
}