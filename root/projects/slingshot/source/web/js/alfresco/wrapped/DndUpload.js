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
 * @module alfresco/wrapped/DndUpload
 * @extends module:alfresco/core/WrappedShareWidget
 * @mixes dijit/_TemplatedMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/WrappedShareWidget", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/DndUpload.html",
        "dojo/dom-construct"], 
        function(declare, WrappedShareWidget, _TemplatedMixin, template, domConstruct) {
   
   return declare([WrappedShareWidget, _TemplatedMixin], {

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/AlfDialog.css"}]
       */
      cssRequirements: [{cssFile:"/components/upload/dnd-upload.css"}],
      
      /**
       * This has to be set the same as the wrapped widget in order for message to be successfully retrieved.
       * This then ensures that the correct scope is set so that the messages can be retrieved as usual.
       * 
       *  @instance
       *  @type {string}
       *  @default "Alfresco.DNDUpload"
       */
      i18nScope: "Alfresco.DNDUpload",
      
      /**
       * Specifies the properties file from the WebScript that is used to instantiate the widget. It's only necessary
       * to specify the default property file - the Dojo dependency handler will sort out the locale as necessary
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "/WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/upload/dnd-upload.get.properties""}]
       */
      i18nRequirements: [{i18nFile: "/WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/upload/dnd-upload.get.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * The JavaScript file referenced by the activities WebScript
       * 
       * @instance
       * @type {string[]}
       * @default ["/components/upload/file-upload.js","/components/upload/dnd-upload.js"]
       */
      nonAmdDependencies: ["/components/upload/file-upload.js",
                           "/components/upload/dnd-upload.js"],
      
      /**
       * @instance
       * @type {object}
       * @default null
       */
      templateMessages: null,
      
      /**
       * The constructor is extended so that we can construct an object containing all the i18n properties to 
       * be substituted by the template. This is because the template can't call functions to obtain data.
       * There are potentially better ways of doing this - but it does at least work.
       * 
       * @instance
       */
      constructor: function alfresco_wrapped_DndUpload__constructor(args) {
         declare.safeMixin(this, args);
         
         this.templateMessages = {
            button_selectFiles: this.message("button.selectFiles"),
            section_version: this.message("section.version"),
            label_version: this.message("label.version"),  
            label_minorVersion: this.message("label.minorVersion"), 
            label_majorVersion: this.message("label.majorVersion"),
            label_comments: this.message("label.comments"),
            button_upload: this.message("button.upload"),
            button_cancel: this.message("button.cancel")
         };
      },
      
      /**
       * Creates an Alfresco.DNDUpload instance.
       * 
       * @instance
       */
      postCreate: function alfresco_wrapped_DndUpload__postCreate() {
         new Alfresco.DNDUpload(this.id).setOptions({});
      }
   });
});