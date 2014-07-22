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

package org.springframework.extensions.config.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.Constants;
import org.springframework.core.io.Resource;
import org.springframework.extensions.config.BaseConfigService;
import org.springframework.extensions.config.ConfigDeployer;
import org.springframework.extensions.config.ConfigDeployment;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.ConfigSection;
import org.springframework.extensions.config.ConfigSectionImpl;
import org.springframework.extensions.config.ConfigSource;
import org.springframework.extensions.config.evaluator.Evaluator;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;
import org.springframework.extensions.config.xml.elementreader.GenericElementReader;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringValueResolver;

/**
 * XML based configuration service.
 * <p/>
 * The sytem properties can be used; to override entries in the properties files, act as fallback values or be ignored.
 * <ul>
 *   <li><b>SYSTEM_PROPERTIES_MODE_NEVER:             </b>Don't use system properties at all.</li>
 *   <li><b>SYSTEM_PROPERTIES_MODE_FALLBACK:          </b>Fallback to a system property only for undefined properties.</li>
 *   <li><b>SYSTEM_PROPERTIES_MODE_OVERRIDE: (DEFAULT)</b>Use a system property if it is available.</li>
 * </ul>
 * 
 * @author gavinc
 * @author David Draper
 */
public class XMLConfigService extends BaseConfigService implements XMLConfigConstants
{
    private static final Log logger = LogFactory.getLog(XMLConfigService.class);

    private static final Constants constants = new Constants(PropertyPlaceholderConfigurer.class);

    private Resource[] propertyLocations;
    private int systemPropertiesMode = PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE;
    private PropertyConfigurer propertyConfigurer;
    private Map<String, ConfigElementReader> elementReaders;
    
    /**
     * Constructs an XMLConfigService using the given config source
     * 
     * @param configSource
     *            A ConfigSource
     */
    public XMLConfigService(ConfigSource configSource)
    {
        super(configSource);
    }

	/**
	 * Set locations of properties files to be loaded.
	 * <p>Can point to classic properties files or to XML files
	 * that follow JDK 1.5's properties XML format.
	 */
	public void setProperties(Resource[] locations)
	{
	    this.propertyLocations = locations;
	}

    /**
     * Set the system property mode by the name of the corresponding constant,
     * e.g. "SYSTEM_PROPERTIES_MODE_OVERRIDE".
     * @param constantName name of the constant
     * @throws java.lang.IllegalArgumentException if an invalid constant was specified
     * @see #setSystemPropertiesMode
     */
    public void setSystemPropertiesModeName(String constantName) throws IllegalArgumentException
    {
        this.systemPropertiesMode = constants.asNumber(constantName).intValue();
    }

	public List<ConfigDeployment> initConfig()
    {
        if (logger.isDebugEnabled())
            logger.debug("Commencing initialisation");

        List<ConfigDeployment> configDeployments = super.initConfig();

        // initialise property configurer
        propertyConfigurer = null;
        if (propertyLocations != null)
        {
           PropertyConfigurer configurer = new PropertyConfigurer();
           configurer.setLocations(propertyLocations);
           configurer.setIgnoreUnresolvablePlaceholders(true);
           configurer.setSystemPropertiesMode(systemPropertiesMode);
           configurer.init();
           propertyConfigurer = configurer;
        }

        // initialise the element readers map with built-in readers
        putElementReaders(new HashMap<String, ConfigElementReader>());

        List<ConfigDeployment> deployments = parse();
        configDeployments.addAll(deployments);
                
        // append additional config, if any, in deterministic order sorted by sort key
        for (ConfigDeployer configDeployer : configDeployers.values())
        {
        	deployments = configDeployer.initConfig();
        	configDeployments.addAll(deployments);
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Completed initialisation");
        
        return configDeployments;
    }
    
    public void destroy()
    {
        removeElementReaders();
        super.destroy();
    }

    /**
     * <p>Parse the supplied XML element</p>
     * @param rootElement
     * @param parsedElementReaders
     * @param parsedEvaluators
     * @param parsedConfigSections
     * @return
     */
    public String parseFragment(Element rootElement,
                              Map<String, ConfigElementReader> parsedElementReaders,
                              Map<String, Evaluator> parsedEvaluators,
                              List<ConfigSection> parsedConfigSections)
    {
        // see if there is an area defined
        String currentArea = rootElement.attributeValue("area");

        // parse the plug-ins section of a config file
        Element pluginsElement = rootElement.element(ELEMENT_PLUG_INS);
        if (pluginsElement != null)
        {
            // parse the evaluators section
            Map<String, Evaluator> evaluators = parseEvaluatorsElement(pluginsElement.element(ELEMENT_EVALUATORS));
            if (evaluators != null)
            {
                parsedEvaluators.putAll(evaluators);
            }

            // parse the element readers section
            Map<String, ConfigElementReader> elementReaders = parseElementReadersElement(pluginsElement.element(ELEMENT_ELEMENT_READERS));
            if (elementReaders != null)
            {
                parsedElementReaders.putAll(elementReaders);
            }
        }

        // parse each config section in turn
        @SuppressWarnings("unchecked")
        Iterator<Element> configElements = rootElement.elementIterator(ELEMENT_CONFIG);
        while (configElements.hasNext())
        {
            Element configElement = configElements.next();
            parsedConfigSections.add(parseConfigElement(parsedElementReaders, configElement, currentArea));
        }
        
        return currentArea;
    }
    
    protected void parse(InputStream stream)
    {
    	Map<String, ConfigElementReader> parsedElementReaders = new HashMap<String, ConfigElementReader>();
    	Map<String, Evaluator> parsedEvaluators = new HashMap<String, Evaluator>();
    	List<ConfigSection> parsedConfigSections = new ArrayList<ConfigSection>();
    	
    	String currentArea = null;
        try
        {
            // get the root element
            SAXReader reader = new SAXReader();
            Document document = reader.read(stream);
            Element rootElement = document.getRootElement();
            currentArea = parseFragment(rootElement, parsedElementReaders, parsedEvaluators, parsedConfigSections);
        }
        catch (Throwable e)
        {
            if (e instanceof ConfigException)
            {
               throw (ConfigException)e;
            }
            else
            {
               throw new ConfigException("Failed to parse config stream", e);
            }
        }
        
        try 
        {
            // valid for this stream, now add to config service ...
            
            if (parsedEvaluators != null)
            {
                for (Map.Entry<String, Evaluator> entry : parsedEvaluators.entrySet())
                {
                    // add the evaluators to the config service
                    addEvaluator(entry.getKey(), entry.getValue());
                }
            }
            
            if (parsedElementReaders != null)
            {
                for (Map.Entry<String, ConfigElementReader> entry : parsedElementReaders.entrySet())
                {
                    // add the element readers to the config service
                    addConfigElementReader(entry.getKey(), entry.getValue());
                }
            }
            
            if (parsedConfigSections != null)
            {
                for (ConfigSection section : parsedConfigSections)
                {
                    // add the config sections to the config service
                    addConfigSection(section, currentArea);
                }
            }
	    }
	    catch (Throwable e)
	    {
	        throw new ConfigException("Failed to add config to config service", e);
	    }
    }


    /**
     * Parses the evaluators element
     * 
     * @param evaluatorsElement
     */
    private Map<String, Evaluator> parseEvaluatorsElement(Element evaluatorsElement)
    {
        if (evaluatorsElement != null)
        {
            Map<String, Evaluator> parsedEvaluators = new HashMap<String, Evaluator>();
            @SuppressWarnings("unchecked")
            Iterator<Element> evaluators = evaluatorsElement.elementIterator();
            while (evaluators.hasNext())
            {
                Element evaluatorElement = evaluators.next();
                String evaluatorName = evaluatorElement.attributeValue(ATTR_ID);
                String evaluatorClass = evaluatorElement.attributeValue(ATTR_CLASS);

                // TODO: Can these checks be removed if we use a DTD and/or
                // schema??
                if (evaluatorName == null || evaluatorName.length() == 0)
                {
                    throw new ConfigException("All evaluator elements must define an id attribute");
                }

                if (evaluatorClass == null || evaluatorClass.length() == 0)
                {
                    throw new ConfigException("Evaluator '" + evaluatorName + "' must define a class attribute");
                }

                // add the evaluator
                parsedEvaluators.put(evaluatorName, createEvaluator(evaluatorName, evaluatorClass));
            }
            
            return parsedEvaluators;
        }
        
        return null;
    }

    /**
     * Parses the element-readers element
     * 
     * @param readersElement
     */
    private Map<String, ConfigElementReader> parseElementReadersElement(Element readersElement)
    {
        if (readersElement != null)
        {
            Map<String, ConfigElementReader> parsedElementReaders = new HashMap<String, ConfigElementReader>();
            @SuppressWarnings("unchecked")
            Iterator<Element> readers = readersElement.elementIterator();
            while (readers.hasNext())
            {
                Element readerElement = readers.next();
                String readerElementName = readerElement.attributeValue(ATTR_ELEMENT_NAME);
                String readerElementClass = readerElement.attributeValue(ATTR_CLASS);

                if (readerElementName == null || readerElementName.length() == 0)
                {
                    throw new ConfigException("All element-reader elements must define an element-name attribute");
                }

                if (readerElementClass == null || readerElementClass.length() == 0)
                {
                    throw new ConfigException("Element-reader '" + readerElementName
                            + "' must define a class attribute");
                }

                // add the element reader
                parsedElementReaders.put(readerElementName, createConfigElementReader(readerElementName, readerElementClass));
            }
            
            return parsedElementReaders;
        }
        
        return null;
    }

    /**
     * Parses a config element of a config file
     * 
     * @param configElement The config element
     * @param currentArea The current area
     */
    private ConfigSection parseConfigElement(Map<String, ConfigElementReader> parsedElementReaders, Element configElement, String currentArea)
    {
        if (configElement != null)
        {
            boolean replace = false;
            String evaluatorName = configElement.attributeValue(ATTR_EVALUATOR);
            String condition = configElement.attributeValue(ATTR_CONDITION);
            String replaceValue = configElement.attributeValue(ATTR_REPLACE);
            if (replaceValue != null && replaceValue.equalsIgnoreCase("true"))
            {
               replace = true;
            }

            // create the section object
            ConfigSectionImpl section = new ConfigSectionImpl(evaluatorName, condition, replace);

            // retrieve the config elements for the section
            @SuppressWarnings("unchecked")
            Iterator<Element> children = configElement.elementIterator();
            while (children.hasNext())
            {
                Element child = children.next();
                String elementName = child.getName();

                // get the element reader for the child
                ConfigElementReader elementReader = null;
                if (parsedElementReaders != null)
                {
                	elementReader = parsedElementReaders.get(elementName);
                }
                
                if (elementReader == null)
                {
                	elementReader = getConfigElementReader(elementName);
                }
                
                if (logger.isDebugEnabled())
                    logger.debug("Retrieved element reader " + elementReader + " for element named '" + elementName
                            + "'");

                if (elementReader == null)
                {
                    elementReader = new GenericElementReader(propertyConfigurer);

                    if (logger.isDebugEnabled())
                        logger.debug("Defaulting to " + elementReader + " as there wasn't an element "
                                + "reader registered for element '" + elementName + "'");
                }

                ConfigElement cfgElement = elementReader.parse(child);
                section.addConfigElement(cfgElement);

                if (logger.isDebugEnabled())
                    logger.debug("Added " + cfgElement + " to " + section);
            }
            
            return section;
        }
        
        return null;
    }

    /**
     * Adds the config element reader to the config service
     * 
     * @param name
     *            Name of the element
     * @param elementReader
     *            The element reader
     */
    private void addConfigElementReader(String elementName, ConfigElementReader elementReader)
    {
        putConfigElementReader(elementName, elementReader);

        if (logger.isDebugEnabled())
            logger.debug("Added element reader '" + elementName + "': " + elementReader.getClass().getName());
    }
    
    /**
     * Instantiate the config element reader with the given name and class
     * 
     * @param name
     *            Name of the element
     * @param className
     *            Class name of the element reader
     */
    private ConfigElementReader createConfigElementReader(String elementName, String className)
    {
        ConfigElementReader elementReader = null;

        try
        {
            @SuppressWarnings("unchecked")
            Class clazz = Class.forName(className);
            elementReader = (ConfigElementReader) clazz.newInstance();
        }
        catch (Throwable e)
        {
            throw new ConfigException("Could not instantiate element reader for '" + elementName + "' with class: "
                    + className, e);

        }

        return elementReader;
    }

    /**
     * Gets the element reader from the in-memory 'cache' for the given element name
     * 
     * @param elementName Name of the element to get the reader for
     * @return ConfigElementReader object or null if it doesn't exist
     */
    private ConfigElementReader getConfigElementReader(String elementName)
    {
        return (ConfigElementReader) getElementReaders().get(elementName);
    }
    
    /**
     * Put the config element reader into the in-memory 'cache' for the given element name
     * 
     * @param elementName
     * @param elementReader
     */
    private void putConfigElementReader(String elementName, ConfigElementReader elementReader)
    {
        getElementReaders().put(elementName, elementReader);
    }
    
    /**
     * Get the elementReaders from the in-memory 'cache'
     * 
     * @return elementReaders
     */
    protected Map<String, ConfigElementReader> getElementReaders()
    {
        return elementReaders;
    }  
    
    /**
     * Put the elementReaders into the in-memory 'cache'
     * 
     * @param elementReaders
     */
    protected void putElementReaders(Map<String, ConfigElementReader> elementReaders)
    {
        this.elementReaders = elementReaders;
    }  
    
    /**
     * Remove the elementReaders from the in-memory 'cache'
     */
    protected void removeElementReaders()
    {
        elementReaders.clear();
        elementReaders = null;
    } 
        
    /**
     * Provides access to property values 
     */
    public static class PropertyConfigurer extends PropertyPlaceholderConfigurer
    {
		private PlaceholderResolvingStringValueResolver resolver;
        
        /**
         * Initialise
         */
        /*package*/ void init()
        {
            try
            {
                Properties properties = mergeProperties();
                this.resolver = new PlaceholderResolvingStringValueResolver(properties, DEFAULT_PLACEHOLDER_PREFIX, DEFAULT_PLACEHOLDER_SUFFIX, DEFAULT_VALUE_SEPARATOR, true);
            }
            catch(IOException e)
            {
                throw new ConfigException("Failed to retrieve properties", e);
            }
        }

        /**
         * Resolve values
         * 
         * @param value
         * @return resolved value
         */
        public String resolveValue(String val)
        {
        	return resolver.resolveStringValue(val);
        }
    }
    
	/**
	 * BeanDefinitionVisitor that resolves placeholders in String values,
	 * delegating to the <code>parseStringValue</code> method of the
	 * containing class.
	 */
    public static class PlaceholderResolvingStringValueResolver implements StringValueResolver
    {
		private final PropertyPlaceholderHelper helper;
		private final Properties props;

		public PlaceholderResolvingStringValueResolver(Properties props, String placeholderPrefix, String placeholderSuffix, String valueSeparator, boolean ignoreUnresolvablePlaceholders) 
		{
			this.helper = new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
			this.props = props;
		}

		public String resolveStringValue(String strVal) throws BeansException 
		{
			String value = this.helper.replacePlaceholders(strVal, props);
			return value;
		}
	}
}
