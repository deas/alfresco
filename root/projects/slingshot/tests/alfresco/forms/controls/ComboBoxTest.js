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
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, assert, require, TestCommon, keys) {

   registerSuite({
      name: 'ComboBox Test',
      'Check Setup': function () {

         var testname = "Test ComboBox Setup";
         return TestCommon.loadTestWebScript(this.remote, "/ComboBox", testname)

         // Open the tags combo and count the available options...
         .findByCssSelector("#TAGS .dijitArrowButtonInner")
            .click()
            .sleep(10000)
            .end()

         .findAllByCssSelector("#TAGS_CONTROL_popup .dijitMenuItem[item]")
            .then(function(elements) {
               TestCommon.log(testname, "Checking the number of tag options...");
               assert(elements.length == 4, "Test 1a - Four tag options were expected, found: " + elements.length);
            })
            .end()

         .findByCssSelector("#PROPERTIES .dijitArrowButtonInner")
            .click()
            .end()

         .findAllByCssSelector("#PROPERTIES_CONTROL_popup .dijitMenuItem[item]")
            .then(function(elements) {
               TestCommon.log(testname, "Checking the number of properties options...");
               assert(elements.length == 5, "Test 1b - Five property options were expected, found: " + elements.length);
            })
            .end();

      },

      'Handles Input': function () {

         var testname = "Handle Input Test";
         var browser = this.remote;
         return browser

         .findByCssSelector("#TAGS_CONTROL")
            .click()
            .type("t")
            .sleep(1000)
            .end()

         .findAllByCssSelector("#TAGS_CONTROL_popup .dijitMenuItem[item]")
            .then(function(elements) {
               TestCommon.log(testname, "Checking tag options are reduced...");
               assert(elements.length == 3, "Test 1a - Three tag options were expected, found: " + elements.length);
            })
            .end()

         .findByCssSelector("#TAGS_CONTROL")
            .click()
            .type("ag1")
            .sleep(1000)
            .end()

         .findAllByCssSelector("#TAGS_CONTROL_popup .dijitMenuItem[item]")
            .then(function(elements) {
               TestCommon.log(testname, "Checking tag options are further reduced...");
               assert(elements.length == 2, "Test 1b - Two tag options were expected, found: " + elements.length);
            })
            .end()

         // Select and submit...
         .findByCssSelector("#TAGS_CONTROL")
            .click()
            .pressKeys(keys.RETURN)
            .end()

         .findByCssSelector(".confirmationButton > span")
            .click()
            .end()

         .findAllByCssSelector(TestCommon.pubDataCssSelector("POST_FORM", "tag", "tag1"))
            .then(function(elements) {
               assert(elements.length === 1, "Test #1c - The tag value was not auto-completed and posted");
            })
            .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});