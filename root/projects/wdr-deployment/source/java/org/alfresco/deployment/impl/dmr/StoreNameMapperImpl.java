/*
 * Copyright (C) 2009-2010 Alfresco Software Limited.
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

/**
 * This store name mapper works with the DM deployment target(s)
 * as follows.
 * 
 * The root folder is found via an xpath query
 * e.g /app:company_home/cm:dm_deploy
 * 
 * AVM stores are mapped to one folder (e.g. staging, author, workflow all go
 * to the same destination folder.   This can be enabled or disabled via the "consolidate" 
 * property.
 */
public class StoreNameMapperImpl implements StoreNameMapper
{    
    /**
     * consolidate staging, author and workflow stores to one DM path.
     */
    private boolean consolidate = true;  
    
    /**
     * Map the storeName / project name
     */
    public String mapProjectName(String storeName)
    {
        /**
         * author AVM stores have the form
         * storeName--userId
         * 
         * workflow AVM sandboxes have the form
         * storeName--userId--workflowId
         */
        if(isConsolidate())
        {
            // collapse author and workflow sandboxes
            if(storeName.contains("--"))
            {
                return storeName.substring(0, storeName.indexOf("-"));
            }
        }
        return storeName;
    }

    public void setConsolidate(boolean consolidate)
    {
        this.consolidate = consolidate;
    }

    public boolean isConsolidate()
    {
        return consolidate;
    }
}
