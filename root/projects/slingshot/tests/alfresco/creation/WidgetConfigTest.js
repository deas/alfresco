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
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!expect",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!leadfoot/Command"], 
        function (registerSuite, expect, assert, require, TestCommon, Command) {

   registerSuite({
      name: 'Page Creator Test',
      'PageCreator1': function () {

         // var command = new Command(session);
         var browser = this.remote;

         var testname = "PageCreator1";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/creation/page_models/WidgetConfig_TestPage.json", testname)

         .findByCssSelector("#dojoUnique1 > .title")
            .then(function(element) {
               browser.moveMouseTo(element)
            })
            .pressMouseButton()
            .moveMouseTo(null, 1, 1)
            .end()

         .findByCssSelector(".alfresco-creation-DropZone > div")
            .then(function(element) {
               browser.moveMouseTo(element)
            })
            .releaseMouseButton()
            .sleep(5000)
            .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});