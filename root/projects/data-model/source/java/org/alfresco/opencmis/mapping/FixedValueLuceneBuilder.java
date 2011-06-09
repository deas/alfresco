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
package org.alfresco.opencmis.mapping;

import java.io.Serializable;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.util.EqualsHelper;
import org.alfresco.util.SearchLanguageConversion;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Property lucene builder for fixed value mapping (eg to null, true, etc)
 * 
 * @author andyh
 */
public class FixedValueLuceneBuilder extends AbstractLuceneBuilder
{
    private Serializable value;

    /**
     * Construct
     * 
     * @param serviceRegistry
     * @param propertyName
     * @param value
     */
    public FixedValueLuceneBuilder(Serializable value)
    {
        super();
        this.value = value;
    }

    @Override
    public Query buildLuceneEquality(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        if (EqualsHelper.nullSafeEquals(value, value))
        {
            return new MatchAllDocsQuery();
        }
        else
        {
            return new TermQuery(new Term("NO_TOKENS", "__"));
        }
    }

    @Override
    public Query buildLuceneExists(AbstractLuceneQueryParser lqp, Boolean not) throws ParseException
    {
        if (not)
        {
            if (value == null)
            {
                return new MatchAllDocsQuery();
            }
            else
            {
                return new TermQuery(new Term("NO_TOKENS", "__"));
            }
        }
        else
        {
            if (value == null)
            {
                return new TermQuery(new Term("NO_TOKENS", "__"));
            }
            else
            {
                return new MatchAllDocsQuery();
            }
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public Query buildLuceneGreaterThan(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        if (value instanceof Comparable)
        {
            Comparable comparable = (Comparable) value;
            if (comparable.compareTo(value) > 0)
            {
                return new MatchAllDocsQuery();
            }
            else
            {
                return new TermQuery(new Term("NO_TOKENS", "__"));
            }
        }
        else
        {
            return new TermQuery(new Term("NO_TOKENS", "__"));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Query buildLuceneGreaterThanOrEquals(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        if (value instanceof Comparable)
        {
            Comparable comparable = (Comparable) value;
            if (comparable.compareTo(value) >= 0)
            {
                return new MatchAllDocsQuery();
            }
            else
            {
                return new TermQuery(new Term("NO_TOKENS", "__"));
            }
        }
        else
        {
            return new TermQuery(new Term("NO_TOKENS", "__"));
        }
    }

    @Override
    public Query buildLuceneIn(AbstractLuceneQueryParser lqp, Collection<Serializable> values, Boolean not, PredicateMode mode) throws ParseException
    {
        boolean in = false;
        for (Serializable value : values)
        {
            if (EqualsHelper.nullSafeEquals(value, value))
            {
                in = true;
                break;
            }
        }

        if (in == !not)
        {
            return new MatchAllDocsQuery();
        }
        else
        {
            return new TermQuery(new Term("NO_TOKENS", "__"));
        }
    }

    @Override
    public Query buildLuceneInequality(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        if (!EqualsHelper.nullSafeEquals(value, value))
        {
            return new MatchAllDocsQuery();
        }
        else
        {
            return new TermQuery(new Term("NO_TOKENS", "__"));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Query buildLuceneLessThan(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        if (value instanceof Comparable)
        {
            Comparable comparable = (Comparable) value;
            if (comparable.compareTo(value) < 0)
            {
                return new MatchAllDocsQuery();
            }
            else
            {
                return new TermQuery(new Term("NO_TOKENS", "__"));
            }
        }
        else
        {
            return new TermQuery(new Term("NO_TOKENS", "__"));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Query buildLuceneLessThanOrEquals(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        if (value instanceof Comparable)
        {
            Comparable comparable = (Comparable) value;
            if (comparable.compareTo(value) <= 0)
            {
                return new MatchAllDocsQuery();
            }
            else
            {
                return new TermQuery(new Term("NO_TOKENS", "__"));
            }
        }
        else
        {
            return new TermQuery(new Term("NO_TOKENS", "__"));
        }
    }

    @Override
    public Query buildLuceneLike(AbstractLuceneQueryParser lqp, Serializable value, Boolean not) throws ParseException
    {
        if (value != null)
        {
            boolean matches = false;

            Object converted = DefaultTypeConverter.INSTANCE.convert(value.getClass(), value);
            String asString = DefaultTypeConverter.INSTANCE.convert(String.class, converted);
            String regExpression = SearchLanguageConversion.convertSQLLikeToRegex(asString);
            Pattern pattern = Pattern.compile(regExpression);
            String target = DefaultTypeConverter.INSTANCE.convert(String.class, value);
            Matcher matcher = pattern.matcher(target);
            if (matcher.matches())
            {
                matches = true;
            }

            if (matches == !not)
            {
                return new MatchAllDocsQuery();
            }
            else
            {
                return new TermQuery(new Term("NO_TOKENS", "__"));
            }
        }
        else
        {
            return new TermQuery(new Term("NO_TOKENS", "__"));
        }
    }

    @Override
    public String getLuceneSortField(AbstractLuceneQueryParser lqp)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLuceneFieldName()
    {
        throw new UnsupportedOperationException();
    }

}
