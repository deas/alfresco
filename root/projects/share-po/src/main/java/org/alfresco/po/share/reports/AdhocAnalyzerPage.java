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
            WebElement reportName = drone.findAndWait(By.cssSelector(REPORT_TITLE));
            String title = reportName.getText();
            drone.switchToDefaultContent();
            return title;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No report title " + nse);
            throw new PageException("Unable to find report title.", nse);
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
}
