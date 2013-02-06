define(["alfresco/core/ProcessWidgets",
        "dojo/_base/declare",
        "dojo/dom-construct",
        "dojo/_base/array"], 
        function(ProcessWidgets, declare, domConstruct, array) {
   
   return declare([ProcessWidgets], {
      
      /**
       * The scope to use for i18n messages.
       * 
       * @property i18nScope {String}
       */
      i18nScope: "org.alfresco.js.VerticalWidgets"
   });
});