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

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.rest.api.tests.client.PublicApiClient;
import org.alfresco.rest.api.tests.client.data.CMISNode;
import org.alfresco.rest.api.tests.client.data.ContentData;
import org.alfresco.rest.api.tests.client.data.FolderNode;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CmisUtils;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisStreamNotSupportedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for cmis tests with various bindings as poc
 * 
 * @author Meenal Bhave
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne", "MyAlfresco" })
public class CmisBrowserTests extends CmisUtils
{
    private String testName;
    private String testUser;
    private String siteName;
    private String fileName;
    private String folderName;
    private String testUser2;
    private static Log logger = LogFactory.getLog(CmisBrowserTests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName);
        testUser2 = getUserNameFreeDomain(testName + "_1");
        siteName = getSiteName(testName) + System.currentTimeMillis();
        fileName = getFileName(testName) + System.currentTimeMillis();
        folderName = getFolderName(testName) + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        ShareUser.login(drone, testUser2);

        ShareUser.createSite(drone, getSiteShortname(siteName), SITE_VISIBILITY_PUBLIC);

        ShareUser.openDocumentLibrary(drone);
    }

    @Test
    public void ALF_235601() throws Exception
    {
        // Create Folder
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, "folder-" + System.currentTimeMillis());
        Folder f = createFolder(CMISBinding.BROWSER11, testUser2, testUser2, DOMAIN, siteName, properties);

        // Create Document
        properties = new HashMap<String, String>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, "doc-" + System.currentTimeMillis());

        // Specify Content for the Document to be created
        StringBuilder fileContent = new StringBuilder();
        fileContent.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        fileContent.append("<entry xmlns=\"http://www.w3.org/2005/Atom\"");
        fileContent.append("xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\"");
        fileContent.append("xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\">");
        fileContent.append("<title>important document</title>");
        fileContent.append("<summary>VERY important document</summary>");
        fileContent.append("<content type=\"text/plain\">");
        fileContent.append("MS4gR2l0YSAKIDIuIEthcm1heW9nYSBieSBWaXZla2FuYW5k");
        fileContent.append("</content>");
        fileContent.append("<cmisra:object>");
        fileContent.append("<cmis:properties>");
        fileContent.append("<cmis:propertyId propertyDefinitionId=\"cmis:objectTypeId\"><cmis:value>cmis:document</cmis:value></cmis:propertyId>");
        fileContent.append("</cmis:properties>");
        fileContent.append("</cmisra:object>");
        fileContent.append("</entry>");
        String docContent = fileContent.toString();

        Document d1 = createDocument(CMISBinding.ATOMPUB10, testUser2, testUser2, DOMAIN, siteName, docContent, properties);

        // Copy Document to Folder created above
        Document d1Copied = d1.copy(f);

        // Compare Original Document with the Copied Document
        ContentStream docatom10 = d1.getContentStream();
        ContentStream docatom10Copied = d1Copied.getContentStream();

        assertTrue(docatom10Copied.getFileName().equals(docatom10.getFileName()));
        assertTrue(docatom10Copied.getMimeType().equals(docatom10.getMimeType()));
        assertFalse(docatom10Copied.equals(docatom10));

        // Create new Document as Original in the same folder with different name
        properties.put(PropertyIds.NAME, "doc-" + System.currentTimeMillis());

        Document d2 = createDocument(CMISBinding.ATOMPUB11, testUser2, testUser2, DOMAIN, siteName, docContent, properties);

        ContentStream docatom11 = d2.getContentStream();

        // Compare Original Document with the New Document in the same folder
        assertFalse(docatom11.getFileName().equals(docatom10.getFileName()));
        assertFalse(docatom11.getStream().equals(docatom10.getStream()));
        assertTrue(docatom11.getMimeType().equals(docatom10.getMimeType()));

        // Check In Minor Version of Document2
        properties.put(PropertyIds.NAME, "doc-" + System.currentTimeMillis());
        Document docNewVersion = checkInDocument(CMISBinding.ATOMPUB11, testUser2, testUser2, DOMAIN, d2, docContent, properties, false, "");

        String newVersion = d2.getVersionLabel();
        logger.info(newVersion);
        newVersion = docNewVersion.getVersionLabel();
        logger.info(newVersion);

        assertEquals(2, d2.getAllVersions().size());

        // Check In Major Version of Document2
        properties.put(PropertyIds.NAME, "doc-" + System.currentTimeMillis());
        docNewVersion = checkInDocument(CMISBinding.ATOMPUB11, testUser2, testUser2, DOMAIN, d1, docContent, properties, true, "Major Version");

        newVersion = d1.getVersionLabel();
        logger.info(newVersion);
        newVersion = docNewVersion.getVersionLabel();
        logger.info(newVersion);

        assertEquals(2, d1.getAllVersions().size());
    }

    @Test
    public void ALF_219001() throws Exception
    {
        String testName = getTestName();
        String thisFolderName = getFolderName(testName);
        String thisFileName = getFileName(testName);

        ShareUser.login(drone, testUser2);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.createFolderInFolder(drone, thisFolderName, thisFolderName, DOCLIB);
        ShareUser.uploadFileInFolder(drone, new String[] { thisFileName });
        String thisFileNameGuid = ShareUser.getGuid(drone, thisFileName);
        String thisFolderNameGuid = ShareUser.getGuid(drone, thisFolderName);


        PublicApiClient.CmisSession cmisSession = getCmisSession(CMISBinding.ATOMPUB10, testUser2, DOMAIN);

        ContentData document = cmisSession.getContent(thisFileNameGuid);
        assertTrue(document.getBytes().length > 0);

        String invalidId = thisFileNameGuid + "inv";
        try {
            document = cmisSession.getContent(invalidId);
        } catch (Exception e) {
            assertTrue(e instanceof CmisObjectNotFoundException, "Document should be null as the id does not exist:" + e);
        }
        try {
            document = cmisSession.getContent(thisFolderNameGuid);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException, "Object is not a document!:" + e);
        }

    }

    @Test
    public void ALF_219101() throws Exception
    {
        String testName = getTestName();
        String thisFolderName = getFolderName(testName);
        String thisFileName = getFileName(testName);

        ShareUser.login(drone, testUser2);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.createFolderInFolder(drone, thisFolderName, thisFolderName, DOCLIB);
        ShareUser.uploadFileInFolder(drone, new String[] { thisFileName, thisFolderName });
        String thisFileNameGuid = ShareUser.getGuid(drone, thisFileName);
        String thisFolderNameGuid = ShareUser.getGuid(drone, thisFolderName);


        PublicApiClient.CmisSession cmisSession = getCmisSession(CMISBinding.ATOMPUB10, testUser2, DOMAIN);

        FolderNode children = cmisSession.getChildren(thisFolderNameGuid, 0, 100);
        Map<String, CMISNode> documentNodes = children.getDocumentNodes();
        Collection<CMISNode> values = documentNodes.values();
        assertTrue(values.contains(getObject(CMISBinding.ATOMPUB10,testUser2, DOMAIN, thisFileNameGuid)));

        String invalidId = thisFolderName + "inv";

        try {
            children = cmisSession.getChildren(invalidId, 0, 100);
        } catch (Exception e) {
            assertTrue(e instanceof CmisObjectNotFoundException, "Document should be null as the id does not exist:" + e);
        }

        try {
            children = cmisSession.getChildren(thisFileNameGuid, 0, 100);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException, "Object is not a document!:" + e);
        }
    }

    @Test
    public void ALF_235301() throws Exception
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(CMISBinding.ATOMPUB10, testUser2, DOMAIN);
        RepositoryInfo repositoryInfo = cmisSession.getCMISSession().getRepositoryInfo();
    }

    @Test
    public void ALF_235701() throws Exception {
        String testName = getTestName();
        String thisFileName = getFileName(testName);

        ShareUser.login(drone, testUser2);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.uploadFileInFolder(drone, new String[]{thisFileName});
        String thisFileNameGuid = ShareUser.getGuid(drone, thisFileName);

        PublicApiClient.CmisSession cmisSession = getCmisSession(CMISBinding.ATOMPUB10, testUser2, DOMAIN);
        List<Document> allVersions = cmisSession.getAllVersions(thisFileNameGuid);
    }


    @Test
    public void ALF_237201() throws Exception
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(CMISBinding.ATOMPUB10, testUser2, DOMAIN);
//        cmisSession.
    }

}
