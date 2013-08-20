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
 * @module alfresco/renderers/Actions
 * @extends module:alfresco/menus/AlfMenuBar
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/menus/AlfMenuBar",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "alfresco/menus/AlfMenuBarPopup",
        "alfresco/menus/AlfMenuGroup",
        "alfresco/menus/AlfMenuItem",
        "dojo/_base/array",
        "dojo/_base/lang"], 
        function(declare, AlfMenuBar, _AlfDocumentListTopicMixin, AlfMenuBarPopup, AlfMenuGroup, AlfMenuItem, array, lang) {

   return declare([AlfMenuBar, _AlfDocumentListTopicMixin], {
      
      /**
       * Overrides the default to create a popup containing a group containing all the actions
       * for the current item.
       * 
       * @instance
       */
      postCreate: function alfresco_renderers_Actions__postCreate() {
         this.inherited(arguments);
         
         // Create a group to hold all the actions...
         this.actionsGroup = new AlfMenuGroup({
            
         });
         // Iterate over the actions to create a menu item for each of them...
         if (this.currentItem.actions && this.currentItem.actions.length > 0)
         {
            array.forEach(this.currentItem.actions, lang.hitch(this, "addAction"));
         }

         // Create a menu popup to hold the group...
         this.actionsMenu = new AlfMenuBarPopup({
            label: "Actions"
         });
         this.actionsMenu.popup.addChild(this.actionsGroup);
         this._menuBar.addChild(this.actionsMenu);
         this._menuBar.placeAt(this.containerNode);
      },
      
      /**
       * 
       * @instance
       * @param {object} action The configuration for the action to add
       * @param (integer} index The index of the action
       */
      addAction: function alfresco_renderers_Actions__addAction(action, index) {
         this.alfLog("log", "Adding action", action);
         var menuItem = new AlfMenuItem({
            label: action.label,
            iconImage: Alfresco.constants.URL_RESCONTEXT + "components/documentlibrary/actions/" + action.icon + "-16.png",
            type: action.type,
            publishTopic: this.singleDocumentActionTopic,
            publishPayload: {
               document: this.currentItem,
               action: action
            }
         });
         this.actionsGroup.addChild(menuItem);
      }
   });
});