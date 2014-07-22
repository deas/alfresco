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

import org.springframework.extensions.surf.util.ISO8601DateFormat;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * NOTE: sourced from org.alfresco.repo.template.ISO8601DateFormatMethod.
 * 
 * @author David Caruana
 * @author Kevin Roast
 * 
 * Custom FreeMarker Template language method.
 * <p>
 * Render Date to ISO8601 format.<br>
 * Or parse ISO6801 format string date to a Date object.
 * <p>
 * Usage: xmldate(Date date)
 *        xmldate(String date)
 */
@ScriptClass 
(
        help="Render Date to ISO8601 format. Or parse ISO6801 format string date to a Date object.\n\nUsage: xmldate(Date date)\n        xmldate(String date)",
        code="<updated>${xmldate(date)}</updated>",
        types=
        {
                ScriptClassType.TemplateRootObject
        }
)
public class ISO8601DateFormatMethod implements TemplateMethodModelEx
{
    /**
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    public Object exec(List args) throws TemplateModelException
    {
        Object result = null;
        
        if (args.size() == 1)
        {
            Object arg0 = args.get(0);
            if (arg0 instanceof TemplateDateModel)
            {
                result = ISO8601DateFormat.format(((TemplateDateModel)arg0).getAsDate());
            }
            else if (arg0 instanceof TemplateScalarModel)
            {
                result = ISO8601DateFormat.parse(((TemplateScalarModel)arg0).getAsString());
            }
        }
        
        return result != null ? result : "";
    }
}