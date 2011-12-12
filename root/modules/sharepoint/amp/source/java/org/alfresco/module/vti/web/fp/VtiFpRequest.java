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

package org.alfresco.module.vti.web.fp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.alfresco.module.vti.metadata.dic.VtiSort;
import org.alfresco.module.vti.metadata.dic.VtiSortField;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.alfresco.module.vti.metadata.model.Document;
import org.alfresco.module.vti.web.VtiRequestDispatcher;
import org.springframework.extensions.surf.util.URLDecoder;


/**
 * VtiFpRequest is wrapper for HttpServletRequest. It provides specific methods 
 * which allow to retrieve appropriate parameters from request data
 * for Frontpage extension protocol. 
 * 
 * @author Michael Shavnev
 *
 */
public class VtiFpRequest extends HttpServletRequestWrapper
{
    // Syntax Delimiters
    protected static final String OBRACKET = "[";
    protected static final String CBRACKET = "]";
    protected static final String LISTSEP = ";";
    protected static final String COMMASEP = ",";

    // METADICT-CONSTRAINT-CHAR
    protected static final String METADICT_CONSTRAINT_IGNORE = "X"; // ignore
    protected static final String METADICT_CONSTRAINT_RO = "R"; // read only
    protected static final String METADICT_CONSTRAINT_RW = "W"; // read/write

    // METADICT-VALUE
    protected static final String METADICT_VALUE_TIME = "T"; // TIME
    protected static final String METADICT_VALUE_STRING_VECTOR = "V"; // METADICT-STRING-VECTOR
    protected static final String METADICT_VALUE_BOOLEAN = "B"; // BOOLEAN
    protected static final String METADICT_VALUE_INT_VECTOR = "U"; // METADICT-INT-VECTOR
    protected static final String METADICT_VALUE_DOUBLE = "D"; // DOUBLE
    protected static final String METADICT_VALUE_STRING = "S"; // STRING

    private Map<String, String[]> supplementParamMap;

    private String alfrescoContextName = null;

    /**
     * Constructor
     * 
     * @param request HttpServletRequest 
     */
    public VtiFpRequest(HttpServletRequest request)
    {
        super(ensureUTF8(request));
        supplementParamMap = new HashMap<String, String[]>();
        alfrescoContextName = (String) request.getAttribute(VtiRequestDispatcher.VTI_ALFRESCO_CONTEXT);
    }
    
    /**
     * Typically, Office neglects to include the encoding information
     *  in the requests, and assumes it's UTF-8.
     * So that everything works correctly, set this encoding if
     *  it isn't there 
     */
    private static HttpServletRequest ensureUTF8(HttpServletRequest request)
    {
       if (request.getCharacterEncoding() == null)
       {
          try
          {
             request.setCharacterEncoding("UTF-8");
          } catch(UnsupportedEncodingException e) {} // UTF-8 always supported
       }
       return request;
    }

    /**
     * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
     */
    public String getParameter(String name)
    {
        String param = null;
        String[] params = getParameterValues(name);
        if (params != null && params.length > 0)
        {
            param = checkForLineFeed(params[0]);
        }

        return param;
    }
    
    /**
     * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
     */
    public String getNotEncodedParameter(String name)
    {
        String param = null;
        String[] params = getParameterValues(name);
        if (params != null && params.length > 0)
        {
            param = checkForLineFeed(params[0]);
        }
        
        return param;
    }

    /**
     * @see javax.servlet.ServletRequestWrapper#getParameterValues(java.lang.String)
     */
    public String[] getParameterValues(String name)
    {
        String[] params = null;
        if (supplementParamMap.containsKey(name))
        {
            params = supplementParamMap.get(name);
        }
        else
        {
            params = super.getParameterValues(name);
        }
        return params;
    }

    /**
     * Set additional specific request parameters
     * 
     * @param name parameter name
     * @param values parameter values 
     */
    public void setParameterValues(String name, String[] values)
    {
        supplementParamMap.put(name, values);
    }

    /**
     * Set additional specific request parameter
     * 
     * @param name parameter name
     * @param value parameter value 
     */
    public void setParameter(String name, String value)
    {
        setParameterValues(name, new String[] { value });
    }

    /**
     * @see javax.servlet.ServletRequestWrapper#getParameterMap()
     */
    @SuppressWarnings("unchecked")
    public Map<String, String[]> getParameterMap()
    {
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.putAll(super.getParameterMap());
        paramMap.putAll(supplementParamMap);
        return paramMap;
    }
    
    
    /**
     * @see javax.servlet.ServletRequestWrapper#getParameterNames()
     */
    @SuppressWarnings("unchecked")
    public Enumeration<String> getParameterNames()
    {
        Set<String> paramNameSet = new HashSet<String>();
        for (Enumeration<String> enumeration = super.getParameterNames(); enumeration.hasMoreElements();)
        {
            paramNameSet.add(enumeration.nextElement());
        }
        paramNameSet.addAll(supplementParamMap.keySet());
        return new IteratorEnumeration<String>(paramNameSet.iterator());
    }

    private static class IteratorEnumeration<E> implements Enumeration<E>
    {

        private Iterator<E> iterator;

        public IteratorEnumeration(Iterator<E> iterator)
        {
            this.iterator = iterator;
        }

        public boolean hasMoreElements()
        {
            return iterator.hasNext();
        }

        public E nextElement()
        {
            return iterator.next();
        }
    };

    // ----------------------------------------------------------------------------------------------

    /**
     * Get boolean parameter
     * 
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return boolean parameter from request or defaultValue if not present
     */
    public boolean getParameter(String paramName, boolean defaultValue)
    {
        boolean value = defaultValue;
        String stringValue = getParameter(paramName);
        stringValue = checkForLineFeed(stringValue);
        if (stringValue != null)
        {
            if ("true".equalsIgnoreCase(stringValue))
            {
                value = true;
            }
            else
            {
                value = false;
            }
        }
        return value;
    }

    /**
     * Get Date parameter
     *  
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return Date parameter from request or defaultValue if not present
     */
    public Date getParameter(String paramName, Date defaultValue)
    {
        Date value = defaultValue;
        String stringValue = getParameter(paramName);
        stringValue = checkForLineFeed(stringValue);
        if (stringValue != null)
        {
            try
            {
                value = DateFormat.getDateInstance().parse(stringValue);
            }
            catch (ParseException e)
            {
                // ignore
            }
        }
        return value;
    }

    /**
     * Get VtiSort parameter
     *  
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return VtiSort parameter from request or defaultValue if not present
     */
    public VtiSort getParameter(String paramName, VtiSort defaultValue)
    {
        String value = getParameter(paramName);
        if (value == null)
        {
            return defaultValue;
        }
        return VtiSort.value(checkForLineFeed(value));
    }

    /**
     * Get VtiSortField parameter
     * 
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return VtiSortField parameter from request or defaultValue if not present
     */
    public VtiSortField getParameter(String paramName, VtiSortField defaultValue)
    {
        String value = getParameter(paramName);
        if (value == null)
        {
            return defaultValue;
        }
        return VtiSortField.value(checkForLineFeed(value));
    }

    /**
     * Get VtiSortField parameter
     * 
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return int parameter from request or defaultValue if not present
     */
    public int getParameter(String paramName, int defaultValue)
    {
        int value = defaultValue;
        String stringValue = getParameter(paramName);
        stringValue = checkForLineFeed(stringValue);
        if (stringValue != null && !stringValue.equals(""))
        {
            value = Integer.valueOf(stringValue);
        }
        return value;
    }

    /**
     * Get String parameter
     * 
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return String parameter from request or defaultValue if not present
     */
    public String getParameter(String paramName, String defaultValue) throws UnsupportedEncodingException
    {
        String value = getParameter(paramName);
        value = checkForLineFeed(value);
        if (value == null)
        {
            value = defaultValue;
        }
        
        try
        {
            value = URLDecoder.decode(value);
        }
        catch (Exception e)
        {
        }
        return checkForIllegalSlashes(value);
    }

    /**
     * Get List of Strings parameter
     * 
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return List<String> parameter from request or defaultValue if not present
     */
    public List<String> getParameter(String paramName, List<String> defaultValue)
    {
        String vectorString = getParameter(paramName);
        vectorString = checkForLineFeed(vectorString);
        List<String> vector = null;

        if (vectorString != null)
        {
            if (vectorString.indexOf(OBRACKET) == 0 && vectorString.lastIndexOf(CBRACKET) == (vectorString.length() - 1))
            {
                vectorString = vectorString.substring(1, vectorString.length() - 1);
                String[] urls = split(vectorString, LISTSEP);
                vector = new ArrayList<String>();
                for (String url : urls)
                {
                    vector.add(checkForIllegalSlashes(url));
                }
                return vector;
            }
        }
        return defaultValue;
    }

    /**
     * Get DocsMetaInfo parameter 
     * 
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return DocsMetaInfo parameter from request or defaultValue if not present
     */
    public DocsMetaInfo getParameter(String paramName, DocsMetaInfo defaultValue)
    {
        String mapString = checkForLineFeed(getParameter(paramName));

        DocsMetaInfo result = new DocsMetaInfo();

        if (mapString.indexOf(OBRACKET) == 0 && mapString.lastIndexOf(CBRACKET) == (mapString.length() - 1))
        {
            mapString = mapString.substring(1, mapString.length() - 1);
            String[] urls = split(mapString, "\\]\\]");
            for (String url : urls)
            {
                DocMetaInfo folder = new DocMetaInfo(true);
                String folder_name = url.substring(0, url.lastIndexOf(";meta_info")).substring(url.indexOf("=") + 1);
                folder.setPath(checkForIllegalSlashes(folder_name));
                String meta_info = url.substring(url.lastIndexOf(";meta_info") + 1);
                meta_info = meta_info.substring(meta_info.indexOf("[") + 1);
                if (!meta_info.equals(""))
                {
                    Map<String, String> properties = new HashMap<String, String>();
                    String[] meta_keys = split(meta_info, LISTSEP);
                    for (int i = 0; i < meta_keys.length; i += 2)
                    {
                        properties.put(meta_keys[i], meta_keys[i + 1].substring(meta_keys[i + 1].indexOf("|") + 1));
                    }
                    folder.setDocInfoProperties(properties);
                }
                result.getFolderMetaInfoList().add(folder);
            }
            return result;
        }
        return defaultValue;
    }

    /**
     * Get Document parameter 
     * 
     * @param paramName name of parameter
     * @param defaultValue default value for parameter if not present
     * @return Document parameter from request or defaultValue if not present
     */
    public Document getParameter(String paramName, Document defaultValue) throws IOException
    {
        String mapString = checkForLineFeed(getNotEncodedParameter(paramName));

        Document result = new Document();
        if (mapString != null && mapString.length() > 0)
        {
            if (mapString.indexOf(OBRACKET) == 0 && mapString.lastIndexOf(CBRACKET) == (mapString.length() - 1))
            {
                result.setInputStream(this.getInputStream());
                mapString = mapString.substring(1, mapString.length() - 1);
                String document_name = mapString.substring(0, mapString.lastIndexOf(";meta_info")).substring(mapString.indexOf("=") + 1);
                result.setPath(checkForIllegalSlashes(document_name));
                String meta_info = mapString.substring(mapString.lastIndexOf(";meta_info") + 1);
                meta_info = meta_info.substring(meta_info.indexOf("[") + 1, meta_info.indexOf("]"));
                if (!meta_info.equals(""))
                {
                    Map<String, String> properties = new HashMap<String, String>();
                    String[] meta_keys = split(meta_info, LISTSEP);
                    for (int i = 0; i < meta_keys.length; i += 2)
                    {
                        properties.put(meta_keys[i], meta_keys[i + 1].substring(meta_keys[i + 1].indexOf("|") + 1));
                    }
                    result.setDocInfoProperties(properties);
                }
                return result;
            }
        }
        return defaultValue;
    }

    /**
     * Get dictionary
     * 
     * @param paramName name of parameter
     * @return Map<String, String> parameter from request
     */
    public Map<String, String> getDictionary(String paramName)
    {
        String dictionaryString = getParameter(paramName);
        Map<String, String> dictionary = new HashMap<String, String>();

        if (dictionaryString != null)
        {
            dictionary = new HashMap<String, String>();
            if (dictionaryString.indexOf(OBRACKET) == 0 && dictionaryString.lastIndexOf(CBRACKET) == (dictionaryString.length() - 1))
            {
                StringTokenizer tokenizer = new StringTokenizer(dictionaryString, LISTSEP);
                while (true)
                {
                    String key, value;
                    if (tokenizer.hasMoreTokens())
                    {
                        key = tokenizer.nextToken();
                        if (tokenizer.hasMoreTokens())
                        {
                            value = tokenizer.nextToken();
                            dictionary.put(key, checkForIllegalSlashes(value));
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }

        return dictionary;
    }

    /**
     * Get meta-dictionary
     * 
     * @param paramName name of parameter
     * @return Map<String, Object> parameter from request
     */
    public Map<String, Object> getMetaDictionary(String paramName)
    {
        String metaDictionaryString = getParameter(paramName);
        Map<String, Object> metaDictionary = new HashMap<String, Object>();

        if (metaDictionaryString != null)
        {
            metaDictionary = new HashMap<String, Object>();
            if (metaDictionaryString.indexOf(OBRACKET) == 0 && metaDictionaryString.lastIndexOf(CBRACKET) == (metaDictionaryString.length() - 1))
            {
                StringTokenizer tokenizer = new StringTokenizer(metaDictionaryString, LISTSEP);
                while (true)
                {
                    String key, valueWithMetaDataString;
                    if (tokenizer.hasMoreTokens())
                    {
                        key = tokenizer.nextToken();
                        if (tokenizer.hasMoreTokens())
                        {
                            valueWithMetaDataString = tokenizer.nextToken();

                            // METADICT-VALUE = constraint type "|" value
                            if (valueWithMetaDataString.length() >= 3 && valueWithMetaDataString.substring(1, 2).equals(METADICT_CONSTRAINT_IGNORE) == false)
                            {
                                String typeString = valueWithMetaDataString.substring(0, 1);
                                String valueString = valueWithMetaDataString.substring(3);

                                if (typeString.equals(METADICT_VALUE_TIME))
                                {
                                    try
                                    {
                                        metaDictionary.put(key, DateFormat.getDateInstance().parse(valueString));
                                    }
                                    catch (ParseException e)
                                    {
                                        // ignore
                                    }
                                }
                                else if (typeString.equals(METADICT_VALUE_STRING))
                                {
                                    metaDictionary.put(key, checkForIllegalSlashes(valueString));
                                }
                                else if (typeString.equals(METADICT_VALUE_DOUBLE))
                                {
                                    try
                                    {
                                        metaDictionary.put(key, new Double(Double.parseDouble(valueString)));
                                    }
                                    catch (NumberFormatException e)
                                    {
                                        // ignore
                                    }
                                }
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

            }
        }

        return metaDictionary;
    }

    private String checkForLineFeed(String value)
    {
        if (value != null && value.endsWith("\n"))
        {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
    
    private String checkForIllegalSlashes(String value)
    {
        if (value != null)
        {
            value =  value.replace("\\", "");
        }
        
        return value;
    }
    
    private static String[] split(String value, String separator)
    {            
        String[] parts = value.split(separator);
        List<Integer> indexes = new ArrayList<Integer>();        
        
        for (int i = 0; i < parts.length; i++)
        {            
            
            if (parts[i].endsWith("\\") && i < parts.length - 1)
            {
                parts[i+1] = parts[i] + separator + parts[i+1];
                
            }
            else
            {
                indexes.add(new Integer(i));
            }
            
        }  
        
        String[] result = new String[indexes.size()];
        int pos = 0;
        Iterator<Integer> it = indexes.iterator();
       
        while (it.hasNext())
        {
            result[pos] = parts[it.next().intValue()];  
            pos++;
        }
        return result;
    }

    /**
     * @return context name
     */
    public String getAlfrescoContextName()
    {
        return alfrescoContextName;
    }
}
