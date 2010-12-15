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
 * Document Library "Copy-, Move- and File-To" module for Records Management.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.RecordsCopyMoveFileTo
 */
(function()
{
   Alfresco.module.RecordsCopyMoveFileTo = function(htmlId)
   {
      Alfresco.module.DoclibSiteFolder.superclass.constructor.call(this, htmlId);
      
      // Re-register with our own name
      this.name = "Alfresco.module.RecordsCopyMoveFileTo";
      Alfresco.util.ComponentManager.reregister(this);

      // Initialise prototype properties
      this.modules.actions = new Alfresco.module.DoclibActions();
      this.pathsToExpand = [];

      return this;
   };
   
   YAHOO.extend(Alfresco.module.RecordsCopyMoveFileTo, Alfresco.module.DoclibSiteFolder,
   {
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @override
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.module.RecordsCopyMoveFileTo} returns 'this' for method chaining
       */
      setOptions: function RMCMFT_setOptions(obj)
      {
         var myOptions =
         {
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/dod5015/copy-move-file-to"
         };

         if (typeof obj.mode !== "undefined")
         {
            var dataWebScripts =
            {
               copy: "copy-to",
               move: "move-to",
               file: "add-child"
            };

            if (typeof dataWebScripts[obj.mode] == "undefined")
            {
               throw new Error("Alfresco.module.RecordsCopyMoveFileTo: Invalid mode '" + obj.mode + "'");
            }
            myOptions.dataWebScript = dataWebScripts[obj.mode];
         }
         
         return Alfresco.module.RecordsCopyMoveFileTo.superclass.setOptions.call(this, YAHOO.lang.merge(myOptions, obj));
      },

      /**
       * Gets a custom message
       *
       * @method msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @override
       */
      msg: function RMCMFT_msg(messageId)
      {
         return Alfresco.util.message.call(this, this.options.mode + "." + messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Dialog OK button event handler
       *
       * @method onOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       * @override
       */
      onOK: function RMCMFT_onOK(e, p_obj)
      {
         var files, multipleFiles = [], params, i, j;

         // Single/multi files into array of nodeRefs
         if (YAHOO.lang.isArray(this.options.files))
         {
            files = this.options.files;
         }
         else
         {
            files = [this.options.files];
         }
         for (i = 0, j = files.length; i < j; i++)
         {
            multipleFiles.push(files[i].nodeRef);
         }
         
         // Success callback function
         var fnSuccess = function RMCMFT__onOK_success(p_data)
         {
            var result,
               successCount = p_data.json.successCount,
               failureCount = p_data.json.failureCount;
            
            this.widgets.dialog.hide();

            // Did the operation succeed?
            if (!p_data.json.overallSuccess)
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.msg("message.failure")
               });
               return;
            }
            
            YAHOO.Bubbling.fire("filesMoved",
            {
               destination: this.currentPath,
               successCount: successCount,
               failureCount: failureCount
            });
            
            for (var i = 0, j = p_data.json.totalResults; i < j; i++)
            {
               result = p_data.json.results[i];
               
               if (result.success)
               {
                  YAHOO.Bubbling.fire(result.type == "folder" ? "folderMoved" : "fileMoved",
                  {
                     multiple: true,
                     nodeRef: result.nodeRef,
                     destination: this.currentPath
                  });
               }
            }
            
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.success", successCount)
            });
         };
         
         // Failure callback function
         var fnFailure = function RMCMFT__onOK_failure(p_data)
         {
            this.widgets.dialog.hide();

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.failure")
            });
         };

         // Construct webscript URI based on current viewMode
         var webscriptName = this.options.dataWebScript + "/site/{site}/{container}{path}";
         params =
         {
            site: this.options.siteId,
            container: this.options.containerId,
            path: Alfresco.util.encodeURIPath(this.selectedNode.data.path)
         };

         // Construct the data object for the genericAction call
         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: fnSuccess,
                  scope: this
               }
            },
            failure:
            {
               callback:
               {
                  fn: fnFailure,
                  scope: this
               }
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: webscriptName,
               params: params
            },
            wait:
            {
               message: this.msg("message.please-wait")
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  nodeRefs: multipleFiles
               }
            }
         });

         this.widgets.okButton.set("disabled", true);
         this.widgets.cancelButton.set("disabled", true);
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Internal show dialog function
       * @method _showDialog
       * @override
       */
      _showDialog: function RMCMFT__showDialog()
      {
         this.widgets.okButton.set("label", this.msg("button"));
         return Alfresco.module.RecordsCopyMoveFileTo.superclass._showDialog.apply(this, arguments);
      },

      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
       _buildTreeNodeUrl: function RMCMFT__buildTreeNodeUrl(path)
       {
          var uriTemplate = Alfresco.constants.PROXY_URI + "slingshot/doclib/dod5015/treenode/site/{site}/{container}{path}";
          uriTemplate += "?children=" + this.options.evaluateChildFolders;
          uriTemplate += "&max=" + this.options.maximumFolderCount;

          var url = YAHOO.lang.substitute(uriTemplate,
          {
             site: encodeURIComponent(this.options.siteId),
             container: encodeURIComponent(this.options.containerId),
             path: Alfresco.util.encodeURIPath(path)
          });

          return url;
       }
   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.RecordsCopyMoveFileTo("null");
})();
