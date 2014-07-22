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
 * Well known Web Script Formats
 * 
 * @author dcaruana
 */
public enum Format
{
    HTML ("text/html"),
    XHTML ("text/xhtml"),
    TEXT ("text/plain"),
    JAVASCRIPT ("text/javascript"),
    XML ("text/xml"),
    ATOM ("application/atom+xml"),
    ATOMFEED ("application/atom+xml;type=feed"),
    ATOMENTRY ("application/atom+xml;type=entry"),
    FORMDATA ("multipart/form-data"),
    JSON ("application/json");
    
    private String mimetype;

    Format(String mimetype)
    {
        this.mimetype = mimetype;
    }
    
    public String mimetype()
    {
        return mimetype;
    }
}
