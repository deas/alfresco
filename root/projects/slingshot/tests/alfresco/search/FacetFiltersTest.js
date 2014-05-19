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
        "alfresco/TestCommon",
        "intern/dojo/node!wd/lib/special-keys"], 
        function (registerSuite, expect, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'FacetFilters Test',
      'Mouse tests': function () {

         var browser = this.remote;
         var testname = "FacetFiltersTest - Mouse tests";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/search/page_models/FacetFilters_TestPage.json", testname)

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
         .click()
         .end()

         .elementsByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
         .then(function (rows) {
            TestCommon.log(testname,67,"Check facets are shown after clicking button 1");
            expect(rows).to.have.length(4, "There should be 4 rows in the facet display");
         })
         .end()

         // Check the facet values
         .elementById("FACET1")
         .text()
         .then(function (facets) {
            TestCommon.log(testname,76,"Check the first set of facets have appeared");
            expect(facets).to.contain("result 1", "Facets should contain 'result 1'");
            expect(facets).to.contain("result 2", "Facets should contain 'result 2'");
            expect(facets).to.contain("result 3", "Facets should contain 'result 3'");
            expect(facets).to.contain("result 4", "Facets should contain 'result 4'");
         })
         .end()

         // Click button 2 - 2 rows of facet data should appear
         .elementById("DO_FACET_BUTTON_2")
         .moveTo()
         .click()
         .end()

         .elementsByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
         .then(function (rows) {
            TestCommon.log(testname,92,"Check facets are shown after clicking button 2");
            expect(rows).to.have.length(2, "There should be 2 rows in the facet display");
         })
         .end()

         // Check the facet values
         .elementById("FACET1")
         .text()
         .then(function (facets) {
            TestCommon.log(testname,101,"Check the second set of facets have appeared");
            expect(facets).to.contain("result 5", "Facets should contain 'result 5'");
            expect(facets).to.contain("result 6", "Facets should contain 'result 6'");
         })
         .end()

         // Click button 3 - 4 rows of facet data should appear
         .elementById("DO_FACET_BUTTON_3")
         .moveTo()
         .click()
         .end()

         .elementsByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
         .then(function (rows) {
            TestCommon.log(testname,115,"Check facets are shown after clicking button 3");
            expect(rows).to.have.length(6, "There should be 6 rows in the facet display");
         })
         .end()

         // Check the facet values
         .elementById("FACET1")
         .text()
         .then(function (facets) {
            TestCommon.log(testname,124,"Check the third set of facets have appeared");
            expect(facets).to.contain("result 7", "Facets should contain 'result 7'");
            expect(facets).to.contain("result 8", "Facets should contain 'result 8'");
            expect(facets).to.contain("result 9", "Facets should contain 'result 9'");
            expect(facets).to.contain("result 10", "Facets should contain 'result 10'");
            expect(facets).to.contain("result 11", "Facets should contain 'result 11'");
            expect(facets).to.contain("Show More", "Facets should contain 'More choices'");
            expect(facets).to.not.contain("result 12", "Facets should not contain 'result 12'");
         })
         .end()

         // Click the more choices button
         .elementByCss("li.showMore")
         .moveTo()
         .click()
         .end()

         // Check the facet values
         .elementById("FACET1")
         .text()
         .then(function (facets) {
            TestCommon.log(testname,145,"Check the four set of facets are shown");
            expect(facets).to.contain("result 7", "Facets should contain 'result 7'");
            expect(facets).to.contain("result 8", "Facets should contain 'result 8'");
            expect(facets).to.contain("result 9", "Facets should contain 'result 9'");
            expect(facets).to.contain("result 10", "Facets should contain 'result 10'");
            expect(facets).to.contain("result 11", "Facets should contain 'result 11'");
            expect(facets).to.contain("Show Fewer", "Facets should contain 'Less choices'");
            expect(facets).to.contain("result 12", "Facets should contain 'result 12'");
         })
         .end()

         // Click the less choices button
         .elementByCss("li.showLess")
         .moveTo()
         .click()
         .end()

         // Check the facet values
         .elementById("FACET1")
         .text()
         .then(function (facets) {
            TestCommon.log(testname,166,"Check the fifth set of facets are shown");
            expect(facets).to.contain("result 7", "Facets should contain 'result 7'");
            expect(facets).to.contain("result 8", "Facets should contain 'result 8'");
            expect(facets).to.contain("result 9", "Facets should contain 'result 9'");
            expect(facets).to.contain("result 10", "Facets should contain 'result 10'");
            expect(facets).to.contain("result 11", "Facets should contain 'result 11'");
            expect(facets).to.contain("Show More", "Facets should contain 'More choices'");
            expect(facets).to.not.contain("result 12", "Facets should not contain 'result 12'");
         })
         .end()

         // Click the title - the facet menu should disappear
         .elementByCss("#FACET1 > div.label")
         .moveTo()
         .click()
         .end()

         .elementByCssSelector("#FACET1 > ul.filters")
         .isDisplayed()
         .then(function (displayed) {
            TestCommon.log(testname,186,"Check facet menu is hidden when the title is clicked");
            expect(displayed).to.equal(false, "Facet menu should be hidden when the title is clicked");
         })
         .end()

         // Click the title again - the facet menu should reappear
         .elementByCss("#FACET1 > div.label")
         .moveTo()
         .click()
         .end()

         .elementByCssSelector("#FACET1 > ul.filters")
         .isDisplayed()
         .then(function (displayed) {
            TestCommon.log(testname,200,"Check facet menu is shown when the title is clicked again");
            expect(displayed).to.equal(true, "Facet menu should be shown when the title is clicked again");
         })
         .end()

         // Click the first facet menu item - it should select
         .elementByCssSelector("#FACET1 > ul.filters > li:first-of-type span.filterLabel")
         .moveTo()
         .click()
         .end()

         .elementByCssSelector("#FACET1 > ul.filters > li:first-of-type > span.status > span")
         .isDisplayed()
         .then(function (displayed) {
            TestCommon.log(testname,214,"Facet menu item should select when clicked");
            expect(displayed).to.equal(true, "Facet menu item should select when clicked");
         })
         .end()

         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "alfTopic", "ALF_APPLY_FACET_FILTER"))
         .then(function(published) {
            TestCommon.log(testname,221,"Clicking a facet should publish");
            expect(published).to.equal(true, "The facet did not publish on 'ALF_APPLY_FACET_FILTER'");
         })
         .end()

         // Click the first facet menu item again - it should de-select
         .elementByCssSelector("#FACET1 > ul.filters > li:first-of-type span.filterLabel")
         .moveTo()
         .click()
         .end()

         .elementByCssSelector("#FACET1 > ul.filters > li:first-of-type > span.status > span")
         .isDisplayed()
         .then(function (displayed) {
            TestCommon.log(testname,235,"Facet menu item should de-select when clicked again");
            expect(displayed).to.equal(false, "Facet menu item should de-select when clicked again");
         })
         .end()

         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "alfTopic", "ALF_REMOVE_FACET_FILTER"))
         .then(function(published) {
            TestCommon.log(testname,242,"Clicking a facet to deselect should publish");
            expect(published).to.equal(true, "The facet deselection did not publish on 'ALF_REMOVE_FACET_FILTER'");
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      },

      'Keyboard tests': function () {

         var browser = this.remote;
         var testname = "FacetFiltersTest - Keyboard tests";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/search/page_models/FacetFilters_TestPage.json", testname)

         .end()

         // Check no facets are shown to begin with
         .elementById("FACET1")
         .text()
         .then(function (initialValue) {
            TestCommon.log(testname,266,"Check no facets are shown to begin with");
            expect(initialValue).to.equal("Facet 1", "The only text shown should be 'Facet 1'");
         })
         .end()

         .elementsByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
         .then(function (rows) {
            TestCommon.log(testname,273,"Check no facet rows are shown to begin with");
            expect(rows).to.have.length(0, "There should be no visible rows in the facet display");
         })
         .end()

         // 'click' the first button
         .keys(specialKeys.Tab)
         .keys(specialKeys["Return"])
         .end()

         .elementsByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
         .then(function (rows) {
            TestCommon.log(testname,285,"Check facets are shown after selecting button 1 with the keyboard");
            expect(rows).to.have.length(4, "There should be 4 rows in the facet display");
         })
         .end()

         // Move to the facet menu label and 'click' it
         .keys(specialKeys.Tab)
         .keys(specialKeys.Tab)
         .keys(specialKeys.Tab)
         .keys(specialKeys["Return"])
         .end()

         .elementByCssSelector("#FACET1 > ul.filters")
         .isDisplayed()
         .then(function (displayed) {
            TestCommon.log(testname,300,"Check facet menu is hidden when the title is clicked with the keyboard");
            expect(displayed).to.equal(false, "Facet menu should be hidden when the title is clicked using the keyboard");
         })
         .end()

         // 'Click' the menu label again to re-show the menu
         .keys(specialKeys["Return"])
         .end()

         .elementByCssSelector("#FACET1 > ul.filters")
         .isDisplayed()
         .then(function (displayed) {
            TestCommon.log(testname,312,"Check facet menu is displayed when the title is re-clicked with the keyboard");
            expect(displayed).to.equal(true, "Facet menu should be displayed when the title is re-clicked using the keyboard");
         })
         .end()

         // Tab onto the first facet in the menu and 'click' it - it should select
         .keys(specialKeys.Tab)
         .keys(specialKeys["Return"])
         .end()

         .elementByCssSelector("#FACET1 > ul.filters > li:first-of-type > span.status > span")
         .isDisplayed()
         .then(function (displayed) {
            TestCommon.log(testname,325,"Facet menu item should select when clicked with the keyboard");
            expect(displayed).to.equal(true, "Facet menu item should select when clicked using the keyboard");
         })
         .end()

         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "alfTopic", "ALF_APPLY_FACET_FILTER"))
         .then(function(published) {
            TestCommon.log(testname,332,"Clicking a facet with the keyboard should publish");
            expect(published).to.equal(true, "The facet did not publish on 'ALF_APPLY_FACET_FILTER' when clicked with the keyboard");
         })
         .end()

         // 'Click' the first facet menu item again - it should de-select
         .keys(specialKeys["Return"])
         .end()

         .elementByCssSelector("#FACET1 > ul.filters > li:first-of-type > span.status > span")
         .isDisplayed()
         .then(function (displayed) {
            TestCommon.log(testname,344,"Facet menu item should de-select when clicked again using the keyboard");
            expect(displayed).to.equal(false, "Facet menu item should de-select when clicked again using the keyboard");
         })
         .end()

         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "alfTopic", "ALF_REMOVE_FACET_FILTER"))
         .then(function(published) {
            TestCommon.log(testname,351,"Clicking a facet using the keyboard to deselect should publish");
            expect(published).to.equal(true, "The facet deselection using the keyboard did not publish on 'ALF_REMOVE_FACET_FILTER'");
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      },

      'Url hash tests': function () {

         var browser = this.remote;
         var testname = "FacetFiltersTest - Url hash tests";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/search/page_models/FacetFilters_TestPage.json", testname)

         .end()

         // Click button 4 - 3 rows of facet data should appear
         .elementById("DO_FACET_BUTTON_4")
         .moveTo()
         .click()
         .end()

         .elementsByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
         .then(function (rows) {
            TestCommon.log(testname,378,"Check facets are shown after clicking button 4");
            expect(rows).to.have.length(3, "There should be 3 rows in the facet display");
         })
         .end()

         // Click facet1 - check the url hash appears as expected
         .elementByCssSelector("#FACET2 > ul.filters > li:first-of-type span.filterLabel")
         .moveTo()
         .click()
         .end()

         .url()
         .then(function (url) {
            TestCommon.log(testname,391,"Click the first item in the facet menu");
            expect(url).to.contain("FACET2QNAME", "The url hash should contain 'FACET2QNAME'")
               .and.to.contain("facFil1", "The facet click did not write the value 'facFil1' to the url hash as expected");
         })
         .end()

         // Click facet2 - check the url hash appears as expected
         .elementByCssSelector("#FACET2 > ul.filters > li:nth-of-type(2) span.filterLabel")
         .moveTo()
         .click()
         .end()

         .url()
         .then(function (url) {
            TestCommon.log(testname,405,"Click the second item in the facet menu");
            expect(url).to.contain("FACET2QNAME", "The url hash should contain 'FACET2QNAME'")
               .and.to.contain("facFil1", "The url hash should contain 'facFil2'")
               .and.to.contain("facFil2", "The facet click did not add the value 'facFil2' to the url hash as expected");
         })
         .end()

         // Click facet1 - check the url hash appears as expected
         .elementByCssSelector("#FACET2 > ul.filters > li:first-of-type span.filterLabel")
         .moveTo()
         .click()
         .end()

         .url()
         .then(function (url) {
            TestCommon.log(testname,420,"Click the first item in the facet menu again");
            expect(url).to.contain("FACET2QNAME", "The url hash should contain 'FACET2QNAME'")
               .and.to.not.contain("facFil1", "The facet click did not remove the value 'facFil1' from the url hash as expected");
         })
         .end()

         // Click facet2 - check the url hash appears as expected
         .elementByCssSelector("#FACET2 > ul.filters > li:nth-of-type(2) span.filterLabel")
         .moveTo()
         .click()
         .end()

         .url()
         .then(function (url) {
            TestCommon.log(testname,434,"Click the second item in the facet menu again");
            expect(url).to.not.contain("FACET2QNAME", "The url hash should not now contain 'FACET2QNAME'")
               .and.to.not.contain("facFil2", "The facet click did not remove the value 'facFil2' from the url hash as expected");
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});