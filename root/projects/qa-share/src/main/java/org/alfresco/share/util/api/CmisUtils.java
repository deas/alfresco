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
package org.alfresco.share.util.api;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.opencmis.CMISDispatcherRegistry.Binding;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.filestore.FileContentWriter;
import org.alfresco.rest.api.tests.client.PublicApiClient.CmisSession;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.util.GUID;
import org.alfresco.util.TempFileProvider;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.SecondaryType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Cmis Utils
 * 
 * @author Meenal Bhave
 */
public class CmisUtils extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(CmisUtils.class);

    /**
     * Creates a CMIS session using the publicApiClient
     * 
     * @param {@link CMISBindin} CMIS binding and Version
     * @param authUser
     * @param domain
     */
    public CmisSession getCmisSession(CMISBinding cmisBinding, String authUser, String domain)
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        CmisSession cmisSession = null;

        switch (cmisBinding)
        {
        case ATOMPUB10:
            cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.0");
            logger.info("Binding: atom 1.0");
            break;
        case ATOMPUB11:
            cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.1");
            logger.info("Binding: atom 1.1");
            break;
        case BROWSER11:
            cmisSession = publicApiClient.createPublicApiCMISSession(Binding.browser, "1.1");
            logger.info("Binding: browser 1.1");
            break;
        default:
            // cmisSession =
            // publicApiClient.createPublicApiCMISSession(Binding.atom, "1.0");
            logger.info("Binding: Not specified");
        }

        return cmisSession;
    }

    /**
     * Creates a folder using CMIS
     * 
     * @param cmisBinding
     *            {@link CMISBinding} CMIS binding and Version
     * @param authUser
     * @param forUser
     * @param domain
     * @param parentFolderPath
     * @param properties
     */
    public Folder createFolder(CMISBinding cmisBinding, String authUser, String forUser, String domain, String parentFolderPath, Map<String, String> properties)
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);
        Session session = cmisSession.getCMISSession();
        Folder rootFolder = session.getRootFolder();

        logger.info("Root folder Id: " + rootFolder.getId());
        logger.info("Root folder Type: " + rootFolder.getType());

        System.out.println("Created By: " + rootFolder.getName());

        Folder documentLibrary = (Folder) cmisSession.getObjectByPath("/Sites/" + parentFolderPath + "/documentLibrary");
        Folder folder = documentLibrary.createFolder(properties);
        // Folder subF = folder.createFolder(properties);

        return folder;
    }

    /**
     * Creates a Document using CMIS
     * 
     * @param cmisBinding
     *            {@link CMISBinding} CMIS binding and Version
     * @param authUser
     * @param forUser
     * @param domain
     * @param parentFolderPath
     * @param docContent
     * @param properties
     * @throws IOException
     */
    public Document createDocument(CMISBinding cmisBinding, String authUser, String forUser, String domain, String parentFolderPath, String docContent,
            Map<String, String> properties) throws IOException
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);
        Session session = cmisSession.getCMISSession();

        Folder rootFolder = session.getRootFolder();

        System.out.println("Created By: " + rootFolder.getName());

        Folder documentLibrary = (Folder) session.getObjectByPath("/Sites/" + parentFolderPath + "/documentLibrary");

        // Document
        ContentStream fileContent = streamContent(docContent, MimetypeMap.MIMETYPE_TEXT_PLAIN);

        Document d = documentLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);

        ContentData content = cmisSession.getContent(d.getId());
        logger.info(docContent.getBytes().toString());
        logger.info(content.getBytes().toString());

        return d;
    }

    /**
     * Creates a Document using CMIS from folder node ref
     * 
     * @param cmisBinding
     *            {@link CMISBinding} CMIS binding and Version
     * @param authUser
     * @param fileName
     * @param domain
     * @param folderNodeRef
     * @param properties
     * @param versioningState
     * @param contentStream
     * @return
     * @throws IOException
     */
    public Document createDocumentInFolder(CMISBinding cmisBinding, String authUser, String fileName, String domain, String folderNodeRef,
            Map<String, Serializable> properties, VersioningState versioningState, ContentStream contentStream) throws IOException
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);

        ContentStreamImpl fileContent;
        if (contentStream == null)
        {
            fileContent = new ContentStreamImpl();
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }
        else
        {
            fileContent = (ContentStreamImpl) contentStream;
        }
        logger.info("Created By: " + folderNodeRef + " with properties - " + properties);
        Document d = cmisSession.createDocument(folderNodeRef, fileName, properties, contentStream, versioningState);

        return d;
    }

    /**
     * Creates a Document using CMIS from source.
     * 
     * @param cmisBinding
     *            {@link CMISBinding} CMIS binding and Version
     * @param authUser
     * @param fileName
     * @param domain
     * @param folderNodeRef
     * @param properties
     * @param versioningState
     * @param sourceNodeRef
     * @return
     * @throws IOException
     */
    public Document createDocumentFromSource(CMISBinding cmisBinding, String authUser, String fileName, String domain, String folderNodeRef,
            Map<String, Serializable> properties, VersioningState versioningState, String sourceNodeRef) throws IOException
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);
        logger.info("Created In: " + folderNodeRef);
        Session session = cmisSession.getCMISSession();
        logger.info("Created By: " + folderNodeRef + " with properties - " + properties);
        ObjectId objectId = session.createDocumentFromSource(cmisSession.getObject(sourceNodeRef), properties, cmisSession.getObject(folderNodeRef),
                versioningState);
        Document doc = (Document) cmisSession.getObject(objectId.getId());
        return doc;
    }

    /**
     * Creates a Document using CMIS
     * 
     * @param cmisBinding
     *            {@link CMISBinding} CMIS binding and Version
     * @param authUser
     * @param forUser
     * @param domain
     * @param docToCheckIn
     * @param docContent
     * @param properties
     * @param majorVersion
     * @param checkInComment
     * @return
     * @throws IOException
     */
    public Document checkInDocument(CMISBinding cmisBinding, String authUser, String forUser, String domain, Document docToCheckIn, String docContent,
            Map<String, String> properties, Boolean majorVersion, String checkInComment) throws IOException
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);
        Session session = cmisSession.getCMISSession();

        ObjectId d2WorkingCopy = docToCheckIn.checkOut();
        Document pwc = (Document) cmisSession.getObject(d2WorkingCopy.getId());

        pwc.checkIn(majorVersion, properties, streamContent(docContent, MimetypeMap.MIMETYPE_TEXT_PLAIN), checkInComment);

        return (Document) session.getObject(docToCheckIn.getId());
    }

    /**
     * @param cmisBinding
     * @param authUser
     * @param domain
     * @param folderRef
     * @param allVersions
     * @param unfile
     * @param continueOnFailure
     * @return
     */
    public boolean deleteTree(CMISBinding cmisBinding, String authUser, String domain, String folderRef, boolean allVersions, UnfileObject unfile,
            boolean continueOnFailure)
    {
        boolean deleted = false;

        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);
        cmisSession.removeTree(folderRef, allVersions, unfile, continueOnFailure);
        return deleted;
    }

    /**
     * @param cmisBinding
     * @param authUser
     * @param domain
     * @param nodeRef
     */
    public void delete(CMISBinding cmisBinding, String authUser, String domain, String nodeRef)
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);
        cmisSession.getObject(nodeRef).delete();
    }

    /**
     * @param cmisBinding
     * @param authUser
     * @param domain
     * @param sourceObjectId
     * @param targetObjectId
     * @param objectTypeValue
     * @return
     */
    public ObjectId createRelationship(CMISBinding cmisBinding, String authUser, String domain, String sourceObjectId, String targetObjectId,
            String objectTypeValue)
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);

        Map<String, Serializable> relProps = new HashMap<String, Serializable>();
        relProps.put("cmis:sourceId", "workspace://SpacesStore/" + sourceObjectId);
        relProps.put("cmis:targetId", "workspace://SpacesStore/" + targetObjectId);
        relProps.put(PropertyIds.NAME, "testRelationship");
        relProps.put("cmis:objectTypeId", objectTypeValue);
        logger.info("Creating relationship for properties:" + relProps);
        ObjectId result = cmisSession.getCMISSession().createRelationship(relProps);
        return result;
    }

    /**
     * @param cmisBinding
     * @param authUser
     * @param domain
     * @param fileNodeRef
     * @param content
     * @return
     */
    public Document setContent(CMISBinding cmisBinding, String authUser, String domain, String fileNodeRef, String content)
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);
        ContentStream fileContent = streamContent(content, MimetypeMap.MIMETYPE_TEXT_PLAIN);

        Document doc1 = (Document) cmisSession.getObject(fileNodeRef);
        doc1 = doc1.setContentStream(fileContent, true);
        return doc1;
    }

    /**
     * @param cmisBinding
     * @param authUser
     * @param domain
     * @param fileNodeRef
     * @param content
     * @param isLastChunk
     * @return
     */
    public Document appendContent(CMISBinding cmisBinding, String authUser, String domain, String fileNodeRef, String content, boolean isLastChunk)
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);
        ContentStream fileContent = streamContent(content, MimetypeMap.MIMETYPE_TEXT_PLAIN);

        Document doc1 = (Document) cmisSession.getObject(fileNodeRef);
        doc1 = doc1.appendContentStream(fileContent, isLastChunk);
        return doc1;
    }

    /**
     * @param docContent
     * @param mimeType
     * @return
     */
    protected ContentStream streamContent(String docContent, String mimeType)
    {
        ContentStreamImpl fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile("Temp" + System.currentTimeMillis(), ".txt"));
            writer.putContent(docContent);
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }

        return fileContent;
    }

    /**
     * Method to add aspect
     * 
     * @param cmisBinding
     * @param userName
     * @param domain
     * @param documentNodeRef
     * @param documentAspects
     */
    public void addAspect(CMISBinding cmisBinding, String userName, String domain, String documentNodeRef, List<DocumentAspect> documentAspects)
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, userName, domain);

        CmisObject content = cmisSession.getObject(documentNodeRef);

        List<SecondaryType> secondaryTypesList = content.getSecondaryTypes();
        List<String> secondaryTypes = new ArrayList<String>();

        for (SecondaryType secondaryType : secondaryTypesList)
        {
            secondaryTypes.add(secondaryType.getId());
        }
        for (DocumentAspect aspect : documentAspects)
        {
            secondaryTypes.add(aspect.getProperty());
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        {
            properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondaryTypes);
        }
        content.updateProperties(properties);
    }

    /**
     * Method to add aspect
     * 
     * @param cmisBinding
     * @param userName
     * @param domain
     * @param documentNodeRef
     * @param documentAspects
     */
    public void removeAspect(CMISBinding cmisBinding, String userName, String domain, String documentNodeRef, List<DocumentAspect> documentAspects)
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, userName, domain);

        CmisObject content = cmisSession.getObject(documentNodeRef);

        List<SecondaryType> secondaryTypesList = content.getSecondaryTypes();
        List<String> secondaryTypes = new ArrayList<String>();

        for (SecondaryType secondaryType : secondaryTypesList)
        {
            secondaryTypes.add(secondaryType.getId());
        }
        for (DocumentAspect aspect : documentAspects)
        {
            secondaryTypes.remove(aspect.getProperty());
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        {
            properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondaryTypes);
        }
        content.updateProperties(properties);
    }

    /**
     * Method to add properties
     * 
     * @param cmisBinding
     * @param userName
     * @param domain
     * @param documentNodeRef
     * @param propertiesMap
     */
    public void addProperties(CMISBinding cmisBinding, String userName, String domain, String documentNodeRef, Map<String, Object> propertiesMap)
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, userName, domain);

        CmisObject content = cmisSession.getObject(documentNodeRef);

        content.updateProperties(propertiesMap);
    }

    /**
     * Method to get Folder Properties
     * 
     * @param cmisBinding
     * @param userName
     * @param domain
     * @param folderNodeRef
     */
    public List<Property<?>> getFolderProperties(CMISBinding cmisBinding, String userName, String domain, String folderNodeRef)
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, userName, domain);

        Folder folder = (Folder) cmisSession.getObject(folderNodeRef);

        return folder.getProperties();
    }

    /**
     * @param cmisBinding
     * @param userName
     * @param domain
     * @param nodeRef
     * @return
     */
    protected CmisObject getObject(CMISBinding cmisBinding, String userName, String domain, String nodeRef)
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, userName, domain);
        return cmisSession.getObject(nodeRef);
    }

    /**
     * Method to get Content Node ref.
     * 
     * @param cmisBinding
     * @param authUser
     * @param domain
     * @param siteName
     * @param parentForlderPath
     * @param ContentName
     * @return
     */
    public String getNodeRef(CMISBinding cmisBinding, String authUser, String domain, String siteName, String parentForlderPath, String ContentName)
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);
        Session session = cmisSession.getCMISSession();
        Folder rootFolder = session.getRootFolder();

        System.out.println("Created By: " + rootFolder.getName());

        Folder documentLibrary = (Folder) cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary/" + parentForlderPath);
        ItemIterable<CmisObject> allContent = documentLibrary.getChildren();
        for (CmisObject content : allContent)
        {
            if (content.getName().equalsIgnoreCase(ContentName))
            {
                String nodeRef = "workspace://SpacesStore/" + content.getId().split(";")[0];
                logger.info("Node Ref: " + nodeRef);
                return nodeRef;
            }
        }
        throw new UnsupportedOperationException("Unable to find the document node ref");
    }

    /**
     * @param propertyList
     * @param propertyName
     * @return
     */
    public Object getPropertyValue(List<Property<?>> propertyList, String propertyName)
    {
        String value = null;
        for (Property property : propertyList)
        {
            if (property.getDefinition().getLocalName().equals(propertyName))
            {
                logger.info("Property LocalName: " + property.getDefinition().getLocalName());
                logger.info("Property Value: " + property.getValueAsString());
                value = property.getValueAsString();
                break;
            }
        }
        return value;
    }

    /**
     * @param cmisBinding
     * @param authUser
     * @param domain
     * @param nodeRef
     */
    public void deleteContent(CMISBinding cmisBinding, String authUser, String domain, String nodeRef)
    {
        CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);
        cmisSession.deleteContent(nodeRef, true);
    }

}
