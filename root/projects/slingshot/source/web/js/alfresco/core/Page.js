define(["alfresco/core/ProcessWidgets",
        "dojo/_base/declare",
        "dojo/dom-construct",
        "dojo/_base/array"], 
        function(ProcessWidgets, declare, domConstruct, array) {
   
   return declare([ProcessWidgets], {
      postCreate: function() {
         
         if (this.services)
         {
            this.processServices(this.services);
         }
         
         if (this.widgets)
         {
            this.processWidgets(this.widgets, this.containerNode);
         }
         
         this.alfLog("log", "Page widgets and service processed", {});
         this.alfPublish("PageReady", {});
      }
   });
});