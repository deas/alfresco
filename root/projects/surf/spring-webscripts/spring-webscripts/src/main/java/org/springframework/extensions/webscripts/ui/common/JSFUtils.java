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

package org.springframework.extensions.webscripts.ui.common;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.myfaces.shared_impl.renderkit.html.HtmlFormRendererBase;

/**
 * Class containing misc helper methods for managing JSF Components.
 * 
 * NOTE: Extracted from org.alfresco.web.ui.common.Utils;
 * 
 * @author Kevin Roast
 */
public final class JSFUtils
{
   
   /**
    * Private constructor
    */
   private JSFUtils()
   {
   }
   
   /**
    * Helper to output an attribute to the output stream
    * 
    * @param out        ResponseWriter
    * @param attr       attribute value object (cannot be null)
    * @param mapping    mapping to output as e.g. style="..."
    * 
    * @throws IOException
    */
   public static void outputAttribute(ResponseWriter out, Object attr, String mapping)
      throws IOException
   {
      if (attr != null)
      {
         out.write(' ');
         out.write(mapping);
         out.write("=\"");
         out.write(attr.toString());
         out.write('"');
      }
   }
   
   /**
    * Get the hidden field name for any action component.
    * 
    * All components that wish to simply encode a form value with their client ID can reuse the same
    * hidden field within the parent form. NOTE: components which use this method must only encode
    * their client ID as the value and nothing else!
    * 
    * Build a shared field name from the parent form name and the string "act".
    * 
    * @return hidden field name shared by all action components within the Form.
    */
   public static String getActionHiddenFieldName(FacesContext context, UIComponent component)
   {
      return JSFUtils.getParentForm(context, component).getClientId(context) + NamingContainer.SEPARATOR_CHAR + "act";
   }
   
   /**
    * Helper to recursively render a component and it's child components
    * 
    * @param context    FacesContext
    * @param component  UIComponent
    * 
    * @throws IOException
    */
   public static void encodeRecursive(FacesContext context, UIComponent component)
      throws IOException
   {
      if (component.isRendered() == true)
      {
         component.encodeBegin(context);
         
         // follow the spec for components that render their children
         if (component.getRendersChildren() == true)
         {
            component.encodeChildren(context);
         }
         else
         {
            if (component.getChildCount() != 0)
            {
               for (Iterator i=component.getChildren().iterator(); i.hasNext(); /**/)
               {
                  encodeRecursive(context, (UIComponent)i.next());
               }
            }
         }
         
         component.encodeEnd(context);
      }
   }
   
   /**
    * Generate the JavaScript to submit set the specified hidden Form field to the
    * supplied value and submit the parent Form.
    * 
    * NOTE: the supplied hidden field name is added to the Form Renderer map for output.
    * 
    * @param context       FacesContext
    * @param component     UIComponent to generate JavaScript for
    * @param fieldId       Hidden field id to set value for
    * @param fieldValue    Hidden field value to set hidden field too on submit
    * 
    * @return JavaScript event code
    */
   public static String generateFormSubmit(FacesContext context, UIComponent component, String fieldId, String fieldValue)
   {
      return generateFormSubmit(context, component, fieldId, fieldValue, false, null);
   }
   
   /**
    * Generate the JavaScript to submit set the specified hidden Form field to the
    * supplied value and submit the parent Form.
    * 
    * NOTE: the supplied hidden field name is added to the Form Renderer map for output.
    * 
    * @param context       FacesContext
    * @param component     UIComponent to generate JavaScript for
    * @param fieldId       Hidden field id to set value for
    * @param fieldValue    Hidden field value to set hidden field too on submit
    * @param params        Optional map of param name/values to output
    * 
    * @return JavaScript event code
    */
   public static String generateFormSubmit(FacesContext context, UIComponent component, String fieldId, 
         String fieldValue, Map<String, String> params)
   {
      return generateFormSubmit(context, component, fieldId, fieldValue, false, params);
   }
      
   /**
    * Generate the JavaScript to submit set the specified hidden Form field to the
    * supplied value and submit the parent Form.
    * 
    * NOTE: the supplied hidden field name is added to the Form Renderer map for output.
    * 
    * @param context       FacesContext
    * @param component     UIComponent to generate JavaScript for
    * @param fieldId       Hidden field id to set value for
    * @param fieldValue    Hidden field value to set hidden field too on submit
    * @param valueIsParam  Determines whether the fieldValue parameter should be treated
    *                      as a parameter in the generated JavaScript, false will treat
    *                      the value i.e. surround it with single quotes
    * @param params        Optional map of param name/values to output
    * 
    * @return JavaScript event code
    */
   public static String generateFormSubmit(FacesContext context, UIComponent component, String fieldId, 
         String fieldValue, boolean valueIsParam, Map<String, String> params)
   {
      UIForm form = JSFUtils.getParentForm(context, component);
      if (form == null)
      {
         throw new IllegalStateException("Must nest components inside UIForm to generate form submit!");
      }
      
      String formClientId = form.getClientId(context);
      
      StringBuilder buf = new StringBuilder(200);
      buf.append("document.forms['");
      buf.append(formClientId);
      buf.append("']['");
      buf.append(fieldId);
      buf.append("'].value=");
      if (valueIsParam == false)
      {
         buf.append("'");
      }
      buf.append(fieldValue);
      if (valueIsParam == false)
      {
         buf.append("'");
      }
      buf.append(";");
      
      if (params != null)
      {
         for (String name : params.keySet())
         {
            buf.append("document.forms['");
            buf.append(formClientId);
            buf.append("']['");
            buf.append(name);
            buf.append("'].value='");
            buf.append(StringUtils.replace(params.get(name), "'", "\\'"));
            buf.append("';");
            
            // weak, but this seems to be the way Sun RI do it...
            //FormRenderer.addNeededHiddenField(context, name);
            HtmlFormRendererBase.addHiddenCommandParameter(context, form, name);
         }
      }
      
      buf.append("document.forms['");
      buf.append(formClientId);
      buf.append("'].submit();");
      
      if (valueIsParam == false)
      {
         buf.append("return false;");
      }
      
      // weak, but this seems to be the way Sun RI do it...
      //FormRenderer.addNeededHiddenField(context, fieldId);
      HtmlFormRendererBase.addHiddenCommandParameter(context, form, fieldId);
      
      return buf.toString();
   }
   
   /**
    * Generate the JavaScript to submit the parent Form.
    * 
    * @param context       FacesContext
    * @param component     UIComponent to generate JavaScript for
    * 
    * @return JavaScript event code
    */
   public static String generateFormSubmit(FacesContext context, UIComponent component)
   {
      UIForm form = JSFUtils.getParentForm(context, component);
      if (form == null)
      {
         throw new IllegalStateException("Must nest components inside UIForm to generate form submit!");
      }
      
      String formClientId = form.getClientId(context);
      
      StringBuilder buf = new StringBuilder(48);
      
      buf.append("document.forms['");
      buf.append(formClientId);
      buf.append("'].submit()");
      
      buf.append(";return false;");
      
      return buf.toString();
   }
   
   /**
    * Build a context path safe image tag for the supplied image path.
    * Image path should be supplied with a leading slash '/'.
    * 
    * @param context       FacesContext
    * @param image         The local image path from the web folder with leading slash '/'
    * @param width         Width in pixels
    * @param height        Height in pixels
    * @param alt           Optional alt/title text
    * @param onclick       JavaScript onclick event handler code
    * 
    * @return Populated <code>img</code> tag
    */
   public static String buildImageTag(FacesContext context, String image, int width, int height,
         String alt, String onclick)
   {
      return buildImageTag(context, image, width, height, alt, onclick, null);
   }
   
   /**
    * Build a context path safe image tag for the supplied image path.
    * Image path should be supplied with a leading slash '/'.
    * 
    * @param context       FacesContext
    * @param image         The local image path from the web folder with leading slash '/'
    * @param width         Width in pixels
    * @param height        Height in pixels
    * @param alt           Optional alt/title text
    * @param onclick       JavaScript onclick event handler code
    * @param verticalAlign         Optional HTML alignment value
    * 
    * @return Populated <code>img</code> tag
    */
   public static String buildImageTag(FacesContext context, String image, int width, int height,
                                      String alt, String onclick, String verticalAlign)
   {
      StringBuilder buf = new StringBuilder(200);
      
      String style = "border-width:0px;";
      buf.append("<img src='")
         .append(context.getExternalContext().getRequestContextPath())
         .append(image)
         .append("' width='")
         .append(width)
         .append("' height='")
         .append(height)
         .append("'");
      
      if (alt != null)
      {
         alt = StringUtils.encode(alt);
         buf.append(" alt='")
            .append(alt)
            .append("' title='")
            .append(alt)
            .append("'");
      }
      else
      {
         buf.append(" alt=''");
      }

      if (verticalAlign != null)
      {
         StringBuilder styleBuf = new StringBuilder(40);
         styleBuf.append(style).append("vertical-align:").append(verticalAlign).append(";");
         style = styleBuf.toString();
      }
      
      if (onclick != null)
      {
         buf.append(" onclick=\"").append(onclick).append('"');
         StringBuilder styleBuf = new StringBuilder(style.length() + 16);
         styleBuf.append(style).append("cursor:pointer;");
         style = styleBuf.toString();
      }
      buf.append(" style='").append(style).append("'/>");
      
      return buf.toString();
   }
   
   /**
    * Build a context path safe image tag for the supplied image path.
    * Image path should be supplied with a leading slash '/'.
    * 
    * @param context       FacesContext
    * @param image         The local image path from the web folder with leading slash '/'
    * @param width         Width in pixels
    * @param height        Height in pixels
    * @param alt           Optional alt/title text
    * 
    * @return Populated <code>img</code> tag
    */
   public static String buildImageTag(FacesContext context, String image, int width, int height, String alt)
   {
      return buildImageTag(context, image, width, height, alt, null);
   }
   
   /**
    * Build a context path safe image tag for the supplied image path.
    * Image path should be supplied with a leading slash '/'.
    * 
    * @param context       FacesContext
    * @param image         The local image path from the web folder with leading slash '/'
    * @param alt           Optional alt/title text
    * 
    * @return Populated <code>img</code> tag
    */
   public static String buildImageTag(FacesContext context, String image, String alt)
   {
      return buildImageTag(context, image, alt, null);
   }
   
   /**
    * Build a context path safe image tag for the supplied image path.
    * Image path should be supplied with a leading slash '/'.
    * 
    * @param context       FacesContext
    * @param image         The local image path from the web folder with leading slash '/'
    * @param alt           Optional alt/title text
    * @param verticalAlign         Optional HTML alignment value
    * 
    * @return Populated <code>img</code> tag
    */
   public static String buildImageTag(FacesContext context, String image, String alt, String verticalAlign)
   {
      StringBuilder buf = new StringBuilder(128);
      buf.append("<img src='")
         .append(context.getExternalContext().getRequestContextPath())
         .append(image)
         .append("' ");

      String style = "border-width:0px;";
      if (alt != null)
      {
         alt = StringUtils.encode(alt);
         buf.append(" alt='")
            .append(alt)
            .append("' title='")
            .append(alt)
            .append("'");
      }
      else
      {
         buf.append(" alt=''");
      }

      if (verticalAlign != null)
      {
         StringBuilder styleBuf = new StringBuilder(40);
         styleBuf.append(style).append("vertical-align:").append(verticalAlign).append(";");
         style = styleBuf.toString();
      }
      
      buf.append(" style='").append(style).append("'/>");
      
      return buf.toString();
   }
   
   /**
    * Return the parent UIForm component for the specified UIComponent
    * 
    * @param context       FaceContext
    * @param component     The UIComponent to find parent Form for
    * 
    * @return UIForm parent or null if none found in hiearachy
    */
   public static UIForm getParentForm(FacesContext context, UIComponent component)
   {
      UIComponent parent = component.getParent();
      while (parent != null)
      {
         if (parent instanceof UIForm)
         {
            break;
         }
         parent = parent.getParent();
      }
      return (UIForm)parent;
   }
   
   /**
    * Return the parent UIComponent implementing the NamingContainer interface for
    * the specified UIComponent.
    * 
    * @param context       FaceContext
    * @param component     The UIComponent to find parent Form for
    * 
    * @return NamingContainer parent or null if none found in hiearachy
    */
   public static UIComponent getParentNamingContainer(FacesContext context, UIComponent component)
   {
      UIComponent parent = component.getParent();
      while (parent != null)
      {
         if (parent instanceof NamingContainer)
         {
            break;
         }
         parent = parent.getParent();
      }
      return (UIComponent)parent;
   }
   
   /**
    * Determines whether the given component is disabled or readonly
    * 
    * @param component The component to test
    * @return true if the component is either disabled or set to readonly
    */
   public static boolean isComponentDisabledOrReadOnly(UIComponent component)
   {
      boolean disabled = false;
      boolean readOnly = false;
      
      Object disabledAttr = component.getAttributes().get("disabled");
      if (disabledAttr != null)
      {
         disabled = disabledAttr.equals(Boolean.TRUE);
      }
      
      if (disabled == false)
      {
         Object readOnlyAttr = component.getAttributes().get("readonly");
         if (readOnlyAttr != null)
         {
            readOnly = readOnlyAttr.equals(Boolean.TRUE);
         }
      }

      return disabled || readOnly;
   }
   
   /**
    * Invoke the method encapsulated by the supplied MethodBinding
    * 
    * @param context    FacesContext
    * @param method     MethodBinding to invoke
    * @param event      ActionEvent to pass to the method of signature:
    *                   public void myMethodName(ActionEvent event)
    */
   public static void processActionMethod(FacesContext context, MethodBinding method, ActionEvent event)
   {
      try
      {
         method.invoke(context, new Object[] {event});
      }
      catch (EvaluationException e)
      {
         Throwable cause = e.getCause();
         if (cause instanceof AbortProcessingException)
         {
            throw (AbortProcessingException)cause;
         }
         else
         {
            throw e;
         }
      }   
   }
   
}
