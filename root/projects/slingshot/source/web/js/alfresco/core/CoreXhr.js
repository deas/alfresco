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
 * @module alfresco/core/CoreXhr
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/registry",
        "dojo/topic",
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/dom-construct",
        "dojox/uuid/generateRandomUuid",
        "dojo/request/xhr",
        "dojo/json",
        "dojo/date/stamp"], 
        function(declare, registry, pubSub, array, lang, domConstruct, uuid, xhr, JSON, stamp) {
   
   return declare(null, {
      
      /**
       * This function can be used to clean up JSON responses to remove any superfluous whitespace characters and
       * remove any trailing commas in arrays/objects. This function is particularly handy since Dojo can be very
       * fussy about JSON.
       * 
       * @instance
       * @param {string} input
       * @returns {string} A cleaned up JSON response.
       */
      cleanupJSONResponse: function alfresco_core_Core__cleanupJSONResponse(input) {
         var r = input;
         if (typeof input == "string")
         {
            r = input.replace(/,}/g, "}");
         }
         return r;
      },
      
      /**
       * This method handles XHR requests. As well as providing default callback handlers for the success, failure and
       * progress responses it also performs some additional JSON cleanup of responses (where required) which is useful
       * when REST APIs return invalid code (this is especially useful as Dojo can be quite particular about parsing
       * JSON).  
       * 
       * The function takes a single object as an argument that will allow updates to be made to include additional
       * data and provide defaults when it is not provided.
       * 
       * By default this function will issue a POST method
       * 
       * @instance
       * @param {Object} config The configuration for the request
       * @todo List the available config object attributes
       */
      serviceXhr: function alfresco_core_Core__serviceXhr(config) {
         
         var _this = this;
         
         if (config)
         {
            if (typeof config.url == "undefined")
            {
               this.alfLog("error", "An XHR request was made but no URL was provided", config);
            }
            else
            {
               var headers = (config.headers) ? config.headers : { 'Content-Type': 'application/json' };
               if (Alfresco && Alfresco.util && Alfresco.util.CSRFPolicy && Alfresco.util.CSRFPolicy.isFilterEnabled())
               {
                  headers[Alfresco.util.CSRFPolicy.getHeader()] = Alfresco.util.CSRFPolicy.getToken();
               }
               
               xhr(config.url, {
                  handleAs: (config.handleAs) ? config.handleAs : "text",
                  method: (config.method) ? config.method : "POST",
                  data: (config.data) ? JSON.stringify(config.data) : null,
                  query: (config.query) ? config.query : null,
                  headers: headers
               }).then(function(response){
                  
                  // HANDLE SUCCESS...
                  if (typeof response == "string")
                  {
                     response = JSON.parse(_this.cleanupJSONResponse(response));
                  }
                  if (typeof config.successCallback == "function")
                  {
                     var callbackScope = (config.successCallbackScope ? config.successCallbackScope : (config.callbackScope ? config.callbackScope : _this)); 
                     config.successCallback.call(callbackScope, response, config)
                  }
                  else
                  {
                     _this.defaultSuccessCallback(response, config);
                  }
                  
               }, function(response){
                  
                  // HANDLE FAILURE...
                  
                  if (typeof response == "string")
                  {
                     response = JSON.parse(_this.cleanupJSONResponse(response));
                  }
                  if (typeof config.failureCallback == "function")
                  {
                     var callbackScope = (config.failureCallbackScope ? config.failureCallbackScope : (config.callbackScope ? config.callbackScope : _this)); 
                     config.failureCallback.call(callbackScope, response, config)
                  }
                  else
                  {
                     _this.defaultFailureCallback(response, config);
                  }

               }, function(response){
                  
                  // HANDLE PROGRESS...
                  
                  if (typeof response == "string")
                  {
                     response = JSON.parse(_this.cleanupJSONResponse(response));
                  }
                  if (typeof config.progressCallback == "function")
                  {
                     var callbackScope = (config.progressCallbackScope ? config.progressCallbackScope : (config.callbackScope ? config.callbackScope : _this)); 
                     config.progressCallback.call(callbackScope, response, config)
                  }
                  else
                  {
                     _this.defaultProgressCallback(response, config);
                  }
               });
            }
         }
         else
         {
            this.alfLog("error", "A request was made to perform an XHR request, but no configuration for the request was provided");
         }
      },
      
      /**
       * This is the default success callback for XHR requests that will be used if no other is provided.
       * 
       * @instance
       * @param {object} response The object returned from the successful XHR request
       * @param {object} requestConfig The original configuration passed when the request was made
       */
      defaultSuccessCallback: function alfresco_core_Core__defaultSuccessCallback(response, requestConfig) {
         this.alfLog("log", "[DEFAULT CALLBACK] The following successful response was received", response, requestConfig);
         if (requestConfig.alfTopic)
         {
            this.alfPublish(requestConfig.alfTopic + "_SUCCESS", {
               requestConfig: requestConfig,
               response: response
            });
         }
      },
      
      /**
       * This is the default failure callback for XHR requests that will be used if no other is provided.
       * 
       * @instance
       * @param {object} response The object returned from the failed XHR request
       * @param {object} requestConfig The original configuration passed when the request was made
       */
      defaultFailureCallback: function alfresco_core_Core__defaultFailureCallback(response, requestConfig) {
         this.alfLog("log", "[DEFAULT CALLBACK] The following failure response was received", response, requestConfig);
         if (requestConfig.alfTopic)
         {
            this.alfPublish(requestConfig.alfTopic + "_FAILURE", {
               requestConfig: requestConfig,
               response: response
            });
         }
         if (typeof this.displayMessage === "function" && response.response.text)
         {
            try
            {
               var responseObj = JSON.parse(response.response.text);
               if (responseObj.message)
               {
                  var msg = responseObj.message;
                  // generic exception message (from standard error template for REST APIs)
                  // attempt to strip out message text if in standard format:
                  // "org.alfresco.package.SpecificException: 12345678 Rest Of Message"
                  // - give up and display all if not
                  var eIndex = msg.indexOf("Exception: ");
                  if (eIndex !== -1 && msg.length > eIndex + 20)
                  {
                     msg = msg.substring(eIndex + 20);
                  }
                  this.displayMessage(msg);
               }
            }
            catch (e)
            {
               // Ignore failures here. The parsing was a best effort to get a message.
            }
         }
      },
      
      /**
       * This is the default progress callback for XHR requests that will be used if no other is provided.
       * 
       * @instance
       * @param {object} response The object returned from the progress update of the XHR request
       * @param {object} requestConfig The original configuration passed when the request was made
       */
      defaultProgressCallback: function alfresco_core_Core__defaultProgressCallback(response, requestConfig) {
         this.alfLog("log", "[DEFAULT CALLBACK] The following progress response was received", response, requestConfig);
         if (requestConfig.alfTopic)
         {
            this.alfPublish(requestConfig.alfTopic + "_PROGRESS", {
               requestConfig: requestConfig,
               response: response
            });
         }
      }
   });
});