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

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is designed to be run at midnight. It finds any web assets that are either becoming available today or expiring today
 * and sets their "published" flag as appropriate
 * @author Brian
 *
 */
public class AvailabilityProcessor
{
    private static final Log log = LogFactory.getLog(AvailabilityProcessor.class);
    
    private RetryingTransactionHelper txHelper;
    private SearchService searchService;
    private NodeService nodeService;
    private BehaviourFilter behaviourFilter;
    
    public void run()
    {
        txHelper.doInTransaction(new RetryingTransactionCallback<Object>()
        {
            @Override
            public Object execute() throws Throwable
            {
                return AuthenticationUtil.runAs(new RunAsWork<Object>()
                {
                    @Override
                    public Object doWork() throws Exception
                    {
                        behaviourFilter.disableBehaviour(ContentModel.ASPECT_AUDITABLE);
                        try
                        {
                            //Find all web assets that are due to become available today
                            ResultSet rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
                                    SearchService.LANGUAGE_LUCENE, "+@ws\\:availableFromDate:today +@ws\\:published:\"false\"");
                         
                            if (log.isDebugEnabled())
                            {
                                log.debug("Number of assets found that are due to become available: " + rs.length());
                            }
                            for (ResultSetRow row : rs)
                            {
                                nodeService.setProperty(row.getNodeRef(), WebSiteModel.PROP_AVAILABLE, Boolean.TRUE);
                            }

                            //Find all web assets that are due to expire today
                            rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
                                    SearchService.LANGUAGE_LUCENE, "+@ws\\:availableToDate:today +@ws\\:published:\"true\"");
                         
                            if (log.isDebugEnabled())
                            {
                                log.debug("Number of assets found that are due to expire: " + rs.length());
                            }
                            for (ResultSetRow row : rs)
                            {
                                nodeService.setProperty(row.getNodeRef(), WebSiteModel.PROP_AVAILABLE, Boolean.FALSE);
                            }
                        }
                        finally
                        {
                            behaviourFilter.enableBehaviour(ContentModel.ASPECT_AUDITABLE);
                        }
                        return null;
                    }
                }, AuthenticationUtil.SYSTEM_USER_NAME);
            }
        });
    }
    
    public void setTxHelper(RetryingTransactionHelper txHelper)
    {
        this.txHelper = txHelper;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }
    
}
