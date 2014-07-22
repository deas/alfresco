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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;


/**
 * Retrieves the list of available Web Scripts
 * 
 * @author davidc
 */
public class IndexUpdate extends DeclarativeWebScript
{
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.Status)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        List<String> tasks = new ArrayList<String>();

        // reset index
        String reset = req.getParameter("reset");
        if (reset != null && reset.equals("on"))
        {
            // reset list of web scripts
            int previousCount = getContainer().getRegistry().getWebScripts().size();
            int previousFailures = getContainer().getRegistry().getFailures().size();
            getContainer().reset();
            tasks.add("Reset Web Scripts Registry; registered " + getContainer().getRegistry().getWebScripts().size() + " Web Scripts.  Previously, there were " + previousCount + ".");
            int newFailures = getContainer().getRegistry().getFailures().size();
            if (newFailures != 0 || previousFailures != 0)
            {
                tasks.add("Warning: found " + newFailures + " broken Web Scripts.  Previously, there were " + previousFailures + ".");
            }
        }

        // create model for rendering
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        model.put("tasks", tasks);
        model.put("webscripts", getContainer().getRegistry().getWebScripts());
        model.put("failures", getContainer().getRegistry().getFailures());
        return model;
    }

}
