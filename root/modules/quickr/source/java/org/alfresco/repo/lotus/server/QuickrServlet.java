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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.bus.spring.BusApplicationContext;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author EugeneZh
 */
public class QuickrServlet extends CXFServlet
{
    private static final long serialVersionUID = 1L;

    private boolean inRefresh;

    private ServletContext sc;

    @Override
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (!inRefresh && event instanceof ContextRefreshedEvent)
        {
            // need to re-do the bus/controller stuff
            try
            {
                inRefresh = true;
                updateContext(((ContextRefreshedEvent) event).getApplicationContext());
            }
            finally
            {
                inRefresh = false;
            }
        }
    }

    private void updateContext(ApplicationContext ctx)
    {
        // This constructor works whether there is a context or not
        // If the ctx is null, we just start up the default bus
        if (ctx == null)
        {
            bus = new SpringBusFactory().createBus();
            ctx = bus.getExtension(BusApplicationContext.class);
        }
        else
        {
            bus = new SpringBusFactory(ctx).createBus();
        }

        replaceDestinationFactory();
        ServletConfig conf = new QuickrServletConfig();
        sc = conf.getServletContext();
        // Set up the ServletController
        controller = createServletController(conf);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        if (request.getMethod().equals("GET") && (request.getPathInfo().endsWith("/services/LibraryService") || request.getPathInfo().endsWith("/services/ContentService")))
        {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        super.doGet(request, response);
    }

    @Override
    public ServletContext getServletContext()
    {
        return sc;
    }
}
