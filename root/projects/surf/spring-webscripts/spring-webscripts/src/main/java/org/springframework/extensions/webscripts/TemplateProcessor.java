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

package org.springframework.extensions.webscripts;

import java.io.Writer;


/**
 * Web Script Template Processor
 * 
 * @author davidc
 */
public interface TemplateProcessor
{
    /**
     * Determines if a template exists
     * 
     * @param template
     * @return  true => exists
     */
    public boolean hasTemplate(String template);
    
    /**
     * Process a template against the supplied data model and write to the out.
     * 
     * @param template       Template name/path
     * @param model          Object model to process template against
     * @param out            Writer object to send output too
     */
    public void process(String template, Object model, Writer out);
    
    /**
     * Process a string template against the supplied data model and write to the out.
     * 
     * @param template       Template string
     * @param model          Object model to process template against
     * @param out            Writer object to send output too
     */
    public void processString(String template, Object model, Writer out);

    /**
     * Gets the default encoding
     * 
     * @return  default encoding
     */
    public String getDefaultEncoding();
    
    /**
     * Reset the Template Processor
     */
    public void reset();

}
