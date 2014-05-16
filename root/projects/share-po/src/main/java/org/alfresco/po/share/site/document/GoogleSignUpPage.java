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
package org.alfresco.po.share.site.document;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Set;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Create site page object, to do a sign up to google Account Google.
 * 
 * @author Subashni Prasanna
 * @since 1.5
 */
public class GoogleSignUpPage extends SharePage
{
    private static final By GOOGLE_USERNAME = By.cssSelector("input#Email");
    private static final By GOOGLE_PASSWORD = By.cssSelector("input#Passwd");
    private static final By SIGNUP_BUTTON = By.cssSelector("input#signIn");
    private static final String googleAccountTitle = "Google Accounts";

    private boolean isGoogleCreate;
    private String documentVersion;

    /**
     * Constructor and switch to the sign up window
     */
    protected GoogleSignUpPage(WebDrone drone, String documentVersion, Boolean isGoogleCreate)
    {
        super(drone);
        this.isGoogleCreate = isGoogleCreate;
        this.documentVersion = documentVersion;
    }

    /**
     * Public constructor and switch to the sign up window
     */
    public GoogleSignUpPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoogleSignUpPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Ensures that the 'checked out' message is visible.
     * 
     * @param timer Max time to wait
     * @return {@link GoogleSignUpPage}
     */
    @SuppressWarnings("unchecked")
    @Override
    public GoogleSignUpPage render(RenderTime timer)
    {
        while (true)
        {
            switchToGoogleSignIn();
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(200L);
                }
                catch (InterruptedException ite)
                {
                }
            }
            try
            {
                if (isSignupWindowDisplayed())
                {
                    // It's there and visible
                    break;
                }
            }
            catch (NoSuchElementException nse)
            {
                // Keep waiting for it
            }
            timer.end();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoogleSignUpPage render() throws PageRenderTimeException
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if googleSignup Dialog is displayed.
     * 
     * @return true if dialog is displayed.
     */
    public boolean isSignupWindowDisplayed()
    {
        try
        {
            return drone.find(GOOGLE_USERNAME).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Enter the Username , password and Click on Sign up button.
     * 
     * @return-EditInGoogleDocsPage
     */
    public EditInGoogleDocsPage signUp(String username, String password)
    {
        try
        {
            WebElement usernameInput = drone.findAndWait(GOOGLE_USERNAME);
            usernameInput.clear();
            usernameInput.sendKeys(username);

            WebElement passwordInput = drone.findAndWait(GOOGLE_PASSWORD);
            passwordInput.clear();
            passwordInput.sendKeys(password);

            WebElement submitButton = drone.find(SIGNUP_BUTTON);
            submitButton.click();
            switchToShare();
        }
        catch (TimeoutException te)
        {
            throw new TimeoutException("Google Sign up page timeout", te);
        }
        String message = "Editing in Google Docs";
        if (isGoogleCreate)
        {
            message = "Creating Google Docs";
        }
        drone.waitUntilVisible(By.cssSelector("div.bd>span.message"), message, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd>span.message"), message, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        String errorMessage = "";
        try
        {
            errorMessage = drone.find(By.cssSelector("div.bd>span.message")).getText();
        }
        catch (NoSuchElementException e)
        {
            return new EditInGoogleDocsPage(drone, documentVersion, isGoogleCreate);
        }
        throw new PageException(errorMessage);
    }

    /**
     * Enter the Username , password and Click on Sign up button.
     */
    public void channelSignUp(String username, String password)
    {
        try
        {
            WebElement usernameInput = drone.findAndWait(GOOGLE_USERNAME);
            usernameInput.clear();
            usernameInput.sendKeys(username);

            WebElement passwordInput = drone.findAndWait(GOOGLE_PASSWORD);
            passwordInput.clear();
            passwordInput.sendKeys(password);

            WebElement submitButton = drone.find(SIGNUP_BUTTON);
            submitButton.click();
        }
        catch (TimeoutException te)
        {
            throw new TimeoutException("Google Sign up page timeout", te);
        }
    }

    /**
     * Switch to google Doc Signup Window based on title.
     */
    private void switchToGoogleSignIn()
    {
        Set<String> windowHandles = drone.getWindowHandles();
        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            if (drone.getTitle().endsWith(googleAccountTitle))
            {
                break;
            }
        }
    }

    /**
     * Switch to Share Window.
     */
    private void switchToShare()
    {
        Set<String> windowHandles = drone.getWindowHandles();
        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            if (drone.getTitle() != null)
            {
                if ((drone.getTitle().contains("Document Details")))
                {
                    break;
                }
                else if ((drone.getTitle().contains("Document Library")))
                {
                    break;
                }
            }
        }
    }
}
