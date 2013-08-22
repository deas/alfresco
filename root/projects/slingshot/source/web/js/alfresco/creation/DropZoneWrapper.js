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
 * @module alfresco/creation/DropZoneWrapper
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/DropZoneWrapper.html",
        "alfresco/core/Core",
        "dojo/on",
        "dijit/registry",
        "dojo/_base/lang",
        "dojo/_base/array"], 
        function(declare, _Widget, _Templated, template, AlfCore, on, registry, lang, array) {
   
   return declare([_Widget, _Templated, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/DropZoneWrapper.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/DropZoneWrapper.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * @instance
       * @type {string}
       * @default ""
       */
      moduleName: "<not set>",
      
      /**
       * @instance
       */
      postCreate: function alfresco_creation_DropZoneWrapper__postCreate() {
         if (this.widgets != null)
         {
            this.processWidgets(this.widgets, this.controlNode);
         }
         
         // Whenever a widget update event is emitted we want to attach the information about this 
         // wrapper instance and the item data that it contains...
         on(this.controlNode, "onWidgetUpdate", lang.hitch(this, "updateWidgetUpdateEvent"));
      },
      
      /**
       * Whenever a widget update event occurs (emitted from a descendant widget) then we want to attach
       * this wrappers node information to the event along with all the item data contained within it. This
       * allows widgets (in particular DropZones) in the hierarchy to keep in-sync with the latest changes.
       * 
       * @instance
       * @param {object} evt The custom event
       */
      updateWidgetUpdateEvent: function alfresco_creation_DropZoneWrapper__updateWidgetUpdateEvent(evt) {
         this.alfLog("log", "Updating event with wrapper ndoe", evt);
         evt.widgetWrapperNode = this.domNode;
         evt.widgetWrapperItems = this.getCurrentItems();
      },
      
      /**
       * Emits a custom a "onWidgetDelete" event to indicate that the widget should be deleted.
       * 
       * @instance
       * @param {object} evt The click event that triggers the delete.
       */
      onWidgetDelete: function alfresco_creation_DropZoneWrapper__onWidgetDelete(evt) {
         on.emit(this.domNode, "onWidgetDelete", {
            bubbles: true,
            cancelable: true,
            widgetToDelete: this
         });
      },
      
      /**
       * Although this iterates over all the widgets, really it is assuming that there is only one root 
       * widget that is wrapped. Should this be wrapping multiple items then the returned data might not
       * be as expected.
       * 
       * @instance
       */
      getCurrentItems: function alfresco_creation_DropZoneWrapper__getCurrentItems() {
         var widgets = registry.findWidgets(this.controlNode);
         var items = [];
         array.forEach(widgets, function(widget, i) {
            if (typeof widget.getCurrentItems === "function")
            {
               array.forEach(widget.getCurrentItems(), function(item, j) {
                  items.push(item.data);
               }, this);
            }
         }, this);
         return items;
      },
      
      /**
       * @instance
       * @returns {object[]} An array of widget definitions from the nested widget (and its sub-widgets).
       */
      getWidgetDefinitions: function alfresco_creation_DropZoneWrapper__getWidgetDefinitions() {
         // Get all the widgets defined with the DropZone to get any widget
         // definitions that they define...
         var widgets = registry.findWidgets(this.controlNode);
         var widgetDefs = [];
         array.forEach(widgets, lang.hitch(this, "getSubWidgetDefinitions", widgetDefs));
         return widgetDefs;
      },
      
      /**
       * @instance
       * @param {object[]} widgetDefs The array of widget definitions to add to
       * @param {object} widget The current widget to inspect for widget defintions
       * @param {number} index The index of the current widget to inspect.
       */
      getSubWidgetDefinitions: function alfresco_creation_DropZoneWrapper__getSubWidgetDefinitions(widgetDefs, widget, index) {
         if (typeof widget.getWidgetDefinitions === "function")
         {
            var defs = widget.getWidgetDefinitions();
            if (defs != null && defs.length > 0)
            {
               array.forEach(defs, function(def, i) {
                  widgetDefs.push(def);
               });
            }
         }
      }
   });
});