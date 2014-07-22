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

package org.springframework.extensions.config.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.extensions.config.source.FileConfigSource;
import org.springframework.extensions.config.xml.XMLConfigService;

import junit.framework.TestCase;

/**
 * Base class for all JUnit tests
 * 
 * @author gavinc, Neil McErlean
 */
public abstract class BaseTest extends TestCase
{
   private String resourcesDir;
   
   public BaseTest()
   {
	   /*
      this.resourcesDir = System.getProperty("user.dir") + File.separator + "source" + 
                          File.separator + "test-resources" + File.separator;
       */
	   this.resourcesDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + 
	   					   File.separator + "resources" + File.separator;
   }
   
   public String getResourcesDir()
   {
      return this.resourcesDir;
   }

    protected void assertFileIsValid(String fullFileName)
    {
        File f = new File(fullFileName);
        assertTrue("Required file missing: " + fullFileName, f.exists());
        assertTrue("Required file not readable: " + fullFileName, f.canRead());
    }
    
    protected XMLConfigService initXMLConfigService(String xmlConfigFile)
    {
        String fullFileName = getResourcesDir() + xmlConfigFile;
        assertFileIsValid(fullFileName);
    
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(
                fullFileName));
        svc.initConfig();
        return svc;
    }
    
    protected XMLConfigService initXMLConfigService(String xmlConfigFile, String overridingXmlConfigFile)
    {
        List<String> files = new ArrayList<String>(2);
        files.add(xmlConfigFile);
        files.add(overridingXmlConfigFile);
        return initXMLConfigService(files);
    }

    protected XMLConfigService initXMLConfigService(List<String> xmlConfigFilenames)
    {
        List<String> configFiles = new ArrayList<String>();
        for (String filename : xmlConfigFilenames)
        {
            String path = getResourcesDir() + filename;
            assertFileIsValid(path);
            configFiles.add(path);
        }
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
        svc.initConfig();
        return svc;
    }
}
