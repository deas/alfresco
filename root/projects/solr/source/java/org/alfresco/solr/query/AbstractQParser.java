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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Locale;

import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchParameters.Operator;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.namespace.NamespaceService;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author Andy
 *
 */
public abstract class AbstractQParser extends QParser
{
    protected final static Logger log = LoggerFactory.getLogger(AbstractQParser.class);

    private static final String ALFRESCO_JSON = "ALFRESCO_JSON";

    private static final String AUTHORITY_FILTER_FROM_JSON = "AUTHORITY_FILTER_FROM_JSON";
    
    private static final String TENANT_FILTER_FROM_JSON = "TENANT_FILTER_FROM_JSON";
    
    /**
     * @param qstr
     * @param localParams
     * @param params
     * @param req
     */
    public AbstractQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req)
    {
        super(qstr, localParams, params, req);
    }
    
    protected SearchParameters getSearchParameters()
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
                    ArrayList<String> tenantList = new ArrayList<String>(1);
                    JSONArray tenants = json.getJSONArray("tenants");
                    for (int i = 0; i < tenants.length(); i++)
                    {
                        String tenantString = tenants.getString(i);
                        tenantList.add(tenantString);
                    }
                    
                    ArrayList<String> authorityList = new ArrayList<String>(1);
                    JSONArray authorities = json.getJSONArray("authorities");
                    for (int i = 0; i < authorities.length(); i++)
                    {
                        String authorityString = authorities.getString(i);
                        authorityList.add(authorityString);
                    }
                    
                    StringBuilder authQuery = new StringBuilder();
                    for(String tenant : tenantList)
                    {
                        for(String authority : authorityList)
                        {
                            if (authQuery.length() > 0)
                            {
                                authQuery.append(" ");
                            }
                            switch(AuthorityType.getAuthorityType(authority))
                            {
                            case USER:
                                authQuery.append("|AUTHORITY:\"").append(authority).append("\"");
                                break;
                            case GROUP:
                            case EVERYONE:
                            case GUEST:
                                if(tenant.length() == 0)
                                {
                                    // Default tenant matches 4.0 
                                    authQuery.append("|AUTHORITY:\"").append(authority).append("\"");
                                }
                                else
                                {
                                    authQuery.append("|AUTHORITY:\"").append(authority).append("@").append(tenant).append("\"");
                                }
                                break;
                            default:
                                authQuery.append("|AUTHORITY:\"").append(authority).append("\"");
                                break;
                            }
                            
                        }
                    }
                    
                    if (authQuery.length() > 0)
                    {
                        searchParameters.setQuery(authQuery.toString());
                    }
                }
                else if (getString().equals(TENANT_FILTER_FROM_JSON))
                {
                    ArrayList<String> tenantList = new ArrayList<String>(1);
                    JSONArray tenants = json.getJSONArray("tenants");
                    for (int i = 0; i < tenants.length(); i++)
                    {
                        String tenantString = tenants.getString(i);
                        tenantList.add(tenantString);
                    }
                    
                    StringBuilder tenantQuery = new StringBuilder();
                    for(String tenant : tenantList)
                    {
                        if (tenantQuery.length() > 0)
                        {
                            tenantQuery.append(" ");
                        }
                        
                        if(tenant.length() > 0)

                        {
                            tenantQuery.append("|TENANT:\"").append(tenant).append("\"");
                        }
                        else
                        {
                            // TODO: Need to check for the default tenant or no tenant (4.0) or we force a reindex requirement later ...
                            // Better to add default tenant to the 4.0 index
                            tenantQuery.append("|TENANT:\"").append("_DEFAULT_").append("\"");
                            //tenantQuery.append(" |(+ISNODE:T -TENANT:*)");
                        }
                       
                    }
                    searchParameters.setQuery(tenantQuery.toString());
  
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
            if(log.isDebugEnabled())
            {
                log.debug(json.toString());
            }
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

        // searchParameters.setMlAnalaysisMode(getMLAnalysisMode());
        searchParameters.setNamespace(NamespaceService.CONTENT_MODEL_1_0_URI);
        
        return searchParameters;
    }
}
