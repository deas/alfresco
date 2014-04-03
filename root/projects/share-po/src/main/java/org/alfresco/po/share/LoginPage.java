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
package org.alfresco.po.share;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Login Page object that holds all information and methods that can be found on
 * the login page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class LoginPage extends SharePage
{
    private static final By PASSWORD_INPUT = By.cssSelector("input[id$='password']");
    private static final By USERNAME_INPUT = By.cssSelector("input[id$='username']");

    /**
     * Constructor.
     */
    public LoginPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoginPage render(RenderTime timer)
    {
        basicRender(timer);
        while (true)
        {
            timer.start();
            try
            {
                if (drone.find(By.cssSelector("form[id$='form']")).isDisplayed())
                {
                    if (drone.find(USERNAME_INPUT).isDisplayed() && drone.find(PASSWORD_INPUT).isDisplayed())
                    {
                        break;
                    }
                }
            }
            catch (NoSuchElementException nse)
            {
            }
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoginPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoginPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if login html element div is displayed.
     * 
     * @return true if panel is displayed
     */
    public boolean exists()
    {
        return panelExists("div.login-logo");
    }

    /**
     * Logs user into the site by first finding the login panel,populating the
     * fields and submitting the form.
     * 
     * @param username String username value
     * @param password String password value
     */
    public void loginAs(final String username, final String password)
    {
        if (username == null || password == null)
        {
            throw new IllegalArgumentException("Input param can not be null");
        }
        WebElement usernameInput = drone.findAndWait(USERNAME_INPUT);
        usernameInput.click();
        usernameInput.clear();
        usernameInput.sendKeys(username);
        /*
         * Do not move below line as this line acts as a buffer.
         * The sendKeys would concatanate the username and password
         * as there are too many request sent to the browser.
         * Instead of usign Thread.sleep we are find the submit button
         * and the password input field which has given a buffer that
         * allows to input username and password into it rescpective fields
         * correctly.
         */

        boolean isCloud = alfrescoVersion.isCloud();
        String selector = isDojoSupport() || isCloud ? "button[id$='button']" : "input#btn-login";
        WebElement button = drone.findAndWait(By.cssSelector(selector));
        WebElement passwordInput = drone.findAndWait(PASSWORD_INPUT);
        passwordInput.click();
        passwordInput.clear();
        passwordInput.sendKeys(password);

        String usernameEntered = usernameInput.getAttribute("value");
        if (!username.equalsIgnoreCase(usernameEntered))
        {
            throw new PageOperationException(String.format("The username %s did not match input %s", username, usernameEntered));
        }

        button.submit();
    }

    /**
     * Verify if error message is displayed.
     * 
     * @return true if div.bd is displayed
     */
    public boolean hasErrorMessage()
    {
        try
        {
            if (alfrescoVersion.isCloud() || isDojoSupport())
            {
                return drone.find(By.cssSelector("div.error")).isDisplayed();
            }
            return drone.find(By.cssSelector("div.bd")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    public String getErrorMessage()
    {
        if (alfrescoVersion.isCloud() || isDojoSupport())
        {
            return drone.find(By.cssSelector("div.error")).getText();
        }
        return drone.find(By.cssSelector("div.bd")).getText();
    }

}
