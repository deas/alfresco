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
 * This extends the [basic list]{@link module:alfresco/lists/AlfList} to provide support
 * for reloading data based on relevant changes to the browser URL hash. The hash parameters that
 * are relevant (i.e. that when changed should cause the data to be reloaded) should be defined 
 * in the [hashVarsForUpdate attribute]{@link module:alfresco/lists/AlfHashList#hashVarsForUpdate}.
 * 
 * @module alfresco/lists/AlfHashList
 * @extends module:alfresco/lists/AlfList
 * @mixes module:alfresco/documentlibrary/_AlfHashMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/lists/AlfList", 
        "alfresco/documentlibrary/_AlfHashMixin",
        "dojo/_base/lang",
        "dojo/hash",
        "dojo/io-query"], 
        function(declare, AlfList, _AlfHashMixin, lang, hash, ioQuery) {
   
   return declare([AlfList, _AlfHashMixin], {
      
      /**
       * Indicates whether the location should be driven by changes to the browser URL hash
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      useHash: false,

      /**
       * An array of hash variables that when changed will trigger a reload
       *
       * @instance
       * @type {array}
       * @default []
       */
      hashVarsForUpdate: [],

      /**
       * The AlfHashList is intended to work co-operatively with other widgets on a page to assist with
       * setting the data that should be retrieved. As related widgets are created and publish their initial
       * state they may trigger requests to load data. As such, data loading should not be started until
       * all the widgets on the page are ready. This function extends the [inherited function]{@link module:alfresco/lists/AlfList#onPageWidgetsReady}
       * to honour the [useHash configuration]{@link module:alfresco/lists/AlfList#useHash} and load data
       * appropriately.
       *
       * @instance
       * @param {object} payload
       */
      onPageWidgetsReady: function alfresco_lists_AlfHashList__onPageWidgetsReady(payload) {
         // Remove the subscription to ensure it's only processed once...
         this.alfUnsubscribe(this.pageWidgetsReadySubcription);

         this._readyToLoad = true;
         if (this.useHash)
         {
            // Only subscribe to filter changes if 'useHash' is set to true. This is because multiple DocLists might
            // be required on the same page and they can't all feed off the hash to drive the location.
            this.alfSubscribe(this.hashChangeTopic, lang.hitch(this, this.onHashChanged));

            var currHash = ioQuery.queryToObject(hash());
            if(!this._payloadContainsUpdateableVar(currHash))
            {
               this.alfPublish("ALF_NAVIGATE_TO_PAGE", {
                  url: ioQuery.objectToQuery(this.currentFilter),
                  type: "HASH"
               }, true);
            }
            else
            {
               // When using hashes (e.g. a URL fragment in the browser address bar) then we need to 
               // actually get the initial filter and use it to generate the first data set...
               this.initialiseFilter(); // Function provided by the _AlfHashMixin
            }
         }
         else
         {
            // When not using a URL hash (e.g. because this DocList is being used as a secondary item - 
            // maybe as part of a picker, etc) then we need to load the initial data set using the instance
            // variables provided. We also need to subscribe to topics that indicate that the location has 
            // changed. Each view renderer that registers a link will need to set a "linkClickTopic" and this
            // should be matched by the "linkClickTopic" of this instance)
            this.alfSubscribe(this.linkClickTopic, lang.hitch(this, "onItemLinkClick"));
            if (this._readyToLoad) this.loadData();
         }
      },

      /**
       * Compares the payload object with the hashVarsForUpdate array of key names
       * Returns true if hashVarsForUpdate is empty
       * Returns true if the payload contains a key that is specified in hashVarsForUpdate
       * Returns false otherwise
       *
       * @instance
       * @param {object} payload The payload object
       * @return {boolean}
       */
      _payloadContainsUpdateableVar: function alfresco_lists_AlfHashList___payloadContainsUpdateableVar(payload) {
         
         // No hashVarsForUpdate - return true
         if(this.hashVarsForUpdate == null || this.hashVarsForUpdate.length == 0)
         {
            return true;
         }
         
         // Iterate over the keys defined in hashVarsForUpdate - return true if the payload contains one of them
         for(var i=0; i < this.hashVarsForUpdate.length; i++)
         {
            if(this.hashVarsForUpdate[i] in payload)
            {
               return true;
            }
         }
         return false;
      },

      /**
       * This function is called whenever the browser URL hash fragment is changed.e end of the fragment
       * 
       * @instance
       * @param {object} payload
       */
      onHashChanged: function alfresco_lists_AlfHashList__onHashChanged(payload) {
         if(this._payloadContainsUpdateableVar(payload))
         {
            //this.currentFilter = payload;
            if (this._readyToLoad) this.loadData();
         }
      }
   });
});