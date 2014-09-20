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

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.search.impl.QueryParserUtils;
import org.alfresco.repo.search.impl.lucene.analysis.MLTokenDuplicator;
import org.alfresco.repo.search.impl.parsers.FTSQueryException;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.AlfrescoSolrDataModel.ContentFieldType;
import org.alfresco.solr.AlfrescoSolrDataModel.FieldInstance;
import org.alfresco.solr.AlfrescoSolrDataModel.FieldUse;
import org.alfresco.solr.AlfrescoSolrDataModel.IndexedField;
import org.alfresco.util.CachingDateFormat;
import org.alfresco.util.ISO9075;
import org.alfresco.util.Pair;
import org.alfresco.util.SearchLanguageConversion;
import org.antlr.misc.OrderedHashSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.base.XPathReader;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author Andy
 *
 */
public class Solr4QueryParser extends QueryParser implements QueryConstants
{
    /**
     * @param matchVersion
     * @param f
     * @param a
     */
    public Solr4QueryParser(IndexSchema schema, Version matchVersion, String f, Analyzer a)
    {
        super(matchVersion, f, a);
        this.schema = schema;
        setAllowLeadingWildcard(true);
        setAnalyzeRangeTerms(true);
    }

    IndexSchema schema;
    
    @SuppressWarnings("unused")
    private static Log s_logger = LogFactory.getLog(Solr4QueryParser.class);

    protected NamespacePrefixResolver namespacePrefixResolver;

    protected DictionaryService dictionaryService;

    private TenantService tenantService;

    private SearchParameters searchParameters;

    private MLAnalysisMode mlAnalysisMode = MLAnalysisMode.EXACT_LANGUAGE_AND_ALL;

    private int internalSlop = 0;

    
    /**
     * @param searchParameters
     */
    public void setSearchParameters(SearchParameters searchParameters)
    {
        this.searchParameters = searchParameters;
    }

    /**
     * @param namespacePrefixResolver
     */
    public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver)
    {
        this.namespacePrefixResolver = namespacePrefixResolver;
    }

    /**
     * @param tenantService
     */
    public void setTenantService(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }

    public SearchParameters getSearchParameters()
    {
        return searchParameters;
    }
    
    protected Query getFieldQuery(String field, String queryText, int slop) throws ParseException
    {
        try
        {
            internalSlop = slop;
            Query query;
            query = getFieldQuery(field, queryText);
            return query;
        }
        finally
        {
            internalSlop = 0;
        }

    }

    /**
     * @param field
     * @param queryText
     * @param analysisMode
     * @param slop
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     * @throws IOException 
     */
    public Query getFieldQuery(String field, String queryText, AnalysisMode analysisMode, int slop, LuceneFunction luceneFunction) throws ParseException
    {
        try
        {
            internalSlop = slop;
            Query query = getFieldQuery(field, queryText, analysisMode, luceneFunction);
            return query;
        }
        finally
        {
            internalSlop = 0;
        }

    }

    /**
     * @param field
     * @param sqlLikeClause
     * @param analysisMode
     * @return the query
     * @throws ParseException
     */
    public Query getLikeQuery(String field, String sqlLikeClause, AnalysisMode analysisMode) throws ParseException
    {
        String luceneWildCardExpression = translate(sqlLikeClause);
        return getWildcardQuery(field, luceneWildCardExpression, AnalysisMode.LIKE);
    }

    private String translate(String string)
    {
        StringBuilder builder = new StringBuilder(string.length());

        boolean lastWasEscape = false;

        for (int i = 0; i < string.length(); i++)
        {
            char c = string.charAt(i);
            if (lastWasEscape)
            {
                builder.append(c);  
                lastWasEscape = false;
            }
            else
            {
                if (c == '\\')
                {
                    lastWasEscape = true;
                }
                else if (c == '%')
                {
                    builder.append('*');
                }
                else if (c == '_')
                {
                    builder.append('?');
                }
                else if (c == '*')
                {
                    builder.append('\\');
                    builder.append(c);
                }
                else if (c == '?')
                {
                    builder.append('\\');
                    builder.append(c);
                }
                else
                {
                    builder.append(c);
                }
            }
        }
        if (lastWasEscape)
        {
            throw new FTSQueryException("Escape character at end of string " + string);
        }

        return builder.toString();
    }


    /**
     * @param field
     * @param queryText
     * @param analysisMode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     * @throws IOException 
     */
    public Query getDoesNotMatchFieldQuery(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        BooleanQuery query = new BooleanQuery();
        Query allQuery = new MatchAllDocsQuery();
        Query matchQuery = getFieldQuery(field, queryText, analysisMode, luceneFunction);
        if ((matchQuery != null))
        {
            query.add(allQuery, Occur.MUST);
            query.add(matchQuery, Occur.MUST_NOT);
        }
        else
        {
            throw new UnsupportedOperationException();
        }
        return query;
    }

    public Query getFieldQuery(String field, String queryText) throws ParseException
    {
        return getFieldQuery(field, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    /**
     * @param field
     * @param first
     * @param last
     * @param slop
     * @param inOrder
     * @return the query
     * @throws ParseException 
     */
    public Query getSpanQuery(String field, String first, String last, int slop, boolean inOrder) throws ParseException
    {
        if (field.equals(FIELD_PATH))
        {
            throw new UnsupportedOperationException("Span is not supported for " + FIELD_PATH);
        }
        else if (field.equals(FIELD_PATHWITHREPEATS))
        {
            throw new UnsupportedOperationException("Span is not supported for " + FIELD_PATHWITHREPEATS);
        }
        else if (field.equals(FIELD_TEXT))
        {
            Set<String> text = searchParameters.getTextAttributes();
            if ((text == null) || (text.size() == 0))
            {
                Query query = getSpanQuery(PROPERTY_FIELD_PREFIX + ContentModel.PROP_CONTENT.toString(), first, last, slop, inOrder);
                return query;
            }
            else
            {
                BooleanQuery query = new BooleanQuery();
                for (String fieldName : text)
                {
                    Query part = getSpanQuery(fieldName, first, last, slop, inOrder);
                    query.add(part, Occur.SHOULD);
                }
                return query;
            }
        }
        else if (field.equals(FIELD_CLASS))
        {
            throw new UnsupportedOperationException("Span is not supported for " + FIELD_CLASS);
        }
        else if (field.equals(FIELD_TYPE))
        {
            throw new UnsupportedOperationException("Span is not supported for " + FIELD_TYPE);
        }
        else if (field.equals(FIELD_EXACTTYPE))
        {
            throw new UnsupportedOperationException("Span is not supported for " + FIELD_EXACTTYPE);
        }
        else if (field.equals(FIELD_ASPECT))
        {
            throw new UnsupportedOperationException("Span is not supported for " + FIELD_ASPECT);
        }
        else if (field.equals(FIELD_EXACTASPECT))
        {
            throw new UnsupportedOperationException("Span is not supported for " + FIELD_EXACTASPECT);
        }
        else if (isPropertyField(field))
        {
            return spanQueryBuilder(field, first, last, slop, inOrder);
        }
        else if (field.equals(FIELD_ALL))
        {
            Set<String> all = searchParameters.getAllAttributes();
            if ((all == null) || (all.size() == 0))
            {
                Collection<QName> contentAttributes = dictionaryService.getAllProperties(null);
                BooleanQuery query = new BooleanQuery();
                for (QName qname : contentAttributes)
                {
                    Query part = getSpanQuery(PROPERTY_FIELD_PREFIX + qname.toString(), first, last, slop, inOrder);
                    query.add(part, Occur.SHOULD);
                }
                return query;
            }
            else
            {
                BooleanQuery query = new BooleanQuery();
                for (String fieldName : all)
                {
                    Query part = getSpanQuery(fieldName, first, last, slop, inOrder);
                    query.add(part, Occur.SHOULD);
                }
                return query;
            }

        }
        else if (field.equals(FIELD_ISUNSET))
        {
            throw new UnsupportedOperationException("Span is not supported for " + FIELD_ISUNSET);
        }
        else if (field.equals(FIELD_ISNULL))
        {
            throw new UnsupportedOperationException("Span is not supported for " + FIELD_ISNULL);
        }
        else if (field.equals(FIELD_ISNOTNULL))
        {
            throw new UnsupportedOperationException("Span is not supported for " + FIELD_ISNOTNULL);
        }
        else if (QueryParserUtils.matchDataTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService, field) != null)
        {
            Collection<QName> contentAttributes = dictionaryService.getAllProperties(QueryParserUtils.matchDataTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService,field).getName());
            BooleanQuery query = new BooleanQuery();
            for (QName qname : contentAttributes)
            {
                Query part = getSpanQuery(PROPERTY_FIELD_PREFIX + qname.toString(), first, last, slop, inOrder);
                query.add(part, Occur.SHOULD);
            }
            return query;
        }
        else if (field.equals(FIELD_FTSSTATUS))
        {
            throw new UnsupportedOperationException("Span is not supported for " + FIELD_FTSSTATUS);
        }
        else if (field.equals(FIELD_TAG))
        {
            return null;
        }
        else if (isPropertyField(field))
        {
            return spanQueryBuilder(field, first, last, slop, inOrder);   
        }
        else
        {
                BytesRef firstBytes = analyzeMultitermTerm(field, first, getAnalyzer());
                BytesRef lastBytes = analyzeMultitermTerm(field, last, getAnalyzer());
                SpanQuery firstTerm = new SpanTermQuery(new Term(field, firstBytes));
                SpanQuery lastTerm = new SpanTermQuery(new Term(field, lastBytes));
                return new SpanNearQuery(new SpanQuery[] { firstTerm, lastTerm }, slop, inOrder);
        }
    }

   
    protected Query getFieldQuery(String field, String queryText, boolean quoted) throws ParseException
    {
        return getFieldQuery(field, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    /**
     * @param field
     * @param queryText
     * @param analysisMode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     * @throws IOException 
     */
    public Query getFieldQuery(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        try
        {
            if (field.equals(FIELD_PATH))
            {
                return createPathQuery(queryText, false);
            }
            else if (field.equals(FIELD_PATHWITHREPEATS))
            {
                return createPathQuery(queryText, true);
            }
            else if (field.equals(FIELD_TEXT))
            {
                return createTextQuery(queryText, analysisMode, luceneFunction);
            }
            else if (field.equals(FIELD_ID))
            {
                return createIdQuery(queryText);
            }
            else if (field.equals(FIELD_SOLR4_ID))
            {
                return createSolr4IdQuery(queryText);
            }
            else if (field.equals(FIELD_DBID))
            {
                return createDbidQuery(queryText);
            }
            else if (field.equals(FIELD_ACLID))
            {
                return createAclIdQuery(queryText);
            }
            else if (field.equals(FIELD_OWNER))
            {
                return createOwnerQuery(queryText);
            }
            else if (field.equals(FIELD_OWNERSET))
            {
                return createOwnerSetQuery(queryText);
            }
            else if (field.equals(FIELD_READER))
            {
                return createReaderQuery(queryText);
            }
            else if (field.equals(FIELD_READERSET))
            {
                return createReaderSetQuery(queryText);
            }
            else if (field.equals(FIELD_AUTHORITY))
            {
                return createAuthorityQuery(queryText);
            }
            else if (field.equals(FIELD_AUTHORITYSET))
            {
                return createAuthoritySetQuery(queryText);
            }
            else if (field.equals(FIELD_DENIED))
            {
                return createDeniedQuery(queryText);
            }
            else if (field.equals(FIELD_DENYSET))
            {
                return createDenySetQuery(queryText);
            }
            else if (field.equals(FIELD_ISROOT))
            {
                return createIsRootQuery(queryText);
            }
            else if (field.equals(FIELD_ISCONTAINER))
            {
                return createIsContainerQuery(queryText);
            }
            else if (field.equals(FIELD_ISNODE))
            {
                return createIsNodeQuery(queryText);
            }
            else if (field.equals(FIELD_TX))
            {
                return createTransactionQuery(queryText);
            }
            else if (field.equals(FIELD_INTXID))
            {
                return createInTxIdQuery(queryText);
            }
            else if (field.equals(FIELD_INACLTXID))
            {
                return createInAclTxIdQuery(queryText);
            }
            else if (field.equals(FIELD_PARENT))
            {
                return createParentQuery(queryText);
            }
            else if (field.equals(FIELD_PRIMARYPARENT))
            {
                return createPrimaryParentQuery(queryText);
            }
            else if (field.equals(FIELD_QNAME))
            {
                return createQNameQuery(queryText);
            }
            else if (field.equals(FIELD_PRIMARYASSOCQNAME))
            {
                return createPrimaryAssocQNameQuery(queryText);
            }
            else if (field.equals(FIELD_PRIMARYASSOCTYPEQNAME))
            {
                return createPrimaryAssocTypeQNameQuery(queryText);
            }
            else if (field.equals(FIELD_ASSOCTYPEQNAME))
            {
                return createAssocTypeQNameQuery(queryText);
            }
            else if (field.equals(FIELD_CLASS))
            {
                ClassDefinition target = QueryParserUtils.matchClassDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService,queryText);
                if (target == null)
                {
                    throw new ParseException("Invalid type: " + queryText);
                }
                return getFieldQuery(target.isAspect() ? FIELD_ASPECT : FIELD_TYPE, queryText, analysisMode, luceneFunction);
            }
            else if (field.equals(FIELD_TYPE))
            {
                return createTypeQuery(queryText, false);
            }
            else if (field.equals(FIELD_EXACTTYPE))
            {
                return createTypeQuery(queryText, true);
            }
            else if (field.equals(FIELD_ASPECT))
            {
                return createAspectQuery(queryText, false);
            }
            else if (field.equals(FIELD_EXACTASPECT))
            {
                return createAspectQuery(queryText, true);
            }
            else if (isPropertyField(field))
            {
                Query query = attributeQueryBuilder(field, queryText, new FieldQuery(), analysisMode, luceneFunction);
                return query;
            }
            else if (field.equals(FIELD_ALL))
            {
                return createAllQuery(queryText, analysisMode, luceneFunction);
            }
            else if (field.equals(FIELD_ISUNSET))
            {
                return createIsUnsetQuery(queryText, analysisMode, luceneFunction);
            }
            else if (field.equals(FIELD_ISNULL))
            {
                return createIsNullQuery(queryText, analysisMode, luceneFunction);
            }
            else if (field.equals(FIELD_ISNOTNULL))
            {
                return createIsNotNull(queryText, analysisMode, luceneFunction);
            }
            else if (QueryParserUtils.matchDataTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService,field) != null)
            {
                return createDataTypeDefinitionQuery(field, queryText, analysisMode, luceneFunction);
            }
            else if (field.equals(FIELD_FTSSTATUS))
            {
                return createTermQuery(field, queryText);
            }
            else if (field.equals(FIELD_TXID))
            {
                return createTxIdQuery(queryText);
            }
            else if (field.equals(FIELD_ACLTXID))
            {
                return createAclTxIdQuery(queryText);
            }
            else if (field.equals(FIELD_TXCOMMITTIME))
            {
                return createTxCommitTimeQuery(queryText);
            }
            else if (field.equals(FIELD_ACLTXCOMMITTIME))
            {
                return createAclTxCommitTimeQuery(queryText);
            }
            else if (field.equals(FIELD_TAG))
            {
                return createTagQuery(queryText);
            }
            else if (field.equals(FIELD_SITE))
            {
                return createSiteQuery(queryText);
            }
            else if (field.equals(FIELD_PNAME))
            {
                return createPNameQuery(queryText);
            }
            else if (field.equals(FIELD_NPATH))
            {
                return createNPathQuery(queryText);
            }
            else if (field.equals(FIELD_TENANT))
            {
                return createTenantQuery(queryText);
            }
            else if (field.equals(FIELD_ANCESTOR))
            {
                return createAncestorQuery(queryText);
            }
            else
            {
                return getFieldQueryImpl(field, queryText, analysisMode, luceneFunction);
            }

        }
        catch (SAXPathException e)
        {
            throw new ParseException("Failed to parse XPath...\n" + e.getMessage());
        }
        catch (IOException e)
        {
            throw new ParseException("IO: " + e.getMessage());
        }

    }

    /**
     * @param queryText
     * @return
     */
    private org.apache.lucene.search.Query createNPathQuery(String queryText)
    {
        return createTermQuery(FIELD_NPATH, queryText);
    }

    /**
     * @param queryText
     * @return
     */
    private Query createPNameQuery(String queryText)
    {
        return createTermQuery(FIELD_PNAME, queryText);
    }

    /**
     * @param queryText
     * @return
     */
    private Query createSiteQuery(String queryText)
    {
        if(queryText.equals("_EVERYTHING_"))
        {
            return createTermQuery(FIELD_ISNODE, "T");
        }
        else if(queryText.equals("_ALL_SITES_"))
        {
            BooleanQuery invertedRepositoryQuery = new BooleanQuery();
            invertedRepositoryQuery.add(createTermQuery(FIELD_ISNODE, "T"), Occur.MUST);
            invertedRepositoryQuery.add(createTermQuery(FIELD_SITE, "_REPOSITORY_"), Occur.MUST_NOT);
            return invertedRepositoryQuery;
        }
        else
        {
            return createTermQuery(FIELD_SITE, queryText);
        }
    }

    private boolean isPropertyField(String field)
    {
        if(field.startsWith(PROPERTY_FIELD_PREFIX))
        {
            return true;
        }
        int index = field.lastIndexOf('@');
        if(index > -1)
        {
            PropertyDefinition pDef = QueryParserUtils.matchPropertyDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService, field.substring(index));
            if(pDef != null)
            {
                IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(pDef.getName());
                for(FieldInstance instance : indexedField.getFields())
                {
                    if(instance.getField().equals(field))
                    {
                        return true;
                    }
                }
                return false;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
       
    }
  
    
    protected Query createTenantQuery(String queryText) throws ParseException
    {
       
        if(queryText.length() > 0)
        {

            return getFieldQueryImplWithIOExceptionWrapped(FIELD_TENANT, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);

        }
        else
        {
            return getFieldQueryImplWithIOExceptionWrapped(FIELD_TENANT, "_DEFAULT_", AnalysisMode.DEFAULT, LuceneFunction.FIELD);
        }

    }


    protected Query createAncestorQuery(String queryText) throws ParseException
    {
        return createNodeRefQuery(FIELD_ANCESTOR, queryText);
    }

    /**
     * @param tag (which will then be ISO9075 encoded)
     * @return
     * @throws ParseException
     */
    protected Query createTagQuery(String tag) throws ParseException
    {
        return createTermQuery(FIELD_TAG, tag.toLowerCase());
    }

   

    private Query getFieldQueryImplWithIOExceptionWrapped(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        try
        {
            return getFieldQueryImpl(field, queryText, analysisMode, luceneFunction);
        }
        catch (IOException e)
        {
            throw new ParseException("IO: " + e.getMessage());
        }
    }
    
    /**
     * @param queryText
     * @return
     */
    protected Query createDbidQuery(String queryText) throws ParseException
    {
        return getFieldQueryImplWithIOExceptionWrapped(FIELD_DBID, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    protected Query createTxIdQuery(String queryText) throws ParseException
    {
        return getFieldQueryImplWithIOExceptionWrapped(FIELD_TXID, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    protected Query createAclTxIdQuery(String queryText) throws ParseException
    {
        return getFieldQueryImplWithIOExceptionWrapped(FIELD_ACLTXID, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    protected Query createTxCommitTimeQuery(String queryText) throws ParseException
    {
        return getFieldQueryImplWithIOExceptionWrapped(FIELD_TXCOMMITTIME, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    protected Query createAclTxCommitTimeQuery(String queryText) throws ParseException
    {
        return getFieldQueryImplWithIOExceptionWrapped(FIELD_ACLTXCOMMITTIME, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    protected Query createDataTypeDefinitionQuery(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        Collection<QName> contentAttributes = dictionaryService.getAllProperties(QueryParserUtils.matchDataTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService,field).getName());
        BooleanQuery query = new BooleanQuery();
        for (QName qname : contentAttributes)
        {
            // The super implementation will create phrase queries etc if required
            Query part = getFieldQuery(PROPERTY_FIELD_PREFIX + qname.toString(), queryText, analysisMode, luceneFunction);
            if (part != null)
            {
                query.add(part, Occur.SHOULD);
            }
            else
            {
                query.add(createNoMatchQuery(), Occur.SHOULD);
            }
        }
        return query;
    }

    protected Query createIsNotNull(String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        PropertyDefinition pd = QueryParserUtils.matchPropertyDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService, queryText);
        if (pd != null)
        {
            BooleanQuery query = new BooleanQuery();
            IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(pd.getName());
            for(FieldInstance field : indexedField.getFields())
            {
                if(!field.isLocalised())
                {
                    Query presenceQuery = getWildcardQuery(field.getField(), "*");
                    if (presenceQuery != null)
                    {
                        query.add(presenceQuery, Occur.SHOULD);
                    }
                }
            }
            return query;
        }
        else
        {
            BooleanQuery query = new BooleanQuery();
            
            Query presenceQuery = getWildcardQuery(queryText, "*");
            if (presenceQuery != null)
            {
                query.add(presenceQuery, Occur.MUST);
            }
            return query;
        }
    }

    protected Query createIsNullQuery(String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        PropertyDefinition pd = QueryParserUtils.matchPropertyDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService, queryText);
        if (pd != null)
        {
            BooleanQuery query = new BooleanQuery();
            IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(pd.getName());
            for(FieldInstance field : indexedField.getFields())
            {
                if(!field.isLocalised())
                {
                    Query presenceQuery = getWildcardQuery(field.getField(), "*");
                    if (presenceQuery != null)
                    {
                        if(query.getClauses().length == 0)
                        {
                            query.add(new MatchAllDocsQuery(), Occur.MUST);
                        }
                        query.add(presenceQuery, Occur.MUST_NOT);
                    }
                }
            }
            return query;
        }
        else
        {
            BooleanQuery query = new BooleanQuery();
            
            Query presenceQuery = getWildcardQuery(queryText, "*");
            if (presenceQuery != null)
            {
                query.add(new MatchAllDocsQuery(), Occur.MUST);
                query.add(presenceQuery, Occur.MUST_NOT);
            }
            return query;
        }
    }

    protected Query createIsUnsetQuery(String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        PropertyDefinition pd = QueryParserUtils.matchPropertyDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService, queryText);
        if (pd != null)
        {
            ClassDefinition containerClass = pd.getContainerClass();
            QName container = containerClass.getName();
            String classType = containerClass.isAspect() ? FIELD_ASPECT : FIELD_TYPE;
            Query typeQuery = getFieldQuery(classType, container.toString(), analysisMode, luceneFunction);
            
            BooleanQuery query = new BooleanQuery();
            IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(pd.getName());
            for(FieldInstance field : indexedField.getFields())
            {
                if(!field.isLocalised())
                {
                    Query presenceQuery = getWildcardQuery(field.getField(), "*");
                    if (presenceQuery != null)
                    {
                        query.add(typeQuery, Occur.MUST);
                        query.add(presenceQuery, Occur.MUST_NOT);
                    }
                }
            }
            return query;
        }
        else
        {
            BooleanQuery query = new BooleanQuery();
            
            Query presenceQuery = getWildcardQuery(queryText, "*");
            if (presenceQuery != null)
            {
                query.add(presenceQuery, Occur.MUST_NOT);
            }
            return query;
        }
    }

    protected Query createAllQuery(String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
//        Set<String> all = searchParameters.getAllAttributes();
//        if ((all == null) || (all.size() == 0))
//        {
//            Collection<QName> contentAttributes = dictionaryService.getAllProperties(null);
//            BooleanQuery query = new BooleanQuery();
//            for (QName qname : contentAttributes)
//            {
//                // The super implementation will create phrase queries etc if required
//                Query part = getFieldQuery(PROPERTY_FIELD_PREFIX + qname.toString(), queryText, analysisMode, luceneFunction);
//                if (part != null)
//                {
//                    query.add(part, Occur.SHOULD);
//                }
//                else
//                {
//                    query.add(createNoMatchQuery(), Occur.SHOULD);
//                }
//            }
//            return query;
//        }
//        else
//        {
//            BooleanQuery query = new BooleanQuery();
//            for (String fieldName : all)
//            {
//                Query part = getFieldQuery(fieldName, queryText, analysisMode, luceneFunction);
//                if (part != null)
//                {
//                    query.add(part, Occur.SHOULD);
//                }
//                else
//                {
//                    query.add(createNoMatchQuery(), Occur.SHOULD);
//                }
//            }
//            return query;
//        }
        throw new UnsupportedOperationException();
    }

    protected Query createAspectQuery(String queryText, boolean exactOnly)
    {
        AspectDefinition target = QueryParserUtils.matchAspectDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService, queryText);
        if (target == null)
        {
            // failed to find the aspect in the dictionary
            throw new AlfrescoRuntimeException("Unknown aspect specified in query: " + queryText);
        }

        if (exactOnly)
        {
            QName targetQName = target.getName();
            TermQuery termQuery = new TermQuery(new Term(FIELD_ASPECT, targetQName.toString()));

            return termQuery;
        }
        else
        {
            Collection<QName> subclasses = dictionaryService.getSubAspects(target.getName(), true);

            BooleanQuery booleanQuery = new BooleanQuery();
            for (QName qname : subclasses)
            {
                AspectDefinition current = dictionaryService.getAspect(qname);
                if (target.getName().equals(current.getName()) || current.getIncludedInSuperTypeQuery())
                {
                    TermQuery termQuery = new TermQuery(new Term(FIELD_ASPECT, qname.toString()));
                    if (termQuery != null)
                    {
                        booleanQuery.add(termQuery, Occur.SHOULD);
                    }
                }
            }
            return booleanQuery;
        }

    }

    protected Query createTypeQuery(String queryText, boolean exactOnly) throws ParseException
    {
        TypeDefinition target = QueryParserUtils.matchTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService, queryText);
        if (target == null)
        {
            throw new ParseException("Invalid type: " + queryText);
        }
        if (exactOnly)
        {
            QName targetQName = target.getName();
            TermQuery termQuery = new TermQuery(new Term(FIELD_TYPE, targetQName.toString()));
            return termQuery;
        }
        else
        {
            Collection<QName> subclasses = dictionaryService.getSubTypes(target.getName(), true);
            BooleanQuery booleanQuery = new BooleanQuery();
            for (QName qname : subclasses)
            {
                TypeDefinition current = dictionaryService.getType(qname);
                if (target.getName().equals(current.getName()) || current.getIncludedInSuperTypeQuery())
                {
                    TermQuery termQuery = new TermQuery(new Term(FIELD_TYPE, qname.toString()));
                    if (termQuery != null)
                    {
                        booleanQuery.add(termQuery, Occur.SHOULD);
                    }
                }
            }
            return booleanQuery;
        }
    }

    protected Query createInTxIdQuery(String queryText) throws ParseException
    {      
        return getFieldQueryImplWithIOExceptionWrapped(FIELD_INTXID, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    protected Query createInAclTxIdQuery(String queryText) throws ParseException
    {      
        return getFieldQueryImplWithIOExceptionWrapped(FIELD_INACLTXID, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    protected Query createTransactionQuery(String queryText)
    {
        return createTermQuery(FIELD_TX, queryText);
    }

    protected Query createIsNodeQuery(String queryText)
    {
        return createTermQuery(FIELD_ISNODE, queryText);
    }

    protected Query createIsContainerQuery(String queryText)
    {
        return createTermQuery(FIELD_ISCONTAINER, queryText);
    }

    protected Query createIsRootQuery(String queryText)
    {
        return createTermQuery(FIELD_ISROOT, queryText);
    }

    protected Query createTermQuery(String field, String queryText)
    {
        TermQuery termQuery = new TermQuery(new Term(field, queryText));
        return termQuery;
    }

    protected Query createPrimaryParentQuery(String queryText)
    {
        return createNodeRefQuery(FIELD_PRIMARYPARENT, queryText);
    }

    protected Query createParentQuery(String queryText)
    {
        return createNodeRefQuery(FIELD_PARENT, queryText);
    }

    protected Query createNodeRefQuery(String field, String queryText)
    {
        if (tenantService.isTenantUser() && (queryText.contains(StoreRef.URI_FILLER)))
        {
            // assume NodeRef, since it contains StorRef URI filler
            queryText = tenantService.getName(new NodeRef(queryText)).toString();
        }
        return createTermQuery(field, queryText);
    }

    protected Query createTextQuery(String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        Set<String> text = searchParameters.getTextAttributes();
        if ((text == null) || (text.size() == 0))
        {
            Query query = getFieldQuery(PROPERTY_FIELD_PREFIX + ContentModel.PROP_CONTENT.toString(), queryText, analysisMode, luceneFunction);
            if (query == null)
            {
                return createNoMatchQuery();
            }
            return query;    
        }
        else
        {
            BooleanQuery query = new BooleanQuery();
            for (String fieldName : text)
            {
                Query part = getFieldQuery(fieldName, queryText, analysisMode, luceneFunction);
                if (part != null)
                {
                    query.add(part, Occur.SHOULD);
                }
                else
                {
                    query.add(createNoMatchQuery(), Occur.SHOULD);
                }
            }
            return query;
        }
    }

    @SuppressWarnings("unchecked")
    protected Query getFieldQueryImpl(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException, IOException
    {
        // Use the analyzer to get all the tokens, and then build a TermQuery,
        // PhraseQuery, or noth

        // TODO: Untokenised columns with functions require special handling

        if (luceneFunction != LuceneFunction.FIELD)
        {
            throw new UnsupportedOperationException("Field queries are not supported on lucene functions (UPPER, LOWER, etc)");
        }

        // if the incoming string already has a language identifier we strip it iff and addit back on again

        String localePrefix = "";

        String toTokenise = queryText;

        if(queryText.startsWith("{"))
        {
            int position = queryText.indexOf("}");
            String language = queryText.substring(0, position + 1);
            Locale locale = new Locale(queryText.substring(1, position));
            String token = queryText.substring(position + 1);
            boolean found = false;
            for(Locale current : Locale.getAvailableLocales())
            {
                if(current.toString().equalsIgnoreCase(locale.toString()))
                {
                    found = true;
                    break;
                }
            }
            if(found)
            {
                localePrefix = language;
                toTokenise = token;
            }
            else
            {
                toTokenise = token;
            }
        }

        String testText = toTokenise;
        boolean requiresMLTokenDuplication = false;
        String localeString = null;
        if (isPropertyField(field) && (localePrefix.length() == 0))
        {
            if ((queryText.length() > 0) && (queryText.charAt(0) == '\u0000'))
            {
                int position = queryText.indexOf("\u0000", 1);
                testText = queryText.substring(position + 1);
                requiresMLTokenDuplication = true;
                localeString = queryText.substring(1, position);
                
            }
        }

        // find the positions of any escaped * and ? and ignore them

        Set<Integer> wildcardPoistions = getWildcardPositions(testText);
        
        TokenStream source;
        if((localePrefix.length() == 0) || (wildcardPoistions.size() > 0) || (analysisMode == AnalysisMode.IDENTIFIER))
        {
            source = getAnalyzer().tokenStream(field, new StringReader(toTokenise));
        }
        else
        {
            source = getAnalyzer().tokenStream(field, new StringReader("\u0000"+localePrefix.substring(1, localePrefix.length()-1)+"\u0000"+toTokenise));
            localePrefix = "";
        }

        ArrayList<org.apache.lucene.analysis.Token> list = new ArrayList<org.apache.lucene.analysis.Token>();
        org.apache.lucene.analysis.Token reusableToken = new org.apache.lucene.analysis.Token();
        org.apache.lucene.analysis.Token nextToken;
        int positionCount = 0;
        boolean severalTokensAtSamePosition = false;

        source.reset();
        while (source.incrementToken())
        {
            CharTermAttribute cta = source.getAttribute(CharTermAttribute.class);
            OffsetAttribute offsetAtt = source.getAttribute(OffsetAttribute.class);
            TypeAttribute typeAtt = null;
            if(source.hasAttribute(TypeAttribute.class))
            {
                typeAtt = source.getAttribute(TypeAttribute.class);
            }
            PositionIncrementAttribute posIncAtt = null;
            if(source.hasAttribute(PositionIncrementAttribute.class))
            {
                posIncAtt = source.getAttribute(PositionIncrementAttribute.class);
            }
            nextToken = new Token(cta.buffer(), 0, cta.length(), offsetAtt.startOffset(), offsetAtt.endOffset());
            if(typeAtt != null)
            {
                nextToken.setType(typeAtt.type());
            }
            if(posIncAtt != null)
            {
                nextToken.setPositionIncrement(posIncAtt.getPositionIncrement());
            }
            
            list.add(nextToken);
            if (nextToken.getPositionIncrement() != 0)
                positionCount += nextToken.getPositionIncrement();
            else
                severalTokensAtSamePosition = true;
        }
        try
        {
            source.close();
        }
        catch (IOException e)
        {
            // ignore
        }

        // add any alpha numeric wildcards that have been missed
        // Fixes most stop word and wild card issues

        for (int index = 0; index < testText.length(); index++)
        {
            char current = testText.charAt(index);
            if (((current == '*') || (current == '?')) && wildcardPoistions.contains(index))
            {
                StringBuilder pre = new StringBuilder(10);
                if(index == 0)
                {
                    // "*" and "?" at the start

                    boolean found = false;
                    for (int j = 0; j < list.size(); j++)
                    {
                        org.apache.lucene.analysis.Token test = list.get(j);
                        if ((test.startOffset() <= 0) && (0 < test.endOffset()))
                        {
                            found = true;
                            break;
                        }
                    }
                    if (!found && (testText.length()  == 1))
                    {
                        // Add new token followed by * not given by the tokeniser
                        org.apache.lucene.analysis.Token newToken = new org.apache.lucene.analysis.Token("", 0, 0);
                        newToken.setType("ALPHANUM");
                        if (requiresMLTokenDuplication)
                        {
                            Locale locale = I18NUtil.parseLocale(localeString);
                            MLTokenDuplicator duplicator = new MLTokenDuplicator(locale, mlAnalysisMode);
                            Iterator<org.apache.lucene.analysis.Token> it = duplicator.buildIterator(newToken);
                            if (it != null)
                            {
                                int count = 0;
                                while (it.hasNext())
                                {
                                    list.add(it.next());
                                    count++;
                                    if (count > 1)
                                    {
                                        severalTokensAtSamePosition = true;
                                    }
                                }
                            }
                        }
                        // content
                        else
                        {
                            list.add(newToken);
                        }
                    }
                }
                else if (index > 0)
                {
                    // Add * and ? back into any tokens from which it has been removed

                    boolean tokenFound = false;
                    for (int j = 0; j < list.size(); j++)
                    {
                        org.apache.lucene.analysis.Token test = list.get(j);
                        if ((test.startOffset() <= index) && (index < test.endOffset()))
                        {
                            if(requiresMLTokenDuplication)
                            {
                                String termText = test.toString();
                                int position = termText.indexOf("}");
                                String language = termText.substring(0, position + 1);
                                String token = termText.substring(position + 1);
                                if(index >= test.startOffset()+token.length())
                                {
                                    test.setEmpty();
                                    test.append(language + token + current);
                                }
                            }
                            else
                            {
                                if(index >= test.startOffset()+test.length())
                                {
                                    test.setEmpty();
                                    test.append(test.toString() + current);
                                }
                            }
                            tokenFound = true;
                            break;
                        }
                    }

                    if(!tokenFound)
                    {
                        for (int i = index - 1; i >= 0; i--)
                        {
                            char c = testText.charAt(i);
                            if (Character.isLetterOrDigit(c))
                            {
                                boolean found = false;
                                for (int j = 0; j < list.size(); j++)
                                {
                                    org.apache.lucene.analysis.Token test = list.get(j);
                                    if ((test.startOffset() <= i) && (i < test.endOffset()))
                                    {
                                        found = true;
                                        break;
                                    }
                                }
                                if (found)
                                {
                                    break;
                                }
                                else
                                {
                                    pre.insert(0, c);
                                }
                            }
                            else
                            {
                                break;
                            }
                        }
                        if (pre.length() > 0)
                        {
                            // Add new token followed by * not given by the tokeniser
                            org.apache.lucene.analysis.Token newToken = new org.apache.lucene.analysis.Token(pre.toString(), index - pre.length(), index);
                            newToken.setType("ALPHANUM");
                            if (requiresMLTokenDuplication)
                            {
                                Locale locale = I18NUtil.parseLocale(localeString);
                                MLTokenDuplicator duplicator = new MLTokenDuplicator(locale, mlAnalysisMode);
                                Iterator<org.apache.lucene.analysis.Token> it = duplicator.buildIterator(newToken);
                                if (it != null)
                                {
                                    int count = 0;
                                    while (it.hasNext())
                                    {
                                        list.add(it.next());
                                        count++;
                                        if (count > 1)
                                        {
                                            severalTokensAtSamePosition = true;
                                        }
                                    }
                                }
                            }
                            // content
                            else
                            {
                                list.add(newToken);
                            }
                        }
                    }
                }

                StringBuilder post = new StringBuilder(10);
                if (index > 0)
                {
                    for (int i = index + 1; i < testText.length(); i++)
                    {
                        char c = testText.charAt(i);
                        if (Character.isLetterOrDigit(c))
                        {
                            boolean found = false;
                            for (int j = 0; j < list.size(); j++)
                            {
                                org.apache.lucene.analysis.Token test = list.get(j);
                                if ((test.startOffset() <= i) && (i < test.endOffset()))
                                {
                                    found = true;
                                    break;
                                }
                            }
                            if (found)
                            {
                                break;
                            }
                            else
                            {
                                post.append(c);
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    if (post.length() > 0)
                    {
                        // Add new token followed by * not given by the tokeniser
                        org.apache.lucene.analysis.Token newToken = new org.apache.lucene.analysis.Token(post.toString(), index + 1, index + 1 + post.length());
                        newToken.setType("ALPHANUM");
                        if (requiresMLTokenDuplication)
                        {
                            Locale locale = I18NUtil.parseLocale(localeString);
                            MLTokenDuplicator duplicator = new MLTokenDuplicator(locale, mlAnalysisMode);
                            Iterator<org.apache.lucene.analysis.Token> it = duplicator.buildIterator(newToken);
                            if (it != null)
                            {
                                int count = 0;
                                while (it.hasNext())
                                {
                                    list.add(it.next());
                                    count++;
                                    if (count > 1)
                                    {
                                        severalTokensAtSamePosition = true;
                                    }
                                }
                            }
                        }
                        // content
                        else
                        {
                            list.add(newToken);
                        }
                    }
                }



            }
        }

        Collections.sort(list, new Comparator<org.apache.lucene.analysis.Token>()
                {

            public int compare(Token o1, Token o2)
            {
                int dif = o1.startOffset() - o2.startOffset();
                if (dif != 0)
                {
                    return dif;
                }
                else
                {
                    return o2.getPositionIncrement() - o1.getPositionIncrement();
                }
            }
                });

        // Combined * and ? based strings - should redo the tokeniser

        // Build tokens by position

        LinkedList<LinkedList<org.apache.lucene.analysis.Token>> tokensByPosition = new LinkedList<LinkedList<org.apache.lucene.analysis.Token>>();
        LinkedList<org.apache.lucene.analysis.Token> currentList = null;
        for (org.apache.lucene.analysis.Token c : list)
        {    
            if (c.getPositionIncrement() == 0)
            {
                if(currentList == null)
                {
                    currentList = new LinkedList<org.apache.lucene.analysis.Token>();
                    tokensByPosition.add(currentList);
                }
                currentList.add(c);
            }
            else
            {
                currentList = new LinkedList<org.apache.lucene.analysis.Token>();
                tokensByPosition.add(currentList);
                currentList.add(c);
            }
        }

        // Build all the token sequences and see which ones get strung together

        LinkedList<LinkedList<org.apache.lucene.analysis.Token>> allTokenSequences = new LinkedList<LinkedList<org.apache.lucene.analysis.Token>>();
        for(LinkedList<org.apache.lucene.analysis.Token> tokensAtPosition : tokensByPosition)
        {
            if(allTokenSequences.size() == 0)
            {
                for(org.apache.lucene.analysis.Token t : tokensAtPosition)
                {
                    LinkedList<org.apache.lucene.analysis.Token> newEntry = new LinkedList<org.apache.lucene.analysis.Token>();
                    newEntry.add(t);
                    allTokenSequences.add(newEntry);
                }
            }
            else
            {
                LinkedList<LinkedList<org.apache.lucene.analysis.Token>> newAllTokeSequences = new LinkedList<LinkedList<org.apache.lucene.analysis.Token>>();

                FOR_FIRST_TOKEN_AT_POSITION_ONLY: for(org.apache.lucene.analysis.Token t : tokensAtPosition)
                {
                    boolean tokenFoundSequence = false;
                    for(LinkedList<org.apache.lucene.analysis.Token> tokenSequence : allTokenSequences)
                    {
                        LinkedList<org.apache.lucene.analysis.Token> newEntry = new LinkedList<org.apache.lucene.analysis.Token>();
                        newEntry.addAll(tokenSequence);
                        if(newEntry.getLast().endOffset() <= t.startOffset())
                        {
                            newEntry.add(t);
                            tokenFoundSequence = true;
                        }
                        newAllTokeSequences.add(newEntry);
                    }
                    if(false == tokenFoundSequence)
                    {
                        LinkedList<org.apache.lucene.analysis.Token> newEntry = new LinkedList<org.apache.lucene.analysis.Token>();
                        newEntry.add(t);
                        newAllTokeSequences.add(newEntry);
                    }
                    // Limit the max number of permutations we consider
                    if(newAllTokeSequences.size() > 64)
                    {
                        break FOR_FIRST_TOKEN_AT_POSITION_ONLY;
                    }
                }
                allTokenSequences = newAllTokeSequences;
            }
        }

        // build the uniquie

        LinkedList<LinkedList<org.apache.lucene.analysis.Token>> fixedTokenSequences = new LinkedList<LinkedList<org.apache.lucene.analysis.Token>>();
        for(LinkedList<org.apache.lucene.analysis.Token> tokenSequence : allTokenSequences)
        {
            LinkedList<org.apache.lucene.analysis.Token> fixedTokenSequence = new LinkedList<org.apache.lucene.analysis.Token>();
            fixedTokenSequences.add(fixedTokenSequence);
            org.apache.lucene.analysis.Token replace = null;
            for (org.apache.lucene.analysis.Token c : tokenSequence)
            {
                if (replace == null)
                {
                    StringBuilder prefix = new StringBuilder();
                    for (int i = c.startOffset() - 1; i >= 0; i--)
                    {
                        char test = testText.charAt(i);
                        if (((test == '*') || (test == '?')) && wildcardPoistions.contains(i))
                        {
                            prefix.insert(0, test);
                        }
                        else
                        {
                            break;
                        }
                    }
                    String pre = prefix.toString();
                    if (requiresMLTokenDuplication)
                    {
                        String termText = c.toString();
                        int position = termText.indexOf("}");
                        String language = termText.substring(0, position + 1);
                        String token = termText.substring(position + 1);
                        replace = new org.apache.lucene.analysis.Token(language + pre + token, c.startOffset() - pre.length(), c.endOffset());
                        replace.setType(c.type());
                        replace.setPositionIncrement(c.getPositionIncrement());
                    }
                    else
                    {
                        String termText = c.toString();
                        replace = new org.apache.lucene.analysis.Token(pre + termText, c.startOffset() - pre.length(), c.endOffset());
                        replace.setType(c.type());
                        replace.setPositionIncrement(c.getPositionIncrement());
                    }
                }
                else
                {
                    StringBuilder prefix = new StringBuilder();
                    StringBuilder postfix = new StringBuilder();
                    StringBuilder builder = prefix;
                    for (int i = c.startOffset() - 1; i >= replace.endOffset(); i--)
                    {
                        char test = testText.charAt(i);
                        if (((test == '*') || (test == '?')) && wildcardPoistions.contains(i))
                        {
                            builder.insert(0, test);
                        }
                        else
                        {
                            builder = postfix;
                            postfix.setLength(0);
                        }
                    }
                    String pre = prefix.toString();
                    String post = postfix.toString();

                    // Does it bridge?
                    if ((pre.length() > 0) && (replace.endOffset() + pre.length()) == c.startOffset())
                    {
                        String termText = c.toString();
                        if (requiresMLTokenDuplication)
                        {
                            int position = termText.indexOf("}");
                            @SuppressWarnings("unused")
                            String language = termText.substring(0, position + 1);
                            String token = termText.substring(position + 1);
                            int oldPositionIncrement = replace.getPositionIncrement();
                            String replaceTermText = replace.toString();
                            replace = new org.apache.lucene.analysis.Token(replaceTermText + pre + token, replace.startOffset(), c.endOffset());
                            replace.setType(replace.type());
                            replace.setPositionIncrement(oldPositionIncrement);
                        }
                        else
                        {
                            int oldPositionIncrement = replace.getPositionIncrement();
                            String replaceTermText = replace.toString(); 
                            replace = new org.apache.lucene.analysis.Token(replaceTermText + pre + termText, replace.startOffset(), c.endOffset());
                            replace.setType(replace.type());
                            replace.setPositionIncrement(oldPositionIncrement);
                        }
                    }
                    else
                    {
                        String termText = c.toString();
                        if (requiresMLTokenDuplication)
                        {
                            int position = termText.indexOf("}");
                            String language = termText.substring(0, position + 1);
                            String token = termText.substring(position + 1);
                            String replaceTermText = replace.toString();
                            org.apache.lucene.analysis.Token last = new org.apache.lucene.analysis.Token(replaceTermText + post, replace.startOffset(), replace.endOffset() + post.length());
                            last.setType(replace.type());
                            last.setPositionIncrement(replace.getPositionIncrement());
                            fixedTokenSequence.add(last);
                            replace = new org.apache.lucene.analysis.Token(language + pre + token, c.startOffset() - pre.length(), c.endOffset());
                            replace.setType(c.type());
                            replace.setPositionIncrement(c.getPositionIncrement());
                        }
                        else
                        {
                            String replaceTermText = replace.toString();
                            org.apache.lucene.analysis.Token last = new org.apache.lucene.analysis.Token(replaceTermText + post, replace.startOffset(), replace.endOffset() + post.length());
                            last.setType(replace.type());
                            last.setPositionIncrement(replace.getPositionIncrement());
                            fixedTokenSequence.add(last);
                            replace = new org.apache.lucene.analysis.Token(pre + termText, c.startOffset() - pre.length(), c.endOffset());
                            replace.setType(c.type());
                            replace.setPositionIncrement(c.getPositionIncrement());
                        }
                    }
                }
            }
            // finish last
            if (replace != null)
            {
                StringBuilder postfix = new StringBuilder();
                if ((replace.endOffset() >= 0) && (replace.endOffset() < testText.length()))
                {
                    for (int i = replace.endOffset(); i < testText.length(); i++)
                    {
                        char test = testText.charAt(i);
                        if (((test == '*') || (test == '?')) && wildcardPoistions.contains(i))
                        {
                            postfix.append(test);
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                String post = postfix.toString();
                int oldPositionIncrement = replace.getPositionIncrement();
                String replaceTermText = replace.toString();
                replace = new org.apache.lucene.analysis.Token(replaceTermText + post, replace.startOffset(), replace.endOffset() + post.length());
                replace.setType(replace.type());
                replace.setPositionIncrement(oldPositionIncrement);
                fixedTokenSequence.add(replace);
            }
        }

        // rebuild fixed list

        ArrayList<org.apache.lucene.analysis.Token> fixed = new ArrayList<org.apache.lucene.analysis.Token>();
        for(LinkedList<org.apache.lucene.analysis.Token> tokenSequence : fixedTokenSequences)
        {
            for (org.apache.lucene.analysis.Token token : tokenSequence)
            {
                fixed.add(token);
            }
        }


        // reorder by start position and increment

        Collections.sort(fixed, new Comparator<org.apache.lucene.analysis.Token>()
                {

            public int compare(Token o1, Token o2)
            {
                int dif = o1.startOffset() - o2.startOffset();
                if (dif != 0)
                {
                    return dif;
                }
                else
                {
                    return o1.getPositionIncrement() - o2.getPositionIncrement();
                }
            }
                });

        // make sure we remove any tokens we have duplicated

        @SuppressWarnings("rawtypes")
        OrderedHashSet unique = new OrderedHashSet();
        unique.addAll(fixed);
        fixed = new ArrayList<org.apache.lucene.analysis.Token>(unique);

        list = fixed;

        // add any missing locales back to the tokens

        if(localePrefix.length() > 0)
        {
            for (int j = 0; j < list.size(); j++)
            {
                org.apache.lucene.analysis.Token currentToken = list.get(j);
                String termText = currentToken.toString();
                currentToken.setEmpty();
                currentToken.append(localePrefix+termText);
            }
        }

        if (list.size() == 0)
            return null;
        else if (list.size() == 1)
        {
            nextToken = list.get(0);
            String termText = nextToken.toString(); 
            if (termText.contains("*") || termText.contains("?"))
            {
                return newWildcardQuery(new Term(field, getLowercaseExpandedTerms() ? termText.toLowerCase() : termText));
            }
            else
            {
                return newTermQuery(new Term(field, termText));
            }
        }
        else
        {
            if (severalTokensAtSamePosition)
            {
                if (positionCount == 1)
                {
                    // no phrase query:
                    BooleanQuery q = newBooleanQuery(true);
                    for (int i = 0; i < list.size(); i++)
                    {
                        Query currentQuery;
                        nextToken = list.get(i);
                        String termText = nextToken.toString();
                        if (termText.contains("*") || termText.contains("?"))
                        {
                            currentQuery = newWildcardQuery(new Term(field, getLowercaseExpandedTerms() ? termText.toLowerCase() : termText));
                        }
                        else
                        {
                            currentQuery = newTermQuery(new Term(field, termText));
                        }
                        q.add(currentQuery, BooleanClause.Occur.SHOULD);
                    }
                    return q;
                }
                // Consider if we can use a multi-phrase query (e.g for synonym use rather then WordDelimiterFilterFactory)
                else if(canUseMultiPhraseQuery(fixedTokenSequences))
                {
                    // phrase query:
                    MultiPhraseQuery mpq = newMultiPhraseQuery();
                    mpq.setSlop(internalSlop);
                    ArrayList<Term> multiTerms = new ArrayList<Term>();
                    int position = 0;
                    for (int i = 0; i < list.size(); i++)
                    {
                        nextToken = list.get(i);
                        String termText = nextToken.toString();
                        
                        Term term = new Term(field, termText);
                        if ((termText != null) && (termText.contains("*") || termText.contains("?")))
                        {
                            throw new IllegalStateException("Wildcards are not allowed in multi phrase anymore");
                        }
                        else
                        {
                            multiTerms.add(term);
                        }
                        
                        if (nextToken.getPositionIncrement() > 0 && multiTerms.size() > 0)
                        {
                            if (getEnablePositionIncrements())
                            {
                                mpq.add(multiTerms.toArray(new Term[0]), position);
                            }
                            else
                            {
                                mpq.add(multiTerms.toArray(new Term[0]));
                            }
                            checkTermCount(field, queryText, mpq);
                            multiTerms.clear();
                        }
                        position += nextToken.getPositionIncrement();
                        
                    }
                    if (getEnablePositionIncrements())
                    {
                        if (multiTerms.size() > 0)
                        {
                            mpq.add(multiTerms.toArray(new Term[0]), position);
                        }
//                        else
//                        {
//                            mpq.add(new Term[] { new Term(field, "\u0000") }, position);
//                        }
                    }
                    else
                    {
                        if (multiTerms.size() > 0)
                        {
                            mpq.add(multiTerms.toArray(new Term[0]));
                        }
//                        else
//                        {
//                            mpq.add(new Term[] { new Term(field, "\u0000") });
//                        }
                    }
                    checkTermCount(field, queryText, mpq);
                    return mpq;

                }
                // Word delimiter factory and other odd things generate complex token patterns
                // Smart skip token  sequences with small tokens that generate toomany wildcards
                // Fall back to the larger pattern
                // e.g Site1* will not do (S ite 1*) or (Site 1*)  if 1* matches too much (S ite1*)  and (Site1*) will still be OK 
                // If we skip all (for just 1* in the input) this is still an issue.
                else
                {
                    SpanOrQuery spanOr = new SpanOrQuery();
                    
                    for(LinkedList<org.apache.lucene.analysis.Token> tokenSequence : fixedTokenSequences)
                    {
                        int gap = 0;
                        SpanQuery spanQuery = null;
                        SpanOrQuery atSamePosition = new SpanOrQuery();
                        for (int i = 0; i < tokenSequence.size(); i++)
                        {
                            nextToken = (org.apache.lucene.analysis.Token) tokenSequence.get(i);
                            String termText = nextToken.toString();
                            
                            Term term = new Term(field, termText);

                            if (getEnablePositionIncrements())
                            {
                                SpanQuery nextSpanQuery;
                                if ((termText != null) && (termText.contains("*") || termText.contains("?")))
                                {
                                    org.apache.lucene.search.WildcardQuery wildQuery = new org.apache.lucene.search.WildcardQuery(term);
                                    nextSpanQuery = new SpanMultiTermQueryWrapper<>(wildQuery);
                                }
                                else
                                {
                                    nextSpanQuery = new SpanTermQuery(term);
                                }
                                if(gap == 0)
                                {
                                    atSamePosition.addClause(nextSpanQuery);
                                }
                                else
                                {
                                    if(atSamePosition.getClauses().length == 0)
                                    {
                                        if(spanQuery == null)
                                        {
                                            spanQuery = nextSpanQuery;
                                        }
                                        else
                                        {
                                            spanQuery = new SpanNearQuery(new SpanQuery[]{spanQuery, nextSpanQuery}, gap + internalSlop, internalSlop < 2);
                                        }
                                        atSamePosition = new SpanOrQuery();
                                    }
                                    else if(atSamePosition.getClauses().length == 1)
                                    {
                                        if(spanQuery == null)
                                        {
                                            spanQuery = atSamePosition.getClauses()[0];
                                        }
                                        else
                                        {
                                            spanQuery = new SpanNearQuery(new SpanQuery[]{spanQuery, atSamePosition.getClauses()[0]}, gap + internalSlop, internalSlop < 2);
                                        }
                                        atSamePosition = new SpanOrQuery();
                                        atSamePosition.addClause(nextSpanQuery);
                                    }
                                    else
                                    {
                                        if(spanQuery == null)
                                        {
                                            spanQuery = atSamePosition;
                                        }
                                        else
                                        {
                                            spanQuery = new SpanNearQuery(new SpanQuery[]{spanQuery, atSamePosition}, gap + internalSlop, internalSlop < 2);
                                        }
                                        atSamePosition = new SpanOrQuery();
                                        atSamePosition.addClause(nextSpanQuery);
                                    }
                                }
                                gap = nextToken.getPositionIncrement();
                                
                            }
                            else
                            {
                                SpanQuery nextSpanQuery;
                                if ((termText != null) && (termText.contains("*") || termText.contains("?")))
                                {
                                    org.apache.lucene.search.WildcardQuery wildQuery = new org.apache.lucene.search.WildcardQuery(term);
                                    nextSpanQuery = new SpanMultiTermQueryWrapper<>(wildQuery);
                                }
                                else
                                {
                                    nextSpanQuery = new SpanTermQuery(term);
                                }
                                if(spanQuery == null)
                                {
                                    spanQuery = new SpanOrQuery();
                                    ((SpanOrQuery)spanQuery).addClause(nextSpanQuery);
                                }
                                else
                                {
                                    ((SpanOrQuery)spanQuery).addClause(nextSpanQuery);
                                }
                            }
                        }
                        if(atSamePosition.getClauses().length == 0)
                        {
                            spanOr.addClause(spanQuery);
                        }
                        else if(atSamePosition.getClauses().length == 1)
                        {
                            if(spanQuery == null)
                            {
                                spanQuery = atSamePosition.getClauses()[0];
                            }
                            else
                            {
                                spanQuery = new SpanNearQuery(new SpanQuery[]{spanQuery, atSamePosition.getClauses()[0]}, gap + internalSlop, internalSlop < 2);
                            }
                            atSamePosition = new SpanOrQuery();
                            spanOr.addClause(spanQuery);
                        }
                        else
                        {
                            if(spanQuery == null)
                            {
                                spanQuery = atSamePosition;
                            }
                            else
                            {
                                spanQuery = new SpanNearQuery(new SpanQuery[]{spanQuery, atSamePosition}, gap + internalSlop, internalSlop < 2);
                            }
                            atSamePosition = new SpanOrQuery();
                            spanOr.addClause(spanQuery);
                        }
                    }
                    return spanOr;
                }
            }
            else
            {
                SpanQuery spanQuery = null;
                SpanOrQuery atSamePosition = new SpanOrQuery();
                int gap = 0;
                for (int i = 0; i < list.size(); i++)
                {
                    nextToken = list.get(i);
                    String termText = nextToken.toString();
                    Term term = new Term(field, termText);
                    if (getEnablePositionIncrements())
                    {
                        SpanQuery nextSpanQuery;
                        if ((termText != null) && (termText.contains("*") || termText.contains("?")))
                        {
                            org.apache.lucene.search.WildcardQuery wildQuery = new org.apache.lucene.search.WildcardQuery(term);
                            nextSpanQuery = new SpanMultiTermQueryWrapper<>(wildQuery);
                        }
                        else
                        {
                            nextSpanQuery = new SpanTermQuery(term);
                        }
                        if(gap == 0)
                        {
                            atSamePosition.addClause(nextSpanQuery);
                        }
                        else
                        {
                            if(atSamePosition.getClauses().length == 0)
                            {
                                if(spanQuery == null)
                                {
                                    spanQuery = nextSpanQuery;
                                }
                                else
                                {
                                    spanQuery = new SpanNearQuery(new SpanQuery[]{spanQuery, nextSpanQuery}, gap + internalSlop, internalSlop < 2);
                                }
                                atSamePosition = new SpanOrQuery();
                            }
                            else if(atSamePosition.getClauses().length == 1)
                            {
                                if(spanQuery == null)
                                {
                                    spanQuery = atSamePosition.getClauses()[0];
                                }
                                else
                                {
                                    spanQuery = new SpanNearQuery(new SpanQuery[]{spanQuery, atSamePosition.getClauses()[0]}, gap + internalSlop, internalSlop < 2);
                                }
                                atSamePosition = new SpanOrQuery();
                                atSamePosition.addClause(nextSpanQuery);
                            }
                            else
                            {
                                if(spanQuery == null)
                                {
                                    spanQuery = atSamePosition;
                                }
                                else
                                {
                                    spanQuery = new SpanNearQuery(new SpanQuery[]{spanQuery, atSamePosition}, gap + internalSlop, internalSlop < 2);
                                }
                                atSamePosition = new SpanOrQuery();
                                atSamePosition.addClause(nextSpanQuery);
                            }
                        }
                        gap = nextToken.getPositionIncrement();
                    }
                    else
                    {
                        SpanQuery nextSpanQuery;
                        if ((termText != null) && (termText.contains("*") || termText.contains("?")))
                        {
                            org.apache.lucene.search.WildcardQuery wildQuery = new org.apache.lucene.search.WildcardQuery(term);
                            nextSpanQuery = new SpanMultiTermQueryWrapper<>(wildQuery);
                        }
                        else
                        {
                            nextSpanQuery = new SpanTermQuery(term);
                        }
                        if(spanQuery == null)
                        {
                            spanQuery = new SpanOrQuery();
                            ((SpanOrQuery)spanQuery).addClause(nextSpanQuery);
                        }
                        else
                        {
                            ((SpanOrQuery)spanQuery).addClause(nextSpanQuery);
                        }
                    }
                }
                if(atSamePosition.getClauses().length == 0)
                {
                    return spanQuery;
                }
                else if(atSamePosition.getClauses().length == 1)
                {
                    if(spanQuery == null)
                    {
                        spanQuery = atSamePosition.getClauses()[0];
                    }
                    else
                    {
                        spanQuery = new SpanNearQuery(new SpanQuery[]{spanQuery, atSamePosition.getClauses()[0]}, gap + internalSlop, internalSlop < 2);
                    }
                    return spanQuery;
                }
                else
                {
                    if(spanQuery == null)
                    {
                        spanQuery = atSamePosition;
                    }
                    else
                    {
                        spanQuery = new SpanNearQuery(new SpanQuery[]{spanQuery, atSamePosition}, gap + internalSlop, internalSlop < 2);
                    }
                    return spanQuery;
                }
            }
        }
    }

    /**
     * @param field
     * @param queryText
     * @param mpq
     * @return
     * @throws ParseException 
     */
    private void checkTermCount(String field, String queryText, MultiPhraseQuery mpq) throws ParseException
    {
        if(exceedsTermCount(mpq))
        {
            throw new ParseException("Wildcard has generated too many clauses: "+field+" "+queryText );
        }
    }
    
    /**
     * 
     * @param mpq
     * @return
     */
    private boolean exceedsTermCount(MultiPhraseQuery mpq)
    {
        int termCount = 0;
        for (Iterator<?> iter = mpq.getTermArrays().iterator(); iter.hasNext(); /**/) 
        {
            Term[] arr = (Term[])iter.next();
            termCount += arr.length;
            if(termCount > BooleanQuery.getMaxClauseCount())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @param fixedTokenSequences 
     * @return
     */
    private boolean canUseMultiPhraseQuery(LinkedList<LinkedList<Token>> fixedTokenSequences)
    {
        if(fixedTokenSequences.size() <= 1)
        {
            return true;
        }
        LinkedList<Token> first = fixedTokenSequences.get(0);
        for(int i = 1; i < fixedTokenSequences.size(); i++)
        {
            LinkedList<Token> current = fixedTokenSequences.get(i);
            if(first.size() != current.size())
            {
                return false;
            }
            for(int j = 0; j < first.size(); j++)
            {
                Token fromFirst = first.get(j);
                Token fromCurrent = current.get(j);
                if(fromFirst.startOffset() != fromCurrent.startOffset())
                {
                    return false;
                }   
                String termText = fromCurrent.toString();
                if ((termText != null) && (termText.contains("*") || termText.contains("?")))
                {
                    return false;
                }
            }
        }
        return true;
    }

    private Set<Integer> getWildcardPositions(String string)
    {
        HashSet<Integer> wildcardPositions = new HashSet<Integer>();

        boolean lastWasEscape = false;

        for (int i = 0; i < string.length(); i++)
        {
            char c = string.charAt(i);
            if (lastWasEscape)
            {
                lastWasEscape = false;
            }
            else
            {
                if (c == '\\')
                {
                    lastWasEscape = true;
                }
                else if (c == '*')
                {
                    wildcardPositions.add(i);
                }
                else if (c == '?')
                {
                    wildcardPositions.add(i);
                }
            }
        }

        return wildcardPositions;
    }


    private Term[] getMatchingTerms(String field, Term term) throws ParseException
    {
        ArrayList<Term> terms = new ArrayList<Term>();
        addWildcardTerms(terms, term);
        if (terms.size() == 0)
        {
            return new Term[] { new Term(field, "\u0000") };
        }
        else
        {
            return terms.toArray(new Term[0]);
        }

    }

    
    // TODO - resort to span if we have to ...
    private void addWildcardTerms(ArrayList<Term> terms, Term term) throws ParseException
    {
//        try
//        {
//            Term searchTerm = term;
//            if (getLowercaseExpandedTerms())
//            {
//                searchTerm = new Term(term.field(), term.text().toLowerCase());
//            }
//            WildcardTermEnum wcte = new WildcardTermEnum(indexReader, searchTerm);
//
//            do
//            {
//                Term current = wcte.term();
//                if(current != null)
//                {
//                    if ((current.text() != null) && (current.text().length() > 0) && (current.text().charAt(0) == '{'))
//                    {
//                        if ((searchTerm != null) && (searchTerm.text().length() > 0) && (searchTerm.text().charAt(0) == '{'))
//                        {
//                            terms.add(current);
//                        }
//                        // If not, we cod not add so wildcards do not match the locale prefix
//                    }
//                    else
//                    {
//                        terms.add(current);
//                    }
//                }
//            }
//            while(wcte.next());
//
//        }
//        catch (IOException e)
//        {
//            throw new ParseException("IO error generating phares wildcards " + e.getMessage());
//        }
        throw new UnsupportedOperationException();
    }

    /**
     * @exception ParseException
     *                throw in overridden method to disallow
     */
    
    protected Query getRangeQuery(String field, String part1, String part2,   boolean startInclusive,
            boolean endInclusive)  throws ParseException
    {
        return getRangeQuery(field, part1, part2, startInclusive, endInclusive, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    protected Query getRangeQuery(String field, String part1, String part2, boolean inclusive)  throws ParseException
    {
        return getRangeQuery(field, part1, part2, inclusive, inclusive, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }
    
    /**
     * @param field
     * @param part1
     * @param part2
     * @param includeLower
     * @param includeUpper
     * @param analysisMode
     * @param luceneFunction
     * @return the query
     * @exception ParseException
     *                throw in overridden method to disallow
     */
    public Query getRangeQuery(String field, String part1, String part2, boolean includeLower, boolean includeUpper, AnalysisMode analysisMode, LuceneFunction luceneFunction)
            throws ParseException
            {
        if (field.equals(FIELD_PATH))
        {
            throw new UnsupportedOperationException("Range Queries are not support for " + FIELD_PATH);
        }
        else if (field.equals(FIELD_PATHWITHREPEATS))
        {
            throw new UnsupportedOperationException("Range Queries are not support for " + FIELD_PATHWITHREPEATS);
        }
        else if (field.equals(FIELD_TEXT))
        {
            Set<String> text = searchParameters.getTextAttributes();
            if ((text == null) || (text.size() == 0))
            {
                Query query = getRangeQuery(PROPERTY_FIELD_PREFIX + ContentModel.PROP_CONTENT.toString(), part1, part2, includeLower, includeUpper, analysisMode, luceneFunction); 
                if (query == null)
                {
                    return createNoMatchQuery();
                }

                return query;
            }
            else
            {
                BooleanQuery query = new BooleanQuery();
                for (String fieldName : text)
                {
                    Query part = getRangeQuery(fieldName, part1, part2, includeLower, includeUpper, analysisMode, luceneFunction);
                    if (part != null)
                    {
                        query.add(part, Occur.SHOULD);
                    }
                    else
                    {
                        query.add(createNoMatchQuery(), Occur.SHOULD);
                    }
                }
                return query;
            }

        }
        // FIELD_ID uses the default
        // FIELD_DBID uses the default
        // FIELD_ISROOT uses the default
        // FIELD_ISCONTAINER uses the default
        // FIELD_ISNODE uses the default
        // FIELD_TX uses the default
        // FIELD_PARENT uses the default
        // FIELD_PRIMARYPARENT uses the default
        // FIELD_QNAME uses the default
        // FIELD_PRIMARYASSOCTYPEQNAME uses the default
        // FIELD_ASSOCTYPEQNAME uses the default
        // FIELD_CLASS uses the default
        // FIELD_TYPE uses the default
        // FIELD_EXACTTYPE uses the default
        // FIELD_ASPECT uses the default
        // FIELD_EXACTASPECT uses the default
        // FIELD_TYPE uses the default
        // FIELD_TYPE uses the default
        if (isPropertyField(field))
        {
            Pair<String, String> fieldNameAndEnding = QueryParserUtils.extractFieldNameAndEnding(field);

            String expandedFieldName = null;
            QName propertyQName;
            PropertyDefinition propertyDef = QueryParserUtils.matchPropertyDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService, fieldNameAndEnding.getFirst());
            IndexTokenisationMode tokenisationMode = IndexTokenisationMode.TRUE;
            if (propertyDef != null)
            {
                tokenisationMode = propertyDef.getIndexTokenisationMode();
                if (tokenisationMode == null)
                {
                    tokenisationMode = IndexTokenisationMode.TRUE;
                }
                propertyQName = propertyDef.getName();
            }
            else
            {
                expandedFieldName = expandAttributeFieldName(field);
                propertyQName = QName.createQName(fieldNameAndEnding.getFirst());
            }
            
            if (propertyDef != null)
            {
                // LOWER AND UPPER
                if (luceneFunction != LuceneFunction.FIELD)
                {
                    if(luceneFunction == LuceneFunction.LOWER)
                    {
                        if( (false == part1.toLowerCase().equals(part1)) || (false == part2.toLowerCase().equals(part2)) )
                        {
                            return createNoMatchQuery();
                        }
                    }

                    if(luceneFunction == LuceneFunction.UPPER)
                    {
                        if( (false == part1.toUpperCase().equals(part1)) || (false == part2.toUpperCase().equals(part2)) )
                        {
                            return createNoMatchQuery();
                        }
                    }

                    if (propertyDef.getDataType().getName().equals(DataTypeDefinition.TEXT))
                    {
                        BooleanQuery booleanQuery = new BooleanQuery();
                        List<Locale> locales = searchParameters.getLocales();
                        List<Locale> expandedLocales = new ArrayList<Locale>();
                        for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
                        {
                            expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, false));
                        }
                        for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
                        {
                            addLocaleSpecificUntokenisedTextRangeFunction(expandedFieldName, propertyDef, part1, part2, includeLower, includeUpper, luceneFunction, booleanQuery, mlAnalysisMode,
                                    locale, tokenisationMode);
                        }
                        return booleanQuery;
                    }
                    else
                    {
                        throw new UnsupportedOperationException("Lucene Function");
                    }
                }

                if (propertyDef.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
                {
                    throw new UnsupportedOperationException("Range is not supported against ml-text");
                }
                else if (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT))
                {
                    if (fieldNameAndEnding.getSecond().equals(FIELD_SIZE_SUFFIX))
                    {
                        String solrField = AlfrescoSolrDataModel.getInstance().getQueryableFields(propertyDef.getName(), ContentFieldType.SIZE, FieldUse.ID).getFields().get(0).getField();
                        String start = null;
                        try
                        {
                            analyzeMultitermTerm(solrField, part1, null);
                            start = part1;
                        }
                        catch(Exception e)
                        {
                            
                        }
                        String end = null;
                        try
                        {
                            analyzeMultitermTerm(solrField, part2, null);
                            end = part2;
                        }
                        catch(Exception e)
                        {
                            
                        }
                        
                        SchemaField sf = schema.getField(solrField);
                        return sf.getType().getRangeQuery(null, sf, start, end, includeLower, includeUpper);
                        
                    }
                    else 
                    {
                        throw new UnsupportedOperationException("Range is not supported against content");
                    }
                }
                else if (propertyDef.getDataType().getName().equals(DataTypeDefinition.TEXT))
                {
                    BooleanQuery booleanQuery = new BooleanQuery();
                    List<Locale> locales = searchParameters.getLocales();
                    List<Locale> expandedLocales = new ArrayList<Locale>();
                    for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
                    {
                        expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, false));
                    }
                    for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
                    {

                        addTextRange(field, propertyDef, part1, part2, includeLower, includeUpper, analysisMode, expandedFieldName, propertyDef, tokenisationMode, booleanQuery, mlAnalysisMode, locale);

                    }
                    return booleanQuery;
                }
                else if (propertyDef.getDataType().getName().equals(DataTypeDefinition.DATETIME) || propertyDef.getDataType().getName().equals(DataTypeDefinition.DATE))
                {
                    Pair<Date, Integer> dateAndResolution1 = parseDateString(part1);
                    Pair<Date, Integer> dateAndResolution2 = parseDateString(part2);
                    
                    BooleanQuery bQuery = new BooleanQuery(); 
                    IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getQueryableFields(propertyDef.getName(), null, FieldUse.ID);
                    for(FieldInstance instance : indexedField.getFields())
                    {
                        String start = dateAndResolution1 == null ? part1 : (includeLower ? getDateStart(dateAndResolution1) : getDateEnd(dateAndResolution1) );
                        String end = dateAndResolution2 == null ? part2 : (includeUpper ? getDateEnd(dateAndResolution2) : getDateStart(dateAndResolution2) );

                        SchemaField sf = schema.getField(instance.getField());
                        
                        Query query =  sf.getType().getRangeQuery(null, sf, start, end, includeLower, includeUpper);
                        if(query != null)
                        {
                            bQuery.add(query,Occur.SHOULD);
                        }
                    }
                    return bQuery;
                }
                else
                {
                    String solrField = AlfrescoSolrDataModel.getInstance().getQueryableFields(propertyDef.getName(), null, FieldUse.ID).getFields().get(0).getField();
                          
                    String start = null;
                    try
                    {
                        analyzeMultitermTerm(solrField, part1, null);
                        start = part1;
                    }
                    catch(Exception e)
                    {
                        
                    }
                    String end = null;
                    try
                    {
                        analyzeMultitermTerm(solrField, part2, null);
                        end = part2;
                    }
                    catch(Exception e)
                    {
                        
                    }
                    
                    SchemaField sf = schema.getField(solrField);
                    return sf.getType().getRangeQuery(null, sf, start, end, includeLower, includeUpper);
                }
            }
            else
            {
                throw new UnsupportedOperationException();
            }
        }
        else if (field.equals(FIELD_ALL))
        {
            Set<String> all = searchParameters.getAllAttributes();
            if ((all == null) || (all.size() == 0))
            {
                Collection<QName> contentAttributes = dictionaryService.getAllProperties(null);
                BooleanQuery query = new BooleanQuery();
                for (QName qname : contentAttributes)
                {
                    Query part = getRangeQuery(PROPERTY_FIELD_PREFIX + qname.toString(), part1, part2, includeLower, includeUpper, analysisMode, luceneFunction);
                    query.add(part, Occur.SHOULD);
                }
                return query;
            }
            else
            {
                BooleanQuery query = new BooleanQuery();
                for (String fieldName : all)
                {
                    Query part = getRangeQuery(fieldName, part1, part2, includeLower, includeUpper, analysisMode, luceneFunction);
                    query.add(part, Occur.SHOULD);
                }
                return query;
            }

        }
        // FIELD_ISUNSET uses the default
        // FIELD_ISNULL uses the default
        // FIELD_ISNOTNULL uses the default
        else if (QueryParserUtils.matchDataTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService,field) != null)
        {
            Collection<QName> contentAttributes = dictionaryService.getAllProperties(QueryParserUtils.matchDataTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService,field).getName());
            BooleanQuery query = new BooleanQuery();
            for (QName qname : contentAttributes)
            {
                Query part = getRangeQuery(PROPERTY_FIELD_PREFIX + qname.toString(), part1, part2, includeLower, includeUpper, analysisMode, luceneFunction);
                query.add(part, Occur.SHOULD);
            }
            return query;
        }
        // FIELD_FTSSTATUS uses the default
        if (field.equals(FIELD_TAG))
        {
            throw new UnsupportedOperationException("Range Queries are not support for "+FIELD_TAG);
        }
        else
        {
            // None property - leave alone
            throw new UnsupportedOperationException();
        }
    }


    private String expandAttributeFieldName(String field)
    {
        return PROPERTY_FIELD_PREFIX + QueryParserUtils.expandQName(searchParameters.getNamespace(), namespacePrefixResolver, field.substring(1));
    }

   
    protected String getToken(String field, String value, AnalysisMode analysisMode) throws ParseException
    {
        try(TokenStream source = getAnalyzer().tokenStream(field, new StringReader(value)))
        {
            org.apache.lucene.analysis.Token reusableToken = new org.apache.lucene.analysis.Token();
            org.apache.lucene.analysis.Token nextToken;
            String tokenised = null;

            while (source.incrementToken())
            {
                CharTermAttribute cta = source.getAttribute(CharTermAttribute.class);
                OffsetAttribute offsetAtt = source.getAttribute(OffsetAttribute.class);
                TypeAttribute typeAtt = null;
                if(source.hasAttribute(TypeAttribute.class))
                {
                    typeAtt = source.getAttribute(TypeAttribute.class);
                }
                PositionIncrementAttribute posIncAtt = null;
                if(source.hasAttribute(PositionIncrementAttribute.class))
                {
                    posIncAtt = source.getAttribute(PositionIncrementAttribute.class);
                }
                Token token = new Token(cta.buffer(), 0, cta.length(), offsetAtt.startOffset(), offsetAtt.endOffset());
                if(typeAtt != null)
                {
                    token.setType(typeAtt.type());
                }
                if(posIncAtt != null)
                {
                    token.setPositionIncrement(posIncAtt.getPositionIncrement());
                }

                tokenised = token.toString();
            }

            source.close();
            return tokenised;
        }
        catch (IOException e)
        {
            throw new ParseException("IO" + e.getMessage());
        }
    

       
    }

    @Override
    public Query getPrefixQuery(String field, String termStr) throws ParseException
    {
        return getPrefixQuery(field, termStr, AnalysisMode.PREFIX);
    }


    public Query getPrefixQuery(String field, String termStr, AnalysisMode analysisMode) throws ParseException
    {
        if (field.equals(FIELD_PATH))
        {
            throw new UnsupportedOperationException("Prefix Queries are not support for " + FIELD_PATH);
        }
        else if (field.equals(FIELD_PATHWITHREPEATS))
        {
            throw new UnsupportedOperationException("Prefix Queries are not support for " + FIELD_PATHWITHREPEATS);
        }
        else if (field.equals(FIELD_TEXT))
        {
            Set<String> text = searchParameters.getTextAttributes();
            if ((text == null) || (text.size() == 0))
            {
                Query query =  getPrefixQuery(PROPERTY_FIELD_PREFIX + ContentModel.PROP_CONTENT.toString(), termStr, analysisMode); 
                if (query == null)
                {
                    return createNoMatchQuery();
                }

                return query;      
            }
            else
            {
                BooleanQuery query = new BooleanQuery();
                for (String fieldName : text)
                {
                    Query part = getPrefixQuery(fieldName, termStr, analysisMode);
                    if (part != null)
                    {
                        query.add(part, Occur.SHOULD);
                    }
                    else
                    {
                        query.add(createNoMatchQuery(), Occur.SHOULD);
                    }
                }
                return query;
            }
        }
        else if (field.equals(FIELD_ID))
        {
            boolean lowercaseExpandedTerms = getLowercaseExpandedTerms();
            try
            {
                setLowercaseExpandedTerms(false);
                return super.getPrefixQuery(FIELD_LID, termStr);
            }
            finally
            {
                setLowercaseExpandedTerms(lowercaseExpandedTerms);
            }
        }
        else if (field.equals(FIELD_DBID) || field.equals(FIELD_ISROOT) || field.equals(FIELD_ISCONTAINER) || field.equals(FIELD_ISNODE) || field.equals(FIELD_TX)
                || field.equals(FIELD_PARENT) || field.equals(FIELD_PRIMARYPARENT) || field.equals(FIELD_QNAME) || field.equals(FIELD_PRIMARYASSOCTYPEQNAME)
                || field.equals(FIELD_ASSOCTYPEQNAME))
        {
            boolean lowercaseExpandedTerms = getLowercaseExpandedTerms();
            try
            {
                setLowercaseExpandedTerms(false);
                return super.getPrefixQuery(field, termStr);
            }
            finally
            {
                setLowercaseExpandedTerms(lowercaseExpandedTerms);
            }
        }
        else if (field.equals(FIELD_CLASS))
        {
            return super.getPrefixQuery(field, termStr);
            // throw new UnsupportedOperationException("Prefix Queries are not support for "+FIELD_CLASS);
        }
        else if (field.equals(FIELD_TYPE))
        {
            return super.getPrefixQuery(field, termStr);
            // throw new UnsupportedOperationException("Prefix Queries are not support for "+FIELD_TYPE);
        }
        else if (field.equals(FIELD_EXACTTYPE))
        {
            return super.getPrefixQuery(field, termStr);
            // throw new UnsupportedOperationException("Prefix Queries are not support for "+FIELD_EXACTTYPE);
        }
        else if (field.equals(FIELD_ASPECT))
        {
            return super.getPrefixQuery(field, termStr);
            // throw new UnsupportedOperationException("Prefix Queries are not support for "+FIELD_ASPECT);
        }
        else if (field.equals(FIELD_EXACTASPECT))
        {
            return super.getPrefixQuery(field, termStr);
            // throw new UnsupportedOperationException("Prefix Queries are not support for "+FIELD_EXACTASPECT);
        }
        else if (isPropertyField(field))
        {
            return attributeQueryBuilder(field, termStr, new PrefixQuery(), analysisMode, LuceneFunction.FIELD);
        }
        else if (field.equals(FIELD_ALL))
        {
            Set<String> all = searchParameters.getAllAttributes();
            if ((all == null) || (all.size() == 0))
            {
                Collection<QName> contentAttributes = dictionaryService.getAllProperties(null);
                BooleanQuery query = new BooleanQuery();
                for (QName qname : contentAttributes)
                {
                    // The super implementation will create phrase queries etc if required
                    Query part = getPrefixQuery(PROPERTY_FIELD_PREFIX + qname.toString(), termStr, analysisMode);
                    if (part != null)
                    {
                        query.add(part, Occur.SHOULD);
                    }
                    else
                    {
                        query.add(createNoMatchQuery(), Occur.SHOULD);
                    }
                }
                return query;
            }
            else
            {
                BooleanQuery query = new BooleanQuery();
                for (String fieldName : all)
                {
                    Query part = getPrefixQuery(fieldName, termStr, analysisMode);
                    if (part != null)
                    {
                        query.add(part, Occur.SHOULD);
                    }
                    else
                    {
                        query.add(createNoMatchQuery(), Occur.SHOULD);
                    }
                }
                return query;
            }
        }
        else if (field.equals(FIELD_ISUNSET))
        {
            throw new UnsupportedOperationException("Prefix Queries are not support for " + FIELD_ISUNSET);
        }
        else if (field.equals(FIELD_ISNULL))
        {
            throw new UnsupportedOperationException("Prefix Queries are not support for " + FIELD_ISNULL);
        }
        else if (field.equals(FIELD_ISNOTNULL))
        {
            throw new UnsupportedOperationException("Prefix Queries are not support for " + FIELD_ISNOTNULL);
        }
        else if (QueryParserUtils.matchDataTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService,field) != null)
        {
            Collection<QName> contentAttributes = dictionaryService.getAllProperties(QueryParserUtils.matchDataTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService,field).getName());
            BooleanQuery query = new BooleanQuery();
            for (QName qname : contentAttributes)
            {
                // The super implementation will create phrase queries etc if required
                Query part = getPrefixQuery(PROPERTY_FIELD_PREFIX + qname.toString(), termStr, analysisMode);
                if (part != null)
                {
                    query.add(part, Occur.SHOULD);
                }
                else
                {
                    query.add(createNoMatchQuery(), Occur.SHOULD);
                }
            }
            return query;
        }
        else if (field.equals(FIELD_FTSSTATUS))
        {
            throw new UnsupportedOperationException("Prefix Queries are not support for " + FIELD_FTSSTATUS);
        }
        else if (field.equals(FIELD_TAG))
        {
            return super.getPrefixQuery(field, termStr);
        }
        else if (field.equals(FIELD_SITE))
        {
            return super.getPrefixQuery(field, termStr);
        }
        else if (field.equals(FIELD_NPATH))
        {
            return super.getPrefixQuery(field, termStr);
        }
        else if (field.equals(FIELD_PNAME))
        {
            return super.getPrefixQuery(field, termStr);
        }
        else
        {
            return super.getPrefixQuery(field, termStr);
        }
    }

    @Override
    public Query getWildcardQuery(String field, String termStr) throws ParseException
    {
        return getWildcardQuery(field, termStr, AnalysisMode.WILD);
    }

    public Query getWildcardQuery(String field, String termStr, AnalysisMode analysisMode) throws ParseException
    {
        if (field.equals(FIELD_PATH))
        {
            throw new UnsupportedOperationException("Wildcard Queries are not support for " + FIELD_PATH);
        }
        else if (field.equals(FIELD_PATHWITHREPEATS))
        {
            throw new UnsupportedOperationException("Wildcard Queries are not support for " + FIELD_PATHWITHREPEATS);
        }
        else if (field.equals(FIELD_TEXT))
        {
            Set<String> text = searchParameters.getTextAttributes();
            if ((text == null) || (text.size() == 0))
            {
                Query query =  getWildcardQuery(PROPERTY_FIELD_PREFIX + ContentModel.PROP_CONTENT.toString(), termStr, analysisMode); 
                if (query == null)
                {
                    return createNoMatchQuery();
                }

                return query;
            }
            else
            {
                BooleanQuery query = new BooleanQuery();
                for (String fieldName : text)
                {
                    Query part = getWildcardQuery(fieldName, termStr, analysisMode);
                    if (part != null)
                    {
                        query.add(part, Occur.SHOULD);
                    }
                    else
                    {
                        query.add(createNoMatchQuery(), Occur.SHOULD);
                    }
                }
                return query;
            }
        }
        else if (field.equals(FIELD_ID))
        {
            boolean lowercaseExpandedTerms = getLowercaseExpandedTerms();
            try
            {
                setLowercaseExpandedTerms(false);
                return super.getWildcardQuery(FIELD_LID, termStr);
            }
            finally
            {
                setLowercaseExpandedTerms(lowercaseExpandedTerms);
            }
        }
        else if (field.equals(FIELD_DBID) || field.equals(FIELD_ISROOT) || field.equals(FIELD_ISCONTAINER) || field.equals(FIELD_ISNODE) || field.equals(FIELD_TX)
                || field.equals(FIELD_PARENT) || field.equals(FIELD_PRIMARYPARENT) || field.equals(FIELD_QNAME) || field.equals(FIELD_PRIMARYASSOCTYPEQNAME)
                || field.equals(FIELD_ASSOCTYPEQNAME))
        {
            boolean lowercaseExpandedTerms = getLowercaseExpandedTerms();
            try
            {
                setLowercaseExpandedTerms(false);
                return super.getWildcardQuery(field, termStr);
            }
            finally
            {
                setLowercaseExpandedTerms(lowercaseExpandedTerms);
            }
        }
        else if (field.equals(FIELD_CLASS))
        {
            return super.getWildcardQuery(field, termStr);
            // throw new UnsupportedOperationException("Wildcard Queries are not support for "+FIELD_CLASS);
        }
        else if (field.equals(FIELD_TYPE))
        {
            return super.getWildcardQuery(field, termStr);
            // throw new UnsupportedOperationException("Wildcard Queries are not support for "+FIELD_TYPE);
        }
        else if (field.equals(FIELD_EXACTTYPE))
        {
            return super.getWildcardQuery(field, termStr);
            // throw new UnsupportedOperationException("Wildcard Queries are not support for "+FIELD_EXACTTYPE);
        }
        else if (field.equals(FIELD_ASPECT))
        {
            return super.getWildcardQuery(field, termStr);
            // throw new UnsupportedOperationException("Wildcard Queries are not support for "+FIELD_ASPECT);
        }
        else if (field.equals(FIELD_EXACTASPECT))
        {
            return super.getWildcardQuery(field, termStr);
            // throw new UnsupportedOperationException("Wildcard Queries are not support for "+FIELD_EXACTASPECT);
        }
        else if (isPropertyField(field))
        {
            return attributeQueryBuilder(field, termStr, new WildcardQuery(), analysisMode, LuceneFunction.FIELD);
        }
        else if (field.equals(FIELD_ALL))
        {
            Set<String> all = searchParameters.getAllAttributes();
            if ((all == null) || (all.size() == 0))
            {
                Collection<QName> contentAttributes = dictionaryService.getAllProperties(null);
                BooleanQuery query = new BooleanQuery();
                for (QName qname : contentAttributes)
                {
                    // The super implementation will create phrase queries etc if required
                    Query part = getWildcardQuery(PROPERTY_FIELD_PREFIX + qname.toString(), termStr, analysisMode);
                    if (part != null)
                    {
                        query.add(part, Occur.SHOULD);
                    }
                    else
                    {
                        query.add(createNoMatchQuery(), Occur.SHOULD);
                    }
                }
                return query;
            }
            else
            {
                BooleanQuery query = new BooleanQuery();
                for (String fieldName : all)
                {
                    Query part = getWildcardQuery(fieldName, termStr, analysisMode);
                    if (part != null)
                    {
                        query.add(part, Occur.SHOULD);
                    }
                    else
                    {
                        query.add(createNoMatchQuery(), Occur.SHOULD);
                    }
                }
                return query;
            }
        }
        else if (field.equals(FIELD_ISUNSET))
        {
            throw new UnsupportedOperationException("Wildcard Queries are not support for " + FIELD_ISUNSET);
        }
        else if (field.equals(FIELD_ISNULL))
        {
            throw new UnsupportedOperationException("Wildcard Queries are not support for " + FIELD_ISNULL);
        }
        else if (field.equals(FIELD_ISNOTNULL))
        {
            throw new UnsupportedOperationException("Wildcard Queries are not support for " + FIELD_ISNOTNULL);
        }
        else if (QueryParserUtils.matchDataTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService,field) != null)
        {
            Collection<QName> contentAttributes = dictionaryService.getAllProperties(QueryParserUtils.matchDataTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService,field).getName());
            BooleanQuery query = new BooleanQuery();
            for (QName qname : contentAttributes)
            {
                // The super implementation will create phrase queries etc if required
                Query part = getWildcardQuery(PROPERTY_FIELD_PREFIX + qname.toString(), termStr, analysisMode);
                if (part != null)
                {
                    query.add(part, Occur.SHOULD);
                }
                else
                {
                    query.add(createNoMatchQuery(), Occur.SHOULD);
                }
            }
            return query;
        }
        else if (field.equals(FIELD_FTSSTATUS))
        {
            throw new UnsupportedOperationException("Wildcard Queries are not support for " + FIELD_FTSSTATUS);
        }
        else if (field.equals(FIELD_TAG))
        {
            return super.getWildcardQuery(field, termStr);
        }
        else if (field.equals(FIELD_SITE))
        {
            return super.getWildcardQuery(field, termStr);
        }
        else if (field.equals(FIELD_PNAME))
        {
            return super.getWildcardQuery(field, termStr);
        }
        else if (field.equals(FIELD_NPATH))
        {
            return super.getWildcardQuery(field, termStr);
        }
        else
        {
            return super.getWildcardQuery(field, termStr);
        }
    }

    @Override
    public Query getFuzzyQuery(String field, String termStr, float minSimilarity) throws ParseException
    {
        if (field.equals(FIELD_PATH))
        {
            throw new UnsupportedOperationException("Fuzzy Queries are not support for " + FIELD_PATH);
        }
        else if (field.equals(FIELD_PATHWITHREPEATS))
        {
            throw new UnsupportedOperationException("Fuzzy Queries are not support for " + FIELD_PATHWITHREPEATS);
        }
        else if (field.equals(FIELD_TEXT))
        {
            Set<String> text = searchParameters.getTextAttributes();
            if ((text == null) || (text.size() == 0))
            {
                Query query =  getFuzzyQuery(PROPERTY_FIELD_PREFIX + ContentModel.PROP_CONTENT.toString(), termStr, minSimilarity);
                if (query == null)
                {
                    return createNoMatchQuery();
                }

                return query;
            }
            else
            {
                BooleanQuery query = new BooleanQuery();
                for (String fieldName : text)
                {
                    Query part = getFuzzyQuery(fieldName, termStr, minSimilarity);
                    if (part != null)
                    {
                        query.add(part, Occur.SHOULD);
                    }
                    else
                    {
                        query.add(createNoMatchQuery(), Occur.SHOULD);
                    }
                }
                return query;
            }
        }
        else if (field.equals(FIELD_ID)
                || field.equals(FIELD_DBID) || field.equals(FIELD_ISROOT) || field.equals(FIELD_ISCONTAINER) || field.equals(FIELD_ISNODE) || field.equals(FIELD_TX)
                || field.equals(FIELD_PARENT) || field.equals(FIELD_PRIMARYPARENT) || field.equals(FIELD_QNAME) || field.equals(FIELD_PRIMARYASSOCTYPEQNAME)
                || field.equals(FIELD_ASSOCTYPEQNAME))
        {
            boolean lowercaseExpandedTerms = getLowercaseExpandedTerms();
            try
            {
                setLowercaseExpandedTerms(false);
                return super.getFuzzyQuery(field, termStr, minSimilarity);
            }
            finally
            {
                setLowercaseExpandedTerms(lowercaseExpandedTerms);
            }
        }
        else if (field.equals(FIELD_CLASS))
        {
            throw new UnsupportedOperationException("Fuzzy Queries are not support for " + FIELD_CLASS);
        }
        else if (field.equals(FIELD_TYPE))
        {
            throw new UnsupportedOperationException("Fuzzy Queries are not support for " + FIELD_TYPE);
        }
        else if (field.equals(FIELD_EXACTTYPE))
        {
            throw new UnsupportedOperationException("Fuzzy Queries are not support for " + FIELD_EXACTTYPE);
        }
        else if (field.equals(FIELD_ASPECT))
        {
            throw new UnsupportedOperationException("Fuzzy Queries are not support for " + FIELD_ASPECT);
        }
        else if (field.equals(FIELD_EXACTASPECT))
        {
            throw new UnsupportedOperationException("Fuzzy Queries are not support for " + FIELD_EXACTASPECT);
        }
        else if (isPropertyField(field))
        {
            return attributeQueryBuilder(field, termStr, new FuzzyQuery(minSimilarity), AnalysisMode.FUZZY, LuceneFunction.FIELD);
        }
        else if (field.equals(FIELD_ALL))
        {
            Set<String> all = searchParameters.getAllAttributes();
            if ((all == null) || (all.size() == 0))
            {
                Collection<QName> contentAttributes = dictionaryService.getAllProperties(null);
                BooleanQuery query = new BooleanQuery();
                for (QName qname : contentAttributes)
                {
                    // The super implementation will create phrase queries etc if required
                    Query part = getFuzzyQuery(PROPERTY_FIELD_PREFIX + qname.toString(), termStr, minSimilarity);
                    if (part != null)
                    {
                        query.add(part, Occur.SHOULD);
                    }
                    else
                    {
                        query.add(createNoMatchQuery(), Occur.SHOULD);
                    }
                }
                return query;
            }
            else
            {
                BooleanQuery query = new BooleanQuery();
                for (String fieldName : all)
                {
                    Query part = getFuzzyQuery(fieldName, termStr, minSimilarity);
                    if (part != null)
                    {
                        query.add(part, Occur.SHOULD);
                    }
                    else
                    {
                        query.add(createNoMatchQuery(), Occur.SHOULD);
                    }
                }
                return query;
            }
        }
        else if (field.equals(FIELD_ISUNSET))
        {
            throw new UnsupportedOperationException("Fuzzy Queries are not support for " + FIELD_ISUNSET);
        }
        else if (field.equals(FIELD_ISNULL))
        {
            throw new UnsupportedOperationException("Fuzzy Queries are not support for " + FIELD_ISNULL);
        }
        else if (field.equals(FIELD_ISNOTNULL))
        {
            throw new UnsupportedOperationException("Fuzzy Queries are not support for " + FIELD_ISNOTNULL);
        }
        else if (QueryParserUtils.matchDataTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService,field) != null)
        {
            Collection<QName> contentAttributes = dictionaryService.getAllProperties(QueryParserUtils.matchDataTypeDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService,field).getName());
            BooleanQuery query = new BooleanQuery();
            for (QName qname : contentAttributes)
            {
                // The super implementation will create phrase queries etc if required
                Query part = getFuzzyQuery(PROPERTY_FIELD_PREFIX + qname.toString(), termStr, minSimilarity);
                if (part != null)
                {
                    query.add(part, Occur.SHOULD);
                }
                else
                {
                    query.add(createNoMatchQuery(), Occur.SHOULD);
                }
            }
            return query;
        }
        else if (field.equals(FIELD_FTSSTATUS))
        {
            throw new UnsupportedOperationException("Fuzzy Queries are not support for " + FIELD_FTSSTATUS);
        }
        else if (field.equals(FIELD_TAG))
        {
            return super.getFuzzyQuery(field, termStr, minSimilarity);
        } 
        else if (field.equals(FIELD_SITE))
        {
            return super.getFuzzyQuery(field, termStr, minSimilarity);
        } 
        else if (field.equals(FIELD_PNAME))
        {
            return super.getFuzzyQuery(field, termStr, minSimilarity);
        } 
        else if (field.equals(FIELD_NPATH))
        {
            return super.getFuzzyQuery(field, termStr, minSimilarity);
        } 
        else
        {
            return super.getFuzzyQuery(field, termStr, minSimilarity);
        }
    }

    /**
     * @param dictionaryService
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * @param field
     * @param queryText
     * @param analysisMode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     */
    public Query getSuperFieldQuery(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        return getFieldQueryImplWithIOExceptionWrapped(field, queryText, analysisMode, luceneFunction);
    }

    /**
     * @param field
     * @param termStr
     * @param minSimilarity
     * @return the query
     * @throws ParseException
     */
    public Query getSuperFuzzyQuery(String field, String termStr, float minSimilarity) throws ParseException
    {
        return super.getFuzzyQuery(field, termStr, minSimilarity);
    }

    /**
     * @param field
     * @param termStr
     * @return the query
     * @throws ParseException
     */
    public Query getSuperPrefixQuery(String field, String termStr) throws ParseException
    {
        return super.getPrefixQuery(field, termStr);
    }

    /**
     * @param field
     * @param termStr
     * @return the query
     * @throws ParseException
     */
    public Query getSuperWildcardQuery(String field, String termStr) throws ParseException
    {
        return super.getWildcardQuery(field, termStr);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.lucene.queryParser.QueryParser#newWildcardQuery(org.apache.lucene.index.Term)
     */
    @Override
    protected Query newWildcardQuery(Term t)
    {
        if (t.text().contains("\\"))
        {
            String regexp = SearchLanguageConversion.convert(SearchLanguageConversion.DEF_LUCENE, SearchLanguageConversion.DEF_REGEX, t.text());
            return new RegexpQuery(new Term(t.field(), regexp));
        }
        else
        {
            return super.newWildcardQuery(t);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.lucene.queryParser.QueryParser#newPrefixQuery(org.apache.lucene.index.Term)
     */
    @Override
    protected Query newPrefixQuery(Term prefix)
    {
        if (prefix.text().contains("\\"))
        {
            String regexp = SearchLanguageConversion.convert(SearchLanguageConversion.DEF_LUCENE, SearchLanguageConversion.DEF_REGEX, prefix.text());
            return new RegexpQuery(new Term(prefix.field(), regexp));
        }
        else
        {
            return super.newPrefixQuery(prefix);
        }

    }

    public interface SubQuery
    {
        /**
         * @param field
         * @param queryText
         * @param analysisMode
         * @param luceneFunction
         * @return the query
         * @throws ParseException
         */
        Query getQuery(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException;
    }

    class FieldQuery implements SubQuery
    {
        public Query getQuery(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
        {
            return getSuperFieldQuery(field, queryText, analysisMode, luceneFunction);
        }
    }

    class FuzzyQuery implements SubQuery
    {
        float minSimilarity;

        FuzzyQuery(float minSimilarity)
        {
            this.minSimilarity = minSimilarity;
        }

        public Query getQuery(String field, String termStr, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
        {
            return getSuperFuzzyQuery(field, translateLocale(termStr), minSimilarity);
        }
    }

    private String translateLocale(String localised)
    {
        if(localised.startsWith("\u0000"))
        {
            if(localised.startsWith("\u0000\u0000"))
            {
                if(localised.length() < 3)
                {
                    return "";
                }
                else
                {
                    return localised.substring(2);
                }
            }
            else
            {
                int end = localised.indexOf('\u0000', 1);
                if(end == -1)
                {
                    return localised;
                }
                else
                {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("{");
                    buffer.append(localised.substring(1, end));
                    buffer.append("}");
                    buffer.append(localised.substring(end+1));
                    return buffer.toString();
                }
            }
        }
        else
        {
            return localised;
        }
    }

    class PrefixQuery implements SubQuery
    {
        public Query getQuery(String field, String termStr, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
        {
            StringBuilder builder = new StringBuilder(termStr.length()+1);
            builder.append(termStr);
            builder.append("*");
            return getSuperFieldQuery(field, builder.toString(), analysisMode, luceneFunction);
            //return getSuperPrefixQuery(field, termStr);
        }
    }

    class WildcardQuery implements SubQuery
    {
        public Query getQuery(String field, String termStr, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
        {
            return getSuperFieldQuery(field, termStr, analysisMode, luceneFunction);
            //return getSuperWildcardQuery(field, termStr);
        }
    }

    private Query spanQueryBuilder(String field, String first, String last, int slop, boolean inOrder) throws ParseException
    {
        String propertyFieldName = field.substring(1);
        String expandedFieldName = null;
        
        PropertyDefinition propertyDef = QueryParserUtils.matchPropertyDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService, propertyFieldName);
        IndexTokenisationMode tokenisationMode = IndexTokenisationMode.TRUE;
        if (propertyDef != null)
        {
            tokenisationMode = propertyDef.getIndexTokenisationMode();
            if (tokenisationMode == null)
            {
                tokenisationMode = IndexTokenisationMode.TRUE;
            }
            QName propertyQName = propertyDef.getName();
        }
        else
        {
            expandedFieldName = expandAttributeFieldName(field);
        }


        if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.MLTEXT)))
        {
            // Build a sub query for each locale and or the results together - the analysis will take care of
            // cross language matching for each entry
            BooleanQuery booleanQuery = new BooleanQuery();
            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, false));
            }
            for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
            {
                addMLTextSpanQuery(field, propertyDef, first, last, slop, inOrder, expandedFieldName, propertyDef, tokenisationMode, booleanQuery,
                        mlAnalysisMode, locale);
            }
            return booleanQuery;
        }
        // Content
        else if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
        {

            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, addContentCrossLocaleWildcards()));
            }

            return addContentSpanQuery(field, propertyDef, first, last, slop, inOrder, expandedFieldName, expandedLocales, mlAnalysisMode);

        }
        else if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.TEXT)))
        {
            BooleanQuery booleanQuery = new BooleanQuery();
            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, false));
            }
            for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
            {

                addTextSpanQuery(field, propertyDef, first, last, slop, inOrder, expandedFieldName, tokenisationMode, booleanQuery, mlAnalysisMode, locale);

            }
            return booleanQuery;
        }
        else
        {
            throw new UnsupportedOperationException("Span queries are only supported for d:text, d:mltext and d:content data types");    
        }
    }

 
    
    
    private Query attributeQueryBuilder(String field, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        // TODO: Fix duplicate token generation for mltext, content and text.
        // -locale expansion here and in tokeisation -> duplicates

        // Get type info etc

        // TODO: additional suffixes
        
        Pair<String, String> fieldNameAndEnding = QueryParserUtils.extractFieldNameAndEnding(field);

        String expandedFieldName = null;
        QName propertyQName;
        PropertyDefinition propertyDef = QueryParserUtils.matchPropertyDefinition(searchParameters.getNamespace(), namespacePrefixResolver, dictionaryService, fieldNameAndEnding.getFirst());
        IndexTokenisationMode tokenisationMode = IndexTokenisationMode.TRUE;
        if (propertyDef != null)
        {
            tokenisationMode = propertyDef.getIndexTokenisationMode();
            if (tokenisationMode == null)
            {
                tokenisationMode = IndexTokenisationMode.TRUE;
            }
            propertyQName = propertyDef.getName();
        }
        else
        {
            expandedFieldName = expandAttributeFieldName(field);
            propertyQName = QName.createQName(fieldNameAndEnding.getFirst());
        }

        if (luceneFunction != LuceneFunction.FIELD)
        {
            if ((tokenisationMode == IndexTokenisationMode.FALSE) || (tokenisationMode == IndexTokenisationMode.BOTH))
            {
                if(luceneFunction == LuceneFunction.LOWER)
                {
                    if(false == queryText.toLowerCase().equals(queryText))
                    {
                        return createNoMatchQuery();
                    }
                }
                if(luceneFunction == LuceneFunction.UPPER)
                {
                    if(false == queryText.toUpperCase().equals(queryText))
                    {
                        return createNoMatchQuery();
                    }
                }

                return functionQueryBuilder(expandedFieldName, fieldNameAndEnding.getSecond(), propertyQName, propertyDef, tokenisationMode, queryText, luceneFunction);
            }
        }

        // Mime type
        if (fieldNameAndEnding.getSecond().equals(FIELD_MIMETYPE_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                return subQueryBuilder.getQuery(AlfrescoSolrDataModel.getInstance().getQueryableFields(propertyQName, ContentFieldType.MIMETYPE, FieldUse.ID).getFields().get(0).getField(), queryText, analysisMode, luceneFunction);
            }

        }
        else if (fieldNameAndEnding.getSecond().equals(FIELD_SIZE_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                return subQueryBuilder.getQuery(AlfrescoSolrDataModel.getInstance().getQueryableFields(propertyQName, ContentFieldType.SIZE, FieldUse.ID).getFields().get(0).getField(), queryText, analysisMode, luceneFunction);
                
            }

        }
        else if (fieldNameAndEnding.getSecond().equals(FIELD_LOCALE_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                return subQueryBuilder.getQuery(AlfrescoSolrDataModel.getInstance().getQueryableFields(propertyQName, ContentFieldType.LOCALE, FieldUse.ID).getFields().get(0).getField(), queryText, analysisMode, luceneFunction);
                
            }

        }
        else if (fieldNameAndEnding.getSecond().equals(FIELD_ENCODING_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                return subQueryBuilder.getQuery(AlfrescoSolrDataModel.getInstance().getQueryableFields(propertyQName, ContentFieldType.ENCODING, FieldUse.ID).getFields().get(0).getField(), queryText, analysisMode, luceneFunction);
                
            }

        }
        else if (fieldNameAndEnding.getSecond().equals(FIELD_TRANSFORMATION_STATUS_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                return subQueryBuilder.getQuery(AlfrescoSolrDataModel.getInstance().getQueryableFields(propertyQName, ContentFieldType.TRANSFORMATION_STATUS, FieldUse.ID).getFields().get(0).getField(), queryText, analysisMode, luceneFunction);
                
            }

        }
        else if (fieldNameAndEnding.getSecond().equals(FIELD_TRANSFORMATION_TIME_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                return subQueryBuilder.getQuery(AlfrescoSolrDataModel.getInstance().getQueryableFields(propertyQName, ContentFieldType.TRANSFORMATION_TIME, FieldUse.ID).getFields().get(0).getField(), queryText, analysisMode, luceneFunction);
                
            }

        }
        else if (fieldNameAndEnding.getSecond().equals(FIELD_TRANSFORMATION_EXCEPTION_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                return subQueryBuilder.getQuery(AlfrescoSolrDataModel.getInstance().getQueryableFields(propertyQName, ContentFieldType.TRANSFORMATION_EXCEPTION, FieldUse.ID).getFields().get(0).getField(), queryText, analysisMode, luceneFunction);
                
            }

        }


        // Already in expanded form

        // ML

        if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.MLTEXT)))
        {
            // Build a sub query for each locale and or the results together - the analysis will take care of
            // cross language matching for each entry
            BooleanQuery booleanQuery = new BooleanQuery();
            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, false));
            }
            for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
            {
                addMLTextAttributeQuery(field, propertyDef, queryText, subQueryBuilder, analysisMode, luceneFunction, expandedFieldName, propertyDef, tokenisationMode, booleanQuery,
                        mlAnalysisMode, locale);
            }
            return getNonEmptyBooleanQuery(booleanQuery);
        }
        // Content
        else if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
        {
            // Identifier request are ignored for content

            // Build a sub query for each locale and or the results together -
            // - add an explicit condition for the locale

            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, addContentCrossLocaleWildcards()));
            }

            return addContentAttributeQuery(propertyDef, queryText, subQueryBuilder, analysisMode, luceneFunction, expandedFieldName, expandedLocales);

        }
        else if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.TEXT)))
        {
//            if (propertyQName.equals(ContentModel.PROP_USER_USERNAME) || propertyQName.equals(ContentModel.PROP_USERNAME) || propertyQName.equals(ContentModel.PROP_AUTHORITY_NAME))
//            {
//                // nasty work around for solr support for user and group look up as we can not support lowercased identifiers in the model
//                if(isLucene())
//                {
//                    return subQueryBuilder.getQuery(expandedFieldName, queryText, analysisMode, luceneFunction);
//                }
//            }

            boolean withWildCards = propertyQName.equals(ContentModel.PROP_USER_USERNAME) || propertyQName.equals(ContentModel.PROP_USERNAME) || propertyQName.equals(ContentModel.PROP_AUTHORITY_NAME);

            BooleanQuery booleanQuery = new BooleanQuery();
            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, withWildCards));
            }
            for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
            {

                addTextAttributeQuery(field, propertyDef, queryText, subQueryBuilder, analysisMode, luceneFunction, expandedFieldName, tokenisationMode, booleanQuery, mlAnalysisMode, locale);

            }
            return getNonEmptyBooleanQuery(booleanQuery);
        }
        else
        {
            // Date does not support like
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.DATETIME)))
            {
                if (analysisMode == AnalysisMode.LIKE)
                {
                    throw new UnsupportedOperationException("Wild cards are not supported for the datetime type");
                }
            }
            
            // expand date for loose date parsing 
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.DATETIME) || propertyDef.getDataType().getName().equals(DataTypeDefinition.DATE)))
            {
                Pair<Date, Integer> dateAndResolution = parseDateString(queryText);
                
                BooleanQuery bQuery = new BooleanQuery(); 
                IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getQueryableFields(propertyDef.getName(), null, FieldUse.FTS);
                for(FieldInstance instance : indexedField.getFields())
                {
                    if(dateAndResolution != null)
                    {
                        Query query =  newRangeQuery(instance.getField(), getDateStart(dateAndResolution), getDateEnd(dateAndResolution), true, true);
                        if(query != null)
                        {
                            bQuery.add(query,Occur.SHOULD);
                        }
                    }
                    else
                    {
                        Query query = subQueryBuilder.getQuery(instance.getField(), queryText, AnalysisMode.DEFAULT, luceneFunction);
                        if(query != null)
                        {
                            bQuery.add(query,Occur.SHOULD);
                        }
                    }
                }
                if(bQuery.getClauses().length > 0)
                {
                    return bQuery;
                }
                else
                {
                    return createNoMatchQuery();
                }
            }
            

            if ((propertyDef != null)
                    && (tenantService.isTenantUser()) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.NODE_REF)) && (queryText.contains(StoreRef.URI_FILLER)))
            {
                // ALF-6202
                queryText = tenantService.getName(new NodeRef(queryText)).toString();
            }

            // Sort and id is only special for MLText, text, and content
            // Dates are not special in this case
            if(propertyDef != null)
            {
                BooleanQuery bQuery = new BooleanQuery(); 
                IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getQueryableFields(propertyDef.getName(), null, FieldUse.FTS);
                for(FieldInstance instance : indexedField.getFields())
                {
                    Query query = subQueryBuilder.getQuery(instance.getField(), queryText, AnalysisMode.DEFAULT, luceneFunction);
                    if(query != null)
                    {
                        bQuery.add(query,Occur.SHOULD);
                    }
                }
                if(bQuery.getClauses().length > 0)
                {
                    return bQuery;
                }
                else
                {
                    return createNoMatchQuery();
                }
            }
            else
            {
                Query query = subQueryBuilder.getQuery(expandedFieldName, queryText, AnalysisMode.DEFAULT, luceneFunction);
                if (query != null)
                {
                    return query;
                }
                else
                {
                    return createNoMatchQuery();
                }
            }
        }
    }

    
    /**
     * @param dateAndResolution
     * @return
     */
    private String getDateEnd(Pair<Date, Integer> dateAndResolution)
    {
        Calendar cal= Calendar.getInstance(I18NUtil.getLocale());
        cal.setTime(dateAndResolution.getFirst());
        switch(dateAndResolution.getSecond())
        {
            case Calendar.YEAR:
                cal.set(Calendar.MONTH, cal.getActualMaximum(Calendar.MONTH));
            case Calendar.MONTH:
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            case Calendar.DAY_OF_MONTH:
                cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
            case Calendar.HOUR_OF_DAY:
                cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
            case Calendar.MINUTE:
                cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
            case Calendar.SECOND:
                cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
            case Calendar.MILLISECOND:
            default:
        }
        SimpleDateFormat formatter = CachingDateFormat.getSolrDatetimeFormat();
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(cal.getTime());
    }

    /**
     * @param dateAndResolution
     * @return
     */
    private String getDateStart(Pair<Date, Integer> dateAndResolution)
    {
        Calendar cal= Calendar.getInstance(I18NUtil.getLocale());
        cal.setTime(dateAndResolution.getFirst());
        switch(dateAndResolution.getSecond())
        {
            case Calendar.YEAR:
                cal.set(Calendar.MONTH, cal.getActualMinimum(Calendar.MONTH));
            case Calendar.MONTH:
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            case Calendar.DAY_OF_MONTH:
                cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
            case Calendar.HOUR_OF_DAY:
                cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
            case Calendar.MINUTE:
                cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
            case Calendar.SECOND:
                cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
            case Calendar.MILLISECOND:
            default:
        }
        SimpleDateFormat formatter = CachingDateFormat.getSolrDatetimeFormat();
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(cal.getTime());
    }

    private Pair<Date, Integer> parseDateString(String dateString)
    {
        try
        {
            Pair<Date, Integer> result = CachingDateFormat.lenientParse(dateString, Calendar.YEAR);
            return result;
        }
        catch (java.text.ParseException e)
        {
            SimpleDateFormat oldDf = CachingDateFormat.getDateFormat();
            try
            {
                Date date = oldDf.parse(dateString);
                return new Pair<Date, Integer>(date, Calendar.SECOND);
            }
            catch (java.text.ParseException ee)
            {
                if (dateString.equalsIgnoreCase("min"))
                {
                    Calendar cal = Calendar.getInstance(I18NUtil.getLocale());
                    cal.set(Calendar.YEAR, cal.getMinimum(Calendar.YEAR));
                    cal.set(Calendar.DAY_OF_YEAR, cal.getMinimum(Calendar.DAY_OF_YEAR));
                    cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                    cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                    cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                    cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
                    return new Pair<Date, Integer>(cal.getTime(), Calendar.MILLISECOND);
                }
                else if (dateString.equalsIgnoreCase("now"))
                {
                    return new Pair<Date, Integer>(new Date(), Calendar.MILLISECOND);
                }
                else if (dateString.equalsIgnoreCase("today"))
                {
                    Calendar cal = Calendar.getInstance(I18NUtil.getLocale());
                    cal.setTime(new Date());
                    cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                    cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                    cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                    cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
                    return new Pair<Date, Integer>(cal.getTime(), Calendar.DAY_OF_MONTH);
                }
                else if (dateString.equalsIgnoreCase("max"))
                {
                    Calendar cal = Calendar.getInstance(I18NUtil.getLocale());
                    cal.set(Calendar.YEAR, cal.getMaximum(Calendar.YEAR));
                    cal.set(Calendar.DAY_OF_YEAR, cal.getMaximum(Calendar.DAY_OF_YEAR));
                    cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
                    cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
                    cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
                    cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
                    return new Pair<Date, Integer>(cal.getTime(), Calendar.MILLISECOND);
                }
                else
                {
                    return null; // delegate to SOLR date parsing
                }
            }
        }
    }
 

    protected Query functionQueryBuilder(String expandedFieldName, String ending, QName propertyQName, PropertyDefinition propertyDef, IndexTokenisationMode tokenisationMode, String queryText,
            LuceneFunction luceneFunction) throws ParseException
            {

        // Mime type
        if (ending.equals(FIELD_MIMETYPE_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (ending.equals(FIELD_SIZE_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (ending.equals(FIELD_LOCALE_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (ending.equals(FIELD_ENCODING_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (ending.equals(FIELD_CONTENT_DOC_ID_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (ending.equals(FIELD_TRANSFORMATION_EXCEPTION_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (ending.equals(FIELD_TRANSFORMATION_TIME_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (ending.equals(FIELD_TRANSFORMATION_STATUS_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }

        // Already in expanded form

        // ML

        if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.MLTEXT)))
        {
            // Build a sub query for each locale and or the results together - the analysis will take care of
            // cross language matching for each entry
            BooleanQuery booleanQuery = new BooleanQuery();
            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, false));
            }
            for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
            {

                addLocaleSpecificUntokenisedMLOrTextFunction(expandedFieldName, propertyDef, queryText, luceneFunction, booleanQuery, mlAnalysisMode, locale, tokenisationMode);

            }
            return booleanQuery;
        }
        // Content
        else if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
        {
            throw new UnsupportedOperationException("Lucene functions not supported for content");
        }
        else if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.TEXT)))
        {
            if (propertyQName.equals(ContentModel.PROP_USER_USERNAME) || propertyQName.equals(ContentModel.PROP_USERNAME) || propertyQName.equals(ContentModel.PROP_AUTHORITY_NAME))
            {
                throw new UnsupportedOperationException("Functions are not supported agaisnt special text fields");
            }

            boolean withWildCards = propertyQName.equals(ContentModel.PROP_USER_USERNAME) || propertyQName.equals(ContentModel.PROP_USERNAME) || propertyQName.equals(ContentModel.PROP_AUTHORITY_NAME);

            BooleanQuery booleanQuery = new BooleanQuery();
            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, withWildCards));
            }
            for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
            {
                addLocaleSpecificUntokenisedMLOrTextFunction(expandedFieldName, propertyDef, queryText, luceneFunction, booleanQuery, mlAnalysisMode, locale, tokenisationMode);

            }
            return booleanQuery;
        }
        else
        {
            throw new UnsupportedOperationException("Lucene Function");
        }
            }

   

    protected TermQuery createNoMatchQuery()
    {
        return new TermQuery(new Term("NO_TOKENS", "__"));
    }

  

    /**
     * Returns null if all clause words were filtered away by the analyzer
     * @param booleanQuery - initial BooleanQuery
     * @return BooleanQuery or <code>null</code> if booleanQuery has no clauses 
     */
    protected BooleanQuery getNonEmptyBooleanQuery(BooleanQuery booleanQuery)
    {
        if (booleanQuery.clauses().size() > 0)
        {
            return booleanQuery;
        }
        else
        {
            return null;
        }
    }
    
    protected Query createSolr4IdQuery(String queryText)
    {
        return createTermQuery(FIELD_SOLR4_ID, queryText);
    }
    
    // Previous SOLR
    
   
    protected Query createIdQuery(String queryText)
    {
        if (NodeRef.isNodeRef(queryText))
        {
            return createNodeRefQuery(FIELD_LID, queryText);
        }
        else
        {
            return createNodeRefQuery(FIELD_ID, queryText);
        }
    }


    protected Query createPathQuery(String queryText, boolean withRepeats) throws SAXPathException
    {
        XPathReader reader = new XPathReader();
        SolrXPathHandler handler = new SolrXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse(queryText);
        SolrPathQuery pathQuery = handler.getQuery();
        pathQuery.setRepeats(withRepeats);
        return new SolrCachingPathQuery(pathQuery);
    }

    protected Query createQNameQuery(String queryText) throws SAXPathException
    {
        XPathReader reader = new XPathReader();
        SolrXPathHandler handler = new SolrXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse("//" + queryText);
        SolrPathQuery pathQuery = handler.getQuery();
        return new SolrCachingPathQuery(pathQuery);
    }

    protected Query createPrimaryAssocQNameQuery(String queryText) throws SAXPathException
    {
        XPathReader reader = new XPathReader();
        SolrXPathHandler handler = new SolrXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse("//" + queryText);
        SolrPathQuery pathQuery = handler.getQuery();
        pathQuery.setPathField(FIELD_PRIMARYASSOCQNAME);
        return new SolrCachingPathQuery(pathQuery);
    }

    protected Query createPrimaryAssocTypeQNameQuery(String queryText) throws SAXPathException
    {
        XPathReader reader = new XPathReader();
        SolrXPathHandler handler = new SolrXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse("//" + queryText);
        SolrPathQuery pathQuery = handler.getQuery();
        pathQuery.setPathField(FIELD_PRIMARYASSOCTYPEQNAME);
        return new SolrCachingPathQuery(pathQuery);
    }

    protected Query createAssocTypeQNameQuery(String queryText) throws SAXPathException
    {   
        XPathReader reader = new XPathReader();
        SolrXPathHandler handler = new SolrXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse("//" + queryText);
        SolrPathQuery pathQuery = handler.getQuery();
        pathQuery.setPathField(FIELD_ASSOCTYPEQNAME);
        return new SolrCachingPathQuery(pathQuery);
    }

    /**
     * @param queryText
     * @return
     */
    protected Query createAclIdQuery(String queryText) throws ParseException
    {
        return getFieldQueryImplWithIOExceptionWrapped(FIELD_ACLID, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    /**
     * @param queryText
     * @return
     */
    protected Query createOwnerQuery(String queryText) throws ParseException
    {
        return new SolrOwnerQuery(queryText);
    }

    /**
     * @param queryText
     * @return
     */
    protected Query createReaderQuery(String queryText) throws ParseException
    {
        return new SolrReaderQuery(queryText);
    }

    /**
     * @param queryText
     * @return
     */
    protected Query createAuthorityQuery(String queryText) throws ParseException
    {
        return new SolrAuthorityQuery(queryText);
    }


    // TODO: correct field names
    protected Query addContentAttributeQuery(PropertyDefinition pDef, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction, String expandedFieldName,
            List<Locale> expandedLocales) throws ParseException
    {
        BooleanQuery booleanQuery = new BooleanQuery();
        for (Locale locale : expandedLocales)
        {
            if (locale.toString().length() == 0)
            {
                StringBuilder builder = new StringBuilder(queryText.length() + 10);
                builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
                IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getQueryableFields(pDef.getName(), null, FieldUse.FTS);
                for(FieldInstance field : indexedField.getFields())
                {
                    if(!field.isLocalised())
                    {
                        Query subQuery = subQueryBuilder.getQuery(field.getField(), builder.toString(), analysisMode, luceneFunction);
                        if (subQuery != null)
                        {
                            booleanQuery.add(subQuery, Occur.SHOULD);
                        }
                    }
                }
            }
            else
            {
                StringBuilder builder = new StringBuilder(queryText.length() + 10);
                builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
                IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getQueryableFields(pDef.getName(), null, FieldUse.FTS);
                for(FieldInstance field : indexedField.getFields())
                {
                    if(field.isLocalised())
                    {
                        Query subQuery = subQueryBuilder.getQuery(field.getField(), builder.toString(), analysisMode, luceneFunction);
                        if (subQuery != null)
                        {
                            booleanQuery.add(subQuery, Occur.SHOULD);
                        }
                    }
                }
            }
        }
        return getNonEmptyBooleanQuery(booleanQuery);
    }

   
    protected void addLocaleSpecificUntokenisedMLOrTextFunction(String expandedFieldName, PropertyDefinition pDef, String queryText, LuceneFunction luceneFunction, BooleanQuery booleanQuery,
            MLAnalysisMode mlAnalysisMode, Locale locale, IndexTokenisationMode tokenisationMode)
    {
//        Query subQuery = new CaseInsensitiveFieldQuery(new Term(getFieldName(expandedFieldName, locale, tokenisationMode, IndexTokenisationMode.FALSE), getFixedFunctionQueryText(
//                queryText, locale, tokenisationMode, IndexTokenisationMode.FALSE)));
//        booleanQuery.add(subQuery, Occur.SHOULD);
//
//        if (booleanQuery.getClauses().length == 0)
//        {
//            booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
//        }
        throw new UnsupportedOperationException();
    }

    private String getFixedFunctionQueryText(String queryText, Locale locale, IndexTokenisationMode actualIndexTokenisationMode,
            IndexTokenisationMode preferredIndexTokenisationMode)
    {
        StringBuilder builder = new StringBuilder(queryText.length() + 10);
        if (locale.toString().length() > 0)
        {
            builder.append("{").append(locale.toString()).append("}");
        }
        builder.append(queryText);

        return builder.toString();
    }

    private FieldInstance getFieldInstance(String baseFieldName, PropertyDefinition pDef, Locale locale, IndexTokenisationMode preferredIndexTokenisationMode)
    {
        if(pDef != null)
        {

            switch (preferredIndexTokenisationMode)
            {
            case BOTH:
                throw new IllegalStateException("Preferred mode can not be BOTH");
            case FALSE:
                if(locale.toString().length() == 0)
                {
                    IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getQueryableFields(pDef.getName(), null, FieldUse.ID);
                    for(FieldInstance field : indexedField.getFields())
                    {
                        if(!field.isLocalised())
                        {
                            return field;
                        }
                    }
                }
                else
                {
                    IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getQueryableFields(pDef.getName(), null, FieldUse.ID);
                    for(FieldInstance field : indexedField.getFields())
                    {
                        if(field.isLocalised())
                        {
                            return field;
                        }
                    }
                }
            case TRUE:
                if(locale.toString().length() == 0)
                {
                    IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getQueryableFields(pDef.getName(), null, FieldUse.FTS);
                    for(FieldInstance field : indexedField.getFields())
                    {
                        if(!field.isLocalised())
                        {
                            return field;
                        }
                    }
                }
                else
                {
                    IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getQueryableFields(pDef.getName(), null, FieldUse.FTS);
                    for(FieldInstance field : indexedField.getFields())
                    {
                        if(field.isLocalised())
                        {
                            return field;
                        }
                    }
                }
            }

            
        }

        return new FieldInstance(baseFieldName, false, false);
        
    }

    
    protected void addLocaleSpecificUntokenisedTextRangeFunction(String expandedFieldName, PropertyDefinition pDef, String lower, String upper, boolean includeLower, boolean includeUpper,
            LuceneFunction luceneFunction, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale, IndexTokenisationMode tokenisationMode) throws ParseException
    {
//        String field = getFieldName(expandedFieldName, locale, tokenisationMode, IndexTokenisationMode.FALSE);
//
//        StringBuilder builder = new StringBuilder();
//        builder.append("\u0000").append(locale.toString()).append("\u0000").append(lower);
//        String first = getToken(field, builder.toString(), AnalysisMode.IDENTIFIER);
//
//        builder = new StringBuilder();
//        builder.append("\u0000").append(locale.toString()).append("\u0000").append(upper);
//        String last = getToken(field, builder.toString(), AnalysisMode.IDENTIFIER);
//
//        Query query = new CaseInsensitiveFieldRangeQuery(field, first, last, includeLower, includeUpper);
//        booleanQuery.add(query, Occur.SHOULD);
        
        throw new UnsupportedOperationException();

    }

   
    protected void addMLTextAttributeQuery(String field, PropertyDefinition pDef, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            String expandedFieldName, PropertyDefinition propertyDef, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode,
            Locale locale) throws ParseException
    {

        addMLTextOrTextAttributeQuery(field, pDef, queryText, subQueryBuilder, analysisMode, luceneFunction, expandedFieldName, tokenisationMode, booleanQuery, mlAnalysisMode, locale);
    }

    private void addMLTextOrTextAttributeQuery(String field, PropertyDefinition pDef, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            String expandedFieldName, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale) throws ParseException
    {

        boolean lowercaseExpandedTerms = getLowercaseExpandedTerms();
        try
        {
            switch (tokenisationMode)
            {
            case BOTH:
                switch (analysisMode)
                {
                default:
                case DEFAULT:
                case TOKENISE:
                    addLocaleSpecificMLOrTextAttribute(pDef, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, expandedFieldName, tokenisationMode,
                            IndexTokenisationMode.TRUE);
                    break;
                case IDENTIFIER:
                case FUZZY:
                case PREFIX:
                case WILD:
                case LIKE:
                    setLowercaseExpandedTerms(false);
                    addLocaleSpecificMLOrTextAttribute(pDef, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, expandedFieldName, tokenisationMode,
                            IndexTokenisationMode.FALSE);

                    break;
                }
                break;
            case FALSE:
                setLowercaseExpandedTerms(false);
                addLocaleSpecificMLOrTextAttribute(pDef, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, expandedFieldName, tokenisationMode,
                        IndexTokenisationMode.FALSE);
                break;
            case TRUE:
            default:
                addLocaleSpecificMLOrTextAttribute(pDef, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, expandedFieldName, tokenisationMode,
                        IndexTokenisationMode.TRUE);
                break;
            }
        }
        finally
        {
            setLowercaseExpandedTerms(lowercaseExpandedTerms);
        }

    }

  
    protected void addTextAttributeQuery(String field, PropertyDefinition pDef, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            String expandedFieldName, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale) throws ParseException
    {

        addMLTextOrTextAttributeQuery(field, pDef, queryText, subQueryBuilder, analysisMode, luceneFunction, expandedFieldName, tokenisationMode, booleanQuery, mlAnalysisMode, locale);
    }

  
    private void addLocaleSpecificMLOrTextAttribute(PropertyDefinition pDef, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            BooleanQuery booleanQuery, Locale locale, String textFieldName, IndexTokenisationMode tokenisationMode, IndexTokenisationMode preferredTokenisationMode)
            throws ParseException
    {

        FieldInstance fieldInstance = getFieldInstance(textFieldName, pDef, locale, preferredTokenisationMode);
        StringBuilder builder = new StringBuilder(queryText.length() + 10);
        if(fieldInstance.isLocalised())
        {
            builder.append("\u0000").append(locale.toString()).append("\u0000");
        }
        builder.append(queryText);
        Query subQuery = subQueryBuilder.getQuery(fieldInstance.getField(), builder.toString(), analysisMode,
                luceneFunction);
        if (subQuery != null)
        {
            booleanQuery.add(subQuery, Occur.SHOULD);
        }
    }

 
    protected void addTextRange(String field, PropertyDefinition pDef, String part1, String part2, boolean includeLower, boolean includeUpper, AnalysisMode analysisMode, String fieldName,
            PropertyDefinition propertyDef, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale) throws ParseException
    {
        switch (tokenisationMode)
        {
        case BOTH:
            switch (analysisMode)
            {
            case DEFAULT:
            case TOKENISE:
                addLocaleSpecificTextRange(fieldName, pDef, part1, part2, includeLower, includeUpper, booleanQuery, locale, analysisMode, tokenisationMode, IndexTokenisationMode.TRUE);
                break;
            case IDENTIFIER:
                addLocaleSpecificTextRange(fieldName, pDef, part1, part2, includeLower, includeUpper, booleanQuery, locale, analysisMode, tokenisationMode, IndexTokenisationMode.FALSE);
                break;
            case WILD:
            case LIKE:
            case PREFIX:
            case FUZZY:
            default:
                throw new UnsupportedOperationException();
            }
            break;
        case FALSE:
            addLocaleSpecificTextRange(fieldName, pDef, part1, part2, includeLower, includeUpper, booleanQuery, locale, analysisMode, tokenisationMode, IndexTokenisationMode.FALSE);
            break;
        case TRUE:
            addLocaleSpecificTextRange(fieldName, pDef, part1, part2, includeLower, includeUpper, booleanQuery, locale, analysisMode, tokenisationMode, IndexTokenisationMode.TRUE);
            break;
        default:
        }

    }

    private void addLocaleSpecificTextRange(String expandedFieldName, PropertyDefinition pDef, String part1, String part2, boolean includeLower, boolean includeUpper, BooleanQuery booleanQuery,
            Locale locale, AnalysisMode analysisMode, IndexTokenisationMode tokenisationMode, IndexTokenisationMode preferredTokenisationMode) throws ParseException
    {
        FieldInstance fieldInstance = getFieldInstance(expandedFieldName, pDef, locale, preferredTokenisationMode);
        
        StringBuilder builder = new StringBuilder(part1.length() + 10);
        if(fieldInstance.isLocalised())
        {
            builder.append("\u0000").append(locale.toString()).append("\u0000");
        }
        builder.append(part1);
        String firstString = builder.toString();
       

        builder = new StringBuilder(part2.length() + 10);
        if(fieldInstance.isLocalised())
        {
            builder.append("\u0000").append(locale.toString()).append("\u0000");
        }
        builder.append(part2);
        String lastString = builder.toString();

        TermRangeQuery query = new TermRangeQuery(fieldInstance.getField(), new BytesRef(firstString), new BytesRef(lastString), includeLower, includeUpper);
        booleanQuery.add(query, Occur.SHOULD);
    }

  
    protected void addTextSpanQuery(String field, PropertyDefinition pDef, String first, String last, int slop, boolean inOrder, String expandedFieldName, IndexTokenisationMode tokenisationMode,
            BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale)
    {
        addMLTextOrTextSpanQuery(field, pDef, first, last, slop, inOrder, expandedFieldName, tokenisationMode, booleanQuery, mlAnalysisMode, locale);
    }

   
    protected org.apache.lucene.search.Query addContentSpanQuery(String field, PropertyDefinition pDef, String first, String last, int slop, boolean inOrder, String expandedFieldName,
            List<Locale> expandedLocales, MLAnalysisMode mlAnalysisMode)
    {
//        try
//        {
//            BooleanQuery booleanQuery = new BooleanQuery();
//            for (Locale locale : expandedLocales)
//            {
//                if (locale.toString().length() == 0)
//                {
//                    StringBuilder builder = new StringBuilder(first.length() + 10);
//                    builder.append("\u0000").append(locale.toString()).append("\u0000").append(first);
//                    TokenStream source = getAnalyzer().tokenStream(expandedFieldName + ".__", new StringReader(builder.toString()));
//
//                    org.apache.lucene.analysis.Token reusableToken = new org.apache.lucene.analysis.Token();
//                    org.apache.lucene.analysis.Token nextToken;
//
//                    nextToken = source.next(reusableToken);
//                    SpanQuery firstTerm = new SpanTermQuery(new Term(expandedFieldName + ".__", nextToken.term()));
//                    if (source.next(reusableToken) != null)
//                    {
//                        throw new AlfrescoRuntimeException("Found extra token in span query: " + first);
//                    }
//
//                    builder = new StringBuilder(last.length() + 10);
//                    builder.append("\u0000").append(locale.toString()).append("\u0000").append(last);
//                    source = getAnalyzer().tokenStream(expandedFieldName + ".__", new StringReader(builder.toString()));
//
//                    nextToken = source.next(reusableToken);
//                    SpanQuery lastTerm = new SpanTermQuery(new Term(expandedFieldName + ".__", nextToken.term()));
//                    if (source.next(reusableToken) != null)
//                    {
//                        throw new AlfrescoRuntimeException("Found extra token in span query: " + last);
//                    }
//
//                    SpanNearQuery result = new SpanNearQuery(new SpanQuery[] { firstTerm, lastTerm }, slop, inOrder);
//                    booleanQuery.add(result, Occur.SHOULD);
//
//                }
//                else
//                {
//                    StringBuilder builder = new StringBuilder(first.length() + 10);
//                    builder.append("\u0000").append(locale.toString()).append("\u0000").append(first);
//                    TokenStream source = getAnalyzer().tokenStream(expandedFieldName, new StringReader(builder.toString()), AnalysisMode.TOKENISE);
//
//                    org.apache.lucene.analysis.Token reusableToken = new org.apache.lucene.analysis.Token();
//                    org.apache.lucene.analysis.Token nextToken;
//
//                    nextToken = source.next(reusableToken);
//                    SpanQuery firstTerm = new SpanTermQuery(new Term(expandedFieldName, nextToken.term()));
//                    if (source.next(reusableToken) != null)
//                    {
//                        throw new AlfrescoRuntimeException("Found extra token in span query: " + first);
//                    }
//
//                    builder = new StringBuilder(last.length() + 10);
//                    builder.append("\u0000").append(locale.toString()).append("\u0000").append(last);
//                    source = getAnalyzer().tokenStream(expandedFieldName, new StringReader(builder.toString()), AnalysisMode.TOKENISE);
//
//                    nextToken = source.next(reusableToken);
//                    SpanQuery lastTerm = new SpanTermQuery(new Term(expandedFieldName, nextToken.term()));
//                    if (source.next(reusableToken) != null)
//                    {
//                        throw new AlfrescoRuntimeException("Found extra token in span query: " + last);
//                    }
//
//                    SpanNearQuery result = new SpanNearQuery(new SpanQuery[] { firstTerm, lastTerm }, slop, inOrder);
//                    booleanQuery.add(result, Occur.SHOULD);
//                }
//            }
//            return getNonEmptyBooleanQuery(booleanQuery);
//        }
//        catch (IOException ioe)
//        {
//            return createNoMatchQuery();
//        }
        throw new UnsupportedOperationException();
    }

 
    protected void addMLTextSpanQuery(String field, PropertyDefinition pDef, String first, String last, int slop, boolean inOrder, String expandedFieldName, PropertyDefinition propertyDef,
            IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale)
    {
        addMLTextOrTextSpanQuery(field, pDef, first, last, slop, inOrder, expandedFieldName, tokenisationMode, booleanQuery, mlAnalysisMode, locale);
    }

    private void addMLTextOrTextSpanQuery(String field, PropertyDefinition pDef, String first, String last, int slop, boolean inOrder, String expandedFieldName, IndexTokenisationMode tokenisationMode,
            BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale)
    {
//        try
//        {
//            String fieldName = getFieldName(expandedFieldName, locale, tokenisationMode, IndexTokenisationMode.TRUE);
//
//            StringBuilder builder = new StringBuilder(first.length() + 10);
//            builder.append("\u0000").append(locale.toString()).append("\u0000").append(first);
//            TokenStream source = getAnalyzer().tokenStream(fieldName, new StringReader(builder.toString()), AnalysisMode.TOKENISE);
//
//            org.apache.lucene.analysis.Token reusableToken = new org.apache.lucene.analysis.Token();
//            org.apache.lucene.analysis.Token nextToken;
//
//            nextToken = source.next(reusableToken);
//            SpanQuery firstTerm = new SpanTermQuery(new Term(fieldName, nextToken.term()));
//            if (source.next(reusableToken) != null)
//            {
//                throw new AlfrescoRuntimeException("Found extra token in span query: " + first);
//            }
//
//            builder = new StringBuilder(last.length() + 10);
//            builder.append("\u0000").append(locale.toString()).append("\u0000").append(last);
//            source = getAnalyzer().tokenStream(fieldName, new StringReader(builder.toString()), AnalysisMode.TOKENISE);
//
//            nextToken = source.next(reusableToken);
//            SpanQuery lastTerm = new SpanTermQuery(new Term(fieldName, nextToken.term()));
//            if (source.next(reusableToken) != null)
//            {
//                throw new AlfrescoRuntimeException("Found extra token in span query: " + last);
//            }
//
//            SpanNearQuery result = new SpanNearQuery(new SpanQuery[] { firstTerm, lastTerm }, slop, inOrder);
//            booleanQuery.add(result, Occur.SHOULD);
//        }
//        catch (IOException ioe)
//        {
//            booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
//        }
        throw new UnsupportedOperationException();

    }

   
    public boolean addContentCrossLocaleWildcards()
    {
        return false;
    }

  
    protected Query createOwnerSetQuery(String queryText) throws ParseException
    {
        return new SolrOwnerSetQuery(queryText);
    }

    
    protected Query createReaderSetQuery(String queryText) throws ParseException
    {
        return new SolrReaderSetQuery(queryText);
    }

   
    protected Query createAuthoritySetQuery(String queryText) throws ParseException
    {
        return new SolrAuthoritySetQuery(queryText);
    }
    
    protected Query createDeniedQuery(String queryText) throws ParseException
    {
        return new SolrDeniedQuery(queryText);
    }

    protected Query createDenySetQuery(String queryText) throws ParseException
    {
        return new SolrDenySetQuery(queryText);
    }
}
