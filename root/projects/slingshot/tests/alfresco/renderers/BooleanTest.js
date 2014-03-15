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
 * The purpose of this test is to ensure that keyboard accessibility is possible between the header and the 
 * main table. It should be possible to use the tab/shift-tab keys to navigate along the headers (and the enter/space key
 * to make requests for sorting) and then the cursor keys to navigate around the table itself.
 * 
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!expect",
        "require",
        "alfresco/TestCommon"], 
        function (registerSuite, expect, require, TestCommon) {

   registerSuite({
      name: 'Boolean Test',
      'alfresco/renderers/Boolean': function () {

         var browser = this.remote;
         var testname = "BooleanTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/renderers/page_models/Boolean_TestPage.json")

         .end()

         // Check there are 10 rows
         .elementsByCssSelector("span.alfresco-renderers-Property")
         .then(function (booleans) {
            TestCommon.log(testname,46,"Check there are 10 rows as described in the model");
            expect(booleans).to.have.length(10, "There should be 10 rows rendered");
         })
         .end()

         // Check each row
         .elementByCssSelector("tr.alfresco-documentlibrary-views-layouts-Row:nth-of-type(1)")
         .text()
         .then(function (result1) {
            TestCommon.log(testname,55,"Check the value of a boolean");
            expect(result1).to.equal("Yes", "Row one should say 'Yes'");
         })
         .end()

         // Check each row
         .elementByCssSelector("tr.alfresco-documentlibrary-views-layouts-Row:nth-of-type(2)")
         .text()
         .then(function (result2) {
            TestCommon.log(testname,64,"Check the value of a boolean");
            expect(result2).to.equal("Yes", "Row two should say 'Yes'");
         })
         .end()

         // Check each row
         .elementByCssSelector("tr.alfresco-documentlibrary-views-layouts-Row:nth-of-type(3)")
         .text()
         .then(function (result3) {
            TestCommon.log(testname,73,"Check the value of a boolean");
            expect(result3).to.equal("Yes", "Row three should say 'Yes'");
         })
         .end()

         // Check each row
         .elementByCssSelector("tr.alfresco-documentlibrary-views-layouts-Row:nth-of-type(4)")
         .text()
         .then(function (result4) {
            TestCommon.log(testname,82,"Check the value of a boolean");
            expect(result4).to.equal("Yes", "Row four should say 'Yes'");
         })
         .end()

         // Check each row
         .elementByCssSelector("tr.alfresco-documentlibrary-views-layouts-Row:nth-of-type(5)")
         .text()
         .then(function (result5) {
            TestCommon.log(testname,91,"Check the value of a boolean");
            expect(result5).to.equal("No", "Row five should say 'No'");
         })
         .end()

         // Check each row
         .elementByCssSelector("tr.alfresco-documentlibrary-views-layouts-Row:nth-of-type(6)")
         .text()
         .then(function (result6) {
            TestCommon.log(testname,100,"Check the value of a boolean");
            expect(result6).to.equal("No", "Row six should say 'No'");
         })
         .end()

         // Check each row
         .elementByCssSelector("tr.alfresco-documentlibrary-views-layouts-Row:nth-of-type(7)")
         .text()
         .then(function (result7) {
            TestCommon.log(testname,109,"Check the value of a boolean");
            expect(result7).to.equal("No", "Row seven should say 'No'");
         })
         .end()

         // Check each row
         .elementByCssSelector("tr.alfresco-documentlibrary-views-layouts-Row:nth-of-type(8)")
         .text()
         .then(function (result8) {
            TestCommon.log(testname,118,"Check the value of a boolean");
            expect(result8).to.equal("No", "Row eight should say 'No'");
         })
         .end()

         // Check each row
         .elementByCssSelector("tr.alfresco-documentlibrary-views-layouts-Row:nth-of-type(9)")
         .text()
         .then(function (result9) {
            TestCommon.log(testname,127,"Check the value of a boolean");
            expect(result9).to.equal("Unknown", "Row nine should say 'Unknown'");
         })
         .end()

         // Check each row
         .elementByCssSelector("tr.alfresco-documentlibrary-views-layouts-Row:nth-of-type(10)")
         .text()
         .then(function (result10) {
            TestCommon.log(testname,136,"Check the value of a boolean");
            expect(result10).to.equal("Unknown", "Row ten should say 'Unknown'");
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});