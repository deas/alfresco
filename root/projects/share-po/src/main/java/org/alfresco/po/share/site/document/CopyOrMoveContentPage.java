/**
 * 
 */
package org.alfresco.po.share.site.document;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.LinkedList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * This page does the selection of Destination, site and folderPath to Copy/Move the content.
 * 
 * @author cbairaajoni
 *
 */
public class CopyOrMoveContentPage  extends SharePage
{
    private static Log logger = LogFactory.getLog(CopyOrMoveContentPage.class);

    private final RenderElement FOOTER_ELEMENT = getVisibleRenderElement(By.cssSelector("div[id$='default-copyMoveTo-wrapper'] div.bdft"));
    private final RenderElement HEADER_ELEMENT = getVisibleRenderElement(By.cssSelector("div[id$='default-copyMoveTo-title']"));
    private final RenderElement FOLDER_PATH_ELEMENT = getVisibleRenderElement(By.cssSelector("div[id$='default-copyMoveTo-treeview']>div.ygtvitem"));

    private final By DESTINATION_LIST_CSS = By.cssSelector(".mode.flat-button>div>span>span>button");
    private final By SITE_LIST_CSS = By.cssSelector("div.site>div>div>a>h4");
    private final By DEFAULT_DOCUMENTS_FOLDER_CSS = By
            .cssSelector("div.path>div[id$='default-copyMoveTo-treeview']>div.ygtvitem>div.ygtvchildren>div.ygtvitem>table.ygtvtable>tbody>tr>td>span.ygtvlabel");
    private final By FOLDER_ITEMS_LIST_CSS = By.cssSelector("div.path div.ygtvitem>div.ygtvchildren>div.ygtvitem>table.ygtvtable span.ygtvlabel");
    private final By SELECTED_FOLDER_ITEMS_LIST_CSS = By
            .cssSelector("div.path div.ygtvitem>div.ygtvchildren>div.ygtvitem.selected>div.ygtvchildren>div.ygtvitem span.ygtvlabel");
    private final By COPY_MOVE_OK_BUTTON_CSS = By.cssSelector("button[id$='default-copyMoveTo-ok-button']");
    private final By COPY_MOVE_CANCEL_BUTTON_CSS = By.cssSelector("button[id$='default-copyMoveTo-cancel-button']");
    private final By COPY_MOVE_DIALOG_CLOSE_BUTTON_CSS = By.cssSelector("div[id$='default-copyMoveTo-dialog'] .container-close");
    private final By COPY_MOVE_DIALOG_TITLE_CSS = By.cssSelector("div[id$='default-copyMoveTo-title']");
    
    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public CopyOrMoveContentPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CopyOrMoveContentPage render(RenderTime timer)
    {
        elementRender(timer, HEADER_ELEMENT, FOOTER_ELEMENT, FOLDER_PATH_ELEMENT);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CopyOrMoveContentPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CopyOrMoveContentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method returns the Copy/Move Dialog title.
     * 
     * @return String
     */
    public String getDialogTitle()
    {
        String title = "";

        try
        {
            title = drone.findAndWait(COPY_MOVE_DIALOG_TITLE_CSS).getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the Copy/Move Dialog Css : " + e.getMessage());
        }

        return title;
    }
    
    /**
     * This method finds the list of destinations and return those as list of
     * string values.
     * 
     * @return List<String>
     */
    public List<String> getDestinations()
    {
        List<String> destinations = new LinkedList<String>();

        try
        {
            for (WebElement destination : drone.findAndWaitForElements(DESTINATION_LIST_CSS))
            {
                destinations.add(destination.getText());
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of destionations : " + e.getMessage());
        }

        return destinations;
    }
    
    /**
     * This method finds the list of sites and return those as list of
     * string values.
     * 
     * @return List<String>
     */
    public List<String> getSites()
    {
        List<String> sites = new LinkedList<String>();

        try
        {
            for (WebElement site : drone.findAndWaitForElements(SITE_LIST_CSS))
            {
                sites.add(site.getText());
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of sites : " + e.getMessage());
        }

        return sites;
    }
    
    /**
     * This method finds the list of folders and return those as list of
     * string values.
     * 
     * @return List<String>
     */
    public List<String> getFolders()
    {
        List<String> folders = new LinkedList<String>();

        try
        {
            for (WebElement folder : drone.findAndWaitForElements(FOLDER_ITEMS_LIST_CSS))
            {
                folders.add(folder.getText());
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of folders : " + e.getMessage());
        }

        return folders;
    }
    
    /**
     * This method finds the clicks on copy/move button.
     * 
     * @return DocumentLibraryPage
     */
    public DocumentLibraryPage selectOkButton()
    {
        try
        {
            drone.findAndWait(COPY_MOVE_OK_BUTTON_CSS).click();

            drone.waitForElement(By.cssSelector("div.bd>span.message"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            drone.waitUntilElementDeletedFromDom(By.cssSelector("div.bd>span.message"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));

            return new DocumentLibraryPage(drone);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the Copy/Move Button Css : " + e.getMessage());
            throw new PageException("Unable to find the Copy/Move button on Copy/Move Dialog.");
        }
    }
    
    /**
     * This method finds the clicks on cancel button and 
     * control will be on DocumentLibraryPage only.
     * 
     */
    public void selectCancelButton()
    {
        try
        {
            drone.findAndWait(COPY_MOVE_CANCEL_BUTTON_CSS).click();
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the cancel button Css : " + e.getMessage());
            throw new PageException("Unable to find the cancel button on Copy/Move Dialog.");
        }
    }
    
    /**
     * This method finds the clicks on close button and 
     * control will be on DocumentLibraryPage only.
     * 
     */
    public void selectCloseButton()
    {
        try
        {
            drone.findAndWait(COPY_MOVE_DIALOG_CLOSE_BUTTON_CSS).click();
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the close button Css : " + e.getMessage());
            throw new PageException("Unable to find the close button on Copy/Move Dialog.");
        }
    }
    
    /**
     * This method finds and selects the given destination name from the
     * displayed list of destinations.
     * 
     * @param destination
     * @return CopyOrMoveContentPage
     */
    public CopyOrMoveContentPage selectDestination(String destinationName)
    {
        if (StringUtils.isEmpty(destinationName))
        {
            throw new IllegalArgumentException("Destination name is required");
        }

        try
        {
            for (WebElement destination : drone.findAndWaitForElements(DESTINATION_LIST_CSS))
            {
                if (destination.getText() != null)
                {
                    if (destination.getText().equalsIgnoreCase(destinationName))
                    {
                        destination.click();
                        drone.waitForElement(SITE_LIST_CSS, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));

                        return new CopyOrMoveContentPage(drone);
                    }
                }
            }
        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable to find the inner text of destionation : " + ne.getMessage());
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of destionations : " + e.getMessage());
        }

        throw new PageOperationException("Unable to select Destination : " + destinationName);
    }
    
    /**
     * This method finds and selects the given site name from the
     * displayed list of sites.
     * 
     * @param site
     * @return CopyOrMoveContentPage
     */
    public CopyOrMoveContentPage selectSite(String siteName)
    {
        if (StringUtils.isEmpty(siteName))
        {
            throw new IllegalArgumentException("Site name is required");
        }

        try
        {
            for (WebElement site : drone.findAndWaitForElements(SITE_LIST_CSS))
            {
                if (site.getText() != null)
                {
                    if (site.getText().equalsIgnoreCase(siteName))
                    {
                        site.click();
                        drone.waitForElement(DEFAULT_DOCUMENTS_FOLDER_CSS, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        drone.waitForElement(FOLDER_ITEMS_LIST_CSS, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));

                        return new CopyOrMoveContentPage(drone);
                    }
                }
            }
        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable to find the inner text of site : " + ne.getMessage());
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of sites : " + e.getMessage());
        }

        throw new PageOperationException("Unable to select site.");
    }
    
    /**
     * This method finds and selects the given folder path from the displayed list
     * of folders.
     * 
     * @param folderPath
     * @return CopyOrMoveContentPage
     */
    public CopyOrMoveContentPage selectFolder(String... folderPath)
    {
        if (folderPath == null || folderPath.length < 1)
        {
            throw new IllegalArgumentException("Invalid Folder path!!");
        }
        int length = folderPath.length;
        List<WebElement> folderNames;
        try
        {
            for (String folder : folderPath)
            {
                length--;
                drone.waitForElement(FOLDER_ITEMS_LIST_CSS, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                folderNames = drone.findAndWaitForElements(FOLDER_ITEMS_LIST_CSS);
                for (WebElement folderName : folderNames)
                {
                    if (folderName.getText().equals(folder))
                    {
                        folderName.click();
                        logger.info("Folder \"" + folder + "\" selected");
                        if (length > 0)
                        {
                            drone.waitForElement(FOLDER_ITEMS_LIST_CSS, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                            drone.waitForElement(SELECTED_FOLDER_ITEMS_LIST_CSS, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        }
                        break;
                    }
                }
            }
            return new CopyOrMoveContentPage(drone);
        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable find the folder name. " + ne.getMessage());
        }
        catch (TimeoutException te)
        {
            logger.error("Unable find the folders css. " + te.getMessage());
        }
        throw new PageOperationException("Unable to select the folder path.");
    }
}