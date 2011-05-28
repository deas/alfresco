package org.alfresco.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.Path.AttributeElement;
import org.alfresco.service.cmr.repository.Path.ChildAssocElement;
import org.alfresco.service.cmr.repository.datatype.TypeConversionException;
import org.alfresco.service.cmr.repository.datatype.TypeConverter;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;

public class SOLRDeserializer
{
    protected static final Log logger = LogFactory.getLog(SOLRDeserializer.class);
    
    private Set<QName> NUMBER_TYPES;

    private DictionaryService dictionaryService;

    public SOLRDeserializer()
    {
        NUMBER_TYPES = new HashSet<QName>(4);
        NUMBER_TYPES.add(DataTypeDefinition.DOUBLE);
        NUMBER_TYPES.add(DataTypeDefinition.FLOAT);
        NUMBER_TYPES.add(DataTypeDefinition.INT);
        NUMBER_TYPES.add(DataTypeDefinition.LONG);
    }
    
    public SOLRDeserializer(DictionaryService dictionaryService)
    {
        this();
        this.dictionaryService = dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    private Serializable deserializeValue(PropertyDefinition propertyDef, Object value)
    {
        QName propertyDefName = propertyDef.getDataType().getName();

        boolean isAny = propertyDefName.equals(DataTypeDefinition.ANY);
        boolean isContent = propertyDefName.equals(DataTypeDefinition.CONTENT);
        boolean isNumber = NUMBER_TYPES.contains(propertyDefName);
        boolean isBoolean = propertyDefName.equals(DataTypeDefinition.BOOLEAN);
        boolean isPath = propertyDefName.equals(DataTypeDefinition.PATH);

        try
        {
            if(isPath)
            {
                return SOLRTypeConverter.INSTANCE.convert(Path.class, (JSONArray)value);
            }
            else if(isAny)
            {
                // TODO check the actual type of the value and use constructJSONObjects if not primitive
                return SOLRTypeConverter.INSTANCE.convert(Serializable.class, value);
            }
            else if(isContent || isNumber || isBoolean)
            {
                // just return what we already have. For content properties, should be a Long
                return (Serializable)value;
            }
            else
            {
                return SOLRTypeConverter.INSTANCE.convert(Serializable.class, value);
            }
        }
        catch (TypeConversionException e)
        {
            // no type conversion
            String msg = "Unexpected type conversion error for property " + propertyDef.getName();
            logger.warn(msg, e);
            throw new IllegalArgumentException(msg, e);
        }
    }
    
    public <T> T deserializeValue(Class<T> targetClass, Object value) throws JSONException
    {
        return SOLRTypeConverter.INSTANCE.convert(targetClass, value);
    }

    public Serializable deserialize(QName propName, Object value) throws JSONException
    {
        if(value == null)
        {
            return null;
        }

        PropertyDefinition propertyDef = dictionaryService.getProperty(propName);
        if(propertyDef == null)
        {
            throw new IllegalArgumentException("Could not find property definition for property " + propName);
        }
        boolean isMulti = propertyDef.isMultiValued();

        if(isMulti)
        {
            if(!(value instanceof JSONArray))
            {
                throw new IllegalArgumentException("Multi value: expected an array, got " + value.getClass().getName());
            }
            JSONArray jsonArray = (JSONArray)value;
            List<Object> ret = new ArrayList<Object>(jsonArray.length());
            for(int i = 0; i < jsonArray.length(); i++)
            {
                Object o = jsonArray.get(i);
                ret.add(deserializeValue(propertyDef, o));
            }

            return (Serializable)ret;
        }
        else
        {
            return deserializeValue(propertyDef, value);
        }
    }
    
    private static class SOLRTypeConverter
    {
        /**
         * Default Type Converter
         */
        public static TypeConverter INSTANCE = new TypeConverter();
        
        static
        {
            // dates
            INSTANCE.addConverter(String.class, Date.class, new TypeConverter.Converter<String, Date>()
            {
                public Date convert(String source)
                {
                    try
                    {
                        Date date = ISO8601DateFormat.parse(source);
                        return date;
                    }
                    catch (Exception e)
                    {
                        throw new TypeConversionException("Failed to convert date " + source + " to string", e);
                    }
                }
            });
                    
            // node refs        
            INSTANCE.addConverter(String.class, NodeRef.class, new TypeConverter.Converter<String, NodeRef>()
            {
                public NodeRef convert(String source)
                {
                    return new NodeRef(source);
                }
            });
            
            // paths
            INSTANCE.addConverter(String.class, AttributeElement.class, new TypeConverter.Converter<String, AttributeElement>()
            {
                public AttributeElement convert(String source)
                {
                    return new Path.AttributeElement(source);
                }
            });
            
            INSTANCE.addConverter(String.class, ChildAssocElement.class, new TypeConverter.Converter<String, ChildAssocElement>()
            {
                public ChildAssocElement convert(String source)
                {
                    return new Path.ChildAssocElement(INSTANCE.convert(ChildAssociationRef.class, source));
                }
            });
            
            INSTANCE.addConverter(String.class, Path.DescendentOrSelfElement.class, new TypeConverter.Converter<String, Path.DescendentOrSelfElement>()
            {
                public Path.DescendentOrSelfElement convert(String source)
                {
                    return new Path.DescendentOrSelfElement();
                }
            });
            
            INSTANCE.addConverter(String.class, Path.ParentElement.class, new TypeConverter.Converter<String, Path.ParentElement>()
            {
                public Path.ParentElement convert(String source)
                {
                    return new Path.ParentElement();
                }
            });
            
            INSTANCE.addConverter(String.class, Path.SelfElement.class, new TypeConverter.Converter<String, Path.SelfElement>()
            {
                public Path.SelfElement convert(String source)
                {
                    return new Path.SelfElement();
                }
            });
            
            INSTANCE.addConverter(JSONArray.class, Path.class, new TypeConverter.Converter<JSONArray, Path>()
            {
                public Path convert(JSONArray source)
                {
                    try
                    {
                        Path path = new Path();
                        for(int i = 0; i < source.length(); i++)
                        {
                            String pathElementStr = source.getString(i);
                            Path.Element pathElement = null;
                            int idx = pathElementStr.indexOf("|");
                            if(idx == -1)
                            {
                                throw new IllegalArgumentException("Unable to deserialize to Path Element, invalid string " + pathElementStr);
                            }

                            String prefix = pathElementStr.substring(0, idx+1);
                            String suffix = pathElementStr.substring(idx+1);
                            if(prefix.equals("a|"))
                            {
                                pathElement = INSTANCE.convert(Path.AttributeElement.class, suffix);
                            }
                            else if(prefix.equals("p|"))
                            {
                                pathElement = INSTANCE.convert(Path.ParentElement.class, suffix);
                            }
                            else if(prefix.equals("c|"))
                            {
                                pathElement = INSTANCE.convert(Path.ChildAssocElement.class, suffix);
                            }
                            else if(prefix.equals("s|"))
                            {
                                pathElement = INSTANCE.convert(Path.SelfElement.class, suffix);
                            }
                            else if(prefix.equals("ds|"))
                            {
                                pathElement = new Path.DescendentOrSelfElement();
                            }
                            else
                            {
                                throw new IllegalArgumentException("Unable to deserialize to Path, invalid path element string " + pathElementStr);
                            }

                            path.append(pathElement);
                        }
                        return path;
                    }
                    catch(JSONException e)
                    {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
            
            // associations
            INSTANCE.addConverter(String.class, ChildAssociationRef.class, new TypeConverter.Converter<String, ChildAssociationRef>()
            {
                public ChildAssociationRef convert(String source)
                {
                    return new ChildAssociationRef(source);
                }
            });

            INSTANCE.addConverter(String.class, AssociationRef.class, new TypeConverter.Converter<String, AssociationRef>()
            {
                public AssociationRef convert(String source)
                {
                    return new AssociationRef(source);
                }
            });
        }
    }
}
