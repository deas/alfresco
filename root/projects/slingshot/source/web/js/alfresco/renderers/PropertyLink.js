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
        "dojo/text!./templates/PropertyLink.html",
        "dojo/_base/event"], 
        function(declare, Property, _OnDijitClickMixin, template, event) {

   return declare([Property, _OnDijitClickMixin], {

      /**
       * Overriddes the default HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,

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
            this.alfPublish(publishTopic, this.getPublishPayload());
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
         return this.currentItem;
      }
   });
});