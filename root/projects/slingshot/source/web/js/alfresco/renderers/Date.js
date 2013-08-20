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
 * @module alfresco/renderers/Date
 * @extends module:alfresco/renderers/Property
 * @mixes module:alfresco/core/TemporalUtils
 * @mixes module:alfresco/core/UrlUtils
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/renderers/Property", 
        "alfresco/core/TemporalUtils",
        "alfresco/core/UrlUtils"], 
        function(declare, Property, TemporalUtils, UrlUtils) {

   return declare([Property, TemporalUtils, UrlUtils], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/Date.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/Date.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/Date.css"}]
       */
      cssRequirements: [{cssFile:"./css/Date.css"}],
      
      /**
       * Set up the attributes to be used when rendering the template.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_renderers_Date__postMixInProperties() {
         var jsNode = this.currentItem.jsNode,
             properties = jsNode.properties,
             html = "";

         var dateI18N = "details.modified-by", dateProperty = properties.modified.iso8601;
         if (this.currentItem.workingCopy && this.currentItem.workingCopy.isWorkingCopy)
         {
            dateI18N = "details.editing-started-by";
         }
         else if (dateProperty === properties.created.iso8601)
         {
            dateI18N = "details.created-by";
         }
         this.renderedValue = this.message(dateI18N, {
            0: this.getRelativeTime(dateProperty), 
            1: this.generateUserLink(properties.modifier)
         });
      }
   });
});