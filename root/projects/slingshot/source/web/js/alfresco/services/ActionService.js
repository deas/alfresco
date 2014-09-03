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
        "service/constants/Default",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "alfresco/services/_NavigationServiceTopicMixin",
        "alfresco/core/UrlUtils",
        "alfresco/core/ArrayUtils",
        "alfresco/core/JsNode",
        "alfresco/core/NotificationUtils",
        "dojo/_base/lang",
        "dojo/_base/array",
        "alfresco/dialogs/AlfDialog",
        "alfresco/pickers/Picker"],
        function(declare, AlfCore, AlfCoreXhr, AlfConstants, _AlfDocumentListTopicMixin, _NavigationServiceTopicMixin, UrlUtils, 
                 ArrayUtils, JsNode, NotificationUtils, lang, array, AlfDialog, Picker) {
   
   return declare([AlfCore, AlfCoreXhr, _AlfDocumentListTopicMixin, _NavigationServiceTopicMixin, UrlUtils, NotificationUtils], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/ActionService.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/ActionService.properties"}],
      
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
       * Used by callbacks from picker dialogs to store the picked target.
       *
       * @instance
       * @type {string}
       * @default null
       */
      copyMoveTarget: null,

      /**
       * Sets up the subscriptions for the NavigationService
       * 
       * @instance
       * @param {array} args Constructor arguments
       */
      constructor: function alfresco_services_ActionService__constructor(args) {
         lang.mixin(this, args);
         
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

         // Response handlers...
         this.alfSubscribe("ALF_ON_ACTION_DETAILS_SUCCESS", lang.hitch(this, "onActionDetailsSucess"));
         this.alfSubscribe("ALF_ON_ACTION_EDIT_INLINE_SUCCESS", lang.hitch(this, "onActionEditInlineSucess"));
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
               if (typeof this[payload.action] === "function")
               {
                  // Then check the actions provided by this service...
                  this[payload.action](payload.document);
               }
               else
               {
                  this.alfLog("warn", "No handler implemented to handle this function", this);
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
         if (typeof this[payload.action] === "function")
         {
            var documents = [];
            for (var nodeRef in this.currentlySelectedDocuments)
            {
               documents.push(this.currentlySelectedDocuments[nodeRef])
            }
            this[payload.action].call(this, payload, documents);
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
               commonAspects = lang.clone(file.node.aspects);
            } else
            {
               // every time after that remove aspect if it isn't present on the current node.
               for (j = 0, jj = commonAspects.length; j < jj; j++)
               {
                  if (!ArrayUtils.arrayContains(file.node.aspects, commonAspects[j]))
                  {
                     ArrayUtils.arrayRemove(commonAspects, commonAspects[j])
                  }
               }
            }

            // Build a list of all aspects
            for (j = 0, jj = file.node.aspects.length; j < jj; j++)
            {
               if (!ArrayUtils.arrayContains(allAspects, file.node.aspects[j]))
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
         if (document == null)
         {
            var node = lang.clone(this._currentNode.parent);
            document = {
               nodeRef: node.nodeRef,
               node: node,
               jsNode: new JsNode(node)
            };
         }
         
         // See if the requested function is provided by this service and if not delegate to the Alfresco.DocLibToolbar widget.
         var f = this[payload.params["function"]];
         if (typeof f === "function")
         {
            // TODO: Should the document really be an Array?
            f.call(this, payload, [document]);
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
         this.alfLog("warn", "Legacy toolbar now removed");
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

         // Sometimes Document might be an array!
         document = (lang.isArray(document))? document[0] : document;

         // 1. Get the data.
         // 2. Create a form dialog containing fields for all the properties

         if (document == null || document.nodeRef == null)
         {
            this.alfLog("warn", "A request was made to edit the properties of a document but no document or 'nodeRef' attribute was provided", document, this);
         }
         else
         {
            // TODO: We're not yet setting up a failure response handler, this is just working on the golden path at the moment...
            this.alfPublish("ALF_RETRIEVE_SINGLE_DOCUMENT_REQUEST", {
               alfResponseTopic: "ALF_ON_ACTION_DETAILS",
               nodeRef: document.nodeRef
            });
         }
      },

      /**
       * This function will be called in response to a documents details being successfully retrieved. 
       *
       * @instance
       * @param {object} payload
       */
      onActionDetailsSucess: function alfresco_services_ActionService__onActionDetailsSucess(payload) {
         if (!lang.exists("response.item.node.properties", payload))
         {
            this.alfLog("warn", "When processing a request to display document properties, the expected 'response.item.node.properties' attribute was not found", payload, this);
         }
         else
         {
            var properties = lang.getObject("response.item.node.properties", false, payload);
            this.alfLog("log", "Display these properties", properties);

            // TODO: Contruct dialog form...
            var widgets = [];
            for (var key in properties)
            {
               // It's a converntion of the forms processor to convert the property value into the form
               // "prop_<namespace>_<property name>". It's necessary to convert each property into this form
               // so that it can be posted correctly from the dialog.
               var splitKeyArr = key.split(":"),
                   name = "";
               if (splitKeyArr.length == 2)
               {
                  name = "prop_" + splitKeyArr[0] + "_" + splitKeyArr[1];
               }

               // Construct a new widget definition...
               // TODO: At the moment everything is being generated as a validation text box which is clearly incorrect.
               //       We need to be able to select the correct form control based on the property type. This could potentially
               //       be leveraged from configuration. We also need to filter out properties (e.g. those in the "sys:" namespace)
               //       that shouldn't be displayed.
               var widget = {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     label: key,
                     value: properties[key],
                     name: name 
                  }
               }
               widgets.push(widget);
            }

            // Publish the request to generate a new dialog showing a form with all the properties displayed...
            this.alfPublish("ALF_CREATE_FORM_DIALOG_REQUEST", {
               dialogTitle: "Edit Properties",
               dialogConfirmationButtonTitle: "Save",
               dialogCancellationButtonTitle: "Cancel",
               formSubmissionTopic: "ALF_CREATE_CONTENT_REQUEST",
               widgets: widgets
            });
         }
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
       * Handles requests to edit a document inline.
       *
       * @instance
       * @param {object} response The response from the request
       * @param {object} document The document to edit.
       */
      onActionEditInline: function alfresco_services_ActionService__onActionEditInline(payload, document) {
         if (document == null || document.nodeRef == null)
         {
            this.alfLog("warn", "A request was made to edit the properties of a document but no document or 'nodeRef' attribute was provided", document, this);
         }
         else
         {
            // TODO: We're not yet setting up a failure response handler, this is just working on the golden path at the moment...
            this.alfPublish("ALF_RETRIEVE_SINGLE_DOCUMENT_REQUEST", {
               alfResponseTopic: "ALF_ON_ACTION_EDIT_INLINE",
               nodeRef: document.nodeRef,
               includeContent: true
            });
         }
      },

      /**
       * This function will be called in response to a documents details being successfully retrieved. 
       *
       * @instance
       * @param {object} payload
       */
      onActionEditInlineSucess: function alfresco_services_ActionService__onActionEditInlineSucess(payload) {

         if (!lang.exists("response.item.node", payload))
         {
            this.alfLog("warn", "When processing a request to display document properties, the expected 'response.item.node' attribute was not found", payload, this);
         }
         else
         {
            var node = lang.getObject("response.item.node", false, payload);
            var content = lang.getObject("response.itemContent", false, payload);
            
            this.alfPublish("ALF_CREATE_FORM_DIALOG_REQUEST", {
               dialogTitle: "Edit: " + node.properties["cm:name"],
               dialogConfirmationButtonTitle: "Save",
               dialogCancellationButtonTitle: "Cancel",
               formSubmissionTopic: "ALF_UPDATE_CONTENT_REQUEST",
               widgets: [
                  {
                     name: "alfresco/forms/controls/DojoValidationTextBox",
                     config: {
                        name: "nodeRef",
                        value: node.nodeRef,
                        visibilityConfig: {
                           initialValue: false
                        }
                     }
                  },
                  {
                     name: "alfresco/forms/controls/DojoValidationTextBox",
                     config: {
                        name: "prop_mimetype",
                        value: node.mimetype,
                        visibilityConfig: {
                           initialValue: false
                        }
                     }
                  },
                  {
                     name: "alfresco/forms/controls/DojoValidationTextBox",
                     config: {
                        name: "prop_app_editInline",
                        value: true,
                        visibilityConfig: {
                           initialValue: false
                        }
                     }
                  },
                  {
                     name: "alfresco/forms/controls/DojoValidationTextBox",
                     config: {
                        label: "Name",
                        description: "The name to give the new document",
                        name: "prop_cm_name",
                        value: node.properties["cm:name"],
                        requirementConfig: {
                           initialValue: true
                        }
                     }
                  },
                  {
                     name: "alfresco/forms/controls/DojoValidationTextBox",
                     config: {
                        label: "Title",
                        description: "The title to give to the new document",
                        name: "prop_cm_title",
                        value: node.properties["cm:title"]
                     }
                  },
                  {
                     name: "alfresco/forms/controls/DojoTextarea",
                     config: {
                        label: "Description",
                        description: "A description of the folder",
                        name: "prop_cm_description",
                        value: node.properties["cm:description"]
                     }
                  },
                  {
                     name: "alfresco/forms/controls/AceEditor",
                     config: {
                        mimeType: node.mimetype,
                        label: "Content",
                        description: "The content for the document",
                        name: "prop_cm_content",
                        value: content
                     }
                  }
               ]
            });
         }
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

         // Document might be an array.
         document = (lang.isArray(document))? document[0] : document;

         if (document != null &&
             document.node != null &&
             document.node.nodeRef != null)
         {
            var data = {
               nodeRef: document.node.nodeRef
            };
            var config = {
               url: AlfConstants.PROXY_URI + "slingshot/doclib/action/checkout/node/" + data.nodeRef.replace("://", "/"),
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
               url: AlfConstants.PROXY_URI + response.results[0].downloadUrl, 
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
       * Handles requests to copy the supplied document to another location.
       * 
       * @instance
       * @param {object} payload
       * @param {object} document The document edit offline.
       */
      onActionCopyTo: function alfresco_services_ActionService__onActionCopyTo(payload, document) {

         // TODO: This should accept multiple documents/
         var responseTopic = this.generateUuid() + "_ALF_COPY_LOCATION_PICKED",
            publishPayload = {
               nodes: [document.nodeRef],
               nodeRefTarget: this.copyMoveTarget,
               document: document
            };
         this._actionCopyHandle = this.alfSubscribe(responseTopic, lang.hitch(this, this.onActionCopyToLocationSelected), true);

         this.alfSubscribe("ALF_ITEM_SELECTED", lang.hitch(this, this.onActionCopyAddItem , true));

         // Create a dialog containing the picker...
         var dialog = new AlfDialog({
            generatePubSubScope: true,
            title: this.message("services.ActionService.copyTo.title"),
            widgetsContent: [
               {
                  name: "alfresco/pickers/Picker",
                  config: {
                     widgetsForRootPicker: [
                        {
                           name: "alfresco/menus/AlfVerticalMenuBar",
                           config: {
                              widgets: [
                                 {
                                    name: "alfresco/menus/AlfMenuBarItem",
                                    config: {
                                       label: "My Files",
                                       publishTopic: "ALF_ADD_PICKER",
                                       publishPayload: {
                                          currentPickerDepth: 0,
                                          picker: {
                                             name: "alfresco/pickers/ContainerListPicker",
                                             config: {
                                                nodeRef: "alfresco://user/home",
                                                path: "/"
                                             }
                                          }
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuBarItem",
                                    config: {
                                       label: "Shared Files",
                                       publishTopic: "ALF_ADD_PICKER",
                                       publishPayload: {
                                          currentPickerDepth: 0,
                                          picker: {
                                             name: "alfresco/pickers/ContainerListPicker",
                                             config: {
                                                nodeRef: "alfresco://company/shared",
                                                path: "/"
                                             }
                                          }
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuBarItem",
                                    config: {
                                       label: "Repository",
                                       publishTopic: "ALF_ADD_PICKER",
                                       publishPayload: {
                                          currentPickerDepth: 0,
                                          picker: {
                                             name: "alfresco/pickers/ContainerListPicker",
                                             config: {
                                                nodeRef: "alfresco://company/home",
                                                path: "/"
                                             }
                                          }
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuBarItem",
                                    config: {
                                       label: "Recent Sites",
                                       publishTopic: "ALF_ADD_PICKER",
                                       publishPayload: {
                                          currentPickerDepth: 0,
                                          picker: {
                                             name: "alfresco/pickers/SingleItemPicker",
                                             config: {
                                                currentPickerDepth: 1,
                                                requestItemsTopic: "ALF_GET_RECENT_SITES",
                                                subPicker: "alfresco/pickers/ContainerListPicker"
                                             }
                                          }
                                       }
                                    }
                                 },
                                 {
                                    name: "alfresco/menus/AlfMenuBarItem",
                                    config: {
                                       label: "Favorite Sites",
                                       publishTopic: "ALF_ADD_PICKER",
                                       publishPayload: {
                                          currentPickerDepth: 0,
                                          picker: {
                                             name: "alfresco/pickers/SingleItemPicker",
                                             config: {
                                                currentPickerDepth: 1,
                                                requestItemsTopic: "ALF_GET_FAVOURITE_SITES",
                                                subPicker: "alfresco/pickers/ContainerListPicker"
                                             }
                                          }
                                       }
                                    }
                                 }
                              ]
                           }
                        }
                     ],
                  }
               }
            ],
            widgetsButtons: [
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: "OK",
                     publishTopic: responseTopic,
                     publishPayload: publishPayload
                  }
               },
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: "CANCEL",
                     publishTopic: ""
                  }
               }
            ]
         });
         dialog.show();
      },

      /**
       * Handles the response to an item being clicked.
       *
       * @instance
       * @param payload
       */
      onActionCopyAddItem: function alfresco_services_ActionService__onActionCopyAddItem(payload) {
         this.copyMoveTarget = payload.nodeRef;
      },

      /**
       * Handles the actual copy call - triggered once the location has been selected by
       * [onActionCopyTo]{@link module:alfresco/services/ActionsService#onActionCopyTo}
       *
       * @instance
       * @param payload
       * @param document
       */
      onActionCopyToLocationSelected: function alfresco_services_ActionService__onActionCopyToLocationSelected(payload, document) {
         if (this._actionCopyHandle != null)
         {
            this.alfUnsubscribe(this._actionCopyHandle);
         }
         else
         {
            this.alfLog("warn", "A subscription handle was not found for picking copy location - this could be a potential memory leak", this);
         }

         var nodeRefs = array.map(payload.nodes, function(item) {
            return item.nodeRef;
         });
         var responseTopic = this.generateUuid();
         var subscriptionHandle = this.alfSubscribe(responseTopic + "_SUCCESS", lang.hitch(this, this.onActionCopyToSuccess), true);
         var nodeRefTarget = payload.nodeRefTarget

         this.serviceXhr({
            alfTopic: responseTopic,
            subscriptionHandle: subscriptionHandle,
            url: AlfConstants.PROXY_URI + "slingshot/doclib/action/copy-to/node/" + payload.nodes[0],
            method: "POST",
            data: {
               nodeRefs: nodeRefs,
               parentId: nodeRefTarget
            }
         });
      },

      /**
       * What should we do when the copy action succeeds?
       *
       * @instance
       * @param payload
       */
      onActionCopyToSuccess: function alfresco_services_ActionService__onActionCopyToSuccess(payload) {
         this.onActionCompleteSuccess(arguments);
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
      onActionDelete: function alfresco_services_ActionService__onActionDelete(payload, documents) {

         var responseTopic = this.generateUuid();
         this._actionDeleteHandle = this.alfSubscribe(responseTopic, lang.hitch(this, "onActionDeleteConfirmation"), true);

         var dialog = new AlfDialog({
            generatePubSubScope: false,
            title: "Confirm Delete",
            // textContent: "Are you sure you want to delete '" + documents + "'?",
            widgetsContent: [
               {
                  name: "alfresco/documentlibrary/views/AlfDocumentListView",
                  config: {
                     currentData: {
                        items: documents
                     },
                     widgets: [
                        {
                           name: "alfresco/documentlibrary/views/layouts/Row",
                           config: {
                              widgets: [
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/Cell",
                                    config: {
                                       widgets: [
                                          {
                                             name: "alfresco/renderers/SmallThumbnail"
                                          }
                                       ]
                                    }
                                 },
                                 {
                                    name: "alfresco/documentlibrary/views/layouts/Cell",
                                    config: {
                                       widgets: [
                                          {
                                             name: "alfresco/renderers/Property",
                                             config: {
                                                propertyToRender: "node.properties.cm:name"
                                             }
                                          }
                                       ]
                                    }
                                 }
                              ]
                           }
                        }
                     ]
                  }
               }
            ],
            widgetsButtons: [
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: "Yes",
                     publishTopic: responseTopic,
                     publishPayload: {
                        nodes: documents,
                        completionTopic: payload.completionTopic
                     }
                  }
               },
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     label: "No",
                     publishTopic: "close"
                  }
               }
            ]
         });
         dialog.show();
      },

      /**
       * This function is called when the user confirms that they wish to delete a document
       * 
       * @instance
       * @param {object} payload An object containing the details of the document(s) to be deleted.
       */
      onActionDeleteConfirmation: function alfresco_services_ActionService__onActionDeleteConfirmation(payload) {
         if (this._actionDeleteHandle != null)
         {
            this.alfUnsubscribe(this._actionDeleteHandle);
         }
         else
         {
            this.alfLog("warn", "A subscription handle was not found for confirming delete actions - this could be a potential memory leak", this);
         }

         var nodeRefs = array.map(payload.nodes, function(item) {
            return item.nodeRef;
         });
         var responseTopic = this.generateUuid();
         var subscriptionHandle = this.alfSubscribe(responseTopic + "_SUCCESS", lang.hitch(this, "onActionDeleteSuccess"), true);

         this.serviceXhr({
            alfTopic: responseTopic,
            subscriptionHandle: subscriptionHandle,
            url: AlfConstants.PROXY_URI + "slingshot/doclib/action/files?alf_method=delete",
            method: "POST",
            data: {
               nodeRefs: nodeRefs
            }
         });
      },

      /**
       * This action will be called when documents are successfully deleted
       *
       * @instance
       * @param {object} payload
       */
      onActionDeleteSuccess: function alfresco_services_ActionService__onActionDeleteSuccess(payload) {
         this.onActionCompleteSuccess(arguments);
      },

      /**
       * Used by action handlers. Deletes subscription handle and reloads doclist.
       *
       * @instance
       * @param {object} payload
       */
      onActionCompleteSuccess: function alfresco_services_ActionService__onActionCompleteSuccess(payload) {
         var subscriptionHandle = lang.getObject("requestConfig.subscriptionHandle", false, payload);
         if (subscriptionHandle != null)
         {
            this.alfUnsubscribe(subscriptionHandle);
         }
         this.alfPublish("ALF_DOCLIST_RELOAD_DATA", {});
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
            var url = AlfConstants.PROXY_URI + "slingshot/doclib/node-templates",
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
         var node = lang.clone(this._currentNode.parent);
         var record = {
            nodeRef: node.nodeRef,
            displayName: node.properties["cm:name"],
            jsNode: new JsNode(node)
         };
         this.callLegacyActionHandler("onActionCloudSync", record);
      },
      
      /**
       * @instance
       * @param {object} payload
       */
      onUnsyncLocation: function alfresco_services_ActionService__onUnsyncLocation(payload) {
         var node = lang.clone(this._currentNode.parent);
         var record = {
            nodeRef: node.nodeRef,
            displayName: node.properties["cm:name"],
            jsNode: new JsNode(node)
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
               url = AlfConstants.PROXY_URI + "slingshot/doclib/action/move-to/node/" + payload.targetNodeRefUri;
            }
            else if (payload.targetPath != null)
            {
               if (this.rootNode != null)
               {
                  var currentLocation = new JsNode(this.rootNode).nodeRef.uri;
                  url = AlfConstants.PROXY_URI + "slingshot/doclib/action/move-to/node/" + currentLocation + payload.targetPath;
               }
               else
               {
                  url = AlfConstants.PROXY_URI + "slingshot/doclib/action/move-to/site/" + this.siteId + "/" + this.containerId  + "/" + payload.targetPath;
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