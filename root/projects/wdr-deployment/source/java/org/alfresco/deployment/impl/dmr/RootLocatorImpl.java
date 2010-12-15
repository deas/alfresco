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
package org.alfresco.deployment.impl.dmr;

import java.util.Map;

/**
 * A simple implemention of root locator that puts all deployed web projects 
 * into a single web directory.
 * 
 * Additionally an additional map of mappings between web project 
 * and root query can be specified.  If the web project matches 
 * one of the mappings then that is used, else the default localtion 
 * is used. 
 *
 * @author Mark Rogers
 */
public class RootLocatorImpl implements RootLocator
{
    private String defaultLocation = "/app:company_home";
    
    private Map<String, String> projectToQueryMap;

    public String getRootQuery(String projectName)
    {
        // If there is a project specific mapping
        if(projectToQueryMap != null)
        {
            String query = projectToQueryMap.get(projectName);
            if(query != null)
            {
                return query;
            }
        }
        // return the 
        return defaultLocation;
    }

    public void setDefaultLocation(String rootQuery)
    {
        this.defaultLocation = rootQuery;
    }

    public String getDefaultLocation()
    {
        return defaultLocation;
    }

    public void setProjectToQueryMap(Map<String, String> projectToQueryMap)
    {
        this.projectToQueryMap = projectToQueryMap;
    }

    public Map<String, String> getProjectToQueryMap()
    {
        return projectToQueryMap;
    }
}
