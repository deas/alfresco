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
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.extensibility.CloseModelElement;
import org.springframework.extensions.surf.extensibility.ContentModelElement;
import org.springframework.extensions.surf.extensibility.DeferredContentSourceModelElement;
import org.springframework.extensions.surf.extensibility.DeferredContentTargetModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirective;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.ExtensibilityModelElement;
import org.springframework.extensions.surf.extensibility.HandlesExtensibility;
import org.springframework.extensions.surf.extensibility.OpenModelElement;

import freemarker.template.TemplateException;


public class ExtensibilityModelImpl implements ExtensibilityModel
{
    private static final Log logger = LogFactory.getLog(ExtensibilityModelImpl.class);
    
    /**
     * <p>A reference to the enclosing parent model (if the current instance is nested, this is set to <code>null</code>
     * otherwise.</p>.
     */
    private ExtensibilityModel parentModel = null;
    
    /**
     * <p>The {@link HandlesExtensibility} implementing object responsible for creating this {@link ExtensibilityModel} instance.<p>
     */
    private HandlesExtensibility handler = null;
    
    /**
     * <p>Extensibility models can be nested. If the current model is nested then this method will return the
     * model that it is nested within.</p>
     */
    public ExtensibilityModel getParentModel()
    {
        return parentModel;
    }

    /**
     * 
     */
    private ExtensibilityDebugData debugData = new ExtensibilityDebugData();
    
    public ExtensibilityDebugData getDebugData()
    {
        return debugData;
    }

    private ExtensibilityDebugData childDebugData = null;
    
    public ExtensibilityDebugData getChildDebugData()
    {
        return childDebugData;
    }

    public void setChildDebugData(ExtensibilityDebugData childData)
    {
        this.childDebugData = childData;
    }
    
    /**
     * <p>Constructor for the model. Accepts a parent model if the new instance is to be nested.</p>
     * @param parentModel The parent model (if this instance is to be nested)
     * @param handler The handler used to create the model.
     */
    public ExtensibilityModelImpl(ExtensibilityModel parentModel, HandlesExtensibility handler)
    {
        this.parentModel = parentModel;
        this.handler = handler;
    }
    
    private ArrayList<ExtensibilityModelElement> modelContent = new ArrayList<ExtensibilityModelElement>();
    
    private boolean modelStarted = false;
    
    public boolean isModelStarted()
    {
        return this.modelStarted;
    }
    
    /**
     * <p>Indicates whether or not the basic model or the modules that extend it are currently
     * being processed. When the model is instantiated this is initialised to <code>false</code>
     * and can only switched to <code>true</code> by calling the method <code>switchToExtensionProcessing</code></p>
     */
    private boolean extensionProcessing = false;
    
    /**
     * <p>The {@link Writer} that model output will be written to when the model is flushed.</p>
     */
    private ModelWriter modelWriter = new ModelWriter(); 

    /**
     * <p>Returns the {@link ModelWriter} that will be written to when the model is flushed.</p>
     * @return The {@link ModelWriter} used by this {@link ExtensibilityModel}
     */
    public ModelWriter getWriter()
    {
        return this.modelWriter;
    }

    /**
     * <p>A {@link Stack} of the {@link ExtensibilityDirectiveData} instances that form the model. When a directive
     * is being processed it's associated {@link ExtensibilityDirectiveData} instance will be pushed onto the stack
     * and popped off once it has been processed.</p> 
     */
    private Stack<ExtensibilityDirectiveData> directiveStack = new Stack<ExtensibilityDirectiveData>();
    
    /**
     * <p>Switches the model into extension processing mode. This means that content can no longer
     * me appended to the end of the model but directives must be extending the existing model in some
     * way.</p>
     */
    public void switchToExtensionProcessing()
    {
        this.extensionProcessing = true;
        this.addDiscardContent(); // This ensures that unbounded extension code is not written to the output stream.
    }

    /**
     * <p>Indicates whether or not extensions are being processed or not.</p>
     * 
     * @return <code>true</code> if extensions are being processed or <code>false</code> if the base model
     * is still being created.
     */
    public boolean isExtensionProcessing()
    {
        return this.extensionProcessing;
    }
    
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
    public void insertDeferredContentTarget(int index, 
                                            OpenModelElement open, 
                                            DeferredContentTargetModelElement target, 
                                            CloseModelElement close)
    {
        // Defensive check to avoid for ArrayIndexOutOfBoundsExceptions
        // Added as a result of ACE-1399
        if (this.modelContent.size() < index)
        {
            index = this.modelContent.size();
        }
        this.modelContent.add(index, close);
        this.modelContent.add(index, target);
        this.modelContent.add(index, open);
    }
    
    /**
     * <p>Alias method that uses the current model stack to push the supplied argument onto.</p>
     * 
     * @param directiveData The {@link ExtensibilityDirectiveData} to push onto the stack.
     */
    private void pushDirective(ExtensibilityDirectiveData directiveData)
    {
        this.pushDirective(directiveData, this.modelContent);
    }
    
    /**
     * <p>Pushes a new <code>Directive</code> onto the model stack. The result of this is that new
     * <code>OpenModelElement</code> and <code>ContentModelElements</code> are added to the model.
     * This should occur each time a directive is processed, when the stack is popped a new <code>ContentModelElement</code>
     * of the previous <code>ModelElement</code> will be pushed as the current element to write to.</p>
     * 
     * @param directiveData
     * @param model The model that is passed in can either be the full content model or a sub-range. This will depend upon the
     * current action.
     */
    private void pushDirective(ExtensibilityDirectiveData directiveData, List<ExtensibilityModelElement> model)
    {
        this.directiveStack.push(directiveData);
        model.add(directiveData.createOpen());
        ContentModelElement contentElement = directiveData.createContentModelElement();
        model.add(contentElement);
        this.debugData.addData(contentElement.getId(), contentElement.getDirectiveName(), this.handler.getFileBeingProcessed());
        this.modelWriter.setCurrentBufferElement(contentElement.getNextContentBufferElement());
    }
    
    /**
     * <p>An alias method that uses the current model stack to pop a directive from.</p>
     * 
     * @return The {@link ExtensibilityDirectiveData} popped from the stack.
     */
    private ExtensibilityDirectiveData popDirective()
    {
        return this.popDirective(this.modelContent);
    }
    
    /**
     * <p>Pops the current <code>Directive</code> from the model stack. If there are any <code>Directives</code> still on the
     * stack then the top <code>Directive</code> is peeked and a new <code>ContentModelElement</code> is created for it and
     * pushed as the current <code>ModelWriter</code> element. This means that as the directive stack collapses content can be
     * written outside the current directive.</p>
     * 
     * @param model The model that is passed in can either be the full content model or a sub-range. This will depend upon the
     * current action.
     * 
     * @return
     */
    private ExtensibilityDirectiveData popDirective(List<ExtensibilityModelElement> model)
    {
        ExtensibilityDirectiveData currentDirective = this.directiveStack.pop();
        model.add(currentDirective.createClose());
        if (this.directiveStack.isEmpty())
        {
            // The stack is now empty, no need to re-create the previous directives content...
            if (this.extensionProcessing)
            {
                // If we're extension processing, add a discard content element to ensure that 
                // we don't add content not contained within extensibility directives.
                addDiscardContent();
            }
            else
            {
                // If we're processing the base model then add an unbound content element so
                // that we catch content not defined within extensibility directives.
                addUnboundContent();
            }
            
        }
        else
        {
            ExtensibilityDirectiveData lastDirective = this.directiveStack.peek();
            ContentModelElement contentElement = lastDirective.createContentModelElement();
            model.add(contentElement);
            this.modelWriter.setCurrentBufferElement(contentElement.getNextContentBufferElement());
        }
        return currentDirective;
    }
    
    /**
     * <p>Adds a new {@link UnboundContentModelElementImpl} to the model and sets its
     * content as the current buffer element in the model writer. This will capture any
     * output that is not contained within extensibility directives. Typically unbound
     * content is added before extensibility processing begins and then again once it 
     * has completed.</p>
     */
    public void addUnboundContent()
    {
        this.modelStarted = true;
        ContentModelElement unboundContent = new UnboundContentModelElementImpl();
        this.modelContent.add(unboundContent);
        this.modelWriter.setCurrentBufferElement(unboundContent.getNextContentBufferElement());
    }

    /**
     * <p>Adds a new {@link DiscardUnboundContentModelElementImpl} to the model and sets its
     * content as the current buffer element in the {@link ModelWriter}. Any content that
     * is subsequently rendered will effectively be discarded until a new {@link ContentModelElement}
     * is added. This allows unbounded content in extending templates to be safely discarded.</p>
     */
    public void addDiscardContent()
    {
        this.modelStarted = true;
        ContentModelElement discardContent = new DiscardUnboundContentModelElementImpl();
        this.modelContent.add(discardContent);
        this.modelWriter.setCurrentBufferElement(discardContent.getNextContentBufferElement());
    }
    
    /**
     * <p>Finds and returns the deferred {@link DeferredContentTargetModelElement} specified by the supplied
     * directive ID and name. The purpose of a {@link DeferredContentTargetModelElement} is to allow nested
     * content to directly manipulate content outside their scope. This has been provided for the purpose
     * of allowing WebScripts to add JavaScript and CSS dependencies into the <{@code}head> element of
     * the HTML page which would otherwise not normally be possible because by the time the WebScript
     * is being processed the <{@code}head> element would have already been processed.</p>
     * <p>This method will only return {@link DeferredContentTargetModelElement} objects and not any other
     * type of content added to the model. It will return <code>null</code> if the target content
     * cannot be found.</p>
     * 
     * @param directiveId The ID of the deferred content to find
     * @param directiveName The directive name used to add the deferred content.
     * @return The requested {@link DeferredContentTargetModelElement} or <code>null</code> if it couldn't be found.
     */
    public DeferredContentTargetModelElement getDeferredContent(String directiveId, String directiveName)
    {
        return getDeferredContent(directiveId, directiveName, this);
    }
    
    private DeferredContentTargetModelElement getDeferredContent(String directiveId, String directiveName, ExtensibilityModel modelScope)
    {
        DeferredContentTargetModelElement deferredContent = null;
        if (modelScope != null && modelScope instanceof ExtensibilityModelImpl)
        {
            RangeData targetRange = ((ExtensibilityModelImpl) modelScope).findTargetRange(directiveId, directiveName);
            if (targetRange != null)
            {
                Object o =  ((ExtensibilityModelImpl) modelScope).modelContent.get(targetRange.getStartingIndex() + 1); // The starting index is the OPEN element, add one to get the content
                if (o instanceof DeferredContentTargetModelElement)
                {
                    deferredContent = (DeferredContentTargetModelElement) o;
                }
                else
                {
                    // TODO: Generate error for wrong type.
                }
            }
            else
            {
                // TODO: Generate error for content not found
                deferredContent = getDeferredContent(directiveId, directiveName, modelScope.getParentModel());
            }
        }
        return deferredContent;
    }
    
    /**
     * <p>Flushes the contents of the model to the supplied output stream. This should be
     * called once extensibility processing has completed.</p>
     * 
     * @param out The <code>Writer</code> to flush the model contents to.
     */
    public void flushModel(Writer out)
    {
        // If this model is wrapped by a parent then write to its writer, not the supplied instance...
        Writer targetWriter = out;
        if (this.parentModel != null)
        {
            targetWriter = this.parentModel.getWriter();
        }
        
        for (ExtensibilityModelElement element: this.modelContent)
        {
            if (element instanceof ContentModelElement)
            {
                ContentModelElement cme = (ContentModelElement) element;
                try
                {
                    targetWriter.write(cme.flushContent());
                }
                catch (IOException e)
                {
                    if (logger.isErrorEnabled())
                    {
                        logger.error("The following exception occurred flushing the model for ContentModelElement: " + cme, e);
                    }
                }
            }
        }
        
        // Clear the model...
        this.directiveStack.clear();
        this.modelContent.clear();
        this.modelStarted = false; // Once the model has been flushed indicate the model is no longer in a started state.
    }
    
    /**
     * <p>Merges the supplied {@link ExtensibilityDirectiveData} instance into the model. The merge action can only be
     * used as part of extension processing when it is <b>not</b> the root action of the extension. For example a 
     * <@region> directive can be merged into the model as part of a "before", "after" or "replace" action of an enclosing
     * <@markup> directive. This relies on some additional content elements being created in the model for the new content
     * to be added to.</p>
     * @param The {@link ExtensibilityDirectiveData} instance to merge into the model.
     */
    public void merge(ExtensibilityDirectiveData directiveData) throws TemplateException, IOException
    {
        this.modelStarted = true;
        if (isExtensionProcessing())
        {
            // When extension processing we should check that there are some additional elements to add the
            // merged content to. These can exist either in the current model or within the model hierarchy
            // but without them the merge operation cannot be allowed to go ahead.
            List<ExtensibilityModelElement> additionalElements = getAdditionalContentElements();
            if (additionalElements != null)
            {
                this.pushDirective(directiveData, additionalElements);
                directiveData.render(this.modelWriter);
                this.popDirective(additionalElements);
            }
            else
            {
                logger.error("A merge request was made as a root extension request. This is not allowed.");
            }
        }
        else
        {
            // Perform a normal merge operation using the current model...
            this.pushDirective(directiveData);
            directiveData.render(this.modelWriter);
            this.popDirective();
        }
    }
    
    /**
     * <p>This contains all the elements that are being generated through extension processing.</p>
     */
    private List<ExtensibilityModelElement> additionalContent = null;
    
    /**
     * <p>Retrieves the current {@link List} of {@link ExtensibilityModelElement} instances that are being
     * generated as part of extension processing. This {@link List} will either exist in the current {@link ExtensibilityModel}
     * or somewhere within the model hierarchy. This method will return <code>null</code> if no {@link List} has
     * been instantiated - this typically means that an extension has been created in error. Normally because a
     * "merge" action has been requested as the root extension action.</p>
     */
    public List<ExtensibilityModelElement> getAdditionalContentElements()
    {
        List<ExtensibilityModelElement> additionalElements = this.additionalContent;
        if (additionalElements == null && this.parentModel != null)
        {
            additionalElements = this.parentModel.getAdditionalContentElements();
        }
        return additionalElements;
    }
    
    /**
     * <p>Generates additional content into a <b>new</b> list. This content is <b>not</b> inserted into the model
     * but is simply returned from the method.</p>
     * @param directiveData Data on the directive to process as new content.
     * @return A new list of <code>ExtensibilityModelElements</code> generated from the supplied directive data.
     * @throws TemplateException
     * @throws IOException
     */
    private List<ExtensibilityModelElement> generateAdditionalContent(ExtensibilityDirectiveData directiveData) throws TemplateException, IOException
    {
        this.additionalContent = new ArrayList<ExtensibilityModelElement>();
        this.pushDirective(directiveData, additionalContent);
        directiveData.render(this.modelWriter);
        this.popDirective(additionalContent);
        return additionalContent;
    }
    
    /**
     * <p>Generates the output from the supplied directive data and inserts it into the model at the index
     * before the start of the target defined in the directive. If the target directive cannot be found 
     * then the generated output is not inserted into the model.</p>
     * <p><b>WARNING:</b> The content will also not be added if the model has not been switched into extension 
     * processing mode as it is not valid for a model declaring template to manipulate its own contents.</p>
     * 
     *  @param directiveData
     *  @throws IOException
     *  @throws TemplateException
     */
    public void before(ExtensibilityDirectiveData directiveData) throws TemplateException, IOException
    {
        if (extensionProcessing)
        {
            RangeData targetRange = findTargetRange(directiveData.getTarget(), directiveData.getDirectiveName());
            if (targetRange != null)
            {
                List<DeferredContentSourceModelElement> deferredContentSourceElements = this.enterDeferredContentEditMode(targetRange, ExtensibilityDirective.ACTION_BEFORE);
                List<ExtensibilityModelElement> additionalElements = generateAdditionalContent(directiveData);
                this.modelContent.addAll(targetRange.getStartingIndex(), additionalElements);
                this.exitDeferredContentEditMode(deferredContentSourceElements);
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Could not locate target directive when processing 'before' action for directive: " + directiveData);
                }
            }
        }
        else
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("The 'before' action was attempted to used when defining the base model by directive:" + directiveData);
            }
        }
    }
    
    /**
     * <p>Generates the output from the supplied directive data and inserts it into the model at the index
     * after the end of the target defined in the directive. If the target directive cannot be found 
     * then the generated output is not inserted into the model.</p>
     * <p><b>WARNING:</b> The content will also not be added if the model has not been switched into extension 
     * processing mode as it is not valid for a model declaring template to manipulate its own contents.</p>
     * 
     *  @param directiveData
     *  @throws IOException
     *  @throws TemplateException
     */
    public void after(ExtensibilityDirectiveData directiveData) throws TemplateException, IOException
    {
        if (extensionProcessing)
        {
            RangeData targetRange = findTargetRange(directiveData.getTarget(), directiveData.getDirectiveName());
            if (targetRange != null)
            {
                List<DeferredContentSourceModelElement> deferredContentSourceElements = this.enterDeferredContentEditMode(targetRange, ExtensibilityDirective.ACTION_AFTER);
                List<ExtensibilityModelElement> additionalElements = generateAdditionalContent(directiveData);
                this.modelContent.addAll(targetRange.getEndingIndex() + 1, additionalElements);
                this.exitDeferredContentEditMode(deferredContentSourceElements);
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Could not locate target directive when processing 'after' action for directive: " + directiveData);
                }
            }
        }
        else
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("The 'after' action was attempted to used when defining the base model by directive:" + directiveData);
            }
        }
    }
    
    /**
     * <p>Finds the elements in the model that match the range (including all nested
     * and unbound content) with the specified id parameter and removes them.</p>
     * 
     * @param directiveData The id of the range of elements to remove
     */
    public void remove(ExtensibilityDirectiveData directiveData)
    {
        if (this.extensionProcessing)
        {
            RangeData targetRange = findTargetRange(directiveData.getTarget(), directiveData.getDirectiveName());
            if (targetRange == null)
            {
                // Could not find target, nothing to remove.
                if (logger.isDebugEnabled())
                {
                    logger.debug("Could not find target " + directiveData.getTarget() + " to remove when processing directive: " + directiveData);
                }
            }
            else
            {
                // Check for any DeferredContentSource elements - they will need to update the holder of the
                // deferred content they provide that they are being modified...
                List<DeferredContentSourceModelElement> deferredContentSourceElements = this.enterDeferredContentEditMode(targetRange, ExtensibilityDirective.ACTION_REMOVE);
                targetRange.getTargetRange().clear();
                this.exitDeferredContentEditMode(deferredContentSourceElements);
            }
        }
        else
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("The 'remove' action was attempted to used when defining the base model by directive:" + directiveData);
            }
        }
    }

    /**
     * <p>Replaces the elements of the content model within the range defined by the
     * id of the supplied directive with the output generated by rendering the supplied
     * <code>TemplateDirectiveBody</code>.
     * 
     * @param directiveData The directive requesting to replace the existing elements with the same id
     * @param body The directive body that should be processed to generate the replacement content
     */
    public void replace(ExtensibilityDirectiveData directiveData) throws TemplateException, IOException
    {
        if (this.extensionProcessing)
        {
            RangeData targetRange = findTargetRange(directiveData.getTarget(), directiveData.getDirectiveName());
            if (targetRange != null)
            {
                // Check for any DeferredContentSource elements - they will need to update the holder of the
                // deferred content they provide that they are being modified...
                List<DeferredContentSourceModelElement> deferredContentSourceElements = this.enterDeferredContentEditMode(targetRange, ExtensibilityDirective.ACTION_REPLACE);
                
                // Clear the elements to be replaced...
                targetRange.getTargetRange().clear();
                
                // Generate the new content, but generate it into a new list to avoid ConcurrentModificationExceptions
                this.additionalContent = new ArrayList<ExtensibilityModelElement>();
                this.pushDirective(directiveData, this.additionalContent);
                directiveData.render(this.modelWriter);
                this.popDirective(this.additionalContent);
                
                // Update the DeferredContentSource elements that editing is now complete...
                this.exitDeferredContentEditMode(deferredContentSourceElements);
                
                // Insert the new range into the content model at the index where the removed range began
                this.modelContent.addAll(targetRange.getStartingIndex(), this.additionalContent);
            }
            else
            {
                // If the target range could not be found then we cannot insert the replacement.
                if (logger.isDebugEnabled())
                {
                    logger.debug("Could not find target " + directiveData.getTarget() + " to replace when processing directive: " + directiveData);
                }
            }
        }
        else
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("The 'replace' action was attempted to used when defining the base model by directive:" + directiveData);
            }
        }
    }

    /**
     * <p>Iterates over the supplied {@link RangeData} object and calls the <code>enterEditMode</code> method
     * on any {@link DeferredContentSourceModelElement} instances that are found within it. This allows deferred content
     * to be manipulated when extensions are applied to it. For example - deferred content may have already been
     * set which needs to be replaced.</p>
     * 
     * @param targetRange The range of elements that need to be checked for {@link DeferredContentSourceModelElement} instances
     * @param action The name of the action being processed
     * @return A {@link List} of the {@link DeferredContentSourceModelElement} instances found - this is returned as the range is likely
     * to be cleared.
     */
    public List<DeferredContentSourceModelElement> enterDeferredContentEditMode(RangeData targetRange, String action)
    {
        List<DeferredContentSourceModelElement> deferredSourceElements = new ArrayList<DeferredContentSourceModelElement>();
        for (ExtensibilityModelElement element: targetRange.getTargetRange())
        {
            if (element instanceof DeferredContentSourceModelElement)
            {
                ((DeferredContentSourceModelElement) element).enterEditMode(action);
                deferredSourceElements.add((DeferredContentSourceModelElement) element);
            }
        }
        return deferredSourceElements;
    }
    
    /**
     * <p>Iterates over the supplied {@link RangeData} object and calls the <code>exitEditMode</code> method on 
     * any {@link DeferredContentSourceModelElement} instances that are found within it. This indicates to the
     * associated {@link DeferredContentTargetModelElement} instance that editing is now finished.</p>
     * @param targetRange The range of elements that need to be checked for {@link DeferredContentSourceModelElement} instances 
     */
    public void exitDeferredContentEditMode(List<DeferredContentSourceModelElement> deferredSourceElements)
    {
        for (DeferredContentSourceModelElement element: deferredSourceElements)
        {
            element.exitEditMode();
        }
    }
    
    @Override
    public String toString()
    {
        StringBuilder out = new StringBuilder();
        StringBuilder indent = new StringBuilder();
        for (ExtensibilityModelElement element: this.modelContent)
        {
            if (element.getType().equals(CloseModelElement.TYPE))
            {
                indent.delete(indent.length() - 4, indent.length());
            }
            out.append(indent + "<" + element.getType() + " id=\"" + element.getId() + "\">\n");
            if (element.getType().equals(OpenModelElement.TYPE))
            {
                indent.append("    ");
            }
        }
        return out.toString();
    }

    public ContentModelElement findContentModelElement(String id)
    {
        ContentModelElement target = null;
        ContentModelElement matcher = new DefaultContentModelElement(id, null);
        
        int index = this.modelContent.indexOf(matcher);
        if (index != -1)
        {
            target = (ContentModelElement) this.modelContent.get(index);
        }
        return target;
    }
    
    /*
     * This method has been added for the express purpose of allowing directives that relocate
     * content to remove the relocated elements. This method is potentially open to mis-use and
     * alternatives should be considered.
     */
    public boolean clearRelocatedContent(String id, String directiveName)
    {
        boolean success = false;
        RangeData targetRange = findTargetRange(id, directiveName);
        if (targetRange == null)
        {
            // Could not find target, nothing to remove.
        }
        else
        {
            // Remove the target ModelElements (including any nested content)...
            targetRange.getTargetRange().clear();
        }
        
        return success;
    }
    
    /**
     * <p>Finds a target range of elements within the content model that are contained
     * within the <code>OpenModelElement</code> and <code>CloseModelElement</code> with
     * an id matching the supplied parameter.</p>
     * @param id The id of the open/close elements that the target range should be between.
     * @return A <code>RangeData</code> object from which both the range and the index that
     * the range starts in the content model can be retrieved from.
     */
    private RangeData findTargetRange(String id, String directiveName)
    {
        RangeData results = null;
        List<ExtensibilityModelElement> targetRange = null;
        OpenModelElement targetOpen = new OpenModelElementImpl(id, directiveName);
        
        int openIndex = this.modelContent.indexOf(targetOpen);
        if (openIndex == -1)
        {
            // Could not find open
            if (logger.isDebugEnabled())
            {
                logger.debug("Could not find extensibiliy OPEN model element with the id: " + id);
            }
        }
        else
        {
            CloseModelElement targetClose = new CloseModelElementImpl(id, null);
            int closeIndex = this.modelContent.indexOf(targetClose);
            if (closeIndex == -1)
            {
                // Could not find matching close
                if (logger.isDebugEnabled())
                {
                    logger.debug("Could not find extensibiliy CLOSE model element with the id: " + id);
                }

            }
            else if (closeIndex > openIndex)
            {
                targetRange = this.modelContent.subList(openIndex, closeIndex+1);
                results = new RangeData(openIndex, closeIndex, targetRange);
            }
            else
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("An unexpected error occurred, the index of the CLOSE element is less that that of the associated OPEN element for target id: " + id);
                }
            }
        }
        return results;
    }
    
    /**
     * <p>This private class is used when searching for ranges in the content model.
     * It is used as a container to return both the starting index where the range 
     * begins and to return a sub-range of the content model.</p>
     *  
     * @author David Draper
     */
    private class RangeData
    {
        /**
         * <p>Instantiates a new <code>RangeData</code> object. It is not possible to 
         * change the supplied startingIndex or targetRange values once they have
         * been set.</p>
         * 
         * @param startingIndex The index where the target range starts in the content model
         * @param endingIndex The index where the target range ends in the content model
         * @param targetRange A target range that is a subset of the content model
         */
        public RangeData(int startingIndex, int endingIndex, List<ExtensibilityModelElement> targetRange)
        {
            this.startingIndex = startingIndex;
            this.endingIndex = endingIndex;
            this.targetRange = targetRange;
        }
        
        private int startingIndex = -1;
        
        private int endingIndex = -1;
        
        private List<ExtensibilityModelElement> targetRange = null;

        public int getStartingIndex()
        {
            return startingIndex;
        }

        public int getEndingIndex()
        {
            return endingIndex;
        }

        public List<ExtensibilityModelElement> getTargetRange()
        {
            return targetRange;
        }
    }
}
