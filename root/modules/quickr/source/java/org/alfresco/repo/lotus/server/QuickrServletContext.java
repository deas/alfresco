/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.repo.lotus.server;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author EugeneZh
 */
public class QuickrServletContext implements ServletContext
{
    public Object getAttribute(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration<?> getAttributeNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ServletContext getContext(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getInitParameter(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration<?> getInitParameterNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int getMajorVersion()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getMimeType(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int getMinorVersion()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public RequestDispatcher getNamedDispatcher(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getRealPath(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public URL getResource(String arg0) throws MalformedURLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public InputStream getResourceAsStream(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<?> getResourcePaths(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getServerInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Servlet getServlet(String arg0) throws ServletException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getServletContextName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration<?> getServletNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration<?> getServlets()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void log(String arg0)
    {
        // TODO Auto-generated method stub
    }

    public void log(Exception arg0, String arg1)
    {
        // TODO Auto-generated method stub
    }

    public void log(String arg0, Throwable arg1)
    {
        // TODO Auto-generated method stub
    }

    public void removeAttribute(String arg0)
    {
        // TODO Auto-generated method stub

    }

    public void setAttribute(String arg0, Object arg1)
    {
        // TODO Auto-generated method stub
    }
}
