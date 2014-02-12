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
 * define a document list specifically for selecting documents (e.g. for starting workflows, etc). It was
 * written to be used as part of a [picker]{@link module:alfresco/pickers/Picker} and specifically one that
 * is used as a form control.</p>
 * 
 * @module alfresco/pickers/DocumentListPicker
 * @extends module:alfresco/documentlibrary/AlfDocumentList
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/documentlibrary/AlfDocumentList", 
        "dojo/_base/lang"], 
        function(declare, AlfDocumentList, lang) {
   
   return declare([AlfDocumentList], {

      /**
       * Indicates whether the location should be driven by changes to the browser URL hash
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      useHash: false,

      /**
       * Override the default implementation to call [loadData]{@link module:alfresco/documentlibrary/AlfDocumentList#loadData}
       * with the currently selected folder node.
       *
       * @instance
       * @param {object} payload
       */
      onFolderClick: function alfresco_pickers_DocumentListPicker__onFolderClick(payload) {

         var targetNode = lang.getObject("item.nodeRef", false, payload);
         if (targetNode != null)
         {
            this.nodeRef = targetNode;
            this.loadData();
         }
         else
         {
            this.alfLog("warn", "A 'url' attribute was expected to be provided for an item click", payload, this);
         }
      },

      /**
       * Overrides inherited function to do a no-op. The pick action should be handled by a 
       * [PublishAction widget]{@link module:alfresco/renderers/PublishAction}.
       *
       * @instance
       * @param {object} payload
       */
      onDocumentClick: function alfresco_pickers_DocumentListPicker__onFolderClick(payload) {
         // No action.
      },

      /**
       * The default widgets for the picker. This can be overridden at instantiation based on what is required to be 
       * displayed in the picker.
       *
       * @instance
       * @type {object}
       */
      widgets: [
         {
            name: "alfresco/documentlibrary/views/AlfDocumentListView",
            config: {
               widgets: [
                  {
                     name: "alfresco/documentlibrary/views/layouts/Row",
                     config: {
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
                                          linkClickTopic: "ALF_DOCLIST_NAV"
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
                                       name: "alfresco/renderers/Property",
                                       config: {
                                          propertyToRender: "node.properties.cm:name",
                                          renderAsLink: true,
                                          linkClickTopic: "ALF_DOCLIST_NAV"
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
                                          renderFilter: [
                                             {
                                                property: "node.isContainer",
                                                values: [false]
                                             }
                                          ]
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
         }
      ]
   });
});