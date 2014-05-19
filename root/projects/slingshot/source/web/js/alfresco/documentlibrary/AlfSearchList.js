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
        "dojo/dom-class",
        "dojo/hash",
        "dojo/io-query"], 
        function(declare, AlfDocumentList, PathUtils, array, lang, domConstruct, domClass, hash, ioQuery) {
   
   return declare([AlfDocumentList], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/AlfDocumentList.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/AlfSearchList.properties"}],

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/AlfSearchList.css"}]
       */
      cssRequirements: [{cssFile:"./css/AlfSearchList.css"}],
      
      /**
       * Subscribe the document list topics.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_documentlibrary_AlfSearchList__postMixInProperties() {
         
         this.alfSubscribe("ALF_DOCLIST_SORT", lang.hitch(this, "onSortRequest"));
         this.alfSubscribe("ALF_DOCLIST_SORT_FIELD_SELECTION", lang.hitch(this, "onSortFieldSelection"));
         
         // Subscribe to the topics that will be published on by the DocumentService when retrieving documents
         // that this widget requests...
         this.alfSubscribe("ALF_RETRIEVE_DOCUMENTS_REQUEST_SUCCESS", lang.hitch(this, "onSearchLoadSuccess"));
         this.alfSubscribe("ALF_RETRIEVE_DOCUMENTS_REQUEST_FAILURE", lang.hitch(this, "onDataLoadFailure"));

         // Subscribe to the topics that address specific search updates...
         this.alfSubscribe("ALF_SET_SEARCH_TERM", lang.hitch(this, "onSearchTermRequest"));
         this.alfSubscribe("ALF_INCLUDE_FACET", lang.hitch(this, "onIncludeFacetRequest"));
         this.alfSubscribe("ALF_APPLY_FACET_FILTER", lang.hitch(this, "onApplyFacetFilter"));
         this.alfSubscribe("ALF_REMOVE_FACET_FILTER", lang.hitch(this, "onRemoveFacetFilter"));

         // Infinite scroll handling
         this.alfSubscribe(this.scrollNearBottom, lang.hitch(this, "onScrollNearBottom"));

         // Get the messages for the template...
         this.noViewSelectedMessage = this.message("searchlist.no.view.message");
         this.noDataMessage = this.message("searchlist.no.data.message");
         this.fetchingDataMessage = this.message("searchlist.loading.data.message");
         this.renderingViewMessage = this.message("searchlist.rendering.data.message");
         this.fetchingMoreDataMessage = this.message("searchlist.loading.data.message");

         this.facetFilters = {};
      },

      /**
       * The current term to search on
       *
       * @instance
       * @type {string}
       * @default ""
       */ 
      searchTerm: "",

      /**
       * Updates the current search term. Note that this is not currently sufficient for setting complete
       * search data (such as facets, filters, sort order, etc) so this will need to be iterated on as 
       * needed.
       *
       * @instance
       * @param {object} payload The details of the search term to set
       */
      onSearchTermRequest: function alfresco_documentlibrary_AlfSearchList__onSearchTermRequest(payload) {
         this.alfLog("log", "Setting search term", payload, this);
         var searchTerm = lang.getObject("searchTerm", false, payload);
         if (searchTerm == null)
         {
            this.alfLog("warn", "No searchTerm provided on request", payload, this);
         }
         else
         {
            this.searchTerm = searchTerm;
            var currHash = ioQuery.queryToObject(hash());
            if (this.searchTerm != null)
            {
               currHash.searchTerm = this.searchTerm;
            }
            this.alfPublish("ALF_NAVIGATE_TO_PAGE", {
               url: ioQuery.objectToQuery(currHash),
               type: "HASH"
            }, true);
         }
      },

      /**
       * The facet fields to include in searches. This is updated by the onIncludeFacetRequest function.
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      facetFields: "",

      /**
       * 
       * @instance
       * @param {object} payload The details of the facet to include
       */
      onIncludeFacetRequest: function alfresco_documentlibrary_AlfSearchList__onIncludeFacetRequest(payload) {
         this.alfLog("log", "Adding facet filter", payload, this);
         var qname = lang.getObject("qname", false, payload);
         if (qname == null)
         {
            this.alfLog("warn", "No qname provided when adding facet field", payload, this);
         }
         else
         {
            this.facetFields = (this.facetFields != "") ? this.facetFields + "," + qname : qname;
         }
      },

      /**
       * The filters of facets that should be applied to search queries. This can either be configured
       * when the widget is created or can be set via the browser hash fragment.
       *
       * @instance
       * @type {object}
       * @default null
       */
      facetFilters: null,

      /**
       * This function is called as a result of publishing on the "ALF_APPLY_FACET_FILTER" topic. It will
       * update the current [filters]{@link module:alfresco/documentlibrary/AlfSearchList#facetFilters}
       * object with a new entry for the request filter.
       *
       * @instance
       * @param {object} payload The details of the facet filter to apply
       */
      onApplyFacetFilter: function alfresco_documentlibrary_AlfSearchList__onApplyFacetFilter(payload) {
         this.alfLog("log", "Filtering on facet", payload, this);
         var filter = lang.getObject("filter", false, payload);
         if (filter == null)
         {
            this.alfLog("warn", "No filter provided when filtering by facet", payload, this);
         }
         else
         {
            this.facetFilters[filter] = true;

            this.resetResultsList();
            this.loadData();
         }
      },

      /**
       * This function is called as a result of publishing on the "ALF_REMOVE_FACET_FILTER" topic. It will
       * update the current [filters]{@link module:alfresco/documentlibrary/AlfSearchList#facetFilters}
       * object to delete the supplied filter
       *
       * @instance
       * @param {object} payload The details of the facet filter to apply
       */
      onRemoveFacetFilter: function alfresco_documentlibrary_AlfSearchList__onRemoveFacetFilter(payload) {
         this.alfLog("log", "Removing facet filter", payload, this);
         delete this.facetFilters[payload.filter];
         this.resetResultsList();
         this.loadData();
      },

      /**
       * If [useHash]{@link module:alfresco/documentlibrary/AlfDocumentList#useHash} has been set to true
       * then this function will be called whenever the browser hash fragment is modified. It will update
       * the attributes of this instance with the values provided in the fragment.
       * 
       * @instance
       * @param {object} payload
       */
      onChangeFilter: function alfresco_documentlibrary_AlfSearchList__onChangeFilter(payload) {
         this.alfLog("log", "Filter change detected", payload, this);

         // Only update if the payload contains one of the variables we care about
         if(this._payloadContainsUpdateableVar(payload))
         {
            // If the search term has changed then we want to delete the facet filters as
            // they might not be applicable to the new search results...
            var newSearchTerm = lang.getObject("searchTerm", false, payload);
            if (newSearchTerm != this.searchTerm)
            {
               this.facetFilters = {};
            }
   
            // The facet filters need to be handled directly because they are NOT just passed as 
            // a simple string. Create a new object for the filters and then break up the filters
            // based on comma delimition and assign each element as a new key in the filters object
            var filters = lang.getObject("facetFilters", false, payload);
            if (filters != null)
            {
               var ff = payload["facetFilters"] = {};
               var fArr = filters.split(",");
               array.forEach(fArr, function(filter) {
                  ff[filter] = true;
               }, this);
            }
            else
            {
               this.facetFilters = {};
            }

            lang.mixin(this, payload);
            this.resetResultsList();
            this.loadData();
         }
      },

      /**
       * Processes all the current search arguments into a payload that is published to the [Search Service]{@link module:alfresco/services/SearchService}
       * to perform the actual search request
       *
       * @instance
       */
      loadData: function alfresco_documentlibrary_AlfSearchList__loadData() {
         this.showLoadingMessage();

         var filters = "";
         for (var key in this.facetFilters)
         {
            filters = filters + key.replace(/\.__.u/g, "").replace(/\.__/g, "") + ",";
         }
         filters = filters.substring(0, filters.length - 1);

         var searchPayload = {
            term: this.searchTerm,
            facetFields: this.facetFields,
            filters: filters,
            sortAscending: this.sortAscending,
            sortField: this.sortField,
            site: this.siteId,
            rootNode: this.rootNode,
            repo: this.repo
         };

         // InfiniteScroll uses pagination under the covers.
         if (this.useInfiniteScroll)
         {
            // Search API wants startIndex rather than page, so we need to convert here.
            searchPayload.startIndex = (this.currentPage - 1) * this.currentPageSize;
            searchPayload.pageSize = this.currentPageSize;
         }

         // Set a response topic that is scoped to this widget...
         searchPayload.alfResponseTopic = this.pubSubScope + "ALF_RETRIEVE_DOCUMENTS_REQUEST";

         this.alfPublish("ALF_SEARCH_REQUEST", searchPayload, true);
      },

      /**
       * Handles successful calls to get data from the repository.
       * 
       * @instance
       * @param {object} response The response object
       * @param {object} originalRequestConfig The configuration that was passed to the the [serviceXhr]{@link module:alfresco/core/CoreXhr#serviceXhr} function
       */
      onSearchLoadSuccess: function alfresco_documentlibrary_AlfSearchList__onSearchLoadSuccess(payload) {
         this.alfLog("log", "Search Results Loaded", payload, this);
         
         var newData = payload.response;
         this._currentData = newData; // Some code below expects this even if the view is null.

         // Re-render the current view with the new data...
         var view = this.viewMap[this._currentlySelectedView];
         if (view != null)
         {
            this.showRenderingMessage();

            if (this.useInfiniteScroll)
            {
               view.augmentData(newData);
               this._currentData = view.getData();
            }
            else
            {
               view.setData(newData);
            }

            view.renderView(this.useInfiniteScroll);
            this.showView(view);

         }

         // TODO: This should probably be in the SearchService... but will leave here for now...
         var facets = lang.getObject("response.facets", false, payload);
         var filters = lang.getObject("requestConfig.query.filters", false, payload);
         if (facets != null)
         {
            for (var key in facets)
            {
               this.alfPublish("ALF_FACET_RESULTS_" + key, {
                  facetResults: facets[key],
                  activeFilters: filters
               });
            }
         }

         var resultsCount = this._currentData.totalRecordsUpper;
         if (resultsCount != null)
         {
            // Publish the number of search results found...
            this.alfPublish("ALF_SEARCH_RESULTS_COUNT", {
               count: resultsCount,
               label: this.message("search.results.count.label", {0: resultsCount})
            });
         }

         // Force a resize of the sidebar container to take the new height of the view into account...
         this.alfPublish("ALF_RESIZE_SIDEBAR", {});
      },

      /**
       * Clear Old results from list & reset counts.
       *
       * @instance
       */
      resetResultsList: function alfresco_documentlibrary_AlfSearchList__resetResultsList() {
         this.startIndex = 0;
         this.currentPage = 1;
         this.hideChildren(this.domNode);
         this.alfPublish(this.clearDocDataTopic);
      },

      /**
       * Compares the payload object with the hashVarsForUpdate array of key names
       * Returns true if hashVarsForUpdate is empty
       * Returns true if the payload contains a key that is specified in hashVarsForUpdate
       * Returns false otherwise
       *
       * @instance
       * @param {object} payload The payload object
       * @return {boolean}
       */
      _payloadContainsUpdateableVar: function alfresco_documentlibrary_AlfSearchList___payloadContainsUpdateableVar(payload) {
         
         // No hashVarsForUpdate - return true
         if(this.hashVarsForUpdate == null || this.hashVarsForUpdate.length == 0)
         {
            return true;
         }
         
         // Iterate over the keys defined in hashVarsForUpdate - return true if the payload contains one of them
         for(var i=0; i < this.hashVarsForUpdate.length; i++)
         {
            if(this.hashVarsForUpdate[i] in payload)
            {
               return true;
            }
         }
         
         return false;
      }

   });
});