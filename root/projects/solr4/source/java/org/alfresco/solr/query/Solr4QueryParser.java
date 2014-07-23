/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.CharStream;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserTokenManager;
import org.apache.lucene.util.Version;

/**
 * @author Andy
 *
 */
public class Solr4QueryParser extends QueryParser
{

    /**
     * @param stream
     */
    public Solr4QueryParser(CharStream stream)
    {
        super(stream);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param tm
     */
    public Solr4QueryParser(QueryParserTokenManager tm)
    {
        super(tm);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param matchVersion
     * @param f
     * @param a
     */
    public Solr4QueryParser(Version matchVersion, String f, Analyzer a)
    {
        super(matchVersion, f, a);
        // TODO Auto-generated constructor stub
    }

}
