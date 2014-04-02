/**
 * 
 */
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Abhijeet Bharade
 * 
 */
public class FilmStripViewFileDirectoryInfo extends FilmStripOrGalleryView
{

    private static Log logger = LogFactory.getLog(FilmStripViewFileDirectoryInfo.class);

    private WebElement THUMBNAIL_ROOT;

    /**
     * @param nodeRef
     * @param webElement
     * @param drone
     */
    public FilmStripViewFileDirectoryInfo(String nodeRef, WebElement webElement, WebDrone drone)
    {
        super(nodeRef, webElement, drone);

        FILENAME_IDENTIFIER = "";
        THUMBNAIL_TYPE = String.format(".//div[@class='alf-filmstrip-nav-item-thumbnail']//img[@id='%s']", nodeRef);
        rowElementXPath = "../../../..";
        FILE_DESC_IDENTIFIER = "div.detail:first-of-type span.item";
        THUMBNAIL = THUMBNAIL_TYPE + "/../..";
        resolveStaleness();
        THUMBNAIL_ROOT = drone.findAndWait(By.xpath(THUMBNAIL));
    }

    /**
     * @param fileName
     * @return WebElement
     */
    @Override
    protected WebElement getGalleryInfoIcon()
    {
        try
        {
            clickOnTitle();
            return findAndWait(By.cssSelector("a.alf-show-detail"));
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find the css." + e.getMessage());
        }

        throw new PageException("File directory info with title was not found");
    }

    @Override
    public String getName()
    {
        return THUMBNAIL_ROOT.getText();
    }

    /**
     * Returns true if content in the selected data row on DocumentLibrary is folder Page.
     * 
     * @return {boolean} <tt>true</tt> if the content is of type folder.
     */
    @Override
    public boolean isFolder()
    {
        try
        {
            WebElement thumbnailType = drone.findAndWait(By.xpath(THUMBNAIL_TYPE));
            logger.info("thumbnailType - " + thumbnailType.getAttribute("src"));
            return thumbnailType.getAttribute("src").contains("folder");
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Click on title.
     */
    @Override
    public void clickOnTitle()
    {
        THUMBNAIL_ROOT.click();
        domEventCompleted();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectThumbnail()
     */
    @Override
    public HtmlPage selectThumbnail()
    {
        clickOnTitle();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Gets the Title of the file or directory, if none then empty string is returned.
     * 
     * @return String Content description
     */
    @Override
    public String getTitle()
    {
        openGalleryInfo(false);
        return super.getTitle();
    }

    /**
     * Gets the Create / Edit Information of the file or directory, if none then empty string is returned.
     * 
     * @return String Content Edit Information
     */
    @Override
    public String getContentEditInfo()
    {
        openGalleryInfo(false);
        return findAndWait(By.cssSelector("h3.filename")).getText();
    }
}
