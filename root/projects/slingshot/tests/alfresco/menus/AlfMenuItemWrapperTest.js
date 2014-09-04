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
 * This is the unit test for the alfresco/menus/AlfMenuItemWrapper widget.
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
      name: 'AlfMenuItemWrapper Test',
      'alfresco/menus/AlfMenuItemWrapper': function () {

         var browser = this.remote;
         return TestCommon.loadTestWebScript(this.remote, "/AlfMenuItemWrapper")

            // Test #1
            // Check that keyboard navigation works
            .pressKeys(keys.TAB)
            .pressKeys(keys.ARROW_DOWN) // Opens the drop-down
            .pressKeys(keys.ARROW_DOWN) // Skips over log to 2nd button
            .sleep(1000)
            .pressKeys(keys.SPACE)
            .findByCssSelector(TestCommon.topicSelector("CLICKED_BUTTON_2", "publish", "last"))
               .then(null, function() {
                  assert(false, "Test #1a - The wrapped menu item without focus was not skipped on downward keyboard navigation");
               })
               .end()

            .pressKeys(keys.ARROW_UP) // Skips over log to 2nd button
            .sleep(1000)
            .pressKeys(keys.RETURN)
            .findByCssSelector(TestCommon.topicSelector("CLICKED_BUTTON_1", "publish", "last"))
               .then(null, function() {
                  assert(false, "Test #1b - The wrapped menu item without focus was not skipped on upwards keyboard navigation");
               })
               .end()
            // Currently commented out - this works in manual testing but not in Selenium for some reason
            // .end()
            // .findByCssSelector(".alfresco-logo-large")
            //    .click()
            //    .end()
            .pressKeys(keys.ARROW_DOWN)
            .sleep(1000)
            .pressKeys(keys.SPACE)
            .findByCssSelector(TestCommon.topicSelector("CLICKED_BUTTON_2", "publish", "last"))
               .then(null, function() {
                  assert(false, "Test #1c - The wrapped menu item without focus was not navigated away from successfully");
               })
               .end()
            .pressKeys(keys.ARROW_DOWN) // Skips over log to 2nd button
            .sleep(1000)
            .pressKeys(keys.SPACE)
            .findByCssSelector(TestCommon.topicSelector("CLICKED_BUTTON_1", "publish", "last"))
               .then(null, function() {
                  assert(false, "Test #1d - The empty wrapped menu item was not skipped on keyboard navigation");
               })
               .end()

            // Post the coverage results...
            .then(function() {
               TestCommon.postCoverageResults(browser);
            })
            .end();
      }
   });
});