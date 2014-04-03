/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

package org.alfresco.po.share.workflow;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the HTML page to select and add content to workflow.
 * @author Abhijeet Bharade, Shan Nagarajan
 * @since  1.7.0
 */
public class SelectContentPage extends SharePage
{
    private static final Logger logger = Logger.getLogger(SelectContentPage.class);

    private final By folderUpButton = By.cssSelector("button[id$='_packageItems-cntrl-picker-folderUp-button']");
    private final By navigatorButton = By.cssSelector("button[id$='packageItems-cntrl-picker-navigator-button']");
    private final By navigateCompanyHome = By.cssSelector("ul[id$='packageItems-cntrl-picker-navigatorItems'] li:nth-of-type(1)>a");
    private final By pickerLeftPanel = By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-left'] a");
    private final By addedContents = By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-right'] .name");
    private final By addedContentsElements = By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-right']>div[id$='-cntrl-picker-selectedItems']>table>tbody.yui-dt-data>tr");
    @SuppressWarnings("unused")
	private final By availableContentElements = By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-right']>div[id$='-cntrl-picker-selectedItems']>table>tbody.yui-dt-data>tr");
    private final String sitesString = "Sites";
    private final String documentLibrary = "documentLibrary";
    private final By header = By.cssSelector("div[id$='packageItems-cntrl-picker-head']");
    private final String dashletEmptyPlaceholder = "table>tbody>tr>td.yui-dt-empty>div";
    private final By okButton = By.cssSelector("button[id$='packageItems-cntrl-ok-button']");
    private final By cancelButton = By.cssSelector("button[id$='packageItems-cntrl-cancel-button']");
    private final By closeButton = By.cssSelector("div[id$='_packageItems-cntrl-picker']>a.container-close");

    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public SelectContentPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectContentPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, 
                        getVisibleRenderElement(header),
                        getVisibleRenderElement(folderUpButton),
                        getVisibleRenderElement(navigatorButton),
                        getVisibleRenderElement(By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-left']")),
                        getVisibleRenderElement(By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-right']")),
                        getVisibleRenderElement(okButton),
                        getVisibleRenderElement(cancelButton),
                        new RenderElement(By.cssSelector(dashletEmptyPlaceholder), ElementState.INVISIBLE_WITH_TEXT, "Loading..."));
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectContentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectContentPage render(final long time)
    {
        return render(new RenderTime(time));
    }
    
    /**
     * Add Items set into {@link CompanyHome}.
     * @param companyHome - {@link CompanyHome}
     */
    public void addItems(CompanyHome companyHome)
    {
       
        Set<Site> sites = companyHome.getSites();
        
        if(sites != null)
        {
            drone.find(navigatorButton).click();
            drone.findAndWait(navigateCompanyHome).click();
            clickElementOnLeftPanel(sitesString);
            for (Site site : sites)
            {
                clickElementOnLeftPanel(site.getName());
                clickElementOnLeftPanel(documentLibrary);
                Set<Content> contents = site.getContents();
                for (Content content : contents)
                {
                    contentProcessor(content);
                }
            }
        }
        
    }
    
    private void contentProcessor(Content content)
    {
        if(!content.isFolder() && (content.getContents() == null || content.getContents().size() == 0))
        {
            addContent(content.getName());
        }
        else if(content.isFolder())
        {
            String name = content.getName();
            clickElementOnLeftPanel(name);
            Set<Content> contents = content.getContents();
            if(contents != null)
            {
                for (Content content2 : contents)
                {
                    contentProcessor(content2);
                }
            }
            clickFolderUpButton();
            renderCurrentAvailableItem(name);
        }
    }
    
    private void clickFolderUpButton()
    {
        drone.findAndWait(By.cssSelector("button[id$='packageItems-cntrl-picker-folderUp-button']")).click();
    }
    
    private void clickElementOnLeftPanel(String text)
    {
        if(StringUtils.isEmpty(text))
        {
            throw new IllegalArgumentException("Text can't be empty or null");
        }
        List<WebElement> elements = drone.findAndWaitForElements(pickerLeftPanel);
        
        for (WebElement webElement : elements)
        {
            if(text.equalsIgnoreCase(webElement.getText()))
            {
                webElement.click();
                break;
            }
        }
    }
    
    private void addContent(String name)
    {
        if(StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("Name can't be empty or null");
        }
        List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-left'] .yui-dt-data tr"));
        
        for (WebElement webElement : elements)
        {
            if(name.equalsIgnoreCase(webElement.findElement(By.cssSelector(".item-name")).getText()))
            {
                webElement.findElement(By.cssSelector(".addIcon")).click();

                break;
            }
        }
    }
    
    /**
     * Returns the Added items as Strings.
     * @return {@link List}
     */
    public List<String> getAddedItems()
    {
        List<String> items = new ArrayList<String>();
        List<WebElement> elements = drone.findAll(addedContents);
        if(elements != null)
        {
            for (WebElement webElement : elements)
            {
                items.add(webElement.getText());
            }
        }
        else 
        {
            items = Collections.emptyList();
        }
        return items;
    }

    /**
     * Render the element available in current items, it can be used to wait till element to be loaded into current available items.
     * @param name - Name of the Content to be rendered
     */
    public void renderCurrentAvailableItem(String name)
    {
        if(StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("Name can't be empty or null");
        }
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        outerloop:
        while (true)
        {
            timer.start();
            try
            {
                List<WebElement> elements = drone.findAll(By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-left'] .item-name a"));
                if(elements != null)
                {
                    
                    for (WebElement webElement : elements)
                    {
                        if(name.equalsIgnoreCase(webElement.getText()))
                        {
                            break outerloop;
                        }
                    }
                }
            }
            catch (NoSuchElementException te)
            {
                // Expected if the page has not rendered
            }
            catch (StaleElementReferenceException e)
            {
                // This occurs occasionally, as well
            }
            finally
            {
                timer.end();
            }
        }
    }

    /**
     * Method to click OK button
     * @return
     */
    public HtmlPage selectOKButton()
    {
        try
        {
            drone.find(okButton).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Unable to find \"OK\" button");
            }
        }
        throw new PageException("Unable to find \"OK\" button");
    }

    /**
     * Method to verify Folder Up button is Enabled or not
     * @return True if enabled
     */
    public boolean isFolderUpButtonEnabled()
    {
        try
        {
            return drone.find(folderUpButton).isEnabled();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"Folder Up\" button", nse);
        }
    }

    /**
     * Method to add given file from given site
     * @param fileName
     * @param siteName
     */
    public void addItemFromSite(String fileName, String siteName)
    {
        if(StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("File Name cannot be null or empty");
        }

        if(StringUtils.isEmpty(siteName))
        {
            throw new IllegalArgumentException("Site Name cannot be null or empty");
        }

        Content content = new Content();
        content.setName(fileName);
        content.setFolder(false);
        Set<Content> contents = new HashSet<Content>();
        contents.add(content);

        Set<Site> sites = new HashSet<Site>();
        Site site = new Site();
        site.setName(getSiteShortName(siteName));
        site.setContents(contents);
        sites.add(site);

        CompanyHome companyHome = new CompanyHome();
        companyHome.setSites(sites);
        addItems(companyHome);
    }

    /**
     * Method to verify Add icon is present
     * @return True if Add icon is present
     */
    public boolean isAddIconPresent(String fileName)
    {
        if(StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("FileName cannot be null");
        }
        List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-left'] .yui-dt-data tr"));
        if(elements.size() == 0)
        {
            throw new PageOperationException("File Name doesn't exists in the list");
        }
        for (WebElement webElement : elements)
        {
            if(fileName.equalsIgnoreCase(webElement.findElement(By.cssSelector(".item-name")).getText()))
            {
                return webElement.findElement(By.cssSelector(".addIcon")).isDisplayed();
            }
        }
        return false;
    }

    /**
     * Method to remove a user from Selected Users list
     * @param userName
     */
    public void removeItem(String fileName)
    {
        if(StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("File Name cannot be empty");
        }
        List<WebElement> selectedFiles = drone.findAll(addedContentsElements);
        if(selectedFiles.size() < 1)
        {
            throw new PageOperationException("File is not selected.");
        }
        for(WebElement file: selectedFiles)
        {
            if (file.findElement(By.cssSelector("h3.name")).getText().contains(fileName))
            {
                drone.mouseOverOnElement(file.findElement(By.cssSelector("a.remove-item")));
                file.findElement(By.cssSelector("a.remove-item")).click();
                break;
            }
        }
    }

    /**
     * Method to select Close button
     */
    public void selectCloseButton()
    {
        try
        {
            drone.find(closeButton).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Close Button" + nse);
        }
    }

    /**
     * Method to select Cancel button
     */
    public void selectCancelButton()
    {
        try
        {
            drone.find(cancelButton).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Cancel Button" + nse);
        }
    }


}
