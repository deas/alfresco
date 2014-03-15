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
 * This extends the default [AlfDocumentListView]{@link module:alfresco/documentlibrary/views/AlfDocumentListView}
 * to set an alternative HTML template that contains a <thead> element. It additionally extends the postCreate function
 * to process a row of header widgets. This allows this widget to be used as the basis for grid style views.
 * 
 * @module alfresco/documentlibrary/views/AlfDocumentListWithHeaderView
 * @extends alfresco/documentlibrary/views/AlfDocumentListView
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/documentlibrary/views/AlfDocumentListView", 
        "dojo/text!./templates/AlfDocumentListWithHeaderView.html",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dijit/registry"], 
        function(declare, AlfDocumentListView, template, lang, array, domConstruct, domClass, registry) {
   
   return declare([AlfDocumentListView], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {object[]}
       * @default [{cssFile:"./css/AlfDialog.css"}]
       */
      cssRequirements: [{cssFile:"./css/AlfDocumentListWithHeaderView.css"}],

      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,

      /**
       * Extends the inherited implementation to call the renderHeader function.
       *
       * @instance
       */
      postCreate: function alfresco_documentlibrary_views_AlfDocumentListWithHeaderView__postCreate() {
         this.inherited(arguments);
         this.renderHeader();
      },

      /** 
       * @instance
       */
      renderHeader: function alfresco_documentlibrary_views_AlfDocumentListWithHeaderView__renderHeader() {
         // This is something of a hack to work around the original expected behaviour of a view...
         // It was expected that sub-widgets would need to have a current item set as they were iterated over
         // but obviously that doesn't apply to the header widgets. Therefore we're going to set a currentItem
         // process the header widgets and then remove it again...
         // Without this the header widgets would not get rendered (see the _MultiItemRendererMixin code)
         this.currentItem = {};
         if (this.widgetsForHeader != null)
         {
            this.processWidgets(this.widgetsForHeader, this.headerNode);
         }
         else
         {
            this.alfLog("warn", "A view containing a header was used in a model, but no 'widgetsForHeader' attribute was defined", this);
         }
         this.currentItem = null;
      }
   });
});