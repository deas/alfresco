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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.extensions.webscripts.SearchPath;
import org.springframework.extensions.webscripts.Store;

public class WebScriptExtensibilityModuleHandler
{
    private static final Log logger = LogFactory.getLog(WebScriptExtensibilityModuleHandler.class);
    
    /**
     * <p>This {@link SearchPath} is used for finding {@link BasicExtensionModule} configuration files.</p>
     */
    private SearchPath configurationSearchPath;

    /**
     * <p>A setter provided so that the Spring configuration can set the {@link SearchPath}</p>
     * @param configurationSearchPath
     */
    public void setConfigurationSearchPath(SearchPath configurationSearchPath)
    {
        this.configurationSearchPath = configurationSearchPath;
    }

    /**
     * <p>The {@link List} of {@link BasicExtensionModule} configuration files found on the {@link SearchPath}. This
     * {@link List} will only initialised once (the first time that it is requested).
     */
    protected List<BasicExtensionModule> extensionModules = null;
    
    /**
     * <p>Sets up the list of {@link BasicExtensionModule} instances found in the configuration. This should only
     * be called once when the bean initialises via the <code>init</code> method.</p>
     * 
     * @return A {@link List} of configured {@link BasicExtensionModule} instances.
     */
    public synchronized List<BasicExtensionModule> getExtensionModules()
    {
        if (this.extensionModules == null)
        {
            this.extensionModules = new ArrayList<BasicExtensionModule>();
            if (this.configurationSearchPath != null)
            {
                for (Store store: this.configurationSearchPath.getStores())
                {
                    try
                    {
                        // Find all the XML files in the store and create BasicExtensionModule instances from them...
                        for (String path: store.getDocumentPaths("/", true, "*.xml"))
                        {
                            InputStream is = store.getDocument(path);
                            SAXReader reader = new SAXReader();
                            Document document;
                            try
                            {
                                document = reader.read(is);
                                Element rootElement = document.getRootElement();
                                if (rootElement.getName().equals("extension"))
                                {
                                    for (Object modules: rootElement.elements("modules"))
                                    {
                                        if (modules instanceof Element)
                                        {
                                            for (Object module: ((Element) modules).elements("module"))
                                            {
                                                if (module instanceof Element)
                                                {
                                                    BasicExtensionModule bem = new BasicExtensionModule((Element) module);
                                                    this.extensionModules.add(bem);
                                                }
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    logger.warn("Extensibility configuration file \"" + path + "\" does not have \"extension\" as its root element");
                                }
                            }
                            catch (DocumentException e)
                            {
                                logger.warn("Extensibility configuration file \"" + path + "\" was not a valid XML file.", e);
                            }
                            
                        }
                    }
                    catch (IOException e)
                    {
                        logger.error("An error occurred loading the extension modules", e);
                    }
                }
            }
            else
            {
                logger.error("The \"configurationSearchPath\" has not been set for the extensibility module handler");
            }
        }
        return this.extensionModules;
    }
    
    /**
     * <p>Returns a {@link List} of the files that should be applied to an {@link ExtensibilityModel}
     * being processed.</p>
     * 
     * @param pathBeingProcessed The path of the file being processed. This will typically be a FreeMarker
     * template, JavaScript controller or NLS properties file.
     * @return A {@link List} of the files that extend the current file being processed.
     */
    public List<String> getExtendingModuleFiles(BasicExtensionModule module, String pathBeingProcessed)
    {
     // Create a new list to hold any templates that might be provided...
        ArrayList<String> customizationPaths = new ArrayList<String>();
        for (Customization customization: module.getCustomizations())
        {
            if (customization.getTargetPackageName() != null && customization.getSourcePackageName() != null)
            {
                // If both a target and source package have been provided, see if the current 
                // path falls within the target package...
                String targetPackage = customization.getTargetPackageName().replace(".", "/");
                if (pathBeingProcessed.startsWith(targetPackage))
                {
                    // The path starts with the target package so we know that this module
                    // could be applied to the current module if it passes further evaluation...
                    String packageSubPath = pathBeingProcessed.substring(targetPackage.length());
                    String customizationPath = customization.getSourcePackageName().replace(".", "/");
                    if (!customizationPath.endsWith("/") && !packageSubPath.startsWith("/"))
                    {
                        customizationPath = customizationPath + "/" + packageSubPath;
                    }
                    else
                    {
                        customizationPath = customizationPath + packageSubPath;
                    }
                    
                    if (customizationPath.equals(pathBeingProcessed))
                    {
                        // If the resolved module path is the same as the base path then there is no need to process it.
                        // The only valid reason that this might occur is if the developer has created a module where the
                        // target package root matches the source package root in order to inject dependencies into all 
                        // templates that get processed.
                    }
                    else
                    {
                        customizationPaths.add(customizationPath);
                    }
                    
                    // The following section of code addresses WebScript extensions that should always be applied.
                    // This has been added to support the Alfresco Cloud where a common extension needs to be applied
                    // to all pages that are built using Aikau. This is primarily done to support header extensions
                    // which for other pages are done by a specific component, but full Aikau pages need to share
                    // extensions that don't map precisely to the WebScript being processed...
                    int index = pathBeingProcessed.lastIndexOf("/");
                    if (index != -1)
                    {
                        String fileName = pathBeingProcessed.substring(index);
                        index = fileName.indexOf(".");
                        if (index != -1)
                        {
                            String extension = fileName.substring(index);
                            customizationPath = customization.getSourcePackageName().replace(".", "/");
                            for (String ws: customization.getCommonWebScripts())
                            {
                                if (!customizationPath.endsWith("/"))
                                {
                                    customizationPaths.add(customizationPath + "/" + ws + extension);
                                }
                                else
                                {
                                    customizationPaths.add(customizationPath + ws + extension);
                                }
                            }
                        }
                    }
                }
            }
        }
        return customizationPaths;
    }
}
