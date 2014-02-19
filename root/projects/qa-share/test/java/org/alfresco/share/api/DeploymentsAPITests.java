/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
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

package org.alfresco.share.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.workflow.api.model.Deployment;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.DeploymentAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class DeploymentsAPITests extends DeploymentAPI
{
    private static Log logger = LogFactory.getLog(DeploymentsAPITests.class);
    private Deployment deployment;
    private String testUser;
    private String testUserInvalid;

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName() + System.currentTimeMillis();
        testUser = getUserNamePremiumDomain(testName);
        testUserInvalid = "invalid" + testUser;

        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUser);        

        ListResponse<Deployment> deployments = getDeployments(ADMIN_USERNAME, DOMAIN, null);
        if (deployments.getList().size() > 0)
        {
            deployment = deployments.getList().get(0);
            logger.info("Deployments found: " + deployments.getList().size());
            logger.info("Workflow found: " + deployment.getName());
        }
        else
        {
            logger.info("No Deployments found: " + deployments.getList().size());
        }
    }

    @Test(groups = "Enterprise42")
    public void ALF_2320() throws Exception
    {
        ListResponse<Deployment> deployments = getDeployments(ADMIN_USERNAME, DOMAIN, null);
        assertNotNull(deployments);
        assertTrue(deployments.getList().size() > 0);

        // Check the deployments / workflow types available on the Share UI
        ShareUser.login(drone, testUser);
        StartWorkFlowPage detailsPage = ShareUserWorkFlow.selectStartWorkFlowFromMyTasksPage(drone);
        assertTrue(deployments.getList().size() >= detailsPage.getWorkflowTypes().size());

    }

    @Test(groups = "Enterprise42")
    public void ALF_2321() throws Exception
    {
        try
        {
            getDeployments(testUserInvalid, DOMAIN, null);

            Assert.fail("ALF_1105: Auth failure. Invalid user - " + testUserInvalid);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
    }

    @Test(groups = "Enterprise42")
    public void ALF_2322() throws Exception
    {
        Deployment response = getDeploymentById(ADMIN_USERNAME, DOMAIN, deployment.getId());
        assertNotNull(response);
        assertTrue(response.getId().equals(deployment.getId()), "The id should be - " + deployment.getId());
    }

    @Test(groups = "Enterprise42")
    public void ALF_2323() throws Exception
    {
        try
        {
            Deployment dp = getDeploymentById(testUser, DOMAIN + 1, deployment.getId());

            Assert.fail("ALF_1105: Error in tenant - " + DOMAIN + 1 + "For deployment: " + dp.getName());
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
    }

}
