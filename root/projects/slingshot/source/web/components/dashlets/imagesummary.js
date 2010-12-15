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
 * Dashboard Image Summary component.
 * 
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.ImageSummary
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Event = YAHOO.util.Event;

   /**
    * Dashboard ImageSummary constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.ImageSummary} The new component instance
    * @constructor
    */
   Alfresco.dashlet.ImageSummary = function ImageSummary_constructor(htmlId)
   {
      Alfresco.dashlet.ImageSummary.superclass.constructor.call(this, "Alfresco.dashlet.ImageSummary", htmlId);
      
      this.itemsPerRow = 0;
      Event.addListener(window, 'resize', this.resizeThumbnailList, this, true);
      
      return this;
   };

   YAHOO.extend(Alfresco.dashlet.ImageSummary, Alfresco.component.Base,
   {
      /**
       * Keep track of thumbnail items per row - so don't resize unless actually required
       * 
       * @property itemsPerRow
       * @type integer
       */
      itemsPerRow: null,
      
      /**
       * Fired on window resize event.
       * 
       * @method resizeThumbnailList
       * @param e {object} the event source
       */
      resizeThumbnailList: function resizeThumbnailList(e)
      {
         // calculate number of thumbnails we can display across the dashlet width
         var listDiv = Dom.get(this.id + "-list");
         var count = Math.floor((listDiv.clientWidth - 16) / 112);
         if (count == 0) count = 1;
         
         if (count !== this.itemsPerRow)
         {
            this.itemsPerRow = count;
            var items = Dom.getElementsByClassName("item", null, listDiv);
            for (var i=0, j=items.length; i<j; i++)
            {
               if (i % count == 0)
               {
                  // initial item for the current row
                  Dom.addClass(items[i], "initial");
               }
               else
               {
                  Dom.removeClass(items[i], "initial");
               }
            }
         }
      }
   });
})();