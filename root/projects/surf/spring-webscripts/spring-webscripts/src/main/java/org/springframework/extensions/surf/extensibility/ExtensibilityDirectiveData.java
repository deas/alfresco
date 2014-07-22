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
package org.springframework.extensions.surf.extensibility;

import java.io.IOException;

import org.springframework.extensions.surf.extensibility.impl.DefaultExtensibilityContent;
import org.springframework.extensions.surf.extensibility.impl.ModelWriter;
import org.springframework.extensions.surf.extensibility.impl.OpenModelElementImpl;

import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

/**
 * <p>Represents a single invocation of an {@link ExtensibilityDirective}. This class is necessary since {@link ExtensibilityDirective}
 * can be used multiple times within a single request and their instance variables are volatile. As well as providing methods for the 
 * instantiation of the {@link OpenModelElement}, {@link ContentModelElement} and {@link CloseModelElement} implementations associated with
 * the {@link ExtensibilityDirective} this interface ensures that all information required for processing the directive in the model
 * will not be lost when the next invocation of the directive type occurs.</p> 
 * 
 * @author David Draper
 */
public interface ExtensibilityDirectiveData
{
    /**
     * <p>Returns the id of FreeMarker directive represented by implementing instance.</p> 
     * @return
     */
    public String getId();
    
    /**
     * <p>Gets the action being performed by the invocation of the associated {@link ExtensibilityDirective} type. The default
     * set of actions include "merge", "remove", "replace", "before" and "after".</p>
     * 
     * @return A String representing the action being performed by the invocation of the associated {@link ExtensibilityDirective} type.
     */
    public String getAction();
     
    /**
     * <p>Gets the id of the {@link ExtensibilityDirective} that the invocation of the associated {@link ExtensibilityDirective} type
     * is targeting. This will only be expected to return a non-null value when the {@link ExtensibilityDirective} is being processed as
     * part of an extension. Any action that has an effect on the existing model (such as "remove" or "replace") will always require
     * target information.</p>
     * 
     * @return
     */
    public String getTarget();
    
    /**
     * <p>Returns the name of the associated {@link ExtensibilityDirective} type.</p>
     * @return
     */
    public String getDirectiveName();
    
    /**
     * <p>Returns the {@link TemplateDirectiveBody} of the invocation of the associated {@link ExtensibilityDirective} type.</p>
     * 
     * @return A {@link TemplateDirectiveBody} associated with a single invocation of the associated {@link ExtensibilityDirective}.
     */
    public TemplateDirectiveBody getBody();
    
    /**
     * <p>Creates a new instance of the type of {@link OpenModelElement} for with the associated {@link ExtensibilityDirective}. In almost
     * all cases this would be expected to be an instance of {@link OpenModelElementImpl}.</p>
     * 
     * @return
     */
    public OpenModelElement createOpen();

    /**
     * <p>Creates a new instance of the type of {@link ContentModelElement} for with the associated {@link ExtensibilityDirective}. The
     * implementation returned could be varied depending upon the function of the {@link ExtensibilityDirective}. Where standard rendering
     * of the associated {@link TemplateDirectiveBody} will occur, a {@link DefaultExtensibilityContent} will most likely be returned.</p>
     * 
     * @return
     */
    public ContentModelElement createContentModelElement();
    
    /**
     * <p>Creates a new instance of the type of {@link CloseModelElement} for with the associated {@link ExtensibilityDirective}. In almost
     * all cases this would be expected to be an instance of {@link CloseModelElementImpl}.</p>
     * 
     * @return
     */
    public CloseModelElement createClose();
    
    /**
     * <p>Renders the output provided by the directive. In most cases this will probably call the <code>render</code>
     * method of the associated {@link TemplateDirectiveBody}. However, it is possible that rendering may possible by 
     * directive with an empty body.</p> 
     *  
     * @param writer {@link ModelWriter} to send the rendered output to.
     * @throws TemplateException
     * @throws IOException
     */
    public void render(ModelWriter writer) throws TemplateException, IOException;
}
