define(["dojo/_base/declare"], 
        function(declare) {
   return declare(null, {
      
      callbackMonkey: function(nameOfChangedProperty, oldValue, newValue, callingObject, attribute) {
         return newValue == "required";
      },
      
      callbackOptions: function(nameOfChangedProperty, oldValue, newValue, callingObject) {
         callingObject.setOptions([
            { label: "Dynamic1_" + newValue, value: "Dynamic1"},
            { label: "Dynamic2_" + newValue, value: "Dynamic2"}
         ]);
      },
      
      overSixty: function(nameOfChangedProperty, oldValue, newValue, callingObject) {
         return (newValue >= 60);
      },
      
      overEighteen: function(nameOfChangedProperty, oldValue, newValue, callingObject) {
         return (newValue >= 18);
      },
      
      ambitions: function(nameOfChangedProperty, oldValue, newValue, callingObject) {
         var options = [];
         if (newValue==="u")
         {
            options = [
               { label: "I'd like to get a job", value: "js"},
               { label: "I'm happy unemployed", value: "hu"}
            ];
         }
         else if (newValue==="pt")
         {
            options = [
              { label: "I'd like to quit", value: "q"},
              { label: "I'm happy as I am", value: "h"},
              { label: "I'd like to go full time", value: "ft"}
           ];
         }
         else if (newValue==="ft")
         {
            options = [
              { label: "I'd like to quit", value: "q"},
              { label: "I'd like to go part time", value: "pt"},
              { label: "I'm happy as I am", value: "h"}
           ];
         }
         callingObject.setOptions(options);
      }
      
   });
});