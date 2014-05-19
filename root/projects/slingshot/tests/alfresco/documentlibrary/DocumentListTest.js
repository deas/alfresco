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
      name: 'AlfDocumentList Test',
      'AlfSearchListTest': function () {
         var browser = this.remote;
         var testname = "AlfDocumentListTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/documentlibrary/page_models/DocumentList_TestPage.json", testname)

         .end()
         
         // 1. Check that the initial request for data is correct...
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "path", "/"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #1a - 'path' not initialised correctly");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "type", "all"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #1b - not initialised to show folders");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "site", "fake-site"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #1c - 'site' not initialised correctly");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "container", "documentlibrary"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #1d - 'container' not initialised correctly");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "sortAscending", "false"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #1e - 'sortAscending' not initialised correctly");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "sortField", "cm:title"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #1f - 'sortField' not initialised correctly");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "page", "1"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #1g - 'page' not initialised correctly");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "pageSize", "3"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #1h - 'pageSize' not initialised correctly");
            })
            .end()

         // 2. Change the sort order...
         .elementByCss("#SORT_ASC_REQUEST")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "sortAscending", "true"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #2a - 'sortAscending' not updated correctly");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "sortField", "cm:name"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #2b - 'sortField' not updated correctly");
            })
            .end()

         // 3. Change sort field
         .elementByCss("#SORT_FIELD_SELECTION")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "sortAscending", "true"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #3a - 'sortAscending' changed unexpectedly");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "sortField", "cm:title"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #3b - 'sortField' not updated correctly");
            })
            .end()

         // 4. Change the sort order again (to descending this time)...
         .elementByCss("#SORT_DESC_REQUEST")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "sortAscending", "false"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #4a - 'sortAscending' not updated correctly");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "sortField", "cm:title"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #4b - 'sortField' changed unexpectedly");
            })
            .end()

         // 5. Hide folders...
         .elementByCss("#HIDE_FOLDERS")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "type", "documents"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #5 - 'type' not updated correctly");
            })
            .end()

         // 6. Show folders...
         .elementByCss("#SHOW_FOLDERS")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "type", "all"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #6 - 'type' not updated correctly");
            })
            .end()

         // 7. Set page...
         .elementByCss("#SET_PAGE")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "page", "2"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #7 - 'page' not updated correctly");
            })
            .end()

         .elementByCss("#PUBLISH_DATA")
            .moveTo()
            .click()
            .end()

         // 8. Check the first view is displayed...
        .elementsByCss(".alfresco-documentlibrary-views-layouts-AlfDocumentListView .alfresco-documentlibrary-views-layouts-Cell span.alfresco-renderers-Property:nth-child(1)")
            .then(function(elements) {
               assert(elements.length == 3, "Test #8a - 'VIEW1' was not displayed");
            })
            .end()
        .elementsByCss(".alfresco-documentlibrary-views-layouts-AlfDocumentListView .alfresco-documentlibrary-views-layouts-Cell span.alfresco-renderers-Property:nth-child(2)")
            .then(function(elements) {
               assert(elements.length == 0, "Test #8b - 'VIEW2' was displayed unexpectedly");
            })
            .end()
         .elementByCss("#CHANGE_VIEW")
            .moveTo()
            .click()
            .end()
        .elementsByCss(".alfresco-documentlibrary-views-layouts-AlfDocumentListView .alfresco-documentlibrary-views-layouts-Cell span.alfresco-renderers-Property:nth-child(2)")
            .then(function(elements) {
               assert(elements.length == 3, "Test #8c - 'VIEW2' was not displayed");
            })
            .end()

         // 9. Change the page size
         .elementByCss("#SET_DOCS_PER_PAGE")
            .moveTo()
            .click()
            .end()
        .elementsByCss(TestCommon.pubSubDataCssSelector("last", "page", "1"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #9a - 'page' not didn't get reset to 1 to account for new page size");
            })
            .end()
        .elementsByCss(TestCommon.pubSubDataCssSelector("last", "pageSize", "6"))
            .then(function(elements) {
               assert(elements.length == 1, "Test #9b - 'pageSize' not updated correctly");
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