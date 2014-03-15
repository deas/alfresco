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
        "require",
        "alfresco/TestCommon"], 
        function (registerSuite, expect, require, TestCommon) {

   registerSuite({
      name: 'AlfTooltip Test',
      'alfresco/misc/AlfTooltip': function () {

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/misc/page_models/AlfTooltip_TestPage.json")

         .end()

         // Does the test button exist?
         .elementById("TEST_BUTTON")
         .then(function(el1) {
            expect(el1).to.be.an("object", "The Test Button could not be found");
         })
         .end()

         // Tool tip should be missing to being with
         .hasElementByCss(".dijitTooltip")
         .then(function(result1) {
            expect(result1).to.equal(false, "The Tooltip should not be available yet");
         })
         .end()

         // Move to test button - does the tool tip appear?
         .elementById("TEST_BUTTON")
         .moveTo()
         .sleep(250)
         .hasElementByCss(".dijitTooltip")
         .then(function(result2) {
            expect(result2).to.equal(true, "The Tooltip did not appear");
         })
         .end()

         // Does the tool tip contain the appropriate copy
         .elementByCss(".dijitTooltipContainer.dijitTooltipContents")
         .text()
         .then(function(resultText1) {
            expect(resultText1).to.equal("This is the test button", "The tool tip text is incorrect");
         })
         .end()

         // Move to test button two - does the tool tip disappear?
         .elementById("TEST_BUTTON_TWO")
         .moveTo()
         .sleep(250)
         .hasElementByCss(".dijitTooltip")
         .then(function(result3) {
            expect(result3).to.equal(true, "The Tooltip code should not have disappeared");
         })
         .end()

         .elementByCss(".dijitTooltip")
         .isDisplayed()
         .then(function(result4) {
            expect(result4).to.equal(false, "The Tooltip should be hidden");
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});