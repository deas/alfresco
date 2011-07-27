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
 * Document actions component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentActions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,
      $siteURL = Alfresco.util.siteURL;
   
   /**
    * DocumentActions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentActions} The new DocumentActions instance
    * @constructor
    */
   Alfresco.DocumentActions = function(htmlId)
   {
      Alfresco.DocumentActions.superclass.constructor.call(this, "Alfresco.DocumentActions", htmlId, ["button"]);
      YAHOO.Bubbling.on("filesPermissionsUpdated", this.doRefresh, this);
      return this;
   };

   /**
    * Extend Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.DocumentActions, Alfresco.component.Base);
   
   /**
    * Augment prototype with Actions module
    */
   YAHOO.lang.augmentProto(Alfresco.DocumentActions, Alfresco.doclib.Actions);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.DocumentActions.prototype,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
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
         siteId: null,
         
         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",

         /**
          * Valid inline edit mimetypes
          * Currently allowed are plain text, HTML and XML only
          *
          * @property inlineEditMimetypes
          * @type object
          */
         inlineEditMimetypes:
         {
            "text/plain": true,
            "text/html": true,
            "text/xml": true
         },

         /**
          * SharePoint (Vti) Server Details
          *
          * @property vtiServer
          * @type object
          */
         vtiServer: {},

         /**
          * Replication URL Mapping details
          *
          * @property replicationUrlMapping
          * @type object
          */
         replicationUrlMapping: {},

         /**
          * JSON representation of document details
          *
          * @property documentDetails
          * @type object
          */
         documentDetails: null,

         /**
          * Whether the Repo Browser is in use or not
          *
          * @property repositoryBrowsing
          * @type boolean
          */
         repositoryBrowsing: true
      },
      
      /**
       * The data for the document
       * 
       * @property recordData
       * @type object
       */
      recordData: null,
      
      /**
       * Metadata returned by doclist data webscript
       *
       * @property doclistMetadata
       * @type object
       * @default null
       */
      doclistMetadata: null,
      
      /**
       * Path of asset being viewed - used to scope some actions (e.g. copy to, move to)
       * 
       * @property currentPath
       * @type string
       */
      currentPath: null,

      /**
       * Event handler called when "onReady"
       *
       * @method: onReady
       */
      onReady: function DocumentActions_onReady()
      {
         var me = this;
         
         // Asset data 
         this.recordData = this.options.documentDetails.item;
         this.doclistMetadata = this.options.documentDetails.metadata;
         this.currentPath = this.recordData.location.path;
         
         // Populate convenience property
         this.recordData.jsNode = new Alfresco.util.Node(this.recordData.node);

         var actionTypeMarkup =
         {
            "link": '<div class="{id}"><a title="{label}" class="simple-link" href="{href}" {target}><span>{label}</span></a></div>',
            "pagelink": '<div class="{id}"><a title="{label}" class="simple-link" href="{pageUrl}"><span>{label}</span></a></div>',
            "javascript": '<div class="{id}" title="{jsfunction}"><a title="{label}" class="action-link" href="#"><span>{label}</span></a></div>'
         };
         
         var fnRenderAction = function DA_renderAction(p_action, p_record)
         {
            // Store quick look-up for client-side actions
            p_record.actionParams[p_action.id] = p_action.params;
            
            var markupParams =
            {
               "id": p_action.id,
               "label": me.msg(p_action.label)
            };
            
            // Parameter substitution for each action type
            if (p_action.type === "link")
            {
               if (p_action.params.href)
               {
                  markupParams.href = YAHOO.lang.substitute(p_action.params.href, p_record, function DL_renderAction_href(p_key, p_value, p_meta)
                  {
                     return Alfresco.util.findValueByDotNotation(p_record, p_key);
                  });
                  markupParams.target = p_action.params.target ? "target=\"" + p_action.params.target + "\"" : "";
               }
               else
               {
                  Alfresco.logger.warn("Action configuration error: Missing 'href' parameter for actionId: ", p_action.id);
               }
            }
            else if (p_action.type === "pagelink")
            {
               if (p_action.params.page)
               {
                  markupParams.pageUrl = YAHOO.lang.substitute(p_action.params.page, p_record, function DL_renderAction_pageUrl(p_key, p_value, p_meta)
                  {
                     return Alfresco.util.findValueByDotNotation(p_record, p_key);
                  });
               }
               else
               {
                  Alfresco.logger.warn("Action configuration error: Missing 'page' parameter for actionId: ", p_action.id);
               }
            }
            else if (p_action.type === "javascript")
            {
               if (p_action.params["function"])
               {
                  markupParams.jsfunction = p_action.params["function"];
               }
               else
               {
                  Alfresco.logger.warn("Action configuration error: Missing 'function' parameter for actionId: ", p_action.id);
               }
            }

            return YAHOO.lang.substitute(actionTypeMarkup[p_action.type], markupParams);
         };
         
         // Retrieve the actionSet for this record
         var record = this.recordData,
            node = record.node,
            actions = record.actions,
            actionsEl = Dom.get(this.id + "-actionSet"),
            actionHTML = "",
            actionsSel;

         record.actionParams = {};
         for (var i = 0, ii = actions.length; i < ii; i++)
         {
            actionHTML += fnRenderAction(actions[i], record);
         }

         // Token replacement
         var actionUrls = this.getActionUrls(record);
         actionsEl.innerHTML = YAHOO.lang.substitute(actionHTML, actionUrls);


         /**
          * TODO: Sort this lot out
          *
          */
         // Hide actions which have been disallowed through permissions
         if (record.permissions && record.permissions.userAccess)
         {
            /*
             * Configure the Online Edit URL and permission if correct conditions are met
             * Browser == MSIE; vtiServer details retrieved; vti module installed; mimetype matches whitelist
             */
            
            if (YAHOO.env.ua.ie > 0 &&
               this.options.vtiServer && typeof this.options.vtiServer.port == "number" &&
               this.doclistMetadata.onlineEditing &&
               record.mimetype in this.onlineEditMimetypes)
            {
               var loc = record.location,
                  uri = this.options.vtiServer.host + ":" + this.options.vtiServer.port + "/" + $combine("alfresco", loc.site, loc.container, loc.path, loc.file);
               
               if (!(/^(http|https):\/\//).test(uri))
               {
                  // VTI server now supports HTTPS directly http://issues.alfresco.com/jira/browse/DOC-227
                  uri = window.location.protocol + "//" + uri;
               }
               record.onlineEditUrl = uri;
            }
         }

         Dom.addClass(actionsEl, "action-set");
         Dom.setStyle(actionsEl, "visibility", "visible");

         var displayName = record.displayName,
            downloadUrl = actionUrls.downloadUrl;
         
         // Hook action events
         var fnActionHandler = function DocumentActions_fnActionHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               if (typeof me[owner.title] === "function")
               {
                  args[1].stop = true;
                  me[owner.title].call(me, me.recordData, owner);
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);
         
         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();
         
         // Prompt auto-download (after Edit Offline action)?
         if (window.location.hash == "#editOffline")
         {
            window.location.hash = "";

            if (YAHOO.env.ua.ie > 6)
            {
               // MSIE7 blocks the download and gets the wrong URL in the "manual download bar"
               Alfresco.util.PopupManager.displayPrompt(
               {
                  title: this.msg("message.edit-offline.success", displayName),
                  text: this.msg("message.edit-offline.success.ie7"),
                  buttons: [
                  {
                     text: this.msg("button.download"),
                     handler: function DocumentActions_oAEO_success_download()
                     {
                        window.location = downloadUrl;
                        this.destroy();
                     },
                     isDefault: true
                  },
                  {
                     text: this.msg("button.close"),
                     handler: function DocumentActions_oAEO_success_close()
                     {
                        this.destroy();
                     }
                  }]
               });
            }
            else
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.msg("message.edit-offline.success", displayName)
               });
               // Kick off the download 3 seconds after the confirmation message
               YAHOO.lang.later(3000, this, function()
               {
                  window.location = downloadUrl;
               });
            }
         }
         
         if (window.location.hash == "#editCancelled")
         {
            window.location.hash = "";
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.edit-cancel.success", displayName)
            });
         }

         if (window.location.hash == "#checkoutToGoogleDocs")
         {
            window.location.hash = "";
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.checkout-google.success", displayName)
            });
         }
         
         if (window.location.hash == "#checkinFromGoogleDocs")
         {
            window.location.hash = "";
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.checkin-google.success", displayName)
            });
         }
      },

      /**
       * Edit Offline.
       *
       * @override
       * @method onActionEditOffline
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionEditOffline: function DocumentActions_onActionEditOffline(asset)
      {
         var displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef);

         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: function DocumentActions_oAEO_success(data)
                  {
                     this.recordData.nodeRef = data.json.results[0].nodeRef;
                     window.location = this.getActionUrls(this.recordData).documentDetailsUrl + "#editOffline";
                  },
                  scope: this
               }
            },
            failure:
            {
               message: this.msg("message.edit-offline.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "checkout/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });
      },

      /**
       * Cancel editing.
       *
       * @override
       * @method onActionCancelEditing
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionCancelEditing: function DocumentActions_onActionCancelEditing(asset)
      {
         var displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef);

         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: function DocumentActions_oACE_success(data)
                  {
                     this.recordData.nodeRef = data.json.results[0].nodeRef;
                     window.location = this.getActionUrls(this.recordData).documentDetailsUrl + "#editCancelled";
                  },
                  scope: this
               }
            },
            failure:
            {
               message: this.msg("message.edit-cancel.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "cancel-checkout/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });
      },

      /**
       * Upload new version.
       *
       * @override
       * @method onActionUploadNewVersion
       * @param asset {object} Object literal representing the file to be actioned upon
       */
      onActionUploadNewVersion: function DocumentActions_onActionUploadNewVersion(asset)
      {
         var displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef),
            version = asset.version;

         if (!this.fileUpload)
         {
            this.fileUpload = Alfresco.getFileUploadInstance();
         }

         // Show uploader for multiple files
         var description = this.msg("label.filter-description", displayName),
            extensions = "*";

         if (displayName && new RegExp(/[^\.]+\.[^\.]+/).exec(displayName))
         {
            // Only add a filtering extension if filename contains a name and a suffix
            extensions = "*" + displayName.substring(displayName.lastIndexOf("."));
         }
         
         if (asset.custom && asset.custom.workingCopyVersion)
         {
            version = asset.custom.workingCopyVersion;
         }
         
         var singleUpdateConfig =
         {
            updateNodeRef: nodeRef.toString(),
            updateFilename: displayName,
            updateVersion: version,
            suppressRefreshEvent: true,
            overwrite: true,
            filter: [
            {
               description: description,
               extensions: extensions
            }],
            mode: this.fileUpload.MODE_SINGLE_UPDATE,
            onFileUploadComplete:
            {
               fn: this.onNewVersionUploadCompleteCustom,
               scope: this
            }
         };
         if (Alfresco.util.isValueSet(this.options.siteId))
         {
            singleUpdateConfig.siteId = this.options.siteId;
            singleUpdateConfig.containerId = this.options.containerId;
         }
         this.fileUpload.show(singleUpdateConfig);
      },

      /**
       * Called from the uploader component after a the new version has been uploaded.
       *
       * @method onNewVersionUploadCompleteCustom
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onNewVersionUploadCompleteCustom: function DocumentActions_onNewVersionUploadCompleteCustom(complete)
      {
         // Call the normal callback to post the activity data
         this.onNewVersionUploadComplete.call(this, complete);
         this.recordData.nodeRef = complete.successful[0].nodeRef;
         // Delay page reloading to allow time for async requests to be transmitted
         YAHOO.lang.later(0, this, function()
         {
            window.location = this.getActionUrls(this.recordData).documentDetailsUrl;
         });
      },

      /**
       * Checkout to Google Docs.
       *
       * @override
       * @method onActionCheckoutToGoogleDocs
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionCheckoutToGoogleDocs: function DocumentActions_onActionCheckoutToGoogleDocs(asset)
      {
         var displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef),
            path = asset.location.path,
            fileName = asset.fileName;

         var progressPopup = Alfresco.util.PopupManager.displayMessage(
         {
            displayTime: 0,
            effect: null,
            text: this.msg("message.checkout-google.inprogress", displayName)
         });

         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: function DocumentActions_oAEO_success(data)
                  {
                     this.recordData.nodeRef = data.json.results[0].nodeRef;
                     window.location = this.getActionUrls(this.recordData).documentDetailsUrl + "#checkoutToGoogleDocs";
                  },
                  scope: this
               },
               activity:
               {
                  siteId: this.options.siteId,
                  activityType: "google-docs-checkout",
                  page: "document-details",
                  activityData:
                  {
                     fileName: fileName,
                     path: path,
                     nodeRef: nodeRef.toString()
                  }
               }
            },
            failure:
            {
               callback:
               {
                  fn: function DocumentActions_oAEO_failure(data)
                  {
                     progressPopup.destroy();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.checkout-google.failure", displayName)
                     });
                  },
                  scope: this
               }
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "checkout/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });
      },
      
      /**
       * Check in a new version from Google Docs.
       *
       * @override
       * @method onActionCheckinFromGoogleDocs
       * @param asset {object} Object literal representing the file to be actioned upon
       */
      onActionCheckinFromGoogleDocs: function DocumentActions_onActionCheckinFromGoogleDocs(asset)
      {
         var displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef),
            originalNodeRef = new Alfresco.util.NodeRef(asset.custom.workingCopyOriginal),
            path = asset.location.path,
            fileName = asset.fileName;

          var progressPopup = Alfresco.util.PopupManager.displayMessage(
          {
             displayTime: 0,
             effect: null,
             text: this.msg("message.checkin-google.inprogress", displayName)
          });

         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: function DocumentActions_oACE_success(data)
                  {
                     this.recordData.nodeRef = data.json.results[0].nodeRef;
                     window.location = this.getActionUrls(this.recordData).documentDetailsUrl + "#checkinFromGoogleDocs";
                  },
                  scope: this
               },
               activity:
               {
                  siteId: this.options.siteId,
                  activityType: "google-docs-checkin",
                  page: "document-details",
                  activityData:
                  {
                     fileName: displayName,
                     path: path,
                     nodeRef: originalNodeRef.toString()
                  }
               }
            },
            failure:
            {
               fn: function DocumentActions_oAEO_failure(data)
               {
                  progressPopup.destroy();
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.checkin-google.failure", displayName)
                  });
               },
               scope: this
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "checkin/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });
      },

      /**
       * Delete Asset confirmed.
       *
       * @override
       * @method _onActionDeleteConfirm
       * @param asset {object} Object literal representing file or folder to be actioned
       * @private
       */
      _onActionDeleteConfirm: function DocumentActions__onActionDeleteConfirm(asset)
      {
         var path = asset.location.path,
            fileName = asset.fileName,
            displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef),
            callbackUrl = Alfresco.util.isValueSet(this.options.siteId) ? "documentlibrary" : "repository",
            encodedPath = path.length > 1 ? "?path=" + encodeURIComponent(path) : "";
         
         this.modules.actions.genericAction(
         {
            success:
            {
               activity:
               {
                  siteId: this.options.siteId,
                  activityType: "file-deleted",
                  page: "documentlibrary",
                  activityData:
                  {
                     fileName: fileName,
                     path: path,
                     nodeRef: nodeRef.toString()
                  }
               },
               callback:
               {
                  fn: function DocumentActions_oADC_success(data)
                  {
                     window.location = $siteURL(callbackUrl + encodedPath);
                  }
               }
            },
            failure:
            {
               message: this.msg("message.delete.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.DELETE,
               name: "file/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });
      },

      /**
       * Refresh component in response to metadataRefresh event
       *
       * @method doRefresh
       */
      doRefresh: function DocumentActions_doRefresh()
      {
         YAHOO.Bubbling.unsubscribe("filesPermissionsUpdated", this.doRefresh);
         this.refresh('components/document-details/document-actions?nodeRef={nodeRef}' + (this.options.siteId ? '&site={siteId}' : ''));
      }
   }, true);
})();
