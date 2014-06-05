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

package org.alfresco.share.util;

import org.alfresco.po.alfresco.LoginAlfrescoPage;
import org.alfresco.po.alfresco.MyAlfrescoPage;
import org.alfresco.po.alfresco.TenantAdminConsolePage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by ivan.kornilov on 23.04.2014.
 */

public class AlfrescoUtil
{

    private static final Log logger = LogFactory.getLog(ApplicationPageUtil.class);
    private static long maxWaitTime = 30000;

    /**
     * This method provides create tenant with random user and password.
     *
     * @param drone
     * @param tenantName
     * @param tenantPassword
     * @return String[] { tenantName, tenantPassword }
     */

    public static String[] createTenant(WebDrone drone, String tenantName, String tenantPassword)
    {
        if (tenantName == null || tenantName.isEmpty())
        {
            tenantName = "user" + String.valueOf(RandomUtil.getInt(1000));
        }

        if (tenantPassword == null || tenantPassword.isEmpty())
        {
            tenantPassword = String.valueOf(RandomUtil.getInt(100));
        }

        TenantAdminConsolePage adminConsolePage = new TenantAdminConsolePage(drone);
        adminConsolePage.createTenant(tenantName, tenantPassword);

        return new String[] { tenantName, tenantPassword };
    }

    /**
     * This method provides login to Tenant Admin Console.
     *
     * @param drone
     * @param shareUrl
     * @param username
     * @param password
     * @return TenantAdminConsolePage
     */

    public static TenantAdminConsolePage tenantAdminLogin(WebDrone drone, String shareUrl, String username, String password)
    {
        String tenantURL = PageUtils.getProtocol(shareUrl) + PageUtils.getAddress(shareUrl) + "/alfresco/faces/jsp/admin/tenantadmin-console.jsp";

        try
        {
            //Navigate to Alfresco;
            LoginAlfrescoPage loginPage = new LoginAlfrescoPage(drone);
            drone.navigateTo(PageUtils.getProtocol(shareUrl) + PageUtils.getAddress(shareUrl) + "/alfresco/faces/jsp/login.jsp");
            loginPage.render();

            //Login to Alfresco;
            MyAlfrescoPage myalfrescoPage = loginPage.login(username, password);
            myalfrescoPage.render();

            //Navigate to Tenant Admin Console;
            drone.navigateTo(tenantURL);
            return new TenantAdminConsolePage(drone);
        }

        catch (UnsupportedOperationException uso)
        {
            throw new UnsupportedOperationException("Can not navigate to Tenant Admin Console Page");
        }

    }

}
