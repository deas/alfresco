/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.api.cmis;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.VersionDetails;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.rest.api.tests.client.PublicApiClient.CmisSession;
import org.alfresco.rest.api.tests.client.data.CMISNode;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CmisUtils;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.Principal;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlEntryImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlPrincipalDataImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * @author abharade
 * 
 */
public abstract class CMISActionValuesTest extends CmisUtils
{

    protected String testUser;
    protected String deletableTestUser;
    protected String siteName;
    protected String fileName;
    protected String fileName1;
    protected String deleteVersionFile;
    protected String folderName;
    protected String sourceFolderName;
    protected String folderRef;
    protected String sourceNodeRef;
    protected String targetNodeRef;
    protected String sourceFolderRef;
    protected String deleteVersionNodeRef;
    protected final String fileNameContent = "This is main file content";

    protected CMISBinding binding;
    private String oldVersionLabel;
    private String otherTestUser;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss");
    private long oldDocSize;

    /**
     * @throws Exception
     * @param drone
     * @param uniqueName
     */
    protected void createTestData(WebDrone drone, String uniqueName) throws Exception
    {
        testUser = getUserNameFreeDomain(uniqueName);
        deletableTestUser = getUserNameFreeDomain("deletable" + uniqueName);
        otherTestUser = getUserNameFreeDomain("other" + uniqueName);


        siteName = getSiteName(uniqueName) + System.currentTimeMillis();

        fileName = getFileName(uniqueName) + System.currentTimeMillis();
        deleteVersionFile = getFileName(uniqueName + "Version") + System.currentTimeMillis();
        fileName1 = getFileName(uniqueName + "1") + System.currentTimeMillis();
        folderName = getFolderName(uniqueName) + System.currentTimeMillis();
        sourceFolderName = getFolderName(uniqueName + "Source") + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, deletableTestUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, otherTestUser);

        ShareUser.login(drone, testUser);

        ShareUser.createSite(drone, getSiteShortname(siteName), SITE_VISIBILITY_PUBLIC, true);
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
        folderRef = ShareUser.getGuid(drone, folderName);

        ShareUser.createFolderInFolder(drone, sourceFolderName, sourceFolderName, DOCLIB);
        sourceFolderRef = ShareUser.getGuid(drone, sourceFolderName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName, "", fileNameContent });
        sourceNodeRef = ShareUser.getGuid(drone, fileName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, "", fileNameContent });
        targetNodeRef = ShareUser.getGuid(drone, fileName1);

        ShareUser.uploadFileInFolder(drone, new String[] { deleteVersionFile });
        deleteVersionNodeRef = ShareUser.getGuid(drone, deleteVersionFile);
    }

    /**
     * @param thisFileName
     * @throws IOException
     */
    protected void createDocTest(String thisFileName) throws Exception
    {
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, thisFileName);
        String docContent = "";
        ContentStream fileContent = streamContent(docContent, MimetypeMap.MIMETYPE_TEXT_PLAIN);

        // change to createDoc
       ObjectId id = createDocumentInFolder(binding, testUser, thisFileName, DOMAIN, folderRef, properties, VersioningState.MAJOR, fileContent);
        // Create CMIS util to get object from ID.
        Document d1 = (Document) getObject(id.getId());
        assertTrue(streamToString(d1.getContentStream()).equalsIgnoreCase(docContent));

        checkIfFileCreated(drone, thisFileName, folderName);
        assertEquals(d1.getVersionLabel(), "1.0", d1.getVersionLabel());
        assertEquals(d1.getContentStreamLength(), 0, d1.getContentStreamLength());

        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();

        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(thisFileName).render();

        assertEquals(detailsPage.getDocumentSize(), "0 bytes");
        assertEquals(detailsPage.getDocumentVersion(), "1.0");

        VersionDetails versionDetails = detailsPage.getCurrentVersionDetails();

        assertEquals(versionDetails.getVersionNumber(), "1.0", "Verifying version number");
        assertEquals(versionDetails.getFileName(), thisFileName, "Verifying File Name");
        assertEquals(versionDetails.getComment(), "Initial Version", "Verifying version comment");
    }

    static String streamToString(ContentStream stream) throws IOException
    {
        InputStream in = stream.getStream();
        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer, "UTF-8");
        String content = writer.toString();
        return content;
    }

    /**
     * @throws Exception
     * 
     */
    void checkIfFileCreated(WebDrone drone, String thisFileName, String inFolder) throws Exception
    {
        ShareUser.login(drone, testUser);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.navigateToFolder(drone, inFolder);
        assertTrue(docLibPage.isFileVisible(thisFileName), "Filename - '" + thisFileName + "' should be visible in:" + docLibPage.getFiles());
    }

    /**
     * @param thisFileName
     * @throws Exception
     */
    protected void createFromSource(WebDrone drone, String thisFileName) throws Exception
    {
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, thisFileName);
        ObjectId id = createDocumentFromSource(binding, testUser, thisFileName, DOMAIN, folderRef, properties, VersioningState.MINOR, sourceNodeRef);

        // Create CMIS util to get object from ID.
        CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        Document d1 = (Document) cmisSession.getObject(id.getId());
        assertTrue(streamToString(d1.getContentStream()).equalsIgnoreCase(fileNameContent));

        checkIfFileCreated(drone, thisFileName, folderName);
    }

    /**
     * @param objectTypeValue
     * 
     */
    protected void createRelationship(String objectTypeValue)
    {
        
        OperationContext operationContext = new OperationContextImpl(null, false, false, false, IncludeRelationships.BOTH, Collections.singleton("cmis:none"),
                true, null, true, 100);

        CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        
        Document d1 = (Document) cmisSession.getObject(sourceNodeRef);
        Document d2 = (Document) cmisSession.getObject(targetNodeRef);

        createRelationship(binding, testUser, DOMAIN, sourceNodeRef, targetNodeRef, objectTypeValue);

        Document d1AfterRelation = (Document) cmisSession.getObject(sourceNodeRef);
        Document d2AfterRelation = (Document) cmisSession.getObject(targetNodeRef);

        assertTrue(d1.getLastModificationDate().equals(d1AfterRelation.getLastModificationDate()));
        assertTrue(d2.getLastModificationDate().equals(d2AfterRelation.getLastModificationDate()));
        assertTrue(d1.getVersionLabel().equals(d1AfterRelation.getVersionLabel()));
        assertTrue(d2.getVersionLabel().equals(d2AfterRelation.getVersionLabel()));

        List<Relationship> d1Relationships = cmisSession.getCMISSession().getObject(sourceNodeRef, operationContext).getRelationships();
        assertTrue(d1Relationships.get(0).getSource().getId().contains(sourceNodeRef), "Verifying Source: " + d1Relationships.get(0).getSource().getId());
        assertTrue(d1Relationships.get(0).getTarget().getId().contains(targetNodeRef), "Verifying target: " + d1Relationships.get(0).getTarget().getId());

        List<Relationship> d2Relationships = cmisSession.getCMISSession().getObject(targetNodeRef, operationContext).getRelationships();;
        assertTrue(d2Relationships.get(0).getSource().getId().contains(sourceNodeRef), "Verifying Source: " + d2Relationships.get(0).getSource().getId());
        assertTrue(d2Relationships.get(0).getTarget().getId().contains(targetNodeRef), "Verifying target: " + d2Relationships.get(0).getTarget().getId());
    }

    /**
     * @throws NumberFormatException
     */
    protected void deleteAllVersionsTest() throws NumberFormatException
    {
        String docContent = "Lorem Ipsum";
        ContentStream fileContent = streamContent(docContent, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        Document doc1 = (Document) cmisSession.getObject(deleteVersionNodeRef);
        String versionLabel = doc1.getVersionLabel();

        // Update file with new one.
        doc1.setContentStream(fileContent, true);
        Document doc2 = (Document) doc1.getObjectOfLatestVersion(false);
        String versionLabel2 = doc2.getVersionLabel();

        // New version created.
       assertTrue(Double.parseDouble(versionLabel) < Double.parseDouble(versionLabel2));

        doc2.delete(false);
  doc2 = (Document) cmisSession.getObject(deleteVersionNodeRef);
        versionLabel2 = doc2.getVersionLabel();
        assertTrue(versionLabel.equals(versionLabel2));
    }

    /**
     * @throws NumberFormatException
     */
    protected void updateTest() throws NumberFormatException
    {
        String docContent = "Lorem Ipsum";
        ContentStream fileContent = streamContent(docContent, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        Document doc1 = (Document) cmisSession.getObject(deleteVersionNodeRef);
        String versionLabel = doc1.getVersionLabel();

        // Update file with new one.
        doc1.setContentStream(fileContent, true);
        Document doc2 = (Document) doc1.getObjectOfLatestVersion(false);
        String versionLabel2 = doc2.getVersionLabel();

        assertTrue(Double.parseDouble(versionLabel) < Double.parseDouble(versionLabel2));
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = (DocumentDetailsPage) ShareUserSitePage.openDetailsPage(drone, deleteVersionFile);
        assertEquals(detailsPage.getDocumentVersion(), "1.1", detailsPage.getDocumentVersion());

        VersionDetails currentVersion = detailsPage.getCurrentVersionDetails();

        assertEquals(currentVersion.getVersionNumber(), "1.1");
        assertEquals(currentVersion.getFileName(), deleteVersionFile);
        assertTrue(currentVersion.getUserName().getDescription().contains(testUser));
        assertEquals(currentVersion.getComment(), "Set content stream");

        List<VersionDetails> olderVersions = detailsPage.getOlderVersionDetails();

        assertEquals(olderVersions.get(0).getVersionNumber(), "1.0");
        assertEquals(olderVersions.get(0).getFileName(), deleteVersionFile);
        assertTrue(olderVersions.get(0).getUserName().getDescription().contains(testUser));
        assertEquals(olderVersions.get(0).getComment(), "(No Comment)");
    }

    /**
     * 
     */
    protected void queryTest()
    {
        // For Document Step 1 & 2
        CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        List<CMISNode> results = cmisSession.query("SELECT * FROM cmis:document WHERE cmis:objectId = 'workspace://SpacesStore/" + sourceNodeRef + "'", false,
                0, Integer.MAX_VALUE);
        boolean found = false;
        for (CMISNode node : results)
        {
            String name = (String) node.getProperties().get("cmis:name");
            if (fileName.contains(name))
            {
                found = true;
                break;
            }
        }
        assertTrue(found, fileName + " should be found in :" + results);

        // For Folder Step 3 & 4
 results.clear();
        found = false;
        results = cmisSession.query("SELECT * FROM cmis:folder WHERE cmis:objectId = 'workspace://SpacesStore/" + folderRef + "'", false, 0, Integer.MAX_VALUE);
        for (CMISNode node : results)
        {
            String name = (String) node.getProperties().get("cmis:name");
            if (folderName.contains(name))
            {
                found = true;
                break;
            }
        }
        assertTrue(found, folderName + " should be found in :" + results);
    }

    /**
     * @param thisFolderName
     * @throws Exception
     */
    protected void createFolderTest(WebDrone drone, String thisFolderName) throws Exception
    {
        // Step 1
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, thisFolderName);

        // Step 2
        createFolder(binding, testUser, testUser, DOMAIN, siteName, properties);

        checkIfFileCreated(drone, thisFolderName, DOCLIB);
    }

    /**
     * 
     * @param drone
     * @param thisFileName
     * @param thisFolderName
     * @throws Exception
     */
    protected void deleteTest(WebDrone drone, String thisFileName, String thisFolderName) throws Exception
    {
        // ShareUser.login(drone, testUser);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.createFolderInFolder(drone, thisFolderName, thisFolderName, DOCLIB);
        DocumentLibraryPage docLibPage = ShareUser.uploadFileInFolder(drone, new String[] { thisFileName });

        delete(binding, testUser, DOMAIN, ShareUser.getGuid(drone, thisFolderName));
        delete(binding, testUser, DOMAIN, ShareUser.getGuid(drone, thisFileName));

        docLibPage = ShareUser.openDocumentLibrary(drone);
        assertFalse(docLibPage.isFileVisible(thisFileName));
        assertFalse(docLibPage.isFileVisible(thisFolderName));
    }

    /**
     * @param thisFolderName
     * @param thisSubFolderName1
     * @param thisSubFolderName2
     * @throws Exception
     */
    protected void deleteTreeTest(WebDrone drone, String thisFolderName, String thisSubFolderName1, String thisSubFolderName2) throws Exception
    {
        // Preconditions
        ShareUser.login(drone, testUser);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.createFolderInFolder(drone, thisFolderName, thisFolderName, DOCLIB);
        ShareUser.createFolderInFolder(drone, thisSubFolderName1, thisSubFolderName1, thisFolderName);
        ShareUser.createFolderInFolder(drone, thisSubFolderName2, thisSubFolderName2, thisFolderName);
        ShareUser.openDocumentLibrary(drone);

        // Step 1 & 2
        deleteTree(binding, testUser, DOMAIN, ShareUser.getGuid(drone, thisFolderName), true, UnfileObject.DELETE, true);

        // Step 3
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);
        assertFalse(docLibPage.isFileVisible(thisFolderName));
    }

    /**
     * @throws NumberFormatException
     */
    protected void setContentTest(WebDrone drone) throws NumberFormatException
    {
        String docContent = "Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum";
        ContentStream fileContent = streamContent(docContent, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        Document doc1 = (Document) cmisSession.getObject(sourceNodeRef);

        String versionBefore = doc1.getVersionLabel();
        GregorianCalendar modifiedDate = doc1.getLastModificationDate();
        long sizeBefore = doc1.getContentStreamLength();

        // Update file with new one.
        doc1.setContentStream(fileContent, true);
        Document doc2 = (Document) doc1.getObjectOfLatestVersion(false);
        String versionAfter = doc2.getVersionLabel();
        GregorianCalendar modifiedDateAfter = doc2.getLastModificationDate();

        assertTrue(Double.parseDouble(versionBefore) < Double.parseDouble(versionAfter));
        assertTrue(modifiedDateAfter.after(modifiedDate), modifiedDateAfter + " should be after " + modifiedDate);
        assertTrue(sizeBefore < doc2.getContentStreamLength());

        ShareUser.login(drone, testUser);
   ShareUser.openSitesDocumentLibrary(drone, siteName);
        FileDirectoryInfo fileRow = ShareUserSitePage.getFileDirectoryInfo(drone, fileName);
        assertTrue(versionAfter.equalsIgnoreCase(fileRow.getVersionInfo()));
        assertTrue(fileRow.getModifier().contains(testUser));

        // On doc details page.
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = (DocumentDetailsPage) ShareUserSitePage.openDetailsPage(drone, doc1.getName());
        assertEquals(detailsPage.getDocumentVersion(), versionAfter, detailsPage.getDocumentVersion());
        assertTrue(detailsPage.getCommentsOfLastCommit().contains("Set content stream"), detailsPage.getCommentsOfLastCommit());
    }

    /**
     * 
     * @param nodeRef
     * @return
     */
    CmisObject getObject(String nodeRef)
    {
        return getObject(binding, testUser, DOMAIN, nodeRef);
    }

    /**
     * @param thisFileName
     * @throws Exception
     */
    protected void moveTest(WebDrone drone, String thisFileName) throws Exception
    {
        CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        ObjectId targetId = cmisSession.getObject(folderRef);
        ObjectId sourceId = cmisSession.getObject(sourceFolderRef);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentLibraryPage docLibPage = ShareUser.uploadFileInFolder(drone, new String[] { thisFileName, sourceFolderName });
        String docNodeRef = ShareUser.getGuid(drone, thisFileName);
        Document docId = (Document) cmisSession.getObject(docNodeRef);
        docId.move(sourceId, targetId);
        docLibPage = ShareUserSitePage.navigateToFolder(drone, folderName);
        assertTrue(docLibPage.isFileVisible(thisFileName));
        docLibPage = ShareUserSitePage.navigateToFolder(drone, sourceFolderName);
        assertFalse(docLibPage.isFileVisible(thisFileName));

    }

    protected void appendTest(WebDrone drone) throws IOException
    {
        ShareUser.openDocumentLibrary(drone);
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        Date modifiedDateBefore = ShareUserSitePage.getModifiedDate(drone);

        String expectedVersion = documentDetailsPage.getDocumentVersion();
        String contentToAppend = "New content appended";
        Document doc1 = (Document) getObject(sourceNodeRef);
        long documentSize = doc1.getContentStreamLength();
        appendContent(binding, testUser, DOMAIN, sourceNodeRef, contentToAppend, true);
        doc1 = (Document) getObject(sourceNodeRef);
        assertTrue(streamToString(doc1.getContentStream()).contains(contentToAppend));

        ShareUser.openDocumentLibrary(drone);
        FileDirectoryInfo fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName);
        assertTrue(fileDirectoryInfo.getModifier().contains(testUser), fileDirectoryInfo.getModifier());
        documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
        assertTrue(Double.parseDouble(documentDetailsPage.getDocumentVersion()) > Double.parseDouble(expectedVersion));
        assertTrue(doc1.getContentStreamLength() > documentSize);

        Date modifiedDateAfter = ShareUserSitePage.getModifiedDate(drone);

        assertTrue(modifiedDateAfter.after(modifiedDateBefore), "modifiedDateAfter: " + modifiedDateAfter + ", modifiedDateBefore" + modifiedDateBefore);

        assertTrue(Double.parseDouble(documentDetailsPage.getDocumentVersion()) > Double.parseDouble(expectedVersion), documentDetailsPage.getDocumentVersion());
        assertTrue(documentDetailsPage.getCommentsOfLastCommit().contains("Appended content stream"), documentDetailsPage.getDocumentVersion());
        assertTrue(documentDetailsPage.isModifiedByDetailsPresent());
    }

    /**
     * Removes " bytes" and returns number.
     * 
     * @param documentSize
     * @return
     */
  private double getNumericalSize(String documentSize)
    {
        return Double.parseDouble(StringUtils.substringBefore(documentSize, " bytes"));
    }

    protected void deleteContentTest(WebDrone drone) throws IOException, ParseException
    {
        ShareUser.openDocumentLibrary(drone);
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
        String expectedVersion = documentDetailsPage.getDocumentVersion();

        deleteContent(binding, testUser, DOMAIN, sourceNodeRef);

        Document doc1 = (Document) getObject(sourceNodeRef);
        assertNull(doc1.getContentStream());

        ShareUser.openDocumentLibrary(drone);
        documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
        String documentSize = documentDetailsPage.getDocumentSize();
        assertTrue(Double.parseDouble(documentDetailsPage.getDocumentVersion()) > Double.parseDouble(expectedVersion));

        Date createdDate = simpleDateFormat.parse((String) documentDetailsPage.getProperties().get("CreatedDate"));
        Date modifiedDate = simpleDateFormat.parse((String) documentDetailsPage.getProperties().get("ModifiedDate"));
        assertTrue(documentSize.contains("(None)"));
        assertTrue(modifiedDate.after(createdDate), "Created date :" + createdDate + " modified date: " + modifiedDate);
        assertEquals(documentDetailsPage.getProperties().get("Modifier"), testUser);
    }

    /**
     * 
     * @param thisFileName
     * @param thisFolderName
     */
    protected void addObjectToFolderTest(WebDrone drone, String thisFileName, String thisFolderName) throws Exception
    {
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.createFolderInFolder(drone, thisFolderName, thisFolderName, DOCLIB);
        String parentFolderRef = ShareUser.getGuid(drone, thisFolderName);
        ShareUser.uploadFileInFolder(drone, new String[] { thisFileName });
        String docNodeRef = ShareUser.getGuid(drone, thisFileName);
        Document doc1 = (Document) getObject(docNodeRef);
        doc1.addToFolder(getObject(parentFolderRef), true);

        DocumentLibraryPage documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, thisFolderName);
        assertTrue(documentLibraryPage.isFileVisible(thisFileName));
        assertEquals(documentLibraryPage.getFileDirectoryInfo(thisFileName).getNodeRef(), "workspace://SpacesStore/" + docNodeRef, "Verifying NodeRef");
        documentLibraryPage = ShareUser.openDocumentLibrary(drone);
        assertTrue(documentLibraryPage.isFileVisible(thisFileName));
        assertEquals(documentLibraryPage.getFileDirectoryInfo(thisFileName).getNodeRef(), "workspace://SpacesStore/" + docNodeRef, "Verifying NodeRef");

        documentLibraryPage = ShareUserSitePage.editContentNameInline(drone, thisFileName, thisFileName + "_new", true);
        assertTrue(documentLibraryPage.isFileVisible(thisFileName + "_new"));
        documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, thisFolderName);
        assertTrue(documentLibraryPage.isFileVisible(thisFileName + "_new"));
    }

    /**
     * 
     * @param thisFileName
     * @param thisFolderName
     */
    protected void removeObjectFromFolderTest(WebDrone drone, String thisFileName, String thisFolderName) throws Exception
    {
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.createFolderInFolder(drone, thisFolderName, thisFolderName, DOCLIB);
        String parentFolderRef = ShareUser.getGuid(drone, thisFolderName);
        DocumentLibraryPage docLibPage;
        ShareUser.uploadFileInFolder(drone, new String[] { thisFileName });
        String docNodeRef = ShareUser.getGuid(drone, thisFileName);

        Document doc1 = (Document) getObject(docNodeRef);
        doc1.addToFolder(getObject(parentFolderRef), true);

        DocumentLibraryPage documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, thisFolderName);
        assertTrue(documentLibraryPage.isFileVisible(thisFileName));

        doc1.removeFromFolder(getObject(parentFolderRef));

        docLibPage = ShareUserSitePage.navigateToFolder(drone, thisFolderName);
        assertFalse(docLibPage.isFileVisible(thisFileName));

        docLibPage = docLibPage.getSiteNav().selectSiteDocumentLibrary().renderItem(maxWaitTime, thisFileName);
        assertTrue(docLibPage.isFileVisible(thisFileName));
    }

    /**
     * 
     * @param thisFileName
     * 
     */
    protected void checkOutTest(WebDrone drone, String thisFileName) throws Exception
    {
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.uploadFileInFolder(drone, new String[] { thisFileName });
        String docNodeRef = ShareUser.getGuid(drone, thisFileName);

        Document doc = (Document) getObject(docNodeRef);
        oldVersionLabel = doc.getVersionLabel();
        oldDocSize = doc.getContentStreamLength();
        doc.checkOut();

        ShareUser.openDocumentLibrary(drone);
        FileDirectoryInfo thisRow = ShareUserSitePage.getFileDirectoryInfo(drone, thisFileName);
        assertEquals(thisRow.getContentInfo(), "This document is locked by you for offline editing.", "File " + thisFileName + " isn't locked");
    }

    /**
     * 
     * @param thisFileName
     * @param major
     * @param withContentStream
     * @throws Exception
     */
    protected void checkInTest(WebDrone drone, String thisFileName, boolean major, boolean withContentStream) throws Exception
    {
        checkOutTest(drone, thisFileName);
        String docNodeRef = ShareUser.getGuid(drone, thisFileName);
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, thisFileName);

        Document checkinDoc = (Document) getObject(docNodeRef);
        ContentStream contentStream = null;
        if (withContentStream)
        {
            contentStream = streamContent("New content.", MimetypeMap.MIMETYPE_TEXT_PLAIN);
        }
        checkinDoc.checkIn(major, null, contentStream, "Check in w/o content stream.");

        ShareUser.openDocumentLibrary(drone);
        FileDirectoryInfo thisRow = ShareUserSitePage.getFileDirectoryInfo(drone, thisFileName);
        assertTrue(Double.parseDouble(thisRow.getVersionInfo()) > Double.parseDouble(oldVersionLabel), thisRow.getVersionInfo() + " should be greater than "
                + oldVersionLabel);
        if (major)
        {
            assertEquals(thisRow.getVersionInfo(), "2.0");
        }
        else
        {
            assertEquals(thisRow.getVersionInfo(), "1.1");
        }

        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, thisFileName);
        documentDetailsPage.getDocumentSize();
        Map<String, Object> props = documentDetailsPage.getProperties();

        Date createdDate = simpleDateFormat.parse((String) props.get("CreatedDate"));
        Date modifiedDate = simpleDateFormat.parse((String) props.get("ModifiedDate"));
        double size = getNumericalSize((String) props.get("Size"));
        if (withContentStream)
        {
            assertTrue(size != oldDocSize);
        }
        else
        {
            assertTrue(size == oldDocSize);
        }
        assertEquals(props.get("Modifier"), testUser);
        assertFalse(createdDate.after(modifiedDate), "Created date :" + createdDate + " cannot be after modified date: " + modifiedDate);
    }

    /**
     * 
     * @param thisFileName
     * @throws Exception
     */
    protected void cancelCheckOutTest(WebDrone drone, String thisFileName) throws Exception
    {
        checkOutTest(drone, thisFileName);
        String docNodeRef = ShareUser.getGuid(drone, thisFileName);

        Document oldDoc = (Document) getObject(docNodeRef);
        oldDoc.cancelCheckOut();

        ShareUser.openDocumentLibrary(drone);
        FileDirectoryInfo thisRow = ShareUserSitePage.getFileDirectoryInfo(drone, thisFileName);
        assertTrue(Double.parseDouble(thisRow.getVersionInfo()) == Double.parseDouble(oldVersionLabel), thisRow.getVersionInfo() + " should be equal to "
                + oldVersionLabel);
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, thisFileName);
        documentDetailsPage.getDocumentSize();
        Map<String, Object> props = documentDetailsPage.getProperties();

        Date createdDate = simpleDateFormat.parse((String) props.get("CreatedDate"));
        Date modifiedDate = simpleDateFormat.parse((String) props.get("ModifiedDate"));
        double size = getNumericalSize((String) props.get("Size"));

        assertTrue(size == oldDocSize);

        assertEquals(props.get("Modifier"), testUser);
        assertFalse(modifiedDate.after(createdDate), "Created date :" + createdDate + " cannot be after modified date: " + modifiedDate);
    }

    /**
     * 
     * @param thisFileName
     * @throws Exception
     */
    protected void applyACLTest(WebDrone drone, String thisFileName, String thisFolderName) throws Exception
    {
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.uploadFileInFolder(drone, new String[] { thisFileName });

        String docNodeRef = ShareUser.getGuid(drone, thisFileName);
        Document doc = (Document) getObject(docNodeRef);

        Principal principalData = new AccessControlPrincipalDataImpl("GROUP_EVERYONE");
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add("{http://www.alfresco.org/model/content/1.0}cmobject.Coordinator");
        AccessControlEntryImpl accessControlEntry = new AccessControlEntryImpl(principalData, permissions);
        List<Ace> aces = new ArrayList<Ace>();
        aces.add(accessControlEntry);
        doc.applyAcl(aces, null, AclPropagation.OBJECTONLY);

        ShareUser.login(drone, otherTestUser);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
        ManagePermissionsPage managePermissionsPage = ShareUserSitePage.manageContentPermissions(drone, thisFileName);
        assertEquals(managePermissionsPage.getExistingPermission("EVERYONE"), UserRole.COORDINATOR);
        managePermissionsPage.selectCancel();
        doc.applyAcl(null, aces, AclPropagation.OBJECTONLY);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
        FileDirectoryInfo fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(drone, thisFileName);
        assertFalse(fileDirectoryInfo.isEditOfflineLinkPresent(), "For consumer edit offline link should not be visible.");

        ShareUser.createFolderInFolder(drone, thisFolderName, thisFolderName, DOCLIB);
        String folderNodeRef = ShareUser.getGuid(drone, thisFolderName);
        Folder folder = (Folder) getObject(folderNodeRef);

        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
        managePermissionsPage = ShareUserSitePage.manageContentPermissions(drone, thisFolderName);
        assertEquals(managePermissionsPage.getExistingPermission("EVERYONE"), UserRole.COORDINATOR);
        managePermissionsPage.selectCancel();
        folder.applyAcl(null, aces, AclPropagation.OBJECTONLY);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(drone, thisFolderName);
        assertFalse(fileDirectoryInfo.isEditOfflineLinkPresent(), "For consumer edit offline link should not be visible.");
    }


    public void cmisItemTypeShouldNotBeQueryable() 
    {
        CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        // This throws a CmisInvalidArgumentException
        cmisSession.query("SELECT * FROM cmis:item", false, 0, Integer.MAX_VALUE);
    }
    
    public void cmPersonShouldFindPeople() 
    {
        CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        List<CMISNode> results = cmisSession.query("SELECT * FROM cm:person", false, 0, Integer.MAX_VALUE);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        CMISNode node = results.get(0);
        assertNotNull(node);
        Serializable homeFolder = node.getProperty("cm:homeFolder");
        assertNotNull(homeFolder);
        assertNotEquals("null", String.valueOf(homeFolder));
        assertNotEquals("", String.valueOf(homeFolder));
    }

    public void cmPersonWithWhereClauseShouldFindPerson() 
    {
        CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        List<CMISNode> results = cmisSession.query("SELECT * FROM cm:person where cm:userName like '%ee%'", 
                false, 0, Integer.MAX_VALUE);
        assertNotNull(results);
        assertFalse(results.isEmpty());
    
        // Should find engineering on the Cloud version, or abeecher in the Enterprise edition
        CMISNode node = results.get(0);
        assertNotNull(node);
        Serializable username = node.getProperty("cm:userName");
        assertNotNull(username);
        assertTrue(String.valueOf(username).contains("ee"));
    }
    
    public void cmPersonCanBeUpdatedBySelf()
    {
        updatePerson("cm:userName = '" + testUser + "'");
    }

    private void updatePerson(String whereClause)
    {
        CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        List<CMISNode> results = cmisSession.query("SELECT * FROM cm:person where " + whereClause, 
                false, 0, Integer.MAX_VALUE);
        CMISNode node = results.get(0);
        String objectId = String.valueOf(node.getProperty("cmis:objectId"));
        Serializable location = node.getProperty("cm:location");
        Map<String, String> properties = new HashMap<>();
        String expectedLocation = location + " a change";
        properties.put("cm:location", expectedLocation);
        CmisObject person = cmisSession.getObject(objectId);
        person.updateProperties(properties, true);
        String newLocation = person.getPropertyValue("cm:location");
        assertEquals(newLocation, expectedLocation);
    }
    
    public void cmPersonCannotBeUpdatedByUnauthorizedUser()
    {
        updatePerson("cm:userName like '%ee%'");
    }
    
    public void cmPersonCannotBeDeletedByUnauthorizedUser()
    {
        deleteUser(testUser);
    }
    
    public void cmPersonCannotBeDeletedByAuthorizedUserViaCmis()
    {
        deleteUser(ADMIN_USERNAME);
    }

    private void deleteUser(String userName)
    {
        CmisSession cmisSession = getCmisSession(binding, userName, DOMAIN);
        List<CMISNode> results = cmisSession.query("SELECT * FROM cm:person where cm:userName = '" + deletableTestUser + "'", 
                false, 0, Integer.MAX_VALUE);
        CMISNode node = results.get(0);
        String objectId = String.valueOf(node.getProperty("cmis:objectId"));
        CmisObject person = cmisSession.getObject(objectId);
        person.delete();
        CmisObject personAfterDeletion = cmisSession.getObject(objectId);
        assertNull(personAfterDeletion);
    }
}
