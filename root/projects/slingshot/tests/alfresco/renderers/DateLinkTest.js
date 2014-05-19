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
      name: 'DateLink Test',
      'alfresco/renderers/Date': function () {

         var browser = this.remote;
         var testname = "DateLinkTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/renderers/page_models/DateLink_TestPage.json", testname)

         .end()

         // Test that the dates are rendered as expected. The model uses a very old ISO date which should ensure
         // that we get a relative date in the form "Modified over X years ago" so we're going to use a regular
         // expression that should continue to work in the future as the date gets further into the past
         .elementByCss("#CUSTOM_PROPS .value")
         .text()
         .then(function(resultText) {
            TestCommon.log(testname,46,"Check the first date is rendered correctly");
            assert(/(Modified over \d+ years ago by Brian Griffin)/g.test(resultText), "Test #1 - Custom property not rendered correctly: " + resultText);
         })
         .end()

         .elementByCss("#STANDARD_PROPS .value")
         .text()
         .then(function(resultText) {
            TestCommon.log(testname,54,"Check the second date is rendered correctly");
            assert(/(Modified over \d+ years ago by Chris Griffin)/g.test(resultText), "Test #2 - Standard property not rendered correctly: " + resultText);
         })
         .end()

         // Click the first date
         .elementByCss("#CUSTOM_PROPS .value")
         .moveTo()
         .click()
         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "alfTopic", "ALF_NAVIGATE_TO_PAGE"))
         .then(function(result) {
            TestCommon.log(testname,65,"Check the date click published as expected");
            expect(result).to.equal(true, "The datelink did not publish on 'ALF_NAVIGATE_TO_PAGE' after mouse clicks");
         })
         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "type", "SHARE_PAGE_RELATIVE"))
         .then(function(result) {
            TestCommon.log(testname,70,"Check the date click published the payload as expected");
            expect(result).to.equal(true, "The datelink did not publish the payload with 'type' as 'SHARE_PAGE_RELATIVE'");
         })
         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "url", "/1/2/3/4/5"))
         .then(function(result) {
            TestCommon.log(testname,75,"Check the date click published the payload as expected");
            expect(result).to.equal(true, "The datelink did not publish the payload with 'url' as '/1/2/3/4/5'");
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});