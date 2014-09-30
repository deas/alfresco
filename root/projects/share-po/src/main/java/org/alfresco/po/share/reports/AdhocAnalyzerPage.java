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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Page object for Adhoc Analyzer page
 * 
 * @author jcule
 */
public class AdhocAnalyzerPage extends SharePage
{
    private static Log logger = LogFactory.getLog(AdhocAnalyzerPage.class);

    protected final static String ANALYZE_BUTTON = "span[id^='alfresco_menus_AlfMenuBarPopup']";
    protected final static String CONTENT_USER_ACITVITIES_BUTTON = "td[id^='alfresco_menus_AlfMenuItem']";
    protected final static String OPEN_BUTTON = "span[id^='alfresco_pentaho_menus_AnalysesMenuBarPopup']";
    protected final static String REPORT_TITLE = "div[id='RPT001ReportName']";
    protected final static String ALFRESCO_PENTAHO_IFRAME_ID = "iframe[id='alfrescoPentahoXAnalyzer']";
    protected final static String THERE_ARE_NO_ANALYSES = "//td[text()='(There are no analyses)']"; 
    private final static String EXISTING_REPORTS = "td[id^='uniqName'][id$='text']";

    public AdhocAnalyzerPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdhocAnalyzerPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdhocAnalyzerPage render(RenderTime timer)
    {
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdhocAnalyzerPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Checks if Open button displayed
     * 
     * @return
     */
    public boolean isOpenButtonDisplayed()
    {
        try
        {
            WebElement openButton = drone.find(By.cssSelector(OPEN_BUTTON));
            return openButton.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Open button " + nse);
            throw new PageException("Unable to find Open button.", nse);
        }
    }

    /**
     * Checks if (There are no analyses) message is displayed
     * 
     * @return
     */
    public boolean isThereAreNoAnalysesDisplayed()
    {
        try
        {
            drone.waitForElement(By.xpath(THERE_ARE_NO_ANALYSES), TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
            WebElement noAnalyses = drone.findAndWait(By.xpath(THERE_ARE_NO_ANALYSES), TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
            return noAnalyses.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No (There are no analyses) message " + nse);
            throw new PageException("Unable to find (There are no analyses) message.", nse);
        }
    }
    
    /**
     * Clicks on Analyze button
     */
    public void clickOnAnalyzeButton()
    {
        try
        {
            WebElement analyzeButton = drone.findAndWait(By.cssSelector(ANALYZE_BUTTON));
            analyzeButton.click();
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find analyze button. " + te);
        }
    }

    /**
     * Checks if Create Content, Users and Activities button displayed
     * 
     * @return
     */
    public boolean isCreateContentUsersActivitiesDisplayed()
    {
        try
        {
            WebElement createContentUsersActivities = drone.find(By.cssSelector(CONTENT_USER_ACITVITIES_BUTTON));
            return createContentUsersActivities.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Create Content, Users, Activities button " + nse);
            throw new PageException("Unable to find Create Content, Users, Activities button.", nse);
        }
    }

    /**
     * Clicks on Create button
     */
    public CreateEditAdhocReportPage clickOnCreateReportButton()
    {
        try
        {
            WebElement createButton = drone.findAndWait(By.cssSelector(CONTENT_USER_ACITVITIES_BUTTON));
            createButton.click();
            return new CreateEditAdhocReportPage(drone);

        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find create button. " + te);
        }
        throw new PageException("Unable to find create button element.");
    }

    /**
     * Clicks on Open button
     */
    public CreateEditAdhocReportPage clickOnOpenReportButton()
    {
        try
        {
            WebElement openButton = drone.findAndWait(By.cssSelector(OPEN_BUTTON));
            openButton.click();
            return new CreateEditAdhocReportPage(drone);

        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find open button. " + te);
        }
        throw new PageException("Unable to find open button element.");
    }

    
    /**
     * Returns report title
     * 
     * @return
     */
    public String getReportTitle()
    {
        try
        {
            drone.switchToFrame(getAnalyzerIframeId());
            drone.waitForElement(By.cssSelector(REPORT_TITLE), TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
            WebElement reportName = drone.find(By.cssSelector(REPORT_TITLE));
            String title = reportName.getText();
            drone.switchToDefaultContent();
            return title;
        }
        catch (TimeoutException toe)
        {
            logger.error("No report title " + toe);
            throw new PageException("Unable to find report title.", toe);
        }
    }

    /**
     * Returns report title
     * 
     * @return
     */
    public String getAnalyzerIframeId()
    {
        try
        {
            WebElement analyzerIframeId = drone.findAndWait(By.cssSelector(ALFRESCO_PENTAHO_IFRAME_ID));
            return analyzerIframeId.getAttribute("id");
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No analyzer iframe id " + nse);
            throw new PageException("Unable analyzer iframe id.", nse);
        }
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
        for (WebElement existingReport : existingReports)
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
        for (WebElement existingReport : existingReports)
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
