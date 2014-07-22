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
package org.springframework.extensions.surf.extensibility;

import freemarker.template.TemplateDirectiveModel;

/**
 * <p>Although this is currently an interface that defines no methods. It's existence allows us to support 
 * generics in the {@list ExtensibilityModel} and also provides a location for defining new methods should
 * they be required at a later date.
 * 
 * @author David Draper
 *
 */
public interface ExtensibilityDirective extends TemplateDirectiveModel
{
    public static final String DIRECTIVE_ID = "id";
    public static final String TARGET = "target";
    public static final String ACTION = "action";
    public static final String ACTION_MERGE   = "merge";      // Merge into model at current depth (DEFAULT)
    public static final String ACTION_REPLACE = "replace";  // Replace the target directive in the model
    public static final String ACTION_REMOVE  = "remove";    // Remove the target directive from the model
    public static final String ACTION_MODIFY  = "modify";    // Override the properties of the target directive (e.g. reorder) 
    public static final String ACTION_BEFORE  = "before";
    public static final String ACTION_AFTER   = "after";
}
