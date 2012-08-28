/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.repo.node;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.domain.node.Node;
import org.alfresco.repo.domain.node.NodeDAO;
import org.alfresco.repo.domain.node.NodeEntity;
import org.alfresco.repo.domain.node.NodeVersionKey;
import org.alfresco.repo.domain.node.ParentAssocsInfo;
import org.alfresco.repo.domain.qname.QNameDAO;
import org.alfresco.repo.domain.query.CannedQueryDAO;
import org.alfresco.repo.node.NodeServicePolicies.BeforeCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeSetNodeTypePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeUpdateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnSetNodeTypePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.node.db.NodeHierarchyWalker;
import org.alfresco.repo.node.db.NodeHierarchyWalker.VisitedNode;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.Policy;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeRef.Status;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.GUID;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Tests basic {@link NodeService} functionality
 * 
 * @author Derek Hulley
 * @since 4.0
 */
public class NodeServiceTest extends TestCase
{
    public static final String NAMESPACE = "http://www.alfresco.org/test/BaseNodeServiceTest";
    public static final String TEST_PREFIX = "test";
    public static final QName  TYPE_QNAME_TEST = QName.createQName(NAMESPACE, "multiprop");
    public static final QName  PROP_QNAME_NAME = QName.createQName(NAMESPACE, "name");
    public static final QName  ASSOC_QNAME_CHILDREN = QName.createQName(NAMESPACE, "child");
    
    private static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();
    
    private static Log logger = LogFactory.getLog(NodeServiceTest.class);
    
    protected ServiceRegistry serviceRegistry;
    protected NodeService nodeService;
    protected NodeDAO nodeDAO;
    private TransactionService txnService;
    private PolicyComponent policyComponent;
    private CannedQueryDAO cannedQueryDAO;
    private SimpleCache<Serializable, Serializable> nodesCache;
    private SimpleCache<Serializable, Serializable> propsCache;
    private SimpleCache<Serializable, Serializable> aspectsCache;
    private SimpleCache<Serializable, Serializable> parentAssocsCache;
    
    /** populated during setup */
    protected NodeRef rootNodeRef;

    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception
    {
        I18NUtil.setLocale(null);

        serviceRegistry = (ServiceRegistry) ctx.getBean(ServiceRegistry.SERVICE_REGISTRY);
        nodeService = serviceRegistry.getNodeService();
        nodeDAO = (NodeDAO) ctx.getBean("nodeDAO");
        txnService = serviceRegistry.getTransactionService();
        policyComponent = (PolicyComponent) ctx.getBean("policyComponent");
        cannedQueryDAO = (CannedQueryDAO) ctx.getBean("cannedQueryDAO");
        
        // Get the caches for later testing
        nodesCache = (SimpleCache<Serializable, Serializable>) ctx.getBean("node.nodesSharedCache");
        propsCache = (SimpleCache<Serializable, Serializable>) ctx.getBean("node.propertiesSharedCache");
        aspectsCache = (SimpleCache<Serializable, Serializable>) ctx.getBean("node.aspectsSharedCache");
        parentAssocsCache = (SimpleCache<Serializable, Serializable>) ctx.getBean("node.parentAssocsSharedCache");
        
        // Clear the caches to remove fluff
        nodesCache.clear();
        propsCache.clear();
        aspectsCache.clear();
        parentAssocsCache.clear();
        
        AuthenticationUtil.setRunAsUserSystem();
        
        // create a first store directly
        RetryingTransactionCallback<NodeRef> createStoreWork = new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute()
            {
                StoreRef storeRef = nodeService.createStore(
                        StoreRef.PROTOCOL_WORKSPACE,
                        "Test_" + System.nanoTime());
                return nodeService.getRootNode(storeRef);
            }
        };
        rootNodeRef = txnService.getRetryingTransactionHelper().doInTransaction(createStoreWork);
    }
    
    /**
     * Clean up the test thread
     */
    @Override
    protected void tearDown()
    {
        AuthenticationUtil.clearCurrentSecurityContext();
        I18NUtil.setLocale(null);
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull(rootNodeRef);
    }
    
    public void testLocaleSupport() throws Exception
    {
        // Ensure that the root node has the default locale
        Locale locale = (Locale) nodeService.getProperty(rootNodeRef, ContentModel.PROP_LOCALE);
        assertNotNull("Locale property must occur on every node", locale);
        assertEquals("Expected default locale on the root node", I18NUtil.getLocale(), locale);
        assertTrue("Every node must have sys:localized", nodeService.hasAspect(rootNodeRef, ContentModel.ASPECT_LOCALIZED));
        
        // Now switch to a specific locale and create a new node
        I18NUtil.setLocale(Locale.CANADA_FRENCH);
        
        // Create a node using an explicit locale
        NodeRef nodeRef1 = nodeService.createNode(
                rootNodeRef,
                ContentModel.ASSOC_CHILDREN,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, getName()),
                ContentModel.TYPE_CONTAINER,
                Collections.singletonMap(ContentModel.PROP_LOCALE, (Serializable)Locale.GERMAN)).getChildRef();
        assertTrue("Every node must have sys:localized", nodeService.hasAspect(nodeRef1, ContentModel.ASPECT_LOCALIZED));
        assertEquals(
                "Didn't set the explicit locale during create. ",
                Locale.GERMAN, nodeService.getProperty(nodeRef1, ContentModel.PROP_LOCALE));
        
        // Create a node using the thread's locale
        NodeRef nodeRef2 = nodeService.createNode(
                rootNodeRef,
                ContentModel.ASSOC_CHILDREN,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, getName()),
                ContentModel.TYPE_CONTAINER).getChildRef();
        assertTrue("Every node must have sys:localized", nodeService.hasAspect(nodeRef2, ContentModel.ASPECT_LOCALIZED));
        assertEquals(
                "Didn't set the locale during create. ",
                Locale.CANADA_FRENCH, nodeService.getProperty(nodeRef2, ContentModel.PROP_LOCALE));
        
        // Switch Locale and modify ml:text property
        I18NUtil.setLocale(Locale.CHINESE);
        nodeService.setProperty(nodeRef2, ContentModel.PROP_DESCRIPTION, "Chinese description");
        I18NUtil.setLocale(Locale.FRENCH);
        nodeService.setProperty(nodeRef2, ContentModel.PROP_DESCRIPTION, "French description");
        
        // Expect that we have MLText (if we are ML aware)
        boolean wasMLAware = MLPropertyInterceptor.setMLAware(true);
        try
        {
            MLText checkDescription = (MLText) nodeService.getProperty(nodeRef2, ContentModel.PROP_DESCRIPTION);
            assertEquals("Chinese description", checkDescription.getValue(Locale.CHINESE));
            assertEquals("French description", checkDescription.getValue(Locale.FRENCH));
        }
        finally
        {
            MLPropertyInterceptor.setMLAware(wasMLAware);
        }
        // But the node locale must not have changed
        assertEquals(
                "Node modification should not affect node locale. ",
                Locale.CANADA_FRENCH, nodeService.getProperty(nodeRef2, ContentModel.PROP_LOCALE));
        
        // Now explicitly set the node's locale
        nodeService.setProperty(nodeRef2, ContentModel.PROP_LOCALE, Locale.ITALY);
        assertEquals(
                "Node locale must be settable. ",
                Locale.ITALY, nodeService.getProperty(nodeRef2, ContentModel.PROP_LOCALE));
        // But mltext must be unchanged
        assertEquals(
                "Canada-French must be closest to French. ",
                "French description", nodeService.getProperty(nodeRef2, ContentModel.PROP_DESCRIPTION));
        
        // Finally, ensure that setting Locale to 'null' is takes the node back to its original locale
        nodeService.setProperty(nodeRef2, ContentModel.PROP_LOCALE, null);
        assertEquals(
                "Node locale set to 'null' does nothing. ",
                Locale.ITALY, nodeService.getProperty(nodeRef2, ContentModel.PROP_LOCALE));
        nodeService.removeProperty(nodeRef2, ContentModel.PROP_LOCALE);
        assertEquals(
                "Node locale removal does nothing. ",
                Locale.ITALY, nodeService.getProperty(nodeRef2, ContentModel.PROP_LOCALE));
        
        // Mass-set the properties, changing the locale in the process
        Map<QName, Serializable> props = nodeService.getProperties(nodeRef2);
        props.put(ContentModel.PROP_LOCALE, Locale.GERMAN);
        nodeService.setProperties(nodeRef2, props);
        assertEquals(
                "Node locale not set in setProperties(). ",
                Locale.GERMAN, nodeService.getProperty(nodeRef2, ContentModel.PROP_LOCALE));
    }

    /**
     * Creates a string of parent-child nodes to fill the given array of nodes
     * 
     * @param workspaceRootNodeRef          the store to use
     * @param liveNodeRefs                  the node array to fill
     */
    private void buildNodeHierarchy(final NodeRef workspaceRootNodeRef, final NodeRef[] liveNodeRefs)
    {
        RetryingTransactionCallback<Void> setupCallback = new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                Map<QName, Serializable> props = new HashMap<QName, Serializable>(3);
                props.put(ContentModel.PROP_NAME, "depth-" + 0 + "-" + GUID.generate());
                liveNodeRefs[0] = nodeService.createNode(
                        workspaceRootNodeRef,
                        ContentModel.ASSOC_CHILDREN,
                        QName.createQName(NAMESPACE, "depth-" + 0),
                        ContentModel.TYPE_FOLDER,
                        props).getChildRef();
                for (int i = 1; i < liveNodeRefs.length; i++)
                {
                    props.put(ContentModel.PROP_NAME, "depth-" + i);
                    liveNodeRefs[i] = nodeService.createNode(
                            liveNodeRefs[i-1],
                            ContentModel.ASSOC_CONTAINS,
                            QName.createQName(NAMESPACE, "depth-" + i),
                            ContentModel.TYPE_FOLDER,
                            props).getChildRef();
                }
                return null;
            }
        };
        txnService.getRetryingTransactionHelper().doInTransaction(setupCallback);
    }

    public void testRootAspect() throws Exception
    {
        final NodeRef workspaceRootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        final NodeRef[] nodes = new NodeRef[6];
        buildNodeHierarchy(workspaceRootNodeRef, nodes);

        Set<NodeRef> allRootNodes = nodeService.getAllRootNodes(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        int initialNumRootNodes = allRootNodes.size();

        nodeService.addAspect(nodes[1], ContentModel.ASPECT_ROOT, null);
        nodeService.addAspect(nodes[3], ContentModel.ASPECT_ROOT, null);
        nodeService.addAspect(nodes[4], ContentModel.ASPECT_ROOT, null);

        allRootNodes = nodeService.getAllRootNodes(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        assertEquals("", 3, allRootNodes.size() - initialNumRootNodes);
        List<Path> paths = nodeService.getPaths(nodes[5], false);
        assertEquals("", 4, paths.size());

        nodeService.removeAspect(nodes[3], ContentModel.ASPECT_ROOT);
        allRootNodes = nodeService.getAllRootNodes(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        assertEquals("", 2, allRootNodes.size() - initialNumRootNodes);
        paths = nodeService.getPaths(nodes[5], false);
        for(Path path : paths)
        {
            System.out.println("Path = " + path.toString());
        }
        assertEquals("", 3, paths.size());
    }

    /**
     * Tests that two separate node trees can be deleted concurrently at the database level.
     * This is not a concurrent thread issue; instead we delete a hierarchy and hold the txn
     * open while we delete another in a new txn, thereby testing that DB locks don't prevent
     * concurrent deletes.
     * <p/>
     * See: <a href="https://issues.alfresco.com/jira/browse/ALF-5714">ALF-5714</a>
     * 
     * Note: if this test hangs for MySQL then check if 'innodb_locks_unsafe_for_binlog = true' (and restart MySQL + test)
     */
    public void testConcurrentArchive() throws Exception
    {
        final NodeRef workspaceRootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        final NodeRef[] nodesPrimer = new NodeRef[1];
        buildNodeHierarchy(workspaceRootNodeRef, nodesPrimer);
        final NodeRef[] nodesOne = new NodeRef[10];
        buildNodeHierarchy(workspaceRootNodeRef, nodesOne);
        final NodeRef[] nodesTwo = new NodeRef[10];
        buildNodeHierarchy(workspaceRootNodeRef, nodesTwo);
        
        // Prime the root of the archive store (first child adds inherited ACL)
        nodeService.deleteNode(nodesPrimer[0]);
        
        RetryingTransactionCallback<Void> outerCallback = new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                // Delete the first hierarchy
                nodeService.deleteNode(nodesOne[0]);
                // Keep the txn hanging around to maintain DB locks
                // and start a second transaction to delete another hierarchy
                RetryingTransactionCallback<Void> innerCallback = new RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        nodeService.deleteNode(nodesTwo[0]);
                        return null;
                    }
                };
                txnService.getRetryingTransactionHelper().doInTransaction(innerCallback, false, true);
                return null;
            }
        };
        txnService.getRetryingTransactionHelper().doInTransaction(outerCallback, false, true);
    }
    
    /**
     * Tests archive and restore of simple hierarchy, checking that references and IDs are
     * used correctly.
     */
    public void testArchiveAndRestore()
    {
        // First create a node structure (a very simple one) and record the references and IDs
        final NodeRef[] liveNodeRefs = new NodeRef[10];
        final NodeRef[] archivedNodeRefs = new NodeRef[10];
        
        final NodeRef workspaceRootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        final NodeRef archiveRootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_ARCHIVE_SPACESSTORE);

        buildNodeHierarchy(workspaceRootNodeRef, liveNodeRefs);

        // Get the node status details
        Long txnIdCreate = null;
        for (int i = 0; i < liveNodeRefs.length; i++)
        {
            StoreRef archivedStoreRef = archiveRootNodeRef.getStoreRef();
            archivedNodeRefs[i] = new NodeRef(archivedStoreRef, liveNodeRefs[i].getId());

            Status liveStatus = nodeService.getNodeStatus(liveNodeRefs[i]);
            Status archivedStatus = nodeService.getNodeStatus(archivedNodeRefs[i]);
            
            // Check that live node statuses are correct
            assertNotNull("'Live' node " + i + " status does not exist.", liveStatus);
            assertFalse("'Live' node " + i + " should be node be deleted", liveStatus.isDeleted());
            assertNull("'Archived' node " + i + " should not (yet) exist.", archivedStatus);
            
            // Nodes in the hierarchy must be in the same txn
            if (txnIdCreate == null)
            {
                txnIdCreate = liveStatus.getDbTxnId();
            }
            else
            {
                // Make sure that the DB Txn ID is the same
                assertEquals(
                        "DB TXN ID should have been the same for the hierarchy. ",
                        txnIdCreate, liveStatus.getDbTxnId());
            }
        }
        
        // Archive the top-level node
        nodeService.deleteNode(liveNodeRefs[0]);
        
        // Recheck the nodes and make sure that all the 'live' nodes are deleted
        Long txnIdDelete = null;
        for (int i = 0; i < liveNodeRefs.length; i++)
        {
            Status liveStatus = nodeService.getNodeStatus(liveNodeRefs[i]);
            Status archivedStatus = nodeService.getNodeStatus(archivedNodeRefs[i]);
            
            // Check that the ghosted nodes are marked as deleted and the archived nodes are not
            assertNotNull("'Live' node " + i + " status does not exist.", liveStatus);
            assertTrue("'Live' node " + i + " should be deleted (ghost entries)", liveStatus.isDeleted());
            assertNotNull("'Archived' node " + i + " does not exist.", archivedStatus);
            assertFalse("'Archived' node " + i + " should be undeleted", archivedStatus.isDeleted());

            // Check that both old (ghosted deletes) and new nodes are in the same txn
            if (txnIdDelete == null)
            {
                txnIdDelete = liveStatus.getDbTxnId();
            }
            else
            {
                // Make sure that the DB Txn ID is the same
                assertEquals(
                        "DB TXN ID should have been the same for the deleted (ghost) nodes. ",
                        txnIdDelete, liveStatus.getDbTxnId());
            }
            assertEquals(
                    "DB TXN ID should be the same for deletes across the hierarchy",
                    txnIdDelete, archivedStatus.getDbTxnId());
        }
        
        // Restore the top-level node
        nodeService.restoreNode(archivedNodeRefs[0], workspaceRootNodeRef, null, null);
        
        // Recheck the nodes and make sure that all the 'archived' nodes are deleted and the 'live' nodes are back
        Long txnIdRestore = null;
        for (int i = 0; i < liveNodeRefs.length; i++)
        {
            Status liveStatus = nodeService.getNodeStatus(liveNodeRefs[i]);
            StoreRef archivedStoreRef = archiveRootNodeRef.getStoreRef();
            archivedNodeRefs[i] = new NodeRef(archivedStoreRef, liveNodeRefs[i].getId());
            Status archivedStatus = nodeService.getNodeStatus(archivedNodeRefs[i]);
            
            // Check that the ghosted nodes are marked as deleted and the archived nodes are not
            assertNotNull("'Live' node " + i + " status does not exist.", liveStatus);
            assertFalse("'Live' node " + i + " should not be deleted", liveStatus.isDeleted());
            assertNotNull("'Archived' node " + i + " does not exist.", archivedStatus);
            assertTrue("'Archived' node " + i + " should be deleted (ghost entry)", archivedStatus.isDeleted());

            // Check that both old (ghosted deletes) and new nodes are in the same txn
            if (txnIdRestore == null)
            {
                txnIdRestore = liveStatus.getDbTxnId();
            }
            else
            {
                // Make sure that the DB Txn ID is the same
                assertEquals(
                        "DB TXN ID should have been the same for the restored nodes. ",
                        txnIdRestore, liveStatus.getDbTxnId());
            }
            assertEquals(
                    "DB TXN ID should be the same for the ex-archived (now-ghost) nodes. ",
                    txnIdRestore, archivedStatus.getDbTxnId());
        }
    }
    
    public void testGetAssocById()
    {
        // Get a node association that doesn't exist
        AssociationRef assocRef = nodeService.getAssoc(Long.MAX_VALUE);
        assertNull("Should get null for missing ID of association. ", assocRef);
    }
    
    public void testDuplicateChildNodeName()
    {
        final NodeRef[] liveNodeRefs = new NodeRef[3];
        final NodeRef workspaceRootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        buildNodeHierarchy(workspaceRootNodeRef, liveNodeRefs);
        
        // Get the name of the last node
        final String lastName = (String) nodeService.getProperty(liveNodeRefs[2], ContentModel.PROP_NAME);
        // Now create a node with the same name
        RetryingTransactionCallback<NodeRef> newNodeCallback = new RetryingTransactionCallback<NodeRef>()
        {
            @Override
            public NodeRef execute() throws Throwable
            {
                Map<QName, Serializable> props = new HashMap<QName, Serializable>(3);
                props.put(ContentModel.PROP_NAME, lastName);
                return nodeService.createNode(
                        liveNodeRefs[1],
                        ContentModel.ASSOC_CONTAINS,
                        QName.createQName(NAMESPACE, "duplicate"),
                        ContentModel.TYPE_FOLDER,
                        props).getChildRef();
            }
        };
        try
        {
            txnService.getRetryingTransactionHelper().doInTransaction(newNodeCallback);
            fail("Duplicate child node name not detected.");
        }
        catch (DuplicateChildNodeNameException e)
        {
            // Expected
        }
    }
    
    public void testGetChildren_Limited()
    {
        // Create a node and loads of children
        final NodeRef[] liveNodeRefs = new NodeRef[10];
        final NodeRef workspaceRootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);

        buildNodeHierarchy(workspaceRootNodeRef, liveNodeRefs);
        
        // Hook 3rd and subsequent children into 1st child
        for (int i = 2; i < liveNodeRefs.length; i++)
        {
            nodeService.addChild(
                    liveNodeRefs[0],
                    liveNodeRefs[i],
                    ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NAMESPACE, "secondary"));
        }
        
        // Do limited queries each time
        for (int i = 1; i < liveNodeRefs.length; i++)
        {
            List<ChildAssociationRef> childAssocRefs = nodeService.getChildAssocs(liveNodeRefs[0], null, null, i, true);
            assertEquals("Expected exact number of child assocs", i, childAssocRefs.size());
        }
        
        // Repeat, but don't preload
        for (int i = 1; i < liveNodeRefs.length; i++)
        {
            List<ChildAssociationRef> childAssocRefs = nodeService.getChildAssocs(liveNodeRefs[0], null, null, i, false);
            assertEquals("Expected exact number of child assocs", i, childAssocRefs.size());
        }
    }
    
    /**
     * Checks that the node caches react correctly when a node is deleted
     */
    public void testCaches_DeleteNode()
    {
        final NodeRef[] liveNodeRefs = new NodeRef[10];
        final NodeRef workspaceRootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);

        buildNodeHierarchy(workspaceRootNodeRef, liveNodeRefs);
        nodeService.addAspect(liveNodeRefs[3], ContentModel.ASPECT_TEMPORARY, null);
        
        // Create a child under node 2
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(3);
        props.put(ContentModel.PROP_NAME, "Secondary");
        NodeRef secondaryNodeRef = nodeService.createNode(
                liveNodeRefs[2],
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NAMESPACE, "secondary"),
                ContentModel.TYPE_FOLDER,
                props).getChildRef();
        // Make it a child of node 3
        nodeService.addChild(liveNodeRefs[3], secondaryNodeRef, ContentModel.ASSOC_CONTAINS, QName.createQName(NAMESPACE, "secondary"));
        // Make it a child of node 4
        nodeService.addChild(liveNodeRefs[4], secondaryNodeRef, ContentModel.ASSOC_CONTAINS, QName.createQName(NAMESPACE, "secondary"));
        
        // Check
        List<ChildAssociationRef> parentAssocsPre = nodeService.getParentAssocs(secondaryNodeRef);
        assertEquals("Incorrect number of parent assocs", 3, parentAssocsPre.size());
        
        // Delete node 3 (should affect 2 of the parent associations);
        nodeService.deleteNode(liveNodeRefs[3]);
        
        // Check
        List<ChildAssociationRef> parentAssocsPost = nodeService.getParentAssocs(secondaryNodeRef);
        assertEquals("Incorrect number of parent assocs", 1, parentAssocsPost.size());
    }
    
    /**
     * Checks that file renames are handled when getting children
     */
    public void testCaches_RenameNode()
    {
        final NodeRef[] nodeRefs = new NodeRef[2];
        final NodeRef workspaceRootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        buildNodeHierarchy(workspaceRootNodeRef, nodeRefs);
        
        // What is the name of the first child?
        String name = (String) nodeService.getProperty(nodeRefs[1], ContentModel.PROP_NAME);
        // Now query for it
        NodeRef nodeRefCheck = nodeService.getChildByName(nodeRefs[0], ContentModel.ASSOC_CONTAINS, name);
        assertNotNull("Did not find node by name", nodeRefCheck);
        assertEquals("Node found was not correct", nodeRefs[1], nodeRefCheck);
        
        // Rename the node
        nodeService.setProperty(nodeRefs[1], ContentModel.PROP_NAME, "New Name");
        // Should find nothing
        nodeRefCheck = nodeService.getChildByName(nodeRefs[0], ContentModel.ASSOC_CONTAINS, name);
        assertNull("Should not have found anything", nodeRefCheck);
        
        // Add another child with the same original name
        NodeRef newChildNodeRef = nodeService.createNode(
                nodeRefs[0],
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NAMESPACE, name),
                ContentModel.TYPE_FOLDER,
                Collections.singletonMap(ContentModel.PROP_NAME, (Serializable)name)).getChildRef();
        // We should find this new node when looking for the name
        nodeRefCheck = nodeService.getChildByName(nodeRefs[0], ContentModel.ASSOC_CONTAINS, name);
        assertNotNull("Did not find node by name", nodeRefCheck);
        assertEquals("Node found was not correct", newChildNodeRef, nodeRefCheck);
    }
    
    /**
     * Looks for a key that contains the toString() of the value
     */
    private Object findCacheValue(SimpleCache<Serializable, Serializable> cache, Serializable key)
    {
        Collection<Serializable> keys = cache.getKeys();
        for (Serializable keyInCache : keys)
        {
            String keyInCacheStr = keyInCache.toString();
            String keyStr = key.toString();
            if (keyInCacheStr.endsWith(keyStr))
            {
                Object value = cache.get(keyInCache);
                return value;
            }
        }
        return null;
    }
    
    private static final QName PROP_RESIDUAL = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate());
    /**
     * Check that simple node property modifications advance the node caches correctly
     */
    @SuppressWarnings("unchecked")
    public void testCaches_ImmutableNodeCaches() throws Exception
    {
        final NodeRef[] nodeRefs = new NodeRef[2];
        final NodeRef workspaceRootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        buildNodeHierarchy(workspaceRootNodeRef, nodeRefs);
        final NodeRef nodeRef = nodeRefs[1];

        // Get the current node cache key
        Long nodeId = (Long) findCacheValue(nodesCache, nodeRef);
        assertNotNull("Node not found in cache", nodeId);
        Node nodeOne = (Node) findCacheValue(nodesCache, nodeId);
        assertNotNull("Node not found in cache", nodeOne);
        NodeVersionKey nodeKeyOne = nodeOne.getNodeVersionKey();
        
        // Get the node cached values
        Map<QName, Serializable> nodePropsOne = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeyOne);
        Set<QName> nodeAspectsOne = (Set<QName>) findCacheValue(aspectsCache, nodeKeyOne);
        ParentAssocsInfo nodeParentAssocsOne = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeyOne);
        
        // Check the values
        assertEquals("The node version is incorrect", Long.valueOf(1L), nodeKeyOne.getVersion());
        assertNotNull("No cache entry for properties", nodePropsOne);
        assertNotNull("No cache entry for aspects", nodeAspectsOne);
        assertNotNull("No cache entry for parent assocs", nodeParentAssocsOne);
        assertEquals("Property count incorrect", 1, nodePropsOne.size());
        assertNotNull("Expected a cm:name property", nodePropsOne.get(ContentModel.PROP_NAME));
        assertEquals("Aspect count incorrect", 1, nodeAspectsOne.size());
        assertTrue("Expected a cm:auditable aspect", nodeAspectsOne.contains(ContentModel.ASPECT_AUDITABLE));
        assertEquals("Parent assoc count incorrect", 1, nodeParentAssocsOne.getParentAssocs().size());
        
        // Add a property
        nodeService.setProperty(nodeRef, PROP_RESIDUAL, GUID.generate());
        
        // Get the values for the previous version
        Map<QName, Serializable> nodePropsOneCheck = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeyOne);
        Set<QName> nodeAspectsOneCheck = (Set<QName>) findCacheValue(aspectsCache, nodeKeyOne);
        ParentAssocsInfo nodeParentAssocsOneCheck = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeyOne);
        assertTrue("Previous cache entries must be left alone", nodePropsOneCheck == nodePropsOne);
        assertTrue("Previous cache entries must be left alone", nodeAspectsOneCheck == nodeAspectsOne);
        assertTrue("Previous cache entries must be left alone", nodeParentAssocsOneCheck == nodeParentAssocsOne);

        // Get the current node cache key
        Node nodeTwo = (Node) findCacheValue(nodesCache, nodeId);
        assertNotNull("Node not found in cache", nodeTwo);
        NodeVersionKey nodeKeyTwo = nodeTwo.getNodeVersionKey();
        
        // Get the node cached values
        Map<QName, Serializable> nodePropsTwo = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeyTwo);
        Set<QName> nodeAspectsTwo = (Set<QName>) findCacheValue(aspectsCache, nodeKeyTwo);
        ParentAssocsInfo nodeParentAssocsTwo = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeyTwo);

        // Check the values
        assertEquals("The node version is incorrect", Long.valueOf(2L), nodeKeyTwo.getVersion());
        assertNotNull("No cache entry for properties", nodePropsTwo);
        assertNotNull("No cache entry for aspects", nodeAspectsTwo);
        assertNotNull("No cache entry for parent assocs", nodeParentAssocsTwo);
        assertTrue("Properties must have moved on", nodePropsTwo != nodePropsOne);
        assertEquals("Property count incorrect", 2, nodePropsTwo.size());
        assertNotNull("Expected a cm:name property", nodePropsTwo.get(ContentModel.PROP_NAME));
        assertNotNull("Expected a residual property", nodePropsTwo.get(PROP_RESIDUAL));
        assertTrue("Aspects must be carried", nodeAspectsTwo == nodeAspectsOne);
        assertTrue("Parent assocs must be carried", nodeParentAssocsTwo == nodeParentAssocsOne);
        
        // Remove a property
        nodeService.removeProperty(nodeRef, PROP_RESIDUAL);
        
        // Get the values for the previous version
        Map<QName, Serializable> nodePropsTwoCheck = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeyTwo);
        Set<QName> nodeAspectsTwoCheck = (Set<QName>) findCacheValue(aspectsCache, nodeKeyTwo);
        ParentAssocsInfo nodeParentAssocsTwoCheck = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeyTwo);
        assertTrue("Previous cache entries must be left alone", nodePropsTwoCheck == nodePropsTwo);
        assertTrue("Previous cache entries must be left alone", nodeAspectsTwoCheck == nodeAspectsTwo);
        assertTrue("Previous cache entries must be left alone", nodeParentAssocsTwoCheck == nodeParentAssocsTwo);

        // Get the current node cache key
        Node nodeThree = (Node) findCacheValue(nodesCache, nodeId);
        assertNotNull("Node not found in cache", nodeThree);
        NodeVersionKey nodeKeyThree = nodeThree.getNodeVersionKey();
        
        // Get the node cached values
        Map<QName, Serializable> nodePropsThree = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeyThree);
        Set<QName> nodeAspectsThree = (Set<QName>) findCacheValue(aspectsCache, nodeKeyThree);
        ParentAssocsInfo nodeParentAssocsThree = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeyThree);

        // Check the values
        assertEquals("The node version is incorrect", Long.valueOf(3L), nodeKeyThree.getVersion());
        assertNotNull("No cache entry for properties", nodePropsThree);
        assertNotNull("No cache entry for aspects", nodeAspectsThree);
        assertNotNull("No cache entry for parent assocs", nodeParentAssocsThree);
        assertTrue("Properties must have moved on", nodePropsThree != nodePropsTwo);
        assertEquals("Property count incorrect", 1, nodePropsThree.size());
        assertNotNull("Expected a cm:name property", nodePropsThree.get(ContentModel.PROP_NAME));
        assertNull("Expected no residual property", nodePropsThree.get(PROP_RESIDUAL));
        assertTrue("Aspects must be carried", nodeAspectsThree == nodeAspectsTwo);
        assertTrue("Parent assocs must be carried", nodeParentAssocsThree == nodeParentAssocsTwo);
        
        // Add an aspect
        nodeService.addAspect(nodeRef, ContentModel.ASPECT_TITLED, null);
        
        // Get the values for the previous version
        Map<QName, Serializable> nodePropsThreeCheck = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeyThree);
        Set<QName> nodeAspectsThreeCheck = (Set<QName>) findCacheValue(aspectsCache, nodeKeyThree);
        ParentAssocsInfo nodeParentAssocsThreeCheck = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeyThree);
        assertTrue("Previous cache entries must be left alone", nodePropsThreeCheck == nodePropsThree);
        assertTrue("Previous cache entries must be left alone", nodeAspectsThreeCheck == nodeAspectsThree);
        assertTrue("Previous cache entries must be left alone", nodeParentAssocsThreeCheck == nodeParentAssocsThree);

        // Get the current node cache key
        Node nodeFour = (Node) findCacheValue(nodesCache, nodeId);
        assertNotNull("Node not found in cache", nodeFour);
        NodeVersionKey nodeKeyFour = nodeFour.getNodeVersionKey();
        
        // Get the node cached values
        Map<QName, Serializable> nodePropsFour = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeyFour);
        Set<QName> nodeAspectsFour = (Set<QName>) findCacheValue(aspectsCache, nodeKeyFour);
        ParentAssocsInfo nodeParentAssocsFour = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeyFour);

        // Check the values
        assertEquals("The node version is incorrect", Long.valueOf(4L), nodeKeyFour.getVersion());
        assertNotNull("No cache entry for properties", nodePropsFour);
        assertNotNull("No cache entry for aspects", nodeAspectsFour);
        assertNotNull("No cache entry for parent assocs", nodeParentAssocsFour);
        assertTrue("Properties must be carried", nodePropsFour == nodePropsThree);
        assertTrue("Aspects must have moved on", nodeAspectsFour != nodeAspectsThree);
        assertTrue("Expected cm:titled aspect", nodeAspectsFour.contains(ContentModel.ASPECT_TITLED));
        assertTrue("Parent assocs must be carried", nodeParentAssocsFour == nodeParentAssocsThree);
        
        // Remove an aspect
        nodeService.removeAspect(nodeRef, ContentModel.ASPECT_TITLED);
        
        // Get the values for the previous version
        Map<QName, Serializable> nodePropsFourCheck = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeyFour);
        Set<QName> nodeAspectsFourCheck = (Set<QName>) findCacheValue(aspectsCache, nodeKeyFour);
        ParentAssocsInfo nodeParentAssocsFourCheck = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeyFour);
        assertTrue("Previous cache entries must be left alone", nodePropsFourCheck == nodePropsFour);
        assertTrue("Previous cache entries must be left alone", nodeAspectsFourCheck == nodeAspectsFour);
        assertTrue("Previous cache entries must be left alone", nodeParentAssocsFourCheck == nodeParentAssocsFour);

        // Get the current node cache key
        Node nodeFive = (Node) findCacheValue(nodesCache, nodeId);
        assertNotNull("Node not found in cache", nodeFive);
        NodeVersionKey nodeKeyFive = nodeFive.getNodeVersionKey();
        
        // Get the node cached values
        Map<QName, Serializable> nodePropsFive = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeyFive);
        Set<QName> nodeAspectsFive = (Set<QName>) findCacheValue(aspectsCache, nodeKeyFive);
        ParentAssocsInfo nodeParentAssocsFive = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeyFive);

        // Check the values
        assertEquals("The node version is incorrect", Long.valueOf(5L), nodeKeyFive.getVersion());
        assertNotNull("No cache entry for properties", nodePropsFive);
        assertNotNull("No cache entry for aspects", nodeAspectsFive);
        assertNotNull("No cache entry for parent assocs", nodeParentAssocsFive);
        assertTrue("Properties must be carried", nodePropsFive == nodePropsFour);
        assertTrue("Aspects must have moved on", nodeAspectsFive != nodeAspectsFour);
        assertFalse("Expected no cm:titled aspect ", nodeAspectsFive.contains(ContentModel.ASPECT_TITLED));
        assertTrue("Parent assocs must be carried", nodeParentAssocsFive == nodeParentAssocsFour);
        
        // Add an aspect, some properties and secondary association
        RetryingTransactionCallback<Void> nodeSixWork = new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                Map<QName, Serializable> props = new HashMap<QName, Serializable>();
                props.put(ContentModel.PROP_TITLE, "some title");
                nodeService.addAspect(nodeRef, ContentModel.ASPECT_TITLED, props);
                nodeService.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, "Some description");
                nodeService.addChild(
                        Collections.singletonList(workspaceRootNodeRef),
                        nodeRef,
                        ContentModel.ASSOC_CHILDREN,
                        QName.createQName(TEST_PREFIX, "secondary"));
                return null;
            }
        };
        txnService.getRetryingTransactionHelper().doInTransaction(nodeSixWork);
        
        // Get the values for the previous version
        Map<QName, Serializable> nodePropsFiveCheck = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeyFive);
        Set<QName> nodeAspectsFiveCheck = (Set<QName>) findCacheValue(aspectsCache, nodeKeyFive);
        ParentAssocsInfo nodeParentAssocsFiveCheck = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeyFive);
        assertTrue("Previous cache entries must be left alone", nodePropsFiveCheck == nodePropsFive);
        assertTrue("Previous cache entries must be left alone", nodeAspectsFiveCheck == nodeAspectsFive);
        assertTrue("Previous cache entries must be left alone", nodeParentAssocsFiveCheck == nodeParentAssocsFive);

        // Get the current node cache key
        Node nodeSix = (Node) findCacheValue(nodesCache, nodeId);
        assertNotNull("Node not found in cache", nodeSix);
        NodeVersionKey nodeKeySix = nodeSix.getNodeVersionKey();
        
        // Get the node cached values
        Map<QName, Serializable> nodePropsSix = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeySix);
        Set<QName> nodeAspectsSix = (Set<QName>) findCacheValue(aspectsCache, nodeKeySix);
        ParentAssocsInfo nodeParentAssocsSix = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeySix);

        // Check the values
        assertEquals("The node version is incorrect", Long.valueOf(6L), nodeKeySix.getVersion());
        assertNotNull("No cache entry for properties", nodePropsSix);
        assertNotNull("No cache entry for aspects", nodeAspectsSix);
        assertNotNull("No cache entry for parent assocs", nodeParentAssocsSix);
        assertTrue("Properties must have moved on", nodePropsSix != nodePropsFive);
        assertEquals("Property count incorrect", 3, nodePropsSix.size());
        assertNotNull("Expected a cm:name property", nodePropsSix.get(ContentModel.PROP_NAME));
        assertNotNull("Expected a cm:title property", nodePropsSix.get(ContentModel.PROP_TITLE));
        assertNotNull("Expected a cm:description property", nodePropsSix.get(ContentModel.PROP_DESCRIPTION));
        assertTrue("Aspects must have moved on", nodeAspectsSix != nodeAspectsFive);
        assertTrue("Expected cm:titled aspect ", nodeAspectsSix.contains(ContentModel.ASPECT_TITLED));
        assertTrue("Parent assocs must have moved on", nodeParentAssocsSix != nodeParentAssocsFive);
        assertEquals("Incorrect number of parent assocs", 2, nodeParentAssocsSix.getParentAssocs().size());
        
        // Remove an aspect, some properties and a secondary association
        RetryingTransactionCallback<Void> nodeSevenWork = new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                nodeService.removeAspect(nodeRef, ContentModel.ASPECT_TITLED);
                nodeService.removeChild(workspaceRootNodeRef, nodeRef);
                return null;
            }
        };
        txnService.getRetryingTransactionHelper().doInTransaction(nodeSevenWork);
        
        // Get the values for the previous version
        Map<QName, Serializable> nodePropsSixCheck = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeySix);
        Set<QName> nodeAspectsSixCheck = (Set<QName>) findCacheValue(aspectsCache, nodeKeySix);
        ParentAssocsInfo nodeParentAssocsSixCheck = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeySix);
        assertTrue("Previous cache entries must be left alone", nodePropsSixCheck == nodePropsSix);
        assertTrue("Previous cache entries must be left alone", nodeAspectsSixCheck == nodeAspectsSix);
        assertTrue("Previous cache entries must be left alone", nodeParentAssocsSixCheck == nodeParentAssocsSix);

        // Get the current node cache key
        Node nodeSeven = (Node) findCacheValue(nodesCache, nodeId);
        assertNotNull("Node not found in cache", nodeSeven);
        NodeVersionKey nodeKeySeven = nodeSeven.getNodeVersionKey();
        
        // Get the node cached values
        Map<QName, Serializable> nodePropsSeven = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeySeven);
        Set<QName> nodeAspectsSeven = (Set<QName>) findCacheValue(aspectsCache, nodeKeySeven);
        ParentAssocsInfo nodeParentAssocsSeven = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeySeven);

        // Check the values
        assertEquals("The node version is incorrect", Long.valueOf(7L), nodeKeySeven.getVersion());
        assertNotNull("No cache entry for properties", nodePropsSeven);
        assertNotNull("No cache entry for aspects", nodeAspectsSeven);
        assertNotNull("No cache entry for parent assocs", nodeParentAssocsSeven);
        assertTrue("Properties must have moved on", nodePropsSeven != nodePropsSix);
        assertEquals("Property count incorrect", 1, nodePropsSeven.size());
        assertNotNull("Expected a cm:name property", nodePropsSeven.get(ContentModel.PROP_NAME));
        assertTrue("Aspects must have moved on", nodeAspectsSeven != nodeAspectsSix);
        assertFalse("Expected no cm:titled aspect ", nodeAspectsSeven.contains(ContentModel.ASPECT_TITLED));
        assertTrue("Parent assocs must have moved on", nodeParentAssocsSeven != nodeParentAssocsSix);
        assertEquals("Incorrect number of parent assocs", 1, nodeParentAssocsSeven.getParentAssocs().size());
        
        // Modify cm:auditable
        RetryingTransactionCallback<Void> nodeEightWork = new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                BehaviourFilter behaviourFilter = (BehaviourFilter) ctx.getBean("policyBehaviourFilter");
                // Disable behaviour for txn
                behaviourFilter.disableBehaviour(nodeRef, ContentModel.ASPECT_AUDITABLE);
                nodeService.setProperty(nodeRef, ContentModel.PROP_MODIFIER, "Fred");
                return null;
            }
        };
        txnService.getRetryingTransactionHelper().doInTransaction(nodeEightWork);
        
        // Get the values for the previous version
        Map<QName, Serializable> nodePropsSevenCheck = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeySeven);
        Set<QName> nodeAspectsSevenCheck = (Set<QName>) findCacheValue(aspectsCache, nodeKeySeven);
        ParentAssocsInfo nodeParentAssocsSevenCheck = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeySeven);
        assertTrue("Previous cache entries must be left alone", nodePropsSevenCheck == nodePropsSeven);
        assertTrue("Previous cache entries must be left alone", nodeAspectsSevenCheck == nodeAspectsSeven);
        assertTrue("Previous cache entries must be left alone", nodeParentAssocsSevenCheck == nodeParentAssocsSeven);

        // Get the current node cache key
        Node nodeEight = (Node) findCacheValue(nodesCache, nodeId);
        assertNotNull("Node not found in cache", nodeEight);
        NodeVersionKey nodeKeyEight = nodeEight.getNodeVersionKey();
        
        // Get the node cached values
        Map<QName, Serializable> nodePropsEight = (Map<QName, Serializable>) findCacheValue(propsCache, nodeKeyEight);
        Set<QName> nodeAspectsEight = (Set<QName>) findCacheValue(aspectsCache, nodeKeyEight);
        ParentAssocsInfo nodeParentAssocsEight = (ParentAssocsInfo) findCacheValue(parentAssocsCache, nodeKeyEight);

        // Check the values
        assertEquals("The node version is incorrect", Long.valueOf(8L), nodeKeyEight.getVersion());
        assertNotNull("No cache entry for properties", nodePropsEight);
        assertNotNull("No cache entry for aspects", nodeAspectsEight);
        assertNotNull("No cache entry for parent assocs", nodeParentAssocsEight);
        assertEquals("Expected change to cm:modifier", "Fred", nodeEight.getAuditableProperties().getAuditModifier());
        assertTrue("Properties must be carried", nodePropsEight == nodePropsSeven);
        assertTrue("Aspects be carried", nodeAspectsEight == nodeAspectsSeven);
        assertTrue("Parent assocs must be carried", nodeParentAssocsEight == nodeParentAssocsSeven);
    }
    
    public void testCreateNodePolicies()
    {
        // Create and bind the mock behaviours...
        OnCreateNodePolicy onCreateNodePolicy = createClassPolicy(
                    OnCreateNodePolicy.class,
                    OnCreateNodePolicy.QNAME,
                    ContentModel.TYPE_CONTENT);
        
        BeforeCreateNodePolicy beforeCreateNodePolicy = createClassPolicy(
                    BeforeCreateNodePolicy.class,
                    BeforeCreateNodePolicy.QNAME,
                    ContentModel.TYPE_CONTENT); 
        
        OnCreateChildAssociationPolicy onCreateChildAssociationPolicy = createAssocPolicy(
                        OnCreateChildAssociationPolicy.class,
                        OnCreateChildAssociationPolicy.QNAME,
                        ContentModel.TYPE_STOREROOT);
        
        OnUpdatePropertiesPolicy onUpdatePropertiesPolicy = createClassPolicy(
                        OnUpdatePropertiesPolicy.class,
                        OnUpdatePropertiesPolicy.QNAME,
                        ContentModel.TYPE_CONTENT);
        
        // Create a node - this should result in the behaviours firing.
        NodeRef newNodeRef = nodeService.createNode(
                    this.rootNodeRef, 
                    ContentModel.ASSOC_CHILDREN, 
                    ContentModel.ASSOC_CHILDREN, 
                    ContentModel.TYPE_CONTENT, 
                    PropertyMap.EMPTY_MAP).getChildRef();
        
        Map<QName, Serializable> propsAfter = nodeService.getProperties(newNodeRef);
        ChildAssociationRef childAssocRef = nodeService.getPrimaryParent(newNodeRef);
        
        // Check the behaviours fired as expected...
        verify(beforeCreateNodePolicy).beforeCreateNode(
                    rootNodeRef,
                    ContentModel.ASSOC_CHILDREN,
                    ContentModel.ASSOC_CHILDREN,
                    ContentModel.TYPE_CONTENT);
        verify(onCreateNodePolicy).onCreateNode(childAssocRef);
        verify(onCreateChildAssociationPolicy).onCreateChildAssociation(childAssocRef, true);
        verify(onUpdatePropertiesPolicy).onUpdateProperties(newNodeRef, PropertyMap.EMPTY_MAP, propsAfter);
    }
    
    public void testSetNodeTypePolicies()
    {   
        // Create a node (before behaviours are attached)
        NodeRef nodeRef = nodeService.createNode(
                    this.rootNodeRef, 
                    ContentModel.ASSOC_CHILDREN, 
                    ContentModel.ASSOC_CHILDREN, 
                    ContentModel.TYPE_CONTENT, 
                    new HashMap<QName, Serializable>(0)).getChildRef();
        
        // Create and bind the mock behaviours...
        BeforeUpdateNodePolicy beforeUpdatePolicy = createClassPolicy(
                    BeforeUpdateNodePolicy.class,
                    BeforeUpdateNodePolicy.QNAME,
                    ContentModel.TYPE_CONTENT);
        
        OnUpdateNodePolicy onUpdatePolicy = createClassPolicy(
                    OnUpdateNodePolicy.class,
                    OnUpdateNodePolicy.QNAME,
                    ContentModel.TYPE_FOLDER);
        
        BeforeSetNodeTypePolicy beforeSetNodeTypePolicy = createClassPolicy(
                    BeforeSetNodeTypePolicy.class,
                    BeforeSetNodeTypePolicy.QNAME,
                    ContentModel.TYPE_CONTENT);
        
        OnSetNodeTypePolicy onSetNodeTypePolicy = createClassPolicy(
                    OnSetNodeTypePolicy.class,
                    OnSetNodeTypePolicy.QNAME,
                    ContentModel.TYPE_FOLDER);
             
        // Set the type of the new node - this should trigger the correct behaviours.
        nodeService.setType(nodeRef, ContentModel.TYPE_FOLDER);
        
        // Check the behaviours fired as expected...
        verify(beforeUpdatePolicy).beforeUpdateNode(nodeRef);
        verify(onUpdatePolicy).onUpdateNode(nodeRef);
        verify(beforeSetNodeTypePolicy).beforeSetNodeType(nodeRef, ContentModel.TYPE_CONTENT, ContentModel.TYPE_FOLDER);
        verify(onSetNodeTypePolicy).onSetNodeType(nodeRef, ContentModel.TYPE_CONTENT, ContentModel.TYPE_FOLDER);
    }
    
    private <T extends Policy> T createClassPolicy(Class<T> policyInterface, QName policyQName, QName triggerOnClass)
    {
        T policy = mock(policyInterface);
        policyComponent.bindClassBehaviour(
                    policyQName, 
                    triggerOnClass, 
                    new JavaBehaviour(policy, policyQName.getLocalName()));
        return policy;
    }
    

    private <T extends Policy> T createAssocPolicy(Class<T> policyInterface, QName policyQName, QName triggerOnClass)
    {
        T policy = mock(policyInterface);
        policyComponent.bindAssociationBehaviour(
                    policyQName, 
                    triggerOnClass, 
                    new JavaBehaviour(policy, policyQName.getLocalName()));
        return policy;
    }
    
    /**
     * Ensure that nodes cannot be linked to deleted nodes.
     * <p/>
     * Conditions that <i>might</i> cause this are:<br/>
     * <ul>
     *   <li>Node created within a parent node that is being deleted</li>
     *   <li>The node cache is temporarily incorrect when the association is made</li>
     * </ul>
     * <p/>
     * <a href="https://issues.alfresco.com/jira/browse/ALF-12358">Concurrency: Possible to create association references to deleted nodes</a>
     */
    public void testConcurrentLinkToDeletedNode() throws Throwable
    {
        QNameDAO qnameDAO = (QNameDAO) ctx.getBean("qnameDAO");
        Long deletedTypeQNameId = qnameDAO.getOrCreateQName(ContentModel.TYPE_DELETED).getFirst();
        // First find any broken links to start with
        final NodeEntity params = new NodeEntity();
        params.setId(0L);
        params.setTypeQNameId(deletedTypeQNameId);
        
        // Find all 'at risk' nodes before the test
        final List<Long> attachedToDeletedIdsBefore = getChildNodesWithDeletedParentNode(params, 0);
        logger.debug("Found child nodes with deleted parent node (before): " + attachedToDeletedIdsBefore);
        final List<Long> orphanedNodeIdsBefore = getChildNodesWithNoParentNode(params, 0);
        logger.debug("Found child nodes without parent (before): " + orphanedNodeIdsBefore);
        
        final NodeRef[] nodeRefs = new NodeRef[10];
        final NodeRef workspaceRootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        buildNodeHierarchy(workspaceRootNodeRef, nodeRefs);
        
        // Fire off a bunch of threads that create random nodes within the hierarchy created above
        final RetryingTransactionCallback<NodeRef> createChildCallback = new RetryingTransactionCallback<NodeRef>()
        {
            @Override
            public NodeRef execute() throws Throwable
            {
                String randomName = getName() + "-" + GUID.generate();
                QName randomQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, randomName);
                Map<QName, Serializable> props = new HashMap<QName, Serializable>();
                props.put(ContentModel.PROP_NAME, randomName);
                // Choose a random parent node from the hierarchy
                int random = new Random().nextInt(10);
                return nodeService.createNode(
                        nodeRefs[random],
                        ContentModel.ASSOC_CONTAINS,
                        randomQName,
                        ContentModel.TYPE_CONTAINER,
                        props).getChildRef();
            }
        };
        final Runnable[] runnables = new Runnable[20];
        final List<NodeRef> nodesAtRisk = Collections.synchronizedList(new ArrayList<NodeRef>(100));
        
        final List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < runnables.length; i++)
        {
            runnables[i] = new Runnable()
            {
                @Override
                public synchronized void run()
                {
                    AuthenticationUtil.setRunAsUserSystem();
                    try
                    {
                        wait(1000L);     // A short wait before we kick off (should be notified)
                        for (int i = 0; i < 200; i++)
                        {
                            NodeRef nodeRef = txnService.getRetryingTransactionHelper().doInTransaction(createChildCallback);
                            // Store the node for later checks
                            nodesAtRisk.add(nodeRef);
                            // Wait to give other threads a chance
                            wait(1L);
                        }
                    }
                    catch (Throwable e)
                    {
                        // This is expected i.e. we'll just keep doing it until failure
                        logger.debug("Got exception adding child node: ", e);
                    }
                }
            };
            Thread thread = new Thread(runnables[i]);
            threads.add(thread);
            thread.start();
        }
        
        final RetryingTransactionCallback<NodeRef> deleteWithNestedCallback = new RetryingTransactionCallback<NodeRef>()
        {
            @Override
            public NodeRef execute() throws Throwable
            {
                // Notify the threads to kick off
                for (int i = 0; i < runnables.length; i++)
                {
                    // Notify the threads to stop waiting
                    synchronized(runnables[i])
                    {
                        runnables[i].notify();
                    }
                    // Short wait to give thread a chance to run
                    synchronized(this) { try { wait(10L); } catch (Throwable e) {} };
                }
                // Delete the parent node
                nodeService.deleteNode(nodeRefs[0]);
                return null;
            }
        };
        txnService.getRetryingTransactionHelper().doInTransaction(deleteWithNestedCallback);
        
        // Wait for the threads to finish
        for (Thread t : threads)
        {
            t.join();
        }
        
        logger.info("All threads should have finished");
        
        // Find all 'at risk' nodes after the test
        final List<Long> attachedToDeletedIdsAfter = getChildNodesWithDeletedParentNode(params, attachedToDeletedIdsBefore.size());
        logger.debug("Found child nodes with deleted parent node (after): " + attachedToDeletedIdsAfter);
        final List<Long> orphanedNodeIdsAfter = getChildNodesWithNoParentNode(params, orphanedNodeIdsBefore.size());
        logger.debug("Found child nodes without parent (after): " + attachedToDeletedIdsAfter);
        // Now need to identify the problem nodes

        if (attachedToDeletedIdsAfter.isEmpty() && orphanedNodeIdsAfter.isEmpty())
        {
            // nothing more to test
            return;
        }
        
        // We are already in a failed state, but check if the orphan cleanup works
        
        // workaround recovery: force collection of any orphan nodes (ALF-12358 + ALF-13066)
        for (final NodeRef nodeRef : nodesAtRisk)
        {
            txnService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    if (nodeService.exists(nodeRef))
                    {
                        nodeService.getPath(nodeRef); // ignore return
                    }
                    return null;
                }
            });
        }
        
        // Find all 'at risk' nodes after the test
        final List<Long> attachedToDeletedIdsCleaned = getChildNodesWithDeletedParentNode(params, attachedToDeletedIdsBefore.size());
        logger.debug("Found child nodes with deleted parent node (cleaned): " + attachedToDeletedIdsAfter);
        final List<Long> orphanedNodeIdsCleaned = getChildNodesWithNoParentNode(params, orphanedNodeIdsBefore.size());
        logger.debug("Found child nodes without parent (cleaned): " + attachedToDeletedIdsAfter);
        
        // Check
        assertTrue(
                "Expected full cleanup of nodes referencing deleted nodes: " + attachedToDeletedIdsCleaned,
                attachedToDeletedIdsCleaned.isEmpty());
        assertTrue(
                "Expected full cleanup of nodes referencing without parents: " + orphanedNodeIdsCleaned,
                orphanedNodeIdsCleaned.isEmpty());
        
        // check lost_found ...
        List<NodeRef> lostAndFoundNodeRefs = getLostAndFoundNodes();
        assertFalse(lostAndFoundNodeRefs.isEmpty());
        
        Set<Long> lostAndFoundNodeIds = new HashSet<Long>(lostAndFoundNodeRefs.size());
        for (NodeRef nodeRef : lostAndFoundNodeRefs)
        {
            lostAndFoundNodeIds.add((Long)nodeService.getProperty(nodeRef, ContentModel.PROP_NODE_DBID));
        }
        
        assertTrue("Nodes linked to deleted parent nodes not handled.", lostAndFoundNodeIds.containsAll(attachedToDeletedIdsAfter));
        assertTrue("Orphaned nodes not all handled.", lostAndFoundNodeIds.containsAll(orphanedNodeIdsAfter));
        
        // Now fail because we allowed the situation in the first place
        fail("We allowed orphaned nodes or nodes with deleted parents.");
    }
    
    /**
     * Pending repeatable test - force issue ALF-ALF-13066 (non-root node with no parent)
     */
    public void testForceNonRootNodeWithNoParentNode() throws Throwable
    {
        QNameDAO qnameDAO = (QNameDAO) ctx.getBean("qnameDAO");
        Long deletedTypeQNameId = qnameDAO.getOrCreateQName(ContentModel.TYPE_DELETED).getFirst();
        // First find any broken links to start with
        final NodeEntity params = new NodeEntity();
        params.setId(0L);
        params.setTypeQNameId(deletedTypeQNameId);
        
        List<Long> ids = getChildNodesWithNoParentNode(params, 0);
        logger.debug("Found child nodes with deleted parent node (before): " + ids);
        
        final int idsToSkip = ids.size();
        
        final NodeRef[] nodeRefs = new NodeRef[10];
        final NodeRef workspaceRootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        buildNodeHierarchy(workspaceRootNodeRef, nodeRefs);
        
        int cnt = 5;
        List<NodeRef> childNodeRefs = new ArrayList<NodeRef>(cnt);
        
        final NodeDAO nodeDAO = (NodeDAO)ctx.getBean("nodeDAO");
        
        for (int i = 0; i < cnt; i++)
        {
            // create some pseudo- thumnails
            String randomName = getName() + "-" + System.nanoTime();
            QName randomQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, randomName);
            Map<QName, Serializable> props = new HashMap<QName, Serializable>();
            props.put(ContentModel.PROP_NAME, randomName);
            
            // Choose a random parent node from the hierarchy
            int random = new Random().nextInt(10);
            NodeRef parentNodeRef = nodeRefs[random];
            
            NodeRef childNodeRef = nodeService.createNode(
                    parentNodeRef,
                    ContentModel.ASSOC_CONTAINS,
                    randomQName,
                    ContentModel.TYPE_THUMBNAIL,
                    props).getChildRef();
            
            childNodeRefs.add(childNodeRef);
            
            // forcefully remove the primary parent assoc
            final Long childNodeId = (Long)nodeService.getProperty(childNodeRef, ContentModel.PROP_NODE_DBID);
            txnService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    Pair<Long, ChildAssociationRef> assocPair = nodeDAO.getPrimaryParentAssoc(childNodeId);
                    nodeDAO.deleteChildAssoc(assocPair.getFirst());
                    return null;
                }
            });
        }
        
        // Now need to identify the problem nodes
        final List<Long> childNodeIds = getChildNodesWithNoParentNode(params, idsToSkip);
        assertFalse(childNodeIds.isEmpty());
        logger.debug("Found child nodes with deleted parent node (after): " + childNodeIds);
        
        // workaround recovery: force collection of any orphan nodes (ALF-12358 + ALF-13066)
        for (final NodeRef nodeRef : childNodeRefs)
        {
            txnService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    if (nodeService.exists(nodeRef))
                    {
                        nodeService.getPath(nodeRef); // ignore return
                    }
                    return null;
                }
            });
        }
        
        // check again ...
        ids = getChildNodesWithNoParentNode(params, idsToSkip);
        assertTrue("The following child nodes have no parent node: " + ids, ids.isEmpty());
        
        // check lost_found ...
        List<NodeRef> lostAndFoundNodeRefs = getLostAndFoundNodes();
        assertFalse(lostAndFoundNodeRefs.isEmpty());
        
        List<Long> lostAndFoundNodeIds = new ArrayList<Long>(lostAndFoundNodeRefs.size());
        for (NodeRef nodeRef : lostAndFoundNodeRefs)
        {
            lostAndFoundNodeIds.add((Long)nodeService.getProperty(nodeRef, ContentModel.PROP_NODE_DBID));
        }
        
        for (Long childNodeId : childNodeIds)
        {
            assertTrue("Not found: "+childNodeId, lostAndFoundNodeIds.contains(childNodeId));
        }
    }
    
    private List<Long> getChildNodesWithDeletedParentNode(NodeEntity params, int idsToSkip)
    {
        return cannedQueryDAO.executeQuery(
                "alfresco.query.test",
                "select_NodeServiceTest_testConcurrentLinkToDeletedNode_GetChildNodesWithDeletedParentNodeCannedQuery",
                params,
                idsToSkip,
                Integer.MAX_VALUE);
    }
    
    private List<Long> getChildNodesWithNoParentNode(NodeEntity params, int idsToSkip)
    {
        return cannedQueryDAO.executeQuery(
                "alfresco.query.test",
                "select_NodeServiceTest_testForceNonRootNodeWithNoParentNode_GetChildNodesWithNoParentNodeCannedQuery",
                params,
                idsToSkip,
                Integer.MAX_VALUE);
    }
    
    private List<NodeRef> getLostAndFoundNodes()
    {
        Set<QName> childNodeTypeQNames = new HashSet<QName>(1);
        childNodeTypeQNames.add(ContentModel.TYPE_LOST_AND_FOUND);
        
        List<ChildAssociationRef> childAssocRefs = nodeService.getChildAssocs(nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE), childNodeTypeQNames);
        
        List<NodeRef> lostNodeRefs = null;
        
        if (childAssocRefs.size() > 0)
        {
            List<ChildAssociationRef> lostNodeChildAssocRefs = nodeService.getChildAssocs(childAssocRefs.get(0).getChildRef());
            lostNodeRefs = new ArrayList<NodeRef>(lostNodeChildAssocRefs.size());
            for(ChildAssociationRef lostNodeChildAssocRef : lostNodeChildAssocRefs)
            {
                lostNodeRefs.add(lostNodeChildAssocRef.getChildRef());
            }
        }
        else
        {
            lostNodeRefs = Collections.emptyList();
        }
        
        return lostNodeRefs;
    }
    
    /**
     * @see NodeHierarchyWalker
     */
    public void testNodeHierarchyWalker() throws Exception
    {
        final NodeRef workspaceRootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        final NodeRef[] nodes = new NodeRef[6];
        buildNodeHierarchy(workspaceRootNodeRef, nodes);
        Pair<Long, NodeRef> parentNodePair = nodeDAO.getNodePair(nodes[0]);
        Pair<Long, ChildAssociationRef> parentAssocPair = nodeDAO.getPrimaryParentAssoc(parentNodePair.getFirst());
        // Hook up some associations
        nodeService.addAspect(nodes[1], ContentModel.ASPECT_COPIEDFROM, null);
        nodeService.createAssociation(nodes[1], nodes[0], ContentModel.ASSOC_ORIGINAL);             // Peer n1-n0
        nodeService.addChild(                                                                       // Secondary child n0-n2
                nodes[0],
                nodes[2],
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.ALFRESCO_URI, "testNodeHierarchyWalker"));
        
        // Walk the hierarchy
        NodeHierarchyWalker walker = new NodeHierarchyWalker(nodeDAO);
        walker.walkHierarchy(parentNodePair, parentAssocPair);
        
        List<VisitedNode> nodesLeafFirst = walker.getNodes(true);
        assertEquals("Unexpected number of nodes visited", 6, nodesLeafFirst.size());
        assertEquals("Incorrect order ", nodesLeafFirst.get(0).nodeRef, nodes[5]);
        assertEquals("Incorrect order ", nodesLeafFirst.get(5).nodeRef, nodes[0]);
        List<VisitedNode> nodesParentFirst = walker.getNodes(false);
        assertEquals("Unexpected number of nodes visited", 6, nodesParentFirst.size());
        assertEquals("Incorrect order ", nodesParentFirst.get(0).nodeRef, nodes[0]);
        assertEquals("Incorrect order ", nodesParentFirst.get(5).nodeRef, nodes[5]);
        
        // Check primary parent links
        assertEquals(workspaceRootNodeRef, nodesParentFirst.get(0).primaryParentAssocPair.getSecond().getParentRef());
        assertEquals(nodes[0], nodesParentFirst.get(1).primaryParentAssocPair.getSecond().getParentRef());
        assertEquals(nodes[4], nodesParentFirst.get(5).primaryParentAssocPair.getSecond().getParentRef());
        
        // Check secondary parent links
        assertEquals(0, nodesParentFirst.get(0).secondaryParentAssocs.size());
        assertEquals(nodes[0], nodesParentFirst.get(2).secondaryParentAssocs.get(0).getSecond().getParentRef());
        assertEquals(0, nodesParentFirst.get(1).secondaryParentAssocs.size());
        assertEquals(1, nodesParentFirst.get(2).secondaryParentAssocs.size());
        assertEquals(0, nodesParentFirst.get(3).secondaryParentAssocs.size());
        
        // Check secondary child links
        assertEquals(1, nodesParentFirst.get(0).secondaryChildAssocs.size());
        assertEquals(nodes[2], nodesParentFirst.get(0).secondaryChildAssocs.get(0).getSecond().getChildRef());
        assertEquals(0, nodesParentFirst.get(1).secondaryChildAssocs.size());
        
        // Check target assocs
        assertEquals(0, nodesParentFirst.get(0).targetAssocs.size());
        assertEquals(1, nodesParentFirst.get(1).targetAssocs.size());
        assertEquals(nodes[0], nodesParentFirst.get(1).targetAssocs.get(0).getSecond().getTargetRef());
        
        // Check source assocs
        assertEquals(1, nodesParentFirst.get(0).sourceAssocs.size());
        assertEquals(nodes[1], nodesParentFirst.get(0).sourceAssocs.get(0).getSecond().getSourceRef());
        assertEquals(0, nodesParentFirst.get(1).sourceAssocs.size());
    }
}
