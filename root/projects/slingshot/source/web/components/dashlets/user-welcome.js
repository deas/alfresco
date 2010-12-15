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
 
/*
 * Alfresco.dashlet.UserWelcome
 * Registers a event handler on the 'Remove Me' button to have the component remove itself.
 *
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.UserWelcome
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * UserWelcome constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.UserWelcome} The new component instance
    * @constructor
    */
   Alfresco.dashlet.UserWelcome = function UserWelcome_constructor(htmlId)
   {
      Alfresco.dashlet.UserWelcome.superclass.constructor.call(this, "Alfresco.dashlet.UserWelcome", htmlId);
      
      this.createSite = null;
      
      return this;
   }

   YAHOO.extend(Alfresco.dashlet.UserWelcome, Alfresco.component.Base,
   {
      /**
       * CreateSite module instance.
       *
       * @property createSite
       * @type Alfresco.module.CreateSite
       */
      createSite: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */ 
      onReady: function UserWelcome_onReady()
      {
         // Listen on clicks for the create site link
         Event.addListener(this.id + "-createSite-button", "click", this.onCreateSiteLinkClick, this, true);
      },

      /**
       * Fired by YUI Link when the "Create site" label is clicked
       * @method onCreateSiteLinkClick
       * @param event {domEvent} DOM event
       */
      onCreateSiteLinkClick: function UserWelcome_onCreateSiteLinkClick(event)
      {
         // Create the CreateSite module if it doesn't exist
         if (this.createSite === null)
         {
            this.createSite = Alfresco.module.getCreateSiteInstance();
         }
         // and show it
         this.createSite.show();
      }
   });
})();