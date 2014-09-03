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
 * 
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, assert, require, TestCommon, keys) {

   registerSuite({
      name: 'Document Picker Test',
      'alfresco/forms/controls/DocumentPicker': function () {

         var browser = this.remote;
         var testname = "DocumentPickerTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/forms/controls/page_models/DocumentPicker_TestPage.json", testname)

         .end()

         // Open the dialog to select items...
         .findByCssSelector("#DOCUMENT_PICKER .alfresco-layout-VerticalWidgets > span > span > span")
            .click()
            .end()

         // Check the picker is displayed...
         .findByCssSelector(".alfresco-pickers-Picker")
            .then(null, function() {
               assert(false, "The dialog has NOT opened with the picker");
            })
            .end()

         // Select "Shared Files" (the results for this are mocked)
         .findByCssSelector(".alfresco-pickers-Picker .sub-pickers > div:first-child .dijitMenuItem:nth-child(2)")
            .click()
            .end()

         // Check that a new results set are shown...
         .findByCssSelector(".alfresco-documentlibrary-AlfDocumentList")
            .then(null, function() {
               assert(false, "The Shared Files click did not yield any results");
            })
            .end()

         // Count the mocked results...
         .findAllByCssSelector(".alfresco-documentlibrary-AlfDocumentList .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr")
            .then(function(elements) {
               assert(elements.length == 4, "4 results expected for Shared Files");
            })
            .end()

         // Check the first item has an ADD publish action image...
         // TODO: This could be more specific, e.g. to check the actual image source?
         .findByCssSelector(".alfresco-documentlibrary-AlfDocumentList .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr:nth-child(1) .alfresco-renderers-PublishAction img")
            .then(null, function() {
               assert(false, "The first shared files item did not have an ADD publish action image");
            })
            .end()

         // Click the ADD publish action image to add the item to the picked items...
         .findByCssSelector(".alfresco-documentlibrary-AlfDocumentList .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr:nth-child(1) .alfresco-renderers-PublishAction img")
            .click()
            .end()

         // Count the number of picked items (there should now be 1)...
         // TODO: Could probably check that there were none picked when the dialog was first opened...
         .findAllByCssSelector(".picked-items tr")
            .then(function(elements) {
               assert(elements.length == 1, "Only one result was expected for picked items");
            })
            .end()

         // Close the dialog...
         .findByCssSelector(".alfresco-dialog-AlfDialog .footer .confirmation span.dijitButtonText")
            .click()
            .end()
            
         // Count the selected items...
         .findAllByCssSelector("#DOCUMENT_PICKER .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr")
            .then(function(elements) {
               assert(elements.length == 1, "Only 1 results was expected for picked items after dialog close");
            })
            .end()

         .findByCssSelector("#DOCUMENT_PICKER .alfresco-layout-VerticalWidgets > span.alfresco-buttons-AlfButton.confirmationButton > span > span")
            .then(null, function() {
               // TestCommon.log(testname,117,"Have we found the element? " + result);
            })
            .end()

         // Open the dialog again and check the picked items remain...
         .findByCssSelector("#DOCUMENT_PICKER .alfresco-layout-VerticalWidgets > span.alfresco-buttons-AlfButton.confirmationButton  > span > span")
            .click()
            .end()

         .findAllByCssSelector(".picked-items tr")
            .then(function(elements) {
               assert(elements.length == 1, "The previously selected item was not preserved");
            })
            .end()

         // Check the remove item image exists...
         .findByCssSelector(".picked-items tr .alfresco-renderers-PublishAction img")
            .then(null, function() {
               assert(false, "The remove item image could not be found");
            })
            .end()

         // Remove the previously selected item...
         .findByCssSelector(".picked-items tr .alfresco-renderers-PublishAction img")
            .click()
            .end()

         // Close the dialog...
         .findByCssSelector(".alfresco-dialog-AlfDialog .footer .confirmation span.dijitButtonText")
            .click()
            .end()
            
         // Check the item was removed...
         .findAllByCssSelector("#DOCUMENT_PICKER .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr")
            .then(function(elements) {
               assert(elements.length == 0, "The previously selected item should have been removed");
            })
            .end()

         // Open the dialog again and add some more...
         .findByCssSelector("#DOCUMENT_PICKER .alfresco-layout-VerticalWidgets > span > span > span")
            .click()
            .end()

         // Select "Shared Files" option again...
         .findByCssSelector(".alfresco-pickers-Picker .sub-pickers > div:first-child .dijitMenuItem:nth-child(2)")
            .click()
            .end()

         // Click the ADD publish action image TWICE, check that it was only added once...
         .findByCssSelector(".alfresco-documentlibrary-AlfDocumentList .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr:nth-child(2) .alfresco-renderers-PublishAction img")
            .click()
            .click()
            .end()

         // Count the number of picked items (there should now be 1 DESPITE clicking twice)...
         .findAllByCssSelector(".picked-items tr")
            .then(function(elements) {
               assert(elements.length == 1, "Only one results was expected for picked items");
            })
            .end()

         // Add another item...
         .findByCssSelector(".alfresco-documentlibrary-AlfDocumentList .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr:nth-child(3) .alfresco-renderers-PublishAction img")
            .click()
            .end()

         .findAllByCssSelector(".picked-items tr")
            .then(function(elements) {
               assert(elements.length == 2, "Two results were expected for picked items");
            })
            .end()

         // Close the dialog...
         .findByCssSelector(".alfresco-dialog-AlfDialog .footer .confirmation span.dijitButtonText")
            .click()
            .end()
            
         // Check there are now 2 items...
         .findAllByCssSelector("#DOCUMENT_PICKER .alfresco-documentlibrary-views-layouts-AlfDocumentListView tr")
            .then(function(elements) {
               assert(elements.length == 2, "Two items should have been picked");
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
         });
      }
   });
});