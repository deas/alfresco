define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dojo/_base/lang",
        "dojo/_base/array",
        "alfresco/core/Core"],
        function(declare, _WidgetBase, lang, array, AlfCore) {
   
   return declare([_WidgetBase, AlfCore], {
      
      // This is the global namespaced object for the widget libary (e.g. "YAHOO", or "$")
      globalObj: null,
      
      // This is the list of dependencies for to be loaded...
      dependencies: null,
      
      // This is the list of dependencies that have already been loaded. Notice that we
      // are INTENTIONALLY creating a static object by initialising it. This means that any
      // inheriting class will update this array...
      //
      // TODO: Check this is inherited !!!
      dependenciesAlreadyLoaded: [],
      
      // This is a extension point for the extending widget to create the callback function 
      // that actually creates the widget
      createWidget: null,
      
      postCreate: function() {
         this.inherited(arguments);

         array.forEach(this.globalObj, function(entry, i) {
            // Check to see whether or not the global object exists. This is required to prevent
            // imported dependencies being scoped within the require callback. For example we need
            // to make sure that "YAHOO" is a global object for YUI widgets to work.
            if (!dojo.global[this.globalObj])
            {
               dojo.global[this.globalObj] = {};
            }
         });
         
         // Remove any dependencies already loaded...
         this.dependencies = array.filter(this.dependencies, function(item) {
            return array.indexOf(this.dependenciesAlreadyLoaded, item) == -1;
         }, this);
         
         // Load the required dependencies...
         this._loadDependencies(this.dependencies, this.createWidget);
      },
      
      // This will load all the specified dependencies synchronously - this simulates the loading of 
      // JavaScript onto an HTML page. This is required because a smaller dependency file may finish
      // loading before a larger one and get evaluated first... order is important!!
      //
      // We use recursion to work through the supplied dependencies and once they have all been loaded
      // we then use the call back function which should instantiate the widgets required...
      _loadDependencies: function(dependencies, callback) {
         
         
         // Load dependencies synchronously...
         var _this = this;
         if (dependencies.length > 0)
         {
            require([dependencies[0]], function() {
               dependencies.shift();  // Remove the dependency we've just loaded
               _this._loadDependencies(dependencies, callback); // ...and recurse
            });
         }
         else
         {
            // Once all the dependencies are loaded use the callback function which should instantiate the
            // required widgets
            callback(this);
         }
      }
   });
});