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
        "dojo/io-query"], 
        function(declare, AlfCore, lang, ioQuery) {
   
   return declare([AlfCore], {

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
         return filterObj;
      }
   });
});