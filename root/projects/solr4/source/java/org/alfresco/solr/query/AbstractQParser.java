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
import java.util.Iterator;
import java.util.Locale;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.opencmis.search.CMISQueryOptions.CMISQueryMode;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.search.impl.QueryParserUtils;
import org.alfresco.repo.search.impl.parsers.AlfrescoFunctionEvaluationContext;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.search.QueryConsistency;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchParameters.Operator;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.AlfrescoSolrDataModel.ContentFieldType;
import org.alfresco.solr.AlfrescoSolrDataModel.FieldUse;
import org.alfresco.solr.AlfrescoSolrDataModel.IndexedField;
import org.alfresco.util.Pair;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SortSpec;
import org.apache.solr.search.SyntaxError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author Andy
 */
public abstract class AbstractQParser extends QParser implements QueryConstants
{
    private static char[] SEPARATORS = new char[] { ':', ',', '-', '!', '+', '=', ';', '~', '/' };

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

    
    protected Pair<SearchParameters, Boolean> getSearchParameters()
    {
        SearchParameters searchParameters = new SearchParameters();
        
        Boolean isFilter = Boolean.FALSE;

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
                    throw new AlfrescoRuntimeException("IO Error parsing query parameters", e);
                }
            }
        }

        if (json != null)
        {
            try
            {
                if (getString() != null)
                {
                    if (getString().equals(AUTHORITY_FILTER_FROM_JSON))
                    {
                        isFilter =Boolean.TRUE;
                        
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

                        char separator = getSeparator(authorityList);

                        StringBuilder authQuery = new StringBuilder();
                        StringBuilder denyQuery = new StringBuilder();
                        for (String tenant : tenantList)
                        {
                            for (String authority : authorityList)
                            {
                                if (separator == 0)
                                {
                                    if (authQuery.length() > 0)
                                    {
                                        authQuery.append(" ");
                                        denyQuery.append(" ");                                        
                                    }
                                    switch (AuthorityType.getAuthorityType(authority))
                                    {
                                    case USER:
                                        authQuery.append("|AUTHORITY:\"").append(authority).append("\"");
                                        denyQuery.append("|DENIED:\"").append(authority).append("\"");
                                        break;
                                    case GROUP:
                                    case EVERYONE:
                                    case GUEST:
                                        if (tenant.length() == 0)
                                        {
                                            // Default tenant matches 4.0
                                            authQuery.append("|AUTHORITY:\"").append(authority).append("\"");
                                            denyQuery.append("|DENIED:\"").append(authority).append("\"");
                                        }
                                        else
                                        {
                                            authQuery.append("|AUTHORITY:\"").append(authority).append("@").append(tenant).append("\"");
                                            denyQuery.append("|DENIED:\"").append(authority).append("@").append(tenant).append("\"");
                                        }
                                        break;
                                    default:
                                        authQuery.append("|AUTHORITY:\"").append(authority).append("\"");
                                        denyQuery.append("|DENIED:\"").append(authority).append("\"");
                                        break;
                                    }
                                }
                                else
                                {
                                    if(authQuery.length() == 0)
                                    {
                                        authQuery.append("|AUTHSET:\"");
                                        denyQuery.append("|DENYSET:\"");
                                    }
                                    switch (AuthorityType.getAuthorityType(authority))
                                    {
                                    case USER:
                                        authQuery.append(separator).append(authority);
                                        denyQuery.append(separator).append(authority);
                                        break;
                                    case GROUP:
                                    case EVERYONE:
                                    case GUEST:
                                        if (tenant.length() == 0)
                                        {
                                            // Default tenant matches 4.0
                                            authQuery.append(separator).append(authority);
                                            denyQuery.append(separator).append(authority);
                                        }
                                        else
                                        {
                                            authQuery.append(separator).append(authority).append("@").append(tenant);
                                            denyQuery.append(separator).append(authority).append("@").append(tenant);
                                        }
                                        break;
                                    default:
                                        authQuery.append(separator).append(authority);
                                        denyQuery.append(separator).append(authority);
                                        break;
                                    }
                                }

                            }
                        }
                        if(separator != 0)
                        {
                            authQuery.append("\"");
                            denyQuery.append("\"");
                        }

                        if (authQuery.length() > 0)
                        {
                            authQuery.insert(0, "(").
                                      append(") AND NOT (").
                                      append(denyQuery).
                                      append(")");
                            searchParameters.setQuery(authQuery.toString());
                        }
                    }
                    else if (getString().equals(TENANT_FILTER_FROM_JSON))
                    {
                        isFilter =Boolean.TRUE;
                        
                        ArrayList<String> tenantList = new ArrayList<String>(1);
                        JSONArray tenants = json.getJSONArray("tenants");
                        for (int i = 0; i < tenants.length(); i++)
                        {
                            String tenantString = tenants.getString(i);
                            tenantList.add(tenantString);
                        }

                        StringBuilder tenantQuery = new StringBuilder();
                        for (String tenant : tenantList)
                        {
                            if (tenantQuery.length() > 0)
                            {
                                tenantQuery.append(" ");
                            }

                            if (tenant.length() > 0)

                            {
                                tenantQuery.append("|TENANT:\"").append(tenant).append("\"");
                            }
                            else
                            {
                                // TODO: Need to check for the default tenant or no tenant (4.0) or we force a reindex
                                // requirement later ...
                                // Better to add default tenant to the 4.0 index
                                tenantQuery.append("|TENANT:\"").append("_DEFAULT_").append("\"");
                                // tenantQuery.append(" |(+ISNODE:T -TENANT:*)");
                            }

                        }
                        searchParameters.setQuery(tenantQuery.toString());
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
                searchParameters.setQueryConsistency(QueryConsistency.valueOf(json.getString("queryConsistency")));

            }
            catch (JSONException e)
            {
                // This is expected when there is no json element to the request
            }
        }

        if (json != null)
        {
            if (log.isDebugEnabled())
            {
                log.debug(json.toString());
            }
        }

        if (searchParameters.getQuery() == null)
        {
            searchParameters.setQuery(getString());
        }

        if (searchParameters.getLocales().size() == 0)
        {
            searchParameters.addLocale(I18NUtil.getLocale());
        }

        String defaultField = getParam(CommonParams.DF);
        if (defaultField != null)
        {
            searchParameters.setDefaultFieldName(defaultField);
        }

        // searchParameters.setMlAnalaysisMode(getMLAnalysisMode());
        searchParameters.setNamespace(NamespaceService.CONTENT_MODEL_1_0_URI);

        return new Pair<SearchParameters, Boolean>(searchParameters, isFilter);
    }

    /**
     * @param authorityList
     * @return
     */
    private char getSeparator(ArrayList<String> authorityList)
    {
        StringBuilder builder = new StringBuilder();
        for(String auth : authorityList)
        {
            builder.append(auth);
        }
       String test = builder.toString();
       
       for(int i = 0; i < SEPARATORS.length; i++)
       {
           if(test.indexOf(SEPARATORS[i]) == -1)
           {
               return SEPARATORS[i];
           }
       }
       return 0;
    }

    /* (non-Javadoc)
     * @see org.apache.solr.search.QParser#getSort(boolean)
     */
    @Override
    public SortSpec getSort(boolean useGlobalParams) throws SyntaxError
    {
        
        getQuery(); // ensure query is parsed first

        String sortStr = null;
        String startS = null;
        String rowsS = null;

        if (localParams != null) {
          sortStr = localParams.get(CommonParams.SORT);
          startS = localParams.get(CommonParams.START);
          rowsS = localParams.get(CommonParams.ROWS);

          // if any of these parameters are present, don't go back to the global params
          if (sortStr != null || startS != null || rowsS != null) {
            useGlobalParams = false;
          }
        }

        if (useGlobalParams) {
          if (sortStr ==null) {
              sortStr = params.get(CommonParams.SORT);
          }
          if (startS==null) {
            startS = params.get(CommonParams.START);
          }
          if (rowsS==null) {
            rowsS = params.get(CommonParams.ROWS);
          }
        }

        int start = startS != null ? Integer.parseInt(startS) : 0;
        int rows = rowsS != null ? Integer.parseInt(rowsS) : 10;

        // Fix sort fields here
        if(sortStr != null)
        {
            StringBuilder builder = new StringBuilder();
            StringBuilder propertyBuilder = null;
            char c;
            for(int i = 0; i < sortStr.length(); i++)
            {
                c = sortStr.charAt(i);
                if(propertyBuilder == null)
                {
                    if(c == '@')
                    {
                        propertyBuilder = new StringBuilder();
                        propertyBuilder.append(c);
                    }
                    else
                    {
                        builder.append(c);
                    }
                }
                else
                {
                    if(Character.isWhitespace(c))
                    {
                        String toAppend = AlfrescoSolrDataModel.getInstance().mapProperty(propertyBuilder.toString(), FieldUse.SORT);
                        builder.append(toAppend);
                        builder.append(c);
                        propertyBuilder = null;
                    }
                    else
                    {
                        propertyBuilder.append(c);
                    }
                }
            }
            if(propertyBuilder != null)
            {
                String toAppend =  AlfrescoSolrDataModel.getInstance().mapProperty(propertyBuilder.toString(), FieldUse.SORT);
                builder.append(toAppend);
            }
            sortStr = builder.toString();
        }
        
        SortSpec sort = QueryParsing.parseSortSpec(sortStr, req);

        sort.setOffset(start);
        sort.setCount(rows);
        return sort;
    }

 
    
}
