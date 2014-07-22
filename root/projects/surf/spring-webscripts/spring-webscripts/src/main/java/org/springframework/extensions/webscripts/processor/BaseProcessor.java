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

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.surf.core.processor.Processor;
import org.springframework.extensions.surf.core.processor.ProcessorExtension;

/**
 * Base class for Web-Tier processing classes - Script and Template.
 * 
 * @author Kevin Roast
 */
public abstract class BaseProcessor implements Processor
{
    /** A map containing all the processor extenstions */
    protected Map<String, ProcessorExtension> processorExtensions = new HashMap<String, ProcessorExtension>(16);    
    
    /**
     * @see org.springframework.extensions.surf.core.processor.Processor#registerProcessorExtension(org.springframework.extensions.surf.core.processor.ProcessorExtension)
     */
    public void registerProcessorExtension(ProcessorExtension processorExtension)
    {
        this.processorExtensions.put(processorExtension.getExtensionName(), processorExtension);
    }
}