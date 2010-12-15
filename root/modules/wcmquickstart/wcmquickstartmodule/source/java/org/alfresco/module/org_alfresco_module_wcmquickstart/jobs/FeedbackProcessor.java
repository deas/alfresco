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

import java.util.Map;
import java.util.TreeMap;

import org.alfresco.module.org_alfresco_module_wcmquickstart.jobs.feedback.FeedbackProcessorHandler;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is designed to be run periodically. It finds any visitor feedback
 * that has not been processed yet and, for each item it finds, invokes the feedback handler that has been
 * registered for that type of feedback.
 * 
 * @author Brian
 */
public class FeedbackProcessor
{
    /** Logger */
    private static final Log log = LogFactory.getLog(FeedbackProcessor.class);

    /** Retrying transaction helper */
    private RetryingTransactionHelper txHelper;
    
    /** Search service */
    private SearchService searchService;
    
    /** Node service */
    private NodeService nodeService;
    
    /** Map of feedback processors */
    private Map<String, FeedbackProcessorHandler> handlers = new TreeMap<String, FeedbackProcessorHandler>();

    /**
     * Register a feedback processor handler
     * @param handler   feedback processor handler
     */
    public void registerHandler(FeedbackProcessorHandler handler)
    {
        handlers.put(handler.getFeedbackType(), handler);
    }
    
    /**
     * Run job.  Find all feedback node references that require processing and delegate to appropriate
     * handlers.
     */
    public void run()
    {
        AuthenticationUtil.runAs(new RunAsWork<Object>()
        {
            @Override
            public Object doWork() throws Exception
            {
                txHelper.doInTransaction(new RetryingTransactionCallback<Object>()
                {
                    @Override
                    public Object execute() throws Throwable
                    {                   
                        //Find all visitor feedback nodes that have not yet been processed
                        ResultSet rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
                                SearchService.LANGUAGE_LUCENE, "@ws\\:ratingProcessed:\"false\"");
                     
                        if (log.isDebugEnabled())
                        {
                            log.debug("Running feedback processor across " + rs.length() + " feedback nodes");
                        }
                        for (ResultSetRow row : rs)
                        {                 
                            // Get the feedback node and feedback type
                            NodeRef feedback = row.getNodeRef();
                            String feedbackType = (String)nodeService.getProperty(feedback, WebSiteModel.PROP_FEEDBACK_TYPE);
                            
                            if (feedbackType != null)
                            {
                                // Get the feedback processor handler
                                FeedbackProcessorHandler handler = handlers.get(feedbackType);
                                if (handler != null)
                                {
                                    /// TODO wrap into sub transaction
                                    
                                    // Process the feedback
                                    if (log.isDebugEnabled() == true)
                                    {
                                        log.debug("Processing feedback node " + feedback.toString() + " of feedback type " + feedbackType);                                        
                                    }
                                    handler.processFeedback(feedback);
                                    
                                    // END
                                    // TODO Log exception                                    
                                    
                                    //Set the "ratingProcessed" flag to true on this feedback node so we don't process it again
                                    nodeService.setProperty(feedback, WebSiteModel.PROP_RATING_PROCESSED, Boolean.TRUE);
                                }
                                else
                                {
                                    // Record that a feedback processor could not be found
                                    if (log.isDebugEnabled() == true)
                                    {
                                        log.debug("Feedback processor handler can not be found for feedback type " + feedbackType + " on feedback node " + feedback.toString());
                                    }
                                }
                            }
                            else
                            {
                                // Record that no feedback type has been set for this feedback
                                if (log.isDebugEnabled() == true)
                                {
                                    log.debug("Feedback type not specified for feedback node " + feedback.toString());
                                }
                            }                                
                        }
                        
                        // Execute feedback processor callbacks
                        for (FeedbackProcessorHandler handler : handlers.values())
                        {
                            if (log.isDebugEnabled() == true)
                            {
                                log.debug("Executing feedback handler callback for feedback type " + handler.getFeedbackType());
                            }
                            handler.processorCallback();
                        }
                        
                        return null;
                    }   
                });
                return null;
            }
        }, AuthenticationUtil.SYSTEM_USER_NAME);
    }

    /**
     * Sets the transaction helper
     * @param txHelper  transaction helper
     */
    public void setTxHelper(RetryingTransactionHelper txHelper)
    {
        this.txHelper = txHelper;
    }

    /**
     * Sets the search service
     * @param searchService search service
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    /**
     * Sets the node service
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
}
