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
 * This is the unit test for the alfresco/menus/AlfMenuItemWrapper widget.
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
      name: 'AlfMenuItemWrapper Test',
      'alfresco/menus/AlfMenuItemWrapper': function () {

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/menus/page_models/AlfMenuItemWrapper_TestPage.json")

            // Test #1
            // Check that keyboard navigation works
            .end()
            .keys(specialKeys["Tab"])
            .keys(specialKeys["Down arrow"]) // Opens the drop-down
            .keys(specialKeys["Down arrow"]) // Skips over log to 2nd button
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.topicSelector("CLICKED_BUTTON_2", "publish", "last"))
            .then(function(result) {
               assert(result == true, "Test #1a - The wrapped menu item without focus was not skipped on downward keyboard navigation");
            })
            .keys(specialKeys["Up arrow"]) // Skips over log to 2nd button
            .sleep(1000)
            .keys(specialKeys["Return"])
            .hasElementByCss(TestCommon.topicSelector("CLICKED_BUTTON_1", "publish", "last"))
            .then(function(result) {
               assert(result == true, "Test #1b - The wrapped menu item without focus was not skipped on upwards keyboard navigation");
            })
            // Currently commented out - this works in manual testing but not in Selenium for some reason
            // .end()
            // .elementByCss(".alfresco-logo-large")
            //    .moveTo()
            //    .click()
            //    .end()
            .keys(specialKeys["Down arrow"])
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.topicSelector("CLICKED_BUTTON_2", "publish", "last"))
            .then(function(result) {
               assert(result == true, "Test #1c - The wrapped menu item without focus was not navigated away from successfully");
            })
            .keys(specialKeys["Down arrow"]) // Skips over log to 2nd button
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.topicSelector("CLICKED_BUTTON_1", "publish", "last"))
            .then(function(result) {
               assert(result == true, "Test #1d - The empty wrapped menu item was not skipped on keyboard navigation");
            })

            // Post the coverage results...
            .then(function() {
               TestCommon.postCoverageResults(browser);
            })
            .end();
      }
   });
});