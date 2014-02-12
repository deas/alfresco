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
 * This module is currently a BETA
 *
 * @module alfresco/preview/AlfDocumentPreviewPlugin
 * @extends dijit/_WidgetBase
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "alfresco/core/Core",
        "dojo/_base/lang",
        "dojo/on",
        "dojo/sniff"], 
        function(declare, _Widget, Core, lang, on, sniff) {
   
   return declare([_Widget, Core], {

      /**
       *
       * @instance
       * @param {object[]} args
       */
      constructor: function alfresco_preview_AlfDocumentPreviewPlugin__constructor(args) {
         lang.mixin(args);

         // Ensure that an initial attributes instance variable is defined to avoid
         // errors when the [setAttributes]{@link module:alfresco/preview/AlfDocumentPreviewPlugin#setAttributes}
         // function is called.
         if (this.attributes == null)
         {
            this.attributes = {};
         }
      },

      /**
       * Updates the attributes with the values from the [AlfDocumentPreview]{@link module:alfresco/preview/AlfDocumentPreview}
       * instance for the current Node being previewed.
       *
       * @instance
       * @param {object[]} attributes The attributes object to mixin into the default settings.
       */
      setAttributes: function alfresco_preview_AlfDocumentPreviewPlugin__setAttributes(attributes) {
         var clonedAttributes = lang.clone(attributes);
         lang.mixin(this.attributes, clonedAttributes);
      },

      /**
       * Tests if the plugin can be used in the users browser.
       *
       * @instance
       * @return {String} Returns nothing if the plugin may be used, otherwise returns a message containing the reason
       * it cant be used as a string.
       */
      report: function alfresco_preview_AlfDocumentPreviewPlugin__report() {
         // By default don't report anything.
      },

      /**
       * By default does nothing.
       *
       * @instance
       */
      display: function alfresco_preview_AlfDocumentPreviewPlugin__display() {
         // TODO: Output some HTML indicating that this function hasn't been overridden properly?
      }
   });
});