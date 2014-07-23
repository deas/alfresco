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
package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import java.util.List;
import java.util.Set;

import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.service.cmr.search.SearchParameters.SortDefinition;

/**
 * @author andyh
 * 
 * @param <Q> the query type used by the query engine implementation
 * @param <S> the sort type used by the query engine implementation
 * @param <E> the exception it throws 
 *
 */
public interface LuceneQueryBuilder <Q, S, E extends Throwable>
{
    /**
     * Build the matching lucene query
     * @param selectors
     * @param luceneContext
     * @param functionContext
     * @return - the query
     * @throws ParseException
     */
    public Q buildQuery(Set<String> selectors,  LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext) throws E;

    /**
     * Build the matching lucene sort
     * @param selectors
     * @param luceneContext
     * @param functionContext
     * @return - the sort spec
     * @throws E 
     */
    public S buildSort(Set<String> selectors, LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext) throws E;
    
    /**
     * Build a sort definition for a sorted result set wrapper
     * @param selectors
     * @param luceneContext
     * @param functionContext
     * @return
     */
    public List<SortDefinition> buildSortDefinitions(Set<String> selectors, LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext);

}
