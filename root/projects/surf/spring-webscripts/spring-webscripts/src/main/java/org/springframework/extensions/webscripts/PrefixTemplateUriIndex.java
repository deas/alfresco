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

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;


/**
 * Uri index supporting simple URI templates where matching is performed
 * on static prefix of the URI.
 * 
 * e.g. /a/{b} is matched on /a
 * 
 * Note: this index was used until Alfresco v3.0
 * 
 * @author davidc
 */
public class PrefixTemplateUriIndex implements UriIndex
{
    // map of web scripts by url
    // NOTE: The map is sorted by url (descending order)
    private Map<String, IndexEntry> index = new TreeMap<String, IndexEntry>(Collections.reverseOrder());
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.UriIndex#clear()
     */
    public void clear()
    {
        index.clear();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.UriIndex#getSize()
     */
    public int getSize()
    {
        return index.size();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.UriIndex#registerUri(org.alfresco.web.scripts.WebScript, java.lang.String)
     */
    public void registerUri(WebScript script, String uri)
    {
        Description desc = script.getDescription();
        
        // establish static part of url template
        boolean wildcard = false;
        boolean extension = true;
        int queryArgIdx = uri.indexOf('?');
        if (queryArgIdx != -1)
        {
            uri = uri.substring(0, queryArgIdx);
        }
        int tokenIdx = uri.indexOf('{');
        if (tokenIdx != -1)
        {
            uri = uri.substring(0, tokenIdx);
            wildcard = true;
        }
        if (desc.getFormatStyle() != Description.FormatStyle.argument)
        {
            int extIdx = uri.lastIndexOf(".");
            if (extIdx != -1)
            {
                uri = uri.substring(0, extIdx);
            }
            extension = false;
        }
        
        // index service by static part of url (ensuring no other service has already claimed the url)
        String uriIdx = desc.getMethod() + ":" + uri;
        if (index.containsKey(uriIdx))
        {
            IndexEntry urlIndex = index.get(uriIdx);
            WebScript existingService = urlIndex.script;
            if (!existingService.getDescription().getId().equals(desc.getId()))
            {
                String msg = "Web Script document " + desc.getDescPath() + " is attempting to define the url '" + uriIdx + "' already defined by " + existingService.getDescription().getDescPath();
                throw new WebScriptException(msg);
            }
        }
        else
        {
            IndexEntry urlIndex = new IndexEntry(uri, wildcard, extension, script);
            index.put(uriIdx, urlIndex);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.UriIndex#findWebScript(java.lang.String, java.lang.String)
     */
    public Match findWebScript(String method, String uri)
    {
        String matchedPath = null;
        Match scriptMatch = null;
        String match = method.toUpperCase() + ":" + uri;
        String matchNoExt = method.toUpperCase() + ":" + ((uri.indexOf('.') != -1) ? uri.substring(0, uri.indexOf('.')) : uri);
        
        // locate full match - on URI and METHOD
        for (Map.Entry<String, IndexEntry> entry : index.entrySet())
        {
            IndexEntry urlIndex = entry.getValue();
            String index = entry.getKey();
            String test = urlIndex.includeExtension ? match : matchNoExt; 
            if ((urlIndex.wildcardPath && test.startsWith(index)) || (!urlIndex.wildcardPath && test.equals(index)))
            {
                scriptMatch = new Match(urlIndex.path, null, urlIndex.path, urlIndex.script); 
                break;
            }
            else if ((urlIndex.wildcardPath && uri.startsWith(urlIndex.path)) || (!urlIndex.wildcardPath && uri.equals(urlIndex.path)))
            {
                matchedPath = urlIndex.path;
            }
        }
        
        // locate URI match
        if (scriptMatch == null && matchedPath != null)
        {
            scriptMatch = new Match(matchedPath, null, matchedPath);
        }
        
        return scriptMatch;
    }
    
    /**
     * Index Entry
     */
    private static class IndexEntry
    {
        private IndexEntry(String path, boolean wildcardPath, boolean includeExtension, WebScript script)
        {
            this.path = path;
            this.wildcardPath = wildcardPath;
            this.includeExtension = includeExtension;
            this.script = script;
        }
        
        private String path;
        private boolean wildcardPath;
        private boolean includeExtension;
        private WebScript script;
    }

}
