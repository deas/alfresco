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
 * Document Library Actions module
 *
 * @namespace Alfresco.doclib
 * @class Alfresco.doclib.Actions
 */
(function()
{
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,
      $siteURL = Alfresco.util.siteURL;

   /**
    * Alfresco.doclib.Actions implementation
    */
   Alfresco.doclib.Actions = {};
   Alfresco.doclib.Actions.prototype =
   {
      /**
       * Current actions view type: set by owning class to "browse" or "details".
       *
       * @property actionsView
       * @type string
       */
      actionsView: null,

      /**
       * Renders a single action for a given record.
       * Callers should then use
       * <pre>
       *    YAHOO.lang.substitute(actionHTML, this.getActionUrls(record))
       * </pre>
       * on the final concatenated HTML for multiple actions to populate placeholder URLs.
       *
       * @method renderAction
       * @param p_action {object} Object literal representing the node
       * @param p_record {string} Optional siteId override for site-based locations
       * @return {string} HTML containing action markup
       */
      renderAction: function dlA_renderAction(p_action, p_record)
      {
         var urlContext = Alfresco.constants.URL_RESCONTEXT + "components/documentlibrary/actions/",
           iconStyle = 'style="background-image:url(' + urlContext + '{icon}-16.png)" ',
           actionTypeMarkup =
           {
              "link": '<div class="{id}"><a title="{label}" class="simple-link" href="{href}" ' + iconStyle + '{target}><span>{label}</span></a></div>',
              "pagelink": '<div class="{id}"><a title="{label}" class="simple-link" href="{pageUrl}" ' + iconStyle + '><span>{label}</span></a></div>',
              "javascript": '<div class="{id}" title="{jsfunction}"><a title="{label}" class="action-link" href="#"' + iconStyle + '><span>{label}</span></a></div>'
           };
      
         // Store quick look-up for client-side actions
         p_record.actionParams[p_action.id] = p_action.params;
         
         var markupParams =
         {
            "id": p_action.id,
            "icon": p_action.icon,
            "label": Alfresco.util.substituteDotNotation(this.msg(p_action.label), p_record)
         };
         
         // Parameter substitution for each action type
         if (p_action.type === "link")
         {
            if (p_action.params.href)
            {
               /*
               markupParams.href = YAHOO.lang.substitute(p_action.params.href, p_record, function DL_renderAction_href(p_key, p_value, p_meta)
               {
                  return Alfresco.util.findValueByDotNotation(p_record, p_key);
               });
               */
               markupParams.href = Alfresco.util.substituteDotNotation(p_action.params.href, p_record);
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
               /*
               markupParams.pageUrl = YAHOO.lang.substitute(p_action.params.page, p_record, function DL_renderAction_pageUrl(p_key, p_value, p_meta)
               {
                  return Alfresco.util.findValueByDotNotation(p_record, p_key);
               });
               */
               markupParams.pageUrl = $siteURL(Alfresco.util.substituteDotNotation(p_action.params.page, p_record));
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
      },

      /**
       * The urls to be used when creating links in the action cell
       *
       * @method getActionUrls
       * @param recordData {object} Object literal representing the node
       * @param siteId {string} Optional siteId override for site-based locations
       * @return {object} Object literal containing URLs to be substituted in action placeholders
       */
      getActionUrls: function dlA_getActionUrls(record, siteId)
      {
         var jsNode = record.jsNode,
            nodeRef = jsNode.isLink ? jsNode.linkedNode.nodeRef : jsNode.nodeRef,
            strNodeRef = nodeRef.toString(),
            nodeRefUri = nodeRef.uri,
            contentUrl = jsNode.contentURL,
            workingCopy = record.workingCopy || {},
            siteObj = YAHOO.lang.isString(siteId) ? { site: siteId } : null,
            fnPageURL = Alfresco.util.bind(function(page)
            {
               return Alfresco.util.siteURL(page, siteObj);
            }, this);

         return (
         {
            downloadUrl: $combine(Alfresco.constants.PROXY_URI, contentUrl) + "?a=true",
            viewUrl:  $combine(Alfresco.constants.PROXY_URI, contentUrl) + "\" target=\"_blank",
            documentDetailsUrl: fnPageURL("document-details?nodeRef=" + strNodeRef),
            folderDetailsUrl: fnPageURL("folder-details?nodeRef=" + strNodeRef),
            editMetadataUrl: fnPageURL("edit-metadata?nodeRef=" + strNodeRef),
            inlineEditUrl: fnPageURL("inline-edit?nodeRef=" + strNodeRef),
            managePermissionsUrl: fnPageURL("manage-permissions?nodeRef=" + strNodeRef),
            manageTranslationsUrl: fnPageURL("manage-translations?nodeRef=" + strNodeRef),
            workingCopyUrl: fnPageURL("document-details?nodeRef=" + (workingCopy.workingCopyNodeRef || strNodeRef)),
            workingCopySourceUrl: fnPageURL("document-details?nodeRef=" + (workingCopy.sourceNodeRef || strNodeRef)),
            viewGoogleDocUrl: workingCopy.googleDocUrl + "\" target=\"_blank",
            explorerViewUrl: $combine(this.options.repositoryUrl, "/n/showSpaceDetails/", nodeRefUri) + "\" target=\"_blank",
            sourceRepositoryUrl: this.viewInSourceRepositoryURL(record) + "\" target=\"_blank"
         });
      },


      /**
       * Helper for actions of type "javascript" to get the node's action descriptor with params resolved (unless resolve is set to false).
       *
       * @method getAction
       * @param record {object} Object literal representing one file or folder to be actioned
       * @param owner {HTMLElement} The action html element
       * @param resolve {Boolean} (Optional) Set to false if the action param's {} shouldn't get resolved
       */
      getAction: function dlA_getAction(record, owner, resolve)
      {
         var actionId = owner.getAttribute("class"),
            action = Alfresco.util.findInArray(record.actions, actionId, "id") || {};

         if (resolve === false)
         {
            // Return action without resolved parameters
            return action;
         }
         else
         {
            // Resolve action's parameters before returning them
            action = Alfresco.util.deepCopy(action);
            var params = action.params || {};
            for (var key in params)
            {
               params[key] = YAHOO.lang.substitute(params[key], record, function getActionParams_substitute(p_key, p_value, p_meta)
               {
                  return Alfresco.util.findValueByDotNotation(record, p_key);
               });
            }
            return action;
         }
      },

      /**
       * Tries to get a common parent nodeRef for an action that requires one.
       *
       * @method getParentNodeRef
       * @param record {object} Object literal representing one file or folder to be actioned
       * @return {string|null} Parent nodeRef or null
       */
      getParentNodeRef: function dlA_getParentNodeRef(record)
      {
         var nodeRef = null;

         if (YAHOO.lang.isArray(record))
         {
            try
            {
               nodeRef = this.doclistMetadata.parent.nodeRef;
            }
            catch (e)
            {
               nodeRef = null;
            }

            if (nodeRef === null)
            {
               for (var i = 1, j = record.length, sameParent = true; i < j && sameParent; i++)
               {
                  sameParent = (record[i].parent.nodeRef == record[i - 1].parent.nodeRef)
               }

               nodeRef = sameParent ? record[0].parent.nodeRef : this.doclistMetadata.container;
            }
         }
         else
         {
            nodeRef = record.parent.nodeRef;
         }

         return nodeRef;
      },

      /**
       * Record metadata.
       *
       * @override
       * @method onActionDetails
       * @param record {object} Object literal representing one file or folder to be actioned
       */
      onActionDetails: function dlA_onActionDetails(record)
      {
         var scope = this,
            nodeRef = record.nodeRef,
            jsNode = record.jsNode;

         // Intercept before dialog show
         var doBeforeDialogShow = function dlA_onActionDetails_doBeforeDialogShow(p_form, p_dialog)
         {
            // Dialog title
            var fileSpan = '<span class="light">' + $html(record.displayName) + '</span>';

            Alfresco.util.populateHTML(
               [ p_dialog.id + "-dialogTitle", scope.msg("edit-details.title", fileSpan) ]
            );

            // Edit metadata link button
            this.widgets.editMetadata = Alfresco.util.createYUIButton(p_dialog, "editMetadata", null,
            {
               type: "link",
               label: scope.msg("edit-details.label.edit-metadata"),
               href: $siteURL("edit-metadata?nodeRef=" + nodeRef)
            });
         };

         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
         {
            itemKind: "node",
            itemId: nodeRef,
            mode: "edit",
            submitType: "json",
            formId: "doclib-simple-metadata"
         });

         // Using Forms Service, so always create new instance
         var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");

         editDetails.setOptions(
         {
            width: "40em",
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
               fn: function dlA_onActionDetails_success(response)
               {
                  // Reload the node's metadata
                  Alfresco.util.Ajax.request(
                  {
                     url: $combine(Alfresco.constants.URL_SERVICECONTEXT, "components/documentlibrary/data/node/", jsNode.nodeRef.uri) + "?view=" + this.actionsView,
                     successCallback:
                     {
                        fn: function dlA_onActionDetails_refreshSuccess(response)
                        {
                           var record = response.json.item
                           record.jsNode = new Alfresco.util.Node(response.json.item.node);

                           // Fire "renamed" event
                           YAHOO.Bubbling.fire(record.node.isContainer ? "folderRenamed" : "fileRenamed",
                           {
                              file: record
                           });

                           // Fire "tagRefresh" event
                           YAHOO.Bubbling.fire("tagRefresh");

                           // Display success message
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: this.msg("message.details.success")
                           });
                        },
                        scope: this
                     },
                     failureCallback:
                     {
                        fn: function dlA_onActionDetails_refreshFailure(response)
                        {
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: this.msg("message.details.failure")
                           });
                        },
                        scope: this
                     }
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function dLA_onActionDetails_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.details.failure")
                  });
               },
               scope: this
            }
         }).show();
      },

      /**
       * Locate record.
       *
       * @method onActionLocate
       * @param record {object} Object literal representing one file or folder to be actioned
       */
      onActionLocate: function dlA_onActionLocate(record)
      {
         var jsNode = record.jsNode,
            path = record.location.path,
            file = jsNode.isLink ? jsNode.linkedNode.properties.name : record.displayName;
         
         if (Alfresco.util.isValueSet(this.options.siteId) && record.location.site.name !== this.options.siteId)
         {
            window.location = $siteURL("documentlibrary?file=" + encodeURIComponent(file) + "&path=" + encodeURIComponent(path),
            {
               site: record.location.site.name
            });
         }
         else
         {
            this.options.highlightFile = file;
            
            // Change active filter to path
            YAHOO.Bubbling.fire("changeFilter",
            {
               filterId: "path",
               filterData: path
            });
         }
      },

      /**
       * Delete record.
       *
       * @method onActionDelete
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionDelete: function dlA_onActionDelete(record)
      {
         var me = this,
            jsNode = record.jsNode;

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("actions." + (jsNode.isContainer ? "folder" : "document") + ".delete"),
            text: this.msg("message.confirm.delete", record.displayName),
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function dlA_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm.call(me, record);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function dlA_onActionDelete_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Delete record confirmed.
       *
       * @method _onActionDeleteConfirm
       * @param record {object} Object literal representing the file or folder to be actioned
       * @private
       */
      _onActionDeleteConfirm: function dlA__onActionDeleteConfirm(record)
      {
         var jsNode = record.jsNode,
            path = record.location.path,
            fileName = record.location.file,
            filePath = $combine(path, fileName),
            displayName = record.displayName,
            nodeRef = jsNode.nodeRef;

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
               event:
               {
                  name: jsNode.isContainer ? "folderDeleted" : "fileDeleted",
                  obj:
                  {
                     path: filePath
                  }
               },
               message: this.msg("message.delete.success", displayName)
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
       * Edit Offline.
       * NOTE: Placeholder only, clients MUST implement their own editOffline action
       *
       * @method onActionEditOffline
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionEditOffline: function dlA_onActionEditOffline(record)
      {
         Alfresco.logger.error("onActionEditOffline", "Abstract implementation not overridden");
      },

      /**
       * Valid online edit mimetypes, mapped to application ProgID.
       * Currently allowed are Microsoft Office 2003 and 2007 mimetypes for Excel, PowerPoint and Word only
       *
       * @property onlineEditMimetypes
       * @type object
       */
      onlineEditMimetypes:
      {
         "application/vnd.ms-excel": "Excel.Sheet",
         "application/vnd.ms-powerpoint": "PowerPoint.Slide",
         "application/msword": "Word.Document",
         "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": "Excel.Sheet",
         "application/vnd.openxmlformats-officedocument.presentationml.presentation": "PowerPoint.Slide",
         "application/vnd.openxmlformats-officedocument.wordprocessingml.document": "Word.Document"
      },

      /**
       * Edit Online.
       *
       * @method onActionEditOnline
       * @param record {object} Object literal representing file or folder to be actioned
       */
      onActionEditOnline: function dlA_onActionEditOnline(record)
      {
         if (this._launchOnlineEditor(record))
         {
            YAHOO.Bubbling.fire("metadataRefresh");
         }
      },

      /**
       * Opens the appropriate Microsoft Office application for online editing.
       * Supports: Microsoft Office 2003, 2007 & 2010.
       *
       * @method Alfresco.util.sharePointOpenDocument
       * @param record {object} Object literal representing file or folder to be actioned
       * @return {boolean} True if the action was completed successfully, false otherwise.
       */
      _launchOnlineEditor: function dlA__launchOnlineEditor(record)
      {
         var controlProgID = "SharePoint.OpenDocuments",
            jsNode = record.jsNode,
            loc = record.location,
            mimetype = jsNode.mimetype,
            appProgID = null,
            activeXControl = null,
            extensionMap =
            {
               xls: "application/vnd.ms-excel",
               ppt: "application/vnd.ms-powerpoint",
               doc: "application/msword",
               xlsx: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
               pptx: "application/vnd.openxmlformats-officedocument.presentationml.presentation",
               docx: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            };

         // Try to resolve the record to an application ProgID; by mimetype first, then file extension.
         if (this.onlineEditMimetypes.hasOwnProperty(mimetype))
         {
            appProgID = this.onlineEditMimetypes[mimetype];
         }
         else
         {
            var extn = Alfresco.util.getFileExtension(record.location.file);
            if (extn !== null)
            {
               extn = extn.toLowerCase();
               if (extensionMap.hasOwnProperty(extn))
               {
                  mimetype = extensionMap[extn];
                  if (this.onlineEditMimetypes.hasOwnProperty(mimetype))
                  {
                     appProgID = this.onlineEditMimetypes[mimetype];
                  }
               }
            }
         }

         if (appProgID !== null)
         {
            // Ensure we have the record's onlineEditUrl populated
            if (!Alfresco.util.isValueSet(record.onlineEditUrl))
            {
               var onlineEditUrl = this.doclistMetadata.custom.vtiServer.host + ":" +
                     this.doclistMetadata.custom.vtiServer.port + "/" +
                     $combine("alfresco", loc.site.name, loc.container.name, loc.path, loc.file);

               if (!(/^(http|https):\/\//).test(onlineEditUrl))
               {
                  // VTI server now supports HTTPS directly http://issues.alfresco.com/jira/browse/DOC-227
                  onlineEditUrl = window.location.protocol + "//" + onlineEditUrl;
               }
               record.onlineEditUrl = onlineEditUrl;
            }

            // Try each version of the SharePoint control in turn, newest first
            try
            {
               activeXControl = new ActiveXObject(controlProgID + ".3");
               return activeXControl.EditDocument3(window, record.onlineEditUrl, true, appProgID);
            }
            catch(e)
            {
               try
               {
                  activeXControl = new ActiveXObject(controlProgID + ".2");
                  return activeXControl.EditDocument2(window, record.onlineEditUrl, appProgID);
               }
               catch(e1)
               {
                  try
                  {
                     activeXControl = new ActiveXObject(controlProgID + ".1");
                     return activeXControl.EditDocument(record.onlineEditUrl, appProgID);
                  }
                  catch(e2)
                  {
                     // Do nothing
                  }
               }
            }
         }

         // No success in launching application via ActiveX control; launch the WebDAV URL anyway
         return window.open(record.onlineEditUrl, "_blank");
      },

      /**
       * Checkout to Google Docs.
       * NOTE: Placeholder only, clients MUST implement their own checkoutToGoogleDocs action
       *
       * @method onActionCheckoutToGoogleDocs
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionCheckoutToGoogleDocs: function dlA_onActionCheckoutToGoogleDocs(record)
      {
         Alfresco.logger.error("onActionCheckoutToGoogleDocs", "Abstract implementation not overridden");
      },

      /**
       * Check in a new version from Google Docs.
       * NOTE: Placeholder only, clients MUST implement their own checkinFromGoogleDocs action
       *
       * @method onActionCheckinFromGoogleDocs
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionCheckinFromGoogleDocs: function dlA_onActionCheckinFromGoogleDocs(record)
      {
         Alfresco.logger.error("onActionCheckinFromGoogleDocs", "Abstract implementation not overridden");
      },

      /**
       * Simple Repo Action.
       *
       * Accepts the following <param> declarations from the <action> config:
       *
       * action - The name of  the repo action (i.e. extract-metadata)
       * success - The name of the callback function
       * successMessage - The msg key to use when the repo action succeded (i.e. message.extract-metadata.success)
       * failure - The name of the callback function
       * failureMessage - The msg key to use when the repo action failed (i.e. message.extract-metadata.failure)
       *
       * @method onActionSimpleRepoAction
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionSimpleRepoAction: function dlA_onActionSimpleRepoAction(record, owner)
      {
         // Get action params
         var params = this.getAction(record, owner).params,
            displayName = record.displayName;

         // Prepare genericAction config
         var config =
         {
            success:
            {
               event:
               {
                  name: "metadataRefresh",
                  obj: record
               }
            },
            failure:
            {
               message: this.msg(params.failureMessage, displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               stem: Alfresco.constants.PROXY_URI + "api/",
               name: "actionQueue"
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  actionedUponNode: record.nodeRef,
                  actionDefinitionName: params.action
               }
            }
         };

         // Add configured success callbacks and messages if provided
         if (YAHOO.lang.isFunction(this[params.success]))
         {
            config.success.callback =
            {
               fn: this[params.success],
               obj: record,
               scope: this
            };
         }
         if (params.successMessage)
         {
            config.success.message = this.msg(params.successMessage, displayName);
         }

         // Acd configured failure callback and message if provided
         if (YAHOO.lang.isFunction(this[params.failure]))
         {
            config.failure.callback =
            {
               fn: this[params.failure],
               obj: record,
               scope: this
            };
         }
         if (params.failureMessage)
         {
            config.failure.message = this.msg(params.failureMessage, displayName);
         }

         // Execute the repo action
         this.modules.actions.genericAction(config);
      },

      /**
       * Form Dialog Action.
       *
       * Accepts <param name=""></param> declarations in share config xml for the following names:
       * success - The name of the callback function
       * successMessage - The msg key to use when the repo action succeded (i.e. message.extract-metadata.success)
       * failure - The name of the callback function
       * failureMessage - The msg key to use when the repo action failed (i.e. message.extract-metadata.failure)
       * ...and any other parameter mathing the properties for GET /service/components/form webscript
       * i.e itemid, itemkind, mode etc...
       *
       * @method onActionFormDialog
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionFormDialog: function dlA_onActionFormDialog(record, owner)
      {
         // Get action & params and start create the config for displayForm
         var action = this.getAction(record, owner),
            params = action.params,
            config =
            {
               title: this.msg(action.label)
            },
            displayName = record.displayName;

         // Make sure we don't pass the function as a form parameter
         delete params["function"];

         // Add configured success callback
         var success = params["success"];
         delete params["success"];
         config.success =
         {
            fn: function(response, obj)
            {
               // Invoke callback if configured and available
               if (YAHOO.lang.isFunction(this[success]))
               {
                  this[success].call(this, response, obj);
               }

               // Fire metadataRefresh so other components may update themselves
               YAHOO.Bubbling.fire("metadataRefresh", obj);
            },
            obj: record,
            scope: this
         };

         // Add configure success message
         if (params.successMessage)
         {
            config.successMessage = this.msg(params.successMessage, displayName);
            delete params["successMessage"];
         }

         // Add configured failure callback
         if (YAHOO.lang.isFunction(this[params.failure]))
         {
            config.failure =
            {
               fn: this[params.failure],
               obj: record,
               scope: this
            };
            delete params["failure"];
         }
         // Add configure success message
         if (params.failureMessage)
         {
            config.failureMessage = this.msg(params.failureMessage, displayName);
            delete params["failureMessage"];
         }

         // Use the remaining properties as form properties
         config.properties = params;

         // Finally display form as dialog
         Alfresco.util.PopupManager.displayForm(config);
      },

      /**
       * Upload new version.
       *
       * @method onActionUploadNewVersion
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionUploadNewVersion: function dlA_onActionUploadNewVersion(record)
      {
         var jsNode = record.jsNode,
            displayName = record.displayName,
            nodeRef = jsNode.nodeRef,
            version = record.version;

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

         if (record.workingCopy && record.workingCopy.workingCopyVersion)
         {
            version = record.workingCopy.workingCopyVersion;
         }

         var singleUpdateConfig =
         {
            updateNodeRef: nodeRef.toString(),
            updateFilename: displayName,
            updateVersion: version,
            overwrite: true,
            filter: [
            {
               description: description,
               extensions: extensions
            }],
            mode: this.fileUpload.MODE_SINGLE_UPDATE,
            onFileUploadComplete:
            {
               fn: this.onNewVersionUploadComplete,
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
       * Handles creating activity events after file upload completion
       *
       * @method _uploadComplete
       * @protected
       * @param complete {object} Object literal containing details of successful and failed uploads
       * @param uploadType {String} Either "added" or "updated" depending on the file action
       */
      _uploadComplete: function dlA__uploadComplete(complete, uploadType)
      {
         var success = complete.successful.length, activityData, file;
         if (success > 0)
         {
            if (success < (this.options.groupActivitiesAt || 5))
            {
               // Below cutoff for grouping Activities into one
               for (var i = 0; i < success; i++)
               {
                  file = complete.successful[i];
                  activityData =
                  {
                     fileName: file.fileName,
                     nodeRef: file.nodeRef
                  };
                  this.modules.actions.postActivity(this.options.siteId, "file-" + uploadType, "document-details", activityData);
               }
            }
            else
            {
               // grouped into one message
               activityData =
               {
                  fileCount: success,
                  path: this.currentPath,
                  parentNodeRef: this.doclistMetadata.parent.nodeRef
               };
               this.modules.actions.postActivity(this.options.siteId, "files-" + uploadType, "documentlibrary", activityData);
            }
         }
      },

      /**
       * Called from the uploader component after one or more files have been uploaded.
       *
       * @method onFileUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onFileUploadComplete: function dlA_onFileUploadComplete(complete)
      {
         this._uploadComplete(complete, "added");
      },

      /**
       * Called from the uploader component after one or more files have been updated.
       *
       * @method onNewVersionUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onNewVersionUploadComplete: function dlA_onNewVersionUploadComplete(complete)
      {
         this._uploadComplete(complete, "updated");
      },

      /**
       * Cancel editing.
       *
       * @method onActionCancelEditing
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionCancelEditing: function dlA_onActionCancelEditing(record)
      {
         var displayName = record.displayName;

         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "metadataRefresh"
               },
               message: this.msg("message.edit-cancel.success", displayName)
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
                  nodeRef: record.jsNode.nodeRef.uri
               }
            }
         });
      },

      /**
       * Copy single document or folder.
       *
       * @method onActionCopyTo
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionCopyTo: function dlA_onActionCopyTo(record)
      {
         this._copyMoveTo("copy", record);
      },

      /**
       * Move single document or folder.
       *
       * @method onActionMoveTo
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionMoveTo: function dlA_onActionMoveTo(record)
      {
         this._copyMoveTo("move", record);
      },

      /**
       * Copy/Move To implementation.
       *
       * @method _copyMoveTo
       * @param mode {String} Operation mode: copy|move
       * @param record {object} Object literal representing the file or folder to be actioned
       * @private
       */
      _copyMoveTo: function dlA__copyMoveTo(mode, record)
      {
         // Check mode is an allowed one
         if (!mode in
            {
               copy: true,
               move: true
            })
         {
            throw new Error("'" + mode + "' is not a valid Copy/Move to mode.");
         }

         if (!this.modules.copyMoveTo)
         {
            this.modules.copyMoveTo = new Alfresco.module.DoclibCopyMoveTo(this.id + "-copyMoveTo");
         }

         var allowedViewModes =
         [
            Alfresco.module.DoclibGlobalFolder.VIEW_MODE_SITE
         ];
         if (this.options.repositoryBrowsing === true)
         {
            allowedViewModes.push(Alfresco.module.DoclibGlobalFolder.VIEW_MODE_REPOSITORY, Alfresco.module.DoclibGlobalFolder.VIEW_MODE_USERHOME);
         }

         this.modules.copyMoveTo.setOptions(
         {
            allowedViewModes: allowedViewModes,
            mode: mode,
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: record,
            rootNode: this.options.rootNode,
            parentId: this.getParentNodeRef(record)
         }).showDialog();
      },

      /**
       * Assign workflow.
       *
       * @method onActionAssignWorkflow
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionAssignWorkflow: function dlA_onActionAssignWorkflow(record)
      {
         var nodeRefs = "",
            destination = this.getParentNodeRef(record);

         if (YAHOO.lang.isArray(asset))
         {
            for (var i = 0, il = asset.length; i < il; i++)
            {
               nodeRefs += (i === 0 ? "" : ",") + asset[i].nodeRef;
            }
         }
         else
         {
            nodeRefs = asset.nodeRef;
         }
         var postBody =
         {
            selectedItems: nodeRefs
         };
         if (destination)
         {
            postBody.destination = destination;
         }
         Alfresco.util.navigateTo($siteURL("start-workflow"), "POST", postBody);
      },

      /**
       * Set permissions on a single document or folder.
       *
       * @method onActionManagePermissions
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionManagePermissions: function dlA_onActionManagePermissions(record)
      {
         if (!this.modules.permissions)
         {
            this.modules.permissions = new Alfresco.module.DoclibPermissions(this.id + "-permissions");
         }

         this.modules.permissions.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: record
         }).showDialog();
      },

      /**
       * Manage aspects.
       *
       * @method onActionManageAspects
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionManageAspects: function dlA_onActionManageAspects(record)
      {
         if (!this.modules.aspects)
         {
            this.modules.aspects = new Alfresco.module.DoclibAspects(this.id + "-aspects");
         }

         this.modules.aspects.setOptions(
         {
            file: record
         }).show();
      },

      /**
       * Change Type
       *
       * @method onActionChangeType
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionChangeType: function dlA_onActionChangeType(record)
      {
         var jsNode = record.jsNode,
            currentType = jsNode.type,
            displayName = record.displayName,
            actionUrl = Alfresco.constants.PROXY_URI + $combine("slingshot/doclib/type/node", jsNode.nodeRef.uri);

         var doSetupFormsValidation = function dlA_oACT_doSetupFormsValidation(p_form)
         {
            // Validation
            p_form.addValidation(this.id + "-changeType-type", function fnValidateType(field, args, event, form, silent, message)
            {
               return field.options[field.selectedIndex].value !== "-";
            }, null, "change");
            p_form.setShowSubmitStateDynamically(true, false);
         };

         // Always create a new instance
         this.modules.changeType = new Alfresco.module.SimpleDialog(this.id + "-changeType").setOptions(
         {
            width: "30em",
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/change-type?currentType=" + encodeURIComponent(currentType),
            actionUrl: actionUrl,
            doSetupFormsValidation:
            {
               fn: doSetupFormsValidation,
               scope: this
            },
            firstFocus: this.id + "-changeType-type",
            onSuccess:
            {
               fn: function dlA_onActionChangeType_success(response)
               {
                  YAHOO.Bubbling.fire("metadataRefresh",
                  {
                     highlightFile: displayName
                  });
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.change-type.success", displayName)
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function dlA_onActionChangeType_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.change-type.failure", displayName)
                  });
               },
               scope: this
            }
         });
         this.modules.changeType.show();
      },

      /**
       * View in source Repository URL helper
       *
       * @method viewInSourceRepositoryURL
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      viewInSourceRepositoryURL: function dlA_viewInSourceRepositoryURL(record)
      {
         var node = record.node,
            repoId = record.location.repositoryId,
            urlMapping = this.options.replicationUrlMapping,
            siteUrl;

         if (!repoId || !urlMapping || !urlMapping[repoId])
         {
            return "#";
         }

         // Generate a URL to the relevant details page
         siteUrl = this.getActionUrls(record)[node.isContainer ? "folderDetailsUrl" : "documentDetailsUrl"];
         // Strip off this webapp's context as the mapped one might be different
         siteUrl = siteUrl.substring(Alfresco.constants.URL_CONTEXT.length);

         return $combine(urlMapping[repoId], "/", siteUrl);
      },

      /**
       * Social Publishing
       * 
       * @method onActionPublish
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionPublish: function dlA_onActionPublish(record)
      {
         // Call the Social Publishing Module
         Alfresco.module.getSocialPublishingInstance().show(
         {
            nodeRef: record.nodeRef,
            filename: record.fileName
         });
      }
   };
})();
