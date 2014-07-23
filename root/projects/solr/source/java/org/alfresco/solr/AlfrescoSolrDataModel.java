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
package org.alfresco.solr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.opencmis.dictionary.CMISAbstractDictionaryService;
import org.alfresco.opencmis.dictionary.CMISDictionaryService;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.opencmis.dictionary.QNameFilter;
import org.alfresco.opencmis.search.CMISQueryOptions;
import org.alfresco.opencmis.search.CMISQueryOptions.CMISQueryMode;
import org.alfresco.opencmis.search.CMISQueryParser;
import org.alfresco.opencmis.search.CmisFunctionEvaluationContext;
import org.alfresco.repo.cache.MemoryCache;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.DictionaryDAOImpl;
import org.alfresco.repo.dictionary.DictionaryRegistry;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2ModelDiff;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.repo.i18n.StaticMessageLookup;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.repo.search.impl.parsers.AlfrescoFunctionEvaluationContext;
import org.alfresco.repo.search.impl.parsers.FTSParser;
import org.alfresco.repo.search.impl.parsers.FTSQueryParser;
import org.alfresco.repo.search.impl.querymodel.Constraint;
import org.alfresco.repo.search.impl.querymodel.Ordering;
import org.alfresco.repo.search.impl.querymodel.QueryModelFactory;
import org.alfresco.repo.search.impl.querymodel.QueryOptions.Connective;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilder;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryModelFactory;
import org.alfresco.repo.tenant.SingleTServiceImpl;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AlfrescoClientDataModelServicesFactory.DictionaryKey;
import org.alfresco.solr.client.AlfrescoModel;
import org.alfresco.solr.query.LuceneQueryBuilderContextSolrImpl;
import org.alfresco.solr.query.SolrQueryParser;
import org.alfresco.util.ISO9075;
import org.alfresco.util.Pair;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.CapabilityJoin;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.Sorting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author Andy TODO: Dual tokenisation support?
 */
public class AlfrescoSolrDataModel
{
    protected final static Logger log = LoggerFactory.getLogger(AlfrescoSolrDataModel.class);

    private static HashMap<String, AlfrescoSolrDataModel> models = new HashMap<String, AlfrescoSolrDataModel>();

    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static HashMap<String, NonDictionaryField> nonDictionaryFields = new HashMap<String, NonDictionaryField>();

    public static HashMap<String, NonDictionaryField> additionalContentFields = new HashMap<String, NonDictionaryField>();

    public static HashMap<String, NonDictionaryField> additionalTextFields = new HashMap<String, NonDictionaryField>();

    public static HashMap<String, NonDictionaryField> additionalMlTextFields = new HashMap<String, NonDictionaryField>();

    private TenantService tenantService;

    private NamespaceDAO namespaceDAO;

    private DictionaryDAOImpl dictionaryDAO;

    private  Map<String,DictionaryComponent> dictionaryServices;
    private  Map<DictionaryKey,CMISAbstractDictionaryService> cmisDictionaryServices;

    private boolean storeAll = false;

    private AlfrescoDataType alfrescoDataType;

    private String id;

    private HashMap<String, Set<String>> modelErrors = new HashMap<String, Set<String>>();

    static
    {
        addNonDictionaryField(QueryConstants.FIELD_ID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true); // Must store
        addNonDictionaryField(QueryConstants.FIELD_LID, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(QueryConstants.FIELD_TX, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_PARENT, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(QueryConstants.FIELD_LINKASPECT, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(QueryConstants.FIELD_PATH, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(QueryConstants.FIELD_ANCESTOR, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(QueryConstants.FIELD_ISCONTAINER, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_ISCATEGORY, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_QNAME, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(QueryConstants.FIELD_PRIMARYASSOCQNAME, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(QueryConstants.FIELD_ISROOT, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_PRIMARYASSOCTYPEQNAME, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_ISNODE, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false); // Must store
        addNonDictionaryField(QueryConstants.FIELD_ASSOCTYPEQNAME, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(QueryConstants.FIELD_PRIMARYPARENT, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_TYPE, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_ASPECT, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(QueryConstants.FIELD_FTSSTATUS, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_DBID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false); // Must store
        addNonDictionaryField(QueryConstants.FIELD_TXID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false); // Must store
        addNonDictionaryField(QueryConstants.FIELD_INTXID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_ACLTXID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false); // Must store
        addNonDictionaryField(QueryConstants.FIELD_INACLTXID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_TXCOMMITTIME, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_ACLTXCOMMITTIME, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_EXCEPTION_MESSAGE, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(QueryConstants.FIELD_EXCEPTION_STACK, Store.YES, Index.NO, TermVector.NO, false);

        addNonDictionaryField(QueryConstants.FIELD_ACLID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false); // Must store
        addNonDictionaryField(QueryConstants.FIELD_READER, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(QueryConstants.FIELD_OWNER, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true); // Must store

        addNonDictionaryField(QueryConstants.FIELD_PARENT_ASSOC_CRC, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);

        addAdditionalContentField(".size", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".locale", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".mimetype", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".encoding", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".contentDocId", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".transformationException", Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".transformationTime", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".transformationStatus", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".__", Store.NO, Index.ANALYZED, TermVector.NO, false);

        addAdditionalTextField(".__", Store.NO, Index.ANALYZED, TermVector.NO, true);
        addAdditionalTextField(".u", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addAdditionalTextField(".__.u", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addAdditionalTextField(".sort", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);

        addAdditionalMlTextField(".__", Store.NO, Index.ANALYZED, TermVector.NO, true);
        addAdditionalMlTextField(".u", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addAdditionalMlTextField(".__.u", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addAdditionalMlTextField(".sort", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
    }

    private static void addNonDictionaryField(String name, Store store, Index index, TermVector termVector, boolean multiValued)
    {
        nonDictionaryFields.put(name, new NonDictionaryField(name, store, index, termVector, multiValued));
    }

    private static void addAdditionalContentField(String name, Store store, Index index, TermVector termVector, boolean multiValued)
    {
        additionalContentFields.put(name, new NonDictionaryField(name, store, index, termVector, multiValued));
    }

    private static void addAdditionalTextField(String name, Store store, Index index, TermVector termVector, boolean multiValued)
    {
        additionalTextFields.put(name, new NonDictionaryField(name, store, index, termVector, multiValued));
    }

    private static void addAdditionalMlTextField(String name, Store store, Index index, TermVector termVector, boolean multiValued)
    {
        additionalMlTextFields.put(name, new NonDictionaryField(name, store, index, termVector, multiValued));
    }

    /**
     * @param id
     * @return
     */
    public static AlfrescoSolrDataModel getInstance(String id)
    {
        readWriteLock.readLock().lock();
        try
        {
            AlfrescoSolrDataModel model = models.get(id);
            if (model != null)
            {
                return model;
            }
        }
        finally
        {
            readWriteLock.readLock().unlock();
        }

        // not found

        readWriteLock.writeLock().lock();
        try
        {
            AlfrescoSolrDataModel model = models.get(id);
            if (model == null)
            {
                model = new AlfrescoSolrDataModel(id);
                models.put(id, model);
            }
            return model;
        }
        finally
        {
            readWriteLock.writeLock().unlock();
        }

    }

    private AlfrescoSolrDataModel(String id)
    {
        this.id = id;

        tenantService = new SingleTServiceImpl();

        dictionaryDAO = new DictionaryDAOImpl(/*namespaceDAO*/);
        namespaceDAO = dictionaryDAO;
        dictionaryDAO.setTenantService(tenantService);
        dictionaryDAO.setDictionaryRegistryCache(new MemoryCache<String, DictionaryRegistry>());
        // TODO: use config ....
        dictionaryDAO.setDefaultAnalyserResourceBundleName("alfresco/model/dataTypeAnalyzers");
        dictionaryDAO.setResourceClassLoader(getResourceClassLoader());

        QNameFilter qnameFilter = getQNameFilter();
        dictionaryServices = AlfrescoClientDataModelServicesFactory.constructDictionaryServices(qnameFilter, dictionaryDAO);
        DictionaryComponent dictionaryComponent = getDictionaryService(CMISStrictDictionaryService.DEFAULT);
        dictionaryComponent.setMessageLookup(new StaticMessageLookup());

        cmisDictionaryServices = AlfrescoClientDataModelServicesFactory.constructDictionaries(qnameFilter, namespaceDAO, dictionaryComponent, dictionaryDAO);

    }

    private QNameFilter getQNameFilter()
    {
    	QNameFilter qnameFilter = null;
    	FileSystemXmlApplicationContext ctx = null;

		File resourceDirectory = getResourceDirectory();
		File filterContext = new File(resourceDirectory, "alfresco/model/opencmis-qnamefilter-context.xml");

    	try
    	{
			ctx = new FileSystemXmlApplicationContext(new String[] { "file:" + filterContext.getAbsolutePath() }, false);
			ctx.setClassLoader(this.getClass().getClassLoader());
			ctx.refresh();
			qnameFilter = (QNameFilter)ctx.getBean("cmisTypeExclusions");
    		if(qnameFilter == null)
    		{
    			log.warn("Unable to find type filter at " + filterContext.getAbsolutePath() + ", no type filtering");
    		}
    	}
		catch(BeansException e)
		{
			log.warn("Unable to parse type filter at " + filterContext.getAbsolutePath() + ", no type filtering");
		}
		finally
		{
			if(ctx != null && ctx.getBeanFactory() != null && ctx.isActive())
			{
				ctx.close();
			}
		}

		return qnameFilter;
    }

    /**
     * Gets a DictionaryService, if an Alternative dictionary is specified it tries to get that.
     * It will attempt to get the DEFAULT dictionary service if null is specified or it can't find
     * a dictionary with the name of "alternativeDictionary"
     * @param alternativeDictionary - can be null;
     * @return DictionaryService
     */
    public DictionaryComponent getDictionaryService(String alternativeDictionary)
    {
        DictionaryComponent dictionaryComponent = null;
        
        if (alternativeDictionary != null && !alternativeDictionary.trim().isEmpty())
        {
            dictionaryComponent = dictionaryServices.get(alternativeDictionary);
        }
        
        if (dictionaryComponent == null)
        {
            dictionaryComponent = dictionaryServices.get(CMISStrictDictionaryService.DEFAULT);
        }
        return dictionaryComponent;
    }

    /**
     * Gets the CMISDictionaryService, if an Alternative dictionary is specified it tries to get that.
     * It will attempt to get the DEFAULT dictionary service if null is specified or it can't find
     * a dictionary with the name of "alternativeDictionary"
     * @param alternativeDictionary - can be null;
     * @return CMISDictionaryService
     */
    public CMISDictionaryService getCMISDictionary(String alternativeDictionary, CmisVersion cmisVersion)
    {
        CMISDictionaryService cmisDictionary = null;
        
        if (alternativeDictionary != null && !alternativeDictionary.trim().isEmpty())
        {
        	DictionaryKey key = new DictionaryKey(cmisVersion, alternativeDictionary);
            cmisDictionary = cmisDictionaryServices.get(key);
        }
        
        if (cmisDictionary == null)
        {
        	DictionaryKey key = new DictionaryKey(cmisVersion, CMISStrictDictionaryService.DEFAULT);
            cmisDictionary = cmisDictionaryServices.get(key);
        }
        return cmisDictionary;
    }
    
    public NamespaceDAO getNamespaceDAO()
    {
        return namespaceDAO;
    }

    public boolean isStoreAll()
    {
        return storeAll;
    }

    public void setStoreAll(boolean storeAll)
    {
        this.storeAll = storeAll;
    }

    /**
     * @return
     */
    public MLAnalysisMode getMLAnalysisMode()
    {
        return MLAnalysisMode.EXACT_LANGUAGE_AND_ALL;
    }

    public boolean isIndexedOrStored(QName propertyQName)
    {
        if (storeAll)
        {
            return true;
        }

        String fieldName = QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString();

        PropertyDefinition propertyDefinition = getPropertyDefinition(fieldName);
        if (propertyDefinition != null)
        {
            if (propertyDefinition.isIndexed())
            {
                return true;
            }
            if (propertyDefinition.isStoredInIndex())
            {
                return true;
            }
            return false;

        }
        else
        {
            // by default we would index a residual property (if they have a known prefix/uri ....)
            return true;
        }
    }

    /**
     * @param field
     * @return
     */
    public Index getFieldIndex(SchemaField field)
    {
        PropertyDefinition propertyDefinition = getPropertyDefinition(field.getName());
        if (propertyDefinition != null)
        {
            if (propertyDefinition.isIndexed())
            {
                switch (propertyDefinition.getIndexTokenisationMode())
                {
                case TRUE:
                case BOTH:
                default:
                    return Index.ANALYZED;
                case FALSE:
                    return Index.NOT_ANALYZED;

                }
            }
            else
            {
                return Field.Index.NO;
            }
        }

        NonDictionaryField nonDDField = nonDictionaryFields.get(field.getName());
        if (nonDDField != null)
        {
            return nonDDField.index;
        }

        for (String additionalContentFieldEnding : additionalContentFields.keySet())
        {
            if (field.getName().endsWith(additionalContentFieldEnding)
                    && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalContentFieldEnding.length()))) != null))
            {
                return additionalContentFields.get(additionalContentFieldEnding).index;
            }
        }

        for (String additionalTextFieldEnding : additionalTextFields.keySet())
        {
            if (field.getName().endsWith(additionalTextFieldEnding)
                    && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalTextFieldEnding.length()))) != null))
            {
                return additionalTextFields.get(additionalTextFieldEnding).index;
            }
        }

        for (String additionalMlTextFieldEnding : additionalMlTextFields.keySet())
        {
            if (field.getName().endsWith(additionalMlTextFieldEnding)
                    && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalMlTextFieldEnding.length()))) != null))
            {
                return additionalMlTextFields.get(additionalMlTextFieldEnding).index;
            }
        }

        return Index.ANALYZED;
    }

    private PropertyDefinition getPropertyDefinition(String fieldName)
    {
        QName rawPropertyName = QName.createQName(expandFieldName(fieldName).substring(1));
        QName propertyQName = QName.createQName(rawPropertyName.getNamespaceURI(), ISO9075.decode(rawPropertyName.getLocalName()));
        return getPropertyDefinition(propertyQName);
    }

    String expandFieldName(String fieldName)
    {
        String expandedFieldName = fieldName;
        if (fieldName.startsWith("@"))
        {
            expandedFieldName = expandAttributeFieldName(fieldName);
        }
        else if (fieldName.startsWith("{"))
        {
            expandedFieldName = expandFieldName("@" + fieldName);
        }
        else if (fieldName.contains(":"))
        {
            expandedFieldName = expandFieldName("@" + fieldName);
        }
        else if (nonDictionaryFields.get(fieldName) == null)
        {
            expandedFieldName = expandFieldName("@" + fieldName);
        }
        return expandedFieldName;

    }

    String expandAttributeFieldName(String field)
    {
        String fieldName = field;
        // Check for any prefixes and expand to the full uri
        if (field.charAt(1) != '{')
        {
            int colonPosition = field.indexOf(':');
            if (colonPosition == -1)
            {
                // use the default namespace
                fieldName = "@{" + getNamespaceDAO().getNamespaceURI("") + "}" + field.substring(1);
            }
            else
            {
                // find the prefix
                fieldName = "@{" + getNamespaceDAO().getNamespaceURI(field.substring(1, colonPosition)) + "}" + field.substring(colonPosition + 1);
            }
        }
        return fieldName;
    }

    /**
     * @param field
     * @return
     */
    public Store getFieldStore(SchemaField field)
    {
        if (storeAll)
        {
            return Store.YES;
        }

        PropertyDefinition propertyDefinition = getPropertyDefinition(field.getName());
        if (propertyDefinition != null)
        {
            return propertyDefinition.isStoredInIndex() ? Store.YES : Store.NO;
        }

        NonDictionaryField nonDDField = nonDictionaryFields.get(field.getName());
        if (nonDDField != null)
        {
            return nonDDField.store;
        }

        for (String additionalContentFieldEnding : additionalContentFields.keySet())
        {
            if (field.getName().endsWith(additionalContentFieldEnding)
                    && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalContentFieldEnding.length()))) != null))
            {
                return additionalContentFields.get(additionalContentFieldEnding).store;
            }
        }

        for (String additionalTextFieldEnding : additionalTextFields.keySet())
        {
            if (field.getName().endsWith(additionalTextFieldEnding)
                    && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalTextFieldEnding.length()))) != null))
            {
                return additionalTextFields.get(additionalTextFieldEnding).store;
            }
        }

        for (String additionalMlTextFieldEnding : additionalMlTextFields.keySet())
        {
            if (field.getName().endsWith(additionalMlTextFieldEnding)
                    && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalMlTextFieldEnding.length()))) != null))
            {
                return additionalMlTextFields.get(additionalMlTextFieldEnding).store;
            }
        }

        return Store.NO;
    }

    /**
     * @param field
     * @return
     */
    public TermVector getFieldTermVec(SchemaField field)
    {
        return TermVector.NO;
    }

    /**
     * @param field
     * @return
     */
    public boolean getOmitNorms(SchemaField field)
    {
        PropertyDefinition propertyDefinition = getPropertyDefinition(field.getName());
        if (propertyDefinition != null)
        {
          if(propertyDefinition.getDataType().getName().equals(DataTypeDefinition.CONTENT))
          {
              return false;
          }
          else  if(propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
          {
              return false;
          }
          else  if(propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
          {
              return false;
          }
          else
          {
              return true;
          }
        }

        NonDictionaryField nonDDField = nonDictionaryFields.get(field.getName());
        if (nonDDField != null)
        {
            if ((nonDDField.index == Index.ANALYZED_NO_NORMS) || (nonDDField.index == Index.NOT_ANALYZED_NO_NORMS) || (nonDDField.index == Index.NO_NORMS))
            {
                return true;
            }
            else
            {
                return false;
            }

        }

        for (String additionalContentFieldEnding : additionalContentFields.keySet())
        {
            if (field.getName().endsWith(additionalContentFieldEnding)
                    && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalContentFieldEnding.length()))) != null))
            {
                nonDDField = additionalContentFields.get(additionalContentFieldEnding);
                if ((nonDDField.index == Index.ANALYZED_NO_NORMS) || (nonDDField.index == Index.NOT_ANALYZED_NO_NORMS) || (nonDDField.index == Index.NO_NORMS))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        for (String additionalTextFieldEnding : additionalTextFields.keySet())
        {
            if (field.getName().endsWith(additionalTextFieldEnding)
                    && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalTextFieldEnding.length()))) != null))
            {
                nonDDField = additionalTextFields.get(additionalTextFieldEnding);
                if ((nonDDField.index == Index.ANALYZED_NO_NORMS) || (nonDDField.index == Index.NOT_ANALYZED_NO_NORMS) || (nonDDField.index == Index.NO_NORMS))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        for (String additionalMlTextFieldEnding : additionalMlTextFields.keySet())
        {
            if (field.getName().endsWith(additionalMlTextFieldEnding)
                    && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalMlTextFieldEnding.length()))) != null))
            {
                nonDDField = additionalMlTextFields.get(additionalMlTextFieldEnding);
                if ((nonDDField.index == Index.ANALYZED_NO_NORMS) || (nonDDField.index == Index.NOT_ANALYZED_NO_NORMS) || (nonDDField.index == Index.NO_NORMS))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        return false;

    }

    /**
     * @param field
     * @param part1
     * @param part2
     * @param minInclusive
     * @param maxInclusive
     * @return
     */
    public Query getRangeQuery(SchemaField field, String part1, String part2, boolean minInclusive, boolean maxInclusive)
    {
        SolrLuceneAnalyser defaultAnalyser = new SolrLuceneAnalyser(getDictionaryService(CMISStrictDictionaryService.DEFAULT), getMLAnalysisMode(), alfrescoDataType.getDefaultAnalyzer(), this);
        SolrQueryParser parser = new SolrQueryParser("TEXT", defaultAnalyser);
        parser.setDefaultOperator(Operator.AND);
        parser.setNamespacePrefixResolver(namespaceDAO);
        parser.setDictionaryService(getDictionaryService(CMISStrictDictionaryService.DEFAULT));
        parser.setTenantService(tenantService);
        parser.setSearchParameters(null);
        parser.setDefaultSearchMLAnalysisMode(getMLAnalysisMode());
        parser.setIndexReader(null);
        parser.setAllowLeadingWildcard(true);

        try
        {
            return parser.getRangeQuery(field.getName(), part1, part2, minInclusive, maxInclusive, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
        }
        catch (ParseException e)
        {
            throw new AlfrescoRuntimeException("Parse error building range query", e);
        }
    }

    /**
     * @param model
     */
    public boolean putModel(M2Model model)
    {
        Set<String> errors = validateModel(model);
        if(errors.size() == 0)
        {
            modelErrors.remove(model.getName());
            dictionaryDAO.putModelIgnoringConstraints(model);
            return true;
        }
        else
        {
            if(!modelErrors.containsKey(model.getName()))
            {
                modelErrors.put(model.getName(), errors);
                log.warn(errors.iterator().next());
            }
            return false;
        }
       
    }

    
    private Set<String> validateModel(M2Model model)
    {
        HashSet<String> errors = new HashSet<String>();
        try 
        { 
            dictionaryDAO.getCompiledModel(QName.createQName(model.getName(), namespaceDAO)); 
        } 
        catch (DictionaryException e) 
        {
            // No model to diff
            return errors;
        }
        catch(NamespaceException e)
        {
            // namespace unknown - no model 
            return errors;
        }
        
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModelIgnoringConstraints(model);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            if (modelDiff.getDiffType().equals(M2ModelDiff.DIFF_UPDATED))
            {
                errors.add("Model not updated: "+model.getName()  + "   Failed to validate model update - found non-incrementally updated " + modelDiff.getElementType() + " '" + modelDiff.getElementName() + "'");
            }
        }
        return errors;
    }
  
    
    public M2Model getM2Model(QName modelQName)
    {
        return dictionaryDAO.getCompiledModel(modelQName).getM2Model();
    }

    public void afterInitModels()
    {
        for (CMISAbstractDictionaryService cds : cmisDictionaryServices.values())
        {
            cds.afterDictionaryInit();
        }
    }
    
    public void setCMDefaultUri()
    {
        if(AlfrescoSolrDataModel.getInstance(id).getNamespaceDAO().getURIs().contains(NamespaceService.CONTENT_MODEL_1_0_URI))
        {
            AlfrescoSolrDataModel.getInstance(id).getNamespaceDAO().addPrefix("", NamespaceService.CONTENT_MODEL_1_0_URI);
        }
    }

    private static class NonDictionaryField
    {
        private String name;

        private Store store;

        private Index index;

        private TermVector termVector;

        private boolean multiValued;

        /**
         * 
         */
        public NonDictionaryField(String name, Store store, Index index, TermVector termVector, boolean multiValued)
        {
            this.name = name;
            this.store = store;
            this.index = index;
            this.termVector = termVector;
            this.multiValued = multiValued;
        }
    }

    // public void generateSchema(XMLWriter xmlWriter) throws IOException, SAXException
    // {
    // xmlWriter.startDocument();
    //
    // AttributesImpl attrs = new AttributesImpl();
    // attrs.addAttribute("", "name", "name", null, "alfresco");
    // attrs.addAttribute("", "version", "version", null, "1.0");
    // xmlWriter.startElement("", "schema", "schema", attrs);
    //
    // xmlWriter.startElement("", "types", "types", new AttributesImpl());
    // writeFieldType(xmlWriter, "alfrescoDataType", "org.alfresco.solr.AlfrescoDataType");
    // xmlWriter.endElement("", "types", "types");
    //
    // xmlWriter.startElement("", "fields", "fields", new AttributesImpl());
    //
    // for (NonDictionaryField field : nonDictionaryFields.values())
    // {
    // writeField(xmlWriter, field);
    // }
    //
    // for (QName modelName : dictionaryComponent.getAllModels())
    // {
    // for (QName propertyName : dictionaryComponent.getProperties(modelName))
    // {
    // PropertyDefinition propertyDefinition = dictionaryComponent.getProperty(propertyName);
    // writeField(xmlWriter, propertyDefinition);
    // }
    // }
    // xmlWriter.endElement("", "fields", "fields");
    //
    // xmlWriter.startElement("", "uniqueKey", "uniqueKey", new AttributesImpl());
    // xmlWriter.write("ID");
    // xmlWriter.endElement("", "uniqueKey", "uniqueKey");
    //
    // xmlWriter.startElement("", "defaultSearchField", "defaultSearchField", new AttributesImpl());
    // xmlWriter.write("ID");
    // xmlWriter.endElement("", "defaultSearchField", "defaultSearchField");
    //
    // xmlWriter.endElement("", "schema", "schema");
    //
    // xmlWriter.endDocument();
    // xmlWriter.close();
    // }
    //
    // private void writeFieldType(XMLWriter xmlWriter, String name, String clazz) throws SAXException
    // {
    // AttributesImpl attrs = new AttributesImpl();
    // attrs.addAttribute("", "name", "name", null, name);
    // attrs.addAttribute("", "class", "class", null, clazz);
    // xmlWriter.startElement("", "fieldType", "fieldType", attrs);
    // xmlWriter.startElement("", "analyzer", "analyzer", new AttributesImpl());
    // AttributesImpl tokenizerAttrs = new AttributesImpl();
    // tokenizerAttrs.addAttribute("", "class", "class", null, "org.apache.solr.analysis.WhitespaceTokenizerFactory");
    // xmlWriter.startElement("", "tokenizer", "tokenizer", tokenizerAttrs);
    // xmlWriter.endElement("", "tokenizer", "tokenizer");
    // AttributesImpl filterAttrs = new AttributesImpl();
    // filterAttrs.addAttribute("", "class", "class", null, "org.apache.solr.analysis.LowerCaseFilterFactory");
    // xmlWriter.startElement("", "filter", "filter", filterAttrs);
    // xmlWriter.endElement("", "filter", "filter");
    // xmlWriter.endElement("", "analyzer", "analyzer");
    // xmlWriter.endElement("", "fieldType", "fieldType");
    // }
    //
    // private void writeField(XMLWriter xmlWriter, PropertyDefinition propertyDefinition) throws SAXException
    // {
    // String name = "@" + propertyDefinition.getName().toString();
    //
    // Store store = propertyDefinition.isStoredInIndex() ? Store.YES : Store.NO;
    // boolean multiValued = propertyDefinition.isMultiValued();
    // if (propertyDefinition.isIndexed())
    // {
    // switch (propertyDefinition.getIndexTokenisationMode())
    // {
    // case BOTH:
    // writeField(xmlWriter, name, "alfrescoDataType", store, Index.ANALYZED, TermVector.NO, multiValued);
    // if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.CONTENT))
    // {
    // writeField(xmlWriter, name + ".size", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO,
    // false);
    // writeField(xmlWriter, name + ".locale", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // false);
    // writeField(xmlWriter, name + ".mimetype", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".encoding", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".contentDataId", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".transformationException", "alfrescoDataType", Store.NO,
    // Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
    // writeField(xmlWriter, name + ".transformationTime", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".transformationStatus", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, false);
    // }
    // else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
    // {
    // writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
    // writeField(xmlWriter, name + ".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // writeField(xmlWriter, name + ".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // }
    // else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
    // {
    // writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
    // writeField(xmlWriter, name + ".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // writeField(xmlWriter, name + ".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // }
    // else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.DATETIME))
    // {
    // DataTypeDefinition dataType = propertyDefinition.getDataType();
    // String analyserClassName = propertyDefinition.resolveAnalyserClassName(locale, classLoader);
    // if (analyserClassName.equals(DateTimeAnalyser.class.getCanonicalName()))
    // {
    // writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO,
    // multiValued);
    // }
    // else
    // {
    // // nothing
    // }
    // }
    // else
    // {
    // // nothing
    // }
    // break;
    // case FALSE:
    // writeField(xmlWriter, name, "alfrescoDataType", store, Index.NOT_ANALYZED, TermVector.NO, multiValued);
    // if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.CONTENT))
    // {
    // writeField(xmlWriter, name + ".size", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO,
    // false);
    // writeField(xmlWriter, name + ".locale", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // false);
    // writeField(xmlWriter, name + ".mimetype", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".encoding", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".contentDataId", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".transformationException", "alfrescoDataType", Store.NO,
    // Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
    // writeField(xmlWriter, name + ".transformationTime", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".transformationStatus", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // false);
    // }
    // else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
    // {
    // writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
    // writeField(xmlWriter, name + ".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // writeField(xmlWriter, name + ".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // }
    // else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
    // {
    // writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
    // writeField(xmlWriter, name + ".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // writeField(xmlWriter, name + ".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // }
    // break;
    // case TRUE:
    // writeField(xmlWriter, name, "alfrescoDataType", store, Index.ANALYZED, TermVector.NO, multiValued);
    // if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.CONTENT))
    // {
    // writeField(xmlWriter, name + ".size", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO,
    // false);
    // writeField(xmlWriter, name + ".locale", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // false);
    // writeField(xmlWriter, name + ".mimetype", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".encoding", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".contentDataId", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".transformationException", "alfrescoDataType", Store.NO,
    // Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
    // writeField(xmlWriter, name + ".transformationTime", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".transformationStatus", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS,
    // TermVector.NO, false);
    // writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // false);
    // }
    // else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
    // {
    // writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
    // writeField(xmlWriter, name + ".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // writeField(xmlWriter, name + ".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // }
    // else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
    // {
    // writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
    // writeField(xmlWriter, name + ".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // writeField(xmlWriter, name + ".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO,
    // true);
    // }
    // else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.DATETIME))
    // {
    // DataTypeDefinition dataType = propertyDefinition.getDataType();
    // String analyserClassName = propertyDefinition.resolveAnalyserClassName();
    // if (analyserClassName.equals(DateTimeAnalyser.class.getCanonicalName()))
    // {
    // writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO,
    // multiValued);
    // }
    // else
    // {
    // // nothing
    // }
    // }
    // break;
    // default:
    // }
    //
    // }
    // else
    // {
    // writeField(xmlWriter, name, "alfrescoDataType", store, Index.NO, TermVector.NO, multiValued);
    // }
    // }
    //
    // private void writeField(XMLWriter xmlWriter, NonDictionaryField field) throws SAXException
    // {
    // writeField(xmlWriter, field.name, "alfrescoDataType", field.store, field.index, field.termVector,
    // field.multiValued);
    // }
    //
    // private void writeField(XMLWriter xmlWriter, String name, String fieldType, Store store, Index index, TermVector
    // termVector, boolean multiValued) throws SAXException
    // {
    // AttributesImpl attrs = new AttributesImpl();
    // attrs.addAttribute("", "name", "name", null, name);
    // attrs.addAttribute("", "type", "type", null, fieldType);
    //
    // attrs.addAttribute("", "indexed", "indexed", null, Boolean.valueOf(index != Index.NO).toString());
    // if ((index == Index.NOT_ANALYZED_NO_NORMS) || (index == Index.ANALYZED_NO_NORMS))
    // {
    // attrs.addAttribute("", "omitNorms", "omitNorms", null, "true");
    // }
    //
    // attrs.addAttribute("", "stored", "stored", null, Boolean.valueOf(store != Store.NO).toString());
    //
    // attrs.addAttribute("", "multiValued", "multiValued", null, Boolean.valueOf(multiValued).toString());
    //
    // xmlWriter.startElement("", "field", "field", attrs);
    // xmlWriter.endElement("", "field", "field");
    // }

    public AbstractLuceneQueryParser getLuceneQueryParser(SearchParameters searchParameters, IndexReader indexReader)
    {
        SolrLuceneAnalyser analyzer = new SolrLuceneAnalyser(getDictionaryService(CMISStrictDictionaryService.DEFAULT), getMLAnalysisMode(), alfrescoDataType.getDefaultAnalyzer(), this);
        SolrQueryParser parser = new SolrQueryParser(searchParameters.getDefaultFieldName(), analyzer);
        Operator defaultOperator;
        if (searchParameters.getDefaultOperator() == SearchParameters.AND)
        {
            defaultOperator = LuceneQueryParser.AND_OPERATOR;
        }
        else
        {
            defaultOperator = LuceneQueryParser.OR_OPERATOR;
        }
        parser.setDefaultOperator(defaultOperator);
        parser.setNamespacePrefixResolver(namespaceDAO);
        parser.setDictionaryService(getDictionaryService(CMISStrictDictionaryService.DEFAULT));
        parser.setTenantService(tenantService);
        parser.setSearchParameters(searchParameters);
        parser.setDefaultSearchMLAnalysisMode(getMLAnalysisMode());
        parser.setIndexReader(indexReader);
        parser.setAllowLeadingWildcard(true);

        return parser;
    }

    public LuceneQueryBuilderContext<Query, Sort, ParseException> getLuceneQueryBuilderContext(SearchParameters searchParameters, IndexReader indexReader, String alternativeDictionary)
    {
        LuceneQueryBuilderContextSolrImpl luceneContext = new LuceneQueryBuilderContextSolrImpl(getDictionaryService(alternativeDictionary), namespaceDAO, tenantService, searchParameters,
                getMLAnalysisMode(), indexReader, alfrescoDataType.getQueryAnalyzer(), this);
        return luceneContext;
    }

    public Query getFTSQuery(Pair<SearchParameters, Boolean> searchParametersAndFilter, IndexReader indexReader) throws ParseException
    {
        SearchParameters searchParameters = searchParametersAndFilter.getFirst();
        Boolean isFilter = searchParametersAndFilter.getSecond();
        
        QueryModelFactory factory = new LuceneQueryModelFactory();
        AlfrescoFunctionEvaluationContext functionContext = new AlfrescoFunctionEvaluationContext(namespaceDAO, getDictionaryService(CMISStrictDictionaryService.DEFAULT), NamespaceService.CONTENT_MODEL_1_0_URI);

        FTSParser.Mode mode;

        if (searchParameters.getDefaultFTSOperator() == org.alfresco.service.cmr.search.SearchParameters.Operator.AND)
        {
            mode = FTSParser.Mode.DEFAULT_CONJUNCTION;
        }
        else
        {
            mode = FTSParser.Mode.DEFAULT_DISJUNCTION;
        }

        Constraint constraint = FTSQueryParser.buildFTS(searchParameters.getQuery(), factory, functionContext, null, null, mode,
                searchParameters.getDefaultFTSOperator() == org.alfresco.service.cmr.search.SearchParameters.Operator.OR ? Connective.OR : Connective.AND,
                searchParameters.getQueryTemplates(), searchParameters.getDefaultFieldName());
        org.alfresco.repo.search.impl.querymodel.Query queryModelQuery = factory.createQuery(null, null, constraint, new ArrayList<Ordering>());

        @SuppressWarnings("unchecked")
        LuceneQueryBuilder<Query, Sort, ParseException> builder = (LuceneQueryBuilder<Query, Sort, ParseException>) queryModelQuery;

        LuceneQueryBuilderContext<Query, Sort, ParseException> luceneContext = getLuceneQueryBuilderContext(searchParameters, indexReader, CMISStrictDictionaryService.DEFAULT);

        Set<String> selectorGroup = null;
        if (queryModelQuery.getSource() != null)
        {
            List<Set<String>> selectorGroups = queryModelQuery.getSource().getSelectorGroups(functionContext);

            if (selectorGroups.size() == 0)
            {
                throw new UnsupportedOperationException("No selectors");
            }

            if (selectorGroups.size() > 1)
            {
                throw new UnsupportedOperationException("Advanced join is not supported");
            }

            selectorGroup = selectorGroups.get(0);
        }
        Query luceneQuery = builder.buildQuery(selectorGroup, luceneContext, functionContext);
        // query needs some search parameters fro correct caching ....

        ContextAwareQuery contextAwareQuery = new ContextAwareQuery(luceneQuery, Boolean.TRUE.equals(isFilter) ? null : searchParameters);
        return contextAwareQuery;
    }

    public org.alfresco.repo.search.impl.querymodel.Query parseCMISQueryToAlfrescoAbstractQuery(CMISQueryMode mode, SearchParameters searchParameters,
    		IndexReader indexReader, String alternativeDictionary, CmisVersion cmisVersion) throws ParseException
    {
        // convert search parameters to cmis query options
        // TODO: how to handle store ref
        CMISQueryOptions options = new CMISQueryOptions(searchParameters.getQuery(), StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        options.setQueryMode(CMISQueryMode.CMS_WITH_ALFRESCO_EXTENSIONS);
        options.setDefaultFieldName(searchParameters.getDefaultFieldName());
        // TODO: options.setDefaultFTSConnective()
        // TODO: options.setDefaultFTSFieldConnective()
        options.setIncludeInTransactionData(!searchParameters.excludeDataInTheCurrentTransaction());
        options.setLocales(searchParameters.getLocales());
        options.setMlAnalaysisMode(searchParameters.getMlAnalaysisMode());
        options.setQueryParameterDefinitions(searchParameters.getQueryParameterDefinitions());

        // parse cmis syntax
        CapabilityJoin joinSupport = (mode == CMISQueryMode.CMS_STRICT) ? CapabilityJoin.NONE : CapabilityJoin.INNERONLY;
        CmisFunctionEvaluationContext functionContext = getCMISFunctionEvaluationContext(mode, cmisVersion, alternativeDictionary);
        
        CMISDictionaryService cmisDictionary = getCMISDictionary(alternativeDictionary, cmisVersion);
        
        CMISQueryParser parser = new CMISQueryParser(options, cmisDictionary, joinSupport);
        org.alfresco.repo.search.impl.querymodel.Query queryModelQuery = parser.parse(new LuceneQueryModelFactory(), functionContext);

        // build lucene query
        Set<String> selectorGroup = null;
        if (queryModelQuery.getSource() != null)
        {
            List<Set<String>> selectorGroups = queryModelQuery.getSource().getSelectorGroups(functionContext);
            if (selectorGroups.size() == 0)
            {
                throw new UnsupportedOperationException("No selectors");
            }
            if (selectorGroups.size() > 1)
            {
                throw new UnsupportedOperationException("Advanced join is not supported");
            }
            selectorGroup = selectorGroups.get(0);
        }
        return queryModelQuery;
    }

    public CmisFunctionEvaluationContext getCMISFunctionEvaluationContext(CMISQueryMode mode, CmisVersion cmisVersion, String alternativeDictionary)
    {
        BaseTypeId[] validScopes = (mode == CMISQueryMode.CMS_STRICT) ? CmisFunctionEvaluationContext.STRICT_SCOPES : CmisFunctionEvaluationContext.ALFRESCO_SCOPES;
        CmisFunctionEvaluationContext functionContext = new CmisFunctionEvaluationContext();
        functionContext.setCmisDictionaryService(getCMISDictionary(alternativeDictionary, cmisVersion));
        functionContext.setValidScopes(validScopes);
        return functionContext;
    }
    
    public Query getCMISQuery(CMISQueryMode mode, Pair<SearchParameters, Boolean> searchParametersAndFilter, IndexReader indexReader, org.alfresco.repo.search.impl.querymodel.Query queryModelQuery, CmisVersion cmisVersion, String alternativeDictionary)
            throws ParseException
    {
        SearchParameters searchParameters = searchParametersAndFilter.getFirst();
        Boolean isFilter = searchParametersAndFilter.getSecond();
        
        BaseTypeId[] validScopes = (mode == CMISQueryMode.CMS_STRICT) ? CmisFunctionEvaluationContext.STRICT_SCOPES : CmisFunctionEvaluationContext.ALFRESCO_SCOPES;
        CmisFunctionEvaluationContext functionContext = getCMISFunctionEvaluationContext(mode, cmisVersion, alternativeDictionary);

        Set<String> selectorGroup = queryModelQuery.getSource().getSelectorGroups(functionContext).get(0);

        LuceneQueryBuilderContext<Query, Sort, ParseException> luceneContext = getLuceneQueryBuilderContext(searchParameters, indexReader, alternativeDictionary);
        @SuppressWarnings("unchecked")
        LuceneQueryBuilder<Query, Sort, ParseException> builder = (LuceneQueryBuilder<Query, Sort, ParseException>) queryModelQuery;
        org.apache.lucene.search.Query luceneQuery = builder.buildQuery(selectorGroup, luceneContext, functionContext);

        ContextAwareQuery contextAwareQuery = new ContextAwareQuery(luceneQuery, Boolean.TRUE.equals(isFilter) ? null : searchParameters);
        return contextAwareQuery;
    }

    /**
     * @param field
     * @param reverse
     */
    public SortField getSortField(SchemaField field, boolean reverse)
    {
        // MNT-8557 fix, manually replace '%20' with ' '
        String fieldNameToUse = field.getName().replaceAll("%20", " ");
        PropertyDefinition propertyDefinition = getPropertyDefinition(fieldNameToUse);
        if (propertyDefinition != null)
        {
            if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.DATETIME))
            {
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    return Sorting.getStringSortField(expandFieldName(fieldNameToUse) + ".sort", reverse, field.sortMissingLast(), field.sortMissingFirst());
                }
                else
                {
                    throw new UnsupportedOperationException("Ordering not supported for " + fieldNameToUse);
                }
            }
            else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
            {
                // ignore locale store in the text field

                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    return new SortField(expandFieldName(fieldNameToUse) + ".sort", new TextSortFieldComparatorSource(), reverse);
                }
                else
                {
                    throw new UnsupportedOperationException("Ordering not supported for " + fieldNameToUse);
                }
            }
            else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
            {
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    return new SortField(expandFieldName(fieldNameToUse) + ".sort", new MLTextSortFieldComparatorSource(), reverse);
                }
                else
                {
                    throw new UnsupportedOperationException("Ordering not supported for " + fieldNameToUse);
                }
            }
            else
            {
                return Sorting.getStringSortField(expandFieldName(fieldNameToUse), reverse, field.sortMissingLast(), field.sortMissingFirst());
            }
        }

        if (fieldNameToUse.equals("ID"))
        {
            return Sorting.getStringSortField("LID", reverse, field.sortMissingLast(), field.sortMissingFirst());
        }

        NonDictionaryField nonDDField = nonDictionaryFields.get(fieldNameToUse);
        if (nonDDField != null)
        {
            return Sorting.getStringSortField(fieldNameToUse, reverse, field.sortMissingLast(), field.sortMissingFirst());
        }

        for (String additionalContentFieldEnding : additionalContentFields.keySet())
        {
            if (fieldNameToUse.endsWith(additionalContentFieldEnding)
                    && (getPropertyDefinition(fieldNameToUse.substring(0, (fieldNameToUse.length() - additionalContentFieldEnding.length()))) != null))
            {
                return Sorting.getStringSortField(expandFieldName(fieldNameToUse.substring(0, (fieldNameToUse.length() - additionalContentFieldEnding.length())))+additionalContentFieldEnding, reverse,
                        field.sortMissingLast(), field.sortMissingFirst());
            }
        }

        for (String additionalTextFieldEnding : additionalTextFields.keySet())
        {
            if (fieldNameToUse.endsWith(additionalTextFieldEnding)
                    && (getPropertyDefinition(fieldNameToUse.substring(0, (fieldNameToUse.length() - additionalTextFieldEnding.length()))) != null))
            {
                return Sorting.getStringSortField(expandFieldName(fieldNameToUse.substring(0, (fieldNameToUse.length() - additionalTextFieldEnding.length())))+additionalTextFieldEnding, reverse,
                        field.sortMissingLast(), field.sortMissingFirst());
            }
        }

        for (String additionalMlTextFieldEnding : additionalMlTextFields.keySet())
        {
            if (fieldNameToUse.endsWith(additionalMlTextFieldEnding)
                    && (getPropertyDefinition(fieldNameToUse.substring(0, (fieldNameToUse.length() - additionalMlTextFieldEnding.length()))) != null))
            {
                return Sorting.getStringSortField(expandFieldName(fieldNameToUse.substring(0, (fieldNameToUse.length() - additionalMlTextFieldEnding.length())))+additionalMlTextFieldEnding, reverse,
                        field.sortMissingLast(), field.sortMissingFirst());
            }
        }

        return Sorting.getStringSortField(fieldNameToUse, reverse, field.sortMissingLast(), field.sortMissingFirst());

    }

    /**
     * @param alfrescoDataType
     */
    public void setAlfrescoDataType(AlfrescoDataType alfrescoDataType)
    {
        this.alfrescoDataType = alfrescoDataType;

    }

    public static class TextSortFieldComparatorSource extends FieldComparatorSource
    {

        /*
         * (non-Javadoc)
         * @see org.apache.lucene.search.FieldComparatorSource#newComparator(java.lang.String, int, int, boolean)
         */
        @Override
        public FieldComparator newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException
        {
            return new TextSortFieldComparator(numHits, fieldname, I18NUtil.getLocale());
        }

    }

    public static final class TextSortFieldComparator extends FieldComparator
    {

        private final String[] values;

        private String[] currentReaderValues;

        private final String field;

        final Collator collator;

        private String bottom;

        TextSortFieldComparator(int numHits, String field, Locale locale)
        {
            values = new String[numHits];
            this.field = field;
            collator = Collator.getInstance(locale);
        }

        public int compare(int slot1, int slot2)
        {
            final String val1 = values[slot1];
            final String val2 = values[slot2];
            if (val1 == null)
            {
                if (val2 == null)
                {
                    return 0;
                }
                return -1;
            }
            else if (val2 == null)
            {
                return 1;
            }
            return collator.compare(val1, val2);
        }

        public int compareBottom(int doc)
        {
            final String val2 = stripLocale(currentReaderValues[doc]);
            if (bottom == null)
            {
                if (val2 == null)
                {
                    return 0;
                }
                return -1;
            }
            else if (val2 == null)
            {
                return 1;
            }
            return collator.compare(bottom, val2);
        }

        public void copy(int slot, int doc)
        {
            values[slot] = stripLocale(currentReaderValues[doc]);
        }

        public void setNextReader(IndexReader reader, int docBase) throws IOException
        {
            currentReaderValues = FieldCache.DEFAULT.getStrings(reader, field);
        }

        public void setBottom(final int bottom)
        {
            this.bottom = values[bottom];
        }

        public Comparable value(int slot)
        {
            return values[slot];
        }

        private String stripLocale(String withLocale)
        {
            if (withLocale == null)
            {
                return withLocale;
            }
            else if (withLocale.startsWith("\u0000"))
            {
                return withLocale.substring(withLocale.indexOf('\u0000', 1) + 1);
            }
            else
            {
                return withLocale;
            }
        }
    }

    public static class MLTextSortFieldComparatorSource extends FieldComparatorSource
    {

        /*
         * (non-Javadoc)
         * @see org.apache.lucene.search.FieldComparatorSource#newComparator(java.lang.String, int, int, boolean)
         */
        @Override
        public FieldComparator newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException
        {
            return new MLTextSortFieldComparator(numHits, fieldname, I18NUtil.getLocale());
        }

    }

    public static final class MLTextSortFieldComparator extends FieldComparator
    {

        private final String[] values;

        private String[] currentReaderValues;

        private final String field;

        final Collator collator;

        private String bottom;

        Locale collatorLocale;

        MLTextSortFieldComparator(int numHits, String field, Locale collatorLocale)
        {
            values = new String[numHits];
            this.field = field;
            this.collatorLocale = collatorLocale;
            collator = Collator.getInstance(collatorLocale);
        }

        public int compare(int slot1, int slot2)
        {
            final String val1 = values[slot1];
            final String val2 = values[slot2];
            if (val1 == null)
            {
                if (val2 == null)
                {
                    return 0;
                }
                return -1;
            }
            else if (val2 == null)
            {
                return 1;
            }
            return collator.compare(val1, val2);
        }

        public int compareBottom(int doc)
        {
            final String val2 = findBestValue(currentReaderValues[doc]);
            if (bottom == null)
            {
                if (val2 == null)
                {
                    return 0;
                }
                return -1;
            }
            else if (val2 == null)
            {
                return 1;
            }
            return collator.compare(bottom, val2);
        }

        public void copy(int slot, int doc)
        {
            values[slot] = findBestValue(currentReaderValues[doc]);
        }

        public void setNextReader(IndexReader reader, int docBase) throws IOException
        {
            currentReaderValues = FieldCache.DEFAULT.getStrings(reader, field);
        }

        public void setBottom(final int bottom)
        {
            this.bottom = values[bottom];
        }

        public Comparable value(int slot)
        {
            return values[slot];
        }

        private String findBestValue(String withLocale)
        {
            // split strin into MLText object
            if (withLocale == null)
            {
                return withLocale;
            }
            else if (withLocale.startsWith("\u0000"))
            {
                MLText mlText = new MLText();
                String[] parts = withLocale.split("\u0000");
                for (int i = 0; (i + 2) <= parts.length; i += 3)
                {
                    Locale locale = null;
                    String[] localeParts = parts[i + 1].split("_");
                    if (localeParts.length == 1)
                    {
                        locale = new Locale(localeParts[0]);
                    }
                    else if (localeParts.length == 2)
                    {
                        locale = new Locale(localeParts[0], localeParts[1]);
                    }
                    else if (localeParts.length == 3)
                    {
                        locale = new Locale(localeParts[0], localeParts[1], localeParts[2]);
                    }
                    if (locale != null)
                    {
                        if (i + 2 == parts.length)
                        {
                            mlText.addValue(locale, "");
                        }
                        else
                        {
                            mlText.addValue(locale, parts[i + 2]);
                        }
                    }
                }
                return mlText.getClosestValue(collatorLocale);
            }
            else
            {
                return withLocale;
            }
        }
    }

    /**
     * @return
     */
    public List<AlfrescoModel> getAlfrescoModels()
    {

        ArrayList<AlfrescoModel> answer = new ArrayList<AlfrescoModel>();
        for (QName modelName : dictionaryDAO.getModels())
        {
            M2Model m2Model = dictionaryDAO.getCompiledModel(modelName).getM2Model();
            answer.add(new AlfrescoModel(m2Model, getDictionaryService(CMISStrictDictionaryService.DEFAULT).getModel(modelName).getChecksum(ModelDefinition.XMLBindingType.DEFAULT)));
        }
        return answer;

    }

    /**
     * @param defaultAnalyzer
     * @return
     */
    public SolrLuceneAnalyser getSolrLuceneAnalyser()
    {
        return new SolrLuceneAnalyser(getDictionaryService(CMISStrictDictionaryService.DEFAULT), getMLAnalysisMode(), alfrescoDataType.getDefaultAnalyzer(), this);
    }

    /**
     * @return
     */
    public ClassLoader getResourceClassLoader()
    {

        File f = new File(id, "alfrescoResources");
        if (f.canRead() && f.isDirectory())
        {

            URL[] urls = new URL[1];

            try
            {
                URL url = f.toURI().normalize().toURL();
                urls[0] = url;
            }
            catch (MalformedURLException e)
            {
                throw new AlfrescoRuntimeException("Failed to add resources to classpath ", e);
            }

            return URLClassLoader.newInstance(urls, this.getClass().getClassLoader());
        }
        else
        {
            return this.getClass().getClassLoader();
        }
    }

    /**
     * @return
     */
    public SolrLuceneAnalyser getSolrLuceneQueryAnalyser()
    {
        return new SolrLuceneAnalyser(getDictionaryService(CMISStrictDictionaryService.DEFAULT), getMLAnalysisMode(), alfrescoDataType.getDefaultQueryAnalyzer(), this);
    }

    /**
     * @param propertyQName
     * @return
     */
    public PropertyDefinition getPropertyDefinition(QName propertyQName)
    {
        PropertyDefinition propertyDef = getDictionaryService(CMISStrictDictionaryService.DEFAULT).getProperty(propertyQName);
        if ((propertyDef != null) && (propertyDef.getName().equals(ContentModel.PROP_AUTHOR)))
        {
            return new PropertyDefinitionWrapper(propertyDef);
        }
        else if ((propertyDef != null) && (propertyDef.getName().equals(ContentModel.PROP_CREATOR)))
        {
            return new PropertyDefinitionWrapper(propertyDef);
        }
        else if ((propertyDef != null) && (propertyDef.getName().equals(ContentModel.PROP_MODIFIER)))
        {
            return new PropertyDefinitionWrapper(propertyDef);
        }
        return propertyDef;
    }

    /**
     * @return the failedModels
     */
    public Map<String, Set<String>> getModelErrors()
    {
        return Collections.unmodifiableMap(modelErrors);
    }
    
    private File getResourceDirectory()
    {
        File f = new File(id, "alfrescoResources");
        return f;
    }   
}
