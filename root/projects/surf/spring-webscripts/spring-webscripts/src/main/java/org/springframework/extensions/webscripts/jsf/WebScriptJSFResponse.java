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
import java.io.OutputStream;
import java.io.Writer;

import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;

import org.apache.myfaces.shared_impl.renderkit.html.HtmlFormRendererBase;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WebScriptResponseImpl;
import org.springframework.extensions.webscripts.ui.common.JSFUtils;
import org.springframework.extensions.webscripts.ui.common.StringUtils;

/**
 * Implementation of a WebScript Response for the JSF environment.
 * 
 * @author Kevin Roast
 */
public class WebScriptJSFResponse extends WebScriptResponseImpl implements WebScriptResponse
{
   private FacesContext fc;
   private UIWebScript component;
   
   WebScriptJSFResponse(Runtime container, FacesContext fc, UIWebScript component)
   {
      super(container);
      this.fc = fc;
      this.component = component;
   }
   
   /**
    * @see org.springframework.extensions.webscripts.WebScriptResponse#encodeScriptUrl(java.lang.String)
    */
   public String encodeScriptUrl(String url)
   {
      UIForm form = JSFUtils.getParentForm(fc, component);
      if (form == null)
      {
         throw new IllegalStateException("Must nest components inside UIForm to generate form submit!");
      }
      
      String fieldId = component.getClientId(fc);
      String formClientId = form.getClientId(fc);
      
      StringBuilder buf = new StringBuilder(256);
      // dirty - but can't see any other way to convert to a JSF action click... 
      buf.append("#\" onclick=\"");
      buf.append("document.forms[");
      buf.append("'");
      buf.append(formClientId);
      buf.append("'");
      buf.append("]['");
      buf.append(fieldId);
      buf.append("'].value=");
      buf.append("'");
      // encode the URL to the webscript
      buf.append(URLEncoder.encode(url));
      buf.append("'");
      buf.append(";");
      
      buf.append("document.forms[");
      buf.append("'");
      buf.append(formClientId);
      buf.append("'");
      buf.append("].submit();");
      
      buf.append("return false;");
      
      // weak, but this seems to be the way Sun RI/MyFaces do it...
      HtmlFormRendererBase.addHiddenCommandParameter(fc, form, fieldId);
      
      return buf.toString();
   }

   /**
    * @see org.springframework.extensions.webscripts.WebScriptResponse#reset()
    */
   public void reset()
   {
       // nothing to do
   }

   /**
    * @see org.springframework.extensions.webscripts.WebScriptResponse#getOutputStream()
    */
   public OutputStream getOutputStream() throws IOException
   {
      return fc.getResponseStream();
   }

   /**
    * @see org.springframework.extensions.webscripts.WebScriptResponse#getWriter()
    */
   public Writer getWriter() throws IOException
   {
      return fc.getResponseWriter();
   }

   /**
    * @see org.springframework.extensions.webscripts.WebScriptResponse#setStatus(int)
    */
   public void setStatus(int status)
   {
      // makes no sense in the JSF env
   }
    
   /**
    * @see org.springframework.extensions.webscripts.WebScriptResponse#setHeader(java.lang.String, java.lang.String)
    */
   public void setHeader(String name, String value)
   {
       // NOTE: not applicable
   }

   /**
    * @see org.springframework.extensions.webscripts.WebScriptResponse#addHeader(java.lang.String, java.lang.String)
    */
   public void addHeader(String name, String value)
   {
       // NOTE: not applicable
   }

   /**
    * @see org.springframework.extensions.webscripts.WebScriptResponse#setCache()
    */
   public void setCache(Cache cache)
   {
      // NOTE: not applicable
   }
   
   /**
    * @see org.springframework.extensions.webscripts.WebScriptResponse#setContentType(java.lang.String)
    */
   public void setContentType(String contentType)
   {
      // Alfresco JSF framework only supports the default of text-html
   }

   /*
    * (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptResponse#setContentEncoding(java.lang.String)
    */
   public void setContentEncoding(String contentEncoding)
   {
       // NOTE: not applicable
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptResponse#getEncodeScriptUrlFunction(java.lang.String)
    */
   public String getEncodeScriptUrlFunction(String name)
   {
      UIForm form = JSFUtils.getParentForm(fc, component);
      if (form == null)
      {
         throw new IllegalStateException("Must nest components inside UIForm to generate form submit!");
      }
      String fieldId = component.getClientId(fc);
      String formClientId = form.getClientId(fc);
      HtmlFormRendererBase.addHiddenCommandParameter(fc, form, fieldId);
      
      String func = ENCODE_SCRIPT_URL_FUNCTION.replace("$name$", name);
      func = func.replace("$formClientId$", formClientId);
      func = func.replace("$fieldId$", fieldId);
      return StringUtils.encodeJavascript(func);
   }
   
   private static final String ENCODE_SCRIPT_URL_FUNCTION = 
            "{ $name$: function(url) {" + 
            " var out = '';" + 
            " out += \"#\\\" onclick=\\\"document.forms['$formClientId$']['$fieldId$'].value='\";" + 
            " out += escape(url);" + 
            " out += \"';document.forms['$formClientId$'].submit();return false;\";" + 
            " return out; } }";

   /* (non-Javadoc)
    * @see org.springframework.extensions.webscripts.WebScriptResponse#encodeResourceUrl(java.lang.String)
    */
   public String encodeResourceUrl(String url)
   {
       return url;
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptResponse#getEncodeScriptUrlFunction(java.lang.String)
    */
   public String getEncodeResourceUrlFunction(String name)
   {
      String func = ENCODE_RESOURCE_URL_FUNCTION.replace("$name$", name);
      return StringUtils.encodeJavascript(func);
   }

   private static final String ENCODE_RESOURCE_URL_FUNCTION = 
       "{ $name$: function(url) {" + 
       " return url; } }";   
}
