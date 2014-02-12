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
 * This registers the test suites for the alfresco/menus package.
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
      name: 'Basic Menus Test',
      'Menus Test': function () {

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/menus/page_models/BasicMenuTestPage.json")
            // Test #1 
            // Open the drop-down menu and select the FIRST menut item using the space bar...
            .keys(specialKeys.Tab)
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("3", "item", "MENU_ITEM_1"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_1 after Test #1");
            })

            // Test #2
            // Open the drop-down menu and select the SECOND menu item using the return key...
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Down arrow"])
            .sleep(1000)
            .keys(specialKeys["Return"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("4", "item", "MENU_ITEM_2"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_2 in Test #2");
            })

            // Test #3
            // Open the menu and select the first item in the SECOND group (tests cross-group navigation)...
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Down arrow"])
            .sleep(1000)
            .keys(specialKeys["Return"])
            .end()
            .hasElementByCss(TestCommon.pubSubDataCssSelector("5", "item", "MENU_ITEM_3"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_3 in Test #3");
            })

            // Test #4
            // Test cross group navigation both up and down groups...
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Up arrow"])
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("6", "item", "MENU_ITEM_2"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_2 in Test #4");
            })

            // Test #5
            // Test going from first item in first group to last item in last group...
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Up arrow"])
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("7", "item", "MENU_ITEM_6"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_6 in Test #5");
            })

            // Test #6
            // Test going from the last item in the last group to the first item in the first group...
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Up arrow"])
            .keys(specialKeys["Down arrow"])
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("8", "item", "MENU_ITEM_1"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_1 in Test #6");
            })

            // Test #7
            // Test going along the menu bar (the menu bar should already have focus)...
            .keys(specialKeys["Right arrow"])
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("9", "url", "MENU_BAR_ITEM_1"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_BAR_ITEM_1 in Test #7");
            })

            // Test #8
            // Test navigating between UNGROUPED menu items in a drop down menu...
            // (Moving to the menu will open it and have the first item selected)
            .keys(specialKeys["Right arrow"])
            .keys(specialKeys["Down arrow"])
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("10", "url", "MENU_ITEM_8"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_8 in Test #8");
            })
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Up arrow"])
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("11", "url", "MENU_ITEM_7"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_7 in Test #8");
            })

            // Test #9
            // Test cascade menu keyboard navigation (opening and closing cascades)...
            .keys(specialKeys["Right arrow"])
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Down arrow"]) // Go past and back to cascade
            .keys(specialKeys["Up arrow"])
            .keys(specialKeys["Right arrow"]) // Open the cascade
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("12", "item", "MENU_ITEM_11"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_11 in Test #9");
            })

            // Test #10
            // Test opening cascades within cascades...
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Right arrow"]) // Open the FIRST cascade
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Right arrow"]) // Open the SECOND cascade
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("13", "item", "MENU_ITEM_13"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_13 in Test #10");
            })

            // Test #11
            // Test closing cascades
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Right arrow"]) // Open the cascade
            .keys(specialKeys["Left arrow"])  // Close the cascade 
            .keys(specialKeys["Down arrow"])  // Select the next menu item
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("14", "item", "MENU_ITEM_14"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_14 in Test #11");
            })

            // Test #12
            // Test menu item wrapper navigation (e.g. that you can navigate over non-menu items)
            .keys(specialKeys["Right arrow"])
            .keys(specialKeys["Down arrow"]) // This should jump over the logo widget inserted into the menu
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("15", "item", "MENU_ITEM_10"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_10 in Test #12");
            })

            // Test #13
            // Test menu item wrapper navigation (e.g. that you can navigate back up over non-menu items)
            .keys(specialKeys["Down arrow"])
            .keys(specialKeys["Down arrow"]) // This should jump over the logo widget inserted into the menu
            .keys(specialKeys["Up arrow"]) // This should jump over the logo widget inserted into the menu
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("16", "item", "MENU_ITEM_9"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_9 in Test #13");
            })

            // Test #14
            // Test right cursor wrapping on menu...
            .keys(specialKeys["Right arrow"])
            .keys(specialKeys["Down arrow"])
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("17", "item", "MENU_ITEM_2"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_2 in Test #14");
            })

            // Test #15
            // Test left cursor wrapping on menu...
            .keys(specialKeys["Left arrow"])
            .keys(specialKeys["Down arrow"])
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.pubSubDataCssSelector("18", "item", "MENU_ITEM_10"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_10 in Test #15");
            })

            // Test #16
            // Test drop-down menu using the mouse...
            .elementByCss("#DROP_DOWN_MENU_1")
               .moveTo()
               .click()
               .end()
            .elementByCss("#MENU_ITEM_1")
               .moveTo()
               .click()
               .end()
            .hasElementByCss(TestCommon.pubSubDataCssSelector("19", "item", "MENU_ITEM_1"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_1 in Test #16");
            })

            // Test #17
            // Test cascade menus using the mouse...
            .elementByCss("#DROP_DOWN_MENU_3")
               .moveTo()
               .click()
               .end()
            .elementByCss("#CASCADING_MENU_1")
               .moveTo()
               .click()
               .end()
            .elementByCss("#CASCADING_MENU_2")
               .moveTo()
               .click()
               .end()
            .elementByCss("#MENU_ITEM_13")
               .moveTo()
               .click()
               .end()
            .hasElementByCss(TestCommon.pubSubDataCssSelector("20", "item", "MENU_ITEM_13"))
            .then(function(result) {
               assert(result == true, "Could not find MENU_ITEM_13 in Test #17");
            })

            // Post the coverage results...
            .then(function() {
               TestCommon.postCoverageResults(browser);
            });
      }
   });
});