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
package org.alfresco.module.phpIntegration.methods;

import org.alfresco.module.phpIntegration.PHPMethodExtension;
import org.alfresco.module.phpIntegration.PHPProcessorException;

import com.caucho.quercus.annotation.Optional;

/**
 * @author Roy Wetherall
 */
public class UnitTestMethods extends PHPMethodExtension
{    
    public void assertEquals(Object expected, Object value, @Optional("") String message)
    {
        if (expected.equals(value) == false)
        {
            if (message == null)
            {
                message = "Expected value '" + expected + "' was '" + value + "'";
            }
            throw new PHPProcessorException(message);
        }
    }
    
    public void assertNotNull(Object value, @Optional("") String message)
    {
        if (value == null)
        {
            if (message == null)
            {
                message = "Unexpected null value encountered.";
            }
            throw new PHPProcessorException(message);
        }
    }
    
    public void assertNull(Object value, @Optional("") String message)
    {
        if (value != null)
        {
            if (message == null)
            {
                message = "Unexpected non-null value encountered.";
            }
            throw new PHPProcessorException(message);
        }
    }
    
    public void assertTrue(boolean value, @Optional("") String message)
    {
        if (value == false)
        {
            if (message == null)
            {
                message = "Value is not True";
            }
            throw new PHPProcessorException(message);
        }
    }
    
    public void assertFalse(boolean value, @Optional("") String message)
    {
        if (value == true)
        {
            if (message == null)
            {
                message = "Value is not False";
            }
            throw new PHPProcessorException(message);
        }
    }
    
    public void fail(String message)
    {
        throw new PHPProcessorException(message);
    }
}
