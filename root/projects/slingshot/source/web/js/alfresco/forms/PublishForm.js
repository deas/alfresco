define(["dojo/_base/declare",
        "alfresco/forms/Form"], 
        function(declare, Form) {
   
   return declare([Form], {
      
      /**
       * This function overrides the default implementation so that instead of performing an XHR
       * POST operation it simply publishes a topic indicating that the "OK" button has been clicked.
       */
      _onOK: function() {
         
         
         
      },
   
      /**
       * Overridden to hide the buttons.
       */
      createButtons: function() {
         // TODO: This isn't really accurate for a PublishForm. This should arguably done in a different class
      }
   });
});