/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.solr.content;

import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that wraps up the creation of SOLR content URLs given arbitrary,
 * string-based metadata.
 * 
 * @author Derek Hulley
 * @since 5.0
 */
public class SolrContentUrlBuilder
{
    /**
     * <b>solr</b> is the prefix for SOLR content URLs
     * @see #isContentUrlSupported(String)
     */
    public static final String STORE_PROTOCOL = "store";
    
    protected final static Logger log = LoggerFactory.getLogger(SolrContentUrlBuilder.class);
    
    /** Metadata ordered by key */
    private final TreeMap<String, String> metadata;
    
    /**
     * Protected constructor used by {@link SolrContentUrlBuilder#start()}
     */
    protected SolrContentUrlBuilder()
    {
        this.metadata = new TreeMap<String, String>();
    }
    
    /**
     * Factory method to start building the SOLR content URL.
     * 
     * @return              an instance of the builder
     */
    public static SolrContentUrlBuilder start()
    {
        return new SolrContentUrlBuilder();
    }
    
    /**
     * Add some metadata to the URL generator.  The order in which metadata is added is irrelevant.
     * 
     * @param key           an arbitrary metadata key (never <tt>null</tt>>
     * @param value         some metadata value (<tt>null</tt> is supported)
     * @return              this builder for more building
     * 
     * @throws              IllegalArgumentException if the key is null
     * @throws              IllegalStateException if the key has been used already
     */
    public synchronized SolrContentUrlBuilder add(String key, String value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("The metadata 'key' may not be null.");
        }
        String previous = metadata.put(key, value);
        if (previous != null)
        {
            throw new IllegalStateException("The metadata key, '" + key + "', has already been used.");
        }
        return this;
    }
}
