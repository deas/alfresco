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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.opencmis.dictionary.CMISAbstractDictionaryService;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.opencmis.dictionary.QNameFilter;
import org.alfresco.repo.cache.MemoryCache;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.DictionaryDAOImpl;
import org.alfresco.repo.dictionary.DictionaryDAOImpl.DictionaryRegistry;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2ModelDiff;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.repo.dictionary.NamespaceDAOImpl;
import org.alfresco.repo.dictionary.NamespaceDAOImpl.NamespaceRegistry;
import org.alfresco.repo.i18n.StaticMessageLookup;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.tenant.SingleTServiceImpl;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AlfrescoClientDataModelServicesFactory.DictionaryKey;
import org.alfresco.solr.client.AlfrescoModel;
import org.alfresco.util.ISO9075;
import org.alfresco.util.NumericEncoder;
import org.alfresco.util.Pair;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author Andy
 *
 */
public class AlfrescoSolrDataModel
{
    static enum FieldUse
    {
        FTS,          // Term/Phrase/Range/Fuzzy/Prefix/Proximity/Wild
        ID,           // Exact/ExactRange - Comparison, In, Upper, Lower
        FACET,        // Field, Range, Query
                      // Text fields will require cross language support to avoid tokenisation for facets
        STATS,        // Stats
        SORT,         // Locale
        SUGGESTION,
        COMPETION  
    }
    
    protected final static Logger log = LoggerFactory.getLogger(AlfrescoSolrDataModel.class);

    private static final String CHANGE_SET = "CHANGE_SET";

    private static final String  TX = "TX";
    
    private static final String DEFAULT_TENANT = "_DEFAULT_";
    
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private static AlfrescoSolrDataModel model;

    private AlfrescoFieldType alfrescoFieldType;
    
    private TenantService tenantService;

    private NamespaceDAOImpl namespaceDAO;

    private DictionaryDAOImpl dictionaryDAO;
    
    private  Map<String,DictionaryComponent> dictionaryServices;

    private  Map<DictionaryKey,CMISAbstractDictionaryService> cmisDictionaryServices;
    
    private HashMap<String, Set<String>> modelErrors = new HashMap<String, Set<String>>();

    
    /**
     * @param id
     */
    public AlfrescoSolrDataModel()
    {
        tenantService = new SingleTServiceImpl();
        namespaceDAO = new NamespaceDAOImpl();
        namespaceDAO.setTenantService(tenantService);
        namespaceDAO.setNamespaceRegistryCache(new MemoryCache<String, NamespaceRegistry>());

        dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);
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

    public static String getTenantId(String tenant)
    {
        if((tenant == null) || tenant.equals(TenantService.DEFAULT_DOMAIN))
        {
            return DEFAULT_TENANT;
        }
        else
        {
            return tenant.replaceAll(":", "_.._");
        }
    }
    
    /**
     * 
     * @param tenant
     * @param aclId
     * @return <TENANT>:<ACLID>:A-<ACLID>
     */
    public static String getAclDocumentId(String tenant, Long aclId)
    {
        StringBuilder builder = new StringBuilder();
        // TODO - check and encode for ":"
        builder.append(getTenantId(tenant));
        builder.append(":");
        builder.append(NumericEncoder.encode(aclId));
        builder.append(":ACL");
        return builder.toString();
    }
    
    /**
     * 
     * @param tenant
     * @param aclChangeSetId
     * @return <TENANT>:CHANGE_SET:<aclChangeSetId <- max in doc if compressed>
     */
    public static String getAclChangeSetDocumentId(String tenant, Long aclChangeSetId)
    {
        StringBuilder builder = new StringBuilder();
        // TODO - check and encode for ":"
        builder.append(getTenantId(tenant));
        builder.append(":");
        builder.append(CHANGE_SET);
        builder.append(":");
        builder.append(NumericEncoder.encode(aclChangeSetId));
        return builder.toString();
    }
    
    /**
     * 
     * @param tenant
     * @param aclId
     * @return <TENANT>:<ACLID>: D-<DBID>
     */
    public static String getNodeDocumentId(String tenant, Long aclId, Long dbid)
    {
        StringBuilder builder = new StringBuilder();
        // TODO - check and encode for ":"
        builder.append(getTenantId(tenant));
        builder.append(":");
        builder.append(NumericEncoder.encode(aclId));
        builder.append(":");
        builder.append(NumericEncoder.encode(dbid));
        return builder.toString();
    }
    
    /**
     * 
     * @param tenant
     * @param txId
     * @return <TENANT>:CHANGE_SET:<txId <- max in doc if compressed>
     */
    public static String getTransactionDocumentId(String tenant, Long txId)
    {
        StringBuilder builder = new StringBuilder();
        // TODO - check and encode for ":"
        builder.append(getTenantId(tenant));
        builder.append(":");
        builder.append(TX);
        builder.append(":");
        builder.append(NumericEncoder.encode(txId));
        return builder.toString();
    }
    
    public static Term getLongTerm(String field, long longValue)
    {
        BytesRef bytes = new BytesRef();
        NumericUtils.longToPrefixCoded(longValue, 0, bytes);
        return new Term(field,bytes);
    }
    
    public static Term getLongTerm(String field, String stringValue)
    {
        long longValue = Long.parseLong(stringValue);
        return getLongTerm(field, longValue); 
    }
    
    
    /**
     * @param id
     * @return
     */
    public static AlfrescoSolrDataModel getInstance()
    {
        readWriteLock.readLock().lock();
        try
        {
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
            if (model == null)
            {
                model = new AlfrescoSolrDataModel();
            }
            return model;
        }
        finally
        {
            readWriteLock.writeLock().unlock();
        }

    }
    
    public NamespaceDAO getNamespaceDAO()
    {
        return namespaceDAO;
    }
    
    /**
     * TODO: Fix to load type filter/exclusions from somewhere sensible
     * @return
     */
    private QNameFilter getQNameFilter()
    {
        QNameFilter qnameFilter = null;
        FileSystemXmlApplicationContext ctx = null;

        File resourceDirectory = getResourceDirectory();
        File filterContext = new File(resourceDirectory, "alfresco/model/opencmis-qnamefilter-context.xml");

        if(!filterContext.exists())
        {
            log.warn("No type filter context found at " + filterContext.getAbsolutePath() + ", no type filtering");
            return qnameFilter;
        }
        
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
     * @return
     */
    public ClassLoader getResourceClassLoader()
    {

        File f = getResourceDirectory();
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
    
    private File getResourceDirectory()
    {
        File f = new File("alfrescoResources");
        return f;
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
     * Get all the field names into which we must copy the source data
     * TODO: Determine from the model
     * 
     * @param propertyQName
     * @return
     */
    public IndexedField getIndexedFieldNamesForProperty(QName propertyQName)
    {
        // TODO: Cache and throw on model refresh
        
        IndexedField indexedField = new IndexedField();
        PropertyDefinition propertyDefinition = getPropertyDefinition(propertyQName);
        if((propertyDefinition == null))
        { 
            return indexedField;
        }
        if(!propertyDefinition.isIndexed() && !propertyDefinition.isStoredInIndex())
        {
            return indexedField;
        }

        DataTypeDefinition dataTypeDefinition = propertyDefinition.getDataType();
        if(isTextField(propertyDefinition))
        { 
            if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.TRUE)
                    || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
            {
                indexedField.addField(getFieldForText(true, true, false, propertyDefinition), true, false);
                indexedField.addField(getFieldForText(false, true, false, propertyDefinition), false, false);
            }
            
            if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE)
                    || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
            {
                indexedField.addField(getFieldForText(true, false, false, propertyDefinition), true, false);
                indexedField.addField(getFieldForText(false, false, false, propertyDefinition), true, false);
                
            }

            if(dataTypeDefinition.getName().equals(DataTypeDefinition.TEXT))
            {
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE)
                        || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    indexedField.addField(getFieldForText(false, false, true, propertyDefinition), true, true);
                }   
            }
            
            if(dataTypeDefinition.getName().equals(DataTypeDefinition.MLTEXT))
            {
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE)
                        || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    indexedField.addField(getFieldForText(true, false, true, propertyDefinition), true, true);
                }   
            }
            
            if(isSuggestable(propertyQName))
            {
                indexedField.addField("suggest", false, false);
            }
        }
        else
        {
            indexedField.addField(getFieldForNonText(propertyDefinition), false, false);
        }

        return indexedField;

    }

    private boolean isTextField(PropertyDefinition propertyDefinition)
    {
        QName propertyDataTypeQName = propertyDefinition.getDataType().getName();
        if(propertyDataTypeQName.equals(DataTypeDefinition.MLTEXT))
        {
            return true;
        }
        else if(propertyDataTypeQName.equals(DataTypeDefinition.CONTENT))
        {
            return true;
        }
        else if(propertyDataTypeQName.equals(DataTypeDefinition.TEXT))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
         
    private boolean isSuggestable(QName propertyQName)
    {
        if(propertyQName == null)
        {
            return false;
        }
        return propertyQName.equals(ContentModel.PROP_NAME);
    }
    
    private boolean hasDocValues(QName propertyQName)
    {
        if(propertyQName == null)
        {
            return false;
        }
        return propertyQName.equals(ContentModel.PROP_CREATED) || propertyQName.equals(ContentModel.PROP_MODIFIED); 
    }
    
    private String getFieldForNonText(PropertyDefinition propertyDefinition)
    {
        StringBuilder builder = new StringBuilder();
        QName qName = propertyDefinition.getDataType().getName();
        builder.append(qName.getLocalName());
        builder.append("@");
        builder.append(propertyDefinition.isMultiValued() ? "m" : "s");
        builder.append(hasDocValues(propertyDefinition.getName()) ? "d" : "_");
        builder.append("@");
        builder.append(propertyDefinition.getName().toString());
        return builder.toString();
    }
    
    private String getFieldForText(boolean localised, boolean tokenised, boolean sort, PropertyDefinition propertyDefinition)
    {
        StringBuilder builder = new StringBuilder();
        QName qName = propertyDefinition.getDataType().getName();
        builder.append(qName.getLocalName());
        builder.append("@");
        if(!sort)
        {
            builder.append(localised ? "l" : "_");
            builder.append(tokenised ? "t" : "_");
        }
        else
        {
            builder.append("sort");
        }
        builder.append("@");
        builder.append(propertyDefinition.getName().toString());
        return builder.toString();
    }
    
    
//    /**
//     * Get the schema field to use for a specific use 
//     * TODO: Determine from the model
//     *
//     * @param propertyQName
//     * @param fieldUse
//     * @return
//     */
//    public String getSearchFieldNameForProperty(QName propertyQName, FieldUse fieldUse)
//    {
//        
//    }
    
    
//    public SortField getSortField(SchemaField field, boolean reverse)
//    {
//        // MNT-8557 fix, manually replace '%20' with ' '
//        String fieldNameToUse = field.getName().replaceAll("%20", " ");
//        PropertyDefinition propertyDefinition = getPropertyDefinition(fieldNameToUse);
//        if (propertyDefinition != null)
//        {
//            if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
//            {
//                // ignore locale store in the text field
//
//                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
//                {
//                    return new SortField(expandFieldName(fieldNameToUse) + ".sort", new TextSortFieldComparatorSource(), reverse);
//                }
//                else
//                {
//                    throw new UnsupportedOperationException("Ordering not supported for " + fieldNameToUse);
//                }
//            }
//            else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
//            {
//                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
//                {
//                    return new SortField(expandFieldName(fieldNameToUse) + ".sort", new MLTextSortFieldComparatorSource(), reverse);
//                }
//                else
//                {
//                    throw new UnsupportedOperationException("Ordering not supported for " + fieldNameToUse);
//                }
//            }
//            else
//            {
//                return Sorting.getStringSortField(expandFieldName(fieldNameToUse), reverse, field.sortMissingLast(), field.sortMissingFirst());
//            }
//        }
//        else
//        { 
//            return Sorting.getStringSortField(fieldNameToUse, reverse, field.sortMissingLast(), field.sortMissingFirst());
//        }
//
//    }
    
    private PropertyDefinition getPropertyDefinition(String fieldName)
    {
        QName rawPropertyName = QName.createQName(expandFieldName(fieldName).substring(1));
        QName propertyQName = QName.createQName(rawPropertyName.getNamespaceURI(), ISO9075.decode(rawPropertyName.getLocalName()));
        return getPropertyDefinition(propertyQName);
    }
    

    /**
     * @param propertyQName
     * @return
     */
    public PropertyDefinition getPropertyDefinition(QName propertyQName)
    {
        PropertyDefinition propertyDef = getDictionaryService(CMISStrictDictionaryService.DEFAULT).getProperty(propertyQName);
//        if ((propertyDef != null) && (propertyDef.getName().equals(ContentModel.PROP_AUTHOR)))
//        {
//            return new PropertyDefinitionWrapper(propertyDef);
//        }
//        else if ((propertyDef != null) && (propertyDef.getName().equals(ContentModel.PROP_CREATOR)))
//        {
//            return new PropertyDefinitionWrapper(propertyDef);
//        }
//        else if ((propertyDef != null) && (propertyDef.getName().equals(ContentModel.PROP_MODIFIER)))
//        {
//            return new PropertyDefinitionWrapper(propertyDef);
//        }
        return propertyDef;
    }
    
    
    private String expandFieldName(String fieldName)
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
        return expandedFieldName;

    }

    private String expandAttributeFieldName(String field)
    {
        String fieldName = field;
        // Check for any prefixes and expand to the full uri
        if (field.charAt(1) != '{')
        {
            int colonPosition = field.indexOf(':');
            if (colonPosition == -1)
            {
                // use the default namespace
                //fieldName = "@{" + getNamespaceDAO().getNamespaceURI("") + "}" + field.substring(1);
            }
            else
            {
                // find the prefix
                //fieldName = "@{" + getNamespaceDAO().getNamespaceURI(field.substring(1, colonPosition)) + "}" + field.substring(colonPosition + 1);
            }
        }
        return fieldName;
    }
    
//    public PropertyDefinition getPropertyDefinition(QName propertyQName)
//    {
//        PropertyDefinition propertyDef = getDictionaryService(CMISStrictDictionaryService.DEFAULT).getProperty(propertyQName);
//        if ((propertyDef != null) && (propertyDef.getName().equals(ContentModel.PROP_AUTHOR)))
//        {
//            return new PropertyDefinitionWrapper(propertyDef);
//        }
//        else if ((propertyDef != null) && (propertyDef.getName().equals(ContentModel.PROP_CREATOR)))
//        {
//            return new PropertyDefinitionWrapper(propertyDef);
//        }
//        else if ((propertyDef != null) && (propertyDef.getName().equals(ContentModel.PROP_MODIFIER)))
//        {
//            return new PropertyDefinitionWrapper(propertyDef);
//        }
//        return propertyDef;
//    }
    
    
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
    
//    public static class TextSortFieldComparatorSource extends FieldComparatorSource
//    {
//
//        /*
//         * (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparatorSource#newComparator(java.lang.String, int, int, boolean)
//         */
//        @Override
//        public FieldComparator newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException
//        {
//            return new TextSortFieldComparator(numHits, fieldname, I18NUtil.getLocale());
//        }
//
//    }

    /** Sorts by field's natural Term sort order, using
     *  ordinals.  This is functionally equivalent to {@link
     *  org.apache.lucene.search.FieldComparator.TermValComparator}, but it first resolves the string
     *  to their relative ordinal positions (using the index
     *  returned by {@link FieldCache#getTermsIndex}), and
     *  does most comparisons using the ordinals.  For medium
     *  to large results, this comparator will be much faster
     *  than {@link org.apache.lucene.search.FieldComparator.TermValComparator}.  For very small
     *  result sets it may be slower. */
    public static final class TermOrdValComparator extends FieldComparator<BytesRef> {
      /* Ords for each slot.
         @lucene.internal */
      final int[] ords;

      /* Values for each slot.
         @lucene.internal */
      final BytesRef[] values;

      /* Which reader last copied a value into the slot. When
         we compare two slots, we just compare-by-ord if the
         readerGen is the same; else we must compare the
         values (slower).
         @lucene.internal */
      final int[] readerGen;

      /* Gen of current reader we are on.
         @lucene.internal */
      int currentReaderGen = -1;

      /* Current reader's doc ord/values.
         @lucene.internal */
      SortedDocValues termsIndex;

      private final String field;

      /* Bottom slot, or -1 if queue isn't full yet
         @lucene.internal */
      int bottomSlot = -1;

      /* Bottom ord (same as ords[bottomSlot] once bottomSlot
         is set).  Cached for faster compares.
         @lucene.internal */
      int bottomOrd;

      /* True if current bottom slot matches the current
         reader.
         @lucene.internal */
      boolean bottomSameReader;

      /* Bottom value (same as values[bottomSlot] once
         bottomSlot is set).  Cached for faster compares.
        @lucene.internal */
      BytesRef bottomValue;

      /** Set by setTopValue. */
      BytesRef topValue;
      boolean topSameReader;
      int topOrd;

      private int docBase;

      final BytesRef tempBR = new BytesRef();

      /** -1 if missing values are sorted first, 1 if they are
       *  sorted last */
      final int missingSortCmp;
      
      /** Which ordinal to use for a missing value. */
      final int missingOrd;

      /** Creates this, sorting missing values first. */
      public TermOrdValComparator(int numHits, String field) {
        this(numHits, field, false);
      }

      /** Creates this, with control over how missing values
       *  are sorted.  Pass sortMissingLast=true to put
       *  missing values at the end. */
      public TermOrdValComparator(int numHits, String field, boolean sortMissingLast) {
        ords = new int[numHits];
        values = new BytesRef[numHits];
        readerGen = new int[numHits];
        this.field = field;
        if (sortMissingLast) {
          missingSortCmp = 1;
          missingOrd = Integer.MAX_VALUE;
        } else {
          missingSortCmp = -1;
          missingOrd = -1;
        }
      }

      @Override
      public int compare(int slot1, int slot2) {
        if (readerGen[slot1] == readerGen[slot2]) {
          return ords[slot1] - ords[slot2];
        }

        final BytesRef val1 = values[slot1];
        final BytesRef val2 = values[slot2];
        if (val1 == null) {
          if (val2 == null) {
            return 0;
          }
          return missingSortCmp;
        } else if (val2 == null) {
          return -missingSortCmp;
        }
        return val1.compareTo(val2);
      }

      @Override
      public int compareBottom(int doc) {
        assert bottomSlot != -1;
        int docOrd = termsIndex.getOrd(doc);
        if (docOrd == -1) {
          docOrd = missingOrd;
        }
        if (bottomSameReader) {
          // ord is precisely comparable, even in the equal case
          return bottomOrd - docOrd;
        } else if (bottomOrd >= docOrd) {
          // the equals case always means bottom is > doc
          // (because we set bottomOrd to the lower bound in
          // setBottom):
          return 1;
        } else {
          return -1;
        }
      }

      @Override
      public void copy(int slot, int doc) {
        int ord = termsIndex.getOrd(doc);
        if (ord == -1) {
          ord = missingOrd;
          values[slot] = null;
        } else {
          assert ord >= 0;
          if (values[slot] == null) {
            values[slot] = new BytesRef();
          }
          termsIndex.lookupOrd(ord, values[slot]);
        }
        ords[slot] = ord;
        readerGen[slot] = currentReaderGen;
      }
      
      @Override
      public FieldComparator<BytesRef> setNextReader(AtomicReaderContext context) throws IOException {
        docBase = context.docBase;
        termsIndex = FieldCache.DEFAULT.getTermsIndex(context.reader(), field);
        currentReaderGen++;

        if (topValue != null) {
          // Recompute topOrd/SameReader
          int ord = termsIndex.lookupTerm(topValue);
          if (ord >= 0) {
            topSameReader = true;
            topOrd = ord;
          } else {
            topSameReader = false;
            topOrd = -ord-2;
          }
        } else {
          topOrd = missingOrd;
          topSameReader = true;
        }
        //System.out.println("  setNextReader topOrd=" + topOrd + " topSameReader=" + topSameReader);

        if (bottomSlot != -1) {
          // Recompute bottomOrd/SameReader
          setBottom(bottomSlot);
        }

        return this;
      }
      
      @Override
      public void setBottom(final int bottom) {
        bottomSlot = bottom;

        bottomValue = values[bottomSlot];
        if (currentReaderGen == readerGen[bottomSlot]) {
          bottomOrd = ords[bottomSlot];
          bottomSameReader = true;
        } else {
          if (bottomValue == null) {
            // missingOrd is null for all segments
            assert ords[bottomSlot] == missingOrd;
            bottomOrd = missingOrd;
            bottomSameReader = true;
            readerGen[bottomSlot] = currentReaderGen;
          } else {
            final int ord = termsIndex.lookupTerm(bottomValue);
            if (ord < 0) {
              bottomOrd = -ord - 2;
              bottomSameReader = false;
            } else {
              bottomOrd = ord;
              // exact value match
              bottomSameReader = true;
              readerGen[bottomSlot] = currentReaderGen;            
              ords[bottomSlot] = bottomOrd;
            }
          }
        }
      }

      @Override
      public void setTopValue(BytesRef value) {
        // null is fine: it means the last doc of the prior
        // search was missing this value
        topValue = value;
        //System.out.println("setTopValue " + topValue);
      }

      @Override
      public BytesRef value(int slot) {
        return values[slot];
      }

      @Override
      public int compareTop(int doc) {

        int ord = termsIndex.getOrd(doc);
        if (ord == -1) {
          ord = missingOrd;
        }

        if (topSameReader) {
          // ord is precisely comparable, even in the equal
          // case
          //System.out.println("compareTop doc=" + doc + " ord=" + ord + " ret=" + (topOrd-ord));
          return topOrd - ord;
        } else if (ord <= topOrd) {
          // the equals case always means doc is < value
          // (because we set lastOrd to the lower bound)
          return 1;
        } else {
          return -1;
        }
      }

      @Override
      public int compareValues(BytesRef val1, BytesRef val2) {
        if (val1 == null) {
          if (val2 == null) {
            return 0;
          }
          return missingSortCmp;
        } else if (val2 == null) {
          return -missingSortCmp;
        }
        return val1.compareTo(val2);
      }
    }

    /**
     * @param schema
     */
    public void setAlfrescoFieldType(AlfrescoFieldType alfrescoFieldType)
    {
        this.alfrescoFieldType = alfrescoFieldType;
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
     * @return
     */
    public Map<String, Set<String>> getModelErrors()
    {
       return modelErrors;
    }
    
    
//    public static final class TextSortFieldComparator extends FieldComparator<String>
//    {
//
//        private final String[] values;
//
//        private String[] currentReaderValues;
//
//        private final String field;
//
//        final Collator collator;
//
//        private String bottom;
//        
//        private String top;
//
//        TextSortFieldComparator(int numHits, String field, Locale locale)
//        {
//            values = new String[numHits];
//            this.field = field;
//            collator = Collator.getInstance(locale);
//        }
//
//        /*
//         * (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#compare(int, int)
//         */
//        @Override
//        public int compare(int slot1, int slot2)
//        {
//            final String val1 = values[slot1];
//            final String val2 = values[slot2];
//            if (val1 == null)
//            {
//                if (val2 == null)
//                {
//                    return 0;
//                }
//                return -1;
//            }
//            else if (val2 == null)
//            {
//                return 1;
//            }
//            return collator.compare(val1, val2);
//        }
//
//        /*
//         * (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#compareBottom(int)
//         */
//        @Override
//        public int compareBottom(int doc)
//        {
//            final String val2 = stripLocale(currentReaderValues[doc]);
//            if (bottom == null)
//            {
//                if (val2 == null)
//                {
//                    return 0;
//                }
//                return -1;
//            }
//            else if (val2 == null)
//            {
//                return 1;
//            }
//            return collator.compare(bottom, val2);
//        }
//
//        /*
//         * (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#copy(int, int)
//         */
//        @Override
//        public void copy(int slot, int doc)
//        {
//            values[slot] = stripLocale(currentReaderValues[doc]);
//        }
//
////        public void setNextReader(IndexReader reader, int docBase) throws IOException
////        {
////            currentReaderValues = FieldCache.DEFAULT.getStrings(reader, field);
////        }
//
//        /*
//         * (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#setBottom(int)
//         */
//        @Override
//        public void setBottom(final int bottom)
//        {
//            this.bottom = values[bottom];
//        }
//
////        public Comparable value(int slot)
////        {
////            return values[slot];
////        }
//
//        private String stripLocale(String withLocale)
//        {
//            if (withLocale == null)
//            {
//                return withLocale;
//            }
//            else if (withLocale.startsWith("\u0000"))
//            {
//                return withLocale.substring(withLocale.indexOf('\u0000', 1) + 1);
//            }
//            else
//            {
//                return withLocale;
//            }
//        }
//
//        /* (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#setTopValue(java.lang.Object)
//         */
//        @Override
//        public void setTopValue(String value)
//        {
//           this.top = value;
//            
//        }
//
//        /* (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#compareTop(int)
//         */
//        @Override
//        public int compareTop(int doc) throws IOException
//        {
//            final String val2 = stripLocale(currentReaderValues[doc]);
//            if (top == null)
//            {
//                if (val2 == null)
//                {
//                    return 0;
//                }
//                return -1;
//            }
//            else if (val2 == null)
//            {
//                return 1;
//            }
//            return collator.compare(top, val2);
//        }
//
//        /* (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparator#setNextReader(org.apache.lucene.index.AtomicReaderContext)
//         */
//        @Override
//        public FieldComparator setNextReader(AtomicReaderContext context) throws IOException
//        {
//            currentReaderValues = FieldCache.DEFAULT.getTerms(reader, field, setDocsWithField)getStrings(context, field);
//        }
//    }

//    public static class MLTextSortFieldComparatorSource extends FieldComparatorSource
//    {
//
//        /*
//         * (non-Javadoc)
//         * @see org.apache.lucene.search.FieldComparatorSource#newComparator(java.lang.String, int, int, boolean)
//         */
//        @Override
//        public FieldComparator newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException
//        {
//            return new MLTextSortFieldComparator(numHits, fieldname, I18NUtil.getLocale());
//        }
//
//    }

//    public static final class MLTextSortFieldComparator extends FieldComparator
//    {
//
//        private final String[] values;
//
//        private String[] currentReaderValues;
//
//        private final String field;
//
//        final Collator collator;
//
//        private String bottom;
//
//        Locale collatorLocale;
//
//        MLTextSortFieldComparator(int numHits, String field, Locale collatorLocale)
//        {
//            values = new String[numHits];
//            this.field = field;
//            this.collatorLocale = collatorLocale;
//            collator = Collator.getInstance(collatorLocale);
//        }
//
//        public int compare(int slot1, int slot2)
//        {
//            final String val1 = values[slot1];
//            final String val2 = values[slot2];
//            if (val1 == null)
//            {
//                if (val2 == null)
//                {
//                    return 0;
//                }
//                return -1;
//            }
//            else if (val2 == null)
//            {
//                return 1;
//            }
//            return collator.compare(val1, val2);
//        }
//
//        public int compareBottom(int doc)
//        {
//            final String val2 = findBestValue(currentReaderValues[doc]);
//            if (bottom == null)
//            {
//                if (val2 == null)
//                {
//                    return 0;
//                }
//                return -1;
//            }
//            else if (val2 == null)
//            {
//                return 1;
//            }
//            return collator.compare(bottom, val2);
//        }
//
//        public void copy(int slot, int doc)
//        {
//            values[slot] = findBestValue(currentReaderValues[doc]);
//        }
//
//        public void setNextReader(IndexReader reader, int docBase) throws IOException
//        {
//            currentReaderValues = FieldCache.DEFAULT.getStrings(reader, field);
//        }
//
//        public void setBottom(final int bottom)
//        {
//            this.bottom = values[bottom];
//        }
//
//        public Comparable value(int slot)
//        {
//            return values[slot];
//        }
//
//        private String findBestValue(String withLocale)
//        {
//            // split strin into MLText object
//            if (withLocale == null)
//            {
//                return withLocale;
//            }
//            else if (withLocale.startsWith("\u0000"))
//            {
//                MLText mlText = new MLText();
//                String[] parts = withLocale.split("\u0000");
//                for (int i = 0; (i + 2) <= parts.length; i += 3)
//                {
//                    Locale locale = null;
//                    String[] localeParts = parts[i + 1].split("_");
//                    if (localeParts.length == 1)
//                    {
//                        locale = new Locale(localeParts[0]);
//                    }
//                    else if (localeParts.length == 2)
//                    {
//                        locale = new Locale(localeParts[0], localeParts[1]);
//                    }
//                    else if (localeParts.length == 3)
//                    {
//                        locale = new Locale(localeParts[0], localeParts[1], localeParts[2]);
//                    }
//                    if (locale != null)
//                    {
//                        if (i + 2 == parts.length)
//                        {
//                            mlText.addValue(locale, "");
//                        }
//                        else
//                        {
//                            mlText.addValue(locale, parts[i + 2]);
//                        }
//                    }
//                }
//                return mlText.getClosestValue(collatorLocale);
//            }
//            else
//            {
//                return withLocale;
//            }
//        }
//    }

   
    public static class IndexedField
    {

        private List<FieldInstance> fields = new LinkedList<>();

        public IndexedField()
        {
            super();
        }
        
        public IndexedField(String prefix, boolean localised, boolean sort)
        {
            this();
            addField(prefix, localised, sort);
        }
        
        public List<FieldInstance> getFields()
        {
            return fields;
        }

        public void addField(String prefix, boolean localised, boolean sort)
        {
            fields.add(new FieldInstance(prefix, localised, sort));
        }
        
    }
    
    public static class FieldInstance
    {
        String field;
        boolean localised;
        boolean sort;
        
        /**
         * @param prefix
         * @param localised2
         * @param sort2
         */
        public FieldInstance(String field, boolean localised, boolean sort)
        {
            this.field = field;
            this.localised = localised;
            this.sort = sort;
        }

        public String getField()
        {
            return field;
        }
        
        public boolean isLocalised()
        {
            return localised;
        }

        public boolean isSort()
        {
            return sort;
        }   
    }
}
