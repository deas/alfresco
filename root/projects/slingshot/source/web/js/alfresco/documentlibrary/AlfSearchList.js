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
 * Extends the default [AlfDocumentList]{@link module:alfresco/documentlibrary/AlfDocumentList} to 
 * make search specific requests.
 * 
 * @module alfresco/documentlibrary/AlfSearchList
 * @extends alfresco/documentlibrary/AlfDocumentList
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/documentlibrary/AlfDocumentList", 
        "alfresco/core/PathUtils",
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/dom-construct",
        "dojo/dom-class"], 
        function(declare, AlfDocumentList, PathUtils, array, lang, domConstruct, domClass) {
   
   return declare([AlfDocumentList], {
      

      /**
       * Subscribe the document list topics.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_documentlibrary_AlfDocumentList__postMixInProperties() {
         // this.alfPublish(this.getPreferenceTopic, {
         //    preference: "org.alfresco.share.documentList.documentsPerPage",
         //    callback: this.setPageSize,
         //    callbackScope: this
         // });

         // Only subscribe to filter changes if 'useHash' is set to true. This is because multiple DocLists might
         // be required on the same page and they can't all feed off the hash to drive the location.
         // if (this.useHash)
         // {
         //    this.alfSubscribe(this.filterChangeTopic, lang.hitch(this, "onChangeFilter"));
         // }
         
         // this.alfSubscribe(this.viewSelectionTopic, lang.hitch(this, "onViewSelected"));
         // this.alfSubscribe(this.documentSelectionTopic, lang.hitch(this, "onDocumentSelection"));
         // this.alfSubscribe(this.sortRequestTopic, lang.hitch(this, "onSortRequest"));
         // this.alfSubscribe(this.sortFieldSelectionTopic, lang.hitch(this, "onSortFieldSelection"));
         // this.alfSubscribe(this.showFoldersTopic, lang.hitch(this, "onShowFolders"));
         // this.alfSubscribe(this.pageSelectionTopic, lang.hitch(this, "onPageChange"));
         // this.alfSubscribe(this.docsPerpageSelectionTopic, lang.hitch(this, "onDocsPerPageChange"));
         // this.alfSubscribe(this.reloadDataTopic, lang.hitch(this, "loadData"));
         
         // Subscribe to the topics that will be published on by the DocumentService when retrieving documents
         // that this widget requests...
         this.alfSubscribe("ALF_RETRIEVE_DOCUMENTS_REQUEST_SUCCESS", lang.hitch(this, "onDataLoadSuccess"));
         this.alfSubscribe("ALF_RETRIEVE_DOCUMENTS_REQUEST_FAILURE", lang.hitch(this, "onDataLoadFailure"));
         this.alfSubscribe("ALF_SEARCH_REQUEST_SUCCESS", lang.hitch(this, "onSearchLoadSuccess"));
         this.alfSubscribe("ALF_SEARCH_REQUEST_FAILURE", lang.hitch(this, "onDataLoadFailure"));
         this.alfSubscribe("ALF_SEARCH_RESULT_CLICKED", lang.hitch(this, "onSearchResultClicked"));

         // Get the messages for the template...
         this.noViewSelectedMessage = this.message("doclist.no.view.message");
         this.noDataMessage = this.message("doclist.no.data.message");
         this.fetchingDataMessage = this.message("doclist.loading.data.message");
         this.renderingViewMessage = this.message("doclist.rendering.data.message");
      },
      
      /**
       * Handles successful calls to get data from the repository.
       * 
       * @instance
       * @param {object} response The response object
       * @param {object} originalRequestConfig The configuration that was passed to the the [serviceXhr]{@link module:alfresco/core/CoreXhr#serviceXhr} function
       */
      onSearchLoadSuccess: function alfresco_documentlibrary_AlfDocumentList__onSearchLoadSuccess(payload) {
         this.alfLog("log", "Search Results Loaded", payload, this);
         
         this._currentData = payload.response;
         
         // Re-render the current view with the new data...
         var view = this.viewMap[this._currentlySelectedView];
         if (view != null)
         {
            this.showRenderingMessage();
            view.setData(this._currentData);
            view.renderView();
            this.showView(view);
            
            // Force a resize of the sidebar container to take the new height of the view into account...
            this.alfPublish("ALF_RESIZE_SIDEBAR", {});
         }
      },

      /**
       * @instance
       */
      onSearchResultClicked: function alfresco_documentlibrary_AlfDocumentList__onSearchResultClicked(payload) {
         this.alfLog("log", "Search Result Clicked");

         var isContainer = lang.getObject("item.node.isContainer", false, payload);
         if (isContainer == true)
         {
            this.currentPath = "/";
            this.nodeRef = lang.getObject("item.nodeRef", false, payload);
            this.rootNode = lang.getObject("item.nodeRef", false, payload);
            this.loadData();
         }
         else
         {
            this.alfPublish("ALF_NAVIGATE_TO_PAGE", payload);
         }
      }
   });
});