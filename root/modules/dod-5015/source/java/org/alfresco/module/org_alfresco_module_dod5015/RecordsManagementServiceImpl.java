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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.model.RecordsManagementCustomModel;
import org.alfresco.module.org_alfresco_module_dod5015.model.RecordsManagementModel;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Period;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.ParameterCheck;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Records management service implementation.
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementServiceImpl implements RecordsManagementService,
                                                     RecordsManagementModel,
                                                     ApplicationContextAware,
                                                     RecordsManagementPolicies.OnCreateReference,
                                                     RecordsManagementPolicies.OnRemoveReference
{
    /** Store that the RM roots are contained within */
    private StoreRef defaultStoreRef = StoreRef.STORE_REF_WORKSPACE_SPACESSTORE;

    /** Service registry */
    private RecordsManagementServiceRegistry serviceRegistry;
    
    /** Dictionary service */
    private DictionaryService dictionaryService;
    
    /** Node service */
    private NodeService nodeService;

    /** Policy component */
    private PolicyComponent policyComponent;
    
    /** Records management action service */
    private RecordsManagementActionService rmActionService;

    /** Well-known location of the scripts folder. */
    private NodeRef scriptsFolderNodeRef = new NodeRef("workspace", "SpacesStore", "rm_scripts");
    
    /** List of available record meta-data aspects */
    private Set<QName> recordMetaDataAspects;
    
    /** Application context */
    private ApplicationContext applicationContext;
    
    /** Java behaviour */
    private JavaBehaviour onChangeToDispositionActionDefinition;
    
    /**
     * Set the service registry service
     * 
     * @param serviceRegistry   service registry
     */
    public void setRecordsManagementServiceRegistry(RecordsManagementServiceRegistry serviceRegistry)
    {
        // Internal ops use the unprotected services from the voter (e.g. nodeService)
        this.serviceRegistry = serviceRegistry;
        this.dictionaryService = serviceRegistry.getDictionaryService();
    }
    
    /**
     * Set policy component
     * 
     * @param policyComponent   policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    /**
     * Set search service
     * 
     * @param nodeService   search service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set records management action service
     * 
     * @param rmActionService   records management action service
     */
    public void setRmActionService(RecordsManagementActionService rmActionService)
    {
        this.rmActionService = rmActionService;
    }
    
    /**
     * Sets the default RM store reference
     * @param defaultStoreRef    store reference
     */
    public void setDefaultStoreRef(StoreRef defaultStoreRef) 
    {
        this.defaultStoreRef = defaultStoreRef;
    }

    /**
     * Init method.  Registered behaviours.
     */
    public void init()
    {        
        // Register the association behaviours
        this.policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"), 
                TYPE_RECORD_FOLDER, 
                ContentModel.ASSOC_CONTAINS,
                new JavaBehaviour(this, "onFileContent", NotificationFrequency.TRANSACTION_COMMIT));
        
       this.policyComponent.bindAssociationBehaviour(
                  QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"), 
                  TYPE_RECORDS_MANAGEMENT_CONTAINER, 
                  ContentModel.ASSOC_CONTAINS, 
                  new JavaBehaviour(this, "onAddContentToContainer", NotificationFrequency.EVERY_EVENT));
        
        // Register class behaviours.
        this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                ASPECT_VITAL_RECORD_DEFINITION,
                new JavaBehaviour(this, "onChangeToVRDefinition", NotificationFrequency.TRANSACTION_COMMIT));
        
        // Register script execution behaviour on RM property update.
        this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                ASPECT_FILE_PLAN_COMPONENT,
                new JavaBehaviour(this, "onChangeToAnyRmProperty", NotificationFrequency.TRANSACTION_COMMIT));
        
        // Disposition behaviours
        onChangeToDispositionActionDefinition = new JavaBehaviour(this, "onChangeToDispositionActionDefinition", NotificationFrequency.TRANSACTION_COMMIT); 
        this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                TYPE_DISPOSITION_ACTION_DEFINITION,
                onChangeToDispositionActionDefinition);

        // Reference behaviours
        policyComponent.bindClassBehaviour(RecordsManagementPolicies.ON_CREATE_REFERENCE, 
                                           ASPECT_RECORD, 
                                           new JavaBehaviour(this, "onCreateReference", NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindClassBehaviour(RecordsManagementPolicies.ON_REMOVE_REFERENCE, 
                                           ASPECT_RECORD, 
                                           new JavaBehaviour(this, "onRemoveReference", NotificationFrequency.TRANSACTION_COMMIT));
        
        // Identifier behaviours
        policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                                           ASPECT_RECORD_COMPONENT_ID,
                                           new JavaBehaviour(this, "onIdentifierUpdate", NotificationFrequency.TRANSACTION_COMMIT));    
    }
    
    /**
     * Try to file any record created in a record folder
     * 
     * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy#onCreateChildAssociation(org.alfresco.service.cmr.repository.ChildAssociationRef, boolean)
     */
    public void onFileContent(ChildAssociationRef childAssocRef, boolean bNew)
    {
        // File the document
        rmActionService.executeRecordsManagementAction(childAssocRef.getChildRef(), "file");
    }
    
    /**
     * On add content to container
     * 
     * Prevents content nodes being added to record series and record category folders
     * by imap, cifs etc.
     * 
     * @param childAssocRef
     * @param bNew
     */
    public void onAddContentToContainer(ChildAssociationRef childAssocRef, boolean bNew)
    {
        if (childAssocRef.getTypeQName().equals(ContentModel.ASSOC_CONTAINS))
        {
            QName childType = nodeService.getType(childAssocRef.getChildRef());
            
            if(childType.equals(ContentModel.TYPE_CONTENT))
            {
                throw new AlfrescoRuntimeException("Can not add content nodes to a records management category or series, please add content to record folder.");   
            }
        }
    }
    
    /**
     * Called after a vitalRecordDefinition property has been updated.
     */
    public void onChangeToVRDefinition(NodeRef node, Map<QName, Serializable> oldProps, Map<QName, Serializable> newProps)
    {
        if (nodeService.exists(node) == true)
        {
            rmActionService.executeRecordsManagementAction(node, "broadcastVitalRecordDefinition");
        }
    }
    
    /**
     * Called after a DispositionActionDefinition property has been updated.
     */
    public void onChangeToDispositionActionDefinition(NodeRef node, Map<QName, Serializable> oldProps, Map<QName, Serializable> newProps)
    {
        if (nodeService.exists(node) == true)
        {
            onChangeToDispositionActionDefinition.disable();
            try
            {
                // Determine the properties that have changed
                Set<QName> changedProps = this.determineChangedProps(oldProps, newProps);
                
                if (nodeService.hasAspect(node, ASPECT_UNPUBLISHED_UPDATE) == false)
                {                
                    // Apply the unpublished aspect                
                    Map<QName, Serializable> props = new HashMap<QName, Serializable>();
                    props.put(PROP_UPDATE_TO, UPDATE_TO_DISPOSITION_ACTION_DEFINITION);
                    props.put(PROP_UPDATED_PROPERTIES, (Serializable)changedProps);
                    nodeService.addAspect(node, ASPECT_UNPUBLISHED_UPDATE, props);
                }
                else
                {                
                    Map<QName, Serializable> props = nodeService.getProperties(node);
                    
                    // Check that there isn't a update currently being published
                    if ((Boolean)props.get(PROP_PUBLISH_IN_PROGRESS).equals(Boolean.TRUE) == true)
                    {
                        // Can not update the disposition schedule since there is an outstanding update being published
                        throw new AlfrescoRuntimeException(
                                "You can not update the disposition action defintion as a previous update is currently being published.");
                    }
                    
                    // Update the update information                
                    props.put(PROP_UPDATE_TO, UPDATE_TO_DISPOSITION_ACTION_DEFINITION);
                    props.put(PROP_UPDATED_PROPERTIES, (Serializable)changedProps);
                    nodeService.setProperties(node, props);
                }
            }
            finally
            {
                onChangeToDispositionActionDefinition.enable();
            }
        }
    }
    
    /**
     * Called after any Records Management property has been updated.
     */
    public void onChangeToAnyRmProperty(NodeRef node, Map<QName, Serializable> oldProps, Map<QName, Serializable> newProps)
    {
        if (nodeService.exists(node) == true)
        {
            this.lookupAndExecuteScripts(node, oldProps, newProps);
        }
    }
    
    /**
     * Property update behaviour implementation
     * 
     * @param node
     * @param oldProps
     * @param newProps
     */
    public void onIdentifierUpdate(NodeRef node, Map<QName, Serializable> oldProps, Map<QName, Serializable> newProps)
    {
       if (nodeService.exists(node) == true)
       {
           String newIdValue = (String)newProps.get(PROP_IDENTIFIER);
           if (newIdValue != null)
           {
               String oldIdValue = (String)oldProps.get(PROP_IDENTIFIER);
               if (oldIdValue != null && oldIdValue.equals(newIdValue) == false)
               {
                   throw new AlfrescoRuntimeException("The identifier property value of the object " + 
                                                       node.toString() + 
                                                       " can not be set, it is read only.");
               }
           }
       }
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.OnCreateReference#onCreateReference(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
    public void onCreateReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference)
    {
        // Deal with versioned records
        if (reference.equals(QName.createQName(RecordsManagementCustomModel.RM_CUSTOM_URI, "versions")) == true)
        {
            // Apply the versioned aspect to the from node
            this.nodeService.addAspect(fromNodeRef, ASPECT_VERSIONED_RECORD, null);
        }
        
        // Execute script if for the reference event
        executeReferenceScript("onCreate", reference, fromNodeRef, toNodeRef);
    }
    
    /**
     * Executes a reference script if present
     * 
     * @param policy
     * @param reference
     * @param from
     * @param to
     */
    private void executeReferenceScript(String policy, QName reference, NodeRef from, NodeRef to)
    {
        String referenceId = reference.getLocalName();
    
        // This is the filename pattern which is assumed.
        // e.g. a script file onCreate_superceded.js for the creation of a superseded reference
        String expectedScriptName = policy + "_" + referenceId + ".js";
         
        NodeRef scriptNodeRef = nodeService.getChildByName(scriptsFolderNodeRef, ContentModel.ASSOC_CONTAINS, expectedScriptName);
        if (scriptNodeRef != null)
        {
            Map<String, Object> objectModel = new HashMap<String, Object>(1);
            objectModel.put("node", from);
            objectModel.put("toNode", to);
            objectModel.put("policy", policy);
            objectModel.put("reference", referenceId);

            serviceRegistry.getScriptService().executeScript(scriptNodeRef, null, objectModel);
        }
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.OnRemoveReference#onRemoveReference(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
    public void onRemoveReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference)
    {
        // Deal with versioned records
        if (reference.equals(QName.createQName(RecordsManagementCustomModel.RM_CUSTOM_URI, "versions")) == true)
        {
            // Apply the versioned aspect to the from node
            this.nodeService.removeAspect(fromNodeRef, ASPECT_VERSIONED_RECORD);
        }
        
        // Execute script if for the reference event
        executeReferenceScript("onRemove", reference, fromNodeRef, toNodeRef);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isRecordsManagementRoot(org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean isRecordsManagementRoot(NodeRef nodeRef)
    {
        boolean result = false;
        if (nodeService.exists(nodeRef) == true &&
            nodeService.hasAspect(nodeRef, ASPECT_RECORDS_MANAGEMENT_ROOT) == true)            
        {
            result = true;
        }
        return result;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isRecordsManagmentComponent(org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean isRecordsManagmentComponent(NodeRef nodeRef)
    {
        boolean result = false;
        if (nodeService.exists(nodeRef) == true &&
            nodeService.hasAspect(nodeRef, ASPECT_FILE_PLAN_COMPONENT) == true)
        {
            result = true;
        }
        return result;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isRecordsManagementContainer(org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean isRecordsManagementContainer(NodeRef nodeRef)
    {
        QName nodeType = this.nodeService.getType(nodeRef);
        return this.dictionaryService.isSubClass(nodeType, TYPE_RECORDS_MANAGEMENT_CONTAINER);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isRecordFolder(org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean isRecordFolder(NodeRef nodeRef)
    {
        QName nodeType = this.nodeService.getType(nodeRef);
        return this.dictionaryService.isSubClass(nodeType, TYPE_RECORD_FOLDER);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isRecord(org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean isRecord(NodeRef nodeRef)
    {
        return this.nodeService.hasAspect(nodeRef, ASPECT_RECORD);
    }
    
    /**
     * {@inheritDoc}
     */
    public NodeRef getRecordsManagementRoot(NodeRef nodeRef)
    {
       NodeRef result = null;
               
       if (nodeRef != null)
       {
            result = (NodeRef)nodeService.getProperty(nodeRef, PROP_ROOT_NODEREF);
            if (result == null)
            {
                if (nodeService.hasAspect(nodeRef, ASPECT_RECORDS_MANAGEMENT_ROOT) == true)
                {
                    result = nodeRef;
                }
                else
                {
                    ChildAssociationRef parentAssocRef = nodeService.getPrimaryParent(nodeRef);
                    if (parentAssocRef != null)
                    {
                        result = getRecordsManagementRoot(parentAssocRef.getParentRef());
                    }
                }
            }
       }      
        
       return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<NodeRef> getNodeRefPath(NodeRef nodeRef)
    {
        LinkedList<NodeRef> nodeRefPath = new LinkedList<NodeRef>();
        try
        {
            getNodeRefPathRecursive(nodeRef, nodeRefPath);
        }
        catch (Throwable e)
        {
            throw new AlfrescoRuntimeException(
                    "Unable to get NodeRef path for node: \n" +
                    "   Node: " + nodeRef,
                    e);
        }
        return nodeRefPath;
    }
    
    /**
     * Helper method to build a <b>NodeRef</b> path from the node to the RM root
     */
    private void getNodeRefPathRecursive(NodeRef nodeRef, LinkedList<NodeRef> nodeRefPath)
    {
        if (!nodeService.hasAspect(nodeRef, ASPECT_FILE_PLAN_COMPONENT))
        {
            throw new AlfrescoRuntimeException("RM nodes must have aspect " + ASPECT_FILE_PLAN_COMPONENT);
        }
        // Prepend it to the path
        nodeRefPath.addFirst(nodeRef);
        // Are we at the root
        if (nodeService.hasAspect(nodeRef, ASPECT_RECORDS_MANAGEMENT_ROOT))
        {
            // We're done
        }
        else
        {
            ChildAssociationRef assocRef = nodeService.getPrimaryParent(nodeRef);
            if (assocRef == null)
            {
                // We hit the top of the store
                throw new AlfrescoRuntimeException("Didn't find a RM root");
            }
            // Recurse
            nodeRef = assocRef.getParentRef();
            getNodeRefPathRecursive(nodeRef, nodeRefPath);
        }
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getRecordsManagementRoots(org.alfresco.service.cmr.repository.StoreRef)
     */
    public List<NodeRef> getRecordsManagementRoots(StoreRef storeRef)
    {
        List<NodeRef> result = null;
        SearchService searchService = (SearchService)applicationContext.getBean("searchService");        
        String query = "ASPECT:\"" + ASPECT_RECORDS_MANAGEMENT_ROOT + "\"";        
        ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, query);
        try
        {
            result = resultSet.getNodeRefs();
        }
        finally
        {
            resultSet.close();
        }
        return result;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getRecordsManagementRoots()
     */
    public List<NodeRef> getRecordsManagementRoots()
    {
        return getRecordsManagementRoots(defaultStoreRef);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#createRecordsManagementRoot(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, org.alfresco.service.namespace.QName)
     */
    public NodeRef createRecordsManagementRoot(NodeRef parent, String name, QName type)
    {
        ParameterCheck.mandatory("parent", parent);
        ParameterCheck.mandatory("name", name);
        ParameterCheck.mandatory("type", type);
        
        // Check the parent is not already an RM component node
        // ie: you can't create a rm root in an existing rm hierarchy
        if (isRecordsManagmentComponent(parent) == true)
        {
            throw new AlfrescoRuntimeException("You can not create a records management root in an existing records management hierarchy.");
        }
                
        // Check that the passed type is a sub-type of the RMRootContainer type
        if (TYPE_RECORDS_MANAGEMENT_ROOT_CONTAINER.equals(type) == false &&
            dictionaryService.isSubClass(type, TYPE_RECORDS_MANAGEMENT_ROOT_CONTAINER) == false)
        {
            throw new AlfrescoRuntimeException("The records management root type(" + 
                                               type.toString() + 
                                               ") is not a sub-type of rm:recordsManagementRootContainer");
        }
        
        // Build map of properties
        Map<QName, Serializable> rmRootProps = new HashMap<QName, Serializable>(1);
        rmRootProps.put(ContentModel.PROP_NAME, name);
        
        // Create the root
        ChildAssociationRef assocRef = nodeService.createNode(
                parent,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                type,
                rmRootProps);
        
        // TODO do we need to create role and security groups or is this done automatically?
        
        return assocRef.getChildRef();
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#createRecordsManagementRoot(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
     */
    public NodeRef createRecordsManagementRoot(NodeRef parent, String name)
    {
        return createRecordsManagementRoot(parent, name, TYPE_RECORDS_MANAGEMENT_ROOT_CONTAINER);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#createRecordsManagementContainer(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, org.alfresco.service.namespace.QName)
     */
    public NodeRef createRecordsManagementContainer(NodeRef parent, String name, QName type)
    {
        ParameterCheck.mandatory("parent", parent);
        ParameterCheck.mandatory("name", name);
        ParameterCheck.mandatory("type", type);
        
        // Check that the parent is a container
        QName parentType = nodeService.getType(parent);
        if (TYPE_RECORDS_MANAGEMENT_CONTAINER.equals(parentType) == false &&
            dictionaryService.isSubClass(parentType, TYPE_RECORDS_MANAGEMENT_CONTAINER) == false)
        {
            throw new AlfrescoRuntimeException("Can not create records management container, because parent was not subtype of" +
                                               "rm:recordsManagement (parentType=" + parentType.toString() );
        }
        
        // Check that the the provided type is a sub-type of rm:recordsManagementContainer
        if (TYPE_RECORDS_MANAGEMENT_CONTAINER.equals(type) == false &&
            dictionaryService.isSubClass(type, TYPE_RECORDS_MANAGEMENT_CONTAINER) == false)
        {
            throw new AlfrescoRuntimeException("Can not create records management container, because provided type (" +
                                                type.toString() +
                                               ") is not a sub-type of rm:recordsManagementContainer");
        }
        
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_NAME, name);
        
        return nodeService.createNode(
                parent,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                type,
                props).getChildRef();
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#createRecordsManagementContainer(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
     */
    public NodeRef createRecordsManagementContainer(NodeRef parent, String name)
    {
        // TODO for now default to rm:recordsManagementContainer but in the future
        // the parent could give us context as to which type of container to create
        
        return createRecordsManagementContainer(parent, name, TYPE_RECORDS_MANAGEMENT_CONTAINER);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getAllContained(org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    public List<NodeRef> getAllContained(NodeRef container)
    {
        return getAllContained(container, false);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getAllContained(org.alfresco.service.cmr.repository.NodeRef, boolean)
     */
    @Override
    public List<NodeRef> getAllContained(NodeRef container, boolean deep)
    {
        return getContained(container, null, deep);
    }
    
    /**
     * Get contained nodes of a particular type.  If null return all.
     * 
     * @param container container node reference
     * @param typeFilter type filter, null if none
     * @return {@link List}<{@link NodeRef> list of contained node references
     */
    private List<NodeRef> getContained(NodeRef container, QName typeFilter, boolean deep)
    {   
        // Parameter check
        ParameterCheck.mandatory("container", container);
        
        // Check we have a container in our hands
        if (isRecordsManagementContainer(container) == false)
        {
            throw new AlfrescoRuntimeException("Node reference to a rm:recordsManagementContainer node expected.");
        }
        
        List<NodeRef> result = new ArrayList<NodeRef>(1);
        List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(container, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
        for (ChildAssociationRef assoc : assocs)
        {
            NodeRef child = assoc.getChildRef();
            QName childType = nodeService.getType(child);
            if (typeFilter == null ||
                typeFilter.equals(childType) == true ||
                dictionaryService.isSubClass(childType, typeFilter) == true)
            {
                result.add(child);
            }
            
            // Inspect the containers and add children if deep
            if (deep == true &&
                (TYPE_RECORDS_MANAGEMENT_CONTAINER.equals(childType) == true ||
                 dictionaryService.isSubClass(childType, TYPE_RECORDS_MANAGEMENT_CONTAINER) == true))
            {
                result.addAll(getContained(child, typeFilter, deep));
            }
        }
            
        return result;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getContainedRecordsManagementContainers(org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    public List<NodeRef> getContainedRecordsManagementContainers(NodeRef container)
    {
        return getContainedRecordsManagementContainers(container, false);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getContainedRecordsManagementContainers(org.alfresco.service.cmr.repository.NodeRef, boolean)
     */
    @Override
    public List<NodeRef> getContainedRecordsManagementContainers(NodeRef container, boolean deep)
    {
        return getContained(container, TYPE_RECORDS_MANAGEMENT_CONTAINER, deep);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getContainedRecordFolders(org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    public List<NodeRef> getContainedRecordFolders(NodeRef container)
    {
        return getContainedRecordFolders(container, false);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getContainedRecordFolders(org.alfresco.service.cmr.repository.NodeRef, boolean)
     */
    @Override
    public List<NodeRef> getContainedRecordFolders(NodeRef container, boolean deep)
    {
        return getContained(container, TYPE_RECORD_FOLDER, deep);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isRecordFolderDeclared(org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean isRecordFolderDeclared(NodeRef recordFolder)
    {
        // Check we have a record folder 
        if (isRecordFolder(recordFolder) == false)
        {
            throw new AlfrescoRuntimeException("Expecting a record folder.  Node is not a record folder. (" + recordFolder.toString() + ")");
        }
        
        boolean result = true;
        
        // Check that each record in the record folder in declared
        List<NodeRef> records = getRecords(recordFolder);
        for (NodeRef record : records)
        {
            if (isRecordDeclared(record) == false)
            {
                result = false;
                break;
            }
        }
        
        return result;
        
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getRecordFolders(org.alfresco.service.cmr.repository.NodeRef)
     */
    public List<NodeRef> getRecordFolders(NodeRef record)
    {
        List<NodeRef> result = new ArrayList<NodeRef>(1);
        if (isRecord(record) == true)
        {
            List<ChildAssociationRef> assocs = this.nodeService.getParentAssocs(record, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
            for (ChildAssociationRef assoc : assocs)
            {
                NodeRef parent = assoc.getParentRef();
                if (isRecordFolder(parent) == true)
                {
                    result.add(parent);
                }
            }
        }
        return result;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#createRecordFolder(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, org.alfresco.service.namespace.QName)
     */
    public NodeRef createRecordFolder(NodeRef rmContainer, String name, QName type)
    {
        ParameterCheck.mandatory("rmContainer", rmContainer);
        ParameterCheck.mandatory("name", name);
        ParameterCheck.mandatory("type", type);
        
        // Check that we are not trying to create a record folder in a root container
        if (isRecordsManagementRoot(rmContainer) == true)
        {
            throw new AlfrescoRuntimeException("Can not create a record folder, because the parent is a records management root");
        }
        
        // Check that the parent is a container
        QName parentType = nodeService.getType(rmContainer);
        if (TYPE_RECORDS_MANAGEMENT_CONTAINER.equals(parentType) == false &&
            dictionaryService.isSubClass(parentType, TYPE_RECORDS_MANAGEMENT_CONTAINER) == false)
        {
            throw new AlfrescoRuntimeException("Can not create record folder, because parent was not subtype of" +
                                               "rm:recordsManagementContainer (parentType=" + parentType.toString() );
        }
        
        // Check that the the provided type is a sub-type of rm:recordFolder
        if (TYPE_RECORD_FOLDER.equals(type) == false &&
            dictionaryService.isSubClass(type, TYPE_RECORD_FOLDER) == false)
        {
            throw new AlfrescoRuntimeException("Can not create record folder, because provided type (" +
                                                type.toString() +
                                               ") is not a sub-type of rm:recordFolder");
        }
        
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_NAME, name);
        
        return nodeService.createNode(
                rmContainer,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                type,
                props).getChildRef();
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#createRecordFolder(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
     */
    public NodeRef createRecordFolder(NodeRef rmContrainer, String name)
    {
        // TODO defaults to rm:recordFolder, but in future could auto-detect sub-type of folder based on
        //      context
        return createRecordFolder(rmContrainer, name, TYPE_RECORD_FOLDER);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getRecordMetaDataAspects()
     */
    public Set<QName> getRecordMetaDataAspects() 
    {
    	if (recordMetaDataAspects == null)
    	{
    	    recordMetaDataAspects = new HashSet<QName>(7);
    		Collection<QName> aspects = dictionaryService.getAllAspects();
    		for (QName aspect : aspects) 
    		{
    		    AspectDefinition def = dictionaryService.getAspect(aspect);
    		    if (def != null)
    		    {
    		        QName parent = def.getParentName();
    		        if (parent != null && ASPECT_RECORD_META_DATA.equals(parent) == true)
    		        {
    		            recordMetaDataAspects.add(aspect);
    		        }
    		    }
			}
    	}
    	return recordMetaDataAspects;
	}
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getRecords(org.alfresco.service.cmr.repository.NodeRef)
     */
    public List<NodeRef> getRecords(NodeRef recordFolder)
    {
        List<NodeRef> result = new ArrayList<NodeRef>(1);
        if (isRecordFolder(recordFolder) == true)
        {
            List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(recordFolder, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
            for (ChildAssociationRef assoc : assocs)
            {
                NodeRef child = assoc.getChildRef();
                if (isRecord(child) == true)
                {
                    result.add(child);
                }
            }
        }
        return result;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#isRecord(org.alfresco.service.cmr.repository.NodeRef, boolean)
     */
    public boolean isRecordDeclared(NodeRef record)
    {
        if (isRecord(record) == false)
        {
            throw new AlfrescoRuntimeException("Expecting a record.  Node is not a record. (" + record.toString() + ")");
        }
        return (this.nodeService.hasAspect(record, ASPECT_DECLARED_RECORD));
    } 

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#getVitalRecordDefinition(org.alfresco.service.cmr.repository.NodeRef)
     */
    public VitalRecordDefinition getVitalRecordDefinition(NodeRef nodeRef)
    {
        //TODO Problem refiling a 'none' into a 'daily'
        // File a 'day' into a 'year' is OK

        NodeRef vrdNodeRef = null;
        Boolean vri = null;
        Period reviewPeriod = null;
        if (isRecord(nodeRef) == true)
        {
            // Get the record folders for the record
            List<NodeRef> recordFolders = getRecordFolders(nodeRef);
            
            // Get all the vital record definitions
            for (NodeRef recordFolder : recordFolders)
            {
                NodeRef nextCandidateVrdNodeRef = getVitalRecordDefinitionImpl(recordFolder);
                
                // If the nextVrdNoderef is null, skip it.
                if (nextCandidateVrdNodeRef == null)
                {
                    continue;
                }
                
                Boolean nextCandidateVRI = (Boolean)nodeService.getProperty(nextCandidateVrdNodeRef, PROP_VITAL_RECORD_INDICATOR);
                Period nextCandidateReviewPeriod = (Period)nodeService.getProperty(nextCandidateVrdNodeRef, PROP_REVIEW_PERIOD);

                // If we have no potential vrdNodeRef, initially consider the first.
                if (vrdNodeRef == null)
                {
                    vrdNodeRef = nextCandidateVrdNodeRef;
                    vri = nextCandidateVRI;
                    reviewPeriod = nextCandidateReviewPeriod;
                }
                else
                {
                    // Now we need to select a VRD.
                    // Could refactor this out into a selectionStrategy like DispositionSchedule
                    
                    // Always choose the VRD with a 'true' vital record indicator over a 'false'/null one
                    if ( (vri == null || Boolean.FALSE.equals(vri) )
                            && Boolean.TRUE.equals(nextCandidateVRI))
                    {
                        vrdNodeRef = nextCandidateVrdNodeRef;
                        vri = nextCandidateVRI;
                        reviewPeriod = nextCandidateReviewPeriod;
                    }
                    else if (Boolean.TRUE.equals(nextCandidateVRI) && nextCandidateReviewPeriod != null)
                    {
                        // vri must be TRUE. reviewPeriod could be null.
                        // Take the one with the earliest review date.
                        if (reviewPeriod == null)
                        {
                            vrdNodeRef = nextCandidateVrdNodeRef;
                            vri = nextCandidateVRI;
                            reviewPeriod = nextCandidateReviewPeriod;
                        }
                        else
                        {
                            Date now = new Date();
                            Date nextReviewForCandidate = nextCandidateReviewPeriod.getNextDate(now);
                            Date nextReviewForCurrent = reviewPeriod.getNextDate(now);
                            
                            final int cmp = nextReviewForCurrent.compareTo(nextReviewForCandidate);
                            
                            // < 0 current is before candidate
                            if (cmp > 0)
                            {
                                vrdNodeRef = nextCandidateVrdNodeRef;
                                vri = nextCandidateVRI;
                                reviewPeriod = nextCandidateReviewPeriod;
                            }
                        }
                    }
                }
            }
        }
        else
        {
            // Get the vital record definition for the node reference provided
            vrdNodeRef = getVitalRecordDefinitionImpl(nodeRef);
        }
        
        VitalRecordDefinition result = null;
        if (vrdNodeRef != null)
        {
            result = new VitalRecordDefinitionImpl(serviceRegistry, vrdNodeRef);
        }
        return result;
    }
    
    /**
     * Get vital record definition implementation
     * 
     * @param nodeRef   node reference
     * @return NodeRef  vital record definition
     */
    private NodeRef getVitalRecordDefinitionImpl(NodeRef nodeRef)
    {
        NodeRef result = null;
        
        if (this.nodeService.hasAspect(nodeRef, ASPECT_VITAL_RECORD_DEFINITION) == true)
        {
            result = nodeRef;
        }
        else
        {
            NodeRef parent = this.nodeService.getPrimaryParent(nodeRef).getParentRef();
            if (isRecordsManagementContainer(parent) == true)
            {
                result = getVitalRecordDefinitionImpl(parent);
            }
        }
        return result;
    }      
    
    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    
    /**
     * This method examines the old and new property sets and for those properties which
     * have changed, looks for script resources corresponding to those properties.
     * Those scripts are then called via the ScriptService.
     * 
     * @param nodeWithChangedProperties the node whose properties have changed.
     * @param oldProps the old properties and their values.
     * @param newProps the new properties and their values.
     * 
     * @see #lookupScripts(Map<QName, Serializable>, Map<QName, Serializable>)
     */
    private void lookupAndExecuteScripts(NodeRef nodeWithChangedProperties,
            Map<QName, Serializable> oldProps, Map<QName, Serializable> newProps)
    {
        List<NodeRef> scriptRefs = lookupScripts(oldProps, newProps);
        
        Map<String, Object> objectModel = new HashMap<String, Object>(1);
        objectModel.put("node", nodeWithChangedProperties);
        objectModel.put("oldProperties", oldProps);
        objectModel.put("newProperties", newProps);

        for (NodeRef scriptRef : scriptRefs)
        {
            serviceRegistry.getScriptService().executeScript(scriptRef, null, objectModel);
        }
    }
    
    /**
     * This method determines which properties have changed and for each such property
     * looks for a script resource in a well-known location.
     * 
     * @param oldProps the old properties and their values.
     * @param newProps the new properties and their values.
     * @return A list of nodeRefs corresponding to the Script resources.
     * 
     * @see #determineChangedProps(Map<QName, Serializable>, Map<QName, Serializable>)
     */
    private List<NodeRef> lookupScripts(Map<QName, Serializable> oldProps, Map<QName, Serializable> newProps)
    {
        List<NodeRef> result = new ArrayList<NodeRef>();

        Set<QName> changedProps = determineChangedProps(oldProps, newProps);
        for (QName propQName : changedProps)
        {
            QName prefixedQName = propQName.getPrefixedQName(serviceRegistry.getNamespaceService());

            String [] splitQName = QName.splitPrefixedQName(prefixedQName.toPrefixString());
            final String shortPrefix = splitQName[0];
            final String localName = splitQName[1];

            // This is the filename pattern which is assumed.
            // e.g. a script file cm_name.js would be called for changed to cm:name
            String expectedScriptName = shortPrefix + "_" + localName + ".js";
            
            NodeRef nextElement = nodeService.getChildByName(scriptsFolderNodeRef, ContentModel.ASSOC_CONTAINS, expectedScriptName);
            if (nextElement != null) result.add(nextElement);
        }

        return result;
    }
    
    /**
     * This method compares the oldProps map against the newProps map and returns
     * a set of QNames of the properties that have changed. Changed here means one of
     * <ul>
     * <li>the property has been removed</li>
     * <li>the property has had its value changed</li>
     * <li>the property has been added</li>
     * </ul>
     */
    private Set<QName> determineChangedProps(Map<QName, Serializable> oldProps, Map<QName, Serializable> newProps)
    {
        Set<QName> result = new HashSet<QName>();
        for (QName qn : oldProps.keySet())
        {
            if (newProps.get(qn) == null ||
                newProps.get(qn).equals(oldProps.get(qn)) == false)
            {
                result.add(qn);
            }
        }
        for (QName qn : newProps.keySet())
        {
            if (oldProps.get(qn) == null)
            {
                result.add(qn);
            }
        }
        
        return result;
    }
}
