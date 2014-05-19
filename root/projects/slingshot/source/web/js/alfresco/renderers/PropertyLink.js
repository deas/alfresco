/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * Extends the Property renderer ({@link module:alfresco/renderers/Property}) to provide a linked property.
 * 
 * @module alfresco/renderers/PropertyLink
 * @extends alfresco/renderers/Property
 * @author Richard Smith
 */
define(["dojo/_base/declare",
        "alfresco/renderers/Property",
        "dijit/_OnDijitClickMixin",
        "alfresco/core/ObjectProcessingMixin",
        "dojo/text!./templates/PropertyLink.html",
        "dojo/_base/event",
        "dojo/_base/lang"], 
        function(declare, Property, _OnDijitClickMixin, ObjectProcessingMixin, template, event, lang) {

   return declare([Property, _OnDijitClickMixin, ObjectProcessingMixin], {

      /**
       * Overriddes the default HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,

      /**
       * If this is set to true then the current item will be published when the link is clicked. If set to
       * false then the payload will be generated from the configured value.
       *
       * @instance
       * @type {boolean}
       * @default true
       */
      useCurrentItemAsPayload: true,

      /**
       * Handles the property being clicked. This stops the click event from propogating
       * further through the DOM (to prevent any wrapping anchor elements from triggering
       * browser navigation) and then publishes the configured topic and payload.
       *
       * @instance
       * @param {object} evt The details of the click event
       */
      onLinkClick: function alfresco_renderers_PropertyLink__onLinkClick(evt) {
         event.stop(evt);
         var publishTopic = this.getPublishTopic();
         if (publishTopic == null || publishTopic.trim() == "")
         {
            this.alfLog("warn", "No publishTopic provided for PropertyLink", this);
         }
         else
         {
            var publishGlobal = (this.publishGlobal != null) ? this.publishGlobal : false;
            var publishToParent = (this.publishToParent != null) ? this.publishToParent : false;
            this.alfPublish(publishTopic, this.getPublishPayload(), publishGlobal, publishToParent);
         }
      },

      /**
       * Gets the topic to be published on. This has been abstracted to a separate function
       * so that it can be easily overridden, an example of this is the 
       * [SearchResultPropertyLink]{@link module:alfresco/renderers/SearchResultPropertyLink}
       *
       * @instance
       * @returns {string} The configured publishTopic
       */ 
      getPublishTopic: function alfresco_renderers_PropertyLink__getPublishTopic() {
         return this.publishTopic;
      },

      /**
       * Gets the topic to be published on. This has been abstracted to a separate function
       * so that it can be easily overridden, an example of this is the 
       * [SearchResultPropertyLink]{@link module:alfresco/renderers/SearchResultPropertyLink}
       *
       * @instance
       * @returns {string} The currentItem being renderered.
       */ 
      getPublishPayload: function alfresco_renderers_PropertyLink__getPublishTopic() {
         if (this.useCurrentItemAsPayload == true)
         {
            return this.currentItem;
         }
         else
         {
            return this.generatePayload();
         }
      },

      /**
       * Generates a payload based on the supplied payload configuration.

       * @instance
       * @returns {object} The generated payload
       */
      generatePayload: function alfresco_renderers_PropertyLink__generatePayload() {
         if (this.publishPayload != null)
         {
            var clonedPayload = lang.clone(this.publishPayload);
            return this.processObject([this.processCurrentItemTokens, this.replaceColons], clonedPayload);
         }
         else
         {
            return {};
         }
      }
   });
});