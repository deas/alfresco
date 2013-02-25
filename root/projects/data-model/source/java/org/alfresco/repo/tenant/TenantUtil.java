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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.repo.tenant;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.util.ParameterCheck;

/**
 * Utility helper methods to change the tenant context for threads.
 * 
 * @author janv
 * @since Thor
 */
public class TenantUtil
{
    public interface TenantRunAsWork<Result>
    {
        /**
         * Method containing the work to be done
         * 
         * @return Return the result of the operation
         */
        Result doWork() throws Exception;
    }

    /**
     * Execute a unit of work in a given tenant context. The thread's tenant context will be returned to its normal state
     * after the call.
     * 
     * @param runAsWork    the unit of work to do
     * @param uid          the user ID
     * @return Returns     the work's return value
     */
    public static <R> R runAsPrimaryTenant(final TenantRunAsWork<R> runAsWork, String uid)
    {
        String runAsUser = AuthenticationUtil.getRunAsUser();
        if ((runAsUser != null) && runAsUser.equals(uid))
        {
            // same user (hence same primary/implied tenant) and already in runAs block (hence no runAsUserTenant switch required)
            return runAsWork(runAsWork);
        }
        else
        {
            // TODO review - get users' primary tenant (based on domain)
            String tenantDomain = TenantService.DEFAULT_DOMAIN;
            if (uid != null)
            {
                int idx = uid.lastIndexOf(TenantService.SEPARATOR);
                if ((idx > 0) && (idx < (uid.length()-1)))
                {
                    tenantDomain = uid.substring(idx+1);
                }
            }
            
            return runAsUserTenant(runAsWork, uid, tenantDomain);
        }
    }
    
    /**
     * Execute a unit of work in a given tenant context. The thread's tenant context will be returned to its normal state
     * after the call.
     * 
     * @param runAsWork    the unit of work to do
     * @param uid          the user ID
     * @param tenanDomain  the tenant domain
     * @return Returns     the work's return value
     */
    public static <R> R runAsUserTenant(final TenantRunAsWork<R> runAsWork, final String uid, final String tenantDomain)
    {
        return AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<R>()
        {
            public R doWork()
            {
                return runAsTenant(runAsWork, tenantDomain);
            }
        }, uid);
    }
    
    /**
     * Execute a unit of work in a given tenant context. The thread's tenant context will be returned to its normal state
     * after the call.
     * 
     * @param runAsWork    the unit of work to do
     * @param tenanDomain  the tenant domain
     * @return Returns     the work's return value
     */
    public static <R> R runAsTenant(final TenantRunAsWork<R> runAsWork, String tenantDomain)
    {
        ParameterCheck.mandatory("tenantDomain", tenantDomain);
        
        if (tenantDomain.indexOf(TenantService.SEPARATOR) > 0)
        {
            throw new AlfrescoRuntimeException("Unexpected tenant domain: "+tenantDomain+" (should not contain '"+TenantService.SEPARATOR+"')");
        }
        
        String currentTenantDomain = null;
        try
        {
            currentTenantDomain = TenantContextHolder.setTenantDomain(tenantDomain);
            return runAsWork(runAsWork);
        }
        finally
        {
            TenantContextHolder.setTenantDomain(currentTenantDomain);
        }
    }
    
    public static <R> R runAsDefaultTenant(final TenantRunAsWork<R> runAsWork)
    {
        // Note: with MT Enterprise, if you're current user is not already part of the default domain then this will switch to System
        if (getCurrentDomain().equals(TenantService.DEFAULT_DOMAIN))
        {
            return runAsWork(runAsWork);
        }
        else
        {
            return runAsSystemTenant(runAsWork, TenantService.DEFAULT_DOMAIN); // force System in default domain
        }
    }
    
    // switch tenant and run as System within that tenant
    public static <R> R runAsSystemTenant(final TenantRunAsWork<R> runAsWork, final String tenantDomain)
    {
        return runAsUserTenant(runAsWork, AuthenticationUtil.getSystemUserName(), tenantDomain);
    }
    
    private static <R> R runAsWork(final TenantRunAsWork<R> runAsWork)
    {
        try
        {
            return runAsWork.doWork();
        }
        catch (Throwable exception)
        {
            // Re-throw the exception
            if (exception instanceof RuntimeException)
            {
                throw (RuntimeException) exception;
            }
            throw new RuntimeException("Error during run as.", exception);
        }
    }
    
    // note: this does not check if tenant is enabled (unlike non-static MultiTServiceImpl.getCurrentUserDomain)
    public static String getCurrentDomain()
    {
        String tenantDomain = TenantContextHolder.getTenantDomain();
        if (tenantDomain == null)
        {
            tenantDomain = TenantService.DEFAULT_DOMAIN;
        }
        return tenantDomain;
    }
}