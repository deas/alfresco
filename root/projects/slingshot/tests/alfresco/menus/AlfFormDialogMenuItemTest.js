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
 * This is the unit test for the alfresco/menus/AlfFormDialogMenuItem widget.
 * 
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!wd/lib/special-keys"], 
        function (registerSuite, assert, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'AlfFormDialogMenuItem Test',
      'alfresco/menus/AlfFormDialogMenuItem': function () {

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/menus/page_models/AlfFormDialogMenuItem_TestPage.json")

            // Test #1
            // Check the service subscription is setup
            .end()
            .elementByCss(TestCommon.nthTopicSelector(1))
            .text()
            .then(function(text) {
               console.log("Test #1a");
               assert(text == "ALF_CREATE_FORM_DIALOG_REQUEST", "Test #1a - A subscription from the alfresco/dialogs/AlfDialogService should have been setup");
            })
            .end()
            .elementByCss(TestCommon.nthTopicSelector(2))
            .text()
            .then(function(text) {
               console.log("Test #1b");
               assert(text.lastIndexOf("ALF_CREATE_FORM_DIALOG_MIXIN_REQUEST_TOPIC") != -1, "Test #1b - form dialog mixin request topic missing");
            })
            .end()
            .elementByCss(TestCommon.nthTopicSelector(3))
            .text()
            .then(function(text) {
               console.log("Test #1c");
               assert(text.lastIndexOf("ALF_CREATE_FORM_DIALOG_MIXIN_CONFIRMATION_TOPIC") != -1, "Test #1b - form dialog mixin confirmation topic missing");
            })

            // Test #2
            // Create the dialog...
            .end()
            .elementByCss("#DROP_DOWN_MENU_text")
               .moveTo()
               .click()
               .end()
            .elementByCss("#FDM1_text")
               .moveTo()
               .click()
               .end()
            .hasElementByCss(".alfresco-dialog-AlfDialog")
            .then(function(result) {
               console.log("Test #2a");
               assert(result == true, "Test #2a - The dialog was not created");
            })

            .end()
            .elementByCss(".alfresco-dialog-AlfDialog .dijitDialogTitle")
            .text()
            .then(function(text) {
               console.log("Test #2b");
               assert(text == "New Dialog", "Test #2b - The dialog title was not set correctly");
            })

            .hasElementByCss(".alfresco-forms-controls-BaseFormControl")
            .then(function(result) {
               console.log("Test #2c");
               assert(result == true, "Test #2c - The form control was not displayed");
            })

            .end()
            .hasElementByCss(".alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.confirmation")
            .then(function(result) {
               console.log("Test #2d");
               assert(result == true, "Test #2d - The confirmation button is missing");
            })
            .end()
            .elementByCss(".alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.confirmation")
            .text()
            .then(function(text) {
               console.log("Test #2e");
               assert(text == "Save", "Test #2e - The confirmation button text is wrong: " + text);
            })

            .end()
            .hasElementByCss(".alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.cancellation")
            .then(function(result) {
               console.log("Test #2f");
               assert(result == true, "Test #2f - The cancellation button is missing");
            })
            .end()
            .elementByCss(".alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.cancellation")
            .text()
            .then(function(text) {
               console.log("Test #2g");
               assert(text == "Close", "Test #2g - The cancellation button text is wrong: " + text);
            })

            // Test #3
            // Check publication occurs (don't check details - that's for another test)
            .end()
            .elementByCss(".alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.confirmation span.dijitButtonText")
               .moveTo()
               .click()
               .end()
            .hasElementByCss(TestCommon.topicSelector("FORM_SUBMIT", "publish"))
            .then(function(result) {
               console.log("Test #3a");
               assert(result == true, "Test #3a - Form didn't publish");
            })
            
            // Test #4
            // Close the dialog...
            .end()
            .elementByCss("#DROP_DOWN_MENU_text")
               .moveTo()
               .sleep(1000)
               .click()
               .end()
            .elementByCss("#FDM1_text")
               .moveTo()
               .click()
               .end()
            .hasElementByCss(".alfresco-dialog-AlfDialog")
            .then(function(result) {
               console.log("Test #4a");
               assert(result == true, "Test #4a - The dialog was not created");
            })
            .end()
            .elementByCss(".alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.cancellation span.dijitButtonText")
               .moveTo()
               .click()
               .end()
            .elementByCss(TestCommon.nthTopicSelector("last"))
            .text()
            .then(function(text) {
               console.log("Test #4b"); 
               assert(text.lastIndexOf("ALF_CLOSE_DIALOG") != -1, "Test #4b - A request to close the form was not published");
            })
            .sleep(1000)

            // Post the coverage results...
            .then(function() {
               TestCommon.postCoverageResults(browser);
            })
            .end();
      }
   });
});