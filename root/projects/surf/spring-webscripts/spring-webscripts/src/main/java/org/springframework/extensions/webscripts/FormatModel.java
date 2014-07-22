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


/**
 * Format Model
 * 
 * This class is immutable.
 * 
 * @author davidc
 */
public final class FormatModel
{
    private final FormatRegistry registry;
    private final String format;


    /**
     * Construct
     * 
     * @param registry  format registry
     * @param name  format name
     */
    FormatModel(FormatRegistry registry, String name)
    {
        this.registry = registry;
        this.format = name;
    }

    /**
     * Gets the format name
     * 
     * @return  format name
     */
    public String getName()
    {
        return format;
    }

    /**
     * Gets the format mime/content type
     * 
     * @return  mime/content type
     */
    public String getType()
    {
        String type = registry.getMimeType(null, format);
        return (type == null) ? "" : type;
    }
}
