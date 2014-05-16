package org.alfresco.share.unit;

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

import org.alfresco.share.util.api.DPAPIUtils;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for dp api Utils
 * 
 * @author Meenal Bhave
 */
@Listeners(FailedTestListener.class)
public class DPAPIUtilsTest extends DPAPIUtils
{
    private static Log logger = LogFactory.getLog(DPAPIUtilsTest.class);
    protected String testUser;
    
    public DPAPIUtilsTest() throws Exception
    {
        super();
    }

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "Sanity", "MyAlfresco" })
    public void unitTestCreateDPEntv_2() throws Exception
    {
        String envName = (System.currentTimeMillis() + "dp").substring(6, 14);
        String productType = "myalfresco";
        String buildNo = "4.3-341";
        String configType = "stable";
        String configVersion = "rel32rc12";
        Boolean premiumMode = false;
        String baseAMIID = "ami-57c8fb3e";
        Boolean layer7Enabled = false;

        HttpResponse response;

        response = DPAPIUtils.createDPEnv(envName, productType, buildNo, configType, configVersion, premiumMode, baseAMIID, layer7Enabled);
        checkResult(response, 200);

        response = DPAPIUtils.createDPEnv(envName, productType, buildNo, configType, configVersion, premiumMode, baseAMIID, layer7Enabled);
        checkResult(response, 500);
    }

    @Test(groups = { "Sanity", "MyAlfresco" })
    public void unitTestDeleteDPEntv_3() throws Exception
    {
        String envName = "newnamepr";

        HttpResponse response;

        response = DPAPIUtils.deleteDPEnv(envName);
        checkResult(response, 200);
    }

    @Test(groups = { "Sanity", "MyAlfresco" })
    public void unitTestGetDPStatus_4() throws Exception
    {
        String envName = "autocloudh";

        String status = DPAPIUtils.getDPStatus(envName);

        logger.info("DP Status: " + status);
    }

    @Test(groups = { "Sanity", "MyAlfresco" })
    public void unitTestIsDPRunning_5() throws Exception
    {
        String envName = "autocloudh";

        Boolean status = DPAPIUtils.isDPRunning(envName);
        logger.info("Is DP Running: " + status.toString());
    }

    @Test(groups = { "Sanity", "MyAlfresco" })
    public void unitTestDPLifeCycle_6() throws Exception
    {
        HttpResponse response;

        response = DPAPIUtils.createDPEnv();
        checkResult(response, 200);

        if (DPAPIUtils.waitFoDP(envName))
        {

            logger.info("Starting tests:");

            Assert.assertTrue(DPAPIUtils.isDPRunning(envName));

            logger.info("Tests complete:");

            DPAPIUtils.deleteDPEnv(envName);

            Assert.assertFalse(DPAPIUtils.isDPRunning(envName));
        }
        else
        {
            Assert.fail("Error creating / destroying dp environment within time");
        }

    }
}
