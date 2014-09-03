/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * Used to represent a list of documents.
 * @todo Clearly needs more info
 * 
 * @module alfresco/documentlibrary/AlfDocument
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreWidgetProcessing
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin",
        "dojo/text!./templates/AlfDocument.html",
        "alfresco/core/Core",
        "alfresco/core/CoreWidgetProcessing",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dijit/registry",
        "dojo/dom-construct"], 
        function(declare, _WidgetBase, _TemplatedMixin, _MultiItemRendererMixin, template, AlfCore, CoreWidgetProcessing, lang, array, registry, domConstruct) {
   
   return declare([_WidgetBase, _TemplatedMixin, _MultiItemRendererMixin, AlfCore, CoreWidgetProcessing], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {object[]}
       * @default [{cssFile:"./css/AlfDocumentList.css"}]
       */
      cssRequirements: [{cssFile:"./css/AlfDocument.css"}],

      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * The widgets processed by AlfDocument should all be be designed to work with a "currentItem" attribute.
       *
       * @instance
       * @type {object[]}
       * @default
       */
      widgets: null,
      

      /**
       * Subscribes to the document load topic
       * 
       * @instance
       */
      postMixInProperties: function alfresco_documentlibrary_AlfDocument__postMixInProperties() {
         this.alfSubscribe("ALF_RETRIEVE_SINGLE_DOCUMENT_REQUEST_SUCCESS", lang.hitch(this, "onDocumentLoaded"));
      },
      
      /**
       * @instance
       * @param {object} payload The details of the document that have been provided.
       */
      onDocumentLoaded: function alfresco_documentlibrary_AlfDocument__onDocumentLoaded(payload) {
         if (lang.exists("response.item", payload)) 
         {
            this.currentItem = payload.response.item;
            this.renderDocument();
         }
         else
         {
            this.alfLog("warn", "Document data was provided but the 'response.item' attribute was not found", payload, this);
         }
      },

      /**
       * 
       * @instance
       */
      renderDocument: function alfresco_documentlibrary_AlfDocument__renderDocument() {
         if (this.containerNode != null)
         {
            array.forEach(registry.findWidgets(this.containerNode), lang.hitch(this, "destroyWidget"));
            domConstruct.empty(this.containerNode);
         }
         if (this.currentItem != null && this.containerNode != null)
         {
            // This relies on the alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin implementation of the 
            // createWidget function in order to pass the "currentItem" attribute on to any child widgets...
            this.processWidgets(this.widgets, this.containerNode);
         }
         else
         {
            this.alfLog("warn", "It was not possible to render an item because the item either doesn't exist or there is no DOM node for it", this);
         }
      },
      
      /**
       * Recursive destroy the supplied widget.
       * 
       * @instance
       * @param {object} widget The widget to destroy
       * @param {number} index The index of the widget
       */
      destroyWidget: function alfresco_documentlibrary_AlfDocument__destroyWidget(widget, index) {
         if (typeof widget.destroyRecursive === "function")
         {
            widget.destroyRecursive();
         }
      }
   });
});