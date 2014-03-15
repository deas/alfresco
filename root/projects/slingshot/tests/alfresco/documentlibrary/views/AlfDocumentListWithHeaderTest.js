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
        "intern/chai!assert",
        "intern/chai!expect",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!wd/lib/special-keys"], 
        function (registerSuite, assert, expect, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'AlfDocumentListWithHeaderView',
      'Keyboard Tests': function () {

         var alfPause = 150;
         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/documentlibrary/views/page_models/AlfDocumentListWithHeader_TestPage.json")
            // .end()

            // Sort on the first column header...
            .keys(specialKeys.Tab)
            .sleep(alfPause)
            .active()
            .text()
            .then(function(resultText) {
               console.log("Test #1a - Check tab focus on column header 1");
               expect(resultText).to.equal("Column 1", "The text is incorrect");
            })
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "value", "col1"))
            .then(function(result) {
               console.log("Test #1b - Check sort on column header 1");
               assert(result == true, "Could not request to sort column 1 in PubSubLog");
            })
            .end()

            // Sort on the second column header...
            .keys(specialKeys.Tab)
            .sleep(alfPause)
            .keys(specialKeys["Return"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "value", "col2"))
            .then(function(result) {
               console.log("Test #1c - Check tab focus on column header 2");
               assert(result == true, "Could not request to sort column 1 in PubSubLog");
            })
            .end()

            // Check that sort request doesn't occur for third column...
            .keys(specialKeys.Tab)
            .sleep(alfPause)
            .keys(specialKeys["Return"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "value", "col2"))
            .then(function(result) {
               console.log("Test #1d - Check sort on column header 2");
               assert(result == true, "Could not request to sort column 1 in PubSubLog");
            })
            .end()

            // // Go back to the previous header cell and sort in the opposite direction...
            .keys([specialKeys.Shift,specialKeys.Tab])
            .sleep(alfPause)

            // Check it is currently sorted ascendinging...
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "direction", "ascending"))
            .then(function(result) {
               console.log("Test #1e - Check initial sort direction on column header 2");
               assert(result == true, "The initial sort direction is not ascending");
            })
            // Now change the sort direction...
            .keys(specialKeys["Return"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "direction", "descending"))
            .then(function(result) {
               console.log("Test #1f - Check reversed sort direction on column header 2");
               assert(result == true, "The second sort direction is not descending");
            })

            // Now go to the table itself...
            .keys(specialKeys.Shift) // Need to remove shift...
            .sleep(alfPause)
            .keys(specialKeys.Tab)
            .sleep(alfPause)
            .keys(specialKeys.Tab)
            .sleep(alfPause)

            // Should now be on the first row, tab to focus on the first cell...
            .keys(specialKeys.Tab)
            .sleep(alfPause)

            .active()
            .text()
            .then(function(resultText) {
               console.log("Test #1g - Check row 1 selection");
               expect(resultText).to.equal("A", "The text is incorrect");
            })

            // Use the cursor keys to go to the next line...
            .keys(specialKeys['Down arrow'])
            .sleep(alfPause)

            // Select the first element...
            .keys(specialKeys.Tab)
            .sleep(alfPause)
            .active()
            .text()
            .then(function(resultText) {
               console.log("Test #1h - Check cursor down moves to next row");
               expect(resultText).to.equal("D", "The text is incorrect");
            })
            .end()

            // Use the cursor keys to wrap back to the first row...
            .keys(specialKeys['Down arrow'])
            .sleep(alfPause)
            .keys(specialKeys['Down arrow'])
            .sleep(alfPause)
            .keys(specialKeys['Down arrow'])
            .sleep(alfPause)

            // Select the first element...
            .keys(specialKeys.Tab)
            .sleep(alfPause)
            .active()
            .text()
            .then(function(resultText) {
               console.log("Test #1i - Check cursor down wraps to first row");
               expect(resultText).to.equal("A", "The text is incorrect");
            })
            .end()

            // Use the up cursor to wrap back to the last element...
            .keys(specialKeys['Up arrow'])
            .sleep(alfPause)

            // Select the first element...
            .keys(specialKeys.Tab)
            .sleep(alfPause)
            .active()
            .text()
            .then(function(resultText) {
               console.log("Test #1j - Check cursor up wraps to last row");
               expect(resultText).to.equal("J", "The text is incorrect");
            })
            .end()

            // Use the up cursor to go to the third row
            .keys(specialKeys['Up arrow'])
            .sleep(alfPause)

            // Select the first element...
            .keys(specialKeys.Tab)
            .sleep(alfPause)
            .active()
            .text()
            .then(function(resultText) {
               console.log("Test #1k - Check cursor up moves to previous row");
               expect(resultText).to.equal("G", "The text is incorrect");
            })
            .end()

            // Post the coverage results...
            .then(function() {
               TestCommon.postCoverageResults(browser);
            })
            .end();
      },
      'Mouse Tests': function () {
         var alfPause = 150;
         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/documentlibrary/views/page_models/AlfDocumentListWithHeader_TestPage.json")
            .end()

            .hasElementByCss("#COLUMN1_HEADER > span")
            .then(function(result) {
               console.log("Test #2a - Check column header");
               assert(result == true, "Could not find COLUMN1_HEADER in Test #2a");
            })
            .end()
            .elementByCss("#COLUMN1_HEADER > span")
               .moveTo()
               .click()
               .end()
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "value", "col1"))
            .then(function(result) {
               console.log("Test #2b");
               assert(result == true, "Could not request to sort column 1 via mouse");
            })
            
            // Post the coverage results...
            .then(function() {
               TestCommon.postCoverageResults(browser);
            })
            .end();
      }
   });
});