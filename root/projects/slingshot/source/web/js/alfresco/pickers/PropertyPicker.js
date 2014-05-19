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
 * define a document list specifically for selecting properties. It was
 * written to be used as part of a [picker]{@link module:alfresco/pickers/Picker} and specifically one that
 * is used as a form control.</p>
 * 
 * @module alfresco/pickers/PropertyPicker
 * @extends module:alfresco/documentlibrary/AlfDocumentList
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/documentlibrary/AlfDocumentList",
        "alfresco/core/CoreXhr", 
        "service/constants/Default",
        "dojo/_base/lang"], 
        function(declare, AlfDocumentList, CoreXhr, AlfConstants, lang) {
   
   return declare([AlfDocumentList, CoreXhr], {

      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/AlfDocumentList.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/PropertyPicker.properties"}],

      /**
       * Indicates whether the location should be driven by changes to the browser URL hash
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      useHash: false,

      /**
       * Override the [inherited value]{@link module:alfresco/documentlibrary/AlfDocumentList#waitForPageWidgets} because
       * this widget is typically created after the page has loaded.
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      waitForPageWidgets: false,

      /**
       * Override the default implementation to call [loadData]{@link module:alfresco/documentlibrary/AlfDocumentList#loadData}
       * with the currently selected folder node.
       *
       * @instance
       * @param {object} payload
       */
      onFolderClick: function alfresco_pickers_PropertyPicker__onFolderClick(payload) {

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
      onDocumentClick: function alfresco_pickers_PropertyPicker__onFolderClick(payload) {
         // No action.
      },

      /**
       * Sets some relevant messages to display
       *
       * @instance
       */
      setDisplayMessages: function alfresco_documentlibrary_AlfDocumentList__setDisplayMessages() {
         this.noViewSelectedMessage = this.message("propPicker.no.view.message");
         this.noDataMessage = this.message("propPicker.no.data.message");
         this.fetchingDataMessage = this.message("propPicker.loading.data.message");
         this.renderingViewMessage = this.message("propPicker.rendering.data.message");
         this.fetchingMoreDataMessage = this.message("propPicker.loading.data.message");
      },

      /**
       * The URL to use to request data from
       *
       * @instance
       * @type {string}
       * @default null
       */
      url: null,

      /**
       * Overrides the inherited implementation to request data.
       *
       * @instance
       */
      loadData: function alfresco_pickers_PropertyPicker__loadData() {
         this.showLoadingMessage();
         if (this.url != null)
         {
            var alfTopic = "ALF_RETRIEVE_DOCUMENTS_REQUEST";
            var url = AlfConstants.PROXY_URI + this.url;
            var config = {
               alfTopic: alfTopic,
               url: url,
               method: "GET",
               callbackScope: this
            };
            this.serviceXhr(config);
         }
         else
         {
            this.alfLog("warn", "No 'url' attribute provided to request data", this);
         }
      },

      /**
       * TODO: This should really be the abstract...
       * 
       * @instance
       * @param {object} response The response object
       * @param {object} originalRequestConfig The configuration that was passed to the the [serviceXhr]{@link module:alfresco/core/CoreXhr#serviceXhr} function
       */
      onDataLoadSuccess: function alfresco_pickers_PropertyPicker__onDataLoadSuccess(payload) {
         this.alfLog("log", "Data Loaded", payload, this);
         
         this._currentData = {
            items: payload.response
         };
         
         // Re-render the current view with the new data...
         var view = this.viewMap[this._currentlySelectedView];
         if (view != null)
         {
            this.showRenderingMessage();
            view.setData(this._currentData);
            view.renderView(this.useInfiniteScroll);
            this.showView(view);
            
            // Force a resize of the sidebar container to take the new height of the view into account...
            this.alfPublish("ALF_RESIZE_SIDEBAR", {});
         }
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
               noItemsMessage: "propPicker.no.data.message",
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
                                       name: "alfresco/renderers/Property",
                                       config: {
                                          propertyToRender: "name"
                                       }
                                    },
                                    {
                                       name: "alfresco/renderers/Property",
                                       config: {
                                          propertyToRender: "title",
                                          renderedValuePrefix: "(",
                                          renderedValueSuffix: ")",
                                          renderSize: "small"
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
                                          publishGlobal: false,
                                          publishToParent: true
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