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

   var uploadsSelector = ".alfresco-dialog-AlfDialog .alfresco-upload-AlfUploadDisplay .uploads";
   var failedUploadsSelector = uploadsSelector + " > .failed table tr";

   var okButtonSelector = ".alfresco-dialog-AlfDialog .footer > span:first-child > span";

   registerSuite({
      name: 'Upload Tests',
      'Bad File Data': function () {

         // var browser = this.remote;
         var testname = "Upload Test";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/upload/page_models/Upload_TestPage.json", testname)

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
         .findByCssSelector(okButtonSelector)
            .click()
            .end();
      },
      'Post Code Coverage': function () {
         // Post the coverage results...
         var browser = this.remote;
         return browser.then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});