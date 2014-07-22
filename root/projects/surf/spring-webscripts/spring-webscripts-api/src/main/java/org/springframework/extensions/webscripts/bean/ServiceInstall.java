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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.Store;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.FormData;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;


/**
 * Install a Web Script 
 * 
 * @author davidc
 */
public class ServiceInstall extends DeclarativeWebScript
{

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.Status)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        if (!(req instanceof WebScriptServletRequest))
        {
            throw new WebScriptException("Web Script install only supported via HTTP Servlet");
        }
        HttpServletRequest servletReq = ((WebScriptServletRequest)req).getHttpServletRequest();
        
        // construct model
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        List<InstalledFile> installedFiles = new ArrayList<InstalledFile>();
        model.put("installedFiles", installedFiles);

        try
        {
            // parse request content
            Object content = req.parseContent();
            if (content == null || !(content instanceof FormData))
            {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Web Script install request is not multipart/form-data");
            }
            
            // locate file upload
            FormData formData = (FormData)content;
            FormField file = null;
            for (FormField field : formData.getFields())
            {
                if (field.getIsFile())
                {
                    if (file != null)
                    {
                        throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Web Script install request expects only one file upload");
                    }
                    file = field;
                }
            }
            if (file == null)
            {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Web Script install request is missing file upload");
            }
            
            // find web script definition
            Document document = null;
            InputStream fileIS = file.getContent().getInputStream();
            try
            {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileIS));
                SAXReader reader = new SAXReader();
                document = reader.read(bufferedReader);
            }
            finally
            {
                fileIS.close();
            }
            Element rootElement = document.getRootElement();
            XPath xpath = rootElement.createXPath("//ws:webscript");
            Map<String,String> uris = new HashMap<String,String>();
            uris.put("ws", "http://www.alfresco.org/webscript/1.0");
            xpath.setNamespaceURIs(uris);
            List nodes = xpath.selectNodes(rootElement);
            if (nodes.size() == 0)
            {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Cannot locate Web Script in uploaded file");
            }
            
            // extract web script definition
            Element webscriptElem = (Element)nodes.get(0);
            String scriptId = webscriptElem.attributeValue("scriptid");
            if (scriptId == null || scriptId.length() == 0)
            {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Expected scriptid value on webscript element");
            }
            Iterator iter = webscriptElem.elementIterator();
            while (iter.hasNext())
            {
                Element fileElem = (Element)iter.next();
                String webscriptStore = fileElem.attributeValue("store");
                if (webscriptStore == null || webscriptStore.length() == 0)
                {
                    throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Expected store value on webscript element");
                }
                String webscriptPath = fileElem.attributeValue("path");
                if (webscriptPath == null || webscriptPath.length() == 0)
                {
                    throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Expected file value on webscript element");
                }
                String webscriptContent = fileElem.getText();
                
                // install web script implementation file
                installFile(webscriptStore, webscriptPath, webscriptContent);
                InstalledFile installedFile = new InstalledFile();
                installedFile.store = webscriptStore;
                installedFile.path = webscriptPath;
                installedFiles.add(installedFile);
            }
            
            // reset web scripts
            getContainer().reset();
            
            // locate installed web script
            Registry registry = getContainer().getRegistry();
            WebScript webscript = registry.getWebScript(scriptId);
            if (webscript == null)
            {
                throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to install Web Script " + scriptId);
            }
            model.put("installedScript", webscript.getDescription());
        }
        catch(DocumentException e)
        {
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        catch(IOException e)
        {
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return model;
    }

    
    private void installFile(String storePath, String file, String content)
    {
        // retrieve appropriate web script store
        Store store = getContainer().getSearchPath().getStore(storePath);
        if (store == null)
        {
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Store path " + storePath + " refers to a store that does not exist");
        }
        
        try
        {
            // determine if file already exists in store
            if (store.hasDocument(file))
            {
                throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Web Script file " + file + " already exists in store " + storePath);
            }
            
            // create the web script file
            store.createDocument(file, content);
        }
        catch(IOException e)
        {
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to install Web Script file " + file + " into store" + storePath);
        }
    }
    
    
    public static class InstalledFile
    {
        private String store;
        private String path;
        
        public String getStore()
        {
            return store;
        }
        
        public String getPath()
        {
            return path;
        }
    }

}
    