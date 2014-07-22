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

package org.springframework.extensions.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of a config section
 * 
 * @author gavinc
 */
public class ConfigSectionImpl implements ConfigSection
{
    private String evaluator;
    private String condition;
    private boolean replace = false;
    private List<ConfigElement> configElements;

    public ConfigSectionImpl(String evaluator, String condition, boolean replace)
    {
        this.evaluator = evaluator;
        this.condition = condition;
        this.replace = replace;
        this.configElements = new ArrayList<ConfigElement>();
        
        // don't allow empty strings
        if (this.evaluator != null && this.evaluator.length() == 0)
        {
           throw new ConfigException("The 'evaluator' attribute must have a value if it is present");
        }
        
        if (this.condition != null && this.condition.length() == 0)
        {
           throw new ConfigException("The 'condition' attribute must have a value if it is present");
        }
    }

    /**
     * @see org.springframework.extensions.config.ConfigSection#getEvaluator()
     */
    public String getEvaluator()
    {
        return this.evaluator;
    }

    /**
     * @see org.springframework.extensions.config.ConfigSection#getCondition()
     */
    public String getCondition()
    {
        return this.condition;
    }

    /**
     * @see org.springframework.extensions.config.ConfigSection#getConfigElements()
     */
    public List<ConfigElement> getConfigElements()
    {
        return this.configElements;
    }

    /**
     * Adds a config element to the results for the lookup
     * 
     * @param configElement
     */
    public void addConfigElement(ConfigElement configElement)
    {
        this.configElements.add(configElement);
    }

    /**
     * @see org.springframework.extensions.config.ConfigSection#isGlobal()
     */
    public boolean isGlobal()
    {
        boolean global = false;

        if (this.evaluator == null && this.condition == null)
        {
            global = true;
        }

        return global;
    }
    
    /**
     * @see org.springframework.extensions.config.ConfigSection#isReplace()
     */
    public boolean isReplace()
    {
       return this.replace;
    }

    public String toString()
    {
        StringBuilder buffer = new StringBuilder(super.toString());
        buffer.append(" (evaluator=").append(this.evaluator);
        buffer.append(" condition=").append(this.condition);
        buffer.append(" replace=").append(this.replace).append(")");
        return buffer.toString();
    }
}
