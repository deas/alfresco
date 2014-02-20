/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.Ordering;
import org.alfresco.service.cmr.search.SearchParameters;
import org.apache.lucene.queryParser.ParseException;

/**
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
     * @param part1
     * @param part2
     * @param includeLower
     * @param includeUpper
     * @param analysisMode
     * @param luceneFunction
     * @return
     */
    Q getRangeQuery(String field, String part1, String part2, boolean includeLower, boolean includeUpper, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws E;

    
    Q getMatchAllQuery() throws E;
    
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
     * @return
     */
    MLAnalysisMode getDefaultSearchMLAnalysisMode();

    /**
     * @param field
     * @return
     */
    String getSortField(String field) throws E;

    /**
     * @param field
     * @param stringValue
     * @param analysisMode
     * @param luceneFunction
     * @return
     */
    Q getIdentifierQuery(String field, String stringValue, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws E;

    /**
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
     * @param string
     * @param string2
     * @return
     * @throws ParseException 
     */
    Q getFieldQuery(String string, String string2) throws E;

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
     * @return
     */
    String getField();

    /**
     * @return
     */
    int getPhraseSlop();

    /**
     * @param luceneFieldName
     * @param term
     * @param analysisMode
     * @param slop
     * @param field
     * @return
     */
    Q getFieldQuery(String luceneFieldName, String term, AnalysisMode analysisMode, Integer slop, LuceneFunction field) throws E;

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
     * @param b
     * @return
     */
    Q getSpanQuery(String luceneFieldName, String first, String last, int slop, boolean b) throws E;

    /**
     * @param luceneFieldName
     * @param term
     * @param mode
     * @return
     */
    Q getWildcardQuery(String luceneFieldName, String term, AnalysisMode mode) throws E;
    
    /**
     * 
     * @param query
     * @return
     */
    Q getNegatedQuery(Q query) throws E;
    
    /**
     * 
     * @return
     */
    LuceneQueryParserExpressionAdaptor<Q, E> getExpressionAdaptor();

    /**
     * @return
     */
    Q getMatchAllNodesQuery(); 
}
