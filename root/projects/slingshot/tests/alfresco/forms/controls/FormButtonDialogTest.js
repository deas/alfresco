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
 * @author Richard Smith
 */
define(["intern!object",
        "intern/chai!expect",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!wd/lib/special-keys"], 
        function (registerSuite, expect, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'FormButtonDialog Test',
      'alfresco/forms/controls/FormButtonDialogTest': function () {

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/forms/controls/page_models/FormButtonDialog_TestPage.json")

         .end()

         // Does the form exist?
         .elementByCss("DIV#TEST_FORM > FORM")
         .then(function(el1) {
        	 expect(el1).to.be.an("object", "The Form could not be found");
         })
         .end()
         
         // Does the dialog opening button exist?
         .elementById("TEST_DIALOG_BUTTON")
         .then(function(el2) {
            expect(el2).to.be.an("object", "The Button could not be found");
         })
         .end()

         // Click the dialog button - does the dialog appear?
         .elementById("TEST_DIALOG_BUTTON")
         .moveTo()
         .sleep(500)
         .click()
         .sleep(500)
         .hasElementByCss(".alfresco-dialog-AlfDialog")
         .then(function(result1) {
            expect(result1).to.equal(true, "The Dialog did not appear");
         })
         .end()

         // Does the dialog have an appropriate title
         .elementByCss("span.dijitDialogTitle")
         .text()
         .then(function(resultText1) {
            expect(resultText1).to.equal("Twas brillig and the slithy toves...", "The Dialog title text is incorrect");
         })
         .end()

         // Does the dialog have a couple of buttons
         .elementsByCssSelector("div.alfresco-dialog-AlfDialog div.footer > span.alfresco-buttons-AlfButton")
         .then(function (buttons) {
            expect(buttons).to.have.length(2, "The popup Dialog does not contain 2 buttons");
         })
         .end()

         // Does the cancel button exist?
         .hasElementByCss("span.alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.cancellation")
         .then(function(result2) {
            expect(result2).to.equal(true, "The Dialog cancel button cannot be found");
         })
         .end()

         // Is the button copy correct?
         .elementByCss("span.alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.confirmation span.dijitButtonText")
         .text()
         .then(function (resultText2) {
            expect(resultText2).to.equal("Ok friend", "The copy on Dialog button one was incorrect");
         })
         .end()
         .elementByCss("span.alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.cancellation span.dijitButtonText")
         .text()
         .then(function (resultText3) {
            expect(resultText3).to.equal("No thanks buddy", "The copy on Dialog button two was incorrect");
         })
         .end()

         // Does the checkbox exist?
         .elementByCss("div#TEST_CHECKBOX_CONTAINER > div.control input")
         .then(function (el3) {
            expect(el3).to.be.an("object", "The Checkbox could not be found");
         })
         .end()

         // Is the checkbox checked?
         .elementByCss("div#TEST_CHECKBOX_CONTAINER > div.control input")
         .isSelected()
         .then(function (result3) {
            expect(result3).to.equal(false, "The Checkbox should not be selected when the Dialog form is first loaded");
         })
         .end()

         // Click the checkbox
         .elementByCss("div#TEST_CHECKBOX_CONTAINER > div.control input")
         .moveTo()
         .sleep(500)
         .click()
         .sleep(500)
         .end()

         // Now is the checkbox checked?
         .elementByCss("div#TEST_CHECKBOX_CONTAINER > div.control input")
         .isSelected()
         .then(function (result4) {
            expect(result4).to.equal(true, "The Checkbox should now be selected");
         })
         .end()

         // Click the cancel button
         .elementByCss("span.alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.cancellation span")
         .moveTo()
         .sleep(500)
         .click()
         .sleep(500)
         .end()

         // Has the dialog disappeared?
         .elementByCss(".alfresco-dialog-AlfDialog")
         .isDisplayed()
         .then(function(result5) {
            expect(result5).to.equal(false, "The Dialog was found but should be hidden after the cancel button has been clicked");
         })
         .end()

         // Submit the main form
         .elementByCss("span.alfresco-buttons-AlfButton.confirmationButton > span")
         .moveTo()
         .sleep(500)
         .click()
         .sleep(500)
         .end()

         // Check the correct pub/sub form submission element has appeared
         .elementByCss(TestCommon.nthTopicSelector(29))
         .text()
         .then(function(topicText) {
            expect(topicText).to.contain("TEST_FORM_SUBMITTED", "Form submission did not proceed as expected and the expected PUBLISH topic was missing");
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         })
         .end();
      }
   });
});