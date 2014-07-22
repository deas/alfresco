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

import javax.faces.component.UIComponent;

import org.springframework.extensions.webscripts.ui.common.tag.BaseComponentTag;

/**
 * JSF tag class for the UIWebScript component.
 * 
 * @author Kevin Roast
 */
public class WebScriptTag extends BaseComponentTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   @Override
   public String getComponentType()
   {
      return "org.alfresco.faces.WebScript";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   @Override
   public String getRendererType()
   {
      // the component is self renderering
      return null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      setStringProperty(component, "scriptUrl", this.scriptUrl);
      setStringProperty(component, "context", this.context);
   }
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      this.scriptUrl = null;
      this.context = null;
   }
   
   /**
    * Set the script service Url
    *
    * @param scriptUrl     the script service Url
    */
   public void setScriptUrl(String scriptUrl)
   {
      this.scriptUrl = scriptUrl;
   }
   
   /**
    * Set the script context
    *
    * @param context     the script context
    */
   public void setContext(String context)
   {
      this.context = context;
   }


   /** the script context */
   private String context;

   /** the scriptUrl */
   private String scriptUrl;
}
