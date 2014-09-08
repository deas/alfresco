/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
import org.alfresco.po.share.SharePage;
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

    private SharePage page = null;
    AdhocAnalyzerPage adhocAnalyzePage = null;
    CreateEditAdhocReportPage createEditAdhocReportPage = null;

    @BeforeClass(alwaysRun = true)
    public void loadFiles() throws Exception
    {
        page = loginAs("pentahoBusinessAnalyst", "pentahoBusinessAnalyst");
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
    public void testCreateSaveOpenReport()
    {
        // create new report
        createEditAdhocReportPage.doubleClickOnSiteNameField();
        createEditAdhocReportPage.doubleClickOnEventTypeField();
        createEditAdhocReportPage.doubleClickOnNumberOfEventsField();

        // click on Save button
        SaveAnalysisPage saveAnalysisPage = createEditAdhocReportPage.clickOnSaveReportButton().render();

        // Enter report name
        String reportName = "NewReport-" + System.currentTimeMillis();
        saveAnalysisPage = saveAnalysisPage.enterAnalisysName(reportName).render();

        // Click on Ok button to save report
        createEditAdhocReportPage = saveAnalysisPage.clickOnSaveAnalisysOkButton();

        // Click on open button to open saved report
        createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();

        createEditAdhocReportPage.clickOnExistingReport(reportName);

        // check that the name of the report is saved correctly
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), reportName);
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), ADHOC_ANALYZE);

        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
    }

}
