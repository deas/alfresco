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
 * @module alfresco/creation/DragWidgetPalette
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/DropAndPreview.html",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dijit/registry",
        "dojo/dnd/Source",
        "dojo/dnd/Target",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dojo/aspect",
        "alfresco/creation/WidgetDragWrapper",
        "dojo/on"], 
        function(declare, _Widget, _Templated, template, AlfCore, lang, array, registry, Source, Target, domConstruct, domClass, aspect, WidgetDragWrapper, on) {
   
   return declare([_Widget, _Templated, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/DropAndPreview.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/DropAndPreview.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type template {String}
       */
      templateString: template,
      
      /**
       * The target for dropping widgets onto.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      previewTarget: null,
      
      /**
       * @instance
       */
      postCreate: function alfresco_creation_DropAndPreview__postCreate() {
         this.previewTarget = new Source(this.previewNode, { 
            accept: ["widget"],
            creator: lang.hitch(this, "creator"),
            withHandles: true
         });
         aspect.after(this.previewTarget, "onMouseDown", lang.hitch(this, "onWidgetSelected"), true);
         
         // When additional nodes are created as a result of dropping them into the preview target it
         // will be necessary to publish the details of the available fields. This is done for the benefit
         // of any controls that are dependent upon that information.
         aspect.after(this.previewTarget, "insertNodes", lang.hitch(this, "publishAvailableFields"), true);
         
         // Subscribe to events containing data to re-render and existing item...
         this.alfSubscribe("ALF_UPDATE_RENDERED_WIDGET", lang.hitch(this, "updateItem"));
         
         // Subscribe to requests to publish the details of the fields that are available...
         this.alfSubscribe("ALF_REQUEST_AVAILABLE_FORM_FIELDS", lang.hitch(this, "publishAvailableFields"));
         
         // Listen for widgets requesting to be deleted...
         on(this.previewNode, "onWidgetDelete", lang.hitch(this, "deleteItem"));
      },
      
      /**
       * @instance
       * @returns {object[]} The currently available fields.
       */
      getAvailableFields: function alfresco_creation_DropAndPreview__getAvailableFields() {
         var currentFields = [];
         for (var key in this.previewTarget.map)
         {
            var currentField = this.previewTarget.map[key];
            
            // Get the field name to use as the label (this can change) and the id (which should remain
            // static once created) to populate an individual option.
            var fieldName = lang.getObject("data.data.defaultConfig.name", false, currentField),
                fieldId = lang.getObject("data.data.defaultConfig.fieldId", false, currentField);
            currentFields.push({
               label: fieldName,
               value: fieldId
            });
         }
         return currentFields;
      },
      
      /**
       * @instance
       * @returns {object[]}
       */
      getWidgetDefinitions: function alfresco_creation_DropAndPreview__getWidgetDefinitions() {
         var widgetDefs = [];
         for (var key in this.previewTarget.map)
         {
            var currentField = this.previewTarget.map[key];
            widgetDefs.push({
               name: currentField.data.data.module,
               config: currentField.data.data.defaultConfig
            });
         }
         return widgetDefs;
      },
      
      /**
       * Publishes an array of the names of all of the currently configured fields.
       * 
       * @instance
       */
      publishAvailableFields: function alfresco_creation_DropAndPreview__publishAvailableFields() {
         var payload = {};
         payload.options = this.getAvailableFields();
         this.alfLog("log", "Publishing available fields:", payload, this);
         this.alfPublish("ALF_FORM_FIELDS_UPDATE", payload);
      },
      
      /**
       * @instance
       * @param {object} evt The event.
       */
      deleteItem: function alfresco_creation_DropAndPreview__deleteItem(evt) {
         this.alfLog("log", "Delete widget request detected", evt);
         
         if (evt.target != null && 
             evt.target.id != null &&
             this.previewTarget.getItem(evt.target.id) != null &&
             evt.widgetToDelete != null) 
         {
            evt.widgetToDelete.destroyRecursive(false);
            this.previewTarget.delItem(evt.target.id);
            
            // If the last item has just been deleted the add the dashed border back...
            if (this.previewTarget.getAllNodes().length == 0)
            {
               domClass.remove(this.previewNode, "containsItems");
            }

            // Request that the any config display is cleared.
            // TODO: Currently this doesn't specify the item, we might not want to delete if the deleted item isn't currently selected.
            // Could check to see if it is the selected item? Or include item details in payload.
            this.alfPublish("ALF_CLEAR_CONFIGURE_WIDGET", {});
            this.publishAvailableFields();
         }
      },
      
      /**
       * Handles updates to the configuration for a currently rendered item.
       * 
       * @instance
       */
      updateItem: function alfresco_creation_DropAndPreview__updateItem(payload) {
         this.alfLog("log", "Update item request", payload);
         
         var clonedConfig = lang.clone(payload.originalConfig);
         
         // Update the original configuration with the new data...
         for (var key in payload.updatedConfig)
         {
            // Use "setObject" to allow keys to be dot-notation properties.
            lang.setObject(key, payload.updatedConfig[key], clonedConfig.data.defaultConfig);
         }
         // Update the configuration values...
         for (var i=0; i<clonedConfig.data.configWidgets.length; i++)
         {
            clonedConfig.data.configWidgets[i].config.value = payload.updatedConfig[clonedConfig.data.configWidgets[i].config.name];
         }
         
         // Update the previously stored name the field name
         this.alfSetData(clonedConfig.data.defaultConfig.fieldId + ".name", payload.updatedConfig.name + "");
         
         // Remove any existing widgets associated with the currently selected node,
         // however preserve the DOM so that it can be used as a reference for adding
         // the replacement widget (it will be removed afterwards)...
         array.forEach(this.previewTarget.getSelectedNodes(), function(node, i) {
            var widget = registry.byNode(node);
            if (widget)
            {
               widget.destroyRecursive(true);
            }
         }, this);
         
         // Create the updated object...
         this.previewTarget.insertNodes(false, [clonedConfig], true, payload.node);
         
         // Remove the previous nodes...
         this.previewTarget.deleteSelectedNodes();
         
         // Publish the details of the latest fields...
         this.publishAvailableFields();
      },
      
      /**
       * This handles the creation of the widget in the preview panel.
       * 
       * @instance
       */
      creator: function alfresco_creation_DropAndPreview__creator(item, hint) {
         this.alfLog("log", "Creating", item, hint);
         
         var node = domConstruct.create("div");
         if (item.data.module != null && item.data.module != "")
         {
            // Clone the supplied item... there are several potential possibilities for this
            // creator being called. Either an avatrar is required (should actually be handled
            // separately) a new field is being created or an existing field is being moved.
            // It's important that we create a fieldId if not defined (e.g. when creating a new
            // field) but preserve any existing values.
            var clonedItem = lang.clone(item); 
            var config = (clonedItem.data.defaultConfig != null) ? clonedItem.data.defaultConfig : {};
            if (config.fieldId === undefined)
            {
               config.fieldId = this.generateUuid();
            }
            
            var widgets = [
               {
                  name: item.data.module,
                  config: config
               }
            ];
            var widgetWrapper = new WidgetDragWrapper({
               pubSubScope: this.pubSubScope,
               widgets: widgets
            }, node);
            
            // Store the field name
            this.alfSetData(config.fieldId + ".name", clonedItem.data.defaultConfig.name + "");
         }
         
         // Add a class to indicate that items are present (removes the dashed border)...
         domClass.add(this.previewNode, "containsItems");
         return {node: widgetWrapper.domNode, data: clonedItem, type: ["widget"]};
      },
      
      /**
       * Although this function's name suggests it handles an nodes selection, there is no guarantee
       * that a node has actually been selected. This is simply attached to the mouseDown event.
       * 
       * @instance
       */
      onWidgetSelected: function alfresco_creation_DropAndPreview__onWidgetSelected(e) {
         var selectedNodes = this.previewTarget.getSelectedNodes();
         if (selectedNodes.length > 0 && selectedNodes[0] != null)
         {
            var selectedItem = this.previewTarget.getItem(selectedNodes[0].id);
            this.alfLog("log", "Widget selected", selectedItem);
            var payload = {
               selectedNode: selectedNodes[0],
               selectedItem: selectedItem.data
            };
            this.alfPublish("ALF_CONFIGURE_WIDGET", payload);
         }
      }
   });
});