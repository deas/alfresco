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
 * @module alfresco/documentlibrary/views/AlfTableView
 * @extends module:alfresco/documentlibrary/views/AlfDocumentListWithHeaderView
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/documentlibrary/views/AlfDocumentListWithHeaderView"], 
        function(declare, AlfDocumentListWithHeaderView, template) {
   
   return declare([AlfDocumentListWithHeaderView], {
      
      /**
       * Returns the name of the view that is used when saving user view preferences.
       * 
       * @instance
       * @returns {string} "simple"
       */
      getViewName: function alfresco_documentlibrary_views_AlfTableView__getViewName() {
         return "table";
      },
      
      /**
       * The configuration for selecting the view (configured the menu item)
       * @instance
       * @type {object}
       * @property {string|null} label The label or message key for the view (as appears in the menus)
       * @property {string|null} iconClass The class to place next to the label
       */
      viewSelectionConfig: {
         label: "Table View",
         iconClass: "alf-simplelist-icon"
      },
      
      widgetsForHeader: [
         {
            name: "alfresco/documentlibrary/views/layouts/HeaderCell",
            config: {
               label: "Selector",
               sortable: false
            }
         },
         {
            name: "alfresco/documentlibrary/views/layouts/HeaderCell",
            config: {
               label: "Indicators",
               sortable: false
            }
         },
         {
            name: "alfresco/documentlibrary/views/layouts/HeaderCell",
            config: {
               label: "Name",
               sortable: false
            }
         },
         {
            name: "alfresco/documentlibrary/views/layouts/HeaderCell",
            config: {
               label: "Actions",
               sortable: false
            }
         }
      ],


      /**
       * The definition of how a single item is represented in the view. 
       * 
       * @instance
       * @type {object[]}
       */
      widgets: [
         {
            name: "alfresco/documentlibrary/views/layouts/Row",
            config: {
               widgets: [
                  {
                     name: "alfresco/documentlibrary/views/layouts/Cell",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/renderers/Selector"
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/documentlibrary/views/layouts/Cell",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/renderers/Indicators"
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/documentlibrary/views/layouts/Cell",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/renderers/InlineEditProperty",
                              config: {
                                 propertyToRender: "node.properties.cm:name",
                                 postParam: "prop_cm_name",
                                 renderSize: "large",
                                 renderAsLink: true
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
                              name: "alfresco/renderers/Actions"
                           }
                        ]
                     }
                  }
               ]
            }
         }
      ]
   });
});