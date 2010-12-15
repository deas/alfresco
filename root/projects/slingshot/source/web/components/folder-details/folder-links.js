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
 * Folder links component.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderLinks
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
   var $combine = Alfresco.util.combinePaths;
   
   /**
    * FolderLinks constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.FolderLinks} The new FolderLinks instance
    * @constructor
    */
   Alfresco.FolderLinks = function(htmlId)
   {
      Alfresco.FolderLinks.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.FolderLinks";
      Alfresco.util.ComponentManager.reregister(this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.FolderLinks, Alfresco.DocumentLinks,
   {
      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       */
      onFolderDetailsAvailable: function FolderLinks_onFolderDetailsAvailable(layer, args)
      {
         var folderData = args[1].folderDetails;
         
         if (this.options.repositoryUrl)
         {
            // WebDAV URL
            this.populateLinkUI(
            {
               name: "webdav",
               url: $combine(this.options.repositoryUrl, folderData.webdavUrl)
            });
         }

         // This page
         this.populateLinkUI(
         {
            name: "page",
            url: window.location.href
         });
      }
   });
})();
