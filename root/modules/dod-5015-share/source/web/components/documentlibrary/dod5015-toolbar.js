/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * DocumentList Toolbar component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsDocListToolbar
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * RecordsDocListToolbar constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsDocListToolbar} The new RecordsDocListToolbar instance
    * @constructor
    */
   Alfresco.RecordsDocListToolbar = function(htmlId)
   {
      YAHOO.Bubbling.on("userRMRoles", this.onUserRMRoles, this);
      return Alfresco.RecordsDocListToolbar.superclass.constructor.call(this, htmlId);
   };
   
   Alfresco.RecordsDocListToolbar.containerMap =
   {
      "new-series": "dod:recordSeries",
      "new-category": "dod:recordCategory",
      "new-folder": "rma:recordFolder"
   };

   /**
    * Extend Alfresco.DocListToolbar
    */
   YAHOO.extend(Alfresco.RecordsDocListToolbar, Alfresco.DocListToolbar);

   /**
    * Augment prototype with RecordsActions module, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentProto(Alfresco.RecordsDocListToolbar, Alfresco.doclib.RecordsActions, true);
   
   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.RecordsDocListToolbar.prototype,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DLTB_onReady()
      {
         // New Container button: user needs "create" and container-type access
         this.widgets.newContainer = Alfresco.util.createYUIButton(this, "newContainer-button", this.onNewContainer,
         {
            disabled: true,
            value: "new-series|new-category|new-folder"
         });
         this.widgets.newContainer.createAttribute("activePermission");
         this.widgets.newContainer.setAttributeConfig("activePermission",
         {
            method: this.setNewContainerPermissions,
            owner: this,
            silent: true,
            validator: YAHOO.lang.isString,
            value: ""
         });

         // File Upload button: user needs (generated) "file" access
         this.widgets.fileUpload = Alfresco.util.createYUIButton(this, "fileUpload-button", this.onFileUpload,
         {
            disabled: true,
            value: "file"
         });

         // Import button: user needs "Create" access
         this.widgets.importButton = Alfresco.util.createYUIButton(this, "import-button", this.onImport,
         {
            disabled: true
         });

         // Report button
         this.widgets.reportButton = Alfresco.util.createYUIButton(this, "report-button", this.onPrintReport,
         {
            disabled: true
         });

         // Export All button: only "read" access required
         this.widgets.exportAllButton = Alfresco.util.createYUIButton(this, "exportAll-button", this.onExportAll);

         // Selected Items menu button
         this.widgets.selectedItems = Alfresco.util.createYUIButton(this, "selectedItems-button", this.onSelectedItems,
         {
            type: "menu", 
            menu: "selectedItems-menu",
            lazyloadmenu: false,
            disabled: true
         });

         // Customize button
         this.widgets.customize = Alfresco.util.createYUIButton(this, "customize-button", this.onCustomize);

         // Hide/Show NavBar button
         this.widgets.hideNavBar = Alfresco.util.createYUIButton(this, "hideNavBar-button", this.onHideNavBar);
         this.widgets.hideNavBar.set("label", this.msg(this.options.hideNavBar ? "button.navbar.show" : "button.navbar.hide"));
         Dom.setStyle(this.id + "-navBar", "display", this.options.hideNavBar ? "none" : "block");
         
         // Folder Up Navigation button
         this.widgets.folderUp =  Alfresco.util.createYUIButton(this, "folderUp-button", this.onFolderUp,
         {
            disabled: true
         });

         // Transfers Folder Up Navigation button
         this.widgets.transfersFolderUp =  Alfresco.util.createYUIButton(this, "transfersFolderUp-button", this.onFilterFolderUp,
         {
            disabled: true
         });

         // Holds Folder Up Navigation button
         this.widgets.holdsFolderUp =  Alfresco.util.createYUIButton(this, "holdsFolderUp-button", this.onFilterFolderUp,
         {
            disabled: true
         });

         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();
         
         // Reference to Document List component
         this.modules.docList = Alfresco.util.ComponentManager.findFirst("Alfresco.DocumentList");

         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");

         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmroles?user=" + encodeURIComponent(Alfresco.constants.USERNAME),
            successCallback:
            {
               fn: function(response)
               {
                  if (response.json && response.json.data)
                  {                     
                     // Fire event to inform any listening components that the users rmroles are available
                     YAHOO.Bubbling.fire("userRMRoles",
                     {
                        roles: response.json.data
                     });
                  }                  
               },
               scope: this
            }
         });
      },

      /**
       * Filter Changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFilterChanged: function DLTB_onFilterChanged(layer, args)
      {
         Alfresco.RecordsDocListToolbar.superclass.onFilterChanged.apply(this, arguments);
         
         var upFolderEnabled = (this.currentFilter.filterId == "holds" && this.currentFilter.filterData !== "");
         this.widgets.holdsFolderUp.set("disabled", !upFolderEnabled);

         upFolderEnabled = (this.currentFilter.filterId == "transfers" && this.currentFilter.filterData !== "");
         this.widgets.transfersFolderUp.set("disabled", !upFolderEnabled);
      },

      /**
       * User RMRoles event handler
       *
       * @method onUserRMRoles
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onUserRMRoles: function DLTB_onUserRMRoles(layer, args)
      {
         var obj = args[1];
         if (obj && obj.roles && obj.roles.Administrator)
         {
            // Let RM Administrators do imports
            Dom.removeClass(this.id + "-import-section", "toolbar-hidden");
            this.widgets.importButton.set("disabled", false);
         }
      },

      /**
       * Document List Metadata event handler
       * NOTE: This is a temporary fix to enable access to the View Details action from the breadcrumb.
       *       A more complete solution is to present the full list of parent folder actions.
       *
       * @method onDoclistMetadata
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDoclistMetadata: function DLTB_onDoclistMetadata(layer, args)
      {
         var obj = args[1];
         this.folderDetailsUrl = null;
         if (obj && obj.metadata)
         {
            var p = obj.metadata.parent;
            this.folderDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/" + p.type + "-details?nodeRef=" + p.nodeRef;
         }
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

       /**
        * Required because this class has declared itself a button attribute owner.
        * See declaration of this.widgets.newContainer in onReady()
        *
        * @method fireBeforeChangeEvent
        * @return {boolean} true
        */
       fireBeforeChangeEvent: function DLTB_fireBeforeChangeEvent(e)
       {
          return true;
       },

      /**
       * Called when the value of the button's "permissions" attribute is set.
       *
       * @method setNewContainerPermissions
       * @param {String} p_sPermission String indicating the value for the button's "permission" attribute.
       */
      setNewContainerPermissions: function DLTB_setNewContainerPermissions(p_sValue)
      {
         this.widgets.newContainer.set("label", this.msg("button." + p_sValue));
         this.widgets.newContainer.set("name", Alfresco.RecordsDocListToolbar.containerMap[p_sValue]);
      },

      /**
       * New Container button click handler
       *
       * Look at the event source to work out what type of container to create
       *
       * @method onNewContainer
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewContainer: function DLTB_onNewContainer(e, p_obj)
      {
         var destination = this.modules.docList.doclistMetadata.parent.nodeRef,
            folderType = p_obj.get("name"),
            label = "label.new-" + p_obj.get("name").replace(":", "_"),
            msgTitle = this.msg(label + ".title"),
            msgHeader = this.msg(label + ".header");

         // Intercept before dialog show
         var doBeforeDialogShow = function DLTB_onNewContainer_doBeforeDialogShow(p_form, p_dialog)
         {
            Dom.get(p_dialog.id + "-dialogTitle").innerHTML = msgTitle;
            Dom.get(p_dialog.id + "-dialogHeader").innerHTML = msgHeader;
         };
         
         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&showCancelButton=true",
         {
            itemKind: "type",
            itemId: folderType,
            destination: destination,
            mode: "create",
            submitType: "json"
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
               fn: function DLTB_onNewContainer_success(response)
               {
                  var folderName = response.config.dataObj["prop_cm_name"];
                  YAHOO.Bubbling.fire("folderCreated",
                  {
                     name: folderName,
                     parentNodeRef: destination
                  });
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-folder.success", folderName)
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function DLTB_onNewContainer_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-folder.failure")
                  });
               },
               scope: this
            }
         }).show();
      },

      /**
       * File Upload button click handler
       *
       * @method onFileUpload
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFileUpload: function DLTB_onFileUpload(e, p_obj)
      {
         var me = this;
         
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.file.type.title"),
            text: this.msg("message.file.type"),
            buttons: [
            {
               text: this.msg("button.electronic"),
               handler: function DLTB_onFileUpload_electronic()
               {
                  this.destroy();
                  me.onElectronicRecord.call(me);
               },
               isDefault: true
            },
            {
               text: this.msg("button.non-electronic"),
               handler: function DLTB_onFileUpload_nonElectronic()
               {
                  this.destroy();
                  me.onNonElectronicDocument.call(me);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function DLTB_onFileUpload_cancel()
               {
                  this.destroy();
               }
            }]
         });
      },
      
      /**
       * Electronic Record button click handler
       *
       * @method onElectronicRecord
       */
      onElectronicRecord: function DLTB_onElectronicRecord()
      {
         if (this.fileUpload === null)
         {
            this.fileUpload = Alfresco.getRecordsFileUploadInstance();
         }
         
         // Show uploader for multiple files
         this.fileUpload.show(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            uploadDirectory: this.currentPath,
            filter: [],
            mode: this.fileUpload.MODE_MULTI_UPLOAD,
            thumbnails: "doclib",
            onFileUploadComplete:
            {
               fn: this.onFileUploadComplete,
               scope: this
            }
         });
      },

      /**
       * Non-Electronic Record button click handler
       *
       * @method onNonElectronicDocument
       */
      onNonElectronicDocument: function DLTB_onNonElectronicDocument()
      {
         var destination = this.modules.docList.doclistMetadata.parent.nodeRef,
            label = "label.new-rma_nonElectronicDocument",
            msgTitle = this.msg(label + ".title"),
            msgHeader = this.msg(label + ".header");

         // Intercept before dialog show
         var doBeforeDialogShow = function DLTB_onNonElectronicDocument_doBeforeDialogShow(p_form, p_dialog)
         {
            Dom.get(p_dialog.id + "-dialogTitle").innerHTML = msgTitle;
            Dom.get(p_dialog.id + "-dialogHeader").innerHTML = msgHeader;
         };
         
         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&showCancelButton=true",
         {
            itemKind: "type",
            itemId: "rma:nonElectronicDocument",
            destination: destination,
            mode: "create",
            submitType: "json"
         });

         // Using Forms Service, so always create new instance
         var createRecord = new Alfresco.module.SimpleDialog(this.id + "-createRecord");

         createRecord.setOptions(
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
               fn: function DLTB_onNonElectronicDocument_success(response)
               {
                  var fileName = response.config.dataObj["prop_cm_name"];
                  YAHOO.Bubbling.fire("metadataRefresh",
                  {
                     highlightFile: fileName
                  });
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-record.success", fileName)
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function DLTB_onNonElectronicDocument_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-record.failure")
                  });
               },
               scope: this
            }
         }).show();
      },

      /**
       * Import button click handler
       *
       * @method onImport
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onImport: function DLTB_onImport(e, p_obj)
      {
         // Create Uploader (Importer) component if it doesn't exist
         if (this.fileUpload === null)
         {
            this.fileUpload = Alfresco.getRecordsFileUploadInstance();
         }

         // Show uploader for single import file
         this.fileUpload.show(
         {
            mode: this.fileUpload.MODE_SINGLE_IMPORT,
            importDestination: this.modules.docList.doclistMetadata.parent.nodeRef,
            filter: [
            {
               description: this.msg("label.filter-description.acp"),
               extensions: "*.acp"
            },
            {
               description: this.msg("label.filter-description.zip"),
               extensions: "*.zip"
            }]
         });
      },

      /**
       * Print Report button click handler
       *
       * @method onPrintReport
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onPrintReport: function DLTB_onPrintReport(e, p_obj)
      {
         var url = Alfresco.constants.URL_PAGECONTEXT + 'fileplanreport?nodeRef=' + this.modules.docList.doclistMetadata.parent.nodeRef;
         window.open(url, 'fileplanreport', 'width=550,height=650,scrollbars=yes,resizable=yes,toolbar=no,menubar=no');
      },

      /**
       * Export All button click handler
       *
       * @method onExportAll
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onExportAll: function DLTB_onExportAll(e, p_obj)
      {
         // Url to webscript to get the nodeRefs for the top level series
         var url = Alfresco.constants.PROXY_URI + "slingshot/doclib/dod5015/treenode/site/";
         url += encodeURIComponent(this.options.siteId) + "/" + encodeURIComponent(this.options.containerId);
         url += "?perms=false&children=false";

         // Load all series so they (and all their child objects) can be exported
         Alfresco.util.Ajax.jsonGet(
         {
            url: url,
            successCallback:
            {
               fn: function(serverResponse)
               {
                  if (serverResponse.json && serverResponse.json.items && serverResponse.json.items.length > 0)
                  {
                     // Display the export dialog and do the export
                     this.onActionExport(serverResponse.json.items);
                  }
                  else
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.nothing-to-export")
                     });
                  }
               },
               scope: this
            },
            failureMessage: this.msg("message.load-top-level-assets.failure")
         });
      },

      /**
       * Transfers/Holds Folder Up Navigate button click handler
       *
       * @method onFilterFolderUp
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFilterFolderUp: function DLTB_onFilterFolderUp(e, p_obj)
      {
         YAHOO.Bubbling.fire("changeFilter",
         {
            filterId: this.currentFilter.filterId,
            filterData: ""
         });
         Event.preventDefault(e);
      }
   }, true);
})();