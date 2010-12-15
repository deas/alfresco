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
package org.alfresco.module.phpIntegration.lib;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple logger class for PHP library
 * 
 * @author Roy Wetherall
 */
public class Logger implements ScriptObject
{
    private static final String SCRIPT_OBJECT_NAME = "Logger";
    
    private static Log logger = LogFactory.getLog(Logger.class);
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }

    public void debug(String message)
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug(message);
        }
    }
    
    public void warn(String message)
    {
        if (logger.isWarnEnabled() == true)
        {
            logger.warn(message);
        }
    }
    
    public void info(String message)
    {
        if (logger.isInfoEnabled() == true)
        {
            logger.info(message);
        }
    }
}
