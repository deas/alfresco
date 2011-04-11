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
 * DOD5015 Document Library Actions module
 * 
 * @namespace Alfresco.doclib
 * @class Alfresco.doclib.RecordsActions
 */
(function()
{
   /**
    * Alfresco.doclib.RecordsActions namespace
    */
   Alfresco.doclib.RecordsActions = {};

   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   Alfresco.doclib.RecordsActions.prototype =
   {
      /**
       * Public Action implementations.
       *
       * NOTE: Actions are defined in alphabetical order by convention.
       */
      
      /**
       * Accession action.
       *
       * @method onActionAccession
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionAccession: function RDLA_onActionAccession(assets)
      {
         this._dod5015Action("message.accession", assets, "accession", null,
         {
            success:
            {
               callback:
               {
                  fn: this._transferAccessionComplete,
                  obj:
                  {
                     displayName: YAHOO.lang.isArray(assets) ? this.msg("message.multi-select", assets.length) : $html(assets.displayName)
                  },
                  scope: this
               }
            }
         });
      },

      /**
       * Accession Complete action.
       *
       * @method onActionAccessionComplete
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionAccessionComplete: function RDLA_onActionAccessionComplete(assets)
      {
         this._dod5015Action("message.accession-complete", assets, "accessionComplete");
      },

      /**
       * Copy single document or folder.
       *
       * @method onActionCopyTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionCopyTo: function RDLA_onActionCopyTo(assets)
      {
         this._copyMoveFileTo("copy", assets);
      },

      /**
       * File single document or folder.
       *
       * @method onActionFileTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionFileTo: function RDLA_onActionFileTo(assets)
      {
         this._copyMoveFileTo("file", assets);
      },

      /**
       * Move single document or folder.
       *
       * @method onActionMoveTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionMoveTo: function RDLA_onActionMoveTo(assets)
      {
         this._copyMoveFileTo("move", assets);
      },
      
      /**
       * Close Record Folder action.
       *
       * @method onActionCloseFolder
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionCloseFolder: function RDLA_onActionCloseFolder(assets)
      {
         this._dod5015Action("message.close", assets, "closeRecordFolder");
      },

      /**
       * Cut Off action.
       *
       * @method onActionCutoff
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionCutoff: function RDLA_onActionCutoff(assets)
      {
         this._dod5015Action("message.cutoff", assets, "cutoff");
      },

      /**
       * Declare Record action.
       * Special case handling due to the ability to jump to the Edit Metadata page if the action failed.
       *
       * @method onActionDeclare
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionDeclare: function RDLA_onActionDeclare(assets)
      {
         var displayName = $html(assets.displayName),
            editMetadataUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/edit-metadata?nodeRef=" + assets.nodeRef;

         this._dod5015Action("message.declare", assets, "declareRecord", null,
         {
            failure:
            {
               message: null,
               callback:
               {
                  fn: function RDLA_oAD_failure(data)
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.declare.failure", displayName),
                        text: this.msg("message.declare.failure.more"),
                        buttons: [
                        {
                           text: this.msg("actions.edit-details"),
                           handler: function RDLA_oAD_failure_editDetails()
                           {
                              window.location = editMetadataUrl;
                              this.destroy();
                           },
                           isDefault: true
                        },
                        {
                           text: this.msg("button.cancel"),
                           handler: function RDLA_oAD_failure_cancel()
                           {
                              this.destroy();
                           }
                        }]
                     });
                  },
                  scope: this
               }
            }
         });
      },

      /**
       * Destroy action.
       *
       * @method onActionDestroy
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionDestroy: function RDLA_onActionDestroy(assets)
      {
         // If "Destroy" was triggered from the documentlist assets contain an object instead of an array
         var me = this,
            noOfAssets = YAHOO.lang.isArray(assets) ? assets.length : 1,
            text;
         
         if (noOfAssets == 1)
         {
            text = this.msg("message.confirm.destroy", $html((YAHOO.lang.isArray(assets) ? assets[0].displayName : assets.displayName)));
         }
         else
         {
            text = this.msg("message.confirm.destroyMultiple", noOfAssets);             
         }

         // Show the first confirmation dialog
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.destroy.title"),
            text: text,
            buttons: [
            {
               text: this.msg("button.ok"),
               handler: function RDLA_onActionDestroy_confirm_ok()
               {
                  // Hide the first confirmation dialog
                  this.destroy();

                  // Display the second confirmation dialog
                  text = (noOfAssets == 1 ? me.msg("message.confirm2.destroy") : me.msg("message.confirm2.destroyMultiple"));
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: me.msg("message.confirm2.destroy.title"),
                     text: text,
                     buttons: [
                     {
                        text: me.msg("button.ok"),
                        handler: function RDLA_onActionDestroy_confirm2_ok()
                        {
                           // Hide the second confirmation dialog
                           this.destroy();

                           // Call the destroy action
                           me._dod5015Action("message.destroy", assets, "destroy");
                        },
                        isDefault: true
                     },
                     {
                        text: me.msg("button.cancel"),
                        handler: function RDLA_onActionDestroy_confirm2_cancel()
                        {
                           // Hide the second confirmation dialog
                           this.destroy();
                        }
                     }]
                  });

               },
               isDefault: true
            },
            {
               text: this.msg("button.cancel"),
               handler: function RDLA_onActionDestroy_confirm_cancel()
               {
                  // Hide the first confirmation dialog
                  this.destroy();
               }
            }]
         });

      },

      /**
       * Edit Disposition As Of Date action.
       *
       * @method onActionEditDispositionAsOf
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionEditDispositionAsOf: function RDLA_onActionEditDispositionAsOf(assets)
      {
         var calendarId = Alfresco.util.generateDomId(),
            asOfDate = Alfresco.util.fromExplodedJSONDate(assets.dod5015["rma:recordSearchDispositionActionAsOf"]),
            panel,
            calendar;
         
         panel = Alfresco.util.PopupManager.getUserInput(
         {
            title: this.msg("message.edit-disposition-as-of-date.title"),
            html: '<div id="' + calendarId + '"></div>',
            initialShow: false,
            okButtonText: this.msg("button.update"),
            callback:
            {
               fn: function RDLA_onActionEditDispositionAsOf_callback(unused, cal)
               {
                  this._dod5015Action("message.edit-disposition-as-of-date", assets, "editDispositionActionAsOfDate",
                  {
                     asOfDate:
                     {
                        iso8601: Alfresco.util.toISO8601(cal.getSelectedDates()[0])
                     }
                  });
               },
               scope: this
            }
         });

         var page = (asOfDate.getMonth() + 1) + "/" + asOfDate.getFullYear(),
            selected = (asOfDate.getMonth() + 1) + "/" + asOfDate.getDate() + "/" + asOfDate.getFullYear();   
         calendar = new YAHOO.widget.Calendar(calendarId,
         {
            iframe: false
         });
         calendar.cfg.setProperty("pagedate", page);
         calendar.cfg.setProperty("selected", selected);
         calendar.render();
         calendar.show();
         // Center the calendar
         Dom.setStyle(calendarId, "margin", "0 2em");
         // Only now can we set the panel button's callback reference to the calendar, as it was undefined on panel creation
         panel.cfg.getProperty("buttons")[0].handler.obj.callback.obj = calendar;
         panel.center();
         panel.show();
      },

      /**
       * Edit Hold Details action.
       *
       * @method onActionEditHoldDetails
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionEditHoldDetails: function RDLA_onActionEditHoldDetails(assets)
      {
         Alfresco.util.PopupManager.getUserInput(
         {
            title: this.msg("message.edit-hold.title"),
            text: this.msg("message.edit-hold.reason.label"),
            value: assets.dod5015["rma:holdReason"],
            okButtonText: this.msg("button.update"),
            callback:
            {
               fn: function RDLA_onActionEditHoldDetails_callback(value)
               {
                  this._dod5015Action("message.edit-hold", assets, "editHoldReason",
                  {
                     "reason": value
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Edit Review As Of Date action.
       *
       * @method onActionEditReviewAsOf
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionEditReviewAsOf: function RDLA_onActionEditReviewAsOf(assets)
      {
	if (assets.dod5015 == null) 
	{
		var asOfDate = new Date();
		nodeId = Alfresco.util.NodeRef(assets.nodeRef);

		url = Alfresco.constants.PROXY_URI
			+ "slingshot/rmsearch/rm"
			+ '?site=rm&query=(ASPECT:"rma:record" AND ASPECT:"rma:declaredRecord") AND (rma:identifier:'
			+ nodeId.id + ') AND NOT ASPECT:"rma:versionedRecord"';

		YAHOO.util.Connect.asyncRequest("GET", url, 
		{
			success : function(resp) 
			{
				item = YAHOO.lang.JSON.parse(resp.responseText);
				if (null != item.items[0].properties.rma_reviewAsOf) 
				{
					asOfDate = new Date(item.items[0].properties.rma_reviewAsOf);
					page = (asOfDate.getMonth() + 1) + "/"
					+ asOfDate.getFullYear(),
					selected = (asOfDate.getMonth() + 1) + "/"
					+ asOfDate.getDate() + "/"
					+ asOfDate.getFullYear();
					calendar.cfg.setProperty("pagedate", page);
					calendar.cfg.setProperty("selected", selected);
					calendar.render();
				}
			}
		}, null);
	} else 
	{
		var asOfDate = Alfresco.util
			.fromExplodedJSONDate(assets.dod5015["rma:reviewAsOf"]);
	}

         var calendarId = Alfresco.util.generateDomId(),
            panel,
            calendar;
         
         panel = Alfresco.util.PopupManager.getUserInput(
         {
            title: this.msg("message.edit-review-as-of-date.title"),
            html: '<div id="' + calendarId + '"></div>',
            initialShow: false,
            okButtonText: this.msg("button.update"),
            callback:
            {
               fn: function RDLA_onActionEditReviewAsOf_callback(unused, cal)
               {
                  this._dod5015Action("message.edit-review-as-of-date", assets, "editReviewAsOfDate",
                  {
                     asOfDate:
                     {
                        iso8601: Alfresco.util.toISO8601(cal.getSelectedDates()[0])
                     }
                  });
               },
               scope: this
            }
         });

         var page = (asOfDate.getMonth() + 1) + "/" + asOfDate.getFullYear(),
            selected = (asOfDate.getMonth() + 1) + "/" + asOfDate.getDate() + "/" + asOfDate.getFullYear();   
         calendar = new YAHOO.widget.Calendar(calendarId,
         {
            iframe: false
         });
         calendar.cfg.setProperty("pagedate", page);
         calendar.cfg.setProperty("selected", selected);
         calendar.render();
         calendar.show();
         // Center the calendar
         Dom.setStyle(calendarId, "margin", "0 2em");
         // Only now can we set the panel button's callback reference to the calendar, as it was undefined on panel creation
         panel.cfg.getProperty("buttons")[0].handler.obj.callback.obj = calendar;
         panel.center();
         panel.show();
      },

      /**
       * Export action.
       *
       * @method onActionExport
       * @param assets {array} Array representing one or more file(s) or folder(s) to be exported
       */
      onActionExport: function RDLA_onActionExport(assets)
      {
         // Save the nodeRefs
         var nodeRefs = [];
         for (var i = 0, ii = assets.length; i < ii; i++)
         {
            nodeRefs.push(assets[i].nodeRef);
         }

         // Open the export dialog
         if (!this.modules.exportDialog)
         {
            // Load if for the first time
            this.modules.exportDialog = new Alfresco.module.SimpleDialog(this.id + "-exportDialog").setOptions(
            {
               width: "30em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/dod5015/export",
               actionUrl: Alfresco.constants.PROXY_URI + "api/rma/admin/export",
               firstFocus: this.id + "-exportDialog-acp",
               doBeforeFormSubmit:
               {
                  fn: function RDLA_onActionExport_SimpleDialog_doBeforeFormSubmit()
                  {
                     // Close dialog now since no callback is provided since we are submitting in a hidden iframe.
                     this.modules.exportDialog.hide();
                  },
                  scope: this
               }
            });
         }

         // doBeforeDialogShow needs re-registering each time as nodeRefs array is dynamic
         this.modules.exportDialog.setOptions(
         {
            clearForm: true,
            doBeforeDialogShow:
            {
               fn: function RDLA_onActionExport_SimpleDialog_doBeforeDialogShow(p_config, p_simpleDialog, p_obj)
               {
                  // Set the hidden nodeRefs field to a comma-separated list of nodeRefs
                  Dom.get(this.id + "-exportDialog-nodeRefs").value = p_obj.join(",");
                  var failure = "window.parent.Alfresco.util.ComponentManager.get('" + this.id + "')";
                  Dom.get(this.id + "-exportDialog-failureCallbackFunction").value = failure + ".onExportFailure";
                  Dom.get(this.id + "-exportDialog-failureCallbackScope").value = failure;
               },
               obj: nodeRefs,
               scope: this
            }
         });

         this.modules.exportDialog.show();
      },


      /**
       * Called from the hidden ifram if the Export action fails.
       *
       * @method onExportFailure
       * @param error {object} Object literal describing the error
       * @param error.status.code {string} The http status code
       * @param error.status.name {string} The error name
       * @param error.status.description {string} A description of the error status
       * @param error.message {string} An error message describing the error
       */
      onExportFailure: function RDLA_onExportFailure(error)
      {
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.failure"),
            text: error.message
         });
      },

      /**
       * File Transfer Report action.
       *
       * @method onActionFileTransferReport
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionFileTransferReport: function RDLA_onActionFileTransferReport(assets)
      {
         if (!this.modules.fileTransferReport)
         {
            this.modules.fileTransferReport = new Alfresco.module.RecordsFileTransferReport(this.id + "-fileTransferReport");
         }

         this.modules.fileTransferReport.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            fileplanNodeRef: this.doclistMetadata.filePlan,
            transfer: assets
         }).showDialog();
      },

      /**
       * Freeze action.
       *
       * @method onActionFreeze
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionFreeze: function RDLA_onActionFreeze(assets)
      {
         Alfresco.util.PopupManager.getUserInput(
         {
            title: this.msg("message.freeze.title", assets.length),
            text: this.msg("message.freeze.reason"),
            okButtonText: this.msg("button.freeze.record"),
            callback:
            {
               fn: function RDLA_onActionFreeze_callback(value)
               {
                  this._dod5015Action("message.freeze", assets, "freeze",
                  {
                     "reason": value
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Open Record Folder action.
       *
       * @method onActionOpenFolder
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionOpenFolder: function RDLA_onActionOpenFolder(assets)
      {
         this._dod5015Action("message.open-folder", assets, "openRecordFolder");
      },

      /**
       * Relinquish Hold action.
       *
       * @method onActionRelinquish
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionRelinquish: function RDLA_onActionRelinquish(assets)
      {
         this._dod5015Action("message.relinquish", assets, "relinquishHold");
      },

      /**
       * Review All action.
       * Reviews all records with the given Record Folder.
       *
       * @method onActionReviewAll
       * @param assets {object} Object literal representing parent Record Folder
       */
      onActionReviewAll: function RDLA_onActionReviewAll(assets)
      {
         this._dod5015Action("message.review-all", assets, "reviewed");
      },

      /**
       * Reviewed action.
       *
       * @method onActionReviewed
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionReviewed: function RDLA_onActionReviewed(assets)
      {
         this._dod5015Action("message.reviewed", assets, "reviewed");
      },

      /**
       * Set Record Type
       *
       * @method onActionSetRecordType
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionSetRecordType: function RDLA_onActionSetRecordType(assets)
      {
         // Open the set record type dialog
         var setRecordTypeWebscriptUrl = Alfresco.constants.PROXY_URI + "slingshot/doclib/action/aspects/node/" + assets.nodeRef.replace(":/", "");
         if (!this.modules.setRecordTypeDialog)
         {
            // Load if for the first time
            this.modules.setRecordTypeDialog = new Alfresco.module.SimpleDialog(this.id + "-setRecordTypeDialog").setOptions(
            {
               width: "30em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/dod5015/set-record-type",
               actionUrl: setRecordTypeWebscriptUrl,
               firstFocus: this.id + "-setRecordTypeDialog-recordType",
               onSuccess:
               {
                  fn: function RDLA_onActionSetRecordType_SimpleDialog_success(response)
                  {
                     // Fire event so compnents on page are refreshed
                     YAHOO.Bubbling.fire("metadataRefresh");
                  }
               }
            });
         }
         else
         {
            // Open the set record type dialog again
            this.modules.setRecordTypeDialog.setOptions(
            {
               actionUrl: setRecordTypeWebscriptUrl,
               clearForm: true
            });
         }
         this.modules.setRecordTypeDialog.show();
      },

      /**
       * Split email record action.
       *
       * @method onActionSplitEmail
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionSplitEmail: function RDLA_onActionSplitEmail(assets)
      {
         this._dod5015Action("message.split-email", assets, "splitEmail");
      },

      /**
       * Transfer action.
       *
       * @method onActionTransfer
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionTransfer: function RDLA_onActionTransfer(assets)
      {
         this._dod5015Action("message.transfer", assets, "transfer", null,
         {
            success:
            {
               callback:
               {
                  fn: this._transferAccessionComplete,
                  obj:
                  {
                     displayName: YAHOO.lang.isArray(assets) ? this.msg("message.multi-select", assets.length) : $html(assets.displayName)
                  },
                  scope: this
               }
            }
         });
      },

      /**
       * Transfer Complete action.
       *
       * @method onActionTransferComplete
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionTransferComplete: function RDLA_onActionTransferComplete(assets)
      {
         this._dod5015Action("message.transfer-complete", assets, "transferComplete");
      },

      /**
       * Undeclare record.
       *
       * @method onActionUndeclare
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionUndeclare: function RDLA_onActionUndeclare(assets)
      {
         this._dod5015Action("message.undeclare", assets, "undeclareRecord");
      },

      /**
       * Undo Cut Off action.
       *
       * @method onActionUndoCutoff
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionUndoCutoff: function RDLA_onActionUndoCutoff(assets)
      {
         this._dod5015Action("message.undo-cutoff", assets, "unCutoff");
      },

      /**
       * Unfreeze record.
       *
       * @method onActionUnfreeze
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionUnfreeze: function RDLA_onActionUnfreeze(assets)
      {
         this._dod5015Action("message.unfreeze", assets, "unfreeze");
      },

      /**
       * Unfreeze All records within Hold.
       *
       * @method onActionUnfreezeAll
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionUnfreezeAll: function RDLA_onActionUnfreezeAll(assets)
      {
         this._dod5015Action("message.unfreeze-all", assets, "unfreezeAll");
      },

      /**
       * View Audit log
       *
       * @method onActionViewAuditLog
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionViewAuditLog: function RDLA_onActionViewAuditLog(assets)
      {
         var openAuditLogWindow = function openAuditLogWindow()
         {
            return window.open(Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + '/rmaudit?nodeName='+ encodeURIComponent(assets.displayName) + '&nodeRef=' + assets.nodeRef.replace(':/',''), 'Audit_Log', 'resizable=yes,location=no,menubar=no,scrollbars=yes,status=yes,width=700,height=500');
         };
         // haven't yet opened window yet
         if (!this.fullLogWindowReference)
         {
            this.fullLogWindowReference = openAuditLogWindow.call(this);
         }
         else
         {
            // window has been opened already and is still open, so focus and reload it.
            if (!this.fullLogWindowReference.closed)
            {
               this.fullLogWindowReference.focus();
               this.fullLogWindowReference.location.reload();
            }
            //had been closed so reopen window
            else
            {
               this.fullLogWindowReference = openAuditLogWindow.call(this);
            }
         }
      },


      /**
       * Private action helper functions
       */

      /**
       * Copy/Move/File To implementation.
       *
       * @method _copyMoveFileTo
       * @param mode {String} Operation mode: copy|file|move
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       * @private
       */
      _copyMoveFileTo: function RDLA__copyMoveFileTo(mode, assets)
      {
         // Check mode is an allowed one
         if (!mode in
            {
               copy: true,
               file: true,
               move: true
            })
         {
            throw new Error("'" + mode + "' is not a valid Copy/Move/File to mode.");
         }

         if (!this.modules.copyMoveFileTo)
         {
            this.modules.copyMoveFileTo = new Alfresco.module.RecordsCopyMoveFileTo(this.id + "-copyMoveFileTo");
         }

         this.modules.copyMoveFileTo.setOptions(
         {
            mode: mode,
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: assets
         }).showDialog();
      },

      /**
       * Transfer and Accession action result processing.
       *
       * @method _transferAccessionComplete
       * @param data {object} Object literal containing ajax request and response
       * @param obj {object} Caller-supplied object
       *    <pre>
       *       obj.displayName {string} Filename or number of files submitted to the action.
       *    </pre>
       * @private
       */
      _transferAccessionComplete: function RDLA__transferAccession(data, obj)
      {
         var displayName = obj.displayName;
         
         /**
          * Transfer / Accession container query success callback.
          *
          * @method fnTransferQuerySuccess
          * @param data {object} Object literal containing ajax request and response
          * @param obj {object} Caller-supplied object
          */
         var fnTransferQuerySuccess = function RDLA_onActionTransfer_fnTransferQuerySuccess(data, obj)
         {
            // Check the transfer details to optionally show the PDF warning
            if (data.json && data.json.transfer)
            {
               var transfer = data.json.transfer,
                  fileName = transfer.name,
                  accessionIndicator = transfer["rma:transferAccessionIndicator"],
                  pdfIndicator = transfer["rma:transferPDFIndicator"];

               // If we're a Document Library, then swap to the transfers filter and highlight the newly-created transfer
               if (this.name === "Alfresco.DocumentList")
               {
                  var fnAfterUpdate = function RDLA_onActionTransfer_fnTransferQuerySuccess_fnAfterUpdate()
                  {
                     YAHOO.Bubbling.fire("highlightFile",
                     {
                        fileName: fileName
                     });

                     if (pdfIndicator)
                     {
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                           title: this.msg("message.pdf-record-fonts.title"),
                           text: this.msg(accessionIndicator ? "message.pdf-record-fonts.accession" : "message.pdf-record-fonts.transfer"),
                           icon: YAHOO.widget.SimpleDialog.ICON_WARN
                        });
                     }
                     else
                     {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("message.transfer.success", displayName)
                        });
                     }
                  };
                  this.afterDocListUpdate.push(fnAfterUpdate);
                  YAHOO.Bubbling.fire("changeFilter",
                  {
                     filterOwner: "Alfresco.DocListFilePlan",
                     filterId: "transfers"
                  });
               }
               // Otherwise, use the metadataRefresh event
               else
               {
                  YAHOO.Bubbling.fire("metadataRefresh");
                  
                  if (pdfIndicator)
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.pdf-record-fonts.title"),
                        text: this.msg(accessionIndicator ? "message.pdf-record-fonts.accession" : "message.pdf-record-fonts.transfer"),
                        icon: YAHOO.widget.SimpleDialog.ICON_WARN
                     });
                  }
                  else
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.transfer.success", displayName)
                     });
                  }
               }
            }
         };
         
         /**
          * Transfer / Accession container query failure callback.
          *
          * @method fnTransferQueryFailure
          * @param data {object} Object literal containing ajax request and response
          * @param obj {object} Caller-supplied object
          */
         var fnTransferQueryFailure = function RDLA_onActionTransfer_fnTransferQueryFailure(data, obj)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("message.pdf-record-fonts.title"),
               text: this.msg("message.pdf-record-fonts.unknown"),
               icon: YAHOO.widget.SimpleDialog.ICON_WARN
            });
         };

         // Extract the transfer container nodeRef to query it's properties
         if (data.json && data.json.results)
         {
            // Grab the resulting transfer container nodeRef
            var dataObj = data.config.dataObj,
               nodeRef = YAHOO.lang.isArray(dataObj.nodeRefs) ? dataObj.nodeRefs[0] : dataObj.nodeRef,
               transfer = new Alfresco.util.NodeRef(data.json.results[nodeRef]);
            
            // Now query the transfer nodeRef, looking for the rma:transferPDFIndicator flag
            Alfresco.util.Ajax.jsonGet(
            {
               url: Alfresco.constants.PROXY_URI + "slingshot/doclib/dod5015/transfer/node/" + transfer.uri,
               successCallback:
               {
                  fn: fnTransferQuerySuccess,
                  scope: this
               },
               failureCallback:
               {
                  fn: fnTransferQueryFailure,
                  scope: this
               }
            });
         }
      },

      /**
       * DOD5015 action.
       *
       * @method _dod5015Action
       * @param i18n {string} Will be appended with ".success" or ".failure" depending on action outcome
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       * @param actionName {string} Name of repository action to run
       * @param actionParams {object} Optional object literal to pass parameters to the action
       * @param configOverride {object} Optional object literal to override default configuration parameters
       * @private
       */
      _dod5015Action: function RDLA__dod5015Action(i18n, assets, actionName, actionParams, configOverride)
      {
         var displayName = "",
            dataObj =
            {
               name: actionName
            };

         if (YAHOO.lang.isArray(assets))
         {
            displayName = this.msg("message.multi-select", assets.length);
            dataObj.nodeRefs = [];
            for (var i = 0, ii = assets.length; i < ii; i++)
            {
               dataObj.nodeRefs.push(assets[i].nodeRef);
            }
         }
         else
         {
            displayName = assets.displayName;
            dataObj.nodeRef = assets.nodeRef;
         }

         if (YAHOO.lang.isObject(actionParams))
         {
            dataObj.params = actionParams;
         }
         
         var config =
         {
            success:
            {
               event:
               {
                  name: "metadataRefresh"
               },
               message: this.msg(i18n + ".success", displayName)
            },
            failure:
            {
               message: this.msg(i18n + ".failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               stem: Alfresco.constants.PROXY_URI + "api/rma/actions/",
               name: "ExecutionQueue"
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj: dataObj
            }
         };
         
         if (YAHOO.lang.isObject(configOverride))
         {
            config = YAHOO.lang.merge(config, configOverride);
         }

         this.modules.actions.genericAction(config);
      }
   };
})();