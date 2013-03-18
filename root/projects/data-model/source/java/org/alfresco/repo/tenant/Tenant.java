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
package org.alfresco.repo.tenant;

/**
 * Tenant
 *
 */
public class Tenant
{
    private String tenantDomain;
    
    private boolean enabled = false;
    
    private String rootContentStoreDir = null; // if configured - can be null

    // from Thor - unused
    private String dbUrl = null;

    
    public Tenant(String tenantDomain, boolean enabled, String rootContentStoreDir, String dbUrl)
    {
        this.tenantDomain = tenantDomain;
        this.enabled = enabled;
        this.rootContentStoreDir = rootContentStoreDir;
        this.dbUrl = dbUrl;
    }

    public String getTenantDomain()
    {
        return tenantDomain;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
    
    public String getRootContentStoreDir()
    {
        return rootContentStoreDir;
    }
    
    public String getDbUrl()
    {
        return dbUrl;
    }
}
