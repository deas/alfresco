/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.repo.dictionary;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.util.cache.AbstractAsynchronouslyRefreshedCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Asynchronously refreshed cache for dictionary models.
 */
public class CompiledModelsCache extends AbstractAsynchronouslyRefreshedCache<DictionaryRegistry>
{
    private static Log logger = LogFactory.getLog(CompiledModelsCache.class);

    private DictionaryDAOImpl dictionaryDAO;
    private TenantService tenantService;

    @Override
    protected DictionaryRegistry buildCache(String tenantId)
    {
        if (tenantId == null)
        {
            tenantId = tenantService.getCurrentUserDomain();
        }

        final String finalTenant = tenantId;
        return AuthenticationUtil.runAs(new RunAsWork<DictionaryRegistry>()
        {
            public DictionaryRegistry doWork() throws Exception
            {
                return dictionaryDAO.initDictionaryRegistry(finalTenant);
            }
        }, tenantService.getDomainUser(AuthenticationUtil.getSystemUserName(), tenantId));
    }

    /**
     * @param tenantId the tenantId of cache that will be removed from live cache
     * @return removed DictionaryRegistry
     */
    public DictionaryRegistry remove(final String tenantId)
    {
        //TODO Should be reworked when MNT-11638 will be implemented
        liveLock.writeLock().lock();
        try
        {
            DictionaryRegistry dictionaryRegistry = live.get(tenantId);
            if (dictionaryRegistry != null)
            {
                live.remove(tenantId);
                dictionaryRegistry.remove();
            }
            
            return dictionaryRegistry;
        }
        finally
        {
            liveLock.writeLock().unlock();
        }
    }

    /**
     * @param dictionaryDAO the dictionaryDAOImpl to set
     */
    public void setDictionaryDAO(DictionaryDAOImpl dictionaryDAO)
    {
        this.dictionaryDAO = dictionaryDAO;
    }

    /**
     * @param tenantService the tenantService to set
     */
    public void setTenantService(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }
}
