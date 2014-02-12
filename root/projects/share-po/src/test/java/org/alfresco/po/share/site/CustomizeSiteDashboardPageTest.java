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
package org.alfresco.po.share.site;

import static org.testng.Assert.assertNotNull;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Customize Site Dashboard Page Test.
 * 
 * @author Shan Nagarajan
 * @since  1.6.1
 */
@Listeners(FailedTestListener.class)
@Test(groups="Enterprise4.1")
public class CustomizeSiteDashboardPageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomiseSiteDashboardPage customizeSiteDashboardPage;
    SiteDashboardPage siteDashboardPage;

    @BeforeClass
    public void loadFile() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "CustomizeSiteDashboardPage" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }
    @AfterClass(alwaysRun=true)
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }
    
    @Test
    public void selectCustomizeDashboard() throws Exception
    {
        customizeSiteDashboardPage = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        assertNotNull(customizeSiteDashboardPage);
    }
    
    @Test(dependsOnMethods="selectCustomizeDashboard")
    public void selectChangeLayout() throws Exception
    {
        customizeSiteDashboardPage = customizeSiteDashboardPage.selectChangeLayou().render();
        assertNotNull(customizeSiteDashboardPage);
    }

    @Test(dependsOnMethods="selectChangeLayout")
    public void selectDashboard() throws Exception
    {
        siteDashboardPage = customizeSiteDashboardPage.selectDashboard(SiteLayout.THREE_COLUMN_WIDE_CENTRE).render();
        assertNotNull(siteDashboardPage);
    }
    
}
