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

package org.alfresco.repo.lotus.ws.impl.helper;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.lotus.ws.ClbDataType;
import org.alfresco.repo.lotus.ws.ClbDynamicDateValue;
import org.alfresco.repo.lotus.ws.ClbDynamicDoubleValue;
import org.alfresco.repo.lotus.ws.ClbDynamicLongValue;
import org.alfresco.repo.lotus.ws.ClbDynamicStringValue;
import org.alfresco.repo.lotus.ws.ClbLabelType;
import org.alfresco.repo.lotus.ws.ClbOptionType;
import org.alfresco.repo.lotus.ws.ClbPropertySheet;
import org.alfresco.repo.lotus.ws.ClbPropertyType;
import org.alfresco.repo.lotus.ws.ClbStyleType;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;

public class AlfrescoQuickrDataTypeHelper
{
    private static ClbStyleType QUICKR_TEXT_STYLE = createStyle("ibm:textVariant", "medium");
    private static ClbStyleType QUICKR_SELECTION_STYLE = createStyle("ibm:selectionType", "radio");
    private static ClbStyleType QUICKR_DATE_STYLE = createStyle("ibm:dateVariant", "date");

    private static final String LENGTH = "LENGTH";
    private static final String LIST = "LIST";

    private static Map<QName, ClbDataType> AlfToQuickrDataTypes = new HashMap<QName, ClbDataType>();

    static
    {
        AlfToQuickrDataTypes.put(DataTypeDefinition.ANY, ClbDataType.STRING);
        AlfToQuickrDataTypes.put(DataTypeDefinition.DATE, ClbDataType.DATE_TIME);
        AlfToQuickrDataTypes.put(DataTypeDefinition.DATETIME, ClbDataType.DATE_TIME);
        AlfToQuickrDataTypes.put(DataTypeDefinition.DOUBLE, ClbDataType.DOUBLE);
        AlfToQuickrDataTypes.put(DataTypeDefinition.LONG, ClbDataType.LONG);
        AlfToQuickrDataTypes.put(DataTypeDefinition.TEXT, ClbDataType.STRING);
        AlfToQuickrDataTypes.put(DataTypeDefinition.BOOLEAN, ClbDataType.STRING);
    }

    @SuppressWarnings("unchecked")
    public static ClbPropertyType getQuickrPropertyType(PropertyDefinition alfrescoPropertyDef)
    {
        ClbPropertyType result = new ClbPropertyType();

        // set property id
        result.setPropertyId(alfrescoPropertyDef.getTitle());

        // set property name
        result.setPropertyName(alfrescoPropertyDef.getTitle());

        // set label
        ClbLabelType label = new ClbLabelType();
        label.setLabel(alfrescoPropertyDef.getTitle());
        label.setLang("en");
        result.getLabels().add(label);

        // map alfresco data type to quickr data type
        ClbDataType quickrType = AlfToQuickrDataTypes.get(alfrescoPropertyDef.getDataType().getName());
        if (quickrType != null)
        {
            result.setDataType(quickrType);
        }
        else
        {
            throw new AlfrescoRuntimeException(alfrescoPropertyDef.getDataType().getName() + " can't be used in quickr model.");
        }

        // is mandatory?
        result.setRequired(alfrescoPropertyDef.isMandatory());

        // is multiple?
        result.setMultiple(alfrescoPropertyDef.isMultiValued());

        // allow read/write
        result.setReadOnly(false);

        // allow search
        result.setSearchable(true);

        // set default value
        result.getDefaultValues().add(alfrescoPropertyDef.getDefaultValue());

        List<ConstraintDefinition> constraintDefinitions = alfrescoPropertyDef.getConstraints();

        for (ConstraintDefinition constraintDefinition : constraintDefinitions)
        {
            Constraint constraint = constraintDefinition.getConstraint();
            Map<String, Object> parameters = constraint.getParameters();

            if (constraint.getType().equals(LIST))
            {
                List<String> values = (List<String>) parameters.get("allowedValues");
                result.getStyles().add(QUICKR_SELECTION_STYLE);

                for (String value : values)
                {
                    ClbOptionType option = new ClbOptionType();
                    option.setValue(value);
                    ClbLabelType optionLabel = new ClbLabelType();
                    optionLabel.setLabel(value);
                    optionLabel.setLang("en");
                    option.getLabels().add(optionLabel);
                    result.getOptions().add(option);
                }
            }

            if (constraint.getType().equals(LENGTH))
            {
                try
                {
                    result.setMaxLength(Long.parseLong(parameters.get("maxLength").toString()));
                }
                catch (NumberFormatException e)
                {
                    // TODO handle exception
                }
            }
        }

        if (DataTypeDefinition.BOOLEAN.equals(alfrescoPropertyDef.getDataType().getName()))
        {
            result.getStyles().add(QUICKR_SELECTION_STYLE);

            ClbOptionType trueOption = new ClbOptionType();
            trueOption.setValue("true");
            ClbLabelType trueLabel = new ClbLabelType();
            trueLabel.setLabel("true");
            trueLabel.setLang("en");
            trueOption.getLabels().add(trueLabel);
            result.getOptions().add(trueOption);

            ClbOptionType falseOption = new ClbOptionType();
            falseOption.setValue("false");
            ClbLabelType falseLabel = new ClbLabelType();
            falseLabel.setLabel("false");
            falseLabel.setLang("en");
            falseOption.getLabels().add(falseLabel);
            result.getOptions().add(falseOption);
        }
        else if (DataTypeDefinition.DATE.equals(alfrescoPropertyDef.getDataType().getName()))
        {
            result.getStyles().add(QUICKR_DATE_STYLE);
        }
        else
        {
            if (result.getStyles().isEmpty())
            {
                result.getStyles().add(QUICKR_TEXT_STYLE);
            }
        }

        return result;
    }
    
    public static void addValue(ClbPropertySheet propSheet, PropertyDefinition alfrescoPropertyDef, Serializable value, String key)
    {

        String quickRType = getQuickrPropertyType(alfrescoPropertyDef).getDataType().value();
        if (ClbDataType.DATE_TIME.value().equals(quickRType))
        {
            ClbDynamicDateValue result = new ClbDynamicDateValue();
            result.setKey(key);
            try
            {
                if (value != null)
                {
                    GregorianCalendar gcal = new GregorianCalendar();
                    gcal.setTime((Date) value);
                    result.getValues().add(DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal));
                    propSheet.getDynamicDates().add(result);
                }
            }
            catch (DatatypeConfigurationException e)
            {
            }

        }

        if (ClbDataType.LONG.value().equals(quickRType))
        {
            ClbDynamicLongValue result = new ClbDynamicLongValue();
            result.setKey(key);
            result.getValues().add((Long) value);
            propSheet.getDynamicLongs().add(result);
        }

        if (ClbDataType.DOUBLE.value().equals(quickRType))
        {
            ClbDynamicDoubleValue result = new ClbDynamicDoubleValue();
            result.setKey(key);
            result.getValues().add((Double) value);
            propSheet.getDynamicDoubles().add(result);
        }

        if (ClbDataType.STRING.value().equals(quickRType))
        {
            ClbDynamicStringValue result = new ClbDynamicStringValue();
            result.setKey(key);
            Object convertedValue = DefaultTypeConverter.INSTANCE.convert(alfrescoPropertyDef.getDataType(), value);
            result.getValues().add((convertedValue != null ? convertedValue.toString() : null));
            propSheet.getDynamicStrings().add(result);
        }

    }

    private static ClbStyleType createStyle(String name, String value)
    {
        ClbStyleType result = new ClbStyleType();
        result.setName(name);
        result.setValue(value);

        return result;
    }
}
