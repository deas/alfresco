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
 * @module alfresco/documentlibrary/AlfDocumentListInfiniteScroll
 *
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @author david.webster@alfresco.com
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "alfresco/core/Events",
        "alfresco/core/EventsTopicMixin",
        "dojo/dom-geometry",
        "alfresco/core/DomElementUtils"],
        function(declare,_WidgetBase, _AlfDocumentListTopicMixin, AlfCore, lang, AlfCoreEvents, AlfCoreEventsTopicMixin, domGeom, AlfDomUtils) {

   return declare([_WidgetBase, _AlfDocumentListTopicMixin, AlfCore, AlfCoreEvents, AlfCoreEventsTopicMixin, AlfDomUtils], {

      /**
       * Used to keep track of the current status of the InfiniteScroll
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      requestInProgress: false,

      /**
       * How close to the bottom of the page do we want to get before we request the next items?
       *
       * @instance
       * @type {int}
       * @default 500
       */
      scrollTolerance: 500,

      /**
       * @instance
       */
      postMixInProperties: function alfresco_documentlibrary_AlfDocumentListInfiniteScroll_postMixInProperties() {
         this.alfSubscribe("ALF_RETRIEVE_DOCUMENTS_REQUEST_SUCCESS", lang.hitch(this, "onDocumentsRequestReturn"));
         this.alfSubscribe("ALF_RETRIEVE_DOCUMENTS_REQUEST_FAILURE", lang.hitch(this, "onDocumentsRequestReturn"));
         this.alfSubscribe(this.eventsScrollTopic, lang.hitch(this, "onEventsScroll"));
      },

      /**
       *
       * @instance
       * @param {object} payload
       */
      onEventsScroll: function alfresco_documentlibrary_AlfDocumentListInfiniteScroll_onEventsScroll(payload) {
         if (this.nearBottom() && !this.requestInProgress) {
            this.alfPublish(this.scrollNearBottom);
         }
      },

      /**
       * Reset the request in progress flag so that another request could be triggered if we hit the bottom again.
       *
       * @instance
       * @param {object} payload The details of the documents that have been loaded
       */
      onDocumentsRequestReturn: function alfresco_documentlibrary_AlfDocumentListInfiniteScroll_onDocumentsRequestReturn(payload) {
         this.requestInProgress = false;
      },

      /**
       * The calculation to determine if we're at or close to the bottom of the page yet or now.
       * "close to" bottom is defined by scrollTolerance var.
       *
       * @instance
       * @returns {boolean}
       */
      nearBottom: function alfresco_documentlibrary_AlfDocumentListInfiniteScroll_nearBottom() {
         var currentScrollPos = domGeom.docScroll().y,
            docHeight = this.getDocumentHeight(),
            viewport = domGeom.getContentBox(this.ownerDocumentBody).h;


         return 0 >= (docHeight - viewport - currentScrollPos - this.scrollTolerance);
      }

   });
});