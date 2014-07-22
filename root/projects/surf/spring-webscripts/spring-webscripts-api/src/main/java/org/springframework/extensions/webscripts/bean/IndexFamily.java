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

package org.springframework.extensions.webscripts.bean;

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Path;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Index of Scripts within a Family
 * 
 * @author davidc
 */
public class IndexFamily extends DeclarativeWebScript
{

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.Status)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        // extract web script package
        String familyPath = req.getExtensionPath();
        if (familyPath == null || familyPath.length() == 0)
        {
            familyPath = "/";
        }
        if (!familyPath.startsWith("/"))
        {
            familyPath = "/" + familyPath;
        }
        
        // locate web script package
        Path path = getContainer().getRegistry().getFamily(familyPath);
        if (path == null)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Web Script Family '" + familyPath + "' not found");
        }
        
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        model.put("family",  path);
        return model;
    }

}
