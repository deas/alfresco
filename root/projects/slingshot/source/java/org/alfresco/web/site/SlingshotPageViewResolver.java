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
package org.alfresco.web.site;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.servlet.MTAuthenticationFilter;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.extensions.surf.mvc.PageViewResolver;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Page;

/**
 * Slingshot specific implementation of Page View resolver.
 * 
 * Support for MT in a non-portlet environment is provided via a servlet filter
 * {@link MTAuthenticationFilter} this view resolver makes use of the object provided
 * by the filter to allow authenticated access to the remote store earlier in the
 * Spring MVC lifecycle than would normally occur. This mechanism can only be used in a
 * non-portlet environment.
 * 
 * @author Kevin Roast
 */
public class SlingshotPageViewResolver extends PageViewResolver
{
    @Override
    protected Page lookupPage(String pageId)
    {
        if (ThreadLocalRequestContext.getRequestContext().getUser() == null)
        {
            HttpServletRequest req = MTAuthenticationFilter.getCurrentServletRequest();
            if (req != null)
            {
                try
                {
                    // init the request user context if the thread local is found containing the
                    // servlet request information - this ensures an authenticated Connector is
                    // used when makes a remote call to resolve the Page from the view name
                    RequestContextUtil.initRequestContext(getApplicationContext(), req);
                }
                catch (RequestContextException e)
                {
                    throw new PlatformRuntimeException("Failed to init Request Context: " + e.getMessage(), e);
                }
            }
        }
        
        return super.lookupPage(pageId);
    }
}