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
 * This is a mixin that provides URL related utility functions.
 * 
 * @module alfresco/core/UrlUtils
 * @extends module:alfresco/core/PathUtils
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/PathUtils"], 
        function(declare, PathUtils) {
   
   return declare([PathUtils], {

      /**
       * Declares the dependencies on "legacy" JavaScript files that this is aliasing some functions of
       * 
       * @instance
       * @type {String[]}
       * @default ["/js/alfresco.js"]
       */
      nonAmdDependencies: ["/js/alfresco.js"],
      
      /**
       * Generate User Profile link
       *
       * @instance
       * @param {object} oUser Object literal container user data
       * @return {String} HTML mark-up for user profile link
       */
      generateUserLink: function(oUser) {
         if (oUser.isDeleted === true)
         {
            return '<span>' + this.message("details.user.deleted", Alfresco.util.encodeHTML(oUser.userName)) + '</span>';
         }
         return Alfresco.util.userProfileLink(oUser.userName, YAHOO.lang.trim(oUser.firstName + " " + oUser.lastName));
      },
      
      /**
       * Returns a populated URI template, given a TemplateId and an object literal
       * containing the tokens to be substituted.
       * Understands when the application is hosted in a Portal environment.
       *
       * @instance
       * @param {string} templateId URI TemplateId from web-framework configuration
       * @param {object} obj The object literal containing the token values to substitute
       * @param {boolean} absolute Whether the URL should include the protocol and host
       * @returns {string|null} The populated URI or null if templateId not found
       */
      uriTemplate: function(templateId, obj, absolute) {
         return Alfresco.util.uriTemplate(templateId, obj, absolute);
      },

      /**
       * Returns a populated URI template, given the URI template and an object literal
       * containing the tokens to be substituted.
       * Understands when the application is hosted in a Portal environment.
       *
       * @instance
       * @param {string} template URI Template to be populated
       * @param {object} obj The object literal containing the token values to substitute
       * @param {boolean} absolute Whether the URL should include the protocol and host
       * @returns {string|null} The populated URI or null if templateId not found
       */
      renderUriTemplate: function(template, obj, absolute) {
         return Alfresco.util.renderUriTemplate(template, obj, absolute);
      },

      /**
       * Returns a URL to a site page.
       * If no Site ID is supplied, generates a link to the non-site page.
       *
       * @instance
       * @param {string} pageURI Page ID and and QueryString parameters the page might need, e.g.
       * <pre>
       *    "folder-details?nodeRef=" + nodeRef
       * </pre>
       * @param {object} obj The object literal containing the token values to substitute within the template
       * @param {boolean} absolute Whether the URL should include the protocol and host
       * @returns {string} The populated URL
       */
      siteURL: function(pageURI, obj, absolute) {
         return Alfresco.util.siteURL(pageURI, obj, absolute);
      },
      
      /**
       * This function is required to support "legacy" action handling within Share. 
       * 
       * @instance
       * @param {Object} record The current node to generate actions URLs for. 
       * @param {String} siteId The id of the current site
       * @param {String} repositoryUrl The URL of a linked repository
       */
      getActionUrls: function dlA_getActionUrls(record,
                                                siteId, 
                                                repositoryUrl,
                                                replicationUrlMapping) {
         var jsNode = record.jsNode,
             nodeRef = jsNode.isLink ? jsNode.linkedNode.nodeRef : jsNode.nodeRef,
             strNodeRef = nodeRef.toString(),
             nodeRefUri = nodeRef.uri,
             contentUrl = jsNode.contentURL,
             workingCopy = record.workingCopy || {},
             recordSiteId = (record.location.site != null) ? record.location.site.name : null;

         var site = {
            siteId: (siteId != null) ? siteId : recordSiteId
         };
         actionUrls =
         {
            downloadUrl: this.combinePaths(Alfresco.constants.PROXY_URI, contentUrl) + "?a=true",
            viewUrl:  this.combinePaths(Alfresco.constants.PROXY_URI, contentUrl) + "\" target=\"_blank",
            documentDetailsUrl: this.generatePageUrl("document-details?nodeRef=" + strNodeRef, site),
            folderDetailsUrl: this.generatePageUrl("folder-details?nodeRef=" + strNodeRef, site),
            editMetadataUrl: this.generatePageUrl("edit-metadata?nodeRef=" + strNodeRef, site),
            inlineEditUrl: this.generatePageUrl("inline-edit?nodeRef=" + strNodeRef, site),
            managePermissionsUrl: this.generatePageUrl("manage-permissions?nodeRef=" + strNodeRef, site),
            manageTranslationsUrl: this.generatePageUrl("manage-translations?nodeRef=" + strNodeRef, site),
            workingCopyUrl: this.generatePageUrl("document-details?nodeRef=" + (workingCopy.workingCopyNodeRef || strNodeRef), site),
            workingCopySourceUrl: this.generatePageUrl("document-details?nodeRef=" + (workingCopy.sourceNodeRef || strNodeRef), site),
            viewGoogleDocUrl: workingCopy.googleDocUrl + "\" target=\"_blank",
            explorerViewUrl: this.combinePaths(repositoryUrl, "/n/showSpaceDetails/", nodeRefUri, site) + "\" target=\"_blank",
            cloudViewUrl: this.combinePaths(Alfresco.constants.URL_SERVICECONTEXT, "cloud/cloudUrl?nodeRef=" + strNodeRef)
         };
         
         actionUrls.sourceRepositoryUrl = this.viewInSourceRepositoryURL(record, actionUrls) + "\" target=\"_blank";
         return actionUrls;
      },
      
      /**
       * Alias to [siteURL]{@link module:alfresco/core/UrlUtils#siteURL}
       * 
       * @instance
       * @param {String} 
       * @param {Object[]}
       */
      generatePageUrl: function(page, args) {
         return this.siteURL(page, args);
      },
      
      /**
       * View in source Repository URL helper
       *
       * @instance
       * @param {Object} record Object literal representing the file or folder to be actioned
       * @param {Object} actionUrls Action urls for this record
       */
      viewInSourceRepositoryURL: function dlA_viewInSourceRepositoryURL(record, actionUrls, replicationUrlMapping) {
         var node = record.node,
            repoId = record.location.repositoryId,
            urlMapping = replicationUrlMapping,
            siteUrl;

         if (!repoId || !urlMapping || !urlMapping[repoId])
         {
            return "#";
         }

         // Generate a URL to the relevant details page
         siteUrl = node.isContainer ? actionUrls.folderDetailsUrl : actionUrls.documentDetailsUrl;
         // Strip off this webapp's context as the mapped one might be different
         siteUrl = siteUrl.substring(Alfresco.constants.URL_CONTEXT.length);

         return this.combinePaths(urlMapping[repoId], "/", siteUrl);
      }
   })
});