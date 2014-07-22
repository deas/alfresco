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

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * Custom FreeMarker Template language method.
 * <p>
 * Render resource url independent of script hosting environment e.g. render inside / outside
 * a portal or within a Surf app.
 * <p>
 * Usage: resourceurl(String url)
 * 
 * @author davidc
 * @author muzquiano
 */
public final class ResourceUrlMethod implements TemplateMethodModelEx
{
    WebScriptRequest req;
    WebScriptResponse res;
    
    /**
     * Construct
     * 
     * @param basePath  base path used to construct absolute url
     */
    public ResourceUrlMethod(WebScriptRequest req, WebScriptResponse res)
    {
        this.req = req;
        this.res = res;
    }
    
    
    /**
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    public Object exec(List args) throws TemplateModelException
    {
        String result = "";
        
        if (args.size() != 0)
        {
            Object arg0 = args.get(0);
            boolean prefixContextPath = true;
            if (args.size() == 2 && args.get(1) instanceof TemplateBooleanModel)
            {
                prefixContextPath = ((TemplateBooleanModel)args.get(1)).getAsBoolean();
            }
            
            if (arg0 instanceof TemplateScalarModel)
            {
                String arg = ((TemplateScalarModel)arg0).getAsString();
                StringBuffer buf = new StringBuffer(128);
                
                // optionally prefix with the context path
                if (prefixContextPath)
                {
                    buf.append(req.getContextPath());
                }
                
                if (!arg.startsWith("/") && !req.getContextPath().endsWith("/"))
                {
	                arg = "/" + arg;
                }
                
                buf.append(res.encodeResourceUrl(arg));
                                    
                result = buf.toString();
            }
        }
        
        return result;
    }
}
