define(["dojo/_base/declare",
        "alfresco/forms/controls/MultipleEntryCreator", 
        "alfresco/forms/controls/MultipleEntryElementWrapper",
        "alfresco/forms/controls/MultipleKeyValuePairElement"], 
        function(declare, MultipleEntryCreator, MultipleEntryElementWrapper, MultipleKeyValuePairElement) {
   
   return declare([MultipleEntryCreator], {

      /**
       * Indicates whether or not re-ordering should be enabled through the use of drag and drop
       */
      enableDND: false,
      
      /**
       * This function should be extended by concrete implementations to create the element to go in the
       * element wrapper.
       */
      createElementWidget: function(elementConfig) {
         // By default we're just going to create the "Abstract" instance
         return new MultipleKeyValuePairElement({elementConfig: elementConfig});
      }
   });
});