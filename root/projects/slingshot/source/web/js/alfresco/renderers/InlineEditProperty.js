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
 * Extends the standard (read-only) [property renderer]{@link module:alfresco/renderers/Property} to provide
 * the ability to edit and save changes to the property. The select menu is rendered by a
 * [DojoValidationTextBox widget]{@link module:alfresco/forms/controls/DojoValidationTextBox} and this module accepts the same 
 * [validationConfig]{@link module:alfresco/forms/controls/BaseFormControl#validationConfig} as it does.
 * 
 * @module alfresco/renderers/InlineEditProperty
 * @extends module:alfresco/renderers/Property
 * @mixes dijit/_OnDijitClickMixin
 * @mixes module:alfresco/core/CoreWidgetProcessing
 * @mixes module:alfresco/renderers/_PublishPayloadMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/renderers/Property", 
        "dijit/_OnDijitClickMixin",
        "alfresco/core/CoreWidgetProcessing",
        "alfresco/renderers/_PublishPayloadMixin",
        "dojo/text!./templates/InlineEditProperty.html",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/on",
        "dojo/dom-class",
        "dojo/html",
        "dojo/dom-attr",
        "dojo/_base/fx",
        "dojo/keys",
        "dojo/_base/event",
        "service/constants/Default",
        "alfresco/forms/Form",
        "alfresco/forms/controls/DojoValidationTextBox",
        "alfresco/forms/controls/HiddenValue"], 
        function(declare, Property, _OnDijitClickMixin, CoreWidgetProcessing, _PublishPayloadMixin, 
                 template, lang, array, on, domClass, html, domAttr, fx, keys, event, AlfConstants, Form, DojoValidationTextBox) {

   return declare([Property, _OnDijitClickMixin, CoreWidgetProcessing, _PublishPayloadMixin], {
      
      /**
       * The array of file(s) containing internationalised strings.
       *
       * @instance
       * @type {object}
       * @default [{i18nFile: "./i18n/InlineEditProperty.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/InlineEditProperty.properties"}],

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/InlineEditProperty.css"}]
       */
      cssRequirements: [{cssFile:"./css/InlineEditProperty.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * The is the name of the parameter that will be used to persist changes to the property
       * @instance
       * @type {string}
       * @default null
       */
      postParam: null,
      
       /**
       * This is the message or message key that will be used for save link text.
       *
       * @instance
       * @type {string}
       * @default "inline-edit.save.label"
       */
      saveLabel: "inline-edit.save.label",

       /**
       * This is the message or message key that will be used for the cancel link text.
       *
       * @instance
       * @type {string}
       * @default "inline-edit.cancel.label"
       */
      cancelLabel: "inline-edit.cancel.label",

      /**
       * This is the message or message key that will be used for the alt text attribute on the edit icon
       *
       * @instance
       * @type {string}
       * @default "inline-edit.edit.altText"
       */
      editAltText: "inline-edit.edit.altText",

      /**
       * This is the message or message key that will be used for the label attribute on the edit label
       *
       * @instance
       * @type {string}
       * @default "inline-edit.edit.label"
       */
      editLabel: "inline-edit.edit.label",

      /**
       * This extends the inherited function to set the [postParam]{@link module:alfresco/renderers/InlineEditProperty#postParam]
       * attribute based on the [propertyToRender]{@link module:alfresco/renderers/InlineEditProperty#propertyToRender] if 
       * provided. It is expected that these will be different because the properties WebScript that this widget will use
       * by default to persist changes takes just the name of the property but this is expected to be nested within the
       * [currentItem]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentItem}.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_renderers_InlineEditProperty__postMixInProperties() {
         this.inherited(arguments);
         
         if (this.propertyToRender != null)
         {
            if (this.postParam == null && this.propertyToRender != null)
            {
               this.postParam = this.propertyToRender;
            }
         }
         else
         {
            this.alfLog("warn", "Property to render attribute has not been set", this);
         }

         if (this.editIconImageSrc == null || this.editIconImageSrc == "")
         {
            this.editIconImageSrc = require.toUrl("alfresco/renderers") + "/css/images/edit-16.png";
         }

         // Localize the labels and alt-text...
         this.saveLabel = this.message(this.saveLabel);
         this.cancelLabel = this.message(this.cancelLabel);
         this.editAltText = this.message(this.editAltText, {
            0: this.renderedValue
         });
      },

      /**
       * Emits a custom event to notify any containers that use keyboard navigation that handling
       * keyboard events needs to be suppressed whilst editing is taking place. If the argument
       * is passed as false then it emits a custom event that indicates to containers that keyboard
       * navigation can resume.
       *
       * @instance
       * @param {boolean} suppress Whether or not to suppress keyboard navigation
       */
      suppressContainerKeyboardNavigation: function alfresco_renderers_InlineEditProperty__suppressContainerKeyboardNavigation(suppress) {
         on.emit(this.domNode, "onSuppressKeyNavigation", {
            bubbles: true,
            cancelable: true,
            suppress: suppress
         });
      },

      /**
       * References the widget used for editing. Created by calling the 
       * [getFormWidget]{@link module:alfresco/renderers/InlineEditProperty#getFormWidget}
       * for the first time.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      formWidget: null,

      /**
       * <p>In certain circimstances it may be necessary to submit additional data along with that
       * provided by the main edit control. This configuration property should take the form:</p>
       * <p><pre>hiddenDataRules: [
       *   {
       *     name: "customProperties",
       *     rulePassValue: "hiddenData",
       *     ruleFailValue: "",
       *     is: ["includeHiddenData"]
       *   }
       * ]</pre></p>
       *
       * @instance
       * @type {array}
       * @default null
       */
      hiddenDataRules: null,

      /**
       * Gets the form widget that will be rendered as the edit field. By default this will 
       * return a [validation textbox]{@link module:alfresco/forms/controls/DojoValidationTextBox}
       * but can be overridden to return alternative form controls.
       * 
       * @instance
       */
      getPrimaryFormWidget: function alfresco_renderers_InlineEditProperty__getPrimaryFormWidget() {
         return {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: this.postParam,
               validationConfig: this.validationConfig,
               additionalCssClasses: "hiddenlabel",
               label: this.message(this.editLabel)
            }
         };
      },

      /**
       * In certain circimstances it may be necessary to submit additional data along with that
       * provided by the main edit control. This function processes configurable hidden data rules
       * that generates an array of [hidden form controls]{@link module:alfresco/forms/controls/HiddenValue}
       * that are configured with [autoSetConfig]{@link module:alfresco/forms/controls/BaseFormControl#autoSetConfig}
       * that is derived from the [hiddenDataRules]{@link module:alfresco/renderers/InlineEditProperty#hiddenDataRules}.
       * 
       * @instance
       */
      processHiddenDataRules: function alfresco_renderers_InlineEditProperty__processHiddenDataRules() {
         var additionalFormWidgets = []
         if (this.hiddenDataRules != null)
         {
            array.forEach(this.hiddenDataRules, lang.hitch(this, this.processHiddenDataRule, additionalFormWidgets));
         }
         return additionalFormWidgets;
      },

      /**
       * Called for each entry in the [hiddenDataRules]{@link module:alfresco/renderers/InlineEditProperty#hiddenDataRules}
       * configuration to add a new [hidden form control]{@link module:alfresco/forms/controls/HiddenValue} definition
       * into the supplied array.
       * 
       * @instance
       * @param {array} additionalFormWidgets The array to add additional form widgets into
       * @param {object} rule The current hidden data rule to process.
       */
      processHiddenDataRule: function alfresco_renderers_InlineEditProperty__processHiddenDataRule(additionalFormWidgets, rule) {
         additionalFormWidgets.push({
            name: "alfresco/forms/controls/HiddenValue",
            config: {
               name: rule.name,
               value: "",
               autoSetConfig: [
                  {
                     rulePassValue: rule.rulePassValue,
                     ruleFailValue: rule.ruleFailValue,
                     rules: [{
                        targetId: "PRIMARY_FIELD",
                        is: rule.is,
                        isNot: rule.isNot
                     }]
                  }
               ]
            }
         });
      },

      /**
       * Gets the edit widget (creating it the first time it is requested).
       *
       * @instance
       * @returns {object} The widget for editing.
       */
      getFormWidget: function alfresco_renderers_InlineEditProperty__getFormWidget() {
         if (this.formWidget === null)
         {
            var primaryFormWidget = this.getPrimaryFormWidget();
            var autoSetFields = this.processHiddenDataRules();
            lang.setObject("config.fieldId", "PRIMARY_FIELD", primaryFormWidget);
            this.formWidget = this.createWidget({
               name: "alfresco/forms/Form",
               config: {
                  showOkButton: false,
                  showCancelButton: false,
                  widgets: [primaryFormWidget].concat(autoSetFields)
               }
            }, this.formWidgetNode);
         }
         return this.formWidget;
      },

      /**
       * This function is called whenever the user clicks on the edit icon. It hides the display DOM node
       * and shows the edit DOM nodes.
       * 
       * @instance
       */
      onEditClick: function alfresco_renderers_InlineEditProperty__onEditClick(evt) {
         this.suppressContainerKeyboardNavigation(true);
         var formWidget = this.getFormWidget();
         var o = new Object();
         var o = {};
         lang.setObject(this.postParam, this.originalRenderedValue, o);
         formWidget.setValue(o);
         domClass.toggle(this.renderedValueNode, "hidden");
         domClass.toggle(this.editNode, "hidden");
         formWidget.focus() // Focus on the input node so typing can occur straight away
         if (evt != undefined) event.stop(evt);
      },
      
      /**
       * Checks for the CTRL-e combination and when detected moves into edit mode.
       * 
       * @instance
       * @param {object} evt The keypress event
       */
      onKeyPress: function alfresco_renderers_InlineEditProperty__onKeyPress(evt) {
         if (evt.ctrlKey == true && evt.charCode == "101")
         {
            // On ctrl-e simulate an edit click
            event.stop(evt);
            this.onEditClick();
         }
      },
      
      /**
       * This function is connected via the widget template. It occurs whenever a key is pressed whilst
       * focus is on the input field for updating the property value. All keypress events other than the
       * enter and escape key are ignored. Enter will save the data, escape will cancel editing
       * 
       * @instance
       * @param {object} e The key press event
       */
      onValueEntryKeyPress: function alfresco_renderers_InlineEditProperty__onValueEntryKeyPress(e) {
         if(e.charOrCode == keys.ESCAPE)
         {
            event.stop(e);
            this.onCancel();
         }
         // NOTE: This isn't currently working because Dojo form controls suppress certain keys, including ENTER...
         else if(e.charOrCode == keys.ENTER)
         {
            event.stop(e);
            this.onSave();
         }
      },

      /**
       * @instance
       */
      onSave: function alfresco_renderers_InlineEditProperty__onSave(evt) {
         var responseTopic = this.generateUuid();
         var payload = lang.clone(this.getGeneratedPayload(false, null));
         payload.alfResponseTopic = responseTopic;
         this._saveSuccessHandle = this.alfSubscribe(responseTopic + "_SUCCESS", lang.hitch(this, this.onSaveSuccess), true);
         this._saveFailureHandle = this.alfSubscribe(responseTopic + "_FAILURE", lang.hitch(this, this.onSaveFailure), true);
         this.updateSaveData(payload);
         this.alfPublish(this.publishTopic, payload, true);
         
         // TODO: Set some sort of indicator to show that a save operation is in flight?
      },

      /**
       * Updates the supplied payload with the current form value.
       *
       * @instance
       * @param {object} payload The save payload to update.
       */
      updateSaveData: function alfresco_renderers_InlineEditProperty__getSaveData(payload) {
         lang.mixin(payload, this.getFormWidget().getValue());
      },

      /**
       * Called following successful save attempts. This will update the read-only display using the requested save
       * data.
       * 
       * @instance
       * @param {object} payload The success payload
       */
      onSaveSuccess: function alfresco_renderers_InlineEditProperty__onSaveSuccess(payload) {
         this.alfUnsubscribeSaveHandles([this._saveSuccessHandle, this._saveFailureHandle]);

         this.alfLog("log", "Property '" + this.propertyToRender + "' successfully updated for node: ", this.currentItem);
         this.originalRenderedValue = this.getFormWidget().getValue()[this.postParam];
         this.renderedValue = this.mapValueToDisplayValue(this.originalRenderedValue);
         
         // This is a bit ugly... there will be better ways to handle this...
         // Basically it's handling the situation where the prefix/suffix for cleared when data wasn't originally available...
         var prefix = (this.requestedValuePrefix) ? this.requestedValuePrefix : this.renderedValuePrefix;
         var suffix = (this.requestedValueSuffix) ? this.requestedValueSuffix : this.renderedValueSuffix;
         
         html.set(this.renderedValueNode, prefix + this.renderedValue + suffix);
         domClass.remove(this.renderedValueNode, "hidden faded");
         domClass.add(this.editNode, "hidden");
         this.renderedValueNode.focus();
      },

      /**
       * Called following a failed save attempt. Cancels the edit mode.
       * TODO: Issues an error message
       * 
       * @instance
       * @param {object} payload The success payload
       */
      onSaveFailure: function alfresco_renderers_InlineEditProperty__onSaveFailure(payload) {
         this.alfUnsubscribeSaveHandles([this._saveSuccessHandle, this._saveFailureHandle]);
         this.alfLog("warn", "Property '" + this.propertyToRender + "' was not updated for node: ", this.currentItem);
         this.onCancel();
      },
      
      /**
       * Called when a user cancels out of edit mode. Returns the read-only display to its original state
       * before editing began.
       *
       * @instance
       */
      onCancel: function alfresco_renderers_InlineEditProperty__onCancel() {
         this.suppressContainerKeyboardNavigation(false);

         domClass.remove(this.renderedValueNode, "hidden");
         domClass.add(this.editNode, "hidden");
         
         // Reset the input field...
         this.getFormWidget().setValue(this.renderedValue);
         this.renderedValueNode.focus();
      },
      
      /**
       * TODO: Replace with CSS3
       * @instance
       */
      showEditIcon: function alfresco_renderers_InlineEditProperty__showEditIcon() {
         fx.fadeIn({ node: this.editIconNode }).play();
      },
      
      /**
       * TODO: Replace with CSS3
       * @instance
       */
      hideEditIcon: function alfresco_renderers_InlineEditProperty__hideEditIcon() {
         fx.fadeOut({ node: this.editIconNode }).play();
      }
   });
});