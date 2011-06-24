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
package org.alfresco.module.org_alfresco_module_dod5015.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Records management search service implementation
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementSearchServiceImpl implements RecordsManagementSearchService 
{
    /** Name of the main site container used to store the saved searches within */
    private static final String SEARCH_CONTAINER = "Saved Searches";
    
    /** File folder service */
    private FileFolderService fileFolderService;
	
	/** Site service */
	private SiteService siteService;
	
	private NamespaceService namespaceService;
	
	/** List of report details */
	private List<ReportDetails> reports = new ArrayList<ReportDetails>(13);
	
	/**
	 * @param fileFolderService    file folder service
	 */
	public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }
	
	/**
	 * @param siteService  site service
	 */
	public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }
	
	public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
	
	/**
	 * @param reportsJSON
	 */
	public void setReportsJSON(String reportsJSON)
    {
	    try
	    {
    	   JSONArray jsonArray = new JSONArray(reportsJSON);
    	   if (jsonArray != null)
    	   {
    	       for (int i=0; i < jsonArray.length(); i++)
    	       {
    	           JSONObject report = jsonArray.getJSONObject(i);
    	           
    	           // Get the name
    	           if (report.has(SavedSearchDetails.NAME) == false)
    	           {
    	               throw new AlfrescoRuntimeException("Unable to load report details because name has not been specified. \n" + reportsJSON);
    	           }
    	           String name = report.getString(SavedSearchDetails.NAME);
    	           
    	           // Get the query
    	           if (report.has(SavedSearchDetails.QUERY) == false)
                   {
                       throw new AlfrescoRuntimeException("Unable to load report details because query has not been specified for report " + name + ". \n" + reportsJSON);
                   }
                   String query = report.getString(SavedSearchDetails.QUERY);
                   
                   // Get the description
                   String description = "";
                   if (report.has(SavedSearchDetails.DESCRIPTION) == true)
                   {
                       description = report.getString(SavedSearchDetails.DESCRIPTION);
                   }
                   
                   // Get the sort string
                   String sort = "";
                   if (report.has(SavedSearchDetails.SORT) == true)
                   {
                       sort = report.getString(SavedSearchDetails.SORT);
                   }
                   
                   // Get the param string
                   String params = "";
                   if (report.has(SavedSearchDetails.PARAMS) == true)
                   {
                       params = report.getString(SavedSearchDetails.PARAMS);
                   }
                   
                   // Create the report details and add to list
    	           ReportDetails reportDetails = new ReportDetails(name, description, query, sort, params);
    	           reports.add(reportDetails);
    	       }
    	   }
	    }
	    catch (JSONException exception)
	    {
	        throw new AlfrescoRuntimeException("Unable to load report details.\n" + reportsJSON, exception);
	    }
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.search.RecordsManagementSearchService#search(java.lang.String)
     */
    @Override
    public List<NodeRef> search(String query) 
    {
        return null;
    }
    
    private String buildQueryString(String queryTerm, List<QName> aspects, List<QName> types)
    {
       StringBuilder aspectQuery = new StringBuilder();    
       if (aspects != null)
       {
           for (QName aspect : aspects)
           {
               if (aspectQuery.length() != 0)
               {
                   aspectQuery.append(" AND ");
               }
               aspectQuery.append("ASPECT:\"")
                          .append(aspect.toPrefixString(namespaceService))
                          .append("\"");
           }
       }
       
       StringBuilder typeQuery = new StringBuilder();
       if (types != null)
       {
           for (QName type : types)       
           {
               if (typeQuery.length() != 0)
               {
                   typeQuery.append(" ");
               }
               aspectQuery.append("TYPE:\"")
                          .append(type.toPrefixString(namespaceService))
                          .append("\"");
           }
       }
       
       StringBuilder query = new StringBuilder();
       if (queryTerm == null || queryTerm.length() == 0)
       {
           // Default to search for everything
           query.append("ISNODE:T");
       }
       else
       {
           // TODO .. if the query term is a basic string then do a full text search ...           
           query.append(queryTerm);
       }

       StringBuilder fullQuery = new StringBuilder(1024);
       if (aspectQuery.length() != 0 || typeQuery.length() != 0)
       {
           if (aspectQuery.length() != 0 && typeQuery.length() != 0)
           {
               fullQuery.append("(");
           }
           
           if (aspectQuery.length() != 0)
           {
               fullQuery.append("(").append(aspectQuery).append(") ");
           }
           
           if (typeQuery.length() != 0)
           {
               fullQuery.append("(").append(typeQuery).append(")");
           }
           
           if (aspectQuery.length() != 0 && typeQuery.length() != 0)
           {
               fullQuery.append(")");
           }
       }
       
       if (fullQuery.length() != 0)
       {
           fullQuery.append(" AND ");
       }
       fullQuery.append("(")
                .append(queryTerm)
                .append(") AND NOT ASPECT:\"rma:versionedRecord\"");                
       
       return fullQuery.toString();
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.search.RecordsManagementSearchService#getSavedSearches(java.lang.String)
     */
    @Override
    public List<SavedSearchDetails> getSavedSearches(String siteId) 
    {
        List<SavedSearchDetails> result = new ArrayList<SavedSearchDetails>(17);
        
        NodeRef container = siteService.getContainer(siteId, SEARCH_CONTAINER);
        if (container != null)
        {
            // add the details of all the public saved searches
            List<FileInfo> searches = fileFolderService.listFiles(container);
            for (FileInfo search : searches)
            {
                addSearchDetailsToList(result, search.getNodeRef());
            }
            
            // add the details of any "private" searches for the current user
            String userName = AuthenticationUtil.getFullyAuthenticatedUser();
            NodeRef userContainer = fileFolderService.searchSimple(container, userName);
            if (userContainer != null)
            {
                List<FileInfo> userSearches = fileFolderService.listFiles(userContainer);
                for (FileInfo userSearch : userSearches)
                {
                    addSearchDetailsToList(result, userSearch.getNodeRef());
                }
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param searches
     * @param searchNode
     */
    private void addSearchDetailsToList(List<SavedSearchDetails> searches, NodeRef searchNode)
    {
        ContentReader reader = fileFolderService.getReader(searchNode);
        String jsonString = reader.getContentString();
        SavedSearchDetails savedSearchDetails = SavedSearchDetails.createFromJSON(jsonString);
        searches.add(savedSearchDetails);
    }
        
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.search.RecordsManagementSearchService#getSavedSearch(java.lang.String, java.lang.String)
     */
    @Override
    public SavedSearchDetails getSavedSearch(String siteId, String name) 
    {
        // check for mandatory parameters
        ParameterCheck.mandatory("siteId", siteId);
        ParameterCheck.mandatory("name", name);
        
        SavedSearchDetails result = null;
        
        // get the saved search node
        NodeRef searchNode = getSearchNodeRef(siteId, name);
        
        if (searchNode != null)
        {        
            // get the json content
            ContentReader reader = fileFolderService.getReader(searchNode);
            String jsonString = reader.getContentString();
    
            // create the saved search details
            result = SavedSearchDetails.createFromJSON(jsonString);
        }
        
        return result;
    }
   
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.search.RecordsManagementSearchService#saveSearch(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
	@Override
	public SavedSearchDetails saveSearch(String siteId, String name, String description, String query, String sort, String params, boolean isPublic) 
	{ 
	    // Check for mandatory parameters
	    ParameterCheck.mandatory("siteId", siteId);
	    ParameterCheck.mandatory("name", name);
	    ParameterCheck.mandatory("query", query);
	    
        // Create saved search details
        SavedSearchDetails savedSearchDetails = new SavedSearchDetails(siteId, name, description, query, sort, params, isPublic, false);
	    
        // Save search details
        return saveSearch(savedSearchDetails);
	}
	
	/**
	 * @see org.alfresco.module.org_alfresco_module_dod5015.search.RecordsManagementSearchService#saveSearch(org.alfresco.module.org_alfresco_module_dod5015.search.SavedSearchDetails)
	 */
	@Override
	public SavedSearchDetails saveSearch(final SavedSearchDetails savedSearchDetails) 
    {
	    // Check for mandatory parameters
	    ParameterCheck.mandatory("savedSearchDetails", savedSearchDetails);
	    
	    // Get the root saved search container
	    final String siteId = savedSearchDetails.getSiteId();        
	    NodeRef container = siteService.getContainer(siteId, SEARCH_CONTAINER);
        if (container == null)
        {
            container = AuthenticationUtil.runAs(new RunAsWork<NodeRef>()
            {
                @Override
                public NodeRef doWork() throws Exception
                {
                    return siteService.createContainer(siteId, SEARCH_CONTAINER, null, null);
                }
            }, AuthenticationUtil.getSystemUserName());            
        }
        
        // Get the private container for the current user
        if (savedSearchDetails.isPublic() == false)
        {
            final String userName = AuthenticationUtil.getFullyAuthenticatedUser();
            NodeRef userContainer = fileFolderService.searchSimple(container, userName);
            if (userContainer == null)
            {
                final NodeRef parentContainer = container;
                userContainer = AuthenticationUtil.runAs(new RunAsWork<NodeRef>()
                {
                    @Override
                    public NodeRef doWork() throws Exception
                    {
                        return fileFolderService.create(parentContainer, userName, ContentModel.TYPE_FOLDER).getNodeRef();
                    }
                }, AuthenticationUtil.getSystemUserName());
            }
            container = userContainer;
        }
        
        // Get the saved search node
        NodeRef searchNode = fileFolderService.searchSimple(container, savedSearchDetails.getName());
        if (searchNode == null)
        {
            final NodeRef searchContainer = container;
            searchNode = AuthenticationUtil.runAs(new RunAsWork<NodeRef>()
            {
                @Override
                public NodeRef doWork() throws Exception
                {
                    return fileFolderService.create(searchContainer, savedSearchDetails.getName(), ContentModel.TYPE_CONTENT).getNodeRef();
                }
            }, AuthenticationUtil.getSystemUserName());
        }
        
        // Write the JSON content to search node
        final NodeRef writableSearchNode = searchNode;
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                ContentWriter writer = fileFolderService.getWriter(writableSearchNode);
                writer.setEncoding("UTF-8");
                writer.setMimetype(MimetypeMap.MIMETYPE_JSON);
                writer.putContent(savedSearchDetails.toJSONString());
                
                return null;
            }
        }, AuthenticationUtil.getSystemUserName());
        
        return savedSearchDetails;
    }

	/**
	 * @see org.alfresco.module.org_alfresco_module_dod5015.search.RecordsManagementSearchService#deleteSavedSearch(org.alfresco.module.org_alfresco_module_dod5015.search.SavedSearchDetails)
	 */
    @Override
    public void deleteSavedSearch(SavedSearchDetails savedSearchDetails)
    {
        // Check the parameters
        ParameterCheck.mandatory("savedSearchDetails", savedSearchDetails);
        
        // Get the search node for the saved query
        NodeRef searchNode = getSearchNodeRef(savedSearchDetails.getSiteId(), savedSearchDetails.getName());
        if (searchNode != null && fileFolderService.exists(searchNode) == true)
        {
            fileFolderService.delete(searchNode);
        }        
    }
    
    /**
     * 
     * @param siteId
     * @param name
     * @return
     */
    private NodeRef getSearchNodeRef(String siteId, String name)
    {
        NodeRef searchNode = null;
        
        // Get the root saved search container       
        NodeRef container = siteService.getContainer(siteId, SEARCH_CONTAINER);
        if (container != null)
        {         
            // try and find the search node
            searchNode = fileFolderService.searchSimple(container, name);
            
            // can't find it so check the users container
            if (searchNode == null)
            {
                String userName = AuthenticationUtil.getFullyAuthenticatedUser();
                NodeRef userContainer = fileFolderService.searchSimple(container, userName);
                if (userContainer != null)
                {
                    searchNode = fileFolderService.searchSimple(userContainer, name);
                }
            }
        }
        
        return searchNode;  
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.search.RecordsManagementSearchService#addReports(java.lang.String)
     */
    @Override
    public void addReports(String siteId)
    {
        try
        {
            for (ReportDetails report : reports)
            {
                String params = "terms=" + URLEncoder.encode(report.getQuery(), "UTF-8");
                String fullQuery = buildQueryString(report.getQuery(), null, null);
                
                // Create saved search details
                SavedSearchDetails savedSearchDetails = new SavedSearchDetails(
                                                                siteId, 
                                                                report.getName(), 
                                                                report.getDescription(), 
                                                                fullQuery, 
                                                                report.getSort(),
                                                                params,
                                                                true, 
                                                                true);
                
                // Save search details
                saveSearch(savedSearchDetails);
            }
        }
        catch (UnsupportedEncodingException exception)
        {
            throw new AlfrescoRuntimeException("Unable to add reports to site " + siteId, exception);
        }
    }
}
