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

package org.alfresco.po.share.reports;

import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Page object for Adhoc Analayzer page for creation/editing and saving of adhoc reports
 * 
 * @author jcule
 */
public class CreateEditAdhocReportPage extends AdhocAnalyzerPage
{
    private static Log logger = LogFactory.getLog(CreateEditAdhocReportPage.class);

    // save button
    private final static String SAVE_BUTTON = "span[id^='alfresco_buttons_AlfButton'] span[id^='alfresco_buttons_AlfButton']";

    // Site name
    private final static String SITE_NAME = "div[formula='[Sites].[Name]']";

    // Activity - Event Type
    private final static String EVENT_TYPE = "div[formula='[Activity].[Event Type]']";

    // Number of events
    private final static String EVENTS_NUMBER = "div[formula='[Measures].[Number of Events]']";

    //Day
    private final static String DAY = "div[formula='[Date].[Day]']";
    
    //View as table
    private final static String SHOW_TABLE = "div[id='cmdShowPivot']";
    
    //View as chart
    private final static String SHOW_CHART = "div[id='cmdShowChart']";
     
    //Select another chart type
    private final static String SELECT_ANOTHER_CHART_TYPE = "div[id='cmdSelectChartType']";
    
    //Table status bar
    private final static String TABLE_STATUS_BAR = "div[id='RPT001StatusBar']";
        
    // Site name - table header
    private final static String SITE_NAME_TABLE = "td[formula='[Sites].[Name]']";

    // Activity - Event Type - table header
    private final static String EVENT_TYPE_TABLE = "td[formula='[Activity].[Event Type]']";

    // Number of events - table header
    private final static String EVENTS_NUMBER_TABLE = "td[formula='[Measures].[Number of Events]']";

    //Day - table header
    private final static String DAY_TABLE = "td[formula='[Date].[Day]']";
    
    // Save Analysis title Save Analysis
    private final static String SAVE_ANALYSIS = "//span[text()='Save Analysis']";

    // Save file input field
    private final static String SAVE_FILE_INPUT_FIELD = "input[name='filename']";

    // Save file ok button
    private final static String SAVE_FILE_OK_BUTTON = "//span[text()='Ok']";

    // Cancel button
    private final static String CANCEL_BUTTON = "//span[text()='Cancel']";

    // Close Save Analysis
    private final static String CLOSE_SAVE_ANALYSIS = "span[title='Cancel']";

    
    
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
    public CreateEditAdhocReportPage clickOnSaveReportButton()
    {
        try
        {
            WebElement saveButton = drone.findAndWait(By.cssSelector(SAVE_BUTTON));
            saveButton.click();
            return new CreateEditAdhocReportPage(drone);

        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find save button. " + te);
        }
        throw new PageException("Unable to find save button element.");
    }

    /**
     * Rightclicks on Site Name field
     */
    
    public CreateEditAdhocReportPage rightClickOnSiteNameField()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            WebElement siteNameButton = drone.findAndWait(By.cssSelector(SITE_NAME));
            drone.rightClickOnElement(siteNameButton);
            drone.switchToDefaultContent();
            return new CreateEditAdhocReportPage(drone);

        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find Site Name field. " + te);
        }
        throw new PageException("Unable to find Site Name field.");
    }
    
    
    /**
     * Doubleclicks on Site Name field
     */
    public CreateEditAdhocReportPage doubleClickOnSiteNameField()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            WebElement siteName = drone.findAndWait(By.cssSelector(SITE_NAME));
            drone.doubleClickOnElement(siteName);
            drone.switchToDefaultContent();
            return new CreateEditAdhocReportPage(drone);

        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find Site Name field. " + te);
        }
        throw new PageException("Unable to find Site Name field.");
    }

    /**
     * Doubleclicks on Event Type field
     */
    
    public CreateEditAdhocReportPage doubleClickOnEventTypeField()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            WebElement eventType = drone.findAndWait(By.cssSelector(EVENT_TYPE));
            drone.doubleClickOnElement(eventType);
            drone.switchToDefaultContent();
            return new CreateEditAdhocReportPage(drone);

        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find Event Type field. " + te);
        }
        throw new PageException("Unable to find Event Type field.");
    }
    

    /**
     * Doubleclicks on Day field
     */
    
    public CreateEditAdhocReportPage doubleClickOnDayField()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            WebElement day = drone.findAndWait(By.cssSelector(DAY));
            drone.doubleClickOnElement(day);
            drone.switchToDefaultContent();
            return new CreateEditAdhocReportPage(drone);

        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find Day field. " + te);
        }
        throw new PageException("Unable to find Day field.");
    }
   
    
    /**
     * Doubleclicks on Number of Events field
     */
    
    public CreateEditAdhocReportPage doubleClickOnNumberOfEventsField()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            WebElement numberOfEvents = drone.findAndWait(By.cssSelector(EVENTS_NUMBER));
            drone.doubleClickOnElement(numberOfEvents);
            drone.switchToDefaultContent();
            return new CreateEditAdhocReportPage(drone);

        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find Number of Events field. " + te);
        }
        throw new PageException("Unable to find Number of Events field.");
    }
    
    
    /**
     * Clicks on Switch to chart
     */
    public CreateEditAdhocReportPage clickOnSwitchToChart()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            WebElement chartView = drone.findAndWait(By.cssSelector(SHOW_CHART));
            chartView.click();
            drone.switchToDefaultContent();
            return new CreateEditAdhocReportPage(drone);

        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find Swith to chart button. " + te);
        }
        throw new PageException("Unable to find Swith to chart button.");
    }

    
    /**
     * Clicks on Switch to table
     */
    public CreateEditAdhocReportPage clickOnSwitchToTable()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            WebElement tableView = drone.findAndWait(By.cssSelector(SHOW_TABLE));
            tableView.click();
            drone.switchToDefaultContent();
            return new CreateEditAdhocReportPage(drone);

        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find Swith to table button. " + te);
        }
        throw new PageException("Unable to find Swith to table button.");
    }
    
    /**
     * Gets table status bar
     * 
     * @return
     */
    public String[] getTableStatusBar()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            WebElement tableStatusBar = drone.find(By.cssSelector(TABLE_STATUS_BAR));
            String[] statusBarElements = tableStatusBar.getText().split(" ");
            drone.switchToDefaultContent();
            return statusBarElements;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Table status bar " + nse);
            throw new PageException("Unable to find Table status bar.", nse);
        }
    }
    
    /**
     * Checks if Site Name is displayed in table header
     * 
     * @return
     */
    public boolean isSiteNameDisplayed()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            WebElement siteName = drone.find(By.cssSelector(SITE_NAME_TABLE));
            boolean isSiteNameDisplayed = siteName.isDisplayed();
            drone.switchToDefaultContent();
            return isSiteNameDisplayed;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Site Name in table header " + nse);
            throw new PageException("Unable to find Site Name in table header.", nse);
        }
    }
    
    /**
     * Checks if Event Type is displayed in table header
     * 
     * @return
     */
    public boolean isEventTypeDisplayed()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            WebElement eventType = drone.find(By.cssSelector(EVENT_TYPE_TABLE));
            boolean isEventTypeDisplayed = eventType.isDisplayed();
            drone.switchToDefaultContent();
            return isEventTypeDisplayed;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Event Type in table header " + nse);
            throw new PageException("Unable to find Event Type in table header.", nse);
        }
    }   

    
    /**
     * Checks if Day is displayed in table header
     * 
     * @return
     */
    public boolean isDayDisplayed()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            WebElement day = drone.find(By.cssSelector(DAY_TABLE));
            boolean isDayDisplayed = day.isDisplayed();
            drone.switchToDefaultContent();
            return isDayDisplayed;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Day in table header " + nse);
            throw new PageException("Unable to find Day in table header.", nse);
        }
    } 
    
    /**
     * Checks if Events Number is displayed in table header
     * 
     * @return
     */
    public boolean isEventsNumberDisplayed()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            WebElement eventsNumber = drone.find(By.cssSelector(EVENTS_NUMBER_TABLE));
            boolean isEventsNumberDisplayed = eventsNumber.isDisplayed();
            drone.switchToDefaultContent();
            return isEventsNumberDisplayed;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Events Number in table header " + nse);
            throw new PageException("Unable to find Events Number in table header.", nse);
        }
    }       
    
    /**
     * Checks if Save Analysis popup displayed
     * 
     * @return
     */
    public boolean isSaveAnalysisDispalayed()
    {
        try
        {
            WebElement saveAnalysis = drone.find(By.xpath(SAVE_ANALYSIS));
            return saveAnalysis.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Save Analysis " + nse);
            throw new PageException("Unable to find Save Analysis.", nse);
        }
    }

    /**
     * Enter report file name in Save Analisys field
     */
    public CreateEditAdhocReportPage enterAnalisysName(String name)
    {
        try
        {
            WebElement input = drone.findAndWait(By.cssSelector(SAVE_FILE_INPUT_FIELD));
            input.clear();
            input.sendKeys(name + "\n");
            return new CreateEditAdhocReportPage(drone);

        }
        catch (NoSuchElementException te)
        {
            logger.error("Unable to find Save Analisys input field. " + te);
        }
        throw new PageException("Unable to find Save Analisys input field.");
    }

    /**
     * Clicks on Save Analisys Ok button
     */
    public CreateEditAdhocReportPage clickOnSaveAnalisysOkButton()
    {
        try
        {
            WebElement saveAnalisysOkButton = drone.findAndWait(By.xpath(SAVE_FILE_OK_BUTTON));
            saveAnalisysOkButton.click();
            return new CreateEditAdhocReportPage(drone);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find Save Analisys Ok button. " + te);
        }
        throw new PageException("Unable to find Save Analisys Ok button.");
    }

    /**
     * Clicks on Save Analisys Cancel button
     */
    public CreateEditAdhocReportPage clickOnSaveAnalisysCancelButton()
    {
        try
        {
            WebElement saveAnalisysCancelButton = drone.findAndWait(By.xpath(CANCEL_BUTTON));
            saveAnalisysCancelButton.click();
            return new CreateEditAdhocReportPage(drone);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find Save Analisys Cancel button. " + te);
        }
        throw new PageException("Unable to find Save Analisys Cancel button.");
    }

    /**
     * Clicks on Save Analisys Close button
     */
    public CreateEditAdhocReportPage clickOnSaveAnalisysCloseButton()
    {
        try
        {
            WebElement saveAnalisysCloseButton = drone.findAndWait(By.xpath(CLOSE_SAVE_ANALYSIS));
            saveAnalisysCloseButton.click();
            return new CreateEditAdhocReportPage(drone);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find Save Analisys Close button. " + te);
        }
        throw new PageException("Unable to find Save Analisys Close button.");
    }
}
