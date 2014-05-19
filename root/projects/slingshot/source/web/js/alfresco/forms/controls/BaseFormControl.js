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
 * <p>This should be extended by all form controls in order to provide a consistent look and feel. It wraps
 * a standard widget (which can be provided by JavaScript toolkits other than Dojo) or multiple widgets
 * and creates the name, description and units labels are appropriate. It also provides the capability for
 * form controls to communicate with each other and dynamically update their appearance and behaviour 
 * through configured rules (e.g. to allow progressive disclosure, etc through configuration).</p>
 * 
 * @module alfresco/forms/controls/BaseFormControl
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes dijit/_FocusMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dijit/_FocusMixin",
        "dojo/text!./templates/BaseFormControl.html",
        "dojo/dom-construct",
        "alfresco/core/Core",
        "alfresco/core/ObjectTypeUtils",
        "dojo/_base/xhr",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-style",
        "dojo/dom-class",
        "dijit/focus",
        "dijit/Tooltip"], 
        function(declare, _Widget, _Templated, _FocusMixin, template, domConstruct, AlfCore, ObjectTypeUtils, xhr, lang, array, domStyle, domClass, focusUtil, Tooltip) {
   
   return declare([_Widget, _Templated, _FocusMixin, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {Array}
       */
      cssRequirements: [{cssFile:"./css/BaseFormControl.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/BaseFormControl.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * This will be set to the form control that the user will actually interact with (e.g. a text box, check box, etc).
       * 
       * @instance
       * @type {object}
       * @default null
       */
      wrappedWidget: null,

      /**
       * A scope for prefixing all publication and subscription topics. This is so that form controls can be used
       * and not interfere incorrectly with each other.
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      pubSubScope: "",
      
      /**
       * The widget to instantiate.
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      type: "",

      /**
       * This used to uniquely identify the form field. If not explicitly defined in the instantiation
       * configuration then a new UUID will be generated for it. It is important to have an id attribute
       * that is NOT used as the widgetId (to allow duplications - which the dijit/registry would otherwise
       * reject) and to have a value that can be used as a reference that will be unaffected by changes
       * (e.g. when configuring a form field dynamically with the application).
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      fieldId: "",
      
      /**
       * The label identifying the data to provide. The value supplied will be checked against the available
       * scoped NLS resources to attempt to translate message keys into localized values.
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      label: "",
      
      /**
       * A label for showing units measurements (e.g. "milliseconds", "MB", etc). The value supplied will be 
       * checked against the available scoped NLS resources to attempt to translate message keys into localized values.
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      unitsLabel: "",
      
      /**
       * A description of the field. This will appear in a tooltip by default. The value supplied will be 
       * checked against the available scoped NLS resources to attempt to translate message keys into localized values.
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      description: "",
      
      /**
       * The value to submit as the name for the data captured by this field when the form is submitted.
       * 
       * @instance
       * @type {string}
       */
      name: "", 

      /**
       * The value to submit as the value of the field when the form is submitted.
       *  
       * @instance
       * @type {string}
       * @default ""
       */
      value: "",
      
      /**
       * The list of static options (TODO: We need to provide support for dynamic options via XHR or callback).
       * 
       * @instance
       * @type {array}
       * @default null
       */
      options: null,
      
      /**
       * By default if a field is hidden or disabled then it's value should not be posted. This allows multiple controls
       * representing the same data to be used together with visibility/disablement rules so that only one control's value]
       * is submitted. This variable has no direct effect on this widget but can be used by other widgets such as the 
       * [form]{@link module:alfresco/forms/Form}. Intentionally hidden fields should override the default value so 
       * that they are always submitted.
       *
       * @instance
       * @type {boolean}
       * @default true
       */
      postWhenHiddenOrDisabled: true,

      /**
       * By default all fields will have their value when a parent [form]{@link module:alfresco/forms/Form} has its value set
       * but in certain cases it may be desirable for fields to not have a data update when they are hidden or disabled. This
       * will be the case when multiple fields are being used to represent the same data but through progressive disclosure
       * only one field is displayed at a time. By overriding this variable a field can request not to have it's value updated
       * when it is hidden or disabled.
       */
      noValueUpdateWhenHiddenOrDisabled: false,
      
      /**
       * The default visibility status is always true (this can be overridden by extending controls).
       * 
       * @instance
       * @type {boolean}
       * @default true
       */
      _visible: true,
      
      /**
       * Used to toggle visibility of the field.
       * 
       * @instance
       * @param {boolean} status The boolean value to change the visibility state to.
       */
      alfVisible: function alfresco_forms_controls_BaseFormControl__alfVisible(status) {
         this.alfLog("log", "Change visibility status for '" + this.fieldId + "' to: " + status);
         this._visible = status;
         if (this.containerNode)
         {
            var display = status ? "block" : "none";
            domStyle.set(this.containerNode, {
               display: display
            });
         }
      },
      
      /**
       * The default requirement status is always false (this can be overridden by extending controls).
       * 
       * @instance
       * @type {boolean}
       * @default false
       */
      _required: false,
      
      /**
       * Used to toggle the requirement state of the field.
       * 
       * @instance
       * @param {boolean} status The boolean value to change the requirement state to
       */
      alfRequired: function alfresco_forms_controls_BaseFormControl__alfRequired(status) {
         this.alfLog("log", "Change requirement status for '" + this.fieldId + "' to: " + status, {});
         this._required = status;
         if (this._requirementIndicator)
         {
            if (this._required == true)
            {
               domClass.add(this._requirementIndicator, "required");
            }
            else
            {
               domClass.remove(this._requirementIndicator, "required");
            }
            
            // When requirement state is changed we need to re-validate the widget
            this.validate();
         }
      },
      
      /**
       * The default disabled status is always false  (this can be overridden by extending controls).
       * 
       * @instance
       * @type {boolean}
       */
      _disabled: false,
      
      /**
       * Controls the disability status of the field.
       * 
       * @instance
       * @param {boolean} status The boolean status to set the disablity state of the field to.
       */
      alfDisabled: function alfresco_forms_controls_BaseFormControl__alfDisabled(status) {
         this.alfLog("log", "Change disablement status for '" + this.fieldId + "' to: " + status);
         this._disabled = status;
         if (this.wrappedWidget && typeof this.wrappedWidget.set === "function")
         {
            this.wrappedWidget.set("disabled", status);
         }
      },
      
      /**
       * Defines the visibility behaviour of the widget. It is possible for the widget to dynamically be hidden
       * or displayed based on the value of one or more other widgets. See [processConfig]{@link module:alfresco/forms/controls/BaseFormControl#processConfig}
       * for the structure to use when configuring the rules</p>
       *
       * @instance
       * @type {object}
       * @default
       */
      visibilityConfig: null,
      
      /**
       * Defines the visibility behaviour of the widget. It is possible for the widget to dynamically be required
       * to have a value provided based on the value of one or more other widgets. See [processConfig]{@link module:alfresco/forms/controls/BaseFormControl#processConfig}
       * for the structure to use when configuring the rules</p>
       *
       * @instance
       * @type {object}
       * @default
       */
      requirementConfig: null,
      
      /**
       * Defines the visibility behaviour of the widget. It is possible for the widget to dynamically be disabled
       * or enabled based on the value of one or more other widgets. See [processConfig]{@link module:alfresco/forms/controls/BaseFormControl#processConfig}
       * for the structure to use when configuring the rules</p>
       *
       * @instance
       * @type {object}
       * @default
       */
      disablementConfig: null,
      
      /**
       * @instance
       */
      constructor: function alfresco_forms_controls_BaseFormControl__constructor(args) {
         declare.safeMixin(this, args);
         
         if (this.fieldId == null || this.fieldId == "")
         {
            this.fieldId = this.generateUuid();
         }
         
         // Setup the rules for controlling visibility, requirement and disablement...
         // NOTE: The reason for the "alf_" prefix is that we need to make sure that the functions cannot corrupt attribute
         //       accessors functionality. This is particularly relevant to the "alfDisabled". Originally a function called
         //       "disabled" was created and this caused focus events to occur inadvertently.
         this.processConfig("alfVisible", this.visibilityConfig);
         this.processConfig("alfRequired", this.requirementConfig);
         this.processConfig("alfDisabled", this.disablementConfig);
         
         // Setup the options handling...
         this.processOptionsConfig(this.optionsConfig);
         
         if (this.validationConfig != null && typeof this.validationConfig.regex == "string")
         {
            this.validationConfig.regExObj = new RegExp(this.validationConfig.regex);
         }
      },
      
      /**
       * <p>Processes the configuration for defining options and their update behaviour. This configuration is different to
       * the visibility/requirement/disablement rules so needs to be handled separately. The configuration can be defined
       * in the following structure:<p>
       * <p><pre>"optionsConfig": {
       *    "changesTo": [{
       *       "targetId": "someId",
       *    }],
       *    "updateTopics": [
       *       {
       *          "topic": "someTopic",
       *          "global": true
       *       }
       *    ],
       *    "requestTopic": "TOPIC",
       *    "callback": "functionName",
       *    "fixed": [
       *       { "label": "Option1", "value": "Value1"},
       *       { "label": "Option2", "value": "Value2"}
       *    ]
       *  }</pre></p>
       *  <p>The precendence for setting options is as follows:<ul>
       *  <li>requestTopic</li>
       *  <li>callback</li>
       *  <li>fixed</li>
       *  </ul>e.g. if a "requestTopic" is provided then any "fixed" options will be ignored.</p>
       *  
       * @instance
       * @param {object} config
       */
      processOptionsConfig: function alfresco_forms_controls_BaseFormControl__processOptionsConfig(config) {
         if (config != null)
         {
            // Create update subscriptions based on the requested changes to other fields in the form...
            // We're going to do some helpful checking of the configuration to aid development rather
            // than just ignoring invalid configuration...
            var changesTo = lang.getObject("changesTo", false, config);
            if (changesTo != null)
            {
               if (ObjectTypeUtils.isArray(changesTo))
               {
                  // We're using an additional topic prefix here which is the pattern used for publications
                  // within the form...
                  // TODO: Make the string an instance variable?
                  array.forEach(config.changesTo, lang.hitch(this, "createOptionsChangesTo", config));
               }
               else
               {
                  this.alfLog("warn", "The supplied 'changesTo' attribute for '" + this.fieldId + "' was not an Array");
               }
            }
            // Create update subcriptions based on topics published that are external to the form. This allows
            // the options to respond to "global" events rather than just changes within the form itself.
            var updateTopics = lang.getObject("updateTopics", false, config);
            if (updateTopics != null)
            {
               if (ObjectTypeUtils.isArray(updateTopics))
               {
                  array.forEach(config.updateTopics, lang.hitch(this, "createOptionsSubscriptions", config));
               }
               else
               {
                  this.alfLog("warn", "The supplied 'updateTopics' attribute for '" + this.fieldId + "' was not an Array");
               }
            }
            
            // Generate the initial set of options in the following precedence...
            // 1) Request Topic
            // 2) XHR request
            // 3) Callback function
            // 4) Fixed options
            var requestTopic = lang.getObject("requestTopic", false, config),
                callback = lang.getObject("callback", false, config),
                fixed = lang.getObject("fixed", false, config);
            if (requestTopic != null)
            {
               this.getPubSubOptions(requestTopic);
            }
            else if (callback != null)
            {
               // Handle configuration requests that options be generated through a function call...
               // Note that we're not explicitly handling scope here, it's expected that a hitch call will be
               // used if a scope is required. It is also possible to set the callback as a string which will
               // be matched against a function of the current scope....
               if (typeof callback === "function")
               {
                  this.options = callback(config);
                  array.forEach(this.options, lang.hitch(this, "processOptionLabel"));
               }
               else if (ObjectTypeUtils.isString(callback) && typeof this[callback] === "function")
               {
                  this.options = this[callback](config);
                  array.forEach(this.options, lang.hitch(this, "processOptionLabel"));
               }
               else
               {
                  this.alfLog("warn", "The supplied 'callback' attribute for '" + this.fieldId + "' was not a Function");
               }
            }
            else if (fixed != null)
            {
               // Handle configuration that specifies a fixed configuration...
               if (ObjectTypeUtils.isArray(fixed))
               {
                  this.options = fixed;
                  array.forEach(this.options, lang.hitch(this, "processOptionLabel"));
               }
               else
               {
                  this.alfLog("log", "The supplied fixed options attribute for '" + this.fieldId + "' was not an Array");
               }
            }
         }
      },
      
      /**
       * This is a simple function that is used to convert label message keys into the appropriate
       * translated message.
       * 
       * @instance
       * @param {object} option The option configuration
       * @param {number} index The index of the option
       */
      processOptionLabel: function alfresco_forms_controls_BaseFormControl__processOptionLabels(option, index) {
         if (option.label)
         {
            option.label = this.message(option.label);
         }
         else if (option.value)
         {
            option.label = option.value;
         }
         else
         {
            this.alfLog("warn", "An option was provided with neither label nor value", option, this);
         }
      },
      
      /**
       * Creates the subscription to the supplied topic information. All topics are handled by the 
       * updateOptions function.
       * 
       * @instance
       * @param {object} optionsConfig The overriding options config object
       * @param {object} subscription The details of the subscription to create
       * @param {number} index The index of the topic
       */
      createOptionsChangesTo: function alfresco_forms_controls_BaseFormControl__createOptionsChangesTo(optionsConfig, subscription, index) {
         if (subscription.targetId == null)
         {
            this.alfLog("warn", "No 'targetId' defined in subscription config", subscription, optionsConfig, this);
         }
         else
         {
            // Create the subscription...
            var topic = "_valueChangeOf_" + subscription.targetId,
                global = (subscription.global != null ? subscription.global : false);
            this.alfSubscribe(topic, lang.hitch(this, "updateOptions", optionsConfig), global);
         }
      },
      
      /**
       * Creates the subscription to the supplied topic information. All topics are handled by the 
       * updateOptions function.
       * 
       * @instance
       * @param {object} optionsConfig The overriding options config object
       * @param {object} subscription The details of the subscription to create
       * @param {number} index The index of the topic
       */
      createOptionsSubscriptions: function alfresco_forms_controls_BaseFormControl__createOptionsSubscriptions(optionsConfig, subscription, index) {
         if (subscription.topic == null)
         {
            this.alfLog("warn", "No 'topic' defined in subscription config", subscription, optionsConfig, this);
         }
         else
         {
            // Create the subscription...
            var global = (subscription.global != null ? subscription.global : false);
            this.alfSubscribe(subscription.topic, lang.hitch(this, "updateOptions", optionsConfig), global);
         }
      },
      
      /**
       * This is a built-in options callback that attempts to retrieve options from a publication event 
       * where it is assumed that the publication payload. An example of using this function can be found in
       * the [getFormWidgets]{@link module:alfresco/forms/creation/FormRulesConfigCreatorElement#getFormWidgets}
       * function of the [FormRulesConfigCreatorElement module]{@link module:alfresco/forms/creation/FormRulesConfigCreatorElement}
       * 
       * @instance
       * @param {object} optionsConfig The configuration for options handling defined for the current control
       * @param {object} payload The publication payload
       */
      getOptionsFromPublication: function alfresco_forms_controls_BaseFormControl__getOptionsFromPublication(optionsConfig, payload) {
         var options = lang.getObject("options", false, payload);
         if (options != null && ObjectTypeUtils.isArray(options))
         {
            return options;
         }
         else
         {
            return []
         }
      },

      /**
       * This function is called when an rule triggering options reload occurs (e.g. the value of another relevant field in the 
       * form has been changed).
       * 
       * @instance
       * @param {object} optionsConfig The overriding options config object
       * @param {object} payload The publication payload
       */
      updateOptions: function alfresco_forms_controls_BaseFormControl__onUpdateOptions(optionsConfig, payload) {
         this.alfLog("log", "OPTIONS CONFIG: Field '" + this.fieldId + "' is handling value change of field'" + payload.name);
         if (optionsConfig.requestTopic != null)
         {
            this.getPubSubOptions(optionsConfig.requestTopic);
         }
         else if (optionsConfig.callback != null)
         {
            // Make the callback for setting the options. The callback can either be a function or
            // a String. If it is a String then it is assumed to be the name of a function in the 
            // widget so will be checked.
            if (typeof optionsConfig.callback == "function")
            {
               this.setOptions(optionsConfig.callback(optionsConfig, payload, this));
            }
            else if (typeof optionsConfig.callback == "string" && typeof this[optionsConfig.callback] == "function")
            {
               this.setOptions(this[optionsConfig.callback](optionsConfig, payload, this));
            }
            else
            {
               this.alfLog("log", "The supplied 'callback' attribute for '" + this.fieldId + "' was neither a String nor a function");
            }
         }
      },
      
      /**
       * This gets set to the temporary subscription handle that is created whenever options are dynamically requested
       * by publishing on a configured topic. This information needs to be be maintained as a widget instance variable
       * in order for the temporary subscription to be removed and prevent potential memory leaks.
       *
       * @instance
       * @type {object}
       * @default null
       */
      _pubSubOptionsHandle: null,

      /**
       * Sets up a new subscription for options changes and then publishes a request to get those options.
       *
       * @instance
       * @param {string} topic The topic to publish to retrieve options
       */
      getPubSubOptions: function alfresco_forms_controls_BaseFormControl__getPubSubOptions(topic) {
         // Publish a topic to get the currently available options based on the current value...
         var responseTopic = this.generateUuid();
         this._pubSubOptionsHandle = this.alfSubscribe(responseTopic, lang.hitch(this, "onPubSubOptions"), true);
         this.alfPublish(topic, {
            responseTopic: responseTopic,
         }, true);
      },

      /**
       * This is hitched to the subscription set up in the [getPubSubOptions]{@link module:alfresco/forms/controls/BaseFormControl#getPubSubOptions}
       * function and simply unsubscribes the from the topic generated when requesting the options.
       *
       * @instance
       * @param {object} payload The published information
       */
      onPubSubOptions: function alfresco_forms_controls_BaseFormControl__onPubSubOptions(payload) {
         if (this._pubSubOptionsHandle != null)
         {
            this.alfUnsubscribe(this._pubSubOptionsHandle);
         }
         else
         {
            this.alfLog("warn", "A subscription handle was not found for processing pubSubOptions - this could be a potential memory leak", this);
         }
         if (payload.options != null)
         {
            this.setOptions(payload.options);
         }
         else
         {
            this.alfLog("warn", "No 'options' attribute published in payload for pubSub options", payload, this);
         }
      },

      /**
       * This is called to set the latest options.
       * 
       * @instance
       * @type {object}
       */
      setOptions: function alfresco_forms_controls_BaseFormControl__setOptions(options) {
         
         this.alfLog("log", "Setting options for field '" + this.fieldId + "'", options);
         
         // Get the current value so that we can attempt to reset it when the options are refreshed...
         var currentValue = this.getValue();
         
         this.options = options;
         if (this.wrappedWidget)
         {
            var currentOptions = this.wrappedWidget.get("options");
            if (currentOptions && typeof this.wrappedWidget.removeOption === "function")
            {
               // Remove all the current options...
               array.forEach(currentOptions, lang.hitch(this, "removeOption"));
            }
            if (typeof this.wrappedWidget.addOption === "function")
            {
               // Add all the new options...
               array.forEach(options, lang.hitch(this, "addOption"));
            }
         }
         
         // Reset the option...
         this.setValue(currentValue);
      },
      
      /**
       * Removes an option from the wrapped widet.
       * 
       * @instance
       * @param {object} option The option to remove
       * @param {number} index The index of the option to remove
       */
      removeOption: function alfrecso_forms_controls_BaseFormControl__removeOption(option, index) {
         this.wrappedWidget.removeOption(option);
      },
      
      /**
       * Adds a new option to the wrapped widget.
       * 
       * @instance 
       * @param {object} option The option to add
       * @param {number} index The index of the option to add
       */
      addOption: function alfresco_forms_controls_BaseFormControl__addOption(option, index) {
         this.processOptionLabel(option, index);
         this.wrappedWidget.addOption(option);
      },
      
      /**
       * <p>This function is reused to process the configuration for the visibility, disablement and requirement attributes of the form
       * control. The format for the rules is as follows:</p>
       * <p><pre>"visibilityConfig": {
       *    "initialValue": true,
       *    "rules": [
       *       {
       *          "targetId": "fieldId1",
       *          "is": ["a", "b", "c"],
       *          "isNot": ["d", "e", "f"]
       *       }
       *    ],
       *    "callbacks": {
       *          "id": "functionA"
       *    }
       * }</pre></p>
       * <p>This structure applied to the following attributes:<ul>
       * <li>[visibilityConfig]{@link module:alfresco/forms/controls/BaseFormControl#visibilityConfig}</li>
       * <li>[requirementConfig]{@link module:alfresco/forms/controls/BaseFormControl#requirementConfig}</li>
       * <li>[disablementConfig]{@link module:alfresco/forms/controls/BaseFormControl#disablementConfig}</li></ul></p>
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
               this.processRulesConfig(attribute, config.rules)
            }
            else if (typeof config.rules != "undefined")
            {
               // Debug output when instantiation data is incorrect. Only log when some data is defined but isn't an object.
               // There's no point in logging messages for unsupplied data - just incorrectly supplied data.
               this.alfLog("log", "The rules configuration for attribute '" + attribute + "' for property '" + this.fieldId + "' was not an Object");
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
               this.alfLog("log", "The callback configuration for attribute '" + attribute + "' for property '" + this.fieldId + "' was not an Object");
            }
         }
      },
      
      /**
       * This holds all the data about rules that need to be processed for the various attributes of the widget. By default this
       * will handle rules for visibility, requirement and disability.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      _rulesEngineData: null,
      
      /**
       * This function sets up the subscriptions for processing rules relating to attributes.
       * 
       * @instance
       * @param {string} attribute E.g. visibility, editability, requirement
       * @param {object} rules
       */
      processRulesConfig: function alfresco_forms_controls_BaseFormControl__processRulesConfig(attribute, rules) {
         // TODO: Implement rules for handling changes in validity (each type could have rule type of "isValid"
         //       and should subscribe to changes in validity. The reason for this would be to allow changes
         //       on validity. Validity may change asynchronously from value as it could be performed via a 
         //       remote request.

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
         array.forEach(rules, lang.hitch(this, "processRule", attribute));
      },

      /**
       * This function processes an individual attribute rule (e.g. to change the visibility, disablement or
       * requirement status).
       * 
       * @instance
       * @param {string} attribute The attribute that the rule effects (e.g. visibility)
       * @param {object} rule The rule to process.
       * @param {number} index The index of the rule.
       */
      processRule: function alfresco_forms_controls_BaseFormControl__processRule(attribute, rule, index) {
         if (rule.targetId != null)
         {
            if (typeof this._rulesEngineData[attribute][rule.targetId] == "undefined")
            {
               this._rulesEngineData[attribute][rule.targetId] = {};
            }
            
            // Set the rules to be processed for the current rule...
            // NOTE: Previous rules can be potentically overridden here...
            this._rulesEngineData[attribute][rule.targetId].rules = rule;
            
            // Subscribe to changes in the relevant property...
            this.alfSubscribe("_valueChangeOf_" + rule.targetId, lang.hitch(this, "evaluateRules", attribute));
         }
         else
         {
            this.alfLog("warn", "The following rule is missing a 'name' attribute", rule, this);
         }
      },
      
      /**
       * This function evaluates all the rules configured for a particular attribute (e.g. "visibility") for the 
       * current form control. It is triggered whenever one of the other fields configured as part of a rule changes, 
       * but ALL the rules are evaluated for that attribute.
       * 
       * @instance
       * @param {string} attribute
       * @param {object} payload The publication posted on the topic that triggered the rule
       */
      evaluateRules: function alfresco_forms_controls_BaseFormControl__evaluateRules(attribute, payload) {
         
         this.alfLog("log", "RULES EVALUATION('" + attribute + "'): Field '" + this.fieldId + "'");

         // Set the current value that triggered the evaluation of rules...
         this._rulesEngineData[attribute][payload.fieldId].currentValue = payload.value;

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
                  isInvalidValue = array.some(invalidValues, lang.hitch(this, "ruleValueComparator", currentValue));
               }
               
               // Check to see if the current value is set to a valid value...
               if (!isInvalidValue && typeof validValues != "undefined" && validValues.length > 0)
               {
                  isValidValue = array.some(validValues, lang.hitch(this, "ruleValueComparator", currentValue));
               }
               
               // The overall status is true (i.e. the rule is still passing) if the current status is true and the
               // current value IS set to a valid value and NOT set to an invalid value
               status = status && isValidValue && !isInvalidValue;
            }
         }
         
         // This last AND ensures that we negate the rule if there were no rules to process...
         status = status && hasProps;
         this[attribute](status);
         return status;
      },
      
      /**
       * The default comparator function used for comparing a rule value against the actual value of a field.
       * Note that the target value is expected to be an object from the arrays (assigned to the  "is" or "isNot"
       * attribute) and by default the "value" attribute of those objects are compared with the current value
       * of the field. It is possible to override this comparator to allow a more complex comparison operation.
       * 
       * It's important to note that values are compared as strings. This is done to ensure that booleans can 
       * be compared. This is important as it should be possible to construct rules dynamically and values
       * should be entered as text.
       * 
       * @instance
       * @param {object} currentValue The value currently
       * @param {object} targetValue The value to compare against
       */
      ruleValueComparator: function alfresco_forms_controls_BaseFormControl(currentValue, targetValue) {
         this.alfLog("log", "Comparing", currentValue, targetValue);

         // If both values aren't null then compare the .toString() output, if one of them is null
         // then it doesn't really matter whether or not we get the string output for the value or not
         if (currentValue != null && targetValue.value != null)
         {
            return currentValue.toString() == targetValue.value.toString();
         }
         else
         {
            // return currentValue == targetValue.value; // Commented out because I think this is wrong (shouldn't have .value)
            return currentValue == targetValue;
         }
      },
      
      /**
       * The payload of property value changing publications should have the following attributes... 
       *    1) The name of the property that has changed ("name")
       *    2) The old value of the property that has changed ("oldValue")
       *    3) The new value of the property that has changed ("value")
       *  Callbacks should take the following arguments (nameOfChangedProperty, oldValue, newValue, callingObject, attribute)
       *  
       *  @instance
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
               _this.alfSubscribe("_valueChangeOf_" + key, function(payload) {
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
       * @instance
       */
      postCreate: function alfresco_forms_controls_BaseFormControl__postCreate() {
         
         var _this = this;
         
         /*
          * These are the types of attributes we expect a form to have...
          * Field label (e.g. �Name�)
          * Description (e.g. hover help)
            Units (e.g. �milliseconds�, etc)
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
         this.initialConfig = this.getWidgetConfig();
         
         // Use the _disabled property if not already set...
         if (typeof this.initialConfig.disabled == "undefined") 
         {
            this.initialConfig.disabled = this._disabled;
         }
         this.wrappedWidget = this.createFormControl(this.initialConfig);
         
         // Check to see if the widget is "promised" or is expected to be returned immediately. If promised then
         // the "then" function needs to be hitched to the "onPromisedWidget" so that setup can be completed once it
         // has been delivered...
         if (this.isPromisedWidget && typeof this.wrappedWidget.then === "function")
         {
            this.wrappedWidget.then(lang.hitch(this, "onPromisedWidget"));
         }
         else
         {
            this.completeWidgetSetup();
         }
      },
      
      /**
       * This should be overridden when the [createFormControl function]{@link module:alfresco/forms/controls/BaseFormControl#createFormControl}
       * will return a Promise (that is a widget that will be created asynchronously rather than returned immediately. This allows
       * processing of the control to be deferred until it has been loaded. This will typically be needed when it is not possible
       * to pre-load the required JavaScript files into the cache.
       * 
       * @instance
       * @type {boolean}
       * @default false
       */
      isPromisedWidget: false,
      
      /**
       * This function is called when [isPromisedWidget]{@link module:alfresco/forms/controls/BaseFormControl#isPromisedWidget} is set to true.
       * It is the function that is hitched to the "then" function of the returned promise and is called when the promise is resolved (e.g.
       * when the widget has been created).
       * 
       * @instance
       * @param {object} promisedWidget The widget that was promised.
       */
      onPromisedWidget: function alfresco_forms_controls_BaseFormControl__onPromisedWidget(promisedWidget) {
         this.wrappedWidget = promisedWidget;
         this.completeWidgetSetup();
      },
      
      /**
       * Handles adding the wrapped widget into the DOM model provided by the template. By default this assumes that
       * the widget is a Dojo widget and calls it's  "placeWidget" function.
       * 
       * @instance
       */
      placeWidget: function alfresco_forms_controls_BaseFormControl__placeWrappedWidget() {
         if (this.wrappedWidget != null && typeof this.wrappedWidget.placeAt === "function")
         {
            this.wrappedWidget.placeAt(this._controlNode);
         }
         else
         {
            this.alfLog("error", "The wrapped widget has no 'placeAt' function - perhaps the 'placeWidget' function should be overridden?", this);
         }
      },
      
      /**
       * @instance
       */
      completeWidgetSetup: function alfresco_forms_controls_BaseFormControl__setupChangeEvents() {
         
         // Place the widget into the DOM provided by the template...
         this.placeWidget();
         
         // Set up the events that indicate a change in value...
         this.setupChangeEvents();
         
         // Set the initial visibility, requirement and disablement...
         this.alfVisible(this._visible);
         this.alfRequired(this._required);
         this.alfDisabled(this._disabled);
         
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
         
         if (this.description != null && 
             this.wrappedWidget != null && 
             this.description != "")
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
       * Whenever a widgets value changes we need to publish the details out to the other form controls (that exist in the
       * same scope) so that they can modify their appearance/behaviour as necessary). This function sets up the default events 
       * that indicate that a wigets value has changed. This function can be overridden to handle non-Dojo widgets or when 
       * multiple widgets represent a single control. 
       * 
       * @instance
       */
      setupChangeEvents: function alfresco_forms_controls_BaseFormControl__setupChangeEvents() {
         if (this.wrappedWidget)
         {
            // TODO: Do we need to do anything with the watch handle when the widget is destroyed?
            var watchHandle = this.wrappedWidget.watch("value", lang.hitch(this, "onValueChangeEvent"));
         }
      },
      
      /**
       * Handles the change in value for the current form control by publishing the details of the change and calling the
       * validate function to check that the new value is acceptable.
       * 
       * @instance
       * @param {string} name
       * @param {string} oldValue
       * @param {string} value
       */
      onValueChangeEvent: function alfresco_forms_controls_BaseFormControl__onValueChangeEvent(name, oldValue, value) {
         this.formControlValueChange(name, oldValue, value);
         this.validate();
      },
      
      /**
       * This gets the value currently assigned to the wrapped widget. It assumes the widget has a single "value"
       * attribute that can be retrieved (i.e. it assumes a Dojo widget). Any extending classes that do not use
       * Dojo widgets (or use multiple widgets) should override this implementation to return the correct value.
       * 
       * @instance
       * @returns {object} The current value of the field.
       */
      getValue: function alfresco_forms_controls_BaseFormControl__getValue() {
         var value = null;
         if (this.wrappedWidget)
         {
            try
            {
               value = this.wrappedWidget.getValue();
            }
            catch(e)
            {
               this.alfLog("log", "An exception was thrown retrieving the value for field: '" + this.fieldId + "'");
            }
         }
         // Commented out because it does make the logging quite verbose
         // this.alfLog("log", "Returning value for field: '" + this.fieldId + "': ", value);
         return value;
      },
      
      /**
       * 
       * @instance
       * @param {object} value The value to set.
       */
      setValue: function alfresco_forms_controls_BaseFormControl__setValue(value) {
         this.alfLog("log", "Setting field: '" + this.fieldId + "' with value: ", value);
         if (this.wrappedWidget)
         {
            this.wrappedWidget.setValue(value);
         }
      },
      
      /**
       * This function publishes the current value of the widget. It is provided so that enclosing forms can publish
       * all of its controls values to process all rules.
       * 
       * @instance
       */
      publishValue: function alfresco_forms_controls_BaseFormControl__publishValue() {
         this.alfLog("log", "Publishing value for field: '" + this.fieldId + "'");
         if (this.wrappedWidget)
         {
            this.alfPublish("_valueChangeOf_" + this.fieldId, {
               fieldId: this.fieldId,
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
       * @instance
       * @param {string} attributeName
       * @param {object} oldValue
       * @param {object} value
       */
      formControlValueChange: function alfresco_forms_controls_BaseFormControl__formControlValueChange(attributeName, oldValue, value) {
         this.alfPublish("_valueChangeOf_" + this.fieldId, {
            fieldId: this.fieldId,
            name: this.name,
            oldValue: oldValue,
            value: value
         });
      },
      
      /**
       * 
       * @instance
       * @param {object} config
       */
      createFormControl: function alfresco_forms_controls_BaseFormControl__createFormControl(config) {
         // Extension point
      },
      
      /**
       * 
       * @instance
       * @returns {object} The configuration for the form control.
       */
      getWidgetConfig: function alfresco_forms_controls_BaseFormControl__getWidgetConfig() {
         // This is a method that is expected to be overridden. We won't even assume that the widget configuration
         // will be standard Dojo configuration because we might be instantiated a custom or 3rd party library widget.
         return {};
      },
      
      /**
       * 
       * @instance
       */
      startup: function alfresco_forms_controls_BaseFormControl__startup() {
         
      },
      
      /**
       * This will hold all of the configuration for validation. It is initialised in the constructor.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      validationConfig: null,
      
      /**
       * This function validates the current widget value.
       * 
       * @instance
       * @returns {boolean} A value indicating whether or not validation passed successfully or not.
       */
      validate: function alfresco_forms_controls_BaseFormControl__validate() {
         
         var isValid = this.processValidationRules();
         
         // Publish the results (this topic is provided primarily of an enclosing form)...
         if (isValid)
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
         return isValid;
      },
      
      /**
       * This function defines the default validation processing. It should be overridden by extending form controls
       * that do not use the default rules. This function rather than the "validate" function should be overridden
       * because this function simply indicates whether or not the control is valid but the "validate" function 
       * controls the rendering of error messages and publication of related events.
       *
       * The rules are only processed if the field is visible and enabled because only those fields have their values
       * included in the overall form value (e.g. invisible fields won't be submitted so it doesn't matter if their
       * contents is invalid)
       * 
       * @instance
       * @returns {boolean} Indicates whether or not the validation rules were passed successfully
       */
      processValidationRules: function alfresco_forms_controls_BaseFormControl__processValidationRules() {

         var valid = true;
         if (this._visible && !this._disabled)
         {
            // Things to validate against are...
            // 1) Does the widget have a value if it is required
            var value = this.getValue();
            
            this.alfLog("log", "Validating: '" + this.fieldId + "' with value:", value);
            
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
            valid = passedRequiredTest && passedRegExpTest;
         }
         else
         {
            // No action required if field is invisible or disabled
         }
         return valid;
      },
      
      /**
       * By default this simply adds the "validation-error" and "display" classes to the _validationIndicator
       * and _validationMessage DOM nodes respectively. However, the code has been broken out into a separate function
       * to support extending classes that may provide alternative HTML templates or wish to render errors
       * differently.
       * 
       * @instance
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
       * @instance
       */
      hideValidationFailure: function alfresco_forms_controls_BaseFormControl__hideValidationFailure() {
         domClass.remove(this._validationIndicator, "validation-error");
         domClass.remove(this._validationMessage, "display");
      }
   });
});
