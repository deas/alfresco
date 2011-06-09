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

import org.alfresco.opencmis.dictionary.CMISDictionaryService;
import org.alfresco.opencmis.dictionary.TypeDefinitionWrapper;
import org.alfresco.repo.search.impl.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;

/**
 * Get the CMIS object type id property
 * 
 * @author andyh
 */
public class BaseTypeIdLuceneBuilder extends AbstractLuceneBuilder
{
    private CMISDictionaryService dictionaryService;
    

    /**
     * Construct
     */
    public BaseTypeIdLuceneBuilder(CMISDictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    @Override
    public Query buildLuceneEquality(LuceneQueryParser lqp, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        return lqp.getFieldQuery("TYPE", getType(getValueAsString(value)), AnalysisMode.IDENTIFIER, luceneFunction);     
    }

    @Override
    public Query buildLuceneInequality(LuceneQueryParser lqp, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        return lqp.getDoesNotMatchFieldQuery("TYPE", getType(getValueAsString(value)), AnalysisMode.IDENTIFIER, luceneFunction);
    }
    
    @Override
    public Query buildLuceneIn(LuceneQueryParser lqp, Collection<Serializable> values, Boolean not, PredicateMode mode) throws ParseException
    {
        String field = "TYPE";
        
        // Check type conversion

       
        Collection<String> asStrings = DefaultTypeConverter.INSTANCE.convert(String.class, values);

        if (asStrings.size() == 0)
        {
            if (not)
            {
                return new MatchAllDocsQuery();
            }
            else
            {
                return new TermQuery(new Term("NO_TOKENS", "__"));
            }
        }
        else if (asStrings.size() == 1)
        {
            String value = asStrings.iterator().next();
            if (not)
            {
                return lqp.getDoesNotMatchFieldQuery(field, getType(value), AnalysisMode.IDENTIFIER, LuceneFunction.FIELD);
            }
            else
            {
                return lqp.getFieldQuery(field, getType(value), AnalysisMode.IDENTIFIER, LuceneFunction.FIELD);
            }
        }
        else
        {
            BooleanQuery booleanQuery = new BooleanQuery();
            if (not)
            {
                booleanQuery.add(new MatchAllDocsQuery(), Occur.MUST);
            }
            for (String value : asStrings)
            {
                Query any = lqp.getFieldQuery(field, getType(value), AnalysisMode.IDENTIFIER, LuceneFunction.FIELD);
                if (not)
                {
                    booleanQuery.add(any, Occur.MUST_NOT);
                }
                else
                {
                    booleanQuery.add(any, Occur.SHOULD);
                }
            }
            return booleanQuery;
        }
    }

    @Override
    public Query buildLuceneExists(LuceneQueryParser lqp, Boolean not) throws ParseException
    {
        if (not)
        {
            return new TermQuery(new Term("NO_TOKENS", "__"));
        }
        else
        { 
            return new MatchAllDocsQuery();
        }
    }
    
    private String getType(String tableName)
    {
        TypeDefinitionWrapper typeDef = dictionaryService.findTypeByQueryName(tableName);
        if (typeDef == null)
        {
            throw new CmisInvalidArgumentException("Unknwon type: " + tableName);
        }
        if(!typeDef.isBaseType())
        {
            throw new CmisInvalidArgumentException("Not a base type: " + tableName);
        }
        if(!typeDef.getTypeDefinition(false).isQueryable())
        {
            throw new CmisInvalidArgumentException("Type is not queryable: " + tableName);
        }
        return typeDef.getAlfrescoClass().toString();
    }
    
    private String getValueAsString(Serializable value)
    {
        String asString = DefaultTypeConverter.INSTANCE.convert(String.class, value);
        return asString;
    }
}
