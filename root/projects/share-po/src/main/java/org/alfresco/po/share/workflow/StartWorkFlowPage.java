package org.alfresco.po.share.workflow;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Page object holds all elements of HTML page objects relating to Start
 * WorkFlow connect page.
 * 
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public class StartWorkFlowPage extends SharePage 
{

	private static final By WORKFLOW_DROP_DOWN_BUTTON = By.cssSelector("button[id$='default-workflow-definition-button-button']");
	private static final String WORKFLOW_TEXT = "Please select a workflow";
	private static final By WORKFLOW_BUTTON = By.cssSelector("button[id$='default-workflow-definition-button-button']");
	private static final By WORKFLOW_TITLE_LIST = By.cssSelector("li.yuimenuitem>span.title");
	private final Log logger = LogFactory.getLog(this.getClass());

	/**
	 * Constructor.
	 * 
	 * @param drone
	 *            WebDriver to access page
	 */
	public StartWorkFlowPage(WebDrone drone)
	{
		super(drone);
	}

	@SuppressWarnings("unchecked")
	@Override
	public StartWorkFlowPage render(RenderTime timer)
	{
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(50L);
                }
                catch (InterruptedException e)
                {
                }
                try
                {
                    drone.find(WORKFLOW_DROP_DOWN_BUTTON);
                    if (logger.isTraceEnabled())
                    {
                        logger.trace("!!!!!!======== found it ============= ");

                    }
                    break;
                }
                catch (NoSuchElementException e)
                {
                }
                finally
                {
                    timer.end();
                }
            }
        }
        return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public StartWorkFlowPage render(long time)
	{
		return render(new RenderTime(time));
	}

	@SuppressWarnings("unchecked")
	@Override
	public StartWorkFlowPage render()
	{
		return render(new RenderTime(maxPageLoadingTime));
	}

	/**
	 * Verify if workflow text is present on the page
	 * 
	 * @return true if exists
	 */
	public boolean isWorkFlowTextPresent()
	{
		return (isTextPresent(WORKFLOW_DROP_DOWN_BUTTON, WORKFLOW_TEXT));
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

        try
        {
            workflowText = drone.findAndWait(selector).getText().trim();
        }
        catch (TimeoutException e)
        {
            logger.info("Workflow drop down button not Present", e);
        }

        if (workflowText != null)
        {
            display = workflowText.contains(text);
        }

		return display;
	}

    /**
     * Method to get the workflow sub page for the workflow passed.
     * StartWorkFlow page is returned in common,for any of its subclass.
     * 
     * @param workFlowType
     * @param fromClass
     * @param fromClass
     * @return
     */
    public WorkFlow getWorkflowPage(WorkFlowType workFlowType)
	{
        if(workFlowType == null)
        {
            throw new IllegalArgumentException("Workflow Type can't be null");
        }
		drone.find(WORKFLOW_BUTTON).click();
        workFlowType.getTaskTypeElement(drone).click();
        return FactoryShareWorkFlow.getPage(drone, workFlowType);
	}

    public <T extends WorkFlowPage> T getCurrentPage()
    {
        WebElement dropdownBtn = drone.findAndWait(WORKFLOW_DROP_DOWN_BUTTON);
        String workFlowTypeString = dropdownBtn.getText();
        return FactoryShareWorkFlow.getPage(drone, WorkFlowType.getWorkflowType(workFlowTypeString));
    }

    /**
     * Method to get workflow types exists in select workflow dropdown
     * @return List of WorkFlowType
     */
    public List<WorkFlowType> getWorkflowTypes()
    {
        List<WorkFlowType> workFlowTypes = Collections.emptyList();
        try
        {
            drone.find(WORKFLOW_BUTTON).click();
            if(logger.isInfoEnabled())
            {
                logger.info("Clicked on WORKFLOW_BUTTON");
            }
            List<WebElement> workflowElements = drone.findAll(WORKFLOW_TITLE_LIST);
            workFlowTypes = new ArrayList<WorkFlowType>(workflowElements.size());

            for(WebElement workFlow: workflowElements)
            {
                workFlowTypes.add(WorkFlowType.getWorkflowTypeByTitle(workFlow.getText()));
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        // Click on WorkFlow button to close the drop down
        if(drone.find(WORKFLOW_TITLE_LIST).isDisplayed())
        {
            drone.find(WORKFLOW_BUTTON).click();
        }
        return workFlowTypes;
    }

    /**
     * Method to check if a given WorkFlowType is present in the select Workflow drop down
     * @param workFlowType
     * @return
     */
    public boolean isWorkflowTypePresent(WorkFlowType workFlowType)
    {
        if(workFlowType == null)
        {
            throw new IllegalArgumentException("Workflow Type can not be null");
        }
        return getWorkflowTypes().contains(workFlowType);
    }
}
