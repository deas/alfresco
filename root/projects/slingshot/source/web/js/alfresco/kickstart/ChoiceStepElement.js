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
 * @module alfresco/kickstart/ChoiceStepElement
 * @extends module:alfresco/forms/controls/MultipleEntryElement
 * @author Dave Draper
 */
define(["alfresco/forms/controls/MultipleEntryElement",
        "dojo/_base/declare",
        "alfresco/forms/PublishForm",
        "alfresco/core/ObjectTypeUtils",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dijit/registry",
        "alfresco/forms/controls/DojoValidationTextBox",
        "alfresco/forms/controls/DojoSelect",
        "alfresco/forms/controls/MultipleEntryFormControl"], 
        function(MultipleEntryElement, declare, PublishForm, ObjectTypeUtils, lang, array, registry, DojoValidationTextBox, DojoSelect, MultipleEntryFormControl) {
   
   return declare([MultipleEntryElement], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {Array} i18nRequirements
       */
      i18nRequirements: [{i18nFile: "./i18n/ChoiceStepElement.properties"}],
      
      /**
       * Extends the inherited function to create a subscription for updates to the available form fields to select
       * from when creating a new rule and then explicitly publishes a request to get the latest field information.
       * 
       * @instance
       */
      postCreate: function alfresco_kickstart_ChoiceStepElement__postCreate() {
         this.alfSubscribe("ALF_FORM_FIELDS_UPDATE", lang.hitch(this, "updateAvailableFields"));
         this.alfPublish("ALF_REQUEST_AVAILABLE_FORM_FIELDS", {});
         this.inherited(arguments);
      },
      
      /**
       * This function is the callback handler for requesting the currently available fields. It is very similar to 
       * the "getOptionsFromPublication" function in BaseFormControl except that it sets an instance variable. It is
       * necessary to get the latest fields in order to be able to render the read display correctly.
       * 
       * @instance
       * @param {payload} payload The payload containing the details of the available fields
       */
      updateAvailableFields: function alfresco_kickstart_ChoiceStepElement__setAvailableFields(payload) {
         var options = lang.getObject("options", false, payload);
         if (options != null && ObjectTypeUtils.isArray(options))
         {
            this.availableFields = options;
         }
         else
         {
            this.availableFields = []
         }
      },
      
      readDisplayUpdateHandlers: null,
      
      /**
       * Attempts to create a human readable description of the current rule definition.
       * 
       * @instance
       */
      createReadDisplay: function alfresco_kickstart_ChoiceStepElement__createReadDisplay() {
         
         // Remove any existing data binding listeners...
         array.forEach(this.readDisplayUpdateHandlers, function(binding, i) {
            this.alfRemoveDataListener(binding);
         }, this);
         this.readDisplayUpdateHandlers = [];
         
         var currentValue = this.getValue();
         
         // Be careful not to pollute the data model with "undefined" objects...
         var sourceStep = (currentValue.sourceStep != null) ? this.alfGetData(currentValue.sourceStep + ".name") : "",
             field = (currentValue.field != null) ? this.alfGetData(currentValue.field + ".name") : "",
             targetStep = (currentValue.targetStep != null) ? this.alfGetData(currentValue.targetStep + ".name") : "";
         
         // Create the read display... 
         this.readDisplay.innerHTML = this.message("choiceStepElement.read.display", { 
            "sourceStep" : sourceStep,
            "field" : field,
            "operator" : this.message("choiceStepElement.operator." + currentValue.operator + ".label"),
            "value" : currentValue.operatorValue,
            "targetStep" :  targetStep});
         
         // Setup up data binding change handlers for updates to the names of the steps/fields
         if (this.readDisplayUpdateHandlers == null)
         {
            this.readDisplayUpdateHandlers = [];
         }
         if (currentValue.sourceStep != null)
         {
            this.readDisplayUpdateHandlers.push(this.alfBindDataListener(currentValue.sourceStep + ".name", this.dataScope, lang.hitch(this, "createReadDisplay")));
         }
         if (currentValue.field != null)
         {
            this.readDisplayUpdateHandlers.push(this.alfBindDataListener(currentValue.field + ".name", this.dataScope, lang.hitch(this, "createReadDisplay")));
         }
      },
      
      /**
       * Extends the inherited function to make a request for the available form fields to be published.
       * This ensures that the available fields can be accessed when creating the read display for 
       * an element.
       * 
       * @instance
       */
      createEditDisplay: function alfresco_kickstart_ChoiceStepElement__createEditDisplay() {
         this.inherited(arguments);
         this.alfPublish("ALF_REQUEST_AVAILABLE_FORM_FIELDS", {});
      },
      
      /**
       * Returns the widgets to be used in the form created for edit mode.
       * 
       * @instance
       * @returns {object[]}
       */
      getFormWidgets: function alfresco_kickstart_ChoiceStepElement__getFormWidgets() {
         return [
            {
               name: "alfresco/forms/controls/DojoSelect",
               config: {
                  fieldId: "SourceStepSelect",
                  name: "sourceStep",
                  label: "choiceStepElement.sourceStep.label",
                  description: "choiceStepElement.sourceStep.description",
                  optionsConfig: {
                     updateTopics: [{
                        // Make the subscription global but prefix it with the pubSubScope for the element.
                        // This is necessary because the form being creating has it's own scope (to prevent
                        // pollution of field events) but the field updates must come from the wider scope
                        topic: this.pubSubScope + "ALF_FORM_FIELDS_UPDATE",
                        global: true
                     }],
                     callback: "getOptionsFromPublication"
                  }
               }
            },
            {
               name: "alfresco/forms/controls/DojoSelect",
               config: {
                  fieldId: "FieldSelect",
                  name: "field",
                  label: "choiceStepElement.fieldSelect.label",
                  description: "choiceStepElement.fieldSelect.description",
                  optionsConfig: {
                     changesTo: [{targetId: "SourceStepSelect"}],
                     callback: lang.hitch(this, "getFieldsFromStep")
                  }
               }
            },
            {
               name: "alfresco/forms/controls/DojoSelect",
               config: {
                  fieldId: "OperatorSelect",
                  name: "operator",
                  label: "choiceStepElement.operator.label",
                  description: "choiceStepElement.operator.description",
                  optionsConfig: {
                     fixed: [
                        {label:"choiceStepElement.operator.equalTo.label", value: "equalTo"},
                        {label:"choiceStepElement.operator.notEqualTo.label", value: "notEqualTo"},
                        {label:"choiceStepElement.operator.lessThan.label", value: "lessThan"},
                        {label:"choiceStepElement.operator.moreThan.label", value: "moreThan"}
                     ]
                  }
               }
            },
            {
               name: "alfresco/forms/controls/DojoValidationTextBox",
               config: {
                  fieldId: "OperatorValue",
                  name: "operatorValue",
                  label: "choiceStepElement.operatorValue.label",
                  description: "choiceStepElement.operatorValue.description"
               }
            },
            {
               name: "alfresco/forms/controls/DojoSelect",
               config: {
                  fieldId: "TargetStepSelect",
                  name: "targetStep",
                  label: "choiceStepElement.targetStep.label",
                  description: "choiceStepElement.targetStep.description",
                  optionsConfig: {
                     updateTopics: [{
                        // Make the subscription global but prefix it with the pubSubScope for the element.
                        // This is necessary because the form being creating has it's own scope (to prevent
                        // pollution of field events) but the field updates must come from the wider scope
                        topic: this.pubSubScope + "ALF_FORM_FIELDS_UPDATE",
                        global: true
                     }],
                     callback: "getOptionsFromPublication"
                  }
               }
            }
         ];
      },
      
      /**
       * 
       * @instance
       * @returns {object[]} An array of the available options that map to the configured form fields.
       */
      getFieldsFromStep: function alfresco_kickstart_ChoiceStepElement__getFieldsFromStep(optionsConfig, payload) {
         var fields = [];
         if (payload != null)
         {
            var step = registry.byId(payload.value);
            if (step != null && typeof step.getAvailableFormFields === "function")
            {
               fields = step.getAvailableFormFields();
            }
            else
            {
               // No action.
            }
         }
         this.alfLog("log", "Returning available form fields from step", fields, optionsConfig, payload, this);
         return fields;
      }
   });
});