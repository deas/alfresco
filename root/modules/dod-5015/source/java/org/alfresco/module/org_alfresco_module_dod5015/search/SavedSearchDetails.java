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

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Saved search details.
 * 
 * Example format of posted Saved Search JSON:
 * 
 *      {
 *         "siteid" : "rm",
 *         "name": "search name",
 *         "description": "the search description",
 *         "query": "the complete search query string",
 *         "public": boolean,
 *         "params": "terms=keywords:xyz&undeclared=true",
 *         "sort": "cm:name/asc"
 *      }
 *      
 *      where: name and query values are mandatory
 *             params are in URL encoded name/value pair format
 *             sort is in comma separated "property/dir" packed format i.e. "cm:name/asc,cm:title/desc"
 *             
 * @author Roy Wetherall
 */
public class SavedSearchDetails extends ReportDetails
{
    // JSON label values
    public static final String SITE_ID = "siteid";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String QUERY = "query";
    public static final String SORT = "sort";
    public static final String PARAMS = "params";
    public static final String PUBLIC = "public";
    public static final String REPORT = "report";
    
    /** Site id */
	private String siteId;
		
	/** Indicates whether the saved search is public or not */
	private boolean isPublic;
	
	/** Indicates whether the saved search is a report */
	private boolean isReport = false;
	
	/**
	 * 
	 * @param jsonString
	 * @return
	 */
	public static SavedSearchDetails createFromJSON(String jsonString)
	{
	    try
	    {
    	    JSONObject search = new JSONObject(jsonString);
    	    
    	    // Get the site id
    	    if (search.has(SITE_ID) == false)
    	    {
    	        throw new AlfrescoRuntimeException("Can not create saved search details from json, because required siteid is not present. " + jsonString);
    	    }
    	    String siteId = search.getString(SITE_ID);
    	    
    	    // Get the name
    	    if (search.has(NAME) == false)
    	    {
    	        throw new AlfrescoRuntimeException("Can not create saved search details from json, because required name is not present. " + jsonString);
    	    }
    	    String name = search.getString(NAME);
    	    
    	    // Get the description
    	    String description = "";
    	    if (search.has(DESCRIPTION) == true)
    	    {
    	        description = search.getString(DESCRIPTION);
    	    }
    	    
    	    // Get the sort string
    	    String sort = "";
    	    if (search.has(SORT) == true)
    	    {
    	        sort = search.getString(SORT);
    	    }
    	    
    	    // Get the param string
    	    String params = "";
    	    if (search.has(PARAMS) == true)
    	    {
    	        params = search.getString(PARAMS);
    	    }
    	    
    	    // Get the query
    	    if (search.has(QUERY) == false)
    	    {
    	        throw new AlfrescoRuntimeException("Can not create saved search details from json, because required query is not present. " + jsonString);
    	    }
    	    String query = search.getString(QUERY);    	    
    	    
    	    // Determine whether the saved query is public or not
    	    boolean isPublic = false;
    	    if (search.has(PUBLIC) == true)
    	    {
    	        isPublic = search.getBoolean(PUBLIC);
    	    }
    	    
    	    // Determine whether the saved query is a report or not
    	    boolean isReport = false;
    	    if (search.has(REPORT) == true)
    	    {
    	        isReport = search.getBoolean(REPORT);
    	    }
    	    
    	    // Create the saved search details object
    	    return new SavedSearchDetails(siteId, name, description, query, sort, params, isPublic, isReport);    	    
	    }
	    catch (JSONException exception)
	    {
	        throw new AlfrescoRuntimeException("Can not create saved search details from json. " + jsonString, exception);
	    }	    
	}

	/**
	 * @param siteId
	 * @param name
	 * @param description
	 * @param isPublic
	 */
	public SavedSearchDetails(String siteId, String name, String description, String query, String sort, String params, boolean isPublic, boolean isReport) 
	{
	    super(name, description, query, sort, params);
		this.siteId = siteId;
		this.isPublic = isPublic;
		this.isReport = isReport;
	}
	
	/**
	 * @return
	 */
	public String getSiteId() 
	{
		return siteId;
	}	
	
	/**
	 * @return
	 */
	public boolean isPublic() 
	{
		return isPublic;
	}
	
	/**
	 * @return
	 */
	public boolean isReport()
    {
        return isReport;
    }
	
	/**
	 * @return
	 */
	public String toJSONString()
	{
	    try
	    {
    	    JSONObject jsonObject = new JSONObject();
    	    jsonObject.put(SITE_ID, siteId);
    	    jsonObject.put(NAME, name);
    	    jsonObject.put(DESCRIPTION, description);
    	    jsonObject.put(QUERY, query);
    	    jsonObject.put(SORT, sort);
    	    jsonObject.put(PARAMS, params);
    	    jsonObject.put(PUBLIC, isPublic);
    	    return jsonObject.toString();
	    }
	    catch (JSONException exception)
	    {
	        throw new AlfrescoRuntimeException("Can not convert saved search details into JSON.", exception);
	    }
	}
}
