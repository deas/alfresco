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

import org.springframework.extensions.webscripts.Description.FormatStyle;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * Custom FreeMarker Template language method.
 * <p>
 * Render script url independent of script hosting environment e.g. render inside / outside
 * portal.
 * <p>
 * Usage: scripturl(String url)
 * 
 * @author davidc
 */
@ScriptClass 
(
        help="Render script url independent of script hosting environment e.g. render inside / outside\n\nUsage: scripturl(String url)",
        code="${scripturl(\"?nodeRef=\" + n.parent.nodeRef + \"&n=\" + n.nodeRef + \"&a=p\")}",
        types=
        {
                ScriptClassType.TemplateAPI
        }
)
public final class ScriptUrlMethod implements TemplateMethodModelEx
{
    WebScriptRequest req;
    WebScriptResponse res;
    
    /**
     * Construct
     * 
     * @param basePath  base path used to construct absolute url
     */
    public ScriptUrlMethod(WebScriptRequest req, WebScriptResponse res)
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
            boolean prefixServiceUrl = true;
            if (args.size() == 2 && args.get(1) instanceof TemplateBooleanModel)
            {
               prefixServiceUrl = ((TemplateBooleanModel)args.get(1)).getAsBoolean();
            }
            
            if (arg0 instanceof TemplateScalarModel)
            {
                String arg = ((TemplateScalarModel)arg0).getAsString();
                StringBuffer buf = new StringBuffer(128);
                
                if (prefixServiceUrl)
                {
                    buf.append(req.getServicePath());
                    if (arg.length() > 0)
                    {
                        if (arg.indexOf('?') == -1)
                        {
                            buf.append('?');
                        }
                        buf.append(arg);
                    }
                }
                else
                {
                    buf.append(arg);
                }
                    
                if (buf.indexOf("?guest") == -1 && buf.indexOf("&guest") == -1)
                {
                    buf.append(buf.indexOf("?") == -1 ? '?' : '&');
                    buf.append("guest=" + (req.isGuest() ? "true" : ""));
                }
                if (req.getFormatStyle() == FormatStyle.argument)
                {
                    if (buf.indexOf("?format") == -1 && buf.indexOf("&format") == -1)
                    {
                        buf.append(buf.indexOf("?") == -1 ? '?' : '&');
                        buf.append("format=" + req.getFormat());
                    }
                }
                
                result = res.encodeScriptUrl(buf.toString());
            }
        }
        
        return result;
    }
}
