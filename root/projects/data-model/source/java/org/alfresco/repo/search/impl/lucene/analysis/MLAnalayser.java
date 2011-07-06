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
package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.springframework.extensions.surf.util.I18NUtil;

public class MLAnalayser extends Analyzer
{
    private static Log s_logger = LogFactory.getLog(MLAnalayser.class);

    private DictionaryService dictionaryService;

    private HashMap<Pair<String, Locale>, Analyzer> analysers = new HashMap<Pair<String, Locale>, Analyzer>();

    private MLAnalysisMode mlAnalaysisMode;

    private Analyzer analyzer;
    
    public MLAnalayser(DictionaryService dictionaryService, MLAnalysisMode mlAnalaysisMode)
    {
        this(dictionaryService, mlAnalaysisMode, null);
    }
 

    public MLAnalayser(DictionaryService dictionaryService, MLAnalysisMode mlAnalaysisMode, Analyzer analyzer)
    {
        this.dictionaryService = dictionaryService;
        this.mlAnalaysisMode = mlAnalaysisMode;
        this.analyzer = analyzer;
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        // We use read ahead to get the language info - if this does not exist we need to restart
        // an use the default - there foer we need mark and restore.
        BufferedReader breader = new BufferedReader(reader);
        if (reader instanceof BufferedReader)
        {
            breader = (BufferedReader)reader;
        }
        else
        {
            breader = new BufferedReader(reader);
        }

        try
        {
            if (!breader.markSupported())
            {
                throw new AnalysisException(
                        "Multilingual tokenisation requires a reader that supports marks and reset");
            }
            breader.mark(100);
            StringBuilder builder = new StringBuilder();
            if (breader.read() == '\u0000')
            {
                String language = "";
                String country = "";
                String varient = "";
                char c;
                int count = 0;
                while ((c = (char) breader.read()) != '\u0000')
                {
                    if (count++ > 99)
                    {
                        breader.reset();
                        return getDefaultAnalyser(fieldName).tokenStream(fieldName, breader);
                    }
                    if (c == '_')
                    {
                        if (language.length() == 0)
                        {
                            language = builder.toString();
                        }
                        else if (country.length() == 0)
                        {
                            country = builder.toString();
                        }
                        else if (varient.length() == 0)
                        {
                            varient = builder.toString();
                        }
                        else
                        {
                            breader.reset();
                            return getDefaultAnalyser(fieldName).tokenStream(fieldName, breader);
                        }
                        builder = new StringBuilder();
                    }
                    else
                    {
                        builder.append(c);
                    }
                }
                if (builder.length() > 0)
                {
                    if (language.length() == 0)
                    {
                        language = builder.toString();
                    }
                    else if (country.length() == 0)
                    {
                        country = builder.toString();
                    }
                    else if (varient.length() == 0)
                    {
                        varient = builder.toString();
                    }
                    else
                    {
                        breader.reset();
                        return getDefaultAnalyser(fieldName).tokenStream(fieldName, breader);
                    }
                }
                Locale locale = new Locale(language, country, varient);
                // leave the reader where it is ....
                return new MLTokenDuplicator(getAnalyser(fieldName, locale).tokenStream(fieldName, breader), locale, breader, mlAnalaysisMode);
            }
            else
            {
                breader.reset();
                return getDefaultAnalyser(fieldName).tokenStream(fieldName, breader);
            }
        }
        catch (IOException io)
        {
            try
            {
                breader.reset();
            }
            catch (IOException e)
            {
                throw new AnalysisException("Failed to reset buffered reader - token stream will be invalid", e);
            }
            return getDefaultAnalyser(fieldName).tokenStream(fieldName, breader);
        }
    }

    private Analyzer getDefaultAnalyser(String fieldName)
    {
        return getAnalyser(fieldName, I18NUtil.getLocale());
    }

    private Analyzer getAnalyser(String fieldName, Locale locale)
    {
        if(analyzer != null)
        {
            return analyzer;
        }
        Pair<String, Locale> key = new Pair<String, Locale>(fieldName, locale);
        Analyzer localeSpecificAnalyzer = (Analyzer) analysers.get(key);
        if (localeSpecificAnalyzer == null)
        {
            localeSpecificAnalyzer = findAnalyser(key);
        }
        // wrap analyser to produce plain and prefixed tokens
        return localeSpecificAnalyzer;
    }

    private Analyzer findAnalyser(Pair<String, Locale> key)
    {
        Analyzer localeSpecificAnalyzer = loadAnalyzer(key);
        analysers.put(key, localeSpecificAnalyzer);
        return localeSpecificAnalyzer;
    }

    private Analyzer loadAnalyzer(Pair<String, Locale> key)
    {
        QName propertyQName = QName.createQName(key.getFirst().substring(1));
        PropertyDefinition propertyDef = dictionaryService.getProperty(propertyQName);
        String analyserClassName;
        if(propertyDef == null)
        {
            DataTypeDefinition dataType = dictionaryService.getDataType(DataTypeDefinition.TEXT);
            analyserClassName = dataType.resolveAnalyserClassName(key.getSecond());
        }
        else
        {
            analyserClassName = propertyDef.resolveAnalyserClassName(key.getSecond());
        }
        
        if (s_logger.isDebugEnabled())
        {
            s_logger.debug("Loading " + analyserClassName + " for " + key);
        }
        try
        {
            Class<?> clazz = Class.forName(analyserClassName);
            Analyzer localeSpecificAnalyzer = (Analyzer) clazz.newInstance();
            return localeSpecificAnalyzer;
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Unable to load analyser" + analyserClassName);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Unable to load analyser" + analyserClassName);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Unable to load analyser" + analyserClassName);
        }
    }
    
    
}
