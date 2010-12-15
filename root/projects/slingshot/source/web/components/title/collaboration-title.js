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
 * CollaborationTitle component
 *
 * The title component of a collaboration site
 *
 * @namespace Alfresco
 * @class Alfresco.CollaborationTitle
 */
(function()
{

   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * CollaborationTitle constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.CollaborationTitle} The new DocumentList instance
    * @constructor
    */
   Alfresco.CollaborationTitle = function(containerId)
   {
      this.name = "Alfresco.CollaborationTitle";
      this.id = containerId;
      this.widgets = {};

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["event"], this.onComponentsLoaded, this);
      // Initialise prototype properties
      this.preferencesService = new Alfresco.service.Preferences();
      return this;
   };

   Alfresco.CollaborationTitle.prototype =
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
          * The current user
          *
          * @property user
          * @type string
          */
         user: null,

         /**
          * The current site
          *
          * @property site
          * @type string
          */
         site: null
      },

      /**
       * Holds references to ui widgets
       *
       * @property widgets
       * @type object
       */
      widgets: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.CollaborationTitle} returns 'this' for method chaining
       */
      setOptions: function DL_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this module.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.CollaborationTitle} returns 'this' for method chaining
       */
      setMessages: function CollaborationTitle_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },


      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function CollaborationTitle_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function CollaborationTitle_onReady()
      {
         var link;

         // Add event listener for join link if present
         link = document.getElementById(this.id + "-join-link");
         if (link)
         {
            Event.addListener(link, "click", function(e, obj)
            {
               // Call join site from a scope wokring in all browsers where we have all info
               obj.thisComponent.joinSite(obj.thisComponent.options.user, obj.thisComponent.options.site);
            },
            {
               thisComponent: this
            });
         }

         // Add event listener for request-to-join link if present
         link = document.getElementById(this.id + "-requestJoin-link");
         if (link)
         {
            Event.addListener(link, "click", function(e, obj)
            {
               // Call join site from a scope wokring in all browsers where we have all info
               obj.thisComponent.requestJoinSite(obj.thisComponent.options.user, obj.thisComponent.options.site);
            },
            {
               thisComponent: this
            });
         }

         this.widgets = {};
         // Create More menu
         this.widgets.more = new YAHOO.widget.Button(this.id + "-more",
         {
            type: "menu",
            menu: this.id + "-more-menu"
         });
            if (this.widgets.more.getMenu())
            {
            this.widgets.more.getMenu().subscribe("click", function (p_sType, p_aArgs)            
            {
               var menuItem = p_aArgs[1];
               if (menuItem)
               {
                  switch(menuItem.value)
                  {
                     case "editSite":
                        Alfresco.module.getEditSiteInstance().show({shortName: this.options.site});
                        break;
                     case "customiseSite":
                        window.location =  Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.site + "/customise-site";;
                        break;
                     case "leaveSite":
                        var me = this;
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                           title: me._msg("message.leave", me.options.site),
                           text: me._msg("message.leave-site-prompt",  me.options.site),
                           buttons: 
                           [
                              {
                                 text: Alfresco.util.message("button.ok"),
                                 handler: function leaveSite_onOk()
                                 {
                                    me.leaveSite(me.options.user, me.options.site);
                                    this.destroy();
                                 },
                                 isDefault: true
                              },
                              {
                                 text: Alfresco.util.message("button.cancel"),
                                 handler: function leaveSite_onCancel()
                                 {
                                    this.destroy();
                                 },
                                 isDefault: false
                              }                        
                          ]
                        });
                        break;
                     case "addToFav":
                        this.addAsFav(this.options.site);
                        break;
                        
                  }
               }
            },this,true);
         }
      },

      /**
       * Called when the user clicks on the join site button
       *
       * @method joinSite
       * @param user {string} The user to join to the site
       * @param site {string} The site to join the user to
       */
      joinSite: function CollaborationTitle_joinSite(user, site)
      {
         // make ajax call to site service to join current user
         Alfresco.util.Ajax.jsonRequest(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + site + "/memberships/" + user,
            method: "PUT",
            dataObj:
            {
               role: "SiteConsumer",
               person:
               {
                  userName: user
               }
            },
            successCallback:
            {
               fn: this._joinSiteSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: Alfresco.util.message("message.join-failure", this.name, encodeURIComponent(this.options.user), this.options.site),
               scope: this
            }
         });

         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.joining", this.name, user, site),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Callback handler used when the current user successfully has joined the current site
       *
       * @method _joinSiteSuccess
       * @param response {object}
       */
      _joinSiteSuccess: function CollaborationTitle__joinSiteSuccess(response)
      {
         // Reload page to make sure all new actions on the current page are available to the user
         document.location.reload();
      },

      /**
       * Called when the user clicks on the request-to-join site button
       *
       * @method requestJoinSite
       * @param user {string} The user requesting to join the site
       * @param site {string} The site the user is requesting to join
       */
      requestJoinSite: function CollaborationTitle_requestJoinSite(user, site)
      {
         Alfresco.util.Ajax.jsonRequest(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + site + "/invitations",
            method: "POST",
            dataObj:
            {
               invitationType: "MODERATED",
               inviteeUserName: user,
               inviteeComments: "",
               inviteeRoleName: "SiteConsumer"
            },
            successCallback:
            {
               fn: this._requestJoinSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: Alfresco.util.message("message.request-join-failure", this.name, encodeURIComponent(this.options.user), this.options.site),
               scope: this
            }
         });

         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.request-joining", this.name, user, site),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Callback handler used when the current user successfully has requested to join the current site
       *
       * @method _requestJoinSuccess
       * @param response {object}
       */
      _requestJoinSuccess: function CollaborationTitle__requestJoinSuccess(response)
      {
         if (this.widgets.feedbackMessage)
         {
            this.widgets.feedbackMessage.destroy();
            this.widgets.feedbackMessage = null;
         }
         
         var me = this;
         
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message("message.success"),
            text: Alfresco.util.message("message.request-join-success", this.name, this.options.user, this.options.site),
            buttons: [
            {
               text: Alfresco.util.message("button.ok"),
               handler: function error_onOk()
               {
                  this.destroy();
                  // Reload user dashboard as they are no longer a member of this site
                  document.location.href = Alfresco.constants.URL_PAGECONTEXT + "user/" + me.options.user + "/dashboard";
               },
               isDefault: true
            }]
         });
      },

      /**
       * Called when the user clicks on the leave site button
       *
       * @method leaveSite
       * @param user {string} The user to join to the site
       * @param site {string} The site to join the user to
       */
      leaveSite: function CollaborationTitle_leaveSite(user, site)
      {
         // make ajax call to site service to join user
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + site + "/memberships/" + encodeURIComponent(user),
            method: "DELETE",
            successCallback:
            {
               fn: this._leaveSiteSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: Alfresco.util.message("message.leave-failure", this.name, Alfresco.util.encodeHTML(this.options.user), this.options.site),
               scope: this
            }
         });

         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.leaving", this.name, Alfresco.util.encodeHTML(user), site),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Callback handler used when the current user successfully has left the current site
       *
       * @method _leaveSiteSuccess
       * @param response {object}
       */
      _leaveSiteSuccess: function CollaborationTitle__leaveSiteSuccess(response)
      {
         // Reload user dashboard as they are no longer a member of this site
         document.location.href = Alfresco.constants.URL_PAGECONTEXT + "user/" + this.options.user + "/dashboard";
      },

      /**
       * Generic failure callback handler
       *
       * @method _failureCallback
       * @private
       * @param message {string} Display message
       */
      _failureCallback: function CollaborationTitle__failureCallback(obj, message)
      {
         if (this.widgets.feedbackMessage)
         {
            this.widgets.feedbackMessage.destroy();
            this.widgets.feedbackMessage = null;
         }

         if (message)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: Alfresco.util.message("message.failure"),
               text: message
            });
         }
      },
       /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function CollaborationTitle__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.CollaborationTitle", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();