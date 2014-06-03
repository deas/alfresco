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
 * Extends the abstract [document list view]{@link module:alfresco/documentlibrary/views/AlfDocumentListView}
 * to provide a specific set of instructions to display when no search results are available. This advice is in the
 * form of a bulleted ordered list and is configurable through the [searchAdvice attribute]{@link module:alfresco/documentlibrary/views/AlfSearchListView#searchAdvice}
 *
 * @module alfresco/documentlibrary/views/AlfSearchListView
 * @extends module:alfresco/documentlibrary/views/AlfDocumentListView
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/documentlibrary/views/AlfDocumentListView",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "alfresco/core/Core",
        "dojo/text!./templates/NoSearchResults.html",
        "dojo/_base/array",
        "dojo/dom-construct"], 
        function(declare, AlfDocumentListView,  _WidgetBase, _TemplatedMixin, AlfCore, template, array, domConstruct) {
   
   // Inner widget to render a template of advice...
   var NoSearchResultsTemplate = declare([_WidgetBase, _TemplatedMixin, AlfCore], {
      
      /**
       * @instance
       * @type {string}
       */
      templateString: template,

      /**
       * Iterates over the suggestions.
       *
       * @instance
       */
      postCreate: function alfresco_documentlibrary_views_AlfSearchListView_NoSearchResultsTemplate__postCreate() {
         if (this.title != null)
         {
            this.titleNode.innerHTML = this.message(this.title);
         }
         if (this.suggestions != null)
         {
            array.forEach(this.suggestions, function(suggestion, index) {
               domConstruct.create("li", {
                  innerHTML: this.message(suggestion),
                  className: "suggestion"
               }, this.suggestionsNode, "last");
            }, this);
         }
      }
   });

   return declare([AlfDocumentListView], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {object[]}
       * @default [{cssFile:"./css/AlfSearchListView.css"}]
       */
      cssRequirements: [{cssFile:"./css/AlfSearchListView.css"}],

      /**
       * The definition of how a single item is represented in the view. 
       * 
       * @instance
       * @type {object[]}
       */
      widgets: [
         {
            name: "alfresco/search/AlfSearchResult"
         }
      ],

      /**
       * Overrides the [inherited function]{@link module:alfresco/documentlibrary/views/AlfDocumentListView#renderNoDataDisplay}
       * to render a template providing useful information to help users get some results for their
       * search.
       * 
       * @instance
       */
      renderNoDataDisplay: function alfresco_documentlibrary_views_AlfSearchListView__renderNoDataDisplay() {
         this.clearOldView();
         this.messageNode = domConstruct.create("div", {}, this.domNode);
         this.docListRenderer = new NoSearchResultsTemplate({
            title: this.message(this.searchAdviceTitle),
            suggestions: this.searchAdvice
         },this.messageNode);
      },

      /**
       * A configurable title for the list of searchAdvice elements.
       *
       * @instance
       * @type {string}
       * @default "faceted-search.advice.title"
       */
      searchAdviceTitle: "faceted-search.advice.title:",
      
      /**
       * A configurable array of strings where each entry is a suggestion for how to get the best results out of
       * the search.
       *
       * @instance
       * @type {array}
       * @default []
       */
      searchAdvice: [] 
   });
});