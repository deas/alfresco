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
        "dojo/text!./templates/MultipleEntryElement.html",
        "alfresco/core/Core",
        "dijit/form/TextBox",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dijit/registry"], 
        function(declare, _Widget, _Templated, template, AlfCore, TextBox, domConstruct, domClass, registry) {
   
   return declare([_Widget, _Templated, AlfCore], {
      cssRequirements: [{cssFile:"./css/MultipleEntryElement.css"}],
      i18nRequirements: [{i18nFile: "./i18n/MultipleEntryElement.properties"}],
      templateString: template,
      
      /**
       * The key should be the identifier of this element.
       * TODO: Could we just rely on the widget provided id attribute?
       */
      _alfMultipleElementId: null,
      
      /**
       * The element configuration should be passed as a construction argument. It should provide a key
       * and a value to use for rendering the element.
       */
      elementConfig: null,
      
      /**
       * 
       */
      constructor: function(args) {
         declare.safeMixin(this, args);
         this.determineKeyAndValue();
      },
      
      /**
       * This is the default function for determining the unique key to identify the the element amongst its
       * peers. This function will most likely need to be overridden by extending classes that handle more
       * complex data types. 
       */
      determineKeyAndValue: function() {
         this.alfLog("log", "DetermineKeyAndValue", this.elementConfig);
         if (this.elementConfig != null && 
             this.elementConfig instanceof Object)
         {
            // We're going to make sure that each element has a unique id. If the supplied configuration
            // does not include an id then we're going to create one now...
            if (typeof this.elementConfig._alfMultipleElementId == "undefined" ||
                this.elementConfig._alfMultipleElementId == null ||
                this.elementConfig._alfMultipleElementId == "") 
            {
               this.elementConfig._alfMultipleElementId = this.generateUuid();
            }
            this.alfMultipleElementId = this.elementConfig._alfMultipleElementId;
            this.value = this.elementConfig;
         }
         else
         {
            this._alfMultipleElementId = this.generateUuid();
            this.value = {};
            this.value._alfMultipleElementId = this._alfMultipleElementId;
         }
      },

      /**
       * Calls the createReadDisplay and createEditDisplay functions to setup the edit and read modes for the element.
       * Those functions should be overridden directly to alter the appearance of the element.
       */
      postCreate: function() {
         
         // Some data will be provided and it will be necessary to construct the widgets to render that data in both edit and display mode.
         this.createReadDisplay();
      },
      
      /**
       * The default read display simply shows the value of the element.
       */
      createReadDisplay: function() {
         // Set the innerHTML of the read display to be the value...
         if (typeof this.value.value == "undefined")
         {
            this.value.value = "";
         }
         this.readDisplay.innerHTML = this.value.value;
      },
      
      createEditDisplay: function() {
         domConstruct.empty(this.editDisplay);
         var _this = this;
         var textBox = new TextBox({value: this.value.value});
         textBox.placeAt(this.editDisplay);
         textBox.watch("value", function(name, oldValue, value) {
            _this.value.value = value;
            _this.createReadDisplay();
         }, this);
      },

      /**
       * Switches the visibility of the edit and read displays.
       */
      editMode: function(isEditMode) {
         
         if (isEditMode)
         {
            this.createEditDisplay();
            domClass.remove(this.editDisplay, "hide");
            domClass.add(this.readDisplay, "hide");
         }
         else
         {
            // TODO: We should consider preventing exiting validation of edit mode (there are issues with this though,
            //       it's possible that the user may want to accept an entry that is invalid if it can be made valid
            //       via another entry.
            this.createReadDisplay();
            domClass.add(this.editDisplay, "hide");
            domClass.remove(this.readDisplay, "hide");
         }
      },
      
      getValue: function() {
         return this.value;
      },
      
      /**
       * 
       */
      validate: function() {
         // By default there is no validation performed. 
         return true;
      }
   });
});