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
package org.alfresco.module.org_alfresco_module_wcmquickstart.webscript;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.AssetSerializer;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.AssetSerializerFactory;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Asset GET implementation
 */
public class AssetGet extends AbstractWebScript
{
    private static final String PARAM_NODEREF = "noderef";
    private static final String PARAM_MODIFIED_TIME_ONLY = "modifiedTimeOnly";
    private static final String PARAM_SITE_ID = "siteid";
    private static final String PARAM_SECTION_ID = "sectionid";
    private static final String PARAM_NODE_NAME = "nodename";

    private NodeService nodeService;
    private SearchService searchService;
    private AssetSerializerFactory assetSerializerFactory;
    private SiteHelper siteHelper;

    public void setAssetSerializerFactory(AssetSerializerFactory assetSerializerFactory)
    {
        this.assetSerializerFactory = assetSerializerFactory;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setSiteHelper(SiteHelper siteHelper)
    {
        this.siteHelper = siteHelper;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        try
        {
            List<NodeRef> foundNodes = new ArrayList<NodeRef>();
            String[] nodeRefs = req.getParameterValues(PARAM_NODEREF);
            if (nodeRefs == null || nodeRefs.length == 0)
            {
                String sectionIdText = req.getParameter(PARAM_SECTION_ID);
                String nodeName = req.getParameter(PARAM_NODE_NAME);
                if (sectionIdText == null || sectionIdText.length() == 0 || nodeName == null || nodeName.length() == 0)
                {
                    throw new WebScriptException(
                            "Either noderef or sectionid and nodename are required parameters");
                }
                
                NodeRef siteId = null;
                String siteIdText = req.getParameter(PARAM_SITE_ID);
                if (siteIdText == null)
                {
                    siteId = siteHelper.getRelevantWebSite(new NodeRef(sectionIdText));
                }
                else
                {
                    siteId = new NodeRef(siteIdText);
                }

                String query = "+@ws\\:parentSections:\"" + sectionIdText + "\" +@cm\\:name:\"" + nodeName + "\"";
                SearchParameters searchParameters = new SearchParameters();
                searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
                searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
                searchParameters.setQuery(query);
                List<Locale> locales = siteHelper.getWebSiteLocales(siteId);
                for (Locale locale : locales)
                {
                    searchParameters.addLocale(locale);
                }
                ResultSet rs = searchService.query(searchParameters);
                if (rs.length() > 0)
                {
                    foundNodes.add(rs.getNodeRef(0));
                }
            }
            else
            {
                for (String nodeRefString : nodeRefs)
                {
                    try
                    {
                        NodeRef nodeRef = new NodeRef(nodeRefString);
                        if (nodeService.exists(nodeRef) && (nodeService.getProperties(nodeRef) != null))
                        {
                            foundNodes.add(nodeRef);
                        }
                    }
                    catch (Exception ex)
                    {
                        //Safe to ignore
                    }
                }
            }

            boolean onlyModifiedTime = (req.getParameter(PARAM_MODIFIED_TIME_ONLY) != null);
            
            res.setContentEncoding("UTF-8");
            res.setContentType("text/xml");
            Writer writer = res.getWriter();
            AssetSerializer assetSerializer = assetSerializerFactory.getAssetSerializer();
            assetSerializer.start(writer);
            for (NodeRef nodeRef : foundNodes)
            {
                QName typeName = nodeService.getType(nodeRef);
                Map<QName,Serializable> properties;
                if (onlyModifiedTime)
                {
                    properties = new HashMap<QName, Serializable>(3);
                    properties.put(ContentModel.PROP_MODIFIED, nodeService.getProperty(nodeRef, 
                            ContentModel.PROP_MODIFIED));
                }
                else
                {
                    properties = nodeService.getProperties(nodeRef);
                }
                assetSerializer.writeNode(nodeRef, typeName, properties);
            }
            assetSerializer.end();
        }
        catch (Throwable e)
        {
            throw createStatusException(e, req, res);
        }
    }
}
