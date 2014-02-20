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
package org.alfresco.repo.search.impl.querymodel;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * The function evaluation context for lucene query implementations.
 * 
 * This context is used at query time and also when navigating the results to get column values.
 * 
 * @author andyh
 */
public interface FunctionEvaluationContext
{
    /**
     * @return the matching nodes by selector (at navigation time)
     */
    public Map<String, NodeRef> getNodeRefs();

    /**
     * @return the scores by selector (at navigation time)
     */
    public Map<String, Float> getScores();

    /**
     * Get a property
     * @param nodeRef
     * @param propertyName
     * @return the property (at navigation time)
     */
    public Serializable getProperty(NodeRef nodeRef, String propertyName);

    /**
     * @return the node service
     */
    public NodeService getNodeService();

    /**
     * @return the score (at navigation time)
     */
    public Float getScore();

    /**
     * @param lqp
     * @param propertyName
     * @param value
     * @param mode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     */
    public <Q, S, E extends Throwable> Q buildLuceneEquality(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * Note: null and not null are not required to support functions from the spec
     * @param lqp
     * @param propertyName
     * @param not
     * @return the query
     * @throws ParseException
     */
    public <Q, S, E extends Throwable> Q buildLuceneExists(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Boolean not) throws E;

    /**
     * @param lqp
     * @param propertyName
     * @param value
     * @param mode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     */
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqp
     * @param propertyName
     * @param value
     * @param mode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     */
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqp
     * @param propertyName
     * @param value
     * @param mode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     */
    public <Q, S, E extends Throwable> Q buildLuceneLessThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqp
     * @param propertyName
     * @param value
     * @param mode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     */
    public <Q, S, E extends Throwable> Q buildLuceneLessThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * Note: Like is not required to support functions from the spec
     * @param lqp
     * @param propertyName
     * @param value
     * @param not
     * @return the query
     * @throws ParseException
     */
    public <Q, S, E extends Throwable> Q buildLuceneLike(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, Boolean not) throws E;

    /**
     * @param lqp
     * @param propertyName
     * @param value
     * @param mode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     */
    public <Q, S, E extends Throwable> Q buildLuceneInequality(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * Note: In is not required to support functions from the spec
     * @param lqp
     * @param propertyName
     * @param values
     * @param not
     * @param mode
     * @return the query
     * @throws ParseException
     */
    public <Q, S, E extends Throwable> Q buildLuceneIn(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Collection<Serializable> values, Boolean not, PredicateMode mode) throws E;

    /**
     * @param propertyName
     * @param luceneContext 
     * @param locales 
     * @param analysisMode 
     * @param reader 
     * @return the field used for sorting the given property
     * @throws E 
     */
    public <Q, S, E extends Throwable> String getLuceneSortField(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName) throws E;
    
    /**
     * @param propertyName
     * @return - is this an object id
     */
    public boolean isObjectId(String propertyName);

    /**
     * @param propertyName
     * @return is this property queryable
     */
    public boolean isQueryable(String propertyName);

    /**
     * @param propertyName
     * @return Is this property orderable
     */
    public boolean isOrderable(String propertyName);
    
    /**
     * @param propertyName
     * @return the lucene field name for the property
     */
    public String getLuceneFieldName(String propertyName);
    
    /**
     * @param functionArgument
     * @return the lucene function appropriate to a function argument
     */
    public LuceneFunction getLuceneFunction(FunctionArgument functionArgument);

    /**
     * @param type
     * @param propertyName
     */
    public void checkFieldApplies(Selector selector, String propertyName);
    
    /**
     * Is this a multi-valued property? 
     * @param propertyName
     * @return
     */
    public boolean isMultiValued(String propertyName);
    
    public String getAlfrescoPropertyName(String propertyName);

    /**
     * @param staticValue
     * @return
     */
    public String getAlfrescoTypeName(String staticValue);

}
