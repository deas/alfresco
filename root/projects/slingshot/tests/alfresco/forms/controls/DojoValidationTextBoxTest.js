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
      name: 'Validation Text Box Test',
      'alfresco/forms/controls/DojoValidationTextBox': function () {

         var browser = this.remote;
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/forms/controls/page_models/DojoValidationTextBox_TestPage.json")
            // .end()

            // Test #1 
            // Check initial rendering...
            .end()
            .elementByCss("#BASIC .label")
               .text()
               .then(function(resultText) {
                  assert(resultText == "Basic", "Test #1a - The label was not rendered correctly: " + resultText);
               })
               .end()

            .elementByCss("#UNITS_AND_DESCRIPTION .units")
               .text()
               .then(function(resultText) {
                  assert(resultText == "Some unit", "Test #1b - The units was not rendered correctly: " + resultText);
               })
               .end()

            // TODO: Not sure how to test description tooltip rendering - it might not be worth it, as I suspect a re-design will be required

            // Test #2
            // Check initial value that is set...
            .elementByCss("#INITIAL_VALUE1 .dijitInputContainer input")
               .getValue()
               .then(function(resultText) {
                  assert(resultText == "Val1", "Test #2a - The initial value was not set correctly: " + resultText);
               })
               .end()

            // Test #3
            // Check behaviour rules
            // Based on the initial value of "Field1" it is expected that SINGLE_POSITIVE_RULES should be
            // shown and SINGLE_NEGATIVE_RULES should be hidden
            .hasElementByCss("#SINGLE_POSITIVE_RULES")
               .then(function(result) {
                  assert(result == true, "Test #3a - Widget not displayed as expected");
               })
               .end()
            .elementByCss("#SINGLE_NEGATIVE_RULES")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "none", "Test #3b - Widget displayed unexpectedly");
               })
               .end()

            // ...and the requirement indicator should be displayed for POSITIVE and hidden for NEGATIVE
            .elementByCss("#SINGLE_POSITIVE_RULES span.requirementIndicator")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "block", "Test #3c - Requirement indicator not displayed as expected");
               })
               .end()
            .elementByCss("#SINGLE_NEGATIVE_RULES span.requirementIndicator")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "none", "Test #3d - Requirement indicator displayed unexpectedly");
               })
               .end()

            // ...and the field should be enabled for POSITIVE and disabled for NEGATIVE...
            .elementByCss("#SINGLE_POSITIVE_RULES .dijitInputContainer input")
               .getAttribute("disabled")
               .then(function(result) {
                  assert(result == "true", "Test #3e - Field should have been disabled");
               })
               .end()

            .elementByCss("#SINGLE_NEGATIVE_RULES .dijitInputContainer input")
               .getAttribute("disabled")
               .then(function(result) {
                  assert(result == null, "Test #3f - Field should have been enabled");
               })
               .end()

            // When Field1 is updated the field should become visible...
            .elementByCss("#INITIAL_VALUE1 .dijitInputContainer input")
               .type(specialKeys["Back space"])
               .end()
            .elementByCss("#SINGLE_POSITIVE_RULES")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "none", "Test #3d - Widget displayed unexpectedly after processing positive rules");
               })
               .end()
            .hasElementByCss("#SINGLE_NEGATIVE_RULES")
               .then(function(result) {
                  assert(result == true, "Test #3e - Widget not displayed as expected after processing negative rules");
               })
               .end()

            // When Field2 is updated the field should become required...
            .elementByCss("#INITIAL_VALUE2 .dijitInputContainer input")
               .type(specialKeys["Back space"])
               .end()
            .elementByCss("#SINGLE_NEGATIVE_RULES span.requirementIndicator")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "block", "Test #3f - Requirement indicator not displayed as expected");
               })
               .end()
            .elementByCss("#SINGLE_POSITIVE_RULES span.requirementIndicator")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "none", "Test #3g - Requirement indicator displayed unexpectedly");
               })
               .end()

            // When Field3 is updated the field should become disabled...
            .elementByCss("#INITIAL_VALUE3 .dijitInputContainer input")
               .type(specialKeys["Back space"])
               .end()
            .elementByCss("#SINGLE_NEGATIVE_RULES .dijitInputContainer input")
               .getAttribute("disabled")
               .then(function(result) {
                  assert(result == "true", "Test #3h - Field should have been disabled");
               })
               .end()
            .elementByCss("#SINGLE_POSITIVE_RULES .dijitInputContainer input")
               .getAttribute("disabled")
               .then(function(result) {
                  assert(result == null, "Test #3i - Field should have been enabled");
               })
               .end()

            // Put the values back...
            // When Field1 is updated the field should become visible...
            .elementByCss("#INITIAL_VALUE1 .dijitInputContainer input")
               .type("1")
               .end()
            .elementByCss("#INITIAL_VALUE2 .dijitInputContainer input")
               .type("2")
               .end()
            .elementByCss("#INITIAL_VALUE3 .dijitInputContainer input")
               .type("3")
               .end()
            .hasElementByCss("#SINGLE_POSITIVE_RULES")
               .then(function(result) {
                  assert(result == true, "Test #3a - Widget not displayed as expected");
               })
               .end()
            .elementByCss("#SINGLE_NEGATIVE_RULES")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "none", "Test #3b - Widget displayed unexpectedly");
               })
               .end()
            .elementByCss("#SINGLE_POSITIVE_RULES span.requirementIndicator")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "block", "Test #3c - Requirement indicator not displayed as expected");
               })
               .end()
            .elementByCss("#SINGLE_NEGATIVE_RULES span.requirementIndicator")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "none", "Test #3d - Requirement indicator displayed unexpectedly");
               })
               .end()
            .elementByCss("#SINGLE_POSITIVE_RULES .dijitInputContainer input")
               .getAttribute("disabled")
               .then(function(result) {
                  assert(result == "true", "Test #3e - Field should have been disabled");
               })
               .end()
            .elementByCss("#SINGLE_NEGATIVE_RULES .dijitInputContainer input")
               .getAttribute("disabled")
               .then(function(result) {
                  assert(result == null, "Test #3f - Field should have been enabled");
               })
               .end()

            // Test #4
            // Check mixed rules...
            // It should be initially invisible...
            .elementByCss("#MULTIPLE_MIXED_RULES")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "none", "Test #4a - Multiple mixed rule widget displayed unexpectedly");
               })
               .end()
            // Changing field 2 should make it visible...
            .elementByCss("#INITIAL_VALUE2 .dijitInputContainer input")
               .type("x")
               .end()
            .elementByCss("#MULTIPLE_MIXED_RULES")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "block", "Test #4b - Multiple mixed rule widget should be displayed");
               })
               .end()
            // Changing field 1 should hide it again...
            .elementByCss("#INITIAL_VALUE1 .dijitInputContainer input")
               .type("x")
               .end()
            .elementByCss("#MULTIPLE_MIXED_RULES")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "none", "Test #4c - Multiple mixed should be hidden again");
               })
               .end()

            // Make it visible again...
            .elementByCss("#INITIAL_VALUE1 .dijitInputContainer input")
               .type(specialKeys["Back space"])
               .end()
            // Requirement should be ON
            .elementByCss("#MULTIPLE_MIXED_RULES span.requirementIndicator")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "block", "Test #4d - Multiple mixed requirement indicator was not displayed");
               })
               .end()
            .elementByCss("#MULTIPLE_MIXED_RULES .dijitInputContainer input")
               .getAttribute("disabled")
               .then(function(result) {
                  assert(result == "true", "Test #4e - Multiple mixed should have been disabled");
               })
               .end()
            // Switch requirement OFF
            .elementByCss("#INITIAL_VALUE3 .dijitInputContainer input")
               .type("x")
               .end()
            .elementByCss("#MULTIPLE_MIXED_RULES span.requirementIndicator")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "none", "Test #4f - Multiple mixed requirement indicator displayed unexpectedly");
               })
               .end()
            .elementByCss("#MULTIPLE_MIXED_RULES .dijitInputContainer input")
               .getAttribute("disabled")
               .then(function(result) {
                  assert(result == null, "Test #4g - Multiple mixed should have been enabled");
               })
               .end()

            // Test #5
            // Check validation
            // The field should be initiall invalid (not a number)...
            .elementByCss("#HAS_VALIDATION_CONFIG span.validation")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "block", "Test #5a - Validation error indicator should be displayed");
               })
               .end()
            .elementByCss("#HAS_VALIDATION_CONFIG span.validation-message")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "block", "Test #5b - Validation error message should be displayed");
               })
               .text()
               .then(function(text) {
                  assert(text == "Value must be a number", "Test #5c - Validation error message not set correctly")
               })
               .end()
            // Enter a non-numeric (should still be in error)...
            .elementByCss("#HAS_VALIDATION_CONFIG .dijitInputContainer input")
               .type("x")
               .end()
            .elementByCss("#HAS_VALIDATION_CONFIG span.validation")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "block", "Test #5d - Validation error indicator should be displayed for non-numeric");
               })
               .end()
            // Remove the non-numeric and enter some numbers...
            .elementByCss("#HAS_VALIDATION_CONFIG .dijitInputContainer input")
               .type(specialKeys["Back space"])
               .type("1234")
               .end()
            .elementByCss("#HAS_VALIDATION_CONFIG span.validation")
               .getComputedCss("display")
               .then(function(result) {
                  assert(result == "none", "Test #5a - Validation error indicator should be hidden for numeric entry");
               })
               .end()

            // Check fields with invalid validation config are displayed...
            .elementByCss("#INVALID_VALIDATION_CONFIG_1")
               .isDisplayed()
               .end()
            .elementByCss("#INVALID_VALIDATION_CONFIG_2")
               .isDisplayed()

            // Post the coverage results...
            .then(function() {
               TestCommon.postCoverageResults(browser);
            })
            ;
      }
   });
});