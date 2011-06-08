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
package org.alfresco.opencmis.mapping;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.analysis.DateTimeAnalyser;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.server.support.query.CmisQueryException;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * A simple 1-1 property lucene builder mapping from a CMIS property name to an alfresco property
 * 
 * @author andyh
 */
public class DirectLuceneBuilder extends AbstractSimpleLuceneBuilder
{
    private DictionaryService dictionaryService;
    private QName alfrescoName;
    
    public DirectLuceneBuilder(DictionaryService dictionaryService, QName alfrescoName)
    {
        this.dictionaryService = dictionaryService;
        this.alfrescoName = alfrescoName;
    }
    
    @Override
    public String getLuceneSortField(LuceneQueryParser lqp)
    {
        String field = getLuceneFieldName();
        // need to find the real field to use
        Locale sortLocale = null;

        PropertyDefinition propertyDef = dictionaryService.getProperty(QName.createQName(field.substring(1)));

        if (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT))
        {
            throw new CmisQueryException("Order on content properties is not curently supported");
        }
        else if ((propertyDef.getDataType().getName().equals(DataTypeDefinition.MLTEXT)) || (propertyDef.getDataType().getName().equals(DataTypeDefinition.TEXT)))
        {
            List<Locale> locales = lqp.getSearchParameters().getLocales();
            if (((locales == null) || (locales.size() == 0)))
            {
                locales = Collections.singletonList(I18NUtil.getLocale());
            }

            if (locales.size() > 1)
            {
                throw new CmisQueryException("Order on text/mltext properties with more than one locale is not curently supported");
            }

            sortLocale = locales.get(0);
            // find best field match

            HashSet<String> allowableLocales = new HashSet<String>();
            MLAnalysisMode analysisMode = lqp.getDefaultSearchMLAnalysisMode();
            for (Locale l : MLAnalysisMode.getLocales(analysisMode, sortLocale, false))
            {
                allowableLocales.add(l.toString());
            }

            String sortField = field;

            for (Object current : lqp.getIndexReader().getFieldNames(FieldOption.INDEXED))
            {
                String currentString = (String) current;
                if (currentString.startsWith(field) && currentString.endsWith(".sort"))
                {
                    String fieldLocale = currentString.substring(field.length() + 1, currentString.length() - 5);
                    if (allowableLocales.contains(fieldLocale))
                    {
                        if (fieldLocale.equals(sortLocale.toString()))
                        {
                            sortField = currentString;
                            break;
                        }
                        else if (sortLocale.toString().startsWith(fieldLocale))
                        {
                            if (sortField.equals(field) || (currentString.length() < sortField.length()))
                            {
                                sortField = currentString;
                            }
                        }
                        else if (fieldLocale.startsWith(sortLocale.toString()))
                        {
                            if (sortField.equals(field) || (currentString.length() < sortField.length()))
                            {
                                sortField = currentString;
                            }
                        }
                    }
                }
            }

            field = sortField;

        }
        else if (propertyDef.getDataType().getName().equals(DataTypeDefinition.DATETIME))
        {
            DataTypeDefinition dataType = propertyDef.getDataType();
            String analyserClassName = dataType.getAnalyserClassName();
            if (analyserClassName.equals(DateTimeAnalyser.class.getCanonicalName()))
            {
                field = field + ".sort";
            }
        }

        return field;
    }

    @Override
    public String getLuceneFieldName()
    {
        StringBuilder field = new StringBuilder(64);
        field.append("@");
        field.append(alfrescoName);
        return field.toString();
    }

    @Override
    protected String getValueAsString(Serializable value)
    {
        PropertyDefinition pd = dictionaryService.getProperty(alfrescoName);
        Object converted = DefaultTypeConverter.INSTANCE.convert(pd.getDataType(), value);
        String asString = DefaultTypeConverter.INSTANCE.convert(String.class, converted);
        return asString;
    }

    @Override
    protected QName getQNameForExists()
    {
        return alfrescoName;
    }

    @Override
    protected DataTypeDefinition getInDataType()
    {
        PropertyDefinition pd = dictionaryService.getProperty(alfrescoName);
        return pd.getDataType();
    }

}
