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

      getPublishTopic: function alfresco_renderers_PropertyLink__getPublishTopic() {
         return this.publishTopic;
      },

      getPublishPayload: function alfresco_renderers_PropertyLink__getPublishTopic() {
         return this.currentItem;
      }
   });
});