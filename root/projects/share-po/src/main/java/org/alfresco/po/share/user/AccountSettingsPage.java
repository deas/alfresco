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
package org.alfresco.po.share.user;

import java.util.concurrent.TimeUnit;

import org.alfresco.po.share.InviteToAlfrescoPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * My profile page object, holds all element of the html page relating to
 * share's my profile page.
 */
public class AccountSettingsPage extends SharePage
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final By MANAGEUSERS_LINK = By.cssSelector("a[href='manage-users']");
    private static final By SUB_TITLE = By.cssSelector("div[class$='first cloud-manage-users-header-title']>h1");
    private static final By INVITE_BUTTON = By.cssSelector("button[id$='cloud-console_x0023_default-newUser-button']");

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public AccountSettingsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AccountSettingsPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AccountSettingsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AccountSettingsPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Clicks on Manage Users link.
     *
     * @return {@link MyTasksPage}
     */
    public AccountSettingsPage selectManageUsers()
    {
        try
        {
            logger.info("Select Manage Users");
            drone.findAndWait(MANAGEUSERS_LINK).click();
            drone.waitForElement(SUB_TITLE, TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
            return new AccountSettingsPage(drone).render();
        }
        catch (NoSuchElementException te)
        {
            throw new PageException("Not able to find the Manage Users Page.");
        }
       
    }

    /**
     * Clicks on Invite People button to invoke Invite To Alfresco Page.
     * 
     * @return NewUserPage
     */
    public InviteToAlfrescoPage selectInvitePeople()
    {
        try
        {
            logger.info("Click Invite People button");
            WebElement newUserButton = drone.findAndWait(INVITE_BUTTON);
            newUserButton.click();
            return new InviteToAlfrescoPage(drone);
        }
        catch (NoSuchElementException te)
        {
            throw new PageException("Not able to find the Invite People Button.");
        }
    }

}
