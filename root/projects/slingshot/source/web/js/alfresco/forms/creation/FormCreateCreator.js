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
define(["alfresco/forms/controls/MultipleEntryCreator",
        "alfresco/forms/creation/FormCreateCreatorElement",
        "dojo/_base/declare",
        "dojo/_base/array",
        "dijit/registry",
        "dojo/dom-construct"], 
        function(MultipleEntryCreator, FormCreateCreatorElement, declare, array, registry, domConstruct) {
   
   return declare([MultipleEntryCreator], {
      
      /**
       * This holds a Publication/Subscription scope that is purely used for creating notifications
       * regarding changes of fields. It is instantiated in the constructor and is passed through
       * to all the form controls that the FormCreateCreatorElement instantiates.
       */
      fieldChangePubSubScope: null,
      
      /**
       * Overrides the default Drag-And-Drop type to prevent other objects being dropped into the 
       * creator (for example, we don't want the options or the rules or anything else to be dropped
       * in).
       */
      getDNDType: function() {
         return "FormCreationField";
      },
      
      /**
       * Override the default avatar node construction so that we use the field attribute from the value
       * to indicate what is being dragged.
       */
      createDNDAvatarNode: function(widget) {
         return domConstruct.create("div", { innerHTML: (widget && widget.value && widget.value.field) ? widget.value.field : ""});
      },
      
      constructor: function(args) {
         declare.safeMixin(this, args); 
         this.fieldChangePubSubScope = this.generateUuid();
      },

      /**
       * This method returns the list of available fields and is passed on to any descendent widgets within the form
       * creation process that need to know what fields currently are available to configure.
       */
      getAvailableFields: function(_this, requestingField_alfMultipleElementId) {
         
         var availableFields = [];
         var wrappers = registry.findWidgets(_this.currentEntries);
         array.forEach(wrappers, function(wrapper, index) {
            if (wrapper.widget && wrapper.widget.value && wrapper.widget.value._alfMultipleElementId)
            {
               if (wrapper.widget.value._alfMultipleElementId == requestingField_alfMultipleElementId)
               {
                  // We don't want to add the details for the field that is requesting the list of available
                  // fields. This is because this operation is normally done by some rule (e.g. visibility, etc)
                  // requesting the names of the OTHER fields that it can configure it's behaviour against. There
                  // is no point in returning the requesting field.
               }
               else
               {
                  availableFields.push({label: wrapper.widget.value.field, value: wrapper.widget.value._alfMultipleElementId});
               }
            }
            else
            {
               _this.alfLog("warn", "A FormCreateCreatorElement is missing an '_alfMultipleElementId' attribute (detected when adding an entry)", wrapper);
            }
         });
         return availableFields;
      },
      
      createElementWidget: function(elementConfig) {

         // Create a new UUID to use as the publication/subscription scope for the element to be created...
         var pubSubScope = this.generateUuid(); 
         
         // The element configuration should be an object where the key is the name of property it's been configured for
         // Fortunately, the key will be set when the widget is created...
         var widget = new FormCreateCreatorElement({pubSubScope: pubSubScope,
                                                    fieldChangePubSubScope: this.fieldChangePubSubScope,
                                                    availableFieldsFunction: this.getAvailableFields,
                                                    availableFieldsFunctionContext: this,
                                                    elementConfig: elementConfig});
         return widget;
      },
      
      postCreate: function() {
         // Call the main post create to create all the widgets...
         this.inherited(arguments);
      },
      
      /**
       * Publishes the currently available fields. This is done using the "fieldChangePubSubScope" which is reserved
       * for all child controls that need to subscribe to updates regarding the available fields.
       */
      publishAvailableFields: function() {
         this.alfLog("log", "Publishing available fields: ", this.availableFieldsToConfigure);
         var currentFieldsPayload = {
            availableFields: this.availableFieldsToConfigure
         };
         this.alfPublish(this.fieldChangePubSubScope + "_availableFields", currentFieldsPayload);
      }
   });
});