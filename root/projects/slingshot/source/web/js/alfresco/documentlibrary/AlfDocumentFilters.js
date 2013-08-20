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
 * This should be used to wrap a set of [AlfDocumentFilters]{@link module:alfresco/documentlibrary/AlfDocumentFilter} 
 * in order to achieve the "twisty" and correct look and feel as expected in a document library.
 * 
 * @module alfresco/documentlibrary/AlfDocumentFilters
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/AlfDocumentFilters.html",
        "alfresco/core/Core",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "alfresco/documentlibrary/AlfDocumentFilter",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dojo/on"], 
        function(declare, _WidgetBase, _TemplatedMixin, template,  AlfCore, _AlfDocumentListTopicMixin, AlfDocumentFilter, lang, array, domConstruct, domClass, on) {

   return declare([_WidgetBase, _TemplatedMixin, AlfCore, _AlfDocumentListTopicMixin], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/AlfDocumentFilters.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/AlfDocumentFilters.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/AlfDocumentFilters.css"}]
       */
      cssRequirements: [{cssFile:"./css/AlfDocumentFilters.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String} template
       */
      templateString: template,
      
      /**
       * @instance
       * @type {string}
       * @default "docListFilterPref"
       */
      filterPrefsName: "docListFilterPref",
      
      /**
       * @instance
       */
      postMixInProperties: function() {
         if (this.label != null)
         {
            this.label = this.encodeHTML(this.message(this.label));
         }
      },
      
      /**
       * Processes any widgets defined in the configuration for this instance.
       * 
       * @instance
       */
      postCreate: function alfresco_documentlibrary_AlfDocumentFilters__postCreate() {
         if (this.label != null && this.label != "")
         {
            Alfresco.util.createTwister(this.labelNode, this.filterPrefsName);
         }
         
         if (this.widgets)
         {
            this.processWidgets(this.widgets);
         }
      },
      
      /**
       * Iterates over the processed widgets and calls the 'addFilter' function passing each one as an argument.
       * 
       * @instance
       * @param {object[]} widgets The widgets that were created.
       */
      allWidgetsProcessed: function alfresco_documentlibrary_AlfDocumentFilters__allWidgetsProcessed(widgets) {
         var _this = this;
         array.forEach(widgets, lang.hitch(this, "addFilter"));
      },
      
      /**
       * Adds supplied widget to the 'filtersNode' if it inherites from 'alfresco/documentlibrary/AlfDocumentFilter'
       * 
       * @instance
       * @param {object} widget The widget to be added
       * @param {integer} insertIndex The index to add the widget at
       */
      addFilter: function alfresco_documentlibrary_AlfDocumentFilters__addFilter(widget, insertIndex) {
         if (widget.isInstanceOf(AlfDocumentFilter))
         {
            widget.placeAt(this.filtersNode);
         }
         else
         {
            this.alfLog("warn", "Tried to add a widget that does not inherit from 'alfresco/documentlibrary/AlfDocumentFilter'", widget);
         }
      }
   });
});