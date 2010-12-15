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
*  File    VirtWebappRegistryMBean.java
*----------------------------------------------------------------------------*/


package org.alfresco.mbeans;

import org.alfresco.catalina.host.AVMHostConfig;

public interface VirtWebappRegistryMBean
{

    public Boolean updateVirtualWebapp(
                        Integer version,
                        String  pathToWebapp,
                        Boolean isRecursive);


    public Boolean updateAllVirtualWebapps(
                        Integer version,
                        String  path,
                        Boolean isRecursive);


    public Boolean removeVirtualWebapp(
                        Integer version,
                        String  pathToWebapp,
                        Boolean isRecursive);


    public Boolean removeAllVirtualWebapps(
                        Integer version,
                        String  path,
                        Boolean isRecursive);


    public String[] getVirtWebapps();
    public void setDeployer(AVMHostConfig deployer);
}
