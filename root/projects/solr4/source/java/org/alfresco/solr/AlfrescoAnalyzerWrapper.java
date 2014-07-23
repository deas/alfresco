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
package org.alfresco.solr;

import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.analysis.MLAnalayser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.solr.schema.IndexSchema;

/**
 * Wraps SOLR access to for localising tokens
 * As analysers are cached, and anylysers themselves cache token streams we have to be able to switch locales 
 * inside the MLAnalyser.  
 * 
 * @author Andy
 *
 */
public class AlfrescoAnalyzerWrapper extends AnalyzerWrapper
{
    IndexSchema schema;
    
    /**
     * @param reuseStrategy
     */
    public AlfrescoAnalyzerWrapper(IndexSchema schema)
    {
        super(Analyzer.PER_FIELD_REUSE_STRATEGY);
        this.schema = schema;
    }

    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.AnalyzerWrapper#getWrappedAnalyzer(java.lang.String)
     */
    @Override
    protected Analyzer getWrappedAnalyzer(String fieldName)
    {
        if(fieldName.contains("@l_@"))
        {
            return new MLAnalayser(MLAnalysisMode.EXACT_LANGUAGE, schema);
        }
        else if(fieldName.contains("@lt@"))
        {
            return new MLAnalayser(MLAnalysisMode.EXACT_LANGUAGE, schema);
        }
        else
        {
            return schema.getFieldTypeByName("text___").getAnalyzer();
        }
    }

}
