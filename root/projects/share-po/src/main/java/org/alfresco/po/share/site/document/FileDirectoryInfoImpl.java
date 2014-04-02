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
import java.util.Set;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.lang3.StringUtils;
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
 * @author Shan Nagarajan
 */
public abstract class FileDirectoryInfoImpl extends HtmlElement implements FileDirectoryInfo
{

    private static final By BLACK_MESSAGE = By.cssSelector("div#message>div.bd>span");

    private static final By CLOUD_SYNC_LINK = By.cssSelector("div#onActionCloudSync a");

    private static Log logger = LogFactory.getLog(FileDirectoryInfoImpl.class);

    private static final String CLOUD_SYNC_ICON = "a[data-action='onCloudSyncIndicatorAction']";
    private static final String EDITED_ICON = "img[alt='editing']";
    private static final String WORKFLOW_ICON = "img[alt='active-workflows']";
    protected String FILE_DESC_IDENTIFIER = "td.yui-dt-col-fileName div.yui-dt-liner div:nth-of-type(2)";
    private static final String TAG_INFO = "span[title='Tag'] + form + span.item";
    private static final String TAG_COLLECTION = TAG_INFO + " > span.tag > a";
    private static final String ADD_TAG = "span[title='Tag']";
    @SuppressWarnings("unused")
    private static final String TAG_NAME = "a.tag-link";
    private static final String IMG_FOLDER = "/documentlibrary/images/folder";
    private static final String FAVOURITE_CONTENT = "a.favourite-action";
    private static final String LIKE_CONTENT = "a.like-action";
    private static final String LIKE_COUNT = "span.likes-count";
    private static final String CONTENT_NODEREF = "h3.filename form";
    protected static String ACTIONS_MENU = "td:nth-of-type(5)";
    private static final String ACTIONS_LIST = "div.action-set>div";
    protected String TITLE = "span.title";
    private static final String CLOUD_REMOVE_TAG = "img[src$='delete-item-off.png']";
    private static final String ENTERPRISE_REMOVE_TAG = "img[src$='delete-tag-off.png']";
    private static final String SELECT_CHECKBOX = "input[id^='checkbox-yui']";
    protected By TAG_LINK_LOCATOR = By.cssSelector("div.yui-dt-liner>div>span>span>a.tag-link");
    private static final By SYNC_INFO_PAGE = By.cssSelector("a[data-action='onCloudSyncIndicatorAction']>img[alt='cloud-synced'], img[alt='cloud-indirect-sync']");
    private static final By INFO_BANNER = By.cssSelector("div.info-banner");
    private static final By LOCK_ICON = By.cssSelector("img[alt='lock-owner']");
    private static final By SYNC_FAILED_ICON = By.cssSelector("img[alt='cloud-sync-failed']");
    protected static final By INLINE_EDIT_LINK = By.cssSelector("div.document-inline-edit>a[title='Inline Edit']>span");
    protected static final By EDIT_OFFLINE_LINK = By.cssSelector("div.document-edit-offline>a[title='Edit Offline']>span");
    protected static final By MORE_ACTIONS_MENU = By.cssSelector("div.more-actions");
    protected String THUMBNAIL = "td.yui-dt-col-thumbnail>div>span>a";
    protected String THUMBNAIL_TYPE = "td.yui-dt-col-thumbnail>div>span";
    protected final By REQUEST_TO_SYNC = By.cssSelector("div#onActionCloudSyncRequest>a[title='Request Sync']");
    protected final String LINK_MANAGE_PERMISSION = "div[class$='-permissions']>a";
    protected final long WAIT_TIME_3000 = 3000;
    protected String INPUT_TAG_NAME = "div.inlineTagEdit input";
    protected String INPUT_CONTENT_NAME = "input[name='prop_cm_name']";
    protected String nodeRef;
    protected String INLINE_TAGS = "div.inlineTagEdit>span>span.inlineTagEditTag";
    protected String GOOGLE_DOCS_URL = "googledocsEditor?";
    protected String FILENAME_IDENTIFIER = "h3.filename a";
    protected String DOWNLOAD_DOCUMENT = "div.document-download>a";
    protected String EDIT_CONTENT_NAME_ICON = "span[title='Rename']";
    protected String DOWNLOAD_FOLDER = "div.folder-download>a";
    protected String rowElementXPath = null;
    protected String MORE_ACTIONS;
    private static final By COMMENT_LINK = By.cssSelector("a.comment");
    private static final By QUICK_SHARE_LINK = By.cssSelector("a.quickshare-action");
    protected static final By VIEW_IN_BROWsER_ICON = By.cssSelector("div.document-view-content>a");

    private static final By EDIT_PROP_ICON = By.cssSelector("div.document-edit-properties>a");
    
    public FileDirectoryInfoImpl(String nodeRef,WebElement webElement, WebDrone drone)
    {
        super(webElement, drone);
        if(nodeRef == null)
        {
            throw new IllegalArgumentException("NodeRef is required");
        }
        this.nodeRef = nodeRef;
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getName()
     */
    @Override
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


    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTitle()
     */
    @Override
    public void clickOnTitle()
    {
        findAndWait(By.cssSelector(FILENAME_IDENTIFIER)).click();
        domEventCompleted();
    }


    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isTypeFolder()
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getDescription()
     */
    @Override
    public String getDescription()
    {
        try
        {
            return findAndWait(By.cssSelector(FILE_DESC_IDENTIFIER)).getText();
        }
        catch (TimeoutException te) { }
        return "";
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getContentEditInfo()
     */
    @Override
    public String getContentEditInfo()
    {
        return findAndWait(By.cssSelector("h3.filename+div.detail>span")).getText();
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getTags()
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getCategories()
     */
    @Override
    public List<Categories> getCategories()
    {
        List<Categories> categories = new ArrayList<Categories>();
        try
        {
            List<WebElement> categoryElements = findElements(By.cssSelector(".category>a"));
            for (WebElement webElement : categoryElements)
            {
                categories.add(Categories.getCategory(webElement.getText()));
            }
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Not able to find categories" + e.getMessage());
        }
        return categories;
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDelete()
     */
    @Override
    public ConfirmDeletePage selectDelete()
    {
        try
        {
            WebElement deleteLink = findElement(By.cssSelector("div[class$='delete'] a"));
            deleteLink.click();
        }
        catch (NoSuchElementException e) 
        {
            throw new PageOperationException(e.getMessage());
        }
        catch (StaleElementReferenceException st)
        {
            throw new StaleElementReferenceException(st.getMessage());
        }
        return new ConfirmDeletePage(drone);
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditProperties()
     */
    @Override
    public EditDocumentPropertiesPopup selectEditProperties()
    {
        WebElement editProperties = findElement(EDIT_PROP_ICON);
        editProperties.click();
        return new EditDocumentPropertiesPopup(getDrone());
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewInBrowser()
     */
    @Override
    public void selectViewInBrowser()
    {
        WebElement viewInBrowser = findElement(VIEW_IN_BROWsER_ICON);
        viewInBrowser.click();
        
        Set<String> winSet = getDrone().getWindowHandles();
        List<String> winList = new ArrayList<String>(winSet);
        String newTab = winList.get(winList.size() - 1);
        //close the original tab
        getDrone().closeWindow();
        //switch to new tab
        getDrone().switchToWindow(newTab);
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectFavourite()
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectLike()
     */
    @Override
    public void selectLike()
    {
        findElement(By.cssSelector(LIKE_CONTENT)).click();
        domEventCompleted();
    }
    
    /**
     * Gets the Like option tool tip on the select data row on
     * DocumentLibrary Page.
     */
    @Override
    public String getLikeOrUnlikeTip()
    {
        return findElement(By.cssSelector(LIKE_CONTENT)).getAttribute("title");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLiked()
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isFavourite()
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getLikeCount()
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#hasTags()
     */
    @Override
    public boolean hasTags()
    {
        try
        {
            List<WebElement> tagList = findElements(By.cssSelector(TAG_COLLECTION));
            if (tagList.size() > 0)
            {
                return true;
            }
        }
        catch (NoSuchElementException nse) { }
        return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#addTag(java.lang.String)
     */
    @Override
    public void addTag(final String tagName)
    {
        if(tagName == null || tagName.isEmpty())
        {
            throw new IllegalArgumentException("Tag Name is required");
        }
        try
        {
            clickOnAddTag();
            enterTagString(tagName);
            findAndWait(By.linkText("Save")).click();
            domEventCompleted();
         }
         catch (TimeoutException te)
         {
             logger.error("Error adding tag: ", te);
             throw new PageException("Error While adding tag: " + tagName, te);
         }
    }

    @Override
    public void enterTagString(final String tagName)
    {
        WebElement inputTagName = findAndWait(By.cssSelector(INPUT_TAG_NAME));
        inputTagName.clear();
        inputTagName.sendKeys(tagName + "\n");
    }

    /**
     * Get NodeRef for the content on the selected data row on DocumentLibrary
     * Page.
     * 
     * @return {String} Node Ref / GUID
     */
    @Override
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


    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getTitle()
     */
    @Override
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
                throw new PageException("Exceeded the time to find css.", e);
        }

    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnAddTag()
     */
    @Override
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
                if(findElement(By.cssSelector(INPUT_TAG_NAME)).isDisplayed())
                {
                    break;
                }
            }
            catch (NoSuchElementException e){}
            catch (ElementNotVisibleException e2){}
            catch (StaleElementReferenceException stale){}
            finally { timer.end(); }
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#removeTagButtonIsDisplayed(java.lang.String)
     */
    @Override
    public boolean removeTagButtonIsDisplayed(String tagName)
    {
        if (tagName == null)
        {
            throw new IllegalArgumentException("tagName is required.");
        }
        try
        {
            return getRemoveTagButton(tagName).isDisplayed();
        }
        catch (Exception e){ }
        return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagRemoveButton(java.lang.String)
     */
    @Override
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
            throw new PageException("Unable to find the remove tag button.", e);
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
                    AlfrescoVersion version = getDrone().getProperties().getVersion();
                    String selector = version.isDojoSupported()  ? CLOUD_REMOVE_TAG : ENTERPRISE_REMOVE_TAG;
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagSaveButton()
     */
    @Override
    public void clickOnTagSaveButton()
    {
        try
        {
            findAndWait(By.linkText("Save")).click();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Save button css." + ex.getMessage());
            throw new PageException("Exceeded time to find the Save button css.", ex);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagCancelButton()
     */
    @Override
    public void clickOnTagCancelButton()
    {
        try
        {
            findAndWait(By.linkText("Cancel")).click();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Save button css." + ex.getMessage());
            throw new PageException("Exceeded time to find the Save button css.", ex);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCheckbox()
     */
    @Override
    public void selectCheckbox()
    {
        findElement(By.cssSelector(SELECT_CHECKBOX)).click();
        domEventCompleted();
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isCheckboxSelected()
     */
    @Override
    public boolean isCheckboxSelected()
    {
        try
        {
            return findElement(By.cssSelector(SELECT_CHECKBOX)).isSelected();
        }
        catch(NoSuchElementException nse){ }
        return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectThumbnail()
     */
    @Override
    public HtmlPage selectThumbnail()
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
     * Returns true if content in the selected data row on DocumentLibrary is
     * folder Page.
     * 
     * @return {boolean} <tt>true</tt> if the content is of type folder.
     */
    @Override
    public boolean isFolder()
    {
        try
        {
            WebElement thumbnailType = findElement(By.cssSelector(THUMBNAIL_TYPE));
            return thumbnailType.getAttribute("class").contains("folder");
        }
        catch (Exception e){ }
        return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isCloudSynced()
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isPartOfWorkflow()
     */
    @Override
    public boolean isPartOfWorkflow()
    {
        try
        {
            WebElement thumbnailType = find(By.cssSelector(WORKFLOW_ICON));
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
     * Clicks on the download folder as a zip button from the action menu
     */
    public void downloadFolderAsZip()
    {
        if(!isFolder())
        {
            throw new UnsupportedOperationException("Download folder as zip is available for folders only.");
        }
        try
        {
            WebElement menuOption = findElement(By.cssSelector(DOWNLOAD_FOLDER));
            menuOption.click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to click download folder as a zip");
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDownload()
     */
    @Override
    public void selectDownload()
    {
        WebElement menuOption = findElement(By.cssSelector(DOWNLOAD_DOCUMENT));
        menuOption.click();
        // Assumes driver capability settings to save file in a specific location when
        // <Download> option is selected via Browser
    }
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getNodeRef()
     */
    @Override
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
    protected void resolveStaleness() 
    {
        if(nodeRef == null || nodeRef.isEmpty())
        {
            throw new UnsupportedOperationException(String.format("Content noderef is required: %s",nodeRef));
        }
        try
        {
            WebElement element = getDrone().findAndWait(By.cssSelector(String.format("input[value='%s']",nodeRef)));
            WebElement row = element.findElement(By.xpath("../../.."));
            if(row.getAttribute("class").contains("alf-gallery-item-thumbnail"))
            {
                 row = element.findElement(By.xpath(rowElementXPath));
                 setWebElement(row);
            }
            setWebElement(row);
        }
        catch (TimeoutException te)
        {
            throw new UnsupportedOperationException("there are no elements matching the node ref : " + nodeRef);
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

    /*
     * @see org.alfresco.webdrone.HtmlElement#findElement(org.openqa.selenium.By)
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewFolderDetails()
     */
    @Override
    public FolderDetailsPage selectViewFolderDetails()
    {
        WebElement menuOption = findAndWait(By.cssSelector("div.folder-view-details>a"));
        menuOption.click();

        return new FolderDetailsPage(getDrone());
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagNameLink(java.lang.String)
     */
    @Override
    public DocumentLibraryPage clickOnTagNameLink(String tagName)
    {
        if (tagName == null)
        {
            throw new UnsupportedOperationException("Drone and TagName is required.");
        }

        try
        {
            List<WebElement> tagList = findAllWithWait(TAG_LINK_LOCATOR);
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
            throw new PageException("Exceeded the time to find css.", e);
        }
        throw new PageException("Not able to tag name: " + tagName);
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectSyncToCloud()
     */
    @Override
    public HtmlPage selectSyncToCloud()
    {
        try
        {
            WebElement syncToCloud = findAndWait(CLOUD_SYNC_LINK);
            if(syncToCloud.isEnabled())
            {
                syncToCloud.click();
                drone.waitUntilElementDisappears(CLOUD_SYNC_LINK, 1);
                if(isSignUpDialogVisible())
                {
                    return new CloudSignInPage(getDrone());
                }
                else
                {
                    return new DestinationAndAssigneePage(getDrone());
                }
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditInGoogleDocs()
     */
    @Override
    public HtmlPage selectEditInGoogleDocs()
    {
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
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isSignUpDialogVisible()
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectUnSyncAndRemoveContentFromCloud(boolean)
     */
    @Override
    public DocumentLibraryPage selectUnSyncAndRemoveContentFromCloud(boolean doRemoveContentOnCloud)
    {
        selectUnSyncFromCloud();
        if(doRemoveContentOnCloud)
        {
            getDrone().findAndWait(By.cssSelector(".requestDeleteRemote-checkBox")).click();
        }
        List<WebElement> buttonElements = getDrone().findAndWaitForElements(By.cssSelector("div>span.button-group>span>span.first-child"));
        for (WebElement webElement : buttonElements)
        {
            if("Remove sync".equals(webElement.getText()))
            {
                webElement.click();
                drone.waitUntilElementPresent(BLACK_MESSAGE, SECONDS.convert(maxTime, MILLISECONDS));
                drone.waitUntilElementDeletedFromDom(BLACK_MESSAGE, SECONDS.convert(maxTime, MILLISECONDS));
                break;
            }
        }
        return new DocumentLibraryPage(getDrone());
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectUnSyncFromCloud()
     */
    @Override
    public void selectUnSyncFromCloud()
    {
    	WebElement unSyncToCloud = findAndWait(By.cssSelector("div#onActionCloudUnsync>a[title='Unsync from Cloud']"));
        unSyncToCloud.click();        
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isViewCloudSyncInfoLinkPresent()
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnViewCloudSyncInfo()
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectInlineEdit()
     */
    @Override
    public HtmlPage selectInlineEdit()
    {
        WebElement inLineEdit = findAndWait(INLINE_EDIT_LINK);
        inLineEdit.click();
        return new InlineEditPage(drone);
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getCloudSyncType()
     */
    @Override
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


    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getContentInfo()
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLocked()
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isInlineEditLinkPresent()
     */
    @Override
    public boolean isInlineEditLinkPresent()
    {
        try
        {
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditOfflineLinkPresent()
     */
    @Override
    public boolean isEditOfflineLinkPresent()
    {
        try
        {
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditInGoogleDocsPresent()
     */
    @Override
    public boolean isEditInGoogleDocsPresent()
    {
        try
        {
            WebElement editInGoogleDocs = findAndWait(By.cssSelector("div.google-docs-edit-action-link a"));
            return editInGoogleDocs.isDisplayed();
        }
        catch (NoSuchElementException e) {}
        catch (TimeoutException e) {}

        return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isDeletePresent()
     */
    @Override
    public boolean isDeletePresent()
    {
        try
        {
            WebElement deleteLink = findAndWait(By.cssSelector("div[class$='delete'] a"));
            return deleteLink.isDisplayed();
        }
        catch (NoSuchElementException e) {}
        catch (TimeoutException e) {}

        return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageRules()
     */
    @Override
    public HtmlPage selectManageRules()
    {
        WebElement btn = drone.find(By.cssSelector("div.folder-manage-rules > a"));
        btn.click();
        return drone.getCurrentPage();
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isUnSyncFromCloudLinkPresent()
     */
    @Override
    public boolean isUnSyncFromCloudLinkPresent()
    {
        try
        {
            drone.findAndWait(By.cssSelector("div#onActionCloudUnsync>a[title='Unsync from Cloud']"), WAIT_TIME_3000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            if(logger.isInfoEnabled())
            {
                logger.info("UnSync From Cloud Link is not displayed");
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isSyncFailedIconPresent(long)
     */
    @Override
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectRequestSync()
     */
    @Override
    public DocumentLibraryPage selectRequestSync()
    {
        try
        {
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


    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isRequestToSyncLinkPresent()
     */
    @Override
    public boolean isRequestToSyncLinkPresent()
    {
        try
        {
            return drone.find(REQUEST_TO_SYNC).isDisplayed();         
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
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isSyncToCloudLinkPresent()
     */
    @Override
    public boolean isSyncToCloudLinkPresent()
    {
        try
        {
            return drone.findAndWait(CLOUD_SYNC_LINK, WAIT_TIME_3000).isDisplayed();
        }
        catch (NoSuchElementException nse) { }
        catch (TimeoutException nse)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Unable to find \"Sync to Cloud\" option");
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManagePermission()
     */
    @Override
    public ManagePermissionsPage selectManagePermission()
    {
        try
        {
            WebElement managePermissionLink = findAndWait(By.cssSelector(LINK_MANAGE_PERMISSION));         
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

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCopyTo()
     */
    @Override
    public CopyOrMoveContentPage selectCopyTo()
    {
    	return selectCopyOrMoveTo("Copy to...");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectMoveTo()
     */
    @Override
    public CopyOrMoveContentPage selectMoveTo()
    {
    	return selectCopyOrMoveTo("Move to...");
    }

    private CopyOrMoveContentPage selectCopyOrMoveTo(String linkText)
    {
        try
        {
            WebElement copyToLink = findAndWait(By.linkText(linkText));
            copyToLink.click();
            return new CopyOrMoveContentPage(drone);
        }
        catch (NoSuchElementException nse) {}
        catch (TimeoutException exception)
        {
            logger.error( linkText + " link is not displayed for selected data row");
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectCopyOrMoveTo(linkText);
        }
        throw new PageOperationException(linkText + " link is not displayed for selected data row");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#delete()
     */
    @Override
    public HtmlPage delete()
    {
        selectDelete();
        confirmDelete();
        return drone.getCurrentPage();
    }
    /**
     * Action of selecting ok on confirm delete pop up dialog.
     */
    public void confirmDelete()
    {
        WebElement confirmDelete = drone.find(By.cssSelector("div#prompt div.ft span span button"));
        confirmDelete.click();
        if (logger.isTraceEnabled())
        {
            logger.trace("deleting");
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectStartWorkFlow()
     */
    @Override
    public StartWorkFlowPage selectStartWorkFlow()
    {
        try
        {
            WebElement startWorkFlow = findAndWait(By.linkText(drone.getValue("start.workflow.link.text")));
            startWorkFlow.click();
            return new StartWorkFlowPage(drone);
        }
        catch (NoSuchElementException nse) {}
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element" + exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectStartWorkFlow();
        }
        throw new PageException("Unable to find assign workflow.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectUploadNewVersion()
     */
    
    @Override
    public UpdateFilePage selectUploadNewVersion()
    {
        try
        {
            WebElement uploadNewVersionLink = findElement(By.cssSelector("div[class$='document-upload-new-version'] a"));
            uploadNewVersionLink.click();
        }
        catch (NoSuchElementException e) { }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectUploadNewVersion();
        }
        //TODO add version
        return new UpdateFilePage(drone, "");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isManagePermissionLinkPresent()
     */
    @Override
    public boolean isManagePermissionLinkPresent()
    {
        try
        {
            return drone.find(By.cssSelector(LINK_MANAGE_PERMISSION)).isDisplayed();         
            
        }
        catch (NoSuchElementException nse)
        {
           if (logger.isTraceEnabled())
            {
                logger.trace("Manage permission link is not displayed for selected data row");
            }
        }
        return false;
    }


    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditPropertiesLinkPresent()
     */
    @Override
    public boolean isEditPropertiesLinkPresent()
    {
        try
        {
            return findAndWait(By.cssSelector("div.document-edit-properties>a")).isDisplayed();            
        }
        catch (TimeoutException nse) 
        {
           if (logger.isTraceEnabled())
            {
                logger.trace("Edit properties link is not displayed for selected data row");
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditOffline()
     */
    @Override
    public DocumentLibraryPage selectEditOffline()
    {
        try
        {
            WebElement cancelEditing = findAndWait(By.linkText(drone.getValue("edit.offline.link.text")));
            cancelEditing.click();
            waitUntilMessageAppearAndDisappear("edited");
            return new DocumentLibraryPage(drone);
        }
        catch (NoSuchElementException nse) {}
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element" + exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectEditOffline();
        }
        throw new PageException("Unable to find Edit Offline link");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCancelEditing()
     */
    @Override
    public DocumentLibraryPage selectCancelEditing()
    {
        try
        {
            WebElement cancelEditing = findAndWait(By.linkText(drone.getValue("cancel.editing.link.text")));
            cancelEditing.click();
            waitUntilMessageAppearAndDisappear("cancelled.");
            return new DocumentLibraryPage(drone);
        }
        catch (NoSuchElementException nse) {}
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element" + exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectCancelEditing();
        }
        throw new PageException("Unable to find Cancel Editing link");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEdited()
     */
    @Override
    public boolean isEdited()
    {
        try
        {
            return find(By.cssSelector(EDITED_ICON)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageAspects()
     */
    @Override
    public SelectAspectsPage selectManageAspects()
    {
        try
        {
            WebElement manageAspectLink = findElement(By.cssSelector("div[class$='document-manage-aspects'] a"));
            manageAspectLink.click();
        }
        catch (NoSuchElementException e) { }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectManageAspects();
        }
        return new SelectAspectsPage(drone);
    }
    
    /**
     * Wait until the black message box appear with text then wait until same black message disappear with text.
     *
     * @param text - Text to be checked in the black message.
     */
    protected void waitUntilMessageAppearAndDisappear(String text)
    {
    	long defaultWaitTime = ((WebDroneImpl)drone).getDefaultWaitTime();
        waitUntilMessageAppearAndDisappear(text, SECONDS.convert(defaultWaitTime, MILLISECONDS));
    }

    /**
     * Wait until the black message box appear with text then wait until same black message disappear with text.
     *
     * @param text - Text to be checked in the black message.
     * @param timeInSeconds - Time to wait in seconds.
     */
    protected void waitUntilMessageAppearAndDisappear(String text, long timeInSeconds)
    {
        drone.waitUntilVisible(By.cssSelector("div.bd>span.message"), text, timeInSeconds);
        drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd>span.message"), text, timeInSeconds);
    }
    
    /**
     * Check if comment link is present.
     * @return
     */
    public boolean isCommentLinkPresent()
    {
        try
        {
            WebElement commentLink = find(COMMENT_LINK);
            return commentLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            // no log needed due to negative cases.
        }
        catch (Exception ex)
        {
            // no log needed due to negative cases.
        }
        return false;        
          
    }

    /**
     * Check if quick share link is present.
     * 
     * @return
     */
    @Override
    public boolean isShareLinkVisible()
    {
        try
        {
            WebElement shareLink = findAndWait(QUICK_SHARE_LINK);

            return shareLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            // no log needed due to negative cases.
        }
        catch (TimeoutException ex)
        {
            // no log needed due to negative cases.
        }
        catch (Exception ex)
        {
            // no log needed due to negative cases.
        }
        return false;
    }

    @Override
    public boolean isViewInBrowserVisible()
    {
        try
        {
            WebElement icon = find(VIEW_IN_BROWsER_ICON);

            return icon.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            // no log needed due to negative cases.
        }
        catch (TimeoutException ex)
        {
            // no log needed due to negative cases.
        }
        catch (Exception ex)
        {
            // no log needed due to negative cases.
        }
        return false;
    }

    @Override
    public void contentNameEnableEdit()
    {
        // hover over tag area
        RenderTime timer = new RenderTime(((WebDroneImpl) getDrone()).getMaxPageRenderWaitTime() * 2);
        while (true)
        {
            try
            {
                timer.start();
                WebElement contentNameLink = findAndWait(By.cssSelector(FILENAME_IDENTIFIER));
                getDrone().mouseOver(contentNameLink);
                resolveStaleness();
                // Wait till pencil icon appears
                WebElement editIcon = findElement(By.cssSelector(EDIT_CONTENT_NAME_ICON));
                // Select to get focus
                editIcon.click();
                if (findElement(By.cssSelector(INPUT_CONTENT_NAME)).isDisplayed())
                    break;
            }
            catch (NoSuchElementException e)
            {
            }
            catch (ElementNotVisibleException e2)
            {
            }
            catch (StaleElementReferenceException stale)
            {
            }
            catch (TimeoutException stale)
            {
            }
            finally
            {
                timer.end();
            }
        }
    }

    @Override
    public void contentNameEnter(String newContentName)
    {
        try
        {
            WebElement inputBox = findElement(By.cssSelector(INPUT_CONTENT_NAME));
            if (inputBox.isDisplayed())
            {
                WebElement inputCOntentName = findAndWait(By.cssSelector(INPUT_CONTENT_NAME));
                inputCOntentName.clear();
                inputCOntentName.sendKeys(newContentName);
                return;
            }
            else
            {
                throw new PageException("Input is not displayed displayed");
            }
        }
        catch (NoSuchElementException e)
        {
            logger.error("Input should be displayed" + e.getMessage());
            throw new PageException("Input should be displayed", e);
        }
        
    }

    @Override
    public void contentNameClickSave()
    {
        try
        {
            WebElement inputBox = findElement(By.cssSelector(INPUT_CONTENT_NAME));
            if (inputBox.isDisplayed())
            {
                findAndWait(By.linkText("Save")).click();
            }
            else
            {
                throw new PageException("Input is not displayed displayed");
            }
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Save button css." + ex.getMessage());
            throw new PageException("Exceeded time to find the Save button css.", ex);
        }

    }

    @Override
    public void contentNameClickCancel()
    {
        try
        {
            WebElement inputBox = findElement(By.cssSelector(INPUT_CONTENT_NAME));
            if (inputBox.isDisplayed())
            {
                findAndWait(By.linkText("Cancel")).click();
            }
            else
            {
                throw new PageException("Input is not displayed displayed");
            }
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Save button css." + ex.getMessage());
            throw new PageException("Exceeded time to find the Save button css.", ex);
        }
    }

    @Override
    public void renameContent(String newContentName)
    {
        if (StringUtils.isEmpty(newContentName))
        {
            throw new IllegalArgumentException("Content name is required");
        }
        try
        {
            contentNameEnableEdit();
            contentNameEnter(newContentName);
            contentNameClickSave();
        }
        catch (TimeoutException e)
        {
            logger.error("Error renaming content: ", e);
            throw new PageException("Error While renaming content: " + newContentName);
        }
    }

}