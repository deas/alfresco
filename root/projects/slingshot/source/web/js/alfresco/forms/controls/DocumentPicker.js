/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * @module alfresco/forms/controls/DocumentPicker
 * @extends module:alfresco/forms/controls/Picker
 * @mixes module:alfresco/core/CoreWidgetProcessing
 * @author Dave Draper
 */
define(["alfresco/forms/controls/Picker",
        "alfresco/core/CoreWidgetProcessing",
        "dojo/_base/declare",
        "dojo/_base/lang"], 
        function(BaseFormControl, CoreWidgetProcessing, declare, lang) {
   
   return declare([BaseFormControl, CoreWidgetProcessing], {
      
      /**
       * 
       * @instance
       * @type {object}
       * @default
       */
      widgetsForControl: [
         {
            name: "alfresco/layout/VerticalWidgets",
            assignTo: "verticalWidgets",
            config: {
               widgets: [
                  {
                     name: "alfresco/pickers/PickedItems",
                     assignTo: "pickedItemsWidget"
                  },
                  {
                     name: "alfresco/buttons/AlfButton",
                     assignTo: "formDialogButton",
                     config: {
                        label: "Add",
                        publishTopic: "ALF_CREATE_DIALOG_REQUEST",
                        publishPayload: {
                           dialogTitle: "Select...",
                           handleOverflow: false,
                           widgetsContent: [
                              {
                                 name: "alfresco/pickers/Picker"
                              }
                           ],
                           widgetsButtons: [
                              {
                                 name: "alfresco/buttons/AlfButton",
                                 config: {
                                    label: "OK",
                                    publishTopic: "ALF_ITEMS_SELECTED"
                                 }
                              },
                              {
                                 name: "alfresco/buttons/AlfButton",
                                 config: {
                                    label: "Cancel",
                                    publishTopic: "NO_OP"
                                 }
                              }
                           ]
                        }
                     }
                  },
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: "Remove All",
                        additionalCssClasses: "cancelButton",
                        publishTopic: "ALF_ITEMS_SELECTED",
                        publishPayload: {
                           pickedItems: []
                        }
                     }
                  }
               ]
            }
         }
      ]
   });
});