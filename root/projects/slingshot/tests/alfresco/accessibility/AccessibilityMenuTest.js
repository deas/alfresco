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
         var testname = "AccessibilityMenuTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/accessibility/page_models/AccessibilityMenu_TestPage.json")

         .end()

         // Find the menu element
         .elementById("AccessibilityMenu")
         .then(function (el) {
            TestCommon.log(testname,43,"Find the menu element");
            expect(el).to.be.an("object", "The Accessibility Menu could not be found");
         })
         .end()

         // Find the heading text
         .elementByCss("#AccessibilityMenu > p")
         .text()
         .then(function(headingText) {
            TestCommon.log(testname,52,"Find the heading text");
            expect(headingText).to.equal("Access key links:", "The heading text is wrong");
         })
         .end()

         // Find the menu items
         .elementsByCssSelector("#AccessibilityMenu > ul > li")
         .then(function (menuitems) {
            TestCommon.log(testname,60,"Find the menu items");
            expect(menuitems).to.have.length(7, "The Accessibility Menu does not contain 7 <li> items");
         })
         .end()

         // Find the first target
         .elementById("accesskey-skip")
         .then(function (el) {
            TestCommon.log(testname,68,"Find the first target");
            expect(el).to.be.an("object", "The accesskey-skip target is missing");
         })
         .end()

         // Find the second target
         .elementById("accesskey-foot")
         .then(function (el) {
            TestCommon.log(testname,76,"Find the second target");
            expect(el).to.be.an("object", "The accesskey-foot target is missing");
         })
         .end()

         // Find the first menu link - which links the first target
         .elementByCss("#AccessibilityMenu > ul > li:nth-of-type(1) > a ")
         .then(function (el) {
            TestCommon.log(testname,84,"Find the first menu link - which links the first target");
            expect(el).to.be.an("object", "The first link is missing");
         })
         .end()

         // Hit the browser with a sequence of different accesskey combinations and the letter 's' for a nav skip
         .keys([specialKeys["Shift"], "s"])
         .keys([specialKeys["Shift"]])
         .keys([specialKeys["Alt"], specialKeys["Shift"], "s"])
         .keys([specialKeys["Alt"], specialKeys["Shift"]])
//         .keys([specialKeys["Control"], specialKeys["Command"], "s"])
//         .keys([specialKeys["Control"], specialKeys["Command"]])
         .url()
         .then(function (page) {
            TestCommon.log(testname,98,"Hit the browser with a sequence of different accesskey combinations and the letter 's' for a nav skip");
            expect(page).to.contain("#accesskey-skip", "Accesskey target not linked to");
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