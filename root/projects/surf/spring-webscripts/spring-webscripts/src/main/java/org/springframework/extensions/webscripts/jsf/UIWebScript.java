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

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.extensions.webscripts.AbstractRuntime;
import org.springframework.extensions.webscripts.Authenticator;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.RuntimeContainer;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptRequestURLImpl;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WebScriptSession;
import org.springframework.extensions.webscripts.WebScriptSessionFactory;
import org.springframework.extensions.webscripts.servlet.WebScriptServletSession;
import org.springframework.extensions.webscripts.ui.common.component.SelfRenderingComponent;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * JSF Component implementation for the WebScript component.
 * <p>
 * Responsible for generating a JSF Component specific WebScriptRuntime instance and
 * executing the specified WebScript against the runtime. 
 * 
 * @author Kevin Roast
 */
public class UIWebScript extends SelfRenderingComponent
{
   private static Log logger = LogFactory.getLog(UIWebScript.class);
   
   /** WebScript URL to execute */
   private String scriptUrl = null;
   private boolean scriptUrlModified = false;
   
   /** User defined script context value */
   private Object context = null;
      
   private RuntimeContainer container;
   
   /**
    * Default constructor
    */
   public UIWebScript()
   {
      WebApplicationContext ctx = FacesContextUtils.getRequiredWebApplicationContext(
            FacesContext.getCurrentInstance());
      // TODO: refer to appropriate container
      this.container = (RuntimeContainer)ctx.getBean("webscripts.container");
   }
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   @Override
   public String getFamily()
   {
      return "org.alfresco.faces.Controls";
   }

   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      this.scriptUrl = (String)values[1];
      this.scriptUrlModified = (Boolean)values[2];
      this.context = values[3];
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[] {
         super.saveState(context), this.scriptUrl, this.scriptUrlModified, this.context};
      return values;
   }

   /* (non-Javadoc)
    * @see javax.faces.component.UIComponentBase#broadcast(javax.faces.event.FacesEvent)
    */
   @Override
   public void broadcast(FacesEvent event) throws AbortProcessingException
   {
      if (event instanceof WebScriptEvent)
      {
         this.scriptUrlModified = true;
         this.scriptUrl = ((WebScriptEvent)event).Url;
      }
      else
      {
         super.broadcast(event);
      }
   }

   /* (non-Javadoc)
    * @see javax.faces.component.UIComponentBase#decode(javax.faces.context.FacesContext)
    */
   @Override
   public void decode(FacesContext context)
   {
      Map requestMap = context.getExternalContext().getRequestParameterMap();
      String fieldId = this.getClientId(context);
      String value = (String)requestMap.get(fieldId);
      if (value != null && value.length() != 0)
      {
         // found web-script URL for this component
         String url = URLDecoder.decode(value);
         queueEvent(new WebScriptEvent(this, url));
      }
   }

   /**
    * @see javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context.FacesContext)
    */
   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      String scriptUrl = getScriptUrl();
      
      Object scriptContext = getContext();
      if (scriptContext != null)
      {
         // context object supplied, perform simple variable substitution
         if (scriptContext instanceof Map)
         {
            Map<String, Object> scriptContextMap = (Map<String, Object>)scriptContext;
            for (String key : scriptContextMap.keySet())
            {
               scriptUrl = scriptUrl.replace("{" + key + "}", scriptContextMap.get(key).toString());
            }
         }
         else
         {
            // currently we only support {noderef} replacement directly
            // TODO: move the variable substitution into the WebScript engine - pass in
            //       a bag of context objects i.e. name/value pairs of well known keys
            //       allow common values such as noderef, nodeid, path, user etc.
            scriptUrl = scriptUrl.replace("{noderef}", scriptContext.toString());
         }
      }
      
      // execute WebScript
      if (logger.isDebugEnabled())
         logger.debug("Processing UIWebScript encodeBegin(): " + scriptUrl);
      
      WebScriptJSFRuntime runtime = new WebScriptJSFRuntime(container, context, scriptUrl);
      runtime.executeScript();
   }
   
   /**
    * Set the scriptUrl
    *
    * @param scriptUrl     the scriptUrl
    */
   public void setScriptUrl(String scriptUrl)
   {
      this.scriptUrl = getFacesContext().getExternalContext().getRequestContextPath() + scriptUrl;
   }

   /**
    * @return the scriptUrl
    */
   public String getScriptUrl()
   {
      if (this.scriptUrlModified == false)
      {
         ValueBinding vb = getValueBinding("scriptUrl");
         if (vb != null)
         {
            this.scriptUrl = getFacesContext().getExternalContext().getRequestContextPath() +
                             (String)vb.getValue(getFacesContext());
         }
      }
      return this.scriptUrl;
   }
   
   /**
    * @return the user defined script context object
    */
   public Object getContext()
   {
      ValueBinding vb = getValueBinding("context");
      if (vb != null)
      {
         this.context = vb.getValue(getFacesContext());
      }
      return this.context;
   }

   /**
    * @param context the user defined script context to set
    */
   public void setContext(Object context)
   {
      this.context = context;
   }
   
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class representing the clicking of a webscript url action.
    */
   public static class WebScriptEvent extends ActionEvent
   {
      public WebScriptEvent(UIComponent component, String url)
      {
         super(component);
         this.Url = url;
      }
      
      public String Url = null;
   }
   
   /**
    * Implementation of a WebScriptRuntime for the JSF environment
    * 
    * @author Kevin Roast
    */
   private class WebScriptJSFRuntime extends AbstractRuntime
   {
      private FacesContext fc;
      private String[] scriptUrlParts;
      
      WebScriptJSFRuntime(RuntimeContainer container, FacesContext fc, String scriptUrl)
      {
         super(container);
         this.fc = fc;
         String contextPath = fc.getExternalContext().getRequestContextPath();
         this.scriptUrlParts = WebScriptRequestURLImpl.splitURL(contextPath, scriptUrl);
      }

      /**
       * @see org.springframework.extensions.webscripts.Runtime#getName()
       */
      public String getName()
      {
          return "JSF";
      }
      
      /**
       * @see org.springframework.extensions.webscripts.AbstractRuntime#createAuthenticator()
       */
      @Override
      protected Authenticator createAuthenticator()
      {
         return null;
      }

      /**
       * @see org.springframework.extensions.webscripts.AbstractRuntime#createRequest(org.springframework.extensions.webscripts.Match)
       */
      @Override
      protected WebScriptRequest createRequest(Match match)
      {
         return new WebScriptJSFRequest(this, this.scriptUrlParts, match);
      }

      /**
       * @see org.springframework.extensions.webscripts.AbstractRuntime#createResponse()
       */
      @Override
      protected WebScriptResponse createResponse()
      {
         return new WebScriptJSFResponse(this, fc, UIWebScript.this);
      }

      /**
       * @see org.springframework.extensions.webscripts.Runtime#getSession()
       */
      @Override
      protected WebScriptSessionFactory createSessionFactory()
      {
          return new WebScriptSessionFactory()
          {
              public WebScriptSession createSession()
              {
                  return new WebScriptServletSession((HttpSession)fc.getExternalContext().getSession(false));
              }
          };
      }

      /**
       * @see org.springframework.extensions.webscripts.AbstractRuntime#getScriptMethod()
       */
      @Override
      protected String getScriptMethod()
      {
         return "GET";
      }

      /**
       * @see org.springframework.extensions.webscripts.AbstractRuntime#getScriptUrl()
       */
      @Override
      protected String getScriptUrl()
      {
         return this.scriptUrlParts[2];
      }

   }
}
