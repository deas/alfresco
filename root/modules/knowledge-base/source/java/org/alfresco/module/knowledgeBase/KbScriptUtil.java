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
package org.alfresco.module.knowledgeBase;

import java.util.Set;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.security.AuthorityService;

/**
 * Knowledge base script utility methods 
 * 
 * @author Roy Wetherall
 */
public class KbScriptUtil extends BaseProcessorExtension implements KbModel
{
    private AuthorityService authorityService;
    
    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }
    
    public String getUserVisibility(final String userName)
    {
        final String currentUser = AuthenticationUtil.getFullyAuthenticatedUser();
        
        return AuthenticationUtil.runAs(new RunAsWork<String>()
        {
            public String doWork() throws Exception
            {
                String result = VISIBILITY_TIER_3.getId();
                
                Set<String> authroities = authorityService.getAuthoritiesForUser(userName);
                if (authorityService.isAdminAuthority(currentUser) == true || authroities.contains(GROUP_INTERNAL) == true)
                {
                    result = VISIBILITY_INTERNAL.getId();
                }
                else if (authroities.contains(GROUP_TIER_1) == true) 
                {
                    result = VISIBILITY_TIER_1.getId();
                }
                else if (authroities.contains(GROUP_TIER_2) == true)
                {
                    result = VISIBILITY_TIER_2.getId();
                }
                
                return result;
            }
        }, AuthenticationUtil.getSystemUserName());
    }
}
