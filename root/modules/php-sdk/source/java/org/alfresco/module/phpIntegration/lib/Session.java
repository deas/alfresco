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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caucho.quercus.annotation.Optional;

/**
 * The PHP Session object implementation.
 * 
 * @author Roy Wetherall
 */
public class Session implements ScriptObject
{
    /** Logger */
    private static Log    logger = LogFactory.getLog(Session.class);
    
    /** Script object name */
    private static final String SCRIPT_OBJECT_NAME = "Session";

    /** Repository */
    private Repository repository;
    
    /** The ticket */
    private String ticket;
    
    /** Namespace map */
    private NamespaceMap namespaceMap;
    
    /** Data dictionary */
    private DataDictionary dataDictionary;
    
    /** Internal cache of nodes in the scope of this session */
    private Map<String, Node> nodeMap;
    
    /**
     * Constructor
     * 
     * @param repository        the repository
     * @param ticket            the authentication context within which this session is operating
     */
    /*package*/ Session(Repository repository, String ticket)
    {
        this.repository = repository;
        this.ticket = ticket;
        this.namespaceMap = new NamespaceMap(this);
        this.dataDictionary = new DataDictionary(this);
        this.nodeMap = new HashMap<String, Node>(10);
    }
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    /**
     * The currently authenticated ticket
     * 
     * @return  String  the ticket
     */
    public String getTicket()
    {
        return this.ticket;
    }
    
    public Repository getRepository()
    {
        return this.repository;
    }
    
    /**
     * Get the service registry
     * 
     * @return the service registry
     */
    /*package*/ ServiceRegistry getServiceRegistry() 
    {
		return this.repository.getServiceRegistry();
	}
    
    /*package*/ NodeFactory getNodeFactory()
    {
        return this.repository.getNodeFactory();
    }
    
    /**
     * Get the namespace map
     * 
     * @return  NamespaceMap    the namespace map
     */
    public NamespaceMap getNamespaceMap()
    {
        return this.namespaceMap;
    }
    
    /**
     * Gets the data dictionary
     * 
     * @return  DataDictionary  the data dictionary
     */
    public DataDictionary getDataDictionary()
    {
        return this.dataDictionary;
    }
    
    /**
     * Gets a list of the stores in the repository
     * 
     * @return  Store[]     a list of stores
     */
    public Store[] getStores()
    {
    	return doSessionWork(new SessionWork<Store[]>()
    	{
			public Store[] doWork() 
			{
				// Get the node service
		        NodeService nodeService = getServiceRegistry().getNodeService();
		        
		        // Get the stores
		        List<StoreRef> storeRefs = nodeService.getStores();
		        
		        // Build the result array
		        Store[] result = new Store[storeRefs.size()];
		        int index = 0;
		        for (StoreRef storeRef : storeRefs)
		        {
		            result[index] = new Store(Session.this, storeRef);
		            index ++;
		        }
		        
		        return result;
			}
    	});        
    }    
    
    /**
     * Get the store object
     * 
     * @param address   the address of the store
     * @param scheme    the scheme of the store
     * @return Store    the Store object
     */
    public Store getStore(final String address, @Optional(StoreRef.PROTOCOL_WORKSPACE) final String scheme)
    {
    	StoreRef storeRef = new StoreRef(scheme, address);
		return new Store(Session.this, storeRef);
    }
    
    /**
     * Gets a store reference from a string value
     * 
     * @param value		the value representing the store
     * @return Store	the store
     */
    public Store getStoreFromString(final String value)
    {
    	StoreRef storeRef = new StoreRef(value);
		return new Store(Session.this, storeRef);
    }
    
    /**
     * Get the node object for the provided node details
     * 
     * @param store		the store
     * @param id		the node id
     * @return Node		the node
     */
    public Node getNode(Store store, String id)
    {
        NodeRef nodeRef = new NodeRef(store.getStoreRef(), id);
        return getNodeImpl(nodeRef);
    }
    
    /**
     * Get a node from a string value
     * 
     * @param nodeString	the node string value
     * @return Node			the node
     */
    public Node getNodeFromString(String nodeString)
    {
        NodeRef nodeRef = new NodeRef(nodeString);
        return getNodeImpl(nodeRef);
    }
    
    /**
     * Gets the node for the node reference provided
     * 
     * @param nodeRef
     * @return
     */
    private Node getNodeImpl(final NodeRef nodeRef)
    {
    	return doSessionWork(new SessionWork<Node>()
    	{
			public Node doWork() 
			{
		        Node node = Session.this.nodeMap.get(nodeRef.toString());
		        
		        if (node == null)
		        {        
		            // Check for the existance of the node        
		            if (getServiceRegistry().getNodeService().exists(nodeRef) == true)
		            {
		                // Use the node factory to create the node
		                node = getNodeFactory().createNode(Session.this, nodeRef);
		            }
		        }
		        
		        return node;
			}
    	});
    }
    
    /**
     * Adds a node to the session cache
     * 
     * @param node  the node to add to the session cache
     */
    /*package*/ void addNode(Node node)
    {
        // Log a warning if the node is already in the session
        if (this.nodeMap.containsKey(node.toString()) == true)
        {
            if (logger.isInfoEnabled() == true)
            {
                logger.info("A duplicate node is being added to the session. (" + node.toString() + ")");
            }
        }
        
        // Add the node
        this.nodeMap.put(node.toString(), node);
    }
    
    /**
     * Remove the node from the session cache.  This be called with care as it could mean modificaiton made to the node are lost.
     * 
     * @param node  the node to remove from the session cache
     */
    /*package*/ void removeNode(Node node)
    {
        // Remove the node
        this.nodeMap.remove(node.toString());
    }
    
    /**
     * Execute a query
     * 
     * @param store			the store
     * @param statement		the query statement
     * @param language		the query language
     * @return				the result of the query
     */
    public Node[] query(final Store store, final String statement, @Optional(SearchService.LANGUAGE_LUCENE) final String language)
    {
    	return doSessionWork(new SessionWork<Node[]>()
    	{
			public Node[] doWork() 
			{
		        Node[] result = null;
		        
		        // Get the search service
		        SearchService searchService = getServiceRegistry().getSearchService();
		        
		        // Do the search
		        ResultSet resultSet = searchService.query(store.getStoreRef(), language, statement);
		        List<NodeRef> nodeRefs = resultSet.getNodeRefs();
		        resultSet.close();
		        result = new Node[nodeRefs.size()];
		        int iIndex = 0;
		        for (NodeRef nodeRef : nodeRefs)
		        {
		            result[iIndex] = getNodeImpl(nodeRef);
		            iIndex++;
		        }        
		        
		        return result;
			}
    	});
    }
    
    /**
     * Save all the changes made to the nodes since the last save was called.
     */
    public void save()
    {
    	doSessionWork(new SessionWork<Object>()
    	{
			public Object doWork() 
			{
		        if (logger.isDebugEnabled() == true)
		        {
		            logger.debug("Saving session");
		        }
		        
		        // Prepare for the save
		        for (Node node : Session.this.nodeMap.values())
		        {
		            // Prepare each node for saving
		            node.prepareSave();
		        }
		        
		        // Check each node and see whether the node needs to be saved
		        for (Node node : Session.this.nodeMap.values())
		        {
		            // Do the save processing on each node
		            node.onSave();
		        }
		        
		        return null;
			}
    	});
    }
    
    /**
     * Clean the session, all changes will be lost and node will be re-read.
     */
    public void clean()
    {
        // Clear the node map to clean the session
        this.nodeMap.clear();
    }
    
    /**
     * Executes some work on the session within the correct security and transaction context
     * 
     * @param <R>	the return type
     * @param work	the work 
     * @return R	the result of the work
     */
    public <R> R doSessionWork(final SessionWork<R> work)
    {
    	R result = null;
    	
    	// Get the required services
    	AuthenticationService authenticationService = getServiceRegistry().getAuthenticationService();
    	TransactionService transactionService = getServiceRegistry().getTransactionService();
    	
    	AuthenticationUtil.pushAuthentication();
        try
        {
        	// Validate for the currently held ticket
            authenticationService.validate(ticket);
            
            // Do the work in a retrying transaction
            RetryingTransactionCallback<R> callback = new RetryingTransactionCallback<R>()
            {
                public R execute() throws Throwable
                {
                    return work.doWork();
                }
            };
            result = transactionService.getRetryingTransactionHelper().doInTransaction(callback, false);
        }
        finally
        {
        	// Re-establish the previous authentication context
            AuthenticationUtil.popAuthentication();
        }
    	
    	return result;    	
    }
}
