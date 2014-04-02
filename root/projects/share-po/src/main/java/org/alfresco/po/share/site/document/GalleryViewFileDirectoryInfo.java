/**
 * 
 */
package org.alfresco.po.share.site.document;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author cbairaajoni
 * 
 */
public class GalleryViewFileDirectoryInfo extends FilmStripOrGalleryView
{

    public GalleryViewFileDirectoryInfo(String nodeRef, WebElement webElement, WebDrone drone)
    {
        super(nodeRef, webElement, drone);

        FILENAME_IDENTIFIER = "div.alf-label";
        THUMBNAIL_TYPE = "div.alf-gallery-item-thumbnail>span";
        rowElementXPath = "../../../..";
        FILE_DESC_IDENTIFIER = "h3.filename+div.detail+div.detail>span";
        TAG_LINK_LOCATOR = By.cssSelector("div>div>span>span>a.tag-link");
        resolveStaleness();
        
        if(isFolder())
        {
            THUMBNAIL = "div.alf-gallery-item-thumbnail>div+div+span a";  
        }
        else
        {
            THUMBNAIL = "div.alf-gallery-item-thumbnail>div+div+a";
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectThumbnail()
     */
    @Override
    public HtmlPage selectThumbnail()
    {
        drone.mouseOverOnElement(drone.findAndWait(By.xpath(String.format(".//div[@class='alf-label']/a[text()='%s']",getName()))));
        return super.selectThumbnail();
    }
}