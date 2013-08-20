/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * @module alfresco/renderers/_ItemLinkMixin
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/core/UrlUtils
 * @mixes module:alfresco/core/PathUtils
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/UrlUtils",
        "alfresco/core/PathUtils"], 
        function(declare, AlfCore, UrlUtils, PathUtils) {
   
   return declare([AlfCore, UrlUtils, PathUtils], {

      /**
       * @instance
       */
      generateFileFolderLink: function alfresco_renderers_Thumbnail__generateFileFolderLink() {
         
         var link = {
            itemLinkHref: "",
            itemLinkClass: "",
            itemLinkRelative: ""
         };
         if (this.currentItem != null && this.currentItem.node)
         {
            var jsNode = this.currentItem.jsNode;
            // This was the original if condition - i can't work out why we need to check for a current site??
//            if (jsNode.isLink && 
//                $isValueSet(scope.options.siteId) && 
//                this.currentItem.location.site && 
//                this.currentItem.location.site.name !== scope.options.siteId)
            if (jsNode.isLink && this.currentItem.location.site)
            {
               if (jsNode.isContainer)
               {
                  link.itemLinkHref = $siteURL("documentlibrary?path=" + encodeURIComponent(this.currentItem.location.path),
                  {
                     site: this.currentItem.location.site.name
                  });
               }
               else
               {
                  link.itemLinkHref = this.getActionUrls(this.currentItem, this.currentItem.location.site.name).documentDetailsUrl;
               }
            }
            else
            {
               if (jsNode.isContainer)
               {
                  if (this.currentItem.parent.isContainer)
                  {
                     // handle folder parent node
                     link.itemLinkHref = "#";
                     link.itemLinkClass = "filter-change";
                     link.itemLinkRelative = this.generatePathMarkup(this.currentItem.location);
                  }
                  else if (this.currentItem.location.path === "/")
                  {
                     // handle Repository root parent node (special store_root type - not a folder)
                     link.itemLinkHref = "#";
                     link.itemLinkClass = "filter-change";
                     link.itemLinkRelative = this.generateFilterMarkup({
                        filterId: "path",
                        filterData: $combine(this.currentItem.location.path, "")
                     });
                  }
                  else
                  {
                     // handle unknown parent node types
                     link.itemLinkHref = "#";
                  }
               }
               else
               {
                  // TODO: It'll be necessary to get the actual actionUrls - but currently it's to tangled to untangle easily
                  //var actionUrls = this.getActionUrls(this.currentItem);
                  var actionsUrls = this.getActionUrls(this.currentItem);
                  if (jsNode.isLink && jsNode.linkedNode.isContainer)
                  {
                     link.itemLinkHref = actionUrls.folderDetailsUrl;
                  }
                  else
                  {
                     link.itemLinkHref = actionUrls.documentDetailsUrl;
                  }
               }
            }
         }
         return link;
      },
      
      /**
       * @instance
       * @param {object} filter
       */
      generateFilterMarkup: function alfresco_renderers_Thumbnail__generateFilterMarkup(filter)
      {
         var filterObj = Alfresco.util.cleanBubblingObject(filter);
         return YAHOO.lang.substitute("filter={filterId}|{filterData}|{filterDisplay}", filterObj, function(p_key, p_value, p_meta)
         {
            return typeof p_value === "undefined" ? "" : window.escape(p_value);
         });
      },
      
      /**
       * @instance
       * @param {object} locn
       */
      generatePathMarkup: function alfresco_renderers_Thumbnail__generatePathMarkup(locn)
      {
         return this.generateFilterMarkup(
         {
            filterId: "path",
            filterData: this.combinePaths(locn.path, locn.file)
         });
      },
   });
});