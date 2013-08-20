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
 * A form control for entering JSON. Includes formatting and validation of entered text.
 * 
 * PLEASE NOTE:
 * In order for the scratchpad to work you will need to download the source of https://github.com/josdejong/jsoneditor
 * and place it into the js/lib/jsoneditor directory, you will also need to update the package information in
 * surf.xml (e.g. <package name="jsoneditorlib" location="js/lib/jsoneditorlib"/>)
 * 
 * @module alfresco/forms/controls/JsonEditor
 * @extends module:alfresco/forms/controls/BaseFormControl
 * @author Dave Draper
 */
define(["alfresco/forms/controls/BaseFormControl",
        "dojo/_base/declare",
        "dojo/Deferred",
        "dojo/dom-construct",
        "dojo/_base/lang"], 
        function(BaseFormControl, declare, Deferred, domConstruct, lang) {
   
   return declare([BaseFormControl], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/JsonEditor.css"},
                        {cssFile:"/js/lib/jsoneditorlib/jsoneditor-min.css"}],

      /**
       * @instance
       * @type {boolean}
       * @default false
       */
      isPromisedWidget: true,
      
      /**
       * @instance
       */
      getWidgetConfig: function alfresco_forms_controls_DojoTextarea__getWidgetConfig() {
         // Return the configuration for the widget
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value
         };
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
      createFormControl: function alfresco_forms_controls_JsonEditor__createFormControl(config) {
         
         // The ACE module sneakily overwrites the "require" variable which will prevent subsequent
         // calls to the Dojo require from working, therefore it's necessary to keep a reference to
         // the original function and then undo the damage once ACE has been loaded...
         // Although we could have included these in the modules "define" statement and have them
         // preloaded into the Dojo cache would break the page as soon as the ACE module is processed :(
         var deferred = new Deferred();
         
         // Create a new element to attach the JsonEditor to...
         var targetElement = domConstruct.create("div", { className: "alfresco-forms-controls-JsonEditor" }, this._controlNode);
         
         var _this = this;
         var oldRequire = require;
         require(["jsoneditorlib/jsoneditor",
                  "jsoneditorlib/lib/ace/ace",
                  "jsoneditorlib/lib/jsonlint/jsonlint"], function (jsoneditor) {
            _this.wrappedWidget = new jsoneditor.JSONEditor(targetElement, {
               mode:'code',
               change: lang.hitch(_this, "onEditorChange")
            });
            require = oldRequire;
            deferred.resolve(_this.wrappedWidget);
         });
         return deferred.promise;
      },
      
      /**
       * Overrides the [default function]{@link module:alfresco/forms/controls/BaseFormControl#placeWidget}
       * to perform no action as all change events are setup during instantiation of the editor.
       * 
       * @instance
       */
      placeWidget: function alfresco_forms_controls_JsonEditor__placeWrappedWidget() {
         // No action required. The widget is placed during the createFormControl function.
      },
      
      /**
       * Extends the [inherited function]{@link module:alfresco/forms/controls/BaseFormControl#placeWidget}
       * to ensure that the editor is intialised with the correct value.
       * 
       * @instance
       */
      completeWidgetSetup: function alfresco_forms_controls_JsonEditor__completeWidgetSetup() {
         this.inherited(arguments);
         this.wrappedWidget.setText((this.initialConfig.value != null && this.initialConfig.value != "") ? this.initialConfig.value : "{}");
      },
      
      /**
       * A callback handler that is hitched to change events in the instantiation of the JSON editor.
       * 
       * @instance
       */
      onEditorChange: function alfresco_forms_controls_JsonEditor__onEditorChange() {
         if (typeof this.wrappedWidget.getText === "function")
         {
            this._oldValue = this.__oldValue;
            this.__oldValue = this.wrappedWidget.getText();
            this.formControlValueChange(this.name, this._oldValue, this.__oldValue);
            this.validate();
         }
      },
      
      /**
       * Overrides the [default function]{@link module:alfresco/forms/controls/BaseFormControl#processValidationRules} to 
       * call the JSON.parse function with the current value of the JSON editor. If the editor content is valid then
       * the function will return true and false otherwise
       * 
       * @instance
       * @returns {boolean} True if the editor content is valid and false otherwise.
       */
      processValidationRules: function alfresco_forms_controls_JsonEditor__processValidationRules() {
         var valid = true;
         try
         {
            if (typeof this.wrappedWidget.getText === "function")
            {
               JSON.parse(this.wrappedWidget.getText())
            }
         }
         catch (e)
         {
            valid = false;
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
      setupChangeEvents: function alfresco_forms_controls_JsonEditor__setupChangeEvents() {
         // No action required. The change events are handled in the set up of the JsonEditor
      },
      
      /**
       * Overrides the [inherited function]{@link module:alfresco/forms/controls/BaseFormControl#getValue} to
       * call the "getText" function on the editor. This returns the JSON text rather than a JavaScript object.
       * 
       * @instance
       * @returns {string} The JSON value entered into the editor
       */
      getValue: function alfresco_forms_controls_JsonEditor__getValue() {
         var value = "";
         if (this.wrappedWidget != null)
         {
            value = this.wrappedWidget.getText();
         }
         return value;
      }
   });
});