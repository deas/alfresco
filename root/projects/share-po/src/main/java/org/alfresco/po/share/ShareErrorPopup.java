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
package org.alfresco.po.share;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Share Error popup page object, holds all the methods relevant to Share Error popup
 * 
 * @author Meenal Bhave
 * @since 1.7.0
 */
public class ShareErrorPopup extends SharePage
{      
    private static final String FAILURE_PROMPT = "div[id='prompt']";
    private static final String DEFAULT_BUTTON = "span.yui-button";
    private static final String ERROR_BODY = "div.bd";
    
    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public ShareErrorPopup(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ShareErrorPopup render(RenderTime timer)
    {
        while(true)
        {
            timer.start();
            synchronized (this)
            {
                try{ this.wait(100L); } catch (InterruptedException e) {}
            }
        	if(isShareErrorDisplayed())
        	{
        		break;
        	}
        	timer.end();
        }
        
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ShareErrorPopup render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ShareErrorPopup render(final long time)
    {
        return render(new RenderTime(time));
    }         
    
    /**
     * Helper method to handle the error popup displayed in Share
     * Clicks on the OK button to return to the original page
     * @return SharePage
     * @throws ShareException with the Error message
     */
    public HtmlPage handleErrorMessage() throws ShareException
    {
        if (isShareErrorDisplayed())
        {
            String errorMessage = getShareErrorMessage();
            clickOKOnError().render();            
            throw new ShareException(errorMessage);
        }

        return FactorySharePage.resolvePage(drone);
    }
    
    /**
     * Helper method to click on the OK button to return to the original page
     * 
     */
    private HtmlPage clickOKOnError()
    {
        WebElement popupMessage = getErrorPromptElement();
        popupMessage.findElement(By.cssSelector(DEFAULT_BUTTON)).click();
        return FactorySharePage.resolvePage(drone);          
    }
    
    /**
     * Helper method to get the error message in the Share Error Popup
     * @return String Share Error Message displayed in the popup
     */
    public String getShareErrorMessage()
    {
        try
        {
            WebElement errorMessage = getErrorPromptElement();
            return errorMessage.findElement(By.cssSelector(ERROR_BODY)).getText();
        }
        catch (NoSuchElementException nse)
        {
        }
        return null;
    }
    
    /**
     * Helper method to return true if Share Error popup is displayed 
     * @return boolean <tt>true</tt> is Share Error popup is displayed
     */
    public boolean isShareErrorDisplayed()
    {
        try
        {
            WebElement errorMessage = getErrorPromptElement();
            if (errorMessage != null && errorMessage.isDisplayed())
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }
    
    /**
     * Helper method to return WebElement for the failure prompt
     * @return WebElement
     */
    private WebElement getErrorPromptElement()
    {
        try
        {
            WebElement errorMessage = drone.find(By.cssSelector(FAILURE_PROMPT));

            return errorMessage;
        }
        catch (NoSuchElementException nse)
        {
            return null;
        }
    }
       
}
