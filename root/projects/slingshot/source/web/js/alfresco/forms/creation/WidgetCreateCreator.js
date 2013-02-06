define(["alfresco/forms/controls/MultipleEntryCreator",
        "alfresco/forms/creation/WidgetCreateCreatorElement",
        "dojo/_base/declare",
        "dojo/_base/array",
        "dijit/registry",
        "dojo/dom-construct"], 
        function(MultipleEntryCreator, PageCreateCreatorElement, declare, array, registry, domConstruct) {
   
   return declare([MultipleEntryCreator], {
      
      /**
       * Overrides the default Drag-And-Drop type to prevent other objects being dropped into the 
       * creator (for example, we don't want the options or the rules or anything else to be dropped
       * in).
       */
      getDNDType: function() {
         return "FormCreationField";
      },
      
      /**
       * Override the default avatar node construction so that we use the field attribute from the value
       * to indicate what is being dragged.
       */
      createDNDAvatarNode: function(widget) {
         return domConstruct.create("div", { innerHTML: (widget && widget.value && widget.value.field) ? widget.value.field : ""});
      },
      
      createElementWidget: function(elementConfig) {
         var widget = new PageCreateCreatorElement({elementConfig: elementConfig});
         return widget;
      }
   });
});