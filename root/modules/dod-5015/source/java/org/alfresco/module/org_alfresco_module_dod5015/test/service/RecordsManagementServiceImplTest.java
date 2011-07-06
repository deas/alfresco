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
package org.alfresco.module.org_alfresco_module_dod5015.test.service;

import java.util.List;
import java.util.Set;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementServiceImpl;
import org.alfresco.module.org_alfresco_module_dod5015.model.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.test.util.BaseRMTestCase;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.springframework.util.CollectionUtils;


/**
 * Records management service test.
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementServiceImplTest extends BaseRMTestCase
{    
    /********** RM Component methods **********/
    
    /**
     * @see RecordsManagementService#isRecordsManagmentComponent(org.alfresco.service.cmr.repository.NodeRef)
     */
    public void testIsRecordsManagmentComponent() throws Exception
    {
        doTestInTransaction(new Test<NodeRef>()
        {
            @Override
            public NodeRef run()
            {
                assertTrue("The rm root container should be a rm component", rmService.isRecordsManagmentComponent(rmRootContainer));
                assertTrue("The rm container should be a rm component", rmService.isRecordsManagmentComponent(rmContainer));
                assertTrue("The rm folder should be a rm component", rmService.isRecordsManagmentComponent(rmFolder));
                
                return null;
            }
        });
    }
    
    /**
     * @see RecordsManagementService#isRecordsManagementRoot(NodeRef)
     */
    public void testIsRecordsManagmentRoot() throws Exception
    {
        doTestInTransaction(new Test<NodeRef>()
        {
            @Override
            public NodeRef run()
            {
                assertTrue("This is a records management root", rmService.isRecordsManagementRoot(rmRootContainer));
                assertFalse("This should not be a records management root", rmService.isRecordsManagementRoot(rmContainer));
                assertFalse("This should not be a records management root", rmService.isRecordsManagementRoot(rmFolder));
                
                return null;
            }
        });
    }
    
    /**
     * @see RecordsManagementService#isRecordsManagementContainer(NodeRef)
     */
    public void testIsRecordsManagementContainer() throws Exception
    {
        doTestInTransaction(new Test<NodeRef>()
        {
            @Override
            public NodeRef run()
            {
                assertTrue("This is a records management container", rmService.isRecordsManagementContainer(rmRootContainer));
                assertTrue("This is a records management container", rmService.isRecordsManagementContainer(rmContainer));
                assertFalse("This should not be a records management container", rmService.isRecordsManagementContainer(rmFolder));
                
                return null;
            }
        });
    }
    
    /**
     * @see RecordsManagementService#isRecordFolder(NodeRef)
     */
    public void testIsRecordFolder() throws Exception
    {
        doTestInTransaction(new Test<NodeRef>()
        {
            @Override
            public NodeRef run()
            {
                assertFalse("This should not be a record folder", rmService.isRecordFolder(rmRootContainer));
                assertFalse("This should not be a record folder", rmService.isRecordFolder(rmContainer));
                assertTrue("This should be a record folder", rmService.isRecordFolder(rmFolder));
                
                return null;
            }
        });
    }
    
    // TODO void testIsRecord()
    
    // TODO void testGetNodeRefPath()
    
    /**
     * @see RecordsManagementService#getRecordsManagementRoot()
     */
    public void testGetRecordsManagementRoot() throws Exception
    {
        doTestInTransaction(new Test<NodeRef>()
        {
            @Override
            public NodeRef run()
            {
                assertEquals(rmRootContainer, rmService.getRecordsManagementRoot(rmRootContainer));
                assertEquals(rmRootContainer, rmService.getRecordsManagementRoot(rmContainer));
                assertEquals(rmRootContainer, rmService.getRecordsManagementRoot(rmFolder));
                
                return null;
            }
        });
    }
    
    /********** Record Management Root methods **********/
    
    /**
     * @see RecordsManagementService#getRecordsManagementRoots()
     */
    public void testGetRecordsManagementRoots() throws Exception
    {
        doTestInTransaction(new Test<NodeRef>()
        {
            @Override
            public NodeRef run()
            {
                List<NodeRef> roots = rmService.getRecordsManagementRoots(storeRef);
                assertNotNull(roots);
                assertEquals(1, roots.size());
                assertEquals(rmRootContainer, roots.get(0));
                
                RecordsManagementServiceImpl temp = (RecordsManagementServiceImpl)applicationContext.getBean("recordsManagementService");
                temp.setDefaultStoreRef(storeRef);
                
                roots = rmService.getRecordsManagementRoots();
                assertNotNull(roots);
                assertEquals(1, roots.size());
                assertEquals(rmRootContainer, roots.get(0)); 
                
                return null;
            }
        });      
    }
    
    /**
     * @see RecordsManagementService#createRecordsManagementRoot(org.alfresco.service.cmr.repository.NodeRef, String)
     * @see RecordsManagementService#createRecordsManagementRoot(org.alfresco.service.cmr.repository.NodeRef, String, org.alfresco.service.namespace.QName)
     */
    public void testCreateRecordsManagementRoot() throws Exception
    {
        // Create default type of root
        doTestInTransaction(new Test<NodeRef>()
        {
            @Override
            public NodeRef run()
            {
                String id = setString("id", GUID.generate());
                return rmService.createRecordsManagementRoot(folder, id);
            }

            @Override
            public void test(NodeRef result)
            {
                assertNotNull("Unable to create records management root", result);
                basicRMContainerCheck(result, getString("id"), TYPE_RECORDS_MANAGEMENT_ROOT_CONTAINER);
            }
        });
        
        // Create specific type of root
        doTestInTransaction(new Test<NodeRef>()
        {
            @Override
            public NodeRef run()
            {
                String id = setString("id", GUID.generate());
                return rmService.createRecordsManagementRoot(folder, id, DOD5015Model.TYPE_FILE_PLAN);
            }

            @Override
            public void test(NodeRef result)
            {
                assertNotNull("Unable to create records management root", result);
                basicRMContainerCheck(result, getString("id"), DOD5015Model.TYPE_FILE_PLAN);
            }
        });
        
        // Failure: creating root in existing hierarchy
        doTestInTransaction(new FailureTest()
        {
            @Override
            public void run()
            {
                rmService.createRecordsManagementRoot(rmContainer, GUID.generate());                                
            }
        });
        
        // Failure: type no extended from root container
        doTestInTransaction(new FailureTest()
        {
            @Override
            public void run()
            {
                rmService.createRecordsManagementRoot(folder, GUID.generate(), TYPE_FOLDER);                                
            }
        });
    }
    
    /********** Records Management Container methods **********/
    
    /**
     * @see RecordsManagementService#createRecordsManagementContainer(NodeRef, String)
     * @see RecordsManagementService#createRecordsManagementContainer(NodeRef, String, org.alfresco.service.namespace.QName)
     */
    public void testCreateRecordsManagementContainer() throws Exception
    {
        // Create container (in root)
        doTestInTransaction(new Test<NodeRef>()
        {
            @Override
            public NodeRef run()
            {
                String id = setString("id", GUID.generate());
                return rmService.createRecordsManagementContainer(rmRootContainer, id);
            }

            @Override
            public void test(NodeRef result)
            {
                assertNotNull("Unable to create records management container", result);
                basicRMContainerCheck(result, getString("id"), TYPE_RECORDS_MANAGEMENT_CONTAINER);
            }
        });
        
        // Create container (in container)
        doTestInTransaction(new Test<NodeRef>()
        {
            @Override
            public NodeRef run()
            {
                String id = setString("id", GUID.generate());
                return rmService.createRecordsManagementContainer(rmContainer, id);
            }

            @Override
            public void test(NodeRef result)
            {
                assertNotNull("Unable to create records management container", result);
                basicRMContainerCheck(result, getString("id"), TYPE_RECORDS_MANAGEMENT_CONTAINER);
            }
        });
        
        // Create container of a given type
        doTestInTransaction(new Test<NodeRef>()
        {
            @Override
            public NodeRef run()
            {
                String id = setString("id", GUID.generate());
                return rmService.createRecordsManagementContainer(rmRootContainer, id, DOD5015Model.TYPE_RECORD_SERIES);
            }

            @Override
            public void test(NodeRef result)
            {
                assertNotNull("Unable to create records management container", result);
                basicRMContainerCheck(result, getString("id"), DOD5015Model.TYPE_RECORD_SERIES);
            }
        });
        
        // Fail Test: parent is not a container
        doTestInTransaction(new FailureTest()
        {
            @Override
            public void run()
            {
                rmService.createRecordsManagementContainer(folder, GUID.generate());                                
            }
        });
        
        // Fail Test: type is not a sub-type of rm:recordsManagementContainer
        doTestInTransaction(new FailureTest()
        {
            @Override
            public void run()
            {
                rmService.createRecordsManagementContainer(rmRootContainer, GUID.generate(), TYPE_FOLDER);                                
            }
        });
    }
    
    /**
     * @see RecordsManagementService#getAllContained(NodeRef)
     * @see RecordsManagementService#getAllContained(NodeRef, boolean)
     */
    public void testGetAllContained() throws Exception
    {
        // Get all contained test
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                // Add to the test data
                NodeRef series = rmService.createRecordsManagementContainer(rmContainer, "rmSeries", DOD5015Model.TYPE_RECORD_SERIES);
                NodeRef seriesChildFolder = rmService.createRecordFolder(series, "seriesRecordFolder");
                NodeRef seriesChildContainer = rmService.createRecordsManagementContainer(series, "childContainer");
                
                // Put in model
                setNodeRef("series", series);
                setNodeRef("seriesChildFolder", seriesChildFolder);
                setNodeRef("seriesChildContainer", seriesChildContainer);
                
                return null;
            }
            
            @Override
            public void test(Void result) throws Exception
            {               
                List<NodeRef> nodes = rmService.getAllContained(rmContainer);
                assertNotNull(nodes);
                assertEquals(2, nodes.size());                
                assertTrue(nodes.contains(getNodeRef("series")));
                assertTrue(nodes.contains(rmFolder));
                
                nodes = rmService.getAllContained(rmContainer, false);
                assertNotNull(nodes);
                assertEquals(2, nodes.size());                
                assertTrue(nodes.contains(getNodeRef("series")));
                assertTrue(nodes.contains(rmFolder));
                
                nodes = rmService.getAllContained(rmContainer, true);
                assertNotNull(nodes);
                assertEquals(4, nodes.size());                
                assertTrue(nodes.contains(getNodeRef("series")));
                assertTrue(nodes.contains(rmFolder));         
                assertTrue(nodes.contains(getNodeRef("seriesChildFolder")));
                assertTrue(nodes.contains(getNodeRef("seriesChildContainer")));

            }
        });
        
        // Failure: call on record folder
        doTestInTransaction(new FailureTest()
        {
            @Override
            public void run()
            {
                rmService.getAllContained(rmFolder);
            }
        });       
    }
    
    /**
     * @see RecordsManagementService#getContainedRecordsManagementContainers(NodeRef)
     * @see RecordsManagementService#getContainedRecordsManagementContainers(NodeRef, boolean)
     */
    public void testGetContainedRecordsManagementContainers() throws Exception
    {
        // Test getting all contained containers
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                // Add to the test data
                NodeRef series = rmService.createRecordsManagementContainer(rmContainer, "rmSeries", DOD5015Model.TYPE_RECORD_SERIES);
                NodeRef seriesChildFolder = rmService.createRecordFolder(series, "seriesRecordFolder");
                NodeRef seriesChildContainer = rmService.createRecordsManagementContainer(series, "childContainer");
                
                // Put in model
                setNodeRef("series", series);
                setNodeRef("seriesChildFolder", seriesChildFolder);
                setNodeRef("seriesChildContainer", seriesChildContainer);
                
                return null;
            }
            
            @Override
            public void test(Void result) throws Exception
            {               
                List<NodeRef> nodes = rmService.getContainedRecordsManagementContainers(rmContainer);
                assertNotNull(nodes);
                assertEquals(1, nodes.size()); 
                assertTrue(nodes.contains(getNodeRef("series")));      
                
                nodes = rmService.getContainedRecordsManagementContainers(rmContainer, false);
                assertNotNull(nodes);
                assertEquals(1, nodes.size());       
                assertTrue(nodes.contains(getNodeRef("series")));
                
                nodes = rmService.getContainedRecordsManagementContainers(rmContainer, true);
                assertNotNull(nodes);
                assertEquals(2, nodes.size());       
                assertTrue(nodes.contains(getNodeRef("series")));
                assertTrue(nodes.contains(getNodeRef("seriesChildContainer")));
            }
        });
        
        // Failure: call on record folder
        doTestInTransaction(new FailureTest()
        {
            @Override
            public void run()
            {
                rmService.getContainedRecordsManagementContainers(rmFolder);
            }
        });         
    }
    
    /**
     * @see RecordsManagementService#getContainedRecordFolders(NodeRef)
     * @see RecordsManagementService#getContainedRecordFolders(NodeRef, boolean)
     */
    public void testGetContainedRecordFolders() throws Exception
    {
        // Test getting all contained record folders
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                // Add to the test data
                NodeRef series = rmService.createRecordsManagementContainer(rmContainer, "rmSeries", DOD5015Model.TYPE_RECORD_SERIES);
                NodeRef seriesChildFolder = rmService.createRecordFolder(series, "seriesRecordFolder");
                NodeRef seriesChildContainer = rmService.createRecordsManagementContainer(series, "childContainer");
                
                // Put in model
                setNodeRef("series", series);
                setNodeRef("seriesChildFolder", seriesChildFolder);
                setNodeRef("seriesChildContainer", seriesChildContainer);
                
                return null;
            }
            
            @Override
            public void test(Void result) throws Exception
            {               
                List<NodeRef> nodes = rmService.getContainedRecordFolders(rmContainer);
                assertNotNull(nodes);
                assertEquals(1, nodes.size());              
                assertTrue(nodes.contains(rmFolder));           
                
                nodes = rmService.getContainedRecordFolders(rmContainer, false);
                assertNotNull(nodes);
                assertEquals(1, nodes.size());                      
                assertTrue(nodes.contains(rmFolder));   
                
                nodes = rmService.getContainedRecordFolders(rmContainer, true);
                assertNotNull(nodes);
                assertEquals(2, nodes.size());                   
                assertTrue(nodes.contains(rmFolder));      
                assertTrue(nodes.contains(getNodeRef("seriesChildFolder")));
            }
        });
        
        // Failure: call on record folder
        doTestInTransaction(new FailureTest()
        {
            @Override
            public void run()
            {
                rmService.getContainedRecordFolders(rmFolder);
            }
        });       
    }
    
    /********** Record Folder methods **********/    
    
    // TODO void testIsRecordFolderDeclared()
    
    // TODO void testGetRecords()
    
    /**
     * @see RecordsManagementService#createRecordFolder(NodeRef, String)
     * @see RecordsManagementService#createRecordFolder(NodeRef, String, QName)
     */
    public void testCreateRecordFolder() throws Exception
    {
        // Create record 
        doTestInTransaction(new Test<NodeRef>()
        {
            @Override
            public NodeRef run()
            {
                String id = setString("id", GUID.generate());
                return rmService.createRecordFolder(rmContainer, id);
            }

            @Override
            public void test(NodeRef result)
            {
                assertNotNull("Unable to create record folder", result);
                basicRMContainerCheck(result, getString("id"), TYPE_RECORD_FOLDER);
            }
        });
        
        // TODO Create record of type
        
        // Failure: Create record with invalid type
        doTestInTransaction(new FailureTest()
        {
            @Override
            public void run()
            {
                rmService.createRecordFolder(rmContainer, GUID.generate(), TYPE_FOLDER);                                
            }
        });
        
        // Failure: Create record folder in root
        doTestInTransaction(new FailureTest()
        {
            @Override
            public void run()
            {
                rmService.createRecordFolder(rmRootContainer, GUID.generate());                                
            }
        });
    }
    
    /********** Record methods **********/
    
    /**
     * @see RecordsManagementService#getRecordMetaDataAspects()
     */
    public void testGetRecordMetaDataAspects()
    {
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                Set<QName> aspects = rmService.getRecordMetaDataAspects();
                assertNotNull(aspects);
                assertEquals(5, aspects.size());
                assertTrue(aspects.containsAll(
                        CollectionUtils.arrayToList(new QName[]
                        {
                            DOD5015Model.ASPECT_DIGITAL_PHOTOGRAPH_RECORD, 
                            DOD5015Model.ASPECT_PDF_RECORD,
                            DOD5015Model.ASPECT_WEB_RECORD,
                            DOD5015Model.ASPECT_SCANNED_RECORD,
                            ASPECT_RECORD_META_DATA
                        })));
                
                return null;
            }
        });   
    }
    
    // TODO void testGetRecordFolders(NodeRef record); 
    
    // TODO void testIsRecordDeclared(NodeRef nodeRef);
    
    /********** RM2 - Multi-hierarchy record taxonomy's **********/
    
    /**
     * Test to create a simple multi-hierarchy record taxonomy  
     */
    public void testCreateSimpleHierarchy()
    {
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                // Create 3 level hierarchy
                NodeRef levelOne = setNodeRef("container1", rmService.createRecordsManagementContainer(rmRootContainer, "container1"));    
                assertNotNull("Unable to create container", levelOne);
                NodeRef levelTwo = setNodeRef("container2", rmService.createRecordsManagementContainer(levelOne, "container2"));
                assertNotNull("Unable to create container", levelTwo);
                NodeRef levelThree = setNodeRef("container3", rmService.createRecordsManagementContainer(levelTwo, "container3"));
                assertNotNull("Unable to create container", levelThree);
                NodeRef levelThreeRecordFolder = setNodeRef("recordFolder3", rmService.createRecordFolder(levelThree, "recordFolder3"));
                assertNotNull("Unable to create record folder", levelThreeRecordFolder);
                
                return null;
            }

            @Override
            public void test(Void result)
            {
                // Test that the hierarchy has been created correctly
                basicRMContainerCheck(getNodeRef("container1"), "container1", TYPE_RECORDS_MANAGEMENT_CONTAINER);
                basicRMContainerCheck(getNodeRef("container2"), "container2", TYPE_RECORDS_MANAGEMENT_CONTAINER);
                basicRMContainerCheck(getNodeRef("container3"), "container3", TYPE_RECORDS_MANAGEMENT_CONTAINER);
                basicRMContainerCheck(getNodeRef("recordFolder3"), "recordFolder3", TYPE_RECORD_FOLDER);
                
                // TODO need to check that the parents and children can be retrieved correctly
            }
        });                
    }
    
    /**
     * A basic test of a records management container
     * 
     * @param nodeRef   node reference
     * @param name      name of the container
     * @param type      the type of container 
     */
    private void basicRMContainerCheck(NodeRef nodeRef, String name, QName type)
    {
        // Check the basic details
        assertEquals(name, nodeService.getProperty(nodeRef, PROP_NAME));
        assertNotNull("RM id has not been set", nodeService.getProperty(nodeRef, PROP_IDENTIFIER));
        assertEquals(type, nodeService.getType(nodeRef));        
    }
    
}
