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
      name: 'RenderFilter Test',
      'RenderFilter': function () {
         console.log("HELLO");
         var browser = this.remote;
         var testname = "RenderFilterTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/core/page_models/RenderFilter_TestPage.json")

         .end()


         // The IDs that should be displayed are...
         // MBI1 - successful filter rule
         // MBI3 - negated rule
         // MBI4 - absent property
         // MBI5 - successful AND condition
         // MI1 - successful filter rule following inherited currentItem change

         // The IDs that should not be displayed are...
         // MBI2 - failed filter rule
         // MBI6 - failed AND condition
         // MI2 - failed filter rule following inherited currentItem change

         .elementById("MBI1")
         .then(function (el) {
            TestCommon.log(testname,54,"Test standard single rule");
            expect(el).to.be.an("object", "A single render filter rule failed unexpectedly");
         })
         .end()

         .elementById("MBI3")
         .then(function (el) {
            TestCommon.log(testname,61,"Test negated single rule");
            expect(el).to.be.an("object", "A single negated render filter rule failed unexpectedly");
         })
         .end()

         .elementById("MBI4")
         .then(function (el) {
            TestCommon.log(testname,68,"Test absent property rule");
            expect(el).to.be.an("object", "A single absent property render filter rule failed unexpectedly");
         })
         .end()

         .elementById("MBI5")
         .then(function (el) {
            TestCommon.log(testname,68,"Test AND property rule");
            expect(el).to.be.an("object", "An AND condition property render filter rule failed unexpectedly");
         })
         .end()

         .elementsByCssSelector("#MBI2")
         .then(function (els) {
            TestCommon.log(testname,89,"Test failing standard single rule");
            assert(els.length == 0, "An inherited currentItem change render filter rule passed unexpectedly");
         })
         .end()

         .elementByCss("#DD1_text")
            .moveTo()
            .sleep(250)
            .click()
            .end()

         .sleep(250)

         .end()
         .elementById("MI1")
         .then(function (el) {
            TestCommon.log(testname,82,"Test inherited current item change rule (success)");
            expect(el).to.be.an("object", "An inherited currentItem change render filter rule failed unexpectedly");
         })
         .end()

         .elementsByCssSelector("#MI2")
         .then(function (els) {
            TestCommon.log(testname,82,"Test inherited current item change rule (failure)");
            assert(els.length == 0, "An inherited currentItem change render filter rule passed unexpectedly");
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