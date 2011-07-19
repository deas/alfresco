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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchParameters.Operator;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.QueryParsing;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author Andy
 */
public class AlfrescoLuceneQParserPlugin extends QParserPlugin
{

    private static final String ALFRESCO_JSON = "ALFRESCO_JSON";

    private static final String AUTHORITY_FILTER_FROM_JSON = "AUTHORITY_FILTER_FROM_JSON";
    
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
            SearchParameters searchParameters = new SearchParameters();

            Iterable<ContentStream> streams = req.getContentStreams();

            JSONObject json = (JSONObject) req.getContext().get(ALFRESCO_JSON);

            if (json == null)
            {
                if (streams != null)
                {

                    try
                    {
                        Reader reader = null;
                        for (ContentStream stream : streams)
                        {
                            reader = new BufferedReader(new InputStreamReader(stream.getStream(), "UTF-8"));
                        }

                        // TODO - replace with streaming-based solution e.g. SimpleJSON ContentHandler
                        if (reader != null)
                        {
                            json = new JSONObject(new JSONTokener(reader));
                            req.getContext().put(ALFRESCO_JSON, json);
                        }
                    }
                    catch (JSONException e)
                    {
                        // This is expected when there is no json element to the request
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            if (json != null)
            {
                try
                {
                    if (getString().equals(AUTHORITY_FILTER_FROM_JSON))
                    {
                        String filter = json.getString("filter");
                        if (filter != null)
                        {
                            searchParameters.setQuery(filter);
                        }
                    }
                    else
                    {
                        String query = json.getString("query");
                        if (query != null)
                        {
                            searchParameters.setQuery(query);
                        }
                    }

                    JSONArray locales = json.getJSONArray("locales");
                    for (int i = 0; i < locales.length(); i++)
                    {
                        String localeString = locales.getString(i);
                        Locale locale = DefaultTypeConverter.INSTANCE.convert(Locale.class, localeString);
                        searchParameters.addLocale(locale);
                    }
                   
                    JSONArray templates = json.getJSONArray("templates");
                    for (int i = 0; i < templates.length(); i++)
                    {
                        JSONObject template = templates.getJSONObject(i);
                        String name = template.getString("name");
                        String queryTemplate = template.getString("template");
                        searchParameters.addQueryTemplate(name, queryTemplate);
                    }

                    JSONArray allAttributes = json.getJSONArray("allAttributes");
                    for (int i = 0; i < allAttributes.length(); i++)
                    {
                        String allAttribute = allAttributes.getString(i);
                        searchParameters.addAllAttribute(allAttribute);
                    }

                    searchParameters.setDefaultFTSOperator(Operator.valueOf(json.getString("defaultFTSOperator")));
                    searchParameters.setDefaultFTSFieldConnective(Operator.valueOf(json.getString("defaultFTSFieldOperator")));
                    if (json.has("mlAnalaysisMode"))
                    {
                        searchParameters.setMlAnalaysisMode(MLAnalysisMode.valueOf(json.getString("mlAnalaysisMode")));
                    }
                    searchParameters.setNamespace(json.getString("defaultNamespace"));

                    JSONArray textAttributes = json.getJSONArray("textAttributes");
                    for (int i = 0; i < textAttributes.length(); i++)
                    {
                        String textAttribute = textAttributes.getString(i);
                        searchParameters.addAllAttribute(textAttribute);
                    }

                }
                catch (JSONException e)
                {
                    // This is expected when there is no json element to the request
                }
            }

            if(json != null)
            {
                System.out.println(json.toString());
            }
            
            if (searchParameters.getQuery() == null)
            {
                searchParameters.setQuery(getString());
            }

            if(searchParameters.getLocales().size() == 0)
            {
                searchParameters.addLocale(I18NUtil.getLocale());
            }
            
            String defaultField = getParam(CommonParams.DF);
            if(defaultField != null)
            {
                searchParameters.setDefaultFieldName(defaultField);
            }

            // these could either be checked & set here, or in the SolrQueryParser constructor

            String id = req.getSchema().getResourceLoader().getInstanceDir();
            IndexReader indexReader = req.getSearcher().getIndexReader();

            // searchParameters.setMlAnalaysisMode(getMLAnalysisMode());
            searchParameters.setNamespace(NamespaceService.CONTENT_MODEL_1_0_URI);
            
            
            AbstractLuceneQueryParser lqp = AlfrescoSolrDataModel.getInstance(id).getLuceneQueryParser(searchParameters, indexReader);
            return lqp.parse(qstr);
        }
    }

}
