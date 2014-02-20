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

import org.alfresco.repo.search.impl.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;


/**
 * Lucene Builder for CMIS parent property
 * 
 * @author andyh
 * 
 */
public class ParentLuceneBuilder extends BaseLuceneBuilder
{
    private DictionaryService dictionaryService;

	/**
     * Construct
     * 
     * @param serviceRegistry
     */
    public ParentLuceneBuilder(DictionaryService dictionaryService)
    {
        super();
		this.dictionaryService = dictionaryService;
    }

    private <Q, S, E extends Throwable> StoreRef getStore(LuceneQueryParserAdaptor<Q, S, E> lqpa)
    {
    	ArrayList<StoreRef> stores = lqpa.getSearchParameters().getStores();
    	if(stores.size() < 1)
    	{
    		// default
    		return StoreRef.STORE_REF_WORKSPACE_SPACESSTORE;
    	}
    	return stores.get(0);
    }

    @Override
    public String getLuceneFieldName()
    {
        return "PARENT";
    }
    
    private <Q, S, E extends Throwable> String getValueAsString(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value)
    {
    	String nodeRefStr = (String)value;
        if(!NodeRef.isNodeRef((String)value))
        {
            // assume the value (object id) is the node guid
            StoreRef storeRef = getStore(lqpa);
        	nodeRefStr = storeRef.toString() + "/" + (String)value;
        }

        Object converted = DefaultTypeConverter.INSTANCE.convert(dictionaryService.getDataType(DataTypeDefinition.NODE_REF), nodeRefStr);
        String asString = DefaultTypeConverter.INSTANCE.convert(String.class, converted);
        return asString;
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneEquality(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode,
            LuceneFunction luceneFunction) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(lqpa, value);
        return lqpa.getFieldQuery(field, stringValue, AnalysisMode.IDENTIFIER, luceneFunction);
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneExists(LuceneQueryParserAdaptor<Q, S, E> lqpa, Boolean not) throws E
    {
        if (not)
        {
            return lqpa.getFieldQuery("ISROOT", "T", AnalysisMode.IDENTIFIER, LuceneFunction.FIELD);
            //return new TermQuery(new Term("ISROOT", "T"));
        } else
        {
            return lqpa.getNegatedQuery(lqpa.getFieldQuery("ISROOT", "T", AnalysisMode.IDENTIFIER, LuceneFunction.FIELD));
            //return new MatchAllDocsQuery();
        }
    }

    @Override
    public <Q, S, E extends Throwable> Q buildLuceneLike(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, Boolean not) throws E
    {
        String field = getLuceneFieldName();
        String stringValue = getValueAsString(lqpa, value);

        Q q = lqpa.getLikeQuery(field, stringValue, AnalysisMode.IDENTIFIER);
        if (not)
        {
            return lqpa.getNegatedQuery(q);
        }
        return q;
    }

    @Override
    public <Q, S, E extends Throwable> String getLuceneSortField(LuceneQueryParserAdaptor<Q, S, E> lqpa)
    {
        return getLuceneFieldName();
    }
}
