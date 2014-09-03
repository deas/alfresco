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
 * @module alfresco/forms/controls/ContainerPicker
 * @extends module:alfresco/forms/controls/Picker
 * @mixes module:alfresco/core/CoreWidgetProcessing
 * @author Dave Draper
 */
define(["alfresco/forms/controls/Picker",
        "dojo/_base/declare"], 
        function(Picker, declare) {
   
   return declare([Picker], {
      
      /**
       * This should be overridden to define the widget model for rendering the picker that appears within the 
       * dialog.
       *
       * @instance
       * @type {object}
       * @default []
       */
      configForPicker: {
         generatePubSubScope: true,
         widgetsForPickedItems: [
            {
               name: "alfresco/pickers/PickedItems",
               assignTo: "pickedItemsWidget"
            }
         ],
         widgetsForRootPicker: [
            {
               name: "alfresco/menus/AlfVerticalMenuBar",
               config: {
                  widgets: [
                     {
                        name: "alfresco/menus/AlfMenuBarItem",
                        config: {
                           label: "My Files",
                           publishTopic: "ALF_ADD_PICKER",
                           publishPayload: {
                              currentPickerDepth: 0,
                              picker: {
                                 name: "alfresco/pickers/ContainerListPicker",
                                 config: {
                                    nodeRef: "alfresco://user/home",
                                    path: "/"
                                 }
                              }
                           }
                        }
                     },
                     {
                        name: "alfresco/menus/AlfMenuBarItem",
                        config: {
                           label: "Shared Files",
                           publishTopic: "ALF_ADD_PICKER",
                           publishPayload: {
                              currentPickerDepth: 0,
                              picker: {
                                 name: "alfresco/pickers/ContainerListPicker",
                                 config: {
                                    nodeRef: "alfresco://company/shared",
                                    filter: {
                                       path: "/"
                                    }
                                 }
                              }
                           }
                        }
                     },
                     {
                        name: "alfresco/menus/AlfMenuBarItem",
                        config: {
                           label: "Repository",
                           publishTopic: "ALF_ADD_PICKER",
                           publishPayload: {
                              currentPickerDepth: 0,
                              picker: {
                                 name: "alfresco/pickers/ContainerListPicker",
                                 config: {
                                    nodeRef: "alfresco://company/home",
                                    path: "/"
                                 }
                              }
                           }
                        }
                     },
                     {
                        name: "alfresco/menus/AlfMenuBarItem",
                        config: {
                           label: "Recent Sites",
                           publishTopic: "ALF_ADD_PICKER",
                           publishPayload: {
                              currentPickerDepth: 0,
                              picker: {
                                 name: "alfresco/pickers/SingleItemPicker",
                                 config: {
                                    subPicker: "alfresco/pickers/ContainerListPicker",
                                    currentPickerDepth: 1,
                                    requestItemsTopic: "ALF_GET_RECENT_SITES"
                                 }
                              }
                           }
                        }
                     },
                     {
                        name: "alfresco/menus/AlfMenuBarItem",
                        config: {
                           label: "Favorite Sites",
                           publishTopic: "ALF_ADD_PICKER",
                           publishPayload: {
                              currentPickerDepth: 0,
                              picker: {
                                 name: "alfresco/pickers/SingleItemPicker",
                                 config: {
                                    subPicker: "alfresco/pickers/ContainerListPicker",
                                    currentPickerDepth: 1,
                                    requestItemsTopic: "ALF_GET_FAVOURITE_SITES"
                                 }
                              }
                           }
                        }
                     }
                  ]
               }
            }
         ]
      }
   });
});