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

package org.alfresco.module.vti.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* <p>
* VtiAction is an adapter between the contents of an incoming
* HTTP request and the corresponding business logic that should be executed to
* process this request. The controller ({@link VtiRequestDispatcher}) will select an
* appropriate Action for each request and call the <code>execute</code> method.</p>   
* 
* @author Stas Sokolovsky
*/
public interface VtiAction
{
    /**
    * <p>Process the specified HTTP request, and create the corresponding HTTP response.</p> 
    *
    * @param request HTTP request
    * @param response HTTP response
    */
    public void execute(HttpServletRequest request, HttpServletResponse response);
}
