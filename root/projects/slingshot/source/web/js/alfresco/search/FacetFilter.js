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
       * Sets up the attributes required for the HTML template.
       * @instance
       */
      postMixInProperties: function alfresco_search_FacetFilter__postMixInProperties() {
         if (this.label != null && this.facet != null && this.filter != null && this.hits != null)
         {
            this.label = this.encodeHTML(this.message(this.label));
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
       * 
       * @instance
       * @param {object} evt 
       */
      onApplyFilter: function alfresco_search_FacetFilter__onApplyFilter(evt) {
         this.alfPublish("ALF_APPLY_FACET_FILTER", {
            filter: this.facet + "|" + this.filter
         });
         domClass.remove(this.removeNode, "hidden");
         domClass.add(this.labelNode, "applied");
      },

      /**
       * @instance
       */
      onClearFilter: function alfresco_search_FacetFilter__onClearFilter(evt) {
         this.alfPublish("ALF_REMOVE_FACET_FILTER", {
            filter: this.facet + "|" + this.filter
         });
         domClass.add(this.removeNode, "hidden");
         domClass.remove(this.labelNode, "applied");
      }
   });
});