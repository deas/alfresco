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
        "dojo/_base/lang"], 
        function(declare, lang) {
   
   return declare([], {
      /**
       *
       * @instance
       */
      constructor: function alfresco_testing_MockXhr__constructor(args) {
         lang.mixin(this, args);

         var _this = this;
         var oldRequire = require;
         require(["sinon/sinon/match",
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
                  "sinon/sinon-server-1.10.3"], function () {
            require = oldRequire;
            _this.xhr = sinon.useFakeXMLHttpRequest();
            var requests = _this.requests = [];

            _this.xhr.onCreate = function (xhr) {
               requests.push(xhr);
            };
         });
      }
   });
});
