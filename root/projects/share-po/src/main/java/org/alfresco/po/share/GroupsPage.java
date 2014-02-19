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
package org.alfresco.po.share;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author nshah
 * To create Groups this page object gets used.
 */
public class GroupsPage extends SharePage
{
    private static final String SHOW_ALL_LABEL = "label[for$='_default-show-all']";
    private static final String SHOW_ALL_CHK_BOX = "input[id$='_default-show-all']";

    private static final String BUTTON_BROWSE= "button[id$='default-browse-button-button']";
    private static final String BUTTON_SEARCH= "button[id$='default-search-button-button']";
    private static final String BUTTON_ADD = ".groups-newgroup-button";
    private static final String GROUP_NAMES = "a[class$='groups-item-group']";

    public GroupsPage(WebDrone drone)
    {
        super(drone);       
    }

    @SuppressWarnings("unchecked")
    @Override
    public GroupsPage  render(RenderTime timer)
    {
        RenderElement actionMessage = getActionMessageElement(ElementState.INVISIBLE);
        elementRender(timer, getVisibleRenderElement(By.cssSelector(BUTTON_BROWSE)), getVisibleRenderElement(By.cssSelector(BUTTON_SEARCH)),
                        getVisibleRenderElement(By.cssSelector(SHOW_ALL_LABEL)), getVisibleRenderElement(By.cssSelector(SHOW_ALL_CHK_BOX)), actionMessage);
       return this;
    }
    @SuppressWarnings("unchecked")
    @Override
    public GroupsPage render(final long time)
    {
        return render(new RenderTime(time));
    }
    @SuppressWarnings("unchecked")
    @Override
    public GroupsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    /**
     * @return
     */
    public GroupsPage navigateToAddAndEditGroups()
    {
        drone.findAndWait(By.cssSelector(BUTTON_BROWSE)).click();
        
        return this;
    }
    
    /**
     * @return
     */
    public NewGroupPage navigateToNewGroupPage()
    {
        drone.findAndWait(By.cssSelector(BUTTON_ADD)).click();
        return new NewGroupPage(drone);
    }
    
    /**
     * Get list of available groups.
     * @return
     */
    public List<String> getGroupList()
    {
        List<String> nameOfGroups = new ArrayList<String>();
        List<WebElement> groupElements = drone.findAndWaitForElements(By.cssSelector(GROUP_NAMES));
        for (WebElement webElement : groupElements)
        {
            nameOfGroups.add(webElement.getText());
        }
        return nameOfGroups;
    }

}
