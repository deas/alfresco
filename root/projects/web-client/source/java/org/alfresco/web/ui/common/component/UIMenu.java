/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.ui.common.component;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.alfresco.web.ui.common.Utils;
import org.springframework.extensions.webscripts.ui.common.component.SelfRenderingComponent;

/**
 * @author Kevin Roast
 */
public class UIMenu extends SelfRenderingComponent
{
   // ------------------------------------------------------------------------------
   // Component Impl 
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "org.alfresco.faces.Controls";
   }

   /**
    * @see javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context.FacesContext)
    */
   public void encodeBegin(FacesContext context) throws IOException
   {
      if (!isRendered())
      {
         return;
      }
      
      ResponseWriter out = context.getResponseWriter();
      
      // output a textual label with an optional icon to show the menu
      String menuId = getNextMenuId(this, context);
      out.write("<a href='#' onclick=\"javascript:_toggleMenu(event, '");
      out.write(menuId);
      out.write("');return false;\"");
      outputAttribute(out, getAttributes().get("style"), "style");
      outputAttribute(out, getAttributes().get("styleClass"), "class");
      outputAttribute(out, getTooltip(), "title");
      out.write('>');
      
      // output label text
      String label = getLabel();
      if (label != null)
      {
         out.write(Utils.encode(label));
      }
      
      // output image
      if (getAttributes().get("image") != null)
      {
         out.write(Utils.buildImageTag(context, (String)getAttributes().get("image"), tooltip, "-4px"));
      }
      
      out.write("</a>");
      
      // output the hidden DIV section to contain the menu item table
      out.write("<br><div id='");
      out.write(menuId);
      // NOTE: the use of "*width:0px" is an IE6/7 specific hack to ensure that the CSS is processed
      //       only by IE (which needs the width value) and _not_ FireFox which doesn't...!
      //       Changed the width to "auto" to support IE 10, see MNT-10027
      out.write("' style=\"position:absolute;display:none;padding-left:2px;*width:auto\">");
      out.write("<table border='0' cellpadding='0'");
      outputAttribute(out, getAttributes().get("itemSpacing"), "cellspacing");
      outputAttribute(out, getAttributes().get("menuStyle"), "style");
      outputAttribute(out, getAttributes().get("menuStyleClass"), "class");
      out.write(">");
   }

   /**
    * @see javax.faces.component.UIComponentBase#encodeEnd(javax.faces.context.FacesContext)
    */
   public void encodeEnd(FacesContext context) throws IOException
   {
      if (!isRendered())
      {
         return;
      }
      
      ResponseWriter out = context.getResponseWriter();
      
      // end the menu table and the hidden DIV section
      out.write("</table></div>");
   }
   
   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      this.label = (String)values[1];
      this.tooltip = (String)values[2];
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      return new Object[] { super.saveState(context), this.label, this.tooltip };
   }
   
   
   // ------------------------------------------------------------------------------
   // Strongly typed component property accessors
   
   /**
    * @return Returns the label.
    */
   public String getLabel()
   {
      ValueBinding vb = getValueBinding("label");
      if (vb != null)
      {
         this.label = (String)vb.getValue(getFacesContext());
      }
      return this.label;
   }

   /**
    * @param label The label to set.
    */
   public void setLabel(String label)
   {
      this.label = label;
   }

   /**
    * @return Returns the tooltip.
    */
   public String getTooltip()
   {
      ValueBinding vb = getValueBinding("tooltip");
      if (vb != null)
      {
         this.tooltip = (String)vb.getValue(getFacesContext());
      }
      return this.tooltip;
   }

   /**
    * @param tooltip The tooltip to set.
    */
   public void setTooltip(String tooltip)
   {
      this.tooltip = tooltip;
   }
   
   
   // ------------------------------------------------------------------------------
   // Helpers
   
   /**
    * Return the next usable menu DIV id in a sequence
    * 
    * @param context       FacesContext
    * 
    * @return next menu ID
    */
   public static String getNextMenuId(UIComponent component, FacesContext context)
   {
      Integer val = (Integer)context.getExternalContext().getRequestMap().get(MENU_ID_KEY);
      if (val == null)
      {
         val = Integer.valueOf(0);
      }
      
      // build next id in sequence
      String id = component.getClientId(context) + '_' + val.toString();
      
      // save incremented value in the request ready for next menu component instance
      val = Integer.valueOf( val.intValue() + 1 );
      context.getExternalContext().getRequestMap().put(MENU_ID_KEY, val);
      
      return id;
   }
   
   
   // ------------------------------------------------------------------------------
   // Private members
   
   private final static String MENU_ID_KEY = "__awc_menu_id";
   
   private String label;
   
   private String tooltip;
}
