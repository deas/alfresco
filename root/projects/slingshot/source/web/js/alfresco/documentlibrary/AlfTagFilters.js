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
 * @module alfresco/documentlibrary/AlfTagFilters
 * @extends module:alfresco/documentlibrary/AlfDocumentFilters
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/documentlibrary/AlfDocumentFilters", 
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "alfresco/services/_TagServiceTopics",
        "alfresco/documentlibrary/AlfDocumentFilter",
        "alfresco/core/ObjectTypeUtils",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dojo/on",
        "dijit/registry"], 
        function(declare, AlfDocumentFilters, _AlfDocumentListTopicMixin, _TagServiceTopics, AlfDocumentFilter, ObjectTypeUtils, lang, array, domConstruct, domClass, on, registry) {

   return declare([AlfDocumentFilters, _AlfDocumentListTopicMixin], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/AlfDocumentFilters.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/AlfTagFilters.properties"}],
      
      /**
       * @instance
       * @type {string}
       * @default "docListFilterPref"
       */
      filterPrefsName: "docListTagFilterPref",
      
      /**
       * @instance
       */
      postMixInProperties: function alfresco_documentlibrary_AlfTagFilters__postMixInProperties() {
         this.inherited(arguments);
         
         // Subscribe to filter change events - in particular we're looking for tag filter changes because
         // they won't be represented by an individual filter object (it's down to the filter object to
         // publish display information when it's selected, but we're going to let tags off the hook here)
         this.alfSubscribe(this.filterChangeTopic, lang.hitch(this, "onFilterChange"));
         
         // Subscribe to publications about documents being tagged/untagged...
         this.alfSubscribe(this.documentTaggedTopic, lang.hitch(this, "onDocumentTagged"));
         
         // Make a request to get the initial set of tags for the current location...
         this.alfPublish(_TagServiceTopics.tagQueryTopic, {
            callback: this.onTagQueryResults,
            callbackScope: this
         });
      },
      
      /**
       * @instance
       * @param {object} response
       * @param {object} originalRequestConfig
       */
      onTagQueryResults: function alfresco_documentlibrary_AlfTagFilters__onTagQueryResults(response, originalRequestConfig) {
         // Create the tags...
         if (response && ObjectTypeUtils.isArray(response.tags))
         {
            var oldTags = registry.findWidgets(this.filtersNode);
            array.forEach(oldTags, lang.hitch(this, "clearTags"));
            array.forEach(response.tags, lang.hitch(this, "createTagFilter"));
         }
         else
         {
            this.alfLog("warn", "A request was made to generate filter tag links, but no 'tags' array attribute was provided", response, originalRequestConfig);
         }
      },
      
      /**
       * Destroys the supplied tag widget.
       * 
       * @instance
       * @param {object} widget The widget to destroy
       * @param {number} index The index of the widget
       */
      clearTags: function alfresco_documentlibrary_AlfTagFilters__clearTags(widget, index) {
         if (typeof widget.destroy === "function")
         {
            widget.destroy();
         }
      },
      /**
       * Creates a new [filter widget]{@link module:alfresco/documentlibrary/AlfDocumentFilter} and then calls the
       * [addFilter function]{@link module:alfresco/documentlibrary/AlfDocumentFilters#addFilter} to add it.
       *  
       * @instance
       * @param {{name: string, count: number}} tagData The data to create the tag with.
       */
      createTagFilter: function alfresco_documentlibrary_AlfTagFilters__createTagFilter(tagData) {
         if (tagData && 
             tagData.name != null &&
             tagData.count != null)
         {
            var tagFilter = new AlfDocumentFilter({
               label: this.message("filter.tag.label", {"0": tagData.name, "1": tagData.count}),
               filter: "tag",
               filterData: tagData.name
            });
            this.addFilter(tagFilter);
         }
         else
         {
            this.alfLog("warn", "It is not possible to create a filter tag without 'name' and 'count' attributes", tagData);
         }
      },
      
      /**
       * Provides special case handling for tag filtering.
       * 
       * @instance onClick
       * @param {object} payload 
       */
      onFilterChange: function alfresco_documentlibrary_AlfTagFilters__onFilterChange(payload) {
         if (payload != null && payload.filterId == "tag")
         {
            this.alfPublish(this.filterSelectionTopic, {
               value: payload.filterData,
               description: this.message("filter.tagged.label", {"0":payload.filterData})
            });
         }
      },
      
      /**
       * Used to keep track of the current set of filter tags.
       * 
       * @instance
       * @type {object[]}
       * @default
       */
      currentTagFilters: null,
      
      /**
       * Handles documents being tagged and creates a filter for the tag.
       * 
       * @instance
       * @param {object} payload
       */
      onDocumentTagged: function alfresco_documentlibrary_AlfTagFilters__onDocumentTagged(payload) {
      },
      
      /**
       * @instance
       * @param {string} tag The tag to check against
       * @param {object} tagFilter One of the existing filters to compare against
       * @param {number} index The index of the tag filter being compared.
       */
      compareTags: function alfresco_documentlibrary_AlfTagFilters__compareTags(tag, tagFilter, index) {
         return tagFilter.filterData == tag;
      }
   });
});