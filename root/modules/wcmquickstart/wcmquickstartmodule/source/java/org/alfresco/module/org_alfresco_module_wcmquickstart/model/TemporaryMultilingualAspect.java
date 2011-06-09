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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.ml.MultilingualContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ws:temporaryMultilingual aspect behaviour. This handles turning a regular
 * node into a multilingual one
 * 
 * @author Nick Burch
 */
public class TemporaryMultilingualAspect implements NodeServicePolicies.OnAddAspectPolicy
{
    private static final Log log = LogFactory.getLog(TemporaryMultilingualAspect.class);

    /** Policy component */
    private PolicyComponent policyComponent;

    private BehaviourFilter behaviourFilter;
    
    private MultilingualContentService multilingualContentService;

    private SiteHelper siteHelper;

    private NodeService nodeService;

    /**
     * Set the policy component
     * 
     * @param policyComponent policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }

    public void setMultilingualContentService(MultilingualContentService multilingualContentService)
    {
        this.multilingualContentService = multilingualContentService;
    }

    public void setSiteHelper(SiteHelper siteHelper)
    {
        this.siteHelper = siteHelper;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Init method. Binds model behaviours to policies.
     */
    public void init()
    {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnAddAspectPolicy.QNAME,
                WebSiteModel.ASPECT_TEMPORARY_MULTILINGUAL, new JavaBehaviour(this, "onAddAspect",
                        NotificationFrequency.TRANSACTION_COMMIT));
        
        if(log.isDebugEnabled())
        {
            log.debug("Enabled behaviour on " + WebSiteModel.ASPECT_TEMPORARY_MULTILINGUAL);
        }
    }

    /**
     * Identify the locale of a node. This could be from a ws:language, or
     * could be from walking up the tree until we find one.
     */
    public Locale identifyLocale(NodeRef nodeRef)
    {
        // We can't help them if we don't have a noderef
        if(nodeRef == null)
        {
            return null;
        }
        
        // If this node is the site root, stop looking, we don't know...
        if(siteHelper.isTranslationParentLimitReached(nodeRef))
        {
            return null;
        }
        
        // If the node has the ws:language, use that
        String language = (String)nodeService.getProperty(nodeRef, WebSiteModel.PROP_LANGUAGE);
        if(language != null)
        {
            return new Locale(language);
        }
        
        // Try the sys:locale
        Locale locale = (Locale)nodeService.getProperty(nodeRef, ContentModel.PROP_LOCALE);
        if(locale != null && !"".equals(locale))
        {
            return locale;
        }
        
        // Try the parent
        return identifyLocale( nodeService.getPrimaryParent(nodeRef).getParentRef() );
    }

    @Override
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        NodeRef translationOf = (NodeRef)nodeService.getProperty(nodeRef, WebSiteModel.PROP_TRANSLATION_OF);
        Boolean initiallyOrphaned = (Boolean)nodeService.getProperty(nodeRef, WebSiteModel.PROP_INITIALLY_ORPHANED);
        
        // Try to identify the language for the node
        Locale locale = identifyLocale(nodeRef);
        
        if(locale == null)
        {
            log.warn("Asked to setup multilingual for " + nodeRef + " but no language given and no " +
                     "translated parent found, no translation added");
        }
        else
        {
            if(log.isDebugEnabled())
            {
                log.debug("Enabling translation in " + locale + " for " + nodeRef);
            }
        }
        
        // Tie things up with the ML Service
        if(translationOf != null)
        {
            if(! multilingualContentService.isTranslation(translationOf))
            {
                // The document we're a translation of isn't itself
                //  marked as a translation!
                throw new AlfrescoRuntimeException("Can't make a document a translation of node without a language");
            }
            
            // If this is an explicit translation, then tie that up with the ML Service
            if(locale != null)
            {
                // Mark this as a translation
                multilingualContentService.addTranslation(
                        nodeRef, translationOf, locale
                );
                
                // Now copy over the collections
                // TODO
            }
        }
        else
        {
            if(locale != null)
            {
                // Mark this as being the first translation
                multilingualContentService.makeTranslation(nodeRef, locale);
            }
        }
        
        // If this node is initially orphaned, then create the intermediate folders
        //  that are missing for it
        if(initiallyOrphaned != null && initiallyOrphaned)
        {
            // We currently have a situation like:
            //   Root:
            //     French  -> Folder1 -> Folder2 -> Document
            //     Spanish -> Folder1 -> (Orphan) Document
            // We need to identify the missing bits and fill them in
            
            // Identify the parents that are missing
            List<Pair<NodeRef,String>> parents = new ArrayList<Pair<NodeRef,String>>();
            
            NodeRef parent = nodeService.getPrimaryParent(nodeRef).getParentRef();
            while(parent != null)
            {
                // If we hit the site root, stop
                if(siteHelper.isTranslationParentLimitReached(nodeRef))
                {
                    break;
                }
                
                // If we hit something that's translated into the right language,
                //  then we can stop
                if(multilingualContentService.isTranslation(parent))
                {
                    // TODO
                    break;
                }
                
                parent = nodeService.getPrimaryParent(parent).getParentRef();
            }
            
            // TODO This is not yet supported
        }
        
        // Finally tidy up by removing the temp aspect
        nodeService.removeAspect(nodeRef, WebSiteModel.ASPECT_TEMPORARY_MULTILINGUAL); 
    }
}
