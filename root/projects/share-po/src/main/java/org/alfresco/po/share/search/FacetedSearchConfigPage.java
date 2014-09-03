/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.share.search;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

/**
 * 
 * @author Charu
 * @since 4.3
 */
@SuppressWarnings("unchecked")
public class FacetedSearchConfigPage extends SharePage
{
    private static final By ADD_NEW_FILTER = By.cssSelector("div[class=add-borders] span[class$=ButtonText]");
    private static final By PAGE_TITLE = By.cssSelector("h1[class=alf-menu-title]");    
    private static final Log logger = LogFactory.getLog(FacetedSearchConfigPage.class);
    
    public FacetedSearchConfigPage(WebDrone drone)
    {
        super(drone);
    }
    
    @Override
    public FacetedSearchConfigPage render(RenderTime timer)
    {
        RenderElement actionMessage = getActionMessageElement(ElementState.INVISIBLE);
        elementRender(timer, getVisibleRenderElement(ADD_NEW_FILTER),
                 actionMessage);
        return this;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.webdrone.Render#render()
     */
    @Override
    public FacetedSearchConfigPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    @Override
    public FacetedSearchConfigPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    
    /*
     * Get the title of facetedSearchConfig Page
     * 
     * @return
     */
    
    public String getTitle()
    {
        try
        {
            return drone.findAndWait(PAGE_TITLE).getText();
        } catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: FacetedSearchConfig" + toe.getMessage());
        }

    }
    
    /*
     * Click on Add New Filter button in facetedSearchConfig Page
     * 
     * @return
     */
    
    public CreateNewFilterPopUpPage clickAddNewFilter()
    {
        try
        {
            drone.findAndWait(ADD_NEW_FILTER).click();
            return new CreateNewFilterPopUpPage(drone);
        } catch (TimeoutException e)
        {        	
          logger.error("Unable to find the button: " + e.getMessage());            
        }
        throw new PageOperationException("Not visible Element: AddNewFilter");
    }  
        
    
    

}
