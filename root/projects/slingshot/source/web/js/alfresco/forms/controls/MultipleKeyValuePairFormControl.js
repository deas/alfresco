define(["alfresco/forms/controls/MultipleEntryFormControl",
        "dojo/_base/declare",
        "alfresco/forms/controls/MultipleKeyValuePairCreator",
        "dojo/_base/array"], 
        function(MultipleEntryFormControl, declare, MultipleKeyValuePairCreator, array) {
   
   return declare([MultipleEntryFormControl], {
      
      createFormControl: function(config, domNode) {
         return new MultipleKeyValuePairCreator(config);
      }
   })
});