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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.Store;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;


/**
 * Dumps everything known about the specified Web Script 
 * 
 * @author davidc
 */
public class ServiceDump extends DeclarativeWebScript
{

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        // extract web script id
        String scriptId = req.getExtensionPath();
        if (scriptId == null || scriptId.length() == 0)
        {
            throw new WebScriptException("Web Script Id not provided");
        }
        
        // locate web script
        WebScript script = getContainer().getRegistry().getWebScript(scriptId);
        if (script == null)
        {
            throw new WebScriptException("Web Script Id '" + scriptId + "' not found");
        }

        // construct model
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        Map<String, String> implPaths = new HashMap<String, String>();
        List<ScriptStore> modelStores = new ArrayList<ScriptStore>();
        model.put("script", script.getDescription());
        model.put("script_class", script.getClass().toString());
        model.put("stores", modelStores);

        // locate web script stores
        Collection<Store> stores = getContainer().getSearchPath().getStores();
        for (Store store : stores)
        {
            ScriptStore modelStore = new ScriptStore();
            modelStore.path = store.getBasePath();
            
            // locate script implementation files
            String[] scriptPaths;
            try
            {
                scriptPaths = store.getScriptDocumentPaths(script);
            }
            catch (IOException e)
            {
                throw new WebScriptException("Failed to search for documents for script "
                        + script.getDescription().getId() + " in store " + store, e);
            }
            for (String scriptPath : scriptPaths)
            {
                ScriptImplementation impl = new ScriptImplementation();
                impl.path = scriptPath;
                impl.overridden = implPaths.containsKey(scriptPath);
                
                // extract implementation content
                InputStream documentIS = null;
                try
                {
                    documentIS = store.getDocument(scriptPath);
                    InputStreamReader isReader = new InputStreamReader(documentIS);
                    StringWriter stringWriter = new StringWriter();
                    char[] buffer = new char[2048];
                    int read = isReader.read(buffer);
                    while (read != -1)
                    {
                        stringWriter.write(buffer, 0, read);
                        read = isReader.read(buffer);
                    }
                    impl.content = stringWriter.toString();
                }
                catch(IOException e)
                {
                    impl.throwable = e;
                }
                finally
                {
                    try
                    {
                        if (documentIS != null) documentIS.close();
                    }
                    catch(IOException e)
                    {
                        // NOTE: ignore close exception
                    }
                }
                
                // record web script implementation file against store
                modelStore.files.add(impl);
            }
            
            // record store in list of stores
            modelStores.add(modelStore);
        }
        
        return model;
    }

    
    public static class ScriptStore
    {
        private String path;
        private Collection<ScriptImplementation> files = new ArrayList<ScriptImplementation>();
        
        public String getPath()
        {
            return path;
        }
        
        public Collection<ScriptImplementation> getFiles()
        {
            return files;
        }
     
    }
    
    public static class ScriptImplementation
    {
        private String path;
        private boolean overridden;
        private String content;
        private Exception throwable;
        
        public String getPath()
        {
            return path;
        }
        
        public String getContent()
        {
            return content;
        }

        public boolean getOverridden()
        {
            return overridden;
        }
        
        public Throwable getException()
        {
            return throwable;
        }
    }

}
    