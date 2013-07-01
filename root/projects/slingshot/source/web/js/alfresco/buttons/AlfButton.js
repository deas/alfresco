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
// This class is intended to be mixed into to all menu item instances to ensure that they all share common behaviour 
define(["dojo/_base/declare",
        "dijit/form/Button",
        "alfresco/core/Core",
        "dojo/dom-class"], 
        function(declare, Button, AlfCore, domClass) {
   
   return declare([Button, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/AlfButton.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @property i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/AlfButton.properties"}],
      
      /**
       * @property {string} publishTopic The topic to publish when the button is clicked
       * @default ""
       */
      publishTopic: "",
      
      /**
       * @property {object} publishPayload The payload to publish when the button is clicked
       */
      publishPayload: null,
      
      /**
       * 
       * @method postMixInProperties
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
       * @method postCreate
       */
      postCreate: function alfresco_buttons_AlfButton__postCreate() {
         this.inherited(arguments);
         domClass.add(this.domNode, "alfresco-buttons-AlfButton");
      },
      
      /**
       * @method onClick
       */
      onClick: function alfresco_buttons_AlfButton__onClick() {
         if (this.publishTopic != null && this.publishTopic != "")
         {
            this.alfPublish(this.publishTopic, this.publishPayload);
         }
         else
         {
            this.alfLog("error", "A widget was clicked but did not provide any information on how to handle the event", this);
         }
      }
      
   });
});