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
import java.io.OutputStream;

import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.Format;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;


/**
 * Retrieves a Web Script Description Document 
 * 
 * @author davidc
 */
public class ServiceDescription extends AbstractWebScript
{

    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
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

        // retrieve description document
        Description desc = script.getDescription();
        InputStream serviceDescIS = null;
        try
        {
            serviceDescIS = desc.getDescDocument();
            OutputStream out = res.getOutputStream();
            res.setContentType(Format.XML.mimetype() + ";charset=UTF-8");
            byte[] buffer = new byte[2048];
            int read = serviceDescIS.read(buffer);
            while (read != -1)
            {
                out.write(buffer, 0, read);
                read = serviceDescIS.read(buffer);
            }
        }
        catch(IOException e)
        {
            throw new WebScriptException("Failed to read Web Script description document for '" + scriptId + "'", e);
        }
        finally
        {
            try
            {
                if (serviceDescIS != null) serviceDescIS.close();
            }
            catch(IOException e)
            {
                // NOTE: ignore close exception
            }
        }
    }

}
