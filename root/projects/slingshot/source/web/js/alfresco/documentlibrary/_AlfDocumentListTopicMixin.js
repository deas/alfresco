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
 * This module simply provides a set of attributes that define topic names for publications
 * and subscriptions. This should be mixed into any widget that wishes to use those topics
 * to ensure consistency. It also allows the actual values to be managed from a single file. 
 * 
 * @module alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare"], 
        function(declare) {
   
   return declare(null, {

      /**
       * This topic is used to request that the Document List reloads data using its current parameters.
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_RELOAD_DATA"
       */
      reloadDataTopic: "ALF_DOCLIST_RELOAD_DATA",
      
      /**
       * This topic is used to publish changes to the current displayed parent location. It should be used to provide
       * all the metdata for rendering the current view of documents.
       * 
       * @instance
       * @type {string} 
       * @default "ALF_CURRENT_NODEREF_CHANGED"
       */
      metadataChangeTopic: "ALF_CURRENT_NODEREF_CHANGED",
      
      /**
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_FILTER_CHANGED"
       */
      filterChangeTopic: "ALF_DOCLIST_FILTER_CHANGED",
      
      /**
       * This differs from the "filterChangeTopic" because it describes the "filter" filter (as opposed to a "path" filter)
       * rather than just indicating that the current filter has been changed. This is required so that widgets can reflect
       * information about the currently selected filter - for example, a breadcrumb trail needs to show information about the
       * selected filter.
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_FILTER_SELECTION"
       */
      filterSelectionTopic: "ALF_DOCLIST_FILTER_SELECTION",
      
      /**
       * This topic is used to publish changes for the users access rights to the current location, e.g. if they 
       * no longer have creation rights.
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_USER_ACCESS_CHANGED"
       */
      userAccessChangeTopic: "ALF_DOCLIST_USER_ACCESS_CHANGED",
      
      /**
       * This topic is used to publish the details of documents loaded for the current location.
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_DOCUMENTS_LOADED"
       */
      documentsLoadedTopic: "ALF_DOCLIST_DOCUMENTS_LOADED",
      
      /**
       * This topic is used to publish changes of page within the current location.
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_PAGE_SELECTED"
       */
      pageSelectionTopic: "ALF_DOCLIST_PAGE_SELECTED",
      
      /**
       * This topic is used to publish changes of the number of documents to show for each page.
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_DOCS_PER_PAGE_SELECTION"
       */
      docsPerpageSelectionTopic: "ALF_DOCLIST_DOCS_PER_PAGE_SELECTION",
      
      /**
       * This is the topic on which the valid registered views will publish the menu items that can be used to
       * select them (the view). A default is provided but can be overridden.
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIB_PROVIDE_VIEW_SELECTION_MENU_ITEM"
       */
      selectionMenuItemTopic: "ALF_DOCLIST_PROVIDE_VIEW_SELECTION_MENU_ITEM",
      
      /**
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_SELECT_VIEW"
       */
      viewSelectionTopic: "ALF_DOCLIST_SELECT_VIEW",
      
      /**
       * The name of the group into which to place all view selection menu items.
       * 
       * @instance
       * @type {string}  
       * @default "DOCUMENT_LIBRARY_VIEW"
       */
      viewSelectionMenuItemGroup: "DOCUMENT_LIBRARY_VIEW",
     
      /**
       * The topic to publish on when a view is selected that provides additional controls
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLST_PROVIDE_ADDITIONAL_VIEW_CONTROLS"
       */
      additionalViewControlsTopic: "ALF_DOCLIST_PROVIDE_ADDITIONAL_VIEW_CONTROLS",
      
      /**
       * This differs from the "documentSelectedTopic" attribute as it is used to make general selection requests, e.g. "selectAll"
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_FILE_SELECTION"
       */
      documentSelectionTopic: "ALF_DOCLIST_FILE_SELECTION",
      
      /**
       * This differs from the "documentSelectionTopic" attribute as it should be used for individual documents
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_DOCUMENT_SELECTED"
       */
      documentSelectedTopic: "ALF_DOCLIST_DOCUMENT_SELECTED",
      
      /**
       * Used to indicate the list of currently selected documents has changed and provides the details of those items.
       * 
       * @instance
       * @type {string} 
       * @default "ALF_SELECTED_FILES_CHANGED"
       */
      selectedDocumentsChangeTopic: "ALF_SELECTED_FILES_CHANGED",
      
      /**
       * Use to indicate that an individual document has been deselected (e.g. that it should no longer be included in group actions).
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_DOCUMENT_DESELECTED"
       */
      documentDeselectedTopic: "ALF_DOCLIST_DOCUMENT_DESELECTED",
      
      /**
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_SORT"
       */
      sortRequestTopic: "ALF_DOCLIST_SORT",
      
      /**
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_SORT_FIELD_SELECTION"
       */
      sortFieldSelectionTopic: "ALF_DOCLIST_SORT_FIELD_SELECTION",
      
      /**
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_SHOW_FOLDERS"
       */
      showFoldersTopic: "ALF_DOCLIST_SHOW_FOLDERS",
      
      /**
       * Used to indicate whether or not to display the current path rendered within the document list.
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_SHOW_PATH"
       */
      showPathTopic: "ALF_DOCLIST_SHOW_PATH",
      
      /**
       * Indicates whether or not to display a sidebar to accompany the document list (this may contain additional controls
       * such as trees, filters, etc).
       * 
       * @instance
       * @type {string} 
       * @default "ALF_DOCLIST_SHOW_SIDEBAR"
       */
      showSidebarTopic: "ALF_DOCLIST_SHOW_SIDEBAR",
      
      /**
       * Used to request an action on an individual document (as opposed to a group of documents).
       * 
       * @instance
       * @type {string} 
       * @default "ALF_SINGLE_DOCUMENT_ACTION_REQUEST"
       */
      singleDocumentActionTopic: "ALF_SINGLE_DOCUMENT_ACTION_REQUEST",
      
      /**
       * Used to indicate that a document has been tagged.
       * 
       * @instance
       * @type {string}
       * @default "ALF_DOCUMENT_TAGGED"
       */
      documentTaggedTopic: "ALF_DOCUMENT_TAGGED",
         
      /**
       * @instance
       * @type {string}
       * @default "ALF_UNSYNC_CURRENT_LOCATION"
       */
      syncLocationTopic: "ALF_SYNC_CURRENT_LOCATION",
      
      /**
       * @instance
       * @type {string}
       * @default "ALF_UNSYNC_CURRENT_LOCATION"
       */
      unsyncLocationTopic: "ALF_UNSYNC_CURRENT_LOCATION"
      
   });
});