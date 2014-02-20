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

import org.alfresco.opencmis.dictionary.CMISDictionaryService;
import org.alfresco.opencmis.dictionary.TypeDefinitionWrapper;
import org.alfresco.repo.search.impl.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;

/**
 * Lucene Builder for CMIS object type id property
 * 
 * @author andyh
 */
public class ObjectTypeIdLuceneBuilder extends BaseLuceneBuilder
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
    public <Q, S, E extends Throwable> Q buildLuceneEquality(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        TypeDefinitionWrapper type = cmisDictionaryService.findType(stringValue);
        return lqpa
                .getFieldQuery(field, type.getAlfrescoClass().toString(), AnalysisMode.IDENTIFIER, luceneFunction);
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneExists(LuceneQueryParserAdaptor<Q, S, E> lqpa, Boolean not) throws E
    {
        if (not)
        {
            return lqpa.getMatchNoneQuery();
        } else
        {
            return lqpa.getMatchAllQuery();
        }
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws E
    {
        throw new CmisInvalidArgumentException("Property " + PropertyIds.OBJECT_TYPE_ID + " can not be used in a 'greater than' comparison");
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws E
    {
        throw new CmisInvalidArgumentException("Property " + PropertyIds.OBJECT_TYPE_ID
                + " can not be used in a 'greater than or equals' comparison");
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneInequality(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws E
    {
        return lqpa.getNegatedQuery(buildLuceneEquality(lqpa, value, mode, luceneFunction));
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLessThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws E
    {
        throw new CmisInvalidArgumentException("Property " + PropertyIds.OBJECT_TYPE_ID + " can not be used in a 'less than' comparison");
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLessThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws E
    {
        throw new CmisInvalidArgumentException("Property " + PropertyIds.OBJECT_TYPE_ID + " can not be used in a 'less than or equals' comparison");
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLike(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, Boolean not) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(value);
        TypeDefinitionWrapper type = cmisDictionaryService.findType(stringValue);
        String typeQName = type.getAlfrescoClass().toString();

        Q q = lqpa.getLikeQuery(field, typeQName, AnalysisMode.IDENTIFIER);
        if (not)
        {
            q = lqpa.getNegatedQuery(q);
        } 
        return q;
    }

    @Override
    public <Q, S, E extends Throwable> String getLuceneSortField(LuceneQueryParserAdaptor<Q, S, E> lqpa)
    {
        return getLuceneFieldName();
    }
}
