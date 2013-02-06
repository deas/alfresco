define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/PopupSearch.html",
        "alfresco/core/Core",
        "dijit/form/Textarea"], 
        function(declare, _WidgetBase, _TemplatedMixin, template,  AlfCore, Textarea) {
   
   return declare([_WidgetBase, _TemplatedMixin, AlfCore], {
      
      i18nScope: "org.alfresco.PopupSearch",
      cssRequirements: [{cssFile:"./css/PopupSearch.css"}],
      i18nRequirements: [{i18nFile: "./i18n/PopupSearch.properties"}],
      templateString: template,

      textArea: null,
     
      postCreate: function() {
         
         this.labelNode.innerHTML = this.message("search.label");
         
         this.textArea = new Textarea({
            value: this.message("search.instruction"),
            style: "width:200px;"
         });
         this.textArea.placeAt(this.textAreaNode);
      }
   });
});