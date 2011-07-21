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

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.AbstractAnalyzer;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.analysis.LongAnalyser;
import org.alfresco.repo.search.impl.lucene.analysis.MLAnalayser;
import org.alfresco.repo.search.impl.lucene.analysis.PathAnalyser;
import org.alfresco.repo.search.impl.lucene.analysis.VerbatimAnalyser;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Analyse properties according to the property definition. The default is to use the standard tokeniser. The tokeniser
 * should not have been called when indexing properties that require no tokenisation. (tokenise should be set to false
 * when adding the field to the document)
 * 
 * @author andyh
 */

public class SolrLuceneAnalyser extends AbstractAnalyzer
{

    private static Log s_logger = LogFactory.getLog(SolrLuceneAnalyser.class);

    // Dictinary service to look up analyser classes by data type and locale.
    private DictionaryService dictionaryService;

    // If all else fails a fall back analyser
    private Analyzer defaultAnalyser;

    // Cached analysers for non ML data types.
    private Map<String, Analyzer> analysers = new HashMap<String, Analyzer>();

    private MLAnalysisMode mlAlaysisMode;

    private AlfrescoSolrDataModel model;

    /**
     * Constructs with a default standard analyser
     * 
     * @param defaultAnalyzer
     *            Any fields not specifically defined to use a different analyzer will use the one provided here.
     */
    public SolrLuceneAnalyser(DictionaryService dictionaryService, MLAnalysisMode mlAlaysisMode, Analyzer defaultAnalyser, AlfrescoSolrDataModel model)
    {
        this.dictionaryService = dictionaryService;
        this.mlAlaysisMode = mlAlaysisMode;
        this.defaultAnalyser = defaultAnalyser;
        this.model = model;
    }

    /**
     * @return the defaultAnalyser
     */
    public Analyzer getDefaultAnalyser()
    {
        return defaultAnalyser;
    }

    public TokenStream tokenStream(String fieldName, Reader reader, AnalysisMode analysisMode)
    {
        Analyzer analyser = (Analyzer) analysers.get(fieldName);
        if (analyser == null)
        {
            analyser = findAnalyser(fieldName, analysisMode);
            analysers.put(fieldName, analyser);
        }
        return analyser.tokenStream(fieldName, reader);
    }

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return tokenStream(fieldName, reader, AnalysisMode.DEFAULT);
    }

    /**
     * Pick the analyser from the field name
     * 
     * @param fieldName
     * @return
     */
    private Analyzer findAnalyser(String fieldName, AnalysisMode analysisMode)
    {
        if (fieldName.equals(AbstractLuceneQueryParser.FIELD_ID))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_DBID))
        {
            return new LongAnalyser();
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_TXID))
        {
            return new LongAnalyser();
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_ACLTXID))
        {
            return new LongAnalyser();
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_TXCOMMITTIME))
        {
            return new LongAnalyser();
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_ACLTXCOMMITTIME))
        {
            return new LongAnalyser();
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_ACLID))
        {
            return new LongAnalyser();
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_TX))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_PARENT))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_LINKASPECT))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_PATH))
        {
            return new PathAnalyser();
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_ANCESTOR))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_ISCONTAINER))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_READER))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_OWNER))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_ISCATEGORY))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_QNAME))
        {
            return new PathAnalyser();
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_ISROOT))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_PRIMARYASSOCTYPEQNAME))
        {
            return new PathAnalyser();
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_ISNODE))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_ASSOCTYPEQNAME))
        {
            return new PathAnalyser();
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_PRIMARYPARENT))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_TYPE))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_ASPECT))
        {
            return new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(AbstractLuceneQueryParser.FIELD_FTSSTATUS))
        {
            return new VerbatimAnalyser(false);
        }
        // type dependent analysis
        else if (fieldName.startsWith("@"))

        {
            if (fieldName.endsWith(".sort"))
            {
                return new VerbatimAnalyser(false);
            }
            
            for (String contentEnding : AlfrescoSolrDataModel.additionalContentFields.keySet())
            {
                try
                {
                    QName testPropertyQName = QName.createQName(fieldName.substring(1, (fieldName.length() - contentEnding.length())));
                    PropertyDefinition propertyDef = dictionaryService.getProperty(testPropertyQName);
                    if (propertyDef != null)
                    {
                        if (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT))
                        {
                            if (fieldName.endsWith(".size"))
                            {
                                return new LongAnalyser();
                            }
                            else if (fieldName.endsWith(".locale"))
                            {
                                return new VerbatimAnalyser(true);
                            }
                            else if (fieldName.endsWith(".mimetype"))
                            {
                                return new VerbatimAnalyser();
                            }
                            else if (fieldName.endsWith(".encoding"))
                            {
                                return new VerbatimAnalyser();
                            }
                            else if (fieldName.endsWith(".contentDocId"))
                            {
                                return new LongAnalyser();
                            }
                            else if (fieldName.endsWith(".transformationException"))
                            {
                                return defaultAnalyser;
                            }
                            else if (fieldName.endsWith(".transformationTime"))
                            {
                                return new LongAnalyser();
                            }
                            else if (fieldName.endsWith(".transformationStatus"))
                            {
                                return new VerbatimAnalyser();
                            }
                            else if (fieldName.endsWith(".__"))
                            {
                                return new MLAnalayser(dictionaryService, MLAnalysisMode.ALL_ONLY, defaultAnalyser);
                            }
                        }
                    }
                }
                catch (InvalidQNameException iqne)
                {

                }
            }

            for (String textEnding : AlfrescoSolrDataModel.additionalTextFields.keySet())
            {
                try
                {
                    QName testPropertyQName = QName.createQName(fieldName.substring(1, (fieldName.length() - textEnding.length())));
                    PropertyDefinition propertyDef = dictionaryService.getProperty(testPropertyQName);
                    if (propertyDef != null)
                    {
                        if (propertyDef.getDataType().getName().equals(DataTypeDefinition.TEXT))
                        {
                            if (fieldName.endsWith(".__"))
                            {
                                return new MLAnalayser(dictionaryService, MLAnalysisMode.ALL_ONLY, defaultAnalyser);
                            }
                            else if (fieldName.endsWith(".__.u"))
                            {
                                return new MLAnalayser(dictionaryService, MLAnalysisMode.ALL_ONLY, new VerbatimAnalyser(false));
                            }
                            else if (fieldName.endsWith(".u"))
                            {
                                return new MLAnalayser(dictionaryService, MLAnalysisMode.EXACT_LANGUAGE, new VerbatimAnalyser(false));
                            }
                            else if (fieldName.endsWith(".sort"))
                            {
                                return new VerbatimAnalyser(false);
                            }
                        }
                    }
                }
                catch (InvalidQNameException iqne)
                {

                }
            }

            for (String mlTexttEnding : AlfrescoSolrDataModel.additionalMlTextFields.keySet())
            {
                try
                {
                    QName testPropertyQName = QName.createQName(fieldName.substring(1, (fieldName.length() - mlTexttEnding.length())));
                    PropertyDefinition propertyDef = dictionaryService.getProperty(testPropertyQName);
                    if (propertyDef != null)
                    {
                        if (propertyDef.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
                        {
                            if (fieldName.endsWith(".__"))
                            {
                                return new MLAnalayser(dictionaryService, MLAnalysisMode.ALL_ONLY, defaultAnalyser);
                            }
                            else if (fieldName.endsWith(".__.u"))
                            {
                                return new MLAnalayser(dictionaryService, MLAnalysisMode.ALL_ONLY, new VerbatimAnalyser(false));
                            }
                            else if (fieldName.endsWith(".u"))
                            {
                                return new MLAnalayser(dictionaryService, MLAnalysisMode.EXACT_LANGUAGE, new VerbatimAnalyser(false));
                            }
                            else if (fieldName.endsWith(".sort"))
                            {
                                return new VerbatimAnalyser(false);
                            }
                        }
                    }
                }
                catch (InvalidQNameException iqne)
                {

                }
            }

            QName propertyQName = QName.createQName(fieldName.substring(1));
            // Temporary fix for person and user uids

            if (propertyQName.equals(ContentModel.PROP_USER_USERNAME) || propertyQName.equals(ContentModel.PROP_USERNAME) || propertyQName.equals(ContentModel.PROP_AUTHORITY_NAME))
            {
                return new VerbatimAnalyser(true);
            }
            else
            {
                PropertyDefinition propertyDef = dictionaryService.getProperty(propertyQName);
                IndexTokenisationMode tokenise = IndexTokenisationMode.TRUE;
                if (propertyDef != null)
                {
                    DataTypeDefinition dataType = propertyDef.getDataType();
                    tokenise = propertyDef.getIndexTokenisationMode();
                    if (tokenise == null)
                    {
                        tokenise = IndexTokenisationMode.TRUE;
                    }
                    switch (tokenise)
                    {
                    case TRUE:
                        if (dataType.getName().equals(DataTypeDefinition.CONTENT))
                        {
                            return new MLAnalayser(dictionaryService, MLAnalysisMode.EXACT_LANGUAGE);
                        }
                        else if (dataType.getName().equals(DataTypeDefinition.TEXT))
                        {
                            return new MLAnalayser(dictionaryService, MLAnalysisMode.EXACT_LANGUAGE);
                        }
                        else if (dataType.getName().equals(DataTypeDefinition.MLTEXT))
                        {
                            return new MLAnalayser(dictionaryService,  MLAnalysisMode.EXACT_LANGUAGE);
                        }
                        else
                        {
                            return loadAnalyzer(propertyDef);
                        }
                    case BOTH:
                        switch (analysisMode)
                        {
                        case DEFAULT:
                        case TOKENISE:
                            if (dataType.getName().equals(DataTypeDefinition.CONTENT))
                            {
                                return new MLAnalayser(dictionaryService, MLAnalysisMode.EXACT_LANGUAGE);
                            }
                            else if (dataType.getName().equals(DataTypeDefinition.TEXT))
                            {
                                return new MLAnalayser(dictionaryService, MLAnalysisMode.EXACT_LANGUAGE);
                            }
                            else if (dataType.getName().equals(DataTypeDefinition.MLTEXT))
                            {
                                return new MLAnalayser(dictionaryService, MLAnalysisMode.EXACT_LANGUAGE);
                            }
                            else
                            {
                                return loadAnalyzer(propertyDef);
                            }
                        case IDENTIFIER:
                            if (dataType.getName().equals(DataTypeDefinition.MLTEXT))
                            {
                                return new MLAnalayser(dictionaryService, MLAnalysisMode.ALL_ONLY, new VerbatimAnalyser(false));
                            }
                            else
                            {
                                return new VerbatimAnalyser(false);
                            }
                        default:
                            throw new UnsupportedOperationException("TYPE must not be tokenised");
                        }
                    case FALSE:
                        // TODO: MLText verbatim analyser
                        return new VerbatimAnalyser(false);
                    default:
                        throw new UnsupportedOperationException("TYPE must not be tokenised");
                    }
                }
                else
                {
                    switch (analysisMode)
                    {
                    case IDENTIFIER:
                        return new VerbatimAnalyser(false);
                    case DEFAULT:
                    case TOKENISE:
                        DataTypeDefinition dataType = dictionaryService.getDataType(DataTypeDefinition.TEXT);
                        return loadAnalyzer(dataType);
                    default:
                        throw new UnsupportedOperationException();
                    }

                }
            }
        }
        return defaultAnalyser;
    }

    /**
     * Find an instantiate an analyser. The shuld all be thread sade as Analyser.tokenStream should be re-entrant.
     * 
     * @param dataType
     * @return
     */
    private Analyzer loadAnalyzer(PropertyDefinition property)
    {
        String analyserClassName = property.resolveAnalyserClassName(I18NUtil.getLocale()).trim();
        try
        {
            Class<?> clazz = Class.forName(analyserClassName);
            Analyzer analyser = (Analyzer) clazz.newInstance();
            if (s_logger.isDebugEnabled())
            {
                s_logger.debug("Loaded " + analyserClassName + " for type " + property.getName());
            }
            return analyser;
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Unable to load analyser for property of type " + property.getName() + " using " + analyserClassName);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Unable to load analyser for property of type " + property.getName() + " using " + analyserClassName);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Unable to load analyser for property of type " + property.getName() + " using " + analyserClassName);
        }
    }
    
    private Analyzer loadAnalyzer( DataTypeDefinition dataTypeDef )
    {
        String analyserClassName = dataTypeDef.resolveAnalyserClassName(I18NUtil.getLocale()).trim();
        try
        {
            Class<?> clazz = Class.forName(analyserClassName);
            Analyzer analyser = (Analyzer) clazz.newInstance();
            if (s_logger.isDebugEnabled())
            {
                s_logger.debug("Loaded " + analyserClassName + " for type " + dataTypeDef.getName());
            }
            return analyser;
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Unable to load analyser for property of type " + dataTypeDef.getName() + " using " + analyserClassName);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Unable to load analyser for property of type " + dataTypeDef.getName() + " using " + analyserClassName);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Unable to load analyser for property of type " + dataTypeDef.getName() + " using " + analyserClassName);
        }
    }


    /**
     * For multilingual fields we separate the tokens for each instance to break phrase queries spanning different
     * languages etc.
     */
    @Override
    public int getPositionIncrementGap(String fieldName)
    {
        if (fieldName.startsWith("@") && !fieldName.endsWith(".mimetype"))
        {
            QName propertyQName = QName.createQName(fieldName.substring(1));
            PropertyDefinition propertyDef = dictionaryService.getProperty(propertyQName);
            if (propertyDef != null)
            {
                if (propertyDef.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
                {
                    return 1000;
                }
            }
        }
        return super.getPositionIncrementGap(fieldName);
    }

}
