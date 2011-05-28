package org.alfresco.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @since 4.0
 *
 */
public class SOLRSerializer
{
    protected static final Log logger = LogFactory.getLog(SOLRSerializer.class);
    
    private Set<QName> NUMBER_TYPES;

    private DictionaryService dictionaryService;

    public void init()
    {
        PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);

        NUMBER_TYPES = new HashSet<QName>(4);
        NUMBER_TYPES.add(DataTypeDefinition.DOUBLE);
        NUMBER_TYPES.add(DataTypeDefinition.FLOAT);
        NUMBER_TYPES.add(DataTypeDefinition.INT);
        NUMBER_TYPES.add(DataTypeDefinition.LONG);
    }
    
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    public Object serializeValue(PropertyDefinition propertyDef, Serializable value)
    {
        QName propertyDefName = propertyDef.getDataType().getName();
        boolean isAny = propertyDefName.equals(DataTypeDefinition.ANY);
        boolean isContent = propertyDefName.equals(DataTypeDefinition.CONTENT);
        boolean isNumber = NUMBER_TYPES.contains(propertyDefName);
        boolean isBoolean = propertyDefName.equals(DataTypeDefinition.BOOLEAN);
        boolean isPath = propertyDefName.equals(DataTypeDefinition.PATH);

        if(isPath)
        {
            return SOLRTypeConverter.INSTANCE.convert(JSONArray.class, (Path)value);
        }
        else if(isAny)
        {
            // TODO check the actual type of the value and use constructJSONObjects if not primitive
            return SOLRTypeConverter.INSTANCE.convert(JSONObject.class, value);
        }
        else if(isContent)
        {
            return SOLRTypeConverter.INSTANCE.convert(Long.class, value);
        }
        else if(isNumber || isBoolean)
        {
            return String.valueOf(value);
        }
        else
        {
            // convert to string
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            sb.append(SOLRTypeConverter.INSTANCE.convert(String.class, value));
            sb.append("\"");
            return sb.toString();
        }
    }
    
    public Object serialize(QName propName, Serializable value)
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
        QName propertyDefName = propertyDef.getDataType().getName();
        boolean isMulti = propertyDef.isMultiValued();

        //Collection<Serializable> c = DefaultTypeConverter.INSTANCE.getCollection(Serializable.class, value);

        if(isMulti)
        {
            if(!(value instanceof Collection))
            {
                throw new IllegalArgumentException("Multi value: expected a collection, got " + value.getClass().getName());
            }

            @SuppressWarnings("unchecked")
            Collection<Serializable> c = (Collection<Serializable>)value;
            JSONArray ret = new JSONArray();
            for(Serializable o : c)
            {
                ret.put(serializeValue(propertyDef, o));
            }

            return ret;
        }
        else
        {
            return serializeValue(propertyDef, value);
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
            INSTANCE.addConverter(Date.class, String.class, new TypeConverter.Converter<Date, String>()
            {
                public String convert(Date source)
                {
                    try
                    {
                        return ISO8601DateFormat.format(source);
                    }
                    catch (Exception e)
                    {
                        throw new TypeConversionException("Failed to convert date " + source + " to string", e);
                    }
                }
            });
            
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

            // content
//            INSTANCE.addConverter(ContentDataWithId.class, Long.class, new TypeConverter.Converter<ContentDataWithId, Long>()
//            {
//                public Long convert(ContentDataWithId source)
//                {
//                    return source.getId();
//                }
//            });
                    
            // node refs
            INSTANCE.addConverter(NodeRef.class, String.class, new TypeConverter.Converter<NodeRef, String>()
            {
                public String convert(NodeRef source)
                {
                    return source.toString();
                }
            });
            
            INSTANCE.addConverter(String.class, NodeRef.class, new TypeConverter.Converter<String, NodeRef>()
            {
                public NodeRef convert(String source)
                {
                    return new NodeRef(source);
                }
            });
            
            // paths
            INSTANCE.addConverter(AttributeElement.class, String.class, new TypeConverter.Converter<AttributeElement, String>()
            {
                public String convert(AttributeElement source)
                {
                    return "a|" + source.toString();
                }
            });
            
            INSTANCE.addConverter(ChildAssocElement.class, String.class, new TypeConverter.Converter<ChildAssocElement, String>()
            {
                public String convert(ChildAssocElement source)
                {
                    return "c|" + source.getRef().toString();
                }
            });
            
            INSTANCE.addConverter(Path.DescendentOrSelfElement.class, String.class, new TypeConverter.Converter<Path.DescendentOrSelfElement, String>()
            {
                public String convert(Path.DescendentOrSelfElement source)
                {
                    return "ds|" + source.toString();
                }
            });
            
            INSTANCE.addConverter(Path.ParentElement.class, String.class, new TypeConverter.Converter<Path.ParentElement, String>()
            {
                public String convert(Path.ParentElement source)
                {
                    return "p|" + source.toString();
                }
            });
            
            INSTANCE.addConverter(Path.SelfElement.class, String.class, new TypeConverter.Converter<Path.SelfElement, String>()
            {
                public String convert(Path.SelfElement source)
                {
                    return "s|" + source.toString();
                }
            });
            
//            INSTANCE.addConverter(JCRPath.SimpleElement.class, String.class, new TypeConverter.Converter<JCRPath.SimpleElement, String>()
//            {
//                public String convert(JCRPath.SimpleElement source)
//                {
//                    return "se|" + source.toString();
//                }
//            });
            
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
            
//            INSTANCE.addConverter(String.class, SimpleElement.class, new TypeConverter.Converter<String, SimpleElement>()
//            {
//                public SimpleElement convert(String source)
//                {
//                    return new SimpleElement(null, source);
//                }
//            });
            
            INSTANCE.addConverter(Path.class, JSONArray.class, new TypeConverter.Converter<Path, JSONArray>()
            {
                public JSONArray convert(Path source)
                {
                    JSONArray pathArray = new JSONArray();
                    for(Path.Element element : source)
                    {
                        pathArray.put(INSTANCE.convert(String.class, element));
                    }
                    return pathArray;
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
//                            else if(prefix.equals("se|"))
//                            {
//                                pathElement = new JCRPath.SimpleElement(QName.createQName(suffix));
//                            }
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
            INSTANCE.addConverter(ChildAssociationRef.class, String.class, new TypeConverter.Converter<ChildAssociationRef, String>()
            {
                public String convert(ChildAssociationRef source)
                {
                    return source.toString();
                }
            });

            INSTANCE.addConverter(String.class, ChildAssociationRef.class, new TypeConverter.Converter<String, ChildAssociationRef>()
            {
                public ChildAssociationRef convert(String source)
                {
                    return new ChildAssociationRef(source);
                }
            });

            INSTANCE.addConverter(AssociationRef.class, String.class, new TypeConverter.Converter<AssociationRef, String>()
            {
                public String convert(AssociationRef source)
                {
                    return source.toString();
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
