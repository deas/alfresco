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
 * @module alfresco/services/TagService
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreXhr
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "alfresco/services/_TagServiceTopics",
        "dojo/_base/lang"],
        function(declare, AlfCore, AlfXhr, _TagServiceTopics, lang) {
   
   return declare([AlfCore, AlfXhr], {
      
      /**
       * This should be set when the current context is a site.
       * 
       * @instance
       * @type {string}
       * @default null
       */
      siteId: null,
      
      /**
       * This should be set when the current context is a site, typically this will be set to "documentlibrary"
       * 
       * @instance
       * @type {string}
       * @default null
       */
      containerId: null,
      
      /**
       * This should be set if "siteId" is not set.
       * 
       * @instance
       * @type {string}
       * @default null
       */
      rootNode: null,
      
      /**
       * Sets up the subscriptions for the TagService
       * 
       * @instance 
       * @param {array} args The constructor arguments.
       */
      constructor: function alf_services_TagService__constructor(args) {
         lang.mixin(this, args);
         this.alfSubscribe(_TagServiceTopics.tagQueryTopic, lang.hitch(this, "onTagQuery"));
      },
      
      /**
       * @instance
       * @param {object} payload
       */
      onTagQuery: function alf_services_TagService__onTagQuery(payload) {
         if (payload == null ||
             typeof payload.callback !== "function" ||
             payload.callbackScope == null)
        {
           this.alfLog("warn", "A request was made for site tag data, but one or more of the following attributes was not provided: 'callback', 'callbackScope':", payload);
        }
        else
        {
           var maxTags = (payload.maxTags != null) ? payload.maxTags : 100;
           var d = new Date().getTime();
           var url = null;
           if (this.siteId != null && this.containerId != null)
           {
              url = Alfresco.constants.PROXY_URI + "api/tagscopes/site/" + this.siteId + "/" + this.containerId + "/tags?d=" + d + "&topN=" + maxTags;
           }
           else if (this.rootNode != null)
           {
              Alfresco.constants.PROXY_URI + "collaboration/tagQuery?d=" + d + "&m=" + maxTags + "&s=count&n=" + encodeURIComponent(this.rootNode);
           }
           else
           {
              this.alfLog("warn", "It is not possible to retrieve tags without a 'siteId' and 'containerId' or a 'rootNode' attribute set on the tag service", this);
           }
           
           if (url != null)
           {
              var config = {
                 url: url,
                 method: "GET",
                 successCallback: payload.callback,
                 callbackScope: payload.callbackScope
              };
              this.serviceXhr(config);
           }
        }
      }
   });
});
