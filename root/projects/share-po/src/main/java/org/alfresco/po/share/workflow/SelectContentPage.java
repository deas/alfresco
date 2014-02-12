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
import org.alfresco.webdrone.exception.PageRenderTimeException;
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

    private final By NAVIGATOR_BUTTON = By.cssSelector("button[id$='packageItems-cntrl-picker-navigator-button']");
    private final By NAVIGATE_TO_COMPANY_HOME = By.cssSelector("ul[id$='packageItems-cntrl-picker-navigatorItems'] li:nth-of-type(1)>a");
    private final By PICKER_LEFT_PANEL = By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-left'] a");
    private final By ADDED_CONTENTS = By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-right'] .name");
    private final String SITES = "Sites";
    private final String DOCUMENT_LIBRARY = "documentLibrary";
    private final By HEADER = By.cssSelector("div[id$='packageItems-cntrl-picker-head']");
    private static final String DASHLET_EMPTY_PLACEHOLDER = "table>tbody>tr>td.yui-dt-empty>div";
    private final By OK_BUTTON = By.cssSelector("button[id$='packageItems-cntrl-ok-button']");
    private final By CANCEL_BUTTON = By.cssSelector("button[id$='packageItems-cntrl-cancel-button']");
    
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
                        getVisibleRenderElement(HEADER),
                        getVisibleRenderElement(By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-left']")),
                        getVisibleRenderElement(By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-right']")),
                        getVisibleRenderElement(OK_BUTTON),
                        getVisibleRenderElement(CANCEL_BUTTON),
                        new RenderElement(By.cssSelector(DASHLET_EMPTY_PLACEHOLDER), ElementState.INVISIBLE_WITH_TEXT, "Loading..."));
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
            drone.find(NAVIGATOR_BUTTON).click();
            drone.findAndWait(NAVIGATE_TO_COMPANY_HOME).click();
            clickElementOnLeftPanel(SITES);
            for (Site site : sites)
            {
                clickElementOnLeftPanel(site.getName());
                clickElementOnLeftPanel(DOCUMENT_LIBRARY);
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
        List<WebElement> elements = drone.findAndWaitForElements(PICKER_LEFT_PANEL);
        
        for (WebElement webElement : elements)
        {
            if(webElement.getText().equalsIgnoreCase(text))
            {
                webElement.click();
                break;
            }
        }
    }
    
    private void addContent(String name)
    {
        List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-left'] .yui-dt-data tr"));
        
        for (WebElement webElement : elements)
        {
            if(webElement.findElement(By.cssSelector(".item-name")).getText().equalsIgnoreCase(name))
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
        List<WebElement> elements = drone.findAll(ADDED_CONTENTS);
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
                        if(webElement.getText().equalsIgnoreCase(name))
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
            drone.find(OK_BUTTON).click();
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
    
}
