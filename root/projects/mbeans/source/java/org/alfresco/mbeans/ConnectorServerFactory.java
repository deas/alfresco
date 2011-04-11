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
package org.alfresco.mbeans;

import java.io.IOException;

import javax.management.JMException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.support.ConnectorServerFactoryBean;

/**
 * Factory that creates a JSR-160 <code>JMXConnectorServer</code>, 
 * optionally registers it with the <code>MBeanServer</code> and then starts it.
 * 
 * @author Stas Sokolovsky
 */
public class ConnectorServerFactory extends ConnectorServerFactoryBean
{
    private static Log logger = LogFactory.getLog(ConnectorServerFactory.class);
    
    /**
     * Wraps original initialization method to log errors, rather than having 
     * exceptions occur within the Spring framework itself (this would cause the entire webapp to fail)
     * 
     * @see org.springframework.jmx.support.ConnectorServerFactoryBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws JMException, IOException
    {
        try
        {
            super.afterPropertiesSet();

            if (logger.isInfoEnabled())
            {
                logger.info("Created JMX serverConnector");
            }

        }
        catch (Exception e)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("JMX ServerConnector can't be initialized due to: ", e);
            }
        }
    }
}
