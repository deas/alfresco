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
      name: 'FacetFilters Test',
      'alfresco/search/FacetFilters': function () {

         var browser = this.remote;
         var testname = "FacetFiltersTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/search/page_models/FacetFilters_TestPage.json")

         .end()

         // Check no facets are shown to begin with
         .elementById("FACET1")
         .text()
         .then(function (initialValue) {
            TestCommon.log(testname,47,"Check no facets are shown to begin with");
            expect(initialValue).to.equal("Facet 1", "The only text shown should be 'Facet 1'");
         })
         .end()

         .elementsByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
         .then(function (rows) {
            TestCommon.log(testname,54,"Check no facet rows are shown to begin with");
            expect(rows).to.have.length(0, "There should be no visible rows in the facet display");
         })
         .end()

         // Click button 1 - 4 rows of facet data should appear
         .elementById("DO_FACET_BUTTON_1")
         .moveTo()
         .sleep(500)
         .click()
         .end()

         .elementsByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
         .then(function (rows) {
            TestCommon.log(testname,68,"Check facets are shown after clicking button 1");
            expect(rows).to.have.length(4, "There should be 4 rows in the facet display");
         })
         .end()

         // Check the facet values
         .elementById("FACET1")
         .text()
         .then(function (facets) {
            TestCommon.log(testname,77,"Check the first set of facets have appeared");
            expect(facets).to.contain("result 1", "Facets should contain 'result 1'");
            expect(facets).to.contain("result 2", "Facets should contain 'result 2'");
            expect(facets).to.contain("result 3", "Facets should contain 'result 3'");
            expect(facets).to.contain("result 4", "Facets should contain 'result 4'");
         })
         .end()

         // Click button 2 - 2 rows of facet data should appear
         .elementById("DO_FACET_BUTTON_2")
         .moveTo()
         .sleep(500)
         .click()
         .end()

         .elementsByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
         .then(function (rows) {
            TestCommon.log(testname,94,"Check facets are shown after clicking button 2");
            expect(rows).to.have.length(2, "There should be 2 rows in the facet display");
         })
         .end()

         // Check the facet values
         .elementById("FACET1")
         .text()
         .then(function (facets) {
            TestCommon.log(testname,103,"Check the second set of facets have appeared");
            expect(facets).to.contain("result 5", "Facets should contain 'result 5'");
            expect(facets).to.contain("result 6", "Facets should contain 'result 6'");
         })
         .end()

         // Click button 3 - 4 rows of facet data should appear
         .elementById("DO_FACET_BUTTON_3")
         .moveTo()
         .sleep(500)
         .click()
         .end()

         .elementsByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
         .then(function (rows) {
            TestCommon.log(testname,118,"Check facets are shown after clicking button 3");
            expect(rows).to.have.length(6, "There should be 6 rows in the facet display");
         })
         .end()

         // Check the facet values
         .elementById("FACET1")
         .text()
         .then(function (facets) {
            TestCommon.log(testname,127,"Check the third set of facets have appeared");
            expect(facets).to.contain("result 7", "Facets should contain 'result 7'");
            expect(facets).to.contain("result 8", "Facets should contain 'result 8'");
            expect(facets).to.contain("result 9", "Facets should contain 'result 9'");
            expect(facets).to.contain("result 10", "Facets should contain 'result 10'");
            expect(facets).to.contain("result 11", "Facets should contain 'result 11'");
            expect(facets).to.contain("More choices", "Facets should contain 'More choices'");
            expect(facets).to.not.contain("result 12", "Facets should not contain 'result 12'");
         })
         .end()

         // Click the more choices button
         .elementByCss("li.showMore")
         .moveTo()
         .sleep(500)
         .click()
         .end()

         // Check the facet values
         .elementById("FACET1")
         .text()
         .then(function (facets) {
            TestCommon.log(testname,149,"Check the four set of facets are shown");
            expect(facets).to.contain("result 7", "Facets should contain 'result 7'");
            expect(facets).to.contain("result 8", "Facets should contain 'result 8'");
            expect(facets).to.contain("result 9", "Facets should contain 'result 9'");
            expect(facets).to.contain("result 10", "Facets should contain 'result 10'");
            expect(facets).to.contain("result 11", "Facets should contain 'result 11'");
            expect(facets).to.contain("Less choices", "Facets should contain 'Less choices'");
            expect(facets).to.contain("result 12", "Facets should contain 'result 12'");
         })
         .end()

         // Click the less choices button
         .elementByCss("li.showLess")
         .moveTo()
         .sleep(500)
         .click()
         .end()

         // Check the facet values
         .elementById("FACET1")
         .text()
         .then(function (facets) {
            TestCommon.log(testname,171,"Check the fifth set of facets are shown");
            expect(facets).to.contain("result 7", "Facets should contain 'result 7'");
            expect(facets).to.contain("result 8", "Facets should contain 'result 8'");
            expect(facets).to.contain("result 9", "Facets should contain 'result 9'");
            expect(facets).to.contain("result 10", "Facets should contain 'result 10'");
            expect(facets).to.contain("result 11", "Facets should contain 'result 11'");
            expect(facets).to.contain("More choices", "Facets should contain 'More choices'");
            expect(facets).to.not.contain("result 12", "Facets should not contain 'result 12'");
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});