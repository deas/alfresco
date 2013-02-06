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