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

import java.util.List;
import java.util.Locale;

import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.query.CaseInsensitiveFieldQuery;
import org.alfresco.repo.search.impl.lucene.query.CaseInsensitiveFieldRangeQuery;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.CharStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParserTokenManager;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.saxpath.SAXPathException;

import com.werken.saxpath.XPathReader;

/**
 * @author Andy
 */
public class SolrQueryParser extends AbstractLuceneQueryParser
{

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
        return handler.getQuery();
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
        return pathQuery;
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

        booleanQuery.add(pathQuery, Occur.SHOULD);
        booleanQuery.add(createPrimaryAssocTypeQNameQuery(queryText), Occur.SHOULD);

        return booleanQuery;
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

    /**
     * @param arg0
     * @param arg1
     */
    public SolrQueryParser(String arg0, Analyzer arg1)
    {
        super(arg0, arg1);    }

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
                else
                {
                    booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
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
                else
                {
                    booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
                }
            }
        }
        return booleanQuery;

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
        if (locale.toString().length() == 0)
        {
            StringBuilder builder = new StringBuilder(queryText.length() + 10);
            builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
            Query subQuery = new CaseInsensitiveFieldQuery(new Term(expandedFieldName + ".__.u",  builder.toString()));
            if (subQuery != null)
            {
                booleanQuery.add(subQuery, Occur.SHOULD);
            }
            else
            {
                booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
            }
        }
        else
        {
            StringBuilder builder = new StringBuilder(queryText.length() + 10);
            builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
            Query subQuery = new CaseInsensitiveFieldQuery(new Term(expandedFieldName + ".u",  builder.toString()));
            if (subQuery != null)
            {
                booleanQuery.add(subQuery, Occur.SHOULD);
            }
            else
            {
                booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
            }
        }

        if (booleanQuery.getClauses().length == 0)
        {
            booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
        }
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
        String field;
        if (locale.toString().length() > 0)
        {
            field = expandedFieldName + ".u";
        }
        else
        {
            field = expandedFieldName + ".__.u";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(lower);
        String first = getToken(field, builder.toString(), AnalysisMode.IDENTIFIER);
      
        builder = new StringBuilder();
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(upper);
        String last = getToken(field, builder.toString(), AnalysisMode.IDENTIFIER);

        Query query =  new CaseInsensitiveFieldRangeQuery(expandedFieldName, first, last, includeLower, includeUpper);
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
                    addLocaleSpecificTokenisedMLOrTextAttribute(queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, expandedFieldName);
                    break;
                case IDENTIFIER:
                case FUZZY:
                case PREFIX:
                case WILD:
                case LIKE:
                    setLowercaseExpandedTerms(false);
                    addLocaleSpecificUntokenisedMLOrTextAttribute(field, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, mlAnalysisMode, locale,
                            expandedFieldName);

                    break;
                }
                break;
            case FALSE:
                setLowercaseExpandedTerms(false);
                addLocaleSpecificUntokenisedMLOrTextAttribute(field, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, mlAnalysisMode, locale,
                        expandedFieldName);
                break;
            case TRUE:
            default:
                switch (analysisMode)
                {
                default:
                case DEFAULT:
                case TOKENISE:
                case IDENTIFIER:
                    addLocaleSpecificTokenisedMLOrTextAttribute(queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, expandedFieldName);
                    break;
                case FUZZY:
                case PREFIX:
                case WILD:
                case LIKE:
                    addLocaleSpecificUntokenisedMLOrTextAttribute(field, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, mlAnalysisMode, locale,
                            expandedFieldName);
                    break;
                }
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
                    addLocaleSpecificTokenisedMLOrTextAttribute(queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, expandedFieldName);
                    break;
                case IDENTIFIER:
                case FUZZY:
                case PREFIX:
                case WILD:
                case LIKE:
                    setLowercaseExpandedTerms(false);
                    addLocaleSpecificUntokenisedMLOrTextAttribute(field, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, mlAnalysisMode, locale,
                            expandedFieldName);
                    break;
                }
                break;
            case FALSE:
                setLowercaseExpandedTerms(false);
                addLocaleSpecificUntokenisedMLOrTextAttribute(field, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, mlAnalysisMode, locale,
                        expandedFieldName);
                break;
            case TRUE:
            default:
                switch (analysisMode)
                {
                case DEFAULT:
                case TOKENISE:
                case IDENTIFIER:
                    addLocaleSpecificTokenisedMLOrTextAttribute(queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, expandedFieldName);
                    break;
                case FUZZY:
                case PREFIX:
                case WILD:
                case LIKE:
                    addLocaleSpecificUntokenisedMLOrTextAttribute(field, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, mlAnalysisMode, locale,
                            expandedFieldName);
                    break;
                }
                break;
            }
        }
        finally
        {
            setLowercaseExpandedTerms(lowercaseExpandedTerms);
        }

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
    private void addLocaleSpecificUntokenisedMLOrTextAttribute(String field, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale, String textFieldName) throws ParseException
    {
        if (locale.toString().length() == 0)
        {
            StringBuilder builder = new StringBuilder(queryText.length() + 10);
            builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
            Query subQuery = subQueryBuilder.getQuery(textFieldName + ".__.u", builder.toString(), analysisMode, luceneFunction);
            if (subQuery != null)
            {
                booleanQuery.add(subQuery, Occur.SHOULD);
            }
            else
            {
                booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
            }
        }
        else
        {
            StringBuilder builder = new StringBuilder(queryText.length() + 10);
            builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
            Query subQuery = subQueryBuilder.getQuery(textFieldName + ".u", builder.toString(), analysisMode, luceneFunction);
            if (subQuery != null)
            {
                booleanQuery.add(subQuery, Occur.SHOULD);
            }
            else
            {
                booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
            }
        }

    }

    /**
     * @param queryText
     * @param subQueryBuilder
     * @param analysisMode
     * @param luceneFunction
     * @param booleanQuery
     * @param locale
     * @param textFieldName
     * @throws ParseException
     */
    private void addLocaleSpecificTokenisedMLOrTextAttribute(String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            BooleanQuery booleanQuery, Locale locale, String textFieldName) throws ParseException
    {
        if (locale.toString().length() == 0)
        {
            StringBuilder builder = new StringBuilder(queryText.length() + 10);
            builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
            Query subQuery = subQueryBuilder.getQuery(textFieldName + ".__", builder.toString(), analysisMode, luceneFunction);
            if (subQuery != null)
            {
                booleanQuery.add(subQuery, Occur.SHOULD);
            }
            else
            {
                booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
            }
        }
        else
        {
            StringBuilder builder = new StringBuilder(queryText.length() + 10);
            builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
            Query subQuery = subQueryBuilder.getQuery(textFieldName, builder.toString(), analysisMode, luceneFunction);
            if (subQuery != null)
            {
                booleanQuery.add(subQuery, Occur.SHOULD);
            }
            else
            {
                booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
            }
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
                addLocaleSpecificTokenisedTextRange(fieldName, part1, part2, includeLower, includeUpper, booleanQuery, locale, analysisMode);
                break;
            case IDENTIFIER:
                addLocaleSpecificUntokenisedTextRange(fieldName, part1, part2, includeLower, includeUpper, booleanQuery, locale, analysisMode);
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
            addLocaleSpecificUntokenisedTextRange(fieldName, part1, part2, includeLower, includeUpper, booleanQuery, locale, analysisMode);

            break;
        case TRUE:
            addLocaleSpecificTokenisedTextRange(fieldName, part1, part2, includeLower, includeUpper, booleanQuery, locale, analysisMode);
            break;
        default:
        }

    }

    private void addLocaleSpecificTokenisedTextRange(String expandedFieldName, String part1, String part2, boolean includeLower, boolean includeUpper, BooleanQuery booleanQuery,
            Locale locale, AnalysisMode analysisMode) throws ParseException
    {
        String field;
        if (locale.toString().length() > 0)
        {
            field = expandedFieldName;
        }
        else
        {
            field = expandedFieldName + ".__";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(part1);
        String first = getToken(field, builder.toString(), analysisMode);
        if(first == null)
        {
            first = getToken(field+".u", builder.toString(), analysisMode);
        }

        builder = new StringBuilder();
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(part2);
        String last = getToken(field, builder.toString(), analysisMode);
        if(last == null)
        {
            last = getToken(field+".u", builder.toString(), analysisMode);
        }

        Query query = new TermRangeQuery(field, first, last, includeLower, includeUpper);
        booleanQuery.add(query, Occur.SHOULD);
    }

    private void addLocaleSpecificUntokenisedTextRange(String expandedFieldName, String part1, String part2, boolean includeLower, boolean includeUpper, BooleanQuery booleanQuery,
            Locale locale, AnalysisMode analysisMode) throws ParseException
    {
        String field;
        if (locale.toString().length() > 0)
        {
            field = expandedFieldName + ".u";
        }
        else
        {
            field = expandedFieldName + ".__.u";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(part1);
        String first = getToken(field, builder.toString(), analysisMode);
      
        builder = new StringBuilder();
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(part2);
        String last = getToken(field, builder.toString(), analysisMode);

        Query query = new TermRangeQuery(field, first, last, includeLower, includeUpper);
        booleanQuery.add(query, Occur.SHOULD);
    }
}
