/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * Extends the Property renderer ({@link module:alfresco/renderers/Property}) to provide an interpreted boolean display with i18n functionality.
 * 
 * @module alfresco/renderers/Boolean
 * @extends alfresco/renderers/Property
 * @author Richard Smith
 */
define(["dojo/_base/declare",
        "alfresco/renderers/Property",
        "alfresco/core/ObjectTypeUtils",
        "dojo/_base/lang"], 
        function(declare, Property, ObjectTypeUtils, lang) {

   return declare([Property], {

      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/Boolean.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/Boolean.properties"}],

      /**
       * Set up the attributes to be used when rendering the template.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_renderers_Boolean__postMixInProperties() {
         if (ObjectTypeUtils.isString(this.propertyToRender) && 
             ObjectTypeUtils.isObject(this.currentItem) && 
             lang.exists(this.propertyToRender, this.currentItem))
         {
            this.renderPropertyNotFound = false;
            this.renderedValue = this.getRenderedProperty(lang.getObject(this.propertyToRender, false, this.currentItem));
            if (this.renderedValue===true || 
                this.renderedValue==='true' || 
                this.renderedValue===1 || 
                this.renderedValue==='1')
            {
               this.renderedValue = this.message("boolean.yes");
            }
            else if(this.renderedValue===false || 
                this.renderedValue==='false' || 
                this.renderedValue===0 || 
                this.renderedValue==='0')
            {
               this.renderedValue = this.message("boolean.no");
            }
            else
            {
               this.renderedValue = this.message("boolean.unknown");
            }
         }
         else
         {
            this.alfLog("log", "Property does not exist:", this);
            this.renderedValue = '';
         }
      }

   });
});