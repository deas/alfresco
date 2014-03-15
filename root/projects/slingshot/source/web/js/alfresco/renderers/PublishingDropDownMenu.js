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
 * This renders a drop-down select menu using a wrapped [DojoSelect]{@link module:alfresco/forms/controls/DojoSelect}
 * widget that when changed will publish information about the change in value for the current rendered item.
 * 
 * @module alfresco/renderers/PublishingDropDownMenu
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/PublishingDropDownMenu.html",
        "alfresco/core/Core",
        "alfresco/core/ObjectTypeUtils",
        "alfresco/forms/controls/DojoSelect",
        "dojo/_base/lang"], 
        function(declare, _WidgetBase, _TemplatedMixin, template, AlfCore, ObjectTypeUtils, DojoSelect, lang) {

   return declare([_WidgetBase, _TemplatedMixin, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/PublishingDropDownMenu.css"}]
       */
      cssRequirements: [{cssFile:"./css/PublishingDropDownMenu.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * This is the topic that will be published on when the drop-down menu value is changed.
       *
       * @instance
       * @type {string}
       * @default null
       */
      publishTopic: null,

      /**
       * This will be set to reference the [DojoSelect]{@link module:alfresco/forms/controls/DojoSelect} that is 
       * wrapped by this widget.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      _dropDownWidget: null,

      /**
       * This is the options config that will be passed onto the wrapped [DojoSelect]{@link module:alfresco/forms/controls/DojoSelect}
       * widget.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      optionsConfig: null,

      /**
       * 
       * @instance
       */
      postCreate: function alfresco_renderers_PublishingDropDownMenu__postCreate() {

         if (ObjectTypeUtils.isString(this.propertyToRender) && 
             ObjectTypeUtils.isObject(this.currentItem) && 
             lang.exists(this.propertyToRender, this.currentItem))
         {
            // Get the value of the property to render...
            var value = lang.getObject(this.propertyToRender, false, this.currentItem)

            // Set up the values needed to handle the pub/sub events coming out of the wrapped dropdown...
            var uuid = this.generateUuid();
            var fieldId = "DROPDOWN";
            var subscriptionTopic = uuid + "_valueChangeOf_" + fieldId;

            // Create the widget...
            this._dropDownWidget = new DojoSelect({
               pubSubScope: uuid,
               fieldId: fieldId,
               value: value,
               optionsConfig: this.optionsConfig
            }, this.dropDownNode);

            // Create the subscription AFTER the widget has been instantiated so that we don't 
            // uncessarily process the setup publications which are intended to be processed by 
            // other controls in the same scoped form...
            this.alfSubscribe(subscriptionTopic, lang.hitch(this, "onPublishChange"), true);
         }
         else
         {
            this.alfLog("warn", "Property for PublishingDropDown renderer does not exist:", this);
         } 
      },

      /**
       * 
       * @instance
       * @param {object} payload The information about the change in value.
       */
      onPublishChange: function alfresco_renderers_PublishingDropDownMenu__onPublishChange(payload) {
         this.alfLog("log", "Drop down property changed", payload);

         if (this.publishTopic != null)
         {
            // TODO: We need to set a more abstract payload...
            var updatePayload = this.generatePayload(payload);
            // var updatePayload = {
            //    shortName: lang.getObject("shortName", false, this.currentItem),
            //    visibility: payload.value
            // };
            this.alfPublish(this.publishTopic, updatePayload, true);
         }
         else
         {
            this.alfLog("warn", "A drop-down property was changed but there is no 'publishTopic' defined to publish on", this);
         }
      },

      /** 
       * <p>This function is used to process configurable payloads. It iterates over the first-level of attributes
       * of the defined payload and checks to see if the attribute is an object featuring both 'alfType' and 'alfProperty'
       * attributes. If the attribute does match this criteria then the payload will be processed to attempt to
       * retrieve the defined 'alfProperty' from a specific type. Currently two types are supported:
       * <ul><li>'item' which indicates the property is of the currentItem object</li>
       * <li>'payload' which indicates the property is of the published payload that triggered the publish request</li></ul><p>
       * 
       * @instance
       * @param {object} payload
       * @returns {object} The payload to be published
       */
      generatePayload: function alfresco_renderers_PublishingDropDownMenu__generatePayload(payload) {
         var publishPayload = null;
         if (this.publishPayload != null)
         {
            publishPayload = lang.clone(this.publishPayload);
            for (var key in publishPayload)
            {
               var value = publishPayload[key];
               if (ObjectTypeUtils.isObject(value) &&
                  value.alfType !== undefined &&
                  value.alfProperty !== undefined)
               {
                  var type = value.alfType;
                  var property = value.alfProperty;
                  if (type == "item")
                  {
                     value = lang.getObject(property, null, this.currentItem);
                  }
                  else if (type == "payload")
                  {
                     value = lang.getObject(property, null, payload);
                  }
                  else
                  {
                     this.alfLog("warn", "A payload was defined with 'alfType' and 'alfProperty' attributes but the 'alfType' attribute was neither 'item' nor 'payload' which are the only supported types", this);
                  }
                  publishPayload[key] = value;
               }
            }
         }
         else
         {
            this.alfLog("warn", "A drop-down property was changed but there is no 'publishPayload' defined to publish", this);
         }
         return publishPayload;
      }
   });
});