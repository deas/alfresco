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

   var testname = "Upload Test";
         
   var uploadsSelector = ".alfresco-dialog-AlfDialog .alfresco-upload-AlfUploadDisplay .uploads";
   var successfulUploadsSelector = uploadsSelector + "> .successful table tr";
   var failedUploadsSelector = uploadsSelector + " > .failed table tr";

   var aggProgStatusSelector = uploadsSelector + " .aggregate-progress .percentage";

   var okButtonSelector = ".alfresco-dialog-AlfDialog .footer > span:first-child > span";
   var cancelButtonSelector = ".alfresco-dialog-AlfDialog .footer > span:nth-child(2) > span";

   registerSuite({
      name: 'Upload Tests',
      'Upload Failure': function () {

         // var browser = this.remote;
         return TestCommon.loadTestWebScript(this.remote, "/aikau-upload-failure-unit-test", testname)

         // Simulate providing a zero byte file and check the output...
         .findByCssSelector("#SINGLE_UPLOAD_label")
            .click()
            .end()
         .findAllByCssSelector(failedUploadsSelector)
            .then(function(elements) {
               TestCommon.log(testname, "Checking that there is a failed upload");
               assert(elements.length === 1, "Test #1a - Wrong number of failed uploads, expected 1, found: " + elements.length);
            })
            .end()

         // Close the dialog
         .findByCssSelector(cancelButtonSelector)
            .click()
            .end();
      },
      'Bad File Data': function () {

         // var browser = this.remote;
         return TestCommon.loadTestWebScript(this.remote, "/aikau-upload-unit-test", testname)

         // Simulate providing a zero byte file and check the output...
         .findByCssSelector("#BAD_FILE_DATA_label")
            .click()
            .end()
         .findAllByCssSelector(failedUploadsSelector)
            .then(function(elements) {
               TestCommon.log(testname, "Checking that there is a failed upload");
               assert(elements.length === 1, "Test #1a - Wrong number of failed uploads, expected 1, found: " + elements.length);
            })
            .end()

         // Close the dialog
         .findByCssSelector(cancelButtonSelector)
            .click()
            .end();
      },
      'Single File Upload': function () {
         // Post the coverage results...
         var browser = this.remote;
         return browser.findByCssSelector("#SINGLE_UPLOAD_label")
            .click()
            .end()
         .findAllByCssSelector(failedUploadsSelector)
            .then(function(elements) {
               TestCommon.log(testname, "Checking that there are no failed uploads");
               assert(elements.length === 0, "Test #1a - Wrong number of failed uploads, expected 0, found: " + elements.length);
            })
            .end()
         .findAllByCssSelector(successfulUploadsSelector)
            .then(function(elements) {
               TestCommon.log(testname, "Checking that there is one successful upload");
               assert(elements.length === 1, "Test #1b - Wrong number of successful uploads, expected 1, found: " + elements.length);
            })
            .end()

         .findByCssSelector(aggProgStatusSelector)
            .getVisibleText()
            .then(function(text) {
               TestCommon.log(testname, "Checking the aggregate progress is 100%");
               assert(text === "100%", "Test #1b - The aggregate progress was not 100%: " + text);
            })
            .end()

         // Close the dialog
         .findByCssSelector(okButtonSelector)
            .click()
            .end();
      },
      'No Files Upload': function () {
         // Post the coverage results...
         var browser = this.remote;
         return browser.findByCssSelector("#NO_FILES_UPLOAD_label")
            .click()
            .end()
         .findAllByCssSelector(failedUploadsSelector)
            .then(function(elements) {
               TestCommon.log(testname, "Checking that there are no failed uploads");
               assert(elements.length === 0, "Test #1a - Wrong number of failed uploads, expected 0, found: " + elements.length);
            })
            .end()
         .findAllByCssSelector(successfulUploadsSelector)
            .then(function(elements) {
               TestCommon.log(testname, "Checking that there is one successful upload");
               assert(elements.length === 0, "Test #1b - Wrong number of successful uploads, expected 0, found: " + elements.length);
            })
            .end()

         // Close the dialog
         .findByCssSelector(okButtonSelector)
            .click()
            .end();
      },
      'Multi-File Upload': function () {
         // Post the coverage results...
         var browser = this.remote;
         return browser.findByCssSelector("#MULTI_UPLOAD_label")
            .click()
            .end()
         .findAllByCssSelector(failedUploadsSelector)
            .then(function(elements) {
               TestCommon.log(testname, "Checking that there are no failed uploads");
               assert(elements.length === 0, "Test #1a - Wrong number of failed uploads, expected 0, found: " + elements.length);
            })
            .end()
         .findAllByCssSelector(successfulUploadsSelector)
            .then(function(elements) {
               TestCommon.log(testname, "Checking that there is one successful upload");
               assert(elements.length === 4, "Test #1b - Wrong number of successful uploads, expected 4, found: " + elements.length);
            })
            .end()

         .findByCssSelector(aggProgStatusSelector)
            .getVisibleText()
            .then(function(text) {
               TestCommon.log(testname, "Checking the aggregate progress is 100%");
               assert(text === "100%", "Test #1b - The aggregate progress was not 100%: " + text);
            })
            .end()

         // Close the dialog
         .findByCssSelector(okButtonSelector)
            .click()
            .end().alfPostCoverageResults(browser);
      }
   });
});