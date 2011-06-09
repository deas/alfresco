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

import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;

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
    public Query buildLuceneEquality(AbstractLuceneQueryParser lqp, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException;

    /**
     * Note: null and not null are not required to support functions from the spec
     * @param lqp
     * @param propertyName
     * @param not
     * @return the query
     * @throws ParseException
     */
    public Query buildLuceneExists(AbstractLuceneQueryParser lqp, String propertyName, Boolean not) throws ParseException;

    /**
     * @param lqp
     * @param propertyName
     * @param value
     * @param mode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     */
    public Query buildLuceneGreaterThan(AbstractLuceneQueryParser lqp, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException;

    /**
     * @param lqp
     * @param propertyName
     * @param value
     * @param mode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     */
    public Query buildLuceneGreaterThanOrEquals(AbstractLuceneQueryParser lqp, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException;

    /**
     * @param lqp
     * @param propertyName
     * @param value
     * @param mode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     */
    public Query buildLuceneLessThan(AbstractLuceneQueryParser lqp, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException;

    /**
     * @param lqp
     * @param propertyName
     * @param value
     * @param mode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     */
    public Query buildLuceneLessThanOrEquals(AbstractLuceneQueryParser lqp, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException;

    /**
     * Note: Like is not required to support functions from the spec
     * @param lqp
     * @param propertyName
     * @param value
     * @param not
     * @return the query
     * @throws ParseException
     */
    public Query buildLuceneLike(AbstractLuceneQueryParser lqp, String propertyName, Serializable value, Boolean not) throws ParseException;

    /**
     * @param lqp
     * @param propertyName
     * @param value
     * @param mode
     * @param luceneFunction
     * @return the query
     * @throws ParseException
     */
    public Query buildLuceneInequality(AbstractLuceneQueryParser lqp, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException;

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
    public Query buildLuceneIn(AbstractLuceneQueryParser lqp, String propertyName, Collection<Serializable> values, Boolean not, PredicateMode mode) throws ParseException;

    /**
     * @param propertyName
     * @param luceneContext 
     * @param locales 
     * @param analysisMode 
     * @param reader 
     * @return the field used for sorting the given property
     */
    public String getLuceneSortField(AbstractLuceneQueryParser lqp, String propertyName);
    
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
    

}
