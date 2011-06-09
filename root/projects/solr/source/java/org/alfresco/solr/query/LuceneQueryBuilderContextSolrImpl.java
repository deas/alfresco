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

import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.SolrLuceneAnalyser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;

/**
 * @author andyh
 */
public class LuceneQueryBuilderContextSolrImpl implements LuceneQueryBuilderContext
{
    private SolrQueryParser lqp;

    private NamespacePrefixResolver namespacePrefixResolver;

    /**
     * Context for building lucene queries
     * 
     * @param dictionaryService
     * @param namespacePrefixResolver
     * @param tenantService
     * @param searchParameters
     * @param config
     * @param indexReader
     */
    public LuceneQueryBuilderContextSolrImpl(DictionaryService dictionaryService, NamespacePrefixResolver namespacePrefixResolver, TenantService tenantService,
            SearchParameters searchParameters, MLAnalysisMode defaultSearchMLAnalysisMode, IndexReader indexReader, Analyzer defaultAnalyzer, AlfrescoSolrDataModel model)
    {
        SolrLuceneAnalyser analyzer = new SolrLuceneAnalyser(dictionaryService, searchParameters.getMlAnalaysisMode() == null ? defaultSearchMLAnalysisMode : searchParameters
                .getMlAnalaysisMode(), defaultAnalyzer, model);
        lqp = new SolrQueryParser(searchParameters.getDefaultFieldName(), analyzer);
        lqp.setDefaultOperator(AbstractLuceneQueryParser.OR_OPERATOR);
        lqp.setDictionaryService(dictionaryService);
        lqp.setNamespacePrefixResolver(namespacePrefixResolver);
        lqp.setTenantService(tenantService);
        lqp.setSearchParameters(searchParameters);
        lqp.setDefaultSearchMLAnalysisMode(defaultSearchMLAnalysisMode);
        lqp.setIndexReader(indexReader);
        lqp.setAllowLeadingWildcard(true);
        this.namespacePrefixResolver = namespacePrefixResolver;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext#getLuceneQueryParser()
     */
    public AbstractLuceneQueryParser getLuceneQueryParser()
    {
        return lqp;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext#getNamespacePrefixResolver()
     */
    public NamespacePrefixResolver getNamespacePrefixResolver()
    {
        return namespacePrefixResolver;
    }

}
