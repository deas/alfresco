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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

/**
 * Custom FreeMarker Template language method.
 * <p>
 * Given a URL string and N named/value pairs, replace each URL argument with 
 * respective name/value pair (if name matches existing URL argument), or add 
 * name/value pair to URL (if name does not match existing URL argument).
 * <p>
 * Usage: argreplace(url.args, "skipCount", cursor.nextPage, ...)
 *
 * Example: argreplace("?a=0&b=2", "a", 1, "c", 3) returns "?a=1&b=2&c=3"
 * 
 * @author davidc
 */
@ScriptClass 
(
        help="Given a URL string and N named/value pairs, replace each URL argument with \nrespective name/value pair (if name matches existing URL argument), or add \nname/value pair to URL (if name does not match existing URL argument).\n\nUsage: argreplace(url.args, \"skipCount\", cursor.nextPage, ...)",
        code="[#if cursor.hasFirstPage]\n<link rel=\"first\" href=\"${absurl(encodeuri(scripturl(argreplace(url.args, pageNo,\ncursor.firstPage, pageSize, cursor.pageSize))))?xml}\" type=\"${format.type}\"/>\n[/#if]",
        types=
        {
                ScriptClassType.TemplateRootObject
        }
)
public final class ArgReplaceMethod implements TemplateMethodModelEx
{
    
    /**
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    public Object exec(List args) throws TemplateModelException
    {
        if (args.size() == 0)
        {
            return "";
        }

        String urlArgs = "";
        Object arg0 = args.get(0);
        if (arg0 instanceof TemplateScalarModel)
        {
            urlArgs = ((TemplateScalarModel)arg0).getAsString();
        }

        if (args.size() == 1)
        {
            return urlArgs;
        }
        
        Map<String, String> replacements = new HashMap<String, String>();
        int i = 1;
        while(i < args.size())
        {
            String name = null;
            String val = null;
            Object argname = args.get(i);
            if (argname instanceof TemplateScalarModel)
            {
                name = ((TemplateScalarModel)argname).getAsString();
                i++;
                if (i < args.size())
                {
                    Object argval = args.get(i);
                    if (argval instanceof TemplateScalarModel)
                    {
                        val = ((TemplateScalarModel)argval).getAsString();
                    }
                    else if (argval instanceof TemplateNumberModel)
                    {
                        val = ((TemplateNumberModel)argval).getAsNumber().toString();
                    }
                    else if (argval instanceof TemplateBooleanModel)
                    {
                        val = Boolean.toString(((TemplateBooleanModel)argval).getAsBoolean());
                    }

                    if (val != null)
                    {
                        replacements.put(name, val);
                    }
                }
            }
            i++;
        }

        if (replacements.size() == 0)
        {
            return urlArgs;
        }
        
        StringBuilder newUrlArgs = new StringBuilder();
        if (urlArgs.length() > 0)
        {
            String[] argPairs = urlArgs.split("&");
            int n = 0;
            for (String argPair : argPairs)
            {
                String[] nameVal = argPair.split("=");
                String name = nameVal[0];
                String val = (nameVal.length > 1) ? nameVal[1] : null;
                String replaceVal = replacements.get(name);
                if (replaceVal != null)
                {
                    val = replaceVal; 
                }
                newUrlArgs.append(name);
                if (val != null)
                {
                    newUrlArgs.append("=");
                    newUrlArgs.append(val);
                    replacements.remove(name);
                }
                n++;
                if (n < argPairs.length || replacements.size() > 0)
                {
                    newUrlArgs.append("&");
                }
            }
        }

        int rs = replacements.entrySet().size();
        int r = 0;
        for (Map.Entry<String, String> replacement : replacements.entrySet())
        {
            newUrlArgs.append(replacement.getKey());
            if (replacement.getValue() != null)
            {
                newUrlArgs.append("=");
                newUrlArgs.append(replacement.getValue());
                r++;
                if (r < rs)
                {
                    newUrlArgs.append("&");
                }
            }
        }
        
        return newUrlArgs;    
    }
}
