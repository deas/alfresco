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
 * Drag and Drop Upload component.
 *
 * Pops up a YUI panel and initiates the upload and progress monitoring of
 * the files providing in the calling arguments.
 *
 * A multi file upload scenario could look like:
 *
 * var dndUpload = Alfresco.component.getDNDUploadInstance();
 * var multiUploadConfig =
 * {
 *    files: files,
 *    destination: destination,
 *    siteId: siteId,
 *    containerId: doclibContainerId,
 *    path: docLibUploadPath,
 *    filter: [],
 *    mode: flashUpload.MODE_MULTI_UPLOAD,
 * }
 * dndUpload.show(multiUploadConfig);
 *
 * @namespace Alfresco.module
 * @class Alfresco.DNDUpload
 * @extends Alfresco.component.Base
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Element = YAHOO.util.Element,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * DNDUpload constructor.
    *
    * DNDUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.component.getDNDUploadInstance() instead.
    *
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.component.DNDUpload} The new DNDUpload instance
    * @constructor
    * @private
    */
   Alfresco.DNDUpload = function(htmlId)
   {
      Alfresco.DNDUpload.superclass.constructor.call(this, "Alfresco.DNDUpload", htmlId, ["button", "container", "datatable", "datasource"]);

      this.fileStore = {};
      this.addedFiles = {};
      this.defaultShowConfig =
      {
         files: [],
         siteId: null,
         containerId: null,
         destination: null,
         uploadDirectory: null,
         updateNodeRef: null,
         updateFilename: null,
         updateVersion: "1.0",
         mode: this.MODE_SINGLE_UPLOAD,
         filter: [],
         onFileUploadComplete: null,
         overwrite: false,
         thumbnails: null,
         uploadURL: null,
         username: null,
         suppressRefreshEvent: false
      };
      this.suppliedConfig = {};
      this.showConfig = {};
      this.fileItemTemplates = {};

      // When the DNDUpload instance is first created we should determine the upload method that
      // the client will use. This is done via browser feature detection. If the browser supports
      // the FormData object then this will be used (as it allows data to be streamed without being
      // loaded into memory) otherwise the file will need to be loaded into the browser memory before
      // it can be uploaded to the server. This state can be queried by callers to the instance
      // using the "uploadMethod" attribute.
      if (typeof FormData !== "undefined")
      {
         this.uploadMethod = this.FORMDATA_UPLOAD;
      }
      else
      {
         this.uploadMethod = this.INMEMORY_UPLOAD;
      }

      return this;
   };

   YAHOO.extend(Alfresco.DNDUpload, Alfresco.component.Base,
   {
      /**
       * The flash move will dispatch the contentReady event twice,
       * make sure we only react on it twice.
       *
       * @property contentReady
       * @type boolean
       */
      contentReady: false,

      /**
       * The client supports FormData upload.
       *
       * @property FORMDATA_UPLOAD
       * @type int
       */
      FORMDATA_UPLOAD: 1,

      /**
       * The client requires in-memory upload.
       *
       * @property INMEMORY_UPLOAD
       * @type int
       */
      INMEMORY_UPLOAD: 2,

      /**
       * The method that will be used to perform file upload. This is determined via browser
       * feature detection and is set during singleton instantiation.
       *
       * @property uploadMethod
       * @type int
       */
      uploadMethod: 2,

      /**
       * The user is browsing and adding files to the file list
       *
       * @property STATE_BROWSING
       * @type int
       */
      STATE_BROWSING: 1,

      /**
       * File(s) is being uploaded to the server
       *
       * @property STATE_UPLOADING
       * @type int
       */
      STATE_UPLOADING: 2,

      /**
       * All files are processed and have either failed or been successfully
       * uploaded to the server.
       *
       * @property STATE_FINISHED
       * @type int
       */
      STATE_FINISHED: 3,

      /**
       * File failed to upload.
       *
       * @property STATE_FAILURE
       * @type int
       */
      STATE_FAILURE: 4,

      /**
       * File was successfully STATE_SUCCESS.
       *
       * @property STATE_SUCCESS
       * @type int
       */
      STATE_SUCCESS: 5,

       /**
       * The state of which the uploader currently is, where the flow is.
       * STATE_BROWSING > STATE_UPLOADING > STATE_FINISHED
       *
       * @property state
       * @type int
       */
      state: 1,

      /**
       * Stores references and state for each file that is in the file list.
       * The fileId parameter from the YAHOO.widget.Uploader is used as the key
       * and the value is an object that stores the state and references.
       *
       * @property fileStore
       * @type object Used as a hash table with fileId as key and an object
       *       literal as the value.
       *       The object literal is of the form:
       *       {
       *          contentType: {HTMLElement},        // select, hidden input or null (holds the chosen contentType for the file).
       *          fileButton: {YAHOO.widget.Button}, // Will be disabled on success or STATE_FAILURE
       *          state: {int},                      // Keeps track if the individual file has been successfully uploaded or failed
       *                                             // (state flow: STATE_BROWSING > STATE_UPLOADING > STATE_SUCCESS or STATE_FAILURE)
       *          progress: {HTMLElement},           // span that is the "progress bar" which is moved during progress
       *          progressInfo: {HTMLElement},       // span that displays the filename and the state
       *          progressPercentage: {HTMLElement}, // span that displays the upload percentage for the individual file
       *          fileName: {string},                // filename
       *          nodeRef: {string}                  // nodeRef if the file has been uploaded successfully
       *       }
       */
      fileStore: null,

      /**
       * The number of successful uploads since upload was clicked.
       *
       * @property noOfSuccessfulUploads
       * @type int
       */
      noOfSuccessfulUploads: 0,

      /**
       * The number of failed uploads since upload was clicked.
       *
       * @property noOfFailedUploads
       * @type int
       */
      noOfFailedUploads: 0,

      /**
       * The expected volume of data to be uploaded by all selected files.
       *
       * @property aggregateUploadTargetSize
       * @type int
       */
      aggregateUploadTargetSize: 0,

      /**
       * The current volume of data uploaded in the current operation.
       *
       * @property aggregateUploadCurrentSize
       * @type int
       */
      aggregateUploadCurrentSize: 0,

      /**
       * Remembers what files that how been added to the file list since
       * the show method was called.
       *
       * @property addedFiles
       * @type object
       */
      addedFiles: null,

      /**
       * Shows uploader in single upload mode.
       *
       * @property MODE_SINGLE_UPLOAD
       * @static
       * @type int
       */
      MODE_SINGLE_UPLOAD: 1,

      /**
       * Shows uploader in single update mode.
       *
       * @property MODE_SINGLE_UPDATE
       * @static
       * @type int
       */
      MODE_SINGLE_UPDATE: 2,

      /**
       * Shows uploader in multi upload mode.
       *
       * @property MODE_MULTI_UPLOAD
       * @static
       * @type int
       */
      MODE_MULTI_UPLOAD: 3,

      /**
       * The default config for the gui state for the uploader.
       * The user can override these properties in the show() method to use the
       * uploader for both single & multi uploads and single updates.
       *
       * @property defaultShowConfig
       * @type object
       */
      defaultShowConfig: null,

      /**
       * The config passed in to the show method.
       *
       * @property suppliedConfig
       * @type object
       */
      suppliedConfig: null,

      /**
       * The merged result of the defaultShowConfig and the config passed in
       * to the show method.
       *
       * @property showConfig
       * @type object
       */
      showConfig: null,

      /**
       * Contains the upload gui
       *
       * @property panel
       * @type YAHOO.widget.Panel
       */
      panel: null,

      /**
       * Used to display the user selceted files and keep track of what files
       * that are selected and should be STATE_FINISHED.
       *
       * @property uploader
       * @type YAHOO.widget.DataTable
       */
      dataTable: null,

      /**
       * HTMLElement of type span that displays the dialog title.
       *
       * @property titleText
       * @type HTMLElement
       */
      titleText: null,

      /**
       * HTMLElement of type span that displays the total upload status
       *
       * @property statusText
       * @type HTMLElement
       */
      statusText: null,

      /**
       * HTMLElement of type span that displays the aggregate upload progress
       *
       * @property aggregateProgressText
       * @type HTMLElement
       */
      aggregateProgressText: null,

      /**
       * HTMLElement of type div that displays the aggregate upload data that is
       * is shown when uploads are in progress.
       *
       * @property aggregateDataWrapper
       * @type HTMLElement
       */
      aggregateDataWrapper: null,

      /**
       * HTMLElement of type radio button for major or minor version
       *
       * @property description
       * @type HTMLElement
       */
      minorVersion: null,

      /**
       * HTMLElement of type textarea for version comment
       *
       * @property description
       * @type HTMLElement
       */
      description: null,

      /**
       * HTMLElement of type div that displays the version input form.
       *
       * @property versionSection
       * @type HTMLElement
       */
      versionSection: null,

      /**
       * HTMLElements of type div that is used to to display a column in a
       * row in the file table list. It is loaded dynamically from the server
       * and then cloned for each row and column in the file list.
       * The fileItemTemplates has the following form:
       * {
       *    left:   HTMLElement to display the left column
       *    center: HTMLElement to display the center column
       *    right:  HTMLElement to display the right column
       * }
       *
       * @property fileItemTemplates
       * @type HTMLElement
       */
      fileItemTemplates: null,

      /**
       * The maximum size of the sum of file sizes that be uploaded in a single operation when
       * operating in INMEMORY_UPLOAD mode.
       *
       * @property _inMemoryLimit
       * @private
       * @type int
       */
      _inMemoryLimit: 250000000,

      /**
       * Sets maximum size of the sum of file sizes that be uploaded in a single operation. This
       * limit only affects browsers operating in INMEMORY_UPLOAD mode.
       *
       * @method setInMemoryLimit
       * @param limit
       */
      setInMemoryLimit: function DNDUpload_setInMemoryLimit(limit)
      {
         if (isNaN(limit))
         {
            // If the user has overridden the default value and provided a non-numerical value
            // then we'll just leave the limit as the default.
            Alfresco.logger.warn("Non-numerical value set for \"in-memory-limit\" in share-documentlibrary.xml: ", limit);
            this._inMemoryLimit = 25000000;
         }
         else
         {
            this._inMemoryLimit = limit;
         }
      },

      /**
       * Returns the maximum size of the sum of file sizes that be uploaded in a single operation. This
       * limit only affects browsers operating in INMEMORY_UPLOAD mode.
       *
       * @method setInMemoryLimit
       * @param limit
       */
      getInMemoryLimit: function DNDUpload_getInMemoryLimit()
      {
         return this._inMemoryLimit;
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function DNDUpload_onReady()
      {
         Dom.removeClass(this.id + "-dialog", "hidden");

         // Create the panel
         this.panel = Alfresco.util.createYUIPanel(this.id + "-dialog");

         // Hook close button
         this.panel.hideEvent.subscribe(this.onCancelOkButtonClick, null, this);

         // Save a reference to the file row template that is hidden inside the markup
         this.fileItemTemplates.left = Dom.get(this.id + "-left-div");
         this.fileItemTemplates.center = Dom.get(this.id + "-center-div");
         this.fileItemTemplates.right = Dom.get(this.id + "-right-div");

         // Create the YIU datatable object
         this._createEmptyDataTable();

         // Save a reference to the HTMLElement displaying texts so we can alter the texts later
         this.titleText = Dom.get(this.id + "-title-span");
         this.statusText = Dom.get(this.id + "-status-span");
         this.aggregateProgressText = Dom.get(this.id + "-aggregate-status-span");
         this.aggregateDataWrapper = Dom.get(this.id + "-aggregate-data-wrapper");
         this.description = Dom.get(this.id + "-description-textarea");

         // Save reference to version radio so we can reset and get its value later
         this.minorVersion = Dom.get(this.id + "-minorVersion-radioButton");

         // Save a reference to the HTMLElement displaying version input so we can hide or show it
         this.versionSection = Dom.get(this.id + "-versionSection-div");

         // Create and save a reference to the cancelOkButton so we can alter it later
         this.widgets.cancelOkButton = Alfresco.util.createYUIButton(this, "cancelOk-button", this.onCancelOkButtonClick);

         // Register the ESC key to close the dialog
         this.widgets.escapeListener = new KeyListener(document,
         {
            keys: KeyListener.KEY.ESCAPE
         },
         {
            fn: this.onCancelOkButtonClick,
            scope: this,
            correctScope: true
         });
      },

      /**
       * Show can be called multiple times and will display the uploader dialog
       * in different ways depending on the config parameter.
       *
       * @method show
       * @param config {object} describes how the upload dialog should be displayed
       * The config object is in the form of:
       * {
       *    siteId: {string},        // site to upload file(s) to
       *    containerId: {string},   // container to upload file(s) to (i.e. a doclib id)
       *    destination: {string},   // destination nodeRef to upload to if not using site & container
       *    uploadPath: {string},    // directory path inside the component to where the uploaded file(s) should be save
       *    updateNodeRef: {string}, // nodeRef to the document that should be updated
       *    updateFilename: {string},// The name of the file that should be updated, used to display the tip
       *    mode: {int},             // MODE_SINGLE_UPLOAD, MODE_MULTI_UPLOAD or MODE_SINGLE_UPDATE
       *    filter: {array},         // limits what kind of files the user can select in the OS file selector
       *    onFileUploadComplete: null, // Callback after upload
       *    overwrite: false         // If true and in mode MODE_XXX_UPLOAD it tells
       *                             // the backend to overwrite a versionable file with the existing name
       *                             // If false and in mode MODE_XXX_UPLOAD it tells
       *                             // the backend to append a number to the versionable filename to avoid
       *                             // an overwrite and a new version
       * }
       */
      show: function DNDUpload_show(config)
      {
         // Create an alias for this (it is required for the listener functions declared later)
         var _this = this;

         // Merge the supplied config with default config and check mandatory properties
         this.suppliedConfig = config;
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);
         if (!this.showConfig.uploadDirectory && !this.showConfig.updateNodeRef && !this.showConfig.destination)
         {
             throw new Error("An updateNodeRef, uploadDirectory or destination must be provided");
         }
         if (this.showConfig.files == undefined)
         {
             throw new Error("An array of files to upload must be provided");
         }

         if (this.showConfig.uploadDirectory !== null && this.showConfig.uploadDirectory.length === 0)
         {
            this.showConfig.uploadDirectory = "/";
         }

         // Apply the config before it is shown
         this._resetGUI();

         // Apply the config before it is shown
         this._applyConfig();

         var displayDialog = false,
            data,
            uniqueFileToken;

         for (var i = 0; i < this.showConfig.files.length; i++)
         {
            try
            {
               /**
                * UPLOAD PROGRESS LISTENER
                */
               var progressListener = function DNDUpload_progressListener(e)
               {
                 Alfresco.logger.debug("File upload progress update received", e);
                 if (e.lengthComputable)
                 {
                     try
                     {
                        var percentage = Math.round((e.loaded * 100) / e.total),
                           fileId = e.target._fileData,
                           fileInfo = _this.fileStore[fileId];

                        fileInfo.progressPercentage.innerHTML = percentage + "%";

                        // Set progress position
                        var left = (-400 + ((percentage/100) * 400));
                        Dom.setStyle(fileInfo.progress, "left", left + "px");
                        _this._updateAggregateProgress(fileInfo, e.loaded);

                        // Save value of how much has been loaded for the next iteration
                        fileInfo.lastProgress = e.loaded;
                     }
                     catch(exception)
                     {
                        Alfresco.logger.error("The following error occurred processing an upload progress event: ", exception);
                     }

                 }
                 else
                 {
                     Alfresco.logger.debug("File upload progress not computable", e);
                 }
              };

              /**
               * UPLOAD COMPLETION LISTENER
               */
              var successListener = function DNDUpload_successListener(e)
              {
                 try
                 {
                    Alfresco.logger.debug("File upload completion notification received", e);

                    // The individual file has been transfered completely
                    // Now adjust the gui for the individual file row
                    var fileId = e.target._fileData,
                     fileInfo = _this.fileStore[fileId];

                    if (fileInfo.request.readyState != 4)
                    {
                       // There is an occasional timing issue where the upload completion event fires before
                       // the readyState is correctly updated. This means that we can't check the upload actually
                       // completed successfully, if this occurs then we'll attach a function to the onreadystatechange
                       // extension point and things to catch up before we check everything was ok...
                       fileInfo.request.onreadystatechange = function DNDUpload_onreadystatechange()
                       {
                          if (fileInfo.request.readyState == 4)
                          {
                             _this._processUploadCompletion(fileInfo);
                          }
                       }
                    }
                    else
                    {
                       // If the request correctly indicates that the response has returned then we can process
                       // it to ensure that files have been uploaded correctly.
                       _this._processUploadCompletion(fileInfo);
                    }
                 }
                 catch(exception)
                 {
                    Alfresco.logger.error("The following error occurred processing an upload completion event: ", exception);
                 }
               };

               /**
                * UPLOAD FAILURE LISTENER
                */
               var failureListener = function DNDUpload_failureListener(e)
               {
                  try
                  {
                     var fileId = e.target._fileData,
                        fileInfo = _this.fileStore[fileId];

                     // This sometimes gets called twice, make sure we only adjust the gui once
                     if (fileInfo.state !== _this.STATE_FAILURE)
                     {
                        _this._processUploadFailure(fileInfo, event.status);
                     }
                  }
                  catch(exception)
                  {
                     Alfresco.logger.error("The following error occurred processing an upload failure event: ", exception);
                  }
               };

               // Check if the file has size...
               if (this.showConfig.files[i].size === 0)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.zeroByteFileSelected", this.showConfig.files[i].fileName)
                  });
               }
               else if (!Alfresco.forms.validation.nodeName({ id: 'file', value: this.showConfig.files[i].fileName }, null, null, null, true))
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.illegalCharacters")
                  });
               }
               else
               {
                  // Add the event listener functions to the upload properties of the XMLHttpRequest object...
                  var request = new XMLHttpRequest();
                  request.upload.addEventListener("progress", progressListener, false);
                  request.upload.addEventListener("load", successListener, false);
                  request.upload.addEventListener("error", failureListener, false);

                  var fileId = "file" + i;

                  // Add the data to the upload property of XMLHttpRequest so that we can determine which file each
                  // progress update relates to (the event argument passed in the progress function does not contain
                  // file name details)...
                  request.upload._fileData = fileId;

                  // Construct the data that will be passed to the YUI DataTable to add a row...
                  var fileName = this.showConfig.files[i].fileName;
                  data = {
                      id: fileId,
                      name: fileName,
                      size: this.showConfig.files[i].size
                  };

                  // Construct an object containing the data required for file upload...
                  var uploadData =
                  {
                     filedata: this.showConfig.files[i],
                     filename: this.showConfig.files[i].fileName,
                     destination: this.showConfig.destination,
                     siteId: this.showConfig.siteId,
                     containerId: this.showConfig.containerId,
                     uploaddirectory: this.showConfig.uploadDirectory
                  };

                  // Add the upload data to the file store. It is important that we don't initiate the XMLHttpRequest
                  // send operation before the YUI DataTable has finished rendering because if the file being uploaded
                  // is small and the network is quick we could receive the progress/completion events before we're
                  // ready to handle them.
                  this.fileStore[fileId] =
                  {
                     state: this.STATE_UPLOADING,
                     fileName: fileName,
                     nodeRef: null,
                     uploadData: uploadData,
                     request: request
                  };

                  // Add file to file table
                  this.dataTable.addRow(data);
                  this.addedFiles[uniqueFileToken] = this._getUniqueFileToken(data);
                  displayDialog = true;
               }

               if (displayDialog)
               {
                  // Enable the Esc key listener
                  this.widgets.escapeListener.enable();
                  this.panel.setFirstLastFocusable();
                  this.panel.show();
               }
            }
            catch(exception)
            {
               Alfresco.logger.error("DNDUpload_show: The following exception occurred processing a file to upload: ", exception);
            }
         }
      },

      /**
       * Called from show when an upload complete event fires.
       *
       * @param fileInfo {object} An entry from the fileStore array contains the information about the file that has uploaded.
       * @method _processUploadCompletion
       * @private
       */
      _processUploadCompletion: function DND__processUploadCompletion(fileInfo)
      {
         if (fileInfo.request.status == "200")
         {
            var response = Alfresco.util.parseJSON(fileInfo.request.responseText);

            // update noderef and filename from response
            fileInfo.nodeRef = response.nodeRef;
            fileInfo.fileName = response.fileName;
            fileInfo.state = this.STATE_SUCCESS;

            // Add the label "Successful" after the filename, updating the fileName from the response
            Dom.addClass(fileInfo.progressStatusIncomplete, "hidden");
            Dom.removeClass(fileInfo.progressStatusComplete, "hidden");

            // Change the style of the progress bar
            Dom.removeClass(fileInfo.progress, "fileupload-progressSuccess-span");
            Dom.addClass(fileInfo.progress, "fileupload-progressFinished-span");

            // Move the progress bar to "full" progress
            Dom.setStyle(fileInfo.progress, "left", 0 + "px");
            fileInfo.progressPercentage.innerHTML = "100%";
            this.noOfSuccessfulUploads++;
            this._updateAggregateProgress(fileInfo, fileInfo.uploadData.filedata.size);

            // Adjust the rest of the gui
            this._updateStatus();
            this._adjustGuiIfFinished();
         }
         else
         {
            // Process the upload failure...
            this._processUploadFailure(fileInfo, fileInfo.request.status);
         }
      },

      /**
       * Called from show if an XMLHttpRequest.send() operation fails or completes but returns an HTTP status code of anything
       * other than 200.
       *
       * @param fileInfo {object} An entry from the fileStore array contains the information about the file that has failed to upload.
       * @method _processUploadFailure
       * @private
       */
      _processUploadFailure: function DND__processUploadFailure(fileInfo, status)
      {
         fileInfo.state = this.STATE_FAILURE;

         // Add the failure label to the filename & and as a title attribute
         var key = "label.failure." + status,
             msg = Alfresco.util.message(key, this.name);

         if (msg == key)
         {
            msg = Alfresco.util.message("label.failure", this.name);
         }
         fileInfo.fileSizeInfo["innerHTML"] = fileInfo.fileSizeInfo["innerHTML"] + " (" + msg + ")";
         fileInfo.progressInfoCell.setAttribute("title", msg);

         // Change the style of the progress bar
         Dom.removeClass(fileInfo.progress, "fileupload-progressSuccess-span");
         Dom.addClass(fileInfo.progress, "fileupload-progressFailure-span");

         // Set the progress bar to "full" progress
         Dom.setStyle(fileInfo.progress, "left", 0 + "px");
         this._updateAggregateProgress(fileInfo, fileInfo.uploadData.filedata.size);

         // Adjust the rest of the gui
         this.noOfFailedUploads++;
         this._updateStatus();
         this._adjustGuiIfFinished();
      },

      /**
       * Called whenever file upload progress notification is received. This calculates the overall
       * upload performed and sets the progress span accordingly.
       *
       * @method _updateAggregateProgress
       * @private
       */
      _updateAggregateProgress: function DNDUpload__updateAggregateProgress(fileInfo, loaded)
      {
         // Deduct the last loaded about from the overall loaded value, then add the full
         // file size. This can then be used to calculate the overall progress and set the
         // style of the progress bar...
         this.aggregateUploadCurrentSize -= fileInfo.lastProgress;
         this.aggregateUploadCurrentSize += loaded;
         var overallProgress = (this.aggregateUploadCurrentSize / this.aggregateUploadTargetSize);
         var overallLeft = (-620 + (overallProgress * 620));
         Dom.setStyle(this.id + "-aggregate-progress-span", "left", overallLeft + "px");
      },

      /**
       * Reset GUI to start state
       *
       * @method _resetGUI
       * @private
       */
      _resetGUI: function DNDUpload__resetGUI()
      {
         // Reset references and the gui before showing it
         this.state = this.STATE_UPLOADING; // We're going to start uploading as soon as the dialog is shown
         this.noOfFailedUploads = 0;
         this.noOfSuccessfulUploads = 0;
         this.statusText.innerHTML = "&nbsp;";
         this.description.value = "";
         this.minorVersion.checked = true;
         this.widgets.cancelOkButton.set("label", this.msg("button.cancel"));
         this.widgets.cancelOkButton.set("disabled", false);
         this.aggregateUploadTargetSize = 0;
         this.aggregateUploadCurrentSize = 0;
         Dom.setStyle(this.id + "-aggregate-progress-span", "left", "-620px");
         Dom.removeClass(this.aggregateDataWrapper, "hidden");
      },

      /**
       * Fired by YUI:s DataTable when the added row has been rendered to the data table list.
       *
       * @method onPostRenderEvent
       */
      onPostRenderEvent: function DNDUpload_onPostRenderEvent(e)
      {
         // Display the upload button since all files are rendered
         if (this.dataTable.getRecordSet().getLength() > 0)
         {
            this.panel.setFirstLastFocusable();
            this.panel.focusFirst();
         }
      },

      /**
       * Fired by YUI:s DataTable when a row has been added to the data table list.
       * This retrieves the previously stored file information (which includes
       * prepared XMLHttpRequest and FormData objects) and initiates the file
       * upload.
       *
       * @method onRowAddEvent
       * @param event {object} a DataTable "rowAdd" event
       */
      onRowAddEvent: function FlashUpload_onRowAddEvent(event)
      {
         try
         {
            var url = Alfresco.constants.PROXY_URI + "api/upload";
            var data = event.record.getData();
            var fileInfo = this.fileStore[data.id];
            this.aggregateUploadTargetSize += fileInfo.uploadData.filedata.size;

            // Initialise the lastProgress attribute, this will be updated each time a progress
            // event is processed and will be used to calculate the overall progress of *all* uploads...
            fileInfo.lastProgress = 0;

            if (this.uploadMethod === this.FORMDATA_UPLOAD)
            {
               // For Browsers that support it (currently FireFox 4), the FormData object is the best
               // object to use for file upload as it supports asynchronous multipart upload without
               // the need to read the entire object into memory.
               Alfresco.logger.debug("Using FormData for file upload");
               var formData = new FormData;
               formData.append("filedata", fileInfo.uploadData.filedata);
               formData.append("filename", fileInfo.uploadData.filename);
               formData.append("destination", fileInfo.uploadData.destination);
               formData.append("siteId", fileInfo.uploadData.siteId);
               formData.append("containerId", fileInfo.uploadData.containerId);
               formData.append("uploaddirectory", fileInfo.uploadData.uploaddirectory);
               fileInfo.request.open("POST",  url, true);
               fileInfo.request.send(formData);
            }
            else if (this.uploadMethod === this.INMEMORY_UPLOAD)
            {
               Alfresco.logger.debug("Using custom multipart upload");

               // PLEASE NOTE: Be *VERY* careful modifying the following code, this carefully constructs a multipart formatted request...
               var multipartBoundary = "----AlfrescoCustomMultipartBoundary" + (new Date).getTime();
               var rn = "\r\n";
               var customFormData = "--" + multipartBoundary;

               // Add the file parameter...
               customFormData += rn + "Content-Disposition: form-data; name=\"filedata\"; filename=\"" + unescape(encodeURIComponent(fileInfo.uploadData.filename)) + "\"";
               customFormData += rn + "Content-Type: image/png";
               customFormData += rn + rn + fileInfo.uploadData.filedata.getAsBinary() + rn + "--" + multipartBoundary;

               // Add the String parameters...
               customFormData += rn + "Content-Disposition: form-data; name=\"filename\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.filename)) + rn + "--" + multipartBoundary;
               customFormData += rn + "Content-Disposition: form-data; name=\"destination\"";
               if (fileInfo.uploadData.destination !== null)
               {
                  customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.destination)) + rn + "--" + multipartBoundary;
               }
               else
               {
                  customFormData += rn + rn + rn + "--" + multipartBoundary;
               }
               customFormData += rn + "Content-Disposition: form-data; name=\"siteId\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.siteId)) + rn + "--" + multipartBoundary;
               customFormData += rn + "Content-Disposition: form-data; name=\"containerId\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.containerId)) + rn + "--" + multipartBoundary;
               customFormData += rn + "Content-Disposition: form-data; name=\"uploaddirectory\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.uploaddirectory)) + rn + "--" + multipartBoundary + "--";

               fileInfo.request.open("POST",  url, true);
               fileInfo.request.setRequestHeader("Content-Type", "multipart/form-data; boundary=" + multipartBoundary);
               fileInfo.request.sendAsBinary(customFormData);
            }

            this._updateAggregateStatus();
         }
         catch(exception)
         {
            Alfresco.logger.error("The following error occurred initiating upload: " + exception);
         }
      },

      /**
       * Fired when the user clicks the cancel/ok button.
       * The action taken depends on what state the uploader is in.
       * In STATE_UPLOADING - Cancels current uploads,
       *                      informs the user about how many that were uploaded,
       *                      tells the documentlist to update itself
       *                      and closes the panel.
       * In STATE_FINISHED  - Tells the documentlist to update itself
       *                      and closes the panel.
       *
       * @method onBrowseButtonClick
       * @param event {object} a Button "click" event
       */
      onCancelOkButtonClick: function DNDUpload_onCancelOkButtonClick()
      {
         var message, i;
         if (this.state === this.STATE_UPLOADING)
         {
            this._cancelAllUploads();

            // Inform the user if any files were uploaded before the rest was cancelled
            var noOfUploadedFiles = 0;
            for (i in this.fileStore)
            {
               if (this.fileStore[i] && this.fileStore[i].state === this.STATE_SUCCESS)
               {
                  noOfUploadedFiles++;
               }
            }
            if (noOfUploadedFiles > 0)
            {
               message = YAHOO.lang.substitute(this.msg("message.cancelStatus"),
               {
                  "0": noOfUploadedFiles
               });
            }

            if (!this.showConfig.suppressRefreshEvent)
            {
               // Tell the document list to refresh itself if present
               YAHOO.Bubbling.fire("metadataRefresh",
               {
                  currentPath: this.showConfig.path
               });
            }
         }
         else if (this.state === this.STATE_FINISHED)
         {
            // Tell the document list to refresh itself if present and to
            // highlight the uploaded file (if multi upload was used display the first file)
            var fileName = null, f;
            for (i in this.fileStore)
            {
               f = this.fileStore[i];
               if (f && f.state === this.STATE_SUCCESS)
               {
                  fileName = f.fileName;
                  break;
               }
            }
            if (!this.showConfig.suppressRefreshEvent)
            {
               if (fileName)
               {
                  YAHOO.Bubbling.fire("metadataRefresh",
                  {
                     currentPath: this.showConfig.path,
                     highlightFile: fileName
                  });
               }
               else
               {
                  YAHOO.Bubbling.fire("metadataRefresh",
                  {
                     currentPath: this.showConfig.path
                  });
               }
            }
         }

         // Remove all files and references for this upload "session"
         this._clear();

         // Hide the panel
         this.panel.hide();

         // Disable the Esc key listener
         this.widgets.escapeListener.disable();

         // Inform the user if any files were uploaded before the rest was cancelled
         if (message)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: message
            });
         }
      },

      /**
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       */
      _applyConfig: function DNDUpload__applyConfig()
      {
         // Generate the title based on number of files and destination
         var title = "",
            i18n = this.showConfig.files.length == 1 ? "header.singleUpload" : "header.multiUpload",
            location = this.showConfig.uploadDirectoryName == "/" ? this.msg("label.documents") : this.showConfig.uploadDirectoryName;

         this.titleText.innerHTML = this.msg(i18n, '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/folder-open-16.png" class="title-folder" />' + $html(location));

         if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            // Display the version input form
            Dom.removeClass(this.versionSection, "hidden");
            var versions = (this.showConfig.updateVersion || "1.0").split("."),
               majorVersion = parseInt(versions[0], 10),
               minorVersion = parseInt(versions[1], 10);


            Dom.get(this.id + "-minorVersion").innerHTML = this.msg("label.minorVersion.more", majorVersion + "." + (1 + minorVersion));
            Dom.get(this.id + "-majorVersion").innerHTML = this.msg("label.majorVersion.more", (1 + majorVersion) + ".0");
         }
         else
         {
            // Hide the version input form
            Dom.addClass(this.versionSection, "hidden");
         }

         if (this.showConfig.mode === this.MODE_MULTI_UPLOAD)
         {
            // Show the upload status label, only interesting for multiple files
            Dom.removeClass(this.statusText, "hidden");

            // Make the file list long
            this.dataTable.set("height", "204px", true);
         }
         else
         {
            // Hide the upload status label, only interesting for multiple files
            Dom.addClass(this.statusText, "hidden");

            // Make the file list short
            this.dataTable.set("height", "40px");
         }
      },

      /**
       * Helper function to create the data table and its cell formatter.
       *
       * @method _createEmptyDataTable
       * @private
       */
      _createEmptyDataTable: function DNDUpload__createEmptyDataTable()
      {
         /**
          * Save a reference of 'this' so that the formatter below can use it
          * later (since the formatter method gets called with another scope
          * than 'this').
          */
         var myThis = this;

         /**
          * Responsible for rendering the left row in the data table
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          */
         var formatLeftCell = function(el, oRecord, oColumn, oData)
         {
            try
            {
               myThis._formatCellElements(el, oRecord, myThis.fileItemTemplates.left);
            }
            catch(exception)
            {
               Alfresco.logger.error("DNDUpload__createEmptyDataTable (formatLeftCell): The following error occurred: ", exception);
            }
         };

         /**
          * Responsible for rendering the center row in the data table
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          */
         var formatCenterCell = function(el, oRecord, oColumn, oData)
         {
            try
            {
               myThis._formatCellElements(el, oRecord, myThis.fileItemTemplates.center);
            }
            catch(exception)
            {
               Alfresco.logger.error("DNDUpload__createEmptyDataTable (formatCenterCell): The following error occurred: ", exception);
            }
         };

         /**
          * Responsible for rendering the right row in the data table
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          */
         var formatRightCell = function(el, oRecord, oColumn, oData)
         {
            try
            {
               myThis._formatCellElements(el, oRecord, myThis.fileItemTemplates.right);
            }
            catch(exception)
            {
               Alfresco.logger.error("DNDUpload__createEmptyDataTable (formatRightCell): The following error occurred: ", exception);
            }
         };

         /**
          * Takes a left, center or right column template and looks for expected
          * html components and vcreates yui objects or saves references to
          * them so they can be updated during the upload progress.
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          * @param template the template to display in the column
          */
         this._formatCellElements = function(el, oRecord, template)
         {
            var record = oRecord.getData(),
                fileId = record.id;

            // create an instance from the template and give it a uniqueue id.
            var cell = new Element(el);
            var templateInstance = template.cloneNode(true);
            templateInstance.setAttribute("id", templateInstance.getAttribute("id") + fileId);

            // Save references to elements that will be updated during upload.
            var progress = Dom.getElementsByClassName("fileupload-progressSuccess-span", "span", templateInstance);
            if (progress.length == 1)
            {
               this.fileStore[fileId].progress = progress[0];
            }
            var progressInfo = Dom.getElementsByClassName("fileupload-progressInfo-span", "span", templateInstance);
            if (progressInfo.length == 1)
            {
               // Display the file size in human readable format after the filename.
               var fileInfoStr = record.name;// + " (" + Alfresco.util.formatFileSize(record.size) + ")";
               templateInstance.setAttribute("title", fileInfoStr);

               // Display the file name and size.
               progressInfo = progressInfo[0];
               this.fileStore[fileId].progressInfo = progressInfo;
               this.fileStore[fileId].progressInfo.innerHTML = fileInfoStr;

               // Save the cell element
               this.fileStore[fileId].progressInfoCell = el;
            }

            var fileSize = Dom.getElementsByClassName("fileupload-filesize-span", "span", templateInstance);
            if (fileSize.length == 1)
            {
               // Display the file size in human readable format below the filename.
               var fileInfoStr = Alfresco.util.formatFileSize(record.size);
               fileSize = fileSize[0];
               this.fileStore[fileId].fileSizeInfo = fileSize;
               fileSize.innerHTML = fileInfoStr;
            }

            // Save a reference to the contentType dropdown so we can find each file's contentType before upload.
            var contentType = Dom.getElementsByClassName("fileupload-contentType-select", "select", templateInstance);
            if (contentType.length == 1)
            {
               this.fileStore[fileId].contentType = contentType[0];
            }
            else
            {
               contentType = Dom.getElementsByClassName("fileupload-contentType-input", "input", templateInstance);
               if (contentType.length == 1)
               {
                  this.fileStore[fileId].contentType = contentType[0];
               }
            }

            // Save references to elements that will be updated during upload.
            var progressPercentage = Dom.getElementsByClassName("fileupload-percentage-span", "span", templateInstance);
            if (progressPercentage.length == 1)
            {
               this.fileStore[fileId].progressPercentage = progressPercentage[0];
            }

            var progressStatus = Dom.getElementsByClassName("fileupload-status-img", "img", templateInstance);
            if (progressStatus.length == 2)
            {
               this.fileStore[fileId].progressStatusIncomplete = progressStatus[0];
               this.fileStore[fileId].progressStatusComplete = progressStatus[1];
            }

            // Insert the templateInstance to the column.
            cell.appendChild(templateInstance);
         };

         // Definition of the data table column
         var myColumnDefs = [
            { key: "id", className:"col-left", resizable: false, formatter: formatLeftCell },
            { key: "name", className:"col-center", resizable: false, formatter: formatCenterCell },
            { key: "created", className:"col-right", resizable: false, formatter: formatRightCell }
         ];

         // The data tables underlying data source.
         var myDataSource = new YAHOO.util.DataSource([],
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         });

         /**
          * Create the data table.
          * Set the properties even if they will get changed in applyConfig
          * afterwards, if not set here they will not be changed later.
          */
         YAHOO.widget.DataTable._bStylesheetFallback = !!YAHOO.env.ua.ie;
         var dataTableDiv = Dom.get(this.id + "-filelist-table");
         this.dataTable = new YAHOO.widget.DataTable(dataTableDiv, myColumnDefs, myDataSource,
         {
            scrollable: true,
            height: "100px", // must be set to something so it can be changed afterwards, when the showconfig options decides if its a sinlge or multi upload
            width: "620px",
            renderLoopSize: 1,
            MSG_EMPTY: this.msg("label.noFiles")
         });
         this.dataTable.subscribe("postRenderEvent", this.onPostRenderEvent, this, true);
         this.dataTable.subscribe("rowAddEvent", this.onRowAddEvent, this, true);
      },

      /**
       * Helper function to create a unique file token from the file data object
       *
       * @method _getUniqueFileToken
       * @param data {object} a file data object describing a file
       * @private
       */
      _getUniqueFileToken: function DNDUpload__getUniqueFileToken(data)
      {
         return data.name + ":" + data.size;
      },

      /**
       * Update the status label with the latest information about the upload progress
       *
       * @method _updateStatus
       * @private
       */
      _updateStatus: function DNDUpload__updateStatus()
      {
         if (this.noOfFailedUploads > 0)
         {
            this.statusText.innerHTML = YAHOO.lang.substitute(this.msg("label.uploadStatus.withFailures"),
            {
               "0" : this.noOfSuccessfulUploads,
               "1" : this.dataTable.getRecordSet().getLength(),
               "2" : this.noOfFailedUploads
            });
         }
         else
         {
            this.statusText.innerHTML = YAHOO.lang.substitute(this.msg("label.uploadStatus"),
            {
               "0" : this.noOfSuccessfulUploads,
               "1" : this.dataTable.getRecordSet().getLength()
            });
         }
      },

      /**
       * Update the aggregate status label with the latest information about the overall
       * upload progress
       *
       * @method _updateAggregateStatus
       * @private
       */
      _updateAggregateStatus: function DNDUpload__updateAggregateStatus()
      {
         this.aggregateProgressText.innerHTML = YAHOO.lang.substitute(this.msg("label.aggregateUploadStatus"),
         {
            "0" : this.dataTable.getRecordSet().getLength(),
            "1" : Alfresco.util.formatFileSize(this.aggregateUploadTargetSize)
         });
      },

      /**
       * Checks if all files are finished (successfully uploaded or failed)
       * and if so adjusts the gui.
       *
       * @method _adjustGuiIfFinished
       * @private
       */
      _adjustGuiIfFinished: function DNDUpload__adjustGuiIfFinished()
      {
         try
         {
            var objComplete =
            {
               successful: [],
               failed: []
            };
            var file = null;

            // Go into finished state if all files are finished: successful or failures
            for (var i in this.fileStore)
            {
               file = this.fileStore[i];
               if (file)
               {
                  if (file.state == this.STATE_SUCCESS)
                  {
                     // Push successful file
                     objComplete.successful.push(
                     {
                        fileName: file.fileName,
                        nodeRef: file.nodeRef
                     });
                  }
                  else if (file.state == this.STATE_FAILURE)
                  {
                     // Push failed file
                     objComplete.failed.push(
                     {
                        fileName: file.fileName
                     });
                  }
                  else
                  {
                     return;
                  }
               }
            }
            this.state = this.STATE_FINISHED;
            Dom.addClass(this.aggregateDataWrapper, "hidden");
            this.widgets.cancelOkButton.set("label", this.msg("button.ok"));
            this.widgets.cancelOkButton.focus();

            var callback = this.showConfig.onFileUploadComplete;
            if (callback && typeof callback.fn == "function")
            {
               // Call the onFileUploadComplete callback in the correct scope
               callback.fn.call((typeof callback.scope == "object" ? callback.scope : this), objComplete, callback.obj);
            }
         }
         catch(exception)
         {
            Alfresco.logger.error("_adjustGuiIfFinished", exception);
         }
      },

      /**
       * Cancels all uploads inside the flash movie.
       *
       * @method _cancelAllUploads
       * @private
       */
      _cancelAllUploads: function DNDUpload__cancelAllUploads()
      {
         // Cancel all uploads inside the flash movie
         var length = this.dataTable.getRecordSet().getLength();
         for (var i = 0; i < length; i++)
         {
            var record = this.dataTable.getRecordSet().getRecord(i);
            var fileId = record.getData("id");

            var fileInfo = this.fileStore[fileId];
            if (fileInfo.state === this.STATE_UPLOADING)
            {
                // We will only attempt an upload abort if the file is still being uploaded (there is
                // no point in aborting if the file has completed or failed)
                Alfresco.logger.debug("Aborting upload of file: " + fileInfo.fileName);
                fileInfo.request.abort();
            }
         }
      },

      /**
       * Remove all references to files inside the data table, flash movie
       * and the this class references.
        *
       * @method _clear
       * @private
       */
      _clear: function DNDUpload__clear()
      {
         /**
          * Remove all references to files inside the data table, flash movie
          * and this class's references.
          */
         var length = this.dataTable.getRecordSet().getLength();
         this.addedFiles = {};
         this.fileStore = {};
         this.dataTable.deleteRows(0, length);
      }
   });
})();
