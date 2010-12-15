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
package org.alfresco.wcm.client.impl;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.Query;

/**
 * Collection of assets with meta-data for the collection itself.
 * @author Chris Lack
 */
public class AssetCollectionImpl implements AssetCollection
{

	private static final long serialVersionUID = 1L;
	
	/** Id */
	private String id;
	
	/** Name */
	private String name;
	
	/** Title */
	private String title;
	
	/** Description */
	private String description;

	/** The wrapped collection */
	protected List<Asset> assets = new ArrayList<Asset>();
	
	/** Pagination details */
    private Query query;
    private long totalSize;	

	/**
	 *  @see org.alfresco.wcm.client.AssetCollection#getId()
	 */
	@Override
	public String getId() 
	{
		return id;
	}

	public void setId(String value) 
	{
		this.id = value;
	}	
	
	/**
	 *  @see org.alfresco.wcm.client.AssetCollection#getName()
	 */	
	@Override
	public String getName() 
	{
		return name;
	}

	public void setName(String value) 
	{
		this.name = value;
	}
	
	/**
	 *  @see org.alfresco.wcm.client.AssetCollection#getTitle()
	 */	
	@Override
	public String getTitle() 
	{
		return title;
	}

	public void setTitle(String value) 
	{
		this.title = value;
	}	

	/**
	 *  @see org.alfresco.wcm.client.AssetCollection#getDescription()
	 */	
	@Override
	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String value) 
	{
		this.description = value;
	}
	
	/**
	 *  @see org.alfresco.wcm.client.AssetCollection#getAssets()
	 */	
	@Override
	public List<Asset> getAssets()
	{
		return assets;
	}

	public void setAssets(List<Asset> assets)
	{
		this.assets = assets;
	}

	/**
	 * Add a single asset to the collection.
	 * @param asset asset object
	 */
	public void add(Asset asset)
	{
		this.assets.add(asset);
	}

    @Override
    public Query getQuery()
    {
        return query;
    }

    @Override
    public long getSize()
    {
        return assets.size();
    }

    @Override
    public long getTotalSize()
    {
        return totalSize;
    }

    public void setQuery(Query query)
    {
        this.query = query;
    }

    public void setTotalSize(long totalNumItems)
    {
        this.totalSize = totalNumItems;
    }
}
