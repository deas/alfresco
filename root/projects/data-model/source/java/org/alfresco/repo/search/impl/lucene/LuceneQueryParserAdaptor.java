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
package org.alfresco.repo.search.impl.lucene;

import java.util.List;

import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.Ordering;
import org.alfresco.service.cmr.search.SearchParameters;
import org.apache.lucene.queryParser.ParseException;

/**
 * Adaptor class principally to wrap lucene parser implementations and encapsulate changes between lucene versions
 * of query building.
 * 
 * @param <Q> the query type used by the query engine implementation
 * @param <S> the sort type used by the query engine implementation
 * @param <E> the exception it throws 
 * 
 * @author Andy
 *
 */
public interface LuceneQueryParserAdaptor<Q, S, E extends Throwable>
{

    /**
     * @param field
     * @param queryText
     * @param analysisMode
     * @param luceneFunction
     * @return
     */
    Q getFieldQuery(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws E;

    /**
     * @param field
     * @param lower
     * @param upper
     * @param includeLower
     * @param includeUpper
     * @param analysisMode
     * @param luceneFunction
     * @return
     */
    Q getRangeQuery(String field, String lower, String upper, boolean includeLower, boolean includeUpper, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws E;

    
    /**
     * A query that matches all docs
     * 
     * @return
     * @throws E
     */
    Q getMatchAllQuery() throws E;
    
    /**
     * A query that matches no docs.
     * 
     * @return
     * @throws E
     */
    Q getMatchNoneQuery() throws E;

    /**
     * @param field
     * @param sqlLikeClause
     * @param analysisMode
     * @return
     */
    Q getLikeQuery(String field, String sqlLikeClause, AnalysisMode analysisMode) throws E;

    /**
     * @return
     */
    SearchParameters getSearchParameters();

    /**
     * @param field
     * @return
     */
    String getSortField(String field) throws E;

    /**
     * Wrap generating a potentially complex id + version query
     * 
     * @param field
     * @param stringValue
     * @param analysisMode
     * @param luceneFunction
     * @return
     */
    Q getIdentifierQuery(String field, String stringValue, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws E;

    /**
     * Wrap generating a potentially complex id + version query
     * 
     * @param field
     * @param stringValue
     * @param identifier
     * @return
     */
    Q getIdentifieLikeQuery(String field, String stringValue, AnalysisMode analysisMode) throws E;

    /**
     * @param noLocalField
     * @return
     */
    boolean sortFieldExists(String noLocalField);

    /**
     * @param field
     * @param value
     * @return
     * @throws ParseException 
     */
    Q getFieldQuery(String field, String value) throws E;

    /**
     * @param list 
     * @param functionContext 
     * @return
     * @throws E 
     */
    S buildSort(List<Ordering> list, FunctionEvaluationContext functionContext) throws E;

    /**
     * @param luceneFieldName
     * @param term
     * @param minSimilarity
     * @return
     * @throws ParseException 
     */
    Q getFuzzyQuery(String luceneFieldName, String term, Float minSimilarity) throws E;

    /**
     * Get the default field
     * 
     * @return
     */
    String getField();

    /**
     * Get the default phrase slop
     * 
     * @return
     */
    int getPhraseSlop();

    /**
     * @param luceneFieldName
     * @param term
     * @param analysisMode
     * @param slop
     * @param luceneFunction
     * @return
     */
    Q getFieldQuery(String luceneFieldName, String term, AnalysisMode analysisMode, Integer slop, LuceneFunction luceneFunction) throws E;

    /**
     * @param luceneFieldName
     * @param term
     * @param analysisMode
     * @return
     */
    Q getPrefixQuery(String luceneFieldName, String term, AnalysisMode analysisMode) throws E;

    /**
     * @param luceneFieldName
     * @param first
     * @param last
     * @param slop
     * @param inOrder
     * @return
     */
    Q getSpanQuery(String luceneFieldName, String first, String last, int slop, boolean inOrder) throws E;

    /**
     * @param luceneFieldName
     * @param term
     * @param mode
     * @return
     */
    Q getWildcardQuery(String luceneFieldName, String term, AnalysisMode mode) throws E;
    
    /**
     * Invert a query - add a mandatory must not match anything query alnogside 
     * 
     * @param query
     * @return
     */
    Q getNegatedQuery(Q query) throws E;
    
    /**
     * Utility to build conjunctions, disjunctions and negation
     * @return
     */
    LuceneQueryParserExpressionAdaptor<Q, E> getExpressionAdaptor();

    /**
     * A query that matches all alfresco nodes (not extra stuff that may be in the underlying index)
     * 
     * @return
     */
    Q getMatchAllNodesQuery(); 
}
