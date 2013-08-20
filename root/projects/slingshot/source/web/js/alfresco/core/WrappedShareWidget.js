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
 * The original purpose of this widget was to provide an easy way to wrap existing Share widgets by declaring
 * the common set of non-AMD dependencies that are always defined in any page of Share. It also processes
 * a standard FreeMarker template used by the style of WebScripts that were previously used to define
 * coarse-grained widgets up until Alfresco Share 4.1
 * 
 * @module alfresco/core/WrappedShareWidget
 * @extends dijit/_WidgetBase
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dojo/_base/lang",
        "dojo/_base/array",
        "alfresco/core/Core"],
        function(declare, _WidgetBase, lang, array, AlfCore) {
   
   return declare([_WidgetBase, AlfCore], {
      
      /**
       * This list includes all of the CSS files that would be pulled in by Share by default.
       * 
       * @instance
       * @type {Array.{cssFile: string, media: string}}
       */
      cssRequirements: [{cssFile:"/css/yui-fonts-grids.css"},
                        {cssFile:"/yui/columnbrowser/assets/columnbrowser.css"},
                        {cssFile:"/yui/columnbrowser/assets/skins/default/columnbrowser-skin.css"},
                        {cssFile:"/css/base.css"},
                        {cssFile:"/css/yui-layout.css"},
                        {cssFile:"/themes/lightTheme/presentation.css"}],
      
      /**
       * These are all the common Share JavaScript files.
       * 
       * @instance
       * @type {String[]} 
       */
      nonAmdDependencies: ["/js/log4javascript.v1.4.1.js",
                           "/yui/yahoo/yahoo-debug.js",
                           "/yui/event/event-debug.js",
                           "/yui/dom/dom-debug.js",
                           "/yui/dragdrop/dragdrop-debug.js",
                           "/yui/animation/animation-debug.js",
                           "/yui/logger/logger-debug.js",
                           "/yui/connection/connection-debug.js",
                           "/yui/element/element-debug.js",
                           "/yui/get/get-debug.js",
                           "/yui/yuiloader/yuiloader-debug.js",
                           "/yui/button/button-debug.js",
                           "/yui/container/container-debug.js",
                           "/yui/menu/menu-debug.js",
                           "/yui/json/json-debug.js",
                           "/yui/selector/selector-debug.js",
                           "/yui/datasource/datasource-debug.js",
                           "/yui/autocomplete/autocomplete-debug.js",
                           "/yui/paginator/paginator-debug.js",
                           "/yui/datatable/datatable-debug.js",
                           "/yui/history/history-debug.js",
                           "/yui/treeview/treeview-debug.js",
                           "/yui/cookie/cookie.js",
                           "/yui/uploader/uploader.js",
                           "/yui/calendar/calendar.js",
                           "/yui/resize/resize.js",
                           "/yui/yui-patch.js",
                           "/js/bubbling.v2.1.js",
                           "/js/flash/AC_OETags.js",
                           "/js/alfresco.js",
                           "/modules/editors/tiny_mce/tiny_mce.js",
                           "/modules/editors/tiny_mce.js",
                           "/modules/editors/yui_editor.js",
                           "/js/forms-runtime.js",
                           "/js/share.js",
                           "/js/lightbox.js"],
      
      /**
       * 
       * @instance
       */
      constructor: function alfresco_core_WrappedShareWidget__constructor(args) {
         declare.safeMixin(this, args);
         
         /*
          * Dojo's templatedMixin doesn't support the ability to call functions when performing token substitutions
          * on the template. The WebScript FreeMarker templates all typically call the msg() function in order to 
          * convert NLS keys into localized labels. In order to workaround the issue of needing to modify the templates
          * the following section of code will perform regular expression matching to convert all of these msg() calls
          * into the correct label. 
          * 
          * PLEASE NOTE: 
          *    This solution isn't perfect - the regular expression doesn't take into account the single quote and also
          *    doesn't take String concatenation of message keys into account. You will need to check the templates for
          *    instances of these - particularly if you find that errors are being thrown when rendering the widget.
          */
         if (this.templateString)
         {
            // This first regular expression matches a call to get a message (e.g. ${msg("menu.select")}
            var reAll = /(\${msg\(".*?"\)})/g;
            
            // This second regular expression matches both just the message request and the key from within it
            var reKey = /\${msg\("(.*?)"\)}/;
            
            // Split the original template String into tokens using the full message call. This will return
            // the template as an array where each element is either NOT a message request or IS a message request.
            // We will then test each element and if it is NOT a message request we will just append it to the
            // new template and if it IS a message request we will perform the message request using our own
            // implementation from AlfCore and output the result.
            var _this = this;
            var modifiedTemplate = "";
            var messagesList = this.templateString.split(reAll);
            array.forEach(messagesList, function(token, i) {
               if (reAll.test(token))
               {
                  var keyResults = reKey.exec(token);
                  var key = keyResults[1];
                  var message = _this.message(key);
                  modifiedTemplate = modifiedTemplate + message;
               }
               else
               {
                  modifiedTemplate = modifiedTemplate + token;
               }
            });
            
            this.templateString = modifiedTemplate;
         }
         
         this.url = {
            context : Alfresco.constants.URL_CONTEXT
         };
      }
   });
});