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
import java.util.HashSet;
import java.util.Set;

import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisTypeDefinitionType;
import org.alfresco.repo.cmis.ws.EnumRelationshipDirection;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.GetObjectRelationships;
import org.alfresco.repo.cmis.ws.GetObjectRelationshipsResponse;
import org.alfresco.repo.cmis.ws.RelationshipServicePort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CmisRelationshipServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisRelationshipServiceClient.class);

    private String sourceId;
    private String targetId;

    public CmisRelationshipServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public CmisRelationshipServiceClient()
    {
    }

    /**
     * Initializes Relationship Service client
     */
    @Override
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }

        CmisTypeDefinitionType sourceType = getAndAssertRelationshipSourceType();
        CmisTypeDefinitionType targetType = getAndAssertRelationshipTargetType();

        if ((null == sourceType) || (null == targetType))
        {
            throw new Exception("Relationship Service can't be tested because no one Relationship Type with appropriate Source and Target Object Type Ids was found");
        }

        sourceId = createRelationshipSourceObject(getAndAssertRootFolderId());
        targetId = createRelationshipTargetObject(getAndAssertRootFolderId());

        createAndAssertRelationship(sourceId, targetId, getAndAssertRelationshipTypeId(), getAndAssertRootFolderId());
    }

    /**
     * Invokes all methods in Relationship Service
     */
    @Override
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        RelationshipServicePort relationshipService = getServicesFactory().getRelationshipService(getProxyUrl() + getService().getPath());

        relationshipService.getObjectRelationships(new GetObjectRelationships(getAndAssertRepositoryId(), sourceId, false, EnumRelationshipDirection.either, "Relationship", "*",
                false, BigInteger.valueOf(0), BigInteger.valueOf(0), null));
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
    }

    @Override
    protected void onSetUp() throws Exception
    {
        initialize();
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        try
        {
            deleteAndAssertObject(sourceId, true);
        }
        catch (Exception e)
        {
            LOGGER.error(e.toString());
        }
        try
        {
            deleteAndAssertObject(targetId, true);
        }
        catch (Exception e)
        {
            LOGGER.error(e.toString());
        }
        super.onTearDown();
    }

    public void testGetRelationships()
    {
        GetObjectRelationshipsResponse relationshipsResponse = getAndAssertObjectRelationships(sourceId, true, null, null, null, false, null, null);
        assertTrue("Invalid relationships amount was returned", relationshipsResponse.getObjects().getObjects().length == 1);
    }

    public void testGetRelationshipsSource()
    {
        GetObjectRelationshipsResponse relationshipsResponse = getAndAssertObjectRelationships(sourceId, true, EnumRelationshipDirection.source, null, null, false, null, null);
        assertTrue("Invalid relationships amount was returned", relationshipsResponse.getObjects().getObjects().length == 1);
    }

    public void testGetRelationshipsTarget()
    {
        GetObjectRelationshipsResponse relationshipsResponse = getAndAssertObjectRelationships(targetId, true, EnumRelationshipDirection.target, null, null, false, null, null);
        assertTrue("Invalid relationships amount was returned", relationshipsResponse.getObjects().getObjects().length == 1);
    }

    public void testGetRelationshipsFilter() throws Exception
    {
        GetObjectRelationshipsResponse relationshipsResponse = getAndAssertObjectRelationships(sourceId, true, EnumRelationshipDirection.either, null, PROP_OBJECT_ID + ","
                + PROP_SOURCE_ID, false, null, null);
        assertTrue("Invalid relationships amount was returned", relationshipsResponse.getObjects().getObjects().length <= (getRelationshipSubTypes().size() + 1));
        for (CmisObjectType objectType : relationshipsResponse.getObjects().getObjects())
        {
            assertNotNull("Some relationships properties were not returned", objectType.getProperties());

            assertNull("Not expected properties were returned", objectType.getProperties().getPropertyBoolean());
            assertNull("Not expected properties were returned", objectType.getProperties().getPropertyDecimal());
            assertNull("Not expected properties were returned", objectType.getProperties().getPropertyHtml());
            assertNull("Not expected properties were returned", objectType.getProperties().getPropertyInteger());
            assertNull("Not expected properties were returned", objectType.getProperties().getPropertyUri());
            assertNull("Not expected properties were returned", objectType.getProperties().getPropertyDateTime());
            assertNull("Not expected properties were returned", objectType.getProperties().getPropertyString());

            assertNotNull("Expected properties were not returned", objectType.getProperties().getPropertyId());

            assertEquals("Expected property was not returned", 2, objectType.getProperties().getPropertyId().length);

            assertNotNull("Expected property was not returned", getIdProperty(objectType.getProperties(), PROP_OBJECT_ID));
            assertNotNull("Expected property was not returned", getIdProperty(objectType.getProperties(), PROP_SOURCE_ID));
        }
    }

    public void testGetRelationshipsForWrongObjectId() throws Exception
    {
        try
        {
            LOGGER.info("[RelationshipService->getRelationships]");
            getServicesFactory().getRelationshipService().getObjectRelationships(
                    new GetObjectRelationships(getAndAssertRepositoryId(), "InvalidObjectId", true, EnumRelationshipDirection.either, getAndAssertRelationshipTypeId(), "*",
                            false, null, null, null));
            fail("Relationships were returned for Invalid Object Id");
        }
        catch (Exception e)
        {
            Set<EnumServiceException> exceptions = new HashSet<EnumServiceException>();
            exceptions.add(EnumServiceException.invalidArgument);
            exceptions.add(EnumServiceException.objectNotFound);
            assertException("Object Relationships Receiving for Invlaid Object Id", e, exceptions);
        }
    }

    public void testGetRelationshipsWithAllowableActions() throws Exception
    {
        GetObjectRelationshipsResponse objectRelationships = getAndAssertObjectRelationships(sourceId, true, EnumRelationshipDirection.either, null, "*", true, null, null);
        for (CmisObjectType relationship : objectRelationships.getObjects().getObjects())
        {
            assertNotNull("Invalid Relationships collection! No one Relationship Object may be undefined solely", relationship);
            assertNotNull("Allowable Actions for one of the Relationship Object was not returned", relationship.getAllowableActions());
            assertTrue("Allowable Actions define that Relationship Object Properties can't be read by current user", relationship.getAllowableActions().getCanDeleteObject());
        }
    }

    private GetObjectRelationshipsResponse getAndAssertObjectRelationships(String objectId, boolean allowSubTypes, EnumRelationshipDirection direction, String typeId,
            String filter, boolean includeAllowableActions, Long maxItems, Long skipCount)
    {
        GetObjectRelationshipsResponse objectRelationships = null;
        try
        {
            LOGGER.info("[RelationshipService->getRelationships]");
            objectRelationships = getServicesFactory().getRelationshipService().getObjectRelationships(
                    new GetObjectRelationships(getAndAssertRepositoryId(), objectId, allowSubTypes, direction, typeId, filter, includeAllowableActions,
                            maxItems != null ? BigInteger.valueOf(maxItems) : BigInteger.valueOf(100), skipCount != null ? BigInteger.valueOf(skipCount) : BigInteger.valueOf(0),
                            null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("Get Object Relationships response is undefined", objectRelationships);
        assertNotNull("Get Object Relationships response is undefined", objectRelationships.getObjects());
        assertNotNull("Get Object Relationships response is empty", objectRelationships.getObjects().getObjects());
        return objectRelationships;
    }

    public void testGetRelationshipsAgainstSubTypes() throws Exception
    {
        GetObjectRelationshipsResponse allRelationships = getAndAssertObjectRelationships(sourceId, true, EnumRelationshipDirection.either, null, "*", false, null, null);
        assertTrue("No one Relationship Object was returned", allRelationships.getObjects().getObjects().length >= 1);
        if (allRelationships.getObjects().getObjects().length > 1)
        {
            CmisTypeDefinitionType[] relationshipTypesArray = getRelationshipSubTypes().toArray(new CmisTypeDefinitionType[getRelationshipSubTypes().size()]);
            String typeId = relationshipTypesArray[relationshipTypesArray.length % 2].getId();
            GetObjectRelationshipsResponse relationships = getAndAssertObjectRelationships(sourceId, false, EnumRelationshipDirection.either, typeId, "*", false, null, null);
            assertTrue("No one Sub Relationship Object was returned", relationships.getObjects().getObjects().length == 1);
        }
    }

    public void testWrongRepositoryIdUsing() throws Exception
    {
        try
        {
            LOGGER.info("[RelationshipService->getRelationships]");
            getServicesFactory().getRelationshipService().getObjectRelationships(
                    new GetObjectRelationships(INVALID_REPOSITORY_ID, sourceId, false, null, null, null, false, null, null, null));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
            assertException("Object Relationships Receiving with Invalid Repository Id", e, EnumServiceException.invalidArgument);
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
        AbstractServiceClient client = (CmisRelationshipServiceClient) applicationContext.getBean("cmisRelationshipServiceClient");
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
}
