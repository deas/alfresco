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
 * @module alfresco/kickstart/StepDropAndPreview
 * @extends module:alfresco/creation/DropAndPreview
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/creation/DropAndPreview",
        "dojo/text!./templates/StepTemplate.html",
        "dojo/_base/lang",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dojo/dom-attr",
        "dojo/string"], 
        function(declare, DropAndPreview, StepTemplate, lang, domConstruct, domClass, domAttr, stringUtil) {
   
   return declare([DropAndPreview], {
      
      /**
       * Overridden to avoid the WidgetDragWrapper being created
       * 
       * @instance
       */
      creator: function alfresco_kickstart_StepDropAndPreview__creator(item, hint) {
         this.alfLog("log", "Creating", item, hint);
         
         if (item.data.module != null && item.data.module != "")
         {
            // Clone the default item information (this is necessary to ensure that we don't end up sharing the
            // same item between multiple steps).
            var clonedItem = lang.clone(item); 
            var config = (clonedItem.data.defaultConfig != null) ? clonedItem.data.defaultConfig : {};
            if (config.id === undefined)
            {
               config.id = this.generateUuid();
            }
            if (config.fieldId === undefined)
            {
               config.fieldId = config.id;
            }
            if (config.pubSubScope === undefined)
            {
               config.pubSubScope = config.id;
            }

            // Register an entry in the data model and set the display name
            this.alfSetData(config.id + ".name", clonedItem.data.defaultConfig.stepTitle + "");
            
            // Subscribe to requests for available fields on the pubSubScope of the new step...
            // Ensure that the pubSubScope is passed in the hitch to the callback function so that
            // the available fields are published on the correct scope. It's important to set
            // the global flag on the subscription request to allow us to control the scoping manually...
            this.alfSubscribe(config.pubSubScope + "ALF_REQUEST_AVAILABLE_FORM_FIELDS", lang.hitch(this, "publishAvailableFields", config.pubSubScope), true);
            
            // Note how we assign to the "lastCreatedNode" which is then returned as an attribute of the created
            // object. This is done so that the DND item is the root widget. If the widgets had been processed
            // into a created DOM node and that was returned then there would be a DOM wrapper that we don't want
            // around the widget.
            var widgets = [
               {
                  name: clonedItem.data.module,
                  assignTo: "lastCreatedNode",
                  config: config
               }
            ]
            this.processWidgets(widgets);
         }
         
         // TODO: This is duplicated from the superclass - could be handled better?
         domClass.add(this.previewNode, "containsItems");
         return {node: this.lastCreatedNode.domNode, data: clonedItem, type: ["widget"]};
      },
      
      /**
       * Overrides the inherited function to publish the available fields on the pubSubScope argument.
       * This ensures that the available fields are published on the same scope that the request came from.
       * @instance
       * @param {string} pubSubScope The scope to publish on.
       */
      publishAvailableFields: function alfresco_kickstart_StepDropAndPreview__publishAvailableFields(pubSubScope) {
         var payload = {};
         payload.options = this.getAvailableFields();
         this.alfLog("log", "Publishing available fields:", payload, this);
         this.alfPublish(pubSubScope + "ALF_FORM_FIELDS_UPDATE", payload, true);
      },
   });
});