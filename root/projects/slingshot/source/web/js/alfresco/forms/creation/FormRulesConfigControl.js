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
define(["alfresco/forms/controls/MultipleEntryFormControl",
        "dojo/_base/declare",
        "alfresco/forms/creation/FormRulesConfigCreator"], 
        function(MultipleEntryFormControl, declare, FormRulesConfigCreator) {
   
   return declare([MultipleEntryFormControl], {
      
      getWidgetConfig: function() {
         
         // It's important that we set the pubSubScope as being the "fieldChangePubSubScope". This
         // is done so that the widget can listen to changes regards available fields. The list of
         // available fields is important for form rules because they need to be assigned to a field
         // to work against.
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value,
            pubSubScope: this.fieldChangePubSubScope,
            parent_alfMultipleElementId: this.parent_alfMultipleElementId,
            availableFieldsFunction: this.availableFieldsFunction,
            availableFieldsFunctionContext: this.availableFieldsFunctionContext
         };
      },
      
      createFormControl: function(config, domNode) {
         return new FormRulesConfigCreator(config);
      }
   });
});