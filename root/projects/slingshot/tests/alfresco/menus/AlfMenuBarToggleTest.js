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
 * This is the unit test for the alfresco/menus/AlfMenuBarSelect widget.
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
      name: 'AlfMenuBarToggle Test',
      'alfresco/menus/AlfMenuBarToggle': function () {

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/menus/page_models/AlfMenuBarToggle_TestPage.json")

            // Test #1
            // Check the initial labels are correctly displayed...
            .end()
            .elementByCss("#BASIC_MENU_BAR_TOGGLE_text")
            .text()
            .then(function(resultText) {
               assert(resultText == "Off", "Test #1a - The inital label of the basic toggle was not correct: " + resultText);
            })
            .end()
            .elementByCss("#MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
            .text()
            .then(function(resultText) {
               assert(resultText == "On (Custom Label)", "Test #1b - The inital label of the custom toggle was not correct: " + resultText);
            })
            .end()
            .hasElementByCss("#MENU_BAR_SELECT_WITH_ICON_text")
            .then(function(result) {
               assert(result == false, "Test #1c - Label for icon toggle was displayed and it shouldn't be");
            })
            .end()
            .hasElementByCss("#MENU_BAR_TOGGLE_WITH_ICON>img.alf-sort-descending-icon")
            .then(function(result) {
               assert(result == true, "Test #1d - Image for icon toggle had wrong or missing CSS class");
            })

            // Test #2 - Check the result of clicking and keyboard driving the basic toggle...
            // Check the labels change but no publications occur...
            .end()
            .keys(specialKeys["Tab"])
            .sleep(1000)
            .keys(specialKeys["Space"])
            .hasElementByCss(TestCommon.topicSelector("ALF_WIDGETS_READY", "publish", "last"))
            .then(function(result) {
               assert(result == true, "Test #2a - Keyboard selection of basic toggle published unexpectedly (although it has no publishTopic)");
            })
            .elementByCss("#BASIC_MENU_BAR_TOGGLE_text")
            .text()
            .then(function(resultText) {
               assert(resultText == "On", "Test #2b - The label was not updated after toggle by keyboard: " + resultText);
            })
            .end()
            .elementByCss("#BASIC_MENU_BAR_TOGGLE_text")
               .moveTo()
               .click()
               .end()
            .hasElementByCss(TestCommon.topicSelector("ALF_WIDGETS_READY", "publish", "last"))
            .then(function(result) {
               assert(result == true, "Test #2c - Mouse selection of basic toggle published unexpectedly (although it has no publishTopic)");
            })
            .elementByCss("#BASIC_MENU_BAR_TOGGLE_text")
            .text()
            .then(function(resultText) {
               assert(resultText == "Off", "Test #2d - The label was not updated after toggle by mouse: " + resultText);
            })
            .end()

            // Test #3 - Check the result of clicking and keyboard driving the custom toggle...
            // Check the labels change but and publications occur...
            .end()
            .keys(specialKeys["Right arrow"])
            .sleep(1000)
            .keys(specialKeys["Return"])
            .hasElementByCss(TestCommon.topicSelector("CLICK", "publish", "last"))
            .then(function(result) {
               assert(result == true, "Test #3a - Keyboard selection of custom toggle didn't publish topic as expected");
            })
            .end()
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "value", "OFF"))
            .then(function(result) {
               assert(result == true, "Test #3b - Keyboard selection of custom toggle didn't publish value correctly");
            })
            .end()
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "clicked", "TOGGLE_WITH_LABEL"))
            .then(function(result) {
               assert(result == true, "Test #3c - Keyboard selection of custom toggle didn't publish id correctly");
            })
            .end()
            .elementByCss("#MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
            .text()
            .then(function(resultText) {
               assert(resultText == "Off (Custom Label)", "Test #3d - The label was not updated after toggle by keyboard: " + resultText);
            })
            .end()
            .elementByCss("#MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
               .moveTo()
               .click()
               .end()
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "value", "ON"))
            .then(function(result) {
               assert(result == true, "Test #3e - Mouse selection of custom toggle didn't publish value correctly");
            })
            .end()
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "clicked", "TOGGLE_WITH_LABEL"))
            .then(function(result) {
               assert(result == true, "Test #3f - Mouse selection of custom toggle didn't publish id correctly");
            })
            .end()
            .elementByCss("#MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
            .text()
            .then(function(resultText) {
               assert(resultText == "On (Custom Label)", "Test #3g - The label was not updated after toggle by mouse: " + resultText);
            })
            .end()

            // Test #4 - Check the result of clicking and keyboard driving the icon toggle...
            .end()
            .keys(specialKeys["Right arrow"])
            .sleep(1000)
            .keys(specialKeys["Return"])
            .hasElementByCss(TestCommon.topicSelector("CLICK", "publish", "last"))
            .then(function(result) {
               assert(result == true, "Test #4a - Keyboard selection of icon toggle didn't publish topic as expected");
            })
            .end()
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "value", "ON"))
            .then(function(result) {
               assert(result == true, "Test #4b - Keyboard selection of icon toggle didn't publish value correctly");
            })
            .end()
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "clicked", "TOGGLE_WITH_ICON"))
            .then(function(result) {
               assert(result == true, "Test #4c - Keyboard selection of icon toggle didn't publish id correctly");
            })
            .end()
            .hasElementByCss("#MENU_BAR_TOGGLE_WITH_ICON>img.alf-sort-ascending-icon")
            .then(function(result) {
               assert(result == true, "Test #4d - Image for icon toggle had wrong or missing CSS class after keyboard toggle");
            })
            .end()
            .elementByCss("#MENU_BAR_TOGGLE_WITH_ICON>img.alf-sort-ascending-icon")
               .moveTo()
               .click()
               .end()
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "value", "OFF"))
            .then(function(result) {
               assert(result == true, "Test #4e - Mouse selection of icon toggle didn't publish value correctly");
            })
            .end()
            .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "clicked", "TOGGLE_WITH_ICON"))
            .then(function(result) {
               assert(result == true, "Test #4f - Mouse selection of icon toggle didn't publish id correctly");
            })
            .end()
            .hasElementByCss("#MENU_BAR_TOGGLE_WITH_ICON>img.alf-sort-descending-icon")
            .then(function(result) {
               assert(result == true, "Test #4g - Image for icon toggle had wrong or missing CSS class after update by mouse");
            })

            // Post the coverage results...
            .then(function() {
               TestCommon.postCoverageResults(browser);
            })
            .end();
      }
   });
});