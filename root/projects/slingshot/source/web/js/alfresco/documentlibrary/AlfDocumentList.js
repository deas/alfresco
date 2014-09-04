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
 * @module alfresco/documentlibrary/AlfDocumentList
 * @extends alfresco/lists/AlfSortablePaginatedList
 * @mixes module:alfresco/core/FullScreenMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/lists/AlfSortablePaginatedList", 
        "alfresco/core/JsNode",
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/hash",
        "dojo/io-query"], 
        function(declare, AlfSortablePaginatedList, JsNode, array, lang, hash, ioQuery) {
   
   return declare([AlfSortablePaginatedList], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/AlfDocumentList.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/AlfDocumentList.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {object[]}
       * @default [{cssFile:"./css/AlfDocumentList.css"}]
       */
      cssRequirements: [{cssFile:"./css/AlfDocumentList.css"}],

      /**
       * Indicates whether or not folders should be shown in the document library.
       *
       * @instance
       * @type {boolean}
       * @default true
       */
      showFolders: true,

      /**
       * @instance
       * @type {object}
       * @default null
       */
      currentFilter: null,
      
      /**
       * Extends the [inherited function]{@link module:alfresco/lists/AlfSortablePaginatedListt#postMixInProperties}
       * to set a default filter to be a root path.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_documentlibrary_AlfDocumentList__postMixInProperties() {
         this.inherited(arguments);
         this.currentFilter = {
            path: "/"
         };
      },

      /**
       * This function sets up the subscriptions that the Document List relies upon to manage its
       * internal state and request documents.
       *
       * @instance
       */
      setupSubscriptions: function alfrescdo_documentlibrary_AlfDocumentList__setupSubscriptions() {
         this.inherited(arguments);
         this.alfSubscribe("ALF_DOCUMENTLIST_PATH_CHANGED", lang.hitch(this, this.onPathChanged));
         this.alfSubscribe("ALF_DOCUMENTLIST_CATEGORY_CHANGED", lang.hitch(this, this.onCategoryChanged));
         this.alfSubscribe("ALF_DOCUMENTLIST_TAG_CHANGED", lang.hitch(this, this.onTagChanged));
         this.alfSubscribe(this.filterSelectionTopic, lang.hitch(this, this.onFilterChanged));
         this.alfSubscribe(this.documentSelectionTopic, lang.hitch(this, this.onDocumentSelection));
         this.alfSubscribe(this.showFoldersTopic, lang.hitch(this, this.onShowFolders));
      },

      /**
       * Handles requests to update the current path.
       *
       * @instance
       * @param {object} payload The details of the new path
       */
      onPathChanged: function alfresco_documentlibrary_AlfDocumentList__onPathChanged(payload) {
         if (payload.path)
         {
            if (this.useHash === true)
            {
               var currHash = ioQuery.queryToObject(hash());
               currHash.path = payload.path;
               delete currHash.filter;
               delete currHash.category;
               delete currHash.tag;
               this.alfPublish("ALF_NAVIGATE_TO_PAGE", {
                  url: ioQuery.objectToQuery(currHash),
                  type: "HASH"
               }, true);
            }
            else
            {
               this.currentFilter = {
                  path: payload.path
               };
               this.loadData();
            }
         }
         else
         {
            this.alfLog("warn", "A request was made to change the displayed path of a Document List, but no 'path' attribute was provided", payload, this);
         }
      },

      /**
       * Handles requests to update the current category.
       *
       * @instance
       * @param {object} payload The details of the new category path
       */
      onCategoryChanged: function alfresco_documentlibrary_AlfDocumentList__onCategoryChanged(payload) {
         if (payload.path)
         {
            if (this.useHash === true)
            {
               var currHash = ioQuery.queryToObject(hash());
               currHash.category = payload.path;
               delete currHash.filter;
               delete currHash.path;
               delete currHash.tag;
               this.alfPublish("ALF_NAVIGATE_TO_PAGE", {
                  url: ioQuery.objectToQuery(currHash),
                  type: "HASH"
               }, true);
            }
            else
            {
               this.currentFilter = {
                  category: payload.path
               };
               this.loadData();
            }
         }
         else
         {
            this.alfLog("warn", "A request was made to change the displayed path of a Document List, but no 'path' attribute was provided", payload, this);
         }
      },

      /**
       * 
       *
       * @instance
       * @param {object} payload The details of the changed filter
       */
      onFilterChanged: function alfresco_documentlibrary_AlfDocumentList__onFilterChanged(payload) {
         if (payload.value)
         {
            if (this.useHash === true)
            {
               var currHash = ioQuery.queryToObject(hash());
               currHash.filter = payload.value;
               delete currHash.category;
               delete currHash.path;
               delete currHash.tag;
               this.alfPublish("ALF_NAVIGATE_TO_PAGE", {
                  url: ioQuery.objectToQuery(currHash),
                  type: "HASH"
               }, true);
            }
            else
            {
               this.currentFilter = {
                  filter: payload.value
               };
            }
         }
         else
         {
            this.alfLog("warn", "A request was made to change the filter for a Document List, but no 'value' attribute was provided", payload, this);
         }
      },

      /**
       * 
       *
       * @instance
       * @param {object} payload The details of the changed tag
       */
      onTagChanged: function alfresco_documentlibrary_AlfDocumentList__onTagChanged(payload) {
         if (payload.value)
         {
            if (this.useHash === true)
            {
               var currHash = ioQuery.queryToObject(hash());
               currHash.tag = payload.value;
               delete currHash.category;
               delete currHash.path;
               delete currHash.filter;
               this.alfPublish("ALF_NAVIGATE_TO_PAGE", {
                  url: ioQuery.objectToQuery(currHash),
                  type: "HASH"
               }, true);
            }
            else
            {
               this.currentFilter = {
                  tag: payload.value
               };
            }
         }
         else
         {
            this.alfLog("warn", "A request was made to change the tag filter for a Document List, but no 'value' attribute was provided", payload, this);
         }
      },

      /**
       * This is the topic that will be subscribed to for responding to item clicks unless [useHash]{@link module:alfresco/documentlibrary/AlfDocumentList#useHash}
       * is set to true. [Views]{@link module:alfresco/documentlibrary/views/AlfDocumentListView} that defined
       * renderers that provide links using the [_ItemLinkMixin]{@link module:alfresco/renderers/_ItemLinkMixin} should
       * be configured to set a matching [linkClickTopic][_ItemLinkMixin]{@link module:alfresco/renderers/_ItemLinkMixin#linkClickTopic}
       * attribute in order to have their actions processed.
       *
       * @instance
       * @type {string}
       * @default "ALF_DOCLIST_NAV"
       */
      linkClickTopic: "ALF_DOCLIST_NAV",

      /**
       * This function is called whenever the [linkClickTopic]{@link module:alfresco/documentlibrary/AlfDocumentList#linkClickTopic}
       * is published. It processes the payload and updates the current filter and then refreshes the current
       * data by calling [loadData]{@link module:alfresco/documentlibrary/AlfDocumentList#loadData}.
       * 
       * @instance
       * @param {object} payload
       */
      onItemLinkClick: function alfresco_documentlibrary_AlfDocumentList__onItemLinkClick(payload) {
         var node = lang.getObject("item.node", false, payload) || payload.node;
         if (node.isContainer === true || node.isLink === true)
         {
            this.onFolderClick(payload);
         }
         else
         {
            this.onDocumentClick(payload);
         }
         
      },

      /**
       * 
       * @instance
       * @param {object} payload
       */
      onFolderClick: function alfresco_documentlibrary_AlfDocumentList__onFolderClick(payload) {
         if (payload.url != null)
         {
            this.currentFilter = this.processFilter(payload.url);
            if (this._readyToLoad) this.loadData();
         }
         else
         {
            this.alfLog("warn", "A 'url' attribute was expected to be provided for an item click", payload, this);
         }
      },

      /**
       * 
       * @instance
       * @param {object} payload
       */
      onDocumentClick: function alfresco_documentlibrary_AlfDocumentList__onDocumentClick(payload) {
         // No action for the moment
      },

      /**
       * 
       * @instance
       * @param {object} payload
       */
      onHashChanged: function alfresco_documentlibrary_AlfDocumentList__onHashChanged(payload) {
         if(this._payloadContainsUpdateableVar(payload))
         {
            this.currentFilter = payload;
            if (this._readyToLoad) this.loadData();
         }
      },
      
      /**
       * Extends the [inherited function]{@link module:alfresco/lists/AlfSortablePaginatedList#updateLoadDataPayload} to
       * add the additional document library related data.
       *
       * @instance
       * @param {object} payload The payload object to update
       */
      updateLoadDataPayload: function alfresco_lists_AlfSortablePaginatedList__updateLoadDataPayload(payload) {
         this.inherited(arguments);

         payload.type = this.showFolders ? "all" : "documents";
         payload.site = this.siteId;
         payload.container = this.containerId;
         payload.filter = this.currentFilter;
         payload.libraryRoot = this.rootNode;

         if ((this.siteId == null || this.siteId === "") && this.nodeRef != null)
         {
            // Repository mode (don't resolve Site-based folders)
            payload.nodeRef = this.nodeRef.toString();
         }
      },
      
      /**
       * This is an extension point function for extending modules to perform processing on the loaded
       * data once it's existence has been verified
       *
       * @instance
       * @param {object} response The original response.
       */
      processLoadedData: function alfresco_lists_AlfList__processLoadedData(response) {
         array.forEach(this.currentData, function(item, index) {
            item.jsNode = new JsNode(item.node);
         }, this);
         
         // Publish the details of the metadata returned from the data request...
         if (response.metadata)
         {
            this.alfPublish(this.metadataChangeTopic, {
               node: response.metadata
            });

            // Publish the details of the permissions for the current user. This will 
            // only be available when the a specific node is shown rather than a set
            // of results across multiple nodes (e.g. the result of a filter request)
            if (response.metadata.parent && 
                response.metadata.parent.permissions && 
                response.metadata.parent.permissions.user)
            {
               this.alfPublish(this.userAccessChangeTopic, {
                  userAccess: response.metadata.parent.permissions.user
               });
            }
         }
         this.inherited(arguments);
      }, 
      
      /**
       * @instance
       * @param {object} payload The published details of the selected items
       */
      onDocumentSelection: function alfresco_documentlibrary_AlfDocumentList__onDocumentSelection(payload) {
         this.alfLog("log", "Documents Selected: ", payload);
         // TODO!
      },
      
      /**
       * @instance
       * @param {object} payload The details of the request
       */
      onShowFolders: function alfresco_documentlibrary_AlfDocumentList__onShowFolders(payload) {
         this.alfLog("log", "Show Folders Request: ", payload);
         if (payload && payload.selected != null)
         {
            this.showFolders = payload.selected;
            if (this._readyToLoad) this.loadData();
         }
      }
   });
});
