/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.webdrone.annotations;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

/**
 * It will be used to invoke different user defined annotations with help
 * {@link AnnotationParser}.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class InvokeAnnotation
{

    private static Log logger = LogFactory.getLog(InvokeAnnotation.class);

    /**
     * Call the {@link AnnotationParser} with {@link DataSetup} Annotation.
     * 
     * @param packageName - The Package Name to find all the Classes
     * @param drone - The {@link WebDrone} Object to do the data setup
     * @param hybridDrone - The {@link WebDrone} Object to do the data setup
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public void invoke(String packageName, WebDrone drone, WebDrone hybridDrone, List<DataGroup> groups,
                List<String> testLinkIds) throws Exception
    {

        if (drone == null || hybridDrone == null)
        {
            logger.error("drone is null, so Invoker failed to search for Annotations");
        }
        else
        {
            List<Class> classes = getClasses(packageName);
            if (classes != null && classes.size() > 0)
            {
                logger.info("Number of Classes going to be searched : " + classes.size());
                AnnotationParser annotationParser = new AnnotationParser();
                for (Class classInfo : classes)
                {
                    boolean runAll = false;
                    if ((groups != null && groups.size() == 0) && (testLinkIds != null && testLinkIds.size() == 0))
                    {
                        runAll = true;
                    }
                    annotationParser.parse(classInfo, DataSetup.class, drone, hybridDrone, runAll, groups, testLinkIds);
                }
            }
            else
            {
                logger.error("There is no classes found on the package : " + packageName);
            }
        	drone.quit();
        }
    }
    
    /**
     * Get All TestNG Groups for the given package.
     * @param packageName
     * @return {@link Set} of TestNG Groups
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public Set<String> getAllTestGroups(String packageName) throws ClassNotFoundException, IOException 
    {
        List<Class> classes = getClasses(packageName);
        Set<String> groupSet = new TreeSet<String>();
        if (classes != null && classes.size() > 0)
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Number of Classes going to be searched : " + classes.size());
            }
            for (Class classInfo : classes)
            {
                Method[] methods = classInfo.getMethods();
                for (Method method : methods)
                { 
                    if(method.isAnnotationPresent(Test.class))
                    {
                        Test test = method.getAnnotation(Test.class);
                        String[] groups = test.groups();
                        Collections.addAll(groupSet, groups);
                    }
                }
            }
        }
        return groupSet;
    }
    
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    private static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) 
        {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
		ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) 
        {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }
    
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException 
	{
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) 
        {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) 
        {
            if (file.isDirectory()) 
            {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) 
            {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
