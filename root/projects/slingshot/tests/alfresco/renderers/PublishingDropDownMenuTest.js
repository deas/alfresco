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
        "alfresco/TestCommon",
        "intern/dojo/node!wd/lib/special-keys"], 
        function (registerSuite, expect, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'PublishingDropDownMenu Test',
      'alfresco/renderers/PublishingDropDownMenu': function () {

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/renderers/page_models/PublishingDropDownMenu_TestPage.json")

         .end()

         // Check there are 3 drop down menus
         .elementsByCssSelector("div.alfresco-renderers-PublishingDropDownMenu")
         .then(function (dropdowns) {
            expect(dropdowns).to.have.length(3, "There should be 3 dropdown menus rendered");
         })
         .end()

         // Check the start value of a drop down menu
         .elementByCss("span.dijitSelectLabel:nth-of-type(1)")
         .text()
         .then(function (result1) {
            expect(result1).to.equal("Public", "The start value of dropdown menu 1 should be 'Public'");
         })
         .sleep(500)
         .end()

         // Open the menu
         .elementByCss("span.dijitSelectLabel:nth-of-type(1)")
         .moveTo()
         .sleep(500)
         .click()
         .sleep(500)
         .hasElementByCss(".dijitMenuPopup")
         .then(function(result2) {
            expect(result2).to.equal(true, "The drop down menu did not appear on mouse clicks");
         })
         .end()

         .elementByCss(".dijitMenuPopup")
         .isDisplayed()
         .then(function(result3) {
             expect(result3).to.equal(true, "The drop down menu should be visible after the mouse click");
          })
         .end()

         // Select a different item in the menu by mouse
         .elementByCss("tr.dijitMenuItem:nth-of-type(3)")
         .moveTo()
         .sleep(500)
         .click()
         .sleep(500)
         .hasElementByCss(".dijitMenuPopup")
         .then(function(result4) {
            expect(result4).to.equal(true, "The menu code should not have been removed");
         })
         .end()

         .elementByCss(".dijitMenuPopup")
         .isDisplayed()
         .then(function(result5) {
             expect(result5).to.equal(false, "The drop down menu should be hidden after the mouse click");
          })
         .end()

         //Check the menu published as expected
         .hasElementByCss(TestCommon.pubSubDataCssSelector("31", "alfTopic", "ALF_PUBLISHING_DROPDOWN_MENU"))
         .then(function(result6) {
            expect(result6).to.equal(true, "Clicking the menu did not publish on 'ALF_PUBLISHING_DROPDOWN_MENU' as expected");
         })
         .end()

//         // Open another menu with key functions
//         .keys(specialKeys["Tab"])
//         .keys(specialKeys["Tab"])
//         .keys(specialKeys["Down arrow"])
//         .sleep(500)
//         .elementByCss(".dijitMenuPopup")
//         .isDisplayed()
//         .then(function(result6) {
//             expect(result6).to.equal(true, "The drop down menu should be visible after key presses");
//          })
//         .end()
//
//         .keys(specialKeys["Down arrow"])
//         .keys(specialKeys["Return"])
//         .sleep(500)
//         .elementByCss(".dijitMenuPopup")
//         .isDisplayed()
//         .then(function(result7) {
//             expect(result7).to.equal(false, "The drop down menu should be hidden after key presses");
//          })
//         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});