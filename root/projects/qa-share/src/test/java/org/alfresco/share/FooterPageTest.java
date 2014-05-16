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
package org.alfresco.share;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FootersPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * To test the footer page of Enterprise and Cloud.
 * @author nshah
 *
 */
@Listeners(FailedTestListener.class)
public class FooterPageTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SiteDashBoardTest.class);

    protected String testUser;

    
    /**
     * Class includes: Tests from TestLink in Area: Site DashBoard Tests
     * <ul>
     * <li>Perform an Activity on Site</li>
     * <li>Site DashBoard shows Activity Feed</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("[Suite ] : Start Tests in: " + testName);
    }

    @Test(groups = { "Footer" })
    public void Alf_10240() throws Exception
    {
        DashBoardPage dashBoard = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        
        String copyRightText = dashBoard.getCopyRightDetails();
        
        Assert.assertTrue(copyRightText.contains("2005"), "License beginig year is not correct");
        Assert.assertTrue(copyRightText.contains("2014"), "License ending year is not correct");
        
        String licenseDetail = dashBoard.getLicenseHolder();
        if(isAlfrescoVersionCloud(drone))
        {
            Assert.assertTrue(licenseDetail.contains("Alfresco"), "Please provide correct Licensed to");
        }
        else
        {
            Assert.assertTrue(licenseDetail.contains(licenseShare), "Please provide correct Licensed to");
        }
        
        FootersPage footer = dashBoard.getFooter().render();
        
        Assert.assertTrue(footer instanceof FootersPage);
        
        String version = footer.getAlfrescoVersion();
        
        Assert.assertTrue(version.contains(alfrescoVersion.getVersion().toString() + ".0"), "Product version is not correct.");
        
        if(isAlfrescoVersionCloud(drone))
        {
            Assert.assertTrue(version.contains("Alfresco Cloud"), "Product is not correct.");
           
        }
        else
        {
            Assert.assertTrue(version.contains("Alfresco Enterprise "), "Product is not correct.");
        }
       
           
        ShareUser.logout(drone);
    }
    
    
    @Test(groups = { "Footer" })
    public void Alf_10241() throws Exception
    {
        DashBoardPage dashBoard = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        
        String copyRightText = dashBoard.getCopyRightDetails();
         
        Assert.assertTrue(copyRightText.contains("2005"), "License beginig year is not correct");
        Assert.assertTrue(copyRightText.contains("2014"), "License ending year is not correct");
        Assert.assertTrue(copyRightText.contains("Alfresco Software, Inc."), "Licensed company is not correct please provide proper company name.");
        String licenseDetail = dashBoard.getLicenseHolder();
        if(isAlfrescoVersionCloud(drone))
        {
            Assert.assertTrue(licenseDetail.contains("Alfresco"), "Please provide correct Licensed to");
        }
        else
        {
            Assert.assertTrue(licenseDetail.contains(licenseShare), "Please provide correct Licensed to");
        }      
        ShareUser.logout(drone);
    }
}

