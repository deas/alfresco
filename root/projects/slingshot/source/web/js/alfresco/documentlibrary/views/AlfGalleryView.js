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
 * This defines the widget model for rendering the gallery view. This is a grid based layout of thumbnails
 * that can be scaled using a slider control.
 * 
 * @module alfresco/documentlibrary/views/AlfGalleryView
 * @extends module:alfresco/documentlibrary/views/AlfDocumentListView
 * @mixes module:alfresco/documentlibrary/views/layouts/Grid
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/documentlibrary/views/AlfDocumentListView",
        "alfresco/documentlibrary/views/layouts/Grid",
        "alfresco/documentlibrary/AlfGalleryViewSlider",
        "dojo/_base/lang",
        "dojo/dom-construct"], 
        function(declare, AlfDocumentListView, Grid, AlfGalleryViewSlider, lang, domConstruct) {
   
   return declare([AlfDocumentListView], {
      
      /**
       * Returns the name of the view that is used when saving user view preferences.
       * 
       * @instance
       * @returns {string} "gallery"
       */
      getViewName: function alfresco_documentlibrary_views_AlfDocumentListView__getViewName() {
         return "gallery";
      },
      
      /**
       * The configuration for selecting the view (configured the menu item)
       * @instance
       * @type {object}
       * @property {string|null} label The label or message key for the view (as appears in the menus)
       * @property {string|null} iconClass The class to place next to the label
       */
      viewSelectionConfig: {
         label: "Gallery View",
         iconClass: "alf-gallery-icon"
      },
      
      /**
       * Subscribes to a topic to that sets the number of columns for the gallery. This topic is
       * published on by the slider control and which calls the 
       * [updateColumns]{@link module:alfresco/documentlibrary/views/AlfGalleryView#updateColumns} function
       * 
       * @instance
       */
      constructor: function alfresco_documentlibrary_views_AlfGalleryView__constructor() {
         this.alfSubscribe("ALF_DOCLIST_SET_GALLERY_COLUMNS", lang.hitch(this, "updateColumns"));
      },
      
      /**
       * This is the number of columns to use in the grid.
       * 
       * @instance
       * @type {number}
       * @default 4
       */
      columns: 4,

      /**
       * This function updates the [columns]{@link module:alfresco/documentlibrary/views/AlfGalleryView#columns}
       * attribute with the value attribute of the payload argument and then calls the
       * [renderView]{@link module:alfresco/documentlibrary/views/AlfGalleryView#renderView} function followed by the
       * [resizeCells]{@link module:alfresco/documentlibrary/views/AlfGalleryView#resizeCells} function
       * 
       * @instance
       * @param {object}
       */
      updateColumns: function alfresco_documentlibrary_views_AlfGalleryView__updateColumns(payload) {
         if (this.docListRenderer != null && 
             payload != null && 
             payload.value != null &&
             !isNaN(payload.value) &&
             this.columns != payload.value)
         {
            this.alfLog("log", "Update column count to: ", payload.value);
            this.columns = payload.value;
            this.renderView(false);
         }
      },
      
      /**
       * Overridden to return a new instance of "alfresco/documentlibrary/AlfGalleryViewSlider" to control the 
       * number of columns that should be displayed in the gallery.
       * 
       * @instance
       * @returns {object} A new slider control {@link module:alfresco/documentlibrary/AlfGalleryViewSlider}
       */
      getAdditionalControls: function alfresco_documentlibrary_views_AlfGalleryView__getAdditionalControls() {
         return [new AlfGalleryViewSlider({
            relatedViewName: this.getViewName()
         })];
      },
      
      /**
       * Extends the default implementation to resize the cells in the gallery.
       * 
       * @instance
       */
      renderView: function alfresco_documentlibrary_views_AlfGalleryView__renderView() {
         this.inherited(arguments);
         if (this.docListRenderer != null)
         {
            this.docListRenderer.resizeCells();
         }
      },
      
      /**
       * Creates a new [DocumentListRenderer]{@link module:alfresco/documentlibrary/views/DocumentListRenderer}
       * which is used to render the actual items in the view. This function can be overridden by extending views
       * (such as the [Film Strip View]{@link module:alfresco/documentlibrary/views/AlfFilmStripView}) to create
       * alternative widgets applicable to that view.
       * 
       * @instance
       * @returns {object} A new [DocumentListRenderer]{@link module:alfresco/documentlibrary/views/DocumentListRenderer}
       */
      createDocumentListRenderer: function alfresco_documentlibrary_views_AlfGalleryView__createDocumentListRenderer() {
         var dlr = new Grid({
            id: this.id + "_ITEMS",
            widgets: lang.clone(this.widgets),
            currentData: this.currentData,
            pubSubScope: this.pubSubScope,
            parentPubSubScope: this.parentPubSubScope,
            columns: this.columns
         });
         return dlr;
      },

      /**
       * Called after the view has been shown (note that [renderView]{@link module:alfresco/documentlibrary/views/AlfDocumentListView#renderView}
       * does not mean that the view has been displayed, just that it has been rendered. 
       * @instance
       */
      onViewShown: function alfresco_documentlibrary_views_AlfGalleryView__onViewShown() {
         if (this.docListRenderer != null)
         {
            this.docListRenderer.resizeCells();
         }
      },
      
      // /**
      //  * @instance
      //  */
      // allItemsRendered: function alfresco_documentlibrary_views_AlfGalleryView__allItemsRendered() {
      //    var rem = this.currentData.items.length % this.columns;
      //    if (rem != 0)
      //    {
      //       var lastNode = this.tableNode.children[this.tableNode.children.length-1];
      //       for (var i=0; i<rem; i++)
      //       {
      //          domConstruct.create("TD", {}, lastNode);
      //       }
      //    }
      // },
      
      /**
       * The definition for rendering an item in the view.
       * 
       * @instance
       * @type {object[]}
       */
      widgets: [
         {
            name: "alfresco/renderers/GalleryThumbnail"
         }
      ]
   });
});