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
 * to dynamically re-draw a form based on payload published to a subscribed topic. The idea is that the 
 * displayed form can change (e.g. as the users picks a specific form type from a drop-down or radio buttons)
 * 
 * @module alfresco/forms/DynamicForm
 * @extends module:alfresco/forms/Form
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/forms/Form",
        "dojo/_base/lang",
        "dojo/dom-class",
        "dojo/on"], 
        function(declare, Form, lang, domClass, on) {
   
   return declare([Form], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/SingleEntryForm.css"}]
       */
      cssRequirements: [{cssFile:"./css/SingleEntryForm.css"}],

      /**
       * Override the default attribute to hide the cancel button
       * 
       * @instance
       * @type {boolean}
       * @default false
       */
      showCancelButton: false,

      /**
       * Overridden to set the "widgets" attribute to be a single text box.
       *
       * @instance
       */
      postMixInProperties: function alfresco_forms_SingleEntryForm__postMixInProperties() {
         this.widgets = [
            {
               name: "alfresco/forms/controls/DojoValidationTextBox",
               assignTo: "entryField",
               config: {
                  label: "",
                  name: this.entryFieldName,
                  requirementConfig: {
                     initialValue: true
                  },
                  iconClass: "alf-search-icon",
                  additionalCssClasses: "long"
               }
            }
         ]
      },

      /**
       * Extended to add an additional CSS class to the widget DOM
       * 
       * @instance
       */
      postCreate: function alfresco_forms_SingleEntryForm__postCreate() {
         domClass.add(this.domNode, "alfresco-forms-SingleEntryForm");
         this.inherited(arguments);
      },

      /**
       * Extended to connect keyup events (when the key is enter) to click the 
       * submit button
       *
       * @instance
       * @param {array} widgets The widgets instantiated
       */
      allWidgetsProcessed: function alfresco_forms_SingleEntryForm__allWidgetsProcessed(widgets) {
         this.inherited(arguments)

         on(this.entryField, "keyup", lang.hitch(this, function(evt) {
            if (evt.keyCode == 13)
            {
               this.okButton.onClick();
            }
         }));
      }
   });
});