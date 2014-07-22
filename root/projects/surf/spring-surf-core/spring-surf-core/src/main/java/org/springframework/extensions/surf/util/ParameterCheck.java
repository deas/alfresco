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

package org.springframework.extensions.surf.util;

import java.util.Collection;

/**
 * Utility class to perform various common parameter checks
 * 
 * @author gavinc
 */
public final class ParameterCheck
{
    /**
     * Checks that the parameter with the given name has content i.e. it is not
     * null
     * 
     * @param strParamName Name of parameter to check
     * @param object Value of the parameter to check
     */
    public static final void mandatory(final String strParamName, final Object object)
    {
        // check that the object is not null
        if (object == null)
        {
            throw new IllegalArgumentException(strParamName + " is a mandatory parameter");
        }
    }

    /**
     * Checks that the string parameter with the given name has content i.e. it
     * is not null and not zero length
     * 
     * @param strParamName Name of parameter to check
     * @param strParamValue Value of the parameter to check
     */
    public static final void mandatoryString(final String strParamName, final String strParamValue)
    {
        // check that the given string value has content
        if (strParamValue == null || strParamValue.length() == 0)
        {
            throw new IllegalArgumentException(strParamName + " is a mandatory parameter");
        }
    }

    /**
     * Checks that the collection parameter contains at least one item.
     * 
     * @param strParamName Name of parameter to check
     * @param coll collection to check
     */
    public static final void mandatoryCollection(final String strParamName, final Collection coll)
    {
        if (coll == null || coll.size() == 0)
        {
            throw new IllegalArgumentException(strParamName + " collection must contain at least one item");
        }
    }

}
