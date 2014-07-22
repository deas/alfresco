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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing a Uri Template - with basic {token} format support.
 * See JAX-RS JSR-311.
 * 
 * @author davidc
 */
public class UriTemplate
{
    private static final Pattern VALID_URI = Pattern.compile("^/(([\\w\\-]+|\\{([a-zA-Z][\\w]*)\\})(;*)/?)+(\\.\\w+$)?|^/$");
    private static final Pattern VARIABLE = Pattern.compile("\\{([a-zA-Z]\\w*)\\}");
    private static final String VARIABLE_REGEX = "(.*?)";

    private String template;
    private Pattern regex;
    private String[] vars;
    private int charCnt;

    /**
     * Construct
     * 
     * @param template
     */
    public UriTemplate(String template)
    {
        // ensure template is provided
        if (template == null || template.length() == 0)
        {
            throw new WebScriptException("URI Template not provided");
        }

        // ensure template is syntactically correct
        Matcher validMatcher = VALID_URI.matcher(template);
        if (!validMatcher.matches())
        {
            throw new WebScriptException("URI Template malformed: " + template);
        }

        // convert uri template into equivalent regular expression
        // and extract variable names
        StringBuilder templateRegex = new StringBuilder();
        List<String> names = new ArrayList<String>();
        int charCnt = 0;
        int start = 0;
        int end = 0;
        Matcher matcher = VARIABLE.matcher(template);
        while(matcher.find())
        {
            end = matcher.start();
            charCnt += appendTemplate(template, start, end, templateRegex);
            templateRegex.append(VARIABLE_REGEX);
            String name = matcher.group(1);
            names.add(name);
            start = matcher.end();
        }
        charCnt += appendTemplate(template, start, template.length(), templateRegex);

        // initialise
        this.template = template;
        this.charCnt = charCnt;
        this.regex = Pattern.compile(templateRegex.toString());
        this.vars = new String[names.size()];
        names.toArray(this.vars);
    }

    /**
     * Helper for constructing regular expression (escaping regex chars where necessary)
     * 
     * @param template
     * @param start
     * @param end
     * @param regex
     * @return
     */
    private int appendTemplate(String template, int start, int end, StringBuilder regex)
    {
        for (int i = start; i < end; i++)
        {
            char c = template.charAt(i);
            if ("(.?)".indexOf(c) != -1)
            {
                regex.append("\\");
            }
            regex.append(c);
        }
        return end - start;
    }

    /**
     * Determine if uri is matched by this uri template and return a map of variable
     * values if it does.
     * 
     * @param uri  uri to match
     * @return  map of variable values (or null, if no match, or empty if no vars)
     */
    public Map<String, String> match(String uri)
    {
        Map<String, String> values = null;

        if (uri != null && uri.length() != 0)
        {
            Matcher m = regex.matcher(uri);
            if (m.matches())
            {
                values = new HashMap<String, String>(m.groupCount(), 1.0f);
                for (int i = 0; i < m.groupCount(); i++)
                {
                    String name = vars[i];
                    String value = m.group(i + 1);
                    
                    /**
                     * To support the case where multiple tokens of the same name may appear in the url
                     * there's only a match if the value provided for each instance of the token is the same
                     * e.g.  /{a}/xyx/{a}  only matches  /fred/xyx/fred  not  /fred/xyx/bob
                     */
                    String existingValue = values.get(name);
                    if (existingValue != null && !existingValue.equals(value))
                    {
                        return null;
                    }
                    
                    values.put(vars[i], value);
                }
            }
        }

        return values;
    }

    /**
     * @return  get template
     */
    public String getTemplate()
    {
        return template;
    }

    /**
     * @return  get regular expression equivalent
     */
    public Pattern getRegex()
    {
        return regex;
    }

    /**
     * @return  get variable names contained in uri template
     */
    public String[] getVariableNames()
    {
        return vars;
    }

    /**
     * @return  get number of static characters in uri template
     */
    public int getStaticCharCount()
    {
        return charCnt;
    }

    @Override
    public final String toString()
    {
        String strVars = "";
        for (int i = 0; i < vars.length; i++)
        {
            strVars += vars[i];
            if (i < vars.length -1)
            {
                strVars += ",";
            }
        }
        return regex.toString() + " (vars=[" + strVars + "])"; 
    }

    @Override
    public final int hashCode()
    {
        return regex.hashCode();
    }

    @Override
    public final boolean equals(Object obj)
    {
        if (!(obj instanceof UriTemplate))
        {
            return false;
        }
        return regex.equals(((UriTemplate)obj).regex);
    }
}
