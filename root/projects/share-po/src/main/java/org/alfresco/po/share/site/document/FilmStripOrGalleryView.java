/**
 * 
 */
package org.alfresco.po.share.site.document;

import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Abstract of an Gallery/FilmStrip view of FileDirectoryInfo.
 * 
 * @author cbairaajoni
 */
public abstract class FilmStripOrGalleryView extends FileDirectoryInfoImpl
{
    private static Log logger = LogFactory.getLog(FilmStripOrGalleryView.class);

    public FilmStripOrGalleryView(String nodeRef, WebElement webElement, WebDrone drone)
    {
        super(nodeRef, webElement, drone);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getDescription()
     */
    @Override
    public String getDescription()
    {
        clickInfoIcon(false);
        String desc = super.getDescription();
        focusOnDocLibFooter();
        return desc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getContentEditInfo()
     */
    @Override
    public String getContentEditInfo()
    {
        clickInfoIcon(false);
        String editInfo = super.getContentEditInfo();
        focusOnDocLibFooter();
        return editInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getTags()
     */
    @Override
    public List<String> getTags()
    {
        clickInfoIcon(false);
        
        List<String> tags = super.getTags();
        focusOnDocLibFooter();
        return tags;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getCategories()
     */
    @Override
    public List<Categories> getCategories()
    {
        clickInfoIcon(false);
        List<Categories> cats = super.getCategories();
        focusOnDocLibFooter();
        return cats;
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDelete()
     */
    @Override
    public ConfirmDeletePage selectDelete()
    {
        try
        {
            clickInfoIcon(true);
            return super.selectDelete();
        }
        catch (NoSuchElementException e) { }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectDelete();
        }
        
        throw new PageOperationException("Error in Select Delete.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditProperties()
     */
    @Override
    public EditDocumentPropertiesPopup selectEditProperties()
    {
        clickInfoIcon(false);
        return super.selectEditProperties();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewInBrowser()
     */
    @Override
    public void selectViewInBrowser()
    {
        clickInfoIcon(false);
        super.selectViewInBrowser();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectFavourite()
     */
    @Override
    public void selectFavourite()
    {
        clickInfoIcon(false);
        super.selectFavourite();
        focusOnDocLibFooter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectLike()
     */
    @Override
    public void selectLike()
    {
        clickInfoIcon(false);
        super.selectLike();
        focusOnDocLibFooter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLiked()
     */
    @Override
    public boolean isLiked()
    {
        clickInfoIcon(false);
        boolean like = super.isLiked();
        focusOnDocLibFooter();
        return like;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isFavourite()
     */
    @Override
    public boolean isFavourite()
    {
        clickInfoIcon(false);
        boolean favourite = super.isFavourite();
        focusOnDocLibFooter();
        return favourite;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getLikeCount()
     */
    @Override
    public String getLikeCount()
    {
        clickInfoIcon(false);
        String likeCount = super.getLikeCount();
        focusOnDocLibFooter();
        return likeCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#hasTags()
     */
    @Override
    public boolean hasTags()
    {
        clickInfoIcon(false);
        boolean tag = super.hasTags();
        focusOnDocLibFooter();
        return tag;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getContentNodeRef()
     */
    @Override
    public String getContentNodeRef()
    {
        clickInfoIcon(false);
        String nodeRef = super.getContentNodeRef();
        focusOnDocLibFooter();
        return nodeRef;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#addTag(java.lang.String)
     */
    @Override
    public void addTag(final String tagName)
    {
        clickInfoIcon(false);
        super.addTag(tagName);
        focusOnDocLibFooter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnAddTag()
     */
    @Override
    public void clickOnAddTag()
    {
        clickInfoIcon(false);
        super.clickOnAddTag();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#removeTagButtonIsDisplayed(java.lang.String)
     */
    @Override
    public boolean removeTagButtonIsDisplayed(String tagName)
    {
        clickInfoIcon(false);
        return super.removeTagButtonIsDisplayed(tagName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagRemoveButton(java.lang.String)
     */
    @Override
    public void clickOnTagRemoveButton(String tagName)
    {
        clickInfoIcon(false);
        super.clickOnTagRemoveButton(tagName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isCloudSynced()
     */
    @Override
    public boolean isCloudSynced()
    {
        clickInfoIcon(false);
        boolean cloudSync = super.isCloudSynced();
        focusOnDocLibFooter();
        return cloudSync;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isPartOfWorkflow()
     */
    @Override
    public boolean isPartOfWorkflow()
    {
        clickInfoIcon(false);
        boolean cloudSync = super.isPartOfWorkflow();
        focusOnDocLibFooter();
        return cloudSync;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDownloadFolderAsZip()
     */
    @Override
    public void selectDownloadFolderAsZip()
    {
        clickInfoIcon(false);
        super.downloadFolderAsZip();
        focusOnDocLibFooter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDownload()
     */
    @Override
    public void selectDownload()
    {
        clickInfoIcon(false);
        super.selectDownload();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewFolderDetails()
     */
    @Override
    public FolderDetailsPage selectViewFolderDetails()
    {
        clickInfoIcon(false);
        return super.selectViewFolderDetails();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagNameLink(java.lang.String)
     */
    @Override
    public DocumentLibraryPage clickOnTagNameLink(String tagName)
    {
        return super.clickOnTagNameLink(tagName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectSyncToCloud()
     */
    @Override
    public HtmlPage selectSyncToCloud()
    {
        clickInfoIcon(true);
        return super.selectSyncToCloud();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditInGoogleDocs()
     */
    @Override
    public HtmlPage selectEditInGoogleDocs()
    {
        clickInfoIcon(true);
        return super.selectEditInGoogleDocs();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectUnSyncFromCloud()
     */
    @Override
    public void selectUnSyncFromCloud()
    {
        clickInfoIcon(true);
        super.selectUnSyncFromCloud();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isViewCloudSyncInfoLinkPresent()
     */
    @Override
    public boolean isViewCloudSyncInfoLinkPresent()
    {
        clickInfoIcon(false);
        boolean viewCloudSyncInfo = super.isViewCloudSyncInfoLinkPresent(); 
        focusOnDocLibFooter();
        return viewCloudSyncInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnViewCloudSyncInfo()
     */
    @Override
    public SyncInfoPage clickOnViewCloudSyncInfo()
    {
        clickInfoIcon(false);
        return super.clickOnViewCloudSyncInfo();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectInlineEdit()
     */
    @Override
    public HtmlPage selectInlineEdit()
    {
        clickInfoIcon(true);
        return super.selectInlineEdit();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getCloudSyncType()
     */
    @Override
    public String getCloudSyncType()
    {
        clickInfoIcon(false);
        return super.getCloudSyncType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLocked()
     */
    @Override
    public boolean isLocked()
    {
        clickInfoIcon(false);
        boolean lock = super.isLocked(); 
        focusOnDocLibFooter();
        return lock;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isInlineEditLinkPresent()
     */
    @Override
    public boolean isInlineEditLinkPresent()
    {
        clickInfoIcon(true);
        return super.isInlineEditLinkPresent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditOfflineLinkPresent()
     */
    @Override
    public boolean isEditOfflineLinkPresent()
    {
        clickInfoIcon(true);
        return super.isEditOfflineLinkPresent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditInGoogleDocsPresent()
     */
    @Override
    public boolean isEditInGoogleDocsPresent()
    {
        clickInfoIcon(true);
        return super.isEditInGoogleDocsPresent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isDeletePresent()
     */
    @Override
    public boolean isDeletePresent()
    {
        clickInfoIcon(true);
        return super.isDeletePresent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isDeletePresent()
     */
    @Override
    public boolean isViewInBrowserVisible()
    {
        // openGalleryInfo(true);
        return super.isViewInBrowserVisible();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageRules()
     */
    @Override
    public HtmlPage selectManageRules()
    {
        clickInfoIcon(true);
        return super.selectManageRules();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isUnSyncFromCloudLinkPresent()
     */
    @Override
    public boolean isUnSyncFromCloudLinkPresent()
    {
        clickInfoIcon(true);
        return super.isUnSyncFromCloudLinkPresent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isSyncFailedIconPresent(long)
     */
    @Override
    public boolean isSyncFailedIconPresent(long waitTime)
    {
        clickInfoIcon(false);
        boolean syncFail = super.isSyncFailedIconPresent(waitTime);
        focusOnDocLibFooter();
        return syncFail;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectRequestSync()
     */
    @Override
    public DocumentLibraryPage selectRequestSync()
    {
        clickInfoIcon(true);
        return super.selectRequestSync();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isRequestToSyncLinkPresent()
     */
    @Override
    public boolean isRequestToSyncLinkPresent()
    {
        clickInfoIcon(true);
        return super.isRequestToSyncLinkPresent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isSyncToCloudLinkPresent()
     */
    @Override
    public boolean isSyncToCloudLinkPresent()
    {
        clickInfoIcon(true);
        return super.isSyncToCloudLinkPresent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManagePermission()
     */
    @Override
    public ManagePermissionsPage selectManagePermission()
    {
        clickInfoIcon(true);
        return super.selectManagePermission();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCopyTo()
     */
    @Override
    public CopyOrMoveContentPage selectCopyTo()
    {
        clickInfoIcon(true);
        return super.selectCopyTo();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectMoveTo()
     */
    @Override
    public CopyOrMoveContentPage selectMoveTo()
    {
        clickInfoIcon(true);
        return super.selectMoveTo();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#delete()
     */
    @Override
    public HtmlPage delete()
    {
        selectDelete();
        confirmDelete();
        return drone.getCurrentPage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectStartWorkFlow()
     */
    @Override
    public StartWorkFlowPage selectStartWorkFlow()
    {
        clickInfoIcon(true);
        return super.selectStartWorkFlow();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectUploadNewVersion()
     */

    @Override
    public UpdateFilePage selectUploadNewVersion()
    {
        try
        {
            clickInfoIcon(true);
            return super.selectUploadNewVersion();
        }
        catch (ElementNotVisibleException s)
        {
            resolveStaleness();
            selectUploadNewVersion();
        }
        throw new PageException("Element not visible");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isManagePermissionLinkPresent()
     */
    @Override
    public boolean isManagePermissionLinkPresent()
    {
        clickInfoIcon(true);
        return super.isManagePermissionLinkPresent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditPropertiesLinkPresent()
     */
    @Override
    public boolean isEditPropertiesLinkPresent()
    {
        clickInfoIcon(true);
        return super.isEditPropertiesLinkPresent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditOffline()
     */
    @Override
    public DocumentLibraryPage selectEditOffline()
    {
        clickInfoIcon(true);
        return super.selectEditOffline();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCancelEditing()
     */
    @Override
    public DocumentLibraryPage selectCancelEditing()
    {
        clickInfoIcon(true);
        return super.selectCancelEditing();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageAspects()
     */
    @Override
    public SelectAspectsPage selectManageAspects()
    {
        clickInfoIcon(true);
        return super.selectManageAspects();
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isInfoPopUpDisplayed()
     */
    @Override
    public boolean isInfoPopUpDisplayed()
    {
        try
        {
            return findAndWait(By.cssSelector("div.yui-panel-container")).isDisplayed();
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickInfoIcon()
     */
    @Override
    public void clickInfoIcon()
    {
        clickInfoIcon(false);
    }
    
    /**
     * @param hasMoreLink
     */
    private void clickInfoIcon(boolean hasMoreLink)
    {
        WebElement infoIcon = getInfoIcon();
        infoIcon.click();

        if (hasMoreLink)
        {
            selectMoreLink();
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#selectMoreLink()
     */
    @Override
    public void selectMoreLink()
    {
        try
        {
            findElement(By.cssSelector("a.show-more")).click();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Exceeded time to find the css." + e.getMessage());
            throw new PageException("Unable to find and click the more link");
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isInfoIconVisible()
     */
    @Override
    public boolean isInfoIconVisible()
    {
        try
        {
            WebElement infoIcon = getInfoIcon();
            return infoIcon.isDisplayed();
        }
        catch (PageException e)
        {
        }
        return false;
    }

    /**
     * @param fileName
     * @return WebElement
     */
    protected WebElement getInfoIcon()
    {
        try
        {
           //drone.mouseOver(drone.findAndWait(By.linkText(getName())));
            drone.mouseOverOnElement(drone.findAndWait(By.xpath(String.format(".//div[@class='alf-label']/a[text()='%s']",getName()))));
            return findAndWait(By.cssSelector("a.alf-show-detail"));
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find the css." + e.getMessage());
        }

        throw new PageException("File directory info with title was not found");
    }
    
    private void focusOnDocLibFooter()
    {
        drone.mouseOverOnElement(drone.findAndWait(By.xpath("//div[contains(@id,'default-doclistBarBottom')]")));
    }

    @Override
    public void contentNameEnableEdit()
    {
        clickInfoIcon(false);
        String temp = FILENAME_IDENTIFIER;
        FILENAME_IDENTIFIER = "h3.filename a";
        super.contentNameEnableEdit();
        FILENAME_IDENTIFIER = temp;
    }

    @Override
    public void contentNameEnter(String newContentName)
    {
        // openGalleryInfo(false);
        super.contentNameEnter(newContentName);
    }

    @Override
    public void contentNameClickSave()
    {
        // openGalleryInfo(false);
        super.contentNameClickSave();
    }

    @Override
    public void contentNameClickCancel()
    {
        // openGalleryInfo(false);
        super.contentNameClickCancel();
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#isShareLinkVisible()
     */
    @Override
    public boolean isShareLinkVisible()
    {
        clickInfoIcon(false);
        return super.isShareLinkVisible();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#clickShareLink()
     */
    @Override
    public HtmlPage clickShareLink()
    {
        clickInfoIcon(false);
        return super.clickShareLink();
    }    

    @Override
    public String getContentInfo()
    {
        clickInfoIcon(false);
        return super.getContentInfo();
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#clickOnCategoryNameLink(java.lang.String)
     */
    @Override
    public DocumentLibraryPage clickOnCategoryNameLink(String categoryName)
    {
        clickInfoIcon(true);
        return super.clickOnCategoryNameLink(categoryName);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickCommentsLink()
     */
    @Override
    public HtmlPage clickCommentsLink()
    {
        clickInfoIcon(false);
        return super.clickCommentsLink();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#getCommentsToolTip()
     */
    @Override
    public String getCommentsToolTip()
    {
        clickInfoIcon(false);
        return super.getCommentsToolTip();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#getCommentsCount()
     */
    @Override
    public int getCommentsCount()
    {
        clickInfoIcon(false);
        return super.getCommentsCount();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isCommentLinkPresent()
     */
    @Override
    public boolean isCommentLinkPresent()
    {
        clickInfoIcon(false);
        return super.isCommentLinkPresent();
    }

    @Override
    public String getVersionInfo()
    {
        clickInfoIcon(false);
        return super.getVersionInfo();
    }

    @Override
    public String getContentNameFromInfoMenu()
    {
        clickInfoIcon(false);
        String title = "";
        try
        {
            title = findAndWait(By.cssSelector("h3.filename a")).getText();
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
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#clickContentNameFromInfoMenu()
     */
    @Override
    public HtmlPage clickContentNameFromInfoMenu()
    {
        clickInfoIcon(false);
        try
        {
            findAndWait(By.cssSelector("h3.filename a")).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException te)
        {
            logger.error("Timeout Reached", te);
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            clickContentNameFromInfoMenu();
        }
        throw new PageException("Unable to find and click the file name link");
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#selectModifier()
     */
    @Override
    public HtmlPage selectModifier()
    {
        clickInfoIcon(false);
        return super.selectModifier();
    }
}