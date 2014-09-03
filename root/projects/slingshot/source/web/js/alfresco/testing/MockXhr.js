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
 *
 * @module alfresco/testing/MockXhr
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/MockXhr.html",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "dojo/dom-construct",
        "dojo/aspect",
        "sinon/sinon/match",
        "sinon/sinon/test_case",
        "sinon/sinon/test",
        "sinon/sinon/sandbox",
        "sinon/sinon/assert",
        "sinon/sinon/collection",
        "sinon/sinon/mock",
        "sinon/sinon/stub",
        "sinon/sinon/behavior",
        "sinon/sinon/call",
        "sinon/sinon/spy",
        "sinon/sinon",
        "sinon/sinon-server-1.10.3"], 
        function(declare, _WidgetBase, _TemplatedMixin, template, AlfCore, lang, domConstruct, aspect) {
   
   return declare([ _WidgetBase, _TemplatedMixin, AlfCore], {

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/MockXhr.css"}]
       */
      cssRequirements: [{cssFile:"./css/MockXhr.css"}],

      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,

      /**
       * Sets up the Sinon fake server.
       * 
       * @instance
       */
      constructor: function alfresco_testing_MockXhr__constructor(args) {
         lang.mixin(this, args);

         // Set-up a fake server to handle all the responses...
         this.server = sinon.fakeServer.create();
         this.server.autoRespond = true;
         this.setupServer();

         // Capture each request and log it...
         var requests = this.requests = [];
         aspect.before(this.server, "handleRequest", lang.hitch(this, this.updateLog));

         // require(["sinon/sinon/match",
         //          "sinon/sinon/test_case",
         //          "sinon/sinon/test",
         //          "sinon/sinon/sandbox",
         //          "sinon/sinon/assert",
         //          "sinon/sinon/collection",
         //          "sinon/sinon/mock",
         //          "sinon/sinon/stub",
         //          "sinon/sinon/behavior",
         //          "sinon/sinon/call",
         //          "sinon/sinon/spy",
         //          "sinon/sinon",
         //          "sinon/sinon-server-1.10.3"], function () {

         //    // Create a server to handle responses...
         //    _this.server = sinon.fakeServer.create();
         //    _this.server.autoRespond = true;
         //    _this.setupServer();

         //    // Capture each request and log it...
         //    var requests = _this.requests = [];
         //    aspect.before(_this.server, "handleRequest", lang.hitch(_this, _this.updateLog));
         // });
      },

      /**
       * This sets up the fake server with all the responses it should provide.
       *
       * @instance
       */
      setupServer: function alfresco_testing_MockXhr__setupServer() {
         try
         {
            this.server.respondWith('{"showAddFavourite": false, "showRemoveFavourite": false, "widgetsRecent": [{"id": "ALF_RECENT_SITE___site2", "config": {"id": "HEADER_SITES_MENU_RECENT_site2", "siteShortName": "site2", "siteRole": "SiteManager", "label": "Site2", "iconClass": "alf-recent-site-icon", "targetUrl": "site\/site2\/dashboard"}, "name": "alfresco\/header\/AlfMenuItem"}, {"id": "ALF_RECENT_SITE___site1", "config": {"id": "HEADER_SITES_MENU_RECENT_site1", "siteShortName": "site1", "siteRole": "SiteManager", "label": "Site1", "iconClass": "alf-recent-site-icon", "targetUrl": "site\/site1\/dashboard"}, "name": "alfresco\/header\/AlfMenuItem"}, {"id": "ALF_RECENT_SITE___swsdp", "config": {"id": "HEADER_SITES_MENU_RECENT_swsdp", "siteShortName": "swsdp", "siteRole": "SiteManager", "label": "Sample: Web Site Design Project", "iconClass": "alf-recent-site-icon", "targetUrl": "site\/swsdp\/dashboard"}, "name": "alfresco\/header\/AlfMenuItem"}, {"id": "ALF_RECENT_SITE___photos", "config": {"id": "HEADER_SITES_MENU_RECENT_photos", "siteShortName": "photos", "siteRole": "SiteManager", "label": "Photos", "iconClass": "alf-recent-site-icon", "targetUrl": "site\/photos\/dashboard"}, "name": "alfresco\/header\/AlfMenuItem"}]}');
         }
         catch(e)
         {
            this.alfLog("error", "The following error occurred setting up the mock server", e);
         }
      },

      /**
       * Adds the details of each XHR request to the log so that it can be queried by a unit test to 
       * check that services are making appropriate requests for data.
       * 
       * @instance
       * @param {object} xhrRequest The XHR request that was made
       */
      updateLog: function alfresco_testing_MockXhr__updateLog(xhrRequest) {
         var rowNode = domConstruct.create("tr", {
            className: "mx-row"
         }, this.logNode);
         var methodNode = domConstruct.create("td", {
            className: "mx-method",
            innerHTML: xhrRequest.method
         }, rowNode);
         var urlNode = domConstruct.create("td", {
            className: "mx-url",
            innerHTML: xhrRequest.url
         }, rowNode);

         var payloadNode = domConstruct.create("td", {
            className: "mx-payload",
            innerHTML: (xhrRequest.requestBody != null) ? xhrRequest.requestBody : ""
         }, rowNode);
      }
   });
});
