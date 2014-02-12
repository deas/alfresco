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
 * This mixin is intended to be mixed into any buttons or menu items that require an action that creates a new 
 * [dialog]{@link module:alfresco/dialogs/AlfDialog} that contains a [form]{@link module:alfresco/forms/Form}.
 * 
 * Examples of use include the create content menu items in the document library.
 *
 * @module alfresco/dialogs/_CreateFormDialogMixin
 * @extends module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "alfresco/dialogs/AlfDialog",
        "alfresco/forms/Form"],
        function(declare, AlfCore, lang, AlfDialog, AlfForm) {
   
   return declare([AlfCore], {

      /**
       * Create a new 'publishTopic' for the action and generates a new 'pubSubScope' and then sets 
       * up subscriptions for handling show dialog and cancel dialog requests.
       * 
       * @instance
       */
      postCreate: function alfresco_dialogs__CreateFormDialogMixin__postCreate() {
         // Generate a new pub/sub scope for the widget (this will intentionally override any other settings
         // to contrain communication...
         this.inherited(arguments);
         this.pubSubScope = this.generateUuid();
         this.publishTopic = "ALF_CREATE_FORM_DIALOG_MIXIN_REQUEST_TOPIC";
         this.alfSubscribe(this.publishTopic, lang.hitch(this, "onCreateFormDialogRequest"));
         this.alfSubscribe(this._formConfirmationTopic, lang.hitch(this, "onDialogConfirmation"));
      },

      /**
       * @instance
       * @type {string}
       * @default "ALF_CREATE_FORM_DIALOG_MIXIN_CONFIRMATION_TOPIC"
       */
      _formConfirmationTopic: "ALF_CREATE_FORM_DIALOG_MIXIN_CONFIRMATION_TOPIC",

      /**
       * Keeps a reference to any dialogs that are created. Old dialogs are destroyed before
       * new ones are created.
       *
       * @instance
       * @type {object}
       * @default null
       */
      dialog: null,

      /**
       * The title for the dialog.
       *
       * @instance
       * @type {string}
       * @default ""
       */
      dialogTitle: "",

      /**
       * TODO: Default needs localizing
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      dialogConfirmationButtonTitle: "OK",

      /**
       * TODO: Default needs localizing
       *
       * @instance
       * @type {string}
       * @default ""
       */
      dialogCancellationButtonTitle: "Cancel",

      /**
       * The configuration for the contents of the dialog to be displayed. This should be provided either on instantiation
       * or by the widget that mixes this module in
       *
       * @instance
       * @type {object}
       * @default null
       */
      widgets: null,

      /**
       * Handles requests to create the [dialog]{@link module:alfresco/dialogs/AlfDialog} containining a
       * [form]{@link module:alfresco/forms/Form}. It will delete any previously created dialog (to ensure
       * no stale data is displayed) and create a new dialog containing the form defined.
       *
       * @instance
       * @param {object} payload The payload published on the request topic.
       */ 
      onCreateFormDialogRequest: function alfresco_dialogs__CreateFormDialogMixin__onCreateFormDialogRequest(payload) {
         // Destroy any previously created dialog...
         if (this.dialog != null)
         {
            this.dialog.destroyRecursive();
         }

         if (this.widgets == null)
         {
            this.alfLog("warn", "A request was made to display a dialog but no 'widgets' attribute has been defined", this);
         }
         else
         {
            try
            {
               var createFormConfig = this.createFormConfig(this.widgets);
               var dialogConfig = this.createDialogConfig(createFormConfig);
               this.dialog = new AlfDialog(dialogConfig);
               this.dialog.show();
            }
            catch (e)
            {
               this.alfLog("error", "The following error occurred creating a dialog for defined configuration", e, this.dialogConfig, this);
            }
         }
      },

      /**
       * Creates the configuration object to pass to the dialog.
       *
       * @instance
       * @param {object} formConfig
       * @returns {object} The dialog configuration.
       */
      createDialogConfig: function alfresco_dialogs__CreateFormDialogMixin__createDialogConfig(formConfig) {
         var dialogConfig = {
            title: this.message(this.dialogTitle),
            pubSubScope: this.pubSubScope, // Scope the dialog content so that it doesn't pollute any other widgets
            widgetsContent: [formConfig],
            widgetsButtons: [
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: this.dialogConfirmationButtonTitle,
                        disableOnInvalidControls: true,
                        additionalCssClasses: "alfresco-dialogs-_AlfCreateFormDialogMixin confirmation",
                        publishTopic: this._formConfirmationTopic
                     }
                  },
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: this.dialogCancellationButtonTitle,
                        additionalCssClasses: "alfresco-dialogs-_AlfCreateFormDialogMixin cancellation",
                        publishTopic: "ALF_CLOSE_DIALOG"
                     }
                  }
               ]
         };
         return dialogConfig;
      },

      /**
       * Creates and returns the [form]{@link module:alfresco/forms/Form} configuration to be added to the [dialog]{@link module:alfresco/dialog/AlfDialog}
       *
       * @instance
       * @param {object} widgets This is the configuration of the fields to be included in the form.
       * @returns {object} The configuration for the form to add to the dialog
       */
      createFormConfig: function alfresco_dialogs__CreateFormDialogMixin__createFormConfig(widgets) {
         var formConfig = {
            name: "alfresco/forms/Form",
            config: {
               displayButtons: false,
               widgets: widgets
            }
         };
         return formConfig;
      },

      /**
       * This is the topic that will be published when the dialog is "confirmed" (e.g. the "OK" button is clicked)
       * 
       * @instance
       * @type {string}
       * @default null
       */
      formSubmissionTopic: null,

      /**
       * This handles the user clicking the confirmation button on the dialog (typically, and by default the "OK" button). This has a special
       * handler to process the  payload and construct a simple object reqpresenting the
       * content of the inner [form]{@link module:alfresco/forms/Form}.
       * 
       * @instance
       * @param {object} payload The dialog content
       */
      onDialogConfirmation: function alfresco_dialogs__CreateFormDialogMixin__onDialogConfirmation(payload) {
         if (payload != null && 
             payload.dialogContent != null &&
             payload.dialogContent.length == 1 &&
             typeof payload.dialogContent[0].getValue === "function")
         {
            var data = payload.dialogContent[0].getValue();
            this.alfPublish(this.formSubmissionTopic, data, true);
         }
         else
         {
            this.alfLog("error", "The format of the dialog content was not as expected, the 'formSubmissionTopic' will not be published", payload, this);
         }
      }
   });
});