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
package org.alfresco.module.org_alfresco_module_wcmquickstart.jobs;

import java.util.Calendar;
import java.util.Date;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.WebassetCollectionHelper;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Roy Wetherall
 */
public class DynamicCollectionProcessor implements WebSiteModel
{
    /** Log */
	private static final Log log = LogFactory.getLog(DynamicCollectionProcessor.class);

	/** Query */
	private static final String QUERY = "+ TYPE:\"ws:webassetCollection\" + @ws\\:isDynamic:true";
	
	/** Transaction service */
    private TransactionService transactionService;
    
    /** Node service */
    private NodeService nodeService;
    
    /** Search service */
    private SearchService searchService;
    
    /** Webasset Collection Helper */
    private WebassetCollectionHelper collectionHelper;
    
    /**
     * Set search service
     * @param searchService search service
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    /**
     * Set node service
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set transaction service
     * @param transactionService    transaction service
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    /**
     * Set collection helper
     * @param collectionHelper  collection helper
     */
    public void setCollectionHelper(WebassetCollectionHelper collectionHelper)
    {
        this.collectionHelper = collectionHelper;
    }

    /**
     * Run the processor job.  Refreshing any dynamic queries who's refresh date is before today.
     */
    public void run()
    {
        AuthenticationUtil.runAs(new RunAsWork<Object>()
        {
            @Override
            public Object doWork() throws Exception
            {
                transactionService.getRetryingTransactionHelper().doInTransaction(
                        new RetryingTransactionCallback<Object>()
                {
                    @Override
                    public Object execute() throws Throwable
                    {
                        //Find all web root nodes
                        ResultSet rs = searchService.query(
                        				StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
                        				SearchService.LANGUAGE_LUCENE, 
                        				QUERY);
                     
                        if (log.isDebugEnabled())
                        {
                            log.debug("Running dynamic collection refresh processor across " + rs.length() + " dynamic collection nodes");
                        }
                        
                        // Get the current date
                        Calendar now = Calendar.getInstance();
                        
                        // Interate over the dynamic queries 
                        for (NodeRef collection : rs.getNodeRefs())
                        {
                            Date refreshAtDate = (Date)nodeService.getProperty(collection, PROP_REFRESH_AT);
                            Calendar refreshAt = Calendar.getInstance();
                            if (refreshAtDate != null)
                            {
                                // Convert the date to calendar
                                refreshAt.setTime(refreshAtDate);
                            }
                                
                            if ((refreshAtDate == null) || now.after(refreshAt))
                            {
                                if (log.isDebugEnabled() == true)
                                {
                                    String collectionName = (String)nodeService.getProperty(collection, ContentModel.PROP_NAME);
                                    if (collectionName != null)
                                    {
                                        log.debug("Refreshing dynamic collection " + collectionName);
                                    }
                                }                                    
                                
                                // Refresh the collection
                                collectionHelper.refreshCollection(collection);
                            }
                        }
                        return null;
                    }   
                });
                return null;
            }
        }, AuthenticationUtil.SYSTEM_USER_NAME);
    }
}
