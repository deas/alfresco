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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Path;
import org.springframework.extensions.webscripts.PathImpl;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;


/**
 * Index of all Web Scripts
 * 
 * @author davidc
 */
public class IndexAll extends DeclarativeWebScript
{

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.Status)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        String packageFilter = req.getParameter("package") != null ? req.getParameter("package") : "/";
        String urlFilter = req.getParameter("url") != null ? req.getParameter("url") : "/";
        String familyFilter = req.getParameter("family") != null ? req.getParameter("family") : "/";
        String lifecycleFilter = req.getParameter("lifecycle") != null ? req.getParameter("lifecycle") : "/";

        // filter web scripts
    	Collection<WebScript> scripts = getContainer().getRegistry().getWebScripts();
    	Collection<WebScript> filteredWebScripts = new ArrayList<WebScript>();
    	for (WebScript script : scripts)
    	{
    		if (includeWebScript(script, packageFilter, urlFilter, familyFilter))
    		{
    			filteredWebScripts.add(script);
    		}
    	}

    	// filter packages
        Path rootPackage = getContainer().getRegistry().getPackage("/");
    	Path filteredPackage = filterPath(null, rootPackage, packageFilter, urlFilter, familyFilter);

        // setup model
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        model.put("description", req.getParameter("desc"));
        model.put("webscripts", filteredWebScripts);
        model.put("rootpackage", filteredPackage);
    	model.put("packageFilter", packageFilter);
    	model.put("urlFilter", urlFilter);
    	model.put("familyFilter", familyFilter);
    	return model;
    }
    
    /**
     * Filter Path by given filters
     * 
     * @param filteredParent  parent path
     * @param path  path to filter
     * @param packageFilter
     * @param urlFilter
     * @param familyFilter
     * @return
     */
    private PathImpl filterPath(PathImpl filteredParent, Path path, String packageFilter, String urlFilter, String familyFilter)
    {
    	PathImpl filteredPath = filteredParent == null ? new PathImpl(path.getPath()) : filteredParent.createChildPath(path.getName());
    	
    	// filter web scripts in package
    	for (WebScript script : path.getScripts())
    	{
    		if (includeWebScript(script, packageFilter, urlFilter, familyFilter))
    		{
    			filteredPath.addScript(script);
    		}
    	}

    	// process path children
    	for (Path child : path.getChildren())
    	{
    		filterPath(filteredPath, child, packageFilter, urlFilter, familyFilter);
    	}
    	
    	return filteredPath;
    }
    
    /**
     * Include Web Script given filters?
     * 
     * @param script
     * @param packageFilter
     * @param urlFilter
     * @param familyFilter
     * @return
     */
    private boolean includeWebScript(WebScript script, String packageFilter, String urlFilter, String familyFilter)
    {
    	// is it in the package
    	if (script.getDescription().getPackage() == null ||
    	    !script.getDescription().getPackage().toString().startsWith(packageFilter))
    	{
    		return false;
    	}
    	
    	if (!familyFilter.equals("/"))
    	{
    		// Family filter is ON
    		// 
    		Set<String> familys = script.getDescription().getFamilys();
    	
    		// Do we have a family at all?
    		if (familys == null || familys.size() == 0)
    		{
    			return false;
    		}
    		
    		if(!familys.contains(familyFilter))
    		{
    			return false;
    		}
    	}
		
		// is it in the url
		String[] uris = script.getDescription().getURIs();
		for (String uri : uris)
		{
			if (!uri.startsWith(urlFilter))
			{
				return false;
			}
		}
		
		return true;
    }
}
