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
package org.springframework.extensions.surf.extensibility.impl;

import java.io.IOException;

import org.springframework.extensions.surf.extensibility.CloseModelElement;
import org.springframework.extensions.surf.extensibility.ContentModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.OpenModelElement;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

/**
 * <p>This is the default class for capturing the data contained within extensibility directives.
 * It is required because directive instances are re-used so the data stored within them cannot 
 * be relied upon when building the content model.</p>
 * 
 * @author David Draper
 *
 */
public class DefaultExtensibilityDirectiveData implements ExtensibilityDirectiveData
{
    public DefaultExtensibilityDirectiveData(String id, 
                                             String action, 
                                             String target, 
                                             String directiveName, 
                                             TemplateDirectiveBody body, 
                                             Environment env)
    {
        this.id = id;
        this.action = action;
        this.target = target;
        this.directiveName = directiveName;
        this.body = body;
        this.env = env;
    }
 
    private String id = "";
    private String action = "";
    private String target = "";
    private String directiveName = "";

    private TemplateDirectiveBody body = null;
    private Environment env = null;
    
    public Environment getEnv()
    {
        return env;
    }

    public String getId()
    {
        return id;
    }
    
    public String getAction()
    {
        return action;
    }
    
    public String getTarget()
    {
        return target;
    }

    public String getDirectiveName()
    {
        return directiveName;
    }
    
    public TemplateDirectiveBody getBody()
    {
        return this.body;
    }
    
    public OpenModelElement createOpen()
    {
        return new OpenModelElementImpl(this.id, this.directiveName);
    }

    public ContentModelElement createContentModelElement()
    {
        return new DefaultContentModelElement(this.id, directiveName);
    }

    public CloseModelElement createClose()
    {
        return new CloseModelElementImpl(this.id, directiveName);
    }

    @Override
    public String toString()
    {        
        return "ID: " + this.id + ", ACTION:" + this.action;
    }

    public void render(ModelWriter writer) throws TemplateException, IOException
    {
        if (this.body != null)
        {
            this.body.render(writer);
        }
    }
}
