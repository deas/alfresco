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
 * @author Richard Smith
 */
define(["intern!object",
        "intern/chai!expect",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, expect, require, TestCommon, keys) {

   registerSuite({
      name: 'PublishPayloadMixinOnActions Test',
      'alfresco/renderers/_PublishPayloadMixin': function () {

         var browser = this.remote;
         var testname = "PublishPayloadMixinOnActionsTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/renderers/page_models/PublishPayloadMixinOnActions_TestPage.json", testname)

         .end()

         // Check there are 3 action menus as described in the model
         .findAllByCssSelector("div.alfresco-menus-AlfMenuBar")
         .then(function (actionmenus) {
            TestCommon.log(testname,43,"Check there are 3 action menus as described in the model");
            expect(actionmenus).to.have.length(3, "There should be 3 action menus rendered");
         })
         .end()

         // Open the first action menu by mouse click
         .findByCssSelector("div.dijitMenuItemLabel:nth-of-type(1)")
         .sleep(500)
         .click()
         .sleep(500)
         .hasElementByCss(".dijitPopup")
         .then(function(result1) {
            TestCommon.log(testname,56,"Open the action menu by mouse click");
            expect(result1).to.equal(true, "The action menu did not appear on mouse clicks");
         })
         .end()

         // Check the menu has appeared
         .findByCssSelector(".dijitPopup")
         .isDisplayed()
         .then(function(result2) {
            TestCommon.log(testname,65,"Check the action menu has appeared");
            expect(result2).to.equal(true, "The action menu should be visible on mouse clicks");
          })
         .end()

         // Click the first button in the action menu
         .findByCssSelector(".dijitPopup .dijitMenuItem:nth-of-type(1) .dijitMenuItemLabel")
         .sleep(500)
         .click()
         .sleep(500)
         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "alfTopic", "DELETE_ACTION_TOPIC"))
         .then(function(result3) {
            TestCommon.log(testname,78,"Check the action selection published as expected after mouse clicks");
            expect(result3).to.equal(true, "The action menu did not publish on 'DELETE_ACTION_TOPIC' after mouse clicks");
         })
         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "variable1", "red"))
         .then(function(result4) {
            TestCommon.log(testname,83,"Check the action selection published the payload as expected after mouse clicks");
            expect(result4).to.equal(true, "The action menu did not publish the payload with 'variable1' as 'red' after mouse clicks");
         })
         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "variable2", "orange"))
         .then(function(result5) {
            TestCommon.log(testname,88,"Check the action selection published the payload as expected after mouse clicks");
            expect(result5).to.equal(true, "The action menu did not publish the payload with 'variable2' as 'orange' after mouse clicks");
         })
         .end()

         // Open the first action menu again
         .findByCssSelector("div.dijitMenuItemLabel:nth-of-type(1)")
         .sleep(500)
         .click()
         .end()

         // Click the second button in the action menu
         .findByCssSelector(".dijitPopup .dijitMenuItem:nth-of-type(2) .dijitMenuItemLabel")
         .sleep(500)
         .click()
         .sleep(500)
         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "alfTopic", "MANAGE_ACTION_TOPIC"))
         .then(function(result6) {
            TestCommon.log(testname,108,"Check the action selection published as expected after mouse clicks");
            expect(result6).to.equal(true, "The action menu did not publish on 'MANAGE_ACTION_TOPIC' after mouse clicks");
         })
         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "payloadVariable1", "orange"))
         .then(function(result7) {
            TestCommon.log(testname,113,"Check the action selection published the payload as expected after mouse clicks");
            expect(result7).to.equal(true, "The action menu did not publish the payload with 'payloadVariable1' as 'red' after mouse clicks");
         })
         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "payloadVariable2", "red"))
         .then(function(result8) {
            TestCommon.log(testname,118,"Check the action selection published the payload as expected after mouse clicks");
            expect(result8).to.equal(true, "The action menu did not publish the payload with 'payloadVariable2' as 'orange' after mouse clicks");
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});