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
 * This mixin provides display filtering capabilities. It can be mixed into any widget so that
 * if the widget is configured with a [filterTopic]{@link modulealfresco/menus/AlfDisplayFilterMixin#filterTopic]
 * then it will used as a subscription topic and the [filter]{@link modulealfresco/menus/AlfDisplayFilterMixin#filter]
 * function will be called each time it is published on.
 * 
 * @module alfresco/menus/_AlfDisplayFilterMixin
 * @extends module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "dojo/dom-class"], 
        function(declare, AlfCore, lang, domClass) {
   
   return declare([AlfCore], {
      
      /**
       * @instance
       * @type {string} filterTopic The topic to subscribe to for filtering events
       * @default null
       */
      filterTopic: null,
      
      /**
       * Ensures that the supplied menu item label is translated.
       * @instance
       */
      postCreate: function alfresco_menus_AlfMenuItem__postCreate() {
         if (this.filterTopic != null)
         {
            this.alfSubscribe(this.filterTopic, lang.hitch(this, "filter"));
         }
         this.inherited(arguments);
      },
      
      /**
       * 
       * @instance
       * @param {object} payload The payload published on the filter topic 
       */
      filter: function alfresco_menus_AlfFilteringMenuItem__filter(payload) {
         this.inherited(arguments);
      },
      
      /**
       * Hides the menu item.
       * @instance
       */
      hide: function alfresco_menus_AlfFilteringMenuItem__hide() {
         domClass.add(this.domNode, "hidden");
      },
      
      /**
       * Displays the menu item.
       * @instance
       */
      show: function alfresco_menus_AlfFilteringMenuItem__show() {
         domClass.remove(this.domNode, "hidden");
      }
   });
});