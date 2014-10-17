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
package org.alfresco.share;

import org.alfresco.po.share.AboutPopUp;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ImgUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class LogoTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(LogoTest.class);

    private static final String LOGO_FOLDER = "logo";
    private static final String LOGO_FILE_LOGIN_PAGE = "logo-loginPage.png";
    private static final String LOGO_FILE_TOP = "logo-dashboardPageTop.png";
    private static final String LOGO_FILE_FOOTER = "logo-dashboardPageFooter.png";
    private static final String LOGO_FILE_ABOUT_FOOTER = "logo-aboutFooter.png";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = "CommonTests")
    public void AONE_14159() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String logoLocation = DATA_FOLDER + SLASH + LOGO_FOLDER + SLASH;

        // User
        String[] testUserInfo = new String[] { testUser };
        boolean isUserCreated = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        if (!isUserCreated)
        {
            ShareUser.logout(drone);
        }

        drone.maximize();
        drone.navigateTo(dronePropertiesMap.get(drone).getShareUrl());
        LoginPage loginPage = drone.getCurrentPage().render();

        String actualLogoLoginUrl = loginPage.getLogoUrl();
        String expectedLogoLoginUrl = new File(logoLocation + LOGO_FILE_LOGIN_PAGE).toURI().toURL().toString();
        assertTrue(0.05 > ImgUtil.getPercentDiff(actualLogoLoginUrl, expectedLogoLoginUrl), "Login Page Logo don't equals more than 5% with standard.");

        DashBoardPage dashBoardPage = ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        String actualLogoTopDashBoardUrl = dashBoardPage.getTopLogoUrl();
        String expectedLogoTopDashBoardUrl = new File(logoLocation + LOGO_FILE_TOP).toURI().toURL().toString();
        assertTrue(0.05 > ImgUtil.getPercentDiff(actualLogoTopDashBoardUrl, expectedLogoTopDashBoardUrl), "DashBoard Top Logo don't equals more than 5% with standard.");

        String actualLogoFooterUrl = dashBoardPage.getFooterLogoUrl();
        String expectedFooterUrl = new File(logoLocation + LOGO_FILE_FOOTER).toURI().toURL().toString();
        assertTrue(0.05 > ImgUtil.getPercentDiff(actualLogoFooterUrl, expectedFooterUrl), "DashBoard Footer Logo don't equals more than 5% with standard.");

        AboutPopUp aboutPopUp = dashBoardPage.openAboutPopUp();
        String actualLogoAboutUrl = aboutPopUp.getLogoUrl();
        String expectedLogoAboutUrl = new File(logoLocation + LOGO_FILE_ABOUT_FOOTER).toURI().toURL().toString();
        assertTrue(0.05 > ImgUtil.getPercentDiff(actualLogoAboutUrl, expectedLogoAboutUrl), "About Logo don't equals more than 5% with standard.");
    }

}
