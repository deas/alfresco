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
 * @module alfresco/creation/DropZone
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
        "alfresco/creation/DropZoneWrapper",
        "dojo/on"], 
        function(declare, _Widget, _Templated, template, AlfCore, lang, array, registry, Source, Target, domConstruct, domClass, aspect, DropZoneWrapper, on) {
   
   return declare([_Widget, _Templated, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {Array}
       */
      cssRequirements: [{cssFile:"./css/DropAndPreview.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/DropAndPreview.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * @instance
       * @type {boolean}
       * @default false
       */
      horizontal: false,
      
      /**
       * The target for dropping widgets onto.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      previewTarget: null,
      
      /**
       * A list of the initial items to add to the drop zone when it is first created.
       * 
       * @instance
       * @type {object[]}
       * @default
       */
      initialItems: null,
      
      /**
       * The types that this drop zone will accept. By default this is set to null but if not specified
       * in the configuration this will be initialised to ["widget"].
       *
       * @instance
       * @type {string[]}
       * @default null
       */
      acceptTypes: null,

      /**
       * @instance
       */
      postCreate: function alfresco_creation_DropZone__postCreate() {
         if (this.acceptTypes == null)
         {
            this.acceptTypes = ["widget"];
         }
         
         this.previewTarget = new Source(this.previewNode, { 
            accept: this.acceptTypes,
            creator: lang.hitch(this, "creator"),
            withHandles: true,
            horizontal: this.horizontal
         });

         // Create a new UUID to pass on to the widgets that are dropped into this instance
         // this is done so that this instance can subscribe to requests from it's direct dropped items
         this.childPubSubScope = this.generateUuid()
         
         // Capture wrappers being selected...
         aspect.after(this.previewTarget, "onMouseDown", lang.hitch(this, "onWidgetSelected"), true);
         
         // Capture widgets being dropped...
         aspect.after(this.previewTarget, "onDrop", lang.hitch(this, "emitOnWidgetUpdate"), true);
         
         // When additional nodes are created as a result of dropping them into the preview target it
         // will be necessary to publish the details of the available fields. This is done for the benefit
         // of any controls that are dependent upon that information.
         aspect.after(this.previewTarget, "insertNodes", lang.hitch(this, "publishAvailableFields"), true);
         
         // Subscribe to events containing data to re-render and existing item...
         this.alfSubscribe("ALF_UPDATE_RENDERED_WIDGET", lang.hitch(this, "updateItem"));
         
         // Subscribe to requests to publish the details of the fields that are available...
         this.alfSubscribe(this.childPubSubScope + "ALF_REQUEST_AVAILABLE_FORM_FIELDS", lang.hitch(this, "publishAvailableFields"), true);
         
         // Listen for widgets requesting to be deleted...
         on(this.previewNode, "onWidgetDelete", lang.hitch(this, "deleteItem"));
         
         // Listen for wrapped DropZones being updated...
         on(this.previewNode, "onWidgetUpdate", lang.hitch(this, "updateWidgetsForDisplay"));
         
         // Add in any items that are included as instantiation arguments...
         // These would be included when a DropZone is created as the display
         // widget of a widget with children. The children will be the items to add.
         if (this.initialItems != null)
         {
            var items = [];
            array.forEach(this.initialItems, function (item, index) {
               items.push(this.alfGetData(item));
            }, this);
            // this.previewTarget.insertNodes(false, this.initialItems, false, null);
            this.previewTarget.insertNodes(false, items, false, null);
         }

         if (this.value != null && this.value != "")
         {
            array.forEach(this.value, function(widget, i) {
               var data = {
                  name: widget.widgetDisplayName,
                  module: widget.name,
                  defaultConfig: widget.config,
                  widgetsForDisplay: widget.widgetsForDisplay,
                  widgetsForConfig: widget.widgetsForConfig
               };
               var dndData = this.creator(data);
               this.previewTarget.insertNodes(true, [dndData.data]);
            }, this);
         }
      },
      
      /**
       * @instance
       * @param {object} evt The "onWidgetUpdate" custom event
       */
      updateWidgetsForDisplay: function alfresco_creation_DropZone__updateWidgetsForDisplay(evt) {
         this.alfLog("log", "A nested widget has been added", evt);
         
         if (evt.widgetWrapperNode != null && evt.widgetWrapperItems != null)
         {
            var itemToUpdate = this.previewTarget.getItem(evt.widgetWrapperNode.id);
            if (itemToUpdate != null)
            {
               evt.widgetsForDisplay[0].config.initialItems = evt.widgetWrapperItems;
               // itemToUpdate.data.widgetsForDisplay = evt.widgetsForDisplay;
               evt.stopPropagation();
               evt.preventDefault();
               
               // ...and now generate a new event for this DropZone...
               this.emitOnWidgetUpdate()
            }
         }
      },
      
      /**
       * @instance
       * @returns {object[]} The currently available fields.
       */
      getAvailableFields: function alfresco_creation_DropZone__getAvailableFields() {
         var currentFields = [];
         for (var key in this.previewTarget.map)
         {
            var currentField = this.previewTarget.map[key];
            
            // Get the field name to use as the label (this can change) and the id (which should remain
            // static once created) to populate an individual option.
            var fieldName = lang.getObject("updatedConfig.name", false, currentField.data);
            if (fieldName == null)
            {
               fieldName = lang.getObject("defaultConfig.name", false, currentField.data);
            }
            var fieldId = lang.getObject("updatedConfig.fieldId", false, currentField.data);
            if (fieldId == null)
            {
               fieldId = lang.getObject("defaultConfig.fieldId", false, currentField.data);
            }
            currentFields.push({
               label: fieldName,
               value: fieldId
            });
         }
         return currentFields;
      },
      
      /**
       * Get all the widgets defined with the DropZone to get any sub-widget definitions that they define.
       *
       * @instance
       * @returns {object[]}
       */
      getWidgetDefinitions: function alfresco_creation_DropZone__getWidgetDefinitions() {
            
         // Get all the widgets defined with the DropZone to get any widget definitions that they define...
         var widgetDefs = [];
         array.forEach(this.previewTarget.getAllNodes(), lang.hitch(this, "getWidgetDefinition", widgetDefs));
         return widgetDefs;
      },

      /**
       * This function retrieves the configuration for an individual widget that has been dropped into 
       * the drop zone.
       *
       * @instance
       * @param {object[]} widgetDefs The object array to add the new widget definition object into
       * @param {element} node The DOM node from which to retrieve the widget data
       * @param {number} i The index of the node.
       */
      getWidgetDefinition: function alfresco_creation_DropZone__getWidgetDefinition(widgetDefs, node, i) {
         var currentField = this.previewTarget.getItem(node.id);

         // Allow alternatives to "name" and "config" to be specified in the item data...
         var itemNameKey = (currentField.data.itemNameKey != null) ? currentField.data.itemNameKey : "name",
             itemConfigKey = (currentField.data.itemConfigKey != null) ? currentField.data.itemConfigKey : "config";

         // Create the new definition...
         var newDef = {
            // widgetsForDisplay: currentField.data.widgetsForDisplay,
            // widgetDisplayName: currentField.data.name,
            // widgetsForConfig: currentField.data.widgetsForConfig,
            // previewWidget: currentField.data.previewWidget
         };

         // Set the name and config...
         newDef[itemNameKey] = currentField.data.module;

         // If "updatedConfiguration" is available then use that, otherwise use the default configuration...
         // There should be "updatedConfig" if the user has modified the default form and saved it...
         // ...Also mixin any additional configuration...
         // This is for the benefit of the widget being added to, not the widget being instantiated...
         if (currentField.data.updatedConfig != null)
         {
            newDef[itemConfigKey] = currentField.data.defaultConfig;
            lang.mixin(newDef[itemConfigKey], currentField.data.updatedConfig);
            lang.mixin(newDef, currentField.data.additionalConfig);
         }
         else
         {
            newDef[itemConfigKey] = currentField.data.defaultConfig;
            lang.mixin(newDef, currentField.data.additionalConfig);
         }
         
         // Get any sub-widget definitinos and add them to the new definition...
         this.getSubWidgetDefinitions(node, newDef, currentField);
         
         widgetDefs.push(newDef);
      },

      /**
       * Gets any widgets that are defined within the widget bound to the supplied node. If a widget dropped
       * into the drop-zone has any widgets that have been dropped into it then there definitions need to be
       * retrieved and saved.
       *
       * @instance
       * @param {element} node The DOM node from which to retrieve the widget data
       * @param {object} The definition object that should be updated
       */
      getSubWidgetDefinitions: function alfresco_creation_DropZone__getSubWidgetDefinitions(node, newDef, currentField) {
         var widget = registry.byNode(node);
         if (widget != null && 
             typeof widget.getWidgetDefinitions === "function")
         {
            var defs = widget.getWidgetDefinitions();
            if (defs == null || defs.length == 0)
            {
               // No action
            }
            else
            {
               // It's possible to configure an alternative to "config.widgets" as the value to set the drag and dropped
               // items of. "config.widgets" is the default because it is expected that a set of nested widgets will be constructed
               // however it is also possible to drop an object that represents a Publication. In this case the value to be set
               // will most likely be "publishPayload.widgets"...
               var itemDroppedItemsKey = (currentField.data.itemDroppedItemsKey != null) ? currentField.data.itemDroppedItemsKey : "config.widgets"
               lang.setObject(itemDroppedItemsKey, defs, newDef);

               // It's also possible to "mixin" data from the definition directly into the config for the new definition. This
               // has been added to support widgets dropped into a publication that is dropped into a widget (e.g. the form controls in
               // a request dialog publication dropped into a menu item)...
               if (currentField.data.itemDroppedMixinKey != null)
               {
                  try
                  {
                     var mixinValue = lang.getObject(currentField.data.itemDroppedMixinKey, false, defs);
                     if (mixinValue != null)
                     {
                        lang.mixin(newDef.config, mixinValue);
                     }
                  }
                  catch (e)
                  {
                     this.alfLog("warn", "An error occurred mixing in dropped attributes");
                  }
               }
            }
         }
      },
      
      /**
       * @instance
       * @returns {object[]} The items contained by any widgets nested within the current DropZone.
       */
      getCurrentItems: function alfresco_creation_DropZone__getCurrentItems() {
         var currentItems = [];
         array.forEach(this.previewTarget.getAllNodes(), function(node, i) {
            currentItems.push(this.previewTarget.getItem(node.id));
         }, this);
         return currentItems;
      },
      
      /**
       * Publishes an array of the names of all of the currently configured fields.
       * 
       * @instance
       */
      publishAvailableFields: function alfresco_creation_DropZone__publishAvailableFields() {
         var payload = {};
         payload.options = this.getAvailableFields();
         this.alfLog("log", "Publishing available fields:", payload, this);
         this.alfPublish(this.childPubSubScope + "ALF_FORM_FIELDS_UPDATE", payload, true);
      },
      
      /**
       * @instance
       * @param {object} evt The event.
       */
      deleteItem: function alfresco_creation_DropZone__deleteItem(evt) {
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
            
            // Emit the event to alert wrapping widgets to changes...
            this.emitOnWidgetUpdate();
            
            this.publishAvailableFields();
         }
      },
      
      /**
       * Handles updates to the configuration for a currently rendered item.
       * 
       * @instance
       */
      updateItem: function alfresco_creation_DropZone__updateItem(payload) {
         // Check that the updated item belongs to this DropZone instance (this is done to prevent
         // multiple DropZone instances trying to modify the wrong objects)...
         if (payload.node != null)
         {
            var myNode = array.some(this.previewTarget.getAllNodes(), function(node, i) {
               return payload.node.id == node.id;
            });
            if (myNode == true)
            {
               this.alfLog("log", "Updating item", payload);

               var clonedConfig = lang.clone(payload.originalConfig);

               // TODO: Potentially fragile...
               var fieldId = payload.originalConfig.defaultConfig.fieldId;
               var widgetsForDisplay = this.alfGetData(fieldId + "__widgetsForDisplay");
               
               // Get the widgetsForConfig data from the data model...
               var widgetsForConfig = this.alfGetData(payload.originalConfig.defaultConfig.fieldId + "__widgetsForConfig");

               // Create a new attribute to store the updated configuration, this is going to be 
               // set using the dot-notation properties so that attributes such as "clonedConfig.updatedConfig.defaultConfig.x"
               // might exist...
               clonedConfig.updatedConfig = {};

               // Update the original configuration with the new data...
               for (var key in payload.updatedConfig)
               {
                  // Use "setObject" to allow keys to be dot-notation properties.
                  lang.setObject(key, payload.updatedConfig[key], clonedConfig);
                  lang.setObject(key, payload.updatedConfig[key], clonedConfig.updatedConfig);
               }

               // Update the configuration values...
               // for (var i=0; i<clonedConfig.widgetsForConfig.length; i++)
               // {
               //    // clonedConfig.widgetsForConfig[i].config.value = payload.updatedConfig[clonedConfig.widgetsForConfig[i].config.name];
               //    clonedConfig.widgetsForConfig[i].config.value = lang.getObject(clonedConfig.widgetsForConfig[i].config.name, false, payload.updatedConfig);
               // }
               
               // Update the config...
               for (var i=0; i<widgetsForConfig.length; i++)
               {
                  // clonedConfig.widgetsForConfig[i].config.value = payload.updatedConfig[clonedConfig.widgetsForConfig[i].config.name];
                  widgetsForConfig[i].config.value = lang.getObject(widgetsForConfig[i].config.name, false, payload.updatedConfig);
               }
               // ...and save it
               this.alfSetData(fieldId + "__widgetsForConfig", widgetsForConfig);

               // Update the previously stored name the field name
               // Commented out due to missing .config.aname on payload.updatedConfig
//               this.alfSetData(clonedConfig.defaultConfig.fieldId + ".name", payload.updatedConfig.config.name + "");
               
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
               
               // Set the widgets for display...
               if (widgetsForDisplay != null)
               {
                  clonedConfig.widgetsForDisplay = widgetsForDisplay;
               }
               if (widgetsForConfig != null)
               {
                  clonedConfig.widgetsForConfig = widgetsForConfig;
               }

               try
               {
                  // Create the updated object...
                  this.previewTarget.insertNodes(false, [clonedConfig], true, payload.node);
               }
               catch(e)
               {
                  this.alfLog("log", "Error", e);
               }
               finally
               {
                  this.alfLog("log", "Finally");
               }
               
               // Remove the previous nodes...
               this.previewTarget.deleteSelectedNodes();

               // Emit the event to alert wrapping widgets to changes...
               this.emitOnWidgetUpdate();
               
               // Publish the details of the latest fields...
               this.publishAvailableFields();
            }
         }
      },
      
      /**
       * This handles the creation of the widget in the preview panel.
       * 
       * @instance
       */
      creator: function alfresco_creation_DropZone__creator(item, hint) {
         this.alfLog("log", "Creating", item, hint);
         
         var node = domConstruct.create("div");
         if (item.module != null && item.module != "")
         {
            // Clone the supplied item... there are several potential possibilities for this
            // creator being called. Either an avatrar is required (should actually be handled
            // separately) a new field is being created or an existing field is being moved.
            // It's important that we create a fieldId if not defined (e.g. when creating a new
            // field) but preserve any existing values.
            var clonedItem = lang.clone(item); 
            var config = (clonedItem.defaultConfig != null) ? clonedItem.defaultConfig : {};
            if (config.fieldId === undefined)
            {
               config.fieldId = this.generateUuid();
            }
            
            // Preview the widget within the wrapper if requested or if no widgetsForDisplay
            // configuration has been provided...

            var widgets = this.alfGetData(config.fieldId + "__widgetsForDisplay");
            if (widgets == null)
            {
               if (item.previewWidget == true || 
                item.widgetsForDisplay == null ||
                item.widgetsForDisplay.length == 0)
               {
                  widgets = [
                     {
                        name: item.module,
                        config: config
                     }
                  ];

                  // this.alfSetData(config.fieldId + ".widgetsForDisplay", widgets);
               }
               else
               {
                  // TODO: This won't work for first creation (the original data needs to be provided)
                  // widgets = this.alfGetData(config.fieldId + ".widgetsForDisplay");
                  widgets = item.widgetsForDisplay;
               }
               
               this.alfSetData(config.fieldId + "__widgetsForDisplay", widgets);
            }

            // Add in any additional configuration...
            // TODO: Need to make sure that cloning isn't required?
            if (this.widgetsForNestedConfig != null)
            {
               // Check to see if the item passed in has the "originalConfigWidgets" attribute set...
               // If it doesn't then this is a create triggered by dragging from the palette. If it has
               // the attribute set then this call has been triggered by an update...
               if (item.originalConfigWidgets === undefined)
               {
                  clonedItem.originalConfigWidgets = lang.clone(item.widgetsForConfig);
               }
               
               // Create the additional configuration widgets...
               clonedItem.widgetsForConfig = clonedItem.originalConfigWidgets.concat(lang.clone(this.widgetsForNestedConfig));
               
               // Make sure that each of the additional widgets is set with an up-to-date value...
               array.forEach(clonedItem.widgetsForConfig, function(widget, i) {
                  var updatedValue = lang.getObject(widget.config.name, false, clonedItem);
                  if (updatedValue != null)
                  {
                     widget.config.value = updatedValue;
                  }
               }, this);
            }

            // Store the field id against the updated config...
            // This needs to be saved so it can be retrieved when inserting nodes again...
            this.alfSetData(config.fieldId, clonedItem);

            // Store the widgets for config data...
            // This will get updated when the widget is saved...
            this.alfSetData(config.fieldId + "__widgetsForConfig", clonedItem.widgetsForConfig);
            
            var widgetWrapper = new DropZoneWrapper({
               fieldId: config.fieldId,
               parentPubSubScope: this.childPubSubScope,
               pubSubScope: this.pubSubScope,
               widgets: widgets,
               moduleName: item.name
            }, node);
            
            // Store the field name
            // this.alfSetData(config.fieldId + ".name", clonedItem.defaultConfig.name + "");
         }
         else
         {
            this.alfLog("log", "The requested item to create was missing a 'module' attribute", item, this);
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
       * @param {object} e The selection event
       */
      onWidgetSelected: function alfresco_creation_DropZone__onWidgetSelected(e) {
         var selectedNodes = this.previewTarget.getSelectedNodes();
         if (selectedNodes.length > 0 && selectedNodes[0] != null)
         {
            var selectedItem = this.previewTarget.getItem(selectedNodes[0].id);
            this.alfLog("log", "Widget selected", selectedItem);
            var payload = {
               pubSubScope: this.childPubSubScope,
               selectedNode: selectedNodes[0],
               selectedItem: selectedItem.data
            };
            this.alfPublish("ALF_CONFIGURE_WIDGET", payload);
         }
      },
      
      /**
       * This function is called as an after aspect of the onDrop function of the DND target. It is used
       * to capture widgets being added into the DropZone and emits an event intended for it's own 
       * DropZoneWrapper and DropZones in it's hierarchy (if any exist) so that they can update their 
       * own configuration with the details of their internal DropZones. This is done so that when an outer
       * DropZone is updates (e.g. it's configuration is changed) that the internals can be faithfully
       * be recreated.
       * 
       * @instance
       */
      emitOnWidgetUpdate: function alfresco_creation_DropZone__emitOnWidgetUpdate() {
         this.alfLog("log", "Widgets updated");
         
         // At this point we're about to return the information that will be used to add a new item
         // to the DND target. This information contains the data for displaying the widget configuration.
         // However, if this DropZone is nested (e.g. a MenuBarItem within a MenuBar) then the MenuBar 
         // object needs to be notified of the new content...
         on.emit(this.domNode, "onWidgetUpdate", {
            bubbles: true,
            cancelable: true,
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: this.horizontal,
                     widgetsForNestedConfig: this.widgetsForNestedConfig
                  }
               }
            ]
         });
      }
   });
});