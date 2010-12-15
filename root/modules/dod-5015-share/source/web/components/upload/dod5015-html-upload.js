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
 * RecordsHtmlUpload component.
 *
 * Popups a YUI panel and displays a filelist and buttons to browse for files
 * and upload them. Files can be removed and uploads can be cancelled.
 * For single file uploads version input can be submitted.
 *
 * A multi file upload scenario could look like:
 *
 * var htmlUpload = Alfresco.getRecordsRecordsHtmlUploadInstance();
 * var multiUploadConfig =
 * {
 *    siteId: siteId, *    containerId: doclibContainerId,
 *    path: docLibUploadPath,
 *    filter: [],
 *    mode: htmlUpload.MODE_MULTI_UPLOAD,
 * }
 * this.htmlUpload.show(multiUploadConfig);
 *
 * @namespace Alfresco
 * @class Alfresco.RecordsHtmlUpload
 * @extends Alfresco.HtmlUpload
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * RecordsRecordsHtmlUpload constructor.
    *
    * RecordsHtmlUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.getRecordsHtmlUploadInstance() instead.
    *
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.RecordsHtmlUpload} The new RecordsHtmlUpload instance
    * @constructor
    * @private
    */
   Alfresco.RecordsHtmlUpload = function(htmlId)
   {
      Alfresco.RecordsHtmlUpload.superclass.constructor.call(this, htmlId); 
      
      this.name = "Alfresco.RecordsHtmlUpload";
      Alfresco.util.ComponentManager.reregister(this);

      return this;
   };

   YAHOO.extend(Alfresco.RecordsHtmlUpload, Alfresco.HtmlUpload,
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
       */
      onReady: function RecordsHtmlUpload_onReady()
      {
         // Create and save a reference to the uploadButton so we can alter it later
         this.widgets.recordType = new YAHOO.widget.Button(
         {
            id: this.id + "-recordTypes-select-menu",
            name: "aspects",
            label: this.msg("recordType.default"),
            type: "menu",
            menu: this.id + "-recordTypes-select",
            lazyloadmenu: false,
            container: this.id + "-recordTypes-select-container"
         });

         /**
          * "selectedMenuItemChange" event handler for a Button that will set 
          * the Button's "label" attribute to the value of the "text" 
          * configuration property of the MenuItem that was clicked.
          */
         var fnRecordTypeItemChange = function RecordsFlashUpload_fnRecordTypeItemChange(event)
         {
            var oMenuItem = event.newValue;
            this.set("label", oMenuItem.cfg.getProperty("text"));
            this.createHiddenFields();
         };
         this.widgets.recordType.on("selectedMenuItemChange", fnRecordTypeItemChange);

         // Make sure we can evaluate selectedMenuItem even if no user interaction occurs
         var menu = this.widgets.recordType.getMenu();
         menu.initEvent.fire();
         menu.render();
         this.widgets.recordType.set("selectedMenuItem", menu.getItem(menu.srcElement.selectedIndex));

         // Save reference to html eements so we can modify them later
         this.widgets.destination = Dom.get(this.id + "-destination-hidden");
         this.widgets.recordTypeSection = Dom.get(this.id + "-recordTypeSection-div");

         // Call super class
         Alfresco.RecordsHtmlUpload.superclass.onReady.call(this);
      },

      /**
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       */
      _applyConfig: function RecordsHtmlUpload__applyConfig()
      {
         // Call super class that does that applies the main part of the config attributes 
         Alfresco.RecordsHtmlUpload.superclass._applyConfig.call(this);

         if (this.showConfig.mode === this.MODE_SINGLE_IMPORT)
         {
            // Hide the record type form & flash tips
            Dom.addClass(this.widgets.recordTypeSection, "hidden");
            Dom.addClass(this.widgets.singleUploadTip, "hidden");

            // Set the panel title
            this.widgets.titleText.innerHTML = this.msg("header.singleImport", this.name);

            // Set the forms action url
            var formEl = Dom.get(this.id + "-htmlupload-form");
            if (!this.showConfig.importURL)
            {
               // The .html suffix is required - it is not possible to do a multipart post using an ajax call.
               // So it has to be a FORM submit, to make it feel like an ajax call a a hidden iframe is used.
               // Since the component still needs to be called when the upload is finished, the script returns
               // an html template with SCRIPT tags inside that which calls the component that triggered it.
               formEl.action = Alfresco.constants.PROXY_URI + "api/rma/admin/import.html";
            }
            else
            {
               formEl.action = Alfresco.constants.PROXY_URI + this.showConfig.importURL;
            }
            // Set the file input name to match the import webscripts
            this.widgets.filedata.setAttribute("name", "archive");

            // Set the hidden parameters
            this.widgets.destination.value = this.showConfig.importDestination;
         }
         else
         {
            // Display the record type form & flash tips
            Dom.removeClass(this.widgets.recordTypeSection, "hidden");
            Dom.addClass(this.widgets.singleUploadTip, "hidden");            
            // Only show the "Install Flash" message if Flash is enabled via config
            if (this.showConfig.adobeFlashEnabled)
            {
               // Show the help label for single uploads
               Dom.removeClass(this.widgets.singleUploadTip, "hidden");
            }

            // Set the file input name to match the upload webscripts
            this.widgets.filedata.setAttribute("name", "filedata");
         }
      }
   });
})();