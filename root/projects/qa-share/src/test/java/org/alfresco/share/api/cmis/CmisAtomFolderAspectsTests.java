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
    public void dataPrep_AONE_14580() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14580() throws Exception
    {
        folderName = getFolderName(getTestName());
        addClasifiableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "AONE_14580")
    public void AONE_14593() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeClasifiableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14581() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14581() throws Exception
    {
        folderName = getFolderName(getTestName());
        addComplianceableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);

    }

    @Test(dependsOnMethods = "AONE_14581")
    public void AONE_14594() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeComplianceableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14582() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14582() throws Exception
    {
        folderName = getFolderName(getTestName());
        addDublinCoreAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "AONE_14582")
    public void AONE_14595() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeDublinCoreAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14583() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14583() throws Exception
    {
        folderName = getFolderName(getTestName());
        addEffectivityAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "AONE_14583")
    public void AONE_14596() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeEffectivityAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14584() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14584() throws Exception
    {
        folderName = getFolderName(getTestName());
        addSummarizableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "AONE_14584")
    public void AONE_14597() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeSummarizableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14585() throws Exception
    {
        testName = getTestName();
        dataPrepTemplatableAspect(drone, testUser, testName, siteName);
    }

    @Test
    public void AONE_14585() throws Exception
    {
        testName = getTestName();
        String templateFolderName = "Template-" + getFolderName(testName);
        folderName = getFolderName(testName);
        addTemplatableAspect(drone, testUser, folderName, siteName, templateFolderName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "AONE_14585")
    public void AONE_14598() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        String templateFolderName = "Template-" + getFolderName(testName);
        removeTemplatableAspect(drone, testUser, folderName, siteName, templateFolderName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14586() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14586() throws Exception
    {
        folderName = getFolderName(getTestName());
        addEmailedAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "AONE_14586")
    public void AONE_14599() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeEmailedAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14587() throws Exception
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

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14587() throws Exception
    {
        testName = getTestName();
        folderName = getFolderName(testName);
        String tag = ("Tag-" + getFolderName(testName)).toLowerCase();

        addTaggableAspect(drone, testUser, folderName, siteName, tag, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "AONE_14587", groups = {"IntermittentBugs"})
    public void AONE_14600() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        String tag = ("Tag-" + getFolderName(testName)).toLowerCase();
        removeTaggableAspect(drone, testUser, folderName, siteName, tag, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14588() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14588() throws Exception
    {
        folderName = getFolderName(getTestName());
        addGeographicAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "AONE_14588")
    public void AONE_14601() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeGeographicAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14589() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14589() throws Exception
    {
        folderName = getFolderName(getTestName());
        addEXIFAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "AONE_14589")
    public void AONE_14602() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeEXIFAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14590() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14590() throws Exception
    {
        folderName = getFolderName(getTestName());
        addAudioAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "AONE_14590")
    public void AONE_14603() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeAudioAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14591() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14591() throws Exception
    {
        folderName = getFolderName(getTestName());
        addIndexControlAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "AONE_14591")
    public void AONE_14604() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeIndexControlAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14592() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14592() throws Exception
    {
        folderName = getFolderName(getTestName());
        addRestrictableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }

    @Test(dependsOnMethods = "AONE_14592")
    public void AONE_14605() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeRestrictableAspect(drone, testUser, folderName, siteName, cmisVersionAtom11);
    }
}
