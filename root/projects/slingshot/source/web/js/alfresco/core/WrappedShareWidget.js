define(["dojo/_base/declare",
        "alfresco/core/WrappedThirdPartyWidget", 
        "dojo/_base/lang",
        "dojo/_base/array"],
        function(declare, WrappedThirdPartyWidget, lang, array) {
   
   return declare([WrappedThirdPartyWidget], {
      
      /**
       * This list includes all of the CSS files that would be pulled in by Share by default.
       */
      cssRequirements: [{cssFile:"../../../../css/yui-fonts-grids.css"},
                        {cssFile:"../../../../yui/columnbrowser/assets/columnbrowser.css"},
                        {cssFile:"../../../../yui/columnbrowser/assets/skins/default/columnbrowser-skin.css"},
                        {cssFile:"../../../../yui/assets/skins/default/skin.css"},
                        {cssFile:"../../../../css/base.css"},
                        {cssFile:"../../../../css/yui-layout.css"},
                        {cssFile:"../../../../themes/default/presentation.css"}],
      /**
       * These are the two global namespace objects that are required. These could be abstracted to a "wrapped
       * Share widget" class.
       */
      globalObj: ["YAHOO","Alfresco"],
      
      /**
       * These are all the common Share JavaScript files.
       * TODO: We also need to add support in Surf for processing these types of dependency to be included in the layer
       * TODO: It would be nice if we could use "<RES>" and have Surf understand that this meant to use the resource mapping. 
       */
      shareDependencies: [Alfresco.constants.URL_RESCONTEXT + "js/log4javascript.v1.4.1.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/yahoo/yahoo-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/event/event-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/dom/dom-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/dragdrop/dragdrop-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/animation/animation-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/logger/logger-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/connection/connection-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/element/element-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/get/get-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/yuiloader/yuiloader-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/button/button-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/container/container-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/menu/menu-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/json/json-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/selector/selector-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/datasource/datasource-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/autocomplete/autocomplete-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/paginator/paginator-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/datatable/datatable-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/history/history-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/treeview/treeview-debug.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/cookie/cookie.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/uploader/uploader.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/calendar/calendar.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/resize/resize.js",
                          Alfresco.constants.URL_RESCONTEXT + "yui/yui-patch.js",
                          Alfresco.constants.URL_RESCONTEXT + "js/bubbling.v2.1.js",
                          Alfresco.constants.URL_RESCONTEXT + "js/flash/AC_OETags.js",
                          Alfresco.constants.URL_RESCONTEXT + "js/alfresco.js",
                          Alfresco.constants.URL_RESCONTEXT + "modules/editors/tiny_mce/tiny_mce.js",
                          Alfresco.constants.URL_RESCONTEXT + "modules/editors/tiny_mce.js",
                          Alfresco.constants.URL_RESCONTEXT + "modules/editors/yui_editor.js",
                          Alfresco.constants.URL_RESCONTEXT + "js/forms-runtime.js",
                          Alfresco.constants.URL_RESCONTEXT + "js/share.js",
                          Alfresco.constants.URL_RESCONTEXT + "js/lightbox.js"],
      
      constructor: function(args) {
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
         
         // Merge the Share dependencies into the beginning of the specific widget dependencies...
         this.dependencies = this.shareDependencies.concat(this.dependencies);
      }
   });
});