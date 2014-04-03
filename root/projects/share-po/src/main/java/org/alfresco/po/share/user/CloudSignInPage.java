package org.alfresco.po.share.user;

/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * User Cloud SignIn page object holds all elements of HTML page objects relating to Cloud Sync connect page.
 * 
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public class CloudSignInPage extends SharePage
{

    private static final By PASSWORD_INPUT = By.cssSelector("input[id$='password']");
    private static final By USERNAME_INPUT = By.cssSelector("input[id$='username']");
    private static final By CONNECT_BUTTON = By.cssSelector("button[id$='-authForm-button-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='authForm-button-cancel-button']");
    private static final By SIGN_IN_HEADER = By.cssSelector("div.hd");
    private static final By SIGN_UP_LINK = By.cssSelector("a.theme-color-1:first-of-type");
    private static final By FORGOT_PASSWORD_LINK = By.cssSelector("a[href$='forgot-password']");
    private final Log logger = LogFactory.getLog(CloudSignInPage.class);

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public CloudSignInPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudSignInPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudSignInPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudSignInPage render(final long time)
    {
        return render(new RenderTime(time));

    }

    /**
     * Logs user into the site by first finding the login panel,populating the
     * fields and submitting the form.
     * 
     * @param username String username value
     * @param password String password value
     * @return true if exists
     */
    public HtmlPage loginAs(final String username, final String password)
    {
        WebElement usernameInput = drone.findAndWait(USERNAME_INPUT);
        usernameInput.clear();
        usernameInput.sendKeys(username);

        WebElement passwordInput = drone.findAndWait(PASSWORD_INPUT);
        passwordInput.clear();
        passwordInput.sendKeys(password);

        WebElement button = drone.findAndWait(CONNECT_BUTTON);
        String id = button.getAttribute("id");
        button.submit();
        drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        drone.waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        try
        {
            if (isDisconnectButtonDisplayed())
            {
                return new CloudSyncPage(drone);
            }
            else
            {
                return new DestinationAndAssigneePage(drone);
            }
        }
        catch (PageRenderTimeException pte)
        {
            throw new PageException("Neither DestinationAndAssigneePage or CloudSyncPage is returned", pte);
        }
    }

    /**
     * Returns true if Disconnect Button is displayed
     * 
     * @return boolean
     */
    public boolean isDisconnectButtonDisplayed()
    {
        RenderTime time = new RenderTime(maxPageLoadingTime);

        time.start();

        while (true)
        {
            try
            {
                if (drone.find(By.cssSelector("button[id$='default-button-delete-button']")).isDisplayed())
                {
                    return true;
                }
            }
            catch (NoSuchElementException nse)
            {
            }
            catch (StaleElementReferenceException se)
            {

            }
            try
            {
                return !drone.find(By.cssSelector("div[id$='default-cloud-folder-title']")).isDisplayed();
            }
            catch (NoSuchElementException nse)
            {

            }
            catch (StaleElementReferenceException se)
            {
            }

            try
            {
                return !drone.find(By.cssSelector("div[id$='cloudDestination-cloud-folder-treeview']")).isDisplayed();
            }
            catch (NoSuchElementException nse)
            {
            }
            catch (StaleElementReferenceException se)
            {
            }
            time.end();
            continue;
        }

    }

    public void selectCancelButton()
    {
        drone.findAndWait(CANCEL_BUTTON).click();
    }

    /**
     * Performs the action of clicking the sign up link.
     */
    public void selectSignUpLink()
    {
        drone.findAndWait(SIGN_UP_LINK).click();
    }

    /**
     * Performs the action of clicking the Forgot password link.
     */
    // TODO Bug ALF-19865 raised.
    public void selectFogotPassordLink()
    {
        drone.findAndWait(FORGOT_PASSWORD_LINK).click();
    }

    /**
     * Method to get the Cloud SignIn page dialog header
     * 
     * @return
     */

    @Override
    public String getPageTitle()
    {
        try
        {
            return drone.findAndWait(SIGN_IN_HEADER).getText();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Error getting the title");
            }
        }
        return "";
    }

    /**
     * Verify if the Forgot Password Link is displayed
     * 
     * @return true if the link displayed else false.
     */
    public boolean isForgotPasswordLinkDisplayed()
    {
        try
        {
            return drone.find(FORGOT_PASSWORD_LINK).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Forgot password link is not visible", e);
            }
        }
        return false;
    }

    /**
     * Method to get Forgot password link URL
     * 
     * @return
     */
    public String getForgotPasswordURL()
    {
        try
        {
            return drone.findAndWait(FORGOT_PASSWORD_LINK).getAttribute("href");
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Forgot password link is not visible", e);
            }
        }
        return "";
    }
}