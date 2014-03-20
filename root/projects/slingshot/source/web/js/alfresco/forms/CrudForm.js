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
 * This module extends the standard [Form widget]{@link module:alfresco/forms/Form} to provide the ability
 * to change the data and buttons displayed based upon topics published relating to various CRUD states.
 * For example the same form can show empty (or default) fields for creating new objects or can be updated
 * with data for existing items. The buttons will vary based on the current form state, e.g. "Delete" and "Update"
 * will be shown for existing data, but will not be displayed when creating new items.
 * 
 * @module alfresco/forms/DynamicForm
 * @extends module:alfresco/forms/Form
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/forms/Form",
        "dojo/_base/lang",
        "dojo/_base/array",
        "alfresco/buttons/AlfButton",
        "dojo/dom-construct",
        "dojo/dom-style",
        "dijit/registry"], 
        function(declare, Form, lang, array, AlfButton, domConstruct, domStyle, registry) {
   
   return declare([Form], {
      
      /**
       * This should be configured to be an object that contains the default data set to use
       * for creating new instances. This object will be passed to the 
       * [setValue]{@link module:alfresco/forms/Form#setValue} function when the "create" form
       * state is entered.
       * 
       * @instance
       * @type {object}
       * @default
       */
      defaultData: null,

      /**
       * Subscribes to 
       * @instance
       */
      postMixInProperties: function alfresco_forms_CrudForm__postMixInProperties() {

         // There are two main states for the form - creating new items and updating
         // existing updates...
         this.alfSubscribe("ALF_CRUD_FORM_CREATE", lang.hitch(this, "onShowCreateState"));
         this.alfSubscribe("ALF_CRUD_FORM_UPDATE", lang.hitch(this, "onShowUpdateState"));

      },

      /**
       * Extends the default implementation to capture the form fields and update the default
       * data object.
       * 
       * @instance
       * @param {array} widgets The created form controls
       */
      allWidgetsProcessed: function alfresco_forms_CrudForm__allWidgetsProcessed(widgets) {
         this.inherited(arguments);

         // Collect the initial set of form data...
         var startingFormData = {};
         array.forEach(this._form.getChildren(), function(entry, i) {
            lang.setObject(entry.get("name"), entry.getValue(), startingFormData);
         });

         if (this.defaultData != null)
         {
            // Mix the default data into the starting form data (this overrides what's initialised in the form)
            lang.mixin(startingFormData, this.defaultData);
         }
         else
         {
            // Use the initial form data as the default data set for creating new items...
            this.defaultData = startingFormData;
         }
      },

      /**
       * Overrides the default implementation to creates the "Create", "Update" and "Save" buttons
       * 
       * @instance
       */
      createButtons: function alfresco_forms_CrudForm__createButtons() {
         
         // Clear the default buttonsNode...
         domConstruct.empty(this.buttonsNode);

         // Create the "Create" button...
         // TODO: Should be using "createWidget" helper function
         this.createButtonNode = domConstruct.create("div", null, this.buttonsNode);
         this.createButton = new AlfButton({
            pubSubScope: this.pubSubScope,
            label: this.message(this.createButtonLabel),
            additionalCssClasses: "createButton",
            publishTopic: this.createButtonPublishTopic,
            publishPayload: this.createButtonPublishPayload,
            publishGlobal: this.createButtonPublishGlobal
         }, this.createButtonNode);

         // Create the "Update" button...
         // TODO: Should be using "createWidget" helper function
         this.updateButtonNode = domConstruct.create("div", null, this.buttonsNode);
         this.updateButton = new AlfButton({
            pubSubScope: this.pubSubScope,
            label: this.message(this.updateButtonLabel),
            additionalCssClasses: "updateButton",
            publishTopic: this.updateButtonPublishTopic,
            publishPayload: this.updateButtonPublishPayload,
            publishGlobal: this.updateButtonPublishGlobal
         }, this.updateButtonNode);

         // Create the "Delete" button...
         // TODO: Should be using "createWidget" helper function
         this.deleteButtonNode = domConstruct.create("div", null, this.buttonsNode);
         this.deleteButton = new AlfButton({
            pubSubScope: this.pubSubScope,
            label: this.message(this.deleteButtonLabel),
            additionalCssClasses: "deleteButton",
            publishTopic: this.deleteButtonPublishTopic,
            publishPayload: this.deleteButtonPublishPayload,
            publishGlobal: this.deleteButtonPublishGlobal
         }, this.deleteButtonNode);
         
         // Copied from original code - might not need the IF condition...
         if (this.widgetsAdditionalButtons != null)
         {
            this.additionalButtons = [];
            this.processWidgets(this.widgetsAdditionalButtons, this.buttonsNode);
         }
         else
         {
            this.additionalButtons = registry.findWidgets(this.buttonsNode);
         }

         // Initialise to the create state...
         this.onShowCreateState(null);
      },

      /**
       * Hides the "Update" and "Delete" buttons and shows the "Create" button and updates the 
       * form to display the default data.
       *
       * @instance
       * @param {object} payload The details of the create state
       */
      onShowCreateState: function alfresco_forms_CrudForm__onShowCreateState(payload) {
         // Update the form to display the default data.
         this.setValue(this.defaultData);

         // TODO: Change the displayed buttons (show the "Create" button, hide the "Update" & "Delete" buttons)
         domStyle.set(this.createButton.domNode, "display", "");
         domStyle.set(this.updateButton.domNode, "display", "none");
         domStyle.set(this.deleteButton.domNode, "display", "none");

         // Set the "OK" button as the "Create" button and then validate so that it is enabled/disabled appropriately
         this.okButton = this.createButton; 
         this.validate();
      },

      /**
       * Hides the "Create" button and shows the "Update" and "Delete" buttons. Updates the
       * form with the requested data.
       * 
       * @instance
       * @param {object} payload The details of the create state
       */
      onShowUpdateState: function alfresco_forms_CrudForm__onShowUpdateState(payload) {

         // In the future, this should potentially make an XHR request to get the latest data,
         // but for now just use the data provided in the payload. It is expected that the payload
         // will contain all the relevant data.
         this.setValue(payload);

         domStyle.set(this.createButton.domNode, "display", "none");
         domStyle.set(this.updateButton.domNode, "display", "");
         domStyle.set(this.deleteButton.domNode, "display", "");

         // Set the "OK" button as the "Update" button and then validate so that it is enabled/disabled appropriately
         this.okButton = this.updateButton; 
         this.validate();
      }
   });
});