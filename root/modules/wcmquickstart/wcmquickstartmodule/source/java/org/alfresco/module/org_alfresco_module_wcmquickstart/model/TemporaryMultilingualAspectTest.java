/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.WCMQuickStartTest;
import org.alfresco.service.cmr.ml.MultilingualContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Tests for the way that the temporary multilingual aspect behaviour works
 * 
 * @author Nick Burch
 */
public class TemporaryMultilingualAspectTest extends WCMQuickStartTest implements WebSiteModel
{
    private MultilingualContentService multilingualContentService;
    
    @Override
    protected void setUp() throws Exception 
    {
        super.setUp();
        
        multilingualContentService = (MultilingualContentService)appContext.getBean("multilingualContentService");
    }
    
    public void testSiblingTranslationDocs() throws Exception
    {
        UserTransaction userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        
        // Create a French document
        NodeRef french = nodeService.createNode(
                editorialSiteRoot, ContentModel.ASSOC_CONTAINS,
                QName.createQName("French"), TYPE_ARTICLE
        ).getChildRef();
        multilingualContentService.makeTranslation(french, Locale.FRENCH);
        
        // Now create the Spanish translation of it
        NodeRef spanish = nodeService.createNode(
                editorialSiteRoot, ContentModel.ASSOC_CONTAINS,
                QName.createQName("Spanish"), TYPE_ARTICLE
        ).getChildRef();
        
        Map<QName,Serializable> props = new HashMap<QName, Serializable>();
        props.put(PROP_TRANSLATION_OF, french);
        props.put(PROP_LANGUAGE, "Spanish");
        nodeService.addAspect(spanish, ASPECT_TEMPORARY_MULTILINGUAL, props);
        
        assertEquals(true, nodeService.hasAspect(spanish, ASPECT_TEMPORARY_MULTILINGUAL));
        assertEquals(false, nodeService.hasAspect(spanish, ContentModel.ASPECT_LOCALIZED));
        assertEquals(false, multilingualContentService.isTranslation(spanish));
        
        
        // Commit
        userTransaction.commit();
        
        
        // Check the behaviour fired properly
        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        
        assertEquals(false, nodeService.hasAspect(spanish, ASPECT_TEMPORARY_MULTILINGUAL));
        assertEquals(true, nodeService.hasAspect(spanish, ContentModel.ASPECT_LOCALIZED));
        assertEquals(true, multilingualContentService.isTranslation(spanish));
        
        userTransaction.commit();
    }
    
    public void testSiblingTranslationFolders() throws Exception
    {
        UserTransaction userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
     
        // Create a French folder, with some collections
        NodeRef french = nodeService.createNode(
                editorialSiteRoot, ContentModel.ASSOC_CONTAINS,
                QName.createQName("French"), TYPE_SECTION
        ).getChildRef();
        multilingualContentService.makeTranslation(french, Locale.FRENCH);
        
        userTransaction.commit();
        userTransaction.begin();
        
        // TODO Populate the collection 
        
        
        // Now create the Spanish one
        NodeRef spanish = nodeService.createNode(
                editorialSiteRoot, ContentModel.ASSOC_CONTAINS,
                QName.createQName("Spanish"), TYPE_SECTION
        ).getChildRef();
        
        Map<QName,Serializable> props = new HashMap<QName, Serializable>();
        props.put(PROP_TRANSLATION_OF, french);
        props.put(PROP_LANGUAGE, "Spanish");
        nodeService.addAspect(spanish, ASPECT_TEMPORARY_MULTILINGUAL, props);

        
        assertEquals(true, nodeService.hasAspect(spanish, ASPECT_TEMPORARY_MULTILINGUAL));
        assertEquals(false, nodeService.hasAspect(spanish, ContentModel.ASPECT_LOCALIZED));
        assertEquals(false, multilingualContentService.isTranslation(spanish));
        
        
        // Commit
        userTransaction.commit();
        
        
        // Check the behaviour fired properly
        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        
        assertEquals(false, nodeService.hasAspect(spanish, ASPECT_TEMPORARY_MULTILINGUAL));
        assertEquals(true, nodeService.hasAspect(spanish, ContentModel.ASPECT_LOCALIZED));
        assertEquals(true, multilingualContentService.isTranslation(spanish));
        
        // Now check the Spanish collections
        // TODO
        
        userTransaction.commit();
    }
    
    public void testTranslationDocAllFoldersExist() throws Exception
    {
        
    }
    
    public void testTranslationDocFoldersToBeCreated() throws Exception
    {
        
    }
}
