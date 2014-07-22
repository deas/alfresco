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

import org.springframework.extensions.config.evaluator.Evaluator;

/**
 * Interface definition for a config lookup algorithm, this may be last value
 * wins, a merging strategy or based on inheritance.
 * 
 * @author gavinc
 */
public interface ConfigLookupAlgorithm
{
   /**
    * Determines whether the given section applies to the given object, if so
    * the section is added to the results
    * 
    * @param section The config section to test 
    * @param evaluator The evaluator for the section being processed
    * @param object The object which is the subject of the config lookup
    * @param results The Config object holding all the matched sections
    */
   public void process(ConfigSection section, Evaluator evaluator, Object object, Config results);
}
