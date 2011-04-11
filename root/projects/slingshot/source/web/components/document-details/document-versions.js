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
 * Document Details Version component.
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentVersions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $userProfileLink = Alfresco.util.userProfileLink,
      $userAvatar = Alfresco.Share.userAvatar;

   /**
    * DocumentVersions constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentVersions} The new component instance
    * @constructor
    */
   Alfresco.DocumentVersions = function DocumentVersions_constructor(htmlId)
   {
      Alfresco.DocumentVersions.superclass.constructor.call(this, "Alfresco.DocumentVersions", htmlId, ["datasource", "datatable", "paginator", "history", "animation"]);

      YAHOO.Bubbling.on("metadataRefresh", this.doRefresh, this);
      return this;
   };

   YAHOO.extend(Alfresco.DocumentVersions, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type {object} object literal
       */
      options:
      {
         /**
          * Reference to the current document
          *
          * @property nodeRef
          * @type string
          */
         nodeRef: null,

         /**
          * Current siteId, if any.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * The name of container that the node lives in, will be used when uploading new versions.
          *
          * @property containerId
          * @type string
          */
         containerId: null,

         /**
          * The version of the working copy (if it is a working copy), will be used during upload.
          *
          * @property workingCopyVersion
          * @type string
          */
         workingCopyVersion: null
      },

      /**
       * The latest version of the document
       *
       * @property latestVersion
       * @type {Object}
       */
      latestVersion: null,

      /**
       * Fired by YUI when parent element is available for scripting
       *
       * @method onReady
       */
      onReady: function DocumentVersions_onReady()
      {
         this.widgets.alfrescoDataTable = new Alfresco.util.DataTable(
         {
            dataSource:
            {
               url: Alfresco.constants.PROXY_URI + "api/version?nodeRef=" + this.options.nodeRef,
               doBeforeParseData: this.bind(function(oRequest, oFullResponse)
               {
                  // Versions are returned in an array but must be placed in an object to be able to be parse by yui
                  // Also skip the first version since that is the current version
                  this.latestVersion = oFullResponse.splice(0, 1)[0];
                  Dom.get(this.id + "-latestVersion").innerHTML = this.getDocumentVersionMarkup(this.latestVersion);
                  return (
                  {
                     "data" : oFullResponse
                  });
               })
            },
            dataTable:
            {
               container: this.id + "-olderVersions",
               columnDefinitions:
               [
                  { key: "version", sortable: false, formatter: this.bind(this.renderCellVersion) }
               ],
               config:
               {
                  MSG_EMPTY: this.msg("message.noVersions")
               }
            }
         });
      },

      /**
       * Version renderer
       *
       * @method renderCellVersion
       */
      renderCellVersion: function DocumentVersions_renderCellVersions(elCell, oRecord, oColumn, oData)
      {
         elCell.innerHTML = this.getDocumentVersionMarkup(oRecord.getData());
      },

      /**
       * Builds and returns the markup for a version.
       *
       * @method getDocumentVersionMarkup
       * @param doc {Object} The details for the document
       */
      getDocumentVersionMarkup: function DocumentVersions_getDocumentVersionMarkup(doc)
      {
         var downloadURL = Alfresco.constants.PROXY_URI + '/api/node/content/' + doc.nodeRef.replace(":/", "") + '/' + doc.name + '?a=true',
            html = '';

         html += '<div class="version-panel-left">'
         html += '   <span class="document-version">' + $html(doc.label) + '</span>';
         html += '</div>';
         html += '<div class="version-panel-right">';
         html += '   <h3 class="thin dark">' + $html(doc.name) +  '</h3>';
         html += '   <span class="actions">';
         html += '      <a href="#" name=".onRevertVersionClick" rel="' + doc.label + '" class="' + this.id + ' revert" title="' + this.msg("label.revert") + '">&nbsp;</a>';
         html += '      <a href="' + downloadURL + '" class="download" title="' + this.msg("label.download") + '">&nbsp;</a>';
         html += '   </span>';
         html += '   <div class="clear"></div>';
         html += '   <div class="version-details">';
         html += '      <div class="version-details-left">'
         html += $userAvatar(doc.creator.userName, 32);
         html += '      </div>';
         html += '      <div class="version-details-right">';
         html += $userProfileLink(doc.creator.userName, doc.creator.firstName + ' ' + doc.creator.lastName, 'class="theme-color-1"') + ' ';
         html += Alfresco.util.relativeTime(Alfresco.util.fromISO8601(doc.createdDateISO)) + '<br />';
         html += ((doc.description || "").length > 0) ? $html(doc.description, true) : '<span class="faded">(' + this.msg("label.noComment") + ')</span>';
         html += '      </div>';
         html += '   </div>';
         html += '</div>';

         html += '<div class="clear"></div>';
         return html;
      },

      /**
       * Called when a "onRevertVersionClick" link has been clicked for a version.
       * Will display the devert version dialog.
       *
       * @method onRevertVersionClick
       * @param version
       */
      onRevertVersionClick: function DocumentVersions_onRevertVersionClick(version)
      {
         // Find the version through the index and display the revert dialog for the version
         Alfresco.module.getRevertVersionInstance().show(
         {
            filename: this.latestVersion.name,
            nodeRef: this.options.nodeRef,
            version: version,
            onRevertVersionComplete:
            {
               fn: this.onRevertVersionComplete,
               scope: this
            }
         });
      },

      /**
       * Fired by the Revert Version component after a successful revert.
       * Will display a message and reload the page.
       *
       * @method onRevertVersionComplete
       */
      onRevertVersionComplete: function DocumentVersions_onRevertVersionComplete()
      {
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.revertComplete")
         });

         // Fire metadatarefresh so components may refresh themselves
         YAHOO.Bubbling.fire("metadataRefresh", {});
      },

      /**
       * Called when the "onUploadNewVersionClick" link has been clicked.
       * Will display the upload dialog in new version mode.
       *
       * @method onUploadNewVersionClick
       */
      onUploadNewVersionClick: function DocumentVersions_onUploadNewVersionClick()
      {
         if (!this.modules.fileUpload)
         {
            this.modules.fileUpload = Alfresco.getFileUploadInstance();
         }

         var current = this.latestVersion,
            displayName =  current.name,
            extensions = "*";

         if (displayName && new RegExp(/[^\.]+\.[^\.]+/).exec(displayName))
         {
            // Only add a filtering extension if filename contains a name and a suffix
            extensions = "*" + displayName.substring(displayName.lastIndexOf("."));
         }

         this.modules.fileUpload.show(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            updateNodeRef: this.options.nodeRef,
            updateFilename: displayName,
            updateVersion: this.options.workingCopyVersion || current.label,
            overwrite: true,
            filter: [
               {
                  description: this.msg("label.filter-description", displayName),
                  extensions: extensions
               }],
            mode: this.modules.fileUpload.MODE_SINGLE_UPDATE,
            onFileUploadComplete:
            {
               fn: this.onNewVersionUploadComplete,
               scope: this
            }
         });
      },

      /**
       * Called when the upload new version dialog is finished uploading the new version.
       * Will display succes or failure and repload the page if everything went ok.
       *
       * @method onNewVersionUploadComplete
       */
      onNewVersionUploadComplete: function DocumentVersions_onNewVersionUploadComplete(complete)
      {
         if (complete.failed.length == 0 && complete.successful.length > 0)
         {
            // No activities in Repository mode
            if (this.options.siteId == null || this.options.siteId.length == 0)
            {
               return;
            }

            try
            {
               Alfresco.util.Ajax.jsonPost(
               {
                  url: Alfresco.constants.PROXY_URI + "slingshot/doclib/activity",
                  dataObj:
                  {
                     fileName: complete.successful[0].fileName,
                     nodeRef: complete.successful[0].nodeRef,
                     site: this.options.siteId,
                     type: "file-updated",
                     page: "document-details"
                  }
               });
            }
            catch (e)
            {
               // Ignore, not important enough to bother user about
            }
         }
      },

      /**
       * Refresh component in response to metadataRefresh event
       *
       * @method doRefresh
       */
      doRefresh: function DocumentVersions_doRefresh()
      {
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh);
         this.refresh('components/document-details/document-versions?nodeRef={nodeRef}' + (this.options.siteId ? '&site={siteId}' :  ''));
      }
   });
})();
