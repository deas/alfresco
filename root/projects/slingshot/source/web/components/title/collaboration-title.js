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
   Alfresco.CollaborationTitle = function(htmlId)
   {
      Alfresco.CollaborationTitle.superclass.constructor.call(this, "Alfresco.CollaborationTitle", htmlId);

      // Initialise prototype properties
      this.preferencesService = new Alfresco.service.Preferences();

      return this;
   };

   YAHOO.extend(Alfresco.CollaborationTitle, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function CollaborationTitle_onReady()
      {
         // Add event listeners. We use Dom.get() so that Event doesn't add an onAvailable() listener for non-existent elements.
         Event.on(Dom.get(this.id + "-join-link"), "click", function(e)
         {
            this.joinSite();
            Event.stopEvent(e);
         }, this, true);

         Event.on(Dom.get(this.id + "-requestJoin-link"), "click", function(e)
         {
            this.requestJoinSite();
            Event.stopEvent(e);
         }, this, true);

         // Create More menu
         this.widgets.more = new YAHOO.widget.Button(this.id + "-more",
         {
            type: "menu",
            menu: this.id + "-more-menu"
         });

         if (this.widgets.more.getMenu())
         {
            this.widgets.more.getMenu().subscribe("click", function(p_sType, p_aArgs)
            {
               var menuItem = p_aArgs[1];
               if (menuItem)
               {
                  switch (menuItem.value)
                  {
                     case "editSite":
                        Alfresco.module.getEditSiteInstance().show(
                        {
                           shortName: this.options.site
                        });
                        break;

                     case "customiseSite":
                        window.location =  Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.site + "/customise-site";
                        break;

                     case "leaveSite":
                        var me = this;
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                           title: me.msg("message.leave", me.options.siteTitle),
                           text: me.msg("message.leave-site-prompt",  me.options.siteTitle),
                           buttons:
                           [
                              {
                                 text: this.msg("button.ok"),
                                 handler: function leaveSite_onOk()
                                 {
                                    me.leaveSite();
                                    this.destroy();
                                 }
                              },
                              {
                                 text: this.msg("button.cancel"),
                                 handler: function leaveSite_onCancel()
                                 {
                                    this.destroy();
                                 },
                                 isDefault: true
                              }
                           ]
                        });
                        break;
                  }
               }
            }, this, true);
         }
      },

      /**
       * Called when the user clicks on the join site button
       *
       * @method joinSite
       */
      joinSite: function CollaborationTitle_joinSite()
      {
         var site = this.options.site,
            user = this.options.user;

         // Call site service to join current user to current site
         Alfresco.util.Ajax.jsonPut(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(site) + "/memberships",
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
               fn: function CollaborationTitle__joinSiteSuccess()
               {
                  // Reload page to make sure all new actions on the current page are available to the user
                  window.location.reload();
               },
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: this.msg("message.join-failure", user, site),
               scope: this
            }
         });

         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.joining", user, site),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Called when the user clicks on the request-to-join site button
       *
       * @method requestJoinSite
       */
      requestJoinSite: function CollaborationTitle_requestJoinSite()
      {
         var site = this.options.site,
            user = this.options.user;

         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(site) + "/invitations",
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
               obj: this.msg("message.request-join-failure", user, site),
               scope: this
            }
         });

         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.request-joining", user, site),
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

         var site = this.options.site,
            user = this.options.user;

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.success"),
            text: this.msg("message.request-join-success", user, site),
            buttons: [
            {
               text: this.msg("button.ok"),
               handler: function error_onOk()
               {
                  this.destroy();
                  // Redirect the user back to their dashboard
                  window.location = Alfresco.constants.URL_CONTEXT;
               },
               isDefault: true
            }]
         });
      },

      /**
       * Called when the user clicks on the leave site button
       *
       * @method leaveSite
       */
      leaveSite: function CollaborationTitle_leaveSite()
      {
         var site = this.options.site,
            user = this.options.user;

         // Call site service to remove user from this site
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(site) + "/memberships/" + encodeURIComponent(user),
            method: "DELETE",
            successCallback:
            {
               fn: function CollaborationTitle__leaveSiteSuccess()
               {
                  // Navigate to the default page as they are no longer a member of this site
                  window.location.href = Alfresco.constants.URL_CONTEXT;
               },
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: this.msg("message.leave-failure", user, site),
               scope: this
            }
         });

         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.leaving", user, site),
            spanClass: "wait",
            displayTime: 0
         });
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
               title: this.msg("message.failure"),
               text: message
            });
         }
      }
   });
})();