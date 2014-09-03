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
 * 
 * @author Erik Winlöf
 */
define(["intern!object",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, assert, require, TestCommon, keys) {

           debugger;

   registerSuite({
      name: 'Select DojoDateTextBox Test',
      'alfresco/forms/controls/DojoDateTextBox': function () {

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/forms/controls/page_models/DojoDateTextBox_TestPage.json")

            // TEST 1
               .findByCssSelector("#DOJODATETEXTBOX_CONTROL")
               .getValue()
               .then(function(value) {
                  assert(value == "2012-12-12", "Unexpected date value found in control");
               })
               .end()

            // TEST 2
               .findByCssSelector("#DOJODATETEXTBOX .control .dijitArrowButton input.dijitArrowButtonInner")
               .click()
               .end()
               .findByCssSelector("#DOJODATETEXTBOX_CONTROL_popup tbody tr:nth-of-type(3) td:nth-of-type(5) span")
               .click()
               .end()
               .findByCssSelector("#DOJODATETEXTBOX_CONTROL")
               .getValue()
               .then(function(value) {
                  assert(value == "2012-12-14", "Unexpected date value found in control after date change");
               })
               .end()

            // TEST 3
               .findByCssSelector("#FORM > .buttons > span:nth-of-type(1) > span > span > span:nth-of-type(3)")
               .click()
               .end()
               .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "someDate", "2012-12-14"))
               .then(function(result) {
                  assert(result == true, "Form submission did not publish the expected event");
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