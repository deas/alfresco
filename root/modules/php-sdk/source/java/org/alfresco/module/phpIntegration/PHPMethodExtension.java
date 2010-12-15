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
package org.alfresco.module.phpIntegration;

import org.alfresco.processor.ProcessorExtension;
import org.alfresco.service.ServiceRegistry;

import com.caucho.quercus.module.AbstractQuercusModule;
import com.caucho.quercus.module.QuercusModule;

/**
 * Base class representing an extension to the PHP processor that adds new methods.
 * 
 * @author Roy Wetherall
 */
public class PHPMethodExtension extends AbstractQuercusModule implements ProcessorExtension
{
    /** The name of the extension */
    protected String extensionName;
    
    /** The PHP processor */
    protected PHPProcessor phpProcessor;
    
    /** The service registry */
    protected ServiceRegistry serviceRegistry;    
    
    /**
     * Sets the service registry
     * 
     * @param serviceRegistry   the service registry
     */
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }
    
    /**
     * Sets the extension name
     * 
     * @param extensionName     the extension name
     */
    public void setExtensionName(String extensionName)
    {
        this.extensionName = extensionName;
    }
    
    /**
     * @see org.alfresco.service.cmr.repository.ProcessorExtension#getExtensionName()
     */
    public String getExtensionName()
    {
        return this.extensionName;
    }
    
    /**
     * Sets the PHP Processor
     * 
     * @param phpProcessor  the PHP processor
     */
    public void setPhpProcessor(PHPProcessor phpProcessor)
    {
        this.phpProcessor = phpProcessor;
    }
    
    /**
     * Register the method extension with the PHP processor.
     */
    public void register()
    {
       this.phpProcessor.registerProcessorExtension(this);
    }
    
    /**
     * Callback used to copy across state to the Quercus module.  This is needed because the Quercus
     * library creates a new instance of the module once it has been added.
     * 
     * @param module    the Quercus module
     */
    public void initialiseModule(QuercusModule module)
    {
        PHPMethodExtension baseModule = (PHPMethodExtension)module;
        baseModule.extensionName = this.extensionName;
        baseModule.phpProcessor = this.phpProcessor;
        baseModule.serviceRegistry = this.serviceRegistry;
    }    
}
