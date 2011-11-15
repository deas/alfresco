/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

import org.alfresco.opencmis.search.CMISQueryOptions.CMISQueryMode;
import org.alfresco.repo.search.impl.querymodel.Order;
import org.alfresco.repo.search.impl.querymodel.Ordering;
import org.alfresco.repo.search.impl.querymodel.PropertyArgument;
import org.alfresco.repo.search.impl.querymodel.impl.functions.PropertyAccessor;
import org.alfresco.repo.search.impl.querymodel.impl.functions.Score;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andy
 */
public class CmisQParserPlugin extends QParserPlugin
{
    protected final static Logger log = LoggerFactory.getLogger(CmisQParserPlugin.class);

    /*
     * (non-Javadoc)
     * @see org.apache.solr.search.QParserPlugin#createParser(java.lang.String,
     * org.apache.solr.common.params.SolrParams, org.apache.solr.common.params.SolrParams,
     * org.apache.solr.request.SolrQueryRequest)
     */
    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
    {
        return new CmisQParser(qstr, localParams, params, req);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.solr.util.plugin.NamedListInitializedPlugin#init(org.apache.solr.common.util.NamedList)
     */
    public void init(NamedList arg0)
    {
    }

    public static class CmisQParser extends AbstractQParser
    {
        public CmisQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
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
            SearchParameters searchParameters = getSearchParameters();
            // these could either be checked & set here, or in the SolrQueryParser constructor

            String id = req.getSchema().getResourceLoader().getInstanceDir();
            IndexReader indexReader = req.getSearcher().getIndexReader();

            org.alfresco.repo.search.impl.querymodel.Query queryModelQuery = AlfrescoSolrDataModel.getInstance(id).parseCMISQueryToAlfrescoAbstractQuery(CMISQueryMode.CMS_WITH_ALFRESCO_EXTENSIONS, searchParameters, indexReader);
            
            // build the sort param and update the params on the request if required .....
            
            if ((queryModelQuery.getOrderings() != null) && (queryModelQuery.getOrderings().size() > 0))
            {
                StringBuilder sortParameter = new StringBuilder();

                for (Ordering ordering : queryModelQuery.getOrderings())
                {
                    if (ordering.getColumn().getFunction().getName().equals(PropertyAccessor.NAME))
                    {
                        PropertyArgument property = (PropertyArgument) ordering.getColumn().getFunctionArguments().get(PropertyAccessor.ARG_PROPERTY);

                        if (property == null)
                        {
                            throw new IllegalStateException();
                        }

                        String propertyName = property.getPropertyName();

                        String luceneField =  AlfrescoSolrDataModel.getInstance(id).getCMISFunctionEvaluationContext(CMISQueryMode.CMS_WITH_ALFRESCO_EXTENSIONS).getLuceneFieldName(propertyName);

                        if(sortParameter.length() > 0)
                        {
                            sortParameter.append(", ");
                        }
                        sortParameter.append(luceneField).append(" ");
                        if(ordering.getOrder() == Order.DESCENDING)
                        {
                            sortParameter.append("desc");
                        }
                        else
                        {
                            sortParameter.append("asc");
                        }
                        
                    }
                    else if (ordering.getColumn().getFunction().getName().equals(Score.NAME))
                    {
                        if(sortParameter.length() > 0)
                        {
                            sortParameter.append(", ");
                        }
                        sortParameter.append("SCORE ");
                        if(ordering.getOrder() == Order.DESCENDING)
                        {
                            sortParameter.append("desc");
                        }
                        else
                        {
                            sortParameter.append("asc");
                        }
                    }
                    else
                    {
                        throw new IllegalStateException();
                    }

                }
                
                // update request params
                
                ModifiableSolrParams newParams = new ModifiableSolrParams(req.getParams());
                newParams.set("sort", sortParameter.toString());
                req.setParams(newParams);
                this.params = newParams;
            }

           
            
            return AlfrescoSolrDataModel.getInstance(id).getCMISQuery(CMISQueryMode.CMS_WITH_ALFRESCO_EXTENSIONS, searchParameters, indexReader, queryModelQuery);
        }
    }

}
