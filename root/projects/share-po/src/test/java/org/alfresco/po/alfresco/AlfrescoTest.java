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

package org.alfresco.po.alfresco;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.util.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Created by ivan.kornilov on 23.04.2014.
 */
@Listeners(FailedTestListener.class)
public class AlfrescoTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(AbstractTest.class);

    @Test(groups = "Enterprise-only", timeOut = 400000)
    public void checkLogin() throws Exception
    {
        LoginAlfrescoPage loginPage = new LoginAlfrescoPage(drone);
        drone.navigateTo(loginPage.getAlfrescoURL(shareUrl));
        MyAlfrescoPage alfrescoPage = loginPage.login(username, password);
        alfrescoPage.render();
        assertTrue(alfrescoPage.userIsLoggedIn("admin"));

    }

    @Test(dependsOnMethods = "checkLogin", groups = "Enterprise-only", timeOut = 400000)
    public void checkConsole() throws InterruptedException
    {

        TenantAdminConsolePage adminConsolePage = new TenantAdminConsolePage(drone);
        drone.navigateTo(adminConsolePage.getTenantURL(shareUrl));
        adminConsolePage.createTenant("user123", "123");
        adminConsolePage.render();
        assertTrue(adminConsolePage.isOpened(), String.format("Page %s does not opened", adminConsolePage));

    }

}
