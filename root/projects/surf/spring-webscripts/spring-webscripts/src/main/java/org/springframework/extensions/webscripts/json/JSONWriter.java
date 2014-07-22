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
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.springframework.extensions.surf.util.StringBuilderWriter;

/**
 * Fast and simple JSON stream writer. Wraps a Writer to output a JSON object stream.
 * No intermediate objects are created - writes are immediate to the underlying stream.
 * Quoted and correct JSON encoding is performed on string values, - encoding is
 * not performed on key names - it is assumed they are simple strings. The developer must
 * call JSONWriter.encodeJSONString() on the key name if required.
 * <p>
 * The JSON output is safe to be rendered directly into a "text/javascript" mimetype
 * resource as all unicode characters that are not supported in JavaScript are encoded in
 * hex \\uXXXX format.
 * 
 * @since 1.0
 * Added improvements to support 'double' and 'long' datatype and all methods now return
 * the current JSONWriter to allow chaining of calls for more succinct serialization code.
 * @since 1.2
 * Improvements to handle NaN/Infinity in double/float processing. Added helpers to
 * recursively encode a hierarchy of Java POJO objects (List, Map, basic data-types) into
 * a JSON string.
 * 
 * @author Kevin Roast
 */
public final class JSONWriter
{
    private Writer out;
    private Stack<Boolean> stack = new Stack<Boolean>();

    /**
     * Constructor
     * 
     * @param out    The Writer to immediately append values to (no internal buffering)
     */
    public JSONWriter(Writer out)
    {
        this.out = out;
        stack.push(Boolean.FALSE);
    }

    /**
     * Start an array structure, the endArray() method must be called later.
     * NOTE: Within the array, either output objects or use the single arg writeValue() method.
     */
    public JSONWriter startArray() throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write("[");
        stack.pop();
        stack.push(Boolean.TRUE);
        stack.push(Boolean.FALSE);
        return this;
    }

    /**
     * End an array structure.
     */
    public JSONWriter endArray() throws IOException
    {
        out.write("]");
        stack.pop();
        return this;
    }

    /**
     * Start an object structure, the endObject() method must be called later.
     */
    public JSONWriter startObject() throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write("{");
        stack.pop();
        stack.push(Boolean.TRUE);
        stack.push(Boolean.FALSE);
        return this;
    }

    /**
     * End an object structure.
     */
    public JSONWriter endObject() throws IOException
    {
        out.write("}");
        stack.pop();
        return this;
    }

    /**
     * Start a value (outputs just a name key), the endValue() method must be called later.
     * NOTE: follow with an array or object only.
     */
    public JSONWriter startValue(String name) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write('"');
        out.write(name);
        out.write("\": ");
        stack.pop();
        stack.push(Boolean.TRUE);
        stack.push(Boolean.FALSE);
        return this;
    }

    /**
     * End a value that was started with startValue()
     */
    public JSONWriter endValue()
    {
        stack.pop();
        return this;
    }

    /**
     * Output a JSON string name and value pair.
     */
    public JSONWriter writeValue(String name, String value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write('"');
        out.write(name);
        out.write("\": \"");
        out.write(encodeJSONString(value));
        out.write('"');
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON number name and value pair.
     */
    public JSONWriter writeValue(String name, int value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write('"');
        out.write(name);
        out.write("\": ");
        out.write(Integer.toString(value));
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON number name and value pair.
     */
    public JSONWriter writeValue(String name, long value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write('"');
        out.write(name);
        out.write("\": ");
        out.write(Long.toString(value));
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON number name and value pair.
     */
    public JSONWriter writeValue(String name, float value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write('"');
        out.write(name);
        out.write("\": ");
        out.write(Float.toString(value));
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON number name and value pair.
     */
    public JSONWriter writeValue(String name, double value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write('"');
        out.write(name);
        out.write("\": ");
        out.write(Double.toString(value));
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON boolean name and value pair.
     */
    public JSONWriter writeValue(String name, boolean value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write('"');
        out.write(name);
        out.write("\": ");
        out.write(Boolean.toString(value));
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON string value.
     * NOTE: no name is written - call from within an array structure.
     */
    public JSONWriter writeValue(String value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write('"');
        out.write(encodeJSONString(value));
        out.write('"');
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON number value.
     * NOTE: no name is written - call from within an array structure. 
     */
    public JSONWriter writeValue(int value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write(Integer.toString(value));
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON number value.
     * NOTE: no name is written - call from within an array structure. 
     */
    public JSONWriter writeValue(long value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write(Long.toString(value));
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON number value.
     * NOTE: no name is written - call from within an array structure. 
     */
    public JSONWriter writeValue(float value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write((Float.isInfinite(value) || Float.isNaN(value)) ? "0" : Float.toString(value));
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON number value.
     * NOTE: no name is written - call from within an array structure. 
     */
    public JSONWriter writeValue(double value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write((Double.isInfinite(value) || Double.isNaN(value)) ? "0" : Double.toString(value));
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON boolean value.
     * NOTE: no name is written - call from within an array structure.
     */
    public JSONWriter writeValue(boolean value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write(Boolean.toString(value));
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON null value.
     * NOTE: no name is written - call from within an array structure.
     */
    public JSONWriter writeNullValue() throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write("null");
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON null value.
     */
    public JSONWriter writeNullValue(String name) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write('"');
        out.write(name);
        out.write("\": null");
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }

    /**
     * Output a JSON boolean value.
     * NOTE: no name is written - call from within an array structure.
     */
    public JSONWriter writeRawValue(RawValue value) throws IOException
    {
        if (stack.peek() == true) out.write(", ");
        out.write(value.toJSONString());
        stack.pop();
        stack.push(Boolean.TRUE);
        return this;
    }
    
    /**
     * Encode a simple Java object structure to JSON text.
     * <p>
     * Handles standard Java data types such as String, Boolean, Integer, Float, Double, null.
     * Also deals with simple List as JSON Array and Map as JSON Object. Recursively processes
     * lists and maps as needed.
     * 
     * @param obj    Java object of basic data types or List or Map.
     * 
     * @return JSON string.
     */
    public static String encodeToJSON(final Object obj)
    {
        try
        {
            final Writer out = new StringBuilderWriter(1024);
            encodeToJSON(obj, new JSONWriter(out));
            return out.toString();
        }
        catch (IOException ioe)
        {
            return "";
        }
    }
    
    /**
     * Encode a simple Java object structure to JSON text.
     * <p>
     * Handles standard Java data types such as String, Boolean, Integer, Float, Double, null.
     * Also deals with simple List as JSON Array and Map as JSON Object. Recursively processes
     * lists and maps as needed.
     * 
     * @param obj    Java object of basic data types or List or Map.
     * @param writer JSONWriter for output
     * 
     * @return JSON string.
     */
    public static void encodeToJSON(final Object obj, final JSONWriter writer) throws IOException
    {
        if (obj instanceof Map)
        {
            writer.startObject();
            for (Object key: ((Map)obj).keySet())
            {
                writer.startValue(key.toString());
                encodeToJSON(((Map)obj).get(key), writer);
                writer.endValue();
            }
            writer.endObject();
        }
        else if (obj instanceof List)
        {
            writer.startArray();
            for (Object val: (List)obj)
            {
                encodeToJSON(val, writer);
            }
            writer.endArray();
        }
        else if (obj instanceof String)
        {
            writer.writeValue((String)obj);
        }
        else if (obj instanceof Boolean)
        {
            writer.writeValue(((Boolean)obj).booleanValue());
        }
        else if (obj instanceof Double)
        {
            writer.writeValue(((Double)obj).doubleValue());
        }
        else if (obj instanceof Float)
        {
            writer.writeValue(((Float)obj).floatValue());
        }
        else if (obj instanceof Long)
        {
            writer.writeValue(((Long)obj).longValue());
        }
        else if (obj instanceof Integer)
        {
            writer.writeValue(((Integer)obj).intValue());
        }
        else if (obj instanceof RawValue)
        {
            writer.writeRawValue((RawValue)obj);
        }
        else if (obj != null)
        {
            writer.writeValue(obj.toString());
        }
        else
        {
            writer.writeNullValue();
        }
    }


    /**
     * Safely encode a JSON string value.
     * @return encoded string, null is handled and returned as "".
     */
    public static String encodeJSONString(final String s)
    {
        if (s == null || s.length() == 0)
        {
            return "";
        }

        StringBuilder sb = null;      // create on demand
        String enc;
        char c;
        final int len = s.length();
        for (int i = 0; i < len; i++)
        {
            enc = null;
            c = s.charAt(i);
            switch (c)
            {
                case '\\':
                    enc = "\\\\";
                    break;
                case '"':
                    enc = "\\\"";
                    break;
                case '/':
                    enc = "\\/";
                    break;
                case '\b':
                    enc = "\\b";
                    break;
                case '\t':
                    enc = "\\t";
                    break;
                case '\n':
                    enc = "\\n";
                    break;
                case '\f':
                    enc = "\\f";
                    break;
                case '\r':
                    enc = "\\r";
                    break;

                default:
                    if ((int)c >= 0x80 || (int)c < 32)
                    {
                        // encode all non basic latin characters including control chars
                        // not captured in Java above, such as the Vertical Tab /v character
                        String u = "000" + Integer.toHexString((int)c);
                        enc = "\\u" + u.substring(u.length() - 4);
                    }
                    break;
            }

            if (enc != null)
            {
                if (sb == null)
                {
                    String soFar = s.substring(0, i);
                    sb = new StringBuilder(i + 8);
                    sb.append(soFar);
                }
                sb.append(enc);
            }
            else
            {
                if (sb != null)
                {
                    sb.append(c);
                }
            }
        }

        if (sb == null)
        {
            return s;
        }
        else
        {
            return sb.toString();
        }
    }
}