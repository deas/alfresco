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
 * @module alfresco/wrapped/DocumentListToolbar
 * @extends module:alfresco/core/WrappedShareWidget
 * @mixes dijit/_TemplatedMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/WrappedShareWidget", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/DocumentListToolbar.html"], 
        function(declare, WrappedShareWidget, _TemplatedMixin, template) {
   
   return declare([WrappedShareWidget, _TemplatedMixin], {

      /**
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * This has to be set the same as the wrapped widget in order for message to be successfully retrieved.
       * This then ensures that the correct scope is set so that the messages can be retrieved as usual. 
       * 
       * @instance
       * @type {string}
       * @default "Alfresco.DocListToolbar"
       */
      i18nScope: "Alfresco.DocListToolbar",
      
      /**
       * Specifies the properties file from the WebScript that is used to instantiate the widget. It's only necessary
       * to specify the default property file - the Dojo dependency handler will sort out the locale as necessary
       * 
       * @instance
       * @type {object[]}
       */
      i18nRequirements: [{i18nFile: "/WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/documentlibrary/toolbar.get.properties"}],
      
      /**
       * The CSS file used by the DocumentList
       * @instance
       * @type {object[]}
       */
      cssRequirements: [{cssFile:"/components/documentlibrary/toolbar.css"},
                        {cssFile:"/modules/social-publish.css"},
                        {cssFile:"/modules/cloud/cloud-auth-form.css"},
                        {cssFile:"/modules/cloud/cloud-folder-picker.css"},
                        {cssFile:"/modules/cloud/cloud-sync-status.css"},
                        {cssFile:"/modules/documentlibrary/aspects.css"},
                        {cssFile:"/modules/documentlibrary/copy-to.css"},
                        {cssFile:"/modules/documentlibrary/global-folder.css"},
                        {cssFile:"/modules/documentlibrary/permissions.css"},
                        {cssFile:"/modules/documentlibrary/site-folder.css"}],
                        
      /**
       * The JavaScript file referenced by the activities WebScript
       * @instance
       * @type {string[]}
       */
      nonAmdDependencies: ["/modules/social-publish.js",
                           "/modules/documentlibrary/global-folder.js",
                           "/modules/documentlibrary/doclib-actions.js",
                           "/modules/documentlibrary/copy-move-to.js",
                           "/modules/documentlibrary/permissions.js",
                           "/components/download/archive-and-download.js",
                           "/modules/documentlibrary/cloud-folder.js",
                           "/modules/cloud-auth.js",
                           "/components/documentlibrary/actions.js",
                           "/components/documentlibrary/toolbar.js"],
                     
      /**
       * This will be set to the YUI2 implemented toolbar, however - no DOM model is created for this
       * widget, it is only wrapped to provide access to it's action handling code. 
       * 
       * @instance
       * @type {object}
       * @default null
       */
      toolbar: null,
      
      /**
       * Creates an "Alfresco.DocListToolbar" instance. The .setOptions() function is called passing all of 
       * the default options that would normally be set via the toolbar.get.js controller. These options should
       * be passed as constructor arguments to this widget.
       * 
       * @instance
       */
      postCreate: function alfresco_wrapped_DocumentListToolbar__postCreate() {
         this.toolbar = new Alfresco.DocListToolbar(this.id, false).setOptions({
            siteId: this.siteId,
            rootNode: this.rootNode,
            hideNavBar: this.hideNavBar,
            googleDocsEnabled: this.googleDocsEnabled,
            repositoryBrowsing: this.repositoryBrowsing,
            useTitle: this.useTitle,
            syncMode: this.syncMode,
            createContentByTemplateEnabled: this.createContentByTemplateEnabled,
            createContentActions: this.createContentActions
         });
      }
   });
});