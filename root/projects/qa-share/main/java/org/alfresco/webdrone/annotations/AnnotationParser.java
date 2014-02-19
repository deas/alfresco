/*
 * Copyright (C) 2005-2013 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.webdrone.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.alfresco.share.util.ShareUser;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Parse the {@link DataSetup} Annotation and invoke the methods.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class AnnotationParser
{

    private static Log logger = LogFactory.getLog(AnnotationParser.class);

    /**
     * Search for {@link DataSetup} Annotation on the given class, if found invoke that method with {@link WebDrone} object.
     * 
     * @param clazz - The Class name to be search for {@link DataSetup} Annotation.
     * @param drone - The {@link WebDrone} object.
     * @param hybridDrone - The {@link WebDrone} object.
     * @throws Exception - while searching for annotation
     */
    public void parse(Class<?> clazz, Class<? extends Annotation> annotationClass, WebDrone drone, WebDrone hybridDrone, boolean runAll, List<DataGroup> groups, List<String> testLinkIds)
            throws Exception
    {
        Method[] methods = clazz.getMethods();
        int pass = 0;
        int fail = 0;

        for (Method method : methods)
        {
            if (method.isAnnotationPresent(annotationClass))
            {

                try
                {
                    Class<?>[] parameterClasses = method.getParameterTypes();

                    if (parameterClasses.length == 1)
                    {

                        if ((WebDrone.class).equals(parameterClasses[0]))
                        {

                            DataSetup dataSetup = (DataSetup) method.getAnnotation(annotationClass);
                            // TODO: Shan: Revisit the logic, !runAll necessary? Also this needs to happen only for methods in the roup specified and / or data
                            // setup methods for testLinkIds specified
                            if (runAll || (!runAll && (containDataGroup(groups, dataSetup.groups()) || testLinkIds.contains(dataSetup.testLinkId()))))
                            {

                                method.invoke((clazz.asSubclass(clazz)).newInstance(), drone);
                                ShareUser.logout(drone);
                                pass++;

                            }

                        }
                        else
                        {

                            logger.info(clazz + " The Parameter type is not WebDrone : It is " + parameterClasses[0]);
                            fail++;

                        }

                    }
                    else if (parameterClasses.length == 2)
                    {

                        if ((WebDrone.class).equals(parameterClasses[1]))
                        {
                            DataSetup dataSetup = (DataSetup) method.getAnnotation(annotationClass);
                            // TODO: Shan: Revisit the logic, !runAll necessary? Also this needs to happen only for methods in the roup specified and / or data
                            // setup methods for testLinkIds specified
                            if (runAll || (!runAll && (containDataGroup(groups, dataSetup.groups()) || testLinkIds.contains(dataSetup.testLinkId()))))
                            {

                                method.invoke((clazz.asSubclass(clazz)).newInstance(), drone, hybridDrone);
                                ShareUser.logout(drone);
                                pass++;

                            }

                        }
                        else
                        {

                            logger.info(clazz + " The Parameter type is not WebDrone : It is " + parameterClasses[0]);
                            fail++;

                        }

                    }
                    else
                    {
                        logger.info(clazz + " Method have more or less than Parameter(s), Number of Parameter(s) : " + parameterClasses.length);
                        fail++;
                    }

                }
                catch (Exception e)
                {
                    fail++;
                    logger.info("Failed to call the methods : " + method, e.fillInStackTrace());
                    logger.error(e);
                }
            }
        }

        if (pass > 0)
        {
            logger.info(clazz + " Number of Methods Sucessfully Called: " + pass);
        }
        if (fail > 0)
        {
            logger.info(clazz + " Number of Methods Failed to Call by Parser: " + fail);
        }

    }

    protected boolean containDataGroup(List<DataGroup> groups, DataGroup[] annonatedGroups)
    {

        boolean contain = false;

        for (DataGroup dataGroup : annonatedGroups)
        {
            if (groups.contains(dataGroup))
            {
                contain = true;
                break;
            }
        }

        return contain;

    }

}
