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
 * @module alfresco/renderers/SmallThumbnail
 * @extends module:alfresco/renderers/Thumbnail
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/renderers/Thumbnail"], 
        function(declare, Thumbnail) {

   return declare([Thumbnail], {
      
      /**
       * Declare the dependencies on "legacy" JS files that this is wrapping.
       * 
       * @instance
       * @type {string}
       * @default ["/js/alfresco.js"]
       */
      nonAmdDependencies: ["/js/alfresco.js"],
                           
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/SmallThumbnail.css"}]
       */
      cssRequirements: [{cssFile:"./css/SmallThumbnail.css"}],
      
      /**
       * Adds the "small" CSS classes the main DOM node defined in the template
       * @instance
       * @type {string}
       * @default "small"
       */
      customClasses: "small",
      
      /**
       * Generates the URL to use as the source of the thumbnail.
       * 
       * @instance
       * @param {string} renditionName
       * @returns {string}
       */
      generateThumbnailUrl: function DL_generateThumbnailUrl(renditionName) {
         var url,
             jsNode = this.currentItem.jsNode;
         if (jsNode.isContainer || (jsNode.isLink && jsNode.linkedNode.isContainer))
         {
            url = Alfresco.constants.URL_RESCONTEXT + "components/documentlibrary/images/folder-32.png";
            // TODO: DnD
         }
         else
         {
            url = Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + Alfresco.util.getFileIcon(this.currentItem.fileName);
            // TODO: Preview
         }
         return url;
      }
   });
});