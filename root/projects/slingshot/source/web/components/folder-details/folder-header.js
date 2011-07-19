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
 * Folder header component.
 *
 * @namespace Alfresco
 * @class Alfresco.component.FolderHeader
 */
(function()
{
   /**
    * FolderHeader constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.FolderHeader} The new FolderHeader instance
    * @constructor
    */
   Alfresco.component.FolderHeader = function FolderHeader_constructor(htmlId)
   {
      Alfresco.component.FolderHeader.superclass.constructor.call(this, "Alfresco.component.FolderHeader", htmlId);
      YAHOO.Bubbling.on("metadataRefresh", this.doRefresh, this);
      return this;
   };

   YAHOO.extend(Alfresco.component.FolderHeader, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Reference to the current folder
          *
          * @property nodeRef
          * @type string
          */
         nodeRef: null,

         /**
          * Current siteId, if any.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Root page to create links to.
          *
          * @property rootPage
          * @type string
          * @default "documentlibrary"
          */
         rootPage: "documentlibrary",

         /**
          * Root label ID. The I18N property of the document library root container.
          *
          * @property rootLabelId
          * @type string
          * @default "path.documents"
          */
         rootLabelId: "path.documents",

         /**
          * Flag indicating whether or not to show favourite
          *
          * @property showFavourite
          * @type boolean
          * @default: true
          */
         showFavourite: true,

         /**
          * Flag indicating whether or not to show likes
          *
          * @property showLikes
          * @type boolean
          * @default: true
          */
         showLikes: true,

         /**
          * Flag indicating whether or not to show comments
          *
          * @property showComments
          * @type boolean
          * @default: true
          */
         showComments: true,

         /**
          * Object describing if the nodeRef is liked by the current user and other
          *
          * @property likes
          * @type {Object}
          */
         likes: {}
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function FolderHeader_onReady()
      {
         if (this.options.showLikes)
         {
            // Create like widget
            new Alfresco.Like(this.id + '-like').setOptions(
            {
               nodeRef: this.options.nodeRef,
               siteId: this.options.siteId,
               type: "folder",
               displayName: this.options.displayName
            }).display(this.options.likes.isLiked, this.options.likes.totalLikes);
         }

         if (this.options.showFavourite)
         {
            // Create favourite widget
            new Alfresco.Favourite(this.id + '-favourite').setOptions(
            {
               nodeRef: this.options.nodeRef,
               type: "folder"
            }).display(this.options.isFavourite);
         }
      },

      /**
       * Refresh component in response to metadataRefresh event
       *
       * @method doRefresh
       */
      doRefresh: function FolderHeader_doRefresh()
      {
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh);
         var url = 'components/folder-details/folder-header?nodeRef={nodeRef}&rootPage={rootPage}';
         url += '&rootLabelId={rootLabelId}&showFavourite={showFavourite}&showLikes={showLikes}';
         url += '&showComments={showComments}' + (this.options.siteId ? '&site={siteId}' :  '');
         this.refresh(url);
      }
   });
})();
