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
package org.alfresco.solr.component;

import java.io.IOException;
import java.util.HashMap;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;

/**
 * @author Andy
 *
 */
public class RewriteFacetCountsComponent extends SearchComponent
{

    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#prepare(org.apache.solr.handler.component.ResponseBuilder)
     */
    @Override
    public void prepare(ResponseBuilder rb) throws IOException
    {
       // Nothing to do
    }

    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#process(org.apache.solr.handler.component.ResponseBuilder)
     */
    @Override
    public void process(ResponseBuilder rb) throws IOException
    {
        // rewrite

        rewrite(rb, "_field_mappings_", "facet_counts", "facet_fields");
        rewrite(rb, "_date_mappings_", "facet_counts", "facet_dates");
        rewrite(rb, "_range_mappings_", "facet_counts", "facet_ranges");
        
        //rewrite(rb, "_pivot_mappings_", "facet_counts", "facet_fields");
        // TODO: rewrite(rb, "_interval_mappings_", "facet_counts", "facet_fields");
        
        rewrite(rb, "_stats_field_mappings_", "stats", "stats_fields");
        
        HashMap<String, String> mappings = (HashMap<String, String>)rb.rsp.getValues().get("_stats_field_mappings_");
        if(mappings != null)
        {
            for(String key : mappings.keySet())
            {
                rewrite(rb, "_stats_facet_mappings_", "stats", "stats_fields", key, "facets");
            }
        }
        

    }

    /**
     * @param rb
     */
    private void rewrite(ResponseBuilder rb, String mappingName, String ... sections)
    {
        HashMap<String, String> mappings = (HashMap<String, String>)rb.rsp.getValues().get(mappingName);
        if(mappings != null)
        {
            HashMap<String, String> reverse = getReverseLookUp(mappings);
     
            NamedList<Object>  found = (NamedList<Object>) rb.rsp.getValues();
            for(String section : sections)
            {
                found = (NamedList<Object>)found.get(section);
                if(found == null)
                {
                    return;
                }
            }
            
            for(int i = 0; i < found.size(); i++)
            {
                String name = found.getName(i);
                String newName = reverse.get(name);
                if(newName != null)
                {
                    found.setName(i, newName);
                }
            }
            
          
        }
    }

    
    private HashMap<String, String> getReverseLookUp(HashMap<String, String> map)
    {
        HashMap<String, String> reverse = new HashMap<String, String>();
        for(String key : map.keySet())
        {
            String value = map.get(key);
            reverse.put(value,  key);
        }
        return reverse;
    }

    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#getDescription()
     */
    @Override
    public String getDescription()
    {
        return "RewriteFacetCounts";
    }

    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#getSource()
     */
    @Override
    public String getSource()
    {
        return "";
    }

}
