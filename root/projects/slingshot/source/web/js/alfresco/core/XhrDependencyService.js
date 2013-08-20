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
 * This provides the capability to asynchronously request dependencies that were not initially loaded when the
 * page was loaded. It needs to be updated to make proper use of the functions provided by 
 * [CoreXhr]{@link module:alfresco/core/CoreXhr}. It also probably needs to be relocated to the "alfresco/services"
 * package. There is definitely outstanding work to be done with this module so should be used with caution.
 * 
 * @module alfresco/core/XhrDependencyService
 * @extends module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "dojo/json",
        "dojo/request/xhr",
        "dojo/_base/lang",
        "dojo/query",
        "dojo/NodeList-manipulate"], 
        function(declare, AlfCore, JSON, xhr, lang, query) {
   
   return declare([AlfCore], {

      /**
       * @instance
       * @param {Object} model
       */
      processXhrDependencies: function(model) {
         
         var _this = this;
         try
         {
            // The model MUST be valid JSON (e.g. with strings for keys, not a JavaScript object). If it is
            // not then the JSON parser will fail...
            var jsonContent = JSON.parse(model, true);
            
            // Use the XHR dependency WebScript to get the dependencies required for the requested Widgets...
            // We need to capture both the JavaScript, CSS and i18n dependencies.
            xhr(appContext + "/service/surf/dojo/xhr/dependencies", {
                handleAs: "json",
                query: {
                  widgets: model
               }
            }).then(function(data) {
               
               // Iterate over the CSS map and append a new <link> element into the <head> element to ensure that all the
               // widgets CSS dependencies are loaded... 
               for (var media in data.cssMap)
               {
                  // TODO: query for the node outside of the loop
                  // TODO: keep a reference to each node appended and then remove it when the preview is regenerated
                  query("head").append('<link rel="stylesheet" type="text/css" href="' + appContext + data.cssMap[media] + '" media="' + media + '">');
               }
               
               // Build in the i18n properties into the global object...
               for (var scope in data.i18nMap)
               {
                  if (typeof window[data.i18nGlobalObject].messages.scope[scope] == "undefined")
                  {
                     // If the scope hasn't already been used then we can just assign it directly...
                     window[data.i18nGlobalObject].messages.scope[scope] = data.i18nMap[scope];
                  }
                  else
                  {
                     // ...but if the scope already exists, then we need to mixin the new properties...
                     lang.mixin(window[data.i18nGlobalObject].messages.scope[scope], data.i18nMap[scope]);
                  }
               }
               
               // The data response will contain a MD5 referencing JavaScript resource that we should request that Dojo loads...
               var requires = [data.javaScript];
               require(requires, function() {
                  // Once the require request has completed we can process the requested widgets knowing that all of the
                  // dependent JavaScript files have been loaded...
                  _this.processWidgets(jsonContent.widgets, _this.previewNode);
               });
            }, function(error) {
               // Handle any errors...
               // TODO: We should output something sensible into the preview pane...
               _this.alfLog("error", "An error occurred requesting the XHR dependencies");
            });
         }
         catch(e)
         {
            this.alfLog("log", "An error occurred parsing the JSON", e);
         }
      }
   });
});