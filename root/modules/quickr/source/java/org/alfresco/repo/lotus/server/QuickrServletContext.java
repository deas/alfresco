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
