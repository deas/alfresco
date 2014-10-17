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
import java.util.List;

import org.alfresco.share.enums.CMISBinding;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.chemistry.opencmis.client.api.Property;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for cmis  with Browser11 binding
 * 
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
public class CmisBrowserDocumentAspectsTests extends CmisDocumentAspectUtils
{
    private String testUser;
    private String siteName;
    private String fileName;
    private String templateName;
    private String tag;
    private CMISBinding cmisVersion = CMISBinding.BROWSER11;

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // This util has been purposely used since beforeClass runs for dataprep as well as test
        SiteUtil.createSite(drone, siteName, testName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14435() throws Exception
    {
        dataPrepSecondaryObjectTypeIDsProperty(drone, testUser, getTestName(), siteName);
    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14435() throws Exception
    {
        testName = getTestName();
        fileName = getFileName(testName);
        String folderName = getFolderName(testName);
        String fileNodeRef = getNodeRef(cmisVersion, testUser, DOMAIN, siteName, "", fileName);
        String folderNodeRef = getNodeRef(cmisVersion, testUser, DOMAIN, siteName, "", folderName);

        List<Property<?>> fileProperties = getProperties(cmisVersion, testUser, DOMAIN, fileNodeRef);
        List<Property<?>> folderProperties = getProperties(cmisVersion, testUser, DOMAIN, folderNodeRef);

        for (Property property : fileProperties)
        {
            if(property.getId().equals("cmis:secondaryObjectTypeIds"))
                {
                Assert.assertEquals(property.getId(), "cmis:secondaryObjectTypeIds", "Verifying propertyDefinitionId");
                Assert.assertEquals(property.getLocalName(), "secondaryObjectTypeIds", "Verifying LocalName");
                Assert.assertEquals(property.getDisplayName(), "Secondary Object Type Ids", "Verifying DisplayName");
                Assert.assertEquals(property.getQueryName(), "cmis:secondaryObjectTypeIds", "Verifying QueryName");
                Assert.assertEquals(property.getType().value(), "id", "Verifying ID");
                Assert.assertEquals(property.getDefinition().getCardinality().value(), "multi", "Verifying Cardinality");
                Assert.assertTrue(property.getValuesAsString().contains("P:cm:titled"), "Verifying \"P:cm:titled\" value exists");
                Assert.assertTrue(property.getValuesAsString().contains("P:sys:localized"), "Verifying \"P:sys:localized\" value exists");
            }
        }

        for (Property property : folderProperties)
        {
            if(property.getId().equals("cmis:secondaryObjectTypeIds"))
            {
                Assert.assertEquals(property.getId(), "cmis:secondaryObjectTypeIds", "Verifying propertyDefinitionId");
                Assert.assertEquals(property.getLocalName(), "secondaryObjectTypeIds", "Verifying LocalName");
                Assert.assertEquals(property.getDisplayName(), "Secondary Object Type Ids", "Verifying DisplayName");
                Assert.assertEquals(property.getQueryName(), "cmis:secondaryObjectTypeIds", "Verifying QueryName");
                Assert.assertEquals(property.getType().value(), "id", "Verifying ID");
                Assert.assertEquals(property.getDefinition().getCardinality().value(), "multi", "Verifying Cardinality");
                Assert.assertTrue(property.getValuesAsString().contains("P:cm:titled"), "Verifying \"P:cm:titled\" value exists");
                Assert.assertTrue(property.getValuesAsString().contains("P:sys:localized"), "Verifying \"P:sys:localized\" value exists");
            }
        }

    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14436() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14436() throws Exception
    {
        fileName = getFileName(getTestName());
        addClasifiableAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test (dependsOnMethods = "AONE_14436")
    public void AONE_14449() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeClasifiableAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14437() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14437() throws Exception
    {
        fileName = getFileName(getTestName());
        addComplianceableAspect(drone, testUser, fileName, siteName, cmisVersion);

    }

    @Test(dependsOnMethods = "AONE_14437")
    public void AONE_14450() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeComplianceableAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14438() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14438() throws Exception
    {
        fileName = getFileName(getTestName());
        addDublinCoreAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test (dependsOnMethods = "AONE_14438")
    public void AONE_14451() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeDublinCoreAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14439() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14439() throws Exception
    {
        fileName = getFileName(getTestName());
        addEffectivityAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test (dependsOnMethods = "AONE_14439")
    public void AONE_14452() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeEffectivityAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14440() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14440() throws Exception
    {
        fileName = getFileName(getTestName());
        addSummarizableAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test (dependsOnMethods = "AONE_14440")
    public void AONE_14453() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeSummarizableAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14441() throws Exception
    {
        testName = getTestName();
        dataPrepTemplatableAspect(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14441() throws Exception
    {
        templateName = "Template-" + getFileName(getTestName());
        fileName = getFileName(getTestName());
        addTemplatableAspect(drone, testUser, fileName, siteName, templateName, cmisVersion);
    }

    @Test (dependsOnMethods = "AONE_14441")
    public void AONE_14455() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeTemplatableAspect(drone, testUser, fileName, siteName, templateName, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14442() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14442() throws Exception
    {
        fileName = getFileName(getTestName());
        addEmailedAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test (dependsOnMethods = "AONE_14442")
    public void AONE_14456() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeEmailedAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisBrowser"})
    public void dataPrep_AONE_14443() throws Exception
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

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14443() throws Exception
    {
        testName = getTestName();
        fileName = getFileName(testName);
        tag = ("Tag-" + getFileName(testName)).toLowerCase();

        addTaggableAspect(drone, testUser, fileName, siteName, tag, cmisVersion);
    }

    @Test (dependsOnMethods = "AONE_14443", groups = {"IntermittentBugs"})
    public void AONE_14457() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeTaggableAspect(drone, testUser, fileName, siteName, tag, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14444() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14444() throws Exception
    {
        fileName = getFileName(getTestName());
        addGeographicAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test (dependsOnMethods = "AONE_14444")
    public void AONE_14458() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeGeographicAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14445() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14445() throws Exception
    {
        fileName = getFileName(getTestName());
        addEXIFAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test (dependsOnMethods = "AONE_14445")
    public void AONE_14459() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeEXIFAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14446() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14446() throws Exception
    {
        fileName = getFileName(getTestName());
        addAudioAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test (dependsOnMethods = "AONE_14446")
    public void AONE_14460() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeAudioAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14447() throws Exception
    {
        dataPrepIndexControlAspect(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14447() throws Exception
    {
        fileName = getFileName(getTestName());
        String content = "Content: " + getTestName();
        addIndexControlAspect(drone, testUser, fileName, content, siteName, cmisVersion);
    }

    @Test (dependsOnMethods = "AONE_14447")
    public void AONE_14461() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeIndexControlAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14448() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14448() throws Exception
    {
        fileName = getFileName(getTestName());
        addRestrictableAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test (dependsOnMethods = "AONE_14448")
    public void AONE_14462() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeRestrictableAspect(drone, testUser, fileName, siteName, cmisVersion);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14454() throws Exception
    {
        dataPrepRemoveVersionableAspect(drone, testUser, getTestName(), siteName, cmisVersion);
    }

    @Test
    public void AONE_14454() throws Exception
    {
        fileName = getFileName(getTestName());
        removeVersionableAspect(drone, testUser, fileName, siteName, cmisVersion);
    }
}
