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


/**
 * Report details.
 *            
 * @author Roy Wetherall
 */
public class ReportDetails 
{
    /** Name */
	protected String name;
	
	/** Description */
	protected String description;
	
	/** Query */
	protected String query;
	
	/** Sort */
	protected String sort;
	
	/** Params */
	protected String params;

	/**
	 * @param name         name 
	 * @param description  description string
	 * @param query        query string
	 */
	public ReportDetails(String name, String description, String query, String sort, String params) 
	{
		this.name = name;
		this.description = description;
		this.query = query;
		this.sort = sort;
		this.params = params;
	}
	
	/**
	 * @return {@link String}  name
	 */
	public String getName() 
	{
		return name;
	}
	
	/**
	 * @return {@link String}  description
	 */
	public String getDescription() 
	{
		return description;
	}

	/**
	 * @param description  description
	 */
	public void setDescription(String description) 
	{
		this.description = description;
	}

	/**
	 * @return {@link String}  query string
	 */
	public String getQuery()
    {
        return query;
    }
	
	/**
	 * @param query query string
	 */
	public void setQuery(String query)
    {
        this.query = query;
    }	
	
	/**
	 * @return {@link String}  sort string 
	 */
	public String getSort()
    {
        return sort;
    }
	
	/**
	 * @param sort
	 */
	public void setSort(String sort)
    {
        this.sort = sort;
    }
	
	/**
	 * @return
	 */
	public String getParams()
    {
        return params;
    }
	
	/**
	 * @param params
	 */
	public void setParams(String params)
    {
        this.params = params;
    }
}
