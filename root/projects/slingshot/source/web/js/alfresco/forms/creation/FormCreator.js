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
        "dojo/text!./templates/FormCreator.html",
        "alfresco/core/Core",
        "alfresco/forms/creation/FormCreateControl",
        "dijit/form/Button",
        "dojo/json",
        "dojo/dom-construct",
        "dojox/dtl/filter/htmlstrings"], 
        function(declare, _Widget, _Templated, template, AlfCore, FormCreateControl, Button, JSON, domConstruct, htmlStrings) {
   
   return declare([_Widget, _Templated, AlfCore], {
      cssRequirements: [{cssFile:"./css/FormCreator.css"}],
      i18nRequirements: [{i18nFile: "./i18n/FormCreator.properties"}],
      templateString: template,
      editor: null,
      generateButton: null,
      postCreate: function() {
         
         // Create an editor for adding the JSON code...
         this.editor = new FormCreateControl({name: "FormCreate",
                                              label: "FormCreate"});
         this.editor.placeAt(this.editorNode);
        
         // Create a button for previewing...
         var _this = this;
         this.generateButton = new Button({
            label: "Preview",
            onClick: function(){
               _this.generatePreview();
            }
         });
         this.generateButton.placeAt(this.generateButtonNode);
      },
      
      generatePreview: function() {
         
         // Clear out any previous preview...
         domConstruct.empty(this.previewNode);
         
         var value = this.editor.getValue();
         this.processWidgets(value, this.previewNode);
      }
   });
});