/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.workflow;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * My WorkFlows page object, holds all element of the html page relating to share's
 * my workflows page.
 * 
 * @author Ranjith Manyam
 * @since 1.7
 */
public class MyWorkFlowsPage extends SharePage
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final By START_WORKFLOW_BUTTON = By.cssSelector("button[id$='-startWorkflow-button-button']");
    private static final By WORKFLOW_ROWS = By.cssSelector("tr.yui-dt-rec");
    private static final By ACTIVE_LINK = By.cssSelector("a[rel='active']");
    private static final By COMPLETED_LINK = By.cssSelector("a[rel='completed']");

    private static final By SUB_TITLE = By.cssSelector("h2[id$='_default-filterTitle']");

    private RenderElement loadingElement = new RenderElement(By.cssSelector(".yui-dt-loading"), ElementState.INVISIBLE);
    private RenderElement startWorkFlowButtonRender = RenderElement.getVisibleRenderElement(START_WORKFLOW_BUTTON);
    private RenderElement content = new RenderElement(By.cssSelector("div.yui-dt-liner"), ElementState.PRESENT);

    /**
     * Constructor.
     * 
     * @param drone
     *            WebDriver to access page
     */
    public MyWorkFlowsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyWorkFlowsPage render(RenderTime timer)
    {
        elementRender(timer, startWorkFlowButtonRender, loadingElement, content);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyWorkFlowsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyWorkFlowsPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if people finder title is present on the page
     * 
     * @return true if exists
     */
    protected boolean isTitlePresent()
    {
        return isBrowserTitle("Workflows I've Started");
    }

    /**
     * Clicks on Start workflow button.
     * 
     * @return {@link org.alfresco.webdrone.share.workflow.StartWorkFlowPage}
     */
    public HtmlPage selectStartWorkflowButton()
    {
        try
        {
            drone.findAndWait(By.cssSelector("button[id$='-startWorkflow-button-button']")).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException e)
        {
            logger.error("Not able to find start work flow button", e);
        }
        throw new PageException("Not able to find start work flow button");
    }

    /**
     * @param workFlowName
     * @return
     */
    private List<WebElement> findWorkFlowRow(String workFlowName)
    {
        if (StringUtils.isEmpty(workFlowName))
        {
            throw new IllegalArgumentException("Workflow Name can't be empty");
        }
        List<WebElement> workflowRowsElements = new ArrayList<WebElement>();

        try
        {
            List<WebElement> workFlowRows = drone.findAll(WORKFLOW_ROWS);
            if (null != workFlowRows && workFlowRows.size() > 0)
            {
                for (WebElement workFlowRow : workFlowRows)
                {

                    if (StringUtils.deleteWhitespace(workFlowName).equals(
                            StringUtils.deleteWhitespace(workFlowRow.findElement(By.cssSelector("h3 a")).getText())))
                    {
                        workflowRowsElements.add(workFlowRow);
                    }
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("No workflow found", nse);
            }
        }
        catch (StaleElementReferenceException se)
        {
        }
        return workflowRowsElements;
    }

    /**
     * Method to get workflow details for a given workflow
     * 
     * @param workFlowName
     * @return {@link WorkFlowDetails}
     */
    public List<WorkFlowDetails> getWorkFlowDetails(String workFlowName)
    {
        if (StringUtils.isEmpty(workFlowName))
        {
            throw new IllegalArgumentException("Workflow Name cannot be null");
        }
        List<WebElement> workFlowRow = findWorkFlowRow(workFlowName);
        if (workFlowRow.size() > 0)
        {
            List<WorkFlowDetails> workFlowDetailsList = new ArrayList<WorkFlowDetails>();
            for (WebElement workflow : workFlowRow)
            {
                WorkFlowDetails workFlowDetails = new WorkFlowDetails();
                workFlowDetails.setWorkFlowName(workflow.findElement(By.cssSelector("div.yui-dt-liner>h3>a")).getText());
                workFlowDetails.setDue(workflow.findElement(By.cssSelector("div.due>span")).getText());
                workFlowDetails.setStartDate(workflow.findElement(By.cssSelector("div[class^='started']>span")).getText());
                workFlowDetails.setType(workflow.findElement(By.cssSelector("div.type>span")).getText());
                workFlowDetails.setDescription(workflow.findElement(By.cssSelector("div.description>span")).getText());

                try
                {
                    if (workflow.findElement(By.cssSelector("div[class^='started']>span")).isDisplayed())
                    {
                        workFlowDetails.setEndDate(workflow.findElement(By.cssSelector("div[class^='ended']>span")).getText());
                    }
                }
                catch (NoSuchElementException nse)
                {

                }

                workFlowDetailsList.add(workFlowDetails);
            }
            return workFlowDetailsList;
        }
        return Collections.emptyList();
    }

    /**
     * Method to select a given WorkFlow
     * 
     * @param workFlowName
     * @return
     */
    public HtmlPage selectWorkFlow(String workFlowName)
    {
        if (StringUtils.isEmpty(workFlowName))
        {
            throw new IllegalArgumentException("Workflow Name cannot be null");
        }
        List<WebElement> workFlowRow = findWorkFlowRow(workFlowName);

        if (workFlowRow.size() == 0)
        {
            throw new PageException("No WorkFlows exists with name: " + workFlowName);
        }
        else if (workFlowRow.size() == 1)
        {
            workFlowRow.get(0).findElement(By.cssSelector("td.yui-dt-col-title>div.yui-dt-liner>h3>a[title='View History']")).click();
            return FactorySharePage.resolvePage(drone);
        }
        else if (workFlowRow.size() > 1)
        {
            throw new PageException("More than 1 WorkFlows exists with name: " + workFlowName);
        }

        throw new PageException("Select workflow failed");
    }

    /**
     * Method to check if a given workflow is displayed in MyWorkFlows page
     * 
     * @param workFlowName
     * @return True if workflow exists
     */
    public boolean isWorkFlowPresent(String workFlowName)
    {
        if (StringUtils.isEmpty(workFlowName))
        {
            throw new IllegalArgumentException("Work flow name is required");
        }
        try
        {
            String xpathExpression = String.format("//h3/a[contains(.,'%s')]", workFlowName);
            WebElement workFlowRow = drone.findAndWait(By.xpath(xpathExpression));
            return workFlowRow.isDisplayed();
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * Method to get the page subtitle (Active workflows, Completed workflows etc)
     * 
     * @return
     */
    public String getSubTitle()
    {
        try
        {
            return drone.findAndWait(SUB_TITLE).getText();
        }
        catch (TimeoutException te)
        {
            throw new PageException("Page Subtitle is not displayed", te);
        }
    }

    /**
     * Clicks on Active WorkFlows link.
     * 
     * @return {@link MyWorkFlowsPage}
     */
    public HtmlPage selectActiveWorkFlows()
    {
        drone.find(ACTIVE_LINK).click();
        drone.waitUntilVisible(SUB_TITLE, "Active Workflows", TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Clicks on Active WorkFlows link.
     * 
     * @return {@link MyWorkFlowsPage}
     */
    public HtmlPage selectCompletedWorkFlows()
    {
        drone.findAndWait(COMPLETED_LINK).click();
        drone.waitUntilVisible(SUB_TITLE, "Completed Workflows", TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Method to cancel given workflow. If more than one workflow found, cancel all workflows.
     * 
     * @param workFlowName
     */
    public HtmlPage cancelWorkFlow(String workFlowName)
    {
        List<WebElement> workFlowRow = findWorkFlowRow(workFlowName);
        if (workFlowRow.size() < 1)
        {
            throw new PageException("No workflows found with name: " + workFlowName);
        }
        try
        {
            for (int i = (workFlowRow.size() - 1); i >= 0; i--)
            {
                workFlowRow = findWorkFlowRow(workFlowName);
                drone.mouseOverOnElement(workFlowRow.get(i));
                workFlowRow.get(i).findElement(By.cssSelector("td.yui-dt-last>div.yui-dt-liner>div.workflow-cancel-link>a>span")).click();
                drone.waitForElement(By.cssSelector("div#prompt"), TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
                List<WebElement> buttons = drone.findAll(By.cssSelector("div#prompt>div.ft>span.button-group>span.yui-button>span.first-child>button"));
                for (WebElement button : buttons)
                {
                    if (button.getText().equals("Yes"))
                    {
                        button.click();
                        drone.waitUntilElementPresent(By.cssSelector("div#message>div.bd>span"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        drone.waitUntilElementDeletedFromDom(By.cssSelector("div#message>div.bd>span"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        break;
                    }
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Cancel workflow link doesn't exists for workflow: " + workFlowName, nse);
        }
        return this;
    }

    /**
     * Method to delete given workflow. If more than one workflow found, delete all workflows.
     * 
     * @param workFlowName
     */
    public void deleteWorkFlow(String workFlowName)
    {
        if (StringUtils.isEmpty(workFlowName))
        {
            throw new IllegalArgumentException("Workflow Name cannot be null");
        }
        List<WebElement> workFlowRow = findWorkFlowRow(workFlowName);
        if (workFlowRow.size() < 1)
        {
            throw new PageException("No workflows found with name: " + workFlowName);
        }
        try
        {
            for (WebElement workFlow : workFlowRow)
            {
                drone.mouseOverOnElement(workFlow);
                workFlow.findElement(By.cssSelector("td.yui-dt-last>div.yui-dt-liner>div.workflow-delete-link>a")).click();
                drone.waitForElement(By.cssSelector("div#prompt"), TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
                List<WebElement> buttons = drone.findAll(By.cssSelector("div#prompt>div.ft>span.button-group>span.yui-button>span.first-child>button"));
                for (WebElement button : buttons)
                {
                    if (button.getText().equals("Yes"))
                    {
                        button.click();
                        break;
                    }
                }
            }
        }
        catch (TimeoutException nse)
        {
            throw new PageException("Cancel workflow link doesn't exists for workflow: " + workFlowName, nse);
        }
        catch (NoSuchElementException nse)
        {
        }
    }
}
