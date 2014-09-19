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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.AlfrescoSolrDataModel.FieldUse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;


/**
 * @author Andy
 *
 */
public class RewriteFacetParametersComponent extends SearchComponent
{

    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#prepare(org.apache.solr.handler.component.ResponseBuilder)
     */
    @Override
    public void prepare(ResponseBuilder rb) throws IOException
    {
        SolrQueryRequest req = rb.req;
        SolrParams params = req.getParams();
        
        ModifiableSolrParams fixed = new ModifiableSolrParams();
        fixFacetParams(fixed, params, rb);
        req.setParams(fixed);
       
    }

    
    /**
     * @param params
     * @return
     */
    private SolrParams fixFacetParams(ModifiableSolrParams fixed, SolrParams params, ResponseBuilder rb)
    {
        HashMap<String, String> fieldMappings = new HashMap<>();
        HashMap<String, String> dateMappings = new HashMap<>();
        HashMap<String, String> rangeMappings = new HashMap<>();
        HashMap<String, String> pivotMappings = new HashMap<>();
        HashMap<String, String> intervalMappings = new HashMap<>();
        
        HashMap<String, String> statsFieldMappings = new HashMap<>();
        HashMap<String, String> statsFacetMappings = new HashMap<>();
       
        
        rewriteFacetFieldList(fixed, params, "facet.field", fieldMappings);
        rewriteFacetFieldList(fixed, params, "facet.date", dateMappings);
        rewriteFacetFieldList(fixed, params, "facet.range", rangeMappings);
        rewriteFacetFieldList(fixed, params, "facet.pivot", pivotMappings);
        rewriteFacetFieldList(fixed, params, "facet.interval", intervalMappings);
        
        rewriteFacetFieldList(fixed, params, "stats.field", statsFieldMappings);
        rewriteFacetFieldList(fixed, params, "stats.facet", statsFacetMappings);
        
        rewriteFacetFieldOptions(fixed, params, "facet.field", fieldMappings);
        rewriteFacetFieldOptions(fixed, params, "facet.date", dateMappings);
        rewriteFacetFieldOptions(fixed, params, "facet.range", rangeMappings);
        rewriteFacetFieldOptions(fixed, params, "facet.pivot", pivotMappings);
        rewriteFacetFieldOptions(fixed, params, "facet.interval", intervalMappings);
        
        // TODO: 
        //    f.<stats_field>.stats.facet=<new Field> 
        //    would require a more complex rewrite  
        
        copyNonFacetParams(fixed, params);
        
        rb.rsp.add("_original_parameters_", params);
        rb.rsp.add("_field_mappings_", fieldMappings);
        rb.rsp.add("_date_mappings_", dateMappings);
        rb.rsp.add("_range_mappings_", rangeMappings);
        rb.rsp.add("_pivot_mappings_", pivotMappings);
        rb.rsp.add("_interval_mappings_", intervalMappings);
        rb.rsp.add("_stats_field_mappings_", statsFieldMappings);
        rb.rsp.add("_stats_facet_mappings_", statsFacetMappings);
        
        return fixed;
    }


    /**
     * @param fixed
     * @param params
     */
    private void copyNonFacetParams(ModifiableSolrParams fixed, SolrParams params)
    {
        for(Iterator<String> it = params.getParameterNamesIterator(); it.hasNext(); /**/)
        {
            String name = it.next();
            if(name.startsWith("f.") || name.startsWith("facet.field") || name.startsWith("facet.date") || name.startsWith("facet.range") || name.startsWith("facet.pivot") || name.startsWith("facet.interval")|| name.startsWith("stats."))
            {
                // Already done 
                continue;
            }    
            else
            {
                fixed.add(name, params.getParams(name));
            }
        }
    }


    /**
     * @param fixed
     * @param params
     */
    private void rewriteFacetFieldOptions(ModifiableSolrParams fixed, SolrParams params, String paramName, HashMap<String, String> fieldMappings)
    {
        for(Iterator<String> it = params.getParameterNamesIterator(); it.hasNext(); /**/)
        {
            String name = it.next();
            if(name.startsWith("f."))
            {
                int index = name.indexOf("."+paramName, 2);
                if(index > -1)
                {
                    String source = name.substring(2, index);
                    if(fieldMappings.containsKey(source))
                    {
                        fixed.add("f."+fieldMappings.get(source)+name.substring(index), params.getParams(name));
                    }
                    else
                    {
                        fixed.add(name, params.getParams(name));
                    }
                }
                else
                {
                    fixed.add(name, params.getParams(name));
                }
            }       
        }
    }


    /**
     * @param fixed
     * @param params
     */
    private void rewriteFacetFieldList(ModifiableSolrParams fixed, SolrParams params, String paramName, HashMap<String, String> fieldMappings)
    {
        String[] facetFieldsOrig = params.getParams(paramName);
        if(facetFieldsOrig != null)
        {
            ArrayList<String> newFacetFields = new ArrayList<String>();
            for(String facetField : facetFieldsOrig)
            {
                String mappedField = AlfrescoSolrDataModel.getInstance().mapProperty(facetField, FieldUse.FACET);
                if(!mappedField.equals(facetField))
                {
                    fieldMappings.put(facetField, mappedField);
                }
                newFacetFields.add(mappedField);
            }
            fixed.add(paramName,  newFacetFields.toArray(new String[newFacetFields.size()]));
        }
    }

    
    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#process(org.apache.solr.handler.component.ResponseBuilder)
     */
    @Override
    public void process(ResponseBuilder rb) throws IOException
    {
       // Nothing to do
    }

    /* (non-Javadoc)
     * @see org.apache.solr.handler.component.SearchComponent#getDescription()
     */
    @Override
    public String getDescription()
    {
        return "RewriteFacetParameters";
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
