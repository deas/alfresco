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
 * Folder info component.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderInfo
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
    * FolderInfo constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.FolderInfo} The new FolderInfo instance
    * @constructor
    */
   Alfresco.FolderInfo = function(htmlId)
   {
      Alfresco.FolderInfo.superclass.constructor.call(this, "Alfresco.FolderInfo", htmlId);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.FolderInfo, Alfresco.component.Base,
   {
      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       */
      onFolderDetailsAvailable: function FolderInfo_onFolderDetailsAvailable(layer, args)
      {
         var folderData = args[1].folderDetails,
            i, ii;
         
         // render tags values
         var tags = folderData.tags,
            tagsHtml = "";
         
         if (tags.length === 0)
         {
            tagsHtml = this.msg("label.none");
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
         
         // render permissions values
         var noPerms = this.msg("folder-info.role.None"),
            managerPerms = noPerms,
            collaboratorPerms = noPerms,
            consumerPerms = noPerms,
            everyonePerms = noPerms;
         
         var rawPerms = folderData.permissions.roles;
         for (i = 0, ii = rawPerms.length; i < ii; i++)
         {
            var permParts = rawPerms[i].split(";");
            var group = permParts[1];
            if (group.indexOf("_SiteManager") != -1)
            {
               managerPerms = this.msg("folder-info.role." + permParts[2]);
            }
            else if (group.indexOf("_SiteCollaborator") != -1)
            {
               collaboratorPerms = this.msg("folder-info.role." + permParts[2]);
            }
            else if (group.indexOf("_SiteConsumer") != -1)
            {
               consumerPerms = this.msg("folder-info.role." + permParts[2]);
            }
            else if (group === "GROUP_EVERYONE")
            {
               everyonePerms = this.msg("folder-info.role." + permParts[2]);
            }
         }
         
         Dom.get(this.id + "-perms-managers").innerHTML = $html(managerPerms);
         Dom.get(this.id + "-perms-collaborators").innerHTML = $html(collaboratorPerms);
         Dom.get(this.id + "-perms-consumers").innerHTML = $html(consumerPerms);
         Dom.get(this.id + "-perms-everyone").innerHTML = $html(everyonePerms);
      }
   });
})();
