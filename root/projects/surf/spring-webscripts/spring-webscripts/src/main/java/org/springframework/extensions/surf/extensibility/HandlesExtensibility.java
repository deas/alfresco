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

import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.extensions.webscripts.ExtendedScriptConfigModel;
import org.springframework.extensions.webscripts.ScriptConfigModel;
import org.springframework.extensions.webscripts.TemplateConfigModel;
import org.springframework.extensions.webscripts.WebScriptPropertyResourceBundle;

/**
 * <p></p>
 * 
 * @author David Draper
 */
public interface HandlesExtensibility
{
    /**
     * <p>This indicates that although extensibility is handled it has been temporarily suppressed</p>
     * @return
     */
    public boolean isExtensibilitySuppressed();
    
    /**
     * <p>Should be implemented to return an {@link ExtensibilityModel} if one is currently being built. If a model
     * is not being built then this should return null.</p>
     * 
     * @return
     */
    public ExtensibilityModel getCurrentExtensibilityModel();
    
    /**
     * <p>One {@link ExtensibilityModel} can be nested within another. This may occur where it is necessary to render the output of
     * an entire child model as an element of the current model. Calling this method will effectively nest a new {@link ExtensibilityModel}
     * within the model currently being processed.</p>
     *  
     * @return A reference to a new nested {@link ExtensibilityModel}.
     */
    public ExtensibilityModel openExtensibilityModel();
    
    /**
     * <p>One {@link ExtensibilityModel} can be nested within another. This may occur where it is necessary to render the output of
     * an entire child model as an element of the current model. Calling this method will close the current model so that an enclosing
     * parent model will become the current model. The current model should also be flushed to the supplied {@link Writer} when the 
     * model is closed.</p>
     * 
     * @param model The model to close.
     * @param out The {@link Writer} to render the output of the closed model to.
     */
    public void closeExtensibilityModel(ExtensibilityModel model, Writer out);
    
    /**
     * <p>Returns an HTML Spring defining the JavaScript and CSS resource dependencies defined by extending
     * modules.</p>
     * 
     * @param pathBeingProcessed
     * @param model
     */
    public void updateExtendingModuleDependencies(String pathBeingProcessed, Map<String, Object> model);
    
    
    /**
     * <p>Returns a {@link List} of the files that should be applied to an {@link ExtensibilityModel}
     * being processed.</p>
     * 
     * @param pathBeingProcessed The path of the file being processed. This will typically be a FreeMarker
     * template, JavaScript controller or NLS properties file.
     * @return A {@link List} of the files that extend the current file being processed.
     */
    public List<String> getExtendingModuleFiles(String pathBeingProcessed);
    
    /**
     * <p>Returns the path of the file currently being processed in the model. This information is primarily provided
     * for the purposes of generating debug information.</p>
     * @return The path of the file currently being processed.
     */
    public String getFileBeingProcessed();
    
    /**
     * <p>Sets the path of the file currently being processed in the model. This information should be collected to assist
     * with providing debug information.</p>
     * @param file The path of the file currently being processed.
     */
    public void setFileBeingProcessed(String file);
    
    /**
     * <p>Checks the container to see if it has cached an extended bundle (that is a basic {@link ResourceBundle} that
     * has had extension modules applied to it. Extended bundles can only be safely cached once per request as the modules
     * applied can vary for each request.</p>
     * 
     * @param webScriptId The id of the WebScript to retrieve the extended bundle for.
     * @return A cached bundle or <code>null</code> if the bundle has not previously been cached.
     */
    public ResourceBundle getCachedExtendedBundle(String webScriptId);
    
    /**
     * <p>Adds a new extended bundle to the cache. An extended bundle is a WebScript {@link ResourceBundle} that has had
     * {@link ResourceBundle} instances merged into it from extension modules that have been applied. These can only be
     * cached for the lifetime of the request as different modules may be applied to the same WebScript for different
     * requests.</p>
     * 
     * @param webScriptId The id of the WebScript to cache the extended bundle against.
     * @param extensionBUndle The extended bundle to cache.
     */
    public void addExtensionBundleToCache(String webScriptId, WebScriptPropertyResourceBundle extensionBUndle);
    
    /**
     * <p>Creates a new {@link ExtendedScriptConfigModel} instance which contains configuration provided by the extensions that
     * have been evaluated to be processed. This method will return <code>null</code> if it is not possible to create a new instance
     * which may be the case for many reasons.</p>
     * 
     * @param xmlConfig Optional additional XML configuration to include. This is typically provided by WebScripts.
     * @return A new {@link ExtendedScriptConfigModel} or <code>null</code> if one could not be created. 
     */
    public ScriptConfigModel getExtendedScriptConfigModel(String xmlConfig);
    
    /**
     * <p>Creates a new {@link ExtendedTemplateConfigModel} instance which contains configuration provided by the extensions that
     * have been evaluated to be processed. This method will return <code>null</code> if it is not possible to create a new instance
     * which may be the case for many reasons.</p>
     * 
     * @param xmlConfig Optional additional XML configuration to include. This is typically provided by WebScripts.
     * @return A new {@link ExtendedTemplateConfigModel} or <code>null</code> if one could not be created. 
     */
    public TemplateConfigModel getExtendedTemplateConfigModel(String xmlConfig);
    
    /**
     * <p>Adds any custom FreeMarker directives required for rendering templates when being processed.</p>
     * @param freeMarkerModel The model to add the directives to.
     * @param extModel The current {@link ExtensibilityModel} being worked on.
     */
    public void addExtensibilityDirectives(Map<String, Object> freeMarkerModel, ExtensibilityModel extModel);
}
