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
 * Folder actions component.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderActions
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
    * FolderActions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.FolderActions} The new FolderActions instance
    * @constructor
    */
   Alfresco.FolderActions = function(htmlId)
   {
      Alfresco.FolderActions.superclass.constructor.call(this, "Alfresco.FolderActions", htmlId, ["button"]);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      
      return this;
   };
   
   /**
    * Extend Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.FolderActions, Alfresco.component.Base);
   
   /**
    * Augment prototype with Actions module
    */
   YAHOO.lang.augmentProto(Alfresco.FolderActions, Alfresco.doclib.Actions);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.FolderActions.prototype,
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
          * Working mode: Site or Repository.
          * Affects how actions operate, e.g. actvities are not posted in Repository mode.
          * 
          * @property workingMode
          * @type number
          * @default Alfresco.doclib.MODE_SITE
          */
         workingMode: Alfresco.doclib.MODE_SITE,

         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",
         
         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",

         /**
          * Replication URL Mapping details
          *
          * @property replicationUrlMapping
          * @type object
          */
         replicationUrlMapping: {}
      },
      
      /**
       * The data for the folder
       * 
       * @property assetData
       * @type object
       */
      assetData: null,

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
       * The urls to be used when creating links in the action cell
       *
       * @method getActionUrls
       * @param recordData {object} Object literal representing the node
       * @param siteId {string} Optional siteId override for site-based locations
       * @return {object} Object literal containing URLs to be substituted in action placeholders
       */
      getActionUrls: function FolderActions_getActionUrls(recordData, siteId)
      {
         var nodeRef = recordData.nodeRef,
            nodeRefUri = new Alfresco.util.NodeRef(nodeRef).uri,
            siteObj = YAHOO.lang.isString(siteId) ? { site: siteId } : null,
            fnPageURL = Alfresco.util.bind(function(page)
            {
               return Alfresco.util.siteURL(page, siteObj);
            }, this);

         return (
         {
            editMetadataUrl: fnPageURL("edit-metadata?nodeRef=" + nodeRef),
            folderRulesUrl: fnPageURL("folder-rules?nodeRef=" + nodeRef),
            managePermissionsUrl: fnPageURL("manage-permissions?nodeRef=" + nodeRef),
            explorerViewUrl: $combine(this.options.repositoryUrl, "/n/showSpaceDetails/", nodeRefUri) + "\" target=\"_blank",
            sourceRepositoryUrl: this.viewInSourceRepositoryURL(recordData) + "\" target=\"_blank"
         });
      },
       
      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       */
      onFolderDetailsAvailable: function FolderActions_onFolderDetailsAvailable(layer, args)
      {
         var me = this;
         
         // Asset data passed-in through event arguments
         this.assetData = args[1].folderDetails;
         this.doclistMetadata = args[1].metadata;
         this.currentPath = this.assetData.location.path;
         
         // Copy template into active area
         var assetData = this.assetData,
            actionsContainer = Dom.get(this.id + "-actionSet"),
            actionSet = assetData.actionSet,
            clone = Dom.get(this.id + "-actionSet-" + actionSet).cloneNode(true);

         // Token replacement
         clone.innerHTML = YAHOO.lang.substitute(window.unescape(clone.innerHTML), this.getActionUrls(this.assetData));

         // Replace existing actions and assign correct class for icon rendering
         actionsContainer.innerHTML = clone.innerHTML;
         Dom.addClass(actionsContainer, assetData.type);

         // Hide actions which have been disallowed through permissions
         if (assetData.permissions && assetData.permissions.userAccess)
         {
            var userAccess = assetData.permissions.userAccess,
               actionLabels = assetData.actionLabels || {},
               actions = YAHOO.util.Selector.query("div", actionsContainer),
               action, actionPermissions, aP, i, ii, j, jj, actionAllowed, aTag, spanTag;

            // Inject special-case permissions
            if (this.options.repositoryUrl)
            {
               userAccess.repository = true;
            }
            userAccess.portlet = Alfresco.constants.PORTLET;

            for (i = 0, ii = actions.length; i < ii; i++)
            {
               action = actions[i];
               actionAllowed = true;
               aTag = action.firstChild;
               spanTag = aTag.firstChild;

               if (spanTag && actionLabels[action.className])
               {
                  spanTag.innerHTML = $html(actionLabels[action.className]);
               }

               if (aTag.rel !== "")
               {
                  actionPermissions = aTag.rel.split(",");
                  for (j = 0, jj = actionPermissions.length; j < jj; j++)
                  {
                     aP = actionPermissions[j];
                     // Support "negative" permissions
                     if ((aP.charAt(0) == "~") ? !!userAccess[aP.substring(1)] : !userAccess[aP])
                     {
                        actionAllowed = false;
                        break;
                     }
                  }
               }
               Dom.setStyle(action, "display", actionAllowed ? "block" : "none");
            }
         }
         Dom.setStyle(actionsContainer, "visibility", "visible");
         
         // Hook action events
         var fnActionHandler = function FolderActions_fnActionHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = owner.className;
               if (typeof me[action] == "function")
               {
                  args[1].stop = true;
                  me[action].call(me, me.assetData, owner);
               }
            }
            return true;
         };
         
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);
         
         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions(this.options.workingMode);
      },

      /**
       * Delete Asset confirmed.
       *
       * @override
       * @method _onActionDeleteConfirm
       * @param asset {object} Object literal representing file or folder to be actioned
       * @private
       */
      _onActionDeleteConfirm: function FolderActions__onActionDeleteConfirm(asset)
      {
         var path = asset.location.path,
            displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef),
            callbackUrl = this.options.workingMode == Alfresco.doclib.MODE_SITE ? "documentlibrary" : "repository",
            encodedPath = path.length > 1 ? "?path=" + encodeURIComponent(path) : "";
         
         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: function FolderActions_oADC_success(data)
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
      }
   }, true);
})();