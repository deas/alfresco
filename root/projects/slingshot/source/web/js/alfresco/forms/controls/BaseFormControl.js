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
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dijit/_FocusMixin",
        "dojo/text!./templates/BaseFormControl.html",
        "dojo/dom-construct",
        "alfresco/core/Core",
        "dojo/_base/xhr",
        "dojo/_base/array",
        "dojo/dom-style",
        "dojo/dom-class",
        "dijit/focus",
        "dijit/Tooltip",
        "dojo/fx"], 
        function(declare, _Widget, _Templated, _FocusMixin, template, domConstruct, AlfCore, xhr, array, domStyle, domClass, focusUtil, Tooltip, fx) {
   
   return declare([_Widget, _Templated, _FocusMixin, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/BaseFormControl.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @property {Array} i18nRequirements
       */
      i18nRequirements: [{i18nFile: "./i18n/BaseFormControl.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @property {String} template
       */
      templateString: template,
      
      /**
       * 
       * 
       * @property {object} The widget that the base form control wraps
       * @default null
       */
      wrappedWidget: null,

      /**
       * A scope for prefixing all publication and subscription topics. This is so that form controls can be used
       * and not interfere incorrectly with each other.
       * 
       * @property {string} pubSubScope
       * @default ""
       */
      pubSubScope: "",
      
      /**
       * The widget to instantiate.
       * 
       * @property {string} type
       * @default ""
       */
      type: "",

      /**
       * The label identifying the data to provide. The value supplied will be checked against the available
       * scoped NLS resources to attempt to translate message keys into localized values.
       * 
       * @property {string} label
       * @default ""
       */
      label: "",
      
      /**
       * A label for showing units measurements (e.g. "milliseconds", "MB", etc). The value supplied will be 
       * checked against the available scoped NLS resources to attempt to translate message keys into localized values.
       * 
       * @property {string} unitsLabel
       * @default ""
       */
      unitsLabel: "",
      
      /**
       * A description of the field. This will appear in a tooltip by default. The value supplied will be 
       * checked against the available scoped NLS resources to attempt to translate message keys into localized values.
       * 
       * @property {string} description
       * @default ""
       */
      description: "",
      
      /**
       * The value to submit as the name for the data captured by this field when the form is submitted.
       * 
       * @property {string} name
       */
      name: "", 

      /**
       * The value to submit as the value of the field when the form is submitted.
       *  
       * @property {string} value
       * @default ""
       */
      value: "",
      
      /**
       * The list of static options (TODO: We need to provide support for dynamic options via XHR or callback).
       * 
       * @property {array}
       * @default null
       */
      options: null,
      
      /**
       * The default visibility status is always true (this can be overridden by extending controls).
       * 
       * @property {boolean} _visible
       * @default true
       */
      _visible: true,
      
      /**
       * Used to toggle visibility of the field.
       * 
       * @method alf_Visible
       * @param {boolean} status The boolean value to change the visibility state to.
       */
      alf_Visible: function alfresco_forms_controls_BaseFormControl__alf_Visible(status) {
         this.alfLog("log", "Change visibility status for '" + this.name + "' to: " + status);
         this._visible = status;
         if (this.containerNode)
         {
//            var display = status ? "block" : "none";
//            domStyle.set(this.containerNode, {
//               display: display
//            });
            
            // This bit of code uses wipes for showing/hiding the nodes... not sure whether to keep it or not!
            if (status)
            {
               fx.wipeIn({node: this.containerNode}).play();
            }
            else
            {
               fx.wipeOut({node: this.containerNode}).play();
            }
            
         }
      },
      
      /**
       * The default requirement status is always false (this can be overridden by extending controls).
       * 
       * @property {boolean} _required
       * @default false
       */
      _required: false,
      
      /**
       * Used to toggle the requirement state of the field.
       * 
       * @method alf_Required
       * @param {boolean} status The boolean value to change the requirement state to
       */
      alf_Required: function alfresco_forms_controls_BaseFormControl__alf_Required(status) {
         this.alfLog("log", "Change requirement status for '" + this.name + "' to: " + status, {});
         this._required = status;
         if (this._requirementIndicator)
         {
            var display = status ? "block" : "none";
            domStyle.set(this._requirementIndicator, {
               display: display
            });
            
            // When requirement state is changed we need to re-validate the widget
            this.validate();
         }
      },
      
      /**
       * The default disabled status is always false  (this can be overridden by extending controls).
       * 
       * @property {boolean} _disabled
       */
      _disabled: false,
      
      /**
       * Controls the disability status of the field.
       * 
       * @method alf_Disabled
       * @param {boolean} status The boolean status to set the disablity state of the field to.
       */
      alf_Disabled: function alfresco_forms_controls_BaseFormControl__alf_Disabled(status) {
         this.alfLog("log", "Change disablement status for '" + this.name + "' to: " + status);
         this._disabled = status;
         if (this.wrappedWidget)
         {
            this.wrappedWidget.set("disabled", status);
         }
      },
      
      /**
       * 
       * @property {object} visibilityConfig
       */
      visibilityConfig: null,
      requirementConfig: null,
      disablementConfig: null,
      
      functionMixins: null,
      
      constructor: function alfresco_forms_controls_BaseFormControl__constructor(args) {
         declare.safeMixin(this, args);
         
         // Process all function mixins (this function is defined in the Core class and will mixin 
         // the functions provided by any classes defined that will provide callback functions for 
         // dynamic property processing. For example visibility/requirement/disablement callbacks on
         // property changes.
         // PLEASE NOTE: In order to ensure that the function mixins are detected if part of the WebScript controller
         //              declaration then the the mixins should be added as services. By doing this they will be preloaded
         //              and the require won't need an async request. This does mean that function mixins won't work
         //              when the application isn't being run with aggregated dependencies.
         //              Also, there is not support for form controls defined within widgets. This could be a candidate
         //              for another Surf dependency rule !!
         this.processFunctionMixins();
         
         // Setup the rules for controlling visibility, requirement and disablement...
         // NOTE: The reason for the "alf_" prefix is that we need to make sure that the functions cannot corrupt attribute
         //       accessors functionality. This is particularly relevant to the "alf_Disabled". Originally a function called
         //       "disabled" was created and this caused focus events to occur inadvertently.
         this.processConfig("alf_Visible", this.visibilityConfig);
         this.processConfig("alf_Required", this.requirementConfig);
         this.processConfig("alf_Disabled", this.disablementConfig);
         
         // Setup the options handling...
         this.processOptionsConfig(this.optionsConfig);
         
         if (this.validationConfig != null && typeof this.validationConfig.regex == "string")
         {
            this.validationConfig.regExObj = new RegExp(this.validationConfig.regex);
         }
      },
      
      /**
       * Processes the configuration for defining options and their update behaviour. This configuration is different to
       * the visibility/requirement/disablement rules so needs to be handled separately. The configuration can be defined
       * in the following structure:
       *    optionsConfig: {
       *       defaultValue: "someValue",
       *       changesTo: ["A","B"],
       *       makeXhr : "/some/rest/call" 
       *       callback: "functionName",=
       *       fixed: [
       *          { label: "Option1", value: "Value1"},
       *          { label: "Option2", value: "Value2"}
       *       ]
       *    }
       *    
       *  The precendence for setting options is as follows:
       *  - makrXhr
       *  - callback
       *  - fixed
       *  
       *  The fixed options will only be used if neither async or callbacks are configured.
       *  
       * @method processOptionsConfig
       * @param {object} config
       */
      processOptionsConfig: function alfresco_forms_controls_BaseFormControl__processOptionsConfig(config) {
         var _this = this;
         if (typeof config == "object" && config != null) // Null is an object in JavaScript :)
         {
            if (typeof config.changesTo != "undefined" &&
                (typeof config.makeXhr != "undefined" || typeof config.callback != "undefined"))
            {
               // If changesTo and either makeXhr or callback are defined in the configuration then
               // we'll set up a subscription for updating the options dynamically. We don't actually
               // need to set the options because they should get called as options triggers get fired
               if (config.changesTo instanceof Array)
               {
                  array.forEach(config.changesTo, function(property, index) {
                     _this.alfSubscribe(_this.pubSubScope + "_valueChangeOf_" + property, function(payload) {
                        _this.alfLog("log", "OPTIONS CONFIG: Field '" + _this.name + "' is handling value change of field'" + payload.name);
                        if (config.makeXhr != null)
                        {
                           _this.getXhrOptions(config.makeXhr);
                        }
                        else if (config.callback != null)
                        {
                           // Make the callback for setting the options. The callback can either be a function or
                           // a String. If it is a String then it is assumed to be the name of a function in the 
                           // widget so will be checked.
                           if (typeof config.callback == "function")
                           {
                              config.callback(payload.name, payload.oldValue, payload.value, _this);
                           }
                           else if (typeof config.callback == "string" && typeof _this[config.callback] == "function")
                           {
                              _this[config.callback](payload.name, payload.oldValue, payload.value, _this);
                           }
                           else
                           {
                              _this.alfLog("log", "The supplied 'callback' attribute for '" + _this.name + "' was neither a String nor a function");
                           }
                        }
                     });
                  });
               }
               else
               {
                  this.alfLog("log", "The supplied 'changesTo' attribute for '" + this.name + "' was not an Array");
               }
            }
            else if (typeof config.makeXhr != "undefined")
            {
               _this.getXhrOptions(config.makeXhr);
            }
            else if (typeof config.fixed != "undefined")
            {
               // If there is no dynamic options configuration set then setup any fixed options
               // that have been provided.
               
               if (config.fixed instanceof Array)
               {
                  this.options = config.fixed;
                  array.forEach(this.options, function(currentOption, index) {
                     if (currentOption.label)
                     {
                        currentOption.label = _this.message(currentOption.label);
                     }
                  });
               }
               else
               {
                  this.alfLog("log", "The supplied fixed options attribute for '" + this.name + "' was not an Array");
               }
            }
            
            // TODO: Handle setting the default value.
         }
      },
      
      /**
       * @method getXhrOptions
       * @param {string} url The URL from which to get the options
       */
      getXhrOptions: function alfresco_forms_controls_BaseFormControl__getXhrOptions(url) {
         var _this = this;
         xhr.get({
            url: url,
            handleAs: "json",
            load: function(result) {
               try
               {
                  _this.setOptions(result);
               }
               catch (e)
               {
                  _this.alfLog("error", "The following error occurred setting asynchronous results", e);
                  _this.setOptions([]);
               }
            }
         });
      },
      
      /**
       * 
       * @property _defaultOption
       * @default null
       */
      _defaultOption: null,
      
      /**
       * This is the default implementation for setting options on the widget. It can be overridden was extending classes
       * as necessary to set the options supplied.
       * 
       * @method setOptions
       * @param {object} options
       */
      setOptions: function alfresco_forms_controls_BaseFormControl__setOptions(options) {
         this.alfLog("log", "Setting options for field '" + this.name + "'", options);
         var _this = this;
         this.options = options;
         if (this.wrappedWidget)
         {
            var currentOptions = this.wrappedWidget.get("options");
            if (currentOptions && this.wrappedWidget.removeOption)
            {
               // Remove all the current options...
               array.forEach(currentOptions, function(currentOption, index) {
                  _this.wrappedWidget.removeOption(currentOption);
               })
            }
            if (this.wrappedWidget.addOption)
            {
               // Add all the new options...
               array.forEach(options, function(currentOption, index) {
                  if (currentOption.label)
                  {
                     currentOption.label = _this.message(currentOption.label);
                  }
                  _this.wrappedWidget.addOption(currentOption);
               });
            }
         }
      },
      
      /**
       * This function is reused to process the configuration for the visibility, disablement and requirement attributes of the form
       * control. The type supplied is the 
       * The format for the rules is as follows:
       * 
       *    visible: {
       *       initial: true/false,
       *       rules: {
       *          "name1" : {
       *             is: ["a", "b", "c"],
       *             isNot: ["d", "e", "f"]
       *          }
       *          
       *       },
       *       callbacks: {
       *          "name1": "functionA"
       *       }
       *    }
       *  
       * @mmethod processConfig
       * @param {string} attribute
       * @param {object} config
       */
      processConfig: function alfresco_forms_controls_BaseFormControl__processConfig(attribute, config) {
         
         if (config)
         {
            // Set the initial value...
            if (typeof config.initialValue != "undefined")
            {
               this[attribute](config.initialValue);
            }
            
            // Process the rule subscriptions...
            if (typeof config.rules != "undefined")
            {
               this._processRulesConfig(attribute, config.rules)
            }
            else if (typeof config.rules != "undefined")
            {
               // Debug output when instantiation data is incorrect. Only log when some data is defined but isn't an object.
               // There's no point in logging messages for unsupplied data - just incorrectly supplied data.
               this.alfLog("log", "The rules configuration for attribute '" + attribute + "' for property '" + this.name + "' was not an Object");
            }
            
            // Process the callback subscriptions...
            if (typeof config.callbacks == "object")
            {
               this._processCallbacksConfig(attribute, config.callbacks)
            }
            else if (typeof config.callbacks != "undefined")
            {
               // Debug output when instantiation data is incorrect. Only log when some data is defined but isn't an object.
               // There's no point in logging messages for unsupplied data - just incorrectly supplied data.
               this.alfLog("log", "The callback configuration for attribute '" + attribute + "' for property '" + this.name + "' was not an Object");
            }
         }
      },
      
      /**
       * This function evaluates all the rules for a particular attribute.
       * 
       * @method _evaluateRules
       * @param {string} attribute
       */
      _evaluateRules: function alfresco_forms_controls_BaseFormControl___evaluateRules(attribute) {
         
         this.alfLog("log", "RULES EVALUATION('" + attribute + "'): Field '" + this.name + "'");
         // Make the assumption that the current status is true (i.e. the rule is PASSED). This is done so that
         // we can AND the value against the result of each iteration (we can also stop processing the rules once
         // the rule is negated...
         var status = true;
         
         // The exception to the above comment is when NO rules are configured - in that case we leave the status
         // as false by default
         var hasProps = false;
         
         for (var key in this._rulesEngineData[attribute])
         {
            // Need this assignment to "prove" there are properties (this approach is used for compatibility with older
            // browsers)...
            hasProps = true;
            
            // Keep processing rules until the rule status is negated...
            if (status)
            {
               var currentValue = this._rulesEngineData[attribute][key].currentValue;
               var validValues = this._rulesEngineData[attribute][key].rules.is;
               var invalidValues = this._rulesEngineData[attribute][key].rules.isNot;
               
               // Assume that its NOT valid value (we'll only do the actual test if its not set to an INVALID value)...
               // UNLESS there are no valid values specified (in which case any value is valid apart form those in the invalid list)
               var isValidValue = (typeof validValues == "undefined" || validValues.length == 0);

               // Initialise the invalid value to be false if no invalid values have been declared (and only check values if defined)...
               var isInvalidValue = (typeof invalidValues != "undefined" && invalidValues.length > 0);
               if (isInvalidValue)
               {
                  // Check to see if the current value is set to an invalid value (i.e. a value that negates the rule)
                  isInvalidValue = array.some(invalidValues, function(value) {
                     return value == currentValue;
                  });
               }
               
               // Check to see if the current value is set to a valid value...
               if (!isInvalidValue && typeof validValues != "undefined" && validValues.length > 0)
               {
                  isValidValue = array.some(validValues, function(value) {
                     return value == currentValue;
                  });
               }
               
               // The overall status is true (i.e. the rule is still passing) if the current status is true and the
               // current value IS set to a valid value and NOT set to an invalid value
               status = status && isValidValue && !isInvalidValue;
            }
         }
         
         // This last AND ensures that we negate the rule if there were no rules to process...
         status = status && hasProps;
         return status;
      },
      
      /**
       * This holds all the data about rules that need to be processed for the various attributes of the widget. By default this
       * will handle rules for visibility, requirement and disability.
       * 
       * @property {object} _rulesEngineData
       * @default null
       */
      _rulesEngineData: null,
      
      /**
       * This function sets up the subscriptions for processing rules relating to attributes.
       * 
       * @method _processRulesConfig
       * @param {string} attribute
       * @param {object} rules
       */
      _processRulesConfig: function alfresco_forms_controls_BaseFormControl___processRulesConfig(attribute, rules) {
         
         // TODO: Implement rules for handling changes in validity (each type could have rule type of "isValid"
         //       and should subscribe to changes in validity. The reason for this would be to allow changes
         //       on validity. Validity may change asynchronously from value as it could be performed via a 
         //       remote request.
         
         var _this = this;
         
         // Set up the data structure that will be required for processing the rules for the target property changes...
         if (this._rulesEngineData == null)
         {
            // Ensure that the rulesEngineData object has been created
            this._rulesEngineData = {};
         }
         if (typeof this._rulesEngineData[attribute] == "undefined")
         {
            // Ensure that the rulesEngineData object has specific information about the form control attribute...
            this._rulesEngineData[attribute] = {};
         }
         
         for (var key in rules) {
            // Create an object for the property to configure rules for...
            if (typeof this._rulesEngineData[attribute][key] == "undefined")
            {
               this._rulesEngineData[attribute][key] = {};
            }
            
            // Set the rules to be processed for the current rule...
            this._rulesEngineData[attribute][key].rules = rules[key];
            
            // Subscribe to changes in the relevant property...
            this.alfSubscribe(this.pubSubScope + "_valueChangeOf_" + key, function(payload) {
               // Update the new property value and evaluate all the rules...
               _this._rulesEngineData[attribute][payload.name].currentValue = payload.value;
               var status = _this._evaluateRules(attribute);
               _this[attribute](status);
            });
         }
      },

      /**
       * The payload of property value changing publications should have the following attributes... 
       *    1) The name of the property that has changed ("name")
       *    2) The old value of the property that has changed ("oldValue")
       *    3) The new value of the property that has changed ("value")
       *  Callbacks should take the following arguments (nameOfChangedProperty, oldValue, newValue, callingObject, attribute)
       *  
       *  @method _processCallbacksConfig
       *  @param {string} attribute
       *  @param {object} callbacks
       */
      _processCallbacksConfig: function alfresco_forms_controls_BaseFormControl___processCallbacksConfig(attribute, callbacks) {
         var _this = this;
         for (var key in callbacks) {
            if (typeof callbacks[key] == "function")
            {
               // Subscribe using the supplied function (this will only be possible when form controls are created
               // dynamically from widgets (rather than in configuration)...
               _this.alfSubscribe(_this.pubSubScope + "_valueChangeOf_" + key, function(payload) {
                  var status = callbacks[payload.name](payload.name, payload.oldValue, payload.value, _this, attribute);
                  _this[attribute](status);
               });
            }
            else if (typeof callbacks[key] == "string" &&
                     typeof _this[callbacks[key]] == "function")
            {
               // Subscribe using a String reference to a function defined in this widget...
               _this.alfSubscribe(_this.pubSubScope + "_valueChangeOf_" + key, function(payload) {
                  var status = _this[callbacks[payload.name]](payload.name, payload.oldValue, payload.value, _this, attribute);
                  _this[attribute](status);
               });
            }
            else
            {
               // Log a message if the callback supplied isn't actually a function...
               this.alfLog("log", "The callback for property '" + _this.name + "' for handling changes to property '" + key + "' was not a function or was not a String that references a local function");
            }
         }
      },
      
      /**
       * 
       * @method postCreate
       */
      postCreate: function alfresco_forms_controls_BaseFormControl__postCreate() {
         
         var _this = this;
         
         
         /*
          * These are the types of attributes we expect a form to have...
          * Field label (e.g. “Name”)
          * Description (e.g. hover help)
            Units (e.g. “milliseconds”, etc)
            User control (e.g. text box, drop-down menu, radio buttons, etc)
            Validation - regex expression
            Validation - callback function reference
            Validation - server side REST validation call (required when there is not enough client-side data for validation, e.g. has the name provided by the user already been used?)
            Requirement indicator - whether or not the field requires input by default
            Visibility - whether or not the field is displayed by default
            Enablement - whether or not the field is disabled by default
            Requirement rules - dynamic evaluation of the other controls within the form to determine if field is required
            Visibility rules - dynamic evaluation of the other controls within the form to determine whether or not the field is displayed
            Enablement rules - dynamic evaluation of the other controls within the form to determine whether or not the field is disabled
            Standard errors message keys (for missing data, regex failure, etc)
            Drag and drop call back functions?
          */
         var config = this.getWidgetConfig();
         
         // Use the _disabled property if not already set...
         if (typeof config.disabled == "undefined") 
         {
            config.disabled = this._disabled;
         }
         this.wrappedWidget = this.createFormControl(config);
         this.wrappedWidget.placeAt(this._controlNode);
         
         // Set up the events that indicate a change in value...
         this.setupChangeEvents();
         
         // Set the initial visibility, requirement and disablement...
         this.alf_Visible(this._visible);
         this.alf_Required(this._required);
         this.alf_Disabled(this._disabled);
         
         // Set the label...
         this._labelNode.innerHTML = this.encodeHTML(this.message(this.label));
         
         // Set the units label...
         if (this.unitsLabel != null && this.unitsLabel != "")
         {
            this._unitsNode.innerHTML = this.encodeHTML(this.message(this.unitsLabel));
         }
         else
         {
            // Hide the units node if there are no units to display...
            domStyle.set(this._unitsNode, { display: "none"});
         }
         
         // Set the error message for validation...
         if (this.validationConfig != null && typeof this.validationConfig.errorMessage == "string")
         {
            // TODO: This message might not make much sense if it is just missing data for a required field...
            this._validationMessage.innerHTML = this.encodeHTML(this.message(this.validationConfig.errorMessage));
         }
         
         if (this.wrappedWidget != null && this.description != "")
         {
            // Create a tooltip for the control...
            Tooltip.defaultPosition=['above', 'below'];
            var tooltip = new Tooltip({label: this.message(this.description),
                                       showDelay: 250,
                                       connectId: [this.wrappedWidget.domNode]});
         }
         else
         {
            this.alfLog("log", "Tooltip not created because form control not returned by call to createFormControl");
         }
      },
      
      /**
       * This function sets up the default events that indicate that a wigets value has changed. This function can be overridden
       * to handle non-Dojo widgets or when multiple widgets represent a single control. 
       * 
       * @method setupChangeEvents
       */
      setupChangeEvents: function alfresco_forms_controls_BaseFormControl__setupChangeEvents() {
         var _this = this;
         
         // Whenever a widgets value changes then we need to publish the details out to other form controls (that exist in the
         // same scope so that they can modify their appearance/behaviour as necessary)...
         if (this.wrappedWidget)
         {
            // TODO: Do we need to do anything with the watch handle when the widget is destroyed?
            var watchHandle = this.wrappedWidget.watch("value", function(name, oldValue, value) {
               _this.formControlValueChange(name, oldValue, value);
               _this.validate();
            });
         }
      },
      
      /**
       * This gets the value currently assigned to the wrapped widget. It assumes the widget has a single "value"
       * attribute that can be retrieved (i.e. it assumes a Dojo widget). Any extending classes that do not use
       * Dojo widgets (or use multiple widgets) should override this implementation to return the correct value.
       * 
       * The meaningful argument indicates that data should be returned that has some meaning rather than just the value
       * that is different from the value that can be used to repopulate the control. This is more applicable to more complex 
       * controls where the data being collected differs from the data used to get the display in the control
       * 
       * @method getValue
       * @param {boolean} meaningful
       * @returns {object} The current value of the field.
       */
      getValue: function alfresco_forms_controls_BaseFormControl__getValue(meaningful) {
         
         var value = null;
         if (this.wrappedWidget)
         {
            try
            {
               value = this.wrappedWidget.getValue(meaningful);
            }
            catch(e)
            {
               this.alfLog("log", "An exception was thrown retrieving the value for field: '" + this.name + "'");
            }
         }
//         this.alfLog("log", "Returning value for field: '" + this.name + "': ", value);
         return value;
      },
      
      /**
       * 
       * @method setValue
       * @param {object} value The value to set.
       */
      setValue: function alfresco_forms_controls_BaseFormControl__setValue(value) {
         this.alfLog("log", "Setting field: '" + this.name + "' with value: ", value);
         if (this.wrappedWidget)
         {
            this.wrappedWidget.setValue(value);
         }
      },
      
      /**
       * This function publishes the current value of the widget. It is provided so that enclosing forms can publish
       * all of its controls values to process all rules.
       * 
       * @method publishValue
       */
      publishValue: function alfresco_forms_controls_BaseFormControl__publishValue() {
//         this.alfLog("log", "Publishing value for field: '" + this.name + "'");
         if (this.wrappedWidget)
         {
            this.alfPublish(this.pubSubScope + "_valueChangeOf_" + this.name, {
               name: this.name,
               oldValue: this.getValue(),
               value: this.getValue()
            });
         }
      },
      
      /**
       * This function is called whenever the value of the wrapped form widget changes. It publishes the details of the change
       * so that other form widgets can update their status based on the value.
       * 
       * @method formControlValueChange
       * @param {string} attributeName
       * @param {object} oldValue
       * @param {object} value
       */
      formControlValueChange: function alfresco_forms_controls_BaseFormControl__formControlValueChange(attributeName, oldValue, value) {
         this.alfPublish(this.pubSubScope + "_valueChangeOf_" + this.name, {
            name: this.name,
            oldValue: oldValue,
            value: value
         });
      },
      
      /**
       * 
       * @method createFormControl
       * @param {object} config
       */
      createFormControl: function alfresco_forms_controls_BaseFormControl__createFormControl(config) {
         // Extension point
      },
      
      /**
       * 
       * @method getWidgetConfig
       * @returns {object} The configuration for the form control.
       */
      getWidgetConfig: function alfresco_forms_controls_BaseFormControl__getWidgetConfig() {
         // This is a method that is expected to be overridden. We won't even assume that the widget configuration
         // will be standard Dojo configuration because we might be instantiated a custom or 3rd party library widget.
         return {};
      },
      
      /**
       * 
       * @method startup
       */
      startup: function alfresco_forms_controls_BaseFormControl__startup() {
         
      },
      
      /**
       * This will hold all of the configuration for validation. It is initialised in the constructor.
       * 
       * @property {object} validationConfig
       * @default null
       */
      validationConfig: null,
      
      /**
       * This function validates the current widget value.
       * 
       * @method validate
       * @returns {boolean} A value indicating whether or not validation passed successfully or not.
       */
      validate: function alfresco_forms_controls_BaseFormControl__validate() {
         
         var isValid = this.processValidationRules();
         
         // Publish the results (this topic is provided primarily of an enclosing form)...
         if (isValid)
         {
            this.alfPublish(this.pubSubScope + "_validFormControl", {
               name: this.name
            });
            this.hideValidationFailure();
         }
         else
         {
            this.alfPublish(this.pubSubScope + "_invalidFormControl", {
               name: this.name
            });
            this.showValidationFailure();
         }
         return isValid;
      },
      
      /**
       * This function defines the default validation processing. It should be overridden by extending form controls
       * that do not use the default rules. This function rather than the "validate" function should be overridden
       * because this function simply indicates whether or not the control is valid but the "validate" function 
       * controls the rendering of error messages and publication of related events.
       * 
       * @method processValidationRules
       * @returns {boolean} Indicates whether or not the validation rules were passed successfully
       */
      processValidationRules: function alfresco_forms_controls_BaseFormControl__processValidationRules() {
          // Things to validate against are...
         // 1) Does the widget have a value if it is required
         var value = this.getValue();
         
         this.alfLog("log", "Validating: '" + this.name + "' with value:", value);
         
         var passedRequiredTest = true,
             passedRegExpTest = true; // Assume valid starting point.
         
         // Check that a value has been specified if this is a required field...
         passedRequiredTest = !(this._required && (value == null || value == ""));
         
         // Check if any specified regular expression is passed...
         if (this.validationConfig != null)
         {
            if (typeof this.validationConfig.regExObj != "undefined" && this.validationConfig.regExObj instanceof RegExp)
            {
               passedRegExpTest = this.validationConfig.regExObj.test(value);
            }
         }
         
         // 3) Does the widget value satisfy a callback function
         // 4) Does the widget value satisfy a remote validation request
         
         // TODO: Need to output an appropriate error message.
         return passedRequiredTest && passedRegExpTest;
      },
      
      /**
       * By default this simply adds the "validation-error" and "display" classes to the _validationIndicator
       * and _validationMessage DOM nodes respectively. However, the code has been broken out into a separate function
       * to support extending classes that may provide alternative HTML templates or wish to render errors
       * differently.
       * 
       * @method showValidationFailure
       */
      showValidationFailure: function alfresco_forms_controls_BaseFormControl__showValidationFailure() {
         domClass.add(this._validationIndicator, "validation-error");
         domClass.add(this._validationMessage, "display");
      },
      
      /**
       * By default this simply removes the "validation-error" and "display" classes to the _validationIndicator
       * and _validationMessage DOM nodes respectively. However, the code has been broken out into a separate function
       * to support extending classes that may provide alternative HTML templates or wish to render errors
       * differently.
       * 
       * @method hideValidationFailure
       */
      hideValidationFailure: function alfresco_forms_controls_BaseFormControl__hideValidationFailure() {
         domClass.remove(this._validationIndicator, "validation-error");
         domClass.remove(this._validationMessage, "display");
      }
   });
});
