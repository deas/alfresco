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
 * This should be used to wrap a set of [AlfDocumentFilters]{@link module:alfresco/documentlibrary/AlfDocumentFilter} 
 * in order to achieve the "twisty" and correct look and feel as expected in a document library.
 * 
 * @module alfresco/search/FacetFilters
 * @extends alfresco/documentlibrary/AlfDocumentFilters
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/documentlibrary/AlfDocumentFilters",
        "alfresco/search/FacetFilter",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dojo/on",
        "dijit/registry"], 
        function(declare, AlfDocumentFilters, FacetFilter, lang, array, domConstruct, domClass, on, registry) {

   return declare([AlfDocumentFilters], {
      
      /**
       * @instance
       * @type {string}
       * @default "docListFilterPref"
       */
      filterPrefsName: "docListFilterPref",
      
      /**
       * This should be configured to be a QName for use as a facet
       *
       * @instance
       * @type {string}
       * @default null
       */
      facetQName: null,

      /**
       * The number of filters that should be shown for each facet. If this is null then there is no maximum
       *
       * @instance
       * @type {number}
       * @default null
       */
      maxFilters: null,

      /**
       * The number hits that a filter should have in order for it to be shown.
       *
       * @instance
       * @type {number}
       * @default
       */
      hitThreshold: null,

      /**
       * The length (in number of characters) that a filter should have in order for it to be shown. This is particularly
       * relevant for facets similar to description where lots of short meaningless filters might be returned.
       *
       * @instance
       * @type {number}
       * @default null
       */
      minFilterValueLength: null,

      /**
       * How the filters are ordered. The options are:
       * <ul><li>ALPHABETICALLY (sort alphabetically by label)</li>
       * <li>ASCENDING (ascending number of hits)</li>
       * <li>DESCENDING (descending number of hits)</li></ul>
       *
       * @instance
       * @type {string}
       * @default "ALPHABETICALLY"
       */
      sortBy: "ALPHABETICALLY",

      /**
       * Pass through setting for useHash on initialised FacetFilter widgets
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      useHash: false,

      /**
       * Processes any widgets defined in the configuration for this instance.
       * 
       * @instance
       */
      postCreate: function alfresco_search_FacetFilters__postCreate() {
         if (this.label != null && this.label != "")
         {
            Alfresco.util.createTwister(this.labelNode, this.filterPrefsName);
         }

         // TODO: Commented out and published because it's being developed against QuaddsWidgets...
         this.alfSubscribe("ALF_WIDGETS_READY", lang.hitch(this, "publishFacets"), true);
         // this.publishFacets();
      },
      
      /**
       * 
       * @instance
       */
      publishFacets: function alfresco_search_FacetFilters__publishFacets() {
         if (this.facetQName != null && this.facetQName != "")
         {
            // Publish the details of the facet for the SearchService to log for inclusion in search requests
            this.alfPublish("ALF_INCLUDE_FACET", {
               qname: this.facetQName
            }, true);

            // Subscribe to facet property results to add facet properties...
            this.alfSubscribe("ALF_FACET_RESULTS_@" + this.facetQName, lang.hitch(this, "processFacetFilters"));
         }
         else
         {
            this.alfLog("warn", "No facet QNname was provided for a filter", this);
         }
      },

      /**
       * This function is expectd to handle generation of filters relevant to the current search
       * results. It clears out the previous filters and adds the new ones
       *
       * @instance
       * @param {object} payload The details of the filters to generate
       */
      processFacetFilters: function alfresco_search_FacetFilters__processFacetFilters(payload) {
         this.alfLog("log", "Facet Results received!", payload, this);

         // Reset the more/less options...
         this.moreFiltersList = null;
         domClass.add(this.showMoreNode, "hidden");
         domClass.add(this.showLessNode, "hidden");

         // Remove all previous filters...
         var widgets = registry.findWidgets(this.filtersNode);
         array.forEach(widgets, function(widget, i) {
            widget.destroy();
         });

         // Put all the active filters into a list...
         var activeFilters = [];
         if (payload.activeFilters != null)
         {
            activeFilters = payload.activeFilters.split(",");
         }

         // Create a new array and populate with the the facet filters...
         var filters = [];
         for (var key in payload.facetResults)
         {
            var currFilter = payload.facetResults[key];
            filters.push({
               label: currFilter.label,
               value: currFilter.value,
               hits: currFilter.hits
            });
            
         }

         // Sort the filters...
         filters = this.sortFacetFilters(filters);

         // Set a count of the filters to add...
         var filtersToAdd = this.maxFilters;

         // Create widgets for each filter...
         array.forEach(filters, function(filter, index) {

            if (this.minFilterValueLength != null && filter.value.length < this.minFilterValueLength)
            {
               // Don't add filters that have a value shorter than the minimum specified length
            }
            else if (this.hitThreshold == null || this.hitThreshold <= filter.hits)
            {
               // Check to see if this is an active filter...
               var applied = array.some(activeFilters, function(activeFilter, index) {
                  return activeFilter == this.facetQName.replace(/\.__/g, "") + "|" + filter.value;
               }, this);

               // Keeping adding (visible) filters until we've hit the maximum number...
               var filterWidget = new FacetFilter({
                  label: filter.label,
                  hits: filter.hits,
                  filter: filter.value,
                  facet: this.facetQName,
                  applied: applied,
                  useHash: this.useHash,
                  pubSubScope: this.pubSubScope
               });

               if (filtersToAdd != null && filtersToAdd <= 0 && !applied)
               {
                  this.addMoreFilter(filterWidget);
               }

               filterWidget.placeAt(this.showMoreNode, "before");

               // Decrement the filter count...
               if (filtersToAdd != null) filtersToAdd--;
            }
            else
            {

            }

         }, this);

         if (this.moreFiltersList != null)
         {
            domClass.remove(this.showMoreNode, "hidden");
         }

         if (filters.length == 0)
         {
            domClass.add(this.domNode, "hidden");
         }
         else
         {
            domClass.remove(this.domNode, "hidden");
         }
      },

      /**
       * Sorts the filter values based on the sortBy attribute.
       *
       * @instance
       * @param {array} filters The processed filters to sort
       * @returns {array} The sorted filters
       */
      sortFacetFilters: function alfresco_search_FacetFilters__sortFacetFilters(filters) {
         if (this.sortBy == null || this.sortBy.trim() == "ALPHABETICALLY")
         {
            return filters.sort(this._alphaSort);
         }
         else if (this.sortBy.trim() == "ASCENDING")
         {
            return filters.sort(this._ascSort);
         }
         else if (this.sortBy.trim() == "DESCENDING")
         {
            return filters.sort(this._descSort);
         }
         else
         {
            return filters;
         }
      },

      /**
       * A function for sorting the facet filter values alphabetically (from a-b)
       *
       * @instance
       * @param {object} a The first filter value object
       * @param {object} b The second filter value object
       * @returns {number} -1, 0 or 1 according to standard array sorting conventions
       */
      _alphaSort: function alfresco_search_FacetFilters___alphaSort(a,b) {
         var alc = a.label.toLowerCase();
         var blc = b.label.toLowerCase();
         if(alc < blc) return -1;
         if(alc > blc) return 1;
         return 0;
      },

      /**
       * A function for sorting the facet filter values hits from low to high
       *
       * @instance
       * @param {object} a The first filter value object
       * @param {object} b The second filter value object
       * @returns {number} -1, 0 or 1 according to standard array sorting conventions
       */
      _ascSort: function alfresco_search_FacetFilters___ascSort(a, b) {
         return a.hits - b.hits;
      },

      /**
       * A function for sorting the facet filter values hits from high to low
       *
       * @instance
       * @param {object} a The first filter value object
       * @param {object} b The second filter value object
       * @returns {number} -1, 0 or 1 according to standard array sorting conventions
       */
      _descSort: function alfresco_search_FacetFilter___descSort(a, b) {
         return b.hits - a.hits;
      }
   });
});