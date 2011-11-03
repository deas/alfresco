/**
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
 * Document and Folder header component.
 *
 * @namespace Alfresco
 * @class Alfresco.component.NodeHeader
 */
(function()
{
   /**
    * NodeHeader constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.NodeHeader} The new NodeHeader instance
    * @constructor
    */
   Alfresco.component.NodeHeader = function NodeHeader_constructor(htmlId)
   {
      Alfresco.component.NodeHeader.superclass.constructor.call(this, "Alfresco.component.NodeHeader", htmlId);

      YAHOO.Bubbling.on("metadataRefresh", this.doRefresh, this);

      this.nodeType = null;

      return this;
   };

   YAHOO.extend(Alfresco.component.NodeHeader, Alfresco.component.Base,
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
          * Reference to the current document
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
          * Flag indicating whether or not to show download button
          *
          * @property showDownload
          * @type boolean
          * @default: true
          */
         showDownload: true,

         /**
          * Flag indicating whether or not to show path
          *
          * @property showPath
          * @type boolean
          * @default: true
          */
         showPath: true,

         /**
          * Object describing if the nodeRef is liked by the current user and other
          *
          * @property likes
          * @type {Object}
          */
         likes: {},

         /**
          * Whether the node we're currently viewing is a container or not
          *
          * @property isContainer
          * @type boolean
          */
        isContainer: false
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function NodeHeader_onReady()
      {
         this.nodeType = this.options.isContainer ? "folder" : "document";

         if (this.options.showLikes)
         {
            // Create like widget
            new Alfresco.Like(this.id + '-like').setOptions(
            {
               nodeRef: this.options.nodeRef,
               siteId: this.options.siteId,
               type: this.nodeType,
               displayName: this.options.displayName
            }).display(this.options.likes.isLiked, this.options.likes.totalLikes);
         }

         if (this.options.showFavourite)
         {
            // Create favourite widget
            new Alfresco.Favourite(this.id + '-favourite').setOptions(
            {
               nodeRef: this.options.nodeRef,
               type: this.nodeType
            }).display(this.options.isFavourite);
         }
      },

      /**
       * Refresh component in response to metadataRefresh event
       *
       * @method doRefresh
       */
      doRefresh: function NodeHeader_doRefresh()
      {
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);

         var url = 'components/node-details/node-header?nodeRef={nodeRef}&rootPage={rootPage}' +
            '&rootLabelId={rootLabelId}&showFavourite={showFavourite}&showLikes={showLikes}' +
            '&showComments={showComments}&showDownload={showDownload}&showPath={showPath}' +
            (this.options.siteId ? '&site={siteId}' :  '');

         this.refresh(url);
      }
   });
})();
