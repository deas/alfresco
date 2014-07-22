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
import java.io.Writer;
import java.util.List;

import org.springframework.extensions.surf.extensibility.impl.DiscardUnboundContentModelElementImpl;
import org.springframework.extensions.surf.extensibility.impl.ExtensibilityDebugData;
import org.springframework.extensions.surf.extensibility.impl.ModelWriter;
import org.springframework.extensions.surf.extensibility.impl.UnboundContentModelElementImpl;

import freemarker.template.TemplateException;


public interface ExtensibilityModel
{
    /**
     * <p>Indicates whether or not the has been started.</p>
     * @return
     */
    public boolean isModelStarted();

    /**
     * <p>Switches from defining the base model to processing extensions. When extension processing the "merge" action can no longer
     * be used but the "remove", "replace", "modify", "before" and "after" actions can be. 
     */
    public void switchToExtensionProcessing();
    
    /**
     * <p>Indicates whether the base model is complete and extensions are now being processed. Extension processing commences once
     * the <code>switchToExtensionProcessing</code> method has been called. When extension processing the "merge" action can no longer
     * be used but the "remove", "replace", "modify", "before" and "after" actions can be. 
     * 
     * @return
     */
    public boolean isExtensionProcessing();
    
    /**
     * <p>Gets the {@link ExtensibilityModel} enclosing this model (assuming that this model is nested within a parent).</p>
     * @return The enclosing {@link ExtensibilityModel} if the current model is nested and <code>null</code> otherwise.
     */
    public ExtensibilityModel getParentModel();
    
    /**
     * <p>Gets the {@link ModelWriter} being used by this instance.</p>
     * @return
     */
    public ModelWriter getWriter();
    
    /**
     * <p>Flushes the entire content of the model to the supplied {@link Writer}. Once this has been done it will no longer
     * be possible to update the model.</p>
     * @param out A {@link Writer} to render the model to.
     */
    public void flushModel(Writer out);
    
    /**
     * <p>Add a new {@link UnboundContentModelElementImpl} into the model so that when content is rendered it will
     * be kept but will not be available to extensions to modify. This is used when processing the content in base files
     * that is not provided by an {@link ExtensibilityDirective}. 
     */
    public void addUnboundContent();
    
    /**
     * <p>Add a new {@link DiscardUnboundContentModelElementImpl} into the model so that when content is rendered
     * it will be discarded. This is used when extension files are processed that contain "unbounded" content (that is
     * content that is not provided by an {@link ExtensibilityDirective}).</p>
     */
    public void addDiscardContent();
    
    public DeferredContentTargetModelElement getDeferredContent(String directiveId, String directiveName);
    public boolean clearRelocatedContent(String id, String directiveName);
    
    /**
     * <p>Removes content from the model.</p>
     * @param directive
     * @throws TemplateException
     * @throws IOException
     */
    public void remove(ExtensibilityDirectiveData directive) throws TemplateException, IOException;
    
    /**
     * <p>Replaces content in the model.</p>
     * @param directive
     * @throws TemplateException
     * @throws IOException
     */
    public void replace(ExtensibilityDirectiveData directive) throws TemplateException, IOException;
    
    /**
     * <p>Places new content in the base model.</p>
     * 
     * @param directive
     * @throws TemplateException
     * @throws IOException
     */
    public void merge(ExtensibilityDirectiveData directive) throws TemplateException, IOException;
    
    /**
     * <p>Places content in the model before some base content.</p>
     * @param directive
     * @throws TemplateException
     * @throws IOException
     */
    public void before(ExtensibilityDirectiveData directive) throws TemplateException, IOException;
    
    /**
     * <p>Places content in the modell after some base content.</p>
     * 
     * @param directive
     * @throws TemplateException
     * @throws IOException
     */
    public void after(ExtensibilityDirectiveData directive) throws TemplateException, IOException;
    
    /**
     * <p>Finds a specific {@link ContentModelElement} (with the supplied identifier) in the model.</p>
     * @param id
     * @return
     */
    public ContentModelElement findContentModelElement(String id);
    
    /**
     * @return The {@link ExtensibilityDebugData} associated with the {@link ExtensibilityModel}.
     */
    public ExtensibilityDebugData getDebugData();
    
    /**
     * @return The {@link ExtensibilityDebugData} associated with the nested child {@link ExtensibilityModel}.
     */
    public ExtensibilityDebugData getChildDebugData();
    
    /**
     * <p>Sets the {@link ExtensibilityDebugData} associated with the nested child {@link ExtensibilityModel}.</p>
     */
    public void setChildDebugData(ExtensibilityDebugData childData);
    
    /**
     * <p>Retrieves the list of elements being generated as part of extension processing.</p>
     * @return
     */
    public List<ExtensibilityModelElement> getAdditionalContentElements();
    
    /**
     * <p>Allows {@link DeferredContentTargetModelElement} instances (along with their associated {@link OpenModelElement} and
     * {@link CloseModelElement} instances) to be inserted at a requested point in the model. This facility should be used with
     * extreme care and was provided to allow WebScripts run outside of the context of a page to process dependencies.</p>
     * 
     * @param index The index within the model to insert the elements
     * @param open The {@link OpenModelElement} that identifies the start of the content
     * @param target The {@link DeferredContentTargetModelElement} for adding deferred content into
     * @param close The {@link CloseModelElement} that identifies the end of the content
     */
    public void insertDeferredContentTarget(int index, OpenModelElement open, DeferredContentTargetModelElement target, CloseModelElement close);
}
