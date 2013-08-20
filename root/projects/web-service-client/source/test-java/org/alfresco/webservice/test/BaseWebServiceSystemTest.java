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

import junit.framework.TestCase;

import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLCreateAssociation;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for all web service system tests that need to authenticate. The
 * setUp method calls the AuthenticationService and authenticates as
 * admin/admin, the returned ticket is then stored in
 * <code>TicketHolder.ticket</code> so that all subclass implementations can
 * use it to call other services.
 * 
 * @see junit.framework.TestCase#setUp()
 * @author gavinc
 */
public abstract class BaseWebServiceSystemTest extends TestCase
{
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(BaseWebServiceSystemTest.class);

    protected static final String USERNAME = "admin";
    protected static final String PASSWORD = "admin";
    
    public static final String FOLDER_NAME = "test folder";
    protected static final String CONTENT_NAME = "test content";
    protected static final String CONTENT_NAME_2 = "test content 2";
    
    protected static final String TEST_CONTENT = "This is some test content.  This is some test content.";
    
    protected static Store store;
    protected static Reference rootReference;    
    protected static Reference contentReference;
    protected static Reference contentReference2;
    protected static Reference folderReference;
    
    protected RepositoryServiceSoapBindingStub repositoryService;
    protected ContentServiceSoapBindingStub contentService;
    
    public BaseWebServiceSystemTest()
    {
        this.repositoryService = WebServiceFactory.getRepositoryService();
        this.contentService = WebServiceFactory.getContentService();
    }
    
    /**
     * Calls the AuthenticationService to retrieve a ticket for all tests to
     * use.
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();        
        
        // Start a new session
        AuthenticationUtils.startSession(USERNAME, PASSWORD);
        
        // Create the store
        if (BaseWebServiceSystemTest.store == null)
        {
            // Create the store
            BaseWebServiceSystemTest.store = WebServiceFactory.getRepositoryService().createStore(Constants.WORKSPACE_STORE, "Test" + System.currentTimeMillis());
            
            // Get the root node reference
            Predicate predicate = new Predicate(null, BaseWebServiceSystemTest.store, null);
            Node[] nodes = WebServiceFactory.getRepositoryService().get(predicate);
            if (nodes.length == 1)
            {
                BaseWebServiceSystemTest.rootReference = nodes[0].getReference();
            }
            else
            {
                throw new Exception("Unable to get the root not of the created sotre.");
            }

            // Create test content
            ParentReference contentParentRef = new ParentReference(BaseWebServiceSystemTest.store, BaseWebServiceSystemTest.rootReference.getUuid(), null, Constants.ASSOC_CHILDREN, "{" + Constants.NAMESPACE_CONTENT_MODEL + "}testContent");
            NamedValue[] contentProperties = new NamedValue[]{new NamedValue(Constants.PROP_NAME, false, CONTENT_NAME, null)};
            CMLCreate createContent = new CMLCreate("testContent", contentParentRef, null, null, null, Constants.TYPE_CONTENT, contentProperties);
            
            ParentReference contentParentRef2 = new ParentReference(BaseWebServiceSystemTest.store, BaseWebServiceSystemTest.rootReference.getUuid(), null, Constants.ASSOC_CHILDREN, "{" + Constants.NAMESPACE_CONTENT_MODEL + "}testContent2");
            NamedValue[] contentProperties2 = new NamedValue[]{new NamedValue(Constants.PROP_NAME, false, CONTENT_NAME_2, null)};
            CMLCreate createContent2 = new CMLCreate("testContent2", contentParentRef2, null, null, null, Constants.TYPE_CONTENT, contentProperties2);
            
            // Create test folder
            ParentReference folderParentRef = new ParentReference(BaseWebServiceSystemTest.store, BaseWebServiceSystemTest.rootReference.getUuid(), null, Constants.ASSOC_CHILDREN, "{" + Constants.NAMESPACE_CONTENT_MODEL + "}testFolder");            
            NamedValue[] folderProperties = new NamedValue[]{new NamedValue(Constants.PROP_NAME, false, FOLDER_NAME, null)};
            CMLCreate createFolder = new CMLCreate("testFolder", folderParentRef, null, null, null, Constants.TYPE_FOLDER, folderProperties);
            
            // Create an associatin between the content
            CMLAddAspect cmlAddAspect = new CMLAddAspect("{http://www.alfresco.org/model/content/1.0}attachable", null, null, "testContent");
            CMLCreateAssociation createAssoc = new CMLCreateAssociation(null, "testContent", null, "testContent2", Constants.createQNameString(
                    Constants.NAMESPACE_CONTENT_MODEL, "attachments"));
            
            CML cml = new CML();
            cml.setCreate(new CMLCreate[]{createContent, createContent2, createFolder});
            cml.setAddAspect(new CMLAddAspect[]{cmlAddAspect});
            cml.setCreateAssociation(new CMLCreateAssociation[]{createAssoc});
            
            UpdateResult[] updateResult = this.repositoryService.update(cml);
            BaseWebServiceSystemTest.contentReference = updateResult[0].getDestination();
            BaseWebServiceSystemTest.contentReference2 = updateResult[1].getDestination();
            BaseWebServiceSystemTest.folderReference = updateResult[2].getDestination();
            
            // Write the test content to the reference
            this.contentService.write(BaseWebServiceSystemTest.contentReference, Constants.PROP_CONTENT, TEST_CONTENT.getBytes(), new ContentFormat(Constants.MIMETYPE_TEXT_PLAIN, "UTF-8"));
        }        
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        // End the current session
        AuthenticationUtils.endSession();
    }

      
    protected ParentReference getFolderParentReference(String assocName)
    {
        ParentReference parentReference = new ParentReference();
        parentReference.setStore(BaseWebServiceSystemTest.store);
        parentReference.setUuid(BaseWebServiceSystemTest.folderReference.getUuid());
        parentReference.setAssociationType(Constants.ASSOC_CONTAINS);
        parentReference.setChildName(assocName);
        return parentReference;
    }

    protected Reference createContentAtRoot(String name, String contentValue) throws Exception
    {
        ParentReference parentRef = new ParentReference();
        parentRef.setStore(BaseWebServiceSystemTest.store);
        parentRef.setUuid(BaseWebServiceSystemTest.rootReference.getUuid());
        parentRef.setAssociationType(Constants.ASSOC_CHILDREN);
        parentRef.setChildName("{" + Constants.NAMESPACE_CONTENT_MODEL + "}test" + System.currentTimeMillis());
        
        NamedValue[] properties = new NamedValue[]{new NamedValue(Constants.PROP_NAME, false, name, null)};
        CMLCreate create = new CMLCreate("1", parentRef, null, null, null, Constants.TYPE_CONTENT, properties);
        CML cml = new CML();
        cml.setCreate(new CMLCreate[]{create});
        UpdateResult[] result = this.repositoryService.update(cml);     
        
        Reference newContentNode = result[0].getDestination();
        
        Content content = this.contentService.write(newContentNode, Constants.PROP_CONTENT, contentValue.getBytes(), new ContentFormat("text/plain", "UTF-8"));
                
        assertNotNull(content);
        assertNotNull(content.getFormat());
        assertEquals("text/plain", content.getFormat().getMimetype());
        
        return content.getNode();        
    }    
    
    protected Predicate convertToPredicate(Reference reference)
    {
        Predicate predicate = new Predicate();
        predicate.setNodes(new Reference[] {reference});
        return predicate;
    }

}
