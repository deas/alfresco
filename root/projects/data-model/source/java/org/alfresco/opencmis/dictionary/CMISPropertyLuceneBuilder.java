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
package org.alfresco.opencmis.dictionary;

import java.io.Serializable;
import java.util.Collection;

import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;


/**
 * Encapsulate the building of lucene queries for property predicates
 */
public interface CMISPropertyLuceneBuilder
{
    /**
     * @param lqp
     * @param value
     * @param mode
     * @param luceneFunction 
     * @return the query - may be null if no query is required
     * @throws ParseException
     */
    public <Q, S, E extends Throwable> Q buildLuceneEquality(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqp
     * @param not
     * @return the query - may be null if no query is required
     * @throws ParseException
     */
    public <Q, S, E extends Throwable> Q buildLuceneExists(LuceneQueryParserAdaptor<Q, S, E> lqpa, Boolean not) throws E;

    /**
     * @param lqp
     * @param value
     * @param mode
     * @param luceneFunction 
     * @return the query - may be null if no query is required
     * @throws ParseException 
     */
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqp
     * @param value
     * @param mode
     * @param luceneFunction 
     * @return the query - may be null if no query is required
     * @throws ParseException 
     */
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqp
     * @param values
     * @param not
     * @param mode
     * @return the query - may be null if no query is required
     * @throws ParseException 
     */
    public <Q, S, E extends Throwable> Q buildLuceneIn(LuceneQueryParserAdaptor<Q, S, E> lqpa, Collection<Serializable> values, Boolean not, PredicateMode mode) throws E;

    /**
     * @param lqp
     * @param value
     * @param mode
     * @param luceneFunction 
     * @return the query - may be null if no query is required
     * @throws ParseException 
     */
    public <Q, S, E extends Throwable> Q buildLuceneInequality(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqp
     * @param value
     * @param mode
     * @param luceneFunction 
     * @return the query - may be null if no query is required
     * @throws ParseException 
     */
    public <Q, S, E extends Throwable> Q buildLuceneLessThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqp
     * @param value
     * @param mode
     * @param luceneFunction 
     * @return the query - may be null if no query is required
     * @throws ParseException 
     */
    public <Q, S, E extends Throwable> Q buildLuceneLessThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqp
     * @param value
     * @param not
     * @return the query - may be null if no query is required
     * @throws ParseException 
     */
    public <Q, S, E extends Throwable> Q buildLuceneLike(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, Boolean not) throws E;

    /**
     * @param lqp TODO
     * @return the sort field
     * @throws E 
     */
    public <Q, S, E extends Throwable> String getLuceneSortField(LuceneQueryParserAdaptor<Q, S, E> lqpa) throws E;
    
    /**
     * @return the field name
     * 
     */
    public String getLuceneFieldName();
}
