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
package org.alfresco.po.share.site.document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Sync Info Details Page
 * 
 * @author Chiran
 * @since 1.7.1
 */
public class SyncInfoPage extends SharePage
{

    private static Log logger = LogFactory.getLog(FileDirectoryInfo.class);
    private static final By CLOSE_BUTTON = By.cssSelector(".info-balloon .closeButton");
    private static final By IS_CLOUD_SYNC_STATUS = By.cssSelector("div.cloud-sync-status-heading+p");
    private static final By SYNC_LOCATION_PRESENT = By.cssSelector("p.location");
    private static final By REQ_SYNC_BUTTON = By.cssSelector("div.cloud-sync-status-buttons>span>span>button");
    private static final By REQ_UNSYNC_BUTTON = By.cssSelector("div.cloud-sync-status-buttons>span>span>button");
    private static final By SYNC_LOCATION = By.cssSelector(".cloud-sync-details-info>p>span[class^='folder-link']>a");
    private static final By SYNC_DOCUMENT_NAME = By.cssSelector(".view-in-cloud");
    private static final By SYNC_PERIOD = By.cssSelector(".cloud-sync-details-info>p>span[title]");
    private static final String DATE_FORMAT = "EEE dd MMM yyyy HH:mm:ss";
    public static final By PROMPT_BUTTONS = By.cssSelector("div[id$='prompt']>div.ft>span>span>span.first-child");
    public static final By REMOVE_CHECKBOX = By.cssSelector("input[id$='requestDeleteRemote']");
    public static final By STATUS_HEADING = By.cssSelector(".cloud-sync-status-heading");

    public enum ButtonType
    {
        CANCEL,
        REMOVE;
    }
    public SyncInfoPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    public SyncInfoPage render(RenderTime timer)
    {
        renderLoop: while(true)
        {
            timer.start();
            try
            {
                List<WebElement> statusElements = drone.findAll(STATUS_HEADING);
                for (WebElement status: statusElements)
                {
                    if(status.isDisplayed())
                    {
                        break renderLoop;
                    }
                }
            }
            catch (NoSuchElementException nse){ }
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public SyncInfoPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    public SyncInfoPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
	
    /**
     * Sync pop up close button is present or not.
     * @return
     */
    public boolean isCloseButtonPresent()
    {
        try
        {
            return drone.findAndWait(CLOSE_BUTTON).isDisplayed();
        }
        catch (TimeoutException nse)
        {
            return false;
        }
    }
    
    /**
     * Status details of synced artifact is present or not.
     * @return
     */
    public boolean isSyncStatusPresent()
    {
        try
        {
            return drone.find(IS_CLOUD_SYNC_STATUS).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }
    
    /**
     * Location of synced artifact is present or not.
     * @return
     */
    public boolean isSyncLocationPresent()
    {
        try
        {
            return drone.find(SYNC_LOCATION_PRESENT).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }
    
    /**
     * Request sync button is present or not.
     * @return
     */
    public boolean isRequestSyncButtonPresent()
    {
        boolean isRequestSyncButtonPresent= false;
        try
        {
            List<WebElement> buttons =  drone.findAndWaitForElements(REQ_SYNC_BUTTON);
            for (WebElement button : buttons)
            {
                if("Request Sync".equals(button.getText()))
                {
                    isRequestSyncButtonPresent=true;
                    break;
                }
            }       
            return isRequestSyncButtonPresent;
        }
        catch (TimeoutException nse)
        {
            logger.error("Time out finding request sync button!!");
        }        
        throw new PageException();
    }
    
    /**
     * Unsync button present or not.
     * @return
     */
    public boolean isUnsyncButtonPresent()
    {
        boolean isUnsyncButtonPresent= false;
        try
        {
            List<WebElement> buttons =  drone.findAndWaitForElements(REQ_UNSYNC_BUTTON);
            for (WebElement button : buttons)
            {
                if("Unsync".equals(button.getText()))
                {
                    isUnsyncButtonPresent = true;
                    break;
                }
            }     
            return isUnsyncButtonPresent;
        }
        catch (TimeoutException nse)
        {
            logger.error("Time out finding unsync button!!");
        }
        throw new PageException();
    }
    
    /**
     * Click on sync pop up button to close pop up.
     * @return
     */
    public void clickOnCloseButton()
    {
        try
        {
            drone.findAndWait(CLOSE_BUTTON).click();            
        }
        catch(TimeoutException e) 
        {
            logger.error("Unable to find Close button on Sync Info page" + e.getMessage());
            throw new PageException("Not able to click on Sync Info close button.");
        }
    }
    
    /**
     * Get Sync status of the artifact.
     * @return
     */
    public String getCloudSyncStatus()
    {
        try
        {
            return drone.findAndWait(IS_CLOUD_SYNC_STATUS).getText();
        }
        catch(TimeoutException e) 
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
        }
        
        throw new PageException("Not able to find Sync Info status.");
    }
    
    /**
     * Get location of synced document.(i.e. premiumnet>sitename>Documents)
     * @return
     */
    public String getCloudSyncLocation()
    {
        StringBuilder location = new StringBuilder("");
        try
        {
            List<WebElement> elements = getDrone().findAndWaitForElements(SYNC_LOCATION);
            int i = elements.size();
            for (WebElement webElement : elements)
            {                
                location.append(webElement.getText());
                while(i > 1)
                {
                    location.append(">");
                    i--;
                    break;
                }
            }
            return location.toString();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
        }

        throw new PageException("Not able to find Sync Info Location.");
    }
    
    /**
     * Get name of the synced document.
     * @return
     */
    public String getCloudSyncDocumentName()
    {
        try
        {
            return drone.findAndWait(SYNC_DOCUMENT_NAME).getText();
        }
        catch(TimeoutException e) 
        {
            logger.error("Exceeded the time to find css." + e.getMessage());
        }
        
        throw new PageException("Not able to find Sync Info Location.");
    }
    
    /**
     * Get Date of sync happened.
     * @return
     */
    public Date getSyncPeriodDetails()
    {
        try
        {
            String syncPeriod =drone.findAndWait(SYNC_PERIOD).getAttribute("title");           
            return new SimpleDateFormat(DATE_FORMAT).parse(syncPeriod);            
        }catch(TimeoutException toe)
        {
           logger.error("Time out finding element for sync time period"); 
        }
        catch (ParseException e)
        {
            logger.error("Parse error no date exist"); 
        }
        throw new PageException();
    }
  
    
    /**
     * Unsync button present or not.
     * @param removeContentFromCloud
     * @see 
     * true: Remove content from cloud.
     * false: Remove sync with Cloud.
     * @return
     */
    public void selectUnsyncRemoveContentFromCloud(boolean removeContentFromCloud)
    {        
        try
        {
            List<WebElement> buttons =  drone.findAndWaitForElements(REQ_UNSYNC_BUTTON);
            for (WebElement button : buttons)
            {
                if("Unsync".equals(button.getText()))
                {
                    button.click();
                    break;
                }
            }     
            if(removeContentFromCloud)
                selectCheckBoxToRemoveContenFromCloud();
            clickButtonFromPopup(ButtonType.REMOVE);  
            return;
        }
        catch (TimeoutException nse)
        {
            logger.error("Time out finding unsync button!!");
        }
        throw new PageException();
    }
    
    /**
     * Click check box to remove content.
     */
    private void selectCheckBoxToRemoveContenFromCloud()
    {        
        try
        {
           drone.findAndWait(REMOVE_CHECKBOX).click();
           return ;
        }
        catch (TimeoutException nse)
        {
            logger.error("Time out finding check box button!!");
        }
        throw new PageException();
    }
    
    /**
     * Click Button to Remove or Cancel unsync operation.
     * @param buttonType
     */
    private void clickButtonFromPopup(ButtonType buttonType)
    {        
        try
        {
          List<WebElement> buttons = drone.findAndWaitForElements(PROMPT_BUTTONS);
          for (WebElement button : buttons)       
           {
              if("Remove sync".equals(button.getText()) && ButtonType.REMOVE.equals(buttonType))
              {
                  button.click();
                  break;
              }
              else if("Cancel".equals(button.getText()) && ButtonType.CANCEL.equals(buttonType))
              {
                  button.click();
                  break;
              }     
           }
           return ;
        }
        catch (TimeoutException nse)
        {
            logger.error("Time out finding buttons!!");
        }
        throw new PageException();
    }
    
    /**
     * @return
     */
    public boolean isUnSyncIconPresentInDetailsPage()
    {        
        return drone.isElementDisplayed(By.cssSelector(".document-unsync-link"));
       
    }
}