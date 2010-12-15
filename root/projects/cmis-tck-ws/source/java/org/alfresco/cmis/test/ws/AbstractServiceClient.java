/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.cmis.test.ws;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.alfresco.repo.cmis.ws.CancelCheckOut;
import org.alfresco.repo.cmis.ws.CheckIn;
import org.alfresco.repo.cmis.ws.CheckInResponse;
import org.alfresco.repo.cmis.ws.CheckOut;
import org.alfresco.repo.cmis.ws.CheckOutResponse;
import org.alfresco.repo.cmis.ws.CmisAccessControlListType;
import org.alfresco.repo.cmis.ws.CmisContentStreamType;
import org.alfresco.repo.cmis.ws.CmisFaultType;
import org.alfresco.repo.cmis.ws.CmisObjectInFolderType;
import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.CmisProperty;
import org.alfresco.repo.cmis.ws.CmisPropertyBoolean;
import org.alfresco.repo.cmis.ws.CmisPropertyId;
import org.alfresco.repo.cmis.ws.CmisPropertyString;
import org.alfresco.repo.cmis.ws.CmisRenditionType;
import org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType;
import org.alfresco.repo.cmis.ws.CmisRepositoryEntryType;
import org.alfresco.repo.cmis.ws.CmisRepositoryInfoType;
import org.alfresco.repo.cmis.ws.CmisTypeContainer;
import org.alfresco.repo.cmis.ws.CmisTypeDefinitionType;
import org.alfresco.repo.cmis.ws.CmisTypeDocumentDefinitionType;
import org.alfresco.repo.cmis.ws.CmisTypeFolderDefinitionType;
import org.alfresco.repo.cmis.ws.CmisTypePolicyDefinitionType;
import org.alfresco.repo.cmis.ws.CmisTypeRelationshipDefinitionType;
import org.alfresco.repo.cmis.ws.CreateDocument;
import org.alfresco.repo.cmis.ws.CreateDocumentResponse;
import org.alfresco.repo.cmis.ws.CreateFolder;
import org.alfresco.repo.cmis.ws.CreateFolderResponse;
import org.alfresco.repo.cmis.ws.CreatePolicy;
import org.alfresco.repo.cmis.ws.CreatePolicyResponse;
import org.alfresco.repo.cmis.ws.CreateRelationship;
import org.alfresco.repo.cmis.ws.CreateRelationshipResponse;
import org.alfresco.repo.cmis.ws.DeleteObject;
import org.alfresco.repo.cmis.ws.DeleteTree;
import org.alfresco.repo.cmis.ws.DeleteTreeResponse;
import org.alfresco.repo.cmis.ws.EnumACLPropagation;
import org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds;
import org.alfresco.repo.cmis.ws.EnumContentStreamAllowed;
import org.alfresco.repo.cmis.ws.EnumIncludeRelationships;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.EnumUnfileObject;
import org.alfresco.repo.cmis.ws.EnumVersioningState;
import org.alfresco.repo.cmis.ws.GetAllVersions;
import org.alfresco.repo.cmis.ws.GetChildren;
import org.alfresco.repo.cmis.ws.GetChildrenResponse;
import org.alfresco.repo.cmis.ws.GetProperties;
import org.alfresco.repo.cmis.ws.GetPropertiesResponse;
import org.alfresco.repo.cmis.ws.GetRenditions;
import org.alfresco.repo.cmis.ws.GetRepositories;
import org.alfresco.repo.cmis.ws.GetRepositoryInfo;
import org.alfresco.repo.cmis.ws.GetRepositoryInfoResponse;
import org.alfresco.repo.cmis.ws.GetTypeChildren;
import org.alfresco.repo.cmis.ws.GetTypeChildrenResponse;
import org.alfresco.repo.cmis.ws.GetTypeDefinition;
import org.alfresco.repo.cmis.ws.GetTypeDefinitionResponse;
import org.alfresco.repo.cmis.ws.GetTypeDescendants;
import org.alfresco.repo.cmis.ws.ObjectServicePortBindingStub;
import org.alfresco.repo.cmis.ws.UpdateProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Base class for all services clients, provides authentication to clients
 * 
 * @author Mike Shavnev
 */
public abstract class AbstractServiceClient extends AbstractDependencyInjectionSpringContextTests
{
    private static Log LOGGER = LogFactory.getLog(AbstractServiceClient.class);

    public static final int TIMEOUT = 60000;

    public static final EnumBaseObjectTypeIds BASE_TYPE_DOCUMENT = EnumBaseObjectTypeIds.value1;
    public static final EnumBaseObjectTypeIds BASE_TYPE_FOLDER = EnumBaseObjectTypeIds.value2;
    public static final EnumBaseObjectTypeIds BASE_TYPE_RELATIONSHIP = EnumBaseObjectTypeIds.value3;
    public static final EnumBaseObjectTypeIds BASE_TYPE_POLICY = EnumBaseObjectTypeIds.value4;

    public static final String PROP_NAME = "cmis:name";
    public static final String PROP_OBJECT_ID = "cmis:objectId";
    public static final String PROP_OBJECT_TYPE_ID = "cmis:objectTypeId";
    public static final String PROP_BASE_TYPE_ID = "cmis:baseTypeId";
    public static final String PROP_CREATED_BY = "cmis:createdBy";
    public static final String PROP_CREATION_DATE = "cmis:creationDate";
    public static final String PROP_LAST_MODIFIED_BY = "cmis:lastModifiedBy";
    public static final String PROP_LAST_MODIFICATION_DATE = "cmis:lastModificationDate";
    public static final String PROP_CHANGE_TOKEN = "cmis:changeToken";
    public static final String PROP_IS_IMMUTABLE = "cmis:isImmutable";
    public static final String PROP_IS_LATEST_VERSION = "cmis:isLatestVersion";
    public static final String PROP_IS_MAJOR_VERSION = "cmis:isMajorVersion";
    public static final String PROP_IS_LATEST_MAJOR_VERSION = "cmis:isLatestMajorVersion";
    public static final String PROP_VERSION_LABEL = "cmis:versionLabel";
    public static final String PROP_VERSION_SERIES_ID = "cmis:versionSeriesId";
    public static final String PROP_IS_VERSION_SERIES_CHECKED_OUT = "cmis:isVersionSeriesCheckedOut";
    public static final String PROP_VERSION_SERIES_CHECKED_OUT_BY = "cmis:versionSeriesCheckedOutBy";
    public static final String PROP_VERSION_SERIES_CHECKED_OUT_ID = "cmis:versionSeriesCheckedOutId";
    public static final String PROP_CHECKIN_COMMENT = "cmis:checkinComment";
    public static final String PROP_CONTENT_STREAM_LENGTH = "cmis:contentStreamLength";
    public static final String PROP_CONTENT_STREAM_MIME_TYPE = "cmis:contentStreamMimeType";
    public static final String PROP_CONTENT_STREAM_FILENAME = "cmis:contentStreamFileName";
    public static final String PROP_CONTENT_STREAM_ID = "cmis:contentStreamId";
    public static final String PROP_PARENT_ID = "cmis:parentId";
    public static final String PROP_ALLOWED_CHILD_OBJECT_TYPE_IDS = "cmis:allowedChildObjectTypeIds";
    public static final String PROP_PATH = "cmis:path";
    public static final String PROP_SOURCE_ID = "cmis:sourceId";
    public static final String PROP_TARGET_ID = "cmis:targetId";

    protected static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    protected static final String TEST_CONTENT = "Test Document content entry. This Document was created during test execution...";
    protected static final String TEST_DIRECTORY_NAME_PATTERN = "TestFolder (%s)%s";
    protected static final String TEST_FILE_NAME_PATTERN = "TestFile (%s)%s.txt";
    protected static final String ENCODING = "UTF-8";
    protected static final String INVALID_REPOSITORY_ID = "Wrong Repository Id";

    private static final int MAXIMUM_FOLDERS_AMOUNT = 4;
    private static final String TEST_FOLDER_NAME_PATTERN = "Test Folder(%d.%d)";
    private static final String TEST_DOCUMENT_NAME_PATTERN = "Test Document(%d.%d).txt";
    private static final String TEST_POLICY_NAME_PATTERN = "Test Policy(%s)%s";

    private static final String RENDITION_FILTER_NONE = "cmis:none";
    private static final String IMAGE_BASE_MIMETYPE = "image/";
    private static final String RENDITION_FILTER_DELIMITER = ",";
    private static final String RENDITION_FILTER_WILDCARD = "*";
    private static final String RENDITION_FILTER_SUBTYPES_POSTFIX = "/*";

    protected static final String PERMISSION_READ = "cmis:read";
    protected static final String PERMISSION_WRITE = "cmis:write";
    protected static final String PERMISSION_ALL = "cmis:all";

    private AbstractService abstractService;

    private String proxyUrl;
    private String serverUrl;

    private String username;
    private String password;

    protected static String aclUsername;
    protected static String aclPassword;
    protected static String aclPrincipalId;

    private CmisServicesFactory servicesFactory;

    private static String repositoryId;
    private static String rootFolderId;
    private static String documentTypeId;
    private static String folderTypeId;
    private static String relationshipTypeId;
    private static String policyTypeId;
    private static String policyControllableTypeId;
    private static CmisTypeDefinitionType relationshipSourceType;
    private static CmisTypeDefinitionType relationshipTargetType;
    private static List<CmisTypeDefinitionType> relationshipSubTypes = new LinkedList<CmisTypeDefinitionType>();
    private static CmisRepositoryCapabilitiesType capabilities;
    private static EnumContentStreamAllowed contentStreamAllowed;
    private static EnumACLPropagation aclPropagation;
    private static boolean versioningAllowed = false;
    private Boolean relationshipsSupported = null;
    private Boolean policySupported = null;

    public AbstractServiceClient()
    {
        super();
    }

    public AbstractServiceClient(AbstractService abstractService)
    {
        this.abstractService = abstractService;
    }

    public AbstractService getService()
    {
        return abstractService;
    }

    public void setService(AbstractService abstractService)
    {
        this.abstractService = abstractService;
    }

    public String getServerUrl()
    {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }

    public String getProxyUrl()
    {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl)
    {
        this.proxyUrl = proxyUrl;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public CmisServicesFactory getServicesFactory()
    {
        return servicesFactory;
    }

    public void setServicesFactory(CmisServicesFactory servicesFactory)
    {
        this.servicesFactory = servicesFactory;
    }

    @Override
    protected String[] getConfigLocations()
    {
        setAutowireMode(AUTOWIRE_BY_NAME);
        setDependencyCheck(false);

        return new String[] { "classpath:cmis-context.xml" };
    }

    public abstract void initialize() throws Exception;

    public abstract void invoke() throws Exception;

    public abstract void release() throws Exception;

    protected CmisContentStreamType createUniqueContentStream() throws Exception
    {
        String content = createTestContnet();
        byte[] contentBytes = content.getBytes(ENCODING);
        CmisContentStreamType contentStream = new CmisContentStreamType(BigInteger.valueOf(contentBytes.length), MIMETYPE_TEXT_PLAIN, generateTestFileName(), contentBytes, null);
        return contentStream;
    }

    protected String createTestContnet()
    {
        return TEST_CONTENT + " " + System.currentTimeMillis();
    }

    protected String createAndAssertDocument() throws Exception
    {
        return createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, null);
    }

    protected String createAndAssertDocument(String parentFolderId) throws Exception
    {
        return createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), parentFolderId, null, TEST_CONTENT, null);
    }

    protected String createAndAssertDocument(String documentName, String documentTypeId, String folderId, CmisPropertiesType properties, String content,
            EnumVersioningState initialVersion) throws Exception
    {
        return createAndAssertDocument(documentName, documentTypeId, folderId, properties, content, initialVersion, null, null);
    }

    protected String createAndAssertDocument(String documentName, String documentTypeId, String folderId, CmisPropertiesType properties, String content,
            EnumVersioningState initialVersion, CmisAccessControlListType addACE, CmisAccessControlListType removeACE) throws Exception
    {
        ObjectServicePortBindingStub objectService = getServicesFactory().getObjectService();

        if (null == properties)
        {
            properties = new CmisPropertiesType();
        }
        else if (properties.getPropertyString() != null)
        {
            for (CmisPropertyString stringProperty : properties.getPropertyString())
            {
                if (PROP_NAME.equals(getPropertyName(stringProperty)))
                {
                    documentName = null;
                    break;
                }
            }
        }

        if ((null != documentName) && !"".equals(documentName))
        {
            CmisPropertyString cmisPropertyString = new CmisPropertyString();
            cmisPropertyString.setPropertyDefinitionId(PROP_NAME);
            cmisPropertyString.setValue(new String[] { documentName });
            properties.setPropertyString(new CmisPropertyString[] { cmisPropertyString });
        }
        if ((null != documentTypeId) && !"".equals(documentTypeId))
        {
            CmisPropertyId idProperty = new CmisPropertyId();
            idProperty.setPropertyDefinitionId(PROP_OBJECT_TYPE_ID);
            idProperty.setValue(new String[] { documentTypeId });
            properties.setPropertyId(new CmisPropertyId[] { idProperty });
        }

        CmisContentStreamType contentStream = null;
        if (content != null)
        {
            contentStream = new CmisContentStreamType(BigInteger.valueOf(content.length()), MIMETYPE_TEXT_PLAIN, documentName, content.getBytes(ENCODING), null);
        }

        LOGGER.info("[ObjectService->createDocument]");
        CreateDocumentResponse createDocument = objectService.createDocument(new CreateDocument(getAndAssertRepositoryId(), properties, folderId, contentStream, initialVersion,
                null, addACE, removeACE, null));

        assertNotNull("Create Document response is undefined", createDocument);
        assertNotNull("Create Document response is empty", createDocument.getObjectId());
        assertNotSame("Create Document response contains undefined Id", "", createDocument.getObjectId());

        return createDocument.getObjectId();
    }

    protected String createAndAssertFolder() throws Exception
    {
        return createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), getAndAssertRootFolderId(), null);
    }

    protected String createAndAssertFolder(String folderName, String folderTypeId, String folderId, CmisPropertiesType properties) throws Exception
    {
        return createAndAssertFolder(folderName, folderTypeId, folderId, properties, null, null);
    }

    protected String createAndAssertFolder(String folderName, String folderTypeId, String folderId, CmisPropertiesType properties, CmisAccessControlListType addACE,
            CmisAccessControlListType removeACE) throws Exception
    {
        ObjectServicePortBindingStub objectService = getServicesFactory().getObjectService();

        if (null == properties)
        {
            properties = new CmisPropertiesType();
        }
        else if (properties.getPropertyString() != null)
        {
            for (CmisPropertyString stringProperty : properties.getPropertyString())
            {
                if (PROP_NAME.equals(getPropertyName(stringProperty)))
                {
                    folderName = null;
                    break;
                }
            }
        }
        if ((null != folderName) && !"".equals(folderName))
        {
            CmisPropertyString cmisPropertyString = new CmisPropertyString();
            cmisPropertyString.setPropertyDefinitionId(PROP_NAME);
            cmisPropertyString.setValue(new String[] { folderName });
            properties.setPropertyString(new CmisPropertyString[] { cmisPropertyString });
        }
        if ((null != folderTypeId) && !"".equals(folderTypeId))
        {
            CmisPropertyId idProperty = new CmisPropertyId();
            idProperty.setPropertyDefinitionId(PROP_OBJECT_TYPE_ID);
            idProperty.setValue(new String[] { folderTypeId });
            properties.setPropertyId(new CmisPropertyId[] { idProperty });
        }

        LOGGER.info("[ObjectService->createFolder]");
        CreateFolderResponse createFolder = objectService.createFolder(new CreateFolder(getAndAssertRepositoryId(), properties, folderId, null, addACE, removeACE, null));

        assertNotNull("Create Folder response is undefined", createFolder);
        assertNotNull("Create Folder response is empty", createFolder.getObjectId());
        assertNotSame("Create Folder response contains undefined Id", "", createFolder.getObjectId());

        return createFolder.getObjectId();
    }

    protected List<String> createAndAssertObjectsTree(String rootFolderId, EnumVersioningState documentsInitialVersion, EnumTypesOfFileableObjects objectsToReturn,
            int numberOfFinishLayer, int depth, int minLayerSize, int maxLayerSize) throws Exception
    {
        List<String> result = new LinkedList<String>();

        if ((depth <= 0) || (maxLayerSize < 1) || (minLayerSize > maxLayerSize))
        {
            return result;
        }

        LinkedList<String> layerFolders = new LinkedList<String>();
        layerFolders.add(rootFolderId);

        SecureRandom decideIncliner = new SecureRandom();

        for (int layerNumber = 0; layerNumber < depth; layerNumber++)
        {
            int requiredFolderContainer = ((layerNumber < (depth - 1)) && (layerFolders.size() > 0)) ? (Math.abs(decideIncliner.nextInt()) % layerFolders.size()) : (-1);
            int currentFolderIndex = 0;

            List<String> currentLayerFolders = new LinkedList<String>();

            while (!layerFolders.isEmpty())
            {
                String folderId = layerFolders.getFirst();
                layerFolders.removeFirst();

                int seed = (maxLayerSize > MAXIMUM_FOLDERS_AMOUNT) ? (MAXIMUM_FOLDERS_AMOUNT) : (maxLayerSize);
                int foldersAmount = Math.abs(decideIncliner.nextInt()) % seed;
                foldersAmount = ((foldersAmount == 0) && (currentFolderIndex++ == requiredFolderContainer)) ? (1) : (foldersAmount);

                seed = maxLayerSize - foldersAmount;
                int documentsAmount = (seed == 0) ? (0) : (Math.abs(decideIncliner.nextInt()) % seed);

                if ((documentsAmount + foldersAmount) < minLayerSize)
                {
                    foldersAmount = (foldersAmount == 0) ? (1) : (foldersAmount);
                    seed = minLayerSize - foldersAmount - documentsAmount;
                    documentsAmount += (seed > 0) ? (seed) : (0);
                }

                for (int i = 0; i < foldersAmount; i++)
                {
                    String newFolderId = createAndAssertFolder(String.format(TEST_FOLDER_NAME_PATTERN, layerNumber, i), getAndAssertFolderTypeId(), folderId, null);

                    if (newFolderId != null)
                    {
                        if ((EnumTypesOfFileableObjects.FOLDERS == objectsToReturn) || (EnumTypesOfFileableObjects.BOTH == objectsToReturn)
                                && ((numberOfFinishLayer < 0) ^ (layerNumber < numberOfFinishLayer)))
                        {
                            result.add(newFolderId);
                        }

                        currentLayerFolders.add(newFolderId);
                    }
                }

                for (int i = 0; i < documentsAmount; i++)
                {
                    String newDocumentId = createAndAssertDocument(String.format(TEST_DOCUMENT_NAME_PATTERN, layerNumber, i), getAndAssertDocumentTypeId(), folderId, null,
                            TEST_CONTENT, documentsInitialVersion);

                    if ((newDocumentId != null) && ((EnumTypesOfFileableObjects.DOCUMENTS == objectsToReturn) || (EnumTypesOfFileableObjects.BOTH == objectsToReturn))
                            && ((numberOfFinishLayer < 0) || (layerNumber < numberOfFinishLayer)))
                    {
                        result.add(newDocumentId);
                    }
                }
            }

            layerFolders.addAll(currentLayerFolders);
        }

        return result;
    }

    protected List<String> deleteAndAssertTree(String rootFolderId, EnumUnfileObject multifilledObjectsBehaviour, boolean notDeletedMustAppear) throws Exception
    {
        ObjectServicePortBindingStub objectService = getServicesFactory().getObjectService();

        LOGGER.info("[ObjectService->deleteTree]");
        DeleteTreeResponse undeletedObjects = objectService.deleteTree(new DeleteTree(getAndAssertRepositoryId(), rootFolderId, true, multifilledObjectsBehaviour,
                notDeletedMustAppear, null));

        assertNotNull(undeletedObjects);

        if (notDeletedMustAppear)
        {
            assertNotNull(undeletedObjects.getFailedToDelete());
            assertNotNull(undeletedObjects.getFailedToDelete().getObjectIds());
            String[] undeletedObjectsIds = undeletedObjects.getFailedToDelete().getObjectIds();
            assertTrue(undeletedObjectsIds.length > 0);

            List<String> result = new LinkedList<String>();

            for (String objectId : undeletedObjectsIds)
            {
                assertNotNull(objectId);

                if (objectId != null)
                {
                    result.add(objectId);
                }
            }

            return result;
        }

        if ((undeletedObjects.getFailedToDelete() != null) && (undeletedObjects.getFailedToDelete() != null) && (null != undeletedObjects.getFailedToDelete().getObjectIds()))
        {
            assertFalse(undeletedObjects.getFailedToDelete().getObjectIds().length > 0);
        }

        return null;
    }

    protected String getPropertyName(CmisProperty property)
    {
        String propertyName = (null != property) ? (String) (property.getPropertyDefinitionId()) : (null);
        if (null == propertyName)
        {
            propertyName = (String) property.getLocalName();
            if (null == propertyName)
            {
                propertyName = (String) property.getDisplayName();
            }
        }
        return propertyName;
    }

    protected String getStringProperty(String documentId, String property) throws Exception
    {
        return getStringProperty(getAndAssertObjectProperties(documentId, "*"), property);
    }

    protected String getStringProperty(CmisPropertiesType cmisProperties, String property) throws Exception
    {
        assertNotNull("Properties are undefined", cmisProperties);
        assertNotNull("String Properties are undefined", cmisProperties.getPropertyString());
        assertTrue("String Properties are empty", cmisProperties.getPropertyString().length > 0);
        for (int i = 0; i < cmisProperties.getPropertyString().length; i++)
        {
            CmisPropertyString stringProperty = cmisProperties.getPropertyString(i);
            if ((null != property) && property.equals(getPropertyName(stringProperty)))
            {
                return getAndAssertStringPropertyValue(stringProperty);
            }
        }
        return null;
    }

    private String getAndAssertStringPropertyValue(CmisPropertyString stringProp)
    {
        assertNotNull("String Property values collection is undefined", stringProp.getValue());
        assertTrue("Values Collection of the String Porperty is empty", stringProp.getValue().length > 0);
        return stringProp.getValue(0);
    }

    protected Boolean getBooleanProperty(String documentId, String property) throws Exception
    {
        return getBooleanProperty(getAndAssertObjectProperties(documentId, "*"), property);
    }

    protected Boolean getBooleanProperty(CmisPropertiesType cmisProperties, String property) throws Exception
    {
        assertNotNull("Properties are undefined", cmisProperties);
        assertNotNull("Boolean Properties are undefined", cmisProperties.getPropertyBoolean());
        assertTrue("Boolean Properties are empty", cmisProperties.getPropertyBoolean().length > 0);
        for (int i = 0; i < cmisProperties.getPropertyBoolean().length; i++)
        {
            CmisPropertyBoolean booleanProperty = cmisProperties.getPropertyBoolean(i);
            assertNotNull("One of the Boolean Properties is in 'not set' state", booleanProperty);
            if ((null != property) && property.equals(getPropertyName(booleanProperty)))
            {
                return getAndAssertBooleanPropertyValue(booleanProperty);
            }
        }
        return null;
    }

    private boolean getAndAssertBooleanPropertyValue(CmisPropertyBoolean booleanProp)
    {
        assertNotNull("Boolean Property values collection is undefined", booleanProp.getValue());
        assertTrue("Values Collection of the Boolean Porperty is empty", booleanProp.getValue().length > 0);
        assertNotNull("One of the Boolean Property from properties' collection is in 'not set' state", booleanProp.getValue(0));
        return booleanProp.getValue(0);
    }

    protected String getIdProperty(String documentId, String property) throws Exception
    {
        return getIdProperty(getAndAssertObjectProperties(documentId, "*"), property);
    }

    protected String getIdProperty(CmisPropertiesType cmisProperties, String property) throws Exception
    {
        assertNotNull("Properties are undefined", cmisProperties);
        assertNotNull("Id Properties are undefined", cmisProperties.getPropertyId());
        assertTrue("Id Properties are empty", cmisProperties.getPropertyId().length > 0);
        for (int i = 0; i < cmisProperties.getPropertyId().length; i++)
        {
            CmisPropertyId idProperty = cmisProperties.getPropertyId(i);
            assertNotNull("One of the Id Properties is in 'not set' state", idProperty);
            if ((null != property) && property.equals(getPropertyName(idProperty)))
            {
                return getAndAssertIdPropertyValue(idProperty);
            }
        }
        return null;
    }

    private String getAndAssertIdPropertyValue(CmisPropertyId idProp)
    {
        assertNotNull("Id Property values collection is undefined", idProp.getValue());
        assertTrue("Values Collection of the Id Porperty is empty", idProp.getValue().length > 0);
        assertNotNull("One of the Id Property from properties' collection is in 'not set' state", idProp.getValue(0));
        return idProp.getValue(0);
    }

    protected boolean isDocumentInFolder(String documentId, String folderId) throws Exception
    {
        boolean found = false;
        LOGGER.info("[NavigationService->getChildren]");
        GetChildrenResponse childrenResponse = getServicesFactory().getNavigationService().getChildren(
                new GetChildren(getAndAssertRepositoryId(), folderId, PROP_OBJECT_ID, null, false, EnumIncludeRelationships.none, "", false, null, null, null));
        if (childrenResponse == null || childrenResponse.getObjects() == null || childrenResponse.getObjects().getObjects() == null)
        {
            return false;
        }
        for (int i = 0; childrenResponse.getObjects() != null && !found && i < childrenResponse.getObjects().getObjects().length; i++)
        {
            CmisObjectInFolderType cmisObjectType = childrenResponse.getObjects().getObjects(i);
            assertNotNull(cmisObjectType);
            assertNotNull(cmisObjectType.getObject());
            assertNotNull(cmisObjectType.getObject().getProperties());
            for (int j = 0; !found && j < cmisObjectType.getObject().getProperties().getPropertyId().length; j++)
            {
                found = PROP_OBJECT_ID.equals(cmisObjectType.getObject().getProperties().getPropertyId()[j].getPropertyDefinitionId())
                        && documentId.equals(cmisObjectType.getObject().getProperties().getPropertyId()[j].getValue(0));
            }
        }
        return found;
    }

    protected String createAndAssertRelationship() throws Exception
    {
        return createAndAssertRelationship(null, null);
    }

    protected String createAndAssertRelationship(String sourceId, String targetId) throws Exception
    {
        return createAndAssertRelationship(sourceId, targetId, getAndAssertRelationshipTypeId(), null);
    }

    protected String createAndAssertRelationship(String folderId) throws Exception
    {
        return createAndAssertRelationship(null, null, getAndAssertRelationshipTypeId(), folderId);
    }

    protected String createAndAssertRelationship(String sourceId, String targetId, String relationshipTypeId) throws Exception
    {
        return createAndAssertRelationship(sourceId, targetId, relationshipTypeId, null);
    }

    protected String createAndAssertRelationship(String sourceId, String targetId, String relationshipTypeId, String folder) throws Exception
    {
        folder = folder == null ? getAndAssertRootFolderId() : folder;
        if (null == sourceId)
        {
            CmisTypeDefinitionType sourceType = getAndAssertRelationshipSourceType();

            if ((null != sourceType) && BASE_TYPE_FOLDER.equals(sourceType.getBaseId()))
            {
                sourceId = createAndAssertFolder(generateTestFolderName(), sourceType.getId(), folder, null);
            }
            else
            {
                sourceId = createAndAssertDocument(generateTestFileName(), sourceType == null ? getAndAssertDocumentTypeId() : sourceType.getId(), folder, null, TEST_CONTENT,
                        EnumVersioningState.none);
            }
        }
        if (null == targetId)
        {
            CmisTypeDefinitionType targetType = getAndAssertRelationshipTargetType();

            if ((null != targetType) && BASE_TYPE_FOLDER.equals(targetType.getBaseId()))
            {
                targetId = createAndAssertFolder(generateTestFolderName(), targetType.getId(), folder, null);
            }
            else
            {
                targetId = createAndAssertDocument(generateTestFileName(), targetType == null ? getAndAssertDocumentTypeId() : targetType.getId(), folder, null, "Test content"
                        + System.currentTimeMillis(), EnumVersioningState.none);
            }
        }

        CmisPropertiesType cmisPropertiesType = new CmisPropertiesType();
        CmisPropertyId idProperty = new CmisPropertyId();
        idProperty.setPropertyDefinitionId(PROP_OBJECT_TYPE_ID);
        idProperty.setValue(new String[] { relationshipTypeId });
        CmisPropertyId[] ids = new CmisPropertyId[3];
        ids[0] = idProperty;
        idProperty = new CmisPropertyId();
        idProperty.setPropertyDefinitionId(PROP_SOURCE_ID);
        idProperty.setValue(new String[] { sourceId });
        ids[1] = idProperty;
        idProperty = new CmisPropertyId();
        idProperty.setPropertyDefinitionId(PROP_TARGET_ID);
        idProperty.setValue(new String[] { targetId });
        ids[2] = idProperty;
        cmisPropertiesType.setPropertyId(ids);

        LOGGER.info("[ObjectService->createRelationship]");
        CreateRelationshipResponse response = getServicesFactory().getObjectService().createRelationship(
                new CreateRelationship(getAndAssertRepositoryId(), cmisPropertiesType, null, null, null, null));
        assertTrue("Relationship was not created", response != null && response.getObjectId() != null);
        return response.getObjectId();
    }

    protected String createRelationshipSourceObject(String folderId) throws Exception
    {
        String sourceId = null;
        CmisTypeDefinitionType sourceType = getAndAssertRelationshipSourceType();

        if ((null != sourceType) && BASE_TYPE_FOLDER.equals(sourceType.getBaseId()))
        {
            sourceId = createAndAssertFolder(generateTestFolderName(), sourceType.getId(), folderId, null);
        }
        else
        {
            sourceId = createAndAssertDocument(generateTestFileName(), sourceType == null ? getAndAssertDocumentTypeId() : sourceType.getId(), folderId, null, TEST_CONTENT,
                    EnumVersioningState.none);
        }
        return sourceId;
    }

    protected String createRelationshipTargetObject(String folderId) throws Exception
    {
        String targetId = null;
        CmisTypeDefinitionType targetType = getAndAssertRelationshipTargetType();

        if ((null != targetType) && BASE_TYPE_FOLDER.equals(targetType.getBaseId()))
        {
            targetId = createAndAssertFolder(generateTestFolderName(), targetType.getId(), folderId, null);
        }
        else
        {
            targetId = createAndAssertDocument(generateTestFileName(), targetType == null ? getAndAssertDocumentTypeId() : targetType.getId(), folderId, null, TEST_CONTENT,
                    EnumVersioningState.none);
        }
        return targetId;
    }

    protected String cancelCheckOutAndAssert(String documentId) throws Exception
    {
        CmisPropertiesType properties = getAndAssertObjectProperties(documentId, "*");
        try
        {
            LOGGER.info("[VersioningService->cancelCheckOut]");
            getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), documentId, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        return getIdProperty(properties, PROP_VERSION_SERIES_ID);
    }

    protected void deleteAndAssertObject(String objectId)
    {
        deleteAndAssertObject(objectId, true);
    }

    protected void deleteAndAssertObject(String objectId, boolean allVersions)
    {
        try
        {
            String repositoryId = getAndAssertRepositoryId();
            ObjectServicePortBindingStub objectService = getServicesFactory().getObjectService();
            LOGGER.info("[ObjectService->deleteObject]");
            objectService.deleteObject(new DeleteObject(repositoryId, objectId, allVersions, null));
            assertFalse(("Object with Id='" + objectId + "' was not deleted"), objectExists(objectId));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }

    protected boolean objectExists(String objectId)
    {
        try
        {
            getAndAssertObjectProperties(objectId, "*", false);
            return true;
        }
        catch (Exception e)
        {
            Set<EnumServiceException> expectedExceptions = new HashSet<EnumServiceException>();
            expectedExceptions.add(EnumServiceException.runtime); // FIXME: maybe this type MUST be deleted when transaction problem will be fixed
            expectedExceptions.add(EnumServiceException.invalidArgument);
            expectedExceptions.add(EnumServiceException.objectNotFound);
            assertException("Getting Properties for Deleted Object", e, expectedExceptions);
            return false;
        }
    }

    protected void assertException(String exceptionCase, Exception actual, EnumServiceException expected)
    {
        Set<EnumServiceException> expectedSet = null;
        if (expected != null)
        {
            expectedSet = new HashSet<EnumServiceException>();
            expectedSet.add(expected);
        }
        assertException(exceptionCase, actual, expectedSet);
    }

    protected void assertException(String exceptionCase, Exception actual, Set<EnumServiceException> expected)
    {
        String caseHint = ((null != exceptionCase) && !"".equals(exceptionCase)) ? (" during " + exceptionCase) : ("");
        if (actual instanceof CmisFaultType)
        {
            if ((null != expected) && !expected.isEmpty())
            {
                assertTrue(("Invalid exception was thrown: " + actual.toString() + caseHint + ". "), expected.contains(((CmisFaultType) actual).getType()));
            }
        }
        else
        {
            fail(("Invalid exception was thrown" + caseHint + ": ") + ((null != actual) ? (actual.toString()) : ("")));
        }
    }

    protected String generateTestFileName()
    {
        return generateTestFileName("");
    }

    protected String generateTestFileName(String appender)
    {
        return String.format(TEST_FILE_NAME_PATTERN, System.currentTimeMillis(), appender);
    }

    protected String generateTestFolderName()
    {
        return generateTestFolderName("");
    }

    protected String generateTestFolderName(String appender)
    {
        return String.format(TEST_DIRECTORY_NAME_PATTERN, System.currentTimeMillis(), appender);
    }

    protected String generateTestPolicyName()
    {
        return generateTestPolicyName("");
    }

    protected String generateTestPolicyName(String appender)
    {
        return String.format(TEST_POLICY_NAME_PATTERN, System.currentTimeMillis(), appender);
    }

    protected GetTypeChildrenResponse getAndAssertTypeChildren(String typeId, boolean includeProperties, Long maxItems, Long skipCount)
    {
        GetTypeChildrenResponse response = null;
        try
        {
            LOGGER.info("[RepositoryService->getTypeChildren]");
            response = getServicesFactory().getRepositoryService().getTypeChildren(
                    new GetTypeChildren(getAndAssertRepositoryId(), typeId, includeProperties, BigInteger.valueOf(maxItems), BigInteger.valueOf(skipCount), null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("GetTypeChildren response is NULL", response);
        assertNotNull("GetTypeChildren response is NULL", response.getTypes());
        assertNotNull("GetTypeChildren response is empty", response.getTypes().getTypes());
        assertTrue("GetTypeChildren response is empty", response.getTypes().getTypes().length > 0);
        for (CmisTypeDefinitionType typeDef : response.getTypes().getTypes())
        {
            assertNotNull("Some of type definition type object is NULL", typeDef);
            assertNotNull("Some of type definition type object id is NULL", typeDef.getId());
            if ((null != typeId) && !BASE_TYPE_DOCUMENT.getValue().equals(typeDef.getId()) && !BASE_TYPE_FOLDER.getValue().equals(typeDef.getId())
                    && !BASE_TYPE_RELATIONSHIP.getValue().equals(typeDef.getId()) && !BASE_TYPE_POLICY.getValue().equals(typeDef.getId()))
            {
                assertEquals(typeId, typeDef.getParentId());
            }
        }
        return response;
    }

    protected CmisTypeContainer[] getAndAssertTypeDescendants(String typeId, long depth, boolean typeDefinitions)
    {
        CmisTypeContainer[] response = null;
        try
        {
            LOGGER.info("[RepositoryService->getTypeDescendants]");
            response = getServicesFactory().getRepositoryService().getTypeDescendants(
                    new GetTypeDescendants(getAndAssertRepositoryId(), typeId, BigInteger.valueOf(depth), typeDefinitions, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        if (response != null && response.length > 0)
        {
            for (CmisTypeContainer container : response)
            {
                assertNotNull("Invalid Type Descendants response: one of the Type Container is undefined", container);
                if (null != container.getChildren())
                {
                    for (CmisTypeContainer childContainer : container.getChildren())
                    {
                        assertNotNull("Invalid Type Descendants response: one of the Descendants Type Container is undefined", childContainer);
                        assertNotNull("Invalid Type Descendants response: one of the Descendants Type is undefined", childContainer.getType());
                    }
                }
            }
        }
        return response;
    }

    private String getTypeId(EnumBaseObjectTypeIds type) throws Exception
    {
        if ((null == type) || (null == type.getValue()) || "".equals(type.getValue()))
        {
            return null;
        }
        String typeId = type.getValue();
        CmisTypeContainer[] response = getAndAssertTypeDescendants(typeId, -1, true);
        BaseConditionCalculator calculator = new BaseConditionCalculator();
        boolean document = BASE_TYPE_DOCUMENT.getValue().equals(typeId);
        if (document)
        {
            calculator = new BaseConditionCalculator()
            {
                private int currentTypeAbility = 0;

                @Override
                public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
                {
                    int enumeratedTypeAbility = 0;
                    if (enumeratedType.isCreatable())
                    {
                        CmisTypeDocumentDefinitionType documentType = (CmisTypeDocumentDefinitionType) enumeratedType;
                        enumeratedTypeAbility = 10000 + ((EnumContentStreamAllowed.notallowed != documentType.getContentStreamAllowed()) ? (1) : (0));
                        enumeratedTypeAbility += (enumeratedType.isControllablePolicy()) ? (10) : (0);
                        enumeratedTypeAbility += (documentType.isVersionable()) ? (100) : (0);
                        enumeratedTypeAbility += (enumeratedType.isFileable()) ? (1000) : (0);
                    }
                    boolean result = enumeratedTypeAbility > currentTypeAbility;
                    if (result)
                    {
                        currentTypeAbility = enumeratedTypeAbility;
                    }
                    return result;
                }
            };
        }
        else
        {
            if (BASE_TYPE_FOLDER.getValue().equals(typeId))
            {
                calculator = new BaseConditionCalculator()
                {
                    private int previousPower = 0;

                    @Override
                    public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
                    {
                        if (super.calculate(currentType, enumeratedType))
                        {
                            int currentPower = 1;
                            currentPower += (enumeratedType.isFileable()) ? (1) : (0);
                            currentPower += (enumeratedType.isQueryable()) ? (1) : (0);
                            currentPower += (enumeratedType.isControllablePolicy()) ? (2) : (0);
                            currentPower += (enumeratedType.isControllableACL()) ? (2) : (0);
                            if (currentPower > previousPower)
                            {
                                previousPower = currentPower;
                                return true;
                            }
                            return false;
                        }
                        return false;
                    }
                };
            }
            else
            {
                if (BASE_TYPE_RELATIONSHIP.getValue().equals(typeId))
                {
                    calculator = new BaseConditionCalculator()
                    {
                        private int currentTypeAbility = -1;

                        @Override
                        public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
                        {
                            boolean result = false;
                            if (enumeratedType.isCreatable())
                            {
                                CmisTypeRelationshipDefinitionType relationshipType = (CmisTypeRelationshipDefinitionType) enumeratedType;
                                String participantTypeId = ((null == relationshipType.getAllowedSourceTypes()) || (relationshipType.getAllowedSourceTypes().length < 1)) ? (getAndAssertFolderTypeId())
                                        : (relationshipType.getAllowedSourceTypes(0));
                                final CmisTypeDefinitionType relationshipSourceType = (null != participantTypeId) ? (getAndAssertTypeDefinition(participantTypeId)) : (null);
                                participantTypeId = ((null == relationshipType.getAllowedTargetTypes()) || (relationshipType.getAllowedTargetTypes().length < 1)) ? (getAndAssertDocumentTypeId())
                                        : (relationshipType.getAllowedTargetTypes(0));
                                final CmisTypeDefinitionType relationshipTargetType = (null != participantTypeId) ? (getAndAssertTypeDefinition(participantTypeId)) : (null);
                                result = (null != relationshipSourceType) && (null != relationshipTargetType);
                                if (result)
                                {
                                    CmisTypeContainer[] subTypes = getAndAssertTypeDescendants(enumeratedType.getId(), -1, false);
                                    final Integer[] currentAbility = new Integer[] { new Integer(0) };
                                    final List<CmisTypeDefinitionType> subTypesList = new LinkedList<CmisTypeDefinitionType>();
                                    enumerateAndAssertTypesHierarchy(subTypes, new BaseConditionCalculator()
                                    {
                                        @Override
                                        public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
                                        {
                                            if (enumeratedType.isCreatable())
                                            {
                                                CmisTypeRelationshipDefinitionType relationshipType = (CmisTypeRelationshipDefinitionType) enumeratedType;
                                                Set<String> allowedSourceIds = null;
                                                if (null != relationshipType.getAllowedSourceTypes())
                                                {
                                                    allowedSourceIds = new HashSet<String>(Arrays.asList(relationshipType.getAllowedSourceTypes()));
                                                }
                                                Set<String> allowedTargetIds = null;
                                                if (null != relationshipType.getAllowedTargetTypes())
                                                {
                                                    allowedTargetIds = new HashSet<String>(Arrays.asList(relationshipType.getAllowedTargetTypes()));
                                                }
                                                boolean validSubType = ((null == allowedSourceIds) || allowedSourceIds.contains(relationshipSourceType.getId()))
                                                        && ((null == allowedTargetIds) || allowedTargetIds.contains(relationshipTargetType.getId()));
                                                if (validSubType)
                                                {
                                                    currentAbility[0]++;
                                                    subTypesList.add(enumeratedType);
                                                }
                                                return validSubType;
                                            }
                                            return false;
                                        }
                                    }, false);
                                    result = currentAbility[0] > currentTypeAbility;
                                    if (result)
                                    {
                                        currentTypeAbility = currentAbility[0];
                                        relationshipSubTypes = subTypesList;
                                        AbstractServiceClient.relationshipSourceType = relationshipSourceType;
                                        AbstractServiceClient.relationshipTargetType = relationshipTargetType;
                                    }
                                }
                            }
                            return result;
                        }
                    };
                }
            }
        }
        typeId = enumerateAndAssertTypesHierarchy(response, calculator, false);
        if (document)
        {
            LOGGER.info("[RepositoryService->getTypeDefinition]");
            GetTypeDefinitionResponse typeDef = getServicesFactory().getRepositoryService().getTypeDefinition(new GetTypeDefinition(getAndAssertRepositoryId(), typeId, null));
            assertNotNull("Type Definition response is empty", typeDef);
            CmisTypeDocumentDefinitionType documentType = (CmisTypeDocumentDefinitionType) typeDef.getType();
            assertNotNull("Invalid Type Definition Response: Type Definition is undefined", documentType);
            contentStreamAllowed = documentType.getContentStreamAllowed();
            versioningAllowed = documentType.isVersionable();
        }
        return typeId;
    }

    protected List<CmisTypeDefinitionType> getRelationshipSubTypes()
    {
        if (null == relationshipSubTypes)
        {
            getAndAssertRelationshipTypeId();
        }
        return relationshipSubTypes;
    }

    protected CmisPropertiesType getAndAssertObjectProperties(String objectId, String filter) throws Exception
    {
        return getAndAssertObjectProperties(objectId, filter, true);
    }

    protected CmisPropertiesType getAndAssertObjectProperties(String objectId, String filter, boolean failOnException) throws Exception
    {
        GetPropertiesResponse result = null;
        try
        {
            LOGGER.info("[ObjectService->getProperties]");
            result = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), objectId, filter, null));
        }
        catch (Exception e)
        {
            if (failOnException)
            {
                fail(e.toString());
            }
            else
            {
                throw e;
            }
        }
        assertNotNull(result);
        assertNotNull(result.getProperties());
        return result.getProperties();
    }

    protected String searchAndAssertFolderFromNotBaseType() throws Exception
    {
        GetTypeChildrenResponse typeChildren = getAndAssertTypeChildren(null, true, 0L, 0L);
        String baseFolderTypeId = getBaseFolderTypeId(typeChildren.getTypes().getTypes());
        typeChildren = getAndAssertTypeChildren(baseFolderTypeId, true, 0L, 0L);
        for (CmisTypeDefinitionType typeDef : typeChildren.getTypes().getTypes())
        {
            if (null != typeDef.getParentId())
            {
                return typeDef.getId();
            }
        }
        return null;
    }

    private String getBaseFolderTypeId(CmisTypeDefinitionType[] types)
    {
        for (CmisTypeDefinitionType type : types)
        {
            if ((type instanceof CmisTypeFolderDefinitionType) && (null == type.getParentId()))
            {
                return type.getId();
            }
        }
        return null;
    }

    protected String searchAndAssertNotAllowedForFolderObjectTypeId(String folderId) throws Exception
    {
        return searchAndAssertNotAllowedForFolderObjectTypeId(folderId, true);
    }

    protected String searchAndAssertNotAllowedForFolderObjectTypeId(String folderId, final boolean document) throws Exception
    {
        CmisPropertiesType properties = getAndAssertObjectProperties(folderId, PROP_ALLOWED_CHILD_OBJECT_TYPE_IDS);
        if (properties == null || properties.getPropertyId() == null || properties.getPropertyId().length == 0)
        {
            return null;
        }
        Set<String> resultIds = new HashSet<String>();
        if (properties != null && properties.getPropertyId(0) != null && properties.getPropertyId(0).getValue() != null && properties.getPropertyId(0).getValue().length > 0)
        {
            resultIds = new HashSet<String>(Arrays.asList(properties.getPropertyId(0).getValue()));
        }
        if (resultIds == null || resultIds.isEmpty() || (resultIds.size() == 1 && resultIds.contains(null)))
        {
            return document ? getAndAssertFolderTypeId() : getAndAssertDocumentTypeId();
        }
        final Set<String> allowedChildObjectTypeIds = new HashSet<String>(resultIds);
        BaseConditionCalculator calculator = new BaseConditionCalculator()
        {
            @Override
            public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
            {
                if ((document && (enumeratedType instanceof CmisTypeDocumentDefinitionType)) || (!document && (enumeratedType instanceof CmisTypeFolderDefinitionType)))
                {
                    return allowedChildObjectTypeIds.isEmpty() || !allowedChildObjectTypeIds.contains(enumeratedType.getId());
                }
                return false;
            }
        };
        return enumerateAndAssertTypesHierarchy(getAndAssertTypeDescendants(null, -1, true), calculator, true);
    }

    protected String searchAndAssertNotVersionableDocumentType() throws Exception
    {
        String baseDocumentTypeId = getBaseDocumentTypeId(getAndAssertTypeChildren(null, false, 0L, 0L));
        return enumerateAndAssertTypesHierarchy(getAndAssertTypeDescendants(baseDocumentTypeId, -1, true), new BaseConditionCalculator()
        {
            @Override
            public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
            {
                return (null != enumeratedType) && (!((CmisTypeDocumentDefinitionType) enumeratedType).isVersionable());
            }
        }, true);
    }

    protected String searchAndAssertRelationshipTypeWithAllowedSourceTypes() throws Exception
    {
        String baseRelationshipTypeId = getBaseRelationshipTypeId(getAndAssertTypeChildren(null, false, 0L, 0L));
        return enumerateAndAssertTypesHierarchy(getAndAssertTypeDescendants(baseRelationshipTypeId, -1, true), new BaseConditionCalculator()
        {
            @Override
            public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
            {
                return (null != enumeratedType) && (null != ((CmisTypeRelationshipDefinitionType) enumeratedType).getAllowedSourceTypes())
                        && ((CmisTypeRelationshipDefinitionType) enumeratedType).getAllowedSourceTypes().length > 0;
            }
        }, true);
    }

    protected String searchAndAssertRelationshipTypeWithAllowedTargetTypes() throws Exception
    {
        String baseRelationshipTypeId = getBaseRelationshipTypeId(getAndAssertTypeChildren(null, false, 0L, 0L));
        return enumerateAndAssertTypesHierarchy(getAndAssertTypeDescendants(baseRelationshipTypeId, -1, true), new BaseConditionCalculator()
        {
            @Override
            public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
            {
                return (null != enumeratedType) && (null != ((CmisTypeRelationshipDefinitionType) enumeratedType).getAllowedTargetTypes())
                        && ((CmisTypeRelationshipDefinitionType) enumeratedType).getAllowedTargetTypes().length > 0;
            }
        }, true);
    }

    protected String searchAndAssertNotAllowedSourceForRelationshipTypeId(String relationshipTypeId) throws Exception
    {

        CmisTypeRelationshipDefinitionType relationshipDefinitionType = (CmisTypeRelationshipDefinitionType) getAndAssertTypeDefinition(relationshipTypeId);
        final Set<String> allowedSourceTypeIds = new HashSet<String>(Arrays.asList(relationshipDefinitionType.getAllowedSourceTypes()));
        BaseConditionCalculator calculator = new BaseConditionCalculator()
        {
            @Override
            public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
            {
                return (null != enumeratedType) && !allowedSourceTypeIds.contains(enumeratedType.getId());
            }
        };
        return enumerateAndAssertTypesHierarchy(getAndAssertTypeDescendants(null, -1, true), calculator, true);
    }

    protected String searchAndAssertNotAllowedTargetForRelationshipTypeId(String relationshipTypeId) throws Exception
    {

        CmisTypeRelationshipDefinitionType relationshipDefinitionType = (CmisTypeRelationshipDefinitionType) getAndAssertTypeDefinition(relationshipTypeId);
        final Set<String> allowedTargetTypeIds = new HashSet<String>(Arrays.asList(relationshipDefinitionType.getAllowedSourceTypes()));
        BaseConditionCalculator calculator = new BaseConditionCalculator()
        {
            @Override
            public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
            {
                return (null != enumeratedType) && !allowedTargetTypeIds.contains(enumeratedType.getId());
            }
        };
        return enumerateAndAssertTypesHierarchy(getAndAssertTypeDescendants(null, -1, true), calculator, true);
    }

    protected String searchAndAssertNotFileableType() throws Exception
    {
        return enumerateAndAssertTypesHierarchy(getAndAssertTypeDescendants(null, -1, true), new BaseConditionCalculator()
        {
            @Override
            public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
            {
                return null != enumeratedType && enumeratedType.isCreatable() && !enumeratedType.getBaseId().equals(BASE_TYPE_DOCUMENT)
                        && !enumeratedType.getBaseId().equals(BASE_TYPE_FOLDER) && !enumeratedType.isFileable();
            }
        }, true);
    }

    protected void assertRenditions(CmisObjectType cmisObject, String filter, String[] expectedKinds, String[] expectedMimetypes)
    {
        assertNotNull("cmisObject is null", cmisObject);

        if (filter == null || filter.equals(RENDITION_FILTER_NONE))
        {
            assertTrue("Rendition are not empty for empty filter", cmisObject.getRendition() == null || cmisObject.getRendition().length == 0);
        }
        else
        {
            if (cmisObject.getRendition() != null)
            {
                for (CmisRenditionType rendition : cmisObject.getRendition())
                {
                    assertRendition(rendition);
                    if (!filter.equals(RENDITION_FILTER_WILDCARD))
                    {
                        assertContains(rendition, expectedKinds, expectedMimetypes);
                    }
                }
            }
        }
    }

    protected void assertRendition(CmisRenditionType rendition)
    {
        assertNotNull("Rendition is null", rendition);
        assertNotNull("Rendition streamId is null", rendition.getStreamId());
        assertNotNull("Rendition kind is null", rendition.getKind());
        assertNotNull("Rendition mimetype is null", rendition.getMimetype());
        if (rendition.getMimetype().startsWith(IMAGE_BASE_MIMETYPE))
        {
            assertNotNull("Rendition width is null", rendition.getWidth());
            assertNotNull("Rendition height is null", rendition.getHeight());
        }
    }

    protected void assertContains(CmisRenditionType rendition, String[] expectedKinds, String[] expectedMimetypes)
    {
        boolean contains = false;
        if (expectedKinds != null)
        {
            for (String kind : expectedKinds)
            {
                if (rendition.getKind().equals(kind))
                {
                    contains = true;
                    break;
                }
            }
        }
        if (expectedMimetypes != null)
        {
            for (String mimetype : expectedMimetypes)
            {
                if (mimetype.endsWith(RENDITION_FILTER_SUBTYPES_POSTFIX))
                {
                    String baseMimetype = getBaseType(mimetype);
                    if (rendition.getMimetype().startsWith(baseMimetype))
                    {
                        contains = true;
                        break;
                    }
                }
                if (rendition.getMimetype().equals(mimetype))
                {
                    contains = true;
                    break;
                }
            }
        }
        assertTrue("Received rendition doesn't satisfy the filter conditions", contains);
    }

    protected String[] getMimeTypes(CmisRenditionType[] renditions)
    {
        Set<String> result = null;
        if (renditions != null)
        {
            result = new HashSet<String>();
            for (CmisRenditionType rendition : renditions)
            {
                result.add(rendition.getMimetype());
            }
        }
        return result != null ? result.toArray(new String[0]) : null;
    }

    protected String[] getBaseMimeTypes(CmisRenditionType[] renditions)
    {
        Set<String> result = null;
        if (renditions != null)
        {
            result = new HashSet<String>();
            for (CmisRenditionType rendition : renditions)
            {
                result.add(getBaseType(rendition.getMimetype()) + RENDITION_FILTER_SUBTYPES_POSTFIX);
            }
        }
        return result != null ? result.toArray(new String[0]) : null;
    }

    protected String getBaseType(String mimetype)
    {
        String baseMymetype = mimetype;
        int subTypeIndex = mimetype.indexOf("/");
        if (subTypeIndex > 0 || subTypeIndex < mimetype.length())
        {
            baseMymetype = mimetype.substring(0, subTypeIndex);
        }
        return baseMymetype;
    }

    protected String[] getKinds(CmisRenditionType[] renditions)
    {
        Set<String> result = null;
        if (renditions != null)
        {
            result = new HashSet<String>();
            for (CmisRenditionType rendition : renditions)
            {
                result.add(rendition.getKind());
            }
        }
        return result != null ? result.toArray(new String[0]) : null;
    }

    protected String createFilter(String[] kinds, String[] mimetypes)
    {
        StringBuilder filter = new StringBuilder();
        if (kinds != null)
        {
            for (String kind : kinds)
            {
                filter.append(kind);
                filter.append(RENDITION_FILTER_DELIMITER);
            }
        }
        if (mimetypes != null)
        {
            for (String mimetype : mimetypes)
            {
                filter.append(mimetype);
                filter.append(RENDITION_FILTER_DELIMITER);
            }
        }
        filter.delete(filter.length() - 1, filter.length());
        return filter.toString();
    }

    protected List<RenditionData> getTestRenditions(String objectId) throws Exception
    {
        GetRenditions getRenditionsRequest = new GetRenditions();
        getRenditionsRequest.setRepositoryId(getAndAssertRepositoryId());
        getRenditionsRequest.setObjectId(objectId);
        getRenditionsRequest.setMaxItems(BigInteger.valueOf(200));
        getRenditionsRequest.setSkipCount(BigInteger.ZERO);
        getRenditionsRequest.setRenditionFilter(RENDITION_FILTER_WILDCARD);
        LOGGER.info("[ObjectService->getRenditions]");
        CmisRenditionType[] allRenditions = getServicesFactory().getObjectService().getRenditions(getRenditionsRequest);

        List<RenditionData> testRenditions = null;
        if (allRenditions != null && allRenditions.length > 0)
        {
            String[] kinds = getKinds(allRenditions);
            String[] mimetypes = getMimeTypes(allRenditions);
            String[] baseMimeTypes = getBaseMimeTypes(allRenditions);
            testRenditions = new ArrayList<RenditionData>();
            testRenditions.add(new RenditionData(RENDITION_FILTER_WILDCARD, null, null));
            testRenditions.add(new RenditionData(new String[] { kinds[0] }, null));
            testRenditions.add(new RenditionData(kinds, null));
            testRenditions.add(new RenditionData(null, new String[] { mimetypes[0] }));
            testRenditions.add(new RenditionData(new String[] { kinds[0] }, new String[] { mimetypes[0] }));
            testRenditions.add(new RenditionData(null, new String[] { baseMimeTypes[0] }));
            testRenditions.add(new RenditionData(new String[] { kinds[0] }, new String[] { baseMimeTypes[0] }));
        }
        return testRenditions;
    }

    protected String enumerateAndAssertTypesHierarchy(CmisTypeContainer[] rootContainers, BaseConditionCalculator calculator, boolean firstIsValid)
    {
        if ((null == rootContainers) || (null == calculator) || (rootContainers.length < 1))
        {
            return null;
        }

        LinkedList<CmisTypeContainer> typesList = new LinkedList<CmisTypeContainer>();
        addContainers(typesList, rootContainers);
        CmisTypeDefinitionType bestType = null;
        for (CmisTypeContainer currentContainer = typesList.getFirst(); !typesList.isEmpty(); typesList.removeFirst(), currentContainer = (!typesList.isEmpty()) ? (typesList
                .getFirst()) : (null))
        {
            if (null == currentContainer)
            {
                continue;
            }

            CmisTypeDefinitionType typeDef = currentContainer.getType();
            assertNotNull("Invalid TypeContainer: parent type is undefined", typeDef);
            if (null != typeDef)
            {
                if (calculator.calculate(bestType, typeDef))
                {
                    if (firstIsValid)
                    {
                        return typeDef.getId();
                    }
                    else
                    {
                        bestType = typeDef;
                    }
                }
            }
            if (currentContainer.getChildren() != null)
            {
                addContainers(typesList, currentContainer.getChildren());
            }
        }

        return (null != bestType) ? (bestType.getId()) : (null);
    }

    protected void addContainers(LinkedList<CmisTypeContainer> typesList, CmisTypeContainer[] currentContainers)
    {
        for (CmisTypeContainer container : currentContainers)
        {
            assertNotNull("Invalid Type Descendants response: one of the Type Containers is undefined", container);
            if (null != container)
            {
                typesList.addLast(container);
            }
        }
    }

    protected String getBaseDocumentTypeId(GetTypeChildrenResponse response)
    {
        String typeId = null;
        for (CmisTypeDefinitionType typeDef : response.getTypes().getTypes())
        {
            if ((typeDef instanceof CmisTypeDocumentDefinitionType) && (null == typeDef.getParentId()))
            {
                typeId = typeDef.getId();
                break;
            }
        }
        return typeId;
    }

    protected String getBaseRelationshipTypeId(GetTypeChildrenResponse response)
    {
        String typeId = null;
        for (CmisTypeDefinitionType typeDef : response.getTypes().getTypes())
        {
            if ((typeDef instanceof CmisTypeRelationshipDefinitionType) && (null == typeDef.getParentId()))
            {
                typeId = typeDef.getId();
                break;
            }
        }
        return typeId;
    }

    protected String getBasePolicyTypeId(GetTypeChildrenResponse response)
    {
        String typeId = null;
        for (CmisTypeDefinitionType typeDef : response.getTypes().getTypes())
        {
            if ((typeDef instanceof CmisTypePolicyDefinitionType) && (null == typeDef.getParentId()))
            {
                typeId = typeDef.getId();
                break;
            }
        }
        return typeId;
    }

    protected String getAndAssertRepositoryId()
    {
        if (repositoryId == null)
        {
            CmisRepositoryEntryType[] repositoriesResponse = null;
            try
            {
                LOGGER.info("[RepositoryService->getRepositories]");
                repositoriesResponse = getServicesFactory().getRepositoryService().getRepositories(new GetRepositories());
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("No repositories found", repositoriesResponse);
            assertTrue("No repositories found", (repositoriesResponse.length > 0));
            CmisRepositoryEntryType repositoryEntryType = repositoriesResponse[0];
            assertNotNull("Repository entry is NULL", repositoryEntryType);
            repositoryId = repositoryEntryType.getRepositoryId();
            assertNotNull("Repository Id is NULL", repositoryId);
        }
        return repositoryId;
    }

    protected CmisRepositoryInfoType getAndAssertRepositoryInfo()
    {
        GetRepositoryInfoResponse cmisRepositoryInfo = null;
        try
        {
            LOGGER.info("[RepositoryService->getRepositoryInfo]");
            cmisRepositoryInfo = getServicesFactory().getRepositoryService().getRepositoryInfo(new GetRepositoryInfo(getAndAssertRepositoryId(), null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("Repository Info is NULL", cmisRepositoryInfo);
        assertNotNull("Repository Info is NULL", cmisRepositoryInfo.getRepositoryInfo());
        return cmisRepositoryInfo.getRepositoryInfo();

    }

    protected CmisPropertiesType fillProperties(String name, String type)
    {
        CmisPropertiesType properties = new CmisPropertiesType();
        if (name != null)
        {
            CmisPropertyString cmisPropertyName = new CmisPropertyString();
            cmisPropertyName.setPropertyDefinitionId(PROP_NAME);
            cmisPropertyName.setValue(new String[] { name });
            properties.setPropertyString(new CmisPropertyString[] { cmisPropertyName });
        }
        if (type != null)
        {
            CmisPropertyId idProperty = new CmisPropertyId();
            idProperty.setPropertyDefinitionId(PROP_OBJECT_TYPE_ID);
            idProperty.setValue(new String[] { type });
            properties.setPropertyId(new CmisPropertyId[] { idProperty });
        }
        return properties;
    }

    protected String getAndAssertRootFolderId()
    {
        if (rootFolderId == null)
        {
            rootFolderId = getAndAssertRepositoryInfo().getRootFolderId();
            assertNotNull("Root Folder Id is NULL", rootFolderId);
        }
        return rootFolderId;
    }

    protected CmisRepositoryCapabilitiesType getAndAssertCapabilities()
    {
        if (capabilities == null)
        {
            capabilities = getAndAssertRepositoryInfo().getCapabilities();
            assertNotNull("Capabilities are NULL", capabilities);
        }
        return capabilities;
    }

    protected EnumACLPropagation getAndAssertACLPrapagation()
    {
        if (aclPropagation == null)
        {
            CmisRepositoryInfoType repoInfo = getAndAssertRepositoryInfo();
            aclPropagation = repoInfo.getAclCapability() != null ? repoInfo.getAclCapability().getPropagation() : null;
            aclPropagation = aclPropagation != null ? aclPropagation : EnumACLPropagation.repositorydetermined;
            assertNotNull("Root Folder Id is NULL", rootFolderId);
        }

        return aclPropagation;
    }

    protected String getAndAssertDocumentTypeId()
    {
        if (documentTypeId == null)
        {
            try
            {
                documentTypeId = getTypeId(BASE_TYPE_DOCUMENT);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("Document type id is NULL", documentTypeId);
        }
        return documentTypeId;
    }

    protected String getAndAssertFolderTypeId()
    {
        if (folderTypeId == null)
        {
            try
            {
                folderTypeId = getTypeId(BASE_TYPE_FOLDER);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("Folder type id is NULL", folderTypeId);
        }
        return folderTypeId;
    }

    protected String getAndAssertRelationshipTypeId()
    {
        if (relationshipTypeId == null)
        {
            try
            {
                relationshipTypeId = getTypeId(BASE_TYPE_RELATIONSHIP);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("Relationship type id is NULL", relationshipTypeId);
        }
        return relationshipTypeId;
    }

    protected String getAndAssertPolicyTypeId()
    {
        if (policyTypeId == null)
        {
            try
            {
                policyTypeId = getTypeId(BASE_TYPE_POLICY);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
        return policyTypeId;
    }

    protected String getAndAssertPolicyControllableTypeId()
    {
        if (null == policyControllableTypeId)
        {
            CmisTypeDefinitionType typeDefinition = getAndAssertTypeDefinition(getAndAssertDocumentTypeId());
            if (typeDefinition.isControllablePolicy())
            {
                policyControllableTypeId = typeDefinition.getId();
            }
            else
            {
                typeDefinition = getAndAssertTypeDefinition(getAndAssertFolderTypeId());
                policyControllableTypeId = (typeDefinition.isControllablePolicy()) ? (typeDefinition.getId()) : (null);
            }
        }
        return policyControllableTypeId;
    }

    protected CmisTypeDefinitionType getAndAssertTypeDefinition(String typeId)
    {
        assertNotNull("Can't receive Type Definition for invalid Type Id", typeId);
        GetTypeDefinitionResponse response = null;
        try
        {
            LOGGER.info("[RepositoryService->getTypeDefinition]");
            response = getServicesFactory().getRepositoryService().getTypeDefinition(new GetTypeDefinition(getAndAssertRepositoryId(), typeId, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull(response);
        assertNotNull(response.getType());
        assertEquals(typeId, response.getType().getId());
        return response.getType();
    }

    protected CmisTypeDefinitionType getAndAssertRelationshipSourceType()
    {
        if (relationshipSourceType == null)
        {
            getAndAssertRelationshipTypeId();
        }
        return relationshipSourceType;
    }

    protected CmisTypeDefinitionType getAndAssertRelationshipTargetType()
    {
        if (relationshipTargetType == null)
        {
            getAndAssertRelationshipTypeId();
        }
        return relationshipTargetType;
    }

    protected boolean isVersioningAllowed()
    {
        if (documentTypeId == null)
        {
            getAndAssertDocumentTypeId();
        }
        return versioningAllowed;
    }

    protected boolean isContentStreamAllowed()
    {
        if (documentTypeId == null)
        {
            getAndAssertDocumentTypeId();
        }
        return EnumContentStreamAllowed._allowed.equals(contentStreamAllowed.getValue()) || EnumContentStreamAllowed._required.equals(contentStreamAllowed.getValue());
    }

    protected boolean isContentStreamRequired()
    {
        if (documentTypeId == null)
        {
            getAndAssertDocumentTypeId();
        }
        return EnumContentStreamAllowed._required.equals(contentStreamAllowed.getValue());
    }

    protected boolean areRelationshipsSupported()
    {
        if (null == relationshipsSupported)
        {
            relationshipsSupported = null != getBaseRelationshipTypeId(getAndAssertTypeChildren(null, false, 4L, 0L));
        }
        return relationshipsSupported;
    }

    protected boolean arePoliciesSupported()
    {
        if (null == policySupported)
        {
            policySupported = null != getBasePolicyTypeId(getAndAssertTypeChildren(null, false, 4L, 0L));
        }
        return policySupported;
    }

    private static class BaseConditionCalculator
    {
        public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
        {
            return enumeratedType.isCreatable();
        }
    }

    protected String createAndAssertPolicy() throws Exception
    {
        return createAndAssertPolicy(null, null, null, getAndAssertRootFolderId());
    }

    protected String createAndAssertPolicy(String name, String policyTypeId, CmisPropertiesType properties, String folderId) throws Exception
    {
        if (null == properties)
        {
            properties = new CmisPropertiesType();

            CmisPropertyString cmisPropertyString = new CmisPropertyString();
            cmisPropertyString.setPropertyDefinitionId(PROP_NAME);
            cmisPropertyString.setValue(new String[] { name == null ? generateTestPolicyName() : name });
            properties.setPropertyString(new CmisPropertyString[] { cmisPropertyString });

            CmisPropertyId idProperty = new CmisPropertyId();
            idProperty.setPropertyDefinitionId(PROP_OBJECT_TYPE_ID);
            idProperty.setValue(new String[] { policyTypeId == null ? getAndAssertPolicyTypeId() : policyTypeId });
            properties.setPropertyId(new CmisPropertyId[] { idProperty });

        }

        LOGGER.info("[ObjectService->createPolicy]");
        CreatePolicyResponse createPolicy = getServicesFactory().getObjectService().createPolicy(
                new CreatePolicy(getAndAssertRepositoryId(), properties, folderId, null, null, null, null));
        assertNotNull("Create Policy response is undefined", createPolicy);
        assertNotNull("Create Policy response is empty", createPolicy.getObjectId());
        assertNotSame("Create Policy response contains undefined Id", "", createPolicy.getObjectId());
        return createPolicy.getObjectId();
    }

    protected CheckOutResponse checkOutAndAssert(String documentId) throws Exception
    {
        CheckOutResponse checkOutResponse = null;
        try
        {
            LOGGER.info("[VersioningService->checkOut]");
            checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("Check Out response is undefined", checkOutResponse);
        assertNotNull("Private Working Copy Id is undefined", checkOutResponse.getObjectId());
        return checkOutResponse;
    }

    protected CheckInResponse checkInAndAssert(String pwcId, boolean major, CmisPropertiesType properties, CmisContentStreamType contentStream, String checkInComment)
    {
        CheckInResponse checkInResponse = null;
        try
        {
            LOGGER.info("[VersioningService->checkIn]");
            checkInResponse = getServicesFactory().getVersioningService().checkIn(
                    new CheckIn(getAndAssertRepositoryId(), pwcId, major, properties, contentStream, checkInComment, null, null, null, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("Check In response is undefined", checkInResponse);
        assertNotNull("Checked In Document Id is undefined", checkInResponse.getObjectId());
        return checkInResponse;
    }

    protected CmisObjectType[] getAndAssertAllVersions(String versionSeriesId, String filter, boolean includeAllowableActions) throws Exception
    {
        CmisObjectType[] response = null;
        try
        {
            LOGGER.info("[VersioningService->getAllVersions]");
            response = getServicesFactory().getVersioningService().getAllVersions(
                    new GetAllVersions(getAndAssertRepositoryId(), versionSeriesId, filter, includeAllowableActions, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("Get All Versions response is undefined", response);
        assertTrue("Get All Versions response is empty", response.length > 0);
        return response;
    }

    protected void getPropertiesUsingCredentials(String objectId, String username, String password) throws Exception
    {
        GetPropertiesResponse response = null;
        LOGGER.info("[ObjectService->getProperties]");
        response = getServicesFactory().getObjectService(username, password).getProperties(new GetProperties(getAndAssertRepositoryId(), objectId, "*", null));
        assertTrue("No properties were returned", response != null && response.getProperties() != null);
        assertNotNull("No 'Name' property was returned", getStringProperty(response.getProperties(), PROP_NAME));
    }

    protected String updatePropertiesUsingCredentials(String objectId, String username, String password) throws Exception
    {
        String documentNameNew = generateTestFileName("_new");
        CmisPropertiesType properties = fillProperties(documentNameNew, null);
        LOGGER.info("[ObjectService->updateProperties]");
        objectId = getServicesFactory().getObjectService(username, password).updateProperties(new UpdateProperties(getAndAssertRepositoryId(), objectId, null, properties, null))
                .getObjectId();
        assertNotNull(objectId);
        return objectId;
    }

    protected String searchAndAssertDocumentTypeWithNoContentAlowed() throws Exception
    {
        String baseDocumentTypeId = getBaseDocumentTypeId(getAndAssertTypeChildren(null, false, 0L, 0L));
        return enumerateAndAssertTypesHierarchy(getAndAssertTypeDescendants(baseDocumentTypeId, -1, true), new BaseConditionCalculator()
        {
            @Override
            public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
            {
                return (null != enumeratedType) && enumeratedType.isCreatable()
                        && !EnumContentStreamAllowed.required.equals(((CmisTypeDocumentDefinitionType) enumeratedType).getContentStreamAllowed());
            }
        }, true);
    }

    protected String searchAndAssertDocumentTypeWithContentRequired() throws Exception
    {
        String baseDocumentTypeId = getBaseDocumentTypeId(getAndAssertTypeChildren(null, false, 0L, 0L));
        return enumerateAndAssertTypesHierarchy(getAndAssertTypeDescendants(baseDocumentTypeId, -1, true), new BaseConditionCalculator()
        {
            @Override
            public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
            {
                return (null != enumeratedType) && enumeratedType.isCreatable()
                        && EnumContentStreamAllowed.required.equals(((CmisTypeDocumentDefinitionType) enumeratedType).getContentStreamAllowed());
            }
        }, true);
    }

    protected String searchAndAssertDocumentTypeWithContentNotAllowed() throws Exception
    {
        String baseDocumentTypeId = getBaseDocumentTypeId(getAndAssertTypeChildren(null, false, 0L, 0L));
        return enumerateAndAssertTypesHierarchy(getAndAssertTypeDescendants(baseDocumentTypeId, -1, true), new BaseConditionCalculator()
        {
            @Override
            public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
            {
                return (null != enumeratedType) && enumeratedType.isCreatable()
                        && EnumContentStreamAllowed.notallowed.equals(((CmisTypeDocumentDefinitionType) enumeratedType).getContentStreamAllowed());
            }
        }, true);
    }

    protected class RenditionData
    {
        private String filter;
        private String[] expectedKinds;
        private String[] expectedMimetypes;

        public String getFilter()
        {
            return filter;
        }

        public String[] getExpectedKinds()
        {
            return expectedKinds;
        }

        public String[] getExpectedMimetypes()
        {
            return expectedMimetypes;
        }

        public RenditionData(String[] expectedKinds, String[] expectedMimetypes)
        {
            this.filter = createFilter(expectedKinds, expectedMimetypes);
            this.expectedKinds = expectedKinds;
            this.expectedMimetypes = expectedMimetypes;
        }

        public RenditionData(String filter, String[] expectedKinds, String[] expectedMimetypes)
        {
            this.filter = filter;
            this.expectedKinds = expectedKinds;
            this.expectedMimetypes = expectedMimetypes;
        }
    }

    @SuppressWarnings("static-access")
    public void setAclUsername(String aclUsername)
    {
        if (aclUsername != null && aclUsername.length() > 0)
        {
            this.aclUsername = aclUsername;
        }
    }

    @SuppressWarnings("static-access")
    public void setAclPassword(String aclPassword)
    {
        if (aclPassword != null && aclPassword.length() > 0)
        {
            this.aclPassword = aclPassword;
        }
    }

    @SuppressWarnings("static-access")
    public void setAclPrincipalId(String aclPrincipalId)
    {
        if (aclPrincipalId != null && aclPrincipalId.length() > 0)
        {
            this.aclPrincipalId = aclPrincipalId;
        }
    }

    public String getAclUsername()
    {
        return aclUsername;
    }

    public String getAclPassword()
    {
        return aclPassword;
    }

    public String getAclPrincipalId()
    {
        return aclPrincipalId;
    }

}
