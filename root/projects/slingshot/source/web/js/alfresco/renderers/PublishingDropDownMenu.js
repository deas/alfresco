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
            this.alfSubscribe(subscriptionTopic, lang.hitch(this, "onPublishChange"));
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

         var updatePayload = {
            shortName: lang.getObject("shortName", false, this.currentItem),
            visibility: payload.value
         };
         this.alfPublish("ALF_UPDATE_SITE_DETAILS", updatePayload, true);
      }
   });
});