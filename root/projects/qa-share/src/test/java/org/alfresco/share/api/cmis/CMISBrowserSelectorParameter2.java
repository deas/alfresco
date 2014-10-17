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

import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for CMIS Selector Parameter for Browser binding
 * 
 * @author Ranjith Manyam
 * 
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne", "MyAlfresco" })
public class CMISBrowserSelectorParameter2 extends CMISSelectorParameter
{

    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(CMISBrowserSelectorParameter2.class);
    private CMISBinding cmisBinding;


    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        try
        {
            super.beforeClass();
            cmisBinding = CMISBinding.BROWSER11;
            testName = this.getClass().getSimpleName();
            testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            siteName = getSiteName(testName);

            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            SiteUtil.createSite(drone, siteName, testName, SITE_VISIBILITY_PUBLIC, true);
            ShareUser.logout(drone);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    @Test (groups = "DataPrepCmisBrowser" )
    public void dataPrep_AONE_14515() throws Exception
    {
        String fileName1 = getFileName(getTestName() + "-1");
        String[] fileInfo1 = { fileName1 };

        String fileName2 = getFileName(getTestName() + "-2");
        String[] fileInfo2 = { fileName2 };

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        if (!ShareUserSitePage.isFileVisible(drone, fileName1))
        {
            ShareUser.uploadFileInFolder(drone, fileInfo1);
        }
        if (!ShareUserSitePage.isFileVisible(drone, fileName2))
        {
            ShareUser.uploadFileInFolder(drone, fileInfo2);
        }

        String f1NodeRef = ShareUser.getGuid(drone, fileName1);
        String f2NodeRef = ShareUser.getGuid(drone, fileName2);

        PublicApiClient.CmisSession cmisSession = getCmisSession(cmisBinding, testUser, DOMAIN);

        Document f1 = (Document) cmisSession.getObject(f1NodeRef);
        Document f2 = (Document) cmisSession.getObject(f2NodeRef);

        createRelationship(cmisBinding, testUser, DOMAIN, f1NodeRef, f2NodeRef, "R:cm:basis");

        Document f1AfterRelation = (Document) cmisSession.getObject(f1NodeRef);
        Document f2AfterRelation = (Document) cmisSession.getObject(f2NodeRef);

        assertTrue(f1.getLastModificationDate().equals(f1AfterRelation.getLastModificationDate()));
        assertTrue(f2.getLastModificationDate().equals(f2AfterRelation.getLastModificationDate()));
        assertTrue(f1.getVersionLabel().equals(f1AfterRelation.getVersionLabel()));
        assertTrue(f2.getVersionLabel().equals(f2AfterRelation.getVersionLabel()));

        ShareUser.logout(drone);
    }

    @Test
    public void AONE_14515() throws Exception
    {
        String fileName1 = getFileName(getTestName() + "-1");

        String fileName2 = getFileName(getTestName() + "-2");
        selectorRelationship(cmisBinding, testUser, siteName, fileName1, fileName2);
    }

    @Test (groups = "DataPrepCmisBrowser" )
    public void dataPrep_AONE_14516() throws Exception
    {
        String fileName = getFileName(getTestName());
        String[] fileInfo = { fileName };

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        if (!ShareUserSitePage.isFileVisible(drone, fileName))
        {
            ShareUser.uploadFileInFolder(drone, fileInfo);
            ShareUserSitePage.openDetailsPage(drone, fileName);
            ShareUser.uploadNewVersionOfDocument(drone, fileName, fileName);
            ShareUser.uploadNewVersionOfDocument(drone, fileName, fileName);
        }

        ShareUser.logout(drone);
    }

    @Test
    public void AONE_14516() throws Exception
    {
        String thisTestName = getTestName();
        selectorVersions(cmisBinding, testUser, thisTestName);
    }

    @Test (groups = "DataPrepCmisBrowser" )
    public void dataPrep_AONE_14517() throws Exception
    {
        String fileName = getFileName(getTestName());
        String folderName = getFolderName(getTestName());
        String[] fileInfo = { fileName , folderName};

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        if (!ShareUserSitePage.isFileVisible(drone, folderName))
        {
            ShareUserSitePage.createFolder(drone, folderName, folderName);
        }
        else
        {
            ShareUserSitePage.navigateToFolder(drone, folderName);
        }
        if (!ShareUserSitePage.isFileVisible(drone, fileName))
        {
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }

        ShareUser.logout(drone);
    }

    @Test
    public void AONE_14517() throws Exception
    {
        String fileName = getFileName(getTestName());
        String folderName = getFolderName(getTestName());
        selectorPolicies(cmisBinding, testUser, siteName, fileName, folderName);
    }

    @Test (groups = "DataPrepCmisBrowser" )
    public void dataPrep_AONE_14518() throws Exception
    {
        String fileName = getFileName(getTestName());
        String folderName = getFolderName(getTestName());
        String[] fileInfo = { fileName , folderName};

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        if (!ShareUserSitePage.isFileVisible(drone, folderName))
        {
            ShareUserSitePage.createFolder(drone, folderName, folderName);
        }
        else
        {
            ShareUserSitePage.navigateToFolder(drone, folderName);
        }
        if (!ShareUserSitePage.isFileVisible(drone, fileName))
        {
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }

        ShareUser.logout(drone);
    }

    @Test
    public void AONE_14518() throws Exception
    {
        String fileName = getFileName(getTestName());
        String folderName = getFolderName(getTestName());
        selectorACL(cmisBinding, testUser, siteName, fileName, folderName);
    }

    @Test (groups = "DataPrepCmisBrowser" )
    public void dataPrep_AONE_14519() throws Exception
    {
        String fileName1 = getFileName(getTestName() + "-1");
        String fileName2 = getFileName(getTestName() + "-2");
        String folderName = getFolderName(getTestName());
        String[] fileInfo1 = { fileName1 , folderName};
        String[] fileInfo2 = { fileName2 , folderName};

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        if (!ShareUserSitePage.isFileVisible(drone, folderName))
        {
            ShareUserSitePage.createFolder(drone, folderName, folderName);
        }
        else
        {
            ShareUserSitePage.navigateToFolder(drone, folderName);
        }
        if (!ShareUserSitePage.isFileVisible(drone, fileName1))
        {
            ShareUser.uploadFileInFolder(drone, fileInfo1);
        }
        if (!ShareUserSitePage.isFileVisible(drone, fileName2))
        {
            ShareUser.uploadFileInFolder(drone, fileInfo2);
        }

        String f1NodeRef = ShareUser.getGuid(drone, fileName1);
        String f2NodeRef = ShareUser.getGuid(drone, fileName2);

        createRelationship(cmisBinding, testUser, DOMAIN, f1NodeRef, f2NodeRef, "R:cm:basis");

        ShareUser.logout(drone);
    }

    @Test
    public void AONE_14519() throws Exception
    {
        String fileName1 = getFileName(getTestName() + "-1");
        String folderName = getFolderName(getTestName());
        selectorNotSpecified(cmisBinding, testUser, siteName, fileName1, folderName);
    }

    @Test
    public void AONE_14520() throws Exception
    {
        String url = DOMAIN + "/public/cmis/versions/1.1/browser";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cmisselector", "RepoSitoryinfo");
        HttpResponse httpResponse = getHttpResponse(url, params);
        assertTrue(httpResponse.getStatusCode() == 200, httpResponse.getResponse());

        JSONObject jsonObject = (JSONObject) httpResponse.getJsonResponse().get(DOMAIN);

        Assert.assertEquals(jsonObject.get("repositoryId"), DOMAIN, "Verifying repositoryId");
        String expectedRepositoryDescription = "";
        if(alfrescoVersion.isCloud())
        {
            expectedRepositoryDescription = DOMAIN;
        }
        Assert.assertEquals(jsonObject.get("repositoryDescription"), expectedRepositoryDescription, "Verifying repositoryDescription");
        Assert.assertEquals(jsonObject.get("vendorName"), "Alfresco", "Verifying vendorName");
        Assert.assertEquals(jsonObject.get("productName"), "Alfresco Enterprise", "Verifying productName");
        Assert.assertNotNull(jsonObject.get("productVersion"), "Verifying productVersion");
        Assert.assertNotNull(jsonObject.get("rootFolderId"), "Verifying rootFolderId");

        JSONObject capabilities = (JSONObject) jsonObject.get("capabilities");

        Assert.assertEquals(capabilities.get("capabilityContentStreamUpdatability"), "anytime", "Verifying capabilityContentStreamUpdatability");
        Assert.assertEquals(capabilities.get("capabilityChanges"), "none", "Verifying capabilityChanges");
        Assert.assertEquals(capabilities.get("capabilityRenditions"), "read", "Verifying capabilityRenditions");
        Assert.assertTrue((Boolean) capabilities.get("capabilityGetDescendants"), "Verifying capabilityGetDescendants");
        Assert.assertTrue((Boolean) capabilities.get("capabilityGetFolderTree"), "Verifying capabilityGetFolderTree");
        Assert.assertTrue((Boolean) capabilities.get("capabilityMultifiling"), "Verifying capabilityMultifiling");
        Assert.assertFalse((Boolean) capabilities.get("capabilityUnfiling"), "Verifying capabilityUnfiling");
        Assert.assertFalse((Boolean) capabilities.get("capabilityVersionSpecificFiling"), "Verifying capabilityVersionSpecificFiling");
        Assert.assertFalse((Boolean) capabilities.get("capabilityPWCSearchable"), "Verifying capabilityPWCSearchable");
        Assert.assertTrue((Boolean) capabilities.get("capabilityPWCUpdatable"), "Verifying capabilityPWCUpdatable");
        Assert.assertFalse((Boolean) capabilities.get("capabilityAllVersionsSearchable"), "Verifying capabilityAllVersionsSearchable");
        Assert.assertNull(capabilities.get("capabilityOrderBy"), "Verifying capabilityOrderBy");
        Assert.assertEquals(capabilities.get("capabilityQuery"), "bothcombined", "Verifying capabilityQuery");
        Assert.assertEquals(capabilities.get("capabilityJoin"), "none", "Verifying capabilityJoin");
        Assert.assertEquals(capabilities.get("capabilityACL"), "manage", "Verifying capabilityACL");

    }

    @Test
    public void AONE_14522() throws Exception
    {
        selectorQuery(cmisBinding, testUser);
    }
}
