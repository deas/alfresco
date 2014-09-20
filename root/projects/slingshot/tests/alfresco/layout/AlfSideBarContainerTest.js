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
        'intern/dojo/node!fs'], 
        function (registerSuite, expect, assert, require, TestCommon, fs) {

   registerSuite({
      name: 'AlfSideBarContainer Test',
      'AlfSideBarContainer Test': function () {

         var browser = this.remote;
         var testname = "AlfSideBarContainer Test";
         var startSize;
         return TestCommon.loadTestWebScript(this.remote, "/AlfSideBarContainer", testname)

         // Test that the sidedbar container requests user preferences...
         .findByCssSelector(TestCommon.pubDataCssSelector("ALF_PREFERENCE_GET", "preference", "org.alfresco.share.sideBarWidth"))
            .then(
               function(element) {
                  TestCommon.log(testname, "Check user preferences requested...");
               },
               function(err) {
                  //browser.takeScreenshot().then(function(data) {fs.writeFileSync("AlfSideBarContainer.png", data, 'base64');}).end();
                  assert(false, "Test #1a - User preferences were not requested");
               })
            .end()

         // Find the resize handle
         .findByCssSelector(".yui-resize-handle.yui-resize-handle-r")
            .then(
               function(element) {
                  TestCommon.log(testname, "Looking for resize handle...");
               },
               function(err) {
                  assert(false, "Test #1b - Couldn't find resize handle");
               })
            .end()

         // Check that the logo widgets have been placed in the correct positions...
         .findByCssSelector(".alfresco-layout-AlfSideBarContainer .sidebar #SIDEBAR_LOGO")
            .then(
               function(element) {
                  TestCommon.log(testname, "Checking for logo in sidebar...");
               },
               function(err) {
                  assert(false, "Test #1c - Sidebar logo wasn't placed correctly");
               })
            .end()

         .findByCssSelector(".alfresco-layout-AlfSideBarContainer .main #MAIN_LOGO")
            .then(
               function(element) {
                  TestCommon.log(testname, "Checking for logo in main panel...");
               },
               function() {
                  assert(false, "Test #1d - Main logo wasn't placed correctly");
               })
            .end()

         // Check the initial width...
         .findByCssSelector(".alfresco-layout-AlfSideBarContainer .sidebar")
            .getComputedStyle("width")
            .then(function(width) {
               TestCommon.log(testname, "Checking width initialised correctly...");
               assert(width == "150px", "Test #1e - The sidebar width wasn't initialised correctly");
            })
            .end()

         // TODO: Need to click a button to simulate receiving a width preference - however, the actual
         //       widget needs to be updated to handle this (at the moment it expects a callback).

         // Hide the sidebar...
         .findByCssSelector(".yui-resize-handle > .yui-resize-handle-inner-r")
            .click()
            .end()

         .findByCssSelector(".alfresco-layout-AlfSideBarContainer .sidebar")
            .getComputedStyle("width")
            .then(function(width) {
               TestCommon.log(testname, "Checking sidebar was hidden...");
               assert(width == "9px", "Test #2a - The sidebar wasn't hidden via the bar control");
            })
            .end()

         // Show the sidebar...
         .findByCssSelector(".yui-resize-handle > .yui-resize-handle-inner-r")
            .click()
            .end()

         .findByCssSelector(".alfresco-layout-AlfSideBarContainer .sidebar")
            .getComputedStyle("width")
            .then(function(width) {
               TestCommon.log(testname, "Checking sidebar was revealed...");
               assert(width == "150px", "Test #2b - The sidebar wasn't shown via the bar control");
            })
            .end()

         // Hide the sidebar (via pub/sub)...
         .findByCssSelector("#HIDE_BUTTON")
            .click()
            .end()

         .findByCssSelector(".alfresco-layout-AlfSideBarContainer .sidebar")
            .getComputedStyle("width")
            .then(function(width) {
               TestCommon.log(testname, "Checking sidebar was hidden (using control)...");
               assert(width == "9px", "Test #3a - The sidebar wasn't hidden via the bar control");
            })
            .end()

         // Show the sidebar (via pub/sub)...
         .findByCssSelector("#SHOW_BUTTON")
            .click()
            .end()

         .findByCssSelector(".alfresco-layout-AlfSideBarContainer .sidebar")
            .getComputedStyle("width")
            .then(function(width) {
               TestCommon.log(testname, "Checking sidebar was revealed (using control)...");
               assert(width == "150px", "Test #3b - The sidebar wasn't shown via the bar control");
            })
            .end()

         // Perform a resize
         .findById("yui-gen0")
            .getSize()
            .then(function(size) {
               startSize = size;
            })
            .end()

         .findById("yui-gen1")
            .then(function(element) {
               browser.moveMouseTo(element);
            })
            .pressMouseButton()
            .moveMouseTo(200, 0)
            .releaseMouseButton()
            .end()

         .findById("yui-gen0")
            .getSize()
            .then(function(endSize) {
               TestCommon.log(testname, "Checking sizes after drag...");
               expect(endSize.width).to.be.at.least(startSize.width, "Test #4a - The sidebar did not resize on the x axis");
               expect(endSize.height).to.equal(startSize.height, "Test #4b - The sidebar should not have resized on the y axis");
            })
            .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});