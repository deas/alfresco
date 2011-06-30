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
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.script.CompiledScript;

import org.alfresco.cmis.CMISScope;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.opencmis.dictionary.CMISDictionaryService;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.opencmis.mapping.CMISMapping;
import org.alfresco.opencmis.mapping.RuntimePropertyLuceneBuilderMapping;
import org.alfresco.opencmis.search.CMISQueryOptions;
import org.alfresco.opencmis.search.CMISQueryParser;
import org.alfresco.opencmis.search.CmisFunctionEvaluationContext;
import org.alfresco.opencmis.search.CMISQueryOptions.CMISQueryMode;
import org.alfresco.repo.cache.MemoryCache;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.DictionaryDAOImpl;
import org.alfresco.repo.dictionary.DictionaryNamespaceComponent;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.repo.dictionary.NamespaceDAOImpl;
import org.alfresco.repo.dictionary.DictionaryDAOImpl.DictionaryRegistry;
import org.alfresco.repo.dictionary.NamespaceDAOImpl.NamespaceRegistry;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.analysis.DateTimeAnalyser;
import org.alfresco.repo.search.impl.parsers.AlfrescoFunctionEvaluationContext;
import org.alfresco.repo.search.impl.parsers.FTSParser;
import org.alfresco.repo.search.impl.parsers.FTSQueryParser;
import org.alfresco.repo.search.impl.querymodel.Constraint;
import org.alfresco.repo.search.impl.querymodel.Ordering;
import org.alfresco.repo.search.impl.querymodel.QueryModelFactory;
import org.alfresco.repo.search.impl.querymodel.QueryOptions.Connective;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilder;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContextImpl;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryModelFactory;
import org.alfresco.repo.tenant.SingleTServiceImpl;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.client.AlfrescoModel;
import org.alfresco.solr.query.LuceneQueryBuilderContextSolrImpl;
import org.alfresco.solr.query.SolrQueryParser;
import org.alfresco.util.ISO9075;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.CapabilityJoin;
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
import org.apache.lucene.search.SortField;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.Sorting;
import org.dom4j.io.XMLWriter;
import org.springframework.extensions.surf.util.I18NUtil;
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

    private CMISStrictDictionaryService cmisDictionaryService;
    
    private boolean testing = true;

    private AlfrescoDataType alfrescoDataType;

    static
    {

        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_ID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_TX, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_PARENT, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_LINKASPECT, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_PATH, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_ANCESTOR, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_ISCONTAINER, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_ISCATEGORY, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_QNAME, Store.YES, Index.ANALYZED, TermVector.NO, true);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_ISROOT, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_PRIMARYASSOCTYPEQNAME, Store.YES, Index.ANALYZED, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_ISNODE, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_ASSOCTYPEQNAME, Store.YES, Index.ANALYZED, TermVector.NO, true);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_PRIMARYPARENT, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_TYPE, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_ASPECT, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_FTSSTATUS, Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_DBID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_TXID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_ACLTXID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_TXCOMMITTIME, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_ACLTXCOMMITTIME, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);

        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_ACLID, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_READER, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField(AbstractLuceneQueryParser.FIELD_OWNER, Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);

        addAdditionalContentField(".size", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".locale", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".mimetype", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".encoding", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".contentDocId", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".transformationException", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".transformationTime", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        addAdditionalContentField(".transformationStatus", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
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
        
        // cmis dictionary
        CMISMapping cmisMapping = new CMISMapping();
        DictionaryNamespaceComponent namespaceService = new DictionaryNamespaceComponent();
        namespaceService.setNamespaceDAO(namespaceDAO);
        cmisMapping.setNamespaceService(namespaceService);
        cmisMapping.setDictionaryService(dictionaryComponent);
        cmisMapping.afterPropertiesSet();
        
        cmisDictionaryService = new CMISStrictDictionaryService();
        cmisDictionaryService.setCmisMapping(cmisMapping);
        cmisDictionaryService.setDictionaryService(dictionaryComponent);
        cmisDictionaryService.setDictionaryDAO(dictionaryDAO);
        cmisDictionaryService.setTenantService(tenantService);
        
        RuntimePropertyLuceneBuilderMapping luceneBuilderMapping = new RuntimePropertyLuceneBuilderMapping();
        luceneBuilderMapping.setDictionaryService(dictionaryComponent);
        luceneBuilderMapping.setCmisDictionaryService(cmisDictionaryService);
        cmisDictionaryService.setPropertyLuceneBuilderMapping(luceneBuilderMapping);

        luceneBuilderMapping.afterPropertiesSet();
    }

    public DictionaryService getDictionaryService()
    {
        return dictionaryComponent;
    }

    public CMISDictionaryService getCMISDictionaryService()
    {
        return cmisDictionaryService;
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
        if (testing)
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
        SolrLuceneAnalyser defaultAnalyser = new SolrLuceneAnalyser(getDictionaryService(), getMLAnalysisMode(), alfrescoDataType.getAnalyzer().getDefaultAnalyser(), this);
        SolrQueryParser parser = new SolrQueryParser("TEXT", defaultAnalyser);
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
        dictionaryDAO.putModelIgnoringConstraints(model);
    }

    public M2Model getM2Model(QName modelQName)
    {
        return dictionaryDAO.getCompiledModel(modelQName).getM2Model();
    }
    
    public void afterInitModels()
    {
        cmisDictionaryService.afterDictionaryInit();
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
                    writeField(xmlWriter, name + ".size", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".locale", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".mimetype", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".encoding", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".contentDataId", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".transformationException", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".transformationTime", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".transformationStatus", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, false);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
                {
                    writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
                    writeField(xmlWriter, name + ".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name + ".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
                {
                    writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
                    writeField(xmlWriter, name + ".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name + ".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.DATETIME))
                {
                    DataTypeDefinition dataType = propertyDefinition.getDataType();
                    String analyserClassName = dataType.getAnalyserClassName();
                    if (analyserClassName.equals(DateTimeAnalyser.class.getCanonicalName()))
                    {
                        writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, multiValued);
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
                    writeField(xmlWriter, name + ".size", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".locale", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".mimetype", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".encoding", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".contentDataId", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".transformationException", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".transformationTime", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".transformationStatus", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
                {
                    writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
                    writeField(xmlWriter, name + ".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name + ".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
                {
                    writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
                    writeField(xmlWriter, name + ".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name + ".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                }
                break;
            case TRUE:
                writeField(xmlWriter, name, "alfrescoDataType", store, Index.ANALYZED, TermVector.NO, multiValued);
                if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.CONTENT))
                {
                    writeField(xmlWriter, name + ".size", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".locale", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".mimetype", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".encoding", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".contentDataId", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".transformationException", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".transformationTime", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".transformationStatus", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                    writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
                {
                    writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
                    writeField(xmlWriter, name + ".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name + ".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
                {
                    writeField(xmlWriter, name + ".__", "alfrescoDataType", Store.NO, Index.ANALYZED, TermVector.NO, true);
                    writeField(xmlWriter, name + ".u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name + ".__.u", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                    writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.DATETIME))
                {
                    DataTypeDefinition dataType = propertyDefinition.getDataType();
                    String analyserClassName = dataType.getAnalyserClassName();
                    if (analyserClassName.equals(DateTimeAnalyser.class.getCanonicalName()))
                    {
                        writeField(xmlWriter, name + ".sort", "alfrescoDataType", Store.NO, Index.ANALYZED_NO_NORMS, TermVector.NO, multiValued);
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

    public AbstractLuceneQueryParser getLuceneQueryParser(String defaultField, String query, Operator defaultOperator, IndexReader indexReader)
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

    public LuceneQueryBuilderContext getLuceneQueryBuilderContext(SearchParameters searchParameters, IndexReader indexReader)
    {
        LuceneQueryBuilderContextSolrImpl luceneContext = new LuceneQueryBuilderContextSolrImpl(dictionaryComponent, namespaceDAO, tenantService, searchParameters,
                getMLAnalysisMode(), indexReader, alfrescoDataType.getAnalyzer(), this);
        return luceneContext;
    }
    
    public Query getFTSQuery(SearchParameters searchParameters, IndexReader indexReader) throws ParseException
    {
        QueryModelFactory factory = new LuceneQueryModelFactory();
        AlfrescoFunctionEvaluationContext functionContext = new AlfrescoFunctionEvaluationContext(namespaceDAO, dictionaryComponent, NamespaceService.CONTENT_MODEL_1_0_URI);

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
                new HashMap<String, String>(), searchParameters.getDefaultFieldName());
        org.alfresco.repo.search.impl.querymodel.Query queryModelQuery = factory.createQuery(null, null, constraint, new ArrayList<Ordering>());

        LuceneQueryBuilder builder = (LuceneQueryBuilder) queryModelQuery;

        LuceneQueryBuilderContext luceneContext = getLuceneQueryBuilderContext(searchParameters, indexReader);

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
        
        ContextAwareQuery contextAwareQuery = new ContextAwareQuery(luceneQuery, searchParameters);
        return contextAwareQuery;
    }

    public  org.alfresco.repo.search.impl.querymodel.Query parseCMISQueryToAlfrescoAbstractQuery(CMISQueryMode mode, SearchParameters searchParameters, IndexReader indexReader) throws ParseException
    {
        // convert search parameters to cmis query options
        // TODO: how to handle store ref
        CMISQueryOptions options = new CMISQueryOptions(searchParameters.getQuery(), StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        options.setDefaultFieldName(searchParameters.getDefaultFieldName());
        // TODO: options.setDefaultFTSConnective()
        // TODO: options.setDefaultFTSFieldConnective()
        options.setIncludeInTransactionData(!searchParameters.excludeDataInTheCurrentTransaction());
        options.setLocales(searchParameters.getLocales());
        options.setMlAnalaysisMode(searchParameters.getMlAnalaysisMode());
        options.setQueryParameterDefinitions(searchParameters.getQueryParameterDefinitions());
        
        // parse cmis syntax
        CapabilityJoin joinSupport = (mode == CMISQueryMode.CMS_STRICT) ? CapabilityJoin.NONE : CapabilityJoin.INNERONLY;
        CmisFunctionEvaluationContext functionContext = getCMISFunctionEvaluationContext(mode);
        CMISQueryParser parser = new CMISQueryParser(options, cmisDictionaryService, joinSupport);
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
    
    public CmisFunctionEvaluationContext getCMISFunctionEvaluationContext(CMISQueryMode mode)
    {
        BaseTypeId[] validScopes = (mode == CMISQueryMode.CMS_STRICT) ? CmisFunctionEvaluationContext.STRICT_SCOPES : CmisFunctionEvaluationContext.ALFRESCO_SCOPES;
        CmisFunctionEvaluationContext functionContext = new CmisFunctionEvaluationContext();
        functionContext.setCmisDictionaryService(cmisDictionaryService);
        functionContext.setValidScopes(validScopes);
        return functionContext;
    }
    
    public Query getCMISQuery(CMISQueryMode mode, SearchParameters searchParameters, IndexReader indexReader, org.alfresco.repo.search.impl.querymodel.Query queryModelQuery) throws ParseException
    {
        BaseTypeId[] validScopes = (mode == CMISQueryMode.CMS_STRICT) ? CmisFunctionEvaluationContext.STRICT_SCOPES : CmisFunctionEvaluationContext.ALFRESCO_SCOPES;
        CmisFunctionEvaluationContext functionContext = getCMISFunctionEvaluationContext(mode);
        
        Set<String> selectorGroup = queryModelQuery.getSource().getSelectorGroups(functionContext).get(0);
        
        LuceneQueryBuilderContext luceneContext = getLuceneQueryBuilderContext(searchParameters, indexReader);
        LuceneQueryBuilder builder = (LuceneQueryBuilder) queryModelQuery;
        org.apache.lucene.search.Query luceneQuery = builder.buildQuery(selectorGroup, luceneContext, functionContext);

        ContextAwareQuery contextAwareQuery = new ContextAwareQuery(luceneQuery, searchParameters);
        return contextAwareQuery;
    }
    
    
    /**
     * @param field
     * @param reverse
     */
    public SortField getSortField(SchemaField field, boolean reverse)
    {
        // TODO: Set up Thread local for locale ?? May need some SOLR guddeling - extra query component?

        // TODO: TEXT and MLTEXT comparator
        // best locale match and subsequent localised ordering
        // cache ordering
        PropertyDefinition propertyDefinition = getPropertyDefinition(field.getName());
        if (propertyDefinition != null)
        {
            if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.DATETIME))
            {
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    return Sorting.getStringSortField(field.getName() + ".sort", reverse, field.sortMissingLast(), field.sortMissingFirst());
                }
                else
                {
                    throw new UnsupportedOperationException("Ordering not supported for " + field.getName());
                }
            }
            else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
            {
                // ignore locale store in the text field

                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    return new SortField(field.getName() + ".sort", new TextSortFieldComparatorSource(), reverse);
                }
                else
                {
                    throw new UnsupportedOperationException("Ordering not supported for " + field.getName());
                }
            }
            else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
            {
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    return new SortField(field.getName() + ".sort", new MLTextSortFieldComparatorSource(), reverse);
                }
                else
                {
                    throw new UnsupportedOperationException("Ordering not supported for " + field.getName());
                }
            }
            else
            {
                return Sorting.getStringSortField(field.getName(), reverse, field.sortMissingLast(), field.sortMissingFirst());
            }
        }

        NonDictionaryField nonDDField = nonDictionaryFields.get(field.getName());
        if (nonDDField != null)
        {
            return Sorting.getStringSortField(field.getName(), reverse, field.sortMissingLast(), field.sortMissingFirst());
        }

        return Sorting.getStringSortField(field.getName(), reverse, field.sortMissingLast(), field.sortMissingFirst());

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
            if(withLocale == null)
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
            if(withLocale == null)
            {
                return withLocale;
            }
            else if (withLocale.startsWith("\u0000"))
            {
                MLText mlText = new MLText();
                String[] parts = withLocale.split("\u0000");
                for(int i = 0; (i+2) <= parts.length; i+=3)
                {
                    Locale locale = null;
                    String[] localeParts = parts[i+1].split("_");
                    if(localeParts.length == 1)
                    {
                        locale = new Locale(localeParts[0]);
                    }
                    else if(localeParts.length == 2)
                    {
                        locale = new Locale(localeParts[0], localeParts[1]);
                    }
                    else if(localeParts.length == 3)
                    {
                        locale = new Locale(localeParts[0], localeParts[1], localeParts[2]);
                    }
                    if(locale != null)
                    { 
                        if(i+2 ==  parts.length)
                        {
                            mlText.addValue(locale, "");
                        }
                        else
                        {
                            mlText.addValue(locale, parts[i+2]);
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
        for(QName modelName : dictionaryDAO.getModels())
        {
            M2Model m2Model = dictionaryDAO.getCompiledModel(modelName).getM2Model();
            answer.add(new AlfrescoModel(m2Model, dictionaryComponent.getModel(modelName).getChecksum(ModelDefinition.XMLBindingType.DEFAULT)));
        }
        return answer;
        
    }

}
