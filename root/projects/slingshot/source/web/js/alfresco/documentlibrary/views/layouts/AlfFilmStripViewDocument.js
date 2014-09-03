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
 * 
 * @module alfresco/documentlibrary/views/layouts/AlfFilmStripViewDocument
 * @extends module:alfresco/documentlibrary/AlfDocument
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/documentlibrary/AlfDocument",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dijit/registry",
        "dojo/dom-construct"], 
        function(declare, AlfDocument,lang, array, registry, domConstruct) {
   
   return declare([AlfDocument], {
      
      /**
       * Overrides the [inherited function]{@link module:alfresco/documentlibrary/AlfDocument#postMixInProperties}
       * to prevent the subscription to the standard document request from being created.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_documentlibrary_views_layouts_AlfFilmStripViewDocument__postMixInProperties() {
         // Intentionally does nothing.
      },

      /**
       * Sets up a NodeRef specific subscription if the current item is a document (such that the document data is only
       * requested if asked for by the [Film Strip View]{@link module:alfresco/documentlibrary/views/AlfFilmStripView})
       * and if the current item is a container then the standard thumbnail is rendered
       *
       * @instance
       */
      postCreate: function alfresco_documentlibrary_views_layouts_AlfFilmStripViewDocument__postCreate() {
         this.isContainer = lang.getObject("currentItem.node.isContainer", false, this);
         this.nodeRef = lang.getObject("currentItem.nodeRef", false, this);
         if (this.isContainer === null)
         {
            // No container information, no subscription
         }
         else if (this.isContainer === true)
         {
            // This is a container, assume it's a folder and render the folder thumbnail.
            this.processWidgets(lang.clone(this.widgetsForContainer), this.containerNode);
         }
         else if (this.isContainer === false)
         {
            // This is a document so we can subscribe to the expected request to display content
            this.widgets = [{
               name: "alfresco/preview/AlfDocumentPreview"
            }];
            this.alfSubscribe("ALF_FILMSTRIP_DOCUMENT_REQUEST__" + this.nodeRef, lang.hitch(this, this.requestDocument, this.nodeRef));
         }
      },

      /**
       * Tracks whether or not the document has already been rendered
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      _documentRequested: false,

      /**
       * This function is provided so that explicit requests can be made to generate the preview
       *
       * @instance
       */
      render: function alfresco_documentlibrary_views_layouts_AlfFilmStripViewDocument__render() {
         if (!this.isContainer && this.nodeRef)
         {
            this.requestDocument(this.nodeRef);
         }
      },

      /**
       * Handles requests to obtain document data in order create a document preview. The specific node is requested
       * via the [DocumentService]{@link module:alfresco/services/DocumentService}.
       *
       * @instance
       * @param {string} nodeRef The NodeRef to request the data for
       * @param {object} payload The payload of the request
       */
      requestDocument: function alfresco_documentlibrary_views_layouts_AlfFilmStripViewDocument__requestDocument(nodeRef, payload) {
         if (!this._documentRequested)
         {
            var uuid = this.generateUuid();
            this.alfSubscribe(uuid + "_SUCCESS", lang.hitch(this, this.onDocumentLoaded));
            this.alfPublish("ALF_RETRIEVE_SINGLE_DOCUMENT_REQUEST", {
               nodeRef: nodeRef,
               alfResponseTopic: this.pubSubScope + uuid
            }, true);
            this._documentRequested = true;
         }
      },

      /**
       * Defines the widgets to use for containers.
       *
       * @instance
       * @type {array}
       */
      widgetsForContainer: [
         {
            name: "alfresco/renderers/Thumbnail",
            config: {
               folderImage: "folder-256.png"
            }
         }
      ]
   });
});