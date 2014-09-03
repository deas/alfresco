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
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon"], 
        function (registerSuite, expect, assert, require, TestCommon) {

   registerSuite({
      name: 'Twister Test',
      'Twister tests': function () {

         var browser = this.remote;
         var testname = "Twister tests";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/layout/page_models/Twister_TestPage.json", testname)

         .end()

         // Click button 1
         .findById("BUTTON_1")
         .click()
         .end()

         .findByCssSelector("#TWISTER_HEADING_LEVEL > div > h3")
         .getVisibleText()
         .then(function (text) {
            TestCommon.log(testname,null,"Check the first twister renders correctly");
            expect(text).to.equal("Twister with heading level", "The first twister did not render correctly");
         })
         .end()

         .findAllByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
         .then(function (rows) {
            TestCommon.log(testname,null,"Check facets are shown after clicking button 1");
            expect(rows).to.have.length.above(0, "There should some facets shown");
         })
         .end()

         .findByCssSelector("#TWISTER_NO_HEADING_LEVEL > div")
         .getVisibleText()
         .then(function (text) {
            TestCommon.log(testname,null,"Check the second twister renders correctly");
            expect(text).to.equal("Twister with no heading level", "The second twister did not render correctly");
         })
         .end()

         .findByCssSelector("#TWISTER_BAD_HEADING_LEVEL > div")
         .getVisibleText()
         .then(function (text) {
            TestCommon.log(testname,null,"Check the third twister renders correctly");
            expect(text).to.equal("", "The third twister did not render correctly");
         })
         .end()

         .findByXpath(TestCommon.consoleXpathSelector("A heading must have a numeric level from 1 to 6 and must have a label"))
         .then(
            function(){TestCommon.log(testname,null,"Check the console logged the faulty twister");},
            function(){assert(false, "The console did not log the faulty twister");}
         )
         .end()

         // Click the title of twister 1
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div > h3")
         .click()
         .end()

         // Title should still be visible
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div > h3")
         .getVisibleText()
         .then(function (text) {
            TestCommon.log(testname,null,"Check the first twister title is still visible");
            expect(text).to.equal("Twister with heading level", "The first twister title is not visible");
         })
         .end()

         .findByCssSelector("#TWISTER_HEADING_LEVEL > ul")
         .getVisibleText()
         .then(function (text) {
            TestCommon.log(testname,null,"Check the facets have hidden");
            expect(text).to.equal("", "The facets should be hidden");
         })
         .end()

         // Click the title of twister 1
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div > h3")
         .click()
         .end()

         // Title should still be visible
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div > h3")
         .getVisibleText()
         .then(function (text) {
            TestCommon.log(testname,null,"Check the first twister title is still visible");
            expect(text).to.equal("Twister with heading level", "The first twister title is not visible");
         })
         .end()

         .findAllByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
         .then(function (rows) {
            TestCommon.log(testname,null,"Check facets are shown after clicking the title again");
            expect(rows).to.have.length.above(0, "There should be some facets shown");
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});