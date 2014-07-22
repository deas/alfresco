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
        "alfresco/core/PathUtils",
        "service/constants/Default",
        "dojo/_base/lang"], 
        function(declare, PathUtils, AlfConstants, lang) {
   
   return declare([PathUtils], {

      /**
       * Declares the dependencies on "legacy" JavaScript files that this is aliasing some functions of
       * 
       * @instance
       * @type {String[]}
       * @default ["/js/alfresco.js"]
       */
      nonAmdDependencies: ["/js/yui-common.js",
                           "/js/alfresco.js"],
      
      /**
       * Generate User Profile link
       *
       * @instance
       * @param {object} oUser Object literal container user data
       * @return {String} HTML mark-up for user profile link
       */
      generateUserLink: function alfresco_core_UrlUtils__generateUserLink(oUser) {
         if (oUser.isDeleted === true)
         {
            return '<span>' + this.message("details.user.deleted", Alfresco.util.encodeHTML(oUser.userName)) + '</span>';
         }
         return this.userProfileLink(oUser.userName, YAHOO.lang.trim(oUser.firstName + " " + oUser.lastName));
      },

      /**
       *
       * @instance
       * @param {string} userName User Name
       * @param {string} fullName Full display name. "userName" used if this param is empty or not supplied
       * @param {string} linkAttr Optional attributes to add to the <a> tag, e.g. "class"
       * @param {boolean} disableLink Optional attribute instructing the link to be disabled (ie returning a span element rather than an a href element)
       * @return {string} The populated HTML Link
       */
      userProfileLink: function alfresco_core_UrlUtils__userProfileLink(userName, fullName, linkAttr, disableLink) {
         if (!YAHOO.lang.isString(userName) || userName.length === 0)
         {
            return "";
         }

         var html = Alfresco.util.encodeHTML(YAHOO.lang.isString(fullName) && fullName.length > 0 ? fullName : userName),
               template = AlfConstants.URI_TEMPLATES["userprofilepage"],
               uri = "";

         // If the "userprofilepage" template doesn't exist or is empty, or we're in portlet mode we'll just return the user's fullName || userName
         if (disableLink || YAHOO.lang.isUndefined(template) || template.length === 0 || lang.getObject("Alfresco.constants.PORTLET"))
         {
            return '<span>' + html + '</span>';
         }

         // Generate the link
         uri = this.uriTemplate("userprofilepage", {
            userid: userName
         });

         return '<a href="' + uri + '" ' + (linkAttr || "") + '>' + html + '</a>';
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
      siteURL: function alfresco_core_UrlUtils__siteURL(pageURI, obj, absolute) {
         // return Alfresco.util.siteURL(pageURI, obj, absolute);

         return this.uriTemplate("sitepage", YAHOO.lang.merge(obj || {},
         {
            pageid: pageURI
         }), absolute);
      },
      
      /**
       * 
       * @instance
       * param {string} template
       * @param {object} obj
       * @param {boolean} absolute
       */
      uriTemplate: function alfresco_core_UrlUtils__uriTemplate(templateId, obj, absolute) {
         // Check we know about the templateId
         if (!(templateId in AlfConstants.URI_TEMPLATES))
         {
            return null;
         }
         return this.renderUriTemplate(AlfConstants.URI_TEMPLATES[templateId], obj, absolute);
      },

      /**
       * 
       * @instance
       * @param {string} template
       * @param {object} obj
       * @param {boolean} absolute
       */
      renderUriTemplate: function alfresco_core_UrlUtils__renderUriTemplate(template, obj, absolute) {
         // If a site page was requested but no {siteid} given, then use the current site or remove the missing parameter
         if (template.indexOf("{site}") !== -1)
         {
            if (obj.hasOwnProperty("site"))
            {
               // Assume passed site is correct for context.
               // Also generates site links from outside of a site context (e.g. search)
               if (!Alfresco.util.isValueSet(obj.site))
               {
                  // Not valid - remove site part of template
                  template = template.replace("/site/{site}", "");
               }
               else if (lang.getObject("Alfresco.constants.PAGECONTEXT") && Alfresco.constants.PAGECONTEXT.length > 0)
               {
                  template = template.replace("/site/{site}", "/context/" + Alfresco.constants.PAGECONTEXT);
               }
            }
            else
            {
               if (lang.getObject("Alfresco.constants.SITE") && Alfresco.constants.SITE.length > 0)
               {
                  // We're currently in a Site, so generate an in-Site link
                  obj.site = Alfresco.constants.SITE;
               }
               else if (lang.getObject("Alfresco.constants.PAGECONTEXT") && Alfresco.constants.PAGECONTEXT.length > 0)
               {
                  template = template.replace("/site/{site}", "/context/" + Alfresco.constants.PAGECONTEXT);
               }
               else
               {
                  // No current Site context, so remove from the template
                  template = template.replace("/site/{site}", "");
               }
            }
         }

         var uri = template,
               regExp = /^(http|https):\/\//;

         /**
          * NOTE: YAHOO.lang.substitute is currently somewhat broken in YUI 2.9.0
          * Specifically, strings are no longer recursively substituted, even with the new "recurse"
          * flag set to "true". See http://yuilibrary.com/projects/yui2/ticket/2529100
          */
         while (uri !== (uri = YAHOO.lang.substitute(uri, obj))){}

         if (!regExp.test(uri))
         {
            // Page context required
            uri = this.combinePaths(AlfConstants.URL_PAGECONTEXT, uri);
         }

         // Portlet scriptUrl mapping required?
         if (AlfConstants.PORTLET)
         {
            // Remove the context prefix
            if (uri.indexOf(AlfConstants.URL_CONTEXT) === 0)
            {
               uri = this.combinePaths("/", uri.substring(AlfConstants.URL_CONTEXT.length));
            }

            uri = AlfConstants.PORTLET_URL.replace("$$scriptUrl$$", encodeURIComponent(decodeURIComponent(uri.replace(/%25/g, "%2525").replace(/%26/g, "%252526"))));
         }

         // Absolute URI needs current protocol and host
         if (absolute && (uri.indexOf(location.protocol + "//") !== 0))
         {
            // Don't use combinePaths in case the PORTLET_URL encoding is fragile
            if (uri.substring(0, 1) !== "/")
            {
               uri = "/" + uri;
            }
            uri = location.protocol + "//" + location.host + uri;
         }

         return uri;
      },

      /**
       * This function is required to support "legacy" action handling within Share. 
       * 
       * @instance
       * @param {Object} record The current node to generate actions URLs for. 
       * @param {String} [siteId] The id of the current site, will be generated if missing from record.
       * @param {String} [repositoryUrl] The URL of a linked repository
       */
      getActionUrls: function alfresco_core_UrlUtils__getActionUrls(record, siteId, repositoryUrl, replicationUrlMapping) {
         var jsNode = record.node,
             nodeRef = jsNode.isLink ? jsNode.linkedNode.nodeRef : jsNode.nodeRef,
             strNodeRef = nodeRef.toString(),
             nodeRefUri = nodeRef.uri,
             contentUrl = jsNode.contentURL,
             workingCopy = record.workingCopy || {},
             recordSiteId = (record.location.site != null) ? record.location.site.name : null;

         var site = {
            site: (siteId != null) ? siteId : recordSiteId
         };
         try
         {
            actionUrls = {};
            actionUrls.downloadUrl = this.combinePaths(AlfConstants.PROXY_URI, contentUrl) + "?a=true";
            actionUrls.viewUrl =  this.combinePaths(AlfConstants.PROXY_URI, contentUrl) + "\" target=\"_blank";
            actionUrls.documentDetailsUrl = this.generatePageUrl("document-details?nodeRef=" + strNodeRef, site);
            actionUrls.folderDetailsUrl = this.generatePageUrl("folder-details?nodeRef=" + strNodeRef, site);
            actionUrls.editMetadataUrl = this.generatePageUrl("edit-metadata?nodeRef=" + strNodeRef, site);
            actionUrls.inlineEditUrl = this.generatePageUrl("inline-edit?nodeRef=" + strNodeRef, site);
            actionUrls.managePermissionsUrl = this.generatePageUrl("manage-permissions?nodeRef=" + strNodeRef, site);
            actionUrls.manageTranslationsUrl = this.generatePageUrl("manage-translations?nodeRef=" + strNodeRef, site);
            actionUrls.workingCopyUrl = this.generatePageUrl("document-details?nodeRef=" + (workingCopy.workingCopyNodeRef || strNodeRef), site);
            actionUrls.workingCopySourceUrl = this.generatePageUrl("document-details?nodeRef=" + (workingCopy.sourceNodeRef || strNodeRef), site);
            actionUrls.explorerViewUrl = this.combinePaths(repositoryUrl, "/n/showSpaceDetails/", nodeRefUri, site) + "\" target=\"_blank";
            actionUrls.cloudViewUrl = this.combinePaths(AlfConstants.URL_SERVICECONTEXT, "cloud/cloudUrl?nodeRef=" + strNodeRef);
            actionUrls.sourceRepositoryUrl = this.viewInSourceRepositoryURL(record, actionUrls) + "\" target=\"_blank";
         }
         catch (e)
         {
            this.alfLog("error", "The following error occurred generating action URLs", e, record, this);
         }
         return actionUrls;
      },
      
      /**
       * Alias to [siteURL]{@link module:alfresco/core/UrlUtils#siteURL}
       * 
       * @instance
       * @param {String} 
       * @param {Object[]}
       */
      generatePageUrl: function alfresco_core_UrlUtils__generatePageUrl(page, args) {
         return this.siteURL(page, args);
      },
      
      /**
       * View in source Repository URL helper
       *
       * @instance
       * @param {Object} record Object literal representing the file or folder to be actioned
       * @param {Object} actionUrls Action urls for this record
       */
      viewInSourceRepositoryURL: function alfresco_core_UrlUtils__viewInSourceRepositoryURL(record, actionUrls, replicationUrlMapping) {
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
         siteUrl = siteUrl.substring(AlfConstants.URL_CONTEXT.length);

         return this.combinePaths(urlMapping[repoId], "/", siteUrl);
      }
   })
});