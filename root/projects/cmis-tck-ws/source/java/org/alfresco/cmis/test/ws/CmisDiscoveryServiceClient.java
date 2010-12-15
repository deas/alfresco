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
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.alfresco.repo.cmis.ws.ApplyACL;
import org.alfresco.repo.cmis.ws.CancelCheckOut;
import org.alfresco.repo.cmis.ws.CheckInResponse;
import org.alfresco.repo.cmis.ws.CheckOutResponse;
import org.alfresco.repo.cmis.ws.CmisAccessControlEntryType;
import org.alfresco.repo.cmis.ws.CmisAccessControlListType;
import org.alfresco.repo.cmis.ws.CmisAccessControlPrincipalType;
import org.alfresco.repo.cmis.ws.CmisObjectListType;
import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType;
import org.alfresco.repo.cmis.ws.DeleteObject;
import org.alfresco.repo.cmis.ws.DiscoveryServicePortBindingStub;
import org.alfresco.repo.cmis.ws.EnumCapabilityACL;
import org.alfresco.repo.cmis.ws.EnumCapabilityChanges;
import org.alfresco.repo.cmis.ws.EnumCapabilityQuery;
import org.alfresco.repo.cmis.ws.EnumCapabilityRendition;
import org.alfresco.repo.cmis.ws.EnumIncludeRelationships;
import org.alfresco.repo.cmis.ws.EnumTypeOfChanges;
import org.alfresco.repo.cmis.ws.EnumVersioningState;
import org.alfresco.repo.cmis.ws.GetContentChanges;
import org.alfresco.repo.cmis.ws.GetContentChangesResponse;
import org.alfresco.repo.cmis.ws.Query;
import org.alfresco.repo.cmis.ws.QueryResponse;
import org.alfresco.repo.cmis.ws.UpdateProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Discovery Service
 * 
 * @author Dmitry Velichkevich
 */
public class CmisDiscoveryServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisDiscoveryServiceClient.class);

    private static int RELATIONSHIPS_AMOUNT = 6;

    private String parentFolderId;
    private List<String> documentIds = new LinkedList<String>();
    private Set<String> sourceIds = new HashSet<String>();
    private Set<String> targetIds = new HashSet<String>();

    public CmisDiscoveryServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public CmisDiscoveryServiceClient()
    {
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
     * Invokes all methods in Discovery Service
     */
    @Override
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        DiscoveryServicePortBindingStub discoveryServicePort = getServicesFactory().getDiscoveryService(getProxyUrl() + getService().getPath());
        Query request = new Query();
        request.setRepositoryId(getAndAssertRepositoryId());
        request.setMaxItems(BigInteger.valueOf(10));
        request.setStatement("SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue());
        discoveryServicePort.query(request);
        discoveryServicePort.getContentChanges(new GetContentChanges(getAndAssertRepositoryId(), null, false, "*", false, false, BigInteger.TEN, null));
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
    public static void main(String[] args)
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-context.xml");
        AbstractServiceClient client = (CmisDiscoveryServiceClient) applicationContext.getBean("cmisDiscoveryServiceClient");
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
        parentFolderId = createAndAssertFolder();
        for (int i = 0; i < RELATIONSHIPS_AMOUNT; i++)
        {
            if (areRelationshipsSupported())
            {
                String sourceId = createRelationshipSourceObject(parentFolderId);
                String targetId = createRelationshipTargetObject(parentFolderId);
                createAndAssertRelationship(sourceId, targetId);
                documentIds.add(sourceId);
                documentIds.add(targetId);
                sourceIds.add(sourceId);
                targetIds.add(targetId);
            }
            else
            {
                documentIds.add(createAndAssertDocument(parentFolderId));
            }
        }
    }

    @Override
    protected void onTearDown() throws Exception
    {
        super.onTearDown();
        for (String documentId : documentIds)
        {
            deleteAndAssertObject(documentId, true);
        }
        deleteAndAssertObject(parentFolderId);
    }

    public void testQueryAll() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryAll() was skipped: Metadata query isn't supported");
        }
        else
        {
            String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue();
            try
            {
                QueryResponse queryResponse = queryAndAssert(statement, false, false, null, null, 10L, null);
                assertTrue("Query response contains more objects than was expected", queryResponse.getObjects().getObjects().length <= 10);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
    }

    public void testQueryWithAscendingOrdering() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryOrder() was skipped: Metadata query isn't supported");
        }
        else
        {
            String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue() + " ORDER BY " + PROP_NAME + " ASC";
            try
            {
                QueryResponse queryResponse = queryAndAssert(statement, false, false, EnumIncludeRelationships.none, null, null, null);
                assertOrdering(queryResponse, new AbstractBaseOrderingComparator()
                {
                    @Override
                    protected int compareImpl(String left, String right)
                    {
                        return left.compareToIgnoreCase(right);
                    }
                });
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
    }

    public void testQueryWithDescendingOrdering() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryOrder() was skipped: Metadata query isn't supported");
        }
        else
        {
            String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue() + " ORDER BY " + PROP_NAME + " DESC";
            try
            {
                QueryResponse queryResponse = queryAndAssert(statement, false, false, EnumIncludeRelationships.none, null, null, null);
                assertOrdering(queryResponse, new AbstractBaseOrderingComparator()
                {
                    @Override
                    protected int compareImpl(String left, String right)
                    {
                        return -left.compareToIgnoreCase(right);
                    }
                });
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
    }

    private abstract class AbstractBaseOrderingComparator implements Comparator<String>
    {
        public int compare(String left, String right)
        {
            assertFalse("Name property(s) of Left or/and Right object(s) is/are undefined", ((null == left) || (null == right)));
            assertFalse("Name property(s) of Left or/and Right object(s) is/are empty", ("".equals(left) || "".equals(right)));
            return compareImpl(left, right);
        }

        protected abstract int compareImpl(String left, String right);
    }

    private void assertOrdering(QueryResponse queryResponse, Comparator<String> orderingChecker) throws Exception
    {
        CmisObjectListType objects = queryResponse.getObjects();
        assertTrue("Query Response contains too little Objects amount than expected", (objects.getObjects().length > 1));
        for (int i = 1; i < objects.getObjects().length; i++)
        {
            String leftName = getStringProperty(objects.getObjects(i - 1).getProperties(), PROP_NAME);
            String currentName = getStringProperty(objects.getObjects(i).getProperties(), PROP_NAME);
            assertTrue(("Query Response Objects are not ordered properly! Object with " + PROP_NAME + "='" + currentName + "' must be located before Object with " + PROP_NAME
                    + "='" + leftName + "'"), (orderingChecker.compare(leftName, currentName) <= 0));
        }
    }

    private QueryResponse queryAndAssert(String statement, boolean searchAllVersions, boolean includeAllowableActions, EnumIncludeRelationships includeRelationships,
            String renditionFilter, Long maxItems, Long skipCount) throws Exception
    {
        QueryResponse queryResponse = null;
        try
        {
            LOGGER.info("[DiscoveryService->query]");
            BigInteger bigMaxItems = (null == maxItems) ? (null) : (BigInteger.valueOf(maxItems));
            BigInteger bigSkipCount = (null == skipCount) ? (null) : (BigInteger.valueOf(skipCount));
            queryResponse = getServicesFactory().getDiscoveryService().query(
                    new Query(getAndAssertRepositoryId(), statement, searchAllVersions, includeAllowableActions, includeRelationships, renditionFilter, bigMaxItems, bigSkipCount,
                            null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("Query response is undefined", queryResponse);
        assertNotNull("Query response is undefined", queryResponse.getObjects());
        assertNotNull("Query response is undefined", queryResponse.getObjects().getObjects());
        return queryResponse;
    }

    public void testQueryField() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryField() was skipped: Metadata query isn't supported");
        }
        else
        {
            String statement = "SELECT " + PROP_NAME + " FROM " + BASE_TYPE_DOCUMENT.getValue();
            QueryResponse queryResponse = queryAndAssert(statement, false, false, null, null, 10L, null);
            assertTrue("Query response contains more objects than was expected", queryResponse.getObjects().getObjects().length <= 10);
            for (CmisObjectType object : queryResponse.getObjects().getObjects())
            {
                assertNotNull("Invalid Query response: one of the result Objects is in 'not set' state solely", object);
                CmisPropertiesType properties = object.getProperties();
                assertNotNull("Object Properties are undefined", properties);
                int amount = (null != properties.getPropertyBoolean()) ? (properties.getPropertyBoolean().length) : (0);
                amount += (null != properties.getPropertyDateTime()) ? (properties.getPropertyDateTime().length) : (0);
                amount += (null != properties.getPropertyDecimal()) ? (properties.getPropertyDecimal().length) : (0);
                amount += (null != properties.getPropertyHtml()) ? (properties.getPropertyHtml().length) : (0);
                amount += (null != properties.getPropertyId()) ? (properties.getPropertyId().length) : (0);
                amount += (null != properties.getPropertyInteger()) ? (properties.getPropertyInteger().length) : (0);
                amount += (null != properties.getPropertyString()) ? (properties.getPropertyString().length) : (0);
                amount += (null != properties.getPropertyUri()) ? (properties.getPropertyUri().length) : (0);
                assertEquals(1, amount);
            }
        }
    }

    public void testQueryPagination() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryOffset() was skipped: Metadata query isn't supported");
        }
        else
        {
            CmisObjectType objectType1 = null;
            CmisObjectType objectType2 = null;
            String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue();
            try
            {
                QueryResponse queryResponse = queryAndAssert(statement, false, false, null, null, 2L, null);
                assertTrue("Query response contains more objects than was expected", queryResponse.getObjects().getObjects().length <= 2);
                objectType1 = queryResponse.getObjects().getObjects(1);
                queryResponse = queryAndAssert(statement, false, false, null, null, 1L, 1L);
                assertTrue("Query response contains more objects than was expected", queryResponse.getObjects().getObjects().length <= 1);
                objectType2 = queryResponse.getObjects().getObjects(0);
                assertEquals("Unexpected Object in Query response after Offsetting", objectType1, objectType2);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
    }

    public void testQueryWhere() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryWhere() was skipped: Metadata query isn't supported");
        }
        else
        {
            CmisPropertiesType response = getAndAssertObjectProperties(documentIds.iterator().next(), PROP_NAME);
            String name = getStringProperty(response, PROP_NAME);
            String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue() + " WHERE " + PROP_NAME + "='" + name + "'";
            QueryResponse queryResponse = queryAndAssert(statement, false, false, null, null, null, null);
            String resultId = getIdProperty(queryResponse.getObjects().getObjects()[0].getProperties(), PROP_OBJECT_ID);
            assertEquals("'WHERE' clause was resulted with invalid Object", documentIds.iterator().next(), resultId);
        }
    }

    public void testQueryIncludeRenditions() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryIncludeRenditions() was skipped: Metadata query isn't supported");
        }
        else
        {
            if (EnumCapabilityRendition.read.equals(getAndAssertCapabilities().getCapabilityRenditions()))
            {
                String documentId = createAndAssertDocument();
                List<RenditionData> testRenditions = getTestRenditions(documentId);
                if (testRenditions != null)
                {
                    for (RenditionData testRendition : testRenditions)
                    {
                        CmisPropertiesType response = getAndAssertObjectProperties(documentIds.iterator().next(), PROP_NAME);
                        String name = getStringProperty(response, PROP_NAME);
                        String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue() + " WHERE " + PROP_NAME + "='" + name + "'";
                        QueryResponse queryResponse = queryAndAssert(statement, false, false, null, testRendition.getFilter(), null, null);
                        String resultId = getIdProperty(queryResponse.getObjects().getObjects()[0].getProperties(), PROP_OBJECT_ID);
                        assertEquals("'WHERE' clause was resulted with invalid Object", documentIds.iterator().next(), resultId);
                        assertTrue("Response is empty", queryResponse != null && queryResponse.getObjects() != null && queryResponse.getObjects().getObjects() != null
                                && queryResponse.getObjects().getObjects().length == 1);
                        assertRenditions(queryResponse.getObjects().getObjects()[0], testRendition.getFilter(), testRendition.getExpectedKinds(), testRendition
                                .getExpectedMimetypes());
                    }
                }
                else
                {
                    LOGGER.info("testQueryIncludeRenditions was skipped: No renditions found for document type");
                }
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
            }
            else
            {
                LOGGER.info("testQueryIncludeRenditions was skipped: Renditions are not supported");
            }

        }
    }

    public void testNotValidQuery()
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testNonValidQuery() was skipped: Metadata query isn't supported");
        }
        else
        {
            Query request = new Query();
            request.setRepositoryId(getAndAssertRepositoryId());
            request.setStatement("SELECT * FROM InvalidTypeId");
            try
            {
                LOGGER.info("[DiscoveryService->query]");
                getServicesFactory().getDiscoveryService().query(request);
                fail("No Exception was thrown during executing Query with wrong Statement");
            }
            catch (Exception e)
            {
                // TODO: It isn't clear what type of exception should be returned in this case
                // Set<EnumServiceException> expectedExceptions = new HashSet<EnumServiceException>();
                // expectedExceptions.add(EnumServiceException.invalidArgument);
                // assertException("Executing Query with wrong Statement", e, expectedExceptions);
            }
        }
    }

    public void testQueryPWCSearchable() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryPWCSearchable() was skipped: Metadata query isn't supported");
        }
        else
        {
            if (isVersioningAllowed())
            {
                String documentId = createAndAssertDocument();
                CheckOutResponse checkOutResponse = checkOutAndAssert(documentId);
                String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue() + " WHERE " + PROP_OBJECT_ID + "='" + checkOutResponse.getObjectId() + "'";
                QueryResponse queryResponse = null;
                try
                {
                    queryResponse = queryAndAssert(statement, false, false, null, null, null, null);
                }
                finally
                {
                    LOGGER.info("[VersioningService->cancelCheckOut]");
                    getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), checkOutResponse.getObjectId(), null));
                    deleteAndAssertObject(documentId);
                }
                if (getAndAssertCapabilities().isCapabilityPWCSearchable())
                {
                    assertEquals("PWC was not found", 1, queryResponse.getObjects().getObjects().length);
                }
                else
                {
                    assertTrue("PWC is not searchable but was found", queryResponse.getObjects().getObjects().length < 1);
                }
            }
            else
            {
                LOGGER.warn("testQueryPWCSearchable() was skipped: Versioning isn't supported");
            }
        }
    }

    public void testQueryAllVersionsSearchable() throws Exception
    {
        CmisRepositoryCapabilitiesType capabilities = getAndAssertCapabilities();
        if (EnumCapabilityQuery.none.equals(capabilities.getCapabilityQuery()) || EnumCapabilityQuery.fulltextonly.equals(capabilities.getCapabilityQuery()))
        {
            LOGGER.warn("testQueryAllVersionsSearchable() was skipped: Metadata querying isn't supported");
        }
        else
        {
            if (isVersioningAllowed())
            {
                String parentFolderId = createAndAssertFolder();
                String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), parentFolderId, null, TEST_CONTENT, null);
                CheckInResponse checkInResponse = new CheckInResponse(documentId, null);
                for (int i = 0; i < 2; i++)
                {
                    CheckOutResponse checkOutResponse = checkOutAndAssert(checkInResponse.getObjectId());
                    checkInResponse = checkInAndAssert(checkOutResponse.getObjectId(), true, new CmisPropertiesType(), createUniqueContentStream(), "");
                }
                CmisObjectType[] allVersions = getAndAssertAllVersions(checkInResponse.getObjectId(), "*", false);
                String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue() + " WHERE IN_FOLDER('" + parentFolderId + "')";
                QueryResponse queryResponse = null;
                try
                {
                    queryResponse = queryAndAssert(statement, true, false, null, null, null, null);
                }
                finally
                {
                    deleteAndAssertObject(documentId, true);
                }
                Set<String> responseIds = new HashSet<String>();
                for (CmisObjectType object : queryResponse.getObjects().getObjects())
                {
                    assertNotNull("Invalid Query response: one of the Objects is in 'not set' state solely", object);
                    responseIds.add(getIdProperty(object.getProperties(), PROP_OBJECT_ID));
                }
                for (CmisObjectType object : allVersions)
                {
                    assertNotNull("Invalid Version Objects collection: one of the Version Objects is in 'not set' state solely", object);
                    String currentId = getIdProperty(object.getProperties(), PROP_OBJECT_ID);
                    if (capabilities.isCapabilityAllVersionsSearchable())
                    {
                        assertTrue("All Versions Searchable capability is supported but Version Objects was not returned", responseIds.contains(currentId));
                    }
                    else
                    {
                        if (!documentId.equals(currentId))
                        {
                            assertFalse("All Versions Searchable capability is not supported but Version Objects was returned", responseIds.contains(currentId));
                        }
                    }
                }
            }
            else
            {
                LOGGER.warn("testQueryAllVersionsSearchable() was skipped: Versioning isn't supported");
            }
        }
    }

    public void testQueryFullText() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.metadataonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.info("testQueryFullText() was skipped: Full Text querying isn't supported");
        }
        else
        {
            if (isContentStreamAllowed())
            {
                String content = createTestContnet();
                String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, content, null);
                String[] tokens = content.split("\\.\\.\\. ");
                String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT + " WHERE CONTAINS('Test Document content " + tokens[tokens.length - 1] + "')";
                QueryResponse queryResponse = null;
                try
                {
                    queryResponse = queryAndAssert(statement, false, false, null, null, null, null);
                }
                finally
                {
                    deleteAndAssertObject(documentId);
                }
                boolean found = false;
                for (int i = 0; !found && (i < queryResponse.getObjects().getObjects().length); i++)
                {
                    found = documentId.equals(getIdProperty(queryResponse.getObjects().getObjects()[i].getProperties(), PROP_OBJECT_ID));
                }
                assertTrue("Full Text Search is supported but Document was not found by content", found);
            }
            else
            {
                LOGGER.info("testQueryFullText() was skipped: Content stream isn't allowed");
            }
        }
    }

    public void testQueryAllowableActions() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryAllowableActions() was skipped: Metadata query isn't supported");
        }
        else
        {
            String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue();
            QueryResponse queryResponse = queryAndAssert(statement, false, true, null, null, null, null);
            for (CmisObjectType object : queryResponse.getObjects().getObjects())
            {
                assertNotNull("Invalid Query response: one of the Objects is in 'not set' state solely", object);
                assertNotNull("Allowable Actions were not returned in Query response", object.getAllowableActions());
                assertTrue("It is denied Getting Properties for Current User Principals", object.getAllowableActions().getCanGetProperties());
            }
        }
    }

    public void testQueryRelationships() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryRelationships() was skipped: Metadata query isn't supported");
        }
        else
        {
            if (areRelationshipsSupported())
            {
                String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue() + " WHERE IN_FOLDER('" + parentFolderId + "')";
                EnumIncludeRelationships[] relationshipInclusionRules = new EnumIncludeRelationships[] { EnumIncludeRelationships.none, EnumIncludeRelationships.both,
                        EnumIncludeRelationships.source, EnumIncludeRelationships.target };
                for (EnumIncludeRelationships rule : relationshipInclusionRules)
                {
                    QueryResponse queryResponse = queryAndAssert(statement, false, false, rule, null, null, null);
                    assertRelationships(queryResponse, rule);
                }
            }
            else
            {
                LOGGER.warn("testQueryRelationships() was skipped: Relationships are not suppoerted by Repository");
            }
        }
    }

    private void assertRelationships(QueryResponse queryResponse, EnumIncludeRelationships includeRelationships) throws Exception
    {
        for (CmisObjectType object : queryResponse.getObjects().getObjects())
        {
            assertNotNull("Invalid Query response: one of the Objects is in 'not set' state solely", object);
            if (EnumIncludeRelationships.none.equals(includeRelationships))
            {
                assertNull("Relationships were returned for one of the Objects with includeRelationships parameter equal to 'NONE'", object.getRelationship());
            }
            else
            {
                String objectId = getIdProperty(object.getProperties(), PROP_OBJECT_ID);
                if (((EnumIncludeRelationships.source == includeRelationships) && !sourceIds.contains(objectId))
                        || ((EnumIncludeRelationships.target == includeRelationships) && !targetIds.contains(objectId)))
                {
                    continue;
                }
                if (EnumIncludeRelationships.none == includeRelationships)
                {
                    assertNull("Relationships were returned for one of the Objects without request", object.getRelationship());
                }
                else
                {
                    assertNotNull(("'" + includeRelationships.getValue() + "' Relationships were not returned for one of the Objects"), object.getRelationship());
                    for (CmisObjectType relationship : object.getRelationship())
                    {
                        assertNotNull("Invalid Relationships collection in Query response: one of the Relationship Objects in 'not set' state solely", relationship);
                        String sourceId = getIdProperty(relationship.getProperties(), PROP_SOURCE_ID);
                        String targetId = getIdProperty(relationship.getProperties(), PROP_TARGET_ID);
                        if (EnumIncludeRelationships.source.equals(includeRelationships))
                        {
                            assertEquals(objectId, sourceId);
                        }
                        else
                        {
                            if (EnumIncludeRelationships.target.equals(includeRelationships))
                            {
                                assertEquals(objectId, targetId);
                            }
                            else
                            {
                                assertTrue(("Object with Id='" + objectId + "' MUST be either Source or Target Object of each Relationship Object"), objectId.equals(sourceId)
                                        || objectId.equals(targetId));
                            }
                        }
                    }
                }
            }
        }
    }

    public void testGetContentChangesForCreating() throws Exception
    {
        if (!EnumCapabilityChanges.none.equals(getAndAssertCapabilities().getCapabilityChanges()))
        {
            String changeLogToken = getAndAssertRepositoryInfo().getLatestChangeLogToken();
            // TODO: Change document creation to default versioning state after versioning problem will be fixed
            String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT,
                    EnumVersioningState.none);

            receiveAndAssertContentChanges(changeLogToken, 30, documentId, EnumTypeOfChanges.created);
        }
        else
        {
            LOGGER.warn("testGetContentChangesForCreating() was skipped: Changes capability is not supported");
        }
    }

    public void testGetContentChangesForUpdating() throws Exception
    {
        if (!EnumCapabilityChanges.none.equals(getAndAssertCapabilities().getCapabilityChanges()))
        {
            // TODO: Change document creation to default versioning state after versioning problem will be fixed
            String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT,
                    EnumVersioningState.none);

            String changeLogToken = getAndAssertRepositoryInfo().getLatestChangeLogToken();
            String documentNameNew = generateTestFileName("_new");
            CmisPropertiesType properties = fillProperties(documentNameNew, null);
            LOGGER.info("[ObjectService->updateProperties]");
            documentId = getServicesFactory().getObjectService().updateProperties(new UpdateProperties(getAndAssertRepositoryId(), documentId, null, properties, null))
                    .getObjectId();

            receiveAndAssertContentChanges(changeLogToken, 30, documentId, EnumTypeOfChanges.updated);
        }
        else
        {
            LOGGER.warn("testGetContentChangesForCreating() was skipped: Changes capability is not supported");
        }
    }

    public void testGetContentChangesForDeleting() throws Exception
    {
        if (!EnumCapabilityChanges.none.equals(getAndAssertCapabilities().getCapabilityChanges()))
        {
            // TODO: Change document creation to default versioning state after versioning problem will be fixed
            String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT,
                    EnumVersioningState.none);

            String changeLogToken = getAndAssertRepositoryInfo().getLatestChangeLogToken();
            deleteAndAssertObject(documentId);

            receiveAndAssertContentChanges(changeLogToken, 30, documentId, EnumTypeOfChanges.deleted);
        }
        else
        {
            LOGGER.warn("testGetContentChangesForCreating() was skipped: Changes capability is not supported");
        }
    }

    public void testGetContentChangesForSecurity() throws Exception
    {
        if (!EnumCapabilityChanges.none.equals(getAndAssertCapabilities().getCapabilityChanges()) && EnumCapabilityACL.manage.equals(getAndAssertCapabilities().getCapabilityACL()))
        {
            // TODO: Change document creation to default versioning state after versioning problem will be fixed
            String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT,
                    EnumVersioningState.none);

            String changeLogToken = getAndAssertRepositoryInfo().getLatestChangeLogToken();

            CmisAccessControlListType acList = new CmisAccessControlListType();
            CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType(aclPrincipalId, null);
            CmisAccessControlEntryType ace = new CmisAccessControlEntryType(principal, new String[] { PERMISSION_READ }, true, null);
            acList.setPermission(new CmisAccessControlEntryType[] { ace });
            LOGGER.info("[ACLService->applyACL]");
            getServicesFactory().getACLService().applyACL(new ApplyACL(getAndAssertRepositoryId(), documentId, acList, null, getAndAssertACLPrapagation(), null));

            LOGGER.info("[DiscoveryService->getContentChanges]");
            receiveAndAssertContentChanges(changeLogToken, 30, documentId, EnumTypeOfChanges.security);
        }
        else
        {
            LOGGER.warn("testGetContentChangesForCreating() was skipped: Changes capability is not supported");
        }
    }

    private void receiveAndAssertContentChanges(String changeLogToken, Integer maxItems, String expectedObjectId, EnumTypeOfChanges changeType) throws Exception
    {
        LOGGER.info("[DiscoveryService->getContentChanges]");
        GetContentChangesResponse response = getServicesFactory().getDiscoveryService().getContentChanges(
                new GetContentChanges(getAndAssertRepositoryId(), changeLogToken, false, "*", false, false, maxItems != null ? BigInteger.valueOf(maxItems) : null, null));
        assertTrue("Get Content Changes response is empty", response != null && response.getObjects() != null && response.getObjects().getObjects() != null);
        assertTrue("Get Content Changes response is empty", response.getObjects().getObjects().length > 0);
        boolean found = false;
        for (CmisObjectType cmisObjectType : response.getObjects().getObjects())
        {
            assertTrue("Cmis object is null", cmisObjectType != null && cmisObjectType.getProperties() != null);
            if (expectedObjectId.equals(getIdProperty(cmisObjectType.getProperties(), PROP_OBJECT_ID)))
            {
                assertNotNull("ChangeEventInfo is null", cmisObjectType.getChangeEventInfo());
                assertNotNull("ChangeTime is null", cmisObjectType.getChangeEventInfo().getChangeTime());
                assertNotNull("ChangeType is null", cmisObjectType.getChangeEventInfo().getChangeType());
                if (changeType.equals(cmisObjectType.getChangeEventInfo().getChangeType()))
                {
                    found = true;
                }
            }
        }
        assertTrue("Ecpected ChangeEvent is not found", found);
    }

}
