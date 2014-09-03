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
 * This is the unit test for the alfresco/menus/AlfMenuTextForClipboard widget.
 * 
 * PLEASE NOTE: Development of this test has been temporarily abandoned as I can't get the modifier keys for
 * the copy/paste actions to work correctly - I've raised the following question on StackOverflow: 
 * http://stackoverflow.com/questions/20565341/how-do-i-keyboard-cut-and-paste-in-intern-functional-test
 * 
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, assert, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'AlfMenuTextForClipboard Test',
      'alfresco/menus/AlfMenuTextForClipboard': function () {

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/menus/page_models/AlfMenuTextForClipboard_TestPage.json")

            // Test #1
            // Check the initial labels...
            .pressKeys(specialKeys["Tab"])
            .pressKeys(specialKeys["Down arrow"])
            .pressKeys(specialKeys["Right arrow"])
            // .findByCssSelector("#TEXT1 span.label")
            //    .text()
            //    .then(function(resultText) {
            //       assert(resultText == "", "Test #1a - The inital label of TEXT1 has been set incorrectly: " + resultText);
            //    })
            //    .end()
            .pressKeys(specialKeys["Left arrow"])
            .pressKeys(specialKeys["Down arrow"])
            .pressKeys(specialKeys["Right arrow"])
            // .findByCssSelector("#TEXT2 span.label")
            //    .text()
            //    .then(function(resultText) {
            //       assert(resultText == "", "Test #1b - The inital label of TEXT2 has been set incorrectly: " + resultText);
            //    })
            // .end()
            .pressKeys(specialKeys["Left arrow"])
            .pressKeys(specialKeys["Down arrow"])
            .pressKeys(specialKeys["Right arrow"])

            // .findByCssSelector("#TEXT3 span.text")
            // .addValue(['Control','x','NULL'],function(err) {
            //             expect(err).to.be.null;
            //         })
            // .sleep(2000)
            // .type([specialKeys["Control"],"c"])


            .pressKeys(specialKeys["Tab"])
            .pressKeys(specialKeys["Tab"])
            // .pressKeys(specialKeys["Tab"])
            // .findByCssSelector("#TEXTAREA textarea")
            //    .click()
            //    // .type("hello")
            //    // .sleep(1000)
            // .sleep(2000)
            // .type([specialKeys["Control"],"v"])
               // .end()
               // .sleep(2000)


            // .findByCssSelector("#TEXT3 span.label")
            //    .text()
            //    .then(function(resultText) {
            //       assert(resultText == "Copy me!", "Test #1c - The inital label of TEXT3 has been set incorrectly: " + resultText);
            //    })


            // Post the coverage results...
            .then(function() {
               TestCommon.postCoverageResults(browser);
            })
            .end();
      }
   });
});