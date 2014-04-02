/**
 * 
 */
package org.alfresco.po.share.site.document;

import java.util.List;

import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.webdrone.HtmlPage;
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
        
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
        openGalleryInfo(false);
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
            openGalleryInfo(true);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(true);
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
        openGalleryInfo(false);
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
        openGalleryInfo(false);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(false);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
        return super.selectUploadNewVersion();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isManagePermissionLinkPresent()
     */
    @Override
    public boolean isManagePermissionLinkPresent()
    {
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
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
        openGalleryInfo(true);
        return super.selectManageAspects();
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCheckbox()
     */
    @Override
    public void selectCheckbox()
    {
        openGalleryInfo(true);
        super.selectCheckbox();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isCheckboxSelected()
     */
    @Override
    public boolean isCheckboxSelected()
    {
        openGalleryInfo(true);
        return super.isCheckboxSelected();
    }

    public boolean isGalleryInfoPopUpDisplayed()
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
    
    public void clickOnGalleryInfo()
    {
        openGalleryInfo(false);  
    }
    
    /**
     * @param hasMoreLink
     *            TODO
     * 
     */
    public void openGalleryInfo(boolean hasMoreLink)
    {
        try
        {
            WebElement infoIcon = getGalleryInfoIcon();
            infoIcon.click();

            if (hasMoreLink)
            {
                findElement(By.cssSelector("a.show-more")).click();
            }
        }
        catch (NoSuchElementException e)
        {
            logger.error("Exceeded time to find the css." + e.getMessage());
            throw new PageException("File directory info with title was not found");
        }

    }

    /**
     * Checks to see if file is visible on the page.
     * 
     * @param fileName
     *            String title
     * @return true if file exists on the page
     */
    public boolean isGalleryInfoIconVisible()
    {
        try
        {
            WebElement infoIcon = getGalleryInfoIcon();
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
    protected WebElement getGalleryInfoIcon()
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
        openGalleryInfo(true);
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
        openGalleryInfo(false);
        return super.isShareLinkVisible();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#clickShareLink()
     */
    @Override
    public HtmlPage clickShareLink()
    {
        openGalleryInfo(false);
        return super.clickShareLink();
    }
}