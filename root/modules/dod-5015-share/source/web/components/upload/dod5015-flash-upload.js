/*
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
 * RecordsFlashUpload component.
 *
 * Popups a YUI panel and displays a filelist and buttons to browse for files
 * and upload them. Files can be removed and uploads can be cancelled.
 * For single file uploads version input can be submitted.
 *
 * A multi file upload scenario could look like:
 *
 * var flashUpload = Alfresco.component.getRecordsFlashUploadInstance();
 * var multiUploadConfig =
 * {
 *    siteId: siteId,
 *    containerId: doclibContainerId,
 *    path: docLibUploadPath,
 *    filter: [],
 *    mode: flashUpload.MODE_MULTI_UPLOAD,
 * }
 * this.flashUpload.show(multiUploadConfig);
 *
 * @namespace Alfresco.module
 * @class Alfresco.RecordsFlashUpload
 * @extends Alfresco.FlashUpload
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * RecordsFlashUpload constructor.
    *
    * RecordsFlashUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.component.getRecordsFlashUploadInstance() instead.
    *
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.component.RecordsFlashUpload} The new RecordsFlashUpload instance
    * @constructor
    * @private
    */
   Alfresco.RecordsFlashUpload = function(htmlId)
   {
      Alfresco.RecordsFlashUpload.superclass.constructor.call(this, htmlId);

      this.name = "Alfresco.RecordsFlashUpload";
      this.defaultShowConfig.importDestination = null;
      this.defaultShowConfig.importUrl = null;

      Alfresco.util.ComponentManager.reregister(this);
      
      return this;
   };

   YAHOO.extend(Alfresco.RecordsFlashUpload, Alfresco.FlashUpload,
   {
      /**
       * Shows uploader in single import mode.
       *
       * @property MODE_SINGLE_IMPORT
       * @static
       * @type int
       */
      MODE_SINGLE_IMPORT: 4,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       * @override
       */
      onReady: function RecordsFlashUpload_onReady()
      {
         Alfresco.RecordsFlashUpload.superclass.onReady.call(this);
         
         // Create and save a reference to the uploadButton so we can alter it later
         this.widgets.recordType = new YAHOO.widget.Button(
         {
            id: this.id + "-recordTypes-select-menu",
            label: this.msg("recordType.default"),
            type: "menu",
            menu: this.id + "-recordTypes-select",
            lazyloadmenu: false,
            container: this.id + "-recordTypes-select-container"
         });
         // Make sure we can evaluate selectedMenuItem even if no user interaction occurs
         var menu = this.widgets.recordType.getMenu();
         menu.initEvent.fire();
         menu.render();
         this.widgets.recordType.set("selectedMenuItem", menu.getItem(menu.srcElement.selectedIndex));

         /**
          * "selectedMenuItemChange" event handler for a Button that will set 
          * the Button's "label" attribute to the value of the "text" 
          * configuration property of the MenuItem that was clicked.
          */
         var fnRecordTypeItemChange = function RecordsFlashUpload_fnRecordTypeItemChange(event)
         {
            var oMenuItem = event.newValue;
            this.set("label", oMenuItem.cfg.getProperty("text"));
         };
         this.widgets.recordType.on("selectedMenuItemChange", fnRecordTypeItemChange);

         // Save a reference to the HTMLElement displaying recordTypeSection input so we can hide or show it
         this.widgets.recordTypeSection = Dom.get(this.id + "-recordTypeSection-div");

      },

      /**
       * Disables Flash uploader if an error is detected.
       * Possibly a temporary workaround for bugs in SWFObject v1.5
       *
       * @method _disableFlashUploader
       * @override
       */
      _disableFlashUploader: function FlashUpload__disableFlashUploader()
      {
         var fileUpload = Alfresco.util.ComponentManager.findFirst("Alfresco.RecordsFileUpload");
         if (fileUpload)
         {
            fileUpload.hasRequiredFlashPlayer = false;
         }
         return fileUpload;
      },

      /**
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       * @override
       */
      _applyConfig: function RecordsFlashUpload__applyConfig()
      {
         Alfresco.RecordsFlashUpload.superclass._applyConfig.call(this);

         // Set the panel title
         if (this.showConfig.mode === this.MODE_SINGLE_IMPORT)
         {
            this.titleText.innerHTML = this.msg("header.singleImport");
         }

         if (this.showConfig.mode === this.MODE_SINGLE_IMPORT)
         {
            // Hide the record type form
            Dom.addClass(this.widgets.recordTypeSection, "hidden");
         }
         else
         {
            // Display the record type form
            Dom.removeClass(this.widgets.recordTypeSection, "hidden");
         }
      },

      /**
       * Starts to upload as many files as specified by noOfUploadsToStart
       * as long as there are files left to upload.
       *
       * @method _uploadFromQueue
       * @param noOfUploadsToStart
       * @private
       * @override
       */
      _uploadFromQueue: function RecordsFlashUpload__uploadFromQueue(noOfUploadsToStart)
      {
         // Generate upload POST url
         var url = Alfresco.constants.PROXY_URI,
               fileParamName;
         if(this.showConfig.mode === this.MODE_SINGLE_IMPORT)
         {
            url += (this.showConfig.importURL) ? this.showConfig.importURL : "api/rma/admin/import";
            fileParamName = "archive";
         }
         else
         {
            url += (this.showConfig.uploadURL) ? this.showConfig.uploadURL : "api/upload";
            fileParamName = "filedata";
         }

         // Flash does not correctly bind to the session cookies during POST
         // so we manually patch the jsessionid directly onto the URL instead
         url += ";jsessionid=" + YAHOO.util.Cookie.get("JSESSIONID");
         
         // Find files to upload
         var startedUploads = 0,
            length = this.dataTable.getRecordSet().getLength(),
            record, flashId, fileInfo, attributes, contentType,
            recordType = this.widgets.recordType.get("selectedMenuItem").value;
         
         for (var i = 0; i < length && startedUploads < noOfUploadsToStart; i++)
         {
            record = this.dataTable.getRecordSet().getRecord(i);
            flashId = record.getData("id");
            fileInfo = this.fileStore[flashId];
            if (fileInfo.state === this.STATE_BROWSING)
            {
               // Upload has NOT been started for this file, start it now
               fileInfo.state = this.STATE_UPLOADING;
               if (this.showConfig.mode === this.MODE_SINGLE_IMPORT)
               {
                  attributes =
                  {
                     destination: this.showConfig.importDestination,
                     username: this.showConfig.username
                  };
               }
               else
               {
                  attributes =
                  {
                     siteId: this.showConfig.siteId,
                     containerId: this.showConfig.containerId,
                     username: this.showConfig.username
                  };
                  if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
                  {
                     attributes.updateNodeRef = this.showConfig.updateNodeRef;
                     attributes.majorVersion = !this.minorVersion.checked;
                     attributes.description = this.description.value;
                  }
                  else
                  {
                     attributes.uploadDirectory = this.showConfig.uploadDirectory;
                     attributes.contentType = fileInfo.contentType.options[fileInfo.contentType.selectedIndex].value;
                     attributes.aspects = recordType;
                     attributes.overwrite = this.showConfig.overwrite;
                     if (this.showConfig.thumbnails)
                     {
                        attributes.thumbnails = this.showConfig.thumbnails;
                     }
                  }
               }
               this.uploader.upload(flashId, url, "POST", attributes, fileParamName);
               startedUploads++;
            }
         }
      }
   });
})();