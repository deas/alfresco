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
package org.alfresco.module.knowledgeBase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.GUID;

/**
 * Knowledge Base Unit Test
 * 
 * @author Roy Wetherall
 */
public class KbTest extends BaseSpringTest implements KbModel
{
    protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
	
	protected NodeService nodeService;
	protected SearchService searchService;
	protected TransactionService transactionService;
	protected ContentService contentService;
    protected AuthenticationComponent authenticationComponent;
    
    protected NodeRef kbNode;
    protected NodeRef companyHome;
	
	@Override
	protected void onSetUpInTransaction() 
		throws Exception 
	{
		super.onSetUpInTransaction();        
        
		// Get references to the relevant services
        this.authenticationComponent = (AuthenticationComponent)this.applicationContext.getBean("authenticationComponent");
		this.nodeService = (NodeService)this.applicationContext.getBean("nodeService");
		this.searchService = (SearchService)this.applicationContext.getBean("searchService");		
		this.transactionService = (TransactionService)this.applicationContext.getBean("transactionComponent");
		this.contentService = (ContentService)this.applicationContext.getBean("contentService");
		
        // Ensure tests are executed as the system user
        this.authenticationComponent.setSystemUserAsCurrentUser();
               
        // Get a reference to the company home node
		ResultSet results1 = this.searchService.query(KbTest.SPACES_STORE, SearchService.LANGUAGE_XPATH, "app:company_home");
		this.companyHome = results1.getNodeRefs().get(0);
		
        String name = "KnowledgeBaseTest" + GUID.generate();
		Map<QName, Serializable> props = new HashMap<QName, Serializable>(10);
		props.put(ContentModel.PROP_NAME, name);
		
		this.kbNode = this.nodeService.createNode(
				this.companyHome, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                QName.createQName("{http://www.alfresco.org/model/knowledgebase/1.0}knowledgeBase"), 
                props).getChildRef();
                     
	}
    
	public void testCreateArticle()
    {
        // Create a document in a folder that is not beneth a knowledge base
        NodeRef notArticle = createTestNode(this.companyHome, "testNotArticle1", ContentModel.TYPE_CONTENT);
        
        // Check the non-article
        assertNotNull(notArticle);
        assertFalse(this.nodeService.hasAspect(notArticle, ASPECT_ARTICLE));
        
        // Create article directly beneth the knowledge base
	    NodeRef article = createTestNode(this.kbNode, "testArticle1", ContentModel.TYPE_CONTENT);
        
        // Check the article
        checkArticle(article, "0001", this.kbNode);
        
        // Create some sub-folders and an article beneth them
        NodeRef folder1 = createTestNode(this.kbNode, "testFolder1", ContentModel.TYPE_FOLDER);
        NodeRef folder2 = createTestNode(folder1, "testFolder2", ContentModel.TYPE_FOLDER);
        NodeRef article2 = createTestNode(folder2, "testArticle2", ContentModel.TYPE_CONTENT);
        
        // Check the article
        checkArticle(article2, "0002", this.kbNode);
        
        // Create a document and move it into the knowledge base
        NodeRef article3 = createTestNode(this.companyHome, "testArticle3", ContentModel.TYPE_CONTENT);
        assertFalse(this.nodeService.hasAspect(article3, ASPECT_ARTICLE));
        this.nodeService.moveNode(article3, folder1, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "testArticle3"));        
        checkArticle(article3, "0003", this.kbNode);
        
        // Move an article within the knowledge base
        // TODO
        
        // Move an article out of the knowledge base
        // TODO
    }
    
    public void testUpdateStatus()
        throws Exception
    {
        // Create article directly beneth the knowledge base
        NodeRef article = createTestNode(this.kbNode, "testArticle1" + GUID.generate(), ContentModel.TYPE_CONTENT);
        
        // Set some content on the article
        ContentWriter writer = this.contentService.getWriter(article, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("This is some article content");
        
        // Check that the status is set to draft tobegin with and that no rendition is present
        List<ChildAssociationRef> children = this.nodeService.getChildAssocs(article, ASSOC_PUBLISHED, RegexQNamePattern.MATCH_ALL);
        assertEquals(0, children.size());
        
        // Change the content and make sure the rendiition has not been created
        ContentWriter writer2 = this.contentService.getWriter(article, ContentModel.PROP_CONTENT, true);
        writer2.putContent("some changed bobbins");
        List<ChildAssociationRef> childrenA = this.nodeService.getChildAssocs(article, ASSOC_PUBLISHED, RegexQNamePattern.MATCH_ALL);
        assertEquals(0, childrenA.size());
        
        // Change the status to pending and check 
        this.nodeService.setProperty(article, PROP_STATUS, STATUS_PENDING);
        List<ChildAssociationRef> children2 = this.nodeService.getChildAssocs(article, ASSOC_PUBLISHED, RegexQNamePattern.MATCH_ALL);
        assertEquals(0, children2.size());
        
        // Change the status to published and check
        this.nodeService.setProperty(article, PROP_STATUS, STATUS_PUBLISHED);
       
        setComplete();
        endTransaction();
        
        UserTransaction tx = transactionService.getUserTransaction();
        tx.begin();
        
        List<ChildAssociationRef> children3 = this.nodeService.getChildAssocs(article, ASSOC_PUBLISHED, RegexQNamePattern.MATCH_ALL);
        assertEquals(1, children3.size());
        
        // Change the content and check that the rendition is updated
        String origContentString = ((ContentData)this.nodeService.getProperty(children3.get(0).getChildRef(), ContentModel.PROP_CONTENT)).getContentUrl();
        ContentWriter writer3 = this.contentService.getWriter(article, ContentModel.PROP_CONTENT, true);
        writer3.putContent("Blar blar blar blar blar blar ... this has changed");
        List<ChildAssociationRef> children4 = this.nodeService.getChildAssocs(article, ASSOC_PUBLISHED, RegexQNamePattern.MATCH_ALL);
        assertEquals(1, children4.size());
        String contentString = ((ContentData)this.nodeService.getProperty(children4.get(0).getChildRef(), ContentModel.PROP_CONTENT)).getContentUrl();
        assertFalse((origContentString == contentString));
        
        tx.commit();
    }
    
    private NodeRef createTestNode(NodeRef parent, String name, QName type)
    {
        name = name + GUID.generate();        
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_NAME, name);
        return this.nodeService.createNode(
                parent, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                type, 
                props).getChildRef();
    }
    
    private void checkArticle(NodeRef article, String expectedId, NodeRef expectedKnowledgeBase)
    {
        Map<QName, Serializable> props = this.nodeService.getProperties(article);
        
        assertNotNull(article);
        assertTrue(this.nodeService.hasAspect(article, ASPECT_ARTICLE));
        assertEquals(expectedId, props.get(PROP_KB_ID));
        
        List<AssociationRef> assocs = this.nodeService.getTargetAssocs(article, ASSOC_KNOWLEDGE_BASE);
        assertEquals(1, assocs.size());
        NodeRef kb = assocs.get(0).getTargetRef();
        assertEquals(expectedKnowledgeBase, kb);
        
        System.out.println(props.get(PROP_STATUS).getClass().getName());
        
        //this.nodeService.setProperty(article, PROP_STATUS, STATUS_DRAFT);
        //props = this.nodeService.getProperties(article);
        //System.out.println(props.get(PROP_STATUS).getClass().getName());
        
        // Check the default values have been set correctly        
        assertEquals(STATUS_DRAFT, 
                     props.get(PROP_STATUS));
        
    }
    
}
