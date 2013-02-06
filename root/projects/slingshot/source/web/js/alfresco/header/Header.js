define(["dojo/_base/declare",
        "alfresco/layout/LeftAndRight",
        "dojo/dom-construct",
        "dojo/_base/array"], 
        function(declare, LeftAndRight, domConstruct, array) {
   
   /**
    * This extends the alfresco/layout/LeftAndRight image to header specific CSS selectors.
    */
   return declare([LeftAndRight], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/Header.css"}]
   });
});