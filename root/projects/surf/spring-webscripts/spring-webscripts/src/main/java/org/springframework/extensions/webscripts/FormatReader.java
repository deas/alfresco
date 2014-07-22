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

import java.util.Map;


/**
 * Convert a mimetype to a Java object.
 * 
 * @author davidc
 * @param <Type>
 */
public interface FormatReader<Type>
{
    /**
     * Gets the source mimetype to convert from
     * 
     * @return  mimetype
     */
    public String getSourceMimetype();
    
    /**
     * Gets the Java Class to convert to
     * 
     * @return  Java Clas
     */
    public Class<? extends Type> getDestinationClass();
        
    /**
     * Converts mimetype to Java Object
     * 
     * @param req  web script request
     * @return  Java Object
     */
    public Type read(WebScriptRequest req);
    
    /**
     * Create script parameters specific to source mimetype
     * 
     * @param req  web script request
     * @param res  web script response
     * @return  map of script objects indexed by name
     */
    public Map<String, Object> createScriptParameters(WebScriptRequest req, WebScriptResponse res);
}
