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
package org.alfresco.module.phpIntegration.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.phpIntegration.PHPMethodExtension;
import org.alfresco.module.phpIntegration.PHPObjectExtension;
import org.alfresco.module.phpIntegration.PHPProcessor;
import org.alfresco.module.phpIntegration.PHPProcessorException;
import org.alfresco.processor.ProcessorExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.caucho.quercus.servlet.PhpClassConfig;
import com.caucho.quercus.servlet.QuercusServlet;

/**
 * Override of the Quercus Servlet.
 * 
 * @author Roy Wetherall
 */
public class AlfrescoQuercusServlet extends QuercusServlet
{
    private static final long serialVersionUID = 3074706465787671284L;
    
    private boolean registered = false;

    public AlfrescoQuercusServlet()
    {
        super();        
    }
    
    /**
     * Over ridden service method ensures that the Alfresco PHP API is loaded into the Quercus engine and that the PHP execution occurs 
     * within a valid transaction and authentication context.
     * 
     * @see com.caucho.quercus.servlet.QuercusServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @SuppressWarnings("unchecked")
	public void service(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException
    {
    	// Set the ALF_AVAILABLE value in $_SERVER
    	QuercusServlet.ServerEnv serverEnv = createServerEnv();
    	serverEnv.put(PHPProcessor.ALF_AVAILABLE, "true");
    	
        ServletContext servletContext = request.getSession().getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        
        if (this.registered == false)
        {
        	// Get the PHP processor and register the various Alfresco extensions with the PHP engine used
        	// by the servlet
        	PHPProcessor phpProcessor = (PHPProcessor)applicationContext.getBean("phpProcessor");
        	for(ProcessorExtension extension: phpProcessor.getProcessorExtensions())
        	{
        		if (extension instanceof PHPMethodExtension)
                {
        			// TODO ... at the moment we can't do this because we can't get at the quercus instance to 
        			//          initialise the module
        			
                    //addModule((QuercusModule)extension);    
                    //((PHPMethodExtension)extension).initialiseModule(this.quercus.findModule(extension.getClass().getName()));
                }
                else if (extension instanceof PHPObjectExtension)
                {
                    try
                    {
                        Class clazz = Class.forName(((PHPObjectExtension)extension).getExtensionClass());
                        registerClass(extension.getExtensionName(), clazz);
                    }
                    catch (ClassNotFoundException exception)
                    {
                        throw new PHPProcessorException("PHP Object Extension class '" + ((PHPObjectExtension)extension).getExtensionClass() + "' could not be found.", exception);
                    }
                }
        	}
            
        	// Indicate that the extensions have been registered
        	this.registered = true;
        }
        
        // Execute the parent servlet
        AlfrescoQuercusServlet.super.service(request, response);
    }
    
    /**
     * Helper method to register a new class with the Quercus engine
     * 
     * @param name		the name of the class
     * @param clazz		the class
     */
    @SuppressWarnings("unchecked")
	private void registerClass(String name, Class clazz)
    {
        PhpClassConfig config = new PhpClassConfig();
        config.setName(name);
        config.setType(clazz);
        addClass(config);
    }
}
