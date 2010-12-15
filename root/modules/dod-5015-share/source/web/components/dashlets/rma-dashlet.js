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
 * Dashboard Records Management component.
 * 
 * @namespace Alfresco
 * @class Alfresco.dashlet.RMA
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
    * Dashboard RMA constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.RMA} The new component instance
    * @constructor
    */
   Alfresco.dashlet.RMA = function RMA_constructor(htmlId)
   {
      return Alfresco.dashlet.RMA.superclass.constructor.call(this, "Alfresco.dashlet.RMA", htmlId);
   };
   
   YAHOO.extend(Alfresco.dashlet.RMA, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function RMA_onReady()
      {
         var me = this;
         
         this.widgets.feedbackMessage = null;
         
         // setup link events
         Event.on(this.id + "-create-site-link", "click", this.onCreateSite, null, this);
         Event.on(this.id + "-load-data-link", "click", this.onLoadTestData, null, this);
         Event.on(this.id + "-role-report-link", "click", this.onUserRoleReport, null, this);
      },
      
      /**
       * Create Site link click event handler
       * 
       * @method onCreateSite
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onCreateSite: function RMA_onCreateSite(e, args)
      {
         Event.stopEvent(e);
         
         if (this.widgets.feedbackMessage === null)
         {
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.creating"),
               spanClass: "wait",
               displayTime: 0
            });
            
            // call web-tier to perform site creation
            Alfresco.util.Ajax.request(
            {
               method: Alfresco.util.Ajax.GET,
               url: Alfresco.constants.URL_SERVICECONTEXT + "utils/create-rmsite?shortname=rm",
               successCallback:
               {
                  fn: function()
                  {
                     this.widgets.feedbackMessage.destroy();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.create-ok")
                     });
                     
                     // refresh UI appropriately
                     Dom.setStyle(this.id + "-create-site", "display", "none");
                     Dom.setStyle(this.id + "-display-site", "display", "block");
                     Dom.setStyle(this.id + "-load-data", "display", "block");
                     Alfresco.util.Anim.pulse(this.id + "-display-site");
                     
                     // reset feedback message - to allow another action if required
                     this.widgets.feedbackMessage = null;
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function()
                  {
                     this.widgets.feedbackMessage.destroy();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.create-fail")
                     });
                     
                     // reset feedback message - to allow another action if required
                     this.widgets.feedbackMessage = null;
                  },
                  scope: this
               }
            });
         }
      },
      
      /**
       * Load Test Data link click event handler
       * 
       * @method onLoadTestData
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onLoadTestData: function RMA_onLoadTestData(e, args)
      {
         Event.stopEvent(e);
         
         if (this.widgets.feedbackMessage === null)
         {
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.importing"),
               spanClass: "wait",
               displayTime: 0
            });
            
            // call repo-tier to perform test data import
            Alfresco.util.Ajax.request(
            {
               method: Alfresco.util.Ajax.GET,
               url: Alfresco.constants.PROXY_URI + "api/rma/bootstraptestdata?site=rm&import=true",
               successCallback:
               {
                  fn: function()
                  {
                     this.widgets.feedbackMessage.destroy();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.import-ok")
                     });
                     
                     // reset feedback message - to allow another action if required
                     this.widgets.feedbackMessage = null;
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function()
                  {
                     this.widgets.feedbackMessage.destroy();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.import-fail")
                     });
                     
                     // reset feedback message - to allow another action if required
                     this.widgets.feedbackMessage = null;
                  },
                  scope: this
               }
            });
         }
      },
      
      /**
       * User Role Report link click event handler
       * 
       * @method onUserRoleReport
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onUserRoleReport: function RMA_onUserRoleReport(e, args)
      {
      }
   });
})();