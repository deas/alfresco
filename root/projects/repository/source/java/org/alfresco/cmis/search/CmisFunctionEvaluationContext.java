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
package org.alfresco.cmis.search;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.alfresco.cmis.CMISCardinalityEnum;
import org.alfresco.cmis.CMISDictionaryModel;
import org.alfresco.cmis.CMISDictionaryService;
import org.alfresco.cmis.CMISPropertyDefinition;
import org.alfresco.cmis.CMISQueryException;
import org.alfresco.cmis.CMISScope;
import org.alfresco.cmis.CMISTypeDefinition;
import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.search.impl.querymodel.FunctionArgument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;
import org.alfresco.repo.search.impl.querymodel.QueryModelException;
import org.alfresco.repo.search.impl.querymodel.Selector;
import org.alfresco.repo.search.impl.querymodel.impl.functions.Lower;
import org.alfresco.repo.search.impl.querymodel.impl.functions.Upper;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;

/**
 * @author andyh
 */
@SuppressWarnings("deprecation")
public class CmisFunctionEvaluationContext implements FunctionEvaluationContext
{
    private static HashSet<String> EXPOSED_FIELDS = new HashSet<String>();

    public static CMISScope[] STRICT_SCOPES = new CMISScope[] { CMISScope.DOCUMENT, CMISScope.FOLDER };

    public static CMISScope[] ALFRESCO_SCOPES = new CMISScope[] { CMISScope.DOCUMENT, CMISScope.FOLDER, CMISScope.POLICY };
    
    private Map<String, NodeRef> nodeRefs;

    private Map<String, Float> scores;

    private NodeService nodeService;

    private CMISDictionaryService cmisDictionaryService;
    
    private CMISScope[] validScopes;

    private Float score;

    static
    {
        EXPOSED_FIELDS.add(QueryConstants.FIELD_PATH);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_TEXT);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_ID);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_ISROOT);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_ISNODE);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_TX);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_PARENT);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_PRIMARYPARENT);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_QNAME);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_CLASS);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_TYPE);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_EXACTTYPE);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_ASPECT);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_EXACTASPECT);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_ALL);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_ISUNSET);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_ISNULL);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_ISNOTNULL);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_FTSSTATUS);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_ASSOCTYPEQNAME);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_PRIMARYASSOCTYPEQNAME);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_DBID);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_TAG);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_TENANT);
        EXPOSED_FIELDS.add(QueryConstants.FIELD_ANCESTOR);
        
        
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.ANY.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.ASSOC_REF.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.BOOLEAN.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.CATEGORY.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.CHILD_ASSOC_REF.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.CONTENT.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.DATE.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.DATETIME.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.DOUBLE.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.FLOAT.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.INT.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.LOCALE.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.LONG.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.MLTEXT.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.NODE_REF.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.PATH.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.PERIOD.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.QNAME.getLocalName());
        EXPOSED_FIELDS.add("d:"+DataTypeDefinition.TEXT.getLocalName());
    }

    /**
     * @param nodeRefs
     *            the nodeRefs to set
     */
    public void setNodeRefs(Map<String, NodeRef> nodeRefs)
    {
        this.nodeRefs = nodeRefs;
    }

    /**
     * @param scores
     *            the scores to set
     */
    public void setScores(Map<String, Float> scores)
    {
        this.scores = scores;
    }

    /**
     * @param nodeService
     *            the nodeService to set
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * @param cmisDictionaryService
     *            the cmisDictionaryService to set
     */
    public void setCmisDictionaryService(CMISDictionaryService cmisDictionaryService)
    {
        this.cmisDictionaryService = cmisDictionaryService;
    }
    
    /**
     * @param validScopes
     *          the valid scopes to set 
     */
    public void setValidScopes(CMISScope[] validScopes)
    {
        this.validScopes = validScopes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#getNodeRefs()
     */
    public Map<String, NodeRef> getNodeRefs()
    {
        return nodeRefs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#getNodeService()
     */
    public NodeService getNodeService()
    {
        return nodeService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#getProperty(org.alfresco.service.cmr.repository.NodeRef,
     *      org.alfresco.service.namespace.QName)
     */
    public Serializable getProperty(NodeRef nodeRef, String propertyName)
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        return propertyDef.getPropertyAccessor().getValue(nodeRef);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#getScores()
     */
    public Map<String, Float> getScores()
    {
        return scores;
    }

    /**
     * @return the score
     */
    public Float getScore()
    {
        return score;
    }

    /**
     * @param score
     *            the score to set
     */
    public void setScore(Float score)
    {
        this.score = score;
    }

    public <Q, S, E extends Throwable> Q buildLuceneEquality(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        return propertyDef.getPropertyLuceneBuilder().buildLuceneEquality(lqpa, value, mode, luceneFunction);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#buildLuceneExists(org.alfresco.repo.search.impl.lucene.LuceneQueryParser,
     *      org.alfresco.service.namespace.QName, java.lang.Boolean)
     */
    public <Q, S, E extends Throwable> Q buildLuceneExists(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Boolean not) throws E
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        return propertyDef.getPropertyLuceneBuilder().buildLuceneExists(lqpa, not);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#buildLuceneGreaterThan(org.alfresco.repo.search.impl.lucene.LuceneQueryParser,
     *      org.alfresco.service.namespace.QName, java.io.Serializable,
     *      org.alfresco.repo.search.impl.querymodel.PredicateMode)
     */
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        return propertyDef.getPropertyLuceneBuilder().buildLuceneGreaterThan(lqpa, value, mode, luceneFunction);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#buildLuceneGreaterThanOrEquals(org.alfresco.repo.search.impl.lucene.LuceneQueryParser,
     *      org.alfresco.service.namespace.QName, java.io.Serializable,
     *      org.alfresco.repo.search.impl.querymodel.PredicateMode)
     */
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        return propertyDef.getPropertyLuceneBuilder().buildLuceneGreaterThanOrEquals(lqpa, value, mode, luceneFunction);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#buildLuceneIn(org.alfresco.repo.search.impl.lucene.LuceneQueryParser,
     *      org.alfresco.service.namespace.QName, java.util.Collection, java.lang.Boolean,
     *      org.alfresco.repo.search.impl.querymodel.PredicateMode)
     */
    public <Q, S, E extends Throwable> Q buildLuceneIn(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Collection<Serializable> values, Boolean not, PredicateMode mode) throws E
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        return propertyDef.getPropertyLuceneBuilder().buildLuceneIn(lqpa, values, not, mode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#buildLuceneInequality(org.alfresco.repo.search.impl.lucene.LuceneQueryParser,
     *      org.alfresco.service.namespace.QName, java.io.Serializable,
     *      org.alfresco.repo.search.impl.querymodel.PredicateMode)
     */
    public <Q, S, E extends Throwable> Q buildLuceneInequality(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        return propertyDef.getPropertyLuceneBuilder().buildLuceneInequality(lqpa, value, mode, luceneFunction);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#buildLuceneLessThan(org.alfresco.repo.search.impl.lucene.LuceneQueryParser,
     *      org.alfresco.service.namespace.QName, java.io.Serializable,
     *      org.alfresco.repo.search.impl.querymodel.PredicateMode)
     */
    public <Q, S, E extends Throwable> Q buildLuceneLessThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        return propertyDef.getPropertyLuceneBuilder().buildLuceneLessThan(lqpa, value, mode, luceneFunction);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#buildLuceneLessThanOrEquals(org.alfresco.repo.search.impl.lucene.LuceneQueryParser,
     *      org.alfresco.service.namespace.QName, java.io.Serializable,
     *      org.alfresco.repo.search.impl.querymodel.PredicateMode)
     */
    public <Q, S, E extends Throwable> Q buildLuceneLessThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        return propertyDef.getPropertyLuceneBuilder().buildLuceneLessThanOrEquals(lqpa, value, mode, luceneFunction);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#buildLuceneLike(org.alfresco.repo.search.impl.lucene.LuceneQueryParser,
     *      org.alfresco.service.namespace.QName, java.io.Serializable, java.lang.Boolean)
     */
    public <Q, S, E extends Throwable> Q buildLuceneLike(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName, Serializable value, Boolean not) throws E
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        return propertyDef.getPropertyLuceneBuilder().buildLuceneLike(lqpa, value, not);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#getLuceneSortField(org.alfresco.service.namespace.QName)
     */
    public <Q, S, E extends Throwable> String getLuceneSortField(LuceneQueryParserAdaptor<Q, S, E> lqpa, String propertyName) throws E
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        return propertyDef.getPropertyLuceneBuilder().getLuceneSortField(lqpa);
    }

    
    public boolean isObjectId(String propertyName)
    {
        return CMISDictionaryModel.PROP_OBJECT_ID.equalsIgnoreCase(propertyName);
    }

    public boolean isOrderable(String fieldName)
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(fieldName, null);
        if(propertyDef == null)
        {
            return false;
        }
        else
        {
            return propertyDef.isOrderable();
        }
    }

    public boolean isQueryable(String fieldName)
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(fieldName, null);
        if(propertyDef == null)
        {
            return true;
        }
        else
        {
            return propertyDef.isQueryable();
        }
    }

    public String getLuceneFieldName(String propertyName)
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        if(propertyDef != null)
        {
           return propertyDef.getPropertyLuceneBuilder().getLuceneFieldName();
        }
        else
        {
            // TODO: restrict to supported "special" fields
            return propertyName;
        }
    }
    
    public LuceneFunction getLuceneFunction(FunctionArgument functionArgument)
    {
        if (functionArgument == null)
        {
            return LuceneFunction.FIELD;
        }
        else
        {
            String functionName = functionArgument.getFunction().getName();
            if (functionName.equals(Upper.NAME))
            {
                return LuceneFunction.UPPER;
            }
            else if (functionName.equals(Lower.NAME))
            {
                return LuceneFunction.LOWER;
            }
            else
            {
                throw new QueryModelException("Unsupported function: " + functionName);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#checkFieldApplies(org.alfresco.service.namespace.QName, java.lang.String)
     */
    public void checkFieldApplies(Selector selector, String propertyName)
    {
        CMISPropertyDefinition propDef = cmisDictionaryService.findPropertyByQueryName(propertyName);
        if (propDef == null)
        {
            if (EXPOSED_FIELDS.contains(propertyName))
            {
                return;
            }
            else
            {
                throw new CMISQueryException("Unknown column/property " + propertyName);
            }
        }
        
        CMISTypeDefinition typeDef = cmisDictionaryService.findTypeForClass(selector.getType(), validScopes);
        if (typeDef == null)
        {
            throw new CMISQueryException("Type unsupported in CMIS queries: " + selector.getAlias());
        }

        // Check column/property applies to selector/type

        if (!typeDef.getPropertyDefinitions().containsKey(propDef.getPropertyId().getId()))
        {
            throw new CMISQueryException("Invalid column for " + typeDef.getQueryName() + "." + propertyName);
        }
        
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#isMultiValued(java.lang.String)
     */
    public boolean isMultiValued(String propertyName)
    {
        CMISPropertyDefinition propDef = cmisDictionaryService.findPropertyByQueryName(propertyName);
        if (propDef == null)
        {
            throw new CMISQueryException("Unknown column/property " + propertyName);
        }
        return propDef.getCardinality() == CMISCardinalityEnum.MULTI_VALUED;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#getAlfrescoPropertyName(java.lang.String)
     */
    @Override
    public String getAlfrescoPropertyName(String propertyName)
    {
        CMISPropertyDefinition propertyDef = cmisDictionaryService.findProperty(propertyName, null);
        if(propertyDef != null)
        {
            QName mapped =  propertyDef.getPropertyAccessor().getMappedProperty();
            if(mapped == null)
            {
                return propertyName;
            }
            else
            {
                return mapped.toString();
            }
        }
        else
        {
            throw new CmisInvalidArgumentException("Unknown column/property " + propertyName);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#getAlfrescoTypeName(java.lang.String)
     */
    @Override
    public String getAlfrescoTypeName(String typeName)
    {
        throw new CMISQueryException("Unsupported");
    }
    
}
