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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.PackageDescriptionDocument;
import org.springframework.extensions.webscripts.Path;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.extensions.webscripts.ResourceDescription;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.TypeDescription;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;


/**
 * Index of a Web Script Package
 * 
 * @author davidc
 */
public class IndexPackageDoc extends DeclarativeWebScript
{

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.Status)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        // extract web script package
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        String packagePath = "/" + templateVars.get("package");
        
        // locate web script package
        Registry registry = getContainer().getRegistry();
        Path path = registry.getPackage(packagePath);
        if (path == null)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Web Script Package '" + packagePath + "' not found");
        }
        
        // locate web script package documentation
        PackageDescriptionDocument packageDoc = registry.getPackageDescriptionDocument(packagePath);
        if (packageDoc == null)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Web Script documentation for package '" + packagePath + "' not found");
        }
        
        // create list of package level schemas
        Map<String, TypeDescription> schemas = new HashMap<String, TypeDescription>();
        WebScript[] webscripts = path.getScripts();
        for (WebScript webscript : webscripts)
        {
            TypeDescription[] requests = webscript.getDescription().getRequestTypes();
            if (requests != null)
            {
                for (TypeDescription request : requests)
                {
                    if (request.getId() != null)
                    {
                        schemas.put(request.getId(), request);
                    }
                }
            }
            TypeDescription[] responses = webscript.getDescription().getResponseTypes();
            if (responses != null)
            {
                for (TypeDescription response : responses)
                {
                    if (response.getId() != null)
                    {
                        schemas.put(response.getId(), response);
                    }
                }
            }
        }
        
        // create list of web scripts that are not mapped to a resource
        List<WebScript> unmappedWebScripts = new ArrayList<WebScript>(Arrays.asList(webscripts));
        ResourceDescription[] resources = packageDoc.getResourceDescriptions();
        if (resources != null)
        {
            for (ResourceDescription resource : resources)
            {
                String[] scriptIds = resource.getScriptIds();
                for (String scriptId : scriptIds)
                {
                    WebScript webscript = registry.getWebScript(scriptId);
                    if (webscript != null)
                    {
                        unmappedWebScripts.remove(webscript);
                    }
                }
            }
        }
        
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        model.put("registry", registry);
        model.put("package",  path);
        model.put("packagedoc", packageDoc);
        model.put("schemas", schemas.values());
        model.put("unmapped", unmappedWebScripts);

        return model;
    }

}
