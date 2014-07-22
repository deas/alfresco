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

package org.springframework.extensions.webscripts.jsf;

import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptRequestURLImpl;

/**
 * Implementation of a WebScript Request for the JSF environment.
 * 
 * @author Kevin Roast
 */
public class WebScriptJSFRequest extends WebScriptRequestURLImpl
{
   /**
    * Construct
    * 
    * @param container
    * @param scriptUrlParts
    * @param match
    */
   public WebScriptJSFRequest(Runtime container, String[] scriptUrlParts, Match match)
   {
      super(container, scriptUrlParts, match);
      // decode url args (as they would be if this was a servlet)
      for (String name : this.queryArgs.keySet())
      {
         this.queryArgs.put(name, URLDecoder.decode(this.queryArgs.get(name)));
      }
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getServerPath()
    */
   public String getServerPath()
   {
      // NOTE: not accessable from JSF context - cannot create absolute external urls...
      return "";
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getAgent()
    */
   public String getAgent()
   {
      // NOTE: unknown in the JSF environment
      return null;
   }
      
   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderNames()
    */
   public String[] getHeaderNames()
   {
       return new String[] {};
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getHeader(java.lang.String)
    */
   public String getHeader(String name)
   {
       return null;
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderValues(java.lang.String)
    */
   public String[] getHeaderValues(String name)
   {
       return null;
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getContent()
    */
   public Content getContent()
   {
       return null;
   }
   
}
