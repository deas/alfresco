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
 * A form control that wraps the ACE editor (see: http://ace.c9.io/)

 * PLEASE NOTE:
 * In order for the scratchpad to work you will need to download the source for the ACE editor and
 * place it into the js/lib/ace directory, you will also need to update the package information in
 * surf.xml (e.g. <package name="acelib" location="js/lib/ace"/>)
 * 
 * @module alfresco/forms/controls/AceEditor
 * @extends module:alfresco/forms/controls/BaseFormControl
 * @author Dave Draper
 */
define(["alfresco/forms/controls/BaseFormControl",
        "dojo/_base/declare",
        "dojo/Deferred",
        "dojo/dom-construct",
        "dojo/_base/lang",
        "dojo/_base/array",
        "ace/ace",
        "ace/theme/tomorrow"], 
        function(BaseFormControl, declare, Deferred, domConstruct, lang, array, ace, aceTomorrowTheme) {
   
   return declare([BaseFormControl], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {Array}
       */
      cssRequirements: [{cssFile:"./css/AceEditor.css"}],

      /**
       * This is the editor mode for the embedded ACE editor. By default it is "html". This
       * will control the syntax highlighting.
       * 
       * @instance
       * @type {string}
       * @default null
       */
      editMode: null,

      /**
       * @instance
       */
      getWidgetConfig: function alfresco_forms_controls_AceEditor__getWidgetConfig() {
         // Return the configuration for the widget
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value
         };
      },
      
      /**
       * A map of MIME type to editor mode.
       *
       * @instance
       * @type {object}
       */
      mimeTypeMap: {
         "text/xml":"xml",
         "text/javascript":"javascript",
         "text/plain":"text",
         "text/html":"html"
      },

      /**
       * Instantiates a new JSON editor, places it in the template DOM and setups the change events.
       * 
       * The ACE module sneakily overwrites the "require" variable which will prevent subsequent
       * calls to the Dojo require from working, therefore it's necessary to keep a reference to
       * the original function and then undo the damage once ACE has been loaded...
       * Although we could have included these in the modules "define" statement and have them
       * preloaded into the Dojo cache would break the page as soon as the ACE module is processed :(
       * 
       * @instance
       */
      createFormControl: function alfresco_forms_controls_AceEditor__createFormControl(config) {
         
         // Create a new element to attach the editor to...
         var generatedId = this.generateUuid();
         var targetElement = domConstruct.create("div", { 
            className: "alfresco-forms-controls-AceEditor",
            id: generatedId
         }, this._controlNode);
         
         // Select the edit mode based on either the requested mode or the requested MIME type...
         var mode = "text";
         if (this.editMode != null)
         {
            mode = this.editMode;
         }
         else if (this.mimeType != null && this.mimeTypeMap[this.mimeType] != null)
         {
            mode = this.mimeTypeMap[this.mimeType];
         }

         this.wrappedWidget = ace.edit(generatedId);
         this.wrappedWidget.setTheme("ace/theme/tomorrow");
         this.wrappedWidget.getSession().setMode("ace/mode/" + mode);
         this.wrappedWidget.getSession().on('change', lang.hitch(this, "onEditorChange"));
         this.wrappedWidget.getSession().on('changeAnnotation', lang.hitch(this, "validate"));

            // Disable the "Tab" key binding for accessibility reasons...
         this.wrappedWidget.commands.bindKey("Tab", null);
         return this.wrappedWidget;
      },
      
      /**
       * Overrides the [default function]{@link module:alfresco/forms/controls/BaseFormControl#placeWidget}
       * to perform no action as all change events are setup during instantiation of the editor.
       * 
       * @instance
       */
      placeWidget: function alfresco_forms_controls_AceEditor__placeWrappedWidget() {
         // No action required. The widget is placed during the createFormControl function.
      },
      
      /**
       * Extends the [inherited function]{@link module:alfresco/forms/controls/BaseFormControl#placeWidget}
       * to ensure that the editor is intialised with the correct value.
       * 
       * @instance
       */
      completeWidgetSetup: function alfresco_forms_controls_AceEditor__completeWidgetSetup() {
         this.inherited(arguments);
         if (this.initialConfig.value != null)
         {
            this.wrappedWidget.setValue(this.initialConfig.value);
         }
      },
      
      /**
       * A callback handler that is hitched to change events in the instantiation of the JSON editor.
       * 
       * @instance
       */
      onEditorChange: function alfresco_forms_controls_AceEditor__onEditorChange() {
         if (this.wrappedWidget != null && typeof this.wrappedWidget.getValue === "function")
         {
            this._oldValue = this.__oldValue;
            this.__oldValue = this.wrappedWidget.getValue();
            this.formControlValueChange(this.name, this._oldValue, this.__oldValue);
            this.validate();
         }
      },
      
      /**
       * Extends the [default function]{@link module:alfresco/forms/controls/BaseFormControl#processValidationRules} to 
       * see if the editor has any annotations that are of type "error". If there are any error annotations then this will
       * return false.
       * 
       * @instance
       * @returns {boolean} True if the editor content is valid and false otherwise.
       */
      processValidationRules: function alfresco_forms_controls_AceEditor__processValidationRules() {
         var valid = this.inherited(arguments);

         if (this.wrappedWidget != null && typeof this.wrappedWidget.getSession === "function")
         {
            var annotations = this.wrappedWidget.getSession().getAnnotations();
            var hasErrorAnnotations = array.some(annotations, function(annotation) {
               return annotation.type == "error";
            });
            valid = valid && !hasErrorAnnotations;
         }
         return valid;
      },
      
      /**
       * This will be set to the last known value of the editor before the most recent change.
       * 
       * @instance
       * @type {string}
       * @default null
       */
      _oldValue: null,
      
      /**
       * This is used as a temporary buffer variable to keep track of changes to the old value. 
       * 
       * @instance
       * @type {string}
       * @default null
       */
      __oldValue: null,

      /**
       * Overrides the [default function]{@link module:alfresco/forms/controls/BaseFormControl#setupChangeEvents}
       * to perform no action as all change events are setup during instantiation of the editor.
       * 
       * @instance
       */
      setupChangeEvents: function alfresco_forms_controls_AceEditor__setupChangeEvents() {
         // No action required. The change events are handled in the set up of the JsonEditor
      },
      
      /**
       * Overrides the [inherited function]{@link module:alfresco/forms/controls/BaseFormControl#getValue} to
       * call the "getText" function on the editor. This returns the JSON text rather than a JavaScript object.
       * 
       * @instance
       * @returns {string} The JSON value entered into the editor
       */
      getValue: function alfresco_forms_controls_AceEditor__getValue() {
         var value = "";
         if (this.wrappedWidget != null && typeof this.wrappedWidget.getValue === "function")
         {
            value = this.wrappedWidget.getValue();
         }
         return value;
      }
   });
});