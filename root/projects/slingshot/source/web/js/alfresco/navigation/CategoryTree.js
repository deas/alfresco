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
 * @module alfresco/navigation/CategoryTree
 * @extends module:alfresco/navigation/Tree
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/navigation/Tree",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "dojo/_base/lang"], 
        function(declare, Tree, _AlfDocumentListTopicMixin, lang) {
   
   return declare([Tree, _AlfDocumentListTopicMixin], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/CategoryTree.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/CategoryTree.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/CategoryTree.css"}]
       */
      cssRequirements: [{cssFile:"./css/CategoryTree.css"}],
      
      /**
       * Overrides the inherited value to allow the CSS selectors to change the icon images.
       * 
       * @instance
       * @type {string}
       * @default "categories"
       */
      customCssClasses: "categories",
      
      /**
       * @instance
       * @type {string}
       * @default "categories.root.label"
       */
      rootLabel: "categories.root.label",
      
      /**
       * @instance
       */
      getTargetUrl: function alfresco_navigation_Tree__getTargetUrl() {
         var url = Alfresco.constants.PROXY_URI + "slingshot/doclib/categorynode/node/alfresco/category/root";
         return url;
      },
      
      /**
       * Overrides the inherited value to set the "category" filterId.
       * 
       * @instance
       * @type {string}
       * @default "category"
       */
      onClickFilterId: "category",
      
      /**
       * @instance
       */
      postMixInProperties: function alfresco_navigation_Tree__postMixInProperties() {
         this.inherited(arguments);
         
         // Subscribe to filter change events - in particular we're looking for category filter changes because
         // they won't be represented by an individual filter object
         this.alfSubscribe(this.filterChangeTopic, lang.hitch(this, "onFilterChange"));
      },
      
      /**
       * Provides special case handling for category filtering.
       * 
       * @instance onClick
       * @param {object} payload 
       */
      onFilterChange: function alfresco_navigation_Tree__onFilterChange(payload) {
         if (payload != null && payload.filterId == "category")
         {
            this.alfPublish(this.filterSelectionTopic, {
               value: payload.filterData,
               description: this.message("filter.classified.label", {"0":payload.filterData})
            });
         }
      }
   });
});