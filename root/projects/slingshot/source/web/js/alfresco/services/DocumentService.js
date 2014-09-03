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
 * Handles requests retrieve documents from the repository and publishes the details of them when they're
 * retrieved.
 * 
 * @module alfresco/services/DocumentService
 * @extends module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "service/constants/Default",
        "alfresco/core/PathUtils",
        "alfresco/core/NodeUtils",
        "dojo/_base/lang",
        "dojo/hash"],
        function(declare, AlfCore, CoreXhr, AlfConstants, PathUtils, NodeUtils, lang, hash) {
   
   return declare([AlfCore, CoreXhr, PathUtils], {
      
      /**
       * 
       * @instance
       * @param {array} args Constructor arguments
       */
      constructor: function alfresco_services_DocumentService__constructor(args) {
         lang.mixin(this, args);
         this.alfSubscribe("ALF_RETRIEVE_SINGLE_DOCUMENT_REQUEST", lang.hitch(this, "onRetrieveSingleDocumentRequest"));
         this.alfSubscribe("ALF_RETRIEVE_DOCUMENTS_REQUEST", lang.hitch(this, "onRetrieveDocumentsRequest"));
      },
      
      /**
       * Retrieves the details for a single document. This currently uses the Repository API and therefore won't collect any Share specific
       * information such as actions, etc. However this could be updated to use a new WebScript in the future.
       *
       * @instance
       * @param {object} payload The payload defining the document to retrieve the details for.
       */
      onRetrieveSingleDocumentRequest: function alfresco_services_DocumentService__onRetrieveSingleDocumentRequest(payload) {
         if (payload == null || payload.nodeRef == null)
         {
            this.alfLog("warn", "A request was made to retrieve the details of a document but no 'nodeRef' attribute was provided", payload, this);
         }
         else
         {
            var nodeRef = NodeUtils.processNodeRef(payload.nodeRef);
            targetNode = payload.nodeRef;
            targetNodeUri = nodeRef.uri;

            // Construct the URI for the request...
            var uriPart = (payload.site != null && payload.site != "") ? "{type}/site/{site}/{container}" : "{type}/node/" + targetNodeUri;
            if (payload.filter != null && payload.filter.filterId === "path")
            {
               // If a path has been provided in the filter then it is necessary to perform some special 
               // encoding. We need to ensure that the data is URI encoded, but we want to preserve the 
               // forward slashes. We also need to "double encode" all % characters because FireFox has
               // a nasty habit of decoding them *before* they've actually been posted back... this 
               // guarantees that the user will be able to bookmark valid URLs...
               var encodedPath = encodeURIComponent(payload.filter.filterData).replace(/%2F/g, "/").replace(/%25/g,"%2525");
               uriPart += this.combinePaths("/", encodedPath) + "/";
            }

            // View mode and No-cache
            var params = "?view=browse&noCache=" + new Date().getTime() + "&includeThumbnails=true";

            var alfTopic = (payload.alfResponseTopic != null) ? payload.alfResponseTopic : "ALF_RETRIEVE_SINGLE_DOCUMENT_REQUEST";
            var url = AlfConstants.URL_SERVICECONTEXT + "components/documentlibrary/data/node/" + targetNodeUri + params;
            var config = {
               alfTopic: alfTopic,
               url: url,
               method: "GET",
               callbackScope: this
            };
            this.serviceXhr(config);
         }
      },

      /**
       * Handles requests to retrieve documents. The payload should contain the following properties:
       * 
       * path
       * type
       * site
       * container
       * filter
       * page
       * pageSize
       * sortAscending
       * sortField
       * rootNode
       * 
       * @instance
       * @param {object} payload The payload published on the topic
       */
      onRetrieveDocumentsRequest: function alfresco_services_DocumentService__onRetrieveDocumentsRequest(payload) {
         
         var targetNode = "alfresco://company/home",
             targetNodeUri = "alfresco/company/home";
         if (payload.nodeRef != null && payload.nodeRef != "")
         {
            var nodeRef = NodeUtils.processNodeRef(payload.nodeRef);
            targetNode = payload.nodeRef;
            targetNodeUri = nodeRef.uri;
         }

         // Construct the URI for the request...
         var uriPart = (payload.site != null && payload.site != "") ? "{type}/site/{site}/{container}" : "{type}/node/" + targetNodeUri;
         if (payload.filter != null && payload.filter.path != null)
         {
            // If a path has been provided in the filter then it is necessary to perform some special 
            // encoding. We need to ensure that the data is URI encoded, but we want to preserve the 
            // forward slashes. We also need to "double encode" all % characters because FireFox has
            // a nasty habit of decoding them *before* they've actually been posted back... this 
            // guarantees that the user will be able to bookmark valid URLs...
            var encodedPath = encodeURIComponent(payload.filter.path).replace(/%2F/g, "/").replace(/%25/g,"%2525");
            uriPart += this.combinePaths("/", encodedPath);
         }

         // Unbelievably it is necessary to remove any trailing forward slashes otherwise the location
         // data set for each item will duplicate first element in the path !!!
         if (uriPart.lastIndexOf("/") === uriPart.length-1)
         {
            uriPart = uriPart.substring(0, uriPart.length-1);
         }

         // Build the URI stem
         var params = lang.replace(uriPart, {
            type: encodeURIComponent(payload.type),
            site: encodeURIComponent(payload.site),
            container: encodeURIComponent(payload.container)
         });

         if (payload.filter)
         {
            if (payload.filter.filter != null)
            {
               params += "?filter=" + payload.filter.filter;
            }
            else if (payload.filter.tag != null)
            {
               params += "?filter=tag&filterData=" + payload.filter.tag;
            }
            else if (payload.filter.category != null)
            {
               params += "?filter=category&filterData=" + payload.filter.category;
            }
            else
            {
               params += "?filter=path"
            }
         }
         

         // if (payload.filter != null)
         // {
         //    // Filter parameters
         //    params += "?filter=" + encodeURIComponent(payload.filter.filterId);
         //    if (payload.filter.filterData && payload.filter.filterId !== "path")
         //    {
         //       params += "&filterData=" + encodeURIComponent(payload.filter.filterData);
         //    }
         // }
         // else
         // {
         //    params += "?filter=path"
         // }

         if (payload.pageSize != null && payload.page != null)
         {
            params += "&size=" + payload.pageSize + "&pos=" + payload.page;
         }

         // Sort parameters
         params += "&sortAsc=" + payload.sortAscending + "&sortField=" + encodeURIComponent(payload.sortField);
         if (payload.site == null)
         {
            if (payload.libraryRoot != null)
            {
               params += "&libraryRoot=" + encodeURIComponent(payload.libraryRoot);
            }
            else
            {
               // Repository mode (don't resolve Site-based folders)
               params += "&libraryRoot=" + encodeURIComponent(targetNode);
            }
         }
         
         // View mode and No-cache
         params += "&view=browse&noCache=" + new Date().getTime();
         
         var alfTopic = (payload.alfResponseTopic != null) ? payload.alfResponseTopic : "ALF_RETRIEVE_DOCUMENTS_REQUEST";
         var url = AlfConstants.URL_SERVICECONTEXT + "components/documentlibrary/data/doclist/" + params;
         var config = {
            alfTopic: alfTopic,
            url: url,
            method: "GET",
            callbackScope: this
         };
         this.serviceXhr(config);
      }
   });
});