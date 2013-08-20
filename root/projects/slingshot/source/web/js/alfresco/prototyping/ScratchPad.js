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
 * This provides a simple editing capability for creating and previewing JSON page models using JSON
 * and then saving them to the repository so that they can be reloaded.
 * 
 * PLEASE NOTE:
 * In order for the scratchpad to work you will need to download the source of https://github.com/josdejong/jsoneditor
 * and place it into the js/lib/jsoneditor directory, you will also need to update the package information in
 * surf.xml (e.g. <package name="jsoneditorlib" location="js/lib/jsoneditorlib"/>)
 * 
 * ADDITIONAL NOTE: There is a bug in Surf that replaces all matches of a package alias in a dependency path
 * 
 * @module alfresco/prototyping/ScratchPad
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/services/_PreviewServiceTopicMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "alfresco/services/_PageServiceTopicMixin",
        "dojo/text!./templates/ScratchPad.html",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "dijit/form/Textarea",
        "alfresco/forms/controls/DojoValidationTextBox",
        "alfresco/buttons/AlfButton",
        "dojo/json",
        "dojo/dom-construct",
        "dojo/request/xhr",
        "dojo/_base/lang",
        "dojo/query",
        "dojo/NodeList-manipulate"], 
        function(declare, _Widget, _Templated, _PageServiceTopicMixin, template, AlfCore, CoreXhr, Textarea, DojoValidationTextBox, AlfButton, JSON, domConstruct, xhr, lang, query) {
   
   return declare([_Widget, _Templated, AlfCore, CoreXhr, _PageServiceTopicMixin], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/ScratchPad.css"},
                        {cssFile:"/js/lib/jsoneditorlib/jsoneditor-min.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/ScratchPad.properties"}],

      /**
       * The HTML template to use for the widget.
       * @instance
       * @type template {String}
       */
      templateString: template,
      
      /**
       * @instance
       */
      editor: null,
      
      /**
       * @instance
       */
      generateButton: null,
      
      /**
       * @instance
       */
      postCreate: function alfresco_prototyping_ScratchPad__postCreate() {

         // NOT SURE WHY THIS REQUIRE STATEMENT IS NEEDED (rather than the define working I mean)
         var _this = this;
         var container = this.editorNode;
         
         // The ACE module sneakily overwrites the "require" variable which will prevent subsequent
         // calls to the Dojo require from working, therefore it's necessary to keep a reference to
         // the original function and then undo the damage once ACE has been loaded...
         // Although we could have included these in the modules "define" statement and have them
         // preloaded into the Dojo cache would break the page as soon as the ACE module is processed :(
         var oldRequire = require;
         require(["jsoneditorlib/jsoneditor",
                  "jsoneditorlib/lib/ace/ace",
                  "jsoneditorlib/lib/jsonlint/jsonlint"], function (jsoneditor) {
            _this.editor = new jsoneditor.JSONEditor(container, {mode:'code'});
            require = oldRequire;
         });
         
         // Create a button for previewing...
         var _this = this;
         this.generateButton = new AlfButton({
            label: "Preview",
            onClick: function(){
               _this.generatePreview();
            }
         });
         this.generateButton.placeAt(this.generateButtonNode);

         // Create a field for adding a name...
         this.nameField = new DojoValidationTextBox({
            label: "Page Name",
            value: "",
            description: "Enter a name for the page"
         });
         this.nameField.placeAt(this.buttonsNode);
         
         this.saveButton = new AlfButton({
            label: "Save",
            onClick: function(){
               _this.save();
            }
         });
         this.saveButton.placeAt(this.buttonsNode);
      },
      
      /**
       * @instance
       */
      save: function alfresco_prototyping_ScratchPad__save() {
         var payload = {
            pageName: this.nameField.getValue(),
            pageDefinition: this.editor.get()
         };
         this.alfPublish(this.createPageTopic, payload);
      },
      
      /**
       * @instance
       */
      generatePreview: function alfresco_prototyping_ScratchPad__generatePreview() {
         
         // Clear out any previous preview...
         domConstruct.empty(this.previewNode);
         
         // Get the editor content, transform it into JSON and process it...
         try
         {
            var query = {
               widgets: this.editor.getText()
            };
            var data = {
               jsonContent: this.editor.get()
            };
            this.serviceXhr({
               url : Alfresco.constants.URL_SERVICECONTEXT + "surf/dojo/xhr/dependencies",
               query: query,
               data: data,
               method: "GET",
               successCallback: this.updatePage,
               failureCallback: this.onDependencyFailure,
               callbackScope: this
            });
         }
         catch(e)
         {
            this.alfLog("log", "An error occurred parsing the JSON", e);
         }
      },
      
      /**
       * @instance
       */
      updatePage: function alfresco_prototyping_ScratchPad__updatePage(response, originalRequestConfig) {
         // Iterate over the CSS map and append a new <link> element into the <head> element to ensure that all the
         // widgets CSS dependencies are loaded... 
         for (var media in response.cssMap)
         {
            // TODO: query for the node outside of the loop
            // TODO: keep a reference to each node appended and then remove it when the preview is regenerated
            query("head").append('<link rel="stylesheet" type="text/css" href="' + appContext + response.cssMap[media] + '" media="' + media + '">');
         }
         
         // Build in the i18n properties into the global object...
         for (var scope in response.i18nMap)
         {
            if (typeof window[response.i18nGlobalObject].messages.scope[scope] == "undefined")
            {
               // If the scope hasn't already been used then we can just assign it directly...
               window[response.i18nGlobalObject].messages.scope[scope] = response.i18nMap[scope];
            }
            else
            {
               // ...but if the scope already exists, then we need to mixin the new properties...
               lang.mixin(window[response.i18nGlobalObject].messages.scope[scope], response.i18nMap[scope]);
            }
         }
         
         // The data response will contain a MD5 referencing JavaScript resource that we should request that Dojo loads...
         var requires = [Alfresco.constants.URL_RESCONTEXT + response.javaScript];
         require(requires, lang.hitch(this, "processWidgets", originalRequestConfig.data.jsonContent.widgets, this.previewNode));
         
      },

      /**
       * @instance
       */
      onDependencyFailure: function alfresco_prototyping_ScratchPad__onDependencyFailure(response, originalRequestConfig) {
         this.alfLog("error", "An error occurred requesting the XHR dependencies", response, originalRequestConfig);
      }
   });
});