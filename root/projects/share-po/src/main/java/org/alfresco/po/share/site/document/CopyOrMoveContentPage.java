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
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.LinkedList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.webdrone.HtmlPage;
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
public class CopyOrMoveContentPage  extends ShareDialogue
{
    private static Log logger = LogFactory.getLog(CopyOrMoveContentPage.class);

    private final By folderPathElementId = By.cssSelector("div[id$='default-copyMoveTo-treeview']>div.ygtvitem, div[id$='_default-ruleConfigAction-destinationDialog-treeview']>div.ygtvitem");
    private final RenderElement footerElement = getVisibleRenderElement(By.cssSelector("div[id$='default-copyMoveTo-wrapper'] div.bdft, div[id$='_default-ruleConfigAction-destinationDialog-wrapper'] div.bdft"));
    private final RenderElement headerElement = getVisibleRenderElement(By.cssSelector("div[id$='default-copyMoveTo-title'], div[id$='_default-ruleConfigAction-destinationDialog-title']"));
    private final By destinationListCss = By.cssSelector(".mode.flat-button>div>span>span>button");
    private final By siteListCss = By.cssSelector("div.site>div>div>a>h4");
    private final By defaultDocumentsFolderCss = By.cssSelector("div.path>div[id$='default-copyMoveTo-treeview']>div.ygtvitem>div.ygtvchildren>div.ygtvitem>table.ygtvtable>tbody>tr>td>span.ygtvlabel,"
            + "div.path>div[id$='_default-ruleConfigAction-destinationDialog-treeview']>div.ygtvitem>div.ygtvchildren>div.ygtvitem>table.ygtvtable>tbody>tr>td>span.ygtvlabel");
    private final By folderItemsListCss = By.cssSelector("div.path div.ygtvitem>div.ygtvchildren>div.ygtvitem>table.ygtvtable span.ygtvlabel");
    private final By selectedFolderItemsListCss = By.cssSelector("div.path div.ygtvitem>div.ygtvchildren>div.ygtvitem.selected>div.ygtvchildren>div.ygtvitem span.ygtvlabel");
    private final By copyMoveOkButtonCss = By.cssSelector("button[id$='default-copyMoveTo-ok-button'], button[id$='_default-ruleConfigAction-destinationDialog-ok-button']");
    private final By copyMoveCancelButtonCss = By.cssSelector("button[id$='default-copyMoveTo-cancel-button'], button[id$='_default-ruleConfigAction-destinationDialog-cancel']");
    private final By copyMoveDialogCloseButtonCss = By.cssSelector("div[id$='default-copyMoveTo-dialog'] .container-close, div[id$='_default-ruleConfigAction-destinationDialog-dialog'] .container-close");
    private final By copyMoveDialogTitleCss = By.cssSelector("div[id$='default-copyMoveTo-title'], div[id$='_default-ruleConfigAction-destinationDialog-title']");
   
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
        elementRender(timer, headerElement, footerElement);
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
            title = drone.findAndWait(copyMoveDialogTitleCss).getText();
        }
        catch (TimeoutException e)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Unable to find the Copy/Move Dialog Css : ", e);
            }
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
            for (WebElement destination : drone.findAndWaitForElements(destinationListCss))
            {
                destinations.add(destination.getText());
            }
        }
        catch (TimeoutException e)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Unable to get the list of destionations : ", e);
            }
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
            for (WebElement site : drone.findAndWaitForElements(siteListCss))
            {
                sites.add(site.getText());
            }
        }
        catch (TimeoutException e)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Unable to get the list of sites : ", e);
            }
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
            for (WebElement folder : drone.findAndWaitForElements(folderItemsListCss))
            {
                folders.add(folder.getText());
            }
        }
        catch (TimeoutException e)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Unable to get the list of folders : ", e);
            }
        }
        return folders;
    }
    
    /**
     * This method finds the clicks on copy/move button.
     * 
     * @return HtmlPage Document library page/ Repository Page
     */
    public HtmlPage selectOkButton()
    {
        try
        {
            drone.findAndWait(copyMoveOkButtonCss).click();
            drone.waitForElement(By.cssSelector("div.bd>span.message"),
                    SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            drone.waitUntilElementDeletedFromDom(By.cssSelector("div.bd>span.message"),
                    SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return FactorySharePage.resolvePage(drone);
        } 
        catch (TimeoutException e)
        {
            logger.error("Unable to find the Copy/Move Button Css : ", e);
            throw new PageException("Unable to find the Copy/Move button on Copy/Move Dialog.");
        }
    }
    
    
    /**
     * This method finds the clicks on cancel button and 
     * control will be on HTML page DocumentLibrary Page/Repository Page
     * 
     */
    public HtmlPage selectCancelButton()
    {
        try
        {
            drone.findAndWait(copyMoveCancelButtonCss).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the cancel button Css : ", e);
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
            drone.findAndWait(copyMoveDialogCloseButtonCss).click();
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the close button Css : ", e);
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
            for (WebElement destination : drone.findAndWaitForElements(destinationListCss))
            {
                if (destination.getText() != null)
                {
                    if (destination.getText().equalsIgnoreCase(destinationName))
                    {
                        destination.click();
                        if(destinationName.contains("Sites"))
                        {
                        drone.waitForElement(siteListCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        }
                        else if ((destinationName.contains("Repository")) || (destinationName.contains("Shared Files")) || (destinationName.contains("My Files")))
                        { 
                            drone.waitForElement(folderPathElementId, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        }
                        return new CopyOrMoveContentPage(drone);
                    }
                }
            }
        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable to find the inner text of destionation", ne);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of destionations",e);
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
            for (WebElement site : drone.findAndWaitForElements(siteListCss))
            {
                if (site.getText() != null)
                {
                    if (site.getText().equalsIgnoreCase(siteName))
                    {
                        site.click();
                        drone.waitForElement(defaultDocumentsFolderCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        drone.waitForElement(folderItemsListCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));

                        return new CopyOrMoveContentPage(drone);
                    }
                }
            }
            throw new PageOperationException("Unable to find the site: " + siteName);
        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable to find the inner text of site", ne);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of sites", e);
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
    public CopyOrMoveContentPage selectPath(String... folderPath)
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
                try
                {
                    drone.waitForElement(By.id("AlfrescoWebdronez1"), SECONDS.convert(WAIT_TIME_3000, MILLISECONDS));
                } 
                catch (TimeoutException e) {}
                //drone.waitFor(WAIT_TIME_3000);
                folderNames = drone.findAndWaitForElements(folderItemsListCss);
                boolean selected = false;
                for (WebElement folderName : folderNames)
                {
                    if (folderName.getText().equalsIgnoreCase(folder))
                    {   
                        selected = true;
                        folderName.click();
                        logger.info("Folder \"" + folder + "\" selected");
                        if (length > 1)
                        {
                            drone.waitForElement(folderItemsListCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                            drone.waitForElement(selectedFolderItemsListCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        }
                        break;
                    }
                }
                length--;
                if (!selected)
                {
                   throw new PageException("Cannot select the folder metioned in the path");
                }
            }
            return new CopyOrMoveContentPage(drone);
        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable find the folder name. ", ne);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable find the folders css. ", te);
        }
        throw new PageOperationException("Unable to select the folder path.");
    }

    protected By getCopyMoveOkButtonCss()
    {
        return copyMoveOkButtonCss;
    }
}