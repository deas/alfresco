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
      name: 'Date Test',
      'alfresco/renderers/Date': function () {

         var browser = this.remote;
         var testname = "DateTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/renderers/page_models/Date_TestPage.json", testname)

         .end()

         // Test that the dates are rendered as expected. The model uses a very old ISO date which should ensure
         // that we get a relative date in the form "Modified over X years ago" so we're going to use a regular
         // expression that should continue to work in the future as the date gets further into the past
         .elementByCss("#CUSTOM_PROPS .value")
         .text()
         .then(function(resultText) {
            TestCommon.log(testname,46,"Check custom property is rendered correctly");
            assert(/(Modified over \d+ years ago by Brian Griffin)/g.test(resultText), "Custom property not rendered correctly: " + resultText);
         })
         .end()

         .elementByCss("#STANDARD_PROPS .value")
         .text()
         .then(function(resultText) {
            TestCommon.log(testname,54,"Check standard property is rendered correctly");
            assert(/(Modified over \d+ years ago by Chris Griffin)/g.test(resultText), "Standard property not rendered correctly: " + resultText);
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});