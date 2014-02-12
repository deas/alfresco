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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * Abstract of Workflowpage.
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public abstract class WorkFlowPage extends SharePage implements WorkFlow 
{
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
        WebElement workFlowDescription = getMessageTextareaElement();// drone.findAndWait(MESSAGE_TEXT);
        workFlowDescription.sendKeys(messageString);
    }

    /**
     * @param messageString
     *            - The message that should be entered in message box
     */
    @Override
    public void enterDueDateText(String date)
    {
        WebElement workFlowDescription = getDueDateElement();
        workFlowDescription.sendKeys(date);
    }
    
    /**
     * Clicks on Select button for selecting reviewers
     * 
     * @param messageString
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
     * @param messageString
     */
    public HtmlPage selectStartWorkflow()
    {
        getStartWorkflowButton().click();
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
                    return true;
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
        List<WebElement> elements = drone.findAll(By.cssSelector("button[type='button']"));
        for (WebElement webElement : elements)
        {
            if(webElement.getText().equals(name))
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
        SelectContentPage selectContentPage = clickAddItems().render();

        Content content = new Content();
        content.setName(fileName);
        content.setFolder(false);
        Set<Content> contents = new HashSet<Content>();
        contents.add(content);

        Set<Site> sites = new HashSet<Site>();
        Site site = new Site();
        site.setName(getSiteShortName(siteName));
        site.setContents(contents);
        sites.add(site);

        CompanyHome companyHome = new CompanyHome();
        companyHome.setSites(sites);
        selectContentPage.addItems(companyHome);

        selectContentPage.selectOKButton().render();
    }

    /**
     * Helper to consistently get the Site Short Name.
     *
     * @param siteName String Name of the test for uniquely identifying / mapping test data with the test
     *
     * @return String site short name
     */
    public static String getSiteShortName(String siteName)
    {
        String siteShortName = "";
        String[] unAllowedCharacters = {"_", "!"};

        for(String removeChar:unAllowedCharacters)
        {
            siteShortName = siteName.replace(removeChar, "");
        }

        return siteShortName.toLowerCase();
    }
}
