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
 * This is a mixin that provides time associated utility functions. These functions are currently
 * aliases to core Alfresco Share functions provides by "alfresco.js" but the from them will be
 * merged directly into this class over time.
 * 
 * @module alfresco/core/TemporalUtils
 * @author Dave Draper
 */
define(["dojo/_base/declare"], 
        function(declare) {
   
   return declare(null, {

      /**
       * Declare the dependencies on "legacy" JS files that this is wrapping.
       * 
       * @instance
       * @type {String[]}
       */
      nonAmdDependencies: ["/js/alfresco.js"],
      
      
      /**
       * Render relative dates on the client
       *
       * Converts all ISO8601 dates within the specified container to relative dates.
       * (indicated by <span class="relativeTime">{date.iso8601}</span>)
       *
       * @instance
       * @param {String} id ID of HTML element containing
       * @returns {String} A description of the time relative to the value provided.
       *
       */
      getRelativeTime: function alfresco_core_TemporalUtils__getRelativeTime(id) {
         return Alfresco.util.relativeTime(id);
      }
   })
});