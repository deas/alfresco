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
package org.alfresco.repo.search.impl.lucene;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.alfresco.repo.search.impl.lucene.analysis.AlfrescoStandardAnalyser;
import org.alfresco.repo.search.impl.lucene.analysis.LongAnalyser;
import org.alfresco.repo.search.impl.lucene.analysis.MLAnalayser;
import org.alfresco.repo.search.impl.lucene.analysis.PathAnalyser;
import org.alfresco.repo.search.impl.lucene.analysis.VerbatimAnalyser;
import org.alfresco.repo.search.impl.lucene.analysis.VerbatimMLAnalayser;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 * Analyse properties according to the property definition. The default is to use the standard tokeniser. The tokeniser
 * should not have been called when indexing properties that require no tokenisation. (tokenise should be set to false
 * when adding the field to the document)
 * 
 * @author andyh
 */

public class LuceneAnalyser extends AbstractAnalyzer
{
    private static Log s_logger = LogFactory.getLog(LuceneAnalyser.class);

    // Dictinary service to look up analyser classes by data type and locale.
    private DictionaryService dictionaryService;

    // If all else fails a fall back analyser
    private Analyzer defaultAnalyser;

    // Cached analysers for non ML data types.
    private Map<String, Analyzer> analysers = new HashMap<String, Analyzer>();

    private MLAnalysisMode mlAlaysisMode;

    /**
     * Constructs with a default standard analyser
     * 
     * @param defaultAnalyzer
     *            Any fields not specifically defined to use a different analyzer will use the one provided here.
     */
    public LuceneAnalyser(DictionaryService dictionaryService, MLAnalysisMode mlAlaysisMode)
    {
        this.defaultAnalyser = new AlfrescoStandardAnalyser();
        this.dictionaryService = dictionaryService;
        this.mlAlaysisMode = mlAlaysisMode;
    }

    public TokenStream tokenStream(String fieldName, Reader reader, AnalysisMode analysisMode)
    {
        Analyzer analyser = (Analyzer) analysers.get(fieldName);
        if (analyser == null)
        {
            analyser = findAnalyser(fieldName, analysisMode);
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
    @SuppressWarnings("deprecation")
    private Analyzer findAnalyser(String fieldName, AnalysisMode analysisMode)
    {
        Analyzer analyser;
        if (fieldName.equals(LuceneQueryParser.FIELD_ID))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals("LEAFID"))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_DBID))
        {
            analyser = new LongAnalyser();
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_TXID))
        {
            analyser = new LongAnalyser();
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_ACLTXID))
        {
            analyser = new LongAnalyser();
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_TXCOMMITTIME))
        {
            analyser = new LongAnalyser();
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_ACLTXCOMMITTIME))
        {
            analyser = new LongAnalyser();
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_ACLID))
        {
            analyser = new LongAnalyser();
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_TX))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_PARENT))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_LINKASPECT))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_PATH))
        {
            analyser = new PathAnalyser();
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_ANCESTOR))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_ISCONTAINER))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_READER))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_OWNER))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_ISCATEGORY))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_QNAME))
        {
            analyser = new PathAnalyser();
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_ISROOT))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_PRIMARYASSOCTYPEQNAME))
        {
            analyser = new PathAnalyser();
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_ISNODE))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_ASSOCTYPEQNAME))
        {
            analyser = new PathAnalyser();
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_PRIMARYPARENT))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_TYPE))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_ASPECT))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals(LuceneQueryParser.FIELD_FTSSTATUS))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.equals("FTSREF"))
        {
            analyser = new VerbatimAnalyser(false);
        }
        else if (fieldName.startsWith("@"))
        {
            if (fieldName.endsWith(".mimetype"))
            {
                analyser = new VerbatimAnalyser();
            }
            else if (fieldName.endsWith(".size"))
            {
                analyser = new LongAnalyser();
            }
            else if (fieldName.endsWith(".locale"))
            {
                analyser = new VerbatimAnalyser(true);
            }
            else
            {
                QName propertyQName = QName.createQName(fieldName.substring(1));
                // Temporary fix for person and user uids

                if (propertyQName.equals(ContentModel.PROP_USER_USERNAME)
                        || propertyQName.equals(ContentModel.PROP_USERNAME) || propertyQName.equals(ContentModel.PROP_AUTHORITY_NAME))
                {
                    analyser = new VerbatimAnalyser(true);
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
                                analyser = new MLAnalayser(dictionaryService, MLAnalysisMode.ALL_ONLY);
                            }
                            else if (dataType.getName().equals(DataTypeDefinition.TEXT))
                            {
                                analyser = new MLAnalayser(dictionaryService, MLAnalysisMode.ALL_ONLY);
                            }
                            else if (dataType.getName().equals(DataTypeDefinition.MLTEXT))
                            {
                                analyser = new MLAnalayser(dictionaryService, mlAlaysisMode);
                            }
                            else
                            {
                                analyser = loadAnalyzer(propertyDef);
                            }
                            break;
                        case BOTH:
                            switch (analysisMode)
                            {
                            case DEFAULT:
                            case TOKENISE:
                            case WILD:
                            case PREFIX:
                                if (dataType.getName().equals(DataTypeDefinition.CONTENT))
                                {
                                    analyser = new MLAnalayser(dictionaryService, MLAnalysisMode.ALL_ONLY);
                                }
                                else if (dataType.getName().equals(DataTypeDefinition.TEXT))
                                {
                                    analyser = new MLAnalayser(dictionaryService, MLAnalysisMode.ALL_ONLY);
                                }
                                else if (dataType.getName().equals(DataTypeDefinition.MLTEXT))
                                {
                                    analyser = new MLAnalayser(dictionaryService, mlAlaysisMode);
                                }
                                else
                                {
                                    analyser = loadAnalyzer(propertyDef);
                                }
                                break;
                            case IDENTIFIER:
                            case LIKE:
                                if (dataType.getName().equals(DataTypeDefinition.MLTEXT))
                                {
                                    analyser = new VerbatimMLAnalayser(mlAlaysisMode);
                                }
                                else
                                {
                                    analyser = new VerbatimAnalyser();
                                }
                                break;
                            default:
                                throw new UnsupportedOperationException("TYPE must not be tokenised");
                            }

                            break;
                        case FALSE:
                            // TODO: MLText verbatim analyser
                            analyser = new VerbatimAnalyser();
                            break;
                        default:
                            throw new UnsupportedOperationException("TYPE must not be tokenised");
                        }
                    }
                    else
                    {
                        switch (analysisMode)
                        {
                        case IDENTIFIER:
                        case LIKE:
                            analyser = new VerbatimAnalyser();
                            break;
                        case DEFAULT:
                        case TOKENISE:
                        case WILD:
                        case PREFIX:
                            DataTypeDefinition dataType = dictionaryService.getDataType(DataTypeDefinition.TEXT);
                            analyser = loadAnalyzer(dataType);
                            break;
                        default:
                            throw new UnsupportedOperationException();
                        }

                    }
                }
            }
        }
        else
        {
            analyser = defaultAnalyser;
        }
        analysers.put(fieldName, analyser);
        return analyser;
    }
    

    /**
     * Find an instantiate an analyser. The shuld all be thread sade as Analyser.tokenStream should be re-entrant.
     * 
     * @param dataType
     * @return
     */
    private Analyzer loadAnalyzer( PropertyDefinition propertyDef )
    {
        String analyserClassName = propertyDef.resolveAnalyserClassName().trim();
        try
        {
            Class<?> clazz = Class.forName(analyserClassName);
            Analyzer analyser = (Analyzer) clazz.newInstance();
            if (s_logger.isDebugEnabled())
            {
                s_logger.debug("Loaded " + analyserClassName + " for type " + propertyDef.getName());
            }
            return analyser;
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Unable to load analyser for property of type " + propertyDef.getName() + " using " + analyserClassName);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Unable to load analyser for property of type " + propertyDef.getName() + " using " + analyserClassName);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Unable to load analyser for property of type " + propertyDef.getName() + " using " + analyserClassName);
        }
    }
    
    private Analyzer loadAnalyzer( DataTypeDefinition dataTypeDef )
    {
        String analyserClassName = dataTypeDef.resolveAnalyserClassName().trim();
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
