/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts.json;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptParameter;

/**
 * Collection of JSON Utility methods.
 * This class is immutable.
 * 
 * @author Roy Wetherall
 * @author Kevin Roast
 */
@ScriptClass 
(
        help="Collection of JSON Utility methods.",
        code="//JavaScript Sample Code\nvar myObj = {'name':'Test Object','size':100};\nvar myObjAsString = jsonUtils.toJSONString(myObj);\n\n//Freemarker Template Sample Code\n[\n    <#list tags as tag>\n        ${jsonUtils.encodeJSONString(tag)}<#if tag_has_next>,</#if>\n    </#list>\n]",
        types=
        {
                ScriptClassType.JavaScriptRootObject,
                ScriptClassType.TemplateRootObject
        }
)
public class JSONUtils
{
    private static final String TYPE_DATE = "Date";
    
    /**
     * Converts a given JavaScript native object and converts it to the relevant JSON string.
     * 
     * @param object            JavaScript object
     * @return String           JSON      
     * @throws IOException 
     */
    @ScriptMethod
    (
            help="Converts a given JavaScript native object and converts it to the relevant JSON string.",
            code="//JavaScript Sample Code\nvar myObj = {'name':'Test Object','size':100};\nvar myObjAsString = jsonUtils.toJSONString(myObj);",
            output="JSON string"
    )
    public String toJSONString(@ScriptParameter(help="JavaScript object") Object object) throws IOException
    {
        StringBuilderWriter buffer = new StringBuilderWriter(128);
        valueToJSONString(object, new JSONWriter(buffer));
        return buffer.toString();
    }
    
    /**
     * Converts the given JavaScript native object to a org.json.simple.JSONObject Java Object.
     * This is a specialized method only used by routines that will later expect a JSONObject.
     * 
     * @param object JavaScript native object
     * @return JSONObject
     * @throws IOException
     */
    @ScriptMethod
    (
            help="Converts the given JavaScript native object to a org.json.simple.JSONObject Java Object.",
            output="the created org.json.simple.JSONObject Java Object"
    )
    public JSONObject toJSONObject(Object object) throws IOException
    {
        StringBuilderWriter buffer = new StringBuilderWriter(128);
        valueToJSONString(object, new JSONWriter(buffer));
        return (JSONObject)JSONValue.parse(buffer.toString());
    }
    
    /**
     * Takes a JSON string and converts it to a native java script object or array
     * 
     * @param  jsonString       a valid json string
     * @return NativeObject     the created native JS object that represents the JSON object or array
     */
    @ScriptMethod
    (
            help="Takes a JSON string and converts it to a native java script object or array",
            code="//JavaScript Sample Code\nmodel.postCode = jsonUtils.toObject(json).postCode;",
            output="the created native JS object that represents the JSON object or array"
    )
    public ScriptableObject toObject(@ScriptParameter(help="A valid json string") String jsonString)
    {
        ScriptableObject result = null;
        
        // Parse JSON string
        final Object jsonObject = JSONValue.parse(jsonString);
        
        // Create native object
        if (jsonObject != null)
        {
            if (jsonObject instanceof JSONObject)
            {
                result = toObject((JSONObject) jsonObject);
            }
            else if (jsonObject instanceof JSONArray)
            {
                result = toObject((JSONArray) jsonObject);
            }
        }
        return result;
    }
    
    /**
     * Takes a JSON object and converts it to a native JS object.
     * 
     * @param jsonObject        the json object
     * @return NativeObject     the created native object
     */
    @ScriptMethod
    (
            help="Takes a JSON object and converts it to a native java script object",
            code="//JavaScript Sample Code\nmodel.postCode = jsonUtils.toObject(json).postCode;",
            output="the created native object"
    )
    public NativeObject toObject(@ScriptParameter(help="The json object") JSONObject jsonObject)
    {
        // Create native object 
        final NativeObject object = new NativeObject();
        
        for (final Object key : ((JSONObject)jsonObject).keySet())
        {
            final Object value = ((JSONObject)jsonObject).get(key);
            if (value instanceof JSONObject)
            {
                object.put((String)key, object, toObject((JSONObject)value));
            }
            else if (value instanceof JSONArray)
            {
                object.put((String)key, object, toObject((JSONArray)value));
            }
            else
            {
                object.put((String)key, object, value);
            }
        }
        
        return object;
    }
    
    /**
     * Takes a JSON array and converts it to a native JS array.
     * 
     * @param jsonObject        the json array
     * @return NativeObject     the created native array
     */
    @ScriptMethod
    (
            help="Takes a JSON object and converts it to a native java script array",
            code="//JavaScript Sample Code\nmodel.postCode = jsonUtils.toObject(json).postCode;",
            output="the created native array"
    )
    public NativeArray toObject(@ScriptParameter(help="The json array") JSONArray jsonArray)
    {
        Object[] array = new Object[jsonArray.size()];
        
        for (int i = 0; i < jsonArray.size(); i++)
        {
            final Object value = jsonArray.get(i);
            if (value instanceof JSONObject)
            {
                array[i] = toObject((JSONObject)value);
            }
            else if (value instanceof JSONArray)
            {
                array[i] = toObject((JSONArray)value);
            }
            else
            {
                array[i] = value;
            }
        }
        
        // Create native object from Object[]
        return new NativeArray(array);
    }
    
    /**
     * Build a JSON string for a native object
     * 
     * @param nativeObject
     * @param writer
     * @throws IOException 
     */
    private void nativeObjectToJSONString(NativeObject nativeObject, JSONWriter writer) throws IOException
    {
        writer.startObject();
        
        final Object[] ids = nativeObject.getIds();
        for (final Object id : ids)
        {
            String key = id.toString();
            writer.startValue(key);
            
            final Object value = nativeObject.get(key, nativeObject);
            valueToJSONString(value, writer);
        }
        
        writer.endObject();
    }
    
    /**
     * Build JSON string for a native array
     * 
     * @param nativeArray
     * @param writer
     * @throws IOException 
     */
    private void nativeArrayToJSONString(NativeArray nativeArray, JSONWriter writer) throws IOException
    {
        final Object[] propIds = nativeArray.getIds();
        if (isArray(propIds) == true)
        {      
            writer.startArray();
            
            for (int i=0; i<propIds.length; i++)
            {
                final Object propId = propIds[i];
                final Object value = nativeArray.get((Integer)propId, nativeArray);
                valueToJSONString(value, writer);
            }
            
            writer.endArray();
        }
        else
        {
            writer.startObject();
            
            for (final Object propId : propIds)
            {
                final Object value = nativeArray.get(propId.toString(), nativeArray);
                writer.startValue(propId.toString());
                valueToJSONString(value, writer);    
            }            
            
            writer.endObject();
        }
    }
    
    /**
     * Look at the id's of a native array and try to determine whether it's actually an Array or a HashMap
     * 
     * @param ids       id's of the native array
     * @return boolean  true if it's an array, false otherwise (ie it's a map)
     */
    private boolean isArray(Object[] ids)
    {
        boolean result = true;
        for (Object id : ids)
        {
            if (id instanceof Integer == false)
            {
               result = false;
               break;
            }
        }
        return result;
    }
    
    /**
     * Convert value to JSON string
     * 
     * @param value         Java object value
     * @param JSONWriter    JSONWriter for output stream
     * @throws IOException 
     */
    private void valueToJSONString(Object value, JSONWriter writer) throws IOException
    {
        if (value instanceof IdScriptableObject &&
            TYPE_DATE.equals(((IdScriptableObject)value).getClassName()))
        {
            Date date = (Date)Context.jsToJava(value, Date.class);
            
            // Build the JSON object to represent the UTC date
            writer.startObject()
                .writeValue("zone", "UTC")
                .writeValue("year", date.getYear())
                .writeValue("month", date.getMonth())
                .writeValue("date", date.getDate())
                .writeValue("hours", date.getHours())
                .writeValue("minutes", date.getMinutes())
                .writeValue("seconds", date.getSeconds())
                .writeValue("milliseconds", date.getTime())
                .endObject();
        }
        else if (value instanceof NativeJavaObject)
        {
            // extract the underlying Java object and recursively output
            Object javaValue = Context.jsToJava(value, Object.class);
            valueToJSONString(javaValue, writer);
        }
        else if (value instanceof NativeArray)
        {
            // Output the native object
            nativeArrayToJSONString((NativeArray)value, writer);
        }
        else if (value instanceof NativeObject)
        {
            // Output the native array
            nativeObjectToJSONString((NativeObject)value, writer);
        }
        else if (value instanceof Number)
        {
            if (value instanceof Integer || value instanceof Long)
            {
                writer.writeValue( ((Number)value).longValue() );
            }
            else if (value instanceof Double)
            {
                writer.writeValue( ((Number)value).doubleValue() );
            }
            else if (value instanceof Float)
            {
                writer.writeValue( ((Number)value).floatValue() );
            }
            else
            {
                writer.writeValue( ((Number)value).doubleValue() );
            }
        }
        else if (value instanceof Boolean)
        {
            writer.writeValue( ((Boolean)value).booleanValue() );
        }
        else if (value instanceof Map)
        {
            writer.startObject();
            for (Object key: ((Map)value).keySet())
            {
                writer.startValue(key.toString());
                valueToJSONString(((Map)value).get(key), writer);
                writer.endValue();
            }
            writer.endObject();
        }
        else if (value instanceof List)
        {
            writer.startArray();
            for (Object val: (List)value)
            {
                valueToJSONString(val, writer);
            }
            writer.endArray();
        }
        else if (value != null)
        {
            writer.writeValue(value.toString());
        }
        else
        {
            writer.writeNullValue();
        }
    }
    
    /**
     * Encodes a JSON string value
     * 
     * @param value     value to encode
     * @return String   encoded value
     */
    @ScriptMethod
    (
            help="Encodes a JSON string value",
            output="Encoded value"
    )
    public Object encodeJSONString(@ScriptParameter(help="Value to encode") Object value)
    {
        if (value instanceof String)
        {
            return JSONWriter.encodeJSONString((String)value);
        }
        else
        {
            return value;
        }
    }
}
