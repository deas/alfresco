define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/Title.html",
        "alfresco/core/Core"], 
        function(declare, _WidgetBase, _TemplatedMixin, template,  AlfCore) {
   
   return declare([_WidgetBase, _TemplatedMixin, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/Title.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @property template {String}
       */
      templateString: template,
      
      /**
       * @property {string} title The title to be displayed. This should be a localized value.
       */
      title: null,
      
      /**
       * @method postCreate
       */
      postCreate: function alfresco_header_Title__postCreate() {
         this.textNode.innerHTML = this.title != null ? this.title : "";
      }
   });
});