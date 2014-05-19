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
 * @module alfresco/search/FacetFilter
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dijit/_OnDijitClickMixin",
        "dojo/text!./templates/FacetFilter.html",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dojo/on"], 
        function(declare, _WidgetBase, _TemplatedMixin, _OnDijitClickMixin, template,  AlfCore, lang, array, domConstruct, domClass, on) {

   return declare([_WidgetBase, _TemplatedMixin, AlfCore], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/FacetFilter.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/FacetFilter.properties"}],

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {object[]}
       * @default [{cssFile:"./css/FacetFilter.css"}]
       */
      cssRequirements: [{cssFile:"./css/FacetFilter.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * Indicates that the filter should be hidden. This will be set to "true" if any required data is missing
       * 
       * @instance
       * @type {boolean}
       * @default false
       */
      hide: false,
      
      /**
       * The facet qname
       *
       * @instance
       * @type {string}
       * @default null
       */
      facet: null,

      /**
       * The filter (or more accurately the filterId) for this filter
       * 
       * @instance
       * @type {string}
       * @default null
       */
      filter: null,
      
      /**
       * Additional data for the filter (appended after the filter with a bar, e.g. tag|sometag)
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      filterData: "",

      /**
       * Indicate whether or not the filter is currently applied
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      applied: false,
      
      /**
       * The path to use as the source for the image that indicates that a filter has been applied
       *
       * @instance
       * @type {string}
       * default null
       */
      appliedFilterImageSrc: "applied-filter.png",

      /**
       * The alt-text to use for the image that indicates that a filter has been applied
       *
       * @instance
       * @type {string}
       * @default
       */
      appliedFilterAltText: "facet.filter.applied.alt-text",

      /**
       * Sets up the attributes required for the HTML template.
       * @instance
       */
      postMixInProperties: function alfresco_search_FacetFilter__postMixInProperties() {
         if (this.label != null && this.facet != null && this.filter != null && this.hits != null)
         {
            this.label = this.encodeHTML(this.message(this.label));

            // Localize the alt-text for the applied filter message...
            this.appliedFilterAltText = this.message(this.appliedFilterAltText, {0: this.label});

            // Set the source for the image to use to indicate that a filter is applied...
            this.appliedFilterImageSrc = require.toUrl("alfresco/search") + "/css/images/" + this.appliedFilterImageSrc;
         }
         else
         {
            // Hide the filter if there is no label or no link...
            this.alfLog("warn", "Not enough information provided for filter. It will not be displayed", this);
            this.hide = true;
         }
      },
      
      /**
       * @instance
       */
      postCreate: function alfresco_search_FacetFilter__postCreate() {
         if (this.hide == true)
         {
            domClass.add(this.domNode, "hidden");
         }

         if (this.applied)
         {
            domClass.remove(this.removeNode, "hidden");
            domClass.add(this.labelNode, "applied");
         }
      },
      
      /**
       * If the filter has previously been applied then it is removed, if the filter is not applied
       * then it is applied.
       *
       * @instance
       */
      onToggleFilter: function alfresco_search_FacetFilter__onToggleFilter(evt) {
         if (this.applied)
         {
            this.onClearFilter();
         }
         else
         {
            this.onApplyFilter();
         }
      },

      /**
       * Applies the current filter by publishing the details of the filter along with the facet to 
       * which it belongs and then displays the "applied" image.
       *
       * @instance
       */
      onApplyFilter: function alfresco_search_FacetFilter__onApplyFilter() {
         this.alfPublish("ALF_APPLY_FACET_FILTER", {
            filter: this.facet + "|" + this.filter
         });
         domClass.remove(this.removeNode, "hidden");
         domClass.add(this.labelNode, "applied");
         this.applied = true;
      },

      /**
       * Removes the current filter by publishing the details of the filter along with the facet
       * to which it belongs and then hides the "applied" image
       * 
       * @instance
       */
      onClearFilter: function alfresco_search_FacetFilter__onClearFilter() {
         this.alfPublish("ALF_REMOVE_FACET_FILTER", {
            filter: this.facet + "|" + this.filter
         });
         domClass.add(this.removeNode, "hidden");
         domClass.remove(this.labelNode, "applied");
         this.applied = false;
      }
   });
});