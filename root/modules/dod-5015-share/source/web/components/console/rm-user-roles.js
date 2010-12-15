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
 * Admin RM Roles component
 *
 * @namespace Alfresco
 * @class Alfresco.admin.RMRoles
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Sel = YAHOO.util.Selector;

   /**
    * RM UserRoles constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.admin.RMRoles} The new component instance
    * @constructor
    */
   Alfresco.admin.RMRoles = function RMRoles_constructor(htmlId)
   {
      Alfresco.admin.RMRoles.superclass.constructor.call(this, "Alfresco.admin.RMRoles", htmlId);
      return this;
   };

   YAHOO.extend(Alfresco.admin.RMRoles, Alfresco.component.Base,
   {
      /**
       * Object representing the current user role and capabilities
       *
       * @property role
       * @type object
       */
      role: null,

      /**
       * Form Object representing the new/edit role form.
       *
       * @property roleForm
       * @type object
       */
      roleForm: null,

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function RMRoles_onReady()
      {
         this.initEvents();
         var buttons = Sel.query('button', this.id),
            button, id;

         // Create widget button while reassigning classname to src element (since YUI removes classes).
         // We need the classname so we can identify what action to take when it is interacted with (event delegation).
         for (var i=0, len = buttons.length; i<len; i++)
         {
            button = buttons[i];
            id = button.id.replace(this.id + '-', '');
            this.widgets[id] = new YAHOO.widget.Button(button.id);
            this.widgets[id]._button.className = button.className;
         }

         Event.addListener("submit", "click", this.onSubmit, this, true);

         // Form definition
         var form = new Alfresco.forms.Form("roleForm");
         form.setSubmitElements(this.widgets.submit);
         form.setShowSubmitStateDynamically(true);

         // Form field validation
         form.addValidation("roleName", Alfresco.forms.validation.mandatory, null, "keyup");

         // Initialise the form
         form.init();
         this.roleForm = form;

         // if Edit, load in the capabilities for the selected role and update checked state
         if (this.options.action === "edit")
         {
            Alfresco.util.Ajax.request(
            {
               method: Alfresco.util.Ajax.GET,
               url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmroles/" + encodeURI(this.options.roleId),
               successCallback:
               {
                  fn: this.onRoleLoaded,
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
                        text: this.msg("message.get-role-failure", json.message)
                     });
                  },
                  scope: this
               }
            });
         }
      },

      /**
       * Initialises event listening and custom events
       * @method: initEvents
       */
      initEvents: function RMRoles_initEvents()
      {
         // Requires EventProvider
         Event.on(this.id, 'click', this.onInteractionEvent, null, this);

         this.registerEventHandler('click', '.cancel',
         {
            handler: this.onCancel,
            scope: this
         });

         this.registerEventHandler('click', '.selectAll',
         {
            handler: this.onSelectAll,
            scope: this
         });

         return this;
      },

      /**
       * Capabilities for the current role - ajax handler callback
       *
       * @method onRoleLoaded
       * @param res {object} Response
       */
      onRoleLoaded: function RMRoles_onRoleLoaded(res)
      {
         var json = Alfresco.util.parseJSON(res.serverResponse.responseText),
            role = json.data;

         // update reference to the role description object
         this.role = role;

         // update UI with role name and capabilities
         var elRoleName = Dom.get("roleName");
         elRoleName.value = role.displayLabel;

         for (var i=0, j=role.capabilities.length; i<j; i++)
         {
            // checkbox representing each capabilities ID has that ID in the DOM
            Dom.get(role.capabilities[i]).checked = true;
         }

         this.roleForm.updateSubmitElements();
      },

      /**
       * Event handler for select all/deselect all button.
       * (De)Selects all relevant checkboxes
       *
       * @method onSelectAll
       * @param {e} Event object
       */
      onSelectAll: function RMRoles_onSelectAll(e, args)
      {
         var elTarget = Event.getTarget(e),
            checkedStatus = false,
            id = elTarget.id.replace('SelectAll-button', '');

         if (!Dom.hasClass(elTarget,'selected'))
         {
            checkedStatus = true;
            Dom.addClass(elTarget, 'selected');
            elTarget.innerHTML = this.msg('label.deselect-all');
         }
         else
         {
            checkedStatus = false;
            Dom.removeClass(elTarget, 'selected');
            elTarget.innerHTML = this.msg('label.select-all');
         }

         var cbs = Sel.query('input[type="checkbox"]', id + 'Capabilities');
         for (var i = 0, len = cbs.length; i < len; i++)
         {
            cbs[i].checked = checkedStatus;
         }

         Event.preventDefault(e);
      },

      /**
       * Validates forms and submits form.
       *
       * @method: onSubmit
       */
      onSubmit: function RMRoles_onSubmit(e, args)
      {
         // get the role name
         var roleName = YAHOO.lang.trim(Dom.get('roleName').value),
            roleId;

         if (this.options.action === "edit")
         {
            roleId = this.role.name;
         }
         else
         {
            // build a safe role id - replacing whitespace and encoding characters
            roleId = roleName.replace(/\s/g, "_");
         }

         // collect up an array of capability id strings
         var caps = [],
            fields = Sel.query('input[type="checkbox"]', this.id);

         for (var i=0, j=fields.length; i<j; i++)
         {
            if (fields[i].checked)
            {
               caps.push(fields[i].id);
            }
         }

         // submit form to REST API
         var obj =
         {
            name: roleId,
            displayLabel: roleName,
            capabilities: caps
         };

         if (this.options.action === "edit")
         {
            // update existing role with a PUT request
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmroles/" + encodeURI(roleId),
               method: Alfresco.util.Ajax.PUT,
               dataObj: obj,
               requestContentType: Alfresco.util.Ajax.JSON,
               successCallback:
               {
                  fn: function(res)
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.edit-success")
                     });

                     // refresh the UI
                     window.location.href = window.location.pathname + '?roleId=' + encodeURI(roleId);
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
                        text: this.msg("message.edit-failure", json.message)
                     });
                  },
                  scope: this
               }
            });
         }
         else
         {
            // create new role with a POST request
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmroles",
               method: Alfresco.util.Ajax.POST,
               dataObj: obj,
               requestContentType: Alfresco.util.Ajax.JSON,
               successCallback:
               {
                  fn: function(res)
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.create-success")
                     });

                     // refresh the UI
                     window.location.href = window.location.pathname + '?roleId=' + encodeURI(roleId);
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
                        text: this.msg("message.create-failure", json.message)
                     });
                  },
                  scope: this
               }
            });
         }
         Event.preventDefault(e);
      },

      /**
       * Cancel button handler
       * @method: onCancel
       */
      onCancel: function RMRoles_onCancel(e, args)
      {
         window.location.href = window.location.pathname + '?roleId=' + encodeURI(this.options.roleId || "");
         Event.preventDefault(e);
      }
   });
})();


/**
 * RM View Roles component
 *
 * @namespace Alfresco
 * @class Alfresco.admin.RMViewRoles
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Sel = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * RM Roles component constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.admin.RMViewRoles} The new component instance
    * @constructor
    */
   Alfresco.admin.RMViewRoles = function RMViewRoles_constructor(htmlId)
   {
      Alfresco.admin.RMViewRoles.superclass.constructor.call(this, "Alfresco.admin.RMViewRoles", htmlId);
      return this;
   };

   YAHOO.extend(Alfresco.admin.RMViewRoles, Alfresco.component.Base,
   {
      /**
       * Object representing the list of user roles and capabilities
       *
       * @property roles
       * @type object
       */
      roles: null,

      /**
       * Initialises event listening and custom events
       * @method: initEvents
       */
      initEvents: function RMViewRoles_initEvents()
      {
         Event.on(this.id, 'click', this.onInteractionEvent, null, this);

         this.registerEventHandler('click','button#newRole-button',
         {
            handler: this.onNewRole,
            scope: this
         });

         this.registerEventHandler('click','button#editRole-button',
         {
            handler: this.onEditRole,
            scope: this
         });

         this.registerEventHandler('click','button#deleteRole-button',
         {
            handler: this.onDeleteRole,
            scope: this
         });

         this.registerEventHandler('click','.role',
         {
            handler: this.onRoleSelect,
            scope: this
         });

         return this;
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function RMViewRoles_onReady()
      {
         this.initEvents();
         var buttons = Sel.query('button',this.id),
            button, id;

         // Create widget button while reassigning classname to src element (since YUI removes classes).
         // We need the classname so we can identify what action to take when it is interacted with (event delegation).
         for (var i = 0, len = buttons.length; i < len; i++)
         {
            button = buttons[i];
            id = button.id.replace(this.id + '-', '');
            this.widgets[id] = new YAHOO.widget.Button(button.id);
            this.widgets[id]._button.className = button.className;
         }

         // well known buttons - set the initial state
         this.widgets.editRole.set("disabled", true);
         this.widgets.deleteRole.set("disabled", true);

         // query the list of roles and capabilities to populate the roles list
         this.updateRolesList();
      },

      /**
       * Query the list of roles and capabilities to populate the roles list.
       *
       * @method updateRolesList
       */
      updateRolesList: function RMViewRoles_updateRolesList()
      {
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.GET,
            url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmroles",
            successCallback:
            {
               fn: this.onRolesLoaded,
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
                     text: this.msg("message.get-roles-failure", json.message)
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Roles and capabilities for the role - ajax handler callback
       *
       * @method onRolesLoaded
       * @param res {object} Response
       */
      onRolesLoaded: function RMViewRoles_onRolesLoaded(res)
      {
         var json = Alfresco.util.parseJSON(res.serverResponse.responseText);

         // update reference to the roles description object
         this.roles = json.data;

         // copy the roles data into an array to sort it
         var sortedRoles = [],
            roles = json.data;

         for (var roleName in roles)
         {
            if (roles.hasOwnProperty(roleName))
            {
               sortedRoles.push(roles[roleName]);
            }
         }
         sortedRoles.sort(this._sortByDisplayLabel);

         // update UI with role list
         var elRolesDiv = Dom.get("roles"),
            elRolesList = Dom.getFirstChild(elRolesDiv);

         elRolesList.innerHTML = "";

         var firstRoleId = null;
         for (var i in sortedRoles)
         {
            if (sortedRoles.hasOwnProperty(i))
            {
               var role = sortedRoles[i];

               if (firstRoleId === null)
               {
                  firstRoleId = role.name;
               }

               // create each list item
               var li = document.createElement("li");

               // and each inner link html
               li.innerHTML = '<a href="#" id="role-' + $html(role.name) + '" class="role">' + $html(role.displayLabel) + '</a>';

               // add and to the DOM
               elRolesList.appendChild(li);
            }
         }

         // update the selected role item - may have been set in the options, else show first in the list
         var roleId = null;
         if (this.options.selectedRoleId && this.options.selectedRoleId.length !== 0)
         {
            roleId = this.options.selectedRoleId;
         }
         else if (firstRoleId !== null)
         {
            roleId = firstRoleId;
         }
         this.updateSelectedRoleUI(roleId);
      },

      /**
       * Event handler for role selection
       * @method onRoleSelect
       * @param {e} Event object
       */
      onRoleSelect: function RMViewRoles_onRoleSelect(e)
      {
         var el = Event.getTarget(e);

         // get the ID of the element - in the format "role-roleId" and extract the roleId value
         var roleId = el.id.substring(5);
         this.updateSelectedRoleUI(roleId);
         Event.stopEvent(e);
      },

      /**
       * Helper to update the capabilities list UI based on selected Role ID.
       *
       * @method updateSelectedRoleUI
       * @param {roleId} Role ID to update for, null to empty the list
       */
      updateSelectedRoleUI: function RMViewRoles_updateSelectedRoleUI(roleId)
      {
         // update selected item background
         var roleLinks = Dom.getElementsByClassName("role", "a");
         for (var r in roleLinks)
         {
            if (roleLinks.hasOwnProperty(r))
            {
               // role link ID is in the format "role-roleId"
               var roleLinkId = roleLinks[r].id,
                  liParent = Dom.get(roleLinkId).parentNode;

               if (roleLinkId.substring(5) === roleId)
               {
                  // found item to selected
                  Dom.addClass(liParent, "selected");
               }
               else
               {
                  // deselect previously selected item
                  Dom.removeClass(liParent, "selected");
               }
            }
         }

         // clear the capabilities list
         var elList = Dom.get("capabilities-list");
         elList.innerHTML = "";

         // display the query capabilities for the selected user role if any
         if (roleId !== null)
         {
            var caps = this.roles[roleId].capabilities;
            caps.sort();
            for (var c in caps)
            {
               if (caps.hasOwnProperty(c))
               {
                  var li = document.createElement("li");
                  li.innerHTML = this.msg("label.role." + caps[c]);
                  elList.appendChild(li);
               }
            }

            // update button values to the current role ID
            this.widgets.editRole.set("value", roleId);
            this.widgets.deleteRole.set("value", roleId);
         }

         // update button state
         this.widgets.editRole.set("disabled", (roleId === null));
         this.widgets.deleteRole.set("disabled", (roleId === null));
      },

      /**
       * Event handler for new role button
       * @method onNewRole
       * @param {e} Event object
       */
      onNewRole: function RMViewRoles_onNewRole(e)
      {
         window.location.href = window.location.pathname + '?action=new';
      },

      /**
       * Event handler for edit role button
       * @method onEditRole
       * @param {e} Event object
       */
      onEditRole: function RMViewRoles_onEditRole(e)
      {
         var el = Event.getTarget(e);

         // Get roleId from button value
         var roleId = this.widgets[el.id.replace('-button', '')].get('value'),
            url = window.location.pathname + '?action=edit&roleId=' + encodeURI(roleId);

         window.location = url;
      },

      /**
       * Event handler for delete role button
       * @method onDeleteRole
       * @param {e} Event object
       */
      onDeleteRole: function RMViewRoles_onDeleteRole(e)
      {
         var el = Event.getTarget(e),
            performDelete = this.performDelete,
            me = this,
            roleId = this.widgets[el.id.replace('-button', '')].get("value");

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg('label.confirm-delete-title'),
            text: this.msg('label.confirm-delete-message'),
            modal: true,
            close: true,
            buttons:
            [
               {
                  text: this.msg("button.ok"),
                  handler: function RMViewRoles_onDeleteRole_ok()
                  {
                     this.destroy();
                     performDelete.call(me, roleId);
                  }
               },
               {
                  text: this.msg("button.cancel"),
                  handler: function RMViewRoles_onDeleteRole_cancel()
                  {
                     this.destroy();
                  }
               }
            ]
        });
      },

      /**
       * Method that calls the REST API to delete role.
       *
       * @method performDelete
       * @param {roleId} role id
       */
      performDelete: function RMViewRoles_performDelete(roleId)
      {
         // execute ajax request to delete role
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmroles/" + encodeURI(roleId),
            method: Alfresco.util.Ajax.DELETE,
            responseContentType: "application/json",
            successMessage: this.msg("message.delete.success"),
            successCallback:
            {
               fn: function(res)
               {
                  // update the UI on successful delete
                  this.updateRolesList();
               },
               scope: this
            },
            failureMessage: this.msg("message.delete.failure")
         });
      },

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Helper to Array.sort() by the 'displayLabel' field of an object.
       *
       * @method _sortByDisplayLabel
       * @return {Number}
       * @private
       */
      _sortByDisplayLabel: function RMViewRoles__sortByDisplayLabel(s1, s2)
      {
         var ss1 = s1.displayLabel.toLowerCase(), ss2 = s2.displayLabel.toLowerCase();
         return (ss1 > ss2) ? 1 : (ss1 < ss2) ? -1 : 0;
      }
   });
})();