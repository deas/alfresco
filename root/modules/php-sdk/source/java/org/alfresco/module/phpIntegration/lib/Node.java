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
package org.alfresco.module.phpIntegration.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.phpIntegration.PHPProcessor;
import org.alfresco.module.phpIntegration.PHPProcessorException;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.TemplateService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.EqualsHelper;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caucho.quercus.annotation.Optional;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.Value;

/**
 * Repository node implementation class.
 * 
 * @author Roy Wetherall
 */
public class Node implements ScriptObject
{
    /** Logger **/
    private static Log    logger = LogFactory.getLog(Node.class);
    
    /** Script object name */
    private static final String SCRIPT_OBJECT_NAME = "Node";
    
    /** New node id delimiter */
    private static final String NEW_NODE_DELIM = "new_";
    
    /** Node service */
    protected NodeService nodeService;
    
    /** Version service */
    protected VersionService versionService;
    
    /** Template service */
    protected TemplateService templateService;
    
    /** Session object */
    protected Session session;
    
    /** Node id */
    private String id;
    
    /** Node type */
    private String type;
    
    /** Node store */
    private Store store;
    
    /** List of nodes aspects (removed and added)*/
    private List<String> aspects;
    private List<String> addedAspects;
    private List<String> removedAspects;
    
    /** Indicates if the properties have been modified */
    private boolean arePropertiesDirty = false;
    private Map<String, Object> properties;
    
    private List<ChildAssociation> children; 
    private List<ChildAssociation> addedChildren;
    private List<ChildAssociation> removedChildren;
    private List<ChildAssociation> parents;
    private ChildAssociation primaryParent;
    private List<Association> associations;
    private List<Association> addedAssociations;
    private List<Association> removedAssociations;
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    /**
     * Constructor 
     * 
     * @param session	the session
     * @param nodeRef	the node reference
     */
    public Node(Session session, NodeRef nodeRef)
    {
        // Call the constructor
        this(session, session.getStoreFromString(nodeRef.getStoreRef().toString()), nodeRef.getId());
    }
    
    /**
     * Constructor
     * 
     * @param session	the session
     * @param store		the store
     * @param id		the node id
     */
    public Node(Session session, Store store, String id)
    {
        // Call the constructor
        this(session, store, id, null);
    }
    
    /**
     * Constructor
     * 
     * @param session	the session
     * @param store		the store
     * @param id		the node id
     * @param type		the node type
     */
    public Node(Session session, Store store, String id, String type)
    {
        // Set the attribute details
        this.session = session;
        this.store = store;
        this.id = id;
        if (type != null)
        {
            this.type = type;
        }
        
        // Set the node service
        this.nodeService = session.getServiceRegistry().getNodeService();
        this.versionService = session.getServiceRegistry().getVersionService();
        this.templateService = session.getServiceRegistry().getTemplateService();
        
        // Add the node to the session
        this.session.addNode(this);
    }
    
    /**
     * Get the node reference that this node represents
     * 
     * @return  the node reference
     */
    public NodeRef getNodeRef()
    {
        NodeRef nodeRef = null;
        if (isNewNode() == false)
        {
            nodeRef = new NodeRef(this.store.getStoreRef(), this.id);
        }
        return nodeRef;
    }
    
    /**
     * Get the nodes session
     * 
     * @return  the session
     */
    public Session getSession()
    {
        return this.session;
    }
    
    /**
     * Get the nodes store
     * 
     * @return  the Store
     */
    public Store getStore()
    {
        return this.store;
    }
    
    /**
     * Gets the id of the node
     * 
     * @return  the id of the node
     */
    public String getId()
    {
        return this.id;
    }
    
    /** 
     * Gets the type of the node
     * 
     * @return  the node type
     */
    public String getType()
    {
    	return this.session.doSessionWork(new SessionWork<String>()
    	{
			public String doWork() 
			{
		        if (Node.this.type == null)
		        {
		        	Node.this.type = Node.this.nodeService.getType(getNodeRef()).toString();
		        }
		        return Node.this.type;
			}
    	});
    }
    
    /**
     * Indicates whether the node is newly created.  True if it is yet to be saved, false otherwise.
     * 
     * @return boolean True if it is a new node, false otherwise.
     */
    public boolean isNewNode()
    {
        return this.id.startsWith(NEW_NODE_DELIM);
    }
    
    /**
     * Get the map of property names and values
     * 
     * @return  a map of property names and values
     */
    public Map<String, Object> getProperties()
    {
        // Make sure the properties are populated
        populateProperties();
        
        // Return the properties
        return new HashMap<String, Object>(this.properties);
    }
    
    /**
     * Get the value of a property
     * 
     * @param propertyName  the property name
     * @return Object       the value of the property
     */
    public Object getProperty(String propertyName)
    {
        // Get the property value from the property map
        propertyName = this.session.getNamespaceMap().getFullName(propertyName);
        Map<String, Object> properties = getProperties();
        return properties.get(propertyName);
    }
    
    /**
     * Set the value of a property
     * 
     * @param propertyName      the property name
     * @param value             the value of the property
     */
    public void setProperty(String propertyName, Object value)
    {
        // Get the full name of the property
        propertyName = this.session.getNamespaceMap().getFullName(propertyName);
        
        // Make sure the properties are populated
        populateProperties();
        
        // Set the value of the property
        this.properties.put(propertyName, value);
        this.arePropertiesDirty = true;
    }
    
    /**
     * Sets the property values for the node
     * 
     * @param properties    a map of property names and values
     */
    public void setProperties(Map<String, Object> properties)
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Setting properties on node " + this.getId());
        }
        
        // Set the property values
        this.properties = new HashMap<String, Object>(properties);
        this.arePropertiesDirty = true;
    }        
    
    /**
     * Sets the values of the properties found in the array provided
     * 
     * @param properties a map of property values
     */
    public void setPropertyValues(Map<String, Object> properties)
    {
        // Make sure the properties are populated
        populateProperties();
        
        // Overwrite/set the properties with the passed property values
        for (Map.Entry<String, Object> entry : properties.entrySet())
        {
            String fullName = this.session.getNamespaceMap().getFullName(entry.getKey());
            this.properties.put(fullName, entry.getValue());
        }
        this.arePropertiesDirty = true;
    }
    
    /**
     * Call back used to indicate that a content property has been modified directly.
     */
    /*package*/ void contentUpdated()
    {
        this.arePropertiesDirty = true;
    }
    
    /**
     * Gets a list of the node's aspects
     * 
     * @return  List<String>    a list of the node aspects
     */
    public List<String> getAspects()
    {
        // Check that the aspects have been populated
        populateAspects();
        
        return this.aspects;
    }
    
    /**
     * Indicates whether an node has the specified aspect or not.
     * 
     * @param aspect    the aspect type (short names accepted)
     * @return boolean  true if the node has the aspect, false otherwise
     */
    public boolean hasAspect(String aspect)
    {
        // Check that the aspects have been populated
        populateAspects();
        
        // Map aspect name to full name
        aspect = this.session.getNamespaceMap().getFullName(aspect);
        
        // Check to see if the aspect is in the list
        return this.aspects.contains(aspect);
    }
    
    /**
     * Adds an aspect to the node
     * 
     * @param aspect        the aspect
     * @param properties    the properties of the aspect
     */
    public void addAspect(String aspect, Map<String, Object> properties)
    {
        // Check that the aspects have been populated
        populateAspects();
        
        // Map aspect name to full name
        aspect = this.session.getNamespaceMap().getFullName(aspect);
        
        // Add the aspect
        if (this.aspects.contains(aspect) == false)
        {
            // Deal with re-added aspects
            if (this.removedAspects.contains(aspect) == true)
            {
                this.removedAspects.remove(aspect);
            }
            else
            {
                this.addedAspects.add(aspect);
            }
            
            this.aspects.add(aspect);                     
        }
        
        // Add the properties
        if (properties != null)
        {
            setPropertyValues(properties);
        }
    }
    
    /**
     * Removes as aspect from the node.
     * 
     * @param aspect    the aspect
     */
    public void removeAspect(String aspect)
    {
        // Check  that the aspects have been populated
        populateAspects();
        
        // Map the aspect name to the correct full name
        aspect = this.session.getNamespaceMap().getFullName(aspect);
        
        // Remove the aspect
        if (this.aspects.contains(aspect) == true)
        {
            if (this.addedAspects.contains(aspect) == true)
            {
                this.addedAspects.remove(aspect);
            }
            else
            {
                this.removedAspects.add(aspect);
            }
            this.aspects.remove(aspect);
        }
    }
  
    /**
     * Get the child associations of this node
     * 
     * @return List<ChildAssociation>   a list of child associations
     */
    public List<ChildAssociation> getChildren()
    {
        // Check the children have been populated
        populateChildren();
        
        return this.children;
    }
    
    /** 
     * Get the parent associations of this node
     * 
     * @return List<ChildAssociation>   a list of parent assocations
     */
    public List<ChildAssociation> getParents()
    {
        // Check that the parents have been populated
        populateParents();
        
        return this.parents;
    }
    
    /**
     * Get the primary parent of this node
     * 
     * @return  the primary parent node
     */
    public Node getPrimaryParent()
    {
        // Check that the parents have been populated
        populateParents();
        
        // Return the primary parent of this node
        return this.primaryParent.getParent();
    }
    
    /**
     * Get the associations eminating from this node
     * 
     * @return List<Association>    a list of associations
     */
    public List<Association> getAssociations()
    {
        // Check that associations have been populated
        populateAssociations();
        
        return this.associations;
    }
    
    /**
     * Updates the content on a content property
     * 
     * @param property  the content property name
     * @param mimetype  the content mimetype
     * @param encoding  the content encoding
     * @param content   the content
     * @return ContentData the contetn data
     */
    public org.alfresco.module.phpIntegration.lib.ContentData updateContent(String property, String mimetype, String encoding, String content)
    {
        // Make sure the properties are populated
        populateProperties();
        
        // Convert to full name
        property = this.session.getNamespaceMap().getFullName(property);
        
        // Create the content data object
        org.alfresco.module.phpIntegration.lib.ContentData contentData = new org.alfresco.module.phpIntegration.lib.ContentData(this, property, mimetype, encoding);
        if (content != null)
        {
            contentData.setContent(content);
        }
        
        // Assign to property
        this.properties.put(property, contentData);
        
        return contentData;
    }
    
    /**
     * Create a new child node
     * 
     * @param type              the type of the node
     * @param associationType   the association type
     * @param associationName   the association name
     * @return Node             the newly create node     
     */
    public Node createChild(final String origType, final String origAssociationType, final String origAssociationName)
    {
        return this.session.doSessionWork(new SessionWork<Node>()
        {
            public Node doWork() 
            {
                // Convert to full names
                String type = Node.this.session.getNamespaceMap().getFullName(origType);
                String associationType = Node.this.session.getNamespaceMap().getFullName(origAssociationType);
                String associationName = Node.this.session.getNamespaceMap().getFullName(origAssociationName);
                
                // Check the children have been populates
                populateChildren();
                
                // Create the new node
                String id = NEW_NODE_DELIM + GUID.generate();
                
                // Use the node factory to create the node of the correct type
                Node newNode = Node.this.session.getNodeFactory().createNode(Node.this.session, getStore(), id, type);
                        
                // Create the child association object
                ChildAssociation childAssociation = new ChildAssociation(Node.this, newNode, associationType, associationName, true, 0);
                
                // Set the parent array of the node node        
                newNode.parents = new ArrayList<ChildAssociation>(5);
                newNode.primaryParent = childAssociation;
                newNode.parents.add(childAssociation);
                
                // Add as a child of the parent node
                Node.this.children.add(childAssociation);
                Node.this.addedChildren.add(childAssociation);
                
                return newNode;
            }
        });
    }
    
    /**
     * Add a new child to the node.  Creates a non-primary child association.
     * 
     * @param node              the child node
     * @param associationType   the association type
     * @param associationName   the association name
     */
    public void addChild(Node node, String associationType, String associationName)
    {
        // Convert to full names
        associationType = this.session.getNamespaceMap().getFullName(associationType);
        associationName = this.session.getNamespaceMap().getFullName(associationName);
        
        // Check that the children have been populated 
        populateChildren();
        
        // Check the parents of the child node have been populated
        node.populateParents();
        
        // Create the child association
        ChildAssociation childAssociation = new ChildAssociation(this, node, associationType, associationName, false, 0);
     
        // Add to the parent list of the child node
        node.parents.add(childAssociation);
        
        // Add to the child lists of the parent node
        this.children.add(childAssociation);
        if (this.removedChildren.contains(childAssociation) == true)
        {
            this.removedChildren.remove(childAssociation);
        }
        else
        {        
            this.addedChildren.add(childAssociation);
        }
    }
    
    /**
     * Removes a non-primary child association from the node.
     * 
     * @param childAssociation  the child association to remove.
     */
    public void removeChild(ChildAssociation childAssociation)
    {
        if (childAssociation.getIsPrimary() == false)
        {
            // Check that the children have been populated
            populateChildren();
            
            if (this.children.contains(childAssociation) == true)
            {
                // Check the parents of the child have been populated
                childAssociation.getChild().populateParents();
                
                // Adjust lists accordingly
                this.children.remove(childAssociation);                
                childAssociation.getChild().parents.remove(childAssociation);
                
                if (this.addedChildren.contains(childAssociation) == true)
                {
                    this.addedChildren.remove(childAssociation);
                }
                else
                {
                    this.removedChildren.add(childAssociation);
                }
            }
            else
            {
                if (logger.isDebugEnabled() == true)
                {
                    logger.debug("The child association being delete is not present of the node.");
                }
            }
        }
        else
        {
            throw new PHPProcessorException("Cannot remove a primary child association.");
        }                
    }
    
    /**
     * Adds an association from one node to another.
     * 
     * @param toNode            the destination node
     * @param associationType   the assocation type
     */
    public void addAssociation(Node toNode, String associationType)
    {
        // Convert to full name
        associationType = this.session.getNamespaceMap().getFullName(associationType);
        
        // Populate the associations for this node
        populateAssociations();
        
        // Create the association
        Association association = new Association(this, toNode, associationType);
        
        // Adjust lists accordingly
        if (removedAssociations.contains(association) == true)
        {
            this.removedAssociations.remove(association);
        }
        else
        {
            this.addedAssociations.add(association);
        }
        this.associations.add(association);
    }
    
    /**
     * Remove an association
     * 
     * @param association   the association
     */
    public void removeAssociation(Association association)
    {   
        // Populate the associations for this node
        populateAssociations();
        
        // Adjust lists accordingly
        if (addedAssociations.contains(association) == true)
        {
            this.addedAssociations.remove(association);
        }
        else
        {
            this.removedAssociations.add(association);
        }
        this.associations.remove(association);        
    }
    
    /**
     * Copies the node and optionally all its children, to another destination
     * 
     * @param destination       the destination node
     * @param associationType   the association type
     * @param associationName   the association name
     * @param copyChildren      indicates whether the children of the node should be copied or not
     * @return Node             the newly created copy of the origional node 
     */
    public Node copy(final Node destination, final String associationType, final String associationName, final boolean copyChildren)
    {
    	return this.session.doSessionWork(new SessionWork<Node>()
    	{
			public Node doWork() 
			{
		        // Get the full names of the association type and name
		        String associationTypeFull = Node.this.session.getNamespaceMap().getFullName(associationType);
		        String associationNameFull = Node.this.session.getNamespaceMap().getFullName(associationName);
		        
		        // Check that the destination node is not an unsaved node
		        if (destination.isDirty() == true)
		        {
		            throw new PHPProcessorException("Can not copy node (" + toString() + ") since there are outstanding modifications that require saving on the destination node (" + destination.toString() + ")");
		        }
		        
		        // Check whether there are any outstanding changes
		        if (isDirty() == true)
		        {
		            throw new PHPProcessorException("Can not copy node (" + toString() + ") since there are outstanding modifications that require saving");
		        }
		        
		        // Copy the node
		        CopyService copyService = Node.this.session.getServiceRegistry().getCopyService();
		        NodeRef nodeRef = copyService.copyAndRename(
		                getNodeRef(), 
		                destination.getNodeRef(),
		                QName.createQName(associationTypeFull),
		                QName.createQName(associationNameFull),
		                copyChildren);
		        
		        // To ensure information is up to date, clean the destination node
		        destination.cleanNode();
		        
		        // Return the newly created node
		        return Node.this.session.getNodeFromString(nodeRef.toString());
			}
    	});
    }
    
    /**
     * Moves the node from its current primary parent into another.
     * 
     * @param destination       the destination node
     * @param associationType   the assocation type
     * @param assocationName    the association name
     */
    public void move(final Node destination, final String associationType, final String associationName)    
    {
    	this.session.doSessionWork(new SessionWork<Object>()
    	{
			public Object doWork() 
			{
		        // Get the full names of the assoc type and name
		        String fullAssociationType = Node.this.session.getNamespaceMap().getFullName(associationType);
		        String fullAssociationName = Node.this.session.getNamespaceMap().getFullName(associationName);
		        
		        // Check the current primary parent for modifications
		        Node currentParent = getPrimaryParent();
		        if (currentParent.isDirty() == true)
		        {
		            throw new PHPProcessorException("Can not move node (" + toString() + ") since there are outstanding modifications that require saving on the current parent node (" + currentParent.toString() + ")");
		        }
		        
		        // Check that the destination node is not an unsaved node
		        if (destination.isDirty() == true)
		        {
		            throw new PHPProcessorException("Can not move node (" + toString() + ") since there are outstanding modifications that require saving on the destination node (" + destination.toString() + ")");
		        }
		        
		        // Check whether there are any outstanding changes
		        if (isDirty() == true)
		        {
		            throw new PHPProcessorException("Can not move node (" + toString() + ") since there are outstanding modifications that require saving");
		        }
		        
		        // Do the move
		        Node.this.nodeService.moveNode(
		                getNodeRef(),
		                destination.getNodeRef(),
		                QName.createQName(fullAssociationType),
		                QName.createQName(fullAssociationName));    
		        
		        // Clean all 3 nodes involved in the mode to ensure no data is out of date
		        currentParent.cleanNode();
		        destination.cleanNode();
		        cleanNode();
		        
		        return null;
			}
    	});
    }
    
    /**
     * Determines whether the current user has specified permissions on the node
     * 
     * @param permission    the permission string
     * @return boolean      true if the user has the permission, false otherwise
     */
    public boolean hasPermission(String permission)
    {    
        boolean allowed = false;
        
        if (permission != null && permission.length() != 0)
        {
            if (this.isNewNode() == true)
            {
                // Since this is a new node then this user must have created it
                allowed = true;
            }
            else
            {
                AccessStatus status = this.session.getServiceRegistry().getPermissionService().hasPermission(getNodeRef(), permission);
                allowed = (AccessStatus.ALLOWED == status);
            }
        }
    
        return allowed;
    }
    
    /**
     * Indicates whether this node is a sub type of the another type.
     * 
     * @param subTypeOf     is this node a sub type of this type.
     * @return boolean      true if it is, false otherwise
     */
    public boolean isSubTypeOf(String subTypeOf)
    {
        return this.session.getDataDictionary().isSubTypeOf(getType(), subTypeOf);
    }
    
    public Version createVersion(@Optional("") final String description, @Optional("false") final boolean major)
	{
    	// TODO .. figure out how we deep version?
    	
		// We can only create a version if there are no outstanding changes for this node
		if (this.isDirty() == true)
		{
			throw new PHPProcessorException("You must save any outstanding modifications before a new version can be created.");
		}
		
		return this.session.doSessionWork(new SessionWork<Version>()
    	{
			public Version doWork() 
			{
				// TODO implement major flag ... 
				//   - send version type correctly
				//   - set major flag on Version return value creation
				
				// Create the new version
				Map<String, Serializable> versionProperties = new HashMap<String, Serializable>(1);
				versionProperties.put(org.alfresco.service.cmr.version.Version.PROP_DESCRIPTION, description);
				org.alfresco.service.cmr.version.Version repoVersion = Node.this.versionService.createVersion(Node.this.getNodeRef(), versionProperties);					      
				
				// Clean the node after the version has been created
				cleanNode();
				
				// Create the version 
				return Version.createVersion(Node.this.session, repoVersion);
			}
		});     				      
	}
    
    /**
     * Dynamic implementation of get properties
     * 
     * @param name      the name of the property
     * @return Value    the value of the property
     */
    public Value __getField(Env env, Value name)
    {
        Value result = null;
        
        String fullName = this.session.getNamespaceMap().getFullName(name.toString());
        if (fullName.equals(name) == false)
        {
            // Make sure the properties are populated
            populateProperties();
            
            Object value = this.properties.get(fullName);
            if (value != null)
            {
                result = PHPProcessor.convertToValue(env, this.session, value);
            }
            else
            {
                result = NullValue.NULL;
            }
        }

        return result;        
    }

    /**
     * Dynamic implemenatation of set properties
     * 
     * @param name    the name of the property
     * @param value   the value of the property
     */
    public void __setField(String name, String value)
    {
        String fullName = this.session.getNamespaceMap().getFullName(name.toString());
        if (fullName.equals(name) == false)
        {
            // Make sure the properties are populated
            populateProperties();
            
            if (logger.isDebugEnabled() == true)
            {
                logger.debug("Setting field on node " + this.getId() + " (name="+ fullName + "; value:" + value.toString() + ")");
            }
            
            // Set the property value
            this.properties.put(fullName, value);
            this.arePropertiesDirty = true;
        }
    }
    
    /**
     * PHP toString implementation
     * 
     * @return  the node string representation
     */
    public String __toString()
    {
        return this.toString();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.store.getScheme() + "://" + this.store.getAddress() + "/" + this.id;
    }
    
    /*package*/ void prepareSave()
    {
        // Handle the creation of a new node
        if (isNewNode() == true)
        {
            // Get the primary parent
            if (this.primaryParent == null)
            {
                throw new PHPProcessorException("Unable to save new node since no valid primary parent has been found.");
            }
            
            // Create the new node
            ChildAssociationRef childAssocRef = this.nodeService.createNode(
                    this.primaryParent.getParent().getNodeRef(),
                    QName.createQName(this.primaryParent.getType()),
                    QName.createQName(this.primaryParent.getName()), 
                    QName.createQName(this.type));
            
            // Set the id of the new node
            this.id = childAssocRef.getChildRef().getId();
        }
    }
    
    /**
     * Called when the node is saved.  Inspects the node and persists any changes as appropriate.
     */
    /*package*/ void onSave()
    {
        // Get the node reference
        NodeRef nodeRef = getNodeRef();        
        if (this.arePropertiesDirty == true)
        {
            // Log details
            if (logger.isDebugEnabled() == true)
            {
                logger.debug("Saving property updates made to node " + this.getId());
            }
            
            // List of pending content properties to process
            List<org.alfresco.module.phpIntegration.lib.ContentData> pendingContentProperties = new ArrayList<org.alfresco.module.phpIntegration.lib.ContentData>(1);
            
            // Update the properties
            Map<QName, Serializable> currentProperties = this.nodeService.getProperties(nodeRef);
            for (Map.Entry<String, Object> entry : this.properties.entrySet())
            {
                if (entry.getValue() instanceof org.alfresco.module.phpIntegration.lib.ContentData)
                {
                    // Save the content property
                    org.alfresco.module.phpIntegration.lib.ContentData contentData = (org.alfresco.module.phpIntegration.lib.ContentData)entry.getValue();
                    pendingContentProperties.add(contentData);
                }
                else
                {
                    Serializable propValue = null; 
                        
                    // Get the property definition so we can do the correct conversion
                    QName propertyName = QName.createQName(entry.getKey());
                    DictionaryService dictionaryService = this.session.getServiceRegistry().getDictionaryService();                    
                    PropertyDefinition propDefintion = dictionaryService.getProperty(propertyName);
                    if (propDefintion == null)
                    {
                        // TODO summert here!
                        propValue = (Serializable)entry.getValue();
                    }
                    else
                    {
                        propValue = (Serializable)DefaultTypeConverter.INSTANCE.convert(propDefintion.getDataType(), entry.getValue());
                    }
                    
                    // Set the property value in the temp map
                    if (propValue == null || propValue.equals(currentProperties.get(propertyName)) == false)
                    {
                        currentProperties.put(propertyName, propValue);
                    }
                }
            }
            
            // Set the values of the updated properties
            this.nodeService.setProperties(nodeRef, currentProperties);
            
            // Sort out any pending content properties
            for (org.alfresco.module.phpIntegration.lib.ContentData contentData : pendingContentProperties)
            {
                contentData.onSave();
            }
        }
        
        // Update the aspects
        if (this.addedAspects != null && this.addedAspects.size() != 0)
        {
            for (String aspect : this.addedAspects)
            {
                this.nodeService.addAspect(nodeRef, QName.createQName(aspect), null);                
            }
        }
        if (this.removedAspects != null && this.removedAspects.size() != 0)
        {
            for (String aspect : this.removedAspects)
            {
                this.nodeService.removeAspect(nodeRef, QName.createQName(aspect));
            }
        }
        
        // Update the child associations
        if (this.addedChildren != null && this.addedChildren.size() != 0)
        {
            for (ChildAssociation addedChildAssociation : this.addedChildren)
            {
                if (addedChildAssociation.getIsPrimary() == false)
                {
                    this.nodeService.addChild(
                            nodeRef, 
                            addedChildAssociation.getChild().getNodeRef(),
                            QName.createQName(addedChildAssociation.getType()),
                            QName.createQName(addedChildAssociation.getName()));
                }
            }
        }
        if (this.removedChildren != null && this.removedChildren.size() != 0)
        {
            for (ChildAssociation removedChildAssociation : this.removedChildren)
            {
                this.nodeService.removeChild(nodeRef, removedChildAssociation.getChild().getNodeRef());
            }
        }
        
        // Update the associations
        if (this.addedAssociations != null && this.addedAssociations.size() != 0)
        {
            for (Association addedAssociation : this.addedAssociations)
            {
                this.nodeService.createAssociation(
                        nodeRef, 
                        addedAssociation.getTo().getNodeRef(), 
                        QName.createQName(addedAssociation.getType()));
            }
        }
        if (this.removedAssociations != null && this.removedAssociations.size() != 0)
        {
            for (Association removedAssociation : this.removedAssociations)
            {
                this.nodeService.removeAssociation(
                        nodeRef, 
                        removedAssociation.getTo().getNodeRef(),
                        QName.createQName(removedAssociation.getType()));
            }
        }
        
        // Refresh the state of the node
        cleanNode();      
    }
    
    /**
     * Cleans the nodes cached data and restores it to its initial state
     */
    protected void cleanNode()
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Cleaning node " + getId());            
        }
        
        this.properties = null;
        this.arePropertiesDirty = false;
        this.aspects = null;
        this.addedAspects = null;
        this.removedAspects = null;
        this.children = null;
        this.addedChildren = null;
        this.removedChildren = null;
        this.parents = null;
        this.primaryParent = null;
        this.associations = null;
        this.addedAssociations = null;
        this.removedAssociations = null;
    }
    
    private boolean isDirty()
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Calling isDrity() for " + getId() + " (isNewNode = " + isNewNode() + "; arePropertiesDirty = " + this.arePropertiesDirty + ")");
        }
        
        if (isNewNode() == false &&
            this.arePropertiesDirty == false &&
            (this.addedAspects == null || this.addedAspects.size() == 0 ) &&
            (this.removedAspects == null || this.removedAspects.size() == 0 ) &&
            (this.addedChildren == null || this.addedChildren.size() == 0 ) &&
            (this.removedChildren == null || this.removedChildren.size() == 0 ) &&
            (this.addedAssociations == null || this.addedAssociations.size() == 0 ) &&
            (this.removedAssociations == null || this.removedAssociations.size() == 0))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * Populates the properties of the node
     */
    private void populateProperties()
    {
    	this.session.doSessionWork(new SessionWork<Object>()
    	{
			public Object doWork() 
			{
		        if (Node.this.properties == null)
		        {
		            if (logger.isDebugEnabled() == true)
		            {
		                logger.debug("Populating properties for node " + Node.this.getId());                
		            }
		            
		            if (isNewNode() == false)
		            {
		                Map<QName, Serializable> properties = Node.this.nodeService.getProperties(getNodeRef());
		                Node.this.properties = new HashMap<String, Object>(properties.size());
		                for (Map.Entry<QName, Serializable> entry : properties.entrySet())
		                {
		                    if (entry.getValue() instanceof ContentData)
		                    {
		                        if (logger.isDebugEnabled() == true)
		                        {
		                            logger.debug("   - Found content property " + entry.getKey());                
		                        }
		                        
		                        ContentData value = (ContentData)entry.getValue();
		                        org.alfresco.module.phpIntegration.lib.ContentData contentData = new org.alfresco.module.phpIntegration.lib.ContentData(
		                        																	Node.this,
		                                                                                            entry.getKey().toString(),
		                                                                                            value.getMimetype(),
		                                                                                            value.getEncoding(),
		                                                                                            value.getSize());
		                        Node.this.properties.put(entry.getKey().toString(), contentData);
		                    }
		                    else
		                    {
		                        String value = DefaultTypeConverter.INSTANCE.convert(String.class, entry.getValue());
		                        Node.this.properties.put(entry.getKey().toString(), value);
		                    }
		                }
		            }
		            else
		            {
		            	Node.this.properties = new HashMap<String, Object>(10);
		            }
		            Node.this.arePropertiesDirty = false;
		        }
		        
		        return null;
			}
    	});
    }
    
    /**
     * Get the list of aspects for this node.
     * 
     * @return  List<String>    list containing aspects
     */
    private void populateAspects()
    {
    	this.session.doSessionWork(new SessionWork<Object>()
    	{
			public Object doWork() 
			{
		        if (Node.this.aspects == null)
		        {
		            if (isNewNode() == false)
		            {
		                // Populate the aspect list from the node service
		                Set<QName> aspects = Node.this.nodeService.getAspects(getNodeRef());
		                Node.this.aspects = new ArrayList<String>(aspects.size());
		                for (QName aspect : aspects)
		                {
		                	Node.this.aspects.add(aspect.toString());
		                }
		            }
		            else
		            {
		            	Node.this.aspects = new ArrayList<String>(5);
		            }
		            
		            // Create the list's used to monitor added and deleted aspects
		            Node.this.addedAspects = new ArrayList<String>();
		            Node.this.removedAspects = new ArrayList<String>();
		        }
		        
		        return null;
			}
    	});
    }
    
    /**
     * Populates the child information for this node
     */
    private void populateChildren()
    {
    	this.session.doSessionWork(new SessionWork<Object>()
    	{
			public Object doWork() 
			{
		        if (Node.this.children == null)
		        {
		            if (isNewNode() == false)
		            {                
		                List<ChildAssociationRef> assocs = Node.this.nodeService.getChildAssocs(getNodeRef());
		                Node.this.children = new ArrayList<ChildAssociation>(assocs.size());
		                for (ChildAssociationRef assoc : assocs)
		                {
		                	Node.this.children.add(
		                            new ChildAssociation(
		                            		Node.this.session.getNodeFromString(assoc.getParentRef().toString()),
		                            		Node.this.session.getNodeFromString(assoc.getChildRef().toString()),
		                                    assoc.getTypeQName().toString(),
		                                    assoc.getQName().toString(),
		                                    assoc.isPrimary(),
		                                    assoc.getNthSibling()));
		                }
		            }
		            else
		            {
		            	Node.this.children = new ArrayList<ChildAssociation>(10);
		            }
		            
		            // Create the added and removed lists
		            Node.this.addedChildren = new ArrayList<ChildAssociation>(5);
		            Node.this.removedChildren = new ArrayList<ChildAssociation>(5);
		        } 
		        
		        return null;
			}
    	});
    }
    
    /**
     * Populates the parent information for this node
     */
    private void populateParents()
    {
    	this.session.doSessionWork(new SessionWork<Object>()
    	{
			public Object doWork() 
			{
		        if (Node.this.parents == null)
		        {
		            if (isNewNode() == false)
		            {
		                List<ChildAssociationRef> parents = Node.this.nodeService.getParentAssocs(getNodeRef());
		                Node.this.parents = new ArrayList<ChildAssociation>(parents.size());
		                for (ChildAssociationRef assoc : parents)
		                {
		                    ChildAssociation childAssociation = new ChildAssociation(
		                    		Node.this.session.getNodeFromString(assoc.getParentRef().toString()),
		                    		Node.this.session.getNodeFromString(assoc.getChildRef().toString()),
		                            assoc.getTypeQName().toString(),
		                            assoc.getQName().toString(),
		                            assoc.isPrimary(),
		                            assoc.getNthSibling());
		                    Node.this.parents.add(childAssociation);
		                    
		                    // Set the primary parent when we come across it
		                    if (assoc.isPrimary() == true)
		                    {
		                    	Node.this.primaryParent = childAssociation;
		                    }
		                }
		            }
		            else
		            {
		            	Node.this.parents = new ArrayList<ChildAssociation>(5);
		            }
		        }
				
				return null;
			}
    	});
    }
    
    /**
     * Populates the association information for this node
     */
    private void populateAssociations()
    {
    	this.session.doSessionWork(new SessionWork<Object>()
    	{
			public Object doWork() 
			{
		        if (Node.this.associations == null)
		        {
		            if (isNewNode() == false)
		            {
		                List<AssociationRef> associations = Node.this.nodeService.getTargetAssocs(getNodeRef(), RegexQNamePattern.MATCH_ALL);
		                Node.this.associations = new ArrayList<Association>(associations.size());
		                for (AssociationRef association : associations)
		                {
		                	Node.this.associations.add(
		                            new Association(
		                            		Node.this.session.getNodeFromString(association.getSourceRef().toString()),
		                            		Node.this.session.getNodeFromString(association.getTargetRef().toString()),
		                                    association.getTypeQName().toString()));
		                }
		            }
		            else
		            {
		            	Node.this.associations = new ArrayList<Association>(5);
		            }
		            
		            // Create the added and removes association lists
		            Node.this.addedAssociations = new ArrayList<Association>(5);
		            Node.this.removedAssociations = new ArrayList<Association>(5);
		        }
		        
		        return null;
			}
    	});
    }
    
    @SuppressWarnings("unused")
    private void dumpProperties(String message)
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Current property values (" + message + ") ...");
            for (Map.Entry<String, Object> entry : this.properties.entrySet())
            {
                logger.debug("   - " + entry.getKey() + ":" + entry.getValue());
            }
        }
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Node))
        {
            return false;
        }
        Node other = (Node) o;

        return (EqualsHelper.nullSafeEquals(this.id, other.id)
                && EqualsHelper.nullSafeEquals(this.store, other.store));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return id.hashCode();
    }
    
    /**
     * 
     * @param template
     * @return
     */
    public String processTemplate(ScriptNode template)
    {
        return processTemplate(template.getContent(), null, null);
    }
    
    /**
     * 
     * @param template
     * @param args
     * @return
     */
    public String processTemplate(ScriptNode template, Map<String, Object> args)
    {
        return processTemplate(template.getContent(), null, args);
    }
    
    /**
     * 
     * @param template
     * @return
     */
    public String processTemplate(String template)
    {
        return processTemplate(template, null, null);
    }
    
    /**
     * 
     * @param template
     * @param args
     * @return
     */
    public String processTemplate(String template, Map<String, Object> args)
    {
        return processTemplate(template, null, args);
    }
    
    /**
     * 
     * @param template
     * @param templateRef
     * @param args
     * @return
     */
    private String processTemplate(String template, NodeRef templateRef, Map<String, Object> args)
    {
        NodeRef person = null;
        NodeRef companyHome = null;
        NodeRef userHome = null;        
        
        Map<String, Object> model = templateService.buildDefaultModel(person, companyHome, userHome, templateRef, null);
                
        // add the current node as either the document/space as appropriate
        DictionaryService dd = this.getSession().getServiceRegistry().getDictionaryService();
        boolean isDocument = Boolean.valueOf(dd.isSubClass(QName.createQName(getType()), ContentModel.TYPE_CONTENT));
        if (isDocument == true)
        {
            model.put("document", getNodeRef());
            model.put("space", getPrimaryParent());
        }
        else
        {
            model.put("space", getNodeRef());
        }
        
        // add the supplied args to the 'args' root object
        if (args != null)
        {            
            // TODO the values may need converting to the correct types ...
            // add the args to the model as the 'args' root object
            model.put("args", args);
        }
        
        // execute template
        return templateService.processTemplateString(null, template, model);
    }
    
}
