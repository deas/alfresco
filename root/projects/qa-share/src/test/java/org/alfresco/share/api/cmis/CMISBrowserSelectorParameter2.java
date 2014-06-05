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
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    public void dataPrep_ALF_158901() throws Exception
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
    public void ALF_158901() throws Exception
    {
        String fileName1 = getFileName(getTestName() + "-1");

        String fileName2 = getFileName(getTestName() + "-2");
        selectorRelationship(cmisBinding, testUser, siteName, fileName1, fileName2);
    }

    @Test (groups = "DataPrepCmisBrowser" )
    public void dataPrep_ALF_158911() throws Exception
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
    public void ALF_158911() throws Exception
    {
        String thisTestName = getTestName();
        selectorVersions(cmisBinding, testUser, thisTestName);
    }

    @Test (groups = "DataPrepCmisBrowser" )
    public void dataPrep_ALF_158921() throws Exception
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
    public void ALF_158921() throws Exception
    {
        String fileName = getFileName(getTestName());
        String folderName = getFolderName(getTestName());
        selectorPolicies(cmisBinding, testUser, siteName, fileName, folderName);
    }

    @Test (groups = "DataPrepCmisBrowser" )
    public void dataPrep_ALF_158931() throws Exception
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
    public void ALF_158931() throws Exception
    {
        String fileName = getFileName(getTestName());
        String folderName = getFolderName(getTestName());
        selectorACL(cmisBinding, testUser, siteName, fileName, folderName);
    }

    @Test (groups = "DataPrepCmisBrowser" )
    public void dataPrep_ALF_159931() throws Exception
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
    public void ALF_159931() throws Exception
    {
        String fileName1 = getFileName(getTestName() + "-1");
        String folderName = getFolderName(getTestName());
        selectorNotSpecified(cmisBinding, testUser, siteName, fileName1, folderName);
    }

    // TODO - Working in Manually but not through API
    @Test
    public void ALF_159941() throws Exception
    {
        String url = getAPIURL(drone)+DOMAIN+"/public/cmis/versions/1.1/browser";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cmisselector", "RepoSitoryinfo");
        System.out.println("getAuthDetails(testUser)[0]: " + getAuthDetails(testUser)[0]);
        System.out.println("getAuthDetails(testUser)[1]: " + getAuthDetails(testUser)[1]);
        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
        HttpResponse httpResponse = publicApiClient.get(url, params);
        System.out.println("URL: " + url );
        System.out.println("Response: " + httpResponse.getResponse() );
        assertTrue(httpResponse.getStatusCode() == 200, httpResponse.getResponse());
    }

    @Test
    public void ALF_159971() throws Exception
    {
        selectorQuery(cmisBinding, testUser);
    }
}
