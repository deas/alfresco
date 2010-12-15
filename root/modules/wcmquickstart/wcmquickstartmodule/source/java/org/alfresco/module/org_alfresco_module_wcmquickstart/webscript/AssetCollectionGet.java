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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Asset collection GET implementation
 * 
 * @author Roy Wetherall
 */
public class AssetCollectionGet extends DeclarativeWebScript implements WebSiteModel
{
	/** Parameter names */
	private static final String PARAM_COLLECTION_NAME = "name";
	private static final String PARAM_SECTION_ID = "sectionid";
	
	/** Node Service */
	private NodeService nodeService;
	
	/** File Folder Service */
	private FileFolderService fileFolderService;
	
	/**
	 * Set the node service
	 * @param nodeService	node serice
	 */
	public void setNodeService(NodeService nodeService)
    {
	    this.nodeService = nodeService;
    }
	
	/**
	 * Set the file folder service
	 * @param fileFolderService	file folder service
	 */
	public void setFileFolderService(FileFolderService fileFolderService)
    {
	    this.fileFolderService = fileFolderService;
    }
	
	/**
	 * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.Status, org.springframework.extensions.webscripts.Cache)
	 */
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
	{
		// Get the collection name
		Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
		String collectionName = templateVars.get(PARAM_COLLECTION_NAME);
		if (collectionName == null || collectionName.length() == 0)
		{
			throw new WebScriptException(Status.STATUS_NOT_FOUND, "No collection name was provided on the URL.");
		}
		
		// Get the section id
		String sectionId = req.getParameter(PARAM_SECTION_ID);
		if (sectionId == null || sectionId.length() == 0)
		{
			throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No section id parameter specified.");
		}
		else if (NodeRef.isNodeRef(sectionId) == false)
		{
			throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No section id is not a valid Alfresco node reference. ( " + sectionId + ")");
		}
			
		// Get the section node reference
		NodeRef sectionNodeRef = new NodeRef(sectionId);		
		
		// Get the collections node reference
		NodeRef collectionsNodeRef = fileFolderService.searchSimple(sectionNodeRef, "collections");
		if (collectionsNodeRef == null)
		{
			throw new WebScriptException(Status.STATUS_NOT_FOUND, "The collections folder for the section " + sectionId + " could not be found.");
		}
		
		// Look for the collection node reference
		NodeRef collection = fileFolderService.searchSimple(collectionsNodeRef, collectionName);
		if (collection == null)
		{
			throw new WebScriptException(Status.STATUS_NOT_FOUND, "Unable to find collection " + collectionName + " in section " + sectionId);
		}
		
		// Gather the collection data
		Map<QName, Serializable> collectionProps = nodeService.getProperties(collection);
		CollectionData collectionData = new CollectionData();
		collectionData.setId(collection.toString());
		collectionData.setName((String)collectionProps.get(ContentModel.PROP_NAME));
		
        String title = (String) collectionProps.get(ContentModel.PROP_TITLE);
        collectionData.setTitle(title == null ? "" : title);
        
        String description = (String) collectionProps.get(ContentModel.PROP_DESCRIPTION);
        collectionData.setDescription(description == null ? "" : description);

		// Gather information about the associated
		List<AssociationRef> assocs = nodeService.getTargetAssocs(collection, ASSOC_WEBASSETS);
		String[] assetIds = new String[assocs.size()];
		int index = 0;
		for (AssociationRef assoc : assocs)
        {
	        assetIds[index] = assoc.getTargetRef().toString();
	        index++;
        }
		collectionData.setAssetIds(assetIds);
		
		// Put the collection data in the model and pass to the view
		Map<String, Object> model = new HashMap<String, Object>(1);
		model.put("collection", collectionData);		
		return model;
	}
	
	/** Class to contain collection data */
	public class CollectionData
	{
		private String id;
		private String name;
		private String title;
		private String description;
		private String[] assetIds;
		/**
         * @param name the name to set
         */
        public void setName(String name)
        {
	        this.name = name;
        }
		/**
         * @return the name
         */
        public String getName()
        {
	        return name;
        }
		/**
         * @param id the id to set
         */
        public void setId(String id)
        {
	        this.id = id;
        }
		/**
         * @return the id
         */
        public String getId()
        {
	        return id;
        }
		/**
         * @param title the title to set
         */
        public void setTitle(String title)
        {
	        this.title = title;
        }
		/**
         * @return the title
         */
        public String getTitle()
        {
	        return title;
        }
		/**
         * @param assetIds the assetIds to set
         */
        public void setAssetIds(String[] assetIds)
        {
	        this.assetIds = assetIds;
        }
		/**
         * @return the assetIds
         */
        public String[] getAssetIds()
        {
	        return assetIds;
        }
		/**
         * @param description the description to set
         */
        public void setDescription(String description)
        {
	        this.description = description;
        }
		/**
         * @return the description
         */
        public String getDescription()
        {
	        return description;
        }		
	}
}
