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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

/**
 * <p>Representation of the configuration of a module that provides an extension. Modules consist
 * of {@link Customization} and {@link AdvancedComponent} instances. They can optionally be configured
 * with a {@link ExtensionModuleEvaluator} that determines whether or not the module should be 
 * applied to a request.</p>
 * 
 * @author David Draper
 */
public class BasicExtensionModule 
{
    private static final Log logger = LogFactory.getLog(BasicExtensionModule.class);
    public static final String ID = "id";
    public static final String AUTO_DEPLOY = "auto-deploy";
    public static final String DESCRIPTION = "description";
    public static final String VERSION = "version";
    public static final String AUTO_DEPLOY_INDEX = "auto-deploy-index";
    public static final String EVALUATOR = "evaluator";
    public static final String EVALUATOR_TYPE = "type";
    public static final String EVALUATOR_PROPS = "params";
    public static final String COMPONENTS = "components";
    public static final String COMPONENT = "component";
    public static final String CUSTOMIZATIONS = "customizations";
    public static final String CUSTOMIZATION = "customization";
    public static final String CONFIGURATIONS = "configurations";
    
    private String id = null;
    private boolean autoDeploy = false;
    private String description = null;
    private String version = null;
    private String autoDeployIndex = null;
    
    private String evaluator = null;
    private Map<String, String> evaluatorProperties = new HashMap<String, String>();
    
    private List<Customization> customizations = new ArrayList<Customization>();
    
    private List<Element> configurations = new ArrayList<Element>();
    
    @SuppressWarnings("unchecked")
    public BasicExtensionModule(Element element)
    {
        // Get the id and description...
        this.id = XMLHelper.getStringData(ID, element, true);
        this.autoDeploy = Boolean.valueOf(XMLHelper.getStringData(AUTO_DEPLOY, element, false)).booleanValue();
        if (this.id != null)
        {
            this.description = XMLHelper.getStringData(DESCRIPTION, element, false);
            this.version = XMLHelper.getStringData(VERSION, element, false);
            this.autoDeployIndex = XMLHelper.getStringData(AUTO_DEPLOY_INDEX, element, false);
            
            // Parse the module evaluation configuration... 
            Element evaluatorEl = element.element(EVALUATOR);
            if (evaluatorEl != null)
            {
                this.evaluator = evaluatorEl.attributeValue(EVALUATOR_TYPE);
                this.evaluatorProperties = XMLHelper.getProperties(EVALUATOR_PROPS, evaluatorEl);
            }
            
            // Parse all the customization configuration...
            List<Element> customizationsList = element.elements(CUSTOMIZATIONS);
            for(Element customizationsEl: customizationsList)
            {
                List<Element> customizationList = customizationsEl.elements(CUSTOMIZATION);
                for (Element customizationEl: customizationList)
                {
                    customizations.add(new Customization(customizationEl));
                }
            }
            
            // Get all configurations provided by the module. It is not necessary to do anything with them at this stage as 
            // all processing will be done by the config service...
            this.configurations = element.elements(CONFIGURATIONS);
        }
        else
        {
            if (logger.isErrorEnabled())
            {
                logger.error("A <module> was found with no identification");
            }
        }
    }
    
    public String getId()
    {
        return this.id;
    }
    
    public boolean isAutoDeploy()
    {
        return this.autoDeploy;
    }
    
    public String getDescription()
    {
        return this.description;
    }
    
    public String getVersion()
    {
        return this.version;
    }
    
    public String getAutoDeployIndex()
    {
        return this.autoDeployIndex;
    }
    
    public String getEvaluator()
    {
        return this.evaluator;
    }
    
    public Map<String, String> getEvaluatorProperties()
    {
        return this.evaluatorProperties;
    }
    
    public List<Customization> getCustomizations()
    {
        return this.customizations;
    }
    
    public List<Element> getConfigurations()
    {
        return this.configurations;
    }
}
