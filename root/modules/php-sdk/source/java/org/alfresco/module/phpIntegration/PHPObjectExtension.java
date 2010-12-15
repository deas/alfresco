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

import org.alfresco.module.phpIntegration.lib.NodeFactory;
import org.alfresco.processor.ProcessorExtension;
import org.alfresco.service.namespace.QName;

import com.caucho.quercus.module.AbstractQuercusModule;

/**
 * 
 * 
 * @author Roy Wetherall
 */
public class PHPObjectExtension extends AbstractQuercusModule implements ProcessorExtension
{
    /** The name of the extension */
    protected String extensionName;
    
    /** The PHP processor */
    protected PHPProcessor phpProcessor;    
    
    /** The extension implementation class */
    protected String extensionClass;
    
    /** The name of the associated node data dictionary type */
    protected String nodeType;
    
    /** The node factory to register the node type with */
    protected NodeFactory nodeFactory;
    
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
     * Sets the extension class name
     * 
     * @param extensionClass    the extension class name
     */
    public void setExtensionClass(String extensionClass)
    {
        this.extensionClass = extensionClass;
    }
    
    /**
     * Get the extension class
     * 
     * @return  String  the extension class
     */
    public String getExtensionClass()
    {
        return extensionClass;
    }
    
    /**
     * Sets the node type if applicable
     * 
     * @param nodeType  the node type
     */
    public void setNodeType(String nodeType)
    {
        this.nodeType = nodeType;
    }
    
    /**
     * Sets the node factory
     * 
     * @param nodeFactory   the node factory
     */
    public void setNodeFactory(NodeFactory nodeFactory)
    {
        this.nodeFactory = nodeFactory;
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
    @SuppressWarnings("unchecked")
    public void register()
    {
       this.phpProcessor.registerProcessorExtension(this);
       
       // Register the node type if specified
       if (this.nodeType != null)
       {
           try
           {
               QName type = QName.createQName(this.nodeType);
               Class clazz = Class.forName(this.extensionClass);           
               this.nodeFactory.addNodeType(type, clazz);
           }
           catch (ClassNotFoundException exception)
           {
               throw new PHPProcessorException("Unable to load node type (" + this.extensionClass + ")", exception);
           }
       }
    }      
}
