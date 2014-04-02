package org.alfresco.po.share.site.document;

import java.util.List;

import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.WebElement;

/**
 * @author cbairaajoni
 *
 */
public class SimpleViewFileDirectoryInfo extends SimpleDetailTableView
{

    public SimpleViewFileDirectoryInfo(String nodeRef, WebElement webElement, WebDrone drone)
    {
        super(nodeRef, webElement, drone);
        rowElementXPath = "../../..";
        resolveStaleness();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getDescription()
     */
    @Override
    public String getDescription()
    {
       throw new UnsupportedOperationException("Description is not available in Simple View File Directory Info.");
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#addTag(java.lang.String)
     */
    @Override
    public void addTag(final String tagName)
    {
        throw new UnsupportedOperationException("Adding Tag functionality is not available in Simple View File Directory Info.");
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getTags()
     */
    @Override
    public List<String> getTags()
    {
        throw new UnsupportedOperationException("Tags are not available in Simple View File Directory Info.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnAddTag()
     */
    @Override
    public void clickOnAddTag()
    {
        throw new UnsupportedOperationException("Add Tag icon is not available in Simple View File Directory Info.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagRemoveButton(java.lang.String)
     */
    @Override
    public void clickOnTagRemoveButton(String tagName)
    {
        throw new UnsupportedOperationException("Remove Tag icon is not available in Simple View File Directory Info.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagSaveButton()
     */
    @Override
    public void clickOnTagSaveButton()
    {
        throw new UnsupportedOperationException("Saving Tag functionality is not available in Simple View File Directory Info.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#clickOnTagCancelButton()
     */
    @Override
    public void clickOnTagCancelButton()
    {
        throw new UnsupportedOperationException("Cancelling Tag functionality is not available in Simple View File Directory Info.");
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectFavourite()
     */
    @Override
    public void selectFavourite()
    {
        throw new UnsupportedOperationException("Favourite selection is not available in Simple View File Directory Info.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectLike()
     */
    @Override
    public void selectLike()
    {
        throw new UnsupportedOperationException("Selecting Like functionality is not available in Simple View File Directory Info.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLiked()
     */
    @Override
    public boolean isLiked()
    {
        throw new UnsupportedOperationException("Like functionality is not available in Simple View File Directory Info.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isFavourite()
     */
    @Override
    public boolean isFavourite()
    {
        throw new UnsupportedOperationException("Favourites are not available in Simple View File Directory Info.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getLikeCount()
     */
    @Override
    public String getLikeCount()
    {
        throw new UnsupportedOperationException("Cancelling Tag functionality is not available in Simple View File Directory Info.");
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getCategories()
     */
    @Override
    public List<Categories> getCategories()
    {
        throw new UnsupportedOperationException("Categories are not available in Simple View File Directory Info.");
    }
}