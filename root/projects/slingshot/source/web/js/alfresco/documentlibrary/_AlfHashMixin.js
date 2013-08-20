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
 * Provides HTML5 style hash based history and location marking. This adopts the pattern originally used by
 * the Document Library in Alfresco Share but can be mixed into any widget that requires hashing. Ideally this
 * should only be mixed into a single widget on a page (e.g. the [DocumentList]{@link module:alfresco/documentlibrary/AlfDocumentList})
 * or multiple publications will occur on a hash change (although this in itself shouldn't be a major problem if
 * subscribers check for changes to their current status). 
 * 
 * @module alfresco/documentlibrary/_AlfHashMixin
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "dojo/hash",
        "dojo/_base/lang",
        "dojo/io-query"], 
        function(declare, AlfCore, _AlfDocumentListTopicMixin, hash, lang, ioQuery) {
   
   return declare([AlfCore, _AlfDocumentListTopicMixin], {

      /**
       * Extends the constructor chain to subscribe to the "/dojo/hashchange" topic which is hitched
       * to [onHashChange]{@link module:alfresco/documentlibrary/_AlfHashMixin#onHashChange}.
       * @instance
       */
      constructor: function() {
         this.alfSubscribe("/dojo/hashchange", lang.hitch(this, "onHashChange"));
      },
      
      /**
       * Checks the initial state of the hash location. This is to ensure that bookmarks and copied
       * links work on page loading.
       * 
       * @instance
       */
      initialiseFilter: function() {
         // Store the current page...
         this.onHashChange(hash());
      },
      
      /**
       * Responds to changes in the page hash. This is typically used to set filters for the 
       * document library.
       * 
       * @instance
       * @param {object} payload The publication topic. This object needs to contain the attribute 'filter' for
       * anything to happen.
       */
      onHashChange: function alfresco_documentlibrary__AlfHashMixin__onHashChange(payload) {
         var filterObj = ioQuery.queryToObject(payload);
         this.alfLog("log", "New filter", filterObj);
         
         if (filterObj != null && filterObj.filter != null)
         {
            // The filter attribute will be divided up into up to 3 parts by the "bar" character (|)
            // Part 1 = filterId (e.g. "path")
            // Part 2 = filterData (e.g. "/some/folder/location")
            // Part 3 = filterDisplay (?? don't actually know where this is used - but it can be provided!)
            var splitFilter = filterObj.filter.split("|");
            if (typeof splitFilter[0] !== "undefined")
            {
               filterObj.filterId = splitFilter[0];
            }
            if (typeof splitFilter[1] !== "undefined")
            {
               filterObj.filterData = splitFilter[1];
            }
            if (typeof splitFilter[2] !== "undefined")
            {
               filterObj.filterDisplay = splitFilter[2];
            }
         }
         else
         {
            filterObj = {
               filterId: "path",
               filterData: "/",
               filterDisplay: ""
            };
         }
         
         // Publish the updated filter...
         this.alfLog("log", "Publishing decoded filter", filterObj);
         this.alfPublish(this.filterChangeTopic, filterObj);
      }
   });
});