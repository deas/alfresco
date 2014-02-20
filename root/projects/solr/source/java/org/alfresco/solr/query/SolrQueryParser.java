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

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Locale;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.query.CaseInsensitiveFieldQuery;
import org.alfresco.repo.search.impl.lucene.query.CaseInsensitiveFieldRangeQuery;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.CharStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParserTokenManager;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.base.XPathReader;

/**
 * @author Andy
 */
public class SolrQueryParser extends AbstractLuceneQueryParser
{

    @Override
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

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#createDbidQuery(java.lang.String)
     */
    @Override
    protected Query createDbidQuery(String queryText) throws ParseException
    {
        Query query = super.createDbidQuery(queryText);
        return new SolrCachingAuxDocQuery(query);
    }

    @Override
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
        // This was broken and using only FIELD_PRIMARYASSOCTYPEQNAME
        // The field was also not indexed correctly.
        // We do both for backward compatability ...
        BooleanQuery booleanQuery = new BooleanQuery();

        XPathReader reader = new XPathReader();
        SolrXPathHandler handler = new SolrXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse("//" + queryText);
        SolrPathQuery pathQuery = handler.getQuery();
        pathQuery.setPathField(FIELD_ASSOCTYPEQNAME);

        booleanQuery.add(new SolrCachingPathQuery(pathQuery), Occur.SHOULD);
        booleanQuery.add(createPrimaryAssocTypeQNameQuery(queryText), Occur.SHOULD);

        return getNonEmptyBooleanQuery(booleanQuery);
    }

    /**
     * @param queryText
     * @return
     */
    protected Query createAclIdQuery(String queryText) throws ParseException
    {
        return getFieldQueryImpl(FIELD_ACLID, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }

    /**
     * @param queryText
     * @return
     */
    protected Query createOwnerQuery(String queryText) throws ParseException
    {
        return new SolrCachingOwnerQuery(queryText);
    }

    /**
     * @param queryText
     * @return
     */
    protected Query createReaderQuery(String queryText) throws ParseException
    {
        return new SolrCachingReaderQuery(queryText);
    }

    /**
     * @param queryText
     * @return
     */
    protected Query createAuthorityQuery(String queryText) throws ParseException
    {
        return new SolrCachingAuthorityQuery(queryText);
    }

    protected Query createParentQuery(String queryText)
    {
        Query query = super.createParentQuery(queryText);
        return new SolrCachingAuxDocQuery(query);
    }

    protected Query createPrimaryParentQuery(String queryText)
    {
        Query query = super.createPrimaryParentQuery(queryText);
        return new SolrCachingAuxDocQuery(query);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public SolrQueryParser(String arg0, Analyzer arg1)
    {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public SolrQueryParser(CharStream arg0)
    {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public SolrQueryParser(QueryParserTokenManager arg0)
    {
        super(arg0);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addContentAttributeQuery(java.lang.String,
     * org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser.SubQuery,
     * org.alfresco.repo.search.impl.lucene.AnalysisMode, org.alfresco.repo.search.impl.lucene.LuceneFunction,
     * java.lang.String, java.util.List)
     */
    @Override
    protected Query addContentAttributeQuery(String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction, String expandedFieldName,
            List<Locale> expandedLocales, MLAnalysisMode mlAnalysisMode) throws ParseException
    {
        BooleanQuery booleanQuery = new BooleanQuery();
        for (Locale locale : expandedLocales)
        {
            if (locale.toString().length() == 0)
            {
                StringBuilder builder = new StringBuilder(queryText.length() + 10);
                builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
                Query subQuery = subQueryBuilder.getQuery(expandedFieldName + ".__", builder.toString(), analysisMode, luceneFunction);
                if (subQuery != null)
                {
                    booleanQuery.add(subQuery, Occur.SHOULD);
                }

            }
            else
            {
                StringBuilder builder = new StringBuilder(queryText.length() + 10);
                builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
                Query subQuery = subQueryBuilder.getQuery(expandedFieldName, builder.toString(), analysisMode, luceneFunction);
                if (subQuery != null)
                {
                    booleanQuery.add(subQuery, Occur.SHOULD);
                }
            }
        }
        return getNonEmptyBooleanQuery(booleanQuery);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addLocaleSpecificUntokenisedMLOrTextFunction(java
     * .lang.String, java.lang.String, org.alfresco.repo.search.impl.lucene.LuceneFunction,
     * org.apache.lucene.search.BooleanQuery, org.alfresco.repo.search.MLAnalysisMode, java.util.Locale,
     * org.alfresco.repo.dictionary.IndexTokenisationMode)
     */
    @Override
    protected void addLocaleSpecificUntokenisedMLOrTextFunction(String expandedFieldName, String queryText, LuceneFunction luceneFunction, BooleanQuery booleanQuery,
            MLAnalysisMode mlAnalysisMode, Locale locale, IndexTokenisationMode tokenisationMode)
    {
        Query subQuery = new CaseInsensitiveFieldQuery(new Term(getFieldName(expandedFieldName, locale, tokenisationMode, IndexTokenisationMode.FALSE), getFixedFunctionQueryText(
                queryText, locale, tokenisationMode, IndexTokenisationMode.FALSE)));
        booleanQuery.add(subQuery, Occur.SHOULD);

        if (booleanQuery.getClauses().length == 0)
        {
            booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
        }
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

    private String getFieldName(String baseFieldName, Locale locale, IndexTokenisationMode actualIndexTokenisationMode, IndexTokenisationMode preferredIndexTokenisationMode)
    {
        StringBuilder builder = new StringBuilder(baseFieldName.length() + 5);
        builder.append(baseFieldName);
        if (locale.toString().length() == 0)
        {
            builder.append(".__");
        }
        switch (actualIndexTokenisationMode)
        {
        case BOTH:
            switch (preferredIndexTokenisationMode)
            {
            case BOTH:
                throw new IllegalStateException("Preferred mode can not be BOTH");
            case FALSE:
                builder.append(".u");
                break;
            case TRUE:
                // nothing to do
                break;
            }
            break;
        case FALSE:
            builder.append(".u");
            break;
        case TRUE:
            // nothing to do
            break;
        }
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addLocaleSpecificUntokenisedTextRangeFunction(
     * java.lang.String, java.lang.String, java.lang.String, boolean, boolean,
     * org.alfresco.repo.search.impl.lucene.LuceneFunction, org.apache.lucene.search.BooleanQuery,
     * org.alfresco.repo.search.MLAnalysisMode, java.util.Locale, org.alfresco.repo.dictionary.IndexTokenisationMode)
     */
    @Override
    protected void addLocaleSpecificUntokenisedTextRangeFunction(String expandedFieldName, String lower, String upper, boolean includeLower, boolean includeUpper,
            LuceneFunction luceneFunction, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale, IndexTokenisationMode tokenisationMode) throws ParseException
    {
        String field = getFieldName(expandedFieldName, locale, tokenisationMode, IndexTokenisationMode.FALSE);

        StringBuilder builder = new StringBuilder();
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(lower);
        String first = getToken(field, builder.toString(), AnalysisMode.IDENTIFIER);

        builder = new StringBuilder();
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(upper);
        String last = getToken(field, builder.toString(), AnalysisMode.IDENTIFIER);

        Query query = new CaseInsensitiveFieldRangeQuery(field, first, last, includeLower, includeUpper);
        booleanQuery.add(query, Occur.SHOULD);

    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addMLTextAttributeQuery(java.lang.String,
     * java.lang.String, org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser.SubQuery,
     * org.alfresco.repo.search.impl.lucene.AnalysisMode, org.alfresco.repo.search.impl.lucene.LuceneFunction,
     * java.lang.String, org.alfresco.service.cmr.dictionary.PropertyDefinition,
     * org.alfresco.repo.dictionary.IndexTokenisationMode, org.apache.lucene.search.BooleanQuery,
     * org.alfresco.repo.search.MLAnalysisMode, java.util.Locale)
     */
    @Override
    protected void addMLTextAttributeQuery(String field, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            String expandedFieldName, PropertyDefinition propertyDef, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode,
            Locale locale) throws ParseException
    {

        addMLTextOrTextAttributeQuery(field, queryText, subQueryBuilder, analysisMode, luceneFunction, expandedFieldName, tokenisationMode, booleanQuery, mlAnalysisMode, locale);
    }

    private void addMLTextOrTextAttributeQuery(String field, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
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
                    addLocaleSpecificMLOrTextAttribute(queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, expandedFieldName, tokenisationMode,
                            IndexTokenisationMode.TRUE);
                    break;
                case IDENTIFIER:
                case FUZZY:
                case PREFIX:
                case WILD:
                case LIKE:
                    setLowercaseExpandedTerms(false);
                    addLocaleSpecificMLOrTextAttribute(queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, expandedFieldName, tokenisationMode,
                            IndexTokenisationMode.FALSE);

                    break;
                }
                break;
            case FALSE:
                setLowercaseExpandedTerms(false);
                addLocaleSpecificMLOrTextAttribute(queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, expandedFieldName, tokenisationMode,
                        IndexTokenisationMode.FALSE);
                break;
            case TRUE:
            default:
                addLocaleSpecificMLOrTextAttribute(queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, expandedFieldName, tokenisationMode,
                        IndexTokenisationMode.TRUE);
                break;
            }
        }
        finally
        {
            setLowercaseExpandedTerms(lowercaseExpandedTerms);
        }

    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addTextAttributeQuery(java.lang.String,
     * java.lang.String, org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser.SubQuery,
     * org.alfresco.repo.search.impl.lucene.AnalysisMode, org.alfresco.repo.search.impl.lucene.LuceneFunction,
     * java.lang.String, org.alfresco.repo.dictionary.IndexTokenisationMode, org.apache.lucene.search.BooleanQuery,
     * org.alfresco.repo.search.MLAnalysisMode, java.util.Locale)
     */
    @Override
    protected void addTextAttributeQuery(String field, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            String expandedFieldName, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale) throws ParseException
    {

        addMLTextOrTextAttributeQuery(field, queryText, subQueryBuilder, analysisMode, luceneFunction, expandedFieldName, tokenisationMode, booleanQuery, mlAnalysisMode, locale);
    }

    /**
     * @param field
     * @param queryText
     * @param subQueryBuilder
     * @param analysisMode
     * @param luceneFunction
     * @param booleanQuery
     * @param mlAnalysisMode
     * @param locale
     * @param textFieldName
     * @throws ParseException
     */
    private void addLocaleSpecificMLOrTextAttribute(String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            BooleanQuery booleanQuery, Locale locale, String textFieldName, IndexTokenisationMode tokenisationMode, IndexTokenisationMode preferredTokenisationMode)
            throws ParseException
    {

        StringBuilder builder = new StringBuilder(queryText.length() + 10);
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
        Query subQuery = subQueryBuilder.getQuery(getFieldName(textFieldName, locale, tokenisationMode, preferredTokenisationMode), builder.toString(), analysisMode,
                luceneFunction);
        if (subQuery != null)
        {
            booleanQuery.add(subQuery, Occur.SHOULD);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addTextRange(java.lang.String,
     * java.lang.String, java.lang.String, boolean, boolean, org.alfresco.repo.search.impl.lucene.AnalysisMode,
     * java.lang.String, org.alfresco.service.cmr.dictionary.PropertyDefinition,
     * org.alfresco.repo.dictionary.IndexTokenisationMode, org.apache.lucene.search.BooleanQuery,
     * org.alfresco.repo.search.MLAnalysisMode, java.util.Locale)
     */
    @Override
    protected void addTextRange(String field, String part1, String part2, boolean includeLower, boolean includeUpper, AnalysisMode analysisMode, String fieldName,
            PropertyDefinition propertyDef, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale) throws ParseException
    {
        switch (tokenisationMode)
        {
        case BOTH:
            switch (analysisMode)
            {
            case DEFAULT:
            case TOKENISE:
                addLocaleSpecificTextRange(fieldName, part1, part2, includeLower, includeUpper, booleanQuery, locale, analysisMode, tokenisationMode, IndexTokenisationMode.TRUE);
                break;
            case IDENTIFIER:
                addLocaleSpecificTextRange(fieldName, part1, part2, includeLower, includeUpper, booleanQuery, locale, analysisMode, tokenisationMode, IndexTokenisationMode.FALSE);
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
            addLocaleSpecificTextRange(fieldName, part1, part2, includeLower, includeUpper, booleanQuery, locale, analysisMode, tokenisationMode, IndexTokenisationMode.FALSE);
            break;
        case TRUE:
            addLocaleSpecificTextRange(fieldName, part1, part2, includeLower, includeUpper, booleanQuery, locale, analysisMode, tokenisationMode, IndexTokenisationMode.TRUE);
            break;
        default:
        }

    }

    private void addLocaleSpecificTextRange(String expandedFieldName, String part1, String part2, boolean includeLower, boolean includeUpper, BooleanQuery booleanQuery,
            Locale locale, AnalysisMode analysisMode, IndexTokenisationMode tokenisationMode, IndexTokenisationMode preferredtokenisationMode) throws ParseException
    {
        String field = getFieldName(expandedFieldName, locale, tokenisationMode, preferredtokenisationMode);
        StringBuilder builder = new StringBuilder();
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(part1);
        String first = getToken(field, builder.toString(), analysisMode);
        if ((first == null) && (false == field.endsWith(".u")))
        {
            first = getToken(field + ".u", builder.toString(), analysisMode);
        }

        builder = new StringBuilder();
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(part2);
        String last = getToken(field, builder.toString(), analysisMode);
        if ((last == null) && (false == field.endsWith(".u")))
        {
            last = getToken(field + ".u", builder.toString(), analysisMode);
        }

        Query query = new TermRangeQuery(field, first, last, includeLower, includeUpper);
        booleanQuery.add(query, Occur.SHOULD);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#isLucene()
     */
    @Override
    protected boolean isLucene()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#createAncestorQuery(java.lang.String)
     */
    protected Query createAncestorQuery(String queryText) throws ParseException
    {
        Query query = super.createAncestorQuery(queryText);
        return new SolrCachingAuxDocQuery(query);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addTextSpanQuery(java.lang.String,
     * java.lang.String, java.lang.String, int, boolean, java.lang.String,
     * org.alfresco.repo.dictionary.IndexTokenisationMode, org.apache.lucene.search.BooleanQuery,
     * org.alfresco.repo.search.MLAnalysisMode, java.util.Locale)
     */
    @Override
    protected void addTextSpanQuery(String field, String first, String last, int slop, boolean inOrder, String expandedFieldName, IndexTokenisationMode tokenisationMode,
            BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale)
    {
        addMLTextOrTextSpanQuery(field, first, last, slop, inOrder, expandedFieldName, tokenisationMode, booleanQuery, mlAnalysisMode, locale);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addContentSpanQuery(java.lang.String,
     * java.lang.String, java.lang.String, int, boolean, java.lang.String, java.util.List,
     * org.alfresco.repo.search.MLAnalysisMode)
     */
    @Override
    protected org.apache.lucene.search.Query addContentSpanQuery(String field, String first, String last, int slop, boolean inOrder, String expandedFieldName,
            List<Locale> expandedLocales, MLAnalysisMode mlAnalysisMode)
    {
        try
        {
            BooleanQuery booleanQuery = new BooleanQuery();
            for (Locale locale : expandedLocales)
            {
                if (locale.toString().length() == 0)
                {
                    StringBuilder builder = new StringBuilder(first.length() + 10);
                    builder.append("\u0000").append(locale.toString()).append("\u0000").append(first);
                    TokenStream source = getAnalyzer().tokenStream(expandedFieldName + ".__", new StringReader(builder.toString()), AnalysisMode.TOKENISE);

                    org.apache.lucene.analysis.Token reusableToken = new org.apache.lucene.analysis.Token();
                    org.apache.lucene.analysis.Token nextToken;

                    nextToken = source.next(reusableToken);
                    SpanQuery firstTerm = new SpanTermQuery(new Term(expandedFieldName + ".__", nextToken.term()));
                    if (source.next(reusableToken) != null)
                    {
                        throw new AlfrescoRuntimeException("Found extra token in span query: " + first);
                    }

                    builder = new StringBuilder(last.length() + 10);
                    builder.append("\u0000").append(locale.toString()).append("\u0000").append(last);
                    source = getAnalyzer().tokenStream(expandedFieldName + ".__", new StringReader(builder.toString()), AnalysisMode.TOKENISE);

                    nextToken = source.next(reusableToken);
                    SpanQuery lastTerm = new SpanTermQuery(new Term(expandedFieldName + ".__", nextToken.term()));
                    if (source.next(reusableToken) != null)
                    {
                        throw new AlfrescoRuntimeException("Found extra token in span query: " + last);
                    }

                    SpanNearQuery result = new SpanNearQuery(new SpanQuery[] { firstTerm, lastTerm }, slop, inOrder);
                    booleanQuery.add(result, Occur.SHOULD);

                }
                else
                {
                    StringBuilder builder = new StringBuilder(first.length() + 10);
                    builder.append("\u0000").append(locale.toString()).append("\u0000").append(first);
                    TokenStream source = getAnalyzer().tokenStream(expandedFieldName, new StringReader(builder.toString()), AnalysisMode.TOKENISE);

                    org.apache.lucene.analysis.Token reusableToken = new org.apache.lucene.analysis.Token();
                    org.apache.lucene.analysis.Token nextToken;

                    nextToken = source.next(reusableToken);
                    SpanQuery firstTerm = new SpanTermQuery(new Term(expandedFieldName, nextToken.term()));
                    if (source.next(reusableToken) != null)
                    {
                        throw new AlfrescoRuntimeException("Found extra token in span query: " + first);
                    }

                    builder = new StringBuilder(last.length() + 10);
                    builder.append("\u0000").append(locale.toString()).append("\u0000").append(last);
                    source = getAnalyzer().tokenStream(expandedFieldName, new StringReader(builder.toString()), AnalysisMode.TOKENISE);

                    nextToken = source.next(reusableToken);
                    SpanQuery lastTerm = new SpanTermQuery(new Term(expandedFieldName, nextToken.term()));
                    if (source.next(reusableToken) != null)
                    {
                        throw new AlfrescoRuntimeException("Found extra token in span query: " + last);
                    }

                    SpanNearQuery result = new SpanNearQuery(new SpanQuery[] { firstTerm, lastTerm }, slop, inOrder);
                    booleanQuery.add(result, Occur.SHOULD);
                }
            }
            return getNonEmptyBooleanQuery(booleanQuery);
        }
        catch (IOException ioe)
        {
            return createNoMatchQuery();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addMLTextSpanQuery(java.lang.String,
     * java.lang.String, java.lang.String, int, boolean, java.lang.String,
     * org.alfresco.service.cmr.dictionary.PropertyDefinition, org.alfresco.repo.dictionary.IndexTokenisationMode,
     * org.apache.lucene.search.BooleanQuery, org.alfresco.repo.search.MLAnalysisMode, java.util.Locale)
     */
    @Override
    protected void addMLTextSpanQuery(String field, String first, String last, int slop, boolean inOrder, String expandedFieldName, PropertyDefinition propertyDef,
            IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale)
    {
        addMLTextOrTextSpanQuery(field, first, last, slop, inOrder, expandedFieldName, tokenisationMode, booleanQuery, mlAnalysisMode, locale);
    }

    private void addMLTextOrTextSpanQuery(String field, String first, String last, int slop, boolean inOrder, String expandedFieldName, IndexTokenisationMode tokenisationMode,
            BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale)
    {
        try
        {
            String fieldName = getFieldName(expandedFieldName, locale, tokenisationMode, IndexTokenisationMode.TRUE);

            StringBuilder builder = new StringBuilder(first.length() + 10);
            builder.append("\u0000").append(locale.toString()).append("\u0000").append(first);
            TokenStream source = getAnalyzer().tokenStream(fieldName, new StringReader(builder.toString()), AnalysisMode.TOKENISE);

            org.apache.lucene.analysis.Token reusableToken = new org.apache.lucene.analysis.Token();
            org.apache.lucene.analysis.Token nextToken;

            nextToken = source.next(reusableToken);
            SpanQuery firstTerm = new SpanTermQuery(new Term(fieldName, nextToken.term()));
            if (source.next(reusableToken) != null)
            {
                throw new AlfrescoRuntimeException("Found extra token in span query: " + first);
            }

            builder = new StringBuilder(last.length() + 10);
            builder.append("\u0000").append(locale.toString()).append("\u0000").append(last);
            source = getAnalyzer().tokenStream(fieldName, new StringReader(builder.toString()), AnalysisMode.TOKENISE);

            nextToken = source.next(reusableToken);
            SpanQuery lastTerm = new SpanTermQuery(new Term(fieldName, nextToken.term()));
            if (source.next(reusableToken) != null)
            {
                throw new AlfrescoRuntimeException("Found extra token in span query: " + last);
            }

            SpanNearQuery result = new SpanNearQuery(new SpanQuery[] { firstTerm, lastTerm }, slop, inOrder);
            booleanQuery.add(result, Occur.SHOULD);
        }
        catch (IOException ioe)
        {
            booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
        }

    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addContentCrossLocaleWildcards()
     */
    @Override
    public boolean addContentCrossLocaleWildcards()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#createOwnerSetQuery(java.lang.String)
     */
    @Override
    protected org.apache.lucene.search.Query createOwnerSetQuery(String queryText) throws ParseException
    {
        return new SolrOwnerSetQuery(queryText);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#createReaderSetQuery(java.lang.String)
     */
    @Override
    protected org.apache.lucene.search.Query createReaderSetQuery(String queryText) throws ParseException
    {
        return new SolrReaderSetQuery(queryText);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#createAuthoritySetQuery(java.lang.String)
     */
    @Override
    protected org.apache.lucene.search.Query createAuthoritySetQuery(String queryText) throws ParseException
    {
        return new SolrAuthoritySetQuery(queryText);
    }

}
