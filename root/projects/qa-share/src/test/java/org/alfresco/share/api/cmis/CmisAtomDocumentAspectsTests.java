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

package org.alfresco.share.api.cmis;

import java.util.Arrays;

import org.alfresco.share.enums.CMISBinding;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for cmis with Atom11 binding
 * 
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
public class CmisAtomDocumentAspectsTests extends CmisDocumentAspectUtils
{
    private String testUser;
    private String siteName;
    private String fileName;
    private String templateName;
    private String tag;
    private CMISBinding cmisVersionAtom11 = CMISBinding.ATOMPUB11;

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        siteName = getSiteShortname(getSiteName(testName));

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // This util has been purposely used since beforeClass runs for dataprep as well as test
        SiteUtil.createSite(drone, siteName, testName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2478() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2478() throws Exception
    {
        fileName = getFileName(getTestName());
        addClasifiableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "ALF_2478")
    public void ALF_2493() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeClasifiableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2479() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2479() throws Exception
    {
        fileName = getFileName(getTestName());
        addComplianceableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);

    }

    @Test(dependsOnMethods = "ALF_2479")
    public void ALF_2494() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeComplianceableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2480() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2480() throws Exception
    {
        fileName = getFileName(getTestName());
        addDublinCoreAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "ALF_2480")
    public void ALF_2495() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeDublinCoreAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2481() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2481() throws Exception
    {
        fileName = getFileName(getTestName());
        addEffectivityAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "ALF_2481")
    public void ALF_2496() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeEffectivityAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2482() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2482() throws Exception
    {
        fileName = getFileName(getTestName());
        addSummarizableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "ALF_2482")
    public void ALF_2497() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeSummarizableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2483() throws Exception
    {
        testName = getTestName();
        dataPrepTemplatableAspect(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2483() throws Exception
    {
        templateName = "Template-" + getFileName(getTestName());
        fileName = getFileName(getTestName());
        addTemplatableAspect(drone, testUser, fileName, siteName, templateName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "ALF_2483")
    public void ALF_2499() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeTemplatableAspect(drone, testUser, fileName, siteName, templateName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2484() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2484() throws Exception
    {
        fileName = getFileName(getTestName());
        addEmailedAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "ALF_2484")
    public void ALF_2500() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeEmailedAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom", "EnterpriseOnly" })
    public void dataPrep_ALF_2486() throws Exception
    {
        testName = getTestName();
        fileName = getFileName(testName);
        fileName = getFileName(testName);
        String fileName1 = getFileName(testName + "-1");
        String[] fileInfo1 = { fileName1 };

        tag = ("Tag-" + getFileName(testName)).toLowerCase();

        dataPrep(drone, testUser, testName, siteName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        if(!ShareUserSitePage.isFileVisible(drone, fileName1))
        {
            ShareUser.uploadFileInFolder(drone, fileInfo1);
        }
        ShareUserSitePage.addTagsFromDocLib(drone, fileName1, Arrays.asList(tag));
        ShareUser.logout(drone);
    }

    // TODO - Need to know how to get Tag NodeRef in Cloud.
    @Test (groups = "EnterpriseOnly")
    public void ALF_2486() throws Exception
    {
        testName = getTestName();
        fileName = getFileName(testName);
        tag = ("Tag-" + getFileName(testName)).toLowerCase();

        addTaggableAspect(drone, testUser, fileName, siteName, tag, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "ALF_2486", groups = "EnterpriseOnly")
    public void ALF_2502() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeTaggableAspect(drone, testUser, fileName, siteName, tag, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2488() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2488() throws Exception
    {
        fileName = getFileName(getTestName());
        addGeographicAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "ALF_2488")
    public void ALF_2504() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeGeographicAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2489() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2489() throws Exception
    {
        fileName = getFileName(getTestName());
        addEXIFAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "ALF_2489")
    public void ALF_2505() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeEXIFAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2490() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2490() throws Exception
    {
        fileName = getFileName(getTestName());
        addAudioAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "ALF_2490")
    public void ALF_2506() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeAudioAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2491() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2491() throws Exception
    {
        fileName = getFileName(getTestName());
        addIndexControlAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "ALF_2491")
    public void ALF_2507() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeIndexControlAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2492() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2492() throws Exception
    {
        fileName = getFileName(getTestName());
        addRestrictableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "ALF_2492")
    public void ALF_2508() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeRestrictableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }
}
