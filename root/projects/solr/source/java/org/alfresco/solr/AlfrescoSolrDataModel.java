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
import org.alfresco.repo.dictionary.NamespaceDAOImpl;
import org.alfresco.repo.dictionary.DictionaryDAOImpl.DictionaryRegistry;
import org.alfresco.repo.dictionary.NamespaceDAOImpl.NamespaceRegistry;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneAnalyser;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
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
import org.alfresco.util.ISO9075;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.Query;
import org.apache.solr.schema.SchemaField;
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

    private static HashMap<String, NonDictionaryField> nonDictionaryFields = new HashMap<String, NonDictionaryField>();

    private TenantService tenantService;

    private NamespaceDAOImpl namespaceDAO;

    private DictionaryDAOImpl dictionaryDAO;

    private DictionaryComponent dictionaryComponent;

    static
    {

        addNonDictionaryField("ID", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField("TX", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField("PARENT", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField("LINKASPECT", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField("PATH", Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField("ANCESTOR", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField("ISCONTAINER", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField("ISCATEGORY", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField("QNAME", Store.YES, Index.ANALYZED, TermVector.NO, true);
        addNonDictionaryField("ISROOT", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField("PRIMARYASSOCTYPEQNAME", Store.YES, Index.ANALYZED, TermVector.NO, false);
        addNonDictionaryField("ISNODE", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField("ASSOCTYPEQNAME", Store.YES, Index.ANALYZED, TermVector.NO, true);
        addNonDictionaryField("PRIMARYPARENT", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField("TYPE", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField("ASPECT", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField("FTSSTATUS", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField("DBID", Store.YES, Index.ANALYZED_NO_NORMS, TermVector.NO, false);
        
        addNonDictionaryField("ACLID", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        addNonDictionaryField("READER", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        addNonDictionaryField("OWNER", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);

    }

    private static void addNonDictionaryField(String name, Store store, Index index, TermVector termVector, boolean multiValued)
    {
        nonDictionaryFields.put(name, new NonDictionaryField(name, store, index, termVector, multiValued));
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
        PropertyDefinition propertyDefinition = getPropertyDefinition(field);
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

        return Index.ANALYZED;
    }

    private PropertyDefinition getPropertyDefinition(SchemaField field)
    {
        QName rawPropertyName = QName.createQName(field.getName().substring(1));
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
        PropertyDefinition propertyDefinition = getPropertyDefinition(field);
        if (propertyDefinition != null)
        {
            return propertyDefinition.isStoredInIndex() ? Store.YES : Store.NO;
        }

        NonDictionaryField nonDDField = nonDictionaryFields.get(field.getName());
        if (nonDDField != null)
        {
            return nonDDField.store;
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
        PropertyDefinition propertyDefinition = getPropertyDefinition(field);
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
        LuceneAnalyser defaultAnalyser = new LuceneAnalyser(getDictionaryService(), getMLAnalysisMode());
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

                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
                {

                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
                {
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.DATETIME))
                {
                    DataTypeDefinition dataType = propertyDefinition.getDataType();
                    String analyserClassName = dataType.getAnalyserClassName();
                    if (analyserClassName.equals(DateTimeAnalyser.class.getCanonicalName()))
                    {
                        writeField(xmlWriter, name + ".sort", "alfrescoDataType", store, Index.NOT_ANALYZED, TermVector.NO, multiValued);
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
                break;
            case TRUE:
                writeField(xmlWriter, name, "alfrescoDataType", store, Index.ANALYZED, TermVector.NO, multiValued);
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
        LuceneAnalyser analyzer = new LuceneAnalyser(getDictionaryService(), getMLAnalysisMode());
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
    
    public Query getFTSQuery(String defaultField, String query, Operator defaultOperator,  IndexReader indexReader) throws ParseException
    {
        QueryModelFactory factory = new LuceneQueryModelFactory();
        AlfrescoFunctionEvaluationContext functionContext = new AlfrescoFunctionEvaluationContext(namespaceDAO,dictionaryComponent,
                NamespaceService.CONTENT_MODEL_1_0_URI);

        FTSParser.Mode mode;

        if(defaultOperator == Operator.AND)
        {
            mode = FTSParser.Mode.DEFAULT_CONJUNCTION;
        }
        else
        {
            mode = FTSParser.Mode.DEFAULT_DISJUNCTION;
        }

        Constraint constraint = FTSQueryParser.buildFTS(query, factory, functionContext, null, null, mode,defaultOperator == Operator.OR ? Connective.OR : Connective.AND,
                new HashMap<String, String>(), defaultField);
        org.alfresco.repo.search.impl.querymodel.Query queryModelQuery = factory.createQuery(null, null, constraint, new ArrayList<Ordering>());

        LuceneQueryBuilder builder = (LuceneQueryBuilder) queryModelQuery;
        
        /* 
         * Used for:
         * locale
         * TEXT
         * ALL
         * default ML mode
         * namespace
         */
        
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMlAnalaysisMode(getMLAnalysisMode());
        searchParameters.setNamespace( NamespaceService.CONTENT_MODEL_1_0_URI);
        
        LuceneQueryBuilderContextSolrImpl luceneContext = new LuceneQueryBuilderContextSolrImpl(dictionaryComponent, namespaceDAO, tenantService, searchParameters, getMLAnalysisMode(),
                indexReader);
        
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

}
