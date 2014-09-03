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
        "intern/dojo/node!leadfoot/Command"], 
        function (registerSuite, expect, assert, require, TestCommon, Command) {

   registerSuite({
      name: 'Page Creator Test',
      'Basic Test': function () {

         // var command = new Command(session);
         var browser = this.remote;

         var testname = "BasicTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/creation/page_models/WidgetConfig_TestPage.json", testname)

         // 1. Drag and drop the single item from the palette onto the drop-zone control...
         .findByCssSelector("#dojoUnique1 > .title")
            .then(function(element) {
               browser.moveMouseTo(element)
            })
            .pressMouseButton()
            .moveMouseTo(null, 1, 1)
            .end()

         .findByCssSelector(".alfresco-creation-DropZone > div")
            .then(function(element) {
               browser.moveMouseTo(element)
            })
            .releaseMouseButton()
            .end()

         // 2. Select the dropped widget by clicking on the drag handle...
         .findByCssSelector(".dojoDndHandle")
            .click()
            .end()

         // 3. Check that the validation text box is displayed...
         .findByCssSelector(".alfresco-forms-controls-DojoValidationTextBox .dijitInputContainer input")
            .getProperty('value')
            .then(function(resultText) {
               assert(resultText == "Value1", "Test #2a - The initial value was not set correctly: " + resultText);
            })
            .end()

         // 4. Save the config...
         .findByCssSelector(".alfresco-creation-WidgetConfig .confirmationButton > span")
            .click()
            .end()

         // 5. Save the form...
         .findByCssSelector("#FORM .confirmationButton > span")
            .click()
            .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});