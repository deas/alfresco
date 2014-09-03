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
        "alfresco/TestCommon"], 
        function (registerSuite, expect, assert, require, TestCommon) {

   registerSuite({
      name: 'Property Test',
      'alfresco/renderers/Property': function () {

         var browser = this.remote;
         var testname = "PropertyTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/renderers/page_models/Property_TestPage.json", testname)

         .end()

         .findByCssSelector("#BASIC .value")
         .getVisibleText()
         .then(function(resultText) {
            TestCommon.log(testname,43,"Check standard property is rendered correctly");
            assert(resultText == "Test", "Standard property not rendered correctly: " + resultText);
         })
         .end()

         .findByCssSelector("#PREFIX_SUFFIX .value")
         .getVisibleText()
         .then(function(resultText) {
            TestCommon.log(testname,51,"Check prefixed/suffixed property is rendered correctly");
            assert(resultText == "(Test)", "Prefix and suffix not rendered correctly: " + resultText);
         })
         .end()

         .findByCssSelector("#NEW_LINE")
         .getComputedStyle("display")
         .then(function(result) {
            TestCommon.log(testname,59,"Check new line property is rendered correctly");
            assert(result == "block", "New line not applied");
         })
         .end()

         .findByCssSelector("#WARN1 .value")
         .getVisibleText()
         .then(function(resultText) {
            TestCommon.log(testname,67,"Check standard warning is rendered correctly");
            assert(resultText == "No property for: \"missing\"", "Standard warning not rendered correctly: " + resultText);
         })
         .end()

         .findByCssSelector("#WARN2 .value")
         .getVisibleText()
         .then(function(resultText) {
            TestCommon.log(testname,75,"Check explicit warning is rendered correctly");
            assert(resultText == "No description", "Explicit warning not rendered correctly: " + resultText);
         })
         .end()

         .findByCssSelector("#HOVER .inner")
         .getComputedStyle("visibility")
         .then(function(result) {
            TestCommon.log(testname,83,"Check hover property is hidden");
            assert(result == "hidden", "Hover displayed unexpectedly");
         })
         .end()

         .findByCssSelector("#LIST_ITEMS tr")
         .end()

         .findByCssSelector("#HOVER .value")
         .getVisibleText()
         .then(function(resultText) {
            TestCommon.log(testname,95,"Check hover property is shown on hover");
            assert(resultText == "Test", "Hover not displayed: " + resultText);
         })
         .end()

         .findByCssSelector("#LABEL .label")
         .getVisibleText()
         .then(function(resultText) {
            TestCommon.log(testname,103,"Check label is rendered correctly");
            assert(resultText == "Label:", "Label not rendered correctly: " + resultText);
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});