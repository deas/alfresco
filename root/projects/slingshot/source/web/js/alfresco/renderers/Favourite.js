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
 * @module alfresco/renderers/Favourite
 * @extends module:alfresco/renderers/Toggle
 * @mixes module:alfresco/services/_PreferenceServiceTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/renderers/Toggle",
        "alfresco/services/_PreferenceServiceTopicMixin"], 
        function(declare, Toggle, _PreferenceServiceTopicMixin) {

   return declare([Toggle, _PreferenceServiceTopicMixin], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/Favourite.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/Favourite.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/Favourite.css"}]
       */
      cssRequirements: [{cssFile:"./css/Favourite.css"}],
      
      /**
       * The label to show when the toggle is on
       * @instance
       * @type {string} 
       * @default ""
       */
      onLabel: "",
      
      /**
       * The label to show when the toggle is on
       * @instance
       * @type {string} 
       * @default "favourite.add.label"
       */
      offLabel: "favourite.add.label",
      
      /**
       * The tooltip to show when the toggle is on
       * @instance
       * @type {string}
       * @default "favourite.remove.tooltip"
       */
      onTooltip: "favourite.remove.tooltip",
      
      /**
       * Override this to add a specific image for the "on" state.
       *
       * @instance
       * @type {string}
       * @default "favourite.add.tooltip"
       */
      offTooltip: "favourite.add.tooltip",
      
      /**
       * The CSS class to apply for the on display
       * @instance
       * @type {string}
       * @default "favourite"
       */
      toggleClass: "favourite",
      
      /**
       * Extends to add the count of all likes.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_renderers_Like__postMixInProperties() {
         this.toggleOnTopic = (this.toggleOnTopic != null) ? this.toggleOnTopic : this.addFavouriteDocumentTopic;
         this.toggleOnSuccessTopic = (this.toggleOnSuccessTopic != null) ? this.toggleOnSuccessTopic : this.addFavouriteDocumentSuccessTopic;
         this.toggleOnFailureTopic = (this.toggleOnFailureTopic != null) ? this.toggleOnFailureTopic : this.addFavouriteDocumentFailureTopic;
         this.toggleOffTopic = (this.toggleOffTopic != null) ? this.toggleOffTopic : this.removeFavouriteDocumentTopic;
         this.toggleOffSuccessTopic = (this.toggleOffSuccessTopic != null) ? this.toggleOffSuccessTopic : this.removeFavouriteDocumentSuccessTopic;
         this.toggleOffFailureTopic = (this.toggleOffFailureTopic != null) ? this.toggleOffFailureTopic : this.removeFavouriteDocumentFailureTopic;
         
         this.inherited(arguments);
      },
      
      /**
       * Overridden to get the favourited state of the current item.
       * 
       * @instance
       * @returns {boolean} Indicating the initial state of the toggle.
       */
      getInitialState: function alfresco_renderers_Favourite__getInitialState() {
         return this.currentItem.isFavourite;
      },
      
      /**
       * Overrides the default implementation to check that the event is related
       * 
       * @instance
       * @returns false
       */
      relatesToMe: function alfresco_renderers_Favourite__relatesToMe(payload) {
         var relatesToMe = false;
         try
         {
            relatesToMe = (payload.requestConfig.node.jsNode.nodeRef.uri == this.currentItem.jsNode.nodeRef.uri);
         }
         catch (e) 
         {
            this.alfLog("error", "Unexpected data structures", e, payload, this);
         }
         return relatesToMe;
      }
      
   });
});