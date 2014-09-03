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
 * The purpose of this test is to ensure that keyboard accessibility is possible between the header and the 
 * main table. It should be possible to use the tab/shift-tab keys to navigate along the headers (and the enter/space key
 * to make requests for sorting) and then the cursor keys to navigate around the table itself.
 * 
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!expect",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, expect, assert, require, TestCommon, keys) {

   registerSuite({
      name: 'AlfMenuBarToggle Test',
      'Basic tests': function () {

         var browser = this.remote;
         var testname = "AlfMenuBarToggle Test - Basic tests";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/menus/page_models/AlfMenuBarToggle_TestPage.json")

         .end()

         // Check the initial labels are correctly displayed...
         .findById("BASIC_MENU_BAR_TOGGLE_text")
            .getVisibleText()
            .then(function (initialValue) {
               expect(initialValue).to.equal("Off", "The inital label of the basic toggle was not correct: " + initialValue);
            })
            .end()

         .findById("MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
            .getVisibleText()
            .then(function (initialValue) {
               expect(initialValue).to.equal("On (Custom Label)", "The inital label of the custom toggle was not correct: " + initialValue);
            })
            .end()

         .findAllByCssSelector("#MENU_BAR_SELECT_WITH_ICON_text")
            .then(function(result) {
               expect(result).to.have.length(0, "Label for icon toggle was displayed and it shouldn't be");
            })
            .end()

         .findByCssSelector("#MENU_BAR_TOGGLE_WITH_ICON > img.alf-sort-descending-icon")
            .then(null, function() {
               assert(false, "Image for icon toggle had wrong or missing CSS class");
            })
            .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });

      },

      'Mouse tests': function () {

         var browser = this.remote;
         var testname = "AlfMenuBarToggle Test - Mouse tests";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/menus/page_models/AlfMenuBarToggle_TestPage.json")

         .end()
         
         .findById("BASIC_MENU_BAR_TOGGLE_text")
            .click()
            .end()

         .findByCssSelector(TestCommon.topicSelector("ALF_WIDGETS_READY", "publish", "last"))
            .then(null, function() {
               assert(false, "Mouse selection of basic toggle published unexpectedly (although it has no publishTopic)");
            })
            .end()

         .findById("BASIC_MENU_BAR_TOGGLE_text")
            .getVisibleText()
            .then(function(resultText) {
               expect(resultText).to.equal("On", "The label was not updated after toggle by mouse: " + resultText);
            })
            .end()

         .findById("MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
            .click()
            .end()

         .findByCssSelector(TestCommon.pubSubDataCssSelector("last", "value", "OFF"))
            .then(null, function() {
               assert(false, "Mouse selection of custom toggle didn't publish value correctly");
            })
            .end()

         .findByCssSelector(TestCommon.pubSubDataCssSelector("last", "clicked", "TOGGLE_WITH_LABEL"))
            .then(null, function() {
               assert(false, "Mouse selection of custom toggle didn't publish id correctly");
            })
            .end()

         .findByCssSelector("#MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
            .getVisibleText()
            .then(function(resultText) {
               expect(resultText).to.equal("Off (Custom Label)", "The label was not updated after toggle by mouse: " + resultText);
            })
            .end()

         .findByCssSelector("#MENU_BAR_TOGGLE_WITH_ICON > img")
            .click()
            .end()

         .findByCssSelector(TestCommon.pubSubDataCssSelector("last", "value", "ON"))
            .then(null, function() {
               assert(false, "Mouse selection of icon toggle didn't publish value correctly");
            })
            .end()

         .findByCssSelector(TestCommon.pubSubDataCssSelector("last", "clicked", "TOGGLE_WITH_ICON"))
            .then(null, function() {
               assert(false, "Mouse selection of icon toggle didn't publish id correctly");
            })
            .end()

         .findByCssSelector("#MENU_BAR_TOGGLE_WITH_ICON > img.alf-sort-ascending-icon")
            .then(null, function() {
               assert(false, "Image for icon toggle had wrong or missing CSS class after update by mouse");
            })
            .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });

      },

      'Keyboard tests': function () {

         var browser = this.remote;
         var testname = "AlfMenuBarToggle Test - Keyboard tests";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/menus/page_models/AlfMenuBarToggle_TestPage.json")

         .end()

         .pressKeys(keys.TAB)
         .pressKeys(keys.SPACE)
         .end()

         .findByCssSelector(TestCommon.topicSelector("ALF_WIDGETS_READY", "publish", "last"))
            .then(null, function() {
               assert(false, "Keyboard selection of basic toggle published unexpectedly (although it has no publishTopic)");
            })
            .end()

         .findById("BASIC_MENU_BAR_TOGGLE_text")
            .getVisibleText()
            .then(function(resultText) {
               TestCommon.log(testname,191,"Check basic toggle text is changed after keyboard selection");
               expect(resultText).to.equal("On", "The label was not updated after toggle by keyboard: " + resultText);
            })
            .end()

         .pressKeys(keys.ARROW_RIGHT)
         .pressKeys(keys.RETURN)
         .end()

         .findByCssSelector(TestCommon.topicSelector("CLICK", "publish", "last"))
            .then(null, function() {
               assert(false, "Keyboard selection of custom toggle didn't publish topic as expected");
            })
            .end()

         .findByCssSelector(TestCommon.pubSubDataCssSelector("last", "value", "OFF"))
            .then(null, function() {
               assert(false, "Keyboard selection of custom toggle didn't publish value correctly");
            })
            .end()

         .findByCssSelector(TestCommon.pubSubDataCssSelector("last", "clicked", "TOGGLE_WITH_LABEL"))
            .then(null, function() {
               assert(false, "Keyboard selection of custom toggle didn't publish id correctly");
            })
            .end()

         .findById("MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
            .getVisibleText()
            .then(function(resultText) {
               TestCommon.log(testname,224,"Check custom toggle text is changed after keyboard selection");
               expect(resultText).to.equal("Off (Custom Label)", "The custom toggle label was not updated after toggle by keyboard: " + resultText);
            })
            .end()

         .pressKeys(keys.ARROW_RIGHT)
         .pressKeys(keys.RETURN)

         .findByCssSelector(TestCommon.topicSelector("CLICK", "publish", "last"))
            .then(null, function() {
               assert(false, "Keyboard selection of icon toggle didn't publish topic as expected");
            })
            .end()

         .findByCssSelector(TestCommon.pubSubDataCssSelector("last", "value", "ON"))
            .then(null, function() {
               assert(false, "Keyboard selection of icon toggle didn't publish value correctly");
            })
            .end()

         .findByCssSelector(TestCommon.pubSubDataCssSelector("last", "clicked", "TOGGLE_WITH_ICON"))
            .then(null, function() {
               assert(false, "Keyboard selection of icon toggle didn't publish id correctly");
            })
            .end()

         .findByCssSelector("#MENU_BAR_TOGGLE_WITH_ICON > img.alf-sort-ascending-icon")
            .then(null, function() {
               assert(false, "Image for icon toggle had wrong or missing CSS class after keyboard toggle");
            })
            .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });

      },

      'Set state tests': function () {

         var browser = this.remote;
         var testname = "AlfMenuBarToggle Test - Set state tests";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/menus/page_models/AlfMenuBarToggle_TestPage.json")

         .end()

         .findById("TEST_BUTTON_ASC")
            .click()
            .end()

         .findById("BASIC_MENU_BAR_TOGGLE_text")
            .getVisibleText()
            .then(function (initialValue) {
               TestCommon.log(testname,283,"Check basic toggle label asc");
               expect(initialValue).to.equal("On", "The asc label of the basic toggle was not correct: " + initialValue);
            })
            .end()

         .findById("MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
            .getVisibleText()
            .then(function (initialValue) {
               TestCommon.log(testname,291,"Check custom toggle label asc");
               expect(initialValue).to.equal("On (Custom Label)", "The asc label of the custom toggle was not correct: " + initialValue);
            })
            .end()

         .findByCssSelector("#MENU_BAR_TOGGLE_WITH_ICON > img.alf-sort-ascending-icon")
            .then(null, function() {
               assert(false, "Image for asc icon toggle had wrong or missing CSS class");
            })
            .end()

         .findById("TEST_BUTTON_DESC")
            .click()
            .end()

         .findById("BASIC_MENU_BAR_TOGGLE_text")
            .getVisibleText()
            .then(function (initialValue) {
               TestCommon.log(testname,311,"Check basic toggle label desc");
               expect(initialValue).to.equal("Off", "The desc label of the basic toggle was not correct: " + initialValue);
            })
            .end()

         .findById("MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
            .getVisibleText()
            .then(function (initialValue) {
               TestCommon.log(testname,319,"Check custom toggle label desc");
               expect(initialValue).to.equal("Off (Custom Label)", "The desc label of the custom toggle was not correct: " + initialValue);
            })
            .end()

         .findByCssSelector("#MENU_BAR_TOGGLE_WITH_ICON > img.alf-sort-descending-icon")
            .then(null, function() {
               assert(false, "Image for desc icon toggle had wrong or missing CSS class");
            })
            .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });

      }

   });
});