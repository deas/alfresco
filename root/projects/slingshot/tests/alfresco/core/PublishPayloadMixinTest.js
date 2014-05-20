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
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!expect",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!wd/lib/special-keys"], 
        function (registerSuite, expect, assert, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'PublishPayloadMixinTest',
      'publishPayloadMixins': function () {
         var browser = this.remote;
         var testname = "PublishPayloadMixinTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/core/page_models/PublishPayloadMixin_TestPage.json", testname)

         .end()

         // Check that with minimal configuration we still get a payload published
         .elementByCss("#PA_NO_TYPE")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.topicSelector("TOPIC1", "publish", "last"))
            .then(function(elements) {
               TestCommon.log(testname,47,"Check that no search request is used when 'useHash' is enabled");
               assert(elements.length == 1, "Test #1a - Minimal configuration publish failure");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "data1", "value1"))
            .then(function(elements) {
               TestCommon.log(testname,53,"Check that minimal configuration works");
               assert(elements.length == 1, "Test #1b - Minimal config payload failure");
            })
            .end()

         // Check that setting the CONFIGURED type works and that the current item is NOT mixed into the payload...
         .elementByCss("#PA_CONFIGURED")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.topicSelector("TOPIC2", "publish", "last"))
            .then(function(elements) {
               TestCommon.log(testname,65,"Check that CONFIGURED type payload is published");
               assert(elements.length == 1, "Test #2a - CONFIGURED type publish failure");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "data2", "value2"))
            .then(function(elements) {
               TestCommon.log(testname,71,"Check that minimal configuration works");
               assert(elements.length == 1, "Test #2b - CONFIGURED type payload failure");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "mixinData1", "mixinValue1"))
            .then(function(elements) {
               TestCommon.log(testname,77,"Check that CONFIGURED type doesn't mixin current item without explicit config");
               assert(elements.length == 0, "Test #2c - CONFIGURED type payload failure mixed in current item unexpectedly");
            })
            .end()

         // Check that setting the CONFIGURED type works and that the current item IS mixed into the payload...
         .elementByCss("#PA_CONFIGURED_WITH_ITEM_MIXIN")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.topicSelector("TOPIC3", "publish", "last"))
            .then(function(elements) {
               TestCommon.log(testname,89,"Check that CONFIGURED type payload is published");
               assert(elements.length == 1, "Test #3a - CONFIGURED type publish failure");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "data3", "value3"))
            .then(function(elements) {
               TestCommon.log(testname,95,"Check that minimal configuration works");
               assert(elements.length == 1, "Test #3b - CONFIGURED type payload failure");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "mixinData2", "mixinValue2"))
            .then(function(elements) {
               TestCommon.log(testname,101,"Check that CONFIGURED type doesn't mixin current item without explicit config");
               assert(elements.length == 1, "Test #3c - CONFIGURED type payload didn't mixin in current item");
            })
            .end()

         // Check that setting the CURRENT_ITEM type works...
         .elementByCss("#PA_CURRENT_ITEM")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.topicSelector("TOPIC4", "publish", "last"))
            .then(function(elements) {
               TestCommon.log(testname,113,"Check that CURRENT_ITEM type payload is published");
               assert(elements.length == 1, "Test #4a - CURRENT_ITEM type publish failure");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "data4", "value4"))
            .then(function(elements) {
               TestCommon.log(testname,119,"Check that CURRENT_ITEM type doesn't include configured payload");
               assert(elements.length == 0, "Test #4b - CURRENT_ITEM type payload failure");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "mixinData3", "mixinValue3"))
            .then(function(elements) {
               TestCommon.log(testname,125,"Check that CURRENT_ITEM type payload is correct");
               assert(elements.length == 1, "Test #4c - CURRENT_ITEM type was not correct");
            })
            .end()

         // Check that setting the PROCESS type works...
         .elementByCss("#PA_PROCESS")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.topicSelector("TOPIC5", "publish", "last"))
            .then(function(elements) {
               TestCommon.log(testname,137,"Check that PROCESS type payload is published");
               assert(elements.length == 1, "Test #5a - PROCESS type publish failure");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "data5", "prefix_mixinValue4_postfix"))
            .then(function(elements) {
               TestCommon.log(testname,143,"Check that PROCESS is generated correctly");
               assert(elements.length == 1, "Test #5b - PROCESS type payload failure");
            })
            .end()

         // Check that setting the BUILD type works...
         .elementByCss("#PA_BUILD")
            .moveTo()
            .click()
            .end()
         .elementsByCss(TestCommon.topicSelector("TOPIC6", "publish", "last"))
            .then(function(elements) {
               TestCommon.log(testname,155,"Check that BUILD type payload is published");
               assert(elements.length == 1, "Test #6a - BUILD type publish failure");
            })
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "itemData", "mixinValue5"))
            .then(function(elements) {
               TestCommon.log(testname,161,"Check that BUILD type payload was correct");
               assert(elements.length == 1, "Test #6b - BUILD type payload failure");
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