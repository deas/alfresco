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
       * Overrides the [inherited value]{@link moduule:alfresco/lists/AlfList#waitForPageWidgets} to ensure that pickers
       * don't wait for the page to be loaded (as typically the page will be loaded long before the picker is opened).
       * This can still be overridden again in configuration when creating a new picker.
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      waitForPageWidgets: false,

      /**
       * Overrides the [inherited value]{@link moduule:alfresco/lists/AlfHashList#useHash} to indicate that the location
       * should not be driven by changes to the browser URL hash
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      useHash: false,

      /**
       * Overrides the [inherited function]{@link module:alfresco/lists/AlfList#postCreate} to create the picker
       * view for selecting documents.
       *
       * @instance
       */
      postCreate: function alfresco_pickers_DocumentListPicker__postCreate(payload) {
         var config = [{
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
                                          publishPayloadType: "CURRENT_ITEM",
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
         }];
         this.processWidgets(config, this.itemsNode);
      },

      /**
       * Override the default implementation to call [loadData]{@link module:alfresco/documentlibrary/AlfDocumentList#loadData}
       * with the currently selected folder node.
       *
       * @instance
       * @param {object} payload
       * @todo Refactor this code to accept the same payload format as {@link module:alfresco/documentlibrary/AlfDocumentList#onItemClick}
       */
      onFolderClick: function alfresco_pickers_DocumentListPicker__onFolderClick(payload) {
         var targetNode = lang.getObject("item.nodeRef", false, payload) || lang.getObject("node.nodeRef", false, payload) || payload.nodeRef;
         if (targetNode != null)
         {
            this.nodeRef = targetNode;
            this.loadData();
         }
         else
         {
            this.alfLog("warn", "A 'nodeRef' attribute was expected to be provided for a folder click", payload, this);
         }
      },

      /**
       * Overrides inherited function to do a no-op. The pick action should be handled by a
       * [PublishAction widget]{@link module:alfresco/renderers/PublishAction}.
       *
       * @instance
       * @param {object} payload
       */
      onDocumentClick: function alfresco_pickers_DocumentListPicker__onDocumentClick(payload) {
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

      ]
   });
});