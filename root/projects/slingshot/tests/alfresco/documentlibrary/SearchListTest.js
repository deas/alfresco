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
      name: 'AlfSearchList Test',
      'AlfSearchListTest': function () {
         var browser = this.remote;
         var testname = "AlfSearchListTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/documentlibrary/page_models/SearchList_TestPage.json")

         .end()
         
         // The initial AlfSearchList doesn't perform an initial search when "useHash" is set to true (which
         // for this test it is).

         // Check that no request to search exists...
         .elementsByCss(TestCommon.topicSelector("ALF_SET_SEARCH_TERM", "publish", "any"))
            .then(function(elements) {
               TestCommon.log(testname,46,"Check that no search request is used when 'useHash' is enabled");
               assert(elements.length == 0, "Test #1 - Search term set unexpectedly");
            })
            .end()

         // Click the button to set the search term via the hash...
         .elementByCss("#SET_SEARCH_TERM")
            .moveTo()
            .click()
            .end()

         // Check that updating the hash results in a search request being made...
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "term", "testTerm1"))
            .then(function(elements) {
               TestCommon.log(testname,59,"Check setting the searchTerm hash attribute requests the appropriate search");
               assert(elements.length == 1, "Test #2 - Search term on hash didn't request search");
            })
            .end()

         // Click the button to set the search term via the hash...
         .elementByCss("#SET_MULTIPLE_SEARCH_DATA")
            .moveTo()
            .click()
            .end()

         // Check that updating the hash results in a search request being made...
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "term", "testTerm2"))
            .then(function(elements) {
               TestCommon.log(testname,74,"Check setting multiple hash attribute requests the appropriate search (search term)");
               assert(elements.length == 1, "Test #3a - search term not set appropriately from hash change");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "facetFields", "facetFieldData"))
            .then(function(elements) {
               TestCommon.log(testname,80,"Check setting multiple hash attribute requests the appropriate search (facet fields)");
               assert(elements.length == 1, "Test #3b - facet fields not set appropriately from hash change");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "filters", "filter1,filter2,filter3"))
            .then(function(elements) {
               TestCommon.log(testname,86,"Check setting multiple hash attribute requests the appropriate search (facet filters)");
               assert(elements.length == 1, "Test #3c - facet filters not set appropriately from hash change");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "sortAscending", "false"))
            .then(function(elements) {
               TestCommon.log(testname,92,"Check setting multiple hash attribute requests the appropriate search (sort order)");
               assert(elements.length == 1, "Test #3d - sort order not set appropriately from hash change");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "sortField", "cm:title"))
            .then(function(elements) {
               TestCommon.log(testname,98,"Check setting multiple hash attribute requests the appropriate search (sort property)");
               assert(elements.length == 1, "Test #3e - sort property not set appropriately from hash change");
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