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

import java.util.List;

/**
 * Definition of a config section.
 * 
 * @author gavinc
 */
public interface ConfigSection
{
    /**
     * Returns the name of an evaluator to use to determine whether the config
     * section this object represents is applicable to the current lookup
     * operation
     * 
     * @return Name of an evaluator held by the config service
     */
    public String getEvaluator();

    /**
     * Returns the condition to use to determine whether the config section this
     * object represents is applicable. The condition is passed to the evaluator
     * 
     * @return Condition to test
     */
    public String getCondition();

    /**
     * Returns the config elements that make up this config section
     * 
     * @return List of config elements
     */
    public List<ConfigElement> getConfigElements();

    /**
     * Determines whether this config section is global
     * 
     * @return true if it is global, false otherwise
     */
    public boolean isGlobal();
    
    /**
     * Determines whether the config elements found inside this section
     * should replace any previously found config elements with the same
     * name
     * 
     * @return true if config elements should replace existing ones, false otherwise
     */
    public boolean isReplace();
}
