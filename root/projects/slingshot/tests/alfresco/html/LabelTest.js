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
        "intern/chai!expect",
        "require",
        "alfresco/TestCommon"], 
        function (registerSuite, expect, require, TestCommon) {

   registerSuite({
      name: 'Label Test',
      'alfresco/html/Label': function () {

         var browser = this.remote;
         var testname = "LabelTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/html/page_models/Label_TestPage.json", testname)

         .end()
         
         // Has label with correct phrase
         .elementById("TEST_LABEL")
         .text()
         .then(function (label) {
            TestCommon.log(testname,47,"Check the label is shown with the correct text to begin with");
            expect(label).to.equal("This is a test label", "The label should contain 'This is a test label'");
         })
         .end()
         
         // Label has appropriate css class
         .hasElementByCss("#TEST_LABEL.bold")
         .then(function (result) {
            TestCommon.log(testname,55,"Check the label has the class 'bold' when rendered");
            expect(result).to.equal(true, "The label should have css class 'bold'");
         })
         .end()
         
         
         // Has subscribed to appropriate topic
         .hasElementByCss(TestCommon.topicSelector("NOT_A_REAL_TOPIC", "subscribe", "any"))
         .then(function (result) {
            TestCommon.log(testname,64,"Check the label has subscribed to topic 'NOT_A_REAL_TOPIC'");
            expect(result).to.equal(true, "The label should have subscribed to topic 'NOT_A_REAL_TOPIC'");
         })
         .end()
         
         // Click button to publish topic
         .elementById("TEST_BUTTON")
         .moveTo()
         .click()
         .end()

         // Has published to appropriate topic
         .hasElementByCss(TestCommon.topicSelector("NOT_A_REAL_TOPIC", "publish", "any"))
         .then(function (result) {
            TestCommon.log(testname,78,"Check the button has published to topic 'NOT_A_REAL_TOPIC'");
            expect(result).to.equal(true, "The button should have published to topic 'NOT_A_REAL_TOPIC'");
         })
         .end()

         // Label copy has changed appropriately
         .elementById("TEST_LABEL")
         .text()
         .then(function (label) {
            TestCommon.log(testname,87,"Check the label is now shown with the text from the topic publish payload");
            expect(label).to.equal("Label is updated", "The label should contain 'Label is updated'");
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});