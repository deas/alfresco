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
 * This handles publications requesting to perform actions on documents. It instantiates a "legacy" YUI2 based 
 * Alfresco.DocListToolbar widget which is delegated any actions that aren't explicitly handled by the service.
 * Over time this service should handle more and more of the core document actions as the old YUI2 code is phased
 * out. However, currently it just aliases those actions as well as any custom actions that are registered by
 * extensions.
 * 
 * Custom actions prior to 4.2 were provided via the YAHOO.Bubbling.fire("registerAction" ...) event
 * where the target function accepted a single argument of the file to work with.
 * 
 * "action.js" handles the registering of these functions.
 * "toolbar.js" augments its prototype with that of "action.js" to get all of the default action handlers
 * and in turn will be able to register additional handlers.
 *
 * Custom actions only supported single files in versions prior to 4.2
 * 
 * @module alfresco/services/ActionService
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreXhr
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @mixes module:alfresco/services/_NavigationServiceTopicMixin
 * @mixes module:alfresco/core/UrlUtils
 * @mixes module:alfresco/core/NotificationUtils
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "alfresco/services/_NavigationServiceTopicMixin",
        "alfresco/core/UrlUtils",
        "alfresco/core/NotificationUtils",
        "dojo/_base/lang",
        "alfresco/wrapped/DocumentListToolbar",
        "dojo/_base/array"],
        function(declare, AlfCore, AlfCoreXhr, _AlfDocumentListTopicMixin, _NavigationServiceTopicMixin, UrlUtils, NotificationUtils, lang, DocumentListToolbar, array) {
   
   return declare([AlfCore, AlfCoreXhr, _AlfDocumentListTopicMixin, _NavigationServiceTopicMixin, UrlUtils, NotificationUtils], {
      
      /**
       * Re-use the old Alfresco.DocListToolbar scope. This is necessary to ensure that legacy popups (such as the 
       * edit properties dialog) show up with the correct messages.
       * 
       * @instance
       * @type {string}
       * @default "Alfresco.DocListToolbar"
       */
      i18nScope: "Alfresco.DocListToolbar",
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/ActionService.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/ActionService.properties"}],
      
      /**
       * These files need to be included as they may be required as a result of processing actions handled
       * by the "legacy" action handling toolbar.
       * 
       * @instance
       */
      cssRequirements: [{cssFile:"/components/documentlibrary/toolbar.css"},
                        {cssFile:"/modules/social-publish.css"},
                        {cssFile:"/modules/cloud/cloud-auth-form.css"},
                        {cssFile:"/modules/cloud/cloud-folder-picker.css"},
                        {cssFile:"/modules/cloud/cloud-sync-status.css"},
                        {cssFile:"/modules/documentlibrary/aspects.css"},
                        {cssFile:"/modules/documentlibrary/copy-to.css"},
                        {cssFile:"/modules/documentlibrary/global-folder.css"},
                        {cssFile:"/modules/documentlibrary/permissions.css"},
                        {cssFile:"/modules/documentlibrary/site-folder.css"},
                        {cssFile:"/components/object-finder/object-finder.css"}],
                        
      /**
       * These files need to be included as they may be required as a result of processing actions handled
       * by the "legacy" action handling toolbar.
       * 
       * @instance
       */
      nonAmdDependencies: ["/modules/social-publish.js",
                           "/modules/documentlibrary/global-folder.js",
                           "/modules/documentlibrary/doclib-actions.js",
                           "/modules/documentlibrary/copy-move-to.js",
                           "/modules/documentlibrary/permissions.js",
                           "/components/download/archive-and-download.js",
                           "/modules/documentlibrary/cloud-folder.js",
                           "/modules/cloud-auth.js",
                           "/components/documentlibrary/actions.js",
                           "/components/documentlibrary/toolbar.js",
                           "/components/form/form.js",
                           "/components/form/date.js",
                           "/components/form/date-picker.js",
                           "/components/form/period.js",
                           "/components/object-finder/object-finder.js",
                           "/yui/calendar/calendar.js",
                           "/modules/editors/tiny_mce/tiny_mce.js",
                           "/modules/editors/tiny_mce.js",
                           "/components/form/rich-text.js",
                           "/components/form/content.js",
                           "/components/form/workflow/transitions.js",
                           "/components/form/workflow/activiti-transitions.js",
                           "/components/form/jmx/operations.js",
                           "/modules/documentlibrary/doclib-actions.js",
                           "/modules/simple-dialog.js"],

      /**
       * This should be set when the current context is a site.
       * 
       * @instance
       * @type {string}
       * @default null
       */
      siteId: null,
      
      /**
       * This should be set when the current context is a site, typically this will be set to "documentlibrary"
       * 
       * @instance
       * @type {string}
       * @default null
       */
      containerId: null,
      
      /**
       * This should be set if "siteId" is not set.
       * 
       * @instance
       * @type {string}
       * @default null
       */
      rootNode: null,
      
      /**
       * This will be set to the old YUI2 implemented toolbar in order to give the ActionService access to all
       * of the default and custom actions that might be available for use. 
       *  
       * @instance
       * @type {object}
       * @default null
       */
      _legacyToolbar: null,
      
      /**
       * 
       * @instance
       */
      getLegacyToolbar: function alfresco_services_ActionService__getLegacyToolbar() {
         return this.toolbar;
      },
      
      /**
       * Sets up the subscriptions for the NavigationService
       * 
       * @instance
       * @param {array} args Constructor arguments
       */
      constructor: function alfresco_services_ActionService__constructor(args) {
         lang.mixin(this, args);
         
         // Create a new Alfresco.DocListToolbar. This will be used to handle "legacy" actions. This is necessary because it
         // for prior to version 4.2 Enterprise the toolbar would be responsible for handling action requests and as such
         // it contains the basic action handling code as well as a mechanism for registering additional action handlers.
         this.toolbar = new Alfresco.DocListToolbar(this.id, false).setOptions({
            siteId: this.siteId,
            rootNode: this.rootNode
         });
         this.toolbar.onReady();
         
         this.loadCustomActionHandlers();
         this.subscribeToLegacyEvents();
         
         // Normal processing...
         this.currentlySelectedDocuments = {};
         this.alfSubscribe(this.documentsLoadedTopic, lang.hitch(this, "onDocumentsLoaded"));
         this.alfSubscribe(this.metadataChangeTopic, lang.hitch(this, "handleCurrentNodeChange"));
         this.alfSubscribe(this.documentSelectedTopic, lang.hitch(this, "onDocumentSelected"));
         this.alfSubscribe(this.documentDeselectedTopic, lang.hitch(this, "onDocumentDeselected"));
         this.alfSubscribe(this.singleDocumentActionTopic, lang.hitch(this, "handleSingleDocAction"));
         this.alfSubscribe(this.syncLocationTopic, lang.hitch(this, "onSyncLocation"));
         this.alfSubscribe(this.unsyncLocationTopic, lang.hitch(this, "onUnsyncLocation"));
         
         this.alfSubscribe("ALF_MULTIPLE_DOCUMENT_ACTION_REQUEST", lang.hitch(this, "handleMultiDocLegacyAction"));
         this.alfSubscribe("ALF_CREATE_CONTENT", lang.hitch(this, "processActionObject"));
         
         // Non-legacy action handlers...
         this.alfSubscribe("ALF_MOVE_DOCUMENTS", lang.hitch(this, "onMoveDocuments"));
      },

      /**
       * Set up handlers to bridge YUI and Dojo events (this is required to ensure that the document list is refreshed
       * when actions request it).
       *
       * @instance
       */
      subscribeToLegacyEvents: function alfresco_services_ActionService__subscribeToLegacyEvents() {
         YAHOO.Bubbling.on("fileCopied", this.onFileAction, this);
         YAHOO.Bubbling.on("fileDeleted", this.onFileAction, this);
         YAHOO.Bubbling.on("fileMoved", this.onFileAction, this);
         YAHOO.Bubbling.on("filePermissionsUpdated", this.onFileAction, this);
         YAHOO.Bubbling.on("folderCopied", this.onFileAction, this);
         YAHOO.Bubbling.on("folderDeleted", this.onFileAction, this);
         YAHOO.Bubbling.on("folderMoved", this.onFileAction, this);
         YAHOO.Bubbling.on("folderPermissionsUpdated", this.onFileAction, this);
         // Multi-file actions
         YAHOO.Bubbling.on("filesCopied", this.requestRefresh, this);
         YAHOO.Bubbling.on("filesDeleted", this.requestRefresh, this);
         YAHOO.Bubbling.on("filesMoved", this.requestRefresh, this);
         YAHOO.Bubbling.on("filesPermissionsUpdated", this.requestRefresh, this);
      },
      
      /**
       * Generic file action event handler
       *
       * @instance
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFileAction: function DL_onFileAction(layer, args)
      {
         var obj = args[1];
         if (obj)
         {
            if (!obj.multiple)
            {
               this.requestRefresh();
            }
         }
      },
      
      /**
       * @instance
       */
      requestRefresh: function() {
         this.alfPublish(this.reloadDataTopic, {});
      },
      
      /**
       * (Prior to version 4.2 Enterprise) in order to provide additional document actions that are handled by 
       * JavaScript functions it is necessary to ensure that the JavaScript files that define those functions are included 
       * as resources in the page. These resources need to be loaded *after* the non-AMD dependencies "toolbar.js" and
       * "actions.js" as these setup the YAHOO action registration event handling (if they are included in the page
       * before the registration event handling is setup then the registration events will fire but not be captured).
       * 
       * An generated aggregrated resource containing all these additional JavaScript dependencies should be provided
       * as a construction argument to this service so that they can then be asynchronously loaded and have their actions
       * registered correctly.
       * 
       * This approach to action registration should be phased out with custom action handling being provided through
       * additional services that subscribe to publications on custom topics used by the action
       * 
       * @instance
       */
      loadCustomActionHandlers: function alfresco_services_ActionService__loadCustomActionHandlers() {
         if (this.customAggregatedJsResource)
         {
            require([Alfresco.constants.URL_RESCONTEXT + this.customAggregatedJsResource], lang.hitch(this, "customActionHandlersLoaded"));
         }
      },
      
      /**
       * This is a callback handler that is called when [loadCustomActionHandlers]{@link module:alfresco/service/ActionService#loadCustomActionHandlers}
       * has loaded the additional action handlers.
       * 
       * @instance
       */
      customActionHandlersLoaded: function() {
         this.alfLog("log", "Custom action handlers loaded");
      },
      
      /**
       * The primary purpose of this function is to reset the 'currentlySelectedDocuments' attribute.
       * 
       * @instance
       * @param {object} payload The details of the documents loaded
       */
      onDocumentsLoaded: function alfresco_services_ActionService__onDocumentsLoaded(payload) {
         this.alfLog("log", "New Documents Loaded", payload);
         this.currentlySelectedDocuments = {};
         this.onSelectedFilesChanged();
      },
      
      /**
       * The current Node that content will be worked relative to.
       * @instance
       * @type {object}
       * @default null
       */
      _currentNode: null,
      
      /**
       *
       * @instance
       */
      handleCurrentNodeChange: function alfresco_services_ActionService__handleCurrentNodeRefChange(payload) {
         if (payload && payload.node)
         {
            this.alfLog("log", "Updating current nodeRef to: ", payload.node);
            this._currentNode = payload.node;
            this.getLegacyToolbar().doclistMetadata.parent = payload.node;
         }
         else
         {
            this.alfLog("error", "A request was made to update the current NodeRef, but no 'node' property was provided in the payload: ", payload);
         }
      },
      
      /**
       * This function handles requests to perform an action on a single document. The action will be handled by the legacy action handler.
       * 
       * @instance
       * @param payload
       */
      handleSingleDocAction: function alfresco_services_ActionService__handleSingleDocAction(payload) {
         this.alfLog("log", "Single document action request:", payload);
         if (payload && 
             payload.document != null && 
             payload.action != null)
         {
            if (typeof payload.action === "string")
            {
               // If the action supplied is a string then it is assumed to be the name of a function to call.
               if (typeof this.toolbar[payload.action] === "function")
               {
                  // Check the legacy actions first...
                  this.toolbar[payload.action].call(this.toolbar, payload.document);
               }
               else if (typeof this[payload.action] === "function")
               {
                  // Then check the actions provided by this service...
                  this[payload.action](payload.document);
               }
            }
            else if (typeof payload.action === "object")
            {
               // If the action is an object then it is assumed to contain information for processing
               // via the legacy toolbar.
               this.processActionObject(payload.action, payload.document);
            }
         }
      },
      
      /**
       * 
       * @instance
       * @param {object} payload The data passed in the request to perform the action.
       */
      handleMultiDocLegacyAction: function alfresco_services_ActionService__handleLegacyAction(payload) {
         this.alfLog("log", "Multiple document action request:", payload);
         if (payload && payload.action)
         {
            // Check that we have a handler for the requested function. The action should be available in the
            // DocumentListToolbar that was instantiated (this should also include any 3rd party custom actions
            // that have been provided.
            // TODO: Make sure custom action JavaScript resources are loaded via WebScript .get.html.ftl file
            if (typeof this.getLegacyToolbar()[payload.action] === "function")
            {
               this.alfLog("log", "Found legacy action handler");
               if (this.currentlySelectedDocuments != null)
               {
                  this.getLegacyToolbar()[payload.action].call(this.getLegacyToolbar(), this.getSelectedDocumentArray());
               }
               else
               {
                  this.alfLog("log", "No documents selected to perform an action on");
               }
            }
            else
            {
               this.alfLog("error", "A request was made to perform an action on multiple documents, but the JavaScript handler could not be found: ", payload.action);
            }
         }
         else
         {
            this.alfLog("error", "A request was made to perform an action on multiple documents, but no action was provided: ", payload);
         }
      },
      
      /**
       * Calls a "legacy" action handler that is defined within the DocumentListToolbar widget.
       * 
       * @instance
       * @param {function} func The handler function to call
       * @param {object} doc The document to pass as the argument to the handler
       * @param {integer} i The index in the array of selected documents that the document represents
       */
      _callLegacyActionHandler: function alfresco_services_ActionService(func, doc, i) {
         this.alfLog("log", "Calling handler", func, doc, i);
         func.call(this.getLegacyToolbar(), doc);
      },
      
      /**
       * This is used to keep track of the documents that are currently selected. It is initialised to an empty
       * array in the constructor, the onDocumentSelected function adds elements and the onDocumentDeselected
       * function removes them.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      currentlySelectedDocuments: null,

      /**
       * This is used to keep a reference to a timeout that is started on the publication of a selected document
       * topic. It is important that multiple selection events can be captured so that only one publication of
       * selected items occurs. Otherwise the responsiveness ot the UI is degraded as each individual selection
       * event is processed (e.g. by [AlfDocumentActionMenuItems]{@link module:alfresco/documentlibrary/AlfDocumentActionMenuItem})
       * 
       * @instance
       * @type {timeout}
       * @default null
       */
      selectionTimeout: null,
      
      /**
       * Updates the aray of documents that are currently selected.
       * @instance
       * @param {object} The details of the document selected
       */
      onDocumentSelected: function alfresco_services_ActionService__onDocumentSelected(payload) {
         if (payload && payload.value && payload.value.nodeRef != null)
         {
            this.currentlySelectedDocuments[payload.value.nodeRef] = payload.value;
            if (this.selectionTimeout != null)
            {
               clearTimeout(this.selectionTimeout);
            }
            this.selectionTimeout = setTimeout(lang.hitch(this, "deferredSelectionHandler"), 50);
         }
      },
      
      /**
       * This is called from [onDocumentSelected]{@link module:alfresco/services/ActionService#onDocumentSelected}
       * when the [selectionTimeout]{@link module:alfresco/services/ActionService#selectionTimeout} times out. It
       * rests the [selectionTimeout]{@link module:alfresco/services/ActionService#selectionTimeout} to null and
       * calls [onSelectedFilesChanged]{@link module:alfresco/services/ActionService#deselectionTimeout}
       * 
       * @instance
       */
      deferredSelectionHandler: function alfresco_services_ActionService__deferredSelectionHandler() {
         this.onSelectedFilesChanged();
         this.selectionTimeout = null;
      },
      
      /**
       * Updates the array of documents that are currently selected.
       * 
       * @instance
       * @param {object} The details of the document selected
       */
      onDocumentDeselected: function alfresco_services_ActionService__onDocumentDeselected(payload) {
         if (payload && payload.value && payload.value.nodeRef != null)
         {
            delete this.currentlySelectedDocuments[payload.value.nodeRef];
            if (this.selectionTimeout != null)
            {
               clearTimeout(this.selectionTimeout);
            }
            this.selectionTimeout = setTimeout(lang.hitch(this, "deferredSelectionHandler"), 50);
         }
      },
      
      /**
       * Converts the currently selected documents object into an array for easier iteration.
       * 
       * @instance
       */
      getSelectedDocumentArray: function alfresco_services_ActionService__getSelectedDocumentArray() {
         var a = [];
         for (var key in this.currentlySelectedDocuments)
         {
            a.push(this.currentlySelectedDocuments[key]);
         }
         return a;
      },
      
      /**
       * Handle changes in file selection by updating the ActionService 'currentlySelectedDocuments' attribute
       * so that other handlers can apply actions to the appropriate files and then evaluates the permissions
       * and aspects on the selected files and publishes the details on the 'selectedDocumentsChangeTopic' attribute
       * topic to allow menus to filter actions appropriately. 
       * 
       * @instance
       */
      onSelectedFilesChanged: function alfresco_services_ActionService__onSelectedFilesChanged() {
         var files = this.getSelectedDocumentArray(), fileTypes = [], file,
             fileType, userAccess = {}, fileAccess, index,
             commonAspects = [], allAspects = [],
             i, ii, j, jj;
         
         var fnFileType = function fnFileType(file)
         {
            return (file.node.isContainer ? "folder" : "document");
         };

         // Check each file for user permissions
         for (i = 0, ii = files.length; i < ii; i++)
         {
            file = files[i];
            
            // Required user access level - logical AND of each file's permissions
            fileAccess = file.node.permissions.user;
            for (index in fileAccess)
            {
               if (fileAccess.hasOwnProperty(index))
               {
                  userAccess[index] = (userAccess[index] === undefined ? fileAccess[index] : userAccess[index] && fileAccess[index]);
               }
            }
            
            // Make a note of all selected file types Using a hybrid array/object so we can use both array.length and "x in object"
            fileType = fnFileType(file);
            if (!(fileType in fileTypes))
            {
               fileTypes[fileType] = true;
               fileTypes.push(fileType);
            }

            // Build a list of common aspects
            if (i === 0)
            {
               // first time around fill with aspects from first node -
               // NOTE copy so we don't remove aspects from file node.
               commonAspects = Alfresco.util.deepCopy(file.node.aspects);
            } else
            {
               // every time after that remove aspect if it isn't present on the current node.
               for (j = 0, jj = commonAspects.length; j < jj; j++)
               {
                  if (!Alfresco.util.arrayContains(file.node.aspects, commonAspects[j]))
                  {
                     Alfresco.util.arrayRemove(commonAspects, commonAspects[j])
                  }
               }
            }

            // Build a list of all aspects
            for (j = 0, jj = file.node.aspects.length; j < jj; j++)
            {
               if (!Alfresco.util.arrayContains(allAspects, file.node.aspects[j]))
               {
                  allAspects.push(file.node.aspects[j])
               }
            }
         }
         
         // Publish the information about the actions so that menu items can be filtered...
         this.alfPublish(this.selectedDocumentsChangeTopic, {
            selectedFiles: files,
            userAccess: userAccess,
            commonAspects: commonAspects,
            allAspects: allAspects
         });
      },
      
      /**
       * This function handles requests to create new content. It handles content creation of 4 different types:
       * - pagelink (a link to another page within the application)
       * - link (a link to an external page)
       * - javascript (calls a JavaScript action handler)
       * - template (creates templated content)
       * 
       * @instance
       * @param {object} action An object containing the details of the action to perform
       * @param {object} document The document to perform the action on (only applicable to actions of type "javascript")
       */
      processActionObject: function alfresco_services_ContentService__processActionObject(action, document) {
         if (action && action.type)
         {
            if (action.type == "pagelink")
            {
               if (action.params.page)
               {
                  this.createPageLinkContent(action, document);
               }
               else
               {
                  this.alfLog("error", "A request was made to perform an action. The 'pagelink' type was requested, but no 'page' attribute was provided: ", action);
               }
            }
            else if (action.type == "link")
            {
               if (action.params.href)
               {
                  this.createLinkContent(action, document);
               }
               else
               {
                  this.alfLog("error", "A request was made to perform an action. The 'link' type was requested, but no 'href' attribute was provided: ", action);
               }
            }
            else if (action.type == "javascript")
            {
               if (action.params["function"])
               {
                  this.createJavaScriptContent(action, document);
               }
               else
               {
                  this.alfLog("error", "A request was made to perform an action. The 'javascript' type was requested, but no 'function' attribute was provided: ", action);
               }
            }
            else if (action.type == "template")
            {
               if (action.params.nodeRef)
               {
                  this.createTemplateContent(action, document);
               }
               else
               {
                  this.alfLog("error", "A request was made to perform an action. The 'template' type was requested, but no 'nodeRef' attribute was provided: ", action);
               }
            }
            else
            {
               this.alfLog("error", "A request was made to perform an action, but an unknown 'type' was provided: ", action);
            }
         }
         else
         {
            this.alfLog("error", "A request was made to perform an action, but no 'type' was provided in the payload: ", action);
         }
      },
      
      /**
       * Links to another page within Share to handle the content creation.
       * 
       * @instance
       * @param {object} payload
       * @param {object} document The document to perform the action using.
       */
      createPageLinkContent: function alfresco_services_ActionService__createPageLinkContent(payload, document) {
         var url = payload.params.page;
         if (document != null)
         {
            // TODO: Need to check other substitution points...
            url = lang.replace(url, document);
         }
         else if (this._currentNode != null)
         {
            url = lang.replace(url, { nodeRef: this._currentNode.parent.nodeRef});
         }
         
         // Make a request to navigation to a the URL (relative to the Share page content) within the current window...
         this.alfPublish(this.navigateToPageTopic, {
            type: this.sharePageRelativePath,
            url: url,
            target: this.currentTarget
         });
      },
      
      /**
       * Links to an external page to handle content creation.
       * 
       * @instance
       * @param {object} payload The payload published on the requesting topic
       * @param {object} document The document to perform the action on.
       */
      createLinkContent: function alfresco_services_ActionService__createLinkContent(payload, document) {
         // Make a request to navigation to a the URL defined within the current window...
         this.alfPublish(this.navigateToPageTopic, {
            type: this.fullPath,
            url: lang.replace(payload.params.href, this.getActionUrls(document, this.siteId, this.repositoryUrl, this.replicationUrlMapping)),
            target: this.currentTarget
         });
      },
      
      /**
       * Calls a JavaScript function to handle content creation.
       * 
       * @instance
       * @param {object} payload The payload published on the requesting topic
       * @param {object} document The document to perform the action on.
       */
      createJavaScriptContent: function alfresco_services_ActionService__createJavaScriptContent(payload, document) {
         // We're going to rely on the legacy Alfresco.util functions being available, however at some point these
         // will be replaced with Dojo based functions that should be used instead. We'll simply put some defensive
         // code in place to ensure that the Alfreco JavaScript resources are available...
         if (Alfresco && Alfresco.util && typeof Alfresco.util.deepCopy === "function" && typeof Alfresco.util.Node === "function")
         {
            if (document == null)
            {
               var node = Alfresco.util.deepCopy(this._currentNode.parent);
               document = {
                  nodeRef: node.nodeRef,
                  node: node,
                  jsNode: new Alfresco.util.Node(node)
               };
            }
         }
         
         // See if the requested function is provided by this service and if not delegate to the Alfresco.DocLibToolbar widget.
         var f = this[payload.params["function"]];
         if (typeof f === "function")
         {
            f.call(this, payload, document);
         }
         else
         {
            this.callLegacyActionHandler(payload.params["function"], document);
         }
      },
      
      /**
       * Calls a JavaScript function provided (or registered with) the Alfresco.DocLibToolbar widget.
       * 
       * @instance
       * @param {string} functionName The name of the function to call
       * @param {object} document The document to perform the action on.
       */
      callLegacyActionHandler: function alfresco_services_ActionService__callLegacyActionHandler(functionName, document) {
         var f = this.getLegacyToolbar()[functionName];
         if (typeof f === "function")
         {
            f.call(this.getLegacyToolbar(), document);
         }
         else
         {
            this.alfLog("error", "A request was made to perform an action but supplied function name does not map to a known function", functionName);
         }
      },
      
      /**
       * Handles requests to display the details of the supplied document. This function currently
       * delegates handling of the request to the Alfresco.DocLibToolbar by calling
       * [callLegacyActionHandler]{@link module:alfresco/services/ActionsService#callLegacyActionHandler}
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} document The document to get the details for
       */
      onActionDetails: function alfresco_services_ActionService__onActionDetails(payload, document) {
         this.callLegacyActionHandler("onActionDetails", document);
      },
      
      /**
       * Handles requests to upload a new version of the supplied document. This function currently
       * delegates handling of the request to the Alfresco.DocLibToolbar by calling
       * [callLegacyActionHandler]{@link module:alfresco/services/ActionsService#callLegacyActionHandler}
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} document The document to upload a new version for.
       */
      onActionUploadNewVersion: function alfresco_services_ActionService__onActionUploadNewVersion(payload, document) {
         this.callLegacyActionHandler("onActionUploadNewVersion", document);
      },
      
      /**
       * Handles requests to edit the supplied document offline. This posts a request to the
       * "/slingshot/doclib/action/checkout/node/{store_type}/{store_id}" repository WebScript to 
       * checkout the document. Successful requests are handled by the
       * [onActionEditOfflineSuccess]{@link module:alfresco/services/ActionsService#onActionEditOfflineSuccess} function
       * and failed requests are handled by the [onActionEditOfflineFailure]{@link module:alfresco/services/ActionsService#onActionEditOfflineFailure}
       * function.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} document The document edit offline.
       */
      onActionEditOffline: function alfresco_services_ActionService__onActionEditOffline(payload, document) {
         if (document != null && 
             document.jsNode != null && 
             document.jsNode.nodeRef != null)
         {
            var data = {
               nodeRef: document.jsNode.nodeRef.uri
            };
            var config = {
               url: Alfresco.constants.PROXY_URI + "slingshot/doclib/action/checkout/node/" + document.jsNode.nodeRef.uri,
               method: "POST",
               data: data,
               successCallback: this.onActionEditOfflineSuccess,
               failureCallback: this.onActionEditOfflineFailure,
               callbackScope: this
            };
            this.serviceXhr(config);
         }
         else
         {
            this.alfLog("error", "A request was made to edit a document offline, but the document supplied does not contain enough information", document);
         }
      },
      
      /**
       * This is the success callback handler from the XHR request made by [onActionEditOffline]{@link module:alfresco/services/ActionsService#onActionEditOffline}.
       * The success response should contain a download URL for the checked out document which is then passed to the browser
       * to automatically trigger a download of the document. A request is then published to reload the document list data.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      onActionEditOfflineSuccess: function alfresco_services_ActionService__onActionEditOfflineSuccess(response, originalRequestConfig) {
         this.alfLog("log", "Edit offline request success", response, originalRequestConfig);
         
         if (response != null && 
             response.results != null && 
             response.results.length > 0 &&
             response.results[0].downloadUrl != null)
         {
            this.displayMessage(this.message("message.edit-offline.success", {"0": response.results[0].id}));
            this.alfPublish(this.navigateToPageTopic, {
               url: Alfresco.constants.PROXY_URI + response.results[0].downloadUrl, 
               type: this.fullPath
            });
            this.alfPublish(this.reloadDataTopic, {});
         }
         else
         {
            this.alfLog("error", "A request to edit a document offline returned a successful response but did not provide a 'downloadUrl' attribute", response, originalRequestConfig);
         }
      },
      
      /**
       * This is the failure callback handler from the XHR request made by [onActionEditOffline]{@link module:alfresco/services/ActionsService#onActionEditOffline}.
       * It prompts the user with a message indicating that the document could not be checked out.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      onActionEditOfflineFailure: function alfresco_services_ActionService__onActionEditOfflineSuccess(response, originalRequestConfig) {
         this.alfLog("error", "Edit offline request failure", response, originalRequestConfig);
         
         this.displayMessage(this.message("message.edit-offline.failure", {"0": response.results[0].id}));
      },
      
      /**
       * Handles requests to copy the supplied document to another location. This function currently
       * delegates handling of the request to the Alfresco.DocLibToolbar by calling
       * [callLegacyActionHandler]{@link module:alfresco/services/ActionsService#callLegacyActionHandler}
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} document The document edit offline.
       */
      onActionCopyTo: function alfresco_services_ActionService__onActionCopyTo(payload, document) {
         this.callLegacyActionHandler("onActionCopyTo", document);
      },
      
      /**
       * Handles requests to move the supplied document to another location. This function currently
       * delegates handling of the request to the Alfresco.DocLibToolbar by calling
       * [callLegacyActionHandler]{@link module:alfresco/services/ActionsService#callLegacyActionHandler}
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} document The document edit offline.
       */
      onActionMoveTo: function alfresco_services_ActionService__onActionMoveTo(payload, document) {
         this.callLegacyActionHandler("onActionMoveTo", document);
      },
      
      /**
       * Handles requests to delete the supplied document. This function currently
       * delegates handling of the request to the Alfresco.DocLibToolbar by calling
       * [callLegacyActionHandler]{@link module:alfresco/services/ActionsService#callLegacyActionHandler}
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} document The document edit offline.
       */
      onActionDelete: function alfresco_services_ActionService__onActionDelete(payload, document) {
         this.callLegacyActionHandler("onActionDelete", document);
      },
      
      /**
       * Handles requests to start a new workflow using the supplied document. This function currently
       * delegates handling of the request to the Alfresco.DocLibToolbar by calling
       * [callLegacyActionHandler]{@link module:alfresco/services/ActionsService#callLegacyActionHandler}
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} document The document edit offline.
       */
      onActionAssignWorkflow: function alfresco_services_ActionService__onActionAssignWorkflow(payload, document) {
         this.callLegacyActionHandler("onActionAssignWorkflow", document);
      },
      
      /**
       * Handles requests to start a socially publish the supplied document. This function currently
       * delegates handling of the request to the Alfresco.DocLibToolbar by calling
       * [callLegacyActionHandler]{@link module:alfresco/services/ActionsService#callLegacyActionHandler}
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} document The document edit offline.
       */
      onActionPublish: function alfresco_services_ActionService__onActionPublish(payload, document) {
         this.callLegacyActionHandler("onActionPublish", document);
      },
      
      /**
       * Creates new content based on the nodeRef supplied.
       * 
       * @instance
       * @param {object} payload
       */
      createTemplateContent: function alfresco_services_ActionService__createTemplateContent(payload) {
         
         // Create content based on a template
         var node = payload.params.nodeRef,
             destination = this._currentNode.parent.nodeRef;

         // If node is undefined the loading or empty menu items were clicked
         if (node)
         {
            // Set up the favourites information...
            var url = Alfresco.constants.PROXY_URI + "slingshot/doclib/node-templates",
                dataObj = {
                   parentNodeRef: destination,
                   sourceNodeRef: node
                };
            this.serviceXhr({url : url,
                             node: node,
                             data: dataObj,
                             method: "POST",
                             successCallback: this.templateContentCreateSuccess,
                             failureCallback: this.templateContentCreateFailure,
                             callbackScope: this});
         }
      },
      
      /**
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      templateContentCreateSuccess: function alfresco_services_ActionService__templateContentCreateSuccess(response, originalRequestConfig) {
         this.displayMessage(this.message("message.create-content-by-template-node.success", response.name));
         this.alfPublish("ALF_NODE_CREATED", {
            name: response.name,
            parentNodeRef: originalRequestConfig.data.parentNodeRef,
            highlightFile: response.name
         });
      },
      
      /**
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      templateContentCreateFailure: function alfresco_services_ActionService__templateContentCreateSuccess(response, originalRequestConfig) {
         this.displayMessage(this.message("message.create-content-by-template-node.failure", response.name));
      },
      
      /**
       * @instance
       * @param {object} payload
       */
      onSyncLocation: function alfresco_services_ActionService__onSyncLocation(payload) {
         var node = Alfresco.util.deepCopy(this._currentNode.parent);
         var record = {
            nodeRef: node.nodeRef,
            displayName: node.properties["cm:name"],
            jsNode: new Alfresco.util.Node(node)
         };
         this.callLegacyActionHandler("onActionCloudSync", record);
      },
      
      /**
       * @instance
       * @param {object} payload
       */
      onUnsyncLocation: function alfresco_services_ActionService__onUnsyncLocation(payload) {
         var node = Alfresco.util.deepCopy(this._currentNode.parent);
         var record = {
            nodeRef: node.nodeRef,
            displayName: node.properties["cm:name"],
            jsNode: new Alfresco.util.Node(node)
         };
         this.callLegacyActionHandler("onActionCloudUnsync", record);
      },
      
      /**
       * Performs a document move. Note, this doesn't display a dialog for move options - it simply performs the move. The payload
       * published on this topic must contain a "sourceNodeRefs" attribute which should be an array of strings where each element
       * is a NodeRef to be moved. It can accept either a "targetNodeRefUri" or a "targetPath". The "targetNodeRefUri" should be
       * a URI fragment of a NodeRef (e.g. with the "://" converted to "/"). When a "targetPath" is specified then either the
       * "rootNode" attribute or a combination of "siteId" and "containerId" will be used to construct the POST URL.
       * 
       * @instance
       * @param {object} payload
       */
      onMoveDocuments: function alfresco_services_ActionService__onMoveDocuments(payload) {
         
         if (payload && 
             payload.sourceNodeRefs != null)
         {
            var url = null;
            if (payload.targetNodeRefUri != null)
            {
               url = Alfresco.constants.PROXY_URI + "slingshot/doclib/action/move-to/node/" + payload.targetNodeRefUri;
            }
            else if (payload.targetPath != null)
            {
               if (this.rootNode != null)
               {
                  var currentLocation = new Alfresco.util.Node(this.rootNode).nodeRef.uri;
                  url = Alfresco.constants.PROXY_URI + "slingshot/doclib/action/move-to/node/" + currentLocation + payload.targetPath;
               }
               else
               {
                  url = Alfresco.constants.PROXY_URI + "slingshot/doclib/action/move-to/site/" + this.siteId + "/" + this.containerId  + "/" + payload.targetPath;
               }
            }
            if (url != null)
            {
               var dataObj = {
                  nodeRefs: payload.sourceNodeRefs,
                  parentId: this._currentNode.parent.nodeRef
               };
               this.serviceXhr({url : url,
                                data: dataObj,
                                method: "POST",
                                successCallback: this.onMoveDocumentsSuccess,
                                failureCallback: this.onMoveDocumentsFailure,
                                callbackScope: this});
            }
            else
            {
               this.alfLog("warn", "Could not process a request to move documents due to missing attributes, either 'targetNodeRefUri' or 'targetPath' is required", payload);
            }
         }
      },
      
      /**
       * @instance
       * @param {object} response
       * @param {object} originalRequestConfig
       */
      onMoveDocumentsSuccess: function alfresco_services_ActionService__onMoveDocumentsSuccess(response, originalRequestConfig) {
         // TODO: Display a success message.
         this.alfPublish(this.reloadDataTopic, {});
      },
      
      /**
       * @instance
       * @param {object} response
       * @param {object} originalRequestConfig
       */
      onMoveDocumentsFailure: function alfresco_services_ActionService__onMoveDocumentsSuccess(response, originalRequestConfig) {
         // TODO: Publish an error message.
      }
   });
});