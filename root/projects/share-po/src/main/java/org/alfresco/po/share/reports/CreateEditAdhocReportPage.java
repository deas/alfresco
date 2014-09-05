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

package org.alfresco.po.share.reports;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * 
 * Page object for Adhoc Analayzer page for creation/editing and saving of adhoc reports
 * 
 * @author jcule
 *
 */
public class CreateEditAdhocReportPage extends AdhocAnalyzerPage
{
    private static Log logger = LogFactory.getLog(CreateEditAdhocReportPage.class);
    
    //save button
    private final static String SAVE_BUTTON = "span[id^='alfresco_buttons_AlfButton'] span[id^='alfresco_buttons_AlfButton']";

    //Existing reports that can be opened
    private final static String EXISTING_REPORTS = "td[id^='uniqName'][id$='text']";
    
    //Activity - Event Type
    private final static String ACTIVITY_EVENT_TYPE =  "div[formula='[Activity].[Event Type]']";
    
    /**
     * Constructor
     * 
     * @param drone
     */
    protected CreateEditAdhocReportPage(WebDrone drone)
    {
        super(drone);
    }
    
    
    /**
     * Checks if save button displayed
     * 
     * @return
     */
    public boolean isSaveButtonDisplayed()
    {
        try
        {
            WebElement openButton = drone.find(By.cssSelector(SAVE_BUTTON));
            return openButton.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Save button " + nse);
            throw new PageException("Unable to find Save button.", nse);
        }
    }
    
    
    /**
     * Clicks on Save button
     */
    public HtmlPage clickOnSaveReportButton()
    {
        try
        {
            WebElement saveButton = drone.findAndWait(By.cssSelector(SAVE_BUTTON));
            saveButton.click();
            return FactorySharePage.resolvePage(drone);
         
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find save button. " + te);
        }
        throw new PageException("Unable to find save button element.");
    }
    
    /**
     * Returns the list of existing reports
     * 
     * @return
     */
    public List<WebElement> getExistingReports()
    {
        try
        {
            List<WebElement> existingReports = new ArrayList<WebElement>();
            try
            {
                existingReports = drone.findAll(By.cssSelector(EXISTING_REPORTS));

            }
            catch (NoSuchElementException nse)
            {
                logger.error("No existing reports " + nse);
            }
            return existingReports;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No existing reports " + nse);
            throw new PageException("Unable to find existing reports.", nse);
        }
         
    }
    
    
    /**
     * Gets an existing report element from the existing reports list by name
     * 
     * @param existingReportName
     * @return
     */
    public WebElement getExistingReport(String existingReportName)
    {
        List<WebElement> existingReports = getExistingReports();
        WebElement report = null; 
        for (WebElement existingReport : existingReports )
        {
            if (existingReportName.equals(existingReport.getText().trim()))
            {
                return report;

            }
        }
        return report;
    } 
    
    /**
     * Gets an existing report by name
     * 
     * @param existingReportName
     * @return
     */
    public String getExistingReportName(String existingReportName)
    {
        List<WebElement> existingReports = getExistingReports();
        String reportName = ""; 
        for (WebElement existingReport : existingReports )
        {
            if (existingReportName.equals(existingReport.getText().trim()))
            {
                reportName = existingReport.getText().trim();
                break;
            }
        }
        return reportName;
    }
   
    /**
     * Clicks on the existing report name
     * 
     * @param existingReportName
     * @return
     */
    public CreateEditAdhocReportPage clickOnExistingReport(String existingReportName)
    {
        try
        {
            List<WebElement> existingReports = drone.findAll(By.cssSelector(EXISTING_REPORTS));
            for (WebElement existingReport : existingReports)
            {
                if (existingReportName.equals(existingReport.getText()))
                {
                    System.out.println("EEEEEE **** " + existingReport.getText());
                    existingReport.click();
                    return new CreateEditAdhocReportPage(drone);
                }
            }
            throw new PageException("Existing report cannot be found in the list of existing reports");
        }
        catch (TimeoutException e)
        {
            logger.error("List of existing reports cannot be found");
            throw new PageException("Not able to find a list of existing reports.", e);
        }
    }
}
