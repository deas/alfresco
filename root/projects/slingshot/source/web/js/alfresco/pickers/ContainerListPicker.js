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
 * <p>This extends the standard [document list]{@link module:alfresco/documentlibrary/AlfDocumentList} to 
 * define a document list specifically for selecting containers (folders) (e.g. for copy and move targets, etc). It was
 * written to be used as part of a [picker]{@link module:alfresco/pickers/Picker}.</p>
 * 
 * @module alfresco/pickers/ContainerListPicker
 * @extends module:alfresco/pickers/DocumentListPicker
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/pickers/DocumentListPicker",
        "dojo/_base/lang"], 
        function(declare, DocumentListPicker, lang) {
   
   return declare([DocumentListPicker], {

      /**
       * Overrides the [inherited function]{@link module:alfresco/lists/AlfList#postCreate} to create the picker
       * view for selecting documents.
       * 
       * @instance
       */
      postCreate: function alfresco_pickers_ContainerListPicker__postCreate(payload) {
         var config = [{
            name: "alfresco/documentlibrary/views/AlfDocumentListView",
            config: {
               widgets: [
                  {
                     name: "alfresco/documentlibrary/views/layouts/Row",
                     config: {
                        renderFilter: [
                             {
                                property: "node.isContainer",
                                values: [true]
                             }
                          ],
                          widgets: [
                           {
                              name: "alfresco/documentlibrary/views/layouts/Cell",
                              config: {
                                 width: "20px",
                                 widgets: [
                                    {
                                       name: "alfresco/renderers/FileType",
                                       config: {
                                          size: "small",
                                          renderAsLink: true,
                                          publishTopic: "ALF_DOCLIST_NAV"
                                       }
                                    }
                                 ]
                              }
                           },
                           {
                              name: "alfresco/documentlibrary/views/layouts/Cell",
                              config: {
                                 widgets: [
                                    {
                                       name: "alfresco/renderers/PropertyLink",
                                       config: {
                                          propertyToRender: "node.properties.cm:name",
                                          renderAsLink: true,
                                          publishTopic: "ALF_DOCLIST_NAV"
                                       }
                                    }
                                 ]
                              }
                           },
                           {
                              name: "alfresco/documentlibrary/views/layouts/Cell",
                              config: {
                                 width: "20px",
                                 widgets: [
                                    {
                                       name: "alfresco/renderers/PublishAction",
                                       config: {
                                          publishPayloadType: "CURRENT_ITEM"
                                       }
                                    }
                                 ]
                              }
                           }
                        ]
                     }
                  }
               ]
            }
         }];
         this.processWidgets(config, this.itemsNode);
      },

        // postCreate: function alfresco_pickers_ContainerListPicker__postCreate(payload) {
        //    var config = [{
        //       name: "alfresco/documentlibrary/views/AlfDocumentListView",
        //       config: {
        //          widgets: [
        //             {
        //                name: "alfresco/documentlibrary/views/layouts/Row",
        //                config: {
        //                   renderFilter: [
        //                      {
        //                         property: "node.isContainer",
        //                         values: [true]
        //                      }
        //                   ],
        //                   widgets: [
        //                      {
        //                         name: "alfresco/documentlibrary/views/layouts/Cell",
        //                         config: {
        //                            widgets: [
        //                               {
        //                                  name: "alfresco/renderers/PropertyLink",
        //                                  config: {
        //                                     propertyToRender: "node.properties.cm:name",
        //                                     renderAsLink: true,
        //                                     publishTopic: "ALF_ADD_PICKER",
        //                                     useCurrentItemAsPayload: false,
        //                                     publishPayloadType: "BUILD",
        //                                     publishPayload: {
        //                                        currentPickerDepth: null,
        //                                        picker: {
        //                                           name: "alfresco/pickers/ContainerListPicker",
        //                                           config: {
        //                                              libraryRoot: {
        //                                                 alfType: "item",
        //                                                 alfProperty: "nodeRef"
        //                                              },
        //                                              nodeRef: {
        //                                                 alfType: "item",
        //                                                 alfProperty: "nodeRef"
        //                                              },
        //                                              // TODO: Is this path correct?
        //                                              path: "/documentlibrary"
        //                                           }
        //                                        }
        //                                     }
        //                                  }
        //                               }
        //                            ]
        //                         }
        //                      },
        //                      {
        //                         name: "alfresco/documentlibrary/views/layouts/Cell",
        //                         config: {
        //                            width: "20px",
        //                            widgets: [
        //                               {
        //                                  name: "alfresco/renderers/PublishAction",
        //                                  config: {
        //                                     publishGlobal: true
        //                                  }
        //                               }
        //                            ]
        //                         }
        //                      }
        //                   ]
        //                }
        //             }
        //          ]
        //       }
        //    }];

        //    this.processWidgets(config, this.itemsNode);
        // }
   });
});