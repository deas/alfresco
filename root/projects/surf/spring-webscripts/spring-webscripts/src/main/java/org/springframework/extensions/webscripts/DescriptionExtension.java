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

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;


/**
 * Web Script Description Extension
 * 
 * Provides a Web Script custom service implementation with the ability
 * to maintain custom description entries.
 * 
 * @author davidc
 */
public interface DescriptionExtension
{
    /**
     * Gets the custom description extensions
     * 
     * @param serviceDescPath  path to service doc
     * @param serviceDesc  service doc input stream
     * @return  extensions mapped by name
     */
    public Map<String, Serializable> parseExtensions(String serviceDescPath, InputStream servicedesc);
}
