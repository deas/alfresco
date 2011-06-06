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
package org.alfresco.webservice.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.repository.Association;
import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryServiceLocator;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.CMLCopy;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLDelete;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.CMLWriteContent;
import org.alfresco.webservice.types.ClassDefinition;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.NodeDefinition;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.PropertyDefinition;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.QueryConfiguration;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSet;
import org.alfresco.webservice.types.ResultSetRow;
import org.alfresco.webservice.types.ResultSetRowNode;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.ISO9075;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RepositoryServiceSystemTest extends BaseWebServiceSystemTest
{
    private static Log logger = LogFactory.getLog(RepositoryServiceSystemTest.class);

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * added:  November 20 2008, ETWOONE-396
     * 
     * Now getTotalRowCount  return the number of rows in this specific result set. 
     * In the previous version getTotalRowCount return the number of rows in the batch.
     * The method getNextResults(RESULTSETROW[] allResults)  in AbstractQuerySession class was corrected.
     * Also "paged result set"  and  "full query result set" are equal  (against the same data set)
     * 
     * @throws Exception
     */
    public void testFetchMoreAndbatchSize() throws Exception
    {
    	long NUMBEER_CREATED_FILES = 14;
    	long fetchRows = 0;
    	for(long i = 0;i<NUMBEER_CREATED_FILES;i++)
   	  	{
    		createTestFetchMoreContent();
   	  	}
  	 
    	System.out.println("testFetchMore_AND_BatcSize:created "+ NUMBEER_CREATED_FILES+" files");
    	RepositoryServiceSoapBindingStub repositoryService = WebServiceFactory.getRepositoryService();
    	Query query =    new Query(Constants.QUERY_LANG_LUCENE,"TYPE:\"{http://www.alfresco.org/model/content/1.0}content\" AND @\\{http\\://www.alfresco.org/model/content/1.0\\}name:Web Services sample*");
        
        QueryConfiguration queryCfg = new QueryConfiguration();
   	  	int batchSize = 3;
   	  	queryCfg.setFetchSize(batchSize);
   	  
   	    repositoryService.setHeader(new RepositoryServiceLocator().getServiceName().getNamespaceURI(), "QueryHeader", queryCfg);
   	  	QueryResult queryResult =repositoryService.query(BaseWebServiceSystemTest.store, query,false);

   	  	ResultSet resultSet = queryResult.getResultSet();
   	 
   	  
   	  	long totalRowCount = resultSet.getTotalRowCount();
   	    assertTrue("Number of the created files and resultSet.getTotalRowCount() are equal", NUMBEER_CREATED_FILES == totalRowCount);
   	    
   	    
   	  	System.out.println("*************************************");
   	  	System.out.println(" getTotalRowCount(),all rows:"+ resultSet.getTotalRowCount());
   	  	System.out.println("*************************************");
   	  	
   	  	fetchRows = showRows(queryResult, batchSize);
   	  	

   	  	System.out.println("==============PagedQuery===============");
   	  	String querySession = queryResult.getQuerySession();

   	  	while (querySession != null) 
   	  	{
   	  		queryResult = repositoryService.fetchMore(querySession);
   	  		fetchRows+=showRows(queryResult, batchSize);
   	  		querySession = queryResult.getQuerySession();
   	  	}

   	    assertTrue("Number of the created files to equally number of the fetcheded files ", NUMBEER_CREATED_FILES == fetchRows);
    	
    }
    private  long showRows(QueryResult queryResult, int batchSize) 
    {
    	long fetchedRows = 0;
		ResultSetRow[] rows = queryResult.getResultSet().getRows();
		System.out.println(String.format(
				"--batch size = [%d]----getting next [%d] rows: ", batchSize,
				rows.length));
		for (int x = 0; x < rows.length; x++) 
		{
			ResultSetRow row = rows[x];
			System.out.println(row.getColumns(2).getValue());
			fetchedRows++;
		}
		return fetchedRows;
	}
    private void createTestFetchMoreContent()throws Exception
    {
    	
             ParentReference parentRef = new ParentReference( BaseWebServiceSystemTest.store , null, BaseWebServiceSystemTest.folderReference.getPath(), Constants.ASSOC_CONTAINS, null);
             String name = "Web Services sample (" + System.currentTimeMillis() + ")";
             parentRef.setChildName("cm:" + name);
             
             NamedValue[] contentProps = new NamedValue[1]; 
             contentProps[0] = Utils.createNamedValue(Constants.PROP_NAME, name); 
             CMLCreate create = new CMLCreate("1", parentRef, null, null, null, Constants.TYPE_CONTENT, contentProps);
             
             NamedValue[] titledProps = new NamedValue[2];
             titledProps[0] = Utils.createNamedValue(Constants.PROP_TITLE, name);
             titledProps[1] = Utils.createNamedValue(Constants.PROP_DESCRIPTION, name);
             CMLAddAspect addAspect = new CMLAddAspect(Constants.ASPECT_TITLED, titledProps, null, "1");
             
             CML cml = new CML();
             cml.setCreate(new CMLCreate[] {create});
             cml.setAddAspect(new CMLAddAspect[] {addAspect});

             WebServiceFactory.getRepositoryService().update(cml);     
           
    } 
    /**
     * Tests the getStores method
     */
    public void testGetStores() throws Exception
    {
        Store[] stores = WebServiceFactory.getRepositoryService().getStores();
        assertNotNull("Stores array should not be null", stores);
        assertTrue("There should be at least 1 store", stores.length >= 1);
    }

    /**
     * Tests the query service call
     */
    public void testQuery() throws Exception
    {
        //Query query = new Query(QueryLanguageEnum.lucene, "*");
        Query query = new Query(Constants.QUERY_LANG_LUCENE,
                "( +@\\{http\\://www.alfresco.org/1.0\\}name:test*) OR  TEXT:test*");

        QueryResult queryResult = WebServiceFactory.getRepositoryService().query(BaseWebServiceSystemTest.store, query,
                false);
        assertNotNull("queryResult should not be null", queryResult);

        ResultSet resultSet = queryResult.getResultSet();
        assertNotNull("The result set should not be null", resultSet);
        logger.debug("There are " + resultSet.getTotalRowCount() + " rows:");

        if (resultSet.getTotalRowCount() > 0)
        {
            ResultSetRow[] rows = resultSet.getRows();
            for (int x = 0; x < rows.length; x++)
            {
                ResultSetRow row = rows[x];
                NamedValue[] columns = row.getColumns();
                for (int y = 0; y < columns.length; y++)
                {
                    logger.debug("row " + x + ": "
                            + row.getColumns(y).getName() + " = "
                            + row.getColumns(y).getValue());
                }
                
                // Check that the aspects are being set
                ResultSetRowNode node = row.getNode();
                assertNotNull(node);
                assertNotNull(node.getId());
                assertNotNull(node.getType());   
                String[] aspects = node.getAspects();
                assertNotNull(aspects);
            }
            
        } else
        {
            logger.debug("The query returned no results");
            fail("The query returned no results");
        }
    }    

    /**
     * Tests the query service call
     */
    private String[] getMissingProperties(List<PropertyDefinition> actualProperties, Map<String, NamedValue> searchProperties)
    {
        List<String> missingProps = new ArrayList<String>();
        for(PropertyDefinition propDef : actualProperties)
        {
            System.out.print("Checking whether " + propDef.getName() + " exists in search properties...");
            if(!searchProperties.containsKey(propDef.getName())) {
                System.out.println("Nope");
                missingProps.add(propDef.getName());
            } else {
                System.out.println("");
            }
        }
        
        return missingProps.toArray(new String[0]);
    }

    /**
     *  Tests that Query returns all the properties for matching nodes
     */
    public void testALF649() throws Exception
    {
        Query query = new Query(Constants.QUERY_LANG_LUCENE,
                "( +@\\{http\\://www.alfresco.org/1.0\\}name:test*) OR  TEXT:test*");

        QueryResult queryResult = WebServiceFactory.getRepositoryService().query(BaseWebServiceSystemTest.store, query,
                false);
        assertNotNull("queryResult should not be null", queryResult);

        ResultSet resultSet = queryResult.getResultSet();
        assertNotNull("The result set should not be null", resultSet);
        logger.debug("There are " + resultSet.getTotalRowCount() + " rows:");

        if (resultSet.getTotalRowCount() > 0)
        {
            ResultSetRow[] rows = resultSet.getRows();
            for (int x = 0; x < rows.length; x++)
            {
                ResultSetRow row = rows[x];
                NamedValue[] columns = row.getColumns();
                for (int y = 0; y < columns.length; y++)
                {
                    logger.debug("row " + x + ": "
                            + row.getColumns(y).getName() + " = "
                            + row.getColumns(y).getValue());
                }
                
                // Check that the aspects are being set
                ResultSetRowNode node = row.getNode();

                // Get the actual properties of the node
                // create a predicate object to to send to describe method
                Reference ref = new Reference();
                ref.setStore(BaseWebServiceSystemTest.store);
                ref.setUuid(node.getId());
                Predicate predicate = new Predicate(new Reference[] { ref }, null, null);
                NodeDefinition[] nodeDefs = WebServiceFactory.getRepositoryService().describe(predicate);
                assertNotNull("SearchTest nodeDefs is null", nodeDefs);
                assertTrue("SearchTest expected only 1 node match", nodeDefs.length == 1);

                List<PropertyDefinition> nodeProperties = new ArrayList<PropertyDefinition>();
                
                ClassDefinition type = nodeDefs[0].getType();
                assertNotNull("SearchTest node type is null", type);
                PropertyDefinition[] propDefs = type.getProperties();
                if(null != propDefs) {
                    for(PropertyDefinition propDef : propDefs) {
                        System.out.println("Adding " + propDef.getName() + " to node properties.");
                        nodeProperties.add(propDef);
                    }
                }

                for(ClassDefinition classDef : nodeDefs[0].getAspects()) {
                    System.out.println("SearchTest classDef name = " + classDef.getName());
                    propDefs = classDef.getProperties();
                    if(null != propDefs) {
                        for(PropertyDefinition propDef : propDefs) {
                            if(propDef.isMandatory()) {
                                System.out.println("Adding " + propDef.getName() + " from aspect " + classDef.getName() + " to node properties.");
                                nodeProperties.add(propDef);
                            } else {
                                System.out.println("Property " + propDef.getName() + " from aspect " + classDef.getName() + " is not mandatory, not adding to node properties.");                                
                            }
                        }
                    }
                }

                // Compare properties
                Map<String, NamedValue> searchProperties = new HashMap<String, NamedValue>();
                for(NamedValue column : row.getColumns()) {
                    System.out.println("Adding " + column.getName() + " to search properties.");
                    searchProperties.put(column.getName(), column);
                }
                String[] missingProperties = getMissingProperties(nodeProperties, searchProperties);

                if(missingProperties.length > 0) {
                    StringBuilder sb = new StringBuilder("Search results do not include all node properties\n");
                    for(String missingProp : missingProperties) {
                        sb.append("Missing property: " + missingProp + "\n");
                    }
                    fail(sb.toString());
                }
            }
            
        } else
        {
            logger.debug("The query returned no results");
            fail("The query returned no results");
        }
    }    
    
    /**
     * Tests the queryParents service method
     */
    public void testQueryParents() throws Exception
    {
        // query for all the child nodes of the root
        Reference node = BaseWebServiceSystemTest.rootReference;
        String rootId = node.getUuid();

        QueryResult rootChildren = WebServiceFactory.getRepositoryService().queryChildren(node);

        assertNotNull("rootChildren should not be null", rootChildren);
        ResultSet rootChildrenResults = rootChildren.getResultSet();
        assertNotNull("rootChildrenResults should not be null",
                rootChildrenResults);
        assertTrue("There should be at least one child of the root node",
                rootChildrenResults.getRows().length > 0);

        // get hold of the id of the first child
        ResultSetRow firstRow = rootChildrenResults.getRows(0);
        assertNotNull("getColumns() should not return null", firstRow
                .getColumns());
        String id = firstRow.getNode().getId();
        logger.debug("Retrieving parents for first node found: " + id + "....");

        node = new Reference();
        node.setStore(BaseWebServiceSystemTest.store);
        node.setUuid(id);
        QueryResult parents = WebServiceFactory.getRepositoryService().queryParents(node);

        assertNotNull("parents should not be null", parents);
        ResultSet parentsResults = parents.getResultSet();
        assertNotNull("parentsResults should not be null", parentsResults);
        assertTrue("There should be at least one parent", parentsResults
                .getRows().length > 0);

        // show the results
        boolean rootFound = false;
        ResultSetRow[] rows = parentsResults.getRows();
        logger.debug("There are " + rows.length + " rows:");
        for (int x = 0; x < rows.length; x++)
        {
            ResultSetRow row = rows[x];
            assertNotNull("getColumns() should not return null", row
                    .getColumns());
            ResultSetRowNode rowNode = row.getNode();
            String nodeId = rowNode.getId();
            logger.debug("parent node = " + nodeId + ", type = "
                    + rowNode.getType());
            NamedValue[] columns = row.getColumns();
            for (int y = 0; y < columns.length; y++)
            {
                logger.debug("row " + x + ": "
                        + row.getColumns(y).getName() + " = "
                        + row.getColumns(y).getValue());
            }
   
            // Check that the aspects are being set
            assertNotNull(rowNode);
            assertNotNull(rowNode.getId());
            assertNotNull(rowNode.getType());   
            String[] aspects = rowNode.getAspects();
            assertNotNull(aspects);

            if (nodeId.equals(rootId) == true)
            {
                rootFound = true;
            }
        }

        // make sure the root node was one of the parents
        assertTrue("The root node was not found as one of the parents!!",
                rootFound);
    }

    /**
     * Tests the queryChildren service method
     */
    public void testQueryChildren() throws Exception
    {
        // query for all the child nodes of the root
        Reference node = BaseWebServiceSystemTest.rootReference;
        QueryResult rootChildren = WebServiceFactory.getRepositoryService().queryChildren(node);

        assertNotNull("rootChildren should not be null", rootChildren);
        ResultSet rootChildrenResults = rootChildren.getResultSet();
        assertNotNull("rootChildrenResults should not be null",
                rootChildrenResults);
        assertTrue("There should be at least one child of the root node",
                rootChildrenResults.getRows().length > 0);

        // show the results
        ResultSetRow[] rows = rootChildrenResults.getRows();
        logger.debug("There are " + rows.length + " rows:");
        for (int x = 0; x < rows.length; x++)
        {
            ResultSetRow row = rows[x];
            assertNotNull("getColumns() should not return null", row
                    .getColumns());
            ResultSetRowNode rowNode = row.getNode();
            String nodeId = rowNode.getId();
            logger.debug("child node = " + nodeId + ", type = "
                    + rowNode.getType());
            
            // Check that the aspects are being set
            assertNotNull(rowNode);
            assertNotNull(rowNode.getId());
            assertNotNull(rowNode.getType());   
            String[] aspects = rowNode.getAspects();
            assertNotNull(aspects);
            
            NamedValue[] columns = row.getColumns();
            for (int y = 0; y < columns.length; y++)
            {
                logger.debug("row " + x + ": "
                        + row.getColumns(y).getName() + " = "
                        + row.getColumns(y).getValue());
            }            
        }
    }
            
    /**
     * Tests the queryAssociated service method
     */
    public void testQueryAssociated() throws Exception
    {
        Association association = new Association(Constants.createQNameString(
                Constants.NAMESPACE_CONTENT_MODEL, "attachments"),
                "target");
        QueryResult result = WebServiceFactory.getRepositoryService().queryAssociated(BaseWebServiceSystemTest.contentReference, association);
        assertNotNull(result);
        assertNotNull(result.getResultSet());
        assertNotNull(result.getResultSet().getRows());
        assertEquals(1, result.getResultSet().getRows().length);

        logger.debug("There is 1 result row:");

        ResultSetRow row = result.getResultSet().getRows()[0];
        NamedValue[] columns = row.getColumns();
        for (int y = 0; y < columns.length; y++)
        {
          logger.debug("row 0" + ": "
              + row.getColumns(y).getName() + " = "
              + row.getColumns(y).getValue());
        }     
        
        // Check that the aspects are being set
        ResultSetRowNode rowNode = row.getNode();
        assertNotNull(rowNode);
        assertEquals(BaseWebServiceSystemTest.contentReference2.getUuid(), rowNode.getId());
        assertNotNull(rowNode.getType());   
        String[] aspects = rowNode.getAspects();
        assertNotNull(aspects);
        
        // Now query the other way
        Association association2 = new Association(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "attachments"), "source");
		QueryResult result2 = WebServiceFactory.getRepositoryService().queryAssociated(BaseWebServiceSystemTest.contentReference2, association2);
		assertNotNull(result2);
		assertNotNull(result2.getResultSet());
		assertNotNull(result2.getResultSet().getRows());
		assertEquals(1, result2.getResultSet().getRows().length);
		ResultSetRow row2 = result2.getResultSet().getRows()[0];
		ResultSetRowNode rowNode2 = row2.getNode();
        assertNotNull(rowNode2);
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), rowNode2.getId());  
        assertNotNull(rowNode2.getType());   
        String[] aspects2 = rowNode2.getAspects();
        assertNotNull(aspects2);
    }

    /**
     * Tests the describe service method
     */
    public void testDescribe() throws Exception
    {
        // get hold of a node we know some info about so we can test the
        // returned values (the Alfresco Tutorial PDF)
        Query query = new Query(Constants.QUERY_LANG_LUCENE,
                "( +@\\{http\\://www.alfresco.org/1.0\\}name:test*) OR  TEXT:test*");
        QueryResult queryResult = this.repositoryService.query(BaseWebServiceSystemTest.store, query,
                false);
        assertNotNull("queryResult should not be null", queryResult);
        ResultSet resultSet = queryResult.getResultSet();
        assertNotNull("The result set should not be null", resultSet);
        assertTrue("There should be at least one result",
                resultSet.getTotalRowCount() > 0);
        String id = resultSet.getRows(0).getNode().getId();
        assertNotNull("Id of Alfresco Tutorial PDF should not be null", id);

        // create a predicate object to to send to describe method
        Reference ref = new Reference();
        ref.setStore(BaseWebServiceSystemTest.store);
        ref.setUuid(id);
        Predicate predicate = new Predicate(new Reference[] { ref }, null, null);

        // make the service call
        NodeDefinition[] nodeDefs = WebServiceFactory.getRepositoryService().describe(predicate);
        assertNotNull("nodeDefs should not be null", nodeDefs);
        assertTrue("There should only be one result", nodeDefs.length == 1);

        // get the result
        NodeDefinition nodeDef = nodeDefs[0];
        assertNotNull("The nodeDef should not be null", nodeDef);
        ClassDefinition typeDef = nodeDef.getType();
        assertNotNull("Type definition should not be null", typeDef);

        assertEquals("Type name is incorrect",
                "{http://www.alfresco.org/model/content/1.0}content",
                typeDef.getName());
        assertEquals("Superclass type name is incorrect",
                "{http://www.alfresco.org/model/content/1.0}cmobject",
                typeDef.getSuperClass());
        assertEquals("Type title is incorrect", "Content", typeDef.getTitle());
        assertEquals("Type description is incorrect", "Base Content Object",
                typeDef.getDescription());
        assertFalse("Type is an aspect and it shouldn't be",
                typeDef.isIsAspect());
        assertNull("There should not be any associations",
                typeDef.getAssociations());
        assertNotNull("Properties should not be null", typeDef.getProperties());
        assertEquals("There should be 2 properties", 2, typeDef.getProperties().length);

        // Check that we have all some of the properties we expect
        PropertyDefinition[] propertyDefs = typeDef.getProperties();
        boolean foundContent = false;
        boolean foundName = false;
        for (PropertyDefinition propertyDef : propertyDefs)
        {
            String name = propertyDef.getName();
            if (name.equals("{http://www.alfresco.org/model/content/1.0}content"))
            {
                foundContent = true;
                assertEquals("cm:content property data type is incorrect",
                        "{http://www.alfresco.org/model/dictionary/1.0}content",
                        propertyDef.getDataType());
            }
            else if (name.equals("{http://www.alfresco.org/model/content/1.0}name"))
            {
                foundName = true;
                assertEquals("cm:name property data type is incorrect",
                        "{http://www.alfresco.org/model/dictionary/1.0}text",
                        propertyDef.getDataType());
            }
        }
        assertTrue(foundContent);
        assertTrue(foundName);
        
        // check the aspects
        ClassDefinition[] aspectDefs = nodeDef.getAspects();
        assertNotNull("aspects should not be null", aspectDefs);
        assertEquals("There should be 3 aspects", 3, aspectDefs.length);

        for (ClassDefinition aspectDef : aspectDefs)
        {
            assertTrue("Not an aspect", aspectDef.isIsAspect());
            //assertNotNull("Aspect should have properties", aspectDef.getProperties());

            String name = aspectDef.getName();
            if (name.equals("{http://www.alfresco.org/model/system/1.0}referenceable"))
            {
                assertEquals("Wrong number of properties", 4, aspectDef.getProperties().length);
            }
            else if (name.equals("{http://www.alfresco.org/model/content/1.0}auditable"))
            {
                assertEquals("Wrong number of properties", 5, aspectDef.getProperties().length);
            }
        }
    }

    /**
     * Tests passing a query in the predicate to return items to describe
     * 
     * @throws Exception
     */
    public void testPredicateQuery() throws Exception
    {
        // define a query to add to the predicate (get everything that mentions
        // 'test')
        Query query = new Query(Constants.QUERY_LANG_LUCENE,
                "( +@\\{http\\://www.alfresco.org/1.0\\}name:test*) OR  TEXT:test*");

        Predicate predicate = new Predicate();
        predicate.setQuery(query);
        predicate.setStore(BaseWebServiceSystemTest.store);

        // call the service and make sure we get some details back
        NodeDefinition[] nodeDefs = WebServiceFactory.getRepositoryService().describe(predicate);
        assertNotNull("nodeDefs should not be null", nodeDefs);
        assertTrue("There should be at least one result", nodeDefs.length > 0);

        NodeDefinition nodeDef = nodeDefs[0];
        assertNotNull("The nodeDef should not be null", nodeDef);
        ClassDefinition typeDef = nodeDef.getType();
        assertNotNull("Type definition should not be null", typeDef);

        logger.debug("type name = " + typeDef.getName());
        logger.debug("is aspect = " + typeDef.isIsAspect());
        PropertyDefinition[] propDefs = typeDef.getProperties();
        if (propDefs != null)
        {
            logger.debug("There are " + propDefs.length + " properties:");
            for (int x = 0; x < propDefs.length; x++)
            {
                PropertyDefinition propDef = propDefs[x];
                logger.debug("name = " + propDef.getName() + " type = "
                        + propDef.getDataType());
            }
        }
    }

    /**
     * Tests the use of a path within a reference
     * 
     * @throws Exception
     */
    public void testPathReference() throws Exception
    {
        // setup a predicate to find the test folder using an xpath
        Reference ref = new Reference();
        ref.setStore(BaseWebServiceSystemTest.store);
        ref.setPath("//*[@cm:name = '" + FOLDER_NAME + "']");
        Predicate predicate = new Predicate();
        predicate.setNodes(new Reference[] { ref });

        // call the service and make sure we get some details back
        NodeDefinition[] nodeDefs = WebServiceFactory.getRepositoryService().describe(predicate);
        assertNotNull("nodeDefs should not be null", nodeDefs);
        assertTrue("There should be at least one result", nodeDefs.length > 0);

        NodeDefinition nodeDef = nodeDefs[0];
        assertNotNull("The nodeDef should not be null", nodeDef);
        ClassDefinition typeDef = nodeDef.getType();
        assertNotNull("Type definition should not be null", typeDef);

        logger.debug("type name = " + typeDef.getName());
        assertEquals("Type is incorrect",
                "{http://www.alfresco.org/model/content/1.0}folder", typeDef
                        .getName());
        logger.debug("is aspect = " + typeDef.isIsAspect());
        assertFalse("Item should not be an aspect", typeDef.isIsAspect());
        PropertyDefinition[] propDefs = typeDef.getProperties();
        if (propDefs != null)
        {
            logger.debug("There are " + propDefs.length + " properties:");
            for (int x = 0; x < propDefs.length; x++)
            {
                PropertyDefinition propDef = propDefs[x];
                logger.debug("name = " + propDef.getName() + " type = "
                        + propDef.getDataType());
            }
        }
    }

    /**
     * Tests the update service method
     * 
     * @throws Exception
     */
    public void testUpdate() throws Exception
    {
        CMLCreate create = new CMLCreate();
        create.setId("id1");
        create.setType(Constants.TYPE_CONTENT);

        ParentReference parentReference = new ParentReference();
        parentReference.setAssociationType(Constants.ASSOC_CHILDREN);
        parentReference.setChildName(Constants.ASSOC_CHILDREN);
        parentReference.setStore(BaseWebServiceSystemTest.store);
        parentReference.setUuid(BaseWebServiceSystemTest.rootReference.getUuid());

        create.setParent(parentReference);
        create.setProperty(new NamedValue[] {
                        new NamedValue(
                                Constants.PROP_NAME,
                                false,
                                "name.txt",
                                null)});
        
        // Create a folder used for later tests
        CMLCreate createFolder = new CMLCreate();
        createFolder.setId("folder1");
        createFolder.setType(Constants.TYPE_FOLDER);
        createFolder.setParent(parentReference);
        createFolder.setProperty(new NamedValue[] {
                new NamedValue(
                        Constants.PROP_NAME,
                        false,
                        "tempFolder",
                        null)});
        
        CMLAddAspect aspect = new CMLAddAspect();
        aspect.setAspect(Constants.ASPECT_VERSIONABLE);
        aspect.setWhere_id("id1");

        ContentFormat format = new ContentFormat(Constants.MIMETYPE_TEXT_PLAIN, "UTF-8");
        
        CMLWriteContent write = new CMLWriteContent();
        write.setProperty(Constants.PROP_CONTENT);
        write.setContent("this is a test".getBytes());
        write.setFormat(format);
        write.setWhere_id("id1");
        
        CML cml = new CML();
        cml.setCreate(new CMLCreate[]{create, createFolder});
        cml.setAddAspect(new CMLAddAspect[]{aspect});
        cml.setWriteContent(new CMLWriteContent[]{write});
        
        UpdateResult[] results = WebServiceFactory.getRepositoryService().update(cml);
        assertNotNull(results);
        assertEquals(4, results.length);
        
        // Get a reference to the create node
        Reference reference = results[0].getDestination();
        Reference folderReference = results[1].getDestination();
        
        // Check that the content has been set successfully
        Content[] content = WebServiceFactory.getContentService().read(new Predicate(new Reference[]{reference}, null, null), Constants.PROP_CONTENT);
        assertNotNull(content);
        assertEquals(1, content.length);
        assertEquals("this is a test", ContentUtils.getContentAsString(content[0]));

        // Try and copy the reference into the folder
        CMLCopy copy = new CMLCopy();
        copy.setTo(new ParentReference(folderReference.getStore(), folderReference.getUuid(), null, Constants.ASSOC_CONTAINS, "{" + Constants.NAMESPACE_CONTENT_MODEL + "}name.txt"));
        copy.setWhere(new Predicate(new Reference[]{reference}, null, null));
        CML cmlCopy = new CML();
        cmlCopy.setCopy(new CMLCopy[]{copy});
        
        UpdateResult[] results2 = WebServiceFactory.getRepositoryService().update(cmlCopy);
        assertNotNull(results2);
        assertEquals(1, results2.length);        
        Reference newCopy = results2[0].getDestination();
        assertNotNull(newCopy);
        
        // Check that the name has been set correctly
        Node[] nodes = this.repositoryService.get(new Predicate(new Reference[]{newCopy}, null, null));
        Node node = nodes[0];
        boolean checked = false;
        for (NamedValue namedValue : node.getProperties())
        {
            if (namedValue.getName().equals(Constants.PROP_NAME) == true)
            {
                assertEquals("name.txt", namedValue.getValue());
                checked = true;
            }
        }
        assertTrue(checked);
        
        // Try and copy the reference into the same folde and check for rename
        CMLCopy copy2 = new CMLCopy();
        copy2.setTo(new ParentReference(folderReference.getStore(), folderReference.getUuid(), null, Constants.ASSOC_CONTAINS, "{" + Constants.NAMESPACE_CONTENT_MODEL + "}name.txt"));
        copy2.setWhere(new Predicate(new Reference[]{reference}, null, null));
        CML cmlCopy2 = new CML();
        cmlCopy2.setCopy(new CMLCopy[]{copy2});
        
        UpdateResult[] results22 = WebServiceFactory.getRepositoryService().update(cmlCopy2);
        assertNotNull(results22);
        assertEquals(1, results22.length);        
        Reference newCopy2 = results22[0].getDestination();
        assertNotNull(newCopy2);
        
        // Check that the name has been set correctly
        Node[] nodes2 = this.repositoryService.get(new Predicate(new Reference[]{newCopy2}, null, null));
        Node node2 = nodes2[0];
        boolean checked2 = false;
        for (NamedValue namedValue : node2.getProperties())
        {
            if (namedValue.getName().equals(Constants.PROP_NAME) == true)
            {
                assertFalse("name.txt".equals(namedValue.getValue()));
                assertTrue(namedValue.getValue().contains("name.txt"));
                checked2 = true;
            }
        }
        assertTrue(checked2);
        
        // Check that the folder does indeed have the copied reference
        QueryResult result = this.repositoryService.queryChildren(folderReference);
        assertEquals(2, result.getResultSet().getTotalRowCount());
        
        // Test delete
        CMLDelete delete = new CMLDelete();
        delete.setWhere(new Predicate(new Reference[]{newCopy}, null, null));
        CML cmlDelete = new CML();
        cmlDelete.setDelete(new CMLDelete[]{delete});
        
        UpdateResult[] results3 = WebServiceFactory.getRepositoryService().update(cmlDelete);
        assertNotNull(results3);
        assertEquals(1, results3.length);
        UpdateResult updateResult3 = results3[0];
        assertNull(updateResult3.getDestination());
        assertEquals(newCopy.getUuid(), updateResult3.getSource().getUuid());        
    }
    
    // Test for creation with space in file name
    public void testADB12()
    	throws Exception
    {
    	String fileName = "this is my file.txt";
    	String folderName = "this is my folder";
    	
    	CMLCreate create = new CMLCreate();
        create.setId("id1");
        create.setType(Constants.TYPE_CONTENT);

        ParentReference parentReference = new ParentReference();
        parentReference.setAssociationType(Constants.ASSOC_CHILDREN);
        parentReference.setChildName(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, fileName));
        parentReference.setStore(BaseWebServiceSystemTest.store);
        parentReference.setUuid(BaseWebServiceSystemTest.rootReference.getUuid());

        create.setParent(parentReference);
        create.setProperty(new NamedValue[] {
                        new NamedValue(
                                Constants.PROP_NAME,
                                false,
                                fileName,
                                null)});
        
        // Create a folder used for later tests
        ParentReference parentReference2 = new ParentReference();
        parentReference2.setAssociationType(Constants.ASSOC_CHILDREN);
        parentReference2.setChildName(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, folderName));
        parentReference2.setStore(BaseWebServiceSystemTest.store);
        parentReference2.setUuid(BaseWebServiceSystemTest.rootReference.getUuid());
        CMLCreate createFolder = new CMLCreate();
        createFolder.setId("folder1");
        createFolder.setType(Constants.TYPE_FOLDER);
        createFolder.setParent(parentReference2);
        createFolder.setProperty(new NamedValue[] {
                new NamedValue(
                        Constants.PROP_NAME,
                        false,
                        folderName,
                        null)});

        ContentFormat format = new ContentFormat(Constants.MIMETYPE_TEXT_PLAIN, "UTF-8");
        
        CMLWriteContent write = new CMLWriteContent();
        write.setProperty(Constants.PROP_CONTENT);
        write.setContent("this is a test".getBytes());
        write.setFormat(format);
        write.setWhere_id("id1");
        
        CML cml = new CML();
        cml.setCreate(new CMLCreate[]{create, createFolder});
        cml.setWriteContent(new CMLWriteContent[]{write});
        
        UpdateResult[] results = WebServiceFactory.getRepositoryService().update(cml);
        assertNotNull(results);
        assertEquals(3, results.length);
        
        // Get a reference to the create node
        Reference reference = results[0].getDestination();
        assertNotNull(reference);
        Reference folderReference = results[1].getDestination();
        assertNotNull(folderReference);
    }
    
    public void testGet() 
        throws Exception
    {
        Predicate predicate = new Predicate(null, BaseWebServiceSystemTest.store, null);
        Node[] nodes = WebServiceFactory.getRepositoryService().get(predicate);
        assertNotNull(nodes);
        assertEquals(1, nodes.length);
        
        Node rootNode = nodes[0];
        assertEquals(BaseWebServiceSystemTest.rootReference.getUuid(), rootNode.getReference().getUuid());
        
        logger.debug("Root node type = " + rootNode.getType());
        String aspects = "";
        for (String aspect : rootNode.getAspects())
        {
            aspects += aspect + ", ";
        }
        logger.debug("Root node aspects = " + aspects);
        for (NamedValue prop : rootNode.getProperties())
        {
            logger.debug("Root node property " + prop.getName() + " = " + prop.getValue());
        }        
    }

    /**
     * Test that the uuid and path are both returned in a Reference object
     * @throws Exception
     */
    public void testGetPath() throws Exception
    {      
      Predicate predicate = new Predicate(new Reference[]{BaseWebServiceSystemTest.folderReference}, null, null);   
      Node[] nodes = WebServiceFactory.getRepositoryService().get(predicate);
      assertNotNull(nodes);
      assertEquals(1, nodes.length);
      Node node = nodes[0];
      String path = node.getReference().getPath();
      String uuid = node.getReference().getUuid();
      
      logger.debug("Folder reference path = " + BaseWebServiceSystemTest.folderReference.getPath());
      logger.debug("Retrieved node path = " + path);
      logger.debug("Retrieved node uuid = " + uuid);
      
      assertNotNull(path);
      assertNotNull(uuid);
      assertEquals(BaseWebServiceSystemTest.folderReference.getPath(), path);
      
    }    
    
    public void testPropertySetGet() throws Exception
    {
        // Load a dynamic custom model using the cm:dictionaryModel type
        CMLCreate create = new CMLCreate();
        create.setId("id1");
        create.setType(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "dictionaryModel"));

        ParentReference parentReference = new ParentReference(new Store(Constants.WORKSPACE_STORE, "SpacesStore"), null, "/app:company_home/app:dictionary/app:models", Constants.ASSOC_CONTAINS, Constants.ASSOC_CONTAINS);                    
        
        create.setParent(parentReference);
        create.setProperty(new NamedValue[] {
                        new NamedValue(
                                Constants.PROP_NAME,
                                false,
                                "testModel.xml",
                                null),
                        new NamedValue(
                                Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "modelActive"),
                                false,
                                "true",
                                null)});
        
        CML cml = new CML();
        cml.setCreate(new CMLCreate[]{create});
        UpdateResult[] results = WebServiceFactory.getRepositoryService().update(cml);
        Reference model = results[0].getDestination();
        
        // Now add the content to the model
        InputStream viewStream = getClass().getClassLoader().getResourceAsStream("org/alfresco/webservice/test/resources/propertymodel.xml");
        byte[] bytes = ContentUtils.convertToByteArray(viewStream);
        this.contentService.write(model, Constants.PROP_CONTENT, bytes, new ContentFormat(Constants.MIMETYPE_XML, "UTF-8"));
        
        try
        {
            // Now create a node of the type specified in the model
            ParentReference parentReference2 = new ParentReference();
            parentReference2.setAssociationType(Constants.ASSOC_CHILDREN);
            parentReference2.setChildName(Constants.ASSOC_CHILDREN);
            parentReference2.setStore(BaseWebServiceSystemTest.store);
            parentReference2.setUuid(BaseWebServiceSystemTest.rootReference.getUuid());
            create = new CMLCreate();
            create.setId("id1");
            create.setType(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "testproperties"));
            create.setParent(parentReference2);
            cml = new CML();
            cml.setCreate(new CMLCreate[]{create});
            UpdateResult[] results2 = WebServiceFactory.getRepositoryService().update(cml);
            Reference reference = results2[0].getDestination();
            
            Collection<String> list = new ArrayList<String>();
            list.add("Filrst sadf d");
            list.add("Seconf sdfasdf");
            System.out.println(list.toString());
            
            
            System.out.println(new String[] {"firstValue", "secondValue", "thirdValue"}.toString());
            
            // Now we can try and set all the various different types of properties
            NamedValue[] properties = new NamedValue[]{
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "textProp"), "some text"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "intProp"), "12"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "longProp"), "1234567890"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "floatProp"), "12.345"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "doubleProp"), "12.345"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "dateProp"), "2005-09-16T00:00:00.000+00:00"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "datetimeProp"), "2005-09-16T17:01:03.456+01:00"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "booleanProp"), "false"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "qnameProp"), "{http://www.alfresco.org/model/webservicetestmodel/1.0}testProperties"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "noderefProp"), "workspace://SpacesStore/123123123"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "textMultiProp"), new String[] {"firstValue", "secondValue", "thirdValue"}),                    
            };
            CMLUpdate cmlUpdate = new CMLUpdate(properties, new Predicate(new Reference[]{reference}, null, null), null);
            cml = new CML();
            cml.setUpdate(new CMLUpdate[]{cmlUpdate});
            WebServiceFactory.getRepositoryService().update(cml);
            
            // Output all the set property values for visual inspection
            Node[] nodes = WebServiceFactory.getRepositoryService().get(new Predicate(new Reference[]{reference}, null, null));
            Node node = nodes[0];            
            for(NamedValue namedValue : node.getProperties())
            {
                if (namedValue.getIsMultiValue() == null || namedValue.getIsMultiValue() == false)
                {
                    System.out.println(namedValue.getName() + " = " + namedValue.getValue());
                }
                else
                {
                    System.out.print(namedValue.getName() + " = ");
                    for (String value : namedValue.getValues())
                    {
                        System.out.print(value + " ");
                    }
                    System.out.println("");
                }
            }            
        }
        finally
        {
            // Need to delete the model from the spaces store to tidy things up
            Predicate where = new Predicate(new Reference[]{model}, null, null);
            CMLDelete cmlDelete = new CMLDelete(where);
            cml = new CML();
            cml.setDelete(new CMLDelete[]{cmlDelete});
            WebServiceFactory.getRepositoryService().update(cml);
        }
    }
    
    public void testFolderCreate()
        throws Exception
    {
        Reference newFolder = createFolder(BaseWebServiceSystemTest.rootReference, "123TestFolder");
        assertNotNull(newFolder);
        Reference newFolder2 = createFolder(BaseWebServiceSystemTest.rootReference, "2007");
        assertNotNull(newFolder2);
    }
    
    public void testPathLookup()
        throws Exception
    {
        Reference newFolder = createFolder(BaseWebServiceSystemTest.rootReference, "A Test Folder");
        queryForFolder(newFolder.getPath(), newFolder);
        queryForFolder("/cm:" + ISO9075.encode("A Test Folder"), newFolder);
    }
    
    /**
     * Tests the ability to retrieve the results of a query in batches
     */
    public void xtestQuerySession() throws Exception
    {
        // define a query that will return a lot of hits i.e. EVERYTHING
        Query query = new Query(Constants.QUERY_LANG_LUCENE, "*");

        // add the query configuration header to the call
        int batchSize = 5;
        QueryConfiguration queryCfg = new QueryConfiguration();
        queryCfg.setFetchSize(batchSize);
        WebServiceFactory.getRepositoryService().setHeader(new RepositoryServiceLocator()
                .getServiceName().getNamespaceURI(), "QueryHeader", queryCfg);

        // get the first set of results back
        QueryResult queryResult = WebServiceFactory.getRepositoryService().query(BaseWebServiceSystemTest.store, query,
                false);
        assertNotNull("queryResult should not be null", queryResult);
        String querySession = queryResult.getQuerySession();
        String origQuerySession = querySession;
        assertNotNull("querySession should not be null", querySession);

        ResultSet resultSet = queryResult.getResultSet();
        assertNotNull("The result set should not be null", resultSet);
        logger.debug("There are " + resultSet.getTotalRowCount()
                + " rows in total");
        logger.debug("There are " + resultSet.getRows().length
                + " rows in the first set");
        assertTrue("The result set size should be " + batchSize, (resultSet.getRows().length==batchSize));

        // get the next batch of results
        queryResult = WebServiceFactory.getRepositoryService().fetchMore(querySession);
        assertNotNull("queryResult should not be null", queryResult);
        querySession = queryResult.getQuerySession();
        assertNotNull("querySession should not be null", querySession);

        ResultSet resultSet2 = queryResult.getResultSet();
        assertNotNull("The second result set should not be null", resultSet2);
        logger.debug("There are " + resultSet2.getRows().length
                + " rows in the second set");
        assertEquals("The result set size should be " + batchSize, batchSize,
                resultSet2.getRows().length);

        // get the rest of the results to make sure it finishes properly
        while (querySession != null)
        {
            queryResult = WebServiceFactory.getRepositoryService().fetchMore(querySession);
            assertNotNull("queryResult returned in loop should not be null",
                    queryResult);
            querySession = queryResult.getQuerySession();
            logger.debug("There were another "
                    + queryResult.getResultSet().getRows().length
                    + " rows returned");
        }

        // try and fetch some more results and we should get an error
        try
        {
            queryResult = WebServiceFactory.getRepositoryService().fetchMore(origQuerySession);
            fail("We should have seen an error as all the results have been returned");
        } catch (Exception e)
        {
            // expected
        }
    }
    
    private Reference createFolder(Reference parent, String folderName)
        throws Exception
    {
        ParentReference parentReference = new ParentReference();
        parentReference.setAssociationType(Constants.ASSOC_CHILDREN);
        parentReference.setChildName(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, folderName));
        parentReference.setStore(parent.getStore());
        parentReference.setUuid(parent.getUuid());
        
        NamedValue[] properties = new NamedValue[]{Utils.createNamedValue(Constants.PROP_NAME, folderName)};
        CMLCreate create = new CMLCreate("1", parentReference, null, null, null, Constants.TYPE_FOLDER, properties);
        CML cml = new CML();
        cml.setCreate(new CMLCreate[]{create});
        
        UpdateResult[] results = WebServiceFactory.getRepositoryService().update(cml);
        
        return results[0].getDestination();
    }
    
    private void queryForFolder(String pathName, Reference expected) 
        throws Exception
    {
        Reference folderRef = new Reference(BaseWebServiceSystemTest.store, null, pathName);
        Node[] nodes = repositoryService.get(new Predicate(new Reference[]{folderRef}, null, null));
        if( nodes == null || nodes.length < 1 ) 
        {
            fail("No such folder found.");
        } 
        else if( nodes.length > 1) 
        {
            fail("Found more than one reference--should only be one.");
        } 
        else 
        {
            Reference ref = nodes[0].getReference();
            assertNotNull(ref);
            assertEquals(expected.getUuid(), ref.getUuid());
            assertEquals(expected.getPath(), ref.getPath());
        } 
    }
}
