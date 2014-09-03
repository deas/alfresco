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
 *
 * @module alfresco/core/NodeUtils
 * @author Dave Draper & David Webster
 */
define(["dojo/_base/lang",
        "dojo/_base/array"],
        function(lang, array) {

   return {

      /**
       * Converts the supplied NodeRef string into an object containing its composite attributes.
       *
       * @instance
       * @param {string} nodeRef
       * @return {object}
       */
      processNodeRef: function alfresco_core_NodeUtils__processNodeRef(nodeRef) {
         try
         {
            var uri = nodeRef.replace(":/", ""),
                arr = uri.split("/");

            return (
            {
               nodeRef: nodeRef,
               storeType: arr[0],
               storeId: arr[1],
               id: arr[2],
               uri: uri,
               toString: function()
               {
                  return nodeRef;
               }
            });
         }
         catch (e)
         {
            e.message = "Invalid nodeRef: " + nodeRef;
            throw e;
         }
      },

      /**
       * This method takes an array of node objects and turns it into an array containing just the nodeRef for
       * each node in the object.
       *
       * @param {Object[]} nodes
       * @returns {Array} nodeRefArray
       */
      nodeRefArray: function alfresco_core_NodeUtils__nodeRefArray(nodes) {

         if (!lang.isArray(nodes))
         {
            this.alfLog("error", "expected an array of nodes, but didn't receive one");
         }

         var nodeRefArray = array.map(nodes, function(node){
            if (node.nodeRef) {
               return node.nodeRef;
            }
         });

         return nodeRefArray;
      }
   };
});