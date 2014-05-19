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
 * 
 * @module alfresco/core/ObjectProcessingMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dojo/_base/lang",
        "dojo/_base/array",
        "alfresco/core/ObjectTypeUtils"], 
        function(declare, lang, array, ObjectTypeUtils) {
   
   return declare(null, {
      
      /**
       * This utility function will perform token substitution on the supplied string value using the
       * values from currentItem.
       *
       * @instance
       * @param {string} v The value to process.
       * @returns The processed value
       */
      processCurrentItemTokens: function alfresco_core_ObjectProcessingMixin__processCurrentItemTokens(v) {
         var u = lang.replace(v, this.currentItem);
         return u;
      },

      /**
       * This utility function will replaced all colons in a string with underscores. This has been provided
       * for assisting with processing qnames into values that can be included in a URL. 
       *
       * @instance
       * @param {string} v The value to process.
       * @returns {string} The processed value.
       */
      replaceColons: function alfresco_core_ObjectProcessingMixin__replaceColons(v) {
         var u = v.replace(/:/g, "_");
         return u;
      },

      /**
       * This utility function can be used to work through the supplied object and process string
       * values with all the supplied functions. 
       * 
       * @intance
       * @param {array} functions An array of functions to apply to values
       * @param {object} o The object to process
       * @return {object} The processed object
       */
      processObject: function alfresco_core_ObjectProcessingMixin__processObject(functions, o) {
         for (var key in o)
         {
            var v = o[key];
            if (ObjectTypeUtils.isString(v))
            {
               array.forEach(functions, function(f) {
                  v = f.apply(this, [v])
               }, this);
               o[key] = v;
            }
            else if (ObjectTypeUtils.isArray(v))
            {
               array.forEach(v, lang.hitch(this.processObject(this, f)));
            }
            else if (ObjectTypeUtils.isObject(v))
            {
               o[key] = this.processObject(functions, v);
            }
         }
         return o;
      }
   });
});