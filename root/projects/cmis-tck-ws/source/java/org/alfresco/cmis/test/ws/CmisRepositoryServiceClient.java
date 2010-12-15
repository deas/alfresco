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
import java.util.LinkedList;

import org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType;
import org.alfresco.repo.cmis.ws.CmisRepositoryEntryType;
import org.alfresco.repo.cmis.ws.CmisRepositoryInfoType;
import org.alfresco.repo.cmis.ws.CmisTypeContainer;
import org.alfresco.repo.cmis.ws.CmisTypeDefinitionType;
import org.alfresco.repo.cmis.ws.CmisTypePolicyDefinitionType;
import org.alfresco.repo.cmis.ws.CmisTypeRelationshipDefinitionType;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.GetRepositories;
import org.alfresco.repo.cmis.ws.GetRepositoryInfo;
import org.alfresco.repo.cmis.ws.GetRepositoryInfoResponse;
import org.alfresco.repo.cmis.ws.GetTypeChildren;
import org.alfresco.repo.cmis.ws.GetTypeChildrenResponse;
import org.alfresco.repo.cmis.ws.GetTypeDefinition;
import org.alfresco.repo.cmis.ws.GetTypeDescendants;
import org.alfresco.repo.cmis.ws.RepositoryServicePortBindingStub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Repository Service
 * 
 * @author Dmitry Velichkevich
 */
public class CmisRepositoryServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisRepositoryServiceClient.class);

    private static final String WRONG_TYPE_ID = "Wrong TypeId Parameter";

    private static final String PROPERTY_DEFINITIONS_NOT_RETURNED_MESSAGE_PATTERN = "Property Definitions for \"%s\" Type Definitions was not returned in request with returnPropertyDefinitions=TRUE";
    private static final String PROPERTY_DEFINITIONS_RETURNED_MESSAGE_PATTERN = "Property Definitions for \"%s\" Type Definitions was not returned in request with returnPropertyDefinitions=FALSE";
    private static final String BASE_DOCUMENT_TYPE_NOT_FOUND_MESSAGE = "Base Document type definition was not found";

    public CmisRepositoryServiceClient()
    {
    }

    public CmisRepositoryServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    @Override
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
    }

    /**
     * Invokes all methods in Repository Service
     */
    @Override
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        RepositoryServicePortBindingStub repositoryService = getServicesFactory().getRepositoryService(getProxyUrl() + getService().getPath());
        CmisRepositoryEntryType[] repositories = repositoryService.getRepositories(new GetRepositories());
        String repositoryId = repositories[0].getRepositoryId();
        GetRepositoryInfo getRepositoryInfo = new GetRepositoryInfo(repositoryId, null);
        repositoryService.getRepositoryInfo(getRepositoryInfo);
        String typeId = repositoryService.getTypeDescendants(new GetTypeDescendants(repositoryId, null, BigInteger.valueOf(-1), true, null))[0].getType().getId();
        repositoryService.getTypeChildren(new GetTypeChildren(repositoryId, typeId, true, BigInteger.ZERO, BigInteger.ZERO, null)).getTypes().getTypes()[0].getId();
        repositoryService.getTypeDefinition(new GetTypeDefinition(repositoryId, typeId, null));
    }

    @Override
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
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
        AbstractServiceClient client = (CmisRepositoryServiceClient) applicationContext.getBean("cmisRepositoryServiceClient");
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
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        super.onTearDown();
    }

    public void testGetRepositories()
    {
        CmisRepositoryEntryType[] repositories = null;
        try
        {
            LOGGER.info("[RepositoryService->getRepositories]");
            repositories = getServicesFactory().getRepositoryService().getRepositories(new GetRepositories());
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("GetRepositories response is undefined", repositories);
        assertTrue("GetRepositories response is empty", repositories.length > 0);
        assertNotNull("GetRepositories response is empty", repositories[0]);
    }

    public void testGetRepositoryInfo()
    {
        GetRepositoryInfoResponse getInfo = null;
        try
        {
            LOGGER.info("[RepositoryService->getRepositoryInfo]");
            getInfo = getServicesFactory().getRepositoryService().getRepositoryInfo(new GetRepositoryInfo(getAndAssertRepositoryId(), null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("GetRepositoryInfo response is undefined", getInfo);
        CmisRepositoryInfoType repositoryInfo = getInfo.getRepositoryInfo();
        assertEquals("Repository Id is not valid", getAndAssertRepositoryId(), repositoryInfo.getRepositoryId());
        assertNotNull("Repository Name is undefined", repositoryInfo.getRepositoryName());
        assertNotNull("Repository Description is undefined", repositoryInfo.getRepositoryDescription());
        assertNotNull("Repository Product Name is undefined", repositoryInfo.getProductName());
        assertNotNull("Repository Vendor Name is undefined", repositoryInfo.getVendorName());
        assertNotNull("Repository Product Version is undefined", repositoryInfo.getProductVersion());
        assertNotNull("Repository Root Folder Id is undefined", repositoryInfo.getRootFolderId());
        assertFalse("Repository Root Folder Id is empty", "".equals(repositoryInfo.getRootFolderId()));
        assertNotNull("Repository CMIS Version Supported is undefined", repositoryInfo.getCmisVersionSupported());
        assertNotNull("Repository ACL Capability is undefined", repositoryInfo.getAclCapability());
        assertNotNull("Changes On Type are undefined", repositoryInfo.getChangesOnType());
        assertNotNull("Repository Principal Anonymous is undefined", repositoryInfo.getPrincipalAnonymous());
        assertNotNull("Repository Principal Anyone is undefined", repositoryInfo.getPrincipalAnyone());

        CmisRepositoryCapabilitiesType capabilities = repositoryInfo.getCapabilities();
        assertNotNull("Repository capabilities are undefined", capabilities);
        assertNotNull("Repository capabilityACL is undefined", capabilities.getCapabilityACL());
        assertNotNull("Repository capabilityChanges is undefined", capabilities.getCapabilityChanges());
        assertNotNull("Repository capabilityContentStreamUpdatability is undefined", capabilities.getCapabilityContentStreamUpdatability());
        assertNotNull("Repository capabilityJoin is undefined", capabilities.getCapabilityJoin());
        assertNotNull("Repository capabilityQuery is undefined", capabilities.getCapabilityQuery());
        assertNotNull("Repository capabilityRenditions is undefined", capabilities.getCapabilityRenditions());
    }

    public void testGetTypeChildren()
    {
        getAndAssertTypeChildren(null, true, 0L, 0L);
    }

    public void testGetTypeChildrenWithTypeIdInNotSetAndSetState()
    {
        GetTypeChildrenResponse response = getAndAssertTypeChildren(null, false, 0L, 0L);
        assertEquals("Invalid type amount was returned", 4, response.getTypes().getTypes().length);
        String typeId = getBaseDocumentTypeId(response);
        assertNotNull(BASE_DOCUMENT_TYPE_NOT_FOUND_MESSAGE, typeId);
        response = getAndAssertTypeChildren(typeId, false, 10L, 0L);
    }

    public void testGetTypeChildrenPagination()
    {
        GetTypeChildrenResponse response = getAndAssertTypeChildren(null, false, 4L, 0L);
        assertNotNull("GetTypeChildren response property 'hasMoreItems' is undefined", response.getTypes().isHasMoreItems());
        assertTrue("GetTypeChildren response property 'hasMoreItems' is invalid", !response.getTypes().isHasMoreItems() && (4 == response.getTypes().getTypes().length));

        response = getAndAssertTypeChildren(null, false, 0L, 0L);
        assertNotNull("GetTypeChildren response property 'hasMoreItems' is NULL", response.getTypes().isHasMoreItems());
        assertTrue("GetTypeChildren response property 'hasMoreItems' is invalid", !response.getTypes().isHasMoreItems() && (4 == response.getTypes().getTypes().length));
        String typeId = null;
        for (CmisTypeDefinitionType typeDef : response.getTypes().getTypes())
        {
            if (!(typeDef instanceof CmisTypePolicyDefinitionType) && !(typeDef instanceof CmisTypeRelationshipDefinitionType))
            {
                typeId = typeDef.getId();
                break;
            }
        }
        assertNotNull("Type id is NULL", typeId);

        response = getAndAssertTypeChildren(null, false, 0L, 1L);
        assertNotNull("GetTypeChildren response property 'hasMoreItems' is NULL", response.getTypes().isHasMoreItems());
        assertTrue("GetTypeChildren response property 'hasMoreItems' is invalid", !response.getTypes().isHasMoreItems() && (3 == response.getTypes().getTypes().length));

        response = getAndAssertTypeChildren(null, false, 2L, 1L);
        assertNotNull("GetTypeChildren response property 'hasMoreItems' is NULL", response.getTypes().isHasMoreItems());
        assertTrue("GetTypeChildren response property 'hasMoreItems' is invalid", response.getTypes().isHasMoreItems() && (2 == response.getTypes().getTypes().length));

        response = getAndAssertTypeChildren(typeId, false, 10L, 0L);
        assertNotNull("GetTypeChildren response property 'hasMoreItems' is NULL", response.getTypes().isHasMoreItems());
        assertTrue("GetTypeChildren response property 'hasMoreItems' is invalid", (response.getTypes().isHasMoreItems() && (10 == response.getTypes().getTypes().length))
                || (!response.getTypes().isHasMoreItems() && (response.getTypes().getTypes().length <= 10)));
    }

    public void testGetTypeChildrenWithPropertyDefinitions() throws Exception
    {
        validateAllTypesOnValidProperties(getAndAssertTypeChildren(null, true, 0L, 0L), true);
        validateAllTypesOnValidProperties(getAndAssertTypeChildren(null, false, 0L, 0L), false);
    }

    private void validateAllTypesOnValidProperties(GetTypeChildrenResponse response, boolean propertiesAreExpected)
    {
        for (CmisTypeDefinitionType typeDef : response.getTypes().getTypes())
        {
            if (propertiesAreExpected != propertiesDefinitionIsValid(typeDef, propertiesAreExpected))
            {
                fail(String.format(((propertiesAreExpected) ? (PROPERTY_DEFINITIONS_NOT_RETURNED_MESSAGE_PATTERN) : (PROPERTY_DEFINITIONS_RETURNED_MESSAGE_PATTERN)), typeDef
                        .getId()));
            }
        }
    }

    public void testGetTypeChildrenForWrongTypeId()
    {
        try
        {
            LOGGER.info("[RepositoryService->getTypeChildren]");
            getServicesFactory().getRepositoryService().getTypeChildren(
                    new GetTypeChildren(getAndAssertRepositoryId(), WRONG_TYPE_ID, false, BigInteger.valueOf(10), BigInteger.valueOf(0), null));
            fail("No one Exception was thrown during Getting Type Children for wrong Type Id");
        }
        catch (Exception e)
        {
            assertException("Type Children Receiving for wrong Type Id", e, EnumServiceException.invalidArgument);
        }
    }

    private boolean propertiesDefinitionIsValid(CmisTypeDefinitionType typeDef, boolean trueExpected)
    {
        // FIXME: remove this condition checking when policy type definitions will be corrected
        if (typeDef instanceof CmisTypePolicyDefinitionType)
        {
            return trueExpected;
        }
        if (((null == typeDef.getPropertyBooleanDefinition()) || (typeDef.getPropertyBooleanDefinition().length < 1))
                && ((null == typeDef.getPropertyDateTimeDefinition()) || (typeDef.getPropertyDateTimeDefinition().length < 1))
                && ((null == typeDef.getPropertyDecimalDefinition()) || (typeDef.getPropertyDecimalDefinition().length < 1))
                && ((null == typeDef.getPropertyHtmlDefinition()) || (typeDef.getPropertyHtmlDefinition().length < 1))
                && ((null == typeDef.getPropertyIdDefinition()) || (typeDef.getPropertyIdDefinition().length < 1))
                && ((null == typeDef.getPropertyIntegerDefinition()) || (typeDef.getPropertyIntegerDefinition().length < 1))
                && ((null == typeDef.getPropertyStringDefinition()) || (typeDef.getPropertyStringDefinition().length < 1))
                && ((null == typeDef.getPropertyUriDefinition()) || (typeDef.getPropertyUriDefinition().length < 1)))
        {
            return false;
        }
        return true;
    }

    public void testGetTypeDescendants() throws Exception
    {
        getAndAssertTypeDescendants(null, -1, false);
    }

    public void testGetTypeDescendantsWithPropertyDefinitions() throws Exception
    {
        validateAllTypeContainersOnValidPropertiesDefinitions(getAndAssertTypeDescendants(null, -1, true), true);
        validateAllTypeContainersOnValidPropertiesDefinitions(getAndAssertTypeDescendants(null, -1, false), false);
    }

    private void validateAllTypeContainersOnValidPropertiesDefinitions(CmisTypeContainer[] rootContainers, boolean expectedConditionValue)
    {
        LinkedList<CmisTypeContainer> containerList = new LinkedList<CmisTypeContainer>();
        addContainers(containerList, rootContainers);
        for (CmisTypeContainer container = containerList.getFirst(); !containerList.isEmpty(); containerList.removeFirst(), container = (!containerList.isEmpty()) ? (containerList
                .getFirst()) : (null))
        {
            if (null == container)
            {
                continue;
            }

            assertNotNull("Invalid Type Descendants response: one of the Type Containers' Type is undefined", container.getType());
            if (expectedConditionValue != propertiesDefinitionIsValid(container.getType(), expectedConditionValue))
            {
                fail(String.format(((expectedConditionValue) ? (PROPERTY_DEFINITIONS_NOT_RETURNED_MESSAGE_PATTERN) : (PROPERTY_DEFINITIONS_RETURNED_MESSAGE_PATTERN)), container
                        .getType().getId()));
            }

            if (null != container.getChildren())
            {
                addContainers(containerList, container.getChildren());
            }
        }
    }

    public void testGetTypeDescendantsDepthing() throws Exception
    {
        long allTypesAmount = calculateTypesAmount(getAndAssertTypeDescendants(null, -1, false));
        String typeId = getBaseDocumentTypeId(getAndAssertTypeChildren(null, false, 0L, 0L));
        assertNotNull(typeId);
        long documentTypesAmount = calculateTypesAmount(getAndAssertTypeDescendants(typeId, -1, false));
        long toConcreateDepthDocumentTypesAmount = calculateTypesAmount(getAndAssertTypeDescendants(typeId, 2, false));

        assertTrue(allTypesAmount > 0);
        assertTrue(documentTypesAmount > 0);
        assertTrue(toConcreateDepthDocumentTypesAmount > 0);
        assertTrue(documentTypesAmount < allTypesAmount);
        assertTrue(toConcreateDepthDocumentTypesAmount < allTypesAmount);
        assertTrue(toConcreateDepthDocumentTypesAmount < documentTypesAmount);
    }

    private long calculateTypesAmount(CmisTypeContainer[] rootContainers)
    {
        LinkedList<CmisTypeContainer> containerList = new LinkedList<CmisTypeContainer>();
        addContainers(containerList, rootContainers);
        long result = 0;
        for (CmisTypeContainer container = containerList.getFirst(); !containerList.isEmpty(); containerList.removeFirst(), container = (!containerList.isEmpty()) ? (containerList
                .getFirst()) : (null))
        {
            if (null == container)
            {
                continue;
            }

            assertNotNull("Invalid Type Descendants response: one of the Type Containers' Type is undefined", container.getType());
            result++;

            if (null != container.getChildren())
            {
                addContainers(containerList, container.getChildren());
            }
        }
        return result;
    }

    public void testGetTypeDescendantsWithTypeIdNotSetAndSetParameterState() throws Exception
    {
        String typeId = getBaseDocumentTypeId(getAndAssertTypeChildren(null, true, 0L, 0L));
        assertNotNull("Base document type id is NULL");
        CmisTypeContainer[] documentTypes = getAndAssertTypeDescendants(typeId, -1, true);

        LinkedList<CmisTypeContainer> typesList = new LinkedList<CmisTypeContainer>();
        addContainers(typesList, documentTypes);
        for (CmisTypeContainer container = typesList.getFirst(); !typesList.isEmpty(); typesList.removeFirst(), container = (!typesList.isEmpty()) ? (typesList.getFirst())
                : (null))
        {
            if (null == container)
            {
                continue;
            }

            CmisTypeDefinitionType type = container.getType();
            assertNotNull("One of returned type definition type is NULL", type);
            if (!typeId.equals(type.getBaseId().getValue()))
            {
                fail("Type Children Response with concreate TypeId contains odd Type Definition. Expected: \"" + typeId + "\", actual: \"" + type.getBaseId() + "\"");
            }

            if (null != container.getChildren())
            {
                addContainers(typesList, container.getChildren());
            }
        }
    }

    public void testGetTypeDescendantsWithWrongTypeIdParameter() throws Exception
    {
        try
        {
            LOGGER.info("[RepositoryService->getTypeDescendants]");
            getServicesFactory().getRepositoryService().getTypeDescendants(new GetTypeDescendants(getAndAssertRepositoryId(), WRONG_TYPE_ID, BigInteger.valueOf(-1), false, null));
            fail("Get Type Descendants service has processed Invalid Type Id as Valid Type Id");
        }
        catch (Exception e)
        {
            assertException("Type Descendants Receiving for Invalid Type Id", e, EnumServiceException.invalidArgument);
        }
    }

    public void testGetTypeDescentantsWithDepthEqualToZero() throws Exception
    {
        try
        {
            LOGGER.info("[RepositoryService->getTypeDescendants]");
            getServicesFactory().getRepositoryService().getTypeDescendants(
                    new GetTypeDescendants(getAndAssertRepositoryId(), BASE_TYPE_DOCUMENT.getValue(), BigInteger.valueOf(0L), false, null));
            fail("Type Descentants can't be received for Depth equal to '0'");
        }
        catch (Exception e)
        {
            assertException("Type Descentans Receiving with Depth equal to '0'", e, EnumServiceException.invalidArgument);
        }
    }

    public void testGetTypeDefinition() throws Exception
    {
        CmisTypeContainer[] getTypesResponse = getAndAssertTypeDescendants(null, 1, true);
        getAndAssertTypeDefinition(getTypesResponse[0].getType().getId());
    }

    public void testGetTypeDefinitionWithWrongTypeId()
    {
        try
        {
            LOGGER.info("[RepositoryService->getTypeDefinition]");
            getServicesFactory().getRepositoryService().getTypeDefinition(new GetTypeDefinition(getAndAssertRepositoryId(), WRONG_TYPE_ID, null));
            fail("Get Type Definition service has processed Invalid TypeId as valid TypeId");
        }
        catch (Exception e)
        {
            assertException("Type Definition Receiving for Invalid Type Id", e, EnumServiceException.invalidArgument);
        }
    }

    public void testWrongRepositoryIdUsing() throws Exception
    {
        try
        {
            LOGGER.info("[RepositoryService->getRepositoryInfo]");
            getServicesFactory().getRepositoryService().getRepositoryInfo(new GetRepositoryInfo(INVALID_REPOSITORY_ID, null));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
            assertException("Repository Info Receiving for Invalid Repository Id", e, EnumServiceException.invalidArgument);
        }

        try
        {
            LOGGER.info("[RepositoryService->getTypeChildren]");
            getServicesFactory().getRepositoryService().getTypeChildren(
                    new GetTypeChildren(INVALID_REPOSITORY_ID, null, false, BigInteger.valueOf(10), BigInteger.valueOf(0), null));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
            assertException("Type Children Receiving with Invalid Repository Id", e, EnumServiceException.invalidArgument);
        }

        try
        {
            LOGGER.info("[RepositoryService->getTypeDescendants]");
            getServicesFactory().getRepositoryService().getTypeDescendants(new GetTypeDescendants(INVALID_REPOSITORY_ID, null, BigInteger.valueOf(1), true, null));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
            assertException("Type Descendants Receiving with Invalid Repository Id", e, EnumServiceException.invalidArgument);
        }

        try
        {
            CmisTypeContainer[] getTypesResponse = getAndAssertTypeDescendants(null, 1, true);
            LOGGER.info("[RepositoryService->getTypeDefinition]");
            getServicesFactory().getRepositoryService().getTypeDefinition(new GetTypeDefinition(INVALID_REPOSITORY_ID, getTypesResponse[0].getType().getId(), null));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
            assertException("Type Definition Receiving with Invalid Repository Id", e, EnumServiceException.invalidArgument);
        }
    }
}
