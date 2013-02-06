define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "alfresco/core/Core",
        "dojo/text!./templates/ProcessWidgets.html",
        "dojo/dom-construct",
        "dojo/_base/array"], 
        function(declare, _Widget, _Templated, AlfCore, template, domConstruct, array) {
   
   /**
    * TODO: It's possible that this widget is not really needed anymore - need to check.
    */
   return declare([_Widget, _Templated, AlfCore], {
      
      /**
       * The HTML template to use for the widget.
       * @property template {String}
       */
      templateString: template,
      
      /**
       * @property {object} config
       * @default null
       */
      config: null,
      
      /**
       * @property {string} configUrl
       * @default ""
       */
      configUrl: "",
      
      /**
       * @property {string} baseClass
       * @default "widgets"
       */
      baseClass: "widgets",
      
      /**
       * @method postCreate
       */
      postCreate: function alfresco_core_ProcessWidgets__postCreate() {
         if (this.widgets)
         {
            this.processWidgets(this.widgets, this.containerNode);
         }
      }
   });
});