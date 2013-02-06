define(["alfresco/forms/controls/MultipleEntryFormControl",
        "dojo/_base/declare",
        "alfresco/forms/creation/WidgetCreateCreator",
        "dojo/_base/array",
        "dojo/_base/lang"], 
        function(MultipleEntryFormControl, declare, WidgetCreateCreator, array, lang) {
   
   return declare([MultipleEntryFormControl], {
      
      createFormControl: function(config, domNode) {
         return new WidgetCreateCreator(config);
      },
      
      getValue: function(meaningful) {
         var _this = this;
         var value = this.inherited(arguments);
         if (meaningful)
         {
            value = lang.clone(value);
            if (value && value instanceof Array)
            {
               array.forEach(value, function(entry, index) {
                  // Convert the default config value (which will be an array of objects
                  // where each object represents a key/value pair into a first class object
                  // of key/value pairs.
                  if (entry.config && entry.config instanceof Array)
                  {
                     var updatedconfig = {};
                     array.forEach(entry.config, function(co, i) {
                        if (co.value && co.value._key)
                        {
                           updatedconfig[co.value._key] = co.value._value;
                        }
                     });
                     entry.config = updatedconfig;
                  }
                  
                  if (entry.additionalConfig && entry.additionalConfig instanceof Array)
                  {
                     // Convert the additionalConfig object into properties of the current entry.
                     // The additionalConfig object represents widget configuration that is used
                     // when instantiating the widget but is not used as arguments when instantiating
                     // the object directly
                     array.forEach(entry.additionalConfig, function(co, i) {
                        if (co.value && co.value._key && 
                            (co.value._key != "widgets" || co.value._key != "name" || co.value._key != "id" || co.value._key != "config" || co.value._key != "classes"))
                        {
                           entry[co.value._key] = co.value._value;
                        }
                     });
                     delete entry.additionalConfig;
                  }
                  
                  // Merge any widgets defined into the config object...
                  if (entry.widgets)
                  {
                     var tmp = { widgets: entry.widgets };
                     lang.mixin(entry.config, tmp);
                     delete entry.widgets;
                  }
                  
                  if (entry._alfMultipleElementId)
                  {
                     delete entry._alfMultipleElementId;
                  }
               });
            }
         }
         return value;
      }
   });
});