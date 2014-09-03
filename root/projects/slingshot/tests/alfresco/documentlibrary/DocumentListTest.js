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
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, expect, assert, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'AlfDocumentList Test',
      'AlfSearchListTest': function () {

         var browser = this.remote;
         var testname = "AlfDocumentListTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/documentlibrary/page_models/DocumentList_TestPage.json", testname)

         .end()

         // 1. Check that the initial request for data is correct...
         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "path", "/"))
         .then(function(elements) {
            TestCommon.log(testname,44,"Check 'path' initialised correctly");
            assert(elements.length == 1, "'path' not initialised correctly");
         })
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "type", "all"))
         .then(function(elements) {
            TestCommon.log(testname,51,"Check initialised to show folders");
            assert(elements.length == 1, "not initialised to show folders");
         })
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "site", "fake-site"))
         .then(function(elements) {
            TestCommon.log(testname,58,"Check 'site' initialised correctly");
            assert(elements.length == 1, "'site' not initialised correctly");
         })
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "container", "documentlibrary"))
         .then(function(elements) {
            TestCommon.log(testname,65,"Check 'container' initialised correctly");
            assert(elements.length == 1, "'container' not initialised correctly");
         })
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "sortAscending", "false"))
         .then(function(elements) {
            TestCommon.log(testname,72,"Check 'sortAscending' initialised correctly");
            assert(elements.length == 1, "'sortAscending' not initialised correctly");
         })
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "sortField", "cm:title"))
         .then(function(elements) {
            TestCommon.log(testname,79,"Check 'sortField' initialised correctly");
            assert(elements.length == 1, "'sortField' not initialised correctly");
         })
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "page", "1"))
         .then(function(elements) {
            TestCommon.log(testname,86,"Check 'page' initialised correctly");
            assert(elements.length == 1, "'page' not initialised correctly");
         })
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "pageSize", "3"))
         .then(function(elements) {
            TestCommon.log(testname,93,"Check 'pageSize' initialised correctly");
            assert(elements.length == 1, "'pageSize' not initialised correctly");
         })
         .end()

         // 2. Change the sort order...
         .findByCssSelector("#SORT_ASC_REQUEST")
         .moveTo()
         .click()
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "sortAscending", "true"))
         .then(function(elements) {
            TestCommon.log(testname,106,"Check 'sortAscending' updated correctly");
            assert(elements.length == 1, "'sortAscending' not updated correctly");
         })
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "sortField", "cm:name"))
         .then(function(elements) {
            TestCommon.log(testname,113,"Check 'sortField' updated correctly");
            assert(elements.length == 1, "'sortField' not updated correctly");
         })
         .end()

         // 3. Change sort field
         .findByCssSelector("#SORT_FIELD_SELECTION")
         .moveTo()
         .click()
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "sortAscending", "true"))
         .then(function(elements) {
            TestCommon.log(testname,126,"Check 'sortAscending' has not changed");
            assert(elements.length == 1, "'sortAscending' changed unexpectedly");
         })
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "sortField", "cm:title"))
         .then(function(elements) {
            TestCommon.log(testname,133,"Check 'sortField' updated correctly");
            assert(elements.length == 1, "'sortField' not updated correctly");
         })
         .end()

         // 4. Change the sort order again (to descending this time)...
         .findByCssSelector("#SORT_DESC_REQUEST")
         .moveTo()
         .click()
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "sortAscending", "false"))
         .then(function(elements) {
            TestCommon.log(testname,146,"Check 'sortAscending' updated correctly");
            assert(elements.length == 1, "'sortAscending' not updated correctly");
         })
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "sortField", "cm:title"))
         .then(function(elements) {
            TestCommon.log(testname,153,"Check 'sortField' has not changed");
            assert(elements.length == 1, "'sortField' changed unexpectedly");
         })
         .end()

         // 5. Hide folders...
         .findByCssSelector("#HIDE_FOLDERS")
         .moveTo()
         .click()
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "type", "documents"))
         .then(function(elements) {
            TestCommon.log(testname,166,"Check 'type' updated correctly");
            assert(elements.length == 1, "'type' not updated correctly");
         })
         .end()

         // 6. Show folders...
         .findByCssSelector("#SHOW_FOLDERS")
         .moveTo()
         .click()
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "type", "all"))
         .then(function(elements) {
            TestCommon.log(testname,179,"Check 'type' updated correctly");
            assert(elements.length == 1, "'type' not updated correctly");
         })
         .end()

         // 7. Set page...
         .findByCssSelector("#SET_PAGE")
         .moveTo()
         .click()
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "page", "2"))
         .then(function(elements) {
            TestCommon.log(testname,192,"Check 'page' updated correctly");
            assert(elements.length == 1, "'page' not updated correctly");
         })
         .end()

         .findByCssSelector("#PUBLISH_DATA")
         .moveTo()
         .click()
         .end()

         // 8. Check the first view is displayed...
        .findAllByCssSelector(".alfresco-documentlibrary-views-layouts-AlfDocumentListView .alfresco-documentlibrary-views-layouts-Cell span.alfresco-renderers-Property:nth-child(1)")
         .then(function(elements) {
            TestCommon.log(testname,205,"Check 'VIEW1' was displayed");
            assert(elements.length == 3, "'VIEW1' was not displayed");
         })
         .end()

         .findAllByCssSelector(".alfresco-documentlibrary-views-layouts-AlfDocumentListView .alfresco-documentlibrary-views-layouts-Cell span.alfresco-renderers-Property:nth-child(2)")
         .then(function(elements) {
            TestCommon.log(testname,212,"Check 'VIEW1' was not displayed");
            assert(elements.length == 0, "'VIEW2' was displayed unexpectedly");
         })
         .end()

         .findByCssSelector("#CHANGE_VIEW")
         .moveTo()
         .click()
         .end()

         .findAllByCssSelector(".alfresco-documentlibrary-views-layouts-AlfDocumentListView .alfresco-documentlibrary-views-layouts-Cell span.alfresco-renderers-Property:nth-child(2)")
         .then(function(elements) {
            TestCommon.log(testname,224,"Check 'VIEW2' has displayed");
            assert(elements.length == 3, "'VIEW2' was not displayed");
         })
         .end()

         // 9. Change the page size
         .findByCssSelector("#SET_DOCS_PER_PAGE")
         .moveTo()
         .click()
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "page", "1"))
         .then(function(elements) {
            TestCommon.log(testname,237,"Check 'page' was reset to 1 to account for new page size");
            assert(elements.length == 1, "'page' not didn't get reset to 1 to account for new page size");
         })
         .end()

         .findAllByCssSelector(TestCommon.pubSubDataCssSelector("last", "pageSize", "6"))
         .then(function(elements) {
            TestCommon.log(testname,244,"Check 'pageSize' updated correctly");
            assert(elements.length == 1, "'pageSize' not updated correctly");
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