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
 * This is used as the standard search result template used to control the layout of search results.
 * It is more efficient to use a single widget to control the layout than to build a complex model
 * out of smaller widgets, however this widget can still be easily replaced by other widgets to 
 * provide a completely custom rendering.
 * 
 * @module alfresco/search/AlfSearchResult
 * @extends alfresco/documentlibrary/views/layouts/Row
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/documentlibrary/views/layouts/Row", 
        "dojo/text!./templates/AlfSearchResult.html",
        "alfresco/renderers/Thumbnail",
        "alfresco/renderers/SearchResultPropertyLink",
        "alfresco/renderers/Property",
        "alfresco/renderers/Date",
        "dojo/_base/lang",
        "dojo/dom-class"], 
        function(declare, Row, template, Thumbnail, PropertyLink, Property, Date, lang, domClass) {

   return declare([Row], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/AlfSearchResult.css"}]
       */
      cssRequirements: [{cssFile:"./css/AlfSearchResult.css"}],
      
      /**
       * The HTML template to use for the widget.
       * 
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * Creates the renderers to display for a search result and adds them into the template. Renderers
       * will only be created if there is data for them. This is done to further improve the performance
       * of the search rendering.
       * 
       * @instance postCreate
       */
      postCreate: function alfresco_search_AlfSearchResult__postCreate() {
         new Thumbnail({
            currentItem: this.currentItem,
            pubSubScope: this.pubSubScope
         }, this.thumbnailNode);

         new PropertyLink({
            currentItem: this.currentItem,
            pubSubScope: this.pubSubScope,
            propertyToRender: "displayName",
            renderSize: "large",
            publishTopic: "ALF_SEARCH_RESULT_LINK"
         }, this.nameNode);

         if (this.currentItem.title == null || this.currentItem.title == "")
         {
            domClass.add(this.titleNode, "hidden")
         }
         else
         {
            new Property({
               currentItem: this.currentItem,
               pubSubScope: this.pubSubScope,
               propertyToRender: "title",
               renderSize: "small",
               renderedValuePrefix: "(",
               renderedValueSuffix: ")"
            }, this.titleNode);
         }
         
         new Date({
            currentItem: this.currentItem,
            pubSubScope: this.pubSubScope,
            modifiedDateProperty: "modifiedOn",
            modifiedByProperty: "modifiedBy"
         }, this.dateNode);

         if (this.currentItem.description == null || this.currentItem.description == "")
         {
            domClass.add(this.descriptionNode, "hidden")
         }
         else
         {
            new Property({
               currentItem: this.currentItem,
               pubSubScope: this.pubSubScope,
               propertyToRender: "description",
            }, this.descriptionNode);
         }

         var site = lang.getObject("site.title", false, this.currentItem);
         if (site == null || site == "")
         {
            domClass.add(this.descriptionNode, "hidden")
         }
         else
         {
            new Property({
               currentItem: this.currentItem,
               pubSubScope: this.pubSubScope,
               propertyToRender: "site.title",
               renderedValuePrefix: this.message("faceted-search.doc-lib.value-prefix.site") + " "
            }, this.siteNode);
         }
      }
   });
});