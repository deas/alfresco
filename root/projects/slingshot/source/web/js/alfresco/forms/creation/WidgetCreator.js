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
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/WidgetCreator.html",
        "alfresco/core/Core",
        "alfresco/forms/Form",
        "dijit/form/Button",
        "dojo/json",
        "dojo/dom-construct",
        "dojox/dtl/filter/htmlstrings",
        "dojo/request/xhr",
        "dojo/_base/lang",
        "dojo/query",
        "dojo/NodeList-manipulate"], 
        function(declare, _Widget, _Templated, template, AlfCore, Form, Button, JSON, domConstruct, htmlStrings, xhr, lang, query) {
   
   return declare([_Widget, _Templated, AlfCore], {
      cssRequirements: [{cssFile:"./css/WidgetCreator.css"}],
      i18nRequirements: [{i18nFile: "./i18n/WidgetCreator.properties"}],
      templateString: template,
      
      /**
       * 
       */
      form: null,
      
      /**
       * 
       */
      previewButton: null,
      
      /**
       * 
       */
      saveButton: null,
      
      /**
       * 
       */
      postCreate: function() {

         // Create a new form containing the controls required to create a page...
         if (this.form == null)
         {
            this.form = new Form({
               widgets: this.widgets});
            this.form.placeAt(this.editorNode);
         }
         
         // Create a button for previewing...
         var _this = this;
         this.previewButton = new Button({
            label: "Preview",
            onClick: function(){
               _this.generatePreview();
            }
         });
         this.previewButton.placeAt(this.buttonsNode);
         this.saveButton = new Button({
            label: "Save",
            onClick: function(){
               _this.save();
            }
         });
         this.saveButton.placeAt(this.buttonsNode);
      },
      
      /**
       * 
       */
      generatePreview: function() {
         
         // Clear out any previous preview...
         domConstruct.empty(this.previewNode);
         
         var _this = this;
         var formContent = this.form.getValue(true),
             services = formContent.services,
             widgets = formContent.widgets;
         var jsonContent = { widgets: widgets,
                             services: services };
         
         
         try
         {
            // The content in the editor MUST be valid JSON (e.g. with strings for keys, not a JavaScript object). If it is
            // not then the JSON parser will fail...
            var jsonStr = JSON.stringify(jsonContent, true);
            
            // Use the XHR dependency WebScript to get the dependencies required for the requested Widgets...
            // We need to capture both the JavaScript, CSS and i18n dependencies.
            xhr(appContext + "/service/surf/dojo/xhr/dependencies", {
               handleAs: "json",
               query: {
                  widgets: jsonStr
               }
            }).then(function(data) {
               
               // Iterate over the CSS map and append a new <link> element into the <head> element to ensure that all the
               // widgets CSS dependencies are loaded... 
               for (var media in data.cssMap)
               {
                  // TODO: query for the node outside of the loop
                  // TODO: keep a reference to each node appended and then remove it when the preview is regenerated
                  query("head").append('<link rel="stylesheet" type="text/css" href="' + appContext + data.cssMap[media] + '" media="' + media + '">');
               }
               
               // Build in the i18n properties into the global object...
               for (var scope in data.i18nMap)
               {
                  if (typeof window[data.i18nGlobalObject].messages.scope[scope] == "undefined")
                  {
                     // If the scope hasn't already been used then we can just assign it directly...
                     window[data.i18nGlobalObject].messages.scope[scope] = data.i18nMap[scope];
                  }
                  else
                  {
                     // ...but if the scope already exists, then we need to mixin the new properties...
                     lang.mixin(window[data.i18nGlobalObject].messages.scope[scope], data.i18nMap[scope]);
                  }
               }
               
               // The data response will contain a MD5 referencing JavaScript resource that we should request that Dojo loads...
               var requires = [data.javaScript];
               require(requires, function() {
                  // Once the require request has completed we can process the requested widgets knowing that all of the
                  // dependent JavaScript files have been loaded...
                  _this.processWidgets(jsonContent.widgets, _this.previewNode);
               });
            }, function(error) {
               // Handle any errors...
               // TODO: We should output something sensible into the preview pane...
               _this.alfLog("error", "An error occurred requesting the XHR dependencies");
            });
         }
         catch(e)
         {
            this.alfLog("log", "An error occurred parsing the JSON", e);
         }
      },
      
      /**
       * 
       */
      save: function() {
         var _this = this;
         try
         {
            // The content in the editor MUST be valid JSON (e.g. with strings for keys, not a JavaScript object). If it is
            // not then the JSON parser will fail...
            var formContent = this.form.getValue(true),
                pageName = formContent.name,
                services = formContent.services,
                widgets = formContent.widgets;
            
            // Create the payload describing the page...
            var jsonContent = {
               services: services,
               widgets: widgets
            };
            // ...and then convert it into a String that can be passed as a request parameter and parsed by the receiving 
            // WebScript on the repository...
            var jsonStr = JSON.stringify(formContent, true);
            
            xhr(appContext + "/proxy/alfresco/share/page-definition", {
               handleAs: "json",
               method: "POST",
               query: {
                  name: pageName,
                  json: jsonStr
               }
            }).then(function(data){
               // Do something with the handled data
               _this.alfLog("log", "Successfully created page");
               _this.alfPublish("GetPages", {});
            }, function(err){
              // Handle the error condition
               _this.alfLog("log", "Failed to create page");
               _this.alfPublish("GetPages", {});
            });
         }
         catch(e)
         {
            this.alfLog("log", "An error occurred parsing the JSON", e);
         }
      },
      
      widgets: [
         {
            id: "PageName",
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "name",
               label: "page.creator.pageName.label",
               description: "page.creator.pageName.description",
               requirementConfig: {
                  initialValue: true
               }
            }
         },
         {
            id: "Services",
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "services",
               label: "page.creator.services.label",
               description: "page.creator.services.description",
               optionsConfig: {
                  makeXhr: Alfresco.constants.URL_SERVICECONTEXT + "share/service/options"
               }
            }
         },
         {
            id: "Widgets",
            name: "alfresco/forms/creation/WidgetCreateControl",
            config: {
               name: "widgets",
               label: "page.creator.widgets.label",
               description: "page.creator.widgets.description"
            }
         }
      ]
   });
});