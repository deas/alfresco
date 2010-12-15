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
 * Records Search component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsPermissions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * Search constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsPermissions} The new RecordsPermissions instance
    * @constructor
    */
   Alfresco.RecordsPermissions = function(htmlId)
   {
      /* Super class constructor call */
      Alfresco.RecordsPermissions.superclass.constructor.call(
         this, "Alfresco.RecordsPermissions", htmlId,
         ["button", "container", "datasource", "datatable", "json", "menu"]);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.RecordsPermissions, Alfresco.component.Base,
   {
      /**
       * Object container for storing YUI menu instances, indexed by property name.
       * 
       * @property modifyMenus
       * @type object
       */
      modifyMenus: null,
      
      /**
       * Object container for storing YUI button instances, indexed by property name.
       * 
       * @property removeButtons
       * @type object
       */
      removeButtons: null,
      
      /**
       * Array of objects representing the permissions list as displayed.
       * Of the form:
       * {
       *    "authority": "GROUP|USERNAME",
       *    "id": "PERMISSIONID",
       *    "remove": BOOLEAN,
       *    "modified": BOOLEAN,
       *    "el": DOMELEMENT
       * }
       * 
       * @property permissions
       * @type Array
       */
      permissions: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsPermissions_onReady()
      {
         var me = this;
         
         // Buttons
         this.widgets.addButton = Alfresco.util.createYUIButton(this, "addusergroup-button", this.onAddClick);
         this.widgets.finishButton = Alfresco.util.createYUIButton(this, "finish-button", this.onFinishClick);
         
         // Load in the Authority Finder component from the server
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/authority-finder",
            dataObj:
            {
               htmlid: this.id + "-authoritypicker"
            },
            successCallback:
            {
               fn: this.onAuthorityFinderLoaded,
               scope: this
            },
            failureMessage: this.msg("message.authoritypickerfail"),
            execScripts: true
         });
         
         // initial update of the UI
         this.refreshPermissionsList();
      },
      
      /**
       * Called when the authority finder template has been loaded.
       * Creates a dialog and inserts the authority finder for choosing groups and users to add.
       *
       * @method onAuthorityFinderLoaded
       * @param response The server response
       */
      onAuthorityFinderLoaded: function RecordsPermissions_onAuthorityFinderLoaded(response)
      {
         // Inject the component from the XHR request into it's placeholder DIV element
         var finderDiv = Dom.get(this.id + "-authoritypicker");
         finderDiv.innerHTML = response.serverResponse.responseText;
         
         this.widgets.authorityFinder = finderDiv;
         
         // Find the Authority Finder by container ID
         this.modules.authorityFinder = Alfresco.util.ComponentManager.get(this.id + "-authoritypicker");
         
         // Set the correct options for our use
         this.modules.authorityFinder.setOptions(
         {
            viewMode: Alfresco.AuthorityFinder.VIEW_MODE_COMPACT,
            singleSelectMode: true,
            minSearchTermLength: 3
         });
         
         // Make sure we listen for events when the user selects an authority
         YAHOO.Bubbling.on("itemSelected", this.onAuthoritySelected, this);
      },
      
      /**
       * Authority selected event handler. This event is fired from Authority picker.
       * 
       * @method onAuthoritySelected
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onAuthoritySelected: function ViewPanelHandler_onAuthoritySelected(e, args)
      {
         // construct permission descriptor and add permission row
         var permission =
         {
            "id": "ReadRecords",
            "authority":
            {
               "id": args[1].itemName,
               "label": args[1].displayName
            }
         };
         this.addPermissionRow(permission, true);
         
         // remove authority selector popup
         Dom.removeClass(this.widgets.authorityFinder, "active");
         this.showingFilter = false;
      },
      
      /**
       * Refresh the permissions list.
       * 
       * @method refreshPermissionsList
       */
      refreshPermissionsList: function RecordsPermissions_refreshPermissionsList()
      {
         // clear the list of meta-data items
         var elPermList = Dom.get(this.id + "-list");
         elPermList.innerHTML = "";
         
         // reset widget references
         this.modifyMenus = {};
         this.removeButtons = {};
         
         // perform ajax call to get the current permissions for the node
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.GET,
            url: Alfresco.constants.PROXY_URI + "api/node/" + this.options.nodeRef.replace(":/", "") + "/rmpermissions",
            successCallback:
            {
               fn: this.onPermissionsLoaded,
               scope: this
            },
            failureCallback:
            {
               fn: function()
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.getpermissionsfail")
                  });
               },
               scope: this
            }
         });
      },
      
      /**
       * Permissions list - ajax handler callback
       *
       * @method onPermissionsLoaded
       * @param res {object} Response
       */
      onPermissionsLoaded: function RecordsPermissions_onPermissionsLoaded(res)
      {
         // clear the list of local permissions
         this.permissions = [];
         
         var json = Alfresco.util.parseJSON(res.serverResponse.responseText);
         var perms = json.data.permissions;
         
         // sort the array from the json response - alphabetically
         perms.sort(function(a, b)
         {
            return (a.authority.label > b.authority.label) ? -1 : (a.authority.label < b.authority.label) ? 1 : 0;
         });
         
         for (var i in perms)
         {
            this.addPermissionRow(perms[i], false);
         }
      },
      
      /**
       * Add a row to the list of permissions. Also updates the internal local
       * permission object list.
       * Expects a permission object descriptor:
       * {
       *    "id": "Filing",
       *    "authority":
       *    {
       *       "id": "GROUP_ALFRESCO_ADMINISTRATORS",
       *       "label": "ALFRESCO_ADMINISTRATORS"
       *    }
       * }
       * Generally provided via JSON call or created for a new permission.
       * 
       * @method addPermissionRow
       * @param permission {object} See above
       * @param created {boolean} If true then this is a newly created permission.
       */
      addPermissionRow: function RecordsPermissions_addPermissionRow(permission, created)
      {
         var me = this;
         
         // quick exit from the function if the added authority already exists as a local permission
         if (created)
         {
            for (var n in this.permissions)
            {
               var perm = this.permissions[n];
               if (perm.remove === false && perm.authority === permission.authority.id)
               {
                  return;
               }
            }
         }
         
         var elPermList = Dom.get(this.id + "-list");
         
         // build row item for the permission and controls
         var div = document.createElement("div");
         
         // construct local permission reference from current data
         var i = this.permissions.length;
         var p =
         {
            "authority": permission.authority.id,
            "id": permission.id,
            "remove": false,
            "created": created,
            "modified": false,
            "el": div
         };
         this.permissions.push(p);
         
         // dynamically generated button ids
         var modifyMenuContainerId = this.id + '-edit-' + i;
         var removeBtnContainerId  = this.id + '-remove-' + i;
         
         // messages
         var msgReadOnly = this.msg("label.readonly");
         var msgReadFile = this.msg("label.readandfile");
         
         // construct row data
         var html = '<div class="list-item"><div class="actions">';
         html += '<span id="' + removeBtnContainerId + '"></span></div><div class="controls"><span id="' + modifyMenuContainerId + '"></span>';
         html += '</div><div><span class="label">' + $html(permission.authority.label) + '</span></div></div>';
         
         div.innerHTML = html;
         
         // insert into the DOM for display
         elPermList.appendChild(div);
         
         // generate menu and buttons (NOTE: must occur after DOM insertion)
         this.modifyMenus[i] = new YAHOO.widget.Button(
         {
            type: "menu",
            container: modifyMenuContainerId,
            menu: [
               { text: msgReadOnly, value: "ReadRecords" },
               { text: msgReadFile, value: "Filing" }
            ]
         });
         // set menu button text on current permission
         this.modifyMenus[i].set("label", (permission.id === "Filing" ? msgReadFile : msgReadOnly));
         // subscribe to the menu click event
         this.modifyMenus[i].getMenu().subscribe("click", function(p_sType, p_aArgs, index)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               // update menu button text to selected item label
               me.modifyMenus[index].set("label", menuItem.cfg.getProperty("text"));
               
               // update modified permissions value and set as modified
               me.permissions[i].id = menuItem.value;
               me.permissions[i].modified = true;
            }
         }, i);
         
         this.removeButtons[i] = new YAHOO.widget.Button(
         {
            type: "button",
            label: this.msg("button.remove"),
            name: this.id + '-removeButton-' + i,
            container: removeBtnContainerId,
            onclick:
            {
               fn: this.onClickRemovePermission,
               obj: i,
               scope: this
            }
         });
      },
      
      /**
       * Remove Permission button click handler
       *
       * @method onClickRemovePermission
       * @param e {object} DomEvent
       * @param obj {object} Object passed back from addListener method
       */
      onClickRemovePermission: function RecordsPermissions_onClickRemovePermission(e, obj)
      {
         // mark as removed and clear related DOM element
         var permission = this.permissions[obj];
         permission.remove = true;
         permission.el.parentNode.removeChild(permission.el);
         permission.el = null;
      },
      
      /**
       * Fired when the Add User/Group button is clicked.
       * 
       * @method onAddClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onAddClick: function RecordsPermissions_onAddClick(e, args)
      {
         if (!this.showingFilter)
         {
            this.modules.authorityFinder.clearResults();
            Dom.addClass(this.widgets.authorityFinder, "active");
            var el = Dom.get(this.id + "-authoritypicker-search-text");
            el.focus();
            this.showingFilter = true;            
         }
         else
         {
            Dom.removeClass(this.widgets.authorityFinder, "active");
            this.showingFilter = false;
         }
      },
      
      /**
       * Fired when the Finish button is clicked.
       * 
       * @method onFinishClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onFinishClick: function RecordsPermissions_onFinishClick(e, args)
      {
         this.widgets.finishButton.set("disabled", true);
         
         var obj =
         {
            "permissions": []
         };
         
         for (var i in this.permissions)
         {
            var p = this.permissions[i];
            // we either: add newly created permissions or remove existing ones or update existing ones
            if ((p.created && p.remove === false) ||
                (p.created === false && p.remove) ||
                (p.created === false && p.modified))
            {
               // special case for "upgrading" or "downgrading" existing permissions
               if (p.created === false && p.modified)
               {
                  // first remove existing permission
                  var permission = 
                  {
                     "id": (p.id === "Filing" ? "ReadRecords": "Filing"),
                     "authority": p.authority,
                     "remove": true
                  };
                  obj.permissions.push(permission);
               }
               var permission = 
               {
                  "id": p.id,
                  "authority": p.authority,
                  "remove": p.remove
               };
               obj.permissions.push(permission);
            }
         }
         
         if (obj.permissions.length !== 0)
         {
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "api/node/" + this.options.nodeRef.replace(":/", "") + "/rmpermissions",
               method: Alfresco.util.Ajax.POST,
               dataObj: obj,
               requestContentType: Alfresco.util.Ajax.JSON,
               successCallback:
               {
                  fn: function(res)
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.finish-success")
                     });
                     
                     // return to appropriate location
                     this._navigateForward();
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function(res)
                  {
                     var json = Alfresco.util.parseJSON(res.serverResponse.responseText);
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.failure"),
                        text: this.msg("message.finish-failure", json.message)
                     });
                     this.widgets.finishButton.set("disabled", false);
                  },
                  scope: this
               }
            });
         }
         else
         {
            // return to appropriate location
            this._navigateForward();
         }
      },
      
      /**
       * Displays the corresponding return page for the current node.
       * 
       * @method _navigateForward
       * @private
       */
      _navigateForward: function RecordsPermissions__navigateForward()
      {
         // Did we come from the document library? If so, then direct the user back there
         if (document.referrer.match(/documentlibrary([?]|$)/))
         {
            history.go(-1);
         }
         else
         {
            var nodeType = this.options.nodeType;
            switch (this.options.nodeType)
            {
               case "record":
               case "undeclared-record":
               case "record-nonelec":
               case "undeclared-record-nonelec":
                  nodeType = "document";
                  break;
            }
            // go back to the appropriate details page for the node
            window.location.href = nodeType + "-details?nodeRef=" + this.options.nodeRef;
         }
      }
   });
})();