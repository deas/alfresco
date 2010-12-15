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
 * Document Library Selector. Allows selection of document library (and folder) of a specified site
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.SelectAuditRecordLocation
 */
(function()
{
   Alfresco.module.SelectAuditRecordLocation = function(htmlId)
   {
      Alfresco.module.SelectAuditRecordLocation.superclass.constructor.call(this, htmlId);
      
      // Re-register with our own name
      this.name = "Alfresco.module.SelectAuditRecordLocation";
      Alfresco.util.ComponentManager.reregister(this);

      return this;
   };
   
   YAHOO.extend(Alfresco.module.SelectAuditRecordLocation, Alfresco.module.DoclibSiteFolder,
   {
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.module.SelectAuditRecordLocation} returns 'this' for method chaining
       * @override
       */
      setOptions: function SARL_setOptions(obj)
      {
         return Alfresco.module.SelectAuditRecordLocation.superclass.setOptions.call(this, YAHOO.lang.merge(
         {
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/dod5015/copy-move-file-to"
         }, obj));
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Dialog OK button event handler
       *
       * @method onOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       * @override
       */
      onOK: function SARL_onOK(e, p_obj)
      {
         var node = this.widgets.treeview.getNodeByProperty("path", this.currentPath);
         YAHOO.Bubbling.fire("AuditRecordLocationSelected",
         {
            nodeRef: node.data.nodeRef
         });
         this.widgets.dialog.hide();
      },

      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       * @override
       */
       _buildTreeNodeUrl: function SARL__buildTreeNodeUrl(path)
       {
          var uriTemplate = Alfresco.constants.PROXY_URI + "slingshot/doclib/dod5015/treenode/site/{site}/{container}{path}";
          uriTemplate += "?children=" + this.options.evaluateChildFolders;

          var url = YAHOO.lang.substitute(uriTemplate,
          {
             site: encodeURIComponent(this.options.siteId),
             container: encodeURIComponent(this.options.containerId),
             path: Alfresco.util.encodeURIPath(path)
          });

          return url;
       }
   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.SelectAuditRecordLocation("null");
})();

