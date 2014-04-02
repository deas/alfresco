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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 *
 * Abstract of Workflowpage.
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public abstract class WorkFlowPage extends SharePage implements WorkFlow 
{
    private static Log logger = LogFactory.getLog(WorkFlowPage.class);

    private static final By SUBMIT_BUTTON = By.cssSelector("button[id$='form-submit-button']");
    private static final By REMOVE_ALL_BUTTON = By.xpath("//div[contains(@id, '_packageItems-cntrl-itemGroupActions')]/span[2]/span/button");
    private static final By NO_ITEM_SELECTED_MESSAGE = By.cssSelector("div[id$='_packageItems-cntrl-currentValueDisplay']>table>tbody.yui-dt-message>tr>td.yui-dt-empty>div");
    private static final By ITEM_ROW = By.cssSelector("div[id$='_packageItems-cntrl-currentValueDisplay']>table>tbody.yui-dt-data>tr");
    private static final By ITEM_NAME = By.cssSelector("h3.name");
    private static final By ERROR_MESSAGE = By.cssSelector("div.balloon>div.text>div");

    private static final By WORKFLOW_COULD_NOT_BE_STARTED_PROMPT_HEADER = By.cssSelector("#prompt_h");
    private static final By WORKFLOW_COULD_NOT_BE_STARTED_MESSAGE = By.cssSelector("#prompt>div.bd");

    private static final By WORKFLOW_DESCRIPTION_HELP_ICON = By.cssSelector("img[id$='_prop_bpm_workflowDescription-help-icon']");
    private static final By WORKFLOW_DESCRIPTION_HELP_MESSAGE = By.cssSelector("div[id$='_prop_bpm_workflowDescription-help']");

	/**
	 * Constructor.
	 * 
	 * @param drone
	 *            WebDriver to access page
	 */
    public WorkFlowPage(WebDrone drone)
	{
		super(drone);
	}

    /**
     * @param messageString - The message that should be entered in message box
     */
    @Override
    public void enterMessageText(String messageString)
    {
        if(StringUtils.isEmpty(messageString))
        {
            throw new IllegalArgumentException("Message cannot be Empty or null");
        }
        WebElement workFlowDescription = getMessageTextareaElement();// drone.findAndWait(MESSAGE_TEXT);
        workFlowDescription.sendKeys(messageString);
    }

    /**
     * @param date
     *            - The message that should be entered in message box
     */
    @Override
    public void enterDueDateText(String date)
    {
        if(StringUtils.isEmpty(date))
        {
            throw new IllegalArgumentException("Date cannot be Empty or null");
        }
        WebElement workFlowDescription = getDueDateElement();
        workFlowDescription.clear();
        workFlowDescription.sendKeys(date);
    }
    
    /**
     * Clicks on Select button for selecting reviewers
     * 
     */
    @Override
    public AssignmentPage selectReviewer()
    {
        getSelectReviewButton().click();
        return new AssignmentPage(drone);
    }

    /**
     * @return
     */
    protected abstract WebElement getSelectReviewButton();

    /**
     * Clicks on Select button for selecting reviewers
     * 
     */
    public HtmlPage selectStartWorkflow()
    {
        drone.find(SUBMIT_BUTTON).click();
        drone.waitUntilElementDeletedFromDom(SUBMIT_BUTTON, TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }


    /**
     * @return
     */
    protected abstract WebElement getStartWorkflowButton();

    /**
     * @param cloudUsers
     * @return boolean suggesting any user is blank.
     */
    protected boolean isReviewersBlank(List<String> cloudUsers)
    {
        for (String cloudUser : cloudUsers)
        {
            if (StringUtils.isEmpty(cloudUser))
            {
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns the WebElement for message textarea.
     * 
     * @return
     */
    abstract WebElement getMessageTextareaElement();
    
    /**
     * Mimics the click Add Items button.
     * @return {@link SelectContentPage}
     */
    public SelectContentPage clickAddItems()
    {
        clickUnamedButton("Add");
        return new SelectContentPage(drone);
    }
    
    private void clickUnamedButton(String name)
    {
        if(StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("Name cannot be Empty or null");
        }
        List<WebElement> elements = drone.findAll(By.cssSelector("button[type='button']"));
        for (WebElement webElement : elements)
        {
            if(name.equals(webElement.getText()))
            {
                webElement.click();
                break;
            }
        }
    }

    /**
     * Returns the WebElement for message textarea.
     * 
     * @return
     */
    abstract WebElement getDueDateElement();

    /**
     * Method to select given file from the given site.
     * @param fileName
     * @param siteName
     */
    public void selectItem(String fileName, String siteName)
    {
        if(StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("File Name cannot be Empty");
        }
        if(StringUtils.isEmpty(siteName))
        {
            throw new IllegalArgumentException("Site Name cannot be Empty");
        }
        SelectContentPage selectContentPage = clickAddItems().render();
        selectContentPage.addItemFromSite(fileName, siteName);
        selectContentPage.selectOKButton().render();
    }

    /**
     * Method to verify "No items selected" message is displayed
     * @return True if "No items selected" message is displayed
     */
    public boolean isNoItemsSelectedMessagePresent()
    {
        try
        {
            return drone.find(NO_ITEM_SELECTED_MESSAGE).isDisplayed() && drone.find(NO_ITEM_SELECTED_MESSAGE).getText().equals("No items selected");
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"No Items Selected\" Message", nse);
        }
    }

    /**
     * Method to verify Remove All button is Enabled
     * @return True if Remove All button
     */
    public boolean isRemoveAllButtonEnabled()
    {
        try
        {
            return drone.find(REMOVE_ALL_BUTTON).isEnabled();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"Remove\" All button", nse);
        }
    }

    private List<WebElement> getSelectedItemElements()
    {
        try
        {
            return drone.findAll(ITEM_ROW);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Item Rows", nse);
        }
    }

    /**
     * Method to get List of all selected workflow items
     * @return {@link List<SelectedWorkFlowItem>}
     */
    public List<SelectedWorkFlowItem> getSelectedItems()
    {
        List<SelectedWorkFlowItem> selectedWorkFlowItems = new ArrayList<SelectedWorkFlowItem>();
        try
        {
            List<WebElement> itemsRows = getSelectedItemElements();

            for(WebElement item: itemsRows)
            {
                selectedWorkFlowItems.add(new SelectedWorkFlowItem(item, drone));
            }
            return selectedWorkFlowItems;
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (PageOperationException poe)
        {
        }
        return Collections.emptyList();
    }

    /**
     * Method to get the Selected Items List for a given file name
     * @param fileName
     * @return {@link List<SelectedWorkFlowItem>}
     */
    public List<SelectedWorkFlowItem> getSelectedItem(String fileName)
    {
        if(StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("FileName cannot be empty");
        }
        List<WebElement> selectedItemElements = getSelectedItemElements();
        List<SelectedWorkFlowItem> selectedWorkFlowItems = new ArrayList<SelectedWorkFlowItem>();
        for (WebElement item : selectedItemElements)
        {
            if(fileName.equals(item.findElement(ITEM_NAME).getText()))
            {
                selectedWorkFlowItems.add(new SelectedWorkFlowItem(item, drone));
            }
        }
        return selectedWorkFlowItems;
    }

    /**
     * Method to verify if the item is added or not
     * @param fileName
     * @return True if the item is added
     */
    public boolean isItemAdded(String fileName)
    {
        if(StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("File Name can't null or empty");
        }
        List<SelectedWorkFlowItem> selectedWorkFlowItems = getSelectedItem(fileName);

        if(selectedWorkFlowItems.size()==0)
        {
            return false;
        }
        for(SelectedWorkFlowItem item: selectedWorkFlowItems)
        {
            if (fileName.equals(item.getItemName()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to verify Remove All button is Enabled
     * @return True if Remove All button
     */
    public void selectRemoveAllButton()
    {
        try
        {
            drone.find(REMOVE_ALL_BUTTON).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"Remove\" All button", nse);
        }
    }

    public boolean isErrorBalloonPresent()
    {
        try
        {

            return drone.find(ERROR_MESSAGE).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Unable to find Error PopUp");
            }
        }
        return false;
    }

    public String getErrorBalloonMessage()
    {
        try
        {
            return drone.find(ERROR_MESSAGE).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Error Message", nse);
        }
    }

    public String getWorkFlowCouldNotBeStartedPromptHeader()
    {
        try
        {
            return drone.findAndWait(WORKFLOW_COULD_NOT_BE_STARTED_PROMPT_HEADER).getText();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find WorkFlow Could NOT be started prompt" + te.getMessage());
        }
    }

    public String getWorkFlowCouldNotBeStartedPromptMessage()
    {
        try
        {
            return drone.find(WORKFLOW_COULD_NOT_BE_STARTED_MESSAGE).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find WorkFlow Could NOT be started message", nse);
        }
    }

    /**
     * Mimics the action of clicking the help icon.
     */
    public void clickHelpIcon()
    {
        try
        {
            drone.find(WORKFLOW_DESCRIPTION_HELP_ICON).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable find help icon" + nse.getMessage());
        }  
    }
    
    /**
     * Find the help text and return, else throw {@link PageOperationException}.
     * 
     * @return Help text
     */
    public String getHelpText()
    {
        try
        {
            return drone.find(WORKFLOW_DESCRIPTION_HELP_MESSAGE).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable find help text, Please click the help icon first." + nse.getMessage());
        } 
    }
}
