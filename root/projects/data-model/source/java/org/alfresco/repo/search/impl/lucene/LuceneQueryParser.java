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

import java.util.List;
import java.util.Locale;

import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.analysis.PathTokenFilter;
import org.alfresco.repo.search.impl.lucene.query.CaseInsensitiveFieldQuery;
import org.alfresco.repo.search.impl.lucene.query.CaseInsensitiveFieldRangeQuery;
import org.alfresco.repo.search.impl.lucene.query.PathQuery;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.CharStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParserTokenManager;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.saxpath.SAXPathException;

import com.werken.saxpath.XPathReader;

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
public class LuceneQueryParser extends AbstractLuceneQueryParser
{

    private static Log s_logger = LogFactory.getLog(LuceneQueryParser.class);

    /**
     * Parses a query string, returning a {@link org.apache.lucene.search.Query}.
     * 
     * @param query
     *            the query string to be parsed.
     * @param field
     *            the default field for query terms.
     * @param analyzer
     *            used to find terms in the query text.
     * @param namespacePrefixResolver
     * @param dictionaryService
     * @param tenantService
     * @param defaultOperator
     * @param searchParameters
     * @param indexReader
     * @return - the query
     * @throws ParseException
     *             if the parsing fails
     */
    static public Query parse(String query, String field, Analyzer analyzer, NamespacePrefixResolver namespacePrefixResolver, DictionaryService dictionaryService,
            TenantService tenantService, Operator defaultOperator, SearchParameters searchParameters, MLAnalysisMode defaultSearchMLAnalysisMode, IndexReader indexReader)
            throws ParseException
    {
        if (s_logger.isDebugEnabled())
        {
            s_logger.debug("Using Alfresco Lucene Query Parser for query: " + query);
        }
        LuceneQueryParser parser = new LuceneQueryParser(field, analyzer);
        parser.setDefaultOperator(defaultOperator);
        parser.setNamespacePrefixResolver(namespacePrefixResolver);
        parser.setDictionaryService(dictionaryService);
        parser.setTenantService(tenantService);
        parser.setSearchParameters(searchParameters);
        parser.setDefaultSearchMLAnalysisMode(defaultSearchMLAnalysisMode);
        parser.setIndexReader(indexReader);
        parser.setAllowLeadingWildcard(true);
        // TODO: Apply locale constraints at the top level if required for the non ML doc types.
        Query result = parser.parse(query);
        if (s_logger.isDebugEnabled())
        {
            s_logger.debug("Query " + query + "                             is\n\t" + result.toString());
        }
        return result;
    }

    /**
     * Lucene default constructor
     * 
     * @param arg0
     * @param arg1
     */
    public LuceneQueryParser(String arg0, Analyzer arg1)
    {
        super(arg0, arg1);
    }

    /**
     * Lucene default constructor
     * 
     * @param arg0
     */
    public LuceneQueryParser(CharStream arg0)
    {
        super(arg0);
    }

    /**
     * Lucene default constructor
     * 
     * @param arg0
     */
    public LuceneQueryParser(QueryParserTokenManager arg0)
    {
        super(arg0);
    }

    /**
     * @param queryText
     * @return
     */
    protected Query createAclIdQuery(String queryText) throws ParseException
    {
        return createNoMatchQuery();
    }

    /**
     * @param queryText
     * @return
     */
    protected Query createOwnerQuery(String queryText) throws ParseException
    {
        return createNoMatchQuery();
    }

    /**
     * @param queryText
     * @return
     */
    protected Query createReaderQuery(String queryText) throws ParseException
    {
        return createNoMatchQuery();
    }

    /**
     * @param queryText
     * @return
     */
    protected Query createAuthorityQuery(String queryText) throws ParseException
    {
        return createNoMatchQuery();
    }

    protected Query createAssocTypeQNameQuery(String queryText) throws SAXPathException
    {
        // This was broken and using only FIELD_PRIMARYASSOCTYPEQNAME
        // The field was also not indexed correctly.
        // We do both for backward compatability ...
        BooleanQuery booleanQuery = new BooleanQuery();

        XPathReader reader = new XPathReader();
        LuceneXPathHandler handler = new LuceneXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse("//" + queryText);
        PathQuery query = handler.getQuery();
        query.setPathField(FIELD_PATH);
        query.setQnameField(FIELD_ASSOCTYPEQNAME);

        booleanQuery.add(query, Occur.SHOULD);
        booleanQuery.add(createPrimaryAssocTypeQNameQuery(queryText), Occur.SHOULD);

        return booleanQuery;
    }

    protected Query createPrimaryAssocTypeQNameQuery(String queryText) throws SAXPathException
    {
        XPathReader reader = new XPathReader();
        LuceneXPathHandler handler = new LuceneXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse("//" + queryText);
        PathQuery query = handler.getQuery();
        query.setPathField(FIELD_PATH);
        query.setQnameField(FIELD_PRIMARYASSOCTYPEQNAME);
        return query;
    }

    protected Query createQNameQuery(String queryText) throws SAXPathException
    {
        XPathReader reader = new XPathReader();
        LuceneXPathHandler handler = new LuceneXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse(queryText);
        PathQuery query = handler.getQuery();
        // if it matches all docs
        if ((query.getPathStructuredFieldPositions().size() == 0) && (query.getQNameStructuredFieldPositions().size() == 2))
        {
            if (query.getQNameStructuredFieldPositions().get(0).getTermText().equals(PathTokenFilter.NO_NS_TOKEN_TEXT))
            {
                return createTermQuery(FIELD_QNAME, queryText);
            }
        }

        return createPathQuery("//" + queryText, false);
    }

    protected Query createPathQuery(String queryText, boolean withRepeats) throws SAXPathException
    {
        XPathReader reader = new XPathReader();
        LuceneXPathHandler handler = new LuceneXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse(queryText);
        PathQuery pathQuery = handler.getQuery();
        pathQuery.setRepeats(withRepeats);
        return pathQuery;
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
    protected void addTextRange(String field, String part1, String part2, boolean includeLower, boolean includeUpper, AnalysisMode analysisMode, String fieldName,
            PropertyDefinition propertyDef, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale) throws ParseException
    {
        String textFieldName = fieldName;

        if ((analysisMode == AnalysisMode.IDENTIFIER) || (analysisMode == AnalysisMode.LIKE))
        {
            {
                // text and ml text need locale
                IndexTokenisationMode tm = propertyDef.getIndexTokenisationMode();
                if ((tm != null) && (tm == IndexTokenisationMode.BOTH))
                {
                    if (locale.toString().length() == 0)
                    {
                        textFieldName = textFieldName + FIELD_NO_LOCALE_SUFFIX;
                    }
                    else
                    {
                        textFieldName = textFieldName + "." + locale + FIELD_SORT_SUFFIX;
                    }

                }

            }
        }
        switch (tokenisationMode)
        {
        case BOTH:
            switch (analysisMode)
            {
            case DEFAULT:
            case TOKENISE:
                addLocaleSpecificTokenisedTextRange(part1, part2, includeLower, includeUpper, analysisMode, fieldName, booleanQuery, locale, textFieldName);
                break;
            case IDENTIFIER:
                addLocaleSpecificUntokenisedTextRange(field, part1, part2, includeLower, includeUpper, booleanQuery, mlAnalysisMode, locale, textFieldName);
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
            addLocaleSpecificUntokenisedTextRange(field, part1, part2, includeLower, includeUpper, booleanQuery, mlAnalysisMode, locale, textFieldName);

            break;
        case TRUE:
            addLocaleSpecificTokenisedTextRange(part1, part2, includeLower, includeUpper, analysisMode, fieldName, booleanQuery, locale, textFieldName);
            break;
        default:
        }
    }

    protected void addLocaleSpecificUntokenisedTextRangeFunction(String expandedFieldName, String lower, String upper, boolean includeLower, boolean includeUpper,
            LuceneFunction luceneFunction, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale, IndexTokenisationMode tokenisationMode)
    {

        if (locale.toString().length() == 0)
        {
            return;
        }

        String textFieldName = expandedFieldName;
        if (tokenisationMode == IndexTokenisationMode.BOTH)
        {
            textFieldName = textFieldName + "." + locale + FIELD_SORT_SUFFIX;
        }

        String lowerTermText = lower;
        if (locale.toString().length() > 0)
        {
            lowerTermText = "{" + locale + "}" + lower;
        }
        String upperTermText = upper;
        if (locale.toString().length() > 0)
        {
            upperTermText = "{" + locale + "}" + upper;
        }
        Query subQuery = buildRangeFunctionQuery(textFieldName, lowerTermText, upperTermText, includeLower, includeUpper, luceneFunction);
        booleanQuery.add(subQuery, Occur.SHOULD);

        if (booleanQuery.getClauses().length == 0)
        {
            booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
        }
    }

    private Query buildRangeFunctionQuery(String expandedFieldName, String lowerTermText, String upperTermText, boolean includeLower, boolean includeUpper,
            LuceneFunction luceneFunction)
    {
        String testLowerTermText = lowerTermText;
        if (testLowerTermText.startsWith("{"))
        {
            int index = lowerTermText.indexOf("}");
            testLowerTermText = lowerTermText.substring(index + 1);
        }

        String testUpperTermText = upperTermText;
        if (testUpperTermText.startsWith("{"))
        {
            int index = upperTermText.indexOf("}");
            testUpperTermText = upperTermText.substring(index + 1);
        }

        switch (luceneFunction)
        {
        case LOWER:
            if (testLowerTermText.equals(testLowerTermText.toLowerCase()) && testUpperTermText.equals(testUpperTermText.toLowerCase()))
            {
                return new CaseInsensitiveFieldRangeQuery(expandedFieldName, lowerTermText, upperTermText, includeLower, includeUpper);
            }
            else
            {
                // No match
                return createNoMatchQuery();
            }
        case UPPER:
            if (testLowerTermText.equals(testLowerTermText.toUpperCase()) && testUpperTermText.equals(testUpperTermText.toUpperCase()))
            {
                return new CaseInsensitiveFieldRangeQuery(expandedFieldName, lowerTermText, upperTermText, includeLower, includeUpper);
            }
            else
            {
                // No match
                return createNoMatchQuery();
            }
        default:
            throw new UnsupportedOperationException("Unsupported Lucene Function " + luceneFunction);

        }
    }

    private void addLocaleSpecificTokenisedTextRange(String part1, String part2, boolean includeLower, boolean includeUpper, AnalysisMode analysisMode, String fieldName,
            BooleanQuery booleanQuery, Locale locale, String textFieldName) throws ParseException
    {
        StringBuilder builder = new StringBuilder();
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(part1);
        String first = getToken(fieldName, builder.toString(), analysisMode);

        builder = new StringBuilder();
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(part2);
        String last = getToken(fieldName, builder.toString(), analysisMode);

        Query query = new ConstantScoreRangeQuery(textFieldName, first, last, includeLower, includeUpper);
        booleanQuery.add(query, Occur.SHOULD);
    }

    private void addLocaleSpecificUntokenisedTextRange(String field, String part1, String part2, boolean includeLower, boolean includeUpper, BooleanQuery booleanQuery,
            MLAnalysisMode mlAnalysisMode, Locale locale, String textFieldName)
    {
    
        if (locale.toString().length() > 0)
        {
            String lower = "{" + locale + "}" + part1;
            String upper = "{" + locale + "}" + part2;
            

            Query subQuery = new ConstantScoreRangeQuery(textFieldName, lower, upper, includeLower, includeUpper);
            booleanQuery.add(subQuery, Occur.SHOULD);

            if (booleanQuery.getClauses().length == 0)
            {
                booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
            }
        }
        else
        {
            if((part1.compareTo("{") > 0) || (part2.compareTo("{") < 0)) 
            {
                Query subQuery = new ConstantScoreRangeQuery(textFieldName, part1, part2, includeLower, includeUpper);
                booleanQuery.add(subQuery, Occur.SHOULD);

                if (booleanQuery.getClauses().length == 0)
                {
                    booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
                } 
            }
            else
            {
                // Split to avoid match {en} etc
                BooleanQuery splitQuery = new BooleanQuery();

                Query lowerQuery = new ConstantScoreRangeQuery(textFieldName, part1, "{", includeLower, false);
                Query upperQuery = new ConstantScoreRangeQuery(textFieldName, "|", part2, true, includeUpper);

                splitQuery.add(lowerQuery, Occur.SHOULD);
                splitQuery.add(upperQuery, Occur.SHOULD);

                booleanQuery.add(splitQuery, Occur.SHOULD);

                if (booleanQuery.getClauses().length == 0)
                {
                    booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
                }
            }
        }
        
    }

    protected void addLocaleSpecificUntokenisedMLOrTextFunction(String expandedFieldName, String queryText, LuceneFunction luceneFunction, BooleanQuery booleanQuery,
            MLAnalysisMode mlAnalysisMode, Locale locale, IndexTokenisationMode tokenisationMode)
    {
        String textFieldName = expandedFieldName;

        if (tokenisationMode == IndexTokenisationMode.BOTH)
        {
            if (locale.toString().length() == 0)
            {
                textFieldName = textFieldName + FIELD_NO_LOCALE_SUFFIX;
            }
            else
            {
                textFieldName = textFieldName + "." + locale + FIELD_SORT_SUFFIX;
            }
        }

        
        String termText = queryText;
        if (locale.toString().length() > 0)
        {
            termText = "{" + locale + "}" + queryText;
        }
        Query subQuery = buildFunctionQuery(textFieldName, termText, luceneFunction);
        booleanQuery.add(subQuery, Occur.SHOULD);

        if (booleanQuery.getClauses().length == 0)
        {
            booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
        }
    }
    
    private void addLocaleSpecificUntokenisedMLOrTextAttribute(String sourceField, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode,
            LuceneFunction luceneFunction, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale, String actualField) throws ParseException
    {

        String termText = queryText;
        if (locale.toString().length() > 0)
        {
            termText = "{" + locale + "}" + queryText;
        }
        Query subQuery = subQueryBuilder.getQuery(actualField, termText, analysisMode, luceneFunction);
        booleanQuery.add(subQuery, Occur.SHOULD);

        if (booleanQuery.getClauses().length == 0)
        {
            booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
        }
    }

    private void addLocaleSpecificTokenisedMLOrTextAttribute(String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            BooleanQuery booleanQuery, Locale locale, String actualField) throws ParseException
    {
        StringBuilder builder = new StringBuilder(queryText.length() + 10);
        builder.append("\u0000").append(locale.toString()).append("\u0000").append(queryText);
        Query subQuery = subQueryBuilder.getQuery(actualField, builder.toString(), analysisMode, luceneFunction);
        if (subQuery != null)
        {
            booleanQuery.add(subQuery, Occur.SHOULD);
        }
        else
        {
            booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
        }
    }

//    private void addLocaleSpecificUntokenisedMLOrTextFunction(String expandedFieldName, String queryText, LuceneFunction luceneFunction, BooleanQuery booleanQuery,
//            MLAnalysisMode mlAnalysisMode, Locale locale, String textFieldName)
//    {
//        String termText = queryText;
//        if (locale.toString().length() > 0)
//        {
//            termText = "{" + locale + "}" + queryText;
//        }
//        Query subQuery = buildFunctionQuery(textFieldName, termText, luceneFunction);
//        booleanQuery.add(subQuery, Occur.SHOULD);
//
//        if (booleanQuery.getClauses().length == 0)
//        {
//            booleanQuery.add(createNoMatchQuery(), Occur.SHOULD);
//        }
//    }

    private Query buildFunctionQuery(String expandedFieldName, String termText, LuceneFunction luceneFunction)
    {
        String testText = termText;
        if (termText.startsWith("{"))
        {
            int index = termText.indexOf("}");
            testText = termText.substring(index + 1);
        }
        switch (luceneFunction)
        {
        case LOWER:
            if (testText.equals(testText.toLowerCase()))
            {
                return new CaseInsensitiveFieldQuery(new Term(expandedFieldName, termText));
            }
            else
            {
                // No match
                return createNoMatchQuery();
            }
        case UPPER:
            if (testText.equals(testText.toUpperCase()))
            {
                return new CaseInsensitiveFieldQuery(new Term(expandedFieldName, termText));
            }
            else
            {
                // No match
                return createNoMatchQuery();
            }
        default:
            throw new UnsupportedOperationException("Unsupported Lucene Function " + luceneFunction);

        }
    }

    protected void addMLTextAttributeQuery(String field, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            String expandedFieldName, PropertyDefinition propertyDef, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode,
            Locale locale) throws ParseException
    {
        String mlFieldName = expandedFieldName;

        if ((tokenisationMode == IndexTokenisationMode.BOTH) && ((analysisMode == AnalysisMode.IDENTIFIER) || (analysisMode == AnalysisMode.LIKE)))
        {
            {
                // text and ml text need locale
                IndexTokenisationMode tm = propertyDef.getIndexTokenisationMode();
                if ((tm != null) && (tm == IndexTokenisationMode.BOTH))
                {
                    if (locale.toString().length() == 0)
                    {
                        mlFieldName = mlFieldName + FIELD_NO_LOCALE_SUFFIX;
                    }
                    else
                    {
                        mlFieldName = mlFieldName + "." + locale + FIELD_SORT_SUFFIX;
                    }
                }

            }
        }

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
                            mlFieldName);

                    break;
                }
                break;
            case FALSE:
                setLowercaseExpandedTerms(false);
                addLocaleSpecificUntokenisedMLOrTextAttribute(field, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, mlAnalysisMode, locale,
                        mlFieldName);
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
                            mlFieldName);
                    break;
                }
            }
        }
        finally
        {
            setLowercaseExpandedTerms(lowercaseExpandedTerms);
        }
    }
    
    protected Query addContentAttributeQuery(String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction, String expandedFieldName,
            List<Locale> expandedLocales, MLAnalysisMode mlAnalysisMode) throws ParseException
    {
        
      if (mlAnalysisMode.includesAll())
      {
          return subQueryBuilder.getQuery(expandedFieldName, queryText, analysisMode, luceneFunction);
      }
        
        if (expandedLocales.size() > 0)
        {
            BooleanQuery booleanQuery = new BooleanQuery();
            Query contentQuery = subQueryBuilder.getQuery(expandedFieldName, queryText, analysisMode, luceneFunction);
            if (contentQuery != null)
            {
                booleanQuery.add(contentQuery, Occur.MUST);
                BooleanQuery subQuery = new BooleanQuery();
                for (Locale locale : (expandedLocales))
                {
                    StringBuilder builder = new StringBuilder();
                    builder.append(expandedFieldName).append(FIELD_LOCALE_SUFFIX);
                    String localeString = locale.toString();
                    if (localeString.indexOf("*") == -1)
                    {
                        Query localeQuery = getFieldQuery(builder.toString(), localeString);
                        if (localeQuery != null)
                        {
                            subQuery.add(localeQuery, Occur.SHOULD);
                        }
                        else
                        {
                            subQuery.add(createNoMatchQuery(), Occur.SHOULD);
                        }
                    }
                    else
                    {
                        Query localeQuery = getWildcardQuery(builder.toString(), localeString);
                        if (localeQuery != null)
                        {
                            subQuery.add(localeQuery, Occur.SHOULD);
                        }
                        else
                        {
                            subQuery.add(createNoMatchQuery(), Occur.SHOULD);
                        }
                    }
                }
                booleanQuery.add(subQuery, Occur.MUST);
            }
            return booleanQuery;
        }
        else
        {
            Query query = subQueryBuilder.getQuery(expandedFieldName, queryText, analysisMode, luceneFunction);
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
    protected void addTextAttributeQuery(String field, String queryText, SubQuery subQueryBuilder, AnalysisMode analysisMode, LuceneFunction luceneFunction,
            String expandedFieldName, IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale) throws ParseException
    {
        String textFieldName = expandedFieldName;

        if ((tokenisationMode == IndexTokenisationMode.BOTH) && ((analysisMode == AnalysisMode.IDENTIFIER) || (analysisMode == AnalysisMode.LIKE)))
        {
            if ((null != locale) && (0 == locale.toString().length()))
            {
                textFieldName += FIELD_NO_LOCALE_SUFFIX;
            }
            else
            {
                textFieldName = textFieldName + "." + locale + FIELD_SORT_SUFFIX;
            }
        }

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
                    addLocaleSpecificTokenisedMLOrTextAttribute(queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, locale, textFieldName);
                    break;
                case IDENTIFIER:
                case FUZZY:
                case PREFIX:
                case WILD:
                case LIKE:
                    setLowercaseExpandedTerms(false);
                    addLocaleSpecificUntokenisedMLOrTextAttribute(field, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, mlAnalysisMode, locale,
                            textFieldName);
                    break;
                }
                break;
            case FALSE:
                setLowercaseExpandedTerms(false);
                addLocaleSpecificUntokenisedMLOrTextAttribute(field, queryText, subQueryBuilder, analysisMode, luceneFunction, booleanQuery, mlAnalysisMode, locale, textFieldName);
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
                            textFieldName);
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

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#isLucene()
     */
    @Override
    protected boolean isLucene()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addTextSpanQuery(java.lang.String, java.lang.String, java.lang.String, int, boolean, java.lang.String, org.alfresco.repo.dictionary.IndexTokenisationMode, org.apache.lucene.search.BooleanQuery, org.alfresco.repo.search.MLAnalysisMode, java.util.Locale)
     */
    @Override
    protected void addTextSpanQuery(String field, String first, String last, int slop, boolean inOrder, String expandedFieldName, IndexTokenisationMode tokenisationMode,
            BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale)
    {
        SpanQuery firstTerm = new SpanTermQuery(new Term(field, first));
        SpanQuery lastTerm = new SpanTermQuery(new Term(field, last));
        SpanNearQuery result =  new SpanNearQuery(new SpanQuery[] { firstTerm, lastTerm }, slop, inOrder);
        booleanQuery.add(result, Occur.SHOULD);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addContentSpanQuery(java.lang.String, java.lang.String, java.lang.String, int, boolean, java.lang.String, java.util.List, org.alfresco.repo.search.MLAnalysisMode)
     */
    @Override
    protected org.apache.lucene.search.Query addContentSpanQuery(String field, String first, String last, int slop, boolean inOrder, String expandedFieldName,
            List<Locale> expandedLocales, MLAnalysisMode mlAnalysisMode)
    {
        SpanQuery firstTerm = new SpanTermQuery(new Term(field, first));
        SpanQuery lastTerm = new SpanTermQuery(new Term(field, last));
        SpanNearQuery result =  new SpanNearQuery(new SpanQuery[] { firstTerm, lastTerm }, slop, inOrder);
        return result;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser#addMLTextSpanQuery(java.lang.String, java.lang.String, java.lang.String, int, boolean, java.lang.String, org.alfresco.service.cmr.dictionary.PropertyDefinition, org.alfresco.repo.dictionary.IndexTokenisationMode, org.apache.lucene.search.BooleanQuery, org.alfresco.repo.search.MLAnalysisMode, java.util.Locale)
     */
    @Override
    protected void addMLTextSpanQuery(String field, String first, String last, int slop, boolean inOrder, String expandedFieldName, PropertyDefinition propertyDef,
            IndexTokenisationMode tokenisationMode, BooleanQuery booleanQuery, MLAnalysisMode mlAnalysisMode, Locale locale)
    {
        SpanQuery firstTerm = new SpanTermQuery(new Term(field, first));
        SpanQuery lastTerm = new SpanTermQuery(new Term(field, last));
        SpanNearQuery result =  new SpanNearQuery(new SpanQuery[] { firstTerm, lastTerm }, slop, inOrder);
        booleanQuery.add(result, Occur.SHOULD);
    }

}
