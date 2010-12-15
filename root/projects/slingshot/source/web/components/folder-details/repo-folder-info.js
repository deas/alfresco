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
 * Repository Folder info component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RepositoryFolderInfo
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * RepositoryFolderInfo constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RepositoryFolderInfo} The new RepositoryFolderInfo instance
    * @constructor
    */
   Alfresco.RepositoryFolderInfo = function(htmlId)
   {
      return Alfresco.RepositoryFolderInfo.superclass.constructor.call(this, htmlId);
   };
   
   YAHOO.extend(Alfresco.RepositoryFolderInfo, Alfresco.FolderInfo,
   {
      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       *
       * @override
       * @method: onFolderDetailsAvailable
       */
      onFolderDetailsAvailable: function RepositoryFolderInfo_onFolderDetailsAvailable(layer, args)
      {
         var folderData = args[1].folderDetails;
         
         // render tags values
         var tags = folderData.tags,
            tagsHtml = "",
            i, ii;
         
         if (tags.length === 0)
         {
            tagsHtml = Alfresco.util.message("label.none", this.name);
         }
         else
         {
            for (i = 0, ii = tags.length; i < ii; i++)
            {
               tagsHtml += '<div class="tag"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/tag-16.png" />';
               tagsHtml += $html(tags[i]) + '</div>';
            }
         }
         
         Dom.get(this.id + "-tags").innerHTML = tagsHtml;
      }
   });
})();
