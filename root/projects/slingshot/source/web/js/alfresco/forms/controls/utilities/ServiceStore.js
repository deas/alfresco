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
 * This module was written with the express purpose of working with the [ComboBox]{@link module:alfresco/forms/controls/ComboBox}
 * form control. It extends the Dojo JsonRest module to support queries over the Aikau publication/subscription
 * communication layer (rather than by direct XHR request).
 * 
 * @module alfresco/forms/controls/ServiceStore
 * @extends dojo/store/JsonRest
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dojo/store/JsonRest",
        "alfresco/core/Core",
        "dojo/Deferred",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/store/util/QueryResults",
        "dojo/store/util/SimpleQueryEngine"], 
        function(declare, JsonRest, AlfCore, Deferred, lang, array, QueryResults, SimpleQueryEngine) {

   return declare([JsonRest, AlfCore], {

      /**
       * This is the topic to publish to get the options for.
       *
       * @instance
       * @type {string}
       * @default null
       */
      publishTopic: null,

      /**
       * The payload to publish on the [publishTopic]{@link module:alfresco/forms/controls/utilities/ServiceStore#publishTopic}
       * to assist with retrieving data.
       *
       * @instance
       * @type {object}
       * @default null
       */
      publishPayload: null,

      /**
       * This is the attribute to use when querying the result data for matching items. This is set to 
       * "name" by default but can be overridden. When used by a [form control]{@link module:alfresco/forms/controls/BaseFormControl}
       * it would be expected that this would be set to be the [name attribute]{@link module:alfresco/forms/controls/BaseFormControl#name}
       * of that form control.
       *
       * @instance
       * @type {string}
       * @default "name"
       */
      queryAttribute: "name",

      /**
       * Overrides the inherited function from the JsonRest store to make a request for data by publishing a request
       * on a specific topic. This returns a Deferred object which is resolved by the 
       * [onOptions]{@link module:alfresco/forms/controls/utilities/ServiceStore#onOptions} function.
       *
       * @instance
       * @param {object} query The query to use for retrieving objects from the store.
       * @param {object} options The optional arguments to apply to the resultset.
       * @returns {object} The results of the query, extended with iterative methods.
       */
      query: function alfresco_forms_controls_utilities_ServiceStore__query(query, options){
         var response = null;
         if (this.publishTopic != null)
         {
            response = new Deferred();
            var responseTopic = this.generateUuid();
            var payload = this.publishPayload;
            if (payload == null)
            {
               payload = {};
            }
            payload.alfResponseTopic = responseTopic;

            // Set up a dot-notation address to retrieve the results from, this will be set to response if not included
            // in the payload...
            var resultsProperty = (this.publishPayload.resultsProperty != null) ? this.publishPayload.resultsProperty : "response";

            // Add in an additional query attribute. Some services (e.g. the TagService) will use this as an additional
            // search term request parameter...
            payload.query = query[(this.queryAttribute != null) ? this.queryAttribute : "name"];

            this._optionsHandle = this.alfSubscribe(responseTopic + "_SUCCESS", lang.hitch(this, "onOptions", response, query, resultsProperty), true);
            this.alfPublish(this.publishTopic, payload, true);
         }
         else
         {
            this.alfLog("warn", "A ServiceStore was set up without a 'publishTopic' to use to retrieve options", this);
            response = {};
         }
         return response;
      },

      /**
       * This is hitched to a generated topic subscription that is published when the target service has retrieved
       * the requested data. It performs a query on the data provided to generate the result set.
       *
       * @instance
       * @param {obejct} dfd The deferred object to resolve.
       * @param {object} query The requested query data.
       * @param {string} resultsProperty A dot-notation address in the payload that should contain the list of options.
       * @param {object} payload The options to use
       */
      onOptions: function alfresco_forms_controls_utilities_ServiceStore__onOptions(dfd, query, resultsProperty, payload) {
         this.alfUnsubscribeSaveHandles([this._optionsHandle]);
         var results = lang.getObject(resultsProperty, false, payload);
         if (results != null)
         {
            // Get the list of options from the payload...
            var queryAttribute = (this.queryAttribute != null) ? this.queryAttribute : "name";
            
            // Check that all the data is valid, this is done to ensure any data sets that don't contain all the data...
            // This is a workaround for an issue with the Dojo query engine that will break when an item doesn't contain
            // the query attribute...
            array.forEach(results, function(item, i) {
               if (item[queryAttribute] == null)
               {
                  item[queryAttribute] = "";
               }
            });

            var updatedQuery = {};
            updatedQuery[this.queryAttribute] = new RegExp("^" + query[this.queryAttribute].toString() + ".*$");

            // NOTE: Ignore JSHint warnings on the following 2 lines...
            var queryEngine = SimpleQueryEngine(updatedQuery);
            var queriedResults = QueryResults(queryEngine(results));
            dfd.resolve(queriedResults);
         }
         else
         {
            this.alfLog("warn", "No '" + resultsProperty + "' attribute published in payload for the query options", payload, this);
            dfd.resolve([]);
         }
      }
   });
});