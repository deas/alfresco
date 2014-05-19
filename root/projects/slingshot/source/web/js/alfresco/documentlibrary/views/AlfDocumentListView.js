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
 * An abstract view for the Alfresco Share document list. It can be used in JSON page models if 
 * configured with a widgets definition. Otherwise it can be extended to define specific views
 * 
 * @module alfresco/documentlibrary/views/AlfDocumentListView
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes dijit/_KeyNavContainer
 * @mixes module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/documentlibrary/_AlfDndDocumentUploadMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/AlfDocumentListView.html",
        "alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin",
        "alfresco/documentlibrary/_AlfDndDocumentUploadMixin",
        "alfresco/documentlibrary/views/DocumentListRenderer",
        "alfresco/core/Core",
        "alfresco/core/JsNode",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dojo/query",
        "dijit/registry"], 
        function(declare, _WidgetBase, _TemplatedMixin, template, _MultiItemRendererMixin, _AlfDndDocumentUploadMixin, DocumentListRenderer, 
                 AlfCore, JsNode, lang, array, domConstruct, domClass, query, registry) {
   
   return declare([_WidgetBase, _TemplatedMixin, _MultiItemRendererMixin, AlfCore, _AlfDndDocumentUploadMixin], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/AlfDocumentListView.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/AlfDocumentListView.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {object[]}
       * @default [{cssFile:"./css/AlfDialog.css"}]
       */
      cssRequirements: [{cssFile:"./css/AlfDocumentListView.css"}],

      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * The widgets to be processed to generate each item in the rendered view.
       * 
       * @instance 
       * @type {object[]} 
       * @default null
       */
      widgets: null,
      
      /**
       * This can be set to be a custom message that is displayed when there are no items to
       * be displayed in the current view.
       *
       * @instance
       * @type {string}
       * @default null
       */
      noItemsMessage: null,

      /**
       * Should the widget subscribe to events triggered by the documents request?
       * This should be set to true in the widget config for standalone/isolated usage.
       *
       * @instance
       * @type Boolean
       * @default true
       */
      subscribeToDocRequests: false,

      /**
       * Implements the widget life-cycle method to add drag-and-drop upload capabilities to the root DOM node.
       * This allows files to be dragged and dropped from the operating system directly into the browser
       * and uploaded to the location represented by the document list. 
       * 
       * @instance
       */
      postCreate: function alfresco_documentlibrary_views_AlfDocumentListView__postCreate() {
         this.inherited(arguments);

         // Allow custom messages to be displayed when no items are available for display...
         if (this.noItemsMessage == null)
         {
            this.noItemsMessage = this.message("doclistview.no.data.message");
         }
         else
         {
            this.noItemsMessage = this.message(this.noItemsMessage);
         }

         this.addUploadDragAndDrop(this.domNode);

         if (this.subscribeToDocRequests)
         {
            this.alfSubscribe(this.filterChangeTopic, lang.hitch(this, "onFilterChange"));
            this.alfSubscribe("ALF_RETRIEVE_DOCUMENTS_REQUEST_SUCCESS", lang.hitch(this, "onDocumentsLoaded"));
         }
         if (this.currentData != null)
         {
            this.renderView();
         }

         this.alfSubscribe(this.clearDocDataTopic, lang.hitch(this, "clearOldView"));
      },
      
      /**
       * @instance
       * @param {object} payload The details of the documents that have been provided.
       */
      onDocumentsLoaded: function alfresco_documentlibrary_views_AlfDocumentListView__onDocumentsLoaded(payload) {
         var items = lang.getObject("response.items", false, payload);
         if (items != null)
         {
            for (var i = 0; i<items.length; i++)
            {
               items[i].jsNode = new JsNode(items[i].node);
            }

            this.setData(payload.response);
            this.renderView();
         }
         else
         {
            this.alfLog("warn", "Payload contained no 'response.items' attribute", payload, this);
         }
      },
      
      /**
       * Handles changes to the current filter and removes drag-and-drop capabilities for all bar the
       * "path" filter types (this is because only the path defines an actual location for uploading
       * files to).
       * 
       * @instance onClick
       * @param {object} payload 
       */
      onFilterChange: function alfresco_documentlibrary_views_AlfDocumentListView__onFilterChange(payload) {
         if (payload != null && payload.filterId == "path")
         {
            this.addUploadDragAndDrop(this.domNode);
         }
         else
         {
            this.removeUploadDragAndDrop(this.domNode);
         }
      },
      
      /**
       * The configuration for view selection menu items. This needs to be either configured or defined in an 
       * extending module. If this isn't specified then the view will not be selectable in the document list.
       * 
       * @instance
       * @type {Object}
       * @default {}
       */
      viewSelectionConfig: {
         label: "Abstract",
         value: "Abstract"
      },
      
      /**
       * This should be overridden to give each view a name. If it's not overridden then the view will just get given
       * a name of the index that it was registered with. It will still be possible to select the view but it will cause
       * issues with preferences.
       * 
       * @instance
       * @returns {string} "Abstract"
       */
      getViewName: function alfresco_documentlibrary_views_AlfDocumentListView__getViewName() {
         return this.viewSelectionConfig.value;
      },
      
      /**
       * This should be overridden to provide configuration for view selection. As a minimum, a localised label MUST be provided
       * as the "label" attribute. Other attributes that could be provided would be "iconClass". This configuration will typically
       * be used to construct a menu item. By default this just returns the 
       * [viewSelectionConfig]{@link module:alfresco/documentlibrary/views/AlfDocumentListView#viewSelectionConfig}
       * 
       * @instance
       * @returns {Object} The configuration for selecting the view.
       */
      getViewSelectionConfig: function alfresco_documentlibrary_views_AlfDocumentListView__getViewSelectionConfig() {
         return this.viewSelectionConfig;
      },
      
      
      /**
       * This function should be overridden to publish the details of any additional controls that are needed to control
       * view of the data that it provides. An example of a control would be the thumbnail size slider for the gallery 
       * control.
       * 
       * @instance getAdditionalControls
       * @returns {Object[]}
       */
      getAdditionalControls: function alfresco_documentlibrary_views_AlfDocumentListView__getAdditionalControls() {
         // For the abstract view there are no additional controls.
         return [];
      },
      
      /**
       * Calls the [renderData]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#renderData}
       * function if the [currentData]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentData}
       * attribute has been set to an object with an "items" attribute that is an array of objects.
       * 
       * @instance
       */
      renderView: function alfresco_documentlibrary_views_AlfDocumentListView__renderView() {
         if (this.currentData && this.currentData.items && this.currentData.items.length > 0)
         {
            try
            {
               if (this.docListRenderer != null)
               {
                  this.docListRenderer.destroy(true);
               }

               this.alfLog("log", "Rendering items", this);
               this.docListRenderer = new DocumentListRenderer({
                  id: this.id + "_ITEMS",
                  widgets: this.widgets,
                  currentData: this.currentData,
                  pubSubScope: this.pubSubScope
               });
               this.docListRenderer.placeAt(this.tableNode, "last");
               this.docListRenderer.renderData();
            }
            catch(e)
            {
               this.alfLog("error", "The following error occurred rendering the data", e, this);
               this.renderErrorDisplay();
            }
            
         }
         else
         {
            this.renderNoDataDisplay();
         }
      },

      /**
       * Removes the previously rendered view.
       *
       * @instance
       */
      clearOldView: function alfresco_documentlibrary_views_AlfDocumentListView__clearOldView() {
         if (this.docListRenderer != null)
         {
            this.docListRenderer.destroy();
         }
         if (this.messageNode != null)
         {
            domConstruct.destroy(this.messageNode);
         }
         // Remove all table body elements. (preserves headers)
         query("tbody", this.tableNode).forEach(domConstruct.destroy);
         this.clearData();
      },
      
      /**
       * Called from [renderView]{@link module:alfresco/documentlibrary/views/AlfDocumentListView#renderView} for 
       * every widget created for the last view. It is important that widgets are properly destroyed to ensure that
       * they do not respond to topics that they have subscribed to (e.g. selection events such as selecting all
       * documents). 
       * 
       * @instance
       * @param {object} widget The widget to destroy
       * @param {number} index The index of the widget
       */
      destroyWidget: function alfresco_documentlibrary_views_AlfDocumentListView__destroyWidget(widget, index) {
         if (typeof widget.destroyRecursive === "function")
         {
            widget.destroyRecursive();
         }
      },
      
      /**
       * Called after the view has been shown (note that [renderView]{@link module:alfresco/documentlibrary/views/AlfDocumentListView#renderView}
       * does not mean that the view has been displayed, just that it has been rendered. 
       * @instance
       */
      onViewShown: function alfresco_documentlibrary_views_AlfDocumentListView__onViewShown() {
         // No action by default.
      },
      
      /**
       * This method is called when there is no data to be shown. By default this just shows a standard localized
       * message to say that there is no data.
       * 
       * @instance
       */
      renderNoDataDisplay: function alfresco_documentlibrary_views_AlfDocumentListView__renderNoDataDisplay() {
         this.clearOldView();
         this.messageNode = domConstruct.create("div", {
            innerHTML: this.noItemsMessage
         }, this.domNode);
      },

      /**
       * This method is called when there is an error occurred rendering the view
       * 
       * @instance
       */
      renderErrorDisplay: function alfresco_documentlibrary_views_AlfDocumentListView__renderErrorDisplay() {
         this.clearOldView();
         this.messageNode = domConstruct.create("div", {
            innerHTML: this.message("doclistview.rendering.error.message")
         }, this.domNode);
      }
   });
});