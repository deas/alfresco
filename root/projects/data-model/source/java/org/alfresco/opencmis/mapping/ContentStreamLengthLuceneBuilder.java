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

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;

/**
 * Lucene Builder for CMIS content stream length property
 * 
 * @author andyh
 */
public class ContentStreamLengthLuceneBuilder extends AbstractSimpleLuceneBuilder
{
    private DictionaryService dictionaryService;
    
    /**
     * Construct
     * 
     * @param serviceRegistry
     */
    public ContentStreamLengthLuceneBuilder(DictionaryService dictionaryService)
    {
        super();
        this.dictionaryService = dictionaryService;
    }

    @Override
    public String getLuceneFieldName()
    {
        StringBuilder field = new StringBuilder(128);
        field.append("@");
        field.append(ContentModel.PROP_CONTENT);
        field.append(".size");
        return field.toString();
    }

    @Override
    protected String getValueAsString(Serializable value)
    {
        Object converted = DefaultTypeConverter.INSTANCE.convert(dictionaryService.getDataType(DataTypeDefinition.LONG), value);
        String asString = DefaultTypeConverter.INSTANCE.convert(String.class, converted);
        return asString;
    }

    @Override
    protected QName getQNameForExists()
    {
        return ContentModel.PROP_CONTENT;
    }

    @Override
    protected DataTypeDefinition getInDataType()
    {
        return dictionaryService.getDataType(DataTypeDefinition.LONG);
    }
}
