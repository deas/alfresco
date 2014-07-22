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

package org.springframework.extensions.config.evaluator;

/**
 * Definition of an evaluator, an object that decides whether the config section applies to
 * the current object being looked up. 
 * 
 * @author gavinc
 */
public interface Evaluator
{
   /**
    * Determines whether the given condition evaluates to true for the given object
    * 
    * @param obj The object to use as the basis for the test
    * @param condition The condition to test
    * @return true if this evaluator applies to the given object, false otherwise
    */
   public boolean applies(Object obj, String condition);
}
