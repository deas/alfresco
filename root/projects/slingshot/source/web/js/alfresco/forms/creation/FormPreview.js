define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/FormPreview.html",
        "alfresco/core/Core",
        "alfresco/forms/PublishForm"], 
        function(declare, _Widget, _Templated, template, AlfCore, PublishForm) {
   
   return declare([_Widget, _Templated, AlfCore], {
      cssRequirements: [{cssFile:"./css/FormPreview.css"}],
      i18nRequirements: [{i18nFile: "./i18n/FormPreview.properties"}],
      templateString: template,
      
      postCreate: function() {
         this.labelNode.innerHTML = this.message("form.preview.title");
         var previewForm = new PublishForm({widgets: this.widgets});
         previewForm.placeAt(this.previewNode);
      },
      
      closePreview: function() {
         // This is provided for the calling widget to add an aspect to.
      }
   });
});