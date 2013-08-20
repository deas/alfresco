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
 * @module alfresco/layouts/LeftAndRight
 * @extends module:alfresco/core/ProcessWidgets
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/ProcessWidgets",
        "dojo/text!./templates/LeftAndRight.html",
        "dojo/dom-construct",
        "dojo/_base/array"], 
        function(declare, ProcessWidgets, template, domConstruct, array) {
   
   return declare([ProcessWidgets], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/LeftAndRight.css"}]
       */
      cssRequirements: [{cssFile:"./css/LeftAndRight.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * Iterates through the array of widgets to be created and creates the appropriate DOM node based
       * on the "align" attribute of the widget configuration. 
       * 
       * @instance
       */
      postCreate: function alfresco_layout_LeftAndRight__postCreate() {
         var _this = this;
         if (this.widgets)
         {
            // Iterate over all the widgets in the configuration object and add them...
            array.forEach(this.widgets, function(entry, i) {
               var domNode = null;
               if (entry.align == "right")
               {
                  domNode = _this.createWidgetDomNode(entry, _this.rightWidgets, entry.className);
               }
               else
               {
                  domNode = _this.createWidgetDomNode(entry, _this.leftWidgets, entry.className);
               }
               _this.createWidget(entry, domNode);
            });
         }
      },
      
      /**
       * Overrides the default implementation to ensure that the DOM node created has the appropriate CSS
       * classes applied such that they are aligned appropriately.
       * 
       * @instance
       * @param {object} widget The configuration for the widget to create a new DOM node for.
       * @param {element} rootNode The DOM node to add the new DOM node as a child of
       * @param {string} rootClassName The CSS class (or space separated list of CSS classes) to be applied to the DOM node
       */
      createWidgetDomNode: function alfresco_layout_LeftAndRight__createWidgetDomNode(widget, rootNode, rootClassName) {
         var className = (rootClassName) ? rootClassName + " horizontal-widget" : "horizontal-widget";
         var outerDiv = domConstruct.create("div", { className: className}, rootNode);
         var innerDiv = domConstruct.create("div", {}, outerDiv);
         return innerDiv;
      }
   });
});