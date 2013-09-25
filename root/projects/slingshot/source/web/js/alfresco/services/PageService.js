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
 * @mixes module:alfresco/services/_PageServiceTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "alfresco/services/_PageServiceTopicMixin",
        "alfresco/core/NotificationUtils",
        "dojo/request/xhr",
        "dojo/_base/lang"],
        function(declare, AlfCore, CoreXhr, _PageServiceTopicMixin, NotificationUtils, xhr, lang) {
   
   return declare([AlfCore, CoreXhr, _PageServiceTopicMixin, NotificationUtils], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/ActionService.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/PageService.properties"}],
      
      /**
       * Sets up the subscriptions for the PageService
       * 
       * @instance
       * @param {array} args Constructor arguments
       */
      constructor: function alfresco_services_PageService__constructor(args) {
         lang.mixin(this, args);
         this.alfSubscribe(this.availablePagesRequestTopic, lang.hitch(this, "loadPages"));
         this.alfSubscribe(this.createPageTopic, lang.hitch(this, "createPage"));
         this.alfSubscribe(this.updatePageTopic, lang.hitch(this, "updatePage"));
      },
      
      /**
       * Makes an XHR request to retrieve the pages that are available. The pages returned are those
       * that have been created and stored in the Data Dictionary on the Alfresco repository. Having 
       * retrieved the available pages it will publish on the "AvailablePages" topic.
       * @instance
       */
      loadPages: function alfresco_services_PageService__loadPages() {
         this.serviceXhr({
            url: Alfresco.constants.PROXY_URI + "/remote-share/pages",
            method: "GET",
            successCallback: this.pageCreateSuccess,
            failureCallback: this.pageCreateFailure,
            callbackScope: this
         });
      },
      
      /**
       * @instance
       * @param {object} response
       * @param {object} originalRequestConfig
       */
      loadPagesSuccess: function alfresco_services_PageService__loadPagesSuccess(response, originalRequestConfig) {
         this.alfPublish(this.availablePagesTopic, {
            response: response,
            originalRequestConfig: originalRequestConfig
         });
      },
      
      /**
       * @instance
       * @param {object} response
       * @param {object} originalRequestConfig
       */
      loadPagesFailure: function alfresco_services_PageService__loadPagesFailure(response, originalRequestConfig) {
         this.availablePagesLoadFailure(this.availablePagesTopic, {
            response: response,
            originalRequestConfig: originalRequestConfig
         });
      },
      
      /**
       * 
       * @instance
       * @param {object} payload The details of the page to create
       */
      createPage: function alfresco_services_PageService__createPage(payload) {
         if (payload != null && 
             payload.pageDefinition != null &&
             payload.pageName != null)
         {
            var data = {
               name: payload.pageName,
               json: payload.pageDefinition
            };
            this.serviceXhr({
               url : Alfresco.constants.PROXY_URI + "remote-share/page-definition",
               data: data,
               method: "POST",
               successCallback: this.pageCreateSuccess,
               failureCallback: this.pageCreateFailure,
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to save a page definition to the repository but not enough information was provided", payload);
         }
      },
      
      /**
       * @instance
       * @param {object} response
       * @param {object} originalRequestConfig
       */
      pageCreateSuccess: function alfresco_prototyping_ScratchPad__pageCreateSuccess(response, originalRequestConfig) {
         this.alfLog("log", "Successfully created page", response, originalRequestConfig);
         this.displayMessage(this.message("page.creation.success", [originalRequestConfig.data.name]));
         this.alfPublish(this.createPageSuccessTopic, {
            response: response,
            originalRequestConfig: originalRequestConfig
         });
      },
      
      /**
       * @instance
       * @param {object} response
       * @param {object} originalRequestConfig
       */
      pageCreateFailure: function alfresco_prototyping_ScratchPad__pageCreateFailure(response, originalRequestConfig) {
         this.alfLog("error", "Failed to create page", response, originalRequestConfig);
         this.displayMessage(this.message("page.creation.failure", [originalRequestConfig.data.name]));
         this.alfPublish(this.createPageFailureTopic, {
            response: response,
            originalRequestConfig: originalRequestConfig
         });
      },
      
      /**
       * 
       * @instance
       * @param {object} payload The details of the page to update
       */
      updatePage: function alfresco_services_PageService__updatePage(payload) {
         if (payload != null && 
             payload.pageDefinition != null &&
             payload.pageName != null)
         {
            var data = {
                  name: payload.pageName,
                  json: payload.pageDefinition
            };
            this.serviceXhr({
               url : Alfresco.constants.PROXY_URI + "remote-share/page-definition/" + payload.pageName,
               data: data,
               method: "PUT",
               successCallback: this.pageUpdateSuccess,
               failureCallback: this.pageUpdateFailure,
               callbackScope: this
            });
         }
         else
         {
            this.alfLog("warn", "A request was made to update a page definition to the repository but not enough information was provided", payload);
         }
      },
      
      /**
       * @instance
       * @param {object} response
       * @param {object} originalRequestConfig
       */
      pageUpdateSuccess: function alfresco_prototyping_ScratchPad__pageUpdateSuccess(response, originalRequestConfig) {
         this.alfLog("log", "Successfully updated page", response, originalRequestConfig);
         this.displayMessage(this.message("page.update.success", [originalRequestConfig.data.name]));
         this.alfPublish(this.updatePageSuccessTopic, {
            response: response,
            originalRequestConfig: originalRequestConfig
         });
      },
      
      /**
       * @instance
       * @param {object} response
       * @param {object} originalRequestConfig
       */
      pageUpdateFailure: function alfresco_prototyping_ScratchPad__pageUpdateFailure(response, originalRequestConfig) {
         this.alfLog("error", "Failed to update page", response, originalRequestConfig);
         this.displayMessage(this.message("page.update.failure", [originalRequestConfig.data.name]));
         this.alfPublish(this.updatePageFailureTopic, {
            response: response,
            originalRequestConfig: originalRequestConfig
         });
      }
   });
});