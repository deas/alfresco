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
public class CmisAtomFolderAspectsTests extends CmisFolderAspectUtils
{
    private String testUser;
    private String siteName;
    private String folderName;
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
        // This util has been purposely used since beforeClass runs for dataprep
        // as well as test
        SiteUtil.createSite(drone, siteName, testName, SITE_VISIBILITY_PUBLIC, true);
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2509() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2509() throws Exception
    {
        folderName = getFolderName(getTestName());
        addClasifiableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "ALF_2509")
    public void ALF_2524() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeClasifiableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2510() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2510() throws Exception
    {
        folderName = getFolderName(getTestName());
        addComplianceableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);

    }

    @Test(dependsOnMethods = "ALF_2510")
    public void ALF_2525() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeComplianceableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2511() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2511() throws Exception
    {
        folderName = getFolderName(getTestName());
        addDublinCoreAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "ALF_2511")
    public void ALF_2526() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeDublinCoreAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2512() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2512() throws Exception
    {
        folderName = getFolderName(getTestName());
        addEffectivityAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "ALF_2512")
    public void ALF_2527() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeEffectivityAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2513() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2513() throws Exception
    {
        folderName = getFolderName(getTestName());
        addSummarizableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "ALF_2513")
    public void ALF_2528() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeSummarizableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2514() throws Exception
    {
        testName = getTestName();
        dataPrepTemplatableAspect(drone, testUser, testName, siteName);
    }

    @Test
    public void ALF_2514() throws Exception
    {
        testName = getTestName();
        String templateFolderName = "Template-" + getFolderName(testName);
        folderName = getFolderName(testName);
        addTemplatableAspect(drone, testUser, folderName, siteName, templateFolderName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "ALF_2514")
    public void ALF_2529() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        String templateFolderName = "Template-" + getFolderName(testName);
        removeTemplatableAspect(drone, testUser, folderName, siteName, templateFolderName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2515() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2515() throws Exception
    {
        folderName = getFolderName(getTestName());
        addEmailedAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "ALF_2515")
    public void ALF_2530() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeEmailedAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2517() throws Exception
    {
        testName = getTestName();
        folderName = getFolderName(testName);
        String folderName1 = getFolderName(testName + "-1");

        String tag = ("Tag-" + getFolderName(testName)).toLowerCase();

        dataPrep(drone, testUser, testName, siteName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        if (!ShareUserSitePage.isFileVisible(drone, folderName1))
        {
            ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        }
        ShareUserSitePage.addTagsFromDocLib(drone, folderName1, Arrays.asList(tag));
        ShareUser.logout(drone);
    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2517() throws Exception
    {
        testName = getTestName();
        folderName = getFolderName(testName);
        String tag = ("Tag-" + getFolderName(testName)).toLowerCase();

        addTaggableAspect(drone, testUser, folderName, siteName, tag, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "ALF_2517", groups = "EnterpriseOnly")
    public void ALF_2532() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        String tag = ("Tag-" + getFolderName(testName)).toLowerCase();
        removeTaggableAspect(drone, testUser, folderName, siteName, tag, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2519() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2519() throws Exception
    {
        folderName = getFolderName(getTestName());
        addGeographicAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "ALF_2519")
    public void ALF_2534() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeGeographicAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2520() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2520() throws Exception
    {
        folderName = getFolderName(getTestName());
        addEXIFAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "ALF_2520")
    public void ALF_2535() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeEXIFAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2521() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2521() throws Exception
    {
        folderName = getFolderName(getTestName());
        addAudioAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "ALF_2521")
    public void ALF_2536() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeAudioAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2522() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2522() throws Exception
    {
        folderName = getFolderName(getTestName());
        addIndexControlAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "ALF_2522")
    public void ALF_2537() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeIndexControlAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_ALF_2523() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_2523() throws Exception
    {
        folderName = getFolderName(getTestName());
        addRestrictableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "ALF_2523")
    public void ALF_2538() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeRestrictableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }
}
