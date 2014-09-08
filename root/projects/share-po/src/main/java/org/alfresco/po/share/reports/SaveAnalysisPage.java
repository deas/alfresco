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

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

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
 * Page object for Save Analysis popup
 * 
 * @author jcule
 */
public class SaveAnalysisPage extends SharePage
{

    private static Log logger = LogFactory.getLog(SaveAnalysisPage.class);

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

    protected SaveAnalysisPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SaveAnalysisPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.xpath(SAVE_ANALYSIS)));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SaveAnalysisPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SaveAnalysisPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
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
    public SaveAnalysisPage enterAnalisysName(String name)
    {
        try
        {
            WebElement input = drone.findAndWait(By.cssSelector(SAVE_FILE_INPUT_FIELD));
            input.clear();
            input.sendKeys(name + "\n");
            return new SaveAnalysisPage(drone);

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
