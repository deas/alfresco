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
 * 
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!wd/lib/special-keys"], 
        function (registerSuite, assert, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'Document Picker Test',
      'alfresco/forms/controls/DocumentPicker': function () {

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/forms/controls/page_models/DocumentPicker_TestPage.json")
            .end()

            // Open the dialog to select items...
            .elementByCss("#DOCUMENT_PICKER .alfresco-layout-VerticalWidgets > span > span > span")
               .moveTo()
               .click()
               .end()

            // Check the picker is displayed...
            .hasElementByCss(".alfresco-pickers-Picker")
               .then(function(result) {
                  console.log("Test 1a");
                  assert(result == true, "Test #1a - The dialog has NOT opened with the picker");
               })
               .end()

            // Select "Shared Files" (the results for this are mocked)
            .elementByCss(".alfresco-pickers-Picker .sub-pickers > div:first-child .dijitMenuItem:nth-child(2)")
               .moveTo()
               .click()
               .end()

            // Check that a new results set are shown...
            .hasElementByCss(".alfresco-documentlibrary-AlfDocumentList")
               .then(function(result) {
                  console.log("Test 1b");
                  assert(result == true, "Test #1b - The Shared Files click did not yield any results");
               })
               .end()

            // Cound the mocked results...
            .elementsByCss(".alfresco-documentlibrary-AlfDocumentList .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr")
               .then(function(elements) {
                  console.log("Test 1c");
                  assert(elements.length == 4, "Test #1c - 4 results expected for Shared Files");
               })
               .end()

            // Check the first item has an ADD publish action image...
            // TODO: This could be more specific, e.g. to check the actual image source?
            .hasElementByCss(".alfresco-documentlibrary-AlfDocumentList .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr:nth-child(1) .alfresco-renderers-PublishAction img")
               .then(function(result) {
                  console.log("Test 1d");
                  assert(result == true, "Test #1d - The first shared files item did not have an ADD publish action image");
               })
               .end()

            // Click the ADD publish action image to add the item to the picked items...
            .elementByCss(".alfresco-documentlibrary-AlfDocumentList .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr:nth-child(1) .alfresco-renderers-PublishAction img")
               .moveTo()
               .click()
               .end()

            // Count the number of picked items (there should now be 1)...
            // TODO: Could probably check that there were none picked when the dialog was first opened...
            .elementsByCss(".picked-items tr")
               .then(function(elements) {
                  console.log("Test 1e");
                  assert(elements.length == 1, "Test #1e - Only 1 results was expected for picked items");
               })
               .end()

            // Close the dialog...
            .elementByCss(".alfresco-dialog-AlfDialog .footer .confirmation span.dijitButtonText")
               .moveTo()
               .click()
               .end()
            
            // Count the selected items...
            .elementsByCss("#DOCUMENT_PICKER .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr")
               .then(function(elements) {
                  console.log("Test 1f");
                  assert(elements.length == 1, "Test #1f - Only 1 results was expected for picked items after dialog close");
               })
               .end()

            .hasElementByCss("#DOCUMENT_PICKER .alfresco-layout-VerticalWidgets > span.alfresco-buttons-AlfButton.confirmationButton  > span > span")
               .then(function(result) {
                  console.log("Found element? " + result);
                  
               })
               .end()

            // Open the dialog again and check the picked items remain...
            .elementByCss("#DOCUMENT_PICKER .alfresco-layout-VerticalWidgets > span.alfresco-buttons-AlfButton.confirmationButton  > span > span")
               .moveTo()
               .sleep(1000)
               .click()
               .end()

            .elementsByCss(".picked-items tr")
               .then(function(elements) {
                  console.log("Test 1g");
                  assert(elements.length == 1, "Test #1g - The previously selected item was not preserved");
               })
               .end()

            // Check the remove item image exists...
            .hasElementByCss(".picked-items tr .alfresco-renderers-PublishAction img")
               .then(function(result) {
                  console.log("Test 1h");
                  assert(result == true, "Test #1h - The remove item image could not be found");
               })
               .end()

            // Remove the previously selected item...
            .elementByCss(".picked-items tr .alfresco-renderers-PublishAction img")
               .moveTo()
               .click()
               .end()

            // Close the dialog...
            .elementByCss(".alfresco-dialog-AlfDialog .footer .confirmation span.dijitButtonText")
               .moveTo()
               .click()
               .end()
            
            // Check the item was removed...
            .elementsByCss("#DOCUMENT_PICKER .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr")
               .then(function(elements) {
                  console.log("Test 1i");
                  assert(elements.length == 0, "Test #1i - The previously selected item should have been removed");
               })
               .end()

            // Open the dialog again and add some more...
            .elementByCss("#DOCUMENT_PICKER .alfresco-layout-VerticalWidgets > span > span > span")
               .moveTo()
               .click()
               .end()

            // Select "Shared Files" option again...
            .elementByCss(".alfresco-pickers-Picker .sub-pickers > div:first-child .dijitMenuItem:nth-child(2)")
               .moveTo()
               .click()
               .end()

            // Click the ADD publish action image TWICE, check that it was only added once...
            .elementByCss(".alfresco-documentlibrary-AlfDocumentList .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr:nth-child(2) .alfresco-renderers-PublishAction img")
               .moveTo()
               .click()
               .click()
               .end()

            // Count the number of picked items (there should now be 1 DESPITE clicking twice)...
            .elementsByCss(".picked-items tr")
               .then(function(elements) {
                  console.log("Test 1j");
                  assert(elements.length == 1, "Test #1j - Only 1 results was expected for picked items");
               })
               .end()

            // Add another item...
            .elementByCss(".alfresco-documentlibrary-AlfDocumentList .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr:nth-child(3) .alfresco-renderers-PublishAction img")
               .moveTo()
               .click()
               .end()

            .elementsByCss(".picked-items tr")
               .then(function(elements) {
                  console.log("Test 1k");
                  assert(elements.length == 2, "Test #1k - Two results were expected for picked items");
               })
               .end()

            // Close the dialog...
            .elementByCss(".alfresco-dialog-AlfDialog .footer .confirmation span.dijitButtonText")
               .moveTo()
               .click()
               .end()
            
            // Check there are now 2 items...
            .elementsByCss("#DOCUMENT_PICKER .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr")
               .then(function(elements) {
                  console.log("Test 1l");
                  assert(elements.length == 2, "Test #1l - Two items should have been picked");
               })
               .end()

            // TODO: Check all items can be removed
            // TODO: Check form values can be set and retrieved
            // TODO: Check that initial values can be set
            // TOOD: Check that folders don't have publish action images
            // TODO: Set up controls with invalid data
            // TODO: Click on a folder to get sub-results

            // Post the coverage results...
            .then(function() {
               TestCommon.postCoverageResults(browser);
            })
            .end();
      }
   });
});