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
        "alfresco/TestCommon"], 
        function (registerSuite, expect, assert, require, TestCommon) {

   registerSuite({
      name: 'Pagination Test',
      'Paging Test': function () {

         var browser = this.remote;
         var testname = "Pagination Test";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/documentlibrary/page_models/Paginator_TestPage.json", testname)

         // Make sure the page has loaded...
         .findByCssSelector(TestCommon.topicSelector("ALF_WIDGETS_READY", "publish", "any"))
            .end()

         // Check that the hard-coded data renders the paginators initial state correctly...
         .findByCssSelector("#PAGINATOR_PAGE_SELECTOR_text")
            .getVisibleText()
            .then(function(text) {
               assert(text === "1-3 of 3", "Test #1a - Hard coded data didn't yield correct page data: " + text);
            })
            .end()

         // Check that the previous and next controls are disabled...
         .findAllByCssSelector("#PAGINATOR_PAGE_BACK.dijitDisabled")
            .then(function(elements) {
               assert(elements.length === 1, "Test #1b - The previous page button should be disabled");
            })
            .end()
         .findAllByCssSelector("#PAGINATOR_PAGE_FORWARD.dijitDisabled")
            .then(function(elements) {
               assert(elements.length === 1, "Test #1b - The next page button should be disabled");
            })
            .end()

         // Switch to 50 results per page (will load data via Mock XHR request)...
         .findByCssSelector("#PAGINATOR_RESULTS_PER_PAGE_SELECTOR_text")
            .click()
            .sleep(100)
            .end()

         .findByCssSelector("#PAGINATOR_RESULTS_PER_PAGE_SELECTOR_dropdown tr:nth-child(2) td:nth-child(3)")
            .click()
            .end()

         // Wait for the data to load and the page to draw - this is currently slow and the rendering needs to be
         // sped up - hopefully at some point in the future we can remove this sleep!
         .sleep(2000)

         .findByCssSelector("#PAGINATOR_PAGE_SELECTOR_text")
            .getVisibleText()
            .then(function(text) {
               assert(text === "1-50 of 57", "Test #2a - First page not displayed correctly: " + text);
            })
            .end()

          // Check that the previous is still disabled but next is now enabled...
         .findAllByCssSelector("#PAGINATOR_PAGE_BACK.dijitDisabled")
            .then(function(elements) {
               assert(elements.length === 1, "Test #2b - The previous page button should be disabled");
            })
            .end()
         .findAllByCssSelector("#PAGINATOR_PAGE_FORWARD.dijitDisabled")
            .then(function(elements) {
               assert(elements.length === 0, "Test #2c - The next page button should now be enabled");
            })
            .end()

         // Click the next button...
         .findByCssSelector("#PAGINATOR_PAGE_FORWARD_text")
            .click()
            .sleep(1000)
            .end()

         .findByCssSelector("#PAGINATOR_PAGE_SELECTOR_text")
            .getVisibleText()
            .then(function(text) {
               assert(text === "51-57 of 57", "Test #3a - Second page not displayed correctly: " + text);
            })
            .end()

          // Check that the previous is still disabled but next is now enabled...
         .findAllByCssSelector("#PAGINATOR_PAGE_BACK.dijitDisabled")
            .then(function(elements) {
               assert(elements.length === 0, "Test #3b - The previous page button should now be enabled");
            })
            .end()
         .findAllByCssSelector("#PAGINATOR_PAGE_FORWARD.dijitDisabled")
            .then(function(elements) {
               assert(elements.length === 1, "Test #3c - The next page button should now be disabled");
            })
            .end()

         // Click the previous button...
         .findByCssSelector("#PAGINATOR_PAGE_BACK_text")
            .click()
            .sleep(2000)
            .end()

         .findByCssSelector("#PAGINATOR_PAGE_SELECTOR_text")
            .getVisibleText()
            .then(function(text) {
               assert(text === "1-50 of 57", "Test #4a - First page not displayed correctly: " + text);
            })
            .end()

          // Check that the previous is still disabled but next is now enabled...
         .findAllByCssSelector("#PAGINATOR_PAGE_BACK.dijitDisabled")
            .then(function(elements) {
               assert(elements.length === 1, "Test #4b - The previous page button should now be enabled");
            })
            .end()
         .findAllByCssSelector("#PAGINATOR_PAGE_FORWARD.dijitDisabled")
            .then(function(elements) {
               assert(elements.length === 0, "Test #4c - The next page button should now be disabled");
            })
            .end()

         // Select page 2 via the drop down...
         .findByCssSelector("#PAGINATOR_PAGE_SELECTOR_text")
            .click()
            .end()

         .findByCssSelector("#PAGINATOR_PAGE_SELECTOR_dropdown tr:nth-child(2) td:nth-child(3)")
            .click()
            .end()

         .findByCssSelector("#PAGINATOR_PAGE_FORWARD_text")
            .click()
            .sleep(1000)
            .end()

         .findByCssSelector("#PAGINATOR_PAGE_SELECTOR_text")
            .getVisibleText()
            .then(function(text) {
               assert(text === "51-57 of 57", "Test #5a - Second page not displayed correctly: " + text);
            })
            .end()

          // Check that the previous is still disabled but next is now enabled...
         .findAllByCssSelector("#PAGINATOR_PAGE_BACK.dijitDisabled")
            .then(function(elements) {
               assert(elements.length === 0, "Test #5b - The previous page button should now be enabled");
            })
            .end()
         .findAllByCssSelector("#PAGINATOR_PAGE_FORWARD.dijitDisabled")
            .then(function(elements) {
               assert(elements.length === 1, "Test #5c - The next page button should now be disabled");
            })
            .end()

         .findByCssSelector("#MENU_BAR_POPUP_text")
            .click()
            .end()

         .findAllByCssSelector("#MENU_BAR_POPUP_dropdown tr:nth-child(2) td.alf-selected-icon")
            .then(function(elements) {
               assert(elements.length === 1, "Test #6a - Results per page widget updated correctly");
            })
            .end()


         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});