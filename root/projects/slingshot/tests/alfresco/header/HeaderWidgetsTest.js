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
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, expect, assert, require, TestCommon, keys) {

   registerSuite({
      name: 'Header Widgets Test',
      'Basic Test': function () {

         var browser = this.remote;
         var testname = "WarningTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/header/page_models/HeaderWidgets_TestPage.json", testname)

         .end()

         // Check that the header CSS is applied...
         .findByCssSelector(".alfresco-layout-LeftAndRight.alfresco-header-Header")
            .then(null, function() {
               assert(false, "Test #1a - The header CSS was not applied correctly")
            })
            .end()

         // Test the title text...
         .findByCssSelector(".alfresco-header-Title > .text")
            .getVisibleText()
            .then(function(resultText) {
               assert(resultText == "Test Title", "Test #1b - The title was not set correctly")
            })
            .end()

         // Click on the popup... 
         .findByCssSelector("#HEADER_POPUP_text")
            .click()
            .end()

         // Check the preset user status...
         .findByCssSelector("#PRESET_STATUS > div.status")
            .getVisibleText()
            .then(function(resultText) {
               assert(resultText == "Test Status", "Test #1c - Preset status not set correctly");
            })
            .end()

         .findByCssSelector("#PRESET_STATUS > div.lastUpdate > span")
            .getVisibleText()
            .then(function(resultText) {
               assert(resultText == "over 14 years ago", "Test #1d - Preset status time not displayed as expected");
            })
            .end()

         // Check the unset user status...
         .findByCssSelector("#NO_STATUS > div.status")
            .getVisibleText()
            .then(function(resultText) {
               assert(resultText == "What are you thinking?", "Test #1e - Unset status not displayed correctly");
            })
            .end()

         .findByCssSelector("#NO_STATUS > div.lastUpdate")
            .getVisibleText()
            .then(function(resultText) {
               assert(resultText == "Last updated: never", "Test #1f - Unset status time not displayed as expected");
            })
            .end()

         // Click on a status to display the update dialog...
         .findByCssSelector("#NO_STATUS > div.status")
            .click()
            .end()

         .findByCssSelector(".alfresco-dialog-AlfDialog")
            .then(null, function() {
               assert(false, "Test #2a - The update dialog was not displayed")
            })
            .end()
         .findByCssSelector(".alfresco-dialog-AlfDialog .dijitDialogTitleBar > span")
            .getVisibleText()
            .then(function(resultText) {
               assert(resultText == "What Are You Thinking?", "Test #2b - User status dialog title not set correctly");
            })
            .end()

         // Update the status...
         .findByCssSelector("#NO_STATUS_STATUS_TEXTAREA")
            .sleep(250)
            .pressKeys([keys.SHIFT, keys.HOME])
            .type("Status Update")
            .pressKeys([keys.SHIFT])
            .pressKeys([keys.TAB])
            .pressKeys([keys.RETURN])
            .end()

         // Check that the status update was posted correctly...
         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "status", "Status Update"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #2c - User status not published correctly");
            })
            .end()

         // Use the menus to simulate a status update...
         // (with the bonus of checking the header versions of the menu items work)...
         .findByCssSelector("#HEADER_POPUP_text")
            .click()
            .end()
            .sleep(150)
         .findByCssSelector("#CASCADE_1_text")
            .click()
            .end()
            .sleep(150)
         .findByCssSelector("#MENU_ITEM_1_text")
            .click()
            .end()
            .sleep(150)

         // Open the popup again...
         .findByCssSelector("#HEADER_POPUP_text")
            .click()
            .end()

         // Check the preset user status (which should be updated)...
         .findByCssSelector("#NO_STATUS > div.status")
            .getVisibleText()
            .then(function(resultText) {
               assert(resultText == "Button Update", "Test #3a - Status not updated");
            })
            .end()
         .findByCssSelector("#NO_STATUS > div.lastUpdate > span")
            .getVisibleText()
            .then(function(resultText) {
               assert(resultText == "over 14 years ago", "Test #3b - Status time not updated");
            })
            .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});