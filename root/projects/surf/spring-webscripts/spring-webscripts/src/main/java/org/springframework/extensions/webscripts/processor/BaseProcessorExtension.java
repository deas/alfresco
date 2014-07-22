/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts.processor;

import java.util.List;

import org.springframework.extensions.surf.core.processor.Processor;
import org.springframework.extensions.surf.core.processor.ProcessorExtension;

/**
 * Abstract base class for a processor extension in the presentation tier.
 * 
 * {@link org.alfresco.repo.processor.BaseProcessorExtension}
 */
public abstract class BaseProcessorExtension implements ProcessorExtension
{
	/** The list of processors */
	private List<Processor> processors = null;
	
	/** The name of the extension */
	private String extensionName;
	
	
	/**
	 * Sets the processor list.
	 * 
	 * @param processor		  The processor list
	 */
	public void setProcessors(List<Processor> processors)
	{
		this.processors = processors;
	}
	
	/**
	 * Spring bean init method - registers this extension with the appropriate processor.
	 */
	public void register()
	{
	    if (this.processors != null)
	    {
	        for (Processor processor : this.processors)
	        {
	            processor.registerProcessorExtension(this);
	        }
	    }
	}
	
	/**
	 * Sets the extension name.
	 * 
	 * @param extensionName the extension name
	 */
	public void setExtensionName(String extension)
	{
		this.extensionName = extension;
	}
    
    /**
     * @see org.springframework.extensions.surf.core.processor.ProcessorExtension#getExtensionName()
     */
    public String getExtensionName()
    {
    	return this.extensionName;
    }
}