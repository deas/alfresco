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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

public class Customization
{
    public static final String TARGET_PACKAGE = "targetPackageRoot";
    public static final String SOURCE_PACKAGE = "sourcePackageRoot";
    public static final String COMMON_WEBSCRIPTS = "alwaysApply";
    public static final String WEBSCRIPT_NAME = "webscript";
    public static final String DEPENDENCIES = "dependencies";
    public static final String CSS = "css";
    public static final String CSS_MEDIA = "media";
    public static final String DEFAULT_CSS_MEDIA = "screen";
    public static final String JS = "js";
    public static final String EVALUATOR = "evaluator";
    public static final String EVALUATOR_PROPS = "evaluatorProperties";
    public static final String EVALUATOR_PROP = "evaluatorProperty";
    public static final String EVALUATOR_PROP_NAME = "name";
    public static final String EVALUATOR_PROP_VALUE = "value";
    
    private String targetPackageName = null;
    private String sourcePackageName = null;
    private List<String> commonWebScripts;
    private Map<String, List<String>> cssDependencies;
    private List<String> jsDependencies;
    private String evaluator = null;
    private Map<String, String> evaluatorProperties = new HashMap<String, String>();
    
    public Customization(Element sourceElement)
    {
        applyConfiguration(sourceElement);
    }
    
    public void applyConfiguration(Element customizationEl)
    {
        this.targetPackageName = XMLHelper.getStringData(TARGET_PACKAGE, customizationEl, true);
        this.sourcePackageName = XMLHelper.getStringData(SOURCE_PACKAGE, customizationEl, true);
        this.commonWebScripts = getJavaScriptDependencies(COMMON_WEBSCRIPTS, WEBSCRIPT_NAME, customizationEl);
        this.evaluatorProperties = XMLHelper.getProperties(EVALUATOR_PROPS, customizationEl);
        this.cssDependencies = getCssDependencies(DEPENDENCIES, CSS, customizationEl);
        this.jsDependencies = getJavaScriptDependencies(DEPENDENCIES, JS, customizationEl);
    }
    
    
    
    /**
     * <p>Retrieves the list of JavaScript resource dependencies specified within the supplied 
     * configuration customization element.<p>
     * 
     * @param elementName The name of the element to look for.
     * @param dependencyElementName The dependency element to look for
     * @param sourceElement The element to search within.
     * @return A {@link List} of the JavaScript dependencies.
     */
    @SuppressWarnings("unchecked")
    private List<String> getJavaScriptDependencies(String elementName, String dependencyElementName, Element sourceElement)
    {
        List<String> dependencies = new ArrayList<String>();
        Element el = sourceElement.element(elementName);
        if (el != null)
        {
            List<Element> elementList = el.elements(dependencyElementName);
            for (Element element: elementList)
            {
                dependencies.add(element.getTextTrim());
            }
        }
        return dependencies;
    }
    
    /**
     * <p>Creates a {@link Map} of CSS media types to lists of dependencies for that type. This map is generated
     * from the content defined within children of the supplied {@link Element}.</p>
     * <p>CSS depdendencies are defined as follows:
     * <pre>
     * <{@code dependencies}>
     *    <{@code css}><{@code/css>
     *    <{@code css media="screen"}><{@code}/css>
     *    <{@code css media="print"}><{@code}/css>
     * <{@code /dependencies}></pre>
     * If no "media" attribute is specified then "screen" will be used by default.
     * </p>
     * 
     * @param elementName
     * @param dependencyElementName
     * @param sourceElement
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, List<String>> getCssDependencies(String elementName, String dependencyElementName, Element sourceElement)
    {
        Map<String, List<String>> dependencies = new HashMap<String, List<String>>();
        Element el = sourceElement.element(elementName);
        if (el != null)
        {
            List<Element> elementList = el.elements(dependencyElementName);
            for (Element element: elementList)
            {
                String media = element.attributeValue(CSS_MEDIA);
                if (media == null)
                {
                    // If no media attribute is set then use the default media type...
                    media = DEFAULT_CSS_MEDIA;
                }
                
                // Get the list of dependencies specific to the requested media type...
                List<String> mediaSpecificDependencies = dependencies.get(media);
                if (mediaSpecificDependencies == null)
                {
                    // If no other dependencies for the requested media have not been added yet
                    // then create a new list to hold them and add it to the map...
                    mediaSpecificDependencies = new ArrayList<String>();
                    dependencies.put(media, mediaSpecificDependencies);
                }
                
                // Add the dependency to the list...
                mediaSpecificDependencies.add(element.getTextTrim());
            }
        }
        return dependencies;
    }
    
    public String getTargetPackageName()
    {
        return targetPackageName;
    }

    public String getSourcePackageName()
    {
        return sourcePackageName;
    }
    
    public List<String> getCommonWebScripts() {
       if (this.commonWebScripts == null)
       {
           this.commonWebScripts = new ArrayList<String>();
       }
       return this.commonWebScripts;
    }

    public Map<String, List<String>> getCssDependencies()
    {
        if (this.cssDependencies == null)
        {
            this.cssDependencies = new HashMap<String, List<String>>();
        }
        return cssDependencies;
    }

    public List<String> getJsDependencies()
    {
        if (this.jsDependencies == null)
        {
            this.jsDependencies = new ArrayList<String>();
        }
        return jsDependencies;
    }

    public String getEvaluator()
    {
        return evaluator;
    }

    public Map<String, String> getEvaluatorProperties()
    {
        return evaluatorProperties;
    }
}
