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

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Records management search service.
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementSearchService 
{
	/**
	 * Execute a records management search
	 * @param query	search query string
	 * @return {@link List}<{@link NodeRef}> search results 
	 */
	// TODO introduce paging into search API??	
	List<NodeRef> search(String query);	
	
	/**
	 * 
	 * @param siteId
	 * @return
	 */
	List<SavedSearchDetails> getSavedSearches(String siteId);
	
	/**
	 * 
	 * @param siteId
	 * @param name
	 * @return
	 */
	SavedSearchDetails getSavedSearch(String siteId, String name);
	
	/**
     * 
     * @param siteId
     * @param name
     * @param description
     * @param query
     * @param isPublic
     * @return
     */
    SavedSearchDetails saveSearch(String siteId, String name, String description, String query, boolean isPublic);
	
	/**
	 * 
	 * @param savedSearchDetails
	 * @return
	 */
	SavedSearchDetails saveSearch(SavedSearchDetails savedSearchDetails);
	
	/**
	 * 
	 * @param siteId
	 * @param name
	 */
	void deleteSavedSearch(SavedSearchDetails savedSearchDetails);
}
