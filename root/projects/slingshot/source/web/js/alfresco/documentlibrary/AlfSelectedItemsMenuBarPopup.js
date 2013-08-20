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
 * Extends the [AlfMenuBarPopup]{@link module:alfresco/menus/AlfMenuBarPopup} widget to listen to publications
 * that indicate that documents have been selected and disables the menu bar if nothing is selected.
 * 
 * @module alfresco/documentlibrary/AlfSelectedItemsMenuBarPopup
 * @extends module:alfresco/menus/AlfMenuBarPopup
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/menus/AlfMenuBarPopup",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "dojo/_base/lang",
        "dojo/dom-class"], 
        function(declare, AlfMenuBarPopup, _AlfDocumentListTopicMixin, lang, domClass) {
   
   return declare([AlfMenuBarPopup, _AlfDocumentListTopicMixin], {
      
      /**
       * Overrides the default to initialise as disabled.
       * 
       * @instance
       * @type {boolean}
       * @default true
       */
      disabled: true,
      
      /**
       * Extends the [superclass function]{@link module:alfresco/menus/AlfMenuBarPopup#postCreate} to subscribe to
       * the [selectedDocumentsChangeTopic]{@link module:alfresco/documentlibrary/_AlfDocumentListTopicMixin#selectedDocumentsChangeTopic}
       * topic which is handled by [onFilesSelected]{@link module:alfresco/documentlibrary/AlfSelectedItemsMenuBarPopup#onFilesSelected}.
       * @instance
       */
      postCreate: function alfresco_documentlibrary_AlfSelectedItemsMenuBarPopup__postCreate() {
         this.alfSubscribe(this.selectedDocumentsChangeTopic, lang.hitch(this, "onFilesSelected"));
         this.inherited(arguments);
      },
      
      /**
       * Called when [selectedDocumentsChangeTopic]{@link module:alfresco/documentlibrary/_AlfDocumentListTopicMixin#selectedDocumentsChangeTopic} is
       * published on and disables the popup menu if no files have been selected. 
       * 
       * @instance
       * @param {object} payload The details of the selected files.
       */
      onFilesSelected: function alfresco_documentlibrary_AlfSelectedItemsMenuBarPopup__onFilesSelected(payload) {
         this.set('disabled', (payload && payload.selectedFiles && payload.selectedFiles.length == 0));
      }
   });
});