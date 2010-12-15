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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.alfresco.repo.cmis.ws.AddObjectToFolder;
import org.alfresco.repo.cmis.ws.CancelCheckOut;
import org.alfresco.repo.cmis.ws.CheckIn;
import org.alfresco.repo.cmis.ws.CheckInResponse;
import org.alfresco.repo.cmis.ws.CheckOut;
import org.alfresco.repo.cmis.ws.CheckOutResponse;
import org.alfresco.repo.cmis.ws.CmisContentStreamType;
import org.alfresco.repo.cmis.ws.CmisFaultType;
import org.alfresco.repo.cmis.ws.CmisObjectInFolderContainerType;
import org.alfresco.repo.cmis.ws.CmisObjectInFolderListType;
import org.alfresco.repo.cmis.ws.CmisObjectInFolderType;
import org.alfresco.repo.cmis.ws.CmisObjectParentsType;
import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.CmisPropertyDateTime;
import org.alfresco.repo.cmis.ws.CmisPropertyId;
import org.alfresco.repo.cmis.ws.CmisPropertyString;
import org.alfresco.repo.cmis.ws.DeleteObject;
import org.alfresco.repo.cmis.ws.DeleteTree;
import org.alfresco.repo.cmis.ws.EnumCapabilityRendition;
import org.alfresco.repo.cmis.ws.EnumIncludeRelationships;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.EnumUnfileObject;
import org.alfresco.repo.cmis.ws.EnumVersioningState;
import org.alfresco.repo.cmis.ws.GetCheckedOutDocs;
import org.alfresco.repo.cmis.ws.GetCheckedOutDocsResponse;
import org.alfresco.repo.cmis.ws.GetChildren;
import org.alfresco.repo.cmis.ws.GetChildrenResponse;
import org.alfresco.repo.cmis.ws.GetDescendants;
import org.alfresco.repo.cmis.ws.GetFolderParent;
import org.alfresco.repo.cmis.ws.GetFolderParentResponse;
import org.alfresco.repo.cmis.ws.GetFolderTree;
import org.alfresco.repo.cmis.ws.GetObjectByPath;
import org.alfresco.repo.cmis.ws.GetObjectByPathResponse;
import org.alfresco.repo.cmis.ws.GetObjectParents;
import org.alfresco.repo.cmis.ws.NavigationServicePort;
import org.alfresco.repo.cmis.ws.NavigationServicePortBindingStub;
import org.alfresco.repo.cmis.ws.VersioningServicePortBindingStub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Navigation Service
 */
public class CmisNavigationServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisNavigationServiceClient.class);

    private static final int TEST_TREE_DEPTH = 3;

    private static final int PAGING_STEP = 3;

    private static final int CHILDREN_TEST_OBJECTS_AMOUNT = 6;

    private static final String INVALID_REPOSITORY_ID = "Wrong Repository Id";
    private static final String INVALID_FILTER = "Name, *eationDa*";

    private static final String OBJECT_IS_NULL_MESSAGE = "Some Object from response is null";
    private static final String INVALID_WRONG_FILTER_PROCESSING_MESSAGE = "Wrong filter was not processed as correct filter";
    private static final String INVALID_OBJECTS_INTEGRITY_MESSAGE = "Not all Objects or odd Objects were returned in Response from ";
    private static final String INVALID_OBJECTS_LAYERS_DESCRIPTION_MESSAGE = "Objects layers description and actual Objects list are not compliant";

    private static final String TEST_FOLDER_NAME_PATTERN = "Test Folder(%d.%d)";
    private static final String TEST_DOCUMENT_NAME_PATTERN = "Test Document(%d.%d).txt";

    private String folderId;
    private String documentId;
    private EnumVersioningState versioningState;

    public CmisNavigationServiceClient()
    {
        super();
    }

    public CmisNavigationServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    /**
     * Initializes Navigation Service client
     */
    @Override
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        versioningState = isVersioningAllowed() ? EnumVersioningState.major : EnumVersioningState.none;
        folderId = createAndAssertFolder();
        documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folderId, null, TEST_CONTENT, null);
        getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
    }

    /**
     * Invokes all methods in Navigation Service
     */
    @Override
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        NavigationServicePort navigationServicePort = getServicesFactory().getNavigationService(getProxyUrl() + getService().getPath());

        navigationServicePort.getDescendants(new GetDescendants(getAndAssertRepositoryId(), getAndAssertRootFolderId(), BigInteger.valueOf(2), "*", Boolean.FALSE,
                EnumIncludeRelationships.both, null, null, null));

        GetFolderParent getFolderParent = new GetFolderParent();
        getFolderParent.setRepositoryId(getAndAssertRepositoryId());
        getFolderParent.setFolderId(folderId);
        getFolderParent.setFilter("*");
        navigationServicePort.getFolderParent(getFolderParent);

        GetObjectParents getObjectParents = new GetObjectParents();
        getObjectParents.setRepositoryId(getAndAssertRepositoryId());
        getObjectParents.setObjectId(documentId);
        getObjectParents.setFilter("*");
        navigationServicePort.getObjectParents(getObjectParents);

        GetCheckedOutDocs getCheckedoutDocs = new GetCheckedOutDocs();
        getCheckedoutDocs.setRepositoryId(getAndAssertRepositoryId());
        getCheckedoutDocs.setFolderId(folderId);
        getCheckedoutDocs.setFilter("*");
        getCheckedoutDocs.setMaxItems(BigInteger.valueOf(0));
        getCheckedoutDocs.setSkipCount(BigInteger.valueOf(0));
        getCheckedoutDocs.setIncludeAllowableActions(true);
        getCheckedoutDocs.setIncludeRelationships(EnumIncludeRelationships.both);
        navigationServicePort.getCheckedOutDocs(getCheckedoutDocs);

        GetChildren getChildren = new GetChildren();
        getChildren.setRepositoryId(getAndAssertRepositoryId());
        getChildren.setFolderId(getAndAssertRootFolderId());
        getChildren.setFilter("*");
        getChildren.setIncludeAllowableActions(false);
        getChildren.setIncludeRelationships(EnumIncludeRelationships.both);
        getChildren.setMaxItems(BigInteger.valueOf(0));
        getChildren.setSkipCount(BigInteger.valueOf(0));
        navigationServicePort.getChildren(getChildren);
    }

    /**
     * Remove initial data
     */
    @Override
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
        getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, true, EnumUnfileObject.delete, true, null));
    }

    /**
     * Main method to start client
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-context.xml");
        AbstractServiceClient client = (CmisNavigationServiceClient) applicationContext.getBean("cmisNavigationServiceClient");
        try
        {
            client.initialize();
            client.invoke();
            client.release();
        }
        catch (Exception e)
        {
            LOGGER.error("Some error occured during client running. Exception message: " + e.getMessage());
        }
    }

    @Override
    protected void onSetUp() throws Exception
    {
        versioningState = isVersioningAllowed() ? EnumVersioningState.major : EnumVersioningState.none;
        folderId = createAndAssertFolder();
        documentId = createAndAssertDocument();
        getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, true, EnumUnfileObject.delete, true, null));
        super.onTearDown();
    }

    public void testFoldersTreeReceiving() throws Exception
    {
        TreeNode<String> expectedTree = createObjectsTree(folderId, versioningState, EnumTypesOfFileableObjects.FOLDERS, TEST_TREE_DEPTH, 1, 5, TEST_TREE_DEPTH);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        LOGGER.info("[NavigationService->getFolderTree]");
        CmisObjectInFolderContainerType[] foldersTreeResponse = navigationService.getFolderTree(new GetFolderTree(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(-1),
                "*", false, EnumIncludeRelationships.none, null, null, null));

        assertNotNull("Folder tree response is NULL", foldersTreeResponse);
        assertObjectsTree(foldersTreeResponse, expectedTree);
    }

    public void testDepthLimitedFoldersTreeReceiving() throws Exception
    {
        TreeNode<String> expectedTree = createObjectsTree(folderId, versioningState, EnumTypesOfFileableObjects.FOLDERS, TEST_TREE_DEPTH, 1, 5, 2);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        LOGGER.info("[NavigationService->getFolderTree]");
        CmisObjectInFolderContainerType[] foldersTreeResponse = navigationService.getFolderTree(new GetFolderTree(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(2), "*",
                false, EnumIncludeRelationships.none, null, null, null));

        assertNotNull("Folder tree response is NULL", foldersTreeResponse);
        assertObjectsTree(foldersTreeResponse, expectedTree);
    }

    public void testFilteredFoldersTreeReceiving() throws Exception
    {
        TreeNode<String> expectedTree = createObjectsTree(folderId, versioningState, EnumTypesOfFileableObjects.FOLDERS, 3, 1, 3, 3);
        String filter = PROP_NAME + "," + PROP_OBJECT_ID + "," + PROP_CREATION_DATE;
        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        LOGGER.info("[NavigationService->getFolderTree]");
        CmisObjectInFolderContainerType[] foldersTreeResponse = navigationService.getFolderTree(new GetFolderTree(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(3),
                filter, false, EnumIncludeRelationships.none, null, null, null));

        assertNotNull("Folder tree response is NULL", foldersTreeResponse);
        assertObjectsTree(foldersTreeResponse, expectedTree);
        List<CmisObjectInFolderContainerType> resultList = convertTreeToObjectsList(foldersTreeResponse);

        for (CmisObjectInFolderContainerType object : resultList)
        {
            CmisPropertiesType properties = object.getObjectInFolder().getObject().getProperties();

            assertNull("Not expected properties were returned", properties.getPropertyBoolean());
            assertNull("Not expected properties were returned", properties.getPropertyDecimal());
            assertNull("Not expected properties were returned", properties.getPropertyHtml());
            assertNull("Not expected properties were returned", properties.getPropertyInteger());
            assertNull("Not expected properties were returned", properties.getPropertyUri());

            assertNotNull("Expected properties were not returned", properties.getPropertyId());
            assertNotNull("Expected properties were not returned", properties.getPropertyDateTime());
            assertNotNull("Expected properties were not returned", properties.getPropertyString());

            assertEquals("Expected property was not returned", 1, properties.getPropertyId().length);
            assertEquals("Expected property was not returned", 1, properties.getPropertyDateTime().length);
            assertEquals("Expected property was not returned", 1, properties.getPropertyString().length);

            getAndAssertIdPropertyValue(properties, PROP_OBJECT_ID);
            assertNotNull("Expected property was not returned", getStringProperty(properties, PROP_NAME));
            getAndAssertDateTimePropertyValue(properties, PROP_CREATION_DATE);
        }
    }

    public void testDescendantsReceiving() throws Exception
    {
        TreeNode<String> expectedTree = createObjectsTree(folderId, versioningState, EnumTypesOfFileableObjects.BOTH, TEST_TREE_DEPTH, 1, 5, TEST_TREE_DEPTH);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        LOGGER.info("[NavigationService->getDescendants]");
        CmisObjectInFolderContainerType[] descendantsResponse = navigationService.getDescendants(new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(-1),
                "*", false, EnumIncludeRelationships.none, null, null, null));

        assertNotNull("GetDescendants response is NULL", descendantsResponse);
        assertObjectsTree(descendantsResponse, expectedTree);
    }

    public void testDescendantsReceivingIncludeRenditions() throws Exception
    {
        if (EnumCapabilityRendition.read.equals(getAndAssertCapabilities().getCapabilityRenditions()))
        {
            String documentId = createAndAssertDocument();
            List<RenditionData> testRenditions = getTestRenditions(documentId);
            if (testRenditions != null)
            {
                TreeNode<String> expectedTree = createObjectsTree(folderId, versioningState, EnumTypesOfFileableObjects.BOTH, TEST_TREE_DEPTH, 1, 5, TEST_TREE_DEPTH);
                for (RenditionData testRendition : testRenditions)
                {
                    NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
                    LOGGER.info("[NavigationService->getDescendants]");
                    CmisObjectInFolderContainerType[] descendantsResponse = navigationService.getDescendants(new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger
                            .valueOf(-1), "*", false, EnumIncludeRelationships.none, testRendition.getFilter(), null, null));

                    assertNotNull("GetDescendants response is NULL", descendantsResponse);
                    assertObjectsTree(descendantsResponse, expectedTree);
                    List<CmisObjectInFolderContainerType> objectsList = convertTreeToObjectsList(descendantsResponse);
                    for (CmisObjectInFolderContainerType objectInFolder : objectsList)
                    {
                        assertRenditions(objectInFolder.getObjectInFolder().getObject(), testRendition.getFilter(), testRendition.getExpectedKinds(), testRendition
                                .getExpectedMimetypes());
                    }
                }
            }
            else
            {
                LOGGER.info("testDescendantsReceivingIncludeRenditions was skipped: No renditions found for document type");
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
        }
        else
        {
            LOGGER.info("testDescendantsReceivingIncludeRenditions was skipped: Renditions are not supported");
        }

    }

    public void testDepthLimitedDescendantsReceiving() throws Exception
    {
        TreeNode<String> expectedTree = createObjectsTree(folderId, versioningState, EnumTypesOfFileableObjects.BOTH, TEST_TREE_DEPTH, 1, 4, 2);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        LOGGER.info("[NavigationService->getDescendants]");
        CmisObjectInFolderContainerType[] descendantsResponse = navigationService.getDescendants(new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(2),
                "*", false, EnumIncludeRelationships.none, null, null, null));

        assertNotNull("GetDescendants response is NULL", descendantsResponse);
        assertObjectsTree(descendantsResponse, expectedTree);
    }

    public void testFilteredDescendantsReceiving() throws Exception
    {
        TreeNode<String> expectedTree = createObjectsTree(folderId, versioningState, EnumTypesOfFileableObjects.BOTH, TEST_TREE_DEPTH, 1, 3, TEST_TREE_DEPTH);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        String filter = PROP_NAME + "," + PROP_OBJECT_ID + "," + PROP_CREATION_DATE;
        LOGGER.info("[NavigationService->getDescendants]");
        CmisObjectInFolderContainerType[] descendantsResponse = navigationService.getDescendants(new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(-1),
                filter, false, EnumIncludeRelationships.none, null, null, null));

        assertNotNull("GetDescendants response is NULL", descendantsResponse);
        assertObjectsTree(descendantsResponse, expectedTree);
        List<CmisObjectInFolderContainerType> resultList = convertTreeToObjectsList(descendantsResponse);

        for (CmisObjectInFolderContainerType object : resultList)
        {
            CmisPropertiesType properties = object.getObjectInFolder().getObject().getProperties();

            assertNull("Not expected properties were returned", properties.getPropertyBoolean());
            assertNull("Not expected properties were returned", properties.getPropertyDecimal());
            assertNull("Not expected properties were returned", properties.getPropertyHtml());
            assertNull("Not expected properties were returned", properties.getPropertyInteger());
            assertNull("Not expected properties were returned", properties.getPropertyUri());

            assertNotNull("Expected properties were not returned", properties.getPropertyId());
            assertNotNull("Expected properties were not returned", properties.getPropertyDateTime());
            assertNotNull("Expected properties were not returned", properties.getPropertyString());

            assertEquals("Expected property was not returned", 1, properties.getPropertyId().length);
            assertEquals("Expected property was not returned", 1, properties.getPropertyDateTime().length);
            assertEquals("Expected property was not returned", 1, properties.getPropertyString().length);

            getAndAssertIdPropertyValue(properties, PROP_OBJECT_ID);
            assertNotNull("Expected property was not returned", getStringProperty(properties, PROP_NAME));
            getAndAssertDateTimePropertyValue(properties, PROP_CREATION_DATE);
        }
    }

    public void testGetDescendantsMultifiled() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            CmisObjectInFolderContainerType[] descendantsResponse = null;
            NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
            String documentId = createAndAssertDocument();
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId, false, null));

            try
            {
                LOGGER.info("[NavigationService->getDescendants]");
                descendantsResponse = navigationService.getDescendants(new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(-1), "*", false,
                        EnumIncludeRelationships.none, null, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetDescendants response is NULL", descendantsResponse);
            assertTrue("GetDescendants response is empty", descendantsResponse.length > 0);
            boolean found = false;
            for (CmisObjectInFolderContainerType objectType : descendantsResponse)
            {
                assertTrue("Object in descedants response is null", objectType != null && objectType.getObjectInFolder() != null
                        && objectType.getObjectInFolder().getObject() != null);
                CmisPropertiesType properties = objectType.getObjectInFolder().getObject().getProperties();
                if (documentId.equals(getIdProperty(properties, PROP_OBJECT_ID)))
                {
                    found = true;
                }
            }
            assertTrue("Multifiled object was not found in response", found);
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
        else
        {
            LOGGER.warn("testGetDescendantsMultifiled was skipped: Multifiling isn't supported");
        }
    }

    public void testGetDescendantsVersionSpecificFiling() throws Exception
    {
        if (isVersioningAllowed() && getAndAssertCapabilities().isCapabilityVersionSpecificFiling())
        {
            CmisObjectInFolderContainerType[] descendantsResponse = null;
            String documentId = createAndAssertDocument();
            VersioningServicePortBindingStub versioningService = getServicesFactory().getVersioningService();
            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = versioningService.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
            LOGGER.info("[VersioningService->checkIn]");
            CheckInResponse checkInResponse = versioningService.checkIn(new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getObjectId(), true, new CmisPropertiesType(),
                    new CmisContentStreamType(BigInteger.valueOf(0), "text/plain", generateTestFileName(), "Test content".getBytes(), null), "", null, null, null, null));
            LOGGER.info("[VersioningService->checkOut]");
            checkOutResponse = versioningService.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
            LOGGER.info("[VersioningService->checkIn]");
            versioningService.checkIn(new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getObjectId(), true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger
                    .valueOf(0), "text/plain", generateTestFileName(), "Test content".getBytes(), null), "", null, null, null, null));

            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(
                    new AddObjectToFolder(getAndAssertRepositoryId(), checkInResponse.getObjectId(), folderId, true, null));

            try
            {
                LOGGER.info("[NavigationService->getDescendants]");
                descendantsResponse = getServicesFactory().getNavigationService().getDescendants(
                        new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(-1), "*", false, EnumIncludeRelationships.none, null, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetDescendants response is NULL", descendantsResponse);
            assertTrue("GetDescendants response is empty", descendantsResponse.length > 0);
            boolean found = false;
            for (CmisObjectInFolderContainerType objectType : descendantsResponse)
            {
                assertTrue("Object in descedants response is null", objectType != null && objectType.getObjectInFolder() != null
                        && objectType.getObjectInFolder().getObject() != null);
                CmisPropertiesType properties = objectType.getObjectInFolder().getObject().getProperties();
                if (!found && checkInResponse.getObjectId().equals(getIdProperty(properties, PROP_OBJECT_ID)))
                {
                    found = true;
                    break;
                }
            }
            assertTrue("Specific version of object was not found in response", found);
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
        else
        {
            LOGGER.warn("testGetDescendantsVersionSpecificFiling was skipped: Versioning or VersionSpecificFiling isn't supported");
        }
    }

    public void testChildrenReceiving() throws Exception
    {
        TreeNode<String> expectedTree = createObjectsTree(folderId, versioningState, EnumTypesOfFileableObjects.BOTH, 2, 1, CHILDREN_TEST_OBJECTS_AMOUNT, 2);
        Set<String> expectedObjects = expectedTree.getChildren().keySet();
        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();

        LOGGER.info("[NavigationService->getChildren]");
        GetChildrenResponse childrenResponse = navigationService.getChildren(new GetChildren(getAndAssertRepositoryId(), folderId, "*", null, false, EnumIncludeRelationships.none,
                null, null, BigInteger.valueOf(0), BigInteger.valueOf(0), null));
        assertTrue("GetChildren response is NULL", childrenResponse.getObjects() != null && childrenResponse.getObjects().getObjects() != null);
        assertObjectsFromResponse(childrenResponse.getObjects().getObjects(), 0, expectedObjects.size());
        assertChildren(expectedObjects, childrenResponse.getObjects().getObjects(), true, "GetChildren service");
    }

    public void testChildrenReceivingIncludeRenditions() throws Exception
    {
        if (EnumCapabilityRendition.read.equals(getAndAssertCapabilities().getCapabilityRenditions()))
        {
            String documentId = createAndAssertDocument();
            List<RenditionData> testRenditions = getTestRenditions(documentId);
            if (testRenditions != null)
            {
                TreeNode<String> expectedTree = createObjectsTree(folderId, versioningState, EnumTypesOfFileableObjects.BOTH, 2, 1, CHILDREN_TEST_OBJECTS_AMOUNT, 2);
                Set<String> expectedObjects = expectedTree.getChildren().keySet();
                for (RenditionData testRendition : testRenditions)
                {
                    NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
                    LOGGER.info("[NavigationService->getChildren]");
                    GetChildrenResponse childrenResponse = navigationService.getChildren(new GetChildren(getAndAssertRepositoryId(), folderId, "*", null, false,
                            EnumIncludeRelationships.none, testRendition.getFilter(), null, BigInteger.valueOf(0), BigInteger.valueOf(0), null));
                    assertTrue("GetChildren response is NULL", childrenResponse.getObjects() != null && childrenResponse.getObjects().getObjects() != null);
                    assertObjectsFromResponse(childrenResponse.getObjects().getObjects(), 0, expectedObjects.size());
                    assertChildren(expectedObjects, childrenResponse.getObjects().getObjects(), true, "GetChildren service");
                    for (CmisObjectInFolderType objectInFolder : childrenResponse.getObjects().getObjects())
                    {
                        assertRenditions(objectInFolder.getObject(), testRendition.getFilter(), testRendition.getExpectedKinds(), testRendition.getExpectedMimetypes());
                    }
                }
            }
            else
            {
                LOGGER.info("testChildrenReceivingIncludeRenditions was skipped: No renditions found for document type");
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
        }
        else
        {
            LOGGER.info("testChildrenReceivingIncludeRenditions was skipped: Renditions are not supported");
        }

    }

    public void testGetChildrenPaigingFunctionality() throws Exception
    {
        TreeNode<String> expectedTree = createObjectsTree(folderId, versioningState, EnumTypesOfFileableObjects.BOTH, 1, CHILDREN_TEST_OBJECTS_AMOUNT,
                CHILDREN_TEST_OBJECTS_AMOUNT, 1);
        Set<String> expectedObjects = expectedTree.getChildren().keySet();
        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();

        Set<CmisObjectInFolderType> actualChildrenResponse = new HashSet<CmisObjectInFolderType>();
        int skipCount = PAGING_STEP;
        GetChildrenResponse childrenResponse;

        do
        {
            LOGGER.info("[NavigationService->getChildren]");
            childrenResponse = navigationService.getChildren(new GetChildren(getAndAssertRepositoryId(), folderId, "*", null, false, EnumIncludeRelationships.none, null, null,
                    BigInteger.valueOf(PAGING_STEP), BigInteger.valueOf(skipCount - PAGING_STEP), null));
            assertNotNull("GetChildren Response with Paging is null", childrenResponse);
            assertNotNull("Returned Response Objects are null", childrenResponse.getObjects());
            assertNotNull("Returned Response Objects are null", childrenResponse.getObjects().getObjects());
            assertObjectsFromResponse(childrenResponse.getObjects().getObjects(), 0, childrenResponse.getObjects().getObjects().length);

            assertTrue("Paging for GetChildren service works wrongly", (childrenResponse.getObjects().getObjects().length == PAGING_STEP)
                    || (childrenResponse.getObjects().getObjects().length == 1));
            actualChildrenResponse.addAll(Arrays.asList(childrenResponse.getObjects().getObjects()));

            skipCount += PAGING_STEP;
        } while (childrenResponse.getObjects().isHasMoreItems());

        assertChildren(expectedObjects, actualChildrenResponse.toArray(new CmisObjectInFolderType[actualChildrenResponse.size()]), true, "GetChildren service with paging");
    }

    public void testGetChildrenVersionSpecificFiling() throws Exception
    {
        if (isVersioningAllowed() && getAndAssertCapabilities().isCapabilityVersionSpecificFiling())
        {
            GetChildrenResponse getChildrenResponse = null;
            String documentId = createAndAssertDocument();
            VersioningServicePortBindingStub versioningService = getServicesFactory().getVersioningService();
            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = versioningService.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
            LOGGER.info("[VersioningService->checkIn]");
            CheckInResponse checkInResponse = versioningService.checkIn(new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getObjectId(), true, new CmisPropertiesType(),
                    new CmisContentStreamType(BigInteger.valueOf(0), "text/plain", generateTestFileName(), "Test content".getBytes(), null), "", null, null, null, null));
            LOGGER.info("[VersioningService->checkOut]");
            checkOutResponse = versioningService.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
            LOGGER.info("[VersioningService->checkIn]");
            versioningService.checkIn(new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getObjectId(), true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger
                    .valueOf(0), "text/plain", generateTestFileName(), "Test content".getBytes(), null), "", null, null, null, null));

            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(
                    new AddObjectToFolder(getAndAssertRepositoryId(), checkInResponse.getObjectId(), folderId, true, null));

            try
            {
                LOGGER.info("[NavigationService->getChildren]");
                getChildrenResponse = getServicesFactory().getNavigationService().getChildren(
                        new GetChildren(getAndAssertRepositoryId(), folderId, "*", null, true, EnumIncludeRelationships.both, null, null, BigInteger.valueOf(0), BigInteger
                                .valueOf(0), null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetChildren response is NULL", getChildrenResponse);
            assertTrue("GetChildren response is empty", getChildrenResponse.getObjects() != null && getChildrenResponse.getObjects().getObjects() != null
                    && getChildrenResponse.getObjects().getObjects().length > 0);
            boolean found = false;
            for (CmisObjectInFolderType objectType : getChildrenResponse.getObjects().getObjects())
            {
                if (!found && checkInResponse.getObjectId().equals(getIdProperty(objectType.getObject().getProperties(), PROP_OBJECT_ID)))
                {
                    found = true;
                    break;
                }
            }
            assertTrue("Specific version of object was not found in response", found);
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
        else
        {
            LOGGER.warn("testGetChildrenVersionSpecificFiling was skipped: Versioning or VersionSpecificFiling isn't supported");
        }
    }

    public void testFolderParentReceiving() throws Exception
    {
        String childFolder = createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), folderId, null);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        LOGGER.info("[NavigationService->getFolderParent]");
        GetFolderParentResponse parentsResponse = navigationService.getFolderParent(new GetFolderParent(getAndAssertRepositoryId(), childFolder, "*", null));
        assertNotNull("GetParents Response is null", parentsResponse);
        assertObjectsFromResponse(new CmisObjectType[] { parentsResponse.getObject() }, 0, 1);
        assertEquals(folderId, getAndAssertIdPropertyValue(parentsResponse.getObject().getProperties(), PROP_OBJECT_ID));
    }

    public void testGetObjectParents() throws Exception
    {
        String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folderId, null, TEST_CONTENT, versioningState);

        CmisObjectParentsType[] parentsResponse = null;
        try
        {
            LOGGER.info("[NavigationService->getObjectParents]");
            parentsResponse = getServicesFactory().getNavigationService().getObjectParents(
                    new GetObjectParents(getAndAssertRepositoryId(), documentId, "*", false, EnumIncludeRelationships.none, null, null, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("GetObjectParents response is NULL", parentsResponse);
        assertObjectsFromResponse(parentsResponse, 0, 1);
        assertEquals(folderId, getAndAssertIdPropertyValue(parentsResponse[0].getObject().getProperties(), PROP_OBJECT_ID));
        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
    }

    public void testGetObjectParentsMultifiled() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            String documentId = createAndAssertDocument();
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId, true, null));
            CmisObjectParentsType[] parentsResponse = null;
            try
            {
                LOGGER.info("[NavigationService->getObjectParents]");
                parentsResponse = getServicesFactory().getNavigationService().getObjectParents(
                        new GetObjectParents(getAndAssertRepositoryId(), documentId, "*", false, EnumIncludeRelationships.none, null, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetObjectParents response is NULL", parentsResponse);
            assertObjectsFromResponse(parentsResponse, 0, 2);
            assertTrue((folderId.equals(getAndAssertIdPropertyValue(parentsResponse[0].getObject().getProperties(), PROP_OBJECT_ID)) || (folderId
                    .equals(getAndAssertIdPropertyValue(parentsResponse[1].getObject().getProperties(), PROP_OBJECT_ID))))
                    && ((getAndAssertRootFolderId().equals(getAndAssertIdPropertyValue(parentsResponse[0].getObject().getProperties(), PROP_OBJECT_ID)) || (getAndAssertRootFolderId()
                            .equals(getAndAssertIdPropertyValue(parentsResponse[1].getObject().getProperties(), PROP_OBJECT_ID))))));
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
        else
        {
            LOGGER.warn("testGetObjectParentMultifiled was skipped: Multifiling isn't supported");
        }
    }

    public void testPathSegments() throws Exception
    {
        String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folderId, null, TEST_CONTENT, versioningState);

        LOGGER.info("[NavigationService->getChildren]");
        GetChildrenResponse childrenResponse = getServicesFactory().getNavigationService().getChildren(
                new GetChildren(getAndAssertRepositoryId(), getAndAssertRootFolderId(), "*", null, false, EnumIncludeRelationships.none, null, true, BigInteger.valueOf(200),
                        BigInteger.valueOf(0), null));
        assertTrue("GetChildren response is NULL", childrenResponse.getObjects() != null && childrenResponse.getObjects().getObjects() != null);

        String folderPathSegment = null;
        for (CmisObjectInFolderType objectInFolder : childrenResponse.getObjects().getObjects())
        {
            assertNotNull("CmisObjectInFolder is NULL", objectInFolder);
            assertNotNull("pathSegment is NULL", objectInFolder.getPathSegment());
            assertNotNull("CmisObject is NULL", objectInFolder.getObject());
            if (folderId.equals(getIdProperty(objectInFolder.getObject().getProperties(), PROP_OBJECT_ID)))
            {
                folderPathSegment = objectInFolder.getPathSegment();
            }
        }

        assertNotNull("pathSegment is NULL", folderPathSegment);

        LOGGER.info("[NavigationService->getObjectParents]");
        CmisObjectParentsType[] parentsResponse = null;
        parentsResponse = getServicesFactory().getNavigationService().getObjectParents(
                new GetObjectParents(getAndAssertRepositoryId(), documentId, "*", false, EnumIncludeRelationships.none, null, true, null));
        assertNotNull("GetObjectParents response is NULL", parentsResponse);
        assertTrue("GetObjectParents response is empty", parentsResponse.length > 0);
        String documentPathSegment = parentsResponse[0].getRelativePathSegment();
        assertNotNull("pathSegment is NULL", documentPathSegment);

        String folderPath = "/" + folderPathSegment;
        String documentPath = "/" + folderPathSegment + "/" + documentPathSegment;

        LOGGER.info("[ObjectService->getObjectByPath]");
        GetObjectByPathResponse objectByPathResponse = getServicesFactory().getObjectService().getObjectByPath(
                new GetObjectByPath(getAndAssertRepositoryId(), folderPath, PROP_OBJECT_ID, false, EnumIncludeRelationships.none, null, null, null, null));
        assertTrue("GetObjectByPath response is NULL", objectByPathResponse != null && objectByPathResponse.getObject() != null);
        String folderIdByPath = getAndAssertIdPropertyValue(objectByPathResponse.getObject().getProperties(), PROP_OBJECT_ID);
        assertEquals("Returned by path objectId is not equal to expected", folderId, folderIdByPath);

        LOGGER.info("[ObjectService->getObjectByPath]");
        objectByPathResponse = getServicesFactory().getObjectService().getObjectByPath(
                new GetObjectByPath(getAndAssertRepositoryId(), documentPath, PROP_OBJECT_ID, false, EnumIncludeRelationships.none, null, null, null, null));
        assertTrue("GetObjectByPath response is NULL", objectByPathResponse != null && objectByPathResponse.getObject() != null);
        String documentIdByPath = getAndAssertIdPropertyValue(objectByPathResponse.getObject().getProperties(), PROP_OBJECT_ID);
        assertEquals("Returned by path objectId is not equal to expected", documentId, documentIdByPath);

        LOGGER.info("[NavigationService->getDescedants]");
        CmisObjectInFolderContainerType[] descendantsResponse = getServicesFactory().getNavigationService().getDescendants(
                new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(2), "*", false, EnumIncludeRelationships.none, null, true, null));
        assertNotNull("GetDescendants response is NULL", descendantsResponse);

        for (CmisObjectInFolderContainerType objectInFolderContainer : descendantsResponse)
        {
            assertNotNull("CmisObjectInFolderContainer is NULL", objectInFolderContainer);
            assertNotNull("CmisObjectInFolder is NULL", objectInFolderContainer.getObjectInFolder());
            assertNotNull("pathSegment is NULL", objectInFolderContainer.getObjectInFolder().getPathSegment());
            assertNotNull("CmisObject is NULL", objectInFolderContainer.getObjectInFolder().getObject());
            if (documentId.equals(getIdProperty(objectInFolderContainer.getObjectInFolder().getObject().getProperties(), PROP_OBJECT_ID)))
            {
                assertEquals("Returned by path objectId is not equal to expected", documentPathSegment, objectInFolderContainer.getObjectInFolder().getPathSegment());
            }
        }

    }

    public void testRelationshipsAndAllowableActionsReceiving() throws Exception
    {
        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        String folderId = createAndAssertFolder();
        String sourceId = createRelationshipSourceObject(folderId);
        String targetId = createRelationshipTargetObject(folderId);
        createAndAssertRelationship(sourceId, targetId, getAndAssertRelationshipTypeId());

        LOGGER.info("[NavigationService->getChildren]");
        GetChildrenResponse childrenResponse = navigationService.getChildren(new GetChildren(getAndAssertRepositoryId(), folderId, "*", null, true, EnumIncludeRelationships.both,
                null, null, BigInteger.valueOf(1000), BigInteger.valueOf(0), null));

        LOGGER.info("[NavigationService->getDescendants]");
        CmisObjectInFolderContainerType[] descendantsResponse = navigationService.getDescendants(new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(-1),
                "*", true, EnumIncludeRelationships.both, null, null, null));
        LOGGER.info("[NavigationService->getObjectParents]");
        CmisObjectParentsType[] objectParentsResponse = getServicesFactory().getNavigationService().getObjectParents(
                new GetObjectParents(getAndAssertRepositoryId(), sourceId, "*", true, EnumIncludeRelationships.both, null, null, null));

        List<CmisObjectType> objects = new LinkedList<CmisObjectType>();
        assertAndAddObjectsToList(objects, childrenResponse.getObjects(), "GetChildren service");
        assertAndAddObjectsToList(objects, descendantsResponse, "GetDescendants service");
        assertAndAddObjectsToList(objects, objectParentsResponse, "GetObjectParents service");

        for (CmisObjectType object : objects)
        {
            assertNotNull(OBJECT_IS_NULL_MESSAGE, object);
            assertNotNull("Some returned Object Properties are null", object.getProperties());
            assertNotNull("Some returned Object String Properties are null", object.getProperties().getPropertyString());
            assertNotNull("Allowable Actions for Object were not returned", object.getAllowableActions());
        }

        objects = new LinkedList<CmisObjectType>();
        assertAndAddObjectsToList(objects, childrenResponse.getObjects(), "GetChildren service");

        for (CmisObjectType object : objects)
        {
            assertNotNull("Relationships Objects for Object were not returned", object.getRelationship());
            assertTrue("No one Relationship Object was returned in Response", object.getRelationship().length > 0);

            String id = getAndAssertIdPropertyValue(object.getProperties(), PROP_OBJECT_ID);

            for (CmisObjectType relationshipObject : object.getRelationship())
            {
                assertNotNull("Some returned Relationship Object is null for object ", relationshipObject);

                String sourceObjectId = getAndAssertIdPropertyValue(relationshipObject.getProperties(), PROP_SOURCE_ID);
                String targetObjectId = getAndAssertIdPropertyValue(relationshipObject.getProperties(), PROP_TARGET_ID);

                assertTrue("Object response has no any connection with its Relationship Object (it is not Target or Source Object)", id.equals(sourceObjectId)
                        || id.equals(targetObjectId));
            }
        }
    }

    public void testFilteringWithWrongFilter() throws Exception
    {
        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();

        try
        {
            LOGGER.info("[NavigationService->GetChildren]");
            navigationService.getChildren(new GetChildren(getAndAssertRepositoryId(), folderId, INVALID_FILTER, null, false, EnumIncludeRelationships.none, null, null, BigInteger
                    .valueOf(0), BigInteger.valueOf(0), null));

            fail(INVALID_WRONG_FILTER_PROCESSING_MESSAGE);
        }
        catch (Throwable e)
        {
            assertTrue(e.toString(), (e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.filterNotValid)));
        }

        try
        {
            LOGGER.info("[NavigationService->GetDescendants]");
            navigationService.getDescendants(new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(-1), INVALID_FILTER, false, EnumIncludeRelationships.none,
                    null, null, null));

            fail(INVALID_WRONG_FILTER_PROCESSING_MESSAGE);
        }
        catch (Throwable e)
        {
            assertTrue(e.toString(), (e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.filterNotValid)));
        }

        try
        {
            if (isVersioningAllowed())
            {
                LOGGER.info("[NavigationService->getCheckedOutDocs]");
                navigationService.getCheckedOutDocs(new GetCheckedOutDocs(getAndAssertRepositoryId(), folderId, INVALID_FILTER, null, false, EnumIncludeRelationships.none, null,
                        BigInteger.valueOf(0), BigInteger.valueOf(0), null));
                fail(INVALID_WRONG_FILTER_PROCESSING_MESSAGE);
            }
        }
        catch (Throwable e)
        {
            assertTrue(e.toString(), (e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.filterNotValid)));
        }
    }

    public void testWrongRepositoryIdUsing() throws Exception
    {
        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();

        try
        {
            if (isVersioningAllowed())
            {
                LOGGER.info("[NavigationService->getCheckedOutDocs]");
                navigationService.getCheckedOutDocs(new GetCheckedOutDocs(INVALID_REPOSITORY_ID, folderId, "*", null, false, EnumIncludeRelationships.none, null, BigInteger
                        .valueOf(0), BigInteger.valueOf(0), null));
                fail("Repository with specified Id was not described with RepositoryService");
            }
        }
        catch (Throwable e)
        {
        }

        try
        {
            LOGGER.info("[NavigationService->getChildren]");
            navigationService.getChildren(new GetChildren(INVALID_REPOSITORY_ID, folderId, "*", null, false, EnumIncludeRelationships.none, null, null, BigInteger.valueOf(0),
                    BigInteger.valueOf(0), null));

            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Throwable e)
        {
        }

        try
        {
            LOGGER.info("[NavigationService->GetDescendants]");
            navigationService.getDescendants(new GetDescendants(INVALID_REPOSITORY_ID, folderId, BigInteger.valueOf(-1), "*", false, EnumIncludeRelationships.none, null, null,
                    null));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Throwable e)
        {
        }

        try
        {
            LOGGER.info("[NavigationService->getFolderParent]");
            navigationService.getFolderParent(new GetFolderParent(INVALID_REPOSITORY_ID, folderId, "*", null));

            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Throwable e)
        {
        }

        try
        {
            LOGGER.info("[NavigationService->getObjectParents]");
            navigationService.getObjectParents(new GetObjectParents(INVALID_REPOSITORY_ID, folderId, "*", false, EnumIncludeRelationships.none, null, null, null));

            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Throwable e)
        {
        }
    }

    public void testGetCheckedoutDocs() throws Exception
    {
        if (isVersioningAllowed())
        {
            GetCheckedOutDocsResponse response = null;
            TreeNode<String> objectsTree = createObjectsTree(folderId, EnumVersioningState.checkedout, EnumTypesOfFileableObjects.DOCUMENTS, 1, 2, 3, 1);
            Set<String> createdDocuments = objectsTree.getChildren().keySet();
            try
            {
                LOGGER.info("[NavigationService->getCheckedOutDocs]");
                response = getServicesFactory().getNavigationService().getCheckedOutDocs(
                        new GetCheckedOutDocs(getAndAssertRepositoryId(), folderId, "*", null, false, EnumIncludeRelationships.none, null, null, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertTrue("No checked out documents were returned", response != null && response.getObjects() != null && response.getObjects().getObjects() != null
                    && response.getObjects().getObjects().length > 0);
            int length = response.getObjects().getObjects().length;
            assertTrue("Number of checked out documents is invalid", length >= createdDocuments.size());

            for (String documentId : createdDocuments)
            {
                boolean found = false;
                for (int i = 0; !found && i < length; i++)
                {
                    found = documentId.equals(getIdProperty(response.getObjects().getObjects()[i].getProperties(), PROP_OBJECT_ID));
                }
                assertTrue("Not all checked out documents were returned", found);
            }
            try
            {
                LOGGER.info("[NavigationService->getCheckedOutDocs]");
                response = getServicesFactory().getNavigationService().getCheckedOutDocs(
                        new GetCheckedOutDocs(getAndAssertRepositoryId(), folderId, "*", null, false, EnumIncludeRelationships.none, null, null, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertTrue("No checked out documents were returned", response != null && response.getObjects() != null && response.getObjects().getObjects() != null
                    && response.getObjects().getObjects().length > 0);
            length = response.getObjects().getObjects().length;
            assertTrue("Number of checked out documents is invalid", length == createdDocuments.size());

            for (String documentId : createdDocuments)
            {
                boolean found = false;
                for (int i = 0; !found && i < length; i++)
                {
                    found = documentId.equals(getIdProperty(response.getObjects().getObjects()[i].getProperties(), PROP_OBJECT_ID));
                }
                assertTrue("Not all checked out documents were returned", found);
            }
            LOGGER.info("[VersioningService->cancelCheckOut]");
            for (String documentId : createdDocuments)
            {
                getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), documentId, null));
            }
        }
        else
        {
            LOGGER.warn("testGetCheckedoutDocsFolder was skipped: Versioning isn't supported");
        }
    }

    public void testGetCheckedoutDocsIncludeRenditions() throws Exception
    {
        if (isVersioningAllowed())
        {
            if (EnumCapabilityRendition.read.equals(getAndAssertCapabilities().getCapabilityRenditions()))
            {
                String documentId = createAndAssertDocument();
                List<RenditionData> testRenditions = getTestRenditions(documentId);
                if (testRenditions != null)
                {
                    GetCheckedOutDocsResponse response = null;
                    TreeNode<String> objectsTree = createObjectsTree(folderId, EnumVersioningState.checkedout, EnumTypesOfFileableObjects.DOCUMENTS, 1, 2, 3, 1);
                    Set<String> createdDocuments = objectsTree.getChildren().keySet();
                    for (RenditionData testRendition : testRenditions)
                    {
                        try
                        {
                            LOGGER.info("[NavigationService->getCheckedOutDocs]");
                            response = getServicesFactory().getNavigationService().getCheckedOutDocs(
                                    new GetCheckedOutDocs(getAndAssertRepositoryId(), folderId, "*", null, false, EnumIncludeRelationships.none, testRendition.getFilter(), null,
                                            null, null));
                        }
                        catch (Exception e)
                        {
                            fail(e.toString());
                        }
                        assertTrue("No checked out documents were returned", response != null && response.getObjects() != null && response.getObjects().getObjects() != null
                                && response.getObjects().getObjects().length > 0);
                        for (CmisObjectType cmisObject : response.getObjects().getObjects())
                        {
                            assertRenditions(cmisObject, testRendition.getFilter(), testRendition.getExpectedKinds(), testRendition.getExpectedMimetypes());
                        }

                    }
                    LOGGER.info("[VersioningService->cancelCheckOut]");
                    for (String checkedOutDocumentId : createdDocuments)
                    {
                        getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), checkedOutDocumentId, null));
                    }
                }
                else
                {
                    LOGGER.info("testGetObjectIncludeRenditions was skipped: No renditions found for document type");
                }
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
            }
            else
            {
                LOGGER.info("testGetObjectIncludeRenditions was skipped: Renditions are not supported");
            }

        }
        else
        {
            LOGGER.warn("testGetCheckedoutDocsFolder was skipped: Versioning isn't supported");
        }
    }

    private void assertAndAddObjectsToList(List<CmisObjectType> targetList, CmisObjectInFolderContainerType[] objects, String responseType)
    {
        List<CmisObjectInFolderContainerType> listObjectInFolderContainers = convertTreeToObjectsList(objects);
        List<CmisObjectType> cmisObjecs = new ArrayList<CmisObjectType>();
        for (CmisObjectInFolderContainerType container : listObjectInFolderContainers)
        {
            assertTrue("Object from response is null", container != null && container.getObjectInFolder() != null && container.getObjectInFolder().getObject() != null);
            cmisObjecs.add(container.getObjectInFolder().getObject());
        }
        assertAndAddObjectsToList(targetList, cmisObjecs.toArray(new CmisObjectType[0]), responseType);
    }

    private void assertAndAddObjectsToList(List<CmisObjectType> targetList, CmisObjectInFolderListType objects, String responseType)
    {
        List<CmisObjectType> cmisObjecs = new ArrayList<CmisObjectType>();
        assertTrue("Object from response is null", objects != null && objects.getObjects() != null);
        for (CmisObjectInFolderType objectInFolder : objects.getObjects())
        {
            assertTrue("Object from response is null", objectInFolder != null && objectInFolder.getObject() != null);
            cmisObjecs.add(objectInFolder.getObject());
        }
        assertAndAddObjectsToList(targetList, cmisObjecs.toArray(new CmisObjectType[0]), responseType);
    }

    private void assertAndAddObjectsToList(List<CmisObjectType> targetList, CmisObjectParentsType[] objects, String responseType)
    {
        List<CmisObjectType> cmisObjecs = new ArrayList<CmisObjectType>();
        for (CmisObjectParentsType objectParent : objects)
        {
            assertTrue("Object from response is null", objectParent != null && objectParent.getObject() != null);
            cmisObjecs.add(objectParent.getObject());
        }
        assertAndAddObjectsToList(targetList, cmisObjecs.toArray(new CmisObjectType[0]), responseType);
    }

    private void assertAndAddObjectsToList(List<CmisObjectType> targetList, CmisObjectType[] objects, String responseType)
    {
        assertNotNull("Response Objects are null for " + responseType, objects);
        assertTrue("No one Object was returned in Response from " + responseType, objects.length > 0);

        if ((objects == null) || (objects.length <= 0))
        {
            return;
        }

        for (CmisObjectType object : objects)
        {
            assertNotNull("Some Expected Object was not found in Response", object);
            assertNotNull("Expected Object properties were not found in Response", object.getProperties());

            CmisPropertyString cmisPropertyString = new CmisPropertyString();
            cmisPropertyString.setPropertyDefinitionId(responseType);
            cmisPropertyString.setValue(new String[] { responseType });
            object.getProperties().setPropertyString(new CmisPropertyString[] { cmisPropertyString });
            targetList.add(object);
        }
    }

    private void assertChildren(Collection<String> expectedTree, CmisObjectInFolderType[] objects, boolean hasNoneFolderObjects, String sourceName) throws Exception
    {
        CmisObjectType[] cmisObjects = new CmisObjectType[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            assertNotNull("Some objects are null in response", objects[i].getObject());
            cmisObjects[i] = objects[i].getObject();
        }
        assertChildren(expectedTree, cmisObjects, hasNoneFolderObjects, sourceName);
    }

    private void assertChildren(Collection<String> expectedTree, CmisObjectType[] objects, boolean hasNoneFolderObjects, String sourceName) throws Exception
    {
        assertNotNull("Some Expected Object was not found in Response", objects);
        assertTrue("Some Expected Object was not found in Response", objects.length > 0);

        List<CmisObjectType> actualObjectsList = new LinkedList<CmisObjectType>(Arrays.asList(objects));

        for (String objectId : expectedTree)
        {
            CmisObjectType object = searchObjectById(actualObjectsList, objectId);

            assertNotNull("Some Expected Object was not found in Response", object);

            actualObjectsList.remove(object);
        }

        if (getAndAssertCapabilities().isCapabilityVersionSpecificFiling() && hasNoneFolderObjects)
        {
            assertTrue((INVALID_OBJECTS_INTEGRITY_MESSAGE + sourceName), (objects.length >= expectedTree.size()));
        }
        else
        {
            assertEquals((INVALID_OBJECTS_INTEGRITY_MESSAGE + sourceName), expectedTree.size(), objects.length);
        }
    }

    private Date getAndAssertDateTimePropertyValue(CmisPropertiesType properties, String propertyName)
    {
        assertNotNull("Properties are null", properties);
        assertNotNull("DateTime properties are null", properties.getPropertyDateTime());

        if ((propertyName == null) || (properties == null) || (properties.getPropertyId() == null))
        {
            return null;
        }

        for (CmisPropertyDateTime property : properties.getPropertyDateTime())
        {
            assertNotNull("One of the DateTime properties is null", property);
            assertNotNull("Property DateTime Name is null", property.getPropertyDefinitionId());

            if (propertyName.equals(property.getPropertyDefinitionId()))
            {
                assertNotNull("Property DateTime Value is null", property.getValue());
                return property.getValue(0).getTime();
            }
        }
        return null;
    }

    private void assertObjectsFromResponse(Object[] objects, int startIndex, int length) throws Exception
    {
        assertNotNull("Objects from response are null", objects);
        assertTrue("No one Object was returned in response", objects.length > 0);

        int endIndex = startIndex + length;

        assertTrue(INVALID_OBJECTS_LAYERS_DESCRIPTION_MESSAGE, ((startIndex >= 0) && (startIndex < objects.length)));
        assertTrue(INVALID_OBJECTS_LAYERS_DESCRIPTION_MESSAGE, (endIndex <= objects.length));

        if ((startIndex < 0) || (startIndex >= objects.length) || (endIndex > objects.length))
        {
            return;
        }

        for (int i = startIndex; i < endIndex; i++)
        {
            assertNotNull(OBJECT_IS_NULL_MESSAGE, objects[i]);

            if (objects[i] == null)
            {
                return;
            }
        }
    }

    private CmisObjectType searchObjectById(List<CmisObjectType> objects, String objectId)
    {
        if ((objectId == null) || (objects == null))
        {
            return null;
        }

        for (CmisObjectType object : objects)
        {
            assertNotNull(OBJECT_IS_NULL_MESSAGE, object);

            if (object != null)
            {
                String id = getAndAssertIdPropertyValue(object.getProperties(), PROP_OBJECT_ID);

                if (objectId.equals(id))
                {
                    return object;
                }
            }
        }

        return null;
    }

    private String getAndAssertIdPropertyValue(CmisPropertiesType properties, String propertyName)
    {
        assertNotNull("Properties are null", properties);
        assertNotNull("Id properties are null", properties.getPropertyId());

        if ((propertyName == null) || (properties == null) || (properties.getPropertyId() == null))
        {
            return null;
        }

        for (CmisPropertyId property : properties.getPropertyId())
        {
            assertNotNull("One of the Id properties is null", property);
            assertNotNull("Property Id Name is null", property.getPropertyDefinitionId());

            if (propertyName.equals(property.getPropertyDefinitionId()))
            {
                assertNotNull("Property Id Value is null", property.getValue());
                return property.getValue(0);
            }
        }
        return null;
    }

    protected TreeNode<String> createObjectsTree(String rootFolderId, EnumVersioningState documentsInitialVersion, EnumTypesOfFileableObjects returnObjectTypes, int depth,
            int minLayerSize, int maxLayerSize, int returnToLevel) throws Exception
    {
        TreeNode<String> root = new TreeNode<String>(rootFolderId, 0);

        if ((depth <= 0) || (maxLayerSize < 1) || (minLayerSize > maxLayerSize))
        {
            return root;
        }

        Stack<TreeNode<String>> foldersStack = new Stack<TreeNode<String>>();
        foldersStack.add(root);

        while (!foldersStack.isEmpty())
        {
            TreeNode<String> element = foldersStack.pop();
            if (element.getLevel() <= depth)
            {
                int layerSize = minLayerSize + (int) (Math.random() * (maxLayerSize - minLayerSize));
                int foldersOnLayer = !returnObjectTypes.equals(EnumTypesOfFileableObjects.DOCUMENTS) ? 1 + (int) (Math.random() * (layerSize - 1)) : 0;
                int documentsOnLayer = layerSize - foldersOnLayer;
                if (layerSize > 0 && element.getChildren() == null)
                {
                    element.setChildren(new HashMap<String, TreeNode<String>>());
                }
                for (int i = 0; i < foldersOnLayer; i++)
                {
                    String newFolderId = createAndAssertFolder(String.format(TEST_FOLDER_NAME_PATTERN, element.getLevel() + 1, i), getAndAssertFolderTypeId(),
                            element.getElement(), null);
                    TreeNode<String> child = new TreeNode<String>(newFolderId, element.getLevel() + 1);
                    if (element.getLevel() <= returnToLevel && !returnObjectTypes.equals(EnumTypesOfFileableObjects.DOCUMENTS))
                    {
                        element.getChildren().put(newFolderId, child);
                    }
                    if (element.getLevel() < depth - 1)
                    {
                        foldersStack.push(child);
                    }
                }
                for (int i = 0; i < documentsOnLayer; i++)
                {
                    String newDocumentId = createAndAssertDocument(String.format(TEST_DOCUMENT_NAME_PATTERN, element.getLevel() + 1, i), getAndAssertDocumentTypeId(), element
                            .getElement(), null, TEST_CONTENT, documentsInitialVersion);
                    if (element.getLevel() <= returnToLevel && !returnObjectTypes.equals(EnumTypesOfFileableObjects.FOLDERS))
                    {
                        element.getChildren().put(newDocumentId, new TreeNode<String>(newDocumentId, element.getLevel() + 1));
                    }
                }
            }
        }
        return root;
    }

    private void assertObjectsTree(CmisObjectInFolderContainerType[] receivedTree, TreeNode<String> expectedTreeRoot) throws Exception
    {
        assertNotNull("Objects from response are null", receivedTree);
        assertTrue("No one Object was returned in response", receivedTree.length > 0);

        TreeNode<String> currentTreeNode = expectedTreeRoot;
        Stack<Pair<CmisObjectInFolderContainerType, TreeNode<String>>> elementsStack = new Stack<Pair<CmisObjectInFolderContainerType, TreeNode<String>>>();
        CmisObjectInFolderContainerType root = new CmisObjectInFolderContainerType(null, receivedTree, null);
        elementsStack.push(new Pair<CmisObjectInFolderContainerType, TreeNode<String>>(root, expectedTreeRoot));

        while (!elementsStack.isEmpty())
        {
            Pair<CmisObjectInFolderContainerType, TreeNode<String>> element = elementsStack.pop();
            assertNotNull("Expected tree element not found", element.getLeft());
            assertNotNull("Received tree element not found", element.getRight());
            currentTreeNode = element.getRight();
            assertTrue("Count of returned childs are not equal to expected count of childs", getSize(element.getLeft().getChildren()) == getSize(currentTreeNode.getChildren()));
            if (element.getLeft().getChildren() != null && currentTreeNode.getChildren() != null)
            {
                Set<String> receivedIds = new HashSet<String>();
                for (CmisObjectInFolderContainerType objectInFolderContainer : element.getLeft().getChildren())
                {
                    String objectId = getAndAssertObjectId(objectInFolderContainer);
                    assertFalse("Returned tree childs are not equal to expected childs", receivedIds.contains(objectId));
                    receivedIds.add(objectId);
                    TreeNode<String> childTreeNode = currentTreeNode.getChildren().get(objectId);
                    elementsStack.push(new Pair<CmisObjectInFolderContainerType, TreeNode<String>>(objectInFolderContainer, childTreeNode));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private int getSize(Object obj)
    {
        if (obj == null)
        {
            return 0;
        }
        if (obj instanceof Object[])
        {
            return ((Object[]) obj).length;
        }
        if (obj instanceof Collection)
        {
            return ((Collection) obj).size();
        }
        if (obj instanceof Map)
        {
            return ((Map) obj).size();
        }
        return 0;
    }

    private static class Pair<FirstType, SecondType>
    {
        private FirstType left;
        private SecondType right;

        public Pair(FirstType left, SecondType right)
        {
            this.left = left;
            this.right = right;
        }

        public FirstType getLeft()
        {
            return left;
        }

        public SecondType getRight()
        {
            return right;
        }
    }

    private List<CmisObjectInFolderContainerType> convertTreeToObjectsList(CmisObjectInFolderContainerType[] rootChildren)
    {
        List<CmisObjectInFolderContainerType> result = new ArrayList<CmisObjectInFolderContainerType>();
        Stack<CmisObjectInFolderContainerType> elementsStack = new Stack<CmisObjectInFolderContainerType>();
        for (CmisObjectInFolderContainerType objectInFolderContainer : rootChildren)
        {
            elementsStack.push(objectInFolderContainer);
        }
        while (!elementsStack.isEmpty())
        {
            CmisObjectInFolderContainerType element = elementsStack.pop();
            result.add(element);
            if (element.getChildren() != null)
            {
                for (CmisObjectInFolderContainerType objectInFolderContainer : element.getChildren())
                {
                    elementsStack.push(objectInFolderContainer);
                }
            }
        }
        return result;
    }

    private String getAndAssertObjectId(CmisObjectInFolderContainerType objectInFolderContainer)
    {
        assertTrue("Object from response is null", objectInFolderContainer != null && objectInFolderContainer.getObjectInFolder() != null
                && objectInFolderContainer.getObjectInFolder().getObject() != null);
        CmisObjectType cmisObject = objectInFolderContainer.getObjectInFolder().getObject();
        return getAndAssertIdPropertyValue(cmisObject.getProperties(), PROP_OBJECT_ID);
    }

    protected class TreeNode<T>
    {
        private T element;

        private int level;

        private Map<T, TreeNode<T>> children;

        public T getElement()
        {
            return element;
        }

        public void setElement(T element)
        {
            this.element = element;
        }

        public Map<T, TreeNode<T>> getChildren()
        {
            return children;
        }

        public void setChildren(Map<T, TreeNode<T>> children)
        {
            this.children = children;
        }

        public int getLevel()
        {
            return level;
        }

        public void setLevel(int level)
        {
            this.level = level;
        }

        public TreeNode(T element, int level)
        {
            super();
            this.element = element;
            this.level = level;
        }

        public TreeNode()
        {
        }
    }

}
