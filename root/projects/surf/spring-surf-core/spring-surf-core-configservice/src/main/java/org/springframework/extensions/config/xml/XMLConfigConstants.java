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

package org.springframework.extensions.config.xml;

/**
 * Constants for the XML configuration service
 * 
 * @author gavinc
 */
public interface XMLConfigConstants
{
    // XML attribute names
    public static final String ATTR_ID = "id";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_VALUE = "value";
    public static final String ATTR_CLASS = "class";
    public static final String ATTR_ELEMENT_NAME = "element-name";
    public static final String ATTR_EVALUATOR = "evaluator";
    public static final String ATTR_CONDITION = "condition";
    public static final String ATTR_REPLACE = "replace";

    // XML element names
    public static final String ELEMENT_PLUG_INS = "plug-ins";
    public static final String ELEMENT_CONFIG = "config";
    public static final String ELEMENT_EVALUATORS = "evaluators";
    public static final String ELEMENT_EVALUATOR = "evaluator";
    public static final String ELEMENT_ELEMENT_READERS = "element-readers";
    public static final String ELEMENT_ELEMENT_READER = "element-reader";
}
