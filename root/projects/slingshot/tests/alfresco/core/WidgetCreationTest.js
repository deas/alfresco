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
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, expect, assert, require, TestCommon, keys) {

   registerSuite({
      name: 'Widget Creation Test',
      'WidgetCreation': function () {
         var browser = this.remote;
         var testname = "WidgetCreationTest";
         return TestCommon.loadTestWebScript(this.remote, "/WidgetCreation", testname)

         // This isn't the optimal way of testing this - ideally we want to get each widget and then
         // check the IDs - however, it's not easily understood how to do this with mulitple selection and 
         // chaining of promises - this test should be sufficient but it would be nice to update at some
         // point in the future!

         .findAllByCssSelector(".alfresco-logo-Logo")
            .then(function (els) {
               TestCommon.log(testname,"Count the number of Logo widgets");
               assert(els.length == 3, "An unexpected number of logo widgets found", els.length);
            })
         .end()

         .findAllByCssSelector("#SPECIFIC_DOM_ID")
            .then(function (els) {
               TestCommon.log(testname,"Check for the Logo with the specific ID");
               assert(els.length == 1, "Couldn't find Logo with specific DOM id", els.length);
            })
         .end()
         .findAllByCssSelector("#SPECIFIC_DOM_ID")
            .then(function (els) {
               TestCommon.log(testname,"Check for the Logo with the overridden ID");
               assert(els.length == 1, "Couldn't find Logo with overridden DOM id", els.length);
            })
         .end()

         .alfPostCoverageResults(browser);
      }
   });
});