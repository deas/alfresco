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

/**
 * <p>This is a {@link ContentModelElement} that doesn't place any content at the location where it is placed in
 * the {@link ExtensibilityModel}. Instead it is adds content to a previously rendered {@link DeferredContentTargetModelElement}
 * that it is associated with. This has been provided to allow WebScripts to add new CSS and JavaScript dependency requests
 * into the <{@code HEAD>} element of the HTML page after it has already been rendered.</p>
 * 
 * @author David Draper
 */
public interface DeferredContentSourceModelElement extends ContentModelElement
{
    /**
     * <p>Indicates the previously generated deferred content is being edited by an extension. This 
     * calls the associated {@link DeferredContentTargetModelElement} to indicate that editing is occurring
     * to prepare it for handling subsequent requests.</p>
     * 
     * @param mode The mode of editing being started, e.g. "replace", "remove", "after", etc.
     */
    public void enterEditMode(String mode);
    
    /**
     * <p>Indicates that the previously generated deferred content has finished being edited by an
     * extension.</p>
     */
    public void exitEditMode();
    
    /**
     * <p>Allow elements to be marked when they're removed. This is done because even if an element is removed from 
     * the model it may still be referenced elsewhere. This makes it possible to check that the element is still
     * part of the model when the model is not available.<p>
     */
    public void markAsRemoved();
    
    /**
     * <p>Indicates whether or not the element has been removed from the model or not.</p>
     * @return <code>true</code> if the element is no longer in the model and <code>false</code> otherwise.
     */
    public boolean hasBeenRemoved();
}
