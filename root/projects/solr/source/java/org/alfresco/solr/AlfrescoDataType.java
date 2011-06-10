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
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.repo.search.impl.lucene.LuceneAnalyser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.solr.core.Config;
import org.apache.solr.request.TextResponseWriter;
import org.apache.solr.request.XMLWriter;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.QParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Generic data type for alfresco - the field instance specifies as much as it can TODO: This is a generic multi-valued
 * type - consider a single valued type for performance. TODO: wire up config for Alfresco data models etc and implement
 * init.
 * 
 * @author Andy
 */
public class AlfrescoDataType extends FieldType
{
    private String id;

    @Override
    public boolean isTokenized()
    {
        // Our tokeniser takes care of untokenised.
        return true;
    }

    @Override
    public boolean isMultiValued()
    {
        // There is no restriction at index time.
        // Any restriction would be enforced in Alfresco
        return true;
    }

    @Override
    public boolean multiValuedFieldCache()
    {
        // Consider another type for single valued properties for performance
        return true;
    }

    // Helpers for field creation

    /*
     * (non-Javadoc)
     * @see org.apache.solr.schema.FieldType#getSortField(org.apache.solr.schema.SchemaField, boolean)
     */
    @Override
    public SortField getSortField(SchemaField field, boolean reverse)
    {
        return AlfrescoSolrDataModel.getInstance(id).getSortField(field, reverse);
    }

    @Override
    protected void init(IndexSchema schema, Map<String, String> args)
    {
        HashMap<String, M2Model> modelMap = new HashMap<String, M2Model>();
        id = schema.getResourceLoader().getInstanceDir();
        InputStream is = schema.getResourceLoader().openResource("alfrescoConfig.xml");
        Config alfrescoConf;
        try
        {
            alfrescoConf = new Config(schema.getResourceLoader(), "schema", is, "/alfresco/");
            Document document = alfrescoConf.getDocument();
            final XPath xpath = alfrescoConf.getXPath();

            String expression = "/alfresco/model";
            NodeList nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++)
            {
                Node node = nodes.item(i);
                String modelLocaltion = node.getTextContent();
                InputStream modelStream = schema.getResourceLoader().openResource(modelLocaltion);
                M2Model model = M2Model.createModel(modelStream);
                for (M2Namespace namespace : model.getNamespaces())
                {
                    modelMap.put(namespace.getUri(), model);
                }
            }

            // Load the models ensuring that they are loaded in the correct order
            HashSet<String> loadedModels = new HashSet<String>();
            for (M2Model model : modelMap.values())
            {
                loadModel(modelMap, loadedModels, model);
            }

        }
        catch (Exception e)
        {
            throw new AlfrescoRuntimeException("Failed to read Alfresco schema", e);
        }

        AlfrescoSolrDataModel.getInstance(id).afterInitModels();
        AlfrescoSolrDataModel.getInstance(id).setAlfrescoDataType(this);
        super.init(schema, args);
    }

    public void write(XMLWriter xmlWriter, String name, Fieldable f) throws IOException
    {
        xmlWriter.writeStr(name, f.stringValue());
    }

    public void write(TextResponseWriter writer, String name, Fieldable f) throws IOException
    {
        writer.writeStr(name, f.stringValue(), true);
    }

    @Override
    public SolrLuceneAnalyser getAnalyzer()
    {
        return new SolrLuceneAnalyser(AlfrescoSolrDataModel.getInstance(id).getDictionaryService(), AlfrescoSolrDataModel.getInstance(id).getMLAnalysisMode(), super.getAnalyzer(), AlfrescoSolrDataModel.getInstance(id));
    }

    public Field createField(SchemaField field, String externalVal, float boost)
    {
        String val;
        try
        {
            val = toInternal(externalVal);
        }
        catch (RuntimeException e)
        {
            // throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, "Error while creating field '" + field +
            // "' from value '" + externalVal + "'", e, false);
            throw e;
        }
        if (val == null)
            return null;
        if (!field.indexed() && !field.stored())
        {

            return null;
        }

        Field f = new Field(field.getName(), val, getFieldStore(field, val), getFieldIndex(field, val), getFieldTermVec(field, val));
        f.setOmitNorms(getOmitNorms(field, val));
        f.setOmitTermFreqAndPositions(field.omitTf());
        // Ignore index time boost
        // f.setBoost(boost);
        return f;
    }

    @Override
    protected Index getFieldIndex(SchemaField field, String internalVal)
    {
        return AlfrescoSolrDataModel.getInstance(id).getFieldIndex(field);
    }

    @Override
    protected Store getFieldStore(SchemaField field, String internalVal)
    {
        return AlfrescoSolrDataModel.getInstance(id).getFieldStore(field);
    }

    @Override
    protected TermVector getFieldTermVec(SchemaField field, String internalVal)
    {
        return AlfrescoSolrDataModel.getInstance(id).getFieldTermVec(field);
    }

    protected boolean getOmitNorms(SchemaField field, String internalVal)
    {
        return AlfrescoSolrDataModel.getInstance(id).getOmitNorms(field);
    }

    @Override
    public Analyzer getQueryAnalyzer()
    {
        return new SolrLuceneAnalyser(AlfrescoSolrDataModel.getInstance(id).getDictionaryService(), AlfrescoSolrDataModel.getInstance(id).getMLAnalysisMode(), super.getAnalyzer(), AlfrescoSolrDataModel.getInstance(id));
    }

    @Override
    public Query getRangeQuery(QParser parser, SchemaField field, String part1, String part2, boolean minInclusive, boolean maxInclusive)
    {
        return AlfrescoSolrDataModel.getInstance(id).getRangeQuery(field, part1, part2, minInclusive, maxInclusive);
    }

    /**
     * Loads a model (and its dependents) if it does not exist in the list of loaded models.
     * 
     * @param modelMap
     *            a map of the models to be loaded
     * @param loadedModels
     *            the list of models already loaded
     * @param model
     *            the model to try and load
     */
    private void loadModel(Map<String, M2Model> modelMap, HashSet<String> loadedModels, M2Model model)
    {
        String modelName = model.getName();
        if (loadedModels.contains(modelName) == false)
        {
            for (M2Namespace importNamespace : model.getImports())
            {
                M2Model importedModel = modelMap.get(importNamespace.getUri());
                if (importedModel != null)
                {

                    // Ensure that the imported model is loaded first
                    loadModel(modelMap, loadedModels, importedModel);
                }
            }

            AlfrescoSolrDataModel.getInstance(id).putModel(model);
            loadedModels.add(modelName);
        }
    }
}
