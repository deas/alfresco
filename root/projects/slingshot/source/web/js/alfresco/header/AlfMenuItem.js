define(["dojo/_base/declare",
        "alfresco/menus/AlfMenuItem"], 
        function(declare, AlfMenuItem) {
   
   return declare([AlfMenuItem], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/AlfMenuItem.css"}]
   });
});