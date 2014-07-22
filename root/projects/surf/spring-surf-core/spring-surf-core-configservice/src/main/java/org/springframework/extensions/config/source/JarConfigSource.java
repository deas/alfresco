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

package org.springframework.extensions.config.source;

import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ConfigSource implementation that gets its data via a file within a JAR file.
 * <p>
 * The location passed to the constructor should be in the default url format
 * for referencing a file in a JAR file i.e. jar:file:/c:\lib\file.jar!/META-INF/web-client-config-custom.xml
 * </p>
 * 
 * @author gavinc
 */
public class JarConfigSource extends BaseConfigSource
{
   public static final String JAR_PROTOCOL = "jar";
   public static final String JAR_PATH_SEPARATOR = "!/";
   
   private static final Log logger = LogFactory.getLog(JarConfigSource.class);
   
   /**
    * Constructs a JarConfigSource
    * 
    * @param location The location of the file within a JAR
    */
   public JarConfigSource(String location)
   {
      super(Collections.singletonList(location));
   }
   
   @Override
   protected InputStream getInputStream(String sourceString)
   {
      InputStream in = null;
      
      try
      {
         // make sure the source string is valid
         if (sourceString.startsWith(JAR_PROTOCOL) == false)
         {
            throw new IllegalArgumentException("sourceString must start with \"" + JAR_PROTOCOL + ":\"");
         }
         
         int indexSep = sourceString.indexOf(JAR_PATH_SEPARATOR);
         if (indexSep == -1)
         {
            throw new IllegalArgumentException("sourceString must contain an entry within the JAR file i.e. jar:file:/[file]!/[entry]");
         }
         
         // extract the entry part from source string
         String entryStr = sourceString.substring(indexSep + JAR_PATH_SEPARATOR.length());
         
         // open the JAR file
         URL url = new URL(sourceString);
         URLConnection conn = url.openConnection();
         if (conn instanceof JarURLConnection)
         {
            // open the jar file, if it does not contain the entry requested a FileNotFound exception
            // is thrown and reported below
            JarFile jar = ((JarURLConnection)conn).getJarFile();
            ZipEntry entry = jar.getEntry(entryStr);
            if (entry != null)
            {
               in = jar.getInputStream(entry);
            }
         }
      }
      catch (Exception e)
      {
         if (logger.isDebugEnabled())
            logger.debug("Failed to obtain input stream to URL: " + sourceString, e);
      }
      
      return in;
   }
}
