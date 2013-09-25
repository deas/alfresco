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
 * @module alfresco/services/RatingsService
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreXhr
 * @mixes module:alfresco/services/_RatingsServiceTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "alfresco/services/_RatingsServiceTopicMixin",
        "dojo/_base/lang"],
        function(declare, AlfCore, CoreXhr, _RatingsServiceTopicMixin, lang) {
   
   return declare([AlfCore, CoreXhr, _RatingsServiceTopicMixin], {
      
      /**
       * Sets up the subscriptions for the RatingsService
       * 
       * @instance
       * @param {array} args Constructor arguments
       */
      constructor: function alfresco_services_RatingsService__constructor(args) {
         lang.mixin(this, args);
         this.alfSubscribe(this.addRatingTopic, lang.hitch(this, "onAddRating"));
         this.alfSubscribe(this.removeRatingTopic, lang.hitch(this, "onRemoveRating"));
      },
      
      /**
       * Returns a URL for adding and removing ratings for a node.
       * 
       * @instance
       * @param {string} nodeRefUri The nodeRef URI fragment representation
       */
      getAddRatingsUrl: function alfresco_services_RatingsService__getRatingsUrl(nodeRefUri) {
         return Alfresco.constants.PROXY_URI + "api/node/" + nodeRefUri + "/ratings";
      },
      
      /**
       * Returns a URL for adding and removing ratings for a node.
       * 
       * @instance
       * @param {string} nodeRefUri The nodeRef URI fragment representation
       */
      getRemoveRatingsUrl: function alfresco_services_RatingsService__getRatingsUrl(nodeRefUri) {
         return Alfresco.constants.PROXY_URI + "api/node/" + nodeRefUri + "/ratings/likesRatingScheme";
      },
      
      /**
       * Adds a rating
       * 
       * @instance
       * @param {object} payload
       */
      onAddRating: function alfresco_services_RatingsService__onAddRating(payload) {
         if (payload != null &&
             payload.node != null &&
             payload.node.jsNode != null &&
             payload.node.jsNode.nodeRef != null &&
             payload.node.jsNode.nodeRef.uri != null)
         {
            var url = this.getAddRatingsUrl(payload.node.jsNode.nodeRef.uri);
            var data = {
               nodeRefUri: payload.node.jsNode.nodeRef.uri,
               rating: 1,
               ratingScheme: "likesRatingScheme"
            };
            
            this.serviceXhr({url : url,
                             data: data,
                             method: "POST",
                             successCallback: this.onAddRatingSuccess,
                             failureCallback: this.onAddRatingFailure,
                             callbackScope: this});
         }
      },
      
      /**
       * This handles successfully completed requests to add a rating.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      onAddRatingSuccess: function alfresco_services_RatingsService__onAddRatingSuccess(response, originalRequestConfig) {
         this.alfLog("log", "Successfully rated a document", response, originalRequestConfig);
         this.alfPublish(this.addRatingSuccessTopic, {
            response: response,
            requestConfig: originalRequestConfig
         });
      },
      
      /**
       * This handles unsuccessful requests to add a rating.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      onAddRatingFailure: function alfresco_services_RatingsService__onAddRatingFailure(response, originalRequestConfig) {
         this.alfLog("error", "Failed to rate a document", response, originalRequestConfig);
         this.alfPublish(this.addRatingFailureTopic, {
            response: response,
            requestConfig: originalRequestConfig
         });
      },
      
      /**
       * Removes a rating
       * 
       * @instance
       * @param {object} payload
       */
      onRemoveRating: function alfresco_services_RatingsService__onRemoveRating(payload) {
         if (payload != null &&
             payload.node != null &&
             payload.node.jsNode != null &&
             payload.node.jsNode.nodeRef != null &&
             payload.node.jsNode.nodeRef.uri != null)
         {
            var url = this.getRemoveRatingsUrl(payload.node.jsNode.nodeRef.uri);
            var data = {
               nodeRefUri: payload.node.jsNode.nodeRef.uri,
               ratingScheme: "likesRatingScheme"
            };
            this.serviceXhr({url : url,
                             data: data,
                             method: "DELETE", 
                             successCallback: this.onRemoveRatingSuccess,
                             failureCallback: this.onRemoveRatingFailure,
                             callbackScope: this});
         }
      },
      
      /**
       * This handles successfully completed requests to remove a rating.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      onRemoveRatingSuccess: function alfresco_services_RatingsService__onRemoveRatingSuccess(response, originalRequestConfig) {
         this.alfLog("log", "Successfully removed a document rating", response, originalRequestConfig);
         this.alfPublish(this.removeRatingSuccessTopic, {
            response: response,
            requestConfig: originalRequestConfig
         });
      },
      
      /**
       * This handles unsuccessful requests to remove a rating.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      onRemoveRatingFailure: function alfresco_services_RatingsService__onRemoveRatingFailure(response, originalRequestConfig) {
         this.alfLog("error", "Failed to remove a document rating", response, originalRequestConfig);
         this.alfPublish(this.removeRatingFailureTopic, {
            response: response,
            requestConfig: originalRequestConfig
         });
      }
   });
});