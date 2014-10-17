/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.share.adminconsole.summary;

import org.alfresco.po.share.systemsummary.directorymanagement.DirectoryInfoRow;
import org.alfresco.po.share.systemsummary.directorymanagement.DirectoryManagementPage;
import org.alfresco.po.share.systemsummary.directorymanagement.StatusRow;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.RandomUtil;
import org.alfresco.share.util.SystemSummaryAdminUtil;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class SyncLdapTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SyncLdapTests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_144()
    {
        String authChainName = RandomUtil.getRandomString(5);
        try
        {
            DirectoryManagementPage directoryManagementPage = SystemSummaryAdminUtil.addOpenLdapAuthChain(drone, authChainName);
            directoryManagementPage.runSync();
            directoryManagementPage.waitUntilAlert();
            drone.refresh();
            assertEquals(directoryManagementPage.getSyncStatus(), "IN_PROGRESS", "Sync status wrong.");
            DirectoryInfoRow directoryInfoRow = directoryManagementPage.getDirectoryInfoRowBy(authChainName);
            List<StatusRow> statusRowList = directoryInfoRow.clickStatus();
            assertTrue(statusRowList.size() > 0, "Information about sync status don't displayed.");
            for (StatusRow statusRow : statusRowList)
            {
                assertTrue(statusRow.isAllInfoDisplayed(), "Some status row is bad.");
            }
            StatusRow firstStatusRow = statusRowList.get(0);
            assertEquals(firstStatusRow.getBeanNameInfo(), "1 Group Analysis", "Wrong first BeanName.");
            String syncTimeInfo = firstStatusRow.getSyncTimeInfo();
            assertTrue(syncTimeInfo.contains("Start Time:"), "Wrong timeInfo.");
            assertTrue(syncTimeInfo.contains("End Time:"), "Wrong timeInfo.");
            String statusCountInfo = firstStatusRow.getStatusCountInfo();
            assertTrue(statusCountInfo.contains("Successful:"), "Wrong statusCountInfo.");
            assertTrue(statusCountInfo.contains("Failed:"), "Wrong statusCountInfo.");
            String totalCountInfo = firstStatusRow.getTotalCount();
            assertTrue(totalCountInfo.contains("Percent Complete:"), "Wrong totalCountInfo.");
            assertTrue(totalCountInfo.contains("Total Results:"), "Wrong totalCountInfo.");

            //check that information changed dynamically
            List<String> startTotalCountInfoList = new ArrayList<String>(6);
            boolean isChanged = false;
            for (StatusRow statusRow : statusRowList)
            {
                startTotalCountInfoList.add(statusRow.getTotalCount());
            }
            outerLoop:
            for (int i = 0; i < 1000; i++)
            {
                for (int j = 0; j < statusRowList.size(); j++)
                {
                    try
                    {
                        if (!statusRowList.get(j).getTotalCount().equals(startTotalCountInfoList.get(j)))
                        {
                            isChanged = true;
                            break outerLoop;
                        }
                    }
                    catch (StaleElementReferenceException e)
                    {
                        //if UI changed reset all PO objects.
                        directoryManagementPage = drone.getCurrentPage().render();
                        directoryInfoRow = directoryManagementPage.getDirectoryInfoRowBy(authChainName);
                        statusRowList = directoryInfoRow.getStatusRows();
                        if (!statusRowList.get(j).getTotalCount().equals(startTotalCountInfoList.get(j)))
                        {
                            isChanged = true;
                            break outerLoop;
                        }
                    }
                }
            }
            assertTrue(isChanged, "Information changed dynamically.");
            boolean isSunc = false;
            for (int i = 0; i < 30; i++)
            {
                try
                {
                    directoryManagementPage.getSyncStatus();
                    drone.refresh();
                    directoryManagementPage.waitUntilAlert().render();
                }
                catch (TimeoutException e)
                {
                    logger.info("Status info don't found. It's normal.");
                    isSunc = true;
                    break;
                }
            }
            assertTrue(isSunc, "Auth Chain Synced.");

        }
        finally
        {
            SystemSummaryAdminUtil.deleteAuthChain(drone, authChainName);
        }
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_147()
    {
        String authChainName = RandomUtil.getRandomString(5);
        try
        {
            DirectoryManagementPage directoryManagementPage = SystemSummaryAdminUtil.addOpenLdapAuthChain(drone, authChainName);
            directoryManagementPage.runSync();
            directoryManagementPage.waitUntilAlert();
            drone.refresh();
            assertEquals(directoryManagementPage.getSyncStatus(), "IN_PROGRESS", "Sync status wrong.");
            directoryManagementPage = drone.getCurrentPage().render();
            directoryManagementPage.runSync();
            drone.refresh();
            assertEquals(directoryManagementPage.getSyncStatus(), "IN_PROGRESS", "Sync status wrong.");
        }
        finally
        {
            SystemSummaryAdminUtil.deleteAuthChain(drone, authChainName);
        }
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_148()
    {
        String authChainName = RandomUtil.getRandomString(5);
        try
        {
            DirectoryManagementPage directoryManagementPage = SystemSummaryAdminUtil.addOpenLdapAuthChain(drone, authChainName);
            directoryManagementPage.runSync();
            directoryManagementPage.waitUntilAlert();
            drone.refresh();
            assertEquals(directoryManagementPage.getSyncStatus(), "IN_PROGRESS", "Sync status wrong.");
            String result = directoryManagementPage.runTestSyncFor(authChainName);
            assertEquals(result, "Test Passed", "Another action don't work correctly.");
        }
        finally
        {
            SystemSummaryAdminUtil.deleteAuthChain(drone, authChainName);
        }
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_153()
    {
        String authChainName = RandomUtil.getRandomString(5);
        try
        {
            DirectoryManagementPage directoryManagementPage = SystemSummaryAdminUtil.addAdLdapAuthChain(drone, authChainName);
            directoryManagementPage.runSync();
            directoryManagementPage.waitUntilAlert();
            drone.refresh();
            assertEquals(directoryManagementPage.getSyncStatus(), "IN_PROGRESS", "Sync status wrong.");
            directoryManagementPage = drone.getCurrentPage().render();
            directoryManagementPage.runSync();
            drone.refresh();
            assertEquals(directoryManagementPage.getSyncStatus(), "IN_PROGRESS", "Sync status wrong.");
        }
        finally
        {
            SystemSummaryAdminUtil.deleteAuthChain(drone, authChainName);
        }
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_154()
    {
        String authChainName = RandomUtil.getRandomString(5);
        try
        {
            DirectoryManagementPage directoryManagementPage = SystemSummaryAdminUtil.addAdLdapAuthChain(drone, authChainName);
            directoryManagementPage.runSync();
            directoryManagementPage.waitUntilAlert();
            drone.refresh();
            assertEquals(directoryManagementPage.getSyncStatus(), "IN_PROGRESS", "Sync status wrong.");
            String result = directoryManagementPage.runTestSyncFor(authChainName);
            assertEquals(result, "Test Passed", "Another action don't work correctly.");
            assertEquals(directoryManagementPage.getSyncStatus(), "IN_PROGRESS", "Sync status wrong.");
        }
        finally
        {
            SystemSummaryAdminUtil.deleteAuthChain(drone, authChainName);
        }
    }

}
