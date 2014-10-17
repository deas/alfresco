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

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.exception.AlfrescoVersionException;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
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
 * Entity that models the list of file or directories as it appears on the {@link DocumentLibraryPage}. The list models the HTML element representing
 * the file or directory.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @author mbhave
 */
public abstract class FileDirectoryInfoImpl extends HtmlElement implements FileDirectoryInfo
{

    protected static final By INLINE_EDIT_LINK = By.cssSelector("div.document-inline-edit>a[title='Inline Edit']>span");
    protected static final By EDIT_OFFLINE_LINK = By.cssSelector("div.document-edit-offline>a[title='Edit Offline']>span");
    protected static final By MORE_ACTIONS_MENU = By.cssSelector("div.more-actions");
    protected static final By FILE_VERSION_IDENTIFIER = By.cssSelector("span.document-version");
    protected static final By VIEW_IN_BROWsER_ICON = By.cssSelector("div.document-view-content>a");
    protected static final By CATEGORY_LINK = By.cssSelector("span.category > a");
    private static final By BLACK_MESSAGE = By.cssSelector("div#message>div.bd>span");
    private static final By CLOUD_SYNC_LINK = By.cssSelector("div#onActionCloudSync a");
    private static final String CLOUD_SYNC_ICON = "a[data-action='onCloudSyncIndicatorAction']";
    private static final String EDITED_ICON = "img[alt='editing']";
    private static final String WORKFLOW_ICON = "img[alt='active-workflows']";
    @SuppressWarnings("unused")
    private static final String FILE_EDIT_INFO = "div.yui-dt-liner div:nth-of-type(1)";
    private static final String TAG_INFO = "span[title='Tag'] + form + span.item";
    private static final String TAG_COLLECTION = TAG_INFO + " > span.tag > a";
    private static final String IMG_FOLDER = "/documentlibrary/images/folder";
    private static final String FAVOURITE_CONTENT = "a[class*='favourite-action']";
    private static final String LIKE_CONTENT = "a[class*='like-action']";
    private static final String LIKE_COUNT = "span.likes-count";
    private static final String CONTENT_NODEREF = "h3.filename form";
    private static final String ACTIONS_LIST = "div.action-set>div";
    private static final String RULES_ICON = "img[alt='rules']";
    private static final String CLOUD_REMOVE_TAG = "img[src$='delete-item-off.png']";
    private static final String ENTERPRISE_REMOVE_TAG = "img[src$='delete-tag-off.png']";
    private static final String SELECT_CHECKBOX = "input[id^='checkbox-yui']";
    private static final By SYNC_INFO_PAGE = By
            .cssSelector("a[data-action='onCloudSyncIndicatorAction']>img[alt='cloud-synced'], img[alt='cloud-indirect-sync']");
    private static final By INFO_BANNER = By.cssSelector("div.info-banner");
    private static final By LOCK_ICON = By.cssSelector("img[alt='lock-owner']");
    private static final By SYNC_FAILED_ICON = By.cssSelector("img[alt='cloud-sync-failed']");
    private static final By COMMENT_LINK = By.cssSelector("a.comment");
    private static final By QUICK_SHARE_LINK = By.cssSelector("a.quickshare-action");
    private static final By EDIT_PROP_ICON = By.cssSelector("div.document-edit-properties>a");
    private static final By CREATE_TASK_WORKFLOW = By.cssSelector("div.document-assign-workflow>a");
    protected static String ACTIONS_MENU = "td:nth-of-type(5)";
    private static Log logger = LogFactory.getLog(FileDirectoryInfoImpl.class);
    protected final By REQUEST_TO_SYNC = By.cssSelector("div#onActionCloudSyncRequest>a[title='Request Sync']");
    protected final String LINK_MANAGE_PERMISSION = "div[class$='-permissions']>a";
    protected final long WAIT_TIME_3000 = 3000;
    protected String FILE_DESC_IDENTIFIER = "td.yui-dt-col-fileName div.yui-dt-liner div:nth-of-type(2)";
    protected String TITLE = "span.title";
    protected By TAG_LINK_LOCATOR = By.cssSelector("div.yui-dt-liner>div>span>span>a.tag-link");
    protected String THUMBNAIL = "td.yui-dt-col-thumbnail>div>span>a";
    protected String THUMBNAIL_TYPE = "td.yui-dt-col-thumbnail>div>span";
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
    protected String VIEW_ORIGINAL_DOCUMENT = "div.document-view-original>a";
    private static final By TAGS_FIELD = By.cssSelector("div.detail span.item span.faded");
    protected String DESCRIPTION_INFO = "div.detail>span.faded";

    protected String LOCATE_FILE = "div.document-locate>a";

    protected By DETAIL_WINDOW = By.xpath("//div[@class='alf-detail-thumbnail']/../../..");

    public FileDirectoryInfoImpl(String nodeRef, WebElement webElement, WebDrone drone)
    {
        super(webElement, drone);
        if (nodeRef == null)
        {
            throw new IllegalArgumentException("NodeRef is required");
        }
        this.nodeRef = nodeRef;
    }

    /*
     * (non-Javadoc)
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

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTitle()
     */
    @Override
    public HtmlPage clickOnTitle()
    {
        try
        {
            findAndWait(By.cssSelector(FILENAME_IDENTIFIER)).click();
            domEventCompleted();
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded the time to find FILENAME_IDENTIFIER", te);
        }

        throw new PageException("Unable to click on content Title.");
    }

    /*
     * (non-Javadoc)
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
            if (path != null && path.contains(IMG_FOLDER))
            {
                isFolder = true;
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return isFolder;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getDescription()
     */
    @Override
    public String getDescription()
    {
        try
        {
            return findAndWait(By.cssSelector(FILE_DESC_IDENTIFIER)).getText();
        }
        catch (TimeoutException te)
        {
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getContentEditInfo()
     */
    @Override
    public String getContentEditInfo()
    {
        return findAndWait(By.cssSelector("h3.filename+div.detail>span")).getText();
    }

    /*
     * (non-Javadoc)
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
            if (logger.isDebugEnabled())
            {
                logger.debug("Timed out while waiting for Tag Information", te);
            }
        }
        return tagsList;
    }

    /*
     * (non-Javadoc)
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
            throw new PageOperationException("Not able to find categories", e);
        }
        return categories;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getCategories()
     */
    @Override
    public List<String> getCategoryList()
    {
        List<String> categories = new ArrayList<>();
        try
        {
            List<WebElement> categoryElements = findElements(By.cssSelector(".category>a"));
            for (WebElement webElement : categoryElements)
            {
                categories.add(webElement.getText());
            }
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Not able to find categories", e);
        }
        return categories;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDelete()
     */
    @Override
    public ConfirmDeletePage selectDelete()
    {
        try
        {
            WebElement deleteLink = findAndWait(By.cssSelector("div[class$='delete'] a"));
            deleteLink.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find the css ", e);
        }
        catch (StaleElementReferenceException st)
        {
            throw new StaleElementReferenceException("Unable to find the css ", st);
        }
        return new ConfirmDeletePage(drone);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditProperties()
     */
    @Override
    public EditDocumentPropertiesPage selectEditProperties()
    {
        WebElement editProperties = findElement(EDIT_PROP_ICON);
        editProperties.click();
        return new EditDocumentPropertiesPage(getDrone());
    }

    /*
     * (non-Javadoc)
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
        // // close the original tab

        // DONOT CLOSE THE WINDOW

        // getDrone().closeWindow();
        // switch to new tab
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

    /*
     * (non-Javadoc)
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

    /*
     * (non-Javadoc)
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

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLiked()
     */
    @Override
    public boolean isLiked()
    {
        try
        {
            WebElement likeContent = findElement(By.cssSelector(LIKE_CONTENT));
            String status = likeContent.getAttribute("class");
            if (status != null)
            {
                boolean liked = status.contains("like-action enabled");
                return liked;
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            return isLiked();
        }
        return false;
    }

    /*
     * (non-Javadoc)
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
        catch (NoSuchElementException nse)
        {
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            return isFavourite();
        }
        return false;
    }

    /*
     * (non-Javadoc)
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

    /*
     * (non-Javadoc)
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
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#addTag(java.lang.String)
     */
    @Override
    public void addTag(final String tagName)
    {
        if (tagName == null || tagName.isEmpty())
        {
            throw new IllegalArgumentException("Tag Name is required");
        }
        try
        {
            clickOnAddTag();
            enterTagString(tagName);
            resolveStaleness();
            clickOnTagSaveButton();
            domEventCompleted();
        }
        catch (TimeoutException te)
        {
            logger.error("Error adding tag: ", te);
            throw new PageException("Error While adding tag: " + tagName);
        }
    }

    @Override
    public void enterTagString(final String tagName)
    {
        try
        {

            WebElement inputTagName = findAndWait(By.cssSelector(INPUT_TAG_NAME));
            inputTagName.clear();
            inputTagName.sendKeys(tagName + "\n");
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the tag input css.", te);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#sendKeysToTagInput(CharSequence)
     */
    @Override
    public void sendKeysToTagInput(CharSequence... keysToSend)
    {
        WebElement inputTagName = findAndWait(By.cssSelector(INPUT_TAG_NAME));
        // inputTagName.clear();
        inputTagName.sendKeys(keysToSend);
    }

    @Override
    public boolean isTagHighlightedOnEdit(String tagName)
    {

        try
        {
            WebElement highlightedTag = drone.find(By.xpath(String.format(
                    "//div[@class='inlineTagEdit']/span/span[contains(@class,'inlineTagEditTag')]/span[text()='%s']", tagName)));
            return highlightedTag.isDisplayed();
        }
        catch (TimeoutException e)
        {
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickTagOnEdit(String)
     */
    @Override
    public void clickTagOnEdit(String tagName)
    {
        try
        {
            WebElement InlineEditTag = drone.find(By.xpath(String.format(
                    "//div[@class='inlineTagEdit']/span/span[contains(@class,'inlineTagEditTag')]/span[text()=\"%s\"]", tagName)));
            InlineEditTag.click();
        }
        catch (TimeoutException e)
        {
        }

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

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getTitle()
     */
    @Override
    public String getTitle()
    {
        try
        {
            return findAndWait(By.cssSelector(TITLE)).getText();
        }
        catch (TimeoutException te)
        {
        }
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
            logger.error("Exceeded the time to find css.", e);
            throw new PageException("Exceeded the time to find css.");
        }

    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnAddTag()
     */
    @Override
    public void clickOnAddTag()
    {
        RenderTime timer = new RenderTime(((WebDroneImpl) getDrone()).getMaxPageRenderWaitTime() * 2);
        while (true)
        {
            try
            {
                timer.start();
                WebElement tagInfo = findAndWait(By.cssSelector(TAG_INFO));
                getDrone().mouseOver(tagInfo);
                By addTagButton = By.xpath(String.format("//h3/span/a[text()='%s']/../../../div/span[@title='Tag']", getName()));
                drone.waitUntilElementClickable(addTagButton, SECONDS.convert(WAIT_TIME_3000, MILLISECONDS));
                drone.executeJavaScript("arguments[0].click();", drone.findAndWait(addTagButton));
                if (findElement(By.cssSelector(INPUT_TAG_NAME)).isDisplayed())
                {
                    break;
                }
            }
            catch (NoSuchElementException e)
            {
                logger.error("Unable to find the add tag icon", e);
            }
            catch (TimeoutException te)
            {
                logger.error("Exceeded time to find the tag info area ", te);
            }
            catch (ElementNotVisibleException e2)
            {
            }
            catch (StaleElementReferenceException stale)
            {
            }
            finally
            {
                timer.end();
            }
        }
    }

    /*
     * (non-Javadoc)
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
        catch (Exception e)
        {
        }
        return false;
    }

    /*
     * (non-Javadoc)
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
     * 
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
                    String selector = version.isDojoSupported() ? CLOUD_REMOVE_TAG : ENTERPRISE_REMOVE_TAG;
                    return tag.findElement(By.cssSelector(selector));
                }
                catch (NoSuchElementException e)
                {
                    logger.error("Unable to find the remove tag button.", e);
                }
            }
        }
        throw new PageException("Unable to find the remove tag button.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagSaveButton()
     */
    @Override
    public void clickOnTagSaveButton()
    {
        try
        {
            findAndWait(By.xpath("//form[@class='insitu-edit']/a[text()='Save']")).click();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Save button css.", ex);
            throw new PageException("Exceeded time to find the Save button css.");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagCancelButton()
     */
    @Override
    public void clickOnTagCancelButton()
    {
        try
        {
            findAndWait(By.xpath("//form[@class='insitu-edit']/a[text()='Cancel']")).click();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Save button css.", ex);
            throw new PageException("Exceeded time to find the Save button css.");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCheckbox()
     */
    @Override
    public void selectCheckbox()
    {
        findAndWait(By.cssSelector(SELECT_CHECKBOX)).click();
        domEventCompleted();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isCheckboxSelected()
     */
    @Override
    public boolean isCheckboxSelected()
    {
        try
        {
            return findElement(By.cssSelector(SELECT_CHECKBOX)).isSelected();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectThumbnail()
     */
    @Override
    public HtmlPage selectThumbnail()
    {
        try
        {
            findElement(By.cssSelector(THUMBNAIL)).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to find and click thumbnail icon ", e);
        }

        throw new PageOperationException("Unable to click find and click on Thumbnail icon");
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
        catch (Exception e)
        {
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isCloudSynced()
     */
    @Override
    public boolean isCloudSynced()
    {
        try
        {
            WebElement thumbnailType = findElement(By.cssSelector(CLOUD_SYNC_ICON));
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

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isPartOfWorkflow()
     */
    @Override
    public boolean isPartOfWorkflow()
    {
        try
        {
            WebElement thumbnailType = drone.find(By.cssSelector(WORKFLOW_ICON));
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
        AlfrescoVersion version = getDrone().getProperties().getVersion();
        if (!isFolder())
        {
            throw new UnsupportedOperationException("Download folder as zip is available for folders only.");
        }
        if (AlfrescoVersion.Enterprise41.equals(version) || version.isCloud())
        {
            throw new AlfrescoVersionException("Option Download Folder as Zip is not available for this version of Alfresco");
        }

        try
        {
            WebElement menuOption = findElement(By.cssSelector(DOWNLOAD_FOLDER));
            menuOption.click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to click download folder as a zip", nse);
        }
    }

    /*
     * (non-Javadoc)
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

    /*
     * (non-Javadoc)
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
        if (nodeRef == null || nodeRef.isEmpty())
        {
            throw new UnsupportedOperationException(String.format("Content noderef is required: %s", nodeRef));
        }

        List<WebElement> elements = getDrone().findAll(By.cssSelector(String.format("input[value='%s']", nodeRef)));
        if (elements == null || elements.isEmpty())
        {
            throw new UnsupportedOperationException("there are no elements matching the node ref : " + nodeRef);
        }

        for (WebElement element : elements)
        {
            if (element.getText() != null && element.getAttribute("value").equalsIgnoreCase(nodeRef))
            {
                WebElement row = element.findElement(By.xpath(rowElementXPath));
                setWebElement(row);
            }
        }
    }

    /**
     * Performs the find and wait given amount of time
     * with an added resolveStaleness.
     * If we encounter the staleness exception we refresh the web
     * element we are working with and re-do the search.
     * 
     * @param cssSelector By
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
     * 
     * @param cssSelector By
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

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewFolderDetails()
     */
    @Override
    public FolderDetailsPage selectViewFolderDetails()
    {
        WebElement menuOption = findAndWait(By.cssSelector("div.folder-view-details>a"));
        menuOption.click();

        return new FolderDetailsPage(getDrone());
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagNameLink(java.lang.String)
     */
    @Override
    public HtmlPage clickOnTagNameLink(String tagName)
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
                        return FactorySharePage.resolvePage(drone);
                    }
                }
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
            throw new PageException("Exceeded the time to find css.");
        }
        throw new PageException("Not able to tag name: " + tagName);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectSyncToCloud()
     */
    @Override
    public HtmlPage selectSyncToCloud()
    {
        try
        {
            WebElement syncToCloud = findAndWait(CLOUD_SYNC_LINK);
            if (syncToCloud.isEnabled())
            {
                syncToCloud.click();
                drone.waitUntilElementDisappears(CLOUD_SYNC_LINK, 1);
                if (isSignUpDialogVisible())
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
            if (logger.isTraceEnabled())
            {
                logger.trace("No Such Element exception", nse);
            }
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Timeout exception", te);
            }
        }
        throw new PageException("Unable to select SyncToCloud option");

    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditInGoogleDocs()
     */
    @Override
    public HtmlPage selectEditInGoogleDocs()
    {
        WebElement editLink = findAndWait(By.cssSelector("div#onGoogledocsActionEdit a"));
        editLink.click();
        String text = "Editing in Google Docs";
        drone.waitUntilVisible(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxTime, MILLISECONDS));
        drone.waitUntilNotVisible(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxTime, MILLISECONDS));
        // drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxTime, MILLISECONDS));
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

    /*
     * (non-Javadoc)
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

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectUnSyncAndRemoveContentFromCloud(boolean)
     */
    @Override
    public DocumentLibraryPage selectUnSyncAndRemoveContentFromCloud(boolean doRemoveContentOnCloud)
    {
        selectUnSyncFromCloud();
        if (doRemoveContentOnCloud)
        {
            getDrone().findAndWait(By.cssSelector(".requestDeleteRemote-checkBox")).click();
        }
        List<WebElement> buttonElements = getDrone().findAndWaitForElements(By.cssSelector("div>span.button-group>span>span.first-child"));
        for (WebElement webElement : buttonElements)
        {
            if (drone.getValue("remove.sync").equals(webElement.getText()))
            {
                webElement.click();
                drone.waitUntilElementPresent(BLACK_MESSAGE, SECONDS.convert(maxTime, MILLISECONDS));
                drone.waitUntilElementDeletedFromDom(BLACK_MESSAGE, SECONDS.convert(maxTime, MILLISECONDS));
                break;
            }
        }
        return new DocumentLibraryPage(getDrone());
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectUnSyncFromCloud()
     */
    @Override
    public void selectUnSyncFromCloud()
    {
        WebElement unSyncToCloud = findAndWait(By.cssSelector("div#onActionCloudUnsync>a[title='Unsync from Cloud']"));
        unSyncToCloud.click();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#selectForceUnSyncInCloud()
     */
    @Override
    public DocumentLibraryPage selectForceUnSyncInCloud()
    {
        try
        {
            WebElement forceUnSync = findAndWait(By.cssSelector("div#onActionCloudUnsync>a[title='Force Unsync']"));
            forceUnSync.click();

            List<WebElement> buttonElements = getDrone().findAndWaitForElements(By.cssSelector("div>span.button-group>span>span.first-child"));
            for (WebElement webElement : buttonElements)
            {
                if (drone.getValue("remove.sync").equals(webElement.getText()))
                {
                    webElement.click();
                    drone.waitUntilElementPresent(BLACK_MESSAGE, SECONDS.convert(maxTime, MILLISECONDS));
                    drone.waitUntilElementDeletedFromDom(BLACK_MESSAGE, SECONDS.convert(maxTime, MILLISECONDS));
                    break;
                }
            }
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Timeout finding the element" + toe.getMessage());
        }
        return FactorySharePage.resolvePage(drone).render();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isViewCloudSyncInfoLinkPresent()
     */
    @Override
    public boolean isViewCloudSyncInfoLinkPresent()
    {
        try
        {
            WebElement viewCloudSync = findElement(By.cssSelector("img[title='Click to view sync info']"));
            return viewCloudSync.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }

        return false;
    }

    /*
     * (non-Javadoc)
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
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageException("Not able to click on view cloud sync info link.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectInlineEdit()
     */
    @Override
    public HtmlPage selectInlineEdit()
    {
        WebElement inLineEdit = findAndWait(INLINE_EDIT_LINK);
        inLineEdit.click();
        return new InlineEditPage(drone);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getCloudSyncType()
     */
    @Override
    public String getCloudSyncType()
    {
        try
        {
            return findAndWait(SYNC_INFO_PAGE).getAttribute("title");
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageException("Not able to click on view cloud sync info link.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getContentInfo()
     */
    @Override
    public String getContentInfo()
    {
        try
        {
            return findAndWait(INFO_BANNER).getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find Info banner.", e);
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLocked()
     */
    @Override
    public boolean isLocked()
    {
        try
        {
            return findElement(LOCK_ICON).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Lock icon is not displayed", te);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isInlineEditLinkPresent()
     */
    @Override
    public boolean isInlineEditLinkPresent()
    {
        try
        {
            findElement(MORE_ACTIONS_MENU);
            return drone.find(INLINE_EDIT_LINK).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Inline Edit link is not displayed", te);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditOfflineLinkPresent()
     */
    @Override
    public boolean isEditOfflineLinkPresent()
    {
        try
        {
            findElement(MORE_ACTIONS_MENU);
            return drone.find(EDIT_OFFLINE_LINK).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Edit Offline link is not displayed", te);
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditInGoogleDocsPresent()
     */
    @Override
    public boolean isEditInGoogleDocsPresent()
    {
        try
        {
            WebElement editInGoogleDocs = findElement(By.cssSelector("div.google-docs-edit-action-link a"));
            return editInGoogleDocs.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isDeletePresent()
     */
    @Override
    public boolean isDeletePresent()
    {
        try
        {
            WebElement deleteLink = findElement(By.cssSelector("div[class$='delete'] a"));
            return deleteLink.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageRules()
     */
    @Override
    public HtmlPage selectManageRules()
    {
        try
        {
            findAndWait(By.cssSelector("div.folder-manage-rules > a")).click();
            return drone.getCurrentPage();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Manage Rules link is not displayed for selected data row", te);
        }
    }

    /*
     * (non-Javadoc)
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
            if (logger.isInfoEnabled())
            {
                logger.info("UnSync From Cloud Link is not displayed", e);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
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
            if (logger.isTraceEnabled())
            {
                logger.trace("Sync failed icon is not displayed", e);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isRuleIconPresent(long)
     */
    @Override
    public boolean isRuleIconPresent(long waitTime)
    {
        try
        {
            return drone.findAndWait(By.cssSelector(RULES_ICON), waitTime).isDisplayed();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Rule icon is not displayed", e);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
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
            if (logger.isTraceEnabled())
            {
                logger.trace("Request Sync link is not displayed", e);
            }
        }
        throw new PageException("Unable to select Request Sync option");
    }

    /*
     * (non-Javadoc)
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
            if (logger.isTraceEnabled())
            {
                logger.trace("Request Sync link element is not present", e);
            }
        }
        throw new PageException("Request Sync link element is not present");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isSyncToCloudLinkPresent()
     */
    @Override
    public boolean isSyncToCloudLinkPresent()
    {
        try
        {
            return drone.findAndWait(CLOUD_SYNC_LINK, WAIT_TIME_3000).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find \"Sync to Cloud\" option", nse);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
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
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Manage permission link is not displayed for selected data row", exception);
            }
        }
        throw new PageOperationException("Manage permission link is not displayed for selected data row");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCopyTo()
     */
    @Override
    public CopyOrMoveContentPage selectCopyTo()
    {
        return selectCopyOrMoveTo("Copy to...");
    }

    /*
     * (non-Javadoc)
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
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            logger.error(linkText + " link is not displayed for selected data row", exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectCopyOrMoveTo(linkText);
        }
        throw new PageOperationException(linkText + " link is not displayed for selected data row");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#delete()
     */
    @Override
    public HtmlPage delete()
    {
        return selectDelete().selectAction(Action.Delete);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectStartWorkFlow()
     */
    @Override
    public StartWorkFlowPage selectStartWorkFlow()
    {
        try
        {
            // css selector changed to suite MyAlfresco + fix localisation issues due to linkText
            WebElement startWorkFlow = findAndWait(CREATE_TASK_WORKFLOW);
            startWorkFlow.click();
            return new StartWorkFlowPage(drone);
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectStartWorkFlow();
        }
        throw new PageException("Unable to find assign workflow.");
    }

    /*
     * (non-Javadoc)
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
        catch (NoSuchElementException e)
        {
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectUploadNewVersion();
        }

        // TODO add version
        return new UpdateFilePage(drone, "");
    }

    /*
     * (non-Javadoc)
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
                logger.trace("Manage permission link is not displayed for selected data row", nse);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditPropertiesLinkPresent()
     */
    @Override
    public boolean isEditPropertiesLinkPresent()
    {
        try
        {
            return findElement(By.cssSelector("div.document-edit-properties>a")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Edit properties link is not displayed for selected data row", nse);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
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
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectEditOffline();
        }
        catch (Exception e)
        {
            throw new PageException("Robot not working");
        }
        throw new PageException("Unable to find Edit Offline link");
    }

    /*
     * (non-Javadoc)
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
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectCancelEditing();
        }
        throw new PageException("Unable to find Cancel Editing link");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEdited()
     */
    @Override
    public boolean isEdited()
    {
        try
        {
            return drone.find(By.cssSelector(EDITED_ICON)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /*
     * (non-Javadoc)
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
        catch (NoSuchElementException e)
        {
        }
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
        long defaultWaitTime = ((WebDroneImpl) drone).getDefaultWaitTime();
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

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isCommentLinkPresent()
     */
    @Override
    public boolean isCommentLinkPresent()
    {
        try
        {
            return findElement(COMMENT_LINK).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
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
            WebElement shareLink = findElement(QUICK_SHARE_LINK);

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
            WebElement icon = drone.find(VIEW_IN_BROWsER_ICON);

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
        WebElement contentNameLink = findAndWait(By.cssSelector(FILENAME_IDENTIFIER));
        getDrone().mouseOver(contentNameLink);
        resolveStaleness();
        // Wait till pencil icon appears
        WebElement editIcon = findElement(By.cssSelector(EDIT_CONTENT_NAME_ICON));
        // Select to get focus
        editIcon.click();
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
            logger.error("Input should be displayed", e);
            throw new PageOperationException("Input should be displayed");
        }

    }

    @Override
    public void contentNameClickSave()
    {
        ClickLinkText(By.cssSelector(INPUT_CONTENT_NAME), "Save");
    }

    private void ClickLinkText(By by, String linkText)
    {
        String expectionMessage = "";
        try
        {
            WebElement inputBox = findElement(by);
            if (inputBox.isDisplayed())
            {
                findAndWait(By.linkText(linkText)).click();
                return;
            }
            else
            {
                throw new PageOperationException("Input is not displayed displayed");
            }
        }
        catch (TimeoutException ex)
        {
            expectionMessage = "Exceeded time to find the " + linkText + " button css." + ex;
            logger.error(expectionMessage);
        }
        catch (NoSuchElementException ex)
        {
            expectionMessage = "Not able to find the input css." + ex;
            logger.error(expectionMessage);
        }
        throw new PageOperationException("Exceeded time to find the " + linkText + " button css." + expectionMessage);
    }

    @Override
    public void contentNameClickCancel()
    {
        ClickLinkText(By.cssSelector(INPUT_CONTENT_NAME), "Cancel");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModified()
    {
        throw new UnsupportedOperationException("Modified is not available in current view.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModifier()
    {
        try
        {
            if (!hasCreator())
            {
                return findAndWait(By.xpath(".//div[@class='yui-dt-liner']/div[1]/span/*[2]")).getText();
            }
            else
            {
                throw new PageOperationException("Content just created.");
            }
        }
        catch (TimeoutException e)
        {
            throw new UnsupportedOperationException("Modifier is not available in current view. ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreated()
    {
        throw new UnsupportedOperationException("Created is not available in current view.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlPage selectCreator()
    {
        throw new UnsupportedOperationException("Creator is not available in current view.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreator()
    {
        try
        {
            if (hasCreator())
            {
                return findAndWait(By.xpath(".//div[@class='yui-dt-liner']/div[1]/span/*[2]")).getText();
            }
            else
            {
                throw new PageOperationException("Content modified.");
            }
        }
        catch (TimeoutException e)
        {
            throw new UnsupportedOperationException("Creator is not available in current view.");
        }
    }

    private boolean hasCreator()
    {
        return findAndWait(By.xpath(".//div[@class='yui-dt-liner']/div[1]/span")).getText().contains("Created ");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickShareLink()
     */
    @Override
    public HtmlPage clickShareLink()
    {
        if (isFolder())
        {
            throw new UnsupportedOperationException("Share Link is not Supported for the Folder");
        }
        try
        {
            findAndWait(QUICK_SHARE_LINK).click();
            return new ShareLinkPage(drone);
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the share link element", ex);
        }

        throw new PageException("Unable to find the Share Link.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#getFileOrFolderHeight()
     */
    public double getFileOrFolderHeight()
    {
        throw new UnsupportedOperationException("File or Folder Height is not available in this view type.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isInfoPopUpDisplayed()
     */
    @Override
    public boolean isInfoPopUpDisplayed()
    {
        throw new UnsupportedOperationException("Info Icon is not available in this view type.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickInfoIcon()
     */
    @Override
    public void clickInfoIcon()
    {
        throw new UnsupportedOperationException("Info Icon is not available in this view type.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isInfoIconVisible()
     */
    @Override
    public boolean isInfoIconVisible()
    {
        throw new UnsupportedOperationException("Info Icon is not available in this view type.");
    }

    @Override
    public String getContentNameFromInfoMenu()
    {
        throw new UnsupportedOperationException("Info menu is not available in this view type.");
    }

    @Override
    public String getVersionInfo()
    {
        if (isFolder())
        {
            throw new UnsupportedOperationException("Only available for file.");
        }
        String version = "";
        try
        {
            version = findAndWait(By.xpath(".//span[@class='document-version']")).getText();
        }
        catch (TimeoutException te)
        {
            logger.error("Timeout Reached", te);
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            getVersionInfo();
        }
        return version;
    }

    @Override
    public boolean isCheckBoxVisible()
    {
        try
        {
            return findElement(By.cssSelector(SELECT_CHECKBOX)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    @Override
    public boolean isVersionVisible()
    {
        try
        {
            return findElement(FILE_VERSION_IDENTIFIER).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagNameLink(java.lang.String)
     */
    @Override
    public HtmlPage clickOnCategoryNameLink(String categoryName)
    {
        if (categoryName == null)
        {
            throw new UnsupportedOperationException("Drone and category Name is required.");
        }

        try
        {
            List<WebElement> categoryList = findAllWithWait(CATEGORY_LINK);
            if (categoryList != null)
            {
                for (WebElement tag : categoryList)
                {
                    String tagText = tag.getText();
                    if (categoryName.equalsIgnoreCase(tagText))
                    {
                        tag.click();
                        drone.refresh();
                        return FactorySharePage.resolvePage(drone);
                    }
                }
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
            throw new PageException("Exceeded the time to find css. ACE-3037");
        }
        throw new PageException("Not able to category name: " + categoryName);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickCommentsLink()
     */
    @Override
    public HtmlPage clickCommentsLink()
    {
        try
        {
            findAndWait(COMMENT_LINK).click();

            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the comments element", ex);
        }

        throw new PageException("Unable to find the comments Link.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#getCommentsToolTip()
     */
    @Override
    public String getCommentsToolTip()
    {
        try
        {
            return findAndWait(COMMENT_LINK).getAttribute("title");
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the comments tooltip element", ex);
        }

        throw new PageException("Unable to find the comments tooltip.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#getCommentsCount()
     */
    @Override
    public int getCommentsCount()
    {
        int cnt = 0;
        try
        {
            String count = findAndWait(By.cssSelector("span.comment-count")).getText();
            cnt = Integer.parseInt(count);
        }
        catch (NumberFormatException nfe)
        {
            logger.error("Unable to convert comments count string value into int", nfe);
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the comments tooltip element", ex);
        }

        return cnt;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickContentNameFromInfoMenu()
     */
    @Override
    public HtmlPage clickContentNameFromInfoMenu()
    {
        throw new UnsupportedOperationException("Info menu is not available in this view type.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#selectModifier()
     */
    @Override
    public HtmlPage selectModifier()
    {
        try
        {
            WebElement creatorLink = findAndWait(By.cssSelector("a[href$='profile']"));
            creatorLink.click();

            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the comments tooltip element", ex);
        }

        throw new PageOperationException("Error in finding and clicking on modifier link.");
    }

    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isNodeRefColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("NodeRef column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isStatusColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Status column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isThumbnailColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Thumbnail column is not available in current view.");
    // }
    //
    // @Override
    // public boolean isNameColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Name column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isTitleColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Title column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isDescriptionColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Description column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isCreatorColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Creator column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isCreatedColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Created column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isModifierColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Modifier column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isModifiedColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Modified column is not available in current view.");
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean isActionsColHeaderVisible()
    // {
    // throw new UnsupportedOperationException("Actions column is not available in current view.");
    // }

    /**
     * (non-Javadoc)
     * 
     * @see FileDirectoryInfo#getPreViewUrl()
     */
    @Override
    public String getPreViewUrl()
    {
        try
        {
            return drone.findAndWait(By.cssSelector(THUMBNAIL + ">img")).getAttribute("src");
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the preView img", ex);
        }
        throw new PageOperationException("Error getting URL to preview image");
    }

    /**
     * Gets the Like option tool tip on the select data row on
     * DocumentLibrary Page.
     */
    @Override
    public String getFavouriteOrUnFavouriteTip()
    {
        return findElement(By.cssSelector(FAVOURITE_CONTENT)).getAttribute("title");
    }

    /**
     * Check if the file is shared.
     * 
     * @return
     */
    @Override
    public boolean isFileShared()
    {
        try
        {
            WebElement shareLink = findElement(QUICK_SHARE_LINK);

            String elClass = shareLink.getAttribute("class");
            return elClass.contains("enabled");
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

    /**
     * Check if the save link is visible.
     * 
     * @return
     */
    @Override
    public boolean isSaveLinkVisible()
    {
        return isLinkVisible("Save");
    }

    /**
     * Check if the save link is visible.
     * 
     * @return
     */
    @Override
    public boolean isCancelLinkVisible()
    {
        return isLinkVisible("Cancel");
    }

    /**
     * Check if the link is visible.
     * 
     * @return
     */
    private boolean isLinkVisible(String linkText)
    {
        try
        {
            return findElement(By.linkText(linkText)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            // no log needed due to negative cases.
        }
        return false;
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public boolean isViewOriginalLinkPresent()
    {
        try
        {
            return drone.findAndWait(By.cssSelector(VIEW_ORIGINAL_DOCUMENT)).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("View Original Document link is not displayed", te);
            }
        }

        return false;
    }

    @Override
    public DocumentDetailsPage selectViewOriginalDocument()
    {
        try
        {
            WebElement link = drone.findAndWait(By.cssSelector(VIEW_ORIGINAL_DOCUMENT));
            link.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Unable to select View Original Document ", e);
        }

        return new DocumentDetailsPage(drone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getThumbnailURL()
    {
        throw new UnsupportedOperationException("Not implemented in current view.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface.isGeoLocationIconDisplayed()
     */
    @Override
    public boolean isGeoLocationIconDisplayed()
    {
        try
        {
            WebElement geoLocation = findElement(By.cssSelector("img[title='Geolocation metadata available']"));
            return geoLocation.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Geolocation Metadata available icon");
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface.isEXIFIconDisplayed()
     */
    @Override
    public boolean isEXIFIconDisplayed()
    {
        try
        {
            WebElement exifIcon = findElement(By.cssSelector("img[title='EXIF metadata available']"));
            return exifIcon.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find EXIF Metadata available icon");
            }
        }

        return false;
    }

    @Override
    public boolean isDownloadPresent()
    {
        try
        {
            return drone.find(By.cssSelector(DOWNLOAD_DOCUMENT)).isDisplayed();

        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Manage permission link is not displayed for selected data row", nse);
            }
        }
        return false;
    }

    @Override
    public boolean isMoreMenuButtonPresent()
    {
        try
        {
            return drone.find(MORE_ACTIONS_MENU).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("More+ menu is not displayed", te);
            }
        }
        return false;
    }

    @Override
    public boolean isTagsFieldPresent()
    {
        try
        {
            return drone.find(TAGS_FIELD).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Tags field is not displayed", te);
            }
        }
        return false;
    }

    @Override
    public List<String> getDescriptionList()
    {
        List<String> descriptionsList = new ArrayList<String>();
        try
        {
            List<WebElement> categoryElements = findElements(By.cssSelector("div.detail span.item"));
            for (WebElement webElement : categoryElements)
            {
                descriptionsList.add(webElement.getText());
            }
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Not able to find description", e);
        }
        return descriptionsList;
    }

    @Override
    public String getDescriptionFromInfo()
    {
        try
        {
            return findAndWait(By.cssSelector(DESCRIPTION_INFO)).getText();
        }
        catch (TimeoutException te)
        {
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectLocateFile()
     */
    @Override
    public void selectLocateFile()
    {
        try
        {
            WebElement menuOption = findElement(By.cssSelector(LOCATE_FILE));
            menuOption.click();
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }

    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditOfflineAndCloseFileWindow()
     */
    @Override
    public DocumentLibraryPage selectEditOfflineAndCloseFileWindow()
    {
        try
        {
            WebElement cancelEditing = findAndWait(By.linkText(drone.getValue("edit.offline.link.text")));
            cancelEditing.click();
            waitUntilMessageAppearAndDisappear("edited");

            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ESCAPE);
            robot.keyRelease(KeyEvent.VK_ESCAPE);

            return new DocumentLibraryPage(drone);
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectEditOfflineAndCloseFileWindow();
        }
        catch (Exception e)
        {
            throw new PageException("Robot not working");
        }
        throw new PageException("Unable to find Edit Offline link");
    }
}