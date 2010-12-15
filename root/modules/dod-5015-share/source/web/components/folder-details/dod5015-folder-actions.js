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
 * Folder actions component - DOD5015 extensions.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsFolderActions
 */
(function()
{
   /**
    * RecordsFolderActions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsFolderActions} The new RecordsFolderActions instance
    * @constructor
    */
   Alfresco.RecordsFolderActions = function(htmlId)
   {
      return Alfresco.RecordsFolderActions.superclass.constructor.call(this, htmlId);
   }
   
   /**
    * Extend from Alfresco.FolderActions
    */
   YAHOO.extend(Alfresco.RecordsFolderActions, Alfresco.FolderActions);
   
   /**
    * Augment prototype with RecordActions module, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentProto(Alfresco.RecordsFolderActions, Alfresco.doclib.RecordsActions, true);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.RecordsFolderActions.prototype,
   {
      /**
       * The urls to be used when creating links in the action cell
       *
       * @method getActionUrls
       * @override
       * @param recordData {object} Object literal representing the node
       * @return {object} Object literal containing URLs to be substituted in action placeholders
       */
      getActionUrls: function RecordsFolderActions_getActionUrls(recordData)
      {
         var urlContextSite = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/",
            nodeRef = new Alfresco.util.NodeRef(recordData.nodeRef);

         return (
         {
            editMetadataUrl: urlContextSite + "edit-metadata?nodeRef=" + nodeRef.nodeRef,
            managePermissionsUrl: urlContextSite + "rmpermissions?nodeRef=" + nodeRef.nodeRef + "&itemName=" + encodeURIComponent(recordData.displayName) + "&nodeType=" + recordData.type
         });
      }
   }, true);
})();
