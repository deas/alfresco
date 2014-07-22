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
 * <p>This is a {@link ContentModelElement} that does not produce any content when it is first rendered by instead
 * relies on subsequently rendered {@link DeferredContentSourceModelElement} instances to populate it with data
 * before its final content is flushed. This has been provided to act as a placeholder for CSS and JavaScript
 * dependency requests that may be made by WebScripts rendered later on in the model.</p>
 * 
 * @author David Draper
 */
public interface DeferredContentTargetModelElement extends ContentModelElement
{
    /**
     * <p>This method should be called by a {@link DeferredContentSourceModelElement} to register itself with its
     * target so that when the {@link DeferredContentTargetModelElement} flushes its content it can reach into the
     * {@link ExtensibilityModel} to retrieve the content it needs to output.</p>
     * 
     * @param sourceElement The {@link DeferredContentSourceModelElement} calling the method.
     */
    public void registerDeferredSourceElement(DeferredContentSourceModelElement sourceElement);
    
    /**
     * <p>Moves the {@link DeferredContentTargetModelElement} into edit mode so that subsequent requests are 
     * processed appropriately. This allows the insertion of content at the correct point by an extension.</p>
     * @param mode The edit mode that has been started, e.g. "remove", "replace", "after", etc.
     * @param sourceElement THe {@link DeferredContentSourceModelElement} that is being edited.
     */
    public void enterEditMode(String mode, DeferredContentSourceModelElement sourceElement);
    
    /**
     * <p>Moves the {@link DeferredContentTargetModelElement} out of edit mode so that normal processing is 
     * resumed.</p>
     */
    public void exitEditMode();
}
