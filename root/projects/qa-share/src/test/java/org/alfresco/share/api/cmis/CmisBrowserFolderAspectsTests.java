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
 * Class to include: Tests for cmis with BROWSER11 binding
 * 
 * @author Ranjith Manyam
 */

@Listeners(FailedTestListener.class)
public class CmisBrowserFolderAspectsTests extends CmisFolderAspectUtils
{
    private String testUser;
    private String siteName;
    private String folderName;
    private CMISBinding cmisVersionBrowser = CMISBinding.BROWSER11;

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

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159291() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_159291() throws Exception
    {
        folderName = getFolderName(getTestName());
        addClasifiableAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(dependsOnMethods = "ALF_159291")
    public void ALF_159441() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeClasifiableAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159301() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_159301() throws Exception
    {
        folderName = getFolderName(getTestName());
        addComplianceableAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);

    }

    @Test(dependsOnMethods = "ALF_159301")
    public void ALF_159451() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeComplianceableAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159311() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_159311() throws Exception
    {
        folderName = getFolderName(getTestName());
        addDublinCoreAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(dependsOnMethods = "ALF_159311")
    public void ALF_159461() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeDublinCoreAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159321() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_159321() throws Exception
    {
        folderName = getFolderName(getTestName());
        addEffectivityAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(dependsOnMethods = "ALF_159321")
    public void ALF_159471() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeEffectivityAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159331() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_159331() throws Exception
    {
        folderName = getFolderName(getTestName());
        addSummarizableAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(dependsOnMethods = "ALF_159331")
    public void ALF_159481() throws Exception
    {
        folderName = getFolderName(getTestName(getDependsOnMethodName(this.getClass())));
        removeSummarizableAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159341() throws Exception
    {
        testName = getTestName();
        dataPrepTemplatableAspect(drone, testUser, testName, siteName);
    }

    @Test
    public void ALF_159341() throws Exception
    {
        testName = getTestName();
        String templateFolderName = "Template-" + getFolderName(testName);
        folderName = getFolderName(testName);
        addTemplatableAspect(drone, testUser, folderName, siteName, templateFolderName, cmisVersionBrowser);
    }

    @Test(dependsOnMethods = "ALF_159341")
    public void ALF_159501() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        String templateFolderName = "Template-" + getFolderName(testName);
        removeTemplatableAspect(drone, testUser, folderName, siteName, templateFolderName, cmisVersionBrowser);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159351() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_159351() throws Exception
    {
        folderName = getFolderName(getTestName());
        addEmailedAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(dependsOnMethods = "ALF_159351")
    public void ALF_159511() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeEmailedAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159371() throws Exception
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

    @Test
    public void ALF_159371() throws Exception
    {
        testName = getTestName();
        folderName = getFolderName(testName);
        String tag = ("Tag-" + getFolderName(testName)).toLowerCase();

        addTaggableAspect(drone, testUser, folderName, siteName, tag, cmisVersionBrowser);
    }

    @Test(dependsOnMethods = "ALF_159371")
    public void ALF_159531() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        String tag = ("Tag-" + getFolderName(testName)).toLowerCase();
        removeTaggableAspect(drone, testUser, folderName, siteName, tag, cmisVersionBrowser);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159391() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_159391() throws Exception
    {
        folderName = getFolderName(getTestName());
        addGeographicAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(dependsOnMethods = "ALF_159391")
    public void ALF_159551() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeGeographicAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159401() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_159401() throws Exception
    {
        folderName = getFolderName(getTestName());
        addEXIFAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(dependsOnMethods = "ALF_159401")
    public void ALF_159561() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeEXIFAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159411() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_159411() throws Exception
    {
        folderName = getFolderName(getTestName());
        addAudioAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(dependsOnMethods = "ALF_159411")
    public void ALF_159571() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeAudioAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159421() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_159421() throws Exception
    {
        folderName = getFolderName(getTestName());
        addIndexControlAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(dependsOnMethods = "ALF_159421")
    public void ALF_159581() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeIndexControlAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_ALF_159431() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void ALF_159431() throws Exception
    {
        folderName = getFolderName(getTestName());
        addRestrictableAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }

    @Test(dependsOnMethods = "ALF_159431")
    public void ALF_159591() throws Exception
    {
        testName = getTestName(getDependsOnMethodName(this.getClass()));
        folderName = getFolderName(testName);
        removeRestrictableAspect(drone, testUser, folderName, siteName, cmisVersionBrowser);
    }
}
