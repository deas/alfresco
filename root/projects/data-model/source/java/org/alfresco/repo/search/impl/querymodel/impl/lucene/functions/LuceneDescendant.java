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
package org.alfresco.repo.search.impl.querymodel.impl.lucene.functions;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.QueryModelException;
import org.alfresco.repo.search.impl.querymodel.impl.functions.Descendant;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;

/**
 * @author andyh
 *
 */
public class LuceneDescendant extends Descendant implements LuceneQueryBuilderComponent
{

    /**
     * 
     */
    public LuceneDescendant()
    {
        super();
    }

    private StoreRef getStore(LuceneQueryBuilderContext luceneContext)
    {
    	ArrayList<StoreRef> stores = luceneContext.getLuceneQueryParser().getSearchParameters().getStores();
    	if(stores.size() < 1)
    	{
    		// default
    		return StoreRef.STORE_REF_WORKSPACE_SPACESSTORE;
    	}
    	return stores.get(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent#addComponent(org.apache.lucene.search.BooleanQuery,
     *      org.apache.lucene.search.BooleanQuery, org.alfresco.service.cmr.dictionary.DictionaryService,
     *      java.lang.String)
     */
    public Query addComponent(Set<String> selectors, Map<String, Argument> functionArgs, LuceneQueryBuilderContext luceneContext, FunctionEvaluationContext functionContext)
            throws ParseException
    {
        AbstractLuceneQueryParser lqp = luceneContext.getLuceneQueryParser();
        Argument argument = functionArgs.get(ARG_ANCESTOR);
        String id = (String) argument.getValue(functionContext);
        argument = functionArgs.get(ARG_SELECTOR);
        if(argument != null)
        {
            String selector = (String) argument.getValue(functionContext);
            if(!selectors.contains(selector))
            {
                throw new QueryModelException("Unkown selector "+selector); 
            }
        }
        else
        {
            if(selectors.size() > 1)
            {
                throw new QueryModelException("Selector must be specified for child constraint (IN_TREE) and join"); 
            }
        }

        NodeRef nodeRef;
        if(NodeRef.isNodeRef(id))
        {
            nodeRef = new NodeRef(id);
        }
        else
        {
        	// assume id is the node uuid e.g. for OpenCMIS
        	StoreRef storeRef = getStore(luceneContext);
        	nodeRef = new NodeRef(storeRef, id);
        }

        // Lucene world 
        if(functionContext.getNodeService() != null)
        {
            if(!functionContext.getNodeService().exists(nodeRef))
            {
                throw new QueryModelException("Object does not exist: "+id); 
            }
            Path path = functionContext.getNodeService().getPath(nodeRef);
            StringBuilder builder = new StringBuilder(path.toPrefixString(luceneContext.getNamespacePrefixResolver()));
            builder.append("//*");
            Query query = lqp.getFieldQuery("PATH", builder.toString());
            return query;
        }
        // SOLR
        else
        {
            Query query = lqp.getFieldQuery("ANCESTOR", nodeRef.toString());
            return query;
        }
        
    }
    
}
