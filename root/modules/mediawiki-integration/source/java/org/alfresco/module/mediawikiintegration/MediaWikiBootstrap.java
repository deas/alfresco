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
import java.util.HashMap;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.module.AbstractModuleComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

public class MediaWikiBootstrap extends AbstractModuleComponent
{    
    private SearchService searchService;
    private NodeService nodeService; 
    
    private String wikiName = "Wiki";
    private String wikiTitle = "";
    private String wikiDescription = "";   
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    public void setWikiName(String wikiName)
    {
        this.wikiName = wikiName;
    }
    
    public void setWikiTitle(String wikiTitle)
    {
        this.wikiTitle = wikiTitle;
    }
    
    public void setWikiDescription(String wikiDescription)
    {
        this.wikiDescription = wikiDescription;
    }
    
    @Override
    protected void executeInternal() throws Throwable
    {
        // Get the company home node
        StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
        ResultSet resultSet = this.searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, "PATH:\"app:company_home\"");
        if (resultSet.length() == 0)
        {
            throw new AlfrescoRuntimeException("Unable to find company home node when running mediawiki integration bootstrap");
        }
        NodeRef companyHome = resultSet.getNodeRef(0);
        resultSet.close();
        
        // Create a mediawiki space within the company home space
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
        properties.put(ContentModel.PROP_NAME, this.wikiName);
        NodeRef mediaWikiNodeRef = this.nodeService.createNode(
                companyHome, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, this.wikiName), 
                Constants.TYPE_MEDIAWIKI,
                properties).getChildRef();
        
        // Add the titled aspect
        Map<QName, Serializable> titledProperties = new HashMap<QName, Serializable>(2);
        titledProperties.put(ContentModel.PROP_TITLE, this.wikiTitle);
        titledProperties.put(ContentModel.PROP_DESCRIPTION, this.wikiDescription);
        this.nodeService.addAspect(mediaWikiNodeRef, ContentModel.ASPECT_TITLED, titledProperties);
    }

}