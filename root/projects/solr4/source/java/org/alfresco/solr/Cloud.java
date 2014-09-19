/*
 * Copyright (C) 2014 Alfresco Software Limited.
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

package org.alfresco.solr;

import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.ResultContext;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.DocList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cloud consists of support methods for running solr queries in the cloud.
 * 
 * @author Ahmed Owian
 */
public class Cloud
{
    protected final static Logger log = LoggerFactory.getLogger(Cloud.class);

    @SuppressWarnings("rawtypes")
    String getQuery(String fieldName, String operator, Collection... valueLists)
    {
        StringBuilder query = new StringBuilder();
        for (Collection values : valueLists)
        {
            for (Object value : values)
            {
                query.append(fieldName).append(":").append(value.toString()).append(operator);
            }
        }

        if (query.length() >= operator.length())
        {
            // Removes the last appended operator from the query string
            query.delete(query.length() - operator.length(), query.length());
        }

        return query.toString();
    }

    boolean selectReturnsDoc(SolrCore core, SolrQueryRequest request, String query)
    {
        ModifiableSolrParams params = new ModifiableSolrParams(request.getParams());
        // Sets 1 because this is effectively a boolean query to see if there exists a single match
        params.set("q", query).set("fl", "id").set("rows", "1");
        ResultContext rc = this.select(core, request, params);

        if (rc != null)
        {
            if (rc.docs != null) { return rc.docs.iterator().hasNext(); }
        }

        return false;
    }

    DocList getDocList(SolrCore core, SolrQueryRequest request, String query)
    {
        ModifiableSolrParams params = new ModifiableSolrParams(request.getParams());
        // Sets MAX_VALUE to get all the rows
        params.set("q", query).set("fl", "id").set("rows", Integer.MAX_VALUE);
        ResultContext rc = this.select(core, request, params);
        return rc != null ? rc.docs : null;
    }

    /**
     * Populates a BytesRef field result with the field value.
     * @param request a live solr request
     * @param field the name of the field
     * @param docId the id of the solr doc
     * @param result the reference to the bytes to hold the field value
     * @return <code>true</code> if the field result is populated
     * @throws IOException
     */
    boolean populateFieldResult(SolrQueryRequest request, String field, int docId, BytesRef result) throws IOException
    {
        SortedSetDocValues ssdv = request.getSearcher().getAtomicReader().getSortedSetDocValues(field);
        ssdv.setDocument(docId);
        long ordinal = ssdv.nextOrd();
        boolean populateResult = ordinal != SortedSetDocValues.NO_MORE_ORDS;
        if (populateResult)
        {
            ssdv.lookupOrd(ordinal, result);
        }

        return populateResult;
    }

    ResultContext select(SolrCore core, SolrQueryRequest request, SolrParams params)
    {
        SolrRequestHandler requestHandler = core.getRequestHandler("/select");
        request.setParams(params);
        log.info("Running query " + params.get("q"));
        SolrQueryResponse solrRsp = new SolrQueryResponse();
        requestHandler.handleRequest(request, solrRsp);
        ResultContext rc = (ResultContext) solrRsp.getValues().get("response");

        return rc;
    }
}