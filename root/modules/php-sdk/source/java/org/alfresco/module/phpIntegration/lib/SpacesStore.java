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
package org.alfresco.module.phpIntegration.lib;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;

/**
 * @author Roy Wetherall
 */
public class SpacesStore extends Store implements ScriptObject
{
    private static final String SCRIPT_OBJECT_NAME = "SpacesStore";
    
    public SpacesStore(Session session)
    {
        super(session, "SpacesStore", StoreRef.PROTOCOL_WORKSPACE);
    }
    
    @Override
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    public Node getCompanyHome()
    {
    	return this.session.doSessionWork(new SessionWork<Node>()
    	{
			public Node doWork() 
			{
		        SearchService searchService = SpacesStore.this.session.getServiceRegistry().getSearchService();
		        ResultSet resultSet = searchService.query(SpacesStore.this.storeRef, SearchService.LANGUAGE_LUCENE, "PATH:\"app:company_home\"");
		        NodeRef companyHome = resultSet.getNodeRef(0);
		        resultSet.close();
		        return new Node(session, companyHome);
			}
    	});
    }

}
