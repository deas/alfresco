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
package org.alfresco.module.org_alfresco_module_dod5015;

import java.util.List;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;

/**
 * Records management service interface.
 * 
 * Allows simple creation, manipulation and querying of records management components.
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementService
{
    /********** RM Component methods **********/
    
    /**
     * Indicates whether the given node is a records management component or not.
     * 
     * @param  nodeRef   node reference
     * @return boolean   true if a records management component, false otherwise
     */
    boolean isRecordsManagmentComponent(NodeRef nodeRef);
    
    /**
     * Indicates whether the given node is a records management root or not.
     * 
     * @param nodeRef   node reference
     * @return boolean  true if node is a records management root
     */
    boolean isRecordsManagementRoot(NodeRef nodeRef);
    
    /**
     * Indicates whether the given node is a record management container of not.
     * 
     * @param nodeRef   node reference
     * @return boolean  true if records management container
     */
    boolean isRecordsManagementContainer(NodeRef nodeRef);
    
    /**
     * Indicates whether the given node is a record folder or not.
     * 
     * @param nodeRef   node reference
     * @return boolean  true if record folder, false otherwise
     */
    boolean isRecordFolder(NodeRef nodeRef);
    
    /**
     * Indicates whether the given node is a record or not.
     * 
     * @param nodeRef   node reference
     * @return boolean  true if record, false otherwise
     */
    boolean isRecord(NodeRef nodeRef);
    
    /**
     * Gets the <b>NodeRef</b> sequence from the {@link #getRecordsManagementRoot(NodeRef) root}
     * down to the fileplan component given.  The array will start with the <b>NodeRef</b> of the root
     * and end with the name of the fileplan component node given.
     * 
     * @param nodeRef           a fileplan component
     * @return                  Returns a <b>NodeRef</b> path starting with the name of the
     *                          records management root
     */
    List<NodeRef> getNodeRefPath(NodeRef nodeRef);
    
    /**
     * Gets the records management root node for the file plan component specified
     * 
     * @return  NodeRef records management root
     */
    NodeRef getRecordsManagementRoot(NodeRef nodeRef);
    
    /********** Record Management Root methods **********/
    
    /**
     * Gets all the records management root nodes.  Searches the SpacesStore by
     * default. 
     * 
     * @return  List<NodeRef>    list of record management root nodes
     */
    List<NodeRef> getRecordsManagementRoots();
    
    /**
     * Specify the store which should be searched.
     * 
     * @see RecordsManagementService#getRecordManagementRoots()
     * 
     * @param  storeRef         store reference
     * @return List<NodeRef>    list of record management root nodes
     */
    @Deprecated
    List<NodeRef> getRecordsManagementRoots(StoreRef storeRef);
    
    // TODO NodeRef getRecordsManagementRootById(String id);
    
    /**
     * Creates a records management root as a child of the given parent node, with the name
     * provided.
     * 
     * @param   parent  parent node reference
     * @param   name    name of the root
     * @param   type    type of root created (must be sub-type of rm:recordsManagementRootContainer)
     * @return  NodeRef node reference to the newly create RM root
     */
    NodeRef createRecordsManagementRoot(NodeRef parent, String name, QName type);
    
    /**
     * Defaults to the a RM root of type rm:recordsManagementRootContainer
     * 
     * @see RecordsManagementService#createRecordsManagementRoot(NodeRef, String, QName)
     */
    NodeRef createRecordsManagementRoot(NodeRef parent, String name);
    
    // TODO void deleteRecordsManagementRoot(NodeRef root);
    
    /********** Records Management Container methods **********/
    
    // TODO NodeRef getRecordsManagementContainerByPath(String path);
    
    // TODO NodeRef getRecordsManagementContainerById(String id);
    
    // TODO NodeRef getRecordsManagementContainerByName(NodeRef parent, String id); ??
    
    /**
     * Get all the items contained within a container.  This will include record folders and other record containers.
     * 
     * @param container container node reference
     * @param deep if true then return all children including sub-containers and their children in turn, if false then just
     *             return the immediate children
     * @return {@link List}<{@link NodeRef>} list of contained node references
     */
    List<NodeRef> getAllContained(NodeRef container, boolean deep);
    
    /**
     * Only return the immediate children.
     * 
     * @see RecordsManagementService#getAllContained(NodeRef, boolean)
     * 
     * @param container container node reference
     * @return {@link List}<{@link NodeRef>} list of contained node references
     */
    List<NodeRef> getAllContained(NodeRef container);    
    
    /**
     * Get all the containers contained within a container.
     * 
     * @param container container node reference
     * @param deep if true then return all children including sub-containers and their children in turn, if false then just
     *             return the immediate children
     * @return {@link List}<{@link NodeRef>} list of container node references
     */
    List<NodeRef> getContainedRecordsManagementContainers(NodeRef container, boolean deep);
    
    /**
     * Only return immediate children.
     * 
     * @see RecordsManagementService#getContainedRecordsManagementContainers(NodeRef, boolean)
     * 
     * @param container container node reference
     * @return {@link List}<{@link NodeRef>} list of container node references
     */
    List<NodeRef> getContainedRecordsManagementContainers(NodeRef container);
    
    /**
     * Get all the record folders contained within a container
     * 
     * @param container container node reference
     * @param deep if true then return all children including sub-containers and their children in turn, if false then just
     *             return the immediate children
     * @return {@link List}<{@link NodeRef>} list of record folder node references
     */
    List<NodeRef> getContainedRecordFolders(NodeRef container, boolean deep);
    
    /**
     * Only return immediate children.
     * 
     * @see RecordsManagementService#getContainedRecordFolders(NodeRef, boolean)
     * 
     * @param container container node reference
     * @return {@link List}<{@link NodeRef>} list of record folder node references
     */
    List<NodeRef> getContainedRecordFolders(NodeRef container);
    
    // TODO List<NodeRef> getParentRecordsManagementContainers(NodeRef container); // also applicable to record folders 
    
    /**
     * Create a records management container.
     * 
     * @param  parent    parent node reference, must be a records management container.
     * @param  name      name of the new container
     * @param  type      type of container to create, must be a sub-type of rm:recordsManagementContainer
     * @return NodeRef   node reference of the created rm container
     */
    NodeRef createRecordsManagementContainer(NodeRef parent, String name, QName type);
    
    /**
     * Creates a container of type rm:recordsManagementContainer.
     * 
     * @see RecordsManagementService#createRecordsManagementContainer(NodeRef, String, QName)
     * 
     * @param  parent    parent node reference, must be a records management container.
     * @param  name      name of the new container
     * @return NodeRef   node reference of the created rm container
     */
    NodeRef createRecordsManagementContainer(NodeRef parent, String name);
    
    // TODO void deleteRecordsManagementContainer(NodeRef container);
    
    // TODO move, copy, link ??
    
    /********** Record Folder methods **********/    
    
    /**
     * Indicates whether the contents of a record folder are all declared.
     * 
     * @param nodeRef   node reference (record folder)
     * @return boolean  true if record folder contents are declared, false otherwise
     */
    boolean isRecordFolderDeclared(NodeRef nodeRef);
    
    // TODO NodeRef getRecordFolderByPath(String path);
    
    // TODO NodeRef getRecordFolderById(String id);
    
    // TODO NodeRef getRecordFolderByName(NodeRef parent, String name);
    
    
    /**
     * Create a record folder in the rm container.  The record folder with take the name and type 
     * provided.
     * 
     * @param  rmContainer   records management container
     * @param  name          name
     * @param  type          type
     * @return NodeRef       node reference of record folder
     */
    NodeRef createRecordFolder(NodeRef rmContainer, String name, QName type);
    
    /**
     * Type defaults to rm:recordFolder
     * 
     * @see RecordsManagementService#createRecordsManagementContainer(NodeRef, String, QName)
     * 
     * @param  rmContainer   records management container
     * @param  name          name
     * @return NodeRef       node reference of record folder
     */
    NodeRef createRecordFolder(NodeRef parent, String name);
    
    // TODO void deleteRecordFolder(NodeRef recordFolder);
    
    // TODO List<NodeRef> getParentRecordsManagementContainers(NodeRef container); // also applicable to record folders
    
    /**
     * Gets a list of all the records within a record folder
     * 
     * @param recordFolder      record folder
     * @return List<NodeRef>    list of records in the record folder
     */
    // TODO rename to getContainedRecords(NodeRef recordFolder);
    List<NodeRef> getRecords(NodeRef recordFolder);
    
    // TODO move? copy? link?
    
    /********** Record methods **********/
    
    /**
     * Get a list of all the record meta-data aspects
     * 
     * @return {@link Set}<{@link QName}>	list of record meta-data aspects
     */
    Set<QName> getRecordMetaDataAspects();
    
    /**
     * Get all the record folders that a record is filed into.
     * 
     * @param record            the record node reference
     * @return List<NodeRef>    list of folder record node references
     */
    // TODO rename to List<NodeRef> getParentRecordFolders(NodeRef record);
    List<NodeRef> getRecordFolders(NodeRef record); 
        
    /**
     * Indicates whether the record is declared
     * 
     * @param nodeRef   node reference (record)
     * @return boolean  true if record is declared, false otherwise
     */
    boolean isRecordDeclared(NodeRef nodeRef);
    
    /********** Vital Record methods **********/
    // TODO move to vital record (or review?) service
    
    /**
     * Get the vital record definition for a given node reference within the file plan
     * 
     * @param nodeRef               node reference to a container, record folder or record
     * @return VitalRecordDetails   vital record details, null if none
     */
    VitalRecordDefinition getVitalRecordDefinition(NodeRef nodeRef);     
    
}
