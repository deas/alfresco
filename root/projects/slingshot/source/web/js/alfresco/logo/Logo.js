define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/Logo.html",
        "alfresco/core/Core",
        "dojo/dom-construct",
        "dojo/dom-style"], 
        function(declare, _Widget, _Templated, template, Core, domConstruct, domStyle) {
   
   return declare([_Widget, _Templated, Core], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/Logo.css",mediaType:"screen"}],

      /**
       * 
       * @property {string} logoClasses The CSS class or classes to use to generate the logo
       * @default "alfresco-logo-large"
       */
      logoClasses: "alfresco-logo-large",
      
      /**
       * @property {string} 
       */
      logoSrc: null,

      /**
       * 
       * @property {string} cssNodeStyle
       * @default "display: none;"
       */
      cssNodeStyle: "display: none;",
      
      /**
       * 
       * @property {string} imgNodeStyle
       * @default "display: none;"
       */
      imgNodeStyle: "display: none;",
         
      /**
       * The HTML template to use for the widget.
       * @property template {String}
       */
      templateString: template,
      
      /**
       * This controls whether or not the image is rendered with the img element or the div in the template.
       * The default it to use the div because it is controlled via CSS which allows for finer control over the
       * dimensions of the displayed logo. When using the img element the dimensions will be those of the supplied
       * image. 
       * 
       * @method buildRendering
       */
      buildRendering: function alfresco_logo_Logo__buildRendering() {
         if (this.logoSrc)
         {
            this.imgNodeStyle = "display: block;";
         }
         else
         {
            this.cssNodeStyle = "display: block";
         }
         this.inherited(arguments);
      }
   });
});