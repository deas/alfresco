/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.user;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * Represent elements found on the HTML page relating to the profile navigation
 * bar
 * 
 * @author Abhijeet Bharade
 * @since 1.7.1
 */
public class ProfileNavigation
{
    private static final By CLOUD_SYNC_LINK = By.cssSelector("div>a[href='user-cloud-auth']");
    private static final By TRASHCAN_LINK = By.cssSelector("div>a[href='user-trashcan']");
    private static final By LANGUAGE_LINK = By.cssSelector("div>a[href='change-locale']");
    private final Log logger = LogFactory.getLog(ProfileNavigation.class);
    
    private final WebDrone drone;
    
    /**
     * Constructor
     * 
     * @param drone WebDriver browser client
     */
    public ProfileNavigation(WebDrone drone)
    {
        this.drone = drone;
    }

    /**
     * Does the action of clicking on Cloud Sync link on
     * 
     * @return {@link CloudSyncPage}
     */
    public CloudSyncPage selectCloudSyncPage()
    {
    	AlfrescoVersion version = drone.getProperties().getVersion();
        if (version.isCloud())
        {
            throw new UnsupportedOperationException("Cloud sync functionality available only for Enterprise.");
        }
        try 
        {
            drone.findAndWait(CLOUD_SYNC_LINK).click();
        }
        catch (TimeoutException exception) 
        {
            String message = "Not able to find the Cloud Sync Link";
            logger.error(message + exception.getMessage());
            throw new PageException(message, exception);
        }
        return new CloudSyncPage(drone);
    }
    
    /**
     * Click on the trashcan link
     * 
     * @return - {@link TrashCanPage}
     * @author sprasanna 
     */

    public TrashCanPage selectTrashCan()
    {
        drone.find(TRASHCAN_LINK).click();
        return new TrashCanPage(drone);
    }

    /**
     * Method to select Language link
     * @return
     */
    public LanguageSettingsPage selectLanguage()
    {
    	AlfrescoVersion version = drone.getProperties().getVersion();
        if(!version.isCloud())
        {
            throw new UnsupportedOperationException("Language Settings are not available for Environment: " + version.toString());
        }
        try
        {
            drone.find(LANGUAGE_LINK).click();
            return new LanguageSettingsPage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Language link", nse);
        }
    }
}