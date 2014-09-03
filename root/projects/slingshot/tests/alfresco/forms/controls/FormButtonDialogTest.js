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
        function (registerSuite, expect, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'FormButtonDialog Test',
      'alfresco/forms/controls/FormButtonDialogTest': function () {

         var browser = this.remote;
         var testname = "FormButtonDialogTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/forms/controls/page_models/FormButtonDialog_TestPage.json", testname)

         .end()

         // Does the form exist?
         .findByCssSelector("DIV#TEST_FORM > FORM")
         .then(function(el1) {
            TestCommon.log(testname,43,"Does the form exist?");
            expect(el1).to.be.an("object", "The Form could not be found");
         })
         .end()
         
         // Does the dialog opening button exist?
         .elementById("TEST_DIALOG_BUTTON")
         .then(function(el2) {
            TestCommon.log(testname,51,"Does the dialog opening button exist?");
            expect(el2).to.be.an("object", "The Button could not be found");
         })
         .end()

         // Click the dialog button - does the dialog appear?
         .elementById("TEST_DIALOG_BUTTON")
         .sleep(500)
         .click()
         .sleep(500)
         .hasElementByCss(".alfresco-dialog-AlfDialog")
         .then(function(result1) {
            TestCommon.log(testname,64,"Click the dialog button - does the dialog appear?");
            expect(result1).to.equal(true, "The Dialog did not appear");
         })
         .end()

         // Does the dialog have an appropriate title?
         .findByCssSelector("span.dijitDialogTitle")
         .getVisibleText()
         .then(function(resultText1) {
            TestCommon.log(testname,73,"Does the dialog have an appropriate title?");
            expect(resultText1).to.equal("Twas brillig and the slithy toves...", "The Dialog title text is incorrect");
         })
         .end()

         // Does the dialog have a couple of buttons?
         .elementsByCssSelector("div.alfresco-dialog-AlfDialog div.footer > span.alfresco-buttons-AlfButton")
         .then(function (buttons) {
            TestCommon.log(testname,81,"Does the dialog have a couple of buttons?");
            expect(buttons).to.have.length(2, "The popup Dialog does not contain 2 buttons");
         })
         .end()

         // Does the cancel button exist?
         .hasElementByCss("span.alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.cancellation")
         .then(function(result2) {
            TestCommon.log(testname,89,"Does the cancel button exist?");
            expect(result2).to.equal(true, "The Dialog cancel button cannot be found");
         })
         .end()

         // Is the button copy correct?
         .findByCssSelector("span.alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.confirmation span.dijitButtonText")
         .getVisibleText()
         .then(function (resultText2) {
            TestCommon.log(testname,98,"Is the button copy correct?");
            expect(resultText2).to.equal("Ok friend", "The copy on Dialog button one was incorrect");
         })
         .end()
         .findByCssSelector("span.alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.cancellation span.dijitButtonText")
         .getVisibleText()
         .then(function (resultText3) {
            TestCommon.log(testname,105,"Is the button copy correct?");
            expect(resultText3).to.equal("No thanks buddy", "The copy on Dialog button two was incorrect");
         })
         .end()

         // Does the checkbox exist?
         .findByCssSelector("div#TEST_CHECKBOX_CONTAINER > div.control input")
         .then(function (el3) {
            TestCommon.log(testname,113,"Does the checkbox exist?");
            expect(el3).to.be.an("object", "The Checkbox could not be found");
         })
         .end()

         // Is the checkbox checked?
         .findByCssSelector("div#TEST_CHECKBOX_CONTAINER > div.control input")
         .isSelected()
         .then(function (result3) {
            TestCommon.log(testname,122,"Is the checkbox checked?");
            expect(result3).to.equal(false, "The Checkbox should not be selected when the Dialog form is first loaded");
         })
         .end()

         // Click the checkbox
         .findByCssSelector("div#TEST_CHECKBOX_CONTAINER > div.control input")
         .sleep(500)
         .click()
         .sleep(500)
         .end()

         // Now is the checkbox checked?
         .findByCssSelector("div#TEST_CHECKBOX_CONTAINER > div.control input")
         .isSelected()
         .then(function (result4) {
            TestCommon.log(testname,139,"Now is the checkbox checked?");
            expect(result4).to.equal(true, "The Checkbox should now be selected");
         })
         .end()

         // Click the cancel button
         .findByCssSelector("span.alfresco-buttons-AlfButton.alfresco-dialogs-_AlfCreateFormDialogMixin.cancellation span")
         .sleep(500)
         .click()
         .sleep(500)
         .end()

         // Has the dialog disappeared?
         .findByCssSelector(".alfresco-dialog-AlfDialog")
         .isDisplayed()
         .then(function(result5) {
            TestCommon.log(testname,156,"Has the dialog disappeared?");
            expect(result5).to.equal(false, "The Dialog was found but should be hidden after the cancel button has been clicked");
         })
         .end()

         // Submit the main form
         .findByCssSelector("span.alfresco-buttons-AlfButton.confirmationButton > span")
         .sleep(500)
         .click()
         .sleep(500)
         .end()

         // Check the correct pub/sub form submission element has appeared
         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "alfTopic", "TEST_FORM_SUBMITTED"))
         .then(function(result6) {
            TestCommon.log(testname,172,"Check the correct pub/sub form submission element has appeared");
            expect(result6).to.equal(true, "Form submission did not proceed as expected and the expected publish on 'TEST_FORM_SUBMITTED' was missing");
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