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
package org.alfresco.solr.query;

import java.util.List;

import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserExpressionAdaptor;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.Ordering;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.search.SearchParameters;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

/**
 * @author Andy
 *
 */
public class Lucene4QueryParserAdaptor implements LuceneQueryParserAdaptor<Query, Sort, Exception>
{

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getFieldQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.adaptor.lucene.AnalysisMode, org.alfresco.repo.search.adaptor.lucene.LuceneFunction)
     */
    @Override
    public Query getFieldQuery(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getRangeQuery(java.lang.String, java.lang.String, java.lang.String, boolean, boolean, org.alfresco.repo.search.adaptor.lucene.AnalysisMode, org.alfresco.repo.search.adaptor.lucene.LuceneFunction)
     */
    @Override
    public Query getRangeQuery(String field, String lower, String upper, boolean includeLower, boolean includeUpper, AnalysisMode analysisMode, LuceneFunction luceneFunction)
            throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getMatchAllQuery()
     */
    @Override
    public Query getMatchAllQuery() throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getMatchNoneQuery()
     */
    @Override
    public Query getMatchNoneQuery() throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getLikeQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.adaptor.lucene.AnalysisMode)
     */
    @Override
    public Query getLikeQuery(String field, String sqlLikeClause, AnalysisMode analysisMode) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getSearchParameters()
     */
    @Override
    public SearchParameters getSearchParameters()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getSortField(java.lang.String)
     */
    @Override
    public String getSortField(String field) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getIdentifierQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.adaptor.lucene.AnalysisMode, org.alfresco.repo.search.adaptor.lucene.LuceneFunction)
     */
    @Override
    public Query getIdentifierQuery(String field, String stringValue, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getIdentifieLikeQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.adaptor.lucene.AnalysisMode)
     */
    @Override
    public Query getIdentifieLikeQuery(String field, String stringValue, AnalysisMode analysisMode) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#sortFieldExists(java.lang.String)
     */
    @Override
    public boolean sortFieldExists(String noLocalField)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getFieldQuery(java.lang.String, java.lang.String)
     */
    @Override
    public Query getFieldQuery(String field, String value) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#buildSort(java.util.List, org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext)
     */
    @Override
    public Sort buildSort(List<Ordering> list, FunctionEvaluationContext functionContext) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getFuzzyQuery(java.lang.String, java.lang.String, java.lang.Float)
     */
    @Override
    public Query getFuzzyQuery(String luceneFieldName, String term, Float minSimilarity) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getField()
     */
    @Override
    public String getField()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getPhraseSlop()
     */
    @Override
    public int getPhraseSlop()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getFieldQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.adaptor.lucene.AnalysisMode, java.lang.Integer, org.alfresco.repo.search.adaptor.lucene.LuceneFunction)
     */
    @Override
    public Query getFieldQuery(String luceneFieldName, String term, AnalysisMode analysisMode, Integer slop, LuceneFunction luceneFunction) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getPrefixQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.adaptor.lucene.AnalysisMode)
     */
    @Override
    public Query getPrefixQuery(String luceneFieldName, String term, AnalysisMode analysisMode) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getSpanQuery(java.lang.String, java.lang.String, java.lang.String, int, boolean)
     */
    @Override
    public Query getSpanQuery(String luceneFieldName, String first, String last, int slop, boolean inOrder) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getWildcardQuery(java.lang.String, java.lang.String, org.alfresco.repo.search.adaptor.lucene.AnalysisMode)
     */
    @Override
    public Query getWildcardQuery(String luceneFieldName, String term, AnalysisMode mode) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getNegatedQuery(java.lang.Object)
     */
    @Override
    public Query getNegatedQuery(Query query) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getExpressionAdaptor()
     */
    @Override
    public LuceneQueryParserExpressionAdaptor<Query, Exception> getExpressionAdaptor()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getMatchAllNodesQuery()
     */
    @Override
    public Query getMatchAllNodesQuery()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor#getDatetimeSortField(java.lang.String, org.alfresco.service.cmr.dictionary.PropertyDefinition)
     */
    @Override
    public String getDatetimeSortField(String field, PropertyDefinition propertyDef)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
