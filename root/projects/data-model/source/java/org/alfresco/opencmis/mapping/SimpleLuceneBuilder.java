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
package org.alfresco.opencmis.mapping;

import java.io.Serializable;

import org.alfresco.repo.search.impl.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.namespace.QName;

/**
 * Common support for lucene query building.
 * 
 * @author davidc
 */
public abstract class SimpleLuceneBuilder extends BaseLuceneBuilder
{
    /**
     * Construct
     */
    protected SimpleLuceneBuilder()
    {
        super();
    }

    protected abstract String getValueAsString(Serializable value);
    
    protected String getRangeMax()
    {
        return "\uFFFF";
    }
    
    protected String getRangeMin()
    {
        return "\u0000";
    }
    
    protected abstract DataTypeDefinition getInDataType();
    
    protected abstract QName getQNameForExists();

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneEquality(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        return lqpa.getFieldQuery(getLuceneFieldName(), getValueAsString(value), AnalysisMode.IDENTIFIER, luceneFunction);
    }
    
    @Override
    public <Q, S, E extends Throwable> Q buildLuceneExists(LuceneQueryParserAdaptor<Q, S, E> lqpa, Boolean not) throws E
    {
        if (not)
        {
            return lqpa.getFieldQuery("ISNULL", getQNameForExists().toString(), AnalysisMode.DEFAULT, LuceneFunction.FIELD);
        }
        else
        {
            return lqpa.getFieldQuery("ISNOTNULL", getQNameForExists().toString(), AnalysisMode.DEFAULT, LuceneFunction.FIELD);
        }
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        return lqpa.getRangeQuery(field, stringValue, getRangeMax(), false, true, AnalysisMode.IDENTIFIER, luceneFunction);
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        return lqpa.getRangeQuery(field, stringValue, getRangeMax(), true, true, AnalysisMode.IDENTIFIER, luceneFunction);
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLessThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        return lqpa.getRangeQuery(field, getRangeMin(), stringValue, true, false, AnalysisMode.IDENTIFIER, luceneFunction);
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLessThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        return lqpa.getRangeQuery(field, getRangeMin(), stringValue, true, true, AnalysisMode.IDENTIFIER, luceneFunction);
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLike(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, Boolean not) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);

        Q q =  lqpa.getLikeQuery(field, stringValue, AnalysisMode.IDENTIFIER);
        if (not)
        {
            q = lqpa.getNegatedQuery(q);
        }
        return q;
    }

    @Override
    public <Q, S, E extends Throwable> String getLuceneSortField(LuceneQueryParserAdaptor<Q, S, E> lqpa) throws E
    {
        return getLuceneFieldName();
    }
}
