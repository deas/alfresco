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
 * Dashboard SiteLinks component.
 * 
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.SiteLinks
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Event = YAHOO.util.Event;

   /**
    * Dashboard SiteLinks constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.SiteLinks} The new component instance
    * @constructor
    */
   Alfresco.dashlet.SiteLinks = function Links_constructor(htmlId)
   {
      return Alfresco.dashlet.SiteLinks.superclass.constructor.call(this, "Alfresco.dashlet.SiteLinks", htmlId);
   };

   YAHOO.extend(Alfresco.dashlet.SiteLinks, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type {object} object literal
       */
      options:
      {
         /**
          * @property siteId
          * @type String
          * */
         siteId : ""
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function SL_onReady()
      {
         Event.on(this.id + "-createLink-button", "click", this.onCreateLinkClick, null, this);
      },

      /**
       * Fired by YUI Link when the "Create link" link is clicked
       * @method onCreateLinkClick
       * @param event {domEvent} DOM event
       */
      onCreateLinkClick: function SL_onCreateLinkButtonClick(e)
      {
         Event.stopEvent(e);
         
         window.location = Alfresco.util.uriTemplate("sitepage",
         {
            site: this.options.siteId,
            pageid: "links-linkedit"
         });
      }
   });
})();