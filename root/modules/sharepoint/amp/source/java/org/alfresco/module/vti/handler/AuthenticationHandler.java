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
package org.alfresco.module.vti.handler;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.repo.SessionUser;

/**
 * Web authentication fundamental API
 * 
 * @author PavelYur
 *
 */
public interface AuthenticationHandler
{

    /**
     * Authenticate user based on information in http request such as Authorization header or else.
     * 
     * @param context servlet context
     * @param request http request
     * @param response http response
     * @param alfrescoContext deployment context of alfresco application
     * @return SessionUser information about currently loged in user or null. 
     * @throws ServletException 
     * @throws IOException 
     * @throws SiteMemberMappingException
     */
    public SessionUser authenticateRequest(ServletContext context, HttpServletRequest request, HttpServletResponse response, String alfrescoContext) throws IOException, ServletException;
    
}
