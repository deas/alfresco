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
 * This is a work-in-progress widget... use with caution.
 * 
 * @module alfresco/forms/Form
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dijit/form/Form",
        "dojo/_base/xhr",
        "alfresco/core/Core",
        "dojo/text!./templates/Form.html",
        "dojo/_base/lang",
        "alfresco/buttons/AlfButton",
        "dojo/_base/array",
        "dojo/json",
        "dijit/registry"], 
        function(declare, _Widget, _Templated, Form, xhr, AlfCore, template, lang, AlfButton, array, json, registry) {
   
   return declare([_Widget, _Templated, AlfCore], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/AlfDialog.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/Form.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * @instance
       * @type {object}
       * @default null
       */
      _form: null,
      
      /**
       * @instance
       * @type {object[]}
       * @default null
       */
      widgets: null,
      
      /**
       * The URL that the form will be posted to
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      postUrl: "",
      
      /**
       * @instance
       * @type {boolean}
       * @default false
       */
      convertFormToJsonString: false,
      
      /**
       * This will be instantiated as an array and used to keep track of any controls that report themselves as being
       * in an invalid state. The "OK" button for submitting the form should only be enabled when this list is empty.
       * 
       * @instance
       * @type {object[]}
       * @default null
       */
      invalidFormControls: null,
      
      /**
       * A reference to the "OK" button for the form.
       * TODO: It should be possible to configure alternative labels for the button
       * 
       * @instance
       * @type {object}
       * @default null
       */
      okButton: null,
      
      /**
       * A reference to the "Cancel" button for the form.
       * TODO: It should be possible to configure alternative labels for the button.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      cancelButton: null,
      
      /**
       * Indicates that the a new pubSubScope should be generated for this widget so that it's
       * form controls will be scoped to only communicate with this instance and not "pollute"
       * any other forms that may also be on the page.
       * 
       * @instance
       * @type {boolean}
       * @default true
       */
      scopeFormControls: true,
      
      /**
       * @instance
       */
      postCreate: function alfresco_forms_Form__postCreate() {
         
         // Setup some arrays for recording the valid and invalid widgets...
         this.invalidFormControls = [];
         
         // Generate a new pubSubScope if required...
         if (this.scopeFormControls == true && this.pubSubScope == "")
         {
            this.pubSubScope = this.generateUuid();
         }
         
         this._form = new Form({
            id: this.generateUuid()
         }, this.formNode);
         
         // Set up the handlers for form controls reporting themselves as valid or invalid
         // following user update...
         this.alfSubscribe("_invalidFormControl", lang.hitch(this, "onInvalidField"));
         this.alfSubscribe("_validFormControl", lang.hitch(this, "onValidField"));

         // Add the widgets to the form...
         // The widgets should automatically inherit the pubSubScope from the form to scope communication
         // to this widget. However, this widget will need to be assigned with a pubSubScope... 
         if (this.widgets)
         {
            this.processWidgets(this.widgets, this._form.domNode);
         }
         
         // Create the buttons for the form...
         this.createButtons();
      },
      
      /**
       * Handles the reporting of an invalid field. This will disable the "OK" button if it has
       * been created to prevent users from attempting to submit invalid data.
       * 
       * @instance
       * @param {object} payload The published details of the invalid field.
       */
      onInvalidField: function alfresco_forms_Form__onInvalidField(payload) {
         var alreadyCaptured = array.some(this._invalidFormControls, function(item) {
            return item == payload.name;
         });
         if (!alreadyCaptured)
         {
            this.invalidFormControls.push(payload.name);
         }
         if (this.okButton)
         {
            this.okButton.set("disabled", "true");
         }
      },
      
      /**
       * Handles the reporting of a valid field. If the field was previously recorded as being
       * invalid then it is removed from the [invalidFormControls]{@link module:alfresco/forms/Form#invalidFormControls}
       * attribute and it was the field was the only field in error then the "OK" button is 
       * enabled. 
       * 
       * @instance
       * @param {object} payload The published details of the field that has become valid
       */
      onValidField: function alfresco_forms_Form__onValidField(payload) {
         this.invalidFormControls = array.filter(this.invalidFormControls, function(item) {
            return item != payload.name;
         });
         if (this.okButton)
         {
            this.okButton.set("disabled", this.invalidFormControls.length > 0);
            
            // Update the publishPayload of the "OK" button so that when it is clicked
            // it will provide the current form data...
            var formValue = this.getValue();
            array.forEach(this.additionalButtons, function(button, index) {
               if (button.publishPayload != null)
               {
                  lang.mixin(button.publishPayload, formValue);
               }
               else
               {
                  button.publishPayload = formValue;
               }
            });
         }
      },
      
      /**
       * Indicates whether or not the "OK" button should be displayed or not.
       * 
       * @instance
       * @type {boolean}
       * @default true
       */
      showOkButton: true,
      
      /**
       * Indicates whether or not the "Cancel" button should be displayed or not.
       * 
       * @instance
       * @type {boolean}
       * @default true
       */
      showCancelButton: true,
      
      /**
       * The label that will be used for the "OK" button. This value can either be an explicit
       * localised value or an properties key that will be used to retrieve a localised value.
       * 
       * @instance
       * @type {string}
       * @default "form.button.ok.label"
       */
      okButtonLabel: "form.button.ok.label",
      
      /**
       * @instance 
       * @type {string}
       * @default null
       */
      okButtonPublishTopic: null,
      
      /**
       * @instance
       * @type {object}
       * @defualt null
       */
      okButtonPublishPayload: null,
      
      /**
       * The label that will be used for the "Cancel" button. This value can either be an explicit
       * localised value or an properties key that will be used to retrieve a localised value.
       *
       * @instance
       * @type {string}
       * @default "form.button.cancel.label"
       */
      cancelButtonLabel: "form.button.cancel.label",
      
      /**
       * @instance
       * @type {string}
       * @default null
       */
      cancelButtonPublishTopic: null,
      
      /**
       * @instance
       * @type {object}
       * @defualt null
       */
      cancelButtonPublishPayload: null,
      
      /**
       * This can be configured with details of additional buttons to be included with the form.
       * Any button added will have the publishPayload set with the form value. 
       * 
       * @instance
       * @type {object[]}
       * @default null
       */
      widgetsAdditionalButtons: null,
      
      /**
       * @instance
       * @type {object[]}
       * @default null
       */
      additionalButtons: null,
      
      /**
       * Creates the buttons for the form. This can be overridden to change the buttons that are displayed.
       * 
       * @instance
       */
      createButtons: function alfresco_forms_Form__createButtons() {
         
         if (this.showOkButton == true)
         {
            this.okButton = new AlfButton({
               label: this.message(this.okButtonLabel),
               publishTopic: this.okButtonPublishTopic,
               publishPayload: this.okButtonPublishPayload
            }, this.okButtonNode);
         }
         if (this.showCancelButton == true)
         {
            this.cancelButton = new AlfButton({
               label: this.message(this.cancelButtonLabel),
               publishTopic: this.cancelButtonPublishTopic,
               publishPayload: this.cancelButtonPublishPayload
            }, this.cancelButtonNode);
         }
         
         // If there are any other additional buttons to add, then process them here...
         if (this.widgetsAdditionalButtons != null)
         {
            this.additionalButtons = [];
            this.processWidgets(this.widgetsAdditionalButtons, this.buttonsNode);
         }
         else
         {
            this.additionalButtons = registry.findWidgets(this.buttonsNode);
         }
      },
      
      /**
       * Makes a call to the [validate]{@link module:alfresco/forms/Form#validate} function to check the initial
       * state of the form.
       * 
       * @instance
       */
      allWidgetsProcessed: function alfresco_forms_Form__allWidgetsProcessed(widgets) {
         this.validate();
         
         // If additional button configuration has been processed, then get a reference to ALL the buttons...
         if (this.widgetsAdditionalButtons != null && 
            this.additionalButtons != null &&
            this.additionalButtons.length == 0)
         {
            this.additionalButtons = registry.findWidgets(this.buttonsNode);
         }
      },
      
      /**
       * Handles posting the form...
       *  
       * @instance
       */
      _onOK: function() {
         var _this = this;
         var xhrArgs = {
            url: this.postUrl,
            handleAs: "json",
            load: function(response) {
               _this._onPostSuccess(response);
            },
            error: function(response) {
               _this._onPostFailure(response);
            }
         };
         
         // A form can either be posted directly or its contents converted into a JSON string
         // (this has been done to support existing Share WebScripts - such as for site creation)...
         if (this.convertFormToJsonString)
         {
            xhrArgs.headers = { "Content-Type": "application/json"};
            xhrArgs.postData = this._convertFormToJsonString(); 
         }
         else
         {
            xhrArgs.form =  this._form.id;
         }
         
         xhr.post(xhrArgs);
      },
      
      /**
       * Converts values of the widgets contained in the form into a JSON string
       * 
       * @instance
       */
      _convertFormToJsonString: function() {
         // Construct a JSON string payload
         var payload = {};
         array.forEach(this._form.getChildren(), function(entry, i) {
            var name = entry.get("name");
            if (name)
            {
               var widget = entry.get("_wrappedWidget");
               payload[entry.get("name")] = widget ? (widget.value ? widget.value : "") : "";
            }
         });
         
         return json.stringify(payload);
      },
     
      /**
       * Handles post success...
       * 
       * @instance
       */
      _onPostSuccess: function(response) {
         var payload = {
            response: response,
            form: this._form
         }
         this.alfPublish(this.id + "_POST_SUCCESS", payload);
      },
      
      /**
       * Handles post failure...
       * 
       * @instance
       */
      _onPostFailure: function(response) {
         this.alfPublish(this.id + "_POST_FAILURE", response);
      },
      
      // 
      /**
       * Handles cancelling the form.
       * 
       * @instance
       */
      _onCancel: function() {
         this.alfPublish(this.id + "_CANCEL", null);
      },
      
      /**
       * @instance
       * @return {object}
       */
      getValue: function() {
         var values = {};
         if (this._form)
         {
            array.forEach(this._form.getChildren(), function(entry, i) {
               values[entry.get("name")] = entry.getValue();
            });
         }
         this.alfLog("log", "Returning form values: ", values);
         return values;
      },
      
      /**
       * @instance
       * @param {object} values The values to set
       */
      setValue: function(values) {
         this.alfLog("log", "Setting form values: ", values);
         if (values && values instanceof Object)
         {
            if (this._form)
            {
               array.forEach(this._form.getChildren(), function(entry, i) {
                  entry.setValue(values[entry.get("name")]);
               });
            }
         }
      },
      
      /**
       * @instance
       * @returns {boolean}
       */
      validate: function() {
         this.alfLog("log", "Validating form", this._form);
         
         // THIS IS NOT A TYPO... the publish operation is performed twice. The first time 
         // will initialise the rules engine in each widget with the values of all the other
         // form controls that they have expressed an interest in and the second time will allow
         // the rules to be processed.
         array.forEach(this._processedWidgets, function(widget, i) {
            if (widget.publishValue && typeof widget.publishValue == "function")
            {
               widget.validate(); // Validate the initial value
               widget.publishValue();
            }
         });
         array.forEach(this._processedWidgets, function(widget, i) {
            if (widget.publishValue && typeof widget.publishValue == "function")
            {
               widget.publishValue();
            }
         });
         
         // The form is valid if there are no invalid form controls...
         return this.invalidFormControls.length == 0;
      }
   });
});