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
 * @module alfresco/services/QuickShareService
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "dojo/_base/lang"],
        function(declare, AlfCore, _AlfDocumentListTopicMixin, lang) {
   
   return declare([AlfCore, _AlfDocumentListTopicMixin], {
      
      /**
       * Re-use the old Alfresco.DocListToolbar scope. This could be replaced with a custom scope if the i18nRequirements file is also changed.
       * @instance
       * @type {string}
       * @default "Alfresco.DocListToolbar"
       */
      i18nScope: "Alfresco.DocListToolbar",
      
      /**
       * Re-use the toolbar properties for the DocumentList - this gives us access to the same labels for folders, etc.
       * @instance
       * @type {object[]}
       */
      i18nRequirements: [{i18nFile: "../../../WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/documentlibrary/toolbar.get.properties"}],
      
      /**
       * Sets up the subscriptions for the NavigationService
       * 
       * @instance
       * @param {array} args Constructor arguments
       */
      constructor: function alfresco_services_ContentService__constructor(args) {
         lang.mixin(this, args);
         this.alfSubscribe("ALF_CURRENT_NODEREF_CHANGED", lang.hitch(this, "handleCurrentNodeChange"));
         this.alfSubscribe("ALF_SHOW_UPLOADER", lang.hitch(this, "showUploader"));
         this.alfSubscribe("ALF_CREATE_NEW_FOLDER", lang.hitch(this, "createNewFolder"));
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
      handleCurrentNodeChange: function alfresco_services_ContentService__handleCurrentNodeRefChange(payload) {
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
       * 
       * @instance
       */
      showUploader: function alfresco_services_ContentService__showUploader(payload) {
         var multiUploadConfig =
         {
            destination: this._currentNode.parent.nodeRef,
            filter: [],
            mode: 3,
            thumbnails: "doclib",
            onFileUploadComplete:
            {
               fn: this.uploadComplete,
               scope: this
            }
         };
         Alfresco.util.ComponentManager.findFirst("Alfresco.DNDUpload").show(multiUploadConfig);
      },
      
      /**
       * This function is called when an upload started from the uploader displayed by the 'showUploader' function
       * complete.
       * 
       * @instance
       */
      uploadComplete: function alfresco_services_ContentService__uploadComplete(complete) {
         // No action here. The normal action is only to post activity updates and that is not currently
         // required.
      },
      
      /**
       * @instance
       */
      createNewFolder: function alfresco_services_ContentService__createNewFolder(payload) {
         var destination = this._currentNode.parent.nodeRef;

         // Intercept before dialog show
         var doBeforeDialogShow = function DLTB_onNewFolder_doBeforeDialogShow(p_form, p_dialog)
         {
            Dom.get(p_dialog.id + "-dialogTitle").innerHTML = this.message("label.new-folder.title");
            Dom.get(p_dialog.id + "-dialogHeader").innerHTML = this.message("label.new-folder.header");
         };
         
         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
         {
            itemKind: "type",
            itemId: "cm:folder",
            destination: destination,
            mode: "create",
            submitType: "json",
            formId: "doclib-common"
         });

         // Using Forms Service, so always create new instance
         var createFolder = new Alfresco.module.SimpleDialog(this.id + "-createFolder");

         createFolder.setOptions(
         {
            width: "33em",
            templateUrl: templateUrl,
            actionUrl: null,
            destroyOnHide: true,
            doBeforeDialogShow:
            {
               fn: doBeforeDialogShow,
               scope: this
            },
            onSuccess:
            {
               fn: function DLTB_onNewFolder_success(response)
               {
                  var activityData;
                  var folderName = response.config.dataObj["prop_cm_name"];
                  var folderNodeRef = response.json.persistedObject;
                  
//                  activityData =
//                  {
//                     fileName: folderName,
//                     nodeRef: folderNodeRef,
//                     path: this.currentPath + (this.currentPath !== "/" ? "/" : "") + folderName
//                  };
//                  this.modules.actions.postActivity(this.options.siteId, "folder-added", "documentlibrary", activityData);
                  
                  YAHOO.Bubbling.fire("folderCreated",
                  {
                     name: folderName,
                     parentNodeRef: destination
                  });
                  this.alfPublish(this.reloadDataTopic, {});
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.message("message.new-folder.success", {"0": folderName})
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function DLTB_onNewFolder_failure(response)
               {
                  if (response)
                  {
                     var folderName = response.config.dataObj["prop_cm_name"];
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.message("message.new-folder.failure", {"0": folderName})
                     });
                  }
                  else
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.message("message.failure")
                     });
                  }
               },
               scope: this
            }
         }).show();
      }
   });
});