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

package org.springframework.extensions.surf.core.processor;

/**
 * Interface for Proccessor classes - such as Template or Scripting Processors.
 * 
 * @author Roy Wetherall
 */
public interface Processor
{
    /**
     * Get the name of the processor
     * 
     * @return  the name of the processor
     */
    public String getName();
    
    /**
     * The file extension that the processor is associated with, null if none.
     * 
     * @return  the extension
     */
    public String getExtension();
    
    /**
     * Registers a processor extension with the processor
     * 
     * @param processorExtension    the process extension
     */
    public void registerProcessorExtension(ProcessorExtension processorExtension);
}