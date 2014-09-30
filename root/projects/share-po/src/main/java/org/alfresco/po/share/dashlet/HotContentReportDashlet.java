/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.po.share.dashlet;

import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * 
 * Represents Hot Content Report Dashlet
 * 
 * @author jcule
 *
 */
public class HotContentReportDashlet extends AdhocAnalyzerDashlet
{

    private static Log logger = LogFactory.getLog(HotContentReportDashlet.class);
    
    private static final String NO_DATA = "span[class='noDataHeader']";
    
    protected HotContentReportDashlet(WebDrone drone)
    {
        super(drone);
    }
    
    
    /**
     * Checks if This report has no data. message is displayed
     * 
     * @return
     */
    public boolean isNoDataDisplayed()
    {
        try
        {
            WebElement noData = drone.find(By.cssSelector(NO_DATA));
            return noData.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No This report has no data. message " + nse);
            throw new PageException("Unable to find This report has no data. message.", nse);
        }
    }    

}
