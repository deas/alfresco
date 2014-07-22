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

package org.springframework.extensions.webscripts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

/**
 * Value Converter to marshal objects between Java and Javascript. 
 * 
 * @author Kevin Roast
 */
public final class ScriptValueConverter
{
    private static final String TYPE_DATE = "Date";
    
    
    /**
     * Private constructor - methods are static
     */
    private ScriptValueConverter()
    {
    }
    
    /**
     * Convert an object from a script wrapper value to a serializable value valid outside
     * of the Rhino script processor context.
     * 
     * This includes converting JavaScript Array objects to Lists of valid objects.
     * 
     * @param value     Value to convert from script wrapper object to external object value.
     * 
     * @return unwrapped and converted value.
     */
    public static Object unwrapValue(Object value)
    {
        if (value == null)
        {
            return null;
        }
        else if (value instanceof Wrapper)
        {
            // unwrap a Java object from a JavaScript wrapper
            // recursively call this method to convert the unwrapped value
            value = unwrapValue(((Wrapper)value).unwrap());
        }
        else if (value instanceof IdScriptableObject)
        {
            // check for special case Native object wrappers
            final String className = ((IdScriptableObject)value).getClassName();
            // check for special case of the String object
            if ("String".equals(className))
            {
                value = Context.jsToJava(value, String.class);
            }
            // check for special case of a Date object
            else if ("Date".equals(className))
            {
                value = Context.jsToJava(value, Date.class);
            }
            else
            {
                // a scriptable object will probably indicate a multi-value property set
                // set using a JavaScript associative Array object
                final Scriptable values = (Scriptable)value;
                final Object[] propIds = values.getIds();
                
                // is it a JavaScript associative Array object using Integer indexes?
                if (values instanceof NativeArray && isArray(propIds))
                {
                    // convert JavaScript array of values to a List of Serializable objects
                    final List<Object> propValues = new ArrayList<Object>(propIds.length);
                    // annoyingly, it is possible for Rhino to hand out arrays where the IDs are *not* in
                    // numeric order, it's rare but it DOES happen - deal with this using add() then set()
                    final int length = (int)((NativeArray)values).getLength();
                    for (int i=0; i<length; i++)
                    {
                        propValues.add(null);
                    }
                    for (int i=0; i<propIds.length; i++)
                    {
                        // work on each key in turn
                        final Integer propId = (Integer)propIds[i];
                        
                        // get the value out for the specified key
                        Object val = values.get(propId, values);
                        // recursively call unwrapValue method to convert the contained value
                        propValues.set(propId, unwrapValue(val));
                    }
                    
                    value = propValues;
                }
                else
                {
                    // any other JavaScript object that supports properties - convert to a Map of objects
                    final Map<String, Object> propValues = new HashMap<String, Object>(propIds.length);
                    for (int i=0; i<propIds.length; i++)
                    {
                        // work on each key in turn
                        Object propId = propIds[i];
                        
                        // we are only interested in keys that indicate a list of values
                        if (propId instanceof String)
                        {
                            // get the value out for the specified key
                            Object val = values.get((String)propId, values);
                            // recursively call this method to convert the value
                            propValues.put((String)propId, unwrapValue(val));
                        }
                    }
                    value = propValues;
                }
            }
        }
        else if (value instanceof Object[])
        {
            // convert back a list Object Java values
            final Object[] array = (Object[])value;
            final ArrayList<Object> list = new ArrayList<Object>(array.length);
            for (int i=0; i<array.length; i++)
            {
                list.add(unwrapValue(array[i]));
            }
            value = list;
        }
        else if (value instanceof Map && !(value instanceof JSONObject))
        {
            // ensure each value in the Map is unwrapped (which may have been an unwrapped NativeMap!)
            final Map<Object, Object> map = (Map<Object, Object>)value;
            final Map<Object, Object> copyMap = new HashMap<Object, Object>(map.size());
            for (Object key : map.keySet())
            {
                copyMap.put(key, unwrapValue(map.get(key)));
            }
            value = copyMap;
        }
        else if (value instanceof CharSequence)
        {
            // Rhino has some interesting internal classes such as ConsString which cannot be cast to String
            // but fortunately are instanceof CharSequence so we can toString() them.
            value = value.toString();
        }
        return value;
    }
    
    /**
     * Convert an object from any repository serialized value to a valid script object.
     * This includes converting Collection multi-value properties into JavaScript Array objects.
     *
     * @param services  Repository Services Registry
     * @param scope     Scripting scope
     * @param qname     QName of the property value for conversion
     * @param value     Property value
     * 
     * @return Value safe for scripting usage
     */
    public static Object wrapValue(Scriptable scope, Object value)
    {
        // perform conversions from Java objects to JavaScript scriptable instances
        if (value == null)
        {
            return null;
        }
        else if (value instanceof Date)
        {
            // convert Date to JavaScript native Date object
            // call the "Date" constructor on the root scope object - passing in the millisecond
            // value from the Java date - this will construct a JavaScript Date with the same value
            Date date = (Date)value;
            value = ScriptRuntime.newObject(
                        Context.getCurrentContext(), scope, TYPE_DATE, new Object[] {date.getTime()});
        }
        else if (value instanceof Collection)
        {
            // recursively convert each value in the collection
            Collection<Object> collection = (Collection<Object>)value;
            Object[] array = new Object[collection.size()];
            int index = 0;
            for (Object obj : collection)
            {
                array[index++] = wrapValue(scope, obj);
            }
            // convert array to a native JavaScript Array
            value = Context.getCurrentContext().newArray(scope, array);
        }
        else if (value instanceof Map)
        {
            value = new NativeMap(scope, (Map)value);
        }
        
        // simple numbers, strings and booleans are wrapped automatically by Rhino
        
        return value;
    }
    
    /**
     * Look at the id's of a native array and try to determine whether it's actually an Array or a Hashmap
     * 
     * @param ids       id's of the native array
     * @return boolean  true if it's an array, false otherwise (ie it's a map)
     */
    private static boolean isArray(final Object[] ids)
    {
        boolean result = true;
        for (int i=0; i<ids.length; i++)
        {
            if (!(ids[i] instanceof Integer))
            {
               result = false;
               break;
            }
        }
        return result;
    }
}
