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
 * @module alfresco/services/QuickShareService
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreXhr
 * @mixes module:alfresco/services/_QuickShareServiceTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "alfresco/services/_QuickShareServiceTopicMixin",
        "dojo/_base/lang"],
        function(declare, AlfCore, CoreXhr, _QuickShareServiceTopicMixin, lang) {
   
   return declare([AlfCore, CoreXhr, _QuickShareServiceTopicMixin], {
      
      /**
       * Sets up the subscriptions for the RatingsService
       * 
       * @instance
       * @param {array} args Constructor arguments
       */
      constructor: function alfresco_services_QuickShareService__constructor(args) {
         lang.mixin(this, args);
         this.alfSubscribe(this.addQuickShareTopic, lang.hitch(this, "onAddQuickShare"));
         this.alfSubscribe(this.removeQuickShareTopic, lang.hitch(this, "onRemoveQuickShare"));
         this.alfSubscribe(this.getQuickShareLinkTopic, lang.hitch(this, "onQuickShareLinkRequest"));
         this.alfSubscribe(this.getSocialLinksTopic, lang.hitch(this, "onSocialLinksRequest"));
      },

      /**
       * @instance
       * @param {object} payload
       */
      onQuickShareLinkRequest: function alfresco_services_QuickShareService__onQuickShareLinkRequest(payload) {
         if (payload != null && 
             payload.callback != null &&
             payload.callbackScope != null)
         {
            if (this.quickShareLink != null)
            {
               payload.callback.call(payload.callbackScope, this.quickShareLink);
            }
            else
            {
               this.alfLog("warn", "A request was made for the QuickShare link but it has not been configured", this);
            }
         }
         else
         {
            this.alfLog("warn", "A request was made for the QuickShare link, but either the 'callback' or 'callbackScope' attributes were not provided", payload);
         }
      },
      
      /**
       * @instance
       * @param {object} payload
       */
      onSocialLinksRequest: function alfresco_services_QuickShareService__onSocialLinksRequest(payload) {
         if (payload != null && 
             payload.callback != null &&
             payload.callbackScope != null)
         {
            if (this.socialLinks != null)
            {
               payload.callback.call(payload.callbackScope, this.socialLinks);
            }
            else
            {
               this.alfLog("warn", "A request was made for the Social Links but they have not been configured", this);
            }
            
         }
         else
         {
            this.alfLog("warn", "A request was made for the Social Links, but either the 'callback' or 'callbackScope' attributes were not provided", payload);
         }
      },
      
      /**
       * Returns a URL for quick sharing a node
       * 
       * @instance
       * @param {string} nodeRefUri The nodeRef URI fragment representation
       */
      getAddQuickShareUrl: function alfresco_services_QuickShareService__getRatingsUrl(nodeRefUri) {
         return Alfresco.constants.PROXY_URI + "api/internal/shared/share/" + nodeRefUri;
      },
      
      /**
       * Returns a URL for removing the the quick share of a node.
       * 
       * @instance
       * @param {string} shareId The id to of the share to remove
       */
      getRemoveQuickShareUrl: function alfresco_services_QuickShareService__getRatingsUrl(shareId) {
         return Alfresco.constants.PROXY_URI + "api/internal/shared/unshare/" + shareId;
      },
      
      /**
       * Makes a request to quick share an node
       * 
       * @instance
       * @param {object} payload
       */
      onAddQuickShare: function alfresco_services_QuickShareService__onAddRating(payload) {
         if (payload != null &&
             payload.node != null &&
             payload.node.jsNode != null &&
             payload.node.jsNode.nodeRef != null &&
             payload.node.jsNode.nodeRef.uri != null)
         {
            var url = this.getAddQuickShareUrl(payload.node.jsNode.nodeRef.uri);
            var data = {
               node: payload.node
            };
            this.serviceXhr({url : url,
                             data: data,
                             method: "POST",
                             successCallback: this.onAddQuickShareSuccess,
                             failureCallback: this.onAddQuickShareFailure,
                             callbackScope: this});
         }
      },
      
      /**
       * This handles successfully completed requests to add a quick share.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      onAddQuickShareSuccess: function alfresco_services_QuickShareService__onAddQuickShareSuccess(response, originalRequestConfig) {
         this.alfLog("log", "Successfully quick shared a document", response, originalRequestConfig);
         this.alfPublish(this.addQuickShareSuccessTopic, {
            response: response,
            requestConfig: originalRequestConfig
         });
      },
      
      /**
       * This handles unsuccessful requests to add a quick share.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      onAddQuickShareFailure: function alfresco_services_QuickShareService__onAddQuickShareFailure(response, originalRequestConfig) {
         this.alfLog("error", "Failed to remove a document quick share", response, originalRequestConfig);
         this.alfPublish(this.addQuickShareFailureTopic, {
            response: response,
            requestConfig: originalRequestConfig
         });
      },
      
      /**
       * Makes a request to removes a quick share
       * 
       * @instance
       * @param {object} payload
       */
      onRemoveQuickShare: function alfresco_services_QuickShareService__onRemoveQuickShare(payload) {
         if (payload != null &&
             payload.node != null &&
             (payload.sharedId != null ||
              (payload.node.jsNode != null &&
              payload.node.jsNode.properties != null &&
              payload.node.jsNode.properties["qshare:sharedId"] != null)))
         {
            var quickShareId = (payload.sharedId != null) ? payload.sharedId : payload.node.jsNode.properties["qshare:sharedId"];
            var url = this.getRemoveQuickShareUrl(quickShareId);
            var data = {
               node: payload.node
            };
            this.serviceXhr({url : url,
                             data: data,
                             method: "DELETE", 
                             successCallback: this.onRemoveQuickShareSuccess,
                             failureCallback: this.onRemoveQuickShareFailure,
                             callbackScope: this});
         }
      },
      
      /**
       * This handles successfully completed requests to remove a quick share.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      onRemoveQuickShareSuccess: function alfresco_services_QuickShareService__onRemoveQuickShareSuccess(response, originalRequestConfig) {
         this.alfLog("log", "Successfully removed a document quick share", response, originalRequestConfig);
         this.alfPublish(this.removeQuickShareSuccessTopic, {
            response: response,
            requestConfig: originalRequestConfig
         });
      },
      
      /**
       * This handles unsuccessful requests to remove a quick share.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      onRemoveQuickShareFailure: function alfresco_services_QuickShareService__onRemoveQuickShareFailure(response, originalRequestConfig) {
         this.alfLog("error", "Failed to remove a document quick share", response, originalRequestConfig);
         this.alfPublish(this.removeQuickShareFailureTopic, {
            response: response,
            requestConfig: originalRequestConfig
         });
      }
   });
});