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
 * A mixin that provides the ability to test object types simply. Currently this just aliases the
 * Dojo lang calls which have been deprecated. This allows us to handle the deprecation of those
 * utility functions just from this module. 
 * 
 * @module alfresco/core/ObjectTypeUtils
 * @author Dave Draper
 */
define(["dojo/_base/lang"], 
        function(lang) {
   
   return {
      
      /**
       * Checks to see if the supplied argument is a string.
       * 
       * @instance
       * @param {unknown} The value to test
       * @returns {boolean} Returns true if the supplied value is a string
       */
      isString: function alfresco_core_ObjectTypeUtils__isString(value) {
         return lang.isString(value);
      },
      
      /**
       * Checks to see if the supplied argument is an object.
       * 
       * @instance
       * @param {unknown} The value to test
       * @returns {boolean} Returns true if the supplied value is an object
       */
      isObject: function alfresco_core_ObjectTypeUtils__isObject(value) {
         return lang.isObject(value);
      },
      
      /**
       * Checks to see if the supplied argument is an array.
       * 
       * @instance
       * @param {unknown} The value to test
       * @returns {boolean} Returns true if the supplied value is an array
       */
      isArray: function alfresco_core_ObjectTypeUtils__isArray(value) {
         return lang.isArray(value);
      }
   };
});