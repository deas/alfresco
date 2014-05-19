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
 * <p>This module can be mixed into other modules to generate [menu items]{@link module:alfresco/menus/AlfMenuItem}
 * representing Alfresco document or folder actions generated for a specific node or an entirely custom action
 * list. It exists as mixin to support multipe ways of rendering actions (e.g. either in a menu bar or in a
 * context menu)</p>.
 * 
 * @module alfresco/renderers/_ActionsMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @mixes module:alfresco/renderers/_PublishPayloadMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "alfresco/renderers/_PublishPayloadMixin",
        "alfresco/menus/AlfMenuItem",
        "dojo/_base/array",
        "dojo/_base/lang",
        "service/constants/Default",
        "alfresco/core/ArrayUtils"],
        function(declare, AlfCore, _AlfDocumentListTopicMixin, _PublishPayloadMixin, AlfMenuItem, array, lang, AlfConstants, AlfArray) {

   return declare([AlfCore, _AlfDocumentListTopicMixin, _PublishPayloadMixin], {

      /**
       * Indicates whether or not actions should be filtered according to the 
       * [allowedActions array]{@link module:alfresco/renderers/Actions#allowedActions}.
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      filterActions: true,

      /**
       *  Array containing a list of allowed actions
       *  This is used to filter out actions that the actions API returns, but haven't yet been implemented.
       *  TODO: Remove this once all actions have been implemented by the actions service.
       *  Currently - all actions of type link and pagelink should work.
       */
      allowedActions: [
         "document-download",
         "document-view-content",
         "document-view-details",
         "folder-view-details",
         "document-edit-metadata",
         "document-inline-edit",
         "document-manage-granular-permissions",
         "document-manage-repo-permissions",
         "document-view-original",
         "document-view-working-copy",
         "folder-manage-rules",
         "view-in-explorer",
         "document-view-googledoc",
         "document-view-googlemaps",
         "document-view-in-source-repository",
         "document-view-in-cloud",
         "document-delete", // TODO: Works, but highlights eventual consistency issues.
         "document-edit-offline" // TODO: Works, but Working copy handling isn't quite correct.

         // TODO: Fix Document Picker scoping issues.
         //  "document-copy-to",
         //  "document-move-to",

         // TODO: Dialog Service not read for property edits.
         // "document-edit-properties",

         // TODO: Not implemented yet.
         // "document-upload-new-version",
         // "document-assign-workflow",
         // "document-publish"
      ],

      /**
       * Add the actions provided by the current item.
       *
       * @instance
       */
      addActions: function alfresco_renderers__ActionsMixin__postCreate() {
         // Iterate over the actions to create a menu item for each of them...
         if (this.customActions != null && this.customActions.length > 0)
         {
            array.forEach(this.customActions, lang.hitch(this, "addAction"));
         }
         else if (this.currentItem.actions && this.currentItem.actions.length > 0)
         {
            array.forEach(this.currentItem.actions, lang.hitch(this, "addAction"));
         }
      },
      
      /**
       * 
       * @instance
       * @param {object} action The configuration for the action to add
       * @param (integer} index The index of the action
       */
      addAction: function alfresco_renderers__ActionsMixin__addAction(action, index) {
         if (this.filterActions === false || AlfArray.arrayContains(this.allowedActions, action.id))
         {
            this.alfLog("log", "Adding action", action);

            var payload = (action.publishPayload != null) ? action.publishPayload : {document: this.currentItem, action: action};
            var menuItem = new AlfMenuItem({
               label: action.label,
               iconImage: AlfConstants.URL_RESCONTEXT + "components/documentlibrary/actions/" + action.icon + "-16.png",
               type: action.type,
               pubSubScope: this.pubSubScope,
               publishTopic: (action.publishTopic != null) ? action.publishTopic : this.singleDocumentActionTopic,
               publishPayload: this.generatePayload(payload, this.currentItem, null, action.publishPayloadType, action.publishPayloadItemMixin, action.publishPayloadModifiers)
            });
            this.actionsGroup.addChild(menuItem);
         }
         else
         {
            this.alfLog("log", "Skipping action as it's missing from whitelist: " + action);
         }
      }
   });
});