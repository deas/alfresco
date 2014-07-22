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

package org.springframework.extensions.webscripts.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation type for java classes that will either serve as JavaScript or Template root objects 
 * or provide JavaScript or Template APIs. 
 * 
 * In general, developer needs to provide help information, sample code and class type in java code which 
 * will then be processed by a custom Spring Surf mavnen plugin for producing reference documents as part
 * of the build process. This will avoid manual documentation step.
 * 
 * @author drq
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScriptClass 
{
    /**
     * @return a help message for this object (the default is a blank String, which means there is no help)
     */
    String help() default "";

    /**
     * @return code snippet for this object (the default is a blank String, which means there is no sample code)
     */
    String code() default "";
    
    /**
     * @return type of this class (the default is JavaScriptAPI)
     */
    ScriptClassType[] types() default {ScriptClassType.JavaScriptAPI};    
}
