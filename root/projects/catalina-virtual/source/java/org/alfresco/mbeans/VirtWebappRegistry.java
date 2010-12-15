/*-----------------------------------------------------------------------------
*  Copyright 2007-2010 Alfresco Software Limited.
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
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    VirtWebappRegistry.java
*----------------------------------------------------------------------------*/


package org.alfresco.mbeans;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.ArrayList;
import org.alfresco.catalina.host.AVMHostConfig;

public class VirtWebappRegistry implements VirtWebappRegistryMBean 
{
    private String [] virtWebapps_ = { "totally", "bogus", "example", "of", "list" };
    private AVMHostConfig deployer_;

    public VirtWebappRegistry() { }

    public Boolean 
    updateVirtualWebapp(Integer version, String pathToWebapp, Boolean isRecursive)
    {
        if ( deployer_ != null )
        {
            return deployer_.updateVirtualWebapp( 
                                version.intValue(), 
                                pathToWebapp,
                                isRecursive.booleanValue());
        }
        return false;
    }


    public Boolean 
    updateAllVirtualWebapps(Integer version, String path, Boolean isRecursive)
    {
        if ( deployer_ != null )
        {
            return deployer_.updateAllVirtualWebapps( 
                                version.intValue(), 
                                path,
                                isRecursive.booleanValue());
        }
        return false;
    }

    public Boolean 
    removeVirtualWebapp(Integer version, String pathToWebapp, Boolean isRecursive)
    {
        if ( deployer_ != null )
        {
            return deployer_.removeVirtualWebapp( 
                                version.intValue(), 
                                pathToWebapp,
                                isRecursive.booleanValue());
        }
        return false;
    }


    public Boolean 
    removeAllVirtualWebapps(Integer version, String path, Boolean isRecursive)
    {
        if ( deployer_ != null )
        {
            return deployer_.removeAllVirtualWebapps( 
                                version.intValue(), 
                                path,
                                isRecursive.booleanValue());
        }
        return false;
    }


    public String[] getVirtWebapps()
    {
        return virtWebapps_;
    }

    /** Sets AVMHostConfig webapp deployer 
    *  The deployer handles the actual load/reload/unload of webapps
    */
    public void setDeployer(AVMHostConfig deployer)
    {
        deployer_ = deployer;
    }
}
