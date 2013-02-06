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
       * @method cleanupJSONResponse
       * @param {string} input
       * @returns {string} A cleaned up JSON response.
       */
      cleanupJSONResponse: function alfresco_core_Core__cleanupJSONResponse(input) {
         var r = input;
         if (typeof input == "string")
         {
            r = input.replace(/\s/g, "").replace(/,}/g, "}");
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
       * @method serviceXhr
       * @param {object} config
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
               xhr(config.url, {
                  handleAs: "text",
                  method: (config.method) ? config.method : "POST",
                  data: (config.data) ? JSON.stringify(config.data) : null,
                  headers: (config.headers) ? config.headers : { 'Content-Type': 'application/json' }
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
       * @method defaultSuccessCallback
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
       * @method defaultFailureCallback
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
                  this.displayPrompt(responseObj.message);
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
       * @method defaultProgressCallback
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