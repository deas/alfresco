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

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Ant task to verify an Eclipse classpath. Ensures that all referenced items in the .classpath file are present in a
 * specified directory.
 * 
 * @author gavinc
 */
public class CopyLibrariesFromEclipseClasspath extends Task
{
    /**
     * 
     */
    private static final String _3RD_PARTY = "/3rd Party/";

    private File projectDirectory;

    private File libDirectory;

    private File destDir;

    private static final String CLASSPATH_FILE_NAME = ".classpath";

    private static final String ELEM_ENTRY = "classpathentry";

    private static final String ATTR_KIND = "kind";

    private static final String ATTR_PATH = "path";

    private static final String VALUE_KIND = "lib";

    public void setProjectdir(File projectDirectory)
    {
        this.projectDirectory = projectDirectory;
    }

    public void setLibdir(File libDir)
    {
        this.libDirectory = libDir;
    }

    public void setDestdir(File destDir)
    {
        this.destDir = destDir;
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

        // check dest dir

        if (this.destDir == null)
        {
            throw new BuildException("You must specify the destDir attribute");
        }

        // make sure the destDir attribute points to a folder
        if (this.destDir.isFile())
        {
            throw new BuildException("destDir must specify a directory not a file!");
        }

        if (!this.destDir.canWrite())
        {
            throw new BuildException("Can not write to destDir!");
        }

        String searchBaseDir = this.libDirectory.getAbsolutePath();
        File classpathFile = new File(this.projectDirectory.getAbsoluteFile() + File.separator + CLASSPATH_FILE_NAME);

        try
        {
            // get the root element
            SAXReader reader = new SAXReader();
            Document document = reader.read(classpathFile);
            Element rootElement = document.getRootElement();

            log("Copying files to: " + destDir);
            
            Iterator<Object> entries = rootElement.elementIterator(ELEM_ENTRY);
            while (entries.hasNext())
            {
                Element entry = (Element) entries.next();
                Attribute kind = entry.attribute(ATTR_KIND);
                if (kind != null && kind.getValue().equals(VALUE_KIND))
                {
                    String pathValue = entry.attributeValue(ATTR_PATH);
                    if (pathValue.startsWith(_3RD_PARTY))
                    {
                        String searchPath = searchBaseDir + File.separator + pathValue.substring(_3RD_PARTY.length());
                        File searchItem = new File(searchPath);

                       

                        if (searchItem.exists())
                        {
                            File destination = new File(destDir, searchItem.getName());
                            if (destination.exists())
                            {
                                long source = FileUtils.checksumCRC32(searchItem);
                                long dest = FileUtils.checksumCRC32(destination);
                                if (source != dest)
                                {
                                    log("Copying updated library: " + searchPath);
                                    destination.delete();
                                    FileUtils.copyFile(searchItem, new File(destDir, searchItem.getName()));
                                }
                            }
                            else
                            {
                                log("Copying new library: " + searchPath);
                                FileUtils.copyFile(searchItem, new File(destDir, searchItem.getName()));
                            }
                        }
                        else
                        {
                            log("Source library not found for: " + searchPath);
                        }
                    }
                }
            }

        }
        catch (Throwable e)
        {
            throw new BuildException(e.getMessage());
        }
    }
}
