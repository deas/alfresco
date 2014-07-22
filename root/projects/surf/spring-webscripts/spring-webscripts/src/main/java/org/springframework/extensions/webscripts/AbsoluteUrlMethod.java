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

import java.util.List;

import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * @author David Caruana
 * 
 * Custom FreeMarker Template language method.
 * <p>
 * Render absolute url for the specified url (only if the url isn't already absolute).
 * <p>
 * Usage: absurl(String url)
 */
@ScriptClass 
(
        help="Render absolute url for the specified url (only if the url isn't already absolute).\n\nUsage: absurl(String url)",
        code="${absurl(url.context)}",
        types=
        {
                ScriptClassType.TemplateAPI
        }
)
public final class AbsoluteUrlMethod implements TemplateMethodModelEx
{
    private String basePath;
    
    /**
     * Construct
     * 
     * @param basePath  base path used to construct absolute url
     */
    public AbsoluteUrlMethod(String basePath)
    {
        this.basePath = basePath;
    }
    
    
    /**
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    public Object exec(List args) throws TemplateModelException
    {
        String result = "";
        
        if (args.size() == 1)
        {
            Object arg0 = args.get(0);
            if (arg0 instanceof TemplateScalarModel)
            {
                result = ((TemplateScalarModel)arg0).getAsString();
                if (result.length() == 0 || result.startsWith("/"))
                {
                    result = basePath + result;
                }
            }
        }
        
        return result;
    }
}
