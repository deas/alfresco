/**
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

/**
 * @module alfresco/layouts/HorizontalWidgets
 * @extends module:alfresco/core/ProcessWidgets
 * @author Dave Draper
 */
define(["alfresco/core/ProcessWidgets",
        "dojo/_base/declare",
        "dojo/text!./templates/HorizontalWidgets.html",
        "dojo/dom-construct",
        "dojo/dom-style"], 
        function(ProcessWidgets, declare, template, domConstruct, domStyle) {
   
   return declare([ProcessWidgets], {
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * The CSS class (or a space separated list of classes) to include in the DOM node.
       * 
       * @instance
       * @type {string}
       * @default "horizontal-widgets"
       */
      baseClass: "horizontal-widgets",
      
      /**
       * This will be set to a percentage value such that each widget displayed has an equal share
       * of page width. 
       * 
       * @instance
       * @type {string}
       * @default null 
       */
      widgetWidth: null,
      
      /**
       * Sets up the default width to be allocated to each child widget to be added.
       * 
       * @instance
       */
      postCreate: function alfresco_layout_HorizontalWidgets__postCreate() {
         // Split the full width between all widgets... 
         // We should update this to allow for specific widget width requests...
         if (this.widgets)
         {
            this.widgetWidth = 100 / this.widgets.length; 
         }
         this.inherited(arguments);
      },
      
      /**
       * This overrides the default implementation to ensure that each each child widget added has the 
       * appropriate CSS classes applied such that they appear horizontally. It also sets the width
       * of each widget appropriately (either based on the default generated width which is an equal
       * percentage assigned to each child widget) or the specific width configured for the widget.
       * 
       * @instance
       * @param {object} widget The definition of the widget to create the DOM node for.
       * @returns {element} A new DOM node for the widget to be attached to
       */
      createWidgetDomNode: function alfresco_layout_HorizontalWidgets__createWidgetDomNode(widget) {
         var outerDiv = domConstruct.create("div", { className: "horizontal-widget"}, this.containerNode);
         
         var width = this.widgetWidth + "%";
        
         if (widget.config && widget.config.width)
         {
            width = widget.config.width;
         }
         // Set the width of each widget according to how many there are...
         domStyle.set(outerDiv, {
            "width" : width
         });
         
         var innerDiv = domConstruct.create("div", {}, outerDiv);
         return innerDiv;
      }
   });
});