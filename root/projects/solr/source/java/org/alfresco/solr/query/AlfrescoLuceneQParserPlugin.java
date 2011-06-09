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
package org.alfresco.solr.query;

import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.QueryParsing;

/**
 * @author Andy
 */
public class AlfrescoLuceneQParserPlugin extends QParserPlugin
{

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

    public static class AlfrescoLuceneQParser extends QParser
    {

        AbstractLuceneQueryParser lqp;

        public AlfrescoLuceneQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
        {
            super(qstr, localParams, params, req);
        }

        /*
         * (non-Javadoc)
         * @see org.apache.solr.search.QParser#parse()
         */
        @Override
        public Query parse() throws ParseException
        {
            String qstr = getString();

            String defaultField = getParam(CommonParams.DF);
            if (defaultField==null) {
              defaultField = getReq().getSchema().getDefaultSearchFieldName();
            }

            // these could either be checked & set here, or in the SolrQueryParser constructor
            QueryParser.Operator defaultOperator;
            String opParam = getParam(QueryParsing.OP);
            if (opParam != null) 
            {
              if("AND".equals(opParam))
                      {
                  defaultOperator = QueryParser.Operator.AND ;
                      }
              else
              {
                  defaultOperator =  QueryParser.Operator.OR;
              }
            } 
            else 
            {
              // try to get default operator from schema
              defaultOperator = getReq().getSchema().getSolrQueryParser(null).getDefaultOperator();
            }

            String id =  req.getSchema().getResourceLoader().getInstanceDir();
            IndexReader indexReader = req.getSearcher().getIndexReader();
            AbstractLuceneQueryParser lqp = AlfrescoSolrDataModel.getInstance(id).getLuceneQueryParser(defaultField, qstr, defaultOperator, indexReader);
            return lqp.parse(qstr);
        }
    }

}
