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

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Lucene Builder for CMIS object id property.
 * 
 * @author andyh
 * @author dward
 */
public class ObjectIdLuceneBuilder extends AbstractLuceneBuilder
{
    private DictionaryService dictionaryService;
    
	/**
     * Construct
     * 
     * @param serviceRegistry
     */
    public ObjectIdLuceneBuilder(DictionaryService dictionaryService)
    {
    	this.dictionaryService = dictionaryService;
    }

    @Override
    public String getLuceneFieldName()
    {
        return "ID";
    }

    private StoreRef getStore(AbstractLuceneQueryParser lqp)
    {
    	ArrayList<StoreRef> stores = lqp.getSearchParameters().getStores();
    	if(stores.size() < 1)
    	{
    		// default
    		return StoreRef.STORE_REF_WORKSPACE_SPACESSTORE;
    	}
    	return stores.get(0);
    }

    private String getValueAsString(AbstractLuceneQueryParser lqp, Serializable value)
    {
    	String nodeRefStr = null;
        if(!NodeRef.isNodeRef((String)value))
        {
            // assume the object id is the node guid
            StoreRef storeRef = getStore(lqp);
        	nodeRefStr = storeRef.toString() + "/" + (String)value;
        }
        else
        {
        	nodeRefStr = (String)value;
        }

        Object converted = DefaultTypeConverter.INSTANCE.convert(dictionaryService.getDataType(DataTypeDefinition.NODE_REF), nodeRefStr);
        String asString = DefaultTypeConverter.INSTANCE.convert(String.class, converted);
        return asString;
    }

    @Override
    public Query buildLuceneEquality(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(lqp, value);
        String[] split = stringValue.split(";");
        if(split.length == 1)
        {
            return lqp.getFieldQuery(field, stringValue, AnalysisMode.IDENTIFIER, luceneFunction);
        }
        else
        {
            if(split[1].equalsIgnoreCase("PWC"))
            {
                return new TermQuery(new Term("NO_TOKENS", "__"));
            }
            
            BooleanQuery query = new BooleanQuery();
            BooleanQuery part1 = new BooleanQuery();
            part1.add(lqp.getFieldQuery(field, split[0], AnalysisMode.IDENTIFIER, luceneFunction), Occur.MUST);
            part1.add(lqp.getFieldQuery("@"+ContentModel.PROP_VERSION_LABEL.toString(), split[1], AnalysisMode.IDENTIFIER, luceneFunction), Occur.MUST);
            query.add(part1, Occur.SHOULD);
            
            if(split[1].equals("1.0"))
            {
                BooleanQuery part2 = new BooleanQuery();
                part2.add(lqp.getFieldQuery(field, split[0], AnalysisMode.IDENTIFIER, luceneFunction), Occur.MUST);
                part2.add(lqp.getFieldQuery(AbstractLuceneQueryParser.FIELD_ASPECT, ContentModel.ASPECT_VERSIONABLE.toString(), AnalysisMode.IDENTIFIER, luceneFunction), Occur.MUST_NOT);
                query.add(part2, Occur.SHOULD);
            }
            return query;
        }
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
        throw new CmisInvalidArgumentException("Property " + PropertyIds.OBJECT_ID + " can not be used in a 'greater than' comparison");
    }

    @Override
    public Query buildLuceneGreaterThanOrEquals(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws ParseException
    {
        throw new CmisInvalidArgumentException("Property " + PropertyIds.OBJECT_ID
                + " can not be used in a 'greater than or equals' comparison");
    }

    @Override
    public Query buildLuceneIn(AbstractLuceneQueryParser lqp, Collection<Serializable> values, Boolean not, PredicateMode mode) throws ParseException
    {

        // Check type conversion

//        @SuppressWarnings("unused")
//        Object converted = DefaultTypeConverter.INSTANCE.convert(dictionaryService.getDataType(DataTypeDefinition.NODE_REF), values);
//        Collection<String> asStrings = DefaultTypeConverter.INSTANCE.convert(String.class, values);

        if (values.size() == 0)
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
        else if (values.size() == 1)
        {
            Serializable value = values.iterator().next();
            if (not)
            {
                BooleanQuery booleanQuery = new BooleanQuery();
                booleanQuery.add(new MatchAllDocsQuery(), Occur.MUST);
                booleanQuery.add(buildLuceneEquality(lqp, value, mode, LuceneFunction.FIELD), Occur.MUST_NOT);
                return booleanQuery;
            }
            else
            {
                return buildLuceneEquality(lqp, value, mode, LuceneFunction.FIELD);
            }
        }
        else
        {
            BooleanQuery booleanQuery = new BooleanQuery();
            if (not)
            {
                booleanQuery.add(new MatchAllDocsQuery(), Occur.MUST);
            }
            for (Serializable value : values)
            {
                Query any = buildLuceneEquality(lqp, value, mode, LuceneFunction.FIELD);
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
    public Query buildLuceneInequality(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(new MatchAllDocsQuery(), Occur.MUST);
        String stringValue = getValueAsString(lqp, value);
        booleanQuery.add(buildLuceneEquality(lqp, stringValue, mode, LuceneFunction.FIELD), Occur.MUST_NOT);
        return booleanQuery;
    }

    @Override
    public Query buildLuceneLessThan(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws ParseException
    {
        throw new CmisInvalidArgumentException("Property " + PropertyIds.OBJECT_ID + " can not be used in a 'less than' comparison");
    }

    @Override
    public Query buildLuceneLessThanOrEquals(AbstractLuceneQueryParser lqp, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws ParseException
    {
        throw new CmisInvalidArgumentException("Property " + PropertyIds.OBJECT_ID + " can not be used in a 'less than or equals' comparison");
    }

    @Override
    public Query buildLuceneLike(AbstractLuceneQueryParser lqp, Serializable value, Boolean not) throws ParseException
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(lqp, value);

        if (not)
        {
            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add(new MatchAllDocsQuery(), Occur.MUST);
            booleanQuery.add(lqp.getLikeQuery(field, stringValue, AnalysisMode.IDENTIFIER), Occur.MUST_NOT);
            return booleanQuery;
        } else
        {
            return lqp.getLikeQuery(field, stringValue, AnalysisMode.IDENTIFIER);
        }
    }

    @Override
    public String getLuceneSortField(AbstractLuceneQueryParser lqp)
    {
        return getLuceneFieldName();
    }

}
