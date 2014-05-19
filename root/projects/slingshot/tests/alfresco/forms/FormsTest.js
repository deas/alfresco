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
        "alfresco/TestCommon",
        "intern/dojo/node!wd/lib/special-keys"], 
        function (registerSuite, expect, assert, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'Forms Test',
      'Forms': function () {
         var browser = this.remote;
         var testname = "FormsTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/forms/page_models/Forms_TestPage.json", testname)

         .end()

         // 1. Test setting and getting the form value from the hash fragment
         .elementByCss("#HASH_TEXT_BOX_1 .dijitInputContainer input")
            .type("test1")
            .end()
         .elementByCss("#HASH_TEXT_BOX_2 .dijitInputContainer input")
            .type("test2")
            .end()
         .elementByCss("#HASH_FORM .buttons .alfresco-buttons-AlfButton.confirmationButton > span")
            .moveTo()
            .click()
            .end()
         .url()
            .then(function(page) {
               TestCommon.log(testname,54,"Check fragment hash has been updated");
               expect(page).to.contain("#field1=test1&field2=test2", "Test #1 - form submit did not update hash fragment");
            })
            .end()

         // 2. Check that setting the hash will update the form...
         .elementByCss("#SET_HASH")
            .moveTo()
            .click()
            .end()

         .elementByCss("#HASH_TEXT_BOX_1 .dijitInputContainer input")
            .getValue()
            .then(function(resultText) {
               TestCommon.log(testname,67,"Check fragment hash change sets field1");
               assert(resultText == "updatedField1", "Test #2a - field1 was not set by the hash: " + resultText);
            })
            .end()
         .elementByCss("#HASH_TEXT_BOX_2 .dijitInputContainer input")
            .getValue()
            .then(function(resultText) {
               TestCommon.log(testname,74,"Check fragment hash change sets field2");
               assert(resultText == "updatedField2", "Test #2b - field2 was not set by the hash: " + resultText);
            })
            .end()

         // 2. Test hiding/displaying/configuring the standard buttons
         .elementsByCss("#STANDARD_FORM .buttons .alfresco-buttons-AlfButton.confirmationButton.dijitButtonDisabled")
            .then(function(elements) {
               TestCommon.log(testname,82,"Check that the standard form confirmation button is initially disabled");
               assert(elements.length == 1, "Test #3 - Standard form button was not initially disabled");
            })
            .end()

         // 3. Update the fields and check that the submit button become enabled...
         .elementByCss("#TEXT_BOX_1 .dijitInputContainer input")
            .type("test3")
            .end()
         .elementByCss("#TEXT_BOX_2 .dijitInputContainer input")
            .type("9")
            .end()
         .elementsByCss("#STANDARD_FORM .buttons .alfresco-buttons-AlfButton.confirmationButton.dijitButtonDisabled")
            .then(function(elements) {
               TestCommon.log(testname,82,"Check that the standard form confirmation button is initially disabled");
               assert(elements.length == 0, "Test #4a - Standard form button was not enabled following valid data entry");
            })
            .end()
         .elementByCss("#STANDARD_FORM .buttons .alfresco-buttons-AlfButton.confirmationButton > span")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "field3", "test3"))
            .then(function(elements) {
               TestCommon.log(testname,106,"Check standard form was published correctly");
               assert(elements.length == 1, "Test #4b - standard form didn't publish correctly");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "field4", "9"))
            .then(function(elements) {
               TestCommon.log(testname,106,"Check standard form was published correctly");
               assert(elements.length == 1, "Test #4c - standard form didn't publish correctly");
            })
            .end()

         // 3. Test creating additional buttons
         .elementsByCss("#ADD_BUTTON_1")
            .then(function(elements) {
               TestCommon.log(testname,120,"Check that the first additional button was added");
               assert(elements.length == 1, "Test #5a - The first additional button could not be found");
            })
            .end()
         .elementsByCss("#ADD_BUTTON_2")
            .then(function(elements) {
               TestCommon.log(testname,126,"Check that the second additional button was added");
               assert(elements.length == 1, "Test #5b - The second additional button could not be found");
            })
            .end()
         .elementByCss("#ADD_TEXT_BOX_1 .dijitInputContainer input")
            .type("test4")
            .end()
         .elementByCss("#ADD_TEXT_BOX_2 .dijitInputContainer input")
            .type("test5")
            .end()
         .elementByCss("#ADD_BUTTON_1")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "field5", "test4"))
            .then(function(elements) {
               TestCommon.log(testname,142,"Check additional form was published correctly");
               assert(elements.length == 1, "Test #5c - the additional button didn't publish data correctly");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "field6", "test5"))
            .then(function(elements) {
               TestCommon.log(testname,148,"Check additional form was published correctly");
               assert(elements.length == 1, "Test #5d - the additional button didn't publish data correctly");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "extra", "stuff"))
            .then(function(elements) {
               TestCommon.log(testname,154,"Check additional form was published correctly");
               assert(elements.length == 1, "Test #5e - the additional button didn't publish data correctly");
            })
            .end()

         // 4. Test scoping
         .elementsByCss(TestCommon.topicSelector("CUSTOM_SCOPE_AddButton1", "publish", "any"))
            .then(function(elements) {
               TestCommon.log(testname,162,"Check custom scope applied to additional button form");
               assert(elements.length == 1, "Test #6a - Custom scope not set");
            })
            .end()

         .elementsByCss(TestCommon.topicSelector("SET_HASH", "publish", "any"))
            .then(function(elements) {
               TestCommon.log(testname,169,"Check global scope applied to hash form");
               assert(elements.length == 1, "Test #6b - Global scope not set");
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