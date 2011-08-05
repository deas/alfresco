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
package org.alfresco.repo.search.impl.lucene;

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
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.analysis.DateTimeAnalyser;
import org.alfresco.repo.search.impl.lucene.analysis.MLTokenDuplicator;
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
import org.alfresco.util.CachingDateFormat;
import org.alfresco.util.Pair;
import org.alfresco.util.SearchLanguageConversion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.CharStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParserTokenManager;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreRangeQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardTermEnum;
import org.apache.lucene.search.regex.RegexQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.saxpath.SAXPathException;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Extensions to the standard lucene query parser.
 * <p>
 * Covers:
 * <ul>
 * <li>special fields;
 * <li>range expansion;
 * <li>adds wild card support for phrases;
 * <li>exposes more helper methods to build lucene queries and request tokneisation bahviour.
 * </ul>
 * TODO: Locale loop should not include tokenisation expansion
 * 
 * @author andyh
 */
public abstract class AbstractLuceneQueryParser extends QueryParser
{
    public static final String FIELD_NO_LOCALE_SUFFIX = ".no_locale";
    public static final String FIELD_SORT_SUFFIX = ".sort";
    public static final String FIELD_LOCALE_SUFFIX = ".locale";
    public static final String FIELD_SIZE_SUFFIX = ".size";
    public static final String FIELD_MIMETYPE_SUFFIX = ".mimetype";
    public static final String FIELD_FTSSTATUS = "FTSSTATUS";
    public static final String FIELD_FTSREF = "FTSREF";
    public static final String FIELD_ISNOTNULL = "ISNOTNULL";
    public static final String FIELD_ISNULL = "ISNULL";
    public static final String FIELD_ISUNSET = "ISUNSET";
    public static final String FIELD_ALL = "ALL";
    public static final String PROPERTY_FIELD_PREFIX = "@";
    public static final String FIELD_EXACTASPECT = "EXACTASPECT";
    public static final String FIELD_EXACTTYPE = "EXACTTYPE";
    public static final String FIELD_TYPE = "TYPE";
    public static final String FIELD_ASPECT = "ASPECT";
    public static final String FIELD_CLASS = "CLASS";
    public static final String FIELD_ASSOCTYPEQNAME = "ASSOCTYPEQNAME";
    public static final String FIELD_PRIMARYASSOCTYPEQNAME = "PRIMARYASSOCTYPEQNAME";
    public static final String FIELD_QNAME = "QNAME";
    public static final String FIELD_PRIMARYPARENT = "PRIMARYPARENT";
    public static final String FIELD_PARENT = "PARENT";
    public static final String FIELD_TX = "TX";
    public static final String FIELD_ISNODE = "ISNODE";
    public static final String FIELD_ISCONTAINER = "ISCONTAINER";
    public static final String FIELD_ISROOT = "ISROOT";
    public static final String FIELD_DBID = "DBID";
    public static final String FIELD_ID = "ID";
    public static final String FIELD_TEXT = "TEXT";
    public static final String FIELD_PATHWITHREPEATS = "PATHWITHREPEATS";
    public static final String FIELD_PATH = "PATH";
    public static final String FIELD_TAG = "TAG";
    public static final String FIELD_ACLID = "ACLID";
    public static final String FIELD_OWNER = "OWNER";
    public static final String FIELD_READER = "READER";
    public static final String FIELD_AUTHORITY = "AUTHORITY";
    public static final String FIELD_TXID = "TXID";
    public static final String FIELD_ACLTXID = "ACLTXID";
    public static final String FIELD_TXCOMMITTIME = "TXCOMMITTIME";
    public static final String FIELD_ACLTXCOMMITTIME = "ACLTXCOMMITTIME";
    public static final String FIELD_LINKASPECT = "LINKASPECT";
    public static final String FIELD_ANCESTOR = "ANCESTOR";
    public static final String FIELD_ISCATEGORY = "ISCATEGORY";
    public static final String FIELD_ENCODING_SUFFIX = ".encoding";
    public static final String FIELD_CONTENT_DOC_ID_SUFFIX = "contentDocId";
    public static final String FIELD_TRANSFORMATION_EXCEPTION_SUFFIX = ".transformationException";
    public static final String FIELD_TRANSFORMATION_TIME_SUFFIX = ".transformationTime";
    public static final String FIELD_TRANSFORMATION_STATUS_SUFFIX = ".transformationStatus";
    public static final String FIELD_PARENT_ASSOC_CRC = "PARENTASSOCCRC";
    public static final String FIELD_PRIMARYASSOCQNAME = "PRIMARYASSOCQNAME";
    public static final String FIELD_ASSOCQNAME = "ASSOCQNAME";

    private static Log s_logger = LogFactory.getLog(AbstractLuceneQueryParser.class);

    protected NamespacePrefixResolver namespacePrefixResolver;

    protected DictionaryService dictionaryService;

    private TenantService tenantService;

    private SearchParameters searchParameters;

    private MLAnalysisMode defaultSearchMLAnalysisMode;

    private IndexReader indexReader;

    private int internalSlop = 0;

    private AbstractAnalyzer luceneAnalyser;

    /**
     * @param defaultSearchMLAnalysisMode
     */
    public void setDefaultSearchMLAnalysisMode(MLAnalysisMode defaultSearchMLAnalysisMode)
    {
        this.defaultSearchMLAnalysisMode = defaultSearchMLAnalysisMode;
    }

    /**
     * @param indexReader
     */
    public void setIndexReader(IndexReader indexReader)
    {
        this.indexReader = indexReader;
    }

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

    public IndexReader getIndexReader()
    {
        return indexReader;
    }

    public MLAnalysisMode getDefaultSearchMLAnalysisMode()
    {
        return defaultSearchMLAnalysisMode;
    }

    /**
     * Lucene default constructor
     * 
     * @param arg0
     * @param arg1
     */
    public AbstractLuceneQueryParser(String arg0, Analyzer arg1)
    {
        super(arg0, arg1);
        if (arg1 instanceof AbstractAnalyzer)
        {
            luceneAnalyser = (AbstractAnalyzer) arg1;
        }
    }

    /**
     * Lucene default constructor
     * 
     * @param arg0
     */
    public AbstractLuceneQueryParser(CharStream arg0)
    {
        super(arg0);
    }

    /**
     * Lucene default constructor
     * 
     * @param arg0
     */
    public AbstractLuceneQueryParser(QueryParserTokenManager arg0)
    {
        super(arg0);
    }

    protected Query getFieldQuery(String field, String queryText, int slop) throws ParseException
    {
        try
        {
            internalSlop = slop;
            Query query = getFieldQuery(field, queryText);
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
        String luceneWildCardExpression = SearchLanguageConversion.convert(SearchLanguageConversion.DEF_SQL_LIKE, SearchLanguageConversion.DEF_LUCENE, sqlLikeClause);
        return getWildcardQuery(field, luceneWildCardExpression, AnalysisMode.LIKE);
    }

    /**
     * @param field
     * @param queryText
     * @param analysisMode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
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
     */
    public Query getSpanQuery(String field, String first, String last, int slop, boolean inOrder)
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
                Collection<QName> contentAttributes = dictionaryService.getAllProperties(DataTypeDefinition.CONTENT);
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
        else if (field.startsWith(PROPERTY_FIELD_PREFIX))
        {
            // need to build each term for the span
            SpanQuery firstTerm = new SpanTermQuery(new Term(field, first));
            SpanQuery lastTerm = new SpanTermQuery(new Term(field, last));
            return new SpanNearQuery(new SpanQuery[] { firstTerm, lastTerm }, slop, inOrder);
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
        else if (matchDataTypeDefinition(field) != null)
        {
            Collection<QName> contentAttributes = dictionaryService.getAllProperties(matchDataTypeDefinition(field).getName());
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
        else
        {
            // Default behaviour for the following fields

            // FIELD_ID
            // FIELD_DBID
            // FIELD_ISROOT
            // FIELD_ISCONTAINER
            // FIELD_ISNODE
            // FIELD_TX
            // FIELD_PARENT
            // FIELD_PRIMARYPARENT
            // FIELD_QNAME
            // FIELD_PRIMARYASSOCTYPEQNAME
            // FIELD_ASSOCTYPEQNAME
            // 
            // 

            SpanQuery firstTerm = new SpanTermQuery(new Term(field, first));
            SpanQuery lastTerm = new SpanTermQuery(new Term(field, last));
            return new SpanNearQuery(new SpanQuery[] { firstTerm, lastTerm }, slop, inOrder);
        }

    }

    private DataTypeDefinition matchDataTypeDefinition(String string)
    {
        QName search = QName.createQName(expandQName(string));
        DataTypeDefinition dataTypeDefinition = dictionaryService.getDataType(QName.createQName(expandQName(string)));
        QName match = null;
        if (dataTypeDefinition == null)
        {
            for (QName definition : dictionaryService.getAllDataTypes())
            {
                if (definition.getNamespaceURI().equalsIgnoreCase(search.getNamespaceURI()))
                {
                    if (definition.getLocalName().equalsIgnoreCase(search.getLocalName()))
                    {
                        if (match == null)
                        {
                            match = definition;
                        }
                        else
                        {
                            throw new LuceneQueryParserException("Ambiguous data datype " + string);
                        }
                    }
                }

            }
        }
        else
        {
            return dataTypeDefinition;
        }
        if (match == null)
        {
            return null;
        }
        else
        {
            return dictionaryService.getDataType(match);
        }
    }

    private PropertyDefinition matchPropertyDefinition(String string)
    {
        QName search = QName.createQName(expandQName(string));
        PropertyDefinition propertyDefinition = dictionaryService.getProperty(QName.createQName(expandQName(string)));
        QName match = null;
        if (propertyDefinition == null)
        {
            for (QName definition : dictionaryService.getAllProperties(null))
            {
                if (definition.getNamespaceURI().equalsIgnoreCase(search.getNamespaceURI()))
                {
                    if (definition.getLocalName().equalsIgnoreCase(search.getLocalName()))
                    {
                        if (match == null)
                        {
                            match = definition;
                        }
                        else
                        {
                            throw new LuceneQueryParserException("Ambiguous data datype " + string);
                        }
                    }
                }

            }
        }
        else
        {
            return propertyDefinition;
        }
        if (match == null)
        {
            return null;
        }
        else
        {
            return dictionaryService.getProperty(match);
        }
    }

    private AspectDefinition matchAspectDefinition(String string)
    {
        QName search = QName.createQName(expandQName(string));
        AspectDefinition aspectDefinition = dictionaryService.getAspect(QName.createQName(expandQName(string)));
        QName match = null;
        if (aspectDefinition == null)
        {
            for (QName definition : dictionaryService.getAllAspects())
            {
                if (definition.getNamespaceURI().equalsIgnoreCase(search.getNamespaceURI()))
                {
                    if (definition.getLocalName().equalsIgnoreCase(search.getLocalName()))
                    {
                        if (match == null)
                        {
                            match = definition;
                        }
                        else
                        {
                            throw new LuceneQueryParserException("Ambiguous data datype " + string);
                        }
                    }
                }
            }
        }
        else
        {
            return aspectDefinition;
        }
        if (match == null)
        {
            return null;
        }
        else
        {
            return dictionaryService.getAspect(match);
        }
    }

    private TypeDefinition matchTypeDefinition(String string)
    {
        QName search = QName.createQName(expandQName(string));
        TypeDefinition typeDefinition = dictionaryService.getType(QName.createQName(expandQName(string)));
        QName match = null;
        if (typeDefinition == null)
        {
            for (QName definition : dictionaryService.getAllTypes())
            {
                if (definition.getNamespaceURI().equalsIgnoreCase(search.getNamespaceURI()))
                {
                    if (definition.getLocalName().equalsIgnoreCase(search.getLocalName()))
                    {
                        if (match == null)
                        {
                            match = definition;
                        }
                        else
                        {
                            throw new LuceneQueryParserException("Ambiguous data datype " + string);
                        }
                    }
                }
            }
        }
        else
        {
            return typeDefinition;
        }
        if (match == null)
        {
            return null;
        }
        else
        {
            return dictionaryService.getType(match);
        }
    }

    private ClassDefinition matchClassDefinition(String string)
    {
        QName search = QName.createQName(expandQName(string));
        ClassDefinition classDefinition = dictionaryService.getClass(QName.createQName(expandQName(string)));
        QName match = null;
        if (classDefinition == null)
        {
            for (QName definition : dictionaryService.getAllTypes())
            {
                if (definition.getNamespaceURI().equalsIgnoreCase(search.getNamespaceURI()))
                {
                    if (definition.getLocalName().equalsIgnoreCase(search.getLocalName()))
                    {
                        if (match == null)
                        {
                            match = definition;
                        }
                        else
                        {
                            throw new LuceneQueryParserException("Ambiguous data datype " + string);
                        }
                    }
                }
            }
            for (QName definition : dictionaryService.getAllAspects())
            {
                if (definition.getNamespaceURI().equalsIgnoreCase(search.getNamespaceURI()))
                {
                    if (definition.getLocalName().equalsIgnoreCase(search.getLocalName()))
                    {
                        if (match == null)
                        {
                            match = definition;
                        }
                        else
                        {
                            throw new LuceneQueryParserException("Ambiguous data datype " + string);
                        }
                    }
                }
            }
        }
        else
        {
            return classDefinition;
        }
        if (match == null)
        {
            return null;
        }
        else
        {
            return dictionaryService.getClass(match);
        }
    }

    /**
     * @param field
     * @param queryText
     * @param analysisMode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
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
            else if (field.equals(FIELD_READER))
            {
                return createReaderQuery(queryText);
            }
            else if (field.equals(FIELD_AUTHORITY))
            {
                return createAuthorityQuery(queryText);
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
                ClassDefinition target = matchClassDefinition(queryText);
                if (target == null)
                {
                    throw new LuceneQueryParserException("Invalid type: " + queryText);
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
            else if (field.startsWith(PROPERTY_FIELD_PREFIX))
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
            else if (matchDataTypeDefinition(field) != null)
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
            else
            {
                return getFieldQueryImpl(field, queryText, analysisMode, luceneFunction);
            }

        }
        catch (SAXPathException e)
        {
            throw new ParseException("Failed to parse XPath...\n" + e.getMessage());
        }

    }

    /**
     * @param queryText
     * @return
     */
    protected abstract Query createAclIdQuery(String queryText) throws ParseException;

    /**
     * @param queryText
     * @return
     */
    protected abstract Query createOwnerQuery(String queryText) throws ParseException;

    /**
     * @param queryText
     * @return
     */
    protected abstract Query createReaderQuery(String queryText) throws ParseException;

    /**
     * @param queryText
     * @return
     */
    protected abstract Query createAuthorityQuery(String queryText) throws ParseException;

    /**
     * @param queryText
     * @return
     */
    protected Query createDbidQuery(String queryText) throws ParseException
    {
        return getFieldQueryImpl(FIELD_DBID, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    protected Query createTxIdQuery(String queryText) throws ParseException
    {
        return getFieldQueryImpl(FIELD_TXID, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }
    
    protected Query createAclTxIdQuery(String queryText) throws ParseException
    {
        return getFieldQueryImpl(FIELD_ACLTXID, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    protected Query createTxCommitTimeQuery(String queryText) throws ParseException
    {
        return getFieldQueryImpl(FIELD_TXCOMMITTIME, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }
    
    protected Query createAclTxCommitTimeQuery(String queryText) throws ParseException
    {
        return getFieldQueryImpl(FIELD_ACLTXCOMMITTIME, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    protected Query createDataTypeDefinitionQuery(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        Collection<QName> contentAttributes = dictionaryService.getAllProperties(matchDataTypeDefinition(field).getName());
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
        PropertyDefinition pd = matchPropertyDefinition(queryText);
        if (pd != null)
        {
            ClassDefinition containerClass = pd.getContainerClass();
            QName container = containerClass.getName();
            BooleanQuery query = new BooleanQuery();
            String classType = containerClass.isAspect() ? FIELD_ASPECT : FIELD_TYPE;
            Query typeQuery = getFieldQuery(classType, container.toString(), analysisMode, luceneFunction);
            Query presenceQuery = getWildcardQuery(PROPERTY_FIELD_PREFIX + pd.getName().toString(), "*");
            if ((typeQuery != null) && (presenceQuery != null))
            {
                // query.add(typeQuery, Occur.MUST);
                query.add(presenceQuery, Occur.MUST);
            }
            return query;
        }
        else
        {
            return getFieldQueryImpl(FIELD_ISNOTNULL, queryText, analysisMode, luceneFunction);
        }
    }

    protected Query createIsNullQuery(String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        PropertyDefinition pd = matchPropertyDefinition(queryText);
        if (pd != null)
        {
            BooleanQuery query = new BooleanQuery();
            Query presenceQuery = getWildcardQuery(PROPERTY_FIELD_PREFIX + pd.getName().toString(), "*");
            if (presenceQuery != null)
            {
                query.add(new MatchAllDocsQuery(), Occur.MUST);
                query.add(presenceQuery, Occur.MUST_NOT);
            }
            return query;
        }
        else
        {
            return getFieldQueryImpl(FIELD_ISNULL, queryText, analysisMode, luceneFunction);
        }
    }

    protected Query createIsUnsetQuery(String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        PropertyDefinition pd = matchPropertyDefinition(queryText);
        if (pd != null)
        {
            ClassDefinition containerClass = pd.getContainerClass();
            QName container = containerClass.getName();
            BooleanQuery query = new BooleanQuery();
            String classType = containerClass.isAspect() ? FIELD_ASPECT : FIELD_TYPE;
            Query typeQuery = getFieldQuery(classType, container.toString(), analysisMode, luceneFunction);
            Query presenceQuery = getWildcardQuery(PROPERTY_FIELD_PREFIX + pd.getName().toString(), "*");
            if ((typeQuery != null) && (presenceQuery != null))
            {
                query.add(typeQuery, Occur.MUST);
                query.add(presenceQuery, Occur.MUST_NOT);
            }
            return query;
        }
        else
        {
            return getFieldQueryImpl(FIELD_ISUNSET, queryText, analysisMode, luceneFunction);
        }
    }

    protected Query createAllQuery(String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        Set<String> all = searchParameters.getAllAttributes();
        if ((all == null) || (all.size() == 0))
        {
            Collection<QName> contentAttributes = dictionaryService.getAllProperties(null);
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
        else
        {
            BooleanQuery query = new BooleanQuery();
            for (String fieldName : all)
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

    protected Query createAspectQuery(String queryText, boolean exactOnly)
    {
        AspectDefinition target = matchAspectDefinition(queryText);
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

    protected Query createTypeQuery(String queryText, boolean exactOnly)
    {
        TypeDefinition target = matchTypeDefinition(queryText);
        if (target == null)
        {
            throw new LuceneQueryParserException("Invalid type: " + queryText);
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

    protected abstract Query createAssocTypeQNameQuery(String queryText) throws SAXPathException;

    protected abstract Query createPrimaryAssocTypeQNameQuery(String queryText) throws SAXPathException;

    protected abstract Query createQNameQuery(String queryText) throws SAXPathException;

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

    protected Query createIdQuery(String queryText)
    {
        return createNodeRefQuery(FIELD_ID, queryText);
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

    abstract protected Query createPathQuery(String queryText, boolean withRepeats) throws SAXPathException;

    protected Query createTextQuery(String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        Set<String> text = searchParameters.getTextAttributes();
        if ((text == null) || (text.size() == 0))
        {
            Collection<QName> contentAttributes = dictionaryService.getAllProperties(DataTypeDefinition.CONTENT);
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

    protected Query getFieldQueryImpl(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        // Use the analyzer to get all the tokens, and then build a TermQuery,
        // PhraseQuery, or noth

        // TODO: Untokenised columns with functions require special handling

        if (luceneFunction != LuceneFunction.FIELD)
        {
            throw new UnsupportedOperationException("Field queries are not supported on lucene functions (UPPER, LOWER, etc)");
        }

        boolean requiresMLTokenDuplication = false;
        String testText = queryText;
        String localeString = null;
        if (field.startsWith(PROPERTY_FIELD_PREFIX))
        {
            if ((queryText.length() > 0) && (queryText.charAt(0) == '\u0000'))
            {
                int position = queryText.indexOf("\u0000", 1);
                testText = queryText.substring(position + 1);
                requiresMLTokenDuplication = true;
                localeString = queryText.substring(1, position);
            }
        }

        TokenStream source = getAnalyzer().tokenStream(field, new StringReader(queryText), analysisMode);

        ArrayList<org.apache.lucene.analysis.Token> list = new ArrayList<org.apache.lucene.analysis.Token>();
        org.apache.lucene.analysis.Token reusableToken = new org.apache.lucene.analysis.Token();
        org.apache.lucene.analysis.Token nextToken;
        int positionCount = 0;
        boolean severalTokensAtSamePosition = false;

        while (true)
        {
            try
            {
                nextToken = source.next(reusableToken);
            }
            catch (IOException e)
            {
                nextToken = null;
            }
            if (nextToken == null)
                break;
            list.add((org.apache.lucene.analysis.Token) nextToken.clone());
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
            if ((current == '*') || (current == '?'))
            {
                StringBuilder pre = new StringBuilder(10);
                if (index > 0)
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
                                if ((test.startOffset() <= i) && (i <= test.endOffset()))
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
                    }
                    if (pre.length() > 0)
                    {
                        // Add new token followed by * not given by the tokeniser
                        org.apache.lucene.analysis.Token newToken = new org.apache.lucene.analysis.Token(index - pre.length(), index);
                        newToken.setTermBuffer(pre.toString());
                        newToken.setType("ALPHANUM");
                        if (requiresMLTokenDuplication)
                        {
                            Locale locale = I18NUtil.parseLocale(localeString);
                            MLAnalysisMode mlAnalysisMode = searchParameters.getMlAnalaysisMode() == null ? defaultSearchMLAnalysisMode : searchParameters.getMlAnalaysisMode();
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
                                if ((test.startOffset() <= i) && (i <= test.endOffset()))
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
                    }
                    if (post.length() > 0)
                    {
                        // Add new token followed by * not given by the tokeniser
                        org.apache.lucene.analysis.Token newToken = new org.apache.lucene.analysis.Token(index + 1, index + 1 + post.length());
                        newToken.setTermBuffer(post.toString());
                        newToken.setType("ALPHANUM");
                        if (requiresMLTokenDuplication)
                        {
                            Locale locale = I18NUtil.parseLocale(localeString);
                            MLAnalysisMode mlAnalysisMode = searchParameters.getMlAnalaysisMode() == null ? defaultSearchMLAnalysisMode : searchParameters.getMlAnalaysisMode();
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

        // Assume we only string together tokens for the same position

        int max = 0;
        int current = 0;
        for (org.apache.lucene.analysis.Token c : list)
        {
            if (c.getPositionIncrement() == 0)
            {
                current++;
            }
            else
            {
                if (current > max)
                {
                    max = current;
                }
                current = 0;
            }
        }
        if (current > max)
        {
            max = current;
        }

        ArrayList<org.apache.lucene.analysis.Token> fixed = new ArrayList<org.apache.lucene.analysis.Token>();
        for (int repeat = 0; repeat <= max; repeat++)
        {
            org.apache.lucene.analysis.Token replace = null;
            current = 0;
            for (org.apache.lucene.analysis.Token c : list)
            {
                if (c.getPositionIncrement() == 0)
                {
                    current++;
                }
                else
                {
                    current = 0;
                }

                if (current == repeat)
                {

                    if (replace == null)
                    {
                        StringBuilder prefix = new StringBuilder();
                        for (int i = c.startOffset() - 1; i >= 0; i--)
                        {
                            char test = testText.charAt(i);
                            if ((test == '*') || (test == '?'))
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
                            String termText = new String(c.termBuffer(), 0, c.termLength());
                            int position = termText.indexOf("}");
                            String language = termText.substring(0, position + 1);
                            String token = termText.substring(position + 1);
                            replace = new org.apache.lucene.analysis.Token(c.startOffset() - pre.length(), c.endOffset());
                            replace.setTermBuffer(language + pre + token);
                            replace.setType(c.type());
                            replace.setPositionIncrement(c.getPositionIncrement());
                        }
                        else
                        {
                            String termText = new String(c.termBuffer(), 0, c.termLength());
                            replace = new org.apache.lucene.analysis.Token(c.startOffset() - pre.length(), c.endOffset());
                            replace.setTermBuffer(pre + termText);
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
                            if ((test == '*') || (test == '?'))
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
                            String termText = new String(c.termBuffer(), 0, c.termLength());
                            if (requiresMLTokenDuplication)
                            {
                                int position = termText.indexOf("}");
                                @SuppressWarnings("unused")
                                String language = termText.substring(0, position + 1);
                                String token = termText.substring(position + 1);
                                int oldPositionIncrement = replace.getPositionIncrement();
                                String replaceTermText = new String(replace.termBuffer(), 0, replace.termLength());
                                replace = new org.apache.lucene.analysis.Token(replace.startOffset(), c.endOffset());
                                replace.setTermBuffer(replaceTermText + pre + token);
                                replace.setType(replace.type());
                                replace.setPositionIncrement(oldPositionIncrement);
                            }
                            else
                            {
                                int oldPositionIncrement = replace.getPositionIncrement();
                                String replaceTermText = new String(replace.termBuffer(), 0, replace.termLength());
                                replace = new org.apache.lucene.analysis.Token(replace.startOffset(), c.endOffset());
                                replace.setTermBuffer(replaceTermText + pre + termText);
                                replace.setType(replace.type());
                                replace.setPositionIncrement(oldPositionIncrement);
                            }
                        }
                        else
                        {
                            String termText = new String(c.termBuffer(), 0, c.termLength());
                            if (requiresMLTokenDuplication)
                            {
                                int position = termText.indexOf("}");
                                String language = termText.substring(0, position + 1);
                                String token = termText.substring(position + 1);
                                String replaceTermText = new String(replace.termBuffer(), 0, replace.termLength());
                                org.apache.lucene.analysis.Token last = new org.apache.lucene.analysis.Token(replace.startOffset(), replace.endOffset() + post.length());
                                last.setTermBuffer(replaceTermText + post);
                                last.setType(replace.type());
                                last.setPositionIncrement(replace.getPositionIncrement());
                                fixed.add(last);
                                replace = new org.apache.lucene.analysis.Token(c.startOffset() - pre.length(), c.endOffset());
                                replace.setTermBuffer(language + pre + token);
                                replace.setType(c.type());
                                replace.setPositionIncrement(c.getPositionIncrement());
                            }
                            else
                            {
                                String replaceTermText = new String(replace.termBuffer(), 0, replace.termLength());
                                org.apache.lucene.analysis.Token last = new org.apache.lucene.analysis.Token(replace.startOffset(), replace.endOffset() + post.length());
                                last.setTermBuffer(replaceTermText + post);
                                last.setType(replace.type());
                                last.setPositionIncrement(replace.getPositionIncrement());
                                fixed.add(last);
                                replace = new org.apache.lucene.analysis.Token(c.startOffset() - pre.length(), c.endOffset());
                                replace.setTermBuffer(pre + termText);
                                replace.setType(c.type());
                                replace.setPositionIncrement(c.getPositionIncrement());
                            }
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
                        if ((test == '*') || (test == '?'))
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
                String replaceTermText = new String(replace.termBuffer(), 0, replace.termLength());
                replace = new org.apache.lucene.analysis.Token(replace.startOffset(), replace.endOffset() + post.length());
                replace.setTermBuffer(replaceTermText + post);
                replace.setType(replace.type());
                replace.setPositionIncrement(oldPositionIncrement);
                fixed.add(replace);

            }
        }

        // Add in any missing words containsing * and ?

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
                    return o2.getPositionIncrement() - o1.getPositionIncrement();
                }
            }
        });

        list = fixed;

        if (list.size() == 0)
            return null;
        else if (list.size() == 1)
        {
            nextToken = (org.apache.lucene.analysis.Token) list.get(0);
            String termText = new String(nextToken.termBuffer(), 0, nextToken.termLength());
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
                        nextToken = (org.apache.lucene.analysis.Token) list.get(i);
                        String termText = new String(nextToken.termBuffer(), 0, nextToken.termLength());
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
                else
                {
                    // phrase query:
                    MultiPhraseQuery mpq = newMultiPhraseQuery();
                    mpq.setSlop(internalSlop);
                    ArrayList<Term> multiTerms = new ArrayList<Term>();
                    int position = -1;
                    for (int i = 0; i < list.size(); i++)
                    {
                        nextToken = (org.apache.lucene.analysis.Token) list.get(i);
                        String termText = new String(nextToken.termBuffer(), 0, nextToken.termLength());
                        if (nextToken.getPositionIncrement() > 0 && multiTerms.size() > 0)
                        {
                            if (getEnablePositionIncrements())
                            {
                                mpq.add((Term[]) multiTerms.toArray(new Term[0]), position);
                            }
                            else
                            {
                                mpq.add((Term[]) multiTerms.toArray(new Term[0]));
                            }
                            multiTerms.clear();
                        }
                        position += nextToken.getPositionIncrement();

                        Term term = new Term(field, termText);
                        if ((termText != null) && (termText.contains("*") || termText.contains("?")))
                        {
                            addWildcardTerms(multiTerms, term);
                        }
                        else
                        {
                            multiTerms.add(term);
                        }
                    }
                    if (getEnablePositionIncrements())
                    {
                        if (multiTerms.size() > 0)
                        {
                            mpq.add((Term[]) multiTerms.toArray(new Term[0]), position);
                        }
                        else
                        {
                            mpq.add(new Term[] { new Term(field, "\u0000") }, position);
                        }
                    }
                    else
                    {
                        if (multiTerms.size() > 0)
                        {
                            mpq.add((Term[]) multiTerms.toArray(new Term[0]));
                        }
                        else
                        {
                            mpq.add(new Term[] { new Term(field, "\u0000") });
                        }
                    }
                    return mpq;
                }
            }
            else
            {
                MultiPhraseQuery q = new MultiPhraseQuery();
                q.setSlop(internalSlop);
                int position = -1;
                for (int i = 0; i < list.size(); i++)
                {
                    nextToken = (org.apache.lucene.analysis.Token) list.get(i);
                    String termText = new String(nextToken.termBuffer(), 0, nextToken.termLength());
                    Term term = new Term(field, termText);
                    if (getEnablePositionIncrements())
                    {
                        position += nextToken.getPositionIncrement();
                        if ((termText != null) && (termText.contains("*") || termText.contains("?")))
                        {
                            q.add(getMatchingTerms(field, term), position);
                        }
                        else
                        {
                            q.add(new Term[] { term }, position);
                        }
                    }
                    else
                    {
                        if ((termText != null) && (termText.contains("*") || termText.contains("?")))
                        {
                            q.add(getMatchingTerms(field, term));
                        }
                        else
                        {
                            q.add(term);
                        }
                    }
                }
                return q;
            }
        }
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

    private void addWildcardTerms(ArrayList<Term> terms, Term term) throws ParseException
    {
        try
        {
            Term searchTerm = term;
            if (getLowercaseExpandedTerms())
            {
                searchTerm = new Term(term.field(), term.text().toLowerCase());
            }
            WildcardTermEnum wcte = new WildcardTermEnum(indexReader, searchTerm);

            while (!wcte.endEnum())
            {
                Term current = wcte.term();
                if ((current.text() != null) && (current.text().length() > 0) && (current.text().charAt(0) == '{'))
                {
                    if ((searchTerm != null) && (searchTerm.text().length() > 0) && (searchTerm.text().charAt(0) == '{'))
                    {
                        terms.add(current);
                    }
                    // If not, we cod not add so wildcards do not match the locale prefix
                }
                else
                {
                    terms.add(current);
                }

                wcte.next();
            }
        }
        catch (IOException e)
        {
            throw new ParseException("IO error generating phares wildcards " + e.getMessage());
        }
    }

    /**
     * @exception ParseException
     *                throw in overridden method to disallow
     */
    protected Query getRangeQuery(String field, String part1, String part2, boolean inclusive) throws ParseException
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
                Collection<QName> contentAttributes = dictionaryService.getAllProperties(DataTypeDefinition.CONTENT);
                BooleanQuery query = new BooleanQuery();
                for (QName qname : contentAttributes)
                {
                    // The super implementation will create phrase queries etc if required
                    Query part = getRangeQuery(PROPERTY_FIELD_PREFIX + qname.toString(), part1, part2, includeLower, includeUpper, analysisMode, luceneFunction);
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
        if (field.startsWith(PROPERTY_FIELD_PREFIX))
        {
            String fieldName;
            PropertyDefinition propertyDef = matchPropertyDefinition(field.substring(1));
            if (propertyDef != null)
            {
                fieldName = PROPERTY_FIELD_PREFIX + propertyDef.getName();
            }
            else
            {
                fieldName = expandAttributeFieldName(field);
            }

            IndexTokenisationMode tokenisationMode = IndexTokenisationMode.TRUE;
            if (propertyDef != null)
            {
                tokenisationMode = propertyDef.getIndexTokenisationMode();
                if (tokenisationMode == null)
                {
                    tokenisationMode = IndexTokenisationMode.TRUE;
                }
            }

            if (propertyDef != null)
            {
                // LOWER AND UPPER
                if (luceneFunction != LuceneFunction.FIELD)
                {
                    if (propertyDef.getDataType().getName().equals(DataTypeDefinition.TEXT))
                    {
                        BooleanQuery booleanQuery = new BooleanQuery();
                        MLAnalysisMode mlAnalysisMode = searchParameters.getMlAnalaysisMode() == null ? defaultSearchMLAnalysisMode : searchParameters.getMlAnalaysisMode();
                        List<Locale> locales = searchParameters.getLocales();
                        List<Locale> expandedLocales = new ArrayList<Locale>();
                        for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
                        {
                            expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, false));
                        }
                        for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
                        {
                            addLocaleSpecificUntokenisedTextRangeFunction(fieldName, part1, part2, includeLower, includeUpper, luceneFunction, booleanQuery, mlAnalysisMode,
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
                    throw new UnsupportedOperationException("Range is not supported against content");
                }
                else if (propertyDef.getDataType().getName().equals(DataTypeDefinition.TEXT))
                {
                    BooleanQuery booleanQuery = new BooleanQuery();
                    MLAnalysisMode mlAnalysisMode = searchParameters.getMlAnalaysisMode() == null ? defaultSearchMLAnalysisMode : searchParameters.getMlAnalaysisMode();
                    List<Locale> locales = searchParameters.getLocales();
                    List<Locale> expandedLocales = new ArrayList<Locale>();
                    for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
                    {
                        expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, false));
                    }
                    for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
                    {

                        addTextRange(field, part1, part2, includeLower, includeUpper, analysisMode, fieldName, propertyDef, tokenisationMode, booleanQuery, mlAnalysisMode, locale);

                    }
                    return booleanQuery;
                }
                else if (propertyDef.getDataType().getName().equals(DataTypeDefinition.DATETIME))
                {
                    String analyserClassName = propertyDef.resolveAnalyserClassName();
                    boolean usesDateTimeAnalyser = analyserClassName.equals(DateTimeAnalyser.class.getCanonicalName());
                    // Expand query for internal date time format

                    if (usesDateTimeAnalyser)
                    {
                        Calendar start = Calendar.getInstance();
                        int startResolution;
                        Calendar end = Calendar.getInstance();
                        int endResolution;
                        try
                        {
                            Pair<Date, Integer> result = CachingDateFormat.lenientParse(part1, Calendar.YEAR);
                            start.setTime(result.getFirst());
                            startResolution = result.getSecond();
                        }
                        catch (java.text.ParseException e)
                        {
                            SimpleDateFormat oldDf = CachingDateFormat.getDateFormat();
                            try
                            {
                                Date date = oldDf.parse(part1);
                                start.setTime(date);
                                start.set(Calendar.MILLISECOND, 0);
                                startResolution = Calendar.SECOND;
                            }
                            catch (java.text.ParseException ee)
                            {
                                if (part1.equalsIgnoreCase("min"))
                                {
                                    start.set(Calendar.YEAR, start.getMinimum(Calendar.YEAR));
                                    start.set(Calendar.DAY_OF_YEAR, start.getMinimum(Calendar.DAY_OF_YEAR));
                                    start.set(Calendar.HOUR_OF_DAY, start.getMinimum(Calendar.HOUR_OF_DAY));
                                    start.set(Calendar.MINUTE, start.getMinimum(Calendar.MINUTE));
                                    start.set(Calendar.SECOND, start.getMinimum(Calendar.SECOND));
                                    start.set(Calendar.MILLISECOND, start.getMinimum(Calendar.MILLISECOND));
                                    startResolution = Calendar.MILLISECOND;
                                }
                                else if (part1.equalsIgnoreCase("now"))
                                {
                                    start.setTime(new Date());
                                    startResolution = Calendar.MILLISECOND;
                                }
                                else if (part1.equalsIgnoreCase("today"))
                                {
                                    start.setTime(new Date());
                                    start.set(Calendar.HOUR_OF_DAY, start.getMinimum(Calendar.HOUR_OF_DAY));
                                    start.set(Calendar.MINUTE, start.getMinimum(Calendar.MINUTE));
                                    start.set(Calendar.SECOND, start.getMinimum(Calendar.SECOND));
                                    start.set(Calendar.MILLISECOND, start.getMinimum(Calendar.MILLISECOND));
                                    startResolution = Calendar.DAY_OF_MONTH;

                                }
                                else
                                {
                                    return createNoMatchQuery();
                                }
                            }
                        }
                        try
                        {
                            Pair<Date, Integer> result = CachingDateFormat.lenientParse(part2, Calendar.YEAR);
                            end.setTime(result.getFirst());
                            endResolution = result.getSecond();
                        }
                        catch (java.text.ParseException e)
                        {
                            SimpleDateFormat oldDf = CachingDateFormat.getDateFormat();
                            try
                            {
                                Date date = oldDf.parse(part2);
                                end.setTime(date);
                                end.set(Calendar.MILLISECOND, 0);
                                endResolution = Calendar.SECOND;
                            }
                            catch (java.text.ParseException ee)
                            {
                                if (part2.equalsIgnoreCase("max"))
                                {
                                    end.set(Calendar.YEAR, end.getMaximum(Calendar.YEAR));
                                    end.set(Calendar.DAY_OF_YEAR, end.getMaximum(Calendar.DAY_OF_YEAR));
                                    end.set(Calendar.HOUR_OF_DAY, end.getMaximum(Calendar.HOUR_OF_DAY));
                                    end.set(Calendar.MINUTE, end.getMaximum(Calendar.MINUTE));
                                    end.set(Calendar.SECOND, end.getMaximum(Calendar.SECOND));
                                    end.set(Calendar.MILLISECOND, end.getMaximum(Calendar.MILLISECOND));
                                    endResolution = Calendar.MILLISECOND;
                                }
                                else if (part2.equalsIgnoreCase("now"))
                                {
                                    end.setTime(new Date());
                                    endResolution = Calendar.MILLISECOND;
                                }
                                else if (part1.equalsIgnoreCase("today"))
                                {
                                    end.setTime(new Date());
                                    end.set(Calendar.HOUR_OF_DAY, end.getMinimum(Calendar.HOUR_OF_DAY));
                                    end.set(Calendar.MINUTE, end.getMinimum(Calendar.MINUTE));
                                    end.set(Calendar.SECOND, end.getMinimum(Calendar.SECOND));
                                    end.set(Calendar.MILLISECOND, end.getMinimum(Calendar.MILLISECOND));
                                    endResolution = Calendar.DAY_OF_MONTH;
                                }
                                else
                                {
                                    return createNoMatchQuery();
                                }
                            }
                        }

                        // Build a composite query for all the bits
                        Query rq = buildDateTimeRange(fieldName, start, startResolution, end, endResolution, includeLower, includeUpper);
                        return rq;
                    }
                    else
                    {
                        // Old Date time
                        String first = getToken(fieldName, part1, AnalysisMode.DEFAULT);
                        String last = getToken(fieldName, part2, AnalysisMode.DEFAULT);
                        return new ConstantScoreRangeQuery(fieldName, first, last, includeLower, includeUpper);
                    }
                }
                else
                {
                    // Default property behaviour
                    String first = getToken(fieldName, part1, AnalysisMode.DEFAULT);
                    String last = getToken(fieldName, part2, AnalysisMode.DEFAULT);
                    return new ConstantScoreRangeQuery(fieldName, first, last, includeLower, includeUpper);
                }
            }
            else
            {
                // No DD def
                String first = getToken(fieldName, part1, AnalysisMode.DEFAULT);
                String last = getToken(fieldName, part2, AnalysisMode.DEFAULT);
                return new ConstantScoreRangeQuery(fieldName, first, last, includeLower, includeUpper);
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
        else if (matchDataTypeDefinition(field) != null)
        {
            Collection<QName> contentAttributes = dictionaryService.getAllProperties(matchDataTypeDefinition(field).getName());
            BooleanQuery query = new BooleanQuery();
            for (QName qname : contentAttributes)
            {
                Query part = getRangeQuery(PROPERTY_FIELD_PREFIX + qname.toString(), part1, part2, includeLower, includeUpper, analysisMode, luceneFunction);
                query.add(part, Occur.SHOULD);
            }
            return query;
        }
        // FIELD_FTSSTATUS uses the default
        else
        {
            // None property - leave alone
            if (getLowercaseExpandedTerms())
            {
                part1 = part1.toLowerCase();
                part2 = part2.toLowerCase();
            }
            return new ConstantScoreRangeQuery(field, part1, part2, includeLower, includeUpper);
        }
    }

    /**
     * @param field
     * @param part1
     * @param part2
     * @param includeLower
     * @param includeUpper
     * @param analysisMode
     * @param fieldName
     * @param propertyDef
     * @param tokenisationMode
     * @param booleanQuery
     * @param mlAnalysisMode
     * @param locale
     * @throws ParseException
     */
    protected abstract void addTextRange(String field, String part1, String part2, boolean includeLower, boolean includeUpper, AnalysisMode analysisMode, String fieldName,
            PropertyDefinition propertyDef, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale) throws ParseException;

    protected abstract void addLocaleSpecificUntokenisedTextRangeFunction(String expandedFieldName, String lower, String upper, boolean includeLower, boolean includeUpper,
            LuceneFunction luceneFunction, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale, IndexTokenisationMode tokenisationMode) throws ParseException;

    // private Query buildRangeFunctionQuery(String expandedFieldName, String lowerTermText, String upperTermText,
    // boolean includeLower, boolean includeUpper,
    // LuceneFunction luceneFunction)
    // {
    // String testLowerTermText = lowerTermText;
    // if (testLowerTermText.startsWith("{"))
    // {
    // int index = lowerTermText.indexOf("}");
    // testLowerTermText = lowerTermText.substring(index + 1);
    // }
    //
    // String testUpperTermText = upperTermText;
    // if (testUpperTermText.startsWith("{"))
    // {
    // int index = upperTermText.indexOf("}");
    // testUpperTermText = upperTermText.substring(index + 1);
    // }
    //
    // switch (luceneFunction)
    // {
    // case LOWER:
    // if (testLowerTermText.equals(testLowerTermText.toLowerCase()) &&
    // testUpperTermText.equals(testUpperTermText.toLowerCase()))
    // {
    // return new CaseInsensitiveFieldRangeQuery(expandedFieldName, lowerTermText, upperTermText, includeLower,
    // includeUpper);
    // }
    // else
    // {
    // // No match
    // return createNoMatchQuery();
    // }
    // case UPPER:
    // if (testLowerTermText.equals(testLowerTermText.toUpperCase()) &&
    // testUpperTermText.equals(testUpperTermText.toUpperCase()))
    // {
    // return new CaseInsensitiveFieldRangeQuery(expandedFieldName, lowerTermText, upperTermText, includeLower,
    // includeUpper);
    // }
    // else
    // {
    // // No match
    // return createNoMatchQuery();
    // }
    // default:
    // throw new UnsupportedOperationException("Unsupported Lucene Function " + luceneFunction);
    //
    // }
    // }

    // private void addLocaleSpecificTokenisedTextRange(String part1, String part2, boolean includeLower, boolean
    // includeUpper, AnalysisMode analysisMode, String fieldName,
    // BooleanQuery booleanQuery, Locale locale, String textFieldName) throws ParseException
    // {
    // StringBuilder builder = new StringBuilder();
    // builder.append("\u0000").append(locale.toString()).append("\u0000").append(part1);
    // String first = getToken(fieldName, builder.toString(), analysisMode);
    //
    // builder = new StringBuilder();
    // builder.append("\u0000").append(locale.toString()).append("\u0000").append(part2);
    // String last = getToken(fieldName, builder.toString(), analysisMode);
    //
    // Query query = new ConstantScoreRangeQuery(textFieldName, first, last, includeLower, includeUpper);
    // booleanQuery.add(query, Occur.SHOULD);
    // }
    //
    // private void addLocaleSpecificUntokenisedTextRange(String field, String part1, String part2, boolean
    // includeLower, boolean includeUpper, BooleanQuery booleanQuery,
    // MLAnalysisMode mlAnalysisMode, Locale locale, String textFieldName)
    // {
    // String lower = part1;
    // String upper = part2;
    // if (locale.toString().length() > 0)
    // {
    // lower = "{" + locale + "}" + part1;
    // upper = "{" + locale + "}" + part2;
    // }
    //
    // Query subQuery = new ConstantScoreRangeQuery(textFieldName, lower, upper, includeLower, includeUpper);
    // booleanQuery.add(subQuery, Occur.SHOULD);
    //
    // if (booleanQuery.getClauses().length == 0)
    // {
    // booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
    // }
    // }

    private Query buildDateTimeRange(String field, Calendar startIn, int startResolution, Calendar endIn, int endResolution, boolean includeLower, boolean includeUpper)
            throws ParseException
    {
        int minResolution = (startResolution <= endResolution) ? startResolution : endResolution;

        // fix start and end dates and treat all as inclusive ranges

        Calendar start = Calendar.getInstance();
        start.setTime(startIn.getTime());
        if (!includeLower)
        {
            start.add(startResolution, 1);
        }

        Calendar end = Calendar.getInstance();
        end.setTime(endIn.getTime());
        if (!includeUpper)
        {
            end.add(endResolution, -1);
        }

        // Calendar comparison does not work for MAX .... joy
        if (start.get(Calendar.YEAR) > end.get(Calendar.YEAR))
        {
            return createNoMatchQuery();
        }
        else if (start.get(Calendar.YEAR) == end.get(Calendar.YEAR))
        {
            if (start.get(Calendar.MONTH) > end.get(Calendar.MONTH))
            {
                return createNoMatchQuery();
            }
            else if (start.get(Calendar.MONTH) == end.get(Calendar.MONTH))
            {
                if (start.get(Calendar.DAY_OF_MONTH) > end.get(Calendar.DAY_OF_MONTH))
                {
                    return createNoMatchQuery();
                }
                else if (start.get(Calendar.DAY_OF_MONTH) == end.get(Calendar.DAY_OF_MONTH))
                {
                    if (start.get(Calendar.HOUR_OF_DAY) > end.get(Calendar.HOUR_OF_DAY))
                    {
                        return createNoMatchQuery();
                    }
                    else if (start.get(Calendar.HOUR_OF_DAY) == end.get(Calendar.HOUR_OF_DAY))
                    {
                        if (start.get(Calendar.MINUTE) > end.get(Calendar.MINUTE))
                        {
                            return createNoMatchQuery();
                        }
                        else if (start.get(Calendar.MINUTE) == end.get(Calendar.MINUTE))
                        {
                            if (start.get(Calendar.SECOND) > end.get(Calendar.SECOND))
                            {
                                return createNoMatchQuery();
                            }
                            else if (start.get(Calendar.SECOND) == end.get(Calendar.SECOND))
                            {
                                if (start.get(Calendar.MILLISECOND) > end.get(Calendar.MILLISECOND))
                                {
                                    return createNoMatchQuery();
                                }
                                else if (start.get(Calendar.MILLISECOND) == end.get(Calendar.MILLISECOND))
                                {
                                    // continue
                                }
                            }
                        }
                    }
                }
            }
        }

        BooleanQuery query = new BooleanQuery();
        Query part;
        if ((minResolution > Calendar.YEAR) && (start.get(Calendar.YEAR) == end.get(Calendar.YEAR)))
        {
            part = new TermQuery(new Term(field, "YE" + start.get(Calendar.YEAR)));
            query.add(part, Occur.MUST);
            if ((minResolution > Calendar.MONTH) && (start.get(Calendar.MONTH) == end.get(Calendar.MONTH)))
            {
                part = new TermQuery(new Term(field, build2SF("MO", start.get(Calendar.MONTH))));
                query.add(part, Occur.MUST);
                if ((minResolution > Calendar.DAY_OF_MONTH) && (start.get(Calendar.DAY_OF_MONTH) == end.get(Calendar.DAY_OF_MONTH)))
                {
                    part = new TermQuery(new Term(field, build2SF("DA", start.get(Calendar.DAY_OF_MONTH))));
                    query.add(part, Occur.MUST);
                    if ((minResolution > Calendar.HOUR_OF_DAY) && (start.get(Calendar.HOUR_OF_DAY) == end.get(Calendar.HOUR_OF_DAY)))
                    {
                        part = new TermQuery(new Term(field, build2SF("HO", start.get(Calendar.HOUR_OF_DAY))));
                        query.add(part, Occur.MUST);
                        if ((minResolution > Calendar.MINUTE) && (start.get(Calendar.MINUTE) == end.get(Calendar.MINUTE)))
                        {
                            part = new TermQuery(new Term(field, build2SF("MI", start.get(Calendar.MINUTE))));
                            query.add(part, Occur.MUST);
                            if ((minResolution > Calendar.SECOND) && (start.get(Calendar.SECOND) == end.get(Calendar.SECOND)))
                            {
                                part = new TermQuery(new Term(field, build2SF("SE", start.get(Calendar.SECOND))));
                                query.add(part, Occur.MUST);
                                if (minResolution >= Calendar.MILLISECOND)
                                {
                                    if (start.get(Calendar.MILLISECOND) == end.get(Calendar.MILLISECOND))
                                    {

                                        part = new TermQuery(new Term(field, build3SF("MS", start.get(Calendar.MILLISECOND))));
                                        query.add(part, Occur.MUST);

                                    }
                                    else
                                    {
                                        part = new ConstantScoreRangeQuery(field, build3SF("MS", start.get(Calendar.MILLISECOND)), build3SF("MS", end.get(Calendar.MILLISECOND)),
                                                true, true);
                                        query.add(part, Occur.MUST);
                                    }
                                }
                                else
                                {
                                    return createNoMatchQuery();
                                }
                            }
                            else
                            {
                                // s + ms

                                BooleanQuery subQuery = new BooleanQuery();
                                Query subPart;

                                for (int i : new int[] { Calendar.MILLISECOND })
                                {
                                    subPart = buildStart(field, start, Calendar.SECOND, i, startResolution);
                                    if (subPart != null)
                                    {
                                        subQuery.add(subPart, Occur.SHOULD);
                                    }
                                }

                                if (Calendar.SECOND < minResolution)
                                {
                                    if ((end.get(Calendar.SECOND) - start.get(Calendar.SECOND)) > 1)
                                    {
                                        subPart = new ConstantScoreRangeQuery(field, build2SF("SE", start.get(Calendar.SECOND)), build2SF("SE", end.get(Calendar.SECOND)), false,
                                                false);
                                        subQuery.add(subPart, Occur.SHOULD);
                                    }
                                }
                                if (Calendar.SECOND == minResolution)
                                {
                                    if (start.get(Calendar.SECOND) == end.get(Calendar.SECOND))
                                    {
                                        if (includeLower && includeUpper)
                                        {
                                            part = new TermQuery(new Term(field, build2SF("SE", start.get(Calendar.SECOND))));
                                            query.add(part, Occur.MUST);
                                        }

                                        else
                                        {
                                            return createNoMatchQuery();
                                        }
                                    }
                                    else
                                    {
                                        subPart = new ConstantScoreRangeQuery(field, build2SF("SE", start.get(Calendar.SECOND)), build2SF("SE", end.get(Calendar.SECOND)),
                                                includeLower, includeUpper);
                                        subQuery.add(subPart, Occur.SHOULD);
                                    }
                                }

                                for (int i : new int[] { Calendar.MILLISECOND })
                                {

                                    subPart = buildEnd(field, end, Calendar.SECOND, i, endResolution);
                                    if (subPart != null)
                                    {
                                        subQuery.add(subPart, Occur.SHOULD);
                                    }

                                }

                                if (subQuery.clauses().size() > 0)
                                {
                                    query.add(subQuery, Occur.MUST);
                                }

                            }
                        }
                        else
                        {
                            // min + s + ms

                            BooleanQuery subQuery = new BooleanQuery();
                            Query subPart;

                            for (int i : new int[] { Calendar.MILLISECOND, Calendar.SECOND })
                            {

                                subPart = buildStart(field, start, Calendar.MINUTE, i, startResolution);
                                if (subPart != null)
                                {
                                    subQuery.add(subPart, Occur.SHOULD);
                                }

                            }

                            if (Calendar.MINUTE < minResolution)
                            {
                                if ((end.get(Calendar.MINUTE) - start.get(Calendar.MINUTE)) > 1)
                                {
                                    subPart = new ConstantScoreRangeQuery(field, build2SF("MI", start.get(Calendar.MINUTE)), build2SF("MI", end.get(Calendar.MINUTE)), false, false);
                                    subQuery.add(subPart, Occur.SHOULD);
                                }
                            }
                            if (Calendar.MINUTE == minResolution)
                            {
                                if (start.get(Calendar.MINUTE) == end.get(Calendar.MINUTE))
                                {
                                    if (includeLower && includeUpper)
                                    {
                                        part = new TermQuery(new Term(field, build2SF("MI", start.get(Calendar.MINUTE))));
                                        query.add(part, Occur.MUST);
                                    }

                                    else
                                    {
                                        return createNoMatchQuery();
                                    }
                                }
                                else
                                {
                                    subPart = new ConstantScoreRangeQuery(field, build2SF("MI", start.get(Calendar.MINUTE)), build2SF("MI", end.get(Calendar.MINUTE)),
                                            includeLower, includeUpper);
                                    subQuery.add(subPart, Occur.SHOULD);
                                }
                            }

                            for (int i : new int[] { Calendar.SECOND, Calendar.MILLISECOND })
                            {

                                subPart = buildEnd(field, end, Calendar.MINUTE, i, endResolution);
                                if (subPart != null)
                                {
                                    subQuery.add(subPart, Occur.SHOULD);
                                }

                            }

                            if (subQuery.clauses().size() > 0)
                            {
                                query.add(subQuery, Occur.MUST);
                            }
                        }
                    }
                    else
                    {
                        // hr + min + s + ms

                        BooleanQuery subQuery = new BooleanQuery();
                        Query subPart;

                        for (int i : new int[] { Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE })
                        {

                            subPart = buildStart(field, start, Calendar.HOUR_OF_DAY, i, startResolution);
                            if (subPart != null)
                            {
                                subQuery.add(subPart, Occur.SHOULD);
                            }

                        }

                        if (Calendar.HOUR_OF_DAY < minResolution)
                        {
                            if ((end.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY)) > 1)
                            {
                                subPart = new ConstantScoreRangeQuery(field, build2SF("HO", start.get(Calendar.HOUR_OF_DAY)), build2SF("HO", end.get(Calendar.HOUR_OF_DAY)), false,
                                        false);
                                subQuery.add(subPart, Occur.SHOULD);
                            }
                        }
                        if (Calendar.HOUR_OF_DAY == minResolution)
                        {
                            if (start.get(Calendar.HOUR_OF_DAY) == end.get(Calendar.HOUR_OF_DAY))
                            {
                                if (includeLower && includeUpper)
                                {
                                    part = new TermQuery(new Term(field, build2SF("HO", start.get(Calendar.HOUR_OF_DAY))));
                                    query.add(part, Occur.MUST);
                                }

                                else
                                {
                                    return createNoMatchQuery();
                                }
                            }
                            else
                            {
                                subPart = new ConstantScoreRangeQuery(field, build2SF("HO", start.get(Calendar.HOUR_OF_DAY)), build2SF("HO", end.get(Calendar.HOUR_OF_DAY)),
                                        includeLower, includeUpper);
                                subQuery.add(subPart, Occur.SHOULD);
                            }
                        }
                        for (int i : new int[] { Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND })
                        {

                            subPart = buildEnd(field, end, Calendar.HOUR_OF_DAY, i, endResolution);
                            if (subPart != null)
                            {
                                subQuery.add(subPart, Occur.SHOULD);
                            }

                        }

                        if (subQuery.clauses().size() > 0)
                        {
                            query.add(subQuery, Occur.MUST);
                        }
                    }
                }
                else
                {
                    // day + hr + min + s + ms

                    BooleanQuery subQuery = new BooleanQuery();
                    Query subPart;

                    for (int i : new int[] { Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY })
                    {

                        subPart = buildStart(field, start, Calendar.DAY_OF_MONTH, i, startResolution);
                        if (subPart != null)
                        {
                            subQuery.add(subPart, Occur.SHOULD);
                        }

                    }

                    if (Calendar.DAY_OF_MONTH < minResolution)
                    {
                        if ((end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH)) > 1)
                        {
                            subPart = new ConstantScoreRangeQuery(field, build2SF("DA", start.get(Calendar.DAY_OF_MONTH)), build2SF("DA", end.get(Calendar.DAY_OF_MONTH)), false,
                                    false);
                            subQuery.add(subPart, Occur.SHOULD);
                        }
                    }
                    if (Calendar.DAY_OF_MONTH == minResolution)
                    {
                        if (start.get(Calendar.DAY_OF_MONTH) == end.get(Calendar.DAY_OF_MONTH))
                        {
                            if (includeLower && includeUpper)
                            {
                                part = new TermQuery(new Term(field, build2SF("DA", start.get(Calendar.DAY_OF_MONTH))));
                                query.add(part, Occur.MUST);
                            }

                            else
                            {
                                return createNoMatchQuery();
                            }
                        }
                        else
                        {
                            subPart = new ConstantScoreRangeQuery(field, build2SF("DA", start.get(Calendar.DAY_OF_MONTH)), build2SF("DA", end.get(Calendar.DAY_OF_MONTH)),
                                    includeLower, includeUpper);
                            subQuery.add(subPart, Occur.SHOULD);
                        }
                    }

                    for (int i : new int[] { Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND })
                    {

                        subPart = buildEnd(field, end, Calendar.DAY_OF_MONTH, i, endResolution);
                        if (subPart != null)
                        {
                            subQuery.add(subPart, Occur.SHOULD);
                        }

                    }

                    if (subQuery.clauses().size() > 0)
                    {
                        query.add(subQuery, Occur.MUST);
                    }

                }
            }
            else
            {
                // month + day + hr + min + s + ms

                BooleanQuery subQuery = new BooleanQuery();
                Query subPart;

                for (int i : new int[] { Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH })
                {

                    subPart = buildStart(field, start, Calendar.MONTH, i, startResolution);
                    if (subPart != null)
                    {
                        subQuery.add(subPart, Occur.SHOULD);
                    }

                }

                if (Calendar.MONTH < minResolution)
                {
                    if ((end.get(Calendar.MONTH) - start.get(Calendar.MONTH)) > 1)
                    {
                        subPart = new ConstantScoreRangeQuery(field, build2SF("MO", start.get(Calendar.MONTH)), build2SF("MO", end.get(Calendar.MONTH)), false, false);
                        subQuery.add(subPart, Occur.SHOULD);
                    }
                }
                if (Calendar.MONTH == minResolution)
                {
                    if (start.get(Calendar.MONTH) == end.get(Calendar.MONTH))
                    {
                        if (includeLower && includeUpper)
                        {
                            part = new TermQuery(new Term(field, build2SF("MO", start.get(Calendar.MONTH))));
                            query.add(part, Occur.MUST);
                        }

                        else
                        {
                            return createNoMatchQuery();
                        }
                    }
                    else
                    {
                        subPart = new ConstantScoreRangeQuery(field, build2SF("MO", start.get(Calendar.MONTH)), build2SF("MO", end.get(Calendar.MONTH)), includeLower, includeUpper);
                        subQuery.add(subPart, Occur.SHOULD);
                    }
                }

                for (int i : new int[] { Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND })
                {

                    subPart = buildEnd(field, end, Calendar.MONTH, i, endResolution);
                    if (subPart != null)
                    {
                        subQuery.add(subPart, Occur.SHOULD);
                    }

                }

                if (subQuery.clauses().size() > 0)
                {
                    query.add(subQuery, Occur.MUST);
                }
            }
        }
        else
        {
            // year + month + day + hr + min + s + ms

            BooleanQuery subQuery = new BooleanQuery();
            Query subPart;

            for (int i : new int[] { Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH, Calendar.MONTH })
            {

                subPart = buildStart(field, start, Calendar.YEAR, i, startResolution);
                if (subPart != null)
                {
                    subQuery.add(subPart, Occur.SHOULD);
                }

            }

            if (Calendar.YEAR < minResolution)
            {
                if ((end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) > 1)
                {
                    subPart = new ConstantScoreRangeQuery(field, "YE" + start.get(Calendar.YEAR), "YE" + end.get(Calendar.YEAR), false, false);
                    subQuery.add(subPart, Occur.SHOULD);
                }
            }
            if (Calendar.YEAR == minResolution)
            {
                if (start.get(Calendar.YEAR) == end.get(Calendar.YEAR))
                {
                    if (includeLower && includeUpper)
                    {
                        part = new TermQuery(new Term(field, "YE" + start.get(Calendar.YEAR)));
                        query.add(part, Occur.MUST);
                    }

                    else
                    {
                        return createNoMatchQuery();
                    }
                }
                else
                {
                    subPart = new ConstantScoreRangeQuery(field, "YE" + start.get(Calendar.YEAR), "YE" + end.get(Calendar.YEAR), includeLower, includeUpper);
                    subQuery.add(subPart, Occur.SHOULD);
                }
            }

            for (int i : new int[] { Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND })
            {

                subPart = buildEnd(field, end, Calendar.YEAR, i, endResolution);
                if (subPart != null)
                {
                    subQuery.add(subPart, Occur.SHOULD);
                }

            }

            if (subQuery.clauses().size() > 0)
            {
                query.add(subQuery, Occur.MUST);
            }
        }

        return query;
    }

    private Query buildStart(String field, Calendar cal, int startField, int padField, int resolutionField)
    {
        BooleanQuery range = new BooleanQuery();
        // only ms difference
        Query part;

        switch (startField)
        {
        case Calendar.YEAR:
            if ((cal.get(Calendar.YEAR) == 1)
                    && (cal.get(Calendar.MONTH) == 0) && (cal.get(Calendar.DAY_OF_MONTH) == 1) && (cal.get(Calendar.HOUR_OF_DAY) == 0) && (cal.get(Calendar.MINUTE) == 0)
                    && (cal.get(Calendar.SECOND) == 0) && (cal.get(Calendar.MILLISECOND) == 0))
            {
                if (padField == Calendar.MONTH)
                {
                    if (Calendar.YEAR <= resolutionField)
                    {
                        part = new TermQuery(new Term(field, "YE" + cal.get(Calendar.YEAR)));
                        range.add(part, Occur.MUST);
                    }
                    else
                    {
                        return null;
                    }

                }
                break;
            }
            else if (padField == Calendar.YEAR)
            {
                return null;
            }
            else
            {
                if (Calendar.YEAR <= resolutionField)
                {
                    part = new TermQuery(new Term(field, "YE" + cal.get(Calendar.YEAR)));
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
            }
        case Calendar.MONTH:
            if ((cal.get(Calendar.MONTH) == 0)
                    && (cal.get(Calendar.DAY_OF_MONTH) == 1) && (cal.get(Calendar.HOUR_OF_DAY) == 0) && (cal.get(Calendar.MINUTE) == 0) && (cal.get(Calendar.SECOND) == 0)
                    && (cal.get(Calendar.MILLISECOND) == 0))
            {
                if (padField == Calendar.DAY_OF_MONTH)
                {
                    if (Calendar.MONTH <= resolutionField)
                    {
                        part = new TermQuery(new Term(field, build2SF("MO", cal.get(Calendar.MONTH))));
                        range.add(part, Occur.MUST);
                    }
                    else
                    {
                        return null;
                    }

                }
                break;
            }
            else if (padField == Calendar.MONTH)
            {
                if (Calendar.MONTH < resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("MO", (cal.get(Calendar.MONTH) + 1)), "MO" + cal.getMaximum(Calendar.MONTH), true, true);
                    range.add(part, Occur.MUST);
                }
                else if (Calendar.MONTH == resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("MO", (cal.get(Calendar.MONTH))), "MO" + cal.getMaximum(Calendar.MONTH), true, true);
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
                break;
            }
            else
            {
                if (Calendar.MONTH <= resolutionField)
                {
                    part = new TermQuery(new Term(field, build2SF("MO", cal.get(Calendar.MONTH))));
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }

            }
        case Calendar.DAY_OF_MONTH:
            if ((cal.get(Calendar.DAY_OF_MONTH) == 1)
                    && (cal.get(Calendar.HOUR_OF_DAY) == 0) && (cal.get(Calendar.MINUTE) == 0) && (cal.get(Calendar.SECOND) == 0) && (cal.get(Calendar.MILLISECOND) == 0))
            {
                if (padField == Calendar.HOUR_OF_DAY)
                {
                    if (Calendar.DAY_OF_MONTH <= resolutionField)
                    {
                        part = new TermQuery(new Term(field, build2SF("DA", cal.get(Calendar.DAY_OF_MONTH))));
                        range.add(part, Occur.MUST);
                    }
                    else
                    {
                        return null;
                    }

                }
                break;
            }
            else if (padField == Calendar.DAY_OF_MONTH)
            {
                if (Calendar.DAY_OF_MONTH < resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("DA", (cal.get(Calendar.DAY_OF_MONTH) + 1)), "DA" + cal.getMaximum(Calendar.DAY_OF_MONTH), true, true);
                    range.add(part, Occur.MUST);

                }
                else if (Calendar.DAY_OF_MONTH == resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("DA", (cal.get(Calendar.DAY_OF_MONTH))), "DA" + cal.getMaximum(Calendar.DAY_OF_MONTH), true, true);
                    range.add(part, Occur.MUST);

                }
                else
                {
                    return null;
                }
                break;
            }
            else
            {
                if (Calendar.DAY_OF_MONTH <= resolutionField)
                {
                    part = new TermQuery(new Term(field, build2SF("DA", cal.get(Calendar.DAY_OF_MONTH))));
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }

            }
        case Calendar.HOUR_OF_DAY:
            if ((cal.get(Calendar.HOUR_OF_DAY) == 0) && (cal.get(Calendar.MINUTE) == 0) && (cal.get(Calendar.SECOND) == 0) && (cal.get(Calendar.MILLISECOND) == 0))
            {
                if (padField == Calendar.MINUTE)
                {
                    if (Calendar.HOUR_OF_DAY <= resolutionField)
                    {
                        part = new TermQuery(new Term(field, build2SF("HO", cal.get(Calendar.HOUR_OF_DAY))));
                        range.add(part, Occur.MUST);
                    }
                    else
                    {
                        return null;
                    }

                }
                break;
            }
            else if (padField == Calendar.HOUR_OF_DAY)
            {
                if (Calendar.HOUR_OF_DAY < resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("HO", (cal.get(Calendar.HOUR_OF_DAY) + 1)), "HO" + cal.getMaximum(Calendar.HOUR_OF_DAY), true, true);
                    range.add(part, Occur.MUST);

                }
                else if (Calendar.HOUR_OF_DAY == resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("HO", (cal.get(Calendar.HOUR_OF_DAY))), "HO" + cal.getMaximum(Calendar.HOUR_OF_DAY), true, true);
                    range.add(part, Occur.MUST);

                }
                else
                {
                    return null;
                }
                break;
            }
            else
            {
                if (Calendar.HOUR_OF_DAY <= resolutionField)
                {
                    part = new TermQuery(new Term(field, build2SF("HO", cal.get(Calendar.HOUR_OF_DAY))));
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }

            }
        case Calendar.MINUTE:
            if ((cal.get(Calendar.MINUTE) == 0) && (cal.get(Calendar.SECOND) == 0) && (cal.get(Calendar.MILLISECOND) == 0))
            {
                if (padField == Calendar.SECOND)
                {
                    if (Calendar.MINUTE <= resolutionField)
                    {
                        part = new TermQuery(new Term(field, build2SF("MI", cal.get(Calendar.MINUTE))));
                        range.add(part, Occur.MUST);
                    }
                    else
                    {
                        return null;
                    }

                }
                break;
            }
            else if (padField == Calendar.MINUTE)
            {
                if (Calendar.MINUTE < resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("MI", (cal.get(Calendar.MINUTE) + 1)), "MI" + cal.getMaximum(Calendar.MINUTE), true, true);
                    range.add(part, Occur.MUST);

                }
                else if (Calendar.MINUTE == resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("MI", (cal.get(Calendar.MINUTE))), "MI" + cal.getMaximum(Calendar.MINUTE), true, true);
                    range.add(part, Occur.MUST);

                }
                else
                {
                    return null;
                }
                break;
            }
            else
            {
                if (Calendar.MINUTE <= resolutionField)
                {
                    part = new TermQuery(new Term(field, build2SF("MI", cal.get(Calendar.MINUTE))));
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }

            }
        case Calendar.SECOND:
            if ((cal.get(Calendar.SECOND) == 0) && (cal.get(Calendar.MILLISECOND) == 0))
            {
                if (padField == Calendar.MILLISECOND)
                {
                    if (Calendar.SECOND <= resolutionField)
                    {
                        part = new TermQuery(new Term(field, build2SF("SE", cal.get(Calendar.SECOND))));
                        range.add(part, Occur.MUST);
                    }
                    else
                    {
                        return null;
                    }

                }
                break;
            }
            else if (padField == Calendar.SECOND)
            {
                if (Calendar.SECOND < resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("SE", (cal.get(Calendar.SECOND) + 1)), "SE" + cal.getMaximum(Calendar.SECOND), true, true);
                    range.add(part, Occur.MUST);

                }
                else if (Calendar.SECOND == resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("SE", (cal.get(Calendar.SECOND))), "SE" + cal.getMaximum(Calendar.SECOND), true, true);
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
                break;
            }
            else
            {
                if (Calendar.SECOND <= resolutionField)
                {
                    part = new TermQuery(new Term(field, build2SF("SE", cal.get(Calendar.SECOND))));
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }

            }
        case Calendar.MILLISECOND:
            if ((cal.get(Calendar.MILLISECOND) > 0) && (cal.get(Calendar.MILLISECOND) <= cal.getMaximum(Calendar.MILLISECOND)))
            {
                if (Calendar.MILLISECOND <= resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build3SF("MS", cal.get(Calendar.MILLISECOND)), "MS" + cal.getMaximum(Calendar.MILLISECOND), true, true);
                    range.add(part, Occur.MUST);
                }
                else
                {
                    break;
                }

            }
        default:
        }

        if (range.clauses().size() > 0)
        {
            return range;
        }
        else
        {
            return null;
        }
    }

    private Query buildEnd(String field, Calendar cal, int startField, int padField, int resolutionField)
    {
        BooleanQuery range = new BooleanQuery();
        Query part;

        switch (startField)
        {
        case Calendar.YEAR:
            if (padField == Calendar.YEAR)
            {
                if (Calendar.YEAR < resolutionField)
                {
                    if (cal.get(Calendar.YEAR) > cal.getMinimum(Calendar.YEAR))
                    {
                        part = new ConstantScoreRangeQuery(field, "YE" + cal.getMinimum(Calendar.YEAR), "YE" + (cal.get(Calendar.YEAR) - 1), true, true);
                        range.add(part, Occur.MUST);
                    }
                    else
                    {
                        return null;
                    }
                }
                else if (Calendar.YEAR == resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, "YE" + cal.getMinimum(Calendar.YEAR), "YE" + (cal.get(Calendar.YEAR)), true, true);
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
                break;
            }
            else
            {
                if (Calendar.YEAR <= resolutionField)
                {
                    part = new TermQuery(new Term(field, "YE" + cal.get(Calendar.YEAR)));
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
            }
        case Calendar.MONTH:
            if (padField == Calendar.MONTH)
            {
                if (Calendar.MONTH < resolutionField)
                {
                    if (cal.get(Calendar.MONTH) > cal.getMinimum(Calendar.MONTH))
                    {
                        part = new ConstantScoreRangeQuery(field, build2SF("MO", cal.getMinimum(Calendar.MONTH)), build2SF("MO", (cal.get(Calendar.MONTH) - 1)), true, true);
                        range.add(part, Occur.MUST);
                    }
                    else
                    {
                        return null;
                    }
                }
                else if (Calendar.MONTH == resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("MO", cal.getMinimum(Calendar.MONTH)), build2SF("MO", (cal.get(Calendar.MONTH))), true, true);
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
                break;
            }
            else
            {
                if (Calendar.MONTH <= resolutionField)
                {
                    part = new TermQuery(new Term(field, build2SF("MO", cal.get(Calendar.MONTH))));
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
            }
        case Calendar.DAY_OF_MONTH:
            if (padField == Calendar.DAY_OF_MONTH)
            {
                if (Calendar.DAY_OF_MONTH < resolutionField)
                {
                    if (cal.get(Calendar.DAY_OF_MONTH) > cal.getMinimum(Calendar.DAY_OF_MONTH))
                    {
                        part = new ConstantScoreRangeQuery(field, build2SF("DA", cal.getMinimum(Calendar.DAY_OF_MONTH)), build2SF("DA", (cal.get(Calendar.DAY_OF_MONTH) - 1)),
                                true, true);
                        range.add(part, Occur.MUST);
                    }
                    else
                    {
                        return null;
                    }
                }
                else if (Calendar.DAY_OF_MONTH == resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("DA", cal.getMinimum(Calendar.DAY_OF_MONTH)), build2SF("DA", (cal.get(Calendar.DAY_OF_MONTH))), true, true);
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
                break;
            }
            else
            {
                if (Calendar.DAY_OF_MONTH <= resolutionField)
                {
                    part = new TermQuery(new Term(field, build2SF("DA", cal.get(Calendar.DAY_OF_MONTH))));
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
            }
        case Calendar.HOUR_OF_DAY:
            if (padField == Calendar.HOUR_OF_DAY)
            {
                if (Calendar.HOUR_OF_DAY < resolutionField)
                {
                    if (cal.get(Calendar.HOUR_OF_DAY) > cal.getMinimum(Calendar.HOUR_OF_DAY))
                    {
                        part = new ConstantScoreRangeQuery(field, build2SF("HO", cal.getMinimum(Calendar.HOUR_OF_DAY)), build2SF("HO", (cal.get(Calendar.HOUR_OF_DAY) - 1)), true,
                                true);
                        range.add(part, Occur.MUST);
                    }
                    else
                    {
                        return null;
                    }

                }
                else if (Calendar.HOUR_OF_DAY == resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("HO", cal.getMinimum(Calendar.HOUR_OF_DAY)), build2SF("HO", (cal.get(Calendar.HOUR_OF_DAY))), true, true);
                    range.add(part, Occur.MUST);

                }
                else
                {
                    return null;
                }
                break;
            }
            else
            {
                if (Calendar.HOUR_OF_DAY <= resolutionField)
                {
                    part = new TermQuery(new Term(field, build2SF("HO", cal.get(Calendar.HOUR_OF_DAY))));
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
            }
        case Calendar.MINUTE:
            if (padField == Calendar.MINUTE)
            {
                if (Calendar.MINUTE < resolutionField)
                {
                    if (cal.get(Calendar.MINUTE) > cal.getMinimum(Calendar.MINUTE))
                    {
                        part = new ConstantScoreRangeQuery(field, build2SF("MI", cal.getMinimum(Calendar.MINUTE)), build2SF("MI", (cal.get(Calendar.MINUTE) - 1)), true, true);
                        range.add(part, Occur.MUST);
                    }
                    else
                    {
                        return null;
                    }
                }
                else if (Calendar.MINUTE == resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("MI", cal.getMinimum(Calendar.MINUTE)), build2SF("MI", (cal.get(Calendar.MINUTE))), true, true);
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
                break;
            }
            else
            {
                if (Calendar.MINUTE <= resolutionField)
                {
                    part = new TermQuery(new Term(field, build2SF("MI", cal.get(Calendar.MINUTE))));
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
            }
        case Calendar.SECOND:
            if (padField == Calendar.SECOND)
            {
                if (Calendar.SECOND < resolutionField)
                {
                    if (cal.get(Calendar.SECOND) > cal.getMinimum(Calendar.SECOND))
                    {
                        part = new ConstantScoreRangeQuery(field, build2SF("SE", cal.getMinimum(Calendar.SECOND)), build2SF("SE", (cal.get(Calendar.SECOND) - 1)), true, true);
                        range.add(part, Occur.MUST);
                    }
                    else
                    {
                        return null;
                    }
                }
                else if (Calendar.SECOND == resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build2SF("SE", cal.getMinimum(Calendar.SECOND)), build2SF("SE", (cal.get(Calendar.SECOND))), true, true);
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
                break;
            }

            else
            {
                if (Calendar.SECOND <= resolutionField)
                {
                    part = new TermQuery(new Term(field, build2SF("SE", cal.get(Calendar.SECOND))));
                    range.add(part, Occur.MUST);
                }
                else
                {
                    return null;
                }
            }
        case Calendar.MILLISECOND:
            if ((cal.get(Calendar.MILLISECOND) >= cal.getMinimum(Calendar.MILLISECOND)) && (cal.get(Calendar.MILLISECOND) < cal.getMaximum(Calendar.MILLISECOND)))
            {
                if (Calendar.MILLISECOND <= resolutionField)
                {
                    part = new ConstantScoreRangeQuery(field, build3SF("MS", cal.getMinimum(Calendar.MILLISECOND)), build3SF("MS", cal.get(Calendar.MILLISECOND)), true, true);
                    range.add(part, Occur.MUST);
                }
                else
                {
                    break;
                }
            }
        default:
        }

        if (range.clauses().size() > 0)
        {
            return range;
        }
        else
        {
            return null;
        }
    }

    private String build2SF(String prefix, int value)
    {
        if (value < 10)
        {
            return prefix + "0" + value;
        }
        else
        {
            return prefix + value;
        }
    }

    private String build3SF(String prefix, int value)
    {
        if (value < 10)
        {
            return prefix + "00" + value;
        }
        else if (value < 100)
        {
            return prefix + "0" + value;
        }
        else
        {
            return prefix + value;
        }
    }

    private String expandAttributeFieldName(String field)
    {
        return PROPERTY_FIELD_PREFIX + expandQName(field.substring(1));
    }

    private String expandQName(String qnameString)
    {
        String fieldName = qnameString;
        // Check for any prefixes and expand to the full uri
        if (qnameString.charAt(0) != '{')
        {
            int colonPosition = qnameString.indexOf(':');
            if (colonPosition == -1)
            {
                // use the default namespace
                fieldName = "{" + searchParameters.getNamespace() + "}" + qnameString;
            }
            else
            {
                String prefix = qnameString.substring(0, colonPosition);
                String uri = matchURI(prefix);
                if (uri == null)
                {
                    fieldName = "{" + searchParameters.getNamespace() + "}" + qnameString;
                }
                else
                {
                    fieldName = "{" + uri + "}" + qnameString.substring(colonPosition + 1);
                }

            }
        }
        return fieldName;
    }

    private String matchURI(String prefix)
    {
        HashSet<String> prefixes = new HashSet<String>(namespacePrefixResolver.getPrefixes());
        if (prefixes.contains(prefix))
        {
            return namespacePrefixResolver.getNamespaceURI(prefix);
        }
        String match = null;
        for (String candidate : prefixes)
        {
            if (candidate.equalsIgnoreCase(prefix))
            {
                if (match == null)
                {
                    match = candidate;
                }
                else
                {

                    throw new LuceneQueryParserException("Ambiguous namespace prefix " + prefix);

                }
            }
        }
        if (match == null)
        {
            return null;
        }
        else
        {
            return namespacePrefixResolver.getNamespaceURI(match);
        }
    }

    protected String getToken(String field, String value, AnalysisMode analysisMode) throws ParseException
    {
        TokenStream source = getAnalyzer().tokenStream(field, new StringReader(value), analysisMode);
        org.apache.lucene.analysis.Token reusableToken = new org.apache.lucene.analysis.Token();
        org.apache.lucene.analysis.Token nextToken;
        String tokenised = null;

        while (true)
        {
            try
            {
                nextToken = source.next(reusableToken);
            }
            catch (IOException e)
            {
                nextToken = null;
            }
            if (nextToken == null)
                break;
            tokenised = new String(nextToken.termBuffer(), 0, nextToken.termLength());
        }
        try
        {
            source.close();
        }
        catch (IOException e)
        {

        }

        return tokenised;
    }

    @Override
    public Query getPrefixQuery(String field, String termStr) throws ParseException
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
                Collection<QName> contentAttributes = dictionaryService.getAllProperties(DataTypeDefinition.CONTENT);
                BooleanQuery query = new BooleanQuery();
                for (QName qname : contentAttributes)
                {
                    // The super implementation will create phrase queries etc if required
                    Query part = getPrefixQuery(PROPERTY_FIELD_PREFIX + qname.toString(), termStr);
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
                for (String fieldName : text)
                {
                    Query part = getPrefixQuery(fieldName, termStr);
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
        else if (field.startsWith(PROPERTY_FIELD_PREFIX))
        {
            return attributeQueryBuilder(field, termStr, new PrefixQuery(), AnalysisMode.PREFIX, LuceneFunction.FIELD);
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
                    Query part = getPrefixQuery(PROPERTY_FIELD_PREFIX + qname.toString(), termStr);
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
                    Query part = getPrefixQuery(fieldName, termStr);
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
        else if (matchDataTypeDefinition(field) != null)
        {
            Collection<QName> contentAttributes = dictionaryService.getAllProperties(matchDataTypeDefinition(field).getName());
            BooleanQuery query = new BooleanQuery();
            for (QName qname : contentAttributes)
            {
                // The super implementation will create phrase queries etc if required
                Query part = getPrefixQuery(PROPERTY_FIELD_PREFIX + qname.toString(), termStr);
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

    private Query getWildcardQuery(String field, String termStr, AnalysisMode analysisMode) throws ParseException
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
                Collection<QName> contentAttributes = dictionaryService.getAllProperties(DataTypeDefinition.CONTENT);
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
        else if (field.equals(FIELD_ID)
                || field.equals(FIELD_DBID) || field.equals(FIELD_ISROOT) || field.equals(FIELD_ISCONTAINER) || field.equals(FIELD_ISNODE) || field.equals(FIELD_TX)
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
        else if (field.startsWith(PROPERTY_FIELD_PREFIX))
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
        else if (matchDataTypeDefinition(field) != null)
        {
            Collection<QName> contentAttributes = dictionaryService.getAllProperties(matchDataTypeDefinition(field).getName());
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
                Collection<QName> contentAttributes = dictionaryService.getAllProperties(DataTypeDefinition.CONTENT);
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
        else if (field.startsWith(PROPERTY_FIELD_PREFIX))
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
        else if (matchDataTypeDefinition(field) != null)
        {
            Collection<QName> contentAttributes = dictionaryService.getAllProperties(matchDataTypeDefinition(field).getName());
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
        return getFieldQueryImpl(field, queryText, analysisMode, luceneFunction);
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
            return new RegexQuery(new Term(t.field(), regexp));
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
            return new RegexQuery(new Term(prefix.field(), regexp));
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
            return getSuperPrefixQuery(field, translateLocale(termStr));
        }
    }

    class WildcardQuery implements SubQuery
    {
        public Query getQuery(String field, String termStr, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
        {
            return getSuperWildcardQuery(field, translateLocale(termStr));
        }
    }

    private Query attributeQueryBuilder(String field, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws ParseException
    {
        // TODO: Fix duplicate token generation for mltext, content and text.
        // -locale expansion here and in tokeisation -> duplicates

        // Get type info etc

        // TODO: additional suffixes
        String propertyFieldName = null;
        String ending = "";
        if (field.endsWith(FIELD_MIMETYPE_SUFFIX))
        {
            propertyFieldName = field.substring(1, field.length() - FIELD_MIMETYPE_SUFFIX.length());
            ending = FIELD_MIMETYPE_SUFFIX;
        }
        else if (field.endsWith(FIELD_SIZE_SUFFIX))
        {
            propertyFieldName = field.substring(1, field.length() - FIELD_SIZE_SUFFIX.length());
            ending = FIELD_SIZE_SUFFIX;
        }
        else if (field.endsWith(FIELD_LOCALE_SUFFIX))
        {
            propertyFieldName = field.substring(1, field.length() - FIELD_LOCALE_SUFFIX.length());
            ending = FIELD_LOCALE_SUFFIX;
        }
        else if (field.endsWith(FIELD_ENCODING_SUFFIX))
        {
            propertyFieldName = field.substring(1, field.length() - FIELD_ENCODING_SUFFIX.length());
            ending = FIELD_ENCODING_SUFFIX;
        }
        else if (field.endsWith(FIELD_CONTENT_DOC_ID_SUFFIX))
        {
            propertyFieldName = field.substring(1, field.length() - FIELD_CONTENT_DOC_ID_SUFFIX.length());
            ending = FIELD_CONTENT_DOC_ID_SUFFIX;
        }
        else if (field.endsWith(FIELD_TRANSFORMATION_EXCEPTION_SUFFIX))
        {
            propertyFieldName = field.substring(1, field.length() - FIELD_TRANSFORMATION_EXCEPTION_SUFFIX.length());
            ending = FIELD_TRANSFORMATION_EXCEPTION_SUFFIX;
        }
        else if (field.endsWith(FIELD_TRANSFORMATION_TIME_SUFFIX))
        {
            propertyFieldName = field.substring(1, field.length() - FIELD_TRANSFORMATION_TIME_SUFFIX.length());
            ending = FIELD_TRANSFORMATION_TIME_SUFFIX;
        }
        else if (field.endsWith(FIELD_TRANSFORMATION_STATUS_SUFFIX))
        {
            propertyFieldName = field.substring(1, field.length() - FIELD_TRANSFORMATION_STATUS_SUFFIX.length());
            ending = FIELD_TRANSFORMATION_STATUS_SUFFIX;
        }
        else
        {
            propertyFieldName = field.substring(1);
        }

        String expandedFieldName;
        QName propertyQName;
        PropertyDefinition propertyDef = matchPropertyDefinition(propertyFieldName);
        IndexTokenisationMode tokenisationMode = IndexTokenisationMode.TRUE;
        if (propertyDef != null)
        {
            tokenisationMode = propertyDef.getIndexTokenisationMode();
            if (tokenisationMode == null)
            {
                tokenisationMode = IndexTokenisationMode.TRUE;
            }
            expandedFieldName = PROPERTY_FIELD_PREFIX + propertyDef.getName() + ending;
            propertyQName = propertyDef.getName();
        }
        else
        {
            expandedFieldName = expandAttributeFieldName(field);
            propertyQName = QName.createQName(propertyFieldName);
        }

        if (luceneFunction != LuceneFunction.FIELD)
        {
            if ((tokenisationMode == IndexTokenisationMode.FALSE) || (tokenisationMode == IndexTokenisationMode.BOTH))
            {
                return functionQueryBuilder(expandedFieldName, propertyQName, propertyDef, tokenisationMode, queryText, luceneFunction);
            }
        }

        // Mime type
        if (expandedFieldName.endsWith(FIELD_MIMETYPE_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                return subQueryBuilder.getQuery(expandedFieldName, queryText, analysisMode, luceneFunction);
            }

        }
        else if (expandedFieldName.endsWith(FIELD_SIZE_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                return subQueryBuilder.getQuery(expandedFieldName, queryText, analysisMode, luceneFunction);
            }

        }
        else if (expandedFieldName.endsWith(FIELD_LOCALE_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                return subQueryBuilder.getQuery(expandedFieldName, queryText, analysisMode, luceneFunction);
            }

        }

        // Already in expanded form

        // ML

        if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.MLTEXT)))
        {
            // Build a sub query for each locale and or the results together - the analysis will take care of
            // cross language matching for each entry
            BooleanQuery booleanQuery = new BooleanQuery();
            MLAnalysisMode mlAnalysisMode = searchParameters.getMlAnalaysisMode() == null ? defaultSearchMLAnalysisMode : searchParameters.getMlAnalaysisMode();
            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, false));
            }
            for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
            {
                addMLTextAttributeQuery(field, queryText, subQueryBuilder, analysisMode, luceneFunction, expandedFieldName, propertyDef, tokenisationMode, booleanQuery,
                        mlAnalysisMode, locale);
            }
            return booleanQuery;
        }
        // Content
        else if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
        {
            // Identifier request are ignored for content

            // Build a sub query for each locale and or the results together -
            // - add an explicit condition for the locale

            MLAnalysisMode mlAnalysisMode = searchParameters.getMlAnalaysisMode() == null ? defaultSearchMLAnalysisMode : searchParameters.getMlAnalaysisMode();



            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, true));
            }

            return addContentAttributeQuery(queryText, subQueryBuilder, analysisMode, luceneFunction, expandedFieldName, expandedLocales, mlAnalysisMode);

        }
        else if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.TEXT)))
        {
            if (propertyQName.equals(ContentModel.PROP_USER_USERNAME) || propertyQName.equals(ContentModel.PROP_USERNAME) || propertyQName.equals(ContentModel.PROP_AUTHORITY_NAME))
            {
                // nasty work around for solr support for user and group look up as we can not support lowercased identifiers ion the model
                if(isLucene())
                {
                   return subQueryBuilder.getQuery(expandedFieldName, queryText, analysisMode, luceneFunction);
                }
            }

            BooleanQuery booleanQuery = new BooleanQuery();
            MLAnalysisMode mlAnalysisMode = searchParameters.getMlAnalaysisMode() == null ? defaultSearchMLAnalysisMode : searchParameters.getMlAnalaysisMode();
            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, false));
            }
            for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
            {

                addTextAttributeQuery(field, queryText, subQueryBuilder, analysisMode, luceneFunction, expandedFieldName, tokenisationMode, booleanQuery, mlAnalysisMode, locale);

            }
            return booleanQuery;
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

            if ((propertyDef != null)
                    && (tenantService.isTenantUser()) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.NODE_REF)) && (queryText.contains(StoreRef.URI_FILLER)))
            {
                // ALF-6202
                queryText = tenantService.getName(new NodeRef(queryText)).toString();
            }

            // Sort and id is only special for MLText, text, and content
            // Dates are not special in this case
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

    /**
     * @return
     */
    protected abstract boolean isLucene();

    /**
     * @param field
     * @param queryText
     * @param subQueryBuilder
     * @param analysisMode
     * @param luceneFunction
     * @param expandedFieldName
     * @param tokenisationMode
     * @param booleanQuery
     * @param mlAnalysisMode
     * @param locale
     * @param textFieldName
     * @throws ParseException
     */
    protected abstract void addTextAttributeQuery(String field, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            String expandedFieldName, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale) throws ParseException;

    /**
     * @param queryText
     * @param subQueryBuilder
     * @param analysisMode
     * @param luceneFunction
     * @param expandedFieldName
     * @param expandedLocales
     * @return
     * @throws ParseException
     */
    protected abstract Query addContentAttributeQuery(String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            String expandedFieldName, List<Locale> expandedLocales, MLAnalysisMode mlAnalysisMode) throws ParseException;

    /**
     * @param field
     * @param queryText
     * @param subQueryBuilder
     * @param analysisMode
     * @param luceneFunction
     * @param expandedFieldName
     * @param propertyDef
     * @param tokenisationMode
     * @param booleanQuery
     * @param mlAnalysisMode
     * @param locale
     * @throws ParseException
     */
    protected abstract void addMLTextAttributeQuery(String field, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            String expandedFieldName, PropertyDefinition propertyDef, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode,
            Locale locale) throws ParseException;

//    private void addLocaleSpecificUntokenisedMLOrTextAttribute(String sourceField, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode,
//            LuceneFunction luceneFunction, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale, String actualField) throws ParseException
//    {
//
//        String termText = queryText;
//        if (locale.toString().length() > 0)
//        {
//            termText = "{" + locale + "}" + queryText;
//        }
//        Query subQuery = subQueryBuilder.getQuery(actualField, termText, analysisMode, luceneFunction);
//        booleanQuery.add(subQuery, Occur.SHOULD);
//
//        if (booleanQuery.getClauses().length == 0)
//        {
//            booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
//        }
//    }

//    private void addLocaleSpecificTokenisedMLOrTextAttribute(String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
//            BooleanQuery booleanQuery, Locale locale, String actualField) throws ParseException
//    {
//        StringBuilder builder = new StringBuilder(queryText.length() + 10);
//        builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
//        Query subQuery = subQueryBuilder.getQuery(actualField, builder.toString(), analysisMode, luceneFunction);
//        if (subQuery != null)
//        {
//            booleanQuery.add(subQuery, Occur.SHOULD);
//        }
//        else
//        {
//            booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
//        }
//    }

    protected Query functionQueryBuilder(String expandedFieldName, QName propertyQName, PropertyDefinition propertyDef, IndexTokenisationMode tokenisationMode, String queryText,
            LuceneFunction luceneFunction) throws ParseException
    {

        // Mime type
        if (expandedFieldName.endsWith(FIELD_MIMETYPE_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (expandedFieldName.endsWith(FIELD_SIZE_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (expandedFieldName.endsWith(FIELD_LOCALE_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (expandedFieldName.endsWith(FIELD_ENCODING_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (expandedFieldName.endsWith(FIELD_CONTENT_DOC_ID_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (expandedFieldName.endsWith(FIELD_TRANSFORMATION_EXCEPTION_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (expandedFieldName.endsWith(FIELD_TRANSFORMATION_TIME_SUFFIX))
        {
            if ((propertyDef != null) && (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT)))
            {
                throw new UnsupportedOperationException("Lucene Function");
            }

        }
        else if (expandedFieldName.endsWith(FIELD_TRANSFORMATION_STATUS_SUFFIX))
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
            MLAnalysisMode mlAnalysisMode = searchParameters.getMlAnalaysisMode() == null ? defaultSearchMLAnalysisMode : searchParameters.getMlAnalaysisMode();
            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, false));
            }
            for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
            {

                addLocaleSpecificUntokenisedMLOrTextFunction(expandedFieldName, queryText, luceneFunction, booleanQuery, mlAnalysisMode, locale, tokenisationMode);

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

            BooleanQuery booleanQuery = new BooleanQuery();
            MLAnalysisMode mlAnalysisMode = searchParameters.getMlAnalaysisMode() == null ? defaultSearchMLAnalysisMode : searchParameters.getMlAnalaysisMode();
            List<Locale> locales = searchParameters.getLocales();
            List<Locale> expandedLocales = new ArrayList<Locale>();
            for (Locale locale : (((locales == null) || (locales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : locales))
            {
                expandedLocales.addAll(MLAnalysisMode.getLocales(mlAnalysisMode, locale, false));
            }
            for (Locale locale : (((expandedLocales == null) || (expandedLocales.size() == 0)) ? Collections.singletonList(I18NUtil.getLocale()) : expandedLocales))
            {
                addLocaleSpecificUntokenisedMLOrTextFunction(expandedFieldName, queryText, luceneFunction, booleanQuery, mlAnalysisMode, locale, tokenisationMode);

            }
            return booleanQuery;
        }
        else
        {
            throw new UnsupportedOperationException("Lucene Function");
        }
    }

    protected abstract void addLocaleSpecificUntokenisedMLOrTextFunction(String expandedFieldName, String queryText, LuceneFunction luceneFunction, BooleanQuery booleanQuery,
            MLAnalysisMode mlAnalysisMode, Locale locale, IndexTokenisationMode tokenisationMode);

    // private Query buildFunctionQuery(String expandedFieldName, String termText, LuceneFunction luceneFunction)
    // {
    // String testText = termText;
    // if (termText.startsWith("{"))
    // {
    // int index = termText.indexOf("}");
    // testText = termText.substring(index + 1);
    // }
    // switch (luceneFunction)
    // {
    // case LOWER:
    // if (testText.equals(testText.toLowerCase()))
    // {
    // return new CaseInsensitiveFieldQuery(new Term(expandedFieldName, termText));
    // }
    // else
    // {
    // // No match
    // return createNoMatchQuery();
    // }
    // case UPPER:
    // if (testText.equals(testText.toUpperCase()))
    // {
    // return new CaseInsensitiveFieldQuery(new Term(expandedFieldName, termText));
    // }
    // else
    // {
    // // No match
    // return createNoMatchQuery();
    // }
    // default:
    // throw new UnsupportedOperationException("Unsupported Lucene Function " + luceneFunction);
    //
    // }
    // }

    protected TermQuery createNoMatchQuery()
    {
        return new TermQuery(new Term("NO_TOKENS", "__"));
    }

    public static void main(String[] args) throws ParseException, java.text.ParseException
    {
        //Query query;

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        SimpleDateFormat df = CachingDateFormat.getDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", false);

        Date date = df.parse("2007-11-30T22:58:58.998");
        System.out.println(date);
        start.setTime(date);
        System.out.println(start);

        date = df.parse("2008-01-01T03:00:01.002");
        System.out.println(date);
        end.setTime(date);
        System.out.println(end);

        // start.set(Calendar.YEAR, start.getMinimum(Calendar.YEAR));
        // start.set(Calendar.DAY_OF_YEAR, start.getMinimum(Calendar.DAY_OF_YEAR));
        // start.set(Calendar.HOUR_OF_DAY, start.getMinimum(Calendar.HOUR_OF_DAY));
        // start.set(Calendar.MINUTE, start.getMinimum(Calendar.MINUTE));
        // start.set(Calendar.SECOND, start.getMinimum(Calendar.SECOND));
        // start.set(Calendar.MILLISECOND, start.getMinimum(Calendar.MILLISECOND));
        // LuceneQueryParser lqp = new LuceneQueryParser(null, null);
        // query = lqp.buildDateTimeRange("TEST", start, end, false, false);
        // System.out.println("Query is " + query);
    }

    @Override
    public AbstractAnalyzer getAnalyzer()
    {
        return luceneAnalyser;
    }

}
