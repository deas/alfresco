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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.processor.JSScriptProcessor.PresentationWrapFactory;

/**
 * JSON Utils Unit Test
 * 
 * @author Roy Wetherall
 */
public class JSONUtilsTest extends TestCase
{
    private static WrapFactory wrapFactory = new PresentationWrapFactory(); 
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }
    
    public void testToJSONString() throws Exception
    {
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("jsonUtils", new JSONUtils());
        
        Object result = executeScript(SCRIPT_1, model, true);
        String value = Context.toString(result);
        
        System.out.println(value);
        
        JSONObject obj = new JSONObject(value);
        assertNotNull(obj);
        assertEquals(1, obj.getInt("number"));
        assertEquals("string", obj.getString("string"));
        assertNotNull("date", obj.getJSONObject("date"));
        assertEquals(3.142, obj.getDouble("number2"));
        assertEquals("UTC", obj.getJSONObject("date").get("zone"));
        assertEquals("hello", obj.getJSONObject("myObject").get("hello"));
        assertEquals(123, obj.getJSONObject("myObject").get("goodbye"));
        
        result = executeScript(SCRIPT_2, model, true);
        value = Context.toString(result);
        
        System.out.println(value);
        
        obj = new JSONObject(value);
        assertEquals("a value", obj.getString("a"));
        assertEquals("b value", obj.getString("b"));
        
        result = executeScript(SCRIPT_3, model, true);
        value = Context.toString(result);
        
        System.out.println(value);
        
        JSONArray arr = new JSONArray(value);
        assertEquals(5, arr.length());
        assertEquals("one", arr.getString(0));
        assertEquals(12, arr.getInt(1));
        assertEquals(3.142, arr.getDouble(2));
        assertEquals(true, arr.getBoolean(3));
        assertEquals("hello", arr.getJSONObject(4).getString("hello"));
        assertEquals(123, arr.getJSONObject(4).getInt("goodbye"));
        
        JSONObject testObject = new JSONObject();
        testObject.put("string", "myString");
        model.put("json", testObject);        
        
        result = executeScript(SCRIPT_4, model, true);
        value = Context.toString(result);
        
        System.out.println(value);
    }
    
    public void testToObject()
        throws Exception
    {
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("jsonUtils", new JSONUtils());
        
        JSONObject testObject = new JSONObject();
        testObject.put("string", "myString");
        testObject.put("int", 10);
        testObject.put("number", 3.142);
        JSONObject subObj = new JSONObject();
        subObj.put("sunValue", "tad-ahhhh");
        testObject.put("comp1", subObj);
        JSONArray subArr = new JSONArray();
        subArr.put("myArrayVal");
        testObject.put("comp2", subArr);
        model.put("json", testObject); 
        model.put("jsonString", testObject.toString());        
        
        Object result = executeScript(SCRIPT_5, model, true);
        assertNotNull(result);
        NativeObject nativeObj = (NativeObject)result;
        assertEquals("myString", nativeObj.get("string", nativeObj));
        assertEquals(10, ((Number)nativeObj.get("int", nativeObj)).intValue());
        assertEquals(3.142, nativeObj.get("number", nativeObj));
        NativeObject subObjResult = (NativeObject)nativeObj.get("comp1", nativeObj);
        assertEquals("tad-ahhhh", subObjResult.get("sunValue", subObjResult));    
        NativeArray subArrResult = (NativeArray)nativeObj.get("comp2", nativeObj);
        assertEquals("myArrayVal", subArrResult.get(0));
        
        result = executeScript(SCRIPT_6, model, true);
        assertNotNull(result);
        nativeObj = (NativeObject)result;
        assertEquals("myString", nativeObj.get("string", nativeObj));
        assertEquals(10, ((Number)nativeObj.get("int", nativeObj)).intValue());
        assertEquals(3.142, nativeObj.get("number", nativeObj));
        subObjResult = (NativeObject)nativeObj.get("comp1", nativeObj);
        assertEquals("tad-ahhhh", subObjResult.get("sunValue", subObjResult));
        subArrResult = (NativeArray)nativeObj.get("comp2", nativeObj);
        assertEquals("myArrayVal", subArrResult.get(0));
    }
    
    public void testArrayToObject()
        throws Exception
    {
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("jsonUtils", new JSONUtils());
        
        JSONArray testArr = new JSONArray();
        testArr.put("myArrayVal");
        model.put("json", testArr);
        model.put("jsonString", testArr.toString());
        
        Object result = executeScript(SCRIPT_5, model, true);
        assertNotNull(result);
        NativeArray nativeArr = (NativeArray)result;
        assertEquals(1, nativeArr.getLength());
        
        result = executeScript(SCRIPT_6, model, true);
        assertNotNull(result);
        nativeArr = (NativeArray)result;
        assertEquals(1, nativeArr.getLength());
    }
    
    public void testSimpleValuesToObject()
        throws Exception
    {
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("jsonUtils", new JSONUtils());
        model.put("json", "");
        Object result = executeScript(SCRIPT_5, model, true);
        assertNull(result);

        model.put("json", 123);
        result = executeScript(SCRIPT_5, model, true);
        assertNull(result);
    }
    
    public void testParenthesisedString()
        throws Exception
    {
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("jsonUtils", new JSONUtils());
        model.put("jsonString", "({\"a\": \"b\"})");
        Object result = executeScript(SCRIPT_6, model, true);
        assertNull(result);
    }
    
    public void testInvalidString()
        throws Exception
    {
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("jsonUtils", new JSONUtils());
        model.put("jsonString", "88&&^&@(");
        Object result = executeScript(SCRIPT_6, model, true);
        assertNull(result);
    }
    
    
    
    private Object executeScript(String script, Map<String, Object> model, boolean secure)
    {
        Context cx = Context.enter();
        try
        {
            cx.setWrapFactory(wrapFactory);
            Scriptable scope;
            if (!secure)
            {
                scope = cx.initStandardObjects();
                // remove security issue related objects - this ensures the script may not access
                // unsecure java.* libraries or import any other classes for direct access - only
                // the configured root host objects will be available to the script writer
                scope.delete("Packages");
                scope.delete("getClass");
                scope.delete("java");
            }
            else
            {
                // allow access to all libraries and objects, including the importer
                // @see http://www.mozilla.org/rhino/ScriptingJava.html
                scope = new ImporterTopLevel(cx);
            }

            // insert supplied object model into root of the default scope
            if (model != null)
            {
                for (String key : model.keySet())
                {
                    Object obj = model.get(key);
                    ScriptableObject.putProperty(scope, key, obj);
                }
            }
            
            // execute the script
            Object result = cx.evaluateString(scope, script, "AlfrescoScript", 1, null);
            return result;
        }
        catch (Throwable e)
        {
            throw new WebScriptException(e.getMessage(), e);
        }
        finally
        {
            Context.exit();
        }
    }
    
    private static final String SCRIPT_1 =
        "var obj = new Object();" +
        "obj.string = \"string\";" +
        "obj.number = 1;" +
        "obj.number2 = 3.142;" +
        "obj.date = new Date();" +
        "obj.myObject = new Object();" +
        "   obj.myObject.hello = \"hello\";" +
        "   obj.myObject.goodbye = 123;" +
        "jsonUtils.toJSONString(obj);";
    private static final String SCRIPT_2 =
        "var array = Array();" +
        "array[\"a\"] = \"a value\";" +
        "array[\"b\"] = \"b value\";" +
        "jsonUtils.toJSONString(array);";
    private static final String SCRIPT_3 =
        "var array = Array();" +
        "var myObject = new Object();" +
        "myObject.hello = \"hello\";" +
        "myObject.goodbye = 123;" +
        "array[0] = \"one\";" +
        "array[1] = 12;" +
        "array[2] = 3.142;" +
        "array[3] = true;" +
        "array[4] = myObject;" +
        "jsonUtils.toJSONString(array);";
    private static final String SCRIPT_4 =
        "var obj = new Object();" +
        "obj.value = json.getString(\"string\");" +
        "jsonUtils.toJSONString(obj);";
    private static final String SCRIPT_5 =
        "var obj = jsonUtils.toObject(json);" +
        "obj;";
    private static final String SCRIPT_6 =
        "var obj = jsonUtils.toObject(jsonString);" +
        "obj;";
}
