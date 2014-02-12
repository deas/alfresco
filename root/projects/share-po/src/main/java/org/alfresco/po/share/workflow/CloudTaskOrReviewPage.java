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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


/**
 * Represent elements found on the HTML page relating to the CloudTaskOrReviewPage page load.
  * @author Siva Kaliyappan
  * @since 1.6.2
  */
public class CloudTaskOrReviewPage extends WorkFlowPage
{

    private static final By ADDED_FILE_ROW = By.cssSelector("div[id$='_assoc_packageItems-cntrl'] .yui-dt-data>tr");
    private static final By VIEW_MORE_ACTIONS = By.cssSelector("a.view_more_actions");
    private static final By REMOVE_BUTTON = By.cssSelector("a[class^='remove-list-item']");
    private static final By AFTER_COMPLETION_DROPDOWN = By.cssSelector("select[id$='hwf_retainStrategy']");
    private static final By PRIORITY_DROPDOWN = By.cssSelector("select[id$='_bpm_workflowPriority']");
    private static final By DUE_DATE = By.cssSelector("input[id$='workflowDueDate-cntrl-date']");
    private static final By LOCK_ON_PREMISE = By.cssSelector("input[id$='lockOnPremiseCopy-entry']");
    private static final By MESSAGE_TEXT = By.cssSelector("textarea[id$='_workflowDescription']");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[id$='form-submit-button']");
    private static final By TYPE_DROP_DOWN_BUTTON = By.cssSelector("select[id$='default-startWorkflowForm-alf-id3_prop_hwf_cloudWorkflowType']");
    private static final By DESTINATION_BUTTON = By.cssSelector("button[id$='default-startWorkflowForm-alf-id3_prop_hwf_cloudDestination-select-button-button']");
    private static final By ASSIGNMENT_BUTTON = By.cssSelector("button[id$='yui-gen24-button']");
    private static final String TYPE_DROP_DOWN_BUTTON_SELECTED_TEXT = "Simple Cloud Task";
    private static final By REQUIRED_APPROVAL_PERCENTAGE = By.cssSelector("input[id$='_requiredApprovalPercentage']");

    private static final By DESTINATION_NETWORK = By.cssSelector("span[id$='_hwf_cloudDestination-tenant']");
    private static final By DESTINATION_SITE = By.cssSelector("span[id$='_hwf_cloudDestination-site']");
    private static final By DESTINATION_FOLDER = By.cssSelector("span[id$='_hwf_cloudDestination-folder']");

    private final Log logger = LogFactory.getLog(this.getClass());

    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public CloudTaskOrReviewPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudTaskOrReviewPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudTaskOrReviewPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudTaskOrReviewPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to fill in the details for form and submit.
     * formDetails to get the form details
     * @return HtmlPage
     */
    @Override
    public HtmlPage startWorkflow(WorkFlowFormDetails formDetails) throws InterruptedException
    {
    	if (formDetails == null || StringUtils.isEmpty(formDetails.getSiteName()) || StringUtils.isEmpty(formDetails.getMessage()) || formDetails.getReviewers().size() < 1 || isReviewersBlank(formDetails.getReviewers()))
        {
            throw new UnsupportedOperationException("siteName or message or cloudUsers cannot be blank");
        }

    	enterMessageText(formDetails.getMessage());

        if(formDetails.getDueDate() != null)
        {
            enterDueDateText(formDetails.getDueDate());
        }

        if(formDetails.getApprovalPercentage()!=null)
        {
            enterRequiredApprovalPercentage(formDetails.getApprovalPercentage());
        }

        selectTask(formDetails.getTaskType());

        DestinationAndAssigneePage destinationAndAssigneePage = selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectSite(formDetails.getSiteName());
        destinationAndAssigneePage.selectSubmitButtonToSync();

        AssignmentPage assignmentPage = selectAssignmentPage().render();
        assignmentPage.selectAssignment(formDetails.getReviewers());
        
        selectLockOnPremiseCheckbox(formDetails.isLockOnPremise());
        
        selectAfterCompleteDropDown(formDetails.getContentStrategy());
        selectPriorityDropDown(formDetails.getTaskPriority());

        drone.findAndWait(SUBMIT_BUTTON).click();
        drone.waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }
    
    /**
     * Verify if workflow text is present on the page
     * @return true if exists
     */
    public boolean isTaskTypeSelected(TaskType taskType) 
    {
        return (isTextPresent(TYPE_DROP_DOWN_BUTTON, TYPE_DROP_DOWN_BUTTON_SELECTED_TEXT));
    }


    /**
     * Selects specific tasks on the pageS
     *
     * @return true if exists
     */
    public void selectTask(TaskType taskType)
    {
        WebElement dropDown = drone.findAndWait(TYPE_DROP_DOWN_BUTTON);
        Select select = new Select(dropDown);
        select.selectByIndex(taskType.ordinal());
    }
    
	/**
	 * Method to check for test in WorkFlow page
	 * 
	 * @param selector
	 * @param text
	 * @return
	 */
	protected boolean isTextPresent(By selector, String text)
	{
		boolean display = false;
		String workflowText = null;

		try {
			workflowText = drone.findAndWait(selector).getText().trim();
		} catch (NoSuchElementException e) {
			logger.info("Workflow drop down button not Present", e);
		}

		if (workflowText != null) {
			display = workflowText.contains(text);
		}

		return display;
	}

    /**
     * Returns the WebElement for message textarea.
     * 
     * @return
     */
    @Override
    protected WebElement getMessageTextareaElement()
    {
        return drone.findAndWait(MESSAGE_TEXT);
    }

    /**
     * Returns the WebElement for message textarea.
     * 
     * @return
     */
    @Override
    protected WebElement getDueDateElement()
    {
        return drone.findAndWait(DUE_DATE);
    }

    /**
     * Returns the WebElement for Select reviewer button.
     * 
     * @return
     */
    @Override
    protected WebElement getSelectReviewButton()
    {
        return drone.findAndWait(By.cssSelector("button[id$='yui-gen24-button']"));
    }

    /**
     * Returns the WebElement for Start workflow button.
     * 
     * @return
     */
    @Override
    protected WebElement getStartWorkflowButton()
    {
        return drone.findAndWait(By.cssSelector("button[id$='-form-submit-button']"));
    }

    /**
     * Mimics the action of selecting the Destination and Assignee button.
     * 
     * @return HtmlPage response page object
     */
    public DestinationAndAssigneePage selectDestinationAndAssigneePage()
    {
        drone.findAndWait(DESTINATION_BUTTON).click();
        return new DestinationAndAssigneePage(drone);
    }

    /**
     * Mimics the action of selecting the Lock on premise checkbox.
     * 
     * @param toCheck
     * 
     * @return HtmlPage response page object
     */
    public void selectLockOnPremiseCheckbox(boolean toCheck)
    {
        WebElement checkbox = drone.findAndWait(LOCK_ON_PREMISE);
        if (toCheck != checkbox.isSelected())
        {
            checkbox.click();
        }
    }

    /**
     * Mimics the action of selecting the Assignment button.
     * 
     * @return HtmlPage response page object
     */
    public AssignmentPage selectAssignmentPage()
    {
        drone.findAndWait(ASSIGNMENT_BUTTON).click();
        return new AssignmentPage(drone);
    }
    
    /**
     * @return
     */
    public String getErrorMessage()
    {
        try
        {
            drone.find(DESTINATION_BUTTON).click();
            WebElement ele = drone.findAndWait(By.cssSelector("div>span.message"));
            return ele.getText();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding an element");
        }
        throw new PageException();
    }

    /**
     * Mimics the action of selecting the Remove button on an added document.
     */
    public void selecRemoveBtn(String filename)
    {
        performActionForFile(filename, REMOVE_BUTTON);
    }

    /**
     * Mimics the action of selecting the View more actions button on an added
     * document.
     * 
     */
    public DocumentDetailsPage selectViewMoreActionsBtn(String filename)
    {
        performActionForFile(filename, VIEW_MORE_ACTIONS);
        return new DocumentDetailsPage(drone);
    }

    /**
     * @param filename
     */
    private void performActionForFile(String filename, By action)
    {
        if (filename == null || action == null)
        {
            throw new UnsupportedOperationException("Both filename and action should not be null");
        }
        WebElement filerow = findFileRow(filename);
        if(filerow != null){
            filerow.findElement(action).click();
        }
        else
        {
            logger.error("File not added.");
            throw new PageException("File not found");
        }
    }

    /**
     * @param filename
     * @return
     */
    private WebElement findFileRow(String filename)
    {
        List<WebElement> fileRows = drone.findAndWaitForElements(ADDED_FILE_ROW, maxPageLoadingTime);
        if (null != fileRows && fileRows.size() > 0)
        {
            for (WebElement fileRow : fileRows)
            {
                if (filename.equals(fileRow.findElement(By.cssSelector("h3 a")).getText()))
                    return fileRow;
            }
        }
        return null;
    }

    /**
     * Selects the Status drop down list.
     * 
     * @param strategy
     */
    public void selectAfterCompleteDropDown(KeepContentStrategy strategy)
    {
        WebElement statusSelectDropDown = drone.findAndWait(AFTER_COMPLETION_DROPDOWN);
        statusSelectDropDown.click();
        statusSelectDropDown.sendKeys(strategy.getStrategy());
        statusSelectDropDown.sendKeys(Keys.TAB);
    }

    /**
     * Selects the priority down list.
     */
    public void selectPriorityDropDown(Priority priority)
    {
        WebElement priorityDropDown = drone.findAndWait(PRIORITY_DROPDOWN);
        priorityDropDown.click();
        priorityDropDown.sendKeys(priority.toString());
        priorityDropDown.sendKeys(Keys.TAB);
    }

    /**
     * Method to enter Required Approval Percentage
     * @param percentage
     */
    public void enterRequiredApprovalPercentage(String percentage)
    {
        if(StringUtils.isEmpty(percentage))
        {
            throw new IllegalArgumentException("Percentage cannot be null or Empty");
        }

        try
        {
            WebElement requiredApprovalPercentage = drone.find(REQUIRED_APPROVAL_PERCENTAGE);
            requiredApprovalPercentage.clear();
            requiredApprovalPercentage.sendKeys(percentage);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Required Approval Percentage");
        }
    }

    public String getDestinationNetwork()
    {
        return getElementText(DESTINATION_NETWORK);
    }

    public String getDestinationSite()
    {
        return getElementText(DESTINATION_SITE);
    }

    public String getDestinationFolder()
    {
        return getElementText(DESTINATION_FOLDER);
    }
}


