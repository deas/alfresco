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

    @Test(groups = { "DataPrepCmisBrowser" })
    public void dataPrep_AONE_14552() throws Exception
    {
        dataPrepSecondaryObjectTypeIDsProperty(drone, testUser, getTestName(), siteName);
    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14552() throws Exception
    {
        testName = getTestName();
        fileName = getFileName(testName);
        String folderName = getFolderName(testName);
        String fileNodeRef = getNodeRef(cmisVersionAtom11, testUser, DOMAIN, siteName, "", fileName);
        String folderNodeRef = getNodeRef(cmisVersionAtom11, testUser, DOMAIN, siteName, "", folderName);

        List<Property<?>> fileProperties = getProperties(cmisVersionAtom11, testUser, DOMAIN, fileNodeRef);
        List<Property<?>> folderProperties = getProperties(cmisVersionAtom11, testUser, DOMAIN, folderNodeRef);

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

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14553() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14553() throws Exception
    {
        fileName = getFileName(getTestName());
        addClasifiableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "AONE_14553")
    public void AONE_14566() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeClasifiableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14554() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14554() throws Exception
    {
        fileName = getFileName(getTestName());
        addComplianceableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);

    }

    @Test(dependsOnMethods = "AONE_14554")
    public void AONE_14567() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeComplianceableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14555() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14555() throws Exception
    {
        fileName = getFileName(getTestName());
        addDublinCoreAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "AONE_14555")
    public void AONE_14568() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeDublinCoreAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14556() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14556() throws Exception
    {
        fileName = getFileName(getTestName());
        addEffectivityAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "AONE_14556")
    public void AONE_14569() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeEffectivityAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14557() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14557() throws Exception
    {
        fileName = getFileName(getTestName());
        addSummarizableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "AONE_14557")
    public void AONE_14570() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeSummarizableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14558() throws Exception
    {
        testName = getTestName();
        dataPrepTemplatableAspect(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14558() throws Exception
    {
        templateName = "Template-" + getFileName(getTestName());
        fileName = getFileName(getTestName());
        addTemplatableAspect(drone, testUser, fileName, siteName, templateName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "AONE_14558")
    public void AONE_14572() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeTemplatableAspect(drone, testUser, fileName, siteName, templateName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14559() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14559() throws Exception
    {
        fileName = getFileName(getTestName());
        addEmailedAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "AONE_14559")
    public void AONE_14573() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeEmailedAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14560() throws Exception
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
    public void AONE_14560() throws Exception
    {
        testName = getTestName();
        fileName = getFileName(testName);
        tag = ("Tag-" + getFileName(testName)).toLowerCase();

        addTaggableAspect(drone, testUser, fileName, siteName, tag, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "AONE_14560", groups = {"IntermittentBugs"})
    public void AONE_14574() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeTaggableAspect(drone, testUser, fileName, siteName, tag, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14561() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14561() throws Exception
    {
        fileName = getFileName(getTestName());
        addGeographicAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "AONE_14561")
    public void AONE_14575() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeGeographicAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14562() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14562() throws Exception
    {
        fileName = getFileName(getTestName());
        addEXIFAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "AONE_14562")
    public void AONE_14576() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeEXIFAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14563() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14563() throws Exception
    {
        fileName = getFileName(getTestName());
        addAudioAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "AONE_14563")
    public void AONE_14577() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeAudioAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14564() throws Exception
    {
        dataPrepIndexControlAspect(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14564() throws Exception
    {
        fileName = getFileName(getTestName());
        String content = "Content: " + getTestName();
        addIndexControlAspect(drone, testUser, fileName, content, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "AONE_14564")
    public void AONE_14578() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeIndexControlAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14565() throws Exception
    {
        dataPrep(drone, testUser, getTestName(), siteName);
    }

    @Test
    public void AONE_14565() throws Exception
    {
        fileName = getFileName(getTestName());
        addRestrictableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test (dependsOnMethods = "AONE_14565")
    public void AONE_14579() throws Exception
    {
        fileName = getFileName(getTestName(getDependsOnMethodName(this.getClass())));
        removeRestrictableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }

    @Test(groups = { "DataPrepCmisAtom" })
    public void dataPrep_AONE_14571() throws Exception
    {
        dataPrepRemoveVersionableAspect(drone, testUser, getTestName(), siteName, cmisVersionAtom11);
    }

    @Test
    public void AONE_14571() throws Exception
    {
        fileName = getFileName(getTestName());
        removeVersionableAspect(drone, testUser, fileName, siteName, cmisVersionAtom11);
    }
}
