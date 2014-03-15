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
      name: 'AccessibilityMenu Test',
      'alfresco/accessibility/AccessibilityMenu': function () {

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/accessibility/page_models/AccessibilityMenu_TestPage.json")

         .end()

         // Find the menu element
         .elementById("AccessibilityMenu")
         .then(function (el) {
            // Test #1
            // Check the menu exists at all...
            expect(el).to.be.an("object", "Test #1 - The Accessibility Menu could not be found");
         })
         .end()

         // Find the heading text
         .elementByCss("#AccessibilityMenu > p")
         .text()
         .then(function(headingText) {
            // Test #2
            // Check the heading text is correct...
            expect(headingText).to.equal("Access key links:", "Test #2 - The heading text is wrong");
         })
         .end()

         // Find the menu items
         .elementsByCssSelector("#AccessibilityMenu > ul > li")
         .then(function (menuitems) {
        	// Test #3
            // Check the menu contains an unordered list with 7 elements
            expect(menuitems).to.have.length(7, "Test #3 - The Accessibility Menu does not contain 7 <li> items");
         })
         .end()

         // Find the first target
         .elementById("accesskey-skip")
         .then(function (el) {
            // Test #4
            // Check the target exists at all...
            expect(el).to.be.an("object", "Test #4 - The accesskey-skip target is missing");
         })
         .end()

         // Find the second target
         .elementById("accesskey-foot")
         .then(function (el) {
            // Test #5
            // Check the target exists at all...
            expect(el).to.be.an("object", "Test #5 - The accesskey-foot target is missing");
         })
         .end()

         // Find the first menu link - which links the first target
         .elementByCss("#AccessibilityMenu > ul > li:nth-of-type(1) > a ")
         .then(function (el) {
            // Test #6
            // Check the first link exists
            expect(el).to.be.an("object", "Test #6 - The first link is missing");
         })
         .end()

         // Hit the browser with a sequence of different accesskey combinations and the letter 's' for a nav skip
         .keys([specialKeys["Alt"], specialKeys["Shift"], "s"])
         .keys([specialKeys["Shift"], "s"]) // Release SHIFT (keep ALT down)
         .keys([specialKeys["Alt"]]) // Release ALT
         .keys([specialKeys["Control"], specialKeys["Command"], "s"])
         .url()
         .then(function (page) {
        	 // Test #7
             // Check the url contains the accesskey target
        	 expect(page).to.contain("#accesskey-skip", "Test #7 - Accesskey target not linked to");
         })
         .end()

         // Release control, command...
         .keys([specialKeys["Control"], specialKeys["Command"]])

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         })
         .end();
      }
   });
});