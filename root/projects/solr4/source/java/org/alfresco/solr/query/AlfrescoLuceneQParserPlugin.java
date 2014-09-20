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
package org.alfresco.solr.query;

import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.ContextAwareQuery;
import org.alfresco.util.Pair;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.SyntaxError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andy
 */
public class AlfrescoLuceneQParserPlugin extends QParserPlugin
{
    protected final static Logger log = LoggerFactory.getLogger(AlfrescoLuceneQParserPlugin.class);
    
    /*
     * (non-Javadoc)
     * @see org.apache.solr.search.QParserPlugin#createParser(java.lang.String,
     * org.apache.solr.common.params.SolrParams, org.apache.solr.common.params.SolrParams,
     * org.apache.solr.request.SolrQueryRequest)
     */
    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
    {
        return new AlfrescoLuceneQParser(qstr, localParams, params, req);

    }

    /*
     * (non-Javadoc)
     * @see org.apache.solr.util.plugin.NamedListInitializedPlugin#init(org.apache.solr.common.util.NamedList)
     */
    public void init(NamedList arg0)
    {
    }

    public static class AlfrescoLuceneQParser extends AbstractQParser
    {
        public AlfrescoLuceneQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
        {
            super(qstr, localParams, params, req);
        }

        /*
         * (non-Javadoc)
         * @see org.apache.solr.search.QParser#parse()
         */
        @Override
        public Query parse() throws SyntaxError
        {
            Pair<SearchParameters, Boolean> searchParametersAndFilter = getSearchParameters();
            SearchParameters searchParameters = searchParametersAndFilter.getFirst();
            Boolean isFilter = searchParametersAndFilter.getSecond();
            
            Solr4QueryParser qp = AlfrescoSolrDataModel.getInstance().getLuceneQueryParser(searchParameters, req);
            Query query;
            try
            {
                // escape / not in a string and not already escaped
                query = qp.parse(searchParameters.getQuery().replaceAll("([^\\\\])/(?=([^\"\\\\]*(\\\\.|\"([^\"\\\\]*\\\\.)*[^\"\\\\]*\"))*[^\"]*$)", "$1\\\\/"));
            }
            catch (ParseException pe)
            {
                throw new SyntaxError(pe);
            }
            ContextAwareQuery contextAwareQuery = new ContextAwareQuery(query, Boolean.TRUE.equals(isFilter) ? null : searchParameters);
            if(log.isDebugEnabled())
            {
                log.debug("Lucene QP query as lucene:\t    "+contextAwareQuery);
            }
            return contextAwareQuery;
        }
    }

}
