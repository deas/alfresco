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
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.extensions.surf.extensibility.ExtensibilityDirective;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <p>Defines the abstract behaviour of an extensibility directive by implementing the common structure. An extensibility directive
 * has the following attributes:
 * <ul>
 * <li>id: The identifier of the directive to declare or modify</li>
 * <li>action: The action to before (merge is the implicit default action)</li>
 * <li>target: The identifier of the directive that some actions use as a relative target (i.e. when adding new content)</li>
 * </ul>
 * The basic actions defined are:
 * <ul>
 * <li>merge: Merges content into the model</li>
 * <li>remove: Removes content from the model</li>
 * <li>replace: Replaces content in the model</li>
 * <li>modify: Modifies existing content in the model</li>
 * <li>before: Places new content before existing content in the model</li>
 * <li>after: Places new content after existing content in the model</li>
 * </ul>
 * The class has been structured so that concrete directive implementations can modify the default action behaviour easily.</p>
 * 
 * @author David Draper
 */
public abstract class AbstractExtensibilityDirective extends AbstractFreeMarkerDirective implements ExtensibilityDirective
{
    private static final String ID_PREFIX = "_alfExt_";
    private static final AtomicLong IDGEN = new AtomicLong();
    
    /**
     * <p>Contains the model of all directives being processed by the current WebScript FreeMarker template.</p>
     */
    private ExtensibilityModel model;

    private String id;
    
    public String getId()
    {
        return id;
    }

    public ExtensibilityModel getModel()
    {
        return model;
    }

    /**
     * Requires a DirectiveModel to contribute to.
     * 
     * @param model
     */
    public AbstractExtensibilityDirective(String directiveName, ExtensibilityModel model)
    {
        super(directiveName);
        this.model = model;
        
    }
    
    /**
     * <p>Creates and returns a <code>DefaultExtensibilityDirectiveData</code> object containing the
     * primary data needed for processing the current directive invocation. This object is required
     * when building the model because <code>TemplateDirectiveModel</code> instances are re-used so
     * the data assigned to them is volatile.</p>
     * <p>Extending classes implementing concrete directives should choose to override this method if
     * the directive being defined requires additional data or needs to instantiate different 
     * <code>ContentModelElement</code> instances in order to provide more advance content processing.</p>
     * 
     * @param id The id of the current directive invocation
     * @param action The action of the current directive invocation
     * @param target The target of the current directive invocation (this could be null)
     * @param directiveName The name of the directive
     * @param params The parameters used by the directive invocation.
     * @param body The {@link TemplateDirectiveBody} of the current directive invocation.
     * @param env The current FreeMarker template {@link Environment}
     * @return A new {@link DefaultExtensibilityDirectiveData} object containing the data supplied as 
     * parameters to the method call.
     */
    @SuppressWarnings("rawtypes")
    public ExtensibilityDirectiveData createExtensibilityDirectiveData(String id,
                                                                       String action, 
                                                                       String target, 
                                                                       Map params, 
                                                                       TemplateDirectiveBody body, 
                                                                       Environment env) throws TemplateException
    {
        return new DefaultExtensibilityDirectiveData(id, action, target, getDirectiveName(), body, env);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void execute(Environment env, 
                        Map params, 
                        TemplateModel[] loopVars, 
                        TemplateDirectiveBody body) throws TemplateException, IOException
    {
        id = getStringProperty(params, ExtensibilityDirective.DIRECTIVE_ID, false);
        if (id == null)
        {
            id = ID_PREFIX + Long.toHexString(IDGEN.getAndIncrement());
        }
        
        String action = getStringProperty(params, ExtensibilityDirective.ACTION, false);
        String target = getStringProperty(params, ExtensibilityDirective.TARGET, false);
        
        ExtensibilityDirectiveData directiveData = createExtensibilityDirectiveData(id, action, target, params, body, env);
        
        if (action == null || action.equals(ExtensibilityDirective.ACTION_MERGE))
        {
            merge(directiveData, params);
        }
        else if (action.equals(ExtensibilityDirective.ACTION_BEFORE))
        {
            before(directiveData);
        }
        else if (action.equals(ExtensibilityDirective.ACTION_AFTER))
        {
            after(directiveData);
        }
        else if (action.equals(ExtensibilityDirective.ACTION_REMOVE))
        {
            remove(directiveData);
        }
        else if (action.equals(ExtensibilityDirective.ACTION_REPLACE))
        {
            replace(directiveData);
        }
    }
    
    /**
     * <p>Merges the output of directive data into the content model.</p>
     * <p>The default implementation of this action is simply to delegate the request to the <code>ExtensibilityModel.merge()</code> 
     * method but this has been abstracted into its own method so that extending directives can easily modify the default 
     * behaviour of this action.</p>
     *   
     * @param directiveData
     * @throws TemplateException
     * @throws IOException
     */
    public void merge(ExtensibilityDirectiveData directiveData, Map<String, Object> params) throws TemplateException, IOException
    {
        this.model.merge(directiveData);
    }
    
    /**
     * <p>The default implementation of this action is simply to delegate the request to the <code>ExtensibilityModel.before()</code> 
     * method but this has been abstracted into its own method so that extending directives can easily modify the default 
     * behaviour of this action.</p>
     *   
     * @param directiveData
     * @throws TemplateException
     * @throws IOException
     */
    public void before(ExtensibilityDirectiveData directiveData) throws TemplateException, IOException
    {
        this.model.before(directiveData);
    }
    
    /**
     * <p>The default implementation of this action is simply to delegate the request to the <code>ExtensibilityModel.after()</code> 
     * method but this has been abstracted into its own method so that extending directives can easily modify the default 
     * behaviour of this action.</p>
     *   
     * @param directiveData
     * @throws TemplateException
     * @throws IOException
     */
    public void after(ExtensibilityDirectiveData directiveData) throws TemplateException, IOException
    {
        this.model.after(directiveData);
    }
    
    /**
     * <p>The default implementation of this action is simply to delegate the request to the <code>ExtensibilityModel.remove()</code> 
     * method but this has been abstracted into its own method so that extending directives can easily modify the default 
     * behaviour of this action.</p>
     *   
     * @param directiveData
     * @throws TemplateException
     * @throws IOException
     */
    public void remove(ExtensibilityDirectiveData directiveData) throws TemplateException, IOException
    {
        this.model.remove(directiveData);
    }
    
    /**
     * <p>The default implementation of this action is simply to delegate the request to the <code>ExtensibilityModel.replace()</code> 
     * method but this has been abstracted into its own method so that extending directives can easily modify the default 
     * behaviour of this action.</p>
     *   
     * @param directiveData
     * @throws TemplateException
     * @throws IOException
     */
    public void replace(ExtensibilityDirectiveData directiveData) throws TemplateException, IOException
    {
        this.model.replace(directiveData);
    }
}
