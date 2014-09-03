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
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, expect, require, TestCommon, specialKeys) {

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
            TestCommon.log(testname,48,"Check basic toggle label");
            expect(initialValue).to.equal("Off", "The inital label of the basic toggle was not correct: " + initialValue);
         })
         .end()

         .findById("MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
         .getVisibleText()
         .then(function (initialValue) {
            TestCommon.log(testname,56,"Check custom toggle label");
            expect(initialValue).to.equal("On (Custom Label)", "The inital label of the custom toggle was not correct: " + initialValue);
         })
         .end()

         .findAllById("MENU_BAR_SELECT_WITH_ICON_text")
         .then(function(result) {
            TestCommon.log(testname,63,"Check label for icon toggle");
            expect(result).to.have.length(0, "Label for icon toggle was displayed and it shouldn't be");
         })
         .end()

         .hasElementByCss("#MENU_BAR_TOGGLE_WITH_ICON > img.alf-sort-descending-icon")
         .then(function(result) {
            TestCommon.log(testname,70,"Check css classes of icon toggle image");
            expect(result).to.equal(true, "Image for icon toggle had wrong or missing CSS class");
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

         .hasElementByCss(TestCommon.topicSelector("ALF_WIDGETS_READY", "publish", "last"))
         .then(function(result) {
            TestCommon.log(testname,97,"Check basic toggle does not publish when clicked");
            expect(result).to.equal(true, "Mouse selection of basic toggle published unexpectedly (although it has no publishTopic)");
         })
         .end()

         .findById("BASIC_MENU_BAR_TOGGLE_text")
         .getVisibleText()
         .then(function(resultText) {
            TestCommon.log(testname,105,"Check basic toggle text is changed after mouse click");
            expect(resultText).to.equal("On", "The label was not updated after toggle by mouse: " + resultText);
         })
         .end()

         .findById("MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
         .click()
         .end()

         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "value", "OFF"))
         .then(function(result) {
            TestCommon.log(testname,117,"Check custom toggle publishes value correctly");
            expect(result).to.equal(true, "Mouse selection of custom toggle didn't publish value correctly");
         })
         .end()

         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "clicked", "TOGGLE_WITH_LABEL"))
         .then(function(result) {
            TestCommon.log(testname,124,"Check custom toggle publishes id correctly");
            expect(result).to.equal(true, "Mouse selection of custom toggle didn't publish id correctly");
         })
         .end()

         .findByCssSelector("#MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
         .getVisibleText()
         .then(function(resultText) {
            TestCommon.log(testname,132,"Check custom toggle label changes after mouse click");
            expect(resultText).to.equal("Off (Custom Label)", "The label was not updated after toggle by mouse: " + resultText);
         })
         .end()

         .findByCssSelector("#MENU_BAR_TOGGLE_WITH_ICON > img")
         .click()
         .end()

         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "value", "ON"))
         .then(function(result) {
            TestCommon.log(testname,144,"Check icon toggle publishes value correctly");
            expect(result).to.equal(true, "Mouse selection of icon toggle didn't publish value correctly");
         })
         .end()

         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "clicked", "TOGGLE_WITH_ICON"))
         .then(function(result) {
            TestCommon.log(testname,151,"Check icon toggle publishes id correctly");
            expect(result).to.equal(true, "Mouse selection of icon toggle didn't publish id correctly");
         })
         .end()

         .hasElementByCss("#MENU_BAR_TOGGLE_WITH_ICON > img.alf-sort-ascending-icon")
         .then(function(result) {
            TestCommon.log(testname,158,"Check image for icon toggle has correct css");
            expect(result).to.equal(true, "Image for icon toggle had wrong or missing CSS class after update by mouse");
         })

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

         .pressKeys(specialKeys.Tab)
         .pressKeys(specialKeys["Space"])
         .end()

         .hasElementByCss(TestCommon.topicSelector("ALF_WIDGETS_READY", "publish", "last"))
         .then(function(result) {
            TestCommon.log(testname,183,"Check basic toggle does not publish when selected by keyboard");
            expect(result).to.equal(true, "Keyboard selection of basic toggle published unexpectedly (although it has no publishTopic)");
         })
         .end()

         .findById("BASIC_MENU_BAR_TOGGLE_text")
         .getVisibleText()
         .then(function(resultText) {
            TestCommon.log(testname,191,"Check basic toggle text is changed after keyboard selection");
            expect(resultText).to.equal("On", "The label was not updated after toggle by keyboard: " + resultText);
         })
         .end()

         .pressKeys(specialKeys["Right arrow"])
         .pressKeys(specialKeys["Return"])
         .end()

         .hasElementByCss(TestCommon.topicSelector("CLICK", "publish", "last"))
         .then(function(result) {
            TestCommon.log(testname,202,"Check keyboard selection of custom toggle publishes as expected");
            expect(result).to.equal(true, "Keyboard selection of custom toggle didn't publish topic as expected");
         })
         .end()

         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "value", "OFF"))
         .then(function(result) {
            TestCommon.log(testname,209,"Check keyboard selection of custom toggle publishes value correctly");
            expect(result).to.equal(true, "Keyboard selection of custom toggle didn't publish value correctly");
         })
         .end()

         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "clicked", "TOGGLE_WITH_LABEL"))
         .then(function(result) {
            TestCommon.log(testname,216,"Check keyboard selection of custom toggle publishes id correctly");
            expect(result).to.equal(true, "Keyboard selection of custom toggle didn't publish id correctly");
         })
         .end()

         .findById("MENU_BAR_TOGGLE_CUSTOM_LABEL_text")
         .getVisibleText()
         .then(function(resultText) {
            TestCommon.log(testname,224,"Check custom toggle text is changed after keyboard selection");
            expect(resultText).to.equal("Off (Custom Label)", "The custom toggle label was not updated after toggle by keyboard: " + resultText);
         })
         .end()

         .pressKeys(specialKeys["Right arrow"])
         .pressKeys(specialKeys["Return"])

         .hasElementByCss(TestCommon.topicSelector("CLICK", "publish", "last"))
         .then(function(result) {
            TestCommon.log(testname,234,"Check keyboard selection of icon toggle publishes as expected");
            expect(result).to.equal(true, "Keyboard selection of icon toggle didn't publish topic as expected");
         })
         .end()

         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "value", "ON"))
         .then(function(result) {
            TestCommon.log(testname,241,"Check keyboard selection of icon toggle publishes value correctly");
            expect(result).to.equal(true, "Keyboard selection of icon toggle didn't publish value correctly");
         })
         .end()

         .hasElementByCss(TestCommon.pubSubDataCssSelector("last", "clicked", "TOGGLE_WITH_ICON"))
         .then(function(result) {
            TestCommon.log(testname,248,"Check keyboard selection of icon toggle publishes id correctly");
            expect(result).to.equal(true, "Keyboard selection of icon toggle didn't publish id correctly");
         })
         .end()

         .hasElementByCss("#MENU_BAR_TOGGLE_WITH_ICON > img.alf-sort-ascending-icon")
         .then(function(result) {
            TestCommon.log(testname,255,"Check icon toggle css is changed after keyboard toggle");
            expect(result).to.equal(true, "Image for icon toggle had wrong or missing CSS class after keyboard toggle");
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

         .hasElementByCss("#MENU_BAR_TOGGLE_WITH_ICON > img.alf-sort-ascending-icon")
         .then(function(result) {
            TestCommon.log(testname,298,"Check css classes of icon toggle image asc");
            expect(result).to.equal(true, "Image for asc icon toggle had wrong or missing CSS class");
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

         .hasElementByCss("#MENU_BAR_TOGGLE_WITH_ICON > img.alf-sort-descending-icon")
         .then(function(result) {
            TestCommon.log(testname,326,"Check css classes of icon toggle image desc");
            expect(result).to.equal(true, "Image for desc icon toggle had wrong or missing CSS class");
         })
         .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });

      }

   });
});