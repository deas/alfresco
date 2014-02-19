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
package org.alfresco.share.dashlet;

import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.annotations.DataGroup;
import org.alfresco.webdrone.annotations.DataSetup;

/**
 * Test Cases to test {@link DataSetup} Annotation.
 *
 * @author Shan Nagarajan
 */
//TODO: Shan: Is this being used anywhere? If not, remove the class.
public class DataSetupCreateSite extends AbstractTests
{
    //TODO: Shan: Create user step missing.
    @DataSetup(testLinkId="7950", groups={DataGroup.DASHLETS})
    public void dataPrep_Dashlets_7950(WebDrone drone)
    {
        try
        {
            String testName = getTestName();
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] {testUser};

            String siteName1 = getSiteName(testName)+ "_1";
            String siteName2 = getSiteName(testName) + "_2";
            
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
            ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
/*    //@DataSetup(testLinkId="9999991")
    public void createSite2(WebDrone webDrone)
    {

        try
        {
            ShareUser.login(webDrone, username, password);
            ShareUser.createSite(webDrone, "Test Data Setup Create Site2 " + System.currentTimeMillis(), "Public");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }*/

}
