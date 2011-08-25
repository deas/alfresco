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
import java.util.ArrayList;
import java.util.Collection;

import org.alfresco.opencmis.dictionary.CMISDictionaryService;
import org.alfresco.opencmis.dictionary.TypeDefinitionWrapper;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;

/**
 * Lucene Builder for CMIS object type id property
 * 
 * @author andyh
 */
public class ObjectTypeIdLuceneBuilder extends AbstractLuceneBuilder
{
    private CMISDictionaryService cmisDictionaryService;
    
    /**
     * Construct
     * 
     * @param serviceRegistry
     */
    public ObjectTypeIdLuceneBuilder(CMISDictionaryService cmisDictionaryService)
    {
        this.cmisDictionaryService = cmisDictionaryService;
    }

    @Override
    public String getLuceneFieldName()
    {
        return "EXACTTYPE";
    }

    private String getValueAsString(Serializable value)
    {
        // Object converted =
        // DefaultTypeConverter.INSTANCE.convert(getServiceRegistry().getDictionaryService().getDataType(DataTypeDefinition.QNAME),
        // value);
        String asString = DefaultTypeConverter.INSTANCE.convert(String.class, value);
        return asString;
    }

    @Override
    public Query buildLuceneEquality(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws ParseException
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        TypeDefinitionWrapper type = cmisDictionaryService.findType(stringValue);
        return lqp
                .getFieldQuery(field, type.getAlfrescoClass().toString(), AnalysisMode.IDENTIFIER, luceneFunction);
    }

    @Override
    public Query buildLuceneExists(AbstractLuceneQueryParser lqp, Boolean not) throws ParseException
    {
        if (not)
        {
            return new TermQuery(new Term("NO_TOKENS", "__"));
        } else
        {
            return new MatchAllDocsQuery();
        }
    }

    @Override
    public Query buildLuceneGreaterThan(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws ParseException
    {
        throw new CmisInvalidArgumentException("Property " + PropertyIds.OBJECT_TYPE_ID + " can not be used in a 'greater than' comparison");
    }

    @Override
    public Query buildLuceneGreaterThanOrEquals(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws ParseException
    {
        throw new CmisInvalidArgumentException("Property " + PropertyIds.OBJECT_TYPE_ID
                + " can not be used in a 'greater than or equals' comparison");
    }

    @Override
    public Query buildLuceneIn(AbstractLuceneQueryParser lqp, Collection<Serializable> values, Boolean not, PredicateMode mode)
            throws ParseException
    {
        String field = getLuceneFieldName();

        Collection<String> asStrings = new ArrayList<String>(values.size());
        for (Serializable value : values)
        {
            String stringValue = getValueAsString(value);
            TypeDefinitionWrapper type = cmisDictionaryService.findType(stringValue);
            asStrings.add(type.getAlfrescoClass().toString());
        }

        if (asStrings.size() == 0)
        {
            if (not)
            {
                return new MatchAllDocsQuery();
            } else
            {
                return new TermQuery(new Term("NO_TOKENS", "__"));
            }
        } else if (asStrings.size() == 1)
        {
            String value = asStrings.iterator().next();
            if (not)
            {
                return lqp.getDoesNotMatchFieldQuery(field, value, AnalysisMode.IDENTIFIER, LuceneFunction.FIELD);
            } else
            {
                return lqp.getFieldQuery(field, value, AnalysisMode.IDENTIFIER, LuceneFunction.FIELD);
            }
        } else
        {
            BooleanQuery booleanQuery = new BooleanQuery();
            if (not)
            {
                booleanQuery.add(new MatchAllDocsQuery(), Occur.MUST);
            }
            for (String value : asStrings)
            {
                Query any = lqp.getFieldQuery(field, value, AnalysisMode.IDENTIFIER, LuceneFunction.FIELD);
                if (not)
                {
                    booleanQuery.add(any, Occur.MUST_NOT);
                } else
                {
                    booleanQuery.add(any, Occur.SHOULD);
                }
            }
            return booleanQuery;
        }
    }

    @Override
    public Query buildLuceneInequality(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws ParseException
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        TypeDefinitionWrapper type = cmisDictionaryService.findType(stringValue);
        return lqp.getDoesNotMatchFieldQuery(field, type.getAlfrescoClass().toString(), AnalysisMode.IDENTIFIER,
                luceneFunction);
    }

    @Override
    public Query buildLuceneLessThan(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws ParseException
    {
        throw new CmisInvalidArgumentException("Property " + PropertyIds.OBJECT_TYPE_ID + " can not be used in a 'less than' comparison");
    }

    @Override
    public Query buildLuceneLessThanOrEquals(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws ParseException
    {
        throw new CmisInvalidArgumentException("Property " + PropertyIds.OBJECT_TYPE_ID + " can not be used in a 'less than or equals' comparison");
    }

    @Override
    public Query buildLuceneLike(AbstractLuceneQueryParser lqp, Serializable value, Boolean not) throws ParseException
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        TypeDefinitionWrapper type = cmisDictionaryService.findType(stringValue);
        String typeQName = type.getAlfrescoClass().toString();

        if (not)
        {
            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add(new MatchAllDocsQuery(), Occur.MUST);
            booleanQuery.add(lqp.getLikeQuery(field, typeQName, AnalysisMode.IDENTIFIER), Occur.MUST_NOT);
            return booleanQuery;
        } else
        {
            return lqp.getLikeQuery(field, typeQName, AnalysisMode.IDENTIFIER);
        }
    }

    @Override
    public String getLuceneSortField(AbstractLuceneQueryParser lqp)
    {
        return getLuceneFieldName();
    }
}
