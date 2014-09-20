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
 * <p>This module handles form control validation and was written with the intention of being mixed into the
 * [BaseFormControl module]{@link module:alfresco/forms/controls/BaseFormControl}. It provides the ability
 * handle more complex validation, including asynchronous validation. This means that it is possible for 
 * a form to request remote data (e.g. to check whether or not a suggested identifier has already been used).</p>
 * <p>The validators that are currently provided include checking that the length of a form field value is 
 * neither too long or too short, that it matches a specific Regular Expression pattern and whether or not the
 * value is unique. Each validator should be configured with an explicit error message (if no error message is
 * provided then the invalid indicator will be displayed with no message).</p>
 * <p>Multiple validators can be chained together and if more than one validator reports that they are in error
 * then their respective error messages will be displayed in sequence.</p>
 * <p>Example configuration:</p>
 * <p><pre>"validationConfig": [
 *   {
 *     "validation": "minLength",
 *     "length": 3,
 *     "errorMessage": "Too short"
 *   },
 *   {
 *     "validation": "maxLength",
 *     "length": 5,
 *     "errorMessage": "Too long"
 *   },
 *   {
 *     "validation": "regex",
 *     "regex": "^[A-Za-z]+$",
 *     "errorMessage": "Letters only"
 *   },
 *   {
 *     "validation": "validateUnique",
 *     "errorMessage": "Already used",
 *     "itemsProperty": "items",
 *     "publishTopic": "GET_VALUES"
 *   }
 * ]</pre></p>
 *
 * @module alfresco/forms/controls/FormControlValidationMixin
 * @extends module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-class"], 
        function(declare, AlfCore, lang, array, domClass) {
   
   return declare([AlfCore], {
      
      /**
       * Indicates whether or not validation is currently in-progress or not
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      _validationInProgress: false,

      /**
       * Keeps track of all the validators that are currently processing. This array is added to
       * by [processValidationArrayElement]{@link module:alfresco/forms/controls/FormControlValidationMixin#processValidationArrayElement}
       * and removed from during [validationComplete]{@link module:alfresco/forms/controls/FormControlValidationMixin#validationComplete}
       * 
       * @instance
       * @type {object}
       * @default null
       */
      _validatorsInProgress: null,

      /**
       * Keeps track of the current validation state. Is updated by each call back to 
       * [validationComplete]{@link module:alfresco/forms/controls/FormControlValidationMixin#validationComplete}
       * 
       * @instance
       * @type {boolean}
       * @default true
       */
      _validationInProgressState: true,

      /**
       * This is used to build up the overall validation message. 
       * 
       * @instance
       * @type {string}
       * @default null
       */
      _validationErrorMessage: null,

      /**
       * Called to start processing validators. If validation is already in progress then a flag will be set
       * to queue another validation run once the current processing has completed.
       *
       * @instance
       */
      startValidation: function alfresco_forms_controls_FormControlValidationMixin__startValidation() {
         if (this._validationInProgress === true)
         {
            this._queuedValidationRequest = true;
         }
         else
         {
            // Reset the _validatorsInProgress attribute...
            this._validationInProgressState = true;
            this._validatorsInProgress = {};
            this._validationErrorMessage = "";
            this._validationMessage.innerHTML = this._validationErrorMessage;
            this._validationInProgress = true;
            this._queuedValidationRequest = false;

            // Put the control into the invalid state (TODO: show a processing icon)
            this.alfPublish("ALF_INVALID_CONTROL", {
               name: this.name,
               fieldId: this.fieldId
            });

            // Hide any previous errors and reveal the in-progress indicator...
            this.hideValidationFailure();
            domClass.remove(this._validationInProgressIndicator, "hidden");

            // Iterate over each validation configuration, start it and add it to a count...
            var validationErrors = [];
            passedValidationArray = array.forEach(this.validationConfig, lang.hitch(this, this.processValidationArrayElement, validationErrors));

            var count = this.countValidatorsInProgress();
            if (count === 0)
            {
               // No validation validators were configured so just complete validation...
               this.validationComplete();
            }
            else
            {
               // Call the relevant validation function for each validation configuration element,
               // this is done *after* processing the configuration to ensure that validation isn't
               // recorded as having completed *before* all the validators have started...
               for (var key in this._validatorsInProgress)
               {
                  var validationConfig = this._validatorsInProgress[key];
                  this[validationConfig.validation](validationConfig);
               }
            }
         }
      },

      /**
       * This function is called from the [processValidationRules]{@link module:alfresco/forms/controls/BaseFormControl#processValidationRules}
       * function for each element of the [validationConfig]{@link module:alfresco/forms/controls/BaseFormControl#validationConfig}
       * configuration attribute. It checks that the supplied 'validation' attribute maps to a function
       * a function (the core validation functions are defined in the [FormControlValidationMixin]{@link module:alfresco/forms/controls/FormControlValidationMixin}
       * module).
       *
       * @instance
       * @param {array} validationErrors An array to populate with validation errors.
       * @param {object} validationConfig The current element to process
       * @param {number} index The index of the element in the array
       * @returns {boolean} True if validation is passed and false otherwise 
       */
      processValidationArrayElement: function alfresco_forms_controls_BaseFormControl__processValidationArrayElement(validationErrors, validationConfig, index) {
         var validationType = lang.getObject("validation", false, validationConfig);
         if (validationType != null)
         {
            if (typeof this[validationType] === "function")
            {
               // Add the current validator to the those currently in-flight and
               // using the index as a key. This index is also added to the 
               this._validatorsInProgress[index] = validationConfig;
               validationConfig.index = index;
            }
            else
            {
               this.alfLog("warn", "Validation configuration 'validation' attribute refers to non-existent function", validationType, this);
            }
         }
         else
         {
            this.alfLog("warn", "Validation configuration missing a 'validation' attribute", validationConfig, this);
         }
      },

      /**
       * Counts the validators that are currently in progress.
       * 
       * @instance
       * @returns {number} The number of validators still in progress
       */
      countValidatorsInProgress: function alfresco_forms_controls_FormControlValidationMixin__countValidatorsInProgress() {
         var count = 0;
         for (var key in this._validatorsInProgress)
         {
            if (this._validatorsInProgress.hasOwnProperty(key))
            {
               count++;
            }
         }
         return count;
      },

      /**
       * This function should be called by all validators when they have finished validating the
       * current entry.
       *
       * @instance
       * @param {object} validationConfig The configuration for the validator that has just completed
       * @param {boolean} result Whether or not the validation was successful or not
       */
      reportValidationResult: function alfresco_forms_controls_FormControlValidationMixin__reportValidationResult(validationConfig, result) {
         this.alfLog("log", "Validator complete, result: " + result, validationConfig);

         var index = lang.getObject("index", false, validationConfig);
         if (index == null)
         {
            this.alfLog("warn", "Validation completion call without index attribute", validationConfig, this);
         }
         else
         {
            // Update the error message...
            if (result === false)
            {
               if (validationConfig.errorMessage != null)
               {
                  if (this._validationErrorMessage.length !== 0)
                  {
                     this._validationErrorMessage += ", ";
                  }
                  this._validationErrorMessage += this.message(validationConfig.errorMessage);
               }
               
               // Update the validation message...
               this.showValidationFailure();
               this._validationMessage.innerHTML = this._validationErrorMessage;
            }

            // Update the overall result...
            this._validationInProgressState = this._validationInProgressState && result;

            // Remove the completed validator using the configured index...
            delete this._validatorsInProgress[index];

            // Count the remaining validators...
            var count = this.countValidatorsInProgress();
            
            // If all are complete then update validation status
            if (count === 0)
            {
               this.validationComplete();
            }
            else
            {
               this.alfLog("log", "Waiting on " + count + " more validators to complete");
            }
         }
      },

      /**
       * This function is called when all validators have reported their finished state.
       *
       * @instance
       */
      validationComplete: function alfresco_forms_controls_FormControlValidationMixin__validationComplete() {
         // Hide the in-progress indicator...
         domClass.add(this._validationInProgressIndicator, "hidden");

         // Check requirement validation...
         var value = this.getValue();
         var requirementTest = !(this._required && (value == null || value == ""));

         // Publish the results...
         if (this._validationInProgressState && requirementTest)
         {
            this.alfPublish("ALF_VALID_CONTROL", {
               name: this.name,
               fieldId: this.fieldId
            });
            this.hideValidationFailure();
         }
         else
         {
            this.alfPublish("ALF_INVALID_CONTROL", {
               name: this.name,
               fieldId: this.fieldId
            });
            this.showValidationFailure();
         }
         this._validationInProgress = false;

         if (this._queuedValidationRequest === true)
         {
            this.startValidation();
         }
      },
      
      /**
       * This validator checks the current form field value against the configured regex pattern.
       *
       * @instance
       * @param {object} validationConfig The configuration for this validator
       */
      regex: function alfresco_forms_controls_FormControlValidationMixin__regex(validationConfig) {
         var isValid = true;
         var regexPattern = lang.getObject("regex", false, validationConfig);
         if (regexPattern != null && typeof regexPattern === "string")
         {
            var value = this.getValue();
            var regExObj = new RegExp(regexPattern);
            isValid = regExObj.test(value);
            if (validationConfig.invertRule === true)
            {
               isValid = !isValid;
            }
         }
         else
         {
            this.alfLog("warn", "A regex validation was configured with an invalid 'regex' attribute", validationConfig, this);
         }
         this.reportValidationResult(validationConfig, isValid);
      },

      /**
       * This validator checks that the current form field value is longer than the configured value.
       *
       * @instance
       * @param {object} validationConfig The configuration for this validator
       */
      maxLength: function alfresco_forms_controls_FormControlValidationMixin__maxLength(validationConfig) {
         var isValid = true;
         var targetLength = lang.getObject("length", false, validationConfig);
         if (targetLength != null && !isNaN(targetLength))
         {
            var value = this.getValue();
            isValid = value.length <= targetLength;
         }
         else
         {
            this.alfLog("warn", "A maxLength validation was configured with an invalid 'length' attribute", validationConfig, this);
         }
         this.reportValidationResult(validationConfig, isValid);
      },

      /**
       * This validator checks that the current form field value is shorter than the configured value.
       *
       * @instance
       * @param {object} validationConfig The configuration for this validator
       */
      minLength: function alfresco_forms_controls_FormControlValidationMixin__minLength(validationConfig) {
         var isValid = true;
         var targetLength = lang.getObject("length", false, validationConfig);
         if (targetLength != null && !isNaN(targetLength))
         {
            var value = this.getValue();
            isValid = value.length >= targetLength;
         }
         else
         {
            this.alfLog("warn", "A minLength validation was configured with an invalid 'length' attribute", validationConfig, this);
         }
         this.reportValidationResult(validationConfig, isValid);
      },

      /**
       * Stores the validator configuration for validating uniqueness so that it doesn't need to be included
       * in the payload from the called service. It is set in [validateUnique]{@link module:alfresco/forms/controls/FormControlValidationMixin#validateUnique}
       * and reset to null in [onValidationUniqueResponse]{@link module:alfresco/forms/controls/FormControlValidationMixin#onValidationUniqueResponse}.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      _validateUniqueConfig: null,

      /**
       * This validator checks that the current form field value has not already been used on another item.
       * It should be configured with a 'publishTopic' attribute to request a list of existing items and
       * a 'publishPayload' attribute to go with it. Optionally an 'itemsProperty' attribute can be configured
       * that will identify the attribute in the returned payload that will contain the array of items to
       * iterate through looking for a value that matches the currently entered value.
       * 
       * @instance
       * @param {object} config The configuration for this validation
       */
      validateUnique: function alfresco_forms_controls_FormControlValidationMixin__validateUnique(validationConfig) {
         var responseTopic = this.generateUuid();
         var payload = (validationConfig.publishPayload) ? lang.clone(validationConfig.publishPayload) : {};
         payload.alfResponseTopic = responseTopic;
         this._validateUniqueConfig = validationConfig;
         this._validateUniqueHandle = this.alfSubscribe(responseTopic + "_SUCCESS", lang.hitch(this, this.onValidationUniqueResponse), true);
         this.alfPublish(validationConfig.publishTopic, payload, true);
         return true;
      },

      /**
       * This is the callback function that is called as a result of using the 
       * [validateUnique]{@link module:alfresco/forms/controls/FormControlValidationMixin#validateUnique} validator.
       *
       * @instance
       * @param {object} payload The payload containing the items to iterate through looking for an existing use of the form field value
       */
      onValidationUniqueResponse: function alfresco_forms_controls_FormControlValidationMixin__onValidationUniqueResponse(payload) {
         this.alfUnsubscribeSaveHandles([this._validateUniqueHandle]);
         var notUnique = false;

         // Grab the previously saved configuration from the instance and then remove it...
         var validationConfig = this._validateUniqueConfig;
         this._validateUniqueConfig = null;

         if (payload != null)
         {
            // Get the configured items property (this identifies the attribute in the payload containing
            // the array to iterate over)...
            var itemsProperty = lang.getObject("itemsProperty", false, validationConfig);
            if (itemsProperty == null)
            {
               itemsProperty = "items"; 
            }
            // Get the current form field value to compare with each item...
            var value = this.getValue();

            // Get the array of items and check if the form field 'name' attribute for each item matches the
            // currently entered form field value...
            var items = lang.getObject(itemsProperty, false, payload);
            if (items == null)
            {
               this.alfLog("warn", "Attempting to validate uniqueness but 'itemsProperty' attribute doesn't map to an an array", validationConfig, this);
            }
            else
            {
               notUnique = array.some(items, function(item, index) {
                  var itemValue = lang.getObject(this.name, false, item);
                  return itemValue == value;
               }, this);
            }
         }

         // Report back with the validation result...
         this.reportValidationResult(validationConfig, !notUnique);
      }
   });
});