/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.devenv.ant;

import java.io.File;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Ant task to verify an Eclipse classpath. Ensures that all referenced items in the 
 * .classpath file are present in a specified directory.
 * 
 * @author gavinc
 */
public class VerifyEclipseClasspath extends Task
{
   private boolean verbose;
   private File projectDirectory;
   private File libDirectory;
   
   private static final String CLASSPATH_FILE_NAME = ".classpath";
   private static final String ELEM_ENTRY = "classpathentry";
   private static final String ATTR_KIND = "kind";
   private static final String ATTR_PATH = "path";
   private static final String VALUE_KIND = "lib";
   
   public void setVerbose(boolean verbose)
   {
      this.verbose = verbose;
   }
   
   public void setProjectdir(File projectDirectory)
   {
      this.projectDirectory = projectDirectory;
   }
   
   public void setLibdir(File libDir)
   {
      this.libDirectory = libDir;
   }
   
   @SuppressWarnings("unchecked")
   public void execute() throws BuildException
   {
      if (this.projectDirectory == null)
      {
         throw new BuildException("You must specify the projectdir attribute");
      }
      
      // make sure the projectdir attribute points to a folder
      if (this.projectDirectory.isFile())
      {
         throw new BuildException("projectdir must specify a directory not a file!");
      }
      
      // if the lib directory has not been supplied use the same location as
      // the .classpath file, if it has been supplied make sure its a folder
      if (this.libDirectory == null)
      {
         this.libDirectory = projectDirectory;
      }
      else if (this.libDirectory.isFile())
      {
         throw new BuildException("libdir must specify a directory not a file!");
      }
      
      String searchBaseDir = this.libDirectory.getAbsolutePath();
      File classpathFile = new File(this.projectDirectory.getAbsoluteFile() + 
            File.separator + CLASSPATH_FILE_NAME);
         
      if (this.verbose)
      {
         log("Verifying Eclipse classpath file: " + classpathFile.getAbsolutePath());
         log("Using library directory: " + searchBaseDir);
      }
      
      try
      {
         // get the root element
         SAXReader reader = new SAXReader();
         Document document = reader.read(classpathFile);
         Element rootElement = document.getRootElement();

         StringBuilder missingItems = new StringBuilder();
         Iterator entries = rootElement.elementIterator(ELEM_ENTRY);
         while (entries.hasNext())
         {
            Element entry = (Element)entries.next();
            Attribute kind = entry.attribute(ATTR_KIND);
            if (kind != null && kind.getValue().equals(VALUE_KIND))
            {
               String searchPath = searchBaseDir + File.separator + entry.attributeValue(ATTR_PATH);
               File searchItem = new File(searchPath);
               
               if (this.verbose) log("Looking for library: " + searchPath);
               
               if (searchItem.exists())
               {
                  if (this.verbose) log("Found: " + searchPath);
               }
               else
               {
                  if (this.verbose) log("Did not find: " + searchPath);
                  
                  missingItems.append(searchPath);
                  missingItems.append("\n");
               }
            }
         }
         
         // output the result
         if (missingItems.length() > 0)
         {
            throw new BuildException("Invalid classpath found in " + 
                  classpathFile.getAbsolutePath() + "\nThe following items could not be located:\n" + 
                  missingItems.toString());
         }
         else
         {
            log(classpathFile.getAbsolutePath() + " is valid");
         }
      }
      catch (Throwable e)
      {
         String msg = null;
         if (this.verbose)
         {
            msg = e.toString();
         }
         else
         {
            msg = e.getMessage();
         }
         
         throw new BuildException(msg);
      }
   }
}
