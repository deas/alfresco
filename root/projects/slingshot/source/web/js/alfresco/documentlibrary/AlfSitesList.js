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
 * 
 * @module alfresco/documentlibrary/AlfSitesList
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
       * Overrides the default implementation to retrieve site data.
       *
       * @instance
       */
      loadData: function alfresco_documentlibrary_AlfSitesList__loadData() {
         this.showLoadingMessage(); // Commented out because of timing issues...

         // Set a response topic that is scoped to this widget...
         var documentPayload = {
            responseTopic: this.pubSubScope + "ALF_RETRIEVE_DOCUMENTS_REQUEST_SUCCESS"
         };

         this.alfPublish("ALF_GET_SITES", documentPayload, true);
      },

      /**
       * Handles successful calls to get site data.
       * 
       * @instance
       * @param {object} response The response object
       * @param {object} originalRequestConfig The configuration that was passed to the the [serviceXhr]{@link module:alfresco/core/CoreXhr#serviceXhr} function
       */
      onDataLoadSuccess: function alfresco_documentlibrary_AlfSitesList__onDataLoadSuccess(payload) {
         this.alfLog("log", "Data Loaded", payload, this);
         
         this._currentData = payload;

         // Publish the details of the loaded documents. The API isn't currently returning data
         // beyond the pagination limit...
         this.alfPublish(this.documentsLoadedTopic, {
            documents: payload.items,
            totalDocuments: payload.items.length,
            startIndex: 0
         });

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
   });
});