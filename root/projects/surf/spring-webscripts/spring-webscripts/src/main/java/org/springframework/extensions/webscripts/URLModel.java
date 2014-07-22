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

import java.util.Map;


/**
 * Script / Template Model representing Web Script URLs
 * 
 * This class is immutable.
 * 
 * @author davidc
 */
public interface URLModel
{
    public String getServer();
    public String getContext();
    public String getServiceContext();
    public String getService();
    public String getFull();
    public String getArgs();
    public String getMatch();
    public String getExtension();
    public String getTemplate();
    public Map<String, String> getTemplateArgs();
}
