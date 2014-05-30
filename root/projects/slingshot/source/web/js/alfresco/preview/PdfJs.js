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
 * This module is currently a BETA
 *
 * @module alfresco/preview/PdfJs
 * @extends module:alfresco/preview/AlfDocumentPreviewPlugin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/preview/AlfDocumentPreviewPlugin", 
        "dojo/_base/lang",
        "dojo/aspect"], 
        function(declare, AlfDocumentPreviewPlugin, lang, aspect) {
   
   var AikauPdfJs = declare([AlfDocumentPreviewPlugin], {

      /**
       * Declares the dependencies on PdfJs dependencies.
       * 
       * @instance
       * @type {String[]}
       * @default ["/components/preview/PdfJs.js","/components/preview/pdfjs/compatibility.js","/components/preview/pdfjs/pdf.js","/components/preview/pdfjs/pdf.worker.js","/components/preview/spin.js"]
       */
      nonAmdDependencies: ["/js/yui-common.js",
                           "/js/alfresco.js",
                           "/components/preview/web-preview.js",
                           "/components/preview/PdfJs.js",
                           "/components/preview/pdfjs/compatibility.js",
                           "/components/preview/pdfjs/pdf.js",
                           "/components/preview/pdfjs/pdf.worker.js",
                           "/components/preview/spin.js",
                           "/yui/tabview/tabview.js"],

      /**
       * The PdfJs CSS file to include.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/AlfDocumentPreview.css"}]
       */
      cssRequirements: [{cssFile:"/components/preview/PdfJs.css"}],

      /**
       *
       * @instance
       * @param {object[]} args
       */
      constructor: function alfresco_preview_PdfJs__constructor(args) {
         lang.mixin(args);

         
         this.pages = [];
         this.pageText = [];
         this.widgets = {};
         this.documentConfig = {};
         this.wp = args.previewManager;
         // this.id = this.wp.id; // needed by Alfresco.util.createYUIButton
         // this.attributes = YAHOO.lang.merge(Alfresco.util.deepCopy(this.attributes), attributes);
         this.attributes = Alfresco.util.deepCopy(this.attributes);
      
         this.wp.options = {};
         this.wp.options.nodeRef = this.wp.nodeRef;
         this.wp.options.name = this.wp.name;
         this.wp.options.size = this.wp.size;
         this.wp.options.mimeType = this.wp.mimeType;
         this.wp.msg = this.wp.message;

         /*
          * Custom events
          */
         this.onPdfLoaded = new YAHOO.util.CustomEvent("pdfLoaded", this);
         this.onResize = new YAHOO.util.CustomEvent("resize", this);

         var _this = this;
         aspect.before(this, "display", function() {
            _this.onComponentsLoaded();
         });
         


         // this.attributes = {
         //    src: null,
         //    srcMaxSize: "2000000"
         // };
      }

   });

   
   var pt = lang.getObject("Alfresco.WebPreview.prototype.Plugins.PdfJs.prototype");
   if (pt != null)
   {
      lang.mixin(AikauPdfJs.prototype, pt);
   }
   return AikauPdfJs;
});