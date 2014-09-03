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
        function (registerSuite, expect, assert, require, TestCommon, keys) {

   registerSuite({
      name: 'AlfSearchListScroll Test',
      'AlfSearchListTest': function () {
         var browser = this.remote,
             testname = "AlfSearchListTest",
             document,
             countResults = function(expected) {
                /* TODO: Mock Search service doesn't work, so no results are returned.
                TestCommon.log(testname, null, "Checking that result count is correct");
                // Get all result rows from DOM
                browser.findAllByCssSelector(".alfresco-search-AlfSearchResult")
                   .then(function(elements) {
                      assert(elements.length === expected, "Counting Result, expected: " + expected + ", found: " + elements.length);
                   })
                   .end();
                */
             },
            scrollToBottom = function() {
               browser.eval("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight))")
            },
            scrollToTop = function() {
               browser.eval("window.scrollTo(0,0)");
            };

         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/documentlibrary/page_models/SearchListScroll_TestPage.json", testname)

         .end()

         // Click the button to set the search term via the hash...
         .then(function(){
         TestCommon.log(testname, null, "Setting search data");
         })
         .findByCssSelector("#SET_MULTIPLE_SEARCH_DATA")
         .click()
         .end()

         // Check search term has been set.
         .findAllByCssSelector(TestCommon.topicSelector("ALF_SET_SEARCH_TERM", "publish", "any"))
         .then(function(elements) {
            TestCommon.log(testname,65,"Check that search request triggered");
            assert(elements.length === 0, "Search not triggered.");
         })
         .end()

         // Count number of results - there should be 25 (Request 1)
         .then(function(){
         countResults(25);
         })

         // Trigger Infinite Scroll.
         .then(function(){
            scrollToBottom();
            scrollToTop();
            scrollToBottom();
         })

         // Check Trottled Scroll event
         .findAllByCssSelector(TestCommon.topicSelector("ALF_EVENTS_SCROLL", "publish", "any"))
         .then(function(elements) {
            TestCommon.log(testname,74,"Checking that ALF scroll event fired.");
            assert(elements.length == 1, "Scroll event didn't fire");
         })
         .end()

         // Check Infinite Scroll Event fired.
         .findAllByCssSelector(TestCommon.topicSelector("ALF_SCROLL_NEAR_BOTTOM", "publish", "any"))
         .then(function(elements) {
            TestCommon.log(testname,74,"Checking that ALF scroll near bottom event fired.");
            assert(elements.length == 1, "Scroll near bottom event didn't fire");
         })
         .end()

         // Count Results. there should be 50. (Request 2)
         .then(function(){
            countResults(50);
         })


         // Scroll Again.
         .then(function(){
            scrollToBottom();
         })

         // Count Results there should be 75 (Request 3)
         .then(function(){
            countResults(75);
         })

         // Facet Results. Check Facet Event
         .findByCssSelector("#APPLY_FACET_FILTER")
         .click()
         .end()

         // Count Results there should be 6 (Request 4)
         .then(function(){
            countResults(6);
         })

         // Scroll to bottom
         .then(function(){
            scrollToBottom();
         })

         // Check no scroll event is triggered.
         .then(function(){
            countResults(6);
         })

         // Retrigger Search results
         .findByCssSelector("#SET_MULTIPLE_SEARCH_DATA")
         .click()
         .end()

         // Count Results. Check 25 Exist.
         .then(function(){
            countResults(25);
         })

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         })
         .end();
      }
   });
});