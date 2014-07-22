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

import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * Custom FreeMarker Template language method.
 * <p>
 * Encode a URL Path.
 * <p>
 * Usage: pathencode(String url)
 */
@ScriptClass 
(
        help="Encode a URL Path.\n\nUsage: pathencode(String url)",
        code="<link rel=\"self\" href=\"${absurl(encodeuri(url.full))?xml}\"/>",
        types=
        {
                ScriptClassType.TemplateRootObject
        }
)
public class UrlEncodeMethod implements TemplateMethodModelEx
{
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
                result = URLEncoder.encodeUri(((TemplateScalarModel)arg0).getAsString());
            }
        }
        
        return result;
    }
}
