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
        if (request.getMethod().equals("GET") && request.getPathInfo().endsWith("/services/LibraryService"))
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
