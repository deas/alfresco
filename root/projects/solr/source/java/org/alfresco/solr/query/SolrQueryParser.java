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
package org.alfresco.solr.query;

import org.alfresco.repo.search.impl.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.LuceneXPathHandler;
import org.alfresco.repo.search.impl.lucene.query.PathQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.CharStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParserTokenManager;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
import org.saxpath.SAXPathException;

import com.werken.saxpath.XPathReader;

/**
 * @author Andy
 *
 */
public class SolrQueryParser extends LuceneQueryParser
{
    
    
    @Override
    protected Query createPathQuery(String queryText, boolean withRepeats) throws SAXPathException
    {
        XPathReader reader = new XPathReader();
        SolrXPathHandler handler = new SolrXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse(queryText);
        SolrPathQuery pathQuery = handler.getQuery();
        pathQuery.setRepeats(withRepeats);
        return new SolrCachingPathQuery(pathQuery);
    }
    
    protected Query createQNameQuery(String queryText) throws SAXPathException
    {
        XPathReader reader = new XPathReader();
        SolrXPathHandler handler = new SolrXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse("//" + queryText);
        return handler.getQuery();
    }
    
    protected Query createPrimaryAssocTypeQNameQuery(String queryText) throws SAXPathException
    {
        XPathReader reader = new XPathReader();
        SolrXPathHandler handler = new SolrXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse("//" + queryText);
        SolrPathQuery pathQuery = handler.getQuery();
        pathQuery.setPathField(FIELD_PRIMARYASSOCTYPEQNAME);
        return pathQuery;
    }
    
    protected Query createAssocTypeQNameQuery(String queryText) throws SAXPathException
    {
        // This was broken and using only FIELD_PRIMARYASSOCTYPEQNAME
        // The field was also not indexed correctly.
        // We do both for backward compatability ...
        BooleanQuery booleanQuery = new BooleanQuery();
        
        XPathReader reader = new XPathReader();
        SolrXPathHandler handler = new SolrXPathHandler();
        handler.setNamespacePrefixResolver(namespacePrefixResolver);
        handler.setDictionaryService(dictionaryService);
        reader.setXPathHandler(handler);
        reader.parse("//" + queryText);
        SolrPathQuery pathQuery = handler.getQuery();
        pathQuery.setPathField(FIELD_ASSOCTYPEQNAME);
        
        booleanQuery.add(pathQuery, Occur.SHOULD);
        booleanQuery.add(createPrimaryAssocTypeQNameQuery(queryText), Occur.SHOULD);
        
        return booleanQuery;
    }

    
    /**
     * @param queryText
     * @return
     */
    protected Query createAclIdQuery(String queryText) throws ParseException
    {
        return getFieldQueryImpl(FIELD_ACLID, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }
    
    /**
     * @param queryText
     * @return
     */
    protected Query createOwnerQuery(String queryText) throws ParseException
    {
        return new SolrCachingOwnerQuery(queryText);
    }
    
    /**
     * @param queryText
     * @return
     */
    protected Query createReaderQuery(String queryText) throws ParseException
    {
        return new SolrCachingReaderQuery(queryText);
    }
    
    /**
     * @param queryText
     * @return
     */
    protected Query createAuthorityQuery(String queryText) throws ParseException
    {
       return new SolrCachingAuthorityQuery(queryText);
    }

    
    /**
     * @param arg0
     * @param arg1
     */
    public SolrQueryParser(String arg0, Analyzer arg1)
    {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public SolrQueryParser(CharStream arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public SolrQueryParser(QueryParserTokenManager arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

}
