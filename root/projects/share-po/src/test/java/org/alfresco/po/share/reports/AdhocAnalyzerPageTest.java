/*
sd * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.po.share.reports;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.AdhocAnalyzerDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.util.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * AdhocAnalyzerPageTest test class for adhoc analyzer page object
 * 
 * @author jcule
 */

@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class AdhocAnalyzerPageTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(AdhocAnalyzerPageTest.class);

    private static final String ADHOC_ANALYZE = "Adhoc Analyze";
    private static final String UNSAVED_REPORT = "Unsaved Report";
    private static final String PENTAHO_BUSINESS_ANALYST_USERNAME = "pentahoBusinessAnalyst";
    private static final String PENTAHO_BUSINESS_ANALYST_PASSWORD = "pentahoBusinessAnalyst";
    private static String reportName = null;

    private SharePage page = null;
    AdhocAnalyzerPage adhocAnalyzePage = null;
    CreateEditAdhocReportPage createEditAdhocReportPage = null;

    @BeforeClass(alwaysRun = true)
    public void loadFiles() throws Exception
    {
        page = loginAs(PENTAHO_BUSINESS_ANALYST_USERNAME, PENTAHO_BUSINESS_ANALYST_PASSWORD);
        //create some users, sites and content first??????
        adhocAnalyzePage = page.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), ADHOC_ANALYZE);

    }

    /**
     * Check if Adhoc Analyze page title is displayed correctly and test for Create Content, Users, Activities button
     */
    
    @Test
    public void testAnalyzeAndContentUsersActivitiesButton()
    {
        adhocAnalyzePage.clickOnAnalyzeButton();
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), ADHOC_ANALYZE);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);
    }

    /**
     * Test for Create, Save and Open saved report
     */
    @Test(dependsOnMethods = "testAnalyzeAndContentUsersActivitiesButton")
    public void testCreateSaveOpenEditReport()
    {
        // create new report
        createEditAdhocReportPage.doubleClickOnSiteNameField();
        createEditAdhocReportPage.doubleClickOnEventTypeField();
        createEditAdhocReportPage.doubleClickOnNumberOfEventsField();
        
        // click on Save button
        createEditAdhocReportPage.clickOnSaveReportButton();
        
        //check popup is displayed
        Assert.assertTrue(createEditAdhocReportPage.isSaveAnalysisDispalayed());

        // Enter report name
        reportName = "NewReport-" + System.currentTimeMillis();
        createEditAdhocReportPage.enterAnalisysName(reportName);

        // Click on Ok button to save report
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();

        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());
        
        // Click on open button to open saved report
        createEditAdhocReportPage = createEditAdhocReportPage.clickOnOpenReportButton();
        createEditAdhocReportPage.clickOnExistingReport(reportName);
        
        //edit existing report, save it and check that report is updated
        createEditAdhocReportPage.doubleClickOnDayField();
        createEditAdhocReportPage.clickOnSaveReportButton();

        String [] tableStatusBarElements = createEditAdhocReportPage.getTableStatusBar();
               
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isDayDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());
        
        
        Assert.assertEquals(tableStatusBarElements[0].trim(), "Rows:");
        Assert.assertEquals(tableStatusBarElements[3].trim(), "Cols:");
        Assert.assertTrue(Integer.parseInt(tableStatusBarElements[1].trim()) > 0);
        Assert.assertTrue(Integer.parseInt(tableStatusBarElements[4].trim()) > 0);
        
        // check that the name of the report is saved correctly
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), reportName);
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), ADHOC_ANALYZE);

        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        
    }
    
    /**
     * Verifies that existing adhoc report can be opened in Adhoc Analyzer dashlet 
     */
    @Test(dependsOnMethods = "testCreateSaveOpenEditReport")
    public void testOpenReportInDashlet()
    {
        DashBoardPage dashboardPage = (DashBoardPage)createEditAdhocReportPage.getNav().selectMyDashBoard().render();
        CustomiseUserDashboardPage customiseUserDashboardPage = dashboardPage.getNav().selectCustomizeUserDashboard().render();
        
        dashboardPage = customiseUserDashboardPage.addDashlet(Dashlets.ADHOC_ANALYZER, 2).render();
        AdhocAnalyzerDashlet adhocAnalyzerDashlet = dashboardPage.getDashlet("adhoc-analyzer").render();
        Assert.assertTrue(adhocAnalyzerDashlet.isTitleDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isOpenDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isDashletMessageDisplayed());
        adhocAnalyzerDashlet.clickOnOpenDropdown();
        adhocAnalyzerDashlet.clickOnExistingReport(reportName);
        Assert.assertEquals(adhocAnalyzerDashlet.getDashletTitle(), reportName);
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());

    }

}
