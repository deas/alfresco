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

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;


/**
 * Custom FreeMarker Template language method.
 * <p>
 * Render object to mimetype of web script template.  If object cannot be serialized to
 * mimetype then no output is written.
 * <p>
 * Usage: formatwrite(object)
 * 
 * Where:
 *        object => object to write
 * 
 * @author davidc
 */
@ScriptClass 
(
        help="Render object to mimetype of web script template.  If object cannot be serialized to mimetype\nthen no output is written.\n\nUsage: formatwrite(object)",
        types=
        {
                ScriptClassType.TemplateAPI
        }
)
public final class FormatWriterMethod implements TemplateMethodModelEx
{
    private FormatRegistry formatRegistry;
    private String mimetype;

    /**
     * Construct
     */
    public FormatWriterMethod(FormatRegistry formatRegistry, String format)
    {
        this.formatRegistry = formatRegistry;
        this.mimetype = formatRegistry.getMimeType(null, format);
    }

    /* (non-Javadoc)
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    public Object exec(List args) throws TemplateModelException
    {
        String result = "";
        if (args.size() != 0)
        {
            // retrieve object to serialize
            Object object = null;
            Object arg0 = args.get(0);
            if (arg0 instanceof BeanModel)
            {
                object = ((BeanModel)arg0).getWrappedObject();
            }
            
            if (object != null)
            {
                FormatWriter<Object> writer = formatRegistry.getWriter(object, mimetype);
                if (writer != null)
                {
                    // NOTE: For now, streaming directly to freemarker writer i.e. not relying on
                    //       result to return serialized form
                    writer.write(object, Environment.getCurrentEnvironment().getOut());
                }
            }
        }
        return result;
    }
    
}
