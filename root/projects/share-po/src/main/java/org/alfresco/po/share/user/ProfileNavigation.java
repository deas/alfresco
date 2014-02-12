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
import org.openqa.selenium.By;

/**
 * Represent elements found on the HTML page relating to the profile navigation
 * bar
 * 
 * @author Abhijeet Bharade
 * @since 1.710
 */
public class ProfileNavigation
{
    private final By CLOUD_SYNC_LINK = By.cssSelector("div>a[href='user-cloud-auth']");
    private final By TRASHCAN_LINK = By.cssSelector("div>a[href='user-trashcan']");
    private static ProfileNavigation navigation; 
    
    private final WebDrone drone;
    private AlfrescoVersion alfrescoVersion;

    public static ProfileNavigation getInstance(WebDrone drone)
    {
        if(navigation == null)
        {
            navigation = new ProfileNavigation(drone);
        }
        return navigation;
    }
    
    /**
     * Constructor
     * 
     * @param drone WebDriver browser client
     */
    private ProfileNavigation(WebDrone drone)
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
        if (alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("Cloud sync functionality available only for Enterprise.");
        }
        drone.find(CLOUD_SYNC_LINK).click();
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
    
    public void setAlfrescoVersion(AlfrescoVersion alfrescoVersion) {
        this.alfrescoVersion = alfrescoVersion;
    }
}