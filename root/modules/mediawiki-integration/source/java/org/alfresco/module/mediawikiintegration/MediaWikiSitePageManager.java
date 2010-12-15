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
package org.alfresco.module.mediawikiintegration;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.mediawikiintegration.action.MediaWikiActionExecuter;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.TemplateService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Manages wiki pages relating to sites.  If site are not supported by the repository intalled then 
 * this management will be ingnored.
 * 
 * @author Roy Wetherall
 */
public class MediaWikiSitePageManager implements NodeServicePolicies.OnCreateNodePolicy,
                                                 Constants
{
    /** Policy Component */
    private PolicyComponent policyComponent;
    
    /** Node sevice */
    private NodeService nodeService;
    
    /** Action serivice **/
    private ActionService actionService;
    
    /** The template service */
    private TemplateService templateService;
    
    /** Site page tamplate node */
    private static NodeRef templateNodeRef = new NodeRef("workspace://SpacesStore/php_mediawiki_site_page");
    
    /** MediaWiki url */
    private String mediaWikiURL;
    
    /**
     * Set policy component
     * 
     * @param policyComponent   policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * Set node service
     * 
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Set action service
     * 
     * @param actionService     action service
     */
    public void setActionService(ActionService actionService)
    {
        this.actionService = actionService;
    }
    
    /**
     * Set template service
     * 
     * @param templateService   template service
     */
    public void setTemplateService(TemplateService templateService)
    {
        this.templateService = templateService;
    }
    
    /**
     * Set the mediaWiki URL
     * 
     * @param mediaWikiURL
     */
    public void setMediaWikiURL(String mediaWikiURL)
    {
        this.mediaWikiURL = mediaWikiURL;
    }
    
    /**
     * Initialise method
     */
    public void init()
    {
        QName siteType = QName.createQName("http://www.alfresco.org/model/site/1.0", "site");
        
        // Register the behaviours
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"), 
                siteType, 
                new JavaBehaviour(this, "onCreateNode"));
    }

    /**
     * Action to take when a site is created
     * 
     * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy#onCreateNode(org.alfresco.service.cmr.repository.ChildAssociationRef)
     */
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        // Get the site node
        NodeRef site = childAssocRef.getChildRef();
        
        // Get the title of the site to be used as the title of the created page
        String siteTitle = (String)this.nodeService.getProperty(site, ContentModel.PROP_TITLE);
        
        // Execute the site page template         
        Object model = new HashMap<String, Object>(1);
        ((Map<String, Object>)model).put("site", site);
        String pageContent = this.templateService.processTemplate(MediaWikiSitePageManager.templateNodeRef.toString(), model);        
        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put("pageContent", pageContent);
        
        // Execute the action to contact mediawiki and create the site page
        Action action = actionService.createAction(MediaWikiActionExecuter.NAME);
        action.setParameterValue(MediaWikiActionExecuter.PARAM_MEDIAWIKI_ACTION, "createPage");
        action.setParameterValue(MediaWikiActionExecuter.PARAM_PAGE_TITLE, siteTitle);
        action.setParameterValue(MediaWikiActionExecuter.PARAM_PARAMS, params);
        actionService.executeAction(action, site, false, true);
        
        // Apply the custom aspect to the site and generate the media wiki page
        String pageURL = MessageFormat.format(MEDIAWIKI_PAGE_URL, this.mediaWikiURL, siteTitle);
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
        properties.put(PROP_MEDIA_WIKI_URL, pageURL);
        this.nodeService.addAspect(site, ASPECT_SITE_CUSTOM_PROPERTIES, properties);         
    } 
}
