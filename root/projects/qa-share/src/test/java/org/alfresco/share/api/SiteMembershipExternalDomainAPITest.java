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
package org.alfresco.share.api;

import org.alfresco.po.alfresco.TenantAdminConsolePage;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.share.util.AlfrescoUtil;
import org.alfresco.share.util.RandomUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.SiteMembershipAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne" })
public class SiteMembershipExternalDomainAPITest extends SiteMembershipAPI
{
    private static Log logger = LogFactory.getLog(SiteMembershipExternalDomainAPITest.class);

    private static String adminDomain1;
    private static String adminDomain2;
    private String testUserDomain2;
    private static String siteName;
    private static String modSite;
    private static String privateSite;
    private static String publicSite;

    private static String domain1 = RandomUtil.getRandomString(4) + ".test";
    private static String domain2 = RandomUtil.getRandomString(4) + ".test";

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName() + System.currentTimeMillis();

        adminDomain1 = getUserNameForDomain("admin", domain1).replace("user", "");
        adminDomain2 = getUserNameForDomain("admin", domain2).replace("user", "");
        testUserDomain2 = getUserNameForDomain("test", domain2);

        siteName = getSiteName(testName) + System.currentTimeMillis();

        modSite = siteName + "mod";
        privateSite = siteName + "private";
        publicSite = siteName + "public";

        if (isAlfrescoVersionCloud(drone))
        {
            adminDomain1 = adminDomain1.replace("admin","adm");
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, adminDomain1);
            adminDomain2 = adminDomain2.replace("admin","adm");
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, adminDomain2);
        }
        else
        {
            TenantAdminConsolePage tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
            tenantConsolePage.render();
            AlfrescoUtil.createTenant(drone, domain1, DEFAULT_PASSWORD);
            AlfrescoUtil.createTenant(drone, domain2, DEFAULT_PASSWORD);
        }
        CreateUserAPI.CreateActivateUser(drone, adminDomain2, testUserDomain2);

        ShareUser.login(drone, adminDomain2, DEFAULT_PASSWORD);
        SiteUtil.createSite(drone, siteName, SITE_VISIBILITY_MODERATED);
        ShareUser.logout(drone);
        createSiteMembershipRequest(testUserDomain2, domain2, testUserDomain2, siteName, "");

        ShareUser.login(drone, adminDomain1, DEFAULT_PASSWORD);
        SiteUtil.createSite(drone, publicSite, SITE_VISIBILITY_PUBLIC);
        SiteUtil.createSite(drone, privateSite, SITE_VISIBILITY_PRIVATE);
        SiteUtil.createSite(drone, modSite, SITE_VISIBILITY_MODERATED);
    }

    @Test
    public void AONE_14325() throws Exception
    {
        try
        {
            createSiteMembershipRequest(adminDomain1, domain1, testUserDomain2, publicSite, "");
            fail("Create SMR for userId 'external user' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Create SMR for userId 'external user' should fail with 404");
        }
        try
        {
            createSiteMembershipRequest(adminDomain1, domain1, testUserDomain2, modSite, "");
            fail("Create SMR for userId 'external user' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Create SMR for userId 'external user' should fail with 404");
        }
        try
        {
            createSiteMembershipRequest(adminDomain1, domain1, testUserDomain2, privateSite, "");
            fail("Create SMR for userId 'external user' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Create SMR for userId 'external user' should fail with 404");
        }
        try
        {
            createSiteMembershipRequest(adminDomain1, domain1, RandomUtil.getRandomString(5), privateSite, "");
            fail("Create SMR for userId 'not matching the current user' should fail with 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Create SMR for userId 'not matching the current user' should fail with 404");
        }
    }

}
