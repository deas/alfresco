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
 * 
 * @module alfresco/pickers/ContainerPicker
 * @extends module:alfresco/pickers/Picker
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/pickers/Picker"], 
        function(declare, Picker) {
   
   return declare([Picker], {

      /**
       * The default widgets for the picker. This can be overridden at instantiation based on what is required to be 
       * displayed in the picker.
       *
       * @instance
       * @type {object}
       */
      widgetsForRootPicker: [
         {
            name: "alfresco/menus/AlfVerticalMenuBar",
            config: {
               widgets: [
                  {
                     name: "alfresco/menus/AlfMenuBarItem",
                     config: {
                        label: "picker.myFiles.label",
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
                        label: "picker.sharedFiles.label",
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
                        label: "picker.repository.label",
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
                        label: "picker.recentSites.label",
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
                        label: "picker.favouriteSites.label",
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
      ],

      /**
       * This is the widget model for displaying picked items. It will only be displayed when
       * requested.
       *
       * @instance
       * @type {object}
       * @default
       */
      widgetsForPickedItems: [
         {
            name: "alfresco/pickers/PickedItems",
            assignTo: "pickedItemsWidget"
         }
      ]
   });
});