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
 * @module alfresco/header/LicenseWarning
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/LicenseWarning.html",
        "alfresco/core/Core",
        "dojo/dom-style",
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/dom-construct"], 
        function(declare, _WidgetBase, _TemplatedMixin, template, AlfCore, domStyle, array, lang, domConstruct) {
   
   return declare([_WidgetBase, _TemplatedMixin, AlfCore], {
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/LicenseWarning.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/LicenseWarning.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/LicenseWarning.css"}]
       */
      cssRequirements: [{cssFile:"./css/LicenseWarning.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * @instance
       * @type {string}
       */
      usage: null,
      
      /**
       * @instance
       * @type {boolean} userIsAdmin Indicates whether or not the current user has admin priveleges
       */
      userIsAdmin: false,
      
      /**
       * @instance
       */
      postCreate: function alfresco_header_LicenseWarning__postCreate() {
         if (this.usage == null)
         {
            // If there are no usage instructions then no action is required...
         }
         else
         {
            // Always show a warning if Alfresco is in read only mode...
            if (this.usage.readOnly == true)
            {
               // Always show an error when the system is in readonly mode...
               this.addError(this.message("readonly.warning"));
               domStyle.set(this.domNode, "display", "block");
            }

            if ((this.usage.warnings.length != 0 || this.usage.errors.length != 0) && 
                (this.userIsAdmin == true || this.usage.level >= 2))
            {
               // If warnings or errors are present, display them to the Admin or user
               // Admin sees messages if WARN_ADMIN, WARN_ALL, LOCKED_DOWN
               // Users see messages if WARN_ALL, LOCKED_DOWN
               if (this.usage.warnings != null)
               {
                  array.forEach(this.usage.warnings, lang.hitch(this, "addWarning"));
               }
               if (this.usage.warnings != null)
               {
                  array.forEach(this.usage.errors, lang.hitch(this, "addError"));
               }
               domStyle.set(this.domNode, "display", "block");
            }
         }
      },
      
      /**
       * @instance
       */
      addMessage: function alfresco_header_LicenseWarning__addMessage(message, index, level) {
         var outer = domConstruct.create("div", {
            "class": "info"
         }, this.warningsNode);
         domConstruct.create("span", {
            "class": "level" + level
         }, outer);
         domConstruct.create("span", {
            innerHTML: message
         }, outer);
      },
      
      /**
       * @instance
       */
      addWarning: function alfresco_header_LicenseWarning__addWarning(warning, index) {
         this.addMessage(warning, index, 1);
      },
      
      /**
       * @instance
       */
      addError: function alfresco_header_LicenseWarning__addError(error, index) {
         this.addMessage(error, index, 3);
      }
   });
});