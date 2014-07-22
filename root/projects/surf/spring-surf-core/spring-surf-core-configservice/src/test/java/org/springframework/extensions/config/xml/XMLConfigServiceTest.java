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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.ConfigLookupContext;
import org.springframework.extensions.config.source.ClassPathConfigSource;
import org.springframework.extensions.config.source.FileConfigSource;
import org.springframework.extensions.config.source.HTTPConfigSource;
import org.springframework.extensions.config.source.JarConfigSource;
import org.springframework.extensions.config.source.UrlConfigSource;
import org.springframework.extensions.config.util.BaseTest;
import org.springframework.extensions.config.xml.XMLConfigService;

/**
 * Unit tests for the XML based configuration service
 * 
 * @author gavinc
 */
@SuppressWarnings("unused")
public class XMLConfigServiceTest extends BaseTest
{
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * Tests the config.xml file
     */
    public void testConfig()
    {
        // setup the config service
        String configFile = getResourcesDir() + "config.xml";
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFile));
        svc.initConfig();

        // try and get the global item
        Config global = svc.getGlobalConfig();
        ConfigElement globalItem = global.getConfigElement("global-item");
        assertNotNull("globalItem should not be null", globalItem);
        assertEquals("The global-item value should be 'The global value'", "The global value", globalItem.getValue());

        // try and get the override item
        ConfigElement overrideItem = global.getConfigElement("override");
        assertNotNull("overrideItem should not be null", overrideItem);
        assertEquals("The override item should be false", "false", overrideItem.getValue());

        // test the string evaluator by getting the item config element
        // in the "Unit Test" config section
        Config unitTest = svc.getConfig("Unit Test");
        assertNotNull("unitTest config result should not be null", unitTest);
        ConfigElement item = unitTest.getConfigElement("item");
        assertNotNull("item should not be null", item);
        assertEquals("The item value should be 'The value'", "The value", item.getValue());

        // make sure the override value has changed when retrieved from item
        overrideItem = unitTest.getConfigElement("override");
        assertNotNull("overrideItem should not be null", overrideItem);
        assertEquals("The override item should now be true", "true", overrideItem.getValue());
    }
    
    /**
     * Tests the config.xml file properties
     */
    public void testConfigProperties()
    {
        // setup the config service
        String configFile = getResourcesDir() + "config-props.xml";
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFile));
        svc.setProperties(new Resource[] {new FileSystemResource(getResourcesDir() + "config-props.properties")});
        svc.init();

        // try and get the global item
        Config global = svc.getGlobalConfig();
        ConfigElement globalItem = global.getConfigElement("global-item");
        assertNotNull("globalItem should not be null", globalItem);
        assertEquals("The global-item value should be 'The global value'", "The global value", globalItem.getValue());
        ConfigElement globalItemProp = global.getConfigElement("global-item-prop");
        assertNotNull("globalItemProp should not be null", globalItemProp);
        assertEquals("The global-item value should be 'globalValue'", "globalValue", globalItemProp.getValue());
        ConfigElement globalItemMissingProp = global.getConfigElement("global-item-missing-prop");
        assertNotNull("globalItemMissingProp should not be null", globalItemMissingProp);
        assertEquals("The global-item value should be '${missingGlobalValue}'", "${missingGlobalValue}", globalItemMissingProp.getValue());

        // try and get the override item
        ConfigElement overrideItem = global.getConfigElement("override");
        assertNotNull("overrideItem should not be null", overrideItem);
        assertEquals("The override item should be false", "false", overrideItem.getValue());

        // test the string evaluator by getting the item config element
        // in the "Unit Test" config section
        Config unitTest = svc.getConfig("Unit Test");
        assertNotNull("unitTest config result should not be null", unitTest);
        ConfigElement item = unitTest.getConfigElement("item");
        assertNotNull("item should not be null", item);
        assertEquals("The item value should be 'The value'", "The value", item.getValue());
        ConfigElement itemProp = unitTest.getConfigElement("item-prop");
        assertNotNull("item should not be null", itemProp);
        assertEquals("The item value should be 'theValue'", "theValue", itemProp.getValue());
        String attrValue = itemProp.getAttribute("item-attr");
        assertNotNull("item attr should not be null", attrValue);
        assertEquals("The item attr value should be 'attrValue'", "attrValue", attrValue);
        ConfigElement itemMissingProp = unitTest.getConfigElement("item-missing-prop");
        assertNotNull("item should not be null", itemMissingProp);
        assertEquals("The item value should be '${missingTheValue}'", "${missingTheValue}", itemMissingProp.getValue());

        // make sure the override value has changed when retrieved from item
        overrideItem = unitTest.getConfigElement("override");
        assertNotNull("overrideItem should not be null", overrideItem);
        assertEquals("The override item should now be true", "true", overrideItem.getValue());
        ConfigElement overrideItemProp = unitTest.getConfigElement("override-prop");
        assertNotNull("overrideItem should not be null", overrideItemProp);
        assertEquals("The override item should now be true", "true", overrideItem.getValue());
        ConfigElement overrideItemMissingProp = unitTest.getConfigElement("override-missing-prop");
        assertNotNull("overrideItem should not be null", overrideItemMissingProp);
        assertEquals("The override item should now be true", "${missingTrue}", overrideItemMissingProp.getValue());
    }
    
    /**
     * Tests the absence of referenced files
     */
    public void testMissingFiles()
    {
        // setup the config service using a missing file source
        String configFile = "file:" + getResourcesDir() + "missing.xml";
        XMLConfigService svc = new XMLConfigService(new UrlConfigSource(configFile, true));
        svc.initConfig();

        // make sure attempts to retrieve config don't fail
        Config global = svc.getGlobalConfig();
        assertNotNull("Global config should not be null", global);
        assertEquals("There shouldn't be any config elements for global", 0, 
              global.getConfigElements().size());
        
        Config cfg = svc.getConfig("Nothing");
        assertNotNull("Config for Nothing should not be null", cfg);
        assertEquals("There shouldn't be any config elements for 'Nothing'", 0, 
              cfg.getConfigElements().size());
        
        // do the same test for a classpath resource
        configFile = "classpath:missing/missing.xml";
        svc = new XMLConfigService(new UrlConfigSource(configFile, true));
        svc.initConfig();

        // make sure attempts to retrieve config don't fail
        global = svc.getGlobalConfig();
        assertNotNull("Global config should not be null", global);
        assertEquals("There shouldn't be any config elements for global", 0, 
              global.getConfigElements().size());
        
        cfg = svc.getConfig("Nothing");
        assertNotNull("Config for Nothing should not be null", cfg);
        assertEquals("There shouldn't be any config elements for 'Nothing'", 0, 
              cfg.getConfigElements().size());
        
        // do the same test for a HTTP resource
        configFile = "http://localhost:8080/missing.xml";
        svc = new XMLConfigService(new UrlConfigSource(configFile, true));
        svc.initConfig();

        // make sure attempts to retrieve config don't fail
        global = svc.getGlobalConfig();
        assertNotNull("Global config should not be null", global);
        assertEquals("There shouldn't be any config elements for global", 0, 
              global.getConfigElements().size());
        
        cfg = svc.getConfig("Nothing");
        assertNotNull("Config for Nothing should not be null", cfg);
        assertEquals("There shouldn't be any config elements for 'Nothing'", 0, 
              cfg.getConfigElements().size());
    }
    
    /**
     * Tests the retrieval of a named child
     */
    public void testGetNamedChild()
    {
       // setup the config service
        String configFile = getResourcesDir() + "config.xml";
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFile));
        svc.initConfig();
        
        // get the "Named Child Test" config
        Config cfg = svc.getConfig("Named Child Test");
        assertNotNull("Named child test config should not be null", cfg);
        
        // get the children config element
        ConfigElement children = cfg.getConfigElement("children");
        // check the getNumberOfChildren method works
        assertEquals("There should be four children", 4, children.getChildCount());
        
        // try and get a named child
        ConfigElement childTwo = children.getChild("child-two");
        assertNotNull("Child two config element should not be null", childTwo);
        assertEquals("Child two value should be 'child two value'", "child two value", 
              childTwo.getValue());
        assertEquals("The number of attributes should be 0", 0, childTwo.getAttributeCount());
        
        // try and get a non existent child and check its null
        ConfigElement noChild = children.getChild("not-there");
        assertNull("The noChild config element should be null", noChild);
        
        // test the retrieval of grand children
        ConfigElement childThree = children.getChild("child-three");
        assertNotNull("Child three config element should not be null", childThree);
        ConfigElement grandKids = childThree.getChild("grand-children");
        assertNotNull("Grand child config element should not be null", grandKids);
        assertEquals("There should be 2 grand child config elements", 2, 
              grandKids.getChildCount());
        ConfigElement grandKidOne = grandKids.getChild("grand-child-one");
        assertNotNull("Grand child one config element should not be null", grandKidOne);
        assertEquals("The number of attributes for grand child one should be 1", 
              1, grandKidOne.getAttributeCount());
        assertEquals("The number of children for grand child one should be 0", 
              0, grandKidOne.getChildCount());
        assertTrue("The attribute 'an-attribute' should be present", 
              grandKidOne.getAttribute("an-attribute") != null);
    }
    
    /**
     * Tests the config service's ability to reset
     */
    public void testReset()
    {
       // setup the config service
        String configFile = getResourcesDir() + "config.xml";
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFile));
        svc.initConfig();

        // try and get the global item
        Config unitTest = svc.getConfig("Unit Test");
        assertNotNull("unitTest should not be null", unitTest);
        
        // reset the config service then try to retrieve some config again
        svc.reset();
        unitTest = svc.getConfig("Unit Test");
        assertNotNull("unitTest should not be null", unitTest);
    }

    /**
     * Tests the use of the class path source config
     * 
     * TODO: Enable this test when we have a classpath config resource to load!
     */
    public void xtestClasspathSource()
    {
        String configFile = "org/configservice/config-classpath.xml"; 
        XMLConfigService svc = new XMLConfigService(new ClassPathConfigSource(configFile));
        svc.initConfig();
        
        Config config = svc.getGlobalConfig();
        assertNotNull(config);
    }
    
    /**
     * Tests the use of the HTTP source config
     * 
     * TODO: Enable this test when we have an HTTP config resource to load!
     */
    public void xtestHTTPSource()
    {
        List<String> configFile = new ArrayList<String>(1);
        configFile.add("http://localhost:8080/configservice/config-http.xml");
        XMLConfigService svc = new XMLConfigService(new HTTPConfigSource(configFile));
        svc.initConfig();
        
        Config config = svc.getGlobalConfig();
        assertNotNull(config);
    }
    
    /**
     * Tests loading config from a known JAR file
     */
    public void testJarSource()
    {
       String resDir = this.getResourcesDir();
       String jarFile = "jar:file:" + resDir + "custom-config.jar!/META-INF/web-client-config-custom.xml";
       JarConfigSource source = new JarConfigSource(jarFile);
       XMLConfigService svc = new XMLConfigService(source);
       svc.initConfig();
      
       // make sure the global config is present
       Config config = svc.getGlobalConfig();
       assertNotNull(config);
       
       // make sure the from address is present and correct
       ConfigElement clientElem = config.getConfigElement("client");
       assertNotNull(clientElem);
       ConfigElement fromAddressElem = clientElem.getChild("from-email-address");
       assertNotNull(fromAddressElem);
       String fromAddress = fromAddressElem.getValue();
       assertEquals("From address should be 'me@somewhere.net'", "me@somewhere.net", fromAddress);
    }
    
    /**
     * Tests loading a file from within any JAR file on the classpath
     * 
     * TODO: This needs the JAR file to be in the classpath when run via ant i.e.
     *       the continuous build, so until we have a way to include adhoc items
     *       disable this test.
     */
    public void xtestMultiJarSource()
    {
       List<String> configFiles = new ArrayList<String>(2);
       configFiles.add("file:" + getResourcesDir() + "config.xml");
       configFiles.add("jar:*!/META-INF/web-client-config-custom.xml");
       UrlConfigSource configSrc = new UrlConfigSource(configFiles, true);
       XMLConfigService svc = new XMLConfigService(configSrc);
       svc.initConfig();
       
       // try and get the global config section
       Config globalSection = svc.getGlobalConfig();

       // try and get items from the global section defined in each file
       ConfigElement globalItem = globalSection.getConfigElement("global-item");
       assertNotNull("globalItem should not be null", globalItem);
       assertEquals("The global-item value should be 'The global value'", "The global value", globalItem.getValue());
       
       // make sure the from address is present and correct
       ConfigElement clientElem = globalSection.getConfigElement("client");
       assertNotNull(clientElem);
       ConfigElement fromAddressElem = clientElem.getChild("from-email-address");
       assertNotNull(fromAddressElem);
       String fromAddress = fromAddressElem.getValue();
       assertEquals("From address should be 'me@somewhere.net'", "me@somewhere.net", fromAddress);
    }
    
    /**
     * Tests the config service's ability to load multiple files and merge the
     * results
     */
    public void testMultiConfig()
    {
        // setup the config service
        List<String> configFiles = new ArrayList<String>(2);
        configFiles.add(getResourcesDir() + "config.xml");
        configFiles.add(getResourcesDir() + "config-multi.xml");
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
        svc.initConfig();

        // try and get the global config section
        Config globalSection = svc.getGlobalConfig();

        // try and get items from the global section defined in each file
        ConfigElement globalItem = globalSection.getConfigElement("global-item");
        assertNotNull("globalItem should not be null", globalItem);
        assertEquals("The global-item value should be 'The global value'", "The global value", globalItem.getValue());

        ConfigElement globalItem2 = globalSection.getConfigElement("another-global-item");
        assertNotNull("globalItem2 should not be null", globalItem2);
        assertEquals("The another-global-item value should be 'Another global value'", "Another global value",
                globalItem2.getValue());

        // lookup the "Unit Test" section, this should match a section in each
        // file so
        // we should be able to get hold of config elements "item" and
        // "another-item"
        Config unitTest = svc.getConfig("Unit Test");
        assertNotNull("unitTest should not be null", unitTest);
        ConfigElement item = unitTest.getConfigElement("item");
        assertNotNull("item should not be null", item);
        ConfigElement anotherItem = unitTest.getConfigElement("another-item");
        assertNotNull("another-item should not be null", anotherItem);
    }

    /**
     * Tests the config service's ability to restrict searches to a named area
     */
    public void testAreaConfig()
    {
        // setup the config service
        List<String> configFiles = new ArrayList<String>(2);
        configFiles.add(getResourcesDir() + "config.xml");
        configFiles.add(getResourcesDir() + "config-areas.xml");
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
        svc.initConfig();

        // try and get a section defined in an area (without restricting the
        // area), the result should be null
        Config config = svc.getConfig("Restricted Area Test");
        ConfigElement restrictedElement = config.getConfigElement("restricted");
        ConfigElement availableElement = config.getConfigElement("available");
        assertNull("restrictedElement should be null as a global lookup was performed for a section in an area", restrictedElement);
        assertNotNull("availableElement should not be null as the element is available in the default area", availableElement);

        // try and get an section defined in an area (with an area restricted
        // search)
        ConfigLookupContext lookupContext = new ConfigLookupContext();
        lookupContext.addArea("test-area");
        config = svc.getConfig("Area Specific Config", lookupContext);
        ConfigElement areaTest = config.getConfigElement("parent-item");
        assertNotNull("areaTest should not be null as it is defined in test-area", areaTest);

        // try and find a section defined outside an area with an area
        // restricted search
        config = svc.getConfig("Unit Test", lookupContext);
        ConfigElement unitTest = config.getConfigElement("item");
        assertNull("unitTest should be null as it is not defined in test-area", unitTest);

        // try and find some config in area that has not been defined, ensure we
        // get an error
        try
        {
            Config notThere = svc.getConfig("Unit Test", new ConfigLookupContext("not-there"));
            fail("Retrieving a non existent area should have thrown an exception!");
        }
        catch (ConfigException ce)
        {
            // expected to get this error
        }
    }

    /**
     * Tests the merge features of the config service
     */
    public void testMerging()
    {
        // setup the config service
        List<String> configFiles = new ArrayList<String>(2);
        configFiles.add(getResourcesDir() + "config.xml");
        configFiles.add(getResourcesDir() + "config-multi.xml");
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
        svc.initConfig();
        
        // try and get the global config section
        Config globalSection = svc.getGlobalConfig();
        assertNotNull("global section should not be null", globalSection);
        
        // make sure that the override config value got overridden in the global
        // section
        ConfigElement overrideItem = globalSection.getConfigElement("override");
        assertNotNull("overrideItem should not be null", overrideItem);
        assertEquals("The override item should be true", "true", overrideItem.getValue());
        
        // make sure the global section gets merged properly
        ConfigElement mergeChildren = globalSection.getConfigElement("merge-children");
        assertNotNull("mergeChildren should not be null", mergeChildren);
        List<ConfigElement> kids = mergeChildren.getChildren();
        assertEquals("There should be 2 children", 2, kids.size());
        
        // get the merge test config section
        Config mergeTest = svc.getConfig("Merge Test");
        assertNotNull("Merge test config should not be null", mergeTest);
        
        // check that there is a first, second, thrid and fourth config element
        ConfigElement first = mergeTest.getConfigElement("first-item");
        ConfigElement second = mergeTest.getConfigElement("second-item");
        ConfigElement third = mergeTest.getConfigElement("third-item");
        ConfigElement fourth = mergeTest.getConfigElement("fourth-item");
        assertNotNull("first should not be null", first);
        assertNotNull("second should not be null", second);
        assertNotNull("third should not be null", third);
        assertNotNull("fourth should not be null", fourth);
        
        // test that the first-item got overridden
        String firstValue = first.getValue();
        assertEquals("The first value is wrong", "the overridden first value", firstValue);
        
        // test that there are two child items under the children config element
        ConfigElement children = mergeTest.getConfigElement("children");
        assertNotNull("children should not be null", children);
        kids = children.getChildren();
        assertEquals("There should be 3 children", 3, kids.size());
    }
    
    /**
     * Tests the replace feature of the config service
     */
    public void testReplace()
    {
        // setup the config service
        List<String> configFiles = new ArrayList<String>(2);
        configFiles.add(getResourcesDir() + "config.xml");
        configFiles.add(getResourcesDir() + "config-replace.xml");
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
        svc.initConfig();
        
        // try and get the global config section
        Config globalSection = svc.getGlobalConfig();
        assertNotNull("global section should not be null", globalSection);

        // make sure the global-item value has changed
        ConfigElement globalItem = globalSection.getConfigElement("global-item");
        assertEquals("global-item", "The replaced global value", globalItem.getValue());
        
        // make sure the override element is still present with the same value
        ConfigElement override = globalSection.getConfigElement("override");
        assertNotNull("override should not be null", override);
        assertEquals("override element", "false", override.getValue());
        
        // make sure the children-replace element only has 1 child and not 4
        ConfigElement childrenReplace = globalSection.getConfigElement("children-replace");
        assertNotNull("childrenReplace should not be null", childrenReplace);
        List<ConfigElement> children = childrenReplace.getChildren();
        assertEquals("number of children elements", 1, children.size());
        
        // make sure the child is the correct one
        ConfigElement customChild = children.get(0);
        assertEquals("custom child element value", "child custom value", customChild.getValue());
        
        // make sure the config section is still present
        Config replaceTestCfg = svc.getConfig("Replace Test");
        assertNotNull("Replace Test should not be null", replaceTestCfg);
        
        // make sure there are 9 elements in the replaced section (including the global section)
        Map<String, ConfigElement> elements = replaceTestCfg.getConfigElements();
        assertEquals("number of elements", 9, elements.size());
        
        // make sure first-item is different
        assertEquals("first-item", "the replaced first value", replaceTestCfg.
              getConfigElement("first-item").getValue());
        
        // make sure second-item is the same
        assertEquals("second-item", "second value", replaceTestCfg.
              getConfigElement("second-item").getValue());
        
        // make sure there is a fourth-item is now present
        assertEquals("fourth-item", "new fourth value", replaceTestCfg.
              getConfigElement("fourth-item").getValue());
        
        // make sure the children config now has 2 children
        ConfigElement childrenElement = replaceTestCfg.getConfigElement("children");
        assertEquals("number of children of children", 2, childrenElement.getChildCount());
        
        // make sure the two child elements are correct
        assertEquals("child two name", "child-two", childrenElement.getChildren().get(0).getName());
        assertEquals("child two value", "child two value", childrenElement.getChildren().get(0).getValue());
        assertEquals("child three name", "child-three", childrenElement.getChildren().get(1).getName());
        assertEquals("child three value", "child three value", childrenElement.getChildren().get(1).getValue());
    }
}
