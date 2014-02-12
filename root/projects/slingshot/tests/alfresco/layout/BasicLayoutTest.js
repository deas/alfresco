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
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!wd/lib/special-keys"], 
        function (registerSuite, assert, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'Basic Layout Test',
      'BasicLayout': function () {

         var testableDimensions = {};

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/layout/page_models/BasicLayouts_TestPage.json")

            // Test #1
            // Check margins in vertical widgets...
            .end()
            .elementByCss("#SURF_LOGO1")
               .getAttribute("style")
               .then(function(style) {
                  assert(style == "margin-top: 10px;", "Test #1a - The style was not set correctly on a vertical widget with top margin: " + style);
               })

            .end()
            .elementByCss("#SURF_LOGO2")
               .getAttribute("style")
               .then(function(style) {
                  assert(style == "margin-bottom: 20px;", "Test #1b - The style was not set correctly on a vertical widget with bottom margin: " + style);
               })

            // Test #2
            // Check empty layouts are rendererd...
            .end()
            .hasElementByCss("#EMPTY_HORIZONTAL")
               .then(function(result) {
                  assert(result == true, "Test #2a - Empty horizontal widgets was not rendered");
               })

            .end()
            .hasElementByCss("#EMPTY_VERTICAL")
               .then(function(result) {
                  assert(result == true, "Test #2b - Empty vertical widgets was not rendered");
               })

            .end()
            .hasElementByCss("#EMPTY_LEFT_AND_RIGHT")
               .then(function(result) {
                  assert(result == true, "Test #2c - Empty left and right widgets was not rendered");
               })

            // Test #3
            // Test left/right alignment of left and right widgets...
            .end()
            .hasElementByCss(".left-widgets #SURF_LOGO3")
               .then(function(result) {
                  assert(result == true, "Test #3a - widget was not left aligned");
               })

            .end()
            .hasElementByCss(".right-widgets #ALFRESCO_LOGO1")
               .then(function(result) {
                  assert(result == true, "Test #3b - logo was not right aligned");
               })

            // Test #4
            // Test size allocation in horizontal widgets...
            .end()
            .elementByCss("#LEVEL2_HORIZONTAL2")
               .getComputedCss("width")
               .then(function(width) {
                  testableDimensions.horiz2 = width.substring(0, width.lastIndexOf("px"));
               })

            .end()
            .elementByCss("#LEVEL2_HORIZONTAL2 > div > div:nth-child(3)")
               .getComputedCss("margin-left")
               .then(function(x) {
                  assert(x == "10px", "Test #4a - The left margin was not set correctly on a horizontal widget: " + x);
               })
               .getComputedCss("margin-right")
               .then(function(x) {
                  assert(x == "20px", "Test #4b - The right margin was not set correctly on a horizontal widget: " + x);
               })
               .getComputedCss("width")
               .then(function(width) {
                  // Calculate what the width should be...
                  // 75% of the the width of the REMAINDER of the horizontal widget parent minus all the margins and the pixel width widget
                  var x = width.substring(0, width.lastIndexOf("px"));
                  var shouldBe = (testableDimensions.horiz2 - 90 - 300) * 0.75;
                  assert(shouldBe == x, "Test #4c - The width was not set correctly by remaining percentage");
               })

            .end()
            .elementByCss("#LEVEL2_HORIZONTAL2 > div > div:nth-child(2)")
               .getComputedCss("width")
               .then(function(width) {
                  assert(width == "300px", "Test #4d - The width was not set correctly by pixels: " + width);
               })

            .end()
            .elementByCss("#LEVEL2_HORIZONTAL3")
               .getComputedCss("width")
               .then(function(width) {
                  testableDimensions.horiz3 = width.substring(0, width.lastIndexOf("px"));
               })

            .end()
            .elementByCss("#LEVEL2_HORIZONTAL3 > div > div:nth-child(1)")
               .getComputedCss("width")
               .then(function(width) {
                  var x = width.substring(0, width.lastIndexOf("px"));
                  var shouldBe = (testableDimensions.horiz3) * 0.5;
                  assert(shouldBe == x, "Test #4e - The width was not set correctly by evenly dividing space");
               })
            .end()
            .elementByCss("#LEVEL2_HORIZONTAL3 > div > div:nth-child(2)")
               .getComputedCss("width")
               .then(function(width) {
                  var x = width.substring(0, width.lastIndexOf("px"));
                  var shouldBe = (testableDimensions.horiz3) * 0.5;
                  assert(shouldBe == x, "Test #4f - The width was not set correctly by evenly dividing space");
               })

            .end()
            .hasElementByCss("#LOGO6")
               .then(function(result) {
                  assert(result == true, "Test #4g - first widget with excess pixels was not rendered");
               })
            .end()
            .hasElementByCss("#LOGO7")
               .then(function(result) {
                  assert(result == true, "Test #4h - second widget with excess pixels was not rendered");
               })
            .end()
            .hasElementByCss("#LOGO8")
               .then(function(result) {
                  assert(result == true, "Test #4i - widget1 with excess pixels was not rendered");
               })

            // Post the coverage results...
            .then(function() {
               TestCommon.postCoverageResults(browser);
            })
            ;
      }
   });
});