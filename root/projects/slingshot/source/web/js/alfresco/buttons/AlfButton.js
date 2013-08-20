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
 * This extends the default Dojo button to provide Alfresco specific styling. It also overrides
 * the [onClick]{@link module:alfresco/buttons/AlfButton#onClick} function to publish the 
 * [publishPayload]{@link module:alfresco/buttons/AlfButton#publishPayload} on the 
 * [publishTopic]{@link module:alfresco/buttons/AlfButton#publishTopic}
 * 
 * @module alfresco/buttons/AlfButton
 * @extends module:dijit/form/Button
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/form/Button",
        "alfresco/core/Core",
        "dojo/dom-class"], 
        function(declare, Button, AlfCore, domClass) {
   
   return declare([Button, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/AlfButton.css"}]
       */
      cssRequirements: [{cssFile:"./css/AlfButton.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance 
       * @type {object[]}
       * @default [{i18nFile: "./i18n/AlfButton.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/AlfButton.properties"}],
      
      /**
       * The topic to publish when the button is clicked
       *
       * @instance 
       * @type {string}
       * @default ""
       */
      publishTopic: "",
      
      /**
       * The payload to publish when the button is clicked
       * 
       * @instance
       * @type {object}
       * @default null
       */
      publishPayload: null,
      
      /**
       * Extends the default implementation to check that the [publishPayload]{@link module:alfresco/buttons/AlfButton#publishPayload} attribute has been set
       * to something other null and if it hasn't initialises it to a new (empty) object.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_buttons_AlfButton__postMixInProperties() {
         this.inherited(arguments);
         if (this.publishPayload == null)
         {
            this.publishPayload == {};
         }
      },
      
      /**
       * Extends the default Dojo button implementation to add a widget DOM node CSS class to ensure that the 
       * CSS selectors are matched.
       * 
       * @instance
       */
      postCreate: function alfresco_buttons_AlfButton__postCreate() {
         this.inherited(arguments);
         domClass.add(this.domNode, "alfresco-buttons-AlfButton");
      },
      
      /**
       * Handles click events to publish the [publishPayload]{@link module:alfresco/buttons/AlfButton#publishPayload} 
       * on the [publishTopic]{@link module:alfresco/buttons/AlfButton#publishTopic}
       * 
       * @instance 
       * @param {object} evt The click event
       */
      onClick: function alfresco_buttons_AlfButton__onClick(evt) {
         if (this.publishTopic != null && this.publishTopic != "")
         {
            this.alfPublish(this.publishTopic, this.publishPayload);
         }
         else
         {
            this.alfLog("warn", "A widget was clicked but did not provide any information on how to handle the event", this);
         }
      }
   });
});