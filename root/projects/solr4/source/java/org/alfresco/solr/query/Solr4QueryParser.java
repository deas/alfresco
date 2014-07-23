/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

import java.util.Collection;
import java.util.HashSet;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.apache.solr.search.SyntaxError;

/**
 * @author Andy
 *
 */
public class Solr4QueryParser extends QueryParser implements QueryConstants
{
    @SuppressWarnings("unused")
    private static Log s_logger = LogFactory.getLog(Solr4QueryParser.class);

    protected NamespacePrefixResolver namespacePrefixResolver;

    protected DictionaryService dictionaryService;

    private TenantService tenantService;

    private SearchParameters searchParameters;

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

    /**
     * @return
     */
    public SearchParameters getSearchParameters()
    {
       return searchParameters;
    }

    /**
     * @param searchParameters the searchParameters to set
     */
    public void setSearchParameters(SearchParameters searchParameters)
    {
        this.searchParameters = searchParameters;
    }

    
    
    /**
     * @return the namespacePrefixResolver
     */
    public NamespacePrefixResolver getNamespacePrefixResolver()
    {
        return namespacePrefixResolver;
    }

    /**
     * @param namespacePrefixResolver the namespacePrefixResolver to set
     */
    public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver)
    {
        this.namespacePrefixResolver = namespacePrefixResolver;
    }

    /**
     * @return the dictionaryService
     */
    public DictionaryService getDictionaryService()
    {
        return dictionaryService;
    }

    /**
     * @param dictionaryService the dictionaryService to set
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * @return the tenantService
     */
    public TenantService getTenantService()
    {
        return tenantService;
    }

    /**
     * @param tenantService the tenantService to set
     */
    public void setTenantService(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }

    /**
     * @param field
     * @param queryText
     * @param analysisMode
     * @param luceneFunction
     * @return
     * @throws SyntaxError 
     */
    public Query getFieldQuery(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws SyntaxError
    {
        if (field.equals(FIELD_CLASS))
        {
            ClassDefinition target = matchClassDefinition(queryText);
            if (target == null)
            {
                throw new SyntaxError("Invalid type: " + queryText);
            }
            return getFieldQuery(target.isAspect() ? FIELD_ASPECT : FIELD_TYPE, queryText, analysisMode, luceneFunction);
        }
        else if (field.equals(FIELD_TYPE))
        {
            return createTypeQuery(queryText, false);
        }
        else if (field.equals(FIELD_EXACTTYPE))
        {
            return createTypeQuery(queryText, true);
        }
        else if (field.equals(FIELD_ASPECT))
        {
            return createAspectQuery(queryText, false);
        }
        else if (field.equals(FIELD_EXACTASPECT))
        {
            return createAspectQuery(queryText, true);
        }
        throw new UnsupportedOperationException();
    }

    public Query getFieldQuery(String field, String queryText) throws SyntaxError
    {
        return getFieldQuery(field, queryText, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
    }
    
    
    protected Query createTypeQuery(String queryText, boolean exactOnly) throws SyntaxError
    {
        TypeDefinition target = matchTypeDefinition(queryText);
        if (target == null)
        {
            throw new SyntaxError("Invalid type: " + queryText);
        }
        if (exactOnly)
        {
            QName targetQName = target.getName();
            TermQuery termQuery = new TermQuery(new Term(FIELD_TYPE, targetQName.toString()));
            return termQuery;
        }
        else
        {
            Collection<QName> subclasses = dictionaryService.getSubTypes(target.getName(), true);
            BooleanQuery booleanQuery = new BooleanQuery();
            for (QName qname : subclasses)
            {
                TypeDefinition current = dictionaryService.getType(qname);
                if (target.getName().equals(current.getName()) || current.getIncludedInSuperTypeQuery())
                {
                    TermQuery termQuery = new TermQuery(new Term(FIELD_TYPE, qname.toString()));
                    if (termQuery != null)
                    {
                        booleanQuery.add(termQuery, Occur.SHOULD);
                    }
                }
            }
            return booleanQuery;
        }
    }
    
    protected Query createAspectQuery(String queryText, boolean exactOnly) throws SyntaxError
    {
        AspectDefinition target = matchAspectDefinition(queryText);
        if (target == null)
        {
            // failed to find the aspect in the dictionary
            throw new AlfrescoRuntimeException("Unknown aspect specified in query: " + queryText);
        }

        if (exactOnly)
        {
            QName targetQName = target.getName();
            TermQuery termQuery = new TermQuery(new Term(FIELD_ASPECT, targetQName.toString()));

            return termQuery;
        }
        else
        {
            Collection<QName> subclasses = dictionaryService.getSubAspects(target.getName(), true);

            BooleanQuery booleanQuery = new BooleanQuery();
            for (QName qname : subclasses)
            {
                AspectDefinition current = dictionaryService.getAspect(qname);
                if (target.getName().equals(current.getName()) || current.getIncludedInSuperTypeQuery())
                {
                    TermQuery termQuery = new TermQuery(new Term(FIELD_ASPECT, qname.toString()));
                    if (termQuery != null)
                    {
                        booleanQuery.add(termQuery, Occur.SHOULD);
                    }
                }
            }
            return booleanQuery;
        }

    }

    
    
    private TypeDefinition matchTypeDefinition(String string) throws SyntaxError
    {
        QName search = QName.createQName(expandQName(string));
        TypeDefinition typeDefinition = dictionaryService.getType(QName.createQName(expandQName(string)));
        QName match = null;
        if (typeDefinition == null)
        {
            for (QName definition : dictionaryService.getAllTypes())
            {
                if (definition.getNamespaceURI().equalsIgnoreCase(search.getNamespaceURI()))
                {
                    if (definition.getLocalName().equalsIgnoreCase(search.getLocalName()))
                    {
                        if (match == null)
                        {
                            match = definition;
                        }
                        else
                        {
                            throw new SyntaxError("Ambiguous data datype " + string);
                        }
                    }
                }
            }
        }
        else
        {
            return typeDefinition;
        }
        if (match == null)
        {
            return null;
        }
        else
        {
            return dictionaryService.getType(match);
        }
    }


    private String expandQName(String qnameString) throws SyntaxError
    {
        String fieldName = qnameString;
        // Check for any prefixes and expand to the full uri
        if (qnameString.charAt(0) != '{')
        {
            int colonPosition = qnameString.indexOf(':');
            if (colonPosition == -1)
            {
                // use the default namespace
                fieldName = "{" + searchParameters.getNamespace() + "}" + qnameString;
            }
            else
            {
                String prefix = qnameString.substring(0, colonPosition);
                String uri = matchURI(prefix);
                if (uri == null)
                {
                    fieldName = "{" + searchParameters.getNamespace() + "}" + qnameString;
                }
                else
                {
                    fieldName = "{" + uri + "}" + qnameString.substring(colonPosition + 1);
                }

            }
        }
        return fieldName;
    }
    
    private String matchURI(String prefix) throws SyntaxError
    {
        HashSet<String> prefixes = new HashSet<String>(namespacePrefixResolver.getPrefixes());
        if (prefixes.contains(prefix))
        {
            return namespacePrefixResolver.getNamespaceURI(prefix);
        }
        String match = null;
        for (String candidate : prefixes)
        {
            if (candidate.equalsIgnoreCase(prefix))
            {
                if (match == null)
                {
                    match = candidate;
                }
                else
                {

                    throw new SyntaxError("Ambiguous namespace prefix " + prefix);

                }
            }
        }
        if (match == null)
        {
            return null;
        }
        else
        {
            return namespacePrefixResolver.getNamespaceURI(match);
        }
    }

    private ClassDefinition matchClassDefinition(String string) throws SyntaxError
    {
        QName search = QName.createQName(expandQName(string));
        ClassDefinition classDefinition = dictionaryService.getClass(QName.createQName(expandQName(string)));
        QName match = null;
        if (classDefinition == null)
        {
            for (QName definition : dictionaryService.getAllTypes())
            {
                if (definition.getNamespaceURI().equalsIgnoreCase(search.getNamespaceURI()))
                {
                    if (definition.getLocalName().equalsIgnoreCase(search.getLocalName()))
                    {
                        if (match == null)
                        {
                            match = definition;
                        }
                        else
                        {
                            throw new SyntaxError("Ambiguous data datype " + string);
                        }
                    }
                }
            }
            for (QName definition : dictionaryService.getAllAspects())
            {
                if (definition.getNamespaceURI().equalsIgnoreCase(search.getNamespaceURI()))
                {
                    if (definition.getLocalName().equalsIgnoreCase(search.getLocalName()))
                    {
                        if (match == null)
                        {
                            match = definition;
                        }
                        else
                        {
                            throw new SyntaxError("Ambiguous data datype " + string);
                        }
                    }
                }
            }
        }
        else
        {
            return classDefinition;
        }
        if (match == null)
        {
            return null;
        }
        else
        {
            return dictionaryService.getClass(match);
        }
    }
    
    private AspectDefinition matchAspectDefinition(String string) throws SyntaxError
    {
        QName search = QName.createQName(expandQName(string));
        AspectDefinition aspectDefinition = dictionaryService.getAspect(QName.createQName(expandQName(string)));
        QName match = null;
        if (aspectDefinition == null)
        {
            for (QName definition : dictionaryService.getAllAspects())
            {
                if (definition.getNamespaceURI().equalsIgnoreCase(search.getNamespaceURI()))
                {
                    if (definition.getLocalName().equalsIgnoreCase(search.getLocalName()))
                    {
                        if (match == null)
                        {
                            match = definition;
                        }
                        else
                        {
                            throw new SyntaxError("Ambiguous data datype " + string);
                        }
                    }
                }
            }
        }
        else
        {
            return aspectDefinition;
        }
        if (match == null)
        {
            return null;
        }
        else
        {
            return dictionaryService.getAspect(match);
        }
    }

}
