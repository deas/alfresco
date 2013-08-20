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
 * @module alfresco/creation/WidgetDragWrapper
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/WidgetDragWrapper.html",
        "alfresco/core/Core",
        "dojo/on"], 
        function(declare, _Widget, _Templated, template, AlfCore, on) {
   
   return declare([_Widget, _Templated, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/WidgetDragWrapper.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/WidgetDragWrapper.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type template {String}
       */
      templateString: template,
      
      /**
       * @instance
       */
      postCreate: function alfresco_creation_WidgetDragWrapper__postCreate() {
         if (this.widgets != null)
         {
            this.processWidgets(this.widgets, this.controlNode);
         }
      },
      
      /**
       * Emits a custom a "onWidgetDelete" event to indicate that the widget should be deleted.
       * 
       * @instance
       * @param {object} evt The click event that triggers the delete.
       */
      onWidgetDelete: function alfresco_creation_WidgetDragWrapper__onWidgetDelete(evt) {
         on.emit(this.domNode, "onWidgetDelete", {
            bubbles: true,
            cancelable: true,
            widgetToDelete: this
         });
      }
   });
});