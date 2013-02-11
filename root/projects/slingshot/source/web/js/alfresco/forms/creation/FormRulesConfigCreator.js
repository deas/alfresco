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
        "alfresco/forms/creation/FormRulesConfigCreatorElement",
        "dojo/_base/declare",
        "dojo/_base/array",
        "dijit/registry",
        "dojo/_base/lang"], 
        function(MultipleEntryCreator, FormRulesConfigCreatorElement, declare, array, registry, lang) {
   
   return declare([MultipleEntryCreator], {
      
      /**
       * This is used as the scope for publishing/subscribing to events regarding the selection of fields.
       * It is shared by all the elements created so that they know which of the available fields have already
       * been selected and can filter them from their available list.
       */
      fieldSelectPubSubScope: null,
      
      /**
       * This keeps track of all the fields that are currently being used in a rule. This list is managed from 
       * within this instance and is shared with its elements as the list is updated.
       */
      selectedFields: null,
      
      constructor: function(args) {
         declare.safeMixin(this, args); 
         
         // We generate a new UUID to use as a scope for sharing field selection events...
         this.fieldSelectPubSubScope = this.generateUuid();
         this.selectedFields = [];
      },
      
      createElementWidget: function(elementConfig) {
         
         // The element configuration should be an object where the key is the name of property it's been configured for
         // Fortunately, the key will be set when the widget is created...
         // TODO: Should we set a specific scope for field updates...
         var widget = new FormRulesConfigCreatorElement({pubSubScope: this.pubSubScope,
                                                         fieldSelectPubSubScope: this.fieldSelectPubSubScope,
                                                         parent_alfMultipleElementId: this.parent_alfMultipleElementId,
                                                         selectedFields: this.selectedFields,
                                                         availableFieldsFunction: this.availableFieldsFunction,
                                                         availableFieldsFunctionContext: this.availableFieldsFunctionContext,
                                                         value: elementConfig});
         return widget;
      },
      
      postCreate: function() {
         // Call the main post create to create all the widgets...
         this.inherited(arguments);

         var _this = this;
         this.alfSubscribe(this.fieldSelectPubSubScope + "_fieldSelected", function(payload) {
            _this.availableFieldsToConfigure = payload.availableFields;
         });
         
      }
   });
});