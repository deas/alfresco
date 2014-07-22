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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author Kevin Roast
 * 
 * Base class for returning an I18N message string for a WebScript.
 * <p>
 * Returns an I18N message resolved for the current locale and specified message ID.
 * <p>
 * Firstly the service resource for the parent WebScript will be used for the lookup,
 * followed by the global webscripts.properties resource bundle. 
 */
public class AbstractMessageHelper
{
    private WebScript webscript;
    
    
    /**
     * Constructor
     * 
     * @param webscript     The WebScript to lookup resources against first
     */
    public AbstractMessageHelper(WebScript webscript)
    {
        if (webscript == null)
        {
            throw new IllegalArgumentException("WebScript must be provided to constructor.");
        }
        this.webscript = webscript;
    }
    
    
    /**
     * Get an I18Ned message.
     * 
     * @param id        The message Id
     * @param args      The optional list of message arguments
     * 
     * @return resolved message string or the original ID if unable to find
     */
    protected final String resolveMessage(final String id, final Object... args)
    {
        String result = null;
        
        // lookup msg resource in webscript specific bundle
        final ResourceBundle resources = webscript.getResources();
        if (resources != null)
        {
            try
            {
                if (resources.containsKey(id))
                {
                    result = resources.getString(id);
                }
            }
            catch (MissingResourceException mre)
            {
                // key not present
            }
        }
        
        // if not found, try global bundles
        if (result == null)
        {
            result = I18NUtil.getMessage(id);
        }
        
        if (args.length == 0)
        {
            // for no args, just return found msg or the id on failure
            if (result == null)
            {
            	result = id;
            }
        }
        else
        {
            // for supplied msg args, format msg or return id on failure
            if (result != null)
            {
                result = MessageFormat.format(result, args);
            }
            else
            {
                result = id;
            }
        }
        
        return result;
    }
}
