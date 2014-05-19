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
 * @module alfresco/testing/mockservices/SearchTestService
 * @extends module:alfresco/core/Core
 * @author David.Webster@alfresco.com
 */
define(["dojo/_base/declare",
   "alfresco/core/Core",
   "service/constants/Default",
   "dojo/_base/lang",
   "dojo/json",
   "dojo/text!./responseTemplates/SearchTest/FirstRequest.json",
   "dojo/text!./responseTemplates/SearchTest/SecondRequest.json",
   "dojo/text!./responseTemplates/SearchTest/ThirdRequest.json",
   "dojo/text!./responseTemplates/SearchTest/FacetedRequest.json"],
   function(declare, AlfCore, AlfConstants, lang, dojoJson, firstRequest, secondRequest, thirdRequest, facetedRequest) {

      return declare([AlfCore], {

         /**
          * Use this to determine if the service has already been called.
          *
          */
         queryCount: 0,

         /**
          *
          *
          * @instance
          * @param {array} args The constructor arguments.
          */
         constructor: function alfresco_testing_mockservices_SearchTestService__constructor(args) {
            lang.mixin(this, args);
            this.alfSubscribe("ALF_SEARCH_REQUEST", lang.hitch(this, "onSearchRequest"));
         },

         /**
          * @instance
          */
         onSearchRequest: function alfresco_testing_mockservices_SearchTestService__onSearchRequest(payload) {
            var alfTopic = (payload.alfResponseTopic != null) ? payload.alfResponseTopic : "ALF_SEARCH_REQUEST",
               response = firstRequest;

            // Update the query Count (including on first run)
            ++this.queryCount;

            // Switch and return the correct request for these results.
            switch (this.queryCount) {
               case 2:
                  response = secondRequest;
                  break;
               case 3:
                  response = thirdRequest;
                  break;
               case 4:
                  response = facetedRequest;
            }

            // Publish the normal topic with mocked response in the payload
            this.alfPublish(alfTopic, {
               response: dojoJson.parse(response)
            });
         }
      });
   });
