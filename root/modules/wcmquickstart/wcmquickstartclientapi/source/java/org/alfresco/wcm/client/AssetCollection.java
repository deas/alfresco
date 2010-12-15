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
package org.alfresco.wcm.client;

import java.io.Serializable;
import java.util.List;

/**
 * A collection of assets with meta-data for the collection itself.
 * 
 * @author Chris Lack
 */
public interface AssetCollection extends Serializable 
{
	/**
	 * The id of the item
	 * @return String item id
	 */
	String getId();
	
	/**
	 * The name of the item
	 * @return String item name
	 */
	String getName();
	
	/**
	 * The title of the item
	 * @return String item title
	 */
	String getTitle();	
	
	/**
	 * The description
	 * @return String description
	 */
	String getDescription();

	/**
	 * Get the collection of assets
	 * 
	 * @return List<Asset> the wrapped collection
	 */
	List<Asset> getAssets();
	
    /**
     * Obtain the total results count.
     * This is the total number of results that the query returned before any pagination filters were applied.
     * @return
     */
    long getTotalSize();
    
    /**
     * Obtain the number of results held by this object.
     * @return
     */
    long getSize();

    /**
     * Obtain the query that was executed to return these results.
     * @return
     */
    Query getQuery();	
}