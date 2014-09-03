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
      dataloadInProgress: false,

      /**
       * Scroll tolerance in pixels.
       *
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

         // hook point to allow other widgets to let us know when they're done processing a scroll request.
         this.alfSubscribe(this.scrollReturn, lang.hitch(this, this.onScrollReturn));

         // Bind to this explicitly to reduce duplication in other widgets
         this.alfSubscribe(this.requestFinishedTopic, lang.hitch(this, this.onScrollReturn));

         // tie in to the events scroll module.
         this.alfSubscribe(this.eventsScrollTopic, lang.hitch(this, this.onEventsScroll));
      },

      /**
       * When the scroll event triggers, check location and pass on the warning that we're near the bottom of the page
       * sets dataloadInProgress to prevent duplicated triggers when the page is scrolled slowly.
       *
       * @instance
       * @param {object} payload
       */
      onEventsScroll: function alfresco_documentlibrary_AlfDocumentListInfiniteScroll_onEventsScroll(payload) {
         if (this.nearBottom() && !this.dataloadInProgress) {
            this.dataloadInProgress = true;
            this.alfPublish(this.scrollNearBottom);
         }
      },

      /**
       * Called when infinite scroll request has been processed and allows us to trigger further scroll events
       *
       * @instance
       * @param {object} payload
       */
      onScrollReturn: function alfresco_documentlibrary_AlfDocumentListInfiniteScroll_onScrollReturn(payload) {
         this.dataloadInProgress = false;
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