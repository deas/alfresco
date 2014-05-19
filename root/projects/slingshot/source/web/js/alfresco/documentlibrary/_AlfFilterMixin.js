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
 * <p>This module provides a function for converting filter query strings into JavaScript
 * objects comprised of the following attributes:<ul>
 * <li>filterId</li>
 * <li>filterData</li>
 * <li>filterDisplay</li></ul>
 * It was written to service both the [_AlfHashMixin]{@link module:alfresco/documentlibrary/_AlfHashMixin}
 * and [AlfDocumentList]{@link module:alfresco/documentlibrary/AlfDocumentList} modules.</p>
 * 
 * @module alfresco/documentlibrary/_AlfFilterMixin
 * @extends module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/io-query"], 
        function(declare, AlfCore, lang, array, ioQuery) {
   
   return declare([AlfCore], {

      /**
       * This is the string that is used to split the filter on. By default this is "|", but this can
       * be overridden by any modules that mix this module into them.
       * 
       * @instance
       * @type {string}
       * @default "|"
       */
      filterDelimiter: "|",

      /**
       * Converts a filter string (of the form filter=<id>|<data>|<display>)) into an
       * object.
       * 
       * @instance
       * @param {object} data The data to convert to a filter
       * anything to happen.
       */
      processFilter: function alfresco_documentlibrary__AlfFilterMixin__processFilter(data) {
         var filterObj = ioQuery.queryToObject(data);
         if (filterObj != null)
         {
            if (filterObj.filter != null)
            {
               // The filter attribute will be divided up into up to 3 parts by the "bar" character (|)
               var splitFilter = filterObj.split(this.filterDelimiter);

               // If no filter keys are defined, then just provide an empty array to avoid reference errors...
               if (this.filterKeys == null)
               {
                  this.filterKeys = [];
               }
               array.forEach(splitFilter, lang.hitch(this, "processFilterElement", processedFilter, this.filterKeys));
            }
            else
            {
               // Just return the filter object as is...
            }
            
         }
         else
         { 
            // The default filter is root location in a document lib...
            filterObj = {
               filterId: "path",
               filterData: "/",
               filterDisplay: ""
            };
         }
         return filterObj;
      },

      /**
       * Processes an individual data element extracted from the hash fragment. By default the data is simply
       * added into the supplied processedFilter object using a key mapped from the supplied processedFilterKeys
       * argument. If no key is provided for the supplied index then the index itself is used as the key.
       * 
       * @instance
       * @param {object} processedFilter The final filter object that needs to be updated
       * @param {array} processedFilterKeys An array of keys to use for the filter object
       * @param {string} elementData The element to be processed
       */
      processFilterElement: function alfresco_documentlibrary__AlfFilterMixin__processFilterElement(processedFilter, processedFilterKeys, elementData, index) {
         if (elementData !== null)
         {
            var key = processedFilterKeys[index];
            if (key == undefined)
            {
               key = index;
            }
            processedFilter[key] = elementData;
         }
      }
   });
});