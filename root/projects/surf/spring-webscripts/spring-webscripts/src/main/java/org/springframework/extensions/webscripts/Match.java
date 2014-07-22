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

import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;


/**
 * Represents a URI to Web Script match
 * 
 * This class is immutable.
 * 
 * @author davidc
 */
public final class Match
{
    final private String templatePath;
    final private Map<String, String> templateVars;
    final private String matchPath;
    final private WebScript script;
    final private Kind kind;

    /**
     * Kind of Match
     */
    public enum Kind
    {
        /** URL request matches on URI only */
        URI,
        /** URL request matches on URI and Method */
        FULL
    };

    /**
     * Construct
     * 
     * @param templateVars
     * @param script
     */
    public Match(String templatePath, Map<String, String> templateVars, String matchPath, WebScript script)
    {
        this.kind = Kind.FULL;
        this.templatePath = templatePath;
        this.templateVars = Collections.unmodifiableMap(templateVars);
        this.matchPath = matchPath;
        this.script = script;
    }
    
    /**
     * Construct
     * 
     * @param templatePath
     */
    public Match(String templatePath, Map<String, String> templateVars, String matchPath)
    {
        this.kind = Kind.URI;
        this.templatePath = templatePath;
        this.templateVars = Collections.unmodifiableMap(templateVars);
        this.matchPath = matchPath;
        this.script = null;
    }

    /**
     * Gets the kind of Match
     */
    public Kind getKind()
    {
        return this.kind;
    }
    
    /**
     * Gets the template request URL that matched the Web Script URL Template
     * 
     * @return  matching url template
     */
    public String getTemplate()
    {
        return templatePath;
    }

    /**
     * Gets the template variable substitutions
     * 
     * @return  template variable values (value indexed by name)
     */
    public Map<String, String> getTemplateVars()
    {
        return templateVars;
    }
    
    /**
     * Gets the static (i.e. without tokens) part of the request URL that matched
     * the Web Script URL Template
     * 
     * @return  matching static url path
     */
    public String getPath()
    {
        return matchPath;
    }
    
    /**
     * Gets the matching web script
     * 
     * @return  service (or null, if match kind is URI)
     */
    public WebScript getWebScript()
    {
        return script;
    }

    @Override
    public String toString()
    {
        return templatePath;
    }
}
