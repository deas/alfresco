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
package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.*;
import org.alfresco.po.share.enums.Dashlets;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Aliaksei Boole
 */
public class MyProfileDashletTest extends AbstractDashletTest
{
    private DashBoardPage dashBoardPage;
    private CustomiseUserDashboardPage customiseUserDashboardPage;
    private AlfrescoVersion version;
    private String userName;
    private MyProfileDashlet dashlet;

    @BeforeClass(groups = { "alfresco-one" })
    public void setup() throws Exception
    {
        userName = "UserMeeting" + System.currentTimeMillis();

        version = drone.getProperties().getVersion();
        if (version.isCloud())
        {
            ShareUtil.loginAs(drone, shareUrl, username, password).render();
        }
        else
        {
            createEnterpriseUser(userName);
            ShareUtil.loginAs(drone, shareUrl, userName, UNAME_PASSWORD).render();
        }
    }

    @Test
    public void instantiateMyProfileDashlet()
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customiseUserDashboardPage = dashBoard.getNav().selectCustomizeUserDashboard();
        customiseUserDashboardPage.render();

        dashBoard = customiseUserDashboardPage.addDashlet(Dashlets.MY_PROFILE, 1).render();

        dashlet = new MyProfileDashlet(drone).render();
    }

}
