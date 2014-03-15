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
 * @module alfresco/documentlibrary/views/layouts/HeaderCell
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/HeaderCell.html",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "dojo/dom-class"], 
        function(declare, _WidgetBase, _TemplatedMixin, template, AlfCore, lang, domClass) {

   return declare([_WidgetBase, _TemplatedMixin, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/Cell.css"}]
       */
      cssRequirements: [{cssFile:"./css/HeaderCell.css"}],
      
      /**
       * The HTML template to use for the widget.
       * 
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * Indicates whether or not this header can actually be used to trigger sort requests.
       * 
       * @instance
       * @type boolean
       * @default false
       */
      sortable: true,

      /**
       * Indicate whether or not this cell is currently being used as the sort field.
       *
       * @instance
       * @type boolean
       * @default false
       */
      usedForSort: false,

      /**
       * Indicates whether or not the column headed by this cell is sorted in ascending order or not.
       * This value is only relevant when the [usedForSort]{@link module:alfresco/documentlibrary/views/layouts/HeaderCell#usedForSort}
       * attribute is true.
       * 
       * @instance
       * @type boolean
       * @default false
       */
      sortedAscending: false,

      /**
       * The value to publish to sort on.
       *
       * @instance
       * @type {string}
       * @default null
       */
      sortValue: null,

      /**
       * @instance
       */
      postMixinProperties: function alfresco_documentlibrary_views_layouts_HeaderCell__postMixinProperties() {
         if (this.label != null)
         {
            this.label = this.message(this.label);
         }
         this.currentItem = {};
      },

      /**
       * Calls [processWidgets]{@link module:alfresco/core/Core#processWidgets}
       * 
       * @instance postCreate
       */
      postCreate: function alfresco_documentlibrary_views_layouts_HeaderCell__postCreate() {

         this.alfSubscribe("ALF_DOCLIST_SORT", lang.hitch(this, "onExternalSortRequest"));
         this.alfSubscribe("ALF_DOCLIST_SORT_FIELD_SELECTION", lang.hitch(this, "onExternalSortRequest"));

         if (this.sortable == false || this.usedForSort == false)
         {
            domClass.add(this.ascendingSortNode, "hidden");
            domClass.add(this.descendingSortNode, "hidden");
         }
         else
         {
            if (this.sortedAscending == false)
            {
               domClass.add(this.descendingSortNode, "hidden");
            }
            else
            {
               domClass.add(this.ascendingSortNode, "hidden");
            }
         }
      },

      /**
       * This function is called whenever the header cell is clicked. It publishes a request to 
       * resort the current data and updates its display
       *
       * @instance
       * @param {object} evt The click event
       */
      onSortClick: function alfresco_documentlibrary_views_layouts_HeaderCell__onSortClick(evt) {
         if (this.sortable == true)
         {
            this.alfLog("log", "Sort request received");
            if (this.usedForSort == false)
            {
               // If currently NOT being used for sort then use it now...
               this.usedForSort = true;
               this.sortedAscending = true;
               domClass.remove(this.ascendingSortNode, "hidden");
               this.publishSortRequest();
            }
            else if (this.sortedAscending == true)
            {
               domClass.add(this.ascendingSortNode, "hidden");
               domClass.remove(this.descendingSortNode, "hidden");
               this.sortedAscending = false;
               this.publishSortRequest();
            }
            else
            {
               domClass.remove(this.ascendingSortNode, "hidden");
               domClass.add(this.descendingSortNode, "hidden");
               this.sortedAscending = true;
               this.publishSortRequest();
            }
         }
      },

      /**
       * @instance
       */
      publishSortRequest: function alfresco_documentlibrary_views_layouts_HeaderCell__publishSortRequest() {
         this.alfPublish("ALF_DOCLIST_SORT", {
            direction: (this.sortedAscending) ? "ascending" : "descending",
            value: this.sortValue,
            requester: this
         });
      },

      /**
       * This handles external sort requests so that the header cell can match the current 
       * status.
       *
       * @instance
       * @param {object} payload
       */
      onExternalSortRequest: function alfresco_documentlibrary_views_layouts_HeaderCell__onExternalSortRequest(payload) {
         var requester = lang.getObject("requester", false, payload);
         if (requester != this)
         {
            var value = lang.getObject("value", false, payload);
            if (value != null)
            {
               if (value == this.sortValue)
               {
                  this.usedForSort = true;
                  if (this.sortedAscending == true)
                  {
                     domClass.add(this.ascendingSortNode, "hidden");
                     domClass.remove(this.descendingSortNode, "hidden");
                  }
                  else
                  {
                     domClass.remove(this.ascendingSortNode, "hidden");
                     domClass.add(this.descendingSortNode, "hidden");
                  }
               }
               else
               {
                  // A different field has been used for sorting, hide the sort icons and update the status...
                  this.usedForSort = false;
                  domClass.add(this.ascendingSortNode, "hidden");
                  domClass.add(this.descendingSortNode, "hidden");
               }
            }

            var direction = lang.getObject("direction", false, payload);
            if (direction != null)
            {
               this.sortedAscending = (direction == "ascending");
               if (this.usedForSort == true)
               {
                  if (this.sortedAscending == true)
                  {
                     domClass.add(this.ascendingSortNode, "hidden");
                     domClass.remove(this.descendingSortNode, "hidden");
                  }
                  else
                  {
                     domClass.remove(this.ascendingSortNode, "hidden");
                     domClass.add(this.descendingSortNode, "hidden");
                  }
               }
            }
         }
      }
   });
});