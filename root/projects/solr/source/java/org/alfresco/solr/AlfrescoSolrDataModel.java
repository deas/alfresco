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
package org.alfresco.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.cache.MemoryCache;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.DictionaryDAOImpl;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.repo.dictionary.NamespaceDAOImpl;
import org.alfresco.repo.dictionary.DictionaryDAOImpl.DictionaryRegistry;
import org.alfresco.repo.dictionary.NamespaceDAOImpl.NamespaceRegistry;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneAnalyser;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.analysis.AlfrescoStandardAnalyser;
import org.alfresco.repo.search.impl.lucene.analysis.DateTimeAnalyser;
import org.alfresco.repo.search.impl.parsers.AlfrescoFunctionEvaluationContext;
import org.alfresco.repo.search.impl.parsers.FTSParser;
import org.alfresco.repo.search.impl.parsers.FTSQueryParser;
import org.alfresco.repo.search.impl.querymodel.Constraint;
import org.alfresco.repo.search.impl.querymodel.Ordering;
import org.alfresco.repo.search.impl.querymodel.QueryModelFactory;
import org.alfresco.repo.search.impl.querymodel.QueryOptions.Connective;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilder;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryModelFactory;
import org.alfresco.repo.tenant.SingleTServiceImpl;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.query.LuceneQueryBuilderContextSolrImpl;
import org.alfresco.solr.query.SolrQueryParser;
import org.alfresco.util.ISO9075;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.Sorting;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Andy TODO: Dual tokenisation support?
 */
public class AlfrescoSolrDataModel
{
    private static HashMap<String, AlfrescoSolrDataModel> models = new HashMap<String, AlfrescoSolrDataModel>();

    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static HashMap<String, NonDictionaryField> nonDictionaryFields = new HashMap<String, NonDictionaryField>();
    
    public static HashMap<String, NonDictionaryField> additionalContentFields = new HashMap<String, NonDictionaryField>();
    
    public static HashMap<String, NonDictionaryField> additionalTextFields = new HashMap<String, NonDictionaryField>();
    
    public static HashMap<String, NonDictionaryField> additionalMlTextFields = new HashMap<String, NonDictionaryField>();

    private TenantService tenantService;

    private NamespaceDAOImpl namespaceDAO;

    private DictionaryDAOImpl dictionaryDAO;

    private DictionaryComponent dictionaryComponent;

    private boolean testing = true;

    private AlfrescoDataType alfrescoDataType;

    static
    {

        addNonDictionaryField(LuceneQueryParser.FIELD_ID, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(LuceneQueryParser.FIELD_TX, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(LuceneQueryParser.FIELD_PARENT, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(LuceneQueryParser.FIELD_LINKASPECT, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(LuceneQueryParser.FIELD_PATH, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(LuceneQueryParser.FIELD_ANCESTOR, Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(LuceneQueryParser.FIELD_ISCONTAINER, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(LuceneQueryParser.FIELD_ISCATEGORY, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(LuceneQueryParser.FIELD_QNAME, Store.YES, Index.ANALYZED, TermVector.NO, true);
        addNonDictionaryField(LuceneQueryParser.FIELD_ISROOT, Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(LuceneQueryParser.FIELD_PRIMARYASSOCTYPEQNAME, Store.YES, Index.ANALYZED, TermVector.NO, false);
        addNonDictionaryField(LuceneQueryParser.FIELD_ISNODE, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(LuceneQueryParser.FIELD_ASSOCTYPEQNAME, Store.YES, Index.ANALYZED, TermVector.NO, true);
        addNonDictionaryField(LuceneQueryParser.FIELD_PRIMARYPARENT, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(LuceneQueryParser.FIELD_TYPE, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(LuceneQueryParser.FIELD_ASPECT, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(LuceneQueryParser.FIELD_FTSSTATUS, Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(LuceneQueryParser.FIELD_DBID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(LuceneQueryParser.FIELD_TXID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(LuceneQueryParser.FIELD_TXCOMMITTIME, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        
        addNonDictionaryField(LuceneQueryParser.FIELD_ACLID, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(LuceneQueryParser.FIELD_READER, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(LuceneQueryParser.FIELD_OWNER, Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        
        addAdditionalContentField(".size", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".locale", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".mimetype", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".encoding", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".contentDocId", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".transformationException", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".transformationTime", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".transformationStatus", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".__", Store.NO, Index.ANALYZED, TermVector.NO, false);
        
        addAdditionalTextField(".__", Store.NO, Index.ANALYZED, TermVector.NO, true);
        addAdditionalTextField(".u", Store.NO, Index.ANALYZED, TermVector.NO, true);
        addAdditionalTextField(".__.u", Store.NO, Index.ANALYZED, TermVector.NO, true);
        addAdditionalTextField(".sort", Store.NO, Index.ANALYZED, TermVector.NO, false);
        
        addAdditionalMlTextField(".__", Store.NO, Index.ANALYZED, TermVector.NO, true);
        addAdditionalMlTextField(".u", Store.NO, Index.ANALYZED, TermVector.NO, true);
        addAdditionalMlTextField(".__.u", Store.NO, Index.ANALYZED, TermVector.NO, true);
        addAdditionalMlTextField(".sort", Store.NO, Index.ANALYZED, TermVector.NO, false);
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
                model = new AlfrescoSolrDataModel();
                models.put(id, model);
            }
            return model;
        }
        finally
        {
            readWriteLock.writeLock().unlock();
        }

    }

    private AlfrescoSolrDataModel()
    {

        tenantService = new SingleTServiceImpl();
        namespaceDAO = new NamespaceDAOImpl();
        namespaceDAO.setTenantService(tenantService);
        namespaceDAO.setNamespaceRegistryCache(new MemoryCache<String, NamespaceRegistry>());

        dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);
        dictionaryDAO.setTenantService(tenantService);
        dictionaryDAO.setDictionaryRegistryCache(new MemoryCache<String, DictionaryRegistry>());

        dictionaryComponent = new DictionaryComponent();
        dictionaryComponent.setDictionaryDAO(dictionaryDAO);
    }

    public DictionaryService getDictionaryService()
    {
        return dictionaryComponent;
    }
    
    public NamespaceDAO getNamespaceDAO()
    {
         return namespaceDAO;
    }

    /**
     * @return
     */
    public MLAnalysisMode getMLAnalysisMode()
    {
        return MLAnalysisMode.EXACT_LANGUAGE_AND_ALL;
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

        for(String additionalContentFieldEnding : additionalContentFields.keySet())
        {
            if(field.getName().endsWith(additionalContentFieldEnding) && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalContentFieldEnding.length()))) != null))
            {
                return additionalContentFields.get(additionalContentFieldEnding).index;
            }
        }
        
        for(String additionalTextFieldEnding : additionalTextFields.keySet())
        {
            if(field.getName().endsWith(additionalTextFieldEnding) && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalTextFieldEnding.length()))) != null))
            {
                return additionalTextFields.get(additionalTextFieldEnding).index;
            }
        }
        
        for(String additionalMlTextFieldEnding : additionalMlTextFields.keySet())
        {
            if(field.getName().endsWith(additionalMlTextFieldEnding) && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalMlTextFieldEnding.length()))) != null))
            {
                return additionalMlTextFields.get(additionalMlTextFieldEnding).index;
            }
        }
        
        return Index.ANALYZED;
    }

    private PropertyDefinition getPropertyDefinition(String fieldName)
    {
        QName rawPropertyName = QName.createQName(fieldName.substring(1));
        QName propertyName = QName.createQName(rawPropertyName.getNamespaceURI(), ISO9075.decode(rawPropertyName.getLocalName()));
        PropertyDefinition propertyDef = getDictionaryService().getProperty(propertyName);
        return propertyDef;
    }

    /**
     * @param field
     * @return
     */
    public Store getFieldStore(SchemaField field)
    {
        if(testing)
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

        for(String additionalContentFieldEnding : additionalContentFields.keySet())
        {
            if(field.getName().endsWith(additionalContentFieldEnding) && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalContentFieldEnding.length()))) != null))
            {
                return additionalContentFields.get(additionalContentFieldEnding).store;
            }
        }
        
        for(String additionalTextFieldEnding : additionalTextFields.keySet())
        {
            if(field.getName().endsWith(additionalTextFieldEnding) && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalTextFieldEnding.length()))) != null))
            {
                return additionalTextFields.get(additionalTextFieldEnding).store;
            }
        }
        
        for(String additionalMlTextFieldEnding : additionalMlTextFields.keySet())
        {
            if(field.getName().endsWith(additionalMlTextFieldEnding) && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalMlTextFieldEnding.length()))) != null))
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
            return false;
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
        
        for(String additionalContentFieldEnding : additionalContentFields.keySet())
        {
            if(field.getName().endsWith(additionalContentFieldEnding) && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalContentFieldEnding.length()))) != null))
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
        
        for(String additionalTextFieldEnding : additionalTextFields.keySet())
        {
            if(field.getName().endsWith(additionalTextFieldEnding) && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalTextFieldEnding.length()))) != null))
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
        
        for(String additionalMlTextFieldEnding : additionalMlTextFields.keySet())
        {
            if(field.getName().endsWith(additionalMlTextFieldEnding) && (getPropertyDefinition(field.getName().substring(0, (field.getName().length() - additionalMlTextFieldEnding.length()))) != null))
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
        SolrLuceneAnalyser defaultAnalyser = new SolrLuceneAnalyser(getDictionaryService(), getMLAnalysisMode(), alfrescoDataType.getAnalyzer().getDefaultAnalyser(), this);
        LuceneQueryParser parser = new LuceneQueryParser("TEXT", defaultAnalyser);
        parser.setDefaultOperator(Operator.AND);
        parser.setNamespacePrefixResolver(namespaceDAO);
        parser.setDictionaryService(getDictionaryService());
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
    public void putModel(M2Model model)
    {
        dictionaryDAO.putModel(model);
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

    public void generateSchema(XMLWriter xmlWriter) throws IOException, SAXException
    {
        xmlWriter.startDocument();

        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "name", "name", null, "alfresco");
        attrs.addAttribute("", "version", "version", null, "1.0");
        xmlWriter.startElement("", "schema", "schema", attrs);

        xmlWriter.startElement("", "types", "types", new AttributesImpl());
        writeFieldType(xmlWriter, "alfrescoDataType", "org.alfresco.solr.AlfrescoDataType");
        xmlWriter.endElement("", "types", "types");

        xmlWriter.startElement("", "fields", "fields", new AttributesImpl());

        for (NonDictionaryField field : nonDictionaryFields.values())
        {
            writeField(xmlWriter, field);
        }

        for (QName modelName : dictionaryComponent.getAllModels())
        {
            for (QName propertyName : dictionaryComponent.getProperties(modelName))
            {
                PropertyDefinition propertyDefinition = dictionaryComponent.getProperty(propertyName);
                writeField(xmlWriter, propertyDefinition);
            }
        }
        xmlWriter.endElement("", "fields", "fields");

        xmlWriter.startElement("", "uniqueKey", "uniqueKey", new AttributesImpl());
        xmlWriter.write("ID");
        xmlWriter.endElement("", "uniqueKey", "uniqueKey");

        xmlWriter.startElement("", "defaultSearchField", "defaultSearchField", new AttributesImpl());
        xmlWriter.write("ID");
        xmlWriter.endElement("", "defaultSearchField", "defaultSearchField");

        xmlWriter.endElement("", "schema", "schema");

        xmlWriter.endDocument();
        xmlWriter.close();
    }

    private void writeFieldType(XMLWriter xmlWriter, String name, String clazz) throws SAXException
    {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "name", "name", null, name);
        attrs.addAttribute("", "class", "class", null, clazz);
        xmlWriter.startElement("", "fieldType", "fieldType", attrs);
        xmlWriter.startElement("", "analyzer", "analyzer", new AttributesImpl());
        AttributesImpl tokenizerAttrs = new AttributesImpl();
        tokenizerAttrs.addAttribute("", "class", "class", null, "org.apache.solr.analysis.WhitespaceTokenizerFactory");
        xmlWriter.startElement("", "tokenizer", "tokenizer", tokenizerAttrs);
        xmlWriter.endElement("", "tokenizer", "tokenizer");
        AttributesImpl filterAttrs = new AttributesImpl();
        filterAttrs.addAttribute("", "class", "class", null, "org.apache.solr.analysis.LowerCaseFilterFactory");
        xmlWriter.startElement("", "filter", "filter", filterAttrs);
        xmlWriter.endElement("", "filter", "filter");
        xmlWriter.endElement("", "analyzer", "analyzer");
        xmlWriter.endElement("", "fieldType", "fieldType");
    }

    private void writeField(XMLWriter xmlWriter, PropertyDefinition propertyDefinition) throws SAXException
    {
        String name = "@" + propertyDefinition.getName().toString();

        Store store = propertyDefinition.isStoredInIndex() ? Store.YES : Store.NO;
        boolean multiValued = propertyDefinition.isMultiValued();
        if (propertyDefinition.isIndexed())
        {
            switch (propertyDefinition.getIndexTokenisationMode())
            {
            case BOTH:
                writeField(xmlWriter, name, "alfrescoDataType", store, Index.ANALYZED, TermVector.NO, multiValued);
                if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.CONTENT))
                {
                    writeField(xmlWriter, name+".size", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".locale", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".mimetype", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".encoding", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".contentDataId", "alfrescoDataType",  Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".transformationException", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".transformationTime", "alfrescoDataType",  Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".transformationStatus", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".__", "alfrescoDataType",  Store.NO, Index.ANALYZED, TermVector.NO, false);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
                {
                    writeField(xmlWriter, name+".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
                    writeField(xmlWriter, name+".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name+".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name+".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
                {
                    writeField(xmlWriter, name+".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
                    writeField(xmlWriter, name+".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name+".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name+".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.DATETIME))
                {
                    DataTypeDefinition dataType = propertyDefinition.getDataType();
                    String analyserClassName = dataType.getAnalyserClassName();
                    if (analyserClassName.equals(DateTimeAnalyser.class.getCanonicalName()))
                    {
                        writeField(xmlWriter, name + ".sort", "alfrescoDataType",  Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, multiValued);
                    }
                    else
                    {
                        // nothing
                    }
                }
                else
                {
                    // nothing
                }
                break;
            case FALSE:
                writeField(xmlWriter, name, "alfrescoDataType", store, Index.NOT_ANALYZED, TermVector.NO, multiValued);
                if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.CONTENT))
                {
                    writeField(xmlWriter, name+".size", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".locale", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".mimetype", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".encoding", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".contentDataId", "alfrescoDataType",  Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".transformationException", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".transformationTime", "alfrescoDataType",  Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".transformationStatus", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".__", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
                {
                    writeField(xmlWriter, name+".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
                    writeField(xmlWriter, name+".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name+".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name+".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
                {
                    writeField(xmlWriter, name+".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
                    writeField(xmlWriter, name+".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name+".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name+".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                }
                break;
            case TRUE:
                writeField(xmlWriter, name, "alfrescoDataType", store, Index.ANALYZED, TermVector.NO, multiValued);
                if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.CONTENT))
                {
                    writeField(xmlWriter, name+".size", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".locale", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".mimetype", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".encoding", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".contentDataId", "alfrescoDataType",  Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".transformationException", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".transformationTime", "alfrescoDataType",  Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".transformationStatus", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name+".__", "alfrescoDataType",  Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
                {
                    writeField(xmlWriter, name+".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
                    writeField(xmlWriter, name+".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name+".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name+".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
                {
                    writeField(xmlWriter, name+".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
                    writeField(xmlWriter, name+".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name+".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name+".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.DATETIME))
                {
                    DataTypeDefinition dataType = propertyDefinition.getDataType();
                    String analyserClassName = dataType.getAnalyserClassName();
                    if (analyserClassName.equals(DateTimeAnalyser.class.getCanonicalName()))
                    {
                        writeField(xmlWriter, name + ".sort", "alfrescoDataType",  Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, multiValued);
                    }
                    else
                    {
                        // nothing
                    }
                }
                break;
            default:
            }

        }
        else
        {
            writeField(xmlWriter, name, "alfrescoDataType", store, Index.NO, TermVector.NO, multiValued);
        }
    }

    private void writeField(XMLWriter xmlWriter, NonDictionaryField field) throws SAXException
    {
        writeField(xmlWriter, field.name, "alfrescoDataType", field.store, field.index, field.termVector, field.multiValued);
    }

    private void writeField(XMLWriter xmlWriter, String name, String fieldType, Store store, Index index, TermVector termVector, boolean multiValued) throws SAXException
    {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "name", "name", null, name);
        attrs.addAttribute("", "type", "type", null, fieldType);

        attrs.addAttribute("", "indexed", "indexed", null, Boolean.valueOf(index != Index.NO).toString());
        if ((index == Index.NOT_ANALYZED_NO_NORMS) || (index == Index.ANALYZED_NO_NORMS))
        {
            attrs.addAttribute("", "omitNorms", "omitNorms", null, "true");
        }

        attrs.addAttribute("", "stored", "stored", null, Boolean.valueOf(store != Store.NO).toString());

        attrs.addAttribute("", "multiValued", "multiValued", null, Boolean.valueOf(multiValued).toString());

        xmlWriter.startElement("", "field", "field", attrs);
        xmlWriter.endElement("", "field", "field");
    }

    public LuceneQueryParser getLuceneQueryParser(String defaultField, String query, Operator defaultOperator,  IndexReader indexReader)
    {
        // TODO: Ordering
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
        searchParameters.setQuery(query);
        SolrLuceneAnalyser analyzer = new SolrLuceneAnalyser(getDictionaryService(), getMLAnalysisMode(), alfrescoDataType.getAnalyzer().getDefaultAnalyser(), this);
        SolrQueryParser parser = new SolrQueryParser(defaultField, analyzer);
        parser.setDefaultOperator(defaultOperator);
        parser.setNamespacePrefixResolver(namespaceDAO);
        parser.setDictionaryService(dictionaryComponent);
        parser.setTenantService(tenantService);
        parser.setSearchParameters(searchParameters);
        parser.setDefaultSearchMLAnalysisMode(getMLAnalysisMode());
        parser.setIndexReader(indexReader);
        parser.setAllowLeadingWildcard(true);
        
        return parser;
    }
    
    public Query getFTSQuery(SearchParameters searchParameters, IndexReader indexReader) throws ParseException
    {
        QueryModelFactory factory = new LuceneQueryModelFactory();
        AlfrescoFunctionEvaluationContext functionContext = new AlfrescoFunctionEvaluationContext(namespaceDAO,dictionaryComponent,
                NamespaceService.CONTENT_MODEL_1_0_URI);

        FTSParser.Mode mode;

        if(searchParameters.getDefaultFTSOperator() == org.alfresco.service.cmr.search.SearchParameters.Operator.AND)
        {
            mode = FTSParser.Mode.DEFAULT_CONJUNCTION;
        }
        else
        {
            mode = FTSParser.Mode.DEFAULT_DISJUNCTION;
        }
        
        

        Constraint constraint = FTSQueryParser.buildFTS(searchParameters.getQuery(), factory, functionContext, null, null, mode, searchParameters.getDefaultFTSOperator() == org.alfresco.service.cmr.search.SearchParameters.Operator.OR ? Connective.OR : Connective.AND,
                new HashMap<String, String>(), searchParameters.getDefaultFieldName());
        org.alfresco.repo.search.impl.querymodel.Query queryModelQuery = factory.createQuery(null, null, constraint, new ArrayList<Ordering>());

        LuceneQueryBuilder builder = (LuceneQueryBuilder) queryModelQuery;
        
        LuceneQueryBuilderContextSolrImpl luceneContext = new LuceneQueryBuilderContextSolrImpl(dictionaryComponent, namespaceDAO, tenantService, searchParameters, getMLAnalysisMode(),
                indexReader, alfrescoDataType.getAnalyzer(), this);
        
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
        return luceneQuery; 
    }

    /**
     * @param field
     * @param reverse
     */
    public SortField getSortField(SchemaField field, boolean reverse)
    {   
        return Sorting.getStringSortField(field.getName(), reverse, field.sortMissingLast(),field.sortMissingFirst());   
    }

    /**
     * @param alfrescoDataType
     */
    public void setAlfrescoDataType(AlfrescoDataType alfrescoDataType)
    {
        this.alfrescoDataType = alfrescoDataType;
        
    }

}
