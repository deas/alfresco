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
package org.alfresco.po.share.task;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * This class represents the Select Assignee page. Its purpose is to search
 * users and assign task to them.
 * 
 * @author Abhijeet Bharade
 * @since v1.6.2
 */
public class SelectAssigneePage extends SharePage 
{
    private final String SELECT_ASSIGNEE = "div[id$='_default-peopleFinder-body']";
    private final String PEOPLE_FINDER_INPUT = "div[id$='_default-peopleFinder-body'] input";
    private final String SEARCH_BUTTON = "div[id$='_default-peopleFinder-body'] button";
    private static Log logger = LogFactory.getLog(SelectAssigneePage.class);
    
    /**
     * @param drone
     */
    public SelectAssigneePage(WebDrone drone) 
    {
        super(drone);
    }
    
    /**
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.HtmlPage#render(org.alfresco.po.share.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public SelectAssigneePage render(RenderTime timer) throws PageException 
    {
        try
        {
            while (true)
            {
                try
                {
                    timer.start();
                    WebElement selectDiv = drone.find(By.cssSelector(SELECT_ASSIGNEE));
                    if (selectDiv.isDisplayed())
                    {
                        drone.find(By.cssSelector(SEARCH_BUTTON));
                        break;
                    }
                }
                catch (NoSuchElementException e)
                {
                    logger.error("Not able to find Search Table.");
                }
                catch (StaleElementReferenceException stale) {}
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectAssigneePage render() throws PageException
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SelectAssigneePage render(final long time) throws PageException
    {
        return render(new RenderTime(time));
    }
    
    /**
     * Searches for user with the string passed.
     * 
     * @param searchString
     * @return searchUserList - a list of AssigneeResultsRow.
     */
    public List<AssigneeResultsRow> searchPeople(String searchString)
    {
        if (StringUtils.isEmpty(searchString)) { throw new UnsupportedOperationException("Enter atleast 1 character(s)"); }
        WebElement selectAssigneeDiv = drone.find(By.cssSelector(SELECT_ASSIGNEE));
        // Enter search String and click search
        WebElement searchInput = selectAssigneeDiv.findElement(By.cssSelector(PEOPLE_FINDER_INPUT));
        searchInput.sendKeys(searchString);
        selectAssigneeDiv.findElement(By.cssSelector(SEARCH_BUTTON)).click();
        return createListOfSearchResults();
    }

    /**
     * Searches for user with the string passed.
     * 
     * @param searchString
     * @return searchUserList - a list of AssigneeResultsRow.
     */
    public EditTaskPage closePage()
    {
        drone.find(By.cssSelector("div[id$='default-reassignPanel'] a.container-close")).click();
        return new EditTaskPage(drone).render();
    }

    /**
     * Return a populated list of AssigneeResultsRow with the search result on
     * select assignee page.
     * 
     * @return list of {@link AssigneeResultsRow}.
     * @see org.alfresco.webdrone.share.hybridworkflow.AssigneeResultsRow
     */
    private List<AssigneeResultsRow> createListOfSearchResults()
    {
        List<AssigneeResultsRow> userList = new ArrayList<AssigneeResultsRow>();

        try
        {
            // retrieve the search results
            List<WebElement> userRows = drone.findAndWaitForElements(By.cssSelector(SELECT_ASSIGNEE + " tbody[class$='data'] tr"));
            for (WebElement userRow : userRows)
            {
                AssigneeResultsRow user = new AssigneeResultsRow();
                
                user.setUsername(removeBrackets(userRow.findElement(By.cssSelector("h3>span")).getText()));
                user.setUserProfileLink(new ShareLink(userRow.findElement(By.tagName("a")), drone));
                user.setSelectButton(userRow.findElement(By.tagName("button")));
                
                userList.add(user);
            }
        }
        catch (TimeoutException e)
        {
        }
        return userList;
    }

    /**
     * @param text
     * @return a plaing string w/o brackets.
     */
    private String removeBrackets(String text)
    {
        text = text.replace("(", "");
        text = text.replace(")", "");
        return text;
    }

    /**
     * This method selects the user by clicking and Select button.
     * 
     * @param user
     *            - the AssigneeResultsRow that was returned during search.
     * @param loggedInUsername
     *            - the current logged in username.
     * @return
     */
    public HtmlPage selectUserAsAssignee(AssigneeResultsRow user, String loggedInUsername)
    {
        if(user == null || StringUtils.isEmpty(loggedInUsername)) 
        { 
            throw new UnsupportedOperationException("AssigneeResultsRow user should not be null. And logged in user should not be empty"); 
        }
        user.getSelectButton().click();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * This class represent a row of result that comes up in this page. This
     * object should be created from only within this class.
     * 
     * @author Abhijeet Bharade
     * 
     */
    public class AssigneeResultsRow
    {
        private String username;
        private ShareLink userProfileLink;
        private WebElement selectButton;

        /**
         * We want only {@link SelectAssigneePage} to create new instances.
         */
        AssigneeResultsRow(){}
    
        /**
         * @return the username
         */
        public String getUsername()
        {
            return username;
        }
        /**
         * @param username
         *            the username to set
         */
        public void setUsername(String username) 
        {
            this.username = username;
        }
        /**
         * @return the userProfileLink
         */
        public ShareLink getUserProfileLink() 
        {
            return userProfileLink;
        }
        
        /**
         * @param userProfileLink
         *            the userProfileLink to set
         */
        public void setUserProfileLink(ShareLink userProfileLink) 
        {
            this.userProfileLink = userProfileLink;
        }
        /**
         * @return the selectButton
         */
        WebElement getSelectButton() 
        {
            return selectButton;
        }
        /**
         * @param selectButton
         *            the selectButton to set
         */
        void setSelectButton(WebElement selectButton) 
        {
            this.selectButton = selectButton;
        }
    }
}
