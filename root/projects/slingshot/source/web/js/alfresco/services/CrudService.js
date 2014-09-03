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
 * This is a generic service for handling CRUD requests between widgets and the repository.
 * 
 * @module alfresco/services/CrudService
 * @extends module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "service/constants/Default",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/json"],
        function(declare, AlfCore, CoreXhr, AlfConstants, lang, array, dojoJson) {
   
   return declare([AlfCore, CoreXhr], {
      
      /**
       * 
       * @instance
       * @param {array} args Constructor arguments
       */
      constructor: function alfresco_services_CrudService__constructor(args) {
         lang.mixin(this, args);
         this.alfSubscribe("ALF_CRUD_GET_ALL", lang.hitch(this, this.onGetAll));
         this.alfSubscribe("ALF_CRUD_GET_ONE", lang.hitch(this, this.onGetOne));
         this.alfSubscribe("ALF_CRUD_CREATE", lang.hitch(this, this.onCreate));
         this.alfSubscribe("ALF_CRUD_UPDATE", lang.hitch(this, this.onUpdate));
         this.alfSubscribe("ALF_CRUD_DELETE", lang.hitch(this, this.onDelete));
      },
      
      /**
       * This is called whenever a create, update or delete operation is performed to ensure that
       * any associated list views are refreshed. It does this by publishing on the "ALF_DOCLIST_RELOAD_DATA"
       * topic (for historical reasons - a more generic topic should be used in the future).
       * 
       * @instance
       * @param {object} response The response from the original XHR request.
       * @param {object} originalRequestConfig The configuration passed to the original XHR request.
       */
      refreshRequest: function alfresco_services_CrudService__refreshRequest(response, originalRequestConfig) {
         var responseTopic = lang.getObject("alfTopic", false, originalRequestConfig);
         if (responseTopic != null)
         {
            this.alfPublish(responseTopic + "_SUCCESS", response);
         }
         else
         {
            this.alfLog("warn", "It was not possible to publish requested CRUD data because the 'responseTopic' attribute was not set on the original request", response, originalRequestConfig);
         }

         // TODO: Need a context sensitive, localized message...
         this.alfPublish("ALF_DISPLAY_NOTIFICATION", {
            message: "Operation Completed Successfully"
         });


         this.alfPublish("ALF_DOCLIST_RELOAD_DATA");
      },

      /**
       * This function is called to get the URL from the payload provided and will issue a warning if one
       * is not found. This is called from all the CRUD handling functions. The payload is expected to contain
       * a 'url' attribute that maps to a Repository WebScript. This function will automatically prefix it
       * with the appropriate proxy stem.
       * 
       * @instance
       * @param {object} payload
       * @returns {string} The URL to use to make the CRUD request or null if no 'url' attribute was provided.
       */
      getUrlFromPayload: function alfresco_services_CrudService__getUrlFromPayload(payload) {
         var url = lang.getObject("url", false, payload);
         if (url === null)
         {
            this.alfLog("warn", "A request was made to service a CRUD request but no 'url' attribute was provided on the payload", payload, this);
         }
         else
         {
            url = AlfConstants.PROXY_URI + url;
         }
         return url;
      },

      /**
       * Makes a GET request to the Repository using the 'url' attribute provided in the payload passed
       * in the publication on the topic that this function subscribes to. The 'url' is expected to be a 
       * Repository WebScript URL and should not include the Repository proxy stem. 
       * 
       * @instance
       * @param {object} payload 
       */
      onGetAll: function alfresco_services_CrudService__onGetAll(payload) {
         var url = this.getUrlFromPayload(payload);
         if (url !== null)
         {
            this.serviceXhr({url: url,
                             alfTopic: (payload.alfResponseTopic ? payload.alfResponseTopic : null),
                             method: "GET"});
         }
      },

      /**
       * TODO: This needs to be completed.
       *
       * @instance
       * @param {object} payload 
       */
      onGetOne: function alfresco_services_CrudService__onGetOne(payload) {
         // TODO: Need to append the identifier to specify the object to retrieve
         var url = this.getUrlFromPayload(payload);
         if (url !== null)
         {
            this.serviceXhr({url: url,
                             method: "GET"});
         }
      },

      /**
       * Creates a new item via the supplied URL. The payload needs to contain both a 'url' attribute
       * (that indicates the REST API to call) and a 'data' attribute (that defines the object to be
       * created).
       * 
       * @instance
       * @param {object} payload 
       */
      onCreate: function alfresco_services_CrudService__onCreate(payload) {
         var url = this.getUrlFromPayload(payload);
         var data = lang.getObject("data", false, payload);
         if (data == null)
         {
            this.alfLog("warn", "A request was made to create a new item but no data was provided", payload, this);
         }
         else if (url !== null)
         {
            this.serviceXhr({url: url,
                             data: data,
                             method: "POST",
                             alfTopic: payload.responseTopic,
                             successCallback: this.refreshRequest,
                             callbackScope: this});
         }
      },

      /**
       *
       * @instance
       * @param {object} payload 
       */
      onUpdate: function alfresco_services_CrudService__onUpdate(payload) {
         var url = this.getUrlFromPayload(payload);
         var data = lang.getObject("data", false, payload);
         if (data == null)
         {
            this.alfLog("warn", "A request was made to update a data item in a QuADDS but no data was provided", payload, this);
         }
         else
         {
            this.serviceXhr({url: url,
                             data: data,
                             method: "PUT",
                             alfTopic: payload.responseTopic,
                             successCallback: this.refreshRequest,
                             callbackScope: this});
         }
      },

      /**
       *
       * @instance
       * @param {object} payload 
       */
      onDelete: function alfresco_services_CrudService__onDelete(payload) {
         // TODO: Need to determine whether or not the ID should be provided in the payload or
         //       as part of the URL.
         var url = this.getUrlFromPayload(payload);
         if (url !== null)
         {
            this.serviceXhr({url : url,
                             method: "DELETE",
                             alfTopic: payload.responseTopic,
                             successCallback: this.refreshRequest,
                             callbackScope: this});
         }
      }
   });
});