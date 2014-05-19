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
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!expect",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon"], 
        function (registerSuite, expect, assert, require, TestCommon) {

   registerSuite({
      name: 'Property Test',
      'alfresco/renderers/Property': function () {

         var browser = this.remote;
         var testname = "PropertyTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/renderers/page_models/Property_TestPage.json", testname)

         .end()

         .elementByCss("#BASIC .value")
            .text()
            .then(function(resultText) {
               assert(resultText == "Test", "Test #1- Standard property not rendered correctly: " + resultText);
            })
            .end()

         .elementByCss("#PREFIX_SUFFIX .value")
            .text()
            .then(function(resultText) {
               assert(resultText == "(Test)", "Test #2- Prefix and suffix not rendered correctly: " + resultText);
            })
            .end()

         .elementByCss("#NEW_LINE")
            .getComputedCss("display")
            .then(function(result) {
               assert(result == "block", "Test #3 - New line not applied");
            })
            .end()

         .elementByCss("#WARN1 .value")
            .text()
            .then(function(resultText) {
               assert(resultText == "No property for: \"missing\"", "Test #4- Standard warning not rendered correctly: " + resultText);
            })
            .end()

         .elementByCss("#WARN2 .value")
            .text()
            .then(function(resultText) {
               assert(resultText == "No description", "Test #5 - Explicit warning not rendered correctly: " + resultText);
            })
            .end()

         .elementByCss("#HOVER .inner")
            .getComputedCss("display")
            .then(function(result) {
               assert(result == "none", "Test #6a - Hover displayed unexpectedly");
            })
            .end()
         .elementByCss("#LIST_ITEMS tr")
            .moveTo()
            .end()
         .elementByCss("#HOVER .value")
            .text()
            .then(function(resultText) {
               assert(resultText == "Test", "Test #6b - Hover not displayed: " + resultText);
            })
            .end()

         .elementByCss("#LABEL .label")
            .text()
            .then(function(resultText) {
               assert(resultText == "Label:", "Test #7- Label not rendered correctly: " + resultText);
            })
            .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});